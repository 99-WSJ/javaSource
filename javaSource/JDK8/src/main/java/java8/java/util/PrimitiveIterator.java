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
package java8.java.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/**
 * A base type for primitive specializations of {@code Iterator}.  Specialized
 * subtypes are provided for {@link OfInt int}, {@link OfLong long}, and
 * {@link OfDouble double} values.
 *
 * <p>The specialized subtype default implementations of {@link Iterator#next}
 * and {@link Iterator#forEachRemaining(Consumer)} box
 * primitive values to instances of their corresponding wrapper class.  Such
 * boxing may offset any advantages gained when using the primitive
 * specializations.  To avoid boxing, the corresponding primitive-based methods
 * should be used.  For example, {@link java.util.PrimitiveIterator.OfInt#nextInt()} and
 * {@link java.util.PrimitiveIterator.OfInt#forEachRemaining(IntConsumer)}
 * should be used in preference to {@link java.util.PrimitiveIterator.OfInt#next()} and
 * {@link java.util.PrimitiveIterator.OfInt#forEachRemaining(Consumer)}.
 *
 * <p>Iteration of primitive values using boxing-based methods
 * {@link Iterator#next next()} and
 * {@link Iterator#forEachRemaining(Consumer) forEachRemaining()},
 * does not affect the order in which the values, transformed to boxed values,
 * are encountered.
 *
 * @implNote
 * If the boolean system property {@code org.openjdk.java.util.stream.tripwire}
 * is set to {@code true} then diagnostic warnings are reported if boxing of
 * primitive values occur when operating on primitive subtype specializations.
 *
 * @param <T> the type of elements returned by this PrimitiveIterator.  The
 *        type must be a wrapper type for a primitive type, such as
 *        {@code Integer} for the primitive {@code int} type.
 * @param <T_CONS> the type of primitive consumer.  The type must be a
 *        primitive specialization of {@link Consumer} for
 *        {@code T}, such as {@link IntConsumer} for
 *        {@code Integer}.
 *
 * @since 1.8
 */
public interface PrimitiveIterator<T, T_CONS> extends Iterator<T> {

    /**
     * Performs the given action for each remaining element, in the order
     * elements occur when iterating, until all elements have been processed
     * or the action throws an exception.  Errors or runtime exceptions
     * thrown by the action are relayed to the caller.
     *
     * @param action The action to be performed for each element
     * @throws NullPointerException if the specified action is null
     */
    @SuppressWarnings("overloads")
    void forEachRemaining(T_CONS action);

    /**
     * An Iterator specialized for {@code int} values.
     * @since 1.8
     */
    public static interface OfInt extends java.util.PrimitiveIterator<Integer, IntConsumer> {

        /**
         * Returns the next {@code int} element in the iteration.
         *
         * @return the next {@code int} element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        int nextInt();

        /**
         * Performs the given action for each remaining element until all elements
         * have been processed or the action throws an exception.  Actions are
         * performed in the order of iteration, if that order is specified.
         * Exceptions thrown by the action are relayed to the caller.
         *
         * @implSpec
         * <p>The default implementation behaves as if:
         * <pre>{@code
         *     while (hasNext())
         *         action.accept(nextInt());
         * }</pre>
         *
         * @param action The action to be performed for each element
         * @throws NullPointerException if the specified action is null
         */
        default void forEachRemaining(IntConsumer action) {
            Objects.requireNonNull(action);
            while (hasNext())
                action.accept(nextInt());
        }

        /**
         * {@inheritDoc}
         * @implSpec
         * The default implementation boxes the result of calling
         * {@link #nextInt()}, and returns that boxed result.
         */
        @Override
        default Integer next() {
            if (java.util.Tripwire.ENABLED)
                java.util.Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfInt.nextInt()");
            return nextInt();
        }

        /**
         * {@inheritDoc}
         * @implSpec
         * If the action is an instance of {@code IntConsumer} then it is cast
         * to {@code IntConsumer} and passed to {@link #forEachRemaining};
         * otherwise the action is adapted to an instance of
         * {@code IntConsumer}, by boxing the argument of {@code IntConsumer},
         * and then passed to {@link #forEachRemaining}.
         */
        @Override
        default void forEachRemaining(Consumer<? super Integer> action) {
            if (action instanceof IntConsumer) {
                forEachRemaining((IntConsumer) action);
            }
            else {
                // The method reference action::accept is never null
                Objects.requireNonNull(action);
                if (java.util.Tripwire.ENABLED)
                    java.util.Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfInt.forEachRemainingInt(action::accept)");
                forEachRemaining((IntConsumer) action::accept);
            }
        }

    }

    /**
     * An Iterator specialized for {@code long} values.
     * @since 1.8
     */
    public static interface OfLong extends java.util.PrimitiveIterator<Long, LongConsumer> {

        /**
         * Returns the next {@code long} element in the iteration.
         *
         * @return the next {@code long} element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        long nextLong();

        /**
         * Performs the given action for each remaining element until all elements
         * have been processed or the action throws an exception.  Actions are
         * performed in the order of iteration, if that order is specified.
         * Exceptions thrown by the action are relayed to the caller.
         *
         * @implSpec
         * <p>The default implementation behaves as if:
         * <pre>{@code
         *     while (hasNext())
         *         action.accept(nextLong());
         * }</pre>
         *
         * @param action The action to be performed for each element
         * @throws NullPointerException if the specified action is null
         */
        default void forEachRemaining(LongConsumer action) {
            Objects.requireNonNull(action);
            while (hasNext())
                action.accept(nextLong());
        }

        /**
         * {@inheritDoc}
         * @implSpec
         * The default implementation boxes the result of calling
         * {@link #nextLong()}, and returns that boxed result.
         */
        @Override
        default Long next() {
            if (java.util.Tripwire.ENABLED)
                java.util.Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfLong.nextLong()");
            return nextLong();
        }

        /**
         * {@inheritDoc}
         * @implSpec
         * If the action is an instance of {@code LongConsumer} then it is cast
         * to {@code LongConsumer} and passed to {@link #forEachRemaining};
         * otherwise the action is adapted to an instance of
         * {@code LongConsumer}, by boxing the argument of {@code LongConsumer},
         * and then passed to {@link #forEachRemaining}.
         */
        @Override
        default void forEachRemaining(Consumer<? super Long> action) {
            if (action instanceof LongConsumer) {
                forEachRemaining((LongConsumer) action);
            }
            else {
                // The method reference action::accept is never null
                Objects.requireNonNull(action);
                if (java.util.Tripwire.ENABLED)
                    java.util.Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfLong.forEachRemainingLong(action::accept)");
                forEachRemaining((LongConsumer) action::accept);
            }
        }
    }

    /**
     * An Iterator specialized for {@code double} values.
     * @since 1.8
     */
    public static interface OfDouble extends java.util.PrimitiveIterator<Double, DoubleConsumer> {

        /**
         * Returns the next {@code double} element in the iteration.
         *
         * @return the next {@code double} element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        double nextDouble();

        /**
         * Performs the given action for each remaining element until all elements
         * have been processed or the action throws an exception.  Actions are
         * performed in the order of iteration, if that order is specified.
         * Exceptions thrown by the action are relayed to the caller.
         *
         * @implSpec
         * <p>The default implementation behaves as if:
         * <pre>{@code
         *     while (hasNext())
         *         action.accept(nextDouble());
         * }</pre>
         *
         * @param action The action to be performed for each element
         * @throws NullPointerException if the specified action is null
         */
        default void forEachRemaining(DoubleConsumer action) {
            Objects.requireNonNull(action);
            while (hasNext())
                action.accept(nextDouble());
        }

        /**
         * {@inheritDoc}
         * @implSpec
         * The default implementation boxes the result of calling
         * {@link #nextDouble()}, and returns that boxed result.
         */
        @Override
        default Double next() {
            if (java.util.Tripwire.ENABLED)
                java.util.Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfDouble.nextLong()");
            return nextDouble();
        }

        /**
         * {@inheritDoc}
         * @implSpec
         * If the action is an instance of {@code DoubleConsumer} then it is
         * cast to {@code DoubleConsumer} and passed to
         * {@link #forEachRemaining}; otherwise the action is adapted to
         * an instance of {@code DoubleConsumer}, by boxing the argument of
         * {@code DoubleConsumer}, and then passed to
         * {@link #forEachRemaining}.
         */
        @Override
        default void forEachRemaining(Consumer<? super Double> action) {
            if (action instanceof DoubleConsumer) {
                forEachRemaining((DoubleConsumer) action);
            }
            else {
                // The method reference action::accept is never null
                Objects.requireNonNull(action);
                if (java.util.Tripwire.ENABLED)
                    java.util.Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfDouble.forEachRemainingDouble(action::accept)");
                forEachRemaining((DoubleConsumer) action::accept);
            }
        }
    }
}
