package j$.util.stream;

import j$.util.function.BiConsumer;

public final /* synthetic */ class LongPipeline$$ExternalSyntheticLambda1 implements BiConsumer {
    public static final /* synthetic */ LongPipeline$$ExternalSyntheticLambda1 INSTANCE = new LongPipeline$$ExternalSyntheticLambda1();

    private /* synthetic */ LongPipeline$$ExternalSyntheticLambda1() {
    }

    public final void accept(Object obj, Object obj2) {
        LongPipeline.lambda$average$3((long[]) obj, (long[]) obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
