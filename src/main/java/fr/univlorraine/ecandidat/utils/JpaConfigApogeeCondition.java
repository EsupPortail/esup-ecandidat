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