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
    MediaController.VideoConvertorListener callback;
    MediaExtractor extractor;
    MP4Builder mediaMuxer;

    public boolean convertVideo(String str, File file, int i, boolean z, int i2, int i3, int i4, int i5, long j, long j2, boolean z2, long j3, MediaController.SavedFilterState savedFilterState, String str2, ArrayList<VideoEditedInfo.MediaEntity> arrayList, boolean z3, MediaController.VideoConvertorListener videoConvertorListener) {
        String str3 = str;
        long j4 = j3;
        this.callback = videoConvertorListener;
        return convertVideoInternal(str, file, i, z, i2, i3, i4, i5, j, j2, j4, z2, false, savedFilterState, str2, arrayList, z3);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v0, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v4, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v5, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v1, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v6, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v2, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v6, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v0, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v3, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v7, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v11, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v1, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v4, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v18, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v8, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v13, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v5, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v19, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v9, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v14, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v2, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v20, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v15, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v10, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v6, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v21, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v13, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v18, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v7, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v8, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v20, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v3, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v9, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v25, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v25, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v14, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v22, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v4, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v10, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v11, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v0, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v28, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v16, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v40, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v29, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v17, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v30, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v18, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v12, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v19, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v13, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v20, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v1, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v14, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v34, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v22, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v35, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v2, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v23, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v15, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v3, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v16, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v38, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v25, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v50, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v51, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v39, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v26, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v27, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v40, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v53, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v41, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v8, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v42, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v28, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v17, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v18, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v43, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v55, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v44, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v56, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v4, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v19, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v1, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v46, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v20, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v5, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v48, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v2, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v18, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v15, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v6, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v50, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v60, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v22, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v29, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v16, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v7, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v3, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v30, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v61, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v83, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v4, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v52, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v8, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v53, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v5, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v54, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v66, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v24, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v6, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v10, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v7, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v12, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v22, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v27, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v55, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v69, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v56, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v70, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v57, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v71, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v28, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v8, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v29, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v75, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v76, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v24, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v9, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v60, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v61, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v10, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v53, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v81, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v63, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v32, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v64, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v82, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v56, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v57, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v55, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v58, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v88, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v60, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v60, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v96, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v65, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v67, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v67, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v102, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v72, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v75, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v103, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v73, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v76, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v74, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v33, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v34, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v84, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v31, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v77, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v36, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v104, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v32, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v78, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v37, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v105, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v79, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v106, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v38, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v80, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v57, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v107, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v81, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v108, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v82, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v109, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v60, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v84, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v62, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v110, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v88, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v64, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v114, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v95, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v123, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v33, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v69, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v96, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v69, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v126, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v97, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v70, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v34, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v98, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v43, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v130, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v71, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v99, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v100, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v35, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v131, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v36, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v74, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v133, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v37, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v73, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v103, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v76, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v134, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v135, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v38, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v105, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v136, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v137, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v138, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v139, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v141, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v135, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v39, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v113, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v78, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v142, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v40, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v75, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v114, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v53, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v79, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v143, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v76, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v80, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v144, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v41, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v145, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v81, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v56, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v146, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v57, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v58, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v78, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v81, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v148, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v135, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v81, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v138, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v153, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v50, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v139, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v154, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v140, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v51, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v141, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v88, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v142, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v157, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v143, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v144, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v160, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v145, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v53, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v146, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v147, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v55, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v148, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v56, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v149, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v150, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v57, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v151, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v58, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v152, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v59, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v37, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v127, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v105, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v128, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v106, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v129, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v107, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v130, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v108, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v131, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v109, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v110, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v113, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v114, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v133, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v134, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v135, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v136, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v138, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v139, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v140, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v115, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v120, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v121, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v141, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v142, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v127, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v144, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v128, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v38, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v129, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v139, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v39, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v130, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v40, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v131, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v132, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v133, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v134, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v135, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v136, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v137, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v196, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v41, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v45, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v164, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v165, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v52, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v284, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v168, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v169, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v170, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v171, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v172, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v173, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v174, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v175, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v176, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v177, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v285, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v60, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v179, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v182, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v183, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v61, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v62, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v287, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v288, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v64, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v289, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v186, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v187, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v60, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v61, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v188, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v189, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v138, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v139, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v140, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v141, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v142, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v143, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v144, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v145, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v146, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v147, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v148, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v149, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v150, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v151, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v152, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v153, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v154, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v58, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v155, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v156, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v157, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v159, resolved type: float} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v193, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v160, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v161, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r56v59, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v162, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v163, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v196, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v164, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v166, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v168, resolved type: int} */
    /* JADX WARNING: type inference failed for: r35v8 */
    /* JADX WARNING: type inference failed for: r35v10 */
    /* JADX WARNING: type inference failed for: r35v11 */
    /* JADX WARNING: type inference failed for: r35v12 */
    /* JADX WARNING: type inference failed for: r35v13 */
    /* JADX WARNING: type inference failed for: r35v14 */
    /* JADX WARNING: type inference failed for: r35v15 */
    /* JADX WARNING: type inference failed for: r35v16 */
    /* JADX WARNING: type inference failed for: r35v17 */
    /* JADX WARNING: type inference failed for: r35v18 */
    /* JADX WARNING: type inference failed for: r35v19 */
    /* JADX WARNING: type inference failed for: r35v20 */
    /* JADX WARNING: type inference failed for: r35v21 */
    /* JADX WARNING: type inference failed for: r35v22 */
    /* JADX WARNING: type inference failed for: r8v26 */
    /* JADX WARNING: type inference failed for: r35v24 */
    /* JADX WARNING: type inference failed for: r35v25 */
    /* JADX WARNING: type inference failed for: r35v26 */
    /* JADX WARNING: type inference failed for: r35v27 */
    /* JADX WARNING: type inference failed for: r35v28 */
    /* JADX WARNING: type inference failed for: r35v29 */
    /* JADX WARNING: type inference failed for: r35v30 */
    /* JADX WARNING: type inference failed for: r53v2 */
    /* JADX WARNING: type inference failed for: r35v31 */
    /* JADX WARNING: type inference failed for: r35v32 */
    /* JADX WARNING: type inference failed for: r35v33 */
    /* JADX WARNING: type inference failed for: r35v34 */
    /* JADX WARNING: type inference failed for: r35v35 */
    /* JADX WARNING: type inference failed for: r35v36 */
    /* JADX WARNING: type inference failed for: r13v16 */
    /* JADX WARNING: type inference failed for: r35v37 */
    /* JADX WARNING: type inference failed for: r35v38 */
    /* JADX WARNING: type inference failed for: r13v20 */
    /* JADX WARNING: type inference failed for: r35v39 */
    /* JADX WARNING: type inference failed for: r1v93, types: [boolean] */
    /* JADX WARNING: type inference failed for: r35v40 */
    /* JADX WARNING: type inference failed for: r35v41 */
    /* JADX WARNING: type inference failed for: r35v42 */
    /* JADX WARNING: type inference failed for: r35v43 */
    /* JADX WARNING: type inference failed for: r35v44 */
    /* JADX WARNING: type inference failed for: r53v16 */
    /* JADX WARNING: type inference failed for: r53v17 */
    /* JADX WARNING: type inference failed for: r8v37, types: [java.lang.Object, java.lang.String] */
    /* JADX WARNING: type inference failed for: r35v46 */
    /* JADX WARNING: type inference failed for: r35v47 */
    /* JADX WARNING: type inference failed for: r8v44 */
    /* JADX WARNING: type inference failed for: r35v48 */
    /* JADX WARNING: type inference failed for: r8v46 */
    /* JADX WARNING: type inference failed for: r2v170 */
    /* JADX WARNING: type inference failed for: r2v171 */
    /* JADX WARNING: type inference failed for: r3v100 */
    /* JADX WARNING: type inference failed for: r3v101 */
    /* JADX WARNING: type inference failed for: r2v215 */
    /* JADX WARNING: type inference failed for: r13v56 */
    /* JADX WARNING: type inference failed for: r13v57 */
    /* JADX WARNING: type inference failed for: r13v58 */
    /* JADX WARNING: type inference failed for: r13v59 */
    /* JADX WARNING: type inference failed for: r13v63 */
    /* JADX WARNING: type inference failed for: r8v158 */
    /* JADX WARNING: type inference failed for: r3v140 */
    /* JADX WARNING: Code restructure failed: missing block: B:1013:0x12bf, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1014:0x12c0, code lost:
        r1 = r0;
        r9 = r27;
        r10 = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1033:0x12f9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1034:0x12fa, code lost:
        r2 = r0;
        r1 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1035:0x12fe, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1036:0x12ff, code lost:
        r8 = r7;
        r2 = r11;
        r11 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1061:0x1387, code lost:
        r3.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1065:?, code lost:
        r3.finishMovie();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1066:0x1392, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1067:0x1393, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x042e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x042f, code lost:
        r34 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x046f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x0470, code lost:
        r4 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x048e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x0512, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x0513, code lost:
        r10 = r1;
        r8 = r7;
        r2 = r11;
        r11 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x0535, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x0536, code lost:
        r1 = r0;
        r10 = r2;
        r8 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x053a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x053c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x053d, code lost:
        r9 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:273:0x053f, code lost:
        r8 = r73;
        r1 = r0;
        r10 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x0543, code lost:
        r2 = r11;
        r11 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:275:0x0546, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0547, code lost:
        r9 = r18;
        r8 = r73;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:277:0x054c, code lost:
        r2 = r11;
        r11 = r12;
        r10 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x057b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x057c, code lost:
        r1 = r0;
        r8 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x0729, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x072a, code lost:
        r2 = r0;
        r1 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x0783, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x0784, code lost:
        r4 = r0;
        r11 = r7;
        r2 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:432:0x07a0, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x07a1, code lost:
        r4 = r0;
        r11 = r7;
        r2 = r14;
        r1 = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x07c4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x07c5, code lost:
        r4 = r0;
        r52 = r1;
        r11 = r7;
        r2 = r14;
        r1 = r27;
        r59 = r33;
        r3 = null;
        r15 = r15;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x07d3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x07d4, code lost:
        r4 = r0;
        r11 = r7;
        r2 = r14;
        r1 = r27;
        r59 = r33;
        r3 = null;
        r15 = r15;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x08ed, code lost:
        r0 = e;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x08ee, code lost:
        r4 = r0;
        r56 = r5;
        r11 = r6;
        r2 = r14;
        r1 = r27;
        r59 = r54;
        r10 = false;
        r57 = null;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x095a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x095b, code lost:
        r7 = r75;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x09a8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x09aa, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x09ab, code lost:
        r1 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x09ad, code lost:
        r11 = r71;
        r6 = r74;
        r4 = r0;
        r57 = r5;
        r56 = r12;
        r2 = r14;
        r1 = r27;
        r59 = r54;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x09bd, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x09be, code lost:
        r1 = r77;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x09c1, code lost:
        r0 = e;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:552:0x09d6, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x09d7, code lost:
        r7 = r75;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x09d9, code lost:
        r1 = r77;
        r12 = r5;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x09dc, code lost:
        r11 = r71;
        r6 = r74;
        r4 = r0;
        r56 = r12;
        r2 = r14;
        r1 = r27;
        r59 = r54;
        r10 = false;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x0a47, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x0a48, code lost:
        r11 = r71;
        r4 = r0;
        r57 = r6;
        r2 = r14;
        r1 = r27;
        r59 = r54;
        r10 = false;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x0a54, code lost:
        r6 = r74;
        r56 = r56;
        r15 = r15;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0ac3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x0ac4, code lost:
        r11 = r71;
        r6 = r74;
        r4 = r0;
        r2 = r14;
        r56 = r56;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0187, code lost:
        r2 = r4;
        r3 = r5;
        r4 = r6;
        r5 = r7;
        r6 = r8;
        r7 = r9;
        r8 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x0b7e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x0b80, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x0b81, code lost:
        r59 = r54;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0b83, code lost:
        r11 = r71;
        r2 = r72;
        r6 = r74;
        r56 = r56;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x0c1b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x0dc7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x0dc8, code lost:
        r4 = r0;
        r2 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x0dcb, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x0dcc, code lost:
        r11 = r71;
        r2 = r72;
        r4 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x0dd1, code lost:
        r3 = r9;
        r56 = r56;
        r15 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x0dd4, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x0dd5, code lost:
        r9 = r3;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:280:0x055c] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:284:0x0572] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:498:0x08c6] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:501:0x08de] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:515:0x0912] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:526:0x0966] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:535:0x0987] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:538:0x098f] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:541:0x0994] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:571:0x0a40] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:579:0x0a60] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:584:0x0a72] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:604:0x0ad9] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:665:0x0bfc] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:702:0x0c9e] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:742:0x0d27] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:767:0x0daf] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:104:0x022e, B:991:0x1245] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1004:0x1290 A[Catch:{ Exception -> 0x12bf, all -> 0x12f9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1010:0x12a3 A[Catch:{ Exception -> 0x12bf, all -> 0x12f9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1012:0x12bb A[Catch:{ Exception -> 0x12bf, all -> 0x12f9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1016:0x12c9 A[Catch:{ Exception -> 0x12bf, all -> 0x12f9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1018:0x12ce A[Catch:{ Exception -> 0x12bf, all -> 0x12f9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1020:0x12d6 A[Catch:{ Exception -> 0x12bf, all -> 0x12f9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1025:0x12e2  */
    /* JADX WARNING: Removed duplicated region for block: B:1028:0x12e9 A[SYNTHETIC, Splitter:B:1028:0x12e9] */
    /* JADX WARNING: Removed duplicated region for block: B:1033:0x12f9 A[ExcHandler: all (r0v7 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r15 
      PHI: (r15v5 org.telegram.messenger.video.MediaCodecVideoConvertor) = (r15v7 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v12 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v36 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v36 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v36 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v36 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v36 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v36 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v36 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v36 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v68 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v68 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v68 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v68 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v68 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v68 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v68 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v68 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v68 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v68 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v68 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v68 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v68 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v7 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v7 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v86 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v87 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v87 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v87 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v87 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v87 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v87 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v87 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v87 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v100 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v100 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v100 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v106 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v106 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v106 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v106 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v106 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v106 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v106 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v111 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v124 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v126 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v151 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v152 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v154 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v155 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v156 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v157 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v158 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v159 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v161 org.telegram.messenger.video.MediaCodecVideoConvertor) binds: [B:280:0x055c, B:1001:0x1287, B:579:0x0a60, B:604:0x0ad9, B:605:?, B:610:0x0af2, B:608:0x0aea, B:609:?, B:584:0x0a72, B:571:0x0a40, B:498:0x08c6, B:508:0x08fe, B:526:0x0966, B:527:?, B:538:0x098f, B:539:?, B:541:0x0994, B:535:0x0987, B:531:0x097c, B:532:?, B:515:0x0912, B:519:0x0934, B:501:0x08de, B:292:0x0585, B:284:0x0572, B:258:0x050e, B:242:0x04c9, B:243:?, B:248:0x04d2, B:249:?, B:251:0x04de, B:252:?, B:254:0x04e8, B:255:?, B:208:0x03fe, B:209:?, B:211:0x0417, B:121:0x028c, B:122:?, B:197:0x03c9, B:134:0x02a6, B:128:0x0299, B:125:0x0294, B:126:?, B:104:0x022e, B:991:0x1245, B:998:0x1260, B:702:0x0c9e, B:703:?, B:767:0x0daf, B:736:0x0d15, B:742:0x0d27, B:707:0x0ca8, B:693:0x0c8e, B:694:?, B:665:0x0bfc] A[DONT_GENERATE, DONT_INLINE], Splitter:B:104:0x022e] */
    /* JADX WARNING: Removed duplicated region for block: B:1047:0x1345  */
    /* JADX WARNING: Removed duplicated region for block: B:1050:0x134c A[SYNTHETIC, Splitter:B:1050:0x134c] */
    /* JADX WARNING: Removed duplicated region for block: B:1056:0x135c  */
    /* JADX WARNING: Removed duplicated region for block: B:1061:0x1387  */
    /* JADX WARNING: Removed duplicated region for block: B:1064:0x138e A[SYNTHETIC, Splitter:B:1064:0x138e] */
    /* JADX WARNING: Removed duplicated region for block: B:1110:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x03e3  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x03f9  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x04cd A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x050e A[SYNTHETIC, Splitter:B:258:0x050e] */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x051c A[Catch:{ Exception -> 0x0512, all -> 0x12f9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0521 A[Catch:{ Exception -> 0x0512, all -> 0x12f9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x05de A[SYNTHETIC, Splitter:B:315:0x05de] */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0648 A[Catch:{ Exception -> 0x067d }] */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x071d A[SYNTHETIC, Splitter:B:394:0x071d] */
    /* JADX WARNING: Removed duplicated region for block: B:397:0x0729 A[ExcHandler: all (r0v85 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:394:0x071d] */
    /* JADX WARNING: Removed duplicated region for block: B:401:0x0738  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0744  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0748  */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x076d A[SYNTHETIC, Splitter:B:414:0x076d] */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0793  */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x07b6 A[SYNTHETIC, Splitter:B:443:0x07b6] */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x07de  */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x07f6 A[SYNTHETIC, Splitter:B:461:0x07f6] */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x085a  */
    /* JADX WARNING: Removed duplicated region for block: B:485:0x0894 A[SYNTHETIC, Splitter:B:485:0x0894] */
    /* JADX WARNING: Removed duplicated region for block: B:494:0x08bc  */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x08c4  */
    /* JADX WARNING: Removed duplicated region for block: B:511:0x090a  */
    /* JADX WARNING: Removed duplicated region for block: B:512:0x090c  */
    /* JADX WARNING: Removed duplicated region for block: B:514:0x0910  */
    /* JADX WARNING: Removed duplicated region for block: B:551:0x09c3  */
    /* JADX WARNING: Removed duplicated region for block: B:556:0x09ec  */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x0a04  */
    /* JADX WARNING: Removed duplicated region for block: B:559:0x0a07  */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x0a20 A[Catch:{ Exception -> 0x115c }] */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x0a3c A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:578:0x0a5e  */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0b90  */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0ba8 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bc8  */
    /* JADX WARNING: Removed duplicated region for block: B:655:0x0bcd  */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0bd8  */
    /* JADX WARNING: Removed duplicated region for block: B:663:0x0bf9  */
    /* JADX WARNING: Removed duplicated region for block: B:789:0x0e02  */
    /* JADX WARNING: Removed duplicated region for block: B:790:0x0e1f  */
    /* JADX WARNING: Removed duplicated region for block: B:900:0x0fe7 A[Catch:{ Exception -> 0x1032 }] */
    /* JADX WARNING: Removed duplicated region for block: B:919:0x102a A[Catch:{ Exception -> 0x1059 }] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:248:0x04d2=Splitter:B:248:0x04d2, B:610:0x0af2=Splitter:B:610:0x0af2, B:736:0x0d15=Splitter:B:736:0x0d15, B:991:0x1245=Splitter:B:991:0x1245, B:197:0x03c9=Splitter:B:197:0x03c9, B:242:0x04c9=Splitter:B:242:0x04c9, B:508:0x08fe=Splitter:B:508:0x08fe} */
    /* JADX WARNING: Unknown variable types count: 5 */
    @android.annotation.TargetApi(18)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideoInternal(java.lang.String r67, java.io.File r68, int r69, boolean r70, int r71, int r72, int r73, int r74, long r75, long r77, long r79, boolean r81, boolean r82, org.telegram.messenger.MediaController.SavedFilterState r83, java.lang.String r84, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r85, boolean r86) {
        /*
            r66 = this;
            r14 = r66
            r13 = r67
            r15 = r69
            r12 = r71
            r11 = r72
            r10 = r73
            r9 = r74
            r7 = r75
            r5 = r77
            r3 = r79
            android.media.MediaCodec$BufferInfo r1 = new android.media.MediaCodec$BufferInfo     // Catch:{ Exception -> 0x1308, all -> 0x1303 }
            r1.<init>()     // Catch:{ Exception -> 0x1308, all -> 0x1303 }
            org.telegram.messenger.video.Mp4Movie r2 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ Exception -> 0x1308, all -> 0x1303 }
            r2.<init>()     // Catch:{ Exception -> 0x1308, all -> 0x1303 }
            r13 = r68
            r2.setCacheFile(r13)     // Catch:{ Exception -> 0x1308, all -> 0x1303 }
            r2.setRotation(r15)     // Catch:{ Exception -> 0x1308, all -> 0x1303 }
            r2.setSize(r12, r11)     // Catch:{ Exception -> 0x1308, all -> 0x1303 }
            r18 = r1
            org.telegram.messenger.video.MP4Builder r1 = new org.telegram.messenger.video.MP4Builder     // Catch:{ Exception -> 0x1308, all -> 0x1303 }
            r1.<init>()     // Catch:{ Exception -> 0x1308, all -> 0x1303 }
            r13 = r70
            org.telegram.messenger.video.MP4Builder r1 = r1.createMovie(r2, r13)     // Catch:{ Exception -> 0x1308, all -> 0x1303 }
            r14.mediaMuxer = r1     // Catch:{ Exception -> 0x1308, all -> 0x1303 }
            float r1 = (float) r3     // Catch:{ Exception -> 0x1308, all -> 0x1303 }
            r19 = 1148846080(0x447a0000, float:1000.0)
            float r20 = r1 / r19
            r66.checkConversionCanceled()     // Catch:{ Exception -> 0x1308, all -> 0x1303 }
            java.lang.String r1 = "csd-0"
            java.lang.String r13 = "prepend-sps-pps-to-idr-frames"
            r22 = r13
            r23 = 4
            java.lang.String r13 = "video/avc"
            r2 = 16
            if (r86 == 0) goto L_0x0552
            if (r9 > 0) goto L_0x0054
            r15 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x0055
        L_0x0054:
            r15 = r9
        L_0x0055:
            int r9 = r12 % 16
            r27 = 1098907648(0x41800000, float:16.0)
            if (r9 == 0) goto L_0x009c
            boolean r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x008f }
            if (r9 == 0) goto L_0x0084
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x008f }
            r9.<init>()     // Catch:{ Exception -> 0x008f }
            java.lang.String r14 = "changing width from "
            r9.append(r14)     // Catch:{ Exception -> 0x008f }
            r9.append(r12)     // Catch:{ Exception -> 0x008f }
            java.lang.String r14 = " to "
            r9.append(r14)     // Catch:{ Exception -> 0x008f }
            float r14 = (float) r12     // Catch:{ Exception -> 0x008f }
            float r14 = r14 / r27
            int r14 = java.lang.Math.round(r14)     // Catch:{ Exception -> 0x008f }
            int r14 = r14 * 16
            r9.append(r14)     // Catch:{ Exception -> 0x008f }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x008f }
            org.telegram.messenger.FileLog.d(r9)     // Catch:{ Exception -> 0x008f }
        L_0x0084:
            float r9 = (float) r12     // Catch:{ Exception -> 0x008f }
            float r9 = r9 / r27
            int r9 = java.lang.Math.round(r9)     // Catch:{ Exception -> 0x008f }
            int r9 = r9 * 16
            r12 = r9
            goto L_0x009c
        L_0x008f:
            r0 = move-exception
            r1 = r0
            r18 = r15
            r14 = 0
            r21 = 0
            r34 = 0
        L_0x0098:
            r15 = r66
            goto L_0x04c9
        L_0x009c:
            int r9 = r11 % 16
            if (r9 == 0) goto L_0x00d3
            boolean r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x008f }
            if (r9 == 0) goto L_0x00c9
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x008f }
            r9.<init>()     // Catch:{ Exception -> 0x008f }
            java.lang.String r14 = "changing height from "
            r9.append(r14)     // Catch:{ Exception -> 0x008f }
            r9.append(r11)     // Catch:{ Exception -> 0x008f }
            java.lang.String r14 = " to "
            r9.append(r14)     // Catch:{ Exception -> 0x008f }
            float r14 = (float) r11     // Catch:{ Exception -> 0x008f }
            float r14 = r14 / r27
            int r14 = java.lang.Math.round(r14)     // Catch:{ Exception -> 0x008f }
            int r14 = r14 * 16
            r9.append(r14)     // Catch:{ Exception -> 0x008f }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x008f }
            org.telegram.messenger.FileLog.d(r9)     // Catch:{ Exception -> 0x008f }
        L_0x00c9:
            float r9 = (float) r11     // Catch:{ Exception -> 0x008f }
            float r9 = r9 / r27
            int r9 = java.lang.Math.round(r9)     // Catch:{ Exception -> 0x008f }
            int r2 = r9 * 16
            r11 = r2
        L_0x00d3:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x04be }
            if (r2 == 0) goto L_0x00fb
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x008f }
            r2.<init>()     // Catch:{ Exception -> 0x008f }
            java.lang.String r9 = "create photo encoder "
            r2.append(r9)     // Catch:{ Exception -> 0x008f }
            r2.append(r12)     // Catch:{ Exception -> 0x008f }
            java.lang.String r9 = " "
            r2.append(r9)     // Catch:{ Exception -> 0x008f }
            r2.append(r11)     // Catch:{ Exception -> 0x008f }
            java.lang.String r9 = " duration = "
            r2.append(r9)     // Catch:{ Exception -> 0x008f }
            r2.append(r3)     // Catch:{ Exception -> 0x008f }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x008f }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x008f }
        L_0x00fb:
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r13, r12, r11)     // Catch:{ Exception -> 0x04be }
            java.lang.String r9 = "color-format"
            r14 = 2130708361(0x7var_, float:1.701803E38)
            r2.setInteger(r9, r14)     // Catch:{ Exception -> 0x04be }
            java.lang.String r9 = "bitrate"
            r2.setInteger(r9, r15)     // Catch:{ Exception -> 0x04be }
            java.lang.String r9 = "frame-rate"
            r2.setInteger(r9, r10)     // Catch:{ Exception -> 0x04be }
            java.lang.String r9 = "i-frame-interval"
            r14 = 2
            r2.setInteger(r9, r14)     // Catch:{ Exception -> 0x04be }
            android.media.MediaCodec r14 = android.media.MediaCodec.createEncoderByType(r13)     // Catch:{ Exception -> 0x04be }
            r16 = r1
            r1 = 1
            r9 = 0
            r14.configure(r2, r9, r9, r1)     // Catch:{ Exception -> 0x04b7 }
            org.telegram.messenger.video.InputSurface r9 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x04b7 }
            android.view.Surface r2 = r14.createInputSurface()     // Catch:{ Exception -> 0x04b7 }
            r9.<init>(r2)     // Catch:{ Exception -> 0x04b7 }
            r9.makeCurrent()     // Catch:{ Exception -> 0x04ad }
            r14.start()     // Catch:{ Exception -> 0x04ad }
            org.telegram.messenger.video.OutputSurface r2 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x04ad }
            float r8 = (float) r10
            r27 = 1
            r33 = r16
            r32 = r18
            r7 = 1
            r1 = r2
            r34 = r2
            r2 = r83
            r3 = r67
            r4 = r84
            r5 = r85
            r6 = r12
            r7 = r11
            r10 = r9
            r9 = r27
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x04aa }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x04a0 }
            r2 = 21
            if (r1 >= r2) goto L_0x0161
            java.nio.ByteBuffer[] r1 = r14.getOutputBuffers()     // Catch:{ Exception -> 0x0159 }
            goto L_0x0162
        L_0x0159:
            r0 = move-exception
            r1 = r0
            r21 = r10
            r18 = r15
            goto L_0x0098
        L_0x0161:
            r1 = 0
        L_0x0162:
            r66.checkConversionCanceled()     // Catch:{ Exception -> 0x04a0 }
            r2 = 1
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = -5
            r8 = 0
        L_0x016d:
            if (r3 != 0) goto L_0x0490
            r66.checkConversionCanceled()     // Catch:{ Exception -> 0x04a0 }
            r16 = r4 ^ 1
            r64 = r4
            r4 = r2
            r2 = r16
            r16 = r8
            r8 = r6
            r9 = r7
            r6 = r64
            r7 = r5
            r5 = r3
            r3 = 1
        L_0x0182:
            if (r2 != 0) goto L_0x0190
            if (r3 == 0) goto L_0x0187
            goto L_0x0190
        L_0x0187:
            r2 = r4
            r3 = r5
            r4 = r6
            r5 = r7
            r6 = r8
            r7 = r9
            r8 = r16
            goto L_0x016d
        L_0x0190:
            r66.checkConversionCanceled()     // Catch:{ Exception -> 0x04a0 }
            if (r82 == 0) goto L_0x01a2
            r38 = 22000(0x55f0, double:1.08694E-319)
            r71 = r2
            r72 = r3
            r74 = r5
            r5 = r32
            r2 = r38
            goto L_0x01ac
        L_0x01a2:
            r71 = r2
            r72 = r3
            r74 = r5
            r5 = r32
            r2 = 2500(0x9c4, double:1.235E-320)
        L_0x01ac:
            int r2 = r14.dequeueOutputBuffer(r5, r2)     // Catch:{ Exception -> 0x04a0 }
            r3 = -1
            if (r2 != r3) goto L_0x01cc
            r32 = r7
            r21 = r10
            r18 = r15
            r24 = r22
            r10 = r33
            r15 = r66
            r7 = r4
            r22 = r8
            r4 = 0
        L_0x01c3:
            r8 = r74
        L_0x01c5:
            r64 = r2
            r2 = r1
            r1 = r64
            goto L_0x03e1
        L_0x01cc:
            r3 = -3
            if (r2 != r3) goto L_0x01f9
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x01ef }
            r18 = r15
            r15 = 21
            if (r3 >= r15) goto L_0x01de
            java.nio.ByteBuffer[] r1 = r14.getOutputBuffers()     // Catch:{ Exception -> 0x01dc }
            goto L_0x01de
        L_0x01dc:
            r0 = move-exception
            goto L_0x01f2
        L_0x01de:
            r15 = r66
            r32 = r7
            r21 = r10
            r24 = r22
            r10 = r33
            r3 = -1
            r7 = r4
            r22 = r8
            r4 = r72
            goto L_0x01c3
        L_0x01ef:
            r0 = move-exception
            r18 = r15
        L_0x01f2:
            r15 = r66
            r1 = r0
            r21 = r10
            goto L_0x04c9
        L_0x01f9:
            r18 = r15
            r3 = -2
            if (r2 != r3) goto L_0x0282
            android.media.MediaFormat r3 = r14.getOutputFormat()     // Catch:{ Exception -> 0x027a }
            boolean r15 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x027a }
            if (r15 == 0) goto L_0x0225
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x021f }
            r15.<init>()     // Catch:{ Exception -> 0x021f }
            r21 = r10
            java.lang.String r10 = "photo encoder new format "
            r15.append(r10)     // Catch:{ Exception -> 0x021d }
            r15.append(r3)     // Catch:{ Exception -> 0x021d }
            java.lang.String r10 = r15.toString()     // Catch:{ Exception -> 0x021d }
            org.telegram.messenger.FileLog.d(r10)     // Catch:{ Exception -> 0x021d }
            goto L_0x0227
        L_0x021d:
            r0 = move-exception
            goto L_0x0222
        L_0x021f:
            r0 = move-exception
            r21 = r10
        L_0x0222:
            r15 = r66
            goto L_0x027f
        L_0x0225:
            r21 = r10
        L_0x0227:
            r10 = -5
            if (r9 != r10) goto L_0x0268
            if (r3 == 0) goto L_0x0268
            r15 = r66
            org.telegram.messenger.video.MP4Builder r9 = r15.mediaMuxer     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r10 = 0
            int r9 = r9.addTrack(r3, r10)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r10 = r22
            boolean r22 = r3.containsKey(r10)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            if (r22 == 0) goto L_0x025e
            r22 = r9
            int r9 = r3.getInteger(r10)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r24 = r10
            r10 = 1
            if (r9 != r10) goto L_0x0262
            r9 = r33
            java.nio.ByteBuffer r7 = r3.getByteBuffer(r9)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            java.lang.String r10 = "csd-1"
            java.nio.ByteBuffer r3 = r3.getByteBuffer(r10)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r7 = r7.limit()     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r3 = r3.limit()     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r7 = r7 + r3
            goto L_0x0264
        L_0x025e:
            r22 = r9
            r24 = r10
        L_0x0262:
            r9 = r33
        L_0x0264:
            r10 = r9
            r9 = r22
            goto L_0x026e
        L_0x0268:
            r15 = r66
            r24 = r22
            r10 = r33
        L_0x026e:
            r32 = r7
            r22 = r8
            r3 = -1
            r8 = r74
            r7 = r4
            r4 = r72
            goto L_0x01c5
        L_0x027a:
            r0 = move-exception
            r15 = r66
            r21 = r10
        L_0x027f:
            r1 = r0
            goto L_0x04c9
        L_0x0282:
            r15 = r66
            r21 = r10
            r24 = r22
            r10 = r33
            if (r2 < 0) goto L_0x0474
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x046f, all -> 0x12f9 }
            r22 = r8
            r8 = 21
            if (r3 >= r8) goto L_0x0299
            r3 = r1[r2]     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            goto L_0x029d
        L_0x0297:
            r0 = move-exception
            goto L_0x027f
        L_0x0299:
            java.nio.ByteBuffer r3 = r14.getOutputBuffer(r2)     // Catch:{ Exception -> 0x046f, all -> 0x12f9 }
        L_0x029d:
            if (r3 == 0) goto L_0x0450
            int r8 = r5.size     // Catch:{ Exception -> 0x046f, all -> 0x12f9 }
            r74 = r1
            r1 = 1
            if (r8 <= r1) goto L_0x03c5
            int r8 = r5.flags     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r25 = 2
            r8 = r8 & 2
            if (r8 != 0) goto L_0x034f
            if (r7 == 0) goto L_0x02bf
            int r8 = r5.flags     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r8 = r8 & r1
            if (r8 == 0) goto L_0x02bf
            int r1 = r5.offset     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r1 = r1 + r7
            r5.offset = r1     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r1 = r5.size     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r1 = r1 - r7
            r5.size = r1     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
        L_0x02bf:
            if (r4 == 0) goto L_0x0313
            int r1 = r5.flags     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r8 = 1
            r1 = r1 & r8
            if (r1 == 0) goto L_0x0313
            int r1 = r5.size     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r8 = 100
            if (r1 <= r8) goto L_0x0312
            int r1 = r5.offset     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r3.position(r1)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            byte[] r1 = new byte[r8]     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r3.get(r1)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r4 = 0
            r25 = 0
        L_0x02da:
            r8 = 96
            if (r4 >= r8) goto L_0x0312
            byte r8 = r1[r4]     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            if (r8 != 0) goto L_0x0309
            int r8 = r4 + 1
            byte r8 = r1[r8]     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            if (r8 != 0) goto L_0x0309
            int r8 = r4 + 2
            byte r8 = r1[r8]     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            if (r8 != 0) goto L_0x0309
            int r8 = r4 + 3
            byte r8 = r1[r8]     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r27 = r1
            r1 = 1
            if (r8 != r1) goto L_0x030b
            int r8 = r25 + 1
            if (r8 <= r1) goto L_0x0306
            int r1 = r5.offset     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r1 = r1 + r4
            r5.offset = r1     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r1 = r5.size     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r1 = r1 - r4
            r5.size = r1     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            goto L_0x0312
        L_0x0306:
            r25 = r8
            goto L_0x030b
        L_0x0309:
            r27 = r1
        L_0x030b:
            int r4 = r4 + 1
            r1 = r27
            r8 = 100
            goto L_0x02da
        L_0x0312:
            r4 = 0
        L_0x0313:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r25 = r4
            r8 = 1
            long r3 = r1.writeSampleData(r9, r3, r5, r8)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r30 = 0
            int r1 = (r3 > r30 ? 1 : (r3 == r30 ? 0 : -1))
            if (r1 == 0) goto L_0x0347
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r15.callback     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            if (r1 == 0) goto L_0x0347
            r1 = r7
            long r7 = r5.presentationTimeUs     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r32 = r1
            r27 = r2
            r1 = r75
            long r7 = r7 - r1
            int r33 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1))
            if (r33 <= 0) goto L_0x0338
            long r7 = r5.presentationTimeUs     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            long r16 = r7 - r1
        L_0x0338:
            r7 = r16
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r15.callback     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            float r2 = (float) r7     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            float r2 = r2 / r19
            float r2 = r2 / r20
            r1.didWriteData(r3, r2)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r16 = r7
            goto L_0x034b
        L_0x0347:
            r27 = r2
            r32 = r7
        L_0x034b:
            r4 = r25
            goto L_0x03c9
        L_0x034f:
            r27 = r2
            r32 = r7
            r1 = -5
            if (r9 != r1) goto L_0x03c9
            int r1 = r5.size     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            byte[] r1 = new byte[r1]     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r2 = r5.offset     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r7 = r5.size     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r2 = r2 + r7
            r3.limit(r2)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r2 = r5.offset     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r3.position(r2)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r3.get(r1)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r2 = r5.size     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r3 = 1
            int r2 = r2 - r3
        L_0x036e:
            if (r2 < 0) goto L_0x03ab
            r7 = 3
            if (r2 <= r7) goto L_0x03ab
            byte r7 = r1[r2]     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            if (r7 != r3) goto L_0x03a7
            int r3 = r2 + -1
            byte r3 = r1[r3]     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            if (r3 != 0) goto L_0x03a7
            int r3 = r2 + -2
            byte r3 = r1[r3]     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            if (r3 != 0) goto L_0x03a7
            int r3 = r2 + -3
            byte r7 = r1[r3]     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            if (r7 != 0) goto L_0x03a7
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r3)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r7 = r5.size     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r7 = r7 - r3
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.allocate(r7)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r8 = 0
            java.nio.ByteBuffer r9 = r2.put(r1, r8, r3)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r9.position(r8)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r9 = r5.size     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            int r9 = r9 - r3
            java.nio.ByteBuffer r1 = r7.put(r1, r3, r9)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r1.position(r8)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            goto L_0x03ad
        L_0x03a7:
            int r2 = r2 + -1
            r3 = 1
            goto L_0x036e
        L_0x03ab:
            r2 = 0
            r7 = 0
        L_0x03ad:
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r13, r12, r11)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            if (r2 == 0) goto L_0x03bd
            if (r7 == 0) goto L_0x03bd
            r1.setByteBuffer(r10, r2)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            java.lang.String r2 = "csd-1"
            r1.setByteBuffer(r2, r7)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
        L_0x03bd:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            r3 = 0
            int r9 = r2.addTrack(r1, r3)     // Catch:{ Exception -> 0x0297, all -> 0x12f9 }
            goto L_0x03c9
        L_0x03c5:
            r27 = r2
            r32 = r7
        L_0x03c9:
            int r1 = r5.flags     // Catch:{ Exception -> 0x046f, all -> 0x12f9 }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x03d3
            r1 = r27
            r2 = 1
            goto L_0x03d6
        L_0x03d3:
            r1 = r27
            r2 = 0
        L_0x03d6:
            r3 = 0
            r14.releaseOutputBuffer(r1, r3)     // Catch:{ Exception -> 0x046f, all -> 0x12f9 }
            r8 = r2
            r7 = r4
            r3 = -1
            r4 = r72
            r2 = r74
        L_0x03e1:
            if (r1 == r3) goto L_0x03f9
            r1 = r2
            r3 = r4
            r4 = r7
            r33 = r10
            r15 = r18
            r10 = r21
            r7 = r32
            r2 = r71
            r32 = r5
            r5 = r8
            r8 = r22
            r22 = r24
            goto L_0x0182
        L_0x03f9:
            if (r6 != 0) goto L_0x0432
            r3 = r34
            r1 = 0
            r3.drawImage(r1)     // Catch:{ Exception -> 0x042e, all -> 0x12f9 }
            r72 = r2
            r1 = r22
            float r2 = (float) r1
            r22 = 1106247680(0x41var_, float:30.0)
            float r2 = r2 / r22
            float r2 = r2 * r19
            float r2 = r2 * r19
            float r2 = r2 * r19
            r34 = r3
            long r2 = (long) r2
            r74 = r4
            r4 = r21
            r4.setPresentationTime(r2)     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            r4.swapBuffers()     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            int r1 = r1 + 1
            float r2 = (float) r1     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            r3 = 1106247680(0x41var_, float:30.0)
            float r3 = r3 * r20
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 < 0) goto L_0x043a
            r14.signalEndOfInputStream()     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            r2 = 0
            r6 = 1
            goto L_0x043c
        L_0x042e:
            r0 = move-exception
            r34 = r3
            goto L_0x0470
        L_0x0432:
            r72 = r2
            r74 = r4
            r4 = r21
            r1 = r22
        L_0x043a:
            r2 = r71
        L_0x043c:
            r3 = r74
            r33 = r10
            r15 = r18
            r22 = r24
            r10 = r4
            r4 = r7
            r7 = r32
            r32 = r5
            r5 = r8
            r8 = r1
            r1 = r72
            goto L_0x0182
        L_0x0450:
            r1 = r2
            r4 = r21
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            r3.<init>()     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            java.lang.String r5 = "encoderOutputBuffer "
            r3.append(r5)     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            r3.append(r1)     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            java.lang.String r1 = " was null"
            r3.append(r1)     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            java.lang.String r1 = r3.toString()     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            throw r2     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
        L_0x046f:
            r0 = move-exception
        L_0x0470:
            r4 = r21
            goto L_0x027f
        L_0x0474:
            r1 = r2
            r4 = r21
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            r3.<init>()     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            java.lang.String r5 = "unexpected result from encoder.dequeueOutputBuffer: "
            r3.append(r5)     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            r3.append(r1)     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            java.lang.String r1 = r3.toString()     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
            throw r2     // Catch:{ Exception -> 0x048e, all -> 0x12f9 }
        L_0x048e:
            r0 = move-exception
            goto L_0x04a6
        L_0x0490:
            r4 = r10
            r18 = r15
            r15 = r66
            r7 = r73
            r9 = r18
            r2 = r34
            r1 = 0
            r35 = 0
            goto L_0x050c
        L_0x04a0:
            r0 = move-exception
            r4 = r10
            r18 = r15
            r15 = r66
        L_0x04a6:
            r1 = r0
            r21 = r4
            goto L_0x04c9
        L_0x04aa:
            r0 = move-exception
            r4 = r10
            goto L_0x04af
        L_0x04ad:
            r0 = move-exception
            r4 = r9
        L_0x04af:
            r18 = r15
            r15 = r66
            r1 = r0
            r21 = r4
            goto L_0x04c7
        L_0x04b7:
            r0 = move-exception
            r18 = r15
            r15 = r66
            r1 = r0
            goto L_0x04c5
        L_0x04be:
            r0 = move-exception
            r18 = r15
            r15 = r66
            r1 = r0
            r14 = 0
        L_0x04c5:
            r21 = 0
        L_0x04c7:
            r34 = 0
        L_0x04c9:
            boolean r2 = r1 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x0546, all -> 0x12f9 }
            if (r2 == 0) goto L_0x04d1
            if (r82 != 0) goto L_0x04d1
            r2 = 1
            goto L_0x04d2
        L_0x04d1:
            r2 = 0
        L_0x04d2:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x053c, all -> 0x12f9 }
            r3.<init>()     // Catch:{ Exception -> 0x053c, all -> 0x12f9 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ Exception -> 0x053c, all -> 0x12f9 }
            r9 = r18
            r3.append(r9)     // Catch:{ Exception -> 0x053a, all -> 0x12f9 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ Exception -> 0x053a, all -> 0x12f9 }
            r7 = r73
            r3.append(r7)     // Catch:{ Exception -> 0x0535, all -> 0x12f9 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ Exception -> 0x0535, all -> 0x12f9 }
            r3.append(r11)     // Catch:{ Exception -> 0x0535, all -> 0x12f9 }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ Exception -> 0x0535, all -> 0x12f9 }
            r3.append(r12)     // Catch:{ Exception -> 0x0535, all -> 0x12f9 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0535, all -> 0x12f9 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ Exception -> 0x0535, all -> 0x12f9 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x0535, all -> 0x12f9 }
            r1 = r2
            r4 = r21
            r2 = r34
            r35 = 1
        L_0x050c:
            if (r2 == 0) goto L_0x051a
            r2.release()     // Catch:{ Exception -> 0x0512, all -> 0x12f9 }
            goto L_0x051a
        L_0x0512:
            r0 = move-exception
            r10 = r1
            r8 = r7
            r2 = r11
            r11 = r12
            r14 = 1
            goto L_0x1311
        L_0x051a:
            if (r4 == 0) goto L_0x051f
            r4.release()     // Catch:{ Exception -> 0x0512, all -> 0x12f9 }
        L_0x051f:
            if (r14 == 0) goto L_0x0527
            r14.stop()     // Catch:{ Exception -> 0x0512, all -> 0x12f9 }
            r14.release()     // Catch:{ Exception -> 0x0512, all -> 0x12f9 }
        L_0x0527:
            r66.checkConversionCanceled()     // Catch:{ Exception -> 0x0512, all -> 0x12f9 }
            r8 = r7
            r27 = r9
            r14 = r11
            r11 = r12
            r2 = r35
            r35 = r1
            goto L_0x12de
        L_0x0535:
            r0 = move-exception
            r1 = r0
            r10 = r2
            r8 = r7
            goto L_0x0543
        L_0x053a:
            r0 = move-exception
            goto L_0x053f
        L_0x053c:
            r0 = move-exception
            r9 = r18
        L_0x053f:
            r8 = r73
            r1 = r0
            r10 = r2
        L_0x0543:
            r2 = r11
            r11 = r12
            goto L_0x054f
        L_0x0546:
            r0 = move-exception
            r9 = r18
            r8 = r73
            r1 = r0
        L_0x054c:
            r2 = r11
            r11 = r12
            r10 = 0
        L_0x054f:
            r14 = 1
            goto L_0x1312
        L_0x0552:
            r15 = r66
            r7 = r10
            r5 = r18
            r24 = r22
            r10 = r1
            r1 = 16
            android.media.MediaExtractor r2 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x12fe, all -> 0x12f9 }
            r2.<init>()     // Catch:{ Exception -> 0x12fe, all -> 0x12f9 }
            r15.extractor = r2     // Catch:{ Exception -> 0x12fe, all -> 0x12f9 }
            r14 = r67
            r2.setDataSource(r14)     // Catch:{ Exception -> 0x12fe, all -> 0x12f9 }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x12fe, all -> 0x12f9 }
            r8 = 0
            int r6 = org.telegram.messenger.MediaController.findTrack(r2, r8)     // Catch:{ Exception -> 0x12fe, all -> 0x12f9 }
            r2 = -1
            if (r9 == r2) goto L_0x057f
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x057b, all -> 0x12f9 }
            r4 = 1
            int r2 = org.telegram.messenger.MediaController.findTrack(r2, r4)     // Catch:{ Exception -> 0x057b, all -> 0x12f9 }
            r3 = r2
            goto L_0x0581
        L_0x057b:
            r0 = move-exception
            r1 = r0
            r8 = r7
            goto L_0x054c
        L_0x057f:
            r4 = 1
            r3 = -1
        L_0x0581:
            java.lang.String r2 = "mime"
            if (r6 < 0) goto L_0x0597
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x057b, all -> 0x12f9 }
            android.media.MediaFormat r4 = r4.getTrackFormat(r6)     // Catch:{ Exception -> 0x057b, all -> 0x12f9 }
            java.lang.String r4 = r4.getString(r2)     // Catch:{ Exception -> 0x057b, all -> 0x12f9 }
            boolean r4 = r4.equals(r13)     // Catch:{ Exception -> 0x057b, all -> 0x12f9 }
            if (r4 != 0) goto L_0x0597
            r4 = 1
            goto L_0x0598
        L_0x0597:
            r4 = 0
        L_0x0598:
            if (r81 != 0) goto L_0x05da
            if (r4 == 0) goto L_0x059d
            goto L_0x05da
        L_0x059d:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x05ca }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ Exception -> 0x05ca }
            r1 = -1
            if (r9 == r1) goto L_0x05a6
            r13 = 1
            goto L_0x05a7
        L_0x05a6:
            r13 = 0
        L_0x05a7:
            r1 = r66
            r10 = 1
            r4 = r5
            r5 = r75
            r16 = 0
            r7 = r77
            r14 = 1
            r9 = r79
            r14 = r11
            r11 = r68
            r15 = r12
            r12 = r13
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ Exception -> 0x05c8 }
            r8 = r73
            r27 = r74
            r11 = r15
            r2 = 0
            r35 = 0
            r15 = r66
            goto L_0x12de
        L_0x05c8:
            r0 = move-exception
            goto L_0x05cd
        L_0x05ca:
            r0 = move-exception
            r14 = r11
            r15 = r12
        L_0x05cd:
            r10 = 0
            r8 = r73
            r9 = r74
            r1 = r0
            r2 = r14
            r11 = r15
            r14 = 1
            r15 = r66
            goto L_0x1312
        L_0x05da:
            r14 = r11
            r15 = r12
            if (r6 < 0) goto L_0x12a3
            java.lang.String r4 = android.os.Build.MANUFACTURER     // Catch:{ Exception -> 0x1230 }
            java.lang.String r4 = r4.toLowerCase()     // Catch:{ Exception -> 0x1230 }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1230 }
            r9 = 18
            if (r7 >= r9) goto L_0x0691
            android.media.MediaCodecInfo r7 = org.telegram.messenger.MediaController.selectCodec(r13)     // Catch:{ Exception -> 0x067d }
            int r17 = org.telegram.messenger.MediaController.selectColorFormat(r7, r13)     // Catch:{ Exception -> 0x067d }
            if (r17 == 0) goto L_0x0675
            java.lang.String r11 = r7.getName()     // Catch:{ Exception -> 0x067d }
            java.lang.String r12 = "OMX.qcom."
            boolean r12 = r11.contains(r12)     // Catch:{ Exception -> 0x067d }
            if (r12 == 0) goto L_0x061a
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x067d }
            if (r11 != r1) goto L_0x0617
            java.lang.String r11 = "lge"
            boolean r11 = r4.equals(r11)     // Catch:{ Exception -> 0x067d }
            if (r11 != 0) goto L_0x0614
            java.lang.String r11 = "nokia"
            boolean r11 = r4.equals(r11)     // Catch:{ Exception -> 0x067d }
            if (r11 == 0) goto L_0x0617
        L_0x0614:
            r11 = 1
        L_0x0615:
            r12 = 1
            goto L_0x0644
        L_0x0617:
            r11 = 1
        L_0x0618:
            r12 = 0
            goto L_0x0644
        L_0x061a:
            java.lang.String r12 = "OMX.Intel."
            boolean r12 = r11.contains(r12)     // Catch:{ Exception -> 0x067d }
            if (r12 == 0) goto L_0x0624
            r11 = 2
            goto L_0x0618
        L_0x0624:
            java.lang.String r12 = "OMX.MTK.VIDEO.ENCODER.AVC"
            boolean r12 = r11.equals(r12)     // Catch:{ Exception -> 0x067d }
            if (r12 == 0) goto L_0x062e
            r11 = 3
            goto L_0x0618
        L_0x062e:
            java.lang.String r12 = "OMX.SEC.AVC.Encoder"
            boolean r12 = r11.equals(r12)     // Catch:{ Exception -> 0x067d }
            if (r12 == 0) goto L_0x0638
            r11 = 4
            goto L_0x0615
        L_0x0638:
            java.lang.String r12 = "OMX.TI.DUCATI1.VIDEO.H264E"
            boolean r11 = r11.equals(r12)     // Catch:{ Exception -> 0x067d }
            if (r11 == 0) goto L_0x0642
            r11 = 5
            goto L_0x0618
        L_0x0642:
            r11 = 0
            goto L_0x0618
        L_0x0644:
            boolean r18 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x067d }
            if (r18 == 0) goto L_0x0672
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x067d }
            r9.<init>()     // Catch:{ Exception -> 0x067d }
            java.lang.String r8 = "codec = "
            r9.append(r8)     // Catch:{ Exception -> 0x067d }
            java.lang.String r7 = r7.getName()     // Catch:{ Exception -> 0x067d }
            r9.append(r7)     // Catch:{ Exception -> 0x067d }
            java.lang.String r7 = " manufacturer = "
            r9.append(r7)     // Catch:{ Exception -> 0x067d }
            r9.append(r4)     // Catch:{ Exception -> 0x067d }
            java.lang.String r7 = "device = "
            r9.append(r7)     // Catch:{ Exception -> 0x067d }
            java.lang.String r7 = android.os.Build.MODEL     // Catch:{ Exception -> 0x067d }
            r9.append(r7)     // Catch:{ Exception -> 0x067d }
            java.lang.String r7 = r9.toString()     // Catch:{ Exception -> 0x067d }
            org.telegram.messenger.FileLog.d(r7)     // Catch:{ Exception -> 0x067d }
        L_0x0672:
            r9 = r17
            goto L_0x0699
        L_0x0675:
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x067d }
            java.lang.String r2 = "no supported color format"
            r1.<init>(r2)     // Catch:{ Exception -> 0x067d }
            throw r1     // Catch:{ Exception -> 0x067d }
        L_0x067d:
            r0 = move-exception
            r1 = r74
            r4 = r0
            r59 = r6
            r2 = r14
            r11 = r15
        L_0x0685:
            r3 = 0
            r6 = 0
        L_0x0687:
            r10 = 0
            r14 = 1
            r52 = 0
        L_0x068b:
            r56 = 0
        L_0x068d:
            r57 = 0
            goto L_0x1245
        L_0x0691:
            r17 = 2130708361(0x7var_, float:1.701803E38)
            r9 = 2130708361(0x7var_, float:1.701803E38)
            r11 = 0
            r12 = 0
        L_0x0699:
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1230 }
            if (r7 == 0) goto L_0x06b1
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x067d }
            r7.<init>()     // Catch:{ Exception -> 0x067d }
            java.lang.String r8 = "colorFormat = "
            r7.append(r8)     // Catch:{ Exception -> 0x067d }
            r7.append(r9)     // Catch:{ Exception -> 0x067d }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x067d }
            org.telegram.messenger.FileLog.d(r7)     // Catch:{ Exception -> 0x067d }
        L_0x06b1:
            int r7 = r15 * r14
            int r8 = r7 * 3
            r17 = 2
            int r8 = r8 / 2
            if (r11 != 0) goto L_0x06cd
            int r4 = r14 % 16
            if (r4 == 0) goto L_0x0701
            int r4 = r14 % 16
            int r1 = r1 - r4
            int r1 = r1 + r14
            int r1 = r1 - r14
            int r1 = r1 * r15
            int r4 = r1 * 5
            int r4 = r4 / 4
        L_0x06ca:
            int r8 = r8 + r4
        L_0x06cb:
            r11 = r1
            goto L_0x0702
        L_0x06cd:
            r1 = 1
            if (r11 != r1) goto L_0x06e3
            java.lang.String r1 = r4.toLowerCase()     // Catch:{ Exception -> 0x067d }
            java.lang.String r4 = "lge"
            boolean r1 = r1.equals(r4)     // Catch:{ Exception -> 0x067d }
            if (r1 != 0) goto L_0x0701
            int r1 = r7 + 2047
            r1 = r1 & -2048(0xffffffffffffvar_, float:NaN)
            int r1 = r1 - r7
            int r8 = r8 + r1
            goto L_0x06cb
        L_0x06e3:
            r1 = 5
            if (r11 != r1) goto L_0x06e7
            goto L_0x0701
        L_0x06e7:
            r1 = 3
            if (r11 != r1) goto L_0x0701
            java.lang.String r1 = "baidu"
            boolean r1 = r4.equals(r1)     // Catch:{ Exception -> 0x067d }
            if (r1 == 0) goto L_0x0701
            int r1 = r14 % 16
            r4 = 16
            int r1 = 16 - r1
            int r1 = r1 + r14
            int r1 = r1 - r14
            int r1 = r1 * r15
            int r4 = r1 * 5
            int r4 = r4 / 4
            goto L_0x06ca
        L_0x0701:
            r11 = 0
        L_0x0702:
            r7 = r15
            r15 = r8
            r8 = r66
            android.media.MediaExtractor r1 = r8.extractor     // Catch:{ Exception -> 0x122a }
            r1.selectTrack(r6)     // Catch:{ Exception -> 0x122a }
            android.media.MediaExtractor r1 = r8.extractor     // Catch:{ Exception -> 0x122a }
            android.media.MediaFormat r4 = r1.getTrackFormat(r6)     // Catch:{ Exception -> 0x122a }
            r17 = r3
            r25 = r4
            r30 = 0
            r3 = r75
            int r1 = (r3 > r30 ? 1 : (r3 == r30 ? 0 : -1))
            if (r1 <= 0) goto L_0x0738
            android.media.MediaExtractor r1 = r8.extractor     // Catch:{ Exception -> 0x072e, all -> 0x0729 }
            r32 = r5
            r5 = 0
            r1.seekTo(r3, r5)     // Catch:{ Exception -> 0x072e, all -> 0x0729 }
            r3 = 0
            r5 = 0
            goto L_0x0742
        L_0x0729:
            r0 = move-exception
            r2 = r0
            r1 = r8
            goto L_0x1383
        L_0x072e:
            r0 = move-exception
            r1 = r74
            r4 = r0
            r59 = r6
            r11 = r7
            r2 = r14
            goto L_0x0685
        L_0x0738:
            r32 = r5
            android.media.MediaExtractor r1 = r8.extractor     // Catch:{ Exception -> 0x122a }
            r3 = 0
            r5 = 0
            r1.seekTo(r3, r5)     // Catch:{ Exception -> 0x122a }
        L_0x0742:
            if (r74 > 0) goto L_0x0748
            r1 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x074a
        L_0x0748:
            r1 = r74
        L_0x074a:
            android.media.MediaFormat r3 = android.media.MediaFormat.createVideoFormat(r13, r7, r14)     // Catch:{ Exception -> 0x1220 }
            java.lang.String r4 = "color-format"
            r3.setInteger(r4, r9)     // Catch:{ Exception -> 0x1220 }
            java.lang.String r4 = "bitrate"
            r3.setInteger(r4, r1)     // Catch:{ Exception -> 0x1220 }
            java.lang.String r4 = "frame-rate"
            r5 = r73
            r3.setInteger(r4, r5)     // Catch:{ Exception -> 0x1220 }
            java.lang.String r4 = "i-frame-interval"
            r33 = r6
            r6 = 2
            r3.setInteger(r4, r6)     // Catch:{ Exception -> 0x121a }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x121a }
            r6 = 23
            if (r4 >= r6) goto L_0x078b
            int r4 = java.lang.Math.min(r14, r7)     // Catch:{ Exception -> 0x0783, all -> 0x0729 }
            r6 = 480(0x1e0, float:6.73E-43)
            if (r4 > r6) goto L_0x078b
            r4 = 921600(0xe1000, float:1.291437E-39)
            if (r1 <= r4) goto L_0x077d
            r1 = 921600(0xe1000, float:1.291437E-39)
        L_0x077d:
            java.lang.String r4 = "bitrate"
            r3.setInteger(r4, r1)     // Catch:{ Exception -> 0x0783, all -> 0x0729 }
            goto L_0x078b
        L_0x0783:
            r0 = move-exception
            r4 = r0
            r11 = r7
            r2 = r14
        L_0x0787:
            r59 = r33
            goto L_0x0685
        L_0x078b:
            r27 = r1
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x120d }
            r4 = 18
            if (r1 >= r4) goto L_0x07a7
            java.lang.String r1 = "stride"
            int r4 = r7 + 32
            r3.setInteger(r1, r4)     // Catch:{ Exception -> 0x07a0, all -> 0x0729 }
            java.lang.String r1 = "slice-height"
            r3.setInteger(r1, r14)     // Catch:{ Exception -> 0x07a0, all -> 0x0729 }
            goto L_0x07a7
        L_0x07a0:
            r0 = move-exception
            r4 = r0
            r11 = r7
            r2 = r14
            r1 = r27
            goto L_0x0787
        L_0x07a7:
            android.media.MediaCodec r6 = android.media.MediaCodec.createEncoderByType(r13)     // Catch:{ Exception -> 0x120d }
            r1 = 0
            r4 = 1
            r6.configure(r3, r1, r1, r4)     // Catch:{ Exception -> 0x11fb }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x11f0 }
            r3 = 18
            if (r1 < r3) goto L_0x07de
            org.telegram.messenger.video.InputSurface r1 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x07d3, all -> 0x0729 }
            android.view.Surface r3 = r6.createInputSurface()     // Catch:{ Exception -> 0x07d3, all -> 0x0729 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x07d3, all -> 0x0729 }
            r1.makeCurrent()     // Catch:{ Exception -> 0x07c4, all -> 0x0729 }
            r4 = r1
            goto L_0x07df
        L_0x07c4:
            r0 = move-exception
            r4 = r0
            r52 = r1
            r11 = r7
            r2 = r14
            r1 = r27
            r59 = r33
            r3 = 0
        L_0x07cf:
            r10 = 0
            r14 = 1
            goto L_0x068b
        L_0x07d3:
            r0 = move-exception
            r4 = r0
            r11 = r7
            r2 = r14
            r1 = r27
            r59 = r33
            r3 = 0
            goto L_0x0687
        L_0x07de:
            r4 = 0
        L_0x07df:
            r6.start()     // Catch:{ Exception -> 0x11da }
            r3 = r25
            java.lang.String r1 = r3.getString(r2)     // Catch:{ Exception -> 0x11da }
            android.media.MediaCodec r1 = android.media.MediaCodec.createDecoderByType(r1)     // Catch:{ Exception -> 0x11da }
            r74 = r1
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x11c2 }
            r25 = r3
            r3 = 18
            if (r1 < r3) goto L_0x085a
            org.telegram.messenger.video.OutputSurface r18 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x0847 }
            r34 = 0
            float r1 = (float) r5
            r35 = 0
            r48 = r74
            r37 = r1
            r1 = r18
            r49 = r2
            r2 = r83
            r50 = r17
            r17 = r25
            r25 = 18
            r30 = 0
            r3 = r34
            r52 = r4
            r51 = r17
            r4 = r84
            r53 = r32
            r5 = r85
            r74 = r6
            r54 = r33
            r6 = r71
            r7 = r72
            r8 = r37
            r17 = r9
            r25 = r15
            r15 = 18
            r9 = r35
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0839 }
            r7 = r69
            r6 = r71
            r5 = r18
            r8 = r30
            goto L_0x087e
        L_0x0839:
            r0 = move-exception
            r11 = r71
            r6 = r74
            r4 = r0
            r2 = r14
            r1 = r27
            r3 = r48
            r59 = r54
            goto L_0x07cf
        L_0x0847:
            r0 = move-exception
            r48 = r74
            r52 = r4
            r74 = r6
            r11 = r71
            r4 = r0
            r2 = r14
            r1 = r27
            r59 = r33
            r3 = r48
            goto L_0x07cf
        L_0x085a:
            r48 = r74
            r49 = r2
            r52 = r4
            r74 = r6
            r50 = r17
            r51 = r25
            r53 = r32
            r54 = r33
            r30 = 0
            r17 = r9
            r25 = r15
            r15 = 18
            org.telegram.messenger.video.OutputSurface r1 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x11af }
            r7 = r69
            r6 = r71
            r8 = r30
            r1.<init>(r6, r14, r7)     // Catch:{ Exception -> 0x11aa }
            r5 = r1
        L_0x087e:
            android.view.Surface r1 = r5.getSurface()     // Catch:{ Exception -> 0x118e }
            r3 = r48
            r2 = r51
            r4 = 0
            r8 = 0
            r3.configure(r2, r1, r4, r8)     // Catch:{ Exception -> 0x1177 }
            r3.start()     // Catch:{ Exception -> 0x1177 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1177 }
            r2 = 21
            if (r1 >= r2) goto L_0x08bc
            java.nio.ByteBuffer[] r9 = r3.getInputBuffers()     // Catch:{ Exception -> 0x08aa }
            java.nio.ByteBuffer[] r1 = r74.getOutputBuffers()     // Catch:{ Exception -> 0x08aa }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x08aa }
            if (r2 >= r15) goto L_0x08a7
            java.nio.ByteBuffer[] r2 = r74.getInputBuffers()     // Catch:{ Exception -> 0x08aa }
            r18 = r2
            goto L_0x08c0
        L_0x08a7:
            r18 = r4
            goto L_0x08c0
        L_0x08aa:
            r0 = move-exception
            r57 = r4
            r56 = r5
            r11 = r6
            r2 = r14
            r1 = r27
            r59 = r54
            r10 = 0
            r14 = 1
            r6 = r74
        L_0x08b9:
            r4 = r0
            goto L_0x1245
        L_0x08bc:
            r1 = r4
            r9 = r1
            r18 = r9
        L_0x08c0:
            r2 = r50
            if (r2 < 0) goto L_0x09ec
            r15 = r66
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x09d6, all -> 0x12f9 }
            android.media.MediaFormat r4 = r4.getTrackFormat(r2)     // Catch:{ Exception -> 0x09d6, all -> 0x12f9 }
            r33 = r1
            r8 = r49
            java.lang.String r1 = r4.getString(r8)     // Catch:{ Exception -> 0x09d6, all -> 0x12f9 }
            r50 = r2
            java.lang.String r2 = "audio/mp4a-latm"
            boolean r1 = r1.equals(r2)     // Catch:{ Exception -> 0x09d6, all -> 0x12f9 }
            if (r1 != 0) goto L_0x08fd
            java.lang.String r1 = r4.getString(r8)     // Catch:{ Exception -> 0x08ed, all -> 0x12f9 }
            java.lang.String r2 = "audio/mpeg"
            boolean r1 = r1.equals(r2)     // Catch:{ Exception -> 0x08ed, all -> 0x12f9 }
            if (r1 == 0) goto L_0x08eb
            goto L_0x08fd
        L_0x08eb:
            r2 = 0
            goto L_0x08fe
        L_0x08ed:
            r0 = move-exception
        L_0x08ee:
            r4 = r0
            r56 = r5
            r11 = r6
            r2 = r14
            r1 = r27
            r59 = r54
            r10 = 0
            r14 = 1
            r57 = 0
            goto L_0x0a54
        L_0x08fd:
            r2 = 1
        L_0x08fe:
            java.lang.String r1 = r4.getString(r8)     // Catch:{ Exception -> 0x09d6, all -> 0x12f9 }
            java.lang.String r8 = "audio/unknown"
            boolean r1 = r1.equals(r8)     // Catch:{ Exception -> 0x09d6, all -> 0x12f9 }
            if (r1 == 0) goto L_0x090c
            r1 = -1
            goto L_0x090e
        L_0x090c:
            r1 = r50
        L_0x090e:
            if (r1 < 0) goto L_0x09c3
            if (r2 == 0) goto L_0x095e
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x095a, all -> 0x12f9 }
            r34 = r2
            r2 = 1
            int r8 = r8.addTrack(r4, r2)     // Catch:{ Exception -> 0x095a, all -> 0x12f9 }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x095a, all -> 0x12f9 }
            r2.selectTrack(r1)     // Catch:{ Exception -> 0x095a, all -> 0x12f9 }
            java.lang.String r2 = "max-input-size"
            int r2 = r4.getInteger(r2)     // Catch:{ Exception -> 0x095a, all -> 0x12f9 }
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocateDirect(r2)     // Catch:{ Exception -> 0x095a, all -> 0x12f9 }
            r36 = r8
            r30 = 0
            r7 = r75
            int r4 = (r7 > r30 ? 1 : (r7 == r30 ? 0 : -1))
            if (r4 <= 0) goto L_0x0941
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x08ed, all -> 0x12f9 }
            r37 = r2
            r2 = 0
            r4.seekTo(r7, r2)     // Catch:{ Exception -> 0x08ed, all -> 0x12f9 }
            r39 = r11
            r38 = r12
            goto L_0x094f
        L_0x0941:
            r37 = r2
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x08ed, all -> 0x12f9 }
            r39 = r11
            r38 = r12
            r4 = 0
            r11 = 0
            r2.seekTo(r11, r4)     // Catch:{ Exception -> 0x08ed, all -> 0x12f9 }
        L_0x094f:
            r11 = r1
            r12 = r5
            r4 = r36
            r5 = r37
            r6 = 0
            r1 = r77
            goto L_0x0a02
        L_0x095a:
            r0 = move-exception
            r7 = r75
            goto L_0x08ee
        L_0x095e:
            r7 = r75
            r34 = r2
            r39 = r11
            r38 = r12
            android.media.MediaExtractor r2 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x09c1, all -> 0x12f9 }
            r2.<init>()     // Catch:{ Exception -> 0x09c1, all -> 0x12f9 }
            r11 = r67
            r12 = r73
            r2.setDataSource(r11)     // Catch:{ Exception -> 0x09c1, all -> 0x12f9 }
            r2.selectTrack(r1)     // Catch:{ Exception -> 0x09c1, all -> 0x12f9 }
            r11 = 0
            int r30 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r30 <= 0) goto L_0x0981
            r11 = 0
            r2.seekTo(r7, r11)     // Catch:{ Exception -> 0x08ed, all -> 0x12f9 }
            r12 = r5
            goto L_0x098a
        L_0x0981:
            r64 = r11
            r12 = r5
            r5 = r64
            r11 = 0
            r2.seekTo(r5, r11)     // Catch:{ Exception -> 0x09bd, all -> 0x12f9 }
        L_0x098a:
            org.telegram.messenger.video.AudioRecoder r5 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x09bd, all -> 0x12f9 }
            r5.<init>(r4, r2, r1)     // Catch:{ Exception -> 0x09bd, all -> 0x12f9 }
            r5.startTime = r7     // Catch:{ Exception -> 0x09aa, all -> 0x12f9 }
            r4 = r1
            r1 = r77
            r5.endTime = r1     // Catch:{ Exception -> 0x09a8, all -> 0x12f9 }
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x09a8, all -> 0x12f9 }
            android.media.MediaFormat r11 = r5.format     // Catch:{ Exception -> 0x09a8, all -> 0x12f9 }
            r36 = r4
            r4 = 1
            int r6 = r6.addTrack(r11, r4)     // Catch:{ Exception -> 0x09a8, all -> 0x12f9 }
            r4 = r6
            r11 = r36
            r6 = r5
            r5 = 0
            goto L_0x0a02
        L_0x09a8:
            r0 = move-exception
            goto L_0x09ad
        L_0x09aa:
            r0 = move-exception
            r1 = r77
        L_0x09ad:
            r11 = r71
            r6 = r74
            r4 = r0
            r57 = r5
            r56 = r12
            r2 = r14
            r1 = r27
            r59 = r54
            goto L_0x0b8c
        L_0x09bd:
            r0 = move-exception
            r1 = r77
            goto L_0x09dc
        L_0x09c1:
            r0 = move-exception
            goto L_0x09d9
        L_0x09c3:
            r7 = r75
            r36 = r1
            r34 = r2
            r39 = r11
            r38 = r12
            r1 = r77
            r12 = r5
            r11 = r36
            r4 = -5
            r5 = 0
            r6 = 0
            goto L_0x0a02
        L_0x09d6:
            r0 = move-exception
            r7 = r75
        L_0x09d9:
            r1 = r77
            r12 = r5
        L_0x09dc:
            r11 = r71
            r6 = r74
            r4 = r0
            r56 = r12
            r2 = r14
            r1 = r27
            r59 = r54
            r10 = 0
            r14 = 1
            goto L_0x068d
        L_0x09ec:
            r15 = r66
            r7 = r75
            r33 = r1
            r50 = r2
            r39 = r11
            r38 = r12
            r1 = r77
            r12 = r5
            r11 = r50
            r4 = -5
            r5 = 0
            r6 = 0
            r34 = 1
        L_0x0a02:
            if (r11 >= 0) goto L_0x0a07
            r36 = 1
            goto L_0x0a09
        L_0x0a07:
            r36 = 0
        L_0x0a09:
            r66.checkConversionCanceled()     // Catch:{ Exception -> 0x115c }
            r40 = r36
            r21 = 1
            r22 = 0
            r36 = 0
            r37 = 0
            r47 = 0
            r48 = -1
            r50 = 0
            r55 = -5
        L_0x0a1e:
            if (r22 == 0) goto L_0x0a37
            if (r34 != 0) goto L_0x0a25
            if (r40 != 0) goto L_0x0a25
            goto L_0x0a37
        L_0x0a25:
            r11 = r71
            r8 = r73
            r1 = r3
            r57 = r6
            r2 = r14
            r59 = r54
            r3 = 0
            r14 = 1
            r35 = 0
            r6 = r74
            goto L_0x1287
        L_0x0a37:
            r66.checkConversionCanceled()     // Catch:{ Exception -> 0x115c }
            if (r34 != 0) goto L_0x0a58
            if (r6 == 0) goto L_0x0a58
            r56 = r12
            org.telegram.messenger.video.MP4Builder r12 = r15.mediaMuxer     // Catch:{ Exception -> 0x0a47, all -> 0x12f9 }
            boolean r12 = r6.step(r12, r4)     // Catch:{ Exception -> 0x0a47, all -> 0x12f9 }
            goto L_0x0a5c
        L_0x0a47:
            r0 = move-exception
            r11 = r71
            r4 = r0
            r57 = r6
            r2 = r14
            r1 = r27
            r59 = r54
            r10 = 0
            r14 = 1
        L_0x0a54:
            r6 = r74
            goto L_0x1245
        L_0x0a58:
            r56 = r12
            r12 = r40
        L_0x0a5c:
            if (r36 != 0) goto L_0x0b90
            r57 = r6
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0b80, all -> 0x12f9 }
            int r6 = r6.getSampleTrackIndex()     // Catch:{ Exception -> 0x0b80, all -> 0x12f9 }
            r58 = r12
            r12 = r54
            if (r6 != r12) goto L_0x0acc
            r59 = r12
            r54 = r13
            r12 = 2500(0x9c4, double:1.235E-320)
            int r6 = r3.dequeueInputBuffer(r12)     // Catch:{ Exception -> 0x0ac3, all -> 0x12f9 }
            if (r6 < 0) goto L_0x0ab9
            int r12 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ac3, all -> 0x12f9 }
            r13 = 21
            if (r12 >= r13) goto L_0x0a81
            r12 = r9[r6]     // Catch:{ Exception -> 0x0ac3, all -> 0x12f9 }
            goto L_0x0a85
        L_0x0a81:
            java.nio.ByteBuffer r12 = r3.getInputBuffer(r6)     // Catch:{ Exception -> 0x0ac3, all -> 0x12f9 }
        L_0x0a85:
            android.media.MediaExtractor r13 = r15.extractor     // Catch:{ Exception -> 0x0ac3, all -> 0x12f9 }
            r60 = r9
            r9 = 0
            int r43 = r13.readSampleData(r12, r9)     // Catch:{ Exception -> 0x0ac3, all -> 0x12f9 }
            if (r43 >= 0) goto L_0x0aa2
            r42 = 0
            r43 = 0
            r44 = 0
            r46 = 4
            r40 = r3
            r41 = r6
            r40.queueInputBuffer(r41, r42, r43, r44, r46)     // Catch:{ Exception -> 0x0ac3, all -> 0x12f9 }
            r36 = 1
            goto L_0x0abb
        L_0x0aa2:
            r42 = 0
            android.media.MediaExtractor r9 = r15.extractor     // Catch:{ Exception -> 0x0ac3, all -> 0x12f9 }
            long r44 = r9.getSampleTime()     // Catch:{ Exception -> 0x0ac3, all -> 0x12f9 }
            r46 = 0
            r40 = r3
            r41 = r6
            r40.queueInputBuffer(r41, r42, r43, r44, r46)     // Catch:{ Exception -> 0x0ac3, all -> 0x12f9 }
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0ac3, all -> 0x12f9 }
            r6.advance()     // Catch:{ Exception -> 0x0ac3, all -> 0x12f9 }
            goto L_0x0abb
        L_0x0ab9:
            r60 = r9
        L_0x0abb:
            r61 = r5
            r12 = r53
            r53 = r4
            goto L_0x0b63
        L_0x0ac3:
            r0 = move-exception
            r11 = r71
            r6 = r74
            r4 = r0
            r2 = r14
            goto L_0x0b8a
        L_0x0acc:
            r60 = r9
            r59 = r12
            r54 = r13
            if (r34 == 0) goto L_0x0b58
            r9 = -1
            if (r11 == r9) goto L_0x0b58
            if (r6 != r11) goto L_0x0b58
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            r9 = 0
            int r6 = r6.readSampleData(r5, r9)     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            r12 = r53
            r12.size = r6     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            r13 = 21
            if (r6 >= r13) goto L_0x0af2
            r5.position(r9)     // Catch:{ Exception -> 0x0ac3, all -> 0x12f9 }
            int r6 = r12.size     // Catch:{ Exception -> 0x0ac3, all -> 0x12f9 }
            r5.limit(r6)     // Catch:{ Exception -> 0x0ac3, all -> 0x12f9 }
        L_0x0af2:
            int r6 = r12.size     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            if (r6 < 0) goto L_0x0b04
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            long r13 = r6.getSampleTime()     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            r12.presentationTimeUs = r13     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            r6.advance()     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            goto L_0x0b09
        L_0x0b04:
            r6 = 0
            r12.size = r6     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            r36 = 1
        L_0x0b09:
            int r6 = r12.size     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            if (r6 <= 0) goto L_0x0b53
            r13 = 0
            int r6 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r6 < 0) goto L_0x0b19
            long r13 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            int r6 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r6 >= 0) goto L_0x0b53
        L_0x0b19:
            r6 = 0
            r12.offset = r6     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            android.media.MediaExtractor r9 = r15.extractor     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            int r9 = r9.getSampleFlags()     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            r12.flags = r9     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            org.telegram.messenger.video.MP4Builder r9 = r15.mediaMuxer     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            long r13 = r9.writeSampleData(r4, r5, r12, r6)     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            r30 = 0
            int r6 = (r13 > r30 ? 1 : (r13 == r30 ? 0 : -1))
            if (r6 == 0) goto L_0x0b53
            org.telegram.messenger.MediaController$VideoConvertorListener r6 = r15.callback     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            if (r6 == 0) goto L_0x0b53
            r53 = r4
            r9 = r5
            long r4 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            long r4 = r4 - r7
            int r6 = (r4 > r50 ? 1 : (r4 == r50 ? 0 : -1))
            if (r6 <= 0) goto L_0x0b42
            long r4 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            long r50 = r4 - r7
        L_0x0b42:
            r4 = r50
            org.telegram.messenger.MediaController$VideoConvertorListener r6 = r15.callback     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            r61 = r9
            float r9 = (float) r4     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            float r9 = r9 / r19
            float r9 = r9 / r20
            r6.didWriteData(r13, r9)     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            r50 = r4
            goto L_0x0b63
        L_0x0b53:
            r53 = r4
            r61 = r5
            goto L_0x0b63
        L_0x0b58:
            r61 = r5
            r12 = r53
            r53 = r4
            r4 = -1
            if (r6 != r4) goto L_0x0b63
            r4 = 1
            goto L_0x0b64
        L_0x0b63:
            r4 = 0
        L_0x0b64:
            if (r4 == 0) goto L_0x0ba0
            r4 = 2500(0x9c4, double:1.235E-320)
            int r41 = r3.dequeueInputBuffer(r4)     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            if (r41 < 0) goto L_0x0ba0
            r42 = 0
            r43 = 0
            r44 = 0
            r46 = 4
            r40 = r3
            r40.queueInputBuffer(r41, r42, r43, r44, r46)     // Catch:{ Exception -> 0x0b7e, all -> 0x12f9 }
            r36 = 1
            goto L_0x0ba0
        L_0x0b7e:
            r0 = move-exception
            goto L_0x0b83
        L_0x0b80:
            r0 = move-exception
            r59 = r54
        L_0x0b83:
            r11 = r71
            r2 = r72
            r6 = r74
        L_0x0b89:
            r4 = r0
        L_0x0b8a:
            r1 = r27
        L_0x0b8c:
            r10 = 0
            r14 = 1
            goto L_0x1245
        L_0x0b90:
            r61 = r5
            r57 = r6
            r60 = r9
            r58 = r12
            r12 = r53
            r59 = r54
            r53 = r4
            r54 = r13
        L_0x0ba0:
            r4 = r37 ^ 1
            r9 = r4
            r5 = r55
            r4 = 1
        L_0x0ba6:
            if (r9 != 0) goto L_0x0bc3
            if (r4 == 0) goto L_0x0bab
            goto L_0x0bc3
        L_0x0bab:
            r14 = r72
            r55 = r5
            r4 = r53
            r13 = r54
            r6 = r57
            r40 = r58
            r54 = r59
            r9 = r60
            r5 = r61
            r53 = r12
            r12 = r56
            goto L_0x0a1e
        L_0x0bc3:
            r66.checkConversionCanceled()     // Catch:{ Exception -> 0x1145 }
            if (r82 == 0) goto L_0x0bcd
            r13 = 22000(0x55f0, double:1.08694E-319)
            r6 = r74
            goto L_0x0bd1
        L_0x0bcd:
            r6 = r74
            r13 = 2500(0x9c4, double:1.235E-320)
        L_0x0bd1:
            int r13 = r6.dequeueOutputBuffer(r12, r13)     // Catch:{ Exception -> 0x1133 }
            r14 = -1
            if (r13 != r14) goto L_0x0bf9
            r26 = r5
            r55 = r9
            r29 = r21
            r28 = r24
            r62 = r50
            r1 = r54
            r5 = -1
            r14 = 3
            r21 = 0
            r24 = 2
        L_0x0bea:
            r9 = r3
            r50 = r47
            r3 = r72
        L_0x0bef:
            r47 = r33
            r33 = r22
            r22 = r11
            r11 = r71
            goto L_0x0e00
        L_0x0bf9:
            r14 = -3
            if (r13 != r14) goto L_0x0CLASSNAME
            int r14 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            r74 = r4
            r4 = 21
            if (r14 >= r4) goto L_0x0CLASSNAME
            java.nio.ByteBuffer[] r33 = r6.getOutputBuffers()     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
        L_0x0CLASSNAME:
            r26 = r5
            r55 = r9
            r29 = r21
            r28 = r24
            r62 = r50
            r1 = r54
            r5 = -1
            r14 = 3
            r24 = 2
            r21 = r74
            goto L_0x0bea
        L_0x0c1b:
            r0 = move-exception
        L_0x0c1c:
            r11 = r71
            r2 = r72
            goto L_0x0b89
        L_0x0CLASSNAME:
            r74 = r4
            r4 = -2
            if (r13 != r4) goto L_0x0CLASSNAME
            android.media.MediaFormat r4 = r6.getOutputFormat()     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            r14 = -5
            if (r5 != r14) goto L_0x0CLASSNAME
            if (r4 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            r14 = 0
            int r5 = r5.addTrack(r4, r14)     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            r14 = r24
            boolean r24 = r4.containsKey(r14)     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            if (r24 == 0) goto L_0x0CLASSNAME
            r24 = r5
            int r5 = r4.getInteger(r14)     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            r55 = r9
            r9 = 1
            if (r5 != r9) goto L_0x0CLASSNAME
            java.nio.ByteBuffer r5 = r4.getByteBuffer(r10)     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            java.lang.String r9 = "csd-1"
            java.nio.ByteBuffer r4 = r4.getByteBuffer(r9)     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            int r5 = r5.limit()     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            int r4 = r4.limit()     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            int r5 = r5 + r4
            r47 = r5
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r24 = r5
            r55 = r9
        L_0x0CLASSNAME:
            r5 = r24
            goto L_0x0c6b
        L_0x0CLASSNAME:
            r55 = r9
            r14 = r24
        L_0x0c6b:
            r9 = r3
            r26 = r5
            r28 = r14
            r29 = r21
            r62 = r50
            r1 = r54
            r5 = -1
            r14 = 3
            r24 = 2
            r3 = r72
            r21 = r74
            r50 = r47
            goto L_0x0bef
        L_0x0CLASSNAME:
            r55 = r9
            r14 = r24
            if (r13 < 0) goto L_0x1101
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1133 }
            r9 = 21
            if (r4 >= r9) goto L_0x0CLASSNAME
            r4 = r33[r13]     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.nio.ByteBuffer r4 = r6.getOutputBuffer(r13)     // Catch:{ Exception -> 0x1133 }
        L_0x0CLASSNAME:
            if (r4 == 0) goto L_0x10d6
            int r9 = r12.size     // Catch:{ Exception -> 0x10c4 }
            r22 = r11
            r11 = 1
            if (r9 <= r11) goto L_0x0dd8
            int r9 = r12.flags     // Catch:{ Exception -> 0x0dd4, all -> 0x12f9 }
            r24 = 2
            r9 = r9 & 2
            if (r9 != 0) goto L_0x0d4b
            if (r47 == 0) goto L_0x0cb9
            int r9 = r12.flags     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            r9 = r9 & r11
            if (r9 == 0) goto L_0x0cb9
            int r9 = r12.offset     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            int r9 = r9 + r47
            r12.offset = r9     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            int r9 = r12.size     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            int r9 = r9 - r47
            r12.size = r9     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
        L_0x0cb9:
            if (r21 == 0) goto L_0x0d13
            int r9 = r12.flags     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            r11 = 1
            r9 = r9 & r11
            if (r9 == 0) goto L_0x0d13
            int r9 = r12.size     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            r11 = 100
            if (r9 <= r11) goto L_0x0d0e
            int r9 = r12.offset     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            r4.position(r9)     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            byte[] r9 = new byte[r11]     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            r4.get(r9)     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            r11 = 0
            r21 = 0
        L_0x0cd4:
            r28 = r14
            r14 = 96
            if (r11 >= r14) goto L_0x0d10
            byte r14 = r9[r11]     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            if (r14 != 0) goto L_0x0d05
            int r14 = r11 + 1
            byte r14 = r9[r14]     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            if (r14 != 0) goto L_0x0d05
            int r14 = r11 + 2
            byte r14 = r9[r14]     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            if (r14 != 0) goto L_0x0d05
            int r14 = r11 + 3
            byte r14 = r9[r14]     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            r40 = r9
            r9 = 1
            if (r14 != r9) goto L_0x0d07
            int r14 = r21 + 1
            if (r14 <= r9) goto L_0x0d02
            int r9 = r12.offset     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            int r9 = r9 + r11
            r12.offset = r9     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            int r9 = r12.size     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            int r9 = r9 - r11
            r12.size = r9     // Catch:{ Exception -> 0x0c1b, all -> 0x12f9 }
            goto L_0x0d10
        L_0x0d02:
            r21 = r14
            goto L_0x0d07
        L_0x0d05:
            r40 = r9
        L_0x0d07:
            int r11 = r11 + 1
            r14 = r28
            r9 = r40
            goto L_0x0cd4
        L_0x0d0e:
            r28 = r14
        L_0x0d10:
            r21 = 0
            goto L_0x0d15
        L_0x0d13:
            r28 = r14
        L_0x0d15:
            org.telegram.messenger.video.MP4Builder r9 = r15.mediaMuxer     // Catch:{ Exception -> 0x0dd4, all -> 0x12f9 }
            r11 = 1
            long r1 = r9.writeSampleData(r5, r4, r12, r11)     // Catch:{ Exception -> 0x0dd4, all -> 0x12f9 }
            r30 = 0
            int r4 = (r1 > r30 ? 1 : (r1 == r30 ? 0 : -1))
            if (r4 == 0) goto L_0x0d41
            org.telegram.messenger.MediaController$VideoConvertorListener r4 = r15.callback     // Catch:{ Exception -> 0x0dd4, all -> 0x12f9 }
            if (r4 == 0) goto L_0x0d41
            r9 = r3
            long r3 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            long r3 = r3 - r7
            int r11 = (r3 > r50 ? 1 : (r3 == r50 ? 0 : -1))
            if (r11 <= 0) goto L_0x0d32
            long r3 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            long r50 = r3 - r7
        L_0x0d32:
            r3 = r50
            org.telegram.messenger.MediaController$VideoConvertorListener r11 = r15.callback     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            float r14 = (float) r3     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            float r14 = r14 / r19
            float r14 = r14 / r20
            r11.didWriteData(r1, r14)     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            r50 = r3
            goto L_0x0d42
        L_0x0d41:
            r9 = r3
        L_0x0d42:
            r11 = r71
            r3 = r72
            r1 = r54
            r14 = 3
            goto L_0x0de4
        L_0x0d4b:
            r9 = r3
            r28 = r14
            r11 = -5
            if (r5 != r11) goto L_0x0d42
            int r1 = r12.size     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            byte[] r1 = new byte[r1]     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            int r2 = r12.offset     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            int r3 = r12.size     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            int r2 = r2 + r3
            r4.limit(r2)     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            int r2 = r12.offset     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            r4.position(r2)     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            r4.get(r1)     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            int r2 = r12.size     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            r3 = 1
            int r2 = r2 - r3
        L_0x0d69:
            r14 = 3
            if (r2 < 0) goto L_0x0da7
            if (r2 <= r14) goto L_0x0da7
            byte r4 = r1[r2]     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            if (r4 != r3) goto L_0x0da2
            int r3 = r2 + -1
            byte r3 = r1[r3]     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            if (r3 != 0) goto L_0x0da2
            int r3 = r2 + -2
            byte r3 = r1[r3]     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            if (r3 != 0) goto L_0x0da2
            int r3 = r2 + -3
            byte r4 = r1[r3]     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            if (r4 != 0) goto L_0x0da2
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r3)     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            int r4 = r12.size     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            int r4 = r4 - r3
            java.nio.ByteBuffer r4 = java.nio.ByteBuffer.allocate(r4)     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            r5 = 0
            java.nio.ByteBuffer r11 = r2.put(r1, r5, r3)     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            r11.position(r5)     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            int r11 = r12.size     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            int r11 = r11 - r3
            java.nio.ByteBuffer r1 = r4.put(r1, r3, r11)     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            r1.position(r5)     // Catch:{ Exception -> 0x0dcb, all -> 0x12f9 }
            goto L_0x0da9
        L_0x0da2:
            int r2 = r2 + -1
            r3 = 1
            r11 = -5
            goto L_0x0d69
        L_0x0da7:
            r2 = 0
            r4 = 0
        L_0x0da9:
            r11 = r71
            r3 = r72
            r1 = r54
            android.media.MediaFormat r5 = android.media.MediaFormat.createVideoFormat(r1, r11, r3)     // Catch:{ Exception -> 0x0dc7, all -> 0x12f9 }
            if (r2 == 0) goto L_0x0dbf
            if (r4 == 0) goto L_0x0dbf
            r5.setByteBuffer(r10, r2)     // Catch:{ Exception -> 0x0dc7, all -> 0x12f9 }
            java.lang.String r2 = "csd-1"
            r5.setByteBuffer(r2, r4)     // Catch:{ Exception -> 0x0dc7, all -> 0x12f9 }
        L_0x0dbf:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0dc7, all -> 0x12f9 }
            r4 = 0
            int r5 = r2.addTrack(r5, r4)     // Catch:{ Exception -> 0x0dc7, all -> 0x12f9 }
            goto L_0x0de4
        L_0x0dc7:
            r0 = move-exception
            r4 = r0
            r2 = r3
            goto L_0x0dd1
        L_0x0dcb:
            r0 = move-exception
            r11 = r71
            r2 = r72
            r4 = r0
        L_0x0dd1:
            r3 = r9
            goto L_0x0b8a
        L_0x0dd4:
            r0 = move-exception
            r9 = r3
            goto L_0x0c1c
        L_0x0dd8:
            r11 = r71
            r9 = r3
            r28 = r14
            r1 = r54
            r14 = 3
            r24 = 2
            r3 = r72
        L_0x0de4:
            int r2 = r12.flags     // Catch:{ Exception -> 0x10bf }
            r2 = r2 & 4
            if (r2 == 0) goto L_0x0dec
            r2 = 1
            goto L_0x0ded
        L_0x0dec:
            r2 = 0
        L_0x0ded:
            r4 = 0
            r6.releaseOutputBuffer(r13, r4)     // Catch:{ Exception -> 0x10bf }
            r26 = r5
            r29 = r21
            r62 = r50
            r5 = -1
            r21 = r74
            r50 = r47
            r47 = r33
            r33 = r2
        L_0x0e00:
            if (r13 == r5) goto L_0x0e1f
            r54 = r1
            r74 = r6
            r3 = r9
            r4 = r21
            r11 = r22
            r5 = r26
            r24 = r28
            r21 = r29
            r22 = r33
            r33 = r47
            r47 = r50
            r9 = r55
            r50 = r62
            r1 = r77
            goto L_0x0ba6
        L_0x0e1f:
            if (r37 != 0) goto L_0x1075
            r4 = r9
            r14 = 2500(0x9c4, double:1.235E-320)
            int r2 = r4.dequeueOutputBuffer(r12, r14)     // Catch:{ Exception -> 0x1061 }
            if (r2 != r5) goto L_0x0e4a
            r16 = r1
            r13 = r3
            r31 = r4
            r1 = r6
            r30 = r10
            r15 = r52
            r32 = r53
            r9 = r56
            r54 = r57
            r51 = r61
            r2 = 18
            r3 = 2500(0x9c4, double:1.235E-320)
            r10 = 0
            r14 = 1
            r35 = 0
            r52 = -1
        L_0x0e46:
            r55 = 0
            goto L_0x1091
        L_0x0e4a:
            r13 = -3
            if (r2 != r13) goto L_0x0e55
        L_0x0e4d:
            r16 = r1
            r13 = r3
            r31 = r4
            r1 = r6
            goto L_0x107b
        L_0x0e55:
            r13 = -2
            if (r2 != r13) goto L_0x0e7e
            android.media.MediaFormat r2 = r4.getOutputFormat()     // Catch:{ Exception -> 0x0e75 }
            boolean r13 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0e75 }
            if (r13 == 0) goto L_0x0e4d
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0e75 }
            r13.<init>()     // Catch:{ Exception -> 0x0e75 }
            java.lang.String r14 = "newFormat = "
            r13.append(r14)     // Catch:{ Exception -> 0x0e75 }
            r13.append(r2)     // Catch:{ Exception -> 0x0e75 }
            java.lang.String r2 = r13.toString()     // Catch:{ Exception -> 0x0e75 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x0e75 }
            goto L_0x0e4d
        L_0x0e75:
            r0 = move-exception
            r2 = r3
            r3 = r4
            r1 = r27
            r10 = 0
        L_0x0e7b:
            r14 = 1
            goto L_0x08b9
        L_0x0e7e:
            if (r2 < 0) goto L_0x1036
            int r13 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1061 }
            r14 = 18
            if (r13 < r14) goto L_0x0e90
            int r13 = r12.size     // Catch:{ Exception -> 0x0e75 }
            if (r13 == 0) goto L_0x0e8c
            r13 = 1
            goto L_0x0e8d
        L_0x0e8c:
            r13 = 0
        L_0x0e8d:
            r30 = 0
            goto L_0x0ea2
        L_0x0e90:
            int r13 = r12.size     // Catch:{ Exception -> 0x1061 }
            if (r13 != 0) goto L_0x0e9f
            long r13 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0e75 }
            r30 = 0
            int r15 = (r13 > r30 ? 1 : (r13 == r30 ? 0 : -1))
            if (r15 == 0) goto L_0x0e9d
            goto L_0x0ea1
        L_0x0e9d:
            r13 = 0
            goto L_0x0ea2
        L_0x0e9f:
            r30 = 0
        L_0x0ea1:
            r13 = 1
        L_0x0ea2:
            int r14 = (r77 > r30 ? 1 : (r77 == r30 ? 0 : -1))
            if (r14 <= 0) goto L_0x0eba
            long r14 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0e75 }
            int r40 = (r14 > r77 ? 1 : (r14 == r77 ? 0 : -1))
            if (r40 < 0) goto L_0x0eba
            int r13 = r12.flags     // Catch:{ Exception -> 0x0e75 }
            r13 = r13 | 4
            r12.flags = r13     // Catch:{ Exception -> 0x0e75 }
            r13 = 0
            r14 = 0
            r36 = 1
            r37 = 1
            goto L_0x0ebc
        L_0x0eba:
            r14 = 0
        L_0x0ebc:
            int r30 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1))
            if (r30 <= 0) goto L_0x0ef7
            r30 = -1
            int r40 = (r48 > r30 ? 1 : (r48 == r30 ? 0 : -1))
            if (r40 != 0) goto L_0x0ef7
            r30 = r10
            long r9 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0e75 }
            int r31 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r31 >= 0) goto L_0x0ef2
            boolean r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0e75 }
            if (r9 == 0) goto L_0x0ef0
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0e75 }
            r9.<init>()     // Catch:{ Exception -> 0x0e75 }
            java.lang.String r10 = "drop frame startTime = "
            r9.append(r10)     // Catch:{ Exception -> 0x0e75 }
            r9.append(r7)     // Catch:{ Exception -> 0x0e75 }
            java.lang.String r10 = " present time = "
            r9.append(r10)     // Catch:{ Exception -> 0x0e75 }
            long r14 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0e75 }
            r9.append(r14)     // Catch:{ Exception -> 0x0e75 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0e75 }
            org.telegram.messenger.FileLog.d(r9)     // Catch:{ Exception -> 0x0e75 }
        L_0x0ef0:
            r13 = 0
            goto L_0x0ef9
        L_0x0ef2:
            long r9 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0e75 }
            r48 = r9
            goto L_0x0ef9
        L_0x0ef7:
            r30 = r10
        L_0x0ef9:
            r4.releaseOutputBuffer(r2, r13)     // Catch:{ Exception -> 0x1061 }
            if (r13 == 0) goto L_0x0fca
            r56.awaitNewImage()     // Catch:{ Exception -> 0x0var_ }
            r2 = 0
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ Exception -> 0x0fc3 }
            r2 = 1
        L_0x0var_:
            if (r2 != 0) goto L_0x0fca
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0fc3 }
            r9 = 18
            if (r2 < r9) goto L_0x0var_
            r9 = r56
            r10 = 0
            r9.drawImage(r10)     // Catch:{ Exception -> 0x0f3a }
            long r13 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0f3a }
            r40 = 1000(0x3e8, double:4.94E-321)
            long r13 = r13 * r40
            r15 = r52
            r15.setPresentationTime(r13)     // Catch:{ Exception -> 0x0var_ }
            r15.swapBuffers()     // Catch:{ Exception -> 0x0var_ }
            r16 = r1
            r13 = r3
            r31 = r4
            r32 = r53
            r54 = r57
            r51 = r61
            goto L_0x0fda
        L_0x0var_:
            r0 = move-exception
            r2 = r3
            r3 = r4
            r56 = r9
            r52 = r15
            goto L_0x0var_
        L_0x0f3a:
            r0 = move-exception
            r15 = r52
            r2 = r3
            r3 = r4
            r56 = r9
        L_0x0var_:
            r1 = r27
            goto L_0x0e7b
        L_0x0var_:
            r15 = r52
            r9 = r56
            r10 = 0
            r13 = 2500(0x9c4, double:1.235E-320)
            int r41 = r6.dequeueInputBuffer(r13)     // Catch:{ Exception -> 0x0fb3 }
            if (r41 < 0) goto L_0x0var_
            r14 = 1
            r9.drawImage(r14)     // Catch:{ Exception -> 0x0f8f }
            java.nio.ByteBuffer r2 = r9.getFrame()     // Catch:{ Exception -> 0x0f8f }
            r13 = r18[r41]     // Catch:{ Exception -> 0x0f8f }
            r13.clear()     // Catch:{ Exception -> 0x0f8f }
            r16 = r1
            r1 = r2
            r2 = r13
            r13 = r3
            r31 = r4
            r3 = r17
            r32 = r53
            r35 = 0
            r4 = r71
            r51 = r61
            r52 = -1
            r5 = r72
            r53 = r6
            r54 = r57
            r6 = r39
            r7 = r38
            org.telegram.messenger.Utilities.convertVideoFrame(r1, r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0ff1 }
            r42 = 0
            long r1 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0ff1 }
            r46 = 0
            r40 = r53
            r43 = r25
            r44 = r1
            r40.queueInputBuffer(r41, r42, r43, r44, r46)     // Catch:{ Exception -> 0x0ff1 }
            goto L_0x0fe1
        L_0x0f8f:
            r0 = move-exception
            r31 = r4
            r53 = r6
            r54 = r57
            goto L_0x0fbb
        L_0x0var_:
            r16 = r1
            r13 = r3
            r31 = r4
            r32 = r53
            r54 = r57
            r51 = r61
            r14 = 1
            r35 = 0
            r52 = -1
            r53 = r6
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0ff1 }
            if (r1 == 0) goto L_0x0fe1
            java.lang.String r1 = "input buffer not available"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0ff1 }
            goto L_0x0fe1
        L_0x0fb3:
            r0 = move-exception
            r31 = r4
            r53 = r6
            r54 = r57
            r14 = 1
        L_0x0fbb:
            r4 = r0
            r2 = r3
            r56 = r9
            r52 = r15
            goto L_0x106f
        L_0x0fc3:
            r0 = move-exception
            r31 = r4
            r53 = r6
            goto L_0x1065
        L_0x0fca:
            r16 = r1
            r13 = r3
            r31 = r4
            r15 = r52
            r32 = r53
            r9 = r56
            r54 = r57
            r51 = r61
            r10 = 0
        L_0x0fda:
            r14 = 1
            r35 = 0
            r52 = -1
            r53 = r6
        L_0x0fe1:
            int r1 = r12.flags     // Catch:{ Exception -> 0x1032 }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x102a
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1032 }
            if (r1 == 0) goto L_0x1000
            java.lang.String r1 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0ff1 }
            goto L_0x1000
        L_0x0ff1:
            r0 = move-exception
            r4 = r0
            r56 = r9
            r2 = r13
            r52 = r15
            r1 = r27
            r3 = r31
            r6 = r53
            goto L_0x1173
        L_0x1000:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1032 }
            r2 = 18
            if (r1 < r2) goto L_0x100f
            r53.signalEndOfInputStream()     // Catch:{ Exception -> 0x0ff1 }
            r1 = r53
            r3 = 2500(0x9c4, double:1.235E-320)
            goto L_0x0e46
        L_0x100f:
            r1 = r53
            r3 = 2500(0x9c4, double:1.235E-320)
            int r41 = r1.dequeueInputBuffer(r3)     // Catch:{ Exception -> 0x1059 }
            if (r41 < 0) goto L_0x0e46
            r42 = 0
            r43 = 1
            long r5 = r12.presentationTimeUs     // Catch:{ Exception -> 0x1059 }
            r46 = 4
            r40 = r1
            r44 = r5
            r40.queueInputBuffer(r41, r42, r43, r44, r46)     // Catch:{ Exception -> 0x1059 }
            goto L_0x0e46
        L_0x102a:
            r1 = r53
            r2 = 18
            r3 = 2500(0x9c4, double:1.235E-320)
            goto L_0x1091
        L_0x1032:
            r0 = move-exception
            r1 = r53
            goto L_0x105a
        L_0x1036:
            r13 = r3
            r31 = r4
            r1 = r6
            r15 = r52
            r9 = r56
            r54 = r57
            r10 = 0
            r14 = 1
            java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1059 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1059 }
            r4.<init>()     // Catch:{ Exception -> 0x1059 }
            java.lang.String r5 = "unexpected result from decoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x1059 }
            r4.append(r2)     // Catch:{ Exception -> 0x1059 }
            java.lang.String r2 = r4.toString()     // Catch:{ Exception -> 0x1059 }
            r3.<init>(r2)     // Catch:{ Exception -> 0x1059 }
            throw r3     // Catch:{ Exception -> 0x1059 }
        L_0x1059:
            r0 = move-exception
        L_0x105a:
            r4 = r0
            r6 = r1
            r56 = r9
            r2 = r13
            goto L_0x112c
        L_0x1061:
            r0 = move-exception
            r31 = r4
            r1 = r6
        L_0x1065:
            r15 = r52
            r9 = r56
            r54 = r57
            r10 = 0
            r14 = 1
            r4 = r0
            r2 = r3
        L_0x106f:
            r1 = r27
            r3 = r31
            goto L_0x1245
        L_0x1075:
            r16 = r1
            r13 = r3
            r1 = r6
            r31 = r9
        L_0x107b:
            r30 = r10
            r15 = r52
            r32 = r53
            r9 = r56
            r54 = r57
            r51 = r61
            r2 = 18
            r3 = 2500(0x9c4, double:1.235E-320)
            r10 = 0
            r14 = 1
            r35 = 0
            r52 = -1
        L_0x1091:
            r7 = r75
            r74 = r1
            r56 = r9
            r52 = r15
            r4 = r21
            r11 = r22
            r5 = r26
            r24 = r28
            r21 = r29
            r10 = r30
            r3 = r31
            r53 = r32
            r22 = r33
            r33 = r47
            r47 = r50
            r61 = r51
            r57 = r54
            r9 = r55
            r50 = r62
            r15 = r66
            r1 = r77
            r54 = r16
            goto L_0x0ba6
        L_0x10bf:
            r0 = move-exception
            r1 = r6
            r31 = r9
            goto L_0x1065
        L_0x10c4:
            r0 = move-exception
            r11 = r71
            r31 = r3
            r1 = r6
            r15 = r52
            r9 = r56
            r54 = r57
            r10 = 0
            r14 = 1
            r2 = r72
            goto L_0x1143
        L_0x10d6:
            r11 = r71
            r2 = r72
            r31 = r3
            r1 = r6
            r15 = r52
            r9 = r56
            r54 = r57
            r10 = 0
            r14 = 1
            java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1127 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1127 }
            r4.<init>()     // Catch:{ Exception -> 0x1127 }
            java.lang.String r5 = "encoderOutputBuffer "
            r4.append(r5)     // Catch:{ Exception -> 0x1127 }
            r4.append(r13)     // Catch:{ Exception -> 0x1127 }
            java.lang.String r5 = " was null"
            r4.append(r5)     // Catch:{ Exception -> 0x1127 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x1127 }
            r3.<init>(r4)     // Catch:{ Exception -> 0x1127 }
            throw r3     // Catch:{ Exception -> 0x1127 }
        L_0x1101:
            r11 = r71
            r2 = r72
            r31 = r3
            r1 = r6
            r15 = r52
            r9 = r56
            r54 = r57
            r10 = 0
            r14 = 1
            java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1127 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1127 }
            r4.<init>()     // Catch:{ Exception -> 0x1127 }
            java.lang.String r5 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x1127 }
            r4.append(r13)     // Catch:{ Exception -> 0x1127 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x1127 }
            r3.<init>(r4)     // Catch:{ Exception -> 0x1127 }
            throw r3     // Catch:{ Exception -> 0x1127 }
        L_0x1127:
            r0 = move-exception
            r4 = r0
            r6 = r1
            r56 = r9
        L_0x112c:
            r52 = r15
            r1 = r27
            r3 = r31
            goto L_0x1173
        L_0x1133:
            r0 = move-exception
            r11 = r71
            r2 = r72
            r31 = r3
            r1 = r6
            r15 = r52
            r9 = r56
            r54 = r57
            r10 = 0
            r14 = 1
        L_0x1143:
            r4 = r0
            goto L_0x1158
        L_0x1145:
            r0 = move-exception
            r11 = r71
            r2 = r72
            r1 = r74
            r31 = r3
            r15 = r52
            r9 = r56
            r54 = r57
            r10 = 0
            r14 = 1
            r4 = r0
            r6 = r1
        L_0x1158:
            r1 = r27
            goto L_0x1245
        L_0x115c:
            r0 = move-exception
            r11 = r71
            r1 = r74
            r31 = r3
            r9 = r12
            r2 = r14
            r15 = r52
            r59 = r54
            r10 = 0
            r14 = 1
            r54 = r6
            r4 = r0
            r6 = r1
            r56 = r9
            r1 = r27
        L_0x1173:
            r57 = r54
            goto L_0x1245
        L_0x1177:
            r0 = move-exception
            r1 = r74
            r31 = r3
            r35 = r4
            r9 = r5
            r11 = r6
            r2 = r14
            r15 = r52
            r59 = r54
            r10 = 0
            r14 = 1
            r4 = r0
            r6 = r1
            r56 = r9
            r1 = r27
            goto L_0x11a6
        L_0x118e:
            r0 = move-exception
            r1 = r74
            r9 = r5
            r11 = r6
            r2 = r14
            r31 = r48
            r15 = r52
            r59 = r54
            r10 = 0
            r14 = 1
            r35 = 0
            r4 = r0
            r6 = r1
            r56 = r9
            r1 = r27
            r3 = r31
        L_0x11a6:
            r57 = r35
            goto L_0x1245
        L_0x11aa:
            r0 = move-exception
            r1 = r74
            r11 = r6
            goto L_0x11b4
        L_0x11af:
            r0 = move-exception
            r11 = r71
            r1 = r74
        L_0x11b4:
            r2 = r14
            r31 = r48
            r15 = r52
            r59 = r54
            r10 = 0
            r14 = 1
            r35 = 0
            r4 = r0
            r6 = r1
            goto L_0x11d2
        L_0x11c2:
            r0 = move-exception
            r31 = r74
            r15 = r4
            r1 = r6
            r11 = r7
            r2 = r14
            r59 = r33
            r10 = 0
            r14 = 1
            r35 = 0
            r4 = r0
            r52 = r15
        L_0x11d2:
            r1 = r27
            r3 = r31
            r56 = r35
            goto L_0x1243
        L_0x11da:
            r0 = move-exception
            r15 = r4
            r1 = r6
            r11 = r7
            r2 = r14
            r59 = r33
            r10 = 0
            r14 = 1
            r35 = 0
            r4 = r0
            r52 = r15
            r1 = r27
            r3 = r35
            r56 = r3
            goto L_0x1243
        L_0x11f0:
            r0 = move-exception
            r1 = r6
            r11 = r7
            r2 = r14
            r59 = r33
            r10 = 0
            r14 = 1
            r35 = 0
            goto L_0x1205
        L_0x11fb:
            r0 = move-exception
            r35 = r1
            r1 = r6
            r11 = r7
            r2 = r14
            r59 = r33
            r10 = 0
            r14 = 1
        L_0x1205:
            r4 = r0
            r1 = r27
            r3 = r35
            r52 = r3
            goto L_0x1241
        L_0x120d:
            r0 = move-exception
            r11 = r7
            r2 = r14
            r59 = r33
            r10 = 0
            r14 = 1
            r35 = 0
            r4 = r0
            r1 = r27
            goto L_0x123c
        L_0x121a:
            r0 = move-exception
            r11 = r7
            r2 = r14
            r59 = r33
            goto L_0x1225
        L_0x1220:
            r0 = move-exception
            r59 = r6
            r11 = r7
            r2 = r14
        L_0x1225:
            r10 = 0
            r14 = 1
            r35 = 0
            goto L_0x123b
        L_0x122a:
            r0 = move-exception
            r59 = r6
            r11 = r7
            r2 = r14
            goto L_0x1235
        L_0x1230:
            r0 = move-exception
            r59 = r6
            r2 = r14
            r11 = r15
        L_0x1235:
            r10 = 0
            r14 = 1
            r35 = 0
            r1 = r74
        L_0x123b:
            r4 = r0
        L_0x123c:
            r3 = r35
            r6 = r3
            r52 = r6
        L_0x1241:
            r56 = r52
        L_0x1243:
            r57 = r56
        L_0x1245:
            boolean r5 = r4 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x129b }
            if (r5 == 0) goto L_0x124c
            if (r82 != 0) goto L_0x124c
            r10 = 1
        L_0x124c:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x129b }
            r5.<init>()     // Catch:{ Exception -> 0x129b }
            java.lang.String r7 = "bitrate: "
            r5.append(r7)     // Catch:{ Exception -> 0x129b }
            r5.append(r1)     // Catch:{ Exception -> 0x129b }
            java.lang.String r7 = " framerate: "
            r5.append(r7)     // Catch:{ Exception -> 0x129b }
            r8 = r73
            r5.append(r8)     // Catch:{ Exception -> 0x1297 }
            java.lang.String r7 = " size: "
            r5.append(r7)     // Catch:{ Exception -> 0x1297 }
            r5.append(r2)     // Catch:{ Exception -> 0x1297 }
            java.lang.String r7 = "x"
            r5.append(r7)     // Catch:{ Exception -> 0x1297 }
            r5.append(r11)     // Catch:{ Exception -> 0x1297 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x1297 }
            org.telegram.messenger.FileLog.e((java.lang.String) r5)     // Catch:{ Exception -> 0x1297 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ Exception -> 0x1297 }
            r15 = r66
            r27 = r1
            r1 = r3
            r35 = r10
            r12 = r56
            r3 = 1
        L_0x1287:
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x12bf, all -> 0x12f9 }
            r5 = r59
            r4.unselectTrack(r5)     // Catch:{ Exception -> 0x12bf, all -> 0x12f9 }
            if (r1 == 0) goto L_0x12b9
            r1.stop()     // Catch:{ Exception -> 0x12bf, all -> 0x12f9 }
            r1.release()     // Catch:{ Exception -> 0x12bf, all -> 0x12f9 }
            goto L_0x12b9
        L_0x1297:
            r0 = move-exception
            r15 = r66
            goto L_0x12a0
        L_0x129b:
            r0 = move-exception
            r15 = r66
            r8 = r73
        L_0x12a0:
            r9 = r1
            goto L_0x1311
        L_0x12a3:
            r8 = r73
            r2 = r14
            r11 = r15
            r10 = 0
            r14 = 1
            r35 = 0
            r15 = r66
            r27 = r74
            r6 = r35
            r12 = r6
            r52 = r12
            r57 = r52
            r3 = 0
            r35 = 0
        L_0x12b9:
            if (r12 == 0) goto L_0x12c7
            r12.release()     // Catch:{ Exception -> 0x12bf, all -> 0x12f9 }
            goto L_0x12c7
        L_0x12bf:
            r0 = move-exception
            r1 = r0
            r9 = r27
            r10 = r35
            goto L_0x1312
        L_0x12c7:
            if (r52 == 0) goto L_0x12cc
            r52.release()     // Catch:{ Exception -> 0x12bf, all -> 0x12f9 }
        L_0x12cc:
            if (r6 == 0) goto L_0x12d4
            r6.stop()     // Catch:{ Exception -> 0x12bf, all -> 0x12f9 }
            r6.release()     // Catch:{ Exception -> 0x12bf, all -> 0x12f9 }
        L_0x12d4:
            if (r57 == 0) goto L_0x12d9
            r57.release()     // Catch:{ Exception -> 0x12bf, all -> 0x12f9 }
        L_0x12d9:
            r66.checkConversionCanceled()     // Catch:{ Exception -> 0x12bf, all -> 0x12f9 }
            r14 = r2
            r2 = r3
        L_0x12de:
            android.media.MediaExtractor r1 = r15.extractor
            if (r1 == 0) goto L_0x12e5
            r1.release()
        L_0x12e5:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer
            if (r1 == 0) goto L_0x12f2
            r1.finishMovie()     // Catch:{ Exception -> 0x12ed }
            goto L_0x12f2
        L_0x12ed:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x12f2:
            r1 = r2
            r6 = r11
            r7 = r14
            r9 = r27
            goto L_0x135a
        L_0x12f9:
            r0 = move-exception
            r2 = r0
            r1 = r15
            goto L_0x1383
        L_0x12fe:
            r0 = move-exception
            r8 = r7
            r2 = r11
            r11 = r12
            goto L_0x130d
        L_0x1303:
            r0 = move-exception
            r2 = r0
            r1 = r14
            goto L_0x1383
        L_0x1308:
            r0 = move-exception
            r8 = r10
            r2 = r11
            r11 = r12
            r15 = r14
        L_0x130d:
            r10 = 0
            r14 = 1
            r9 = r74
        L_0x1311:
            r1 = r0
        L_0x1312:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x137f }
            r3.<init>()     // Catch:{ all -> 0x137f }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x137f }
            r3.append(r9)     // Catch:{ all -> 0x137f }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x137f }
            r3.append(r8)     // Catch:{ all -> 0x137f }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x137f }
            r3.append(r2)     // Catch:{ all -> 0x137f }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x137f }
            r3.append(r11)     // Catch:{ all -> 0x137f }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x137f }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x137f }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x137f }
            android.media.MediaExtractor r1 = r15.extractor
            if (r1 == 0) goto L_0x1348
            r1.release()
        L_0x1348:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer
            if (r1 == 0) goto L_0x1355
            r1.finishMovie()     // Catch:{ Exception -> 0x1350 }
            goto L_0x1355
        L_0x1350:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1355:
            r7 = r2
            r35 = r10
            r6 = r11
            r1 = 1
        L_0x135a:
            if (r35 == 0) goto L_0x137e
            r17 = 1
            r1 = r66
            r2 = r67
            r3 = r68
            r4 = r69
            r5 = r70
            r8 = r73
            r10 = r75
            r12 = r77
            r14 = r79
            r16 = r81
            r18 = r83
            r19 = r84
            r20 = r85
            r21 = r86
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r14, r16, r17, r18, r19, r20, r21)
        L_0x137e:
            return r1
        L_0x137f:
            r0 = move-exception
            r1 = r66
            r2 = r0
        L_0x1383:
            android.media.MediaExtractor r3 = r1.extractor
            if (r3 == 0) goto L_0x138a
            r3.release()
        L_0x138a:
            org.telegram.messenger.video.MP4Builder r3 = r1.mediaMuxer
            if (r3 == 0) goto L_0x1397
            r3.finishMovie()     // Catch:{ Exception -> 0x1392 }
            goto L_0x1397
        L_0x1392:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x1397:
            goto L_0x1399
        L_0x1398:
            throw r2
        L_0x1399:
            goto L_0x1398
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, long, long, long, boolean, boolean, org.telegram.messenger.MediaController$SavedFilterState, java.lang.String, java.util.ArrayList, boolean):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00f0, code lost:
        if (r9[r15 + 3] != 1) goto L_0x00f6;
     */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0199  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long readAndWriteTracks(android.media.MediaExtractor r27, org.telegram.messenger.video.MP4Builder r28, android.media.MediaCodec.BufferInfo r29, long r30, long r32, long r34, java.io.File r36, boolean r37) throws java.lang.Exception {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            r2 = r28
            r3 = r29
            r4 = r30
            r6 = 0
            int r7 = org.telegram.messenger.MediaController.findTrack(r1, r6)
            r9 = 1
            if (r37 == 0) goto L_0x0019
            int r10 = org.telegram.messenger.MediaController.findTrack(r1, r9)
            r11 = r34
            goto L_0x001c
        L_0x0019:
            r11 = r34
            r10 = -1
        L_0x001c:
            float r11 = (float) r11
            r12 = 1148846080(0x447a0000, float:1000.0)
            float r11 = r11 / r12
            java.lang.String r13 = "max-input-size"
            r14 = 0
            if (r7 < 0) goto L_0x0041
            r1.selectTrack(r7)
            android.media.MediaFormat r12 = r1.getTrackFormat(r7)
            int r16 = r2.addTrack(r12, r6)
            int r12 = r12.getInteger(r13)
            int r17 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r17 <= 0) goto L_0x003d
            r1.seekTo(r4, r6)
            goto L_0x0044
        L_0x003d:
            r1.seekTo(r14, r6)
            goto L_0x0044
        L_0x0041:
            r12 = 0
            r16 = -1
        L_0x0044:
            if (r10 < 0) goto L_0x007a
            r1.selectTrack(r10)
            android.media.MediaFormat r8 = r1.getTrackFormat(r10)
            java.lang.String r6 = "mime"
            java.lang.String r6 = r8.getString(r6)
            java.lang.String r14 = "audio/unknown"
            boolean r6 = r6.equals(r14)
            if (r6 == 0) goto L_0x005e
            r6 = -1
            r10 = -1
            goto L_0x007b
        L_0x005e:
            int r6 = r2.addTrack(r8, r9)
            int r8 = r8.getInteger(r13)
            int r12 = java.lang.Math.max(r8, r12)
            r13 = 0
            int r8 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r8 <= 0) goto L_0x0075
            r8 = 0
            r1.seekTo(r4, r8)
            goto L_0x007b
        L_0x0075:
            r8 = 0
            r1.seekTo(r13, r8)
            goto L_0x007b
        L_0x007a:
            r6 = -1
        L_0x007b:
            java.nio.ByteBuffer r8 = java.nio.ByteBuffer.allocateDirect(r12)
            r12 = -1
            if (r10 >= 0) goto L_0x0087
            if (r7 < 0) goto L_0x0086
            goto L_0x0087
        L_0x0086:
            return r12
        L_0x0087:
            r26.checkConversionCanceled()
            r20 = r12
            r14 = 0
            r18 = 0
        L_0x008f:
            if (r14 != 0) goto L_0x01c2
            r26.checkConversionCanceled()
            r15 = 0
            int r12 = r1.readSampleData(r8, r15)
            r3.size = r12
            int r12 = r27.getSampleTrackIndex()
            if (r12 != r7) goto L_0x00a5
            r13 = r16
        L_0x00a3:
            r15 = -1
            goto L_0x00ab
        L_0x00a5:
            if (r12 != r10) goto L_0x00a9
            r13 = r6
            goto L_0x00a3
        L_0x00a9:
            r13 = -1
            goto L_0x00a3
        L_0x00ab:
            if (r13 == r15) goto L_0x019f
            int r15 = android.os.Build.VERSION.SDK_INT
            r9 = 21
            if (r15 >= r9) goto L_0x00bc
            r9 = 0
            r8.position(r9)
            int r9 = r3.size
            r8.limit(r9)
        L_0x00bc:
            if (r12 == r10) goto L_0x0129
            byte[] r9 = r8.array()
            if (r9 == 0) goto L_0x0129
            int r15 = r8.arrayOffset()
            int r22 = r8.limit()
            int r22 = r15 + r22
            r35 = r6
            r6 = -1
        L_0x00d1:
            r23 = 4
            r37 = r14
            int r14 = r22 + -4
            if (r15 > r14) goto L_0x0126
            byte r24 = r9[r15]
            if (r24 != 0) goto L_0x00f3
            int r24 = r15 + 1
            byte r24 = r9[r24]
            if (r24 != 0) goto L_0x00f3
            int r24 = r15 + 2
            byte r24 = r9[r24]
            if (r24 != 0) goto L_0x00f3
            int r24 = r15 + 3
            r25 = r10
            byte r10 = r9[r24]
            r1 = 1
            if (r10 == r1) goto L_0x00f8
            goto L_0x00f6
        L_0x00f3:
            r25 = r10
            r1 = 1
        L_0x00f6:
            if (r15 != r14) goto L_0x011d
        L_0x00f8:
            r10 = -1
            if (r6 == r10) goto L_0x011c
            int r10 = r15 - r6
            if (r15 == r14) goto L_0x0100
            goto L_0x0102
        L_0x0100:
            r23 = 0
        L_0x0102:
            int r10 = r10 - r23
            int r14 = r10 >> 24
            byte r14 = (byte) r14
            r9[r6] = r14
            int r14 = r6 + 1
            int r1 = r10 >> 16
            byte r1 = (byte) r1
            r9[r14] = r1
            int r1 = r6 + 2
            int r14 = r10 >> 8
            byte r14 = (byte) r14
            r9[r1] = r14
            int r6 = r6 + 3
            byte r1 = (byte) r10
            r9[r6] = r1
        L_0x011c:
            r6 = r15
        L_0x011d:
            int r15 = r15 + 1
            r1 = r27
            r14 = r37
            r10 = r25
            goto L_0x00d1
        L_0x0126:
            r25 = r10
            goto L_0x012f
        L_0x0129:
            r35 = r6
            r25 = r10
            r37 = r14
        L_0x012f:
            int r1 = r3.size
            if (r1 < 0) goto L_0x013b
            long r9 = r27.getSampleTime()
            r3.presentationTimeUs = r9
            r1 = 0
            goto L_0x013f
        L_0x013b:
            r1 = 0
            r3.size = r1
            r1 = 1
        L_0x013f:
            int r6 = r3.size
            if (r6 <= 0) goto L_0x0163
            if (r1 != 0) goto L_0x0163
            r9 = 0
            if (r12 != r7) goto L_0x0157
            int r6 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r6 <= 0) goto L_0x0157
            r14 = -1
            int r6 = (r20 > r14 ? 1 : (r20 == r14 ? 0 : -1))
            if (r6 != 0) goto L_0x0157
            long r14 = r3.presentationTimeUs
            r20 = r14
        L_0x0157:
            int r6 = (r32 > r9 ? 1 : (r32 == r9 ? 0 : -1))
            if (r6 < 0) goto L_0x0167
            long r9 = r3.presentationTimeUs
            int r6 = (r9 > r32 ? 1 : (r9 == r32 ? 0 : -1))
            if (r6 >= 0) goto L_0x0162
            goto L_0x0167
        L_0x0162:
            r1 = 1
        L_0x0163:
            r6 = 0
        L_0x0164:
            r17 = 1148846080(0x447a0000, float:1000.0)
            goto L_0x0197
        L_0x0167:
            r6 = 0
            r3.offset = r6
            int r9 = r27.getSampleFlags()
            r3.flags = r9
            long r9 = r2.writeSampleData(r13, r8, r3, r6)
            r13 = 0
            int r12 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r12 == 0) goto L_0x0164
            org.telegram.messenger.MediaController$VideoConvertorListener r12 = r0.callback
            if (r12 == 0) goto L_0x0164
            long r13 = r3.presentationTimeUs
            long r22 = r13 - r20
            int r12 = (r22 > r18 ? 1 : (r22 == r18 ? 0 : -1))
            if (r12 <= 0) goto L_0x0188
            long r18 = r13 - r20
        L_0x0188:
            r12 = r18
            org.telegram.messenger.MediaController$VideoConvertorListener r14 = r0.callback
            float r15 = (float) r12
            r17 = 1148846080(0x447a0000, float:1000.0)
            float r15 = r15 / r17
            float r15 = r15 / r11
            r14.didWriteData(r9, r15)
            r18 = r12
        L_0x0197:
            if (r1 != 0) goto L_0x019c
            r27.advance()
        L_0x019c:
            r9 = r1
            r1 = -1
            goto L_0x01b1
        L_0x019f:
            r35 = r6
            r25 = r10
            r37 = r14
            r1 = -1
            r6 = 0
            r17 = 1148846080(0x447a0000, float:1000.0)
            if (r12 != r1) goto L_0x01ad
            r9 = 1
            goto L_0x01b1
        L_0x01ad:
            r27.advance()
            r9 = 0
        L_0x01b1:
            if (r9 == 0) goto L_0x01b5
            r14 = 1
            goto L_0x01b7
        L_0x01b5:
            r14 = r37
        L_0x01b7:
            r1 = r27
            r6 = r35
            r10 = r25
            r9 = 1
            r12 = -1
            goto L_0x008f
        L_0x01c2:
            r25 = r10
            r1 = r27
            if (r7 < 0) goto L_0x01cb
            r1.unselectTrack(r7)
        L_0x01cb:
            if (r25 < 0) goto L_0x01d2
            r10 = r25
            r1.unselectTrack(r10)
        L_0x01d2:
            return r20
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.readAndWriteTracks(android.media.MediaExtractor, org.telegram.messenger.video.MP4Builder, android.media.MediaCodec$BufferInfo, long, long, long, java.io.File, boolean):long");
    }

    private void checkConversionCanceled() {
        MediaController.VideoConvertorListener videoConvertorListener = this.callback;
        if (videoConvertorListener != null && videoConvertorListener.checkConversionCanceled()) {
            throw new RuntimeException("canceled conversion");
        }
    }
}
