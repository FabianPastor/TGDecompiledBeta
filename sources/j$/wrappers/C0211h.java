package j$.wrappers;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
/* renamed from: j$.wrappers.h */
/* loaded from: classes2.dex */
public final /* synthetic */ class CLASSNAMEh implements Spliterator {
    final /* synthetic */ j$.util.u a;

    private /* synthetic */ CLASSNAMEh(j$.util.u uVar) {
        this.a = uVar;
    }

    public static /* synthetic */ Spliterator a(j$.util.u uVar) {
        if (uVar == null) {
            return null;
        }
        return uVar instanceof CLASSNAMEg ? ((CLASSNAMEg) uVar).a : new CLASSNAMEh(uVar);
    }

    @Override // java.util.Spliterator
    public /* synthetic */ int characteristics() {
        return this.a.characteristics();
    }

    @Override // java.util.Spliterator
    public /* synthetic */ long estimateSize() {
        return this.a.estimateSize();
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

    @Override // java.util.Spliterator
    public /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.a.b(CLASSNAMEw.b(consumer));
    }

    @Override // java.util.Spliterator
    public /* synthetic */ Spliterator trySplit() {
        return a(this.a.moNUMtrySplit());
    }
}
