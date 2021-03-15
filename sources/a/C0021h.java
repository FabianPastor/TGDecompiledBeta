package a;

import j$.util.function.C;
import j$.util.function.Consumer;
import j$.util.t;
import java.util.PrimitiveIterator;

/* renamed from: a.h  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEh implements t.c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrimitiveIterator.OfLong var_a;

    private /* synthetic */ CLASSNAMEh(PrimitiveIterator.OfLong ofLong) {
        this.var_a = ofLong;
    }

    public static /* synthetic */ t.c a(PrimitiveIterator.OfLong ofLong) {
        if (ofLong == null) {
            return null;
        }
        return ofLong instanceof CLASSNAMEi ? ((CLASSNAMEi) ofLong).var_a : new CLASSNAMEh(ofLong);
    }

    public /* synthetic */ void d(C c) {
        this.var_a.forEachRemaining(CLASSNAMEj0.a(c));
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

    public /* synthetic */ long nextLong() {
        return this.var_a.nextLong();
    }

    public /* synthetic */ void remove() {
        this.var_a.remove();
    }
}
