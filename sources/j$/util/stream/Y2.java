package j$.util.stream;

import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import j$.util.k;
import java.util.Comparator;

final class Y2<T> implements Spliterator<T>, Consumer<T> {
    private static final Object d = new Object();

    /* renamed from: a  reason: collision with root package name */
    private final Spliterator var_a;
    private final ConcurrentHashMap b;
    private Object c;

    Y2(Spliterator spliterator) {
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        this.var_a = spliterator;
        this.b = concurrentHashMap;
    }

    private Y2(Spliterator spliterator, ConcurrentHashMap concurrentHashMap) {
        this.var_a = spliterator;
        this.b = concurrentHashMap;
    }

    public void accept(Object obj) {
        this.c = obj;
    }

    public boolean b(Consumer consumer) {
        while (this.var_a.b(this)) {
            ConcurrentHashMap concurrentHashMap = this.b;
            Object obj = this.c;
            if (obj == null) {
                obj = d;
            }
            if (concurrentHashMap.putIfAbsent(obj, Boolean.TRUE) == null) {
                consumer.accept(this.c);
                this.c = null;
                return true;
            }
        }
        return false;
    }

    public int characteristics() {
        return (this.var_a.characteristics() & -16469) | 1;
    }

    public long estimateSize() {
        return this.var_a.estimateSize();
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }

    public void forEachRemaining(Consumer consumer) {
        this.var_a.forEachRemaining(new CLASSNAMEx0(this, consumer));
    }

    public void g(Consumer consumer, Object obj) {
        if (this.b.putIfAbsent(obj != null ? obj : d, Boolean.TRUE) == null) {
            consumer.accept(obj);
        }
    }

    public Comparator getComparator() {
        return this.var_a.getComparator();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return k.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return k.f(this, i);
    }

    public Spliterator trySplit() {
        Spliterator trySplit = this.var_a.trySplit();
        if (trySplit != null) {
            return new Y2(trySplit, this.b);
        }
        return null;
    }
}
