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

package at.fhhagenberg.mint.automate.android.basemanager.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import at.fh.hagenberg.mint.automate.loggingclient.androidextension.kernel.AndroidKernel;
import at.fh.hagenberg.mint.automate.loggingclient.androidextension.time.TrustedTimeManager;
import at.fhhagenberg.mint.automate.android.basemanager.connectivity.event.NetworkInfoTransmissionEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.action.EventAction;
import at.fhhagenberg.mint.automate.loggingclient.javacore.event.EventManager;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.AbstractManager;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.KernelListener;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.ManagerException;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;

/**
 * Gathers information about the network information.
 */
public class NetworkInfoManager extends AbstractManager implements KernelListener {
	public static final Id ID = new Id(NetworkInfoManager.class);

	private long mStartTime;
	private NetworkInfo mCurrentNetworkInfo;

	private BroadcastReceiver mNetworkBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo networkInfo = manager.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				updateCurrentNetwork(networkInfo);
			}
		}
	};

	public NetworkInfoManager() {
		addDependency(EventManager.ID);
		addDependency(TrustedTimeManager.ID);
	}

	@Override
	protected void doStart() throws ManagerException {
		super.doStart();

		final Context context = ((AndroidKernel) getKernel()).getContext();
		final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		context.registerReceiver(mNetworkBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		final NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			updateCurrentNetwork(networkInfo);
		}

		getKernel().addListener(this);
	}

	private void updateCurrentNetwork(NetworkInfo info) {
		if (mCurrentNetworkInfo != null && info != null && mCurrentNetworkInfo.getType() == info.getType() && mCurrentNetworkInfo.getSubtype() == info.getSubtype() && mCurrentNetworkInfo.isRoaming() == info.isRoaming()) {
			return;
		}

		sendCurrentNetwork();

		TrustedTimeManager timeService = AbstractManager.getInstance(getKernel(), TrustedTimeManager.class);
		mStartTime = timeService.currentTimeMillis();
		mCurrentNetworkInfo = info;
	}

	private void sendCurrentNetwork() {
		if (mCurrentNetworkInfo != null) {
			TrustedTimeManager timeService = AbstractManager.getInstance(getKernel(), TrustedTimeManager.class);
			long duration = timeService.currentTimeMillis() - mStartTime;

			new EventAction(getKernel(), new NetworkInfoTransmissionEvent(mStartTime, duration, mCurrentNetworkInfo.getType(), mCurrentNetworkInfo.getSubtype(), mCurrentNetworkInfo.isRoaming())).execute();
		}
	}

	@Override
	protected void doStop() {
		getKernel().removeListener(this);

		final Context context = ((AndroidKernel) getKernel()).getContext();
		context.unregisterReceiver(mNetworkBroadcastReceiver);

		super.doStop();
	}

	@Override
	public void startupFinished() {
	}

	@Override
	public void onPrepareShutdown() {
		sendCurrentNetwork();
	}

	@Override
	public void onShutdown() {
	}

	@Override
	public Id getId() {
		return ID;
	}
}
