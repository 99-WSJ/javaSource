/*
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
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
import java.util.function.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamOpFlag;

/**
 * Abstract base class for an intermediate pipeline stage or pipeline source
 * stage implementing whose elements are of type {@code int}.
 *
 * @param <E_IN> type of elements in the upstream source
 * @since 1.8
 */
abstract class IntPipeline<E_IN>
        extends java.util.stream.AbstractPipeline<E_IN, Integer, IntStream>
        implements IntStream {

    /**
     * Constructor for the head of a stream pipeline.
     *
     * @param source {@code Supplier<Spliterator>} describing the stream source
     * @param sourceFlags The source flags for the stream source, described in
     *        {@link StreamOpFlag}
     * @param parallel {@code true} if the pipeline is parallel
     */
    IntPipeline(Supplier<? extends Spliterator<Integer>> source,
                int sourceFlags, boolean parallel) {
        super(source, sourceFlags, parallel);
    }

    /**
     * Constructor for the head of a stream pipeline.
     *
     * @param source {@code Spliterator} describing the stream source
     * @param sourceFlags The source flags for the stream source, described in
     *        {@link StreamOpFlag}
     * @param parallel {@code true} if the pipeline is parallel
     */
    IntPipeline(Spliterator<Integer> source,
                int sourceFlags, boolean parallel) {
        super(source, sourceFlags, parallel);
    }

    /**
     * Constructor for appending an intermediate operation onto an existing
     * pipeline.
     *
     * @param upstream the upstream element source
     * @param opFlags the operation flags for the new operation
     */
    IntPipeline(java.util.stream.AbstractPipeline<?, E_IN, ?> upstream, int opFlags) {
        super(upstream, opFlags);
    }

    /**
     * Adapt a {@code Sink<Integer> to an {@code IntConsumer}, ideally simply
     * by casting.
     */
    private static IntConsumer adapt(java.util.stream.Sink<Integer> sink) {
        if (sink instanceof IntConsumer) {
            return (IntConsumer) sink;
        }
        else {
            if (java.util.stream.Tripwire.ENABLED)
                java.util.stream.Tripwire.trip(java.util.stream.AbstractPipeline.class,
                              "using IntStream.adapt(Sink<Integer> s)");
            return sink::accept;
        }
    }

    /**
     * Adapt a {@code Spliterator<Integer>} to a {@code Spliterator.OfInt}.
     *
     * @implNote
     * The implementation attempts to cast to a Spliterator.OfInt, and throws an
     * exception if this cast is not possible.
     */
    private static Spliterator.OfInt adapt(Spliterator<Integer> s) {
        if (s instanceof Spliterator.OfInt) {
            return (Spliterator.OfInt) s;
        }
        else {
            if (java.util.stream.Tripwire.ENABLED)
                java.util.stream.Tripwire.trip(java.util.stream.AbstractPipeline.class,
                              "using IntStream.adapt(Spliterator<Integer> s)");
            throw new UnsupportedOperationException("IntStream.adapt(Spliterator<Integer> s)");
        }
    }


    // Shape-specific methods

    @Override
    final StreamShape getOutputShape() {
        return StreamShape.INT_VALUE;
    }

    @Override
    final <P_IN> java.util.stream.Node<Integer> evaluateToNode(PipelineHelper<Integer> helper,
                                                               Spliterator<P_IN> spliterator,
                                                               boolean flattenTree,
                                                               IntFunction<Integer[]> generator) {
        return java.util.stream.Nodes.collectInt(helper, spliterator, flattenTree);
    }

    @Override
    final <P_IN> Spliterator<Integer> wrap(PipelineHelper<Integer> ph,
                                           Supplier<Spliterator<P_IN>> supplier,
                                           boolean isParallel) {
        return new java.util.stream.StreamSpliterators.IntWrappingSpliterator<>(ph, supplier, isParallel);
    }

    @Override
    @SuppressWarnings("unchecked")
    final Spliterator.OfInt lazySpliterator(Supplier<? extends Spliterator<Integer>> supplier) {
        return new java.util.stream.StreamSpliterators.DelegatingSpliterator.OfInt((Supplier<Spliterator.OfInt>) supplier);
    }

    @Override
    final void forEachWithCancel(Spliterator<Integer> spliterator, java.util.stream.Sink<Integer> sink) {
        Spliterator.OfInt spl = adapt(spliterator);
        IntConsumer adaptedSink = adapt(sink);
        do { } while (!sink.cancellationRequested() && spl.tryAdvance(adaptedSink));
    }

    @Override
    final java.util.stream.Node.Builder<Integer> makeNodeBuilder(long exactSizeIfKnown,
                                                                 IntFunction<Integer[]> generator) {
        return java.util.stream.Nodes.intBuilder(exactSizeIfKnown);
    }


    // IntStream

    @Override
    public final PrimitiveIterator.OfInt iterator() {
        return Spliterators.iterator(spliterator());
    }

    @Override
    public final Spliterator.OfInt spliterator() {
        return adapt(super.spliterator());
    }

    // Stateless intermediate ops from IntStream

    @Override
    public final LongStream asLongStream() {
        return new java.util.stream.LongPipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE,
                                                     java.util.stream.StreamOpFlag.NOT_SORTED | java.util.stream.StreamOpFlag.NOT_DISTINCT) {
            @Override
            java.util.stream.Sink<Integer> opWrapSink(int flags, java.util.stream.Sink<Long> sink) {
                return new java.util.stream.Sink.ChainedInt<Long>(sink) {
                    @Override
                    public void accept(int t) {
                        downstream.accept((long) t);
                    }
                };
            }
        };
    }

    @Override
    public final DoubleStream asDoubleStream() {
        return new DoublePipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE,
                                                       java.util.stream.StreamOpFlag.NOT_SORTED | java.util.stream.StreamOpFlag.NOT_DISTINCT) {
            @Override
            java.util.stream.Sink<Integer> opWrapSink(int flags, java.util.stream.Sink<Double> sink) {
                return new java.util.stream.Sink.ChainedInt<Double>(sink) {
                    @Override
                    public void accept(int t) {
                        downstream.accept((double) t);
                    }
                };
            }
        };
    }

    @Override
    public final Stream<Integer> boxed() {
        return mapToObj(Integer::valueOf);
    }

    @Override
    public final IntStream map(IntUnaryOperator mapper) {
        Objects.requireNonNull(mapper);
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE,
                                        java.util.stream.StreamOpFlag.NOT_SORTED | java.util.stream.StreamOpFlag.NOT_DISTINCT) {
            @Override
            java.util.stream.Sink<Integer> opWrapSink(int flags, java.util.stream.Sink<Integer> sink) {
                return new java.util.stream.Sink.ChainedInt<Integer>(sink) {
                    @Override
                    public void accept(int t) {
                        downstream.accept(mapper.applyAsInt(t));
                    }
                };
            }
        };
    }

    @Override
    public final <U> Stream<U> mapToObj(IntFunction<? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return new java.util.stream.ReferencePipeline.StatelessOp<Integer, U>(this, StreamShape.INT_VALUE,
                                                             java.util.stream.StreamOpFlag.NOT_SORTED | java.util.stream.StreamOpFlag.NOT_DISTINCT) {
            @Override
            java.util.stream.Sink<Integer> opWrapSink(int flags, java.util.stream.Sink<U> sink) {
                return new java.util.stream.Sink.ChainedInt<U>(sink) {
                    @Override
                    public void accept(int t) {
                        downstream.accept(mapper.apply(t));
                    }
                };
            }
        };
    }

    @Override
    public final LongStream mapToLong(IntToLongFunction mapper) {
        Objects.requireNonNull(mapper);
        return new java.util.stream.LongPipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE,
                                                     java.util.stream.StreamOpFlag.NOT_SORTED | java.util.stream.StreamOpFlag.NOT_DISTINCT) {
            @Override
            java.util.stream.Sink<Integer> opWrapSink(int flags, java.util.stream.Sink<Long> sink) {
                return new java.util.stream.Sink.ChainedInt<Long>(sink) {
                    @Override
                    public void accept(int t) {
                        downstream.accept(mapper.applyAsLong(t));
                    }
                };
            }
        };
    }

    @Override
    public final DoubleStream mapToDouble(IntToDoubleFunction mapper) {
        Objects.requireNonNull(mapper);
        return new DoublePipeline.StatelessOp<Integer>(this, StreamShape.INT_VALUE,
                                                       java.util.stream.StreamOpFlag.NOT_SORTED | java.util.stream.StreamOpFlag.NOT_DISTINCT) {
            @Override
            java.util.stream.Sink<Integer> opWrapSink(int flags, java.util.stream.Sink<Double> sink) {
                return new java.util.stream.Sink.ChainedInt<Double>(sink) {
                    @Override
                    public void accept(int t) {
                        downstream.accept(mapper.applyAsDouble(t));
                    }
                };
            }
        };
    }

    @Override
    public final IntStream flatMap(IntFunction<? extends IntStream> mapper) {
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE,
                                        java.util.stream.StreamOpFlag.NOT_SORTED | java.util.stream.StreamOpFlag.NOT_DISTINCT | java.util.stream.StreamOpFlag.NOT_SIZED) {
            @Override
            java.util.stream.Sink<Integer> opWrapSink(int flags, java.util.stream.Sink<Integer> sink) {
                return new java.util.stream.Sink.ChainedInt<Integer>(sink) {
                    @Override
                    public void begin(long size) {
                        downstream.begin(-1);
                    }

                    @Override
                    public void accept(int t) {
                        try (IntStream result = mapper.apply(t)) {
                            // We can do better that this too; optimize for depth=0 case and just grab spliterator and forEach it
                            if (result != null)
                                result.sequential().forEach(i -> downstream.accept(i));
                        }
                    }
                };
            }
        };
    }

    @Override
    public IntStream unordered() {
        if (!isOrdered())
            return this;
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE, java.util.stream.StreamOpFlag.NOT_ORDERED) {
            @Override
            java.util.stream.Sink<Integer> opWrapSink(int flags, java.util.stream.Sink<Integer> sink) {
                return sink;
            }
        };
    }

    @Override
    public final IntStream filter(IntPredicate predicate) {
        Objects.requireNonNull(predicate);
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE,
                                        java.util.stream.StreamOpFlag.NOT_SIZED) {
            @Override
            java.util.stream.Sink<Integer> opWrapSink(int flags, java.util.stream.Sink<Integer> sink) {
                return new java.util.stream.Sink.ChainedInt<Integer>(sink) {
                    @Override
                    public void begin(long size) {
                        downstream.begin(-1);
                    }

                    @Override
                    public void accept(int t) {
                        if (predicate.test(t))
                            downstream.accept(t);
                    }
                };
            }
        };
    }

    @Override
    public final IntStream peek(IntConsumer action) {
        Objects.requireNonNull(action);
        return new StatelessOp<Integer>(this, StreamShape.INT_VALUE,
                                        0) {
            @Override
            java.util.stream.Sink<Integer> opWrapSink(int flags, java.util.stream.Sink<Integer> sink) {
                return new java.util.stream.Sink.ChainedInt<Integer>(sink) {
                    @Override
                    public void accept(int t) {
                        action.accept(t);
                        downstream.accept(t);
                    }
                };
            }
        };
    }

    // Stateful intermediate ops from IntStream

    @Override
    public final IntStream limit(long maxSize) {
        if (maxSize < 0)
            throw new IllegalArgumentException(Long.toString(maxSize));
        return SliceOps.makeInt(this, 0, maxSize);
    }

    @Override
    public final IntStream skip(long n) {
        if (n < 0)
            throw new IllegalArgumentException(Long.toString(n));
        if (n == 0)
            return this;
        else
            return SliceOps.makeInt(this, n, -1);
    }

    @Override
    public final IntStream sorted() {
        return java.util.stream.SortedOps.makeInt(this);
    }

    @Override
    public final IntStream distinct() {
        // While functional and quick to implement, this approach is not very efficient.
        // An efficient version requires an int-specific map/set implementation.
        return boxed().distinct().mapToInt(i -> i);
    }

    // Terminal ops from IntStream

    @Override
    public void forEach(IntConsumer action) {
        evaluate(java.util.stream.ForEachOps.makeInt(action, false));
    }

    @Override
    public void forEachOrdered(IntConsumer action) {
        evaluate(java.util.stream.ForEachOps.makeInt(action, true));
    }

    @Override
    public final int sum() {
        return reduce(0, Integer::sum);
    }

    @Override
    public final OptionalInt min() {
        return reduce(Math::min);
    }

    @Override
    public final OptionalInt max() {
        return reduce(Math::max);
    }

    @Override
    public final long count() {
        return mapToLong(e -> 1L).sum();
    }

    @Override
    public final OptionalDouble average() {
        long[] avg = collect(() -> new long[2],
                             (ll, i) -> {
                                 ll[0]++;
                                 ll[1] += i;
                             },
                             (ll, rr) -> {
                                 ll[0] += rr[0];
                                 ll[1] += rr[1];
                             });
        return avg[0] > 0
               ? OptionalDouble.of((double) avg[1] / avg[0])
               : OptionalDouble.empty();
    }

    @Override
    public final IntSummaryStatistics summaryStatistics() {
        return collect(IntSummaryStatistics::new, IntSummaryStatistics::accept,
                       IntSummaryStatistics::combine);
    }

    @Override
    public final int reduce(int identity, IntBinaryOperator op) {
        return evaluate(java.util.stream.ReduceOps.makeInt(identity, op));
    }

    @Override
    public final OptionalInt reduce(IntBinaryOperator op) {
        return evaluate(java.util.stream.ReduceOps.makeInt(op));
    }

    @Override
    public final <R> R collect(Supplier<R> supplier,
                               ObjIntConsumer<R> accumulator,
                               BiConsumer<R, R> combiner) {
        BinaryOperator<R> operator = (left, right) -> {
            combiner.accept(left, right);
            return left;
        };
        return evaluate(java.util.stream.ReduceOps.makeInt(supplier, accumulator, operator));
    }

    @Override
    public final boolean anyMatch(IntPredicate predicate) {
        return evaluate(java.util.stream.MatchOps.makeInt(predicate, java.util.stream.MatchOps.MatchKind.ANY));
    }

    @Override
    public final boolean allMatch(IntPredicate predicate) {
        return evaluate(java.util.stream.MatchOps.makeInt(predicate, java.util.stream.MatchOps.MatchKind.ALL));
    }

    @Override
    public final boolean noneMatch(IntPredicate predicate) {
        return evaluate(java.util.stream.MatchOps.makeInt(predicate, java.util.stream.MatchOps.MatchKind.NONE));
    }

    @Override
    public final OptionalInt findFirst() {
        return evaluate(FindOps.makeInt(true));
    }

    @Override
    public final OptionalInt findAny() {
        return evaluate(FindOps.makeInt(false));
    }

    @Override
    public final int[] toArray() {
        return java.util.stream.Nodes.flattenInt((java.util.stream.Node.OfInt) evaluateToArrayNode(Integer[]::new))
                        .asPrimitiveArray();
    }

    //

    /**
     * Source stage of an IntStream.
     *
     * @param <E_IN> type of elements in the upstream source
     * @since 1.8
     */
    static class Head<E_IN> extends java.util.stream.IntPipeline<E_IN> {
        /**
         * Constructor for the source stage of an IntStream.
         *
         * @param source {@code Supplier<Spliterator>} describing the stream
         *               source
         * @param sourceFlags the source flags for the stream source, described
         *                    in {@link StreamOpFlag}
         * @param parallel {@code true} if the pipeline is parallel
         */
        Head(Supplier<? extends Spliterator<Integer>> source,
             int sourceFlags, boolean parallel) {
            super(source, sourceFlags, parallel);
        }

        /**
         * Constructor for the source stage of an IntStream.
         *
         * @param source {@code Spliterator} describing the stream source
         * @param sourceFlags the source flags for the stream source, described
         *                    in {@link StreamOpFlag}
         * @param parallel {@code true} if the pipeline is parallel
         */
        Head(Spliterator<Integer> source,
             int sourceFlags, boolean parallel) {
            super(source, sourceFlags, parallel);
        }

        @Override
        final boolean opIsStateful() {
            throw new UnsupportedOperationException();
        }

        @Override
        final java.util.stream.Sink<E_IN> opWrapSink(int flags, java.util.stream.Sink<Integer> sink) {
            throw new UnsupportedOperationException();
        }

        // Optimized sequential terminal operations for the head of the pipeline

        @Override
        public void forEach(IntConsumer action) {
            if (!isParallel()) {
                adapt(sourceStageSpliterator()).forEachRemaining(action);
            }
            else {
                super.forEach(action);
            }
        }

        @Override
        public void forEachOrdered(IntConsumer action) {
            if (!isParallel()) {
                adapt(sourceStageSpliterator()).forEachRemaining(action);
            }
            else {
                super.forEachOrdered(action);
            }
        }
    }

    /**
     * Base class for a stateless intermediate stage of an IntStream
     *
     * @param <E_IN> type of elements in the upstream source
     * @since 1.8
     */
    abstract static class StatelessOp<E_IN> extends java.util.stream.IntPipeline<E_IN> {
        /**
         * Construct a new IntStream by appending a stateless intermediate
         * operation to an existing stream.
         * @param upstream The upstream pipeline stage
         * @param inputShape The stream shape for the upstream pipeline stage
         * @param opFlags Operation flags for the new stage
         */
        StatelessOp(java.util.stream.AbstractPipeline<?, E_IN, ?> upstream,
                    StreamShape inputShape,
                    int opFlags) {
            super(upstream, opFlags);
            assert upstream.getOutputShape() == inputShape;
        }

        @Override
        final boolean opIsStateful() {
            return false;
        }
    }

    /**
     * Base class for a stateful intermediate stage of an IntStream.
     *
     * @param <E_IN> type of elements in the upstream source
     * @since 1.8
     */
    abstract static class StatefulOp<E_IN> extends java.util.stream.IntPipeline<E_IN> {
        /**
         * Construct a new IntStream by appending a stateful intermediate
         * operation to an existing stream.
         * @param upstream The upstream pipeline stage
         * @param inputShape The stream shape for the upstream pipeline stage
         * @param opFlags Operation flags for the new stage
         */
        StatefulOp(java.util.stream.AbstractPipeline<?, E_IN, ?> upstream,
                   StreamShape inputShape,
                   int opFlags) {
            super(upstream, opFlags);
            assert upstream.getOutputShape() == inputShape;
        }

        @Override
        final boolean opIsStateful() {
            return true;
        }

        @Override
        abstract <P_IN> java.util.stream.Node<Integer> opEvaluateParallel(PipelineHelper<Integer> helper,
                                                                          Spliterator<P_IN> spliterator,
                                                                          IntFunction<Integer[]> generator);
    }
}
