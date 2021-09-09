package j$.wrappers;

import j$.util.function.m;
import java.util.function.IntFunction;

public final /* synthetic */ class U implements IntFunction {
    final /* synthetic */ m a;

    private /* synthetic */ U(m mVar) {
        this.a = mVar;
    }

    public static /* synthetic */ IntFunction a(m mVar) {
        if (mVar == null) {
            return null;
        }
        return mVar instanceof T ? ((T) mVar).a : new U(mVar);
    }

    public /* synthetic */ Object apply(int i) {
        return this.a.apply(i);
    }
}
