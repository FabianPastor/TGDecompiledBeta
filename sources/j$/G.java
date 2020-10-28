package j$;

import j$.util.function.r;
import java.util.function.DoubleFunction;

public final /* synthetic */ class G implements DoubleFunction {
    final /* synthetic */ r a;

    private /* synthetic */ G(r rVar) {
        this.a = rVar;
    }

    public static /* synthetic */ DoubleFunction a(r rVar) {
        if (rVar == null) {
            return null;
        }
        return rVar instanceof F ? ((F) rVar).a : new G(rVar);
    }

    public /* synthetic */ Object apply(double d) {
        return this.a.apply(d);
    }
}
