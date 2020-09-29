package j$;

import j$.util.function.G;
import java.util.function.IntUnaryOperator;

public final /* synthetic */ class a0 implements IntUnaryOperator {
    final /* synthetic */ G a;

    private /* synthetic */ a0(G g) {
        this.a = g;
    }

    public static /* synthetic */ IntUnaryOperator a(G g) {
        if (g == null) {
            return null;
        }
        return g instanceof Z ? ((Z) g).a : new a0(g);
    }

    public /* synthetic */ IntUnaryOperator andThen(IntUnaryOperator intUnaryOperator) {
        return a(((Z) this.a).a(Z.d(intUnaryOperator)));
    }

    public /* synthetic */ int applyAsInt(int i) {
        return ((Z) this.a).b(i);
    }

    public /* synthetic */ IntUnaryOperator compose(IntUnaryOperator intUnaryOperator) {
        return a(((Z) this.a).c(Z.d(intUnaryOperator)));
    }
}
