/*
 * Copyright (c) 2008, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.swing.colorchooser;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import static java.util.Locale.ENGLISH;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DocumentFilter;

final class ValueFormatter extends AbstractFormatter implements FocusListener, Runnable {

    static void init(int length, boolean hex, JFormattedTextField text) {
        javax.swing.colorchooser.ValueFormatter formatter = new javax.swing.colorchooser.ValueFormatter(length, hex);
        text.setColumns(length);
        text.setFormatterFactory(new DefaultFormatterFactory(formatter));
        text.setHorizontalAlignment(SwingConstants.RIGHT);
        text.setMinimumSize(text.getPreferredSize());
        text.addFocusListener(formatter);
    }

    private final DocumentFilter filter = new DocumentFilter() {
        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (isValid(fb.getDocument().getLength() - length)) {
                fb.remove(offset, length);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet set) throws BadLocationException {
            if (isValid(fb.getDocument().getLength() + text.length() - length) && isValid(text)) {
                fb.replace(offset, length, text.toUpperCase(ENGLISH), set);
            }
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet set) throws BadLocationException {
            if (isValid(fb.getDocument().getLength() + text.length()) && isValid(text)) {
                fb.insertString(offset, text.toUpperCase(ENGLISH), set);
            }
        }
    };

    private final int length;
    private final int radix;

    private JFormattedTextField text;

    ValueFormatter(int length, boolean hex) {
        this.length = length;
        this.radix = hex ? 16 : 10;
    }

    @Override
    public Object stringToValue(String text) throws ParseException {
        try {
            return Integer.valueOf(text, this.radix);
        }
        catch (NumberFormatException nfe) {
            ParseException pe = new ParseException("illegal format", 0);
            pe.initCause(nfe);
            throw pe;
        }
    }

    @Override
    public String valueToString(Object object) throws ParseException {
        if (object instanceof Integer) {
            if (this.radix == 10) {
                return object.toString();
            }
            int value = (Integer) object;
            int index = this.length;
            char[] array = new char[index];
            while (0 < index--) {
                array[index] = Character.forDigit(value & 0x0F, this.radix);
                value >>= 4;
            }
            return new String(array).toUpperCase(ENGLISH);
        }
        throw new ParseException("illegal object", 0);
    }

    @Override
    protected DocumentFilter getDocumentFilter() {
        return this.filter;
    }

    public void focusGained(FocusEvent event) {
        Object source = event.getSource();
        if (source instanceof JFormattedTextField) {
            this.text = (JFormattedTextField) source;
            SwingUtilities.invokeLater(this);
        }
    }

    public void focusLost(FocusEvent event) {
    }

    public void run() {
        if (this.text != null) {
            this.text.selectAll();
        }
    }

    private boolean isValid(int length) {
        return (0 <= length) && (length <= this.length);
    }

    private boolean isValid(String text) {
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            if (Character.digit(ch, this.radix) < 0) {
                return false;
            }
        }
        return true;
    }
}
