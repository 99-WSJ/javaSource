/*
 * Copyright (c) 2003, 2006, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.sql.rowset.spi;

import java.sql.SQLException;
import javax.sql.rowset.*;
import javax.sql.rowset.spi.SyncResolver;

/**
 * Indicates an error with the <code>SyncProvider</code> mechanism. This exception
 * is created by a <code>SyncProvider</code> abstract class extension if it
 * encounters violations in reading from or writing to the originating data source.
 * <P>
 * If it is implemented to do so, the <code>SyncProvider</code> object may also create a
 * <code>SyncResolver</code> object and either initialize the <code>SyncProviderException</code>
 * object with it at construction time or set it with the <code>SyncProvider</code> object at
 * a later time.
 * <P>
 * The method <code>acceptChanges</code> will throw this exception after the writer
 * has finished checking for conflicts and has found one or more conflicts. An
 * application may catch a <code>SyncProviderException</code> object and call its
 * <code>getSyncResolver</code> method to get its <code>SyncResolver</code> object.
 * See the code fragment in the interface comment for
 * <a href="SyncResolver.html"><code>SyncResolver</code></a> for an example.
 * This <code>SyncResolver</code> object will mirror the <code>RowSet</code>
 * object that generated the exception, except that it will contain only the values
 * from the data source that are in conflict.  All other values in the <code>SyncResolver</code>
 * object will be <code>null</code>.
 * <P>
 * The <code>SyncResolver</code> object may be used to examine and resolve
 * each conflict in a row and then go to the next row with a conflict to
 * repeat the procedure.
 * <P>
 * A <code>SyncProviderException</code> object may or may not contain a description of the
 * condition causing the exception.  The inherited method <code>getMessage</code> may be
 * called to retrieve the description if there is one.
 *
 * @author Jonathan Bruce
 * @see javax.sql.rowset.spi.SyncFactory
 * @see SyncResolver
 * @see javax.sql.rowset.spi.SyncFactoryException
 */
public class SyncProviderException extends SQLException {

    /**
     * The instance of <code>javax.sql.rowset.spi.SyncResolver</code> that
     * this <code>SyncProviderException</code> object will return when its
     * <code>getSyncResolver</code> method is called.
     */
     private SyncResolver syncResolver = null;

    /**
     * Creates a new <code>SyncProviderException</code> object without a detail message.
     */
    public SyncProviderException() {
        super();
    }

    /**
     * Constructs a <code>SyncProviderException</code> object with the specified
     * detail message.
     *
     * @param msg the detail message
     */
    public SyncProviderException(String msg)  {
        super(msg);
    }

    /**
     * Constructs a <code>SyncProviderException</code> object with the specified
     * <code>SyncResolver</code> instance.
     *
     * @param syncResolver the <code>SyncResolver</code> instance used to
     *     to process the synchronization conflicts
     * @throws IllegalArgumentException if the <code>SyncResolver</code> object
     *     is <code>null</code>.
     */
    public SyncProviderException(SyncResolver syncResolver)  {
        if (syncResolver == null) {
            throw new IllegalArgumentException("Cannot instantiate a SyncProviderException " +
                "with a null SyncResolver object");
        } else {
            this.syncResolver = syncResolver;
        }
    }

    /**
     * Retrieves the <code>SyncResolver</code> object that has been set for
     * this <code>SyncProviderException</code> object, or
     * if none has been set, an instance of the default <code>SyncResolver</code>
     * implementation included in the reference implementation.
     * <P>
     * If a <code>SyncProviderException</code> object is thrown, an application
     * may use this method to generate a <code>SyncResolver</code> object
     * with which to resolve the conflict or conflicts that caused the
     * exception to be thrown.
     *
     * @return the <code>SyncResolver</code> object set for this
     *     <code>SyncProviderException</code> object or, if none has
     *     been set, an instance of the default <code>SyncResolver</code>
     *     implementation. In addition, the default <code>SyncResolver</code>
     *     implementation is also returned if the <code>SyncResolver()</code> or
     *     <code>SyncResolver(String)</code> constructors are used to instantiate
     *     the <code>SyncResolver</code> instance.
     */
    public SyncResolver getSyncResolver() {
        if (syncResolver != null) {
            return syncResolver;
        } else {
            try {
              syncResolver = new com.sun.rowset.internal.SyncResolverImpl();
            } catch (SQLException sqle) {
            }
            return syncResolver;
        }
    }

    /**
     * Sets the <code>SyncResolver</code> object for this
     * <code>SyncProviderException</code> object to the one supplied.
     * If the argument supplied is <code>null</code>, a call to the method
     * <code>getSyncResolver</code> will return the default reference
     * implementation of the <code>SyncResolver</code> interface.
     *
     * @param syncResolver the <code>SyncResolver</code> object to be set;
     *     cannot be <code>null</code>
     * @throws IllegalArgumentException if the <code>SyncResolver</code> object
     *     is <code>null</code>.
     * @see #getSyncResolver
     */
    public void setSyncResolver(SyncResolver syncResolver) {
        if (syncResolver == null) {
            throw new IllegalArgumentException("Cannot set a null SyncResolver " +
                "object");
        } else {
            this.syncResolver = syncResolver;
        }
    }

    static final long serialVersionUID = -939908523620640692L;

}
