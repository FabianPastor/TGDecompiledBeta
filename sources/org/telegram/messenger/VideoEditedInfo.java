package org.telegram.messenger;

import android.graphics.Bitmap;
import android.view.View;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.MediaController;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.Point;

public class VideoEditedInfo {
    public long avatarStartTime = -1;
    public int bitrate;
    public boolean canceled;
    public MediaController.CropState cropState;
    public TLRPC.InputEncryptedFile encryptedFile;
    public float end;
    public long endTime;
    public long estimatedDuration;
    public long estimatedSize;
    public TLRPC.InputFile file;
    public MediaController.SavedFilterState filterState;
    public int framerate = 24;
    public boolean isPhoto;
    public byte[] iv;
    public byte[] key;
    public ArrayList<MediaEntity> mediaEntities;
    public boolean muted;
    public boolean needUpdateProgress = false;
    public int originalBitrate;
    public long originalDuration;
    public int originalHeight;
    public String originalPath;
    public int originalWidth;
    public String paintPath;
    public int resultHeight;
    public int resultWidth;
    public int rotationValue;
    public boolean roundVideo;
    public boolean shouldLimitFps = true;
    public float start;
    public long startTime;
    public boolean videoConvertFirstWrite;

    public static class MediaEntity {
        public AnimatedFileDrawable animatedFileDrawable;
        public Bitmap bitmap;
        public int color;
        public float currentFrame;
        public TLRPC.Document document;
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

        private MediaEntity(SerializedData data) {
            this.type = data.readByte(false);
            this.subType = data.readByte(false);
            this.x = data.readFloat(false);
            this.y = data.readFloat(false);
            this.rotation = data.readFloat(false);
            this.width = data.readFloat(false);
            this.height = data.readFloat(false);
            this.text = data.readString(false);
            this.color = data.readInt32(false);
            this.fontSize = data.readInt32(false);
            this.viewWidth = data.readInt32(false);
            this.viewHeight = data.readInt32(false);
        }

        /* access modifiers changed from: private */
        public void serializeTo(SerializedData data) {
            data.writeByte(this.type);
            data.writeByte(this.subType);
            data.writeFloat(this.x);
            data.writeFloat(this.y);
            data.writeFloat(this.rotation);
            data.writeFloat(this.width);
            data.writeFloat(this.height);
            data.writeString(this.text);
            data.writeInt32(this.color);
            data.writeInt32(this.fontSize);
            data.writeInt32(this.viewWidth);
            data.writeInt32(this.viewHeight);
        }

        public MediaEntity copy() {
            MediaEntity entity = new MediaEntity();
            entity.type = this.type;
            entity.subType = this.subType;
            entity.x = this.x;
            entity.y = this.y;
            entity.rotation = this.rotation;
            entity.width = this.width;
            entity.height = this.height;
            entity.text = this.text;
            entity.color = this.color;
            entity.fontSize = this.fontSize;
            entity.viewWidth = this.viewWidth;
            entity.viewHeight = this.viewHeight;
            entity.scale = this.scale;
            entity.textViewWidth = this.textViewWidth;
            entity.textViewHeight = this.textViewHeight;
            entity.textViewX = this.textViewX;
            entity.textViewY = this.textViewY;
            return entity;
        }
    }

    public String getString() {
        String filters;
        byte[] paintPathBytes;
        PhotoFilterView.CurvesValue curvesValue;
        ArrayList<MediaEntity> arrayList;
        if (this.avatarStartTime == -1 && this.filterState == null && this.paintPath == null && (((arrayList = this.mediaEntities) == null || arrayList.isEmpty()) && this.cropState == null)) {
            filters = "";
        } else {
            int len = 10;
            if (this.filterState != null) {
                len = 10 + 160;
            }
            String str = this.paintPath;
            if (str != null) {
                paintPathBytes = str.getBytes();
                len += paintPathBytes.length;
            } else {
                paintPathBytes = null;
            }
            SerializedData serializedData = new SerializedData(len);
            serializedData.writeInt32(5);
            serializedData.writeInt64(this.avatarStartTime);
            serializedData.writeInt32(this.originalBitrate);
            if (this.filterState != null) {
                serializedData.writeByte(1);
                serializedData.writeFloat(this.filterState.enhanceValue);
                serializedData.writeFloat(this.filterState.softenSkinValue);
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
                if (this.filterState.blurExcludePoint != null) {
                    serializedData.writeFloat(this.filterState.blurExcludePoint.x);
                    serializedData.writeFloat(this.filterState.blurExcludePoint.y);
                } else {
                    serializedData.writeFloat(0.0f);
                    serializedData.writeFloat(0.0f);
                }
                serializedData.writeFloat(this.filterState.blurExcludeBlurSize);
                serializedData.writeFloat(this.filterState.blurAngle);
                for (int a = 0; a < 4; a++) {
                    if (a == 0) {
                        curvesValue = this.filterState.curvesToolValue.luminanceCurve;
                    } else if (a == 1) {
                        curvesValue = this.filterState.curvesToolValue.redCurve;
                    } else if (a == 2) {
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
            if (paintPathBytes != null) {
                serializedData.writeByte(1);
                serializedData.writeByteArray(paintPathBytes);
            } else {
                serializedData.writeByte(0);
            }
            ArrayList<MediaEntity> arrayList2 = this.mediaEntities;
            if (arrayList2 == null || arrayList2.isEmpty()) {
                serializedData.writeByte(0);
            } else {
                serializedData.writeByte(1);
                serializedData.writeInt32(this.mediaEntities.size());
                int N = this.mediaEntities.size();
                for (int a2 = 0; a2 < N; a2++) {
                    this.mediaEntities.get(a2).serializeTo(serializedData);
                }
                serializedData.writeByte((int) this.isPhoto);
            }
            if (this.cropState != null) {
                serializedData.writeByte(1);
                serializedData.writeFloat(this.cropState.cropPx);
                serializedData.writeFloat(this.cropState.cropPy);
                serializedData.writeFloat(this.cropState.cropPw);
                serializedData.writeFloat(this.cropState.cropPh);
                serializedData.writeFloat(this.cropState.cropScale);
                serializedData.writeFloat(this.cropState.cropRotate);
                serializedData.writeInt32(this.cropState.transformWidth);
                serializedData.writeInt32(this.cropState.transformHeight);
                serializedData.writeInt32(this.cropState.transformRotation);
                serializedData.writeBool(this.cropState.mirrored);
            } else {
                serializedData.writeByte(0);
            }
            String filters2 = Utilities.bytesToHex(serializedData.toByteArray());
            serializedData.cleanup();
            filters = filters2;
        }
        return String.format(Locale.US, "-1_%d_%d_%d_%d_%d_%d_%d_%d_%d_%d_-%s_%s", new Object[]{Long.valueOf(this.startTime), Long.valueOf(this.endTime), Integer.valueOf(this.rotationValue), Integer.valueOf(this.originalWidth), Integer.valueOf(this.originalHeight), Integer.valueOf(this.bitrate), Integer.valueOf(this.resultWidth), Integer.valueOf(this.resultHeight), Long.valueOf(this.originalDuration), Integer.valueOf(this.framerate), filters, this.originalPath});
    }

    public boolean parseString(String string) {
        int start2;
        PhotoFilterView.CurvesValue curvesValue;
        if (string.length() < 6) {
            return false;
        }
        try {
            String[] args = string.split("_");
            if (args.length >= 11) {
                this.startTime = Long.parseLong(args[1]);
                this.endTime = Long.parseLong(args[2]);
                this.rotationValue = Integer.parseInt(args[3]);
                this.originalWidth = Integer.parseInt(args[4]);
                this.originalHeight = Integer.parseInt(args[5]);
                this.bitrate = Integer.parseInt(args[6]);
                this.resultWidth = Integer.parseInt(args[7]);
                this.resultHeight = Integer.parseInt(args[8]);
                this.originalDuration = Long.parseLong(args[9]);
                this.framerate = Integer.parseInt(args[10]);
                this.muted = this.bitrate == -1;
                if (args[11].startsWith("-")) {
                    start2 = 12;
                    String s = args[11].substring(1);
                    if (s.length() > 0) {
                        SerializedData serializedData = new SerializedData(Utilities.hexToBytes(s));
                        int version = serializedData.readInt32(false);
                        if (version >= 3) {
                            this.avatarStartTime = serializedData.readInt64(false);
                            this.originalBitrate = serializedData.readInt32(false);
                        }
                        if (serializedData.readByte(false) != 0) {
                            MediaController.SavedFilterState savedFilterState = new MediaController.SavedFilterState();
                            this.filterState = savedFilterState;
                            savedFilterState.enhanceValue = serializedData.readFloat(false);
                            if (version >= 5) {
                                this.filterState.softenSkinValue = serializedData.readFloat(false);
                            }
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
                            for (int a = 0; a < 4; a++) {
                                if (a == 0) {
                                    curvesValue = this.filterState.curvesToolValue.luminanceCurve;
                                } else if (a == 1) {
                                    curvesValue = this.filterState.curvesToolValue.redCurve;
                                } else if (a == 2) {
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
                            int count = serializedData.readInt32(false);
                            this.mediaEntities = new ArrayList<>(count);
                            for (int a2 = 0; a2 < count; a2++) {
                                this.mediaEntities.add(new MediaEntity(serializedData));
                            }
                            this.isPhoto = serializedData.readByte(false) == 1;
                        }
                        if (version >= 2 && serializedData.readByte(false) != 0) {
                            MediaController.CropState cropState2 = new MediaController.CropState();
                            this.cropState = cropState2;
                            cropState2.cropPx = serializedData.readFloat(false);
                            this.cropState.cropPy = serializedData.readFloat(false);
                            this.cropState.cropPw = serializedData.readFloat(false);
                            this.cropState.cropPh = serializedData.readFloat(false);
                            this.cropState.cropScale = serializedData.readFloat(false);
                            this.cropState.cropRotate = serializedData.readFloat(false);
                            this.cropState.transformWidth = serializedData.readInt32(false);
                            this.cropState.transformHeight = serializedData.readInt32(false);
                            this.cropState.transformRotation = serializedData.readInt32(false);
                            if (version >= 4) {
                                this.cropState.mirrored = serializedData.readBool(false);
                            }
                        }
                        serializedData.cleanup();
                    }
                } else {
                    start2 = 11;
                }
                for (int a3 = start2; a3 < args.length; a3++) {
                    if (this.originalPath == null) {
                        this.originalPath = args[a3];
                    } else {
                        this.originalPath += "_" + args[a3];
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
        if (this.mediaEntities == null && this.paintPath == null && this.filterState == null && this.cropState == null && this.roundVideo && this.startTime <= 0) {
            long j = this.endTime;
            if (j == -1 || j == this.estimatedDuration) {
                return false;
            }
        }
        return true;
    }

    public boolean canAutoPlaySourceVideo() {
        return this.roundVideo;
    }
}
