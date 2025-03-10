/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
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

/**
 * The <code>CheckboxGroup</code> class is used to group together
 * a set of <code>Checkbox</code> buttons.
 * <p>
 * Exactly one check box button in a <code>CheckboxGroup</code> can
 * be in the "on" state at any given time. Pushing any
 * button sets its state to "on" and forces any other button that
 * is in the "on" state into the "off" state.
 * <p>
 * The following code example produces a new check box group,
 * with three check boxes:
 *
 * <hr><blockquote><pre>
 * setLayout(new GridLayout(3, 1));
 * CheckboxGroup cbg = new CheckboxGroup();
 * add(new Checkbox("one", cbg, true));
 * add(new Checkbox("two", cbg, false));
 * add(new Checkbox("three", cbg, false));
 * </pre></blockquote><hr>
 * <p>
 * This image depicts the check box group created by this example:
 * <p>
 * <img src="doc-files/CheckboxGroup-1.gif"
 * alt="Shows three checkboxes, arranged vertically, labeled one, two, and three. Checkbox one is in the on state."
 * style="float:center; margin: 7px 10px;">
 * <p>
 * @author      Sami Shaio
 * @see         Checkbox
 * @since       JDK1.0
 */
public class CheckboxGroup implements java.io.Serializable {
    /**
     * The current choice.
     * @serial
     * @see #getCurrent()
     * @see #setCurrent(Checkbox)
     */
    Checkbox selectedCheckbox = null;

    /*
     * JDK 1.1 serialVersionUID
     */
    private static final long serialVersionUID = 3729780091441768983L;

    /**
     * Creates a new instance of <code>CheckboxGroup</code>.
     */
    public CheckboxGroup() {
    }

    /**
     * Gets the current choice from this check box group.
     * The current choice is the check box in this
     * group that is currently in the "on" state,
     * or <code>null</code> if all check boxes in the
     * group are off.
     * @return   the check box that is currently in the
     *                 "on" state, or <code>null</code>.
     * @see      Checkbox
     * @see      java.awt.CheckboxGroup#setSelectedCheckbox
     * @since    JDK1.1
     */
    public Checkbox getSelectedCheckbox() {
        return getCurrent();
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>getSelectedCheckbox()</code>.
     */
    @Deprecated
    public Checkbox getCurrent() {
        return selectedCheckbox;
    }

    /**
     * Sets the currently selected check box in this group
     * to be the specified check box.
     * This method sets the state of that check box to "on" and
     * sets all other check boxes in the group to be off.
     * <p>
     * If the check box argument is <tt>null</tt>, all check boxes
     * in this check box group are deselected. If the check box argument
     * belongs to a different check box group, this method does
     * nothing.
     * @param     box   the <code>Checkbox</code> to set as the
     *                      current selection.
     * @see      Checkbox
     * @see      java.awt.CheckboxGroup#getSelectedCheckbox
     * @since    JDK1.1
     */
    public void setSelectedCheckbox(Checkbox box) {
        setCurrent(box);
    }

    /**
     * @deprecated As of JDK version 1.1,
     * replaced by <code>setSelectedCheckbox(Checkbox)</code>.
     */
    @Deprecated
    public synchronized void setCurrent(Checkbox box) {
        if (box != null && box.group != this) {
            return;
        }
        Checkbox oldChoice = this.selectedCheckbox;
        this.selectedCheckbox = box;
        if (oldChoice != null && oldChoice != box && oldChoice.group == this) {
            oldChoice.setState(false);
        }
        if (box != null && oldChoice != box && !box.getState()) {
            box.setStateInternal(true);
        }
    }

    /**
     * Returns a string representation of this check box group,
     * including the value of its current selection.
     * @return    a string representation of this check box group.
     */
    public String toString() {
        return getClass().getName() + "[selectedCheckbox=" + selectedCheckbox + "]";
    }

}
