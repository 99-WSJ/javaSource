/*
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

/*
 *
 *
 *
 *
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java8.java.util.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A stage of a possibly asynchronous computation, that performs an
 * action or computes a value when another CompletionStage completes.
 * A stage completes upon termination of its computation, but this may
 * in turn trigger other dependent stages.  The functionality defined
 * in this interface takes only a few basic forms, which expand out to
 * a larger set of methods to capture a range of usage styles: <ul>
 *
 * <li>The computation performed by a stage may be expressed as a
 * Function, Consumer, or Runnable (using methods with names including
 * <em>apply</em>, <em>accept</em>, or <em>run</em>, respectively)
 * depending on whether it requires arguments and/or produces results.
 * For example, {@code stage.thenApply(x -> square(x)).thenAccept(x ->
 * System.out.print(x)).thenRun(() -> System.out.println())}. An
 * additional form (<em>compose</em>) applies functions of stages
 * themselves, rather than their results. </li>
 *
 * <li> One stage's execution may be triggered by completion of a
 * single stage, or both of two stages, or either of two stages.
 * Dependencies on a single stage are arranged using methods with
 * prefix <em>then</em>. Those triggered by completion of
 * <em>both</em> of two stages may <em>combine</em> their results or
 * effects, using correspondingly named methods. Those triggered by
 * <em>either</em> of two stages make no guarantees about which of the
 * results or effects are used for the dependent stage's
 * computation.</li>
 *
 * <li> Dependencies among stages control the triggering of
 * computations, but do not otherwise guarantee any particular
 * ordering. Additionally, execution of a new stage's computations may
 * be arranged in any of three ways: default execution, default
 * asynchronous execution (using methods with suffix <em>async</em>
 * that employ the stage's default asynchronous execution facility),
 * or custom (via a supplied {@link Executor}).  The execution
 * properties of default and async modes are specified by
 * CompletionStage implementations, not this interface. Methods with
 * explicit Executor arguments may have arbitrary execution
 * properties, and might not even support concurrent execution, but
 * are arranged for processing in a way that accommodates asynchrony.
 *
 * <li> Two method forms support processing whether the triggering
 * stage completed normally or exceptionally: Method {@link
 * #whenComplete whenComplete} allows injection of an action
 * regardless of outcome, otherwise preserving the outcome in its
 * completion. Method {@link #handle handle} additionally allows the
 * stage to compute a replacement result that may enable further
 * processing by other dependent stages.  In all other cases, if a
 * stage's computation terminates abruptly with an (unchecked)
 * exception or error, then all dependent stages requiring its
 * completion complete exceptionally as well, with a {@link
 * CompletionException} holding the exception as its cause.  If a
 * stage is dependent on <em>both</em> of two stages, and both
 * complete exceptionally, then the CompletionException may correspond
 * to either one of these exceptions.  If a stage is dependent on
 * <em>either</em> of two others, and only one of them completes
 * exceptionally, no guarantees are made about whether the dependent
 * stage completes normally or exceptionally. In the case of method
 * {@code whenComplete}, when the supplied action itself encounters an
 * exception, then the stage exceptionally completes with this
 * exception if not already completed exceptionally.</li>
 *
 * </ul>
 *
 * <p>All methods adhere to the above triggering, execution, and
 * exceptional completion specifications (which are not repeated in
 * individual method specifications). Additionally, while arguments
 * used to pass a completion result (that is, for parameters of type
 * {@code T}) for methods accepting them may be null, passing a null
 * value for any other parameter will result in a {@link
 * NullPointerException} being thrown.
 *
 * <p>This interface does not define methods for initially creating,
 * forcibly completing normally or exceptionally, probing completion
 * status or results, or awaiting completion of a stage.
 * Implementations of CompletionStage may provide means of achieving
 * such effects, as appropriate.  Method {@link #toCompletableFuture}
 * enables interoperability among different implementations of this
 * interface by providing a common conversion type.
 *
 * @author Doug Lea
 * @since 1.8
 */
public interface CompletionStage<T> {

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed with this stage's result as the argument
     * to the supplied function.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param fn the function to use to compute the value of
     * the returned CompletionStage
     * @param <U> the function's return type
     * @return the new CompletionStage
     */
    public <U> java.util.concurrent.CompletionStage<U> thenApply(Function<? super T,? extends U> fn);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed using this stage's default asynchronous
     * execution facility, with this stage's result as the argument to
     * the supplied function.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param fn the function to use to compute the value of
     * the returned CompletionStage
     * @param <U> the function's return type
     * @return the new CompletionStage
     */
    public <U> java.util.concurrent.CompletionStage<U> thenApplyAsync
        (Function<? super T,? extends U> fn);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed using the supplied Executor, with this
     * stage's result as the argument to the supplied function.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param fn the function to use to compute the value of
     * the returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @param <U> the function's return type
     * @return the new CompletionStage
     */
    public <U> java.util.concurrent.CompletionStage<U> thenApplyAsync
        (Function<? super T,? extends U> fn,
         Executor executor);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed with this stage's result as the argument
     * to the supplied action.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<Void> thenAccept(Consumer<? super T> action);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed using this stage's default asynchronous
     * execution facility, with this stage's result as the argument to
     * the supplied action.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed using the supplied Executor, with this
     * stage's result as the argument to the supplied action.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action,
                                                                      Executor executor);
    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, executes the given action.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<Void> thenRun(Runnable action);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, executes the given action using this stage's default
     * asynchronous execution facility.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<Void> thenRunAsync(Runnable action);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, executes the given action using the supplied Executor.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<Void> thenRunAsync(Runnable action,
                                                                   Executor executor);

    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage both complete normally, is executed with the two
     * results as arguments to the supplied function.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param fn the function to use to compute the value of
     * the returned CompletionStage
     * @param <U> the type of the other CompletionStage's result
     * @param <V> the function's return type
     * @return the new CompletionStage
     */
    public <U,V> java.util.concurrent.CompletionStage<V> thenCombine
        (java.util.concurrent.CompletionStage<? extends U> other,
         BiFunction<? super T,? super U,? extends V> fn);

    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage complete normally, is executed using this stage's
     * default asynchronous execution facility, with the two results
     * as arguments to the supplied function.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param fn the function to use to compute the value of
     * the returned CompletionStage
     * @param <U> the type of the other CompletionStage's result
     * @param <V> the function's return type
     * @return the new CompletionStage
     */
    public <U,V> java.util.concurrent.CompletionStage<V> thenCombineAsync
        (java.util.concurrent.CompletionStage<? extends U> other,
         BiFunction<? super T,? super U,? extends V> fn);

    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage complete normally, is executed using the supplied
     * executor, with the two results as arguments to the supplied
     * function.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param fn the function to use to compute the value of
     * the returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @param <U> the type of the other CompletionStage's result
     * @param <V> the function's return type
     * @return the new CompletionStage
     */
    public <U,V> java.util.concurrent.CompletionStage<V> thenCombineAsync
        (java.util.concurrent.CompletionStage<? extends U> other,
         BiFunction<? super T,? super U,? extends V> fn,
         Executor executor);

    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage both complete normally, is executed with the two
     * results as arguments to the supplied action.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @param <U> the type of the other CompletionStage's result
     * @return the new CompletionStage
     */
    public <U> java.util.concurrent.CompletionStage<Void> thenAcceptBoth
        (java.util.concurrent.CompletionStage<? extends U> other,
         BiConsumer<? super T, ? super U> action);

    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage complete normally, is executed using this stage's
     * default asynchronous execution facility, with the two results
     * as arguments to the supplied action.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @param <U> the type of the other CompletionStage's result
     * @return the new CompletionStage
     */
    public <U> java.util.concurrent.CompletionStage<Void> thenAcceptBothAsync
        (java.util.concurrent.CompletionStage<? extends U> other,
         BiConsumer<? super T, ? super U> action);

    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage complete normally, is executed using the supplied
     * executor, with the two results as arguments to the supplied
     * function.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @param <U> the type of the other CompletionStage's result
     * @return the new CompletionStage
     */
    public <U> java.util.concurrent.CompletionStage<Void> thenAcceptBothAsync
        (java.util.concurrent.CompletionStage<? extends U> other,
         BiConsumer<? super T, ? super U> action,
         Executor executor);

    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage both complete normally, executes the given action.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<Void> runAfterBoth(java.util.concurrent.CompletionStage<?> other,
                                                                   Runnable action);
    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage complete normally, executes the given action using
     * this stage's default asynchronous execution facility.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<Void> runAfterBothAsync(java.util.concurrent.CompletionStage<?> other,
                                                                        Runnable action);

    /**
     * Returns a new CompletionStage that, when this and the other
     * given stage complete normally, executes the given action using
     * the supplied executor
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<Void> runAfterBothAsync(java.util.concurrent.CompletionStage<?> other,
                                                                        Runnable action,
                                                                        Executor executor);
    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, is executed with the
     * corresponding result as argument to the supplied function.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param fn the function to use to compute the value of
     * the returned CompletionStage
     * @param <U> the function's return type
     * @return the new CompletionStage
     */
    public <U> java.util.concurrent.CompletionStage<U> applyToEither
        (java.util.concurrent.CompletionStage<? extends T> other,
         Function<? super T, U> fn);

    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, is executed using this
     * stage's default asynchronous execution facility, with the
     * corresponding result as argument to the supplied function.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param fn the function to use to compute the value of
     * the returned CompletionStage
     * @param <U> the function's return type
     * @return the new CompletionStage
     */
    public <U> java.util.concurrent.CompletionStage<U> applyToEitherAsync
        (java.util.concurrent.CompletionStage<? extends T> other,
         Function<? super T, U> fn);

    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, is executed using the
     * supplied executor, with the corresponding result as argument to
     * the supplied function.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param fn the function to use to compute the value of
     * the returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @param <U> the function's return type
     * @return the new CompletionStage
     */
    public <U> java.util.concurrent.CompletionStage<U> applyToEitherAsync
        (java.util.concurrent.CompletionStage<? extends T> other,
         Function<? super T, U> fn,
         Executor executor);

    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, is executed with the
     * corresponding result as argument to the supplied action.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<Void> acceptEither
        (java.util.concurrent.CompletionStage<? extends T> other,
         Consumer<? super T> action);

    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, is executed using this
     * stage's default asynchronous execution facility, with the
     * corresponding result as argument to the supplied action.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<Void> acceptEitherAsync
        (java.util.concurrent.CompletionStage<? extends T> other,
         Consumer<? super T> action);

    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, is executed using the
     * supplied executor, with the corresponding result as argument to
     * the supplied function.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<Void> acceptEitherAsync
        (java.util.concurrent.CompletionStage<? extends T> other,
         Consumer<? super T> action,
         Executor executor);

    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, executes the given action.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<Void> runAfterEither(java.util.concurrent.CompletionStage<?> other,
                                                                     Runnable action);

    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, executes the given action
     * using this stage's default asynchronous execution facility.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<Void> runAfterEitherAsync
        (java.util.concurrent.CompletionStage<?> other,
         Runnable action);

    /**
     * Returns a new CompletionStage that, when either this or the
     * other given stage complete normally, executes the given action
     * using supplied executor.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param action the action to perform before completing the
     * returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<Void> runAfterEitherAsync
        (java.util.concurrent.CompletionStage<?> other,
         Runnable action,
         Executor executor);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed with this stage as the argument
     * to the supplied function.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param fn the function returning a new CompletionStage
     * @param <U> the type of the returned CompletionStage's result
     * @return the CompletionStage
     */
    public <U> java.util.concurrent.CompletionStage<U> thenCompose
        (Function<? super T, ? extends java.util.concurrent.CompletionStage<U>> fn);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed using this stage's default asynchronous
     * execution facility, with this stage as the argument to the
     * supplied function.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param fn the function returning a new CompletionStage
     * @param <U> the type of the returned CompletionStage's result
     * @return the CompletionStage
     */
    public <U> java.util.concurrent.CompletionStage<U> thenComposeAsync
        (Function<? super T, ? extends java.util.concurrent.CompletionStage<U>> fn);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * normally, is executed using the supplied Executor, with this
     * stage's result as the argument to the supplied function.
     *
     * See the {@link java.util.concurrent.CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param fn the function returning a new CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @param <U> the type of the returned CompletionStage's result
     * @return the CompletionStage
     */
    public <U> java.util.concurrent.CompletionStage<U> thenComposeAsync
        (Function<? super T, ? extends java.util.concurrent.CompletionStage<U>> fn,
         Executor executor);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * exceptionally, is executed with this stage's exception as the
     * argument to the supplied function.  Otherwise, if this stage
     * completes normally, then the returned stage also completes
     * normally with the same value.
     *
     * @param fn the function to use to compute the value of the
     * returned CompletionStage if this CompletionStage completed
     * exceptionally
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<T> exceptionally
        (Function<Throwable, ? extends T> fn);

    /**
     * Returns a new CompletionStage with the same result or exception
     * as this stage, and when this stage completes, executes the
     * given action with the result (or {@code null} if none) and the
     * exception (or {@code null} if none) of this stage.
     *
     * @param action the action to perform
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<T> whenComplete
        (BiConsumer<? super T, ? super Throwable> action);

    /**
     * Returns a new CompletionStage with the same result or exception
     * as this stage, and when this stage completes, executes the
     * given action executes the given action using this stage's
     * default asynchronous execution facility, with the result (or
     * {@code null} if none) and the exception (or {@code null} if
     * none) of this stage as arguments.
     *
     * @param action the action to perform
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<T> whenCompleteAsync
        (BiConsumer<? super T, ? super Throwable> action);

    /**
     * Returns a new CompletionStage with the same result or exception
     * as this stage, and when this stage completes, executes using
     * the supplied Executor, the given action with the result (or
     * {@code null} if none) and the exception (or {@code null} if
     * none) of this stage as arguments.
     *
     * @param action the action to perform
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletionStage
     */
    public java.util.concurrent.CompletionStage<T> whenCompleteAsync
        (BiConsumer<? super T, ? super Throwable> action,
         Executor executor);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * either normally or exceptionally, is executed with this stage's
     * result and exception as arguments to the supplied function.
     * The given function is invoked with the result (or {@code null}
     * if none) and the exception (or {@code null} if none) of this
     * stage when complete as arguments.
     *
     * @param fn the function to use to compute the value of the
     * returned CompletionStage
     * @param <U> the function's return type
     * @return the new CompletionStage
     */
    public <U> java.util.concurrent.CompletionStage<U> handle
        (BiFunction<? super T, Throwable, ? extends U> fn);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * either normally or exceptionally, is executed using this stage's
     * default asynchronous execution facility, with this stage's
     * result and exception as arguments to the supplied function.
     * The given function is invoked with the result (or {@code null}
     * if none) and the exception (or {@code null} if none) of this
     * stage when complete as arguments.
     *
     * @param fn the function to use to compute the value of the
     * returned CompletionStage
     * @param <U> the function's return type
     * @return the new CompletionStage
     */
    public <U> java.util.concurrent.CompletionStage<U> handleAsync
        (BiFunction<? super T, Throwable, ? extends U> fn);

    /**
     * Returns a new CompletionStage that, when this stage completes
     * either normally or exceptionally, is executed using the
     * supplied executor, with this stage's result and exception as
     * arguments to the supplied function.  The given function is
     * invoked with the result (or {@code null} if none) and the
     * exception (or {@code null} if none) of this stage when complete
     * as arguments.
     *
     * @param fn the function to use to compute the value of the
     * returned CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @param <U> the function's return type
     * @return the new CompletionStage
     */
    public <U> java.util.concurrent.CompletionStage<U> handleAsync
        (BiFunction<? super T, Throwable, ? extends U> fn,
         Executor executor);

    /**
     * Returns a {@link CompletableFuture} maintaining the same
     * completion properties as this stage. If this stage is already a
     * CompletableFuture, this method may return this stage itself.
     * Otherwise, invocation of this method may be equivalent in
     * effect to {@code thenApply(x -> x)}, but returning an instance
     * of type {@code CompletableFuture}. A CompletionStage
     * implementation that does not choose to interoperate with others
     * may throw {@code UnsupportedOperationException}.
     *
     * @return the CompletableFuture
     * @throws UnsupportedOperationException if this implementation
     * does not interoperate with CompletableFuture
     */
    public CompletableFuture<T> toCompletableFuture();

}
