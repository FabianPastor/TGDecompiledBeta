package j$;

import j$.util.V;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

/* renamed from: j$.m  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEm implements Spliterator.OfPrimitive {
    final /* synthetic */ V a;

    private /* synthetic */ CLASSNAMEm(V v) {
        this.a = v;
    }

    public static /* synthetic */ Spliterator.OfPrimitive a(V v) {
        if (v == null) {
            return null;
        }
        return new CLASSNAMEm(v);
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
        this.a.forEachRemaining(r.a(consumer));
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
        return this.a.a(r.a(consumer));
    }
}