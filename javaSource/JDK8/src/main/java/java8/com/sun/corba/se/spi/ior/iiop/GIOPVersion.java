/*
 * Copyright (c) 2000, 2004, Oracle and/or its affiliates. All rights reserved.
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

package java8.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;

public class GIOPVersion {

    // Static fields

    public static final com.sun.corba.se.spi.ior.iiop.GIOPVersion V1_0 = new com.sun.corba.se.spi.ior.iiop.GIOPVersion((byte)1, (byte)0);
    public static final com.sun.corba.se.spi.ior.iiop.GIOPVersion V1_1 = new com.sun.corba.se.spi.ior.iiop.GIOPVersion((byte)1, (byte)1);
    public static final com.sun.corba.se.spi.ior.iiop.GIOPVersion V1_2 = new com.sun.corba.se.spi.ior.iiop.GIOPVersion((byte)1, (byte)2);
    public static final com.sun.corba.se.spi.ior.iiop.GIOPVersion V1_3 = new com.sun.corba.se.spi.ior.iiop.GIOPVersion((byte)1, (byte)3);

    // Major version 13 indicates Java serialization,
    // Minor version [00-FF] is the version number.
    public static final com.sun.corba.se.spi.ior.iiop.GIOPVersion V13_XX =
        new com.sun.corba.se.spi.ior.iiop.GIOPVersion((byte)13, (byte)Message.JAVA_ENC_VERSION);

    public static final com.sun.corba.se.spi.ior.iiop.GIOPVersion DEFAULT_VERSION = V1_2;

    public static final int VERSION_1_0 = 0x0100;
    public static final int VERSION_1_1 = 0x0101;
    public static final int VERSION_1_2 = 0x0102;
    public static final int VERSION_1_3 = 0x0103;
    public static final int VERSION_13_XX =
        ((0x0D << 8) & 0x0000FF00) | Message.JAVA_ENC_VERSION;

    // Instance variables

    private byte major = (byte) 0;
    private byte minor = (byte) 0;

    // Constructor

    public GIOPVersion() {}

    public GIOPVersion(byte majorB, byte minorB) {
        this.major = majorB;
        this.minor = minorB;
    }

    public GIOPVersion(int major, int minor) {
        this.major = (byte)major;
        this.minor = (byte)minor;
    }

    // Accessor methods

    public byte getMajor() {
        return this.major;
    }

    public byte getMinor() {
        return this.minor;
    }

    // General methods

    public boolean equals(com.sun.corba.se.spi.ior.iiop.GIOPVersion gv){
        return gv.major == this.major && gv.minor == this.minor ;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof com.sun.corba.se.spi.ior.iiop.GIOPVersion))
            return equals((com.sun.corba.se.spi.ior.iiop.GIOPVersion)obj);
        else
            return false;
    }

    public int hashCode()
    {
        return 37*major + minor ;
    }

    public boolean lessThan(com.sun.corba.se.spi.ior.iiop.GIOPVersion gv) {
        if (this.major < gv.major) {
            return true;
        } else if (this.major == gv.major) {
            if (this.minor < gv.minor) {
                return true;
            }
        }

        return false;
    }

    public int intValue()
    {
        return (major << 8 | minor);
    }

    public String toString()
    {
        return major + "." + minor;
    }

    public static com.sun.corba.se.spi.ior.iiop.GIOPVersion getInstance(byte major, byte minor)
    {
        switch(((major << 8) | minor)) {
            case VERSION_1_0:
                return com.sun.corba.se.spi.ior.iiop.GIOPVersion.V1_0;
            case VERSION_1_1:
                return com.sun.corba.se.spi.ior.iiop.GIOPVersion.V1_1;
            case VERSION_1_2:
                return com.sun.corba.se.spi.ior.iiop.GIOPVersion.V1_2;
            case VERSION_1_3:
                return com.sun.corba.se.spi.ior.iiop.GIOPVersion.V1_3;
            case VERSION_13_XX:
                return com.sun.corba.se.spi.ior.iiop.GIOPVersion.V13_XX;
            default:
                return new com.sun.corba.se.spi.ior.iiop.GIOPVersion(major, minor);
        }
    }

    public static com.sun.corba.se.spi.ior.iiop.GIOPVersion parseVersion(String s)
    {
        int dotIdx = s.indexOf('.');

        if (dotIdx < 1 || dotIdx == s.length() - 1)
            throw new NumberFormatException("GIOP major, minor, and decimal point required: " + s);

        int major = Integer.parseInt(s.substring(0, dotIdx));
        int minor = Integer.parseInt(s.substring(dotIdx + 1, s.length()));

        return com.sun.corba.se.spi.ior.iiop.GIOPVersion.getInstance((byte)major, (byte)minor);
    }

    /**
     * This chooses the appropriate GIOP version.
     *
     * @return the GIOP version 13.00 if Java serialization is enabled, or
     *         smallest(profGIOPVersion, orbGIOPVersion)
     */
    public static com.sun.corba.se.spi.ior.iiop.GIOPVersion chooseRequestVersion(ORB orb, IOR ior ) {

        com.sun.corba.se.spi.ior.iiop.GIOPVersion orbVersion = orb.getORBData().getGIOPVersion();
        IIOPProfile prof = ior.getProfile() ;
        com.sun.corba.se.spi.ior.iiop.GIOPVersion profVersion = prof.getGIOPVersion();

        // Check if the profile is from a legacy Sun ORB.

        ORBVersion targetOrbVersion = prof.getORBVersion();
        if (!(targetOrbVersion.equals(ORBVersionFactory.getFOREIGN())) &&
                targetOrbVersion.lessThan(ORBVersionFactory.getNEWER())) {
            // we are dealing with a SUN legacy orb which emits 1.1 IORs,
            // in spite of being able to handle only GIOP 1.0 messages.
            return V1_0;
        }

        // Now the target has to be (FOREIGN | NEWER*)

        byte prof_major = profVersion.getMajor();
        byte prof_minor = profVersion.getMinor();

        byte orb_major = orbVersion.getMajor();
        byte orb_minor = orbVersion.getMinor();

        if (orb_major < prof_major) {
            return orbVersion;
        } else if (orb_major > prof_major) {
            return profVersion;
        } else { // both major version are the same
            if (orb_minor <= prof_minor) {
                return orbVersion;
            } else {
                return profVersion;
            }
        }
    }

    public boolean supportsIORIIOPProfileComponents()
    {
        return getMinor() > 0 || getMajor() > 1;
    }

    // IO methods

    public void read(org.omg.CORBA.portable.InputStream istream) {
        this.major = istream.read_octet();
        this.minor = istream.read_octet();
    }

    public void write(org.omg.CORBA.portable.OutputStream ostream) {
        ostream.write_octet(this.major);
        ostream.write_octet(this.minor);
    }
}
