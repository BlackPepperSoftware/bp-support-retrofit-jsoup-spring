/*
 * Copyright 2014 Black Pepper Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.blackpepper.support.retrofit.jsoup.spring;

import org.jsoup.nodes.Element;

import com.google.common.base.Function;

/**
 * Factory for functions that convert from HTML to string.
 * 
 * @see AbstractBeanHtmlConverter#setProperty(String, Function, String)
 */
public final class FromBody {
	
	private FromBody() {
		throw new AssertionError();
	}
	
	public static Function<Element, String> textById(final String id) {
		return new Function<Element, String>() {
			@Override
			public String apply(Element element) {
				Element propertyElement = element.getElementById(id);
				return (propertyElement == null) ? null : propertyElement.text();
			}
		};
	}
	
	public static Function<Element, String> textByClass(final String className) {
		return new Function<Element, String>() {
			@Override
			public String apply(Element element) {
				Element propertyElement = element.getElementsByClass(className).first();
				return (propertyElement == null) ? null : propertyElement.text();
			}
		};
	}

	public static Function<Element, String> attributeById(final String id, final String attributeName) {
		return new Function<Element, String>() {
			@Override
			public String apply(Element element) {
				Element propertyElement = element.getElementById(id);
				return (propertyElement == null) ? null : propertyElement.attr(attributeName);
			}
		};
	}
	
	public static Function<Element, String> attributeByClass(final String className, final String attributeName) {
		return new Function<Element, String>() {
			@Override
			public String apply(Element element) {
				Element propertyElement = element.getElementsByClass(className).first();
				return (propertyElement == null) ? null : propertyElement.attr(attributeName);
			}
		};
	}
}
