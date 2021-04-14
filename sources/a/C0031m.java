package a;

import j$.util.Spliterator;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* renamed from: a.m  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEm implements Spliterator.OfDouble {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Spliterator.a var_a;

    private /* synthetic */ CLASSNAMEm(Spliterator.a aVar) {
        this.var_a = aVar;
    }

    public static /* synthetic */ Spliterator.OfDouble a(Spliterator.a aVar) {
        if (aVar == null) {
            return null;
        }
        return aVar instanceof CLASSNAMEl ? ((CLASSNAMEl) aVar).var_a : new CLASSNAMEm(aVar);
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

    public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.var_a.e(D.b(doubleConsumer));
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

    public /* synthetic */ boolean tryAdvance(DoubleConsumer doubleConsumer) {
        return this.var_a.o(D.b(doubleConsumer));
    }
}
