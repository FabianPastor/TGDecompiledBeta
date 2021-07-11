package j$;

import j$.util.s;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/* renamed from: j$.d  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEd implements PrimitiveIterator.OfInt {
    final /* synthetic */ s.b a;

    private /* synthetic */ CLASSNAMEd(s.b bVar) {
        this.a = bVar;
    }

    public static /* synthetic */ PrimitiveIterator.OfInt a(s.b bVar) {
        if (bVar == null) {
            return null;
        }
        return bVar instanceof CLASSNAMEc ? ((CLASSNAMEc) bVar).a : new CLASSNAMEd(bVar);
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
