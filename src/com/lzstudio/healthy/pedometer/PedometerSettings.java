/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lzstudio.healthy.pedometer;

import android.content.SharedPreferences;

/**
 * Wrapper for {@link SharedPreferences}, handles preferences-related tasks.
 * 
 * @author Levente Bagi
 */
public class PedometerSettings {

	SharedPreferences mSettings;

	public PedometerSettings(SharedPreferences settings) {
		mSettings = settings;
	}

	public boolean isMetric() {
		return mSettings.getString("units", "metric").equals("metric");
	}

	public float getStepLength() {
		return mSettings.getFloat("step_length", 60);
	}

	public float getBodyWeight() {
		return mSettings.getFloat("body_weight", 50);
	}

	public boolean isRunning() {
		return mSettings.getString("exercise_type", "running")
				.equals("running");
	}

}
