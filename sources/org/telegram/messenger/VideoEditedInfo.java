package org.telegram.messenger;

import java.util.Locale;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;

public class VideoEditedInfo {
    public int bitrate;
    public InputEncryptedFile encryptedFile;
    public float end;
    public long endTime;
    public long estimatedDuration;
    public long estimatedSize;
    public InputFile file;
    public int framerate = 24;
    public byte[] iv;
    public byte[] key;
    public boolean muted;
    public int originalHeight;
    public String originalPath;
    public int originalWidth;
    public int resultHeight;
    public int resultWidth;
    public int rotationValue;
    public boolean roundVideo;
    public float start;
    public long startTime;

    public String getString() {
        return String.format(Locale.US, "-1_%d_%d_%d_%d_%d_%d_%d_%d_%d_%s", new Object[]{Long.valueOf(this.startTime), Long.valueOf(this.endTime), Integer.valueOf(this.rotationValue), Integer.valueOf(this.originalWidth), Integer.valueOf(this.originalHeight), Integer.valueOf(this.bitrate), Integer.valueOf(this.resultWidth), Integer.valueOf(this.resultHeight), Integer.valueOf(this.framerate), this.originalPath});
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x007e A:{Catch:{ Exception -> 0x00a3 }} */
    /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x006b */
    /* JADX WARNING: Can't wrap try/catch for region: R(8:6|7|(2:9|10)|11|12|(1:16)|(4:19|(2:21|30)(2:22|29)|23|17)|28) */
    public boolean parseString(java.lang.String r9) {
        /*
        r8 = this;
        r0 = "_";
        r1 = r9.length();
        r2 = 0;
        r3 = 6;
        if (r1 >= r3) goto L_0x000b;
    L_0x000a:
        return r2;
    L_0x000b:
        r9 = r9.split(r0);	 Catch:{ Exception -> 0x00a3 }
        r1 = r9.length;	 Catch:{ Exception -> 0x00a3 }
        r4 = 10;
        r5 = 1;
        if (r1 < r4) goto L_0x00a2;
    L_0x0015:
        r1 = r9[r5];	 Catch:{ Exception -> 0x00a3 }
        r6 = java.lang.Long.parseLong(r1);	 Catch:{ Exception -> 0x00a3 }
        r8.startTime = r6;	 Catch:{ Exception -> 0x00a3 }
        r1 = 2;
        r1 = r9[r1];	 Catch:{ Exception -> 0x00a3 }
        r6 = java.lang.Long.parseLong(r1);	 Catch:{ Exception -> 0x00a3 }
        r8.endTime = r6;	 Catch:{ Exception -> 0x00a3 }
        r1 = 3;
        r1 = r9[r1];	 Catch:{ Exception -> 0x00a3 }
        r1 = java.lang.Integer.parseInt(r1);	 Catch:{ Exception -> 0x00a3 }
        r8.rotationValue = r1;	 Catch:{ Exception -> 0x00a3 }
        r1 = 4;
        r1 = r9[r1];	 Catch:{ Exception -> 0x00a3 }
        r1 = java.lang.Integer.parseInt(r1);	 Catch:{ Exception -> 0x00a3 }
        r8.originalWidth = r1;	 Catch:{ Exception -> 0x00a3 }
        r1 = 5;
        r1 = r9[r1];	 Catch:{ Exception -> 0x00a3 }
        r1 = java.lang.Integer.parseInt(r1);	 Catch:{ Exception -> 0x00a3 }
        r8.originalHeight = r1;	 Catch:{ Exception -> 0x00a3 }
        r1 = r9[r3];	 Catch:{ Exception -> 0x00a3 }
        r1 = java.lang.Integer.parseInt(r1);	 Catch:{ Exception -> 0x00a3 }
        r8.bitrate = r1;	 Catch:{ Exception -> 0x00a3 }
        r1 = 7;
        r1 = r9[r1];	 Catch:{ Exception -> 0x00a3 }
        r1 = java.lang.Integer.parseInt(r1);	 Catch:{ Exception -> 0x00a3 }
        r8.resultWidth = r1;	 Catch:{ Exception -> 0x00a3 }
        r1 = 8;
        r1 = r9[r1];	 Catch:{ Exception -> 0x00a3 }
        r1 = java.lang.Integer.parseInt(r1);	 Catch:{ Exception -> 0x00a3 }
        r8.resultHeight = r1;	 Catch:{ Exception -> 0x00a3 }
        r1 = r9.length;	 Catch:{ Exception -> 0x00a3 }
        r3 = 11;
        r6 = 9;
        if (r1 < r3) goto L_0x006b;
    L_0x0063:
        r1 = r9[r6];	 Catch:{ Exception -> 0x006b }
        r1 = java.lang.Integer.parseInt(r1);	 Catch:{ Exception -> 0x006b }
        r8.framerate = r1;	 Catch:{ Exception -> 0x006b }
    L_0x006b:
        r1 = r8.framerate;	 Catch:{ Exception -> 0x00a3 }
        if (r1 <= 0) goto L_0x0075;
    L_0x006f:
        r1 = r8.framerate;	 Catch:{ Exception -> 0x00a3 }
        r3 = 400; // 0x190 float:5.6E-43 double:1.976E-321;
        if (r1 <= r3) goto L_0x007b;
    L_0x0075:
        r1 = 25;
        r8.framerate = r1;	 Catch:{ Exception -> 0x00a3 }
        r4 = 9;
    L_0x007b:
        r1 = r9.length;	 Catch:{ Exception -> 0x00a3 }
        if (r4 >= r1) goto L_0x00a2;
    L_0x007e:
        r1 = r8.originalPath;	 Catch:{ Exception -> 0x00a3 }
        if (r1 != 0) goto L_0x0087;
    L_0x0082:
        r1 = r9[r4];	 Catch:{ Exception -> 0x00a3 }
        r8.originalPath = r1;	 Catch:{ Exception -> 0x00a3 }
        goto L_0x009f;
    L_0x0087:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00a3 }
        r1.<init>();	 Catch:{ Exception -> 0x00a3 }
        r3 = r8.originalPath;	 Catch:{ Exception -> 0x00a3 }
        r1.append(r3);	 Catch:{ Exception -> 0x00a3 }
        r1.append(r0);	 Catch:{ Exception -> 0x00a3 }
        r3 = r9[r4];	 Catch:{ Exception -> 0x00a3 }
        r1.append(r3);	 Catch:{ Exception -> 0x00a3 }
        r1 = r1.toString();	 Catch:{ Exception -> 0x00a3 }
        r8.originalPath = r1;	 Catch:{ Exception -> 0x00a3 }
    L_0x009f:
        r4 = r4 + 1;
        goto L_0x007b;
    L_0x00a2:
        return r5;
    L_0x00a3:
        r9 = move-exception;
        org.telegram.messenger.FileLog.e(r9);
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.VideoEditedInfo.parseString(java.lang.String):boolean");
    }

    /* JADX WARNING: Missing block: B:8:0x001a, code skipped:
            if (r0 != r5.estimatedDuration) goto L_0x001f;
     */
    public boolean needConvert() {
        /*
        r5 = this;
        r0 = r5.roundVideo;
        if (r0 == 0) goto L_0x001f;
    L_0x0004:
        if (r0 == 0) goto L_0x001d;
    L_0x0006:
        r0 = r5.startTime;
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 > 0) goto L_0x001f;
    L_0x000e:
        r0 = r5.endTime;
        r2 = -1;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 == 0) goto L_0x001d;
    L_0x0016:
        r2 = r5.estimatedDuration;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 == 0) goto L_0x001d;
    L_0x001c:
        goto L_0x001f;
    L_0x001d:
        r0 = 0;
        goto L_0x0020;
    L_0x001f:
        r0 = 1;
    L_0x0020:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.VideoEditedInfo.needConvert():boolean");
    }
}
