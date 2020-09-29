package j$.util.stream;

/* renamed from: j$.util.stream.j1  reason: case insensitive filesystem */
abstract class CLASSNAMEj1 {
    protected final int a;
    protected int b;
    protected int c;
    protected long[] d;

    public abstract void clear();

    protected CLASSNAMEj1() {
        this.a = 4;
    }

    protected CLASSNAMEj1(int initialCapacity) {
        if (initialCapacity >= 0) {
            this.a = Math.max(4, 32 - Integer.numberOfLeadingZeros(initialCapacity - 1));
            return;
        }
        throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
    }

    public long count() {
        int i = this.c;
        if (i == 0) {
            return (long) this.b;
        }
        return this.d[i] + ((long) this.b);
    }

    /* access modifiers changed from: protected */
    public int y(int n) {
        int power;
        if (n == 0 || n == 1) {
            power = this.a;
        } else {
            power = Math.min((this.a + n) - 1, 30);
        }
        return 1 << power;
    }
}
