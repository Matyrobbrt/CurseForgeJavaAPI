package io.github.matyrobbrt.curseforgeapi.util;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import io.github.matyrobbrt.curseforgeapi.annotation.Nonnull;
import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;
import io.github.matyrobbrt.curseforgeapi.annotation.ParametersAreNonnullByDefault;
import io.github.matyrobbrt.curseforgeapi.request.AsyncRequest;
import io.github.matyrobbrt.curseforgeapi.request.Response;

/**
 * A pair of elements.
 *
 * <p>
 * This record gives access to a pair of elements <var>F</var>, <var>S</var>,
 * where <var>F</var> is the {@linkplain #first()} element and <var>S</var> is
 * the {@linkplain #second()} element. Pairs are <b>immutable</b>.
 *
 * <p>
 * Mappers return a new pair instance, and such are chainable:
 *
 * <pre>
 * pair.mapFirst(0).mapSecond(12); // this pair now has the first value 0, and the second one 12
 * </pre>
 *
 * @param <F> the type of the first element.
 * @param <S> the type of the second element.
 */
@ParametersAreNonnullByDefault
public record Pair<F, S> (F first, S second) {

    /**
     * A map containg predicates for checking if the value inside a {@link Pair} is
     * present. Used by {@link Pair#toOptional()} <br>
     * By default, this contains the {@link Optional#isPresent()} and
     * {@link Response#isPresent()} predicates.
     * 
     */
    private static final Map<Class<?>, Predicate<?>> IS_PRESENT_PREDICATES = new HashMap<>();

    static {
        registerIsPresentPredicate(Optional.class, Optional::isPresent);
        registerIsPresentPredicate(Response.class, Response::isPresent);
    }

    /**
     * Register an {@link #IS_PRESENT_PREDICATES "Is Present"} predicate for the
     * specified class. <br>
     * 
     * @param <T>       the type to register a predicate for
     * @param clazz     the class of the object to register the predicate for
     * @param predicate the predicate to register
     */
    public static <T> void registerIsPresentPredicate(Class<T> clazz, Predicate<T> predicate) {
        IS_PRESENT_PREDICATES.put(clazz, getIsPresentPredicate(clazz).and(predicate));
    }

    /**
     * Gets the {@link #IS_PRESENT_PREDICATES "Is Present"} predicate for the
     * specified class. <br>
     * The return value is <b>never</b> null, as if the
     * {@link #IS_PRESENT_PREDICATES} doesn't contain an entry for the specified
     * type, a predicate returning {@code true} is returned.
     * 
     * @param  <T>   the type of the object whose predicate to get
     * @param  clazz the class of the object whose predicate to get
     * @return       the predicate
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> getIsPresentPredicate(Class<T> clazz) {
        return (Predicate<T>) IS_PRESENT_PREDICATES.computeIfAbsent(clazz, k -> v -> true);
    }

    /**
     * Makes a pair from the given value
     *
     * @param  first  the first value
     * @param  second the second value
     * @param  <F>    the type of the first value
     * @param  <S>    the type of the second value
     * @return        the newly created pair
     */
    public static <F, S> Pair<F, S> of(@Nullable F first, @Nullable S second) {
        return new Pair<>(first, second);
    }

    /**
     * Makes an empty pair, which has each value {@code null}.
     *
     * @param  <F> the type of the first value
     * @param  <S> the type of the second value
     * @return     the empty pair.
     */
    public static <F, S> Pair<F, S> empty() {
        return of(null, null);
    }

    //@formatter:off
    /**
     * Maps a pair containing 2 responses into an optional that may have a pair
     * containing the values of both responses, if both <b>are present</b>. <br>
     * 
     * This is especially useful when making multiple requests using
     * {@link AsyncRequest#and(AsyncRequest)} because the values returned by the 2
     * async requests are merged into a pair.
     * 
     * Example:
     * 
     * <pre>
     * {@code
     *     final Response<String> firstResponse = Response.of("I exist", 200);
     *     final Response<Integer> secondResponse = Response.empty(400);
     *     final Pair<Response<String>, Response<Integer>> responsePair = Pair.of(firstResponse, secondResponse);
     *     Pair.mapResponses(responsePair).ifPresent(pair -> pair.accept((string, integerValue) -> {
     *         System.out.println(string + ": " + integerValue);
     *     }));
     * }
     * </pre>
     * 
     * In the example above, the <i>System.out</i> call would not run, because one
     * of the responses is empty.
     * 
     * @param  <F>  the first response type
     * @param  <S>  the second response type
     * @param  pair the pair containing the responses to map
     * @return      the optional
     */
    public static <F, S> Optional<Pair<F, S>> mapResponses(Pair<Response<F>, Response<S>> pair) {
        return pair.toOptional().map(p -> p.mapBoth(Response::get, Response::get));
    }
    //@formatter:on

    /**
     * Accepts the given {@code consumer} on the values from the pair
     *
     * @param  consumer the consumer
     * @return          the pair, for chaining
     */
    public Pair<F, S> accept(BiConsumer<F, S> consumer) {
        consumer.accept(first(), second());
        return this;
    }

    /**
     * Accepts the given {@code consumer} on the first value of the pair.
     *
     * @param  consumer the consumer
     * @return          the pair, for chaining
     */
    public Pair<F, S> acceptFirst(Consumer<F> consumer) {
        consumer.accept(first());
        return this;
    }

    /**
     * Accepts the given {@code consumer} on the second value of the pair.
     *
     * @param  consumer the consumer
     * @return          the pair, for chaining
     */
    public Pair<F, S> acceptSecond(Consumer<S> consumer) {
        consumer.accept(second());
        return this;
    }

    /**
     * Maps the pair to another value
     *
     * @param  mapper the mapper
     * @param  <T>    the return type
     * @return        the mapped value
     */
    public <T> T map(BiFunction<F, S, T> mapper) {
        return mapper.apply(first(), second());
    }

    /**
     * Maps the pair's values to new ones.
     *
     * @param  firstMapper  the mapper to use for mapping the first value
     * @param  secondMapper the mapper to use for mapping the second value
     * @param  <NEW_FIRST>  the new first value type
     * @param  <NEW_SECOND> the new first value type
     * @return              the mapped pair
     */
    public <NEW_FIRST, NEW_SECOND> Pair<NEW_FIRST, NEW_SECOND> mapBoth(Function<F, NEW_FIRST> firstMapper,
        Function<S, NEW_SECOND> secondMapper) {
        return of(firstMapper.apply(first()), secondMapper.apply(second()));
    }

    /**
     * Maps the first value to another value.
     *
     * @param  mapper      the mapper
     * @param  <NEW_FIRST> the new first value type
     * @return             the mapped pair
     */
    public <NEW_FIRST> Pair<NEW_FIRST, S> mapFirst(Function<F, NEW_FIRST> mapper) {
        return of(mapper.apply(first()), second());
    }

    /**
     * Maps the first value to another value.
     *
     * @param  newFirst    the new first value
     * @param  <NEW_FIRST> the new first value type
     * @return             the mapped pair
     */
    public <NEW_FIRST> Pair<NEW_FIRST, S> mapFirst(NEW_FIRST newFirst) {
        return of(newFirst, second());
    }

    /**
     * Maps the second value to another value.
     *
     * @param  mapper       the mapper
     * @param  <NEW_SECOND> the new first value type
     * @return              the mapped pair
     */
    public <NEW_SECOND> Pair<F, NEW_SECOND> mapSecond(Function<S, NEW_SECOND> mapper) {
        return of(first(), mapper.apply(second()));
    }

    /**
     * Maps the second value to another value.
     *
     * @param  newSecond    the new second value
     * @param  <NEW_SECOND> the new first value type
     * @return              the mapped pair
     */
    public <NEW_SECOND> Pair<F, NEW_SECOND> mapSecond(NEW_SECOND newSecond) {
        return of(first(), newSecond);
    }

    /**
     * Maps this pair to a {@link java.util.AbstractMap.SimpleImmutableEntry}
     * containing the pair's values in a First-Second order.
     *
     * @return the entry.
     */
    public Map.Entry<F, S> toEntryFS() {
        return new AbstractMap.SimpleImmutableEntry<>(first(), second());
    }

    /**
     * Maps this pair to a {@link java.util.AbstractMap.SimpleImmutableEntry}
     * containing the pair's values in a Second-First order.
     *
     * @return the entry.
     */
    public Map.Entry<S, F> toEntrySF() {
        return new AbstractMap.SimpleImmutableEntry<>(second(), first());
    }

    /**
     * Clears the first value of the pair, by making it {@code null}.
     *
     * @return the new pair
     */
    public Pair<F, S> clearFirst() {
        return of(null, second());
    }

    /**
     * Clears the second value of the pair, by making it {@code null}.
     *
     * @return the new pair
     */
    public Pair<F, S> clearSecond() {
        return of(first(), null);
    }

    /**
     * Swaps the order of the values.
     *
     * @return the swapped pair
     */
    public Pair<S, F> swap() {
        return of(second(), first());
    }

    /**
     * Returns an optional which may contain this pair if the following conditions
     * are met:
     * <ul>
     * <li>neither {@link #first()} nor {@link #second()} are {@code null}
     * <li>if {@link #IS_PRESENT_PREDICATES} contains predicates for either
     * {@link #first()} or {@link #second()}, testing them returns {@code true}
     * </ul>
     * 
     * @return the optional
     */
    public Optional<Pair<F, S>> toOptional() {
        if (first() == null || second() == null) { return Optional.empty(); }
        if (!unsafePredicateTest(getIsPresentPredicate(first().getClass()), first())) { return Optional.empty(); }
        if (!unsafePredicateTest(getIsPresentPredicate(second().getClass()), second())) { return Optional.empty(); }
        return Optional.of(this);
    }

    @SuppressWarnings("unchecked")
    private static <T, Z> boolean unsafePredicateTest(Predicate<Z> predicate, T object) {
        return predicate.test((Z) object);
    }
}
