package a;

import j$.util.function.w;
import java.util.function.IntConsumer;

public final /* synthetic */ class V implements IntConsumer {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ w var_a;

    private /* synthetic */ V(w wVar) {
        this.var_a = wVar;
    }

    public static /* synthetic */ IntConsumer a(w wVar) {
        if (wVar == null) {
            return null;
        }
        return wVar instanceof U ? ((U) wVar).var_a : new V(wVar);
    }

    public /* synthetic */ void accept(int i) {
        this.var_a.accept(i);
    }

    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return a(this.var_a.l(U.b(intConsumer)));
    }
}
