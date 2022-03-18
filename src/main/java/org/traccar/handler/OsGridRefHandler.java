/*
 * Copyright 2015 Amila Silva
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
package org.traccar.handler;

import io.netty.channel.ChannelHandler;
import org.traccar.BaseDataHandler;
import org.traccar.config.Config;
import org.traccar.config.Keys;
import org.traccar.database.IdentityManager;
import org.traccar.model.Position;
import uk.gov.dstl.geo.osgb.Constants;
import uk.gov.dstl.geo.osgb.EastingNorthingConversion;
import uk.gov.dstl.geo.osgb.NationalGrid;

@ChannelHandler.Sharable
public class OsGridRefHandler extends BaseDataHandler {

    private final IdentityManager identityManager;

    private final boolean filter;
    private final int coordinatesMinError;
    private final int coordinatesMaxError;

    public OsGridRefHandler(Config config, IdentityManager identityManager) {
        this.identityManager = identityManager;
        this.filter = config.getBoolean(Keys.COORDINATES_FILTER);
        this.coordinatesMinError = config.getInteger(Keys.COORDINATES_MIN_ERROR);
        this.coordinatesMaxError = config.getInteger(Keys.COORDINATES_MAX_ERROR);
    }

    @Override
    protected Position handlePosition(Position position) {
        double[] eastingsNorthings = EastingNorthingConversion.fromLatLon(
                new double[]{position.getLatitude(), position.getLongitude()},
                Constants.ELLIPSOID_AIRY1830_MAJORAXIS,
                Constants.ELLIPSOID_AIRY1830_MINORAXIS,
                Constants.NATIONALGRID_N0,
                Constants.NATIONALGRID_E0,
                Constants.NATIONALGRID_F0,
                Constants.NATIONALGRID_LAT0,
                Constants.NATIONALGRID_LON0);
        position.set(Position.KEY_OS_GRID_REFERENCE, NationalGrid.toNationalGrid(eastingsNorthings).orElse(""));
        return position;
    }

}
