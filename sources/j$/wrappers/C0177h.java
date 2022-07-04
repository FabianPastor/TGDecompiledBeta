package j$.wrappers;

import j$.util.u;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

/* renamed from: j$.wrappers.h  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEh implements Spliterator {
    final /* synthetic */ u a;

    private /* synthetic */ CLASSNAMEh(u uVar) {
        this.a = uVar;
    }

    public static /* synthetic */ Spliterator a(u uVar) {
        if (uVar == null) {
            return null;
        }
        return uVar instanceof CLASSNAMEg ? ((CLASSNAMEg) uVar).a : new CLASSNAMEh(uVar);
    }

    public /* synthetic */ int characteristics() {
        return this.a.characteristics();
    }

    public /* synthetic */ long estimateSize() {
        return this.a.estimateSize();
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEw.b(consumer));
    }

    public /* synthetic */ Comparator getComparator() {
        return this.a.getComparator();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return this.a.getExactSizeIfKnown();
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return this.a.hasCharacteristics(i);
    }

    public /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.a.b(CLASSNAMEw.b(consumer));
    }

    public /* synthetic */ Spliterator trySplit() {
        return a(this.a.trySplit());
    }
}
