package j$;

import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.x;
import java.util.PrimitiveIterator;

/* renamed from: j$.d  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEd implements x {
    final /* synthetic */ PrimitiveIterator.OfDouble a;

    private /* synthetic */ CLASSNAMEd(PrimitiveIterator.OfDouble ofDouble) {
        this.a = ofDouble;
    }

    public static /* synthetic */ x a(PrimitiveIterator.OfDouble ofDouble) {
        if (ofDouble == null) {
            return null;
        }
        return ofDouble instanceof CLASSNAMEe ? ((CLASSNAMEe) ofDouble).a : new CLASSNAMEd(ofDouble);
    }

    public /* synthetic */ void e(q qVar) {
        this.a.forEachRemaining(E.a(qVar));
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(A.a(consumer));
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining(obj);
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
