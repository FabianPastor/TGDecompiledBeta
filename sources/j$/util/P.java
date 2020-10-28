package j$.util;

import j$.util.function.Consumer;
import java.util.Comparator;

final class P extends Q implements Spliterator {
    P() {
    }

    public boolean b(Consumer consumer) {
        consumer.getClass();
        return false;
    }

    public void forEachRemaining(Consumer consumer) {
        consumer.getClass();
    }

    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return CLASSNAMEk.e(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return CLASSNAMEk.f(this, i);
    }
}
