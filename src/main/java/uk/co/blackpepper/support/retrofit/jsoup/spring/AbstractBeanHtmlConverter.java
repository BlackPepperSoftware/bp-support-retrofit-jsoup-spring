package uk.co.blackpepper.support.retrofit.jsoup.spring;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.format.support.DefaultFormattingConversionService;

import com.google.common.base.Function;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;
import uk.co.blackpepper.support.retrofit.jsoup.TypedElement;

public abstract class AbstractBeanHtmlConverter<T extends TypedOutput> implements Converter {
	
	private final Map<String, Function<? super Element, String>> fromBodyFunctionsByPropertyName;

	private final Map<String, String> toBodyNamesByPropertyName;

	private ConversionService conversionService;
	
	public AbstractBeanHtmlConverter() {
		fromBodyFunctionsByPropertyName = new HashMap<>();
		toBodyNamesByPropertyName = new HashMap<>();
		conversionService = new DefaultFormattingConversionService();
	}
	
	protected final ConversionService getConversionService() {
		return conversionService;
	}
	
	protected final void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/**
	 * @deprecated Use {@link #setProperty(String, Function, String)} with {@link FromBody#textById(String)} instead.
	 */
	@Deprecated
	protected final void setPropertyId(String propertyName, String id) {
		setProperty(propertyName, FromBody.textById(id), id);
	}
	
	/**
	 * @deprecated Use {@link #setProperty(String, Function, String)} with
	 *             {@link FromBody#attributeById(String, String)} instead.
	 */
	@Deprecated
	protected final void setPropertyIdAndAttribute(String propertyName, String id, String attributeName) {
		setProperty(propertyName, FromBody.attributeById(id, attributeName), id);
	}
	
	/**
	 * @deprecated Use {@link #setProperty(String, Function, String)} with {@link FromBody#textByClass(String)} instead.
	 */
	@Deprecated
	protected final void setPropertyClass(String propertyName, String className) {
		setProperty(propertyName, FromBody.textByClass(className), className);
	}
	
	/**
	 * @deprecated Use {@link #setProperty(String, Function, String)} with
	 *             {@link FromBody#attributeByClass(String, String)} instead.
	 */
	@Deprecated
	protected final void setPropertyClassAndAttribute(String propertyName, String className, String attributeName) {
		setProperty(propertyName, FromBody.attributeByClass(className, attributeName), className);
	}
	
	protected final void setProperty(String propertyName, Function<? super Element, String> fromBodyFunction,
		String toBodyName) {
		fromBodyFunctionsByPropertyName.put(propertyName, fromBodyFunction);
		toBodyNamesByPropertyName.put(propertyName, toBodyName);
	}

	@Override
	public Object fromBody(TypedInput body, Type type) throws ConversionException {
		TypedElement element = TypedElement.parse(body);
		Object bean = createBean(type);
		setProperties(bean, element.getElement());
		return bean;
	}
	
	@Override
	public TypedOutput toBody(Object bean) {
		return toBody(bean, newTypedOutput());
	}

	protected TypedOutput toBody(Object bean, T output) {
		for (String propertyName : toBodyNamesByPropertyName.keySet()) {
			String toBodyName = toBodyNamesByPropertyName.get(propertyName);
			String text = getAsText(bean, propertyName);
			
			if (text != null) {
				addProperty(output, toBodyName, text);
			}
		}
		
		return output;
	}
	
	protected abstract T newTypedOutput();

	protected abstract void addProperty(T output, String id, String text);

	/**
	 * Instantiates the bean used when converting from HTTP body.
	 * <p>
	 * This method is called by {@link #fromBody(TypedInput, Type)}. The default implementation attempts to instantiate
	 * the bean using its default constructor. Override this method to create the bean using a different constructor.
	 * 
	 * @throws ConversionException
	 *             if the bean cannot be created
	 */
	protected Object createBean(Type type) throws ConversionException {
		Class<?> beanClass = (Class<?>) type;
		return BeanUtils.instantiateClass(beanClass);
	}
	
	private void setProperties(Object bean, Element element) throws ConversionException {
		for (String propertyName : fromBodyFunctionsByPropertyName.keySet()) {
			Function<? super Element, String> fromBodyFunction = fromBodyFunctionsByPropertyName.get(propertyName);
			String text = fromBodyFunction.apply(element);
			
			if (text != null) {
				setAsText(bean, propertyName, text);
			}
		}
	}
	
	private String getAsText(Object bean, String propertyName) {
		BeanWrapper beanWrapper = wrap(bean);
		Object value = beanWrapper.getPropertyValue(propertyName);
		TypeDescriptor typeDescriptor = beanWrapper.getPropertyTypeDescriptor(propertyName);
		return (String) conversionService.convert(value, typeDescriptor, TypeDescriptor.valueOf(String.class));
	}

	private void setAsText(Object bean, String propertyName, String text) throws ConversionException {
		BeanWrapper beanWrapper = wrap(bean);
		TypeDescriptor typeDescriptor = beanWrapper.getPropertyTypeDescriptor(propertyName);
		
		Object value;
		try {
			value = conversionService.convert(text, TypeDescriptor.valueOf(String.class), typeDescriptor);
		}
		catch (TypeMismatchException exception) {
			String message = String.format("Error converting from '%s' to type '%s' for property '%s'", text,
				typeDescriptor, propertyName);
			throw new ConversionException(message, exception);
		}
		
		try {
			beanWrapper.setPropertyValue(propertyName, value);
		}
		catch (BeansException exception) {
			String message = String.format("Error setting bean property '%s' to: %s", propertyName, value);
			throw new ConversionException(message, exception);
		}
	}
	
	private BeanWrapper wrap(Object bean) {
		BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
		beanWrapper.setConversionService(conversionService);
		return beanWrapper;
	}
}
