/*
 * Copyright (c) 1998, 2009, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.swing.plaf.metal;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.MetalInternalFrameTitlePane;
import javax.swing.plaf.metal.MetalUtils;

/**
 * Metal implementation of JInternalFrame.
 * <p>
 *
 * @author Steve Wilson
 */
public class MetalInternalFrameUI extends BasicInternalFrameUI {

  private static final PropertyChangeListener metalPropertyChangeListener =
        new MetalPropertyChangeHandler();

  private static final Border handyEmptyBorder = new EmptyBorder(0,0,0,0);

  protected static String IS_PALETTE   = "JInternalFrame.isPalette";
  private static String IS_PALETTE_KEY = "JInternalFrame.isPalette";
  private static String FRAME_TYPE     = "JInternalFrame.frameType";
  private static String NORMAL_FRAME   = "normal";
  private static String PALETTE_FRAME  = "palette";
  private static String OPTION_DIALOG  = "optionDialog";

  public MetalInternalFrameUI(JInternalFrame b)   {
    super(b);
  }

  public static ComponentUI createUI(JComponent c)    {
      return new javax.swing.plaf.metal.MetalInternalFrameUI( (JInternalFrame) c);
  }

  public void installUI(JComponent c) {
    super.installUI(c);

    Object paletteProp = c.getClientProperty(IS_PALETTE_KEY);
    if ( paletteProp != null ) {
        setPalette( ((Boolean)paletteProp).booleanValue() );
    }

    Container content = frame.getContentPane();
    stripContentBorder(content);
    //c.setOpaque(false);
  }

  public void uninstallUI(JComponent c) {
      frame = (JInternalFrame)c;

      Container cont = ((JInternalFrame)(c)).getContentPane();
      if (cont instanceof JComponent) {
        JComponent content = (JComponent)cont;
        if ( content.getBorder() == handyEmptyBorder) {
          content.setBorder(null);
        }
      }
      super.uninstallUI(c);
  }

    protected void installListeners() {
        super.installListeners();
        frame.addPropertyChangeListener(metalPropertyChangeListener);
    }

    protected void uninstallListeners() {
        frame.removePropertyChangeListener(metalPropertyChangeListener);
        super.uninstallListeners();
    }

  protected void installKeyboardActions(){
      super.installKeyboardActions();
      ActionMap map = SwingUtilities.getUIActionMap(frame);
      if (map != null) {
          // BasicInternalFrameUI creates an action with the same name, we override
          // it as Metal frames do not have system menus.
          map.remove("showSystemMenu");
      }
  }

  protected void uninstallKeyboardActions(){
      super.uninstallKeyboardActions();
  }

    protected void uninstallComponents() {
        titlePane = null;
        super.uninstallComponents();
    }

  private void stripContentBorder(Object c) {
        if ( c instanceof JComponent ) {
            JComponent contentComp = (JComponent)c;
            Border contentBorder = contentComp.getBorder();
            if (contentBorder == null || contentBorder instanceof UIResource) {
                contentComp.setBorder( handyEmptyBorder );
            }
        }
  }


  protected JComponent createNorthPane(JInternalFrame w) {
      return new MetalInternalFrameTitlePane(w);
  }


  private void setFrameType( String frameType )
  {
      if ( frameType.equals( OPTION_DIALOG ) )
      {
          LookAndFeel.installBorder(frame, "InternalFrame.optionDialogBorder");
          ((MetalInternalFrameTitlePane)titlePane).setPalette( false );
      }
      else if ( frameType.equals( PALETTE_FRAME ) )
      {
          LookAndFeel.installBorder(frame, "InternalFrame.paletteBorder");
          ((MetalInternalFrameTitlePane)titlePane).setPalette( true );
      }
      else
      {
          LookAndFeel.installBorder(frame, "InternalFrame.border");
          ((MetalInternalFrameTitlePane)titlePane).setPalette( false );
      }
  }

  // this should be deprecated - jcs
  public void setPalette(boolean isPalette) {
    if (isPalette) {
        LookAndFeel.installBorder(frame, "InternalFrame.paletteBorder");
    } else {
        LookAndFeel.installBorder(frame, "InternalFrame.border");
    }
    ((MetalInternalFrameTitlePane)titlePane).setPalette(isPalette);

  }

  private static class MetalPropertyChangeHandler implements
        PropertyChangeListener
  {
      public void propertyChange(PropertyChangeEvent e)
      {
          String name = e.getPropertyName();
          JInternalFrame jif = (JInternalFrame)e.getSource();

          if (!(jif.getUI() instanceof javax.swing.plaf.metal.MetalInternalFrameUI)) {
              return;
          }

          javax.swing.plaf.metal.MetalInternalFrameUI ui = (javax.swing.plaf.metal.MetalInternalFrameUI)jif.getUI();

          if ( name.equals( FRAME_TYPE ) )
          {
              if ( e.getNewValue() instanceof String )
              {
                  ui.setFrameType( (String) e.getNewValue() );
              }
          }
          else if ( name.equals(IS_PALETTE_KEY) )
          {
              if ( e.getNewValue() != null )
              {
                  ui.setPalette( ((Boolean)e.getNewValue()).booleanValue() );
              }
              else
              {
                  ui.setPalette( false );
              }
          } else if ( name.equals( JInternalFrame.CONTENT_PANE_PROPERTY ) ) {
              ui.stripContentBorder(e.getNewValue());
          }
      }
  } // end class MetalPropertyChangeHandler


    private class BorderListener1 extends BorderListener implements SwingConstants
    {

        Rectangle getIconBounds() {
            boolean leftToRight = javax.swing.plaf.metal.MetalUtils.isLeftToRight(frame);
            int xOffset = leftToRight ? 5 : titlePane.getWidth() - 5;
            Rectangle rect = null;

            Icon icon = frame.getFrameIcon();
            if ( icon != null ) {
                if ( !leftToRight ) {
                    xOffset -= icon.getIconWidth();
                }
                int iconY = ((titlePane.getHeight() / 2) - (icon.getIconHeight() /2));
                rect = new Rectangle(xOffset, iconY,
                    icon.getIconWidth(), icon.getIconHeight());
            }
            return rect;
        }

        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && e.getSource() == getNorthPane() &&
                frame.isClosable() && !frame.isIcon()) {
                Rectangle rect = getIconBounds();
                if ((rect != null) && rect.contains(e.getX(), e.getY())) {
                    frame.doDefaultCloseAction();
                }
                else {
                    super.mouseClicked(e);
                }
            }
            else {
                super.mouseClicked(e);
            }
        }
    };    /// End BorderListener Class


    /**
     * Returns the <code>MouseInputAdapter</code> that will be installed
     * on the TitlePane.
     *
     * @param w the <code>JInternalFrame</code>
     * @return the <code>MouseInputAdapter</code> that will be installed
     * on the TitlePane.
     * @since 1.6
     */
    protected MouseInputAdapter createBorderListener(JInternalFrame w) {
        return new BorderListener1();
    }
}
