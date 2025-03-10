/*
 * Copyright (c) 1997, 1998, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.AlphaComposite;
import java.awt.CompositeContext;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

/**
 * The <code>Composite</code> interface, along with
 * {@link CompositeContext}, defines the methods to compose a draw
 * primitive with the underlying graphics area.
 * After the <code>Composite</code> is set in the
 * {@link Graphics2D} context, it combines a shape, text, or an image
 * being rendered with the colors that have already been rendered
 * according to pre-defined rules. The classes
 * implementing this interface provide the rules and a method to create
 * the context for a particular operation.
 * <code>CompositeContext</code> is an environment used by the
 * compositing operation, which is created by the <code>Graphics2D</code>
 * prior to the start of the operation.  <code>CompositeContext</code>
 * contains private information and resources needed for a compositing
 * operation.  When the <code>CompositeContext</code> is no longer needed,
 * the <code>Graphics2D</code> object disposes of it in order to reclaim
 * resources allocated for the operation.
 * <p>
 * Instances of classes implementing <code>Composite</code> must be
 * immutable because the <code>Graphics2D</code> does not clone
 * these objects when they are set as an attribute with the
 * <code>setComposite</code> method or when the <code>Graphics2D</code>
 * object is cloned.  This is to avoid undefined rendering behavior of
 * <code>Graphics2D</code>, resulting from the modification of
 * the <code>Composite</code> object after it has been set in the
 * <code>Graphics2D</code> context.
 * <p>
 * Since this interface must expose the contents of pixels on the
 * target device or image to potentially arbitrary code, the use of
 * custom objects which implement this interface when rendering directly
 * to a screen device is governed by the <code>readDisplayPixels</code>
 * {@link AWTPermission}.  The permission check will occur when such
 * a custom object is passed to the <code>setComposite</code> method
 * of a <code>Graphics2D</code> retrieved from a {@link Component}.
 * @see AlphaComposite
 * @see CompositeContext
 * @see Graphics2D#setComposite
 */
public interface Composite {

    /**
     * Creates a context containing state that is used to perform
     * the compositing operation.  In a multi-threaded environment,
     * several contexts can exist simultaneously for a single
     * <code>Composite</code> object.
     * @param srcColorModel  the {@link ColorModel} of the source
     * @param dstColorModel  the <code>ColorModel</code> of the destination
     * @param hints the hint that the context object uses to choose between
     * rendering alternatives
     * @return the <code>CompositeContext</code> object used to perform the
     * compositing operation.
     */
    public CompositeContext createContext(ColorModel srcColorModel,
                                          ColorModel dstColorModel,
                                          RenderingHints hints);

}
