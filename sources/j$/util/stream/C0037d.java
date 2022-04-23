package j$.util.stream;

import j$.util.y;
import java.util.concurrent.atomic.AtomicReference;

/* renamed from: j$.util.stream.d  reason: case insensitive filesystem */
abstract class CLASSNAMEd extends CLASSNAMEf {
    protected final AtomicReference h;
    protected volatile boolean i;

    protected CLASSNAMEd(CLASSNAMEd dVar, y yVar) {
        super((CLASSNAMEf) dVar, yVar);
        this.h = dVar.h;
    }

    protected CLASSNAMEd(CLASSNAMEz2 z2Var, y yVar) {
        super(z2Var, yVar);
        this.h = new AtomicReference((Object) null);
    }

    public Object b() {
        if (!e()) {
            return super.b();
        }
        Object obj = this.h.get();
        return obj == null ? k() : obj;
    }

    public void compute() {
        Object obj;
        y trySplit;
        y yVar = this.b;
        long estimateSize = yVar.estimateSize();
        long j = this.c;
        if (j == 0) {
            j = CLASSNAMEf.h(estimateSize);
            this.c = j;
        }
        boolean z = false;
        AtomicReference atomicReference = this.h;
        CLASSNAMEd dVar = this;
        while (true) {
            obj = atomicReference.get();
            if (obj != null) {
                break;
            }
            boolean z2 = dVar.i;
            if (!z2) {
                CLASSNAMEf c = dVar.c();
                while (true) {
                    CLASSNAMEd dVar2 = (CLASSNAMEd) c;
                    if (z2 || dVar2 == null) {
                        break;
                    }
                    z2 = dVar2.i;
                    c = dVar2.c();
                }
            }
            if (z2) {
                obj = dVar.k();
                break;
            } else if (estimateSize <= j || (trySplit = yVar.trySplit()) == null) {
                obj = dVar.a();
            } else {
                CLASSNAMEd dVar3 = (CLASSNAMEd) dVar.f(trySplit);
                dVar.d = dVar3;
                CLASSNAMEd dVar4 = (CLASSNAMEd) dVar.f(yVar);
                dVar.e = dVar4;
                dVar.setPendingCount(1);
                if (z) {
                    yVar = trySplit;
                    dVar = dVar3;
                    dVar3 = dVar4;
                } else {
                    dVar = dVar4;
                }
                z = !z;
                dVar3.fork();
                estimateSize = yVar.estimateSize();
            }
        }
        dVar.g(obj);
        dVar.tryComplete();
    }

    /* access modifiers changed from: protected */
    public void g(Object obj) {
        if (!e()) {
            super.g(obj);
        } else if (obj != null) {
            this.h.compareAndSet((Object) null, obj);
        }
    }

    public Object getRawResult() {
        return b();
    }

    /* access modifiers changed from: protected */
    public void i() {
        this.i = true;
    }

    /* access modifiers changed from: protected */
    public void j() {
        CLASSNAMEd dVar = this;
        for (CLASSNAMEd dVar2 = (CLASSNAMEd) c(); dVar2 != null; dVar2 = (CLASSNAMEd) dVar2.c()) {
            if (dVar2.d == dVar) {
                CLASSNAMEd dVar3 = (CLASSNAMEd) dVar2.e;
                if (!dVar3.i) {
                    dVar3.i();
                }
            }
            dVar = dVar2;
        }
    }

    /* access modifiers changed from: protected */
    public abstract Object k();

    /* access modifiers changed from: protected */
    public void l(Object obj) {
        if (obj != null) {
            this.h.compareAndSet((Object) null, obj);
        }
    }
}
