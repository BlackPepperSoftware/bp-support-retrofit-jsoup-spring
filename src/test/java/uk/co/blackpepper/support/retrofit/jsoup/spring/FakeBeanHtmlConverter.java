package uk.co.blackpepper.support.retrofit.jsoup.spring;

import retrofit.mime.FormUrlEncodedTypedOutput;

public class FakeBeanHtmlConverter extends AbstractBeanHtmlConverter<FormUrlEncodedTypedOutput> {

	@Override
	protected FormUrlEncodedTypedOutput newTypedOutput() {
		return new FormUrlEncodedTypedOutput();
	}

	@Override
	protected void addProperty(FormUrlEncodedTypedOutput output, String id, String text) {
		output.addField(id, text);
	}
}
