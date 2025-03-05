package java8.org.omg.IOP;


/**
* org/omg/IOP/ExceptionDetailMessage.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from d:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8/2238/corba/src/share/classes/org/omg/PortableInterceptor/IOP.idl
* Tuesday, March 4, 2014 3:47:38 AM PST
*/

public interface ExceptionDetailMessage
{

  /**
     * CORBA formal/02-06-01: 13.7.1:
     * ExceptionDetailMessage identifies a CDR encapsulation of a wstring,
     * encoded using GIOP 1.2 with a TCS-W of UTF-16.  This service context
     * may be sent on Reply messages with a reply_status of SYSTEM_EXCEPTION
     * or USER_EXCEPTION. The usage of this service context is defined
     * by language mappings. <br/> <br/>
     *
     * IDL/Java: ptc/02-01-22: 1.15.2: 
     * When a System Exception is marshaled, its GIOP Reply message shall
     * include an associated ExceptionDetailMessage service context. The
     * callee's stack trace is often very valuable debugging information but
     * may contain sensitive or unwanted information. The wstring within the
     * service context will therefore contain additional information relating
     * to the exception, for example the result of calling either
     * printStackTrace(PrintWriter) or getMessage() on the exception. When
     * unmarshaling a System Exception on the client side, the wstring from
     * any ExceptionDetailMessage service context shall become the Java error
     * message in the unmarshaled exception object.
     */
  public static final int value = (int)(14L);
}
