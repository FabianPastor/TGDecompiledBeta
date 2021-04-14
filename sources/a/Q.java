package a;

import java.util.function.Function;

public final /* synthetic */ class Q implements Function {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j$.util.function.Function var_a;

    private /* synthetic */ Q(j$.util.function.Function function) {
        this.var_a = function;
    }

    public static /* synthetic */ Function a(j$.util.function.Function function) {
        if (function == null) {
            return null;
        }
        return function instanceof P ? ((P) function).var_a : new Q(function);
    }

    public /* synthetic */ Function andThen(Function function) {
        return a(this.var_a.a(P.c(function)));
    }

    public /* synthetic */ Object apply(Object obj) {
        return this.var_a.apply(obj);
    }

    public /* synthetic */ Function compose(Function function) {
        return a(this.var_a.b(P.c(function)));
    }
}
