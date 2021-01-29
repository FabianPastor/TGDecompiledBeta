package j$;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.q;
import java.util.Comparator;
import java.util.Spliterator;

/* renamed from: j$.l  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEl implements Spliterator.a {
    final /* synthetic */ Spliterator.OfDouble a;

    private /* synthetic */ CLASSNAMEl(Spliterator.OfDouble ofDouble) {
        this.a = ofDouble;
    }

    public static /* synthetic */ Spliterator.a a(Spliterator.OfDouble ofDouble) {
        if (ofDouble == null) {
            return null;
        }
        return ofDouble instanceof CLASSNAMEm ? ((CLASSNAMEm) ofDouble).a : new CLASSNAMEl(ofDouble);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return this.a.tryAdvance(A.a(consumer));
    }

    public /* synthetic */ int characteristics() {
        return this.a.characteristics();
    }

    public /* synthetic */ void e(q qVar) {
        this.a.forEachRemaining(E.a(qVar));
    }

    public /* synthetic */ long estimateSize() {
        return this.a.estimateSize();
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(A.a(consumer));
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining(obj);
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

    public /* synthetic */ boolean o(q qVar) {
        return this.a.tryAdvance(E.a(qVar));
    }

    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.a.tryAdvance(obj);
    }
}
