package org.telegram.messenger.video;

import android.media.MediaExtractor;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.VideoEditedInfo;

public class MediaCodecVideoConvertor {
    private static final int MEDIACODEC_TIMEOUT_DEFAULT = 2500;
    private static final int MEDIACODEC_TIMEOUT_INCREASED = 22000;
    private static final int PROCESSOR_TYPE_INTEL = 2;
    private static final int PROCESSOR_TYPE_MTK = 3;
    private static final int PROCESSOR_TYPE_OTHER = 0;
    private static final int PROCESSOR_TYPE_QCOM = 1;
    private static final int PROCESSOR_TYPE_SEC = 4;
    private static final int PROCESSOR_TYPE_TI = 5;
    private MediaController.VideoConvertorListener callback;
    private long endPresentationTime;
    private MediaExtractor extractor;
    private MP4Builder mediaMuxer;

    public boolean convertVideo(String videoPath, File cacheFile, int rotationValue, boolean isSecret, int originalWidth, int originalHeight, int resultWidth, int resultHeight, int framerate, int bitrate, int originalBitrate, long startTime, long endTime, long avatarStartTime, boolean needCompress, long duration, MediaController.SavedFilterState savedFilterState, String paintPath, ArrayList<VideoEditedInfo.MediaEntity> mediaEntities, boolean isPhoto, MediaController.CropState cropState, MediaController.VideoConvertorListener callback2) {
        long j = duration;
        this.callback = callback2;
        return convertVideoInternal(videoPath, cacheFile, rotationValue, isSecret, originalWidth, originalHeight, resultWidth, resultHeight, framerate, bitrate, originalBitrate, startTime, endTime, avatarStartTime, j, needCompress, false, savedFilterState, paintPath, mediaEntities, isPhoto, cropState);
    }

    public long getLastFrameTimestamp() {
        return this.endPresentationTime;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r101v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r95v1, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v22, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r95v3, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v16, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r95v5, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v21, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r95v6, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r95v7, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r95v8, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r95v9, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v23, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v24, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r95v10, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v54, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v35, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v55, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v64, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v65, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v40, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v40, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v72, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r73v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v45, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r95v11, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r95v12, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r95v13, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v50, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v51, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r95v14, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r95v15, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v65, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r95v16, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v64, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v53, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v66, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v67, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v69, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v70, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v71, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v72, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v73, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v74, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v75, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v76, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v77, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v78, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v77, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v79, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v55, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v56, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v75, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v110, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v77, resolved type: android.media.MediaFormat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v57, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v69, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v70, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v112, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v113, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v115, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v116, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v118, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v74, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v121, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v75, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v76, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v77, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v78, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v79, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v125, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v58, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v59, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v126, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v80, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v159, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v160, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v133, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v62, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v79, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v63, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v136, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v81, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v65, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v138, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v83, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v66, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v139, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r101v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v140, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v141, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v161, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v85, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v67, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v142, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v162, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v163, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v143, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v164, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v144, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v167, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v145, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v170, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v171, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v172, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v68, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v69, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v173, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v88, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v71, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v175, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v92, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v96, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v176, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v94, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v97, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v177, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v95, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v98, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v178, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v96, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v90, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v57, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v74, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v91, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v99, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v58, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v75, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v100, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v179, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v101, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v180, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v102, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v183, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v103, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v184, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v104, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v185, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v105, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v186, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v106, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v105, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v189, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v76, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v107, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v106, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v190, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v79, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v108, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v107, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v191, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v109, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v109, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v192, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v82, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v110, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v110, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v77, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v61, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v62, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v111, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v116, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v118, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v78, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v64, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v112, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r95v28, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v66, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v113, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v79, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v67, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v80, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v81, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v82, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v83, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v114, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v84, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v85, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v86, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v115, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v80, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v87, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v88, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v116, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v89, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v90, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v117, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v91, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v74, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v93, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v94, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v118, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v95, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v100, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v119, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v101, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v102, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v103, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v120, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v126, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v86, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v115, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v121, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v128, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v116, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v129, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v132, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v122, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v123, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v124, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v137, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v83, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v125, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v229, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v138, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v84, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v126, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v230, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v142, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v85, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v127, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v92, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v231, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v149, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v121, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v145, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v146, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v147, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v148, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v122, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v123, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v124, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v125, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v127, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v130, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v134, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v136, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v136, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v137, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v138, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v162, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v163, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v164, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v165, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v166, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v167, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v138, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v139, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v71, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v186, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v168, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v140, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v157, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v164, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v165, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v166, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v167, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v168, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v169, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v170, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v188, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v190, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v191, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v192, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v193, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v194, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v195, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v196, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v197, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v198, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v199, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v200, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v201, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v202, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v203, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v204, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v205, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v206, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v207, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v169, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v171, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v174, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v176, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v177, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v179, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v180, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v142, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v74, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v171, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v173, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v175, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v176, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v177, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v178, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v179, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v182, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v183, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v186, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v187, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v188, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v208, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v209, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v210, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v211, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v212, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v213, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v214, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v216, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v217, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v218, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v219, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v182, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v183, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v184, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v185, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v186, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v161, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v162, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v163, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v164, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v165, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v166, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v167, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v168, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v169, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v170, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v171, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v172, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v173, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v174, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v175, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v188, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v189, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v190, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v191, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v193, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v194, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v195, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v196, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v197, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v198, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v176, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v177, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v178, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v179, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v180, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v181, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v182, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v183, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v184, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v185, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v195, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v196, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v197, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v198, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v199, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v200, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v201, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v202, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v203, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v204, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v205, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v206, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v207, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v208, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v232, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v233, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v224, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v225, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v226, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v200, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v201, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v202, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v203, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v204, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v205, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v206, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v207, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v208, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v209, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v210, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v230, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v211, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v233, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v212, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v209, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v186, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v189, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v190, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v211, resolved type: android.media.MediaFormat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v212, resolved type: android.media.MediaFormat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v192, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v193, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v194, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v240, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v241, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v195, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v196, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v200, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v201, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v228, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v231, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v232, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v233, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v236, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v237, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v238, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v239, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v240, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v241, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v242, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v243, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v245, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v246, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v247, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v248, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v249, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v255, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v257, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v243, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v202, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v244, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v245, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v246, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v247, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v208, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v214, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v215, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v216, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v217, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r11v14 */
    /* JADX WARNING: type inference failed for: r73v4 */
    /* JADX WARNING: type inference failed for: r73v5 */
    /* JADX WARNING: type inference failed for: r3v49 */
    /* JADX WARNING: type inference failed for: r73v7 */
    /* JADX WARNING: type inference failed for: r3v50 */
    /* JADX WARNING: type inference failed for: r3v51 */
    /* JADX WARNING: type inference failed for: r3v54 */
    /* JADX WARNING: type inference failed for: r73v8 */
    /* JADX WARNING: type inference failed for: r3v55 */
    /* JADX WARNING: type inference failed for: r3v56 */
    /* JADX WARNING: type inference failed for: r3v57 */
    /* JADX WARNING: type inference failed for: r3v58 */
    /* JADX WARNING: type inference failed for: r3v59 */
    /* JADX WARNING: type inference failed for: r3v62 */
    /* JADX WARNING: type inference failed for: r4v85 */
    /* JADX WARNING: type inference failed for: r4v87 */
    /* JADX WARNING: type inference failed for: r3v63 */
    /* JADX WARNING: type inference failed for: r3v64 */
    /* JADX WARNING: type inference failed for: r4v93 */
    /* JADX WARNING: type inference failed for: r3v65 */
    /* JADX WARNING: type inference failed for: r3v66 */
    /* JADX WARNING: type inference failed for: r3v67 */
    /* JADX WARNING: type inference failed for: r3v68 */
    /* JADX WARNING: type inference failed for: r3v69 */
    /* JADX WARNING: type inference failed for: r3v70 */
    /* JADX WARNING: type inference failed for: r3v71 */
    /* JADX WARNING: type inference failed for: r3v72 */
    /* JADX WARNING: type inference failed for: r95v36 */
    /* JADX WARNING: type inference failed for: r7v115 */
    /* JADX WARNING: type inference failed for: r95v37 */
    /* JADX WARNING: type inference failed for: r8v124 */
    /* JADX WARNING: type inference failed for: r8v125 */
    /* JADX WARNING: type inference failed for: r8v126 */
    /* JADX WARNING: type inference failed for: r8v127 */
    /* JADX WARNING: type inference failed for: r8v128 */
    /* JADX WARNING: type inference failed for: r8v129 */
    /* JADX WARNING: type inference failed for: r8v130 */
    /* JADX WARNING: type inference failed for: r8v131 */
    /* JADX WARNING: type inference failed for: r8v132 */
    /* JADX WARNING: type inference failed for: r8v133 */
    /* JADX WARNING: type inference failed for: r8v134 */
    /* JADX WARNING: type inference failed for: r11v168 */
    /* JADX WARNING: type inference failed for: r11v169 */
    /* JADX WARNING: type inference failed for: r11v170 */
    /* JADX WARNING: type inference failed for: r11v171 */
    /* JADX WARNING: type inference failed for: r11v172 */
    /* JADX WARNING: type inference failed for: r11v173 */
    /* JADX WARNING: type inference failed for: r11v174 */
    /* JADX WARNING: type inference failed for: r11v175 */
    /* JADX WARNING: type inference failed for: r11v176 */
    /* JADX WARNING: type inference failed for: r11v177 */
    /* JADX WARNING: type inference failed for: r11v178 */
    /* JADX WARNING: type inference failed for: r11v182 */
    /* JADX WARNING: type inference failed for: r11v183 */
    /* JADX WARNING: type inference failed for: r11v184 */
    /* JADX WARNING: type inference failed for: r11v185 */
    /* JADX WARNING: type inference failed for: r8v141 */
    /* JADX WARNING: type inference failed for: r8v143 */
    /* JADX WARNING: type inference failed for: r8v144 */
    /* JADX WARNING: type inference failed for: r8v145 */
    /* JADX WARNING: type inference failed for: r8v146 */
    /* JADX WARNING: type inference failed for: r8v147 */
    /* JADX WARNING: type inference failed for: r8v148 */
    /* JADX WARNING: type inference failed for: r8v149 */
    /* JADX WARNING: type inference failed for: r8v150 */
    /* JADX WARNING: type inference failed for: r8v151 */
    /* JADX WARNING: type inference failed for: r8v152 */
    /* JADX WARNING: type inference failed for: r8v153 */
    /* JADX WARNING: type inference failed for: r8v154 */
    /* JADX WARNING: type inference failed for: r8v155 */
    /* JADX WARNING: type inference failed for: r8v156 */
    /* JADX WARNING: type inference failed for: r8v157 */
    /* JADX WARNING: type inference failed for: r8v158 */
    /* JADX WARNING: type inference failed for: r8v159 */
    /* JADX WARNING: type inference failed for: r8v160 */
    /* JADX WARNING: type inference failed for: r11v221 */
    /* JADX WARNING: type inference failed for: r11v222 */
    /* JADX WARNING: type inference failed for: r11v236 */
    /* JADX WARNING: type inference failed for: r10v250 */
    /* JADX WARNING: type inference failed for: r10v251 */
    /* JADX WARNING: type inference failed for: r10v252 */
    /* JADX WARNING: type inference failed for: r10v253 */
    /* JADX WARNING: type inference failed for: r10v254 */
    /* JADX WARNING: type inference failed for: r10v256 */
    /* JADX WARNING: type inference failed for: r10v258 */
    /* JADX WARNING: type inference failed for: r11v242 */
    /* JADX WARNING: type inference failed for: r8v203 */
    /* JADX WARNING: type inference failed for: r8v204 */
    /* JADX WARNING: type inference failed for: r8v205 */
    /* JADX WARNING: type inference failed for: r8v207 */
    /* JADX WARNING: type inference failed for: r95v42 */
    /* JADX WARNING: type inference failed for: r95v43 */
    /* JADX WARNING: Code restructure failed: missing block: B:1064:0x18b6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1065:0x18b7, code lost:
        r10 = r0;
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1066:0x18b9, code lost:
        r10 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1067:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1076:0x18d5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1077:0x18d6, code lost:
        r3 = r95;
        r44 = r102;
        r25 = r76;
        r24 = r5;
        r5 = r41;
        r18 = r59;
        r2 = r62;
        r1 = r74;
        r4 = r0;
        r56 = r56;
        r8 = r8;
        r12 = r12;
        r11 = r11;
        r10 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1087:0x1916, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1088:0x1917, code lost:
        r74 = r2;
        r3 = r95;
        r44 = r102;
        r25 = r76;
        r24 = r5;
        r5 = r41;
        r18 = r59;
        r2 = r62;
        r1 = r74;
        r4 = r0;
        r56 = r56;
        r8 = r8;
        r12 = r12;
        r11 = r11;
        r10 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1091:0x1954, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1092:0x1955, code lost:
        r12 = r92;
        r11 = r93;
        r1 = r0;
        r8 = r19;
        r10 = r37;
        r14 = r59;
        r2 = r62;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1093:0x1964, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1094:0x1965, code lost:
        r3 = r95;
        r44 = r102;
        r25 = r4;
        r24 = r5;
        r5 = r41;
        r18 = r59;
        r2 = r62;
        r1 = r74;
        r4 = r0;
        r56 = r56;
        r8 = r8;
        r12 = r12;
        r11 = r11;
        r10 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1119:0x1a84, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1120:0x1a85, code lost:
        r13 = r94;
        r56 = r1;
        r74 = r2;
        r3 = r9;
        r44 = r102;
        r25 = r76;
        r24 = r5;
        r18 = r6;
        r5 = r41;
        r2 = r62;
        r1 = r74;
        r4 = r0;
        r12 = r12;
        r11 = r11;
        r10 = r10;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1121:0x1aa2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1122:0x1aa3, code lost:
        r13 = r94;
        r12 = r92;
        r11 = r93;
        r1 = r0;
        r14 = r6;
        r8 = r19;
        r10 = r37;
        r2 = r62;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1123:0x1ab3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1124:0x1ab4, code lost:
        r13 = r94;
        r56 = r1;
        r74 = r2;
        r3 = r9;
        r44 = r102;
        r25 = r76;
        r24 = r95;
        r18 = r6;
        r5 = r41;
        r2 = r62;
        r1 = r74;
        r4 = r0;
        r12 = r12;
        r11 = r11;
        r10 = r10;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1130:0x1b0b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1131:0x1b0c, code lost:
        r5 = r95;
        r56 = r1;
        r95 = r3;
        r36 = r13;
        r13 = r14;
        r14 = r52;
        r51 = r99;
        r44 = r7;
        r25 = r76;
        r24 = r5;
        r5 = r41;
        r2 = r62;
        r26 = r65;
        r1 = r4;
        r4 = r0;
        r12 = r12;
        r11 = r11;
        r10 = r10;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1132:0x1b32, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1133:0x1b33, code lost:
        r56 = r1;
        r36 = r13;
        r13 = r14;
        r9 = r51;
        r14 = r52;
        r51 = r99;
        r25 = r76;
        r24 = r95;
        r3 = r9;
        r5 = r41;
        r2 = r62;
        r26 = r65;
        r1 = r4;
        r4 = r0;
        r8 = r8;
        r10 = r10;
        r12 = r12;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1136:0x1b78, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1137:0x1b79, code lost:
        r13 = r14;
        r12 = r92;
        r11 = r93;
        r51 = r99;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1138:0x1b81, code lost:
        r14 = -5;
        r8 = r19;
        r10 = r37;
        r2 = r62;
        r26 = r65;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1139:0x1b8d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1140:0x1b8e, code lost:
        r36 = r13;
        r13 = r14;
        r9 = r51;
        r14 = r52;
        r51 = r99;
        r25 = r76;
        r24 = r95;
        r3 = r9;
        r56 = r26;
        r5 = r41;
        r2 = r62;
        r26 = r65;
        r1 = r74;
        r4 = r0;
        r8 = r8;
        r10 = r10;
        r12 = r12;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1141:0x1baf, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1142:0x1bb0, code lost:
        r74 = r3;
        r19 = r12;
        r36 = r13;
        r13 = r14;
        r9 = r51;
        r14 = r52;
        r51 = r99;
        r25 = r6;
        r24 = r7;
        r3 = r9;
        r56 = r26;
        r5 = r41;
        r2 = r62;
        r26 = r101;
        r1 = r74;
        r4 = r0;
        r8 = r8;
        r11 = r11;
        r10 = r10;
        r12 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1143:0x1bd5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1144:0x1bd6, code lost:
        r19 = r12;
        r36 = r13;
        r13 = r14;
        r9 = r51;
        r14 = r52;
        r51 = r99;
        r25 = r6;
        r24 = r7;
        r3 = r9;
        r56 = r26;
        r5 = r41;
        r1 = r61;
        r2 = r62;
        r26 = r101;
        r4 = r0;
        r8 = r8;
        r11 = r11;
        r10 = r10;
        r12 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1147:0x1c1a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1148:0x1c1b, code lost:
        r13 = r14;
        r11 = r93;
        r51 = r99;
        r1 = r0;
        r8 = r12;
        r14 = -5;
        r10 = r37;
        r2 = r62;
        r26 = r101;
        r12 = r92;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1149:0x1CLASSNAME, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1150:0x1CLASSNAME, code lost:
        r19 = r12;
        r36 = r13;
        r13 = r14;
        r9 = r51;
        r14 = r52;
        r51 = r99;
        r4 = r0;
        r3 = r9;
        r56 = r26;
        r5 = r41;
        r1 = r61;
        r2 = r62;
        r26 = r101;
        r8 = r8;
        r11 = r11;
        r10 = r10;
        r12 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1187:0x1d38, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1188:0x1d39, code lost:
        r1 = r0;
        r14 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1200:0x1d8a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1201:0x1d8b, code lost:
        r1 = r0;
        r14 = r18;
        r2 = r62;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1229:0x1e24, code lost:
        r1.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1233:?, code lost:
        r1.finishMovie();
        r15.endPresentationTime = r15.mediaMuxer.getLastFrameTimestamp(r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1234:0x1e37, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1235:0x1e38, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1288:0x14c2, code lost:
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1289:0x14c2, code lost:
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x0391, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x0392, code lost:
        r13 = r94;
        r51 = r99;
        r26 = r101;
        r1 = r0;
        r11 = r20;
        r8 = r31;
        r10 = r37;
        r2 = r44;
        r12 = r46;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x03a5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x03a6, code lost:
        r25 = r95;
        r24 = r1;
        r49 = r5;
        r18 = r14;
        r11 = r20;
        r12 = r46;
        r4 = r103;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x04ef, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x04f0, code lost:
        r49 = r5;
        r25 = r95;
        r4 = r103;
        r24 = r1;
        r18 = r14;
        r11 = r20;
        r12 = r46;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:277:0x062f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0630, code lost:
        r46 = r4;
        r4 = r103;
        r13 = r94;
        r11 = r5;
        r51 = r99;
        r26 = r101;
        r1 = r0;
        r14 = r18;
        r8 = r31;
        r10 = r37;
        r2 = r44;
        r12 = r46;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x064b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x064c, code lost:
        r46 = r4;
        r4 = r103;
        r11 = r5;
        r24 = r1;
        r25 = r95;
        r12 = r46;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x071c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x071d, code lost:
        r7 = r95;
        r49 = r5;
        r95 = r11;
        r46 = r12;
        r4 = r103;
        r24 = r1;
        r25 = r7;
        r18 = r14;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x0730, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x0731, code lost:
        r4 = r103;
        r95 = r11;
        r46 = r12;
        r13 = r94;
        r51 = r99;
        r26 = r101;
        r1 = r0;
        r8 = r31;
        r10 = r37;
        r2 = r44;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x07b0, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:311:0x07b1, code lost:
        r4 = r3;
        r95 = r11;
        r46 = r12;
        r13 = r94;
        r51 = r99;
        r26 = r101;
        r1 = r0;
        r14 = -5;
        r8 = r31;
        r10 = r37;
        r2 = r44;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x07c7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x07c8, code lost:
        r95 = r11;
        r46 = r12;
        r9 = r20;
        r4 = r3;
        r24 = r1;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x0a7d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x0a7e, code lost:
        r11 = r93;
        r51 = r99;
        r1 = r0;
        r26 = r3;
        r10 = r12;
        r13 = r14;
        r14 = -5;
        r8 = r37;
        r12 = r92;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x0a8f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x0a90, code lost:
        r36 = r13;
        r13 = r14;
        r56 = r26;
        r19 = r37;
        r5 = r41;
        r14 = r52;
        r26 = r3;
        r37 = r12;
        r3 = r51;
        r51 = r99;
        r4 = r0;
        r8 = r8;
        r11 = r11;
        r10 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x0CLASSNAME, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x0CLASSNAME, code lost:
        r12 = r92;
        r11 = r93;
        r51 = r99;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:519:0x0CLASSNAME, code lost:
        r13 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0CLASSNAME, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x0CLASSNAME, code lost:
        r24 = r95;
        r56 = r1;
        r1 = r4;
        r36 = r13;
        r13 = r14;
        r5 = r41;
        r3 = r51;
        r14 = r52;
        r2 = r62;
        r26 = r65;
        r25 = r76;
        r51 = r99;
        r4 = r0;
        r10 = r10;
        r12 = r12;
        r11 = r11;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0cc5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x0cc6, code lost:
        r24 = r95;
        r56 = r1;
        r1 = r4;
        r3 = r9;
        r36 = r13;
        r13 = r14;
        r5 = r41;
        r14 = r52;
        r2 = r62;
        r26 = r65;
        r25 = r76;
        r51 = r99;
        r4 = r0;
        r12 = r12;
        r10 = r10;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x0d0c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x0d49, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x0d4a, code lost:
        r11 = r97;
        r24 = r95;
        r56 = r1;
        r1 = r4;
        r3 = r6;
        r36 = r13;
        r13 = r14;
        r5 = r41;
        r14 = r52;
        r2 = r62;
        r26 = r65;
        r25 = r76;
        r51 = r99;
        r4 = r0;
        r12 = r12;
        r10 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x0dcd, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x0dce, code lost:
        r24 = r95;
        r56 = r1;
        r1 = r4;
        r44 = r7;
        r36 = r13;
        r13 = r14;
        r3 = r26;
        r5 = r41;
        r14 = r52;
        r2 = r62;
        r26 = r65;
        r25 = r76;
        r4 = r0;
        r51 = r8;
        r12 = r12;
        r10 = r10;
        r11 = r11;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x0deb, code lost:
        r0 = th;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x0e30, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0e32, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x0e33, code lost:
        r8 = r99;
        r24 = r95;
        r56 = r1;
        r1 = r4;
        r36 = r13;
        r13 = r14;
        r3 = r6;
        r5 = r41;
        r14 = r52;
        r2 = r62;
        r26 = r65;
        r25 = r76;
        r4 = r0;
        r51 = r8;
        r12 = r12;
        r10 = r10;
        r11 = r11;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:600:0x0e69, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0e6a, code lost:
        r11 = r97;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0e6c, code lost:
        r8 = r99;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0e6e, code lost:
        r12 = r92;
        r11 = r93;
        r1 = r0;
        r51 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x0e77, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0e78, code lost:
        r11 = r97;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x0e7c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x0e7d, code lost:
        r11 = r97;
        r24 = r95;
        r56 = r1;
        r1 = r4;
        r3 = r9;
        r36 = r13;
        r13 = r14;
        r5 = r41;
        r14 = r52;
        r2 = r62;
        r26 = r65;
        r25 = r76;
        r51 = r99;
        r4 = r0;
        r12 = r12;
        r10 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x0var_, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0var_, code lost:
        r13 = r94;
        r24 = r95;
        r44 = r102;
        r56 = r1;
        r1 = r4;
        r18 = r20;
        r5 = r41;
        r14 = r52;
        r2 = r62;
        r25 = r76;
        r4 = r0;
        r51 = r8;
        r12 = r12;
        r11 = r11;
        r10 = r10;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0fa9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0faa, code lost:
        r12 = r92;
        r11 = r93;
        r13 = r94;
        r1 = r0;
        r51 = r8;
        r8 = r19;
        r14 = r20;
        r10 = r37;
        r2 = r62;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0fbd, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0fbe, code lost:
        r13 = r94;
        r24 = r95;
        r44 = r7;
        r56 = r1;
        r1 = r4;
        r18 = r20;
        r5 = r41;
        r14 = r52;
        r2 = r62;
        r25 = r76;
        r4 = r0;
        r51 = r8;
        r12 = r12;
        r11 = r11;
        r10 = r10;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x101a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x101b, code lost:
        r7 = r3;
        r13 = r94;
        r24 = r95;
        r44 = r102;
        r56 = r1;
        r1 = r4;
        r18 = r20;
        r5 = r41;
        r14 = r52;
        r2 = r62;
        r25 = r76;
        r4 = r0;
        r51 = r8;
        r12 = r12;
        r11 = r11;
        r10 = r10;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x1052, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x1053, code lost:
        r13 = r94;
        r24 = r95;
        r44 = r102;
        r56 = r1;
        r45 = r2;
        r1 = r4;
        r3 = r7;
        r51 = r8;
        r18 = r20;
        r5 = r41;
        r2 = r62;
        r25 = r76;
        r4 = r0;
        r12 = r12;
        r11 = r11;
        r8 = r8;
        r10 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x1080, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x1081, code lost:
        r13 = r94;
        r24 = r95;
        r44 = r102;
        r4 = r0;
        r56 = r1;
        r45 = r2;
        r3 = r7;
        r51 = r8;
        r18 = r20;
        r5 = r41;
        r2 = r62;
        r1 = r74;
        r25 = r76;
        r12 = r12;
        r11 = r11;
        r8 = r8;
        r10 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x109b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x109c, code lost:
        r74 = r4;
        r13 = r94;
        r24 = r95;
        r44 = r102;
        r4 = r0;
        r56 = r1;
        r45 = r2;
        r3 = r7;
        r51 = r8;
        r18 = r20;
        r5 = r41;
        r2 = r62;
        r1 = r74;
        r25 = r76;
        r12 = r12;
        r11 = r11;
        r8 = r8;
        r10 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x1103, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x1104, code lost:
        r13 = r94;
        r24 = r95;
        r44 = r102;
        r4 = r0;
        r56 = r1;
        r45 = r2;
        r3 = r9;
        r18 = r20;
        r5 = r41;
        r2 = r62;
        r1 = r74;
        r25 = r76;
        r12 = r12;
        r11 = r11;
        r8 = r8;
        r10 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x112e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x112f, code lost:
        r13 = r94;
        r24 = r95;
        r4 = r0;
        r56 = r1;
        r5 = r7;
        r3 = r9;
        r18 = r20;
        r45 = r44;
        r2 = r62;
        r1 = r74;
        r25 = r76;
        r44 = r102;
        r12 = r12;
        r11 = r11;
        r8 = r8;
        r10 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x1160, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x1161, code lost:
        r13 = r94;
        r24 = r95;
        r4 = r0;
        r56 = r1;
        r3 = r9;
        r18 = r20;
        r5 = r41;
        r45 = r2;
        r2 = r62;
        r1 = r74;
        r25 = r76;
        r44 = r102;
        r12 = r12;
        r11 = r11;
        r8 = r8;
        r10 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x1193, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x1194, code lost:
        r51 = r8;
        r13 = r94;
        r24 = r95;
        r4 = r0;
        r56 = r1;
        r3 = r7;
        r18 = r20;
        r5 = r41;
        r45 = r2;
        r2 = r62;
        r1 = r74;
        r25 = r76;
        r44 = r102;
        r12 = r12;
        r11 = r11;
        r8 = r8;
        r10 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:0x11b1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x11b2, code lost:
        r74 = r4;
        r51 = r8;
        r13 = r94;
        r24 = r95;
        r4 = r0;
        r56 = r1;
        r3 = r7;
        r18 = r20;
        r5 = r41;
        r45 = r2;
        r2 = r62;
        r1 = r74;
        r25 = r76;
        r44 = r102;
        r12 = r12;
        r11 = r11;
        r8 = r8;
        r10 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x123c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x123d, code lost:
        r12 = r92;
        r11 = r93;
        r13 = r94;
        r1 = r0;
        r8 = r19;
        r14 = r20;
        r10 = r37;
        r2 = r62;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x124e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x124f, code lost:
        r13 = r94;
        r24 = r95;
        r44 = r102;
        r4 = r0;
        r56 = r1;
        r1 = r2;
        r3 = r9;
        r18 = r20;
        r5 = r41;
        r2 = r62;
        r25 = r76;
        r12 = r12;
        r11 = r11;
        r8 = r8;
        r10 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x1267, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x1268, code lost:
        r51 = r8;
        r12 = r92;
        r11 = r93;
        r13 = r94;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x1330, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x1331, code lost:
        r12 = r92;
        r11 = r93;
        r13 = r94;
        r1 = r0;
        r14 = r6;
        r8 = r19;
        r10 = r37;
        r2 = r62;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x139f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x13a0, code lost:
        r13 = r94;
        r3 = r95;
        r44 = r102;
        r4 = r0;
        r56 = r1;
        r1 = r2;
        r24 = r5;
        r18 = r20;
        r5 = r41;
        r2 = r62;
        r25 = r76;
        r12 = r12;
        r11 = r11;
        r8 = r8;
        r10 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0222, code lost:
        r42 = r3;
        r25 = r14;
        r14 = r92;
        r3 = r103;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1027:0x182f  */
    /* JADX WARNING: Removed duplicated region for block: B:1028:0x1832  */
    /* JADX WARNING: Removed duplicated region for block: B:1033:0x1842  */
    /* JADX WARNING: Removed duplicated region for block: B:1044:0x187f A[Catch:{ Exception -> 0x179e, all -> 0x1954 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1046:0x1883 A[Catch:{ Exception -> 0x179e, all -> 0x1954 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1047:0x1888 A[Catch:{ Exception -> 0x179e, all -> 0x1954 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1055:0x189f  */
    /* JADX WARNING: Removed duplicated region for block: B:1079:0x18f3 A[Catch:{ Exception -> 0x1964, all -> 0x1954 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1082:0x1902 A[Catch:{ Exception -> 0x1964, all -> 0x1954 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1086:0x1912 A[Catch:{ Exception -> 0x1964, all -> 0x1954 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1091:0x1954 A[ExcHandler: all (r0v35 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r13 r26 r51 r59 
      PHI: (r13v40 'videoIndex' int) = (r13v41 'videoIndex' int), (r13v41 'videoIndex' int), (r13v41 'videoIndex' int), (r13v41 'videoIndex' int), (r13v41 'videoIndex' int), (r13v41 'videoIndex' int), (r13v41 'videoIndex' int), (r13v41 'videoIndex' int), (r13v41 'videoIndex' int), (r13v41 'videoIndex' int), (r13v50 'videoIndex' int) binds: [B:1062:0x18b2, B:1071:0x18c4, B:1072:?, B:1074:0x18ce, B:1066:0x18b9, B:1063:?, B:1057:0x18a5, B:1052:0x189a, B:1053:?, B:1036:0x1847, B:996:0x172b] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r26v30 'avatarStartTime' long) = (r26v31 'avatarStartTime' long), (r26v31 'avatarStartTime' long), (r26v31 'avatarStartTime' long), (r26v31 'avatarStartTime' long), (r26v31 'avatarStartTime' long), (r26v31 'avatarStartTime' long), (r26v31 'avatarStartTime' long), (r26v31 'avatarStartTime' long), (r26v31 'avatarStartTime' long), (r26v31 'avatarStartTime' long), (r26v27 'avatarStartTime' long) binds: [B:1062:0x18b2, B:1071:0x18c4, B:1072:?, B:1074:0x18ce, B:1066:0x18b9, B:1063:?, B:1057:0x18a5, B:1052:0x189a, B:1053:?, B:1036:0x1847, B:996:0x172b] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r51v33 'endTime' long) = (r51v34 'endTime' long), (r51v34 'endTime' long), (r51v34 'endTime' long), (r51v34 'endTime' long), (r51v34 'endTime' long), (r51v34 'endTime' long), (r51v34 'endTime' long), (r51v34 'endTime' long), (r51v34 'endTime' long), (r51v34 'endTime' long), (r51v30 'endTime' long) binds: [B:1062:0x18b2, B:1071:0x18c4, B:1072:?, B:1074:0x18ce, B:1066:0x18b9, B:1063:?, B:1057:0x18a5, B:1052:0x189a, B:1053:?, B:1036:0x1847, B:996:0x172b] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r59v10 'videoTrackIndex' int) = (r59v11 'videoTrackIndex' int), (r59v11 'videoTrackIndex' int), (r59v11 'videoTrackIndex' int), (r59v11 'videoTrackIndex' int), (r59v11 'videoTrackIndex' int), (r59v11 'videoTrackIndex' int), (r59v11 'videoTrackIndex' int), (r59v11 'videoTrackIndex' int), (r59v11 'videoTrackIndex' int), (r59v11 'videoTrackIndex' int), (r59v20 'videoTrackIndex' int) binds: [B:1062:0x18b2, B:1071:0x18c4, B:1072:?, B:1074:0x18ce, B:1066:0x18b9, B:1063:?, B:1057:0x18a5, B:1052:0x189a, B:1053:?, B:1036:0x1847, B:996:0x172b] A[DONT_GENERATE, DONT_INLINE], Splitter:B:996:0x172b] */
    /* JADX WARNING: Removed duplicated region for block: B:1121:0x1aa2 A[ExcHandler: all (r0v30 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:783:0x12c1] */
    /* JADX WARNING: Removed duplicated region for block: B:1136:0x1b78 A[ExcHandler: all (r0v22 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:504:0x0CLASSNAME] */
    /* JADX WARNING: Removed duplicated region for block: B:1147:0x1c1a A[ExcHandler: all (r0v17 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:492:0x0be9] */
    /* JADX WARNING: Removed duplicated region for block: B:1185:0x1d2f A[Catch:{ all -> 0x1d38, all -> 0x1d8a }] */
    /* JADX WARNING: Removed duplicated region for block: B:1199:0x1d86 A[Catch:{ all -> 0x1d38, all -> 0x1d8a }] */
    /* JADX WARNING: Removed duplicated region for block: B:1203:0x1d94 A[Catch:{ all -> 0x1d38, all -> 0x1d8a }] */
    /* JADX WARNING: Removed duplicated region for block: B:1205:0x1d99 A[Catch:{ all -> 0x1d38, all -> 0x1d8a }] */
    /* JADX WARNING: Removed duplicated region for block: B:1207:0x1da1 A[Catch:{ all -> 0x1d38, all -> 0x1d8a }] */
    /* JADX WARNING: Removed duplicated region for block: B:1212:0x1daf  */
    /* JADX WARNING: Removed duplicated region for block: B:1215:0x1db6 A[SYNTHETIC, Splitter:B:1215:0x1db6] */
    /* JADX WARNING: Removed duplicated region for block: B:1229:0x1e24  */
    /* JADX WARNING: Removed duplicated region for block: B:1232:0x1e2b A[SYNTHETIC, Splitter:B:1232:0x1e2b] */
    /* JADX WARNING: Removed duplicated region for block: B:1238:0x1e4c  */
    /* JADX WARNING: Removed duplicated region for block: B:1240:0x1e82  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x0391 A[ExcHandler: all (r0v145 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r46 
      PHI: (r46v23 'resultWidth' int) = (r46v24 'resultWidth' int), (r46v24 'resultWidth' int), (r46v24 'resultWidth' int), (r46v24 'resultWidth' int), (r46v24 'resultWidth' int), (r46v24 'resultWidth' int), (r46v30 'resultWidth' int) binds: [B:212:0x0495, B:213:?, B:217:0x049e, B:168:0x03ca, B:151:0x038e, B:152:?, B:121:0x02e6] A[DONT_GENERATE, DONT_INLINE], Splitter:B:121:0x02e6] */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0597 A[Catch:{ Exception -> 0x064b, all -> 0x062f }] */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x0599 A[Catch:{ Exception -> 0x064b, all -> 0x062f }] */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x062f A[ExcHandler: all (r0v140 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r4 r5 r18 
      PHI: (r4v167 'resultWidth' int) = (r4v169 'resultWidth' int), (r4v169 'resultWidth' int), (r4v182 'resultWidth' int) binds: [B:263:0x05ce, B:264:?, B:251:0x0591] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r5v121 'resultHeight' int) = (r5v122 'resultHeight' int), (r5v122 'resultHeight' int), (r5v126 'resultHeight' int) binds: [B:263:0x05ce, B:264:?, B:251:0x0591] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r18v66 'videoTrackIndex' int) = (r18v67 'videoTrackIndex' int), (r18v67 'videoTrackIndex' int), (r18v74 'videoTrackIndex' int) binds: [B:263:0x05ce, B:264:?, B:251:0x0591] A[DONT_GENERATE, DONT_INLINE], Splitter:B:251:0x0591] */
    /* JADX WARNING: Removed duplicated region for block: B:299:0x0730 A[ExcHandler: all (r0v138 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:85:0x022b] */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x07b0 A[ExcHandler: all (r0v133 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:61:0x01bf] */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x089e A[Catch:{ all -> 0x08ba }] */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x08a3 A[Catch:{ all -> 0x08ba }] */
    /* JADX WARNING: Removed duplicated region for block: B:353:0x08a8 A[Catch:{ all -> 0x08ba }] */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x0a7d A[ExcHandler: all (r0v111 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r2 r3 
      PHI: (r2v161 'bitrate' int) = (r2v28 'bitrate' int), (r2v28 'bitrate' int), (r2v27 'bitrate' int) binds: [B:443:0x0ab6, B:444:?, B:430:0x0a76] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r3v141 'avatarStartTime' long) = (r3v18 'avatarStartTime' long), (r3v18 'avatarStartTime' long), (r3v17 'avatarStartTime' long) binds: [B:443:0x0ab6, B:444:?, B:430:0x0a76] A[DONT_GENERATE, DONT_INLINE], Splitter:B:430:0x0a76] */
    /* JADX WARNING: Removed duplicated region for block: B:514:0x0c6d A[SYNTHETIC, Splitter:B:514:0x0c6d] */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x0CLASSNAME A[ExcHandler: all (th java.lang.Throwable), Splitter:B:514:0x0c6d] */
    /* JADX WARNING: Removed duplicated region for block: B:524:0x0ca4 A[SYNTHETIC, Splitter:B:524:0x0ca4] */
    /* JADX WARNING: Removed duplicated region for block: B:540:0x0cef  */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0cf1  */
    /* JADX WARNING: Removed duplicated region for block: B:543:0x0cf4  */
    /* JADX WARNING: Removed duplicated region for block: B:591:0x0deb A[ExcHandler: all (th java.lang.Throwable), Splitter:B:583:0x0db2] */
    /* JADX WARNING: Removed duplicated region for block: B:596:0x0e30 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:566:0x0d6c] */
    /* JADX WARNING: Removed duplicated region for block: B:599:0x0e52  */
    /* JADX WARNING: Removed duplicated region for block: B:604:0x0e77 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:524:0x0ca4] */
    /* JADX WARNING: Removed duplicated region for block: B:606:0x0e7c A[ExcHandler: Exception (r0v86 'e' java.lang.Exception A[CUSTOM_DECLARE]), PHI: r10 
      PHI: (r10v75 float) = (r10v166 float), (r10v246 float), (r10v247 float), (r10v248 float) binds: [B:524:0x0ca4, B:535:0x0ce3, B:536:?, B:537:0x0ce9] A[DONT_GENERATE, DONT_INLINE], Splitter:B:524:0x0ca4] */
    /* JADX WARNING: Removed duplicated region for block: B:608:0x0e99  */
    /* JADX WARNING: Removed duplicated region for block: B:610:0x0eaa  */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0eac  */
    /* JADX WARNING: Removed duplicated region for block: B:633:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0fa9 A[ExcHandler: all (r0v81 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:638:0x0var_] */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x1048  */
    /* JADX WARNING: Removed duplicated region for block: B:696:0x1070 A[SYNTHETIC, Splitter:B:696:0x1070] */
    /* JADX WARNING: Removed duplicated region for block: B:705:0x10b8  */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x10c3  */
    /* JADX WARNING: Removed duplicated region for block: B:749:0x118a  */
    /* JADX WARNING: Removed duplicated region for block: B:769:0x123c A[ExcHandler: all (r0v64 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r20 r26 r51 
      PHI: (r20v24 'videoTrackIndex' int) = (r20v22 'videoTrackIndex' int), (r20v4 'videoTrackIndex' int), (r20v4 'videoTrackIndex' int), (r20v4 'videoTrackIndex' int), (r20v4 'videoTrackIndex' int), (r20v4 'videoTrackIndex' int), (r20v4 'videoTrackIndex' int), (r20v4 'videoTrackIndex' int), (r20v4 'videoTrackIndex' int), (r20v4 'videoTrackIndex' int) binds: [B:821:0x1385, B:764:0x1222, B:724:0x10f8, B:725:?, B:734:0x111e, B:735:?, B:737:0x1127, B:738:?, B:728:0x10ff, B:729:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r26v34 'avatarStartTime' long) = (r26v27 'avatarStartTime' long), (r26v26 'avatarStartTime' long), (r26v26 'avatarStartTime' long), (r26v26 'avatarStartTime' long), (r26v26 'avatarStartTime' long), (r26v26 'avatarStartTime' long), (r26v26 'avatarStartTime' long), (r26v26 'avatarStartTime' long), (r26v26 'avatarStartTime' long), (r26v26 'avatarStartTime' long) binds: [B:821:0x1385, B:764:0x1222, B:724:0x10f8, B:725:?, B:734:0x111e, B:735:?, B:737:0x1127, B:738:?, B:728:0x10ff, B:729:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r51v41 'endTime' long) = (r51v30 'endTime' long), (r51v44 'endTime' long), (r51v55 'endTime' long), (r51v55 'endTime' long), (r51v55 'endTime' long), (r51v55 'endTime' long), (r51v55 'endTime' long), (r51v55 'endTime' long), (r51v55 'endTime' long), (r51v55 'endTime' long) binds: [B:821:0x1385, B:764:0x1222, B:724:0x10f8, B:725:?, B:734:0x111e, B:735:?, B:737:0x1127, B:738:?, B:728:0x10ff, B:729:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:724:0x10f8] */
    /* JADX WARNING: Removed duplicated region for block: B:774:0x1267 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:681:0x1037] */
    /* JADX WARNING: Removed duplicated region for block: B:778:0x1291  */
    /* JADX WARNING: Removed duplicated region for block: B:786:0x12c6  */
    /* JADX WARNING: Removed duplicated region for block: B:787:0x12ce  */
    /* JADX WARNING: Removed duplicated region for block: B:793:0x12de  */
    /* JADX WARNING: Removed duplicated region for block: B:794:0x12f5  */
    /* JADX WARNING: Removed duplicated region for block: B:803:0x1330 A[Catch:{ Exception -> 0x140a, all -> 0x1330 }, ExcHandler: all (r0v62 'th' java.lang.Throwable A[CUSTOM_DECLARE, Catch:{ Exception -> 0x140a, all -> 0x1330 }]), Splitter:B:898:0x14f0] */
    /* JADX WARNING: Removed duplicated region for block: B:941:0x1607 A[Catch:{ Exception -> 0x19e8, all -> 0x19d6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:942:0x1609 A[Catch:{ Exception -> 0x19e8, all -> 0x19d6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:947:0x1619  */
    /* JADX WARNING: Removed duplicated region for block: B:948:0x1637  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideoInternal(java.lang.String r86, java.io.File r87, int r88, boolean r89, int r90, int r91, int r92, int r93, int r94, int r95, int r96, long r97, long r99, long r101, long r103, boolean r105, boolean r106, org.telegram.messenger.MediaController.SavedFilterState r107, java.lang.String r108, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r109, boolean r110, org.telegram.messenger.MediaController.CropState r111) {
        /*
            r85 = this;
            r15 = r85
            r13 = r86
            r14 = r88
            r12 = r92
            r11 = r93
            r9 = r94
            r10 = r95
            r7 = r96
            r5 = r97
            r3 = r103
            r8 = r105
            r2 = r111
            java.lang.String r1 = " framerate: "
            java.lang.String r14 = "bitrate: "
            long r28 = java.lang.System.currentTimeMillis()
            r16 = 0
            r17 = 0
            r18 = -5
            android.media.MediaCodec$BufferInfo r19 = new android.media.MediaCodec$BufferInfo     // Catch:{ all -> 0x1de5 }
            r19.<init>()     // Catch:{ all -> 0x1de5 }
            r20 = r19
            org.telegram.messenger.video.Mp4Movie r19 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ all -> 0x1de5 }
            r19.<init>()     // Catch:{ all -> 0x1de5 }
            r21 = r19
            r7 = r87
            r5 = r21
            r5.setCacheFile(r7)     // Catch:{ all -> 0x1de5 }
            r6 = 0
            r5.setRotation(r6)     // Catch:{ all -> 0x1de5 }
            r5.setSize(r12, r11)     // Catch:{ all -> 0x1de5 }
            org.telegram.messenger.video.MP4Builder r6 = new org.telegram.messenger.video.MP4Builder     // Catch:{ all -> 0x1de5 }
            r6.<init>()     // Catch:{ all -> 0x1de5 }
            r7 = r89
            org.telegram.messenger.video.MP4Builder r6 = r6.createMovie(r5, r7)     // Catch:{ all -> 0x1de5 }
            r15.mediaMuxer = r6     // Catch:{ all -> 0x1de5 }
            r21 = r5
            r5 = 0
            float r2 = (float) r3     // Catch:{ all -> 0x1de5 }
            r22 = 1148846080(0x447a0000, float:1000.0)
            float r23 = r2 / r22
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r30 = 1000(0x3e8, double:4.94E-321)
            long r7 = r3 * r30
            r15.endPresentationTime = r7     // Catch:{ all -> 0x1de5 }
            r85.checkConversionCanceled()     // Catch:{ all -> 0x1de5 }
            java.lang.String r8 = "csd-1"
            java.lang.String r2 = "csd-0"
            java.lang.String r7 = "prepend-sps-pps-to-idr-frames"
            r31 = r14
            java.lang.String r14 = "video/avc"
            r37 = r14
            if (r110 == 0) goto L_0x0909
            r14 = 0
            r41 = 0
            r42 = 0
            r39 = 0
            int r43 = (r101 > r39 ? 1 : (r101 == r39 ? 0 : -1))
            if (r43 < 0) goto L_0x009d
            r43 = 1157234688(0x44fa0000, float:2000.0)
            int r43 = (r23 > r43 ? 1 : (r23 == r43 ? 0 : -1))
            if (r43 > 0) goto L_0x008e
            r10 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x00a2
        L_0x008e:
            r43 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r43 = (r23 > r43 ? 1 : (r23 == r43 ? 0 : -1))
            if (r43 > 0) goto L_0x0099
            r10 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x00a2
        L_0x0099:
            r10 = 1560000(0x17cdc0, float:2.186026E-39)
            goto L_0x00a2
        L_0x009d:
            if (r10 > 0) goto L_0x00a2
            r10 = 921600(0xe1000, float:1.291437E-39)
        L_0x00a2:
            int r43 = r12 % 16
            r44 = 1098907648(0x41800000, float:16.0)
            if (r43 == 0) goto L_0x00fc
            boolean r43 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00ef, all -> 0x00e0 }
            if (r43 == 0) goto L_0x00d4
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00ef, all -> 0x00e0 }
            r13.<init>()     // Catch:{ Exception -> 0x00ef, all -> 0x00e0 }
            r45 = r14
            java.lang.String r14 = "changing width from "
            r13.append(r14)     // Catch:{ Exception -> 0x00ef, all -> 0x00e0 }
            r13.append(r12)     // Catch:{ Exception -> 0x00ef, all -> 0x00e0 }
            java.lang.String r14 = " to "
            r13.append(r14)     // Catch:{ Exception -> 0x00ef, all -> 0x00e0 }
            float r14 = (float) r12     // Catch:{ Exception -> 0x00ef, all -> 0x00e0 }
            float r14 = r14 / r44
            int r14 = java.lang.Math.round(r14)     // Catch:{ Exception -> 0x00ef, all -> 0x00e0 }
            int r14 = r14 * 16
            r13.append(r14)     // Catch:{ Exception -> 0x00ef, all -> 0x00e0 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x00ef, all -> 0x00e0 }
            org.telegram.messenger.FileLog.d(r13)     // Catch:{ Exception -> 0x00ef, all -> 0x00e0 }
            goto L_0x00d6
        L_0x00d4:
            r45 = r14
        L_0x00d6:
            float r13 = (float) r12     // Catch:{ Exception -> 0x00ef, all -> 0x00e0 }
            float r13 = r13 / r44
            int r13 = java.lang.Math.round(r13)     // Catch:{ Exception -> 0x00ef, all -> 0x00e0 }
            int r12 = r13 * 16
            goto L_0x00fe
        L_0x00e0:
            r0 = move-exception
            r51 = r99
            r26 = r101
            r13 = r9
            r2 = r10
            r14 = r18
            r8 = r31
            r10 = r1
            r1 = r0
            goto L_0x1df2
        L_0x00ef:
            r0 = move-exception
            r37 = r1
            r49 = r5
            r44 = r10
            r9 = r20
            r1 = r0
            r4 = r3
            goto L_0x085b
        L_0x00fc:
            r45 = r14
        L_0x00fe:
            int r13 = r11 % 16
            if (r13 == 0) goto L_0x0151
            boolean r13 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0144, all -> 0x0135 }
            if (r13 == 0) goto L_0x012b
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0144, all -> 0x0135 }
            r13.<init>()     // Catch:{ Exception -> 0x0144, all -> 0x0135 }
            java.lang.String r14 = "changing height from "
            r13.append(r14)     // Catch:{ Exception -> 0x0144, all -> 0x0135 }
            r13.append(r11)     // Catch:{ Exception -> 0x0144, all -> 0x0135 }
            java.lang.String r14 = " to "
            r13.append(r14)     // Catch:{ Exception -> 0x0144, all -> 0x0135 }
            float r14 = (float) r11     // Catch:{ Exception -> 0x0144, all -> 0x0135 }
            float r14 = r14 / r44
            int r14 = java.lang.Math.round(r14)     // Catch:{ Exception -> 0x0144, all -> 0x0135 }
            int r14 = r14 * 16
            r13.append(r14)     // Catch:{ Exception -> 0x0144, all -> 0x0135 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0144, all -> 0x0135 }
            org.telegram.messenger.FileLog.d(r13)     // Catch:{ Exception -> 0x0144, all -> 0x0135 }
        L_0x012b:
            float r13 = (float) r11     // Catch:{ Exception -> 0x0144, all -> 0x0135 }
            float r13 = r13 / r44
            int r13 = java.lang.Math.round(r13)     // Catch:{ Exception -> 0x0144, all -> 0x0135 }
            int r11 = r13 * 16
            goto L_0x0151
        L_0x0135:
            r0 = move-exception
            r51 = r99
            r26 = r101
            r13 = r9
            r2 = r10
            r14 = r18
            r8 = r31
            r10 = r1
            r1 = r0
            goto L_0x1df2
        L_0x0144:
            r0 = move-exception
            r37 = r1
            r49 = r5
            r44 = r10
            r9 = r20
            r1 = r0
            r4 = r3
            goto L_0x085b
        L_0x0151:
            boolean r13 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0808, all -> 0x07f0 }
            if (r13 == 0) goto L_0x0196
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0189, all -> 0x017a }
            r13.<init>()     // Catch:{ Exception -> 0x0189, all -> 0x017a }
            java.lang.String r14 = "create photo encoder "
            r13.append(r14)     // Catch:{ Exception -> 0x0189, all -> 0x017a }
            r13.append(r12)     // Catch:{ Exception -> 0x0189, all -> 0x017a }
            java.lang.String r14 = " "
            r13.append(r14)     // Catch:{ Exception -> 0x0189, all -> 0x017a }
            r13.append(r11)     // Catch:{ Exception -> 0x0189, all -> 0x017a }
            java.lang.String r14 = " duration = "
            r13.append(r14)     // Catch:{ Exception -> 0x0189, all -> 0x017a }
            r13.append(r3)     // Catch:{ Exception -> 0x0189, all -> 0x017a }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0189, all -> 0x017a }
            org.telegram.messenger.FileLog.d(r13)     // Catch:{ Exception -> 0x0189, all -> 0x017a }
            goto L_0x0196
        L_0x017a:
            r0 = move-exception
            r51 = r99
            r26 = r101
            r13 = r9
            r2 = r10
            r14 = r18
            r8 = r31
            r10 = r1
            r1 = r0
            goto L_0x1df2
        L_0x0189:
            r0 = move-exception
            r37 = r1
            r49 = r5
            r44 = r10
            r9 = r20
            r1 = r0
            r4 = r3
            goto L_0x085b
        L_0x0196:
            r13 = r37
            android.media.MediaFormat r14 = android.media.MediaFormat.createVideoFormat(r13, r12, r11)     // Catch:{ Exception -> 0x0808, all -> 0x07f0 }
            java.lang.String r9 = "color-format"
            r37 = r1
            r1 = 2130708361(0x7var_, float:1.701803E38)
            r14.setInteger(r9, r1)     // Catch:{ Exception -> 0x07ee, all -> 0x07d6 }
            java.lang.String r1 = "bitrate"
            r14.setInteger(r1, r10)     // Catch:{ Exception -> 0x07ee, all -> 0x07d6 }
            java.lang.String r1 = "frame-rate"
            r9 = 30
            r14.setInteger(r1, r9)     // Catch:{ Exception -> 0x07ee, all -> 0x07d6 }
            java.lang.String r1 = "i-frame-interval"
            r9 = 1
            r14.setInteger(r1, r9)     // Catch:{ Exception -> 0x07ee, all -> 0x07d6 }
            android.media.MediaCodec r1 = android.media.MediaCodec.createEncoderByType(r13)     // Catch:{ Exception -> 0x07ee, all -> 0x07d6 }
            r44 = r10
            r10 = 0
            r1.configure(r14, r10, r10, r9)     // Catch:{ Exception -> 0x07c7, all -> 0x07b0 }
            org.telegram.messenger.video.InputSurface r9 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x07c7, all -> 0x07b0 }
            android.view.Surface r10 = r1.createInputSurface()     // Catch:{ Exception -> 0x07c7, all -> 0x07b0 }
            r9.<init>(r10)     // Catch:{ Exception -> 0x07c7, all -> 0x07b0 }
            r9.makeCurrent()     // Catch:{ Exception -> 0x079e, all -> 0x07b0 }
            r1.start()     // Catch:{ Exception -> 0x079e, all -> 0x07b0 }
            r10 = 0
            r24 = 0
            r92 = r10
            int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x079e, all -> 0x07b0 }
            r93 = r14
            r14 = 21
            if (r10 >= r14) goto L_0x0202
            java.nio.ByteBuffer[] r10 = r1.getOutputBuffers()     // Catch:{ Exception -> 0x01f5, all -> 0x01e3 }
            goto L_0x0204
        L_0x01e3:
            r0 = move-exception
            r13 = r94
            r51 = r99
            r26 = r101
            r1 = r0
            r14 = r18
            r8 = r31
            r10 = r37
            r2 = r44
            goto L_0x1df2
        L_0x01f5:
            r0 = move-exception
            r24 = r1
            r49 = r5
            r25 = r9
            r9 = r20
            r1 = r0
            r4 = r3
            goto L_0x085b
        L_0x0202:
            r10 = r92
        L_0x0204:
            r14 = 1
            r85.checkConversionCanceled()     // Catch:{ Exception -> 0x079e, all -> 0x07b0 }
            r25 = r18
            r18 = r14
            r14 = r45
        L_0x020e:
            if (r14 != 0) goto L_0x0786
            r85.checkConversionCanceled()     // Catch:{ Exception -> 0x0772, all -> 0x075b }
            r36 = r41 ^ 1
            r45 = 1
            r92 = r14
            r14 = r25
            r3 = r42
        L_0x021d:
            if (r36 != 0) goto L_0x022b
            if (r45 == 0) goto L_0x0222
            goto L_0x022b
        L_0x0222:
            r42 = r3
            r25 = r14
            r14 = r92
            r3 = r103
            goto L_0x020e
        L_0x022b:
            r85.checkConversionCanceled()     // Catch:{ Exception -> 0x0746, all -> 0x0730 }
            if (r106 == 0) goto L_0x0237
            r46 = 22000(0x55f0, double:1.08694E-319)
            r25 = r3
            r3 = r46
            goto L_0x023b
        L_0x0237:
            r25 = r3
            r3 = 2500(0x9c4, double:1.235E-320)
        L_0x023b:
            r95 = r9
            r9 = r20
            int r3 = r1.dequeueOutputBuffer(r9, r3)     // Catch:{ Exception -> 0x071c, all -> 0x0730 }
            r4 = -1
            if (r3 != r4) goto L_0x0259
            r4 = 0
            r20 = r2
            r45 = r4
            r49 = r5
            r47 = r7
            r5 = r11
            r4 = r12
            r2 = r18
            r18 = r14
            r14 = r92
            goto L_0x05a1
        L_0x0259:
            r4 = -3
            if (r3 != r4) goto L_0x02cf
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02bf, all -> 0x02ad }
            r20 = r11
            r11 = 21
            if (r4 >= r11) goto L_0x029c
            java.nio.ByteBuffer[] r4 = r1.getOutputBuffers()     // Catch:{ Exception -> 0x028c, all -> 0x027a }
            r10 = r4
            r49 = r5
            r47 = r7
            r4 = r12
            r5 = r20
            r20 = r2
            r2 = r18
            r18 = r14
            r14 = r92
            goto L_0x05a1
        L_0x027a:
            r0 = move-exception
            r13 = r94
            r51 = r99
            r26 = r101
            r1 = r0
            r11 = r20
            r8 = r31
            r10 = r37
            r2 = r44
            goto L_0x1df2
        L_0x028c:
            r0 = move-exception
            r25 = r95
            r24 = r1
            r49 = r5
            r18 = r14
            r11 = r20
            r4 = r103
            r1 = r0
            goto L_0x085b
        L_0x029c:
            r49 = r5
            r47 = r7
            r4 = r12
            r5 = r20
            r20 = r2
            r2 = r18
            r18 = r14
            r14 = r92
            goto L_0x05a1
        L_0x02ad:
            r0 = move-exception
            r20 = r11
            r13 = r94
            r51 = r99
            r26 = r101
            r1 = r0
            r8 = r31
            r10 = r37
            r2 = r44
            goto L_0x1df2
        L_0x02bf:
            r0 = move-exception
            r20 = r11
            r25 = r95
            r24 = r1
            r49 = r5
            r18 = r14
            r4 = r103
            r1 = r0
            goto L_0x085b
        L_0x02cf:
            r20 = r11
            r4 = -2
            if (r3 != r4) goto L_0x0384
            android.media.MediaFormat r4 = r1.getOutputFormat()     // Catch:{ Exception -> 0x0372, all -> 0x035e }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0372, all -> 0x035e }
            if (r11 == 0) goto L_0x02f4
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0372, all -> 0x035e }
            r11.<init>()     // Catch:{ Exception -> 0x0372, all -> 0x035e }
            r46 = r12
            java.lang.String r12 = "photo encoder new format "
            r11.append(r12)     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            r11.append(r4)     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            org.telegram.messenger.FileLog.d(r11)     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            goto L_0x02f6
        L_0x02f4:
            r46 = r12
        L_0x02f6:
            r11 = -5
            if (r14 != r11) goto L_0x034c
            if (r4 == 0) goto L_0x034c
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            r12 = 0
            int r11 = r11.addTrack(r4, r12)     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            boolean r12 = r4.containsKey(r7)     // Catch:{ Exception -> 0x033a, all -> 0x0325 }
            if (r12 == 0) goto L_0x0323
            int r12 = r4.getInteger(r7)     // Catch:{ Exception -> 0x033a, all -> 0x0325 }
            r14 = 1
            if (r12 != r14) goto L_0x0323
            java.nio.ByteBuffer r12 = r4.getByteBuffer(r2)     // Catch:{ Exception -> 0x033a, all -> 0x0325 }
            java.nio.ByteBuffer r14 = r4.getByteBuffer(r8)     // Catch:{ Exception -> 0x033a, all -> 0x0325 }
            int r42 = r12.limit()     // Catch:{ Exception -> 0x033a, all -> 0x0325 }
            int r47 = r14.limit()     // Catch:{ Exception -> 0x033a, all -> 0x0325 }
            int r27 = r42 + r47
            r14 = r11
            goto L_0x034c
        L_0x0323:
            r14 = r11
            goto L_0x034c
        L_0x0325:
            r0 = move-exception
            r13 = r94
            r51 = r99
            r26 = r101
            r1 = r0
            r14 = r11
            r11 = r20
            r8 = r31
            r10 = r37
            r2 = r44
            r12 = r46
            goto L_0x1df2
        L_0x033a:
            r0 = move-exception
            r25 = r95
            r24 = r1
            r49 = r5
            r18 = r11
            r11 = r20
            r12 = r46
            r4 = r103
            r1 = r0
            goto L_0x085b
        L_0x034c:
            r49 = r5
            r47 = r7
            r5 = r20
            r4 = r46
            r20 = r2
            r2 = r18
            r18 = r14
            r14 = r92
            goto L_0x05a1
        L_0x035e:
            r0 = move-exception
            r46 = r12
            r13 = r94
            r51 = r99
            r26 = r101
            r1 = r0
            r11 = r20
            r8 = r31
            r10 = r37
            r2 = r44
            goto L_0x1df2
        L_0x0372:
            r0 = move-exception
            r46 = r12
            r25 = r95
            r24 = r1
            r49 = r5
            r18 = r14
            r11 = r20
            r4 = r103
            r1 = r0
            goto L_0x085b
        L_0x0384:
            r46 = r12
            if (r3 < 0) goto L_0x06d8
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x06c2, all -> 0x06aa }
            r11 = 21
            if (r4 >= r11) goto L_0x03b7
            r4 = r10[r3]     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            goto L_0x03bb
        L_0x0391:
            r0 = move-exception
            r13 = r94
            r51 = r99
            r26 = r101
            r1 = r0
            r11 = r20
            r8 = r31
            r10 = r37
            r2 = r44
            r12 = r46
            goto L_0x1df2
        L_0x03a5:
            r0 = move-exception
            r25 = r95
            r24 = r1
            r49 = r5
            r18 = r14
            r11 = r20
            r12 = r46
            r4 = r103
            r1 = r0
            goto L_0x085b
        L_0x03b7:
            java.nio.ByteBuffer r4 = r1.getOutputBuffer(r3)     // Catch:{ Exception -> 0x06c2, all -> 0x06aa }
        L_0x03bb:
            if (r4 == 0) goto L_0x0682
            int r11 = r9.size     // Catch:{ Exception -> 0x06c2, all -> 0x06aa }
            r12 = 1
            if (r11 <= r12) goto L_0x057f
            int r11 = r9.flags     // Catch:{ Exception -> 0x056b, all -> 0x0555 }
            r12 = 2
            r11 = r11 & r12
            if (r11 != 0) goto L_0x046d
            if (r27 == 0) goto L_0x03dc
            int r11 = r9.flags     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            r12 = 1
            r11 = r11 & r12
            if (r11 == 0) goto L_0x03dc
            int r11 = r9.offset     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            int r11 = r11 + r27
            r9.offset = r11     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            int r11 = r9.size     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            int r11 = r11 - r27
            r9.size = r11     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
        L_0x03dc:
            if (r18 == 0) goto L_0x043f
            int r11 = r9.flags     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            r12 = 1
            r11 = r11 & r12
            if (r11 == 0) goto L_0x043f
            int r11 = r9.size     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            r12 = 100
            if (r11 <= r12) goto L_0x0438
            int r11 = r9.offset     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            r4.position(r11)     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            byte[] r11 = new byte[r12]     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            r4.get(r11)     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            r30 = 0
            r42 = 0
            r12 = r42
        L_0x03fa:
            r47 = r7
            int r7 = r11.length     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            int r7 = r7 + -4
            if (r12 >= r7) goto L_0x0435
            byte r7 = r11[r12]     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            if (r7 != 0) goto L_0x042c
            int r7 = r12 + 1
            byte r7 = r11[r7]     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            if (r7 != 0) goto L_0x042c
            int r7 = r12 + 2
            byte r7 = r11[r7]     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            if (r7 != 0) goto L_0x042c
            int r7 = r12 + 3
            byte r7 = r11[r7]     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            r48 = r10
            r10 = 1
            if (r7 != r10) goto L_0x042e
            int r7 = r30 + 1
            if (r7 <= r10) goto L_0x0429
            int r10 = r9.offset     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            int r10 = r10 + r12
            r9.offset = r10     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            int r10 = r9.size     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            int r10 = r10 - r12
            r9.size = r10     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            goto L_0x043c
        L_0x0429:
            r30 = r7
            goto L_0x042e
        L_0x042c:
            r48 = r10
        L_0x042e:
            int r12 = r12 + 1
            r7 = r47
            r10 = r48
            goto L_0x03fa
        L_0x0435:
            r48 = r10
            goto L_0x043c
        L_0x0438:
            r47 = r7
            r48 = r10
        L_0x043c:
            r18 = 0
            goto L_0x0443
        L_0x043f:
            r47 = r7
            r48 = r10
        L_0x0443:
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            r10 = 1
            long r11 = r7.writeSampleData(r14, r4, r9, r10)     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            r10 = r11
            r39 = 0
            int r7 = (r10 > r39 ? 1 : (r10 == r39 ? 0 : -1))
            if (r7 == 0) goto L_0x045d
            org.telegram.messenger.MediaController$VideoConvertorListener r7 = r15.callback     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            if (r7 == 0) goto L_0x045d
            float r12 = (float) r5     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
            float r12 = r12 / r22
            float r12 = r12 / r23
            r7.didWriteData(r10, r12)     // Catch:{ Exception -> 0x03a5, all -> 0x0391 }
        L_0x045d:
            r30 = r4
            r49 = r5
            r5 = r20
            r4 = r46
            r20 = r2
            r2 = r18
            r18 = r14
            goto L_0x0591
        L_0x046d:
            r47 = r7
            r48 = r10
            r7 = -5
            if (r14 != r7) goto L_0x054a
            int r10 = r9.size     // Catch:{ Exception -> 0x056b, all -> 0x0555 }
            byte[] r10 = new byte[r10]     // Catch:{ Exception -> 0x056b, all -> 0x0555 }
            int r11 = r9.offset     // Catch:{ Exception -> 0x056b, all -> 0x0555 }
            int r12 = r9.size     // Catch:{ Exception -> 0x056b, all -> 0x0555 }
            int r11 = r11 + r12
            r4.limit(r11)     // Catch:{ Exception -> 0x056b, all -> 0x0555 }
            int r11 = r9.offset     // Catch:{ Exception -> 0x056b, all -> 0x0555 }
            r4.position(r11)     // Catch:{ Exception -> 0x056b, all -> 0x0555 }
            r4.get(r10)     // Catch:{ Exception -> 0x056b, all -> 0x0555 }
            r11 = 0
            r12 = 0
            int r7 = r9.size     // Catch:{ Exception -> 0x056b, all -> 0x0555 }
            r30 = r4
            r4 = 1
            int r7 = r7 - r4
        L_0x0490:
            if (r7 < 0) goto L_0x0504
            r4 = 3
            if (r7 <= r4) goto L_0x0501
            byte r4 = r10[r7]     // Catch:{ Exception -> 0x04ef, all -> 0x0391 }
            r49 = r5
            r5 = 1
            if (r4 != r5) goto L_0x04e9
            int r4 = r7 + -1
            byte r4 = r10[r4]     // Catch:{ Exception -> 0x04d9, all -> 0x0391 }
            if (r4 != 0) goto L_0x04e9
            int r4 = r7 + -2
            byte r4 = r10[r4]     // Catch:{ Exception -> 0x04d9, all -> 0x0391 }
            if (r4 != 0) goto L_0x04e9
            int r4 = r7 + -3
            byte r4 = r10[r4]     // Catch:{ Exception -> 0x04d9, all -> 0x0391 }
            if (r4 != 0) goto L_0x04e9
            int r4 = r7 + -3
            java.nio.ByteBuffer r4 = java.nio.ByteBuffer.allocate(r4)     // Catch:{ Exception -> 0x04d9, all -> 0x0391 }
            r11 = r4
            int r4 = r9.size     // Catch:{ Exception -> 0x04d9, all -> 0x0391 }
            int r5 = r7 + -3
            int r4 = r4 - r5
            java.nio.ByteBuffer r4 = java.nio.ByteBuffer.allocate(r4)     // Catch:{ Exception -> 0x04d9, all -> 0x0391 }
            r12 = r4
            int r4 = r7 + -3
            r5 = 0
            java.nio.ByteBuffer r4 = r11.put(r10, r5, r4)     // Catch:{ Exception -> 0x04d9, all -> 0x0391 }
            r4.position(r5)     // Catch:{ Exception -> 0x04d9, all -> 0x0391 }
            int r4 = r7 + -3
            int r5 = r9.size     // Catch:{ Exception -> 0x04d9, all -> 0x0391 }
            int r6 = r7 + -3
            int r5 = r5 - r6
            java.nio.ByteBuffer r4 = r12.put(r10, r4, r5)     // Catch:{ Exception -> 0x04d9, all -> 0x0391 }
            r5 = 0
            r4.position(r5)     // Catch:{ Exception -> 0x04d9, all -> 0x0391 }
            goto L_0x0506
        L_0x04d9:
            r0 = move-exception
            r25 = r95
            r4 = r103
            r24 = r1
            r18 = r14
            r11 = r20
            r12 = r46
            r1 = r0
            goto L_0x085b
        L_0x04e9:
            int r7 = r7 + -1
            r5 = r49
            r4 = 1
            goto L_0x0490
        L_0x04ef:
            r0 = move-exception
            r49 = r5
            r25 = r95
            r4 = r103
            r24 = r1
            r18 = r14
            r11 = r20
            r12 = r46
            r1 = r0
            goto L_0x085b
        L_0x0501:
            r49 = r5
            goto L_0x0506
        L_0x0504:
            r49 = r5
        L_0x0506:
            r5 = r20
            r4 = r46
            android.media.MediaFormat r6 = android.media.MediaFormat.createVideoFormat(r13, r4, r5)     // Catch:{ Exception -> 0x053c, all -> 0x052a }
            if (r11 == 0) goto L_0x0518
            if (r12 == 0) goto L_0x0518
            r6.setByteBuffer(r2, r11)     // Catch:{ Exception -> 0x053c, all -> 0x052a }
            r6.setByteBuffer(r8, r12)     // Catch:{ Exception -> 0x053c, all -> 0x052a }
        L_0x0518:
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ Exception -> 0x053c, all -> 0x052a }
            r20 = r2
            r2 = 0
            int r7 = r7.addTrack(r6, r2)     // Catch:{ Exception -> 0x053c, all -> 0x052a }
            r2 = r7
            r84 = r18
            r18 = r2
            r2 = r84
            goto L_0x0591
        L_0x052a:
            r0 = move-exception
            r13 = r94
            r51 = r99
            r26 = r101
            r1 = r0
            r12 = r4
            r11 = r5
            r8 = r31
            r10 = r37
            r2 = r44
            goto L_0x1df2
        L_0x053c:
            r0 = move-exception
            r25 = r95
            r24 = r1
            r12 = r4
            r11 = r5
            r18 = r14
            r4 = r103
            r1 = r0
            goto L_0x085b
        L_0x054a:
            r30 = r4
            r49 = r5
            r5 = r20
            r4 = r46
            r20 = r2
            goto L_0x058d
        L_0x0555:
            r0 = move-exception
            r5 = r20
            r4 = r46
            r13 = r94
            r51 = r99
            r26 = r101
            r1 = r0
            r12 = r4
            r11 = r5
            r8 = r31
            r10 = r37
            r2 = r44
            goto L_0x1df2
        L_0x056b:
            r0 = move-exception
            r49 = r5
            r5 = r20
            r4 = r46
            r25 = r95
            r24 = r1
            r12 = r4
            r11 = r5
            r18 = r14
            r4 = r103
            r1 = r0
            goto L_0x085b
        L_0x057f:
            r30 = r4
            r49 = r5
            r47 = r7
            r48 = r10
            r5 = r20
            r4 = r46
            r20 = r2
        L_0x058d:
            r2 = r18
            r18 = r14
        L_0x0591:
            int r6 = r9.flags     // Catch:{ Exception -> 0x064b, all -> 0x062f }
            r6 = r6 & 4
            if (r6 == 0) goto L_0x0599
            r6 = 1
            goto L_0x059a
        L_0x0599:
            r6 = 0
        L_0x059a:
            r14 = r6
            r6 = 0
            r1.releaseOutputBuffer(r3, r6)     // Catch:{ Exception -> 0x064b, all -> 0x062f }
            r10 = r48
        L_0x05a1:
            r6 = -1
            if (r3 == r6) goto L_0x05ba
            r12 = r4
            r11 = r5
            r92 = r14
            r14 = r18
            r3 = r25
            r7 = r47
            r5 = r49
            r18 = r2
            r2 = r20
            r20 = r9
            r9 = r95
            goto L_0x021d
        L_0x05ba:
            if (r41 != 0) goto L_0x065f
            r26.drawImage()     // Catch:{ Exception -> 0x064b, all -> 0x062f }
            r6 = r25
            float r7 = (float) r6
            r11 = 1106247680(0x41var_, float:30.0)
            float r7 = r7 / r11
            float r7 = r7 * r22
            float r7 = r7 * r22
            float r7 = r7 * r22
            long r11 = (long) r7
            r7 = r95
            r7.setPresentationTime(r11)     // Catch:{ Exception -> 0x061d, all -> 0x062f }
            r7.swapBuffers()     // Catch:{ Exception -> 0x061d, all -> 0x062f }
            int r6 = r6 + 1
            r92 = r2
            float r2 = (float) r6
            r46 = r4
            r95 = r5
            r25 = r6
            r4 = r103
            float r6 = (float) r4
            float r6 = r6 / r22
            r30 = 1106247680(0x41var_, float:30.0)
            float r6 = r6 * r30
            int r2 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r2 < 0) goto L_0x061a
            r41 = 1
            r2 = 0
            r1.signalEndOfInputStream()     // Catch:{ Exception -> 0x060e, all -> 0x05f8 }
            r36 = r2
            r3 = r25
            goto L_0x066b
        L_0x05f8:
            r0 = move-exception
            r13 = r94
            r11 = r95
            r51 = r99
            r26 = r101
            r1 = r0
            r14 = r18
            r8 = r31
            r10 = r37
            r2 = r44
            r12 = r46
            goto L_0x1df2
        L_0x060e:
            r0 = move-exception
            r11 = r95
            r24 = r1
            r25 = r7
            r12 = r46
            r1 = r0
            goto L_0x085b
        L_0x061a:
            r3 = r25
            goto L_0x066b
        L_0x061d:
            r0 = move-exception
            r46 = r4
            r95 = r5
            r4 = r103
            r11 = r95
            r24 = r1
            r25 = r7
            r12 = r46
            r1 = r0
            goto L_0x085b
        L_0x062f:
            r0 = move-exception
            r46 = r4
            r95 = r5
            r4 = r103
            r13 = r94
            r11 = r95
            r51 = r99
            r26 = r101
            r1 = r0
            r14 = r18
            r8 = r31
            r10 = r37
            r2 = r44
            r12 = r46
            goto L_0x1df2
        L_0x064b:
            r0 = move-exception
            r7 = r95
            r46 = r4
            r95 = r5
            r4 = r103
            r11 = r95
            r24 = r1
            r25 = r7
            r12 = r46
            r1 = r0
            goto L_0x085b
        L_0x065f:
            r7 = r95
            r92 = r2
            r46 = r4
            r95 = r5
            r4 = r103
            r3 = r25
        L_0x066b:
            r11 = r95
            r2 = r20
            r12 = r46
            r5 = r49
            r20 = r9
            r9 = r7
            r7 = r47
            r84 = r18
            r18 = r92
            r92 = r14
            r14 = r84
            goto L_0x021d
        L_0x0682:
            r7 = r95
            r30 = r4
            r49 = r5
            r48 = r10
            r95 = r20
            r4 = r103
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x070e, all -> 0x06fa }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x070e, all -> 0x06fa }
            r6.<init>()     // Catch:{ Exception -> 0x070e, all -> 0x06fa }
            java.lang.String r8 = "encoderOutputBuffer "
            r6.append(r8)     // Catch:{ Exception -> 0x070e, all -> 0x06fa }
            r6.append(r3)     // Catch:{ Exception -> 0x070e, all -> 0x06fa }
            java.lang.String r8 = " was null"
            r6.append(r8)     // Catch:{ Exception -> 0x070e, all -> 0x06fa }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x070e, all -> 0x06fa }
            r2.<init>(r6)     // Catch:{ Exception -> 0x070e, all -> 0x06fa }
            throw r2     // Catch:{ Exception -> 0x070e, all -> 0x06fa }
        L_0x06aa:
            r0 = move-exception
            r4 = r103
            r95 = r20
            r13 = r94
            r11 = r95
            r51 = r99
            r26 = r101
            r1 = r0
            r8 = r31
            r10 = r37
            r2 = r44
            r12 = r46
            goto L_0x1df2
        L_0x06c2:
            r0 = move-exception
            r7 = r95
            r49 = r5
            r95 = r20
            r4 = r103
            r11 = r95
            r24 = r1
            r25 = r7
            r18 = r14
            r12 = r46
            r1 = r0
            goto L_0x085b
        L_0x06d8:
            r7 = r95
            r49 = r5
            r48 = r10
            r95 = r20
            r4 = r103
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x070e, all -> 0x06fa }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x070e, all -> 0x06fa }
            r6.<init>()     // Catch:{ Exception -> 0x070e, all -> 0x06fa }
            java.lang.String r8 = "unexpected result from encoder.dequeueOutputBuffer: "
            r6.append(r8)     // Catch:{ Exception -> 0x070e, all -> 0x06fa }
            r6.append(r3)     // Catch:{ Exception -> 0x070e, all -> 0x06fa }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x070e, all -> 0x06fa }
            r2.<init>(r6)     // Catch:{ Exception -> 0x070e, all -> 0x06fa }
            throw r2     // Catch:{ Exception -> 0x070e, all -> 0x06fa }
        L_0x06fa:
            r0 = move-exception
            r13 = r94
            r11 = r95
            r51 = r99
            r26 = r101
            r1 = r0
            r8 = r31
            r10 = r37
            r2 = r44
            r12 = r46
            goto L_0x1df2
        L_0x070e:
            r0 = move-exception
            r11 = r95
            r24 = r1
            r25 = r7
            r18 = r14
            r12 = r46
            r1 = r0
            goto L_0x085b
        L_0x071c:
            r0 = move-exception
            r7 = r95
            r49 = r5
            r95 = r11
            r46 = r12
            r4 = r103
            r24 = r1
            r25 = r7
            r18 = r14
            r1 = r0
            goto L_0x085b
        L_0x0730:
            r0 = move-exception
            r4 = r103
            r95 = r11
            r46 = r12
            r13 = r94
            r51 = r99
            r26 = r101
            r1 = r0
            r8 = r31
            r10 = r37
            r2 = r44
            goto L_0x1df2
        L_0x0746:
            r0 = move-exception
            r49 = r5
            r7 = r9
            r95 = r11
            r46 = r12
            r9 = r20
            r4 = r103
            r24 = r1
            r25 = r7
            r18 = r14
            r1 = r0
            goto L_0x085b
        L_0x075b:
            r0 = move-exception
            r4 = r3
            r95 = r11
            r46 = r12
            r13 = r94
            r51 = r99
            r26 = r101
            r1 = r0
            r14 = r25
            r8 = r31
            r10 = r37
            r2 = r44
            goto L_0x1df2
        L_0x0772:
            r0 = move-exception
            r49 = r5
            r7 = r9
            r95 = r11
            r46 = r12
            r9 = r20
            r4 = r3
            r24 = r1
            r18 = r25
            r1 = r0
            r25 = r7
            goto L_0x085b
        L_0x0786:
            r49 = r5
            r7 = r9
            r95 = r11
            r46 = r12
            r9 = r20
            r4 = r3
            r18 = r25
            r14 = r31
            r3 = r37
            r10 = r44
            r25 = r7
            r7 = r94
            goto L_0x089c
        L_0x079e:
            r0 = move-exception
            r49 = r5
            r7 = r9
            r95 = r11
            r46 = r12
            r9 = r20
            r4 = r3
            r24 = r1
            r25 = r7
            r1 = r0
            goto L_0x085b
        L_0x07b0:
            r0 = move-exception
            r4 = r3
            r95 = r11
            r46 = r12
            r13 = r94
            r51 = r99
            r26 = r101
            r1 = r0
            r14 = r18
            r8 = r31
            r10 = r37
            r2 = r44
            goto L_0x1df2
        L_0x07c7:
            r0 = move-exception
            r49 = r5
            r95 = r11
            r46 = r12
            r9 = r20
            r4 = r3
            r24 = r1
            r1 = r0
            goto L_0x085b
        L_0x07d6:
            r0 = move-exception
            r4 = r3
            r44 = r10
            r95 = r11
            r46 = r12
            r13 = r94
            r51 = r99
            r26 = r101
            r1 = r0
            r14 = r18
            r8 = r31
            r10 = r37
            r2 = r44
            goto L_0x0806
        L_0x07ee:
            r0 = move-exception
            goto L_0x080b
        L_0x07f0:
            r0 = move-exception
            r4 = r3
            r44 = r10
            r95 = r11
            r46 = r12
            r13 = r94
            r51 = r99
            r26 = r101
            r10 = r1
            r14 = r18
            r8 = r31
            r2 = r44
            r1 = r0
        L_0x0806:
            goto L_0x1df2
        L_0x0808:
            r0 = move-exception
            r37 = r1
        L_0x080b:
            r49 = r5
            r44 = r10
            r95 = r11
            r46 = r12
            r9 = r20
            r4 = r3
            r1 = r0
            goto L_0x085b
        L_0x0818:
            r0 = move-exception
            r4 = r3
            r44 = r10
            r46 = r12
            r13 = r94
            r51 = r99
            r26 = r101
            r10 = r1
            r14 = r18
            r8 = r31
            r2 = r44
            r1 = r0
            goto L_0x1df2
        L_0x082e:
            r0 = move-exception
            r37 = r1
            r49 = r5
            r44 = r10
            r46 = r12
            r9 = r20
            r4 = r3
            r1 = r0
            goto L_0x085b
        L_0x083c:
            r0 = move-exception
            r4 = r3
            r44 = r10
            r13 = r94
            r51 = r99
            r26 = r101
            r10 = r1
            r14 = r18
            r8 = r31
            r2 = r44
            r1 = r0
            goto L_0x1df2
        L_0x0850:
            r0 = move-exception
            r37 = r1
            r49 = r5
            r44 = r10
            r9 = r20
            r4 = r3
            r1 = r0
        L_0x085b:
            boolean r2 = r1 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x08f6 }
            if (r2 == 0) goto L_0x0863
            if (r106 != 0) goto L_0x0863
            r17 = 1
        L_0x0863:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x08f6 }
            r2.<init>()     // Catch:{ all -> 0x08f6 }
            r14 = r31
            r2.append(r14)     // Catch:{ all -> 0x08e7 }
            r10 = r44
            r2.append(r10)     // Catch:{ all -> 0x08d7 }
            r3 = r37
            r2.append(r3)     // Catch:{ all -> 0x08c8 }
            r7 = r94
            r2.append(r7)     // Catch:{ all -> 0x08ba }
            java.lang.String r6 = " size: "
            r2.append(r6)     // Catch:{ all -> 0x08ba }
            r2.append(r11)     // Catch:{ all -> 0x08ba }
            java.lang.String r6 = "x"
            r2.append(r6)     // Catch:{ all -> 0x08ba }
            r2.append(r12)     // Catch:{ all -> 0x08ba }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x08ba }
            org.telegram.messenger.FileLog.e((java.lang.String) r2)     // Catch:{ all -> 0x08ba }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x08ba }
            r2 = 1
            r16 = r2
            r1 = r24
        L_0x089c:
            if (r26 == 0) goto L_0x08a1
            r26.release()     // Catch:{ all -> 0x08ba }
        L_0x08a1:
            if (r25 == 0) goto L_0x08a6
            r25.release()     // Catch:{ all -> 0x08ba }
        L_0x08a6:
            if (r1 == 0) goto L_0x08ae
            r1.stop()     // Catch:{ all -> 0x08ba }
            r1.release()     // Catch:{ all -> 0x08ba }
        L_0x08ae:
            r85.checkConversionCanceled()     // Catch:{ all -> 0x08ba }
            r51 = r99
            r26 = r101
            r13 = r7
            r1 = r18
            goto L_0x1dab
        L_0x08ba:
            r0 = move-exception
            r51 = r99
            r26 = r101
            r1 = r0
            r13 = r7
            r2 = r10
            r8 = r14
            r14 = r18
            r10 = r3
            goto L_0x1df2
        L_0x08c8:
            r0 = move-exception
            r13 = r94
            r51 = r99
            r26 = r101
            r1 = r0
            r2 = r10
            r8 = r14
            r14 = r18
            r10 = r3
            goto L_0x1df2
        L_0x08d7:
            r0 = move-exception
            r13 = r94
            r51 = r99
            r26 = r101
            r1 = r0
            r2 = r10
            r8 = r14
            r14 = r18
            r10 = r37
            goto L_0x1df2
        L_0x08e7:
            r0 = move-exception
            r10 = r44
            r13 = r94
            r51 = r99
            r26 = r101
            r1 = r0
            r2 = r10
            r8 = r14
            r14 = r18
            goto L_0x0905
        L_0x08f6:
            r0 = move-exception
            r10 = r44
            r13 = r94
            r51 = r99
            r26 = r101
            r1 = r0
            r2 = r10
            r14 = r18
            r8 = r31
        L_0x0905:
            r10 = r37
            goto L_0x1df2
        L_0x0909:
            r49 = r5
            r47 = r7
            r7 = r9
            r9 = r20
            r14 = r31
            r13 = r37
            r30 = 100
            r20 = r2
            r4 = r3
            r3 = r1
            android.media.MediaExtractor r1 = new android.media.MediaExtractor     // Catch:{ all -> 0x1dd7 }
            r1.<init>()     // Catch:{ all -> 0x1dd7 }
            r15.extractor = r1     // Catch:{ all -> 0x1dd7 }
            r6 = r86
            r2 = -5
            r1.setDataSource(r6)     // Catch:{ all -> 0x1dd7 }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ all -> 0x1dd7 }
            r2 = 0
            int r1 = org.telegram.messenger.MediaController.findTrack(r1, r2)     // Catch:{ all -> 0x1dd7 }
            r2 = r1
            r1 = -1
            if (r10 == r1) goto L_0x0959
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ all -> 0x094b }
            r37 = r3
            r3 = 1
            int r1 = org.telegram.messenger.MediaController.findTrack(r1, r3)     // Catch:{ all -> 0x093c }
            goto L_0x095c
        L_0x093c:
            r0 = move-exception
            r51 = r99
            r26 = r101
            r1 = r0
            r13 = r7
            r2 = r10
            r8 = r14
            r14 = r18
            r10 = r37
            goto L_0x1df2
        L_0x094b:
            r0 = move-exception
            r51 = r99
            r26 = r101
            r1 = r0
            r13 = r7
            r2 = r10
            r8 = r14
            r14 = r18
            r10 = r3
            goto L_0x1df2
        L_0x0959:
            r37 = r3
            r1 = -1
        L_0x095c:
            r3 = r1
            r1 = 0
            java.lang.String r11 = "mime"
            if (r2 < 0) goto L_0x098a
            r31 = r1
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ all -> 0x0979 }
            android.media.MediaFormat r1 = r1.getTrackFormat(r2)     // Catch:{ all -> 0x0979 }
            java.lang.String r1 = r1.getString(r11)     // Catch:{ all -> 0x0979 }
            boolean r1 = r1.equals(r13)     // Catch:{ all -> 0x0979 }
            if (r1 != 0) goto L_0x098c
            r1 = 1
            r31 = r1
            goto L_0x098c
        L_0x0979:
            r0 = move-exception
            r11 = r93
            r51 = r99
            r26 = r101
            r1 = r0
            r13 = r7
            r2 = r10
            r8 = r14
            r14 = r18
            r10 = r37
            goto L_0x1df2
        L_0x098a:
            r31 = r1
        L_0x098c:
            if (r105 != 0) goto L_0x0a0c
            if (r31 == 0) goto L_0x099f
            r51 = r3
            r52 = r9
            r10 = r13
            r12 = r37
            r41 = r49
            r13 = r2
            r37 = r14
            r14 = r7
            goto L_0x0a19
        L_0x099f:
            android.media.MediaExtractor r8 = r15.extractor     // Catch:{ all -> 0x09f6 }
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ all -> 0x09f6 }
            r1 = -1
            if (r10 == r1) goto L_0x09a9
            r43 = 1
            goto L_0x09ab
        L_0x09a9:
            r43 = 0
        L_0x09ab:
            r13 = r37
            r1 = r85
            r19 = r13
            r37 = r14
            r14 = r111
            r13 = r2
            r2 = r8
            r8 = r3
            r3 = r11
            r4 = r9
            r11 = r6
            r41 = r49
            r5 = r97
            r14 = r96
            r51 = r8
            r7 = r99
            r14 = r94
            r52 = r9
            r9 = r103
            r11 = r87
            r12 = r43
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ all -> 0x09e1 }
            r12 = r92
            r11 = r93
            r10 = r95
            r51 = r99
            r26 = r101
            r13 = r14
            r1 = r18
            goto L_0x1dab
        L_0x09e1:
            r0 = move-exception
            r12 = r92
            r11 = r93
            r2 = r95
            r51 = r99
            r26 = r101
            r1 = r0
            r13 = r14
            r14 = r18
            r10 = r19
            r8 = r37
            goto L_0x1df2
        L_0x09f6:
            r0 = move-exception
            r19 = r37
            r12 = r92
            r11 = r93
            r2 = r95
            r51 = r99
            r26 = r101
            r1 = r0
            r13 = r7
            r8 = r14
            r14 = r18
            r10 = r19
            goto L_0x1df2
        L_0x0a0c:
            r51 = r3
            r52 = r9
            r10 = r13
            r12 = r37
            r41 = r49
            r13 = r2
            r37 = r14
            r14 = r7
        L_0x0a19:
            r44 = 0
            r45 = 0
            r46 = 1
            if (r13 < 0) goto L_0x1d6a
            r1 = 0
            r48 = -1
            r50 = 0
            r53 = 0
            r54 = 0
            r55 = 0
            r56 = -5
            r57 = 0
            r59 = -2147483648(0xfffffffvar_, double:NaN)
            r2 = 1000(0x3e8, float:1.401E-42)
            int r2 = r2 / r14
            int r2 = r2 * 1000
            long r6 = (long) r2     // Catch:{ Exception -> 0x1cc3, all -> 0x1caf }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x1cc3, all -> 0x1caf }
            r2.selectTrack(r13)     // Catch:{ Exception -> 0x1cc3, all -> 0x1caf }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x1cc3, all -> 0x1caf }
            android.media.MediaFormat r2 = r2.getTrackFormat(r13)     // Catch:{ Exception -> 0x1cc3, all -> 0x1caf }
            r9 = r2
            r2 = 0
            int r4 = (r101 > r2 ? 1 : (r101 == r2 ? 0 : -1))
            if (r4 < 0) goto L_0x0a66
            r2 = 1157234688(0x44fa0000, float:2000.0)
            int r2 = (r23 > r2 ? 1 : (r23 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0a55
            r2 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x0a63
        L_0x0a55:
            r2 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r2 = (r23 > r2 ? 1 : (r23 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0a60
            r2 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x0a63
        L_0x0a60:
            r2 = 1560000(0x17cdc0, float:2.186026E-39)
        L_0x0a63:
            r3 = 0
            goto L_0x0a72
        L_0x0a66:
            if (r95 > 0) goto L_0x0a6e
            r2 = 921600(0xe1000, float:1.291437E-39)
            r3 = r101
            goto L_0x0a72
        L_0x0a6e:
            r2 = r95
            r3 = r101
        L_0x0a72:
            r5 = r96
            if (r5 <= 0) goto L_0x0aa8
            int r61 = java.lang.Math.min(r5, r2)     // Catch:{ Exception -> 0x0a8f, all -> 0x0a7d }
            r2 = r61
            goto L_0x0aa8
        L_0x0a7d:
            r0 = move-exception
            r11 = r93
            r51 = r99
            r1 = r0
            r26 = r3
            r10 = r12
            r13 = r14
            r14 = r18
            r8 = r37
            r12 = r92
            goto L_0x1df2
        L_0x0a8f:
            r0 = move-exception
            r36 = r13
            r13 = r14
            r56 = r26
            r30 = r27
            r19 = r37
            r5 = r41
            r14 = r52
            r26 = r3
            r37 = r12
            r3 = r51
            r51 = r99
            r4 = r0
            goto L_0x1cdf
        L_0x0aa8:
            r39 = 0
            int r61 = (r3 > r39 ? 1 : (r3 == r39 ? 0 : -1))
            if (r61 < 0) goto L_0x0ab0
            r3 = -1
        L_0x0ab0:
            int r61 = (r3 > r39 ? 1 : (r3 == r39 ? 0 : -1))
            if (r61 < 0) goto L_0x0adc
            r61 = r1
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0ac1, all -> 0x0a7d }
            r5 = 0
            r1.seekTo(r3, r5)     // Catch:{ Exception -> 0x0ac1, all -> 0x0a7d }
            r101 = r3
            r5 = 0
            goto L_0x0b25
        L_0x0ac1:
            r0 = move-exception
            r36 = r13
            r13 = r14
            r56 = r26
            r30 = r27
            r19 = r37
            r5 = r41
            r14 = r52
            r1 = r61
            r26 = r3
            r37 = r12
            r3 = r51
            r51 = r99
            r4 = r0
            goto L_0x1cdf
        L_0x0adc:
            r61 = r1
            r101 = r3
            r39 = 0
            r3 = r97
            int r1 = (r3 > r39 ? 1 : (r3 == r39 ? 0 : -1))
            if (r1 <= 0) goto L_0x0b1d
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0b02, all -> 0x0af0 }
            r5 = 0
            r1.seekTo(r3, r5)     // Catch:{ Exception -> 0x0b02, all -> 0x0af0 }
            r5 = 0
            goto L_0x0b25
        L_0x0af0:
            r0 = move-exception
            r11 = r93
            r51 = r99
            r26 = r101
            r1 = r0
            r10 = r12
            r13 = r14
            r14 = r18
            r8 = r37
            r12 = r92
            goto L_0x1df2
        L_0x0b02:
            r0 = move-exception
            r4 = r0
            r36 = r13
            r13 = r14
            r56 = r26
            r30 = r27
            r19 = r37
            r5 = r41
            r3 = r51
            r14 = r52
            r1 = r61
            r51 = r99
            r26 = r101
            r37 = r12
            goto L_0x1cdf
        L_0x0b1d:
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x1CLASSNAME, all -> 0x1c7e }
            r3 = 0
            r5 = 0
            r1.seekTo(r3, r5)     // Catch:{ Exception -> 0x1CLASSNAME, all -> 0x1c7e }
        L_0x0b25:
            r4 = r111
            if (r4 == 0) goto L_0x0b79
            r1 = 90
            r3 = r88
            r84 = r37
            r37 = r12
            r12 = r84
            if (r3 == r1) goto L_0x0b44
            r1 = 270(0x10e, float:3.78E-43)
            if (r3 != r1) goto L_0x0b3a
            goto L_0x0b44
        L_0x0b3a:
            int r1 = r4.transformWidth     // Catch:{ Exception -> 0x0b60, all -> 0x0b4e }
            int r5 = r4.transformHeight     // Catch:{ Exception -> 0x0b60, all -> 0x0b4e }
            r84 = r5
            r5 = r1
            r1 = r84
            goto L_0x0b8a
        L_0x0b44:
            int r1 = r4.transformHeight     // Catch:{ Exception -> 0x0b60, all -> 0x0b4e }
            int r5 = r4.transformWidth     // Catch:{ Exception -> 0x0b60, all -> 0x0b4e }
            r84 = r5
            r5 = r1
            r1 = r84
            goto L_0x0b8a
        L_0x0b4e:
            r0 = move-exception
            r11 = r93
            r51 = r99
            r26 = r101
            r1 = r0
            r8 = r12
            r13 = r14
            r14 = r18
            r10 = r37
            r12 = r92
            goto L_0x1df2
        L_0x0b60:
            r0 = move-exception
            r4 = r0
            r19 = r12
            r36 = r13
            r13 = r14
            r56 = r26
            r30 = r27
            r5 = r41
            r3 = r51
            r14 = r52
            r1 = r61
            r51 = r99
            r26 = r101
            goto L_0x1cdf
        L_0x0b79:
            r3 = r88
            r84 = r37
            r37 = r12
            r12 = r84
            r1 = r92
            r5 = r93
            r84 = r5
            r5 = r1
            r1 = r84
        L_0x0b8a:
            boolean r62 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1CLASSNAME, all -> 0x1c4e }
            if (r62 == 0) goto L_0x0baa
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b60, all -> 0x0b4e }
            r3.<init>()     // Catch:{ Exception -> 0x0b60, all -> 0x0b4e }
            java.lang.String r4 = "create encoder with w = "
            r3.append(r4)     // Catch:{ Exception -> 0x0b60, all -> 0x0b4e }
            r3.append(r5)     // Catch:{ Exception -> 0x0b60, all -> 0x0b4e }
            java.lang.String r4 = " h = "
            r3.append(r4)     // Catch:{ Exception -> 0x0b60, all -> 0x0b4e }
            r3.append(r1)     // Catch:{ Exception -> 0x0b60, all -> 0x0b4e }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0b60, all -> 0x0b4e }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x0b60, all -> 0x0b4e }
        L_0x0baa:
            android.media.MediaFormat r3 = android.media.MediaFormat.createVideoFormat(r10, r5, r1)     // Catch:{ Exception -> 0x1CLASSNAME, all -> 0x1c4e }
            r4 = r3
            java.lang.String r3 = "color-format"
            r63 = r6
            r6 = 2130708361(0x7var_, float:1.701803E38)
            r4.setInteger(r3, r6)     // Catch:{ Exception -> 0x1CLASSNAME, all -> 0x1c4e }
            java.lang.String r3 = "bitrate"
            r4.setInteger(r3, r2)     // Catch:{ Exception -> 0x1CLASSNAME, all -> 0x1c4e }
            java.lang.String r3 = "frame-rate"
            r4.setInteger(r3, r14)     // Catch:{ Exception -> 0x1CLASSNAME, all -> 0x1c4e }
            java.lang.String r3 = "i-frame-interval"
            r6 = 2
            r4.setInteger(r3, r6)     // Catch:{ Exception -> 0x1CLASSNAME, all -> 0x1c4e }
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1CLASSNAME, all -> 0x1c4e }
            r6 = 23
            if (r3 >= r6) goto L_0x0be7
            int r3 = java.lang.Math.min(r1, r5)     // Catch:{ Exception -> 0x0b60, all -> 0x0b4e }
            r6 = 480(0x1e0, float:6.73E-43)
            if (r3 > r6) goto L_0x0be7
            r3 = 921600(0xe1000, float:1.291437E-39)
            if (r2 <= r3) goto L_0x0bdf
            r2 = 921600(0xe1000, float:1.291437E-39)
        L_0x0bdf:
            java.lang.String r3 = "bitrate"
            r4.setInteger(r3, r2)     // Catch:{ Exception -> 0x0b60, all -> 0x0b4e }
            r62 = r2
            goto L_0x0be9
        L_0x0be7:
            r62 = r2
        L_0x0be9:
            android.media.MediaCodec r2 = android.media.MediaCodec.createEncoderByType(r10)     // Catch:{ Exception -> 0x1CLASSNAME, all -> 0x1c1a }
            r7 = r2
            r2 = 0
            r3 = 1
            r7.configure(r4, r2, r2, r3)     // Catch:{ Exception -> 0x1bf9, all -> 0x1c1a }
            org.telegram.messenger.video.InputSurface r2 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x1bf9, all -> 0x1c1a }
            android.view.Surface r3 = r7.createInputSurface()     // Catch:{ Exception -> 0x1bf9, all -> 0x1c1a }
            r2.<init>(r3)     // Catch:{ Exception -> 0x1bf9, all -> 0x1c1a }
            r6 = r2
            r6.makeCurrent()     // Catch:{ Exception -> 0x1bd5, all -> 0x1c1a }
            r7.start()     // Catch:{ Exception -> 0x1bd5, all -> 0x1c1a }
            java.lang.String r2 = r9.getString(r11)     // Catch:{ Exception -> 0x1bd5, all -> 0x1c1a }
            android.media.MediaCodec r2 = android.media.MediaCodec.createDecoderByType(r2)     // Catch:{ Exception -> 0x1bd5, all -> 0x1c1a }
            r3 = r2
            org.telegram.messenger.video.OutputSurface r24 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x1baf, all -> 0x1c1a }
            r25 = 0
            float r2 = (float) r14
            r61 = 0
            r72 = r1
            r1 = r24
            r73 = r20
            r20 = r2
            r2 = r107
            r65 = r101
            r74 = r3
            r3 = r25
            r25 = r4
            r4 = r108
            r75 = r5
            r19 = 0
            r5 = r109
            r76 = r6
            r6 = r111
            r95 = r7
            r77 = r47
            r7 = r92
            r78 = r8
            r8 = r93
            r79 = r9
            r9 = r88
            r80 = r10
            r10 = r20
            r19 = r12
            r12 = r11
            r11 = r61
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x1b8d, all -> 0x1b78 }
            r1 = r24
            java.lang.String r2 = createFragmentShader(r90, r91, r92, r93)     // Catch:{ Exception -> 0x1b56, all -> 0x1b78 }
            r1.changeFragmentShader(r2)     // Catch:{ Exception -> 0x1b56, all -> 0x1b78 }
            android.view.Surface r2 = r1.getSurface()     // Catch:{ Exception -> 0x1b56, all -> 0x1b78 }
            r4 = r74
            r3 = r79
            r5 = 0
            r6 = 0
            r4.configure(r3, r2, r5, r6)     // Catch:{ Exception -> 0x1b32, all -> 0x1b78 }
            r4.start()     // Catch:{ Exception -> 0x1b32, all -> 0x1b78 }
            r2 = 0
            r5 = 0
            r7 = 0
            int r8 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1b32, all -> 0x1b78 }
            r9 = 21
            if (r8 >= r9) goto L_0x0c9f
            java.nio.ByteBuffer[] r8 = r4.getInputBuffers()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
            r2 = r8
            java.nio.ByteBuffer[] r8 = r95.getOutputBuffers()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
            r5 = r8
            goto L_0x0c9f
        L_0x0CLASSNAME:
            r0 = move-exception
        L_0x0CLASSNAME:
            r12 = r92
            r11 = r93
            r51 = r99
            r1 = r0
        L_0x0CLASSNAME:
            r13 = r14
            goto L_0x1b81
        L_0x0CLASSNAME:
            r0 = move-exception
            r24 = r95
            r56 = r1
            r1 = r4
            r36 = r13
            r13 = r14
            r30 = r27
            r5 = r41
            r3 = r51
            r14 = r52
            r2 = r62
            r26 = r65
            r25 = r76
            r51 = r99
            r4 = r0
            goto L_0x1cdf
        L_0x0c9f:
            r8 = 0
            r9 = r51
            if (r9 < 0) goto L_0x0e99
            android.media.MediaExtractor r10 = r15.extractor     // Catch:{ Exception -> 0x0e7c, all -> 0x0e77 }
            android.media.MediaFormat r10 = r10.getTrackFormat(r9)     // Catch:{ Exception -> 0x0e7c, all -> 0x0e77 }
            java.lang.String r11 = r10.getString(r12)     // Catch:{ Exception -> 0x0e7c, all -> 0x0e77 }
            java.lang.String r6 = "audio/mp4a-latm"
            boolean r6 = r11.equals(r6)     // Catch:{ Exception -> 0x0e7c, all -> 0x0e77 }
            if (r6 != 0) goto L_0x0ce0
            java.lang.String r6 = r10.getString(r12)     // Catch:{ Exception -> 0x0cc5, all -> 0x0CLASSNAME }
            java.lang.String r11 = "audio/mpeg"
            boolean r6 = r6.equals(r11)     // Catch:{ Exception -> 0x0cc5, all -> 0x0CLASSNAME }
            if (r6 == 0) goto L_0x0cc3
            goto L_0x0ce0
        L_0x0cc3:
            r6 = 0
            goto L_0x0ce1
        L_0x0cc5:
            r0 = move-exception
            r24 = r95
            r56 = r1
            r1 = r4
            r3 = r9
            r36 = r13
            r13 = r14
            r30 = r27
            r5 = r41
            r14 = r52
            r2 = r62
            r26 = r65
            r25 = r76
            r51 = r99
            r4 = r0
            goto L_0x1cdf
        L_0x0ce0:
            r6 = 1
        L_0x0ce1:
            r46 = r6
            java.lang.String r6 = r10.getString(r12)     // Catch:{ Exception -> 0x0e7c, all -> 0x0e77 }
            java.lang.String r11 = "audio/unknown"
            boolean r6 = r6.equals(r11)     // Catch:{ Exception -> 0x0e7c, all -> 0x0e69 }
            if (r6 == 0) goto L_0x0cf1
            r6 = -1
            goto L_0x0cf2
        L_0x0cf1:
            r6 = r9
        L_0x0cf2:
            if (r6 < 0) goto L_0x0e52
            if (r46 == 0) goto L_0x0d66
            org.telegram.messenger.video.MP4Builder r9 = r15.mediaMuxer     // Catch:{ Exception -> 0x0d49, all -> 0x0e77 }
            r11 = 1
            int r9 = r9.addTrack(r10, r11)     // Catch:{ Exception -> 0x0d49, all -> 0x0e77 }
            r56 = r9
            android.media.MediaExtractor r9 = r15.extractor     // Catch:{ Exception -> 0x0d49, all -> 0x0e77 }
            r9.selectTrack(r6)     // Catch:{ Exception -> 0x0d49, all -> 0x0e77 }
            java.lang.String r9 = "max-input-size"
            int r9 = r10.getInteger(r9)     // Catch:{ Exception -> 0x0d0c, all -> 0x0CLASSNAME }
            r8 = r9
            goto L_0x0d11
        L_0x0d0c:
            r0 = move-exception
            r9 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)     // Catch:{ Exception -> 0x0d49, all -> 0x0e77 }
        L_0x0d11:
            if (r8 > 0) goto L_0x0d15
            r8 = 65536(0x10000, float:9.18355E-41)
        L_0x0d15:
            java.nio.ByteBuffer r9 = java.nio.ByteBuffer.allocateDirect(r8)     // Catch:{ Exception -> 0x0d49, all -> 0x0e77 }
            r45 = r9
            r11 = r97
            r39 = 0
            int r9 = (r11 > r39 ? 1 : (r11 == r39 ? 0 : -1))
            if (r9 <= 0) goto L_0x0d30
            android.media.MediaExtractor r9 = r15.extractor     // Catch:{ Exception -> 0x0d86, all -> 0x0CLASSNAME }
            r79 = r3
            r3 = 0
            r9.seekTo(r11, r3)     // Catch:{ Exception -> 0x0d86, all -> 0x0CLASSNAME }
            r101 = r7
            r102 = r8
            goto L_0x0d3e
        L_0x0d30:
            r79 = r3
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x0d86, all -> 0x0CLASSNAME }
            r101 = r7
            r102 = r8
            r7 = 0
            r9 = 0
            r3.seekTo(r7, r9)     // Catch:{ Exception -> 0x0d86, all -> 0x0CLASSNAME }
        L_0x0d3e:
            r8 = r102
            r24 = r5
            r3 = r6
            r7 = r44
            r5 = r56
            goto L_0x0ea8
        L_0x0d49:
            r0 = move-exception
            r11 = r97
            r24 = r95
            r56 = r1
            r1 = r4
            r3 = r6
            r36 = r13
            r13 = r14
            r30 = r27
            r5 = r41
            r14 = r52
            r2 = r62
            r26 = r65
            r25 = r76
            r51 = r99
            r4 = r0
            goto L_0x1cdf
        L_0x0d66:
            r11 = r97
            r79 = r3
            r101 = r7
            android.media.MediaExtractor r3 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0e32, all -> 0x0e30 }
            r3.<init>()     // Catch:{ Exception -> 0x0e32, all -> 0x0e30 }
            r7 = r86
            r3.setDataSource(r7)     // Catch:{ Exception -> 0x0e32, all -> 0x0e30 }
            r3.selectTrack(r6)     // Catch:{ Exception -> 0x0e32, all -> 0x0e30 }
            r39 = 0
            int r9 = (r11 > r39 ? 1 : (r11 == r39 ? 0 : -1))
            if (r9 <= 0) goto L_0x0da1
            r9 = 0
            r3.seekTo(r11, r9)     // Catch:{ Exception -> 0x0d86, all -> 0x0CLASSNAME }
            r102 = r8
            goto L_0x0da9
        L_0x0d86:
            r0 = move-exception
            r24 = r95
            r56 = r1
            r1 = r4
            r3 = r6
            r36 = r13
            r13 = r14
            r30 = r27
            r5 = r41
            r14 = r52
            r2 = r62
            r26 = r65
            r25 = r76
            r51 = r99
            r4 = r0
            goto L_0x1cdf
        L_0x0da1:
            r9 = 0
            r102 = r8
            r7 = 0
            r3.seekTo(r7, r9)     // Catch:{ Exception -> 0x0e32, all -> 0x0e30 }
        L_0x0da9:
            org.telegram.messenger.video.AudioRecoder r7 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x0e32, all -> 0x0e30 }
            r7.<init>(r10, r3, r6)     // Catch:{ Exception -> 0x0e32, all -> 0x0e30 }
            r7.startTime = r11     // Catch:{ Exception -> 0x0e0e, all -> 0x0e30 }
            r8 = r99
            r7.endTime = r8     // Catch:{ Exception -> 0x0dee, all -> 0x0deb }
            r20 = r3
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ Exception -> 0x0dee, all -> 0x0deb }
            r24 = r5
            android.media.MediaFormat r5 = r7.format     // Catch:{ Exception -> 0x0dee, all -> 0x0deb }
            r26 = r6
            r6 = 1
            int r3 = r3.addTrack(r5, r6)     // Catch:{ Exception -> 0x0dcd, all -> 0x0deb }
            r56 = r3
            r8 = r102
            r3 = r26
            r5 = r56
            goto L_0x0ea8
        L_0x0dcd:
            r0 = move-exception
            r24 = r95
            r56 = r1
            r1 = r4
            r44 = r7
            r36 = r13
            r13 = r14
            r3 = r26
            r30 = r27
            r5 = r41
            r14 = r52
            r2 = r62
            r26 = r65
            r25 = r76
            r4 = r0
            r51 = r8
            goto L_0x1cdf
        L_0x0deb:
            r0 = move-exception
            goto L_0x0e6e
        L_0x0dee:
            r0 = move-exception
            r26 = r6
            r24 = r95
            r56 = r1
            r1 = r4
            r44 = r7
            r36 = r13
            r13 = r14
            r3 = r26
            r30 = r27
            r5 = r41
            r14 = r52
            r2 = r62
            r26 = r65
            r25 = r76
            r4 = r0
            r51 = r8
            goto L_0x1cdf
        L_0x0e0e:
            r0 = move-exception
            r8 = r99
            r26 = r6
            r24 = r95
            r56 = r1
            r1 = r4
            r44 = r7
            r36 = r13
            r13 = r14
            r3 = r26
            r30 = r27
            r5 = r41
            r14 = r52
            r2 = r62
            r26 = r65
            r25 = r76
            r4 = r0
            r51 = r8
            goto L_0x1cdf
        L_0x0e30:
            r0 = move-exception
            goto L_0x0e6c
        L_0x0e32:
            r0 = move-exception
            r8 = r99
            r26 = r6
            r24 = r95
            r56 = r1
            r1 = r4
            r36 = r13
            r13 = r14
            r3 = r26
            r30 = r27
            r5 = r41
            r14 = r52
            r2 = r62
            r26 = r65
            r25 = r76
            r4 = r0
            r51 = r8
            goto L_0x1cdf
        L_0x0e52:
            r11 = r97
            r79 = r3
            r24 = r5
            r26 = r6
            r101 = r7
            r102 = r8
            r8 = r99
            r8 = r102
            r3 = r26
            r7 = r44
            r5 = r56
            goto L_0x0ea8
        L_0x0e69:
            r0 = move-exception
            r11 = r97
        L_0x0e6c:
            r8 = r99
        L_0x0e6e:
            r12 = r92
            r11 = r93
            r1 = r0
            r51 = r8
            goto L_0x0CLASSNAME
        L_0x0e77:
            r0 = move-exception
            r11 = r97
            goto L_0x0CLASSNAME
        L_0x0e7c:
            r0 = move-exception
            r11 = r97
            r24 = r95
            r56 = r1
            r1 = r4
            r3 = r9
            r36 = r13
            r13 = r14
            r30 = r27
            r5 = r41
            r14 = r52
            r2 = r62
            r26 = r65
            r25 = r76
            r51 = r99
            r4 = r0
            goto L_0x1cdf
        L_0x0e99:
            r11 = r97
            r79 = r3
            r24 = r5
            r101 = r7
            r102 = r8
            r3 = r9
            r7 = r44
            r5 = r56
        L_0x0ea8:
            if (r3 >= 0) goto L_0x0eac
            r6 = 1
            goto L_0x0ead
        L_0x0eac:
            r6 = 0
        L_0x0ead:
            r9 = 1
            r85.checkConversionCanceled()     // Catch:{ Exception -> 0x1b0b, all -> 0x1b78 }
            r10 = r8
            r20 = r18
            r30 = r27
            r26 = r65
            r18 = r9
            r8 = r99
        L_0x0ebc:
            if (r50 == 0) goto L_0x0ee2
            if (r46 != 0) goto L_0x0ec3
            if (r6 != 0) goto L_0x0ec3
            goto L_0x0ee2
        L_0x0ec3:
            r12 = r92
            r11 = r93
            r24 = r95
            r56 = r1
            r1 = r3
            r3 = r4
            r44 = r7
            r36 = r13
            r13 = r14
            r18 = r20
            r10 = r37
            r5 = r41
            r14 = r52
            r25 = r76
            r51 = r8
            r8 = r19
            goto L_0x1d26
        L_0x0ee2:
            r85.checkConversionCanceled()     // Catch:{ Exception -> 0x1ae6, all -> 0x1ad3 }
            if (r46 != 0) goto L_0x0f1f
            if (r7 == 0) goto L_0x0f1f
            r99 = r6
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x0var_, all -> 0x0ef2 }
            boolean r6 = r7.step(r6, r5)     // Catch:{ Exception -> 0x0var_, all -> 0x0ef2 }
            goto L_0x0var_
        L_0x0ef2:
            r0 = move-exception
            r12 = r92
            r11 = r93
            r1 = r0
            r51 = r8
            r13 = r14
            r8 = r19
            r14 = r20
            r10 = r37
            r2 = r62
            goto L_0x1df2
        L_0x0var_:
            r0 = move-exception
            r24 = r95
            r56 = r1
            r1 = r4
            r44 = r7
            r36 = r13
            r13 = r14
            r18 = r20
            r5 = r41
            r14 = r52
            r2 = r62
            r25 = r76
            r4 = r0
            r51 = r8
            goto L_0x1cdf
        L_0x0f1f:
            r99 = r6
            r6 = r99
        L_0x0var_:
            if (r53 != 0) goto L_0x1291
            r32 = 0
            r99 = r6
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x1272, all -> 0x1267 }
            int r6 = r6.getSampleTrackIndex()     // Catch:{ Exception -> 0x1272, all -> 0x1267 }
            if (r6 != r13) goto L_0x0fd8
            r36 = r13
            r13 = 2500(0x9c4, double:1.235E-320)
            int r44 = r4.dequeueInputBuffer(r13)     // Catch:{ Exception -> 0x0fbd, all -> 0x0fa9 }
            r13 = r44
            if (r13 < 0) goto L_0x0f9c
            int r14 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0fbd, all -> 0x0fa9 }
            r102 = r7
            r7 = 21
            if (r14 >= r7) goto L_0x0var_
            r7 = r2[r13]     // Catch:{ Exception -> 0x0var_, all -> 0x0fa9 }
            goto L_0x0f4c
        L_0x0var_:
            java.nio.ByteBuffer r7 = r4.getInputBuffer(r13)     // Catch:{ Exception -> 0x0var_, all -> 0x0fa9 }
        L_0x0f4c:
            android.media.MediaExtractor r14 = r15.extractor     // Catch:{ Exception -> 0x0var_, all -> 0x0fa9 }
            r47 = r2
            r2 = 0
            int r14 = r14.readSampleData(r7, r2)     // Catch:{ Exception -> 0x0var_, all -> 0x0fa9 }
            r2 = r14
            if (r2 >= 0) goto L_0x0f6a
            r67 = 0
            r68 = 0
            r69 = 0
            r71 = 4
            r65 = r4
            r66 = r13
            r65.queueInputBuffer(r66, r67, r68, r69, r71)     // Catch:{ Exception -> 0x0var_, all -> 0x0fa9 }
            r53 = 1
            goto L_0x0fa0
        L_0x0f6a:
            r67 = 0
            android.media.MediaExtractor r14 = r15.extractor     // Catch:{ Exception -> 0x0var_, all -> 0x0fa9 }
            long r69 = r14.getSampleTime()     // Catch:{ Exception -> 0x0var_, all -> 0x0fa9 }
            r71 = 0
            r65 = r4
            r66 = r13
            r68 = r2
            r65.queueInputBuffer(r66, r67, r68, r69, r71)     // Catch:{ Exception -> 0x0var_, all -> 0x0fa9 }
            android.media.MediaExtractor r14 = r15.extractor     // Catch:{ Exception -> 0x0var_, all -> 0x0fa9 }
            r14.advance()     // Catch:{ Exception -> 0x0var_, all -> 0x0fa9 }
            goto L_0x0fa0
        L_0x0var_:
            r0 = move-exception
            r13 = r94
            r24 = r95
            r44 = r102
            r56 = r1
            r1 = r4
            r18 = r20
            r5 = r41
            r14 = r52
            r2 = r62
            r25 = r76
            r4 = r0
            r51 = r8
            goto L_0x1cdf
        L_0x0f9c:
            r47 = r2
            r102 = r7
        L_0x0fa0:
            r74 = r4
            r14 = r52
            r51 = r8
            r9 = r3
            goto L_0x121c
        L_0x0fa9:
            r0 = move-exception
            r12 = r92
            r11 = r93
            r13 = r94
            r1 = r0
            r51 = r8
            r8 = r19
            r14 = r20
            r10 = r37
            r2 = r62
            goto L_0x1df2
        L_0x0fbd:
            r0 = move-exception
            r102 = r7
            r13 = r94
            r24 = r95
            r44 = r102
            r56 = r1
            r1 = r4
            r18 = r20
            r5 = r41
            r14 = r52
            r2 = r62
            r25 = r76
            r4 = r0
            r51 = r8
            goto L_0x1cdf
        L_0x0fd8:
            r47 = r2
            r102 = r7
            r36 = r13
            if (r46 == 0) goto L_0x1210
            r2 = -1
            if (r3 == r2) goto L_0x1210
            if (r6 != r3) goto L_0x1210
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x11f3, all -> 0x1267 }
            r7 = 28
            if (r2 < r7) goto L_0x1034
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x101a, all -> 0x0fa9 }
            long r13 = r2.getSampleSize()     // Catch:{ Exception -> 0x101a, all -> 0x0fa9 }
            r7 = r3
            long r2 = (long) r10
            int r44 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
            if (r44 <= 0) goto L_0x1035
            r2 = 1024(0x400, double:5.06E-321)
            long r2 = r2 + r13
            int r10 = (int) r2
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocateDirect(r10)     // Catch:{ Exception -> 0x1000, all -> 0x0fa9 }
            goto L_0x1037
        L_0x1000:
            r0 = move-exception
            r13 = r94
            r24 = r95
            r44 = r102
            r56 = r1
            r1 = r4
            r3 = r7
            r18 = r20
            r5 = r41
            r14 = r52
            r2 = r62
            r25 = r76
            r4 = r0
            r51 = r8
            goto L_0x1cdf
        L_0x101a:
            r0 = move-exception
            r7 = r3
            r13 = r94
            r24 = r95
            r44 = r102
            r56 = r1
            r1 = r4
            r18 = r20
            r5 = r41
            r14 = r52
            r2 = r62
            r25 = r76
            r4 = r0
            r51 = r8
            goto L_0x1cdf
        L_0x1034:
            r7 = r3
        L_0x1035:
            r2 = r45
        L_0x1037:
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x11d1, all -> 0x1267 }
            r13 = 0
            int r3 = r3.readSampleData(r2, r13)     // Catch:{ Exception -> 0x11d1, all -> 0x1267 }
            r14 = r52
            r14.size = r3     // Catch:{ Exception -> 0x11b1, all -> 0x1267 }
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x11b1, all -> 0x1267 }
            r13 = 21
            if (r3 >= r13) goto L_0x106c
            r3 = 0
            r2.position(r3)     // Catch:{ Exception -> 0x1052, all -> 0x0fa9 }
            int r3 = r14.size     // Catch:{ Exception -> 0x1052, all -> 0x0fa9 }
            r2.limit(r3)     // Catch:{ Exception -> 0x1052, all -> 0x0fa9 }
            goto L_0x106c
        L_0x1052:
            r0 = move-exception
            r13 = r94
            r24 = r95
            r44 = r102
            r56 = r1
            r45 = r2
            r1 = r4
            r3 = r7
            r51 = r8
            r18 = r20
            r5 = r41
            r2 = r62
            r25 = r76
            r4 = r0
            goto L_0x1cdf
        L_0x106c:
            int r3 = r14.size     // Catch:{ Exception -> 0x11b1, all -> 0x1267 }
            if (r3 < 0) goto L_0x10b8
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x109b, all -> 0x0fa9 }
            r74 = r4
            long r3 = r3.getSampleTime()     // Catch:{ Exception -> 0x1080, all -> 0x0fa9 }
            r14.presentationTimeUs = r3     // Catch:{ Exception -> 0x1080, all -> 0x0fa9 }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x1080, all -> 0x0fa9 }
            r3.advance()     // Catch:{ Exception -> 0x1080, all -> 0x0fa9 }
            goto L_0x10bf
        L_0x1080:
            r0 = move-exception
            r13 = r94
            r24 = r95
            r44 = r102
            r4 = r0
            r56 = r1
            r45 = r2
            r3 = r7
            r51 = r8
            r18 = r20
            r5 = r41
            r2 = r62
            r1 = r74
            r25 = r76
            goto L_0x1cdf
        L_0x109b:
            r0 = move-exception
            r74 = r4
            r13 = r94
            r24 = r95
            r44 = r102
            r4 = r0
            r56 = r1
            r45 = r2
            r3 = r7
            r51 = r8
            r18 = r20
            r5 = r41
            r2 = r62
            r1 = r74
            r25 = r76
            goto L_0x1cdf
        L_0x10b8:
            r74 = r4
            r3 = 0
            r14.size = r3     // Catch:{ Exception -> 0x1193, all -> 0x1267 }
            r53 = 1
        L_0x10bf:
            int r3 = r14.size     // Catch:{ Exception -> 0x1193, all -> 0x1267 }
            if (r3 <= 0) goto L_0x118a
            r3 = 0
            int r13 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r13 < 0) goto L_0x10d7
            long r3 = r14.presentationTimeUs     // Catch:{ Exception -> 0x1080, all -> 0x0fa9 }
            int r13 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r13 >= 0) goto L_0x10d0
            goto L_0x10d7
        L_0x10d0:
            r44 = r2
            r51 = r8
            r9 = r7
            goto L_0x118f
        L_0x10d7:
            r3 = 0
            r14.offset = r3     // Catch:{ Exception -> 0x1193, all -> 0x1267 }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x1193, all -> 0x1267 }
            int r3 = r3.getSampleFlags()     // Catch:{ Exception -> 0x1193, all -> 0x1267 }
            r14.flags = r3     // Catch:{ Exception -> 0x1193, all -> 0x1267 }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ Exception -> 0x1193, all -> 0x1267 }
            r4 = 0
            long r44 = r3.writeSampleData(r5, r2, r14, r4)     // Catch:{ Exception -> 0x1193, all -> 0x1267 }
            r3 = r44
            r39 = 0
            int r13 = (r3 > r39 ? 1 : (r3 == r39 ? 0 : -1))
            if (r13 == 0) goto L_0x1181
            org.telegram.messenger.MediaController$VideoConvertorListener r13 = r15.callback     // Catch:{ Exception -> 0x1193, all -> 0x1267 }
            if (r13 == 0) goto L_0x117b
            r51 = r8
            r9 = r7
            long r7 = r14.presentationTimeUs     // Catch:{ Exception -> 0x1160, all -> 0x123c }
            long r7 = r7 - r11
            int r13 = (r7 > r41 ? 1 : (r7 == r41 ? 0 : -1))
            if (r13 <= 0) goto L_0x111c
            long r7 = r14.presentationTimeUs     // Catch:{ Exception -> 0x1103, all -> 0x123c }
            long r7 = r7 - r11
            goto L_0x111e
        L_0x1103:
            r0 = move-exception
            r13 = r94
            r24 = r95
            r44 = r102
            r4 = r0
            r56 = r1
            r45 = r2
            r3 = r9
            r18 = r20
            r5 = r41
            r2 = r62
            r1 = r74
            r25 = r76
            goto L_0x1cdf
        L_0x111c:
            r7 = r41
        L_0x111e:
            org.telegram.messenger.MediaController$VideoConvertorListener r13 = r15.callback     // Catch:{ Exception -> 0x1146, all -> 0x123c }
            r44 = r2
            float r2 = (float) r7
            float r2 = r2 / r22
            float r2 = r2 / r23
            r13.didWriteData(r3, r2)     // Catch:{ Exception -> 0x112e, all -> 0x123c }
            r41 = r7
            goto L_0x1186
        L_0x112e:
            r0 = move-exception
            r13 = r94
            r24 = r95
            r4 = r0
            r56 = r1
            r5 = r7
            r3 = r9
            r18 = r20
            r45 = r44
            r2 = r62
            r1 = r74
            r25 = r76
            r44 = r102
            goto L_0x1cdf
        L_0x1146:
            r0 = move-exception
            r44 = r2
            r13 = r94
            r24 = r95
            r4 = r0
            r56 = r1
            r5 = r7
            r3 = r9
            r18 = r20
            r45 = r44
            r2 = r62
            r1 = r74
            r25 = r76
            r44 = r102
            goto L_0x1cdf
        L_0x1160:
            r0 = move-exception
            r44 = r2
            r13 = r94
            r24 = r95
            r4 = r0
            r56 = r1
            r3 = r9
            r18 = r20
            r5 = r41
            r45 = r44
            r2 = r62
            r1 = r74
            r25 = r76
            r44 = r102
            goto L_0x1cdf
        L_0x117b:
            r44 = r2
            r51 = r8
            r9 = r7
            goto L_0x1186
        L_0x1181:
            r44 = r2
            r51 = r8
            r9 = r7
        L_0x1186:
            r45 = r44
            goto L_0x121c
        L_0x118a:
            r44 = r2
            r51 = r8
            r9 = r7
        L_0x118f:
            r45 = r44
            goto L_0x121c
        L_0x1193:
            r0 = move-exception
            r44 = r2
            r51 = r8
            r9 = r7
            r13 = r94
            r24 = r95
            r4 = r0
            r56 = r1
            r3 = r9
            r18 = r20
            r5 = r41
            r45 = r44
            r2 = r62
            r1 = r74
            r25 = r76
            r44 = r102
            goto L_0x1cdf
        L_0x11b1:
            r0 = move-exception
            r44 = r2
            r74 = r4
            r51 = r8
            r9 = r7
            r13 = r94
            r24 = r95
            r4 = r0
            r56 = r1
            r3 = r9
            r18 = r20
            r5 = r41
            r45 = r44
            r2 = r62
            r1 = r74
            r25 = r76
            r44 = r102
            goto L_0x1cdf
        L_0x11d1:
            r0 = move-exception
            r44 = r2
            r74 = r4
            r14 = r52
            r51 = r8
            r9 = r7
            r13 = r94
            r24 = r95
            r4 = r0
            r56 = r1
            r3 = r9
            r18 = r20
            r5 = r41
            r45 = r44
            r2 = r62
            r1 = r74
            r25 = r76
            r44 = r102
            goto L_0x1cdf
        L_0x11f3:
            r0 = move-exception
            r74 = r4
            r14 = r52
            r51 = r8
            r9 = r3
            r13 = r94
            r24 = r95
            r44 = r102
            r4 = r0
            r56 = r1
            r18 = r20
            r5 = r41
            r2 = r62
            r1 = r74
            r25 = r76
            goto L_0x1cdf
        L_0x1210:
            r74 = r4
            r14 = r52
            r51 = r8
            r9 = r3
            r2 = -1
            if (r6 != r2) goto L_0x121c
            r32 = 1
        L_0x121c:
            if (r32 == 0) goto L_0x1264
            r2 = r74
            r3 = 2500(0x9c4, double:1.235E-320)
            int r7 = r2.dequeueInputBuffer(r3)     // Catch:{ Exception -> 0x124e, all -> 0x123c }
            r3 = r7
            if (r3 < 0) goto L_0x129f
            r67 = 0
            r68 = 0
            r69 = 0
            r71 = 4
            r65 = r2
            r66 = r3
            r65.queueInputBuffer(r66, r67, r68, r69, r71)     // Catch:{ Exception -> 0x124e, all -> 0x123c }
            r53 = 1
            goto L_0x129f
        L_0x123c:
            r0 = move-exception
            r12 = r92
            r11 = r93
            r13 = r94
            r1 = r0
            r8 = r19
            r14 = r20
            r10 = r37
            r2 = r62
            goto L_0x1df2
        L_0x124e:
            r0 = move-exception
            r13 = r94
            r24 = r95
            r44 = r102
            r4 = r0
            r56 = r1
            r1 = r2
            r3 = r9
            r18 = r20
            r5 = r41
            r2 = r62
            r25 = r76
            goto L_0x1cdf
        L_0x1264:
            r2 = r74
            goto L_0x129f
        L_0x1267:
            r0 = move-exception
            r51 = r8
            r12 = r92
            r11 = r93
            r13 = r94
            goto L_0x1adb
        L_0x1272:
            r0 = move-exception
            r2 = r4
            r102 = r7
            r36 = r13
            r14 = r52
            r51 = r8
            r9 = r3
            r13 = r94
            r24 = r95
            r44 = r102
            r4 = r0
            r56 = r1
            r1 = r2
            r18 = r20
            r5 = r41
            r2 = r62
            r25 = r76
            goto L_0x1cdf
        L_0x1291:
            r47 = r2
            r2 = r4
            r99 = r6
            r102 = r7
            r36 = r13
            r14 = r52
            r51 = r8
            r9 = r3
        L_0x129f:
            r3 = r54 ^ 1
            r4 = 1
            r6 = r20
            r7 = r59
        L_0x12a6:
            if (r3 != 0) goto L_0x12c1
            if (r4 == 0) goto L_0x12ab
            goto L_0x12c1
        L_0x12ab:
            r4 = r2
            r20 = r6
            r59 = r7
            r3 = r9
            r13 = r36
            r2 = r47
            r8 = r51
            r6 = r99
            r7 = r102
            r52 = r14
            r14 = r94
            goto L_0x0ebc
        L_0x12c1:
            r85.checkConversionCanceled()     // Catch:{ Exception -> 0x1ab3, all -> 0x1aa2 }
            if (r106 == 0) goto L_0x12ce
            r59 = 22000(0x55f0, double:1.08694E-319)
            r100 = r3
            r13 = r4
            r3 = r59
            goto L_0x12d3
        L_0x12ce:
            r100 = r3
            r13 = r4
            r3 = 2500(0x9c4, double:1.235E-320)
        L_0x12d3:
            r32 = r5
            r5 = r95
            int r3 = r5.dequeueOutputBuffer(r14, r3)     // Catch:{ Exception -> 0x1a84, all -> 0x1aa2 }
            r4 = -1
            if (r3 != r4) goto L_0x12f5
            r4 = 0
            r56 = r1
            r74 = r2
            r60 = r7
            r95 = r9
            r44 = r10
            r11 = r72
            r10 = r73
            r8 = r75
            r9 = r78
            r13 = r80
            goto L_0x1616
        L_0x12f5:
            r4 = -3
            if (r3 != r4) goto L_0x135a
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1341, all -> 0x1330 }
            r95 = r9
            r9 = 21
            if (r4 >= r9) goto L_0x131b
            java.nio.ByteBuffer[] r4 = r5.getOutputBuffers()     // Catch:{ Exception -> 0x140a, all -> 0x1330 }
            r24 = r4
            r56 = r1
            r74 = r2
            r60 = r7
            r44 = r10
            r4 = r13
            r11 = r72
            r10 = r73
            r8 = r75
            r9 = r78
            r13 = r80
            goto L_0x1616
        L_0x131b:
            r56 = r1
            r74 = r2
            r60 = r7
            r44 = r10
            r4 = r13
            r11 = r72
            r10 = r73
            r8 = r75
            r9 = r78
            r13 = r80
            goto L_0x1616
        L_0x1330:
            r0 = move-exception
            r12 = r92
            r11 = r93
            r13 = r94
            r1 = r0
            r14 = r6
            r8 = r19
            r10 = r37
            r2 = r62
            goto L_0x1df2
        L_0x1341:
            r0 = move-exception
            r95 = r9
            r13 = r94
            r3 = r95
            r44 = r102
            r4 = r0
            r56 = r1
            r1 = r2
            r24 = r5
            r18 = r6
            r5 = r41
            r2 = r62
            r25 = r76
            goto L_0x1cdf
        L_0x135a:
            r95 = r9
            r4 = -2
            if (r3 != r4) goto L_0x1421
            android.media.MediaFormat r4 = r5.getOutputFormat()     // Catch:{ Exception -> 0x140a, all -> 0x1330 }
            r9 = -5
            if (r6 != r9) goto L_0x13f5
            if (r4 == 0) goto L_0x13f5
            org.telegram.messenger.video.MP4Builder r9 = r15.mediaMuxer     // Catch:{ Exception -> 0x140a, all -> 0x1330 }
            r44 = r10
            r10 = 0
            int r9 = r9.addTrack(r4, r10)     // Catch:{ Exception -> 0x140a, all -> 0x1330 }
            r6 = r9
            r9 = r77
            boolean r10 = r4.containsKey(r9)     // Catch:{ Exception -> 0x13dc, all -> 0x13c8 }
            if (r10 == 0) goto L_0x13bd
            int r10 = r4.getInteger(r9)     // Catch:{ Exception -> 0x13dc, all -> 0x13c8 }
            r20 = r6
            r6 = 1
            if (r10 != r6) goto L_0x13b6
            r10 = r73
            java.nio.ByteBuffer r6 = r4.getByteBuffer(r10)     // Catch:{ Exception -> 0x139f, all -> 0x123c }
            r77 = r9
            r9 = r78
            java.nio.ByteBuffer r56 = r4.getByteBuffer(r9)     // Catch:{ Exception -> 0x139f, all -> 0x123c }
            int r59 = r6.limit()     // Catch:{ Exception -> 0x139f, all -> 0x123c }
            int r60 = r56.limit()     // Catch:{ Exception -> 0x139f, all -> 0x123c }
            int r30 = r59 + r60
            r6 = r20
            goto L_0x13fb
        L_0x139f:
            r0 = move-exception
            r13 = r94
            r3 = r95
            r44 = r102
            r4 = r0
            r56 = r1
            r1 = r2
            r24 = r5
            r18 = r20
            r5 = r41
            r2 = r62
            r25 = r76
            goto L_0x1cdf
        L_0x13b6:
            r77 = r9
            r10 = r73
            r9 = r78
            goto L_0x13c5
        L_0x13bd:
            r20 = r6
            r77 = r9
            r10 = r73
            r9 = r78
        L_0x13c5:
            r6 = r20
            goto L_0x13fb
        L_0x13c8:
            r0 = move-exception
            r20 = r6
            r12 = r92
            r11 = r93
            r13 = r94
            r1 = r0
            r8 = r19
            r14 = r20
            r10 = r37
            r2 = r62
            goto L_0x1df2
        L_0x13dc:
            r0 = move-exception
            r20 = r6
            r13 = r94
            r3 = r95
            r44 = r102
            r4 = r0
            r56 = r1
            r1 = r2
            r24 = r5
            r18 = r20
            r5 = r41
            r2 = r62
            r25 = r76
            goto L_0x1cdf
        L_0x13f5:
            r44 = r10
            r10 = r73
            r9 = r78
        L_0x13fb:
            r56 = r1
            r74 = r2
            r60 = r7
            r4 = r13
            r11 = r72
            r8 = r75
            r13 = r80
            goto L_0x1616
        L_0x140a:
            r0 = move-exception
            r13 = r94
            r3 = r95
            r44 = r102
            r4 = r0
            r56 = r1
            r1 = r2
            r24 = r5
            r18 = r6
            r5 = r41
            r2 = r62
            r25 = r76
            goto L_0x1cdf
        L_0x1421:
            r44 = r10
            r10 = r73
            r9 = r78
            if (r3 < 0) goto L_0x1a3f
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1a23, all -> 0x1aa2 }
            r20 = r13
            r13 = 21
            if (r4 >= r13) goto L_0x1434
            r4 = r24[r3]     // Catch:{ Exception -> 0x140a, all -> 0x1330 }
            goto L_0x1438
        L_0x1434:
            java.nio.ByteBuffer r4 = r5.getOutputBuffer(r3)     // Catch:{ Exception -> 0x1a23, all -> 0x1aa2 }
        L_0x1438:
            if (r4 == 0) goto L_0x19fe
            int r13 = r14.size     // Catch:{ Exception -> 0x1a23, all -> 0x1aa2 }
            r56 = r1
            r1 = 1
            if (r13 <= r1) goto L_0x15f3
            int r1 = r14.flags     // Catch:{ Exception -> 0x15db, all -> 0x1330 }
            r13 = 2
            r1 = r1 & r13
            if (r1 != 0) goto L_0x1523
            if (r30 == 0) goto L_0x1473
            int r1 = r14.flags     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            r35 = 1
            r1 = r1 & 1
            if (r1 == 0) goto L_0x1473
            int r1 = r14.offset     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            int r1 = r1 + r30
            r14.offset = r1     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            int r1 = r14.size     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            int r1 = r1 - r30
            r14.size = r1     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            goto L_0x1473
        L_0x145e:
            r0 = move-exception
            r13 = r94
            r3 = r95
            r44 = r102
            r4 = r0
            r1 = r2
            r24 = r5
            r18 = r6
            r5 = r41
            r2 = r62
            r25 = r76
            goto L_0x1cdf
        L_0x1473:
            if (r18 == 0) goto L_0x14cc
            int r1 = r14.flags     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            r35 = 1
            r1 = r1 & 1
            if (r1 == 0) goto L_0x14cc
            int r1 = r14.size     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            r13 = 100
            if (r1 <= r13) goto L_0x14c7
            int r1 = r14.offset     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            r4.position(r1)     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            byte[] r1 = new byte[r13]     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            r4.get(r1)     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            r59 = 0
            r60 = 0
            r13 = r60
        L_0x1493:
            r60 = r7
            int r7 = r1.length     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            int r7 = r7 + -4
            if (r13 >= r7) goto L_0x14c9
            byte r7 = r1[r13]     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            if (r7 != 0) goto L_0x14c2
            int r7 = r13 + 1
            byte r7 = r1[r7]     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            if (r7 != 0) goto L_0x14c2
            int r7 = r13 + 2
            byte r7 = r1[r7]     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            if (r7 != 0) goto L_0x14c2
            int r7 = r13 + 3
            byte r7 = r1[r7]     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            r8 = 1
            if (r7 != r8) goto L_0x14c2
            int r7 = r59 + 1
            if (r7 <= r8) goto L_0x14c0
            int r8 = r14.offset     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            int r8 = r8 + r13
            r14.offset = r8     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            int r8 = r14.size     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            int r8 = r8 - r13
            r14.size = r8     // Catch:{ Exception -> 0x145e, all -> 0x1330 }
            goto L_0x14c9
        L_0x14c0:
            r59 = r7
        L_0x14c2:
            int r13 = r13 + 1
            r7 = r60
            goto L_0x1493
        L_0x14c7:
            r60 = r7
        L_0x14c9:
            r18 = 0
            goto L_0x14ce
        L_0x14cc:
            r60 = r7
        L_0x14ce:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ Exception -> 0x15db, all -> 0x1330 }
            r7 = 1
            long r65 = r1.writeSampleData(r6, r4, r14, r7)     // Catch:{ Exception -> 0x15db, all -> 0x1330 }
            r7 = r65
            r39 = 0
            int r1 = (r7 > r39 ? 1 : (r7 == r39 ? 0 : -1))
            if (r1 == 0) goto L_0x1515
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r15.callback     // Catch:{ Exception -> 0x15db, all -> 0x1330 }
            if (r1 == 0) goto L_0x1512
            r74 = r2
            long r1 = r14.presentationTimeUs     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            long r1 = r1 - r11
            int r13 = (r1 > r41 ? 1 : (r1 == r41 ? 0 : -1))
            if (r13 <= 0) goto L_0x14ee
            long r1 = r14.presentationTimeUs     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            long r1 = r1 - r11
            goto L_0x14f0
        L_0x14ee:
            r1 = r41
        L_0x14f0:
            org.telegram.messenger.MediaController$VideoConvertorListener r13 = r15.callback     // Catch:{ Exception -> 0x14fd, all -> 0x1330 }
            float r11 = (float) r1     // Catch:{ Exception -> 0x14fd, all -> 0x1330 }
            float r11 = r11 / r22
            float r11 = r11 / r23
            r13.didWriteData(r7, r11)     // Catch:{ Exception -> 0x14fd, all -> 0x1330 }
            r41 = r1
            goto L_0x1517
        L_0x14fd:
            r0 = move-exception
            r13 = r94
            r3 = r95
            r44 = r102
            r4 = r0
            r24 = r5
            r18 = r6
            r25 = r76
            r5 = r1
            r2 = r62
            r1 = r74
            goto L_0x1cdf
        L_0x1512:
            r74 = r2
            goto L_0x1517
        L_0x1515:
            r74 = r2
        L_0x1517:
            r1 = r18
            r11 = r72
            r8 = r75
            r13 = r80
            r18 = r6
            goto L_0x1601
        L_0x1523:
            r74 = r2
            r60 = r7
            r1 = -5
            if (r6 != r1) goto L_0x15d4
            int r1 = r14.size     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            byte[] r1 = new byte[r1]     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            int r2 = r14.offset     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            int r7 = r14.size     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            int r2 = r2 + r7
            r4.limit(r2)     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            int r2 = r14.offset     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            r4.position(r2)     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            r4.get(r1)     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            r2 = 0
            r7 = 0
            int r8 = r14.size     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            r11 = 1
            int r8 = r8 - r11
        L_0x1544:
            if (r8 < 0) goto L_0x1590
            r12 = 3
            if (r8 <= r12) goto L_0x1591
            byte r13 = r1[r8]     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            if (r13 != r11) goto L_0x158c
            int r13 = r8 + -1
            byte r13 = r1[r13]     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            if (r13 != 0) goto L_0x158c
            int r13 = r8 + -2
            byte r13 = r1[r13]     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            if (r13 != 0) goto L_0x158c
            int r13 = r8 + -3
            byte r13 = r1[r13]     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            if (r13 != 0) goto L_0x158c
            int r13 = r8 + -3
            java.nio.ByteBuffer r13 = java.nio.ByteBuffer.allocate(r13)     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            r2 = r13
            int r13 = r14.size     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            int r38 = r8 + -3
            int r13 = r13 - r38
            java.nio.ByteBuffer r13 = java.nio.ByteBuffer.allocate(r13)     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            r7 = r13
            int r13 = r8 + -3
            r11 = 0
            java.nio.ByteBuffer r13 = r2.put(r1, r11, r13)     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            r13.position(r11)     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            int r11 = r8 + -3
            int r13 = r14.size     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            int r38 = r8 + -3
            int r13 = r13 - r38
            java.nio.ByteBuffer r11 = r7.put(r1, r11, r13)     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            r13 = 0
            r11.position(r13)     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            goto L_0x1591
        L_0x158c:
            int r8 = r8 + -1
            r11 = 1
            goto L_0x1544
        L_0x1590:
            r12 = 3
        L_0x1591:
            r11 = r72
            r8 = r75
            r13 = r80
            android.media.MediaFormat r38 = android.media.MediaFormat.createVideoFormat(r13, r8, r11)     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            r59 = r38
            if (r2 == 0) goto L_0x15aa
            if (r7 == 0) goto L_0x15aa
            r12 = r59
            r12.setByteBuffer(r10, r2)     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            r12.setByteBuffer(r9, r7)     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            goto L_0x15ac
        L_0x15aa:
            r12 = r59
        L_0x15ac:
            r59 = r1
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            r65 = r2
            r2 = 0
            int r1 = r1.addTrack(r12, r2)     // Catch:{ Exception -> 0x15be, all -> 0x1330 }
            r84 = r18
            r18 = r1
            r1 = r84
            goto L_0x1601
        L_0x15be:
            r0 = move-exception
            r13 = r94
            r3 = r95
            r44 = r102
            r4 = r0
            r24 = r5
            r18 = r6
            r5 = r41
            r2 = r62
            r1 = r74
            r25 = r76
            goto L_0x1cdf
        L_0x15d4:
            r11 = r72
            r8 = r75
            r13 = r80
            goto L_0x15fd
        L_0x15db:
            r0 = move-exception
            r74 = r2
            r13 = r94
            r3 = r95
            r44 = r102
            r4 = r0
            r24 = r5
            r18 = r6
            r5 = r41
            r2 = r62
            r1 = r74
            r25 = r76
            goto L_0x1cdf
        L_0x15f3:
            r74 = r2
            r60 = r7
            r11 = r72
            r8 = r75
            r13 = r80
        L_0x15fd:
            r1 = r18
            r18 = r6
        L_0x1601:
            int r2 = r14.flags     // Catch:{ Exception -> 0x19e8, all -> 0x19d6 }
            r2 = r2 & 4
            if (r2 == 0) goto L_0x1609
            r6 = 1
            goto L_0x160a
        L_0x1609:
            r6 = 0
        L_0x160a:
            r50 = r6
            r2 = 0
            r5.releaseOutputBuffer(r3, r2)     // Catch:{ Exception -> 0x19e8, all -> 0x19d6 }
            r6 = r18
            r4 = r20
            r18 = r1
        L_0x1616:
            r1 = -1
            if (r3 == r1) goto L_0x1637
            r3 = r100
            r75 = r8
            r78 = r9
            r73 = r10
            r72 = r11
            r80 = r13
            r10 = r44
            r1 = r56
            r7 = r60
            r2 = r74
            r9 = r95
            r11 = r97
            r95 = r5
            r5 = r32
            goto L_0x12a6
        L_0x1637:
            if (r54 != 0) goto L_0x19a8
            r12 = r6
            r2 = r74
            r6 = 2500(0x9c4, double:1.235E-320)
            int r20 = r2.dequeueOutputBuffer(r14, r6)     // Catch:{ Exception -> 0x198c, all -> 0x1978 }
            r33 = r20
            r6 = r33
            if (r6 != r1) goto L_0x1664
            r7 = 0
            r74 = r2
            r20 = r4
            r3 = r7
            r75 = r8
            r78 = r9
            r73 = r10
            r72 = r11
            r59 = r12
            r80 = r13
            r7 = r60
            r4 = r76
            r67 = -5
            r13 = r94
            goto L_0x19c0
        L_0x1664:
            r7 = -3
            if (r6 != r7) goto L_0x1681
            r74 = r2
            r20 = r4
            r75 = r8
            r78 = r9
            r73 = r10
            r72 = r11
            r59 = r12
            r80 = r13
            r7 = r60
            r4 = r76
            r67 = -5
            r13 = r94
            goto L_0x19be
        L_0x1681:
            r7 = -2
            if (r6 != r7) goto L_0x16e4
            android.media.MediaFormat r7 = r2.getOutputFormat()     // Catch:{ Exception -> 0x16cf, all -> 0x16be }
            boolean r20 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x16cf, all -> 0x16be }
            if (r20 == 0) goto L_0x16a4
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x16cf, all -> 0x16be }
            r1.<init>()     // Catch:{ Exception -> 0x16cf, all -> 0x16be }
            r20 = r4
            java.lang.String r4 = "newFormat = "
            r1.append(r4)     // Catch:{ Exception -> 0x16cf, all -> 0x16be }
            r1.append(r7)     // Catch:{ Exception -> 0x16cf, all -> 0x16be }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x16cf, all -> 0x16be }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x16cf, all -> 0x16be }
            goto L_0x16a6
        L_0x16a4:
            r20 = r4
        L_0x16a6:
            r74 = r2
            r75 = r8
            r78 = r9
            r73 = r10
            r72 = r11
            r59 = r12
            r80 = r13
            r7 = r60
            r4 = r76
            r67 = -5
            r13 = r94
            goto L_0x19be
        L_0x16be:
            r0 = move-exception
            r11 = r93
            r13 = r94
            r1 = r0
            r14 = r12
            r8 = r19
            r10 = r37
            r2 = r62
            r12 = r92
            goto L_0x1df2
        L_0x16cf:
            r0 = move-exception
            r13 = r94
            r3 = r95
            r44 = r102
            r4 = r0
            r1 = r2
            r24 = r5
            r18 = r12
            r5 = r41
            r2 = r62
            r25 = r76
            goto L_0x1cdf
        L_0x16e4:
            r20 = r4
            if (r6 < 0) goto L_0x192e
            int r1 = r14.size     // Catch:{ Exception -> 0x198c, all -> 0x1978 }
            if (r1 == 0) goto L_0x16ee
            r1 = 1
            goto L_0x16ef
        L_0x16ee:
            r1 = 0
        L_0x16ef:
            r75 = r8
            long r7 = r14.presentationTimeUs     // Catch:{ Exception -> 0x198c, all -> 0x1978 }
            r33 = 0
            int r4 = (r51 > r33 ? 1 : (r51 == r33 ? 0 : -1))
            if (r4 <= 0) goto L_0x1708
            int r4 = (r7 > r51 ? 1 : (r7 == r51 ? 0 : -1))
            if (r4 < 0) goto L_0x1708
            r53 = 1
            r54 = 1
            r1 = 0
            int r4 = r14.flags     // Catch:{ Exception -> 0x16cf, all -> 0x16be }
            r4 = r4 | 4
            r14.flags = r4     // Catch:{ Exception -> 0x16cf, all -> 0x16be }
        L_0x1708:
            r4 = 0
            r33 = 0
            int r59 = (r26 > r33 ? 1 : (r26 == r33 ? 0 : -1))
            if (r59 < 0) goto L_0x1811
            r33 = r1
            int r1 = r14.flags     // Catch:{ Exception -> 0x17f8, all -> 0x17ef }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x17dc
            r72 = r11
            r1 = r12
            r11 = r97
            long r67 = r26 - r11
            long r67 = java.lang.Math.abs(r67)     // Catch:{ Exception -> 0x17c5, all -> 0x17b1 }
            r34 = 1000000(0xvar_, float:1.401298E-39)
            r59 = r1
            r80 = r13
            r13 = r94
            int r1 = r34 / r13
            r78 = r9
            r73 = r10
            long r9 = (long) r1     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            int r1 = (r67 > r9 ? 1 : (r67 == r9 ? 0 : -1))
            if (r1 <= 0) goto L_0x1798
            r9 = 0
            int r1 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r1 <= 0) goto L_0x1746
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            r9 = 0
            r1.seekTo(r11, r9)     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            r34 = r4
            r4 = 0
            goto L_0x1750
        L_0x1746:
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            r34 = r4
            r4 = 0
            r9 = 0
            r1.seekTo(r9, r4)     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
        L_0x1750:
            long r57 = r60 + r63
            r9 = r26
            r26 = -1
            r1 = 0
            r51 = 0
            r33 = 0
            int r4 = r14.flags     // Catch:{ Exception -> 0x1783, all -> 0x1771 }
            r67 = -5
            r4 = r4 & -5
            r14.flags = r4     // Catch:{ Exception -> 0x1783, all -> 0x1771 }
            r2.flush()     // Catch:{ Exception -> 0x1783, all -> 0x1771 }
            r4 = 1
            r53 = r1
            r1 = r33
            r54 = r51
            r51 = r9
            goto L_0x1829
        L_0x1771:
            r0 = move-exception
            r12 = r92
            r11 = r93
            r1 = r0
            r51 = r9
            r8 = r19
            r10 = r37
            r14 = r59
            r2 = r62
            goto L_0x1df2
        L_0x1783:
            r0 = move-exception
            r3 = r95
            r44 = r102
            r4 = r0
            r1 = r2
            r24 = r5
            r51 = r9
            r5 = r41
            r18 = r59
            r2 = r62
            r25 = r76
            goto L_0x1cdf
        L_0x1798:
            r34 = r4
            r67 = -5
            goto L_0x1825
        L_0x179e:
            r0 = move-exception
            r3 = r95
            r44 = r102
            r4 = r0
            r1 = r2
            r24 = r5
            r5 = r41
            r18 = r59
            r2 = r62
            r25 = r76
            goto L_0x1cdf
        L_0x17b1:
            r0 = move-exception
            r13 = r94
            r59 = r1
            r12 = r92
            r11 = r93
            r1 = r0
            r8 = r19
            r10 = r37
            r14 = r59
            r2 = r62
            goto L_0x1df2
        L_0x17c5:
            r0 = move-exception
            r13 = r94
            r59 = r1
            r3 = r95
            r44 = r102
            r4 = r0
            r1 = r2
            r24 = r5
            r5 = r41
            r18 = r59
            r2 = r62
            r25 = r76
            goto L_0x1cdf
        L_0x17dc:
            r34 = r4
            r78 = r9
            r73 = r10
            r72 = r11
            r59 = r12
            r80 = r13
            r67 = -5
            r13 = r94
            r11 = r97
            goto L_0x1825
        L_0x17ef:
            r0 = move-exception
            r13 = r94
            r59 = r12
            r11 = r97
            goto L_0x197d
        L_0x17f8:
            r0 = move-exception
            r13 = r94
            r59 = r12
            r11 = r97
            r3 = r95
            r44 = r102
            r4 = r0
            r1 = r2
            r24 = r5
            r5 = r41
            r18 = r59
            r2 = r62
            r25 = r76
            goto L_0x1cdf
        L_0x1811:
            r33 = r1
            r34 = r4
            r78 = r9
            r73 = r10
            r72 = r11
            r59 = r12
            r80 = r13
            r67 = -5
            r13 = r94
            r11 = r97
        L_0x1825:
            r1 = r33
            r4 = r34
        L_0x1829:
            r9 = 0
            int r33 = (r26 > r9 ? 1 : (r26 == r9 ? 0 : -1))
            if (r33 < 0) goto L_0x1832
            r33 = r26
            goto L_0x1834
        L_0x1832:
            r33 = r11
        L_0x1834:
            r68 = r33
            r11 = r68
            int r33 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r33 <= 0) goto L_0x187f
            r9 = -1
            int r33 = (r48 > r9 ? 1 : (r48 == r9 ? 0 : -1))
            if (r33 != 0) goto L_0x187f
            int r9 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r9 >= 0) goto L_0x186f
            r1 = 0
            boolean r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            if (r9 == 0) goto L_0x186c
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            r9.<init>()     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            java.lang.String r10 = "drop frame startTime = "
            r9.append(r10)     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            r9.append(r11)     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            java.lang.String r10 = " present time = "
            r9.append(r10)     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            r33 = r7
            long r7 = r14.presentationTimeUs     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            r9.append(r7)     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            java.lang.String r7 = r9.toString()     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            org.telegram.messenger.FileLog.d(r7)     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            goto L_0x1881
        L_0x186c:
            r33 = r7
            goto L_0x1881
        L_0x186f:
            r33 = r7
            long r7 = r14.presentationTimeUs     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            r48 = r7
            r7 = -2147483648(0xfffffffvar_, double:NaN)
            int r9 = (r60 > r7 ? 1 : (r60 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x1881
            long r57 = r57 - r48
            goto L_0x1881
        L_0x187f:
            r33 = r7
        L_0x1881:
            if (r4 == 0) goto L_0x1888
            r7 = -1
            r48 = r7
            goto L_0x189d
        L_0x1888:
            r7 = -1
            int r9 = (r26 > r7 ? 1 : (r26 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x189a
            r7 = 0
            int r9 = (r57 > r7 ? 1 : (r57 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x189a
            long r7 = r14.presentationTimeUs     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            long r7 = r7 + r57
            r14.presentationTimeUs = r7     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
        L_0x189a:
            r2.releaseOutputBuffer(r6, r1)     // Catch:{ Exception -> 0x1916, all -> 0x1954 }
        L_0x189d:
            if (r1 == 0) goto L_0x18f3
            r7 = 0
            int r9 = (r26 > r7 ? 1 : (r26 == r7 ? 0 : -1))
            if (r9 < 0) goto L_0x18af
            long r9 = r14.presentationTimeUs     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            r7 = r60
            long r9 = java.lang.Math.max(r7, r9)     // Catch:{ Exception -> 0x179e, all -> 0x1954 }
            r7 = r9
            goto L_0x18b1
        L_0x18af:
            r7 = r60
        L_0x18b1:
            r9 = 0
            r56.awaitNewImage()     // Catch:{ Exception -> 0x18b6, all -> 0x1954 }
            goto L_0x18bc
        L_0x18b6:
            r0 = move-exception
            r10 = r0
            r9 = 1
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)     // Catch:{ Exception -> 0x1916, all -> 0x1954 }
        L_0x18bc:
            if (r9 != 0) goto L_0x18eb
            r56.drawImage()     // Catch:{ Exception -> 0x1916, all -> 0x1954 }
            r10 = r1
            r74 = r2
            long r1 = r14.presentationTimeUs     // Catch:{ Exception -> 0x18d5, all -> 0x1954 }
            r60 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 * r60
            r60 = r4
            r4 = r76
            r4.setPresentationTime(r1)     // Catch:{ Exception -> 0x1964, all -> 0x1954 }
            r4.swapBuffers()     // Catch:{ Exception -> 0x1964, all -> 0x1954 }
            goto L_0x18fc
        L_0x18d5:
            r0 = move-exception
            r4 = r76
            r3 = r95
            r44 = r102
            r25 = r4
            r24 = r5
            r5 = r41
            r18 = r59
            r2 = r62
            r1 = r74
            r4 = r0
            goto L_0x1cdf
        L_0x18eb:
            r10 = r1
            r74 = r2
            r60 = r4
            r4 = r76
            goto L_0x18fc
        L_0x18f3:
            r10 = r1
            r74 = r2
            r7 = r60
            r60 = r4
            r4 = r76
        L_0x18fc:
            int r1 = r14.flags     // Catch:{ Exception -> 0x1964, all -> 0x1954 }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x1912
            r1 = 0
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1964, all -> 0x1954 }
            if (r2 == 0) goto L_0x190c
            java.lang.String r2 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x1964, all -> 0x1954 }
        L_0x190c:
            r5.signalEndOfInputStream()     // Catch:{ Exception -> 0x1964, all -> 0x1954 }
            r3 = r1
            goto L_0x19c0
        L_0x1912:
            r3 = r100
            goto L_0x19c0
        L_0x1916:
            r0 = move-exception
            r74 = r2
            r4 = r76
            r3 = r95
            r44 = r102
            r25 = r4
            r24 = r5
            r5 = r41
            r18 = r59
            r2 = r62
            r1 = r74
            r4 = r0
            goto L_0x1cdf
        L_0x192e:
            r13 = r94
            r74 = r2
            r75 = r8
            r72 = r11
            r59 = r12
            r7 = r60
            r4 = r76
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1964, all -> 0x1954 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1964, all -> 0x1954 }
            r2.<init>()     // Catch:{ Exception -> 0x1964, all -> 0x1954 }
            java.lang.String r9 = "unexpected result from decoder.dequeueOutputBuffer: "
            r2.append(r9)     // Catch:{ Exception -> 0x1964, all -> 0x1954 }
            r2.append(r6)     // Catch:{ Exception -> 0x1964, all -> 0x1954 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x1964, all -> 0x1954 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x1964, all -> 0x1954 }
            throw r1     // Catch:{ Exception -> 0x1964, all -> 0x1954 }
        L_0x1954:
            r0 = move-exception
            r12 = r92
            r11 = r93
            r1 = r0
            r8 = r19
            r10 = r37
            r14 = r59
            r2 = r62
            goto L_0x1df2
        L_0x1964:
            r0 = move-exception
            r3 = r95
            r44 = r102
            r25 = r4
            r24 = r5
            r5 = r41
            r18 = r59
            r2 = r62
            r1 = r74
            r4 = r0
            goto L_0x1cdf
        L_0x1978:
            r0 = move-exception
            r13 = r94
            r59 = r12
        L_0x197d:
            r12 = r92
            r11 = r93
            r1 = r0
            r8 = r19
            r10 = r37
            r14 = r59
            r2 = r62
            goto L_0x1df2
        L_0x198c:
            r0 = move-exception
            r13 = r94
            r74 = r2
            r59 = r12
            r4 = r76
            r3 = r95
            r44 = r102
            r25 = r4
            r24 = r5
            r5 = r41
            r18 = r59
            r2 = r62
            r1 = r74
            r4 = r0
            goto L_0x1cdf
        L_0x19a8:
            r20 = r4
            r59 = r6
            r75 = r8
            r78 = r9
            r73 = r10
            r72 = r11
            r80 = r13
            r7 = r60
            r4 = r76
            r67 = -5
            r13 = r94
        L_0x19be:
            r3 = r100
        L_0x19c0:
            r9 = r95
            r11 = r97
            r76 = r4
            r95 = r5
            r4 = r20
            r5 = r32
            r10 = r44
            r1 = r56
            r6 = r59
            r2 = r74
            goto L_0x12a6
        L_0x19d6:
            r0 = move-exception
            r13 = r94
            r12 = r92
            r11 = r93
            r1 = r0
            r14 = r18
            r8 = r19
            r10 = r37
            r2 = r62
            goto L_0x1df2
        L_0x19e8:
            r0 = move-exception
            r13 = r94
            r4 = r76
            r3 = r95
            r44 = r102
            r25 = r4
            r24 = r5
            r5 = r41
            r2 = r62
            r1 = r74
            r4 = r0
            goto L_0x1cdf
        L_0x19fe:
            r13 = r94
            r56 = r1
            r74 = r2
            r1 = r4
            r4 = r76
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1a70, all -> 0x1a61 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1a70, all -> 0x1a61 }
            r9.<init>()     // Catch:{ Exception -> 0x1a70, all -> 0x1a61 }
            java.lang.String r10 = "encoderOutputBuffer "
            r9.append(r10)     // Catch:{ Exception -> 0x1a70, all -> 0x1a61 }
            r9.append(r3)     // Catch:{ Exception -> 0x1a70, all -> 0x1a61 }
            java.lang.String r10 = " was null"
            r9.append(r10)     // Catch:{ Exception -> 0x1a70, all -> 0x1a61 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x1a70, all -> 0x1a61 }
            r2.<init>(r9)     // Catch:{ Exception -> 0x1a70, all -> 0x1a61 }
            throw r2     // Catch:{ Exception -> 0x1a70, all -> 0x1a61 }
        L_0x1a23:
            r0 = move-exception
            r13 = r94
            r56 = r1
            r74 = r2
            r4 = r76
            r3 = r95
            r44 = r102
            r25 = r4
            r24 = r5
            r18 = r6
            r5 = r41
            r2 = r62
            r1 = r74
            r4 = r0
            goto L_0x1cdf
        L_0x1a3f:
            r56 = r1
            r74 = r2
            r20 = r13
            r4 = r76
            r13 = r94
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1a70, all -> 0x1a61 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1a70, all -> 0x1a61 }
            r2.<init>()     // Catch:{ Exception -> 0x1a70, all -> 0x1a61 }
            java.lang.String r9 = "unexpected result from encoder.dequeueOutputBuffer: "
            r2.append(r9)     // Catch:{ Exception -> 0x1a70, all -> 0x1a61 }
            r2.append(r3)     // Catch:{ Exception -> 0x1a70, all -> 0x1a61 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x1a70, all -> 0x1a61 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x1a70, all -> 0x1a61 }
            throw r1     // Catch:{ Exception -> 0x1a70, all -> 0x1a61 }
        L_0x1a61:
            r0 = move-exception
            r12 = r92
            r11 = r93
            r1 = r0
            r14 = r6
            r8 = r19
            r10 = r37
            r2 = r62
            goto L_0x1df2
        L_0x1a70:
            r0 = move-exception
            r3 = r95
            r44 = r102
            r25 = r4
            r24 = r5
            r18 = r6
            r5 = r41
            r2 = r62
            r1 = r74
            r4 = r0
            goto L_0x1cdf
        L_0x1a84:
            r0 = move-exception
            r13 = r94
            r56 = r1
            r74 = r2
            r95 = r9
            r4 = r76
            r3 = r95
            r44 = r102
            r25 = r4
            r24 = r5
            r18 = r6
            r5 = r41
            r2 = r62
            r1 = r74
            r4 = r0
            goto L_0x1cdf
        L_0x1aa2:
            r0 = move-exception
            r13 = r94
            r12 = r92
            r11 = r93
            r1 = r0
            r14 = r6
            r8 = r19
            r10 = r37
            r2 = r62
            goto L_0x1df2
        L_0x1ab3:
            r0 = move-exception
            r13 = r94
            r5 = r95
            r56 = r1
            r74 = r2
            r95 = r9
            r4 = r76
            r3 = r95
            r44 = r102
            r25 = r4
            r24 = r5
            r18 = r6
            r5 = r41
            r2 = r62
            r1 = r74
            r4 = r0
            goto L_0x1cdf
        L_0x1ad3:
            r0 = move-exception
            r51 = r8
            r13 = r14
            r12 = r92
            r11 = r93
        L_0x1adb:
            r1 = r0
            r8 = r19
            r14 = r20
            r10 = r37
            r2 = r62
            goto L_0x1df2
        L_0x1ae6:
            r0 = move-exception
            r5 = r95
            r56 = r1
            r95 = r3
            r74 = r4
            r102 = r7
            r36 = r13
            r13 = r14
            r14 = r52
            r4 = r76
            r51 = r8
            r44 = r102
            r25 = r4
            r24 = r5
            r18 = r20
            r5 = r41
            r2 = r62
            r1 = r74
            r4 = r0
            goto L_0x1cdf
        L_0x1b0b:
            r0 = move-exception
            r5 = r95
            r56 = r1
            r95 = r3
            r74 = r4
            r102 = r7
            r36 = r13
            r13 = r14
            r14 = r52
            r4 = r76
            r51 = r99
            r44 = r102
            r25 = r4
            r24 = r5
            r30 = r27
            r5 = r41
            r2 = r62
            r26 = r65
            r1 = r74
            r4 = r0
            goto L_0x1cdf
        L_0x1b32:
            r0 = move-exception
            r5 = r95
            r56 = r1
            r74 = r4
            r36 = r13
            r13 = r14
            r9 = r51
            r14 = r52
            r4 = r76
            r51 = r99
            r25 = r4
            r24 = r5
            r3 = r9
            r30 = r27
            r5 = r41
            r2 = r62
            r26 = r65
            r1 = r74
            r4 = r0
            goto L_0x1cdf
        L_0x1b56:
            r0 = move-exception
            r5 = r95
            r56 = r1
            r36 = r13
            r13 = r14
            r9 = r51
            r14 = r52
            r4 = r76
            r51 = r99
            r25 = r4
            r24 = r5
            r3 = r9
            r30 = r27
            r5 = r41
            r2 = r62
            r26 = r65
            r1 = r74
            r4 = r0
            goto L_0x1cdf
        L_0x1b78:
            r0 = move-exception
            r13 = r14
            r12 = r92
            r11 = r93
            r51 = r99
            r1 = r0
        L_0x1b81:
            r14 = r18
            r8 = r19
            r10 = r37
            r2 = r62
            r26 = r65
            goto L_0x1df2
        L_0x1b8d:
            r0 = move-exception
            r5 = r95
            r36 = r13
            r13 = r14
            r9 = r51
            r14 = r52
            r4 = r76
            r51 = r99
            r25 = r4
            r24 = r5
            r3 = r9
            r56 = r26
            r30 = r27
            r5 = r41
            r2 = r62
            r26 = r65
            r1 = r74
            r4 = r0
            goto L_0x1cdf
        L_0x1baf:
            r0 = move-exception
            r65 = r101
            r74 = r3
            r4 = r6
            r5 = r7
            r19 = r12
            r36 = r13
            r13 = r14
            r9 = r51
            r14 = r52
            r51 = r99
            r25 = r4
            r24 = r5
            r3 = r9
            r56 = r26
            r30 = r27
            r5 = r41
            r2 = r62
            r26 = r65
            r1 = r74
            r4 = r0
            goto L_0x1cdf
        L_0x1bd5:
            r0 = move-exception
            r65 = r101
            r4 = r6
            r5 = r7
            r19 = r12
            r36 = r13
            r13 = r14
            r9 = r51
            r14 = r52
            r51 = r99
            r25 = r4
            r24 = r5
            r3 = r9
            r56 = r26
            r30 = r27
            r5 = r41
            r1 = r61
            r2 = r62
            r26 = r65
            r4 = r0
            goto L_0x1cdf
        L_0x1bf9:
            r0 = move-exception
            r65 = r101
            r5 = r7
            r19 = r12
            r36 = r13
            r13 = r14
            r9 = r51
            r14 = r52
            r51 = r99
            r4 = r0
            r24 = r5
            r3 = r9
            r56 = r26
            r30 = r27
            r5 = r41
            r1 = r61
            r2 = r62
            r26 = r65
            goto L_0x1cdf
        L_0x1c1a:
            r0 = move-exception
            r65 = r101
            r13 = r14
            r11 = r93
            r51 = r99
            r1 = r0
            r8 = r12
            r14 = r18
            r10 = r37
            r2 = r62
            r26 = r65
            r12 = r92
            goto L_0x1df2
        L_0x1CLASSNAME:
            r0 = move-exception
            r65 = r101
            r19 = r12
            r36 = r13
            r13 = r14
            r9 = r51
            r14 = r52
            r51 = r99
            r4 = r0
            r3 = r9
            r56 = r26
            r30 = r27
            r5 = r41
            r1 = r61
            r2 = r62
            r26 = r65
            goto L_0x1cdf
        L_0x1c4e:
            r0 = move-exception
            r65 = r101
            r13 = r14
            r11 = r93
            r51 = r99
            r1 = r0
            r8 = r12
            r14 = r18
            r10 = r37
            r26 = r65
            r12 = r92
            goto L_0x1df2
        L_0x1CLASSNAME:
            r0 = move-exception
            r65 = r101
            r19 = r12
            r36 = r13
            r13 = r14
            r9 = r51
            r14 = r52
            r51 = r99
            r4 = r0
            r3 = r9
            r56 = r26
            r30 = r27
            r5 = r41
            r1 = r61
            r26 = r65
            goto L_0x1cdf
        L_0x1c7e:
            r0 = move-exception
            r65 = r101
            r13 = r14
            r11 = r93
            r51 = r99
            r1 = r0
            r10 = r12
            r14 = r18
            r8 = r37
            r26 = r65
            r12 = r92
            goto L_0x1df2
        L_0x1CLASSNAME:
            r0 = move-exception
            r65 = r101
            r36 = r13
            r13 = r14
            r19 = r37
            r9 = r51
            r14 = r52
            r37 = r12
            r51 = r99
            r4 = r0
            r3 = r9
            r56 = r26
            r30 = r27
            r5 = r41
            r1 = r61
            r26 = r65
            goto L_0x1cdf
        L_0x1caf:
            r0 = move-exception
            r13 = r14
            r11 = r93
            r2 = r95
            r51 = r99
            r26 = r101
            r1 = r0
            r10 = r12
            r14 = r18
            r8 = r37
            r12 = r92
            goto L_0x1df2
        L_0x1cc3:
            r0 = move-exception
            r61 = r1
            r36 = r13
            r13 = r14
            r19 = r37
            r9 = r51
            r14 = r52
            r37 = r12
            r2 = r95
            r51 = r99
            r4 = r0
            r3 = r9
            r56 = r26
            r30 = r27
            r5 = r41
            r26 = r101
        L_0x1cdf:
            boolean r7 = r4 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x1d5c }
            if (r7 == 0) goto L_0x1ce8
            if (r106 != 0) goto L_0x1ce8
            r7 = 1
            r17 = r7
        L_0x1ce8:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x1d5c }
            r7.<init>()     // Catch:{ all -> 0x1d5c }
            r8 = r19
            r7.append(r8)     // Catch:{ all -> 0x1d50 }
            r7.append(r2)     // Catch:{ all -> 0x1d50 }
            r10 = r37
            r7.append(r10)     // Catch:{ all -> 0x1d46 }
            r7.append(r13)     // Catch:{ all -> 0x1d46 }
            java.lang.String r9 = " size: "
            r7.append(r9)     // Catch:{ all -> 0x1d46 }
            r11 = r93
            r7.append(r11)     // Catch:{ all -> 0x1d3e }
            java.lang.String r9 = "x"
            r7.append(r9)     // Catch:{ all -> 0x1d3e }
            r12 = r92
            r7.append(r12)     // Catch:{ all -> 0x1d38 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x1d38 }
            org.telegram.messenger.FileLog.e((java.lang.String) r7)     // Catch:{ all -> 0x1d38 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ all -> 0x1d38 }
            r7 = 1
            r62 = r2
            r16 = r7
            r84 = r3
            r3 = r1
            r1 = r84
        L_0x1d26:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x1d8a }
            r4 = r36
            r2.unselectTrack(r4)     // Catch:{ all -> 0x1d8a }
            if (r3 == 0) goto L_0x1d35
            r3.stop()     // Catch:{ all -> 0x1d8a }
            r3.release()     // Catch:{ all -> 0x1d8a }
        L_0x1d35:
            r3 = r1
            goto L_0x1d84
        L_0x1d38:
            r0 = move-exception
            r1 = r0
            r14 = r18
            goto L_0x1df2
        L_0x1d3e:
            r0 = move-exception
            r12 = r92
            r1 = r0
            r14 = r18
            goto L_0x1df2
        L_0x1d46:
            r0 = move-exception
            r12 = r92
            r11 = r93
            r1 = r0
            r14 = r18
            goto L_0x1df2
        L_0x1d50:
            r0 = move-exception
            r12 = r92
            r11 = r93
            r10 = r37
            r1 = r0
            r14 = r18
            goto L_0x1df2
        L_0x1d5c:
            r0 = move-exception
            r12 = r92
            r11 = r93
            r8 = r19
            r10 = r37
            r1 = r0
            r14 = r18
            goto L_0x1df2
        L_0x1d6a:
            r11 = r93
            r10 = r12
            r4 = r13
            r13 = r14
            r8 = r37
            r9 = r51
            r14 = r52
            r12 = r92
            r62 = r95
            r51 = r99
            r3 = r9
            r56 = r26
            r30 = r27
            r5 = r41
            r26 = r101
        L_0x1d84:
            if (r56 == 0) goto L_0x1d92
            r56.release()     // Catch:{ all -> 0x1d8a }
            goto L_0x1d92
        L_0x1d8a:
            r0 = move-exception
            r1 = r0
            r14 = r18
            r2 = r62
            goto L_0x1df2
        L_0x1d92:
            if (r25 == 0) goto L_0x1d97
            r25.release()     // Catch:{ all -> 0x1d8a }
        L_0x1d97:
            if (r24 == 0) goto L_0x1d9f
            r24.stop()     // Catch:{ all -> 0x1d8a }
            r24.release()     // Catch:{ all -> 0x1d8a }
        L_0x1d9f:
            if (r44 == 0) goto L_0x1da4
            r44.release()     // Catch:{ all -> 0x1d8a }
        L_0x1da4:
            r85.checkConversionCanceled()     // Catch:{ all -> 0x1d8a }
            r1 = r18
            r10 = r62
        L_0x1dab:
            android.media.MediaExtractor r2 = r15.extractor
            if (r2 == 0) goto L_0x1db2
            r2.release()
        L_0x1db2:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer
            if (r2 == 0) goto L_0x1dc7
            r2.finishMovie()     // Catch:{ all -> 0x1dc2 }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ all -> 0x1dc2 }
            long r2 = r2.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x1dc2 }
            r15.endPresentationTime = r2     // Catch:{ all -> 0x1dc2 }
            goto L_0x1dc7
        L_0x1dc2:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x1dc7:
            r30 = r1
            r14 = r10
            r31 = r16
            r32 = r17
            r33 = r26
            r84 = r12
            r12 = r11
            r11 = r84
            goto L_0x1e4a
        L_0x1dd7:
            r0 = move-exception
            r10 = r3
            r13 = r7
            r8 = r14
            r2 = r95
            r51 = r99
            r26 = r101
            r1 = r0
            r14 = r18
            goto L_0x1df2
        L_0x1de5:
            r0 = move-exception
            r10 = r1
            r13 = r9
            r8 = r14
            r2 = r95
            r51 = r99
            r26 = r101
            r1 = r0
            r14 = r18
        L_0x1df2:
            r16 = 1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x1ed8 }
            r3.<init>()     // Catch:{ all -> 0x1ed8 }
            r3.append(r8)     // Catch:{ all -> 0x1ed8 }
            r3.append(r2)     // Catch:{ all -> 0x1ed8 }
            r3.append(r10)     // Catch:{ all -> 0x1ed8 }
            r3.append(r13)     // Catch:{ all -> 0x1ed8 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x1ed8 }
            r3.append(r11)     // Catch:{ all -> 0x1ed8 }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x1ed8 }
            r3.append(r12)     // Catch:{ all -> 0x1ed8 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x1ed8 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x1ed8 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x1ed8 }
            android.media.MediaExtractor r1 = r15.extractor
            if (r1 == 0) goto L_0x1e27
            r1.release()
        L_0x1e27:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer
            if (r1 == 0) goto L_0x1e3c
            r1.finishMovie()     // Catch:{ all -> 0x1e37 }
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ all -> 0x1e37 }
            long r3 = r1.getLastFrameTimestamp(r14)     // Catch:{ all -> 0x1e37 }
            r15.endPresentationTime = r3     // Catch:{ all -> 0x1e37 }
            goto L_0x1e3c
        L_0x1e37:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1e3c:
            r30 = r14
            r31 = r16
            r32 = r17
            r33 = r26
            r14 = r2
            r84 = r12
            r12 = r11
            r11 = r84
        L_0x1e4a:
            if (r32 == 0) goto L_0x1e82
            r22 = 1
            r1 = r85
            r2 = r86
            r3 = r87
            r4 = r88
            r5 = r89
            r6 = r90
            r7 = r91
            r8 = r11
            r9 = r12
            r10 = r94
            r81 = r11
            r11 = r14
            r82 = r12
            r12 = r96
            r83 = r14
            r13 = r97
            r15 = r51
            r17 = r33
            r19 = r103
            r21 = r105
            r23 = r107
            r24 = r108
            r25 = r109
            r26 = r110
            r27 = r111
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r15, r17, r19, r21, r22, r23, r24, r25, r26, r27)
            return r1
        L_0x1e82:
            r81 = r11
            r82 = r12
            r83 = r14
            long r1 = java.lang.System.currentTimeMillis()
            long r1 = r1 - r28
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x1ecf
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "compression completed time="
            r3.append(r4)
            r3.append(r1)
            java.lang.String r4 = " needCompress="
            r3.append(r4)
            r4 = r105
            r3.append(r4)
            java.lang.String r5 = " w="
            r3.append(r5)
            r12 = r81
            r3.append(r12)
            java.lang.String r5 = " h="
            r3.append(r5)
            r11 = r82
            r3.append(r11)
            java.lang.String r5 = " bitrate="
            r3.append(r5)
            r10 = r83
            r3.append(r10)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.d(r3)
            goto L_0x1ed7
        L_0x1ecf:
            r4 = r105
            r12 = r81
            r11 = r82
            r10 = r83
        L_0x1ed7:
            return r31
        L_0x1ed8:
            r0 = move-exception
            r4 = r105
            r1 = r0
            r3 = r85
            android.media.MediaExtractor r5 = r3.extractor
            if (r5 == 0) goto L_0x1ee5
            r5.release()
        L_0x1ee5:
            org.telegram.messenger.video.MP4Builder r5 = r3.mediaMuxer
            if (r5 == 0) goto L_0x1efa
            r5.finishMovie()     // Catch:{ all -> 0x1ef5 }
            org.telegram.messenger.video.MP4Builder r5 = r3.mediaMuxer     // Catch:{ all -> 0x1ef5 }
            long r5 = r5.getLastFrameTimestamp(r14)     // Catch:{ all -> 0x1ef5 }
            r3.endPresentationTime = r5     // Catch:{ all -> 0x1ef5 }
            goto L_0x1efa
        L_0x1ef5:
            r0 = move-exception
            r5 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x1efa:
            goto L_0x1efc
        L_0x1efb:
            throw r1
        L_0x1efc:
            goto L_0x1efb
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, int, int, int, long, long, long, long, boolean, boolean, org.telegram.messenger.MediaController$SavedFilterState, java.lang.String, java.util.ArrayList, boolean, org.telegram.messenger.MediaController$CropState):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:71:0x013c, code lost:
        if (r14[r10 + 3] != 1) goto L_0x0142;
     */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x0225  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x023f  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0242 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00e7  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00ea  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00f2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long readAndWriteTracks(android.media.MediaExtractor r32, org.telegram.messenger.video.MP4Builder r33, android.media.MediaCodec.BufferInfo r34, long r35, long r37, long r39, java.io.File r41, boolean r42) throws java.lang.Exception {
        /*
            r31 = this;
            r1 = r31
            r2 = r32
            r3 = r33
            r4 = r34
            r5 = r35
            r7 = 0
            int r8 = org.telegram.messenger.MediaController.findTrack(r2, r7)
            r10 = 1
            if (r42 == 0) goto L_0x0017
            int r0 = org.telegram.messenger.MediaController.findTrack(r2, r10)
            goto L_0x0018
        L_0x0017:
            r0 = -1
        L_0x0018:
            r11 = r0
            r0 = -1
            r12 = -1
            r13 = 0
            r14 = 0
            r9 = r39
            float r7 = (float) r9
            r16 = 1148846080(0x447a0000, float:1000.0)
            float r7 = r7 / r16
            r17 = 0
            java.lang.String r9 = "max-input-size"
            r10 = r12
            r18 = r13
            if (r8 < 0) goto L_0x0061
            r2.selectTrack(r8)
            android.media.MediaFormat r12 = r2.getTrackFormat(r8)
            r13 = 0
            int r21 = r3.addTrack(r12, r13)
            int r0 = r12.getInteger(r9)     // Catch:{ Exception -> 0x0041 }
            r17 = r0
            goto L_0x0047
        L_0x0041:
            r0 = move-exception
            r13 = r0
            r0 = r13
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0047:
            r22 = r12
            r12 = 0
            int r0 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x0056
            r12 = 0
            r2.seekTo(r5, r12)
            r23 = r14
            goto L_0x005e
        L_0x0056:
            r12 = 0
            r23 = r14
            r13 = 0
            r2.seekTo(r13, r12)
        L_0x005e:
            r12 = r17
            goto L_0x0067
        L_0x0061:
            r23 = r14
            r21 = r0
            r12 = r17
        L_0x0067:
            if (r11 < 0) goto L_0x00a3
            r2.selectTrack(r11)
            android.media.MediaFormat r13 = r2.getTrackFormat(r11)
            java.lang.String r0 = "mime"
            java.lang.String r0 = r13.getString(r0)
            java.lang.String r14 = "audio/unknown"
            boolean r0 = r0.equals(r14)
            if (r0 == 0) goto L_0x0081
            r11 = -1
            goto L_0x00a3
        L_0x0081:
            r14 = 1
            int r10 = r3.addTrack(r13, r14)
            int r0 = r13.getInteger(r9)     // Catch:{ Exception -> 0x0090 }
            int r0 = java.lang.Math.max(r0, r12)     // Catch:{ Exception -> 0x0090 }
            r12 = r0
            goto L_0x0094
        L_0x0090:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0094:
            r14 = 0
            int r0 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x009f
            r9 = 0
            r2.seekTo(r5, r9)
            goto L_0x00a3
        L_0x009f:
            r9 = 0
            r2.seekTo(r14, r9)
        L_0x00a3:
            if (r12 > 0) goto L_0x00a7
            r12 = 65536(0x10000, float:9.18355E-41)
        L_0x00a7:
            java.nio.ByteBuffer r0 = java.nio.ByteBuffer.allocateDirect(r12)
            r13 = -1
            if (r11 >= 0) goto L_0x00b3
            if (r8 < 0) goto L_0x00b2
            goto L_0x00b3
        L_0x00b2:
            return r13
        L_0x00b3:
            r25 = -1
            r31.checkConversionCanceled()
        L_0x00b8:
            if (r18 != 0) goto L_0x0251
            r31.checkConversionCanceled()
            r9 = 0
            int r15 = android.os.Build.VERSION.SDK_INT
            r13 = 28
            if (r15 < r13) goto L_0x00d8
            long r13 = r32.getSampleSize()
            r15 = r0
            long r0 = (long) r12
            int r17 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1))
            if (r17 <= 0) goto L_0x00d9
            r0 = 1024(0x400, double:5.06E-321)
            long r0 = r0 + r13
            int r1 = (int) r0
            java.nio.ByteBuffer r0 = java.nio.ByteBuffer.allocateDirect(r1)
            r12 = r1
            goto L_0x00da
        L_0x00d8:
            r15 = r0
        L_0x00d9:
            r0 = r15
        L_0x00da:
            r1 = 0
            int r13 = r2.readSampleData(r0, r1)
            r4.size = r13
            int r1 = r32.getSampleTrackIndex()
            if (r1 != r8) goto L_0x00ea
            r13 = r21
            goto L_0x00ef
        L_0x00ea:
            if (r1 != r11) goto L_0x00ee
            r13 = r10
            goto L_0x00ef
        L_0x00ee:
            r13 = -1
        L_0x00ef:
            r14 = -1
            if (r13 == r14) goto L_0x0225
            int r14 = android.os.Build.VERSION.SDK_INT
            r15 = 21
            if (r14 >= r15) goto L_0x0101
            r14 = 0
            r0.position(r14)
            int r14 = r4.size
            r0.limit(r14)
        L_0x0101:
            if (r1 == r11) goto L_0x018c
            byte[] r14 = r0.array()
            if (r14 == 0) goto L_0x0183
            int r15 = r0.arrayOffset()
            int r17 = r0.limit()
            int r17 = r15 + r17
            r22 = -1
            r27 = r15
            r28 = r9
            r9 = r22
            r22 = r10
            r10 = r27
        L_0x011f:
            r27 = r12
            int r12 = r17 + -4
            if (r10 > r12) goto L_0x017e
            byte r12 = r14[r10]
            if (r12 != 0) goto L_0x013f
            int r12 = r10 + 1
            byte r12 = r14[r12]
            if (r12 != 0) goto L_0x013f
            int r12 = r10 + 2
            byte r12 = r14[r12]
            if (r12 != 0) goto L_0x013f
            int r12 = r10 + 3
            byte r12 = r14[r12]
            r29 = r15
            r15 = 1
            if (r12 == r15) goto L_0x0146
            goto L_0x0142
        L_0x013f:
            r29 = r15
            r15 = 1
        L_0x0142:
            int r12 = r17 + -4
            if (r10 != r12) goto L_0x0173
        L_0x0146:
            r12 = -1
            if (r9 == r12) goto L_0x016f
            int r12 = r10 - r9
            int r15 = r17 + -4
            if (r10 == r15) goto L_0x0151
            r15 = 4
            goto L_0x0152
        L_0x0151:
            r15 = 0
        L_0x0152:
            int r12 = r12 - r15
            int r15 = r12 >> 24
            byte r15 = (byte) r15
            r14[r9] = r15
            int r15 = r9 + 1
            r30 = r11
            int r11 = r12 >> 16
            byte r11 = (byte) r11
            r14[r15] = r11
            int r11 = r9 + 2
            int r15 = r12 >> 8
            byte r15 = (byte) r15
            r14[r11] = r15
            int r11 = r9 + 3
            byte r15 = (byte) r12
            r14[r11] = r15
            r9 = r10
            goto L_0x0175
        L_0x016f:
            r30 = r11
            r9 = r10
            goto L_0x0175
        L_0x0173:
            r30 = r11
        L_0x0175:
            int r10 = r10 + 1
            r12 = r27
            r15 = r29
            r11 = r30
            goto L_0x011f
        L_0x017e:
            r30 = r11
            r29 = r15
            goto L_0x0194
        L_0x0183:
            r28 = r9
            r22 = r10
            r30 = r11
            r27 = r12
            goto L_0x0194
        L_0x018c:
            r28 = r9
            r22 = r10
            r30 = r11
            r27 = r12
        L_0x0194:
            int r9 = r4.size
            if (r9 < 0) goto L_0x01a1
            long r9 = r32.getSampleTime()
            r4.presentationTimeUs = r9
            r9 = r28
            goto L_0x01a5
        L_0x01a1:
            r9 = 0
            r4.size = r9
            r9 = 1
        L_0x01a5:
            int r10 = r4.size
            if (r10 <= 0) goto L_0x0218
            if (r9 != 0) goto L_0x0218
            if (r1 != r8) goto L_0x01be
            r10 = 0
            int r12 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r12 <= 0) goto L_0x01be
            r10 = -1
            int r12 = (r25 > r10 ? 1 : (r25 == r10 ? 0 : -1))
            if (r12 != 0) goto L_0x01c0
            long r14 = r4.presentationTimeUs
            r25 = r14
            goto L_0x01c0
        L_0x01be:
            r10 = -1
        L_0x01c0:
            r14 = 0
            int r12 = (r37 > r14 ? 1 : (r37 == r14 ? 0 : -1))
            if (r12 < 0) goto L_0x01d5
            long r14 = r4.presentationTimeUs
            int r12 = (r14 > r37 ? 1 : (r14 == r37 ? 0 : -1))
            if (r12 >= 0) goto L_0x01cd
            goto L_0x01d5
        L_0x01cd:
            r9 = 1
            r19 = 0
            r12 = r31
            r17 = r0
            goto L_0x021e
        L_0x01d5:
            r14 = 0
            r4.offset = r14
            int r12 = r32.getSampleFlags()
            r4.flags = r12
            long r10 = r3.writeSampleData(r13, r0, r4, r14)
            r19 = 0
            int r12 = (r10 > r19 ? 1 : (r10 == r19 ? 0 : -1))
            if (r12 == 0) goto L_0x0213
            r15 = r31
            org.telegram.messenger.MediaController$VideoConvertorListener r12 = r15.callback
            if (r12 == 0) goto L_0x020f
            long r14 = r4.presentationTimeUs
            long r14 = r14 - r25
            int r12 = (r14 > r23 ? 1 : (r14 == r23 ? 0 : -1))
            if (r12 <= 0) goto L_0x01fd
            long r14 = r4.presentationTimeUs
            long r23 = r14 - r25
            r14 = r23
            goto L_0x01ff
        L_0x01fd:
            r14 = r23
        L_0x01ff:
            r12 = r31
            r17 = r0
            org.telegram.messenger.MediaController$VideoConvertorListener r0 = r12.callback
            float r3 = (float) r14
            float r3 = r3 / r16
            float r3 = r3 / r7
            r0.didWriteData(r10, r3)
            r23 = r14
            goto L_0x0217
        L_0x020f:
            r17 = r0
            r12 = r15
            goto L_0x0217
        L_0x0213:
            r12 = r31
            r17 = r0
        L_0x0217:
            goto L_0x021e
        L_0x0218:
            r19 = 0
            r12 = r31
            r17 = r0
        L_0x021e:
            if (r9 != 0) goto L_0x0223
            r32.advance()
        L_0x0223:
            r3 = -1
            goto L_0x023d
        L_0x0225:
            r17 = r0
            r28 = r9
            r22 = r10
            r30 = r11
            r27 = r12
            r19 = 0
            r12 = r31
            r3 = -1
            if (r1 != r3) goto L_0x0238
            r9 = 1
            goto L_0x023d
        L_0x0238:
            r32.advance()
            r9 = r28
        L_0x023d:
            if (r9 == 0) goto L_0x0242
            r0 = 1
            r18 = r0
        L_0x0242:
            r3 = r33
            r1 = r12
            r0 = r17
            r10 = r22
            r12 = r27
            r11 = r30
            r13 = -1
            goto L_0x00b8
        L_0x0251:
            r17 = r0
            r22 = r10
            r30 = r11
            if (r8 < 0) goto L_0x025c
            r2.unselectTrack(r8)
        L_0x025c:
            if (r30 < 0) goto L_0x0264
            r11 = r30
            r2.unselectTrack(r11)
            goto L_0x0266
        L_0x0264:
            r11 = r30
        L_0x0266:
            return r25
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.readAndWriteTracks(android.media.MediaExtractor, org.telegram.messenger.video.MP4Builder, android.media.MediaCodec$BufferInfo, long, long, long, java.io.File, boolean):long");
    }

    private void checkConversionCanceled() {
        MediaController.VideoConvertorListener videoConvertorListener = this.callback;
        if (videoConvertorListener != null && videoConvertorListener.checkConversionCanceled()) {
            throw new ConversionCanceledException();
        }
    }

    private static String createFragmentShader(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
        float kernelSizeX = Math.max(2.0f, ((float) srcWidth) / ((float) dstWidth));
        float kernelSizeY = Math.max(2.0f, ((float) srcHeight) / ((float) dstHeight));
        int kernelRadiusX = ((int) Math.ceil((double) (kernelSizeX - 0.1f))) / 2;
        int kernelRadiusY = ((int) Math.ceil((double) (kernelSizeY - 0.1f))) / 2;
        float stepX = (kernelSizeX / ((float) ((kernelRadiusX * 2) + 1))) * (1.0f / ((float) srcWidth));
        float stepY = (kernelSizeY / ((float) ((kernelRadiusY * 2) + 1))) * (1.0f / ((float) srcHeight));
        float sum = (float) (((kernelRadiusX * 2) + 1) * ((kernelRadiusY * 2) + 1));
        StringBuilder colorLoop = new StringBuilder();
        for (int i = -kernelRadiusX; i <= kernelRadiusX; i++) {
            for (int j = -kernelRadiusY; j <= kernelRadiusY; j++) {
                if (i != 0 || j != 0) {
                    colorLoop.append("      + texture2D(sTexture, vTextureCoord.xy + vec2(");
                    colorLoop.append(((float) i) * stepX);
                    colorLoop.append(", ");
                    colorLoop.append(((float) j) * stepY);
                    colorLoop.append("))\n");
                }
            }
        }
        return "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n    gl_FragColor = (texture2D(sTexture, vTextureCoord)\n" + colorLoop.toString() + "    ) / " + sum + ";\n}\n";
    }

    public class ConversionCanceledException extends RuntimeException {
        public ConversionCanceledException() {
            super("canceled conversion");
        }
    }
}
