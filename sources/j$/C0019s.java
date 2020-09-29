package j$;

import j$.util.J;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* renamed from: j$.s  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEs implements PrimitiveIterator.OfLong {
    final /* synthetic */ J a;

    private /* synthetic */ CLASSNAMEs(J j) {
        this.a = j;
    }

    public static /* synthetic */ PrimitiveIterator.OfLong a(J j) {
        if (j == null) {
            return null;
        }
        return new CLASSNAMEs(j);
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(C.a(consumer));
    }

    public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.a.d(c0.a(longConsumer));
    }

    public /* synthetic */ boolean hasNext() {
        return this.a.hasNext();
    }

    public /* synthetic */ long nextLong() {
        return this.a.nextLong();
    }

    public /* synthetic */ void remove() {
        this.a.remove();
    }
}
