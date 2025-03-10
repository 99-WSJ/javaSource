/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.swing.filechooser;

import java.io.File;
import javax.swing.*;

/**
 * <code>FileView</code> defines an abstract class that can be implemented
 * to provide the filechooser with UI information for a <code>File</code>.
 * Each L&amp;F <code>JFileChooserUI</code> object implements this
 * class to pass back the correct icons and type descriptions specific to
 * that L&amp;F. For example, the Microsoft Windows L&amp;F returns the
 * generic Windows icons for directories and generic files.
 * Additionally, you may want to provide your own <code>FileView</code> to
 * <code>JFileChooser</code> to return different icons or additional
 * information using {@link JFileChooser#setFileView}.
 *
 * <p>
 *
 * <code>JFileChooser</code> first looks to see if there is a user defined
 * <code>FileView</code>, if there is, it gets type information from
 * there first. If <code>FileView</code> returns <code>null</code> for
 * any method, <code>JFileChooser</code> then uses the L&amp;F specific
 * view to get the information.
 * So, for example, if you provide a <code>FileView</code> class that
 * returns an <code>Icon</code> for JPG files, and returns <code>null</code>
 * icons for all other files, the UI's <code>FileView</code> will provide
 * default icons for all other files.
 *
 * <p>
 *
 * For an example implementation of a simple file view, see
 * <code><i>yourJDK</i>/demo/jfc/FileChooserDemo/ExampleFileView.java</code>.
 * For more information and examples see
 * <a
 href="http://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html">How to Use File Choosers</a>,
 * a section in <em>The Java Tutorial</em>.
 *
 * @see JFileChooser
 *
 * @author Jeff Dinkins
 *
 */
public abstract class FileView {
    /**
     * The name of the file. Normally this would be simply
     * <code>f.getName()</code>.
     */
    public String getName(File f) {
        return null;
    };

    /**
     * A human readable description of the file. For example,
     * a file named <i>jag.jpg</i> might have a description that read:
     * "A JPEG image file of James Gosling's face".
     */
    public String getDescription(File f) {
        return null;
    }

    /**
     * A human readable description of the type of the file. For
     * example, a <code>jpg</code> file might have a type description of:
     * "A JPEG Compressed Image File"
     */
    public String getTypeDescription(File f) {
        return null;
    }

    /**
     * The icon that represents this file in the <code>JFileChooser</code>.
     */
    public Icon getIcon(File f) {
        return null;
    }

    /**
     * Whether the directory is traversable or not. This might be
     * useful, for example, if you want a directory to represent
     * a compound document and don't want the user to descend into it.
     */
    public Boolean isTraversable(File f) {
        return null;
    }

}
