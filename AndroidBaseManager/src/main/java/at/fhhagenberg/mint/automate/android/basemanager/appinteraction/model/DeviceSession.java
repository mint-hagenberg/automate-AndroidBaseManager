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
 * A device usage session from lockscreen to lockscreen.
 */
public class DeviceSession implements XMLSerializable {
	private List<AppUsage> mApps;

	public DeviceSession() {
		mApps = new ArrayList<>();
	}

	/**
	 * Get the apps.
	 *
	 * @return -
	 */
	public List<AppUsage> getApps() {
		return mApps;
	}

	/**
	 * Add an app usage.
	 *
	 * @param app -
	 */
	public void addApp(AppUsage app) {
		mApps.add(app);
	}

	@Override
	public Element toXML(Document doc) {
		Element node = doc.createElement("session");
		for (AppUsage usage : mApps) {
			node.appendChild(usage.toXML(doc));
		}
		return node;
	}
}
