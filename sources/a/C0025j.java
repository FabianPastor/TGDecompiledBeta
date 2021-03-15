package a;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import java.util.Comparator;

/* renamed from: a.j  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEj implements Spliterator {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ java.util.Spliterator var_a;

    private /* synthetic */ CLASSNAMEj(java.util.Spliterator spliterator) {
        this.var_a = spliterator;
    }

    public static /* synthetic */ Spliterator a(java.util.Spliterator spliterator) {
        if (spliterator == null) {
            return null;
        }
        return spliterator instanceof CLASSNAMEk ? ((CLASSNAMEk) spliterator).var_a : new CLASSNAMEj(spliterator);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return this.var_a.tryAdvance(A.a(consumer));
    }

    public /* synthetic */ int characteristics() {
        return this.var_a.characteristics();
    }

    public /* synthetic */ long estimateSize() {
        return this.var_a.estimateSize();
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.var_a.forEachRemaining(A.a(consumer));
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

    public /* synthetic */ Spliterator trySplit() {
        return a(this.var_a.trySplit());
    }
}
