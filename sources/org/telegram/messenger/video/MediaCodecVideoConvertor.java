package org.telegram.messenger.video;

import android.media.MediaExtractor;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.Utilities;
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

    public boolean convertVideo(String videoPath, File cacheFile, int rotationValue, boolean isSecret, int originalWidth, int originalHeight, int resultWidth, int resultHeight, int framerate, int bitrate, int originalBitrate, long startTime, long endTime, long avatarStartTime, boolean needCompress, long duration, MediaController.SavedFilterState savedFilterState, String paintPath, ArrayList<VideoEditedInfo.MediaEntity> mediaEntities, boolean isPhoto, MediaController.CropState cropState, boolean isRound, MediaController.VideoConvertorListener callback2) {
        long j = duration;
        this.callback = callback2;
        return convertVideoInternal(videoPath, cacheFile, rotationValue, isSecret, originalWidth, originalHeight, resultWidth, resultHeight, framerate, bitrate, originalBitrate, startTime, endTime, avatarStartTime, j, needCompress, false, savedFilterState, paintPath, mediaEntities, isPhoto, cropState, isRound);
    }

    public long getLastFrameTimestamp() {
        return this.endPresentationTime;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v1, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v2, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v3, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v4, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r108v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r108v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r108v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v26, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r79v0, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r102v1, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v5, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v6, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v7, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r102v5, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r102v7, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r102v12, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r102v13, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v62, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v72, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r102v14, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r102v15, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v20, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r102v16, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v106, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v21, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v22, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v23, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v24, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v25, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v26, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v50, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v27, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v51, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v28, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r102v18, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v30, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v57, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v32, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v34, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v58, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v35, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v59, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v60, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v36, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v61, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v62, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v37, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v40, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v38, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v39, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v40, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v63, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v41, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v80, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v64, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v80, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v46, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v47, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v76, resolved type: android.media.MediaFormat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v48, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v81, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v49, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v50, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v82, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v51, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v83, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v92, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v53, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v92, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v84, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v55, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v56, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v95, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v85, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v100, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v96, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v57, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v97, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v58, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v98, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v87, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v99, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v59, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v100, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v88, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v101, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v60, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v102, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v89, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v91, resolved type: org.telegram.messenger.MediaController$CropState} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v103, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v61, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v93, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v94, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v104, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v105, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v91, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v96, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v106, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v107, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v94, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v108, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v109, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v110, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v111, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v101, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r108v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r108v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v112, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v113, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v104, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r108v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v114, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v115, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v116, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v108, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v178, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v179, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v180, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v117, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v118, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v109, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v119, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v111, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v105, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v120, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v121, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v112, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v62, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v122, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v63, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v123, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v124, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v125, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v126, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v127, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v118, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v128, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v119, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v129, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v120, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v64, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v130, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v124, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v65, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v127, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v128, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v131, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v124, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v132, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v125, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v132, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v127, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v133, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v129, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v130, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v143, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v134, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v148, resolved type: android.media.MediaFormat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v135, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v136, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v137, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v138, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v158, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v139, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v159, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v140, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v160, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v144, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v141, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v161, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v146, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v147, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v148, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v114, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v124, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v150, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v153, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v154, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v158, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v159, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v160, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v161, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v167, resolved type: org.telegram.messenger.MediaController$CropState} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v130, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v131, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v132, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v133, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v134, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v135, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v136, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v137, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v138, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v139, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v140, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v141, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v142, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v143, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v144, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v145, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v146, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v147, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v148, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v149, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v150, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v151, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v152, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v153, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v154, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v155, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v156, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v157, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v158, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v159, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v160, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v161, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v162, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v163, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v164, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v165, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v166, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v167, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v168, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v169, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v170, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v171, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v172, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v173, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v170, resolved type: android.media.MediaFormat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v175, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v177, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v178, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v179, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v180, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v181, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v182, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v236, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v237, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v199, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v201, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r102v37, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r102v38, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r102v39, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r102v40, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v238, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v202, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v203, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v207, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v222, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v224, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v226, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v227, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r102v43, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v202, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v203, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v204, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v205, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v206, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v207, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v212, resolved type: android.media.MediaFormat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v213, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v214, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v215, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v216, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v217, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v218, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v219, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v221, resolved type: org.telegram.messenger.MediaController$CropState} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v222, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v223, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v225, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v163, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v164, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v166, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v171, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v172, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v174, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v176, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v177, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX WARNING: type inference failed for: r8v15 */
    /* JADX WARNING: type inference failed for: r7v16 */
    /* JADX WARNING: type inference failed for: r8v26 */
    /* JADX WARNING: type inference failed for: r8v27 */
    /* JADX WARNING: type inference failed for: r8v28 */
    /* JADX WARNING: type inference failed for: r8v29 */
    /* JADX WARNING: type inference failed for: r8v33 */
    /* JADX WARNING: type inference failed for: r8v37 */
    /* JADX WARNING: type inference failed for: r8v38 */
    /* JADX WARNING: type inference failed for: r13v58 */
    /* JADX WARNING: type inference failed for: r8v73 */
    /* JADX WARNING: type inference failed for: r8v82 */
    /* JADX WARNING: type inference failed for: r8v97 */
    /* JADX WARNING: type inference failed for: r8v144 */
    /* JADX WARNING: type inference failed for: r8v145 */
    /* JADX WARNING: type inference failed for: r8v146 */
    /* JADX WARNING: type inference failed for: r8v147 */
    /* JADX WARNING: type inference failed for: r8v148 */
    /* JADX WARNING: type inference failed for: r8v152 */
    /* JADX WARNING: type inference failed for: r8v153 */
    /* JADX WARNING: type inference failed for: r8v156 */
    /* JADX WARNING: type inference failed for: r8v157 */
    /* JADX WARNING: type inference failed for: r8v158 */
    /* JADX WARNING: type inference failed for: r8v159 */
    /* JADX WARNING: type inference failed for: r8v160 */
    /* JADX WARNING: type inference failed for: r8v161 */
    /* JADX WARNING: type inference failed for: r8v162 */
    /* JADX WARNING: type inference failed for: r6v162 */
    /* JADX WARNING: type inference failed for: r6v163 */
    /* JADX WARNING: type inference failed for: r6v164 */
    /* JADX WARNING: type inference failed for: r6v165 */
    /* JADX WARNING: type inference failed for: r6v166 */
    /* JADX WARNING: type inference failed for: r7v127 */
    /* JADX WARNING: type inference failed for: r7v128 */
    /* JADX WARNING: type inference failed for: r7v129 */
    /* JADX WARNING: type inference failed for: r8v163 */
    /* JADX WARNING: type inference failed for: r8v164 */
    /* JADX WARNING: type inference failed for: r8v165 */
    /* JADX WARNING: type inference failed for: r8v166 */
    /* JADX WARNING: type inference failed for: r8v167 */
    /* JADX WARNING: type inference failed for: r6v168 */
    /* JADX WARNING: type inference failed for: r6v169 */
    /* JADX WARNING: type inference failed for: r6v171 */
    /* JADX WARNING: type inference failed for: r8v168 */
    /* JADX WARNING: type inference failed for: r6v172 */
    /* JADX WARNING: type inference failed for: r8v169 */
    /* JADX WARNING: type inference failed for: r6v174 */
    /* JADX WARNING: type inference failed for: r8v171 */
    /* JADX WARNING: type inference failed for: r8v173 */
    /* JADX WARNING: type inference failed for: r8v174 */
    /* JADX WARNING: type inference failed for: r8v175 */
    /* JADX WARNING: type inference failed for: r8v176 */
    /* JADX WARNING: type inference failed for: r8v177 */
    /* JADX WARNING: type inference failed for: r8v178 */
    /* JADX WARNING: type inference failed for: r8v179 */
    /* JADX WARNING: type inference failed for: r8v180 */
    /* JADX WARNING: type inference failed for: r8v181 */
    /* JADX WARNING: type inference failed for: r8v182 */
    /* JADX WARNING: type inference failed for: r8v183 */
    /* JADX WARNING: type inference failed for: r6v176 */
    /* JADX WARNING: type inference failed for: r8v184 */
    /* JADX WARNING: type inference failed for: r8v185 */
    /* JADX WARNING: type inference failed for: r8v186 */
    /* JADX WARNING: type inference failed for: r8v187 */
    /* JADX WARNING: type inference failed for: r8v188 */
    /* JADX WARNING: type inference failed for: r8v190 */
    /* JADX WARNING: type inference failed for: r8v191 */
    /* JADX WARNING: type inference failed for: r8v195 */
    /* JADX WARNING: type inference failed for: r8v197 */
    /* JADX WARNING: type inference failed for: r8v198 */
    /* JADX WARNING: type inference failed for: r8v199 */
    /* JADX WARNING: type inference failed for: r6v183 */
    /* JADX WARNING: type inference failed for: r6v184 */
    /* JADX WARNING: type inference failed for: r6v185 */
    /* JADX WARNING: type inference failed for: r6v186 */
    /* JADX WARNING: type inference failed for: r6v187 */
    /* JADX WARNING: type inference failed for: r6v188 */
    /* JADX WARNING: type inference failed for: r6v189 */
    /* JADX WARNING: type inference failed for: r6v190 */
    /* JADX WARNING: type inference failed for: r6v191 */
    /* JADX WARNING: type inference failed for: r6v192 */
    /* JADX WARNING: type inference failed for: r6v193 */
    /* JADX WARNING: type inference failed for: r6v194 */
    /* JADX WARNING: type inference failed for: r6v195 */
    /* JADX WARNING: type inference failed for: r6v196 */
    /* JADX WARNING: type inference failed for: r6v197 */
    /* JADX WARNING: type inference failed for: r6v198 */
    /* JADX WARNING: type inference failed for: r6v199 */
    /* JADX WARNING: type inference failed for: r6v200 */
    /* JADX WARNING: type inference failed for: r6v201 */
    /* JADX WARNING: type inference failed for: r8v204 */
    /* JADX WARNING: type inference failed for: r8v205 */
    /* JADX WARNING: type inference failed for: r8v207 */
    /* JADX WARNING: type inference failed for: r8v208 */
    /* JADX WARNING: type inference failed for: r8v209 */
    /* JADX WARNING: type inference failed for: r8v210 */
    /* JADX WARNING: type inference failed for: r8v211 */
    /* JADX WARNING: type inference failed for: r8v212 */
    /* JADX WARNING: type inference failed for: r8v213 */
    /* JADX WARNING: type inference failed for: r8v220 */
    /* JADX WARNING: type inference failed for: r8v225 */
    /* JADX WARNING: type inference failed for: r6v211 */
    /* JADX WARNING: type inference failed for: r8v228 */
    /* JADX WARNING: type inference failed for: r8v229 */
    /* JADX WARNING: type inference failed for: r8v230 */
    /* JADX WARNING: type inference failed for: r8v231 */
    /* JADX WARNING: type inference failed for: r8v232 */
    /* JADX WARNING: type inference failed for: r8v233 */
    /* JADX WARNING: type inference failed for: r6v220 */
    /* JADX WARNING: Code restructure failed: missing block: B:1024:0x1723, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1025:0x1724, code lost:
        r26 = r102;
        r35 = r108;
        r3 = r0;
        r24 = r6;
        r1 = r7;
        r4 = r47;
        r18 = r55;
        r2 = r69;
        r25 = r78;
        r7 = r7;
        r6 = r6;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1082:0x1817, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1083:0x1818, code lost:
        r26 = r102;
        r35 = r108;
        r3 = r0;
        r25 = r78;
        r24 = r6;
        r1 = r7;
        r4 = r47;
        r18 = r55;
        r2 = r69;
        r7 = r7;
        r6 = r6;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1086:0x184f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1087:0x1850, code lost:
        r14 = r99;
        r10 = r100;
        r1 = r0;
        r5 = r55;
        r6 = r62;
        r7 = r69;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1088:0x185d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1089:0x185e, code lost:
        r26 = r102;
        r35 = r108;
        r3 = r0;
        r25 = r4;
        r24 = r6;
        r1 = r7;
        r4 = r47;
        r18 = r55;
        r2 = r69;
        r7 = r7;
        r6 = r6;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1090:0x1870, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1091:0x1871, code lost:
        r13 = r101;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1093:0x1880, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1094:0x1881, code lost:
        r13 = r101;
        r26 = r102;
        r35 = r108;
        r3 = r0;
        r25 = r78;
        r24 = r6;
        r1 = r7;
        r4 = r47;
        r18 = r5;
        r2 = r69;
        r7 = r7;
        r6 = r6;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1110:0x1955, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1111:0x1956, code lost:
        r14 = r99;
        r10 = r100;
        r1 = r0;
        r6 = r62;
        r7 = r69;
        r13 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1112:0x1961, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1113:0x1962, code lost:
        r26 = r102;
        r35 = r108;
        r3 = r0;
        r25 = r4;
        r18 = r5;
        r24 = r6;
        r1 = r7;
        r4 = r47;
        r2 = r69;
        r7 = r7;
        r8 = r8;
        r6 = r6;
        r13 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1114:0x1974, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1115:0x1975, code lost:
        r13 = r101;
        r66 = r10;
        r26 = r1;
        r35 = r108;
        r3 = r0;
        r25 = r78;
        r18 = r5;
        r24 = r6;
        r1 = r7;
        r4 = r47;
        r2 = r69;
        r7 = r7;
        r8 = r8;
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1116:0x198f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1117:0x1990, code lost:
        r13 = r101;
        r66 = r10;
        r14 = r99;
        r10 = r100;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1119:0x199f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1120:0x19a0, code lost:
        r13 = r101;
        r6 = r102;
        r66 = r10;
        r26 = r1;
        r35 = r108;
        r3 = r0;
        r25 = r78;
        r18 = r5;
        r24 = r6;
        r1 = r7;
        r4 = r47;
        r2 = r69;
        r7 = r7;
        r8 = r8;
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1126:0x19f7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1127:0x19f8, code lost:
        r13 = r101;
        r6 = r102;
        r37 = r14;
        r14 = r87;
        r26 = r1;
        r66 = r106;
        r35 = r2;
        r3 = r0;
        r25 = r78;
        r24 = r6;
        r1 = r7;
        r4 = r4;
        r2 = r69;
        r7 = r7;
        r8 = r8;
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1130:0x1a38, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1131:0x1a39, code lost:
        r13 = r101;
        r6 = r102;
        r37 = r14;
        r7 = r77;
        r14 = r87;
        r26 = r1;
        r66 = r106;
        r25 = r78;
        r24 = r6;
        r1 = r7;
        r2 = r69;
        r4 = r63;
        r3 = r0;
        r8 = r8;
        r6 = r6;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1132:0x1a58, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1133:0x1a59, code lost:
        r13 = r101;
        r14 = r99;
        r10 = r100;
        r66 = r106;
        r1 = r0;
        r5 = -5;
        r6 = r62;
        r7 = r69;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1134:0x1a6a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1135:0x1a6b, code lost:
        r13 = r101;
        r6 = r102;
        r37 = r14;
        r7 = r77;
        r14 = r87;
        r66 = r106;
        r25 = r78;
        r24 = r6;
        r1 = r7;
        r2 = r69;
        r4 = r63;
        r3 = r0;
        r8 = r8;
        r6 = r6;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1136:0x1a86, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1137:0x1a87, code lost:
        r60 = r108;
        r7 = r1;
        r37 = r14;
        r14 = r21;
        r66 = r106;
        r25 = r2;
        r24 = r6;
        r2 = r69;
        r4 = r63;
        r3 = r0;
        r8 = r8;
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1138:0x1a9d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1139:0x1a9e, code lost:
        r60 = r108;
        r37 = r14;
        r14 = r21;
        r66 = r106;
        r25 = r2;
        r24 = r6;
        r1 = r68;
        r2 = r69;
        r4 = r63;
        r3 = r0;
        r7 = r7;
        r8 = r8;
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1142:0x1aca, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1143:0x1acb, code lost:
        r60 = r108;
        r14 = r99;
        r10 = r100;
        r66 = r106;
        r1 = r0;
        r5 = -5;
        r6 = r62;
        r7 = r69;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1144:0x1adc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1145:0x1add, code lost:
        r60 = r108;
        r37 = r14;
        r14 = r21;
        r66 = r106;
        r4 = r63;
        r1 = r68;
        r2 = r69;
        r3 = r0;
        r7 = r7;
        r8 = r8;
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1148:0x1b00, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1149:0x1b01, code lost:
        r60 = r108;
        r14 = r99;
        r10 = r100;
        r66 = r106;
        r1 = r0;
        r7 = r2;
        r5 = -5;
        r6 = r62;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1186:0x1bc2, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1187:0x1bc3, code lost:
        r8 = r99;
        r7 = r100;
        r6 = r62;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1224:0x1CLASSNAME, code lost:
        r1.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1228:?, code lost:
        r1.finishMovie();
        r15.endPresentationTime = r15.mediaMuxer.getLastFrameTimestamp(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1229:0x1CLASSNAME, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1230:0x1CLASSNAME, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x0555, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x0556, code lost:
        r11 = r110;
        r1 = r0;
        r18 = r6;
        r24 = r10;
        r25 = r50;
        r10 = r56;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x060d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x060e, code lost:
        r11 = r110;
        r1 = r0;
        r25 = r50;
        r18 = r6;
        r24 = r10;
        r10 = r56;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x0675, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x0676, code lost:
        r11 = r110;
        r13 = r101;
        r66 = r106;
        r60 = r108;
        r1 = r0;
        r5 = r6;
        r7 = r52;
        r10 = r56;
        r6 = r57;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:273:0x0688, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:277:0x06b3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x06b4, code lost:
        r13 = r101;
        r66 = r106;
        r60 = r108;
        r1 = r0;
        r5 = r6;
        r7 = r52;
        r10 = r56;
        r6 = r57;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x06e4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x06e5, code lost:
        r15 = r92;
        r11 = r110;
        r13 = r101;
        r66 = r106;
        r60 = r108;
        r1 = r0;
        r5 = r6;
        r7 = r52;
        r10 = r56;
        r6 = r57;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x06f9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x06fa, code lost:
        r15 = r92;
        r11 = r110;
        r13 = r20;
        r8 = r53;
        r1 = r0;
        r25 = r50;
        r18 = r6;
        r24 = r99;
        r10 = r56;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x0759, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x075a, code lost:
        r15 = r92;
        r11 = r2;
        r52 = r7;
        r56 = r13;
        r57 = r19;
        r13 = r20;
        r1 = r0;
        r25 = r6;
        r24 = r9;
        r10 = r56;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x0772, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x0773, code lost:
        r15 = r92;
        r11 = r2;
        r52 = r7;
        r56 = r13;
        r57 = r19;
        r13 = r20;
        r1 = r0;
        r24 = r9;
        r10 = r56;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x0788, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x0789, code lost:
        r15 = r92;
        r11 = r2;
        r52 = r7;
        r56 = r13;
        r13 = r101;
        r66 = r106;
        r60 = r108;
        r1 = r0;
        r5 = -5;
        r6 = r19;
        r10 = r56;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x0a83, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x0a84, code lost:
        r14 = r99;
        r10 = r100;
        r66 = r106;
        r60 = r108;
        r1 = r0;
        r7 = r2;
        r5 = -5;
        r6 = r62;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x0a94, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x0a95, code lost:
        r66 = r106;
        r60 = r108;
        r3 = r0;
        r37 = r14;
        r14 = r21;
        r4 = r63;
        r7 = r7;
        r8 = r8;
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x0bdc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x0bdd, code lost:
        r13 = r101;
        r66 = r106;
        r1 = r0;
        r10 = r3;
        r14 = r4;
        r5 = -5;
        r6 = r62;
        r7 = r69;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x0bec, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x0bed, code lost:
        r13 = r101;
        r24 = r102;
        r66 = r106;
        r3 = r0;
        r26 = r1;
        r37 = r14;
        r4 = r63;
        r2 = r69;
        r1 = r77;
        r25 = r78;
        r14 = r87;
        r7 = r7;
        r8 = r8;
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x0cd9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x0cda, code lost:
        r13 = r101;
        r24 = r102;
        r66 = r106;
        r3 = r0;
        r26 = r1;
        r1 = r7;
        r4 = r11;
        r37 = r14;
        r2 = r69;
        r25 = r78;
        r14 = r87;
        r7 = r7;
        r6 = r6;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x0d1b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x0d95, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:588:0x0d96, code lost:
        r13 = r101;
        r24 = r102;
        r66 = r106;
        r3 = r0;
        r26 = r1;
        r4 = r6;
        r1 = r7;
        r37 = r14;
        r2 = r69;
        r25 = r78;
        r14 = r87;
        r7 = r7;
        r6 = r6;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x0dee, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0def, code lost:
        r14 = r99;
        r13 = r101;
        r1 = r0;
        r66 = r10;
        r5 = -5;
        r6 = r62;
        r7 = r69;
        r10 = r100;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x0e00, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x0e02, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x0e03, code lost:
        r10 = r106;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x0e05, code lost:
        r26 = r3;
        r13 = r101;
        r24 = r102;
        r3 = r0;
        r4 = r6;
        r66 = r10;
        r37 = r14;
        r35 = r26;
        r2 = r69;
        r25 = r78;
        r14 = r87;
        r26 = r1;
        r1 = r7;
        r7 = r7;
        r6 = r6;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x0e1e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x0e1f, code lost:
        r14 = r99;
        r13 = r101;
        r1 = r0;
        r66 = r106;
        r5 = -5;
        r6 = r62;
        r7 = r69;
        r10 = r100;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0var_, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0var_, code lost:
        r14 = r99;
        r13 = r101;
        r1 = r0;
        r66 = r10;
        r5 = r21;
        r6 = r62;
        r7 = r69;
        r10 = r100;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0var_, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x0var_, code lost:
        r13 = r101;
        r24 = r102;
        r3 = r0;
        r26 = r1;
        r35 = r2;
        r1 = r7;
        r66 = r10;
        r37 = r14;
        r18 = r21;
        r2 = r69;
        r25 = r78;
        r14 = r87;
        r7 = r7;
        r8 = r8;
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x1010, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x1011, code lost:
        r24 = r102;
        r35 = r108;
        r3 = r0;
        r26 = r1;
        r1 = r7;
        r66 = r10;
        r38 = r13;
        r18 = r21;
        r2 = r69;
        r25 = r78;
        r13 = r101;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x1084, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x1085, code lost:
        r24 = r102;
        r35 = r108;
        r3 = r0;
        r26 = r1;
        r1 = r7;
        r66 = r10;
        r38 = r13;
        r18 = r21;
        r4 = r47;
        r2 = r69;
        r25 = r78;
        r13 = r101;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x10c9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x10ca, code lost:
        r24 = r102;
        r35 = r108;
        r3 = r0;
        r26 = r1;
        r1 = r7;
        r38 = r13;
        r18 = r21;
        r4 = r47;
        r66 = r10;
        r2 = r69;
        r25 = r78;
        r13 = r101;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x10fd, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x10fe, code lost:
        r47 = r4;
        r24 = r102;
        r35 = r108;
        r3 = r0;
        r26 = r1;
        r1 = r7;
        r38 = r13;
        r18 = r21;
        r66 = r10;
        r2 = r69;
        r25 = r78;
        r13 = r101;
        r7 = r7;
        r8 = r8;
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x117b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x117c, code lost:
        r14 = r99;
        r10 = r100;
        r13 = r101;
        r1 = r0;
        r5 = r21;
        r66 = r51;
        r6 = r62;
        r7 = r69;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x118d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x118e, code lost:
        r13 = r101;
        r24 = r102;
        r35 = r108;
        r3 = r0;
        r26 = r1;
        r1 = r7;
        r18 = r21;
        r4 = r47;
        r66 = r51;
        r2 = r69;
        r25 = r78;
        r7 = r7;
        r8 = r8;
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x11a4, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x11a5, code lost:
        r51 = r10;
        r14 = r99;
        r10 = r100;
        r13 = r101;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x127b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x127c, code lost:
        r14 = r99;
        r13 = r101;
        r1 = r0;
        r66 = r10;
        r6 = r62;
        r7 = r69;
        r10 = r100;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x128b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x128c, code lost:
        r13 = r101;
        r26 = r1;
        r35 = r108;
        r3 = r0;
        r18 = r5;
        r24 = r6;
        r1 = r7;
        r66 = r10;
        r4 = r47;
        r2 = r69;
        r25 = r78;
        r7 = r7;
        r8 = r8;
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x025d, code lost:
        r18 = r6;
        r42 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x143b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x143c, code lost:
        r13 = r101;
        r26 = r102;
        r35 = r108;
        r31 = r2;
        r18 = r5;
        r24 = r6;
        r1 = r7;
        r4 = r47;
        r2 = r69;
        r25 = r78;
        r3 = r0;
        r7 = r7;
        r6 = r6;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x15e0, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x15e1, code lost:
        r14 = r99;
        r10 = r100;
        r13 = r101;
        r1 = r0;
        r6 = r62;
        r7 = r69;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1018:0x1713 A[SYNTHETIC, Splitter:B:1018:0x1713] */
    /* JADX WARNING: Removed duplicated region for block: B:1028:0x173c A[Catch:{ Exception -> 0x1723, all -> 0x184f }] */
    /* JADX WARNING: Removed duplicated region for block: B:1029:0x173f A[Catch:{ Exception -> 0x1723, all -> 0x184f }] */
    /* JADX WARNING: Removed duplicated region for block: B:1034:0x174f A[Catch:{ Exception -> 0x1723, all -> 0x184f }] */
    /* JADX WARNING: Removed duplicated region for block: B:1043:0x178c A[Catch:{ Exception -> 0x1723, all -> 0x184f }] */
    /* JADX WARNING: Removed duplicated region for block: B:1045:0x1790 A[Catch:{ Exception -> 0x1723, all -> 0x184f }] */
    /* JADX WARNING: Removed duplicated region for block: B:1046:0x1795 A[Catch:{ Exception -> 0x1723, all -> 0x184f }] */
    /* JADX WARNING: Removed duplicated region for block: B:1054:0x17ac A[Catch:{ Exception -> 0x1817, all -> 0x184f }] */
    /* JADX WARNING: Removed duplicated region for block: B:1074:0x17ee A[Catch:{ Exception -> 0x185d, all -> 0x184f }] */
    /* JADX WARNING: Removed duplicated region for block: B:1077:0x17ff A[Catch:{ Exception -> 0x185d, all -> 0x184f }] */
    /* JADX WARNING: Removed duplicated region for block: B:1081:0x1811 A[Catch:{ Exception -> 0x185d, all -> 0x184f }] */
    /* JADX WARNING: Removed duplicated region for block: B:1086:0x184f A[ExcHandler: all (r0v41 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r13 r55 r60 r66 
      PHI: (r13v42 'resultHeight' int) = (r13v44 'resultHeight' int), (r13v44 'resultHeight' int), (r13v44 'resultHeight' int), (r13v44 'resultHeight' int), (r13v44 'resultHeight' int), (r13v44 'resultHeight' int), (r13v44 'resultHeight' int), (r13v196 'resultHeight' int), (r13v198 'resultHeight' int) binds: [B:1062:0x17c3, B:1071:0x17df, B:1066:0x17ca, B:1063:?, B:1057:0x17b6, B:1051:0x17a7, B:1018:0x1713, B:994:0x167c, B:995:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r55v6 'videoTrackIndex' int) = (r55v8 'videoTrackIndex' int), (r55v8 'videoTrackIndex' int), (r55v8 'videoTrackIndex' int), (r55v8 'videoTrackIndex' int), (r55v8 'videoTrackIndex' int), (r55v8 'videoTrackIndex' int), (r55v8 'videoTrackIndex' int), (r55v14 'videoTrackIndex' int), (r55v14 'videoTrackIndex' int) binds: [B:1062:0x17c3, B:1071:0x17df, B:1066:0x17ca, B:1063:?, B:1057:0x17b6, B:1051:0x17a7, B:1018:0x1713, B:994:0x167c, B:995:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r60v24 'avatarStartTime' long) = (r60v26 'avatarStartTime' long), (r60v26 'avatarStartTime' long), (r60v26 'avatarStartTime' long), (r60v26 'avatarStartTime' long), (r60v26 'avatarStartTime' long), (r60v26 'avatarStartTime' long), (r60v26 'avatarStartTime' long), (r60v21 'avatarStartTime' long), (r60v21 'avatarStartTime' long) binds: [B:1062:0x17c3, B:1071:0x17df, B:1066:0x17ca, B:1063:?, B:1057:0x17b6, B:1051:0x17a7, B:1018:0x1713, B:994:0x167c, B:995:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r66v38 'endTime' long) = (r66v40 'endTime' long), (r66v40 'endTime' long), (r66v40 'endTime' long), (r66v40 'endTime' long), (r66v40 'endTime' long), (r66v40 'endTime' long), (r66v40 'endTime' long), (r66v35 'endTime' long), (r66v35 'endTime' long) binds: [B:1062:0x17c3, B:1071:0x17df, B:1066:0x17ca, B:1063:?, B:1057:0x17b6, B:1051:0x17a7, B:1018:0x1713, B:994:0x167c, B:995:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:994:0x167c] */
    /* JADX WARNING: Removed duplicated region for block: B:1090:0x1870 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:937:0x1577] */
    /* JADX WARNING: Removed duplicated region for block: B:1110:0x1955 A[ExcHandler: all (r0v35 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r5 r13 r66 
      PHI: (r5v32 'videoTrackIndex' int) = (r5v35 'videoTrackIndex' int), (r5v35 'videoTrackIndex' int), (r5v31 'videoTrackIndex' int), (r5v31 'videoTrackIndex' int) binds: [B:986:0x1654, B:987:?, B:826:0x137d, B:1103:0x18fc] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r13v33 int) = (r13v199 int), (r13v201 int), (r13v203 java.lang.String), (r13v207 int) binds: [B:986:0x1654, B:987:?, B:826:0x137d, B:1103:0x18fc] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r66v32 'endTime' long) = (r66v35 'endTime' long), (r66v35 'endTime' long), (r66v27 'endTime' long), (r66v48 'endTime' long) binds: [B:986:0x1654, B:987:?, B:826:0x137d, B:1103:0x18fc] A[DONT_GENERATE, DONT_INLINE], Splitter:B:986:0x1654] */
    /* JADX WARNING: Removed duplicated region for block: B:1116:0x198f A[ExcHandler: all (th java.lang.Throwable), Splitter:B:770:0x120a] */
    /* JADX WARNING: Removed duplicated region for block: B:1132:0x1a58 A[ExcHandler: all (r0v24 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:491:0x0bac] */
    /* JADX WARNING: Removed duplicated region for block: B:1142:0x1aca A[ExcHandler: all (r0v19 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:480:0x0b43] */
    /* JADX WARNING: Removed duplicated region for block: B:1148:0x1b00 A[ExcHandler: all (r0v16 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r108 
      PHI: (r108v2 'avatarStartTime' long) = (r108v3 'avatarStartTime' long), (r108v3 'avatarStartTime' long), (r108v3 'avatarStartTime' long), (r108v3 'avatarStartTime' long), (r108v12 'avatarStartTime' long), (r108v12 'avatarStartTime' long) binds: [B:450:0x0aad, B:451:?, B:459:0x0ae2, B:460:?, B:433:0x0a5d, B:434:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:450:0x0aad] */
    /* JADX WARNING: Removed duplicated region for block: B:1179:0x1baf A[Catch:{ all -> 0x1bb6, all -> 0x1be9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1191:0x1be5 A[Catch:{ all -> 0x1bb6, all -> 0x1be9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1195:0x1bf5 A[Catch:{ all -> 0x1bb6, all -> 0x1be9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1197:0x1bfa A[Catch:{ all -> 0x1bb6, all -> 0x1be9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1199:0x1CLASSNAME A[Catch:{ all -> 0x1bb6, all -> 0x1be9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1204:0x1c0e  */
    /* JADX WARNING: Removed duplicated region for block: B:1207:0x1CLASSNAME A[SYNTHETIC, Splitter:B:1207:0x1CLASSNAME] */
    /* JADX WARNING: Removed duplicated region for block: B:1224:0x1CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1227:0x1CLASSNAME A[SYNTHETIC, Splitter:B:1227:0x1CLASSNAME] */
    /* JADX WARNING: Removed duplicated region for block: B:1233:0x1ca5  */
    /* JADX WARNING: Removed duplicated region for block: B:1235:0x1cdc  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x0592 A[Catch:{ Exception -> 0x060d, all -> 0x0675 }] */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x0594 A[Catch:{ Exception -> 0x060d, all -> 0x0675 }] */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0675 A[Catch:{ Exception -> 0x06c4, all -> 0x06b3 }, ExcHandler: all (r0v141 'th' java.lang.Throwable A[CUSTOM_DECLARE, Catch:{ Exception -> 0x06c4, all -> 0x06b3 }]), PHI: r6 r15 r56 
      PHI: (r6v127 'videoTrackIndex' int) = (r6v129 'videoTrackIndex' int), (r6v129 'videoTrackIndex' int), (r6v122 'videoTrackIndex' int), (r6v122 'videoTrackIndex' int), (r6v130 'videoTrackIndex' int), (r6v122 'videoTrackIndex' int) binds: [B:255:0x05d6, B:256:?, B:135:0x0383, B:136:?, B:243:0x058c, B:143:0x039e] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r15v31 org.telegram.messenger.video.MediaCodecVideoConvertor) = (r15v32 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v32 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v37 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v37 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v37 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v37 org.telegram.messenger.video.MediaCodecVideoConvertor) binds: [B:255:0x05d6, B:256:?, B:135:0x0383, B:136:?, B:243:0x058c, B:143:0x039e] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r56v30 'resultHeight' int) = (r56v31 'resultHeight' int), (r56v31 'resultHeight' int), (r56v27 'resultHeight' int), (r56v27 'resultHeight' int), (r56v32 'resultHeight' int), (r56v27 'resultHeight' int) binds: [B:255:0x05d6, B:256:?, B:135:0x0383, B:136:?, B:243:0x058c, B:143:0x039e] A[DONT_GENERATE, DONT_INLINE], Splitter:B:135:0x0383] */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x06b3 A[ExcHandler: all (r0v140 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r6 r15 r56 
      PHI: (r6v125 'videoTrackIndex' int) = (r6v129 'videoTrackIndex' int), (r6v129 'videoTrackIndex' int), (r6v122 'videoTrackIndex' int), (r6v130 'videoTrackIndex' int), (r6v122 'videoTrackIndex' int), (r6v122 'videoTrackIndex' int), (r6v122 'videoTrackIndex' int), (r6v122 'videoTrackIndex' int), (r6v122 'videoTrackIndex' int), (r6v122 'videoTrackIndex' int), (r6v122 'videoTrackIndex' int), (r6v122 'videoTrackIndex' int), (r6v122 'videoTrackIndex' int) binds: [B:255:0x05d6, B:260:0x05f5, B:135:0x0383, B:243:0x058c, B:149:0x03ab, B:150:?, B:229:0x0529, B:184:0x0429, B:190:0x044f, B:154:0x03b3, B:139:0x038b, B:140:?, B:116:0x030b] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r15v30 org.telegram.messenger.video.MediaCodecVideoConvertor) = (r15v32 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v32 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v37 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v37 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v37 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v37 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v37 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v37 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v37 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v37 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v37 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v37 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v40 org.telegram.messenger.video.MediaCodecVideoConvertor) binds: [B:255:0x05d6, B:260:0x05f5, B:135:0x0383, B:243:0x058c, B:149:0x03ab, B:150:?, B:229:0x0529, B:184:0x0429, B:190:0x044f, B:154:0x03b3, B:139:0x038b, B:140:?, B:116:0x030b] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r56v29 'resultHeight' int) = (r56v31 'resultHeight' int), (r56v31 'resultHeight' int), (r56v27 'resultHeight' int), (r56v32 'resultHeight' int), (r56v27 'resultHeight' int), (r56v27 'resultHeight' int), (r56v36 'resultHeight' int), (r56v27 'resultHeight' int), (r56v27 'resultHeight' int), (r56v27 'resultHeight' int), (r56v27 'resultHeight' int), (r56v27 'resultHeight' int), (r56v27 'resultHeight' int) binds: [B:255:0x05d6, B:260:0x05f5, B:135:0x0383, B:243:0x058c, B:149:0x03ab, B:150:?, B:229:0x0529, B:184:0x0429, B:190:0x044f, B:154:0x03b3, B:139:0x038b, B:140:?, B:116:0x030b] A[DONT_GENERATE, DONT_INLINE], Splitter:B:184:0x0429] */
    /* JADX WARNING: Removed duplicated region for block: B:284:0x06e4 A[ExcHandler: all (r0v136 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:85:0x0262] */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0788 A[ExcHandler: all (r0v130 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:48:0x0156] */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x0836 A[Catch:{ all -> 0x0856 }] */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x083b A[Catch:{ all -> 0x0856 }] */
    /* JADX WARNING: Removed duplicated region for block: B:329:0x0840 A[Catch:{ all -> 0x0856 }] */
    /* JADX WARNING: Removed duplicated region for block: B:445:0x0a83 A[ExcHandler: all (r0v109 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:441:0x0a6f] */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0bb3  */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x0bdc A[ExcHandler: all (r0v100 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r3 r4 
      PHI: (r3v128 int) = (r3v34 int), (r3v34 int), (r3v34 int), (r3v34 int), (r3v34 int), (r3v34 int), (r3v129 int), (r3v129 int) binds: [B:585:0x0d91, B:586:?, B:556:0x0d15, B:568:0x0d35, B:538:0x0cca, B:528:0x0CLASSNAME, B:504:0x0bce, B:505:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r4v152 int) = (r4v31 int), (r4v31 int), (r4v31 int), (r4v31 int), (r4v31 int), (r4v31 int), (r4v163 int), (r4v163 int) binds: [B:585:0x0d91, B:586:?, B:556:0x0d15, B:568:0x0d35, B:538:0x0cca, B:528:0x0CLASSNAME, B:504:0x0bce, B:505:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:504:0x0bce] */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x0CLASSNAME A[SYNTHETIC, Splitter:B:528:0x0CLASSNAME] */
    /* JADX WARNING: Removed duplicated region for block: B:535:0x0cb0 A[SYNTHETIC, Splitter:B:535:0x0cb0] */
    /* JADX WARNING: Removed duplicated region for block: B:549:0x0cfe  */
    /* JADX WARNING: Removed duplicated region for block: B:550:0x0d00  */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x0d03  */
    /* JADX WARNING: Removed duplicated region for block: B:604:0x0dee A[ExcHandler: all (r0v93 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:596:0x0dbc] */
    /* JADX WARNING: Removed duplicated region for block: B:610:0x0e1e A[ExcHandler: all (r0v89 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:580:0x0d84] */
    /* JADX WARNING: Removed duplicated region for block: B:618:0x0e7a  */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0ebf  */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0ed5  */
    /* JADX WARNING: Removed duplicated region for block: B:626:0x0ed7  */
    /* JADX WARNING: Removed duplicated region for block: B:632:0x0ee6 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0f0b A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:642:0x0var_ A[ExcHandler: all (r0v81 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:640:0x0f0f] */
    /* JADX WARNING: Removed duplicated region for block: B:648:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x1006  */
    /* JADX WARNING: Removed duplicated region for block: B:697:0x102b A[SYNTHETIC, Splitter:B:697:0x102b] */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x1039  */
    /* JADX WARNING: Removed duplicated region for block: B:704:0x1042  */
    /* JADX WARNING: Removed duplicated region for block: B:739:0x10f3  */
    /* JADX WARNING: Removed duplicated region for block: B:757:0x117b A[ExcHandler: all (r0v71 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r51 
      PHI: (r51v20 'endTime' long) = (r51v21 'endTime' long), (r51v32 'endTime' long), (r51v32 'endTime' long) binds: [B:752:0x1161, B:729:0x10a1, B:730:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:729:0x10a1] */
    /* JADX WARNING: Removed duplicated region for block: B:761:0x11a4 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:682:0x0ff5] */
    /* JADX WARNING: Removed duplicated region for block: B:765:0x11cc  */
    /* JADX WARNING: Removed duplicated region for block: B:768:0x11ed A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:773:0x120f  */
    /* JADX WARNING: Removed duplicated region for block: B:774:0x1217  */
    /* JADX WARNING: Removed duplicated region for block: B:780:0x1227  */
    /* JADX WARNING: Removed duplicated region for block: B:781:0x1241  */
    /* JADX WARNING: Removed duplicated region for block: B:790:0x127b A[Catch:{ Exception -> 0x135c, all -> 0x127b }, ExcHandler: all (r0v67 'th' java.lang.Throwable A[CUSTOM_DECLARE, Catch:{ Exception -> 0x135c, all -> 0x127b }]), Splitter:B:783:0x1244] */
    /* JADX WARNING: Removed duplicated region for block: B:877:0x141a A[Catch:{ Exception -> 0x15ee, all -> 0x15e0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:891:0x1455  */
    /* JADX WARNING: Removed duplicated region for block: B:928:0x1541 A[Catch:{ Exception -> 0x18d9, all -> 0x18c9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:929:0x1543 A[Catch:{ Exception -> 0x18d9, all -> 0x18c9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:934:0x1553  */
    /* JADX WARNING: Removed duplicated region for block: B:935:0x1571  */
    /* JADX WARNING: Removed duplicated region for block: B:952:0x15e0 A[ExcHandler: all (r0v37 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r5 r66 
      PHI: (r5v34 'videoTrackIndex' int) = (r5v35 'videoTrackIndex' int), (r5v35 'videoTrackIndex' int), (r5v35 'videoTrackIndex' int), (r5v31 'videoTrackIndex' int), (r5v31 'videoTrackIndex' int), (r5v31 'videoTrackIndex' int), (r5v31 'videoTrackIndex' int) binds: [B:968:0x161f, B:969:?, B:947:0x15b0, B:895:0x146c, B:885:0x142e, B:886:?, B:856:0x13d3] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r66v34 'endTime' long) = (r66v35 'endTime' long), (r66v35 'endTime' long), (r66v35 'endTime' long), (r66v54 'endTime' long), (r66v55 'endTime' long), (r66v55 'endTime' long), (r66v59 'endTime' long) binds: [B:968:0x161f, B:969:?, B:947:0x15b0, B:895:0x146c, B:885:0x142e, B:886:?, B:856:0x13d3] A[DONT_GENERATE, DONT_INLINE], Splitter:B:856:0x13d3] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideoInternal(java.lang.String r93, java.io.File r94, int r95, boolean r96, int r97, int r98, int r99, int r100, int r101, int r102, int r103, long r104, long r106, long r108, long r110, boolean r112, boolean r113, org.telegram.messenger.MediaController.SavedFilterState r114, java.lang.String r115, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r116, boolean r117, org.telegram.messenger.MediaController.CropState r118, boolean r119) {
        /*
            r92 = this;
            r15 = r92
            r14 = r93
            r13 = r95
            r12 = r97
            r11 = r98
            r9 = r99
            r10 = r100
            r8 = r101
            r7 = r102
            r6 = r103
            r4 = r104
            r2 = r110
            r1 = r112
            java.lang.String r13 = "bitrate: "
            long r29 = java.lang.System.currentTimeMillis()
            r16 = 0
            r17 = 0
            r18 = -5
            android.media.MediaCodec$BufferInfo r19 = new android.media.MediaCodec$BufferInfo     // Catch:{ all -> 0x1CLASSNAME }
            r19.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r20 = r19
            org.telegram.messenger.video.Mp4Movie r19 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ all -> 0x1CLASSNAME }
            r19.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r21 = r19
            r19 = r13
            r6 = r21
            r13 = r94
            r6.setCacheFile(r13)     // Catch:{ all -> 0x1CLASSNAME }
            r13 = 0
            r6.setRotation(r13)     // Catch:{ all -> 0x1CLASSNAME }
            r6.setSize(r9, r10)     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.messenger.video.MP4Builder r13 = new org.telegram.messenger.video.MP4Builder     // Catch:{ all -> 0x1CLASSNAME }
            r13.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r14 = r96
            org.telegram.messenger.video.MP4Builder r13 = r13.createMovie(r6, r14)     // Catch:{ all -> 0x1CLASSNAME }
            r15.mediaMuxer = r13     // Catch:{ all -> 0x1CLASSNAME }
            r13 = 0
            float r1 = (float) r2     // Catch:{ all -> 0x1CLASSNAME }
            r22 = 1148846080(0x447a0000, float:1000.0)
            float r23 = r1 / r22
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r31 = 1000(0x3e8, double:4.94E-321)
            long r4 = r2 * r31
            r15.endPresentationTime = r4     // Catch:{ all -> 0x1CLASSNAME }
            r92.checkConversionCanceled()     // Catch:{ all -> 0x1CLASSNAME }
            java.lang.String r5 = "csd-1"
            java.lang.String r1 = "csd-0"
            r31 = r13
            java.lang.String r13 = "prepend-sps-pps-to-idr-frames"
            java.lang.String r15 = "video/avc"
            r35 = r13
            r13 = 0
            r39 = r6
            r6 = 1
            if (r117 == 0) goto L_0x088d
            r40 = 0
            r41 = 0
            r42 = 0
            int r43 = (r108 > r13 ? 1 : (r108 == r13 ? 0 : -1))
            if (r43 < 0) goto L_0x009f
            r43 = 1157234688(0x44fa0000, float:2000.0)
            int r43 = (r23 > r43 ? 1 : (r23 == r43 ? 0 : -1))
            if (r43 > 0) goto L_0x0090
            r7 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x00a4
        L_0x0090:
            r43 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r43 = (r23 > r43 ? 1 : (r23 == r43 ? 0 : -1))
            if (r43 > 0) goto L_0x009b
            r7 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x00a4
        L_0x009b:
            r7 = 1560000(0x17cdc0, float:2.186026E-39)
            goto L_0x00a4
        L_0x009f:
            if (r7 > 0) goto L_0x00a4
            r7 = 921600(0xe1000, float:1.291437E-39)
        L_0x00a4:
            int r43 = r9 % 16
            r44 = 1098907648(0x41800000, float:16.0)
            if (r43 == 0) goto L_0x00fe
            boolean r43 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00ee, all -> 0x00de }
            if (r43 == 0) goto L_0x00d3
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00ee, all -> 0x00de }
            r4.<init>()     // Catch:{ Exception -> 0x00ee, all -> 0x00de }
            java.lang.String r13 = "changing width from "
            r4.append(r13)     // Catch:{ Exception -> 0x00ee, all -> 0x00de }
            r4.append(r9)     // Catch:{ Exception -> 0x00ee, all -> 0x00de }
            java.lang.String r13 = " to "
            r4.append(r13)     // Catch:{ Exception -> 0x00ee, all -> 0x00de }
            float r13 = (float) r9     // Catch:{ Exception -> 0x00ee, all -> 0x00de }
            float r13 = r13 / r44
            int r13 = java.lang.Math.round(r13)     // Catch:{ Exception -> 0x00ee, all -> 0x00de }
            int r13 = r13 * 16
            r4.append(r13)     // Catch:{ Exception -> 0x00ee, all -> 0x00de }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x00ee, all -> 0x00de }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x00ee, all -> 0x00de }
        L_0x00d3:
            float r4 = (float) r9     // Catch:{ Exception -> 0x00ee, all -> 0x00de }
            float r4 = r4 / r44
            int r4 = java.lang.Math.round(r4)     // Catch:{ Exception -> 0x00ee, all -> 0x00de }
            int r4 = r4 * 16
            r14 = r4
            goto L_0x00ff
        L_0x00de:
            r0 = move-exception
            r15 = r92
            r66 = r106
            r60 = r108
            r1 = r0
            r13 = r8
            r14 = r9
            r5 = r18
            r6 = r19
            goto L_0x1c4f
        L_0x00ee:
            r0 = move-exception
            r15 = r92
            r1 = r0
            r11 = r2
            r52 = r7
            r14 = r9
            r57 = r19
            r13 = r20
            r8 = r31
            goto L_0x07f6
        L_0x00fe:
            r14 = r9
        L_0x00ff:
            int r4 = r10 % 16
            if (r4 == 0) goto L_0x0155
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0146, all -> 0x0137 }
            if (r4 == 0) goto L_0x012c
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0146, all -> 0x0137 }
            r4.<init>()     // Catch:{ Exception -> 0x0146, all -> 0x0137 }
            java.lang.String r9 = "changing height from "
            r4.append(r9)     // Catch:{ Exception -> 0x0146, all -> 0x0137 }
            r4.append(r10)     // Catch:{ Exception -> 0x0146, all -> 0x0137 }
            java.lang.String r9 = " to "
            r4.append(r9)     // Catch:{ Exception -> 0x0146, all -> 0x0137 }
            float r9 = (float) r10     // Catch:{ Exception -> 0x0146, all -> 0x0137 }
            float r9 = r9 / r44
            int r9 = java.lang.Math.round(r9)     // Catch:{ Exception -> 0x0146, all -> 0x0137 }
            int r9 = r9 * 16
            r4.append(r9)     // Catch:{ Exception -> 0x0146, all -> 0x0137 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0146, all -> 0x0137 }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x0146, all -> 0x0137 }
        L_0x012c:
            float r4 = (float) r10     // Catch:{ Exception -> 0x0146, all -> 0x0137 }
            float r4 = r4 / r44
            int r4 = java.lang.Math.round(r4)     // Catch:{ Exception -> 0x0146, all -> 0x0137 }
            int r4 = r4 * 16
            r13 = r4
            goto L_0x0156
        L_0x0137:
            r0 = move-exception
            r15 = r92
            r66 = r106
            r60 = r108
            r1 = r0
            r13 = r8
            r5 = r18
            r6 = r19
            goto L_0x1c4f
        L_0x0146:
            r0 = move-exception
            r15 = r92
            r1 = r0
            r11 = r2
            r52 = r7
            r57 = r19
            r13 = r20
            r8 = r31
            goto L_0x07f6
        L_0x0155:
            r13 = r10
        L_0x0156:
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x079f, all -> 0x0788 }
            if (r4 == 0) goto L_0x019f
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x018f, all -> 0x017f }
            r4.<init>()     // Catch:{ Exception -> 0x018f, all -> 0x017f }
            java.lang.String r9 = "create photo encoder "
            r4.append(r9)     // Catch:{ Exception -> 0x018f, all -> 0x017f }
            r4.append(r14)     // Catch:{ Exception -> 0x018f, all -> 0x017f }
            java.lang.String r9 = " "
            r4.append(r9)     // Catch:{ Exception -> 0x018f, all -> 0x017f }
            r4.append(r13)     // Catch:{ Exception -> 0x018f, all -> 0x017f }
            java.lang.String r9 = " duration = "
            r4.append(r9)     // Catch:{ Exception -> 0x018f, all -> 0x017f }
            r4.append(r2)     // Catch:{ Exception -> 0x018f, all -> 0x017f }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x018f, all -> 0x017f }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x018f, all -> 0x017f }
            goto L_0x019f
        L_0x017f:
            r0 = move-exception
            r15 = r92
            r66 = r106
            r60 = r108
            r1 = r0
            r10 = r13
            r5 = r18
            r6 = r19
            r13 = r8
            goto L_0x1c4f
        L_0x018f:
            r0 = move-exception
            r15 = r92
            r1 = r0
            r11 = r2
            r52 = r7
            r10 = r13
            r57 = r19
            r13 = r20
            r8 = r31
            goto L_0x07f6
        L_0x019f:
            android.media.MediaFormat r4 = android.media.MediaFormat.createVideoFormat(r15, r14, r13)     // Catch:{ Exception -> 0x079f, all -> 0x0788 }
            r10 = r4
            java.lang.String r4 = "color-format"
            r9 = 2130708361(0x7var_, float:1.701803E38)
            r10.setInteger(r4, r9)     // Catch:{ Exception -> 0x079f, all -> 0x0788 }
            java.lang.String r4 = "bitrate"
            r10.setInteger(r4, r7)     // Catch:{ Exception -> 0x079f, all -> 0x0788 }
            java.lang.String r4 = "frame-rate"
            r9 = 30
            r10.setInteger(r4, r9)     // Catch:{ Exception -> 0x079f, all -> 0x0788 }
            java.lang.String r4 = "i-frame-interval"
            r10.setInteger(r4, r6)     // Catch:{ Exception -> 0x079f, all -> 0x0788 }
            android.media.MediaCodec r4 = android.media.MediaCodec.createEncoderByType(r15)     // Catch:{ Exception -> 0x079f, all -> 0x0788 }
            r9 = r4
            r4 = 0
            r9.configure(r10, r4, r4, r6)     // Catch:{ Exception -> 0x0772, all -> 0x0788 }
            org.telegram.messenger.video.InputSurface r4 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x0772, all -> 0x0788 }
            android.view.Surface r6 = r9.createInputSurface()     // Catch:{ Exception -> 0x0772, all -> 0x0788 }
            r4.<init>(r6)     // Catch:{ Exception -> 0x0772, all -> 0x0788 }
            r6 = r4
            r6.makeCurrent()     // Catch:{ Exception -> 0x0759, all -> 0x0788 }
            r9.start()     // Catch:{ Exception -> 0x0759, all -> 0x0788 }
            org.telegram.messenger.video.OutputSurface r24 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x0759, all -> 0x0788 }
            r25 = 0
            float r4 = (float) r8
            r44 = 1
            r47 = r1
            r1 = r24
            r2 = r114
            r3 = r93
            r43 = r4
            r4 = r115
            r49 = r15
            r15 = r5
            r5 = r116
            r50 = r6
            r51 = r15
            r15 = 1
            r6 = r25
            r52 = r7
            r7 = r14
            r8 = r13
            r99 = r9
            r9 = r97
            r25 = r10
            r10 = r98
            r11 = r95
            r12 = r43
            r56 = r13
            r57 = r19
            r53 = r31
            r58 = r35
            r15 = 21
            r13 = r44
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x0743, all -> 0x072d }
            r26 = r24
            r1 = 0
            r2 = 0
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0743, all -> 0x072d }
            if (r3 >= r15) goto L_0x0248
            java.nio.ByteBuffer[] r3 = r99.getOutputBuffers()     // Catch:{ Exception -> 0x0236, all -> 0x0222 }
            r1 = r3
            goto L_0x0248
        L_0x0222:
            r0 = move-exception
            r15 = r92
            r13 = r101
            r66 = r106
            r60 = r108
            r1 = r0
            r5 = r18
            r7 = r52
            r10 = r56
            r6 = r57
            goto L_0x1c4f
        L_0x0236:
            r0 = move-exception
            r15 = r92
            r24 = r99
            r11 = r110
            r1 = r0
            r13 = r20
            r25 = r50
            r8 = r53
            r10 = r56
            goto L_0x07f6
        L_0x0248:
            r3 = 1
            r92.checkConversionCanceled()     // Catch:{ Exception -> 0x0743, all -> 0x072d }
        L_0x024c:
            if (r40 != 0) goto L_0x0711
            r92.checkConversionCanceled()     // Catch:{ Exception -> 0x0743, all -> 0x072d }
            r4 = r41 ^ 1
            r5 = 1
            r6 = r18
            r7 = r42
        L_0x0258:
            if (r4 != 0) goto L_0x0262
            if (r5 == 0) goto L_0x025d
            goto L_0x0262
        L_0x025d:
            r18 = r6
            r42 = r7
            goto L_0x024c
        L_0x0262:
            r92.checkConversionCanceled()     // Catch:{ Exception -> 0x06f9, all -> 0x06e4 }
            if (r113 == 0) goto L_0x026a
            r8 = 22000(0x55f0, double:1.08694E-319)
            goto L_0x026c
        L_0x026a:
            r8 = 2500(0x9c4, double:1.235E-320)
        L_0x026c:
            r10 = r99
            r13 = r20
            int r8 = r10.dequeueOutputBuffer(r13, r8)     // Catch:{ Exception -> 0x06d0, all -> 0x06e4 }
            r9 = -1
            if (r8 != r9) goto L_0x028c
            r5 = 0
            r15 = r92
            r99 = r2
            r21 = r4
            r20 = r7
            r12 = r47
            r7 = r49
            r11 = r51
            r2 = r1
            r1 = r8
            r8 = r53
            goto L_0x05a1
        L_0x028c:
            r9 = -3
            if (r8 != r9) goto L_0x02e5
            int r9 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02d3, all -> 0x02c0 }
            if (r9 >= r15) goto L_0x02ac
            java.nio.ByteBuffer[] r9 = r10.getOutputBuffers()     // Catch:{ Exception -> 0x02d3, all -> 0x02c0 }
            r1 = r9
            r15 = r92
            r99 = r2
            r21 = r4
            r20 = r7
            r12 = r47
            r7 = r49
            r11 = r51
            r2 = r1
            r1 = r8
            r8 = r53
            goto L_0x05a1
        L_0x02ac:
            r15 = r92
            r99 = r2
            r21 = r4
            r20 = r7
            r12 = r47
            r7 = r49
            r11 = r51
            r2 = r1
            r1 = r8
            r8 = r53
            goto L_0x05a1
        L_0x02c0:
            r0 = move-exception
            r15 = r92
            r13 = r101
            r66 = r106
            r60 = r108
            r1 = r0
            r5 = r6
            r7 = r52
            r10 = r56
            r6 = r57
            goto L_0x1c4f
        L_0x02d3:
            r0 = move-exception
            r15 = r92
            r11 = r110
            r1 = r0
            r18 = r6
            r24 = r10
            r25 = r50
            r8 = r53
            r10 = r56
            goto L_0x07f6
        L_0x02e5:
            r9 = -2
            if (r8 != r9) goto L_0x037b
            android.media.MediaFormat r9 = r10.getOutputFormat()     // Catch:{ Exception -> 0x02d3, all -> 0x02c0 }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x02d3, all -> 0x02c0 }
            if (r11 == 0) goto L_0x0304
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02d3, all -> 0x02c0 }
            r11.<init>()     // Catch:{ Exception -> 0x02d3, all -> 0x02c0 }
            java.lang.String r12 = "photo encoder new format "
            r11.append(r12)     // Catch:{ Exception -> 0x02d3, all -> 0x02c0 }
            r11.append(r9)     // Catch:{ Exception -> 0x02d3, all -> 0x02c0 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x02d3, all -> 0x02c0 }
            org.telegram.messenger.FileLog.d(r11)     // Catch:{ Exception -> 0x02d3, all -> 0x02c0 }
        L_0x0304:
            r11 = -5
            if (r6 != r11) goto L_0x0367
            if (r9 == 0) goto L_0x0367
            r15 = r92
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            r12 = 0
            int r11 = r11.addTrack(r9, r12)     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            r18 = r11
            r11 = r58
            boolean r6 = r9.containsKey(r11)     // Catch:{ Exception -> 0x0359, all -> 0x0347 }
            if (r6 == 0) goto L_0x033e
            int r6 = r9.getInteger(r11)     // Catch:{ Exception -> 0x0359, all -> 0x0347 }
            r12 = 1
            if (r6 != r12) goto L_0x033e
            r12 = r47
            java.nio.ByteBuffer r6 = r9.getByteBuffer(r12)     // Catch:{ Exception -> 0x0359, all -> 0x0347 }
            r58 = r11
            r11 = r51
            java.nio.ByteBuffer r20 = r9.getByteBuffer(r11)     // Catch:{ Exception -> 0x0359, all -> 0x0347 }
            int r21 = r6.limit()     // Catch:{ Exception -> 0x0359, all -> 0x0347 }
            int r24 = r20.limit()     // Catch:{ Exception -> 0x0359, all -> 0x0347 }
            int r27 = r21 + r24
            r6 = r18
            goto L_0x036d
        L_0x033e:
            r58 = r11
            r12 = r47
            r11 = r51
            r6 = r18
            goto L_0x036d
        L_0x0347:
            r0 = move-exception
            r13 = r101
            r66 = r106
            r60 = r108
            r1 = r0
            r5 = r18
            r7 = r52
            r10 = r56
            r6 = r57
            goto L_0x1c4f
        L_0x0359:
            r0 = move-exception
            r11 = r110
            r1 = r0
            r24 = r10
            r25 = r50
            r8 = r53
            r10 = r56
            goto L_0x07f6
        L_0x0367:
            r15 = r92
            r12 = r47
            r11 = r51
        L_0x036d:
            r99 = r2
            r21 = r4
            r20 = r7
            r7 = r49
            r2 = r1
            r1 = r8
            r8 = r53
            goto L_0x05a1
        L_0x037b:
            r15 = r92
            r12 = r47
            r11 = r51
            if (r8 < 0) goto L_0x068a
            int r9 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0688, all -> 0x0675 }
            r99 = r2
            r2 = 21
            if (r9 >= r2) goto L_0x039e
            r2 = r1[r8]     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            goto L_0x03a2
        L_0x038e:
            r0 = move-exception
            r11 = r110
            r1 = r0
            r18 = r6
            r24 = r10
            r25 = r50
            r8 = r53
            r10 = r56
            goto L_0x07f6
        L_0x039e:
            java.nio.ByteBuffer r2 = r10.getOutputBuffer(r8)     // Catch:{ Exception -> 0x0688, all -> 0x0675 }
        L_0x03a2:
            if (r2 == 0) goto L_0x0647
            int r9 = r13.size     // Catch:{ Exception -> 0x0688, all -> 0x0675 }
            r100 = r1
            r1 = 1
            if (r9 <= r1) goto L_0x057a
            int r1 = r13.flags     // Catch:{ Exception -> 0x056a, all -> 0x06b3 }
            r9 = 2
            r1 = r1 & r9
            if (r1 != 0) goto L_0x046a
            if (r27 == 0) goto L_0x03c5
            int r1 = r13.flags     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            r9 = 1
            r1 = r1 & r9
            if (r1 == 0) goto L_0x03c5
            int r1 = r13.offset     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            int r1 = r1 + r27
            r13.offset = r1     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            int r1 = r13.size     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            int r1 = r1 - r27
            r13.size = r1     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
        L_0x03c5:
            if (r3 == 0) goto L_0x0425
            int r1 = r13.flags     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            r9 = 1
            r1 = r1 & r9
            if (r1 == 0) goto L_0x0425
            int r1 = r13.size     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            r9 = 100
            if (r1 <= r9) goto L_0x0421
            int r1 = r13.offset     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            r2.position(r1)     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            byte[] r1 = new byte[r9]     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            r2.get(r1)     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            r18 = 0
            r20 = 0
            r9 = r20
        L_0x03e3:
            r102 = r3
            int r3 = r1.length     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            int r3 = r3 + -4
            if (r9 >= r3) goto L_0x041e
            byte r3 = r1[r9]     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            if (r3 != 0) goto L_0x0415
            int r3 = r9 + 1
            byte r3 = r1[r3]     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            if (r3 != 0) goto L_0x0415
            int r3 = r9 + 2
            byte r3 = r1[r3]     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            if (r3 != 0) goto L_0x0415
            int r3 = r9 + 3
            byte r3 = r1[r3]     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            r20 = r1
            r1 = 1
            if (r3 != r1) goto L_0x0417
            int r3 = r18 + 1
            if (r3 <= r1) goto L_0x0412
            int r1 = r13.offset     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            int r1 = r1 + r9
            r13.offset = r1     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            int r1 = r13.size     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            int r1 = r1 - r9
            r13.size = r1     // Catch:{ Exception -> 0x038e, all -> 0x06b3 }
            goto L_0x0423
        L_0x0412:
            r18 = r3
            goto L_0x0417
        L_0x0415:
            r20 = r1
        L_0x0417:
            int r9 = r9 + 1
            r3 = r102
            r1 = r20
            goto L_0x03e3
        L_0x041e:
            r20 = r1
            goto L_0x0423
        L_0x0421:
            r102 = r3
        L_0x0423:
            r3 = 0
            goto L_0x0429
        L_0x0425:
            r102 = r3
            r3 = r102
        L_0x0429:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ Exception -> 0x056a, all -> 0x06b3 }
            r9 = 1
            long r20 = r1.writeSampleData(r6, r2, r13, r9)     // Catch:{ Exception -> 0x056a, all -> 0x06b3 }
            r31 = r20
            r102 = r3
            r1 = r4
            r9 = r7
            r18 = r8
            r3 = r31
            r7 = 0
            int r20 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r20 == 0) goto L_0x045a
            org.telegram.messenger.MediaController$VideoConvertorListener r7 = r15.callback     // Catch:{ Exception -> 0x056a, all -> 0x06b3 }
            if (r7 == 0) goto L_0x0453
            r21 = r1
            r20 = r9
            r8 = r53
            float r1 = (float) r8
            float r1 = r1 / r22
            float r1 = r1 / r23
            r7.didWriteData(r3, r1)     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            goto L_0x0460
        L_0x0453:
            r21 = r1
            r20 = r9
            r8 = r53
            goto L_0x0460
        L_0x045a:
            r21 = r1
            r20 = r9
            r8 = r53
        L_0x0460:
            r3 = r102
            r24 = r2
            r32 = r5
            r7 = r49
            goto L_0x058c
        L_0x046a:
            r102 = r3
            r21 = r4
            r20 = r7
            r18 = r8
            r8 = r53
            r1 = -5
            if (r6 != r1) goto L_0x0563
            int r1 = r13.size     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            byte[] r1 = new byte[r1]     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            int r3 = r13.offset     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            int r4 = r13.size     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            int r3 = r3 + r4
            r2.limit(r3)     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            int r3 = r13.offset     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            r2.position(r3)     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            r2.get(r1)     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            r3 = 0
            r4 = 0
            int r7 = r13.size     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            r24 = r2
            r2 = 1
            int r7 = r7 - r2
        L_0x0493:
            if (r7 < 0) goto L_0x04ec
            r31 = r3
            r3 = 3
            if (r7 <= r3) goto L_0x04e9
            byte r3 = r1[r7]     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            if (r3 != r2) goto L_0x04df
            int r2 = r7 + -1
            byte r2 = r1[r2]     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            if (r2 != 0) goto L_0x04df
            int r2 = r7 + -2
            byte r2 = r1[r2]     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            if (r2 != 0) goto L_0x04df
            int r2 = r7 + -3
            byte r2 = r1[r2]     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            if (r2 != 0) goto L_0x04df
            int r2 = r7 + -3
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r2)     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            r3 = r2
            int r2 = r13.size     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            int r31 = r7 + -3
            int r2 = r2 - r31
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r2)     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            r4 = r2
            int r2 = r7 + -3
            r32 = r5
            r5 = 0
            java.nio.ByteBuffer r2 = r3.put(r1, r5, r2)     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            r2.position(r5)     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            int r2 = r7 + -3
            int r5 = r13.size     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            int r31 = r7 + -3
            int r5 = r5 - r31
            java.nio.ByteBuffer r2 = r4.put(r1, r2, r5)     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            r5 = 0
            r2.position(r5)     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            goto L_0x04f2
        L_0x04df:
            r32 = r5
            int r7 = r7 + -1
            r3 = r31
            r5 = r32
            r2 = 1
            goto L_0x0493
        L_0x04e9:
            r32 = r5
            goto L_0x04f0
        L_0x04ec:
            r31 = r3
            r32 = r5
        L_0x04f0:
            r3 = r31
        L_0x04f2:
            r7 = r49
            r2 = r56
            android.media.MediaFormat r5 = android.media.MediaFormat.createVideoFormat(r7, r14, r2)     // Catch:{ Exception -> 0x0545, all -> 0x0532 }
            if (r3 == 0) goto L_0x0522
            if (r4 == 0) goto L_0x0522
            r5.setByteBuffer(r12, r3)     // Catch:{ Exception -> 0x0515, all -> 0x0505 }
            r5.setByteBuffer(r11, r4)     // Catch:{ Exception -> 0x0515, all -> 0x0505 }
            goto L_0x0522
        L_0x0505:
            r0 = move-exception
            r13 = r101
            r66 = r106
            r60 = r108
            r1 = r0
            r10 = r2
            r5 = r6
            r7 = r52
            r6 = r57
            goto L_0x1c4f
        L_0x0515:
            r0 = move-exception
            r11 = r110
            r1 = r0
            r18 = r6
            r24 = r10
            r25 = r50
            r10 = r2
            goto L_0x07f6
        L_0x0522:
            r31 = r1
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ Exception -> 0x0545, all -> 0x0532 }
            r56 = r2
            r2 = 0
            int r1 = r1.addTrack(r5, r2)     // Catch:{ Exception -> 0x0555, all -> 0x06b3 }
            r3 = r102
            r6 = r1
            goto L_0x058c
        L_0x0532:
            r0 = move-exception
            r56 = r2
            r13 = r101
            r66 = r106
            r60 = r108
            r1 = r0
            r5 = r6
            r7 = r52
            r10 = r56
            r6 = r57
            goto L_0x1c4f
        L_0x0545:
            r0 = move-exception
            r56 = r2
            r11 = r110
            r1 = r0
            r18 = r6
            r24 = r10
            r25 = r50
            r10 = r56
            goto L_0x07f6
        L_0x0555:
            r0 = move-exception
            r11 = r110
            r1 = r0
            r18 = r6
            r24 = r10
            r25 = r50
            r10 = r56
            goto L_0x07f6
        L_0x0563:
            r24 = r2
            r32 = r5
            r7 = r49
            goto L_0x058a
        L_0x056a:
            r0 = move-exception
            r8 = r53
            r11 = r110
            r1 = r0
            r18 = r6
            r24 = r10
            r25 = r50
            r10 = r56
            goto L_0x07f6
        L_0x057a:
            r24 = r2
            r102 = r3
            r21 = r4
            r32 = r5
            r20 = r7
            r18 = r8
            r7 = r49
            r8 = r53
        L_0x058a:
            r3 = r102
        L_0x058c:
            int r1 = r13.flags     // Catch:{ Exception -> 0x060d, all -> 0x0675 }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x0594
            r1 = 1
            goto L_0x0595
        L_0x0594:
            r1 = 0
        L_0x0595:
            r40 = r1
            r1 = r18
            r2 = 0
            r10.releaseOutputBuffer(r1, r2)     // Catch:{ Exception -> 0x060d, all -> 0x0675 }
            r2 = r100
            r5 = r32
        L_0x05a1:
            r4 = -1
            if (r1 == r4) goto L_0x05bb
            r1 = r2
            r49 = r7
            r53 = r8
            r51 = r11
            r47 = r12
            r7 = r20
            r4 = r21
            r15 = 21
            r2 = r99
            r99 = r10
            r20 = r13
            goto L_0x0258
        L_0x05bb:
            if (r41 != 0) goto L_0x061d
            r26.drawImage()     // Catch:{ Exception -> 0x060d, all -> 0x0675 }
            r100 = r2
            r4 = r20
            float r2 = (float) r4
            r18 = 1106247680(0x41var_, float:30.0)
            float r2 = r2 / r18
            float r2 = r2 * r22
            float r2 = r2 * r22
            float r2 = r2 * r22
            r102 = r3
            long r2 = (long) r2
            r18 = r5
            r5 = r50
            r5.setPresentationTime(r2)     // Catch:{ Exception -> 0x05ff, all -> 0x0675 }
            r5.swapBuffers()     // Catch:{ Exception -> 0x05ff, all -> 0x0675 }
            int r4 = r4 + 1
            r31 = r2
            float r2 = (float) r4
            r51 = r11
            r47 = r12
            r11 = r110
            float r3 = (float) r11
            float r3 = r3 / r22
            r20 = 1106247680(0x41var_, float:30.0)
            float r3 = r3 * r20
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 < 0) goto L_0x05fb
            r41 = 1
            r2 = 0
            r10.signalEndOfInputStream()     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
            r1 = r4
            r4 = r2
            goto L_0x0630
        L_0x05fb:
            r1 = r4
            r4 = r21
            goto L_0x0630
        L_0x05ff:
            r0 = move-exception
            r11 = r110
            r1 = r0
            r25 = r5
            r18 = r6
            r24 = r10
            r10 = r56
            goto L_0x07f6
        L_0x060d:
            r0 = move-exception
            r11 = r110
            r5 = r50
            r1 = r0
            r25 = r5
            r18 = r6
            r24 = r10
            r10 = r56
            goto L_0x07f6
        L_0x061d:
            r100 = r2
            r102 = r3
            r18 = r5
            r51 = r11
            r47 = r12
            r4 = r20
            r5 = r50
            r11 = r110
            r1 = r4
            r4 = r21
        L_0x0630:
            r2 = r99
            r3 = r102
            r50 = r5
            r49 = r7
            r53 = r8
            r99 = r10
            r20 = r13
            r5 = r18
            r15 = 21
            r7 = r1
            r1 = r100
            goto L_0x0258
        L_0x0647:
            r11 = r110
            r100 = r1
            r24 = r2
            r102 = r3
            r21 = r4
            r32 = r5
            r4 = r7
            r1 = r8
            r5 = r50
            r8 = r53
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
            r3.<init>()     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
            java.lang.String r7 = "encoderOutputBuffer "
            r3.append(r7)     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
            r3.append(r1)     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
            java.lang.String r7 = " was null"
            r3.append(r7)     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
            throw r2     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
        L_0x0675:
            r0 = move-exception
            r11 = r110
            r13 = r101
            r66 = r106
            r60 = r108
            r1 = r0
            r5 = r6
            r7 = r52
            r10 = r56
            r6 = r57
            goto L_0x1c4f
        L_0x0688:
            r0 = move-exception
            goto L_0x06d3
        L_0x068a:
            r11 = r110
            r100 = r1
            r99 = r2
            r102 = r3
            r21 = r4
            r32 = r5
            r4 = r7
            r1 = r8
            r5 = r50
            r8 = r53
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
            r3.<init>()     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
            java.lang.String r7 = "unexpected result from encoder.dequeueOutputBuffer: "
            r3.append(r7)     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
            r3.append(r1)     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
            throw r2     // Catch:{ Exception -> 0x06c4, all -> 0x06b3 }
        L_0x06b3:
            r0 = move-exception
            r13 = r101
            r66 = r106
            r60 = r108
            r1 = r0
            r5 = r6
            r7 = r52
            r10 = r56
            r6 = r57
            goto L_0x1c4f
        L_0x06c4:
            r0 = move-exception
            r1 = r0
            r25 = r5
            r18 = r6
            r24 = r10
            r10 = r56
            goto L_0x07f6
        L_0x06d0:
            r0 = move-exception
            r15 = r92
        L_0x06d3:
            r11 = r110
            r5 = r50
            r8 = r53
            r1 = r0
            r25 = r5
            r18 = r6
            r24 = r10
            r10 = r56
            goto L_0x07f6
        L_0x06e4:
            r0 = move-exception
            r15 = r92
            r11 = r110
            r13 = r101
            r66 = r106
            r60 = r108
            r1 = r0
            r5 = r6
            r7 = r52
            r10 = r56
            r6 = r57
            goto L_0x1c4f
        L_0x06f9:
            r0 = move-exception
            r15 = r92
            r10 = r99
            r11 = r110
            r13 = r20
            r5 = r50
            r8 = r53
            r1 = r0
            r25 = r5
            r18 = r6
            r24 = r10
            r10 = r56
            goto L_0x07f6
        L_0x0711:
            r15 = r92
            r10 = r99
            r11 = r110
            r99 = r2
            r13 = r20
            r5 = r50
            r8 = r53
            r6 = r101
            r25 = r5
            r24 = r10
            r7 = r52
            r10 = r56
            r5 = r57
            goto L_0x0834
        L_0x072d:
            r0 = move-exception
            r15 = r92
            r11 = r110
            r13 = r101
            r66 = r106
            r60 = r108
            r1 = r0
            r5 = r18
            r7 = r52
            r10 = r56
            r6 = r57
            goto L_0x1c4f
        L_0x0743:
            r0 = move-exception
            r15 = r92
            r10 = r99
            r11 = r110
            r13 = r20
            r5 = r50
            r8 = r53
            r1 = r0
            r25 = r5
            r24 = r10
            r10 = r56
            goto L_0x07f6
        L_0x0759:
            r0 = move-exception
            r15 = r92
            r11 = r2
            r5 = r6
            r52 = r7
            r10 = r9
            r56 = r13
            r57 = r19
            r13 = r20
            r8 = r31
            r1 = r0
            r25 = r5
            r24 = r10
            r10 = r56
            goto L_0x07f6
        L_0x0772:
            r0 = move-exception
            r15 = r92
            r11 = r2
            r52 = r7
            r10 = r9
            r56 = r13
            r57 = r19
            r13 = r20
            r8 = r31
            r1 = r0
            r24 = r10
            r10 = r56
            goto L_0x07f6
        L_0x0788:
            r0 = move-exception
            r15 = r92
            r11 = r2
            r52 = r7
            r56 = r13
            r13 = r101
            r66 = r106
            r60 = r108
            r1 = r0
            r5 = r18
            r6 = r19
            r10 = r56
            goto L_0x1c4f
        L_0x079f:
            r0 = move-exception
            r15 = r92
            r11 = r2
            r52 = r7
            r56 = r13
            r57 = r19
            r13 = r20
            r8 = r31
            r1 = r0
            r10 = r56
            goto L_0x07f6
        L_0x07b1:
            r0 = move-exception
            r15 = r92
            r11 = r2
            r52 = r7
            r13 = r101
            r66 = r106
            r60 = r108
            r1 = r0
            r5 = r18
            r6 = r19
            goto L_0x1c4f
        L_0x07c4:
            r0 = move-exception
            r15 = r92
            r11 = r2
            r52 = r7
            r57 = r19
            r13 = r20
            r8 = r31
            r1 = r0
            goto L_0x07f6
        L_0x07d2:
            r0 = move-exception
            r15 = r92
            r11 = r2
            r52 = r7
            r14 = r99
            r13 = r101
            r66 = r106
            r60 = r108
            r1 = r0
            r5 = r18
            r6 = r19
            goto L_0x1c4f
        L_0x07e7:
            r0 = move-exception
            r15 = r92
            r11 = r2
            r52 = r7
            r57 = r19
            r13 = r20
            r8 = r31
            r14 = r99
            r1 = r0
        L_0x07f6:
            boolean r2 = r1 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x087d }
            if (r2 == 0) goto L_0x07fe
            if (r113 != 0) goto L_0x07fe
            r17 = 1
        L_0x07fe:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x087d }
            r2.<init>()     // Catch:{ all -> 0x087d }
            r5 = r57
            r2.append(r5)     // Catch:{ all -> 0x086f }
            r7 = r52
            r2.append(r7)     // Catch:{ all -> 0x0862 }
            java.lang.String r3 = " framerate: "
            r2.append(r3)     // Catch:{ all -> 0x0862 }
            r6 = r101
            r2.append(r6)     // Catch:{ all -> 0x0856 }
            java.lang.String r3 = " size: "
            r2.append(r3)     // Catch:{ all -> 0x0856 }
            r2.append(r10)     // Catch:{ all -> 0x0856 }
            java.lang.String r3 = "x"
            r2.append(r3)     // Catch:{ all -> 0x0856 }
            r2.append(r14)     // Catch:{ all -> 0x0856 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0856 }
            org.telegram.messenger.FileLog.e((java.lang.String) r2)     // Catch:{ all -> 0x0856 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x0856 }
            r2 = 1
            r16 = r2
        L_0x0834:
            if (r26 == 0) goto L_0x0839
            r26.release()     // Catch:{ all -> 0x0856 }
        L_0x0839:
            if (r25 == 0) goto L_0x083e
            r25.release()     // Catch:{ all -> 0x0856 }
        L_0x083e:
            if (r24 == 0) goto L_0x0846
            r24.stop()     // Catch:{ all -> 0x0856 }
            r24.release()     // Catch:{ all -> 0x0856 }
        L_0x0846:
            r92.checkConversionCanceled()     // Catch:{ all -> 0x0856 }
            r66 = r106
            r60 = r108
            r13 = r6
            r69 = r7
            r7 = r10
            r8 = r14
            r1 = r18
            goto L_0x1c0a
        L_0x0856:
            r0 = move-exception
            r66 = r106
            r60 = r108
            r1 = r0
            r13 = r6
            r6 = r5
            r5 = r18
            goto L_0x1c4f
        L_0x0862:
            r0 = move-exception
            r13 = r101
            r66 = r106
            r60 = r108
            r1 = r0
            r6 = r5
            r5 = r18
            goto L_0x1c4f
        L_0x086f:
            r0 = move-exception
            r7 = r52
            r13 = r101
            r66 = r106
            r60 = r108
            r1 = r0
            r6 = r5
            r5 = r18
            goto L_0x088b
        L_0x087d:
            r0 = move-exception
            r7 = r52
            r13 = r101
            r66 = r106
            r60 = r108
            r1 = r0
            r5 = r18
            r6 = r57
        L_0x088b:
            goto L_0x1c4f
        L_0x088d:
            r47 = r1
            r11 = r2
            r51 = r5
            r14 = r7
            r6 = r8
            r7 = r15
            r5 = r19
            r13 = r20
            r8 = r31
            r58 = r35
            r4 = 100
            r15 = r92
            android.media.MediaExtractor r1 = new android.media.MediaExtractor     // Catch:{ all -> 0x1CLASSNAME }
            r1.<init>()     // Catch:{ all -> 0x1CLASSNAME }
            r15.extractor = r1     // Catch:{ all -> 0x1CLASSNAME }
            r3 = r93
            r1.setDataSource(r3)     // Catch:{ all -> 0x1CLASSNAME }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ all -> 0x1CLASSNAME }
            r2 = 0
            int r1 = org.telegram.messenger.MediaController.findTrack(r1, r2)     // Catch:{ all -> 0x1CLASSNAME }
            r2 = r1
            r1 = -1
            r3 = 0
            if (r14 == r1) goto L_0x08d1
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ all -> 0x08c2 }
            r3 = 1
            int r1 = org.telegram.messenger.MediaController.findTrack(r1, r3)     // Catch:{ all -> 0x08c2 }
            goto L_0x08d2
        L_0x08c2:
            r0 = move-exception
            r66 = r106
            r60 = r108
            r1 = r0
            r13 = r6
            r7 = r14
            r14 = r99
            r6 = r5
            r5 = r18
            goto L_0x1c4f
        L_0x08d1:
            r1 = -1
        L_0x08d2:
            r4 = r1
            r1 = 0
            java.lang.String r3 = "mime"
            if (r2 < 0) goto L_0x08ee
            r20 = r1
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ all -> 0x08c2 }
            android.media.MediaFormat r1 = r1.getTrackFormat(r2)     // Catch:{ all -> 0x08c2 }
            java.lang.String r1 = r1.getString(r3)     // Catch:{ all -> 0x08c2 }
            boolean r1 = r1.equals(r7)     // Catch:{ all -> 0x08c2 }
            if (r1 != 0) goto L_0x08f0
            r1 = 1
            r20 = r1
            goto L_0x08f0
        L_0x08ee:
            r20 = r1
        L_0x08f0:
            r1 = r112
            if (r1 != 0) goto L_0x094c
            if (r20 == 0) goto L_0x0902
            r14 = r2
            r63 = r4
            r62 = r5
            r31 = r8
            r21 = r13
            r13 = r6
            goto L_0x0956
        L_0x0902:
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ all -> 0x093a }
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ all -> 0x093a }
            r1 = -1
            if (r14 == r1) goto L_0x090c
            r55 = 1
            goto L_0x090e
        L_0x090c:
            r55 = 0
        L_0x090e:
            r1 = r92
            r14 = r2
            r2 = r3
            r3 = r7
            r7 = r4
            r4 = r13
            r62 = r5
            r21 = r13
            r13 = r6
            r5 = r104
            r31 = r8
            r9 = r7
            r7 = r106
            r63 = r9
            r9 = r110
            r11 = r94
            r12 = r55
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ all -> 0x1b42 }
            r8 = r99
            r7 = r100
            r69 = r102
            r66 = r106
            r60 = r108
            r1 = r18
            goto L_0x1c0a
        L_0x093a:
            r0 = move-exception
            r14 = r99
            r10 = r100
            r7 = r102
            r66 = r106
            r60 = r108
            r1 = r0
            r13 = r6
            r6 = r5
            r5 = r18
            goto L_0x1c4f
        L_0x094c:
            r14 = r2
            r63 = r4
            r62 = r5
            r31 = r8
            r21 = r13
            r13 = r6
        L_0x0956:
            r35 = 0
            r38 = 0
            r40 = 1
            r41 = -1
            if (r14 < 0) goto L_0x1bd1
            r1 = 0
            r43 = -1
            r45 = 0
            r46 = 0
            r49 = 0
            r50 = 0
            r52 = -5
            r53 = 0
            r56 = -2147483648(0xfffffffvar_, double:NaN)
            r2 = 1000(0x3e8, float:1.401E-42)
            int r4 = r2 / r13
            int r4 = r4 * 1000
            long r11 = (long) r4
            r4 = 30
            if (r13 >= r4) goto L_0x0997
            int r4 = r13 + 5
            int r4 = r2 / r4
            int r4 = r4 * 1000
            long r4 = (long) r4
            r64 = r4
            goto L_0x09a0
        L_0x0987:
            r0 = move-exception
            r2 = r102
            r66 = r106
            r60 = r108
            r3 = r0
            r37 = r14
            r14 = r21
            r4 = r63
            goto L_0x1b65
        L_0x0997:
            int r4 = r13 + 1
            int r4 = r2 / r4
            int r4 = r4 * 1000
            long r4 = (long) r4     // Catch:{ Exception -> 0x1b54 }
            r64 = r4
        L_0x09a0:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x1b54 }
            r2.selectTrack(r14)     // Catch:{ Exception -> 0x1b54 }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x1b54 }
            android.media.MediaFormat r2 = r2.getTrackFormat(r14)     // Catch:{ Exception -> 0x1b54 }
            r10 = r2
            r4 = 0
            int r2 = (r108 > r4 ? 1 : (r108 == r4 ? 0 : -1))
            if (r2 < 0) goto L_0x09cd
            r2 = 1157234688(0x44fa0000, float:2000.0)
            int r2 = (r23 > r2 ? 1 : (r23 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x09bc
            r2 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x09ca
        L_0x09bc:
            r2 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r2 = (r23 > r2 ? 1 : (r23 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x09c7
            r2 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x09ca
        L_0x09c7:
            r2 = 1560000(0x17cdc0, float:2.186026E-39)
        L_0x09ca:
            r4 = 0
            goto L_0x09d9
        L_0x09cd:
            if (r102 > 0) goto L_0x09d5
            r2 = 921600(0xe1000, float:1.291437E-39)
            r4 = r108
            goto L_0x09d9
        L_0x09d5:
            r2 = r102
            r4 = r108
        L_0x09d9:
            r9 = r103
            if (r9 <= 0) goto L_0x0a02
            int r6 = java.lang.Math.min(r9, r2)     // Catch:{ Exception -> 0x09f4, all -> 0x09e3 }
            r2 = r6
            goto L_0x0a02
        L_0x09e3:
            r0 = move-exception
            r14 = r99
            r10 = r100
            r66 = r106
            r1 = r0
            r7 = r2
            r60 = r4
            r5 = r18
            r6 = r62
            goto L_0x1c4f
        L_0x09f4:
            r0 = move-exception
            r66 = r106
            r3 = r0
            r60 = r4
            r37 = r14
            r14 = r21
            r4 = r63
            goto L_0x1b65
        L_0x0a02:
            r60 = 0
            int r6 = (r4 > r60 ? 1 : (r4 == r60 ? 0 : -1))
            if (r6 < 0) goto L_0x0a0c
            r4 = -1
            r5 = r4
            goto L_0x0a0d
        L_0x0a0c:
            r5 = r4
        L_0x0a0d:
            int r4 = (r5 > r60 ? 1 : (r5 == r60 ? 0 : -1))
            if (r4 < 0) goto L_0x0a40
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x0a32, all -> 0x0a21 }
            r8 = 0
            r4.seekTo(r5, r8)     // Catch:{ Exception -> 0x0a32, all -> 0x0a21 }
            r108 = r5
            r66 = r11
            r5 = 0
            r8 = 0
            r11 = r104
            goto L_0x0a60
        L_0x0a21:
            r0 = move-exception
            r14 = r99
            r10 = r100
            r66 = r106
            r1 = r0
            r7 = r2
            r60 = r5
            r5 = r18
            r6 = r62
            goto L_0x1c4f
        L_0x0a32:
            r0 = move-exception
            r66 = r106
            r3 = r0
            r60 = r5
            r37 = r14
            r14 = r21
            r4 = r63
            goto L_0x1b65
        L_0x0a40:
            r66 = r11
            r60 = 0
            r11 = r104
            int r4 = (r11 > r60 ? 1 : (r11 == r60 ? 0 : -1))
            if (r4 <= 0) goto L_0x0a56
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x0a32, all -> 0x0a21 }
            r8 = 0
            r4.seekTo(r11, r8)     // Catch:{ Exception -> 0x0a32, all -> 0x0a21 }
            r108 = r5
            r5 = 0
            r8 = 0
            goto L_0x0a60
        L_0x0a56:
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x1b32, all -> 0x1b21 }
            r108 = r5
            r5 = 0
            r8 = 0
            r4.seekTo(r5, r8)     // Catch:{ Exception -> 0x1b11, all -> 0x1b00 }
        L_0x0a60:
            r4 = r118
            if (r4 == 0) goto L_0x0aa2
            r5 = 90
            r6 = r95
            if (r6 == r5) goto L_0x0a79
            r5 = 270(0x10e, float:3.78E-43)
            if (r6 != r5) goto L_0x0a6f
            goto L_0x0a79
        L_0x0a6f:
            int r5 = r4.transformWidth     // Catch:{ Exception -> 0x0a94, all -> 0x0a83 }
            int r8 = r4.transformHeight     // Catch:{ Exception -> 0x0a94, all -> 0x0a83 }
            r90 = r8
            r8 = r5
            r5 = r90
            goto L_0x0aad
        L_0x0a79:
            int r5 = r4.transformHeight     // Catch:{ Exception -> 0x0a94, all -> 0x0a83 }
            int r8 = r4.transformWidth     // Catch:{ Exception -> 0x0a94, all -> 0x0a83 }
            r90 = r8
            r8 = r5
            r5 = r90
            goto L_0x0aad
        L_0x0a83:
            r0 = move-exception
            r14 = r99
            r10 = r100
            r66 = r106
            r60 = r108
            r1 = r0
            r7 = r2
            r5 = r18
            r6 = r62
            goto L_0x1c4f
        L_0x0a94:
            r0 = move-exception
            r66 = r106
            r60 = r108
            r3 = r0
            r37 = r14
            r14 = r21
            r4 = r63
            goto L_0x1b65
        L_0x0aa2:
            r6 = r95
            r5 = r99
            r8 = r100
            r90 = r8
            r8 = r5
            r5 = r90
        L_0x0aad:
            boolean r68 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1b11, all -> 0x1b00 }
            if (r68 == 0) goto L_0x0ae0
            r68 = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ad0, all -> 0x0a83 }
            r1.<init>()     // Catch:{ Exception -> 0x0ad0, all -> 0x0a83 }
            java.lang.String r4 = "create encoder with w = "
            r1.append(r4)     // Catch:{ Exception -> 0x0ad0, all -> 0x0a83 }
            r1.append(r8)     // Catch:{ Exception -> 0x0ad0, all -> 0x0a83 }
            java.lang.String r4 = " h = "
            r1.append(r4)     // Catch:{ Exception -> 0x0ad0, all -> 0x0a83 }
            r1.append(r5)     // Catch:{ Exception -> 0x0ad0, all -> 0x0a83 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0ad0, all -> 0x0a83 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0ad0, all -> 0x0a83 }
            goto L_0x0ae2
        L_0x0ad0:
            r0 = move-exception
            r66 = r106
            r60 = r108
            r3 = r0
            r37 = r14
            r14 = r21
            r4 = r63
            r1 = r68
            goto L_0x1b65
        L_0x0ae0:
            r68 = r1
        L_0x0ae2:
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r7, r8, r5)     // Catch:{ Exception -> 0x1aef, all -> 0x1b00 }
            r4 = r1
            java.lang.String r1 = "color-format"
            r6 = 2130708361(0x7var_, float:1.701803E38)
            r4.setInteger(r1, r6)     // Catch:{ Exception -> 0x1aef, all -> 0x1b00 }
            java.lang.String r1 = "bitrate"
            r4.setInteger(r1, r2)     // Catch:{ Exception -> 0x1aef, all -> 0x1b00 }
            java.lang.String r1 = "frame-rate"
            r4.setInteger(r1, r13)     // Catch:{ Exception -> 0x1aef, all -> 0x1b00 }
            java.lang.String r1 = "i-frame-interval"
            r6 = 2
            r4.setInteger(r1, r6)     // Catch:{ Exception -> 0x1aef, all -> 0x1b00 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1aef, all -> 0x1b00 }
            r6 = 23
            if (r1 >= r6) goto L_0x0b41
            int r1 = java.lang.Math.min(r5, r8)     // Catch:{ Exception -> 0x0ad0, all -> 0x0a83 }
            r6 = 480(0x1e0, float:6.73E-43)
            if (r1 > r6) goto L_0x0b41
            r1 = 921600(0xe1000, float:1.291437E-39)
            if (r2 <= r1) goto L_0x0b16
            r1 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x0b17
        L_0x0b16:
            r1 = r2
        L_0x0b17:
            java.lang.String r2 = "bitrate"
            r4.setInteger(r2, r1)     // Catch:{ Exception -> 0x0b30, all -> 0x0b1f }
            r69 = r1
            goto L_0x0b43
        L_0x0b1f:
            r0 = move-exception
            r14 = r99
            r10 = r100
            r66 = r106
            r60 = r108
            r7 = r1
            r5 = r18
            r6 = r62
            r1 = r0
            goto L_0x1c4f
        L_0x0b30:
            r0 = move-exception
            r66 = r106
            r60 = r108
            r3 = r0
            r2 = r1
            r37 = r14
            r14 = r21
            r4 = r63
            r1 = r68
            goto L_0x1b65
        L_0x0b41:
            r69 = r2
        L_0x0b43:
            android.media.MediaCodec r1 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x1adc, all -> 0x1aca }
            r6 = r1
            r1 = 0
            r2 = 1
            r6.configure(r4, r1, r1, r2)     // Catch:{ Exception -> 0x1ab5, all -> 0x1aca }
            org.telegram.messenger.video.InputSurface r1 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x1ab5, all -> 0x1aca }
            android.view.Surface r2 = r6.createInputSurface()     // Catch:{ Exception -> 0x1ab5, all -> 0x1aca }
            r1.<init>(r2)     // Catch:{ Exception -> 0x1ab5, all -> 0x1aca }
            r2 = r1
            r2.makeCurrent()     // Catch:{ Exception -> 0x1a9d, all -> 0x1aca }
            r6.start()     // Catch:{ Exception -> 0x1a9d, all -> 0x1aca }
            java.lang.String r1 = r10.getString(r3)     // Catch:{ Exception -> 0x1a9d, all -> 0x1aca }
            android.media.MediaCodec r1 = android.media.MediaCodec.createDecoderByType(r1)     // Catch:{ Exception -> 0x1a9d, all -> 0x1aca }
            org.telegram.messenger.video.OutputSurface r24 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x1a86, all -> 0x1aca }
            r25 = 0
            r70 = r3
            float r3 = (float) r13
            r68 = 0
            r77 = r1
            r1 = r24
            r78 = r2
            r2 = r114
            r59 = r3
            r79 = r70
            r3 = r25
            r25 = r4
            r4 = r115
            r60 = r108
            r80 = r5
            r5 = r116
            r102 = r6
            r6 = r118
            r81 = r7
            r7 = r99
            r82 = r8
            r48 = 0
            r8 = r100
            r9 = r97
            r83 = r10
            r10 = r98
            r12 = r51
            r84 = r58
            r33 = r66
            r11 = r95
            r85 = r12
            r86 = r47
            r12 = r59
            r87 = r21
            r13 = r68
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x1a6a, all -> 0x1a58 }
            r1 = r24
            if (r119 != 0) goto L_0x0CLASSNAME
            r3 = r100
            int r2 = java.lang.Math.max(r3, r3)     // Catch:{ Exception -> 0x0c4a, all -> 0x0CLASSNAME }
            float r2 = (float) r2
            r13 = r97
            r12 = r98
            int r4 = java.lang.Math.max(r12, r13)     // Catch:{ Exception -> 0x0c1a, all -> 0x0CLASSNAME }
            float r4 = (float) r4
            float r2 = r2 / r4
            r4 = 1063675494(0x3var_, float:0.9)
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 >= 0) goto L_0x0CLASSNAME
            r4 = r99
            r2 = 1
            java.lang.String r5 = createFragmentShader(r13, r12, r4, r3, r2)     // Catch:{ Exception -> 0x0bec, all -> 0x0bdc }
            r2 = 0
            java.lang.String r6 = createFragmentShader(r13, r12, r4, r3, r2)     // Catch:{ Exception -> 0x0bec, all -> 0x0bdc }
            r1.changeFragmentShader(r5, r6)     // Catch:{ Exception -> 0x0bec, all -> 0x0bdc }
            goto L_0x0CLASSNAME
        L_0x0bdc:
            r0 = move-exception
            r13 = r101
            r66 = r106
            r1 = r0
            r10 = r3
            r14 = r4
            r5 = r18
            r6 = r62
            r7 = r69
            goto L_0x1c4f
        L_0x0bec:
            r0 = move-exception
            r13 = r101
            r24 = r102
            r66 = r106
            r3 = r0
            r26 = r1
            r37 = r14
            r4 = r63
            r2 = r69
            r1 = r77
            r25 = r78
            r14 = r87
            goto L_0x1b65
        L_0x0CLASSNAME:
            r4 = r99
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = move-exception
            r4 = r99
            r13 = r101
            r66 = r106
            r1 = r0
            r10 = r3
            r14 = r4
            r5 = r18
            r6 = r62
            r7 = r69
            goto L_0x1c4f
        L_0x0c1a:
            r0 = move-exception
            r4 = r99
            r13 = r101
            r24 = r102
            r66 = r106
            r3 = r0
            r26 = r1
            r37 = r14
            r4 = r63
            r2 = r69
            r1 = r77
            r25 = r78
            r14 = r87
            goto L_0x1b65
        L_0x0CLASSNAME:
            r0 = move-exception
            r13 = r97
            r12 = r98
            r4 = r99
            r13 = r101
            r66 = r106
            r1 = r0
            r10 = r3
            r14 = r4
            r5 = r18
            r6 = r62
            r7 = r69
            goto L_0x1c4f
        L_0x0c4a:
            r0 = move-exception
            r13 = r97
            r12 = r98
            r4 = r99
            r13 = r101
            r24 = r102
            r66 = r106
            r3 = r0
            r26 = r1
            r37 = r14
            r4 = r63
            r2 = r69
            r1 = r77
            r25 = r78
            r14 = r87
            goto L_0x1b65
        L_0x0CLASSNAME:
            r13 = r97
            r12 = r98
            r4 = r99
            r3 = r100
        L_0x0CLASSNAME:
            r2 = 0
        L_0x0CLASSNAME:
            android.view.Surface r5 = r1.getSurface()     // Catch:{ Exception -> 0x1a38, all -> 0x1a58 }
            r7 = r77
            r6 = r83
            r8 = 0
            r7.configure(r6, r5, r8, r2)     // Catch:{ Exception -> 0x1a1a, all -> 0x1a58 }
            r7.start()     // Catch:{ Exception -> 0x1a1a, all -> 0x1a58 }
            r5 = 0
            r8 = 0
            r9 = 0
            int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1a1a, all -> 0x1a58 }
            r11 = 21
            if (r10 >= r11) goto L_0x0cab
            java.nio.ByteBuffer[] r10 = r7.getInputBuffers()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0bdc }
            r5 = r10
            java.nio.ByteBuffer[] r10 = r102.getOutputBuffers()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0bdc }
            r8 = r10
            goto L_0x0cab
        L_0x0CLASSNAME:
            r0 = move-exception
            r13 = r101
            r24 = r102
            r66 = r106
            r3 = r0
            r26 = r1
            r1 = r7
            r37 = r14
            r4 = r63
            r2 = r69
            r25 = r78
            r14 = r87
            goto L_0x1b65
        L_0x0cab:
            r10 = 0
            r11 = r63
            if (r11 < 0) goto L_0x0ebf
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0ea2, all -> 0x0e8a }
            android.media.MediaFormat r2 = r2.getTrackFormat(r11)     // Catch:{ Exception -> 0x0ea2, all -> 0x0e8a }
            r83 = r6
            r108 = r8
            r6 = r79
            java.lang.String r8 = r2.getString(r6)     // Catch:{ Exception -> 0x0ea2, all -> 0x0e8a }
            r109 = r9
            java.lang.String r9 = "audio/mp4a-latm"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x0ea2, all -> 0x0e8a }
            if (r8 != 0) goto L_0x0cef
            java.lang.String r8 = r2.getString(r6)     // Catch:{ Exception -> 0x0cd9, all -> 0x0bdc }
            java.lang.String r9 = "audio/mpeg"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x0cd9, all -> 0x0bdc }
            if (r8 == 0) goto L_0x0cd7
            goto L_0x0cef
        L_0x0cd7:
            r8 = 0
            goto L_0x0cf0
        L_0x0cd9:
            r0 = move-exception
            r13 = r101
            r24 = r102
            r66 = r106
            r3 = r0
            r26 = r1
            r1 = r7
            r4 = r11
            r37 = r14
            r2 = r69
            r25 = r78
            r14 = r87
            goto L_0x1b65
        L_0x0cef:
            r8 = 1
        L_0x0cf0:
            r40 = r8
            java.lang.String r6 = r2.getString(r6)     // Catch:{ Exception -> 0x0ea2, all -> 0x0e8a }
            java.lang.String r8 = "audio/unknown"
            boolean r6 = r6.equals(r8)     // Catch:{ Exception -> 0x0ea2, all -> 0x0e8a }
            if (r6 == 0) goto L_0x0d00
            r6 = -1
            goto L_0x0d01
        L_0x0d00:
            r6 = r11
        L_0x0d01:
            if (r6 < 0) goto L_0x0e7a
            if (r40 == 0) goto L_0x0d7b
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x0d63, all -> 0x0d51 }
            r9 = 1
            int r8 = r8.addTrack(r2, r9)     // Catch:{ Exception -> 0x0d63, all -> 0x0d51 }
            r52 = r8
            android.media.MediaExtractor r8 = r15.extractor     // Catch:{ Exception -> 0x0d63, all -> 0x0d51 }
            r8.selectTrack(r6)     // Catch:{ Exception -> 0x0d63, all -> 0x0d51 }
            java.lang.String r8 = "max-input-size"
            int r8 = r2.getInteger(r8)     // Catch:{ Exception -> 0x0d1b, all -> 0x0bdc }
            r10 = r8
            goto L_0x0d20
        L_0x0d1b:
            r0 = move-exception
            r8 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)     // Catch:{ Exception -> 0x0d63, all -> 0x0d51 }
        L_0x0d20:
            if (r10 > 0) goto L_0x0d25
            r8 = 65536(0x10000, float:9.18355E-41)
            r10 = r8
        L_0x0d25:
            java.nio.ByteBuffer r8 = java.nio.ByteBuffer.allocateDirect(r10)     // Catch:{ Exception -> 0x0d63, all -> 0x0d51 }
            r38 = r8
            r8 = r104
            r21 = r10
            r10 = 0
            int r24 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r24 <= 0) goto L_0x0d3c
            android.media.MediaExtractor r10 = r15.extractor     // Catch:{ Exception -> 0x0d95, all -> 0x0bdc }
            r11 = 0
            r10.seekTo(r8, r11)     // Catch:{ Exception -> 0x0d95, all -> 0x0bdc }
            goto L_0x0d44
        L_0x0d3c:
            android.media.MediaExtractor r10 = r15.extractor     // Catch:{ Exception -> 0x0d95, all -> 0x0bdc }
            r11 = 0
            r13 = 0
            r10.seekTo(r11, r13)     // Catch:{ Exception -> 0x0d95, all -> 0x0bdc }
        L_0x0d44:
            r13 = r93
            r10 = r106
            r4 = r6
            r24 = r21
            r2 = r35
            r3 = r52
            goto L_0x0ed3
        L_0x0d51:
            r0 = move-exception
            r8 = r104
            r13 = r101
            r66 = r106
            r1 = r0
            r10 = r3
            r14 = r4
            r5 = r18
            r6 = r62
            r7 = r69
            goto L_0x1c4f
        L_0x0d63:
            r0 = move-exception
            r8 = r104
            r13 = r101
            r24 = r102
            r66 = r106
            r3 = r0
            r26 = r1
            r4 = r6
            r1 = r7
            r37 = r14
            r2 = r69
            r25 = r78
            r14 = r87
            goto L_0x1b65
        L_0x0d7b:
            r8 = r104
            android.media.MediaExtractor r11 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0e60, all -> 0x0e4a }
            r11.<init>()     // Catch:{ Exception -> 0x0e60, all -> 0x0e4a }
            r13 = r93
            r11.setDataSource(r13)     // Catch:{ Exception -> 0x0e32, all -> 0x0e1e }
            r11.selectTrack(r6)     // Catch:{ Exception -> 0x0e32, all -> 0x0e1e }
            r47 = 0
            int r12 = (r8 > r47 ? 1 : (r8 == r47 ? 0 : -1))
            if (r12 <= 0) goto L_0x0dab
            r12 = 0
            r11.seekTo(r8, r12)     // Catch:{ Exception -> 0x0d95, all -> 0x0bdc }
            goto L_0x0db1
        L_0x0d95:
            r0 = move-exception
            r13 = r101
            r24 = r102
            r66 = r106
            r3 = r0
            r26 = r1
            r4 = r6
            r1 = r7
            r37 = r14
            r2 = r69
            r25 = r78
            r14 = r87
            goto L_0x1b65
        L_0x0dab:
            r12 = 0
            r3 = 0
            r11.seekTo(r3, r12)     // Catch:{ Exception -> 0x0e32, all -> 0x0e1e }
        L_0x0db1:
            org.telegram.messenger.video.AudioRecoder r3 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x0e32, all -> 0x0e1e }
            r3.<init>(r2, r11, r6)     // Catch:{ Exception -> 0x0e32, all -> 0x0e1e }
            r3.startTime = r8     // Catch:{ Exception -> 0x0e02, all -> 0x0e1e }
            r4 = r10
            r12 = r11
            r10 = r106
            r3.endTime = r10     // Catch:{ Exception -> 0x0e00, all -> 0x0dee }
            r21 = r2
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0e00, all -> 0x0dee }
            r24 = r4
            android.media.MediaFormat r4 = r3.format     // Catch:{ Exception -> 0x0e00, all -> 0x0dee }
            r26 = r3
            r3 = 1
            int r2 = r2.addTrack(r4, r3)     // Catch:{ Exception -> 0x0dd6, all -> 0x0dee }
            r52 = r2
            r4 = r6
            r2 = r26
            r3 = r52
            goto L_0x0ed3
        L_0x0dd6:
            r0 = move-exception
            r13 = r101
            r24 = r102
            r3 = r0
            r4 = r6
            r66 = r10
            r37 = r14
            r35 = r26
            r2 = r69
            r25 = r78
            r14 = r87
            r26 = r1
            r1 = r7
            goto L_0x1b65
        L_0x0dee:
            r0 = move-exception
            r14 = r99
            r13 = r101
            r1 = r0
            r66 = r10
            r5 = r18
            r6 = r62
            r7 = r69
            r10 = r100
            goto L_0x1c4f
        L_0x0e00:
            r0 = move-exception
            goto L_0x0e05
        L_0x0e02:
            r0 = move-exception
            r10 = r106
        L_0x0e05:
            r26 = r3
            r13 = r101
            r24 = r102
            r3 = r0
            r4 = r6
            r66 = r10
            r37 = r14
            r35 = r26
            r2 = r69
            r25 = r78
            r14 = r87
            r26 = r1
            r1 = r7
            goto L_0x1b65
        L_0x0e1e:
            r0 = move-exception
            r10 = r106
            r14 = r99
            r13 = r101
            r1 = r0
            r66 = r10
            r5 = r18
            r6 = r62
            r7 = r69
            r10 = r100
            goto L_0x1c4f
        L_0x0e32:
            r0 = move-exception
            r10 = r106
            r13 = r101
            r24 = r102
            r3 = r0
            r26 = r1
            r4 = r6
            r1 = r7
            r66 = r10
            r37 = r14
            r2 = r69
            r25 = r78
            r14 = r87
            goto L_0x1b65
        L_0x0e4a:
            r0 = move-exception
            r13 = r93
            r10 = r106
            r14 = r99
            r13 = r101
            r1 = r0
            r66 = r10
            r5 = r18
            r6 = r62
            r7 = r69
            r10 = r100
            goto L_0x1c4f
        L_0x0e60:
            r0 = move-exception
            r13 = r93
            r10 = r106
            r13 = r101
            r24 = r102
            r3 = r0
            r26 = r1
            r4 = r6
            r1 = r7
            r66 = r10
            r37 = r14
            r2 = r69
            r25 = r78
            r14 = r87
            goto L_0x1b65
        L_0x0e7a:
            r13 = r93
            r8 = r104
            r21 = r2
            r24 = r10
            r10 = r106
            r4 = r6
            r2 = r35
            r3 = r52
            goto L_0x0ed3
        L_0x0e8a:
            r0 = move-exception
            r13 = r93
            r8 = r104
            r10 = r106
            r14 = r99
            r13 = r101
            r1 = r0
            r66 = r10
            r5 = r18
            r6 = r62
            r7 = r69
            r10 = r100
            goto L_0x1c4f
        L_0x0ea2:
            r0 = move-exception
            r13 = r93
            r8 = r104
            r3 = r11
            r10 = r106
            r13 = r101
            r24 = r102
            r26 = r1
            r4 = r3
            r1 = r7
            r66 = r10
            r37 = r14
            r2 = r69
            r25 = r78
            r14 = r87
            r3 = r0
            goto L_0x1b65
        L_0x0ebf:
            r13 = r93
            r83 = r6
            r108 = r8
            r109 = r9
            r24 = r10
            r3 = r11
            r8 = r104
            r10 = r106
            r4 = r3
            r2 = r35
            r3 = r52
        L_0x0ed3:
            if (r4 >= 0) goto L_0x0ed7
            r6 = 1
            goto L_0x0ed8
        L_0x0ed7:
            r6 = 0
        L_0x0ed8:
            r12 = 1
            r92.checkConversionCanceled()     // Catch:{ Exception -> 0x19f7, all -> 0x1a58 }
            r106 = r108
            r21 = r18
            r18 = r12
            r12 = r24
        L_0x0ee4:
            if (r45 == 0) goto L_0x0var_
            if (r40 != 0) goto L_0x0eeb
            if (r6 != 0) goto L_0x0eeb
            goto L_0x0var_
        L_0x0eeb:
            r8 = r99
            r13 = r101
            r24 = r102
            r26 = r1
            r35 = r2
            r1 = r7
            r66 = r10
            r37 = r14
            r18 = r21
            r6 = r62
            r25 = r78
            r14 = r87
            r7 = r100
            goto L_0x1ba6
        L_0x0var_:
            r92.checkConversionCanceled()     // Catch:{ Exception -> 0x19d0, all -> 0x19bc }
            if (r40 != 0) goto L_0x0var_
            if (r2 == 0) goto L_0x0var_
            r107 = r6
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            boolean r6 = r2.step(r6, r3)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            r14 = r99
            r13 = r101
            r1 = r0
            r66 = r10
            r5 = r21
            r6 = r62
            r7 = r69
            r10 = r100
            goto L_0x1c4f
        L_0x0var_:
            r0 = move-exception
            r13 = r101
            r24 = r102
            r3 = r0
            r26 = r1
            r35 = r2
            r1 = r7
            r66 = r10
            r37 = r14
            r18 = r21
            r2 = r69
            r25 = r78
            r14 = r87
            goto L_0x1b65
        L_0x0var_:
            r107 = r6
            r6 = r107
        L_0x0var_:
            if (r46 != 0) goto L_0x11cc
            r24 = 0
            r108 = r2
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x11af, all -> 0x11a4 }
            int r2 = r2.getSampleTrackIndex()     // Catch:{ Exception -> 0x11af, all -> 0x11a4 }
            if (r2 != r14) goto L_0x0fcc
            r37 = r14
            r13 = 2500(0x9c4, double:1.235E-320)
            int r26 = r7.dequeueInputBuffer(r13)     // Catch:{ Exception -> 0x0fb5, all -> 0x0var_ }
            r107 = r26
            r13 = r107
            if (r13 < 0) goto L_0x0fa7
            int r14 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0fb5, all -> 0x0var_ }
            r107 = r6
            r6 = 21
            if (r14 >= r6) goto L_0x0f6c
            r6 = r5[r13]     // Catch:{ Exception -> 0x0fb5, all -> 0x0var_ }
            goto L_0x0var_
        L_0x0f6c:
            java.nio.ByteBuffer r6 = r7.getInputBuffer(r13)     // Catch:{ Exception -> 0x0fb5, all -> 0x0var_ }
        L_0x0var_:
            android.media.MediaExtractor r14 = r15.extractor     // Catch:{ Exception -> 0x0fb5, all -> 0x0var_ }
            r26 = r5
            r5 = 0
            int r14 = r14.readSampleData(r6, r5)     // Catch:{ Exception -> 0x0fb5, all -> 0x0var_ }
            r5 = r14
            if (r5 >= 0) goto L_0x0f8e
            r72 = 0
            r73 = 0
            r74 = 0
            r76 = 4
            r70 = r7
            r71 = r13
            r70.queueInputBuffer(r71, r72, r73, r74, r76)     // Catch:{ Exception -> 0x0fb5, all -> 0x0var_ }
            r46 = 1
            goto L_0x0fab
        L_0x0f8e:
            r72 = 0
            android.media.MediaExtractor r14 = r15.extractor     // Catch:{ Exception -> 0x0fb5, all -> 0x0var_ }
            long r74 = r14.getSampleTime()     // Catch:{ Exception -> 0x0fb5, all -> 0x0var_ }
            r76 = 0
            r70 = r7
            r71 = r13
            r73 = r5
            r70.queueInputBuffer(r71, r72, r73, r74, r76)     // Catch:{ Exception -> 0x0fb5, all -> 0x0var_ }
            android.media.MediaExtractor r14 = r15.extractor     // Catch:{ Exception -> 0x0fb5, all -> 0x0var_ }
            r14.advance()     // Catch:{ Exception -> 0x0fb5, all -> 0x0var_ }
            goto L_0x0fab
        L_0x0fa7:
            r26 = r5
            r107 = r6
        L_0x0fab:
            r35 = r3
            r47 = r4
            r51 = r10
            r14 = r87
            goto L_0x115d
        L_0x0fb5:
            r0 = move-exception
            r13 = r101
            r24 = r102
            r35 = r108
            r3 = r0
            r26 = r1
            r1 = r7
            r66 = r10
            r18 = r21
            r2 = r69
            r25 = r78
            r14 = r87
            goto L_0x1b65
        L_0x0fcc:
            r26 = r5
            r107 = r6
            r37 = r14
            if (r40 == 0) goto L_0x1150
            r5 = -1
            if (r4 == r5) goto L_0x1150
            if (r2 != r4) goto L_0x1150
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1135, all -> 0x11a4 }
            r6 = 28
            if (r5 < r6) goto L_0x0ff3
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x0fb5, all -> 0x0var_ }
            long r5 = r5.getSampleSize()     // Catch:{ Exception -> 0x0fb5, all -> 0x0var_ }
            long r13 = (long) r12     // Catch:{ Exception -> 0x0fb5, all -> 0x0var_ }
            int r35 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r35 <= 0) goto L_0x0ff3
            r13 = 1024(0x400, double:5.06E-321)
            long r13 = r13 + r5
            int r12 = (int) r13     // Catch:{ Exception -> 0x0fb5, all -> 0x0var_ }
            java.nio.ByteBuffer r13 = java.nio.ByteBuffer.allocateDirect(r12)     // Catch:{ Exception -> 0x0fb5, all -> 0x0var_ }
            goto L_0x0ff5
        L_0x0ff3:
            r13 = r38
        L_0x0ff5:
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x1118, all -> 0x11a4 }
            r6 = 0
            int r5 = r5.readSampleData(r13, r6)     // Catch:{ Exception -> 0x1118, all -> 0x11a4 }
            r14 = r87
            r14.size = r5     // Catch:{ Exception -> 0x10fd, all -> 0x11a4 }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x10fd, all -> 0x11a4 }
            r6 = 21
            if (r5 >= r6) goto L_0x1027
            r5 = 0
            r13.position(r5)     // Catch:{ Exception -> 0x1010, all -> 0x0var_ }
            int r5 = r14.size     // Catch:{ Exception -> 0x1010, all -> 0x0var_ }
            r13.limit(r5)     // Catch:{ Exception -> 0x1010, all -> 0x0var_ }
            goto L_0x1027
        L_0x1010:
            r0 = move-exception
            r24 = r102
            r35 = r108
            r3 = r0
            r26 = r1
            r1 = r7
            r66 = r10
            r38 = r13
            r18 = r21
            r2 = r69
            r25 = r78
            r13 = r101
            goto L_0x1b65
        L_0x1027:
            int r5 = r14.size     // Catch:{ Exception -> 0x10fd, all -> 0x11a4 }
            if (r5 < 0) goto L_0x1039
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x1010, all -> 0x0var_ }
            long r5 = r5.getSampleTime()     // Catch:{ Exception -> 0x1010, all -> 0x0var_ }
            r14.presentationTimeUs = r5     // Catch:{ Exception -> 0x1010, all -> 0x0var_ }
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x1010, all -> 0x0var_ }
            r5.advance()     // Catch:{ Exception -> 0x1010, all -> 0x0var_ }
            goto L_0x103e
        L_0x1039:
            r5 = 0
            r14.size = r5     // Catch:{ Exception -> 0x10fd, all -> 0x11a4 }
            r46 = 1
        L_0x103e:
            int r5 = r14.size     // Catch:{ Exception -> 0x10fd, all -> 0x11a4 }
            if (r5 <= 0) goto L_0x10f3
            r5 = 0
            int r35 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r35 < 0) goto L_0x1057
            long r5 = r14.presentationTimeUs     // Catch:{ Exception -> 0x1010, all -> 0x0var_ }
            int r35 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r35 >= 0) goto L_0x104f
            goto L_0x1057
        L_0x104f:
            r35 = r3
            r47 = r4
            r51 = r10
            goto L_0x10f9
        L_0x1057:
            r5 = 0
            r14.offset = r5     // Catch:{ Exception -> 0x10fd, all -> 0x11a4 }
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x10fd, all -> 0x11a4 }
            int r5 = r5.getSampleFlags()     // Catch:{ Exception -> 0x10fd, all -> 0x11a4 }
            r14.flags = r5     // Catch:{ Exception -> 0x10fd, all -> 0x11a4 }
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x10fd, all -> 0x11a4 }
            r6 = 0
            long r47 = r5.writeSampleData(r3, r13, r14, r6)     // Catch:{ Exception -> 0x10fd, all -> 0x11a4 }
            r5 = r47
            r47 = 0
            int r35 = (r5 > r47 ? 1 : (r5 == r47 ? 0 : -1))
            if (r35 == 0) goto L_0x10e9
            r35 = r3
            org.telegram.messenger.MediaController$VideoConvertorListener r3 = r15.callback     // Catch:{ Exception -> 0x10fd, all -> 0x11a4 }
            if (r3 == 0) goto L_0x10e4
            r47 = r4
            long r3 = r14.presentationTimeUs     // Catch:{ Exception -> 0x10c9, all -> 0x11a4 }
            long r3 = r3 - r8
            int r38 = (r3 > r31 ? 1 : (r3 == r31 ? 0 : -1))
            if (r38 <= 0) goto L_0x109d
            long r3 = r14.presentationTimeUs     // Catch:{ Exception -> 0x1084, all -> 0x0var_ }
            long r3 = r3 - r8
            goto L_0x109f
        L_0x1084:
            r0 = move-exception
            r24 = r102
            r35 = r108
            r3 = r0
            r26 = r1
            r1 = r7
            r66 = r10
            r38 = r13
            r18 = r21
            r4 = r47
            r2 = r69
            r25 = r78
            r13 = r101
            goto L_0x1b65
        L_0x109d:
            r3 = r31
        L_0x109f:
            r51 = r10
            org.telegram.messenger.MediaController$VideoConvertorListener r10 = r15.callback     // Catch:{ Exception -> 0x10ae, all -> 0x117b }
            float r11 = (float) r3     // Catch:{ Exception -> 0x10ae, all -> 0x117b }
            float r11 = r11 / r22
            float r11 = r11 / r23
            r10.didWriteData(r5, r11)     // Catch:{ Exception -> 0x10ae, all -> 0x117b }
            r31 = r3
            goto L_0x10ef
        L_0x10ae:
            r0 = move-exception
            r24 = r102
            r35 = r108
            r26 = r1
            r31 = r3
            r1 = r7
            r38 = r13
            r18 = r21
            r4 = r47
            r66 = r51
            r2 = r69
            r25 = r78
            r13 = r101
            r3 = r0
            goto L_0x1b65
        L_0x10c9:
            r0 = move-exception
            r51 = r10
            r24 = r102
            r35 = r108
            r3 = r0
            r26 = r1
            r1 = r7
            r38 = r13
            r18 = r21
            r4 = r47
            r66 = r51
            r2 = r69
            r25 = r78
            r13 = r101
            goto L_0x1b65
        L_0x10e4:
            r47 = r4
            r51 = r10
            goto L_0x10ef
        L_0x10e9:
            r35 = r3
            r47 = r4
            r51 = r10
        L_0x10ef:
            r38 = r13
            goto L_0x115d
        L_0x10f3:
            r35 = r3
            r47 = r4
            r51 = r10
        L_0x10f9:
            r38 = r13
            goto L_0x115d
        L_0x10fd:
            r0 = move-exception
            r47 = r4
            r51 = r10
            r24 = r102
            r35 = r108
            r3 = r0
            r26 = r1
            r1 = r7
            r38 = r13
            r18 = r21
            r66 = r51
            r2 = r69
            r25 = r78
            r13 = r101
            goto L_0x1b65
        L_0x1118:
            r0 = move-exception
            r47 = r4
            r51 = r10
            r14 = r87
            r24 = r102
            r35 = r108
            r3 = r0
            r26 = r1
            r1 = r7
            r38 = r13
            r18 = r21
            r66 = r51
            r2 = r69
            r25 = r78
            r13 = r101
            goto L_0x1b65
        L_0x1135:
            r0 = move-exception
            r47 = r4
            r51 = r10
            r14 = r87
            r13 = r101
            r24 = r102
            r35 = r108
            r3 = r0
            r26 = r1
            r1 = r7
            r18 = r21
            r66 = r51
            r2 = r69
            r25 = r78
            goto L_0x1b65
        L_0x1150:
            r35 = r3
            r47 = r4
            r51 = r10
            r14 = r87
            r3 = -1
            if (r2 != r3) goto L_0x115d
            r24 = 1
        L_0x115d:
            if (r24 == 0) goto L_0x11dc
            r3 = 2500(0x9c4, double:1.235E-320)
            int r5 = r7.dequeueInputBuffer(r3)     // Catch:{ Exception -> 0x118d, all -> 0x117b }
            r3 = r5
            if (r3 < 0) goto L_0x11dc
            r72 = 0
            r73 = 0
            r74 = 0
            r76 = 4
            r70 = r7
            r71 = r3
            r70.queueInputBuffer(r71, r72, r73, r74, r76)     // Catch:{ Exception -> 0x118d, all -> 0x117b }
            r46 = 1
            goto L_0x11dc
        L_0x117b:
            r0 = move-exception
            r14 = r99
            r10 = r100
            r13 = r101
            r1 = r0
            r5 = r21
            r66 = r51
            r6 = r62
            r7 = r69
            goto L_0x1c4f
        L_0x118d:
            r0 = move-exception
            r13 = r101
            r24 = r102
            r35 = r108
            r3 = r0
            r26 = r1
            r1 = r7
            r18 = r21
            r4 = r47
            r66 = r51
            r2 = r69
            r25 = r78
            goto L_0x1b65
        L_0x11a4:
            r0 = move-exception
            r51 = r10
            r14 = r99
            r10 = r100
            r13 = r101
            goto L_0x19c5
        L_0x11af:
            r0 = move-exception
            r47 = r4
            r51 = r10
            r37 = r14
            r14 = r87
            r13 = r101
            r24 = r102
            r35 = r108
            r3 = r0
            r26 = r1
            r1 = r7
            r18 = r21
            r66 = r51
            r2 = r69
            r25 = r78
            goto L_0x1b65
        L_0x11cc:
            r108 = r2
            r35 = r3
            r47 = r4
            r26 = r5
            r107 = r6
            r51 = r10
            r37 = r14
            r14 = r87
        L_0x11dc:
            r2 = r49 ^ 1
            r3 = 1
            r4 = r3
            r5 = r21
            r10 = r51
            r3 = r2
            r2 = r106
            r106 = r12
            r12 = r56
        L_0x11eb:
            if (r3 != 0) goto L_0x120a
            if (r4 == 0) goto L_0x11f0
            goto L_0x120a
        L_0x11f0:
            r6 = r107
            r21 = r5
            r56 = r12
            r87 = r14
            r5 = r26
            r3 = r35
            r14 = r37
            r4 = r47
            r13 = r93
            r12 = r106
            r106 = r2
            r2 = r108
            goto L_0x0ee4
        L_0x120a:
            r92.checkConversionCanceled()     // Catch:{ Exception -> 0x199f, all -> 0x198f }
            if (r113 == 0) goto L_0x1217
            r51 = 22000(0x55f0, double:1.08694E-319)
            r6 = r3
            r21 = r4
            r3 = r51
            goto L_0x121c
        L_0x1217:
            r6 = r3
            r21 = r4
            r3 = 2500(0x9c4, double:1.235E-320)
        L_0x121c:
            r24 = r6
            r6 = r102
            int r3 = r6.dequeueOutputBuffer(r14, r3)     // Catch:{ Exception -> 0x1974, all -> 0x198f }
            r4 = -1
            if (r3 != r4) goto L_0x1241
            r4 = 0
            r102 = r1
            r48 = r2
            r2 = r3
            r21 = r4
            r66 = r10
            r51 = r12
            r10 = r80
            r12 = r81
            r9 = r82
            r58 = r84
            r4 = r85
            r13 = r86
            goto L_0x1550
        L_0x1241:
            r4 = -3
            if (r3 != r4) goto L_0x12a4
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x128b, all -> 0x127b }
            r102 = r1
            r1 = 21
            if (r4 >= r1) goto L_0x1266
            java.nio.ByteBuffer[] r1 = r6.getOutputBuffers()     // Catch:{ Exception -> 0x135c, all -> 0x127b }
            r2 = r1
            r48 = r2
            r2 = r3
            r66 = r10
            r51 = r12
            r10 = r80
            r12 = r81
            r9 = r82
            r58 = r84
            r4 = r85
            r13 = r86
            goto L_0x1550
        L_0x1266:
            r48 = r2
            r2 = r3
            r66 = r10
            r51 = r12
            r10 = r80
            r12 = r81
            r9 = r82
            r58 = r84
            r4 = r85
            r13 = r86
            goto L_0x1550
        L_0x127b:
            r0 = move-exception
            r14 = r99
            r13 = r101
            r1 = r0
            r66 = r10
            r6 = r62
            r7 = r69
            r10 = r100
            goto L_0x1c4f
        L_0x128b:
            r0 = move-exception
            r102 = r1
            r13 = r101
            r26 = r102
            r35 = r108
            r3 = r0
            r18 = r5
            r24 = r6
            r1 = r7
            r66 = r10
            r4 = r47
            r2 = r69
            r25 = r78
            goto L_0x1b65
        L_0x12a4:
            r102 = r1
            r1 = -2
            if (r3 != r1) goto L_0x1373
            android.media.MediaFormat r1 = r6.getOutputFormat()     // Catch:{ Exception -> 0x135c, all -> 0x127b }
            r4 = -5
            if (r5 != r4) goto L_0x1345
            if (r1 == 0) goto L_0x1345
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x135c, all -> 0x127b }
            r51 = r12
            r12 = 0
            int r4 = r4.addTrack(r1, r12)     // Catch:{ Exception -> 0x135c, all -> 0x127b }
            r12 = r84
            boolean r5 = r1.containsKey(r12)     // Catch:{ Exception -> 0x132c, all -> 0x1318 }
            if (r5 == 0) goto L_0x130f
            int r5 = r1.getInteger(r12)     // Catch:{ Exception -> 0x132c, all -> 0x1318 }
            r13 = 1
            if (r5 != r13) goto L_0x130f
            r13 = r86
            java.nio.ByteBuffer r5 = r1.getByteBuffer(r13)     // Catch:{ Exception -> 0x132c, all -> 0x1318 }
            r48 = r4
            r4 = r85
            java.nio.ByteBuffer r56 = r1.getByteBuffer(r4)     // Catch:{ Exception -> 0x12f8, all -> 0x12e6 }
            int r57 = r5.limit()     // Catch:{ Exception -> 0x12f8, all -> 0x12e6 }
            int r58 = r56.limit()     // Catch:{ Exception -> 0x12f8, all -> 0x12e6 }
            int r27 = r57 + r58
            r5 = r48
            goto L_0x134d
        L_0x12e6:
            r0 = move-exception
            r14 = r99
            r13 = r101
            r1 = r0
            r66 = r10
            r5 = r48
            r6 = r62
            r7 = r69
            r10 = r100
            goto L_0x1c4f
        L_0x12f8:
            r0 = move-exception
            r13 = r101
            r26 = r102
            r35 = r108
            r3 = r0
            r24 = r6
            r1 = r7
            r66 = r10
            r4 = r47
            r18 = r48
            r2 = r69
            r25 = r78
            goto L_0x1b65
        L_0x130f:
            r48 = r4
            r4 = r85
            r13 = r86
            r5 = r48
            goto L_0x134d
        L_0x1318:
            r0 = move-exception
            r48 = r4
            r14 = r99
            r13 = r101
            r1 = r0
            r66 = r10
            r5 = r48
            r6 = r62
            r7 = r69
            r10 = r100
            goto L_0x1c4f
        L_0x132c:
            r0 = move-exception
            r48 = r4
            r13 = r101
            r26 = r102
            r35 = r108
            r3 = r0
            r24 = r6
            r1 = r7
            r66 = r10
            r4 = r47
            r18 = r48
            r2 = r69
            r25 = r78
            goto L_0x1b65
        L_0x1345:
            r51 = r12
            r12 = r84
            r4 = r85
            r13 = r86
        L_0x134d:
            r48 = r2
            r2 = r3
            r66 = r10
            r58 = r12
            r10 = r80
            r12 = r81
            r9 = r82
            goto L_0x1550
        L_0x135c:
            r0 = move-exception
            r13 = r101
            r26 = r102
            r35 = r108
            r3 = r0
            r18 = r5
            r24 = r6
            r1 = r7
            r66 = r10
            r4 = r47
            r2 = r69
            r25 = r78
            goto L_0x1b65
        L_0x1373:
            r51 = r12
            r12 = r84
            r4 = r85
            r13 = r86
            if (r3 < 0) goto L_0x1933
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x191a, all -> 0x198f }
            r58 = r12
            r12 = 21
            if (r1 >= r12) goto L_0x1388
            r1 = r2[r3]     // Catch:{ Exception -> 0x135c, all -> 0x127b }
            goto L_0x138c
        L_0x1388:
            java.nio.ByteBuffer r1 = r6.getOutputBuffer(r3)     // Catch:{ Exception -> 0x191a, all -> 0x198f }
        L_0x138c:
            if (r1 == 0) goto L_0x18ee
            int r12 = r14.size     // Catch:{ Exception -> 0x191a, all -> 0x198f }
            r48 = r2
            r2 = 1
            if (r12 <= r2) goto L_0x152b
            int r2 = r14.flags     // Catch:{ Exception -> 0x1514, all -> 0x1509 }
            r12 = 2
            r2 = r2 & r12
            if (r2 != 0) goto L_0x1465
            if (r27 == 0) goto L_0x13b1
            int r2 = r14.flags     // Catch:{ Exception -> 0x135c, all -> 0x127b }
            r19 = 1
            r2 = r2 & 1
            if (r2 == 0) goto L_0x13b1
            int r2 = r14.offset     // Catch:{ Exception -> 0x135c, all -> 0x127b }
            int r2 = r2 + r27
            r14.offset = r2     // Catch:{ Exception -> 0x135c, all -> 0x127b }
            int r2 = r14.size     // Catch:{ Exception -> 0x135c, all -> 0x127b }
            int r2 = r2 - r27
            r14.size = r2     // Catch:{ Exception -> 0x135c, all -> 0x127b }
        L_0x13b1:
            if (r18 == 0) goto L_0x140a
            int r2 = r14.flags     // Catch:{ Exception -> 0x1514, all -> 0x1509 }
            r19 = 1
            r2 = r2 & 1
            if (r2 == 0) goto L_0x140a
            int r2 = r14.size     // Catch:{ Exception -> 0x1514, all -> 0x1509 }
            r12 = 100
            if (r2 <= r12) goto L_0x1405
            int r2 = r14.offset     // Catch:{ Exception -> 0x1514, all -> 0x1509 }
            r1.position(r2)     // Catch:{ Exception -> 0x1514, all -> 0x1509 }
            byte[] r2 = new byte[r12]     // Catch:{ Exception -> 0x1514, all -> 0x1509 }
            r1.get(r2)     // Catch:{ Exception -> 0x1514, all -> 0x1509 }
            r56 = 0
            r57 = 0
            r12 = r57
        L_0x13d1:
            r66 = r10
            int r10 = r2.length     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            int r10 = r10 + -4
            if (r12 >= r10) goto L_0x1407
            byte r10 = r2[r12]     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            if (r10 != 0) goto L_0x1400
            int r10 = r12 + 1
            byte r10 = r2[r10]     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            if (r10 != 0) goto L_0x1400
            int r10 = r12 + 2
            byte r10 = r2[r10]     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            if (r10 != 0) goto L_0x1400
            int r10 = r12 + 3
            byte r10 = r2[r10]     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r11 = 1
            if (r10 != r11) goto L_0x1400
            int r10 = r56 + 1
            if (r10 <= r11) goto L_0x13fe
            int r11 = r14.offset     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            int r11 = r11 + r12
            r14.offset = r11     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            int r11 = r14.size     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            int r11 = r11 - r12
            r14.size = r11     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            goto L_0x1407
        L_0x13fe:
            r56 = r10
        L_0x1400:
            int r12 = r12 + 1
            r10 = r66
            goto L_0x13d1
        L_0x1405:
            r66 = r10
        L_0x1407:
            r18 = 0
            goto L_0x140c
        L_0x140a:
            r66 = r10
        L_0x140c:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r10 = 1
            long r11 = r2.writeSampleData(r5, r1, r14, r10)     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r10 = r11
            r56 = 0
            int r2 = (r10 > r56 ? 1 : (r10 == r56 ? 0 : -1))
            if (r2 == 0) goto L_0x1455
            org.telegram.messenger.MediaController$VideoConvertorListener r2 = r15.callback     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            if (r2 == 0) goto L_0x1452
            r12 = r3
            long r2 = r14.presentationTimeUs     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            long r2 = r2 - r8
            int r56 = (r2 > r31 ? 1 : (r2 == r31 ? 0 : -1))
            if (r56 <= 0) goto L_0x142a
            long r2 = r14.presentationTimeUs     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            long r2 = r2 - r8
            goto L_0x142c
        L_0x142a:
            r2 = r31
        L_0x142c:
            r56 = r12
            org.telegram.messenger.MediaController$VideoConvertorListener r12 = r15.callback     // Catch:{ Exception -> 0x143b, all -> 0x15e0 }
            float r8 = (float) r2     // Catch:{ Exception -> 0x143b, all -> 0x15e0 }
            float r8 = r8 / r22
            float r8 = r8 / r23
            r12.didWriteData(r10, r8)     // Catch:{ Exception -> 0x143b, all -> 0x15e0 }
            r31 = r2
            goto L_0x1457
        L_0x143b:
            r0 = move-exception
            r13 = r101
            r26 = r102
            r35 = r108
            r31 = r2
            r18 = r5
            r24 = r6
            r1 = r7
            r4 = r47
            r2 = r69
            r25 = r78
            r3 = r0
            goto L_0x1b65
        L_0x1452:
            r56 = r3
            goto L_0x1457
        L_0x1455:
            r56 = r3
        L_0x1457:
            r57 = r1
            r1 = r18
            r10 = r80
            r12 = r81
            r9 = r82
            r18 = r5
            goto L_0x153b
        L_0x1465:
            r56 = r3
            r66 = r10
            r2 = -5
            if (r5 != r2) goto L_0x1500
            int r2 = r14.size     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            byte[] r2 = new byte[r2]     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            int r3 = r14.offset     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            int r8 = r14.size     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            int r3 = r3 + r8
            r1.limit(r3)     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            int r3 = r14.offset     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r1.position(r3)     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r1.get(r2)     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r3 = 0
            r8 = 0
            int r9 = r14.size     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r10 = 1
            int r9 = r9 - r10
        L_0x1486:
            if (r9 < 0) goto L_0x14d2
            r11 = 3
            if (r9 <= r11) goto L_0x14d3
            byte r12 = r2[r9]     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            if (r12 != r10) goto L_0x14ce
            int r12 = r9 + -1
            byte r12 = r2[r12]     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            if (r12 != 0) goto L_0x14ce
            int r12 = r9 + -2
            byte r12 = r2[r12]     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            if (r12 != 0) goto L_0x14ce
            int r12 = r9 + -3
            byte r12 = r2[r12]     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            if (r12 != 0) goto L_0x14ce
            int r12 = r9 + -3
            java.nio.ByteBuffer r12 = java.nio.ByteBuffer.allocate(r12)     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r3 = r12
            int r12 = r14.size     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            int r19 = r9 + -3
            int r12 = r12 - r19
            java.nio.ByteBuffer r12 = java.nio.ByteBuffer.allocate(r12)     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r8 = r12
            int r12 = r9 + -3
            r10 = 0
            java.nio.ByteBuffer r12 = r3.put(r2, r10, r12)     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r12.position(r10)     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            int r10 = r9 + -3
            int r12 = r14.size     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            int r57 = r9 + -3
            int r12 = r12 - r57
            java.nio.ByteBuffer r10 = r8.put(r2, r10, r12)     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r12 = 0
            r10.position(r12)     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            goto L_0x14d3
        L_0x14ce:
            int r9 = r9 + -1
            r10 = 1
            goto L_0x1486
        L_0x14d2:
            r11 = 3
        L_0x14d3:
            r10 = r80
            r12 = r81
            r9 = r82
            android.media.MediaFormat r57 = android.media.MediaFormat.createVideoFormat(r12, r9, r10)     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r59 = r57
            if (r3 == 0) goto L_0x14ec
            if (r8 == 0) goto L_0x14ec
            r11 = r59
            r11.setByteBuffer(r13, r3)     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r11.setByteBuffer(r4, r8)     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            goto L_0x14ee
        L_0x14ec:
            r11 = r59
        L_0x14ee:
            r57 = r1
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r59 = r2
            r2 = 0
            int r1 = r1.addTrack(r11, r2)     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r90 = r18
            r18 = r1
            r1 = r90
            goto L_0x153b
        L_0x1500:
            r57 = r1
            r10 = r80
            r12 = r81
            r9 = r82
            goto L_0x1537
        L_0x1509:
            r0 = move-exception
            r66 = r10
            r14 = r99
            r10 = r100
            r13 = r101
            goto L_0x1998
        L_0x1514:
            r0 = move-exception
            r66 = r10
            r13 = r101
            r26 = r102
            r35 = r108
            r3 = r0
            r18 = r5
            r24 = r6
            r1 = r7
            r4 = r47
            r2 = r69
            r25 = r78
            goto L_0x1b65
        L_0x152b:
            r57 = r1
            r56 = r3
            r66 = r10
            r10 = r80
            r12 = r81
            r9 = r82
        L_0x1537:
            r1 = r18
            r18 = r5
        L_0x153b:
            int r2 = r14.flags     // Catch:{ Exception -> 0x18d9, all -> 0x18c9 }
            r2 = r2 & 4
            if (r2 == 0) goto L_0x1543
            r2 = 1
            goto L_0x1544
        L_0x1543:
            r2 = 0
        L_0x1544:
            r45 = r2
            r2 = r56
            r3 = 0
            r6.releaseOutputBuffer(r2, r3)     // Catch:{ Exception -> 0x18d9, all -> 0x18c9 }
            r5 = r18
            r18 = r1
        L_0x1550:
            r1 = -1
            if (r2 == r1) goto L_0x1571
            r1 = r102
            r85 = r4
            r102 = r6
            r82 = r9
            r80 = r10
            r81 = r12
            r86 = r13
            r4 = r21
            r3 = r24
            r2 = r48
            r12 = r51
            r84 = r58
            r10 = r66
            r8 = r104
            goto L_0x11eb
        L_0x1571:
            if (r49 != 0) goto L_0x1899
            r56 = r2
            r1 = 2500(0x9c4, double:1.235E-320)
            int r3 = r7.dequeueOutputBuffer(r14, r1)     // Catch:{ Exception -> 0x1880, all -> 0x1870 }
            r8 = -1
            if (r3 != r8) goto L_0x1596
            r11 = 0
            r85 = r4
            r55 = r5
            r82 = r9
            r80 = r10
            r3 = r11
            r86 = r13
            r72 = r51
            r10 = r66
            r4 = r78
            r28 = -5
            r13 = r101
            goto L_0x18b3
        L_0x1596:
            r11 = -3
            if (r3 != r11) goto L_0x15ad
            r85 = r4
            r55 = r5
            r82 = r9
            r80 = r10
            r86 = r13
            r1 = r51
            r4 = r78
            r28 = -5
            r13 = r101
            goto L_0x18ad
        L_0x15ad:
            r11 = -2
            if (r3 != r11) goto L_0x1603
            android.media.MediaFormat r11 = r7.getOutputFormat()     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            boolean r36 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            if (r36 == 0) goto L_0x15cc
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r1.<init>()     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            java.lang.String r2 = "newFormat = "
            r1.append(r2)     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r1.append(r11)     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
        L_0x15cc:
            r85 = r4
            r55 = r5
            r82 = r9
            r80 = r10
            r86 = r13
            r1 = r51
            r4 = r78
            r28 = -5
            r13 = r101
            goto L_0x18ad
        L_0x15e0:
            r0 = move-exception
            r14 = r99
            r10 = r100
            r13 = r101
            r1 = r0
            r6 = r62
            r7 = r69
            goto L_0x1c4f
        L_0x15ee:
            r0 = move-exception
            r13 = r101
            r26 = r102
            r35 = r108
            r3 = r0
            r18 = r5
            r24 = r6
            r1 = r7
            r4 = r47
            r2 = r69
            r25 = r78
            goto L_0x1b65
        L_0x1603:
            if (r3 < 0) goto L_0x182c
            int r1 = r14.size     // Catch:{ Exception -> 0x1880, all -> 0x1870 }
            if (r1 == 0) goto L_0x160b
            r1 = 1
            goto L_0x160c
        L_0x160b:
            r1 = 0
        L_0x160c:
            r82 = r9
            long r8 = r14.presentationTimeUs     // Catch:{ Exception -> 0x1880, all -> 0x1870 }
            r70 = 0
            int r2 = (r66 > r70 ? 1 : (r66 == r70 ? 0 : -1))
            if (r2 <= 0) goto L_0x1625
            int r2 = (r8 > r66 ? 1 : (r8 == r66 ? 0 : -1))
            if (r2 < 0) goto L_0x1625
            r46 = 1
            r49 = 1
            r1 = 0
            int r2 = r14.flags     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
            r2 = r2 | 4
            r14.flags = r2     // Catch:{ Exception -> 0x15ee, all -> 0x15e0 }
        L_0x1625:
            r2 = 0
            r70 = 0
            int r11 = (r60 > r70 ? 1 : (r60 == r70 ? 0 : -1))
            if (r11 < 0) goto L_0x16f7
            int r11 = r14.flags     // Catch:{ Exception -> 0x16de, all -> 0x16d7 }
            r11 = r11 & 4
            if (r11 == 0) goto L_0x16f7
            r80 = r10
            r10 = r104
            long r70 = r60 - r10
            long r70 = java.lang.Math.abs(r70)     // Catch:{ Exception -> 0x16d3, all -> 0x1870 }
            r57 = 1000000(0xvar_, float:1.401298E-39)
            r59 = r1
            r86 = r13
            r13 = r101
            int r1 = r57 / r13
            r57 = r2
            long r1 = (long) r1
            int r63 = (r70 > r1 ? 1 : (r70 == r1 ? 0 : -1))
            if (r63 <= 0) goto L_0x16c7
            r1 = 0
            int r63 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r63 <= 0) goto L_0x1673
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x1660, all -> 0x1955 }
            r2 = 0
            r1.seekTo(r10, r2)     // Catch:{ Exception -> 0x1660, all -> 0x1955 }
            r85 = r4
            r55 = r5
            r2 = 0
            goto L_0x167f
        L_0x1660:
            r0 = move-exception
            r26 = r102
            r35 = r108
            r3 = r0
            r18 = r5
            r24 = r6
            r1 = r7
            r4 = r47
            r2 = r69
            r25 = r78
            goto L_0x1b65
        L_0x1673:
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x16d1, all -> 0x16ce }
            r85 = r4
            r55 = r5
            r2 = 0
            r4 = 0
            r1.seekTo(r4, r2)     // Catch:{ Exception -> 0x1723, all -> 0x184f }
        L_0x167f:
            long r53 = r51 + r33
            r4 = r60
            r60 = -1
            r1 = 0
            r46 = 0
            r49 = 0
            int r2 = r14.flags     // Catch:{ Exception -> 0x16b2, all -> 0x16a2 }
            r28 = -5
            r2 = r2 & -5
            r14.flags = r2     // Catch:{ Exception -> 0x16b2, all -> 0x16a2 }
            r7.flush()     // Catch:{ Exception -> 0x16b2, all -> 0x16a2 }
            r2 = 1
            r66 = r4
            r90 = r46
            r46 = r1
            r1 = r49
            r49 = r90
            goto L_0x170d
        L_0x16a2:
            r0 = move-exception
            r14 = r99
            r10 = r100
            r1 = r0
            r66 = r4
            r5 = r55
            r6 = r62
            r7 = r69
            goto L_0x1c4f
        L_0x16b2:
            r0 = move-exception
            r26 = r102
            r35 = r108
            r3 = r0
            r66 = r4
            r24 = r6
            r1 = r7
            r4 = r47
            r18 = r55
            r2 = r69
            r25 = r78
            goto L_0x1b65
        L_0x16c7:
            r85 = r4
            r55 = r5
            r28 = -5
            goto L_0x1709
        L_0x16ce:
            r0 = move-exception
            goto L_0x1873
        L_0x16d1:
            r0 = move-exception
            goto L_0x16e3
        L_0x16d3:
            r0 = move-exception
            r13 = r101
            goto L_0x16e3
        L_0x16d7:
            r0 = move-exception
            r13 = r101
            r10 = r104
            goto L_0x1873
        L_0x16de:
            r0 = move-exception
            r13 = r101
            r10 = r104
        L_0x16e3:
            r55 = r5
            r26 = r102
            r35 = r108
            r3 = r0
            r24 = r6
            r1 = r7
            r4 = r47
            r18 = r55
            r2 = r69
            r25 = r78
            goto L_0x1b65
        L_0x16f7:
            r59 = r1
            r57 = r2
            r85 = r4
            r55 = r5
            r80 = r10
            r86 = r13
            r28 = -5
            r13 = r101
            r10 = r104
        L_0x1709:
            r2 = r57
            r1 = r59
        L_0x170d:
            r4 = 0
            int r57 = (r41 > r4 ? 1 : (r41 == r4 ? 0 : -1))
            if (r57 <= 0) goto L_0x1736
            long r4 = r14.presentationTimeUs     // Catch:{ Exception -> 0x1723, all -> 0x184f }
            long r4 = r4 - r41
            int r57 = (r4 > r64 ? 1 : (r4 == r64 ? 0 : -1))
            if (r57 >= 0) goto L_0x1736
            int r4 = r14.flags     // Catch:{ Exception -> 0x1723, all -> 0x184f }
            r4 = r4 & 4
            if (r4 != 0) goto L_0x1736
            r1 = 0
            goto L_0x1736
        L_0x1723:
            r0 = move-exception
            r26 = r102
            r35 = r108
            r3 = r0
            r24 = r6
            r1 = r7
            r4 = r47
            r18 = r55
            r2 = r69
            r25 = r78
            goto L_0x1b65
        L_0x1736:
            r4 = 0
            int r57 = (r60 > r4 ? 1 : (r60 == r4 ? 0 : -1))
            if (r57 < 0) goto L_0x173f
            r70 = r60
            goto L_0x1741
        L_0x173f:
            r70 = r10
        L_0x1741:
            r72 = r70
            r10 = r72
            int r57 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r57 <= 0) goto L_0x178c
            r4 = -1
            int r57 = (r43 > r4 ? 1 : (r43 == r4 ? 0 : -1))
            if (r57 != 0) goto L_0x178c
            int r4 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r4 >= 0) goto L_0x177c
            r1 = 0
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1723, all -> 0x184f }
            if (r4 == 0) goto L_0x1779
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1723, all -> 0x184f }
            r4.<init>()     // Catch:{ Exception -> 0x1723, all -> 0x184f }
            java.lang.String r5 = "drop frame startTime = "
            r4.append(r5)     // Catch:{ Exception -> 0x1723, all -> 0x184f }
            r4.append(r10)     // Catch:{ Exception -> 0x1723, all -> 0x184f }
            java.lang.String r5 = " present time = "
            r4.append(r5)     // Catch:{ Exception -> 0x1723, all -> 0x184f }
            r70 = r8
            long r8 = r14.presentationTimeUs     // Catch:{ Exception -> 0x1723, all -> 0x184f }
            r4.append(r8)     // Catch:{ Exception -> 0x1723, all -> 0x184f }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x1723, all -> 0x184f }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x1723, all -> 0x184f }
            goto L_0x178e
        L_0x1779:
            r70 = r8
            goto L_0x178e
        L_0x177c:
            r70 = r8
            long r4 = r14.presentationTimeUs     // Catch:{ Exception -> 0x1723, all -> 0x184f }
            r43 = r4
            r4 = -2147483648(0xfffffffvar_, double:NaN)
            int r8 = (r51 > r4 ? 1 : (r51 == r4 ? 0 : -1))
            if (r8 == 0) goto L_0x178e
            long r53 = r53 - r43
            goto L_0x178e
        L_0x178c:
            r70 = r8
        L_0x178e:
            if (r2 == 0) goto L_0x1795
            r4 = -1
            r43 = r4
            goto L_0x17aa
        L_0x1795:
            r4 = -1
            int r8 = (r60 > r4 ? 1 : (r60 == r4 ? 0 : -1))
            if (r8 != 0) goto L_0x17a7
            r4 = 0
            int r8 = (r53 > r4 ? 1 : (r53 == r4 ? 0 : -1))
            if (r8 == 0) goto L_0x17a7
            long r4 = r14.presentationTimeUs     // Catch:{ Exception -> 0x1723, all -> 0x184f }
            long r4 = r4 + r53
            r14.presentationTimeUs = r4     // Catch:{ Exception -> 0x1723, all -> 0x184f }
        L_0x17a7:
            r7.releaseOutputBuffer(r3, r1)     // Catch:{ Exception -> 0x1817, all -> 0x184f }
        L_0x17aa:
            if (r1 == 0) goto L_0x17ee
            long r4 = r14.presentationTimeUs     // Catch:{ Exception -> 0x1817, all -> 0x184f }
            r41 = r4
            r4 = 0
            int r8 = (r60 > r4 ? 1 : (r60 == r4 ? 0 : -1))
            if (r8 < 0) goto L_0x17c0
            long r8 = r14.presentationTimeUs     // Catch:{ Exception -> 0x1723, all -> 0x184f }
            r4 = r51
            long r8 = java.lang.Math.max(r4, r8)     // Catch:{ Exception -> 0x1723, all -> 0x184f }
            r4 = r8
            goto L_0x17c2
        L_0x17c0:
            r4 = r51
        L_0x17c2:
            r8 = 0
            r102.awaitNewImage()     // Catch:{ Exception -> 0x17c7, all -> 0x184f }
            goto L_0x17cd
        L_0x17c7:
            r0 = move-exception
            r9 = r0
            r8 = 1
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)     // Catch:{ Exception -> 0x1817, all -> 0x184f }
        L_0x17cd:
            if (r8 != 0) goto L_0x17e6
            r102.drawImage()     // Catch:{ Exception -> 0x1817, all -> 0x184f }
            r51 = r1
            r9 = r2
            long r1 = r14.presentationTimeUs     // Catch:{ Exception -> 0x1817, all -> 0x184f }
            r72 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 * r72
            r72 = r4
            r4 = r78
            r4.setPresentationTime(r1)     // Catch:{ Exception -> 0x185d, all -> 0x184f }
            r4.swapBuffers()     // Catch:{ Exception -> 0x185d, all -> 0x184f }
            goto L_0x17f9
        L_0x17e6:
            r51 = r1
            r9 = r2
            r72 = r4
            r4 = r78
            goto L_0x17f9
        L_0x17ee:
            r9 = r2
            r4 = r78
            r90 = r51
            r51 = r1
            r1 = r90
            r72 = r1
        L_0x17f9:
            int r1 = r14.flags     // Catch:{ Exception -> 0x185d, all -> 0x184f }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x1811
            r1 = 0
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x185d, all -> 0x184f }
            if (r2 == 0) goto L_0x1809
            java.lang.String r2 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x185d, all -> 0x184f }
        L_0x1809:
            r6.signalEndOfInputStream()     // Catch:{ Exception -> 0x185d, all -> 0x184f }
            r3 = r1
            r10 = r66
            goto L_0x18b3
        L_0x1811:
            r3 = r24
            r10 = r66
            goto L_0x18b3
        L_0x1817:
            r0 = move-exception
            r4 = r78
            r26 = r102
            r35 = r108
            r3 = r0
            r25 = r4
            r24 = r6
            r1 = r7
            r4 = r47
            r18 = r55
            r2 = r69
            goto L_0x1b65
        L_0x182c:
            r13 = r101
            r55 = r5
            r82 = r9
            r80 = r10
            r1 = r51
            r4 = r78
            java.lang.RuntimeException r5 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x185d, all -> 0x184f }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x185d, all -> 0x184f }
            r8.<init>()     // Catch:{ Exception -> 0x185d, all -> 0x184f }
            java.lang.String r9 = "unexpected result from decoder.dequeueOutputBuffer: "
            r8.append(r9)     // Catch:{ Exception -> 0x185d, all -> 0x184f }
            r8.append(r3)     // Catch:{ Exception -> 0x185d, all -> 0x184f }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x185d, all -> 0x184f }
            r5.<init>(r8)     // Catch:{ Exception -> 0x185d, all -> 0x184f }
            throw r5     // Catch:{ Exception -> 0x185d, all -> 0x184f }
        L_0x184f:
            r0 = move-exception
            r14 = r99
            r10 = r100
            r1 = r0
            r5 = r55
            r6 = r62
            r7 = r69
            goto L_0x1c4f
        L_0x185d:
            r0 = move-exception
            r26 = r102
            r35 = r108
            r3 = r0
            r25 = r4
            r24 = r6
            r1 = r7
            r4 = r47
            r18 = r55
            r2 = r69
            goto L_0x1b65
        L_0x1870:
            r0 = move-exception
            r13 = r101
        L_0x1873:
            r55 = r5
            r14 = r99
            r10 = r100
            r1 = r0
            r6 = r62
            r7 = r69
            goto L_0x1c4f
        L_0x1880:
            r0 = move-exception
            r13 = r101
            r55 = r5
            r4 = r78
            r26 = r102
            r35 = r108
            r3 = r0
            r25 = r4
            r24 = r6
            r1 = r7
            r4 = r47
            r18 = r55
            r2 = r69
            goto L_0x1b65
        L_0x1899:
            r56 = r2
            r85 = r4
            r55 = r5
            r82 = r9
            r80 = r10
            r86 = r13
            r1 = r51
            r4 = r78
            r28 = -5
            r13 = r101
        L_0x18ad:
            r72 = r1
            r3 = r24
            r10 = r66
        L_0x18b3:
            r1 = r102
            r8 = r104
            r78 = r4
            r102 = r6
            r81 = r12
            r4 = r21
            r2 = r48
            r5 = r55
            r84 = r58
            r12 = r72
            goto L_0x11eb
        L_0x18c9:
            r0 = move-exception
            r13 = r101
            r14 = r99
            r10 = r100
            r1 = r0
            r5 = r18
            r6 = r62
            r7 = r69
            goto L_0x1c4f
        L_0x18d9:
            r0 = move-exception
            r13 = r101
            r4 = r78
            r26 = r102
            r35 = r108
            r3 = r0
            r25 = r4
            r24 = r6
            r1 = r7
            r4 = r47
            r2 = r69
            goto L_0x1b65
        L_0x18ee:
            r13 = r101
            r57 = r1
            r48 = r2
            r56 = r3
            r66 = r10
            r1 = r51
            r4 = r78
            java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1961, all -> 0x1955 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1961, all -> 0x1955 }
            r8.<init>()     // Catch:{ Exception -> 0x1961, all -> 0x1955 }
            java.lang.String r9 = "encoderOutputBuffer "
            r8.append(r9)     // Catch:{ Exception -> 0x1961, all -> 0x1955 }
            r9 = r56
            r8.append(r9)     // Catch:{ Exception -> 0x1961, all -> 0x1955 }
            java.lang.String r10 = " was null"
            r8.append(r10)     // Catch:{ Exception -> 0x1961, all -> 0x1955 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x1961, all -> 0x1955 }
            r3.<init>(r8)     // Catch:{ Exception -> 0x1961, all -> 0x1955 }
            throw r3     // Catch:{ Exception -> 0x1961, all -> 0x1955 }
        L_0x191a:
            r0 = move-exception
            r13 = r101
            r66 = r10
            r4 = r78
            r26 = r102
            r35 = r108
            r3 = r0
            r25 = r4
            r18 = r5
            r24 = r6
            r1 = r7
            r4 = r47
            r2 = r69
            goto L_0x1b65
        L_0x1933:
            r13 = r101
            r48 = r2
            r9 = r3
            r66 = r10
            r1 = r51
            r4 = r78
            java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1961, all -> 0x1955 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1961, all -> 0x1955 }
            r8.<init>()     // Catch:{ Exception -> 0x1961, all -> 0x1955 }
            java.lang.String r10 = "unexpected result from encoder.dequeueOutputBuffer: "
            r8.append(r10)     // Catch:{ Exception -> 0x1961, all -> 0x1955 }
            r8.append(r9)     // Catch:{ Exception -> 0x1961, all -> 0x1955 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x1961, all -> 0x1955 }
            r3.<init>(r8)     // Catch:{ Exception -> 0x1961, all -> 0x1955 }
            throw r3     // Catch:{ Exception -> 0x1961, all -> 0x1955 }
        L_0x1955:
            r0 = move-exception
            r14 = r99
            r10 = r100
            r1 = r0
            r6 = r62
            r7 = r69
            goto L_0x1c4f
        L_0x1961:
            r0 = move-exception
            r26 = r102
            r35 = r108
            r3 = r0
            r25 = r4
            r18 = r5
            r24 = r6
            r1 = r7
            r4 = r47
            r2 = r69
            goto L_0x1b65
        L_0x1974:
            r0 = move-exception
            r13 = r101
            r102 = r1
            r66 = r10
            r4 = r78
            r26 = r102
            r35 = r108
            r3 = r0
            r25 = r4
            r18 = r5
            r24 = r6
            r1 = r7
            r4 = r47
            r2 = r69
            goto L_0x1b65
        L_0x198f:
            r0 = move-exception
            r13 = r101
            r66 = r10
            r14 = r99
            r10 = r100
        L_0x1998:
            r1 = r0
            r6 = r62
            r7 = r69
            goto L_0x1c4f
        L_0x199f:
            r0 = move-exception
            r13 = r101
            r6 = r102
            r102 = r1
            r66 = r10
            r4 = r78
            r26 = r102
            r35 = r108
            r3 = r0
            r25 = r4
            r18 = r5
            r24 = r6
            r1 = r7
            r4 = r47
            r2 = r69
            goto L_0x1b65
        L_0x19bc:
            r0 = move-exception
            r13 = r101
            r51 = r10
            r14 = r99
            r10 = r100
        L_0x19c5:
            r1 = r0
            r5 = r21
            r66 = r51
            r6 = r62
            r7 = r69
            goto L_0x1c4f
        L_0x19d0:
            r0 = move-exception
            r13 = r101
            r6 = r102
            r102 = r1
            r108 = r2
            r47 = r4
            r51 = r10
            r37 = r14
            r4 = r78
            r14 = r87
            r26 = r102
            r35 = r108
            r3 = r0
            r25 = r4
            r24 = r6
            r1 = r7
            r18 = r21
            r4 = r47
            r66 = r51
            r2 = r69
            goto L_0x1b65
        L_0x19f7:
            r0 = move-exception
            r13 = r101
            r6 = r102
            r102 = r1
            r108 = r2
            r47 = r4
            r37 = r14
            r4 = r78
            r14 = r87
            r26 = r102
            r66 = r106
            r35 = r108
            r3 = r0
            r25 = r4
            r24 = r6
            r1 = r7
            r4 = r47
            r2 = r69
            goto L_0x1b65
        L_0x1a1a:
            r0 = move-exception
            r13 = r101
            r6 = r102
            r102 = r1
            r37 = r14
            r3 = r63
            r4 = r78
            r14 = r87
            r26 = r102
            r66 = r106
            r25 = r4
            r24 = r6
            r1 = r7
            r2 = r69
            r4 = r3
            r3 = r0
            goto L_0x1b65
        L_0x1a38:
            r0 = move-exception
            r13 = r101
            r6 = r102
            r102 = r1
            r37 = r14
            r3 = r63
            r7 = r77
            r4 = r78
            r14 = r87
            r26 = r102
            r66 = r106
            r25 = r4
            r24 = r6
            r1 = r7
            r2 = r69
            r4 = r3
            r3 = r0
            goto L_0x1b65
        L_0x1a58:
            r0 = move-exception
            r13 = r101
            r14 = r99
            r10 = r100
            r66 = r106
            r1 = r0
            r5 = r18
            r6 = r62
            r7 = r69
            goto L_0x1c4f
        L_0x1a6a:
            r0 = move-exception
            r13 = r101
            r6 = r102
            r37 = r14
            r3 = r63
            r7 = r77
            r4 = r78
            r14 = r87
            r66 = r106
            r25 = r4
            r24 = r6
            r1 = r7
            r2 = r69
            r4 = r3
            r3 = r0
            goto L_0x1b65
        L_0x1a86:
            r0 = move-exception
            r60 = r108
            r7 = r1
            r4 = r2
            r37 = r14
            r14 = r21
            r3 = r63
            r66 = r106
            r25 = r4
            r24 = r6
            r2 = r69
            r4 = r3
            r3 = r0
            goto L_0x1b65
        L_0x1a9d:
            r0 = move-exception
            r60 = r108
            r4 = r2
            r37 = r14
            r14 = r21
            r3 = r63
            r66 = r106
            r25 = r4
            r24 = r6
            r1 = r68
            r2 = r69
            r4 = r3
            r3 = r0
            goto L_0x1b65
        L_0x1ab5:
            r0 = move-exception
            r60 = r108
            r37 = r14
            r14 = r21
            r3 = r63
            r66 = r106
            r4 = r3
            r24 = r6
            r1 = r68
            r2 = r69
            r3 = r0
            goto L_0x1b65
        L_0x1aca:
            r0 = move-exception
            r60 = r108
            r14 = r99
            r10 = r100
            r66 = r106
            r1 = r0
            r5 = r18
            r6 = r62
            r7 = r69
            goto L_0x1c4f
        L_0x1adc:
            r0 = move-exception
            r60 = r108
            r37 = r14
            r14 = r21
            r3 = r63
            r66 = r106
            r4 = r3
            r1 = r68
            r2 = r69
            r3 = r0
            goto L_0x1b65
        L_0x1aef:
            r0 = move-exception
            r60 = r108
            r37 = r14
            r14 = r21
            r3 = r63
            r66 = r106
            r4 = r3
            r1 = r68
            r3 = r0
            goto L_0x1b65
        L_0x1b00:
            r0 = move-exception
            r60 = r108
            r14 = r99
            r10 = r100
            r66 = r106
            r1 = r0
            r7 = r2
            r5 = r18
            r6 = r62
            goto L_0x1c4f
        L_0x1b11:
            r0 = move-exception
            r60 = r108
            r68 = r1
            r37 = r14
            r14 = r21
            r3 = r63
            r66 = r106
            r4 = r3
            r3 = r0
            goto L_0x1b65
        L_0x1b21:
            r0 = move-exception
            r60 = r5
            r14 = r99
            r10 = r100
            r66 = r106
            r1 = r0
            r7 = r2
            r5 = r18
            r6 = r62
            goto L_0x1c4f
        L_0x1b32:
            r0 = move-exception
            r68 = r1
            r60 = r5
            r37 = r14
            r14 = r21
            r3 = r63
            r66 = r106
            r4 = r3
            r3 = r0
            goto L_0x1b65
        L_0x1b42:
            r0 = move-exception
            r14 = r99
            r10 = r100
            r7 = r102
            r66 = r106
            r60 = r108
            r1 = r0
            r5 = r18
            r6 = r62
            goto L_0x1c4f
        L_0x1b54:
            r0 = move-exception
            r68 = r1
            r37 = r14
            r14 = r21
            r3 = r63
            r2 = r102
            r66 = r106
            r60 = r108
            r4 = r3
            r3 = r0
        L_0x1b65:
            boolean r5 = r3 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x1bc2 }
            if (r5 == 0) goto L_0x1b6e
            if (r113 != 0) goto L_0x1b6e
            r5 = 1
            r17 = r5
        L_0x1b6e:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x1bc2 }
            r5.<init>()     // Catch:{ all -> 0x1bc2 }
            r6 = r62
            r5.append(r6)     // Catch:{ all -> 0x1bbc }
            r5.append(r2)     // Catch:{ all -> 0x1bbc }
            java.lang.String r7 = " framerate: "
            r5.append(r7)     // Catch:{ all -> 0x1bbc }
            r5.append(r13)     // Catch:{ all -> 0x1bbc }
            java.lang.String r7 = " size: "
            r5.append(r7)     // Catch:{ all -> 0x1bbc }
            r7 = r100
            r5.append(r7)     // Catch:{ all -> 0x1bb8 }
            java.lang.String r8 = "x"
            r5.append(r8)     // Catch:{ all -> 0x1bb8 }
            r8 = r99
            r5.append(r8)     // Catch:{ all -> 0x1bb6 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x1bb6 }
            org.telegram.messenger.FileLog.e((java.lang.String) r5)     // Catch:{ all -> 0x1bb6 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)     // Catch:{ all -> 0x1bb6 }
            r5 = 1
            r69 = r2
            r16 = r5
        L_0x1ba6:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x1be9 }
            r5 = r37
            r2.unselectTrack(r5)     // Catch:{ all -> 0x1be9 }
            if (r1 == 0) goto L_0x1be3
            r1.stop()     // Catch:{ all -> 0x1be9 }
            r1.release()     // Catch:{ all -> 0x1be9 }
            goto L_0x1be3
        L_0x1bb6:
            r0 = move-exception
            goto L_0x1bc9
        L_0x1bb8:
            r0 = move-exception
            r8 = r99
            goto L_0x1bc9
        L_0x1bbc:
            r0 = move-exception
            r8 = r99
            r7 = r100
            goto L_0x1bc9
        L_0x1bc2:
            r0 = move-exception
            r8 = r99
            r7 = r100
            r6 = r62
        L_0x1bc9:
            r1 = r0
            r10 = r7
            r14 = r8
            r5 = r18
            r7 = r2
            goto L_0x1c4f
        L_0x1bd1:
            r8 = r99
            r7 = r100
            r5 = r14
            r14 = r21
            r6 = r62
            r3 = r63
            r69 = r102
            r66 = r106
            r60 = r108
            r4 = r3
        L_0x1be3:
            if (r26 == 0) goto L_0x1bf3
            r26.release()     // Catch:{ all -> 0x1be9 }
            goto L_0x1bf3
        L_0x1be9:
            r0 = move-exception
            r1 = r0
            r10 = r7
            r14 = r8
            r5 = r18
            r7 = r69
            goto L_0x1c4f
        L_0x1bf3:
            if (r25 == 0) goto L_0x1bf8
            r25.release()     // Catch:{ all -> 0x1be9 }
        L_0x1bf8:
            if (r24 == 0) goto L_0x1CLASSNAME
            r24.stop()     // Catch:{ all -> 0x1be9 }
            r24.release()     // Catch:{ all -> 0x1be9 }
        L_0x1CLASSNAME:
            if (r35 == 0) goto L_0x1CLASSNAME
            r35.release()     // Catch:{ all -> 0x1be9 }
        L_0x1CLASSNAME:
            r92.checkConversionCanceled()     // Catch:{ all -> 0x1be9 }
            r1 = r18
        L_0x1c0a:
            android.media.MediaExtractor r2 = r15.extractor
            if (r2 == 0) goto L_0x1CLASSNAME
            r2.release()
        L_0x1CLASSNAME:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer
            if (r2 == 0) goto L_0x1CLASSNAME
            r2.finishMovie()     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ all -> 0x1CLASSNAME }
            long r2 = r2.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x1CLASSNAME }
            r15.endPresentationTime = r2     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x1CLASSNAME:
            r31 = r1
            r14 = r7
            r12 = r8
            r32 = r16
            r33 = r17
            r11 = r69
            goto L_0x1ca3
        L_0x1CLASSNAME:
            r0 = move-exception
            r8 = r99
            r13 = r6
            r7 = r10
            r6 = r5
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r0 = move-exception
            r13 = r8
            r8 = r9
            r7 = r10
            r6 = r19
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r0 = move-exception
            r7 = r10
            r6 = r13
            r13 = r8
            r8 = r9
        L_0x1CLASSNAME:
            r66 = r106
            r60 = r108
            r1 = r0
            r14 = r8
            r5 = r18
            r7 = r102
        L_0x1c4f:
            r16 = 1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x1d32 }
            r2.<init>()     // Catch:{ all -> 0x1d32 }
            r2.append(r6)     // Catch:{ all -> 0x1d32 }
            r2.append(r7)     // Catch:{ all -> 0x1d32 }
            java.lang.String r3 = " framerate: "
            r2.append(r3)     // Catch:{ all -> 0x1d32 }
            r2.append(r13)     // Catch:{ all -> 0x1d32 }
            java.lang.String r3 = " size: "
            r2.append(r3)     // Catch:{ all -> 0x1d32 }
            r2.append(r10)     // Catch:{ all -> 0x1d32 }
            java.lang.String r3 = "x"
            r2.append(r3)     // Catch:{ all -> 0x1d32 }
            r2.append(r14)     // Catch:{ all -> 0x1d32 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x1d32 }
            org.telegram.messenger.FileLog.e((java.lang.String) r2)     // Catch:{ all -> 0x1d32 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x1d32 }
            android.media.MediaExtractor r1 = r15.extractor
            if (r1 == 0) goto L_0x1CLASSNAME
            r1.release()
        L_0x1CLASSNAME:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer
            if (r1 == 0) goto L_0x1c9a
            r1.finishMovie()     // Catch:{ all -> 0x1CLASSNAME }
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ all -> 0x1CLASSNAME }
            long r1 = r1.getLastFrameTimestamp(r5)     // Catch:{ all -> 0x1CLASSNAME }
            r15.endPresentationTime = r1     // Catch:{ all -> 0x1CLASSNAME }
            goto L_0x1c9a
        L_0x1CLASSNAME:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1c9a:
            r31 = r5
            r11 = r7
            r12 = r14
            r32 = r16
            r33 = r17
            r14 = r10
        L_0x1ca3:
            if (r33 == 0) goto L_0x1cdc
            r22 = 1
            r1 = r92
            r2 = r93
            r3 = r94
            r4 = r95
            r5 = r96
            r6 = r97
            r7 = r98
            r8 = r12
            r9 = r14
            r10 = r101
            r99 = r11
            r88 = r12
            r12 = r103
            r89 = r14
            r13 = r104
            r15 = r66
            r17 = r60
            r19 = r110
            r21 = r112
            r23 = r114
            r24 = r115
            r25 = r116
            r26 = r117
            r27 = r118
            r28 = r119
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r15, r17, r19, r21, r22, r23, r24, r25, r26, r27, r28)
            return r1
        L_0x1cdc:
            r99 = r11
            r88 = r12
            r89 = r14
            long r1 = java.lang.System.currentTimeMillis()
            long r1 = r1 - r29
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x1d29
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "compression completed time="
            r3.append(r4)
            r3.append(r1)
            java.lang.String r4 = " needCompress="
            r3.append(r4)
            r4 = r112
            r3.append(r4)
            java.lang.String r5 = " w="
            r3.append(r5)
            r8 = r88
            r3.append(r8)
            java.lang.String r5 = " h="
            r3.append(r5)
            r7 = r89
            r3.append(r7)
            java.lang.String r5 = " bitrate="
            r3.append(r5)
            r5 = r99
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.d(r3)
            goto L_0x1d31
        L_0x1d29:
            r5 = r99
            r4 = r112
            r8 = r88
            r7 = r89
        L_0x1d31:
            return r32
        L_0x1d32:
            r0 = move-exception
            r4 = r112
            r1 = r0
            r2 = r92
            android.media.MediaExtractor r3 = r2.extractor
            if (r3 == 0) goto L_0x1d3f
            r3.release()
        L_0x1d3f:
            org.telegram.messenger.video.MP4Builder r3 = r2.mediaMuxer
            if (r3 == 0) goto L_0x1d54
            r3.finishMovie()     // Catch:{ all -> 0x1d4f }
            org.telegram.messenger.video.MP4Builder r3 = r2.mediaMuxer     // Catch:{ all -> 0x1d4f }
            long r8 = r3.getLastFrameTimestamp(r5)     // Catch:{ all -> 0x1d4f }
            r2.endPresentationTime = r8     // Catch:{ all -> 0x1d4f }
            goto L_0x1d54
        L_0x1d4f:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x1d54:
            goto L_0x1d56
        L_0x1d55:
            throw r1
        L_0x1d56:
            goto L_0x1d55
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, int, int, int, long, long, long, long, boolean, boolean, org.telegram.messenger.MediaController$SavedFilterState, java.lang.String, java.util.ArrayList, boolean, org.telegram.messenger.MediaController$CropState, boolean):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:71:0x013b, code lost:
        if (r14[r10 + 3] != 1) goto L_0x0141;
     */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x0224  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x023e  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0241 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00f1  */
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
            if (r11 < 0) goto L_0x00a2
            r2.selectTrack(r11)
            android.media.MediaFormat r13 = r2.getTrackFormat(r11)
            java.lang.String r0 = "mime"
            java.lang.String r0 = r13.getString(r0)
            java.lang.String r14 = "audio/unknown"
            boolean r0 = r0.equals(r14)
            if (r0 == 0) goto L_0x0080
            r11 = -1
            goto L_0x00a2
        L_0x0080:
            r14 = 1
            int r10 = r3.addTrack(r13, r14)
            int r0 = r13.getInteger(r9)     // Catch:{ Exception -> 0x008f }
            int r0 = java.lang.Math.max(r0, r12)     // Catch:{ Exception -> 0x008f }
            r12 = r0
            goto L_0x0093
        L_0x008f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0093:
            r14 = 0
            int r0 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x009e
            r9 = 0
            r2.seekTo(r5, r9)
            goto L_0x00a2
        L_0x009e:
            r9 = 0
            r2.seekTo(r14, r9)
        L_0x00a2:
            if (r12 > 0) goto L_0x00a6
            r12 = 65536(0x10000, float:9.18355E-41)
        L_0x00a6:
            java.nio.ByteBuffer r0 = java.nio.ByteBuffer.allocateDirect(r12)
            r13 = -1
            if (r11 >= 0) goto L_0x00b2
            if (r8 < 0) goto L_0x00b1
            goto L_0x00b2
        L_0x00b1:
            return r13
        L_0x00b2:
            r25 = -1
            r31.checkConversionCanceled()
        L_0x00b7:
            if (r18 != 0) goto L_0x0250
            r31.checkConversionCanceled()
            r9 = 0
            int r15 = android.os.Build.VERSION.SDK_INT
            r13 = 28
            if (r15 < r13) goto L_0x00d7
            long r13 = r32.getSampleSize()
            r15 = r0
            long r0 = (long) r12
            int r17 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1))
            if (r17 <= 0) goto L_0x00d8
            r0 = 1024(0x400, double:5.06E-321)
            long r0 = r0 + r13
            int r1 = (int) r0
            java.nio.ByteBuffer r0 = java.nio.ByteBuffer.allocateDirect(r1)
            r12 = r1
            goto L_0x00d9
        L_0x00d7:
            r15 = r0
        L_0x00d8:
            r0 = r15
        L_0x00d9:
            r1 = 0
            int r13 = r2.readSampleData(r0, r1)
            r4.size = r13
            int r1 = r32.getSampleTrackIndex()
            if (r1 != r8) goto L_0x00e9
            r13 = r21
            goto L_0x00ee
        L_0x00e9:
            if (r1 != r11) goto L_0x00ed
            r13 = r10
            goto L_0x00ee
        L_0x00ed:
            r13 = -1
        L_0x00ee:
            r14 = -1
            if (r13 == r14) goto L_0x0224
            int r14 = android.os.Build.VERSION.SDK_INT
            r15 = 21
            if (r14 >= r15) goto L_0x0100
            r14 = 0
            r0.position(r14)
            int r14 = r4.size
            r0.limit(r14)
        L_0x0100:
            if (r1 == r11) goto L_0x018b
            byte[] r14 = r0.array()
            if (r14 == 0) goto L_0x0182
            int r15 = r0.arrayOffset()
            int r17 = r0.limit()
            int r17 = r15 + r17
            r22 = -1
            r27 = r15
            r28 = r9
            r9 = r22
            r22 = r10
            r10 = r27
        L_0x011e:
            r27 = r12
            int r12 = r17 + -4
            if (r10 > r12) goto L_0x017d
            byte r12 = r14[r10]
            if (r12 != 0) goto L_0x013e
            int r12 = r10 + 1
            byte r12 = r14[r12]
            if (r12 != 0) goto L_0x013e
            int r12 = r10 + 2
            byte r12 = r14[r12]
            if (r12 != 0) goto L_0x013e
            int r12 = r10 + 3
            byte r12 = r14[r12]
            r29 = r15
            r15 = 1
            if (r12 == r15) goto L_0x0145
            goto L_0x0141
        L_0x013e:
            r29 = r15
            r15 = 1
        L_0x0141:
            int r12 = r17 + -4
            if (r10 != r12) goto L_0x0172
        L_0x0145:
            r12 = -1
            if (r9 == r12) goto L_0x016e
            int r12 = r10 - r9
            int r15 = r17 + -4
            if (r10 == r15) goto L_0x0150
            r15 = 4
            goto L_0x0151
        L_0x0150:
            r15 = 0
        L_0x0151:
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
            goto L_0x0174
        L_0x016e:
            r30 = r11
            r9 = r10
            goto L_0x0174
        L_0x0172:
            r30 = r11
        L_0x0174:
            int r10 = r10 + 1
            r12 = r27
            r15 = r29
            r11 = r30
            goto L_0x011e
        L_0x017d:
            r30 = r11
            r29 = r15
            goto L_0x0193
        L_0x0182:
            r28 = r9
            r22 = r10
            r30 = r11
            r27 = r12
            goto L_0x0193
        L_0x018b:
            r28 = r9
            r22 = r10
            r30 = r11
            r27 = r12
        L_0x0193:
            int r9 = r4.size
            if (r9 < 0) goto L_0x01a0
            long r9 = r32.getSampleTime()
            r4.presentationTimeUs = r9
            r9 = r28
            goto L_0x01a4
        L_0x01a0:
            r9 = 0
            r4.size = r9
            r9 = 1
        L_0x01a4:
            int r10 = r4.size
            if (r10 <= 0) goto L_0x0217
            if (r9 != 0) goto L_0x0217
            if (r1 != r8) goto L_0x01bd
            r10 = 0
            int r12 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r12 <= 0) goto L_0x01bd
            r10 = -1
            int r12 = (r25 > r10 ? 1 : (r25 == r10 ? 0 : -1))
            if (r12 != 0) goto L_0x01bf
            long r14 = r4.presentationTimeUs
            r25 = r14
            goto L_0x01bf
        L_0x01bd:
            r10 = -1
        L_0x01bf:
            r14 = 0
            int r12 = (r37 > r14 ? 1 : (r37 == r14 ? 0 : -1))
            if (r12 < 0) goto L_0x01d4
            long r14 = r4.presentationTimeUs
            int r12 = (r14 > r37 ? 1 : (r14 == r37 ? 0 : -1))
            if (r12 >= 0) goto L_0x01cc
            goto L_0x01d4
        L_0x01cc:
            r9 = 1
            r19 = 0
            r12 = r31
            r17 = r0
            goto L_0x021d
        L_0x01d4:
            r14 = 0
            r4.offset = r14
            int r12 = r32.getSampleFlags()
            r4.flags = r12
            long r10 = r3.writeSampleData(r13, r0, r4, r14)
            r19 = 0
            int r12 = (r10 > r19 ? 1 : (r10 == r19 ? 0 : -1))
            if (r12 == 0) goto L_0x0212
            r15 = r31
            org.telegram.messenger.MediaController$VideoConvertorListener r12 = r15.callback
            if (r12 == 0) goto L_0x020e
            long r14 = r4.presentationTimeUs
            long r14 = r14 - r25
            int r12 = (r14 > r23 ? 1 : (r14 == r23 ? 0 : -1))
            if (r12 <= 0) goto L_0x01fc
            long r14 = r4.presentationTimeUs
            long r23 = r14 - r25
            r14 = r23
            goto L_0x01fe
        L_0x01fc:
            r14 = r23
        L_0x01fe:
            r12 = r31
            r17 = r0
            org.telegram.messenger.MediaController$VideoConvertorListener r0 = r12.callback
            float r3 = (float) r14
            float r3 = r3 / r16
            float r3 = r3 / r7
            r0.didWriteData(r10, r3)
            r23 = r14
            goto L_0x0216
        L_0x020e:
            r17 = r0
            r12 = r15
            goto L_0x0216
        L_0x0212:
            r12 = r31
            r17 = r0
        L_0x0216:
            goto L_0x021d
        L_0x0217:
            r19 = 0
            r12 = r31
            r17 = r0
        L_0x021d:
            if (r9 != 0) goto L_0x0222
            r32.advance()
        L_0x0222:
            r3 = -1
            goto L_0x023c
        L_0x0224:
            r17 = r0
            r28 = r9
            r22 = r10
            r30 = r11
            r27 = r12
            r19 = 0
            r12 = r31
            r3 = -1
            if (r1 != r3) goto L_0x0237
            r9 = 1
            goto L_0x023c
        L_0x0237:
            r32.advance()
            r9 = r28
        L_0x023c:
            if (r9 == 0) goto L_0x0241
            r0 = 1
            r18 = r0
        L_0x0241:
            r3 = r33
            r1 = r12
            r0 = r17
            r10 = r22
            r12 = r27
            r11 = r30
            r13 = -1
            goto L_0x00b7
        L_0x0250:
            r17 = r0
            r22 = r10
            r30 = r11
            if (r8 < 0) goto L_0x025b
            r2.unselectTrack(r8)
        L_0x025b:
            if (r30 < 0) goto L_0x0263
            r11 = r30
            r2.unselectTrack(r11)
            goto L_0x0265
        L_0x0263:
            r11 = r30
        L_0x0265:
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

    private static String createFragmentShader(int srcWidth, int srcHeight, int dstWidth, int dstHeight, boolean external) {
        int kernelRadius = (int) Utilities.clamp((((float) Math.max(srcWidth, srcHeight)) / ((float) Math.max(dstHeight, dstWidth))) * 0.8f, 2.0f, 1.0f);
        FileLog.d("source size " + srcWidth + "x" + srcHeight + "    dest size " + dstWidth + dstHeight + "   kernelRadius " + kernelRadius);
        if (external) {
            return "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nconst float kernel = " + kernelRadius + ".0;\nconst float pixelSizeX = 1.0 / " + srcWidth + ".0;\nconst float pixelSizeY = 1.0 / " + srcHeight + ".0;\nuniform samplerExternalOES sTexture;\nvoid main() {\nvec3 accumulation = vec3(0);\nvec3 weightsum = vec3(0);\nfor (float x = -kernel; x <= kernel; x++){\n   for (float y = -kernel; y <= kernel; y++){\n       accumulation += texture2D(sTexture, vTextureCoord + vec2(x * pixelSizeX, y * pixelSizeY)).xyz;\n       weightsum += 1.0;\n   }\n}\ngl_FragColor = vec4(accumulation / weightsum, 1.0);\n}\n";
        }
        return "precision mediump float;\nvarying vec2 vTextureCoord;\nconst float kernel = " + kernelRadius + ".0;\nconst float pixelSizeX = 1.0 / " + srcHeight + ".0;\nconst float pixelSizeY = 1.0 / " + srcWidth + ".0;\nuniform sampler2D sTexture;\nvoid main() {\nvec3 accumulation = vec3(0);\nvec3 weightsum = vec3(0);\nfor (float x = -kernel; x <= kernel; x++){\n   for (float y = -kernel; y <= kernel; y++){\n       accumulation += texture2D(sTexture, vTextureCoord + vec2(x * pixelSizeX, y * pixelSizeY)).xyz;\n       weightsum += 1.0;\n   }\n}\ngl_FragColor = vec4(accumulation / weightsum, 1.0);\n}\n";
    }

    public class ConversionCanceledException extends RuntimeException {
        public ConversionCanceledException() {
            super("canceled conversion");
        }
    }
}
