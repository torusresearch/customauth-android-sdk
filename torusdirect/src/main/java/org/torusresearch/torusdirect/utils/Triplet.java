package org.torusresearch.torusdirect.utils;

public class Triplet<A, B, C> {
    public final A first;
    public final B second;
    public final C third;

    public Triplet(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <A, B, C> Triplet<A, B, C> create(final A value0, final B value1, final C value2) {
        return new Triplet<>(value0, value1, value2);
    }
}
