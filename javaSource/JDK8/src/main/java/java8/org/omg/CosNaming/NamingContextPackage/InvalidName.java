package java8.org.omg.CosNaming.NamingContextPackage;


import org.omg.CosNaming.NamingContextPackage.InvalidNameHelper;

/**
* org/omg/CosNaming/NamingContextPackage/InvalidName.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from d:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8/2238/corba/src/share/classes/org/omg/CosNaming/nameservice.idl
* Tuesday, March 4, 2014 3:47:38 AM PST
*/

public final class InvalidName extends org.omg.CORBA.UserException
{

  public InvalidName ()
  {
    super(InvalidNameHelper.id());
  } // ctor


  public InvalidName (String $reason)
  {
    super(InvalidNameHelper.id() + "  " + $reason);
  } // ctor

} // class InvalidName
