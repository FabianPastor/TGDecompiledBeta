package j$;

import j$.util.H;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public final /* synthetic */ class r implements PrimitiveIterator.OfInt {
    final /* synthetic */ H a;

    private /* synthetic */ r(H h) {
        this.a = h;
    }

    public static /* synthetic */ PrimitiveIterator.OfInt a(H h) {
        if (h == null) {
            return null;
        }
        return new r(h);
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(C.a(consumer));
    }

    public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.a.c(S.a(intConsumer));
    }

    public /* synthetic */ boolean hasNext() {
        return this.a.hasNext();
    }

    public /* synthetic */ int nextInt() {
        return this.a.nextInt();
    }

    public /* synthetic */ void remove() {
        this.a.remove();
    }
}
