/*
 * Created on 23.07.2007
 *
 * Copyright (C) 2007  Emanuel Indermühle <emanuel@inthemill.ch>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * @author emanuel
 */

package ch.unibe.eindermu.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

/**
 * Class to backup settings of a graphics object.
 * The folloing methods are supported to this time:
 * <ul>
 *  <li>color</li>
 *  <li>backgroundcolor</li>
 *  <li>affine Transformation</li>
 *  <li>stroke</li>
 * </ul>
 * @author emanuel
 *
 */
public class GraphicsBackup{
    private Color color,backgroundcolor;
    
    private AffineTransform affineTransform;
    
    private Graphics2D g;
    
    private Stroke stroke;
    
    /**
     * Constructor which is called to do the backup
     * @param g graphics to back up
     */
    public GraphicsBackup(Graphics2D g) {
        this.g = g;
        color = g.getColor();
        backgroundcolor = g.getBackground();
        affineTransform = g.getTransform();
        stroke = g.getStroke();
    }
    
    /**
     * method called to reload the backup
     */
    public void reset() {
        g.setColor(color);
        g.setTransform(affineTransform);
        g.setStroke(stroke);
        g.setBackground(backgroundcolor);
    }

	public Graphics2D getGraphics() {
		return g;
	}
}
