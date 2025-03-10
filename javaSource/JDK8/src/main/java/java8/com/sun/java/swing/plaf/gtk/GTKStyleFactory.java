/*
 * Copyright (c) 2002, 2012, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.java.swing.plaf.gtk;

import com.sun.java.swing.plaf.gtk.GTKEngine.WidgetType;

import javax.swing.*;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthStyleFactory;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Scott Violet
 */
class GTKStyleFactory extends SynthStyleFactory {

    /**
     * Saves all styles that have been accessed.  In most common cases,
     * the hash key is simply the WidgetType, but in more complex cases
     * it will be a ComplexKey object that contains arguments to help
     * differentiate similar styles.
     */
    private final Map<Object, GTKStyle> stylesCache;

    private Font defaultFont;

    GTKStyleFactory() {
        stylesCache = new HashMap<Object, GTKStyle>();
    }

    /**
     * Returns the <code>GTKStyle</code> to use based on the
     * <code>Region</code> id
     *
     * @param c this parameter isn't used, may be null.
     * @param id of the region to get the style.
     */
    public synchronized SynthStyle getStyle(JComponent c, Region id) {
        WidgetType wt = GTKEngine.getWidgetType(c, id);

        Object key = null;
        if (id == Region.SCROLL_BAR) {
            // The style/insets of a scrollbar can depend on a number of
            // factors (see GTKStyle.getScrollBarInsets()) so use a
            // complex key here.
            if (c != null) {
                JScrollBar sb = (JScrollBar)c;
                boolean sp = (sb.getParent() instanceof JScrollPane);
                boolean horiz = (sb.getOrientation() == JScrollBar.HORIZONTAL);
                boolean ltr = sb.getComponentOrientation().isLeftToRight();
                boolean focusable = sb.isFocusable();
                key = new ComplexKey(wt, sp, horiz, ltr, focusable);
            }
        }
        else if (id == Region.CHECK_BOX || id == Region.RADIO_BUTTON) {
            // The style/insets of a checkbox or radiobutton can depend
            // on the component orientation, so use a complex key here.
            if (c != null) {
                boolean ltr = c.getComponentOrientation().isLeftToRight();
                key = new ComplexKey(wt, ltr);
            }
        }
        else if (id == Region.BUTTON) {
            // The style/insets of a button can depend on whether it is
            // default capable or in a toolbar, so use a complex key here.
            if (c != null) {
                JButton btn = (JButton)c;
                boolean toolButton = (btn.getParent() instanceof JToolBar);
                boolean defaultCapable = btn.isDefaultCapable();
                key = new ComplexKey(wt, toolButton, defaultCapable);
            }
        } else if (id == Region.MENU) {
            if (c instanceof JMenu && ((JMenu) c).isTopLevelMenu() &&
                    UIManager.getBoolean("Menu.useMenuBarForTopLevelMenus")) {
                wt = WidgetType.MENU_BAR;
            }
        }

        if (key == null) {
            // Otherwise, just use the WidgetType as the key.
            key = wt;
        }

        GTKStyle result = stylesCache.get(key);
        if (result == null) {
            result = new GTKStyle(defaultFont, wt);
            stylesCache.put(key, result);
        }

        return result;
    }

    void initStyles(Font defaultFont) {
        this.defaultFont = defaultFont;
        stylesCache.clear();
    }

    /**
     * Represents a hash key used for fetching GTKStyle objects from the
     * cache.  In most cases only the WidgetType is used for lookup, but
     * in some complex cases, other Object arguments can be specified
     * via a ComplexKey to differentiate the various styles.
     */
    private static class ComplexKey {
        private final WidgetType wt;
        private final Object[] args;

        ComplexKey(WidgetType wt, Object... args) {
            this.wt = wt;
            this.args = args;
        }

        @Override
        public int hashCode() {
            int hash = wt.hashCode();
            if (args != null) {
                for (Object arg : args) {
                    hash = hash*29 + (arg == null ? 0 : arg.hashCode());
                }
            }
            return hash;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ComplexKey)) {
                return false;
            }
            ComplexKey that = (ComplexKey)o;
            if (this.wt == that.wt) {
                if (this.args == null && that.args == null) {
                    return true;
                }
                if (this.args != null && that.args != null &&
                    this.args.length == that.args.length)
                {
                    for (int i = 0; i < this.args.length; i++) {
                        Object a1 = this.args[i];
                        Object a2 = that.args[i];
                        if (!(a1==null ? a2==null : a1.equals(a2))) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            String str = "ComplexKey[wt=" + wt;
            if (args != null) {
                str += ",args=[";
                for (int i = 0; i < args.length; i++) {
                    str += args[i];
                    if (i < args.length-1) str += ",";
                }
                str += "]";
            }
            str += "]";
            return str;
        }
    }
}
