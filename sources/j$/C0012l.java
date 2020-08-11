package j$;

import j$.util.U;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* renamed from: j$.l  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEl implements Spliterator.OfLong {
    final /* synthetic */ U a;

    private /* synthetic */ CLASSNAMEl(U u) {
        this.a = u;
    }

    public static /* synthetic */ Spliterator.OfLong a(U u) {
        if (u == null) {
            return null;
        }
        return new CLASSNAMEl(u);
    }

    public /* synthetic */ int characteristics() {
        return this.a.characteristics();
    }

    public /* synthetic */ long estimateSize() {
        return this.a.estimateSize();
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(r.a(consumer));
    }

    public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.a.d(Q.a(longConsumer));
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

    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.a.tryAdvance(obj);
    }

    public /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.a.a(r.a(consumer));
    }

    public /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
        return this.a.i(Q.a(longConsumer));
    }
}
