package j$.util;

import java.util.LongSummaryStatistics;

public class LongSummaryStatisticsConversions {
    private LongSummaryStatisticsConversions() {
    }

    public static LongSummaryStatistics convert(LongSummaryStatistics stats) {
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert to java.util.LongSummaryStatistics");
    }

    public static LongSummaryStatistics convert(LongSummaryStatistics stats) {
        throw new Error("Java 8+ API desugaring (library desugaring) cannot convert from java.util.LongSummaryStatistics");
    }
}
