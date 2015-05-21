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

package org.arquillian.container.osgi.allin.remote;

import java.io.IOException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jboss.arquillian.container.osgi.karaf.remote.KarafRemoteContainerConfiguration;
import org.jboss.arquillian.container.osgi.karaf.remote.KarafRemoteDeployableContainer;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;

/**
 * @author Cristina González
 */
public class KarafWithoutBundleRemoteDeployableContainer
	<T extends KarafRemoteContainerConfiguration>
	extends KarafRemoteDeployableContainer<T> {

	@Override
	protected void awaitArquillianBundleActive(long timeout, TimeUnit unit)
		throws InterruptedException, IOException, TimeoutException {

		//On purpose: It is not needed to install an Arquillian Bundle
	}

	@Override
	protected void installArquillianBundle()
		throws IOException, LifecycleException {

		//On purpose: It is not needed to install an Arquillian Bundle
	}

}