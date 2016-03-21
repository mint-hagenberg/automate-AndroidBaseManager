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

package at.fhhagenberg.mint.automate.android.basemanager.context.orientation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Date;

import at.fh.hagenberg.mint.automate.loggingclient.androidextension.kernel.AndroidKernel;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.time.TrustedTimeManager;
import at.fhhagenberg.mint.automate.android.basemanager.context.orientation.event.DeviceOrientationTransmissionEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.action.EventAction;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.AbstractManager;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.KernelBase;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.KernelListener;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.ManagerException;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;

/**
 * React to orientation changes and initiate sending them to the network.
 */
public class OrientationChangeManager extends AbstractManager implements KernelListener {
	public static final Id ID = new Id(OrientationChangeManager.class);

	private BroadcastReceiver mConfigChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_CONFIGURATION_CHANGED)) {
				onConfigChanged();
			}
		}
	};

	private DeviceOrientationTransmissionEvent mLastEvent;

	@Override
	protected void doStart() throws ManagerException {
		super.doStart();

		getKernel().addListener(this);
		((AndroidKernel) getKernel()).getContext().registerReceiver(mConfigChangeReceiver, new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED));
	}

	@Override
	protected void doStop() {
		getKernel().removeListener(this);
		((AndroidKernel) getKernel()).getContext().unregisterReceiver(mConfigChangeReceiver);

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
			mLastEvent.setEndTime(new Date(currentTimeMillis));
			new EventAction(KernelBase.getKernel(), mLastEvent).execute();
		}
	}

	@Override
	public void onShutdown() {
	}

	private void onConfigChanged() {
		try {
			long currentTimeMillis = AbstractManager.getInstance(getKernel(), TrustedTimeManager.class).currentTimeMillis();

			int orientation = ((AndroidKernel) getKernel()).getContext().getResources().getConfiguration().orientation;
			if (mLastEvent == null || orientation != mLastEvent.getOrientation()) {
				sendLastEvent(currentTimeMillis);
				mLastEvent = new DeviceOrientationTransmissionEvent(new Date(currentTimeMillis), orientation);
				getLogger().logDebug(getLoggingSource(), "The current orientation is being set: " + mLastEvent.getOrientation());
			}
		} catch (IllegalStateException ex) {
		}
	}

	@Override
	public Id getId() {
		return ID;
	}
}
