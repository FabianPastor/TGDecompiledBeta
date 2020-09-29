package j$.time.format;

import j$.time.t.q;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;

/* renamed from: j$.time.format.d  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEd implements Consumer {
    public final /* synthetic */ s a;
    public final /* synthetic */ A b;
    public final /* synthetic */ long c;
    public final /* synthetic */ int d;
    public final /* synthetic */ int e;

    public /* synthetic */ CLASSNAMEd(s sVar, A a2, long j, int i, int i2) {
        this.a = sVar;
        this.b = a2;
        this.c = j;
        this.d = i;
        this.e = i2;
    }

    public final void accept(Object obj) {
        q qVar = (q) obj;
        this.a.g(this.b, this.c, this.d, this.e);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }
}
