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

package org.arquillian.container.osgi.remote.processor;

import aQute.bnd.osgi.Jar;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;

/**
 * @author Cristina González
 */
public class ImportPackageManager {

	public List<String> getImportsNotIncludedInClassPath(
			String importsInManifest, Collection<Archive<?>> auxiliaryArchives)
		throws IOException {

		List<String> auxiliaryArchivesPackages = getAuxiliaryArchivesPackages(
			auxiliaryArchives);

		Map<String, List<String>> importsWithDirectivesMap =
			toImportsWithDirectivesMap(importsInManifest);

		List<String> resultImports = new ArrayList<>();

		for (String importValue : importsWithDirectivesMap.keySet()) {
			if (auxiliaryArchivesPackages.contains(importValue)) {
				continue;
			}

			StringBuilder sb = new StringBuilder();
			sb.append(importValue);

			for (String directive : importsWithDirectivesMap.get(importValue)) {
				sb.append(";");
				sb.append(directive);
			}

			resultImports.add(sb.toString());
		}

		return resultImports;
	}

	private List<String> getAuxiliaryArchivesPackages(
			Collection<Archive<?>> auxiliaryArchives)
		throws IOException {

		List<String> packages = new ArrayList<>();

		for (Archive auxiliaryArchive : auxiliaryArchives) {
			ZipExporter zipExporter = auxiliaryArchive.as(ZipExporter.class);

			InputStream auxiliaryArchiveInputStream =
				zipExporter.exportAsInputStream();

			Jar jar = new Jar(
				auxiliaryArchive.getName(), auxiliaryArchiveInputStream);

			packages.addAll(jar.getPackages());
		}

		return packages;
	}

	private Map<String, List<String>> toImportsWithDirectivesMap(
		String importsInManifest) {

		List<String> importsWithDirectives = Arrays.asList(
			importsInManifest.split(","));

		Map<String, List<String>> importsWithDirectivesMap = new HashMap<>();

		for (String importWithDirectives : importsWithDirectives) {
			List<String> importWithDirectivesList = Arrays.asList(
				importWithDirectives.split(";"));

			List<String> currentDirectives = importsWithDirectivesMap.get(
				importWithDirectivesList.get(0));

			if (currentDirectives == null) {
				currentDirectives = new ArrayList<>();
			}

			if (importWithDirectivesList.size() == 1) {
				importsWithDirectivesMap.put(
					importWithDirectivesList.get(0), currentDirectives);

				continue;
			}

			currentDirectives.addAll(
				importWithDirectivesList.subList(
					1, importWithDirectivesList.size()));

			importsWithDirectivesMap.put(
				importWithDirectivesList.get(0), currentDirectives);
		}

		for (String importValue : importsWithDirectivesMap.keySet()) {
			List<String> directives = importsWithDirectivesMap.get(importValue);
			List<String> finalDirectives = new ArrayList<>();

			for (String directive : directives) {
				if (!finalDirectives.contains(directive)) {
					finalDirectives.add(directive);
				}
			}

			importsWithDirectivesMap.put(importValue, finalDirectives);
		}

		return importsWithDirectivesMap;
	}

}