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

package at.fhhagenberg.mint.automate.android.basemanager.context.light.event;

import java.util.Date;

import at.fhhagenberg.mint.automate.loggingclient.javacore.event.SimpleEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;

/**
 * Event to initiate sending the light condition to the server.
 */
public class LightConditionTransmissionEvent extends SimpleEvent {
	public static final Id ID = new Id(LightConditionTransmissionEvent.class);

	/**
	 * Brightness range starts from
	 * http://www.displaymate.com/Smartphone_Brightness_ShootOut_1.htm
	 */
	private static final long[] RANGE_STARTS = {100, 500, 1000, 3000, 10000, 20000, 50000, 100000, 120000};

	/**
	 * Get the brightness classification based on the ranges above.
	 *
	 * @param illuminanceInLux -
	 * @return -
	 */
	public static long getBrightnessRange(float illuminanceInLux) {
		int i = RANGE_STARTS.length - 1;
		while (i >= 0) {
			if (illuminanceInLux > RANGE_STARTS[i]) {
				return RANGE_STARTS[i];
			}
			--i;
		}
		return 0;
	}

	private Date mStartTime;
	private Date mEndTime;
	private long mClassification;

	/**
	 * Creates a new LightSensorEvent instance with the given state.
	 */
	public LightConditionTransmissionEvent(Date startTime, long classification) {
		super(ID);

		mStartTime = startTime;
		mClassification = classification;
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

	public long getClassification() {
		return mClassification;
	}

	public long getDuration() {
		return mEndTime.getTime() - mStartTime.getTime();
	}
}
