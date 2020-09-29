package j$.time.format;

import java.text.ParsePosition;
import java.util.Iterator;
import java.util.Set;

class r {
    protected String a;
    protected String b;
    protected char c;
    protected r d;
    protected r e;

    /* synthetic */ r(String x0, String x1, r x2, CLASSNAMEe x3) {
        this(x0, x1, x2);
    }

    private r(String k, String v, r child) {
        this.a = k;
        this.b = v;
        this.d = child;
        if (k.length() == 0) {
            this.c = 65535;
        } else {
            this.c = this.a.charAt(0);
        }
    }

    public static r f(A context) {
        if (context.k()) {
            return new r("", (String) null, (r) null);
        }
        return new q("", (String) null, (r) null, (CLASSNAMEe) null);
    }

    public static r g(Set keys, A context) {
        r tree = f(context);
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            String k = (String) it.next();
            tree.b(k, k);
        }
        return tree;
    }

    public boolean a(String k, String v) {
        return b(k, v);
    }

    private boolean b(String k, String v) {
        String k2 = j(k);
        int prefixLen = h(k2);
        if (prefixLen != this.a.length()) {
            r n1 = e(this.a.substring(prefixLen), this.b, this.d);
            this.a = k2.substring(0, prefixLen);
            this.d = n1;
            if (prefixLen < k2.length()) {
                this.d.e = e(k2.substring(prefixLen), v, (r) null);
                this.b = null;
            } else {
                this.b = v;
            }
            return true;
        } else if (prefixLen < k2.length()) {
            String subKey = k2.substring(prefixLen);
            for (r c2 = this.d; c2 != null; c2 = c2.e) {
                if (c(c2.c, subKey.charAt(0))) {
                    return c2.b(subKey, v);
                }
            }
            r c3 = e(subKey, v, (r) null);
            c3.e = this.d;
            this.d = c3;
            return true;
        } else {
            this.b = v;
            return true;
        }
    }

    public String d(CharSequence text, ParsePosition pos) {
        int off = pos.getIndex();
        int end = text.length();
        if (!i(text, off, end)) {
            return null;
        }
        int off2 = off + this.a.length();
        if (this.d != null && off2 != end) {
            r c2 = this.d;
            while (true) {
                if (!c(c2.c, text.charAt(off2))) {
                    c2 = c2.e;
                    if (c2 == null) {
                        break;
                    }
                } else {
                    pos.setIndex(off2);
                    String found = c2.d(text, pos);
                    if (found != null) {
                        return found;
                    }
                }
            }
        }
        pos.setIndex(off2);
        return this.b;
    }

    /* access modifiers changed from: protected */
    public String j(String k) {
        return k;
    }

    /* access modifiers changed from: protected */
    public r e(String k, String v, r child) {
        return new r(k, v, child);
    }

    /* access modifiers changed from: protected */
    public boolean c(char c1, char c2) {
        return c1 == c2;
    }

    /* access modifiers changed from: protected */
    public boolean i(CharSequence text, int off, int end) {
        if (text instanceof String) {
            return ((String) text).startsWith(this.a, off);
        }
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

    private int h(String k) {
        int off = 0;
        while (off < k.length() && off < this.a.length() && c(k.charAt(off), this.a.charAt(off))) {
            off++;
        }
        return off;
    }
}
