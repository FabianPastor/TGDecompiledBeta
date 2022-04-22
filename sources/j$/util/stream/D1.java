package j$.util.stream;

abstract class D1 implements B1 {
    protected final B1 a;
    protected final B1 b;
    private final long c;

    D1(B1 b1, B1 b12) {
        this.a = b1;
        this.b = b12;
        this.c = b1.count() + b12.count();
    }

    public B1 b(int i) {
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
