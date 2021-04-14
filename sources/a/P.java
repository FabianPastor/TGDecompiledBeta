package a;

import j$.util.function.Function;

public final /* synthetic */ class P implements Function {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ java.util.function.Function var_a;

    private /* synthetic */ P(java.util.function.Function function) {
        this.var_a = function;
    }

    public static /* synthetic */ Function c(java.util.function.Function function) {
        if (function == null) {
            return null;
        }
        return function instanceof Q ? ((Q) function).var_a : new P(function);
    }

    public /* synthetic */ Function a(Function function) {
        return c(this.var_a.andThen(Q.a(function)));
    }

    public /* synthetic */ Object apply(Object obj) {
        return this.var_a.apply(obj);
    }

    public /* synthetic */ Function b(Function function) {
        return c(this.var_a.compose(Q.a(function)));
    }
}
