/*
 * Copyright (c) 1998, 2003, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.awt.im.spi;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodRequests;
import java.awt.im.spi.InputMethod;
import java.text.AttributedCharacterIterator;

/**
 * Provides methods that input methods
 * can use to communicate with their client components or to request
 * other services.  This interface is implemented by the input method
 * framework, and input methods call its methods on the instance they
 * receive through
 * {@link InputMethod#setInputMethodContext}.
 * There should be no other implementors or callers.
 *
 * @since 1.3
 *
 * @author JavaSoft International
 */

public interface InputMethodContext extends InputMethodRequests {

    /**
     * Creates an input method event from the arguments given
     * and dispatches it to the client component. For arguments,
     * see {@link java.awt.event.InputMethodEvent#InputMethodEvent}.
     */
    public void dispatchInputMethodEvent(int id,
                AttributedCharacterIterator text, int committedCharacterCount,
                TextHitInfo caret, TextHitInfo visiblePosition);

    /**
     * Creates a top-level window for use by the input method.
     * The intended behavior of this window is:
     * <ul>
     * <li>it floats above all document windows and dialogs
     * <li>it and all components that it contains do not receive the focus
     * <li>it has lightweight decorations, such as a reduced drag region without title
     * </ul>
     * However, the actual behavior with respect to these three items is platform dependent.
     * <p>
     * The title may or may not be displayed, depending on the actual type of window created.
     * <p>
     * If attachToInputContext is true, the new window will share the input context that
     * corresponds to this input method context, so that events for components in the window
     * are automatically dispatched to the input method.
     * Also, when the window is opened using setVisible(true), the input context will prevent
     * deactivate and activate calls to the input method that might otherwise be caused.
     * <p>
     * Input methods must call {@link Window#dispose() Window.dispose} on the
     * returned input method window when it is no longer needed.
     * <p>
     * @param title the title to be displayed in the window's title bar,
     *              if there is such a title bar.
     *              A <code>null</code> value is treated as an empty string, "".
     * @param attachToInputContext whether this window should share the input context
     *              that corresponds to this input method context
     * @return a window with special characteristics for use by input methods
     * @exception HeadlessException if <code>GraphicsEnvironment.isHeadless
     *              </code> returns <code>true</code>
     */
    public Window createInputMethodWindow(String title, boolean attachToInputContext);

    /**
     * Creates a top-level Swing JFrame for use by the input method.
     * The intended behavior of this window is:
     * <ul>
     * <li>it floats above all document windows and dialogs
     * <li>it and all components that it contains do not receive the focus
     * <li>it has lightweight decorations, such as a reduced drag region without title
     * </ul>
     * However, the actual behavior with respect to these three items is platform dependent.
     * <p>
     * The title may or may not be displayed, depending on the actual type of window created.
     * <p>
     * If attachToInputContext is true, the new window will share the input context that
     * corresponds to this input method context, so that events for components in the window
     * are automatically dispatched to the input method.
     * Also, when the window is opened using setVisible(true), the input context will prevent
     * deactivate and activate calls to the input method that might otherwise be caused.
     * <p>
     * Input methods must call {@link Window#dispose() Window.dispose} on the
     * returned input method window when it is no longer needed.
     * <p>
     * @param title the title to be displayed in the window's title bar,
     *              if there is such a title bar.
     *              A <code>null</code> value is treated as an empty string, "".
     * @param attachToInputContext whether this window should share the input context
     *              that corresponds to this input method context
     * @return a JFrame with special characteristics for use by input methods
     * @exception HeadlessException if <code>GraphicsEnvironment.isHeadless
     *              </code> returns <code>true</code>
     *
     * @since 1.4
     */
    public JFrame createInputMethodJFrame(String title, boolean attachToInputContext);

    /**
     * Enables or disables notification of the current client window's
     * location and state for the specified input method. When
     * notification is enabled, the input method's {@link
     * InputMethod#notifyClientWindowChange
     * notifyClientWindowChange} method is called as described in that
     * method's specification. Notification is automatically disabled
     * when the input method is disposed.
     *
     * @param inputMethod the input method for which notifications are
     * enabled or disabled
     * @param enable true to enable, false to disable
     */
    public void enableClientWindowNotification(InputMethod inputMethod, boolean enable);
}
