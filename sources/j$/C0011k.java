package j$;

import j$.util.S;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: j$.k  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEk implements Spliterator.OfInt {
    final /* synthetic */ S a;

    private /* synthetic */ CLASSNAMEk(S s) {
        this.a = s;
    }

    public static /* synthetic */ Spliterator.OfInt a(S s) {
        if (s == null) {
            return null;
        }
        return new CLASSNAMEk(s);
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

    public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.a.c(G.a(intConsumer));
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

    public /* synthetic */ boolean tryAdvance(IntConsumer intConsumer) {
        return this.a.f(G.a(intConsumer));
    }
}
