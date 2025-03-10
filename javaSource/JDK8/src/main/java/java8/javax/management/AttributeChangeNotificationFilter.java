/*
 * Copyright (c) 1999, 2006, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.management;


import javax.management.AttributeChangeNotification;
import javax.management.Notification;
import javax.management.NotificationFilter;
import java.util.Vector;


/**
 * This class implements of the {@link NotificationFilter NotificationFilter}
 * interface for the {@link AttributeChangeNotification attribute change notification}.
 * The filtering is performed on the name of the observed attribute.
 * <P>
 * It manages a list of enabled attribute names.
 * A method allows users to enable/disable as many attribute names as required.
 *
 * @since 1.5
 */
public class AttributeChangeNotificationFilter implements NotificationFilter {

    /* Serial version */
    private static final long serialVersionUID = -6347317584796410029L;

    /**
     * @serial {@link Vector} that contains the enabled attribute names.
     *         The default value is an empty vector.
     */
    private Vector<String> enabledAttributes = new Vector<String>();


    /**
     * Invoked before sending the specified notification to the listener.
     * <BR>This filter compares the attribute name of the specified attribute change notification
     * with each enabled attribute name.
     * If the attribute name equals one of the enabled attribute names,
     * the notification must be sent to the listener and this method returns <CODE>true</CODE>.
     *
     * @param notification The attribute change notification to be sent.
     * @return <CODE>true</CODE> if the notification has to be sent to the listener, <CODE>false</CODE> otherwise.
     */
    public synchronized boolean isNotificationEnabled(Notification notification) {

        String type = notification.getType();

        if ((type == null) ||
            (type.equals(AttributeChangeNotification.ATTRIBUTE_CHANGE) == false) ||
            (!(notification instanceof AttributeChangeNotification))) {
            return false;
        }

        String attributeName =
          ((AttributeChangeNotification)notification).getAttributeName();
        return enabledAttributes.contains(attributeName);
    }

    /**
     * Enables all the attribute change notifications the attribute name of which equals
     * the specified name to be sent to the listener.
     * <BR>If the specified name is already in the list of enabled attribute names,
     * this method has no effect.
     *
     * @param name The attribute name.
     * @exception IllegalArgumentException The attribute name parameter is null.
     */
    public synchronized void enableAttribute(String name) throws IllegalArgumentException {

        if (name == null) {
            throw new IllegalArgumentException("The name cannot be null.");
        }
        if (!enabledAttributes.contains(name)) {
            enabledAttributes.addElement(name);
        }
    }

    /**
     * Disables all the attribute change notifications the attribute name of which equals
     * the specified attribute name to be sent to the listener.
     * <BR>If the specified name is not in the list of enabled attribute names,
     * this method has no effect.
     *
     * @param name The attribute name.
     */
    public synchronized void disableAttribute(String name) {
        enabledAttributes.removeElement(name);
    }

    /**
     * Disables all the attribute names.
     */
    public synchronized void disableAllAttributes() {
        enabledAttributes.removeAllElements();
    }

    /**
     * Gets all the enabled attribute names for this filter.
     *
     * @return The list containing all the enabled attribute names.
     */
    public synchronized Vector<String> getEnabledAttributes() {
        return enabledAttributes;
    }

}
