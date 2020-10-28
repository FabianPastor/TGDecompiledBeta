package j$;

import j$.util.y;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: j$.g  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEg implements PrimitiveIterator.OfInt {
    final /* synthetic */ y a;

    private /* synthetic */ CLASSNAMEg(y yVar) {
        this.a = yVar;
    }

    public static /* synthetic */ PrimitiveIterator.OfInt a(y yVar) {
        if (yVar == null) {
            return null;
        }
        return yVar instanceof CLASSNAMEf ? ((CLASSNAMEf) yVar).a : new CLASSNAMEg(yVar);
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
