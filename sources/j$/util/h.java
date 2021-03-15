package j$.util;

import j$.util.function.CLASSNAMEg;
import j$.util.function.Consumer;
import j$.util.function.w;

public final /* synthetic */ class h implements w {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ Consumer var_a;

    public /* synthetic */ h(Consumer consumer) {
        this.var_a = consumer;
    }

    public final void accept(int i) {
        this.var_a.accept(Integer.valueOf(i));
    }

    public w l(w wVar) {
        wVar.getClass();
        return new CLASSNAMEg(this, wVar);
    }
}
