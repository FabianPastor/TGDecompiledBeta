package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.A;
import j$.util.function.B;

/* renamed from: j$.util.stream.e4  reason: case insensitive filesystem */
final class CLASSNAMEe4 extends CLASSNAMEh4 implements D5 {
    private final int[] h;

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        t((Integer) obj);
    }

    public /* synthetic */ B q(B b) {
        return A.a(this, b);
    }

    public /* synthetic */ void t(Integer num) {
        C5.a(this, num);
    }

    CLASSNAMEe4(Spliterator spliterator, CLASSNAMEq4 helper, int[] array) {
        super(spliterator, helper, array.length);
        this.h = array;
    }

    CLASSNAMEe4(CLASSNAMEe4 parent, Spliterator spliterator, long offset, long length) {
        super(parent, spliterator, offset, length, parent.h.length);
        this.h = parent.h;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: c */
    public CLASSNAMEe4 a(Spliterator spliterator, long offset, long size) {
        return new CLASSNAMEe4(this, spliterator, offset, size);
    }

    public void accept(int value) {
        int i = this.f;
        if (i < this.g) {
            int[] iArr = this.h;
            this.f = i + 1;
            iArr[i] = value;
            return;
        }
        throw new IndexOutOfBoundsException(Integer.toString(this.f));
    }
}
