package j$.wrappers;

import j$.util.function.Consumer;
import j$.util.function.p;
import j$.util.w;
import java.util.Comparator;
import java.util.Spliterator;

/* renamed from: j$.wrappers.m  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEm implements w {
    final /* synthetic */ Spliterator.OfLong a;

    private /* synthetic */ CLASSNAMEm(Spliterator.OfLong ofLong) {
        this.a = ofLong;
    }

    public static /* synthetic */ w a(Spliterator.OfLong ofLong) {
        if (ofLong == null) {
            return null;
        }
        return ofLong instanceof CLASSNAMEn ? ((CLASSNAMEn) ofLong).a : new CLASSNAMEm(ofLong);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return this.a.tryAdvance(CLASSNAMEx.a(consumer));
    }

    public /* synthetic */ int characteristics() {
        return this.a.characteristics();
    }

    public /* synthetic */ void d(p pVar) {
        this.a.forEachRemaining(CLASSNAMEg0.a(pVar));
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

    public /* synthetic */ Comparator getComparator() {
        return this.a.getComparator();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return this.a.getExactSizeIfKnown();
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return this.a.hasCharacteristics(i);
    }

    public /* synthetic */ boolean i(p pVar) {
        return this.a.tryAdvance(CLASSNAMEg0.a(pVar));
    }

    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.a.tryAdvance(obj);
    }
}
