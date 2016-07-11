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

package at.fhhagenberg.mint.automate.android.basemanager.appinteraction.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * An app state that is basically a window/activity/fragment, depending on how a app developer defines a state.
 */
public class AppState implements XMLSerializable {
	private String mClass;
	private String mName;
	private long mStartTime;
	private long mDuration;
	private long mInteractionCount;
	private int mOrientation;

	public AppState(String clazz, String name, long startTime, int orientation) {
		mClass = clazz;
		mName = name;
		mStartTime = startTime;
		mOrientation = orientation;
	}

	/**
	 * Get the state class name.
	 *
	 * @return -
	 */
	public String getClassName() {
		return mClass;
	}

	/**
	 * Get the name/title of the state.
	 *
	 * @return -
	 */
	public String getName() {
		return mName;
	}

	/**
	 * Get the start time of the state.
	 *
	 * @return -
	 */
	public long getStartTime() {
		return mStartTime;
	}

	/**
	 * Get the duration for the state visit.
	 *
	 * @return -
	 */
	public long getDuration() {
		return mDuration;
	}

	/**
	 * Get the number of interactions on this state.
	 *
	 * @return -
	 */
	public long getInteractionCount() {
		return mInteractionCount;
	}

	/**
	 * Set the state duration.
	 *
	 * @param duration -
	 */
	public void setDuration(long duration) {
		mDuration = duration;
	}

	/**
	 * Set the interaction counter.
	 *
	 * @param interactionCount -
	 */
	public void setInteractionCount(long interactionCount) {
		mInteractionCount = interactionCount;
	}

	/**
	 * Increment the interaction counter by one.
	 */
	public void incInteractionCount() {
		++mInteractionCount;
	}

	@Override
	public Element toXML(Document doc) {
		Element node = doc.createElement("state");
		node.setAttribute("name", mName);
		node.setAttribute("className", mClass);
		node.setAttribute("duration", String.valueOf(mDuration));
		node.setAttribute("interactionCount", String.valueOf(mInteractionCount));
		node.setAttribute("orientation", String.valueOf(mOrientation));
		return node;
	}
}
