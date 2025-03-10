/*
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.io.ObjectStreamException;
import java.lang.annotation.Native;

/**
 * A class to encapsulate symbolic colors representing the color of
 * native GUI objects on a system.  For systems which support the dynamic
 * update of the system colors (when the user changes the colors)
 * the actual RGB values of these symbolic colors will also change
 * dynamically.  In order to compare the "current" RGB value of a
 * <code>SystemColor</code> object with a non-symbolic Color object,
 * <code>getRGB</code> should be used rather than <code>equals</code>.
 * <p>
 * Note that the way in which these system colors are applied to GUI objects
 * may vary slightly from platform to platform since GUI objects may be
 * rendered differently on each platform.
 * <p>
 * System color values may also be available through the <code>getDesktopProperty</code>
 * method on <code>java.awt.Toolkit</code>.
 *
 * @see Toolkit#getDesktopProperty
 *
 * @author      Carl Quinn
 * @author      Amy Fowler
 */
public final class SystemColor extends Color implements java.io.Serializable {

   /**
     * The array index for the
     * {@link #desktop} system color.
     * @see java.awt.SystemColor#desktop
     */
    @Native public final static int DESKTOP = 0;

    /**
     * The array index for the
     * {@link #activeCaption} system color.
     * @see java.awt.SystemColor#activeCaption
     */
    @Native public final static int ACTIVE_CAPTION = 1;

    /**
     * The array index for the
     * {@link #activeCaptionText} system color.
     * @see java.awt.SystemColor#activeCaptionText
     */
    @Native public final static int ACTIVE_CAPTION_TEXT = 2;

    /**
     * The array index for the
     * {@link #activeCaptionBorder} system color.
     * @see java.awt.SystemColor#activeCaptionBorder
     */
    @Native public final static int ACTIVE_CAPTION_BORDER = 3;

    /**
     * The array index for the
     * {@link #inactiveCaption} system color.
     * @see java.awt.SystemColor#inactiveCaption
     */
    @Native public final static int INACTIVE_CAPTION = 4;

    /**
     * The array index for the
     * {@link #inactiveCaptionText} system color.
     * @see java.awt.SystemColor#inactiveCaptionText
     */
    @Native public final static int INACTIVE_CAPTION_TEXT = 5;

    /**
     * The array index for the
     * {@link #inactiveCaptionBorder} system color.
     * @see java.awt.SystemColor#inactiveCaptionBorder
     */
    @Native public final static int INACTIVE_CAPTION_BORDER = 6;

    /**
     * The array index for the
     * {@link #window} system color.
     * @see java.awt.SystemColor#window
     */
    @Native public final static int WINDOW = 7;

    /**
     * The array index for the
     * {@link #windowBorder} system color.
     * @see java.awt.SystemColor#windowBorder
     */
    @Native public final static int WINDOW_BORDER = 8;

    /**
     * The array index for the
     * {@link #windowText} system color.
     * @see java.awt.SystemColor#windowText
     */
    @Native public final static int WINDOW_TEXT = 9;

    /**
     * The array index for the
     * {@link #menu} system color.
     * @see java.awt.SystemColor#menu
     */
    @Native public final static int MENU = 10;

    /**
     * The array index for the
     * {@link #menuText} system color.
     * @see java.awt.SystemColor#menuText
     */
    @Native public final static int MENU_TEXT = 11;

    /**
     * The array index for the
     * {@link #text} system color.
     * @see java.awt.SystemColor#text
     */
    @Native public final static int TEXT = 12;

    /**
     * The array index for the
     * {@link #textText} system color.
     * @see java.awt.SystemColor#textText
     */
    @Native public final static int TEXT_TEXT = 13;

    /**
     * The array index for the
     * {@link #textHighlight} system color.
     * @see java.awt.SystemColor#textHighlight
     */
    @Native public final static int TEXT_HIGHLIGHT = 14;

    /**
     * The array index for the
     * {@link #textHighlightText} system color.
     * @see java.awt.SystemColor#textHighlightText
     */
    @Native public final static int TEXT_HIGHLIGHT_TEXT = 15;

    /**
     * The array index for the
     * {@link #textInactiveText} system color.
     * @see java.awt.SystemColor#textInactiveText
     */
    @Native public final static int TEXT_INACTIVE_TEXT = 16;

    /**
     * The array index for the
     * {@link #control} system color.
     * @see java.awt.SystemColor#control
     */
    @Native public final static int CONTROL = 17;

    /**
     * The array index for the
     * {@link #controlText} system color.
     * @see java.awt.SystemColor#controlText
     */
    @Native public final static int CONTROL_TEXT = 18;

    /**
     * The array index for the
     * {@link #controlHighlight} system color.
     * @see java.awt.SystemColor#controlHighlight
     */
    @Native public final static int CONTROL_HIGHLIGHT = 19;

    /**
     * The array index for the
     * {@link #controlLtHighlight} system color.
     * @see java.awt.SystemColor#controlLtHighlight
     */
    @Native public final static int CONTROL_LT_HIGHLIGHT = 20;

    /**
     * The array index for the
     * {@link #controlShadow} system color.
     * @see java.awt.SystemColor#controlShadow
     */
    @Native public final static int CONTROL_SHADOW = 21;

    /**
     * The array index for the
     * {@link #controlDkShadow} system color.
     * @see java.awt.SystemColor#controlDkShadow
     */
    @Native public final static int CONTROL_DK_SHADOW = 22;

    /**
     * The array index for the
     * {@link #scrollbar} system color.
     * @see java.awt.SystemColor#scrollbar
     */
    @Native public final static int SCROLLBAR = 23;

    /**
     * The array index for the
     * {@link #info} system color.
     * @see java.awt.SystemColor#info
     */
    @Native public final static int INFO = 24;

    /**
     * The array index for the
     * {@link #infoText} system color.
     * @see java.awt.SystemColor#infoText
     */
    @Native public final static int INFO_TEXT = 25;

    /**
     * The number of system colors in the array.
     */
    @Native public final static int NUM_COLORS = 26;

    /******************************************************************************************/

    /*
     * System colors with default initial values, overwritten by toolkit if
     * system values differ and are available.
     * Should put array initialization above first field that is using
     * SystemColor constructor to initialize.
     */
    private static int[] systemColors = {
        0xFF005C5C,  // desktop = new Color(0,92,92);
        0xFF000080,  // activeCaption = new Color(0,0,128);
        0xFFFFFFFF,  // activeCaptionText = Color.white;
        0xFFC0C0C0,  // activeCaptionBorder = Color.lightGray;
        0xFF808080,  // inactiveCaption = Color.gray;
        0xFFC0C0C0,  // inactiveCaptionText = Color.lightGray;
        0xFFC0C0C0,  // inactiveCaptionBorder = Color.lightGray;
        0xFFFFFFFF,  // window = Color.white;
        0xFF000000,  // windowBorder = Color.black;
        0xFF000000,  // windowText = Color.black;
        0xFFC0C0C0,  // menu = Color.lightGray;
        0xFF000000,  // menuText = Color.black;
        0xFFC0C0C0,  // text = Color.lightGray;
        0xFF000000,  // textText = Color.black;
        0xFF000080,  // textHighlight = new Color(0,0,128);
        0xFFFFFFFF,  // textHighlightText = Color.white;
        0xFF808080,  // textInactiveText = Color.gray;
        0xFFC0C0C0,  // control = Color.lightGray;
        0xFF000000,  // controlText = Color.black;
        0xFFFFFFFF,  // controlHighlight = Color.white;
        0xFFE0E0E0,  // controlLtHighlight = new Color(224,224,224);
        0xFF808080,  // controlShadow = Color.gray;
        0xFF000000,  // controlDkShadow = Color.black;
        0xFFE0E0E0,  // scrollbar = new Color(224,224,224);
        0xFFE0E000,  // info = new Color(224,224,0);
        0xFF000000,  // infoText = Color.black;
    };

   /**
     * The color rendered for the background of the desktop.
     */
    public final static java.awt.SystemColor desktop = new java.awt.SystemColor((byte)DESKTOP);

    /**
     * The color rendered for the window-title background of the currently active window.
     */
    public final static java.awt.SystemColor activeCaption = new java.awt.SystemColor((byte)ACTIVE_CAPTION);

    /**
     * The color rendered for the window-title text of the currently active window.
     */
    public final static java.awt.SystemColor activeCaptionText = new java.awt.SystemColor((byte)ACTIVE_CAPTION_TEXT);

    /**
     * The color rendered for the border around the currently active window.
     */
    public final static java.awt.SystemColor activeCaptionBorder = new java.awt.SystemColor((byte)ACTIVE_CAPTION_BORDER);

    /**
     * The color rendered for the window-title background of inactive windows.
     */
    public final static java.awt.SystemColor inactiveCaption = new java.awt.SystemColor((byte)INACTIVE_CAPTION);

    /**
     * The color rendered for the window-title text of inactive windows.
     */
    public final static java.awt.SystemColor inactiveCaptionText = new java.awt.SystemColor((byte)INACTIVE_CAPTION_TEXT);

    /**
     * The color rendered for the border around inactive windows.
     */
    public final static java.awt.SystemColor inactiveCaptionBorder = new java.awt.SystemColor((byte)INACTIVE_CAPTION_BORDER);

    /**
     * The color rendered for the background of interior regions inside windows.
     */
    public final static java.awt.SystemColor window = new java.awt.SystemColor((byte)WINDOW);

    /**
     * The color rendered for the border around interior regions inside windows.
     */
    public final static java.awt.SystemColor windowBorder = new java.awt.SystemColor((byte)WINDOW_BORDER);

    /**
     * The color rendered for text of interior regions inside windows.
     */
    public final static java.awt.SystemColor windowText = new java.awt.SystemColor((byte)WINDOW_TEXT);

    /**
     * The color rendered for the background of menus.
     */
    public final static java.awt.SystemColor menu = new java.awt.SystemColor((byte)MENU);

    /**
     * The color rendered for the text of menus.
     */
    public final static java.awt.SystemColor menuText = new java.awt.SystemColor((byte)MENU_TEXT);

    /**
     * The color rendered for the background of text control objects, such as
     * textfields and comboboxes.
     */
    public final static java.awt.SystemColor text = new java.awt.SystemColor((byte)TEXT);

    /**
     * The color rendered for the text of text control objects, such as textfields
     * and comboboxes.
     */
    public final static java.awt.SystemColor textText = new java.awt.SystemColor((byte)TEXT_TEXT);

    /**
     * The color rendered for the background of selected items, such as in menus,
     * comboboxes, and text.
     */
    public final static java.awt.SystemColor textHighlight = new java.awt.SystemColor((byte)TEXT_HIGHLIGHT);

    /**
     * The color rendered for the text of selected items, such as in menus, comboboxes,
     * and text.
     */
    public final static java.awt.SystemColor textHighlightText = new java.awt.SystemColor((byte)TEXT_HIGHLIGHT_TEXT);

    /**
     * The color rendered for the text of inactive items, such as in menus.
     */
    public final static java.awt.SystemColor textInactiveText = new java.awt.SystemColor((byte)TEXT_INACTIVE_TEXT);

    /**
     * The color rendered for the background of control panels and control objects,
     * such as pushbuttons.
     */
    public final static java.awt.SystemColor control = new java.awt.SystemColor((byte)CONTROL);

    /**
     * The color rendered for the text of control panels and control objects,
     * such as pushbuttons.
     */
    public final static java.awt.SystemColor controlText = new java.awt.SystemColor((byte)CONTROL_TEXT);

    /**
     * The color rendered for light areas of 3D control objects, such as pushbuttons.
     * This color is typically derived from the <code>control</code> background color
     * to provide a 3D effect.
     */
    public final static java.awt.SystemColor controlHighlight = new java.awt.SystemColor((byte)CONTROL_HIGHLIGHT);

    /**
     * The color rendered for highlight areas of 3D control objects, such as pushbuttons.
     * This color is typically derived from the <code>control</code> background color
     * to provide a 3D effect.
     */
    public final static java.awt.SystemColor controlLtHighlight = new java.awt.SystemColor((byte)CONTROL_LT_HIGHLIGHT);

    /**
     * The color rendered for shadow areas of 3D control objects, such as pushbuttons.
     * This color is typically derived from the <code>control</code> background color
     * to provide a 3D effect.
     */
    public final static java.awt.SystemColor controlShadow = new java.awt.SystemColor((byte)CONTROL_SHADOW);

    /**
     * The color rendered for dark shadow areas on 3D control objects, such as pushbuttons.
     * This color is typically derived from the <code>control</code> background color
     * to provide a 3D effect.
     */
    public final static java.awt.SystemColor controlDkShadow = new java.awt.SystemColor((byte)CONTROL_DK_SHADOW);

    /**
     * The color rendered for the background of scrollbars.
     */
    public final static java.awt.SystemColor scrollbar = new java.awt.SystemColor((byte)SCROLLBAR);

    /**
     * The color rendered for the background of tooltips or spot help.
     */
    public final static java.awt.SystemColor info = new java.awt.SystemColor((byte)INFO);

    /**
     * The color rendered for the text of tooltips or spot help.
     */
    public final static java.awt.SystemColor infoText = new java.awt.SystemColor((byte)INFO_TEXT);

    /*
     * JDK 1.1 serialVersionUID.
     */
    private static final long serialVersionUID = 4503142729533789064L;

    /*
     * An index into either array of SystemColor objects or values.
     */
    private transient int index;

    private static java.awt.SystemColor systemColorObjects [] = {
        java.awt.SystemColor.desktop,
        java.awt.SystemColor.activeCaption,
        java.awt.SystemColor.activeCaptionText,
        java.awt.SystemColor.activeCaptionBorder,
        java.awt.SystemColor.inactiveCaption,
        java.awt.SystemColor.inactiveCaptionText,
        java.awt.SystemColor.inactiveCaptionBorder,
        java.awt.SystemColor.window,
        java.awt.SystemColor.windowBorder,
        java.awt.SystemColor.windowText,
        java.awt.SystemColor.menu,
        java.awt.SystemColor.menuText,
        java.awt.SystemColor.text,
        java.awt.SystemColor.textText,
        java.awt.SystemColor.textHighlight,
        java.awt.SystemColor.textHighlightText,
        java.awt.SystemColor.textInactiveText,
        java.awt.SystemColor.control,
        java.awt.SystemColor.controlText,
        java.awt.SystemColor.controlHighlight,
        java.awt.SystemColor.controlLtHighlight,
        java.awt.SystemColor.controlShadow,
        java.awt.SystemColor.controlDkShadow,
        java.awt.SystemColor.scrollbar,
        java.awt.SystemColor.info,
        java.awt.SystemColor.infoText
    };

    static {
      updateSystemColors();
    }

    /**
     * Called from {@code <init>} and toolkit to update the above systemColors cache.
     */
    private static void updateSystemColors() {
        if (!GraphicsEnvironment.isHeadless()) {
            Toolkit.getDefaultToolkit().loadSystemColors(systemColors);
        }
        for (int i = 0; i < systemColors.length; i++) {
            systemColorObjects[i].value = systemColors[i];
        }
    }

    /**
     * Creates a symbolic color that represents an indexed entry into system
     * color cache. Used by above static system colors.
     */
    private SystemColor(byte index) {
        super(systemColors[index]);
        this.index = index;
    }

    /**
     * Returns a string representation of this <code>Color</code>'s values.
     * This method is intended to be used only for debugging purposes,
     * and the content and format of the returned string may vary between
     * implementations.
     * The returned string may be empty but may not be <code>null</code>.
     *
     * @return  a string representation of this <code>Color</code>
     */
    public String toString() {
        return getClass().getName() + "[i=" + (index) + "]";
    }

    /**
     * The design of the {@code SystemColor} class assumes that
     * the {@code SystemColor} object instances stored in the
     * static final fields above are the only instances that can
     * be used by developers.
     * This method helps maintain those limits on instantiation
     * by using the index stored in the value field of the
     * serialized form of the object to replace the serialized
     * object with the equivalent static object constant field
     * of {@code SystemColor}.
     * See the {@link #writeReplace} method for more information
     * on the serialized form of these objects.
     * @return one of the {@code SystemColor} static object
     *         fields that refers to the same system color.
     */
    private Object readResolve() {
        // The instances of SystemColor are tightly controlled and
        // only the canonical instances appearing above as static
        // constants are allowed.  The serial form of SystemColor
        // objects stores the color index as the value.  Here we
        // map that index back into the canonical instance.
        return systemColorObjects[value];
    }

    /**
     * Returns a specialized version of the {@code SystemColor}
     * object for writing to the serialized stream.
     * @serialData
     * The value field of a serialized {@code SystemColor} object
     * contains the array index of the system color instead of the
     * rgb data for the system color.
     * This index is used by the {@link #readResolve} method to
     * resolve the deserialized objects back to the original
     * static constant versions to ensure unique instances of
     * each {@code SystemColor} object.
     * @return a proxy {@code SystemColor} object with its value
     *         replaced by the corresponding system color index.
     */
    private Object writeReplace() throws ObjectStreamException
    {
        // we put an array index in the SystemColor.value while serialize
        // to keep compatibility.
        java.awt.SystemColor color = new java.awt.SystemColor((byte)index);
        color.value = index;
        return color;
    }
}
