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

import java.awt.geom.AffineTransform;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * The <code>TransformAttribute</code> class provides an immutable
 * wrapper for a transform so that it is safe to use as an attribute.
 */
public final class TransformAttribute implements Serializable {

    /**
     * The <code>AffineTransform</code> for this
     * <code>TransformAttribute</code>, or <code>null</code>
     * if <code>AffineTransform</code> is the identity transform.
     */
    private AffineTransform transform;

    /**
     * Wraps the specified transform.  The transform is cloned and a
     * reference to the clone is kept.  The original transform is unchanged.
     * If null is passed as the argument, this constructor behaves as though
     * it were the identity transform.  (Note that it is preferable to use
     * {@link #IDENTITY} in this case.)
     * @param transform the specified {@link AffineTransform} to be wrapped,
     * or null.
     */
    public TransformAttribute(AffineTransform transform) {
        if (transform != null && !transform.isIdentity()) {
            this.transform = new AffineTransform(transform);
        }
    }

    /**
     * Returns a copy of the wrapped transform.
     * @return a <code>AffineTransform</code> that is a copy of the wrapped
     * transform of this <code>TransformAttribute</code>.
     */
    public AffineTransform getTransform() {
        AffineTransform at = transform;
        return (at == null) ? new AffineTransform() : new AffineTransform(at);
    }

    /**
     * Returns <code>true</code> if the wrapped transform is
     * an identity transform.
     * @return <code>true</code> if the wrapped transform is
     * an identity transform; <code>false</code> otherwise.
     * @since 1.4
     */
    public boolean isIdentity() {
        return transform == null;
    }

    /**
     * A <code>TransformAttribute</code> representing the identity transform.
     * @since 1.6
     */
    public static final java.awt.font.TransformAttribute IDENTITY = new java.awt.font.TransformAttribute(null);

    private void writeObject(java.io.ObjectOutputStream s)
      throws ClassNotFoundException,
             java.io.IOException
    {
        // sigh -- 1.3 expects transform is never null, so we need to always write one out
        if (this.transform == null) {
            this.transform = new AffineTransform();
        }
        s.defaultWriteObject();
    }

    /*
     * @since 1.6
     */
    private Object readResolve() throws ObjectStreamException {
        if (transform == null || transform.isIdentity()) {
            return IDENTITY;
        }
        return this;
    }

    // Added for serial backwards compatibility (4348425)
    static final long serialVersionUID = 3356247357827709530L;

    /**
     * @since 1.6
     */
    public int hashCode() {
        return transform == null ? 0 : transform.hashCode();
    }

    /**
     * Returns <code>true</code> if rhs is a <code>TransformAttribute</code>
     * whose transform is equal to this <code>TransformAttribute</code>'s
     * transform.
     * @param rhs the object to compare to
     * @return <code>true</code> if the argument is a <code>TransformAttribute</code>
     * whose transform is equal to this <code>TransformAttribute</code>'s
     * transform.
     * @since 1.6
     */
    public boolean equals(Object rhs) {
        if (rhs != null) {
            try {
                java.awt.font.TransformAttribute that = (java.awt.font.TransformAttribute)rhs;
                if (transform == null) {
                    return that.transform == null;
                }
                return transform.equals(that.transform);
            }
            catch (ClassCastException e) {
            }
        }
        return false;
    }
}
