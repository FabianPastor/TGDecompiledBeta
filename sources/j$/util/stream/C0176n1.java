package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.E;
import j$.util.function.Function;
import j$.util.function.n;
import java.util.Set;

/* renamed from: j$.util.stream.n1  reason: case insensitive filesystem */
public interface CLASSNAMEn1 {
    BiConsumer accumulator();

    Set characteristics();

    n combiner();

    Function finisher();

    E supplier();
}
