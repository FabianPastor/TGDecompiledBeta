package j$;

import j$.util.function.Consumer;
import j$.util.function.u;
import j$.util.y;
import java.util.PrimitiveIterator;

/* renamed from: j$.f  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEf implements y {
    final /* synthetic */ PrimitiveIterator.OfInt a;

    private /* synthetic */ CLASSNAMEf(PrimitiveIterator.OfInt ofInt) {
        this.a = ofInt;
    }

    public static /* synthetic */ y a(PrimitiveIterator.OfInt ofInt) {
        if (ofInt == null) {
            return null;
        }
        return ofInt instanceof CLASSNAMEg ? ((CLASSNAMEg) ofInt).a : new CLASSNAMEf(ofInt);
    }

    public /* synthetic */ void c(u uVar) {
        this.a.forEachRemaining(V.a(uVar));
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(A.a(consumer));
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
