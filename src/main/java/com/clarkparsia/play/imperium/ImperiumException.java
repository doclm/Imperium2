/*
 * Copyright (c) 2009-2011 Clark & Parsia, LLC. <http://www.clarkparsia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clarkparsia.play.imperium;

import play.api.PlayException;

/**
 * <p>Exception which arises while using Imperium.</p>
 *
 * @author Michael Grove
 * @since 0.1
 * @version 0.1
 */
public class ImperiumException extends PlayException {

	/**
	 * Create a new ImperiumException
	 * @param title title for the error message
	 * @param description the error message
	 * @param theCause the cause of the erro
	 */
	public ImperiumException(final String title, final String description, final Throwable theCause) {
		super(title, description, theCause);
	}



    /**
	 * @inheritDoc
	 */
	public String getErrorTitle() {
		return "Imperium Exception";
	}

	/**
	 * @inheritDoc
	 */
	public String getErrorDescription() {
		return "An error occurred with Imperium.";
	}
}
