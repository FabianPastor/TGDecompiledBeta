package j$.util.function;

import j$.util.function.Function;
import j$.util.function.UnaryOperator;

public final /* synthetic */ class UnaryOperator$$ExternalSyntheticLambda0 implements UnaryOperator {
    public static final /* synthetic */ UnaryOperator$$ExternalSyntheticLambda0 INSTANCE = new UnaryOperator$$ExternalSyntheticLambda0();

    private /* synthetic */ UnaryOperator$$ExternalSyntheticLambda0() {
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return UnaryOperator.CC.lambda$identity$0(obj);
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
