package j$.time.t;

import j$.time.LocalTime;
import j$.time.u.B;
import j$.time.u.E;
import j$.time.u.u;
import j$.time.u.x;
import j$.time.u.z;

public interface f extends u, x, Comparable {
    f D(z zVar);

    f I(long j, E e);

    int J(f fVar);

    f a(x xVar);

    q b();

    f c(B b, long j);

    boolean equals(Object obj);

    f g(long j, E e);

    boolean h(B b);

    int hashCode();

    int lengthOfYear();

    long toEpochDay();

    String toString();

    i u(LocalTime localTime);

    s y();
}
