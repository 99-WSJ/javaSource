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
* The Helper for <tt>Current</tt>.  For more information on
* Helper files, see <a href="doc-files/generatedfiles.html#helper">
* "Generated Files: Helper Files"</a>.<P>
* org/omg/CORBA/CurrentHelper.java
* Generated by the IDL-to-Java compiler (portable), version "3.0"
* from ../../../../../src/share/classes/org/omg/PortableServer/corba.idl
* Saturday, July 17, 1999 12:26:21 AM PDT
*/

abstract public class CurrentHelper
{
  private static String  _id = "IDL:omg.org/CORBA/Current:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.CORBA.Current that)
  {
    throw new org.omg.CORBA.MARSHAL() ;
  }

  public static org.omg.CORBA.Current extract (org.omg.CORBA.Any a)
  {
    throw new org.omg.CORBA.MARSHAL() ;
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (org.omg.CORBA.CurrentHelper.id (), "Current");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.omg.CORBA.Current read (org.omg.CORBA.portable.InputStream istream)
  {
    throw new org.omg.CORBA.MARSHAL() ;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.CORBA.Current value)
  {
    throw new org.omg.CORBA.MARSHAL() ;
  }

  public static org.omg.CORBA.Current narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof org.omg.CORBA.Current)
      return (org.omg.CORBA.Current)obj;
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

}
