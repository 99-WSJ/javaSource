/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.xml.internal.stream.StaxEntityResolverWrapper;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLResolver;
import java.util.HashMap;

/**
 *  This class manages different properties related to Stax specification and its implementation.
 * This class constructor also takes itself (PropertyManager object) as parameter and initializes the
 * object with the property taken from the object passed.
 *
 * @author  Neeraj Bajaj, neeraj.bajaj@sun.com
 * @author K.Venugopal@sun.com
 * @author Sunitha Reddy, sunitha.reddy@sun.com
 */

public class PropertyManager {


    public static final String STAX_NOTATIONS = "javax.xml.stream.notations";
    public static final String STAX_ENTITIES = "javax.xml.stream.entities";

    private static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";

    /** Property identifier: Security manager. */
    private static final String SECURITY_MANAGER = Constants.SECURITY_MANAGER;

    /** Property identifier: Security property manager. */
    private static final String XML_SECURITY_PROPERTY_MANAGER =
            Constants.XML_SECURITY_PROPERTY_MANAGER;

    HashMap supportedProps = new HashMap();

    private XMLSecurityManager fSecurityManager;
    private XMLSecurityPropertyManager fSecurityPropertyMgr;

    public static final int CONTEXT_READER = 1;
    public static final int CONTEXT_WRITER = 2;

    /** Creates a new instance of PropertyManager */
    public PropertyManager(int context) {
        switch(context){
            case CONTEXT_READER:{
                initConfigurableReaderProperties();
                break;
            }
            case CONTEXT_WRITER:{
                initWriterProps();
                break;
            }
        }
    }

    /**
     * Initialize this object with the properties taken from passed PropertyManager object.
     */
    public PropertyManager(com.sun.org.apache.xerces.internal.impl.PropertyManager propertyManager){

        HashMap properties = propertyManager.getProperties();
        supportedProps.putAll(properties);
        fSecurityManager = (XMLSecurityManager)getProperty(SECURITY_MANAGER);
        fSecurityPropertyMgr = (XMLSecurityPropertyManager)getProperty(XML_SECURITY_PROPERTY_MANAGER);
    }

    private HashMap getProperties(){
        return supportedProps ;
    }


    /**
     * Important point:
     * 1. We are not exposing Xerces namespace property. Application should configure namespace through
     * Stax specific property.
     *
     */
    private void initConfigurableReaderProperties(){
        //spec default values
        supportedProps.put(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        supportedProps.put(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
        supportedProps.put(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
        supportedProps.put(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        supportedProps.put(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        supportedProps.put(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
        supportedProps.put(XMLInputFactory.REPORTER, null);
        supportedProps.put(XMLInputFactory.RESOLVER, null);
        supportedProps.put(XMLInputFactory.ALLOCATOR, null);
        supportedProps.put(STAX_NOTATIONS,null );

        //zephyr (implementation) specific properties which can be set by the application.
        //interning is always done
        supportedProps.put(Constants.SAX_FEATURE_PREFIX + Constants.STRING_INTERNING_FEATURE , new Boolean(true));
        //recognizing java encoding names by default
        supportedProps.put(Constants.XERCES_FEATURE_PREFIX + Constants.ALLOW_JAVA_ENCODINGS_FEATURE,  new Boolean(true)) ;
        //in stax mode, namespace declarations are not added as attributes
        supportedProps.put(Constants.ADD_NAMESPACE_DECL_AS_ATTRIBUTE ,  Boolean.FALSE) ;
        supportedProps.put(Constants.READER_IN_DEFINED_STATE, new Boolean(true));
        supportedProps.put(Constants.REUSE_INSTANCE, new Boolean(true));
        supportedProps.put(Constants.ZEPHYR_PROPERTY_PREFIX + Constants.STAX_REPORT_CDATA_EVENT , new Boolean(false));
        supportedProps.put(Constants.ZEPHYR_PROPERTY_PREFIX + Constants.IGNORE_EXTERNAL_DTD, Boolean.FALSE);
        supportedProps.put(Constants.XERCES_FEATURE_PREFIX + Constants.WARN_ON_DUPLICATE_ATTDEF_FEATURE, new Boolean(false));
        supportedProps.put(Constants.XERCES_FEATURE_PREFIX + Constants.WARN_ON_DUPLICATE_ENTITYDEF_FEATURE, new Boolean(false));
        supportedProps.put(Constants.XERCES_FEATURE_PREFIX + Constants.WARN_ON_UNDECLARED_ELEMDEF_FEATURE, new Boolean(false));

        fSecurityManager = new XMLSecurityManager(true);
        supportedProps.put(SECURITY_MANAGER, fSecurityManager);
        fSecurityPropertyMgr = new XMLSecurityPropertyManager();
        supportedProps.put(XML_SECURITY_PROPERTY_MANAGER, fSecurityPropertyMgr);
    }

    private void initWriterProps(){
        supportedProps.put(XMLOutputFactory.IS_REPAIRING_NAMESPACES , Boolean.FALSE);
        //default value of escaping characters is 'true'
        supportedProps.put(Constants.ESCAPE_CHARACTERS , Boolean.TRUE);
        supportedProps.put(Constants.REUSE_INSTANCE, new Boolean(true));
    }

    /**
     * public void reset(){
     * supportedProps.clear() ;
     * }
     */
    public boolean containsProperty(String property){
        return supportedProps.containsKey(property) ||
                (fSecurityManager != null && fSecurityManager.getIndex(property) > -1) ||
                (fSecurityPropertyMgr!=null && fSecurityPropertyMgr.getIndex(property) > -1) ;
    }

    public Object getProperty(String property){
        return supportedProps.get(property);
    }

    public void setProperty(String property, Object value){
        String equivalentProperty = null ;
        if(property == XMLInputFactory.IS_NAMESPACE_AWARE || property.equals(XMLInputFactory.IS_NAMESPACE_AWARE)){
            equivalentProperty = Constants.XERCES_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE ;
        }
        else if(property == XMLInputFactory.IS_VALIDATING || property.equals(XMLInputFactory.IS_VALIDATING)){
            if( (value instanceof Boolean) && ((Boolean)value).booleanValue()){
                throw new IllegalArgumentException("true value of isValidating not supported") ;
            }
        }
        else if(property == STRING_INTERNING || property.equals(STRING_INTERNING)){
            if( (value instanceof Boolean) && !((Boolean)value).booleanValue()){
                throw new IllegalArgumentException("false value of " + STRING_INTERNING + "feature is not supported") ;
            }
        }
        else if(property == XMLInputFactory.RESOLVER || property.equals(XMLInputFactory.RESOLVER)){
            //add internal stax property
            supportedProps.put( Constants.XERCES_PROPERTY_PREFIX + Constants.STAX_ENTITY_RESOLVER_PROPERTY , new StaxEntityResolverWrapper((XMLResolver)value)) ;
        }

        /**
         * It's possible for users to set a security manager through the interface.
         * If it's the old SecurityManager, convert it to the new XMLSecurityManager
         */
        if (property.equals(Constants.SECURITY_MANAGER)) {
            fSecurityManager = XMLSecurityManager.convert(value, fSecurityManager);
            supportedProps.put(Constants.SECURITY_MANAGER, fSecurityManager);
            return;
        }
        if (property.equals(Constants.XML_SECURITY_PROPERTY_MANAGER)) {
            if (value == null) {
                fSecurityPropertyMgr = new XMLSecurityPropertyManager();
            } else {
                fSecurityPropertyMgr = (XMLSecurityPropertyManager)value;
            }
            supportedProps.put(Constants.XML_SECURITY_PROPERTY_MANAGER, fSecurityPropertyMgr);
            return;
        }

        //check if the property is managed by security manager
        if (fSecurityManager == null ||
                !fSecurityManager.setLimit(property, XMLSecurityManager.State.APIPROPERTY, value)) {
            //check if the property is managed by security property manager
            if (fSecurityPropertyMgr == null ||
                    !fSecurityPropertyMgr.setValue(property, XMLSecurityPropertyManager.State.APIPROPERTY, value)) {
                //fall back to the existing property manager
                supportedProps.put(property, value);
            }
        }

        if(equivalentProperty != null){
            supportedProps.put(equivalentProperty, value ) ;
        }
    }

    public String toString(){
        return supportedProps.toString();
    }

}//PropertyManager
