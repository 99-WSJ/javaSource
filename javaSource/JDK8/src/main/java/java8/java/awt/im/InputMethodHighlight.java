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

package java8.java.awt.im;

import java.awt.font.TextAttribute;
import java.util.Map;

/**
* An InputMethodHighlight is used to describe the highlight
* attributes of text being composed.
* The description can be at two levels:
* at the abstract level it specifies the conversion state and whether the
* text is selected; at the concrete level it specifies style attributes used
* to render the highlight.
* An InputMethodHighlight must provide the description at the
* abstract level; it may or may not provide the description at the concrete
* level.
* If no concrete style is provided, a renderer should use
* {@link java.awt.Toolkit#mapInputMethodHighlight} to map to a concrete style.
* <p>
* The abstract description consists of three fields: <code>selected</code>,
* <code>state</code>, and <code>variation</code>.
* <code>selected</code> indicates whether the text range is the one that the
* input method is currently working on, for example, the segment for which
* conversion candidates are currently shown in a menu.
* <code>state</code> represents the conversion state. State values are defined
* by the input method framework and should be distinguished in all
* mappings from abstract to concrete styles. Currently defined state values
* are raw (unconverted) and converted.
* These state values are recommended for use before and after the
* main conversion step of text composition, say, before and after kana-&gt;kanji
* or pinyin-&gt;hanzi conversion.
* The <code>variation</code> field allows input methods to express additional
* information about the conversion results.
* <p>
*
* InputMethodHighlight instances are typically used as attribute values
* returned from AttributedCharacterIterator for the INPUT_METHOD_HIGHLIGHT
* attribute. They may be wrapped into {@link java.text.Annotation Annotation}
* instances to indicate separate text segments.
*
* @see java.text.AttributedCharacterIterator
* @since 1.2
*/

public class InputMethodHighlight {

    /**
     * Constant for the raw text state.
     */
    public final static int RAW_TEXT = 0;

    /**
     * Constant for the converted text state.
     */
    public final static int CONVERTED_TEXT = 1;


    /**
     * Constant for the default highlight for unselected raw text.
     */
    public final static java.awt.im.InputMethodHighlight UNSELECTED_RAW_TEXT_HIGHLIGHT =
        new java.awt.im.InputMethodHighlight(false, RAW_TEXT);

    /**
     * Constant for the default highlight for selected raw text.
     */
    public final static java.awt.im.InputMethodHighlight SELECTED_RAW_TEXT_HIGHLIGHT =
        new java.awt.im.InputMethodHighlight(true, RAW_TEXT);

    /**
     * Constant for the default highlight for unselected converted text.
     */
    public final static java.awt.im.InputMethodHighlight UNSELECTED_CONVERTED_TEXT_HIGHLIGHT =
        new java.awt.im.InputMethodHighlight(false, CONVERTED_TEXT);

    /**
     * Constant for the default highlight for selected converted text.
     */
    public final static java.awt.im.InputMethodHighlight SELECTED_CONVERTED_TEXT_HIGHLIGHT =
        new java.awt.im.InputMethodHighlight(true, CONVERTED_TEXT);


    /**
     * Constructs an input method highlight record.
     * The variation is set to 0, the style to null.
     * @param selected Whether the text range is selected
     * @param state The conversion state for the text range - RAW_TEXT or CONVERTED_TEXT
     * @see java.awt.im.InputMethodHighlight#RAW_TEXT
     * @see java.awt.im.InputMethodHighlight#CONVERTED_TEXT
     * @exception IllegalArgumentException if a state other than RAW_TEXT or CONVERTED_TEXT is given
     */
    public InputMethodHighlight(boolean selected, int state) {
        this(selected, state, 0, null);
    }

    /**
     * Constructs an input method highlight record.
     * The style is set to null.
     * @param selected Whether the text range is selected
     * @param state The conversion state for the text range - RAW_TEXT or CONVERTED_TEXT
     * @param variation The style variation for the text range
     * @see java.awt.im.InputMethodHighlight#RAW_TEXT
     * @see java.awt.im.InputMethodHighlight#CONVERTED_TEXT
     * @exception IllegalArgumentException if a state other than RAW_TEXT or CONVERTED_TEXT is given
     */
    public InputMethodHighlight(boolean selected, int state, int variation) {
        this(selected, state, variation, null);
    }

    /**
     * Constructs an input method highlight record.
     * The style attributes map provided must be unmodifiable.
     * @param selected whether the text range is selected
     * @param state the conversion state for the text range - RAW_TEXT or CONVERTED_TEXT
     * @param variation the variation for the text range
     * @param style the rendering style attributes for the text range, or null
     * @see java.awt.im.InputMethodHighlight#RAW_TEXT
     * @see java.awt.im.InputMethodHighlight#CONVERTED_TEXT
     * @exception IllegalArgumentException if a state other than RAW_TEXT or CONVERTED_TEXT is given
     * @since 1.3
     */
    public InputMethodHighlight(boolean selected, int state, int variation,
                                Map<TextAttribute,?> style)
    {
        this.selected = selected;
        if (!(state == RAW_TEXT || state == CONVERTED_TEXT)) {
            throw new IllegalArgumentException("unknown input method highlight state");
        }
        this.state = state;
        this.variation = variation;
        this.style = style;
    }

    /**
     * Returns whether the text range is selected.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Returns the conversion state of the text range.
     * @return The conversion state for the text range - RAW_TEXT or CONVERTED_TEXT.
     * @see java.awt.im.InputMethodHighlight#RAW_TEXT
     * @see java.awt.im.InputMethodHighlight#CONVERTED_TEXT
     */
    public int getState() {
        return state;
    }

    /**
     * Returns the variation of the text range.
     */
    public int getVariation() {
        return variation;
    }

    /**
     * Returns the rendering style attributes for the text range, or null.
     * @since 1.3
     */
    public Map<TextAttribute,?> getStyle() {
        return style;
    }

    private boolean selected;
    private int state;
    private int variation;
    private Map<TextAttribute, ?> style;

};
