package j$.util.stream;

import j$.util.function.BiConsumer;

public final /* synthetic */ class DoublePipeline$$ExternalSyntheticLambda2 implements BiConsumer {
    public static final /* synthetic */ DoublePipeline$$ExternalSyntheticLambda2 INSTANCE = new DoublePipeline$$ExternalSyntheticLambda2();

    private /* synthetic */ DoublePipeline$$ExternalSyntheticLambda2() {
    }

    public final void accept(Object obj, Object obj2) {
        DoublePipeline.lambda$sum$3((double[]) obj, (double[]) obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
