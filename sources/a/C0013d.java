package a;

import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.t;
import java.util.PrimitiveIterator;

/* renamed from: a.d  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEd implements t.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrimitiveIterator.OfDouble var_a;

    private /* synthetic */ CLASSNAMEd(PrimitiveIterator.OfDouble ofDouble) {
        this.var_a = ofDouble;
    }

    public static /* synthetic */ t.a a(PrimitiveIterator.OfDouble ofDouble) {
        if (ofDouble == null) {
            return null;
        }
        return ofDouble instanceof CLASSNAMEe ? ((CLASSNAMEe) ofDouble).var_a : new CLASSNAMEd(ofDouble);
    }

    public /* synthetic */ void e(q qVar) {
        this.var_a.forEachRemaining(E.a(qVar));
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.var_a.forEachRemaining(A.a(consumer));
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.var_a.forEachRemaining(obj);
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
