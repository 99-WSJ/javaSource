/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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
/*
 * @author    IBM Corp.
 *
 * Copyright IBM Corp. 1999-2000.  All rights reserved.
 */

package java8.javax.management.modelmbean;

import javax.management.Descriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.RuntimeOperationsException;
import javax.management.MBeanException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;

/**
 * This interface is implemented by the ModelMBeanInfo for every ModelMBean. An implementation of this interface
 * must be shipped with every JMX Agent.
 * <P>
 * Java resources wishing to be manageable instantiate the ModelMBean using the MBeanServer's
 * createMBean method.  The resource then sets the ModelMBeanInfo and Descriptors for the ModelMBean
 * instance. The attributes, operations, and notifications exposed via the ModelMBeanInfo for the
 * ModelMBean comprise the management interface and are accessible
 * from MBeans, connectors/adaptors like other MBeans. Through the Descriptors, values and methods in
 * the managed application can be defined and mapped to attributes and operations of the ModelMBean.
 * This mapping can be defined during development in a file or dynamically and
 * programmatically at runtime.
 * <P>
 * Every ModelMBean which is instantiated in the MBeanServer becomes manageable:
 * its attributes, operations, and notifications
 * become remotely accessible through the connectors/adaptors connected to that MBeanServer.
 * A Java object cannot be registered in the MBeanServer unless it is a JMX compliant MBean.
 * By instantiating a ModelMBean, resources are guaranteed that the MBean is valid.
 *
 * MBeanException and RuntimeOperationsException must be thrown on every public method.  This allows
 *  for wrapping exceptions from distributed communications (RMI, EJB, etc.)
 *
 * @since 1.5
 */

public interface ModelMBeanInfo
{


    /**
     * Returns a Descriptor array consisting of all
     * Descriptors for the ModelMBeanInfo of type inDescriptorType.
     *
     * @param inDescriptorType value of descriptorType field that must be set for the descriptor
     * to be returned.  Must be "mbean", "attribute", "operation", "constructor" or "notification".
     * If it is null or empty then all types will be returned.
     *
     * @return Descriptor array containing all descriptors for the ModelMBean if type inDescriptorType.
     *
     * @exception MBeanException Wraps a distributed communication Exception.
     * @exception RuntimeOperationsException Wraps an IllegalArgumentException when the descriptorType in parameter is
     * not one of: "mbean", "attribute", "operation", "constructor", "notification", empty or null.
     *
     * @see #setDescriptors
     */
    public Descriptor[] getDescriptors(String inDescriptorType)
            throws MBeanException, RuntimeOperationsException;

    /**
     * Adds or replaces descriptors in the ModelMBeanInfo.
     *
     * @param inDescriptors The descriptors to be set in the ModelMBeanInfo. Null
     * elements of the list will be ignored.  All descriptors must have name and descriptorType fields.
     *
     * @exception RuntimeOperationsException Wraps an IllegalArgumentException for a null or invalid descriptor.
     * @exception MBeanException Wraps a distributed communication Exception.
     *
     * @see #getDescriptors
     */
    public void setDescriptors(Descriptor[] inDescriptors)
            throws MBeanException, RuntimeOperationsException;

    /**
     * Returns a Descriptor requested by name and descriptorType.
     *
     * @param inDescriptorName The name of the descriptor.
     * @param inDescriptorType The type of the descriptor being
     * requested.  If this is null or empty then all types are
     * searched. Valid types are 'mbean', 'attribute', 'constructor'
     * 'operation', and 'notification'. This value will be equal to
     * the 'descriptorType' field in the descriptor that is returned.
     *
     * @return Descriptor containing the descriptor for the ModelMBean
     * with the same name and descriptorType.  If no descriptor is
     * found, null is returned.
     *
     * @exception MBeanException Wraps a distributed communication Exception.
     * @exception RuntimeOperationsException Wraps an IllegalArgumentException for a null descriptor name or null or invalid type.
     * The type must be "mbean","attribute", "constructor", "operation", or "notification".
     *
     * @see #setDescriptor
     */

    public Descriptor getDescriptor(String inDescriptorName, String inDescriptorType)
            throws MBeanException, RuntimeOperationsException;

    /**
     * Sets descriptors in the info array of type inDescriptorType
     * for the ModelMBean.  The setDescriptor method of the
     * corresponding ModelMBean*Info will be called to set the
     * specified descriptor.
     *
     * @param inDescriptor The descriptor to be set in the
     * ModelMBean. It must NOT be null.  All descriptors must have
     * name and descriptorType fields.
     * @param inDescriptorType The type of the descriptor being
     * set. If this is null then the descriptorType field in the
     * descriptor is used. If specified this value must be set in
     * the descriptorType field in the descriptor. Must be
     * "mbean","attribute", "constructor", "operation", or
     * "notification".
     *
     * @exception RuntimeOperationsException Wraps an
     * IllegalArgumentException for illegal or null arguments or
     * if the name field of the descriptor is not found in the
     * corresponding MBeanAttributeInfo or MBeanConstructorInfo or
     * MBeanNotificationInfo or MBeanOperationInfo.
     * @exception MBeanException Wraps a distributed communication
     * Exception.
     *
     * @see #getDescriptor
     */

    public void setDescriptor(Descriptor inDescriptor, String inDescriptorType)
            throws MBeanException, RuntimeOperationsException;


    /**
     * <p>Returns the ModelMBean's descriptor which contains MBean wide
     * policies.  This descriptor contains metadata about the MBean and default
     * policies for persistence and caching.</p>
     *
     * <P id="descriptor">
     * The fields in the descriptor are defined, but not limited to, the
     * following.  Note that when the Type in this table is Number, a String
     * that is the decimal representation of a Long can also be used.</P>
     *
     * <table border="1" cellpadding="5" summary="ModelMBean Fields">
     * <tr><th>Name</th><th>Type</th><th>Meaning</th></tr>
     * <tr><td>name</td><td>String</td>
     *     <td>MBean name.</td></tr>
     * <tr><td>descriptorType</td><td>String</td>
     *     <td>Must be "mbean".</td></tr>
     * <tr><td>displayName</td><td>String</td>
     *     <td>Name of MBean to be used in displays.</td></tr>
     * <tr><td>persistPolicy</td><td>String</td>
     *     <td>One of: OnUpdate|OnTimer|NoMoreOftenThan|OnUnregister|Always|Never.
     *         See the section "MBean Descriptor Fields" in the JMX specification
     *         document.</td></tr>
     * <tr><td>persistLocation</td><td>String</td>
     *     <td>The fully qualified directory name where the MBean should be
     *         persisted (if appropriate).</td></tr>
     * <tr><td>persistFile</td><td>String</td>
     *     <td>File name into which the MBean should be persisted.</td></tr>
     * <tr><td>persistPeriod</td><td>Number</td>
     *     <td>Frequency of persist cycle in seconds, for OnTime and
     *         NoMoreOftenThan PersistPolicy</td></tr>
     * <tr><td>currencyTimeLimit</td><td>Number</td>
     *     <td>How long cached value is valid: &lt;0 never, =0 always,
     *         &gt;0 seconds.</td></tr>
     * <tr><td>log</td><td>String</td>
     *     <td>t: log all notifications, f: log no notifications.</td></tr>
     * <tr><td>logfile</td><td>String</td>
     *     <td>Fully qualified filename to log events to.</td></tr>
     * <tr><td>visibility</td><td>Number</td>
     *     <td>1-4 where 1: always visible 4: rarely visible.</td></tr>
     * <tr><td>export</td><td>String</td>
     *     <td>Name to be used to export/expose this MBean so that it is
     *         findable by other JMX Agents.</td></tr>
     * <tr><td>presentationString</td><td>String</td>
     *     <td>XML formatted string to allow presentation of data to be
     *         associated with the MBean.</td></tr>
     * </table>
     *
     * <P>
     * The default descriptor is: name=className,descriptorType="mbean", displayName=className,
     *  persistPolicy="never",log="F",visibility="1"
     * If the descriptor does not contain all these fields, they will be added with these default values.
     *
     * <p><b>Note:</b> because of inconsistencies in previous versions of
     * this specification, it is recommended not to use negative or zero
     * values for <code>currencyTimeLimit</code>.  To indicate that a
     * cached value is never valid, omit the
     * <code>currencyTimeLimit</code> field.  To indicate that it is
     * always valid, use a very large number for this field.</p>
     *
     * @return the MBean descriptor.
     *
     * @exception MBeanException Wraps a distributed communication
     * Exception.
     *
     * @exception RuntimeOperationsException a {@link
     * RuntimeException} occurred while getting the descriptor.
     *
     * @see #setMBeanDescriptor
     */
    public Descriptor getMBeanDescriptor()
            throws MBeanException, RuntimeOperationsException;

    /**
     * Sets the ModelMBean's descriptor.  This descriptor contains default, MBean wide
     * metadata about the MBean and default policies for persistence and caching. This operation
     * does a complete replacement of the descriptor, no merging is done. If the descriptor to
     * set to is null then the default descriptor will be created.
     * The default descriptor is: name=className,descriptorType="mbean", displayName=className,
     *  persistPolicy="never",log="F",visibility="1"
     * If the descriptor does not contain all these fields, they will be added with these default values.
     *
     * See {@link #getMBeanDescriptor getMBeanDescriptor} method javadoc for description of valid field names.
     *
     * @param inDescriptor the descriptor to set.
     *
     * @exception MBeanException Wraps a distributed communication Exception.
     * @exception RuntimeOperationsException Wraps an IllegalArgumentException  for invalid descriptor.
     *
     *
     * @see #getMBeanDescriptor
     */

    public void setMBeanDescriptor(Descriptor inDescriptor)
            throws MBeanException, RuntimeOperationsException;


    /**
     * Returns a ModelMBeanAttributeInfo requested by name.
     *
     * @param inName The name of the ModelMBeanAttributeInfo to get.
     * If no ModelMBeanAttributeInfo exists for this name null is returned.
     *
     * @return the attribute info for the named attribute, or null
     * if there is none.
     *
     * @exception MBeanException Wraps a distributed communication
     * Exception.
     * @exception RuntimeOperationsException Wraps an
     * IllegalArgumentException for a null attribute name.
     *
     */

    public ModelMBeanAttributeInfo getAttribute(String inName)
            throws MBeanException, RuntimeOperationsException;


    /**
     * Returns a ModelMBeanOperationInfo requested by name.
     *
     * @param inName The name of the ModelMBeanOperationInfo to get.
     * If no ModelMBeanOperationInfo exists for this name null is returned.
     *
     * @return the operation info for the named operation, or null
     * if there is none.
     *
     * @exception MBeanException Wraps a distributed communication Exception.
     * @exception RuntimeOperationsException Wraps an IllegalArgumentException for a null operation name.
     *
     */

    public ModelMBeanOperationInfo getOperation(String inName)
            throws MBeanException, RuntimeOperationsException;


    /**
     * Returns a ModelMBeanNotificationInfo requested by name.
     *
     * @param inName The name of the ModelMBeanNotificationInfo to get.
     * If no ModelMBeanNotificationInfo exists for this name null is returned.
     *
     * @return the info for the named notification, or null if there
     * is none.
     *
     * @exception MBeanException Wraps a distributed communication Exception.
     * @exception RuntimeOperationsException Wraps an IllegalArgumentException for a null notification name.
     *
     */
    public ModelMBeanNotificationInfo getNotification(String inName)
            throws MBeanException, RuntimeOperationsException;

    /**
     * Creates and returns a copy of this object.
     */
    public Object clone();

    /**
     * Returns the list of attributes exposed for management.
     * Each attribute is described by an <CODE>MBeanAttributeInfo</CODE> object.
     *
     * @return  An array of <CODE>MBeanAttributeInfo</CODE> objects.
     */
    public MBeanAttributeInfo[] getAttributes();

    /**
     * Returns the name of the Java class of the MBean described by
     * this <CODE>MBeanInfo</CODE>.
     *
     * @return the Java class name.
     */
    public String getClassName();

    /**
     * Returns the list of the public constructors  of the MBean.
     * Each constructor is described by an <CODE>MBeanConstructorInfo</CODE> object.
     *
     * @return  An array of <CODE>MBeanConstructorInfo</CODE> objects.
     */
    public MBeanConstructorInfo[] getConstructors();

    /**
     * Returns a human readable description of the MBean.
     *
     * @return the description.
     */
    public String getDescription();

    /**
     * Returns the list of the notifications emitted by the MBean.
     * Each notification is described by an <CODE>MBeanNotificationInfo</CODE> object.
     * <P>
     * In addition to any notification specified by the application,
     * a ModelMBean may always send also two additional notifications:
     * <UL>
     * <LI> One with descriptor name "GENERIC" and displayName "jmx.modelmbean.generic"
     * <LI> Second is a standard attribute change notification
     *      with descriptor name "ATTRIBUTE_CHANGE" and displayName "jmx.attribute.change"
     * </UL>
     * Thus any implementation of ModelMBeanInfo should always add those two notifications
     * in addition to those specified by the application.
     *
     * @return  An array of <CODE>MBeanNotificationInfo</CODE> objects.
     */
    public MBeanNotificationInfo[] getNotifications();

    /**
     * Returns the list of operations  of the MBean.
     * Each operation is described by an <CODE>MBeanOperationInfo</CODE> object.
     *
     * @return  An array of <CODE>MBeanOperationInfo</CODE> objects.
     */
    public MBeanOperationInfo[] getOperations();

}
