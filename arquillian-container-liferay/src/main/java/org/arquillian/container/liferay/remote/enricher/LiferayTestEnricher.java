/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package org.arquillian.container.liferay.remote.enricher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.jboss.arquillian.test.spi.TestEnricher;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.ServiceReference;

/**
 * @author Carlos Sierra Andrés
 */
public class LiferayTestEnricher implements TestEnricher {

	public boolean contains(
		Annotation[] annotations, Class<?> annotationClass) {

		for (Annotation current : annotations) {
			if (annotationClass.isAssignableFrom(current.annotationType())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void enrich(Object testCase) {
		Class<?> testClass = testCase.getClass();

		Field[] declaredFields = testClass.getDeclaredFields();

		for (Field declaredField : declaredFields) {
			if (declaredField.isAnnotationPresent(Inject.class)) {
				try {
					injectField(declaredField, testCase);
				}
				catch (IllegalAccessException e) {
					throw new RuntimeException(
						"It is not possible to inject the field " +
							e.getMessage(),
						e);
				}
			}
		}
	}

	@Override
	public Object[] resolve(Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		Annotation[][] parametersAnnotations = method.getParameterAnnotations();

		Object[] parameters = new Object[parameterTypes.length];

		for (int i = 0; i < parameterTypes.length; i++) {
			Annotation[] parameterAnnotations = parametersAnnotations[i];

			if (contains(parameterAnnotations, Inject.class)) {
				parameters[i] = resolve(
					parameterTypes[i], method.getDeclaringClass());
			}
		}

		return parameters;
	}

	private Bundle getBundle(Class<?> testCaseClass) {
		ClassLoader classLoader = testCaseClass.getClassLoader();

		if (classLoader instanceof BundleReference) {
			return ((BundleReference)classLoader).getBundle();
		}

		throw new RuntimeException("Test is not running inside BundleContext");
	}

	private void injectField(Field declaredField, Object testCase)
		throws IllegalAccessException {

		Class<?> componentClass = declaredField.getType();

		Object service = resolve(componentClass, testCase.getClass());

		setField(declaredField, testCase, service);
	}

	private Object resolve(Class<?> componentClass, Class<?> testCaseClass) {
		Bundle bundle = getBundle(testCaseClass);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceReference<?> serviceReference =
			bundleContext.getServiceReference(componentClass);

		return bundleContext.getService(serviceReference);
	}

	private void setField(Field declaredField, Object testCase, Object service)
		throws IllegalAccessException {

		boolean accessible = declaredField.isAccessible();

		declaredField.setAccessible(true);

		declaredField.set(testCase, service);

		declaredField.setAccessible(accessible);
	}

}