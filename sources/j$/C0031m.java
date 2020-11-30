package j$;

import j$.util.Spliterator;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* renamed from: j$.m  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEm implements Spliterator.OfDouble {
    final /* synthetic */ Spliterator.a a;

    private /* synthetic */ CLASSNAMEm(Spliterator.a aVar) {
        this.a = aVar;
    }

    public static /* synthetic */ Spliterator.OfDouble a(Spliterator.a aVar) {
        if (aVar == null) {
            return null;
        }
        return aVar instanceof CLASSNAMEl ? ((CLASSNAMEl) aVar).a : new CLASSNAMEm(aVar);
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
        this.a.forEachRemaining(CLASSNAMEz.b(consumer));
    }

    public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.a.e(D.b(doubleConsumer));
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
        return this.a.b(CLASSNAMEz.b(consumer));
    }

    public /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
        return this.a.o(D.b(doubleConsumer));
    }
}
