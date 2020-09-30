package j$;

import java.util.function.Function;

public final /* synthetic */ class E implements Function {
    final /* synthetic */ j$.util.function.Function a;

    private /* synthetic */ E(j$.util.function.Function function) {
        this.a = function;
    }

    public static /* synthetic */ Function a(j$.util.function.Function function) {
        if (function == null) {
            return null;
        }
        return function instanceof D ? ((D) function).a : new E(function);
    }

    public /* synthetic */ Function andThen(Function function) {
        return a(this.a.a(D.c(function)));
    }

    public /* synthetic */ Object apply(Object obj) {
        return this.a.apply(obj);
    }

    public /* synthetic */ Function compose(Function function) {
        return a(this.a.b(D.c(function)));
    }
}
