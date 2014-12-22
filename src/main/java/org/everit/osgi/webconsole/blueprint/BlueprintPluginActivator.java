/**
 * This file is part of Everit - OSGi Webconsole Blueprint plugin.
 *
 * Everit - OSGi Webconsole Blueprint plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Everit - OSGi Webconsole Blueprint plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Everit - OSGi Webconsole Blueprint plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.webconsole.blueprint;

import java.util.Hashtable;

import javax.servlet.Servlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.blueprint.container.BlueprintListener;

/**
 * {@link BundleActivator} for Blueprint webConsole plugin.
 */
public class BlueprintPluginActivator implements BundleActivator {
    /**
     * {@link ServiceRegistration} of {@link BlueprintListener}s.
     */
    private ServiceRegistration<BlueprintListener> blueprintListenerSR;
    /**
     * {@link ServiceRegistration} of {@link Servlet}s.
     */
    private ServiceRegistration<Servlet> blueprintServletSR;

    @Override
    public void start(final BundleContext context) throws Exception {

        BlueprintListenerImpl blueprintListener = new BlueprintListenerImpl();
        blueprintListenerSR = context.registerService(BlueprintListener.class, blueprintListener,
                new Hashtable<String, Object>());

        Hashtable<String, String> servletProps = new Hashtable<String, String>();
        servletProps.put("felix.webconsole.label", "everit_blueprint_events");
        servletProps.put("felix.webconsole.category", "Everit");
        servletProps.put("felix.webconsole.title", "Blueprint Events");
        Servlet blueprintServlet = new BlueprintServlet(blueprintListener);
        blueprintServletSR = context.registerService(Servlet.class, blueprintServlet, servletProps);

    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        blueprintServletSR.unregister();
        blueprintListenerSR.unregister();

    }
}
