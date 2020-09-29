package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.I;
import j$.util.function.J;

/* renamed from: j$.util.stream.f4  reason: case insensitive filesystem */
final class CLASSNAMEf4 extends CLASSNAMEh4 implements F5 {
    private final long[] h;

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        n((Long) obj);
    }

    public /* synthetic */ J h(J j) {
        return I.a(this, j);
    }

    public /* synthetic */ void n(Long l) {
        E5.a(this, l);
    }

    CLASSNAMEf4(Spliterator spliterator, CLASSNAMEq4 helper, long[] array) {
        super(spliterator, helper, array.length);
        this.h = array;
    }

    CLASSNAMEf4(CLASSNAMEf4 parent, Spliterator spliterator, long offset, long length) {
        super(parent, spliterator, offset, length, parent.h.length);
        this.h = parent.h;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: c */
    public CLASSNAMEf4 a(Spliterator spliterator, long offset, long size) {
        return new CLASSNAMEf4(this, spliterator, offset, size);
    }

    public void accept(long value) {
        int i = this.f;
        if (i < this.g) {
            long[] jArr = this.h;
            this.f = i + 1;
            jArr[i] = value;
            return;
        }
        throw new IndexOutOfBoundsException(Integer.toString(this.f));
    }
}
