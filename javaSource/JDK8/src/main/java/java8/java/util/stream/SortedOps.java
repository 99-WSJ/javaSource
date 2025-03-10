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

import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.LongStream;


/**
 * Factory methods for transforming streams into sorted streams.
 *
 * @since 1.8
 */
final class SortedOps {

    private SortedOps() { }

    /**
     * Appends a "sorted" operation to the provided stream.
     *
     * @param <T> the type of both input and output elements
     * @param upstream a reference stream with element type T
     */
    static <T> Stream<T> makeRef(java.util.stream.AbstractPipeline<?, T, ?> upstream) {
        return new OfRef<>(upstream);
    }

    /**
     * Appends a "sorted" operation to the provided stream.
     *
     * @param <T> the type of both input and output elements
     * @param upstream a reference stream with element type T
     * @param comparator the comparator to order elements by
     */
    static <T> Stream<T> makeRef(java.util.stream.AbstractPipeline<?, T, ?> upstream,
                                 Comparator<? super T> comparator) {
        return new OfRef<>(upstream, comparator);
    }

    /**
     * Appends a "sorted" operation to the provided stream.
     *
     * @param <T> the type of both input and output elements
     * @param upstream a reference stream with element type T
     */
    static <T> IntStream makeInt(java.util.stream.AbstractPipeline<?, Integer, ?> upstream) {
        return new OfInt(upstream);
    }

    /**
     * Appends a "sorted" operation to the provided stream.
     *
     * @param <T> the type of both input and output elements
     * @param upstream a reference stream with element type T
     */
    static <T> LongStream makeLong(java.util.stream.AbstractPipeline<?, Long, ?> upstream) {
        return new OfLong(upstream);
    }

    /**
     * Appends a "sorted" operation to the provided stream.
     *
     * @param <T> the type of both input and output elements
     * @param upstream a reference stream with element type T
     */
    static <T> DoubleStream makeDouble(java.util.stream.AbstractPipeline<?, Double, ?> upstream) {
        return new OfDouble(upstream);
    }

    /**
     * Specialized subtype for sorting reference streams
     */
    private static final class OfRef<T> extends ReferencePipeline.StatefulOp<T, T> {
        /**
         * Comparator used for sorting
         */
        private final boolean isNaturalSort;
        private final Comparator<? super T> comparator;

        /**
         * Sort using natural order of {@literal <T>} which must be
         * {@code Comparable}.
         */
        OfRef(java.util.stream.AbstractPipeline<?, T, ?> upstream) {
            super(upstream, StreamShape.REFERENCE,
                  java.util.stream.StreamOpFlag.IS_ORDERED | java.util.stream.StreamOpFlag.IS_SORTED);
            this.isNaturalSort = true;
            // Will throw CCE when we try to sort if T is not Comparable
            this.comparator = (Comparator<? super T>) Comparator.naturalOrder();
        }

        /**
         * Sort using the provided comparator.
         *
         * @param comparator The comparator to be used to evaluate ordering.
         */
        OfRef(java.util.stream.AbstractPipeline<?, T, ?> upstream, Comparator<? super T> comparator) {
            super(upstream, StreamShape.REFERENCE,
                  java.util.stream.StreamOpFlag.IS_ORDERED | java.util.stream.StreamOpFlag.NOT_SORTED);
            this.isNaturalSort = false;
            this.comparator = Objects.requireNonNull(comparator);
        }

        @Override
        public Sink<T> opWrapSink(int flags, Sink<T> sink) {
            Objects.requireNonNull(sink);

            // If the input is already naturally sorted and this operation
            // also naturally sorted then this is a no-op
            if (java.util.stream.StreamOpFlag.SORTED.isKnown(flags) && isNaturalSort)
                return sink;
            else if (java.util.stream.StreamOpFlag.SIZED.isKnown(flags))
                return new SizedRefSortingSink<>(sink, comparator);
            else
                return new RefSortingSink<>(sink, comparator);
        }

        @Override
        public <P_IN> Node<T> opEvaluateParallel(PipelineHelper<T> helper,
                                                 Spliterator<P_IN> spliterator,
                                                 IntFunction<T[]> generator) {
            // If the input is already naturally sorted and this operation
            // naturally sorts then collect the output
            if (java.util.stream.StreamOpFlag.SORTED.isKnown(helper.getStreamAndOpFlags()) && isNaturalSort) {
                return helper.evaluate(spliterator, false, generator);
            }
            else {
                // @@@ Weak two-pass parallel implementation; parallel collect, parallel sort
                T[] flattenedData = helper.evaluate(spliterator, true, generator).asArray(generator);
                Arrays.parallelSort(flattenedData, comparator);
                return Nodes.node(flattenedData);
            }
        }
    }

    /**
     * Specialized subtype for sorting int streams.
     */
    private static final class OfInt extends IntPipeline.StatefulOp<Integer> {
        OfInt(java.util.stream.AbstractPipeline<?, Integer, ?> upstream) {
            super(upstream, StreamShape.INT_VALUE,
                  java.util.stream.StreamOpFlag.IS_ORDERED | java.util.stream.StreamOpFlag.IS_SORTED);
        }

        @Override
        public Sink<Integer> opWrapSink(int flags, Sink sink) {
            Objects.requireNonNull(sink);

            if (java.util.stream.StreamOpFlag.SORTED.isKnown(flags))
                return sink;
            else if (java.util.stream.StreamOpFlag.SIZED.isKnown(flags))
                return new SizedIntSortingSink(sink);
            else
                return new IntSortingSink(sink);
        }

        @Override
        public <P_IN> Node<Integer> opEvaluateParallel(PipelineHelper<Integer> helper,
                                                       Spliterator<P_IN> spliterator,
                                                       IntFunction<Integer[]> generator) {
            if (java.util.stream.StreamOpFlag.SORTED.isKnown(helper.getStreamAndOpFlags())) {
                return helper.evaluate(spliterator, false, generator);
            }
            else {
                Node.OfInt n = (Node.OfInt) helper.evaluate(spliterator, true, generator);

                int[] content = n.asPrimitiveArray();
                Arrays.parallelSort(content);

                return Nodes.node(content);
            }
        }
    }

    /**
     * Specialized subtype for sorting long streams.
     */
    private static final class OfLong extends java.util.stream.LongPipeline.StatefulOp<Long> {
        OfLong(java.util.stream.AbstractPipeline<?, Long, ?> upstream) {
            super(upstream, StreamShape.LONG_VALUE,
                  java.util.stream.StreamOpFlag.IS_ORDERED | java.util.stream.StreamOpFlag.IS_SORTED);
        }

        @Override
        public Sink<Long> opWrapSink(int flags, Sink<Long> sink) {
            Objects.requireNonNull(sink);

            if (java.util.stream.StreamOpFlag.SORTED.isKnown(flags))
                return sink;
            else if (java.util.stream.StreamOpFlag.SIZED.isKnown(flags))
                return new SizedLongSortingSink(sink);
            else
                return new LongSortingSink(sink);
        }

        @Override
        public <P_IN> Node<Long> opEvaluateParallel(PipelineHelper<Long> helper,
                                                    Spliterator<P_IN> spliterator,
                                                    IntFunction<Long[]> generator) {
            if (java.util.stream.StreamOpFlag.SORTED.isKnown(helper.getStreamAndOpFlags())) {
                return helper.evaluate(spliterator, false, generator);
            }
            else {
                Node.OfLong n = (Node.OfLong) helper.evaluate(spliterator, true, generator);

                long[] content = n.asPrimitiveArray();
                Arrays.parallelSort(content);

                return Nodes.node(content);
            }
        }
    }

    /**
     * Specialized subtype for sorting double streams.
     */
    private static final class OfDouble extends DoublePipeline.StatefulOp<Double> {
        OfDouble(java.util.stream.AbstractPipeline<?, Double, ?> upstream) {
            super(upstream, StreamShape.DOUBLE_VALUE,
                  java.util.stream.StreamOpFlag.IS_ORDERED | java.util.stream.StreamOpFlag.IS_SORTED);
        }

        @Override
        public Sink<Double> opWrapSink(int flags, Sink<Double> sink) {
            Objects.requireNonNull(sink);

            if (java.util.stream.StreamOpFlag.SORTED.isKnown(flags))
                return sink;
            else if (java.util.stream.StreamOpFlag.SIZED.isKnown(flags))
                return new SizedDoubleSortingSink(sink);
            else
                return new DoubleSortingSink(sink);
        }

        @Override
        public <P_IN> Node<Double> opEvaluateParallel(PipelineHelper<Double> helper,
                                                      Spliterator<P_IN> spliterator,
                                                      IntFunction<Double[]> generator) {
            if (java.util.stream.StreamOpFlag.SORTED.isKnown(helper.getStreamAndOpFlags())) {
                return helper.evaluate(spliterator, false, generator);
            }
            else {
                Node.OfDouble n = (Node.OfDouble) helper.evaluate(spliterator, true, generator);

                double[] content = n.asPrimitiveArray();
                Arrays.parallelSort(content);

                return Nodes.node(content);
            }
        }
    }

    /**
     * {@link Sink} for implementing sort on SIZED reference streams.
     */
    private static final class SizedRefSortingSink<T> extends Sink.ChainedReference<T, T> {
        private final Comparator<? super T> comparator;
        private T[] array;
        private int offset;

        SizedRefSortingSink(Sink<? super T> sink, Comparator<? super T> comparator) {
            super(sink);
            this.comparator = comparator;
        }

        @Override
        public void begin(long size) {
            if (size >= Nodes.MAX_ARRAY_SIZE)
                throw new IllegalArgumentException(Nodes.BAD_SIZE);
            array = (T[]) new Object[(int) size];
        }

        @Override
        public void end() {
            Arrays.sort(array, 0, offset, comparator);
            downstream.begin(offset);
            for (int i = 0; i < offset; i++)
                downstream.accept(array[i]);
            downstream.end();
            array = null;
        }

        @Override
        public void accept(T t) {
            array[offset++] = t;
        }
    }

    /**
     * {@link Sink} for implementing sort on reference streams.
     */
    private static final class RefSortingSink<T> extends Sink.ChainedReference<T, T> {
        private final Comparator<? super T> comparator;
        private ArrayList<T> list;

        RefSortingSink(Sink<? super T> sink, Comparator<? super T> comparator) {
            super(sink);
            this.comparator = comparator;
        }

        @Override
        public void begin(long size) {
            if (size >= Nodes.MAX_ARRAY_SIZE)
                throw new IllegalArgumentException(Nodes.BAD_SIZE);
            list = (size >= 0) ? new ArrayList<T>((int) size) : new ArrayList<T>();
        }

        @Override
        public void end() {
            list.sort(comparator);
            downstream.begin(list.size());
            list.forEach(downstream::accept);
            downstream.end();
            list = null;
        }

        @Override
        public void accept(T t) {
            list.add(t);
        }
    }

    /**
     * {@link Sink} for implementing sort on SIZED int streams.
     */
    private static final class SizedIntSortingSink extends Sink.ChainedInt<Integer> {
        private int[] array;
        private int offset;

        SizedIntSortingSink(Sink<? super Integer> downstream) {
            super(downstream);
        }

        @Override
        public void begin(long size) {
            if (size >= Nodes.MAX_ARRAY_SIZE)
                throw new IllegalArgumentException(Nodes.BAD_SIZE);
            array = new int[(int) size];
        }

        @Override
        public void end() {
            Arrays.sort(array, 0, offset);
            downstream.begin(offset);
            for (int i = 0; i < offset; i++)
                downstream.accept(array[i]);
            downstream.end();
            array = null;
        }

        @Override
        public void accept(int t) {
            array[offset++] = t;
        }
    }

    /**
     * {@link Sink} for implementing sort on int streams.
     */
    private static final class IntSortingSink extends Sink.ChainedInt<Integer> {
        private SpinedBuffer.OfInt b;

        IntSortingSink(Sink<? super Integer> sink) {
            super(sink);
        }

        @Override
        public void begin(long size) {
            if (size >= Nodes.MAX_ARRAY_SIZE)
                throw new IllegalArgumentException(Nodes.BAD_SIZE);
            b = (size > 0) ? new SpinedBuffer.OfInt((int) size) : new SpinedBuffer.OfInt();
        }

        @Override
        public void end() {
            int[] ints = b.asPrimitiveArray();
            Arrays.sort(ints);
            downstream.begin(ints.length);
            for (int anInt : ints)
                downstream.accept(anInt);
            downstream.end();
        }

        @Override
        public void accept(int t) {
            b.accept(t);
        }
    }

    /**
     * {@link Sink} for implementing sort on SIZED long streams.
     */
    private static final class SizedLongSortingSink extends Sink.ChainedLong<Long> {
        private long[] array;
        private int offset;

        SizedLongSortingSink(Sink<? super Long> downstream) {
            super(downstream);
        }

        @Override
        public void begin(long size) {
            if (size >= Nodes.MAX_ARRAY_SIZE)
                throw new IllegalArgumentException(Nodes.BAD_SIZE);
            array = new long[(int) size];
        }

        @Override
        public void end() {
            Arrays.sort(array, 0, offset);
            downstream.begin(offset);
            for (int i = 0; i < offset; i++)
                downstream.accept(array[i]);
            downstream.end();
            array = null;
        }

        @Override
        public void accept(long t) {
            array[offset++] = t;
        }
    }

    /**
     * {@link Sink} for implementing sort on long streams.
     */
    private static final class LongSortingSink extends Sink.ChainedLong<Long> {
        private SpinedBuffer.OfLong b;

        LongSortingSink(Sink<? super Long> sink) {
            super(sink);
        }

        @Override
        public void begin(long size) {
            if (size >= Nodes.MAX_ARRAY_SIZE)
                throw new IllegalArgumentException(Nodes.BAD_SIZE);
            b = (size > 0) ? new SpinedBuffer.OfLong((int) size) : new SpinedBuffer.OfLong();
        }

        @Override
        public void end() {
            long[] longs = b.asPrimitiveArray();
            Arrays.sort(longs);
            downstream.begin(longs.length);
            for (long aLong : longs)
                downstream.accept(aLong);
            downstream.end();
        }

        @Override
        public void accept(long t) {
            b.accept(t);
        }
    }

    /**
     * {@link Sink} for implementing sort on SIZED double streams.
     */
    private static final class SizedDoubleSortingSink extends Sink.ChainedDouble<Double> {
        private double[] array;
        private int offset;

        SizedDoubleSortingSink(Sink<? super Double> downstream) {
            super(downstream);
        }

        @Override
        public void begin(long size) {
            if (size >= Nodes.MAX_ARRAY_SIZE)
                throw new IllegalArgumentException(Nodes.BAD_SIZE);
            array = new double[(int) size];
        }

        @Override
        public void end() {
            Arrays.sort(array, 0, offset);
            downstream.begin(offset);
            for (int i = 0; i < offset; i++)
                downstream.accept(array[i]);
            downstream.end();
            array = null;
        }

        @Override
        public void accept(double t) {
            array[offset++] = t;
        }
    }

    /**
     * {@link Sink} for implementing sort on double streams.
     */
    private static final class DoubleSortingSink extends Sink.ChainedDouble<Double> {
        private SpinedBuffer.OfDouble b;

        DoubleSortingSink(Sink<? super Double> sink) {
            super(sink);
        }

        @Override
        public void begin(long size) {
            if (size >= Nodes.MAX_ARRAY_SIZE)
                throw new IllegalArgumentException(Nodes.BAD_SIZE);
            b = (size > 0) ? new SpinedBuffer.OfDouble((int) size) : new SpinedBuffer.OfDouble();
        }

        @Override
        public void end() {
            double[] doubles = b.asPrimitiveArray();
            Arrays.sort(doubles);
            downstream.begin(doubles.length);
            for (double aDouble : doubles)
                downstream.accept(aDouble);
            downstream.end();
        }

        @Override
        public void accept(double t) {
            b.accept(t);
        }
    }
}
