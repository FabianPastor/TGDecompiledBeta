package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.Supplier;

public final /* synthetic */ class AbstractPipeline$$ExternalSyntheticLambda1 implements Supplier {
    public final /* synthetic */ Spliterator f$0;

    public /* synthetic */ AbstractPipeline$$ExternalSyntheticLambda1(Spliterator spliterator) {
        this.f$0 = spliterator;
    }

    public final Object get() {
        return AbstractPipeline.lambda$wrapSpliterator$1(this.f$0);
    }
}
