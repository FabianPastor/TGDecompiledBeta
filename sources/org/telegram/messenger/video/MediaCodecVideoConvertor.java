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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v17, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v19, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v0, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v8, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v21, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v0, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v1, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v7, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v25, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v11, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v2, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v17, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v8, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v1, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v3, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v3, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v4, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v29, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v8, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v33, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v9, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v34, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v4, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v2, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v15, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v17, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v36, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v5, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v37, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v16, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v18, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v3, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v38, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v17, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v4, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v19, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v5, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v6, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v18, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v24, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v40, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v20, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r75v1, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v7, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v41, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v19, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v6, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v21, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v43, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v20, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v7, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v8, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v8, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v21, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v26, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v22, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v46, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v9, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v9, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v22, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v23, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v47, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v10, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v10, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v23, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v24, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v48, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v18, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v50, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v24, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v11, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v11, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v40, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v25, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v19, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v13, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v21, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v14, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v12, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v22, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v26, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v32, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v52, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v25, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v18, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v19, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v20, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v33, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v55, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v26, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v27, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v15, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v56, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v27, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v16, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v13, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v28, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v36, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v17, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v29, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v15, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v18, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v18, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v30, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v55, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v59, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v60, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v31, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v57, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v61, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v19, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v36, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v62, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v64, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v22, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v63, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v65, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v37, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v20, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v64, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v66, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v38, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v65, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v66, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v25, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v69, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v42, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v67, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v70, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v71, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v26, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v77, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v30, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v51, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v77, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v46, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v80, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v46, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v55, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v56, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v58, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v82, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v84, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v48, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v59, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v83, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v85, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v86, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v67, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v87, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v87, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v52, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v74, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v86, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v89, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v88, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v88, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v55, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v63, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v64, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v58, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v59, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v91, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v90, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v89, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v92, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v65, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v66, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v21, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v38, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v22, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v29, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v94, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v63, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v92, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v63, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v93, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v23, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v93, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v95, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v59, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v94, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v24, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v40, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v25, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v41, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v26, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v42, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v27, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v43, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v44, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v28, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v29, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v94, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v96, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v96, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v30, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v31, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v32, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v95, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v97, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v61, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v97, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v33, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v62, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v98, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v34, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v63, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v78, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v100, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v80, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v102, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v64, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v101, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v103, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v84, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v68, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v65, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v104, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v35, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v106, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v87, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v88, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v109, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v108, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v111, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v102, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v110, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v113, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v36, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v30, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v115, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v106, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v115, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v66, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v37, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r75v10, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v38, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v31, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v116, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v108, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v64, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v116, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v67, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r75v11, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v32, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v117, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v109, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v65, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v117, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v39, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r75v12, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v33, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v82, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v118, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v66, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v52, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v34, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r75v13, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v119, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v35, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r75v14, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v83, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v121, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v114, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v36, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v69, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v122, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v53, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v40, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v68, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v123, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v70, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v37, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v125, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v38, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v71, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v200, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v202, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v54, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v39, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v69, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v128, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v86, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v75, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v137, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v41, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v129, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v124, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v76, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v70, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v40, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v55, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v88, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v130, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v131, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v127, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v76, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v135, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v128, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v136, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v99, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v129, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v100, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v137, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v130, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v102, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v104, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v138, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v131, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v139, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v132, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v140, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v133, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v106, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v107, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v108, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v109, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v88, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v67, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v112, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v113, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v133, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v267, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v272, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v138, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v136, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v137, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v141, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v178, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v142, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v179, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v143, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v144, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v146, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v147, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v148, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v149, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v150, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v151, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v141, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v142, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v183, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v185, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v187, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v188, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v153, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v190, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v154, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v156, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v157, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v157, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v158, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v195, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v159, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v171, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v196, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v172, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v173, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v174, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v176, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v160, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v197, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v161, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v180, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v198, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v181, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v163, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v182, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v200, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v138, resolved type: org.telegram.messenger.video.AudioRecoder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v183, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v166, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v202, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v204, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v205, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v168, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v207, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v185, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v172, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v174, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v175, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v176, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v186, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v178, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v214, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v188, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v215, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v216, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v217, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v218, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v220, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v221, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v222, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v193, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v179, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v195, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v223, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v196, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v197, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v198, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v200, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v201, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v180, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v186, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v203, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v205, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v190, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v206, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v227, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v202, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v193, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v212, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v230, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v196, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v233, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v197, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v198, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v235, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v199, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v216, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v203, resolved type: java.lang.Exception} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v154, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v155, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v156, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v321, resolved type: org.telegram.messenger.video.OutputSurface} */
    /* JADX WARNING: type inference failed for: r2v22 */
    /* JADX WARNING: type inference failed for: r2v27 */
    /* JADX WARNING: type inference failed for: r2v28 */
    /* JADX WARNING: type inference failed for: r13v38 */
    /* JADX WARNING: type inference failed for: r9v69 */
    /* JADX WARNING: type inference failed for: r9v70 */
    /* JADX WARNING: type inference failed for: r9v71 */
    /* JADX WARNING: type inference failed for: r4v106 */
    /* JADX WARNING: type inference failed for: r4v107 */
    /* JADX WARNING: type inference failed for: r1v271, types: [int] */
    /* JADX WARNING: type inference failed for: r4v125 */
    /* JADX WARNING: type inference failed for: r4v126 */
    /* JADX WARNING: type inference failed for: r4v127 */
    /* JADX WARNING: type inference failed for: r4v148 */
    /* JADX WARNING: type inference failed for: r2v199 */
    /* JADX WARNING: type inference failed for: r2v200 */
    /* JADX WARNING: type inference failed for: r9v143 */
    /* JADX WARNING: type inference failed for: r9v158 */
    /* JADX WARNING: type inference failed for: r9v160 */
    /* JADX WARNING: type inference failed for: r9v170 */
    /* JADX WARNING: type inference failed for: r9v199 */
    /* JADX WARNING: type inference failed for: r9v207 */
    /* JADX WARNING: type inference failed for: r9v209 */
    /* JADX WARNING: type inference failed for: r9v215 */
    /* JADX WARNING: Code restructure failed: missing block: B:1005:0x1272, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1006:0x1273, code lost:
        r13 = r13;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1007:0x1274, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1008:0x1275, code lost:
        r9 = r74;
        r13 = r13;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1009:0x1278, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1010:0x1279, code lost:
        r9 = r74;
        r13 = r13;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1014:0x1294, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1015:0x1295, code lost:
        r2 = r0;
        r1 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1016:0x1299, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1017:0x129a, code lost:
        r4 = r2;
        r13 = r13;
        r9 = r9;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1018:0x129b, code lost:
        r2 = r0;
        r13 = r13;
        r9 = r9;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1037:0x12cf, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1038:0x12d0, code lost:
        r2 = r0;
        r1 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1039:0x12d4, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1040:0x12d5, code lost:
        r13 = r11;
        r8 = r12;
        r9 = r15;
        r12 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1053:0x132b, code lost:
        r2.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1057:?, code lost:
        r2.finishMovie();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1058:0x1336, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1059:0x1337, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1069:0x136f, code lost:
        r3.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1073:?, code lost:
        r3.finishMovie();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1074:0x137a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1075:0x137b, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x0384, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x0385, code lost:
        r4 = r36;
        r9 = r75;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x03f6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x03f7, code lost:
        r9 = r75;
        r1 = r0;
        r36 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x03fc, code lost:
        r33 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x049a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x049b, code lost:
        r2 = r0;
        r4 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x04b7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x04b8, code lost:
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x04b9, code lost:
        r1 = r9;
        r13 = r11;
        r8 = r12;
        r9 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x04c4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x04c5, code lost:
        r2 = r0;
        r1 = r9;
        r13 = r11;
        r8 = r12;
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x04fc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x04fd, code lost:
        r2 = r0;
        r1 = r9;
        r13 = r11;
        r8 = r12;
        r9 = r15;
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x0715, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x0716, code lost:
        r2 = r0;
        r1 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0719, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x071a, code lost:
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x071b, code lost:
        r13 = r14;
        r8 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x071d, code lost:
        r50 = r36;
        r9 = r9;
        r13 = r13;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x0736, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x0737, code lost:
        r2 = r0;
        r13 = r14;
        r8 = r15;
        r1 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x075a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:432:0x075b, code lost:
        r2 = r0;
        r53 = r1;
        r13 = r14;
        r8 = r15;
        r1 = r20;
        r50 = r36;
        r6 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x0769, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x076a, code lost:
        r2 = r0;
        r13 = r14;
        r8 = r15;
        r1 = r20;
        r50 = r36;
        r6 = null;
        r53 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x0777, code lost:
        r60 = null;
        r61 = null;
        r14 = r7;
        r9 = r9;
        r13 = r13;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x07c7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x07c8, code lost:
        r2 = r0;
        r13 = r14;
        r8 = r15;
        r1 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x07ce, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x07cf, code lost:
        r49 = r4;
        r53 = r6;
        r75 = r7;
        r2 = r0;
        r13 = r14;
        r8 = r15;
        r1 = r20;
        r50 = r36;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x07dc, code lost:
        r6 = r49;
        r60 = null;
        r13 = r13;
        r9 = r9;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x07e3, code lost:
        r61 = null;
        r13 = r13;
        r9 = r9;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x0873, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x0874, code lost:
        r2 = r0;
        r60 = r9;
        r13 = r14;
        r8 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x0879, code lost:
        r1 = r20;
        r13 = r13;
        r8 = r8;
        r2 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x087c, code lost:
        r9 = r9;
        r13 = r13;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x08bf, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x08c0, code lost:
        r2 = r0;
        r60 = r9;
        r13 = r14;
        r8 = r15;
        r1 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x08e8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x08e9, code lost:
        r7 = r76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x090d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x090e, code lost:
        r8 = r72;
        r2 = r0;
        r60 = r9;
        r13 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0938, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x093a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x093b, code lost:
        r13 = r78;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x093d, code lost:
        r8 = r72;
        r13 = r73;
        r14 = r75;
        r2 = r0;
        r61 = r4;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0948, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x094a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x094b, code lost:
        r15 = r68;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x0961, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x0962, code lost:
        r15 = r68;
        r7 = r76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x0966, code lost:
        r13 = r78;
        r8 = r72;
        r13 = r73;
        r14 = r75;
        r2 = r0;
        r60 = r9;
        r1 = r20;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x09c9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x09ca, code lost:
        r8 = r72;
        r13 = r73;
        r14 = r75;
        r2 = r0;
        r61 = r5;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x09d3, code lost:
        r60 = r9;
        r13 = r13;
        r9 = r9;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x09fa, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x09fb, code lost:
        r8 = r72;
        r13 = r73;
        r14 = r75;
        r2 = r0;
        r61 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0113, code lost:
        r4 = r6;
        r5 = r7;
        r6 = r8;
        r7 = r9;
        r8 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0a46, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:600:0x0a47, code lost:
        r61 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0a49, code lost:
        r8 = r72;
        r13 = r73;
        r14 = r75;
        r2 = r0;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0a50, code lost:
        r50 = r15;
        r13 = r13;
        r9 = r9;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x0b01, code lost:
        r0 = e;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x0b04, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x0b05, code lost:
        r61 = r5;
        r60 = r9;
        r8 = r72;
        r13 = r73;
        r14 = r75;
        r2 = r0;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0b9e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x0b9f, code lost:
        r8 = r72;
        r13 = r73;
        r2 = r0;
        r14 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0CLASSNAME, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x0cce, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x0ccf, code lost:
        r8 = r72;
        r2 = r0;
        r14 = r10;
        r6 = r13;
        r1 = r20;
        r13 = r73;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x0cee, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x0cf0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x0cf1, code lost:
        r29 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x0cf3, code lost:
        r8 = r72;
        r13 = r73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x0d04, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x0d05, code lost:
        r29 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x0d83, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x0d84, code lost:
        r2 = r0;
        r8 = r5;
        r13 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x0d87, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x0d88, code lost:
        r13 = r73;
        r8 = r72;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x0d8c, code lost:
        r2 = r0;
        r13 = r13;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x0d8d, code lost:
        r14 = r10;
        r1 = r20;
        r6 = r29;
        r9 = r9;
        r13 = r13;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x0d94, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x0d95, code lost:
        r13 = r73;
        r29 = r6;
        r8 = r72;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x0e40, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x0e41, code lost:
        r2 = r0;
        r6 = r3;
        r8 = r5;
        r13 = r13;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x0ecf, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x0var_, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x0var_, code lost:
        r2 = r0;
        r6 = r3;
        r53 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x0f0e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x0f0f, code lost:
        r8 = r53;
        r2 = r0;
        r6 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x0var_, code lost:
        r60 = r9;
        r14 = r10;
        r1 = r20;
        r8 = r5;
        r13 = r13;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x0f8f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:906:0x0var_, code lost:
        r64 = r8;
        r48 = r61;
        r8 = r5;
        r2 = r0;
        r60 = r9;
        r14 = r10;
        r1 = r20;
        r6 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x0ff5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x0ff6, code lost:
        r2 = r0;
        r60 = r9;
        r14 = r10;
        r1 = r20;
        r6 = r35;
        r61 = r48;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x1000, code lost:
        r53 = r64;
        r9 = r9;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x1003, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x1004, code lost:
        r8 = r5;
        r64 = r53;
        r9 = r60;
        r48 = r61;
        r2 = r0;
        r14 = r10;
        r1 = r20;
        r6 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x1015, code lost:
        r13 = r13;
        r9 = r9;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:932:0x103b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x103c, code lost:
        r35 = r3;
        r8 = r5;
        r13 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x103f, code lost:
        r64 = r53;
        r9 = r60;
        r48 = r61;
        r2 = r0;
        r14 = r10;
        r1 = r20;
        r13 = r13;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:0x1098, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x1099, code lost:
        r8 = r5;
        r35 = r29;
        r13 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:0x10c8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:943:0x10c9, code lost:
        r8 = r72;
        r13 = r73;
        r35 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:946:0x10f8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x10f9, code lost:
        r2 = r0;
        r60 = r9;
        r14 = r10;
        r1 = r20;
        r6 = r35;
        r61 = r48;
        r13 = r13;
        r9 = r9;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x1105, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:0x1106, code lost:
        r8 = r72;
        r13 = r73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x110b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x110c, code lost:
        r8 = r72;
        r13 = r73;
        r10 = r75;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x1112, code lost:
        r35 = r6;
        r50 = r15;
        r13 = r13;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x1116, code lost:
        r64 = r53;
        r9 = r60;
        r48 = r61;
        r2 = r0;
        r14 = r10;
        r1 = r20;
        r13 = r13;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:0x1125, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x1126, code lost:
        r8 = r72;
        r13 = r73;
        r35 = r6;
        r64 = r53;
        r2 = r0;
        r60 = r9;
        r14 = r75;
        r1 = r20;
        r61 = r5;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:0x113f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:957:0x1140, code lost:
        r10 = r75;
        r35 = r6;
        r32 = null;
        r13 = r14;
        r8 = r15;
        r64 = r53;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:0x114c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x114d, code lost:
        r10 = r75;
        r35 = r6;
        r32 = null;
        r13 = r14;
        r8 = r15;
        r64 = r53;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x1157, code lost:
        r2 = r0;
        r60 = r9;
        r14 = r10;
        r1 = r20;
        r61 = r32;
        r9 = r9;
        r13 = r13;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:0x1163, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:962:0x1164, code lost:
        r13 = r14;
        r8 = r15;
        r35 = r49;
        r64 = r53;
        r2 = r0;
        r60 = r9;
        r14 = r75;
        r1 = r20;
        r61 = null;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:0x117a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:964:0x117b, code lost:
        r13 = r14;
        r8 = r15;
        r35 = r49;
        r64 = r53;
        r2 = r0;
        r14 = r75;
        r1 = r20;
        r60 = null;
        r61 = null;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:965:0x1190, code lost:
        r6 = r35;
        r13 = r13;
        r9 = r9;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:966:0x1194, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:967:0x1195, code lost:
        r64 = r6;
        r13 = r14;
        r8 = r15;
        r50 = r36;
        r2 = r0;
        r14 = r7;
        r1 = r20;
        r60 = null;
        r61 = null;
        r6 = r4;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:968:0x11ae, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:969:0x11af, code lost:
        r64 = r6;
        r13 = r14;
        r8 = r15;
        r50 = r36;
        r2 = r0;
        r14 = r7;
        r1 = r20;
        r6 = null;
        r60 = null;
        r61 = null;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:0x11c5, code lost:
        r53 = r64;
        r13 = r13;
        r9 = r9;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:971:0x11c9, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:972:0x11ca, code lost:
        r10 = r7;
        r13 = r14;
        r8 = r15;
        r50 = r36;
        r32 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:0x11d3, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:974:0x11d4, code lost:
        r32 = null;
        r10 = r7;
        r13 = r14;
        r8 = r15;
        r50 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:975:0x11dc, code lost:
        r2 = r0;
        r14 = r10;
        r1 = r20;
        r6 = r32;
        r53 = r6;
        r9 = r9;
        r13 = r13;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:976:0x11e7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:977:0x11e8, code lost:
        r13 = r14;
        r8 = r15;
        r50 = r36;
        r32 = null;
        r2 = r0;
        r1 = r20;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:978:0x11f5, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:979:0x11f6, code lost:
        r13 = r14;
        r8 = r15;
        r50 = r36;
        r32 = null;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:980:0x1200, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:981:0x1201, code lost:
        r13 = r14;
        r8 = r15;
        r50 = r36;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:982:0x1206, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:983:0x1207, code lost:
        r50 = r4;
        r13 = r14;
        r8 = r15;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:984:0x120b, code lost:
        r32 = null;
        r1 = r75;
        r13 = r13;
        r9 = r9;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:985:0x1212, code lost:
        r2 = r0;
        r13 = r13;
        r9 = r9;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:986:0x1213, code lost:
        r6 = r32;
        r14 = r6;
        r53 = r14;
        r13 = r13;
        r9 = r9;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:987:0x1218, code lost:
        r60 = r53;
        r61 = r60;
        r13 = r13;
        r9 = r9;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:992:0x1222, code lost:
        r4 = true;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:177:0x031e, B:262:0x04df] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:177:0x031e, B:266:0x04f4] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1003:0x1265 A[Catch:{ Exception -> 0x1299, all -> 0x1294 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1011:0x127d A[Catch:{ Exception -> 0x1299, all -> 0x1294 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1013:0x1290 A[Catch:{ Exception -> 0x1299, all -> 0x1294 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1014:0x1294 A[Catch:{ Exception -> 0x1299, all -> 0x1294 }, ExcHandler: all (r0v13 'th' java.lang.Throwable A[CUSTOM_DECLARE, Catch:{ Exception -> 0x1299, all -> 0x1294 }]), Splitter:B:342:0x063c] */
    /* JADX WARNING: Removed duplicated region for block: B:1020:0x12a0 A[Catch:{ Exception -> 0x1299, all -> 0x1294 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1022:0x12a5 A[Catch:{ Exception -> 0x1299, all -> 0x1294 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1024:0x12ad A[Catch:{ Exception -> 0x1299, all -> 0x1294 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1029:0x12b9  */
    /* JADX WARNING: Removed duplicated region for block: B:1032:0x12c0 A[SYNTHETIC, Splitter:B:1032:0x12c0] */
    /* JADX WARNING: Removed duplicated region for block: B:1037:0x12cf A[ExcHandler: all (r0v8 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r6 
      PHI: (r6v2 org.telegram.messenger.video.MediaCodecVideoConvertor) = (r6v5 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v5 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v5 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v79 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v80 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v80 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v80 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v80 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v80 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v80 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v101 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v101 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v101 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v106 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v106 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v106 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v106 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v106 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v106 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v106 org.telegram.messenger.video.MediaCodecVideoConvertor), (r6v110 org.telegram.messenger.video.MediaCodecVideoConvertor) binds: [B:262:0x04df, B:274:0x050b, B:266:0x04f4, B:242:0x0496, B:229:0x0453, B:230:?, B:235:0x045c, B:236:?, B:238:0x0470, B:239:?, B:188:0x0355, B:189:?, B:191:0x036c, B:101:0x01e1, B:102:?, B:177:0x031e, B:114:0x01fc, B:108:0x01ef, B:105:0x01e9, B:106:?, B:86:0x018e] A[DONT_GENERATE, DONT_INLINE], Splitter:B:177:0x031e] */
    /* JADX WARNING: Removed duplicated region for block: B:1053:0x132b  */
    /* JADX WARNING: Removed duplicated region for block: B:1056:0x1332 A[SYNTHETIC, Splitter:B:1056:0x1332] */
    /* JADX WARNING: Removed duplicated region for block: B:1062:0x1342  */
    /* JADX WARNING: Removed duplicated region for block: B:1064:0x1366 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:1069:0x136f  */
    /* JADX WARNING: Removed duplicated region for block: B:1072:0x1376 A[SYNTHETIC, Splitter:B:1072:0x1376] */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x0457 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x0496 A[SYNTHETIC, Splitter:B:242:0x0496] */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x04a0 A[Catch:{ Exception -> 0x049a, all -> 0x12cf }] */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x04a5 A[Catch:{ Exception -> 0x049a, all -> 0x12cf }] */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x056d  */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x05db A[Catch:{ Exception -> 0x061a, all -> 0x1294 }] */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x060a A[Catch:{ Exception -> 0x061a, all -> 0x1294 }] */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x06c2 A[SYNTHETIC, Splitter:B:375:0x06c2] */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x06cb A[SYNTHETIC, Splitter:B:378:0x06cb] */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x06d7  */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x06db  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x06fe A[SYNTHETIC, Splitter:B:390:0x06fe] */
    /* JADX WARNING: Removed duplicated region for block: B:412:0x0729  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x074c A[SYNTHETIC, Splitter:B:426:0x074c] */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x077e  */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x0790 A[SYNTHETIC, Splitter:B:443:0x0790] */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x07e9  */
    /* JADX WARNING: Removed duplicated region for block: B:469:0x0819 A[SYNTHETIC, Splitter:B:469:0x0819] */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x0845  */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x0850 A[SYNTHETIC, Splitter:B:480:0x0850] */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x088d  */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x0890  */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x094e  */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x0978  */
    /* JADX WARNING: Removed duplicated region for block: B:554:0x098a  */
    /* JADX WARNING: Removed duplicated region for block: B:555:0x098c  */
    /* JADX WARNING: Removed duplicated region for block: B:559:0x09a2 A[Catch:{ Exception -> 0x1125, all -> 0x1294 }] */
    /* JADX WARNING: Removed duplicated region for block: B:573:0x09db A[SYNTHETIC, Splitter:B:573:0x09db] */
    /* JADX WARNING: Removed duplicated region for block: B:643:0x0b17  */
    /* JADX WARNING: Removed duplicated region for block: B:646:0x0b30 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0b4d  */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0b52  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0b5d  */
    /* JADX WARNING: Removed duplicated region for block: B:659:0x0b83  */
    /* JADX WARNING: Removed duplicated region for block: B:809:0x0dc4  */
    /* JADX WARNING: Removed duplicated region for block: B:810:0x0de6  */
    /* JADX WARNING: Removed duplicated region for block: B:911:0x0fc1 A[Catch:{ Exception -> 0x0ff5, all -> 0x1294 }] */
    /* JADX WARNING: Removed duplicated region for block: B:921:0x0ff0  */
    /* JADX WARNING: Removed duplicated region for block: B:991:0x1220 A[ADDED_TO_REGION] */
    /* JADX WARNING: Unknown variable types count: 2 */
    @android.annotation.TargetApi(18)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideoInternal(java.lang.String r68, java.io.File r69, int r70, boolean r71, int r72, int r73, int r74, int r75, long r76, long r78, long r80, boolean r82, boolean r83, org.telegram.messenger.MediaController.SavedFilterState r84, java.lang.String r85, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r86, boolean r87) {
        /*
            r67 = this;
            r14 = r67
            r13 = r68
            r15 = r70
            r12 = r72
            r11 = r73
            r10 = r74
            r9 = r75
            r7 = r76
            r5 = r78
            android.media.MediaCodec$BufferInfo r2 = new android.media.MediaCodec$BufferInfo     // Catch:{ Exception -> 0x12ec, all -> 0x12e7 }
            r2.<init>()     // Catch:{ Exception -> 0x12ec, all -> 0x12e7 }
            org.telegram.messenger.video.Mp4Movie r1 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ Exception -> 0x12ec, all -> 0x12e7 }
            r1.<init>()     // Catch:{ Exception -> 0x12ec, all -> 0x12e7 }
            r3 = r69
            r1.setCacheFile(r3)     // Catch:{ Exception -> 0x12ec, all -> 0x12e7 }
            r1.setRotation(r15)     // Catch:{ Exception -> 0x12ec, all -> 0x12e7 }
            r1.setSize(r12, r11)     // Catch:{ Exception -> 0x12ec, all -> 0x12e7 }
            org.telegram.messenger.video.MP4Builder r4 = new org.telegram.messenger.video.MP4Builder     // Catch:{ Exception -> 0x12ec, all -> 0x12e7 }
            r4.<init>()     // Catch:{ Exception -> 0x12ec, all -> 0x12e7 }
            r15 = r71
            org.telegram.messenger.video.MP4Builder r1 = r4.createMovie(r1, r15)     // Catch:{ Exception -> 0x12ec, all -> 0x12e7 }
            r14.mediaMuxer = r1     // Catch:{ Exception -> 0x12ec, all -> 0x12e7 }
            r13 = r80
            float r1 = (float) r13
            r18 = 1148846080(0x447a0000, float:1000.0)
            float r19 = r1 / r18
            r67.checkConversionCanceled()     // Catch:{ Exception -> 0x12dd }
            java.lang.String r1 = "csd-1"
            java.lang.String r13 = "csd-0"
            java.lang.String r14 = "prepend-sps-pps-to-idr-frames"
            r4 = 921600(0xe1000, float:1.291437E-39)
            r21 = r13
            r22 = r14
            r23 = 4
            java.lang.String r13 = "video/avc"
            r14 = 0
            if (r87 == 0) goto L_0x04d2
            r15 = 2130708361(0x7var_, float:1.701803E38)
            int r29 = r12 * r11
            r25 = 3
            int r29 = r29 * 3
            r26 = 2
            int r29 = r29 / 2
            if (r9 > 0) goto L_0x0064
            r9 = 921600(0xe1000, float:1.291437E-39)
        L_0x0064:
            int r4 = r12 % 2
            if (r4 == 0) goto L_0x006b
            int r4 = r12 + 1
            r12 = r4
        L_0x006b:
            int r4 = r11 % 2
            if (r4 == 0) goto L_0x0072
            int r4 = r11 + 1
            r11 = r4
        L_0x0072:
            android.media.MediaFormat r4 = android.media.MediaFormat.createVideoFormat(r13, r12, r11)     // Catch:{ Exception -> 0x0444 }
            java.lang.String r3 = "color-format"
            r4.setInteger(r3, r15)     // Catch:{ Exception -> 0x0444 }
            java.lang.String r3 = "bitrate"
            r4.setInteger(r3, r9)     // Catch:{ Exception -> 0x0444 }
            java.lang.String r3 = "frame-rate"
            r4.setInteger(r3, r10)     // Catch:{ Exception -> 0x0444 }
            java.lang.String r3 = "i-frame-interval"
            r15 = 2
            r4.setInteger(r3, r15)     // Catch:{ Exception -> 0x0444 }
            android.media.MediaCodec r15 = android.media.MediaCodec.createEncoderByType(r13)     // Catch:{ Exception -> 0x0444 }
            r3 = 1
            r15.configure(r4, r14, r14, r3)     // Catch:{ Exception -> 0x043c }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x043c }
            r3 = 18
            if (r4 < r3) goto L_0x00bb
            org.telegram.messenger.video.InputSurface r3 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x00b0 }
            android.view.Surface r4 = r15.createInputSurface()     // Catch:{ Exception -> 0x00b0 }
            r3.<init>(r4)     // Catch:{ Exception -> 0x00b0 }
            r3.makeCurrent()     // Catch:{ Exception -> 0x00a7 }
            r4 = r3
            goto L_0x00bc
        L_0x00a7:
            r0 = move-exception
            r6 = r67
            r1 = r0
            r36 = r3
            r33 = r14
            goto L_0x00b8
        L_0x00b0:
            r0 = move-exception
            r6 = r67
            r1 = r0
            r33 = r14
            r36 = r33
        L_0x00b8:
            r14 = r15
            goto L_0x0453
        L_0x00bb:
            r4 = r14
        L_0x00bc:
            r15.start()     // Catch:{ Exception -> 0x0430 }
            org.telegram.messenger.video.OutputSurface r3 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x0430 }
            float r14 = (float) r10
            r24 = 1
            r31 = r1
            r1 = r3
            r32 = r2
            r2 = r84
            r33 = r3
            r3 = r68
            r36 = r4
            r4 = r85
            r5 = r86
            r6 = r12
            r7 = r11
            r8 = r14
            r14 = r9
            r9 = r24
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0425 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0417 }
            r2 = 21
            if (r1 >= r2) goto L_0x00ef
            java.nio.ByteBuffer[] r1 = r15.getOutputBuffers()     // Catch:{ Exception -> 0x00e9 }
            goto L_0x00f0
        L_0x00e9:
            r0 = move-exception
            r6 = r67
            r1 = r0
            r9 = r14
            goto L_0x00b8
        L_0x00ef:
            r1 = 0
        L_0x00f0:
            r67.checkConversionCanceled()     // Catch:{ Exception -> 0x0417 }
            r2 = 0
            r3 = 1
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = -5
            r8 = 0
        L_0x00fb:
            if (r4 != 0) goto L_0x0404
            r67.checkConversionCanceled()     // Catch:{ Exception -> 0x0417 }
            r16 = r2 ^ 1
            r66 = r6
            r6 = r4
            r4 = r16
            r16 = r8
            r8 = r66
            r9 = r7
            r7 = r5
            r5 = 1
        L_0x010e:
            if (r4 != 0) goto L_0x011a
            if (r5 == 0) goto L_0x0113
            goto L_0x011a
        L_0x0113:
            r4 = r6
            r5 = r7
            r6 = r8
            r7 = r9
            r8 = r16
            goto L_0x00fb
        L_0x011a:
            r67.checkConversionCanceled()     // Catch:{ Exception -> 0x0417 }
            if (r83 == 0) goto L_0x012c
            r39 = 22000(0x55f0, double:1.08694E-319)
            r72 = r4
            r73 = r5
            r75 = r14
            r14 = r32
            r4 = r39
            goto L_0x0136
        L_0x012c:
            r72 = r4
            r73 = r5
            r75 = r14
            r14 = r32
            r4 = 2500(0x9c4, double:1.235E-320)
        L_0x0136:
            int r4 = r15.dequeueOutputBuffer(r14, r4)     // Catch:{ Exception -> 0x0400 }
            r5 = -1
            if (r4 != r5) goto L_0x0153
            r20 = r8
            r5 = r21
            r29 = r22
            r10 = r31
            r8 = -1
            r37 = 0
            r22 = r1
            r21 = r2
            r1 = r4
            r4 = r6
            r2 = 0
            r6 = r67
            goto L_0x0336
        L_0x0153:
            r5 = -3
            if (r4 != r5) goto L_0x017e
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0179 }
            r20 = r6
            r6 = 21
            if (r5 >= r6) goto L_0x0162
            java.nio.ByteBuffer[] r1 = r15.getOutputBuffers()     // Catch:{ Exception -> 0x0179 }
        L_0x0162:
            r6 = r67
            r37 = r73
            r5 = r21
            r29 = r22
            r10 = r31
        L_0x016c:
            r22 = r1
            r21 = r2
            r1 = r4
            r4 = r20
            r2 = 0
            r20 = r8
        L_0x0176:
            r8 = -1
            goto L_0x0336
        L_0x0179:
            r0 = move-exception
            r6 = r67
            goto L_0x0420
        L_0x017e:
            r20 = r6
            r5 = -2
            if (r4 != r5) goto L_0x01d5
            android.media.MediaFormat r5 = r15.getOutputFormat()     // Catch:{ Exception -> 0x0179 }
            r6 = -5
            if (r9 != r6) goto L_0x01ca
            if (r5 == 0) goto L_0x01ca
            r6 = r67
            org.telegram.messenger.video.MP4Builder r9 = r6.mediaMuxer     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r10 = 0
            int r9 = r9.addTrack(r5, r10)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r10 = r22
            boolean r22 = r5.containsKey(r10)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            if (r22 == 0) goto L_0x01be
            r22 = r9
            int r9 = r5.getInteger(r10)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r29 = r10
            r10 = 1
            if (r9 != r10) goto L_0x01c2
            r9 = r21
            java.nio.ByteBuffer r7 = r5.getByteBuffer(r9)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r10 = r31
            java.nio.ByteBuffer r5 = r5.getByteBuffer(r10)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r7 = r7.limit()     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r5 = r5.limit()     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r7 = r7 + r5
            goto L_0x01c6
        L_0x01be:
            r22 = r9
            r29 = r10
        L_0x01c2:
            r9 = r21
            r10 = r31
        L_0x01c6:
            r5 = r9
            r9 = r22
            goto L_0x01d2
        L_0x01ca:
            r6 = r67
            r5 = r21
            r29 = r22
            r10 = r31
        L_0x01d2:
            r37 = r73
            goto L_0x016c
        L_0x01d5:
            r6 = r67
            r5 = r21
            r29 = r22
            r10 = r31
            if (r4 < 0) goto L_0x03da
            r20 = r8
            int r8 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x03d8, all -> 0x12cf }
            r21 = r2
            r2 = 21
            if (r8 >= r2) goto L_0x01ef
            r2 = r1[r4]     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            goto L_0x01f3
        L_0x01ec:
            r0 = move-exception
            goto L_0x0420
        L_0x01ef:
            java.nio.ByteBuffer r2 = r15.getOutputBuffer(r4)     // Catch:{ Exception -> 0x03d8, all -> 0x12cf }
        L_0x01f3:
            if (r2 == 0) goto L_0x03b7
            int r8 = r14.size     // Catch:{ Exception -> 0x03d8, all -> 0x12cf }
            r22 = r1
            r1 = 1
            if (r8 <= r1) goto L_0x031a
            int r8 = r14.flags     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r24 = 2
            r8 = r8 & 2
            if (r8 != 0) goto L_0x02a6
            if (r7 == 0) goto L_0x0215
            int r8 = r14.flags     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r8 = r8 & r1
            if (r8 == 0) goto L_0x0215
            int r1 = r14.offset     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r1 = r1 + r7
            r14.offset = r1     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r1 = r14.size     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r1 = r1 - r7
            r14.size = r1     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
        L_0x0215:
            if (r3 == 0) goto L_0x0269
            int r1 = r14.flags     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r8 = 1
            r1 = r1 & r8
            if (r1 == 0) goto L_0x0269
            int r1 = r14.size     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r8 = 100
            if (r1 <= r8) goto L_0x0268
            int r1 = r14.offset     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r2.position(r1)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            byte[] r1 = new byte[r8]     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r2.get(r1)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r3 = 0
            r24 = 0
        L_0x0230:
            r8 = 96
            if (r3 >= r8) goto L_0x0268
            byte r8 = r1[r3]     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            if (r8 != 0) goto L_0x025f
            int r8 = r3 + 1
            byte r8 = r1[r8]     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            if (r8 != 0) goto L_0x025f
            int r8 = r3 + 2
            byte r8 = r1[r8]     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            if (r8 != 0) goto L_0x025f
            int r8 = r3 + 3
            byte r8 = r1[r8]     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r31 = r1
            r1 = 1
            if (r8 != r1) goto L_0x0261
            int r8 = r24 + 1
            if (r8 <= r1) goto L_0x025c
            int r1 = r14.offset     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r1 = r1 + r3
            r14.offset = r1     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r1 = r14.size     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r1 = r1 - r3
            r14.size = r1     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            goto L_0x0268
        L_0x025c:
            r24 = r8
            goto L_0x0261
        L_0x025f:
            r31 = r1
        L_0x0261:
            int r3 = r3 + 1
            r1 = r31
            r8 = 100
            goto L_0x0230
        L_0x0268:
            r3 = 0
        L_0x0269:
            org.telegram.messenger.video.MP4Builder r1 = r6.mediaMuxer     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r8 = 1
            long r1 = r1.writeSampleData(r9, r2, r14, r8)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r27 = 0
            int r8 = (r1 > r27 ? 1 : (r1 == r27 ? 0 : -1))
            if (r8 == 0) goto L_0x029c
            org.telegram.messenger.MediaController$VideoConvertorListener r8 = r6.callback     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            if (r8 == 0) goto L_0x029c
            r24 = r7
            long r7 = r14.presentationTimeUs     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r32 = r3
            r31 = r4
            r3 = r76
            long r7 = r7 - r3
            int r39 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1))
            if (r39 <= 0) goto L_0x028d
            long r7 = r14.presentationTimeUs     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            long r16 = r7 - r3
        L_0x028d:
            r7 = r16
            org.telegram.messenger.MediaController$VideoConvertorListener r3 = r6.callback     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            float r4 = (float) r7     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            float r4 = r4 / r18
            float r4 = r4 / r19
            r3.didWriteData(r1, r4)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r16 = r7
            goto L_0x02a2
        L_0x029c:
            r32 = r3
            r31 = r4
            r24 = r7
        L_0x02a2:
            r3 = r32
            goto L_0x031e
        L_0x02a6:
            r31 = r4
            r24 = r7
            r1 = -5
            if (r9 != r1) goto L_0x031e
            int r1 = r14.size     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            byte[] r1 = new byte[r1]     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r4 = r14.offset     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r7 = r14.size     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r4 = r4 + r7
            r2.limit(r4)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r4 = r14.offset     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r2.position(r4)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r2.get(r1)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r2 = r14.size     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r4 = 1
            int r2 = r2 - r4
        L_0x02c5:
            if (r2 < 0) goto L_0x0302
            r7 = 3
            if (r2 <= r7) goto L_0x0302
            byte r7 = r1[r2]     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            if (r7 != r4) goto L_0x02fe
            int r4 = r2 + -1
            byte r4 = r1[r4]     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            if (r4 != 0) goto L_0x02fe
            int r4 = r2 + -2
            byte r4 = r1[r4]     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            if (r4 != 0) goto L_0x02fe
            int r4 = r2 + -3
            byte r7 = r1[r4]     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            if (r7 != 0) goto L_0x02fe
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r4)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r7 = r14.size     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r7 = r7 - r4
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.allocate(r7)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r8 = 0
            java.nio.ByteBuffer r9 = r2.put(r1, r8, r4)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r9.position(r8)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r9 = r14.size     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            int r9 = r9 - r4
            java.nio.ByteBuffer r1 = r7.put(r1, r4, r9)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r1.position(r8)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            goto L_0x0304
        L_0x02fe:
            int r2 = r2 + -1
            r4 = 1
            goto L_0x02c5
        L_0x0302:
            r2 = 0
            r7 = 0
        L_0x0304:
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r13, r12, r11)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            if (r2 == 0) goto L_0x0312
            if (r7 == 0) goto L_0x0312
            r1.setByteBuffer(r5, r2)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r1.setByteBuffer(r10, r7)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
        L_0x0312:
            org.telegram.messenger.video.MP4Builder r2 = r6.mediaMuxer     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            r4 = 0
            int r9 = r2.addTrack(r1, r4)     // Catch:{ Exception -> 0x01ec, all -> 0x12cf }
            goto L_0x031e
        L_0x031a:
            r31 = r4
            r24 = r7
        L_0x031e:
            int r1 = r14.flags     // Catch:{ Exception -> 0x03d8, all -> 0x12cf }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x0329
            r1 = r31
            r2 = 0
            r4 = 1
            goto L_0x032d
        L_0x0329:
            r1 = r31
            r2 = 0
            r4 = 0
        L_0x032d:
            r15.releaseOutputBuffer(r1, r2)     // Catch:{ Exception -> 0x03d8, all -> 0x12cf }
            r37 = r73
            r7 = r24
            goto L_0x0176
        L_0x0336:
            if (r1 == r8) goto L_0x0351
            r6 = r4
            r31 = r10
            r32 = r14
            r8 = r20
            r2 = r21
            r1 = r22
            r22 = r29
            r4 = r72
            r10 = r74
            r14 = r75
            r21 = r5
            r5 = r37
            goto L_0x010e
        L_0x0351:
            if (r21 != 0) goto L_0x038c
            r8 = r33
            r8.drawImage(r2)     // Catch:{ Exception -> 0x0384, all -> 0x12cf }
            r1 = r20
            float r2 = (float) r1
            r24 = 1106247680(0x41var_, float:30.0)
            float r2 = r2 / r24
            float r2 = r2 * r18
            float r2 = r2 * r18
            float r2 = r2 * r18
            r73 = r3
            long r2 = (long) r2
            r24 = r4
            r4 = r36
            r4.setPresentationTime(r2)     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            r4.swapBuffers()     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            int r1 = r1 + 1
            float r2 = (float) r1     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            r3 = 1106247680(0x41var_, float:30.0)
            float r3 = r3 * r19
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 < 0) goto L_0x0396
            r15.signalEndOfInputStream()     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            r3 = r1
            r1 = 0
            r2 = 1
            goto L_0x039b
        L_0x0384:
            r0 = move-exception
            r4 = r36
            r9 = r75
            r1 = r0
            goto L_0x03fc
        L_0x038c:
            r73 = r3
            r24 = r4
            r1 = r20
            r8 = r33
            r4 = r36
        L_0x0396:
            r3 = r1
            r2 = r21
            r1 = r72
        L_0x039b:
            r36 = r4
            r21 = r5
            r33 = r8
            r31 = r10
            r32 = r14
            r6 = r24
            r5 = r37
            r10 = r74
            r14 = r75
            r4 = r1
            r8 = r3
            r1 = r22
            r22 = r29
            r3 = r73
            goto L_0x010e
        L_0x03b7:
            r1 = r4
            r8 = r33
            r4 = r36
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            r3.<init>()     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            java.lang.String r5 = "encoderOutputBuffer "
            r3.append(r5)     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            r3.append(r1)     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            java.lang.String r1 = " was null"
            r3.append(r1)     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            java.lang.String r1 = r3.toString()     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            r2.<init>(r1)     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            throw r2     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
        L_0x03d8:
            r0 = move-exception
            goto L_0x041c
        L_0x03da:
            r1 = r4
            r8 = r33
            r4 = r36
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            r3.<init>()     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            java.lang.String r5 = "unexpected result from encoder.dequeueOutputBuffer: "
            r3.append(r5)     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            r3.append(r1)     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            java.lang.String r1 = r3.toString()     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            r2.<init>(r1)     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
            throw r2     // Catch:{ Exception -> 0x03f6, all -> 0x12cf }
        L_0x03f6:
            r0 = move-exception
            r9 = r75
            r1 = r0
            r36 = r4
        L_0x03fc:
            r33 = r8
            goto L_0x00b8
        L_0x0400:
            r0 = move-exception
            r6 = r67
            goto L_0x041c
        L_0x0404:
            r6 = r67
            r75 = r14
            r8 = r33
            r4 = r36
            r9 = r75
            r3 = r8
            r14 = r15
            r1 = 0
            r20 = 0
            r15 = r74
            goto L_0x0494
        L_0x0417:
            r0 = move-exception
            r6 = r67
            r75 = r14
        L_0x041c:
            r8 = r33
            r4 = r36
        L_0x0420:
            r9 = r75
            r1 = r0
            goto L_0x00b8
        L_0x0425:
            r0 = move-exception
            r6 = r67
            r75 = r14
            r4 = r36
            r9 = r75
            r1 = r0
            goto L_0x0438
        L_0x0430:
            r0 = move-exception
            r6 = r67
            r75 = r9
            r1 = r0
            r36 = r4
        L_0x0438:
            r14 = r15
            r33 = 0
            goto L_0x0453
        L_0x043c:
            r0 = move-exception
            r6 = r67
            r75 = r9
            r1 = r0
            r14 = r15
            goto L_0x044f
        L_0x0444:
            r0 = move-exception
            r6 = r67
            r75 = r9
            goto L_0x044d
        L_0x044a:
            r0 = move-exception
            r6 = r67
        L_0x044d:
            r1 = r0
            r14 = 0
        L_0x044f:
            r33 = 0
            r36 = 0
        L_0x0453:
            boolean r2 = r1 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x04c4, all -> 0x12cf }
            if (r2 == 0) goto L_0x045b
            if (r83 != 0) goto L_0x045b
            r4 = 1
            goto L_0x045c
        L_0x045b:
            r4 = 0
        L_0x045c:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04be, all -> 0x12cf }
            r2.<init>()     // Catch:{ Exception -> 0x04be, all -> 0x12cf }
            java.lang.String r3 = "bitrate: "
            r2.append(r3)     // Catch:{ Exception -> 0x04be, all -> 0x12cf }
            r2.append(r9)     // Catch:{ Exception -> 0x04be, all -> 0x12cf }
            java.lang.String r3 = " framerate: "
            r2.append(r3)     // Catch:{ Exception -> 0x04be, all -> 0x12cf }
            r15 = r74
            r2.append(r15)     // Catch:{ Exception -> 0x04b7, all -> 0x12cf }
            java.lang.String r3 = " size: "
            r2.append(r3)     // Catch:{ Exception -> 0x04b7, all -> 0x12cf }
            r2.append(r11)     // Catch:{ Exception -> 0x04b7, all -> 0x12cf }
            java.lang.String r3 = "x"
            r2.append(r3)     // Catch:{ Exception -> 0x04b7, all -> 0x12cf }
            r2.append(r12)     // Catch:{ Exception -> 0x04b7, all -> 0x12cf }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x04b7, all -> 0x12cf }
            org.telegram.messenger.FileLog.e((java.lang.String) r2)     // Catch:{ Exception -> 0x04b7, all -> 0x12cf }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x04b7, all -> 0x12cf }
            r1 = r4
            r3 = r33
            r4 = r36
            r20 = 1
        L_0x0494:
            if (r3 == 0) goto L_0x049e
            r3.release()     // Catch:{ Exception -> 0x049a, all -> 0x12cf }
            goto L_0x049e
        L_0x049a:
            r0 = move-exception
            r2 = r0
            r4 = r1
            goto L_0x04b9
        L_0x049e:
            if (r4 == 0) goto L_0x04a3
            r4.release()     // Catch:{ Exception -> 0x049a, all -> 0x12cf }
        L_0x04a3:
            if (r14 == 0) goto L_0x04ab
            r14.stop()     // Catch:{ Exception -> 0x049a, all -> 0x12cf }
            r14.release()     // Catch:{ Exception -> 0x049a, all -> 0x12cf }
        L_0x04ab:
            r67.checkConversionCanceled()     // Catch:{ Exception -> 0x049a, all -> 0x12cf }
            r2 = r1
            r1 = r9
            r9 = r15
            r4 = r20
            r15 = r12
            r12 = r6
            goto L_0x12b5
        L_0x04b7:
            r0 = move-exception
            r2 = r0
        L_0x04b9:
            r1 = r9
            r13 = r11
            r8 = r12
            r9 = r15
            goto L_0x0503
        L_0x04be:
            r0 = move-exception
            r2 = r0
            r1 = r9
            r13 = r11
            r8 = r12
            goto L_0x04cb
        L_0x04c4:
            r0 = move-exception
            r7 = 0
            r2 = r0
            r1 = r9
            r13 = r11
            r8 = r12
            r4 = 0
        L_0x04cb:
            r37 = 1
            r9 = r74
        L_0x04cf:
            r12 = r6
            goto L_0x12f8
        L_0x04d2:
            r7 = 0
            r6 = r67
            r14 = r2
            r15 = r10
            r5 = r21
            r29 = r22
            r10 = r1
            r1 = 921600(0xe1000, float:1.291437E-39)
            android.media.MediaExtractor r2 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x12d4, all -> 0x12cf }
            r2.<init>()     // Catch:{ Exception -> 0x12d4, all -> 0x12cf }
            r6.extractor = r2     // Catch:{ Exception -> 0x12d4, all -> 0x12cf }
            r8 = r68
            r2.setDataSource(r8)     // Catch:{ Exception -> 0x12d4, all -> 0x12cf }
            android.media.MediaExtractor r2 = r6.extractor     // Catch:{ Exception -> 0x12d4, all -> 0x12cf }
            int r4 = org.telegram.messenger.MediaController.findTrack(r2, r7)     // Catch:{ Exception -> 0x12d4, all -> 0x12cf }
            r2 = -1
            if (r9 == r2) goto L_0x0506
            android.media.MediaExtractor r2 = r6.extractor     // Catch:{ Exception -> 0x04fc, all -> 0x12cf }
            r3 = 1
            int r2 = org.telegram.messenger.MediaController.findTrack(r2, r3)     // Catch:{ Exception -> 0x04fc, all -> 0x12cf }
            goto L_0x0507
        L_0x04fc:
            r0 = move-exception
            r2 = r0
            r1 = r9
            r13 = r11
            r8 = r12
            r9 = r15
            r4 = 0
        L_0x0503:
            r37 = 1
            goto L_0x04cf
        L_0x0506:
            r2 = -1
        L_0x0507:
            java.lang.String r3 = "mime"
            if (r4 < 0) goto L_0x051d
            android.media.MediaExtractor r7 = r6.extractor     // Catch:{ Exception -> 0x04fc, all -> 0x12cf }
            android.media.MediaFormat r7 = r7.getTrackFormat(r4)     // Catch:{ Exception -> 0x04fc, all -> 0x12cf }
            java.lang.String r7 = r7.getString(r3)     // Catch:{ Exception -> 0x04fc, all -> 0x12cf }
            boolean r7 = r7.equals(r13)     // Catch:{ Exception -> 0x04fc, all -> 0x12cf }
            if (r7 != 0) goto L_0x051d
            r7 = 1
            goto L_0x051e
        L_0x051d:
            r7 = 0
        L_0x051e:
            if (r82 != 0) goto L_0x0563
            if (r7 == 0) goto L_0x0523
            goto L_0x0563
        L_0x0523:
            android.media.MediaExtractor r2 = r6.extractor     // Catch:{ Exception -> 0x0552 }
            org.telegram.messenger.video.MP4Builder r3 = r6.mediaMuxer     // Catch:{ Exception -> 0x0552 }
            r7 = -1
            if (r9 == r7) goto L_0x052c
            r13 = 1
            goto L_0x052d
        L_0x052c:
            r13 = 0
        L_0x052d:
            r1 = r67
            r4 = r76
            r10 = 1
            r4 = r14
            r14 = r6
            r5 = r76
            r15 = r8
            r14 = 0
            r7 = r78
            r14 = 1
            r9 = r80
            r14 = r11
            r11 = r69
            r15 = r12
            r12 = r13
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ Exception -> 0x0550 }
            r12 = r67
            r9 = r74
            r1 = r75
            r11 = r14
            r2 = 0
            r4 = 0
            goto L_0x12b5
        L_0x0550:
            r0 = move-exception
            goto L_0x0555
        L_0x0552:
            r0 = move-exception
            r14 = r11
            r15 = r12
        L_0x0555:
            r4 = 0
            r37 = 1
            r12 = r67
            r9 = r74
            r1 = r75
            r2 = r0
            r13 = r14
            r8 = r15
            goto L_0x12f8
        L_0x0563:
            r15 = r12
            r7 = -1
            r12 = r6
            r66 = r14
            r14 = r11
            r11 = r66
            if (r4 < 0) goto L_0x127d
            r16 = -1
            java.lang.String r6 = android.os.Build.MANUFACTURER     // Catch:{ Exception -> 0x1206, all -> 0x1294 }
            java.lang.String r6 = r6.toLowerCase()     // Catch:{ Exception -> 0x1206, all -> 0x1294 }
            int r8 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1206, all -> 0x1294 }
            r1 = 18
            if (r8 >= r1) goto L_0x062f
            android.media.MediaCodecInfo r1 = org.telegram.messenger.MediaController.selectCodec(r13)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            int r8 = org.telegram.messenger.MediaController.selectColorFormat(r1, r13)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            if (r8 == 0) goto L_0x0612
            java.lang.String r9 = r1.getName()     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            java.lang.String r7 = "OMX.qcom."
            boolean r7 = r9.contains(r7)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            if (r7 == 0) goto L_0x05ad
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            r9 = 16
            if (r7 != r9) goto L_0x05aa
            java.lang.String r7 = "lge"
            boolean r7 = r6.equals(r7)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            if (r7 != 0) goto L_0x05a7
            java.lang.String r7 = "nokia"
            boolean r7 = r6.equals(r7)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            if (r7 == 0) goto L_0x05aa
        L_0x05a7:
            r7 = 1
        L_0x05a8:
            r9 = 1
            goto L_0x05d7
        L_0x05aa:
            r7 = 1
        L_0x05ab:
            r9 = 0
            goto L_0x05d7
        L_0x05ad:
            java.lang.String r7 = "OMX.Intel."
            boolean r7 = r9.contains(r7)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            if (r7 == 0) goto L_0x05b7
            r7 = 2
            goto L_0x05ab
        L_0x05b7:
            java.lang.String r7 = "OMX.MTK.VIDEO.ENCODER.AVC"
            boolean r7 = r9.equals(r7)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            if (r7 == 0) goto L_0x05c1
            r7 = 3
            goto L_0x05ab
        L_0x05c1:
            java.lang.String r7 = "OMX.SEC.AVC.Encoder"
            boolean r7 = r9.equals(r7)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            if (r7 == 0) goto L_0x05cb
            r7 = 4
            goto L_0x05a8
        L_0x05cb:
            java.lang.String r7 = "OMX.TI.DUCATI1.VIDEO.H264E"
            boolean r7 = r9.equals(r7)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            if (r7 == 0) goto L_0x05d5
            r7 = 5
            goto L_0x05ab
        L_0x05d5:
            r7 = 0
            goto L_0x05ab
        L_0x05d7:
            boolean r31 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            if (r31 == 0) goto L_0x060a
            r31 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            r2.<init>()     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            r32 = r5
            java.lang.String r5 = "codec = "
            r2.append(r5)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            java.lang.String r1 = r1.getName()     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            r2.append(r1)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            java.lang.String r1 = " manufacturer = "
            r2.append(r1)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            r2.append(r6)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            java.lang.String r1 = "device = "
            r2.append(r1)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            java.lang.String r1 = android.os.Build.MODEL     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            r2.append(r1)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            goto L_0x060e
        L_0x060a:
            r31 = r2
            r32 = r5
        L_0x060e:
            r33 = r9
            r9 = r8
            goto L_0x063c
        L_0x0612:
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            java.lang.String r2 = "no supported color format"
            r1.<init>(r2)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            throw r1     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
        L_0x061a:
            r0 = move-exception
            r1 = r75
            r2 = r0
            r50 = r4
            r13 = r14
            r8 = r15
        L_0x0622:
            r3 = 0
            r6 = 0
            r14 = 0
            r37 = 1
            r53 = 0
            r60 = 0
        L_0x062b:
            r61 = 0
            goto L_0x121c
        L_0x062f:
            r31 = r2
            r32 = r5
            r8 = 2130708361(0x7var_, float:1.701803E38)
            r7 = 0
            r9 = 2130708361(0x7var_, float:1.701803E38)
            r33 = 0
        L_0x063c:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1206, all -> 0x1294 }
            if (r1 == 0) goto L_0x0654
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            r1.<init>()     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            java.lang.String r2 = "colorFormat = "
            r1.append(r2)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            r1.append(r9)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
        L_0x0654:
            int r1 = r15 * r14
            int r2 = r1 * 3
            r5 = 2
            int r2 = r2 / r5
            if (r7 != 0) goto L_0x0675
            int r1 = r14 % 16
            if (r1 == 0) goto L_0x06ab
            int r1 = r14 % 16
            r5 = 16
            int r7 = 16 - r1
            int r1 = r14 + r7
            int r1 = r1 - r14
            int r1 = r1 * r15
            int r5 = r1 * 5
            int r5 = r5 / 4
        L_0x066f:
            int r2 = r2 + r5
        L_0x0670:
            r22 = r1
            r21 = r2
            goto L_0x06af
        L_0x0675:
            r5 = 1
            if (r7 != r5) goto L_0x068c
            java.lang.String r5 = r6.toLowerCase()     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            java.lang.String r6 = "lge"
            boolean r5 = r5.equals(r6)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            if (r5 != 0) goto L_0x06ab
            int r5 = r1 + 2047
            r5 = r5 & -2048(0xffffffffffffvar_, float:NaN)
            int r1 = r5 - r1
            int r2 = r2 + r1
            goto L_0x0670
        L_0x068c:
            r1 = 5
            if (r7 != r1) goto L_0x0690
            goto L_0x06ab
        L_0x0690:
            r1 = 3
            if (r7 != r1) goto L_0x06ab
            java.lang.String r1 = "baidu"
            boolean r1 = r6.equals(r1)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            if (r1 == 0) goto L_0x06ab
            int r1 = r14 % 16
            r5 = 16
            int r7 = 16 - r1
            int r1 = r14 + r7
            int r1 = r1 - r14
            int r1 = r1 * r15
            int r5 = r1 * 5
            int r5 = r5 / 4
            goto L_0x066f
        L_0x06ab:
            r21 = r2
            r22 = 0
        L_0x06af:
            android.media.MediaExtractor r1 = r12.extractor     // Catch:{ Exception -> 0x1206, all -> 0x1294 }
            r1.selectTrack(r4)     // Catch:{ Exception -> 0x1206, all -> 0x1294 }
            android.media.MediaExtractor r1 = r12.extractor     // Catch:{ Exception -> 0x1206, all -> 0x1294 }
            android.media.MediaFormat r8 = r1.getTrackFormat(r4)     // Catch:{ Exception -> 0x1206, all -> 0x1294 }
            r6 = r76
            r1 = 0
            int r5 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x06cb
            android.media.MediaExtractor r1 = r12.extractor     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            r2 = 0
            r1.seekTo(r6, r2)     // Catch:{ Exception -> 0x061a, all -> 0x1294 }
            r36 = r4
            goto L_0x06d5
        L_0x06cb:
            android.media.MediaExtractor r1 = r12.extractor     // Catch:{ Exception -> 0x1206, all -> 0x1294 }
            r36 = r4
            r2 = 0
            r4 = 0
            r1.seekTo(r4, r2)     // Catch:{ Exception -> 0x1200, all -> 0x1294 }
        L_0x06d5:
            if (r75 > 0) goto L_0x06db
            r1 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x06dd
        L_0x06db:
            r1 = r75
        L_0x06dd:
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r13, r15, r14)     // Catch:{ Exception -> 0x11f5, all -> 0x1294 }
            java.lang.String r4 = "color-format"
            r2.setInteger(r4, r9)     // Catch:{ Exception -> 0x11f5, all -> 0x1294 }
            java.lang.String r4 = "bitrate"
            r2.setInteger(r4, r1)     // Catch:{ Exception -> 0x11f5, all -> 0x1294 }
            java.lang.String r4 = "frame-rate"
            r5 = r74
            r2.setInteger(r4, r5)     // Catch:{ Exception -> 0x11f5, all -> 0x1294 }
            java.lang.String r4 = "i-frame-interval"
            r6 = 2
            r2.setInteger(r4, r6)     // Catch:{ Exception -> 0x11f5, all -> 0x1294 }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x11f5, all -> 0x1294 }
            r6 = 23
            if (r4 >= r6) goto L_0x0721
            int r4 = java.lang.Math.min(r14, r15)     // Catch:{ Exception -> 0x0719, all -> 0x1294 }
            r6 = 480(0x1e0, float:6.73E-43)
            if (r4 > r6) goto L_0x0721
            r4 = 921600(0xe1000, float:1.291437E-39)
            if (r1 <= r4) goto L_0x070c
            goto L_0x070d
        L_0x070c:
            r4 = r1
        L_0x070d:
            java.lang.String r1 = "bitrate"
            r2.setInteger(r1, r4)     // Catch:{ Exception -> 0x0715, all -> 0x1294 }
            r20 = r4
            goto L_0x0723
        L_0x0715:
            r0 = move-exception
            r2 = r0
            r1 = r4
            goto L_0x071b
        L_0x0719:
            r0 = move-exception
            r2 = r0
        L_0x071b:
            r13 = r14
            r8 = r15
        L_0x071d:
            r50 = r36
            goto L_0x0622
        L_0x0721:
            r20 = r1
        L_0x0723:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x11e7, all -> 0x1294 }
            r4 = 18
            if (r1 >= r4) goto L_0x073d
            java.lang.String r1 = "stride"
            int r4 = r15 + 32
            r2.setInteger(r1, r4)     // Catch:{ Exception -> 0x0736, all -> 0x1294 }
            java.lang.String r1 = "slice-height"
            r2.setInteger(r1, r14)     // Catch:{ Exception -> 0x0736, all -> 0x1294 }
            goto L_0x073d
        L_0x0736:
            r0 = move-exception
            r2 = r0
            r13 = r14
            r8 = r15
            r1 = r20
            goto L_0x071d
        L_0x073d:
            android.media.MediaCodec r7 = android.media.MediaCodec.createEncoderByType(r13)     // Catch:{ Exception -> 0x11e7, all -> 0x1294 }
            r1 = 0
            r4 = 1
            r7.configure(r2, r1, r1, r4)     // Catch:{ Exception -> 0x11d3, all -> 0x1294 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x11c9, all -> 0x1294 }
            r2 = 18
            if (r1 < r2) goto L_0x077e
            org.telegram.messenger.video.InputSurface r1 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x0769, all -> 0x1294 }
            android.view.Surface r2 = r7.createInputSurface()     // Catch:{ Exception -> 0x0769, all -> 0x1294 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x0769, all -> 0x1294 }
            r1.makeCurrent()     // Catch:{ Exception -> 0x075a, all -> 0x1294 }
            r6 = r1
            goto L_0x077f
        L_0x075a:
            r0 = move-exception
            r2 = r0
            r53 = r1
            r13 = r14
            r8 = r15
            r1 = r20
            r50 = r36
            r3 = 0
            r6 = 0
            r37 = 1
            goto L_0x0777
        L_0x0769:
            r0 = move-exception
            r2 = r0
            r13 = r14
            r8 = r15
            r1 = r20
            r50 = r36
            r3 = 0
            r6 = 0
            r37 = 1
            r53 = 0
        L_0x0777:
            r60 = 0
            r61 = 0
            r14 = r7
            goto L_0x121c
        L_0x077e:
            r6 = 0
        L_0x077f:
            r7.start()     // Catch:{ Exception -> 0x11ae, all -> 0x1294 }
            java.lang.String r1 = r8.getString(r3)     // Catch:{ Exception -> 0x11ae, all -> 0x1294 }
            android.media.MediaCodec r4 = android.media.MediaCodec.createDecoderByType(r1)     // Catch:{ Exception -> 0x11ae, all -> 0x1294 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1194, all -> 0x1294 }
            r2 = 18
            if (r1 < r2) goto L_0x07e9
            org.telegram.messenger.video.OutputSurface r24 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x07ce, all -> 0x1294 }
            r39 = 0
            float r1 = (float) r5
            r40 = 0
            r41 = r1
            r1 = r24
            r46 = r31
            r31 = 18
            r2 = r84
            r47 = r3
            r3 = r39
            r49 = r4
            r50 = r36
            r4 = r85
            r51 = r32
            r5 = r86
            r53 = r6
            r6 = r72
            r75 = r7
            r7 = r73
            r54 = r8
            r8 = r41
            r31 = r9
            r9 = r40
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x07c7, all -> 0x1294 }
            r8 = r70
            r9 = r24
            goto L_0x0803
        L_0x07c7:
            r0 = move-exception
            r2 = r0
            r13 = r14
            r8 = r15
            r1 = r20
            goto L_0x07dc
        L_0x07ce:
            r0 = move-exception
            r49 = r4
            r53 = r6
            r75 = r7
            r2 = r0
            r13 = r14
            r8 = r15
            r1 = r20
            r50 = r36
        L_0x07dc:
            r6 = r49
            r3 = 0
            r37 = 1
            r60 = 0
        L_0x07e3:
            r61 = 0
        L_0x07e5:
            r14 = r75
            goto L_0x121c
        L_0x07e9:
            r47 = r3
            r49 = r4
            r53 = r6
            r75 = r7
            r54 = r8
            r46 = r31
            r51 = r32
            r50 = r36
            r31 = r9
            org.telegram.messenger.video.OutputSurface r1 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x117a, all -> 0x1294 }
            r8 = r70
            r1.<init>(r15, r14, r8)     // Catch:{ Exception -> 0x117a, all -> 0x1294 }
            r9 = r1
        L_0x0803:
            android.view.Surface r1 = r9.getSurface()     // Catch:{ Exception -> 0x1163, all -> 0x1294 }
            r6 = r49
            r2 = r54
            r3 = 0
            r7 = 0
            r6.configure(r2, r1, r7, r3)     // Catch:{ Exception -> 0x114c, all -> 0x1294 }
            r6.start()     // Catch:{ Exception -> 0x113f, all -> 0x1294 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x113f, all -> 0x1294 }
            r2 = 21
            if (r1 >= r2) goto L_0x0845
            java.nio.ByteBuffer[] r1 = r6.getInputBuffers()     // Catch:{ Exception -> 0x0837, all -> 0x1294 }
            java.nio.ByteBuffer[] r2 = r75.getOutputBuffers()     // Catch:{ Exception -> 0x0837, all -> 0x1294 }
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0837, all -> 0x1294 }
            r5 = 18
            if (r3 >= r5) goto L_0x0831
            java.nio.ByteBuffer[] r3 = r75.getInputBuffers()     // Catch:{ Exception -> 0x0837, all -> 0x1294 }
            r24 = r1
            r1 = r2
            r30 = r3
            goto L_0x084c
        L_0x0831:
            r24 = r1
            r1 = r2
            r30 = r7
            goto L_0x084c
        L_0x0837:
            r0 = move-exception
            r2 = r0
            r61 = r7
            r60 = r9
            r13 = r14
            r8 = r15
            r1 = r20
            r3 = 0
            r37 = 1
            goto L_0x07e5
        L_0x0845:
            r5 = 18
            r1 = r7
            r24 = r1
            r30 = r24
        L_0x084c:
            r2 = r46
            if (r2 < 0) goto L_0x0978
            android.media.MediaExtractor r3 = r12.extractor     // Catch:{ Exception -> 0x0961, all -> 0x1294 }
            android.media.MediaFormat r3 = r3.getTrackFormat(r2)     // Catch:{ Exception -> 0x0961, all -> 0x1294 }
            r4 = r47
            java.lang.String r7 = r3.getString(r4)     // Catch:{ Exception -> 0x0961, all -> 0x1294 }
            java.lang.String r5 = "audio/mp4a-latm"
            boolean r5 = r7.equals(r5)     // Catch:{ Exception -> 0x0961, all -> 0x1294 }
            if (r5 != 0) goto L_0x0880
            java.lang.String r5 = r3.getString(r4)     // Catch:{ Exception -> 0x0873, all -> 0x1294 }
            java.lang.String r7 = "audio/mpeg"
            boolean r5 = r5.equals(r7)     // Catch:{ Exception -> 0x0873, all -> 0x1294 }
            if (r5 == 0) goto L_0x0871
            goto L_0x0880
        L_0x0871:
            r5 = 0
            goto L_0x0881
        L_0x0873:
            r0 = move-exception
        L_0x0874:
            r2 = r0
            r60 = r9
            r13 = r14
            r8 = r15
        L_0x0879:
            r1 = r20
            r3 = 0
        L_0x087c:
            r37 = 1
            goto L_0x07e3
        L_0x0880:
            r5 = 1
        L_0x0881:
            java.lang.String r4 = r3.getString(r4)     // Catch:{ Exception -> 0x0961, all -> 0x1294 }
            java.lang.String r7 = "audio/unknown"
            boolean r4 = r4.equals(r7)     // Catch:{ Exception -> 0x0961, all -> 0x1294 }
            if (r4 == 0) goto L_0x088e
            r2 = -1
        L_0x088e:
            if (r2 < 0) goto L_0x094e
            if (r5 == 0) goto L_0x08ec
            org.telegram.messenger.video.MP4Builder r4 = r12.mediaMuxer     // Catch:{ Exception -> 0x08e8, all -> 0x1294 }
            r7 = 1
            int r4 = r4.addTrack(r3, r7)     // Catch:{ Exception -> 0x08e8, all -> 0x1294 }
            android.media.MediaExtractor r7 = r12.extractor     // Catch:{ Exception -> 0x08e8, all -> 0x1294 }
            r7.selectTrack(r2)     // Catch:{ Exception -> 0x08e8, all -> 0x1294 }
            java.lang.String r7 = "max-input-size"
            int r3 = r3.getInteger(r7)     // Catch:{ Exception -> 0x08e8, all -> 0x1294 }
            java.nio.ByteBuffer r3 = java.nio.ByteBuffer.allocateDirect(r3)     // Catch:{ Exception -> 0x08e8, all -> 0x1294 }
            r7 = r76
            r27 = 0
            int r34 = (r7 > r27 ? 1 : (r7 == r27 ? 0 : -1))
            if (r34 <= 0) goto L_0x08c8
            r34 = r1
            android.media.MediaExtractor r1 = r12.extractor     // Catch:{ Exception -> 0x0873, all -> 0x1294 }
            r36 = r3
            r3 = 0
            r1.seekTo(r7, r3)     // Catch:{ Exception -> 0x08bf, all -> 0x1294 }
            r38 = r4
            r39 = r5
            goto L_0x08d8
        L_0x08bf:
            r0 = move-exception
            r2 = r0
            r60 = r9
            r13 = r14
            r8 = r15
            r1 = r20
            goto L_0x087c
        L_0x08c8:
            r34 = r1
            r36 = r3
            android.media.MediaExtractor r1 = r12.extractor     // Catch:{ Exception -> 0x0873, all -> 0x1294 }
            r38 = r4
            r39 = r5
            r3 = 0
            r5 = 0
            r1.seekTo(r3, r5)     // Catch:{ Exception -> 0x0873, all -> 0x1294 }
        L_0x08d8:
            r15 = r68
            r3 = r2
            r4 = r36
            r2 = r38
            r38 = r39
            r5 = 0
            r36 = r13
            r13 = r78
            goto L_0x0988
        L_0x08e8:
            r0 = move-exception
            r7 = r76
            goto L_0x0874
        L_0x08ec:
            r7 = r76
            r34 = r1
            r39 = r5
            android.media.MediaExtractor r1 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x094a, all -> 0x1294 }
            r1.<init>()     // Catch:{ Exception -> 0x094a, all -> 0x1294 }
            r5 = r15
            r15 = r68
            r1.setDataSource(r15)     // Catch:{ Exception -> 0x0948, all -> 0x1294 }
            r1.selectTrack(r2)     // Catch:{ Exception -> 0x0948, all -> 0x1294 }
            r4 = 0
            int r27 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r27 <= 0) goto L_0x0916
            r4 = 0
            r1.seekTo(r7, r4)     // Catch:{ Exception -> 0x090d, all -> 0x1294 }
            r36 = r13
            goto L_0x091d
        L_0x090d:
            r0 = move-exception
            r8 = r72
            r2 = r0
            r60 = r9
            r13 = r14
            goto L_0x0879
        L_0x0916:
            r36 = r13
            r13 = r4
            r4 = 0
            r1.seekTo(r13, r4)     // Catch:{ Exception -> 0x0948, all -> 0x1294 }
        L_0x091d:
            org.telegram.messenger.video.AudioRecoder r4 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x0948, all -> 0x1294 }
            r4.<init>(r3, r1, r2)     // Catch:{ Exception -> 0x0948, all -> 0x1294 }
            r4.startTime = r7     // Catch:{ Exception -> 0x093a, all -> 0x1294 }
            r13 = r78
            r4.endTime = r13     // Catch:{ Exception -> 0x0938, all -> 0x1294 }
            org.telegram.messenger.video.MP4Builder r1 = r12.mediaMuxer     // Catch:{ Exception -> 0x0938, all -> 0x1294 }
            android.media.MediaFormat r3 = r4.format     // Catch:{ Exception -> 0x0938, all -> 0x1294 }
            r5 = 1
            int r1 = r1.addTrack(r3, r5)     // Catch:{ Exception -> 0x0938, all -> 0x1294 }
            r3 = r2
            r5 = r4
            r38 = r39
            r4 = 0
            r2 = r1
            goto L_0x0988
        L_0x0938:
            r0 = move-exception
            goto L_0x093d
        L_0x093a:
            r0 = move-exception
            r13 = r78
        L_0x093d:
            r8 = r72
            r13 = r73
            r14 = r75
            r2 = r0
            r61 = r4
            goto L_0x09d3
        L_0x0948:
            r0 = move-exception
            goto L_0x0966
        L_0x094a:
            r0 = move-exception
            r15 = r68
            goto L_0x0966
        L_0x094e:
            r15 = r68
            r7 = r76
            r34 = r1
            r39 = r5
            r36 = r13
            r13 = r78
            r3 = r2
            r38 = r39
            r2 = -5
            r4 = 0
            r5 = 0
            goto L_0x0988
        L_0x0961:
            r0 = move-exception
            r15 = r68
            r7 = r76
        L_0x0966:
            r13 = r78
            r8 = r72
            r13 = r73
            r14 = r75
            r2 = r0
            r60 = r9
            r1 = r20
            r3 = 0
            r37 = 1
            goto L_0x062b
        L_0x0978:
            r15 = r68
            r7 = r76
            r34 = r1
            r36 = r13
            r13 = r78
            r3 = r2
            r2 = -5
            r4 = 0
            r5 = 0
            r38 = 1
        L_0x0988:
            if (r3 >= 0) goto L_0x098c
            r1 = 1
            goto L_0x098d
        L_0x098c:
            r1 = 0
        L_0x098d:
            r67.checkConversionCanceled()     // Catch:{ Exception -> 0x1125, all -> 0x1294 }
            r55 = r16
            r16 = 1
            r17 = 0
            r46 = 0
            r47 = 0
            r49 = 0
            r54 = -5
            r57 = 0
        L_0x09a0:
            if (r17 == 0) goto L_0x09bb
            if (r38 != 0) goto L_0x09a7
            if (r1 != 0) goto L_0x09a7
            goto L_0x09bb
        L_0x09a7:
            r8 = r72
            r13 = r73
            r14 = r5
            r4 = r6
            r60 = r9
            r1 = r20
            r2 = 0
            r3 = 0
            r37 = 1
            r9 = r74
            r5 = r75
            goto L_0x125c
        L_0x09bb:
            r67.checkConversionCanceled()     // Catch:{ Exception -> 0x1125, all -> 0x1294 }
            if (r38 != 0) goto L_0x09d7
            if (r5 == 0) goto L_0x09d7
            org.telegram.messenger.video.MP4Builder r1 = r12.mediaMuxer     // Catch:{ Exception -> 0x09c9, all -> 0x1294 }
            boolean r1 = r5.step(r1, r2)     // Catch:{ Exception -> 0x09c9, all -> 0x1294 }
            goto L_0x09d7
        L_0x09c9:
            r0 = move-exception
            r8 = r72
            r13 = r73
            r14 = r75
            r2 = r0
            r61 = r5
        L_0x09d3:
            r60 = r9
            goto L_0x0b10
        L_0x09d7:
            r59 = r1
            if (r46 != 0) goto L_0x0b17
            android.media.MediaExtractor r1 = r12.extractor     // Catch:{ Exception -> 0x0b04, all -> 0x1294 }
            int r1 = r1.getSampleTrackIndex()     // Catch:{ Exception -> 0x0b04, all -> 0x1294 }
            r15 = r50
            if (r1 != r15) goto L_0x0a54
            r60 = r9
            r50 = r10
            r9 = 2500(0x9c4, double:1.235E-320)
            int r1 = r6.dequeueInputBuffer(r9)     // Catch:{ Exception -> 0x0a46, all -> 0x1294 }
            if (r1 < 0) goto L_0x0a3d
            int r9 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a46, all -> 0x1294 }
            r10 = 21
            if (r9 >= r10) goto L_0x0a05
            r9 = r24[r1]     // Catch:{ Exception -> 0x09fa, all -> 0x1294 }
            goto L_0x0a09
        L_0x09fa:
            r0 = move-exception
            r8 = r72
            r13 = r73
            r14 = r75
            r2 = r0
            r61 = r5
            goto L_0x0a50
        L_0x0a05:
            java.nio.ByteBuffer r9 = r6.getInputBuffer(r1)     // Catch:{ Exception -> 0x0a46, all -> 0x1294 }
        L_0x0a09:
            android.media.MediaExtractor r10 = r12.extractor     // Catch:{ Exception -> 0x0a46, all -> 0x1294 }
            r61 = r5
            r5 = 0
            int r42 = r10.readSampleData(r9, r5)     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            if (r42 >= 0) goto L_0x0a26
            r41 = 0
            r42 = 0
            r43 = 0
            r45 = 4
            r39 = r6
            r40 = r1
            r39.queueInputBuffer(r40, r41, r42, r43, r45)     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            r46 = 1
            goto L_0x0a3f
        L_0x0a26:
            r41 = 0
            android.media.MediaExtractor r5 = r12.extractor     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            long r43 = r5.getSampleTime()     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            r45 = 0
            r39 = r6
            r40 = r1
            r39.queueInputBuffer(r40, r41, r42, r43, r45)     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            android.media.MediaExtractor r1 = r12.extractor     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            r1.advance()     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            goto L_0x0a3f
        L_0x0a3d:
            r61 = r5
        L_0x0a3f:
            r5 = r2
            r62 = r3
            r63 = r4
            goto L_0x0ae5
        L_0x0a46:
            r0 = move-exception
            r61 = r5
        L_0x0a49:
            r8 = r72
            r13 = r73
            r14 = r75
            r2 = r0
        L_0x0a50:
            r50 = r15
            goto L_0x0b10
        L_0x0a54:
            r61 = r5
            r60 = r9
            r50 = r10
            if (r38 == 0) goto L_0x0ad9
            r9 = -1
            if (r3 == r9) goto L_0x0ad9
            if (r1 != r3) goto L_0x0ad9
            android.media.MediaExtractor r1 = r12.extractor     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            r5 = 0
            int r1 = r1.readSampleData(r4, r5)     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            r11.size = r1     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            r10 = 21
            if (r1 >= r10) goto L_0x0a78
            r4.position(r5)     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            int r1 = r11.size     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            r4.limit(r1)     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
        L_0x0a78:
            int r1 = r11.size     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            if (r1 < 0) goto L_0x0a8a
            android.media.MediaExtractor r1 = r12.extractor     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            long r9 = r1.getSampleTime()     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            r11.presentationTimeUs = r9     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            android.media.MediaExtractor r1 = r12.extractor     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            r1.advance()     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            goto L_0x0a8f
        L_0x0a8a:
            r1 = 0
            r11.size = r1     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            r46 = 1
        L_0x0a8f:
            int r1 = r11.size     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            if (r1 <= 0) goto L_0x0a3f
            r9 = 0
            int r1 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1))
            if (r1 < 0) goto L_0x0a9f
            long r9 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            int r1 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r1 >= 0) goto L_0x0a3f
        L_0x0a9f:
            r1 = 0
            r11.offset = r1     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            android.media.MediaExtractor r5 = r12.extractor     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            int r5 = r5.getSampleFlags()     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            r11.flags = r5     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            org.telegram.messenger.video.MP4Builder r5 = r12.mediaMuxer     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            long r9 = r5.writeSampleData(r2, r4, r11, r1)     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            r27 = 0
            int r1 = (r9 > r27 ? 1 : (r9 == r27 ? 0 : -1))
            if (r1 == 0) goto L_0x0a3f
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r12.callback     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            if (r1 == 0) goto L_0x0a3f
            r5 = r2
            long r1 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            long r1 = r1 - r7
            int r39 = (r1 > r57 ? 1 : (r1 == r57 ? 0 : -1))
            if (r39 <= 0) goto L_0x0ac6
            long r1 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            long r57 = r1 - r7
        L_0x0ac6:
            r62 = r3
            r1 = r57
            org.telegram.messenger.MediaController$VideoConvertorListener r3 = r12.callback     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            r63 = r4
            float r4 = (float) r1     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            float r4 = r4 / r18
            float r4 = r4 / r19
            r3.didWriteData(r9, r4)     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            r57 = r1
            goto L_0x0ae5
        L_0x0ad9:
            r5 = r2
            r62 = r3
            r63 = r4
            r2 = -1
            if (r1 != r2) goto L_0x0ae5
            r3 = r46
            r4 = 1
            goto L_0x0ae8
        L_0x0ae5:
            r3 = r46
            r4 = 0
        L_0x0ae8:
            if (r4 == 0) goto L_0x0b26
            r1 = 2500(0x9c4, double:1.235E-320)
            int r40 = r6.dequeueInputBuffer(r1)     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            if (r40 < 0) goto L_0x0b26
            r41 = 0
            r42 = 0
            r43 = 0
            r45 = 4
            r39 = r6
            r39.queueInputBuffer(r40, r41, r42, r43, r45)     // Catch:{ Exception -> 0x0b01, all -> 0x1294 }
            r3 = 1
            goto L_0x0b26
        L_0x0b01:
            r0 = move-exception
            goto L_0x0a49
        L_0x0b04:
            r0 = move-exception
            r61 = r5
            r60 = r9
            r8 = r72
            r13 = r73
            r14 = r75
            r2 = r0
        L_0x0b10:
            r1 = r20
        L_0x0b12:
            r3 = 0
            r37 = 1
            goto L_0x121c
        L_0x0b17:
            r62 = r3
            r63 = r4
            r61 = r5
            r60 = r9
            r15 = r50
            r5 = r2
            r50 = r10
            r3 = r46
        L_0x0b26:
            r1 = r47 ^ 1
            r9 = r1
            r46 = r3
            r1 = r54
            r4 = 1
        L_0x0b2e:
            if (r9 != 0) goto L_0x0b48
            if (r4 == 0) goto L_0x0b33
            goto L_0x0b48
        L_0x0b33:
            r54 = r1
            r2 = r5
            r10 = r50
            r1 = r59
            r9 = r60
            r5 = r61
            r3 = r62
            r4 = r63
            r50 = r15
            r15 = r68
            goto L_0x09a0
        L_0x0b48:
            r67.checkConversionCanceled()     // Catch:{ Exception -> 0x110b, all -> 0x1294 }
            if (r83 == 0) goto L_0x0b52
            r2 = 22000(0x55f0, double:1.08694E-319)
            r10 = r75
            goto L_0x0b56
        L_0x0b52:
            r10 = r75
            r2 = 2500(0x9c4, double:1.235E-320)
        L_0x0b56:
            int r2 = r10.dequeueOutputBuffer(r11, r2)     // Catch:{ Exception -> 0x1105, all -> 0x1294 }
            r3 = -1
            if (r2 != r3) goto L_0x0b83
            r13 = r73
            r39 = r29
            r52 = r49
            r4 = r51
            r14 = 3
            r25 = 0
        L_0x0b68:
            r26 = 2
            r29 = r6
            r51 = r9
            r49 = r34
            r9 = r50
            r6 = -1
            r50 = r15
            r34 = r16
            r15 = r36
            r16 = r1
            r36 = r17
            r17 = r5
            r5 = r72
            goto L_0x0dc2
        L_0x0b83:
            r3 = -3
            if (r2 != r3) goto L_0x0ba7
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b9e, all -> 0x1294 }
            r75 = r4
            r4 = 21
            if (r3 >= r4) goto L_0x0b92
            java.nio.ByteBuffer[] r34 = r10.getOutputBuffers()     // Catch:{ Exception -> 0x0b9e, all -> 0x1294 }
        L_0x0b92:
            r13 = r73
            r25 = r75
            r39 = r29
            r52 = r49
            r4 = r51
            r14 = 3
            goto L_0x0b68
        L_0x0b9e:
            r0 = move-exception
            r8 = r72
            r13 = r73
            r2 = r0
            r14 = r10
            goto L_0x0a50
        L_0x0ba7:
            r75 = r4
            r3 = -2
            if (r2 != r3) goto L_0x0c1c
            android.media.MediaFormat r3 = r10.getOutputFormat()     // Catch:{ Exception -> 0x0b9e, all -> 0x1294 }
            r4 = -5
            if (r1 != r4) goto L_0x0bf6
            if (r3 == 0) goto L_0x0bf6
            org.telegram.messenger.video.MP4Builder r1 = r12.mediaMuxer     // Catch:{ Exception -> 0x0b9e, all -> 0x1294 }
            r4 = 0
            int r1 = r1.addTrack(r3, r4)     // Catch:{ Exception -> 0x0b9e, all -> 0x1294 }
            r4 = r29
            boolean r29 = r3.containsKey(r4)     // Catch:{ Exception -> 0x0b9e, all -> 0x1294 }
            if (r29 == 0) goto L_0x0be9
            r29 = r1
            int r1 = r3.getInteger(r4)     // Catch:{ Exception -> 0x0b9e, all -> 0x1294 }
            r39 = r4
            r4 = 1
            if (r1 != r4) goto L_0x0bed
            r4 = r51
            java.nio.ByteBuffer r1 = r3.getByteBuffer(r4)     // Catch:{ Exception -> 0x0b9e, all -> 0x1294 }
            r51 = r9
            r9 = r50
            java.nio.ByteBuffer r3 = r3.getByteBuffer(r9)     // Catch:{ Exception -> 0x0b9e, all -> 0x1294 }
            int r1 = r1.limit()     // Catch:{ Exception -> 0x0b9e, all -> 0x1294 }
            int r3 = r3.limit()     // Catch:{ Exception -> 0x0b9e, all -> 0x1294 }
            int r1 = r1 + r3
            r49 = r1
            goto L_0x0bf3
        L_0x0be9:
            r29 = r1
            r39 = r4
        L_0x0bed:
            r4 = r51
            r51 = r9
            r9 = r50
        L_0x0bf3:
            r1 = r29
            goto L_0x0bfe
        L_0x0bf6:
            r39 = r29
            r4 = r51
            r51 = r9
            r9 = r50
        L_0x0bfe:
            r13 = r73
            r25 = r75
            r29 = r6
            r50 = r15
            r15 = r36
            r52 = r49
            r6 = -1
            r14 = 3
            r26 = 2
            r36 = r17
            r49 = r34
            r17 = r5
            r34 = r16
            r5 = r72
        L_0x0CLASSNAME:
            r16 = r1
            goto L_0x0dc2
        L_0x0c1c:
            r39 = r29
            r4 = r51
            r51 = r9
            r9 = r50
            if (r2 < 0) goto L_0x10d0
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1105, all -> 0x1294 }
            r50 = r15
            r15 = 21
            if (r3 >= r15) goto L_0x0c3a
            r3 = r34[r2]     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            goto L_0x0c3e
        L_0x0CLASSNAME:
            r0 = move-exception
        L_0x0CLASSNAME:
            r8 = r72
            r13 = r73
        L_0x0CLASSNAME:
            r2 = r0
        L_0x0CLASSNAME:
            r14 = r10
            goto L_0x0b10
        L_0x0c3a:
            java.nio.ByteBuffer r3 = r10.getOutputBuffer(r2)     // Catch:{ Exception -> 0x10c8, all -> 0x1294 }
        L_0x0c3e:
            if (r3 == 0) goto L_0x109d
            int r15 = r11.size     // Catch:{ Exception -> 0x10c8, all -> 0x1294 }
            r17 = r5
            r5 = 1
            if (r15 <= r5) goto L_0x0d9d
            int r15 = r11.flags     // Catch:{ Exception -> 0x0d94, all -> 0x1294 }
            r26 = 2
            r15 = r15 & 2
            if (r15 != 0) goto L_0x0d09
            if (r49 == 0) goto L_0x0CLASSNAME
            int r15 = r11.flags     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            r15 = r15 & r5
            if (r15 == 0) goto L_0x0CLASSNAME
            int r5 = r11.offset     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            int r5 = r5 + r49
            r11.offset = r5     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            int r5 = r11.size     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            int r5 = r5 - r49
            r11.size = r5     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
        L_0x0CLASSNAME:
            if (r16 == 0) goto L_0x0cb0
            int r5 = r11.flags     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            r15 = 1
            r5 = r5 & r15
            if (r5 == 0) goto L_0x0cb0
            int r5 = r11.size     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            r15 = 100
            if (r5 <= r15) goto L_0x0cae
            int r5 = r11.offset     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            r3.position(r5)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            byte[] r5 = new byte[r15]     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            r3.get(r5)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            r15 = 0
            r16 = 0
        L_0x0c7d:
            r13 = 96
            if (r15 >= r13) goto L_0x0cae
            byte r13 = r5[r15]     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            if (r13 != 0) goto L_0x0ca9
            int r13 = r15 + 1
            byte r13 = r5[r13]     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            if (r13 != 0) goto L_0x0ca9
            int r13 = r15 + 2
            byte r13 = r5[r13]     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            if (r13 != 0) goto L_0x0ca9
            int r13 = r15 + 3
            byte r13 = r5[r13]     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            r14 = 1
            if (r13 != r14) goto L_0x0ca9
            int r13 = r16 + 1
            if (r13 <= r14) goto L_0x0ca7
            int r5 = r11.offset     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            int r5 = r5 + r15
            r11.offset = r5     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            int r5 = r11.size     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            int r5 = r5 - r15
            r11.size = r5     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1294 }
            goto L_0x0cae
        L_0x0ca7:
            r16 = r13
        L_0x0ca9:
            int r15 = r15 + 1
            r13 = r78
            goto L_0x0c7d
        L_0x0cae:
            r16 = 0
        L_0x0cb0:
            org.telegram.messenger.video.MP4Builder r5 = r12.mediaMuxer     // Catch:{ Exception -> 0x0d04, all -> 0x1294 }
            r13 = 1
            long r14 = r5.writeSampleData(r1, r3, r11, r13)     // Catch:{ Exception -> 0x0d04, all -> 0x1294 }
            r27 = 0
            int r3 = (r14 > r27 ? 1 : (r14 == r27 ? 0 : -1))
            if (r3 == 0) goto L_0x0cf9
            org.telegram.messenger.MediaController$VideoConvertorListener r3 = r12.callback     // Catch:{ Exception -> 0x0d04, all -> 0x1294 }
            if (r3 == 0) goto L_0x0cf9
            r13 = r6
            long r5 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0cf0, all -> 0x1294 }
            long r5 = r5 - r7
            int r3 = (r5 > r57 ? 1 : (r5 == r57 ? 0 : -1))
            if (r3 <= 0) goto L_0x0cdd
            long r5 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0cce, all -> 0x1294 }
            long r57 = r5 - r7
            goto L_0x0cdd
        L_0x0cce:
            r0 = move-exception
            r8 = r72
            r2 = r0
            r14 = r10
            r6 = r13
            r1 = r20
            r3 = 0
            r37 = 1
            r13 = r73
            goto L_0x121c
        L_0x0cdd:
            r5 = r57
            org.telegram.messenger.MediaController$VideoConvertorListener r3 = r12.callback     // Catch:{ Exception -> 0x0cf0, all -> 0x1294 }
            r29 = r13
            float r13 = (float) r5
            float r13 = r13 / r18
            float r13 = r13 / r19
            r3.didWriteData(r14, r13)     // Catch:{ Exception -> 0x0cee, all -> 0x1294 }
            r57 = r5
            goto L_0x0cfb
        L_0x0cee:
            r0 = move-exception
            goto L_0x0cf3
        L_0x0cf0:
            r0 = move-exception
            r29 = r13
        L_0x0cf3:
            r8 = r72
            r13 = r73
            goto L_0x0d8c
        L_0x0cf9:
            r29 = r6
        L_0x0cfb:
            r5 = r72
            r13 = r73
            r15 = r36
            r14 = 3
            goto L_0x0da8
        L_0x0d04:
            r0 = move-exception
            r29 = r6
            goto L_0x0CLASSNAME
        L_0x0d09:
            r29 = r6
            r13 = -5
            if (r1 != r13) goto L_0x0cfb
            int r1 = r11.size     // Catch:{ Exception -> 0x0d87, all -> 0x1294 }
            byte[] r1 = new byte[r1]     // Catch:{ Exception -> 0x0d87, all -> 0x1294 }
            int r5 = r11.offset     // Catch:{ Exception -> 0x0d87, all -> 0x1294 }
            int r6 = r11.size     // Catch:{ Exception -> 0x0d87, all -> 0x1294 }
            int r5 = r5 + r6
            r3.limit(r5)     // Catch:{ Exception -> 0x0d87, all -> 0x1294 }
            int r5 = r11.offset     // Catch:{ Exception -> 0x0d87, all -> 0x1294 }
            r3.position(r5)     // Catch:{ Exception -> 0x0d87, all -> 0x1294 }
            r3.get(r1)     // Catch:{ Exception -> 0x0d87, all -> 0x1294 }
            int r3 = r11.size     // Catch:{ Exception -> 0x0d87, all -> 0x1294 }
            r5 = 1
            int r3 = r3 - r5
        L_0x0d26:
            r14 = 3
            if (r3 < 0) goto L_0x0d65
            if (r3 <= r14) goto L_0x0d65
            byte r6 = r1[r3]     // Catch:{ Exception -> 0x0cee, all -> 0x1294 }
            if (r6 != r5) goto L_0x0d60
            int r5 = r3 + -1
            byte r5 = r1[r5]     // Catch:{ Exception -> 0x0cee, all -> 0x1294 }
            if (r5 != 0) goto L_0x0d60
            int r5 = r3 + -2
            byte r5 = r1[r5]     // Catch:{ Exception -> 0x0cee, all -> 0x1294 }
            if (r5 != 0) goto L_0x0d60
            int r5 = r3 + -3
            byte r6 = r1[r5]     // Catch:{ Exception -> 0x0cee, all -> 0x1294 }
            if (r6 != 0) goto L_0x0d60
            java.nio.ByteBuffer r3 = java.nio.ByteBuffer.allocate(r5)     // Catch:{ Exception -> 0x0cee, all -> 0x1294 }
            int r6 = r11.size     // Catch:{ Exception -> 0x0cee, all -> 0x1294 }
            int r6 = r6 - r5
            java.nio.ByteBuffer r6 = java.nio.ByteBuffer.allocate(r6)     // Catch:{ Exception -> 0x0cee, all -> 0x1294 }
            r15 = 0
            java.nio.ByteBuffer r13 = r3.put(r1, r15, r5)     // Catch:{ Exception -> 0x0cee, all -> 0x1294 }
            r13.position(r15)     // Catch:{ Exception -> 0x0cee, all -> 0x1294 }
            int r13 = r11.size     // Catch:{ Exception -> 0x0cee, all -> 0x1294 }
            int r13 = r13 - r5
            java.nio.ByteBuffer r1 = r6.put(r1, r5, r13)     // Catch:{ Exception -> 0x0cee, all -> 0x1294 }
            r1.position(r15)     // Catch:{ Exception -> 0x0cee, all -> 0x1294 }
            r1 = r3
            goto L_0x0d67
        L_0x0d60:
            int r3 = r3 + -1
            r5 = 1
            r13 = -5
            goto L_0x0d26
        L_0x0d65:
            r1 = 0
            r6 = 0
        L_0x0d67:
            r5 = r72
            r13 = r73
            r15 = r36
            android.media.MediaFormat r3 = android.media.MediaFormat.createVideoFormat(r15, r5, r13)     // Catch:{ Exception -> 0x0d83, all -> 0x1294 }
            if (r1 == 0) goto L_0x0d7b
            if (r6 == 0) goto L_0x0d7b
            r3.setByteBuffer(r4, r1)     // Catch:{ Exception -> 0x0d83, all -> 0x1294 }
            r3.setByteBuffer(r9, r6)     // Catch:{ Exception -> 0x0d83, all -> 0x1294 }
        L_0x0d7b:
            org.telegram.messenger.video.MP4Builder r1 = r12.mediaMuxer     // Catch:{ Exception -> 0x0d83, all -> 0x1294 }
            r6 = 0
            int r1 = r1.addTrack(r3, r6)     // Catch:{ Exception -> 0x0d83, all -> 0x1294 }
            goto L_0x0da8
        L_0x0d83:
            r0 = move-exception
            r2 = r0
            r8 = r5
            goto L_0x0d8d
        L_0x0d87:
            r0 = move-exception
            r13 = r73
            r8 = r72
        L_0x0d8c:
            r2 = r0
        L_0x0d8d:
            r14 = r10
            r1 = r20
            r6 = r29
            goto L_0x0b12
        L_0x0d94:
            r0 = move-exception
            r13 = r73
            r29 = r6
            r8 = r72
            goto L_0x0CLASSNAME
        L_0x0d9d:
            r5 = r72
            r13 = r73
            r29 = r6
            r15 = r36
            r14 = 3
            r26 = 2
        L_0x0da8:
            int r3 = r11.flags     // Catch:{ Exception -> 0x1098, all -> 0x1294 }
            r3 = r3 & 4
            if (r3 == 0) goto L_0x0db0
            r3 = 1
            goto L_0x0db1
        L_0x0db0:
            r3 = 0
        L_0x0db1:
            r6 = 0
            r10.releaseOutputBuffer(r2, r6)     // Catch:{ Exception -> 0x1098, all -> 0x1294 }
            r25 = r75
            r36 = r3
            r52 = r49
            r6 = -1
            r49 = r34
            r34 = r16
            goto L_0x0CLASSNAME
        L_0x0dc2:
            if (r2 == r6) goto L_0x0de6
            r13 = r78
            r75 = r10
            r1 = r16
            r5 = r17
            r6 = r29
            r16 = r34
            r17 = r36
            r29 = r39
            r34 = r49
            r49 = r52
            r36 = r15
            r15 = r50
            r50 = r9
            r9 = r51
            r51 = r4
            r4 = r25
            goto L_0x0b2e
        L_0x0de6:
            if (r47 != 0) goto L_0x104e
            r3 = r29
            r1 = 2500(0x9c4, double:1.235E-320)
            int r14 = r3.dequeueOutputBuffer(r11, r1)     // Catch:{ Exception -> 0x103b, all -> 0x1294 }
            if (r14 != r6) goto L_0x0e16
            r35 = r3
            r8 = r5
            r54 = r9
            r64 = r53
            r9 = r60
            r48 = r61
            r53 = r62
            r60 = r63
            r1 = 2500(0x9c4, double:1.235E-320)
            r3 = 0
            r14 = 18
            r27 = 0
            r32 = 0
            r37 = 1
            r51 = 0
        L_0x0e0e:
            r65 = -1
            r62 = r4
            r63 = r39
            goto L_0x106a
        L_0x0e16:
            r1 = -3
            if (r14 != r1) goto L_0x0e20
        L_0x0e19:
            r35 = r3
            r8 = r5
            r54 = r9
            goto L_0x1053
        L_0x0e20:
            r1 = -2
            if (r14 != r1) goto L_0x0e46
            android.media.MediaFormat r1 = r3.getOutputFormat()     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            if (r2 == 0) goto L_0x0e19
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            r2.<init>()     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            java.lang.String r14 = "newFormat = "
            r2.append(r14)     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            r2.append(r1)     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            goto L_0x0e19
        L_0x0e40:
            r0 = move-exception
            r2 = r0
            r6 = r3
            r8 = r5
            goto L_0x0CLASSNAME
        L_0x0e46:
            if (r14 < 0) goto L_0x1018
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1003, all -> 0x1294 }
            r2 = 18
            if (r1 < r2) goto L_0x0e58
            int r1 = r11.size     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            if (r1 == 0) goto L_0x0e54
            r1 = 1
            goto L_0x0e55
        L_0x0e54:
            r1 = 0
        L_0x0e55:
            r27 = 0
            goto L_0x0e6a
        L_0x0e58:
            int r1 = r11.size     // Catch:{ Exception -> 0x1003, all -> 0x1294 }
            if (r1 != 0) goto L_0x0e67
            long r1 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            r27 = 0
            int r40 = (r1 > r27 ? 1 : (r1 == r27 ? 0 : -1))
            if (r40 == 0) goto L_0x0e65
            goto L_0x0e69
        L_0x0e65:
            r1 = 0
            goto L_0x0e6a
        L_0x0e67:
            r27 = 0
        L_0x0e69:
            r1 = 1
        L_0x0e6a:
            int r2 = (r78 > r27 ? 1 : (r78 == r27 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e84
            long r6 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            int r2 = (r6 > r78 ? 1 : (r6 == r78 ? 0 : -1))
            if (r2 < 0) goto L_0x0e84
            int r1 = r11.flags     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            r1 = r1 | 4
            r11.flags = r1     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            r6 = r76
            r1 = 0
            r27 = 0
            r46 = 1
            r47 = 1
            goto L_0x0e88
        L_0x0e84:
            r6 = r76
            r27 = 0
        L_0x0e88:
            int r2 = (r6 > r27 ? 1 : (r6 == r27 ? 0 : -1))
            if (r2 <= 0) goto L_0x0ec3
            r40 = -1
            int r2 = (r55 > r40 ? 1 : (r55 == r40 ? 0 : -1))
            if (r2 != 0) goto L_0x0ec3
            r54 = r9
            long r8 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            int r2 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r2 >= 0) goto L_0x0ebe
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            if (r1 == 0) goto L_0x0ebc
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            r1.<init>()     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            java.lang.String r2 = "drop frame startTime = "
            r1.append(r2)     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            r1.append(r6)     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            java.lang.String r2 = " present time = "
            r1.append(r2)     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            long r8 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            r1.append(r8)     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
        L_0x0ebc:
            r1 = 0
            goto L_0x0ec5
        L_0x0ebe:
            long r8 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0e40, all -> 0x1294 }
            r55 = r8
            goto L_0x0ec5
        L_0x0ec3:
            r54 = r9
        L_0x0ec5:
            r3.releaseOutputBuffer(r14, r1)     // Catch:{ Exception -> 0x1003, all -> 0x1294 }
            if (r1 == 0) goto L_0x0fa2
            r60.awaitNewImage()     // Catch:{ Exception -> 0x0ecf, all -> 0x1294 }
            r1 = 0
            goto L_0x0ed5
        L_0x0ecf:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x1003, all -> 0x1294 }
            r1 = 1
        L_0x0ed5:
            if (r1 != 0) goto L_0x0fa2
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1003, all -> 0x1294 }
            r8 = 18
            if (r1 < r8) goto L_0x0f1e
            r9 = r60
            r14 = 0
            r9.drawImage(r14)     // Catch:{ Exception -> 0x0f0e, all -> 0x1294 }
            long r1 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0f0e, all -> 0x1294 }
            r40 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 * r40
            r8 = r53
            r8.setPresentationTime(r1)     // Catch:{ Exception -> 0x0var_, all -> 0x1294 }
            r8.swapBuffers()     // Catch:{ Exception -> 0x0var_, all -> 0x1294 }
            r35 = r3
            r64 = r8
            r48 = r61
            r53 = r62
            r60 = r63
            r14 = 18
            r32 = 0
            r37 = 1
            r65 = -1
            r62 = r4
            r8 = r5
            goto L_0x0fb9
        L_0x0var_:
            r0 = move-exception
            r2 = r0
            r6 = r3
            r53 = r8
            goto L_0x0var_
        L_0x0f0e:
            r0 = move-exception
            r8 = r53
            r2 = r0
            r6 = r3
        L_0x0var_:
            r60 = r9
            r14 = r10
            r1 = r20
            r3 = 0
            r37 = 1
            r8 = r5
            goto L_0x121c
        L_0x0f1e:
            r8 = r53
            r9 = r60
            r1 = 2500(0x9c4, double:1.235E-320)
            r14 = 0
            int r40 = r10.dequeueInputBuffer(r1)     // Catch:{ Exception -> 0x0f8f, all -> 0x1294 }
            if (r40 < 0) goto L_0x0f6e
            r2 = 1
            r9.drawImage(r2)     // Catch:{ Exception -> 0x0f8f, all -> 0x1294 }
            java.nio.ByteBuffer r1 = r9.getFrame()     // Catch:{ Exception -> 0x0f8f, all -> 0x1294 }
            r35 = r30[r40]     // Catch:{ Exception -> 0x0f8f, all -> 0x1294 }
            r35.clear()     // Catch:{ Exception -> 0x0f8f, all -> 0x1294 }
            r37 = 1
            r2 = r35
            r35 = r3
            r53 = r62
            r3 = r31
            r62 = r4
            r60 = r63
            r63 = r39
            r4 = r72
            r64 = r8
            r48 = r61
            r14 = 18
            r8 = r5
            r5 = r73
            r65 = -1
            r6 = r22
            r32 = 0
            r7 = r33
            org.telegram.messenger.Utilities.convertVideoFrame(r1, r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0ff5, all -> 0x1294 }
            r41 = 0
            long r1 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0ff5, all -> 0x1294 }
            r45 = 0
            r39 = r10
            r42 = r21
            r43 = r1
            r39.queueInputBuffer(r40, r41, r42, r43, r45)     // Catch:{ Exception -> 0x0ff5, all -> 0x1294 }
            goto L_0x0fbb
        L_0x0f6e:
            r35 = r3
            r64 = r8
            r48 = r61
            r53 = r62
            r60 = r63
            r14 = 18
            r32 = 0
            r37 = 1
            r65 = -1
            r62 = r4
            r8 = r5
            r63 = r39
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0ff5, all -> 0x1294 }
            if (r1 == 0) goto L_0x0fbb
            java.lang.String r1 = "input buffer not available"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0ff5, all -> 0x1294 }
            goto L_0x0fbb
        L_0x0f8f:
            r0 = move-exception
            r35 = r3
            r64 = r8
            r48 = r61
            r37 = 1
            r8 = r5
            r2 = r0
            r60 = r9
            r14 = r10
            r1 = r20
            r6 = r35
            goto L_0x1000
        L_0x0fa2:
            r35 = r3
            r8 = r5
            r64 = r53
            r9 = r60
            r48 = r61
            r53 = r62
            r60 = r63
            r14 = 18
            r32 = 0
            r37 = 1
            r65 = -1
            r62 = r4
        L_0x0fb9:
            r63 = r39
        L_0x0fbb:
            int r1 = r11.flags     // Catch:{ Exception -> 0x0ff5, all -> 0x1294 }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x0ff0
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0ff5, all -> 0x1294 }
            if (r1 == 0) goto L_0x0fca
            java.lang.String r1 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0ff5, all -> 0x1294 }
        L_0x0fca:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ff5, all -> 0x1294 }
            if (r1 < r14) goto L_0x0fd4
            r10.signalEndOfInputStream()     // Catch:{ Exception -> 0x0ff5, all -> 0x1294 }
            r1 = 2500(0x9c4, double:1.235E-320)
            goto L_0x0feb
        L_0x0fd4:
            r1 = 2500(0x9c4, double:1.235E-320)
            int r40 = r10.dequeueInputBuffer(r1)     // Catch:{ Exception -> 0x0ff5, all -> 0x1294 }
            if (r40 < 0) goto L_0x0feb
            r41 = 0
            r42 = 1
            long r3 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0ff5, all -> 0x1294 }
            r45 = 4
            r39 = r10
            r43 = r3
            r39.queueInputBuffer(r40, r41, r42, r43, r45)     // Catch:{ Exception -> 0x0ff5, all -> 0x1294 }
        L_0x0feb:
            r3 = 0
            r51 = 0
            goto L_0x106a
        L_0x0ff0:
            r1 = 2500(0x9c4, double:1.235E-320)
            r3 = 0
            goto L_0x106a
        L_0x0ff5:
            r0 = move-exception
            r2 = r0
            r60 = r9
            r14 = r10
            r1 = r20
            r6 = r35
            r61 = r48
        L_0x1000:
            r53 = r64
            goto L_0x1015
        L_0x1003:
            r0 = move-exception
            r35 = r3
            r8 = r5
            r64 = r53
            r9 = r60
            r48 = r61
            r37 = 1
            r2 = r0
            r14 = r10
            r1 = r20
            r6 = r35
        L_0x1015:
            r3 = 0
            goto L_0x121c
        L_0x1018:
            r35 = r3
            r8 = r5
            r64 = r53
            r9 = r60
            r48 = r61
            r3 = 0
            r37 = 1
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            r2.<init>()     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            java.lang.String r4 = "unexpected result from decoder.dequeueOutputBuffer: "
            r2.append(r4)     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            r2.append(r14)     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            throw r1     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
        L_0x103b:
            r0 = move-exception
            r35 = r3
            r8 = r5
        L_0x103f:
            r64 = r53
            r9 = r60
            r48 = r61
            r3 = 0
            r37 = 1
            r2 = r0
            r14 = r10
            r1 = r20
            goto L_0x1190
        L_0x104e:
            r8 = r5
            r54 = r9
            r35 = r29
        L_0x1053:
            r64 = r53
            r9 = r60
            r48 = r61
            r53 = r62
            r60 = r63
            r1 = 2500(0x9c4, double:1.235E-320)
            r3 = 0
            r14 = 18
            r27 = 0
            r32 = 0
            r37 = 1
            goto L_0x0e0e
        L_0x106a:
            r7 = r76
            r13 = r78
            r75 = r10
            r1 = r16
            r5 = r17
            r4 = r25
            r16 = r34
            r6 = r35
            r17 = r36
            r61 = r48
            r34 = r49
            r49 = r52
            r29 = r63
            r36 = r15
            r15 = r50
            r50 = r54
            r63 = r60
            r60 = r9
            r9 = r51
            r51 = r62
            r62 = r53
            r53 = r64
            goto L_0x0b2e
        L_0x1098:
            r0 = move-exception
            r8 = r5
            r35 = r29
            goto L_0x103f
        L_0x109d:
            r8 = r72
            r13 = r73
            r35 = r6
            r64 = r53
            r9 = r60
            r48 = r61
            r3 = 0
            r37 = 1
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            r4.<init>()     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            java.lang.String r5 = "encoderOutputBuffer "
            r4.append(r5)     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            r4.append(r2)     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            java.lang.String r2 = " was null"
            r4.append(r2)     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            java.lang.String r2 = r4.toString()     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            throw r1     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
        L_0x10c8:
            r0 = move-exception
            r8 = r72
            r13 = r73
            r35 = r6
            goto L_0x1116
        L_0x10d0:
            r8 = r72
            r13 = r73
            r35 = r6
            r50 = r15
            r64 = r53
            r9 = r60
            r48 = r61
            r3 = 0
            r37 = 1
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            r4.<init>()     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            java.lang.String r5 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            r4.append(r2)     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            java.lang.String r2 = r4.toString()     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
            throw r1     // Catch:{ Exception -> 0x10f8, all -> 0x1294 }
        L_0x10f8:
            r0 = move-exception
            r2 = r0
            r60 = r9
            r14 = r10
            r1 = r20
            r6 = r35
            r61 = r48
            goto L_0x11c5
        L_0x1105:
            r0 = move-exception
            r8 = r72
            r13 = r73
            goto L_0x1112
        L_0x110b:
            r0 = move-exception
            r8 = r72
            r13 = r73
            r10 = r75
        L_0x1112:
            r35 = r6
            r50 = r15
        L_0x1116:
            r64 = r53
            r9 = r60
            r48 = r61
            r3 = 0
            r37 = 1
            r2 = r0
            r14 = r10
            r1 = r20
            goto L_0x121c
        L_0x1125:
            r0 = move-exception
            r8 = r72
            r13 = r73
            r10 = r75
            r48 = r5
            r35 = r6
            r64 = r53
            r3 = 0
            r37 = 1
            r2 = r0
            r60 = r9
            r14 = r10
            r1 = r20
            r61 = r48
            goto L_0x121c
        L_0x113f:
            r0 = move-exception
            r10 = r75
            r35 = r6
            r32 = r7
            r13 = r14
            r8 = r15
            r64 = r53
            r3 = 0
            goto L_0x1157
        L_0x114c:
            r0 = move-exception
            r10 = r75
            r35 = r6
            r32 = r7
            r13 = r14
            r8 = r15
            r64 = r53
        L_0x1157:
            r37 = 1
            r2 = r0
            r60 = r9
            r14 = r10
            r1 = r20
            r61 = r32
            goto L_0x121c
        L_0x1163:
            r0 = move-exception
            r10 = r75
            r13 = r14
            r8 = r15
            r35 = r49
            r64 = r53
            r3 = 0
            r32 = 0
            r37 = 1
            r2 = r0
            r60 = r9
            r14 = r10
            r1 = r20
            r61 = r32
            goto L_0x1190
        L_0x117a:
            r0 = move-exception
            r10 = r75
            r13 = r14
            r8 = r15
            r35 = r49
            r64 = r53
            r3 = 0
            r32 = 0
            r37 = 1
            r2 = r0
            r14 = r10
            r1 = r20
            r60 = r32
            r61 = r60
        L_0x1190:
            r6 = r35
            goto L_0x121c
        L_0x1194:
            r0 = move-exception
            r35 = r4
            r64 = r6
            r10 = r7
            r13 = r14
            r8 = r15
            r50 = r36
            r3 = 0
            r32 = 0
            r37 = 1
            r2 = r0
            r14 = r10
            r1 = r20
            r60 = r32
            r61 = r60
            r6 = r35
            goto L_0x11c5
        L_0x11ae:
            r0 = move-exception
            r64 = r6
            r10 = r7
            r13 = r14
            r8 = r15
            r50 = r36
            r3 = 0
            r32 = 0
            r37 = 1
            r2 = r0
            r14 = r10
            r1 = r20
            r6 = r32
            r60 = r6
            r61 = r60
        L_0x11c5:
            r53 = r64
            goto L_0x121c
        L_0x11c9:
            r0 = move-exception
            r10 = r7
            r13 = r14
            r8 = r15
            r50 = r36
            r3 = 0
            r32 = 0
            goto L_0x11dc
        L_0x11d3:
            r0 = move-exception
            r32 = r1
            r10 = r7
            r13 = r14
            r8 = r15
            r50 = r36
            r3 = 0
        L_0x11dc:
            r37 = 1
            r2 = r0
            r14 = r10
            r1 = r20
            r6 = r32
            r53 = r6
            goto L_0x1218
        L_0x11e7:
            r0 = move-exception
            r13 = r14
            r8 = r15
            r50 = r36
            r3 = 0
            r32 = 0
            r37 = 1
            r2 = r0
            r1 = r20
            goto L_0x1213
        L_0x11f5:
            r0 = move-exception
            r13 = r14
            r8 = r15
            r50 = r36
            r3 = 0
            r32 = 0
            r37 = 1
            goto L_0x1212
        L_0x1200:
            r0 = move-exception
            r13 = r14
            r8 = r15
            r50 = r36
            goto L_0x120b
        L_0x1206:
            r0 = move-exception
            r50 = r4
            r13 = r14
            r8 = r15
        L_0x120b:
            r3 = 0
            r32 = 0
            r37 = 1
            r1 = r75
        L_0x1212:
            r2 = r0
        L_0x1213:
            r6 = r32
            r14 = r6
            r53 = r14
        L_0x1218:
            r60 = r53
            r61 = r60
        L_0x121c:
            boolean r4 = r2 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x1278, all -> 0x1294 }
            if (r4 == 0) goto L_0x1224
            if (r83 != 0) goto L_0x1224
            r4 = 1
            goto L_0x1225
        L_0x1224:
            r4 = 0
        L_0x1225:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1274, all -> 0x1294 }
            r3.<init>()     // Catch:{ Exception -> 0x1274, all -> 0x1294 }
            java.lang.String r5 = "bitrate: "
            r3.append(r5)     // Catch:{ Exception -> 0x1274, all -> 0x1294 }
            r3.append(r1)     // Catch:{ Exception -> 0x1274, all -> 0x1294 }
            java.lang.String r5 = " framerate: "
            r3.append(r5)     // Catch:{ Exception -> 0x1274, all -> 0x1294 }
            r9 = r74
            r3.append(r9)     // Catch:{ Exception -> 0x1272, all -> 0x1294 }
            java.lang.String r5 = " size: "
            r3.append(r5)     // Catch:{ Exception -> 0x1272, all -> 0x1294 }
            r3.append(r13)     // Catch:{ Exception -> 0x1272, all -> 0x1294 }
            java.lang.String r5 = "x"
            r3.append(r5)     // Catch:{ Exception -> 0x1272, all -> 0x1294 }
            r3.append(r8)     // Catch:{ Exception -> 0x1272, all -> 0x1294 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x1272, all -> 0x1294 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ Exception -> 0x1272, all -> 0x1294 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ Exception -> 0x1272, all -> 0x1294 }
            r2 = r4
            r4 = r6
            r5 = r14
            r14 = r61
            r3 = 1
        L_0x125c:
            android.media.MediaExtractor r6 = r12.extractor     // Catch:{ Exception -> 0x1299, all -> 0x1294 }
            r7 = r50
            r6.unselectTrack(r7)     // Catch:{ Exception -> 0x1299, all -> 0x1294 }
            if (r4 == 0) goto L_0x126b
            r4.stop()     // Catch:{ Exception -> 0x1299, all -> 0x1294 }
            r4.release()     // Catch:{ Exception -> 0x1299, all -> 0x1294 }
        L_0x126b:
            r4 = r3
            r3 = r14
            r32 = r53
            r14 = r60
            goto L_0x128e
        L_0x1272:
            r0 = move-exception
            goto L_0x129b
        L_0x1274:
            r0 = move-exception
            r9 = r74
            goto L_0x129b
        L_0x1278:
            r0 = move-exception
            r9 = r74
            goto L_0x12f6
        L_0x127d:
            r9 = r74
            r13 = r14
            r8 = r15
            r3 = 0
            r32 = 0
            r37 = 1
            r1 = r75
            r3 = r32
            r5 = r3
            r14 = r5
            r2 = 0
            r4 = 0
        L_0x128e:
            if (r14 == 0) goto L_0x129e
            r14.release()     // Catch:{ Exception -> 0x1299, all -> 0x1294 }
            goto L_0x129e
        L_0x1294:
            r0 = move-exception
            r2 = r0
            r1 = r12
            goto L_0x136b
        L_0x1299:
            r0 = move-exception
            r4 = r2
        L_0x129b:
            r2 = r0
            goto L_0x12f8
        L_0x129e:
            if (r32 == 0) goto L_0x12a3
            r32.release()     // Catch:{ Exception -> 0x1299, all -> 0x1294 }
        L_0x12a3:
            if (r5 == 0) goto L_0x12ab
            r5.stop()     // Catch:{ Exception -> 0x1299, all -> 0x1294 }
            r5.release()     // Catch:{ Exception -> 0x1299, all -> 0x1294 }
        L_0x12ab:
            if (r3 == 0) goto L_0x12b0
            r3.release()     // Catch:{ Exception -> 0x1299, all -> 0x1294 }
        L_0x12b0:
            r67.checkConversionCanceled()     // Catch:{ Exception -> 0x1299, all -> 0x1294 }
            r15 = r8
            r11 = r13
        L_0x12b5:
            android.media.MediaExtractor r3 = r12.extractor
            if (r3 == 0) goto L_0x12bc
            r3.release()
        L_0x12bc:
            org.telegram.messenger.video.MP4Builder r3 = r12.mediaMuxer
            if (r3 == 0) goto L_0x12c9
            r3.finishMovie()     // Catch:{ Exception -> 0x12c4 }
            goto L_0x12c9
        L_0x12c4:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x12c9:
            r10 = r1
            r3 = r4
            r7 = r11
            r6 = r15
            goto L_0x1340
        L_0x12cf:
            r0 = move-exception
            r2 = r0
            r1 = r6
            goto L_0x136b
        L_0x12d4:
            r0 = move-exception
            r13 = r11
            r8 = r12
            r9 = r15
            r3 = 0
            r37 = 1
            r12 = r6
            goto L_0x12f4
        L_0x12dd:
            r0 = move-exception
            r9 = r10
            r13 = r11
            r8 = r12
            r3 = 0
            r37 = 1
            r12 = r67
            goto L_0x12f4
        L_0x12e7:
            r0 = move-exception
            r2 = r0
            r1 = r14
            goto L_0x136b
        L_0x12ec:
            r0 = move-exception
            r9 = r10
            r13 = r11
            r8 = r12
            r12 = r14
            r3 = 0
            r37 = 1
        L_0x12f4:
            r1 = r75
        L_0x12f6:
            r2 = r0
            r4 = 0
        L_0x12f8:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x1367 }
            r3.<init>()     // Catch:{ all -> 0x1367 }
            java.lang.String r5 = "bitrate: "
            r3.append(r5)     // Catch:{ all -> 0x1367 }
            r3.append(r1)     // Catch:{ all -> 0x1367 }
            java.lang.String r5 = " framerate: "
            r3.append(r5)     // Catch:{ all -> 0x1367 }
            r3.append(r9)     // Catch:{ all -> 0x1367 }
            java.lang.String r5 = " size: "
            r3.append(r5)     // Catch:{ all -> 0x1367 }
            r3.append(r13)     // Catch:{ all -> 0x1367 }
            java.lang.String r5 = "x"
            r3.append(r5)     // Catch:{ all -> 0x1367 }
            r3.append(r8)     // Catch:{ all -> 0x1367 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x1367 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x1367 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x1367 }
            android.media.MediaExtractor r2 = r12.extractor
            if (r2 == 0) goto L_0x132e
            r2.release()
        L_0x132e:
            org.telegram.messenger.video.MP4Builder r2 = r12.mediaMuxer
            if (r2 == 0) goto L_0x133b
            r2.finishMovie()     // Catch:{ Exception -> 0x1336 }
            goto L_0x133b
        L_0x1336:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x133b:
            r10 = r1
            r2 = r4
            r6 = r8
            r7 = r13
            r3 = 1
        L_0x1340:
            if (r2 == 0) goto L_0x1366
            r17 = 1
            r1 = r67
            r2 = r68
            r3 = r69
            r4 = r70
            r5 = r71
            r8 = r74
            r9 = r10
            r10 = r76
            r12 = r78
            r14 = r80
            r16 = r82
            r18 = r84
            r19 = r85
            r20 = r86
            r21 = r87
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r14, r16, r17, r18, r19, r20, r21)
            return r1
        L_0x1366:
            return r3
        L_0x1367:
            r0 = move-exception
            r1 = r67
            r2 = r0
        L_0x136b:
            android.media.MediaExtractor r3 = r1.extractor
            if (r3 == 0) goto L_0x1372
            r3.release()
        L_0x1372:
            org.telegram.messenger.video.MP4Builder r3 = r1.mediaMuxer
            if (r3 == 0) goto L_0x137f
            r3.finishMovie()     // Catch:{ Exception -> 0x137a }
            goto L_0x137f
        L_0x137a:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x137f:
            goto L_0x1381
        L_0x1380:
            throw r2
        L_0x1381:
            goto L_0x1380
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
