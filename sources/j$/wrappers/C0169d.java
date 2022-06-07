package j$.wrappers;

import j$.util.p;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: j$.wrappers.d  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEd implements PrimitiveIterator.OfInt {
    final /* synthetic */ p.a a;

    private /* synthetic */ CLASSNAMEd(p.a aVar) {
        this.a = aVar;
    }

    public static /* synthetic */ PrimitiveIterator.OfInt a(p.a aVar) {
        if (aVar == null) {
            return null;
        }
        return aVar instanceof CLASSNAMEc ? ((CLASSNAMEc) aVar).a : new CLASSNAMEd(aVar);
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

    /* JADX WARNING: type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.p$a] */
    public /* synthetic */ boolean hasNext() {
        return this.a.hasNext();
    }

    public /* synthetic */ int nextInt() {
        return this.a.nextInt();
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [j$.util.Iterator, j$.util.p$a] */
    public /* synthetic */ void remove() {
        this.a.remove();
    }
}
