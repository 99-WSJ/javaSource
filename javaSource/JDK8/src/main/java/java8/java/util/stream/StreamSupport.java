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
package java8.java.util.stream;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Low-level utility methods for creating and manipulating streams.
 *
 * <p>This class is mostly for library writers presenting stream views
 * of data structures; most static stream methods intended for end users are in
 * the various {@code Stream} classes.
 *
 * @since 1.8
 */
public final class StreamSupport {

    // Suppresses default constructor, ensuring non-instantiability.
    private StreamSupport() {}

    /**
     * Creates a new sequential or parallel {@code Stream} from a
     * {@code Spliterator}.
     *
     * <p>The spliterator is only traversed, split, or queried for estimated
     * size after the terminal operation of the stream pipeline commences.
     *
     * <p>It is strongly recommended the spliterator report a characteristic of
     * {@code IMMUTABLE} or {@code CONCURRENT}, or be
     * <a href="../Spliterator.html#binding">late-binding</a>.  Otherwise,
     * {@link #stream(Supplier, int, boolean)} should be used
     * to reduce the scope of potential interference with the source.  See
     * <a href="package-summary.html#NonInterference">Non-Interference</a> for
     * more details.
     *
     * @param <T> the type of stream elements
     * @param spliterator a {@code Spliterator} describing the stream elements
     * @param parallel if {@code true} then the returned stream is a parallel
     *        stream; if {@code false} the returned stream is a sequential
     *        stream.
     * @return a new sequential or parallel {@code Stream}
     */
    public static <T> Stream<T> stream(Spliterator<T> spliterator, boolean parallel) {
        Objects.requireNonNull(spliterator);
        return new java.util.stream.ReferencePipeline.Head<>(spliterator,
                                            java.util.stream.StreamOpFlag.fromCharacteristics(spliterator),
                                            parallel);
    }

    /**
     * Creates a new sequential or parallel {@code Stream} from a
     * {@code Supplier} of {@code Spliterator}.
     *
     * <p>The {@link Supplier#get()} method will be invoked on the supplier no
     * more than once, and only after the terminal operation of the stream pipeline
     * commences.
     *
     * <p>For spliterators that report a characteristic of {@code IMMUTABLE}
     * or {@code CONCURRENT}, or that are
     * <a href="../Spliterator.html#binding">late-binding</a>, it is likely
     * more efficient to use {@link #stream(Spliterator, boolean)}
     * instead.
     * <p>The use of a {@code Supplier} in this form provides a level of
     * indirection that reduces the scope of potential interference with the
     * source.  Since the supplier is only invoked after the terminal operation
     * commences, any modifications to the source up to the start of the
     * terminal operation are reflected in the stream result.  See
     * <a href="package-summary.html#NonInterference">Non-Interference</a> for
     * more details.
     *
     * @param <T> the type of stream elements
     * @param supplier a {@code Supplier} of a {@code Spliterator}
     * @param characteristics Spliterator characteristics of the supplied
     *        {@code Spliterator}.  The characteristics must be equal to
     *        {@code supplier.get().characteristics()}, otherwise undefined
     *        behavior may occur when terminal operation commences.
     * @param parallel if {@code true} then the returned stream is a parallel
     *        stream; if {@code false} the returned stream is a sequential
     *        stream.
     * @return a new sequential or parallel {@code Stream}
     * @see #stream(Spliterator, boolean)
     */
    public static <T> Stream<T> stream(Supplier<? extends Spliterator<T>> supplier,
                                       int characteristics,
                                       boolean parallel) {
        Objects.requireNonNull(supplier);
        return new java.util.stream.ReferencePipeline.Head<>(supplier,
                                            java.util.stream.StreamOpFlag.fromCharacteristics(characteristics),
                                            parallel);
    }

    /**
     * Creates a new sequential or parallel {@code IntStream} from a
     * {@code Spliterator.OfInt}.
     *
     * <p>The spliterator is only traversed, split, or queried for estimated size
     * after the terminal operation of the stream pipeline commences.
     *
     * <p>It is strongly recommended the spliterator report a characteristic of
     * {@code IMMUTABLE} or {@code CONCURRENT}, or be
     * <a href="../Spliterator.html#binding">late-binding</a>.  Otherwise,
     * {@link #intStream(Supplier, int, boolean)} should be
     * used to reduce the scope of potential interference with the source.  See
     * <a href="package-summary.html#NonInterference">Non-Interference</a> for
     * more details.
     *
     * @param spliterator a {@code Spliterator.OfInt} describing the stream elements
     * @param parallel if {@code true} then the returned stream is a parallel
     *        stream; if {@code false} the returned stream is a sequential
     *        stream.
     * @return a new sequential or parallel {@code IntStream}
     */
    public static IntStream intStream(Spliterator.OfInt spliterator, boolean parallel) {
        return new java.util.stream.IntPipeline.Head<>(spliterator,
                                      java.util.stream.StreamOpFlag.fromCharacteristics(spliterator),
                                      parallel);
    }

    /**
     * Creates a new sequential or parallel {@code IntStream} from a
     * {@code Supplier} of {@code Spliterator.OfInt}.
     *
     * <p>The {@link Supplier#get()} method will be invoked on the supplier no
     * more than once, and only after the terminal operation of the stream pipeline
     * commences.
     *
     * <p>For spliterators that report a characteristic of {@code IMMUTABLE}
     * or {@code CONCURRENT}, or that are
     * <a href="../Spliterator.html#binding">late-binding</a>, it is likely
     * more efficient to use {@link #intStream(Spliterator.OfInt, boolean)}
     * instead.
     * <p>The use of a {@code Supplier} in this form provides a level of
     * indirection that reduces the scope of potential interference with the
     * source.  Since the supplier is only invoked after the terminal operation
     * commences, any modifications to the source up to the start of the
     * terminal operation are reflected in the stream result.  See
     * <a href="package-summary.html#NonInterference">Non-Interference</a> for
     * more details.
     *
     * @param supplier a {@code Supplier} of a {@code Spliterator.OfInt}
     * @param characteristics Spliterator characteristics of the supplied
     *        {@code Spliterator.OfInt}.  The characteristics must be equal to
     *        {@code supplier.get().characteristics()}, otherwise undefined
     *        behavior may occur when terminal operation commences.
     * @param parallel if {@code true} then the returned stream is a parallel
     *        stream; if {@code false} the returned stream is a sequential
     *        stream.
     * @return a new sequential or parallel {@code IntStream}
     * @see #intStream(Spliterator.OfInt, boolean)
     */
    public static IntStream intStream(Supplier<? extends Spliterator.OfInt> supplier,
                                      int characteristics,
                                      boolean parallel) {
        return new java.util.stream.IntPipeline.Head<>(supplier,
                                      java.util.stream.StreamOpFlag.fromCharacteristics(characteristics),
                                      parallel);
    }

    /**
     * Creates a new sequential or parallel {@code LongStream} from a
     * {@code Spliterator.OfLong}.
     *
     * <p>The spliterator is only traversed, split, or queried for estimated
     * size after the terminal operation of the stream pipeline commences.
     *
     * <p>It is strongly recommended the spliterator report a characteristic of
     * {@code IMMUTABLE} or {@code CONCURRENT}, or be
     * <a href="../Spliterator.html#binding">late-binding</a>.  Otherwise,
     * {@link #longStream(Supplier, int, boolean)} should be
     * used to reduce the scope of potential interference with the source.  See
     * <a href="package-summary.html#NonInterference">Non-Interference</a> for
     * more details.
     *
     * @param spliterator a {@code Spliterator.OfLong} describing the stream elements
     * @param parallel if {@code true} then the returned stream is a parallel
     *        stream; if {@code false} the returned stream is a sequential
     *        stream.
     * @return a new sequential or parallel {@code LongStream}
     */
    public static LongStream longStream(Spliterator.OfLong spliterator,
                                        boolean parallel) {
        return new java.util.stream.LongPipeline.Head<>(spliterator,
                                       java.util.stream.StreamOpFlag.fromCharacteristics(spliterator),
                                       parallel);
    }

    /**
     * Creates a new sequential or parallel {@code LongStream} from a
     * {@code Supplier} of {@code Spliterator.OfLong}.
     *
     * <p>The {@link Supplier#get()} method will be invoked on the supplier no
     * more than once, and only after the terminal operation of the stream pipeline
     * commences.
     *
     * <p>For spliterators that report a characteristic of {@code IMMUTABLE}
     * or {@code CONCURRENT}, or that are
     * <a href="../Spliterator.html#binding">late-binding</a>, it is likely
     * more efficient to use {@link #longStream(Spliterator.OfLong, boolean)}
     * instead.
     * <p>The use of a {@code Supplier} in this form provides a level of
     * indirection that reduces the scope of potential interference with the
     * source.  Since the supplier is only invoked after the terminal operation
     * commences, any modifications to the source up to the start of the
     * terminal operation are reflected in the stream result.  See
     * <a href="package-summary.html#NonInterference">Non-Interference</a> for
     * more details.
     *
     * @param supplier a {@code Supplier} of a {@code Spliterator.OfLong}
     * @param characteristics Spliterator characteristics of the supplied
     *        {@code Spliterator.OfLong}.  The characteristics must be equal to
     *        {@code supplier.get().characteristics()}, otherwise undefined
     *        behavior may occur when terminal operation commences.
     * @param parallel if {@code true} then the returned stream is a parallel
     *        stream; if {@code false} the returned stream is a sequential
     *        stream.
     * @return a new sequential or parallel {@code LongStream}
     * @see #longStream(Spliterator.OfLong, boolean)
     */
    public static LongStream longStream(Supplier<? extends Spliterator.OfLong> supplier,
                                        int characteristics,
                                        boolean parallel) {
        return new java.util.stream.LongPipeline.Head<>(supplier,
                                       java.util.stream.StreamOpFlag.fromCharacteristics(characteristics),
                                       parallel);
    }

    /**
     * Creates a new sequential or parallel {@code DoubleStream} from a
     * {@code Spliterator.OfDouble}.
     *
     * <p>The spliterator is only traversed, split, or queried for estimated size
     * after the terminal operation of the stream pipeline commences.
     *
     * <p>It is strongly recommended the spliterator report a characteristic of
     * {@code IMMUTABLE} or {@code CONCURRENT}, or be
     * <a href="../Spliterator.html#binding">late-binding</a>.  Otherwise,
     * {@link #doubleStream(Supplier, int, boolean)} should
     * be used to reduce the scope of potential interference with the source.  See
     * <a href="package-summary.html#NonInterference">Non-Interference</a> for
     * more details.
     *
     * @param spliterator A {@code Spliterator.OfDouble} describing the stream elements
     * @param parallel if {@code true} then the returned stream is a parallel
     *        stream; if {@code false} the returned stream is a sequential
     *        stream.
     * @return a new sequential or parallel {@code DoubleStream}
     */
    public static DoubleStream doubleStream(Spliterator.OfDouble spliterator,
                                            boolean parallel) {
        return new DoublePipeline.Head<>(spliterator,
                                         java.util.stream.StreamOpFlag.fromCharacteristics(spliterator),
                                         parallel);
    }

    /**
     * Creates a new sequential or parallel {@code DoubleStream} from a
     * {@code Supplier} of {@code Spliterator.OfDouble}.
     *
     * <p>The {@link Supplier#get()} method will be invoked on the supplier no
     * more than once, and only after the terminal operation of the stream pipeline
     * commences.
     *
     * <p>For spliterators that report a characteristic of {@code IMMUTABLE}
     * or {@code CONCURRENT}, or that are
     * <a href="../Spliterator.html#binding">late-binding</a>, it is likely
     * more efficient to use {@link #doubleStream(Spliterator.OfDouble, boolean)}
     * instead.
     * <p>The use of a {@code Supplier} in this form provides a level of
     * indirection that reduces the scope of potential interference with the
     * source.  Since the supplier is only invoked after the terminal operation
     * commences, any modifications to the source up to the start of the
     * terminal operation are reflected in the stream result.  See
     * <a href="package-summary.html#NonInterference">Non-Interference</a> for
     * more details.
     *
     * @param supplier A {@code Supplier} of a {@code Spliterator.OfDouble}
     * @param characteristics Spliterator characteristics of the supplied
     *        {@code Spliterator.OfDouble}.  The characteristics must be equal to
     *        {@code supplier.get().characteristics()}, otherwise undefined
     *        behavior may occur when terminal operation commences.
     * @param parallel if {@code true} then the returned stream is a parallel
     *        stream; if {@code false} the returned stream is a sequential
     *        stream.
     * @return a new sequential or parallel {@code DoubleStream}
     * @see #doubleStream(Spliterator.OfDouble, boolean)
     */
    public static DoubleStream doubleStream(Supplier<? extends Spliterator.OfDouble> supplier,
                                            int characteristics,
                                            boolean parallel) {
        return new DoublePipeline.Head<>(supplier,
                                         java.util.stream.StreamOpFlag.fromCharacteristics(characteristics),
                                         parallel);
    }
}
