package j$.util;

import j$.util.function.CLASSNAMEg;
import j$.util.function.Consumer;
import j$.util.function.w;

public final /* synthetic */ class h implements w {
    public final /* synthetic */ Consumer a;

    public /* synthetic */ h(Consumer consumer) {
        this.a = consumer;
    }

    public final void accept(int i) {
        this.a.accept(Integer.valueOf(i));
    }

    public w k(w wVar) {
        wVar.getClass();
        return new CLASSNAMEg(this, wVar);
    }
}
