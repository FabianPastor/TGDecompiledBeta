package j$.wrappers;

import java.util.function.DoubleToIntFunction;
/* loaded from: classes2.dex */
public final /* synthetic */ class G {
    final /* synthetic */ DoubleToIntFunction a;

    private /* synthetic */ G(DoubleToIntFunction doubleToIntFunction) {
        this.a = doubleToIntFunction;
    }

    public static /* synthetic */ G b(DoubleToIntFunction doubleToIntFunction) {
        if (doubleToIntFunction == null) {
            return null;
        }
        return doubleToIntFunction instanceof H ? ((H) doubleToIntFunction).a : new G(doubleToIntFunction);
    }

    public int a(double d) {
        return this.a.applyAsInt(d);
    }
}
