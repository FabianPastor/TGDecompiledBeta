package org.telegram.messenger.camera;

public final class Size {
    public final int mHeight;
    public final int mWidth;

    public Size(int i, int i2) {
        this.mWidth = i;
        this.mHeight = i2;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Size)) {
            return false;
        }
        Size size = (Size) obj;
        if (this.mWidth == size.mWidth && this.mHeight == size.mHeight) {
            z = true;
        }
        return z;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.mWidth);
        stringBuilder.append("x");
        stringBuilder.append(this.mHeight);
        return stringBuilder.toString();
    }

    private static NumberFormatException invalidSize(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid Size: \"");
        stringBuilder.append(str);
        stringBuilder.append("\"");
        throw new NumberFormatException(stringBuilder.toString());
    }

    public static org.telegram.messenger.camera.Size parseSize(java.lang.String r3) throws java.lang.NumberFormatException {
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
        r0 = 42;
        r0 = r3.indexOf(r0);
        if (r0 >= 0) goto L_0x000e;
    L_0x0008:
        r0 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        r0 = r3.indexOf(r0);
    L_0x000e:
        if (r0 >= 0) goto L_0x0015;
    L_0x0010:
        r3 = invalidSize(r3);
        throw r3;
    L_0x0015:
        r1 = new org.telegram.messenger.camera.Size;	 Catch:{ NumberFormatException -> 0x002e }
        r2 = 0;	 Catch:{ NumberFormatException -> 0x002e }
        r2 = r3.substring(r2, r0);	 Catch:{ NumberFormatException -> 0x002e }
        r2 = java.lang.Integer.parseInt(r2);	 Catch:{ NumberFormatException -> 0x002e }
        r0 = r0 + 1;	 Catch:{ NumberFormatException -> 0x002e }
        r0 = r3.substring(r0);	 Catch:{ NumberFormatException -> 0x002e }
        r0 = java.lang.Integer.parseInt(r0);	 Catch:{ NumberFormatException -> 0x002e }
        r1.<init>(r2, r0);	 Catch:{ NumberFormatException -> 0x002e }
        return r1;
    L_0x002e:
        r3 = invalidSize(r3);
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.camera.Size.parseSize(java.lang.String):org.telegram.messenger.camera.Size");
    }

    public int hashCode() {
        return this.mHeight ^ ((this.mWidth << 16) | (this.mWidth >>> 16));
    }
}
