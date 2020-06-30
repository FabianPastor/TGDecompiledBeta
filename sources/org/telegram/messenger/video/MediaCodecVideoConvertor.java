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

    public boolean convertVideo(String str, File file, int i, boolean z, int i2, int i3, int i4, int i5, int i6, long j, long j2, long j3, boolean z2, long j4, MediaController.SavedFilterState savedFilterState, String str2, ArrayList<VideoEditedInfo.MediaEntity> arrayList, boolean z3, MediaController.CropState cropState, MediaController.VideoConvertorListener videoConvertorListener) {
        String str3 = str;
        long j5 = j4;
        this.callback = videoConvertorListener;
        return convertVideoInternal(str, file, i, z, i2, i3, i4, i5, i6, j, j2, j3, j5, z2, false, savedFilterState, str2, arrayList, z3, cropState);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r88v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r88v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r88v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r39v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v25, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r82v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v44, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v54, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v71, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v72, resolved type: java.lang.StringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v76, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v78, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v79, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v50, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v81, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v82, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v83, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v61, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v56, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v67, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v84, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v68, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v85, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v69, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v86, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v70, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v87, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v71, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v89, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v72, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v90, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v73, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v74, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v75, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v94, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v77, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v80, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v101, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v65, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v68, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r82v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v40, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v102, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v73, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v75, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r82v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v65, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v85, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v106, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v87, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v107, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v104, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v89, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v69, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v90, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v91, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v92, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v93, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v94, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v98, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v99, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v101, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r49v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v104, resolved type: org.telegram.messenger.MediaController$VideoConvertorListener} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v73, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v106, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v122, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v107, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v108, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v116, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v123, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v110, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v113, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v80, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v114, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v115, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v116, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v124, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v125, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v124, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v126, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v128, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v123, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v119, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r88v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v134, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v50, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r88v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v51, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r24v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v141, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v92, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v142, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v141, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v143, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v95, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v144, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v142, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v96, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v145, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v143, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v97, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v146, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v144, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v64, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v98, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v146, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v99, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v147, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v66, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v100, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v149, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v149, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v101, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v150, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v150, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v102, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v151, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v151, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v103, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v152, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v152, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v68, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v69, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v70, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v71, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v73, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v74, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v75, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v166, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v181, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v182, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v183, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v184, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v193, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v213, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v218, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v219, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v220, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v93, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v94, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v95, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v96, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v97, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v98, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v176, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v177, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v178, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v179, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v185, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v185, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v186, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v187, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v187, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v189, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v189, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v191, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v192, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v193, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v191, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v192, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v193, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v194, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v195, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v196, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v197, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v200, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v201, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v202, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v198, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v199, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v203, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v204, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v205, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v203, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v206, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v204, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v207, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v208, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v209, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v213, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v211, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v212, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v215, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v216, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v219, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v215, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v105, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v218, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v219, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v220, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v221, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v222, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v224, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v220, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v222, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v226, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v226, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v227, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v107, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v227, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v228, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v108, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v228, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v229, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v109, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v229, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v230, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v230, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v231, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v111, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v231, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v232, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v112, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v234, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v117, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v118, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v119, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v120, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v121, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v122, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v123, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v124, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v125, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v126, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v234, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v235, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v236, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v237, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v238, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v239, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v240, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v241, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v242, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v243, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v244, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v245, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v246, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v247, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v248, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v236, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v237, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v129, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v251, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v238, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v239, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v240, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v241, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v242, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v243, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v244, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v245, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v130, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v252, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v131, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v133, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v134, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v135, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v136, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v137, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v167, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v169, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v253, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v255, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v256, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v257, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v258, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v246, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v247, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v248, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v249, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v251, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v252, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v253, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v254, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v255, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v257, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v258, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v259, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v260, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v259, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v261, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v262, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v260, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v138, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v263, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v264, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v179, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v265, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v140, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v264, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v265, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v267, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v144, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v266, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v269, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v146, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v148, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v149, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v150, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v151, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v152, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v268, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v271, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v273, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v274, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v270, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v271, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v272, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v273, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v153, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v275, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v276, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v277, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v279, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v280, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v281, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v278, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v279, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v280, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v155, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v156, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v157, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v282, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v282, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v283, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v284, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v285, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v286, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v292, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v293, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v295, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v296, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v297, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v298, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v299, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v300, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v303, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v163, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v164, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v304, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v305, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v306, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v288, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v307, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v308, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v291, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v297, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v315, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v316, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v318, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v165, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v319, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v298, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v320, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v299, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v322, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v301, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v324, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v303, resolved type: int} */
    /* JADX WARNING: type inference failed for: r2v53 */
    /* JADX WARNING: type inference failed for: r2v82 */
    /* JADX WARNING: type inference failed for: r8v118, types: [boolean] */
    /* JADX WARNING: type inference failed for: r7v221 */
    /* JADX WARNING: type inference failed for: r7v223 */
    /* JADX WARNING: type inference failed for: r2v237 */
    /* JADX WARNING: type inference failed for: r2v240 */
    /* JADX WARNING: type inference failed for: r2v243 */
    /* JADX WARNING: Code restructure failed: missing block: B:1000:?, code lost:
        r2.finishMovie();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1001:0x11d5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1002:0x11d6, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1003:0x11da, code lost:
        r6 = r8;
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1008:0x1209, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1009:0x120a, code lost:
        r1 = r74;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1010:0x120d, code lost:
        r3 = r1.extractor;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1011:0x120f, code lost:
        if (r3 != null) goto L_0x1211;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1012:0x1211, code lost:
        r3.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1013:0x1214, code lost:
        r3 = r1.mediaMuxer;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1014:0x1216, code lost:
        if (r3 != null) goto L_0x1218;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1016:?, code lost:
        r3.finishMovie();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1017:0x121c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1018:0x121d, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1019:0x1222, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x03b5, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x03b7, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x03b8, code lost:
        r2 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x03ba, code lost:
        r1 = r0;
        r11 = r2;
        r6 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x00a1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x0429, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x042a, code lost:
        r1 = r0;
        r37 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x042f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x0430, code lost:
        r51 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x0432, code lost:
        r10 = r37;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x00a2, code lost:
        r1 = r0;
        r50 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:220:0x0454, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x048d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x048e, code lost:
        r37 = r37;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x0491, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x00a5, code lost:
        r6 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x04a3, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x04a4, code lost:
        r13 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x04a6, code lost:
        r1 = r37;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x04a8, code lost:
        r6 = r13;
        r11 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:0x04ad, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x04ae, code lost:
        r1 = r37;
        r6 = r79;
        r11 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x04b6, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x04b7, code lost:
        r13 = r6;
        r50 = r9;
        r51 = r11;
        r37 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x04be, code lost:
        r31 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x04c1, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x04c3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x04c4, code lost:
        r13 = r6;
        r50 = r9;
        r51 = r11;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x04cc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x04cd, code lost:
        r50 = r9;
        r51 = r11;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x04d4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x04d5, code lost:
        r50 = r9;
        r1 = r0;
        r6 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x04da, code lost:
        r31 = null;
        r37 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x04e0, code lost:
        if ((r1 instanceof java.lang.IllegalStateException) != false) goto L_0x04e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x04e4, code lost:
        r2 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x04e6, code lost:
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:?, code lost:
        r3 = new java.lang.StringBuilder();
        r3.append("bitrate: ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x04f1, code lost:
        r9 = r50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:?, code lost:
        r3.append(r9);
        r3.append(" framerate: ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x04fb, code lost:
        r10 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:?, code lost:
        r3.append(r10);
        r3.append(" size: ");
        r3.append(r11);
        r3.append("x");
        r3.append(r12);
        org.telegram.messenger.FileLog.e(r3.toString());
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x051a, code lost:
        r16 = r2;
        r1 = r37;
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x0525, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x0526, code lost:
        r53 = r86;
        r35 = r88;
        r2 = r0;
        r14 = r9;
        r9 = r10;
        r7 = r11;
        r8 = r12;
        r1 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x0551, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:275:0x0552, code lost:
        r53 = r86;
        r35 = r88;
        r1 = r2;
        r14 = r9;
        r9 = r10;
        r7 = r11;
        r8 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x055c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:277:0x055e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x055f, code lost:
        r9 = r50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x0561, code lost:
        r53 = r86;
        r35 = r88;
        r1 = r2;
        r14 = r9;
        r7 = r11;
        r8 = r12;
        r9 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x056b, code lost:
        r11 = r84;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x056d, code lost:
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0570, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x0571, code lost:
        r53 = r86;
        r35 = r88;
        r2 = r0;
        r14 = r50;
        r7 = r11;
        r8 = r12;
        r1 = 0;
        r9 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x057e, code lost:
        r11 = r84;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x05ab, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x05ac, code lost:
        r53 = r86;
        r35 = r88;
        r2 = r0;
        r14 = r7;
        r9 = r10;
        r7 = r11;
        r8 = r12;
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x05cf, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x05d0, code lost:
        r53 = r86;
        r35 = r88;
        r2 = r0;
        r14 = r7;
        r9 = r10;
        r8 = r12;
        r1 = 0;
        r7 = r80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x0614, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x0615, code lost:
        r8 = r79;
        r7 = r80;
        r14 = r82;
        r11 = r84;
        r53 = r86;
        r35 = r88;
        r2 = r0;
        r9 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x0625, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x0626, code lost:
        r8 = r79;
        r7 = r80;
        r14 = r82;
        r11 = r84;
        r53 = r86;
        r35 = r88;
        r2 = r0;
        r9 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x067b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x067c, code lost:
        r11 = r84;
        r53 = r86;
        r1 = r0;
        r66 = r5;
        r9 = r13;
        r35 = r24;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x0686, code lost:
        r3 = null;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x06bd, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x06be, code lost:
        r53 = r86;
        r1 = r0;
        r11 = r4;
        r35 = r7;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x06c4, code lost:
        r9 = r13;
        r66 = r26;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x06fc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x06fd, code lost:
        r53 = r86;
        r35 = r88;
        r1 = r0;
        r11 = r4;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x0762, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x0763, code lost:
        r11 = r84;
        r53 = r86;
        r35 = r88;
        r1 = r0;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x07f9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x07fa, code lost:
        r10 = r82;
        r11 = r84;
        r53 = r86;
        r1 = r0;
        r43 = null;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x0803, code lost:
        r9 = r13;
        r35 = r25;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x087f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x0880, code lost:
        r10 = r82;
        r11 = r84;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x0884, code lost:
        r53 = r86;
        r1 = r0;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x08a7, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x08a8, code lost:
        r10 = r82;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x08ce, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x08d0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x08d1, code lost:
        r4 = r86;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x08d3, code lost:
        r10 = r82;
        r1 = r0;
        r53 = r4;
        r43 = r6;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x08dc, code lost:
        r0 = e;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x08de, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:480:0x08df, code lost:
        r8 = r75;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x08e1, code lost:
        r4 = r86;
        r10 = r82;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x08f2, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x08f3, code lost:
        r8 = r75;
        r4 = r86;
        r10 = r82;
        r11 = r84;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x08fb, code lost:
        r1 = r0;
        r53 = r4;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:486:0x08fe, code lost:
        r9 = r13;
        r35 = r25;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x095c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x095d, code lost:
        r10 = r82;
        r1 = r0;
        r53 = r4;
        r43 = r9;
        r9 = r13;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x09cb, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x09cc, code lost:
        r10 = r82;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x09ce, code lost:
        r43 = r88;
        r14 = r89;
        r1 = r0;
        r53 = r4;
        r66 = r9;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x09d7, code lost:
        r9 = r81;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x017c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x0a2a, code lost:
        if (r8 < 0) goto L_0x0a2f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x017d, code lost:
        r6 = r79;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0180, code lost:
        r11 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x0a71, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x0a72, code lost:
        r49 = r4;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x0aa3, code lost:
        r0 = e;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x0aa4, code lost:
        r10 = r82;
        r43 = r88;
        r14 = r89;
        r1 = r0;
        r66 = r9;
        r53 = r49;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x0ab1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0ab2, code lost:
        r88 = r9;
        r89 = r14;
        r9 = r81;
        r10 = r82;
        r43 = r88;
        r1 = r0;
        r53 = r4;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0b64, code lost:
        r0 = e;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0bc4, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0bc5, code lost:
        r66 = r9;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0be5, code lost:
        r0 = e;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x01a8, code lost:
        r1 = r2;
        r2 = r4;
        r3 = r5;
        r4 = r6;
        r5 = r7;
        r6 = r8;
        r7 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0c5e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0c5f, code lost:
        r53 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0CLASSNAME, code lost:
        r9 = r81;
        r53 = r53;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0CLASSNAME, code lost:
        r0 = e;
        r53 = r53;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0d16, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0d17, code lost:
        r53 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x0da4, code lost:
        r0 = e;
        r53 = r53;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x0da5, code lost:
        r9 = r81;
        r11 = r84;
        r53 = r53;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x0da9, code lost:
        r43 = r88;
        r14 = r89;
        r1 = r0;
        r53 = r53;
        r9 = r9;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x0e0d, code lost:
        r0 = e;
        r53 = r53;
        r8 = r8;
        r7 = r7;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x0e32, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x0e33, code lost:
        r43 = r88;
        r14 = r89;
        r1 = r0;
        r53 = r35;
        r35 = -1;
        r8 = r8;
        r7 = r7;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x0e43, code lost:
        r0 = e;
        r53 = r53;
        r8 = r8;
        r7 = r7;
        r9 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x0e46, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x0e47, code lost:
        r9 = r81;
        r53 = r53;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x0e4b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x0e4c, code lost:
        r9 = r81;
        r11 = r84;
        r53 = r53;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x0eaf, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x0eb0, code lost:
        r53 = r53;
        r9 = r9;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x01f9, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x0ee4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x01fa, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x01fb, code lost:
        r6 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x0var_, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x0var_, code lost:
        r2 = r65;
        r53 = r53;
        r9 = r9;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x0f3b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x0f3c, code lost:
        r9 = r81;
        r11 = r84;
        r53 = r53;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x0f8d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x0f8e, code lost:
        r9 = r81;
        r53 = r4;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x0var_, code lost:
        r2 = r65;
        r53 = r53;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x0var_, code lost:
        r53 = r53;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x0fb4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x0fb5, code lost:
        r43 = r88;
        r14 = r89;
        r1 = r0;
        r65 = r2;
        r53 = r53;
        r9 = r9;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x0fbe, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x0fc0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:897:0x0fc1, code lost:
        r10 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x0fc3, code lost:
        r53 = r4;
        r66 = r9;
        r2 = r65;
        r9 = r81;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:899:0x0fcc, code lost:
        r43 = r88;
        r14 = r89;
        r1 = r0;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:0x0fd3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x0fd4, code lost:
        r10 = r82;
        r88 = r9;
        r9 = r13;
        r89 = r14;
        r2 = r65;
        r43 = r88;
        r1 = r0;
        r53 = r4;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x0fe7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:0x0fe8, code lost:
        r10 = r82;
        r88 = r9;
        r9 = r13;
        r89 = r14;
        r2 = r65;
        r43 = r88;
        r1 = r0;
        r53 = r4;
        r35 = r25;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:0x0ffb, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x0ffc, code lost:
        r10 = r82;
        r11 = r84;
        r4 = r86;
        r9 = r13;
        r89 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:906:0x1006, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:0x1007, code lost:
        r10 = r82;
        r11 = r84;
        r4 = r86;
        r9 = r13;
        r89 = r14;
        r3 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x1012, code lost:
        r2 = r65;
        r1 = r0;
        r53 = r4;
        r35 = r25;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:909:0x101b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:910:0x101c, code lost:
        r10 = r82;
        r11 = r84;
        r4 = r86;
        r9 = r13;
        r89 = r14;
        r3 = r60;
        r2 = r65;
        r1 = r0;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:0x102c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:0x102d, code lost:
        r11 = r84;
        r3 = r1;
        r2 = r4;
        r10 = r5;
        r9 = r13;
        r66 = r26;
        r4 = r86;
        r25 = r88;
        r89 = r14;
        r1 = r0;
        r65 = r2;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:913:0x103f, code lost:
        r53 = r4;
        r35 = r25;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:0x1044, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:0x1045, code lost:
        r11 = r84;
        r10 = r5;
        r9 = r13;
        r66 = r26;
        r89 = r14;
        r1 = r0;
        r65 = r4;
        r53 = r86;
        r35 = r88;
        r3 = null;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x105b, code lost:
        r22 = null;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:917:0x105d, code lost:
        r43 = null;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:920:0x1076, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x1077, code lost:
        r11 = r84;
        r4 = r86;
        r9 = r13;
        r66 = r26;
        r25 = r88;
        r89 = r14;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x1084, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x1085, code lost:
        r11 = r84;
        r4 = r86;
        r9 = r13;
        r66 = r26;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x108e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x108f, code lost:
        r11 = r4;
        r9 = r13;
        r66 = r26;
        r4 = r86;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x1096, code lost:
        r25 = r88;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x1099, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:928:0x109a, code lost:
        r11 = r4;
        r9 = r13;
        r66 = r26;
        r4 = r86;
        r25 = r7;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:0x10a3, code lost:
        r1 = r0;
        r53 = r4;
        r35 = r25;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x10a9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x10aa, code lost:
        r11 = r84;
        r66 = r5;
        r9 = r13;
        r14 = r82;
        r35 = r88;
        r1 = r0;
        r53 = r86;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:932:0x10b9, code lost:
        r3 = null;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x10ba, code lost:
        r10 = null;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x10c7, code lost:
        r2 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x111a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:957:0x111c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:0x111d, code lost:
        r8 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x1120, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x1121, code lost:
        r8 = r79;
        r7 = r80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:0x1125, code lost:
        r1 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:962:0x1128, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:0x1129, code lost:
        r8 = r79;
        r7 = r80;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:967:0x114e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:968:0x114f, code lost:
        r2 = r0;
        r1 = r38;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:987:0x1181, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:988:0x1182, code lost:
        r2 = r0;
        r1 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:989:0x1186, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:990:0x1187, code lost:
        r9 = r10;
        r7 = r11;
        r8 = r12;
        r11 = r84;
        r14 = r82;
        r35 = r88;
        r2 = r0;
        r53 = r86;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x1196, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:993:?, code lost:
        org.telegram.messenger.FileLog.e("bitrate: " + r14 + " framerate: " + r9 + " size: " + r7 + "x" + r8);
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:994:0x11c6, code lost:
        r2 = r15.extractor;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:995:0x11c8, code lost:
        if (r2 != null) goto L_0x11ca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:996:0x11ca, code lost:
        r2.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:997:0x11cd, code lost:
        r2 = r15.mediaMuxer;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:998:0x11cf, code lost:
        if (r2 != null) goto L_0x11d1;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1005:0x11de  */
    /* JADX WARNING: Removed duplicated region for block: B:1007:0x1208 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:1012:0x1211  */
    /* JADX WARNING: Removed duplicated region for block: B:1015:0x1218 A[SYNTHETIC, Splitter:B:1015:0x1218] */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x03cf A[Catch:{ Exception -> 0x042f, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x03d3 A[Catch:{ Exception -> 0x042f, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x03e0 A[Catch:{ Exception -> 0x042f, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x03f8 A[Catch:{ Exception -> 0x042f, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x04e2 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0521 A[SYNTHETIC, Splitter:B:264:0x0521] */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0535 A[Catch:{ Exception -> 0x0525, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x053a A[Catch:{ Exception -> 0x0525, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x063d  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x06e5  */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x0704  */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x070e A[SYNTHETIC, Splitter:B:379:0x070e] */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x074c A[SYNTHETIC, Splitter:B:388:0x074c] */
    /* JADX WARNING: Removed duplicated region for block: B:416:0x07e9 A[SYNTHETIC, Splitter:B:416:0x07e9] */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0809  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x080f A[SYNTHETIC, Splitter:B:425:0x080f] */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x083d  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0840  */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x08e6  */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0904  */
    /* JADX WARNING: Removed duplicated region for block: B:490:0x0912  */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x0915  */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0936 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:503:0x0953 A[SYNTHETIC, Splitter:B:503:0x0953] */
    /* JADX WARNING: Removed duplicated region for block: B:509:0x0969 A[SYNTHETIC, Splitter:B:509:0x0969] */
    /* JADX WARNING: Removed duplicated region for block: B:594:0x0ac3  */
    /* JADX WARNING: Removed duplicated region for block: B:598:0x0ae5 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:603:0x0b08  */
    /* JADX WARNING: Removed duplicated region for block: B:604:0x0b13  */
    /* JADX WARNING: Removed duplicated region for block: B:610:0x0b24  */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b3d  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0CLASSNAME A[Catch:{ Exception -> 0x0da4, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:747:0x0d37 A[Catch:{ Exception -> 0x0f3b, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:749:0x0d55 A[Catch:{ Exception -> 0x0f3b, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:820:0x0e6d  */
    /* JADX WARNING: Removed duplicated region for block: B:821:0x0e70  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x0e75  */
    /* JADX WARNING: Removed duplicated region for block: B:841:0x0eb6 A[Catch:{ Exception -> 0x0eaf, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:842:0x0eb9 A[Catch:{ Exception -> 0x0eaf, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x0ece  */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x0efe A[Catch:{ Exception -> 0x0fb4, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:871:0x0var_ A[Catch:{ Exception -> 0x0fb4, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:938:0x10c5 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:953:0x110b A[Catch:{ Exception -> 0x114e, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:964:0x1130 A[Catch:{ Exception -> 0x114e, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:966:0x114a A[Catch:{ Exception -> 0x114e, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:970:0x1155 A[Catch:{ Exception -> 0x114e, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:972:0x115a A[Catch:{ Exception -> 0x114e, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:974:0x1162 A[Catch:{ Exception -> 0x114e, all -> 0x1181 }] */
    /* JADX WARNING: Removed duplicated region for block: B:979:0x116f  */
    /* JADX WARNING: Removed duplicated region for block: B:982:0x1176 A[SYNTHETIC, Splitter:B:982:0x1176] */
    /* JADX WARNING: Removed duplicated region for block: B:987:0x1181 A[ExcHandler: all (r0v4 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:1:0x0017] */
    /* JADX WARNING: Removed duplicated region for block: B:996:0x11ca  */
    /* JADX WARNING: Removed duplicated region for block: B:999:0x11d1 A[SYNTHETIC, Splitter:B:999:0x11d1] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:33:0x00e0=Splitter:B:33:0x00e0, B:542:0x0a00=Splitter:B:542:0x0a00, B:935:0x10c1=Splitter:B:935:0x10c1, B:600:0x0b03=Splitter:B:600:0x0b03, B:740:0x0d24=Splitter:B:740:0x0d24, B:59:0x0186=Splitter:B:59:0x0186, B:10:0x0067=Splitter:B:10:0x0067, B:254:0x04e7=Splitter:B:254:0x04e7, B:699:0x0CLASSNAME=Splitter:B:699:0x0CLASSNAME, B:196:0x03c9=Splitter:B:196:0x03c9, B:376:0x070a=Splitter:B:376:0x070a, B:397:0x076c=Splitter:B:397:0x076c, B:499:0x094c=Splitter:B:499:0x094c, B:433:0x0831=Splitter:B:433:0x0831, B:492:0x0917=Splitter:B:492:0x0917, B:71:0x01be=Splitter:B:71:0x01be, B:248:0x04de=Splitter:B:248:0x04de, B:855:0x0edf=Splitter:B:855:0x0edf} */
    /* JADX WARNING: Unknown variable types count: 1 */
    @android.annotation.TargetApi(18)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideoInternal(java.lang.String r75, java.io.File r76, int r77, boolean r78, int r79, int r80, int r81, int r82, int r83, long r84, long r86, long r88, long r90, boolean r92, boolean r93, org.telegram.messenger.MediaController.SavedFilterState r94, java.lang.String r95, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r96, boolean r97, org.telegram.messenger.MediaController.CropState r98) {
        /*
            r74 = this;
            r15 = r74
            r13 = r75
            r14 = r77
            r12 = r79
            r11 = r80
            r10 = r81
            r9 = r82
            r8 = r83
            r6 = r84
            r4 = r90
            r3 = r98
            r1 = 0
            android.media.MediaCodec$BufferInfo r6 = new android.media.MediaCodec$BufferInfo     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            r6.<init>()     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            org.telegram.messenger.video.Mp4Movie r7 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            r7.<init>()     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            r16 = r6
            r6 = r76
            r7.setCacheFile(r6)     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            r7.setRotation(r1)     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            r7.setSize(r12, r11)     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            org.telegram.messenger.video.MP4Builder r1 = new org.telegram.messenger.video.MP4Builder     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            r1.<init>()     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            r6 = r78
            org.telegram.messenger.video.MP4Builder r1 = r1.createMovie(r7, r6)     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            r15.mediaMuxer = r1     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            float r1 = (float) r4     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            r18 = 1148846080(0x447a0000, float:1000.0)
            float r19 = r1 / r18
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            java.lang.String r7 = "csd-1"
            java.lang.String r6 = "csd-0"
            r20 = r6
            java.lang.String r6 = "prepend-sps-pps-to-idr-frames"
            r23 = r6
            r22 = r7
            java.lang.String r7 = "video/avc"
            r30 = r7
            r6 = 0
            if (r97 == 0) goto L_0x0582
            int r32 = (r88 > r6 ? 1 : (r88 == r6 ? 0 : -1))
            if (r32 < 0) goto L_0x0062
            r1 = 1560000(0x17cdc0, float:2.186026E-39)
            r9 = 1560000(0x17cdc0, float:2.186026E-39)
            goto L_0x0067
        L_0x0062:
            if (r9 > 0) goto L_0x0067
            r9 = 921600(0xe1000, float:1.291437E-39)
        L_0x0067:
            int r1 = r12 % 16
            r32 = 1098907648(0x41800000, float:16.0)
            if (r1 == 0) goto L_0x00a9
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            if (r1 == 0) goto L_0x0096
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            r1.<init>()     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            java.lang.String r2 = "changing width from "
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            r1.append(r12)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            java.lang.String r2 = " to "
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            float r2 = (float) r12     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            float r2 = r2 / r32
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            int r2 = r2 * 16
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
        L_0x0096:
            float r1 = (float) r12     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            float r1 = r1 / r32
            int r1 = java.lang.Math.round(r1)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            int r1 = r1 * 16
            r12 = r1
            goto L_0x00a9
        L_0x00a1:
            r0 = move-exception
            r1 = r0
            r50 = r9
        L_0x00a5:
            r6 = 0
        L_0x00a6:
            r13 = 0
            goto L_0x04da
        L_0x00a9:
            int r1 = r11 % 16
            if (r1 == 0) goto L_0x00e0
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            if (r1 == 0) goto L_0x00d6
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            r1.<init>()     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            java.lang.String r2 = "changing height from "
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            r1.append(r11)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            java.lang.String r2 = " to "
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            float r2 = (float) r11     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            float r2 = r2 / r32
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            int r2 = r2 * 16
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
        L_0x00d6:
            float r1 = (float) r11     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            float r1 = r1 / r32
            int r1 = java.lang.Math.round(r1)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            int r1 = r1 * 16
            r11 = r1
        L_0x00e0:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x04cc, all -> 0x1181 }
            if (r1 == 0) goto L_0x0108
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            r1.<init>()     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            java.lang.String r2 = "create photo encoder "
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            r1.append(r12)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            java.lang.String r2 = " "
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            r1.append(r11)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            java.lang.String r2 = " duration = "
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            r1.append(r4)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x00a1, all -> 0x1181 }
        L_0x0108:
            r2 = r30
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r2, r12, r11)     // Catch:{ Exception -> 0x04cc, all -> 0x1181 }
            java.lang.String r6 = "color-format"
            r7 = 2130708361(0x7var_, float:1.701803E38)
            r1.setInteger(r6, r7)     // Catch:{ Exception -> 0x04cc, all -> 0x1181 }
            java.lang.String r6 = "bitrate"
            r1.setInteger(r6, r9)     // Catch:{ Exception -> 0x04cc, all -> 0x1181 }
            java.lang.String r6 = "frame-rate"
            r1.setInteger(r6, r10)     // Catch:{ Exception -> 0x04cc, all -> 0x1181 }
            java.lang.String r6 = "i-frame-interval"
            r7 = 2
            r1.setInteger(r6, r7)     // Catch:{ Exception -> 0x04cc, all -> 0x1181 }
            android.media.MediaCodec r6 = android.media.MediaCodec.createEncoderByType(r2)     // Catch:{ Exception -> 0x04cc, all -> 0x1181 }
            r30 = r2
            r2 = 1
            r7 = 0
            r6.configure(r1, r7, r7, r2)     // Catch:{ Exception -> 0x04c3, all -> 0x1181 }
            org.telegram.messenger.video.InputSurface r1 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x04c3, all -> 0x1181 }
            android.view.Surface r2 = r6.createInputSurface()     // Catch:{ Exception -> 0x04c3, all -> 0x1181 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x04c3, all -> 0x1181 }
            r1.makeCurrent()     // Catch:{ Exception -> 0x04b6, all -> 0x1181 }
            r6.start()     // Catch:{ Exception -> 0x04b6, all -> 0x1181 }
            org.telegram.messenger.video.OutputSurface r31 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x04b6, all -> 0x1181 }
            r32 = 0
            float r2 = (float) r10
            r36 = 1
            r37 = r1
            r1 = r31
            r17 = r2
            r21 = r30
            r2 = r94
            r3 = r75
            r4 = r95
            r5 = r96
            r7 = r6
            r14 = r16
            r13 = 21
            r6 = r32
            r79 = r7
            r49 = r21
            r48 = r22
            r7 = r12
            r8 = r11
            r50 = r9
            r9 = r77
            r10 = r17
            r51 = r11
            r11 = r36
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x04ad, all -> 0x1181 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x04a3, all -> 0x1181 }
            if (r1 >= r13) goto L_0x0185
            java.nio.ByteBuffer[] r6 = r79.getOutputBuffers()     // Catch:{ Exception -> 0x017c, all -> 0x1181 }
            goto L_0x0186
        L_0x017c:
            r0 = move-exception
            r6 = r79
            r1 = r0
        L_0x0180:
            r11 = r51
        L_0x0182:
            r13 = 0
            goto L_0x04de
        L_0x0185:
            r6 = 0
        L_0x0186:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x04a3, all -> 0x1181 }
            r7 = r6
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = -5
            r6 = 1
        L_0x0190:
            if (r1 != 0) goto L_0x0493
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x04a3, all -> 0x1181 }
            r8 = r2 ^ 1
            r9 = r7
            r7 = r5
            r5 = r3
            r3 = 1
            r72 = r2
            r2 = r1
            r1 = r8
            r8 = r6
            r6 = r4
            r4 = r72
        L_0x01a3:
            if (r1 != 0) goto L_0x01b0
            if (r3 == 0) goto L_0x01a8
            goto L_0x01b0
        L_0x01a8:
            r1 = r2
            r2 = r4
            r3 = r5
            r4 = r6
            r5 = r7
            r6 = r8
            r7 = r9
            goto L_0x0190
        L_0x01b0:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x04a3, all -> 0x1181 }
            if (r93 == 0) goto L_0x01ba
            r10 = 22000(0x55f0, double:1.08694E-319)
            r13 = r79
            goto L_0x01be
        L_0x01ba:
            r13 = r79
            r10 = 2500(0x9c4, double:1.235E-320)
        L_0x01be:
            int r10 = r13.dequeueOutputBuffer(r14, r10)     // Catch:{ Exception -> 0x0491, all -> 0x1181 }
            r11 = -1
            if (r10 != r11) goto L_0x01da
            r79 = r1
            r38 = r2
            r17 = r5
            r11 = r20
            r3 = r48
            r2 = r51
            r1 = -1
            r5 = 0
            r20 = r6
            r6 = r9
        L_0x01d6:
            r9 = r49
            goto L_0x03de
        L_0x01da:
            r11 = -3
            if (r10 != r11) goto L_0x01fd
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r79 = r1
            r1 = 21
            if (r11 >= r1) goto L_0x01e9
            java.nio.ByteBuffer[] r9 = r13.getOutputBuffers()     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
        L_0x01e9:
            r38 = r2
            r17 = r5
            r11 = r20
            r2 = r51
            r1 = -1
            r5 = r3
            r20 = r6
            r6 = r9
            r3 = r48
            goto L_0x01d6
        L_0x01f9:
            r0 = move-exception
        L_0x01fa:
            r1 = r0
        L_0x01fb:
            r6 = r13
            goto L_0x0180
        L_0x01fd:
            r79 = r1
            r1 = -2
            if (r10 != r1) goto L_0x027a
            android.media.MediaFormat r1 = r13.getOutputFormat()     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            if (r11 == 0) goto L_0x0221
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r11.<init>()     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r80 = r2
            java.lang.String r2 = "photo encoder new format "
            r11.append(r2)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r11.append(r1)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            java.lang.String r2 = r11.toString()     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            goto L_0x0223
        L_0x0221:
            r80 = r2
        L_0x0223:
            r2 = -5
            if (r7 != r2) goto L_0x0267
            if (r1 == 0) goto L_0x0267
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r11 = 0
            int r7 = r7.addTrack(r1, r11)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r82 = r3
            r3 = r23
            boolean r16 = r1.containsKey(r3)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            if (r16 == 0) goto L_0x025f
            int r11 = r1.getInteger(r3)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r2 = 1
            if (r11 != r2) goto L_0x0258
            r11 = r20
            java.nio.ByteBuffer r5 = r1.getByteBuffer(r11)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r23 = r3
            r3 = r48
            java.nio.ByteBuffer r1 = r1.getByteBuffer(r3)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            int r5 = r5.limit()     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            int r1 = r1.limit()     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            int r5 = r5 + r1
            goto L_0x026a
        L_0x0258:
            r23 = r3
            r11 = r20
            r3 = r48
            goto L_0x026a
        L_0x025f:
            r23 = r3
        L_0x0261:
            r11 = r20
            r3 = r48
            r2 = 1
            goto L_0x026a
        L_0x0267:
            r82 = r3
            goto L_0x0261
        L_0x026a:
            r38 = r80
            r17 = r5
            r20 = r6
            r6 = r9
            r9 = r49
            r2 = r51
            r1 = -1
            r5 = r82
            goto L_0x03de
        L_0x027a:
            r82 = r3
            r11 = r20
            r3 = r48
            r2 = 1
            if (r10 < 0) goto L_0x0474
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0491, all -> 0x1181 }
            r2 = 21
            if (r1 >= r2) goto L_0x028c
            r1 = r9[r10]     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            goto L_0x0290
        L_0x028c:
            java.nio.ByteBuffer r1 = r13.getOutputBuffer(r10)     // Catch:{ Exception -> 0x0491, all -> 0x1181 }
        L_0x0290:
            if (r1 == 0) goto L_0x0456
            int r2 = r14.size     // Catch:{ Exception -> 0x0454, all -> 0x1181 }
            r80 = r9
            r9 = 1
            if (r2 <= r9) goto L_0x03bf
            int r2 = r14.flags     // Catch:{ Exception -> 0x03b7, all -> 0x1181 }
            r9 = 2
            r2 = r2 & r9
            if (r2 != 0) goto L_0x033a
            if (r5 == 0) goto L_0x02b3
            int r2 = r14.flags     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r16 = 1
            r2 = r2 & 1
            if (r2 == 0) goto L_0x02b3
            int r2 = r14.offset     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            int r2 = r2 + r5
            r14.offset = r2     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            int r2 = r14.size     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            int r2 = r2 - r5
            r14.size = r2     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
        L_0x02b3:
            if (r8 == 0) goto L_0x0308
            int r2 = r14.flags     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r16 = 1
            r2 = r2 & 1
            if (r2 == 0) goto L_0x0308
            int r2 = r14.size     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r8 = 100
            if (r2 <= r8) goto L_0x0307
            int r2 = r14.offset     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r1.position(r2)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            byte[] r2 = new byte[r8]     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r1.get(r2)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r8 = 0
            r16 = 0
        L_0x02d0:
            r9 = 96
            if (r8 >= r9) goto L_0x0307
            byte r9 = r2[r8]     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            if (r9 != 0) goto L_0x02ff
            int r9 = r8 + 1
            byte r9 = r2[r9]     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            if (r9 != 0) goto L_0x02ff
            int r9 = r8 + 2
            byte r9 = r2[r9]     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            if (r9 != 0) goto L_0x02ff
            int r9 = r8 + 3
            byte r9 = r2[r9]     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r17 = r2
            r2 = 1
            if (r9 != r2) goto L_0x0301
            int r9 = r16 + 1
            if (r9 <= r2) goto L_0x02fc
            int r2 = r14.offset     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            int r2 = r2 + r8
            r14.offset = r2     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            int r2 = r14.size     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            int r2 = r2 - r8
            r14.size = r2     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            goto L_0x0307
        L_0x02fc:
            r16 = r9
            goto L_0x0301
        L_0x02ff:
            r17 = r2
        L_0x0301:
            int r8 = r8 + 1
            r2 = r17
            r9 = 2
            goto L_0x02d0
        L_0x0307:
            r8 = 0
        L_0x0308:
            r9 = 100
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r9 = 1
            long r1 = r2.writeSampleData(r7, r1, r14, r9)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r16 = r8
            r8 = 0
            int r17 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r17 == 0) goto L_0x032e
            org.telegram.messenger.MediaController$VideoConvertorListener r8 = r15.callback     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            if (r8 == 0) goto L_0x032e
            org.telegram.messenger.MediaController$VideoConvertorListener r8 = r15.callback     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r17 = r5
            r20 = r6
            r5 = 0
            float r9 = (float) r5     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            float r9 = r9 / r18
            float r9 = r9 / r19
            r8.didWriteData(r1, r9)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            goto L_0x0332
        L_0x032e:
            r17 = r5
            r20 = r6
        L_0x0332:
            r8 = r16
            r9 = r49
            r2 = r51
            goto L_0x03c9
        L_0x033a:
            r17 = r5
            r20 = r6
            r6 = r8
            r2 = -5
            if (r7 != r2) goto L_0x03c4
            int r5 = r14.size     // Catch:{ Exception -> 0x03b7, all -> 0x1181 }
            byte[] r5 = new byte[r5]     // Catch:{ Exception -> 0x03b7, all -> 0x1181 }
            int r7 = r14.offset     // Catch:{ Exception -> 0x03b7, all -> 0x1181 }
            int r8 = r14.size     // Catch:{ Exception -> 0x03b7, all -> 0x1181 }
            int r7 = r7 + r8
            r1.limit(r7)     // Catch:{ Exception -> 0x03b7, all -> 0x1181 }
            int r7 = r14.offset     // Catch:{ Exception -> 0x03b7, all -> 0x1181 }
            r1.position(r7)     // Catch:{ Exception -> 0x03b7, all -> 0x1181 }
            r1.get(r5)     // Catch:{ Exception -> 0x03b7, all -> 0x1181 }
            int r1 = r14.size     // Catch:{ Exception -> 0x03b7, all -> 0x1181 }
            r7 = 1
            int r1 = r1 - r7
        L_0x035a:
            if (r1 < 0) goto L_0x0398
            r9 = 3
            if (r1 <= r9) goto L_0x0398
            byte r8 = r5[r1]     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            if (r8 != r7) goto L_0x0393
            int r7 = r1 + -1
            byte r7 = r5[r7]     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            if (r7 != 0) goto L_0x0393
            int r7 = r1 + -2
            byte r7 = r5[r7]     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            if (r7 != 0) goto L_0x0393
            int r7 = r1 + -3
            byte r8 = r5[r7]     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            if (r8 != 0) goto L_0x0393
            java.nio.ByteBuffer r1 = java.nio.ByteBuffer.allocate(r7)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            int r8 = r14.size     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            int r8 = r8 - r7
            java.nio.ByteBuffer r8 = java.nio.ByteBuffer.allocate(r8)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r2 = 0
            java.nio.ByteBuffer r9 = r1.put(r5, r2, r7)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r9.position(r2)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            int r9 = r14.size     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            int r9 = r9 - r7
            java.nio.ByteBuffer r5 = r8.put(r5, r7, r9)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            r5.position(r2)     // Catch:{ Exception -> 0x01f9, all -> 0x1181 }
            goto L_0x039a
        L_0x0393:
            int r1 = r1 + -1
            r2 = -5
            r7 = 1
            goto L_0x035a
        L_0x0398:
            r1 = 0
            r8 = 0
        L_0x039a:
            r9 = r49
            r2 = r51
            android.media.MediaFormat r5 = android.media.MediaFormat.createVideoFormat(r9, r12, r2)     // Catch:{ Exception -> 0x03b5, all -> 0x1181 }
            if (r1 == 0) goto L_0x03ac
            if (r8 == 0) goto L_0x03ac
            r5.setByteBuffer(r11, r1)     // Catch:{ Exception -> 0x03b5, all -> 0x1181 }
            r5.setByteBuffer(r3, r8)     // Catch:{ Exception -> 0x03b5, all -> 0x1181 }
        L_0x03ac:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ Exception -> 0x03b5, all -> 0x1181 }
            r7 = 0
            int r1 = r1.addTrack(r5, r7)     // Catch:{ Exception -> 0x03b5, all -> 0x1181 }
            r7 = r1
            goto L_0x03c8
        L_0x03b5:
            r0 = move-exception
            goto L_0x03ba
        L_0x03b7:
            r0 = move-exception
            r2 = r51
        L_0x03ba:
            r1 = r0
            r11 = r2
            r6 = r13
            goto L_0x0182
        L_0x03bf:
            r17 = r5
            r20 = r6
            r6 = r8
        L_0x03c4:
            r9 = r49
            r2 = r51
        L_0x03c8:
            r8 = r6
        L_0x03c9:
            int r1 = r14.flags     // Catch:{ Exception -> 0x042f, all -> 0x1181 }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x03d3
            r1 = 0
            r38 = 1
            goto L_0x03d6
        L_0x03d3:
            r1 = 0
            r38 = 0
        L_0x03d6:
            r13.releaseOutputBuffer(r10, r1)     // Catch:{ Exception -> 0x042f, all -> 0x1181 }
            r6 = r80
            r5 = r82
            r1 = -1
        L_0x03de:
            if (r10 == r1) goto L_0x03f8
            r1 = r79
            r51 = r2
            r48 = r3
            r3 = r5
            r49 = r9
            r79 = r13
            r5 = r17
            r2 = r38
            r13 = 21
            r9 = r6
            r6 = r20
            r20 = r11
            goto L_0x01a3
        L_0x03f8:
            if (r4 != 0) goto L_0x0436
            r31.drawImage()     // Catch:{ Exception -> 0x042f, all -> 0x1181 }
            r1 = r20
            float r10 = (float) r1
            r20 = 1106247680(0x41var_, float:30.0)
            float r10 = r10 / r20
            float r10 = r10 * r18
            float r10 = r10 * r18
            float r10 = r10 * r18
            r51 = r2
            r48 = r3
            long r2 = (long) r10
            r10 = r37
            r10.setPresentationTime(r2)     // Catch:{ Exception -> 0x0429, all -> 0x1181 }
            r10.swapBuffers()     // Catch:{ Exception -> 0x0429, all -> 0x1181 }
            int r1 = r1 + 1
            float r2 = (float) r1     // Catch:{ Exception -> 0x0429, all -> 0x1181 }
            r3 = 1106247680(0x41var_, float:30.0)
            float r3 = r3 * r19
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 < 0) goto L_0x043e
            r13.signalEndOfInputStream()     // Catch:{ Exception -> 0x0429, all -> 0x1181 }
            r2 = r1
            r1 = 0
            r4 = 1
            goto L_0x0441
        L_0x0429:
            r0 = move-exception
            r1 = r0
            r37 = r10
            goto L_0x01fb
        L_0x042f:
            r0 = move-exception
            r51 = r2
        L_0x0432:
            r10 = r37
            goto L_0x01fa
        L_0x0436:
            r51 = r2
            r48 = r3
            r1 = r20
            r10 = r37
        L_0x043e:
            r2 = r1
            r1 = r79
        L_0x0441:
            r3 = r5
            r49 = r9
            r37 = r10
            r20 = r11
            r79 = r13
            r5 = r17
            r13 = 21
            r9 = r6
            r6 = r2
            r2 = r38
            goto L_0x01a3
        L_0x0454:
            r0 = move-exception
            goto L_0x0432
        L_0x0456:
            r1 = r37
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x048d, all -> 0x1181 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d, all -> 0x1181 }
            r3.<init>()     // Catch:{ Exception -> 0x048d, all -> 0x1181 }
            java.lang.String r4 = "encoderOutputBuffer "
            r3.append(r4)     // Catch:{ Exception -> 0x048d, all -> 0x1181 }
            r3.append(r10)     // Catch:{ Exception -> 0x048d, all -> 0x1181 }
            java.lang.String r4 = " was null"
            r3.append(r4)     // Catch:{ Exception -> 0x048d, all -> 0x1181 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x048d, all -> 0x1181 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x048d, all -> 0x1181 }
            throw r2     // Catch:{ Exception -> 0x048d, all -> 0x1181 }
        L_0x0474:
            r1 = r37
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x048d, all -> 0x1181 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d, all -> 0x1181 }
            r3.<init>()     // Catch:{ Exception -> 0x048d, all -> 0x1181 }
            java.lang.String r4 = "unexpected result from encoder.dequeueOutputBuffer: "
            r3.append(r4)     // Catch:{ Exception -> 0x048d, all -> 0x1181 }
            r3.append(r10)     // Catch:{ Exception -> 0x048d, all -> 0x1181 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x048d, all -> 0x1181 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x048d, all -> 0x1181 }
            throw r2     // Catch:{ Exception -> 0x048d, all -> 0x1181 }
        L_0x048d:
            r0 = move-exception
            r37 = r1
            goto L_0x04a8
        L_0x0491:
            r0 = move-exception
            goto L_0x04a6
        L_0x0493:
            r13 = r79
            r1 = r37
            r10 = r81
            r6 = r13
            r9 = r50
            r11 = r51
            r2 = 0
            r16 = 0
            goto L_0x051f
        L_0x04a3:
            r0 = move-exception
            r13 = r79
        L_0x04a6:
            r1 = r37
        L_0x04a8:
            r6 = r13
            r11 = r51
            r13 = 0
            goto L_0x04c1
        L_0x04ad:
            r0 = move-exception
            r13 = r79
            r1 = r37
            r6 = r13
            r11 = r51
            goto L_0x04be
        L_0x04b6:
            r0 = move-exception
            r13 = r6
            r50 = r9
            r51 = r11
            r37 = r1
        L_0x04be:
            r13 = 0
            r31 = 0
        L_0x04c1:
            r1 = r0
            goto L_0x04de
        L_0x04c3:
            r0 = move-exception
            r13 = r6
            r50 = r9
            r51 = r11
            r1 = r0
            goto L_0x00a6
        L_0x04cc:
            r0 = move-exception
            r50 = r9
            r51 = r11
            r1 = r0
            goto L_0x00a5
        L_0x04d4:
            r0 = move-exception
            r50 = r9
            r13 = 0
            r1 = r0
            r6 = 0
        L_0x04da:
            r31 = 0
            r37 = 0
        L_0x04de:
            boolean r2 = r1 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x0570, all -> 0x1181 }
            if (r2 == 0) goto L_0x04e6
            if (r93 != 0) goto L_0x04e6
            r2 = 1
            goto L_0x04e7
        L_0x04e6:
            r2 = 0
        L_0x04e7:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x055e, all -> 0x1181 }
            r3.<init>()     // Catch:{ Exception -> 0x055e, all -> 0x1181 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ Exception -> 0x055e, all -> 0x1181 }
            r9 = r50
            r3.append(r9)     // Catch:{ Exception -> 0x055c, all -> 0x1181 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ Exception -> 0x055c, all -> 0x1181 }
            r10 = r81
            r3.append(r10)     // Catch:{ Exception -> 0x0551, all -> 0x1181 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ Exception -> 0x0551, all -> 0x1181 }
            r3.append(r11)     // Catch:{ Exception -> 0x0551, all -> 0x1181 }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ Exception -> 0x0551, all -> 0x1181 }
            r3.append(r12)     // Catch:{ Exception -> 0x0551, all -> 0x1181 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0551, all -> 0x1181 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ Exception -> 0x0551, all -> 0x1181 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x0551, all -> 0x1181 }
            r16 = r2
            r1 = r37
            r2 = 1
        L_0x051f:
            if (r31 == 0) goto L_0x0533
            r31.release()     // Catch:{ Exception -> 0x0525, all -> 0x1181 }
            goto L_0x0533
        L_0x0525:
            r0 = move-exception
            r53 = r86
            r35 = r88
            r2 = r0
            r14 = r9
            r9 = r10
            r7 = r11
            r8 = r12
            r1 = r16
            goto L_0x057e
        L_0x0533:
            if (r1 == 0) goto L_0x0538
            r1.release()     // Catch:{ Exception -> 0x0525, all -> 0x1181 }
        L_0x0538:
            if (r6 == 0) goto L_0x0540
            r6.stop()     // Catch:{ Exception -> 0x0525, all -> 0x1181 }
            r6.release()     // Catch:{ Exception -> 0x0525, all -> 0x1181 }
        L_0x0540:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x0525, all -> 0x1181 }
            r53 = r86
            r35 = r88
            r14 = r9
            r9 = r10
            r7 = r11
            r8 = r12
            r1 = r16
            r11 = r84
            goto L_0x116b
        L_0x0551:
            r0 = move-exception
            r53 = r86
            r35 = r88
            r1 = r2
            r14 = r9
            r9 = r10
            r7 = r11
            r8 = r12
            goto L_0x056b
        L_0x055c:
            r0 = move-exception
            goto L_0x0561
        L_0x055e:
            r0 = move-exception
            r9 = r50
        L_0x0561:
            r53 = r86
            r35 = r88
            r1 = r2
            r14 = r9
            r7 = r11
            r8 = r12
            r9 = r81
        L_0x056b:
            r11 = r84
        L_0x056d:
            r2 = r0
            goto L_0x1197
        L_0x0570:
            r0 = move-exception
            r9 = r50
            r53 = r86
            r35 = r88
            r2 = r0
            r14 = r9
            r7 = r11
            r8 = r12
            r1 = 0
            r9 = r81
        L_0x057e:
            r11 = r84
            goto L_0x1197
        L_0x0582:
            r7 = r9
            r14 = r16
            r2 = r20
            r48 = r22
            r9 = r30
            r13 = 0
            android.media.MediaExtractor r3 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            r3.<init>()     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            r15.extractor = r3     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            r8 = r75
            r6 = r2
            r3.setDataSource(r8)     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            int r5 = org.telegram.messenger.MediaController.findTrack(r2, r13)     // Catch:{ Exception -> 0x1186, all -> 0x1181 }
            r2 = -1
            r4 = 2
            if (r7 == r2) goto L_0x05b7
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x05ab, all -> 0x1181 }
            r3 = 1
            int r2 = org.telegram.messenger.MediaController.findTrack(r2, r3)     // Catch:{ Exception -> 0x05ab, all -> 0x1181 }
            goto L_0x05b9
        L_0x05ab:
            r0 = move-exception
            r53 = r86
            r35 = r88
            r2 = r0
            r14 = r7
            r9 = r10
            r7 = r11
            r8 = r12
            r1 = 0
            goto L_0x057e
        L_0x05b7:
            r3 = 1
            r2 = -1
        L_0x05b9:
            java.lang.String r11 = "mime"
            if (r5 < 0) goto L_0x05dc
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x05cf, all -> 0x1181 }
            android.media.MediaFormat r3 = r3.getTrackFormat(r5)     // Catch:{ Exception -> 0x05cf, all -> 0x1181 }
            java.lang.String r3 = r3.getString(r11)     // Catch:{ Exception -> 0x05cf, all -> 0x1181 }
            boolean r3 = r3.equals(r9)     // Catch:{ Exception -> 0x05cf, all -> 0x1181 }
            if (r3 != 0) goto L_0x05dc
            r3 = 1
            goto L_0x05dd
        L_0x05cf:
            r0 = move-exception
            r53 = r86
            r35 = r88
            r2 = r0
            r14 = r7
            r9 = r10
            r8 = r12
            r1 = 0
            r7 = r80
            goto L_0x057e
        L_0x05dc:
            r3 = 0
        L_0x05dd:
            if (r92 != 0) goto L_0x0636
            if (r3 == 0) goto L_0x05e3
            goto L_0x0636
        L_0x05e3:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0625, all -> 0x1181 }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ Exception -> 0x0625, all -> 0x1181 }
            r1 = -1
            if (r7 == r1) goto L_0x05ed
            r16 = 1
            goto L_0x05ef
        L_0x05ed:
            r16 = 0
        L_0x05ef:
            r1 = r74
            r11 = 1
            r4 = r14
            r5 = r84
            r14 = r8
            r7 = r86
            r13 = r10
            r9 = r90
            r14 = 1
            r11 = r76
            r12 = r16
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ Exception -> 0x0614, all -> 0x1181 }
            r8 = r79
            r7 = r80
            r14 = r82
            r11 = r84
            r53 = r86
            r35 = r88
            r9 = r13
            r1 = 0
            r2 = 0
            goto L_0x116b
        L_0x0614:
            r0 = move-exception
            r8 = r79
            r7 = r80
            r14 = r82
            r11 = r84
            r53 = r86
            r35 = r88
            r2 = r0
            r9 = r13
            goto L_0x1196
        L_0x0625:
            r0 = move-exception
            r8 = r79
            r7 = r80
            r14 = r82
            r11 = r84
            r53 = r86
            r35 = r88
            r2 = r0
            r9 = r10
            goto L_0x1196
        L_0x0636:
            r12 = r8
            r13 = r10
            r10 = r14
            r3 = -1
            r14 = 1
            if (r5 < 0) goto L_0x1130
            r16 = -2147483648(0xfffffffvar_, double:NaN)
            r7 = 1000(0x3e8, float:1.401E-42)
            r20 = -1
            int r7 = r7 / r13
            int r7 = r7 * 1000
            long r7 = (long) r7     // Catch:{ Exception -> 0x10a9, all -> 0x1181 }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x10a9, all -> 0x1181 }
            r3.selectTrack(r5)     // Catch:{ Exception -> 0x10a9, all -> 0x1181 }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x10a9, all -> 0x1181 }
            android.media.MediaFormat r3 = r3.getTrackFormat(r5)     // Catch:{ Exception -> 0x10a9, all -> 0x1181 }
            r24 = 0
            int r22 = (r88 > r24 ? 1 : (r88 == r24 ? 0 : -1))
            if (r22 < 0) goto L_0x0664
            r22 = 1560000(0x17cdc0, float:2.186026E-39)
            r1 = r83
            r14 = 1560000(0x17cdc0, float:2.186026E-39)
            r24 = 0
            goto L_0x0674
        L_0x0664:
            if (r82 > 0) goto L_0x066e
            r1 = r83
            r24 = r88
            r14 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x0674
        L_0x066e:
            r14 = r82
            r1 = r83
            r24 = r88
        L_0x0674:
            if (r1 <= 0) goto L_0x068a
            int r14 = java.lang.Math.min(r1, r14)     // Catch:{ Exception -> 0x067b, all -> 0x1181 }
            goto L_0x068a
        L_0x067b:
            r0 = move-exception
            r11 = r84
            r53 = r86
            r1 = r0
            r66 = r5
            r9 = r13
            r35 = r24
        L_0x0686:
            r3 = 0
            r6 = 0
            goto L_0x10ba
        L_0x068a:
            r26 = r5
            r27 = 0
            r4 = r84
            int r30 = (r4 > r27 ? 1 : (r4 == r27 ? 0 : -1))
            if (r30 >= 0) goto L_0x0697
            r30 = r27
            goto L_0x0699
        L_0x0697:
            r30 = r4
        L_0x0699:
            int r32 = (r24 > r27 ? 1 : (r24 == r27 ? 0 : -1))
            if (r32 < 0) goto L_0x06a6
            int r32 = (r30 > r24 ? 1 : (r30 == r24 ? 0 : -1))
            if (r32 != 0) goto L_0x06a6
            r30 = r7
            r7 = r20
            goto L_0x06aa
        L_0x06a6:
            r30 = r7
            r7 = r24
        L_0x06aa:
            int r24 = (r7 > r27 ? 1 : (r7 == r27 ? 0 : -1))
            if (r24 < 0) goto L_0x06c8
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x06bd, all -> 0x1181 }
            r24 = r2
            r2 = 0
            r1.seekTo(r7, r2)     // Catch:{ Exception -> 0x06bd, all -> 0x1181 }
        L_0x06b6:
            r2 = r98
            r88 = r7
            r7 = 0
            goto L_0x06e3
        L_0x06bd:
            r0 = move-exception
            r53 = r86
            r1 = r0
            r11 = r4
            r35 = r7
        L_0x06c4:
            r9 = r13
            r66 = r26
            goto L_0x0686
        L_0x06c8:
            r24 = r2
            r1 = 0
            int r25 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r25 <= 0) goto L_0x06d7
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x06bd, all -> 0x1181 }
            r2 = 0
            r1.seekTo(r4, r2)     // Catch:{ Exception -> 0x06bd, all -> 0x1181 }
            goto L_0x06b6
        L_0x06d7:
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x1099, all -> 0x1181 }
            r88 = r7
            r2 = 0
            r7 = 0
            r1.seekTo(r7, r2)     // Catch:{ Exception -> 0x108e, all -> 0x1181 }
            r2 = r98
        L_0x06e3:
            if (r2 == 0) goto L_0x0704
            r1 = 90
            r12 = r77
            if (r12 == r1) goto L_0x06f5
            r1 = 270(0x10e, float:3.78E-43)
            if (r12 != r1) goto L_0x06f0
            goto L_0x06f5
        L_0x06f0:
            int r1 = r2.transformWidth     // Catch:{ Exception -> 0x06fc, all -> 0x1181 }
            int r7 = r2.transformHeight     // Catch:{ Exception -> 0x06fc, all -> 0x1181 }
            goto L_0x06f9
        L_0x06f5:
            int r1 = r2.transformHeight     // Catch:{ Exception -> 0x06fc, all -> 0x1181 }
            int r7 = r2.transformWidth     // Catch:{ Exception -> 0x06fc, all -> 0x1181 }
        L_0x06f9:
            r8 = r7
            r7 = r1
            goto L_0x070a
        L_0x06fc:
            r0 = move-exception
            r53 = r86
            r35 = r88
            r1 = r0
            r11 = r4
            goto L_0x06c4
        L_0x0704:
            r12 = r77
            r7 = r79
            r8 = r80
        L_0x070a:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x108e, all -> 0x1181 }
            if (r1 == 0) goto L_0x072a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06fc, all -> 0x1181 }
            r1.<init>()     // Catch:{ Exception -> 0x06fc, all -> 0x1181 }
            java.lang.String r2 = "create encoder with w = "
            r1.append(r2)     // Catch:{ Exception -> 0x06fc, all -> 0x1181 }
            r1.append(r7)     // Catch:{ Exception -> 0x06fc, all -> 0x1181 }
            java.lang.String r2 = " h = "
            r1.append(r2)     // Catch:{ Exception -> 0x06fc, all -> 0x1181 }
            r1.append(r8)     // Catch:{ Exception -> 0x06fc, all -> 0x1181 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x06fc, all -> 0x1181 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x06fc, all -> 0x1181 }
        L_0x072a:
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r9, r7, r8)     // Catch:{ Exception -> 0x108e, all -> 0x1181 }
            java.lang.String r2 = "color-format"
            r4 = 2130708361(0x7var_, float:1.701803E38)
            r1.setInteger(r2, r4)     // Catch:{ Exception -> 0x1084, all -> 0x1181 }
            java.lang.String r2 = "bitrate"
            r1.setInteger(r2, r14)     // Catch:{ Exception -> 0x1084, all -> 0x1181 }
            java.lang.String r2 = "frame-rate"
            r1.setInteger(r2, r13)     // Catch:{ Exception -> 0x1084, all -> 0x1181 }
            java.lang.String r2 = "i-frame-interval"
            r4 = 2
            r1.setInteger(r2, r4)     // Catch:{ Exception -> 0x1084, all -> 0x1181 }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1084, all -> 0x1181 }
            r5 = 23
            if (r2 >= r5) goto L_0x076c
            int r2 = java.lang.Math.min(r8, r7)     // Catch:{ Exception -> 0x0762, all -> 0x1181 }
            r5 = 480(0x1e0, float:6.73E-43)
            if (r2 > r5) goto L_0x076c
            r2 = 921600(0xe1000, float:1.291437E-39)
            if (r14 <= r2) goto L_0x075c
            r14 = 921600(0xe1000, float:1.291437E-39)
        L_0x075c:
            java.lang.String r2 = "bitrate"
            r1.setInteger(r2, r14)     // Catch:{ Exception -> 0x0762, all -> 0x1181 }
            goto L_0x076c
        L_0x0762:
            r0 = move-exception
            r11 = r84
            r53 = r86
            r35 = r88
            r1 = r0
            goto L_0x06c4
        L_0x076c:
            android.media.MediaCodec r5 = android.media.MediaCodec.createEncoderByType(r9)     // Catch:{ Exception -> 0x1076, all -> 0x1181 }
            r2 = 0
            r4 = 1
            r5.configure(r1, r2, r2, r4)     // Catch:{ Exception -> 0x1061, all -> 0x1181 }
            org.telegram.messenger.video.InputSurface r4 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x1061, all -> 0x1181 }
            android.view.Surface r1 = r5.createInputSurface()     // Catch:{ Exception -> 0x1061, all -> 0x1181 }
            r4.<init>(r1)     // Catch:{ Exception -> 0x1061, all -> 0x1181 }
            r4.makeCurrent()     // Catch:{ Exception -> 0x1044, all -> 0x1181 }
            r5.start()     // Catch:{ Exception -> 0x1044, all -> 0x1181 }
            java.lang.String r1 = r3.getString(r11)     // Catch:{ Exception -> 0x1044, all -> 0x1181 }
            android.media.MediaCodec r1 = android.media.MediaCodec.createDecoderByType(r1)     // Catch:{ Exception -> 0x1044, all -> 0x1181 }
            org.telegram.messenger.video.OutputSurface r22 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x102c, all -> 0x1181 }
            r25 = 0
            r27 = r3
            float r3 = (float) r13
            r28 = 0
            r60 = r1
            r1 = r22
            r61 = r24
            r2 = r94
            r64 = r23
            r62 = r27
            r63 = r48
            r23 = r3
            r3 = r25
            r65 = r4
            r24 = 2
            r4 = r95
            r82 = r5
            r66 = r26
            r5 = r96
            r67 = r6
            r6 = r98
            r25 = r88
            r68 = r7
            r29 = r30
            r31 = 0
            r7 = r79
            r69 = r8
            r8 = r80
            r70 = r9
            r9 = r77
            r71 = r10
            r10 = r23
            r12 = r11
            r11 = r28
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x101b, all -> 0x1181 }
            android.view.Surface r1 = r22.getSurface()     // Catch:{ Exception -> 0x1006, all -> 0x1181 }
            r3 = r60
            r2 = r62
            r4 = 0
            r5 = 0
            r3.configure(r2, r1, r4, r5)     // Catch:{ Exception -> 0x0ffb, all -> 0x1181 }
            r3.start()     // Catch:{ Exception -> 0x0ffb, all -> 0x1181 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ffb, all -> 0x1181 }
            r2 = 21
            if (r1 >= r2) goto L_0x0809
            java.nio.ByteBuffer[] r6 = r3.getInputBuffers()     // Catch:{ Exception -> 0x07f9, all -> 0x1181 }
            java.nio.ByteBuffer[] r1 = r82.getOutputBuffers()     // Catch:{ Exception -> 0x07f9, all -> 0x1181 }
            r2 = r61
            r72 = r6
            r6 = r1
            r1 = r72
            goto L_0x080d
        L_0x07f9:
            r0 = move-exception
            r10 = r82
            r11 = r84
            r53 = r86
            r1 = r0
            r43 = r4
        L_0x0803:
            r9 = r13
            r35 = r25
        L_0x0806:
            r6 = 0
            goto L_0x10c1
        L_0x0809:
            r1 = r4
            r6 = r1
            r2 = r61
        L_0x080d:
            if (r2 < 0) goto L_0x0904
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x08f2, all -> 0x1181 }
            android.media.MediaFormat r5 = r5.getTrackFormat(r2)     // Catch:{ Exception -> 0x08f2, all -> 0x1181 }
            java.lang.String r7 = r5.getString(r12)     // Catch:{ Exception -> 0x08f2, all -> 0x1181 }
            java.lang.String r8 = "audio/mp4a-latm"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x08f2, all -> 0x1181 }
            if (r7 != 0) goto L_0x0830
            java.lang.String r7 = r5.getString(r12)     // Catch:{ Exception -> 0x07f9, all -> 0x1181 }
            java.lang.String r8 = "audio/mpeg"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x07f9, all -> 0x1181 }
            if (r7 == 0) goto L_0x082e
            goto L_0x0830
        L_0x082e:
            r7 = 0
            goto L_0x0831
        L_0x0830:
            r7 = 1
        L_0x0831:
            java.lang.String r8 = r5.getString(r12)     // Catch:{ Exception -> 0x08f2, all -> 0x1181 }
            java.lang.String r9 = "audio/unknown"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x08f2, all -> 0x1181 }
            if (r8 == 0) goto L_0x083e
            r2 = -1
        L_0x083e:
            if (r2 < 0) goto L_0x08e6
            if (r7 == 0) goto L_0x0889
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x087f, all -> 0x1181 }
            r9 = 1
            int r8 = r8.addTrack(r5, r9)     // Catch:{ Exception -> 0x087f, all -> 0x1181 }
            android.media.MediaExtractor r9 = r15.extractor     // Catch:{ Exception -> 0x087f, all -> 0x1181 }
            r9.selectTrack(r2)     // Catch:{ Exception -> 0x087f, all -> 0x1181 }
            java.lang.String r9 = "max-input-size"
            int r5 = r5.getInteger(r9)     // Catch:{ Exception -> 0x087f, all -> 0x1181 }
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocateDirect(r5)     // Catch:{ Exception -> 0x087f, all -> 0x1181 }
            r11 = r84
            r9 = 0
            int r23 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r23 <= 0) goto L_0x0869
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x08a7, all -> 0x1181 }
            r9 = 0
            r4.seekTo(r11, r9)     // Catch:{ Exception -> 0x08a7, all -> 0x1181 }
            r88 = r5
            goto L_0x0873
        L_0x0869:
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x08a7, all -> 0x1181 }
            r88 = r5
            r5 = 0
            r9 = 0
            r4.seekTo(r9, r5)     // Catch:{ Exception -> 0x08a7, all -> 0x1181 }
        L_0x0873:
            r4 = r86
            r10 = r88
            r88 = r6
            r6 = r8
            r9 = 0
            r8 = r75
            goto L_0x0910
        L_0x087f:
            r0 = move-exception
            r10 = r82
            r11 = r84
        L_0x0884:
            r53 = r86
            r1 = r0
            goto L_0x08fe
        L_0x0889:
            r11 = r84
            android.media.MediaExtractor r4 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x08de, all -> 0x1181 }
            r4.<init>()     // Catch:{ Exception -> 0x08de, all -> 0x1181 }
            r8 = r75
            r4.setDataSource(r8)     // Catch:{ Exception -> 0x08dc, all -> 0x1181 }
            r4.selectTrack(r2)     // Catch:{ Exception -> 0x08dc, all -> 0x1181 }
            r9 = 0
            int r23 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r23 <= 0) goto L_0x08ab
            r9 = 0
            r4.seekTo(r11, r9)     // Catch:{ Exception -> 0x08a7, all -> 0x1181 }
            r88 = r6
            r89 = r7
            goto L_0x08b4
        L_0x08a7:
            r0 = move-exception
            r10 = r82
            goto L_0x0884
        L_0x08ab:
            r88 = r6
            r89 = r7
            r6 = r9
            r9 = 0
            r4.seekTo(r6, r9)     // Catch:{ Exception -> 0x08dc, all -> 0x1181 }
        L_0x08b4:
            org.telegram.messenger.video.AudioRecoder r6 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x08dc, all -> 0x1181 }
            r6.<init>(r5, r4, r2)     // Catch:{ Exception -> 0x08dc, all -> 0x1181 }
            r6.startTime = r11     // Catch:{ Exception -> 0x08d0, all -> 0x1181 }
            r4 = r86
            r6.endTime = r4     // Catch:{ Exception -> 0x08ce, all -> 0x1181 }
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ Exception -> 0x08ce, all -> 0x1181 }
            android.media.MediaFormat r9 = r6.format     // Catch:{ Exception -> 0x08ce, all -> 0x1181 }
            r10 = 1
            int r7 = r7.addTrack(r9, r10)     // Catch:{ Exception -> 0x08ce, all -> 0x1181 }
            r9 = r6
            r6 = r7
            r10 = 0
            r7 = r89
            goto L_0x0910
        L_0x08ce:
            r0 = move-exception
            goto L_0x08d3
        L_0x08d0:
            r0 = move-exception
            r4 = r86
        L_0x08d3:
            r10 = r82
            r1 = r0
            r53 = r4
            r43 = r6
            goto L_0x0803
        L_0x08dc:
            r0 = move-exception
            goto L_0x08e1
        L_0x08de:
            r0 = move-exception
            r8 = r75
        L_0x08e1:
            r4 = r86
            r10 = r82
            goto L_0x08fb
        L_0x08e6:
            r8 = r75
            r11 = r84
            r4 = r86
            r88 = r6
            r89 = r7
            r6 = -5
            goto L_0x090e
        L_0x08f2:
            r0 = move-exception
            r8 = r75
            r4 = r86
            r10 = r82
            r11 = r84
        L_0x08fb:
            r1 = r0
            r53 = r4
        L_0x08fe:
            r9 = r13
            r35 = r25
            r6 = 0
            goto L_0x105d
        L_0x0904:
            r8 = r75
            r11 = r84
            r4 = r86
            r88 = r6
            r6 = -5
            r7 = 1
        L_0x090e:
            r9 = 0
            r10 = 0
        L_0x0910:
            if (r2 >= 0) goto L_0x0915
            r23 = 1
            goto L_0x0917
        L_0x0915:
            r23 = 0
        L_0x0917:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x0fe7, all -> 0x1181 }
            r28 = r88
            r39 = r16
            r46 = r20
            r37 = r23
            r35 = r25
            r16 = 0
            r17 = 0
            r23 = 0
            r25 = 0
            r26 = -5
            r27 = 1
            r31 = 0
            r33 = 0
        L_0x0934:
            if (r16 == 0) goto L_0x094c
            if (r7 != 0) goto L_0x093b
            if (r37 != 0) goto L_0x093b
            goto L_0x094c
        L_0x093b:
            r8 = r79
            r7 = r80
            r6 = r82
            r1 = r3
            r53 = r4
            r43 = r9
            r9 = r13
            r2 = 0
            r38 = 0
            goto L_0x1102
        L_0x094c:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x0fd3, all -> 0x1181 }
            if (r7 != 0) goto L_0x0967
            if (r9 == 0) goto L_0x0967
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x095c, all -> 0x1181 }
            boolean r8 = r9.step(r8, r6)     // Catch:{ Exception -> 0x095c, all -> 0x1181 }
            r37 = r8
            goto L_0x0967
        L_0x095c:
            r0 = move-exception
            r10 = r82
            r1 = r0
            r53 = r4
            r43 = r9
            r9 = r13
            goto L_0x0806
        L_0x0967:
            if (r25 != 0) goto L_0x0ac3
            android.media.MediaExtractor r8 = r15.extractor     // Catch:{ Exception -> 0x0ab1, all -> 0x1181 }
            int r8 = r8.getSampleTrackIndex()     // Catch:{ Exception -> 0x0ab1, all -> 0x1181 }
            r88 = r9
            r9 = r66
            if (r8 != r9) goto L_0x09dc
            r89 = r14
            r13 = 2500(0x9c4, double:1.235E-320)
            int r8 = r3.dequeueInputBuffer(r13)     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            if (r8 < 0) goto L_0x09c0
            int r13 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            r14 = 21
            if (r13 >= r14) goto L_0x0988
            r13 = r1[r8]     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            goto L_0x098c
        L_0x0988:
            java.nio.ByteBuffer r13 = r3.getInputBuffer(r8)     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
        L_0x098c:
            android.media.MediaExtractor r14 = r15.extractor     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            r48 = r1
            r1 = 0
            int r56 = r14.readSampleData(r13, r1)     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            if (r56 >= 0) goto L_0x09a9
            r55 = 0
            r56 = 0
            r57 = 0
            r59 = 4
            r53 = r3
            r54 = r8
            r53.queueInputBuffer(r54, r55, r56, r57, r59)     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            r25 = 1
            goto L_0x09c2
        L_0x09a9:
            r55 = 0
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            long r57 = r1.getSampleTime()     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            r59 = 0
            r53 = r3
            r54 = r8
            r53.queueInputBuffer(r54, r55, r56, r57, r59)     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            r1.advance()     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            goto L_0x09c2
        L_0x09c0:
            r48 = r1
        L_0x09c2:
            r13 = r2
            r49 = r4
            r51 = r6
            r14 = r71
            goto L_0x0a89
        L_0x09cb:
            r0 = move-exception
            r10 = r82
        L_0x09ce:
            r43 = r88
            r14 = r89
            r1 = r0
            r53 = r4
            r66 = r9
        L_0x09d7:
            r6 = 0
            r9 = r81
            goto L_0x10c1
        L_0x09dc:
            r48 = r1
            r89 = r14
            if (r7 == 0) goto L_0x0a7d
            r1 = -1
            if (r2 == r1) goto L_0x0a75
            if (r8 != r2) goto L_0x0a75
            android.media.MediaExtractor r8 = r15.extractor     // Catch:{ Exception -> 0x0a71, all -> 0x1181 }
            r13 = 0
            int r8 = r8.readSampleData(r10, r13)     // Catch:{ Exception -> 0x0a71, all -> 0x1181 }
            r14 = r71
            r14.size = r8     // Catch:{ Exception -> 0x0a71, all -> 0x1181 }
            int r8 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a71, all -> 0x1181 }
            r1 = 21
            if (r8 >= r1) goto L_0x0a00
            r10.position(r13)     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            int r1 = r14.size     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            r10.limit(r1)     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
        L_0x0a00:
            int r1 = r14.size     // Catch:{ Exception -> 0x0a71, all -> 0x1181 }
            if (r1 < 0) goto L_0x0a15
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            r13 = r2
            long r1 = r1.getSampleTime()     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            r14.presentationTimeUs = r1     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            r1.advance()     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            r2 = r25
            goto L_0x0a1a
        L_0x0a15:
            r13 = r2
            r1 = 0
            r14.size = r1     // Catch:{ Exception -> 0x0a71, all -> 0x1181 }
            r2 = 1
        L_0x0a1a:
            int r1 = r14.size     // Catch:{ Exception -> 0x0a71, all -> 0x1181 }
            if (r1 <= 0) goto L_0x0a68
            r44 = 0
            int r1 = (r4 > r44 ? 1 : (r4 == r44 ? 0 : -1))
            if (r1 < 0) goto L_0x0a2d
            r86 = r2
            long r1 = r14.presentationTimeUs     // Catch:{ Exception -> 0x09cb, all -> 0x1181 }
            int r8 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r8 >= 0) goto L_0x0a6a
            goto L_0x0a2f
        L_0x0a2d:
            r86 = r2
        L_0x0a2f:
            r1 = 0
            r14.offset = r1     // Catch:{ Exception -> 0x0a71, all -> 0x1181 }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0a71, all -> 0x1181 }
            int r2 = r2.getSampleFlags()     // Catch:{ Exception -> 0x0a71, all -> 0x1181 }
            r14.flags = r2     // Catch:{ Exception -> 0x0a71, all -> 0x1181 }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0a71, all -> 0x1181 }
            r49 = r4
            long r4 = r2.writeSampleData(r6, r10, r14, r1)     // Catch:{ Exception -> 0x0aa3, all -> 0x1181 }
            r1 = 0
            int r8 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r8 == 0) goto L_0x0a6c
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r15.callback     // Catch:{ Exception -> 0x0aa3, all -> 0x1181 }
            if (r1 == 0) goto L_0x0a6c
            long r1 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0aa3, all -> 0x1181 }
            long r1 = r1 - r11
            int r8 = (r1 > r31 ? 1 : (r1 == r31 ? 0 : -1))
            if (r8 <= 0) goto L_0x0a57
            long r1 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0aa3, all -> 0x1181 }
            long r31 = r1 - r11
        L_0x0a57:
            r1 = r31
            org.telegram.messenger.MediaController$VideoConvertorListener r8 = r15.callback     // Catch:{ Exception -> 0x0aa3, all -> 0x1181 }
            r51 = r6
            float r6 = (float) r1     // Catch:{ Exception -> 0x0aa3, all -> 0x1181 }
            float r6 = r6 / r18
            float r6 = r6 / r19
            r8.didWriteData(r4, r6)     // Catch:{ Exception -> 0x0aa3, all -> 0x1181 }
            r31 = r1
            goto L_0x0a6e
        L_0x0a68:
            r86 = r2
        L_0x0a6a:
            r49 = r4
        L_0x0a6c:
            r51 = r6
        L_0x0a6e:
            r25 = r86
            goto L_0x0a89
        L_0x0a71:
            r0 = move-exception
            r49 = r4
            goto L_0x0aa4
        L_0x0a75:
            r13 = r2
            r49 = r4
            r51 = r6
            r14 = r71
            goto L_0x0a85
        L_0x0a7d:
            r13 = r2
            r49 = r4
            r51 = r6
            r14 = r71
            r1 = -1
        L_0x0a85:
            if (r8 != r1) goto L_0x0a89
            r2 = 1
            goto L_0x0a8a
        L_0x0a89:
            r2 = 0
        L_0x0a8a:
            if (r2 == 0) goto L_0x0ad2
            r1 = 2500(0x9c4, double:1.235E-320)
            int r54 = r3.dequeueInputBuffer(r1)     // Catch:{ Exception -> 0x0aa3, all -> 0x1181 }
            if (r54 < 0) goto L_0x0ad2
            r55 = 0
            r56 = 0
            r57 = 0
            r59 = 4
            r53 = r3
            r53.queueInputBuffer(r54, r55, r56, r57, r59)     // Catch:{ Exception -> 0x0aa3, all -> 0x1181 }
            r2 = 1
            goto L_0x0ad4
        L_0x0aa3:
            r0 = move-exception
        L_0x0aa4:
            r10 = r82
            r43 = r88
            r14 = r89
            r1 = r0
            r66 = r9
            r53 = r49
            goto L_0x09d7
        L_0x0ab1:
            r0 = move-exception
            r49 = r4
            r88 = r9
            r89 = r14
            r9 = r81
            r10 = r82
            r43 = r88
            r1 = r0
            r53 = r49
            goto L_0x0806
        L_0x0ac3:
            r48 = r1
            r13 = r2
            r49 = r4
            r51 = r6
            r88 = r9
            r89 = r14
            r9 = r66
            r14 = r71
        L_0x0ad2:
            r2 = r25
        L_0x0ad4:
            r1 = r17 ^ 1
            r25 = r2
            r6 = r26
            r4 = r49
            r2 = 1
            r72 = r39
            r39 = r7
            r7 = r72
        L_0x0ae3:
            if (r1 != 0) goto L_0x0b03
            if (r2 == 0) goto L_0x0ae8
            goto L_0x0b03
        L_0x0ae8:
            r26 = r6
            r66 = r9
            r2 = r13
            r71 = r14
            r1 = r48
            r6 = r51
            r13 = r81
            r9 = r88
            r14 = r89
            r72 = r7
            r8 = r75
            r7 = r39
            r39 = r72
            goto L_0x0934
        L_0x0b03:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x0fc0, all -> 0x1181 }
            if (r93 == 0) goto L_0x0b13
            r49 = 22000(0x55f0, double:1.08694E-319)
            r86 = r1
            r87 = r2
            r40 = r10
            r1 = r49
            goto L_0x0b1b
        L_0x0b13:
            r86 = r1
            r87 = r2
            r40 = r10
            r1 = 2500(0x9c4, double:1.235E-320)
        L_0x0b1b:
            r10 = r82
            int r1 = r10.dequeueOutputBuffer(r14, r1)     // Catch:{ Exception -> 0x0fbe, all -> 0x1181 }
            r2 = -1
            if (r1 != r2) goto L_0x0b3d
            r53 = r4
            r49 = r7
            r66 = r9
            r82 = r13
            r9 = r63
            r13 = r67
            r2 = r68
            r4 = r69
            r11 = r70
            r5 = 3
            r7 = 1
            r12 = -1
            r8 = r6
            r6 = 0
            goto L_0x0d35
        L_0x0b3d:
            r2 = -3
            if (r1 != r2) goto L_0x0b67
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b64, all -> 0x1181 }
            r82 = r13
            r13 = 21
            if (r2 >= r13) goto L_0x0b4c
            java.nio.ByteBuffer[] r28 = r10.getOutputBuffers()     // Catch:{ Exception -> 0x0b64, all -> 0x1181 }
        L_0x0b4c:
            r53 = r4
            r49 = r7
            r66 = r9
            r9 = r63
            r13 = r67
        L_0x0b56:
            r2 = r68
            r4 = r69
            r11 = r70
            r5 = 3
            r7 = 1
        L_0x0b5e:
            r12 = -1
            r8 = r6
            r6 = r87
            goto L_0x0d35
        L_0x0b64:
            r0 = move-exception
            goto L_0x09ce
        L_0x0b67:
            r82 = r13
            r2 = -2
            if (r1 != r2) goto L_0x0bd2
            android.media.MediaFormat r2 = r10.getOutputFormat()     // Catch:{ Exception -> 0x0bc4, all -> 0x1181 }
            r13 = -5
            if (r6 != r13) goto L_0x0bb9
            if (r2 == 0) goto L_0x0bb9
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x0bc4, all -> 0x1181 }
            r13 = 0
            int r6 = r6.addTrack(r2, r13)     // Catch:{ Exception -> 0x0bc4, all -> 0x1181 }
            r13 = r64
            boolean r26 = r2.containsKey(r13)     // Catch:{ Exception -> 0x0bc4, all -> 0x1181 }
            if (r26 == 0) goto L_0x0bac
            r26 = r6
            int r6 = r2.getInteger(r13)     // Catch:{ Exception -> 0x0bc4, all -> 0x1181 }
            r64 = r13
            r13 = 1
            if (r6 != r13) goto L_0x0ba9
            r13 = r67
            java.nio.ByteBuffer r6 = r2.getByteBuffer(r13)     // Catch:{ Exception -> 0x0bc4, all -> 0x1181 }
            r66 = r9
            r9 = r63
            java.nio.ByteBuffer r2 = r2.getByteBuffer(r9)     // Catch:{ Exception -> 0x0be5, all -> 0x1181 }
            int r6 = r6.limit()     // Catch:{ Exception -> 0x0be5, all -> 0x1181 }
            int r2 = r2.limit()     // Catch:{ Exception -> 0x0be5, all -> 0x1181 }
            int r6 = r6 + r2
            r23 = r6
            goto L_0x0bb6
        L_0x0ba9:
            r66 = r9
            goto L_0x0bb2
        L_0x0bac:
            r26 = r6
            r66 = r9
            r64 = r13
        L_0x0bb2:
            r9 = r63
            r13 = r67
        L_0x0bb6:
            r6 = r26
            goto L_0x0bbf
        L_0x0bb9:
            r66 = r9
            r9 = r63
            r13 = r67
        L_0x0bbf:
            r53 = r4
            r49 = r7
            goto L_0x0b56
        L_0x0bc4:
            r0 = move-exception
            r66 = r9
        L_0x0bc7:
            r9 = r81
            r43 = r88
            r14 = r89
            r1 = r0
            r53 = r4
            goto L_0x0806
        L_0x0bd2:
            r66 = r9
            r9 = r63
            r13 = r67
            if (r1 < 0) goto L_0x0var_
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0f8d, all -> 0x1181 }
            r49 = r7
            r7 = 21
            if (r2 >= r7) goto L_0x0be7
            r2 = r28[r1]     // Catch:{ Exception -> 0x0be5, all -> 0x1181 }
            goto L_0x0beb
        L_0x0be5:
            r0 = move-exception
            goto L_0x0bc7
        L_0x0be7:
            java.nio.ByteBuffer r2 = r10.getOutputBuffer(r1)     // Catch:{ Exception -> 0x0f8d, all -> 0x1181 }
        L_0x0beb:
            if (r2 == 0) goto L_0x0f6a
            int r8 = r14.size     // Catch:{ Exception -> 0x0f8d, all -> 0x1181 }
            r7 = 1
            if (r8 <= r7) goto L_0x0d1b
            int r8 = r14.flags     // Catch:{ Exception -> 0x0d16, all -> 0x1181 }
            r8 = r8 & 2
            if (r8 != 0) goto L_0x0c9e
            if (r23 == 0) goto L_0x0c0b
            int r8 = r14.flags     // Catch:{ Exception -> 0x0be5, all -> 0x1181 }
            r8 = r8 & r7
            if (r8 == 0) goto L_0x0c0b
            int r7 = r14.offset     // Catch:{ Exception -> 0x0be5, all -> 0x1181 }
            int r7 = r7 + r23
            r14.offset = r7     // Catch:{ Exception -> 0x0be5, all -> 0x1181 }
            int r7 = r14.size     // Catch:{ Exception -> 0x0be5, all -> 0x1181 }
            int r7 = r7 - r23
            r14.size = r7     // Catch:{ Exception -> 0x0be5, all -> 0x1181 }
        L_0x0c0b:
            if (r27 == 0) goto L_0x0CLASSNAME
            int r7 = r14.flags     // Catch:{ Exception -> 0x0c5e, all -> 0x1181 }
            r8 = 1
            r7 = r7 & r8
            if (r7 == 0) goto L_0x0CLASSNAME
            int r7 = r14.size     // Catch:{ Exception -> 0x0c5e, all -> 0x1181 }
            r8 = 100
            if (r7 <= r8) goto L_0x0CLASSNAME
            int r7 = r14.offset     // Catch:{ Exception -> 0x0c5e, all -> 0x1181 }
            r2.position(r7)     // Catch:{ Exception -> 0x0c5e, all -> 0x1181 }
            byte[] r7 = new byte[r8]     // Catch:{ Exception -> 0x0c5e, all -> 0x1181 }
            r2.get(r7)     // Catch:{ Exception -> 0x0c5e, all -> 0x1181 }
            r8 = 0
            r16 = 0
        L_0x0CLASSNAME:
            r53 = r4
            r4 = 96
            if (r8 >= r4) goto L_0x0c5b
            byte r4 = r7[r8]     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1181 }
            if (r4 != 0) goto L_0x0CLASSNAME
            int r4 = r8 + 1
            byte r4 = r7[r4]     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1181 }
            if (r4 != 0) goto L_0x0CLASSNAME
            int r4 = r8 + 2
            byte r4 = r7[r4]     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1181 }
            if (r4 != 0) goto L_0x0CLASSNAME
            int r4 = r8 + 3
            byte r4 = r7[r4]     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1181 }
            r5 = 1
            if (r4 != r5) goto L_0x0CLASSNAME
            int r4 = r16 + 1
            if (r4 <= r5) goto L_0x0CLASSNAME
            int r4 = r14.offset     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1181 }
            int r4 = r4 + r8
            r14.offset = r4     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1181 }
            int r4 = r14.size     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1181 }
            int r4 = r4 - r8
            r14.size = r4     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1181 }
            goto L_0x0c5b
        L_0x0CLASSNAME:
            r16 = r4
        L_0x0CLASSNAME:
            int r8 = r8 + 1
            r4 = r53
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r53 = r4
        L_0x0c5b:
            r27 = 0
            goto L_0x0CLASSNAME
        L_0x0c5e:
            r0 = move-exception
            r53 = r4
        L_0x0CLASSNAME:
            r9 = r81
            goto L_0x0da9
        L_0x0CLASSNAME:
            r53 = r4
        L_0x0CLASSNAME:
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            r5 = 1
            long r7 = r4.writeSampleData(r6, r2, r14, r5)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            r4 = 0
            int r2 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MediaController$VideoConvertorListener r2 = r15.callback     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            if (r2 == 0) goto L_0x0CLASSNAME
            long r4 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            long r4 = r4 - r11
            int r2 = (r4 > r31 ? 1 : (r4 == r31 ? 0 : -1))
            if (r2 <= 0) goto L_0x0CLASSNAME
            long r4 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x1181 }
            long r31 = r4 - r11
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = move-exception
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r4 = r31
            org.telegram.messenger.MediaController$VideoConvertorListener r2 = r15.callback     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            float r11 = (float) r4     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            float r11 = r11 / r18
            float r11 = r11 / r19
            r2.didWriteData(r7, r11)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            r31 = r4
        L_0x0CLASSNAME:
            r2 = r68
            r4 = r69
            r11 = r70
            r5 = 3
            r7 = 1
            goto L_0x0d24
        L_0x0c9e:
            r53 = r4
            r4 = -5
            if (r6 != r4) goto L_0x0CLASSNAME
            int r4 = r14.size     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            byte[] r4 = new byte[r4]     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            int r5 = r14.offset     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            int r6 = r14.size     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            int r5 = r5 + r6
            r2.limit(r5)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            int r5 = r14.offset     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            r2.position(r5)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            r2.get(r4)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            int r2 = r14.size     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            r7 = 1
            int r2 = r2 - r7
        L_0x0cbb:
            r5 = 3
            if (r2 < 0) goto L_0x0cf8
            if (r2 <= r5) goto L_0x0cf8
            byte r6 = r4[r2]     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            if (r6 != r7) goto L_0x0cf5
            int r6 = r2 + -1
            byte r6 = r4[r6]     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            if (r6 != 0) goto L_0x0cf5
            int r6 = r2 + -2
            byte r6 = r4[r6]     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            if (r6 != 0) goto L_0x0cf5
            int r6 = r2 + -3
            byte r8 = r4[r6]     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            if (r8 != 0) goto L_0x0cf5
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r6)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            int r8 = r14.size     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            int r8 = r8 - r6
            java.nio.ByteBuffer r8 = java.nio.ByteBuffer.allocate(r8)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            r11 = 0
            java.nio.ByteBuffer r12 = r2.put(r4, r11, r6)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            r12.position(r11)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            int r12 = r14.size     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            int r12 = r12 - r6
            java.nio.ByteBuffer r4 = r8.put(r4, r6, r12)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            r4.position(r11)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            r6 = r2
            goto L_0x0cfa
        L_0x0cf5:
            int r2 = r2 + -1
            goto L_0x0cbb
        L_0x0cf8:
            r6 = 0
            r8 = 0
        L_0x0cfa:
            r2 = r68
            r4 = r69
            r11 = r70
            android.media.MediaFormat r12 = android.media.MediaFormat.createVideoFormat(r11, r2, r4)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            if (r6 == 0) goto L_0x0d0e
            if (r8 == 0) goto L_0x0d0e
            r12.setByteBuffer(r13, r6)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            r12.setByteBuffer(r9, r8)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
        L_0x0d0e:
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            r8 = 0
            int r6 = r6.addTrack(r12, r8)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            goto L_0x0d24
        L_0x0d16:
            r0 = move-exception
            r53 = r4
            goto L_0x0da5
        L_0x0d1b:
            r53 = r4
            r2 = r68
            r4 = r69
            r11 = r70
            r5 = 3
        L_0x0d24:
            int r8 = r14.flags     // Catch:{ Exception -> 0x0f3b, all -> 0x1181 }
            r8 = r8 & 4
            if (r8 == 0) goto L_0x0d2c
            r8 = 1
            goto L_0x0d2d
        L_0x0d2c:
            r8 = 0
        L_0x0d2d:
            r12 = 0
            r10.releaseOutputBuffer(r1, r12)     // Catch:{ Exception -> 0x0f3b, all -> 0x1181 }
            r16 = r8
            goto L_0x0b5e
        L_0x0d35:
            if (r1 == r12) goto L_0x0d55
            r1 = r86
            r68 = r2
            r69 = r4
            r2 = r6
            r6 = r8
            r63 = r9
            r70 = r11
            r67 = r13
            r7 = r49
            r4 = r53
            r9 = r66
            r13 = r82
            r11 = r84
        L_0x0d4f:
            r82 = r10
            r10 = r40
            goto L_0x0ae3
        L_0x0d55:
            if (r17 != 0) goto L_0x0var_
            r87 = r6
            r5 = 2500(0x9c4, double:1.235E-320)
            int r1 = r3.dequeueOutputBuffer(r14, r5)     // Catch:{ Exception -> 0x0f3b, all -> 0x1181 }
            if (r1 != r12) goto L_0x0d7b
            r68 = r2
            r69 = r4
            r41 = r5
            r38 = r8
            r63 = r9
            r70 = r11
            r7 = r49
            r4 = r53
            r2 = r65
            r1 = 0
            r6 = 0
            r9 = r81
            r11 = r84
            goto L_0x0f5c
        L_0x0d7b:
            r5 = -3
            if (r1 != r5) goto L_0x0d84
        L_0x0d7e:
            r68 = r2
            r69 = r4
            goto L_0x0var_
        L_0x0d84:
            r5 = -2
            if (r1 != r5) goto L_0x0db0
            android.media.MediaFormat r1 = r3.getOutputFormat()     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            if (r5 == 0) goto L_0x0d7e
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            r5.<init>()     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            java.lang.String r6 = "newFormat = "
            r5.append(r6)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            r5.append(r1)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            goto L_0x0d7e
        L_0x0da4:
            r0 = move-exception
        L_0x0da5:
            r9 = r81
            r11 = r84
        L_0x0da9:
            r43 = r88
            r14 = r89
            r1 = r0
            goto L_0x0806
        L_0x0db0:
            if (r1 < 0) goto L_0x0f1d
            int r5 = r14.size     // Catch:{ Exception -> 0x0f3b, all -> 0x1181 }
            r6 = r8
            if (r5 == 0) goto L_0x0db9
            r5 = 1
            goto L_0x0dba
        L_0x0db9:
            r5 = 0
        L_0x0dba:
            long r7 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0f3b, all -> 0x1181 }
            r44 = 0
            int r52 = (r53 > r44 ? 1 : (r53 == r44 ? 0 : -1))
            if (r52 <= 0) goto L_0x0dd1
            int r52 = (r7 > r53 ? 1 : (r7 == r53 ? 0 : -1))
            if (r52 < 0) goto L_0x0dd1
            int r5 = r14.flags     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            r5 = r5 | 4
            r14.flags = r5     // Catch:{ Exception -> 0x0da4, all -> 0x1181 }
            r5 = 0
            r17 = 1
            r25 = 1
        L_0x0dd1:
            r44 = 0
            int r52 = (r35 > r44 ? 1 : (r35 == r44 ? 0 : -1))
            if (r52 < 0) goto L_0x0e52
            int r12 = r14.flags     // Catch:{ Exception -> 0x0e4b, all -> 0x1181 }
            r12 = r12 & 4
            if (r12 == 0) goto L_0x0e52
            r70 = r11
            r11 = r84
            long r55 = r35 - r11
            long r55 = java.lang.Math.abs(r55)     // Catch:{ Exception -> 0x0e46, all -> 0x1181 }
            r57 = 1000000(0xvar_, float:1.401298E-39)
            r68 = r2
            r63 = r9
            r41 = 2500(0x9c4, double:1.235E-320)
            r9 = r81
            int r2 = r57 / r9
            r69 = r4
            r57 = r5
            long r4 = (long) r2
            int r2 = (r55 > r4 ? 1 : (r55 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e3e
            r4 = 0
            int r2 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e0f
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0e0d, all -> 0x1181 }
            r4 = 0
            r2.seekTo(r11, r4)     // Catch:{ Exception -> 0x0e0d, all -> 0x1181 }
            r38 = r6
            r6 = 0
            goto L_0x0e19
        L_0x0e0d:
            r0 = move-exception
            goto L_0x0da9
        L_0x0e0f:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0e43, all -> 0x1181 }
            r38 = r6
            r4 = 0
            r6 = 0
            r2.seekTo(r4, r6)     // Catch:{ Exception -> 0x0eaf, all -> 0x1181 }
        L_0x0e19:
            long r33 = r49 + r29
            int r2 = r14.flags     // Catch:{ Exception -> 0x0e32, all -> 0x1181 }
            r4 = -5
            r2 = r2 & r4
            r14.flags = r2     // Catch:{ Exception -> 0x0e32, all -> 0x1181 }
            r3.flush()     // Catch:{ Exception -> 0x0e32, all -> 0x1181 }
            r53 = r35
            r2 = 1
            r17 = 0
            r25 = 0
            r44 = 0
            r57 = 0
            r35 = r20
            goto L_0x0e69
        L_0x0e32:
            r0 = move-exception
            r43 = r88
            r14 = r89
            r1 = r0
            r53 = r35
            r35 = r20
            goto L_0x10c1
        L_0x0e3e:
            r38 = r6
            r4 = -5
            r6 = 0
            goto L_0x0e66
        L_0x0e43:
            r0 = move-exception
            goto L_0x0var_
        L_0x0e46:
            r0 = move-exception
            r9 = r81
            goto L_0x0var_
        L_0x0e4b:
            r0 = move-exception
            r9 = r81
            r11 = r84
            goto L_0x0var_
        L_0x0e52:
            r68 = r2
            r69 = r4
            r57 = r5
            r38 = r6
            r63 = r9
            r70 = r11
            r4 = -5
            r6 = 0
            r41 = 2500(0x9c4, double:1.235E-320)
            r9 = r81
            r11 = r84
        L_0x0e66:
            r2 = 0
            r44 = 0
        L_0x0e69:
            int r5 = (r35 > r44 ? 1 : (r35 == r44 ? 0 : -1))
            if (r5 < 0) goto L_0x0e70
            r4 = r35
            goto L_0x0e71
        L_0x0e70:
            r4 = r11
        L_0x0e71:
            int r55 = (r4 > r44 ? 1 : (r4 == r44 ? 0 : -1))
            if (r55 <= 0) goto L_0x0eb2
            int r55 = (r46 > r20 ? 1 : (r46 == r20 ? 0 : -1))
            if (r55 != 0) goto L_0x0eb2
            int r55 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r55 >= 0) goto L_0x0ea1
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0eaf, all -> 0x1181 }
            if (r7 == 0) goto L_0x0e9f
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0eaf, all -> 0x1181 }
            r7.<init>()     // Catch:{ Exception -> 0x0eaf, all -> 0x1181 }
            java.lang.String r8 = "drop frame startTime = "
            r7.append(r8)     // Catch:{ Exception -> 0x0eaf, all -> 0x1181 }
            r7.append(r4)     // Catch:{ Exception -> 0x0eaf, all -> 0x1181 }
            java.lang.String r4 = " present time = "
            r7.append(r4)     // Catch:{ Exception -> 0x0eaf, all -> 0x1181 }
            long r4 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0eaf, all -> 0x1181 }
            r7.append(r4)     // Catch:{ Exception -> 0x0eaf, all -> 0x1181 }
            java.lang.String r4 = r7.toString()     // Catch:{ Exception -> 0x0eaf, all -> 0x1181 }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x0eaf, all -> 0x1181 }
        L_0x0e9f:
            r4 = 0
            goto L_0x0eb4
        L_0x0ea1:
            long r4 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0eaf, all -> 0x1181 }
            r7 = -2147483648(0xfffffffvar_, double:NaN)
            int r46 = (r49 > r7 ? 1 : (r49 == r7 ? 0 : -1))
            if (r46 == 0) goto L_0x0eac
            long r33 = r33 - r4
        L_0x0eac:
            r46 = r4
            goto L_0x0eb2
        L_0x0eaf:
            r0 = move-exception
            goto L_0x0fcc
        L_0x0eb2:
            r4 = r57
        L_0x0eb4:
            if (r2 == 0) goto L_0x0eb9
            r46 = r20
            goto L_0x0ecc
        L_0x0eb9:
            int r2 = (r35 > r20 ? 1 : (r35 == r20 ? 0 : -1))
            if (r2 != 0) goto L_0x0ec9
            r7 = 0
            int r2 = (r33 > r7 ? 1 : (r33 == r7 ? 0 : -1))
            if (r2 == 0) goto L_0x0ec9
            long r7 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0eaf, all -> 0x1181 }
            long r7 = r7 + r33
            r14.presentationTimeUs = r7     // Catch:{ Exception -> 0x0eaf, all -> 0x1181 }
        L_0x0ec9:
            r3.releaseOutputBuffer(r1, r4)     // Catch:{ Exception -> 0x0var_, all -> 0x1181 }
        L_0x0ecc:
            if (r4 == 0) goto L_0x0efe
            r1 = 0
            int r4 = (r35 > r1 ? 1 : (r35 == r1 ? 0 : -1))
            if (r4 < 0) goto L_0x0edd
            long r4 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0eaf, all -> 0x1181 }
            r7 = r49
            long r7 = java.lang.Math.max(r7, r4)     // Catch:{ Exception -> 0x0eaf, all -> 0x1181 }
            goto L_0x0edf
        L_0x0edd:
            r7 = r49
        L_0x0edf:
            r22.awaitNewImage()     // Catch:{ Exception -> 0x0ee4, all -> 0x1181 }
            r4 = 0
            goto L_0x0eea
        L_0x0ee4:
            r0 = move-exception
            r4 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ Exception -> 0x0var_, all -> 0x1181 }
            r4 = 1
        L_0x0eea:
            if (r4 != 0) goto L_0x0var_
            r22.drawImage()     // Catch:{ Exception -> 0x0var_, all -> 0x1181 }
            long r4 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0var_, all -> 0x1181 }
            r44 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 * r44
            r2 = r65
            r2.setPresentationTime(r4)     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            r2.swapBuffers()     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            goto L_0x0var_
        L_0x0efe:
            r7 = r49
        L_0x0var_:
            r2 = r65
        L_0x0var_:
            int r1 = r14.flags     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x0var_
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            if (r1 == 0) goto L_0x0var_
            java.lang.String r1 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
        L_0x0var_:
            r10.signalEndOfInputStream()     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            r4 = r53
            r1 = 0
            goto L_0x0f5c
        L_0x0var_:
            r0 = move-exception
            r2 = r65
            goto L_0x0fcc
        L_0x0f1d:
            r9 = r81
            r11 = r84
            r2 = r65
            r6 = 0
            java.lang.RuntimeException r4 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            r5.<init>()     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            java.lang.String r7 = "unexpected result from decoder.dequeueOutputBuffer: "
            r5.append(r7)     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            r5.append(r1)     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            r4.<init>(r1)     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            throw r4     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
        L_0x0f3b:
            r0 = move-exception
            r9 = r81
            r11 = r84
            goto L_0x0var_
        L_0x0var_:
            r68 = r2
            r69 = r4
            r87 = r6
        L_0x0var_:
            r38 = r8
            r63 = r9
            r70 = r11
            r7 = r49
            r2 = r65
            r6 = 0
            r41 = 2500(0x9c4, double:1.235E-320)
            r9 = r81
            r11 = r84
        L_0x0var_:
            r1 = r86
            r4 = r53
        L_0x0f5c:
            r65 = r2
            r67 = r13
            r6 = r38
            r9 = r66
            r13 = r82
            r2 = r87
            goto L_0x0d4f
        L_0x0f6a:
            r9 = r81
            r53 = r4
            r2 = r65
            r6 = 0
            java.lang.RuntimeException r4 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            r5.<init>()     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            java.lang.String r7 = "encoderOutputBuffer "
            r5.append(r7)     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            r5.append(r1)     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            java.lang.String r1 = " was null"
            r5.append(r1)     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            r4.<init>(r1)     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            throw r4     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
        L_0x0f8d:
            r0 = move-exception
            r9 = r81
            r53 = r4
        L_0x0var_:
            r2 = r65
        L_0x0var_:
            r6 = 0
            goto L_0x0fcc
        L_0x0var_:
            r9 = r81
            r53 = r4
            r2 = r65
            r6 = 0
            java.lang.RuntimeException r4 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            r5.<init>()     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            java.lang.String r7 = "unexpected result from encoder.dequeueOutputBuffer: "
            r5.append(r7)     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            r5.append(r1)     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            r4.<init>(r1)     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
            throw r4     // Catch:{ Exception -> 0x0fb4, all -> 0x1181 }
        L_0x0fb4:
            r0 = move-exception
            r43 = r88
            r14 = r89
            r1 = r0
            r65 = r2
            goto L_0x10c1
        L_0x0fbe:
            r0 = move-exception
            goto L_0x0fc3
        L_0x0fc0:
            r0 = move-exception
            r10 = r82
        L_0x0fc3:
            r53 = r4
            r66 = r9
            r2 = r65
            r6 = 0
            r9 = r81
        L_0x0fcc:
            r43 = r88
            r14 = r89
            r1 = r0
            goto L_0x10c1
        L_0x0fd3:
            r0 = move-exception
            r10 = r82
            r49 = r4
            r88 = r9
            r9 = r13
            r89 = r14
            r2 = r65
            r6 = 0
            r43 = r88
            r1 = r0
            r53 = r49
            goto L_0x10c1
        L_0x0fe7:
            r0 = move-exception
            r10 = r82
            r88 = r9
            r9 = r13
            r89 = r14
            r2 = r65
            r6 = 0
            r43 = r88
            r1 = r0
            r53 = r4
            r35 = r25
            goto L_0x10c1
        L_0x0ffb:
            r0 = move-exception
            r10 = r82
            r11 = r84
            r4 = r86
            r9 = r13
            r89 = r14
            goto L_0x1012
        L_0x1006:
            r0 = move-exception
            r10 = r82
            r11 = r84
            r4 = r86
            r9 = r13
            r89 = r14
            r3 = r60
        L_0x1012:
            r2 = r65
            r6 = 0
            r1 = r0
            r53 = r4
            r35 = r25
            goto L_0x105d
        L_0x101b:
            r0 = move-exception
            r10 = r82
            r11 = r84
            r4 = r86
            r9 = r13
            r89 = r14
            r3 = r60
            r2 = r65
            r6 = 0
            r1 = r0
            goto L_0x103f
        L_0x102c:
            r0 = move-exception
            r11 = r84
            r3 = r1
            r2 = r4
            r10 = r5
            r9 = r13
            r66 = r26
            r6 = 0
            r4 = r86
            r25 = r88
            r89 = r14
            r1 = r0
            r65 = r2
        L_0x103f:
            r53 = r4
            r35 = r25
            goto L_0x105b
        L_0x1044:
            r0 = move-exception
            r11 = r84
            r2 = r4
            r10 = r5
            r9 = r13
            r66 = r26
            r6 = 0
            r4 = r86
            r25 = r88
            r89 = r14
            r1 = r0
            r65 = r2
            r53 = r4
            r35 = r25
            r3 = 0
        L_0x105b:
            r22 = 0
        L_0x105d:
            r43 = 0
            goto L_0x10c1
        L_0x1061:
            r0 = move-exception
            r11 = r84
            r10 = r5
            r9 = r13
            r66 = r26
            r6 = 0
            r4 = r86
            r25 = r88
            r89 = r14
            r1 = r0
            r53 = r4
            r35 = r25
            r3 = 0
            goto L_0x10bb
        L_0x1076:
            r0 = move-exception
            r11 = r84
            r4 = r86
            r9 = r13
            r66 = r26
            r6 = 0
            r25 = r88
            r89 = r14
            goto L_0x10a3
        L_0x1084:
            r0 = move-exception
            r11 = r84
            r4 = r86
            r9 = r13
            r66 = r26
            r6 = 0
            goto L_0x1096
        L_0x108e:
            r0 = move-exception
            r11 = r4
            r9 = r13
            r66 = r26
            r6 = 0
            r4 = r86
        L_0x1096:
            r25 = r88
            goto L_0x10a3
        L_0x1099:
            r0 = move-exception
            r11 = r4
            r9 = r13
            r66 = r26
            r6 = 0
            r4 = r86
            r25 = r7
        L_0x10a3:
            r1 = r0
            r53 = r4
            r35 = r25
            goto L_0x10b9
        L_0x10a9:
            r0 = move-exception
            r11 = r84
            r66 = r5
            r9 = r13
            r6 = 0
            r4 = r86
            r14 = r82
            r35 = r88
            r1 = r0
            r53 = r4
        L_0x10b9:
            r3 = 0
        L_0x10ba:
            r10 = 0
        L_0x10bb:
            r22 = 0
            r43 = 0
            r65 = 0
        L_0x10c1:
            boolean r2 = r1 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x1128, all -> 0x1181 }
            if (r2 == 0) goto L_0x10c9
            if (r93 != 0) goto L_0x10c9
            r2 = 1
            goto L_0x10ca
        L_0x10c9:
            r2 = 0
        L_0x10ca:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1120, all -> 0x1181 }
            r4.<init>()     // Catch:{ Exception -> 0x1120, all -> 0x1181 }
            java.lang.String r5 = "bitrate: "
            r4.append(r5)     // Catch:{ Exception -> 0x1120, all -> 0x1181 }
            r4.append(r14)     // Catch:{ Exception -> 0x1120, all -> 0x1181 }
            java.lang.String r5 = " framerate: "
            r4.append(r5)     // Catch:{ Exception -> 0x1120, all -> 0x1181 }
            r4.append(r9)     // Catch:{ Exception -> 0x1120, all -> 0x1181 }
            java.lang.String r5 = " size: "
            r4.append(r5)     // Catch:{ Exception -> 0x1120, all -> 0x1181 }
            r7 = r80
            r4.append(r7)     // Catch:{ Exception -> 0x111c, all -> 0x1181 }
            java.lang.String r5 = "x"
            r4.append(r5)     // Catch:{ Exception -> 0x111c, all -> 0x1181 }
            r8 = r79
            r4.append(r8)     // Catch:{ Exception -> 0x111a, all -> 0x1181 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x111a, all -> 0x1181 }
            org.telegram.messenger.FileLog.e((java.lang.String) r4)     // Catch:{ Exception -> 0x111a, all -> 0x1181 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x111a, all -> 0x1181 }
            r38 = r2
            r1 = r3
            r6 = r10
            r2 = 1
        L_0x1102:
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x114e, all -> 0x1181 }
            r4 = r66
            r3.unselectTrack(r4)     // Catch:{ Exception -> 0x114e, all -> 0x1181 }
            if (r1 == 0) goto L_0x1111
            r1.stop()     // Catch:{ Exception -> 0x114e, all -> 0x1181 }
            r1.release()     // Catch:{ Exception -> 0x114e, all -> 0x1181 }
        L_0x1111:
            r1 = r2
            r2 = r6
            r6 = r22
            r3 = r43
            r43 = r65
            goto L_0x1148
        L_0x111a:
            r0 = move-exception
            goto L_0x1125
        L_0x111c:
            r0 = move-exception
            r8 = r79
            goto L_0x1125
        L_0x1120:
            r0 = move-exception
            r8 = r79
            r7 = r80
        L_0x1125:
            r1 = r2
            goto L_0x056d
        L_0x1128:
            r0 = move-exception
            r8 = r79
            r7 = r80
            r2 = r0
            goto L_0x1196
        L_0x1130:
            r8 = r79
            r7 = r80
            r11 = r84
            r4 = r86
            r9 = r13
            r6 = 0
            r14 = r82
            r35 = r88
            r53 = r4
            r1 = 0
            r2 = 0
            r3 = 0
            r6 = 0
            r38 = 0
            r43 = 0
        L_0x1148:
            if (r6 == 0) goto L_0x1153
            r6.release()     // Catch:{ Exception -> 0x114e, all -> 0x1181 }
            goto L_0x1153
        L_0x114e:
            r0 = move-exception
            r2 = r0
            r1 = r38
            goto L_0x1197
        L_0x1153:
            if (r43 == 0) goto L_0x1158
            r43.release()     // Catch:{ Exception -> 0x114e, all -> 0x1181 }
        L_0x1158:
            if (r2 == 0) goto L_0x1160
            r2.stop()     // Catch:{ Exception -> 0x114e, all -> 0x1181 }
            r2.release()     // Catch:{ Exception -> 0x114e, all -> 0x1181 }
        L_0x1160:
            if (r3 == 0) goto L_0x1165
            r3.release()     // Catch:{ Exception -> 0x114e, all -> 0x1181 }
        L_0x1165:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x114e, all -> 0x1181 }
            r2 = r1
            r1 = r38
        L_0x116b:
            android.media.MediaExtractor r3 = r15.extractor
            if (r3 == 0) goto L_0x1172
            r3.release()
        L_0x1172:
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer
            if (r3 == 0) goto L_0x117f
            r3.finishMovie()     // Catch:{ Exception -> 0x117a }
            goto L_0x117f
        L_0x117a:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x117f:
            r6 = r8
            goto L_0x11dc
        L_0x1181:
            r0 = move-exception
            r2 = r0
            r1 = r15
            goto L_0x120d
        L_0x1186:
            r0 = move-exception
            r4 = r86
            r9 = r10
            r7 = r11
            r8 = r12
            r6 = 0
            r11 = r84
            r14 = r82
            r35 = r88
            r2 = r0
            r53 = r4
        L_0x1196:
            r1 = 0
        L_0x1197:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x1209 }
            r3.<init>()     // Catch:{ all -> 0x1209 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x1209 }
            r3.append(r14)     // Catch:{ all -> 0x1209 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x1209 }
            r3.append(r9)     // Catch:{ all -> 0x1209 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x1209 }
            r3.append(r7)     // Catch:{ all -> 0x1209 }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x1209 }
            r3.append(r8)     // Catch:{ all -> 0x1209 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x1209 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x1209 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x1209 }
            android.media.MediaExtractor r2 = r15.extractor
            if (r2 == 0) goto L_0x11cd
            r2.release()
        L_0x11cd:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer
            if (r2 == 0) goto L_0x11da
            r2.finishMovie()     // Catch:{ Exception -> 0x11d5 }
            goto L_0x11da
        L_0x11d5:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x11da:
            r6 = r8
            r2 = 1
        L_0x11dc:
            if (r1 == 0) goto L_0x1208
            r20 = 1
            r1 = r74
            r2 = r75
            r3 = r76
            r4 = r77
            r5 = r78
            r8 = r81
            r9 = r14
            r10 = r83
            r11 = r84
            r13 = r53
            r15 = r35
            r17 = r90
            r19 = r92
            r21 = r94
            r22 = r95
            r23 = r96
            r24 = r97
            r25 = r98
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r13, r15, r17, r19, r20, r21, r22, r23, r24, r25)
            return r1
        L_0x1208:
            return r2
        L_0x1209:
            r0 = move-exception
            r1 = r74
            r2 = r0
        L_0x120d:
            android.media.MediaExtractor r3 = r1.extractor
            if (r3 == 0) goto L_0x1214
            r3.release()
        L_0x1214:
            org.telegram.messenger.video.MP4Builder r3 = r1.mediaMuxer
            if (r3 == 0) goto L_0x1221
            r3.finishMovie()     // Catch:{ Exception -> 0x121c }
            goto L_0x1221
        L_0x121c:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x1221:
            goto L_0x1223
        L_0x1222:
            throw r2
        L_0x1223:
            goto L_0x1222
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, int, long, long, long, long, boolean, boolean, org.telegram.messenger.MediaController$SavedFilterState, java.lang.String, java.util.ArrayList, boolean, org.telegram.messenger.MediaController$CropState):boolean");
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
