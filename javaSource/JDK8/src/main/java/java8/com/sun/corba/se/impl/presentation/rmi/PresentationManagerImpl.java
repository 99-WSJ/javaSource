/*
 * Copyright (c) 2003, 2004, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import com.sun.corba.se.impl.orbutil.graph.GraphImpl;
import com.sun.corba.se.impl.orbutil.graph.Node;
import com.sun.corba.se.impl.presentation.rmi.DynamicMethodMarshallerImpl;
import com.sun.corba.se.impl.presentation.rmi.IDLNameTranslatorImpl;
import com.sun.corba.se.impl.presentation.rmi.InvocationHandlerFactoryImpl;
import com.sun.corba.se.spi.logging.CORBALogDomains;
import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;

import javax.rmi.CORBA.Tie;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.*;

public final class PresentationManagerImpl implements PresentationManager
{
    private Map classToClassData ;
    private Map methodToDMM ;
    private StubFactoryFactory staticStubFactoryFactory ;
    private StubFactoryFactory dynamicStubFactoryFactory ;
    private ORBUtilSystemException wrapper = null ;
    private boolean useDynamicStubs ;

    public PresentationManagerImpl( boolean useDynamicStubs )
    {
        this.useDynamicStubs = useDynamicStubs ;
        wrapper = ORBUtilSystemException.get(
            CORBALogDomains.RPC_PRESENTATION ) ;

        // XXX these should probably be WeakHashMaps.
        classToClassData = new HashMap() ;
        methodToDMM = new HashMap() ;
    }

////////////////////////////////////////////////////////////////////////////////
// PresentationManager interface
////////////////////////////////////////////////////////////////////////////////

    public synchronized DynamicMethodMarshaller getDynamicMethodMarshaller(
        Method method )
    {
        if (method == null)
            return null ;

        DynamicMethodMarshaller result =
            (DynamicMethodMarshaller)methodToDMM.get( method ) ;
        if (result == null) {
            result = new DynamicMethodMarshallerImpl( method ) ;
            methodToDMM.put( method, result ) ;
        }

        return result ;
    }

    public synchronized ClassData getClassData( Class cls )
    {
        ClassData result = (ClassData)classToClassData.get( cls ) ;
        if (result == null) {
            result = new ClassDataImpl( cls ) ;
            classToClassData.put( cls, result ) ;
        }

        return result ;
    }

    private class ClassDataImpl implements ClassData
    {
        private Class cls ;
        private IDLNameTranslator nameTranslator ;
        private String[] typeIds ;
        private StubFactory sfactory ;
        private InvocationHandlerFactory ihfactory ;
        private Map dictionary ;

        public ClassDataImpl( Class cls )
        {
            this.cls = cls ;
            Graph gr = new GraphImpl() ;
            NodeImpl root = new NodeImpl( cls ) ;
            Set rootSet = getRootSet( cls, root, gr ) ;

            // At this point, rootSet contains those remote interfaces
            // that are not related by inheritance, and gr contains
            // all reachable remote interfaces.

            Class[] interfaces = getInterfaces( rootSet ) ;
            nameTranslator = IDLNameTranslatorImpl.get( interfaces ) ;
            typeIds = makeTypeIds( root, gr, rootSet ) ;
            ihfactory = new InvocationHandlerFactoryImpl(
                com.sun.corba.se.impl.presentation.rmi.PresentationManagerImpl.this, this ) ;
            dictionary = new HashMap() ;
        }

        public Class getMyClass()
        {
            return cls ;
        }

        public IDLNameTranslator getIDLNameTranslator()
        {
            return nameTranslator ;
        }

        public String[] getTypeIds()
        {
            return typeIds ;
        }

        public InvocationHandlerFactory getInvocationHandlerFactory()
        {
            return ihfactory ;
        }

        public Map getDictionary()
        {
            return dictionary ;
        }
    }

    public StubFactoryFactory getStubFactoryFactory(
        boolean isDynamic )
    {
        if (isDynamic)
            return dynamicStubFactoryFactory ;
        else
            return staticStubFactoryFactory ;
    }

    public void setStubFactoryFactory( boolean isDynamic,
        StubFactoryFactory sff )
    {
        if (isDynamic)
            dynamicStubFactoryFactory = sff ;
        else
            staticStubFactoryFactory = sff ;
    }

    public Tie getTie()
    {
        return dynamicStubFactoryFactory.getTie( null ) ;
    }

    public boolean useDynamicStubs()
    {
        return useDynamicStubs ;
    }

////////////////////////////////////////////////////////////////////////////////
// Graph computations
////////////////////////////////////////////////////////////////////////////////

    private Set getRootSet( Class target, NodeImpl root, Graph gr )
    {
        Set rootSet = null ;

        if (target.isInterface()) {
            gr.add( root ) ;
            rootSet = gr.getRoots() ; // rootSet just contains root here
        } else {
            // Use this class and its superclasses (not Object) as initial roots
            Class superclass = target ;
            Set initialRootSet = new HashSet() ;
            while ((superclass != null) && !superclass.equals( Object.class )) {
                Node node = new NodeImpl( superclass ) ;
                gr.add( node ) ;
                initialRootSet.add( node ) ;
                superclass = superclass.getSuperclass() ;
            }

            // Expand all nodes into the graph
            gr.getRoots() ;

            // remove the roots and find roots again
            gr.removeAll( initialRootSet ) ;
            rootSet = gr.getRoots() ;
        }

        return rootSet ;
    }

    private Class[] getInterfaces( Set roots )
    {
        Class[] classes = new Class[ roots.size() ] ;
        Iterator iter = roots.iterator() ;
        int ctr = 0 ;
        while (iter.hasNext()) {
            NodeImpl node = (NodeImpl)iter.next() ;
            classes[ctr++] = node.getInterface() ;
        }

        return classes ;
    }

    private String[] makeTypeIds( NodeImpl root, Graph gr, Set rootSet )
    {
        Set nonRootSet = new HashSet( gr ) ;
        nonRootSet.removeAll( rootSet ) ;

        // List<String> for the typeids
        List result = new ArrayList() ;

        if (rootSet.size() > 1) {
            // If the rootSet has more than one element, we must
            // put the type id of the implementation class first.
            // Root represents the implementation class here.
            result.add( root.getTypeId() ) ;
        }

        addNodes( result, rootSet ) ;
        addNodes( result, nonRootSet ) ;

        return (String[])result.toArray( new String[result.size()] ) ;
    }

    private void addNodes( List resultList, Set nodeSet )
    {
        Iterator iter = nodeSet.iterator() ;
        while (iter.hasNext()) {
            NodeImpl node = (NodeImpl)iter.next() ;
            String typeId = node.getTypeId() ;
            resultList.add( typeId ) ;
        }
    }

    private static class NodeImpl implements Node
    {
        private Class interf ;

        public Class getInterface()
        {
            return interf ;
        }

        public NodeImpl( Class interf )
        {
            this.interf = interf ;
        }

        public String getTypeId()
        {
            return "RMI:" + interf.getName() + ":0000000000000000" ;
        }

        public Set getChildren()
        {
            Set result = new HashSet() ;
            Class[] interfaces = interf.getInterfaces() ;
            for (int ctr=0; ctr<interfaces.length; ctr++) {
                Class cls = interfaces[ctr] ;
                if (Remote.class.isAssignableFrom(cls) &&
                    !Remote.class.equals(cls))
                    result.add( new NodeImpl( cls ) ) ;
            }

            return result ;
        }

        public String toString()
        {
            return "NodeImpl[" + interf + "]" ;
        }

        public int hashCode()
        {
            return interf.hashCode() ;
        }

        public boolean equals( Object obj )
        {
            if (this == obj)
                return true ;

            if (!(obj instanceof NodeImpl))
                return false ;

            NodeImpl other = (NodeImpl)obj ;

            return other.interf.equals( interf ) ;
        }
    }
}
