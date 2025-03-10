/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java8.javax.swing.plaf.metal;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Polygon;

import javax.swing.*;

import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalUtils;


/**
 * JButton object for Metal scrollbar arrows.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans&trade;
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @author Tom Santos
 * @author Steve Wilson
 */
public class MetalScrollButton extends BasicArrowButton
{
  private static Color shadowColor;
  private static Color highlightColor;
  private boolean isFreeStanding = false;

  private int buttonWidth;

        public MetalScrollButton( int direction, int width, boolean freeStanding )
        {
            super( direction );

            shadowColor = UIManager.getColor("ScrollBar.darkShadow");
            highlightColor = UIManager.getColor("ScrollBar.highlight");

            buttonWidth = width;
            isFreeStanding = freeStanding;
        }

        public void setFreeStanding( boolean freeStanding )
        {
            isFreeStanding = freeStanding;
        }

        public void paint( Graphics g )
        {
            boolean leftToRight = javax.swing.plaf.metal.MetalUtils.isLeftToRight(this);
            boolean isEnabled = getParent().isEnabled();

            Color arrowColor = isEnabled ? MetalLookAndFeel.getControlInfo() : MetalLookAndFeel.getControlDisabled();
            boolean isPressed = getModel().isPressed();
            int width = getWidth();
            int height = getHeight();
            int w = width;
            int h = height;
            int arrowHeight = (height+1) / 4;
            int arrowWidth = (height+1) / 2;

            if ( isPressed )
            {
                g.setColor( MetalLookAndFeel.getControlShadow() );
            }
            else
            {
                g.setColor( getBackground() );
            }

            g.fillRect( 0, 0, width, height );

            if ( getDirection() == NORTH )
            {
                if ( !isFreeStanding ) {
                    height +=1;
                    g.translate( 0, -1 );
                    width += 2;
                    if ( !leftToRight ) {
                        g.translate( -1, 0 );
                    }
                }

                // Draw the arrow
                g.setColor( arrowColor );
                int startY = ((h+1) - arrowHeight) / 2;
                int startX = (w / 2);
                //                  System.out.println( "startX :" + startX + " startY :"+startY);
                for (int line = 0; line < arrowHeight; line++) {
                    g.drawLine( startX-line, startY+line, startX +line+1, startY+line);
                }
        /*      g.drawLine( 7, 6, 8, 6 );
                g.drawLine( 6, 7, 9, 7 );
                g.drawLine( 5, 8, 10, 8 );
                g.drawLine( 4, 9, 11, 9 );*/

                if (isEnabled) {
                    g.setColor( highlightColor );

                    if ( !isPressed )
                    {
                        g.drawLine( 1, 1, width - 3, 1 );
                        g.drawLine( 1, 1, 1, height - 1 );
                    }

                    g.drawLine( width - 1, 1, width - 1, height - 1 );

                    g.setColor( shadowColor );
                    g.drawLine( 0, 0, width - 2, 0 );
                    g.drawLine( 0, 0, 0, height - 1 );
                    g.drawLine( width - 2, 2, width - 2, height - 1 );
                } else {
                    javax.swing.plaf.metal.MetalUtils.drawDisabledBorder(g, 0, 0, width, height+1);
                }
                if ( !isFreeStanding ) {
                    height -= 1;
                    g.translate( 0, 1 );
                    width -= 2;
                    if ( !leftToRight ) {
                        g.translate( 1, 0 );
                    }
                }
            }
            else if ( getDirection() == SOUTH )
            {
                if ( !isFreeStanding ) {
                    height += 1;
                    width += 2;
                    if ( !leftToRight ) {
                        g.translate( -1, 0 );
                    }
                }

                // Draw the arrow
                g.setColor( arrowColor );

                int startY = (((h+1) - arrowHeight) / 2)+ arrowHeight-1;
                int startX = (w / 2);

                //          System.out.println( "startX2 :" + startX + " startY2 :"+startY);

                for (int line = 0; line < arrowHeight; line++) {
                    g.drawLine( startX-line, startY-line, startX +line+1, startY-line);
                }

        /*      g.drawLine( 4, 5, 11, 5 );
                g.drawLine( 5, 6, 10, 6 );
                g.drawLine( 6, 7, 9, 7 );
                g.drawLine( 7, 8, 8, 8 ); */

                if (isEnabled) {
                    g.setColor( highlightColor );

                    if ( !isPressed )
                    {
                        g.drawLine( 1, 0, width - 3, 0 );
                        g.drawLine( 1, 0, 1, height - 3 );
                    }

                    g.drawLine( 1, height - 1, width - 1, height - 1 );
                    g.drawLine( width - 1, 0, width - 1, height - 1 );

                    g.setColor( shadowColor );
                    g.drawLine( 0, 0, 0, height - 2 );
                    g.drawLine( width - 2, 0, width - 2, height - 2 );
                    g.drawLine( 2, height - 2, width - 2, height - 2 );
                } else {
                    javax.swing.plaf.metal.MetalUtils.drawDisabledBorder(g, 0,-1, width, height+1);
                }

                if ( !isFreeStanding ) {
                    height -= 1;
                    width -= 2;
                    if ( !leftToRight ) {
                        g.translate( 1, 0 );
                    }
                }
            }
            else if ( getDirection() == EAST )
            {
                if ( !isFreeStanding ) {
                    height += 2;
                    width += 1;
                }

                // Draw the arrow
                g.setColor( arrowColor );

                int startX = (((w+1) - arrowHeight) / 2) + arrowHeight-1;
                int startY = (h / 2);

                //System.out.println( "startX2 :" + startX + " startY2 :"+startY);

                for (int line = 0; line < arrowHeight; line++) {
                    g.drawLine( startX-line, startY-line, startX -line, startY+line+1);
                }


/*              g.drawLine( 5, 4, 5, 11 );
                g.drawLine( 6, 5, 6, 10 );
                g.drawLine( 7, 6, 7, 9 );
                g.drawLine( 8, 7, 8, 8 );*/

                if (isEnabled) {
                    g.setColor( highlightColor );

                    if ( !isPressed )
                    {
                        g.drawLine( 0, 1, width - 3, 1 );
                        g.drawLine( 0, 1, 0, height - 3 );
                    }

                    g.drawLine( width - 1, 1, width - 1, height - 1 );
                    g.drawLine( 0, height - 1, width - 1, height - 1 );

                    g.setColor( shadowColor );
                    g.drawLine( 0, 0,width - 2, 0 );
                    g.drawLine( width - 2, 2, width - 2, height - 2 );
                    g.drawLine( 0, height - 2, width - 2, height - 2 );
                } else {
                    javax.swing.plaf.metal.MetalUtils.drawDisabledBorder(g,-1,0, width+1, height);
                }
                if ( !isFreeStanding ) {
                    height -= 2;
                    width -= 1;
                }
            }
            else if ( getDirection() == WEST )
            {
                if ( !isFreeStanding ) {
                    height += 2;
                    width += 1;
                    g.translate( -1, 0 );
                }

                // Draw the arrow
                g.setColor( arrowColor );

                int startX = (((w+1) - arrowHeight) / 2);
                int startY = (h / 2);


                for (int line = 0; line < arrowHeight; line++) {
                    g.drawLine( startX+line, startY-line, startX +line, startY+line+1);
                }

        /*      g.drawLine( 6, 7, 6, 8 );
                g.drawLine( 7, 6, 7, 9 );
                g.drawLine( 8, 5, 8, 10 );
                g.drawLine( 9, 4, 9, 11 );*/

                if (isEnabled) {
                    g.setColor( highlightColor );


                    if ( !isPressed )
                    {
                        g.drawLine( 1, 1, width - 1, 1 );
                        g.drawLine( 1, 1, 1, height - 3 );
                    }

                    g.drawLine( 1, height - 1, width - 1, height - 1 );

                    g.setColor( shadowColor );
                    g.drawLine( 0, 0, width - 1, 0 );
                    g.drawLine( 0, 0, 0, height - 2 );
                    g.drawLine( 2, height - 2, width - 1, height - 2 );
                } else {
                    javax.swing.plaf.metal.MetalUtils.drawDisabledBorder(g,0,0, width+1, height);
                }

                if ( !isFreeStanding ) {
                    height -= 2;
                    width -= 1;
                    g.translate( 1, 0 );
                }
            }
        }

        public Dimension getPreferredSize()
        {
            if ( getDirection() == NORTH )
            {
                return new Dimension( buttonWidth, buttonWidth - 2 );
            }
            else if ( getDirection() == SOUTH )
            {
                return new Dimension( buttonWidth, buttonWidth - (isFreeStanding ? 1 : 2) );
            }
            else if ( getDirection() == EAST )
            {
                return new Dimension( buttonWidth - (isFreeStanding ? 1 : 2), buttonWidth );
            }
            else if ( getDirection() == WEST )
            {
                return new Dimension( buttonWidth - 2, buttonWidth );
            }
            else
            {
                return new Dimension( 0, 0 );
            }
        }

        public Dimension getMinimumSize()
        {
            return getPreferredSize();
        }

        public Dimension getMaximumSize()
        {
            return new Dimension( Integer.MAX_VALUE, Integer.MAX_VALUE );
        }

        public int getButtonWidth() {
            return buttonWidth;
        }
}
