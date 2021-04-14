package a;

import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.Consumer;
import java.util.Comparator;
import java.util.Spliterator;

/* renamed from: a.p  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEp implements Spliterator.c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Spliterator.OfLong var_a;

    private /* synthetic */ CLASSNAMEp(Spliterator.OfLong ofLong) {
        this.var_a = ofLong;
    }

    public static /* synthetic */ Spliterator.c a(Spliterator.OfLong ofLong) {
        if (ofLong == null) {
            return null;
        }
        return ofLong instanceof CLASSNAMEq ? ((CLASSNAMEq) ofLong).var_a : new CLASSNAMEp(ofLong);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return this.var_a.tryAdvance(A.a(consumer));
    }

    public /* synthetic */ int characteristics() {
        return this.var_a.characteristics();
    }

    public /* synthetic */ void d(C c) {
        this.var_a.forEachRemaining(CLASSNAMEj0.a(c));
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

    public /* synthetic */ boolean j(C c) {
        return this.var_a.tryAdvance(CLASSNAMEj0.a(c));
    }

    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.var_a.tryAdvance(obj);
    }
}
