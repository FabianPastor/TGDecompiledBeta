package j$;

import j$.util.s;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* renamed from: j$.f  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEf implements PrimitiveIterator.OfLong {
    final /* synthetic */ s.c a;

    private /* synthetic */ CLASSNAMEf(s.c cVar) {
        this.a = cVar;
    }

    public static /* synthetic */ PrimitiveIterator.OfLong a(s.c cVar) {
        if (cVar == null) {
            return null;
        }
        return cVar instanceof CLASSNAMEe ? ((CLASSNAMEe) cVar).a : new CLASSNAMEf(cVar);
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
