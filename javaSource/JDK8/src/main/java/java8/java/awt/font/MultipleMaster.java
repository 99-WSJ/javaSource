/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
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
package java8.java.awt.font;

import java.awt.*;

/**
 * The <code>MultipleMaster</code> interface represents Type 1
 * Multiple Master fonts.
 * A particular {@link Font} object can implement this interface.
 */
public interface MultipleMaster {

  /**
   * Returns the number of multiple master design controls.
   * Design axes include things like width, weight and optical scaling.
   * @return the number of multiple master design controls
   */
  public  int getNumDesignAxes();

  /**
   * Returns an array of design limits interleaved in the form [from&rarr;to]
   * for each axis.  For example,
   * design limits for weight could be from 0.1 to 1.0. The values are
   * returned in the same order returned by
   * <code>getDesignAxisNames</code>.
   * @return an array of design limits for each axis.
   */
  public  float[]  getDesignAxisRanges();

  /**
   * Returns an array of default design values for each axis.  For example,
   * the default value for weight could be 1.6. The values are returned
   * in the same order returned by <code>getDesignAxisNames</code>.
   * @return an array of default design values for each axis.
   */
  public  float[]  getDesignAxisDefaults();

  /**
   * Returns the name for each design axis. This also determines the order in
   * which the values for each axis are returned.
   * @return an array containing the names of each design axis.
   */
  public  String[] getDesignAxisNames();

  /**
   * Creates a new instance of a multiple master font based on the design
   * axis values contained in the specified array. The size of the array
   * must correspond to the value returned from
   * <code>getNumDesignAxes</code> and the values of the array elements
   * must fall within limits specified by
   * <code>getDesignAxesLimits</code>. In case of an error,
   * <code>null</code> is returned.
   * @param axes an array containing axis values
   * @return a {@link Font} object that is an instance of
   * <code>MultipleMaster</code> and is based on the design axis values
   * provided by <code>axes</code>.
   */
  public Font deriveMMFont(float[] axes);

  /**
   * Creates a new instance of a multiple master font based on detailed metric
   * information. In case of an error, <code>null</code> is returned.
   * @param glyphWidths an array of floats representing the desired width
   * of each glyph in font space
   * @param avgStemWidth the average stem width for the overall font in
   * font space
   * @param typicalCapHeight the height of a typical upper case char
   * @param typicalXHeight the height of a typical lower case char
   * @param italicAngle the angle at which the italics lean, in degrees
   * counterclockwise from vertical
   * @return a <code>Font</code> object that is an instance of
   * <code>MultipleMaster</code> and is based on the specified metric
   * information.
   */
  public Font deriveMMFont(
                                   float[] glyphWidths,
                                   float avgStemWidth,
                                   float typicalCapHeight,
                                   float typicalXHeight,
                                   float italicAngle);


}
