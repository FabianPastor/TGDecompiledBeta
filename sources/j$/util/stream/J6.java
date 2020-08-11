package j$.util.stream;

import j$.util.N;
import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import java.util.Comparator;

final class J6 implements Spliterator, Consumer {
    private static final Object d = new Object();
    private final Spliterator a;
    private final ConcurrentHashMap b;
    private Object c;

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return N.b(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return N.c(this, i);
    }

    J6(Spliterator spliterator) {
        this(spliterator, new ConcurrentHashMap());
    }

    private J6(Spliterator spliterator, ConcurrentHashMap concurrentHashMap) {
        this.a = spliterator;
        this.b = concurrentHashMap;
    }

    public void accept(Object t) {
        this.c = t;
    }

    private Object k(Object t) {
        return t != null ? t : d;
    }

    public boolean a(Consumer consumer) {
        while (this.a.a(this)) {
            if (this.b.putIfAbsent(k(this.c), Boolean.TRUE) == null) {
                consumer.accept(this.c);
                this.c = null;
                return true;
            }
        }
        return false;
    }

    public void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(new CLASSNAMEv0(this, consumer));
    }

    public /* synthetic */ void h(Consumer action, Object t) {
        if (this.b.putIfAbsent(k(t), Boolean.TRUE) == null) {
            action.accept(t);
        }
    }

    public Spliterator trySplit() {
        Spliterator trySplit = this.a.trySplit();
        if (trySplit != null) {
            return new J6(trySplit, this.b);
        }
        return null;
    }

    public long estimateSize() {
        return this.a.estimateSize();
    }

    public int characteristics() {
        return (this.a.characteristics() & -16469) | 1;
    }

    public Comparator getComparator() {
        return this.a.getComparator();
    }
}
