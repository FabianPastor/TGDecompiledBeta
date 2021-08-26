package j$.wrappers;

import j$.util.function.Consumer;
import j$.util.function.p;
import j$.util.r;
import java.util.PrimitiveIterator;

/* renamed from: j$.wrappers.e  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEe implements r {
    final /* synthetic */ PrimitiveIterator.OfLong a;

    private /* synthetic */ CLASSNAMEe(PrimitiveIterator.OfLong ofLong) {
        this.a = ofLong;
    }

    public static /* synthetic */ r a(PrimitiveIterator.OfLong ofLong) {
        if (ofLong == null) {
            return null;
        }
        return ofLong instanceof CLASSNAMEf ? ((CLASSNAMEf) ofLong).a : new CLASSNAMEe(ofLong);
    }

    public /* synthetic */ void d(p pVar) {
        this.a.forEachRemaining(CLASSNAMEg0.a(pVar));
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
