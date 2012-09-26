/*
 * Copyright 2012 Diamond Light Source Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.csstudio.swt.xygraph.linearscale;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.swt.xygraph.linearscale.TickFactory.TickFormatting;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * Class to represent a major tick
 */
class Tick {

	private String text;
	private double value;
	private double position;
	private int tPosition;
	
	/**
	 * @param tickText
	 */
	public void setText(String tickText) {
		text = tickText;
	}
	
	/**
	 * @return the tick text
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * @param tickValue
	 */
	public void setValue(double tickValue) {
		value = tickValue;
	}
	
	/**
	 * @return the tick value
	 */
	public double getValue() {
		return value;
	}
	
	/**
	 * @param tickPosition in pixels
	 */
	public void setPosition(double tickPosition) {
		position = tickPosition;
	}
	
	/**
	 * @return the tick position in pixels
	 */
	public double getPosition() {
		return position;
	}

	/**
	 * @param textPosition in pixels
	 */
	public void setTextPosition(int textPosition) {
		tPosition = textPosition;
	}

	/**
	 * @return the text position in pixels
	 */
	public int getTextPosition() {
		return tPosition;
	}

	@Override
	public String toString() {
		return text + " (" + position + ", " + tPosition + ")";
	}
}

public class LinearScaleTicks2 implements ITicksProvider {
	protected List<Tick> ticks;

	/** the maximum width of tick labels */
	private int maxWidth;

	/** the maximum height of tick labels */
	private int maxHeight;

	/** the array of minor tick positions in pixels */
	protected ArrayList<Integer> minorPositions;

	protected IScaleProvider scale;

	private double majorStepInPixel;

	public LinearScaleTicks2(IScaleProvider scale) {
		this.scale = scale;
		minorPositions = new ArrayList<Integer>();
	}

	@Override
	public List<Integer> getPositions() {
		List<Integer> positions = new ArrayList<Integer>();
		for (Tick t : ticks)
			positions.add((int) t.getPosition());
		return positions;
	}

	@Override
	public int getPosition(int index) {
		return (int) ticks.get(index).getPosition();
	}

	@Override
	public int getLabelPosition(int index) {
		return ticks.get(index).getTextPosition();
	}

	@Override
	public double getValue(int index) {
		return ticks.get(index).getValue();
	}

	@Override
	public String getLabel(int index) {
		return ticks.get(index).getText();
	}

	@Override
	public boolean isVisible(int index) {
		return true;
	}

	@Override
	public int getMajorCount() {
		return ticks.size();
	}

	@Override
	public int getMinorCount() {
		return minorPositions.size();
	}

	@Override
	public int getMinorPosition(int index) {
		return minorPositions.get(index);
	}

	@Override
	public int getMaxWidth() {
		return maxWidth;
	}

	@Override
	public int getMaxHeight() {
		return maxHeight;
	}

	private final static int TICKMINDIST_IN_PIXELS_X = 60;
	private final static int TICKMINDIST_IN_PIXELS_Y = 30;

	@Override
	public Range update(final double min, final double max, final int length) {
		if (scale.isLogScaleEnabled() && (min <= 0 || max <= 0))
			throw new IllegalArgumentException("Range for log scale must be in positive range");

		final int maximumNumTicks = scale.isHorizontal() ? Math.min(8, length / TICKMINDIST_IN_PIXELS_X)
				: Math.min(8, length / TICKMINDIST_IN_PIXELS_Y);
		int numTicks = Math.max(2, maximumNumTicks);

		final TickFactory tf;
		if (scale instanceof AbstractScale) {
			AbstractScale aScale = (AbstractScale) scale;
			if (aScale.hasUserDefinedFormat()) {
				tf = new TickFactory(scale);
			} else if (aScale.isAutoFormat()) {
				tf = new TickFactory(TickFormatting.plainMode);
			} else {
				String format = aScale.getFormatPattern();
				if (format.contains("E")) {
					tf = new TickFactory(TickFormatting.useExponent);
				} else {
					tf = new TickFactory(TickFormatting.plainMode);
				}
			}
		} else {
			tf = new TickFactory(TickFormatting.plainMode);
		}

		final int hMargin = getHeadMargin();
		final int tMargin = getTailMargin();
		// loop until labels fit
		do {
			if (scale.isLogScaleEnabled()) {
				ticks = tf.generateLogTicks(length, min, max, numTicks, scale.isPrimary(), !scale.hasTicksAtEnds());
			} else {
				ticks = tf.generateTicks(length, min, max, numTicks, scale.isPrimary(), !scale.hasTicksAtEnds());
			}
		} while (!updateLabelPositionsAndCheckGaps(length, hMargin, tMargin) && numTicks-- > 2);

		updateMinorTicks(hMargin+length);
		if (scale.hasTicksAtEnds() && ticks.size() > 1)
			return new Range(ticks.get(0).getValue(), ticks.get(ticks.size() - 1).getValue());

		return null;
	}

	@Override
	public String getDefaultFormatPattern(double min, double max) {
		String format = null;

		// calculate the default decimal format
		double mantissa = Math.abs(max - min);
		if (mantissa > 0.1)
			format = "############.##";
		else {
			format = "##.##";
			while (mantissa < 1 && mantissa > 0) {
				mantissa *= 10.0;
				format += "#";
			}
		}

		return format;
	}

	@Override
	public int getHeadMargin() {
		if (ticks == null || ticks.size() == 0 || maxWidth == 0
				|| maxHeight == 0) {
//			System.err.println("No ticks yet!");
			final Dimension l = scale.calculateDimension(scale.getScaleRange().getLower());
			if (scale.isHorizontal()) {
//				System.err.println("calculate X margin with " + r);
				return l.width;
			}
//			System.err.println("calculate Y margin with " + r);
			return l.height;
		}
		return scale.isHorizontal() ? (maxWidth + 1) / 2 : (maxHeight + 1) / 2;
	}

	@Override
	public int getTailMargin() {
		if (ticks == null || ticks.size() == 0 || maxWidth == 0
				|| maxHeight == 0) {
//			System.err.println("No ticks yet!");
			final Dimension h = scale.calculateDimension(scale.getScaleRange().getUpper());
			if (scale.isHorizontal()) {
//				System.err.println("calculate X margin with " + r);
				return h.width;
			}
//			System.err.println("calculate Y margin with " + r);
			return h.height;
//			System.err.println("No ticks yet!");
		}
		return scale.isHorizontal() ? (maxWidth + 1) / 2 : (maxHeight + 1) / 2;
	}

	private static final String MINUS = "-";

	/**
	 * Update positions and max dimensions of tick labels
	 * 
	 * @return true if there is no overlaps
	 */
	private boolean updateLabelPositionsAndCheckGaps(int length, final int hMargin, final int tMargin) {
		for (Tick t : ticks) {
			t.setPosition(length * t.getPosition() + hMargin);
		}
		final int imax = ticks.size();
		if (imax == 0) {
			return true;
		} else if (imax > 1) {
			majorStepInPixel = (ticks.get(imax-1).getPosition() - ticks.get(0).getPosition()) / (imax - 1);
		}

		maxWidth = 0;
		maxHeight = 0;
		final boolean hasNegative = ticks.get(0).getText().startsWith(MINUS);
		final int minus = scale.calculateDimension(MINUS).width;
		for (Tick t : ticks) {
			final String l = t.getText();
			final Dimension d = scale.calculateDimension(l);
			if (hasNegative && !l.startsWith(MINUS)) {
				d.width += minus;
			}
			if (d.width > maxWidth) {
				maxWidth = d.width;
			}
			if (d.height > maxHeight) {
				maxHeight = d.height;
			}
		}

		if (length <= 0)
			return true; // sanity check

//		System.err.println("Max labels have w:" + maxWidth + ", h:" + maxHeight);
		length += hMargin + tMargin; // re-expand length (so labels can flow into margins)
		if (scale.isHorizontal()) {
			double last = 0;
			for (Tick t : ticks) {
				final Dimension d = scale.calculateDimension(t.getText());
				double p = t.getPosition() - d.width * 0.5;
				if (p < 0) {
					p = 0;
				} else if (p + d.width >= length) {
					p = length - 1 - d.width;
				}
				if (last > p) {
					if (ticks.indexOf(t) == (imax-1)) {
						t.setTextPosition((int) Math.ceil(p));
					}
					return false;
				}
				last = p + d.width;
				t.setTextPosition((int) Math.ceil(p));
			}
		} else {
			for (Tick t : ticks) {
				final Dimension d = scale.calculateDimension(t.getText());
				double p = length - 1 - t.getPosition() - d.height * 0.5;
				if (p < 0) {
					p = 0;
				} else if (p + d.height >= length) {
					p = length - 1 - d.height;
				}
				t.setTextPosition((int) Math.ceil(p));
			}
		}

		return true;
	}

	private static final double LAST_STEP_FRAC = 1 - Math.log10(9); // fraction of major tick step between 9 and 10

	private void updateMinorTicks(final int end) {
		minorPositions.clear();

		if (majorStepInPixel <= 0 || ticks.size() == 0)
			return;

		int minorTicks;

		if (scale.isLogScaleEnabled()) {
			if (majorStepInPixel * LAST_STEP_FRAC >= scale.getMinorTickMarkStepHint()) {
				minorTicks = 10;
				double p = ticks.get(0).getPosition();
				if (p > 0) {
					p -= majorStepInPixel;
					for (int i = 1; i < minorTicks; i++) {
						int q = (int) (p + majorStepInPixel * Math.log10((10. * i) / minorTicks));
						if (q >= 0 && q < end && !ticks.contains(q))
							minorPositions.add(q);
					}
				}
				for (int j = 0, jmax = ticks.size(); j < jmax; j++) {
					p = ticks.get(j).getPosition();
					for (int i = 1; i < minorTicks; i++) {
						int q = (int) (p + majorStepInPixel * Math.log10((10. * i) / minorTicks));
						if (q >= 0 && q < end && !ticks.contains(q))
							minorPositions.add(q);
					}
				}
			}
		} else {
			if (scale.isDateEnabled()) {
				minorTicks = 6;
			} else if (majorStepInPixel / 5 >= scale.getMinorTickMarkStepHint()) {
				minorTicks = 5;
			} else if (majorStepInPixel / 4 >= scale.getMinorTickMarkStepHint()) {
				minorTicks = 4;
			} else {
				minorTicks = 2;
			}

			double minorStepInPixel = majorStepInPixel / minorTicks;
			double p = ticks.get(0).getPosition();
//			if (scale.isHorizontal()) {
				if (p > 0) {
					p -= majorStepInPixel;
					for (int i = 1; i < minorTicks; i++) {
						int q = (int) Math.floor(p + i * minorStepInPixel);
						if (q >= 0 && q < end && !ticks.contains(q))
							minorPositions.add(q);
					}
				}
				for (int j = 0, jmax = ticks.size(); j < jmax; j++) {
					p = ticks.get(j).getPosition();
					for (int i = 1; i < minorTicks; i++) {
						int q = (int) Math.floor(p + i * minorStepInPixel);
						if (q >= 0 && q < end && !ticks.contains(q))
							minorPositions.add(q);
					}
				}
//			} else {
//				if (p > 0) {
//					p -= majorStepInPixel;
//					for (int i = 1; i < minorTicks; i++) {
//						int q = (int) Math.ceil(p + i * minorStepInPixel);
//						if (q >= 0 && q < length && !ticks.contains(q))
//							minorPositions.add(q);
//					}
//				}
//				for (int j = 0, jmax = ticks.size(); j < jmax; j++) {
//					p = ticks.get(j).getPosition();
//					for (int i = 1; i < minorTicks; i++) {
//						int q = (int) Math.ceil(p + i * minorStepInPixel);
//						if (q >= 0 && q < length && !ticks.contains(q))
//							minorPositions.add(q);
//					}
//				}
//			}
		}
	}
}
