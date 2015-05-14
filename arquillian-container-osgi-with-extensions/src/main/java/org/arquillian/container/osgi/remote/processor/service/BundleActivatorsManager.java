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

package org.arquillian.container.osgi.remote.processor.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cristina González
 */
public class BundleActivatorsManager {

	public ByteArrayOutputStream getBundleActivatorAsOutputStream(
			List<String> _bundleActivators)
		throws IOException {

		String bundleActivatorsString = "";

		for (String bundleActivator : _bundleActivators) {
			bundleActivatorsString += bundleActivator + "\n";
		}

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		outputStream.write(bundleActivatorsString.getBytes());

		return outputStream;
	}

	public List<String> getBundleActivators(InputStream is) throws IOException {
		return _getBundleActivatorFromInputStream(is);
	}

	private List<String> _getBundleActivatorFromInputStream(InputStream is)
		throws IOException {

		List<String> bundleActivators = new ArrayList<>();

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = reader.readLine()) != null) {
			bundleActivators.add(line);
		}

		reader.close();

		return bundleActivators;
	}

}