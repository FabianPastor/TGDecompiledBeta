package j$.util.stream;

import j$.util.concurrent.ConcurrentMap;
import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda48 implements Function {
    public final /* synthetic */ Function f$0;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda48(Function function) {
        this.f$0 = function;
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return ConcurrentMap.EL.replaceAll((java.util.concurrent.ConcurrentMap) obj, new Collectors$$ExternalSyntheticLambda25(this.f$0));
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
