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
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.TileObserver;
import java.awt.image.WritableRaster;

/**
 * WriteableRenderedImage is a common interface for objects which
 * contain or can produce image data in the form of Rasters and
 * which can be modified and/or written over.  The image
 * data may be stored/produced as a single tile or a regular array
 * of tiles.
 * <p>
 * WritableRenderedImage provides notification to other interested
 * objects when a tile is checked out for writing (via the
 * getWritableTile method) and when the last writer of a particular
 * tile relinquishes its access (via a call to releaseWritableTile).
 * Additionally, it allows any caller to determine whether any tiles
 * are currently checked out (via hasTileWriters), and to obtain a
 * list of such tiles (via getWritableTileIndices, in the form of a Vector
 * of Point objects).
 * <p>
 * Objects wishing to be notified of changes in tile writability must
 * implement the TileObserver interface, and are added by a
 * call to addTileObserver.  Multiple calls to
 * addTileObserver for the same object will result in multiple
 * notifications.  An existing observer may reduce its notifications
 * by calling removeTileObserver; if the observer had no
 * notifications the operation is a no-op.
 * <p>
 * It is necessary for a WritableRenderedImage to ensure that
 * notifications occur only when the first writer acquires a tile and
 * the last writer releases it.
 *
 */

public interface WritableRenderedImage extends RenderedImage
{

  /**
   * Adds an observer.  If the observer is already present,
   * it will receive multiple notifications.
   * @param to the specified <code>TileObserver</code>
   */
  public void addTileObserver(TileObserver to);

  /**
   * Removes an observer.  If the observer was not registered,
   * nothing happens.  If the observer was registered for multiple
   * notifications, it will now be registered for one fewer.
   * @param to the specified <code>TileObserver</code>
   */
  public void removeTileObserver(TileObserver to);

  /**
   * Checks out a tile for writing.
   *
   * The WritableRenderedImage is responsible for notifying all
   * of its TileObservers when a tile goes from having
   * no writers to having one writer.
   *
   * @param tileX the X index of the tile.
   * @param tileY the Y index of the tile.
   * @return a writable tile.
   */
  public WritableRaster getWritableTile(int tileX, int tileY);

  /**
   * Relinquishes the right to write to a tile.  If the caller
   * continues to write to the tile, the results are undefined.
   * Calls to this method should only appear in matching pairs
   * with calls to getWritableTile; any other use will lead
   * to undefined results.
   *
   * The WritableRenderedImage is responsible for notifying all of
   * its TileObservers when a tile goes from having one writer
   * to having no writers.
   *
   * @param tileX the X index of the tile.
   * @param tileY the Y index of the tile.
   */
  public void releaseWritableTile(int tileX, int tileY);

  /**
   * Returns whether a tile is currently checked out for writing.
   *
   * @param tileX the X index of the tile.
   * @param tileY the Y index of the tile.
   * @return <code>true</code> if specified tile is checked out
   *         for writing; <code>false</code> otherwise.
   */
  public boolean isTileWritable(int tileX, int tileY);

  /**
   * Returns an array of Point objects indicating which tiles
   * are checked out for writing.  Returns null if none are
   * checked out.
   * @return an array containing the locations of tiles that are
   *         checked out for writing.
   */
  public Point[] getWritableTileIndices();

  /**
   * Returns whether any tile is checked out for writing.
   * Semantically equivalent to (getWritableTileIndices() != null).
   * @return <code>true</code> if any tiles are checked out for
   *         writing; <code>false</code> otherwise.
   */
  public boolean hasTileWriters();

  /**
   * Sets a rect of the image to the contents of the Raster r, which is
   * assumed to be in the same coordinate space as the WritableRenderedImage.
   * The operation is clipped to the bounds of the WritableRenderedImage.
   * @param r the specified <code>Raster</code>
   */
  public void setData(Raster r);

}
