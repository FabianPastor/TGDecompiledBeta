package a;

import j$.util.Spliterator;
import j$.util.function.Consumer;
import java.util.Comparator;
import java.util.Spliterator;

public final /* synthetic */ class r implements Spliterator.d {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Spliterator.OfPrimitive var_a;

    private /* synthetic */ r(Spliterator.OfPrimitive ofPrimitive) {
        this.var_a = ofPrimitive;
    }

    public static /* synthetic */ Spliterator.d a(Spliterator.OfPrimitive ofPrimitive) {
        if (ofPrimitive == null) {
            return null;
        }
        return ofPrimitive instanceof CLASSNAMEs ? ((CLASSNAMEs) ofPrimitive).var_a : new r(ofPrimitive);
    }

    public /* synthetic */ boolean b(Consumer consumer) {
        return this.var_a.tryAdvance(A.a(consumer));
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

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return this.var_a.hasCharacteristics(i);
    }

    public /* synthetic */ boolean tryAdvance(Object obj) {
        return this.var_a.tryAdvance(obj);
    }
}
