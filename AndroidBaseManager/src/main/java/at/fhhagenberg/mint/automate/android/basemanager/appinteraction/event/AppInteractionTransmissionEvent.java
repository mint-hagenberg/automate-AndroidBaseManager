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

package at.fhhagenberg.mint.automate.android.basemanager.appinteraction.event;

import at.fhhagenberg.mint.automate.loggingclient.javacore.event.SimpleEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;

/**
 * Network transmission event for the app interaction data.
 */
public class AppInteractionTransmissionEvent extends SimpleEvent {
	public static final Id ID = new Id(AppInteractionTransmissionEvent.class);

	private AppScreenVisitEvent mState;
	private AppInteractionEvent mInteraction;

	public AppInteractionTransmissionEvent(AppScreenVisitEvent state, AppInteractionEvent interaction) {
		super(ID);
		mState = state;
		mInteraction = interaction;
	}

	public AppScreenVisitEvent getState() {
		return mState;
	}

	public AppInteractionEvent getInteraction() {
		return mInteraction;
	}
}
