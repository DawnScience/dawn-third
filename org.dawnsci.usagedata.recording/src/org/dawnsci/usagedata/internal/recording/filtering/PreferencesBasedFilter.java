/*
 * Copyright (c) 2012 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.dawnsci.usagedata.internal.recording.filtering;

import org.dawnsci.usagedata.internal.gathering.events.UsageDataEvent;
import org.dawnsci.usagedata.internal.recording.UsageDataRecordingActivator;
import org.dawnsci.usagedata.internal.recording.settings.UsageDataRecordingSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * The {@link PreferencesBasedFilter} is a {@link UsageDataEventFilter} with
 * direct links to preferences for the org.dawnsci.usagedata.recording
 * bundle. When created, instances hook a listener into the preference store
 * for the bundle so as to be notified by changes in preferences and update
 * accordingly. Instances must be released using the {@link #dispose()} method
 * to clean up the listener.
 * <p>
 * A single instance of this class is maintained by the activator for the
 * bundle via the settings object. The activator manages the lifecycle.
 * 
 * @see UsageDataRecordingSettings
 * @author Wayne Beaton
 */
public class PreferencesBasedFilter extends AbstractUsageDataEventFilter {

	public PreferencesBasedFilter() {
		hookListeners();
	}

	protected void hookListeners() {
		propertyChangeListener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (isFilterProperty(event.getProperty())) {
					fireFilterChangedEvent();
				}
			}			
		};
		getPreferenceStore().addPropertyChangeListener(propertyChangeListener);
	}

	/**
	 * This method cleans up hooks made by the instance.
	 */
	public void dispose() {
		getPreferenceStore().removePropertyChangeListener(propertyChangeListener);
	}

	/**
	 * This method is used to test whether or the parameter represents
	 * a property that the receiver is interested in.
	 */
	boolean isFilterProperty(String property) {
		if (UsageDataRecordingSettings.FILTER_ECLIPSE_BUNDLES_ONLY_KEY.equals(property)) return true;
		if (UsageDataRecordingSettings.FILTER_NON_BUNDLE_EVENT_ONLY_KEY.equals(property)) return true;
		if (UsageDataRecordingSettings.FILTER_PATTERNS_KEY.equals(property)) return true;
		return false;
	}

	public boolean includes(UsageDataEvent event) {
		if (includeOnlyEclipseDotOrgBundles()) {
			return event.bundleId.startsWith("org.eclipse."); //$NON-NLS-1$
		}
		if (includeOnlyNonBundlesEvents()) {
			return (event.kind.startsWith("sysinfo") //$NON-NLS-1$
					|| event.kind.startsWith("view") //$NON-NLS-1$
					|| event.kind.startsWith("perspective") //$NON-NLS-1$
					|| event.kind.startsWith("editor") //$NON-NLS-1$
					|| event.kind.startsWith("command") //$NON-NLS-1$
					|| event.kind.startsWith("perspective") //$NON-NLS-1$
					|| event.kind.startsWith("log")); //$NON-NLS-1$
		}
		for (String filter : getFilterPatterns()) {
			if (matches(filter, event.bundleId)) return false;
		}
		return true;
	}

	public String[] getFilterPatterns() {
		String patternString = getPreferenceStore().getString(UsageDataRecordingSettings.FILTER_PATTERNS_KEY);
		if ("".equals(patternString)) return new String[0]; //$NON-NLS-1$
		return patternString.split("\n"); //$NON-NLS-1$
	}

	private boolean includeOnlyEclipseDotOrgBundles() {
		return getPreferenceStore().getBoolean(UsageDataRecordingSettings.FILTER_ECLIPSE_BUNDLES_ONLY_KEY);
	}

	private boolean includeOnlyNonBundlesEvents() {
		return getPreferenceStore().getBoolean(UsageDataRecordingSettings.FILTER_NON_BUNDLE_EVENT_ONLY_KEY);
	}

	IPreferenceStore getPreferenceStore() {
		return UsageDataRecordingActivator.getDefault().getPreferenceStore();
	}

	public void addPattern(String value) {
		String patternString = getPreferenceStore().getString(UsageDataRecordingSettings.FILTER_PATTERNS_KEY);
		if (patternString.trim().length() == 0) {
			patternString = value;
		} else {
			patternString += "\n" + value; //$NON-NLS-1$
		}
		getPreferenceStore().setValue(UsageDataRecordingSettings.FILTER_PATTERNS_KEY, patternString);
		UsageDataRecordingActivator.getDefault().savePluginPreferences();
	}
	
	public boolean includesPattern(String pattern) {
		for (String filter : getFilterPatterns()) {
			if (pattern.equals(filter)) return true;
		}
		return false;
	}

	public void removeFilterPatterns(Object[] toRemove) {
		String patternString = getPreferenceStore().getString(UsageDataRecordingSettings.FILTER_PATTERNS_KEY);
		String[] patterns = patternString.split("\n"); //$NON-NLS-1$
		StringBuilder builder = new StringBuilder();
		String separator = ""; //$NON-NLS-1$
		for (String pattern : patterns) {
			if (!shouldRemovePattern(pattern, toRemove)) {
				builder.append(separator);
				builder.append(pattern);
				separator = "\n"; //$NON-NLS-1$
			}
		}
		getPreferenceStore().setValue(UsageDataRecordingSettings.FILTER_PATTERNS_KEY, builder.toString());
		UsageDataRecordingActivator.getDefault().savePluginPreferences();
	}

	private boolean shouldRemovePattern(String pattern, Object[] toRemove) {
		for (Object object : toRemove) {
			if (object.equals(pattern)) return true;			
		}
		return false;
	}

	public void setEclipseOnly(boolean value) {
		getPreferenceStore().setValue(UsageDataRecordingSettings.FILTER_ECLIPSE_BUNDLES_ONLY_KEY, value);
		UsageDataRecordingActivator.getDefault().savePluginPreferences();
		// Don't need to do this, happens indirectly when value is set above.
		// fireFilterChangedEvent();
	}

	public void setNonBundleEventOnly(boolean value) {
		getPreferenceStore().setValue(UsageDataRecordingSettings.FILTER_NON_BUNDLE_EVENT_ONLY_KEY, value);
		UsageDataRecordingActivator.getDefault().savePluginPreferences();
		// Don't need to do this, happens indirectly when value is set above.
		// fireFilterChangedEvent();
	}

	public boolean isEclipseOnly() {
		return getPreferenceStore().getBoolean(UsageDataRecordingSettings.FILTER_ECLIPSE_BUNDLES_ONLY_KEY);
	}

	public boolean isNonBundleEventOnly() {
		return getPreferenceStore().getBoolean(UsageDataRecordingSettings.FILTER_NON_BUNDLE_EVENT_ONLY_KEY);
	}
}
