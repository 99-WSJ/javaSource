/*
 * Copyright (c) 2000, 2011, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.awt;

/**
 * Capabilities and properties of images.
 * @author Michael Martak
 * @since 1.4
 */
public class ImageCapabilities implements Cloneable {

    private boolean accelerated = false;

    /**
     * Creates a new object for specifying image capabilities.
     * @param accelerated whether or not an accelerated image is desired
     */
    public ImageCapabilities(boolean accelerated) {
        this.accelerated = accelerated;
    }

    /**
     * Returns <code>true</code> if the object whose capabilities are
     * encapsulated in this <code>ImageCapabilities</code> can be or is
     * accelerated.
     * @return whether or not an image can be, or is, accelerated.  There are
     * various platform-specific ways to accelerate an image, including
     * pixmaps, VRAM, AGP.  This is the general acceleration method (as
     * opposed to residing in system memory).
     */
    public boolean isAccelerated() {
        return accelerated;
    }

    /**
     * Returns <code>true</code> if the <code>VolatileImage</code>
     * described by this <code>ImageCapabilities</code> can lose
     * its surfaces.
     * @return whether or not a volatile image is subject to losing its surfaces
     * at the whim of the operating system.
     */
    public boolean isTrueVolatile() {
        return false;
    }

    /**
     * @return a copy of this ImageCapabilities object.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // Since we implement Cloneable, this should never happen
            throw new InternalError(e);
        }
    }

}
