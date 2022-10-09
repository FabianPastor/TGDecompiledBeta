package j$.wrappers;

import java.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class N implements Function {
    final /* synthetic */ j$.util.function.Function a;

    private /* synthetic */ N(j$.util.function.Function function) {
        this.a = function;
    }

    public static /* synthetic */ Function a(j$.util.function.Function function) {
        if (function == null) {
            return null;
        }
        return function instanceof M ? ((M) function).a : new N(function);
    }

    @Override // java.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return a(this.a.andThen(M.a(function)));
    }

    @Override // java.util.function.Function
    public /* synthetic */ Object apply(Object obj) {
        return this.a.apply(obj);
    }

    @Override // java.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return a(this.a.compose(M.a(function)));
    }
}
