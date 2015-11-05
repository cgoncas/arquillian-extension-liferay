package org.arquillian.liferay.portal;

import org.arquillian.liferay.portal.annotation.PortalURLTestEnricher;
import org.arquillian.liferay.portal.remote.PortalURLAuxiliaryAppender;

import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.TestEnricher;

/**
 * @author Cristina González
 */
public class PortalURLExtension implements LoadableExtension {

	@Override
	public void register(ExtensionBuilder builder) {
		builder.service(
			AuxiliaryArchiveAppender.class, PortalURLAuxiliaryAppender.class);

		builder.service(TestEnricher.class, PortalURLTestEnricher.class);
	}

}
