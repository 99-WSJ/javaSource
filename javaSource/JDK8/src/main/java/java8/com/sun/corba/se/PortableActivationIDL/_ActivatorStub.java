package java8.com.sun.corba.se.PortableActivationIDL;


/**
* com/sun/corba/se/PortableActivationIDL/_ActivatorStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from d:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8/2238/corba/src/share/classes/com/sun/corba/se/PortableActivationIDL/activation.idl
* Tuesday, March 4, 2014 3:47:38 AM PST
*/

public class _ActivatorStub extends org.omg.CORBA.portable.ObjectImpl implements com.sun.corba.se.PortableActivationIDL.Activator
{


  /** A new ORB started server registers itself with the Activator
	*/
  public void registerServer (String serverId, com.sun.corba.se.PortableActivationIDL.ServerProxy serverObj) throws com.sun.corba.se.PortableActivationIDL.ServerNotRegistered
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("registerServer", true);
                org.omg.PortableInterceptor.ServerIdHelper.write ($out, serverId);
                com.sun.corba.se.PortableActivationIDL.ServerProxyHelper.write ($out, serverObj);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:PortableActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.PortableActivationIDL.ServerNotRegisteredHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                registerServer (serverId, serverObj        );
            } finally {
                _releaseReply ($in);
            }
  } // registerServer


  /** A server is shutting down that was started by this activator.
	* Complete termination of the server is detected by the death of the
	* process implementing the server.
	*/
  public void serverGoingDown (String serverId)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("serverGoingDown", true);
                org.omg.PortableInterceptor.ServerIdHelper.write ($out, serverId);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                serverGoingDown (serverId        );
            } finally {
                _releaseReply ($in);
            }
  } // serverGoingDown


  /** Called whenever an ORB instance is created.  This registers
	* the transport endpoints and the ORB proxy callback object.
	* Note that we cannot detect when an ORB shuts down, although
	* all of the POA shutdowns should still be reported.
	*/
  public void registerORB (String serverId, String orbId, com.sun.corba.se.PortableActivationIDL.ORBProxy orb, com.sun.corba.se.PortableActivationIDL.EndPointInfo[] endPointInfo) throws com.sun.corba.se.PortableActivationIDL.ServerNotRegistered, com.sun.corba.se.PortableActivationIDL.NoSuchEndPoint, com.sun.corba.se.PortableActivationIDL.ORBAlreadyRegistered
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("registerORB", true);
                org.omg.PortableInterceptor.ServerIdHelper.write ($out, serverId);
                org.omg.PortableInterceptor.ORBIdHelper.write ($out, orbId);
                com.sun.corba.se.PortableActivationIDL.ORBProxyHelper.write ($out, orb);
                com.sun.corba.se.PortableActivationIDL.EndpointInfoListHelper.write ($out, endPointInfo);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:PortableActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.PortableActivationIDL.ServerNotRegisteredHelper.read ($in);
                else if (_id.equals ("IDL:PortableActivationIDL/NoSuchEndPoint:1.0"))
                    throw com.sun.corba.se.PortableActivationIDL.NoSuchEndPointHelper.read ($in);
                else if (_id.equals ("IDL:PortableActivationIDL/ORBAlreadyRegistered:1.0"))
                    throw com.sun.corba.se.PortableActivationIDL.ORBAlreadyRegisteredHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                registerORB (serverId, orbId, orb, endPointInfo        );
            } finally {
                _releaseReply ($in);
            }
  } // registerORB


  /** Construct or find an ORBD object template corresponding to the 
	* server's object template and return it.  Called whenever a 
	* persistent POA is created.
	*/
  public org.omg.PortableInterceptor.ObjectReferenceTemplate registerPOA (String serverId, String orbId, org.omg.PortableInterceptor.ObjectReferenceTemplate poaTemplate)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("registerPOA", true);
                org.omg.PortableInterceptor.ServerIdHelper.write ($out, serverId);
                org.omg.PortableInterceptor.ORBIdHelper.write ($out, orbId);
                org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.write ($out, poaTemplate);
                $in = _invoke ($out);
                org.omg.PortableInterceptor.ObjectReferenceTemplate $result = org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return registerPOA (serverId, orbId, poaTemplate        );
            } finally {
                _releaseReply ($in);
            }
  } // registerPOA


  /** Called whenever a POA is destroyed.
	*/
  public void poaDestroyed (String serverId, String orbId, org.omg.PortableInterceptor.ObjectReferenceTemplate poaTemplate)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("poaDestroyed", true);
                org.omg.PortableInterceptor.ServerIdHelper.write ($out, serverId);
                org.omg.PortableInterceptor.ORBIdHelper.write ($out, orbId);
                org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.write ($out, poaTemplate);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                poaDestroyed (serverId, orbId, poaTemplate        );
            } finally {
                _releaseReply ($in);
            }
  } // poaDestroyed


  /** If the server is not running, start it up.  This is allowed
	* whether or not the server has been installed.
	*/
  public void activate (String serverId) throws com.sun.corba.se.PortableActivationIDL.ServerAlreadyActive, com.sun.corba.se.PortableActivationIDL.ServerNotRegistered, com.sun.corba.se.PortableActivationIDL.ServerHeldDown
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("activate", true);
                org.omg.PortableInterceptor.ServerIdHelper.write ($out, serverId);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:PortableActivationIDL/ServerAlreadyActive:1.0"))
                    throw com.sun.corba.se.PortableActivationIDL.ServerAlreadyActiveHelper.read ($in);
                else if (_id.equals ("IDL:PortableActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.PortableActivationIDL.ServerNotRegisteredHelper.read ($in);
                else if (_id.equals ("IDL:PortableActivationIDL/ServerHeldDown:1.0"))
                    throw com.sun.corba.se.PortableActivationIDL.ServerHeldDownHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                activate (serverId        );
            } finally {
                _releaseReply ($in);
            }
  } // activate


  /** If the server is running, shut it down
	*/
  public void shutdown (String serverId) throws com.sun.corba.se.PortableActivationIDL.ServerNotActive, com.sun.corba.se.PortableActivationIDL.ServerNotRegistered
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("shutdown", true);
                org.omg.PortableInterceptor.ServerIdHelper.write ($out, serverId);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:PortableActivationIDL/ServerNotActive:1.0"))
                    throw com.sun.corba.se.PortableActivationIDL.ServerNotActiveHelper.read ($in);
                else if (_id.equals ("IDL:PortableActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.PortableActivationIDL.ServerNotRegisteredHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                shutdown (serverId        );
            } finally {
                _releaseReply ($in);
            }
  } // shutdown


  /** Invoke the server install hook.  If the server is not 
	* currently running, this method will activate it.
	*/
  public void install (String serverId) throws com.sun.corba.se.PortableActivationIDL.ServerNotRegistered, com.sun.corba.se.PortableActivationIDL.ServerHeldDown, com.sun.corba.se.PortableActivationIDL.ServerAlreadyInstalled
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("install", true);
                org.omg.PortableInterceptor.ServerIdHelper.write ($out, serverId);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:PortableActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.PortableActivationIDL.ServerNotRegisteredHelper.read ($in);
                else if (_id.equals ("IDL:PortableActivationIDL/ServerHeldDown:1.0"))
                    throw com.sun.corba.se.PortableActivationIDL.ServerHeldDownHelper.read ($in);
                else if (_id.equals ("IDL:PortableActivationIDL/ServerAlreadyInstalled:1.0"))
                    throw com.sun.corba.se.PortableActivationIDL.ServerAlreadyInstalledHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                install (serverId        );
            } finally {
                _releaseReply ($in);
            }
  } // install


  /** Invoke the server uninstall hook.  If the server is not
	* currently running, this method will activate it.
	* After this hook completes, the server may still be running.
	*/
  public void uninstall (String serverId) throws com.sun.corba.se.PortableActivationIDL.ServerNotRegistered, com.sun.corba.se.PortableActivationIDL.ServerHeldDown, com.sun.corba.se.PortableActivationIDL.ServerAlreadyUninstalled
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("uninstall", true);
                org.omg.PortableInterceptor.ServerIdHelper.write ($out, serverId);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:PortableActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.PortableActivationIDL.ServerNotRegisteredHelper.read ($in);
                else if (_id.equals ("IDL:PortableActivationIDL/ServerHeldDown:1.0"))
                    throw com.sun.corba.se.PortableActivationIDL.ServerHeldDownHelper.read ($in);
                else if (_id.equals ("IDL:PortableActivationIDL/ServerAlreadyUninstalled:1.0"))
                    throw com.sun.corba.se.PortableActivationIDL.ServerAlreadyUninstalledHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                uninstall (serverId        );
            } finally {
                _releaseReply ($in);
            }
  } // uninstall


  /** list active servers
	*/
  public String[] getActiveServers ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getActiveServers", true);
                $in = _invoke ($out);
                String $result[] = com.sun.corba.se.PortableActivationIDL.ServerIdsHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getActiveServers (        );
            } finally {
                _releaseReply ($in);
            }
  } // getActiveServers


  /** list all registered ORBs for a server
	*/
  public String[] getORBNames (String serverId) throws com.sun.corba.se.PortableActivationIDL.ServerNotRegistered
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getORBNames", true);
                org.omg.PortableInterceptor.ServerIdHelper.write ($out, serverId);
                $in = _invoke ($out);
                String $result[] = com.sun.corba.se.PortableActivationIDL.ORBidListHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:PortableActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.PortableActivationIDL.ServerNotRegisteredHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getORBNames (serverId        );
            } finally {
                _releaseReply ($in);
            }
  } // getORBNames


  /** Find the server template that corresponds to the ORBD's
	* adapter id.
	*/
  public org.omg.PortableInterceptor.ObjectReferenceTemplate lookupPOATemplate (String serverId, String orbId, String[] orbAdapterName)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("lookupPOATemplate", true);
                org.omg.PortableInterceptor.ServerIdHelper.write ($out, serverId);
                org.omg.PortableInterceptor.ORBIdHelper.write ($out, orbId);
                org.omg.PortableInterceptor.AdapterNameHelper.write ($out, orbAdapterName);
                $in = _invoke ($out);
                org.omg.PortableInterceptor.ObjectReferenceTemplate $result = org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return lookupPOATemplate (serverId, orbId, orbAdapterName        );
            } finally {
                _releaseReply ($in);
            }
  } // lookupPOATemplate

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:PortableActivationIDL/Activator:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _ActivatorStub
