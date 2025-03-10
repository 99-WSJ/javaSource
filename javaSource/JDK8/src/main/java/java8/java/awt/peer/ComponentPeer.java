/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.awt.peer;

import sun.awt.CausedFocusEvent;
import sun.java2d.pipe.Region;

import java.awt.*;
import java.awt.event.PaintEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ContainerPeer;
import java.awt.peer.MenuComponentPeer;


/**
 * The peer interface for {@link Component}. This is the top level peer
 * interface for widgets and defines the bulk of methods for AWT component
 * peers. Most component peers have to implement this interface (via one
 * of the subinterfaces), except menu components, which implement
 * {@link MenuComponentPeer}.
 *
 * The peer interfaces are intended only for use in porting
 * the AWT. They are not intended for use by application
 * developers, and developers should not implement peers
 * nor invoke any of the peer methods directly on the peer
 * instances.
 */
public interface ComponentPeer {

    /**
     * Operation for {@link #setBounds(int, int, int, int, int)}, indicating
     * a change in the component location only.
     *
     * @see #setBounds(int, int, int, int, int)
     */
    public static final int SET_LOCATION = 1;

    /**
     * Operation for {@link #setBounds(int, int, int, int, int)}, indicating
     * a change in the component size only.
     *
     * @see #setBounds(int, int, int, int, int)
     */
    public static final int SET_SIZE = 2;

    /**
     * Operation for {@link #setBounds(int, int, int, int, int)}, indicating
     * a change in the component size and location.
     *
     * @see #setBounds(int, int, int, int, int)
     */
    public static final int SET_BOUNDS = 3;

    /**
     * Operation for {@link #setBounds(int, int, int, int, int)}, indicating
     * a change in the component client size. This is used for setting
     * the 'inside' size of windows, without the border insets.
     *
     * @see #setBounds(int, int, int, int, int)
     */
    public static final int SET_CLIENT_SIZE = 4;

    /**
     * Resets the setBounds() operation to DEFAULT_OPERATION. This is not
     * passed into {@link #setBounds(int, int, int, int, int)}.
     *
     * TODO: This is only used internally and should probably be moved outside
     *       the peer interface.
     *
     * @see Component#setBoundsOp
     */
    public static final int RESET_OPERATION = 5;

    /**
     * A flag that is used to suppress checks for embedded frames.
     *
     * TODO: This is only used internally and should probably be moved outside
     *       the peer interface.
     */
    public static final int NO_EMBEDDED_CHECK = (1 << 14);

    /**
     * The default operation, which is to set size and location.
     *
     * TODO: This is only used internally and should probably be moved outside
     *       the peer interface.
     *
     * @see Component#setBoundsOp
     */
    public static final int DEFAULT_OPERATION = SET_BOUNDS;

    /**
     * Determines if a component has been obscured, i.e. by an overlapping
     * window or similar. This is used by JViewport for optimizing performance.
     * This doesn't have to be implemented, when
     * {@link #canDetermineObscurity()} returns {@code false}.
     *
     * @return {@code true} when the component has been obscured,
     *         {@code false} otherwise
     *
     * @see #canDetermineObscurity()
     * @see javax.swing.JViewport#needsRepaintAfterBlit
     */
    boolean isObscured();

    /**
     * Returns {@code true} when the peer can determine if a component
     * has been obscured, {@code false} false otherwise.
     *
     * @return {@code true} when the peer can determine if a component
     *         has been obscured, {@code false} false otherwise
     *
     * @see #isObscured()
     * @see javax.swing.JViewport#needsRepaintAfterBlit
     */
    boolean canDetermineObscurity();

    /**
     * Makes a component visible or invisible.
     *
     * @param v {@code true} to make a component visible,
     *          {@code false} to make it invisible
     *
     * @see Component#setVisible(boolean)
     */
    void setVisible(boolean v);

    /**
     * Enables or disables a component. Disabled components are usually grayed
     * out and cannot be activated.
     *
     * @param e {@code true} to enable the component, {@code false}
     *          to disable it
     *
     * @see Component#setEnabled(boolean)
     */
    void setEnabled(boolean e);

    /**
     * Paints the component to the specified graphics context. This is called
     * by {@link Component#paintAll(Graphics)} to paint the component.
     *
     * @param g the graphics context to paint to
     *
     * @see Component#paintAll(Graphics)
     */
    void paint(Graphics g);

    /**
     * Prints the component to the specified graphics context. This is called
     * by {@link Component#printAll(Graphics)} to print the component.
     *
     * @param g the graphics context to print to
     *
     * @see Component#printAll(Graphics)
     */
    void print(Graphics g);

    /**
     * Sets the location or size or both of the component. The location is
     * specified relative to the component's parent. The {@code op}
     * parameter specifies which properties change. If it is
     * {@link #SET_LOCATION}, then only the location changes (and the size
     * parameters can be ignored). If {@code op} is {@link #SET_SIZE},
     * then only the size changes (and the location can be ignored). If
     * {@code op} is {@link #SET_BOUNDS}, then both change. There is a
     * special value {@link #SET_CLIENT_SIZE}, which is used only for
     * window-like components to set the size of the client (i.e. the 'inner'
     * size, without the insets of the window borders).
     *
     * @param x the X location of the component
     * @param y the Y location of the component
     * @param width the width of the component
     * @param height the height of the component
     * @param op the operation flag
     *
     * @see #SET_BOUNDS
     * @see #SET_LOCATION
     * @see #SET_SIZE
     * @see #SET_CLIENT_SIZE
     */
    void setBounds(int x, int y, int width, int height, int op);

    /**
     * Called to let the component peer handle events.
     *
     * @param e the AWT event to handle
     *
     * @see Component#dispatchEvent(AWTEvent)
     */
    void handleEvent(AWTEvent e);

    /**
     * Called to coalesce paint events.
     *
     * @param e the paint event to consider to coalesce
     *
     * @see EventQueue#coalescePaintEvent
     */
    void coalescePaintEvent(PaintEvent e);

    /**
     * Determines the location of the component on the screen.
     *
     * @return the location of the component on the screen
     *
     * @see Component#getLocationOnScreen()
     */
    Point getLocationOnScreen();

    /**
     * Determines the preferred size of the component.
     *
     * @return the preferred size of the component
     *
     * @see Component#getPreferredSize()
     */
    Dimension getPreferredSize();

    /**
     * Determines the minimum size of the component.
     *
     * @return the minimum size of the component
     *
     * @see Component#getMinimumSize()
     */
    Dimension getMinimumSize();

    /**
     * Returns the color model used by the component.
     *
     * @return the color model used by the component
     *
     * @see Component#getColorModel()
     */
    ColorModel getColorModel();

    /**
     * Returns a graphics object to paint on the component.
     *
     * @return a graphics object to paint on the component
     *
     * @see Component#getGraphics()
     */
    // TODO: Maybe change this to force Graphics2D, since many things will
    // break with plain Graphics nowadays.
    Graphics getGraphics();

    /**
     * Returns a font metrics object to determine the metrics properties of
     * the specified font.
     *
     * @param font the font to determine the metrics for
     *
     * @return a font metrics object to determine the metrics properties of
     *         the specified font
     *
     * @see Component#getFontMetrics(Font)
     */
    FontMetrics getFontMetrics(Font font);

    /**
     * Disposes all resources held by the component peer. This is called
     * when the component has been disconnected from the component hierarchy
     * and is about to be garbage collected.
     *
     * @see Component#removeNotify()
     */
    void dispose();

    /**
     * Sets the foreground color of this component.
     *
     * @param c the foreground color to set
     *
     * @see Component#setForeground(Color)
     */
    void setForeground(Color c);

    /**
     * Sets the background color of this component.
     *
     * @param c the background color to set
     *
     * @see Component#setBackground(Color)
     */
    void setBackground(Color c);

    /**
     * Sets the font of this component.
     *
     * @param f the font of this component
     *
     * @see Component#setFont(Font)
     */
    void setFont(Font f);

    /**
     * Updates the cursor of the component.
     *
     * @see Component#updateCursorImmediately
     */
    void updateCursorImmediately();

    /**
     * Requests focus on this component.
     *
     * @param lightweightChild the actual lightweight child that requests the
     *        focus
     * @param temporary {@code true} if the focus change is temporary,
     *        {@code false} otherwise
     * @param focusedWindowChangeAllowed {@code true} if changing the
     *        focus of the containing window is allowed or not
     * @param time the time of the focus change request
     * @param cause the cause of the focus change request
     *
     * @return {@code true} if the focus change is guaranteed to be
     *         granted, {@code false} otherwise
     */
    boolean requestFocus(Component lightweightChild, boolean temporary,
                         boolean focusedWindowChangeAllowed, long time,
                         CausedFocusEvent.Cause cause);

    /**
     * Returns {@code true} when the component takes part in the focus
     * traversal, {@code false} otherwise.
     *
     * @return {@code true} when the component takes part in the focus
     *         traversal, {@code false} otherwise
     */
    boolean isFocusable();

    /**
     * Creates an image using the specified image producer.
     *
     * @param producer the image producer from which the image pixels will be
     *        produced
     *
     * @return the created image
     *
     * @see Component#createImage(ImageProducer)
     */
    Image createImage(ImageProducer producer);

    /**
     * Creates an empty image with the specified width and height. This is
     * generally used as a non-accelerated backbuffer for drawing onto the
     * component (e.g. by Swing).
     *
     * @param width the width of the image
     * @param height the height of the image
     *
     * @return the created image
     *
     * @see Component#createImage(int, int)
     */
    // TODO: Maybe make that return a BufferedImage, because some stuff will
    // break if a different kind of image is returned.
    Image createImage(int width, int height);

    /**
     * Creates an empty volatile image with the specified width and height.
     * This is generally used as an accelerated backbuffer for drawing onto
     * the component (e.g. by Swing).
     *
     * @param width the width of the image
     * @param height the height of the image
     *
     * @return the created volatile image
     *
     * @see Component#createVolatileImage(int, int)
     */
    // TODO: Include capabilities here and fix Component#createVolatileImage
    VolatileImage createVolatileImage(int width, int height);

    /**
     * Prepare the specified image for rendering on this component. This should
     * start loading the image (if not already loaded) and create an
     * appropriate screen representation.
     *
     * @param img the image to prepare
     * @param w the width of the screen representation
     * @param h the height of the screen representation
     * @param o an image observer to observe the progress
     *
     * @return {@code true} if the image is already fully prepared,
     *         {@code false} otherwise
     *
     * @see Component#prepareImage(Image, int, int, ImageObserver)
     */
    boolean prepareImage(Image img, int w, int h, ImageObserver o);

    /**
     * Determines the status of the construction of the screen representaion
     * of the specified image.
     *
     * @param img the image to check
     * @param w the target width
     * @param h the target height
     * @param o the image observer to notify
     *
     * @return the status as bitwise ORed ImageObserver flags
     *
     * @see Component#checkImage(Image, int, int, ImageObserver)
     */
    int checkImage(Image img, int w, int h, ImageObserver o);

    /**
     * Returns the graphics configuration that corresponds to this component.
     *
     * @return the graphics configuration that corresponds to this component
     *
     * @see Component#getGraphicsConfiguration()
     */
    GraphicsConfiguration getGraphicsConfiguration();

    /**
     * Determines if the component handles wheel scrolling itself. Otherwise
     * it is delegated to the component's parent.
     *
     * @return {@code true} if the component handles wheel scrolling,
     *         {@code false} otherwise
     *
     * @see Component#dispatchEventImpl(AWTEvent)
     */
    boolean handlesWheelScrolling();

    /**
     * Create {@code numBuffers} flipping buffers with the specified
     * buffer capabilities.
     *
     * @param numBuffers the number of buffers to create
     * @param caps the buffer capabilities
     *
     * @throws AWTException if flip buffering is not supported
     *
     * @see Component.FlipBufferStrategy#createBuffers
     */
    void createBuffers(int numBuffers, BufferCapabilities caps)
         throws AWTException;

    /**
     * Returns the back buffer as image.
     *
     * @return the back buffer as image
     *
     * @see Component.FlipBufferStrategy#getBackBuffer
     */
    Image getBackBuffer();

    /**
     * Move the back buffer to the front buffer.
     *
     * @param x1 the area to be flipped, upper left X coordinate
     * @param y1 the area to be flipped, upper left Y coordinate
     * @param x2 the area to be flipped, lower right X coordinate
     * @param y2 the area to be flipped, lower right Y coordinate
     * @param flipAction the flip action to perform
     *
     * @see Component.FlipBufferStrategy#flip
     */
    void flip(int x1, int y1, int x2, int y2, BufferCapabilities.FlipContents flipAction);

    /**
     * Destroys all created buffers.
     *
     * @see Component.FlipBufferStrategy#destroyBuffers
     */
    void destroyBuffers();

    /**
     * Reparents this peer to the new parent referenced by
     * {@code newContainer} peer. Implementation depends on toolkit and
     * container.
     *
     * @param newContainer peer of the new parent container
     *
     * @since 1.5
     */
    void reparent(ContainerPeer newContainer);

    /**
     * Returns whether this peer supports reparenting to another parent without
     * destroying the peer.
     *
     * @return true if appropriate reparent is supported, false otherwise
     *
     * @since 1.5
     */
    boolean isReparentSupported();

    /**
     * Used by lightweight implementations to tell a ComponentPeer to layout
     * its sub-elements.  For instance, a lightweight Checkbox needs to layout
     * the box, as well as the text label.
     *
     * @see Component#validate()
     */
    void layout();

    /**
     * Applies the shape to the native component window.
     * @since 1.7
     *
     * @see Component#applyCompoundShape
     */
    void applyShape(Region shape);

    /**
     * Lowers this component at the bottom of the above HW peer. If the above parameter
     * is null then the method places this component at the top of the Z-order.
     */
    void setZOrder(java.awt.peer.ComponentPeer above);

    /**
     * Updates internal data structures related to the component's GC.
     *
     * @return if the peer needs to be recreated for the changes to take effect
     * @since 1.7
     */
    boolean updateGraphicsData(GraphicsConfiguration gc);
}
