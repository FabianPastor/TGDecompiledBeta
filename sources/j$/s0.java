package j$;

import j$.util.function.G;
import java.util.function.ObjDoubleConsumer;

public final /* synthetic */ class s0 implements ObjDoubleConsumer {
    final /* synthetic */ G a;

    private /* synthetic */ s0(G g) {
        this.a = g;
    }

    public static /* synthetic */ ObjDoubleConsumer a(G g) {
        if (g == null) {
            return null;
        }
        return g instanceof CLASSNAMEr0 ? ((CLASSNAMEr0) g).a : new s0(g);
    }

    public /* synthetic */ void accept(Object obj, double d) {
        this.a.accept(obj, d);
    }
}
