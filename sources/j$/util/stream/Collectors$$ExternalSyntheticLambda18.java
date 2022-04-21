package j$.util.stream;

import j$.util.function.BiConsumer;

public final /* synthetic */ class Collectors$$ExternalSyntheticLambda18 implements BiConsumer {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda18 INSTANCE = new Collectors$$ExternalSyntheticLambda18();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda18() {
    }

    public final void accept(Object obj, Object obj2) {
        ((StringBuilder) obj).append((CharSequence) obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
