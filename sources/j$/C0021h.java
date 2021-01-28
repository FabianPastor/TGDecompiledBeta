package j$;

import j$.util.function.C;
import j$.util.function.Consumer;
import j$.util.t;
import java.util.PrimitiveIterator;

/* renamed from: j$.h  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEh implements t.c {
    final /* synthetic */ PrimitiveIterator.OfLong a;

    private /* synthetic */ CLASSNAMEh(PrimitiveIterator.OfLong ofLong) {
        this.a = ofLong;
    }

    public static /* synthetic */ t.c a(PrimitiveIterator.OfLong ofLong) {
        if (ofLong == null) {
            return null;
        }
        return ofLong instanceof CLASSNAMEi ? ((CLASSNAMEi) ofLong).a : new CLASSNAMEh(ofLong);
    }

    public /* synthetic */ void d(C c) {
        this.a.forEachRemaining(CLASSNAMEj0.a(c));
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

    public /* synthetic */ long nextLong() {
        return this.a.nextLong();
    }

    public /* synthetic */ void remove() {
        this.a.remove();
    }
}
