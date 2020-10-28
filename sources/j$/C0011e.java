package j$;

import j$.util.x;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* renamed from: j$.e  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEe implements PrimitiveIterator.OfDouble {
    final /* synthetic */ x a;

    private /* synthetic */ CLASSNAMEe(x xVar) {
        this.a = xVar;
    }

    public static /* synthetic */ PrimitiveIterator.OfDouble a(x xVar) {
        if (xVar == null) {
            return null;
        }
        return xVar instanceof CLASSNAMEd ? ((CLASSNAMEd) xVar).a : new CLASSNAMEe(xVar);
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
