/*
 * Copyright (c) 1999, 2001, Oracle and/or its affiliates. All rights reserved.
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
package java8.org.omg.CORBA;


/**
* The Helper for <tt>UnionMember</tt>.  For more information on
* Helper files, see <a href="doc-files/generatedfiles.html#helper">
* "Generated Files: Helper Files"</a>.<P>
* org/omg/CORBA/UnionMemberHelper.java
* Generated by the IDL-to-Java compiler (portable), version "3.0"
* from ir.idl
* 03 June 1999 11:33:43 o'clock GMT+00:00
*/

abstract public class UnionMemberHelper
{
  private static String  _id = "IDL:omg.org/CORBA/UnionMember:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.CORBA.UnionMember that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.CORBA.UnionMember extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [4];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CORBA.IdentifierHelper.id (), "Identifier", _tcOf_members0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "name",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_any);
          _members0[1] = new org.omg.CORBA.StructMember (
            "label",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_TypeCode);
          _members0[2] = new org.omg.CORBA.StructMember (
            "type",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.IDLTypeHelper.type ();
          _members0[3] = new org.omg.CORBA.StructMember (
            "type_def",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.omg.CORBA.UnionMemberHelper.id (), "UnionMember", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.omg.CORBA.UnionMember read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.CORBA.UnionMember value = new org.omg.CORBA.UnionMember ();
    value.name = istream.read_string ();
    value.label = istream.read_any ();
    value.type = istream.read_TypeCode ();
    value.type_def = org.omg.CORBA.IDLTypeHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.CORBA.UnionMember value)
  {
    ostream.write_string (value.name);
    ostream.write_any (value.label);
    ostream.write_TypeCode (value.type);
    org.omg.CORBA.IDLTypeHelper.write (ostream, value.type_def);
  }

}
