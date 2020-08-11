package j$.util.stream;

import java.util.Collections;
import java.util.EnumSet;

/* renamed from: j$.util.stream.o1  reason: case insensitive filesystem */
public final class CLASSNAMEo1 {
    static {
        Collections.unmodifiableSet(EnumSet.of(CLASSNAMEm1.CONCURRENT, CLASSNAMEm1.UNORDERED, CLASSNAMEm1.IDENTITY_FINISH));
        Collections.unmodifiableSet(EnumSet.of(CLASSNAMEm1.CONCURRENT, CLASSNAMEm1.UNORDERED));
        Collections.unmodifiableSet(EnumSet.of(CLASSNAMEm1.IDENTITY_FINISH));
        Collections.unmodifiableSet(EnumSet.of(CLASSNAMEm1.UNORDERED, CLASSNAMEm1.IDENTITY_FINISH));
        Collections.emptySet();
    }

    static double[] b(double[] intermediateSum, double value) {
        double tmp = value - intermediateSum[1];
        double sum = intermediateSum[0];
        double velvel = sum + tmp;
        intermediateSum[1] = (velvel - sum) - tmp;
        intermediateSum[0] = velvel;
        return intermediateSum;
    }

    static double a(double[] summands) {
        double tmp = summands[0] + summands[1];
        double simpleSum = summands[summands.length - 1];
        if (!Double.isNaN(tmp) || !Double.isInfinite(simpleSum)) {
            return tmp;
        }
        return simpleSum;
    }
}
