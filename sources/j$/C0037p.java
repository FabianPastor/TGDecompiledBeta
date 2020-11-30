package j$;

import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.Consumer;
import java.util.Comparator;
import java.util.Spliterator;

/* renamed from: j$.p  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEp implements Spliterator.c {
    final /* synthetic */ Spliterator.OfLong a;

    private /* synthetic */ CLASSNAMEp(Spliterator.OfLong ofLong) {
        this.a = ofLong;
    }

    public static /* synthetic */ Spliterator.c a(Spliterator.OfLong ofLong) {
        if (ofLong == null) {
            return null;
        }
        return ofLong instanceof CLASSNAMEq ? ((CLASSNAMEq) ofLong).a : new CLASSNAMEp(ofLong);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return this.a.tryAdvance(A.a(consumer));
    }

    public /* synthetic */ int characteristics() {
        return this.a.characteristics();
    }

    public /* synthetic */ void d(C c) {
        this.a.forEachRemaining(CLASSNAMEj0.a(c));
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

    public /* synthetic */ boolean j(C c) {
        return this.a.tryAdvance(CLASSNAMEj0.a(c));
    }

    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.a.tryAdvance(obj);
    }
}
