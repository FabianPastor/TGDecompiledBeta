package j$.util;

import j$.util.function.CLASSNAMEf;
import j$.util.function.Consumer;
import j$.util.function.q;

public final /* synthetic */ class j implements q {
    public final /* synthetic */ Consumer a;

    public /* synthetic */ j(Consumer consumer) {
        this.a = consumer;
    }

    public final void accept(double d) {
        this.a.accept(Double.valueOf(d));
    }

    public q k(q qVar) {
        qVar.getClass();
        return new CLASSNAMEf(this, qVar);
    }
}
