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


package java8.java.util.logging;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * This <tt>Handler</tt> publishes log records to <tt>System.err</tt>.
 * By default the <tt>SimpleFormatter</tt> is used to generate brief summaries.
 * <p>
 * <b>Configuration:</b>
 * By default each <tt>ConsoleHandler</tt> is initialized using the following
 * <tt>LogManager</tt> configuration properties where {@code <handler-name>}
 * refers to the fully-qualified class name of the handler.
 * If properties are not defined
 * (or have invalid values) then the specified default values are used.
 * <ul>
 * <li>   &lt;handler-name&gt;.level
 *        specifies the default level for the <tt>Handler</tt>
 *        (defaults to <tt>Level.INFO</tt>). </li>
 * <li>   &lt;handler-name&gt;.filter
 *        specifies the name of a <tt>Filter</tt> class to use
 *        (defaults to no <tt>Filter</tt>). </li>
 * <li>   &lt;handler-name&gt;.formatter
 *        specifies the name of a <tt>Formatter</tt> class to use
 *        (defaults to <tt>java.util.logging.SimpleFormatter</tt>). </li>
 * <li>   &lt;handler-name&gt;.encoding
 *        the name of the character set encoding to use (defaults to
 *        the default platform encoding). </li>
 * </ul>
 * <p>
 * For example, the properties for {@code ConsoleHandler} would be:
 * <ul>
 * <li>   java.util.logging.ConsoleHandler.level=INFO </li>
 * <li>   java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter </li>
 * </ul>
 * <p>
 * For a custom handler, e.g. com.foo.MyHandler, the properties would be:
 * <ul>
 * <li>   com.foo.MyHandler.level=INFO </li>
 * <li>   com.foo.MyHandler.formatter=java.util.logging.SimpleFormatter </li>
 * </ul>
 * <p>
 * @since 1.4
 */
public class ConsoleHandler extends StreamHandler {
    // Private method to configure a ConsoleHandler from LogManager
    // properties and/or default values as specified in the class
    // javadoc.
    private void configure() {
        LogManager manager = LogManager.getLogManager();
        String cname = getClass().getName();

        setLevel(manager.getLevelProperty(cname +".level", Level.INFO));
        setFilter(manager.getFilterProperty(cname +".filter", null));
        setFormatter(manager.getFormatterProperty(cname +".formatter", new SimpleFormatter()));
        try {
            setEncoding(manager.getStringProperty(cname +".encoding", null));
        } catch (Exception ex) {
            try {
                setEncoding(null);
            } catch (Exception ex2) {
                // doing a setEncoding with null should always work.
                // assert false;
            }
        }
    }

    /**
     * Create a <tt>ConsoleHandler</tt> for <tt>System.err</tt>.
     * <p>
     * The <tt>ConsoleHandler</tt> is configured based on
     * <tt>LogManager</tt> properties (or their default values).
     *
     */
    public ConsoleHandler() {
        sealed = false;
        configure();
        setOutputStream(System.err);
        sealed = true;
    }

    /**
     * Publish a <tt>LogRecord</tt>.
     * <p>
     * The logging request was made initially to a <tt>Logger</tt> object,
     * which initialized the <tt>LogRecord</tt> and forwarded it here.
     * <p>
     * @param  record  description of the log event. A null record is
     *                 silently ignored and is not published
     */
    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        flush();
    }

    /**
     * Override <tt>StreamHandler.close</tt> to do a flush but not
     * to close the output stream.  That is, we do <b>not</b>
     * close <tt>System.err</tt>.
     */
    @Override
    public void close() {
        flush();
    }
}
