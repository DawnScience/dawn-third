/*
 * Copyright (c) 2012 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.dawnsci.usagedata.internal.ui.preferences;

import org.dawnsci.usagedata.internal.recording.UsageDataRecordingActivator;
import org.dawnsci.usagedata.internal.recording.settings.UsageDataRecordingSettings;
import org.dawnsci.usagedata.internal.recording.uploading.UploadParameters;
import org.dawnsci.usagedata.internal.ui.preview.UploadPreview;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class UsageDataUploadingPreviewPage extends PreferencePage
	implements IWorkbenchPreferencePage {

	public UsageDataUploadingPreviewPage() {
		noDefaultAndApplyButton();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		UploadParameters parameters = new UploadParameters();
		UsageDataRecordingSettings settings = getSettings();
		parameters.setSettings(settings);
		parameters.setFiles(settings.getUsageDataUploadFiles());
		new UploadPreview(parameters).createControl(composite);
		return composite;
	}

	protected UsageDataRecordingSettings getSettings() {
		return UsageDataRecordingActivator.getDefault().getSettings();
	}
}