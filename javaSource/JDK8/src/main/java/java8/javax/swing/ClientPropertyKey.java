/*
 * Copyright (c) 2006, 2012, Oracle and/or its affiliates. All rights reserved.
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

package java8.javax.swing;

import sun.awt.AWTAccessor;

/**
 * An enumeration for keys used as client properties within the Swing
 * implementation.
 * <p>
 * This enum holds only a small subset of the keys currently used within Swing,
 * but we may move more of them here in the future.
 * <p>
 * Adding an item to, and using, this class instead of {@code String} for
 * client properties protects against conflicts with developer-set client
 * properties. Using this class also avoids a problem with {@code StringBuilder}
 * and {@code StringBuffer} keys, whereby the keys are not recognized upon
 * deserialization.
 * <p>
 * When a client property value associated with one of these keys does not
 * implement {@code Serializable}, the result during serialization depends
 * on how the key is defined here. Historically, client properties with values
 * not implementing {@code Serializable} have simply been dropped and left out
 * of the serialized representation. To define keys with such behavior in this
 * enum, provide a value of {@code false} for the {@code reportValueNotSerializable}
 * property. When migrating existing properties to this enum, one may wish to
 * consider using this by default, to preserve backward compatibility.
 * <p>
 * To instead have a {@code NotSerializableException} thrown when a
 * {@code non-Serializable} property is encountered, provide the value of
 * {@code true} for the {@code reportValueNotSerializable} property. This
 * is useful when the property represents something that the developer
 * needs to know about when it cannot be serialized.
 *
 * @author  Shannon Hickey
 */
enum ClientPropertyKey {

    /**
     * Key used by JComponent for storing InputVerifier.
     */
    JComponent_INPUT_VERIFIER(true),

    /**
     * Key used by JComponent for storing TransferHandler.
     */
    JComponent_TRANSFER_HANDLER(true),

    /**
     * Key used by JComponent for storing AncestorNotifier.
     */
    JComponent_ANCESTOR_NOTIFIER(true),

    /**
     * Key used by PopupFactory to force heavy weight popups for a
     * component.
     */
    PopupFactory_FORCE_HEAVYWEIGHT_POPUP(true);


    /**
     * Whether or not a {@code NotSerializableException} should be thrown
     * during serialization, when the value associated with this key does
     * not implement {@code Serializable}.
     */
    private final boolean reportValueNotSerializable;

    static {
        AWTAccessor.setClientPropertyKeyAccessor(
            new AWTAccessor.ClientPropertyKeyAccessor() {
                public Object getJComponent_TRANSFER_HANDLER() {
                    return JComponent_TRANSFER_HANDLER;
                }
            });
    }

    /**
     * Constructs a key with the {@code reportValueNotSerializable} property
     * set to {@code false}.
     */
    private ClientPropertyKey() {
        this(false);
    }

    /**
     * Constructs a key with the {@code reportValueNotSerializable} property
     * set to the given value.
     */
    private ClientPropertyKey(boolean reportValueNotSerializable) {
        this.reportValueNotSerializable = reportValueNotSerializable;
    }

    /**
     * Returns whether or not a {@code NotSerializableException} should be thrown
     * during serialization, when the value associated with this key does
     * not implement {@code Serializable}.
     */
    public boolean getReportValueNotSerializable() {
        return reportValueNotSerializable;
    }
}
