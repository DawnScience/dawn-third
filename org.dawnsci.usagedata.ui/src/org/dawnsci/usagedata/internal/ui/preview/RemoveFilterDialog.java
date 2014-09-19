/*
 * Copyright (c) 2012 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.dawnsci.usagedata.internal.ui.preview;

import java.util.Arrays;

import org.dawnsci.usagedata.internal.recording.filtering.PreferencesBasedFilter;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

public class RemoveFilterDialog {

	private final PreferencesBasedFilter filter;

	public RemoveFilterDialog(PreferencesBasedFilter filter) {
		this.filter = filter;
	}

	public void prompt(Shell shell) {
		ListDialog dialog = new ListDialog(shell) {
			@Override
			protected int getTableStyle() {
				return super.getTableStyle() - SWT.SINGLE + SWT.MULTI;
			}
		};
		dialog.setTitle(Messages.RemoveFilterDialog_0); 
		dialog.setMessage(Messages.RemoveFilterDialog_1); 
		dialog.setAddCancelButton(true);
		dialog.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return (String)element;
			}
		});
		dialog.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return (Object[])inputElement;
			}

			public void dispose() {				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {				
			}			
		});
		String[] filterPatterns = filter.getFilterPatterns();
		Arrays.sort(filterPatterns);
		dialog.setInput(filterPatterns);
		dialog.open();
		Object[] selected = dialog.getResult();
		if (selected == null) return;
		
		filter.removeFilterPatterns(selected);
	}

}
