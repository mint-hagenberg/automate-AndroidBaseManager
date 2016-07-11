/*
 *     Copyright (C) 2016 Research Group Mobile Interactive Systems
 *     Email: mint@fh-hagenberg.at, Website: http://mint.fh-hagenberg.at
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.fhhagenberg.mint.automate.android.basemanager.context;

import java.util.Arrays;
import java.util.List;

import at.fh.hagenberg.mint.automate.loggingclient.androidextension.fileexport.FileExportHandler;
import at.fhhagenberg.mint.automate.android.basemanager.context.battery.event.BatteryInfoTransmissionEvent;
import at.fhhagenberg.mint.automate.android.basemanager.context.light.event.LightConditionTransmissionEvent;
import at.fhhagenberg.mint.automate.android.basemanager.context.orientation.event.DeviceOrientationTransmissionEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.event.Event;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;

/**
 * File export handler implementation for the sensro context events.
 */
public class SensorContextFileExportHandler implements FileExportHandler {
    private static final String FILENAME_BATTERY_INFO = "batteryinfo.csv";
    private static final String FILENAME_LIGHT_CONDITION = "lightcondition.csv";
    private static final String FILENAME_DEVICE_ORIENTATION = "deviceorientation.csv";

    private static final String[] HEADER_BATTERY_INFO = {"startTime", "duration", "health", "level", "plugged", "isPresent", "scale", "status", "technology", "temperature", "voltage"};
    private static final String[] HEADER_LIGHT_CONDITION = {"startTime", "endTime", "classification"};
    private static final String[] HEADER_DEVICE_ORIENTATION = {"startTime", "endTime", "orientation"};

    @Override
    public List<Id> getTransmissionEvents() {
        return Arrays.asList(BatteryInfoTransmissionEvent.ID, LightConditionTransmissionEvent.ID, DeviceOrientationTransmissionEvent.ID);
    }

    @Override
    public List<String> getAllFilenames() {
        return Arrays.asList(FILENAME_BATTERY_INFO, FILENAME_LIGHT_CONDITION, FILENAME_DEVICE_ORIENTATION);
    }

    @Override
    public String getFilename(Id id) {
        if (id.equals(BatteryInfoTransmissionEvent.ID)) {
            return FILENAME_BATTERY_INFO;
        } else if (id.equals(LightConditionTransmissionEvent.ID)) {
            return FILENAME_LIGHT_CONDITION;
        } else if (id.equals(DeviceOrientationTransmissionEvent.ID)) {
            return FILENAME_DEVICE_ORIENTATION;
        } else {
            return null;
        }
    }

    @Override
    public String[] getFileHeader(Id id) {
        if (id.equals(BatteryInfoTransmissionEvent.ID)) {
            return HEADER_BATTERY_INFO;
        } else if (id.equals(LightConditionTransmissionEvent.ID)) {
            return HEADER_LIGHT_CONDITION;
        } else if (id.equals(DeviceOrientationTransmissionEvent.ID)) {
            return HEADER_DEVICE_ORIENTATION;
        } else {
            return null;
        }
    }

    @Override
    public Object[] serialize(Event event) {
        if (event.isOfType(BatteryInfoTransmissionEvent.ID)) {
            BatteryInfoTransmissionEvent temp = (BatteryInfoTransmissionEvent) event;
            return new Object[]{temp.getStartTime(), temp.getDuration(), temp.getHealth(),
                    temp.getLevel(), temp.getPlugged(), temp.isPresent(), temp.getScale(),
                    temp.getStatus(), temp.getTechnology(), temp.getTemperature(), temp.getVoltage()};
        } else if (event.isOfType(LightConditionTransmissionEvent.ID)) {
            LightConditionTransmissionEvent temp = (LightConditionTransmissionEvent) event;
            return new Object[]{temp.getStartTime().getTime(), temp.getEndTime().getTime(),
                    temp.getClassification()};
        } else if (event.isOfType(DeviceOrientationTransmissionEvent.ID)) {
            DeviceOrientationTransmissionEvent temp = (DeviceOrientationTransmissionEvent) event;
            return new Object[]{temp.getStartTime().getTime(), temp.getEndTime().getTime(), temp.getOrientation()};
        } else {
            return null;
        }
    }
}
