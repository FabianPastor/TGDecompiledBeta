package j$;

import j$.util.Spliterator;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: j$.o  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEo implements Spliterator.OfInt {
    final /* synthetic */ Spliterator.b a;

    private /* synthetic */ CLASSNAMEo(Spliterator.b bVar) {
        this.a = bVar;
    }

    public static /* synthetic */ Spliterator.OfInt a(Spliterator.b bVar) {
        if (bVar == null) {
            return null;
        }
        return bVar instanceof CLASSNAMEn ? ((CLASSNAMEn) bVar).a : new CLASSNAMEo(bVar);
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

    public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.a.c(U.b(intConsumer));
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

    public /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
        return this.a.h(U.b(intConsumer));
    }
}
