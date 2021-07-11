package j$.util.stream;

import j$.time.a;
import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.Consumer;
import java.util.Comparator;

final class Y2<T> implements Spliterator<T>, Consumer<T> {
    private static final Object a = new Object();
    private final Spliterator b;
    private final ConcurrentHashMap c;
    private Object d;

    Y2(Spliterator spliterator) {
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        this.b = spliterator;
        this.c = concurrentHashMap;
    }

    private Y2(Spliterator spliterator, ConcurrentHashMap concurrentHashMap) {
        this.b = spliterator;
        this.c = concurrentHashMap;
    }

    public void accept(Object obj) {
        this.d = obj;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public boolean b(Consumer consumer) {
        while (this.b.b(this)) {
            ConcurrentHashMap concurrentHashMap = this.c;
            Object obj = this.d;
            if (obj == null) {
                obj = a;
            }
            if (concurrentHashMap.putIfAbsent(obj, Boolean.TRUE) == null) {
                consumer.accept(this.d);
                this.d = null;
                return true;
            }
        }
        return false;
    }

    public int characteristics() {
        return (this.b.characteristics() & -16469) | 1;
    }

    public long estimateSize() {
        return this.b.estimateSize();
    }

    public void f(Consumer consumer, Object obj) {
        if (this.c.putIfAbsent(obj != null ? obj : a, Boolean.TRUE) == null) {
            consumer.accept(obj);
        }
    }

    public void forEachRemaining(Consumer consumer) {
        this.b.forEachRemaining(new CLASSNAMEx0(this, consumer));
    }

    public Comparator getComparator() {
        return this.b.getComparator();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return a.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return a.f(this, i);
    }

    public Spliterator trySplit() {
        Spliterator trySplit = this.b.trySplit();
        if (trySplit != null) {
            return new Y2(trySplit, this.c);
        }
        return null;
    }
}
