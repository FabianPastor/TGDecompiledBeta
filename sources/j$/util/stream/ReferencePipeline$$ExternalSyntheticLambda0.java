package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.function.Consumer;

public final /* synthetic */ class ReferencePipeline$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ BiConsumer f$0;
    public final /* synthetic */ Object f$1;

    public /* synthetic */ ReferencePipeline$$ExternalSyntheticLambda0(BiConsumer biConsumer, Object obj) {
        this.f$0 = biConsumer;
        this.f$1 = obj;
    }

    public final void accept(Object obj) {
        this.f$0.accept(this.f$1, obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}
