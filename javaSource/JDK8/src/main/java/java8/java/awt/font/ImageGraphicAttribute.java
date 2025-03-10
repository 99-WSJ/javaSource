/*
 * Copyright (c) 1998, 2006, Oracle and/or its affiliates. All rights reserved.
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

/*
 * (C) Copyright Taligent, Inc. 1996 - 1997, All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998, All Rights Reserved
 *
 * The original version of this source code and documentation is
 * copyrighted and owned by Taligent, Inc., a wholly-owned subsidiary
 * of IBM. These materials are provided under terms of a License
 * Agreement between Taligent and Sun. This technology is protected
 * by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java8.java.awt.font;

import java.awt.*;
import java.awt.font.GraphicAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

/**
 * The <code>ImageGraphicAttribute</code> class is an implementation of
 * {@link GraphicAttribute} which draws images in
 * a {@link TextLayout}.
 * @see GraphicAttribute
 */

public final class ImageGraphicAttribute extends GraphicAttribute {

    private Image fImage;
    private float fImageWidth, fImageHeight;
    private float fOriginX, fOriginY;

    /**
     * Constucts an <code>ImageGraphicAttribute</code> from the specified
     * {@link Image}.  The origin is at (0,&nbsp;0).
     * @param image the <code>Image</code> rendered by this
     * <code>ImageGraphicAttribute</code>.
     * This object keeps a reference to <code>image</code>.
     * @param alignment one of the alignments from this
     * <code>ImageGraphicAttribute</code>
     */
    public ImageGraphicAttribute(Image image, int alignment) {

        this(image, alignment, 0, 0);
    }

    /**
     * Constructs an <code>ImageGraphicAttribute</code> from the specified
     * <code>Image</code>. The point
     * (<code>originX</code>,&nbsp;<code>originY</code>) in the
     * <code>Image</code> appears at the origin of the
     * <code>ImageGraphicAttribute</code> within the text.
     * @param image the <code>Image</code> rendered by this
     * <code>ImageGraphicAttribute</code>.
     * This object keeps a reference to <code>image</code>.
     * @param alignment one of the alignments from this
     * <code>ImageGraphicAttribute</code>
     * @param originX the X coordinate of the point within
     * the <code>Image</code> that appears at the origin of the
     * <code>ImageGraphicAttribute</code> in the text line.
     * @param originY the Y coordinate of the point within
     * the <code>Image</code> that appears at the origin of the
     * <code>ImageGraphicAttribute</code> in the text line.
     */
    public ImageGraphicAttribute(Image image,
                                 int alignment,
                                 float originX,
                                 float originY) {

        super(alignment);

        // Can't clone image
        // fImage = (Image) image.clone();
        fImage = image;

        fImageWidth = image.getWidth(null);
        fImageHeight = image.getHeight(null);

        // ensure origin is in Image?
        fOriginX = originX;
        fOriginY = originY;
    }

    /**
     * Returns the ascent of this <code>ImageGraphicAttribute</code>.  The
     * ascent of an <code>ImageGraphicAttribute</code> is the distance
     * from the top of the image to the origin.
     * @return the ascent of this <code>ImageGraphicAttribute</code>.
     */
    public float getAscent() {

        return Math.max(0, fOriginY);
    }

    /**
     * Returns the descent of this <code>ImageGraphicAttribute</code>.
     * The descent of an <code>ImageGraphicAttribute</code> is the
     * distance from the origin to the bottom of the image.
     * @return the descent of this <code>ImageGraphicAttribute</code>.
     */
    public float getDescent() {

        return Math.max(0, fImageHeight-fOriginY);
    }

    /**
     * Returns the advance of this <code>ImageGraphicAttribute</code>.
     * The advance of an <code>ImageGraphicAttribute</code> is the
     * distance from the origin to the right edge of the image.
     * @return the advance of this <code>ImageGraphicAttribute</code>.
     */
    public float getAdvance() {

        return Math.max(0, fImageWidth-fOriginX);
    }

    /**
     * Returns a {@link Rectangle2D} that encloses all of the
     * bits rendered by this <code>ImageGraphicAttribute</code>, relative
     * to the rendering position.  A graphic can be rendered beyond its
     * origin, ascent, descent, or advance;  but if it is, this
     * method's implementation must indicate where the graphic is rendered.
     * @return a <code>Rectangle2D</code> that encloses all of the bits
     * rendered by this <code>ImageGraphicAttribute</code>.
     */
    public Rectangle2D getBounds() {

        return new Rectangle2D.Float(
                        -fOriginX, -fOriginY, fImageWidth, fImageHeight);
    }

    /**
     * {@inheritDoc}
     */
    public void draw(Graphics2D graphics, float x, float y) {

        graphics.drawImage(fImage, (int) (x-fOriginX), (int) (y-fOriginY), null);
    }

    /**
     * Returns a hashcode for this <code>ImageGraphicAttribute</code>.
     * @return  a hash code value for this object.
     */
    public int hashCode() {

        return fImage.hashCode();
    }

    /**
     * Compares this <code>ImageGraphicAttribute</code> to the specified
     * {@link Object}.
     * @param rhs the <code>Object</code> to compare for equality
     * @return <code>true</code> if this
     * <code>ImageGraphicAttribute</code> equals <code>rhs</code>;
     * <code>false</code> otherwise.
     */
    public boolean equals(Object rhs) {

        try {
            return equals((java.awt.font.ImageGraphicAttribute) rhs);
        }
        catch(ClassCastException e) {
            return false;
        }
    }

    /**
     * Compares this <code>ImageGraphicAttribute</code> to the specified
     * <code>ImageGraphicAttribute</code>.
     * @param rhs the <code>ImageGraphicAttribute</code> to compare for
     * equality
     * @return <code>true</code> if this
     * <code>ImageGraphicAttribute</code> equals <code>rhs</code>;
     * <code>false</code> otherwise.
     */
    public boolean equals(java.awt.font.ImageGraphicAttribute rhs) {

        if (rhs == null) {
            return false;
        }

        if (this == rhs) {
            return true;
        }

        if (fOriginX != rhs.fOriginX || fOriginY != rhs.fOriginY) {
            return false;
        }

        if (getAlignment() != rhs.getAlignment()) {
            return false;
        }

        if (!fImage.equals(rhs.fImage)) {
            return false;
        }

        return true;
    }
}
