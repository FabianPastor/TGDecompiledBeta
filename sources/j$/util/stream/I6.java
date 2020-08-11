package j$.util.stream;

import j$.util.N;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.V;
import java.util.Comparator;

class I6 implements Spliterator {
    private final V a;
    private Spliterator b;

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return N.c(this, i);
    }

    I6(V v) {
        this.a = v;
    }

    /* access modifiers changed from: package-private */
    public Spliterator b() {
        if (this.b == null) {
            this.b = (Spliterator) this.a.get();
        }
        return this.b;
    }

    public Spliterator trySplit() {
        return b().trySplit();
    }

    public boolean a(Consumer consumer) {
        return b().a(consumer);
    }

    public void forEachRemaining(Consumer consumer) {
        b().forEachRemaining(consumer);
    }

    public long estimateSize() {
        return b().estimateSize();
    }

    public int characteristics() {
        return b().characteristics();
    }

    public Comparator getComparator() {
        return b().getComparator();
    }

    public long getExactSizeIfKnown() {
        return b().getExactSizeIfKnown();
    }

    public String toString() {
        return getClass().getName() + "[" + b() + "]";
    }
}
