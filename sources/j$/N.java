package j$;

import j$.util.function.G;
import java.util.function.IntUnaryOperator;

public final /* synthetic */ class N implements G {
    final /* synthetic */ IntUnaryOperator a;

    private /* synthetic */ N(IntUnaryOperator intUnaryOperator) {
        this.a = intUnaryOperator;
    }

    public static /* synthetic */ G d(IntUnaryOperator intUnaryOperator) {
        if (intUnaryOperator == null) {
            return null;
        }
        return intUnaryOperator instanceof O ? ((O) intUnaryOperator).a : new N(intUnaryOperator);
    }

    public /* synthetic */ G a(G g) {
        return d(this.a.andThen(O.a(g)));
    }

    public /* synthetic */ int b(int i) {
        return this.a.applyAsInt(i);
    }

    public /* synthetic */ G c(G g) {
        return d(this.a.compose(O.a(g)));
    }
}
