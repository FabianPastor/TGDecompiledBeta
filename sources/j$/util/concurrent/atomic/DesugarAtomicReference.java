package j$.util.concurrent.atomic;

import j$.util.function.BinaryOperator;
import j$.util.function.UnaryOperator;
import java.util.concurrent.atomic.AtomicReference;

public class DesugarAtomicReference {
    private DesugarAtomicReference() {
    }

    public static <V> V getAndUpdate(AtomicReference<V> atomic, UnaryOperator<V> unaryOperator) {
        V prev;
        do {
            prev = atomic.get();
        } while (!atomic.compareAndSet(prev, unaryOperator.apply(prev)));
        return prev;
    }

    public static <V> V updateAndGet(AtomicReference<V> atomic, UnaryOperator<V> unaryOperator) {
        V prev;
        V next;
        do {
            prev = atomic.get();
            next = unaryOperator.apply(prev);
        } while (!atomic.compareAndSet(prev, next));
        return next;
    }

    public static <V> V getAndAccumulate(AtomicReference<V> atomic, V x, BinaryOperator<V> binaryOperator) {
        V prev;
        do {
            prev = atomic.get();
        } while (!atomic.compareAndSet(prev, binaryOperator.apply(prev, x)));
        return prev;
    }

    public static <V> V accumulateAndGet(AtomicReference<V> atomic, V x, BinaryOperator<V> binaryOperator) {
        V prev;
        V next;
        do {
            prev = atomic.get();
            next = binaryOperator.apply(prev, x);
        } while (!atomic.compareAndSet(prev, next));
        return next;
    }
}
