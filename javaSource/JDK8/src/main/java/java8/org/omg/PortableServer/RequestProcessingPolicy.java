package java8.org.omg.PortableServer;


/**
* org/omg/PortableServer/RequestProcessingPolicy.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from d:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8/2238/corba/src/share/classes/org/omg/PortableServer/poa.idl
* Tuesday, March 4, 2014 3:47:39 AM PST
*/


import org.omg.PortableServer.RequestProcessingPolicyOperations;

/**
	 * This policy specifies how requests are processed by 
	 * the created POA.  The default is 
	 * USE_ACTIVE_OBJECT_MAP_ONLY.
	 */
public interface RequestProcessingPolicy extends RequestProcessingPolicyOperations, org.omg.CORBA.Policy, org.omg.CORBA.portable.IDLEntity
{
} // interface RequestProcessingPolicy
