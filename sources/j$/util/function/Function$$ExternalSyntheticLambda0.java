package j$.util.function;

import j$.util.function.Function;

public final /* synthetic */ class Function$$ExternalSyntheticLambda0 implements Function {
    public final /* synthetic */ Function f$0;
    public final /* synthetic */ Function f$1;

    public /* synthetic */ Function$$ExternalSyntheticLambda0(Function function, Function function2) {
        this.f$0 = function;
        this.f$1 = function2;
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return this.f$1.apply(this.f$0.apply(obj));
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
