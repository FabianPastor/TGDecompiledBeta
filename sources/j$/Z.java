package j$;

import j$.util.function.G;
import java.util.function.IntUnaryOperator;

public final /* synthetic */ class Z implements G {
    final /* synthetic */ IntUnaryOperator a;

    private /* synthetic */ Z(IntUnaryOperator intUnaryOperator) {
        this.a = intUnaryOperator;
    }

    public static /* synthetic */ G d(IntUnaryOperator intUnaryOperator) {
        if (intUnaryOperator == null) {
            return null;
        }
        return intUnaryOperator instanceof a0 ? ((a0) intUnaryOperator).a : new Z(intUnaryOperator);
    }

    public /* synthetic */ G a(G g) {
        return d(this.a.andThen(a0.a(g)));
    }

    public /* synthetic */ int b(int i) {
        return this.a.applyAsInt(i);
    }

    public /* synthetic */ G c(G g) {
        return d(this.a.compose(a0.a(g)));
    }
}
