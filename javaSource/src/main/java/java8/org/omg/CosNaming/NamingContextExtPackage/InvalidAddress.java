package java8.org.omg.CosNaming.NamingContextExtPackage;


import org.omg.CosNaming.NamingContextExtPackage.InvalidAddressHelper;

/**
* org/omg/CosNaming/NamingContextExtPackage/InvalidAddress.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from d:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8/2238/corba/src/share/classes/org/omg/CosNaming/nameservice.idl
* Tuesday, March 4, 2014 3:47:38 AM PST
*/

public final class InvalidAddress extends org.omg.CORBA.UserException
{

  public InvalidAddress ()
  {
    super(org.omg.CosNaming.NamingContextExtPackage.InvalidAddressHelper.id());
  } // ctor


  public InvalidAddress (String $reason)
  {
    super(InvalidAddressHelper.id() + "  " + $reason);
  } // ctor

} // class InvalidAddress
