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

package org.arquillian.liferay.test;

import com.liferay.portal.model.Release;
import com.liferay.portal.service.ReleaseLocalService;

import java.io.File;

import org.arquillian.container.liferay.remote.enricher.Inject;
import org.arquillian.liferay.sample.service4injection.Service;
import org.arquillian.liferay.sample.service4injection.ServiceFirstImpl;
import org.arquillian.liferay.sample.service4injection.ServiceSecondImpl;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.osgi.api.BndProjectBuilder;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class TestInjections {

	@Deployment
	public static JavaArchive create() {
		BndProjectBuilder bndProjectBuilder = ShrinkWrap.create(
			BndProjectBuilder.class);

		bndProjectBuilder.setBndFile(new File("bnd-basic-portlet-test.bnd"));

		bndProjectBuilder.generateManifest(true);

		return bndProjectBuilder.as(JavaArchive.class);
	}

	@Test
	public void shouldInjectBundle() {
		Assert.assertNotNull(_bundle);
		Assert.assertEquals(
			"org.arquillian.liferay.sample", _bundle.getSymbolicName());
	}

	@Test
	public void shouldInjectBundleCOntext() {
		Assert.assertNotNull(_bundleContext);
		Assert.assertEquals(_bundle, _bundleContext.getBundle());
	}

	@Test
	public void shouldInjectReleaseLocalService() {
		Assert.assertNotNull(_releaseLocalService);

		Release releasePortal = _releaseLocalService.fetchRelease("portal");

		Assert.assertEquals(7000, releasePortal.getBuildNumber());
	}

	@Test
	public void shouldInjectServiceInOrden() {
		Assert.assertNotNull(_service);

		Assert.assertTrue(_service instanceof ServiceFirstImpl);
	}

	@Test
	public void shouldInjectServiceWithFilter() {
		Assert.assertNotNull(_secondService);

		Assert.assertTrue(_secondService instanceof ServiceSecondImpl);
	}

	@ArquillianResource
	private Bundle _bundle;

	@ArquillianResource
	private BundleContext _bundleContext;

	@Inject
	private ReleaseLocalService _releaseLocalService;

	@Inject("(name=ServiceSecond)")
	private Service _secondService;

	@Inject
	private Service _service;

}