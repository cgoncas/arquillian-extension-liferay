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

package org.arquillian.container.liferay.remote.deploy.processor.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.arquillian.container.liferay.remote.activator.ArquillianBundleActivator;
import org.arquillian.container.liferay.remote.deploy.OSGiDeploymentPackager;
import org.arquillian.container.liferay.remote.deploy.processor.test.util.ManifestUtil;
import org.arquillian.container.liferay.remote.enricher.LiferayEnricherAuxiliaryAppender;

import org.jboss.arquillian.container.spi.client.deployment.DeploymentDescription;
import org.jboss.arquillian.container.test.spi.TestDeployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Cristina González
 */
public class OSGiDeploymentPackagerTest {

	@Test
	public void testGenerateDeployment() throws Exception {
		//given:
		JavaArchive javaArchive = getJavaArchive();
		javaArchive.addClass(this.getClass());

		ManifestUtil.createManifest(javaArchive);

		DeploymentDescription deploymentDescription = new DeploymentDescription(
			"test-deployment", javaArchive);

		TestDeployment testDeployment = new TestDeployment(
			deploymentDescription, javaArchive, new ArrayList<Archive<?>>());

		//when:
		Archive javaArchiveGenerated =
			new OSGiDeploymentPackager().generateDeployment(
				testDeployment, null);

		//then:
		Manifest manifest = getManifest((JavaArchive)javaArchiveGenerated);

		Attributes mainAttributes = manifest.getMainAttributes();

		String bundleActivatorValue = mainAttributes.getValue(
			"Bundle-Activator");

		Assert.assertNotNull(
			"The Bundle-Activator has not been set", bundleActivatorValue);

		Assert.assertEquals(
			ArquillianBundleActivator.class.getName(), bundleActivatorValue);
	}

	@Test
	public void testGenerateDeploymentFromNonOSGiBundle() {
		//given:
		JavaArchive javaArchive = getJavaArchive();
		javaArchive.addClass(this.getClass());

		DeploymentDescription deploymentDescription = new DeploymentDescription(
			"test-deployment", javaArchive);

		TestDeployment testDeployment = new TestDeployment(
			deploymentDescription, javaArchive, new ArrayList<Archive<?>>());

		try {
			//when:
			new OSGiDeploymentPackager().generateDeployment(
				testDeployment, null);

			Assert.fail(
				"If a JavaArchive doesn't contain a Manifest, should fail");
		}
		catch (IllegalArgumentException iae) {
			//then:
			Assert.assertEquals(
				iae.getMessage(), "Not a valid OSGi bundle: " + javaArchive);
		}
	}

	@Test
	public void testGenerateDeploymentWithExtensionsWithImports()
		throws Exception {

		//given:
		JavaArchive javaArchive = getJavaArchive();
		javaArchive.addClass(this.getClass());

		ManifestUtil.createManifest(javaArchive);

		DeploymentDescription deploymentDescription = new DeploymentDescription(
			"test-deployment", javaArchive);

		List<String> importsExtensions = new ArrayList<>();

		importsExtensions.add("import.example.1");
		importsExtensions.add("import.example.2");

		List<Archive<?>> auxiliaryArchives = getAuxiliaryArchivesWithManifest(
			importsExtensions);

		TestDeployment testDeployment = new TestDeployment(
			deploymentDescription, javaArchive, auxiliaryArchives);

		//when:
		Archive javaArchiveGenerated =
			new OSGiDeploymentPackager().generateDeployment(
				testDeployment, null);

		//then:
		Manifest manifest = getManifest((JavaArchive)javaArchiveGenerated);

		Attributes mainAttributes = manifest.getMainAttributes();

		String importPackageValue = mainAttributes.getValue("Import-Package");

		Assert.assertNotNull(
			"Import-Package has not been set", importPackageValue);

		for (String importExtension : importsExtensions) {
			Assert.assertTrue(
				"Import-Package should contains " + importExtension,
				importPackageValue.contains(importExtension));
		}
	}

	@Test
	public void testGenerateDeploymentWithExtensionsWithoutManifest()
		throws Exception {

		//given:
		JavaArchive javaArchive = getJavaArchive();

		javaArchive.addClass(this.getClass());

		ManifestUtil.createManifest(javaArchive);

		DeploymentDescription deploymentDescription = new DeploymentDescription(
			"test-deployment", javaArchive);

		List<Archive<?>> auxiliaryArchives = getAuxiliaryArchives();

		TestDeployment testDeployment = new TestDeployment(
			deploymentDescription, javaArchive, auxiliaryArchives);

		//when:
		Archive javaArchiveGenerated =
			new OSGiDeploymentPackager().generateDeployment(
				testDeployment, null);

		//then:
		Manifest manifest = getManifest((JavaArchive)javaArchiveGenerated);

		Attributes mainAttributes = manifest.getMainAttributes();

		String bundleClassPathValue = mainAttributes.getValue(
			"Bundle-ClassPath");

		Assert.assertNotNull(
			"The Bundle-ClassPath has not been set", bundleClassPathValue);

		for (Archive<?> auxiliaryArchive : auxiliaryArchives) {
			Assert.assertTrue(
				"The Bundle-ClassPath should contain the auxiliaryArchive",
				bundleClassPathValue.contains(auxiliaryArchive.getName()));
		}
	}

	@Test
	public void testGenerateDeploymentWithExtensionsWithRepeatedImports()
		throws Exception {

		//given:
		JavaArchive javaArchive = getJavaArchive();

		javaArchive.addClass(this.getClass());

		ManifestUtil.createManifest(javaArchive);

		DeploymentDescription deploymentDescription = new DeploymentDescription(
			"test-deployment", javaArchive);

		List<String> importsExtensions = new ArrayList<>();

		importsExtensions.add("import.example.1");
		importsExtensions.add("import.example.1");

		List<Archive<?>> auxiliaryArchives = getAuxiliaryArchivesWithManifest(
			importsExtensions);

		TestDeployment testDeployment = new TestDeployment(
			deploymentDescription, javaArchive, auxiliaryArchives);

		//when:
		Archive javaArchiveGenerated =
			new OSGiDeploymentPackager().generateDeployment(
				testDeployment, null);

		//then:
		Manifest manifest = getManifest((JavaArchive)javaArchiveGenerated);

		Attributes mainAttributes = manifest.getMainAttributes();

		String importPackageValue = mainAttributes.getValue("Import-Package");

		Assert.assertNotNull(
			"Import-Package has not been set", importPackageValue);

		String[] importsPackageArray = importPackageValue.split(",");

		int cont = 0;

		for (String importPackage : importsPackageArray) {
			if (importsExtensions.get(0).equals(importPackage)) {
				cont++;
			}
		}

		System.out.println("cont " + cont);

		Assert.assertEquals(
			"The import " + importsExtensions.get(0) +
				" should not be repeated", 1, cont);
	}

	@Test
	public void testGenerateDeploymentWithoutExtensions() throws Exception {
		//given:
		JavaArchive javaArchive = getJavaArchive();

		javaArchive.addClass(this.getClass());

		ManifestUtil.createManifest(javaArchive);

		DeploymentDescription deploymentDescription = new DeploymentDescription(
			"test-deployment", javaArchive);

		TestDeployment testDeployment = new TestDeployment(
			deploymentDescription, javaArchive, new ArrayList<Archive<?>>());

		//when:
		Archive javaArchiveGenerated =
			new OSGiDeploymentPackager().generateDeployment(
				testDeployment, null);

		//then:
		Manifest manifest = getManifest((JavaArchive)javaArchiveGenerated);

		Attributes mainAttributes = manifest.getMainAttributes();

		String bundleClassPathValue = mainAttributes.getValue(
			"Bundle-ClassPath");

		Assert.assertNull(
			"The Bundle-ClassPath atribute has not been correctly initialized",
			bundleClassPathValue);
	}

	private List<Archive<?>> getAuxiliaryArchives() {
		List<Archive<?>> auxiliaryArchives = new ArrayList<>();

		LiferayEnricherAuxiliaryAppender liferayEnricherAuxiliaryAppender =
			new LiferayEnricherAuxiliaryAppender();

		auxiliaryArchives.add(
			liferayEnricherAuxiliaryAppender.createAuxiliaryArchive());

		return auxiliaryArchives;
	}

	private List<Archive<?>> getAuxiliaryArchivesWithManifest(
			List<String> imports)
		throws IOException {

		List<Archive<?>> auxiliaryArchives = new ArrayList<>();

		LiferayEnricherAuxiliaryAppender liferayEnricherAuxiliaryAppender =
			new LiferayEnricherAuxiliaryAppender();

		Archive<?> auxiliaryArchive =
			liferayEnricherAuxiliaryAppender.createAuxiliaryArchive();

		ManifestUtil.createManifest((JavaArchive) auxiliaryArchive, imports);

		auxiliaryArchives.add(auxiliaryArchive);

		return auxiliaryArchives;
	}

	private JavaArchive getJavaArchive() {
		JavaArchive javaArchive = ShrinkWrap.create(
			JavaArchive.class, "arquillian-osgi-liferay-test.jar");

		return javaArchive;
	}

	private Manifest getManifest(JavaArchive javaArchive) throws IOException {
		Node node = javaArchive.get(JarFile.MANIFEST_NAME);

		Assert.assertNotNull(
			"The deployment java archive doen't contain a manifest file", node);

		Asset asset = node.getAsset();

		Assert.assertNotNull(
			"The deployment java archive doen't contain a manifest file",
			asset);

		return new Manifest(asset.openStream());
	}

}