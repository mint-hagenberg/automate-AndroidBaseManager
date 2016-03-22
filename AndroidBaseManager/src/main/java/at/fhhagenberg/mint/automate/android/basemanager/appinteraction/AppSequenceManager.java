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

package at.fhhagenberg.mint.automate.android.basemanager.appinteraction;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import at.fh.hagenberg.mint.automate.loggingclient.androidextension.kernel.AndroidKernel;
import at.fhhagenberg.mint.automate.android.basemanager.appinteraction.event.AppInteractionEvent;
import at.fhhagenberg.mint.automate.android.basemanager.appinteraction.event.AppScreenVisitEvent;
import at.fhhagenberg.mint.automate.android.basemanager.appinteraction.event.AppSequenceTransmissionEvent;
import at.fhhagenberg.mint.automate.android.basemanager.appinteraction.model.AppState;
import at.fhhagenberg.mint.automate.android.basemanager.appinteraction.model.AppUsage;
import at.fhhagenberg.mint.automate.android.basemanager.appinteraction.model.DeviceSession;
import at.fhhagenberg.mint.automate.loggingclient.javacore.action.EventAction;
import at.fhhagenberg.mint.automate.loggingclient.javacore.action.RegisterEventListenerAction;
import at.fhhagenberg.mint.automate.loggingclient.javacore.action.UnregisterEventListenerAction;
import at.fhhagenberg.mint.automate.loggingclient.javacore.event.Event;
import at.fhhagenberg.mint.automate.loggingclient.javacore.event.EventListener;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.AbstractManager;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.KernelBase;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.KernelListener;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.ManagerException;
import at.fhhagenberg.mint.automate.loggingclient.javacore.kernel.annotation.ExternalManager;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;

/**
 * Records the app sequence for a session. Includes the apps flow and the interaction count in the states of the app.
 */
@ExternalManager
public class AppSequenceManager extends AbstractManager implements EventListener, KernelListener {
	public static final Id ID = new Id(AppSequenceManager.class);

	private DeviceSession mCurrentSession;

	@Override
	protected void doStart() throws ManagerException {
		super.doStart();

		getKernel().addListener(this);
		new RegisterEventListenerAction(getKernel(), this, AppScreenVisitEvent.ID, AppInteractionEvent.ID).execute();
	}

	@Override
	protected void doStop() {
		getKernel().removeListener(this);

		new UnregisterEventListenerAction(getKernel(), this);

		super.doStop();
	}

	@Override
	public void startupFinished() {
		finishSession();
		mCurrentSession = new DeviceSession();
	}

	@Override
	public void onPrepareShutdown() {
		finishSession();
	}

	private void finishSession() {
		List<AppUsage> apps = mCurrentSession == null ? null : mCurrentSession.getApps();
		if (mCurrentSession != null && apps != null && apps.size() > 0) {
			getLastAppUsage().endCurrentState(new Date().getTime());
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			Document doc;
			try {
				docBuilder = docFactory.newDocumentBuilder();
				doc = docBuilder.newDocument();

				Element rootElement = mCurrentSession.toXML(doc);
				doc.appendChild(rootElement);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource src = new DOMSource(doc);
				StringWriter writer = new StringWriter();
				StreamResult target = new StreamResult(writer);
				transformer.transform(src, target);

				String xml = writer.toString();
				getLogger().logDebug(getLoggingSource(), xml);

				List<AppState> states = apps.get(apps.size() - 1).getStates();
				if (states != null && states.size() > 0) {
					new EventAction(KernelBase.getKernel(), new AppSequenceTransmissionEvent(xml, states.get(states.size() - 1).getStartTime())).execute();
				}

				mCurrentSession = null;
			} catch (Exception ex) {
				getLogger().logWarning(getLoggingSource(), "Error creating workflow XML: " + ex.getMessage());
			}
		}
	}

	@Override
	public void onShutdown() {
	}

	@Override
	public void handleEvent(Event event) {
		getLogger().logDebug(getLoggingSource(), "Welcome events: " + event);

		if (event.isOfType(AppScreenVisitEvent.ID)) {
			onAppScreenVisitEvent(((AppScreenVisitEvent) event));
		} else if (event.isOfType(AppInteractionEvent.ID)) {
			onAppInteractionEvent(((AppInteractionEvent) event));
		}
	}

	private void onAppScreenVisitEvent(AppScreenVisitEvent event) {
		if (hasRunningApp() && isNewApp(event)) {
			endAppState(event.getEventTime());
		}
		if (isNewApp(event)) {
			createApp(event);
		}

		AppUsage usage = getLastAppUsage();
		usage.endCurrentState(event.getEventTime());
		usage.addState(new AppState(event.getClassName(), event.getTitle(), event.getEventTime(), event.getOrientation()));
	}

	private boolean hasRunningApp() {
		return mCurrentSession != null && mCurrentSession.getApps().size() > 0;
	}

	private void endAppState(long endTime) {
		AppUsage usage = getLastAppUsage();
		usage.endCurrentState(endTime);
	}

	private AppUsage getLastAppUsage() {
		return hasRunningApp() ? mCurrentSession.getApps().get(mCurrentSession.getApps().size() - 1) : null;
	}

	private boolean isNewApp(AppScreenVisitEvent event) {
		return getLastAppUsage() == null || !getLastAppUsage().getPackageName().equals(event.getPackageName());
	}

	private void createApp(AppScreenVisitEvent event) {
		if (hasRunningApp()) {
			incrementCurrentStateInteractionCount();
		}

		mCurrentSession.addApp(new AppUsage(event.getPackageName(), getAppname(event.getPackageName())));
	}

	private void incrementCurrentStateInteractionCount() {
		AppUsage usage = getLastAppUsage();
		if (usage.getStates().size() > 0) {
			usage.getStates().get(usage.getStates().size() - 1).incInteractionCount();
		}
	}

	private String getAppname(String packageName) {
		try {
			PackageManager packageManager = ((AndroidKernel) getKernel()).getContext().getPackageManager();
			ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
			return packageManager.getApplicationLabel(applicationInfo).toString();
		} catch (Exception e) {
			return null;
		}
	}

	private void onAppInteractionEvent(AppInteractionEvent event) {
		if (!hasRunningApp()) {
			// This should never happen
			return;
		}

		incrementCurrentStateInteractionCount();
	}

	@Override
	public Id getId() {
		return ID;
	}

	@Override
	public String getName() {
		return "App Sequence Manager";
	}
}
