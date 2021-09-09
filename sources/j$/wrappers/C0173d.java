package j$.wrappers;

import j$.util.CLASSNAMEp;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: j$.wrappers.d  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEd implements PrimitiveIterator.OfInt {
    final /* synthetic */ CLASSNAMEp a;

    private /* synthetic */ CLASSNAMEd(CLASSNAMEp pVar) {
        this.a = pVar;
    }

    public static /* synthetic */ PrimitiveIterator.OfInt a(CLASSNAMEp pVar) {
        if (pVar == null) {
            return null;
        }
        return pVar instanceof CLASSNAMEc ? ((CLASSNAMEc) pVar).a : new CLASSNAMEd(pVar);
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEw.b(consumer));
    }

    public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.a.c(Q.b(intConsumer));
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [j$.util.p, j$.util.Iterator] */
    public /* synthetic */ boolean hasNext() {
        return this.a.hasNext();
    }

    public /* synthetic */ int nextInt() {
        return this.a.nextInt();
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [j$.util.p, j$.util.Iterator] */
    public /* synthetic */ void remove() {
        this.a.remove();
    }
}
