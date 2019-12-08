package org.telegram.messenger;

import java.util.Locale;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;

public class VideoEditedInfo {
    public int bitrate;
    public boolean canceled;
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
    public boolean needUpdateProgress = false;
    public long originalDuration;
    public int originalHeight;
    public String originalPath;
    public int originalWidth;
    public int resultHeight;
    public int resultWidth;
    public int rotationValue;
    public boolean roundVideo;
    public float start;
    public long startTime;
    public boolean videoConvertFirstWrite;

    public String getString() {
        return String.format(Locale.US, "-1_%d_%d_%d_%d_%d_%d_%d_%d_%d_%d_%s", new Object[]{Long.valueOf(this.startTime), Long.valueOf(this.endTime), Integer.valueOf(this.rotationValue), Integer.valueOf(this.originalWidth), Integer.valueOf(this.originalHeight), Integer.valueOf(this.bitrate), Integer.valueOf(this.resultWidth), Integer.valueOf(this.resultHeight), Long.valueOf(this.originalDuration), Integer.valueOf(this.framerate), this.originalPath});
    }

    public boolean parseString(String str) {
        String str2 = "_";
        if (str.length() < 6) {
            return false;
        }
        try {
            String[] split = str.split(str2);
            int i = 11;
            if (split.length >= 11) {
                this.startTime = Long.parseLong(split[1]);
                this.endTime = Long.parseLong(split[2]);
                this.rotationValue = Integer.parseInt(split[3]);
                this.originalWidth = Integer.parseInt(split[4]);
                this.originalHeight = Integer.parseInt(split[5]);
                this.bitrate = Integer.parseInt(split[6]);
                this.resultWidth = Integer.parseInt(split[7]);
                this.resultHeight = Integer.parseInt(split[8]);
                this.originalDuration = Long.parseLong(split[9]);
                this.framerate = Integer.parseInt(split[10]);
                while (i < split.length) {
                    if (this.originalPath == null) {
                        this.originalPath = split[i];
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(this.originalPath);
                        stringBuilder.append(str2);
                        stringBuilder.append(split[i]);
                        this.originalPath = stringBuilder.toString();
                    }
                    i++;
                }
            }
            return true;
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
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
