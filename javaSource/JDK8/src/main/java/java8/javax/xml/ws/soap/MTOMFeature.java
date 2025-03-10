/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.xml.ws.soap;

import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

/**
 * This feature represents the use of MTOM with a
 * web service.
 *
 * This feature can be used during the creation of SEI proxy, and
 * {@link javax.xml.ws.Dispatch} instances on the client side and {@link Endpoint}
 * instances on the server side. This feature cannot be used for {@link Service}
 * instance creation on the client side.
 *
 * <p>
 * The following describes the affects of this feature with respect
 * to being enabled or disabled:
 * <ul>
 *  <li> ENABLED: In this Mode, MTOM will be enabled. A receiver MUST accept
 * both a non-optimized and an optimized message, and a sender MAY send an
 * optimized message, or a non-optimized message. The heuristics used by a
 * sender to determine whether to use optimization or not are
 * implementation-specific.
 *  <li> DISABLED: In this Mode, MTOM will be disabled
 * </ul>
 * <p>
 * The {@link #threshold} property can be used to set the threshold
 * value used to determine when binary data should be XOP encoded.
 *
 * @since JAX-WS 2.1
 */
public final class MTOMFeature extends WebServiceFeature {
    /**
     * Constant value identifying the MTOMFeature
     */
    public static final String ID = "http://www.w3.org/2004/08/soap/features/http-optimization";


    /**
     * Property for MTOM threshold value. This property serves as a hint when
     * MTOM is enabled, binary data above this size in bytes SHOULD be sent
     * as attachment.
     * The value of this property MUST always be >= 0. Default value is 0.
     */
    // should be changed to private final, keeping original modifier to keep backwards compatibility
    protected int threshold;


    /**
     * Create an <code>MTOMFeature</code>.
     * The instance created will be enabled.
     */
    public MTOMFeature() {
        this.enabled = true;
        this.threshold = 0;
    }

    /**
     * Creates an <code>MTOMFeature</code>.
     *
     * @param enabled specifies if this feature should be enabled or not
     */
    public MTOMFeature(boolean enabled) {
        this.enabled = enabled;
        this.threshold = 0;
    }


    /**
     * Creates an <code>MTOMFeature</code>.
     * The instance created will be enabled.
     *
     * @param threshold the size in bytes that binary data SHOULD be before
     * being sent as an attachment.
     *
     * @throws WebServiceException if threshold is < 0
     */
    public MTOMFeature(int threshold) {
        if (threshold < 0)
            throw new WebServiceException("MTOMFeature.threshold must be >= 0, actual value: "+threshold);
        this.enabled = true;
        this.threshold = threshold;
    }

    /**
     * Creates an <code>MTOMFeature</code>.
     *
     * @param enabled specifies if this feature should be enabled or not
     * @param threshold the size in bytes that binary data SHOULD be before
     * being sent as an attachment.
     *
     * @throws WebServiceException if threshold is < 0
     */
    public MTOMFeature(boolean enabled, int threshold) {
        if (threshold < 0)
            throw new WebServiceException("MTOMFeature.threshold must be >= 0, actual value: "+threshold);
        this.enabled = enabled;
        this.threshold = threshold;
    }

    /**
     * {@inheritDoc}
     */
    public String getID() {
        return ID;
    }

    /**
     * Gets the threshold value used to determine when binary data
     * should be sent as an attachment.
     *
     * @return the current threshold size in bytes
     */
    public int getThreshold() {
        return threshold;
    }
}
