package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.CLASSNAMEe;
import j$.util.function.Consumer;
import java.util.concurrent.atomic.AtomicBoolean;

/* renamed from: j$.util.stream.m  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEm implements Consumer {

    /* renamed from: a  reason: collision with root package name */
    public final /* synthetic */ AtomicBoolean var_a;
    public final /* synthetic */ ConcurrentHashMap b;

    public /* synthetic */ CLASSNAMEm(AtomicBoolean atomicBoolean, ConcurrentHashMap concurrentHashMap) {
        this.var_a = atomicBoolean;
        this.b = concurrentHashMap;
    }

    public final void accept(Object obj) {
        AtomicBoolean atomicBoolean = this.var_a;
        ConcurrentHashMap concurrentHashMap = this.b;
        if (obj == null) {
            atomicBoolean.set(true);
        } else {
            concurrentHashMap.putIfAbsent(obj, Boolean.TRUE);
        }
    }

    public Consumer f(Consumer consumer) {
        consumer.getClass();
        return new CLASSNAMEe(this, consumer);
    }
}
