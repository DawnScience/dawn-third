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

import java.util.List;

import org.eclipse.swt.graphics.Font;

/**
 * 
 *
 */
public interface ITicksProvider {

    /**
     * Gets the tick positions.
     * 
     * @return the tick positions
     */
    public List<Integer> getPositions();

    /**
     * 
     * @param index
     * @return
     */
    public int getPosition(int index);

    /**
     * 
     * @param index
     * @return
     */
    public double getValue(int index);

    /**
     * 
     * @param index
     * @return
     */
    public String getLabel(int index);

    /**
     * 
     * @param index
     * @return
     */
    public boolean isVisible(int index);

    /**
     * 
     * @return
     */
    public int getStepInPixels();

    /**
     * 
     * @return number of major ticks
     */
    public int getCount();

    /**
     * Update ticks
     * 
     * @param min
     * @param max
     * @param length
     */
    public void update(Font font, double min , double max, int length);

    public int getMaxWidth();

    public int getMaxHeight();
}
