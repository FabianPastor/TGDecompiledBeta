package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.Consumer;
import j$.util.stream.DistinctOps;
import java.util.concurrent.atomic.AtomicBoolean;

public final /* synthetic */ class DistinctOps$1$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ AtomicBoolean f$0;
    public final /* synthetic */ ConcurrentHashMap f$1;

    public /* synthetic */ DistinctOps$1$$ExternalSyntheticLambda2(AtomicBoolean atomicBoolean, ConcurrentHashMap concurrentHashMap) {
        this.f$0 = atomicBoolean;
        this.f$1 = concurrentHashMap;
    }

    public final void accept(Object obj) {
        DistinctOps.AnonymousClass1.lambda$opEvaluateParallel$0(this.f$0, this.f$1, obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}
