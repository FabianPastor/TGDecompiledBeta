package j$.util.stream;

import java.util.Arrays;
/* loaded from: classes2.dex */
final class I3 extends E3 {
    private Y3 c;

    /* JADX INFO: Access modifiers changed from: package-private */
    public I3(InterfaceCLASSNAMEm3 interfaceCLASSNAMEm3) {
        super(interfaceCLASSNAMEm3);
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEl3, j$.util.function.q
    public void accept(long j) {
        this.c.accept(j);
    }

    @Override // j$.util.stream.AbstractCLASSNAMEh3, j$.util.stream.InterfaceCLASSNAMEm3
    public void m() {
        long[] jArr = (long[]) this.c.e();
        Arrays.sort(jArr);
        this.a.n(jArr.length);
        int i = 0;
        if (!this.b) {
            int length = jArr.length;
            while (i < length) {
                this.a.accept(jArr[i]);
                i++;
            }
        } else {
            int length2 = jArr.length;
            while (i < length2) {
                long j = jArr[i];
                if (this.a.o()) {
                    break;
                }
                this.a.accept(j);
                i++;
            }
        }
        this.a.m();
    }

    @Override // j$.util.stream.InterfaceCLASSNAMEm3
    public void n(long j) {
        if (j < NUM) {
            this.c = j > 0 ? new Y3((int) j) : new Y3();
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
