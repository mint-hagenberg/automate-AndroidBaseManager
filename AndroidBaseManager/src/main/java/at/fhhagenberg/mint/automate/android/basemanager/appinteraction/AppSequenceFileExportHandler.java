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

import java.util.Arrays;
import java.util.List;

import at.fh.hagenberg.mint.automate.loggingclient.androidextension.fileexport.FileExportHandler;
import at.fhhagenberg.mint.automate.android.basemanager.appinteraction.event.AppInteractionTransmissionEvent;
import at.fhhagenberg.mint.automate.android.basemanager.appinteraction.event.AppSequenceTransmissionEvent;
import at.fhhagenberg.mint.automate.loggingclient.javacore.event.Event;
import at.fhhagenberg.mint.automate.loggingclient.javacore.name.Id;

/**
 * File export handler implementation for the app sequence and interaction events.
 */
public class AppSequenceFileExportHandler implements FileExportHandler {
	private static final String FILENAME_APPSEQUENCE = "appsequence.csv";
	private static final String FILENAME_INTERACTION = "appinteraction.csv";

	private static final String[] HEADER_APPSEQUENCE = {"timestamp", "xml"};
	private static final String[] HEADER_INTERACTION = {"packageName", "className", "title", "eventTime", "orientation", "interactionType", "interactionClassName", "interactionText", "contentDescription", "viewIdResourceName", "interactionEventTime", "screenBoundsLeft", "screenBoundsRight", "screenBoundsTop", "screenBoundsBottom", "parentBoundsLeft", "parentBoundsRight", "parentBoundsTop", "parentBoundsBottom"};

	@Override
	public List<Id> getTransmissionEvents() {
		return Arrays.asList(AppSequenceTransmissionEvent.ID, AppInteractionTransmissionEvent.ID);
	}

	@Override
	public String getFilename(Id id) {
		if (id.equals(AppSequenceTransmissionEvent.ID)) {
			return FILENAME_APPSEQUENCE;
		} else if (id.equals(AppInteractionTransmissionEvent.ID)) {
			return FILENAME_INTERACTION;
		} else {
			return null;
		}
	}

	@Override
	public String[] getFileHeader(Id id) {
		if (id.equals(AppSequenceTransmissionEvent.ID)) {
			return HEADER_APPSEQUENCE;
		} else if (id.equals(AppInteractionTransmissionEvent.ID)) {
			return HEADER_INTERACTION;
		} else {
			return null;
		}
	}

	@Override
	public Object[] serialize(Event event) {
		if (event.isOfType(AppSequenceTransmissionEvent.ID)) {
			AppSequenceTransmissionEvent temp = (AppSequenceTransmissionEvent) event;
			return new Object[]{temp.getTimestamp(), temp.getXml()};
		} else if (event.isOfType(AppInteractionTransmissionEvent.ID)) {
			AppInteractionTransmissionEvent temp = (AppInteractionTransmissionEvent) event;
			return new Object[]{temp.getState() == null ? null : temp.getState().getPackageName(),
					temp.getState() == null ? null : temp.getState().getClassName(),
					temp.getState() == null ? null : temp.getState().getTitle(),
					temp.getState() == null ? null : temp.getState().getEventTime(),
					temp.getState() == null ? null : temp.getState().getOrientation(),
					temp.getInteraction() == null || temp.getInteraction().getInteractionType() == null ? null : temp.getInteraction().getInteractionType().toString(),
					temp.getInteraction() == null ? null : temp.getInteraction().getClassName(),
					temp.getInteraction() == null ? null : temp.getInteraction().getText(),
					temp.getInteraction() == null ? null : temp.getInteraction().getContentDescription(),
					temp.getInteraction() == null ? null : temp.getInteraction().getViewIdResourceName(),
					temp.getInteraction() == null ? null : temp.getInteraction().getEventTime(),
					temp.getInteraction() == null || temp.getInteraction().getScreenBounds() == null ? null : temp.getInteraction().getScreenBounds().left,
					temp.getInteraction() == null || temp.getInteraction().getScreenBounds() == null ? null : temp.getInteraction().getScreenBounds().right,
					temp.getInteraction() == null || temp.getInteraction().getScreenBounds() == null ? null : temp.getInteraction().getScreenBounds().top,
					temp.getInteraction() == null || temp.getInteraction().getScreenBounds() == null ? null : temp.getInteraction().getScreenBounds().bottom,
					temp.getInteraction() == null || temp.getInteraction().getParentBounds() == null ? null : temp.getInteraction().getParentBounds().left,
					temp.getInteraction() == null || temp.getInteraction().getParentBounds() == null ? null : temp.getInteraction().getParentBounds().right,
					temp.getInteraction() == null || temp.getInteraction().getParentBounds() == null ? null : temp.getInteraction().getParentBounds().top,
					temp.getInteraction() == null || temp.getInteraction().getParentBounds() == null ? null : temp.getInteraction().getParentBounds().bottom};
		} else {
			return null;
		}
	}
}
