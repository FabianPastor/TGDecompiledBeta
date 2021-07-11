package j$;

import j$.util.function.x;
import java.util.function.IntFunction;

public final /* synthetic */ class T implements x {
    final /* synthetic */ IntFunction a;

    private /* synthetic */ T(IntFunction intFunction) {
        this.a = intFunction;
    }

    public static /* synthetic */ x a(IntFunction intFunction) {
        if (intFunction == null) {
            return null;
        }
        return intFunction instanceof U ? ((U) intFunction).a : new T(intFunction);
    }

    public /* synthetic */ Object apply(int i) {
        return this.a.apply(i);
    }
}
