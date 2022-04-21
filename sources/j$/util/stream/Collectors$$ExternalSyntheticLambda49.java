package j$.util.stream;

import j$.util.function.Function;
import j$.util.function.Supplier;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda49 implements Function {
    public final /* synthetic */ Supplier f$0;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda49(Supplier supplier) {
        this.f$0 = supplier;
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return this.f$0.get();
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
