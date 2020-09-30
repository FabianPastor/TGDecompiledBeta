package j$.util;

import j$.util.function.CLASSNAMEs;
import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;

/* renamed from: j$.util.a  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEa implements CLASSNAMEt {
    public final /* synthetic */ Consumer a;

    public /* synthetic */ CLASSNAMEa(Consumer consumer) {
        this.a = consumer;
    }

    public final void accept(double d) {
        this.a.accept(Double.valueOf(d));
    }

    public /* synthetic */ CLASSNAMEt p(CLASSNAMEt tVar) {
        return CLASSNAMEs.a(this, tVar);
    }
}
