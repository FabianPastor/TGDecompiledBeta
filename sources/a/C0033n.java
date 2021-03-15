package a;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.w;
import java.util.Comparator;
import java.util.Spliterator;

/* renamed from: a.n  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEn implements Spliterator.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Spliterator.OfInt var_a;

    private /* synthetic */ CLASSNAMEn(Spliterator.OfInt ofInt) {
        this.var_a = ofInt;
    }

    public static /* synthetic */ Spliterator.b a(Spliterator.OfInt ofInt) {
        if (ofInt == null) {
            return null;
        }
        return ofInt instanceof CLASSNAMEo ? ((CLASSNAMEo) ofInt).var_a : new CLASSNAMEn(ofInt);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return this.var_a.tryAdvance(A.a(consumer));
    }

    public /* synthetic */ void c(w wVar) {
        this.var_a.forEachRemaining(V.a(wVar));
    }

    public /* synthetic */ int characteristics() {
        return this.var_a.characteristics();
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

    public /* synthetic */ boolean h(w wVar) {
        return this.var_a.tryAdvance(V.a(wVar));
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return this.var_a.hasCharacteristics(i);
    }

    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.var_a.tryAdvance(obj);
    }
}
