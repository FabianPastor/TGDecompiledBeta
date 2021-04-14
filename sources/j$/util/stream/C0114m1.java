package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.Function;
import j$.util.function.J;
import j$.util.function.n;
import java.util.Set;

/* renamed from: j$.util.stream.m1  reason: case insensitive filesystem */
public interface CLASSNAMEm1<T, A, R> {

    /* renamed from: j$.util.stream.m1$a */
    public enum a {
        CONCURRENT,
        UNORDERED,
        IDENTITY_FINISH
    }

    BiConsumer accumulator();

    Set characteristics();

    n combiner();

    Function finisher();

    J supplier();
}
