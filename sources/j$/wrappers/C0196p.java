package j$.wrappers;

import j$.util.x;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

/* renamed from: j$.wrappers.p  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEp implements Spliterator.OfPrimitive {
    final /* synthetic */ x a;

    private /* synthetic */ CLASSNAMEp(x xVar) {
        this.a = xVar;
    }

    public static /* synthetic */ Spliterator.OfPrimitive a(x xVar) {
        if (xVar == null) {
            return null;
        }
        return xVar instanceof CLASSNAMEo ? ((CLASSNAMEo) xVar).a : new CLASSNAMEp(xVar);
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
}
