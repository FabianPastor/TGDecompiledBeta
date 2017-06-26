package com.google.android.gms.wearable.internal;

import android.os.ParcelFileDescriptor;
import java.util.concurrent.Callable;

final class zzfx implements Callable<Boolean> {
    private /* synthetic */ byte[] zzbKO;
    private /* synthetic */ ParcelFileDescriptor zzbTn;

    zzfx(zzfw com_google_android_gms_wearable_internal_zzfw, ParcelFileDescriptor parcelFileDescriptor, byte[] bArr) {
        this.zzbTn = parcelFileDescriptor;
        this.zzbKO = bArr;
    }

    private final java.lang.Boolean zzDW() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x0113 in list []
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:43)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:60)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r6 = this;
        r1 = 3;
        r0 = "WearableClient";
        r0 = android.util.Log.isLoggable(r0, r1);
        if (r0 == 0) goto L_0x0034;
    L_0x000a:
        r0 = "WearableClient";
        r1 = r6.zzbTn;
        r1 = java.lang.String.valueOf(r1);
        r2 = java.lang.String.valueOf(r1);
        r2 = r2.length();
        r2 = r2 + 36;
        r3 = new java.lang.StringBuilder;
        r3.<init>(r2);
        r2 = "processAssets: writing data to FD : ";
        r2 = r3.append(r2);
        r1 = r2.append(r1);
        r1 = r1.toString();
        android.util.Log.d(r0, r1);
    L_0x0034:
        r1 = new android.os.ParcelFileDescriptor$AutoCloseOutputStream;
        r0 = r6.zzbTn;
        r1.<init>(r0);
        r0 = r6.zzbKO;	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r1.write(r0);	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r1.flush();	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r0 = "WearableClient";	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r2 = 3;	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r0 = android.util.Log.isLoggable(r0, r2);	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        if (r0 == 0) goto L_0x0077;	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
    L_0x004d:
        r0 = "WearableClient";	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r2 = r6.zzbTn;	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r2 = java.lang.String.valueOf(r2);	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r3 = java.lang.String.valueOf(r2);	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r3 = r3.length();	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r3 = r3 + 27;	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r4 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r4.<init>(r3);	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r3 = "processAssets: wrote data: ";	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r3 = r4.append(r3);	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r2 = r3.append(r2);	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r2 = r2.toString();	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        android.util.Log.d(r0, r2);	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
    L_0x0077:
        r0 = 1;	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r0 = java.lang.Boolean.valueOf(r0);	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r2 = "WearableClient";	 Catch:{ IOException -> 0x0159 }
        r3 = 3;	 Catch:{ IOException -> 0x0159 }
        r2 = android.util.Log.isLoggable(r2, r3);	 Catch:{ IOException -> 0x0159 }
        if (r2 == 0) goto L_0x00b0;	 Catch:{ IOException -> 0x0159 }
    L_0x0086:
        r2 = "WearableClient";	 Catch:{ IOException -> 0x0159 }
        r3 = r6.zzbTn;	 Catch:{ IOException -> 0x0159 }
        r3 = java.lang.String.valueOf(r3);	 Catch:{ IOException -> 0x0159 }
        r4 = java.lang.String.valueOf(r3);	 Catch:{ IOException -> 0x0159 }
        r4 = r4.length();	 Catch:{ IOException -> 0x0159 }
        r4 = r4 + 24;	 Catch:{ IOException -> 0x0159 }
        r5 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0159 }
        r5.<init>(r4);	 Catch:{ IOException -> 0x0159 }
        r4 = "processAssets: closing: ";	 Catch:{ IOException -> 0x0159 }
        r4 = r5.append(r4);	 Catch:{ IOException -> 0x0159 }
        r3 = r4.append(r3);	 Catch:{ IOException -> 0x0159 }
        r3 = r3.toString();	 Catch:{ IOException -> 0x0159 }
        android.util.Log.d(r2, r3);	 Catch:{ IOException -> 0x0159 }
    L_0x00b0:
        r1.close();	 Catch:{ IOException -> 0x0159 }
    L_0x00b3:
        return r0;
    L_0x00b4:
        r0 = move-exception;
        r0 = "WearableClient";	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r2 = r6.zzbTn;	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r2 = java.lang.String.valueOf(r2);	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r3 = java.lang.String.valueOf(r2);	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r3 = r3.length();	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r3 = r3 + 36;	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r4 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r4.<init>(r3);	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r3 = "processAssets: writing data failed: ";	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r3 = r4.append(r3);	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r2 = r3.append(r2);	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r2 = r2.toString();	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        android.util.Log.w(r0, r2);	 Catch:{ IOException -> 0x00b4, all -> 0x011c }
        r0 = "WearableClient";
        r2 = 3;
        r0 = android.util.Log.isLoggable(r0, r2);
        if (r0 == 0) goto L_0x0113;
    L_0x00e9:
        r0 = "WearableClient";
        r2 = r6.zzbTn;
        r2 = java.lang.String.valueOf(r2);
        r3 = java.lang.String.valueOf(r2);
        r3 = r3.length();
        r3 = r3 + 24;
        r4 = new java.lang.StringBuilder;
        r4.<init>(r3);
        r3 = "processAssets: closing: ";
        r3 = r4.append(r3);
        r2 = r3.append(r2);
        r2 = r2.toString();
        android.util.Log.d(r0, r2);
    L_0x0113:
        r1.close();
    L_0x0116:
        r0 = 0;
        r0 = java.lang.Boolean.valueOf(r0);
        goto L_0x00b3;
    L_0x011c:
        r0 = move-exception;
        r2 = "WearableClient";	 Catch:{ IOException -> 0x0155 }
        r3 = 3;	 Catch:{ IOException -> 0x0155 }
        r2 = android.util.Log.isLoggable(r2, r3);	 Catch:{ IOException -> 0x0155 }
        if (r2 == 0) goto L_0x0151;	 Catch:{ IOException -> 0x0155 }
    L_0x0127:
        r2 = "WearableClient";	 Catch:{ IOException -> 0x0155 }
        r3 = r6.zzbTn;	 Catch:{ IOException -> 0x0155 }
        r3 = java.lang.String.valueOf(r3);	 Catch:{ IOException -> 0x0155 }
        r4 = java.lang.String.valueOf(r3);	 Catch:{ IOException -> 0x0155 }
        r4 = r4.length();	 Catch:{ IOException -> 0x0155 }
        r4 = r4 + 24;	 Catch:{ IOException -> 0x0155 }
        r5 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0155 }
        r5.<init>(r4);	 Catch:{ IOException -> 0x0155 }
        r4 = "processAssets: closing: ";	 Catch:{ IOException -> 0x0155 }
        r4 = r5.append(r4);	 Catch:{ IOException -> 0x0155 }
        r3 = r4.append(r3);	 Catch:{ IOException -> 0x0155 }
        r3 = r3.toString();	 Catch:{ IOException -> 0x0155 }
        android.util.Log.d(r2, r3);	 Catch:{ IOException -> 0x0155 }
    L_0x0151:
        r1.close();	 Catch:{ IOException -> 0x0155 }
    L_0x0154:
        throw r0;
    L_0x0155:
        r1 = move-exception;
        goto L_0x0154;
    L_0x0157:
        r0 = move-exception;
        goto L_0x0116;
    L_0x0159:
        r1 = move-exception;
        goto L_0x00b3;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.wearable.internal.zzfx.zzDW():java.lang.Boolean");
    }

    public final /* synthetic */ Object call() throws Exception {
        return zzDW();
    }
}
