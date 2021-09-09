package j$.wrappers;

import j$.util.v;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: j$.wrappers.l  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEl implements Spliterator.OfInt {
    final /* synthetic */ v a;

    private /* synthetic */ CLASSNAMEl(v vVar) {
        this.a = vVar;
    }

    public static /* synthetic */ Spliterator.OfInt a(v vVar) {
        if (vVar == null) {
            return null;
        }
        return vVar instanceof CLASSNAMEk ? ((CLASSNAMEk) vVar).a : new CLASSNAMEl(vVar);
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

    public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.a.c(Q.b(intConsumer));
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

    public /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
        return this.a.g(Q.b(intConsumer));
    }
}
