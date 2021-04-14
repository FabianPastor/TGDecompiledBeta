package a;

import j$.util.t;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* renamed from: a.e  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEe implements PrimitiveIterator.OfDouble {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ t.a var_a;

    private /* synthetic */ CLASSNAMEe(t.a aVar) {
        this.var_a = aVar;
    }

    public static /* synthetic */ PrimitiveIterator.OfDouble a(t.a aVar) {
        if (aVar == null) {
            return null;
        }
        return aVar instanceof CLASSNAMEd ? ((CLASSNAMEd) aVar).var_a : new CLASSNAMEe(aVar);
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.var_a.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.var_a.forEachRemaining(CLASSNAMEz.b(consumer));
    }

    public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.var_a.e(D.b(doubleConsumer));
    }

    public /* synthetic */ boolean hasNext() {
        return this.var_a.hasNext();
    }

    public /* synthetic */ double nextDouble() {
        return this.var_a.nextDouble();
    }

    public /* synthetic */ void remove() {
        this.var_a.remove();
    }
}
