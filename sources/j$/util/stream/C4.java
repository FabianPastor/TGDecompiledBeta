package j$.util.stream;

import j$.util.AbstractCLASSNAMEa;
import j$.util.function.Consumer;
import java.util.Comparator;
/* loaded from: classes2.dex */
final class C4 extends D4 implements j$.util.u {
    /* JADX INFO: Access modifiers changed from: package-private */
    public C4(j$.util.u uVar, long j, long j2) {
        super(uVar, j, j2, 0L, Math.min(uVar.estimateSize(), j2));
    }

    private C4(j$.util.u uVar, long j, long j2, long j3, long j4) {
        super(uVar, j, j2, j3, j4);
    }

    @Override // j$.util.stream.D4
    protected j$.util.u a(j$.util.u uVar, long j, long j2, long j3, long j4) {
        return new C4(uVar, j, j2, j3, j4);
    }

    @Override // j$.util.u
    public boolean b(Consumer consumer) {
        long j;
        consumer.getClass();
        if (this.a >= this.e) {
            return false;
        }
        while (true) {
            long j2 = this.a;
            j = this.d;
            if (j2 <= j) {
                break;
            }
            this.c.b(B4.a);
            this.d++;
        }
        if (j >= this.e) {
            return false;
        }
        this.d = j + 1;
        return this.c.b(consumer);
    }

    @Override // j$.util.u
    public void forEachRemaining(Consumer consumer) {
        consumer.getClass();
        long j = this.a;
        long j2 = this.e;
        if (j >= j2) {
            return;
        }
        long j3 = this.d;
        if (j3 >= j2) {
            return;
        }
        if (j3 >= j && this.c.estimateSize() + j3 <= this.b) {
            this.c.forEachRemaining(consumer);
            this.d = this.e;
            return;
        }
        while (this.a > this.d) {
            this.c.b(A4.a);
            this.d++;
        }
        while (this.d < this.e) {
            this.c.b(consumer);
            this.d++;
        }
    }

    @Override // j$.util.u
    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    @Override // j$.util.u
    public /* synthetic */ long getExactSizeIfKnown() {
        return AbstractCLASSNAMEa.e(this);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean hasCharacteristics(int i) {
        return AbstractCLASSNAMEa.f(this, i);
    }
}
