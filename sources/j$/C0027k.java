package j$;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

/* renamed from: j$.k  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEk implements Spliterator {
    final /* synthetic */ j$.util.Spliterator a;

    private /* synthetic */ CLASSNAMEk(j$.util.Spliterator spliterator) {
        this.a = spliterator;
    }

    public static /* synthetic */ Spliterator a(j$.util.Spliterator spliterator) {
        if (spliterator == null) {
            return null;
        }
        return spliterator instanceof CLASSNAMEj ? ((CLASSNAMEj) spliterator).a : new CLASSNAMEk(spliterator);
    }

    public /* synthetic */ int characteristics() {
        return this.a.characteristics();
    }

    public /* synthetic */ long estimateSize() {
        return this.a.estimateSize();
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEz.b(consumer));
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
        return this.a.b(CLASSNAMEz.b(consumer));
    }

    public /* synthetic */ Spliterator trySplit() {
        return a(this.a.trySplit());
    }
}
