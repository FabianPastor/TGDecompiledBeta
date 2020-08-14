package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.CLASSNAMEq;
import j$.util.function.Consumer;
import java.util.concurrent.atomic.AtomicBoolean;

/* renamed from: j$.util.stream.n  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEn implements Consumer {
    public final /* synthetic */ AtomicBoolean a;
    public final /* synthetic */ ConcurrentHashMap b;

    public /* synthetic */ CLASSNAMEn(AtomicBoolean atomicBoolean, ConcurrentHashMap concurrentHashMap) {
        this.a = atomicBoolean;
        this.b = concurrentHashMap;
    }

    public final void accept(Object obj) {
        CLASSNAMEr1.R0(this.a, this.b, obj);
    }

    public /* synthetic */ Consumer g(Consumer consumer) {
        return CLASSNAMEq.a(this, consumer);
    }
}
