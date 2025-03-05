package java8.org.omg.PortableInterceptor;


import org.omg.PortableInterceptor.LOCATION_FORWARD;
import org.omg.PortableInterceptor.RequestInfo;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.omg.PortableInterceptor.TRANSPORT_RETRY;
import org.omg.PortableInterceptor.USER_EXCEPTION;

/**
* org/omg/PortableInterceptor/SUCCESSFUL.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from d:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8/2238/corba/src/share/classes/org/omg/PortableInterceptor/Interceptors.idl
* Tuesday, March 4, 2014 3:47:39 AM PST
*/

public interface SUCCESSFUL
{

  /**
   * Indicates a successful Reply Status. One possible value for 
   * <code>RequestInfo.reply_status</code>.
   * @see RequestInfo#reply_status
   * @see SYSTEM_EXCEPTION
   * @see USER_EXCEPTION
   * @see LOCATION_FORWARD
   * @see TRANSPORT_RETRY
   */
  public static final short value = (short)(0);
}
