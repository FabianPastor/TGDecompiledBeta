package j$.util;

import java.util.IntSummaryStatistics;

public class IntSummaryStatisticsConversions {
    private IntSummaryStatisticsConversions() {
    }

    public static IntSummaryStatistics convert(IntSummaryStatistics stats) {
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert to java.util.IntSummaryStatistics");
    }

    public static IntSummaryStatistics convert(IntSummaryStatistics stats) {
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.IntSummaryStatistics");
    }
}
