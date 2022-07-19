package j$.wrappers;

import j$.util.function.Consumer;
import j$.util.function.l;
import j$.util.u;
import java.util.Comparator;
import java.util.Spliterator;

/* renamed from: j$.wrappers.k  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEk implements u.a {
    final /* synthetic */ Spliterator.OfInt a;

    private /* synthetic */ CLASSNAMEk(Spliterator.OfInt ofInt) {
        this.a = ofInt;
    }

    public static /* synthetic */ u.a a(Spliterator.OfInt ofInt) {
        if (ofInt == null) {
            return null;
        }
        return ofInt instanceof CLASSNAMEl ? ((CLASSNAMEl) ofInt).a : new CLASSNAMEk(ofInt);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return this.a.tryAdvance(CLASSNAMEx.a(consumer));
    }

    public /* synthetic */ void c(l lVar) {
        this.a.forEachRemaining(S.a(lVar));
    }

    public /* synthetic */ int characteristics() {
        return this.a.characteristics();
    }

    public /* synthetic */ long estimateSize() {
        return this.a.estimateSize();
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEx.a(consumer));
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining(obj);
    }

    public /* synthetic */ boolean g(l lVar) {
        return this.a.tryAdvance(S.a(lVar));
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
}
