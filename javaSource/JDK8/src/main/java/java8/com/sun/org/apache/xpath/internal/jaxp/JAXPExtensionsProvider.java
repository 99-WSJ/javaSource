/*
 * Copyright (c) 2007-2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// $Id: JAXPExtensionsProvider.java,v 1.1.2.1 2005/08/01 01:30:17 jeffsuttor Exp $

package java8.sun.org.apache.xpath.internal.jaxp;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xalan.internal.utils.FeatureManager;
import com.sun.org.apache.xpath.internal.ExtensionsProvider;
import com.sun.org.apache.xpath.internal.functions.FuncExtFunction;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.res.XPATHErrorResources;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;
import javax.xml.xpath.XPathFunctionResolver;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author Ramesh Mandava ( ramesh.mandava@sun.com )
 */
public class JAXPExtensionsProvider implements ExtensionsProvider {

    private final XPathFunctionResolver resolver;
    private boolean extensionInvocationDisabled = false;

    public JAXPExtensionsProvider(XPathFunctionResolver resolver) {
        this.resolver = resolver;
        this.extensionInvocationDisabled = false;
    }

    public JAXPExtensionsProvider(XPathFunctionResolver resolver,
        boolean featureSecureProcessing, FeatureManager featureManager ) {
        this.resolver = resolver;
        if (featureSecureProcessing &&
                !featureManager.isFeatureEnabled(FeatureManager.Feature.ORACLE_ENABLE_EXTENSION_FUNCTION)) {
            this.extensionInvocationDisabled = true;
        }
    }

    /**
     * Is the extension function available?
     */

    public boolean functionAvailable(String ns, String funcName)
          throws TransformerException {
      try {
        if ( funcName == null ) {
            String fmsg = XSLMessages.createXPATHMessage(
                XPATHErrorResources.ER_ARG_CANNOT_BE_NULL,
                new Object[] {"Function Name"} );
            throw new NullPointerException ( fmsg );
        }
        //Find the XPathFunction corresponding to namespace and funcName
        QName myQName = new QName( ns, funcName );
        XPathFunction xpathFunction =
            resolver.resolveFunction ( myQName, 0 );
        if (  xpathFunction == null ) {
            return false;
        }
        return true;
      } catch ( Exception e ) {
        return false;
      }


    }


    /**
     * Is the extension element available?
     */
    public boolean elementAvailable(String ns, String elemName)
          throws TransformerException {
        return false;
    }

    /**
     * Execute the extension function.
     */
    public Object extFunction(String ns, String funcName, Vector argVec,
        Object methodKey) throws TransformerException {
        try {

            if ( funcName == null ) {
                String fmsg = XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_ARG_CANNOT_BE_NULL,
                    new Object[] {"Function Name"} );
                throw new NullPointerException ( fmsg );
            }
            //Find the XPathFunction corresponding to namespace and funcName
            QName myQName = new QName( ns, funcName );

            // JAXP 1.3 spec says When XMLConstants.FEATURE_SECURE_PROCESSING
            // feature is set then invocation of extension functions need to
            // throw XPathFunctionException
            if ( extensionInvocationDisabled ) {
                String fmsg = XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED,
                    new Object[] { myQName.toString() } );
                throw new XPathFunctionException ( fmsg );
            }

            // Assuming user is passing all the needed parameters ( including
            // default values )
            int arity = argVec.size();

            XPathFunction xpathFunction =
                resolver.resolveFunction ( myQName, arity );

            // not using methodKey
            ArrayList argList = new ArrayList( arity);
            for ( int i=0; i<arity; i++ ) {
                Object argument = argVec.elementAt( i );
                // XNodeSet object() returns NodeVector and not NodeList
                // Explicitly getting NodeList by using nodelist()
                if ( argument instanceof XNodeSet ) {
                    argList.add ( i, ((XNodeSet)argument).nodelist() );
                } else if ( argument instanceof XObject ) {
                    Object passedArgument = ((XObject)argument).object();
                    argList.add ( i, passedArgument );
                } else {
                    argList.add ( i, argument );
                }
            }

            return ( xpathFunction.evaluate ( argList ));
        } catch ( XPathFunctionException xfe ) {
            // If we get XPathFunctionException then we want to terminate
            // further execution by throwing WrappedRuntimeException
            throw new com.sun.org.apache.xml.internal.utils.WrappedRuntimeException ( xfe );
        } catch ( Exception e ) {
            throw new TransformerException ( e );
        }

    }

    /**
     * Execute the extension function.
     */
    public Object extFunction(FuncExtFunction extFunction,
                              Vector argVec)
        throws TransformerException {
        try {
            String namespace = extFunction.getNamespace();
            String functionName = extFunction.getFunctionName();
            int arity = extFunction.getArgCount();
            QName myQName =
                new QName( namespace, functionName );

            // JAXP 1.3 spec says  When XMLConstants.FEATURE_SECURE_PROCESSING
            // feature is set then invocation of extension functions need to
            // throw XPathFunctionException
            if ( extensionInvocationDisabled ) {
                String fmsg = XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED,                    new Object[] { myQName.toString() } );
                throw new XPathFunctionException ( fmsg );
            }

            XPathFunction xpathFunction =
                resolver.resolveFunction( myQName, arity );

            ArrayList argList = new ArrayList( arity);
            for ( int i=0; i<arity; i++ ) {
                Object argument = argVec.elementAt( i );
                // XNodeSet object() returns NodeVector and not NodeList
                // Explicitly getting NodeList by using nodelist()
                if ( argument instanceof XNodeSet ) {
                    argList.add ( i, ((XNodeSet)argument).nodelist() );
                } else if ( argument instanceof XObject ) {
                    Object passedArgument = ((XObject)argument).object();
                    argList.add ( i, passedArgument );
                } else {
                    argList.add ( i, argument );
                }
            }

            return ( xpathFunction.evaluate ( argList ));

        } catch ( XPathFunctionException xfe ) {
            // If we get XPathFunctionException then we want to terminate
            // further execution by throwing WrappedRuntimeException
            throw new com.sun.org.apache.xml.internal.utils.WrappedRuntimeException ( xfe );
        } catch ( Exception e ) {
            throw new TransformerException ( e );
        }
    }

}
