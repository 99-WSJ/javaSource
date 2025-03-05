/*
 * Copyright (c) 2002, 2013, Oracle and/or its affiliates. All rights reserved.
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
package java8.javax.swing.plaf.synth;

import sun.awt.AppContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.UIDefaults;

/**
 * A distinct rendering area of a Swing component.  A component may
 * support one or more regions.  Specific component regions are defined
 * by the typesafe enumeration in this class.
 * <p>
 * Regions are typically used as a way to identify the <code>Component</code>s
 * and areas a particular style is to apply to. Synth's file format allows you
 * to bind styles based on the name of a <code>Region</code>.
 * The name is derived from the field name of the constant:
 * <ol>
 *  <li>Map all characters to lowercase.
 *  <li>Map the first character to uppercase.
 *  <li>Map the first character after underscores to uppercase.
 *  <li>Remove all underscores.
 * </ol>
 * For example, to identify the <code>SPLIT_PANE</code>
 * <code>Region</code> you would use <code>SplitPane</code>.
 * The following shows a custom <code>SynthStyleFactory</code>
 * that returns a specific style for split panes:
 * <pre>
 *    public SynthStyle getStyle(JComponent c, Region id) {
 *        if (id == Region.SPLIT_PANE) {
 *            return splitPaneStyle;
 *        }
 *        ...
 *    }
 * </pre>
 * The following <a href="doc-files/synthFileFormat.html">xml</a>
 * accomplishes the same thing:
 * <pre>
 * &lt;style id="splitPaneStyle"&gt;
 *   ...
 * &lt;/style&gt;
 * &lt;bind style="splitPaneStyle" type="region" key="SplitPane"/&gt;
 * </pre>
 *
 * @since 1.5
 * @author Scott Violet
 */
public class Region {
    private static final Object UI_TO_REGION_MAP_KEY = new Object();
    private static final Object LOWER_CASE_NAME_MAP_KEY = new Object();

    /**
     * ArrowButton's are special types of buttons that also render a
     * directional indicator, typically an arrow. ArrowButtons are used by
     * composite components, for example ScrollBar's contain ArrowButtons.
     * To bind a style to this <code>Region</code> use the name
     * <code>ArrowButton</code>.
     */
    public static final javax.swing.plaf.synth.Region ARROW_BUTTON = new javax.swing.plaf.synth.Region("ArrowButton", false);

    /**
     * Button region. To bind a style to this <code>Region</code> use the name
     * <code>Button</code>.
     */
    public static final javax.swing.plaf.synth.Region BUTTON = new javax.swing.plaf.synth.Region("Button", false);

    /**
     * CheckBox region. To bind a style to this <code>Region</code> use the name
     * <code>CheckBox</code>.
     */
    public static final javax.swing.plaf.synth.Region CHECK_BOX = new javax.swing.plaf.synth.Region("CheckBox", false);

    /**
     * CheckBoxMenuItem region. To bind a style to this <code>Region</code> use
     * the name <code>CheckBoxMenuItem</code>.
     */
    public static final javax.swing.plaf.synth.Region CHECK_BOX_MENU_ITEM = new javax.swing.plaf.synth.Region("CheckBoxMenuItem", false);

    /**
     * ColorChooser region. To bind a style to this <code>Region</code> use
     * the name <code>ColorChooser</code>.
     */
    public static final javax.swing.plaf.synth.Region COLOR_CHOOSER = new javax.swing.plaf.synth.Region("ColorChooser", false);

    /**
     * ComboBox region. To bind a style to this <code>Region</code> use
     * the name <code>ComboBox</code>.
     */
    public static final javax.swing.plaf.synth.Region COMBO_BOX = new javax.swing.plaf.synth.Region("ComboBox", false);

    /**
     * DesktopPane region. To bind a style to this <code>Region</code> use
     * the name <code>DesktopPane</code>.
     */
    public static final javax.swing.plaf.synth.Region DESKTOP_PANE = new javax.swing.plaf.synth.Region("DesktopPane", false);

    /**
     * DesktopIcon region. To bind a style to this <code>Region</code> use
     * the name <code>DesktopIcon</code>.
     */
    public static final javax.swing.plaf.synth.Region DESKTOP_ICON = new javax.swing.plaf.synth.Region("DesktopIcon", false);

    /**
     * EditorPane region. To bind a style to this <code>Region</code> use
     * the name <code>EditorPane</code>.
     */
    public static final javax.swing.plaf.synth.Region EDITOR_PANE = new javax.swing.plaf.synth.Region("EditorPane", false);

    /**
     * FileChooser region. To bind a style to this <code>Region</code> use
     * the name <code>FileChooser</code>.
     */
    public static final javax.swing.plaf.synth.Region FILE_CHOOSER = new javax.swing.plaf.synth.Region("FileChooser", false);

    /**
     * FormattedTextField region. To bind a style to this <code>Region</code> use
     * the name <code>FormattedTextField</code>.
     */
    public static final javax.swing.plaf.synth.Region FORMATTED_TEXT_FIELD = new javax.swing.plaf.synth.Region("FormattedTextField", false);

    /**
     * InternalFrame region. To bind a style to this <code>Region</code> use
     * the name <code>InternalFrame</code>.
     */
    public static final javax.swing.plaf.synth.Region INTERNAL_FRAME = new javax.swing.plaf.synth.Region("InternalFrame", false);

    /**
     * TitlePane of an InternalFrame. The TitlePane typically
     * shows a menu, title, widgets to manipulate the internal frame.
     * To bind a style to this <code>Region</code> use the name
     * <code>InternalFrameTitlePane</code>.
     */
    public static final javax.swing.plaf.synth.Region INTERNAL_FRAME_TITLE_PANE = new javax.swing.plaf.synth.Region("InternalFrameTitlePane", false);

    /**
     * Label region. To bind a style to this <code>Region</code> use the name
     * <code>Label</code>.
     */
    public static final javax.swing.plaf.synth.Region LABEL = new javax.swing.plaf.synth.Region("Label", false);

    /**
     * List region. To bind a style to this <code>Region</code> use the name
     * <code>List</code>.
     */
    public static final javax.swing.plaf.synth.Region LIST = new javax.swing.plaf.synth.Region("List", false);

    /**
     * Menu region. To bind a style to this <code>Region</code> use the name
     * <code>Menu</code>.
     */
    public static final javax.swing.plaf.synth.Region MENU = new javax.swing.plaf.synth.Region("Menu", false);

    /**
     * MenuBar region. To bind a style to this <code>Region</code> use the name
     * <code>MenuBar</code>.
     */
    public static final javax.swing.plaf.synth.Region MENU_BAR = new javax.swing.plaf.synth.Region("MenuBar", false);

    /**
     * MenuItem region. To bind a style to this <code>Region</code> use the name
     * <code>MenuItem</code>.
     */
    public static final javax.swing.plaf.synth.Region MENU_ITEM = new javax.swing.plaf.synth.Region("MenuItem", false);

    /**
     * Accelerator region of a MenuItem. To bind a style to this
     * <code>Region</code> use the name <code>MenuItemAccelerator</code>.
     */
    public static final javax.swing.plaf.synth.Region MENU_ITEM_ACCELERATOR = new javax.swing.plaf.synth.Region("MenuItemAccelerator", true);

    /**
     * OptionPane region. To bind a style to this <code>Region</code> use
     * the name <code>OptionPane</code>.
     */
    public static final javax.swing.plaf.synth.Region OPTION_PANE = new javax.swing.plaf.synth.Region("OptionPane", false);

    /**
     * Panel region. To bind a style to this <code>Region</code> use the name
     * <code>Panel</code>.
     */
    public static final javax.swing.plaf.synth.Region PANEL = new javax.swing.plaf.synth.Region("Panel", false);

    /**
     * PasswordField region. To bind a style to this <code>Region</code> use
     * the name <code>PasswordField</code>.
     */
    public static final javax.swing.plaf.synth.Region PASSWORD_FIELD = new javax.swing.plaf.synth.Region("PasswordField", false);

    /**
     * PopupMenu region. To bind a style to this <code>Region</code> use
     * the name <code>PopupMenu</code>.
     */
    public static final javax.swing.plaf.synth.Region POPUP_MENU = new javax.swing.plaf.synth.Region("PopupMenu", false);

    /**
     * PopupMenuSeparator region. To bind a style to this <code>Region</code>
     * use the name <code>PopupMenuSeparator</code>.
     */
    public static final javax.swing.plaf.synth.Region POPUP_MENU_SEPARATOR = new javax.swing.plaf.synth.Region("PopupMenuSeparator", false);

    /**
     * ProgressBar region. To bind a style to this <code>Region</code>
     * use the name <code>ProgressBar</code>.
     */
    public static final javax.swing.plaf.synth.Region PROGRESS_BAR = new javax.swing.plaf.synth.Region("ProgressBar", false);

    /**
     * RadioButton region. To bind a style to this <code>Region</code>
     * use the name <code>RadioButton</code>.
     */
    public static final javax.swing.plaf.synth.Region RADIO_BUTTON = new javax.swing.plaf.synth.Region("RadioButton", false);

    /**
     * RegionButtonMenuItem region. To bind a style to this <code>Region</code>
     * use the name <code>RadioButtonMenuItem</code>.
     */
    public static final javax.swing.plaf.synth.Region RADIO_BUTTON_MENU_ITEM = new javax.swing.plaf.synth.Region("RadioButtonMenuItem", false);

    /**
     * RootPane region. To bind a style to this <code>Region</code> use
     * the name <code>RootPane</code>.
     */
    public static final javax.swing.plaf.synth.Region ROOT_PANE = new javax.swing.plaf.synth.Region("RootPane", false);

    /**
     * ScrollBar region. To bind a style to this <code>Region</code> use
     * the name <code>ScrollBar</code>.
     */
    public static final javax.swing.plaf.synth.Region SCROLL_BAR = new javax.swing.plaf.synth.Region("ScrollBar", false);

    /**
     * Track of the ScrollBar. To bind a style to this <code>Region</code> use
     * the name <code>ScrollBarTrack</code>.
     */
    public static final javax.swing.plaf.synth.Region SCROLL_BAR_TRACK = new javax.swing.plaf.synth.Region("ScrollBarTrack", true);

    /**
     * Thumb of the ScrollBar. The thumb is the region of the ScrollBar
     * that gives a graphical depiction of what percentage of the View is
     * currently visible. To bind a style to this <code>Region</code> use
     * the name <code>ScrollBarThumb</code>.
     */
    public static final javax.swing.plaf.synth.Region SCROLL_BAR_THUMB = new javax.swing.plaf.synth.Region("ScrollBarThumb", true);

    /**
     * ScrollPane region. To bind a style to this <code>Region</code> use
     * the name <code>ScrollPane</code>.
     */
    public static final javax.swing.plaf.synth.Region SCROLL_PANE = new javax.swing.plaf.synth.Region("ScrollPane", false);

    /**
     * Separator region. To bind a style to this <code>Region</code> use
     * the name <code>Separator</code>.
     */
    public static final javax.swing.plaf.synth.Region SEPARATOR = new javax.swing.plaf.synth.Region("Separator", false);

    /**
     * Slider region. To bind a style to this <code>Region</code> use
     * the name <code>Slider</code>.
     */
    public static final javax.swing.plaf.synth.Region SLIDER = new javax.swing.plaf.synth.Region("Slider", false);

    /**
     * Track of the Slider. To bind a style to this <code>Region</code> use
     * the name <code>SliderTrack</code>.
     */
    public static final javax.swing.plaf.synth.Region SLIDER_TRACK = new javax.swing.plaf.synth.Region("SliderTrack", true);

    /**
     * Thumb of the Slider. The thumb of the Slider identifies the current
     * value. To bind a style to this <code>Region</code> use the name
     * <code>SliderThumb</code>.
     */
    public static final javax.swing.plaf.synth.Region SLIDER_THUMB = new javax.swing.plaf.synth.Region("SliderThumb", true);

    /**
     * Spinner region. To bind a style to this <code>Region</code> use the name
     * <code>Spinner</code>.
     */
    public static final javax.swing.plaf.synth.Region SPINNER = new javax.swing.plaf.synth.Region("Spinner", false);

    /**
     * SplitPane region. To bind a style to this <code>Region</code> use the name
     * <code>SplitPane</code>.
     */
    public static final javax.swing.plaf.synth.Region SPLIT_PANE = new javax.swing.plaf.synth.Region("SplitPane", false);

    /**
     * Divider of the SplitPane. To bind a style to this <code>Region</code>
     * use the name <code>SplitPaneDivider</code>.
     */
    public static final javax.swing.plaf.synth.Region SPLIT_PANE_DIVIDER = new javax.swing.plaf.synth.Region("SplitPaneDivider", true);

    /**
     * TabbedPane region. To bind a style to this <code>Region</code> use
     * the name <code>TabbedPane</code>.
     */
    public static final javax.swing.plaf.synth.Region TABBED_PANE = new javax.swing.plaf.synth.Region("TabbedPane", false);

    /**
     * Region of a TabbedPane for one tab. To bind a style to this
     * <code>Region</code> use the name <code>TabbedPaneTab</code>.
     */
    public static final javax.swing.plaf.synth.Region TABBED_PANE_TAB = new javax.swing.plaf.synth.Region("TabbedPaneTab", true);

    /**
     * Region of a TabbedPane containing the tabs. To bind a style to this
     * <code>Region</code> use the name <code>TabbedPaneTabArea</code>.
     */
    public static final javax.swing.plaf.synth.Region TABBED_PANE_TAB_AREA = new javax.swing.plaf.synth.Region("TabbedPaneTabArea", true);

    /**
     * Region of a TabbedPane containing the content. To bind a style to this
     * <code>Region</code> use the name <code>TabbedPaneContent</code>.
     */
    public static final javax.swing.plaf.synth.Region TABBED_PANE_CONTENT = new javax.swing.plaf.synth.Region("TabbedPaneContent", true);

    /**
     * Table region. To bind a style to this <code>Region</code> use
     * the name <code>Table</code>.
     */
    public static final javax.swing.plaf.synth.Region TABLE = new javax.swing.plaf.synth.Region("Table", false);

    /**
     * TableHeader region. To bind a style to this <code>Region</code> use
     * the name <code>TableHeader</code>.
     */
    public static final javax.swing.plaf.synth.Region TABLE_HEADER = new javax.swing.plaf.synth.Region("TableHeader", false);

    /**
     * TextArea region. To bind a style to this <code>Region</code> use
     * the name <code>TextArea</code>.
     */
    public static final javax.swing.plaf.synth.Region TEXT_AREA = new javax.swing.plaf.synth.Region("TextArea", false);

    /**
     * TextField region. To bind a style to this <code>Region</code> use
     * the name <code>TextField</code>.
     */
    public static final javax.swing.plaf.synth.Region TEXT_FIELD = new javax.swing.plaf.synth.Region("TextField", false);

    /**
     * TextPane region. To bind a style to this <code>Region</code> use
     * the name <code>TextPane</code>.
     */
    public static final javax.swing.plaf.synth.Region TEXT_PANE = new javax.swing.plaf.synth.Region("TextPane", false);

    /**
     * ToggleButton region. To bind a style to this <code>Region</code> use
     * the name <code>ToggleButton</code>.
     */
    public static final javax.swing.plaf.synth.Region TOGGLE_BUTTON = new javax.swing.plaf.synth.Region("ToggleButton", false);

    /**
     * ToolBar region. To bind a style to this <code>Region</code> use
     * the name <code>ToolBar</code>.
     */
    public static final javax.swing.plaf.synth.Region TOOL_BAR = new javax.swing.plaf.synth.Region("ToolBar", false);

    /**
     * Region of the ToolBar containing the content. To bind a style to this
     * <code>Region</code> use the name <code>ToolBarContent</code>.
     */
    public static final javax.swing.plaf.synth.Region TOOL_BAR_CONTENT = new javax.swing.plaf.synth.Region("ToolBarContent", true);

    /**
     * Region for the Window containing the ToolBar. To bind a style to this
     * <code>Region</code> use the name <code>ToolBarDragWindow</code>.
     */
    public static final javax.swing.plaf.synth.Region TOOL_BAR_DRAG_WINDOW = new javax.swing.plaf.synth.Region("ToolBarDragWindow", false);

    /**
     * ToolTip region. To bind a style to this <code>Region</code> use
     * the name <code>ToolTip</code>.
     */
    public static final javax.swing.plaf.synth.Region TOOL_TIP = new javax.swing.plaf.synth.Region("ToolTip", false);

    /**
     * ToolBar separator region. To bind a style to this <code>Region</code> use
     * the name <code>ToolBarSeparator</code>.
     */
    public static final javax.swing.plaf.synth.Region TOOL_BAR_SEPARATOR = new javax.swing.plaf.synth.Region("ToolBarSeparator", false);

    /**
     * Tree region. To bind a style to this <code>Region</code> use the name
     * <code>Tree</code>.
     */
    public static final javax.swing.plaf.synth.Region TREE = new javax.swing.plaf.synth.Region("Tree", false);

    /**
     * Region of the Tree for one cell. To bind a style to this
     * <code>Region</code> use the name <code>TreeCell</code>.
     */
    public static final javax.swing.plaf.synth.Region TREE_CELL = new javax.swing.plaf.synth.Region("TreeCell", true);

    /**
     * Viewport region. To bind a style to this <code>Region</code> use
     * the name <code>Viewport</code>.
     */
    public static final javax.swing.plaf.synth.Region VIEWPORT = new javax.swing.plaf.synth.Region("Viewport", false);

    private static Map<String, javax.swing.plaf.synth.Region> getUItoRegionMap() {
        AppContext context = AppContext.getAppContext();
        Map<String, javax.swing.plaf.synth.Region> map = (Map<String, javax.swing.plaf.synth.Region>) context.get(UI_TO_REGION_MAP_KEY);
        if (map == null) {
            map = new HashMap<String, javax.swing.plaf.synth.Region>();
            map.put("ArrowButtonUI", ARROW_BUTTON);
            map.put("ButtonUI", BUTTON);
            map.put("CheckBoxUI", CHECK_BOX);
            map.put("CheckBoxMenuItemUI", CHECK_BOX_MENU_ITEM);
            map.put("ColorChooserUI", COLOR_CHOOSER);
            map.put("ComboBoxUI", COMBO_BOX);
            map.put("DesktopPaneUI", DESKTOP_PANE);
            map.put("DesktopIconUI", DESKTOP_ICON);
            map.put("EditorPaneUI", EDITOR_PANE);
            map.put("FileChooserUI", FILE_CHOOSER);
            map.put("FormattedTextFieldUI", FORMATTED_TEXT_FIELD);
            map.put("InternalFrameUI", INTERNAL_FRAME);
            map.put("InternalFrameTitlePaneUI", INTERNAL_FRAME_TITLE_PANE);
            map.put("LabelUI", LABEL);
            map.put("ListUI", LIST);
            map.put("MenuUI", MENU);
            map.put("MenuBarUI", MENU_BAR);
            map.put("MenuItemUI", MENU_ITEM);
            map.put("OptionPaneUI", OPTION_PANE);
            map.put("PanelUI", PANEL);
            map.put("PasswordFieldUI", PASSWORD_FIELD);
            map.put("PopupMenuUI", POPUP_MENU);
            map.put("PopupMenuSeparatorUI", POPUP_MENU_SEPARATOR);
            map.put("ProgressBarUI", PROGRESS_BAR);
            map.put("RadioButtonUI", RADIO_BUTTON);
            map.put("RadioButtonMenuItemUI", RADIO_BUTTON_MENU_ITEM);
            map.put("RootPaneUI", ROOT_PANE);
            map.put("ScrollBarUI", SCROLL_BAR);
            map.put("ScrollPaneUI", SCROLL_PANE);
            map.put("SeparatorUI", SEPARATOR);
            map.put("SliderUI", SLIDER);
            map.put("SpinnerUI", SPINNER);
            map.put("SplitPaneUI", SPLIT_PANE);
            map.put("TabbedPaneUI", TABBED_PANE);
            map.put("TableUI", TABLE);
            map.put("TableHeaderUI", TABLE_HEADER);
            map.put("TextAreaUI", TEXT_AREA);
            map.put("TextFieldUI", TEXT_FIELD);
            map.put("TextPaneUI", TEXT_PANE);
            map.put("ToggleButtonUI", TOGGLE_BUTTON);
            map.put("ToolBarUI", TOOL_BAR);
            map.put("ToolTipUI", TOOL_TIP);
            map.put("ToolBarSeparatorUI", TOOL_BAR_SEPARATOR);
            map.put("TreeUI", TREE);
            map.put("ViewportUI", VIEWPORT);
            context.put(UI_TO_REGION_MAP_KEY, map);
        }
        return map;
    }

    private static Map<javax.swing.plaf.synth.Region, String> getLowerCaseNameMap() {
        AppContext context = AppContext.getAppContext();
        Map<javax.swing.plaf.synth.Region, String> map = (Map<javax.swing.plaf.synth.Region, String>) context.get(LOWER_CASE_NAME_MAP_KEY);
        if (map == null) {
            map = new HashMap<javax.swing.plaf.synth.Region, String>();
            context.put(LOWER_CASE_NAME_MAP_KEY, map);
        }
        return map;
    }

    static javax.swing.plaf.synth.Region getRegion(JComponent c) {
        return getUItoRegionMap().get(c.getUIClassID());
    }

    static void registerUIs(UIDefaults table) {
        for (Object key : getUItoRegionMap().keySet()) {
            table.put(key, "javax.swing.plaf.synth.SynthLookAndFeel");
        }
    }

    private final String name;
    private final boolean subregion;

    private Region(String name, boolean subregion) {
        if (name == null) {
            throw new NullPointerException("You must specify a non-null name");
        }
        this.name = name;
        this.subregion = subregion;
    }

    /**
     * Creates a Region with the specified name. This should only be
     * used if you are creating your own <code>JComponent</code> subclass
     * with a custom <code>ComponentUI</code> class.
     *
     * @param name Name of the region
     * @param ui String that will be returned from
     *           <code>component.getUIClassID</code>. This will be null
     *           if this is a subregion.
     * @param subregion Whether or not this is a subregion.
     */
    protected Region(String name, String ui, boolean subregion) {
        this(name, subregion);
        if (ui != null) {
            getUItoRegionMap().put(ui, this);
        }
    }

    /**
     * Returns true if the Region is a subregion of a Component, otherwise
     * false. For example, <code>Region.BUTTON</code> corresponds do a
     * <code>Component</code> so that <code>Region.BUTTON.isSubregion()</code>
     * returns false.
     *
     * @return true if the Region is a subregion of a Component.
     */
    public boolean isSubregion() {
        return subregion;
    }

    /**
     * Returns the name of the region.
     *
     * @return name of the Region.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the name, in lowercase.
     *
     * @return lower case representation of the name of the Region
     */
    String getLowerCaseName() {
        Map<javax.swing.plaf.synth.Region, String> lowerCaseNameMap = getLowerCaseNameMap();
        String lowerCaseName = lowerCaseNameMap.get(this);
        if (lowerCaseName == null) {
            lowerCaseName = name.toLowerCase(Locale.ENGLISH);
            lowerCaseNameMap.put(this, lowerCaseName);
        }
        return lowerCaseName;
    }

    /**
     * Returns the name of the Region.
     *
     * @return name of the Region.
     */
    @Override
    public String toString() {
        return name;
    }
}
