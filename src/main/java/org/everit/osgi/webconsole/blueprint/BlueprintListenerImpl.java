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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.service.blueprint.container.BlueprintEvent;
import org.osgi.service.blueprint.container.BlueprintListener;

/**
 * Custom implementation of BlueprintListener storing all the events in a {@link ConcurrentHashMap}.
 */
public class BlueprintListenerImpl implements BlueprintListener {
    /**
     * {@link ConcurrentHashMap} for storing BlueprintEvents.
     */
    private final Map<Bundle, BlueprintEvent> eventMap = new ConcurrentHashMap<Bundle, BlueprintEvent>();

    @Override
    public void blueprintEvent(final BlueprintEvent event) {
        if (event.getType() == BlueprintEvent.DESTROYED) {
            eventMap.remove(event.getBundle());
        } else {
            eventMap.put(event.getBundle(), event);
        }
    }

    public Map<Bundle, BlueprintEvent> getEventMap() {
        return eventMap;
    }
}
