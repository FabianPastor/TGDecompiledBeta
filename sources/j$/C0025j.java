package j$;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import java.util.Comparator;

/* renamed from: j$.j  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEj implements Spliterator {
    final /* synthetic */ java.util.Spliterator a;

    private /* synthetic */ CLASSNAMEj(java.util.Spliterator spliterator) {
        this.a = spliterator;
    }

    public static /* synthetic */ Spliterator a(java.util.Spliterator spliterator) {
        if (spliterator == null) {
            return null;
        }
        return spliterator instanceof CLASSNAMEk ? ((CLASSNAMEk) spliterator).a : new CLASSNAMEj(spliterator);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return this.a.tryAdvance(A.a(consumer));
    }

    public /* synthetic */ int characteristics() {
        return this.a.characteristics();
    }

    public /* synthetic */ long estimateSize() {
        return this.a.estimateSize();
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(A.a(consumer));
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

    public /* synthetic */ Spliterator trySplit() {
        return a(this.a.trySplit());
    }
}
