/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
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
package java8.java.util;

import java.util.NoSuchElementException;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/**
 * A container object which may or may not contain a {@code double} value.
 * If a value is present, {@code isPresent()} will return {@code true} and
 * {@code getAsDouble()} will return the value.
 *
 * <p>Additional methods that depend on the presence or absence of a contained
 * value are provided, such as {@link #orElse(double) orElse()}
 * (return a default value if value not present) and
 * {@link #ifPresent(DoubleConsumer) ifPresent()} (execute a block
 * of code if the value is present).
 *
 * <p>This is a <a href="../lang/doc-files/ValueBased.html">value-based</a>
 * class; use of identity-sensitive operations (including reference equality
 * ({@code ==}), identity hash code, or synchronization) on instances of
 * {@code OptionalDouble} may have unpredictable results and should be avoided.
 *
 * @since 1.8
 */
public final class OptionalDouble {
    /**
     * Common instance for {@code empty()}.
     */
    private static final java.util.OptionalDouble EMPTY = new java.util.OptionalDouble();

    /**
     * If true then the value is present, otherwise indicates no value is present
     */
    private final boolean isPresent;
    private final double value;

    /**
     * Construct an empty instance.
     *
     * @implNote generally only one empty instance, {@link java.util.OptionalDouble#EMPTY},
     * should exist per VM.
     */
    private OptionalDouble() {
        this.isPresent = false;
        this.value = Double.NaN;
    }

    /**
     * Returns an empty {@code OptionalDouble} instance.  No value is present for this
     * OptionalDouble.
     *
     * @apiNote Though it may be tempting to do so, avoid testing if an object
     * is empty by comparing with {@code ==} against instances returned by
     * {@code Option.empty()}. There is no guarantee that it is a singleton.
     * Instead, use {@link #isPresent()}.
     *
     *  @return an empty {@code OptionalDouble}.
     */
    public static java.util.OptionalDouble empty() {
        return EMPTY;
    }

    /**
     * Construct an instance with the value present.
     *
     * @param value the double value to be present.
     */
    private OptionalDouble(double value) {
        this.isPresent = true;
        this.value = value;
    }

    /**
     * Return an {@code OptionalDouble} with the specified value present.
     *
     * @param value the value to be present
     * @return an {@code OptionalDouble} with the value present
     */
    public static java.util.OptionalDouble of(double value) {
        return new java.util.OptionalDouble(value);
    }

    /**
     * If a value is present in this {@code OptionalDouble}, returns the value,
     * otherwise throws {@code NoSuchElementException}.
     *
     * @return the value held by this {@code OptionalDouble}
     * @throws NoSuchElementException if there is no value present
     *
     * @see java.util.OptionalDouble#isPresent()
     */
    public double getAsDouble() {
        if (!isPresent) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    public boolean isPresent() {
        return isPresent;
    }

    /**
     * Have the specified consumer accept the value if a value is present,
     * otherwise do nothing.
     *
     * @param consumer block to be executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is
     * null
     */
    public void ifPresent(DoubleConsumer consumer) {
        if (isPresent)
            consumer.accept(value);
    }

    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param other the value to be returned if there is no value present
     * @return the value, if present, otherwise {@code other}
     */
    public double orElse(double other) {
        return isPresent ? value : other;
    }

    /**
     * Return the value if present, otherwise invoke {@code other} and return
     * the result of that invocation.
     *
     * @param other a {@code DoubleSupplier} whose result is returned if no value
     * is present
     * @return the value if present otherwise the result of {@code other.getAsDouble()}
     * @throws NullPointerException if value is not present and {@code other} is
     * null
     */
    public double orElseGet(DoubleSupplier other) {
        return isPresent ? value : other.getAsDouble();
    }

    /**
     * Return the contained value, if present, otherwise throw an exception
     * to be created by the provided supplier.
     *
     * @apiNote A method reference to the exception constructor with an empty
     * argument list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     *
     * @param <X> Type of the exception to be thrown
     * @param exceptionSupplier The supplier which will return the exception to
     * be thrown
     * @return the present value
     * @throws X if there is no value present
     * @throws NullPointerException if no value is present and
     * {@code exceptionSupplier} is null
     */
    public<X extends Throwable> double orElseThrow(Supplier<X> exceptionSupplier) throws X {
        if (isPresent) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Indicates whether some other object is "equal to" this OptionalDouble. The
     * other object is considered equal if:
     * <ul>
     * <li>it is also an {@code OptionalDouble} and;
     * <li>both instances have no value present or;
     * <li>the present values are "equal to" each other via {@code Double.compare() == 0}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {code true} if the other object is "equal to" this object
     * otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof java.util.OptionalDouble)) {
            return false;
        }

        java.util.OptionalDouble other = (java.util.OptionalDouble) obj;
        return (isPresent && other.isPresent)
               ? Double.compare(value, other.value) == 0
               : isPresent == other.isPresent;
    }

    /**
     * Returns the hash code value of the present value, if any, or 0 (zero) if
     * no value is present.
     *
     * @return hash code value of the present value or 0 if no value is present
     */
    @Override
    public int hashCode() {
        return isPresent ? Double.hashCode(value) : 0;
    }

    /**
     * {@inheritDoc}
     *
     * Returns a non-empty string representation of this object suitable for
     * debugging. The exact presentation format is unspecified and may vary
     * between implementations and versions.
     *
     * @implSpec If a value is present the result must include its string
     * representation in the result. Empty and present instances must be
     * unambiguously differentiable.
     *
     * @return the string representation of this instance
     */
    @Override
    public String toString() {
        return isPresent
                ? String.format("OptionalDouble[%s]", value)
                : "OptionalDouble.empty";
    }
}
