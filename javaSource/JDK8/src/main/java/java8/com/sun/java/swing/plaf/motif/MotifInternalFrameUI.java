/*
 * Copyright (c) 1997, 2004, Oracle and/or its affiliates. All rights reserved.
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

package java8.com.sun.java.swing.plaf.motif;

import com.sun.java.swing.plaf.motif.MotifBorders;
import com.sun.java.swing.plaf.motif.MotifDesktopIconUI;
import com.sun.java.swing.plaf.motif.MotifInternalFrameTitlePane;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


/**
 * A Motif L&F implementation of InternalFrame.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @author Tom Ball
 */
public class MotifInternalFrameUI extends BasicInternalFrameUI {

    Color color;
    Color highlight;
    Color shadow;
    MotifInternalFrameTitlePane titlePane;

    /**
     * As of Java 2 platform v1.3 this previously undocumented field is no
     * longer used.
     * Key bindings are now defined by the LookAndFeel, please refer to
     * the key bindings specification for further details.
     *
     * @deprecated As of Java 2 platform v1.3.
     */
    @Deprecated
    protected KeyStroke closeMenuKey;


/////////////////////////////////////////////////////////////////////////////
// ComponentUI Interface Implementation methods
/////////////////////////////////////////////////////////////////////////////
    public static ComponentUI createUI(JComponent w)    {
        return new com.sun.java.swing.plaf.motif.MotifInternalFrameUI((JInternalFrame)w);
    }

    public MotifInternalFrameUI(JInternalFrame w)   {
        super(w);
    }

    public void installUI(JComponent c)   {
        super.installUI(c);
        setColors((JInternalFrame)c);
    }

    protected void installDefaults() {
        Border frameBorder = frame.getBorder();
        frame.setLayout(internalFrameLayout = createLayoutManager());
        if (frameBorder == null || frameBorder instanceof UIResource) {
            frame.setBorder(new MotifBorders.InternalFrameBorder(frame));
        }
    }


    protected void installKeyboardActions(){
      super.installKeyboardActions();
      // We replace the
      // we use JPopup in our TitlePane so need escape support
      closeMenuKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    }


    protected void uninstallDefaults() {
        LookAndFeel.uninstallBorder(frame);
        frame.setLayout(null);
        internalFrameLayout = null;
    }

    private JInternalFrame getFrame(){
      return frame;
    }

    public JComponent createNorthPane(JInternalFrame w) {
        titlePane = new MotifInternalFrameTitlePane(w);
        return titlePane;
    }

    public Dimension getMaximumSize(JComponent x) {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    protected void uninstallKeyboardActions(){
      super.uninstallKeyboardActions();
      if (isKeyBindingRegistered()){
        JInternalFrame.JDesktopIcon di = frame.getDesktopIcon();
        SwingUtilities.replaceUIActionMap(di, null);
        SwingUtilities.replaceUIInputMap(di, JComponent.WHEN_IN_FOCUSED_WINDOW,
                                         null);
      }
    }

    protected void setupMenuOpenKey(){
        super.setupMenuOpenKey();
        ActionMap map = SwingUtilities.getUIActionMap(frame);
        if (map != null) {
            // BasicInternalFrameUI creates an action with the same name, we override
            // it as MotifInternalFrameTitlePane has a titlePane ivar that shadows the
            // titlePane ivar in BasicInternalFrameUI, making supers action throw
            // an NPE for us.
            map.put("showSystemMenu", new AbstractAction(){
                public void actionPerformed(ActionEvent e){
                    titlePane.showSystemMenu();
                }
                public boolean isEnabled(){
                    return isKeyBindingActive();
                }
            });
        }
    }

    protected void setupMenuCloseKey(){
        ActionMap map = SwingUtilities.getUIActionMap(frame);
        if (map != null) {
            map.put("hideSystemMenu", new AbstractAction(){
                public void actionPerformed(ActionEvent e){
                    titlePane.hideSystemMenu();
                }
                public boolean isEnabled(){
                    return isKeyBindingActive();
                }
            });
        }

        // Set up the bindings for the DesktopIcon, it is odd that
        // we install them, and not the desktop icon.
        JInternalFrame.JDesktopIcon di = frame.getDesktopIcon();
        InputMap diInputMap = SwingUtilities.getUIInputMap
                          (di, JComponent.WHEN_IN_FOCUSED_WINDOW);
        if (diInputMap == null) {
            Object[] bindings = (Object[])UIManager.get
                                          ("DesktopIcon.windowBindings");
            if (bindings != null) {
                diInputMap = LookAndFeel.makeComponentInputMap(di, bindings);

                SwingUtilities.replaceUIInputMap(di, JComponent.
                                               WHEN_IN_FOCUSED_WINDOW,
                                               diInputMap);
            }
        }
        ActionMap diActionMap = SwingUtilities.getUIActionMap(di);
        if (diActionMap == null) {
            diActionMap = new ActionMapUIResource();
            diActionMap.put("hideSystemMenu", new AbstractAction(){
                public void actionPerformed(ActionEvent e){
                    JInternalFrame.JDesktopIcon icon = getFrame().
                                     getDesktopIcon();
                    MotifDesktopIconUI micon = (MotifDesktopIconUI)icon.
                                               getUI();
                    micon.hideSystemMenu();
                }
                public boolean isEnabled(){
                    return isKeyBindingActive();
                }
            });
            SwingUtilities.replaceUIActionMap(di, diActionMap);
        }
    }

    /** This method is called when the frame becomes selected.
      */
    protected void activateFrame(JInternalFrame f) {
        super.activateFrame(f);
        setColors(f);
    }
    /** This method is called when the frame is no longer selected.
      */
    protected void deactivateFrame(JInternalFrame f) {
        setColors(f);
        super.deactivateFrame(f);
    }

    void setColors(JInternalFrame frame) {
        if (frame.isSelected()) {
            color = UIManager.getColor("InternalFrame.activeTitleBackground");
        } else {
            color = UIManager.getColor("InternalFrame.inactiveTitleBackground");
        }
        highlight = color.brighter();
        shadow = color.darker().darker();
        titlePane.setColors(color, highlight, shadow);
    }
}
