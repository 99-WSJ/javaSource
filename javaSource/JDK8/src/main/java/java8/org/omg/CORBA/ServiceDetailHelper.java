/*
 * Copyright (c) 1998, 2001, Oracle and/or its affiliates. All rights reserved.
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

 /**
 * The Helper for <tt>ServiceDetail</tt>.  For more information on
 * Helper files, see <a href="doc-files/generatedfiles.html#helper">
 * "Generated Files: Helper Files"</a>.<P>
 */

package java8.org.omg.CORBA;


public abstract class ServiceDetailHelper {

    public static void write(org.omg.CORBA.portable.OutputStream out, org.omg.CORBA.ServiceDetail that) {
        out.write_ulong(that.service_detail_type);
        {
            out.write_long(that.service_detail.length);
            out.write_octet_array(that.service_detail, 0, that.service_detail.length);
        }
    }
    public static org.omg.CORBA.ServiceDetail read(org.omg.CORBA.portable.InputStream in) {
        org.omg.CORBA.ServiceDetail that = new org.omg.CORBA.ServiceDetail();
        that.service_detail_type = in.read_ulong();
        {
            int __length = in.read_long();
            that.service_detail = new byte[__length];
            in.read_octet_array(that.service_detail, 0, that.service_detail.length);
        }
        return that;
    }
    public static org.omg.CORBA.ServiceDetail extract(org.omg.CORBA.Any a) {
        org.omg.CORBA.portable.InputStream in = a.create_input_stream();
        return read(in);
    }
    public static void insert(org.omg.CORBA.Any a, org.omg.CORBA.ServiceDetail that) {
        org.omg.CORBA.portable.OutputStream out = a.create_output_stream();
        write(out, that);
        a.read_value(out.create_input_stream(), type());
    }
    private static org.omg.CORBA.TypeCode _tc;
    synchronized public static org.omg.CORBA.TypeCode type() {
        int _memberCount = 2;
        org.omg.CORBA.StructMember[] _members = null;
        if (_tc == null) {
            _members= new org.omg.CORBA.StructMember[2];
            _members[0] = new org.omg.CORBA.StructMember(
                                                         "service_detail_type",
                                                         org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_ulong),
                                                         null);

            _members[1] = new org.omg.CORBA.StructMember(
                                                         "service_detail",
                                                         org.omg.CORBA.ORB.init().create_sequence_tc(0, org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_octet)),
                                                         null);
            _tc = org.omg.CORBA.ORB.init().create_struct_tc(id(), "ServiceDetail", _members);
        }
        return _tc;
    }
    public static String id() {
        return "IDL:omg.org/CORBA/ServiceDetail:1.0";
    }
}
