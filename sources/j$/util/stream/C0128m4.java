package j$.util.stream;

import j$.util.AbstractCLASSNAMEa;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.Consumer;
import java.util.Comparator;
/* renamed from: j$.util.stream.m4  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
final class CLASSNAMEm4 implements j$.util.u, Consumer {
    private static final Object d = new Object();
    private final j$.util.u a;
    private final ConcurrentHashMap b;
    private Object c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CLASSNAMEm4(j$.util.u uVar) {
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        this.a = uVar;
        this.b = concurrentHashMap;
    }

    private CLASSNAMEm4(j$.util.u uVar, ConcurrentHashMap concurrentHashMap) {
        this.a = uVar;
        this.b = concurrentHashMap;
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        this.c = obj;
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    @Override // j$.util.u
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

    @Override // j$.util.u
    public int characteristics() {
        return (this.a.characteristics() & (-16469)) | 1;
    }

    @Override // j$.util.u
    public long estimateSize() {
        return this.a.estimateSize();
    }

    public void f(Consumer consumer, Object obj) {
        if (this.b.putIfAbsent(obj != null ? obj : d, Boolean.TRUE) == null) {
            consumer.accept(obj);
        }
    }

    @Override // j$.util.u
    public void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(new CLASSNAMEo(this, consumer));
    }

    @Override // j$.util.u
    public Comparator getComparator() {
        return this.a.getComparator();
    }

    @Override // j$.util.u
    public /* synthetic */ long getExactSizeIfKnown() {
        return AbstractCLASSNAMEa.e(this);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean hasCharacteristics(int i) {
        return AbstractCLASSNAMEa.f(this, i);
    }

    @Override // j$.util.u
    /* renamed from: trySplit */
    public j$.util.u moNUMtrySplit() {
        j$.util.u moNUMtrySplit = this.a.moNUMtrySplit();
        if (moNUMtrySplit != null) {
            return new CLASSNAMEm4(moNUMtrySplit, this.b);
        }
        return null;
    }
}
