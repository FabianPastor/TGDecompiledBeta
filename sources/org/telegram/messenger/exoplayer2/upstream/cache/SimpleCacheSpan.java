package org.telegram.messenger.exoplayer2.upstream.cache;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

final class SimpleCacheSpan extends CacheSpan {
    private static final Pattern CACHE_FILE_PATTERN_V1 = Pattern.compile("^(.+)\\.(\\d+)\\.(\\d+)\\.v1\\.exo$", 32);
    private static final Pattern CACHE_FILE_PATTERN_V2 = Pattern.compile("^(.+)\\.(\\d+)\\.(\\d+)\\.v2\\.exo$", 32);
    private static final Pattern CACHE_FILE_PATTERN_V3 = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)\\.v3\\.exo$", 32);
    private static final String SUFFIX = ".v3.exo";

    public static File getCacheFile(File file, int i, long j, long j2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(i);
        stringBuilder.append(".");
        stringBuilder.append(j);
        stringBuilder.append(".");
        stringBuilder.append(j2);
        stringBuilder.append(SUFFIX);
        return new File(file, stringBuilder.toString());
    }

    public static SimpleCacheSpan createLookup(String str, long j) {
        return new SimpleCacheSpan(str, j, -1, C0542C.TIME_UNSET, null);
    }

    public static SimpleCacheSpan createOpenHole(String str, long j) {
        return new SimpleCacheSpan(str, j, -1, C0542C.TIME_UNSET, null);
    }

    public static SimpleCacheSpan createClosedHole(String str, long j, long j2) {
        return new SimpleCacheSpan(str, j, j2, C0542C.TIME_UNSET, null);
    }

    public static org.telegram.messenger.exoplayer2.upstream.cache.SimpleCacheSpan createCacheEntry(java.io.File r12, org.telegram.messenger.exoplayer2.upstream.cache.CachedContentIndex r13) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r13_2 org.telegram.messenger.exoplayer2.upstream.cache.CachedContentIndex) in PHI: PHI: (r13_3 org.telegram.messenger.exoplayer2.upstream.cache.CachedContentIndex) = (r13_1 org.telegram.messenger.exoplayer2.upstream.cache.CachedContentIndex), (r13_2 org.telegram.messenger.exoplayer2.upstream.cache.CachedContentIndex) binds: {(r13_1 org.telegram.messenger.exoplayer2.upstream.cache.CachedContentIndex)=B:11:0x0039, (r13_2 org.telegram.messenger.exoplayer2.upstream.cache.CachedContentIndex)=B:12:0x003b}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
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
        r0 = r12.getName();
        r1 = ".v3.exo";
        r1 = r0.endsWith(r1);
        r2 = 0;
        if (r1 != 0) goto L_0x0018;
    L_0x000d:
        r12 = upgradeFile(r12, r13);
        if (r12 != 0) goto L_0x0014;
    L_0x0013:
        return r2;
    L_0x0014:
        r0 = r12.getName();
    L_0x0018:
        r11 = r12;
        r12 = CACHE_FILE_PATTERN_V3;
        r12 = r12.matcher(r0);
        r0 = r12.matches();
        if (r0 != 0) goto L_0x0026;
    L_0x0025:
        return r2;
    L_0x0026:
        r7 = r11.length();
        r0 = 1;
        r0 = r12.group(r0);
        r0 = java.lang.Integer.parseInt(r0);
        r4 = r13.getKeyForId(r0);
        if (r4 != 0) goto L_0x003b;
    L_0x0039:
        r13 = r2;
        goto L_0x0053;
    L_0x003b:
        r13 = new org.telegram.messenger.exoplayer2.upstream.cache.SimpleCacheSpan;
        r0 = 2;
        r0 = r12.group(r0);
        r5 = java.lang.Long.parseLong(r0);
        r0 = 3;
        r12 = r12.group(r0);
        r9 = java.lang.Long.parseLong(r12);
        r3 = r13;
        r3.<init>(r4, r5, r7, r9, r11);
    L_0x0053:
        return r13;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.upstream.cache.SimpleCacheSpan.createCacheEntry(java.io.File, org.telegram.messenger.exoplayer2.upstream.cache.CachedContentIndex):org.telegram.messenger.exoplayer2.upstream.cache.SimpleCacheSpan");
    }

    private static File upgradeFile(File file, CachedContentIndex cachedContentIndex) {
        String unescapeFileName;
        CharSequence name = file.getName();
        Matcher matcher = CACHE_FILE_PATTERN_V2.matcher(name);
        if (matcher.matches()) {
            unescapeFileName = Util.unescapeFileName(matcher.group(1));
            if (unescapeFileName == null) {
                return null;
            }
        }
        matcher = CACHE_FILE_PATTERN_V1.matcher(name);
        if (!matcher.matches()) {
            return null;
        }
        unescapeFileName = matcher.group(1);
        cachedContentIndex = getCacheFile(file.getParentFile(), cachedContentIndex.assignIdForKey(unescapeFileName), Long.parseLong(matcher.group(2)), Long.parseLong(matcher.group(3)));
        if (file.renameTo(cachedContentIndex) == null) {
            return null;
        }
        return cachedContentIndex;
    }

    private SimpleCacheSpan(String str, long j, long j2, long j3, File file) {
        super(str, j, j2, j3, file);
    }

    public SimpleCacheSpan copyWithUpdatedLastAccessTime(int i) {
        Assertions.checkState(this.isCached);
        long currentTimeMillis = System.currentTimeMillis();
        return new SimpleCacheSpan(this.key, this.position, this.length, currentTimeMillis, getCacheFile(this.file.getParentFile(), i, this.position, currentTimeMillis));
    }
}
