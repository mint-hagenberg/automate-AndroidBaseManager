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

package at.fhhagenberg.mint.automate.android.basemanager.context.orientation.event;

import java.util.Date;

import at.fhhagenberg.mint.automate.loggingclient.javacore.event.SimpleEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;

/**
 * initiate transmission of the orienation change to the server.
 */
public class DeviceOrientationTransmissionEvent extends SimpleEvent {
	public static final Id ID = new Id(DeviceOrientationTransmissionEvent.class);

	private Date mStartTime;
	private Date mEndTime;
	private int mOrientation;

	public DeviceOrientationTransmissionEvent(Date startTime, int orientation) {
		super(ID);
		mStartTime = startTime;
		mOrientation = orientation;
	}

	public Date getStartTime() {
		return mStartTime;
	}

	public Date getEndTime() {
		return mEndTime;
	}

	public void setEndTime(Date value) {
		mEndTime = value;
	}

	public int getOrientation() {
		return mOrientation;
	}
}
