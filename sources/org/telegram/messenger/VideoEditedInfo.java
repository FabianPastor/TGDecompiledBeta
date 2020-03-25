package org.telegram.messenger;

import java.util.Locale;
import org.telegram.tgnet.TLRPC$InputEncryptedFile;
import org.telegram.tgnet.TLRPC$InputFile;

public class VideoEditedInfo {
    public int bitrate;
    public boolean canceled;
    public TLRPC$InputEncryptedFile encryptedFile;
    public float end;
    public long endTime;
    public long estimatedDuration;
    public long estimatedSize;
    public TLRPC$InputFile file;
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
        if (str.length() < 6) {
            return false;
        }
        try {
            String[] split = str.split("_");
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
                for (int i = 11; i < split.length; i++) {
                    if (this.originalPath == null) {
                        this.originalPath = split[i];
                    } else {
                        this.originalPath += "_" + split[i];
                    }
                }
            }
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    public boolean needConvert() {
        boolean z = this.roundVideo;
        if (z) {
            if (z) {
                if (this.startTime <= 0) {
                    long j = this.endTime;
                    if (j == -1 || j == this.estimatedDuration) {
                        return false;
                    }
                }
            }
            return false;
        }
        return true;
    }
}
