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

package at.fhhagenberg.mint.automate.android.basemanager.deviceinfo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Point;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import java.util.Calendar;
import java.util.Locale;

import at.fh.hagenberg.mint.automate.loggingclient.androidextension.kernel.AndroidKernel;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.time.TrustedTimeManager;
import at.fhhagenberg.mint.automate.android.basemanager.deviceinfo.event.DeviceInfoTransmissionEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.action.EventAction;
import at.fhhagenberg.mint.automate.loggingclient.javacore.event.EventManager;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.AbstractManager;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.KernelListener;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.ManagerException;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.annotation.ExternalManager;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;

/**
 * Manager that gathers general information about the device - basically the current session.
 */
@ExternalManager(allowsUserStatusChange = false)
public class DeviceInfoManager extends AbstractManager implements KernelListener {
	public static final Id ID = new Id(DeviceInfoManager.class);

	private long mStartTime;

	private String mDevice;
	private String mCarrierName;
	private String mAppVersionName;
	private String mOsVersion;

	private int mResolutionWidth;
	private int mResolutionHeight;

	private String mSimCountry;
	private String mLocale;
	private String mLocaleCountry;

	public DeviceInfoManager() {
		addDependency(EventManager.ID);
		addDependency(TrustedTimeManager.ID);
	}

	@Override
	protected void doStart() throws ManagerException {
		super.doStart();

		getKernel().addListener(this);
	}

	@Override
	protected void doStop() {
		getKernel().removeListener(this);

		super.doStop();
	}

	@Override
	public void startupFinished() {
		Calendar calendar = Calendar.getInstance();
		mStartTime = calendar.getTimeInMillis();

		final Context context = ((AndroidKernel) getKernel()).getContext();

		mDevice = Build.MODEL;
		mOsVersion = Build.VERSION.RELEASE;

		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(
				Context.TELEPHONY_SERVICE);

		mCarrierName = telephonyManager.getNetworkOperatorName();

		PackageInfo pInfo;
		try {
			pInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			mAppVersionName = pInfo.versionName;
		} catch (Exception e) {
			mAppVersionName = "UNDEFINED";
		}

		setResolution(context);

		mSimCountry = telephonyManager.getSimCountryIso();
		mLocale = Locale.getDefault().getLanguage();
		mLocaleCountry = Locale.getDefault().getCountry();
	}

	private void setResolution(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			setResolutionV13(display);
		} else {
			setResolutionPreV13(display);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void setResolutionV13(Display display) {
		Point size = new Point();
		display.getSize(size);
		mResolutionWidth = size.x < size.y ? size.x : size.y;
		mResolutionHeight = size.x < size.y ? size.y : size.x;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void setResolutionPreV13(Display display) {
		int w = display.getWidth();
		int h = display.getHeight();
		mResolutionWidth = w < h ? w : h;
		mResolutionHeight = w < h ? h : w;
	}

	@Override
	public void onPrepareShutdown() {
		TrustedTimeManager timeService = AbstractManager.getInstance(getKernel(),
				TrustedTimeManager.class);
		long duration = timeService.currentTimeMillis() - mStartTime;

		new EventAction(getKernel(), new DeviceInfoTransmissionEvent(timeService.currentTimeMillis(), timeService.currentTimeZoneOffsetMillis(), duration, mDevice, mCarrierName, mAppVersionName, mOsVersion, mResolutionWidth, mResolutionHeight, mSimCountry, mLocale, mLocaleCountry))
				.execute();
	}

	@Override
	public void onShutdown() {
	}

	@Override
	public Id getId() {
		return ID;
	}

	@Override
	public String getName() {
		return "Device Info Manager";
	}
}
