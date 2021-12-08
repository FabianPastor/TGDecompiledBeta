package j$.util.stream;

import j$.util.StringJoiner;
import j$.util.function.BiConsumer;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda22 implements BiConsumer {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda22 INSTANCE = new Collectors$$ExternalSyntheticLambda22();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda22() {
    }

    public final void accept(Object obj, Object obj2) {
        ((StringJoiner) obj).add((CharSequence) obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
