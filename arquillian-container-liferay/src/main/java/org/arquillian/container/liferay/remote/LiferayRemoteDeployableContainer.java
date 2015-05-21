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

package org.arquillian.container.liferay.remote;

import org.arquillian.container.osgi.allin.remote.KarafWithoutBundleRemoteDeployableContainer;

import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.protocol.metadata.HTTPContext;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.shrinkwrap.api.Archive;

/**
 * @author Carlos Sierra Andrés
 */
public class LiferayRemoteDeployableContainer
	<T extends LiferayRemoteContainerConfiguration>
	extends KarafWithoutBundleRemoteDeployableContainer<T> {

	@Override
	public ProtocolMetaData deploy(Archive<?> archive)
		throws DeploymentException {

		ProtocolMetaData protocolMetaData = super.deploy(archive);

		protocolMetaData.addContext(
			new HTTPContext(config.getHttpHost(), config.getHttpPort()));

		return protocolMetaData;
	}

	@Override
	public Class<T> getConfigurationClass() {
		@SuppressWarnings("uncheked")
		Class<T> clazz = (Class<T>)LiferayRemoteContainerConfiguration.class;
		return clazz;
	}

	@Override
	public void setup(T config) {
		this.config = config;

		super.setup(config);
	}

	protected LiferayRemoteContainerConfiguration config;

}