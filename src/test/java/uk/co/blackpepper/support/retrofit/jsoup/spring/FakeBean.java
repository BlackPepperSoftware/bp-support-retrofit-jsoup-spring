package uk.co.blackpepper.support.retrofit.jsoup.spring;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import static uk.co.blackpepper.support.date.Dates.copy;

public class FakeBean {
	
	public static final String DEFAULT_STRING_PROPERTY_VALUE = "_stringProperty";
	
	public enum FakeEnum {
		X,
		Y;
	}

	private String stringProperty;
	
	private Long longProperty;
	
	private Date dateProperty;
	
	private Date datePatternProperty;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date datePatternField;
	
	private FakeEnum enumProperty;
	
	public FakeBean() {
		stringProperty = DEFAULT_STRING_PROPERTY_VALUE;
	}
	
	public String getStringProperty() {
		return stringProperty;
	}
	
	public void setStringProperty(String stringProperty) {
		this.stringProperty = stringProperty;
	}
	
	public Long getLongProperty() {
		return longProperty;
	}
	
	public void setLongProperty(Long longProperty) {
		this.longProperty = longProperty;
	}
	
	public Date getDateProperty() {
		return copy(dateProperty);
	}
	
	public void setDateProperty(Date dateProperty) {
		this.dateProperty = copy(dateProperty);
	}
	
	public Date getDatePatternProperty() {
		return datePatternProperty;
	}
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	public void setDatePatternProperty(Date datePatternProperty) {
		this.datePatternProperty = datePatternProperty;
	}
	
	public Date getDatePatternField() {
		return datePatternField;
	}
	
	public void setDatePatternField(Date datePatternField) {
		this.datePatternField = datePatternField;
	}
	
	public FakeEnum getEnumProperty() {
		return enumProperty;
	}
	
	public void setEnumProperty(FakeEnum enumProperty) {
		this.enumProperty = enumProperty;
	}
}
