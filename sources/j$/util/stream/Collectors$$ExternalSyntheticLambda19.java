package j$.util.stream;

import j$.util.function.BiConsumer;
import java.util.Collection;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda19 implements BiConsumer {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda19 INSTANCE = new Collectors$$ExternalSyntheticLambda19();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda19() {
    }

    public final void accept(Object obj, Object obj2) {
        ((Collection) obj).add(obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
