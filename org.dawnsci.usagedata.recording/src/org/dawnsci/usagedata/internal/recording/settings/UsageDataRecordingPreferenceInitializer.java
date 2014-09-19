/*
 * Copyright (c) 2012 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.dawnsci.usagedata.internal.recording.settings;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.dawnsci.usagedata.internal.recording.UsageDataRecordingActivator;
import org.eclipse.jface.preference.IPreferenceStore;

public class UsageDataRecordingPreferenceInitializer extends AbstractPreferenceInitializer {
	@Override
	public void initializeDefaultPreferences() {
		String defaultAskToUpload = System.getProperty(UsageDataRecordingSettings.ASK_TO_UPLOAD_KEY, String.valueOf(UsageDataRecordingSettings.ASK_TO_UPLOAD_DEFAULT));
		String defaultUploadPeriod = System.getProperty(UsageDataRecordingSettings.UPLOAD_PERIOD_KEY, String.valueOf(UsageDataRecordingSettings.UPLOAD_PERIOD_DEFAULT));
		String defaultFilterEcplise = System.getProperty(UsageDataRecordingSettings.FILTER_ECLIPSE_BUNDLES_ONLY_KEY, "false");
		String defaultNonBundleEvent = System.getProperty(UsageDataRecordingSettings.FILTER_NON_BUNDLE_EVENT_ONLY_KEY, "true");
		IPreferenceStore preferenceStore = UsageDataRecordingActivator.getDefault().getPreferenceStore();
		preferenceStore.setDefault(UsageDataRecordingSettings.UPLOAD_PERIOD_KEY, defaultUploadPeriod);
		preferenceStore.setDefault(UsageDataRecordingSettings.ASK_TO_UPLOAD_KEY, defaultAskToUpload);
		preferenceStore.setDefault(UsageDataRecordingSettings.FILTER_ECLIPSE_BUNDLES_ONLY_KEY, defaultFilterEcplise);
		preferenceStore.setDefault(UsageDataRecordingSettings.FILTER_NON_BUNDLE_EVENT_ONLY_KEY, defaultNonBundleEvent);
	}

}
