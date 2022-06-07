package j$.wrappers;

import j$.util.function.Consumer;
import j$.util.function.l;
import j$.util.p;
import java.util.PrimitiveIterator;

/* renamed from: j$.wrappers.c  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEc implements p.a {
    final /* synthetic */ PrimitiveIterator.OfInt a;

    private /* synthetic */ CLASSNAMEc(PrimitiveIterator.OfInt ofInt) {
        this.a = ofInt;
    }

    public static /* synthetic */ p.a a(PrimitiveIterator.OfInt ofInt) {
        if (ofInt == null) {
            return null;
        }
        return ofInt instanceof CLASSNAMEd ? ((CLASSNAMEd) ofInt).a : new CLASSNAMEc(ofInt);
    }

    public /* synthetic */ void c(l lVar) {
        this.a.forEachRemaining(S.a(lVar));
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

    public /* synthetic */ int nextInt() {
        return this.a.nextInt();
    }

    public /* synthetic */ void remove() {
        this.a.remove();
    }
}
