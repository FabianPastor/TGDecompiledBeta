package j$.wrappers;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
/* renamed from: j$.wrappers.n */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEn implements Spliterator.OfLong {
    final /* synthetic */ j$.util.v a;

    private /* synthetic */ CLASSNAMEn(j$.util.v vVar) {
        this.a = vVar;
    }

    public static /* synthetic */ Spliterator.OfLong a(j$.util.v vVar) {
        if (vVar == null) {
            return null;
        }
        return vVar instanceof CLASSNAMEm ? ((CLASSNAMEm) vVar).a : new CLASSNAMEn(vVar);
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
    public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.a.forEachRemaining(longConsumer);
    }

    @Override // java.util.Spliterator.OfLong, java.util.Spliterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEw.b(consumer));
    }

    @Override // java.util.Spliterator.OfLong
    /* renamed from: forEachRemaining */
    public /* synthetic */ void forEachRemaining2(LongConsumer longConsumer) {
        this.a.d(CLASSNAMEf0.b(longConsumer));
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
    public /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
        return this.a.tryAdvance(longConsumer);
    }

    @Override // java.util.Spliterator.OfLong, java.util.Spliterator
    public /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.a.b(CLASSNAMEw.b(consumer));
    }

    @Override // java.util.Spliterator.OfLong
    /* renamed from: tryAdvance */
    public /* synthetic */ boolean tryAdvance2(LongConsumer longConsumer) {
        return this.a.i(CLASSNAMEf0.b(longConsumer));
    }

    @Override // java.util.Spliterator.OfLong, java.util.Spliterator.OfPrimitive, java.util.Spliterator
    public /* synthetic */ Spliterator.OfLong trySplit() {
        return a(this.a.moNUMtrySplit());
    }

    @Override // java.util.Spliterator.OfLong, java.util.Spliterator.OfPrimitive, java.util.Spliterator
    /* renamed from: trySplit */
    public /* synthetic */ Spliterator.OfPrimitive moNUMtrySplit() {
        return CLASSNAMEp.a(this.a.moNUMtrySplit());
    }

    @Override // java.util.Spliterator.OfLong, java.util.Spliterator.OfPrimitive, java.util.Spliterator
    /* renamed from: trySplit */
    public /* synthetic */ Spliterator moNUMtrySplit() {
        return CLASSNAMEh.a(this.a.moNUMtrySplit());
    }
}
