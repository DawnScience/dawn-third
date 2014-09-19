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

public interface UsageDataEventFilter {

	boolean includes(UsageDataEvent event);

	void addFilterChangeListener(FilterChangeListener filterChangeListener);

	void removeFilterChangeListener(FilterChangeListener filterChangeListener);
}
