package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.C;
import java.util.concurrent.CountedCompleter;
import java.util.stream.Node;
import java.util.stream.Sink;
import java.util.stream.SliceOps;

final class P5 extends CLASSNAMEi1 {
    private final CLASSNAMEh1 j;
    private final C k;
    private final long l;
    private final long m;
    private long n;
    private volatile boolean o;

    P5(CLASSNAMEh1 op, CLASSNAMEq4 helper, Spliterator spliterator, C c, long offset, long size) {
        super(helper, spliterator);
        this.j = op;
        this.k = c;
        this.l = offset;
        this.m = size;
    }

    P5(P5 parent, Spliterator spliterator) {
        super((CLASSNAMEi1) parent, spliterator);
        this.j = parent.j;
        this.k = parent.k;
        this.l = parent.l;
        this.m = parent.m;
    }

    /* access modifiers changed from: protected */
    /* renamed from: u */
    public P5 h(Spliterator spliterator) {
        return new P5(this, spliterator);
    }

    /* access modifiers changed from: protected */
    /* renamed from: s */
    public final CLASSNAMEt3 m() {
        return CLASSNAMEp4.m(this.j.A0());
    }

    /* access modifiers changed from: protected */
    /* renamed from: q */
    public final CLASSNAMEt3 a() {
        long sizeIfKnown = -1;
        if (g()) {
            if (CLASSNAMEu6.SIZED.L(this.j.c)) {
                sizeIfKnown = this.j.p0(this.b);
            }
            Node.Builder<P_OUT> nb = this.j.s0(sizeIfKnown, this.k);
            Sink<P_OUT> opSink = this.j.J0(this.a.r0(), nb);
            CLASSNAMEq4 q4Var = this.a;
            q4Var.d(q4Var.u0(opSink), this.b);
            return nb.b();
        }
        CLASSNAMEq4 q4Var2 = this.a;
        CLASSNAMEk3 s0 = q4Var2.s0(-1, this.k);
        q4Var2.t0(s0, this.b);
        Node<P_OUT> node = s0.b();
        this.n = node.count();
        this.o = true;
        this.b = null;
        return node;
    }

    public final void onCompletion(CountedCompleter caller) {
        Node<P_OUT> result;
        if (!e()) {
            this.n = ((P5) this.d).n + ((P5) this.e).n;
            if (this.i) {
                this.n = 0;
                result = m();
            } else if (this.n == 0) {
                result = m();
            } else if (((P5) this.d).n == 0) {
                result = (CLASSNAMEt3) ((P5) this.e).b();
            } else {
                result = CLASSNAMEp4.j(this.j.A0(), (CLASSNAMEt3) ((P5) this.d).b(), (CLASSNAMEt3) ((P5) this.e).b());
            }
            i(g() ? r(result) : result);
            this.o = true;
        }
        if (this.m >= 0 && !g() && t(this.l + this.m)) {
            l();
        }
        super.onCompletion(caller);
    }

    /* access modifiers changed from: protected */
    public void k() {
        super.k();
        if (this.o) {
            i(m());
        }
    }

    private CLASSNAMEt3 r(CLASSNAMEt3 input) {
        return input.c(this.l, this.m >= 0 ? Math.min(input.count(), this.l + this.m) : this.n, this.k);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: j$.util.stream.P5} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean t(long r9) {
        /*
            r8 = this;
            boolean r0 = r8.o
            if (r0 == 0) goto L_0x0007
            long r0 = r8.n
            goto L_0x000b
        L_0x0007:
            long r0 = r8.p(r9)
        L_0x000b:
            r2 = 1
            int r3 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r3 < 0) goto L_0x0011
            return r2
        L_0x0011:
            j$.util.stream.k1 r3 = r8.c()
            j$.util.stream.P5 r3 = (j$.util.stream.P5) r3
            r4 = r8
        L_0x0018:
            if (r3 == 0) goto L_0x0037
            j$.util.stream.k1 r5 = r3.e
            if (r4 != r5) goto L_0x002e
            j$.util.stream.k1 r5 = r3.d
            j$.util.stream.P5 r5 = (j$.util.stream.P5) r5
            if (r5 == 0) goto L_0x002e
            long r6 = r5.p(r9)
            long r0 = r0 + r6
            int r6 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r6 < 0) goto L_0x002e
            return r2
        L_0x002e:
            r4 = r3
            j$.util.stream.k1 r5 = r3.c()
            r3 = r5
            j$.util.stream.P5 r3 = (j$.util.stream.P5) r3
            goto L_0x0018
        L_0x0037:
            int r3 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r3 < 0) goto L_0x003c
            goto L_0x003d
        L_0x003c:
            r2 = 0
        L_0x003d:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.P5.t(long):boolean");
    }

    private long p(long target) {
        if (this.o) {
            return this.n;
        }
        SliceOps.SliceTask<P_IN, P_OUT> left = (P5) this.d;
        SliceOps.SliceTask<P_IN, P_OUT> right = (P5) this.e;
        if (left == null || right == null) {
            return this.n;
        }
        long leftSize = left.p(target);
        return leftSize >= target ? leftSize : right.p(target) + leftSize;
    }
}
