package j$;

import j$.util.function.Function;

public final /* synthetic */ class D implements Function {
    final /* synthetic */ java.util.function.Function a;

    private /* synthetic */ D(java.util.function.Function function) {
        this.a = function;
    }

    public static /* synthetic */ Function c(java.util.function.Function function) {
        if (function == null) {
            return null;
        }
        return function instanceof E ? ((E) function).a : new D(function);
    }

    public /* synthetic */ Function a(Function function) {
        return c(this.a.andThen(E.a(function)));
    }

    public /* synthetic */ Object apply(Object obj) {
        return this.a.apply(obj);
    }

    public /* synthetic */ Function b(Function function) {
        return c(this.a.compose(E.a(function)));
    }
}