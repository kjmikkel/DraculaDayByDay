package com.jensen.draculadaybyday.primitives;

public class Tuple<T, S> {

    public final T fst;
    public final S snd;

    public Tuple(T fst, S snd) {
        this.fst = fst;
        this.snd = snd;
    }
}
