package j$.util.stream;

import j$.util.Spliterator;
import java.util.concurrent.atomic.AtomicReference;

/* renamed from: j$.util.stream.i1  reason: case insensitive filesystem */
abstract class CLASSNAMEi1 extends CLASSNAMEk1 {
    protected final AtomicReference h;
    protected volatile boolean i;

    /* access modifiers changed from: protected */
    public abstract Object m();

    protected CLASSNAMEi1(CLASSNAMEq4 helper, Spliterator spliterator) {
        super(helper, spliterator);
        this.h = new AtomicReference((Object) null);
    }

    protected CLASSNAMEi1(CLASSNAMEi1 parent, Spliterator spliterator) {
        super((CLASSNAMEk1) parent, spliterator);
        this.h = parent.h;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0054, code lost:
        r9 = r6.a();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void compute() {
        /*
            r13 = this;
            j$.util.Spliterator r0 = r13.b
            long r1 = r0.estimateSize()
            long r3 = r13.d(r1)
            r5 = 0
            r6 = r13
            java.util.concurrent.atomic.AtomicReference r7 = r13.h
        L_0x000e:
            java.lang.Object r8 = r7.get()
            r9 = r8
            if (r8 != 0) goto L_0x0058
            boolean r8 = r6.o()
            if (r8 == 0) goto L_0x0020
            java.lang.Object r9 = r6.m()
            goto L_0x0058
        L_0x0020:
            int r8 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r8 <= 0) goto L_0x0054
            j$.util.Spliterator r8 = r0.trySplit()
            r10 = r8
            if (r8 != 0) goto L_0x002c
            goto L_0x0054
        L_0x002c:
            j$.util.stream.k1 r8 = r6.h(r10)
            j$.util.stream.i1 r8 = (j$.util.stream.CLASSNAMEi1) r8
            r11 = r8
            r6.d = r8
            j$.util.stream.k1 r8 = r6.h(r0)
            j$.util.stream.i1 r8 = (j$.util.stream.CLASSNAMEi1) r8
            r12 = r8
            r6.e = r8
            r8 = 1
            r6.setPendingCount(r8)
            if (r5 == 0) goto L_0x0049
            r5 = 0
            r0 = r10
            r6 = r11
            r8 = r12
            goto L_0x004c
        L_0x0049:
            r5 = 1
            r6 = r12
            r8 = r11
        L_0x004c:
            r8.fork()
            long r1 = r0.estimateSize()
            goto L_0x000e
        L_0x0054:
            java.lang.Object r9 = r6.a()
        L_0x0058:
            r6.i(r9)
            r6.tryComplete()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEi1.compute():void");
    }

    /* access modifiers changed from: protected */
    public void n(Object result) {
        if (result != null) {
            this.h.compareAndSet((Object) null, result);
        }
    }

    /* access modifiers changed from: protected */
    public void i(Object localResult) {
        if (!g()) {
            super.i(localResult);
        } else if (localResult != null) {
            this.h.compareAndSet((Object) null, localResult);
        }
    }

    public Object getRawResult() {
        return b();
    }

    public Object b() {
        if (!g()) {
            return super.b();
        }
        R answer = this.h.get();
        return answer == null ? m() : answer;
    }

    /* access modifiers changed from: protected */
    public void k() {
        this.i = true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean o() {
        /*
            r3 = this;
            boolean r0 = r3.i
            if (r0 != 0) goto L_0x0018
            j$.util.stream.k1 r1 = r3.c()
            j$.util.stream.i1 r1 = (j$.util.stream.CLASSNAMEi1) r1
        L_0x000a:
            if (r0 != 0) goto L_0x0018
            if (r1 == 0) goto L_0x0018
            boolean r0 = r1.i
            j$.util.stream.k1 r2 = r1.c()
            r1 = r2
            j$.util.stream.i1 r1 = (j$.util.stream.CLASSNAMEi1) r1
            goto L_0x000a
        L_0x0018:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEi1.o():boolean");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: j$.util.stream.i1} */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void l() {
        /*
            r4 = this;
            j$.util.stream.k1 r0 = r4.c()
            j$.util.stream.i1 r0 = (j$.util.stream.CLASSNAMEi1) r0
            r1 = r4
        L_0x0007:
            if (r0 == 0) goto L_0x0021
            j$.util.stream.k1 r2 = r0.d
            if (r2 != r1) goto L_0x0018
            j$.util.stream.k1 r2 = r0.e
            j$.util.stream.i1 r2 = (j$.util.stream.CLASSNAMEi1) r2
            boolean r3 = r2.i
            if (r3 != 0) goto L_0x0018
            r2.k()
        L_0x0018:
            r1 = r0
            j$.util.stream.k1 r2 = r0.c()
            r0 = r2
            j$.util.stream.i1 r0 = (j$.util.stream.CLASSNAMEi1) r0
            goto L_0x0007
        L_0x0021:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEi1.l():void");
    }
}
