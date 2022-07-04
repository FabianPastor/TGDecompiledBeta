package j$.util.concurrent.atomic;

import j$.util.function.LongBinaryOperator;
import j$.util.function.LongUnaryOperator;
import java.util.concurrent.atomic.AtomicLong;

public class DesugarAtomicLong {
    private DesugarAtomicLong() {
    }

    public static long getAndUpdate(AtomicLong atomic, LongUnaryOperator updateFunction) {
        long prev;
        do {
            prev = atomic.get();
        } while (!atomic.compareAndSet(prev, updateFunction.applyAsLong(prev)));
        return prev;
    }

    public static long updateAndGet(AtomicLong atomic, LongUnaryOperator updateFunction) {
        long prev;
        long next;
        do {
            prev = atomic.get();
            next = updateFunction.applyAsLong(prev);
        } while (!atomic.compareAndSet(prev, next));
        return next;
    }

    public static long getAndAccumulate(AtomicLong atomic, long x, LongBinaryOperator accumulatorFunction) {
        long prev;
        do {
            prev = atomic.get();
        } while (!atomic.compareAndSet(prev, accumulatorFunction.applyAsLong(prev, x)));
        return prev;
    }

    public static long accumulateAndGet(AtomicLong atomic, long x, LongBinaryOperator accumulatorFunction) {
        long prev;
        long next;
        do {
            prev = atomic.get();
            next = accumulatorFunction.applyAsLong(prev, x);
        } while (!atomic.compareAndSet(prev, next));
        return next;
    }
}
