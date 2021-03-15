package j$.util;

import j$.util.function.CLASSNAMEf;
import j$.util.function.Consumer;
import j$.util.function.q;

public final /* synthetic */ class j implements q {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ Consumer var_a;

    public /* synthetic */ j(Consumer consumer) {
        this.var_a = consumer;
    }

    public final void accept(double d) {
        this.var_a.accept(Double.valueOf(d));
    }

    public q k(q qVar) {
        qVar.getClass();
        return new CLASSNAMEf(this, qVar);
    }
}
