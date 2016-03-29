/*
 *     Copyright (C) 2016 Mobile Interactive Systems Research Group
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.fhhagenberg.mint.automate.android.basemanager.connectivity;

import java.util.Arrays;
import java.util.List;

import at.fh.hagenberg.mint.automate.loggingclient.androidextension.fileexport.FileExportHandler;
import at.fhhagenberg.mint.automate.android.basemanager.connectivity.event.NetworkInfoTransmissionEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.event.Event;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;

/**
 * File export handler implementation for the connectivity information.
 */
public class ConnectivityInfoFileExportHandler implements FileExportHandler {
	private static final String FILENAME = "networkinfo.csv";

	@Override
	public List<Id> getTransmissionEvents() {
		return Arrays.asList(NetworkInfoTransmissionEvent.ID);
	}

	@Override
	public String getFilename(Id id) {
		return FILENAME;
	}

	@Override
	public Object[] serialize(Event event) {
		NetworkInfoTransmissionEvent temp = (NetworkInfoTransmissionEvent) event;
		return new Object[]{temp.getStartTime(), temp.getDuration(), temp.getType(), temp.getSubType(), temp.isRoaming()};
	}
}
