package j$.util.stream;

import j$.util.Map;
import j$.util.function.Function;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda47 implements Function {
    public final /* synthetic */ Function f$0;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda47(Function function) {
        this.f$0 = function;
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return Map.EL.replaceAll((java.util.Map) obj, new Collectors$$ExternalSyntheticLambda24(this.f$0));
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
