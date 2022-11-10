package j$.wrappers;

import j$.util.function.Consumer;
import java.util.Comparator;
import java.util.Spliterator;
/* renamed from: j$.wrappers.i  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEi implements j$.util.t {
    final /* synthetic */ Spliterator.OfDouble a;

    private /* synthetic */ CLASSNAMEi(Spliterator.OfDouble ofDouble) {
        this.a = ofDouble;
    }

    public static /* synthetic */ j$.util.t a(Spliterator.OfDouble ofDouble) {
        if (ofDouble == null) {
            return null;
        }
        return ofDouble instanceof CLASSNAMEj ? ((CLASSNAMEj) ofDouble).a : new CLASSNAMEi(ofDouble);
    }

    @Override // j$.util.t, j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return this.a.tryAdvance(CLASSNAMEx.a(consumer));
    }

    @Override // j$.util.u
    public /* synthetic */ int characteristics() {
        return this.a.characteristics();
    }

    @Override // j$.util.t
    public /* synthetic */ void e(j$.util.function.f fVar) {
        this.a.forEachRemaining(B.a(fVar));
    }

    @Override // j$.util.u
    public /* synthetic */ long estimateSize() {
        return this.a.estimateSize();
    }

    @Override // j$.util.t, j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEx.a(consumer));
    }

    @Override // j$.util.w
    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining((Spliterator.OfDouble) obj);
    }

    @Override // j$.util.u
    public /* synthetic */ Comparator getComparator() {
        return this.a.getComparator();
    }

    @Override // j$.util.u
    public /* synthetic */ long getExactSizeIfKnown() {
        return this.a.getExactSizeIfKnown();
    }

    @Override // j$.util.u
    public /* synthetic */ boolean hasCharacteristics(int i) {
        return this.a.hasCharacteristics(i);
    }

    @Override // j$.util.t
    public /* synthetic */ boolean k(j$.util.function.f fVar) {
        return this.a.tryAdvance(B.a(fVar));
    }

    @Override // j$.util.w
    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.a.tryAdvance((Spliterator.OfDouble) obj);
    }

    @Override // j$.util.t, j$.util.w, j$.util.u
    /* renamed from: trySplit */
    public /* synthetic */ j$.util.t moNUMtrySplit() {
        return a(this.a.trySplit());
    }

    @Override // j$.util.t, j$.util.w, j$.util.u
    /* renamed from: trySplit  reason: collision with other method in class */
    public /* synthetic */ j$.util.u moNUMtrySplit() {
        return CLASSNAMEg.a(this.a.trySplit());
    }

    @Override // j$.util.t, j$.util.w, j$.util.u
    /* renamed from: trySplit */
    public /* synthetic */ j$.util.w moNUMtrySplit() {
        return CLASSNAMEo.a(this.a.trySplit());
    }
}
