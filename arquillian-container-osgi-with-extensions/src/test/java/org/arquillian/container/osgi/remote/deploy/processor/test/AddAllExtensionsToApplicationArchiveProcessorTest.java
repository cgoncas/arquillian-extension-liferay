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

package org.arquillian.container.osgi.remote.deploy.processor.test;

import java.io.IOException;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.arquillian.container.osgi.remote.activator.ArquillianBundleActivator;
import org.arquillian.container.osgi.remote.deploy.processor.test.mock.DummyInstanceProducerImpl;
import org.arquillian.container.osgi.remote.deploy.processor.test.mock.DummyServiceLoaderWithJarAuxiliaryArchive;
import org.arquillian.container.osgi.remote.deploy.processor.test.mock.DummyServiceLoaderWithOSGIBundleAuxiliaryArchive;
import org.arquillian.container.osgi.remote.deploy.processor.test.mock.DummyServiceLoaderWithoutAuxiliaryArchive;
import org.arquillian.container.osgi.remote.deploy.processor.test.util.ManifestUtil;
import org.arquillian.container.osgi.remote.processor.AddAllExtensionsToApplicationArchiveProcessor;
import org.arquillian.container.osgi.remote.processor.service.ImportPackageManagerImpl;
import org.arquillian.container.osgi.remote.processor.service.ManifestManagerImpl;

import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Cristina González
 */
public class AddAllExtensionsToApplicationArchiveProcessorTest {

	@Test
	public void testGenerateDeployment() throws Exception {
		//given:
		JavaArchive javaArchive = getJavaArchive();
		javaArchive.addClass(this.getClass());

		ManifestUtil.createManifest(javaArchive);

		TestClass testClass = new TestClass(this.getClass());

		//when:
		AddAllExtensionsToApplicationArchiveProcessor processor =
			getProcessorWithoutAuxiliaryArchive();

		processor.process(javaArchive, testClass);

		//then:
		Manifest manifest = getManifest(javaArchive);

		Attributes mainAttributes = manifest.getMainAttributes();

		String bundleActivatorValue = mainAttributes.getValue(
			"Bundle-Activator");

		Assert.assertNotNull(
			"The Bundle-Activator has not been set", bundleActivatorValue);

		Assert.assertEquals(
			ArquillianBundleActivator.class.getName(), bundleActivatorValue);
	}

	@Test
	public void testGenerateDeploymentFromNonOSGiBundle() throws Exception {
		//given:
		JavaArchive javaArchive = getJavaArchive();
		javaArchive.addClass(this.getClass());

		TestClass testClass = new TestClass(this.getClass());

		try {
			//when:
			AddAllExtensionsToApplicationArchiveProcessor processor =
				getProcessorWithoutAuxiliaryArchive();

			processor.process(javaArchive, testClass);

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
	public void testGenerateDeploymentFromNonOSGiBundleDefaultImports()
		throws Exception {

		//given:
		JavaArchive javaArchive = getJavaArchive();
		javaArchive.addClass(this.getClass());

		ManifestUtil.createManifest(javaArchive);

		TestClass testClass = new TestClass(this.getClass());

		//when:
		AddAllExtensionsToApplicationArchiveProcessor processor =
			getProcessorWithoutAuxiliaryArchive();

		processor.process(javaArchive, testClass);

		//then:
		Manifest manifest = getManifest(javaArchive);

		Attributes mainAttributes = manifest.getMainAttributes();

		String importPackageValue = mainAttributes.getValue("Import-Package");

		Assert.assertNotNull(
			"Import-Package has not been set", importPackageValue);

		List<String> importsPackageArray = Arrays.asList(
			importPackageValue.split(","));

		importsPackageArray.contains("org.osgi.util.tracker");
		importsPackageArray.contains("javax.management");
		importsPackageArray.contains("org.osgi.service.startlevel");
	}

	@Test
	public void testGenerateDeploymentWithExtensionsWithImports()
		throws Exception {

		//given:
		JavaArchive javaArchive = getJavaArchive();
		javaArchive.addClass(this.getClass());

		ManifestUtil.createManifest(javaArchive);

		TestClass testClass = new TestClass(this.getClass());

		List<String> imports = new ArrayList<>();

		imports.add("import.example.1");
		imports.add("import.example.2");

		//when:
		AddAllExtensionsToApplicationArchiveProcessor processor =
			getProcessorWithOSGIJarAuxiliaryArchive(imports);

		processor.process(javaArchive, testClass);

		//then:
		Manifest manifest = getManifest(javaArchive);

		Attributes mainAttributes = manifest.getMainAttributes();

		String importPackageValue = mainAttributes.getValue("Import-Package");

		Assert.assertNotNull(
			"Import-Package has not been set", importPackageValue);

		for (String importExtension : imports) {
			Assert.assertTrue(
				"Import-Package should contains " + importExtension,
				importPackageValue.contains(importExtension));
		}
	}

	@Test
	public void testGenerateDeploymentWithExtensionsWithOptionalImportRepeated()
		throws Exception {

		//given:
		JavaArchive javaArchive = getJavaArchive();

		javaArchive.addClass(this.getClass());

		ManifestUtil.createManifest(javaArchive);

		TestClass testClass = new TestClass(this.getClass());

		List<String> imports = new ArrayList<>();

		String importValueNoOptional = "import.example.1";
		String importValueOptional = "import.example.1;resolution=optional";

		imports.add(importValueOptional);
		imports.add(importValueNoOptional);

		//when:
		AddAllExtensionsToApplicationArchiveProcessor processor =
			getProcessorWithOSGIJarAuxiliaryArchive(imports);

		processor.process(javaArchive, testClass);

		//then:
		Manifest manifest = getManifest(javaArchive);

		Attributes mainAttributes = manifest.getMainAttributes();

		String importPackageValue = mainAttributes.getValue("Import-Package");

		Assert.assertNotNull(
			"Import-Package has not been set", importPackageValue);

		String[] importsPackageArray = importPackageValue.split(",");

		int cont = 0;

		for (String importValue : importsPackageArray) {
			if (importValue.contains(importValueNoOptional)) {
				cont++;
			}
		}

		Assert.assertEquals("There are repeated imports", 1, cont);
	}

	@Test
	public void testGenerateDeploymentWithExtensionsWithOptionalImports()
		throws Exception {

		//given:
		JavaArchive javaArchive = getJavaArchive();

		javaArchive.addClass(this.getClass());

		ManifestUtil.createManifest(javaArchive);

		TestClass testClass = new TestClass(this.getClass());

		List<String> imports = new ArrayList<>();

		imports.add("import.example.1;resolution=optional");
		imports.add("import.example.2");

		//when:
		AddAllExtensionsToApplicationArchiveProcessor processor =
			getProcessorWithOSGIJarAuxiliaryArchive(imports);

		processor.process(javaArchive, testClass);

		//then:
		Manifest manifest = getManifest(javaArchive);

		Attributes mainAttributes = manifest.getMainAttributes();

		String importPackageValue = mainAttributes.getValue("Import-Package");

		Assert.assertNotNull(
			"Import-Package has not been set", importPackageValue);

		String[] importsPackageArray = importPackageValue.split(",");

		for (String importValue : importsPackageArray) {
			if (importValue.contains(imports.get(0))) {
				Assert.assertEquals(
					"The import value " + importValue + " is not OK",
					importValue, imports.get(0));
			}
		}
	}

	@Test
	public void testGenerateDeploymentWithExtensionsWithoutManifest()
		throws Exception {

		//given:
		JavaArchive javaArchive = getJavaArchive();

		javaArchive.addClass(this.getClass());

		ManifestUtil.createManifest(javaArchive);

		TestClass testClass = new TestClass(this.getClass());

		//when:
		AddAllExtensionsToApplicationArchiveProcessor processor =
			getProcessorWithJarAuxiliaryArchive();

		processor.process(javaArchive, testClass);

		//then:
		Manifest manifest = getManifest(javaArchive);

		Attributes mainAttributes = manifest.getMainAttributes();

		String bundleClassPathValue = mainAttributes.getValue(
			"Bundle-ClassPath");

		Assert.assertNotNull(
			"The Bundle-ClassPath has not been set", bundleClassPathValue);

		List<String> bundleClassPaths = Arrays.asList(
			bundleClassPathValue.split(","));

		Assert.assertEquals(2, bundleClassPaths.size());

		Assert.assertTrue(
			"Bundle-ClassPath should contain . ",
			bundleClassPathValue.contains("."));

		Assert.assertTrue(
			"The Bundle-ClassPath should contain the auxiliaryArchive",
			bundleClassPathValue.contains("dummy-jar.jar"));
	}

	@Test
	public void testGenerateDeploymentWithExtensionsWithRepeatedImports()
		throws Exception {

		//given:
		JavaArchive javaArchive = getJavaArchive();

		javaArchive.addClass(this.getClass());

		ManifestUtil.createManifest(javaArchive);

		TestClass testClass = new TestClass(this.getClass());

		List<String> imports = new ArrayList<>();

		imports.add("import.example.1");
		imports.add("import.example.2");

		//when:
		AddAllExtensionsToApplicationArchiveProcessor processor =
			getProcessorWithOSGIJarAuxiliaryArchive(imports);

		processor.process(javaArchive, testClass);

		//then:
		Manifest manifest = getManifest(javaArchive);

		Attributes mainAttributes = manifest.getMainAttributes();

		String importPackageValue = mainAttributes.getValue("Import-Package");

		Assert.assertNotNull(
			"Import-Package has not been set", importPackageValue);

		String[] importsPackageArray = importPackageValue.split(",");

		int cont = 0;

		for (String importPackage : importsPackageArray) {
			if (imports.get(0).equals(importPackage)) {
				cont++;
			}
		}

		Assert.assertEquals(
			"The import " + imports.get(0) +
				" should not be repeated", 1, cont);
	}

	@Test
	public void testGenerateDeploymentWithoutExtensions() throws Exception {
		//given:
		JavaArchive javaArchive = getJavaArchive();

		javaArchive.addClass(this.getClass());

		ManifestUtil.createManifest(javaArchive);

		TestClass testClass = new TestClass(this.getClass());

		//when:
		AddAllExtensionsToApplicationArchiveProcessor processor =
			getProcessorWithoutAuxiliaryArchive();

		processor.process(javaArchive, testClass);

		//then:
		Manifest manifest = getManifest(javaArchive);

		Attributes mainAttributes = manifest.getMainAttributes();

		String bundleClassPathValue = mainAttributes.getValue(
			"Bundle-ClassPath");

		Assert.assertNull(
			"The Bundle-ClassPath atribute has not been correctly initialized",
			bundleClassPathValue);
	}

	private JavaArchive getJavaArchive() {
		JavaArchive javaArchive = ShrinkWrap.create(
			JavaArchive.class, "arquillian-osgi-liferay-test.jar");

		return javaArchive;
	}

	private Manifest getManifest(JavaArchive javaArchive) throws IOException {
		Node node = javaArchive.get(JarFile.MANIFEST_NAME);

		Assert.assertNotNull(
			"The deployment java archive doesn't contain a manifest file",
			node);

		Asset asset = node.getAsset();

		Assert.assertNotNull(
			"The deployment java archive doesn't contain a manifest file",
			asset);

		return new Manifest(asset.openStream());
	}

	private AddAllExtensionsToApplicationArchiveProcessor getProcessor(
			ServiceLoader serviceLoader)
		throws NoSuchFieldException {

		AddAllExtensionsToApplicationArchiveProcessor
			addAllExtensionsToApplicationArchiveProcessor =
				new AddAllExtensionsToApplicationArchiveProcessor();

		Field serviceLoaderInstance =
			AddAllExtensionsToApplicationArchiveProcessor.class.
				getDeclaredField("_serviceLoaderInstance");
		serviceLoaderInstance.setAccessible(true);

		DummyInstanceProducerImpl serviceLoaderDummyInstance =
			new DummyInstanceProducerImpl();

		serviceLoaderDummyInstance.set(serviceLoader);

		try {
			serviceLoaderInstance.set(
				addAllExtensionsToApplicationArchiveProcessor,
				serviceLoaderDummyInstance);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		Field importPackageManagerInstance =
			AddAllExtensionsToApplicationArchiveProcessor.class.
				getDeclaredField("_importPackageManagerInstance");
		importPackageManagerInstance.setAccessible(true);

		DummyInstanceProducerImpl importPackageManagerDummyInstance =
			new DummyInstanceProducerImpl();

		importPackageManagerDummyInstance.set(new ImportPackageManagerImpl());

		try {
			importPackageManagerInstance.set(
				addAllExtensionsToApplicationArchiveProcessor,
				importPackageManagerDummyInstance);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		Field manifestManagerInstance =
			AddAllExtensionsToApplicationArchiveProcessor.class.
				getDeclaredField("_manifestManagerInstance");
		manifestManagerInstance.setAccessible(true);

		DummyInstanceProducerImpl manifestManagerDummyInstance =
			new DummyInstanceProducerImpl();

		manifestManagerDummyInstance.set(new ManifestManagerImpl());

		try {
			manifestManagerInstance.set(
				addAllExtensionsToApplicationArchiveProcessor,
				manifestManagerDummyInstance);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return addAllExtensionsToApplicationArchiveProcessor;
	}

	private AddAllExtensionsToApplicationArchiveProcessor
			getProcessorWithJarAuxiliaryArchive()
		throws IllegalAccessException, NoSuchFieldException {

		return getProcessor(new DummyServiceLoaderWithJarAuxiliaryArchive());
	}

	private AddAllExtensionsToApplicationArchiveProcessor
		getProcessorWithOSGIJarAuxiliaryArchive(
			List<String> imports)
		throws IllegalAccessException, NoSuchFieldException {

		return getProcessor(
			new DummyServiceLoaderWithOSGIBundleAuxiliaryArchive(imports));
	}

	private AddAllExtensionsToApplicationArchiveProcessor
			getProcessorWithoutAuxiliaryArchive()
		throws IllegalAccessException, NoSuchFieldException {

		return getProcessor(new DummyServiceLoaderWithoutAuxiliaryArchive());
	}

}