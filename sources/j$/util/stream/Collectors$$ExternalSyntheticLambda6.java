package j$.util.stream;

import j$.util.Map;
import j$.util.Objects;
import j$.util.function.BiConsumer;
import j$.util.function.Function;
import j$.util.function.Supplier;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda6 implements BiConsumer {
    public final /* synthetic */ Function f$0;
    public final /* synthetic */ Supplier f$1;
    public final /* synthetic */ BiConsumer f$2;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda6(Function function, Supplier supplier, BiConsumer biConsumer) {
        this.f$0 = function;
        this.f$1 = supplier;
        this.f$2 = biConsumer;
    }

    public final void accept(Object obj, Object obj2) {
        this.f$2.accept(Map.EL.computeIfAbsent((java.util.Map) obj, Objects.requireNonNull(this.f$0.apply(obj2), "element cannot be mapped to a null key"), new Collectors$$ExternalSyntheticLambda49(this.f$1)), obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
