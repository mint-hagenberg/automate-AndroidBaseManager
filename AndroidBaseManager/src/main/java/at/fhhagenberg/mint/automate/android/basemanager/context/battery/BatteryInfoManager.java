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

package at.fhhagenberg.mint.automate.android.basemanager.context.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import at.fh.hagenberg.mint.automate.loggingclient.androidextension.kernel.AndroidKernel;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.time.TrustedTimeManager;
import at.fhhagenberg.mint.automate.android.basemanager.context.battery.event.BatteryInfoTransmissionEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.action.EventAction;
import at.fhhagenberg.mint.automate.loggingclient.javacore.event.EventManager;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.AbstractManager;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.KernelListener;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.ManagerException;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.annotation.ExternalManager;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;

/**
 * Manager to get updates about battery status and send them to the server if deemed necessary.
 */
@ExternalManager
public class BatteryInfoManager extends AbstractManager implements KernelListener {
	public static final Id ID = new Id(BatteryInfoManager.class);

	private BatteryInfoTransmissionEvent mCurrentInfo;

	private BroadcastReceiver mBatteryBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateCurrentStatus(intent);
		}
	};

	public BatteryInfoManager() {
		addDependency(EventManager.ID);
		addDependency(TrustedTimeManager.ID);
	}

	@Override
	protected void doStart() throws ManagerException {
		super.doStart();

		final Context context = ((AndroidKernel) getKernel()).getContext();
		Intent intent = context.registerReceiver(mBatteryBroadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		updateCurrentStatus(intent);

		getKernel().addListener(this);
	}

	private void updateCurrentStatus(Intent intent) {
		if (intent == null) {
			return;
		}

		TrustedTimeManager timeService = AbstractManager.getInstance(getKernel(),
				TrustedTimeManager.class);
		long startTime = timeService.currentTimeMillis();
		int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
		int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		boolean present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
		int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
		int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
		int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
		final BatteryInfoTransmissionEvent newInfo = new BatteryInfoTransmissionEvent(startTime, health, level, plugged, present, scale, status, technology, temperature, voltage);
		if (!newInfo.equals(mCurrentInfo)) {
			sendCurrentInfo();
			mCurrentInfo = newInfo;
		}
	}

	private void sendCurrentInfo() {
		if (mCurrentInfo != null) {
			TrustedTimeManager timeService = AbstractManager.getInstance(getKernel(),
					TrustedTimeManager.class);
			long duration = timeService.currentTimeMillis() - mCurrentInfo.getStartTime();
			mCurrentInfo.setDuration(duration);

			new EventAction(getKernel(), mCurrentInfo).execute();
		}
	}

	@Override
	protected void doStop() {
		getKernel().removeListener(this);

		final Context context = ((AndroidKernel) getKernel()).getContext();
		context.unregisterReceiver(mBatteryBroadcastReceiver);

		super.doStop();
	}

	@Override
	public void startupFinished() {
	}

	@Override
	public void onPrepareShutdown() {
		sendCurrentInfo();
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
		return "Battery Info Manager";
	}
}
