/*
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.imageio.plugins.common;

import com.sun.imageio.plugins.common.BitFile;
import com.sun.imageio.plugins.common.LZWStringTable;

import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/*
 * Modified from original LZWCompressor to change interface to passing a
 * buffer of data to be compressed.
 */
public class LZWCompressor {
    /** base underlying code size of data being compressed 8 for TIFF, 1 to 8 for GIF **/
    int codeSize;

    /** reserved clear code based on code size **/
    int clearCode;

    /** reserved end of data code based on code size **/
    int endOfInfo;

    /** current number bits output for each code **/
    int numBits;

    /** limit at which current number of bits code size has to be increased **/
    int limit;

    /** the prefix code which represents the predecessor string to current input point **/
    short prefix;

    /** output destination for bit codes **/
    BitFile bf;

    /** general purpose LZW string table **/
    LZWStringTable lzss;

    /** modify the limits of the code values in LZW encoding due to TIFF bug / feature **/
    boolean tiffFudge;

    /**
     * @param out destination for compressed data
     * @param codeSize the initial code size for the LZW compressor
     * @param TIFF flag indicating that TIFF lzw fudge needs to be applied
     * @exception IOException if underlying output stream error
     **/
    public LZWCompressor(ImageOutputStream out, int codeSize, boolean TIFF)
        throws IOException
    {
        bf = new BitFile(out, !TIFF); // set flag for GIF as NOT tiff
        this.codeSize = codeSize;
        tiffFudge = TIFF;
        clearCode = 1 << codeSize;
        endOfInfo = clearCode + 1;
        numBits = codeSize + 1;

        limit = (1 << numBits) - 1;
        if (tiffFudge) {
            --limit;
        }

        prefix = (short)0xFFFF;
        lzss = new LZWStringTable();
        lzss.clearTable(codeSize);
        bf.writeBits(clearCode, numBits);
    }

    /**
     * @param buf data to be compressed to output stream
     * @exception IOException if underlying output stream error
     **/
    public void compress(byte[] buf, int offset, int length)
        throws IOException
    {
        int idx;
        byte c;
        short index;

        int maxOffset = offset + length;
        for (idx = offset; idx < maxOffset; ++idx) {
            c = buf[idx];
            if ((index = lzss.findCharString(prefix, c)) != -1) {
                prefix = index;
            } else {
                bf.writeBits(prefix, numBits);
                if (lzss.addCharString(prefix, c) > limit) {
                    if (numBits == 12) {
                        bf.writeBits(clearCode, numBits);
                        lzss.clearTable(codeSize);
                        numBits = codeSize + 1;
                    } else {
                        ++numBits;
                    }

                    limit = (1 << numBits) - 1;
                    if (tiffFudge) {
                        --limit;
                    }
                }
                prefix = (short)((short)c & 0xFF);
            }
        }
    }

    /*
     * Indicate to compressor that no more data to go so write out
     * any remaining buffered data.
     *
     * @exception IOException if underlying output stream error
     */
    public void flush() throws IOException {
        if (prefix != -1) {
            bf.writeBits(prefix, numBits);
        }

        bf.writeBits(endOfInfo, numBits);
        bf.flush();
    }

    public void dump(PrintStream out) {
        lzss.dump(out);
    }
}
