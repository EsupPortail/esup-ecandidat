package fr.univlorraine.ecandidat.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration du cache.
 * @author Kevin Hergalant
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

	//private final Logger logger = LoggerFactory.getLogger(CacheConfig.class);
	public static final String CACHE_MANAGER_NAME = "cacheManager";
	public static final String CACHE_CONF_ETAB = "conf_etab";
	public static final String CACHE_CONF_RESSOURCE = "conf_ressource";

	@Override
	@Bean(name = "cacheManager")
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager();
	}

	/**
	 * @see org.springframework.cache.annotation.CachingConfigurerSupport#keyGenerator()
	 */
	@Override
	public KeyGenerator keyGenerator() {
		return (target, method, params) -> {
			final StringBuilder sbKey = new StringBuilder();
			sbKey.append(target.getClass().getName());
			sbKey.append("#" + method.getName());
			for (final Object param : params) {
				if (param != null) {
					sbKey.append("#" + param.toString());
				}
			}
			return sbKey.toString();
		};
	}
}
