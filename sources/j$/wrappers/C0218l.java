package j$.wrappers;

import j$.util.u;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
/* renamed from: j$.wrappers.l */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEl implements Spliterator.OfInt {
    final /* synthetic */ u.a a;

    private /* synthetic */ CLASSNAMEl(u.a aVar) {
        this.a = aVar;
    }

    public static /* synthetic */ Spliterator.OfInt a(u.a aVar) {
        if (aVar == null) {
            return null;
        }
        return aVar instanceof CLASSNAMEk ? ((CLASSNAMEk) aVar).a : new CLASSNAMEl(aVar);
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
    public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.a.forEachRemaining(intConsumer);
    }

    @Override // java.util.Spliterator.OfInt, java.util.Spliterator
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEw.b(consumer));
    }

    @Override // java.util.Spliterator.OfInt
    /* renamed from: forEachRemaining */
    public /* synthetic */ void forEachRemaining2(IntConsumer intConsumer) {
        this.a.c(Q.b(intConsumer));
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
    public /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
        return this.a.tryAdvance(intConsumer);
    }

    @Override // java.util.Spliterator.OfInt, java.util.Spliterator
    public /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.a.b(CLASSNAMEw.b(consumer));
    }

    @Override // java.util.Spliterator.OfInt
    /* renamed from: tryAdvance */
    public /* synthetic */ boolean tryAdvance2(IntConsumer intConsumer) {
        return this.a.g(Q.b(intConsumer));
    }

    @Override // java.util.Spliterator.OfInt, java.util.Spliterator.OfPrimitive, java.util.Spliterator
    public /* synthetic */ Spliterator.OfInt trySplit() {
        return a(this.a.mo326trySplit());
    }

    @Override // java.util.Spliterator.OfInt, java.util.Spliterator.OfPrimitive, java.util.Spliterator
    /* renamed from: trySplit */
    public /* synthetic */ Spliterator.OfPrimitive mo321trySplit() {
        return CLASSNAMEp.a(this.a.mo326trySplit());
    }

    @Override // java.util.Spliterator.OfInt, java.util.Spliterator.OfPrimitive, java.util.Spliterator
    /* renamed from: trySplit */
    public /* synthetic */ Spliterator mo322trySplit() {
        return CLASSNAMEh.a(this.a.mo326trySplit());
    }
}
