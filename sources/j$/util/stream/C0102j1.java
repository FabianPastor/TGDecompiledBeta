package j$.util.stream;

/* renamed from: j$.util.stream.j1  reason: case insensitive filesystem */
abstract class CLASSNAMEj1 {

    /* renamed from: a  reason: collision with root package name */
    protected final int var_a;
    protected int b;
    protected int c;
    protected long[] d;

    protected CLASSNAMEj1() {
        this.var_a = 4;
    }

    protected CLASSNAMEj1(int i) {
        if (i >= 0) {
            this.var_a = Math.max(4, 32 - Integer.numberOfLeadingZeros(i - 1));
            return;
        }
        throw new IllegalArgumentException("Illegal Capacity: " + i);
    }

    public abstract void clear();

    public long count() {
        int i = this.c;
        return i == 0 ? (long) this.b : this.d[i] + ((long) this.b);
    }

    /* access modifiers changed from: protected */
    public int s(int i) {
        return 1 << ((i == 0 || i == 1) ? this.var_a : Math.min((this.var_a + i) - 1, 30));
    }
}
