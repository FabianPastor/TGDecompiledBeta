package j$;

import j$.util.t;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* renamed from: j$.i  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEi implements PrimitiveIterator.OfLong {
    final /* synthetic */ t.c a;

    private /* synthetic */ CLASSNAMEi(t.c cVar) {
        this.a = cVar;
    }

    public static /* synthetic */ PrimitiveIterator.OfLong a(t.c cVar) {
        if (cVar == null) {
            return null;
        }
        return cVar instanceof CLASSNAMEh ? ((CLASSNAMEh) cVar).a : new CLASSNAMEi(cVar);
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEz.b(consumer));
    }

    public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.a.d(CLASSNAMEi0.b(longConsumer));
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
