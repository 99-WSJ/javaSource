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

import static com.sun.jmx.defaults.JmxProperties.MODELMBEAN_LOGGER;
import com.sun.jmx.mbeanserver.GetPropertyAction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.util.logging.Level;

import javax.management.Descriptor;
import javax.management.DescriptorKey;
import javax.management.DescriptorAccess;
import javax.management.MBeanAttributeInfo;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.DescriptorSupport;

/**
 * <p>The ModelMBeanAttributeInfo object describes an attribute of the ModelMBean.
 * It is a subclass of MBeanAttributeInfo with the addition of an associated Descriptor
 * and an implementation of the DescriptorAccess interface.</p>
 *
 * <P id="descriptor">
 * The fields in the descriptor are defined, but not limited to, the following.
 * Note that when the Type in this table is Number, a String that is the decimal
 * representation of a Long can also be used.</P>
 *
 * <table border="1" cellpadding="5" summary="ModelMBeanAttributeInfo Fields">
 * <tr><th>Name</th><th>Type</th><th>Meaning</th></tr>
 * <tr><td>name</td><td>String</td>
 *     <td>Attribute name.</td></tr>
 * <tr><td>descriptorType</td><td>String</td>
 *     <td>Must be "attribute".</td></tr>
 * <tr id="value-field"><td>value</td><td>Object</td>
 *     <td>Current (cached) value for attribute.</td></tr>
 * <tr><td>default</td><td>Object</td>
 *     <td>Default value for attribute.</td></tr>
 * <tr><td>displayName</td><td>String</td>
 *     <td>Name of attribute to be used in displays.</td></tr>
 * <tr><td>getMethod</td><td>String</td>
 *     <td>Name of operation descriptor for get method.</td></tr>
 * <tr><td>setMethod</td><td>String</td>
 *     <td>Name of operation descriptor for set method.</td></tr>
 * <tr><td>protocolMap</td><td>Descriptor</td>
 *     <td>See the section "Protocol Map Support" in the JMX specification
 *         document.  Mappings must be appropriate for the attribute and entries
 *         can be updated or augmented at runtime.</td></tr>
 * <tr><td>persistPolicy</td><td>String</td>
 *     <td>One of: OnUpdate|OnTimer|NoMoreOftenThan|OnUnregister|Always|Never.
 *         See the section "MBean Descriptor Fields" in the JMX specification
 *         document.</td></tr>
 * <tr><td>persistPeriod</td><td>Number</td>
 *     <td>Frequency of persist cycle in seconds. Used when persistPolicy is
 *         "OnTimer" or "NoMoreOftenThan".</td></tr>
 * <tr><td>currencyTimeLimit</td><td>Number</td>
 *     <td>How long <a href="#value=field">value</a> is valid: &lt;0 never,
 *         =0 always, &gt;0 seconds.</td></tr>
 * <tr><td>lastUpdatedTimeStamp</td><td>Number</td>
 *     <td>When <a href="#value-field">value</a> was set.</td></tr>
 * <tr><td>visibility</td><td>Number</td>
 *     <td>1-4 where 1: always visible, 4: rarely visible.</td></tr>
 * <tr><td>presentationString</td><td>String</td>
 *     <td>XML formatted string to allow presentation of data.</td></tr>
 * </table>
 *
 * <p>The default descriptor contains the name, descriptorType and displayName
 * fields.  The default value of the name and displayName fields is the name of
 * the attribute.</p>
 *
 * <p><b>Note:</b> because of inconsistencies in previous versions of
 * this specification, it is recommended not to use negative or zero
 * values for <code>currencyTimeLimit</code>.  To indicate that a
 * cached value is never valid, omit the
 * <code>currencyTimeLimit</code> field.  To indicate that it is
 * always valid, use a very large number for this field.</p>
 *
 * <p>The <b>serialVersionUID</b> of this class is <code>6181543027787327345L</code>.
 *
 * @since 1.5
 */

@SuppressWarnings("serial")  // serialVersionUID is not constant
public class ModelMBeanAttributeInfo
    extends MBeanAttributeInfo
    implements DescriptorAccess {

    // Serialization compatibility stuff:
    // Two serial forms are supported in this class. The selected form depends
    // on system property "jmx.serial.form":
    //  - "1.0" for JMX 1.0
    //  - any other value for JMX 1.1 and higher
    //
    // Serial version for old serial form
    private static final long oldSerialVersionUID = 7098036920755973145L;
    //
    // Serial version for new serial form
    private static final long newSerialVersionUID = 6181543027787327345L;
    //
    // Serializable fields in old serial form
    private static final ObjectStreamField[] oldSerialPersistentFields =
    {
      new ObjectStreamField("attrDescriptor", Descriptor.class),
      new ObjectStreamField("currClass", String.class)
    };
    //
    // Serializable fields in new serial form
    private static final ObjectStreamField[] newSerialPersistentFields =
    {
      new ObjectStreamField("attrDescriptor", Descriptor.class)
    };
    //
    // Actual serial version and serial form
    private static final long serialVersionUID;
    /**
     * @serialField attrDescriptor Descriptor The {@link Descriptor}
     * containing the metadata corresponding to this attribute
     */
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat = false;
    static {
        try {
            GetPropertyAction act = new GetPropertyAction("jmx.serial.form");
            String form = AccessController.doPrivileged(act);
            compat = (form != null && form.equals("1.0"));
        } catch (Exception e) {
            // OK: No compat with 1.0
        }
        if (compat) {
            serialPersistentFields = oldSerialPersistentFields;
            serialVersionUID = oldSerialVersionUID;
        } else {
            serialPersistentFields = newSerialPersistentFields;
            serialVersionUID = newSerialVersionUID;
        }
    }
    //
    // END Serialization compatibility stuff

        /**
         * @serial The {@link Descriptor} containing the metadata corresponding to
         * this attribute
         */
        private Descriptor attrDescriptor = validDescriptor(null);

        private final static String currClass = "ModelMBeanAttributeInfo";

        /**
         * Constructs a ModelMBeanAttributeInfo object with a default
         * descriptor. The {@link Descriptor} of the constructed
         * object will include fields contributed by any annotations
         * on the {@code Method} objects that contain the {@link
         * DescriptorKey} meta-annotation.
         *
         * @param name The name of the attribute.
         * @param description A human readable description of the attribute. Optional.
         * @param getter The method used for reading the attribute value.
         *          May be null if the property is write-only.
         * @param setter The method used for writing the attribute value.
         *          May be null if the attribute is read-only.
         * @exception javax.management.IntrospectionException There is a consistency
         * problem in the definition of this attribute.
         *
         */

        public ModelMBeanAttributeInfo(String name,
                                       String description,
                                       Method getter,
                                       Method setter)
        throws javax.management.IntrospectionException {
                super(name, description, getter, setter);

                if (MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                    MODELMBEAN_LOGGER.logp(Level.FINER,
                            javax.management.modelmbean.ModelMBeanAttributeInfo.class.getName(),
                            "ModelMBeanAttributeInfo(" +
                            "String,String,Method,Method)",
                            "Entry", name);
                }

                attrDescriptor = validDescriptor(null);
                // put getter and setter methods in operations list
                // create default descriptor

        }

        /**
         * Constructs a ModelMBeanAttributeInfo object.  The {@link
         * Descriptor} of the constructed object will include fields
         * contributed by any annotations on the {@code Method}
         * objects that contain the {@link DescriptorKey}
         * meta-annotation.
         *
         * @param name The name of the attribute.
         * @param description A human readable description of the attribute. Optional.
         * @param getter The method used for reading the attribute value.
         *          May be null if the property is write-only.
         * @param setter The method used for writing the attribute value.
         *          May be null if the attribute is read-only.
         * @param descriptor An instance of Descriptor containing the
         * appropriate metadata for this instance of the Attribute. If
         * it is null, then a default descriptor will be created.  If
         * the descriptor does not contain the field "displayName" this field is added
         * in the descriptor with its default value.
         * @exception javax.management.IntrospectionException There is a consistency
         * problem in the definition of this attribute.
         * @exception RuntimeOperationsException Wraps an
         * IllegalArgumentException. The descriptor is invalid, or descriptor
         * field "name" is not equal to name parameter, or descriptor field
         * "descriptorType" is not equal to "attribute".
         *
         */

        public ModelMBeanAttributeInfo(String name,
                                       String description,
                                       Method getter,
                                       Method setter,
                                       Descriptor descriptor)
        throws javax.management.IntrospectionException {

                super(name, description, getter, setter);
                // put getter and setter methods in operations list
                if (MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                    MODELMBEAN_LOGGER.logp(Level.FINER,
                            javax.management.modelmbean.ModelMBeanAttributeInfo.class.getName(),
                            "ModelMBeanAttributeInfo(" +
                            "String,String,Method,Method,Descriptor)",
                            "Entry", name);
                }
                attrDescriptor = validDescriptor(descriptor);
        }

        /**
         * Constructs a ModelMBeanAttributeInfo object with a default descriptor.
         *
         * @param name The name of the attribute
         * @param type The type or class name of the attribute
         * @param description A human readable description of the attribute.
         * @param isReadable True if the attribute has a getter method, false otherwise.
         * @param isWritable True if the attribute has a setter method, false otherwise.
         * @param isIs True if the attribute has an "is" getter, false otherwise.
         *
         */
        public ModelMBeanAttributeInfo(String name,
                                       String type,
                                       String description,
                                       boolean isReadable,
                                       boolean isWritable,
                                       boolean isIs)
    {

                super(name, type, description, isReadable, isWritable, isIs);
                // create default descriptor
                if (MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                    MODELMBEAN_LOGGER.logp(Level.FINER,
                            javax.management.modelmbean.ModelMBeanAttributeInfo.class.getName(),
                            "ModelMBeanAttributeInfo(" +
                            "String,String,String,boolean,boolean,boolean)",
                            "Entry", name);
                }
                attrDescriptor = validDescriptor(null);
        }

        /**
         * Constructs a ModelMBeanAttributeInfo object.
         *
         * @param name The name of the attribute
         * @param type The type or class name of the attribute
         * @param description A human readable description of the attribute.
         * @param isReadable True if the attribute has a getter method, false otherwise.
         * @param isWritable True if the attribute has a setter method, false otherwise.
         * @param isIs True if the attribute has an "is" getter, false otherwise.
         * @param descriptor An instance of Descriptor containing the
         * appropriate metadata for this instance of the Attribute. If
         * it is null then a default descriptor will be created.  If
         * the descriptor does not contain the field "displayName" this field
         * is added in the descriptor with its default value.
         * @exception RuntimeOperationsException Wraps an
         * IllegalArgumentException. The descriptor is invalid, or descriptor
         * field "name" is not equal to name parameter, or descriptor field
         * "descriptorType" is not equal to "attribute".
         *
         */
        public ModelMBeanAttributeInfo(String name,
                                       String type,
                                       String description,
                                       boolean isReadable,
                                       boolean isWritable,
                                       boolean isIs,
                                       Descriptor descriptor)
        {
                super(name, type, description, isReadable, isWritable, isIs);
                if (MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                    MODELMBEAN_LOGGER.logp(Level.FINER,
                            javax.management.modelmbean.ModelMBeanAttributeInfo.class.getName(),
                            "ModelMBeanAttributeInfo(String,String,String," +
                            "boolean,boolean,boolean,Descriptor)",
                            "Entry", name);
                }
                attrDescriptor = validDescriptor(descriptor);
        }

        /**
         * Constructs a new ModelMBeanAttributeInfo object from this
         * ModelMBeanAttributeInfo Object.  A default descriptor will
         * be created.
         *
         * @param inInfo the ModelMBeanAttributeInfo to be duplicated
         */

        public ModelMBeanAttributeInfo(javax.management.modelmbean.ModelMBeanAttributeInfo inInfo)
        {
                super(inInfo.getName(),
                          inInfo.getType(),
                          inInfo.getDescription(),
                          inInfo.isReadable(),
                          inInfo.isWritable(),
                          inInfo.isIs());
                if (MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                    MODELMBEAN_LOGGER.logp(Level.FINER,
                            javax.management.modelmbean.ModelMBeanAttributeInfo.class.getName(),
                            "ModelMBeanAttributeInfo(ModelMBeanAttributeInfo)",
                            "Entry");
                }
                Descriptor newDesc = inInfo.getDescriptor();
                attrDescriptor = validDescriptor(newDesc);
        }

        /**
         * Gets a copy of the associated Descriptor for the
         * ModelMBeanAttributeInfo.
         *
         * @return Descriptor associated with the
         * ModelMBeanAttributeInfo object.
         *
         * @see #setDescriptor
         */

        public Descriptor getDescriptor() {
            if (MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                MODELMBEAN_LOGGER.logp(Level.FINER,
                        javax.management.modelmbean.ModelMBeanAttributeInfo.class.getName(),
                        "getDescriptor()", "Entry");
            }
                if (attrDescriptor == null) {
                    attrDescriptor = validDescriptor(null);
                }
                return((Descriptor)attrDescriptor.clone());
        }


        /**
        * Sets associated Descriptor (full replace) for the
        * ModelMBeanAttributeDescriptor.  If the new Descriptor is
        * null, then the associated Descriptor reverts to a default
        * descriptor.  The Descriptor is validated before it is
        * assigned.  If the new Descriptor is invalid, then a
        * RuntimeOperationsException wrapping an
        * IllegalArgumentException is thrown.
        * @param inDescriptor replaces the Descriptor associated with the
        * ModelMBeanAttributeInfo
        *
        * @exception RuntimeOperationsException Wraps an
        * IllegalArgumentException for an invalid Descriptor
        *
        * @see #getDescriptor
        */
        public void setDescriptor(Descriptor inDescriptor) {
            attrDescriptor =  validDescriptor(inDescriptor);
        }

        /**
        * Creates and returns a new ModelMBeanAttributeInfo which is a duplicate of this ModelMBeanAttributeInfo.
        *
        * @exception RuntimeOperationsException for illegal value for
        * field Names or field Values.  If the descriptor construction
        * fails for any reason, this exception will be thrown.
        */

        @Override
        public Object clone()
        {
            if (MODELMBEAN_LOGGER.isLoggable(Level.FINER)) {
                MODELMBEAN_LOGGER.logp(Level.FINER,
                        javax.management.modelmbean.ModelMBeanAttributeInfo.class.getName(),
                        "clone()", "Entry");
            }
                return(new javax.management.modelmbean.ModelMBeanAttributeInfo(this));
        }

        /**
        * Returns a human-readable version of the
        * ModelMBeanAttributeInfo instance.
        */
        @Override
        public String toString()
        {
            return
                "ModelMBeanAttributeInfo: " + this.getName() +
                " ; Description: " + this.getDescription() +
                " ; Types: " + this.getType() +
                " ; isReadable: " + this.isReadable() +
                " ; isWritable: " + this.isWritable() +
                " ; Descriptor: " + this.getDescriptor();
        }


        /**
         * Clones the passed in Descriptor, sets default values, and checks for validity.
         * If the Descriptor is invalid (for instance by having the wrong "name"),
         * this indicates programming error and a RuntimeOperationsException will be thrown.
         *
         * The following fields will be defaulted if they are not already set:
         * displayName=this.getName(),name=this.getName(),descriptorType = "attribute"
         *
         * @param in Descriptor to be checked, or null which is equivalent to
         * an empty Descriptor.
         * @exception RuntimeOperationsException if Descriptor is invalid
         */
        private Descriptor validDescriptor(final Descriptor in) throws RuntimeOperationsException {

            Descriptor clone;
            boolean defaulted = (in == null);
            if (defaulted) {
                clone = new DescriptorSupport();
                MODELMBEAN_LOGGER.finer("Null Descriptor, creating new.");
            } else {
                clone = (Descriptor) in.clone();
            }

            //Setting defaults.
            if (defaulted && clone.getFieldValue("name")==null) {
                clone.setField("name", this.getName());
                MODELMBEAN_LOGGER.finer("Defaulting Descriptor name to " + this.getName());
            }
            if (defaulted && clone.getFieldValue("descriptorType")==null) {
                clone.setField("descriptorType", "attribute");
                MODELMBEAN_LOGGER.finer("Defaulting descriptorType to \"attribute\"");
            }
            if (clone.getFieldValue("displayName") == null) {
                clone.setField("displayName",this.getName());
                MODELMBEAN_LOGGER.finer("Defaulting Descriptor displayName to " + this.getName());
            }

            //Checking validity
            if (!clone.isValid()) {
                 throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"),
                    "The isValid() method of the Descriptor object itself returned false,"+
                    "one or more required fields are invalid. Descriptor:" + clone.toString());
            }
            if (!getName().equalsIgnoreCase((String)clone.getFieldValue("name"))) {
                    throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"),
                    "The Descriptor \"name\" field does not match the object described. " +
                     " Expected: "+ this.getName() + " , was: " + clone.getFieldValue("name"));
            }

            if (!"attribute".equalsIgnoreCase((String)clone.getFieldValue("descriptorType"))) {
                     throw new RuntimeOperationsException(new IllegalArgumentException("Invalid Descriptor argument"),
                    "The Descriptor \"descriptorType\" field does not match the object described. " +
                     " Expected: \"attribute\" ," + " was: " + clone.getFieldValue("descriptorType"));
            }

            return clone;
        }


    /**
     * Deserializes a {@link javax.management.modelmbean.ModelMBeanAttributeInfo} from an {@link ObjectInputStream}.
     */
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
      // New serial form ignores extra field "currClass"
      in.defaultReadObject();
    }


    /**
     * Serializes a {@link javax.management.modelmbean.ModelMBeanAttributeInfo} to an {@link ObjectOutputStream}.
     */
    private void writeObject(ObjectOutputStream out)
            throws IOException {
      if (compat)
      {
        // Serializes this instance in the old serial form
        //
        ObjectOutputStream.PutField fields = out.putFields();
        fields.put("attrDescriptor", attrDescriptor);
        fields.put("currClass", currClass);
        out.writeFields();
      }
      else
      {
        // Serializes this instance in the new serial form
        //
        out.defaultWriteObject();
      }
    }

}
