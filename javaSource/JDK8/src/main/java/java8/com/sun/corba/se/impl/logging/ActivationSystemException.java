// Log wrapper class for Sun private system exceptions in group ACTIVATION
//
// Generated by MC.java version 1.0, DO NOT EDIT BY HAND!
// Generated from input file d:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8/2238/corba/src/share/classes/com/sun/corba/se/spi/logging/data/Activation.mc on Tue Mar 04 03:47:28 PST 2014

package java8.sun.corba.se.impl.logging;

import com.sun.corba.se.impl.util.SUNVMCID;
import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.OBJECT_NOT_EXIST;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ActivationSystemException extends LogWrapperBase {
    
    public ActivationSystemException( Logger logger )
    {
        super( logger ) ;
    }
    
    private static LogWrapperFactory factory = new LogWrapperFactory() {
        public LogWrapperBase create( Logger logger )
        {
            return new com.sun.corba.se.impl.logging.ActivationSystemException( logger ) ;
        }
    } ;
    
    public static com.sun.corba.se.impl.logging.ActivationSystemException get(ORB orb, String logDomain )
    {
        com.sun.corba.se.impl.logging.ActivationSystemException wrapper =
            (com.sun.corba.se.impl.logging.ActivationSystemException) orb.getLogWrapper( logDomain,
                "ACTIVATION", factory ) ;
        return wrapper ;
    } 
    
    public static com.sun.corba.se.impl.logging.ActivationSystemException get(String logDomain )
    {
        com.sun.corba.se.impl.logging.ActivationSystemException wrapper =
            (com.sun.corba.se.impl.logging.ActivationSystemException) ORB.staticGetLogWrapper( logDomain,
                "ACTIVATION", factory ) ;
        return wrapper ;
    } 
    
    ///////////////////////////////////////////////////////////
    // INITIALIZE
    ///////////////////////////////////////////////////////////
    
    public static final int CANNOT_READ_REPOSITORY_DB = SUNVMCID.value + 401 ;
    
    public INITIALIZE cannotReadRepositoryDb( CompletionStatus cs, Throwable t ) {
        INITIALIZE exc = new INITIALIZE( CANNOT_READ_REPOSITORY_DB, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "ACTIVATION.cannotReadRepositoryDb",
                parameters, com.sun.corba.se.impl.logging.ActivationSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INITIALIZE cannotReadRepositoryDb( CompletionStatus cs ) {
        return cannotReadRepositoryDb( cs, null  ) ;
    }
    
    public INITIALIZE cannotReadRepositoryDb( Throwable t ) {
        return cannotReadRepositoryDb( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INITIALIZE cannotReadRepositoryDb(  ) {
        return cannotReadRepositoryDb( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int CANNOT_ADD_INITIAL_NAMING = SUNVMCID.value + 402 ;
    
    public INITIALIZE cannotAddInitialNaming( CompletionStatus cs, Throwable t ) {
        INITIALIZE exc = new INITIALIZE( CANNOT_ADD_INITIAL_NAMING, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "ACTIVATION.cannotAddInitialNaming",
                parameters, com.sun.corba.se.impl.logging.ActivationSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INITIALIZE cannotAddInitialNaming( CompletionStatus cs ) {
        return cannotAddInitialNaming( cs, null  ) ;
    }
    
    public INITIALIZE cannotAddInitialNaming( Throwable t ) {
        return cannotAddInitialNaming( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INITIALIZE cannotAddInitialNaming(  ) {
        return cannotAddInitialNaming( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    ///////////////////////////////////////////////////////////
    // INTERNAL
    ///////////////////////////////////////////////////////////
    
    public static final int CANNOT_WRITE_REPOSITORY_DB = SUNVMCID.value + 401 ;
    
    public INTERNAL cannotWriteRepositoryDb( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( CANNOT_WRITE_REPOSITORY_DB, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "ACTIVATION.cannotWriteRepositoryDb",
                parameters, com.sun.corba.se.impl.logging.ActivationSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL cannotWriteRepositoryDb( CompletionStatus cs ) {
        return cannotWriteRepositoryDb( cs, null  ) ;
    }
    
    public INTERNAL cannotWriteRepositoryDb( Throwable t ) {
        return cannotWriteRepositoryDb( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL cannotWriteRepositoryDb(  ) {
        return cannotWriteRepositoryDb( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int SERVER_NOT_EXPECTED_TO_REGISTER = SUNVMCID.value + 403 ;
    
    public INTERNAL serverNotExpectedToRegister( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( SERVER_NOT_EXPECTED_TO_REGISTER, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "ACTIVATION.serverNotExpectedToRegister",
                parameters, com.sun.corba.se.impl.logging.ActivationSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL serverNotExpectedToRegister( CompletionStatus cs ) {
        return serverNotExpectedToRegister( cs, null  ) ;
    }
    
    public INTERNAL serverNotExpectedToRegister( Throwable t ) {
        return serverNotExpectedToRegister( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL serverNotExpectedToRegister(  ) {
        return serverNotExpectedToRegister( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int UNABLE_TO_START_PROCESS = SUNVMCID.value + 404 ;
    
    public INTERNAL unableToStartProcess( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( UNABLE_TO_START_PROCESS, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "ACTIVATION.unableToStartProcess",
                parameters, com.sun.corba.se.impl.logging.ActivationSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL unableToStartProcess( CompletionStatus cs ) {
        return unableToStartProcess( cs, null  ) ;
    }
    
    public INTERNAL unableToStartProcess( Throwable t ) {
        return unableToStartProcess( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL unableToStartProcess(  ) {
        return unableToStartProcess( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    public static final int SERVER_NOT_RUNNING = SUNVMCID.value + 406 ;
    
    public INTERNAL serverNotRunning( CompletionStatus cs, Throwable t ) {
        INTERNAL exc = new INTERNAL( SERVER_NOT_RUNNING, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "ACTIVATION.serverNotRunning",
                parameters, com.sun.corba.se.impl.logging.ActivationSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public INTERNAL serverNotRunning( CompletionStatus cs ) {
        return serverNotRunning( cs, null  ) ;
    }
    
    public INTERNAL serverNotRunning( Throwable t ) {
        return serverNotRunning( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public INTERNAL serverNotRunning(  ) {
        return serverNotRunning( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    ///////////////////////////////////////////////////////////
    // OBJECT_NOT_EXIST
    ///////////////////////////////////////////////////////////
    
    public static final int ERROR_IN_BAD_SERVER_ID_HANDLER = SUNVMCID.value + 401 ;
    
    public OBJECT_NOT_EXIST errorInBadServerIdHandler( CompletionStatus cs, Throwable t ) {
        OBJECT_NOT_EXIST exc = new OBJECT_NOT_EXIST( ERROR_IN_BAD_SERVER_ID_HANDLER, cs ) ;
        if (t != null)
            exc.initCause( t ) ;
        
        if (logger.isLoggable( Level.WARNING )) {
            Object[] parameters = null ;
            doLog( Level.WARNING, "ACTIVATION.errorInBadServerIdHandler",
                parameters, com.sun.corba.se.impl.logging.ActivationSystemException.class, exc ) ;
        }
        
        return exc ;
    }
    
    public OBJECT_NOT_EXIST errorInBadServerIdHandler( CompletionStatus cs ) {
        return errorInBadServerIdHandler( cs, null  ) ;
    }
    
    public OBJECT_NOT_EXIST errorInBadServerIdHandler( Throwable t ) {
        return errorInBadServerIdHandler( CompletionStatus.COMPLETED_NO, t  ) ;
    }
    
    public OBJECT_NOT_EXIST errorInBadServerIdHandler(  ) {
        return errorInBadServerIdHandler( CompletionStatus.COMPLETED_NO, null  ) ;
    }
    
    
}
