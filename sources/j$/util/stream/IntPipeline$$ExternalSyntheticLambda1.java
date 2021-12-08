package j$.util.stream;

import j$.util.function.BiConsumer;

public final /* synthetic */ class IntPipeline$$ExternalSyntheticLambda1 implements BiConsumer {
    public static final /* synthetic */ IntPipeline$$ExternalSyntheticLambda1 INSTANCE = new IntPipeline$$ExternalSyntheticLambda1();

    private /* synthetic */ IntPipeline$$ExternalSyntheticLambda1() {
    }

    public final void accept(Object obj, Object obj2) {
        IntPipeline.lambda$average$4((long[]) obj, (long[]) obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
