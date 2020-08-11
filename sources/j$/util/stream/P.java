package j$.util.stream;

import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import java.util.List;

public final /* synthetic */ class P implements Consumer {
    public final /* synthetic */ List a;

    public /* synthetic */ P(List list) {
        this.a = list;
    }

    public final void accept(Object obj) {
        this.a.add(obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }
}
