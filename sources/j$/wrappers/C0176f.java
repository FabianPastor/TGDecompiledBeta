package j$.wrappers;

import j$.util.r;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* renamed from: j$.wrappers.f  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEf implements PrimitiveIterator.OfLong {
    final /* synthetic */ r a;

    private /* synthetic */ CLASSNAMEf(r rVar) {
        this.a = rVar;
    }

    public static /* synthetic */ PrimitiveIterator.OfLong a(r rVar) {
        if (rVar == null) {
            return null;
        }
        return rVar instanceof CLASSNAMEe ? ((CLASSNAMEe) rVar).a : new CLASSNAMEf(rVar);
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEw.b(consumer));
    }

    public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.a.d(CLASSNAMEf0.b(longConsumer));
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.r] */
    public /* synthetic */ boolean hasNext() {
        return this.a.hasNext();
    }

    public /* synthetic */ long nextLong() {
        return this.a.nextLong();
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.r] */
    public /* synthetic */ void remove() {
        this.a.remove();
    }
}
