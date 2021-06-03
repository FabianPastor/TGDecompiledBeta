package j$;

import j$.util.Spliterator;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* renamed from: j$.n  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEn implements Spliterator.OfLong {
    final /* synthetic */ Spliterator.c a;

    private /* synthetic */ CLASSNAMEn(Spliterator.c cVar) {
        this.a = cVar;
    }

    public static /* synthetic */ Spliterator.OfLong a(Spliterator.c cVar) {
        if (cVar == null) {
            return null;
        }
        return cVar instanceof CLASSNAMEm ? ((CLASSNAMEm) cVar).a : new CLASSNAMEn(cVar);
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
        this.a.forEachRemaining(CLASSNAMEw.b(consumer));
    }

    public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.a.d(CLASSNAMEf0.b(longConsumer));
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
        return this.a.b(CLASSNAMEw.b(consumer));
    }

    public /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
        return this.a.i(CLASSNAMEf0.b(longConsumer));
    }
}
