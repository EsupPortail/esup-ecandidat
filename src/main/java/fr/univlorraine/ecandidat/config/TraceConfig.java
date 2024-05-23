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
package fr.univlorraine.ecandidat.config;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.aop.Advisor;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.vaadin.spring.annotation.SpringView;

import fr.univlorraine.ecandidat.Initializer;
import fr.univlorraine.ecandidat.controllers.UserController;

/**
 * Configuration mode debug
 * @author Adrien Colson
 */
@SuppressWarnings("serial")
@Configuration
@Profile(Initializer.TRACE_PROFILE)
public class TraceConfig {

	/**
	 * @return Interceptor permettant de logger les appels aux méthodes
	 */
	@Bean
	public CustomizableTraceInterceptor customizableTraceInterceptor() {
		final CustomizableTraceInterceptor customizableTraceInterceptor = new CustomizableTraceInterceptor();
		customizableTraceInterceptor.setUseDynamicLogger(true);
		customizableTraceInterceptor.setEnterMessage("Entering $[methodName]($[arguments])");
		customizableTraceInterceptor.setExitMessage("Leaving  $[methodName]()");
		return customizableTraceInterceptor;
	}

	/**
	 * @return customizableTraceInterceptor sur les méthodes public des classes du package controllers
	 */
	@Bean
	public Advisor controllersAdvisor() {
		return new StaticMethodMatcherPointcutAdvisor(customizableTraceInterceptor()) {

			@Override
			public boolean matches(final Method method, final Class<?> clazz) {
				return Modifier.isPublic(method.getModifiers()) && clazz.getPackage() != null && clazz.getPackage().getName().startsWith(UserController.class.getPackage().getName())
				/* On ne log pas la connexion du candidat */
					&& !"connectCandidatInterne".equals(method.getName());
			}
		};
	}

	/**
	 * @return customizableTraceInterceptor sur les méthodes enter des vues
	 */
	@Bean
	public Advisor viewsEnterAdvisor() {
		return new StaticMethodMatcherPointcutAdvisor(customizableTraceInterceptor()) {
			@Override
			public boolean matches(final Method method, final Class<?> clazz) {
				return clazz.isAnnotationPresent(SpringView.class) && "enter".equals(method.getName());
			}
		};
	}

}
