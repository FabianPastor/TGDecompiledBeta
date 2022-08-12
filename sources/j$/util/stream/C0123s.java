package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.BiConsumer;
import j$.util.function.m;
import j$.util.function.y;
import j$.util.u;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/* renamed from: j$.util.stream.s  reason: case insensitive filesystem */
class CLASSNAMEs extends CLASSNAMEc3 {
    CLASSNAMEs(CLASSNAMEc cVar, CLASSNAMEe4 e4Var, int i) {
        super(cVar, e4Var, i);
    }

    /* access modifiers changed from: package-private */
    public A1 E0(CLASSNAMEy2 y2Var, u uVar, m mVar) {
        if (CLASSNAMEd4.DISTINCT.d(y2Var.s0())) {
            return y2Var.p0(uVar, false, mVar);
        }
        if (CLASSNAMEd4.ORDERED.d(y2Var.s0())) {
            return L0(y2Var, uVar);
        }
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        new CLASSNAMEn0(new CLASSNAMEo(atomicBoolean, concurrentHashMap), false).c(y2Var, uVar);
        Set keySet = concurrentHashMap.keySet();
        if (atomicBoolean.get()) {
            HashSet hashSet = new HashSet(keySet);
            hashSet.add((Object) null);
            keySet = hashSet;
        }
        return new E1(keySet);
    }

    /* access modifiers changed from: package-private */
    public u F0(CLASSNAMEy2 y2Var, u uVar) {
        return CLASSNAMEd4.DISTINCT.d(y2Var.s0()) ? y2Var.w0(uVar) : CLASSNAMEd4.ORDERED.d(y2Var.s0()) ? ((E1) L0(y2Var, uVar)).spliterator() : new CLASSNAMEm4(y2Var.w0(uVar));
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEm3 H0(int i, CLASSNAMEm3 m3Var) {
        m3Var.getClass();
        return CLASSNAMEd4.DISTINCT.d(i) ? m3Var : CLASSNAMEd4.SORTED.d(i) ? new CLASSNAMEq(this, m3Var) : new r(this, m3Var);
    }

    /* access modifiers changed from: package-private */
    public A1 L0(CLASSNAMEy2 y2Var, u uVar) {
        CLASSNAMEp pVar = CLASSNAMEp.a;
        CLASSNAMEm mVar = CLASSNAMEm.a;
        return new E1((Collection) new CLASSNAMEz2(CLASSNAMEe4.REFERENCE, (BiConsumer) CLASSNAMEn.a, (BiConsumer) mVar, (y) pVar).c(y2Var, uVar));
    }
}
