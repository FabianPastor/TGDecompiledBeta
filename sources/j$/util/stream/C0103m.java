package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.Consumer;
import java.util.concurrent.atomic.AtomicBoolean;

/* renamed from: j$.util.stream.m  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEm implements Consumer {
    public final /* synthetic */ AtomicBoolean a;
    public final /* synthetic */ ConcurrentHashMap b;

    public /* synthetic */ CLASSNAMEm(AtomicBoolean atomicBoolean, ConcurrentHashMap concurrentHashMap) {
        this.a = atomicBoolean;
        this.b = concurrentHashMap;
    }

    public final void accept(Object obj) {
        AtomicBoolean atomicBoolean = this.a;
        ConcurrentHashMap concurrentHashMap = this.b;
        if (obj == null) {
            atomicBoolean.set(true);
        } else {
            concurrentHashMap.putIfAbsent(obj, Boolean.TRUE);
        }
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}
