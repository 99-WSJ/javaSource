package java8.sun.corba.se.PortableActivationIDL;


/**
* com/sun/corba/se/PortableActivationIDL/ServerHeldDown.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from d:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8/2238/corba/src/share/classes/com/sun/corba/se/PortableActivationIDL/activation.idl
* Tuesday, March 4, 2014 3:47:38 AM PST
*/

public final class ServerHeldDown extends org.omg.CORBA.UserException
{
  public String serverId = null;

  public ServerHeldDown ()
  {
    super(ServerHeldDownHelper.id());
  } // ctor

  public ServerHeldDown (String _serverId)
  {
    super(ServerHeldDownHelper.id());
    serverId = _serverId;
  } // ctor


  public ServerHeldDown (String $reason, String _serverId)
  {
    super(ServerHeldDownHelper.id() + "  " + $reason);
    serverId = _serverId;
  } // ctor

} // class ServerHeldDown
