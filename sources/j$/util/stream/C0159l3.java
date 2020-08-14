package j$.util.stream;

import j$.util.P;
import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.l3  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEl3 {
    public static void c(CLASSNAMEm3 _this, Consumer consumer) {
        if (consumer instanceof CLASSNAMEt) {
            _this.j((CLASSNAMEt) consumer);
        } else if (!h7.a) {
            ((P) _this.spliterator()).forEachRemaining(consumer);
        } else {
            h7.b(_this.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void a(CLASSNAMEm3 _this, Double[] boxed, int offset) {
        if (!h7.a) {
            double[] array = (double[]) _this.i();
            for (int i = 0; i < array.length; i++) {
                boxed[offset + i] = Double.valueOf(array[i]);
            }
            return;
        }
        h7.b(_this.getClass(), "{0} calling Node.OfDouble.copyInto(Double[], int)");
        throw null;
    }

    public static CLASSNAMEm3 g(CLASSNAMEm3 _this, long from, long to) {
        if (from == 0 && to == _this.count()) {
            return _this;
        }
        long size = to - from;
        P spliterator = (P) _this.spliterator();
        CLASSNAMEh3 nodeBuilder = CLASSNAMEp4.l(size);
        nodeBuilder.s(size);
        for (int i = 0; ((long) i) < from && spliterator.j(CLASSNAMEh0.a); i++) {
        }
        for (int i2 = 0; ((long) i2) < size && spliterator.j(nodeBuilder); i2++) {
        }
        nodeBuilder.r();
        return nodeBuilder.b();
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: MethodInlineVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.visitors.MethodInlineVisitor.inlineMth(MethodInlineVisitor.java:57)
        	at jadx.core.dex.visitors.MethodInlineVisitor.visit(MethodInlineVisitor.java:47)
        */
    public static /* synthetic */ void i() {
        /*
            r0 = 0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEl3.i():void");
    }

    public static double[] f(CLASSNAMEm3 _this, int count) {
        return new double[count];
    }

    public static CLASSNAMEv6 d(CLASSNAMEm3 _this) {
        return CLASSNAMEv6.DOUBLE_VALUE;
    }
}
