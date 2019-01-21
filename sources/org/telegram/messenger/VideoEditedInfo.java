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
    public long startTime;

    public String getString() {
        return String.format(Locale.US, "-1_%d_%d_%d_%d_%d_%d_%d_%d_%d_%s", new Object[]{Long.valueOf(this.startTime), Long.valueOf(this.endTime), Integer.valueOf(this.rotationValue), Integer.valueOf(this.originalWidth), Integer.valueOf(this.originalHeight), Integer.valueOf(this.bitrate), Integer.valueOf(this.resultWidth), Integer.valueOf(this.resultHeight), Integer.valueOf(this.framerate), this.originalPath});
    }

    public boolean parseString(String string) {
        if (string.length() < 6) {
            return false;
        }
        try {
            String[] args = string.split("_");
            if (args.length >= 10) {
                int pathStart;
                this.startTime = Long.parseLong(args[1]);
                this.endTime = Long.parseLong(args[2]);
                this.rotationValue = Integer.parseInt(args[3]);
                this.originalWidth = Integer.parseInt(args[4]);
                this.originalHeight = Integer.parseInt(args[5]);
                this.bitrate = Integer.parseInt(args[6]);
                this.resultWidth = Integer.parseInt(args[7]);
                this.resultHeight = Integer.parseInt(args[8]);
                if (args.length >= 11) {
                    try {
                        this.framerate = Integer.parseInt(args[9]);
                    } catch (Exception e) {
                    }
                }
                if (this.framerate <= 0 || this.framerate > 400) {
                    pathStart = 9;
                    this.framerate = 25;
                } else {
                    pathStart = 10;
                }
                for (int a = pathStart; a < args.length; a++) {
                    if (this.originalPath == null) {
                        this.originalPath = args[a];
                    } else {
                        this.originalPath += "_" + args[a];
                    }
                }
            }
            return true;
        } catch (Throwable e2) {
            FileLog.e(e2);
            return false;
        }
    }

    public boolean needConvert() {
        return !this.roundVideo || (this.roundVideo && (this.startTime > 0 || !(this.endTime == -1 || this.endTime == this.estimatedDuration)));
    }
}
