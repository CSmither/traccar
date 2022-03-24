/*
 * Copyright 2016 - 2021 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.api.resource;

import org.traccar.Context;
import org.traccar.api.BaseResource;
import org.traccar.helper.Ternary;
import org.traccar.model.Event;
import org.traccar.model.Geofence;
import org.traccar.model.Maintenance;
import org.traccar.storage.StorageException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.stream.Collectors;

@Path("events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource extends BaseResource {

    @Path("{id}")
    @GET
    public Event get(@PathParam("id") long id) throws StorageException {
        Event event = Context.getDataManager().getObject(Event.class, id);
        if (event == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build());
        }
        Context.getPermissionsManager().checkDevice(getUserId(), event.getDeviceId());
        if (event.getGeofenceId() != 0) {
            Context.getPermissionsManager().checkPermission(Geofence.class, getUserId(), event.getGeofenceId());
        }
        if (event.getMaintenanceId() != 0) {
            Context.getPermissionsManager().checkPermission(Maintenance.class, getUserId(), event.getMaintenanceId());
        }
        return event;
    }

    @GET
    public Collection<Event> getMany(@QueryParam("acknowledged") Ternary acknowledged) throws StorageException {
        if (acknowledged.getValue().isPresent()) {
            Collection<Event> events = Context.getDataManager().getEventsByAcknowledged(acknowledged.getValue().get());
            return events.stream().filter(event -> {
                if (event == null) {
                    return false;
                }
                Context.getPermissionsManager().checkDevice(getUserId(), event.getDeviceId());
                if (event.getGeofenceId() != 0) {
                    try {
                        Context.getPermissionsManager().checkPermission(Geofence.class, getUserId(), event.getGeofenceId());
                    } catch (SecurityException secex) {
                        return false;
                    }
                }
                if (event.getMaintenanceId() != 0) {
                    try {
                        Context.getPermissionsManager().checkPermission(Maintenance.class, getUserId(), event.getMaintenanceId());
                    } catch (SecurityException secex) {
                        return false;
                    }
                }
                return true;
            }).collect(Collectors.toList());
        }
        throw new StorageException(""); // TODO: Clean this up a bit
    }

    @Path("{id}/ack")
    @POST
    public Event postAck(@PathParam("id") long id) throws StorageException {
        Event event = Context.getDataManager().getObject(Event.class, id);
        if (event == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build());
        }
        Context.getPermissionsManager().checkDevice(getUserId(), event.getDeviceId());
        if (event.getGeofenceId() != 0) {
            Context.getPermissionsManager().checkPermission(Geofence.class, getUserId(), event.getGeofenceId());
        }
        if (event.getMaintenanceId() != 0) {
            Context.getPermissionsManager().checkPermission(Maintenance.class, getUserId(), event.getMaintenanceId());
        }
        event.setAcknowledged(true);
        Context.getDataManager().updateObject(event);
        return event;
    }

}
