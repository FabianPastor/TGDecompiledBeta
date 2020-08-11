package j$.util.stream;

import j$.util.V;

class H6 extends I6 implements V {
    public /* bridge */ /* synthetic */ V trySplit() {
        return (V) super.trySplit();
    }

    H6(j$.util.function.V v) {
        super(v);
    }

    public boolean tryAdvance(Object consumer) {
        return ((V) b()).tryAdvance(consumer);
    }

    public void forEachRemaining(Object consumer) {
        ((V) b()).forEachRemaining(consumer);
    }
}
