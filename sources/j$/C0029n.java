package j$;

import j$.util.D;
import j$.util.function.Consumer;
import j$.util.function.u;
import java.util.Comparator;
import java.util.Spliterator;

/* renamed from: j$.n  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEn implements D {
    final /* synthetic */ Spliterator.OfInt a;

    private /* synthetic */ CLASSNAMEn(Spliterator.OfInt ofInt) {
        this.a = ofInt;
    }

    public static /* synthetic */ D a(Spliterator.OfInt ofInt) {
        if (ofInt == null) {
            return null;
        }
        return ofInt instanceof CLASSNAMEo ? ((CLASSNAMEo) ofInt).a : new CLASSNAMEn(ofInt);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return this.a.tryAdvance(A.a(consumer));
    }

    public /* synthetic */ void c(u uVar) {
        this.a.forEachRemaining(V.a(uVar));
    }

    public /* synthetic */ int characteristics() {
        return this.a.characteristics();
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

    public /* synthetic */ boolean h(u uVar) {
        return this.a.tryAdvance(V.a(uVar));
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return this.a.hasCharacteristics(i);
    }

    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.a.tryAdvance(obj);
    }
}
