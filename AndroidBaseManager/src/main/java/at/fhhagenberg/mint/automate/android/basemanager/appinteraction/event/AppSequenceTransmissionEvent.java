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

package at.fhhagenberg.mint.automate.android.basemanager.appinteraction.event;

import at.fhhagenberg.mint.automate.loggingclient.javacore.event.SimpleEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;

/**
 * Transmission event for app sequence XMLs.
 */
public class AppSequenceTransmissionEvent extends SimpleEvent {
	public static final Id ID = new Id(AppSequenceTransmissionEvent.class);

	private final String mXml;
	private final long mTimestamp;

	public AppSequenceTransmissionEvent(String xml, long timestamp) {
		super(ID);

		mTimestamp = timestamp;
		mXml = xml;
	}

	public String getXml() {
		return mXml;
	}

	public long getTimestamp() {
		return mTimestamp;
	}
}
