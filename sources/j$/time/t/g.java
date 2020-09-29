package j$.time.t;

import j$.time.u.u;
import j$.time.u.x;
import java.io.Serializable;

abstract class g implements f, u, x, Serializable {
    static f A(q chrono, u temporal) {
        D other = (f) temporal;
        if (chrono.equals(other.b())) {
            return other;
        }
        throw new ClassCastException("Chronology mismatch, expected: " + chrono.getId() + ", actual: " + other.b().getId());
    }
}
