package a;

import j$.util.Spliterator;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

/* renamed from: a.s  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEs implements Spliterator.OfPrimitive {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Spliterator.d var_a;

    private /* synthetic */ CLASSNAMEs(Spliterator.d dVar) {
        this.var_a = dVar;
    }

    public static /* synthetic */ Spliterator.OfPrimitive a(Spliterator.d dVar) {
        if (dVar == null) {
            return null;
        }
        return dVar instanceof r ? ((r) dVar).var_a : new CLASSNAMEs(dVar);
    }

    public /* synthetic */ int characteristics() {
        return this.var_a.characteristics();
    }

    public /* synthetic */ long estimateSize() {
        return this.var_a.estimateSize();
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.var_a.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.var_a.forEachRemaining(CLASSNAMEz.b(consumer));
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

    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.var_a.tryAdvance(obj);
    }

    public /* synthetic */ boolean tryAdvance(Consumer consumer) {
        return this.var_a.b(CLASSNAMEz.b(consumer));
    }
}
