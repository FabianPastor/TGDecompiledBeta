package j$;

import j$.util.t;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: j$.g  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEg implements PrimitiveIterator.OfInt {
    final /* synthetic */ t.b a;

    private /* synthetic */ CLASSNAMEg(t.b bVar) {
        this.a = bVar;
    }

    public static /* synthetic */ PrimitiveIterator.OfInt a(t.b bVar) {
        if (bVar == null) {
            return null;
        }
        return bVar instanceof CLASSNAMEf ? ((CLASSNAMEf) bVar).a : new CLASSNAMEg(bVar);
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEz.b(consumer));
    }

    public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.a.c(U.b(intConsumer));
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
