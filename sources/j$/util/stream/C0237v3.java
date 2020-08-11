package j$.util.stream;

/* renamed from: j$.util.stream.v3  reason: case insensitive filesystem */
abstract class CLASSNAMEv3 implements CLASSNAMEt3 {
    protected final CLASSNAMEt3 a;
    protected final CLASSNAMEt3 b;
    private final long c;

    public /* synthetic */ CLASSNAMEv6 b() {
        return CLASSNAMEg3.c(this);
    }

    CLASSNAMEv3(CLASSNAMEt3 left, CLASSNAMEt3 right) {
        this.a = left;
        this.b = right;
        this.c = left.count() + right.count();
    }

    public int w() {
        return 2;
    }

    public CLASSNAMEt3 d(int i) {
        if (i == 0) {
            return this.a;
        }
        if (i == 1) {
            return this.b;
        }
        throw new IndexOutOfBoundsException();
    }

    public long count() {
        return this.c;
    }
}
