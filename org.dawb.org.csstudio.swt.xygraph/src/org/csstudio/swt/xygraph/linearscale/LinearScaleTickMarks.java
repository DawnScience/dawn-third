package org.csstudio.swt.xygraph.linearscale;

import org.csstudio.swt.xygraph.linearscale.AbstractScale.LabelSide;
import org.csstudio.swt.xygraph.util.SWTConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;

/**
 * Linear scale tick marks.
 * @author Xihui Chen
 */
public class LinearScaleTickMarks extends Figure {   



	/** the scale */
    private LinearScale scale;

    /** the line width */
    protected static final int LINE_WIDTH = 1;

    /** the tick length */
    public static final int MAJOR_TICK_LENGTH = 6;
    /** the tick length */
    public static final int MINOR_TICK_LENGTH = 3;

    /**
     * Constructor.
     * 
     * @param chart
     *            the chart
     * @param style
     *            the style
     * @param scale
     *            the scale
     */
    public LinearScaleTickMarks(LinearScale scale) {
        
        this.scale = scale;

        setForegroundColor(scale.getForegroundColor());
    }

   protected void paintClientArea(Graphics graphics) {
	   graphics.translate(bounds.x, bounds.y);
	   ITicksProvider ticks = scale.getTicksProvider();

        int width = getSize().width;
        int height = getSize().height;

        if (scale.isHorizontal()) {
            drawXTickMarks(graphics, ticks, scale.getTickLabelSide(), width,
                    height);
        } else {
            drawYTickMarks(graphics, ticks, scale.getTickLabelSide(), width,
                    height);
        }
   };

    /**
     * Draw the X tick marks.
     * 
     * @param tickLabelPositions
     *            the tick label positions
     * @param tickLabelSide
     *            the side of tick label relative to tick marks
     * @param width
     *            the width to draw tick marks
     * @param height
     *            the height to draw tick marks
     * @param gc
     *            the graphics context
     */
    private void drawXTickMarks(Graphics gc, ITicksProvider ticks,
            LabelSide tickLabelSide, int width, int height) {
    	
        // draw tick marks
        gc.setLineStyle(SWTConstants.LINE_SOLID);
        int imax = ticks.getMajorCount();
        if(scale.isLogScaleEnabled()) {
        	for (int i = 0; i < imax; i++) {
                int x = ticks.getPosition(i);
                int y = 0;
                int tickLength =0;
                if(ticks.isVisible(i))
                	tickLength = MAJOR_TICK_LENGTH;
                else
                	tickLength = MINOR_TICK_LENGTH;

                if (tickLabelSide == LabelSide.Secondary) {
                    y = height - 1 - LINE_WIDTH - tickLength;
                }
                //draw minor ticks for log scale
                if(ticks.isVisible(i) || scale.isMinorTicksVisible())
                	gc.drawLine(x, y, x, y + tickLength);
        	}
        } else {
        	final int y = tickLabelSide == LabelSide.Secondary ? height - 1 - LINE_WIDTH - MAJOR_TICK_LENGTH : 0;
        	for (int i = 0; i < imax; i++) {
                int x = ticks.getPosition(i);
                if (i!=imax-1) {
                    gc.drawLine(x, y, x, y + MAJOR_TICK_LENGTH);
                }
                
            }
            //draw minor ticks for linear scale
            if(scale.isMinorTicksVisible()){
            	int jmax = ticks.getMinorCount();
            	for (int j = 0; j < jmax; j++) {
    				drawXMinorTicks(gc, tickLabelSide, ticks.getMinorPosition(j), y); 
            	}
            }
        }

        //draw scale line
        if(scale.isScaleLineVisible()) {
        	if (tickLabelSide == LabelSide.Primary) {
            gc.drawLine(scale.getMargin(), 0, width - scale.getMargin(), 0);
        } else {
            gc.drawLine(scale.getMargin(), height - 1, width - scale.getMargin(), height - 1);
        }
        }
        
    }


	private void drawXMinorTicks(Graphics gc, LabelSide tickLabelSide, int x,
			int y) {
		if(tickLabelSide == LabelSide.Primary)
			gc.drawLine(x, y, x, y + MINOR_TICK_LENGTH);
		else
			gc.drawLine(x, y + MAJOR_TICK_LENGTH - MINOR_TICK_LENGTH, 
					x, y + MAJOR_TICK_LENGTH);
	}

    /**
     * Draw the Y tick marks.
     * 
     * @param tickLabelPositions
     *            the tick label positions
     * @param tickLabelSide
     *            the side of tick label relative to tick marks
     * @param width
     *            the width to draw tick marks
     * @param height
     *            the height to draw tick marks
     * @param gc
     *            the graphics context
     */
    private void drawYTickMarks(Graphics gc, ITicksProvider ticks, LabelSide tickLabelSide, int width, int height) {
        // draw tick marks
        gc.setLineStyle(SWTConstants.LINE_SOLID);
        int x = 0;
        int y = 0;
        int imax = ticks.getMajorCount();
        if(scale.isLogScaleEnabled()) {
        	for (int i = 0; i < imax; i++) {
        		
                int tickLength =0;
                if(ticks.isVisible(i))
                	tickLength = MAJOR_TICK_LENGTH;
                else
                 	tickLength = MINOR_TICK_LENGTH;            
                
                if (tickLabelSide == LabelSide.Primary) {
                    x = width - 1 - LINE_WIDTH - tickLength;
                } else {
                    x = LINE_WIDTH;
                }
                y = height - ticks.getPosition(i);
                if(ticks.isVisible(i) || scale.isMinorTicksVisible())
                	gc.drawLine(x, y, x + tickLength, y);
        	}
        } else {        
            if (tickLabelSide == LabelSide.Primary) {
                x = width - 1 - LINE_WIDTH - MAJOR_TICK_LENGTH;
            } else {
                x = LINE_WIDTH;
            }
            for (int i = 0; i < imax; i++) {
                y = height - ticks.getPosition(i);
                if (i!=imax-1) {
                    gc.drawLine(x, y, x + MAJOR_TICK_LENGTH, y);
                }
            }
            //draw minor ticks for linear scale
            if(scale.isMinorTicksVisible()){
            	final int jmax = ticks.getMinorCount();
            	for (int j = 0; j < jmax; j++) {
    				drawYMinorTicks(gc, tickLabelSide, x, height - ticks.getMinorPosition(j)); 
            	}
            }
        }

        // draw scale line
        if(scale.isScaleLineVisible()) {
        	if (tickLabelSide == LabelSide.Primary) {
            gc.drawLine(width - 1, scale.getMargin(), width - 1, height - scale.getMargin());
        } else {
            gc.drawLine(0, scale.getMargin(), 0, height - scale.getMargin());
        }
        }
        
    }


	private void drawYMinorTicks(Graphics gc, LabelSide tickLabelSide, int x,
			int y) {
		//there is a mis-illumination (?)
		int verticalMinorTickLength = MINOR_TICK_LENGTH -1;
		if(tickLabelSide == LabelSide.Primary)               				
			gc.drawLine(x + MAJOR_TICK_LENGTH - verticalMinorTickLength, y,
				x + MAJOR_TICK_LENGTH, y);
		else
			gc.drawLine(x, y,
				x + verticalMinorTickLength, y);
	}
    

}
