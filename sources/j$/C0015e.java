package j$;

import j$.util.t;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* renamed from: j$.e  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEe implements PrimitiveIterator.OfDouble {
    final /* synthetic */ t.a a;

    private /* synthetic */ CLASSNAMEe(t.a aVar) {
        this.a = aVar;
    }

    public static /* synthetic */ PrimitiveIterator.OfDouble a(t.a aVar) {
        if (aVar == null) {
            return null;
        }
        return aVar instanceof CLASSNAMEd ? ((CLASSNAMEd) aVar).a : new CLASSNAMEe(aVar);
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEz.b(consumer));
    }

    public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.a.e(D.b(doubleConsumer));
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
