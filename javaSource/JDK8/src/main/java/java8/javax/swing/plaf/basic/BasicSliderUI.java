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

package java8.javax.swing.plaf.basic;

import java.awt.event.*;
import java.awt.*;
import java.beans.*;
import java.util.Dictionary;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.basic.LazyActionMap;

import sun.swing.DefaultLookup;
import sun.swing.UIAction;


/**
 * A Basic L&amp;F implementation of SliderUI.
 *
 * @author Tom Santos
 */
public class BasicSliderUI extends SliderUI{
    // Old actions forward to an instance of this.
    private static final Actions SHARED_ACTION = new Actions();

    public static final int POSITIVE_SCROLL = +1;
    public static final int NEGATIVE_SCROLL = -1;
    public static final int MIN_SCROLL = -2;
    public static final int MAX_SCROLL = +2;

    protected Timer scrollTimer;
    protected JSlider slider;

    protected Insets focusInsets = null;
    protected Insets insetCache = null;
    protected boolean leftToRightCache = true;
    protected Rectangle focusRect = null;
    protected Rectangle contentRect = null;
    protected Rectangle labelRect = null;
    protected Rectangle tickRect = null;
    protected Rectangle trackRect = null;
    protected Rectangle thumbRect = null;

    protected int trackBuffer = 0;  // The distance that the track is from the side of the control

    private transient boolean isDragging;

    protected TrackListener trackListener;
    protected ChangeListener changeListener;
    protected ComponentListener componentListener;
    protected FocusListener focusListener;
    protected ScrollListener scrollListener;
    protected PropertyChangeListener propertyChangeListener;
    private Handler handler;
    private int lastValue;

    // Colors
    private Color shadowColor;
    private Color highlightColor;
    private Color focusColor;

    /**
     * Whther or not sameLabelBaselines is up to date.
     */
    private boolean checkedLabelBaselines;
    /**
     * Whether or not all the entries in the labeltable have the same
     * baseline.
     */
    private boolean sameLabelBaselines;


    protected Color getShadowColor() {
        return shadowColor;
    }

    protected Color getHighlightColor() {
        return highlightColor;
    }

    protected Color getFocusColor() {
        return focusColor;
    }

    /**
     * Returns true if the user is dragging the slider.
     *
     * @return true if the user is dragging the slider
     * @since 1.5
     */
    protected boolean isDragging() {
        return isDragging;
    }

    /////////////////////////////////////////////////////////////////////////////
    // ComponentUI Interface Implementation methods
    /////////////////////////////////////////////////////////////////////////////
    public static ComponentUI createUI(JComponent b)    {
        return new javax.swing.plaf.basic.BasicSliderUI((JSlider)b);
    }

    public BasicSliderUI(JSlider b)   {
    }

    public void installUI(JComponent c)   {
        slider = (JSlider) c;

        checkedLabelBaselines = false;

        slider.setEnabled(slider.isEnabled());
        LookAndFeel.installProperty(slider, "opaque", Boolean.TRUE);

        isDragging = false;
        trackListener = createTrackListener( slider );
        changeListener = createChangeListener( slider );
        componentListener = createComponentListener( slider );
        focusListener = createFocusListener( slider );
        scrollListener = createScrollListener( slider );
        propertyChangeListener = createPropertyChangeListener( slider );

        installDefaults( slider );
        installListeners( slider );
        installKeyboardActions( slider );

        scrollTimer = new Timer( 100, scrollListener );
        scrollTimer.setInitialDelay( 300 );

        insetCache = slider.getInsets();
        leftToRightCache = BasicGraphicsUtils.isLeftToRight(slider);
        focusRect = new Rectangle();
        contentRect = new Rectangle();
        labelRect = new Rectangle();
        tickRect = new Rectangle();
        trackRect = new Rectangle();
        thumbRect = new Rectangle();
        lastValue = slider.getValue();

        calculateGeometry(); // This figures out where the labels, ticks, track, and thumb are.
    }

    public void uninstallUI(JComponent c) {
        if ( c != slider )
            throw new IllegalComponentStateException(
                                                    this + " was asked to deinstall() "
                                                    + c + " when it only knows about "
                                                    + slider + ".");

        scrollTimer.stop();
        scrollTimer = null;

        uninstallDefaults(slider);
        uninstallListeners( slider );
        uninstallKeyboardActions(slider);

        insetCache = null;
        leftToRightCache = true;
        focusRect = null;
        contentRect = null;
        labelRect = null;
        tickRect = null;
        trackRect = null;
        thumbRect = null;
        trackListener = null;
        changeListener = null;
        componentListener = null;
        focusListener = null;
        scrollListener = null;
        propertyChangeListener = null;
        slider = null;
    }

    protected void installDefaults( JSlider slider ) {
        LookAndFeel.installBorder(slider, "Slider.border");
        LookAndFeel.installColorsAndFont(slider, "Slider.background",
                                         "Slider.foreground", "Slider.font");
        highlightColor = UIManager.getColor("Slider.highlight");

        shadowColor = UIManager.getColor("Slider.shadow");
        focusColor = UIManager.getColor("Slider.focus");

        focusInsets = (Insets)UIManager.get( "Slider.focusInsets" );
        // use default if missing so that BasicSliderUI can be used in other
        // LAFs like Nimbus
        if (focusInsets == null) focusInsets = new InsetsUIResource(2,2,2,2);
    }

    protected void uninstallDefaults(JSlider slider) {
        LookAndFeel.uninstallBorder(slider);

        focusInsets = null;
    }

    protected TrackListener createTrackListener(JSlider slider) {
        return new TrackListener();
    }

    protected ChangeListener createChangeListener(JSlider slider) {
        return getHandler();
    }

    protected ComponentListener createComponentListener(JSlider slider) {
        return getHandler();
    }

    protected FocusListener createFocusListener(JSlider slider) {
        return getHandler();
    }

    protected ScrollListener createScrollListener( JSlider slider ) {
        return new ScrollListener();
    }

    protected PropertyChangeListener createPropertyChangeListener(
            JSlider slider) {
        return getHandler();
    }

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    protected void installListeners( JSlider slider ) {
        slider.addMouseListener(trackListener);
        slider.addMouseMotionListener(trackListener);
        slider.addFocusListener(focusListener);
        slider.addComponentListener(componentListener);
        slider.addPropertyChangeListener( propertyChangeListener );
        slider.getModel().addChangeListener(changeListener);
    }

    protected void uninstallListeners( JSlider slider ) {
        slider.removeMouseListener(trackListener);
        slider.removeMouseMotionListener(trackListener);
        slider.removeFocusListener(focusListener);
        slider.removeComponentListener(componentListener);
        slider.removePropertyChangeListener( propertyChangeListener );
        slider.getModel().removeChangeListener(changeListener);
        handler = null;
    }

    protected void installKeyboardActions( JSlider slider ) {
        InputMap km = getInputMap(JComponent.WHEN_FOCUSED, slider);
        SwingUtilities.replaceUIInputMap(slider, JComponent.WHEN_FOCUSED, km);
        javax.swing.plaf.basic.LazyActionMap.installLazyActionMap(slider, javax.swing.plaf.basic.BasicSliderUI.class,
                "Slider.actionMap");
    }

    InputMap getInputMap(int condition, JSlider slider) {
        if (condition == JComponent.WHEN_FOCUSED) {
            InputMap keyMap = (InputMap)DefaultLookup.get(slider, this,
                  "Slider.focusInputMap");
            InputMap rtlKeyMap;

            if (slider.getComponentOrientation().isLeftToRight() ||
                ((rtlKeyMap = (InputMap)DefaultLookup.get(slider, this,
                          "Slider.focusInputMap.RightToLeft")) == null)) {
                return keyMap;
            } else {
                rtlKeyMap.setParent(keyMap);
                return rtlKeyMap;
            }
        }
        return null;
    }

    /**
     * Populates ComboBox's actions.
     */
    static void loadActionMap(javax.swing.plaf.basic.LazyActionMap map) {
        map.put(new Actions(Actions.POSITIVE_UNIT_INCREMENT));
        map.put(new Actions(Actions.POSITIVE_BLOCK_INCREMENT));
        map.put(new Actions(Actions.NEGATIVE_UNIT_INCREMENT));
        map.put(new Actions(Actions.NEGATIVE_BLOCK_INCREMENT));
        map.put(new Actions(Actions.MIN_SCROLL_INCREMENT));
        map.put(new Actions(Actions.MAX_SCROLL_INCREMENT));
    }

    protected void uninstallKeyboardActions( JSlider slider ) {
        SwingUtilities.replaceUIActionMap(slider, null);
        SwingUtilities.replaceUIInputMap(slider, JComponent.WHEN_FOCUSED,
                                         null);
    }


    /**
     * Returns the baseline.
     *
     * @throws NullPointerException {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     * @see JComponent#getBaseline(int, int)
     * @since 1.6
     */
    public int getBaseline(JComponent c, int width, int height) {
        super.getBaseline(c, width, height);
        if (slider.getPaintLabels() && labelsHaveSameBaselines()) {
            FontMetrics metrics = slider.getFontMetrics(slider.getFont());
            Insets insets = slider.getInsets();
            Dimension thumbSize = getThumbSize();
            if (slider.getOrientation() == JSlider.HORIZONTAL) {
                int tickLength = getTickLength();
                int contentHeight = height - insets.top - insets.bottom -
                    focusInsets.top - focusInsets.bottom;
                int thumbHeight = thumbSize.height;
                int centerSpacing = thumbHeight;
                if (slider.getPaintTicks()) {
                    centerSpacing += tickLength;
                }
                // Assume uniform labels.
                centerSpacing += getHeightOfTallestLabel();
                int trackY = insets.top + focusInsets.top +
                    (contentHeight - centerSpacing - 1) / 2;
                int trackHeight = thumbHeight;
                int tickY = trackY + trackHeight;
                int tickHeight = tickLength;
                if (!slider.getPaintTicks()) {
                    tickHeight = 0;
                }
                int labelY = tickY + tickHeight;
                return labelY + metrics.getAscent();
            }
            else { // vertical
                boolean inverted = slider.getInverted();
                Integer value = inverted ? getLowestValue() :
                                           getHighestValue();
                if (value != null) {
                    int thumbHeight = thumbSize.height;
                    int trackBuffer = Math.max(metrics.getHeight() / 2,
                                               thumbHeight / 2);
                    int contentY = focusInsets.top + insets.top;
                    int trackY = contentY + trackBuffer;
                    int trackHeight = height - focusInsets.top -
                        focusInsets.bottom - insets.top - insets.bottom -
                        trackBuffer - trackBuffer;
                    int yPosition = yPositionForValue(value, trackY,
                                                      trackHeight);
                    return yPosition - metrics.getHeight() / 2 +
                        metrics.getAscent();
                }
            }
        }
        return 0;
    }

    /**
     * Returns an enum indicating how the baseline of the component
     * changes as the size changes.
     *
     * @throws NullPointerException {@inheritDoc}
     * @see JComponent#getBaseline(int, int)
     * @since 1.6
     */
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(
            JComponent c) {
        super.getBaselineResizeBehavior(c);
        // NOTE: BasicSpinner really provides for CENTER_OFFSET, but
        // the default min/pref size is smaller than it should be
        // so that getBaseline() doesn't implement the contract
        // for CENTER_OFFSET as defined in Component.
        return Component.BaselineResizeBehavior.OTHER;
    }

    /**
     * Returns true if all the labels from the label table have the same
     * baseline.
     *
     * @return true if all the labels from the label table have the
     *         same baseline
     * @since 1.6
     */
    protected boolean labelsHaveSameBaselines() {
        if (!checkedLabelBaselines) {
            checkedLabelBaselines = true;
            Dictionary dictionary = slider.getLabelTable();
            if (dictionary != null) {
                sameLabelBaselines = true;
                Enumeration elements = dictionary.elements();
                int baseline = -1;
                while (elements.hasMoreElements()) {
                    JComponent label = (JComponent) elements.nextElement();
                    Dimension pref = label.getPreferredSize();
                    int labelBaseline = label.getBaseline(pref.width,
                                                          pref.height);
                    if (labelBaseline >= 0) {
                        if (baseline == -1) {
                            baseline = labelBaseline;
                        }
                        else if (baseline != labelBaseline) {
                            sameLabelBaselines = false;
                            break;
                        }
                    }
                    else {
                        sameLabelBaselines = false;
                        break;
                    }
                }
            }
            else {
                sameLabelBaselines = false;
            }
        }
        return sameLabelBaselines;
    }

    public Dimension getPreferredHorizontalSize() {
        Dimension horizDim = (Dimension)DefaultLookup.get(slider,
                this, "Slider.horizontalSize");
        if (horizDim == null) {
            horizDim = new Dimension(200, 21);
        }
        return horizDim;
    }

    public Dimension getPreferredVerticalSize() {
        Dimension vertDim = (Dimension)DefaultLookup.get(slider,
                this, "Slider.verticalSize");
        if (vertDim == null) {
            vertDim = new Dimension(21, 200);
        }
        return vertDim;
    }

    public Dimension getMinimumHorizontalSize() {
        Dimension minHorizDim = (Dimension)DefaultLookup.get(slider,
                this, "Slider.minimumHorizontalSize");
        if (minHorizDim == null) {
            minHorizDim = new Dimension(36, 21);
        }
        return minHorizDim;
    }

    public Dimension getMinimumVerticalSize() {
        Dimension minVertDim = (Dimension)DefaultLookup.get(slider,
                this, "Slider.minimumVerticalSize");
        if (minVertDim == null) {
            minVertDim = new Dimension(21, 36);
        }
        return minVertDim;
    }

    public Dimension getPreferredSize(JComponent c)    {
        recalculateIfInsetsChanged();
        Dimension d;
        if ( slider.getOrientation() == JSlider.VERTICAL ) {
            d = new Dimension(getPreferredVerticalSize());
            d.width = insetCache.left + insetCache.right;
            d.width += focusInsets.left + focusInsets.right;
            d.width += trackRect.width + tickRect.width + labelRect.width;
        }
        else {
            d = new Dimension(getPreferredHorizontalSize());
            d.height = insetCache.top + insetCache.bottom;
            d.height += focusInsets.top + focusInsets.bottom;
            d.height += trackRect.height + tickRect.height + labelRect.height;
        }

        return d;
    }

    public Dimension getMinimumSize(JComponent c)  {
        recalculateIfInsetsChanged();
        Dimension d;

        if ( slider.getOrientation() == JSlider.VERTICAL ) {
            d = new Dimension(getMinimumVerticalSize());
            d.width = insetCache.left + insetCache.right;
            d.width += focusInsets.left + focusInsets.right;
            d.width += trackRect.width + tickRect.width + labelRect.width;
        }
        else {
            d = new Dimension(getMinimumHorizontalSize());
            d.height = insetCache.top + insetCache.bottom;
            d.height += focusInsets.top + focusInsets.bottom;
            d.height += trackRect.height + tickRect.height + labelRect.height;
        }

        return d;
    }

    public Dimension getMaximumSize(JComponent c) {
        Dimension d = getPreferredSize(c);
        if ( slider.getOrientation() == JSlider.VERTICAL ) {
            d.height = Short.MAX_VALUE;
        }
        else {
            d.width = Short.MAX_VALUE;
        }

        return d;
    }

    protected void calculateGeometry() {
        calculateFocusRect();
        calculateContentRect();
        calculateThumbSize();
        calculateTrackBuffer();
        calculateTrackRect();
        calculateTickRect();
        calculateLabelRect();
        calculateThumbLocation();
    }

    protected void calculateFocusRect() {
        focusRect.x = insetCache.left;
        focusRect.y = insetCache.top;
        focusRect.width = slider.getWidth() - (insetCache.left + insetCache.right);
        focusRect.height = slider.getHeight() - (insetCache.top + insetCache.bottom);
    }

    protected void calculateThumbSize() {
        Dimension size = getThumbSize();
        thumbRect.setSize( size.width, size.height );
    }

    protected void calculateContentRect() {
        contentRect.x = focusRect.x + focusInsets.left;
        contentRect.y = focusRect.y + focusInsets.top;
        contentRect.width = focusRect.width - (focusInsets.left + focusInsets.right);
        contentRect.height = focusRect.height - (focusInsets.top + focusInsets.bottom);
    }

    private int getTickSpacing() {
        int majorTickSpacing = slider.getMajorTickSpacing();
        int minorTickSpacing = slider.getMinorTickSpacing();

        int result;

        if (minorTickSpacing > 0) {
            result = minorTickSpacing;
        } else if (majorTickSpacing > 0) {
            result = majorTickSpacing;
        } else {
            result = 0;
        }

        return result;
    }

    protected void calculateThumbLocation() {
        if ( slider.getSnapToTicks() ) {
            int sliderValue = slider.getValue();
            int snappedValue = sliderValue;
            int tickSpacing = getTickSpacing();

            if ( tickSpacing != 0 ) {
                // If it's not on a tick, change the value
                if ( (sliderValue - slider.getMinimum()) % tickSpacing != 0 ) {
                    float temp = (float)(sliderValue - slider.getMinimum()) / (float)tickSpacing;
                    int whichTick = Math.round( temp );

                    // This is the fix for the bug #6401380
                    if (temp - (int)temp == .5 && sliderValue < lastValue) {
                      whichTick --;
                    }
                    snappedValue = slider.getMinimum() + (whichTick * tickSpacing);
                }

                if( snappedValue != sliderValue ) {
                    slider.setValue( snappedValue );
                }
            }
        }

        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            int valuePosition = xPositionForValue(slider.getValue());

            thumbRect.x = valuePosition - (thumbRect.width / 2);
            thumbRect.y = trackRect.y;
        }
        else {
            int valuePosition = yPositionForValue(slider.getValue());

            thumbRect.x = trackRect.x;
            thumbRect.y = valuePosition - (thumbRect.height / 2);
        }
    }

    protected void calculateTrackBuffer() {
        if ( slider.getPaintLabels() && slider.getLabelTable()  != null ) {
            Component highLabel = getHighestValueLabel();
            Component lowLabel = getLowestValueLabel();

            if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
                trackBuffer = Math.max( highLabel.getBounds().width, lowLabel.getBounds().width ) / 2;
                trackBuffer = Math.max( trackBuffer, thumbRect.width / 2 );
            }
            else {
                trackBuffer = Math.max( highLabel.getBounds().height, lowLabel.getBounds().height ) / 2;
                trackBuffer = Math.max( trackBuffer, thumbRect.height / 2 );
            }
        }
        else {
            if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
                trackBuffer = thumbRect.width / 2;
            }
            else {
                trackBuffer = thumbRect.height / 2;
            }
        }
    }


    protected void calculateTrackRect() {
        int centerSpacing; // used to center sliders added using BorderLayout.CENTER (bug 4275631)
        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            centerSpacing = thumbRect.height;
            if ( slider.getPaintTicks() ) centerSpacing += getTickLength();
            if ( slider.getPaintLabels() ) centerSpacing += getHeightOfTallestLabel();
            trackRect.x = contentRect.x + trackBuffer;
            trackRect.y = contentRect.y + (contentRect.height - centerSpacing - 1)/2;
            trackRect.width = contentRect.width - (trackBuffer * 2);
            trackRect.height = thumbRect.height;
        }
        else {
            centerSpacing = thumbRect.width;
            if (BasicGraphicsUtils.isLeftToRight(slider)) {
                if ( slider.getPaintTicks() ) centerSpacing += getTickLength();
                if ( slider.getPaintLabels() ) centerSpacing += getWidthOfWidestLabel();
            } else {
                if ( slider.getPaintTicks() ) centerSpacing -= getTickLength();
                if ( slider.getPaintLabels() ) centerSpacing -= getWidthOfWidestLabel();
            }
            trackRect.x = contentRect.x + (contentRect.width - centerSpacing - 1)/2;
            trackRect.y = contentRect.y + trackBuffer;
            trackRect.width = thumbRect.width;
            trackRect.height = contentRect.height - (trackBuffer * 2);
        }

    }

    /**
     * Gets the height of the tick area for horizontal sliders and the width of the
     * tick area for vertical sliders.  BasicSliderUI uses the returned value to
     * determine the tick area rectangle.  If you want to give your ticks some room,
     * make this larger than you need and paint your ticks away from the sides in paintTicks().
     */
    protected int getTickLength() {
        return 8;
    }

    protected void calculateTickRect() {
        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            tickRect.x = trackRect.x;
            tickRect.y = trackRect.y + trackRect.height;
            tickRect.width = trackRect.width;
            tickRect.height = (slider.getPaintTicks()) ? getTickLength() : 0;
        }
        else {
            tickRect.width = (slider.getPaintTicks()) ? getTickLength() : 0;
            if(BasicGraphicsUtils.isLeftToRight(slider)) {
                tickRect.x = trackRect.x + trackRect.width;
            }
            else {
                tickRect.x = trackRect.x - tickRect.width;
            }
            tickRect.y = trackRect.y;
            tickRect.height = trackRect.height;
        }
    }

    protected void calculateLabelRect() {
        if ( slider.getPaintLabels() ) {
            if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
                labelRect.x = tickRect.x - trackBuffer;
                labelRect.y = tickRect.y + tickRect.height;
                labelRect.width = tickRect.width + (trackBuffer * 2);
                labelRect.height = getHeightOfTallestLabel();
            }
            else {
                if(BasicGraphicsUtils.isLeftToRight(slider)) {
                    labelRect.x = tickRect.x + tickRect.width;
                    labelRect.width = getWidthOfWidestLabel();
                }
                else {
                    labelRect.width = getWidthOfWidestLabel();
                    labelRect.x = tickRect.x - labelRect.width;
                }
                labelRect.y = tickRect.y - trackBuffer;
                labelRect.height = tickRect.height + (trackBuffer * 2);
            }
        }
        else {
            if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
                labelRect.x = tickRect.x;
                labelRect.y = tickRect.y + tickRect.height;
                labelRect.width = tickRect.width;
                labelRect.height = 0;
            }
            else {
                if(BasicGraphicsUtils.isLeftToRight(slider)) {
                    labelRect.x = tickRect.x + tickRect.width;
                }
                else {
                    labelRect.x = tickRect.x;
                }
                labelRect.y = tickRect.y;
                labelRect.width = 0;
                labelRect.height = tickRect.height;
            }
        }
    }

    protected Dimension getThumbSize() {
        Dimension size = new Dimension();

        if ( slider.getOrientation() == JSlider.VERTICAL ) {
            size.width = 20;
            size.height = 11;
        }
        else {
            size.width = 11;
            size.height = 20;
        }

        return size;
    }

    public class PropertyChangeHandler implements PropertyChangeListener {
        // NOTE: This class exists only for backward compatibility. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this
        // class calls into the Handler.
        public void propertyChange( PropertyChangeEvent e ) {
            getHandler().propertyChange(e);
        }
    }

    protected int getWidthOfWidestLabel() {
        Dictionary dictionary = slider.getLabelTable();
        int widest = 0;
        if ( dictionary != null ) {
            Enumeration keys = dictionary.keys();
            while ( keys.hasMoreElements() ) {
                JComponent label = (JComponent) dictionary.get(keys.nextElement());
                widest = Math.max( label.getPreferredSize().width, widest );
            }
        }
        return widest;
    }

    protected int getHeightOfTallestLabel() {
        Dictionary dictionary = slider.getLabelTable();
        int tallest = 0;
        if ( dictionary != null ) {
            Enumeration keys = dictionary.keys();
            while ( keys.hasMoreElements() ) {
                JComponent label = (JComponent) dictionary.get(keys.nextElement());
                tallest = Math.max( label.getPreferredSize().height, tallest );
            }
        }
        return tallest;
    }

    protected int getWidthOfHighValueLabel() {
        Component label = getHighestValueLabel();
        int width = 0;

        if ( label != null ) {
            width = label.getPreferredSize().width;
        }

        return width;
    }

    protected int getWidthOfLowValueLabel() {
        Component label = getLowestValueLabel();
        int width = 0;

        if ( label != null ) {
            width = label.getPreferredSize().width;
        }

        return width;
    }

    protected int getHeightOfHighValueLabel() {
        Component label = getHighestValueLabel();
        int height = 0;

        if ( label != null ) {
            height = label.getPreferredSize().height;
        }

        return height;
    }

    protected int getHeightOfLowValueLabel() {
        Component label = getLowestValueLabel();
        int height = 0;

        if ( label != null ) {
            height = label.getPreferredSize().height;
        }

        return height;
    }

    protected boolean drawInverted() {
        if (slider.getOrientation()==JSlider.HORIZONTAL) {
            if(BasicGraphicsUtils.isLeftToRight(slider)) {
                return slider.getInverted();
            } else {
                return !slider.getInverted();
            }
        } else {
            return slider.getInverted();
        }
    }

    /**
     * Returns the biggest value that has an entry in the label table.
     *
     * @return biggest value that has an entry in the label table, or
     *         null.
     * @since 1.6
     */
    protected Integer getHighestValue() {
        Dictionary dictionary = slider.getLabelTable();

        if (dictionary == null) {
            return null;
        }

        Enumeration keys = dictionary.keys();

        Integer max = null;

        while (keys.hasMoreElements()) {
            Integer i = (Integer) keys.nextElement();

            if (max == null || i > max) {
                max = i;
            }
        }

        return max;
    }

    /**
     * Returns the smallest value that has an entry in the label table.
     *
     * @return smallest value that has an entry in the label table, or
     *         null.
     * @since 1.6
     */
    protected Integer getLowestValue() {
        Dictionary dictionary = slider.getLabelTable();

        if (dictionary == null) {
            return null;
        }

        Enumeration keys = dictionary.keys();

        Integer min = null;

        while (keys.hasMoreElements()) {
            Integer i = (Integer) keys.nextElement();

            if (min == null || i < min) {
                min = i;
            }
        }

        return min;
    }


    /**
     * Returns the label that corresponds to the highest slider value in the label table.
     * @see JSlider#setLabelTable
     */
    protected Component getLowestValueLabel() {
        Integer min = getLowestValue();
        if (min != null) {
            return (Component)slider.getLabelTable().get(min);
        }
        return null;
    }

    /**
     * Returns the label that corresponds to the lowest slider value in the label table.
     * @see JSlider#setLabelTable
     */
    protected Component getHighestValueLabel() {
        Integer max = getHighestValue();
        if (max != null) {
            return (Component)slider.getLabelTable().get(max);
        }
        return null;
    }

    public void paint( Graphics g, JComponent c )   {
        recalculateIfInsetsChanged();
        recalculateIfOrientationChanged();
        Rectangle clip = g.getClipBounds();

        if ( !clip.intersects(trackRect) && slider.getPaintTrack())
            calculateGeometry();

        if ( slider.getPaintTrack() && clip.intersects( trackRect ) ) {
            paintTrack( g );
        }
        if ( slider.getPaintTicks() && clip.intersects( tickRect ) ) {
            paintTicks( g );
        }
        if ( slider.getPaintLabels() && clip.intersects( labelRect ) ) {
            paintLabels( g );
        }
        if ( slider.hasFocus() && clip.intersects( focusRect ) ) {
            paintFocus( g );
        }
        if ( clip.intersects( thumbRect ) ) {
            paintThumb( g );
        }
    }

    protected void recalculateIfInsetsChanged() {
        Insets newInsets = slider.getInsets();
        if ( !newInsets.equals( insetCache ) ) {
            insetCache = newInsets;
            calculateGeometry();
        }
    }

    protected void recalculateIfOrientationChanged() {
        boolean ltr = BasicGraphicsUtils.isLeftToRight(slider);
        if ( ltr!=leftToRightCache ) {
            leftToRightCache = ltr;
            calculateGeometry();
        }
    }

    public void paintFocus(Graphics g)  {
        g.setColor( getFocusColor() );

        BasicGraphicsUtils.drawDashedRect( g, focusRect.x, focusRect.y,
                                           focusRect.width, focusRect.height );
    }

    public void paintTrack(Graphics g)  {

        Rectangle trackBounds = trackRect;

        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            int cy = (trackBounds.height / 2) - 2;
            int cw = trackBounds.width;

            g.translate(trackBounds.x, trackBounds.y + cy);

            g.setColor(getShadowColor());
            g.drawLine(0, 0, cw - 1, 0);
            g.drawLine(0, 1, 0, 2);
            g.setColor(getHighlightColor());
            g.drawLine(0, 3, cw, 3);
            g.drawLine(cw, 0, cw, 3);
            g.setColor(Color.black);
            g.drawLine(1, 1, cw-2, 1);

            g.translate(-trackBounds.x, -(trackBounds.y + cy));
        }
        else {
            int cx = (trackBounds.width / 2) - 2;
            int ch = trackBounds.height;

            g.translate(trackBounds.x + cx, trackBounds.y);

            g.setColor(getShadowColor());
            g.drawLine(0, 0, 0, ch - 1);
            g.drawLine(1, 0, 2, 0);
            g.setColor(getHighlightColor());
            g.drawLine(3, 0, 3, ch);
            g.drawLine(0, ch, 3, ch);
            g.setColor(Color.black);
            g.drawLine(1, 1, 1, ch-2);

            g.translate(-(trackBounds.x + cx), -trackBounds.y);
        }
    }

    public void paintTicks(Graphics g)  {
        Rectangle tickBounds = tickRect;

        g.setColor(DefaultLookup.getColor(slider, this, "Slider.tickColor", Color.black));

        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            g.translate(0, tickBounds.y);

            if (slider.getMinorTickSpacing() > 0) {
                int value = slider.getMinimum();

                while ( value <= slider.getMaximum() ) {
                    int xPos = xPositionForValue(value);
                    paintMinorTickForHorizSlider( g, tickBounds, xPos );

                    // Overflow checking
                    if (Integer.MAX_VALUE - slider.getMinorTickSpacing() < value) {
                        break;
                    }

                    value += slider.getMinorTickSpacing();
                }
            }

            if (slider.getMajorTickSpacing() > 0) {
                int value = slider.getMinimum();

                while ( value <= slider.getMaximum() ) {
                    int xPos = xPositionForValue(value);
                    paintMajorTickForHorizSlider( g, tickBounds, xPos );

                    // Overflow checking
                    if (Integer.MAX_VALUE - slider.getMajorTickSpacing() < value) {
                        break;
                    }

                    value += slider.getMajorTickSpacing();
                }
            }

            g.translate( 0, -tickBounds.y);
        } else {
            g.translate(tickBounds.x, 0);

            if (slider.getMinorTickSpacing() > 0) {
                int offset = 0;
                if(!BasicGraphicsUtils.isLeftToRight(slider)) {
                    offset = tickBounds.width - tickBounds.width / 2;
                    g.translate(offset, 0);
                }

                int value = slider.getMinimum();

                while (value <= slider.getMaximum()) {
                    int yPos = yPositionForValue(value);
                    paintMinorTickForVertSlider( g, tickBounds, yPos );

                    // Overflow checking
                    if (Integer.MAX_VALUE - slider.getMinorTickSpacing() < value) {
                        break;
                    }

                    value += slider.getMinorTickSpacing();
                }

                if(!BasicGraphicsUtils.isLeftToRight(slider)) {
                    g.translate(-offset, 0);
                }
            }

            if (slider.getMajorTickSpacing() > 0) {
                if(!BasicGraphicsUtils.isLeftToRight(slider)) {
                    g.translate(2, 0);
                }

                int value = slider.getMinimum();

                while (value <= slider.getMaximum()) {
                    int yPos = yPositionForValue(value);
                    paintMajorTickForVertSlider( g, tickBounds, yPos );

                    // Overflow checking
                    if (Integer.MAX_VALUE - slider.getMajorTickSpacing() < value) {
                        break;
                    }

                    value += slider.getMajorTickSpacing();
                }

                if(!BasicGraphicsUtils.isLeftToRight(slider)) {
                    g.translate(-2, 0);
                }
            }
            g.translate(-tickBounds.x, 0);
        }
    }

    protected void paintMinorTickForHorizSlider( Graphics g, Rectangle tickBounds, int x ) {
        g.drawLine( x, 0, x, tickBounds.height / 2 - 1 );
    }

    protected void paintMajorTickForHorizSlider( Graphics g, Rectangle tickBounds, int x ) {
        g.drawLine( x, 0, x, tickBounds.height - 2 );
    }

    protected void paintMinorTickForVertSlider( Graphics g, Rectangle tickBounds, int y ) {
        g.drawLine( 0, y, tickBounds.width / 2 - 1, y );
    }

    protected void paintMajorTickForVertSlider( Graphics g, Rectangle tickBounds, int y ) {
        g.drawLine( 0, y,  tickBounds.width - 2, y );
    }

    public void paintLabels( Graphics g ) {
        Rectangle labelBounds = labelRect;

        Dictionary dictionary = slider.getLabelTable();
        if ( dictionary != null ) {
            Enumeration keys = dictionary.keys();
            int minValue = slider.getMinimum();
            int maxValue = slider.getMaximum();
            boolean enabled = slider.isEnabled();
            while ( keys.hasMoreElements() ) {
                Integer key = (Integer)keys.nextElement();
                int value = key.intValue();
                if (value >= minValue && value <= maxValue) {
                    JComponent label = (JComponent) dictionary.get(key);
                    label.setEnabled(enabled);

                    if (label instanceof JLabel) {
                        Icon icon = label.isEnabled() ? ((JLabel) label).getIcon() : ((JLabel) label).getDisabledIcon();

                        if (icon instanceof ImageIcon) {
                            // Register Slider as an image observer. It allows to catch notifications about
                            // image changes (e.g. gif animation)
                            Toolkit.getDefaultToolkit().checkImage(((ImageIcon) icon).getImage(), -1, -1, slider);
                        }
                    }

                    if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
                        g.translate( 0, labelBounds.y );
                        paintHorizontalLabel( g, value, label );
                        g.translate( 0, -labelBounds.y );
                    }
                    else {
                        int offset = 0;
                        if (!BasicGraphicsUtils.isLeftToRight(slider)) {
                            offset = labelBounds.width -
                                label.getPreferredSize().width;
                        }
                        g.translate( labelBounds.x + offset, 0 );
                        paintVerticalLabel( g, value, label );
                        g.translate( -labelBounds.x - offset, 0 );
                    }
                }
            }
        }

    }

    /**
     * Called for every label in the label table.  Used to draw the labels for horizontal sliders.
     * The graphics have been translated to labelRect.y already.
     * @see JSlider#setLabelTable
     */
    protected void paintHorizontalLabel( Graphics g, int value, Component label ) {
        int labelCenter = xPositionForValue( value );
        int labelLeft = labelCenter - (label.getPreferredSize().width / 2);
        g.translate( labelLeft, 0 );
        label.paint( g );
        g.translate( -labelLeft, 0 );
    }

    /**
     * Called for every label in the label table.  Used to draw the labels for vertical sliders.
     * The graphics have been translated to labelRect.x already.
     * @see JSlider#setLabelTable
     */
    protected void paintVerticalLabel( Graphics g, int value, Component label ) {
        int labelCenter = yPositionForValue( value );
        int labelTop = labelCenter - (label.getPreferredSize().height / 2);
        g.translate( 0, labelTop );
        label.paint( g );
        g.translate( 0, -labelTop );
    }

    public void paintThumb(Graphics g)  {
        Rectangle knobBounds = thumbRect;
        int w = knobBounds.width;
        int h = knobBounds.height;

        g.translate(knobBounds.x, knobBounds.y);

        if ( slider.isEnabled() ) {
            g.setColor(slider.getBackground());
        }
        else {
            g.setColor(slider.getBackground().darker());
        }

        Boolean paintThumbArrowShape =
            (Boolean)slider.getClientProperty("Slider.paintThumbArrowShape");

        if ((!slider.getPaintTicks() && paintThumbArrowShape == null) ||
            paintThumbArrowShape == Boolean.FALSE) {

            // "plain" version
            g.fillRect(0, 0, w, h);

            g.setColor(Color.black);
            g.drawLine(0, h-1, w-1, h-1);
            g.drawLine(w-1, 0, w-1, h-1);

            g.setColor(highlightColor);
            g.drawLine(0, 0, 0, h-2);
            g.drawLine(1, 0, w-2, 0);

            g.setColor(shadowColor);
            g.drawLine(1, h-2, w-2, h-2);
            g.drawLine(w-2, 1, w-2, h-3);
        }
        else if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            int cw = w / 2;
            g.fillRect(1, 1, w-3, h-1-cw);
            Polygon p = new Polygon();
            p.addPoint(1, h-cw);
            p.addPoint(cw-1, h-1);
            p.addPoint(w-2, h-1-cw);
            g.fillPolygon(p);

            g.setColor(highlightColor);
            g.drawLine(0, 0, w-2, 0);
            g.drawLine(0, 1, 0, h-1-cw);
            g.drawLine(0, h-cw, cw-1, h-1);

            g.setColor(Color.black);
            g.drawLine(w-1, 0, w-1, h-2-cw);
            g.drawLine(w-1, h-1-cw, w-1-cw, h-1);

            g.setColor(shadowColor);
            g.drawLine(w-2, 1, w-2, h-2-cw);
            g.drawLine(w-2, h-1-cw, w-1-cw, h-2);
        }
        else {  // vertical
            int cw = h / 2;
            if(BasicGraphicsUtils.isLeftToRight(slider)) {
                  g.fillRect(1, 1, w-1-cw, h-3);
                  Polygon p = new Polygon();
                  p.addPoint(w-cw-1, 0);
                  p.addPoint(w-1, cw);
                  p.addPoint(w-1-cw, h-2);
                  g.fillPolygon(p);

                  g.setColor(highlightColor);
                  g.drawLine(0, 0, 0, h - 2);                  // left
                  g.drawLine(1, 0, w-1-cw, 0);                 // top
                  g.drawLine(w-cw-1, 0, w-1, cw);              // top slant

                  g.setColor(Color.black);
                  g.drawLine(0, h-1, w-2-cw, h-1);             // bottom
                  g.drawLine(w-1-cw, h-1, w-1, h-1-cw);        // bottom slant

                  g.setColor(shadowColor);
                  g.drawLine(1, h-2, w-2-cw,  h-2 );         // bottom
                  g.drawLine(w-1-cw, h-2, w-2, h-cw-1 );     // bottom slant
            }
            else {
                  g.fillRect(5, 1, w-1-cw, h-3);
                  Polygon p = new Polygon();
                  p.addPoint(cw, 0);
                  p.addPoint(0, cw);
                  p.addPoint(cw, h-2);
                  g.fillPolygon(p);

                  g.setColor(highlightColor);
                  g.drawLine(cw-1, 0, w-2, 0);             // top
                  g.drawLine(0, cw, cw, 0);                // top slant

                  g.setColor(Color.black);
                  g.drawLine(0, h-1-cw, cw, h-1 );         // bottom slant
                  g.drawLine(cw, h-1, w-1, h-1);           // bottom

                  g.setColor(shadowColor);
                  g.drawLine(cw, h-2, w-2,  h-2 );         // bottom
                  g.drawLine(w-1, 1, w-1,  h-2 );          // right
            }
        }

        g.translate(-knobBounds.x, -knobBounds.y);
    }

    // Used exclusively by setThumbLocation()
    private static Rectangle unionRect = new Rectangle();

    public void setThumbLocation(int x, int y)  {
        unionRect.setBounds( thumbRect );

        thumbRect.setLocation( x, y );

        SwingUtilities.computeUnion( thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height, unionRect );
        slider.repaint( unionRect.x, unionRect.y, unionRect.width, unionRect.height );
    }

    public void scrollByBlock(int direction)    {
        synchronized(slider)    {
            int blockIncrement =
                (slider.getMaximum() - slider.getMinimum()) / 10;
            if (blockIncrement == 0) {
                blockIncrement = 1;
            }

            if (slider.getSnapToTicks()) {
                int tickSpacing = getTickSpacing();

                if (blockIncrement < tickSpacing) {
                    blockIncrement = tickSpacing;
                }
            }

            int delta = blockIncrement * ((direction > 0) ? POSITIVE_SCROLL : NEGATIVE_SCROLL);
            slider.setValue(slider.getValue() + delta);
        }
    }

    public void scrollByUnit(int direction) {
        synchronized(slider)    {
            int delta = ((direction > 0) ? POSITIVE_SCROLL : NEGATIVE_SCROLL);

            if (slider.getSnapToTicks()) {
                delta *= getTickSpacing();
            }

            slider.setValue(slider.getValue() + delta);
        }
    }

    /**
     * This function is called when a mousePressed was detected in the track, not
     * in the thumb.  The default behavior is to scroll by block.  You can
     *  override this method to stop it from scrolling or to add additional behavior.
     */
    protected void scrollDueToClickInTrack( int dir ) {
        scrollByBlock( dir );
    }

    protected int xPositionForValue( int value )    {
        int min = slider.getMinimum();
        int max = slider.getMaximum();
        int trackLength = trackRect.width;
        double valueRange = (double)max - (double)min;
        double pixelsPerValue = (double)trackLength / valueRange;
        int trackLeft = trackRect.x;
        int trackRight = trackRect.x + (trackRect.width - 1);
        int xPosition;

        if ( !drawInverted() ) {
            xPosition = trackLeft;
            xPosition += Math.round( pixelsPerValue * ((double)value - min) );
        }
        else {
            xPosition = trackRight;
            xPosition -= Math.round( pixelsPerValue * ((double)value - min) );
        }

        xPosition = Math.max( trackLeft, xPosition );
        xPosition = Math.min( trackRight, xPosition );

        return xPosition;
    }

    protected int yPositionForValue( int value )  {
        return yPositionForValue(value, trackRect.y, trackRect.height);
    }

    /**
     * Returns the y location for the specified value.  No checking is
     * done on the arguments.  In particular if <code>trackHeight</code> is
     * negative undefined results may occur.
     *
     * @param value the slider value to get the location for
     * @param trackY y-origin of the track
     * @param trackHeight the height of the track
     * @since 1.6
     */
    protected int yPositionForValue(int value, int trackY, int trackHeight) {
        int min = slider.getMinimum();
        int max = slider.getMaximum();
        double valueRange = (double)max - (double)min;
        double pixelsPerValue = (double)trackHeight / valueRange;
        int trackBottom = trackY + (trackHeight - 1);
        int yPosition;

        if ( !drawInverted() ) {
            yPosition = trackY;
            yPosition += Math.round( pixelsPerValue * ((double)max - value ) );
        }
        else {
            yPosition = trackY;
            yPosition += Math.round( pixelsPerValue * ((double)value - min) );
        }

        yPosition = Math.max( trackY, yPosition );
        yPosition = Math.min( trackBottom, yPosition );

        return yPosition;
    }

    /**
     * Returns the value at the y position. If {@code yPos} is beyond the
     * track at the the bottom or the top, this method sets the value to either
     * the minimum or maximum value of the slider, depending on if the slider
     * is inverted or not.
     */
    public int valueForYPosition( int yPos ) {
        int value;
        final int minValue = slider.getMinimum();
        final int maxValue = slider.getMaximum();
        final int trackLength = trackRect.height;
        final int trackTop = trackRect.y;
        final int trackBottom = trackRect.y + (trackRect.height - 1);

        if ( yPos <= trackTop ) {
            value = drawInverted() ? minValue : maxValue;
        }
        else if ( yPos >= trackBottom ) {
            value = drawInverted() ? maxValue : minValue;
        }
        else {
            int distanceFromTrackTop = yPos - trackTop;
            double valueRange = (double)maxValue - (double)minValue;
            double valuePerPixel = valueRange / (double)trackLength;
            int valueFromTrackTop = (int)Math.round( distanceFromTrackTop * valuePerPixel );

            value = drawInverted() ? minValue + valueFromTrackTop : maxValue - valueFromTrackTop;
        }

        return value;
    }

    /**
     * Returns the value at the x position.  If {@code xPos} is beyond the
     * track at the left or the right, this method sets the value to either the
     * minimum or maximum value of the slider, depending on if the slider is
     * inverted or not.
     */
    public int valueForXPosition( int xPos ) {
        int value;
        final int minValue = slider.getMinimum();
        final int maxValue = slider.getMaximum();
        final int trackLength = trackRect.width;
        final int trackLeft = trackRect.x;
        final int trackRight = trackRect.x + (trackRect.width - 1);

        if ( xPos <= trackLeft ) {
            value = drawInverted() ? maxValue : minValue;
        }
        else if ( xPos >= trackRight ) {
            value = drawInverted() ? minValue : maxValue;
        }
        else {
            int distanceFromTrackLeft = xPos - trackLeft;
            double valueRange = (double)maxValue - (double)minValue;
            double valuePerPixel = valueRange / (double)trackLength;
            int valueFromTrackLeft = (int)Math.round( distanceFromTrackLeft * valuePerPixel );

            value = drawInverted() ? maxValue - valueFromTrackLeft :
              minValue + valueFromTrackLeft;
        }

        return value;
    }


    private class Handler implements ChangeListener,
            ComponentListener, FocusListener, PropertyChangeListener {
        // Change Handler
        public void stateChanged(ChangeEvent e) {
            if (!isDragging) {
                calculateThumbLocation();
                slider.repaint();
            }
            lastValue = slider.getValue();
        }

        // Component Handler
        public void componentHidden(ComponentEvent e) { }
        public void componentMoved(ComponentEvent e) { }
        public void componentResized(ComponentEvent e) {
            calculateGeometry();
            slider.repaint();
        }
        public void componentShown(ComponentEvent e) { }

        // Focus Handler
        public void focusGained(FocusEvent e) { slider.repaint(); }
        public void focusLost(FocusEvent e) { slider.repaint(); }

        // Property Change Handler
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if (propertyName == "orientation" ||
                    propertyName == "inverted" ||
                    propertyName == "labelTable" ||
                    propertyName == "majorTickSpacing" ||
                    propertyName == "minorTickSpacing" ||
                    propertyName == "paintTicks" ||
                    propertyName == "paintTrack" ||
                    propertyName == "font" ||
                    propertyName == "paintLabels" ||
                    propertyName == "Slider.paintThumbArrowShape") {
                checkedLabelBaselines = false;
                calculateGeometry();
                slider.repaint();
            } else if (propertyName == "componentOrientation") {
                calculateGeometry();
                slider.repaint();
                InputMap km = getInputMap(JComponent.WHEN_FOCUSED, slider);
                SwingUtilities.replaceUIInputMap(slider,
                    JComponent.WHEN_FOCUSED, km);
            } else if (propertyName == "model") {
                ((BoundedRangeModel)e.getOldValue()).removeChangeListener(
                    changeListener);
                ((BoundedRangeModel)e.getNewValue()).addChangeListener(
                    changeListener);
                calculateThumbLocation();
                slider.repaint();
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    /// Model Listener Class
    /////////////////////////////////////////////////////////////////////////
    /**
     * Data model listener.
     *
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <code>Foo</code>.
     */
    public class ChangeHandler implements ChangeListener {
        // NOTE: This class exists only for backward compatibility. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this
        // class calls into the Handler.
        public void stateChanged(ChangeEvent e) {
            getHandler().stateChanged(e);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    /// Track Listener Class
    /////////////////////////////////////////////////////////////////////////
    /**
     * Track mouse movements.
     *
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <code>Foo</code>.
     */
    public class TrackListener extends MouseInputAdapter {
        protected transient int offset;
        protected transient int currentMouseX, currentMouseY;

        public void mouseReleased(MouseEvent e) {
            if (!slider.isEnabled()) {
                return;
            }

            offset = 0;
            scrollTimer.stop();

            isDragging = false;
            slider.setValueIsAdjusting(false);
            slider.repaint();
        }

        /**
        * If the mouse is pressed above the "thumb" component
        * then reduce the scrollbars value by one page ("page up"),
        * otherwise increase it by one page.  If there is no
        * thumb then page up if the mouse is in the upper half
        * of the track.
        */
        public void mousePressed(MouseEvent e) {
            if (!slider.isEnabled()) {
                return;
            }

            // We should recalculate geometry just before
            // calculation of the thumb movement direction.
            // It is important for the case, when JSlider
            // is a cell editor in JTable. See 6348946.
            calculateGeometry();

            currentMouseX = e.getX();
            currentMouseY = e.getY();

            if (slider.isRequestFocusEnabled()) {
                slider.requestFocus();
            }

            // Clicked in the Thumb area?
            if (thumbRect.contains(currentMouseX, currentMouseY)) {
                if (UIManager.getBoolean("Slider.onlyLeftMouseButtonDrag")
                        && !SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                switch (slider.getOrientation()) {
                case JSlider.VERTICAL:
                    offset = currentMouseY - thumbRect.y;
                    break;
                case JSlider.HORIZONTAL:
                    offset = currentMouseX - thumbRect.x;
                    break;
                }
                isDragging = true;
                return;
            }

            if (!SwingUtilities.isLeftMouseButton(e)) {
                return;
            }

            isDragging = false;
            slider.setValueIsAdjusting(true);

            Dimension sbSize = slider.getSize();
            int direction = POSITIVE_SCROLL;

            switch (slider.getOrientation()) {
            case JSlider.VERTICAL:
                if ( thumbRect.isEmpty() ) {
                    int scrollbarCenter = sbSize.height / 2;
                    if ( !drawInverted() ) {
                        direction = (currentMouseY < scrollbarCenter) ?
                            POSITIVE_SCROLL : NEGATIVE_SCROLL;
                    }
                    else {
                        direction = (currentMouseY < scrollbarCenter) ?
                            NEGATIVE_SCROLL : POSITIVE_SCROLL;
                    }
                }
                else {
                    int thumbY = thumbRect.y;
                    if ( !drawInverted() ) {
                        direction = (currentMouseY < thumbY) ?
                            POSITIVE_SCROLL : NEGATIVE_SCROLL;
                    }
                    else {
                        direction = (currentMouseY < thumbY) ?
                            NEGATIVE_SCROLL : POSITIVE_SCROLL;
                    }
                }
                break;
            case JSlider.HORIZONTAL:
                if ( thumbRect.isEmpty() ) {
                    int scrollbarCenter = sbSize.width / 2;
                    if ( !drawInverted() ) {
                        direction = (currentMouseX < scrollbarCenter) ?
                            NEGATIVE_SCROLL : POSITIVE_SCROLL;
                    }
                    else {
                        direction = (currentMouseX < scrollbarCenter) ?
                            POSITIVE_SCROLL : NEGATIVE_SCROLL;
                    }
                }
                else {
                    int thumbX = thumbRect.x;
                    if ( !drawInverted() ) {
                        direction = (currentMouseX < thumbX) ?
                            NEGATIVE_SCROLL : POSITIVE_SCROLL;
                    }
                    else {
                        direction = (currentMouseX < thumbX) ?
                            POSITIVE_SCROLL : NEGATIVE_SCROLL;
                    }
                }
                break;
            }

            if (shouldScroll(direction)) {
                scrollDueToClickInTrack(direction);
            }
            if (shouldScroll(direction)) {
                scrollTimer.stop();
                scrollListener.setDirection(direction);
                scrollTimer.start();
            }
        }

        public boolean shouldScroll(int direction) {
            Rectangle r = thumbRect;
            if (slider.getOrientation() == JSlider.VERTICAL) {
                if (drawInverted() ? direction < 0 : direction > 0) {
                    if (r.y  <= currentMouseY) {
                        return false;
                    }
                }
                else if (r.y + r.height >= currentMouseY) {
                    return false;
                }
            }
            else {
                if (drawInverted() ? direction < 0 : direction > 0) {
                    if (r.x + r.width  >= currentMouseX) {
                        return false;
                    }
                }
                else if (r.x <= currentMouseX) {
                    return false;
                }
            }

            if (direction > 0 && slider.getValue() + slider.getExtent() >=
                    slider.getMaximum()) {
                return false;
            }
            else if (direction < 0 && slider.getValue() <=
                    slider.getMinimum()) {
                return false;
            }

            return true;
        }

        /**
        * Set the models value to the position of the top/left
        * of the thumb relative to the origin of the track.
        */
        public void mouseDragged(MouseEvent e) {
            int thumbMiddle;

            if (!slider.isEnabled()) {
                return;
            }

            currentMouseX = e.getX();
            currentMouseY = e.getY();

            if (!isDragging) {
                return;
            }

            slider.setValueIsAdjusting(true);

            switch (slider.getOrientation()) {
            case JSlider.VERTICAL:
                int halfThumbHeight = thumbRect.height / 2;
                int thumbTop = e.getY() - offset;
                int trackTop = trackRect.y;
                int trackBottom = trackRect.y + (trackRect.height - 1);
                int vMax = yPositionForValue(slider.getMaximum() -
                                            slider.getExtent());

                if (drawInverted()) {
                    trackBottom = vMax;
                }
                else {
                    trackTop = vMax;
                }
                thumbTop = Math.max(thumbTop, trackTop - halfThumbHeight);
                thumbTop = Math.min(thumbTop, trackBottom - halfThumbHeight);

                setThumbLocation(thumbRect.x, thumbTop);

                thumbMiddle = thumbTop + halfThumbHeight;
                slider.setValue( valueForYPosition( thumbMiddle ) );
                break;
            case JSlider.HORIZONTAL:
                int halfThumbWidth = thumbRect.width / 2;
                int thumbLeft = e.getX() - offset;
                int trackLeft = trackRect.x;
                int trackRight = trackRect.x + (trackRect.width - 1);
                int hMax = xPositionForValue(slider.getMaximum() -
                                            slider.getExtent());

                if (drawInverted()) {
                    trackLeft = hMax;
                }
                else {
                    trackRight = hMax;
                }
                thumbLeft = Math.max(thumbLeft, trackLeft - halfThumbWidth);
                thumbLeft = Math.min(thumbLeft, trackRight - halfThumbWidth);

                setThumbLocation(thumbLeft, thumbRect.y);

                thumbMiddle = thumbLeft + halfThumbWidth;
                slider.setValue(valueForXPosition(thumbMiddle));
                break;
            }
        }

        public void mouseMoved(MouseEvent e) { }
    }

    /**
     * Scroll-event listener.
     *
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <code>Foo</code>.
     */
    public class ScrollListener implements ActionListener {
        // changed this class to public to avoid bogus IllegalAccessException
        // bug in InternetExplorer browser.  It was protected.  Work around
        // for 4109432
        int direction = POSITIVE_SCROLL;
        boolean useBlockIncrement;

        public ScrollListener() {
            direction = POSITIVE_SCROLL;
            useBlockIncrement = true;
        }

        public ScrollListener(int dir, boolean block)   {
            direction = dir;
            useBlockIncrement = block;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public void setScrollByBlock(boolean block) {
            this.useBlockIncrement = block;
        }

        public void actionPerformed(ActionEvent e) {
            if (useBlockIncrement) {
                scrollByBlock(direction);
            }
            else {
                scrollByUnit(direction);
            }
            if (!trackListener.shouldScroll(direction)) {
                ((Timer)e.getSource()).stop();
            }
        }
    }

    /**
     * Listener for resizing events.
     * <p>
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <code>Foo</code>.
     */
    public class ComponentHandler extends ComponentAdapter {
        // NOTE: This class exists only for backward compatibility. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this
        // class calls into the Handler.
        public void componentResized(ComponentEvent e)  {
            getHandler().componentResized(e);
        }
    }

    /**
     * Focus-change listener.
     * <p>
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <code>Foo</code>.
     */
    public class FocusHandler implements FocusListener {
        // NOTE: This class exists only for backward compatibility. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this
        // class calls into the Handler.
        public void focusGained(FocusEvent e) {
            getHandler().focusGained(e);
        }

        public void focusLost(FocusEvent e) {
            getHandler().focusLost(e);
        }
    }

    /**
     * As of Java 2 platform v1.3 this undocumented class is no longer used.
     * The recommended approach to creating bindings is to use a
     * combination of an <code>ActionMap</code>, to contain the action,
     * and an <code>InputMap</code> to contain the mapping from KeyStroke
     * to action description. The InputMap is is usually described in the
     * LookAndFeel tables.
     * <p>
     * Please refer to the key bindings specification for further details.
     * <p>
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <code>Foo</code>.
     */
    public class ActionScroller extends AbstractAction {
        // NOTE: This class exists only for backward compatibility. All
        // its functionality has been moved into Actions. If you need to add
        // new functionality add it to the Actions, but make sure this
        // class calls into the Actions.
        int dir;
        boolean block;
        JSlider slider;

        public ActionScroller( JSlider slider, int dir, boolean block) {
            this.dir = dir;
            this.block = block;
            this.slider = slider;
        }

        public void actionPerformed(ActionEvent e) {
            SHARED_ACTION.scroll(slider, javax.swing.plaf.basic.BasicSliderUI.this, dir, block);
        }

        public boolean isEnabled() {
            boolean b = true;
            if (slider != null) {
                b = slider.isEnabled();
            }
            return b;
        }

    }


    /**
     * A static version of the above.
     */
    static class SharedActionScroller extends AbstractAction {
        // NOTE: This class exists only for backward compatibility. All
        // its functionality has been moved into Actions. If you need to add
        // new functionality add it to the Actions, but make sure this
        // class calls into the Actions.
        int dir;
        boolean block;

        public SharedActionScroller(int dir, boolean block) {
            this.dir = dir;
            this.block = block;
        }

        public void actionPerformed(ActionEvent evt) {
            JSlider slider = (JSlider)evt.getSource();
            javax.swing.plaf.basic.BasicSliderUI ui = (javax.swing.plaf.basic.BasicSliderUI)BasicLookAndFeel.getUIOfType(
                    slider.getUI(), javax.swing.plaf.basic.BasicSliderUI.class);
            if (ui == null) {
                return;
            }
            SHARED_ACTION.scroll(slider, ui, dir, block);
        }
    }

    private static class Actions extends UIAction {
        public static final String POSITIVE_UNIT_INCREMENT =
            "positiveUnitIncrement";
        public static final String POSITIVE_BLOCK_INCREMENT =
            "positiveBlockIncrement";
        public static final String NEGATIVE_UNIT_INCREMENT =
            "negativeUnitIncrement";
        public static final String NEGATIVE_BLOCK_INCREMENT =
            "negativeBlockIncrement";
        public static final String MIN_SCROLL_INCREMENT = "minScroll";
        public static final String MAX_SCROLL_INCREMENT = "maxScroll";


        Actions() {
            super(null);
        }

        public Actions(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent evt) {
            JSlider slider = (JSlider)evt.getSource();
            javax.swing.plaf.basic.BasicSliderUI ui = (javax.swing.plaf.basic.BasicSliderUI)BasicLookAndFeel.getUIOfType(
                     slider.getUI(), javax.swing.plaf.basic.BasicSliderUI.class);
            String name = getName();

            if (ui == null) {
                return;
            }
            if (POSITIVE_UNIT_INCREMENT == name) {
                scroll(slider, ui, POSITIVE_SCROLL, false);
            } else if (NEGATIVE_UNIT_INCREMENT == name) {
                scroll(slider, ui, NEGATIVE_SCROLL, false);
            } else if (POSITIVE_BLOCK_INCREMENT == name) {
                scroll(slider, ui, POSITIVE_SCROLL, true);
            } else if (NEGATIVE_BLOCK_INCREMENT == name) {
                scroll(slider, ui, NEGATIVE_SCROLL, true);
            } else if (MIN_SCROLL_INCREMENT == name) {
                scroll(slider, ui, MIN_SCROLL, false);
            } else if (MAX_SCROLL_INCREMENT == name) {
                scroll(slider, ui, MAX_SCROLL, false);
            }
        }

        private void scroll(JSlider slider, javax.swing.plaf.basic.BasicSliderUI ui, int direction,
                            boolean isBlock) {
            boolean invert = slider.getInverted();

            if (direction == NEGATIVE_SCROLL || direction == POSITIVE_SCROLL) {
                if (invert) {
                    direction = (direction == POSITIVE_SCROLL) ?
                        NEGATIVE_SCROLL : POSITIVE_SCROLL;
                }

                if (isBlock) {
                    ui.scrollByBlock(direction);
                } else {
                    ui.scrollByUnit(direction);
                }
            } else {  // MIN or MAX
                if (invert) {
                    direction = (direction == MIN_SCROLL) ?
                        MAX_SCROLL : MIN_SCROLL;
                }

                slider.setValue((direction == MIN_SCROLL) ?
                    slider.getMinimum() : slider.getMaximum());
            }
        }
    }
}
