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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.service.blueprint.container.BlueprintEvent;

public class BlueprintServlet extends HttpServlet {
    private static final String BLUEPRINT_EVENT_CREATED = "Created";
    private static final String BLUEPRINT_EVENT_CREATING = "Creating";
    /**
     * String representation of DESTROYED eventType.
     */
    private static final String BLUEPRINT_EVENT_DESTORYED = "DESTORYED";
    /**
     * String representation of DESTROYING eventType.
     */
    private static final String BLUEPRINT_EVENT_DESTROYING = "DESTROYING";
    /**
     * String representation of FAILURE eventType.
     */
    private static final String BLUEPRINT_EVENT_FAILURE = "FAILURE";
    /**
     * String representation of GRACE_PERIOD eventType.
     */
    private static final String BLUEPRINT_EVENT_GRACE_PERIOD = "GRACE_PERIOD";
    /**
     * String representation of WAITING eventType.
     */
    private static final String BLUEPRINT_EVENT_WAITING = "WAITING";
    /**
     * CSS className for table even rows.
     */
    private static final String EVEN_CSS_CLASS = "even";
    /**
     * HTML table close TAG.
     */
    private static final CharSequence HTML_TABLE_CLOSE = "</table>";
    /**
     * CSS className for table odd rows.
     */
    private static final String ODD_CSS_CLASS = "odd";
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * {@link BlueprintListenerImpl}.
     */
    private final BlueprintListenerImpl blueprintListener;
    /**
     * HTML table element.
     */
    private final String HTML_TABLE_WITH_CSS = "<table class=\"nicetable noauto ui-widget\">";

    public BlueprintServlet(final BlueprintListenerImpl blueprintListener) {
        super();
        this.blueprintListener = blueprintListener;
    }

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        String appRoot = (String) request.getAttribute("felix.webconsole.appRoot");
        try {
            PrintWriter writer = response.getWriter();
            writer.append(getTable());
            writer.append(getHeader());

            List<BlueprintEvent> events = new ArrayList<BlueprintEvent>(blueprintListener.getEventMap().values());
            Collections.sort(events, new Comparator<BlueprintEvent>() {

                @Override
                public int compare(final BlueprintEvent o1, final BlueprintEvent o2) {
                    long result = o1.getTimestamp() - o2.getTimestamp();
                    if (result < 0) {
                        return 1;
                    } else if (result > 0) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });

            int i = 0;
            for (BlueprintEvent event : events) {
                i++;
                if ((i % 2) == 0) {
                    writer.append(getFormattedEvent(event, ODD_CSS_CLASS, appRoot));
                } else {
                    writer.append(getFormattedEvent(event, EVEN_CSS_CLASS, appRoot));
                }
            }
            writer.append(HTML_TABLE_CLOSE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCategory() {
        return "Everit";
    }

    private String getDependencies(final String[] dependencies) {
        if ((dependencies == null) || (dependencies.length == 0)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("<ul>");
        for (String s : dependencies) {
            sb.append("<li>" + s + "</li>");
        }
        sb.append("</ul>");
        return sb.toString();
    }

    private String getEventTypeString(final int type) {
        String eventType = "";
        switch (type) {
        case BlueprintEvent.CREATED:
            eventType = BLUEPRINT_EVENT_CREATED;
            break;
        case BlueprintEvent.CREATING:
            eventType = BLUEPRINT_EVENT_CREATING;
            break;
        case BlueprintEvent.DESTROYED:
            eventType = BLUEPRINT_EVENT_DESTORYED;
            break;
        case BlueprintEvent.DESTROYING:
            eventType = BLUEPRINT_EVENT_DESTROYING;
            break;
        case BlueprintEvent.FAILURE:
            eventType = BLUEPRINT_EVENT_FAILURE;
            break;
        case BlueprintEvent.GRACE_PERIOD:
            eventType = BLUEPRINT_EVENT_GRACE_PERIOD;
            break;
        case BlueprintEvent.WAITING:
            eventType = BLUEPRINT_EVENT_WAITING;
            break;
        }
        return eventType;
    }

    private String getFormattedEvent(final BlueprintEvent event, final String trClass, final String approot) {
        String eventType = getEventTypeString(event.getType());
        String timeStamp = getTimeStamp(event.getTimestamp());
        Bundle bundle = event.getBundle();
        String stackTrace = "";
        if (event.getCause() != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter pw = new PrintWriter(stringWriter);
            event.getCause().printStackTrace(pw);
            stackTrace = "<pre>" + stringWriter.toString() + "</pre>";
        }
        String dependencies = getDependencies(event.getDependencies());
        return "<tr class=\"ui-state-default " + trClass + "\" >"
                + "<td>" + timeStamp + "</td>"
                + "<td><a href=\"" + approot + "/bundles/" + bundle.getBundleId() + "\">"
                + bundle.getSymbolicName() + ":" + bundle.getVersion() + " (" + bundle.getBundleId() + ")</a></td>"
                + "<td>" + eventType + "</td>"
                + "<td>" + stackTrace + "</td>"
                + "<td>" + dependencies + "</td>"
                + "</tr>";
    }

    private String getHeader() {
        return "<p class=\"statline ui-state-highlight\">Blueprint events</p>"
                + "<thead><tr>"
                + "<th>Time</th>"
                + "<th>Bundle</th>"
                + "<th>Event type</th>"
                + "<th>Cause</th>"
                + "<th>Missing dependencies</th>"
                + "</tr></thead>";
    }

    private String getTable() {
        return HTML_TABLE_WITH_CSS;
    }

    private String getTimeStamp(final long timestamp) {
        return new Date(timestamp).toString();
    }
}
