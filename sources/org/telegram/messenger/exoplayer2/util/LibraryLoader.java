package org.telegram.messenger.exoplayer2.util;

public final class LibraryLoader {
    private boolean isAvailable;
    private boolean loadAttempted;
    private String[] nativeLibraries;

    public LibraryLoader(String... strArr) {
        this.nativeLibraries = strArr;
    }

    public synchronized void setLibraries(String... strArr) {
        Assertions.checkState(this.loadAttempted ^ 1, "Cannot set libraries after loading");
        this.nativeLibraries = strArr;
    }

    public synchronized boolean isAvailable() {
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
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r5 = this;
        monitor-enter(r5);
        r0 = r5.loadAttempted;	 Catch:{ all -> 0x0020 }
        if (r0 == 0) goto L_0x0009;	 Catch:{ all -> 0x0020 }
    L_0x0005:
        r0 = r5.isAvailable;	 Catch:{ all -> 0x0020 }
        monitor-exit(r5);
        return r0;
    L_0x0009:
        r0 = 1;
        r5.loadAttempted = r0;	 Catch:{ all -> 0x0020 }
        r1 = r5.nativeLibraries;	 Catch:{ UnsatisfiedLinkError -> 0x001c }
        r2 = r1.length;	 Catch:{ UnsatisfiedLinkError -> 0x001c }
        r3 = 0;	 Catch:{ UnsatisfiedLinkError -> 0x001c }
    L_0x0010:
        if (r3 >= r2) goto L_0x001a;	 Catch:{ UnsatisfiedLinkError -> 0x001c }
    L_0x0012:
        r4 = r1[r3];	 Catch:{ UnsatisfiedLinkError -> 0x001c }
        java.lang.System.loadLibrary(r4);	 Catch:{ UnsatisfiedLinkError -> 0x001c }
        r3 = r3 + 1;	 Catch:{ UnsatisfiedLinkError -> 0x001c }
        goto L_0x0010;	 Catch:{ UnsatisfiedLinkError -> 0x001c }
    L_0x001a:
        r5.isAvailable = r0;	 Catch:{ UnsatisfiedLinkError -> 0x001c }
    L_0x001c:
        r0 = r5.isAvailable;	 Catch:{ all -> 0x0020 }
        monitor-exit(r5);
        return r0;
    L_0x0020:
        r0 = move-exception;
        monitor-exit(r5);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.util.LibraryLoader.isAvailable():boolean");
    }
}
