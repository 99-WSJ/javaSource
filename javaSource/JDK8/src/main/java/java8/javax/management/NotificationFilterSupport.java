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


import javax.management.Notification;
import javax.management.NotificationFilter;
import java.util.List;
import java.util.Vector;

/**
 * Provides an implementation of the {@link NotificationFilter} interface.
 * The filtering is performed on the notification type attribute.
 * <P>
 * Manages a list of enabled notification types.
 * A method allows users to enable/disable as many notification types as required.
 * <P>
 * Then, before sending a notification to a listener registered with a filter,
 * the notification broadcaster compares this notification type with all notification types
 * enabled by the filter. The notification will be sent to the listener
 * only if its filter enables this notification type.
 * <P>
 * Example:
 * <BLOCKQUOTE>
 * <PRE>
 * NotificationFilterSupport myFilter = new NotificationFilterSupport();
 * myFilter.enableType("my_example.my_type");
 * myBroadcaster.addListener(myListener, myFilter, null);
 * </PRE>
 * </BLOCKQUOTE>
 * The listener <CODE>myListener</CODE> will only receive notifications the type of which equals/starts with "my_example.my_type".
 *
 * @see javax.management.NotificationBroadcaster#addNotificationListener
 *
 * @since 1.5
 */
public class NotificationFilterSupport implements NotificationFilter {

    /* Serial version */
    private static final long serialVersionUID = 6579080007561786969L;

    /**
     * @serial {@link Vector} that contains the enabled notification types.
     *         The default value is an empty vector.
     */
    private List<String> enabledTypes = new Vector<String>();


    /**
     * Invoked before sending the specified notification to the listener.
     * <BR>This filter compares the type of the specified notification with each enabled type.
     * If the notification type matches one of the enabled types,
     * the notification should be sent to the listener and this method returns <CODE>true</CODE>.
     *
     * @param notification The notification to be sent.
     * @return <CODE>true</CODE> if the notification should be sent to the listener, <CODE>false</CODE> otherwise.
     */
    public synchronized boolean isNotificationEnabled(Notification notification) {

        String type = notification.getType();

        if (type == null) {
            return false;
        }
        try {
            for (String prefix : enabledTypes) {
                if (type.startsWith(prefix)) {
                    return true;
                }
            }
        } catch (NullPointerException e) {
            // Should never occurs...
            return false;
        }
        return false;
    }

    /**
     * Enables all the notifications the type of which starts with the specified prefix
     * to be sent to the listener.
     * <BR>If the specified prefix is already in the list of enabled notification types,
     * this method has no effect.
     * <P>
     * Example:
     * <BLOCKQUOTE>
     * <PRE>
     * // Enables all notifications the type of which starts with "my_example" to be sent.
     * myFilter.enableType("my_example");
     * // Enables all notifications the type of which is "my_example.my_type" to be sent.
     * myFilter.enableType("my_example.my_type");
     * </PRE>
     * </BLOCKQUOTE>
     *
     * Note that:
     * <BLOCKQUOTE><CODE>
     * myFilter.enableType("my_example.*");
     * </CODE></BLOCKQUOTE>
     * will no match any notification type.
     *
     * @param prefix The prefix.
     * @exception IllegalArgumentException The prefix parameter is null.
     */
    public synchronized void enableType(String prefix)
            throws IllegalArgumentException {

        if (prefix == null) {
            throw new IllegalArgumentException("The prefix cannot be null.");
        }
        if (!enabledTypes.contains(prefix)) {
            enabledTypes.add(prefix);
        }
    }

    /**
     * Removes the given prefix from the prefix list.
     * <BR>If the specified prefix is not in the list of enabled notification types,
     * this method has no effect.
     *
     * @param prefix The prefix.
     */
    public synchronized void disableType(String prefix) {
        enabledTypes.remove(prefix);
    }

    /**
     * Disables all notification types.
     */
    public synchronized void disableAllTypes() {
        enabledTypes.clear();
    }


    /**
     * Gets all the enabled notification types for this filter.
     *
     * @return The list containing all the enabled notification types.
     */
    public synchronized Vector<String> getEnabledTypes() {
        return (Vector<String>)enabledTypes;
    }

}
