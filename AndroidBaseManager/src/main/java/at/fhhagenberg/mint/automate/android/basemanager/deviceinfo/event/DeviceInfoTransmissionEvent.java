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

package at.fhhagenberg.mint.automate.android.basemanager.deviceinfo.event;

import at.fhhagenberg.mint.automate.loggingclient.javacore.event.SimpleEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;

/**
 * Event to transfer device info to the server.
 */
public class DeviceInfoTransmissionEvent extends SimpleEvent {
	public static final Id ID = new Id(DeviceInfoTransmissionEvent.class);

	private final long mDate;
	private final int mTimeZoneOffset;
	private final long mDuration;
	private final String mDevice;
	private final String mOperator;
	private final String mVersionName;
	private final String mOs;
	private final int mResolutionWidth;
	private final int mResolutionHeight;
	private final String mLocation;
	private final String mLocaleLanguage;
	private final String mLocaleCountry;

	public DeviceInfoTransmissionEvent(long date, int timezoneOffset, long duration, String device, String operator, String versionName,
									   String os, int resolutionWidth, int resolutionHeight, String location, String localeLanguage,
									   String localeCountry) {
		super(ID);

		mDate = date;
		mTimeZoneOffset = timezoneOffset;
		mDuration = duration;
		mDevice = device;
		mOperator = operator;
		mVersionName = versionName;
		mOs = os;
		mResolutionWidth = resolutionWidth;
		mResolutionHeight = resolutionHeight;
		mLocation = location;
		mLocaleLanguage = localeLanguage;
		mLocaleCountry = localeCountry;
	}

	public long getDuration() {
		return mDuration;
	}

	public String getDevice() {
		return mDevice;
	}

	public String getOperator() {
		return mOperator;
	}

	public String getVersionName() {
		return mVersionName;
	}

	public String getOs() {
		return mOs;
	}

	public long getDate() {
		return mDate;
	}

	public int getTimeZoneOffset() {
		return mTimeZoneOffset;
	}

	public int getResolutionWidth() {
		return mResolutionWidth;
	}

	public int getResolutionHeight() {
		return mResolutionHeight;
	}

	public String getLocation() {
		return mLocation;
	}

	public String getLocaleLanguage() {
		return mLocaleLanguage;
	}

	public String getLocaleCountry() {
		return mLocaleCountry;
	}
}
