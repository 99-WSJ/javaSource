package java8.org.omg.DynamicAny.DynAnyPackage;


import org.omg.DynamicAny.DynAnyPackage.InvalidValueHelper;

/**
* org/omg/DynamicAny/DynAnyPackage/InvalidValue.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from d:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8/2238/corba/src/share/classes/org/omg/DynamicAny/DynamicAny.idl
* Tuesday, March 4, 2014 3:47:38 AM PST
*/

public final class InvalidValue extends org.omg.CORBA.UserException
{

  public InvalidValue ()
  {
    super(org.omg.DynamicAny.DynAnyPackage.InvalidValueHelper.id());
  } // ctor


  public InvalidValue (String $reason)
  {
    super(InvalidValueHelper.id() + "  " + $reason);
  } // ctor

} // class InvalidValue
