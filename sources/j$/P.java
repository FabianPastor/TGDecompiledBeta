package j$;

import java.util.function.Function;

public final /* synthetic */ class P implements Function {
    final /* synthetic */ j$.util.function.Function a;

    private /* synthetic */ P(j$.util.function.Function function) {
        this.a = function;
    }

    public static /* synthetic */ Function a(j$.util.function.Function function) {
        if (function == null) {
            return null;
        }
        return function instanceof O ? ((O) function).a : new P(function);
    }

    public /* synthetic */ Function andThen(Function function) {
        return a(this.a.a(O.c(function)));
    }

    public /* synthetic */ Object apply(Object obj) {
        return this.a.apply(obj);
    }

    public /* synthetic */ Function compose(Function function) {
        return a(this.a.b(O.c(function)));
    }
}
