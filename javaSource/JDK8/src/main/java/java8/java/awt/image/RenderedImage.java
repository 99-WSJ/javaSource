/*
 * Copyright (c) 1997, 2008, Oracle and/or its affiliates. All rights reserved.
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

/* ****************************************************************
 ******************************************************************
 ******************************************************************
 *** COPYRIGHT (c) Eastman Kodak Company, 1997
 *** As  an unpublished  work pursuant to Title 17 of the United
 *** States Code.  All rights reserved.
 ******************************************************************
 ******************************************************************
 ******************************************************************/

package java8.java.awt.image;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Vector;

/**
 * RenderedImage is a common interface for objects which contain
 * or can produce image data in the form of Rasters.  The image
 * data may be stored/produced as a single tile or a regular array
 * of tiles.
 */

public interface RenderedImage {

    /**
     * Returns a vector of RenderedImages that are the immediate sources of
     * image data for this RenderedImage.  This method returns null if
     * the RenderedImage object has no information about its immediate
     * sources.  It returns an empty Vector if the RenderedImage object has
     * no immediate sources.
     * @return a Vector of <code>RenderedImage</code> objects.
     */
    Vector<java.awt.image.RenderedImage> getSources();

    /**
     * Gets a property from the property set of this image.  The set of
     * properties and whether it is immutable is determined by the
     * implementing class.  This method returns
     * java.awt.Image.UndefinedProperty if the specified property is
     * not defined for this RenderedImage.
     * @param name the name of the property
     * @return the property indicated by the specified name.
     * @see java.awt.Image#UndefinedProperty
     */
    Object getProperty(String name);

    /**
      * Returns an array of names recognized by
      * {@link #getProperty(String) getProperty(String)}
      * or <code>null</code>, if no property names are recognized.
      * @return a <code>String</code> array containing all of the
      * property names that <code>getProperty(String)</code> recognizes;
      * or <code>null</code> if no property names are recognized.
      */
    String[] getPropertyNames();

    /**
     * Returns the ColorModel associated with this image.  All Rasters
     * returned from this image will have this as their ColorModel.  This
     * can return null.
     * @return the <code>ColorModel</code> of this image.
     */
    ColorModel getColorModel();

    /**
     * Returns the SampleModel associated with this image.  All Rasters
     * returned from this image will have this as their SampleModel.
     * @return the <code>SampleModel</code> of this image.
     */
    SampleModel getSampleModel();

    /**
     * Returns the width of the RenderedImage.
     * @return the width of this <code>RenderedImage</code>.
     */
    int getWidth();

    /**
     * Returns the height of the RenderedImage.
     * @return the height of this <code>RenderedImage</code>.
     */
    int getHeight();

    /**
     * Returns the minimum X coordinate (inclusive) of the RenderedImage.
     * @return the X coordinate of this <code>RenderedImage</code>.
     */
    int getMinX();

    /**
     * Returns the minimum Y coordinate (inclusive) of the RenderedImage.
     * @return the Y coordinate of this <code>RenderedImage</code>.
     */
    int getMinY();

    /**
     * Returns the number of tiles in the X direction.
     * @return the number of tiles in the X direction.
     */
    int getNumXTiles();

    /**
     * Returns the number of tiles in the Y direction.
     * @return the number of tiles in the Y direction.
     */
    int getNumYTiles();

    /**
     *  Returns the minimum tile index in the X direction.
     *  @return the minimum tile index in the X direction.
     */
    int getMinTileX();

    /**
     *  Returns the minimum tile index in the Y direction.
     *  @return the minimum tile index in the X direction.
     */
    int getMinTileY();

    /**
     *  Returns the tile width in pixels.  All tiles must have the same
     *  width.
     *  @return the tile width in pixels.
     */
    int getTileWidth();

    /**
     *  Returns the tile height in pixels.  All tiles must have the same
     *  height.
     *  @return the tile height in pixels.
     */
    int getTileHeight();

    /**
     * Returns the X offset of the tile grid relative to the origin,
     * i.e., the X coordinate of the upper-left pixel of tile (0, 0).
     * (Note that tile (0, 0) may not actually exist.)
     * @return the X offset of the tile grid relative to the origin.
     */
    int getTileGridXOffset();

    /**
     * Returns the Y offset of the tile grid relative to the origin,
     * i.e., the Y coordinate of the upper-left pixel of tile (0, 0).
     * (Note that tile (0, 0) may not actually exist.)
     * @return the Y offset of the tile grid relative to the origin.
     */
    int getTileGridYOffset();

    /**
     * Returns tile (tileX, tileY).  Note that tileX and tileY are indices
     * into the tile array, not pixel locations.  The Raster that is returned
     * is live and will be updated if the image is changed.
     * @param tileX the X index of the requested tile in the tile array
     * @param tileY the Y index of the requested tile in the tile array
     * @return the tile given the specified indices.
     */
   Raster getTile(int tileX, int tileY);

    /**
     * Returns the image as one large tile (for tile based
     * images this will require fetching the whole image
     * and copying the image data over).  The Raster returned is
     * a copy of the image data and will not be updated if the image
     * is changed.
     * @return the image as one large tile.
     */
    Raster getData();

    /**
     * Computes and returns an arbitrary region of the RenderedImage.
     * The Raster returned is a copy of the image data and will not
     * be updated if the image is changed.
     * @param rect the region of the RenderedImage to be returned.
     * @return the region of the <code>RenderedImage</code>
     * indicated by the specified <code>Rectangle</code>.
     */
    Raster getData(Rectangle rect);

    /**
     * Computes an arbitrary rectangular region of the RenderedImage
     * and copies it into a caller-supplied WritableRaster.  The region
     * to be computed is determined from the bounds of the supplied
     * WritableRaster.  The supplied WritableRaster must have a
     * SampleModel that is compatible with this image.  If raster is null,
     * an appropriate WritableRaster is created.
     * @param raster a WritableRaster to hold the returned portion of the
     *               image, or null.
     * @return a reference to the supplied or created WritableRaster.
     */
    WritableRaster copyData(WritableRaster raster);
}
