package a;

import j$.util.function.G;
import java.util.function.ObjDoubleConsumer;

/* renamed from: a.v0  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEv0 implements ObjDoubleConsumer {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ G var_a;

    private /* synthetic */ CLASSNAMEv0(G g) {
        this.var_a = g;
    }

    public static /* synthetic */ ObjDoubleConsumer a(G g) {
        if (g == null) {
            return null;
        }
        return g instanceof CLASSNAMEu0 ? ((CLASSNAMEu0) g).var_a : new CLASSNAMEv0(g);
    }

    public /* synthetic */ void accept(Object obj, double d) {
        this.var_a.accept(obj, d);
    }
}
