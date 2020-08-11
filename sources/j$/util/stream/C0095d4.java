package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.CLASSNAMEs;
import j$.util.function.CLASSNAMEt;

/* renamed from: j$.util.stream.d4  reason: case insensitive filesystem */
final class CLASSNAMEd4 extends CLASSNAMEh4 implements B5 {
    private final double[] h;

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        v((Double) obj);
    }

    public /* synthetic */ CLASSNAMEt p(CLASSNAMEt tVar) {
        return CLASSNAMEs.a(this, tVar);
    }

    public /* synthetic */ void v(Double d) {
        A5.a(this, d);
    }

    CLASSNAMEd4(Spliterator spliterator, CLASSNAMEq4 helper, double[] array) {
        super(spliterator, helper, array.length);
        this.h = array;
    }

    CLASSNAMEd4(CLASSNAMEd4 parent, Spliterator spliterator, long offset, long length) {
        super(parent, spliterator, offset, length, parent.h.length);
        this.h = parent.h;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: c */
    public CLASSNAMEd4 a(Spliterator spliterator, long offset, long size) {
        return new CLASSNAMEd4(this, spliterator, offset, size);
    }

    public void accept(double value) {
        int i = this.f;
        if (i < this.g) {
            double[] dArr = this.h;
            this.f = i + 1;
            dArr[i] = value;
            return;
        }
        throw new IndexOutOfBoundsException(Integer.toString(this.f));
    }
}
