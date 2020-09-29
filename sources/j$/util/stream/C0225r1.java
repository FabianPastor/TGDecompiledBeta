package j$.util.stream;

import j$.util.Spliterator;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.C;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/* renamed from: j$.util.stream.r1  reason: case insensitive filesystem */
class CLASSNAMEr1 extends CLASSNAMEs5 {
    CLASSNAMEr1(CLASSNAMEh1 upstream, CLASSNAMEv6 inputShape, int opFlags) {
        super(upstream, inputShape, opFlags);
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEt3 S0(CLASSNAMEq4 helper, Spliterator spliterator) {
        return CLASSNAMEp4.y((Collection) ((T4) V4.k(H0.a, CLASSNAMEg1.a, X0.a)).c(helper, spliterator));
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEt3 G0(CLASSNAMEq4 helper, Spliterator spliterator, C c) {
        if (CLASSNAMEu6.DISTINCT.K(helper.r0())) {
            return helper.e(spliterator, false, c);
        }
        if (CLASSNAMEu6.ORDERED.K(helper.r0())) {
            return S0(helper, spliterator);
        }
        AtomicBoolean seenNull = new AtomicBoolean(false);
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        ((Z1) CLASSNAMEc2.d(new CLASSNAMEn(seenNull, concurrentHashMap), false)).c(helper, spliterator);
        Set<T> keys = concurrentHashMap.keySet();
        if (seenNull.get()) {
            keys = new HashSet<>(keys);
            keys.add((Object) null);
        }
        return CLASSNAMEp4.y(keys);
    }

    static /* synthetic */ void R0(AtomicBoolean seenNull, ConcurrentHashMap map, Object t) {
        if (t == null) {
            seenNull.set(true);
        } else {
            map.putIfAbsent(t, Boolean.TRUE);
        }
    }

    /* access modifiers changed from: package-private */
    public Spliterator H0(CLASSNAMEq4 helper, Spliterator spliterator) {
        if (CLASSNAMEu6.DISTINCT.K(helper.r0())) {
            return helper.v0(spliterator);
        }
        if (CLASSNAMEu6.ORDERED.K(helper.r0())) {
            return ((CLASSNAMEx3) S0(helper, spliterator)).spliterator();
        }
        return new J6(helper.v0(spliterator));
    }

    /* access modifiers changed from: package-private */
    public G5 J0(int flags, G5 sink) {
        sink.getClass();
        if (CLASSNAMEu6.DISTINCT.K(flags)) {
            return sink;
        }
        return CLASSNAMEu6.SORTED.K(flags) ? new CLASSNAMEp1(this, sink) : new CLASSNAMEq1(this, sink);
    }
}
