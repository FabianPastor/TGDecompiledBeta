package j$.time.format;

class q extends r {
    /* synthetic */ q(String x0, String x1, r x2, CLASSNAMEe x3) {
        this(x0, x1, x2);
    }

    private q(String k, String v, r child) {
        super(k, v, child, (CLASSNAMEe) null);
    }

    /* access modifiers changed from: protected */
    /* renamed from: k */
    public q e(String k, String v, r child) {
        return new q(k, v, child);
    }

    /* access modifiers changed from: protected */
    public boolean c(char c1, char c2) {
        return A.c(c1, c2);
    }

    /* access modifiers changed from: protected */
    public boolean i(CharSequence text, int off, int end) {
        int off2 = this.a.length();
        if (off2 > end - off) {
            return false;
        }
        int len = 0;
        while (true) {
            int len2 = off2 - 1;
            if (off2 <= 0) {
                return true;
            }
            int off0 = len + 1;
            char charAt = this.a.charAt(len);
            int off3 = off + 1;
            if (!c(charAt, text.charAt(off))) {
                return false;
            }
            int off4 = off3;
            len = off0;
            off = off4;
            off2 = len2;
        }
    }
}
