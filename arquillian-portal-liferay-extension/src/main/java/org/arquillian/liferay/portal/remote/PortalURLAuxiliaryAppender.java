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

package org.arquillian.liferay.portal.remote;

import java.net.URL;

import org.arquillian.liferay.portal.servlet.PortalURLServlet;

import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * @author Cristina González
 */
public class PortalURLAuxiliaryAppender implements AuxiliaryArchiveAppender {

	@Override
	public Archive<?> createAuxiliaryArchive() {
		WebArchive archive = ShrinkWrap.create(
			WebArchive.class, "arquillian-install-portlet-in-liferay.war");

		archive.addClass(PortalURLServlet.class);
		archive.addClass(PortalURLAuxiliaryAppender.class);

		URL urlWebXML = getClass().getResource("web.xml");

		archive.setWebXML(urlWebXML);

		return archive;
	}

}