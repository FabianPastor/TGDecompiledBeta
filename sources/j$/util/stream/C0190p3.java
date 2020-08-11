package j$.util.stream;

import j$.util.U;
import j$.util.function.Consumer;
import j$.util.function.J;

/* renamed from: j$.util.stream.p3  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEp3 {
    public static void c(CLASSNAMEq3 _this, Consumer consumer) {
        if (consumer instanceof J) {
            _this.j((J) consumer);
        } else if (!h7.a) {
            ((U) _this.spliterator()).forEachRemaining(consumer);
        } else {
            h7.b(_this.getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void a(CLASSNAMEq3 _this, Long[] boxed, int offset) {
        if (!h7.a) {
            long[] array = (long[]) _this.i();
            for (int i = 0; i < array.length; i++) {
                boxed[offset + i] = Long.valueOf(array[i]);
            }
            return;
        }
        h7.b(_this.getClass(), "{0} calling Node.OfInt.copyInto(Long[], int)");
        throw null;
    }

    public static CLASSNAMEq3 g(CLASSNAMEq3 _this, long from, long to) {
        if (from == 0 && to == _this.count()) {
            return _this;
        }
        long size = to - from;
        U spliterator = (U) _this.spliterator();
        CLASSNAMEj3 nodeBuilder = CLASSNAMEp4.u(size);
        nodeBuilder.s(size);
        for (int i = 0; ((long) i) < from && spliterator.i(CLASSNAMEj0.a); i++) {
        }
        for (int i2 = 0; ((long) i2) < size && spliterator.i(nodeBuilder); i2++) {
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
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEp3.i():void");
    }

    public static long[] f(CLASSNAMEq3 _this, int count) {
        return new long[count];
    }

    public static CLASSNAMEv6 d(CLASSNAMEq3 _this) {
        return CLASSNAMEv6.LONG_VALUE;
    }
}
