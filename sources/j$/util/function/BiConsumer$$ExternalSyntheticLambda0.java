package j$.util.function;

import j$.util.function.BiConsumer;

public final /* synthetic */ class BiConsumer$$ExternalSyntheticLambda0 implements BiConsumer {
    public final /* synthetic */ BiConsumer f$0;
    public final /* synthetic */ BiConsumer f$1;

    public /* synthetic */ BiConsumer$$ExternalSyntheticLambda0(BiConsumer biConsumer, BiConsumer biConsumer2) {
        this.f$0 = biConsumer;
        this.f$1 = biConsumer2;
    }

    public final void accept(Object obj, Object obj2) {
        BiConsumer.CC.lambda$andThen$0(this.f$0, this.f$1, obj, obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
