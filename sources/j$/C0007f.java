package j$;

/* renamed from: j$.f  reason: case insensitive filesystem */
public /* synthetic */ class CLASSNAMEf {
    public static /* synthetic */ long a(long j, long j2) {
        long j3 = j / j2;
        return (j - (j2 * j3) != 0 && (((j ^ j2) >> 63) | 1) < 0) ? j3 - 1 : j3;
    }
}
