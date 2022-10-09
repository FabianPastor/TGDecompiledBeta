package j$.wrappers;

import java.util.function.IntFunction;
/* loaded from: classes2.dex */
public final /* synthetic */ class T implements j$.util.function.m {
    final /* synthetic */ IntFunction a;

    private /* synthetic */ T(IntFunction intFunction) {
        this.a = intFunction;
    }

    public static /* synthetic */ j$.util.function.m a(IntFunction intFunction) {
        if (intFunction == null) {
            return null;
        }
        return intFunction instanceof U ? ((U) intFunction).a : new T(intFunction);
    }

    @Override // j$.util.function.m
    public /* synthetic */ Object apply(int i) {
        return this.a.apply(i);
    }
}
