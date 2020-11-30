package j$;

import j$.util.function.G;
import java.util.function.ObjDoubleConsumer;

/* renamed from: j$.v0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEv0 implements ObjDoubleConsumer {
    final /* synthetic */ G a;

    private /* synthetic */ CLASSNAMEv0(G g) {
        this.a = g;
    }

    public static /* synthetic */ ObjDoubleConsumer a(G g) {
        if (g == null) {
            return null;
        }
        return g instanceof CLASSNAMEu0 ? ((CLASSNAMEu0) g).a : new CLASSNAMEv0(g);
    }

    public /* synthetic */ void accept(Object obj, double d) {
        this.a.accept(obj, d);
    }
}
