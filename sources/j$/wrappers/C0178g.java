package j$.wrappers;

import j$.util.function.Consumer;
import j$.util.y;
import java.util.Comparator;
import java.util.Spliterator;

/* renamed from: j$.wrappers.g  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEg implements y {
    final /* synthetic */ Spliterator a;

    private /* synthetic */ CLASSNAMEg(Spliterator spliterator) {
        this.a = spliterator;
    }

    public static /* synthetic */ y a(Spliterator spliterator) {
        if (spliterator == null) {
            return null;
        }
        return spliterator instanceof CLASSNAMEh ? ((CLASSNAMEh) spliterator).a : new CLASSNAMEg(spliterator);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return this.a.tryAdvance(CLASSNAMEx.a(consumer));
    }

    public /* synthetic */ int characteristics() {
        return this.a.characteristics();
    }

    public /* synthetic */ long estimateSize() {
        return this.a.estimateSize();
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEx.a(consumer));
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

    public /* synthetic */ y trySplit() {
        return a(this.a.trySplit());
    }
}
