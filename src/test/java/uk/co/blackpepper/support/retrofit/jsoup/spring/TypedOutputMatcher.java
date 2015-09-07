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

import java.io.IOException;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import static com.google.common.base.Preconditions.checkNotNull;

import retrofit.mime.TypedOutput;
import uk.co.blackpepper.support.retrofit.TypedOutputs;

public class TypedOutputMatcher extends TypeSafeMatcher<TypedOutput> {
	
	private final TypedOutput expected;

	public TypedOutputMatcher(TypedOutput expected) {
		this.expected = checkNotNull(expected, "expected");
	}
	
	@Override
	public void describeTo(Description description) {
		try {
			description.appendValue(TypedOutputs.toString(expected));
		}
		catch (IOException exception) {
			description.appendValue(exception);
		}
	}
	
	@Override
	protected boolean matchesSafely(TypedOutput actual) {
		try {
			String expectedString = TypedOutputs.toString(expected);
			String actualString = TypedOutputs.toString(actual);
			
			return expectedString.equals(actualString);
		}
		catch (IOException exception) {
			return false;
		}
	}
	
	@Override
	protected void describeMismatchSafely(TypedOutput actual, Description description) {
		try {
			description.appendText("was: ").appendValue(TypedOutputs.toString(actual));
		}
		catch (IOException exception) {
			description.appendText("threw: ").appendValue(exception);
		}
	}
	
	public static TypeSafeMatcher<TypedOutput> typedOutputEqualTo(TypedOutput expected) {
		return new TypedOutputMatcher(expected);
	}
}
