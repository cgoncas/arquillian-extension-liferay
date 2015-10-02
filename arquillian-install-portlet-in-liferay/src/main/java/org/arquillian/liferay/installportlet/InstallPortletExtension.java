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

package org.arquillian.liferay.installportlet;

import org.arquillian.liferay.installportlet.annotation.InstallPortletTestEnricher;
import org.arquillian.liferay.installportlet.remote.LiferayInstallPortletAuxiliaryAppender;

import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.TestEnricher;

/**
 * @author Cristina González Castellano
 */
public class InstallPortletExtension implements LoadableExtension {

	@Override
	public void register(ExtensionBuilder builder) {
		builder.service(
			AuxiliaryArchiveAppender.class,
			LiferayInstallPortletAuxiliaryAppender.class);

		builder.service(TestEnricher.class, InstallPortletTestEnricher.class);
	}

}