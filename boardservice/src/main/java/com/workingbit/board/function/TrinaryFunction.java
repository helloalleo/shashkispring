package com.workingbit.board.function;

/**
 * Created by Aleksey Popryaduhin on 00:17 11/08/2017.
 */
@FunctionalInterface
public interface TrinaryFunction<T> {
    T apply(T a, T b, T c);
}
