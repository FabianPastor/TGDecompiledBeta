package j$;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

/* renamed from: j$.i  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEi implements Spliterator {
    final /* synthetic */ j$.util.Spliterator a;

    private /* synthetic */ CLASSNAMEi(j$.util.Spliterator spliterator) {
        this.a = spliterator;
    }

    public static /* synthetic */ Spliterator a(j$.util.Spliterator spliterator) {
        if (spliterator == null) {
            return null;
        }
        return new CLASSNAMEi(spliterator);
    }

    public /* synthetic */ int characteristics() {
        return this.a.characteristics();
    }

    public /* synthetic */ long estimateSize() {
        return this.a.estimateSize();
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(r.a(consumer));
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
        return this.a.a(r.a(consumer));
    }

    public /* synthetic */ Spliterator trySplit() {
        return a(this.a.trySplit());
    }
}