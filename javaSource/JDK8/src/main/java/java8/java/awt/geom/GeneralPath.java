/*
 * Copyright (c) 1996, 2006, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.awt.geom;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

/**
 * The {@code GeneralPath} class represents a geometric path
 * constructed from straight lines, and quadratic and cubic
 * (B&eacute;zier) curves.  It can contain multiple subpaths.
 * <p>
 * {@code GeneralPath} is a legacy final class which exactly
 * implements the behavior of its superclass {@link Float}.
 * Together with {@link Double}, the {@link Path2D} classes
 * provide full implementations of a general geometric path that
 * support all of the functionality of the {@link Shape} and
 * {@link PathIterator} interfaces with the ability to explicitly
 * select different levels of internal coordinate precision.
 * <p>
 * Use {@code Path2D.Float} (or this legacy {@code GeneralPath}
 * subclass) when dealing with data that can be represented
 * and used with floating point precision.  Use {@code Path2D.Double}
 * for data that requires the accuracy or range of double precision.
 *
 * @author Jim Graham
 * @since 1.2
 */
public final class GeneralPath extends Path2D.Float {
    /**
     * Constructs a new empty single precision {@code GeneralPath} object
     * with a default winding rule of {@link #WIND_NON_ZERO}.
     *
     * @since 1.2
     */
    public GeneralPath() {
        super(WIND_NON_ZERO, INIT_SIZE);
    }

    /**
     * Constructs a new <code>GeneralPath</code> object with the specified
     * winding rule to control operations that require the interior of the
     * path to be defined.
     *
     * @param rule the winding rule
     * @see #WIND_EVEN_ODD
     * @see #WIND_NON_ZERO
     * @since 1.2
     */
    public GeneralPath(int rule) {
        super(rule, INIT_SIZE);
    }

    /**
     * Constructs a new <code>GeneralPath</code> object with the specified
     * winding rule and the specified initial capacity to store path
     * coordinates.
     * This number is an initial guess as to how many path segments
     * will be added to the path, but the storage is expanded as
     * needed to store whatever path segments are added.
     *
     * @param rule the winding rule
     * @param initialCapacity the estimate for the number of path segments
     *                        in the path
     * @see #WIND_EVEN_ODD
     * @see #WIND_NON_ZERO
     * @since 1.2
     */
    public GeneralPath(int rule, int initialCapacity) {
        super(rule, initialCapacity);
    }

    /**
     * Constructs a new <code>GeneralPath</code> object from an arbitrary
     * {@link Shape} object.
     * All of the initial geometry and the winding rule for this path are
     * taken from the specified <code>Shape</code> object.
     *
     * @param s the specified <code>Shape</code> object
     * @since 1.2
     */
    public GeneralPath(Shape s) {
        super(s, null);
    }

    GeneralPath(int windingRule,
                byte[] pointTypes,
                int numTypes,
                float[] pointCoords,
                int numCoords)
    {
        // used to construct from native

        this.windingRule = windingRule;
        this.pointTypes = pointTypes;
        this.numTypes = numTypes;
        this.floatCoords = pointCoords;
        this.numCoords = numCoords;
    }

    /*
     * JDK 1.6 serialVersionUID
     */
    private static final long serialVersionUID = -8327096662768731142L;
}
