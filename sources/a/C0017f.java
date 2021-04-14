package a;

import j$.util.function.Consumer;
import j$.util.function.w;
import j$.util.t;
import java.util.PrimitiveIterator;

/* renamed from: a.f  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEf implements t.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrimitiveIterator.OfInt var_a;

    private /* synthetic */ CLASSNAMEf(PrimitiveIterator.OfInt ofInt) {
        this.var_a = ofInt;
    }

    public static /* synthetic */ t.b a(PrimitiveIterator.OfInt ofInt) {
        if (ofInt == null) {
            return null;
        }
        return ofInt instanceof CLASSNAMEg ? ((CLASSNAMEg) ofInt).var_a : new CLASSNAMEf(ofInt);
    }

    public /* synthetic */ void c(w wVar) {
        this.var_a.forEachRemaining(V.a(wVar));
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.var_a.forEachRemaining(A.a(consumer));
    }

    public /* synthetic */ void forEachRemaining(Object obj) {
        this.var_a.forEachRemaining(obj);
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
