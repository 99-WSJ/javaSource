/*
 * Copyright (c) 2009, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.nio.file;

import sun.security.action.GetPropertyAction;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.SecureRandom;
import java.util.EnumSet;
import java.util.Set;

import static java.nio.file.attribute.PosixFilePermission.*;
import static java.security.AccessController.doPrivileged;


/**
 * Helper class to support creation of temporary files and directories with
 * initial attributes.
 */

class TempFileHelper {
    private TempFileHelper() { }

    // temporary directory location
    private static final Path tmpdir =
        Paths.get(doPrivileged(new GetPropertyAction("java.io.tmpdir")));

    private static final boolean isPosix =
        FileSystems.getDefault().supportedFileAttributeViews().contains("posix");

    // file name generation, same as java.io.File for now
    private static final SecureRandom random = new SecureRandom();
    private static Path generatePath(String prefix, String suffix, Path dir) {
        long n = random.nextLong();
        n = (n == Long.MIN_VALUE) ? 0 : Math.abs(n);
        Path name = dir.getFileSystem().getPath(prefix + Long.toString(n) + suffix);
        // the generated name should be a simple file name
        if (name.getParent() != null)
            throw new IllegalArgumentException("Invalid prefix or suffix");
        return dir.resolve(name);
    }

    // default file and directory permissions (lazily initialized)
    private static class PosixPermissions {
        static final FileAttribute<Set<PosixFilePermission>> filePermissions =
            PosixFilePermissions.asFileAttribute(EnumSet.of(OWNER_READ, OWNER_WRITE));
        static final FileAttribute<Set<PosixFilePermission>> dirPermissions =
            PosixFilePermissions.asFileAttribute(EnumSet
                .of(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE));
    }

    /**
     * Creates a file or directory in in the given given directory (or in the
     * temporary directory if dir is {@code null}).
     */
    private static Path create(Path dir,
                               String prefix,
                               String suffix,
                               boolean createDirectory,
                               FileAttribute<?>[] attrs)
        throws IOException
    {
        if (prefix == null)
            prefix = "";
        if (suffix == null)
            suffix = (createDirectory) ? "" : ".tmp";
        if (dir == null)
            dir = tmpdir;

        // in POSIX environments use default file and directory permissions
        // if initial permissions not given by caller.
        if (isPosix && (dir.getFileSystem() == FileSystems.getDefault())) {
            if (attrs.length == 0) {
                // no attributes so use default permissions
                attrs = new FileAttribute<?>[1];
                attrs[0] = (createDirectory) ? PosixPermissions.dirPermissions :
                                               PosixPermissions.filePermissions;
            } else {
                // check if posix permissions given; if not use default
                boolean hasPermissions = false;
                for (int i=0; i<attrs.length; i++) {
                    if (attrs[i].name().equals("posix:permissions")) {
                        hasPermissions = true;
                        break;
                    }
                }
                if (!hasPermissions) {
                    FileAttribute<?>[] copy = new FileAttribute<?>[attrs.length+1];
                    System.arraycopy(attrs, 0, copy, 0, attrs.length);
                    attrs = copy;
                    attrs[attrs.length-1] = (createDirectory) ?
                        PosixPermissions.dirPermissions :
                        PosixPermissions.filePermissions;
                }
            }
        }

        // loop generating random names until file or directory can be created
        SecurityManager sm = System.getSecurityManager();
        for (;;) {
            Path f;
            try {
                f = generatePath(prefix, suffix, dir);
            } catch (InvalidPathException e) {
                // don't reveal temporary directory location
                if (sm != null)
                    throw new IllegalArgumentException("Invalid prefix or suffix");
                throw e;
            }
            try {
                if (createDirectory) {
                    return Files.createDirectory(f, attrs);
                } else {
                    return Files.createFile(f, attrs);
                }
            } catch (SecurityException e) {
                // don't reveal temporary directory location
                if (dir == tmpdir && sm != null)
                    throw new SecurityException("Unable to create temporary file or directory");
                throw e;
            } catch (FileAlreadyExistsException e) {
                // ignore
            }
        }
    }

    /**
     * Creates a temporary file in the given directory, or in in the
     * temporary directory if dir is {@code null}.
     */
    static Path createTempFile(Path dir,
                               String prefix,
                               String suffix,
                               FileAttribute<?>[] attrs)
        throws IOException
    {
        return create(dir, prefix, suffix, false, attrs);
    }

    /**
     * Creates a temporary directory in the given directory, or in in the
     * temporary directory if dir is {@code null}.
     */
    static Path createTempDirectory(Path dir,
                                    String prefix,
                                    FileAttribute<?>[] attrs)
        throws IOException
    {
        return create(dir, prefix, null, true, attrs);
    }
}
