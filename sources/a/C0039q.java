package a;

import j$.util.Spliterator;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* renamed from: a.q  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEq implements Spliterator.OfLong {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Spliterator.c var_a;

    private /* synthetic */ CLASSNAMEq(Spliterator.c cVar) {
        this.var_a = cVar;
    }

    public static /* synthetic */ Spliterator.OfLong a(Spliterator.c cVar) {
        if (cVar == null) {
            return null;
        }
        return cVar instanceof CLASSNAMEp ? ((CLASSNAMEp) cVar).var_a : new CLASSNAMEq(cVar);
    }

    public /* synthetic */ int characteristics() {
        return this.var_a.characteristics();
    }

    public /* synthetic */ long estimateSize() {
        return this.var_a.estimateSize();
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.var_a.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.var_a.forEachRemaining(CLASSNAMEz.b(consumer));
    }

    public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.var_a.d(CLASSNAMEi0.b(longConsumer));
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

    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.var_a.tryAdvance(obj);
    }

    public /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.var_a.b(CLASSNAMEz.b(consumer));
    }

    public /* synthetic */ boolean tryAdvance(LongConsumer longConsumer) {
        return this.var_a.j(CLASSNAMEi0.b(longConsumer));
    }
}
