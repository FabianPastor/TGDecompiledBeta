package j$.util.stream;

import java.util.concurrent.atomic.AtomicReference;
/* renamed from: j$.util.stream.d  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
abstract class AbstractCLASSNAMEd extends AbstractCLASSNAMEf {
    protected final AtomicReference h;
    protected volatile boolean i;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractCLASSNAMEd(AbstractCLASSNAMEd abstractCLASSNAMEd, j$.util.u uVar) {
        super(abstractCLASSNAMEd, uVar);
        this.h = abstractCLASSNAMEd.h;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractCLASSNAMEd(AbstractCLASSNAMEy2 abstractCLASSNAMEy2, j$.util.u uVar) {
        super(abstractCLASSNAMEy2, uVar);
        this.h = new AtomicReference(null);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEf
    public Object b() {
        if (e()) {
            Object obj = this.h.get();
            return obj == null ? k() : obj;
        }
        return super.b();
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x006b, code lost:
        r8 = r7.a();
     */
    @Override // j$.util.stream.AbstractCLASSNAMEf, java.util.concurrent.CountedCompleter
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void compute() {
        /*
            r10 = this;
            j$.util.u r0 = r10.b
            long r1 = r0.estimateSize()
            long r3 = r10.c
            r5 = 0
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 == 0) goto Lf
            goto L15
        Lf:
            long r3 = j$.util.stream.AbstractCLASSNAMEf.h(r1)
            r10.c = r3
        L15:
            r5 = 0
            java.util.concurrent.atomic.AtomicReference r6 = r10.h
            r7 = r10
        L19:
            java.lang.Object r8 = r6.get()
            if (r8 != 0) goto L6f
            boolean r8 = r7.i
            if (r8 != 0) goto L34
            j$.util.stream.f r9 = r7.c()
        L27:
            j$.util.stream.d r9 = (j$.util.stream.AbstractCLASSNAMEd) r9
            if (r8 != 0) goto L34
            if (r9 == 0) goto L34
            boolean r8 = r9.i
            j$.util.stream.f r9 = r9.c()
            goto L27
        L34:
            if (r8 == 0) goto L3b
            java.lang.Object r8 = r7.k()
            goto L6f
        L3b:
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L6b
            j$.util.u r1 = r0.mo322trySplit()
            if (r1 != 0) goto L46
            goto L6b
        L46:
            j$.util.stream.f r2 = r7.f(r1)
            j$.util.stream.d r2 = (j$.util.stream.AbstractCLASSNAMEd) r2
            r7.d = r2
            j$.util.stream.f r8 = r7.f(r0)
            j$.util.stream.d r8 = (j$.util.stream.AbstractCLASSNAMEd) r8
            r7.e = r8
            r9 = 1
            r7.setPendingCount(r9)
            if (r5 == 0) goto L60
            r0 = r1
            r7 = r2
            r2 = r8
            goto L61
        L60:
            r7 = r8
        L61:
            r5 = r5 ^ 1
            r2.fork()
            long r1 = r0.estimateSize()
            goto L19
        L6b:
            java.lang.Object r8 = r7.a()
        L6f:
            r7.g(r8)
            r7.tryComplete()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.AbstractCLASSNAMEd.compute():void");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // j$.util.stream.AbstractCLASSNAMEf
    public void g(Object obj) {
        if (!e()) {
            super.g(obj);
        } else if (obj == null) {
        } else {
            this.h.compareAndSet(null, obj);
        }
    }

    @Override // j$.util.stream.AbstractCLASSNAMEf, java.util.concurrent.CountedCompleter, java.util.concurrent.ForkJoinTask
    public Object getRawResult() {
        return b();
    }

    protected void i() {
        this.i = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void j() {
        AbstractCLASSNAMEd abstractCLASSNAMEd = this;
        for (AbstractCLASSNAMEd abstractCLASSNAMEd2 = (AbstractCLASSNAMEd) c(); abstractCLASSNAMEd2 != null; abstractCLASSNAMEd2 = (AbstractCLASSNAMEd) abstractCLASSNAMEd2.c()) {
            if (abstractCLASSNAMEd2.d == abstractCLASSNAMEd) {
                AbstractCLASSNAMEd abstractCLASSNAMEd3 = (AbstractCLASSNAMEd) abstractCLASSNAMEd2.e;
                if (!abstractCLASSNAMEd3.i) {
                    abstractCLASSNAMEd3.i();
                }
            }
            abstractCLASSNAMEd = abstractCLASSNAMEd2;
        }
    }

    protected abstract Object k();

    /* JADX INFO: Access modifiers changed from: protected */
    public void l(Object obj) {
        if (obj != null) {
            this.h.compareAndSet(null, obj);
        }
    }
}
