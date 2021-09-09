package j$.wrappers;

import j$.util.function.m;
import java.util.function.IntFunction;

public final /* synthetic */ class T implements m {
    final /* synthetic */ IntFunction a;

    private /* synthetic */ T(IntFunction intFunction) {
        this.a = intFunction;
    }

    public static /* synthetic */ m a(IntFunction intFunction) {
        if (intFunction == null) {
            return null;
        }
        return intFunction instanceof U ? ((U) intFunction).a : new T(intFunction);
    }

    public /* synthetic */ Object apply(int i) {
        return this.a.apply(i);
    }
}
