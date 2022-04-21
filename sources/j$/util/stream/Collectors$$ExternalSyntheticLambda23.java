package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.stream.Collectors;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda23 implements BiConsumer {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda23 INSTANCE = new Collectors$$ExternalSyntheticLambda23();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda23() {
    }

    public final void accept(Object obj, Object obj2) {
        ((Collectors.AnonymousClass1OptionalBox) obj).accept(obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
