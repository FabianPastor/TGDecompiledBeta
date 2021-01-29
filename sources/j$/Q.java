package j$;

import java.util.function.Function;

public final /* synthetic */ class Q implements Function {
    final /* synthetic */ j$.util.function.Function a;

    private /* synthetic */ Q(j$.util.function.Function function) {
        this.a = function;
    }

    public static /* synthetic */ Function a(j$.util.function.Function function) {
        if (function == null) {
            return null;
        }
        return function instanceof P ? ((P) function).a : new Q(function);
    }

    public /* synthetic */ Function andThen(Function function) {
        return a(this.a.a(P.c(function)));
    }

    public /* synthetic */ Object apply(Object obj) {
        return this.a.apply(obj);
    }

    public /* synthetic */ Function compose(Function function) {
        return a(this.a.b(P.c(function)));
    }
}
