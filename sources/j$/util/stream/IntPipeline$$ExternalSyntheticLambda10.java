package j$.util.stream;

import j$.util.IntSummaryStatistics;
import j$.util.function.ObjIntConsumer;

public final /* synthetic */ class IntPipeline$$ExternalSyntheticLambda10 implements ObjIntConsumer {
    public static final /* synthetic */ IntPipeline$$ExternalSyntheticLambda10 INSTANCE = new IntPipeline$$ExternalSyntheticLambda10();

    private /* synthetic */ IntPipeline$$ExternalSyntheticLambda10() {
    }

    public final void accept(Object obj, int i) {
        ((IntSummaryStatistics) obj).accept(i);
    }
}
