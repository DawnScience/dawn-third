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

import java.math.BigDecimal;
import java.util.LinkedList;

/**
 * Tick factory produces the different axis ticks. When specifying a format and
 * given the screen size parameters and range it will return a list of Ticks
 */

public class TickFactory {
	public enum TickFormatting {
		/**
		 * Plain mode no rounding no chopping maximum 6 figures before the 
		 * fraction point and four after
		 */
		plainMode,
		/**
		 * Rounded or chopped to the nearest decimal
		 */
		roundAndChopMode,
		/**
		 * Use Exponent 
		 */
		useExponent,
		/**
		 * Use SI units (k,M,G,etc.)
		 */
		useSIunits
	}

	private TickFormatting formatOfTicks;
	private final static BigDecimal EPSILON = new BigDecimal("1.0E-20");
	
	private double tickUnit = 0.0;
	private double internalGraphmin;
	private double realGraphmin;
	private double graphmax;
	private int nfrac;
	private boolean overwriteMinAnyway = false;
	
	/**
	 * @param format
	 */
	public TickFactory(TickFormatting format)
	{
	   formatOfTicks = format;	
	}

	private String getTickString(double value)
	{
		String returnString = "";
		switch (formatOfTicks)
		{
			case plainMode:
			{
				String formatStr = String.format("%%.%df",nfrac);
				returnString = String.format(formatStr,value);
			}
			break;
			case roundAndChopMode:
			{
				long newValue = Math.round(value);
				returnString = String.format("%d", newValue);
			}
			break;
			case useExponent:
			{
				String formatStr = String.format("%%.%de",nfrac);
				returnString = String.format(formatStr,value);
			}
			break;
		case useSIunits:
			double absValue = Math.abs(value);
			if (absValue == 0.0) {
				returnString = String.format("%6.2f", value);
			} else if (absValue <= 1E-15) {
				returnString = String.format("%6.2ff", value * 1E15);
			} else if (absValue <= 1E-12) {
				returnString = String.format("%6.2fp", value * 1E12);
			} else if (absValue <= 1E-9) {
				returnString = String.format("%6.2fn", value * 1E9);
			} else if (absValue <= 1E-6) {
				returnString = String.format("%6.2fµ", value * 1E6);
			} else if (absValue <= 1E-3) {
				returnString = String.format("%6.2fm", value * 1E3);
			} else if (absValue < 1E3) {
				returnString = String.format("%6.2f", value);
			} else if (absValue < 1E6) {
				returnString = String.format("%6.2fk", value * 1E-3);
			} else if (absValue < 1E9) {
				returnString = String.format("%6.2fM", value * 1E-6);
			} else if (absValue < 1E12) {
				returnString = String.format("%6,2fG", value * 1E-9);
			} else if (absValue < 1E15) {
				returnString = String.format("%6.2fT", value * 1E-12);
			} else if (absValue < 1E18)
				returnString = String.format("%6.2fP", value * 1E-15);
			break;
		}
		return returnString;
	}

	/**
	 * @param x
	 * @return floor of log 10
	 */
	private int log10(BigDecimal x) {
		int c = x.compareTo(BigDecimal.ONE); 
		int e = 0;
		while (c < 0) {
			e--;
			x = x.scaleByPowerOfTen(1);
			c = x.compareTo(BigDecimal.ONE);
		}
	
		c = x.compareTo(BigDecimal.TEN);
		while (c >= 0) {
			e++;
			x = x.scaleByPowerOfTen(-1);
			c = x.compareTo(BigDecimal.TEN);
		}
	
		return e;
	}

	private BigDecimal nicenum(BigDecimal x, boolean round) {
		int expv; /* exponent of x */
		double f; /* fractional part of x */
		double nf; /* nice, rounded number */
		BigDecimal bf;

		expv = log10(x);
		bf = x.scaleByPowerOfTen(-expv);
		f = bf.doubleValue();
		// f = x/Math.pow(10., expv); /* between 1 and 10 */
		if (round)
			if (f < 1.5)
				nf = 1.;
			else if (f < 3.)
				nf = 2.;
			else if (f < 7.)
				nf = 5.;
			else
				nf = 10.;
		else if (f <= 1.)
			nf = 1.;
		else if (f <= 2.)
			nf = 2.;
		else if (f <= 5.)
			nf = 5.;
		else
			nf = 10.;
		return BigDecimal.valueOf(nf).scaleByPowerOfTen(expv);
	}

	private boolean determineNumTicks(int size, 
								   double min, 
								   double max,
								   int maxTicks,
								   boolean allowMinMaxOver)
	{
		overwriteMinAnyway = false;
	
		BigDecimal bMin = BigDecimal.valueOf(min);
		BigDecimal bMax = BigDecimal.valueOf(max);
		BigDecimal bRange = bMax.subtract(bMin);
		final boolean isReverse;
		if (bRange.signum() < 0) {
			BigDecimal bt = bMin;
			bMin = bMax;
			bMax = bt;
			bRange = bRange.negate();
			isReverse = true;
		} else {
			isReverse = false;
		}
	
		// tick points to dense do not do anything
		if (bRange.compareTo(EPSILON) < 0) {
			return false;
		}
	
		bRange = nicenum(bRange, false);
		BigDecimal bUnit;
		long n;
		do { // ensure number of ticks is less or equal to number requested
			bUnit = nicenum(BigDecimal.valueOf(bRange.doubleValue() / (maxTicks - 1)), true);
			n = bRange.divideToIntegralValue(bUnit).longValue();
		} while (n > maxTicks-- && maxTicks > 1);
	
		tickUnit = isReverse ? -bUnit.doubleValue() : bUnit.doubleValue();
		if (allowMinMaxOver) {
			internalGraphmin = bMin.divideToIntegralValue(bUnit)
					.multiply(bUnit).doubleValue();
			// check if difference is too large
			if ((bMin.doubleValue() - internalGraphmin) > 1000) {
				overwriteMinAnyway = true;
				realGraphmin = min;
			} else
				realGraphmin = internalGraphmin;
			BigDecimal[] bResult = bMax.divideAndRemainder(bUnit);
			if (bResult[1].compareTo(BigDecimal.ZERO) > 0) {
				graphmax = bResult[0].add(BigDecimal.ONE).multiply(bUnit)
						.doubleValue();
			} else {
				graphmax = bResult[0].multiply(bUnit).doubleValue();
			}
		} else {
			internalGraphmin = min;
			realGraphmin = min;
			graphmax = max;
		}
		if (isReverse) {
			double t = internalGraphmin;
			internalGraphmin = graphmax;
			realGraphmin = graphmax;
			graphmax = t;
		}
	
		nfrac = (int) Math.max(-Math.floor(Math.log10(Math.abs(tickUnit))), 0);
		return true;
	}

	/**
	 * @param displaySize 
	 * @param min
	 * @param max
	 * @param maxTicks
	 * @param allowMinMaxOver allow min/maximum overwrite
	 * @param tight if true then remove ticks outside range 
	 * @return a list of the ticks for the axis
	 */
	
	public LinkedList<Tick> generateTicks(int displaySize, 
										  double min, 
										  double max, 
										  int maxTicks,
										  boolean allowMinMaxOver, final boolean tight)
	{
		LinkedList<Tick> ticks = new LinkedList<Tick>();
		determineNumTicks(displaySize, min, max, maxTicks, allowMinMaxOver);
		double p = internalGraphmin;
		if (tickUnit > 0) {
			final double pmax = graphmax + 0.5 * tickUnit;
			while (p < pmax) {
				if (!tight || (p >= min && p <= max))
				if (allowMinMaxOver || p <= max) {
					Tick newTick = new Tick();
					if (p == internalGraphmin && overwriteMinAnyway)
						newTick.setValue(realGraphmin);
					else
						newTick.setValue(p);
					newTick.setText(getTickString(newTick.getValue()));
					ticks.add(newTick);
				}
				double newTickValue = p + tickUnit;
				if (p == newTickValue)
					break;
				p = newTickValue;
			}
			final int imax = ticks.size();
			if (imax == 1) {
				ticks.get(0).setPosition(0.5);
			} else if (imax > 1) {
				double lo = tight ? min : ticks.get(0).getValue();
				double hi = tight ? max : ticks.get(imax - 1).getValue();
				double range = hi - lo;
				for (Tick t : ticks) {
					t.setPosition((t.getValue() - lo) / range);
				}
			}
		} else {
			final double pmin = graphmax + 0.5 * tickUnit;
			while (p > pmin) {
				if (!tight || (p >= max && p <= min))
				if (allowMinMaxOver || p <= max) {
					Tick newTick = new Tick();
					if (p == internalGraphmin && overwriteMinAnyway)
						newTick.setValue(realGraphmin);
					else
						newTick.setValue(p);
					newTick.setText(getTickString(newTick.getValue()));
					ticks.add(newTick);
				}
				double newTickValue = p + tickUnit;
				if (p == newTickValue)
					break;
				p = newTickValue;
			}
			final int imax = ticks.size();
			if (imax == 1) {
				ticks.get(0).setPosition(0.5);
			} else if (imax > 1) {
				double lo = tight ? max : ticks.get(0).getValue();
				double hi = tight ? min : ticks.get(imax - 1).getValue();
				double range = hi - lo;
				for (Tick t : ticks) {
					t.setPosition(1 - (t.getValue() - lo) / range);
				}
			}
		}
		return ticks;
	}


	private boolean determineNumLogTicks(int size, double min, double max, int maxTicks, boolean allowMinMaxOver) {
		overwriteMinAnyway = false;
	
		final boolean isReverse = min > max;
		final int loDecade; // lowest decade (or power of ten)
		final int hiDecade;
		if (isReverse) {
			loDecade = (int) Math.floor(Math.log10(max));
			hiDecade = (int) Math.ceil(Math.log10(min));
		} else {
			loDecade = (int) Math.floor(Math.log10(min));
			hiDecade = (int) Math.ceil(Math.log10(max));
		}
	
		int decades = hiDecade - loDecade;

		int unit = 0;
		int n;
		do {
			n = decades/++unit;
		} while (n > maxTicks);	

		tickUnit = isReverse ? Math.pow(10, -unit) : Math.pow(10, unit);
		if (allowMinMaxOver) {
			internalGraphmin = Math.pow(10, loDecade);
			realGraphmin = internalGraphmin;
			graphmax = Math.pow(10, hiDecade);
		} else {
			internalGraphmin = min;
			realGraphmin = min;
			graphmax = max;
		}
		if (isReverse) {
			double t = internalGraphmin;
			internalGraphmin = graphmax;
			realGraphmin = graphmax;
			graphmax = t;
		}
	
		nfrac = (int) Math.max(-Math.floor(loDecade), 0);
		return true;
	}

	/**
	 * @param displaySize 
	 * @param min
	 * @param max
	 * @param maxTicks
	 * @param allowMinMaxOver allow min/maximum overwrite
	 * @param tight if true then remove ticks outside range 
	 * @return a list of the ticks for the axis
	 */
	public LinkedList<Tick> generateLogTicks(int displaySize, 
										  double min, 
										  double max, 
										  int maxTicks,
										  boolean allowMinMaxOver, final boolean tight)
	{
		LinkedList<Tick> ticks = new LinkedList<Tick>();
		determineNumLogTicks(displaySize, min, max, maxTicks, allowMinMaxOver);
		double p = internalGraphmin;
		if (tickUnit > 1) {
			final double pmax = graphmax * Math.sqrt(tickUnit);
			while (p < pmax) {
				if (!tight || (p >= min && p <= max))
				if (allowMinMaxOver || p <= max) {
					Tick newTick = new Tick();
					if (p == internalGraphmin && overwriteMinAnyway)
						newTick.setValue(realGraphmin);
					else
						newTick.setValue(p);
					newTick.setText(getTickString(newTick.getValue()));
					ticks.add(newTick);
				}
				double newTickValue = p * tickUnit;
				if (p == newTickValue)
					break;
				p = newTickValue;
			}
			final int imax = ticks.size();
			if (imax == 1) {
				ticks.get(0).setPosition(0.5);
			} else if (imax > 1) {
				double lo = Math.log(tight ? min : ticks.get(0).getValue());
				double hi = Math.log(tight ? max : ticks.get(imax - 1).getValue());
				double range = hi - lo;
				for (Tick t : ticks) {
					t.setPosition((Math.log(t.getValue()) - lo) / range);
				}
			}
		} else {
			final double pmin = graphmax * Math.sqrt(tickUnit);
			while (p > pmin) {
				if (!tight || (p >= max && p <= min))
				if (allowMinMaxOver || p <= max) {
					Tick newTick = new Tick();
					if (p == internalGraphmin && overwriteMinAnyway)
						newTick.setValue(realGraphmin);
					else
						newTick.setValue(p);
					newTick.setText(getTickString(newTick.getValue()));
					ticks.add(newTick);
				}
				double newTickValue = p * tickUnit;
				if (p == newTickValue)
					break;
				p = newTickValue;
			}
			final int imax = ticks.size();
			if (imax == 1) {
				ticks.get(0).setPosition(0.5);
			} else if (imax > 1) {
				double lo = Math.log(tight ? max : ticks.get(0).getValue());
				double hi = Math.log(tight ? min : ticks.get(imax - 1).getValue());
				double range = hi - lo;
				for (Tick t : ticks) {
					t.setPosition(1 - (Math.log(t.getValue()) - lo) / range);
				}
			}
		}
		return ticks;
	}
}
