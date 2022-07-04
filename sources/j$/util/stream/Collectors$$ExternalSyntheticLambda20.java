package j$.util.stream;

import j$.util.function.BiConsumer;
import java.util.List;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda20 implements BiConsumer {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda20 INSTANCE = new Collectors$$ExternalSyntheticLambda20();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda20() {
    }

    public final void accept(Object obj, Object obj2) {
        ((List) obj).add(obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
