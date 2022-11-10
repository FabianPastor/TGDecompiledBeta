package j$.wrappers;

import j$.util.function.Consumer;
import java.util.Comparator;
import java.util.Spliterator;
/* renamed from: j$.wrappers.o  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEo implements j$.util.w {
    final /* synthetic */ Spliterator.OfPrimitive a;

    private /* synthetic */ CLASSNAMEo(Spliterator.OfPrimitive ofPrimitive) {
        this.a = ofPrimitive;
    }

    public static /* synthetic */ j$.util.w a(Spliterator.OfPrimitive ofPrimitive) {
        if (ofPrimitive == null) {
            return null;
        }
        return ofPrimitive instanceof CLASSNAMEp ? ((CLASSNAMEp) ofPrimitive).a : new CLASSNAMEo(ofPrimitive);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return this.a.tryAdvance(CLASSNAMEx.a(consumer));
    }

    @Override // j$.util.u
    public /* synthetic */ int characteristics() {
        return this.a.characteristics();
    }

    @Override // j$.util.u
    public /* synthetic */ long estimateSize() {
        return this.a.estimateSize();
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEx.a(consumer));
    }

    @Override // j$.util.w
    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining((Spliterator.OfPrimitive) obj);
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

    @Override // j$.util.w
    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.a.tryAdvance((Spliterator.OfPrimitive) obj);
    }

    @Override // j$.util.w, j$.util.u
    /* renamed from: trySplit */
    public /* synthetic */ j$.util.u moNUMtrySplit() {
        return CLASSNAMEg.a(this.a.trySplit());
    }

    @Override // j$.util.w, j$.util.u
    /* renamed from: trySplit  reason: collision with other method in class */
    public /* synthetic */ j$.util.w moNUMtrySplit() {
        return a(this.a.trySplit());
    }
}
