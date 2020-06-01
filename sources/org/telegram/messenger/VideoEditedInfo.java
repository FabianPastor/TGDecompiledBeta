package org.telegram.messenger;

import android.graphics.Bitmap;
import android.view.View;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.MediaController;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputEncryptedFile;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.Point;

public class VideoEditedInfo {
    public int bitrate;
    public boolean canceled;
    public TLRPC$InputEncryptedFile encryptedFile;
    public float end;
    public long endTime;
    public long estimatedDuration;
    public long estimatedSize;
    public TLRPC$InputFile file;
    public MediaController.SavedFilterState filterState;
    public int framerate = 24;
    public boolean isPhoto;
    public byte[] iv;
    public byte[] key;
    public ArrayList<MediaEntity> mediaEntities;
    public boolean muted;
    public boolean needUpdateProgress = false;
    public long originalDuration;
    public int originalHeight;
    public String originalPath;
    public int originalWidth;
    public String paintPath;
    public int resultHeight;
    public int resultWidth;
    public int rotationValue;
    public boolean roundVideo;
    public float start;
    public long startTime;
    public boolean videoConvertFirstWrite;

    public static class MediaEntity {
        public Bitmap bitmap;
        public int color;
        public float currentFrame;
        public TLRPC$Document document;
        public int fontSize;
        public float framesPerDraw;
        public float height;
        public int[] metadata;
        public Object parentObject;
        public long ptr;
        public float rotation;
        public float scale;
        public byte subType;
        public String text;
        public float textViewHeight;
        public float textViewWidth;
        public float textViewX;
        public float textViewY;
        public byte type;
        public View view;
        public int viewHeight;
        public int viewWidth;
        public float width;
        public float x;
        public float y;

        public MediaEntity() {
        }

        private MediaEntity(SerializedData serializedData) {
            this.type = serializedData.readByte(false);
            this.subType = serializedData.readByte(false);
            this.x = serializedData.readFloat(false);
            this.y = serializedData.readFloat(false);
            this.rotation = serializedData.readFloat(false);
            this.width = serializedData.readFloat(false);
            this.height = serializedData.readFloat(false);
            this.text = serializedData.readString(false);
            this.color = serializedData.readInt32(false);
            this.fontSize = serializedData.readInt32(false);
            this.viewWidth = serializedData.readInt32(false);
            this.viewHeight = serializedData.readInt32(false);
        }

        /* access modifiers changed from: private */
        public void serializeTo(SerializedData serializedData) {
            serializedData.writeByte(this.type);
            serializedData.writeByte(this.subType);
            serializedData.writeFloat(this.x);
            serializedData.writeFloat(this.y);
            serializedData.writeFloat(this.rotation);
            serializedData.writeFloat(this.width);
            serializedData.writeFloat(this.height);
            serializedData.writeString(this.text);
            serializedData.writeInt32(this.color);
            serializedData.writeInt32(this.fontSize);
            serializedData.writeInt32(this.viewWidth);
            serializedData.writeInt32(this.viewHeight);
        }
    }

    public String getString() {
        String str;
        byte[] bArr;
        PhotoFilterView.CurvesValue curvesValue;
        ArrayList<MediaEntity> arrayList;
        if (this.filterState == null && this.paintPath == null && ((arrayList = this.mediaEntities) == null || arrayList.isEmpty())) {
            str = "";
        } else {
            int i = this.filterState != null ? 162 : 2;
            String str2 = this.paintPath;
            if (str2 != null) {
                bArr = str2.getBytes();
                i += bArr.length;
            } else {
                bArr = null;
            }
            SerializedData serializedData = new SerializedData(i);
            serializedData.writeInt32(1);
            if (this.filterState != null) {
                serializedData.writeByte(1);
                serializedData.writeFloat(this.filterState.enhanceValue);
                serializedData.writeFloat(this.filterState.exposureValue);
                serializedData.writeFloat(this.filterState.contrastValue);
                serializedData.writeFloat(this.filterState.warmthValue);
                serializedData.writeFloat(this.filterState.saturationValue);
                serializedData.writeFloat(this.filterState.fadeValue);
                serializedData.writeInt32(this.filterState.tintShadowsColor);
                serializedData.writeInt32(this.filterState.tintHighlightsColor);
                serializedData.writeFloat(this.filterState.highlightsValue);
                serializedData.writeFloat(this.filterState.shadowsValue);
                serializedData.writeFloat(this.filterState.vignetteValue);
                serializedData.writeFloat(this.filterState.grainValue);
                serializedData.writeInt32(this.filterState.blurType);
                serializedData.writeFloat(this.filterState.sharpenValue);
                serializedData.writeFloat(this.filterState.blurExcludeSize);
                Point point = this.filterState.blurExcludePoint;
                if (point != null) {
                    serializedData.writeFloat(point.x);
                    serializedData.writeFloat(this.filterState.blurExcludePoint.y);
                } else {
                    serializedData.writeFloat(0.0f);
                    serializedData.writeFloat(0.0f);
                }
                serializedData.writeFloat(this.filterState.blurExcludeBlurSize);
                serializedData.writeFloat(this.filterState.blurAngle);
                for (int i2 = 0; i2 < 4; i2++) {
                    if (i2 == 0) {
                        curvesValue = this.filterState.curvesToolValue.luminanceCurve;
                    } else if (i2 == 1) {
                        curvesValue = this.filterState.curvesToolValue.redCurve;
                    } else if (i2 == 2) {
                        curvesValue = this.filterState.curvesToolValue.greenCurve;
                    } else {
                        curvesValue = this.filterState.curvesToolValue.blueCurve;
                    }
                    serializedData.writeFloat(curvesValue.blacksLevel);
                    serializedData.writeFloat(curvesValue.shadowsLevel);
                    serializedData.writeFloat(curvesValue.midtonesLevel);
                    serializedData.writeFloat(curvesValue.highlightsLevel);
                    serializedData.writeFloat(curvesValue.whitesLevel);
                }
            } else {
                serializedData.writeByte(0);
            }
            if (bArr != null) {
                serializedData.writeByte(1);
                serializedData.writeByteArray(bArr);
            } else {
                serializedData.writeByte(0);
            }
            ArrayList<MediaEntity> arrayList2 = this.mediaEntities;
            if (arrayList2 == null || arrayList2.isEmpty()) {
                serializedData.writeByte(0);
            } else {
                serializedData.writeByte(1);
                serializedData.writeInt32(this.mediaEntities.size());
                int size = this.mediaEntities.size();
                for (int i3 = 0; i3 < size; i3++) {
                    this.mediaEntities.get(i3).serializeTo(serializedData);
                }
                serializedData.writeByte(this.isPhoto ? 1 : 0);
            }
            str = Utilities.bytesToHex(serializedData.toByteArray());
            serializedData.cleanup();
        }
        return String.format(Locale.US, "-1_%d_%d_%d_%d_%d_%d_%d_%d_%d_%d_-%s_%s", new Object[]{Long.valueOf(this.startTime), Long.valueOf(this.endTime), Integer.valueOf(this.rotationValue), Integer.valueOf(this.originalWidth), Integer.valueOf(this.originalHeight), Integer.valueOf(this.bitrate), Integer.valueOf(this.resultWidth), Integer.valueOf(this.resultHeight), Long.valueOf(this.originalDuration), Integer.valueOf(this.framerate), str, this.originalPath});
    }

    public boolean parseString(String str) {
        PhotoFilterView.CurvesValue curvesValue;
        if (str.length() < 6) {
            return false;
        }
        try {
            String[] split = str.split("_");
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
                this.muted = this.bitrate == -1;
                if (split[11].startsWith("-")) {
                    String substring = split[11].substring(1);
                    if (substring.length() > 0) {
                        SerializedData serializedData = new SerializedData(Utilities.hexToBytes(substring));
                        serializedData.readInt32(false);
                        if (serializedData.readByte(false) != 0) {
                            MediaController.SavedFilterState savedFilterState = new MediaController.SavedFilterState();
                            this.filterState = savedFilterState;
                            savedFilterState.enhanceValue = serializedData.readFloat(false);
                            this.filterState.exposureValue = serializedData.readFloat(false);
                            this.filterState.contrastValue = serializedData.readFloat(false);
                            this.filterState.warmthValue = serializedData.readFloat(false);
                            this.filterState.saturationValue = serializedData.readFloat(false);
                            this.filterState.fadeValue = serializedData.readFloat(false);
                            this.filterState.tintShadowsColor = serializedData.readInt32(false);
                            this.filterState.tintHighlightsColor = serializedData.readInt32(false);
                            this.filterState.highlightsValue = serializedData.readFloat(false);
                            this.filterState.shadowsValue = serializedData.readFloat(false);
                            this.filterState.vignetteValue = serializedData.readFloat(false);
                            this.filterState.grainValue = serializedData.readFloat(false);
                            this.filterState.blurType = serializedData.readInt32(false);
                            this.filterState.sharpenValue = serializedData.readFloat(false);
                            this.filterState.blurExcludeSize = serializedData.readFloat(false);
                            this.filterState.blurExcludePoint = new Point(serializedData.readFloat(false), serializedData.readFloat(false));
                            this.filterState.blurExcludeBlurSize = serializedData.readFloat(false);
                            this.filterState.blurAngle = serializedData.readFloat(false);
                            for (int i2 = 0; i2 < 4; i2++) {
                                if (i2 == 0) {
                                    curvesValue = this.filterState.curvesToolValue.luminanceCurve;
                                } else if (i2 == 1) {
                                    curvesValue = this.filterState.curvesToolValue.redCurve;
                                } else if (i2 == 2) {
                                    curvesValue = this.filterState.curvesToolValue.greenCurve;
                                } else {
                                    curvesValue = this.filterState.curvesToolValue.blueCurve;
                                }
                                curvesValue.blacksLevel = serializedData.readFloat(false);
                                curvesValue.shadowsLevel = serializedData.readFloat(false);
                                curvesValue.midtonesLevel = serializedData.readFloat(false);
                                curvesValue.highlightsLevel = serializedData.readFloat(false);
                                curvesValue.whitesLevel = serializedData.readFloat(false);
                            }
                        }
                        if (serializedData.readByte(false) != 0) {
                            this.paintPath = new String(serializedData.readByteArray(false));
                        }
                        if (serializedData.readByte(false) != 0) {
                            int readInt32 = serializedData.readInt32(false);
                            this.mediaEntities = new ArrayList<>(readInt32);
                            for (int i3 = 0; i3 < readInt32; i3++) {
                                this.mediaEntities.add(new MediaEntity(serializedData));
                            }
                            this.isPhoto = serializedData.readByte(false) == 1;
                        }
                        serializedData.cleanup();
                    }
                    i = 12;
                }
                while (i < split.length) {
                    if (this.originalPath == null) {
                        this.originalPath = split[i];
                    } else {
                        this.originalPath += "_" + split[i];
                    }
                    i++;
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

    public boolean canAutoPlaySourceVideo() {
        return this.roundVideo;
    }
}
