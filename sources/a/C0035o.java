package a;

import j$.util.Spliterator;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: a.o  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEo implements Spliterator.OfInt {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Spliterator.b var_a;

    private /* synthetic */ CLASSNAMEo(Spliterator.b bVar) {
        this.var_a = bVar;
    }

    public static /* synthetic */ Spliterator.OfInt a(Spliterator.b bVar) {
        if (bVar == null) {
            return null;
        }
        return bVar instanceof CLASSNAMEn ? ((CLASSNAMEn) bVar).var_a : new CLASSNAMEo(bVar);
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

    public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.var_a.c(U.b(intConsumer));
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

    public /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
        return this.var_a.h(U.b(intConsumer));
    }
}
