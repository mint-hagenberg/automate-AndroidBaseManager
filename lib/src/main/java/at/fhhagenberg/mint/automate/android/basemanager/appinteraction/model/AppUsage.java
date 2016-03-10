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

package at.fhhagenberg.mint.automate.android.basemanager.appinteraction.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * One app usage will be contained in a session and have multiple states.
 */
public class AppUsage implements XMLSerializable {
	private String mPackageName;
	private String mName;
	private List<AppState> mStates;

	public AppUsage(String packageName, String name) {
		mPackageName = packageName;
		mName = name;
		mStates = new ArrayList<>();
	}

	/**
	 * Get the app package name.
	 *
	 * @return -
	 */
	public String getPackageName() {
		return mPackageName;
	}

	/**
	 * Get the name/title of the app.
	 *
	 * @return -
	 */
	public String getName() {
		return mName;
	}

	/**
	 * Get all the states the app was used in (in order!).
	 *
	 * @return -
	 */
	public List<AppState> getStates() {
		return mStates;
	}

	/**
	 * Add a new state to the end of the app usage.
	 *
	 * @param state -
	 */
	public void addState(AppState state) {
		mStates.add(state);
	}

	/**
	 * End the current state with the given end time.
	 *
	 * @param endTime -
	 */
	public void endCurrentState(long endTime) {
		if (mStates.size() > 0) {
			AppState state = mStates.get(mStates.size() - 1);
			state.setDuration(endTime - state.getStartTime());
		}
	}

	@Override
	public Element toXML(Document doc) {
		Element node = doc.createElement("appUsage");
		node.setAttribute("packageName", mPackageName);
		node.setAttribute("name", mName);

		if (mStates.size() > 0) {
			node.setAttribute("startTime", String.valueOf(mStates.get(0).getStartTime()));
		}
		for (AppState state : mStates) {
			node.appendChild(state.toXML(doc));
		}
		return node;
	}
}
