/*******************************************************************************
 * Copyright (c) 2008 The Eclipse Foundation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    The Eclipse Foundation - initial API and implementation
 *******************************************************************************/
package org.dawnsci.usagedata.internal.recording;

import org.dawnsci.usagedata.internal.recording.filtering.AbstractUsageDataEventFilterTests;
import org.dawnsci.usagedata.internal.recording.filtering.FilterUtilsTests;
import org.dawnsci.usagedata.internal.recording.filtering.PreferencesBasedFilterTests;
import org.dawnsci.usagedata.internal.recording.settings.UsageDataRecordingSettingsTests;
import org.dawnsci.usagedata.internal.recording.uploading.BasicUploaderTests;
import org.dawnsci.usagedata.internal.recording.uploading.UsageDataFileReaderTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses( { 
	UsageDataRecorderUtilsTests.class,
	UsageDataRecordingSettingsTests.class,
	AbstractUsageDataEventFilterTests.class,
	FilterUtilsTests.class,
	PreferencesBasedFilterTests.class,
	BasicUploaderTests.class, 
	UsageDataFileReaderTests.class
})
public class AllTests {

}
