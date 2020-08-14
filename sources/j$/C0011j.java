package j$;

import j$.util.P;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* renamed from: j$.j  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEj implements Spliterator.OfDouble {
    final /* synthetic */ P a;

    private /* synthetic */ CLASSNAMEj(P p) {
        this.a = p;
    }

    public static /* synthetic */ Spliterator.OfDouble a(P p) {
        if (p == null) {
            return null;
        }
        return new CLASSNAMEj(p);
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

    public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.a.e(CLASSNAMEu.a(doubleConsumer));
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

    public /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
        return this.a.j(CLASSNAMEu.a(doubleConsumer));
    }
}
