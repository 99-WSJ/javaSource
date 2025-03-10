/*
 * Copyright (c) 2000, 2001, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java8.org.ietf.jgss;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSException;

import java.net.InetAddress;
import java.util.Arrays;

/**
 * This class encapsulates the concept of caller-provided channel
 * binding information. Channel bindings are used to strengthen the
 * quality with which peer entity authentication is provided during
 * context establishment.  They enable the GSS-API callers to bind the
 * establishment of the security context to relevant characteristics
 * like addresses or to application specific data.<p>
 *
 * The caller initiating the security context must determine the
 * appropriate channel binding values to set in the GSSContext object.
 * The acceptor must provide an identical binding in order to validate
 * that received tokens possess correct channel-related characteristics.<p>
 *
 * Use of channel bindings is optional in GSS-API. ChannelBinding can be
 * set for the {@link GSSContext GSSContext} using the {@link
 * GSSContext#setChannelBinding(ChannelBinding) setChannelBinding} method
 * before the first call to {@link GSSContext#initSecContext(byte[], int, int)
 * initSecContext} or {@link GSSContext#acceptSecContext(byte[], int, int)
 * acceptSecContext} has been performed.  Unless the <code>setChannelBinding</code>
 * method has been used to set the ChannelBinding for a GSSContext object,
 * <code>null</code> ChannelBinding will be assumed. <p>
 *
 * Conceptually, the GSS-API concatenates the initiator and acceptor
 * address information, and the application supplied byte array to form an
 * octet string.  The mechanism calculates a MIC over this octet string and
 * binds the MIC to the context establishment token emitted by
 * <code>initSecContext</code> method of the <code>GSSContext</code>
 * interface.  The same bindings are set by the context acceptor for its
 * <code>GSSContext</code> object and during processing of the
 * <code>acceptSecContext</code> method a MIC is calculated in the same
 * way. The calculated MIC is compared with that found in the token, and if
 * the MICs differ, accept will throw a <code>GSSException</code> with the
 * major code set to {@link GSSException#BAD_BINDINGS BAD_BINDINGS}, and
 * the context will not be established. Some mechanisms may include the
 * actual channel binding data in the token (rather than just a MIC);
 * applications should therefore not use confidential data as
 * channel-binding components.<p>
 *
 *  Individual mechanisms may impose additional constraints on addresses
 *  that may appear in channel bindings.  For example, a mechanism may
 *  verify that the initiator address field of the channel binding
 *  contains the correct network address of the host system.  Portable
 *  applications should therefore ensure that they either provide correct
 *  information for the address fields, or omit setting of the addressing
 *  information.
 *
 * @author Mayank Upadhyay
 * @since 1.4
 */
public class ChannelBinding {

    private InetAddress initiator;
    private InetAddress acceptor;
    private  byte[] appData;

    /**
     * Create a ChannelBinding object with user supplied address information
     * and data.  <code>null</code> values can be used for any fields which the
     * application does not want to specify.
     *
     * @param initAddr the address of the context initiator.
     * <code>null</code> value can be supplied to indicate that the
     * application does not want to set this value.
     * @param acceptAddr the address of the context
     * acceptor. <code>null</code> value can be supplied to indicate that
     * the application does not want to set this value.
     * @param appData application supplied data to be used as part of the
     * channel bindings. <code>null</code> value can be supplied to
     * indicate that the application does not want to set this value.
     */
    public ChannelBinding(InetAddress initAddr, InetAddress acceptAddr,
                        byte[] appData) {

        initiator = initAddr;
        acceptor = acceptAddr;

        if (appData != null) {
            this.appData = new byte[appData.length];
            System.arraycopy(appData, 0, this.appData, 0,
                                appData.length);
        }
    }

    /**
     * Creates a ChannelBinding object without any addressing information.
     *
     * @param appData application supplied data to be used as part of the
     * channel bindings.
     */
    public ChannelBinding(byte[] appData) {
        this(null, null, appData);
    }

    /**
     * Get the initiator's address for this channel binding.
     *
     * @return the initiator's address. <code>null</code> is returned if
     * the address has not been set.
     */
    public InetAddress getInitiatorAddress() {
        return initiator;
    }

    /**
     * Get the acceptor's address for this channel binding.
     *
     * @return the acceptor's address. null is returned if the address has
     * not been set.
     */
    public InetAddress getAcceptorAddress() {
        return acceptor;
    }

    /**
     * Get the application specified data for this channel binding.
     *
     * @return the application data being used as part of the
     * ChannelBinding. <code>null</code> is returned if no application data
     * has been specified for the channel binding.
     */
    public byte[] getApplicationData() {

        if (appData == null) {
            return null;
        }

        byte[] retVal = new byte[appData.length];
        System.arraycopy(appData, 0, retVal, 0, appData.length);
        return retVal;
    }

    /**
     * Compares two instances of ChannelBinding.
     *
     * @param obj another ChannelBinding to compare this one with
     * @return true if the two ChannelBinding's contain
     * the same values for the initiator and acceptor addresses and the
     * application data.
     */
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (! (obj instanceof ChannelBinding))
            return false;

        ChannelBinding cb = (ChannelBinding) obj;

        if ((initiator != null && cb.initiator == null) ||
            (initiator == null && cb.initiator != null))
            return false;

        if (initiator != null && !initiator.equals(cb.initiator))
            return false;

        if ((acceptor != null && cb.acceptor == null) ||
            (acceptor == null && cb.acceptor != null))
            return false;

        if (acceptor != null && !acceptor.equals(cb.acceptor))
            return false;

        return Arrays.equals(appData, cb.appData);
    }

    /**
     * Returns a hashcode value for this ChannelBinding object.
     *
     * @return a hashCode value
     */
    public int hashCode() {
        if (initiator != null)
            return initiator.hashCode();
        else if (acceptor != null)
            return acceptor.hashCode();
        else if (appData != null)
            return new String(appData).hashCode();
        else
            return 1;
    }
}
