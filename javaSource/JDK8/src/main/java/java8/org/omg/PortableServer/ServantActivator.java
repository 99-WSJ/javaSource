package java8.org.omg.PortableServer;


/**
* org/omg/PortableServer/ServantActivator.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from d:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8/2238/corba/src/share/classes/org/omg/PortableServer/poa.idl
* Tuesday, March 4, 2014 3:47:39 AM PST
*/


import org.omg.PortableServer.ServantActivatorOperations;

/**
	 * When the POA has the RETAIN policy it uses servant 
	 * managers that are ServantActivators. 
	 */
public interface ServantActivator extends ServantActivatorOperations, org.omg.PortableServer.ServantManager, org.omg.CORBA.portable.IDLEntity
{
} // interface ServantActivator
