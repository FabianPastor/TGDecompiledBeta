package j$;

import j$.util.function.Consumer;
import j$.util.function.q;
import j$.util.s;
import java.util.PrimitiveIterator;

/* renamed from: j$.a  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEa implements s.a {
    final /* synthetic */ PrimitiveIterator.OfDouble a;

    private /* synthetic */ CLASSNAMEa(PrimitiveIterator.OfDouble ofDouble) {
        this.a = ofDouble;
    }

    public static /* synthetic */ s.a a(PrimitiveIterator.OfDouble ofDouble) {
        if (ofDouble == null) {
            return null;
        }
        return ofDouble instanceof CLASSNAMEb ? ((CLASSNAMEb) ofDouble).a : new CLASSNAMEa(ofDouble);
    }

    public /* synthetic */ void e(q qVar) {
        this.a.forEachRemaining(B.a(qVar));
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEx.a(consumer));
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
