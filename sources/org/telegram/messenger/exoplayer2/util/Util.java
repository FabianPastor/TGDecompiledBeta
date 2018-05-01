package org.telegram.messenger.exoplayer2.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.SeekParameters;

public final class Util {
    private static final int[] CRC32_BYTES_MSBF = new int[]{0, 79764919, 159529838, 222504665, 319059676, 398814059, 445009330, 507990021, 638119352, 583659535, 797628118, 726387553, 890018660, 835552979, NUM, 944750013, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -734892656, -789352409, -575645954, -646886583, -952755380, -NUM, -827056094, -898286187, -231047128, -151282273, -71779514, -8804623, -515967244, -436212925, -390279782, -327299027, 881225847, 809987520, NUM, 969234094, 662832811, 591600412, 771767749, 717299826, 311336399, 374308984, 453813921, 533576470, 25881363, 88864420, 134795389, 214552010, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -525066777, -462094256, -382327159, -302564546, -206542021, -143559028, -97365931, -17609246, -960696225, -NUM, -817968335, -872425850, -709327229, -780559564, -600130067, -654598054, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, 622672798, 568075817, 748617968, 677256519, 907627842, 853037301, NUM, 995781531, 51762726, 131386257, 177728840, 240578815, 269590778, 349224269, 429104020, 491947555, -248556018, -168932423, -122852000, -60002089, -500490030, -420856475, -341238852, -278395381, -685261898, -739858943, -559578920, -630940305, -NUM, -NUM, -845023740, -916395085, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM, 295390185, 358241886, 404320391, 483945776, 43990325, 106832002, 186451547, 266083308, 932423249, 861060070, NUM, 986742920, 613929101, 542559546, 756411363, 701822548, -978770311, -NUM, -869589737, -924188512, -693284699, -764654318, -550540341, -605129092, -475935807, -413084042, -366743377, -287118056, -257573603, -194731862, -114850189, -35218492, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM, -NUM};
    public static final String DEVICE = Build.DEVICE;
    public static final String DEVICE_DEBUG_INFO;
    private static final Pattern ESCAPED_CHARACTER_PATTERN = Pattern.compile("%([A-Fa-f0-9]{2})");
    public static final String MANUFACTURER = Build.MANUFACTURER;
    public static final String MODEL = Build.MODEL;
    public static final int SDK_INT;
    private static final String TAG = "Util";
    private static final Pattern XS_DATE_TIME_PATTERN = Pattern.compile("(\\d\\d\\d\\d)\\-(\\d\\d)\\-(\\d\\d)[Tt](\\d\\d):(\\d\\d):(\\d\\d)([\\.,](\\d+))?([Zz]|((\\+|\\-)(\\d?\\d):?(\\d\\d)))?");
    private static final Pattern XS_DURATION_PATTERN = Pattern.compile("^(-)?P(([0-9]*)Y)?(([0-9]*)M)?(([0-9]*)D)?(T(([0-9]*)H)?(([0-9]*)M)?(([0-9.]*)S)?)?$");

    public static long addWithOverflowDefault(long j, long j2, long j3) {
        long j4 = j + j2;
        return ((j ^ j4) & (j2 ^ j4)) < 0 ? j3 : j4;
    }

    public static int compareLong(long j, long j2) {
        return j < j2 ? -1 : j == j2 ? 0 : 1;
    }

    public static int getAudioContentTypeForStreamType(int i) {
        switch (i) {
            case 0:
                return 1;
            case 1:
            case 2:
            case 4:
            case 5:
            case 8:
                return 4;
            default:
                return 2;
        }
    }

    public static int getAudioUsageForStreamType(int i) {
        switch (i) {
            case 0:
                return 2;
            case 1:
                return 13;
            case 2:
                return 6;
            case 4:
                return 4;
            case 5:
                return 5;
            case 8:
                return 3;
            default:
                return 1;
        }
    }

    public static int getPcmEncoding(int i) {
        return i != 8 ? i != 16 ? i != 24 ? i != 32 ? 0 : NUM : Integer.MIN_VALUE : 2 : 3;
    }

    public static int getStreamTypeForAudioUsage(int i) {
        switch (i) {
            case 1:
            case 12:
            case 14:
                return 3;
            case 2:
                return 0;
            case 3:
                return 8;
            case 4:
                return 4;
            case 5:
            case 7:
            case 8:
            case 9:
            case 10:
                return 5;
            case 6:
                return 2;
            case 13:
                return 1;
            default:
                return 3;
        }
    }

    public static boolean isEncodingHighResolutionIntegerPcm(int i) {
        if (i != Integer.MIN_VALUE) {
            if (i != NUM) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLinebreak(int i) {
        if (i != 10) {
            if (i != 13) {
                return false;
            }
        }
        return true;
    }

    private static boolean shouldEscapeCharacter(char c) {
        if (!(c == '\"' || c == '%' || c == '*' || c == '/' || c == ':' || c == '<' || c == '\\' || c == '|')) {
            switch (c) {
                case '>':
                case '?':
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    public static long subtractWithOverflowDefault(long j, long j2, long j3) {
        long j4 = j - j2;
        return ((j ^ j2) & (j ^ j4)) < 0 ? j3 : j4;
    }

    static {
        int i = (VERSION.SDK_INT == 25 && VERSION.CODENAME.charAt(0) == 'O') ? 26 : VERSION.SDK_INT;
        SDK_INT = i;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DEVICE);
        stringBuilder.append(", ");
        stringBuilder.append(MODEL);
        stringBuilder.append(", ");
        stringBuilder.append(MANUFACTURER);
        stringBuilder.append(", ");
        stringBuilder.append(SDK_INT);
        DEVICE_DEBUG_INFO = stringBuilder.toString();
    }

    private Util() {
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        byte[] bArr = new byte[4096];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(bArr, 0, read);
        }
    }

    @TargetApi(23)
    public static boolean maybeRequestReadExternalStoragePermission(Activity activity, Uri... uriArr) {
        if (SDK_INT < 23) {
            return false;
        }
        for (Uri isLocalFileUri : uriArr) {
            if (isLocalFileUri(isLocalFileUri)) {
                if (activity.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != null) {
                    activity.requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 0);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public static boolean isLocalFileUri(Uri uri) {
        uri = uri.getScheme();
        if (!TextUtils.isEmpty(uri)) {
            if (uri.equals("file") == null) {
                return null;
            }
        }
        return true;
    }

    public static boolean areEqual(Object obj, Object obj2) {
        if (obj == null) {
            return obj2 == null ? true : null;
        } else {
            return obj.equals(obj2);
        }
    }

    public static boolean contains(Object[] objArr, Object obj) {
        for (Object areEqual : objArr) {
            if (areEqual(areEqual, obj)) {
                return 1;
            }
        }
        return false;
    }

    public static <T> void removeRange(List<T> list, int i, int i2) {
        list.subList(i, i2).clear();
    }

    public static ExecutorService newSingleThreadExecutor(final String str) {
        return Executors.newSingleThreadExecutor(new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, str);
            }
        });
    }

    public static void closeQuietly(org.telegram.messenger.exoplayer2.upstream.DataSource r0) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        if (r0 == 0) goto L_0x0005;
    L_0x0002:
        r0.close();	 Catch:{ IOException -> 0x0005 }
    L_0x0005:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.util.Util.closeQuietly(org.telegram.messenger.exoplayer2.upstream.DataSource):void");
    }

    public static void closeQuietly(java.io.Closeable r0) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        if (r0 == 0) goto L_0x0005;
    L_0x0002:
        r0.close();	 Catch:{ IOException -> 0x0005 }
    L_0x0005:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.util.Util.closeQuietly(java.io.Closeable):void");
    }

    public static java.lang.String normalizeLanguageCode(java.lang.String r1) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        if (r1 != 0) goto L_0x0004;
    L_0x0002:
        r1 = 0;
        goto L_0x000e;
    L_0x0004:
        r0 = new java.util.Locale;	 Catch:{ MissingResourceException -> 0x000f }
        r0.<init>(r1);	 Catch:{ MissingResourceException -> 0x000f }
        r0 = r0.getISO3Language();	 Catch:{ MissingResourceException -> 0x000f }
        r1 = r0;
    L_0x000e:
        return r1;
    L_0x000f:
        r1 = r1.toLowerCase();
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.util.Util.normalizeLanguageCode(java.lang.String):java.lang.String");
    }

    public static String fromUtf8Bytes(byte[] bArr) {
        return new String(bArr, Charset.forName(C0542C.UTF8_NAME));
    }

    public static byte[] getUtf8Bytes(String str) {
        return str.getBytes(Charset.forName(C0542C.UTF8_NAME));
    }

    public static String toLowerInvariant(String str) {
        return str == null ? null : str.toLowerCase(Locale.US);
    }

    public static int ceilDivide(int i, int i2) {
        return ((i + i2) - 1) / i2;
    }

    public static long ceilDivide(long j, long j2) {
        return ((j + j2) - 1) / j2;
    }

    public static int constrainValue(int i, int i2, int i3) {
        return Math.max(i2, Math.min(i, i3));
    }

    public static long constrainValue(long j, long j2, long j3) {
        return Math.max(j2, Math.min(j, j3));
    }

    public static float constrainValue(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f, f3));
    }

    public static int binarySearchFloor(int[] iArr, int i, boolean z, boolean z2) {
        int binarySearch = Arrays.binarySearch(iArr, i);
        if (binarySearch < 0) {
            iArr = -(binarySearch + 2);
        } else {
            while (true) {
                binarySearch--;
                if (binarySearch < 0 || iArr[binarySearch] != i) {
                    iArr = z ? binarySearch + 1 : binarySearch;
                }
            }
            if (z) {
            }
        }
        return z2 ? Math.max(0, iArr) : iArr;
    }

    public static int binarySearchFloor(long[] jArr, long j, boolean z, boolean z2) {
        int binarySearch = Arrays.binarySearch(jArr, j);
        if (binarySearch < 0) {
            jArr = -(binarySearch + 2);
        } else {
            while (true) {
                binarySearch--;
                if (binarySearch < 0 || jArr[binarySearch] != j) {
                    jArr = z ? binarySearch + 1 : binarySearch;
                }
            }
            if (z) {
            }
        }
        return z2 ? Math.max(0, jArr) : jArr;
    }

    public static <T> int binarySearchFloor(List<? extends Comparable<? super T>> list, T t, boolean z, boolean z2) {
        int binarySearch = Collections.binarySearch(list, t);
        if (binarySearch < 0) {
            list = -(binarySearch + 2);
        } else {
            while (true) {
                binarySearch--;
                if (binarySearch < 0 || ((Comparable) list.get(binarySearch)).compareTo(t) != 0) {
                    list = z ? binarySearch + 1 : binarySearch;
                }
            }
            if (z) {
            }
        }
        return z2 ? Math.max(null, list) : list;
    }

    public static int binarySearchCeil(long[] jArr, long j, boolean z, boolean z2) {
        int binarySearch = Arrays.binarySearch(jArr, j);
        if (binarySearch < 0) {
            j = binarySearch ^ -1;
        } else {
            while (true) {
                binarySearch++;
                if (binarySearch >= jArr.length || jArr[binarySearch] != j) {
                    j = z ? binarySearch - 1 : binarySearch;
                }
            }
            if (z) {
            }
        }
        return z2 ? Math.min(jArr.length - 1, j) : j;
    }

    public static <T> int binarySearchCeil(List<? extends Comparable<? super T>> list, T t, boolean z, boolean z2) {
        int binarySearch = Collections.binarySearch(list, t);
        if (binarySearch < 0) {
            t = binarySearch ^ -1;
        } else {
            T t2;
            int size = list.size();
            while (true) {
                t2 = binarySearch + 1;
                if (t2 >= size || ((Comparable) list.get(t2)).compareTo(t) != 0) {
                    t = z ? t2 - 1 : t2;
                }
            }
            if (z) {
            }
        }
        return z2 ? Math.min(list.size() - 1, t) : t;
    }

    public static long parseXsDuration(String str) {
        Matcher matcher = XS_DURATION_PATTERN.matcher(str);
        if (!matcher.matches()) {
            return (long) ((Double.parseDouble(str) * 3600.0d) * 1000.0d);
        }
        str = 1 ^ TextUtils.isEmpty(matcher.group(1));
        String group = matcher.group(3);
        double d = 0.0d;
        double parseDouble = group != null ? Double.parseDouble(group) * 3.1556908E7d : 0.0d;
        group = matcher.group(5);
        parseDouble += group != null ? Double.parseDouble(group) * 2629739.0d : 0.0d;
        group = matcher.group(7);
        parseDouble += group != null ? Double.parseDouble(group) * 86400.0d : 0.0d;
        group = matcher.group(10);
        parseDouble += group != null ? 3600.0d * Double.parseDouble(group) : 0.0d;
        group = matcher.group(12);
        parseDouble += group != null ? Double.parseDouble(group) * 60.0d : 0.0d;
        String group2 = matcher.group(14);
        if (group2 != null) {
            d = Double.parseDouble(group2);
        }
        long j = (long) ((parseDouble + d) * 1000.0d);
        if (str != null) {
            j = -j;
        }
        return j;
    }

    public static long parseXsDateTime(String str) throws ParserException {
        Matcher matcher = XS_DATE_TIME_PATTERN.matcher(str);
        if (matcher.matches()) {
            int i = 0;
            if (matcher.group(9) != null) {
                if (matcher.group(9).equalsIgnoreCase("Z") == null) {
                    i = (Integer.parseInt(matcher.group(12)) * 60) + Integer.parseInt(matcher.group(13));
                    if (matcher.group(11).equals("-") != null) {
                        i *= -1;
                    }
                }
            }
            str = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
            str.clear();
            str.set(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)) - 1, Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(5)), Integer.parseInt(matcher.group(6)));
            if (!TextUtils.isEmpty(matcher.group(8))) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("0.");
                stringBuilder.append(matcher.group(8));
                str.set(14, new BigDecimal(stringBuilder.toString()).movePointRight(3).intValue());
            }
            long timeInMillis = str.getTimeInMillis();
            return i != 0 ? timeInMillis - ((long) (i * 60000)) : timeInMillis;
        } else {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Invalid date/time format: ");
            stringBuilder2.append(str);
            throw new ParserException(stringBuilder2.toString());
        }
    }

    public static long scaleLargeTimestamp(long j, long j2, long j3) {
        if (j3 >= j2 && j3 % j2 == 0) {
            return j / (j3 / j2);
        }
        if (j3 < j2 && j2 % j3 == 0) {
            return j * (j2 / j3);
        }
        return (long) (((double) j) * (((double) j2) / ((double) j3)));
    }

    public static long[] scaleLargeTimestamps(List<Long> list, long j, long j2) {
        long[] jArr = new long[list.size()];
        int i = 0;
        if (j2 >= j && j2 % j == 0) {
            j2 /= j;
            while (i < jArr.length) {
                jArr[i] = ((Long) list.get(i)).longValue() / j2;
                i++;
            }
        } else if (j2 >= j || j % j2 != 0) {
            j = ((double) j) / ((double) j2);
            while (i < jArr.length) {
                jArr[i] = (long) (((double) ((Long) list.get(i)).longValue()) * j);
                i++;
            }
        } else {
            j /= j2;
            while (i < jArr.length) {
                jArr[i] = ((Long) list.get(i)).longValue() * j;
                i++;
            }
        }
        return jArr;
    }

    public static void scaleLargeTimestampsInPlace(long[] jArr, long j, long j2) {
        int i = 0;
        if (j2 >= j && j2 % j == 0) {
            j2 /= j;
            while (i < jArr.length) {
                jArr[i] = jArr[i] / j2;
                i++;
            }
        } else if (j2 >= j || j % j2 != 0) {
            j = ((double) j) / ((double) j2);
            while (i < jArr.length) {
                jArr[i] = (long) (((double) jArr[i]) * j);
                i++;
            }
        } else {
            j /= j2;
            while (i < jArr.length) {
                jArr[i] = jArr[i] * j;
                i++;
            }
        }
    }

    public static long getMediaDurationForPlayoutDuration(long j, float f) {
        return f == 1.0f ? j : Math.round(((double) j) * ((double) f));
    }

    public static long getPlayoutDurationForMediaDuration(long j, float f) {
        return f == 1.0f ? j : Math.round(((double) j) / ((double) f));
    }

    public static long resolveSeekPositionUs(long j, SeekParameters seekParameters, long j2, long j3) {
        if (SeekParameters.EXACT.equals(seekParameters)) {
            return j;
        }
        long subtractWithOverflowDefault = subtractWithOverflowDefault(j, seekParameters.toleranceBeforeUs, Long.MIN_VALUE);
        long addWithOverflowDefault = addWithOverflowDefault(j, seekParameters.toleranceAfterUs, Long.MAX_VALUE);
        Object obj = null;
        seekParameters = (subtractWithOverflowDefault > j2 || j2 > addWithOverflowDefault) ? null : 1;
        if (subtractWithOverflowDefault <= j3 && j3 <= addWithOverflowDefault) {
            obj = 1;
        }
        if (seekParameters != null && obj != null) {
            return Math.abs(j2 - j) <= Math.abs(j3 - j) ? j2 : j3;
        } else {
            if (seekParameters != null) {
                return j2;
            }
            return obj != null ? j3 : subtractWithOverflowDefault;
        }
    }

    public static int[] toArray(List<Integer> list) {
        if (list == null) {
            return null;
        }
        int size = list.size();
        int[] iArr = new int[size];
        for (int i = 0; i < size; i++) {
            iArr[i] = ((Integer) list.get(i)).intValue();
        }
        return iArr;
    }

    public static int getIntegerCodeForString(String str) {
        int length = str.length();
        int i = 0;
        Assertions.checkArgument(length <= 4);
        int i2 = 0;
        while (i < length) {
            i2 = (i2 << 8) | str.charAt(i);
            i++;
        }
        return i2;
    }

    public static byte[] getBytesFromHexString(String str) {
        byte[] bArr = new byte[(str.length() / 2)];
        for (int i = 0; i < bArr.length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) ((Character.digit(str.charAt(i2), 16) << 4) + Character.digit(str.charAt(i2 + 1), 16));
        }
        return bArr;
    }

    public static String getCommaDelimitedSimpleClassNames(Object[] objArr) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < objArr.length; i++) {
            stringBuilder.append(objArr[i].getClass().getSimpleName());
            if (i < objArr.length - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    public static java.lang.String getUserAgent(android.content.Context r2, java.lang.String r3) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = r2.getPackageName();	 Catch:{ NameNotFoundException -> 0x0010 }
        r2 = r2.getPackageManager();	 Catch:{ NameNotFoundException -> 0x0010 }
        r1 = 0;	 Catch:{ NameNotFoundException -> 0x0010 }
        r2 = r2.getPackageInfo(r0, r1);	 Catch:{ NameNotFoundException -> 0x0010 }
        r2 = r2.versionName;	 Catch:{ NameNotFoundException -> 0x0010 }
        goto L_0x0012;
    L_0x0010:
        r2 = "?";
    L_0x0012:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r3);
        r3 = "/";
        r0.append(r3);
        r0.append(r2);
        r2 = " (Linux;Android ";
        r0.append(r2);
        r2 = android.os.Build.VERSION.RELEASE;
        r0.append(r2);
        r2 = ") ";
        r0.append(r2);
        r2 = "ExoPlayerLib/2.6.1";
        r0.append(r2);
        r2 = r0.toString();
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.util.Util.getUserAgent(android.content.Context, java.lang.String):java.lang.String");
    }

    public static String getCodecsOfType(String str, int i) {
        String str2 = null;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        str = str.trim().split("(\\s*,\\s*)");
        StringBuilder stringBuilder = new StringBuilder();
        for (String str3 : str) {
            if (i == MimeTypes.getTrackTypeOfCodec(str3)) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(str3);
            }
        }
        if (stringBuilder.length() > null) {
            str2 = stringBuilder.toString();
        }
        return str2;
    }

    public static int getPcmFrameSize(int i, int i2) {
        if (i == Integer.MIN_VALUE) {
            return i2 * 3;
        }
        if (i != NUM) {
            switch (i) {
                case 2:
                    return i2 * 2;
                case 3:
                    return i2;
                case 4:
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return i2 * 4;
    }

    public static java.util.UUID getDrmUuid(java.lang.String r3) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = toLowerInvariant(r3);
        r1 = r0.hashCode();
        r2 = -NUM; // 0xffffffff911c2eef float:-1.2320693E-28 double:NaN;
        if (r1 == r2) goto L_0x002c;
    L_0x000d:
        r2 = -NUM; // 0xffffffffac8548fd float:-3.7881907E-12 double:NaN;
        if (r1 == r2) goto L_0x0022;
    L_0x0012:
        r2 = 790309106; // 0x2f1b28f2 float:1.4111715E-10 double:3.90464579E-315;
        if (r1 == r2) goto L_0x0018;
    L_0x0017:
        goto L_0x0036;
    L_0x0018:
        r1 = "clearkey";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0036;
    L_0x0020:
        r0 = 2;
        goto L_0x0037;
    L_0x0022:
        r1 = "widevine";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0036;
    L_0x002a:
        r0 = 0;
        goto L_0x0037;
    L_0x002c:
        r1 = "playready";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0036;
    L_0x0034:
        r0 = 1;
        goto L_0x0037;
    L_0x0036:
        r0 = -1;
    L_0x0037:
        switch(r0) {
            case 0: goto L_0x0045;
            case 1: goto L_0x0042;
            case 2: goto L_0x003f;
            default: goto L_0x003a;
        };
    L_0x003a:
        r3 = java.util.UUID.fromString(r3);	 Catch:{ RuntimeException -> 0x0049 }
        goto L_0x0048;
    L_0x003f:
        r3 = org.telegram.messenger.exoplayer2.C0542C.CLEARKEY_UUID;
        return r3;
    L_0x0042:
        r3 = org.telegram.messenger.exoplayer2.C0542C.PLAYREADY_UUID;
        return r3;
    L_0x0045:
        r3 = org.telegram.messenger.exoplayer2.C0542C.WIDEVINE_UUID;
        return r3;
    L_0x0048:
        return r3;
    L_0x0049:
        r3 = 0;
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.util.Util.getDrmUuid(java.lang.String):java.util.UUID");
    }

    public static int inferContentType(Uri uri) {
        String path = uri.getPath();
        if (path == null) {
            return 3;
        }
        return inferContentType(path);
    }

    public static int inferContentType(String str) {
        str = toLowerInvariant(str);
        if (str.endsWith(".mpd")) {
            return null;
        }
        if (str.endsWith(".m3u8")) {
            return 2;
        }
        return str.matches(".*\\.ism(l)?(/manifest(\\(.+\\))?)?") != null ? 1 : 3;
    }

    public static String getStringForTime(StringBuilder stringBuilder, Formatter formatter, long j) {
        if (j == C0542C.TIME_UNSET) {
            j = 0;
        }
        long j2 = (j + 500) / 1000;
        long j3 = j2 % 60;
        long j4 = (j2 / 60) % 60;
        j2 /= 3600;
        stringBuilder.setLength(0);
        if (j2 > 0) {
            return formatter.format("%d:%02d:%02d", new Object[]{Long.valueOf(j2), Long.valueOf(j4), Long.valueOf(j3)}).toString();
        }
        return formatter.format("%02d:%02d", new Object[]{Long.valueOf(j4), Long.valueOf(j3)}).toString();
    }

    public static int getDefaultBufferSize(int i) {
        switch (i) {
            case 0:
                return 16777216;
            case 1:
                return C0542C.DEFAULT_AUDIO_BUFFER_SIZE;
            case 2:
                return C0542C.DEFAULT_VIDEO_BUFFER_SIZE;
            case 3:
                return 131072;
            case 4:
                return 131072;
            default:
                throw new IllegalStateException();
        }
    }

    public static String escapeFileName(String str) {
        int length = str.length();
        int i = 0;
        int i2 = 0;
        int i3 = i2;
        while (i2 < length) {
            if (shouldEscapeCharacter(str.charAt(i2))) {
                i3++;
            }
            i2++;
        }
        if (i3 == 0) {
            return str;
        }
        StringBuilder stringBuilder = new StringBuilder((i3 * 2) + length);
        while (i3 > 0) {
            int i4 = i + 1;
            char charAt = str.charAt(i);
            if (shouldEscapeCharacter(charAt)) {
                stringBuilder.append('%');
                stringBuilder.append(Integer.toHexString(charAt));
                i3--;
            } else {
                stringBuilder.append(charAt);
            }
            i = i4;
        }
        if (i < length) {
            stringBuilder.append(str, i, length);
        }
        return stringBuilder.toString();
    }

    public static String unescapeFileName(String str) {
        int length = str.length();
        int i = 0;
        int i2 = 0;
        int i3 = i2;
        while (i2 < length) {
            if (str.charAt(i2) == '%') {
                i3++;
            }
            i2++;
        }
        if (i3 == 0) {
            return str;
        }
        i2 = length - (i3 * 2);
        StringBuilder stringBuilder = new StringBuilder(i2);
        Matcher matcher = ESCAPED_CHARACTER_PATTERN.matcher(str);
        while (i3 > 0 && matcher.find()) {
            char parseInt = (char) Integer.parseInt(matcher.group(1), 16);
            stringBuilder.append(str, i, matcher.start());
            stringBuilder.append(parseInt);
            i = matcher.end();
            i3--;
        }
        if (i < length) {
            stringBuilder.append(str, i, length);
        }
        if (stringBuilder.length() != i2) {
            return null;
        }
        return stringBuilder.toString();
    }

    public static void sneakyThrow(Throwable th) {
        sneakyThrowInternal(th);
    }

    private static <T extends Throwable> void sneakyThrowInternal(Throwable th) throws Throwable {
        throw th;
    }

    public static void recursiveDelete(File file) {
        if (file.isDirectory()) {
            for (File recursiveDelete : file.listFiles()) {
                recursiveDelete(recursiveDelete);
            }
        }
        file.delete();
    }

    public static File createTempDirectory(Context context, String str) throws IOException {
        context = createTempFile(context, str);
        context.delete();
        context.mkdir();
        return context;
    }

    public static File createTempFile(Context context, String str) throws IOException {
        return File.createTempFile(str, null, context.getCacheDir());
    }

    public static int crc(byte[] bArr, int i, int i2, int i3) {
        while (i < i2) {
            i3 = CRC32_BYTES_MSBF[((i3 >>> 24) ^ (bArr[i] & 255)) & 255] ^ (i3 << 8);
            i++;
        }
        return i3;
    }

    public static Point getPhysicalDisplaySize(Context context) {
        return getPhysicalDisplaySize(context, ((WindowManager) context.getSystemService("window")).getDefaultDisplay());
    }

    public static android.graphics.Point getPhysicalDisplaySize(android.content.Context r6, android.view.Display r7) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = SDK_INT;
        r1 = 25;
        if (r0 >= r1) goto L_0x00b5;
    L_0x0006:
        r0 = r7.getDisplayId();
        if (r0 != 0) goto L_0x00b5;
    L_0x000c:
        r0 = "Sony";
        r1 = MANUFACTURER;
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0036;
    L_0x0016:
        r0 = MODEL;
        r1 = "BRAVIA";
        r0 = r0.startsWith(r1);
        if (r0 == 0) goto L_0x0036;
    L_0x0020:
        r6 = r6.getPackageManager();
        r0 = "com.sony.dtv.hardware.panel.qfhd";
        r6 = r6.hasSystemFeature(r0);
        if (r6 == 0) goto L_0x0036;
    L_0x002c:
        r6 = new android.graphics.Point;
        r7 = 3840; // 0xf00 float:5.381E-42 double:1.897E-320;
        r0 = 2160; // 0x870 float:3.027E-42 double:1.067E-320;
        r6.<init>(r7, r0);
        return r6;
    L_0x0036:
        r6 = "NVIDIA";
        r0 = MANUFACTURER;
        r6 = r6.equals(r0);
        if (r6 == 0) goto L_0x00b5;
    L_0x0040:
        r6 = MODEL;
        r0 = "SHIELD";
        r6 = r6.contains(r0);
        if (r6 == 0) goto L_0x00b5;
    L_0x004a:
        r6 = 0;
        r0 = 0;
        r1 = 1;
        r2 = "android.os.SystemProperties";	 Catch:{ Exception -> 0x006d }
        r2 = java.lang.Class.forName(r2);	 Catch:{ Exception -> 0x006d }
        r3 = "get";	 Catch:{ Exception -> 0x006d }
        r4 = new java.lang.Class[r1];	 Catch:{ Exception -> 0x006d }
        r5 = java.lang.String.class;	 Catch:{ Exception -> 0x006d }
        r4[r0] = r5;	 Catch:{ Exception -> 0x006d }
        r3 = r2.getMethod(r3, r4);	 Catch:{ Exception -> 0x006d }
        r4 = new java.lang.Object[r1];	 Catch:{ Exception -> 0x006d }
        r5 = "sys.display-size";	 Catch:{ Exception -> 0x006d }
        r4[r0] = r5;	 Catch:{ Exception -> 0x006d }
        r2 = r3.invoke(r2, r4);	 Catch:{ Exception -> 0x006d }
        r2 = (java.lang.String) r2;	 Catch:{ Exception -> 0x006d }
        r6 = r2;
        goto L_0x0075;
    L_0x006d:
        r2 = move-exception;
        r3 = "Util";
        r4 = "Failed to read sys.display-size";
        android.util.Log.e(r3, r4, r2);
    L_0x0075:
        r2 = android.text.TextUtils.isEmpty(r6);
        if (r2 != 0) goto L_0x00b5;
    L_0x007b:
        r2 = r6.trim();	 Catch:{ NumberFormatException -> 0x009f }
        r3 = "x";	 Catch:{ NumberFormatException -> 0x009f }
        r2 = r2.split(r3);	 Catch:{ NumberFormatException -> 0x009f }
        r3 = r2.length;	 Catch:{ NumberFormatException -> 0x009f }
        r4 = 2;	 Catch:{ NumberFormatException -> 0x009f }
        if (r3 != r4) goto L_0x009f;	 Catch:{ NumberFormatException -> 0x009f }
    L_0x0089:
        r0 = r2[r0];	 Catch:{ NumberFormatException -> 0x009f }
        r0 = java.lang.Integer.parseInt(r0);	 Catch:{ NumberFormatException -> 0x009f }
        r1 = r2[r1];	 Catch:{ NumberFormatException -> 0x009f }
        r1 = java.lang.Integer.parseInt(r1);	 Catch:{ NumberFormatException -> 0x009f }
        if (r0 <= 0) goto L_0x009f;	 Catch:{ NumberFormatException -> 0x009f }
    L_0x0097:
        if (r1 <= 0) goto L_0x009f;	 Catch:{ NumberFormatException -> 0x009f }
    L_0x0099:
        r2 = new android.graphics.Point;	 Catch:{ NumberFormatException -> 0x009f }
        r2.<init>(r0, r1);	 Catch:{ NumberFormatException -> 0x009f }
        return r2;
    L_0x009f:
        r0 = "Util";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Invalid sys.display-size: ";
        r1.append(r2);
        r1.append(r6);
        r6 = r1.toString();
        android.util.Log.e(r0, r6);
    L_0x00b5:
        r6 = new android.graphics.Point;
        r6.<init>();
        r0 = SDK_INT;
        r1 = 23;
        if (r0 < r1) goto L_0x00c4;
    L_0x00c0:
        getDisplaySizeV23(r7, r6);
        goto L_0x00db;
    L_0x00c4:
        r0 = SDK_INT;
        r1 = 17;
        if (r0 < r1) goto L_0x00ce;
    L_0x00ca:
        getDisplaySizeV17(r7, r6);
        goto L_0x00db;
    L_0x00ce:
        r0 = SDK_INT;
        r1 = 16;
        if (r0 < r1) goto L_0x00d8;
    L_0x00d4:
        getDisplaySizeV16(r7, r6);
        goto L_0x00db;
    L_0x00d8:
        getDisplaySizeV9(r7, r6);
    L_0x00db:
        return r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.util.Util.getPhysicalDisplaySize(android.content.Context, android.view.Display):android.graphics.Point");
    }

    @TargetApi(23)
    private static void getDisplaySizeV23(Display display, Point point) {
        display = display.getMode();
        point.x = display.getPhysicalWidth();
        point.y = display.getPhysicalHeight();
    }

    @TargetApi(17)
    private static void getDisplaySizeV17(Display display, Point point) {
        display.getRealSize(point);
    }

    @TargetApi(16)
    private static void getDisplaySizeV16(Display display, Point point) {
        display.getSize(point);
    }

    private static void getDisplaySizeV9(Display display, Point point) {
        point.x = display.getWidth();
        point.y = display.getHeight();
    }
}
