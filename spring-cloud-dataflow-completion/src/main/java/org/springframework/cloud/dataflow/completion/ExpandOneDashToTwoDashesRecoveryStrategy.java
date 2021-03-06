/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.dataflow.completion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.dataflow.core.StreamDefinition;
import org.springframework.cloud.dataflow.core.dsl.CheckPointedParseException;
import org.springframework.cloud.dataflow.core.dsl.ParseException;
import org.springframework.util.Assert;

/**
 * Provides completion when the user has typed in the first dash to a module configuration
 * property.
 *
 * @author Eric Bottard
 */
class ExpandOneDashToTwoDashesRecoveryStrategy extends StacktraceFingerprintingRecoveryStrategy<ParseException> {

	@Autowired
	private ConfigurationPropertyNameAfterDashDashRecoveryStrategy recoveryAfterDashDash;

	public ExpandOneDashToTwoDashesRecoveryStrategy() {
		super(ParseException.class, "file -");
	}

	@Override
	public void addProposals(String dsl, ParseException exception, int detailLevel,
			List<CompletionProposal> proposals) {
		// Pretend there was an additional dash and invoke the dedicated strategy for that
		// case
		String withDashDash = dsl + "-";
		try {
			new StreamDefinition("__dummy", withDashDash);
		}
		catch (CheckPointedParseException recoverable) {
			Assert.isTrue(recoveryAfterDashDash.shouldTrigger(withDashDash, recoverable),
					"did not tigger after dash-dash");
			recoveryAfterDashDash.addProposals(withDashDash, recoverable, detailLevel, proposals);
		}
	}

}
