package a;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.q;
import java.util.Comparator;
import java.util.Spliterator;

/* renamed from: a.l  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEl implements Spliterator.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Spliterator.OfDouble var_a;

    private /* synthetic */ CLASSNAMEl(Spliterator.OfDouble ofDouble) {
        this.var_a = ofDouble;
    }

    public static /* synthetic */ Spliterator.a a(Spliterator.OfDouble ofDouble) {
        if (ofDouble == null) {
            return null;
        }
        return ofDouble instanceof CLASSNAMEm ? ((CLASSNAMEm) ofDouble).var_a : new CLASSNAMEl(ofDouble);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return this.var_a.tryAdvance(A.a(consumer));
    }

    public /* synthetic */ int characteristics() {
        return this.var_a.characteristics();
    }

    public /* synthetic */ void e(q qVar) {
        this.var_a.forEachRemaining(E.a(qVar));
    }

    public /* synthetic */ long estimateSize() {
        return this.var_a.estimateSize();
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.var_a.forEachRemaining(A.a(consumer));
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.var_a.forEachRemaining(obj);
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

    public /* synthetic */ boolean o(q qVar) {
        return this.var_a.tryAdvance(E.a(qVar));
    }

    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.var_a.tryAdvance(obj);
    }
}
