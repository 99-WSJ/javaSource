package java8.org.omg.PortableServer.POAManagerPackage;


import org.omg.PortableServer.POAManagerPackage.AdapterInactiveHelper;

/**
* org/omg/PortableServer/POAManagerPackage/AdapterInactive.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from d:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8/2238/corba/src/share/classes/org/omg/PortableServer/poa.idl
* Tuesday, March 4, 2014 3:47:39 AM PST
*/

public final class AdapterInactive extends org.omg.CORBA.UserException
{

  public AdapterInactive ()
  {
    super(org.omg.PortableServer.POAManagerPackage.AdapterInactiveHelper.id());
  } // ctor


  public AdapterInactive (String $reason)
  {
    super(AdapterInactiveHelper.id() + "  " + $reason);
  } // ctor

} // class AdapterInactive
