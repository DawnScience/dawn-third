package org.csstudio.swt.xygraph.preferences;

import org.csstudio.swt.xygraph.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class XYInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(XYConstants.XY_SHOWLEGEND,  true);

	}

}
