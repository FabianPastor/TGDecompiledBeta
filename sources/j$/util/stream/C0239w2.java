package j$.util.stream;

import j$.CLASSNAMEa0;
import j$.CLASSNAMEe0;
import j$.Y;
import j$.util.CLASSNAMEp;
import j$.util.CLASSNAMEt;
import j$.util.CLASSNAMEu;
import j$.util.D;
import j$.util.Spliterator;
import j$.util.V;
import j$.util.function.BiConsumer;
import j$.util.function.C;
import j$.util.function.E;
import j$.util.function.t;
import j$.util.function.u;
import j$.util.function.v;
import j$.util.function.w;
import j$.util.y;
import java.util.Iterator;

/* renamed from: j$.util.stream.w2  reason: case insensitive filesystem */
abstract class CLASSNAMEw2 extends CLASSNAMEh1 implements CLASSNAMEx2 {
    CLASSNAMEw2(Spliterator spliterator, int i, boolean z) {
        super(spliterator, i, z);
    }

    CLASSNAMEw2(CLASSNAMEh1 h1Var, int i) {
        super(h1Var, i);
    }

    /* access modifiers changed from: private */
    public static D L0(Spliterator spliterator) {
        if (spliterator instanceof D) {
            return (D) spliterator;
        }
        if (L6.a) {
            L6.a(CLASSNAMEh1.class, "using IntStream.adapt(Spliterator<Integer> s)");
            throw null;
        }
        throw new UnsupportedOperationException("IntStream.adapt(Spliterator<Integer> s)");
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEh6 A0() {
        return CLASSNAMEh6.INT_VALUE;
    }

    public void E(u uVar) {
        uVar.getClass();
        w0(new U1(uVar, true));
    }

    public final Stream F(v vVar) {
        vVar.getClass();
        return new CLASSNAMEj2(this, this, CLASSNAMEh6.INT_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s, vVar);
    }

    /* access modifiers changed from: package-private */
    public final Spliterator J0(CLASSNAMEi4 i4Var, E e, boolean z) {
        return new s6(i4Var, e, z);
    }

    public final int K(int i, t tVar) {
        tVar.getClass();
        return ((Integer) w0(new CLASSNAMEz4(CLASSNAMEh6.INT_VALUE, tVar, i))).intValue();
    }

    public final boolean L(Y y) {
        return ((Boolean) w0(CLASSNAMEc3.s(y, Z2.ALL))).booleanValue();
    }

    public final CLASSNAMEx2 M(v vVar) {
        return new CLASSNAMEp2(this, this, CLASSNAMEh6.INT_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s | CLASSNAMEg6.y, vVar);
    }

    public void Q(u uVar) {
        uVar.getClass();
        w0(new U1(uVar, false));
    }

    public final CLASSNAMEx2 W(CLASSNAMEe0 e0Var) {
        e0Var.getClass();
        return new CLASSNAMEh2(this, this, CLASSNAMEh6.INT_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s, e0Var);
    }

    public final CLASSNAMEu Y(t tVar) {
        tVar.getClass();
        return (CLASSNAMEu) w0(new B4(CLASSNAMEh6.INT_VALUE, tVar));
    }

    public final CLASSNAMEx2 Z(Y y) {
        y.getClass();
        return new CLASSNAMEs2(this, this, CLASSNAMEh6.INT_VALUE, CLASSNAMEg6.y, y);
    }

    public final CLASSNAMEx2 a0(u uVar) {
        uVar.getClass();
        return new CLASSNAMEc2(this, this, CLASSNAMEh6.INT_VALUE, 0, uVar);
    }

    public final L1 asDoubleStream() {
        return new CLASSNAMEf2(this, this, CLASSNAMEh6.INT_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s);
    }

    public final T2 asLongStream() {
        return new CLASSNAMEd2(this, this, CLASSNAMEh6.INT_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s);
    }

    public final CLASSNAMEt average() {
        long[] jArr = (long[]) j0(E.a, M.a, K.a);
        if (jArr[0] <= 0) {
            return CLASSNAMEt.a();
        }
        double d = (double) jArr[1];
        double d2 = (double) jArr[0];
        Double.isNaN(d);
        Double.isNaN(d2);
        return CLASSNAMEt.d(d / d2);
    }

    public final Stream boxed() {
        return F(CLASSNAMEd.a);
    }

    public final long count() {
        return ((S2) g(I.a)).sum();
    }

    public final CLASSNAMEx2 distinct() {
        return ((CLASSNAMEl5) ((CLASSNAMEl5) F(CLASSNAMEd.a)).distinct()).m(J.a);
    }

    public final boolean e0(Y y) {
        return ((Boolean) w0(CLASSNAMEc3.s(y, Z2.ANY))).booleanValue();
    }

    public final CLASSNAMEu findAny() {
        return (CLASSNAMEu) w0(new M1(false, CLASSNAMEh6.INT_VALUE, CLASSNAMEu.a(), R0.a, Y.a));
    }

    public final CLASSNAMEu findFirst() {
        return (CLASSNAMEu) w0(new M1(true, CLASSNAMEh6.INT_VALUE, CLASSNAMEu.a(), R0.a, Y.a));
    }

    public final T2 g(w wVar) {
        wVar.getClass();
        return new CLASSNAMEl2(this, this, CLASSNAMEh6.INT_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s, wVar);
    }

    public final L1 g0(CLASSNAMEa0 a0Var) {
        a0Var.getClass();
        return new CLASSNAMEn2(this, this, CLASSNAMEh6.INT_VALUE, CLASSNAMEg6.u | CLASSNAMEg6.s, a0Var);
    }

    public final boolean i0(Y y) {
        return ((Boolean) w0(CLASSNAMEc3.s(y, Z2.NONE))).booleanValue();
    }

    public final y iterator() {
        return V.g(spliterator());
    }

    /* renamed from: iterator  reason: collision with other method in class */
    public Iterator m24iterator() {
        return V.g(spliterator());
    }

    public final Object j0(E e, C c, BiConsumer biConsumer) {
        G g = new G(biConsumer);
        e.getClass();
        c.getClass();
        return w0(new D4(CLASSNAMEh6.INT_VALUE, g, c, e));
    }

    public final CLASSNAMEx2 limit(long j) {
        if (j >= 0) {
            return D5.g(this, 0, j);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final CLASSNAMEu max() {
        return Y(Q0.a);
    }

    public final CLASSNAMEu min() {
        return Y(C.a);
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEg3 s0(long j, v vVar) {
        return CLASSNAMEh4.p(j);
    }

    public final CLASSNAMEx2 skip(long j) {
        int i = (j > 0 ? 1 : (j == 0 ? 0 : -1));
        if (i >= 0) {
            return i == 0 ? this : D5.g(this, j, -1);
        }
        throw new IllegalArgumentException(Long.toString(j));
    }

    public final CLASSNAMEx2 sorted() {
        return new M5(this);
    }

    public final D spliterator() {
        return L0(super.spliterator());
    }

    public final int sum() {
        return ((Integer) w0(new CLASSNAMEz4(CLASSNAMEh6.INT_VALUE, L.a, 0))).intValue();
    }

    public final CLASSNAMEp summaryStatistics() {
        return (CLASSNAMEp) j0(CLASSNAMEd1.a, CLASSNAMEh.a, CLASSNAMEa1.a);
    }

    public final int[] toArray() {
        return (int[]) CLASSNAMEh4.n((CLASSNAMEi3) x0(H.a)).e();
    }

    public CLASSNAMEl1 unordered() {
        return !B0() ? this : new CLASSNAMEq2(this, this, CLASSNAMEh6.INT_VALUE, CLASSNAMEg6.w);
    }

    /* access modifiers changed from: package-private */
    public final CLASSNAMEl3 y0(CLASSNAMEi4 i4Var, Spliterator spliterator, boolean z, v vVar) {
        return CLASSNAMEh4.g(i4Var, spliterator, z);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:6:0x0015 A[LOOP:0: B:6:0x0015->B:9:0x001f, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void z0(j$.util.Spliterator r3, j$.util.stream.CLASSNAMEt5 r4) {
        /*
            r2 = this;
            j$.util.D r3 = L0(r3)
            boolean r0 = r4 instanceof j$.util.function.u
            if (r0 == 0) goto L_0x000c
            r0 = r4
            j$.util.function.u r0 = (j$.util.function.u) r0
            goto L_0x0015
        L_0x000c:
            boolean r0 = j$.util.stream.L6.a
            if (r0 != 0) goto L_0x0022
            j$.util.stream.c r0 = new j$.util.stream.c
            r0.<init>(r4)
        L_0x0015:
            boolean r1 = r4.p()
            if (r1 != 0) goto L_0x0021
            boolean r1 = r3.h(r0)
            if (r1 != 0) goto L_0x0015
        L_0x0021:
            return
        L_0x0022:
            java.lang.Class<j$.util.stream.h1> r3 = j$.util.stream.CLASSNAMEh1.class
            java.lang.String r4 = "using IntStream.adapt(Sink<Integer> s)"
            j$.util.stream.L6.a(r3, r4)
            r3 = 0
            goto L_0x002c
        L_0x002b:
            throw r3
        L_0x002c:
            goto L_0x002b
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEw2.z0(j$.util.Spliterator, j$.util.stream.t5):void");
    }
}
