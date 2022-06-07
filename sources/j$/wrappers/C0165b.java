package j$.wrappers;

import j$.util.CLASSNAMEn;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* renamed from: j$.wrappers.b  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEb implements PrimitiveIterator.OfDouble {
    final /* synthetic */ CLASSNAMEn a;

    private /* synthetic */ CLASSNAMEb(CLASSNAMEn nVar) {
        this.a = nVar;
    }

    public static /* synthetic */ PrimitiveIterator.OfDouble a(CLASSNAMEn nVar) {
        if (nVar == null) {
            return null;
        }
        return nVar instanceof CLASSNAMEa ? ((CLASSNAMEa) nVar).a : new CLASSNAMEb(nVar);
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEw.b(consumer));
    }

    public /* synthetic */ void forEachRemaining(DoubleConsumer doubleConsumer) {
        this.a.e(A.b(doubleConsumer));
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.n] */
    public /* synthetic */ boolean hasNext() {
        return this.a.hasNext();
    }

    public /* synthetic */ double nextDouble() {
        return this.a.nextDouble();
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.n] */
    public /* synthetic */ void remove() {
        this.a.remove();
    }
}
