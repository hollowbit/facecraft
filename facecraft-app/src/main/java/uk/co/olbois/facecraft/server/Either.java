/*
 * Copyright (c) 2018 Ian Clement.  All rights reserved.
 */

package uk.co.olbois.facecraft.server;

/**
 * Represents one of two possible options.
 *
 * Create an Either object through its static methods. Ex:
 *
 * Either<String, Integer> e1 = Either.left("abc");
 * Either<String, Integer> e2 = Either.right(1234);
 *
 * @author Ian Clement (ian.clement@johnabbott.qc.ca)
 * @since 2018-02-01
 */
public interface Either<S, T>  {

    enum Type { LEFT, RIGHT }

    /**
     * Determine if the object is a left or right Either.
     * @return
     */
    Type getType();

    /**
     * Get the left value of the either.
     * @return
     */
    S getLeft();

    /**
     * Get the right value of the either.
     * @return
     */
    T getRight();

    /**
     * Generate a left Either object.
     * @param val
     * @param <S>
     * @param <T>
     * @return
     */
    static <S,T> Either<S,T> left(S val) {

        // return an anonymous inner class of the left value
        return new Either<S, T>() {
            @Override
            public Type getType() {
                return Type.LEFT;
            }

            @Override
            public S getLeft() {
                return val;
            }

            @Override
            public T getRight() {
                throw new RuntimeException();
            }
        };

    }

    /**
     * Generate a right Either object
     * @param val
     * @param <S>
     * @param <T>
     * @return
     */
    static <S,T> Either<S,T> right(T val) {

        // return an anonymous inner class of the right value
        return new Either<S, T>() {

            @Override
            public Type getType() {
                return Type.RIGHT;
            }

            @Override
            public S getLeft() {
                throw new RuntimeException();
            }

            @Override
            public T getRight() {
                return val;
            }
        };

    }
}
