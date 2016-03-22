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

import at.fhhagenberg.mint.automate.android.basemanager.appinteraction.event.AppInteractionEvent;
import at.fhhagenberg.mint.automate.android.basemanager.appinteraction.event.AppInteractionTransmissionEvent;
import at.fhhagenberg.mint.automate.android.basemanager.appinteraction.event.AppScreenVisitEvent;
import at.fhhagenberg.mint.automate.android.basemanager.appinteraction.event.KeyboardShowingEvent;
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
 * Manager that gathers app widget interaction information.
 */
@ExternalManager
public class WidgetInteractionManager extends AbstractManager implements EventListener, KernelListener {
	public static final Id ID = new Id(WidgetInteractionManager.class);

	private AppScreenVisitEvent mCurrentVisit;

	@Override
	protected void doStart() throws ManagerException {
		super.doStart();

		getKernel().addListener(this);
		new RegisterEventListenerAction(getKernel(), this, AppScreenVisitEvent.ID, KeyboardShowingEvent.ID, AppInteractionEvent.ID).execute();
	}

	@Override
	protected void doStop() {
		getKernel().removeListener(this);

		new UnregisterEventListenerAction(getKernel(), this);

		super.doStop();
	}

	@Override
	public void startupFinished() {
	}

	@Override
	public void onPrepareShutdown() {
	}

	@Override
	public void onShutdown() {
	}

	@Override
	public void handleEvent(Event event) {
		getLogger().logDebug(getLoggingSource(), "Welcome events: " + event);

		if (event.isOfType(AppScreenVisitEvent.ID)) {
			onAppScreenVisitEvent(((AppScreenVisitEvent) event));
		} else if (event.isOfType(KeyboardShowingEvent.ID)) {
			onKeyboardShowingEvent(((KeyboardShowingEvent) event));
		} else if (event.isOfType(AppInteractionEvent.ID)) {
			onAppInteractionEvent(((AppInteractionEvent) event));
		}
	}

	private void onAppScreenVisitEvent(AppScreenVisitEvent event) {
		mCurrentVisit = event;
	}

	private void onKeyboardShowingEvent(KeyboardShowingEvent event) {
		// Ignore for now
	}

	private void onAppInteractionEvent(AppInteractionEvent event) {
		getLogger().logDebug(getLoggingSource(), "Interaction " + event.getInteractionType() + " at " + event.getScreenBounds() + " vs " + event.getParentBounds());

		new EventAction(KernelBase.getKernel(), new AppInteractionTransmissionEvent(mCurrentVisit, event)).execute();
	}

	@Override
	public Id getId() {
		return ID;
	}

	@Override
	public String getName() {
		return "Widget Interaction Manager";
	}
}
