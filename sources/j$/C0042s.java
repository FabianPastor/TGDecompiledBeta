package j$;

import j$.util.Spliterator;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

/* renamed from: j$.s  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEs implements Spliterator.OfPrimitive {
    final /* synthetic */ Spliterator.d a;

    private /* synthetic */ CLASSNAMEs(Spliterator.d dVar) {
        this.a = dVar;
    }

    public static /* synthetic */ Spliterator.OfPrimitive a(Spliterator.d dVar) {
        if (dVar == null) {
            return null;
        }
        return dVar instanceof r ? ((r) dVar).a : new CLASSNAMEs(dVar);
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
        this.a.forEachRemaining(CLASSNAMEz.b(consumer));
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
        return this.a.b(CLASSNAMEz.b(consumer));
    }
}
