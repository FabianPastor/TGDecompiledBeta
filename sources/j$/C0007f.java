package j$;

import j$.util.F;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* renamed from: j$.f  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEf implements PrimitiveIterator.OfDouble {
    final /* synthetic */ F a;

    private /* synthetic */ CLASSNAMEf(F f) {
        this.a = f;
    }

    public static /* synthetic */ PrimitiveIterator.OfDouble a(F f) {
        if (f == null) {
            return null;
        }
        return new CLASSNAMEf(f);
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(r.a(consumer));
    }

    public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.a.e(CLASSNAMEu.a(doubleConsumer));
    }

    public /* synthetic */ boolean hasNext() {
        return this.a.hasNext();
    }

    public /* synthetic */ double nextDouble() {
        return this.a.nextDouble();
    }

    public /* synthetic */ void remove() {
        this.a.remove();
    }
}