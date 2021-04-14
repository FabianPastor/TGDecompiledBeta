package a;

import j$.util.t;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: a.g  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEg implements PrimitiveIterator.OfInt {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ t.b var_a;

    private /* synthetic */ CLASSNAMEg(t.b bVar) {
        this.var_a = bVar;
    }

    public static /* synthetic */ PrimitiveIterator.OfInt a(t.b bVar) {
        if (bVar == null) {
            return null;
        }
        return bVar instanceof CLASSNAMEf ? ((CLASSNAMEf) bVar).var_a : new CLASSNAMEg(bVar);
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.var_a.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.var_a.forEachRemaining(CLASSNAMEz.b(consumer));
    }

    public /* synthetic */ void forEachRemaining(IntConsumer intConsumer) {
        this.var_a.c(U.b(intConsumer));
    }

    public /* synthetic */ boolean hasNext() {
        return this.var_a.hasNext();
    }

    public /* synthetic */ int nextInt() {
        return this.var_a.nextInt();
    }

    public /* synthetic */ void remove() {
        this.var_a.remove();
    }
}
