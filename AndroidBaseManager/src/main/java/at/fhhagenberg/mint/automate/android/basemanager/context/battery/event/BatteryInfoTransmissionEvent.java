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

package at.fhhagenberg.mint.automate.android.basemanager.context.battery.event;

import at.fhhagenberg.mint.automate.loggingclient.javacore.event.SimpleEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;

/**
 * Initiate sending the battery info to the server.
 */
public class BatteryInfoTransmissionEvent extends SimpleEvent {
	public static final Id ID = new Id(BatteryInfoTransmissionEvent.class);

	private final long mStartTime;
	private long mDuration;
	private final int mHealth;
	private final int mLevel;
	private final int mPlugged;
	private final boolean mPresent;
	private final int mScale;
	private final int mStatus;
	private final String mTechnology;
	private final int mTemperature;
	private final int mVoltage;

	public BatteryInfoTransmissionEvent(long startTime, int health, int level, int plugged, boolean present, int scale, int status, String technology, int temperature, int voltage) {
		super(ID);
		mStartTime = startTime;
		mHealth = health;
		mLevel = level;
		mPlugged = plugged;
		mPresent = present;
		mScale = scale;
		mStatus = status;
		mTechnology = technology;
		mTemperature = temperature;
		mVoltage = voltage;
	}

	public long getStartTime() {
		return mStartTime;
	}

	public long getDuration() {
		return mDuration;
	}

	public void setDuration(long duration) {
		mDuration = duration;
	}

	public int getHealth() {
		return mHealth;
	}

	public int getLevel() {
		return mLevel;
	}

	public int getPlugged() {
		return mPlugged;
	}

	public boolean isPresent() {
		return mPresent;
	}

	public int getScale() {
		return mScale;
	}

	public int getStatus() {
		return mStatus;
	}

	public String getTechnology() {
		return mTechnology;
	}

	public int getTemperature() {
		return mTemperature;
	}

	public int getVoltage() {
		return mVoltage;
	}

	@Override
	public String toString() {
		return "BatteryInfoTransmissionEvent [startTime=" + mStartTime + "; duration=" + mDuration + "; health=" + mHealth + "; level=" + mLevel + "; plugged=" + mPlugged + "; present=" + mPresent + "; scale=" + mScale + "; technology=" + mTechnology + "; temperature=" + mTemperature + "; voltage=" + mVoltage + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || !(o instanceof BatteryInfoTransmissionEvent)) {
			return false;
		}
		BatteryInfoTransmissionEvent e = (BatteryInfoTransmissionEvent) o;
		// temperature and voltage were left out because they'll probably change more often and we're not interested in all those changes!
		return mHealth == e.mHealth && mLevel == e.mLevel && mPlugged == e.mPlugged && mPresent == e.mPresent && mScale == e.mScale && mStatus == e.mStatus && ((mTechnology == null && e.mTechnology == null) || (mTechnology != null && e.mTechnology != null && mTechnology.equals(e.mTechnology)));
	}
}
