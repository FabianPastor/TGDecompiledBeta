package j$.util.stream;

/* renamed from: j$.util.stream.n3  reason: case insensitive filesystem */
abstract class CLASSNAMEn3 implements CLASSNAMEl3 {
    protected final CLASSNAMEl3 a;
    protected final CLASSNAMEl3 b;
    private final long c;

    CLASSNAMEn3(CLASSNAMEl3 l3Var, CLASSNAMEl3 l3Var2) {
        this.a = l3Var;
        this.b = l3Var2;
        this.c = l3Var.count() + l3Var2.count();
    }

    public CLASSNAMEl3 b(int i) {
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

    public int o() {
        return 2;
    }
}
