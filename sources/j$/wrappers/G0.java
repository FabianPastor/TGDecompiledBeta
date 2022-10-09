package j$.wrappers;

import java.util.function.ToLongFunction;
/* loaded from: classes2.dex */
public final /* synthetic */ class G0 implements ToLongFunction {
    final /* synthetic */ j$.util.function.A a;

    private /* synthetic */ G0(j$.util.function.A a) {
        this.a = a;
    }

    public static /* synthetic */ ToLongFunction a(j$.util.function.A a) {
        if (a == null) {
            return null;
        }
        return a instanceof F0 ? ((F0) a).a : new G0(a);
    }

    @Override // java.util.function.ToLongFunction
    public /* synthetic */ long applyAsLong(Object obj) {
        return this.a.applyAsLong(obj);
    }
}
