package j$.wrappers;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
/* renamed from: j$.wrappers.j */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEj implements Spliterator.OfDouble {
    final /* synthetic */ j$.util.t a;

    private /* synthetic */ CLASSNAMEj(j$.util.t tVar) {
        this.a = tVar;
    }

    public static /* synthetic */ Spliterator.OfDouble a(j$.util.t tVar) {
        if (tVar == null) {
            return null;
        }
        return tVar instanceof CLASSNAMEi ? ((CLASSNAMEi) tVar).a : new CLASSNAMEj(tVar);
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
    public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.a.forEachRemaining(doubleConsumer);
    }

    @Override // java.util.Spliterator.OfDouble, java.util.Spliterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEw.b(consumer));
    }

    @Override // java.util.Spliterator.OfDouble
    /* renamed from: forEachRemaining */
    public /* synthetic */ void forEachRemaining2(DoubleConsumer doubleConsumer) {
        this.a.e(A.b(doubleConsumer));
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
    public /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
        return this.a.tryAdvance(doubleConsumer);
    }

    @Override // java.util.Spliterator.OfDouble, java.util.Spliterator
    public /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.a.b(CLASSNAMEw.b(consumer));
    }

    @Override // java.util.Spliterator.OfDouble
    /* renamed from: tryAdvance */
    public /* synthetic */ boolean tryAdvance2(DoubleConsumer doubleConsumer) {
        return this.a.k(A.b(doubleConsumer));
    }

    @Override // java.util.Spliterator.OfDouble, java.util.Spliterator.OfPrimitive, java.util.Spliterator
    public /* synthetic */ Spliterator.OfDouble trySplit() {
        return a(this.a.moNUMtrySplit());
    }

    @Override // java.util.Spliterator.OfDouble, java.util.Spliterator.OfPrimitive, java.util.Spliterator
    /* renamed from: trySplit */
    public /* synthetic */ Spliterator.OfPrimitive moNUMtrySplit() {
        return CLASSNAMEp.a(this.a.moNUMtrySplit());
    }

    @Override // java.util.Spliterator.OfDouble, java.util.Spliterator.OfPrimitive, java.util.Spliterator
    /* renamed from: trySplit */
    public /* synthetic */ Spliterator moNUMtrySplit() {
        return CLASSNAMEh.a(this.a.moNUMtrySplit());
    }
}
