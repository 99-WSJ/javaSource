package java8.org.omg.PortableServer.POAPackage;


import org.omg.PortableServer.POAPackage.ObjectNotActiveHelper;

/**
* org/omg/PortableServer/POAPackage/ObjectNotActive.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from d:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8/2238/corba/src/share/classes/org/omg/PortableServer/poa.idl
* Tuesday, March 4, 2014 3:47:39 AM PST
*/

public final class ObjectNotActive extends org.omg.CORBA.UserException
{

  public ObjectNotActive ()
  {
    super(ObjectNotActiveHelper.id());
  } // ctor


  public ObjectNotActive (String $reason)
  {
    super(ObjectNotActiveHelper.id() + "  " + $reason);
  } // ctor

} // class ObjectNotActive
