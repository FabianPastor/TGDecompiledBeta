package j$;

import j$.util.s;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/* renamed from: j$.b  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEb implements PrimitiveIterator.OfDouble {
    final /* synthetic */ s.a a;

    private /* synthetic */ CLASSNAMEb(s.a aVar) {
        this.a = aVar;
    }

    public static /* synthetic */ PrimitiveIterator.OfDouble a(s.a aVar) {
        if (aVar == null) {
            return null;
        }
        return aVar instanceof CLASSNAMEa ? ((CLASSNAMEa) aVar).a : new CLASSNAMEb(aVar);
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
