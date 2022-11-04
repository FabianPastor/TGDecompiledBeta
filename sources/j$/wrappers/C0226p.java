package j$.wrappers;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
/* renamed from: j$.wrappers.p  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEp implements Spliterator.OfPrimitive {
    final /* synthetic */ j$.util.w a;

    private /* synthetic */ CLASSNAMEp(j$.util.w wVar) {
        this.a = wVar;
    }

    public static /* synthetic */ Spliterator.OfPrimitive a(j$.util.w wVar) {
        if (wVar == null) {
            return null;
        }
        return wVar instanceof CLASSNAMEo ? ((CLASSNAMEo) wVar).a : new CLASSNAMEp(wVar);
    }

    @Override // java.util.Spliterator
    public /* synthetic */ int characteristics() {
        return this.a.characteristics();
    }

    @Override // java.util.Spliterator
    public /* synthetic */ long estimateSize() {
        return this.a.estimateSize();
    }

    @Override // java.util.Spliterator.OfPrimitive
    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining(obj);
    }

    @Override // java.util.Spliterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEw.b(consumer));
    }

    @Override // java.util.Spliterator
    public /* synthetic */ Comparator getComparator() {
        return this.a.getComparator();
    }

    @Override // java.util.Spliterator
    public /* synthetic */ long getExactSizeIfKnown() {
        return this.a.getExactSizeIfKnown();
    }

    @Override // java.util.Spliterator
    public /* synthetic */ boolean hasCharacteristics(int i) {
        return this.a.hasCharacteristics(i);
    }

    @Override // java.util.Spliterator.OfPrimitive
    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.a.tryAdvance(obj);
    }

    @Override // java.util.Spliterator
    public /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.a.b(CLASSNAMEw.b(consumer));
    }

    @Override // java.util.Spliterator.OfPrimitive, java.util.Spliterator
    public /* synthetic */ Spliterator.OfPrimitive trySplit() {
        return a(this.a.mo326trySplit());
    }

    @Override // java.util.Spliterator.OfPrimitive, java.util.Spliterator
    /* renamed from: trySplit  reason: collision with other method in class */
    public /* synthetic */ Spliterator mo327trySplit() {
        return CLASSNAMEh.a(this.a.mo326trySplit());
    }
}
