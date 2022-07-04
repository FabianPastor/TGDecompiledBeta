package j$.util.stream;

import j$.util.function.Function;
import j$.util.stream.Collectors;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda52 implements Function {
    public final /* synthetic */ Collector f$0;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda52(Collector collector) {
        this.f$0 = collector;
    }

    public /* synthetic */ Function andThen(Function function) {
        return Function.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj) {
        return Collectors.lambda$partitioningBy$57(this.f$0, (Collectors.Partition) obj);
    }

    public /* synthetic */ Function compose(Function function) {
        return Function.CC.$default$compose(this, function);
    }
}
