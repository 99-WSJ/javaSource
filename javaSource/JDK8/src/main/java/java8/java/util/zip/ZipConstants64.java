/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.util.zip;

/*
 * This class defines the constants that are used by the classes
 * which manipulate Zip64 files.
 */

class ZipConstants64 {

    /*
     * ZIP64 constants
     */
    static final long ZIP64_ENDSIG = 0x06064b50L;  // "PK\006\006"
    static final long ZIP64_LOCSIG = 0x07064b50L;  // "PK\006\007"
    static final int  ZIP64_ENDHDR = 56;           // ZIP64 end header size
    static final int  ZIP64_LOCHDR = 20;           // ZIP64 end loc header size
    static final int  ZIP64_EXTHDR = 24;           // EXT header size
    static final int  ZIP64_EXTID  = 0x0001;       // Extra field Zip64 header ID

    static final int  ZIP64_MAGICCOUNT = 0xFFFF;
    static final long ZIP64_MAGICVAL = 0xFFFFFFFFL;

    /*
     * Zip64 End of central directory (END) header field offsets
     */
    static final int  ZIP64_ENDLEN = 4;       // size of zip64 end of central dir
    static final int  ZIP64_ENDVEM = 12;      // version made by
    static final int  ZIP64_ENDVER = 14;      // version needed to extract
    static final int  ZIP64_ENDNMD = 16;      // number of this disk
    static final int  ZIP64_ENDDSK = 20;      // disk number of start
    static final int  ZIP64_ENDTOD = 24;      // total number of entries on this disk
    static final int  ZIP64_ENDTOT = 32;      // total number of entries
    static final int  ZIP64_ENDSIZ = 40;      // central directory size in bytes
    static final int  ZIP64_ENDOFF = 48;      // offset of first CEN header
    static final int  ZIP64_ENDEXT = 56;      // zip64 extensible data sector

    /*
     * Zip64 End of central directory locator field offsets
     */
    static final int  ZIP64_LOCDSK = 4;       // disk number start
    static final int  ZIP64_LOCOFF = 8;       // offset of zip64 end
    static final int  ZIP64_LOCTOT = 16;      // total number of disks

    /*
     * Zip64 Extra local (EXT) header field offsets
     */
    static final int  ZIP64_EXTCRC = 4;       // uncompressed file crc-32 value
    static final int  ZIP64_EXTSIZ = 8;       // compressed size, 8-byte
    static final int  ZIP64_EXTLEN = 16;      // uncompressed size, 8-byte

    /*
     * Language encoding flag EFS
     */
    static final int EFS = 0x800;       // If this bit is set the filename and
                                        // comment fields for this file must be
                                        // encoded using UTF-8.

    /*
     * Constants below are defined here (instead of in ZipConstants)
     * to avoid being exposed as public fields of ZipFile, ZipEntry,
     * ZipInputStream and ZipOutputstream.
     */

    /*
     * Extra field header ID
     */
    static final int  EXTID_ZIP64 = 0x0001;    // Zip64
    static final int  EXTID_NTFS  = 0x000a;    // NTFS
    static final int  EXTID_UNIX  = 0x000d;    // UNIX
    static final int  EXTID_EXTT  = 0x5455;    // Info-ZIP Extended Timestamp

    /*
     * EXTT timestamp flags
     */
    static final int  EXTT_FLAG_LMT = 0x1;       // LastModifiedTime
    static final int  EXTT_FLAG_LAT = 0x2;       // LastAccessTime
    static final int  EXTT_FLAT_CT  = 0x4;       // CreationTime

    private ZipConstants64() {}
}
