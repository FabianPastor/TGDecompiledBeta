package j$.time.u;

/* 'enum' modifier removed */
final class n extends q {
    n(String str, int i) {
        super(str, i, (l) null);
    }

    public G p() {
        return G.j(1, 4);
    }

    public boolean K(w temporal) {
        return temporal.h(j.MONTH_OF_YEAR) && q.a0(temporal);
    }

    public long A(w temporal) {
        if (K(temporal)) {
            return (2 + temporal.f(j.MONTH_OF_YEAR)) / 3;
        }
        throw new F("Unsupported field: QuarterOfYear");
    }

    public u L(u temporal, long newValue) {
        long curValue = A(temporal);
        p().b(newValue, this);
        j jVar = j.MONTH_OF_YEAR;
        return temporal.c(jVar, temporal.f(jVar) + ((newValue - curValue) * 3));
    }

    public String toString() {
        return "QuarterOfYear";
    }
}
