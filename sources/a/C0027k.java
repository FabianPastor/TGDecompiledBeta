package a;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

/* renamed from: a.k  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEk implements Spliterator {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j$.util.Spliterator var_a;

    private /* synthetic */ CLASSNAMEk(j$.util.Spliterator spliterator) {
        this.var_a = spliterator;
    }

    public static /* synthetic */ Spliterator a(j$.util.Spliterator spliterator) {
        if (spliterator == null) {
            return null;
        }
        return spliterator instanceof CLASSNAMEj ? ((CLASSNAMEj) spliterator).var_a : new CLASSNAMEk(spliterator);
    }

    public /* synthetic */ int characteristics() {
        return this.var_a.characteristics();
    }

    public /* synthetic */ long estimateSize() {
        return this.var_a.estimateSize();
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.var_a.forEachRemaining(CLASSNAMEz.b(consumer));
    }

    public /* synthetic */ Comparator getComparator() {
        return this.var_a.getComparator();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return this.var_a.getExactSizeIfKnown();
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return this.var_a.hasCharacteristics(i);
    }

    public /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.var_a.b(CLASSNAMEz.b(consumer));
    }

    public /* synthetic */ Spliterator trySplit() {
        return a(this.var_a.trySplit());
    }
}
