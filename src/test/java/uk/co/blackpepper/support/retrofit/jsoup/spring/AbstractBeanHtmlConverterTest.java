package uk.co.blackpepper.support.retrofit.jsoup.spring;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.Formatter;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.support.DefaultFormattingConversionService;

import com.google.common.base.Function;
import com.google.common.base.Functions;

import retrofit.converter.ConversionException;
import retrofit.mime.FormUrlEncodedTypedOutput;
import retrofit.mime.TypedOutput;
import uk.co.blackpepper.support.retrofit.jsoup.TypedElement;
import uk.co.blackpepper.support.retrofit.jsoup.spring.FakeBean.FakeEnum;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import static uk.co.blackpepper.support.date.Dates.newDate;
import static uk.co.blackpepper.support.retrofit.jsoup.spring.TypedOutputMatcher.typedOutputEqualTo;

public class AbstractBeanHtmlConverterTest {

	private AbstractBeanHtmlConverter<FormUrlEncodedTypedOutput> converter;
	
	private Document document;
	
	@Before
	public void setUp() {
		converter = new FakeBeanHtmlConverter();
		document = Document.createShell("");
	}
	
	@Test
	public void fromBodyWithIdSetsStringProperty() throws ConversionException {
		converter.setPropertyId("stringProperty", "x");
		document.appendChild(newElementWithIdAndText(document, "x", "y"));
		
		FakeBean actual = (FakeBean) converter.fromBody(new TypedElement(document), FakeBean.class);
		
		assertThat(actual.getStringProperty(), is("y"));
	}

	@Test
	public void fromBodyWithIdAndAttributeSetsStringProperty() throws ConversionException {
		converter.setPropertyIdAndAttribute("stringProperty", "x", "y");
		document.appendChild(newElementWithIdAndAttribute(document, "x", "y", "z"));
		
		FakeBean actual = (FakeBean) converter.fromBody(new TypedElement(document), FakeBean.class);
		
		assertThat(actual.getStringProperty(), is("z"));
	}

	@Test
	public void fromBodyWithClassSetsStringProperty() throws ConversionException {
		converter.setPropertyClass("stringProperty", "x");
		document.appendChild(newElementWithClassAndText(document, "x", "y"));
		
		FakeBean actual = (FakeBean) converter.fromBody(new TypedElement(document), FakeBean.class);
		
		assertThat(actual.getStringProperty(), is("y"));
	}

	@Test
	public void fromBodyWithClassAndAttributeSetsStringProperty() throws ConversionException {
		converter.setPropertyClassAndAttribute("stringProperty", "x", "y");
		document.appendChild(newElementWithClassAndAttribute(document, "x", "y", "z"));
		
		FakeBean actual = (FakeBean) converter.fromBody(new TypedElement(document), FakeBean.class);
		
		assertThat(actual.getStringProperty(), is("z"));
	}

	@Test
	public void fromBodyWithFunctionSetsStringProperty() throws ConversionException {
		converter.setProperty("stringProperty", elementToText(), "y");
		document.appendChild(newElementWithText(document, "z"));
		
		FakeBean actual = (FakeBean) converter.fromBody(new TypedElement(document), FakeBean.class);
		
		assertThat(actual.getStringProperty(), is("z"));
	}
	
	@Test
	public void fromBodyWithIdSetsLongProperty() throws ConversionException {
		converter.setPropertyId("longProperty", "x");
		document.appendChild(newElementWithIdAndText(document, "x", "1"));
		
		FakeBean actual = (FakeBean) converter.fromBody(new TypedElement(document), FakeBean.class);
		
		assertThat(actual.getLongProperty(), is(1L));
	}

	@Test
	public void fromBodyWithIdSetsDateProperty() throws ConversionException {
		converter.setPropertyId("dateProperty", "x");
		converter.setConversionService(newConversionService(new DateFormatter("yyyy-MM-dd")));
		document.appendChild(newElementWithIdAndText(document, "x", "2000-01-01"));
		
		FakeBean actual = (FakeBean) converter.fromBody(new TypedElement(document), FakeBean.class);
		
		assertThat(actual.getDateProperty(), is(newDate(2000, 1, 1)));
	}

	@Test
	public void fromBodyWithIdSetsDatePatternProperty() throws ConversionException {
		converter.setPropertyId("datePatternProperty", "x");
		document.appendChild(newElementWithIdAndText(document, "x", "2000-01-01"));
		
		FakeBean actual = (FakeBean) converter.fromBody(new TypedElement(document), FakeBean.class);
		
		assertThat(actual.getDatePatternProperty(), is(newDate(2000, 1, 1)));
	}

	@Test
	public void fromBodyWithIdSetsDatePatternField() throws ConversionException {
		converter.setPropertyId("datePatternField", "x");
		document.appendChild(newElementWithIdAndText(document, "x", "2000-01-01"));
		
		FakeBean actual = (FakeBean) converter.fromBody(new TypedElement(document), FakeBean.class);
		
		assertThat(actual.getDatePatternField(), is(newDate(2000, 1, 1)));
	}

	@Test
	public void fromBodyWithIdSetsEnumProperty() throws ConversionException {
		converter.setPropertyId("enumProperty", "x");
		document.appendChild(newElementWithIdAndText(document, "x", "Y"));
		
		FakeBean actual = (FakeBean) converter.fromBody(new TypedElement(document), FakeBean.class);
		
		assertThat(actual.getEnumProperty(), is(FakeEnum.Y));
	}
	
	@Test
	public void fromBodyWhenMissingPreservesValue() throws ConversionException {
		converter.setPropertyId("stringProperty", "x");
		
		FakeBean actual = (FakeBean) converter.fromBody(new TypedElement(document), FakeBean.class);
		
		assertThat(actual.getStringProperty(), is(FakeBean.DEFAULT_STRING_PROPERTY_VALUE));
	}

	@Test
	public void toBodyWithIdSetsStringProperty() {
		converter.setPropertyId("stringProperty", "x");
		FakeBean bean = new FakeBean();
		bean.setStringProperty("y");
		
		TypedOutput actual = converter.toBody(bean);
		
		assertThat(actual, typedOutputEqualTo(newFormUrlEncodedTypedOutput("x", "y")));
	}

	@Test
	public void toBodyWithIdAndAttributeSetsStringProperty() {
		converter.setPropertyIdAndAttribute("stringProperty", "x", "y");
		FakeBean bean = new FakeBean();
		bean.setStringProperty("z");
		
		TypedOutput actual = converter.toBody(bean);
		
		assertThat(actual, typedOutputEqualTo(newFormUrlEncodedTypedOutput("x", "z")));
	}

	@Test
	public void toBodyWithFunctionSetsStringProperty() {
		converter.setProperty("stringProperty", newStringFunction(), "x");
		FakeBean bean = new FakeBean();
		bean.setStringProperty("y");
		
		TypedOutput actual = converter.toBody(bean);
		
		assertThat(actual, typedOutputEqualTo(newFormUrlEncodedTypedOutput("x", "y")));
	}

	@Test
	public void toBodyWithIdSetsLongProperty() {
		converter.setPropertyId("longProperty", "x");
		FakeBean bean = new FakeBean();
		bean.setLongProperty(1L);
		
		TypedOutput actual = converter.toBody(bean);
		
		assertThat(actual, typedOutputEqualTo(newFormUrlEncodedTypedOutput("x", "1")));
	}

	@Test
	public void toBodyWithIdSetsDateProperty() {
		converter.setPropertyId("dateProperty", "x");
		converter.setConversionService(newConversionService(new DateFormatter("yyyy-MM-dd")));
		FakeBean bean = new FakeBean();
		bean.setDateProperty(newDate(2000, 1, 1));
		
		TypedOutput actual = converter.toBody(bean);
		
		assertThat(actual, typedOutputEqualTo(newFormUrlEncodedTypedOutput("x", "2000-01-01")));
	}
	
	@Test
	public void toBodyWithIdSetsDatePatternProperty() {
		converter.setPropertyId("datePatternProperty", "x");
		FakeBean bean = new FakeBean();
		bean.setDatePatternProperty(newDate(2000, 1, 1));
		
		TypedOutput actual = converter.toBody(bean);
		
		assertThat(actual, typedOutputEqualTo(newFormUrlEncodedTypedOutput("x", "2000-01-01")));
	}
	
	@Test
	public void toBodyWithIdSetsDatePatternField() {
		converter.setPropertyId("datePatternField", "x");
		FakeBean bean = new FakeBean();
		bean.setDatePatternField(newDate(2000, 1, 1));
		
		TypedOutput actual = converter.toBody(bean);
		
		assertThat(actual, typedOutputEqualTo(newFormUrlEncodedTypedOutput("x", "2000-01-01")));
	}
	
	@Test
	public void toBodyWithIdSetsEnumProperty() {
		converter.setPropertyId("enumProperty", "x");
		FakeBean bean = new FakeBean();
		bean.setEnumProperty(FakeEnum.Y);
		
		TypedOutput actual = converter.toBody(bean);
		
		assertThat(actual, typedOutputEqualTo(newFormUrlEncodedTypedOutput("x", "Y")));
	}
	
	@Test
	public void toBodyIgnoresNullProperty() {
		converter.setPropertyId("stringProperty", "x");
		FakeBean bean = new FakeBean();
		bean.setStringProperty(null);
		
		TypedOutput actual = converter.toBody(bean);
		
		assertThat(actual, typedOutputEqualTo(new FormUrlEncodedTypedOutput()));
	}

	private static Element newElementWithText(Document document, String text) {
		return document.createElement("_element")
			.text(text);
	}
	
	private static Element newElementWithIdAndText(Document document, String id, String text) {
		return document.createElement("_element")
			.attr("id", id)
			.text(text);
	}
	
	private static Element newElementWithClassAndText(Document document, String className, String text) {
		return document.createElement("_element")
			.addClass(className)
			.text(text);
	}
	
	private static Element newElementWithIdAndAttribute(Document document, String id, String attributeName,
		String attributeValue) {
		return document.createElement("_element")
			.attr("id", id)
			.attr(attributeName, attributeValue);
	}
	
	private static Element newElementWithClassAndAttribute(Document document, String className, String attributeName,
		String attributeValue) {
		return document.createElement("_element")
			.addClass(className)
			.attr(attributeName, attributeValue);
	}

	private static Function<Object, String> newStringFunction() {
		return Functions.constant("_value");
	}
	
	private static Function<Element, String> elementToText() {
		return new Function<Element, String>() {
			@Override
			public String apply(Element element) {
				return element.text();
			}
		};
	}
	
	private static ConversionService newConversionService(Formatter<?> formatter) {
		DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
		conversionService.addFormatter(formatter);
		return conversionService;
	}

	private static FormUrlEncodedTypedOutput newFormUrlEncodedTypedOutput(String fieldName, String fieldValue) {
		FormUrlEncodedTypedOutput expected = new FormUrlEncodedTypedOutput();
		expected.addField(fieldName, fieldValue);
		return expected;
	}
}
