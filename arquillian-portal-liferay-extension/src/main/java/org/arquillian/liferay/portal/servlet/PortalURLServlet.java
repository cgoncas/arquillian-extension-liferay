/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package org.arquillian.liferay.portal.servlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.User;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.PortletPreferences;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Cristina González
 */
public class PortalURLServlet extends HttpServlet {

	public PortalURLServlet() {
		// NOOP
	}

	@Override
	public void destroy() {
		if (_layouts != null) {
			for (Layout layout : _layouts) {
				try {
					LayoutLocalServiceUtil.deleteLayout(
						layout.getPlid(), new ServiceContext());
				}
				catch (PortalException | SystemException e) {
					_logger.log(
						Level.WARNING,
						"Error trying to delete layout " + layout.getPlid(), e);
				}
			}
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {

		String portletId = request.getParameter("portlet-id");

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		out.println("<h1> Portlet ID: " + portletId + "</h1>");

		try {
			Company company = CompanyLocalServiceUtil.getCompanies().get(0);

			if (_layouts == null) {
				_layouts = new ArrayList<>();
			}

			Group guestGroup = GroupLocalServiceUtil.getGroup(
				company.getCompanyId(), "Guest");

			User defaultUser = UserLocalServiceUtil.getDefaultUser(
				company.getCompanyId());

			UUID uuid = UUID.randomUUID();

			Layout layout = LayoutLocalServiceUtil.addLayout(
				defaultUser.getUserId(), guestGroup.getGroupId(), false, 0,
				uuid.toString(), null, null, "portlet", false,
				"/" + uuid.toString(), new ServiceContext());

			_layouts.add(layout);

			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)layout.getLayoutType();

			layoutTypePortlet.setLayoutTemplateId(
				defaultUser.getUserId(), "1_column");

			String portletIdAdded = layoutTypePortlet.addPortletId(
				defaultUser.getUserId(), portletId, false);

			long ownerId = 0;
			int ownerType = 3;

			PortletPreferences prefs =
				PortletPreferencesLocalServiceUtil.getPreferences(
					company.getCompanyId(), ownerId, ownerType,
					layout.getPlid(), portletIdAdded);

			PortletPreferencesLocalServiceUtil.updatePreferences(
				ownerId, ownerType, layout.getPlid(), portletIdAdded, prefs);

			LayoutLocalServiceUtil.updateLayout(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId(), layout.getTypeSettings());

			response.sendRedirect("/"+uuid.toString());
		}
		catch (PortalException | SystemException e) {
			_logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	public void init() throws ServletException {
	}

	private static final Logger _logger = Logger.getLogger(
		PortalURLServlet.class.getName());

	private List<Layout> _layouts;

}