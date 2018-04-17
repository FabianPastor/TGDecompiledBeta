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

    public static File getCacheFile(File cacheDir, int id, long position, long lastAccessTimestamp) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(id);
        stringBuilder.append(".");
        stringBuilder.append(position);
        stringBuilder.append(".");
        stringBuilder.append(lastAccessTimestamp);
        stringBuilder.append(SUFFIX);
        return new File(cacheDir, stringBuilder.toString());
    }

    public static SimpleCacheSpan createLookup(String key, long position) {
        return new SimpleCacheSpan(key, position, -1, C0542C.TIME_UNSET, null);
    }

    public static SimpleCacheSpan createOpenHole(String key, long position) {
        return new SimpleCacheSpan(key, position, -1, C0542C.TIME_UNSET, null);
    }

    public static SimpleCacheSpan createClosedHole(String key, long position, long length) {
        return new SimpleCacheSpan(key, position, length, C0542C.TIME_UNSET, null);
    }

    public static org.telegram.messenger.exoplayer2.upstream.cache.SimpleCacheSpan createCacheEntry(java.io.File r17, org.telegram.messenger.exoplayer2.upstream.cache.CachedContentIndex r18) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r2_1 org.telegram.messenger.exoplayer2.upstream.cache.SimpleCacheSpan) in PHI: PHI: (r2_2 org.telegram.messenger.exoplayer2.upstream.cache.SimpleCacheSpan) = (r2_0 org.telegram.messenger.exoplayer2.upstream.cache.SimpleCacheSpan), (r2_1 org.telegram.messenger.exoplayer2.upstream.cache.SimpleCacheSpan) binds: {(r2_0 org.telegram.messenger.exoplayer2.upstream.cache.SimpleCacheSpan)=B:12:0x003d, (r2_1 org.telegram.messenger.exoplayer2.upstream.cache.SimpleCacheSpan)=B:13:0x003e}
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
        r0 = r17.getName();
        r1 = ".v3.exo";
        r1 = r0.endsWith(r1);
        r2 = 0;
        if (r1 != 0) goto L_0x0019;
    L_0x000d:
        r1 = upgradeFile(r17, r18);
        if (r1 != 0) goto L_0x0014;
    L_0x0013:
        return r2;
    L_0x0014:
        r0 = r1.getName();
        goto L_0x001b;
    L_0x0019:
        r1 = r17;
    L_0x001b:
        r3 = CACHE_FILE_PATTERN_V3;
        r12 = r3.matcher(r0);
        r3 = r12.matches();
        if (r3 != 0) goto L_0x0028;
    L_0x0027:
        return r2;
    L_0x0028:
        r13 = r1.length();
        r3 = 1;
        r3 = r12.group(r3);
        r15 = java.lang.Integer.parseInt(r3);
        r11 = r18;
        r16 = r11.getKeyForId(r15);
        if (r16 != 0) goto L_0x003e;
    L_0x003d:
        goto L_0x005a;
    L_0x003e:
        r2 = new org.telegram.messenger.exoplayer2.upstream.cache.SimpleCacheSpan;
        r3 = 2;
        r3 = r12.group(r3);
        r5 = java.lang.Long.parseLong(r3);
        r3 = 3;
        r3 = r12.group(r3);
        r9 = java.lang.Long.parseLong(r3);
        r3 = r2;
        r4 = r16;
        r7 = r13;
        r11 = r1;
        r3.<init>(r4, r5, r7, r9, r11);
    L_0x005a:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.upstream.cache.SimpleCacheSpan.createCacheEntry(java.io.File, org.telegram.messenger.exoplayer2.upstream.cache.CachedContentIndex):org.telegram.messenger.exoplayer2.upstream.cache.SimpleCacheSpan");
    }

    private static File upgradeFile(File file, CachedContentIndex index) {
        String key;
        String filename = file.getName();
        Matcher matcher = CACHE_FILE_PATTERN_V2.matcher(filename);
        if (matcher.matches()) {
            key = Util.unescapeFileName(matcher.group(1));
            if (key == null) {
                return null;
            }
        }
        matcher = CACHE_FILE_PATTERN_V1.matcher(filename);
        if (!matcher.matches()) {
            return null;
        }
        key = matcher.group(1);
        File newCacheFile = getCacheFile(file.getParentFile(), index.assignIdForKey(key), Long.parseLong(matcher.group(2)), Long.parseLong(matcher.group(3)));
        if (file.renameTo(newCacheFile)) {
            return newCacheFile;
        }
        return null;
    }

    private SimpleCacheSpan(String key, long position, long length, long lastAccessTimestamp, File file) {
        super(key, position, length, lastAccessTimestamp, file);
    }

    public SimpleCacheSpan copyWithUpdatedLastAccessTime(int id) {
        Assertions.checkState(this.isCached);
        long now = System.currentTimeMillis();
        return new SimpleCacheSpan(this.key, this.position, this.length, now, getCacheFile(this.file.getParentFile(), id, this.position, now));
    }
}
