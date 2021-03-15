package a;

import j$.util.t;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* renamed from: a.i  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEi implements PrimitiveIterator.OfLong {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ t.c var_a;

    private /* synthetic */ CLASSNAMEi(t.c cVar) {
        this.var_a = cVar;
    }

    public static /* synthetic */ PrimitiveIterator.OfLong a(t.c cVar) {
        if (cVar == null) {
            return null;
        }
        return cVar instanceof CLASSNAMEh ? ((CLASSNAMEh) cVar).var_a : new CLASSNAMEi(cVar);
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.var_a.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.var_a.forEachRemaining(CLASSNAMEz.b(consumer));
    }

    public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.var_a.d(CLASSNAMEi0.b(longConsumer));
    }

    public /* synthetic */ boolean hasNext() {
        return this.var_a.hasNext();
    }

    public /* synthetic */ long nextLong() {
        return this.var_a.nextLong();
    }

    public /* synthetic */ void remove() {
        this.var_a.remove();
    }
}
