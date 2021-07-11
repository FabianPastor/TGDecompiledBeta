package j$;

import java.util.function.Function;

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

    public /* synthetic */ Function andThen(Function function) {
        return a(this.a.a(M.c(function)));
    }

    public /* synthetic */ Object apply(Object obj) {
        return this.a.apply(obj);
    }

    public /* synthetic */ Function compose(Function function) {
        return a(this.a.b(M.c(function)));
    }
}
