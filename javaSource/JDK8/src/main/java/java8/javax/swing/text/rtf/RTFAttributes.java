/*
 * Copyright (c) 1997, 2008, Oracle and/or its affiliates. All rights reserved.
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
package java8.javax.swing.text.rtf;

import javax.swing.text.StyleConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.TabStop;
import javax.swing.text.rtf.Constants;
import javax.swing.text.rtf.RTFAttribute;
import javax.swing.text.rtf.RTFGenerator;
import javax.swing.text.rtf.RTFReader;
import java.util.*;
import java.io.IOException;

class RTFAttributes
{
    static javax.swing.text.rtf.RTFAttribute attributes[];

    static {
        Vector<javax.swing.text.rtf.RTFAttribute> a = new Vector<javax.swing.text.rtf.RTFAttribute>();
        int CHR = javax.swing.text.rtf.RTFAttribute.D_CHARACTER;
        int PGF = javax.swing.text.rtf.RTFAttribute.D_PARAGRAPH;
        int SEC = javax.swing.text.rtf.RTFAttribute.D_SECTION;
        int DOC = javax.swing.text.rtf.RTFAttribute.D_DOCUMENT;
        int PST = javax.swing.text.rtf.RTFAttribute.D_META;
        Boolean True = Boolean.valueOf(true);
        Boolean False = Boolean.valueOf(false);

        a.addElement(new BooleanAttribute(CHR, StyleConstants.Italic, "i"));
        a.addElement(new BooleanAttribute(CHR, StyleConstants.Bold, "b"));
        a.addElement(new BooleanAttribute(CHR, StyleConstants.Underline, "ul"));
        a.addElement(NumericAttribute.NewTwips(PGF, StyleConstants.LeftIndent, "li",
                                        0f, 0));
        a.addElement(NumericAttribute.NewTwips(PGF, StyleConstants.RightIndent, "ri",
                                        0f, 0));
        a.addElement(NumericAttribute.NewTwips(PGF, StyleConstants.FirstLineIndent, "fi",
                                        0f, 0));

        a.addElement(new AssertiveAttribute(PGF, StyleConstants.Alignment,
                                            "ql", StyleConstants.ALIGN_LEFT));
        a.addElement(new AssertiveAttribute(PGF, StyleConstants.Alignment,
                                            "qr", StyleConstants.ALIGN_RIGHT));
        a.addElement(new AssertiveAttribute(PGF, StyleConstants.Alignment,
                                            "qc", StyleConstants.ALIGN_CENTER));
        a.addElement(new AssertiveAttribute(PGF, StyleConstants.Alignment,
                                            "qj", StyleConstants.ALIGN_JUSTIFIED));
        a.addElement(NumericAttribute.NewTwips(PGF, StyleConstants.SpaceAbove,
                                        "sa", 0));
        a.addElement(NumericAttribute.NewTwips(PGF, StyleConstants.SpaceBelow,
                                        "sb", 0));

        a.addElement(new AssertiveAttribute(PST, javax.swing.text.rtf.RTFReader.TabAlignmentKey,
                                            "tqr", TabStop.ALIGN_RIGHT));
        a.addElement(new AssertiveAttribute(PST, javax.swing.text.rtf.RTFReader.TabAlignmentKey,
                                            "tqc", TabStop.ALIGN_CENTER));
        a.addElement(new AssertiveAttribute(PST, javax.swing.text.rtf.RTFReader.TabAlignmentKey,
                                            "tqdec", TabStop.ALIGN_DECIMAL));


        a.addElement(new AssertiveAttribute(PST, javax.swing.text.rtf.RTFReader.TabLeaderKey,
                                            "tldot", TabStop.LEAD_DOTS));
        a.addElement(new AssertiveAttribute(PST, javax.swing.text.rtf.RTFReader.TabLeaderKey,
                                            "tlhyph", TabStop.LEAD_HYPHENS));
        a.addElement(new AssertiveAttribute(PST, javax.swing.text.rtf.RTFReader.TabLeaderKey,
                                            "tlul", TabStop.LEAD_UNDERLINE));
        a.addElement(new AssertiveAttribute(PST, javax.swing.text.rtf.RTFReader.TabLeaderKey,
                                            "tlth", TabStop.LEAD_THICKLINE));
        a.addElement(new AssertiveAttribute(PST, javax.swing.text.rtf.RTFReader.TabLeaderKey,
                                            "tleq", TabStop.LEAD_EQUALS));

        /* The following aren't actually recognized by Swing */
        a.addElement(new BooleanAttribute(CHR, javax.swing.text.rtf.Constants.Caps,      "caps"));
        a.addElement(new BooleanAttribute(CHR, javax.swing.text.rtf.Constants.Outline,   "outl"));
        a.addElement(new BooleanAttribute(CHR, javax.swing.text.rtf.Constants.SmallCaps, "scaps"));
        a.addElement(new BooleanAttribute(CHR, javax.swing.text.rtf.Constants.Shadow,    "shad"));
        a.addElement(new BooleanAttribute(CHR, javax.swing.text.rtf.Constants.Hidden,    "v"));
        a.addElement(new BooleanAttribute(CHR, javax.swing.text.rtf.Constants.Strikethrough,
                                               "strike"));
        a.addElement(new BooleanAttribute(CHR, javax.swing.text.rtf.Constants.Deleted,
                                               "deleted"));



        a.addElement(new AssertiveAttribute(DOC, "saveformat", "defformat", "RTF"));
        a.addElement(new AssertiveAttribute(DOC, "landscape", "landscape"));

        a.addElement(NumericAttribute.NewTwips(DOC, javax.swing.text.rtf.Constants.PaperWidth,
                                               "paperw", 12240));
        a.addElement(NumericAttribute.NewTwips(DOC, javax.swing.text.rtf.Constants.PaperHeight,
                                               "paperh", 15840));
        a.addElement(NumericAttribute.NewTwips(DOC, javax.swing.text.rtf.Constants.MarginLeft,
                                               "margl",  1800));
        a.addElement(NumericAttribute.NewTwips(DOC, javax.swing.text.rtf.Constants.MarginRight,
                                               "margr",  1800));
        a.addElement(NumericAttribute.NewTwips(DOC, javax.swing.text.rtf.Constants.MarginTop,
                                               "margt",  1440));
        a.addElement(NumericAttribute.NewTwips(DOC, javax.swing.text.rtf.Constants.MarginBottom,
                                               "margb",  1440));
        a.addElement(NumericAttribute.NewTwips(DOC, javax.swing.text.rtf.Constants.GutterWidth,
                                               "gutter", 0));

        a.addElement(new AssertiveAttribute(PGF, javax.swing.text.rtf.Constants.WidowControl,
                                            "nowidctlpar", False));
        a.addElement(new AssertiveAttribute(PGF, javax.swing.text.rtf.Constants.WidowControl,
                                            "widctlpar", True));
        a.addElement(new AssertiveAttribute(DOC, javax.swing.text.rtf.Constants.WidowControl,
                                            "widowctrl", True));


        javax.swing.text.rtf.RTFAttribute[] attrs = new javax.swing.text.rtf.RTFAttribute[a.size()];
        a.copyInto(attrs);
        attributes = attrs;
    }

    static Dictionary<String, javax.swing.text.rtf.RTFAttribute> attributesByKeyword()
    {
        Dictionary<String, javax.swing.text.rtf.RTFAttribute> d = new Hashtable<String, javax.swing.text.rtf.RTFAttribute>(attributes.length);

        for (javax.swing.text.rtf.RTFAttribute attribute : attributes) {
            d.put(attribute.rtfName(), attribute);
        }

        return d;
    }

    /************************************************************************/
    /************************************************************************/

    static abstract class GenericAttribute
    {
        int domain;
        Object swingName;
        String rtfName;

        protected GenericAttribute(int d,Object s, String r)
        {
            domain = d;
            swingName = s;
            rtfName = r;
        }

        public int domain() { return domain; }
        public Object swingName() { return swingName; }
        public String rtfName() { return rtfName; }

        abstract boolean set(MutableAttributeSet target);
        abstract boolean set(MutableAttributeSet target, int parameter);
        abstract boolean setDefault(MutableAttributeSet target);

        public boolean write(AttributeSet source,
                             javax.swing.text.rtf.RTFGenerator target,
                             boolean force)
            throws IOException
        {
            return writeValue(source.getAttribute(swingName), target, force);
        }

        public boolean writeValue(Object value, javax.swing.text.rtf.RTFGenerator target,
                                  boolean force)
            throws IOException
        {
            return false;
        }
    }

    static class BooleanAttribute
        extends GenericAttribute
        implements javax.swing.text.rtf.RTFAttribute
    {
        boolean rtfDefault;
        boolean swingDefault;

        protected static final Boolean True = Boolean.valueOf(true);
        protected static final Boolean False = Boolean.valueOf(false);

        public BooleanAttribute(int d, Object s,
                                String r, boolean ds, boolean dr)
        {
            super(d, s, r);
            swingDefault = ds;
            rtfDefault = dr;
        }

        public BooleanAttribute(int d, Object s, String r)
        {
            super(d, s, r);

            swingDefault = false;
            rtfDefault = false;
        }

        public boolean set(MutableAttributeSet target)
        {
            /* TODO: There's some ambiguity about whether this should
               *set* or *toggle* the attribute. */
            target.addAttribute(swingName, True);

            return true;  /* true indicates we were successful */
        }

        public boolean set(MutableAttributeSet target, int parameter)
        {
            /* See above note in the case that parameter==1 */
            Boolean value = ( parameter != 0 ? True : False );

            target.addAttribute(swingName, value);

            return true; /* true indicates we were successful */
        }

        public boolean setDefault(MutableAttributeSet target)
        {
            if (swingDefault != rtfDefault ||
                ( target.getAttribute(swingName) != null ) )
              target.addAttribute(swingName, Boolean.valueOf(rtfDefault));
            return true;
        }

        public boolean writeValue(Object o_value,
                                  javax.swing.text.rtf.RTFGenerator target,
                                  boolean force)
            throws IOException
        {
            Boolean val;

            if (o_value == null)
              val = Boolean.valueOf(swingDefault);
            else
              val = (Boolean)o_value;

            if (force || (val.booleanValue() != rtfDefault)) {
                if (val.booleanValue()) {
                    target.writeControlWord(rtfName);
                } else {
                    target.writeControlWord(rtfName, 0);
                }
            }
            return true;
        }
    }


    static class AssertiveAttribute
        extends GenericAttribute
        implements javax.swing.text.rtf.RTFAttribute
    {
        Object swingValue;

        public AssertiveAttribute(int d, Object s, String r)
        {
            super(d, s, r);
            swingValue = Boolean.valueOf(true);
        }

        public AssertiveAttribute(int d, Object s, String r, Object v)
        {
            super(d, s, r);
            swingValue = v;
        }

        public AssertiveAttribute(int d, Object s, String r, int v)
        {
            super(d, s, r);
            swingValue = Integer.valueOf(v);
        }

        public boolean set(MutableAttributeSet target)
        {
            if (swingValue == null)
                target.removeAttribute(swingName);
            else
                target.addAttribute(swingName, swingValue);

            return true;
        }

        public boolean set(MutableAttributeSet target, int parameter)
        {
            return false;
        }

        public boolean setDefault(MutableAttributeSet target)
        {
            target.removeAttribute(swingName);
            return true;
        }

        public boolean writeValue(Object value,
                                  javax.swing.text.rtf.RTFGenerator target,
                                  boolean force)
            throws IOException
        {
            if (value == null) {
                return ! force;
            }

            if (value.equals(swingValue)) {
                target.writeControlWord(rtfName);
                return true;
            }

            return ! force;
        }
    }


    static class NumericAttribute
        extends GenericAttribute
        implements javax.swing.text.rtf.RTFAttribute
    {
        int rtfDefault;
        Number swingDefault;
        float scale;

        protected NumericAttribute(int d, Object s, String r)
        {
            super(d, s, r);
            rtfDefault = 0;
            swingDefault = null;
            scale = 1f;
        }

        public NumericAttribute(int d, Object s,
                                String r, int ds, int dr)
        {
            this(d, s, r, Integer.valueOf(ds), dr, 1f);
        }

        public NumericAttribute(int d, Object s,
                                String r, Number ds, int dr, float sc)
        {
            super(d, s, r);
            swingDefault = ds;
            rtfDefault = dr;
            scale = sc;
        }

        public static NumericAttribute NewTwips(int d, Object s, String r,
                                                float ds, int dr)
        {
            return new NumericAttribute(d, s, r, new Float(ds), dr, 20f);
        }

        public static NumericAttribute NewTwips(int d, Object s, String r,
                                                int dr)
        {
            return new NumericAttribute(d, s, r, null, dr, 20f);
        }

        public boolean set(MutableAttributeSet target)
        {
            return false;
        }

        public boolean set(MutableAttributeSet target, int parameter)
        {
            Number swingValue;

            if (scale == 1f)
                swingValue = Integer.valueOf(parameter);
            else
                swingValue = new Float(parameter / scale);
            target.addAttribute(swingName, swingValue);
            return true;
        }

        public boolean setDefault(MutableAttributeSet target)
        {
            Number old = (Number)target.getAttribute(swingName);
            if (old == null)
                old = swingDefault;
            if (old != null && (
                    (scale == 1f && old.intValue() == rtfDefault) ||
                    (Math.round(old.floatValue() * scale) == rtfDefault)
               ))
                return true;
            set(target, rtfDefault);
            return true;
        }

        public boolean writeValue(Object o_value,
                                  javax.swing.text.rtf.RTFGenerator target,
                                  boolean force)
            throws IOException
        {
            Number value = (Number)o_value;
            if (value == null)
                value = swingDefault;
            if (value == null) {
                /* TODO: What is the proper behavior if the Swing object does
                   not specify a value, and we don't know its default value?
                   Currently we pretend that the RTF default value is
                   equivalent (probably a workable assumption) */
                return true;
            }
            int int_value = Math.round(value.floatValue() * scale);
            if (force || (int_value != rtfDefault))
                target.writeControlWord(rtfName, int_value);
            return true;
        }
    }
}
