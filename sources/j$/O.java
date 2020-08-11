package j$;

import j$.util.function.G;
import java.util.function.IntUnaryOperator;

public final /* synthetic */ class O implements IntUnaryOperator {
    final /* synthetic */ G a;

    private /* synthetic */ O(G g) {
        this.a = g;
    }

    public static /* synthetic */ IntUnaryOperator a(G g) {
        if (g == null) {
            return null;
        }
        return g instanceof N ? ((N) g).a : new O(g);
    }

    public /* synthetic */ IntUnaryOperator andThen(IntUnaryOperator intUnaryOperator) {
        return a(((N) this.a).a(N.d(intUnaryOperator)));
    }

    public /* synthetic */ int applyAsInt(int i) {
        return ((N) this.a).b(i);
    }

    public /* synthetic */ IntUnaryOperator compose(IntUnaryOperator intUnaryOperator) {
        return a(((N) this.a).c(N.d(intUnaryOperator)));
    }
}
