package java8.sun.corba.se.spi.activation;


import com.sun.corba.se.spi.activation._LocatorStub;

/**
* com/sun/corba/se/spi/activation/LocatorHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from d:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8/2238/corba/src/share/classes/com/sun/corba/se/spi/activation/activation.idl
* Tuesday, March 4, 2014 3:47:38 AM PST
*/

abstract public class LocatorHelper
{
  private static String  _id = "IDL:activation/Locator:1.0";

  public static void insert (org.omg.CORBA.Any a, com.sun.corba.se.spi.activation.Locator that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static com.sun.corba.se.spi.activation.Locator extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (com.sun.corba.se.spi.activation.LocatorHelper.id (), "Locator");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static com.sun.corba.se.spi.activation.Locator read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_LocatorStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, com.sun.corba.se.spi.activation.Locator value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static com.sun.corba.se.spi.activation.Locator narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof com.sun.corba.se.spi.activation.Locator)
      return (com.sun.corba.se.spi.activation.Locator)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      _LocatorStub stub = new _LocatorStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static com.sun.corba.se.spi.activation.Locator unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof com.sun.corba.se.spi.activation.Locator)
      return (com.sun.corba.se.spi.activation.Locator)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      _LocatorStub stub = new _LocatorStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
