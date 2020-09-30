package j$;

import j$.util.J;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* renamed from: j$.h  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEh implements PrimitiveIterator.OfLong {
    final /* synthetic */ J a;

    private /* synthetic */ CLASSNAMEh(J j) {
        this.a = j;
    }

    public static /* synthetic */ PrimitiveIterator.OfLong a(J j) {
        if (j == null) {
            return null;
        }
        return new CLASSNAMEh(j);
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(r.a(consumer));
    }

    public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.a.d(Q.a(longConsumer));
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
