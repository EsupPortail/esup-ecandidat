/**
 *  ESUP-Portail eCandidat - Copyright (c) 2016 ESUP-Portail consortium
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package fr.univlorraine.ecandidat.utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

public class JpaConfigApogeeCondition implements Condition {

	@Override
	public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
		try {
			return StringUtils.isNotBlank(context.getEnvironment().getProperty("datasource.apogee.url"))
				||
				new JndiDataSourceLookup().getDataSource("java:/comp/env/jdbc/dbSiScol") != null;
		} catch (final Exception e) {
			return true;
		}
	}
}