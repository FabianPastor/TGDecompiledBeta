package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.Consumer;
import j$.util.y;
import java.util.Comparator;

/* renamed from: j$.util.stream.n4  reason: case insensitive filesystem */
final class CLASSNAMEn4 implements y, Consumer {
    private static final Object d = new Object();
    private final y a;
    private final ConcurrentHashMap b;
    private Object c;

    CLASSNAMEn4(y yVar) {
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        this.a = yVar;
        this.b = concurrentHashMap;
    }

    private CLASSNAMEn4(y yVar, ConcurrentHashMap concurrentHashMap) {
        this.a = yVar;
        this.b = concurrentHashMap;
    }

    public void accept(Object obj) {
        this.c = obj;
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }

    public boolean b(Consumer consumer) {
        while (this.a.b(this)) {
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
        return (this.a.characteristics() & -16469) | 1;
    }

    public long estimateSize() {
        return this.a.estimateSize();
    }

    public void f(Consumer consumer, Object obj) {
        if (this.b.putIfAbsent(obj != null ? obj : d, Boolean.TRUE) == null) {
            consumer.accept(obj);
        }
    }

    public void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(new CLASSNAMEo(this, consumer));
    }

    public Comparator getComparator() {
        return this.a.getComparator();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return CLASSNAMEa.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEa.f(this, i);
    }

    public y trySplit() {
        y trySplit = this.a.trySplit();
        if (trySplit != null) {
            return new CLASSNAMEn4(trySplit, this.b);
        }
        return null;
    }
}
