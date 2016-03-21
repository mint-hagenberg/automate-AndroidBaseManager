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

package at.fhhagenberg.mint.automate.android.basemanager.context.light;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Date;

import at.fh.hagenberg.mint.automate.loggingclient.androidextension.kernel.AndroidKernel;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.time.TrustedTimeManager;
import at.fhhagenberg.mint.automate.android.basemanager.context.light.event.LightConditionTransmissionEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.action.EventAction;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.AbstractManager;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.KernelBase;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.KernelListener;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.ManagerException;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;

/**
 * Check the light sensor data and send updates when the classification changes to avoid sending too many updates to the server.
 */
public class LightConditionManager extends AbstractManager implements SensorEventListener, KernelListener {
	public static final Id ID = new Id(LightConditionManager.class);

	private SensorManager mSensorManager;
	private Sensor mLightSensor;

	private LightConditionTransmissionEvent mLastEvent;

	@Override
	protected void doStart() throws ManagerException {
		super.doStart();

		getKernel().addListener(this);

		Context context = ((AndroidKernel) getKernel()).getContext();
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		if (mLightSensor != null) {
			mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			getLogger().logDebug(getLoggingSource(), "There's no light sensor in this device!");
			pause();
		}
	}

	@Override
	protected void doPause() {
		try {
			getKernel().removeListener(this);
		} catch (Exception ex) {
		}
		if (mLightSensor != null) {
			mSensorManager.unregisterListener(this);
		}

		try {
			long currentTimeMillis = AbstractManager.getInstance(getKernel(), TrustedTimeManager.class).currentTimeMillis();
			sendLastEvent(currentTimeMillis);
			mLastEvent = null;
		} catch (IllegalStateException ex) {
		}
		super.doPause();
	}

	@Override
	protected void doResume() {
		super.doResume();

		getKernel().addListener(this);
		if (mLightSensor != null) {
			mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			getLogger().logDebug(getLoggingSource(), "There's no light sensor in this device!");
			pause();
		}
	}

	@Override
	protected void doStop() {
		try {
			getKernel().removeListener(this);
		} catch (Exception ex) {
		}
		if (mLightSensor != null) {
			mSensorManager.unregisterListener(this);
		}

		super.doStop();
	}

	@Override
	public void startupFinished() {
	}

	@Override
	public void onPrepareShutdown() {
		try {
			long currentTimeMillis = AbstractManager.getInstance(getKernel(), TrustedTimeManager.class).currentTimeMillis();
			sendLastEvent(currentTimeMillis);
		} catch (IllegalStateException ex) {
		}
	}

	private void sendLastEvent(long currentTimeMillis) {
		if (mLastEvent != null) {
			getLogger().logDebug(getLoggingSource(), "New light condition: " + mLastEvent.getClassification());
			mLastEvent.setEndTime(new Date(currentTimeMillis));
			new EventAction(KernelBase.getKernel(), mLastEvent).execute();
		}
	}

	@Override
	public void onShutdown() {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		try {
			long currentTimeMillis = AbstractManager.getInstance(getKernel(), TrustedTimeManager.class).currentTimeMillis();

			float currentReading = event.values[0];
			long classification = LightConditionTransmissionEvent.getBrightnessRange(currentReading);
			if (mLastEvent == null || mLastEvent.getClassification() != classification) {
				sendLastEvent(currentTimeMillis);
				mLastEvent = new LightConditionTransmissionEvent(new Date(currentTimeMillis), classification);
			}
		} catch (IllegalStateException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public Id getId() {
		return ID;
	}
}
