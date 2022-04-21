package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda24 implements BiFunction {
    public final /* synthetic */ Function f$0;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda24(Function function) {
        this.f$0 = function;
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return this.f$0.apply(obj2);
    }
}
