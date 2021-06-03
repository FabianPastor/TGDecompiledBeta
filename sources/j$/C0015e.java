package j$;

import j$.util.function.C;
import j$.util.function.Consumer;
import j$.util.s;
import java.util.PrimitiveIterator;

/* renamed from: j$.e  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEe implements s.c {
    final /* synthetic */ PrimitiveIterator.OfLong a;

    private /* synthetic */ CLASSNAMEe(PrimitiveIterator.OfLong ofLong) {
        this.a = ofLong;
    }

    public static /* synthetic */ s.c a(PrimitiveIterator.OfLong ofLong) {
        if (ofLong == null) {
            return null;
        }
        return ofLong instanceof CLASSNAMEf ? ((CLASSNAMEf) ofLong).a : new CLASSNAMEe(ofLong);
    }

    public /* synthetic */ void d(C c) {
        this.a.forEachRemaining(CLASSNAMEg0.a(c));
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        this.a.forEachRemaining(CLASSNAMEx.a(consumer));
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
