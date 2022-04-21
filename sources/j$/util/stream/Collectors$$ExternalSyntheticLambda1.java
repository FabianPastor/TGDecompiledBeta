package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.Predicate;
import j$.util.stream.Collectors;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda1 implements BiConsumer {
    public final /* synthetic */ BiConsumer f$0;
    public final /* synthetic */ Predicate f$1;

    public /* synthetic */ Collectors$$ExternalSyntheticLambda1(BiConsumer biConsumer, Predicate predicate) {
        this.f$0 = biConsumer;
        this.f$1 = predicate;
    }

    public final void accept(Object obj, Object obj2) {
        Collectors.lambda$partitioningBy$54(this.f$0, this.f$1, (Collectors.Partition) obj, obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
