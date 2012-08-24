package org.csstudio.swt.xygraph.linearscale;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;

/**
 * Linear Scale tick labels.
 * @author Xihui Chen
 */
public class LinearScaleTickLabels extends Figure {

	private ITicksProvider ticks;

    private IScaleProvider scale;

    /**
     * Constructor.
     * 
     * @param linearScale
     *            the scale
     */
    protected LinearScaleTickLabels(IScaleProvider linearScale) {
    	
    	this.scale = linearScale;
    	ticks = new LinearScaleTicks(scale);

        setFont(this.scale.getFont());
        setForegroundColor(this.scale.getForegroundColor());
    }

    public ITicksProvider getTicksProvider() {
    	return ticks;
    }

    /**
     * Updates the tick labels.
     * 
     * @param length
     *            scale tick length (without margin)
     */
    protected void update(int length) {
    	Range range = scale.getScaleRange();
    	ticks.update(getFont(), range.getLower(), range.getUpper(), length);
    }

    @Override
    protected void paintClientArea(Graphics graphics) {
    	graphics.translate(bounds.x, bounds.y);
    	if (scale.isHorizontal()) {
            drawXTick(graphics);
        } else {
            drawYTick(graphics);
        }

    	super.paintClientArea(graphics);
    };

    /**
     * Draw the X tick.
     * 
     * @param grahics
     *            the graphics context
     */
    private void drawXTick(Graphics grahics) {
        // draw tick labels
        grahics.setFont(scale.getFont());
        final int imax = ticks.getMajorCount();
        for (int i = 0; i < imax; i++) {
            if (ticks.isVisible(i)) {
                String text = ticks.getLabel(i);
                int fontWidth = FigureUtilities.getTextExtents(text, getFont()).width;
                int x = (int) Math.ceil(ticks.getPosition(i) - fontWidth / 2.0);// + offset);
                grahics.drawText(text, x, 0);
            }
        }
    }

    /**
     * Draw the Y tick.
     * 
     * @param grahpics
     *            the graphics context
     */
    private void drawYTick(Graphics grahpics) {
        // draw tick labels
        grahpics.setFont(scale.getFont());
        int fontHeight = ticks.getMaxHeight();
        final int imax = ticks.getMajorCount();
        for (int i = 0; i < imax; i++) {
            if (ticks.isVisible(i)) {
                String text = ticks.getLabel(i);
                int x = 0;
                if (ticks.getLabel(0).startsWith("-") && !text.startsWith("-")) {
                    x += FigureUtilities.getTextExtents("-", getFont()).width;
                }
                int y = (int) Math.ceil(scale.getLength() - ticks.getPosition(i)
                        - fontHeight / 2.0);
                grahpics.drawText(text, x, y);
            }
        }
    }

	/**
	 * @return the tickLabelMaxLength
	 */
	public int getTickLabelMaxLength() {
		return ticks.getMaxWidth();
	}

	/**
	 * @return the tickLabelMaxHeight
	 */
	public int getTickLabelMaxHeight() {
		return ticks.getMaxWidth();
	}

	public IScaleProvider getScale() {
		return scale;
	}

	public void setScale(IScaleProvider scale) {
		this.scale = scale;
	}

}
