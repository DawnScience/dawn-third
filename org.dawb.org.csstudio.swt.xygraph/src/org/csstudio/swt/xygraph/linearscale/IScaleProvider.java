package org.csstudio.swt.xygraph.linearscale;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * Provides a scale for drawing tick marks.
 * 
 * This allows the scale to draw tick marks different to the range which the
 * scale is operating over.
 * 
 * @author fcp94556
 *
 */
public interface IScaleProvider {

	public Font getFont();

	public Color getForegroundColor();

	public boolean isLogScaleEnabled();

	public Range getScaleRange();

	public boolean isDateEnabled();

	public String format(Object date);

	public boolean isAutoFormat();

	public void setAutoFormat(boolean autoFormat);

	public void setDefaultFormatPattern(String formatPattern);

	public int getMargin();

	public boolean isHorizontal();

	public double getMajorGridStep();

	public int getMajorTickMarkStepHint();

	public int getMinorTickMarkStepHint();

	public int getTimeUnit();

	public int getLength();

	public Dimension calculateDimension(Object obj);

	public boolean isPrimary();
}
