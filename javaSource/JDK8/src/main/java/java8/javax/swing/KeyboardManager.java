/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
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
package java8.javax.swing;


import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.InputMap;
import javax.swing.JInternalFrame;
import javax.swing.event.*;
import sun.awt.EmbeddedFrame;

/**
  * The KeyboardManager class is used to help dispatch keyboard actions for the
  * WHEN_IN_FOCUSED_WINDOW style actions.  Actions with other conditions are handled
  * directly in JComponent.
  *
  * Here's a description of the symantics of how keyboard dispatching should work
  * atleast as I understand it.
  *
  * KeyEvents are dispatched to the focused component.  The focus manager gets first
  * crack at processing this event.  If the focus manager doesn't want it, then
  * the JComponent calls super.processKeyEvent() this allows listeners a chance
  * to process the event.
  *
  * If none of the listeners "consumes" the event then the keybindings get a shot.
  * This is where things start to get interesting.  First, KeyStokes defined with the
  * WHEN_FOCUSED condition get a chance.  If none of these want the event, then the component
  * walks though it's parents looked for actions of type WHEN_ANCESTOR_OF_FOCUSED_COMPONENT.
  *
  * If no one has taken it yet, then it winds up here.  We then look for components registered
  * for WHEN_IN_FOCUSED_WINDOW events and fire to them.  Note that if none of those are found
  * then we pass the event to the menubars and let them have a crack at it.  They're handled differently.
  *
  * Lastly, we check if we're looking at an internal frame.  If we are and no one wanted the event
  * then we move up to the InternalFrame's creator and see if anyone wants the event (and so on and so on).
  *
  *
  * @see InputMap
  */
class KeyboardManager {

    static javax.swing.KeyboardManager currentManager = new javax.swing.KeyboardManager();

    /**
      * maps top-level containers to a sub-hashtable full of keystrokes
      */
    Hashtable<Container, Hashtable> containerMap = new Hashtable<Container, Hashtable>();

    /**
      * Maps component/keystroke pairs to a topLevel container
      * This is mainly used for fast unregister operations
      */
    Hashtable<ComponentKeyStrokePair, Container> componentKeyStrokeMap = new Hashtable<ComponentKeyStrokePair, Container>();

    public static javax.swing.KeyboardManager getCurrentManager() {
        return currentManager;
    }

    public static void setCurrentManager(javax.swing.KeyboardManager km) {
        currentManager = km;
    }

    /**
      * register keystrokes here which are for the WHEN_IN_FOCUSED_WINDOW
      * case.
      * Other types of keystrokes will be handled by walking the hierarchy
      * That simplifies some potentially hairy stuff.
      */
     public void registerKeyStroke(KeyStroke k, JComponent c) {
         Container topContainer = getTopAncestor(c);
         if (topContainer == null) {
             return;
         }
         Hashtable keyMap = containerMap.get(topContainer);

         if (keyMap ==  null) {  // lazy evaluate one
             keyMap = registerNewTopContainer(topContainer);
         }

         Object tmp = keyMap.get(k);
         if (tmp == null) {
             keyMap.put(k,c);
         } else if (tmp instanceof Vector) {  // if there's a Vector there then add to it.
             Vector v = (Vector)tmp;
             if (!v.contains(c)) {  // only add if this keystroke isn't registered for this component
                 v.addElement(c);
             }
         } else if (tmp instanceof JComponent) {
           // if a JComponent is there then remove it and replace it with a vector
           // Then add the old compoennt and the new compoent to the vector
           // then insert the vector in the table
           if (tmp != c) {  // this means this is already registered for this component, no need to dup
               Vector<JComponent> v = new Vector<JComponent>();
               v.addElement((JComponent) tmp);
               v.addElement(c);
               keyMap.put(k, v);
           }
         } else {
             System.out.println("Unexpected condition in registerKeyStroke");
             Thread.dumpStack();
         }

         componentKeyStrokeMap.put(new ComponentKeyStrokePair(c,k), topContainer);

         // Check for EmbeddedFrame case, they know how to process accelerators even
         // when focus is not in Java
         if (topContainer instanceof EmbeddedFrame) {
             ((EmbeddedFrame)topContainer).registerAccelerator(k);
         }
     }

     /**
       * Find the top focusable Window, Applet, or InternalFrame
       */
     private static Container getTopAncestor(JComponent c) {
        for(Container p = c.getParent(); p != null; p = p.getParent()) {
            if (p instanceof Window && ((Window)p).isFocusableWindow() ||
                p instanceof Applet || p instanceof JInternalFrame) {

                return p;
            }
        }
        return null;
     }

     public void unregisterKeyStroke(KeyStroke ks, JComponent c) {

       // component may have already been removed from the hierarchy, we
       // need to look up the container using the componentKeyStrokeMap.

         ComponentKeyStrokePair ckp = new ComponentKeyStrokePair(c,ks);

         Container topContainer = componentKeyStrokeMap.get(ckp);

         if (topContainer == null) {  // never heard of this pairing, so bail
             return;
         }

         Hashtable keyMap = containerMap.get(topContainer);
         if  (keyMap == null) { // this should never happen, but I'm being safe
             Thread.dumpStack();
             return;
         }

         Object tmp = keyMap.get(ks);
         if (tmp == null) {  // this should never happen, but I'm being safe
             Thread.dumpStack();
             return;
         }

         if (tmp instanceof JComponent && tmp == c) {
             keyMap.remove(ks);  // remove the KeyStroke from the Map
             //System.out.println("removed a stroke" + ks);
         } else if (tmp instanceof Vector ) {  // this means there is more than one component reg for this key
             Vector v = (Vector)tmp;
             v.removeElement(c);
             if ( v.isEmpty() ) {
                 keyMap.remove(ks);  // remove the KeyStroke from the Map
                 //System.out.println("removed a ks vector");
             }
         }

         if ( keyMap.isEmpty() ) {  // if no more bindings in this table
             containerMap.remove(topContainer);  // remove table to enable GC
             //System.out.println("removed a container");
         }

         componentKeyStrokeMap.remove(ckp);

         // Check for EmbeddedFrame case, they know how to process accelerators even
         // when focus is not in Java
         if (topContainer instanceof EmbeddedFrame) {
             ((EmbeddedFrame)topContainer).unregisterAccelerator(ks);
         }
     }

    /**
      * This method is called when the focused component (and none of
      * its ancestors) want the key event.  This will look up the keystroke
      * to see if any chidren (or subchildren) of the specified container
      * want a crack at the event.
      * If one of them wants it, then it will "DO-THE-RIGHT-THING"
      */
    public boolean fireKeyboardAction(KeyEvent e, boolean pressed, Container topAncestor) {

         if (e.isConsumed()) {
              System.out.println("Acquired pre-used event!");
              Thread.dumpStack();
         }

         // There may be two keystrokes associated with a low-level key event;
         // in this case a keystroke made of an extended key code has a priority.
         KeyStroke ks;
         KeyStroke ksE = null;


         if(e.getID() == KeyEvent.KEY_TYPED) {
               ks=KeyStroke.getKeyStroke(e.getKeyChar());
         } else {
               if(e.getKeyCode() != e.getExtendedKeyCode()) {
                   ksE=KeyStroke.getKeyStroke(e.getExtendedKeyCode(), e.getModifiers(), !pressed);
               }
               ks=KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers(), !pressed);
         }

         Hashtable keyMap = containerMap.get(topAncestor);
         if (keyMap != null) { // this container isn't registered, so bail

             Object tmp = null;
             // extended code has priority
             if( ksE != null ) {
                 tmp = keyMap.get(ksE);
                 if( tmp != null ) {
                     ks = ksE;
                 }
             }
             if( tmp == null ) {
                 tmp = keyMap.get(ks);
             }

             if (tmp == null) {
               // don't do anything
             } else if ( tmp instanceof JComponent) {
                 JComponent c = (JComponent)tmp;
                 if ( c.isShowing() && c.isEnabled() ) { // only give it out if enabled and visible
                     fireBinding(c, ks, e, pressed);
                 }
             } else if ( tmp instanceof Vector) { //more than one comp registered for this
                 Vector v = (Vector)tmp;
                 // There is no well defined order for WHEN_IN_FOCUSED_WINDOW
                 // bindings, but we give precedence to those bindings just
                 // added. This is done so that JMenus WHEN_IN_FOCUSED_WINDOW
                 // bindings are accessed before those of the JRootPane (they
                 // both have a WHEN_IN_FOCUSED_WINDOW binding for enter).
                 for (int counter = v.size() - 1; counter >= 0; counter--) {
                     JComponent c = (JComponent)v.elementAt(counter);
                     //System.out.println("Trying collision: " + c + " vector = "+ v.size());
                     if ( c.isShowing() && c.isEnabled() ) { // don't want to give these out
                         fireBinding(c, ks, e, pressed);
                         if (e.isConsumed())
                             return true;
                     }
                 }
             } else  {
                 System.out.println( "Unexpected condition in fireKeyboardAction " + tmp);
                 // This means that tmp wasn't null, a JComponent, or a Vector.  What is it?
                 Thread.dumpStack();
             }
         }

         if (e.isConsumed()) {
             return true;
         }
         // if no one else handled it, then give the menus a crack
         // The're handled differently.  The key is to let any JMenuBars
         // process the event
         if ( keyMap != null) {
             Vector v = (Vector)keyMap.get(JMenuBar.class);
             if (v != null) {
                 Enumeration iter = v.elements();
                 while (iter.hasMoreElements()) {
                     JMenuBar mb = (JMenuBar)iter.nextElement();
                     if ( mb.isShowing() && mb.isEnabled() ) { // don't want to give these out
                         boolean extended = (ksE != null) && !ksE.equals(ks);
                         if (extended) {
                             fireBinding(mb, ksE, e, pressed);
                         }
                         if (!extended || !e.isConsumed()) {
                             fireBinding(mb, ks, e, pressed);
                         }
                         if (e.isConsumed()) {
                             return true;
                         }
                     }
                 }
             }
         }

         return e.isConsumed();
    }

    void fireBinding(JComponent c, KeyStroke ks, KeyEvent e, boolean pressed) {
        if (c.processKeyBinding(ks, e, JComponent.WHEN_IN_FOCUSED_WINDOW,
                                pressed)) {
            e.consume();
        }
    }

    public void registerMenuBar(JMenuBar mb) {
        Container top = getTopAncestor(mb);
        if (top == null) {
            return;
        }
        Hashtable keyMap = containerMap.get(top);

        if (keyMap ==  null) {  // lazy evaluate one
             keyMap = registerNewTopContainer(top);
        }
        // use the menubar class as the key
        Vector menuBars = (Vector)keyMap.get(JMenuBar.class);

        if (menuBars == null) {  // if we don't have a list of menubars,
                                 // then make one.
            menuBars = new Vector();
            keyMap.put(JMenuBar.class, menuBars);
        }

        if (!menuBars.contains(mb)) {
            menuBars.addElement(mb);
        }
    }


    public void unregisterMenuBar(JMenuBar mb) {
        Container topContainer = getTopAncestor(mb);
        if (topContainer == null) {
            return;
        }
        Hashtable keyMap = containerMap.get(topContainer);
        if (keyMap!=null) {
            Vector v = (Vector)keyMap.get(JMenuBar.class);
            if (v != null) {
                v.removeElement(mb);
                if (v.isEmpty()) {
                    keyMap.remove(JMenuBar.class);
                    if (keyMap.isEmpty()) {
                        // remove table to enable GC
                        containerMap.remove(topContainer);
                    }
                }
            }
        }
    }
    protected Hashtable registerNewTopContainer(Container topContainer) {
             Hashtable keyMap = new Hashtable();
             containerMap.put(topContainer, keyMap);
             return keyMap;
    }

    /**
      * This class is used to create keys for a hashtable
      * which looks up topContainers based on component, keystroke pairs
      * This is used to make unregistering KeyStrokes fast
      */
    class ComponentKeyStrokePair {
        Object component;
        Object keyStroke;

        public ComponentKeyStrokePair(Object comp, Object key) {
            component = comp;
            keyStroke = key;
        }

        public boolean equals(Object o) {
            if ( !(o instanceof ComponentKeyStrokePair)) {
                return false;
            }
            ComponentKeyStrokePair ckp = (ComponentKeyStrokePair)o;
            return ((component.equals(ckp.component)) && (keyStroke.equals(ckp.keyStroke)));
        }

        public int hashCode() {
            return component.hashCode() * keyStroke.hashCode();
        }

    }

} // end KeyboardManager
