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

package java8.javax.xml.bind;

import javax.xml.transform.Result;
import java.io.IOException;

/**
 * Controls where a JAXB implementation puts the generates
 * schema files.
 *
 * <p>
 * An implementation of this abstract class has to be provided by the calling
 * application to generate schemas.
 *
 * <p>
 * This is a class, not an interface so as to allow future versions to evolve
 * without breaking the compatibility.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public abstract class SchemaOutputResolver {
    /**
     * Decides where the schema file (of the given namespace URI)
     * will be written, and return it as a {@link Result} object.
     *
     * <p>
     * This method is called only once for any given namespace.
     * IOW, all the components in one namespace is always written
     * into the same schema document.
     *
     * @param namespaceUri
     *      The namespace URI that the schema declares.
     *      Can be the empty string, but never be null.
     * @param suggestedFileName
     *      A JAXB implementation generates an unique file name (like "schema1.xsd")
     *      for the convenience of the callee. This name can be
     *      used for the file name of the schema, or the callee can just
     *      ignore this name and come up with its own name.
     *      This is just a hint.
     *
     * @return
     *      a {@link Result} object that encapsulates the actual destination
     *      of the schema.
     *
     *      If the {@link Result} object has a system ID, it must be an
     *      absolute system ID. Those system IDs are relativized by the caller and used
     *      for &lt;xs:import> statements.
     *
     *      If the {@link Result} object does not have a system ID, a schema
     *      for the namespace URI is generated but it won't be explicitly
     *      &lt;xs:import>ed from other schemas.
     *
     *      If {@code null} is returned, the schema generation for this
     *      namespace URI will be skipped.
     */
    public abstract Result createOutput( String namespaceUri, String suggestedFileName ) throws IOException;
}
