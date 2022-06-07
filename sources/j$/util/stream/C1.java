package j$.util.stream;

abstract class C1 implements A1 {
    protected final A1 a;
    protected final A1 b;
    private final long c;

    C1(A1 a1, A1 a12) {
        this.a = a1;
        this.b = a12;
        this.c = a1.count() + a12.count();
    }

    public A1 b(int i) {
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

    public int p() {
        return 2;
    }
}
