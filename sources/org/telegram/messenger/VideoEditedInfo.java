package org.telegram.messenger;

import java.util.Locale;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;

public class VideoEditedInfo {
    public int bitrate;
    public InputEncryptedFile encryptedFile;
    public long endTime;
    public long estimatedDuration;
    public long estimatedSize;
    public InputFile file;
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
    public long startTime;

    public String getString() {
        return String.format(Locale.US, "-1_%d_%d_%d_%d_%d_%d_%d_%d_%s", new Object[]{Long.valueOf(this.startTime), Long.valueOf(this.endTime), Integer.valueOf(this.rotationValue), Integer.valueOf(this.originalWidth), Integer.valueOf(this.originalHeight), Integer.valueOf(this.bitrate), Integer.valueOf(this.resultWidth), Integer.valueOf(this.resultHeight), this.originalPath});
    }

    public boolean parseString(String str) {
        if (str.length() < 6) {
            return false;
        }
        try {
            str = str.split("_");
            if (str.length >= 10) {
                this.startTime = Long.parseLong(str[1]);
                this.endTime = Long.parseLong(str[2]);
                this.rotationValue = Integer.parseInt(str[3]);
                this.originalWidth = Integer.parseInt(str[4]);
                this.originalHeight = Integer.parseInt(str[5]);
                this.bitrate = Integer.parseInt(str[6]);
                this.resultWidth = Integer.parseInt(str[7]);
                this.resultHeight = Integer.parseInt(str[8]);
                for (int i = 9; i < str.length; i++) {
                    if (this.originalPath == null) {
                        this.originalPath = str[i];
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(this.originalPath);
                        stringBuilder.append("_");
                        stringBuilder.append(str[i]);
                        this.originalPath = stringBuilder.toString();
                    }
                }
            }
            return true;
        } catch (Throwable e) {
            FileLog.m3e(e);
            return false;
        }
    }

    public boolean needConvert() {
        if (this.roundVideo) {
            if (this.roundVideo) {
                if (this.startTime <= 0) {
                    if (!(this.endTime == -1 || this.endTime == this.estimatedDuration)) {
                    }
                }
            }
            return false;
        }
        return true;
    }
}
