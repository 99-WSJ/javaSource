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
import java.util.concurrent.CountedCompleter;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Factory for instances of a short-circuiting {@code TerminalOp} that searches
 * for an element in a stream pipeline, and terminates when it finds one.
 * Supported variants include find-first (find the first element in the
 * encounter order) and find-any (find any element, may not be the first in
 * encounter order.)
 *
 * @since 1.8
 */
final class FindOps {

    private FindOps() { }

    /**
     * Constructs a {@code TerminalOp} for streams of objects.
     *
     * @param <T> the type of elements of the stream
     * @param mustFindFirst whether the {@code TerminalOp} must produce the
     *        first element in the encounter order
     * @return a {@code TerminalOp} implementing the find operation
     */
    public static <T> java.util.stream.TerminalOp<T, Optional<T>> makeRef(boolean mustFindFirst) {
        return new FindOp<>(mustFindFirst, java.util.stream.StreamShape.REFERENCE, Optional.empty(),
                            Optional::isPresent, FindSink.OfRef::new);
    }

    /**
     * Constructs a {@code TerminalOp} for streams of ints.
     *
     * @param mustFindFirst whether the {@code TerminalOp} must produce the
     *        first element in the encounter order
     * @return a {@code TerminalOp} implementing the find operation
     */
    public static java.util.stream.TerminalOp<Integer, OptionalInt> makeInt(boolean mustFindFirst) {
        return new FindOp<>(mustFindFirst, java.util.stream.StreamShape.INT_VALUE, OptionalInt.empty(),
                            OptionalInt::isPresent, FindSink.OfInt::new);
    }

    /**
     * Constructs a {@code TerminalOp} for streams of longs.
     *
     * @param mustFindFirst whether the {@code TerminalOp} must produce the
     *        first element in the encounter order
     * @return a {@code TerminalOp} implementing the find operation
     */
    public static java.util.stream.TerminalOp<Long, OptionalLong> makeLong(boolean mustFindFirst) {
        return new FindOp<>(mustFindFirst, java.util.stream.StreamShape.LONG_VALUE, OptionalLong.empty(),
                            OptionalLong::isPresent, FindSink.OfLong::new);
    }

    /**
     * Constructs a {@code FindOp} for streams of doubles.
     *
     * @param mustFindFirst whether the {@code TerminalOp} must produce the
     *        first element in the encounter order
     * @return a {@code TerminalOp} implementing the find operation
     */
    public static java.util.stream.TerminalOp<Double, OptionalDouble> makeDouble(boolean mustFindFirst) {
        return new FindOp<>(mustFindFirst, java.util.stream.StreamShape.DOUBLE_VALUE, OptionalDouble.empty(),
                            OptionalDouble::isPresent, FindSink.OfDouble::new);
    }

    /**
     * A short-circuiting {@code TerminalOp} that searches for an element in a
     * stream pipeline, and terminates when it finds one.  Implements both
     * find-first (find the first element in the encounter order) and find-any
     * (find any element, may not be the first in encounter order.)
     *
     * @param <T> the output type of the stream pipeline
     * @param <O> the result type of the find operation, typically an optional
     *        type
     */
    private static final class FindOp<T, O> implements java.util.stream.TerminalOp<T, O> {
        private final java.util.stream.StreamShape shape;
        final boolean mustFindFirst;
        final O emptyValue;
        final Predicate<O> presentPredicate;
        final Supplier<java.util.stream.TerminalSink<T, O>> sinkSupplier;

        /**
         * Constructs a {@code FindOp}.
         *
         * @param mustFindFirst if true, must find the first element in
         *        encounter order, otherwise can find any element
         * @param shape stream shape of elements to search
         * @param emptyValue result value corresponding to "found nothing"
         * @param presentPredicate {@code Predicate} on result value
         *        corresponding to "found something"
         * @param sinkSupplier supplier for a {@code TerminalSink} implementing
         *        the matching functionality
         */
        FindOp(boolean mustFindFirst,
                       java.util.stream.StreamShape shape,
                       O emptyValue,
                       Predicate<O> presentPredicate,
                       Supplier<java.util.stream.TerminalSink<T, O>> sinkSupplier) {
            this.mustFindFirst = mustFindFirst;
            this.shape = shape;
            this.emptyValue = emptyValue;
            this.presentPredicate = presentPredicate;
            this.sinkSupplier = sinkSupplier;
        }

        @Override
        public int getOpFlags() {
            return java.util.stream.StreamOpFlag.IS_SHORT_CIRCUIT | (mustFindFirst ? 0 : java.util.stream.StreamOpFlag.NOT_ORDERED);
        }

        @Override
        public java.util.stream.StreamShape inputShape() {
            return shape;
        }

        @Override
        public <S> O evaluateSequential(java.util.stream.PipelineHelper<T> helper,
                                        Spliterator<S> spliterator) {
            O result = helper.wrapAndCopyInto(sinkSupplier.get(), spliterator).get();
            return result != null ? result : emptyValue;
        }

        @Override
        public <P_IN> O evaluateParallel(java.util.stream.PipelineHelper<T> helper,
                                         Spliterator<P_IN> spliterator) {
            return new FindTask<>(this, helper, spliterator).invoke();
        }
    }

    /**
     * Implementation of @{code TerminalSink} that implements the find
     * functionality, requesting cancellation when something has been found
     *
     * @param <T> The type of input element
     * @param <O> The result type, typically an optional type
     */
    private static abstract class FindSink<T, O> implements java.util.stream.TerminalSink<T, O> {
        boolean hasValue;
        T value;

        FindSink() {} // Avoid creation of special accessor

        @Override
        public void accept(T value) {
            if (!hasValue) {
                hasValue = true;
                this.value = value;
            }
        }

        @Override
        public boolean cancellationRequested() {
            return hasValue;
        }

        /** Specialization of {@code FindSink} for reference streams */
        static final class OfRef<T> extends java.util.stream.FindOps.FindSink<T, Optional<T>> {
            @Override
            public Optional<T> get() {
                return hasValue ? Optional.of(value) : null;
            }
        }

        /** Specialization of {@code FindSink} for int streams */
        static final class OfInt extends java.util.stream.FindOps.FindSink<Integer, OptionalInt>
                implements java.util.stream.Sink.OfInt {
            @Override
            public void accept(int value) {
                // Boxing is OK here, since few values will actually flow into the sink
                accept((Integer) value);
            }

            @Override
            public OptionalInt get() {
                return hasValue ? OptionalInt.of(value) : null;
            }
        }

        /** Specialization of {@code FindSink} for long streams */
        static final class OfLong extends java.util.stream.FindOps.FindSink<Long, OptionalLong>
                implements java.util.stream.Sink.OfLong {
            @Override
            public void accept(long value) {
                // Boxing is OK here, since few values will actually flow into the sink
                accept((Long) value);
            }

            @Override
            public OptionalLong get() {
                return hasValue ? OptionalLong.of(value) : null;
            }
        }

        /** Specialization of {@code FindSink} for double streams */
        static final class OfDouble extends java.util.stream.FindOps.FindSink<Double, OptionalDouble>
                implements java.util.stream.Sink.OfDouble {
            @Override
            public void accept(double value) {
                // Boxing is OK here, since few values will actually flow into the sink
                accept((Double) value);
            }

            @Override
            public OptionalDouble get() {
                return hasValue ? OptionalDouble.of(value) : null;
            }
        }
    }

    /**
     * {@code ForkJoinTask} implementing parallel short-circuiting search
     * @param <P_IN> Input element type to the stream pipeline
     * @param <P_OUT> Output element type from the stream pipeline
     * @param <O> Result type from the find operation
     */
    @SuppressWarnings("serial")
    private static final class FindTask<P_IN, P_OUT, O>
            extends java.util.stream.AbstractShortCircuitTask<P_IN, P_OUT, O, FindTask<P_IN, P_OUT, O>> {
        private final FindOp<P_OUT, O> op;

        FindTask(FindOp<P_OUT, O> op,
                 java.util.stream.PipelineHelper<P_OUT> helper,
                 Spliterator<P_IN> spliterator) {
            super(helper, spliterator);
            this.op = op;
        }

        FindTask(FindTask<P_IN, P_OUT, O> parent, Spliterator<P_IN> spliterator) {
            super(parent, spliterator);
            this.op = parent.op;
        }

        @Override
        protected FindTask<P_IN, P_OUT, O> makeChild(Spliterator<P_IN> spliterator) {
            return new FindTask<>(this, spliterator);
        }

        @Override
        protected O getEmptyResult() {
            return op.emptyValue;
        }

        private void foundResult(O answer) {
            if (isLeftmostNode())
                shortCircuit(answer);
            else
                cancelLaterNodes();
        }

        @Override
        protected O doLeaf() {
            O result = helper.wrapAndCopyInto(op.sinkSupplier.get(), spliterator).get();
            if (!op.mustFindFirst) {
                if (result != null)
                    shortCircuit(result);
                return null;
            }
            else {
                if (result != null) {
                    foundResult(result);
                    return result;
                }
                else
                    return null;
            }
        }

        @Override
        public void onCompletion(CountedCompleter<?> caller) {
            if (op.mustFindFirst) {
                    for (FindTask<P_IN, P_OUT, O> child = leftChild, p = null; child != p;
                         p = child, child = rightChild) {
                    O result = child.getLocalResult();
                    if (result != null && op.presentPredicate.test(result)) {
                        setLocalResult(result);
                        foundResult(result);
                        break;
                    }
                }
            }
            super.onCompletion(caller);
        }
    }
}

