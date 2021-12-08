package j$.util.stream;

import j$.util.function.IntFunction;
import j$.util.function.LongFunction;

public final /* synthetic */ class Nodes$CollectorTask$OfRef$$ExternalSyntheticLambda1 implements LongFunction {
    public final /* synthetic */ IntFunction f$0;

    public /* synthetic */ Nodes$CollectorTask$OfRef$$ExternalSyntheticLambda1(IntFunction intFunction) {
        this.f$0 = intFunction;
    }

    public final Object apply(long j) {
        return Nodes.builder(j, this.f$0);
    }
}
