/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
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

package java8.java.security;

import sun.security.util.*;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * An attribute associated with a PKCS12 keystore entry.
 * The attribute name is an ASN.1 Object Identifier and the attribute
 * value is a set of ASN.1 types.
 *
 * @since 1.8
 */
public final class PKCS12Attribute implements KeyStore.Entry.Attribute {

    private static final Pattern COLON_SEPARATED_HEX_PAIRS =
        Pattern.compile("^[0-9a-fA-F]{2}(:[0-9a-fA-F]{2})+$");
    private String name;
    private String value;
    private byte[] encoded;
    private int hashValue = -1;

    /**
     * Constructs a PKCS12 attribute from its name and value.
     * The name is an ASN.1 Object Identifier represented as a list of
     * dot-separated integers.
     * A string value is represented as the string itself.
     * A binary value is represented as a string of colon-separated
     * pairs of hexadecimal digits.
     * Multi-valued attributes are represented as a comma-separated
     * list of values, enclosed in square brackets. See
     * {@link Arrays#toString(Object[])}.
     * <p>
     * A string value will be DER-encoded as an ASN.1 UTF8String and a
     * binary value will be DER-encoded as an ASN.1 Octet String.
     *
     * @param name the attribute's identifier
     * @param value the attribute's value
     *
     * @exception NullPointerException if {@code name} or {@code value}
     *     is {@code null}
     * @exception IllegalArgumentException if {@code name} or
     *     {@code value} is incorrectly formatted
     */
    public PKCS12Attribute(String name, String value) {
        if (name == null || value == null) {
            throw new NullPointerException();
        }
        // Validate name
        ObjectIdentifier type;
        try {
            type = new ObjectIdentifier(name);
        } catch (IOException e) {
            throw new IllegalArgumentException("Incorrect format: name", e);
        }
        this.name = name;

        // Validate value
        int length = value.length();
        String[] values;
        if (value.charAt(0) == '[' && value.charAt(length - 1) == ']') {
            values = value.substring(1, length - 1).split(", ");
        } else {
            values = new String[]{ value };
        }
        this.value = value;

        try {
            this.encoded = encode(type, values);
        } catch (IOException e) {
            throw new IllegalArgumentException("Incorrect format: value", e);
        }
    }

    /**
     * Constructs a PKCS12 attribute from its ASN.1 DER encoding.
     * The DER encoding is specified by the following ASN.1 definition:
     * <pre>
     *
     * Attribute ::= SEQUENCE {
     *     type   AttributeType,
     *     values SET OF AttributeValue
     * }
     * AttributeType ::= OBJECT IDENTIFIER
     * AttributeValue ::= ANY defined by type
     *
     * </pre>
     *
     * @param encoded the attribute's ASN.1 DER encoding. It is cloned
     *     to prevent subsequent modificaion.
     *
     * @exception NullPointerException if {@code encoded} is
     *     {@code null}
     * @exception IllegalArgumentException if {@code encoded} is
     *     incorrectly formatted
     */
    public PKCS12Attribute(byte[] encoded) {
        if (encoded == null) {
            throw new NullPointerException();
        }
        this.encoded = encoded.clone();

        try {
            parse(encoded);
        } catch (IOException e) {
            throw new IllegalArgumentException("Incorrect format: encoded", e);
        }
    }

    /**
     * Returns the attribute's ASN.1 Object Identifier represented as a
     * list of dot-separated integers.
     *
     * @return the attribute's identifier
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the attribute's ASN.1 DER-encoded value as a string.
     * An ASN.1 DER-encoded value is returned in one of the following
     * {@code String} formats:
     * <ul>
     * <li> the DER encoding of a basic ASN.1 type that has a natural
     *      string representation is returned as the string itself.
     *      Such types are currently limited to BOOLEAN, INTEGER,
     *      OBJECT IDENTIFIER, UTCTime, GeneralizedTime and the
     *      following six ASN.1 string types: UTF8String,
     *      PrintableString, T61String, IA5String, BMPString and
     *      GeneralString.
     * <li> the DER encoding of any other ASN.1 type is not decoded but
     *      returned as a binary string of colon-separated pairs of
     *      hexadecimal digits.
     * </ul>
     * Multi-valued attributes are represented as a comma-separated
     * list of values, enclosed in square brackets. See
     * {@link Arrays#toString(Object[])}.
     *
     * @return the attribute value's string encoding
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * Returns the attribute's ASN.1 DER encoding.
     *
     * @return a clone of the attribute's DER encoding
     */
    public byte[] getEncoded() {
        return encoded.clone();
    }

    /**
     * Compares this {@code PKCS12Attribute} and a specified object for
     * equality.
     *
     * @param obj the comparison object
     *
     * @return true if {@code obj} is a {@code PKCS12Attribute} and
     * their DER encodings are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof java.security.PKCS12Attribute)) {
            return false;
        }
        return Arrays.equals(encoded, ((java.security.PKCS12Attribute) obj).getEncoded());
    }

    /**
     * Returns the hashcode for this {@code PKCS12Attribute}.
     * The hash code is computed from its DER encoding.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        if (hashValue == -1) {
            Arrays.hashCode(encoded);
        }
        return hashValue;
    }

    /**
     * Returns a string representation of this {@code PKCS12Attribute}.
     *
     * @return a name/value pair separated by an 'equals' symbol
     */
    @Override
    public String toString() {
        return (name + "=" + value);
    }

    private byte[] encode(ObjectIdentifier type, String[] values)
            throws IOException {
        DerOutputStream attribute = new DerOutputStream();
        attribute.putOID(type);
        DerOutputStream attrContent = new DerOutputStream();
        for (String value : values) {
            if (COLON_SEPARATED_HEX_PAIRS.matcher(value).matches()) {
                byte[] bytes =
                    new BigInteger(value.replace(":", ""), 16).toByteArray();
                if (bytes[0] == 0) {
                    bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
                }
                attrContent.putOctetString(bytes);
            } else {
                attrContent.putUTF8String(value);
            }
        }
        attribute.write(DerValue.tag_Set, attrContent);
        DerOutputStream attributeValue = new DerOutputStream();
        attributeValue.write(DerValue.tag_Sequence, attribute);

        return attributeValue.toByteArray();
    }

    private void parse(byte[] encoded) throws IOException {
        DerInputStream attributeValue = new DerInputStream(encoded);
        DerValue[] attrSeq = attributeValue.getSequence(2);
        ObjectIdentifier type = attrSeq[0].getOID();
        DerInputStream attrContent =
            new DerInputStream(attrSeq[1].toByteArray());
        DerValue[] attrValueSet = attrContent.getSet(1);
        String[] values = new String[attrValueSet.length];
        String printableString;
        for (int i = 0; i < attrValueSet.length; i++) {
            if (attrValueSet[i].tag == DerValue.tag_OctetString) {
                values[i] = Debug.toString(attrValueSet[i].getOctetString());
            } else if ((printableString = attrValueSet[i].getAsString())
                != null) {
                values[i] = printableString;
            } else if (attrValueSet[i].tag == DerValue.tag_ObjectId) {
                values[i] = attrValueSet[i].getOID().toString();
            } else if (attrValueSet[i].tag == DerValue.tag_GeneralizedTime) {
                values[i] = attrValueSet[i].getGeneralizedTime().toString();
            } else if (attrValueSet[i].tag == DerValue.tag_UtcTime) {
                values[i] = attrValueSet[i].getUTCTime().toString();
            } else if (attrValueSet[i].tag == DerValue.tag_Integer) {
                values[i] = attrValueSet[i].getBigInteger().toString();
            } else if (attrValueSet[i].tag == DerValue.tag_Boolean) {
                values[i] = String.valueOf(attrValueSet[i].getBoolean());
            } else {
                values[i] = Debug.toString(attrValueSet[i].getDataBytes());
            }
        }

        this.name = type.toString();
        this.value = values.length == 1 ? values[0] : Arrays.toString(values);
    }
}
