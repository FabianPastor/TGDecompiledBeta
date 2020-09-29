package j$.time.v;

import j$.time.LocalDateTime;
import j$.time.h;
import j$.time.i;
import j$.time.p;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class a implements Comparable, Serializable {
    private final LocalDateTime a;
    private final p b;
    private final p c;

    a(LocalDateTime transition, p offsetBefore, p offsetAfter) {
        this.a = transition;
        this.b = offsetBefore;
        this.c = offsetAfter;
    }

    a(long epochSecond, p offsetBefore, p offsetAfter) {
        this.a = LocalDateTime.W(epochSecond, 0, offsetBefore);
        this.b = offsetBefore;
        this.c = offsetAfter;
    }

    public i K() {
        return this.a.e0(this.b);
    }

    public long toEpochSecond() {
        return this.a.w(this.b);
    }

    public LocalDateTime r() {
        return this.a;
    }

    public LocalDateTime p() {
        return this.a.c0((long) A());
    }

    public p M() {
        return this.b;
    }

    public p L() {
        return this.c;
    }

    public h x() {
        return h.K((long) A());
    }

    private int A() {
        return L().U() - M().U();
    }

    public boolean P() {
        return L().U() > M().U();
    }

    /* access modifiers changed from: package-private */
    public List O() {
        if (P()) {
            return Collections.emptyList();
        }
        return Arrays.asList(new p[]{M(), L()});
    }

    /* renamed from: i */
    public int compareTo(a transition) {
        return K().compareTo(transition.K());
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof a)) {
            return false;
        }
        a d = (a) other;
        if (!this.a.equals(d.a) || !this.b.equals(d.b) || !this.c.equals(d.c)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (this.a.hashCode() ^ this.b.hashCode()) ^ Integer.rotateLeft(this.c.hashCode(), 16);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Transition[");
        buf.append(P() ? "Gap" : "Overlap");
        buf.append(" at ");
        buf.append(this.a);
        buf.append(this.b);
        buf.append(" to ");
        buf.append(this.c);
        buf.append(']');
        return buf.toString();
    }
}
