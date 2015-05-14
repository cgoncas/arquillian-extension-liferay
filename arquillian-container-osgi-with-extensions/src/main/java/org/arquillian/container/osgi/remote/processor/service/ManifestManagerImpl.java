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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;

/**
 * @author Cristina González
 */
public class ManifestManagerImpl implements ManifestManager {

	public Manifest putAttibuteValue(
			Manifest manifest, String attributeName, String... attributeValue)
		throws IOException {

		Attributes mainAttributes = manifest.getMainAttributes();

		String attributeValues = mainAttributes.getValue(attributeName);

		Set<String> attributeValueSet = new HashSet<>();

		if (attributeValues != null) {
			attributeValueSet.addAll(Arrays.asList(attributeValues.split(",")));
		}

		attributeValueSet.addAll(Arrays.asList(attributeValue));

		StringBuilder sb = new StringBuilder();

		for (String value : attributeValueSet) {
			sb.append(value);
			sb.append(",");
		}

		if (!attributeValueSet.isEmpty()) {
			sb.setLength(sb.length() - 1);
		}

		attributeValues = sb.toString();

		mainAttributes.putValue(attributeName, attributeValues);

		return manifest;
	}

	public void replaceManifest(Archive archive, Manifest manifest )
		throws IOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		manifest.write(baos);

		ByteArrayAsset byteArrayAsset = new ByteArrayAsset(baos.toByteArray());

		archive.delete(JarFile.MANIFEST_NAME);

		archive.add(byteArrayAsset, JarFile.MANIFEST_NAME);
	}

}