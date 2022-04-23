package j$.wrappers;

import j$.util.CLASSNAMEn;
import j$.util.function.Consumer;
import j$.util.function.f;
import java.util.PrimitiveIterator;

/* renamed from: j$.wrappers.a  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEa implements CLASSNAMEn {
    final /* synthetic */ PrimitiveIterator.OfDouble a;

    private /* synthetic */ CLASSNAMEa(PrimitiveIterator.OfDouble ofDouble) {
        this.a = ofDouble;
    }

    public static /* synthetic */ CLASSNAMEn a(PrimitiveIterator.OfDouble ofDouble) {
        if (ofDouble == null) {
            return null;
        }
        return ofDouble instanceof CLASSNAMEb ? ((CLASSNAMEb) ofDouble).a : new CLASSNAMEa(ofDouble);
    }

    public /* synthetic */ void e(f fVar) {
        this.a.forEachRemaining(B.a(fVar));
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
