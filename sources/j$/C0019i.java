package j$;

import j$.util.z;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

/* renamed from: j$.i  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEi implements PrimitiveIterator.OfLong {
    final /* synthetic */ z a;

    private /* synthetic */ CLASSNAMEi(z zVar) {
        this.a = zVar;
    }

    public static /* synthetic */ PrimitiveIterator.OfLong a(z zVar) {
        if (zVar == null) {
            return null;
        }
        return zVar instanceof CLASSNAMEh ? ((CLASSNAMEh) zVar).a : new CLASSNAMEi(zVar);
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.a.forEachRemaining(obj);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEz.b(consumer));
    }

    public /* synthetic */ void forEachRemaining(LongConsumer longConsumer) {
        this.a.d(CLASSNAMEi0.b(longConsumer));
    }

    public /* synthetic */ boolean hasNext() {
        return this.a.hasNext();
    }

    public /* synthetic */ long nextLong() {
        return this.a.nextLong();
    }

    public /* synthetic */ void remove() {
        this.a.remove();
    }
}
