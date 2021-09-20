package j$.wrappers;

import j$.util.y;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

/* renamed from: j$.wrappers.h  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEh implements Spliterator {
    final /* synthetic */ y a;

    private /* synthetic */ CLASSNAMEh(y yVar) {
        this.a = yVar;
    }

    public static /* synthetic */ Spliterator a(y yVar) {
        if (yVar == null) {
            return null;
        }
        return yVar instanceof CLASSNAMEg ? ((CLASSNAMEg) yVar).a : new CLASSNAMEh(yVar);
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
