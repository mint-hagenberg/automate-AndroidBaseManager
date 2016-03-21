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

package at.fhhagenberg.mint.automate.android.basemanager.connectivity.event;

import at.fhhagenberg.mint.automate.loggingclient.javacore.event.SimpleEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;

/**
 * Transmission event to initiate sending network infos to the server.
 */
public class NetworkInfoTransmissionEvent extends SimpleEvent {
    public static final Id ID = new Id(NetworkInfoTransmissionEvent.class);

    private final long mStartTime;
    private final long mDuration;
    private final int mType;
    private final int mSubType;
    private final boolean mIsRoaming;

    public NetworkInfoTransmissionEvent(long startTime, long duration, int type, int subtype, boolean isRoaming) {
        super(ID);
        mStartTime = startTime;
        mDuration = duration;
        mType = type;
        mSubType = subtype;
        mIsRoaming = isRoaming;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getDuration() {
        return mDuration;
    }

    public int getType() {
        return mType;
    }

    public int getSubType() {
        return mSubType;
    }

    public boolean isRoaming() {
        return mIsRoaming;
    }
}
