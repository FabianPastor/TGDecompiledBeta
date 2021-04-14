package a;

import j$.util.function.r;
import java.util.function.DoubleFunction;

public final /* synthetic */ class G implements DoubleFunction {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r var_a;

    private /* synthetic */ G(r rVar) {
        this.var_a = rVar;
    }

    public static /* synthetic */ DoubleFunction a(r rVar) {
        if (rVar == null) {
            return null;
        }
        return rVar instanceof F ? ((F) rVar).var_a : new G(rVar);
    }

    public /* synthetic */ Object apply(double d) {
        return this.var_a.apply(d);
    }
}
