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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r79v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v47, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v72, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v73, resolved type: java.lang.StringBuilder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v77, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v79, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v80, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v82, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v52, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v83, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v60, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v84, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v62, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v68, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v85, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v69, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v86, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v87, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v88, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v72, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v90, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v73, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v91, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v74, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v95, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v77, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v78, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v81, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v102, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r79v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v103, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v40, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r79v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v86, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v107, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v88, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v108, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v106, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v90, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v91, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v92, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v93, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v94, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v95, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v99, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v100, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v102, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r64v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v105, resolved type: org.telegram.messenger.MediaController$VideoConvertorListener} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v109, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v111, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v120, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v117, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v121, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v118, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v122, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v123, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v133, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v124, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v129, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v128, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v135, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v142, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v143, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v80, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v144, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v145, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v146, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v147, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v63, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v65, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v150, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v151, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v152, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v153, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v67, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v68, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v69, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v70, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v72, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v74, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v164, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v15, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v179, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v180, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v181, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v182, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v191, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v211, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v216, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v217, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v92, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r51v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v94, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v95, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v96, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v97, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v178, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v187, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v186, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v188, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v188, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v190, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v192, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v194, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v193, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v194, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v195, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v196, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v197, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v198, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v201, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v202, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v203, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v199, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v203, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v204, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v205, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v204, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v206, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v205, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v208, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v209, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v210, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v213, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v212, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v215, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v216, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v219, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v214, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v215, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v217, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v218, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v220, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v221, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v223, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v220, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v226, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v225, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v227, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v226, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v228, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v230, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v229, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v231, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v230, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v232, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v234, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v233, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v238, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v239, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v240, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v241, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v242, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v235, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v237, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v239, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v240, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v243, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v244, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v245, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v246, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v250, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v247, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v254, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v256, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v258, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v259, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v261, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v262, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v263, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v258, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v259, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v265, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v266, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v261, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v263, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v268, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v264, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v270, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v271, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v272, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v273, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v274, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v276, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v277, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v278, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v280, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v281, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v282, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v283, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v280, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v284, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v282, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v286, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v284, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v290, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v291, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v293, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v294, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v295, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v296, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v297, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v298, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v301, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v302, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v303, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v304, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v289, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v305, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v306, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v292, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v298, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v313, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v314, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v316, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v317, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v299, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v318, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v300, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v320, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v302, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v304, resolved type: int} */
    /* JADX WARNING: type inference failed for: r23v3 */
    /* JADX WARNING: type inference failed for: r2v53 */
    /* JADX WARNING: type inference failed for: r2v59 */
    /* JADX WARNING: type inference failed for: r2v80 */
    /* JADX WARNING: type inference failed for: r23v13 */
    /* JADX WARNING: type inference failed for: r23v14 */
    /* JADX WARNING: type inference failed for: r8v120, types: [boolean] */
    /* JADX WARNING: type inference failed for: r7v221 */
    /* JADX WARNING: type inference failed for: r7v223 */
    /* JADX WARNING: type inference failed for: r2v235 */
    /* JADX WARNING: Code restructure failed: missing block: B:1000:0x11a4, code lost:
        r9 = r10;
        r7 = r11;
        r8 = r12;
        r11 = r81;
        r14 = r79;
        r48 = r85;
        r2 = r0;
        r34 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1001:0x11b3, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1003:?, code lost:
        org.telegram.messenger.FileLog.e("bitrate: " + r14 + " framerate: " + r9 + " size: " + r7 + "x" + r8);
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1004:0x11e3, code lost:
        r2 = r15.extractor;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1005:0x11e5, code lost:
        if (r2 != null) goto L_0x11e7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1006:0x11e7, code lost:
        r2.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1007:0x11ea, code lost:
        r2 = r15.mediaMuxer;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1008:0x11ec, code lost:
        if (r2 != null) goto L_0x11ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1010:?, code lost:
        r2.finishMovie();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1011:0x11f2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1012:0x11f3, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1013:0x11f7, code lost:
        r6 = r8;
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1018:0x1226, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1019:0x1227, code lost:
        r1 = r71;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1020:0x122a, code lost:
        r3 = r1.extractor;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1021:0x122c, code lost:
        if (r3 != null) goto L_0x122e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1022:0x122e, code lost:
        r3.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1023:0x1231, code lost:
        r3 = r1.mediaMuxer;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1024:0x1233, code lost:
        if (r3 != null) goto L_0x1235;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1026:?, code lost:
        r3.finishMovie();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1027:0x1239, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1028:0x123a, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1029:0x123f, code lost:
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
        r13 = r76;
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
        r6 = r76;
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
        r10 = r78;
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
        r34 = r83;
        r48 = r85;
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
        r34 = r83;
        r48 = r85;
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
        r34 = r83;
        r48 = r85;
        r1 = r2;
        r14 = r9;
        r7 = r11;
        r8 = r12;
        r9 = r78;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x056b, code lost:
        r11 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x056d, code lost:
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0570, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x0571, code lost:
        r34 = r83;
        r48 = r85;
        r2 = r0;
        r14 = r50;
        r7 = r11;
        r8 = r12;
        r1 = 0;
        r9 = r78;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x057e, code lost:
        r11 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x05ab, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x05ac, code lost:
        r34 = r83;
        r48 = r85;
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
        r34 = r83;
        r48 = r85;
        r2 = r0;
        r14 = r7;
        r9 = r10;
        r8 = r12;
        r1 = 0;
        r7 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x0614, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x0615, code lost:
        r8 = r76;
        r7 = r77;
        r14 = r79;
        r11 = r81;
        r34 = r83;
        r48 = r85;
        r2 = r0;
        r9 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x0625, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x0626, code lost:
        r8 = r76;
        r7 = r77;
        r14 = r79;
        r11 = r81;
        r34 = r83;
        r48 = r85;
        r2 = r0;
        r9 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x0675, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x0676, code lost:
        r11 = r81;
        r34 = r83;
        r48 = r85;
        r1 = r0;
        r60 = r5;
        r9 = r13;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x06b7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x06b8, code lost:
        r34 = r83;
        r1 = r0;
        r11 = r4;
        r48 = r7;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x06f6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x06f7, code lost:
        r34 = r83;
        r48 = r85;
        r1 = r0;
        r11 = r4;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x07f3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x07f4, code lost:
        r10 = r79;
        r11 = r81;
        r34 = r83;
        r1 = r0;
        r43 = null;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x07fd, code lost:
        r9 = r13;
        r48 = r27;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x0879, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x087a, code lost:
        r10 = r79;
        r11 = r81;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x087e, code lost:
        r34 = r83;
        r1 = r0;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x08a1, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x08a2, code lost:
        r10 = r79;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x08c8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x08ca, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x08cb, code lost:
        r4 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x08cd, code lost:
        r10 = r79;
        r1 = r0;
        r34 = r4;
        r43 = r6;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x08d6, code lost:
        r0 = e;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x08d8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:480:0x08d9, code lost:
        r8 = r72;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x08db, code lost:
        r4 = r83;
        r10 = r79;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x08ec, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x08ed, code lost:
        r8 = r72;
        r4 = r83;
        r10 = r79;
        r11 = r81;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x08f5, code lost:
        r1 = r0;
        r34 = r4;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:486:0x08f8, code lost:
        r9 = r13;
        r48 = r27;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x0954, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x0955, code lost:
        r10 = r79;
        r1 = r0;
        r34 = r4;
        r43 = r9;
        r9 = r13;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x09c7, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x09c8, code lost:
        r10 = r79;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x09ca, code lost:
        r43 = r85;
        r14 = r86;
        r1 = r0;
        r34 = r4;
        r60 = r9;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x09d3, code lost:
        r9 = r78;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x017c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x017d, code lost:
        r6 = r76;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x0a26, code lost:
        if (r8 < 0) goto L_0x0a2b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0180, code lost:
        r11 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x0a6d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x0a6e, code lost:
        r64 = r4;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x0a9f, code lost:
        r0 = e;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x0aa0, code lost:
        r10 = r79;
        r43 = r85;
        r14 = r86;
        r1 = r0;
        r60 = r9;
        r34 = r64;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0aad, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x0aae, code lost:
        r85 = r9;
        r86 = r14;
        r9 = r78;
        r10 = r79;
        r43 = r85;
        r1 = r0;
        r34 = r4;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0b63, code lost:
        r0 = e;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0bc3, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0bc4, code lost:
        r60 = r9;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0be4, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0c5d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0c5e, code lost:
        r34 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0CLASSNAME, code lost:
        r9 = r78;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0CLASSNAME, code lost:
        r0 = e;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0d15, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0d16, code lost:
        r34 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x0da8, code lost:
        r0 = e;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x0da9, code lost:
        r9 = r78;
        r11 = r81;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x0dad, code lost:
        r43 = r85;
        r14 = r86;
        r1 = r0;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x0e11, code lost:
        r0 = e;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x0e34, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x0e35, code lost:
        r43 = r85;
        r14 = r86;
        r1 = r0;
        r34 = r48;
        r48 = -1;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x0e40, code lost:
        r0 = e;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x0e48, code lost:
        r0 = e;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x0e4b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x0e4c, code lost:
        r9 = r78;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x0e50, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x0e51, code lost:
        r9 = r78;
        r11 = r81;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x0eba, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x0ebb, code lost:
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x01f9, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x0eef, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x01fa, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:862:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x01fb, code lost:
        r6 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x0f2a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x0f2b, code lost:
        r43 = r85;
        r14 = r86;
        r1 = r0;
        r59 = r2;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x0var_, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x0var_, code lost:
        r2 = r59;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x0var_, code lost:
        r43 = r85;
        r14 = r86;
        r1 = r0;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x0f3b, code lost:
        r34 = r48;
        r48 = r33;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x0var_, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x0var_, code lost:
        r9 = r78;
        r11 = r81;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x0fb1, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:897:0x0fb2, code lost:
        r9 = r78;
        r34 = r4;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x0fb6, code lost:
        r2 = r59;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:899:0x0fb8, code lost:
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x0fd8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:0x0fd9, code lost:
        r43 = r85;
        r14 = r86;
        r1 = r0;
        r59 = r2;
        r9 = r9;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:0x0fe2, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x0fe4, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:906:0x0fe5, code lost:
        r10 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:0x0fe7, code lost:
        r34 = r4;
        r60 = r9;
        r2 = r59;
        r9 = r78;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x0ff0, code lost:
        r43 = r85;
        r14 = r86;
        r1 = r0;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:909:0x0ff7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:910:0x0ff8, code lost:
        r10 = r79;
        r85 = r9;
        r9 = r13;
        r86 = r14;
        r2 = r59;
        r43 = r85;
        r1 = r0;
        r34 = r4;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:0x100b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:0x100c, code lost:
        r10 = r79;
        r85 = r9;
        r9 = r13;
        r86 = r14;
        r2 = r59;
        r43 = r85;
        r1 = r0;
        r34 = r4;
        r48 = r27;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:913:0x101f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:0x1020, code lost:
        r10 = r79;
        r11 = r81;
        r4 = r83;
        r9 = r13;
        r86 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:0x102a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x102b, code lost:
        r10 = r79;
        r11 = r81;
        r4 = r83;
        r9 = r13;
        r86 = r14;
        r3 = r52;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:917:0x1036, code lost:
        r2 = r59;
        r1 = r0;
        r34 = r4;
        r48 = r27;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:918:0x103f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:0x1040, code lost:
        r10 = r79;
        r11 = r81;
        r4 = r83;
        r9 = r13;
        r86 = r14;
        r3 = r52;
        r2 = r59;
        r1 = r0;
        r7 = r7;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:920:0x1050, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x1051, code lost:
        r11 = r81;
        r27 = r85;
        r3 = r1;
        r2 = r4;
        r10 = r5;
        r9 = r13;
        r86 = r14;
        r60 = r24;
        r4 = r83;
        r1 = r0;
        r59 = r2;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x1063, code lost:
        r34 = r4;
        r48 = r27;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x1068, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x1069, code lost:
        r11 = r81;
        r10 = r5;
        r9 = r13;
        r86 = r14;
        r60 = r24;
        r1 = r0;
        r59 = r4;
        r34 = r83;
        r48 = r85;
        r3 = null;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x107f, code lost:
        r22 = null;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x1081, code lost:
        r43 = null;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x1085, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:928:0x1086, code lost:
        r11 = r81;
        r10 = r5;
        r9 = r13;
        r86 = r14;
        r60 = r24;
        r1 = r0;
        r34 = r83;
        r48 = r85;
        r3 = null;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:0x109a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x109b, code lost:
        r11 = r81;
        r4 = r83;
        r27 = r85;
        r9 = r13;
        r86 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x10a5, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:932:0x10a6, code lost:
        r11 = r81;
        r4 = r83;
        r27 = r85;
        r9 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x10ad, code lost:
        r60 = r24;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x10b1, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:935:0x10b2, code lost:
        r27 = r85;
        r11 = r4;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:0x10b6, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:0x10b7, code lost:
        r11 = r4;
        r27 = r7;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:0x10ba, code lost:
        r9 = r13;
        r60 = r24;
        r4 = r83;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x10c0, code lost:
        r1 = r0;
        r34 = r4;
        r48 = r27;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:0x10c6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:0x10c7, code lost:
        r11 = r81;
        r60 = r5;
        r9 = r13;
        r14 = r79;
        r48 = r85;
        r1 = r0;
        r34 = r83;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:0x10d6, code lost:
        r3 = null;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:0x10e4, code lost:
        r2 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:965:0x1137, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:967:0x1139, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:968:0x113a, code lost:
        r8 = r76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:969:0x113d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:0x113e, code lost:
        r8 = r76;
        r7 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:971:0x1142, code lost:
        r1 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:972:0x1145, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:0x1146, code lost:
        r8 = r76;
        r7 = r77;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:977:0x116b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:978:0x116c, code lost:
        r2 = r0;
        r1 = r38;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:997:0x119e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:998:0x119f, code lost:
        r2 = r0;
        r1 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:999:0x11a3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1006:0x11e7  */
    /* JADX WARNING: Removed duplicated region for block: B:1009:0x11ee A[SYNTHETIC, Splitter:B:1009:0x11ee] */
    /* JADX WARNING: Removed duplicated region for block: B:1015:0x11fb  */
    /* JADX WARNING: Removed duplicated region for block: B:1017:0x1225 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:1022:0x122e  */
    /* JADX WARNING: Removed duplicated region for block: B:1025:0x1235 A[SYNTHETIC, Splitter:B:1025:0x1235] */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x03cf A[Catch:{ Exception -> 0x042f, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x03d3 A[Catch:{ Exception -> 0x042f, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x03e0 A[Catch:{ Exception -> 0x042f, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x03f8 A[Catch:{ Exception -> 0x042f, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x04e2 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0521 A[SYNTHETIC, Splitter:B:264:0x0521] */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0535 A[Catch:{ Exception -> 0x0525, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x053a A[Catch:{ Exception -> 0x0525, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x063d  */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x06df  */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x06fe  */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x0708 A[SYNTHETIC, Splitter:B:379:0x0708] */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0746 A[SYNTHETIC, Splitter:B:388:0x0746] */
    /* JADX WARNING: Removed duplicated region for block: B:416:0x07e3 A[SYNTHETIC, Splitter:B:416:0x07e3] */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0803  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0809 A[SYNTHETIC, Splitter:B:425:0x0809] */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x0837  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x083a  */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x08e0  */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x08fe  */
    /* JADX WARNING: Removed duplicated region for block: B:490:0x090c  */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x090f  */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0930 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x094b A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:509:0x0963  */
    /* JADX WARNING: Removed duplicated region for block: B:595:0x0abf  */
    /* JADX WARNING: Removed duplicated region for block: B:599:0x0ae1 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:604:0x0b04  */
    /* JADX WARNING: Removed duplicated region for block: B:605:0x0b15  */
    /* JADX WARNING: Removed duplicated region for block: B:610:0x0b26  */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b3f  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0CLASSNAME A[Catch:{ Exception -> 0x0da8, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:747:0x0d36 A[Catch:{ Exception -> 0x0var_, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:749:0x0d54 A[Catch:{ Exception -> 0x0var_, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:821:0x0e78  */
    /* JADX WARNING: Removed duplicated region for block: B:822:0x0e7b  */
    /* JADX WARNING: Removed duplicated region for block: B:825:0x0e80  */
    /* JADX WARNING: Removed duplicated region for block: B:842:0x0ec1 A[Catch:{ Exception -> 0x0eba, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:843:0x0ec4 A[Catch:{ Exception -> 0x0eba, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x0ed9  */
    /* JADX WARNING: Removed duplicated region for block: B:868:0x0var_ A[Catch:{ Exception -> 0x0f2a, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:872:0x0var_ A[Catch:{ Exception -> 0x0f2a, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:877:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:948:0x10e2 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:963:0x1128 A[Catch:{ Exception -> 0x116b, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:974:0x114d A[Catch:{ Exception -> 0x116b, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:976:0x1167 A[Catch:{ Exception -> 0x116b, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:980:0x1172 A[Catch:{ Exception -> 0x116b, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:982:0x1177 A[Catch:{ Exception -> 0x116b, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:984:0x117f A[Catch:{ Exception -> 0x116b, all -> 0x119e }] */
    /* JADX WARNING: Removed duplicated region for block: B:989:0x118c  */
    /* JADX WARNING: Removed duplicated region for block: B:992:0x1193 A[SYNTHETIC, Splitter:B:992:0x1193] */
    /* JADX WARNING: Removed duplicated region for block: B:997:0x119e A[ExcHandler: all (r0v4 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:1:0x0017] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:33:0x00e0=Splitter:B:33:0x00e0, B:740:0x0d23=Splitter:B:740:0x0d23, B:376:0x0704=Splitter:B:376:0x0704, B:59:0x0186=Splitter:B:59:0x0186, B:397:0x0766=Splitter:B:397:0x0766, B:499:0x0946=Splitter:B:499:0x0946, B:699:0x0CLASSNAME=Splitter:B:699:0x0CLASSNAME, B:10:0x0067=Splitter:B:10:0x0067, B:254:0x04e7=Splitter:B:254:0x04e7, B:196:0x03c9=Splitter:B:196:0x03c9, B:856:0x0eea=Splitter:B:856:0x0eea, B:433:0x082b=Splitter:B:433:0x082b, B:492:0x0911=Splitter:B:492:0x0911, B:848:0x0ed4=Splitter:B:848:0x0ed4, B:543:0x09fc=Splitter:B:543:0x09fc, B:71:0x01be=Splitter:B:71:0x01be, B:248:0x04de=Splitter:B:248:0x04de, B:945:0x10de=Splitter:B:945:0x10de, B:601:0x0aff=Splitter:B:601:0x0aff, B:606:0x0b1f=Splitter:B:606:0x0b1f} */
    /* JADX WARNING: Unknown variable types count: 2 */
    @android.annotation.TargetApi(18)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideoInternal(java.lang.String r72, java.io.File r73, int r74, boolean r75, int r76, int r77, int r78, int r79, int r80, long r81, long r83, long r85, long r87, boolean r89, boolean r90, org.telegram.messenger.MediaController.SavedFilterState r91, java.lang.String r92, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r93, boolean r94, org.telegram.messenger.MediaController.CropState r95) {
        /*
            r71 = this;
            r15 = r71
            r13 = r72
            r14 = r74
            r12 = r76
            r11 = r77
            r10 = r78
            r9 = r79
            r8 = r80
            r6 = r81
            r4 = r87
            r3 = r95
            r1 = 0
            android.media.MediaCodec$BufferInfo r6 = new android.media.MediaCodec$BufferInfo     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            r6.<init>()     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            org.telegram.messenger.video.Mp4Movie r7 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            r7.<init>()     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            r16 = r6
            r6 = r73
            r7.setCacheFile(r6)     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            r7.setRotation(r1)     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            r7.setSize(r12, r11)     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            org.telegram.messenger.video.MP4Builder r1 = new org.telegram.messenger.video.MP4Builder     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            r1.<init>()     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            r6 = r75
            org.telegram.messenger.video.MP4Builder r1 = r1.createMovie(r7, r6)     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            r15.mediaMuxer = r1     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            float r1 = (float) r4     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            r18 = 1148846080(0x447a0000, float:1000.0)
            float r19 = r1 / r18
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            java.lang.String r7 = "csd-1"
            java.lang.String r6 = "csd-0"
            r20 = r6
            java.lang.String r6 = "prepend-sps-pps-to-idr-frames"
            r23 = r6
            r22 = r7
            java.lang.String r7 = "video/avc"
            r30 = r7
            r6 = 0
            if (r94 == 0) goto L_0x0582
            int r32 = (r85 > r6 ? 1 : (r85 == r6 ? 0 : -1))
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
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            if (r1 == 0) goto L_0x0096
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            r1.<init>()     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            java.lang.String r2 = "changing width from "
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            r1.append(r12)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            java.lang.String r2 = " to "
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            float r2 = (float) r12     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            float r2 = r2 / r32
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            int r2 = r2 * 16
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
        L_0x0096:
            float r1 = (float) r12     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            float r1 = r1 / r32
            int r1 = java.lang.Math.round(r1)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
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
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            if (r1 == 0) goto L_0x00d6
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            r1.<init>()     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            java.lang.String r2 = "changing height from "
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            r1.append(r11)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            java.lang.String r2 = " to "
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            float r2 = (float) r11     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            float r2 = r2 / r32
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            int r2 = r2 * 16
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
        L_0x00d6:
            float r1 = (float) r11     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            float r1 = r1 / r32
            int r1 = java.lang.Math.round(r1)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            int r1 = r1 * 16
            r11 = r1
        L_0x00e0:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x04cc, all -> 0x119e }
            if (r1 == 0) goto L_0x0108
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            r1.<init>()     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            java.lang.String r2 = "create photo encoder "
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            r1.append(r12)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            java.lang.String r2 = " "
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            r1.append(r11)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            java.lang.String r2 = " duration = "
            r1.append(r2)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            r1.append(r4)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x00a1, all -> 0x119e }
        L_0x0108:
            r2 = r30
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r2, r12, r11)     // Catch:{ Exception -> 0x04cc, all -> 0x119e }
            java.lang.String r6 = "color-format"
            r7 = 2130708361(0x7var_, float:1.701803E38)
            r1.setInteger(r6, r7)     // Catch:{ Exception -> 0x04cc, all -> 0x119e }
            java.lang.String r6 = "bitrate"
            r1.setInteger(r6, r9)     // Catch:{ Exception -> 0x04cc, all -> 0x119e }
            java.lang.String r6 = "frame-rate"
            r1.setInteger(r6, r10)     // Catch:{ Exception -> 0x04cc, all -> 0x119e }
            java.lang.String r6 = "i-frame-interval"
            r7 = 2
            r1.setInteger(r6, r7)     // Catch:{ Exception -> 0x04cc, all -> 0x119e }
            android.media.MediaCodec r6 = android.media.MediaCodec.createEncoderByType(r2)     // Catch:{ Exception -> 0x04cc, all -> 0x119e }
            r30 = r2
            r2 = 1
            r7 = 0
            r6.configure(r1, r7, r7, r2)     // Catch:{ Exception -> 0x04c3, all -> 0x119e }
            org.telegram.messenger.video.InputSurface r1 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x04c3, all -> 0x119e }
            android.view.Surface r2 = r6.createInputSurface()     // Catch:{ Exception -> 0x04c3, all -> 0x119e }
            r1.<init>(r2)     // Catch:{ Exception -> 0x04c3, all -> 0x119e }
            r1.makeCurrent()     // Catch:{ Exception -> 0x04b6, all -> 0x119e }
            r6.start()     // Catch:{ Exception -> 0x04b6, all -> 0x119e }
            org.telegram.messenger.video.OutputSurface r31 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x04b6, all -> 0x119e }
            r32 = 0
            float r2 = (float) r10
            r36 = 1
            r37 = r1
            r1 = r31
            r17 = r2
            r21 = r30
            r2 = r91
            r3 = r72
            r4 = r92
            r5 = r93
            r7 = r6
            r14 = r16
            r13 = 21
            r6 = r32
            r76 = r7
            r49 = r21
            r48 = r22
            r7 = r12
            r8 = r11
            r50 = r9
            r9 = r74
            r10 = r17
            r51 = r11
            r11 = r36
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x04ad, all -> 0x119e }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x04a3, all -> 0x119e }
            if (r1 >= r13) goto L_0x0185
            java.nio.ByteBuffer[] r6 = r76.getOutputBuffers()     // Catch:{ Exception -> 0x017c, all -> 0x119e }
            goto L_0x0186
        L_0x017c:
            r0 = move-exception
            r6 = r76
            r1 = r0
        L_0x0180:
            r11 = r51
        L_0x0182:
            r13 = 0
            goto L_0x04de
        L_0x0185:
            r6 = 0
        L_0x0186:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x04a3, all -> 0x119e }
            r7 = r6
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = -5
            r6 = 1
        L_0x0190:
            if (r1 != 0) goto L_0x0493
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x04a3, all -> 0x119e }
            r8 = r2 ^ 1
            r9 = r7
            r7 = r5
            r5 = r3
            r3 = 1
            r68 = r2
            r2 = r1
            r1 = r8
            r8 = r6
            r6 = r4
            r4 = r68
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
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x04a3, all -> 0x119e }
            if (r90 == 0) goto L_0x01ba
            r10 = 22000(0x55f0, double:1.08694E-319)
            r13 = r76
            goto L_0x01be
        L_0x01ba:
            r13 = r76
            r10 = 2500(0x9c4, double:1.235E-320)
        L_0x01be:
            int r10 = r13.dequeueOutputBuffer(r14, r10)     // Catch:{ Exception -> 0x0491, all -> 0x119e }
            r11 = -1
            if (r10 != r11) goto L_0x01da
            r76 = r1
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
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r76 = r1
            r1 = 21
            if (r11 >= r1) goto L_0x01e9
            java.nio.ByteBuffer[] r9 = r13.getOutputBuffers()     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
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
            r76 = r1
            r1 = -2
            if (r10 != r1) goto L_0x027a
            android.media.MediaFormat r1 = r13.getOutputFormat()     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            if (r11 == 0) goto L_0x0221
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r11.<init>()     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r77 = r2
            java.lang.String r2 = "photo encoder new format "
            r11.append(r2)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r11.append(r1)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            java.lang.String r2 = r11.toString()     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            goto L_0x0223
        L_0x0221:
            r77 = r2
        L_0x0223:
            r2 = -5
            if (r7 != r2) goto L_0x0267
            if (r1 == 0) goto L_0x0267
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r11 = 0
            int r7 = r7.addTrack(r1, r11)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r79 = r3
            r3 = r23
            boolean r16 = r1.containsKey(r3)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            if (r16 == 0) goto L_0x025f
            int r11 = r1.getInteger(r3)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r2 = 1
            if (r11 != r2) goto L_0x0258
            r11 = r20
            java.nio.ByteBuffer r5 = r1.getByteBuffer(r11)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r23 = r3
            r3 = r48
            java.nio.ByteBuffer r1 = r1.getByteBuffer(r3)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            int r5 = r5.limit()     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            int r1 = r1.limit()     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
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
            r79 = r3
            goto L_0x0261
        L_0x026a:
            r38 = r77
            r17 = r5
            r20 = r6
            r6 = r9
            r9 = r49
            r2 = r51
            r1 = -1
            r5 = r79
            goto L_0x03de
        L_0x027a:
            r79 = r3
            r11 = r20
            r3 = r48
            r2 = 1
            if (r10 < 0) goto L_0x0474
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0491, all -> 0x119e }
            r2 = 21
            if (r1 >= r2) goto L_0x028c
            r1 = r9[r10]     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            goto L_0x0290
        L_0x028c:
            java.nio.ByteBuffer r1 = r13.getOutputBuffer(r10)     // Catch:{ Exception -> 0x0491, all -> 0x119e }
        L_0x0290:
            if (r1 == 0) goto L_0x0456
            int r2 = r14.size     // Catch:{ Exception -> 0x0454, all -> 0x119e }
            r77 = r9
            r9 = 1
            if (r2 <= r9) goto L_0x03bf
            int r2 = r14.flags     // Catch:{ Exception -> 0x03b7, all -> 0x119e }
            r9 = 2
            r2 = r2 & r9
            if (r2 != 0) goto L_0x033a
            if (r5 == 0) goto L_0x02b3
            int r2 = r14.flags     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r16 = 1
            r2 = r2 & 1
            if (r2 == 0) goto L_0x02b3
            int r2 = r14.offset     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            int r2 = r2 + r5
            r14.offset = r2     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            int r2 = r14.size     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            int r2 = r2 - r5
            r14.size = r2     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
        L_0x02b3:
            if (r8 == 0) goto L_0x0308
            int r2 = r14.flags     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r16 = 1
            r2 = r2 & 1
            if (r2 == 0) goto L_0x0308
            int r2 = r14.size     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r8 = 100
            if (r2 <= r8) goto L_0x0307
            int r2 = r14.offset     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r1.position(r2)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            byte[] r2 = new byte[r8]     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r1.get(r2)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r8 = 0
            r16 = 0
        L_0x02d0:
            r9 = 96
            if (r8 >= r9) goto L_0x0307
            byte r9 = r2[r8]     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            if (r9 != 0) goto L_0x02ff
            int r9 = r8 + 1
            byte r9 = r2[r9]     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            if (r9 != 0) goto L_0x02ff
            int r9 = r8 + 2
            byte r9 = r2[r9]     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            if (r9 != 0) goto L_0x02ff
            int r9 = r8 + 3
            byte r9 = r2[r9]     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r17 = r2
            r2 = 1
            if (r9 != r2) goto L_0x0301
            int r9 = r16 + 1
            if (r9 <= r2) goto L_0x02fc
            int r2 = r14.offset     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            int r2 = r2 + r8
            r14.offset = r2     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            int r2 = r14.size     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            int r2 = r2 - r8
            r14.size = r2     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
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
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r9 = 1
            long r1 = r2.writeSampleData(r7, r1, r14, r9)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r16 = r8
            r8 = 0
            int r17 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r17 == 0) goto L_0x032e
            org.telegram.messenger.MediaController$VideoConvertorListener r8 = r15.callback     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            if (r8 == 0) goto L_0x032e
            org.telegram.messenger.MediaController$VideoConvertorListener r8 = r15.callback     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r17 = r5
            r20 = r6
            r5 = 0
            float r9 = (float) r5     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            float r9 = r9 / r18
            float r9 = r9 / r19
            r8.didWriteData(r1, r9)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
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
            int r5 = r14.size     // Catch:{ Exception -> 0x03b7, all -> 0x119e }
            byte[] r5 = new byte[r5]     // Catch:{ Exception -> 0x03b7, all -> 0x119e }
            int r7 = r14.offset     // Catch:{ Exception -> 0x03b7, all -> 0x119e }
            int r8 = r14.size     // Catch:{ Exception -> 0x03b7, all -> 0x119e }
            int r7 = r7 + r8
            r1.limit(r7)     // Catch:{ Exception -> 0x03b7, all -> 0x119e }
            int r7 = r14.offset     // Catch:{ Exception -> 0x03b7, all -> 0x119e }
            r1.position(r7)     // Catch:{ Exception -> 0x03b7, all -> 0x119e }
            r1.get(r5)     // Catch:{ Exception -> 0x03b7, all -> 0x119e }
            int r1 = r14.size     // Catch:{ Exception -> 0x03b7, all -> 0x119e }
            r7 = 1
            int r1 = r1 - r7
        L_0x035a:
            if (r1 < 0) goto L_0x0398
            r9 = 3
            if (r1 <= r9) goto L_0x0398
            byte r8 = r5[r1]     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            if (r8 != r7) goto L_0x0393
            int r7 = r1 + -1
            byte r7 = r5[r7]     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            if (r7 != 0) goto L_0x0393
            int r7 = r1 + -2
            byte r7 = r5[r7]     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            if (r7 != 0) goto L_0x0393
            int r7 = r1 + -3
            byte r8 = r5[r7]     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            if (r8 != 0) goto L_0x0393
            java.nio.ByteBuffer r1 = java.nio.ByteBuffer.allocate(r7)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            int r8 = r14.size     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            int r8 = r8 - r7
            java.nio.ByteBuffer r8 = java.nio.ByteBuffer.allocate(r8)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r2 = 0
            java.nio.ByteBuffer r9 = r1.put(r5, r2, r7)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r9.position(r2)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            int r9 = r14.size     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            int r9 = r9 - r7
            java.nio.ByteBuffer r5 = r8.put(r5, r7, r9)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
            r5.position(r2)     // Catch:{ Exception -> 0x01f9, all -> 0x119e }
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
            android.media.MediaFormat r5 = android.media.MediaFormat.createVideoFormat(r9, r12, r2)     // Catch:{ Exception -> 0x03b5, all -> 0x119e }
            if (r1 == 0) goto L_0x03ac
            if (r8 == 0) goto L_0x03ac
            r5.setByteBuffer(r11, r1)     // Catch:{ Exception -> 0x03b5, all -> 0x119e }
            r5.setByteBuffer(r3, r8)     // Catch:{ Exception -> 0x03b5, all -> 0x119e }
        L_0x03ac:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ Exception -> 0x03b5, all -> 0x119e }
            r7 = 0
            int r1 = r1.addTrack(r5, r7)     // Catch:{ Exception -> 0x03b5, all -> 0x119e }
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
            int r1 = r14.flags     // Catch:{ Exception -> 0x042f, all -> 0x119e }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x03d3
            r1 = 0
            r38 = 1
            goto L_0x03d6
        L_0x03d3:
            r1 = 0
            r38 = 0
        L_0x03d6:
            r13.releaseOutputBuffer(r10, r1)     // Catch:{ Exception -> 0x042f, all -> 0x119e }
            r6 = r77
            r5 = r79
            r1 = -1
        L_0x03de:
            if (r10 == r1) goto L_0x03f8
            r1 = r76
            r51 = r2
            r48 = r3
            r3 = r5
            r49 = r9
            r76 = r13
            r5 = r17
            r2 = r38
            r13 = 21
            r9 = r6
            r6 = r20
            r20 = r11
            goto L_0x01a3
        L_0x03f8:
            if (r4 != 0) goto L_0x0436
            r31.drawImage()     // Catch:{ Exception -> 0x042f, all -> 0x119e }
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
            r10.setPresentationTime(r2)     // Catch:{ Exception -> 0x0429, all -> 0x119e }
            r10.swapBuffers()     // Catch:{ Exception -> 0x0429, all -> 0x119e }
            int r1 = r1 + 1
            float r2 = (float) r1     // Catch:{ Exception -> 0x0429, all -> 0x119e }
            r3 = 1106247680(0x41var_, float:30.0)
            float r3 = r3 * r19
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 < 0) goto L_0x043e
            r13.signalEndOfInputStream()     // Catch:{ Exception -> 0x0429, all -> 0x119e }
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
            r1 = r76
        L_0x0441:
            r3 = r5
            r49 = r9
            r37 = r10
            r20 = r11
            r76 = r13
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
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x048d, all -> 0x119e }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d, all -> 0x119e }
            r3.<init>()     // Catch:{ Exception -> 0x048d, all -> 0x119e }
            java.lang.String r4 = "encoderOutputBuffer "
            r3.append(r4)     // Catch:{ Exception -> 0x048d, all -> 0x119e }
            r3.append(r10)     // Catch:{ Exception -> 0x048d, all -> 0x119e }
            java.lang.String r4 = " was null"
            r3.append(r4)     // Catch:{ Exception -> 0x048d, all -> 0x119e }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x048d, all -> 0x119e }
            r2.<init>(r3)     // Catch:{ Exception -> 0x048d, all -> 0x119e }
            throw r2     // Catch:{ Exception -> 0x048d, all -> 0x119e }
        L_0x0474:
            r1 = r37
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x048d, all -> 0x119e }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x048d, all -> 0x119e }
            r3.<init>()     // Catch:{ Exception -> 0x048d, all -> 0x119e }
            java.lang.String r4 = "unexpected result from encoder.dequeueOutputBuffer: "
            r3.append(r4)     // Catch:{ Exception -> 0x048d, all -> 0x119e }
            r3.append(r10)     // Catch:{ Exception -> 0x048d, all -> 0x119e }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x048d, all -> 0x119e }
            r2.<init>(r3)     // Catch:{ Exception -> 0x048d, all -> 0x119e }
            throw r2     // Catch:{ Exception -> 0x048d, all -> 0x119e }
        L_0x048d:
            r0 = move-exception
            r37 = r1
            goto L_0x04a8
        L_0x0491:
            r0 = move-exception
            goto L_0x04a6
        L_0x0493:
            r13 = r76
            r1 = r37
            r10 = r78
            r6 = r13
            r9 = r50
            r11 = r51
            r2 = 0
            r16 = 0
            goto L_0x051f
        L_0x04a3:
            r0 = move-exception
            r13 = r76
        L_0x04a6:
            r1 = r37
        L_0x04a8:
            r6 = r13
            r11 = r51
            r13 = 0
            goto L_0x04c1
        L_0x04ad:
            r0 = move-exception
            r13 = r76
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
            boolean r2 = r1 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x0570, all -> 0x119e }
            if (r2 == 0) goto L_0x04e6
            if (r90 != 0) goto L_0x04e6
            r2 = 1
            goto L_0x04e7
        L_0x04e6:
            r2 = 0
        L_0x04e7:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x055e, all -> 0x119e }
            r3.<init>()     // Catch:{ Exception -> 0x055e, all -> 0x119e }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ Exception -> 0x055e, all -> 0x119e }
            r9 = r50
            r3.append(r9)     // Catch:{ Exception -> 0x055c, all -> 0x119e }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ Exception -> 0x055c, all -> 0x119e }
            r10 = r78
            r3.append(r10)     // Catch:{ Exception -> 0x0551, all -> 0x119e }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ Exception -> 0x0551, all -> 0x119e }
            r3.append(r11)     // Catch:{ Exception -> 0x0551, all -> 0x119e }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ Exception -> 0x0551, all -> 0x119e }
            r3.append(r12)     // Catch:{ Exception -> 0x0551, all -> 0x119e }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0551, all -> 0x119e }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ Exception -> 0x0551, all -> 0x119e }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x0551, all -> 0x119e }
            r16 = r2
            r1 = r37
            r2 = 1
        L_0x051f:
            if (r31 == 0) goto L_0x0533
            r31.release()     // Catch:{ Exception -> 0x0525, all -> 0x119e }
            goto L_0x0533
        L_0x0525:
            r0 = move-exception
            r34 = r83
            r48 = r85
            r2 = r0
            r14 = r9
            r9 = r10
            r7 = r11
            r8 = r12
            r1 = r16
            goto L_0x057e
        L_0x0533:
            if (r1 == 0) goto L_0x0538
            r1.release()     // Catch:{ Exception -> 0x0525, all -> 0x119e }
        L_0x0538:
            if (r6 == 0) goto L_0x0540
            r6.stop()     // Catch:{ Exception -> 0x0525, all -> 0x119e }
            r6.release()     // Catch:{ Exception -> 0x0525, all -> 0x119e }
        L_0x0540:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x0525, all -> 0x119e }
            r34 = r83
            r48 = r85
            r14 = r9
            r9 = r10
            r7 = r11
            r8 = r12
            r1 = r16
            r11 = r81
            goto L_0x1188
        L_0x0551:
            r0 = move-exception
            r34 = r83
            r48 = r85
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
            r34 = r83
            r48 = r85
            r1 = r2
            r14 = r9
            r7 = r11
            r8 = r12
            r9 = r78
        L_0x056b:
            r11 = r81
        L_0x056d:
            r2 = r0
            goto L_0x11b4
        L_0x0570:
            r0 = move-exception
            r9 = r50
            r34 = r83
            r48 = r85
            r2 = r0
            r14 = r9
            r7 = r11
            r8 = r12
            r1 = 0
            r9 = r78
        L_0x057e:
            r11 = r81
            goto L_0x11b4
        L_0x0582:
            r7 = r9
            r14 = r16
            r2 = r20
            r48 = r22
            r9 = r30
            r13 = 0
            android.media.MediaExtractor r3 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            r3.<init>()     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            r15.extractor = r3     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            r8 = r72
            r6 = r2
            r3.setDataSource(r8)     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            int r5 = org.telegram.messenger.MediaController.findTrack(r2, r13)     // Catch:{ Exception -> 0x11a3, all -> 0x119e }
            r2 = -1
            r4 = 2
            if (r7 == r2) goto L_0x05b7
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x05ab, all -> 0x119e }
            r3 = 1
            int r2 = org.telegram.messenger.MediaController.findTrack(r2, r3)     // Catch:{ Exception -> 0x05ab, all -> 0x119e }
            goto L_0x05b9
        L_0x05ab:
            r0 = move-exception
            r34 = r83
            r48 = r85
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
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x05cf, all -> 0x119e }
            android.media.MediaFormat r3 = r3.getTrackFormat(r5)     // Catch:{ Exception -> 0x05cf, all -> 0x119e }
            java.lang.String r3 = r3.getString(r11)     // Catch:{ Exception -> 0x05cf, all -> 0x119e }
            boolean r3 = r3.equals(r9)     // Catch:{ Exception -> 0x05cf, all -> 0x119e }
            if (r3 != 0) goto L_0x05dc
            r3 = 1
            goto L_0x05dd
        L_0x05cf:
            r0 = move-exception
            r34 = r83
            r48 = r85
            r2 = r0
            r14 = r7
            r9 = r10
            r8 = r12
            r1 = 0
            r7 = r77
            goto L_0x057e
        L_0x05dc:
            r3 = 0
        L_0x05dd:
            if (r89 != 0) goto L_0x0636
            if (r3 == 0) goto L_0x05e3
            goto L_0x0636
        L_0x05e3:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0625, all -> 0x119e }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ Exception -> 0x0625, all -> 0x119e }
            r1 = -1
            if (r7 == r1) goto L_0x05ed
            r16 = 1
            goto L_0x05ef
        L_0x05ed:
            r16 = 0
        L_0x05ef:
            r1 = r71
            r11 = 1
            r4 = r14
            r5 = r81
            r14 = r8
            r7 = r83
            r13 = r10
            r9 = r87
            r14 = 1
            r11 = r73
            r12 = r16
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ Exception -> 0x0614, all -> 0x119e }
            r8 = r76
            r7 = r77
            r14 = r79
            r11 = r81
            r34 = r83
            r48 = r85
            r9 = r13
            r1 = 0
            r2 = 0
            goto L_0x1188
        L_0x0614:
            r0 = move-exception
            r8 = r76
            r7 = r77
            r14 = r79
            r11 = r81
            r34 = r83
            r48 = r85
            r2 = r0
            r9 = r13
            goto L_0x11b3
        L_0x0625:
            r0 = move-exception
            r8 = r76
            r7 = r77
            r14 = r79
            r11 = r81
            r34 = r83
            r48 = r85
            r2 = r0
            r9 = r10
            goto L_0x11b3
        L_0x0636:
            r12 = r8
            r13 = r10
            r10 = r14
            r3 = -1
            r14 = 1
            if (r5 < 0) goto L_0x114d
            r16 = -2147483648(0xfffffffvar_, double:NaN)
            r7 = 1000(0x3e8, float:1.401E-42)
            r20 = -1
            int r7 = r7 / r13
            int r7 = r7 * 1000
            long r7 = (long) r7     // Catch:{ Exception -> 0x10c6, all -> 0x119e }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x10c6, all -> 0x119e }
            r3.selectTrack(r5)     // Catch:{ Exception -> 0x10c6, all -> 0x119e }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x10c6, all -> 0x119e }
            android.media.MediaFormat r3 = r3.getTrackFormat(r5)     // Catch:{ Exception -> 0x10c6, all -> 0x119e }
            r24 = 0
            int r22 = (r85 > r24 ? 1 : (r85 == r24 ? 0 : -1))
            if (r22 < 0) goto L_0x0662
            r22 = 1677721(0x199999, float:2.350988E-39)
            r1 = r80
            r14 = 1677721(0x199999, float:2.350988E-39)
            goto L_0x066e
        L_0x0662:
            if (r79 > 0) goto L_0x066a
            r1 = r80
            r14 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x066e
        L_0x066a:
            r14 = r79
            r1 = r80
        L_0x066e:
            if (r1 <= 0) goto L_0x0684
            int r14 = java.lang.Math.min(r1, r14)     // Catch:{ Exception -> 0x0675, all -> 0x119e }
            goto L_0x0684
        L_0x0675:
            r0 = move-exception
            r11 = r81
            r34 = r83
            r48 = r85
            r1 = r0
            r60 = r5
            r9 = r13
        L_0x0680:
            r3 = 0
            r6 = 0
            goto L_0x10d7
        L_0x0684:
            r24 = r5
            r25 = 0
            r4 = r81
            int r27 = (r4 > r25 ? 1 : (r4 == r25 ? 0 : -1))
            if (r27 >= 0) goto L_0x0691
            r27 = r25
            goto L_0x0693
        L_0x0691:
            r27 = r4
        L_0x0693:
            int r30 = (r85 > r25 ? 1 : (r85 == r25 ? 0 : -1))
            if (r30 < 0) goto L_0x06a0
            int r30 = (r27 > r85 ? 1 : (r27 == r85 ? 0 : -1))
            if (r30 != 0) goto L_0x06a0
            r27 = r7
            r7 = r20
            goto L_0x06a4
        L_0x06a0:
            r27 = r7
            r7 = r85
        L_0x06a4:
            int r30 = (r7 > r25 ? 1 : (r7 == r25 ? 0 : -1))
            if (r30 < 0) goto L_0x06c2
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x06b7, all -> 0x119e }
            r25 = r2
            r2 = 0
            r1.seekTo(r7, r2)     // Catch:{ Exception -> 0x06b7, all -> 0x119e }
        L_0x06b0:
            r2 = r95
            r85 = r7
            r7 = 0
            goto L_0x06dd
        L_0x06b7:
            r0 = move-exception
            r34 = r83
            r1 = r0
            r11 = r4
            r48 = r7
        L_0x06be:
            r9 = r13
            r60 = r24
            goto L_0x0680
        L_0x06c2:
            r25 = r2
            r1 = 0
            int r26 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r26 <= 0) goto L_0x06d1
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x06b7, all -> 0x119e }
            r2 = 0
            r1.seekTo(r4, r2)     // Catch:{ Exception -> 0x06b7, all -> 0x119e }
            goto L_0x06b0
        L_0x06d1:
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x10b6, all -> 0x119e }
            r85 = r7
            r2 = 0
            r7 = 0
            r1.seekTo(r7, r2)     // Catch:{ Exception -> 0x10b1, all -> 0x119e }
            r2 = r95
        L_0x06dd:
            if (r2 == 0) goto L_0x06fe
            r1 = 90
            r12 = r74
            if (r12 == r1) goto L_0x06ef
            r1 = 270(0x10e, float:3.78E-43)
            if (r12 != r1) goto L_0x06ea
            goto L_0x06ef
        L_0x06ea:
            int r1 = r2.transformWidth     // Catch:{ Exception -> 0x06f6, all -> 0x119e }
            int r7 = r2.transformHeight     // Catch:{ Exception -> 0x06f6, all -> 0x119e }
            goto L_0x06f3
        L_0x06ef:
            int r1 = r2.transformHeight     // Catch:{ Exception -> 0x06f6, all -> 0x119e }
            int r7 = r2.transformWidth     // Catch:{ Exception -> 0x06f6, all -> 0x119e }
        L_0x06f3:
            r8 = r7
            r7 = r1
            goto L_0x0704
        L_0x06f6:
            r0 = move-exception
            r34 = r83
            r48 = r85
            r1 = r0
            r11 = r4
            goto L_0x06be
        L_0x06fe:
            r12 = r74
            r7 = r76
            r8 = r77
        L_0x0704:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x10b1, all -> 0x119e }
            if (r1 == 0) goto L_0x0724
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06f6, all -> 0x119e }
            r1.<init>()     // Catch:{ Exception -> 0x06f6, all -> 0x119e }
            java.lang.String r2 = "create encoder with w = "
            r1.append(r2)     // Catch:{ Exception -> 0x06f6, all -> 0x119e }
            r1.append(r7)     // Catch:{ Exception -> 0x06f6, all -> 0x119e }
            java.lang.String r2 = " h = "
            r1.append(r2)     // Catch:{ Exception -> 0x06f6, all -> 0x119e }
            r1.append(r8)     // Catch:{ Exception -> 0x06f6, all -> 0x119e }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x06f6, all -> 0x119e }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x06f6, all -> 0x119e }
        L_0x0724:
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r9, r7, r8)     // Catch:{ Exception -> 0x10b1, all -> 0x119e }
            java.lang.String r2 = "color-format"
            r4 = 2130708361(0x7var_, float:1.701803E38)
            r1.setInteger(r2, r4)     // Catch:{ Exception -> 0x10a5, all -> 0x119e }
            java.lang.String r2 = "bitrate"
            r1.setInteger(r2, r14)     // Catch:{ Exception -> 0x10a5, all -> 0x119e }
            java.lang.String r2 = "frame-rate"
            r1.setInteger(r2, r13)     // Catch:{ Exception -> 0x10a5, all -> 0x119e }
            java.lang.String r2 = "i-frame-interval"
            r4 = 2
            r1.setInteger(r2, r4)     // Catch:{ Exception -> 0x10a5, all -> 0x119e }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x10a5, all -> 0x119e }
            r5 = 23
            if (r2 >= r5) goto L_0x0766
            int r2 = java.lang.Math.min(r8, r7)     // Catch:{ Exception -> 0x075c, all -> 0x119e }
            r5 = 480(0x1e0, float:6.73E-43)
            if (r2 > r5) goto L_0x0766
            r2 = 921600(0xe1000, float:1.291437E-39)
            if (r14 <= r2) goto L_0x0756
            r14 = 921600(0xe1000, float:1.291437E-39)
        L_0x0756:
            java.lang.String r2 = "bitrate"
            r1.setInteger(r2, r14)     // Catch:{ Exception -> 0x075c, all -> 0x119e }
            goto L_0x0766
        L_0x075c:
            r0 = move-exception
            r11 = r81
            r34 = r83
            r48 = r85
            r1 = r0
            goto L_0x06be
        L_0x0766:
            android.media.MediaCodec r5 = android.media.MediaCodec.createEncoderByType(r9)     // Catch:{ Exception -> 0x109a, all -> 0x119e }
            r2 = 0
            r4 = 1
            r5.configure(r1, r2, r2, r4)     // Catch:{ Exception -> 0x1085, all -> 0x119e }
            org.telegram.messenger.video.InputSurface r4 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x1085, all -> 0x119e }
            android.view.Surface r1 = r5.createInputSurface()     // Catch:{ Exception -> 0x1085, all -> 0x119e }
            r4.<init>(r1)     // Catch:{ Exception -> 0x1085, all -> 0x119e }
            r4.makeCurrent()     // Catch:{ Exception -> 0x1068, all -> 0x119e }
            r5.start()     // Catch:{ Exception -> 0x1068, all -> 0x119e }
            java.lang.String r1 = r3.getString(r11)     // Catch:{ Exception -> 0x1068, all -> 0x119e }
            android.media.MediaCodec r1 = android.media.MediaCodec.createDecoderByType(r1)     // Catch:{ Exception -> 0x1068, all -> 0x119e }
            org.telegram.messenger.video.OutputSurface r22 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x1050, all -> 0x119e }
            r26 = 0
            r29 = r3
            float r3 = (float) r13
            r30 = 0
            r52 = r1
            r1 = r22
            r53 = r25
            r2 = r91
            r58 = r23
            r56 = r29
            r57 = r48
            r23 = r3
            r3 = r26
            r59 = r4
            r25 = 2
            r4 = r92
            r79 = r5
            r60 = r24
            r5 = r93
            r61 = r6
            r6 = r95
            r62 = r7
            r36 = r27
            r31 = 0
            r27 = r85
            r7 = r76
            r63 = r8
            r8 = r77
            r66 = r9
            r9 = r74
            r67 = r10
            r10 = r23
            r12 = r11
            r11 = r30
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x103f, all -> 0x119e }
            android.view.Surface r1 = r22.getSurface()     // Catch:{ Exception -> 0x102a, all -> 0x119e }
            r3 = r52
            r2 = r56
            r4 = 0
            r5 = 0
            r3.configure(r2, r1, r4, r5)     // Catch:{ Exception -> 0x101f, all -> 0x119e }
            r3.start()     // Catch:{ Exception -> 0x101f, all -> 0x119e }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x101f, all -> 0x119e }
            r2 = 21
            if (r1 >= r2) goto L_0x0803
            java.nio.ByteBuffer[] r6 = r3.getInputBuffers()     // Catch:{ Exception -> 0x07f3, all -> 0x119e }
            java.nio.ByteBuffer[] r1 = r79.getOutputBuffers()     // Catch:{ Exception -> 0x07f3, all -> 0x119e }
            r2 = r53
            r68 = r6
            r6 = r1
            r1 = r68
            goto L_0x0807
        L_0x07f3:
            r0 = move-exception
            r10 = r79
            r11 = r81
            r34 = r83
            r1 = r0
            r43 = r4
        L_0x07fd:
            r9 = r13
            r48 = r27
        L_0x0800:
            r6 = 0
            goto L_0x10de
        L_0x0803:
            r1 = r4
            r6 = r1
            r2 = r53
        L_0x0807:
            if (r2 < 0) goto L_0x08fe
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x08ec, all -> 0x119e }
            android.media.MediaFormat r5 = r5.getTrackFormat(r2)     // Catch:{ Exception -> 0x08ec, all -> 0x119e }
            java.lang.String r7 = r5.getString(r12)     // Catch:{ Exception -> 0x08ec, all -> 0x119e }
            java.lang.String r8 = "audio/mp4a-latm"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x08ec, all -> 0x119e }
            if (r7 != 0) goto L_0x082a
            java.lang.String r7 = r5.getString(r12)     // Catch:{ Exception -> 0x07f3, all -> 0x119e }
            java.lang.String r8 = "audio/mpeg"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x07f3, all -> 0x119e }
            if (r7 == 0) goto L_0x0828
            goto L_0x082a
        L_0x0828:
            r7 = 0
            goto L_0x082b
        L_0x082a:
            r7 = 1
        L_0x082b:
            java.lang.String r8 = r5.getString(r12)     // Catch:{ Exception -> 0x08ec, all -> 0x119e }
            java.lang.String r9 = "audio/unknown"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x08ec, all -> 0x119e }
            if (r8 == 0) goto L_0x0838
            r2 = -1
        L_0x0838:
            if (r2 < 0) goto L_0x08e0
            if (r7 == 0) goto L_0x0883
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x0879, all -> 0x119e }
            r9 = 1
            int r8 = r8.addTrack(r5, r9)     // Catch:{ Exception -> 0x0879, all -> 0x119e }
            android.media.MediaExtractor r9 = r15.extractor     // Catch:{ Exception -> 0x0879, all -> 0x119e }
            r9.selectTrack(r2)     // Catch:{ Exception -> 0x0879, all -> 0x119e }
            java.lang.String r9 = "max-input-size"
            int r5 = r5.getInteger(r9)     // Catch:{ Exception -> 0x0879, all -> 0x119e }
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocateDirect(r5)     // Catch:{ Exception -> 0x0879, all -> 0x119e }
            r11 = r81
            r9 = 0
            int r23 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r23 <= 0) goto L_0x0863
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x08a1, all -> 0x119e }
            r9 = 0
            r4.seekTo(r11, r9)     // Catch:{ Exception -> 0x08a1, all -> 0x119e }
            r85 = r5
            goto L_0x086d
        L_0x0863:
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x08a1, all -> 0x119e }
            r85 = r5
            r5 = 0
            r9 = 0
            r4.seekTo(r9, r5)     // Catch:{ Exception -> 0x08a1, all -> 0x119e }
        L_0x086d:
            r4 = r83
            r10 = r85
            r85 = r6
            r6 = r8
            r9 = 0
            r8 = r72
            goto L_0x090a
        L_0x0879:
            r0 = move-exception
            r10 = r79
            r11 = r81
        L_0x087e:
            r34 = r83
            r1 = r0
            goto L_0x08f8
        L_0x0883:
            r11 = r81
            android.media.MediaExtractor r4 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x08d8, all -> 0x119e }
            r4.<init>()     // Catch:{ Exception -> 0x08d8, all -> 0x119e }
            r8 = r72
            r4.setDataSource(r8)     // Catch:{ Exception -> 0x08d6, all -> 0x119e }
            r4.selectTrack(r2)     // Catch:{ Exception -> 0x08d6, all -> 0x119e }
            r9 = 0
            int r23 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r23 <= 0) goto L_0x08a5
            r9 = 0
            r4.seekTo(r11, r9)     // Catch:{ Exception -> 0x08a1, all -> 0x119e }
            r85 = r6
            r86 = r7
            goto L_0x08ae
        L_0x08a1:
            r0 = move-exception
            r10 = r79
            goto L_0x087e
        L_0x08a5:
            r85 = r6
            r86 = r7
            r6 = r9
            r9 = 0
            r4.seekTo(r6, r9)     // Catch:{ Exception -> 0x08d6, all -> 0x119e }
        L_0x08ae:
            org.telegram.messenger.video.AudioRecoder r6 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x08d6, all -> 0x119e }
            r6.<init>(r5, r4, r2)     // Catch:{ Exception -> 0x08d6, all -> 0x119e }
            r6.startTime = r11     // Catch:{ Exception -> 0x08ca, all -> 0x119e }
            r4 = r83
            r6.endTime = r4     // Catch:{ Exception -> 0x08c8, all -> 0x119e }
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ Exception -> 0x08c8, all -> 0x119e }
            android.media.MediaFormat r9 = r6.format     // Catch:{ Exception -> 0x08c8, all -> 0x119e }
            r10 = 1
            int r7 = r7.addTrack(r9, r10)     // Catch:{ Exception -> 0x08c8, all -> 0x119e }
            r9 = r6
            r6 = r7
            r10 = 0
            r7 = r86
            goto L_0x090a
        L_0x08c8:
            r0 = move-exception
            goto L_0x08cd
        L_0x08ca:
            r0 = move-exception
            r4 = r83
        L_0x08cd:
            r10 = r79
            r1 = r0
            r34 = r4
            r43 = r6
            goto L_0x07fd
        L_0x08d6:
            r0 = move-exception
            goto L_0x08db
        L_0x08d8:
            r0 = move-exception
            r8 = r72
        L_0x08db:
            r4 = r83
            r10 = r79
            goto L_0x08f5
        L_0x08e0:
            r8 = r72
            r11 = r81
            r4 = r83
            r85 = r6
            r86 = r7
            r6 = -5
            goto L_0x0908
        L_0x08ec:
            r0 = move-exception
            r8 = r72
            r4 = r83
            r10 = r79
            r11 = r81
        L_0x08f5:
            r1 = r0
            r34 = r4
        L_0x08f8:
            r9 = r13
            r48 = r27
            r6 = 0
            goto L_0x1081
        L_0x08fe:
            r8 = r72
            r11 = r81
            r4 = r83
            r85 = r6
            r6 = -5
            r7 = 1
        L_0x0908:
            r9 = 0
            r10 = 0
        L_0x090a:
            if (r2 >= 0) goto L_0x090f
            r23 = 1
            goto L_0x0911
        L_0x090f:
            r23 = 0
        L_0x0911:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x100b, all -> 0x119e }
            r50 = r16
            r52 = r20
            r29 = r23
            r48 = r27
            r16 = 0
            r17 = 0
            r23 = 0
            r24 = 0
            r26 = -5
            r27 = 1
            r39 = 0
            r46 = 0
            r28 = r85
        L_0x092e:
            if (r16 == 0) goto L_0x0946
            if (r7 != 0) goto L_0x0935
            if (r29 != 0) goto L_0x0935
            goto L_0x0946
        L_0x0935:
            r8 = r76
            r7 = r77
            r6 = r79
            r1 = r3
            r34 = r4
            r43 = r9
            r9 = r13
            r2 = 0
            r38 = 0
            goto L_0x111f
        L_0x0946:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x0ff7, all -> 0x119e }
            if (r7 != 0) goto L_0x095f
            if (r9 == 0) goto L_0x095f
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x0954, all -> 0x119e }
            boolean r8 = r9.step(r8, r6)     // Catch:{ Exception -> 0x0954, all -> 0x119e }
            goto L_0x0961
        L_0x0954:
            r0 = move-exception
            r10 = r79
            r1 = r0
            r34 = r4
            r43 = r9
            r9 = r13
            goto L_0x0800
        L_0x095f:
            r8 = r29
        L_0x0961:
            if (r24 != 0) goto L_0x0abf
            r83 = r8
            android.media.MediaExtractor r8 = r15.extractor     // Catch:{ Exception -> 0x0aad, all -> 0x119e }
            int r8 = r8.getSampleTrackIndex()     // Catch:{ Exception -> 0x0aad, all -> 0x119e }
            r85 = r9
            r9 = r60
            if (r8 != r9) goto L_0x09d8
            r86 = r14
            r13 = 2500(0x9c4, double:1.235E-320)
            int r8 = r3.dequeueInputBuffer(r13)     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            if (r8 < 0) goto L_0x09bc
            int r13 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            r14 = 21
            if (r13 >= r14) goto L_0x0984
            r13 = r1[r8]     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            goto L_0x0988
        L_0x0984:
            java.nio.ByteBuffer r13 = r3.getInputBuffer(r8)     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
        L_0x0988:
            android.media.MediaExtractor r14 = r15.extractor     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            r55 = r1
            r1 = 0
            int r32 = r14.readSampleData(r13, r1)     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            if (r32 >= 0) goto L_0x09a5
            r31 = 0
            r32 = 0
            r33 = 0
            r35 = 4
            r29 = r3
            r30 = r8
            r29.queueInputBuffer(r30, r31, r32, r33, r35)     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            r24 = 1
            goto L_0x09be
        L_0x09a5:
            r31 = 0
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            long r33 = r1.getSampleTime()     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            r35 = 0
            r29 = r3
            r30 = r8
            r29.queueInputBuffer(r30, r31, r32, r33, r35)     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            r1.advance()     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            goto L_0x09be
        L_0x09bc:
            r55 = r1
        L_0x09be:
            r13 = r2
            r64 = r4
            r56 = r6
            r14 = r67
            goto L_0x0a85
        L_0x09c7:
            r0 = move-exception
            r10 = r79
        L_0x09ca:
            r43 = r85
            r14 = r86
            r1 = r0
            r34 = r4
            r60 = r9
        L_0x09d3:
            r6 = 0
            r9 = r78
            goto L_0x10de
        L_0x09d8:
            r55 = r1
            r86 = r14
            if (r7 == 0) goto L_0x0a79
            r1 = -1
            if (r2 == r1) goto L_0x0a71
            if (r8 != r2) goto L_0x0a71
            android.media.MediaExtractor r8 = r15.extractor     // Catch:{ Exception -> 0x0a6d, all -> 0x119e }
            r13 = 0
            int r8 = r8.readSampleData(r10, r13)     // Catch:{ Exception -> 0x0a6d, all -> 0x119e }
            r14 = r67
            r14.size = r8     // Catch:{ Exception -> 0x0a6d, all -> 0x119e }
            int r8 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a6d, all -> 0x119e }
            r1 = 21
            if (r8 >= r1) goto L_0x09fc
            r10.position(r13)     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            int r1 = r14.size     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            r10.limit(r1)     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
        L_0x09fc:
            int r1 = r14.size     // Catch:{ Exception -> 0x0a6d, all -> 0x119e }
            if (r1 < 0) goto L_0x0a11
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            r13 = r2
            long r1 = r1.getSampleTime()     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            r14.presentationTimeUs = r1     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            r1.advance()     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            r2 = r24
            goto L_0x0a16
        L_0x0a11:
            r13 = r2
            r1 = 0
            r14.size = r1     // Catch:{ Exception -> 0x0a6d, all -> 0x119e }
            r2 = 1
        L_0x0a16:
            int r1 = r14.size     // Catch:{ Exception -> 0x0a6d, all -> 0x119e }
            if (r1 <= 0) goto L_0x0a64
            r29 = 0
            int r1 = (r4 > r29 ? 1 : (r4 == r29 ? 0 : -1))
            if (r1 < 0) goto L_0x0a29
            r84 = r2
            long r1 = r14.presentationTimeUs     // Catch:{ Exception -> 0x09c7, all -> 0x119e }
            int r8 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r8 >= 0) goto L_0x0a66
            goto L_0x0a2b
        L_0x0a29:
            r84 = r2
        L_0x0a2b:
            r1 = 0
            r14.offset = r1     // Catch:{ Exception -> 0x0a6d, all -> 0x119e }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0a6d, all -> 0x119e }
            int r2 = r2.getSampleFlags()     // Catch:{ Exception -> 0x0a6d, all -> 0x119e }
            r14.flags = r2     // Catch:{ Exception -> 0x0a6d, all -> 0x119e }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0a6d, all -> 0x119e }
            r64 = r4
            long r4 = r2.writeSampleData(r6, r10, r14, r1)     // Catch:{ Exception -> 0x0a9f, all -> 0x119e }
            r1 = 0
            int r8 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r8 == 0) goto L_0x0a68
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r15.callback     // Catch:{ Exception -> 0x0a9f, all -> 0x119e }
            if (r1 == 0) goto L_0x0a68
            long r1 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0a9f, all -> 0x119e }
            long r1 = r1 - r11
            int r8 = (r1 > r39 ? 1 : (r1 == r39 ? 0 : -1))
            if (r8 <= 0) goto L_0x0a53
            long r1 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0a9f, all -> 0x119e }
            long r39 = r1 - r11
        L_0x0a53:
            r1 = r39
            org.telegram.messenger.MediaController$VideoConvertorListener r8 = r15.callback     // Catch:{ Exception -> 0x0a9f, all -> 0x119e }
            r56 = r6
            float r6 = (float) r1     // Catch:{ Exception -> 0x0a9f, all -> 0x119e }
            float r6 = r6 / r18
            float r6 = r6 / r19
            r8.didWriteData(r4, r6)     // Catch:{ Exception -> 0x0a9f, all -> 0x119e }
            r39 = r1
            goto L_0x0a6a
        L_0x0a64:
            r84 = r2
        L_0x0a66:
            r64 = r4
        L_0x0a68:
            r56 = r6
        L_0x0a6a:
            r24 = r84
            goto L_0x0a85
        L_0x0a6d:
            r0 = move-exception
            r64 = r4
            goto L_0x0aa0
        L_0x0a71:
            r13 = r2
            r64 = r4
            r56 = r6
            r14 = r67
            goto L_0x0a81
        L_0x0a79:
            r13 = r2
            r64 = r4
            r56 = r6
            r14 = r67
            r1 = -1
        L_0x0a81:
            if (r8 != r1) goto L_0x0a85
            r2 = 1
            goto L_0x0a86
        L_0x0a85:
            r2 = 0
        L_0x0a86:
            if (r2 == 0) goto L_0x0ad0
            r1 = 2500(0x9c4, double:1.235E-320)
            int r30 = r3.dequeueInputBuffer(r1)     // Catch:{ Exception -> 0x0a9f, all -> 0x119e }
            if (r30 < 0) goto L_0x0ad0
            r31 = 0
            r32 = 0
            r33 = 0
            r35 = 4
            r29 = r3
            r29.queueInputBuffer(r30, r31, r32, r33, r35)     // Catch:{ Exception -> 0x0a9f, all -> 0x119e }
            r2 = 1
            goto L_0x0ad2
        L_0x0a9f:
            r0 = move-exception
        L_0x0aa0:
            r10 = r79
            r43 = r85
            r14 = r86
            r1 = r0
            r60 = r9
            r34 = r64
            goto L_0x09d3
        L_0x0aad:
            r0 = move-exception
            r64 = r4
            r85 = r9
            r86 = r14
            r9 = r78
            r10 = r79
            r43 = r85
            r1 = r0
            r34 = r64
            goto L_0x0800
        L_0x0abf:
            r55 = r1
            r13 = r2
            r64 = r4
            r56 = r6
            r83 = r8
            r85 = r9
            r86 = r14
            r9 = r60
            r14 = r67
        L_0x0ad0:
            r2 = r24
        L_0x0ad2:
            r1 = r17 ^ 1
            r24 = r2
            r6 = r26
            r4 = r64
            r2 = 1
            r26 = r7
            r7 = r50
        L_0x0adf:
            if (r1 != 0) goto L_0x0aff
            if (r2 == 0) goto L_0x0ae4
            goto L_0x0aff
        L_0x0ae4:
            r29 = r83
            r50 = r7
            r60 = r9
            r2 = r13
            r67 = r14
            r7 = r26
            r1 = r55
            r8 = r72
            r13 = r78
            r9 = r85
            r14 = r86
            r26 = r6
            r6 = r56
            goto L_0x092e
        L_0x0aff:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x0fe4, all -> 0x119e }
            if (r90 == 0) goto L_0x0b15
            r29 = 22000(0x55f0, double:1.08694E-319)
            r84 = r1
            r68 = r10
            r10 = r79
            r69 = r29
            r29 = r2
            r30 = r68
            r1 = r69
            goto L_0x0b1f
        L_0x0b15:
            r84 = r1
            r29 = r2
            r30 = r10
            r1 = 2500(0x9c4, double:1.235E-320)
            r10 = r79
        L_0x0b1f:
            int r1 = r10.dequeueOutputBuffer(r14, r1)     // Catch:{ Exception -> 0x0fe2, all -> 0x119e }
            r2 = -1
            if (r1 != r2) goto L_0x0b3f
            r34 = r4
            r31 = r7
            r60 = r9
            r79 = r13
            r9 = r57
            r13 = r61
            r2 = r62
            r4 = r63
            r11 = r66
            r5 = 3
            r7 = 1
            r8 = -1
            r29 = 0
            goto L_0x0d34
        L_0x0b3f:
            r2 = -3
            if (r1 != r2) goto L_0x0b66
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b63, all -> 0x119e }
            r79 = r13
            r13 = 21
            if (r2 >= r13) goto L_0x0b4e
            java.nio.ByteBuffer[] r28 = r10.getOutputBuffers()     // Catch:{ Exception -> 0x0b63, all -> 0x119e }
        L_0x0b4e:
            r34 = r4
            r31 = r7
            r60 = r9
            r9 = r57
            r13 = r61
        L_0x0b58:
            r2 = r62
            r4 = r63
            r11 = r66
            r5 = 3
            r7 = 1
        L_0x0b60:
            r8 = -1
            goto L_0x0d34
        L_0x0b63:
            r0 = move-exception
            goto L_0x09ca
        L_0x0b66:
            r79 = r13
            r2 = -2
            if (r1 != r2) goto L_0x0bd1
            android.media.MediaFormat r2 = r10.getOutputFormat()     // Catch:{ Exception -> 0x0bc3, all -> 0x119e }
            r13 = -5
            if (r6 != r13) goto L_0x0bb8
            if (r2 == 0) goto L_0x0bb8
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x0bc3, all -> 0x119e }
            r13 = 0
            int r6 = r6.addTrack(r2, r13)     // Catch:{ Exception -> 0x0bc3, all -> 0x119e }
            r13 = r58
            boolean r31 = r2.containsKey(r13)     // Catch:{ Exception -> 0x0bc3, all -> 0x119e }
            if (r31 == 0) goto L_0x0bab
            r31 = r6
            int r6 = r2.getInteger(r13)     // Catch:{ Exception -> 0x0bc3, all -> 0x119e }
            r58 = r13
            r13 = 1
            if (r6 != r13) goto L_0x0ba8
            r13 = r61
            java.nio.ByteBuffer r6 = r2.getByteBuffer(r13)     // Catch:{ Exception -> 0x0bc3, all -> 0x119e }
            r60 = r9
            r9 = r57
            java.nio.ByteBuffer r2 = r2.getByteBuffer(r9)     // Catch:{ Exception -> 0x0be4, all -> 0x119e }
            int r6 = r6.limit()     // Catch:{ Exception -> 0x0be4, all -> 0x119e }
            int r2 = r2.limit()     // Catch:{ Exception -> 0x0be4, all -> 0x119e }
            int r6 = r6 + r2
            r23 = r6
            goto L_0x0bb5
        L_0x0ba8:
            r60 = r9
            goto L_0x0bb1
        L_0x0bab:
            r31 = r6
            r60 = r9
            r58 = r13
        L_0x0bb1:
            r9 = r57
            r13 = r61
        L_0x0bb5:
            r6 = r31
            goto L_0x0bbe
        L_0x0bb8:
            r60 = r9
            r9 = r57
            r13 = r61
        L_0x0bbe:
            r34 = r4
            r31 = r7
            goto L_0x0b58
        L_0x0bc3:
            r0 = move-exception
            r60 = r9
        L_0x0bc6:
            r9 = r78
            r43 = r85
            r14 = r86
            r1 = r0
            r34 = r4
            goto L_0x0800
        L_0x0bd1:
            r60 = r9
            r9 = r57
            r13 = r61
            if (r1 < 0) goto L_0x0fba
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0fb1, all -> 0x119e }
            r31 = r7
            r7 = 21
            if (r2 >= r7) goto L_0x0be6
            r2 = r28[r1]     // Catch:{ Exception -> 0x0be4, all -> 0x119e }
            goto L_0x0bea
        L_0x0be4:
            r0 = move-exception
            goto L_0x0bc6
        L_0x0be6:
            java.nio.ByteBuffer r2 = r10.getOutputBuffer(r1)     // Catch:{ Exception -> 0x0fb1, all -> 0x119e }
        L_0x0bea:
            if (r2 == 0) goto L_0x0f8e
            int r8 = r14.size     // Catch:{ Exception -> 0x0fb1, all -> 0x119e }
            r7 = 1
            if (r8 <= r7) goto L_0x0d1a
            int r8 = r14.flags     // Catch:{ Exception -> 0x0d15, all -> 0x119e }
            r8 = r8 & 2
            if (r8 != 0) goto L_0x0c9d
            if (r23 == 0) goto L_0x0c0a
            int r8 = r14.flags     // Catch:{ Exception -> 0x0be4, all -> 0x119e }
            r8 = r8 & r7
            if (r8 == 0) goto L_0x0c0a
            int r7 = r14.offset     // Catch:{ Exception -> 0x0be4, all -> 0x119e }
            int r7 = r7 + r23
            r14.offset = r7     // Catch:{ Exception -> 0x0be4, all -> 0x119e }
            int r7 = r14.size     // Catch:{ Exception -> 0x0be4, all -> 0x119e }
            int r7 = r7 - r23
            r14.size = r7     // Catch:{ Exception -> 0x0be4, all -> 0x119e }
        L_0x0c0a:
            if (r27 == 0) goto L_0x0CLASSNAME
            int r7 = r14.flags     // Catch:{ Exception -> 0x0c5d, all -> 0x119e }
            r8 = 1
            r7 = r7 & r8
            if (r7 == 0) goto L_0x0CLASSNAME
            int r7 = r14.size     // Catch:{ Exception -> 0x0c5d, all -> 0x119e }
            r8 = 100
            if (r7 <= r8) goto L_0x0CLASSNAME
            int r7 = r14.offset     // Catch:{ Exception -> 0x0c5d, all -> 0x119e }
            r2.position(r7)     // Catch:{ Exception -> 0x0c5d, all -> 0x119e }
            byte[] r7 = new byte[r8]     // Catch:{ Exception -> 0x0c5d, all -> 0x119e }
            r2.get(r7)     // Catch:{ Exception -> 0x0c5d, all -> 0x119e }
            r8 = 0
            r16 = 0
        L_0x0CLASSNAME:
            r34 = r4
            r4 = 96
            if (r8 >= r4) goto L_0x0c5a
            byte r4 = r7[r8]     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x119e }
            if (r4 != 0) goto L_0x0CLASSNAME
            int r4 = r8 + 1
            byte r4 = r7[r4]     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x119e }
            if (r4 != 0) goto L_0x0CLASSNAME
            int r4 = r8 + 2
            byte r4 = r7[r4]     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x119e }
            if (r4 != 0) goto L_0x0CLASSNAME
            int r4 = r8 + 3
            byte r4 = r7[r4]     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x119e }
            r5 = 1
            if (r4 != r5) goto L_0x0CLASSNAME
            int r4 = r16 + 1
            if (r4 <= r5) goto L_0x0CLASSNAME
            int r4 = r14.offset     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x119e }
            int r4 = r4 + r8
            r14.offset = r4     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x119e }
            int r4 = r14.size     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x119e }
            int r4 = r4 - r8
            r14.size = r4     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x119e }
            goto L_0x0c5a
        L_0x0CLASSNAME:
            r16 = r4
        L_0x0CLASSNAME:
            int r8 = r8 + 1
            r4 = r34
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r34 = r4
        L_0x0c5a:
            r27 = 0
            goto L_0x0CLASSNAME
        L_0x0c5d:
            r0 = move-exception
            r34 = r4
        L_0x0CLASSNAME:
            r9 = r78
            goto L_0x0dad
        L_0x0CLASSNAME:
            r34 = r4
        L_0x0CLASSNAME:
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            r5 = 1
            long r7 = r4.writeSampleData(r6, r2, r14, r5)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            r4 = 0
            int r2 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MediaController$VideoConvertorListener r2 = r15.callback     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            if (r2 == 0) goto L_0x0CLASSNAME
            long r4 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            long r4 = r4 - r11
            int r2 = (r4 > r39 ? 1 : (r4 == r39 ? 0 : -1))
            if (r2 <= 0) goto L_0x0CLASSNAME
            long r4 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x119e }
            long r39 = r4 - r11
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = move-exception
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r4 = r39
            org.telegram.messenger.MediaController$VideoConvertorListener r2 = r15.callback     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            float r11 = (float) r4     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            float r11 = r11 / r18
            float r11 = r11 / r19
            r2.didWriteData(r7, r11)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            r39 = r4
        L_0x0CLASSNAME:
            r2 = r62
            r4 = r63
            r11 = r66
            r5 = 3
            r7 = 1
            goto L_0x0d23
        L_0x0c9d:
            r34 = r4
            r4 = -5
            if (r6 != r4) goto L_0x0CLASSNAME
            int r4 = r14.size     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            byte[] r4 = new byte[r4]     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            int r5 = r14.offset     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            int r6 = r14.size     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            int r5 = r5 + r6
            r2.limit(r5)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            int r5 = r14.offset     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            r2.position(r5)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            r2.get(r4)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            int r2 = r14.size     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            r7 = 1
            int r2 = r2 - r7
        L_0x0cba:
            r5 = 3
            if (r2 < 0) goto L_0x0cf7
            if (r2 <= r5) goto L_0x0cf7
            byte r6 = r4[r2]     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            if (r6 != r7) goto L_0x0cf4
            int r6 = r2 + -1
            byte r6 = r4[r6]     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            if (r6 != 0) goto L_0x0cf4
            int r6 = r2 + -2
            byte r6 = r4[r6]     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            if (r6 != 0) goto L_0x0cf4
            int r6 = r2 + -3
            byte r8 = r4[r6]     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            if (r8 != 0) goto L_0x0cf4
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r6)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            int r8 = r14.size     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            int r8 = r8 - r6
            java.nio.ByteBuffer r8 = java.nio.ByteBuffer.allocate(r8)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            r11 = 0
            java.nio.ByteBuffer r12 = r2.put(r4, r11, r6)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            r12.position(r11)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            int r12 = r14.size     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            int r12 = r12 - r6
            java.nio.ByteBuffer r4 = r8.put(r4, r6, r12)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            r4.position(r11)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            r6 = r2
            goto L_0x0cf9
        L_0x0cf4:
            int r2 = r2 + -1
            goto L_0x0cba
        L_0x0cf7:
            r6 = 0
            r8 = 0
        L_0x0cf9:
            r2 = r62
            r4 = r63
            r11 = r66
            android.media.MediaFormat r12 = android.media.MediaFormat.createVideoFormat(r11, r2, r4)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            if (r6 == 0) goto L_0x0d0d
            if (r8 == 0) goto L_0x0d0d
            r12.setByteBuffer(r13, r6)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            r12.setByteBuffer(r9, r8)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
        L_0x0d0d:
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            r8 = 0
            int r6 = r6.addTrack(r12, r8)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            goto L_0x0d23
        L_0x0d15:
            r0 = move-exception
            r34 = r4
            goto L_0x0da9
        L_0x0d1a:
            r34 = r4
            r2 = r62
            r4 = r63
            r11 = r66
            r5 = 3
        L_0x0d23:
            int r8 = r14.flags     // Catch:{ Exception -> 0x0var_, all -> 0x119e }
            r8 = r8 & 4
            if (r8 == 0) goto L_0x0d2b
            r8 = 1
            goto L_0x0d2c
        L_0x0d2b:
            r8 = 0
        L_0x0d2c:
            r12 = 0
            r10.releaseOutputBuffer(r1, r12)     // Catch:{ Exception -> 0x0var_, all -> 0x119e }
            r16 = r8
            goto L_0x0b60
        L_0x0d34:
            if (r1 == r8) goto L_0x0d54
            r1 = r84
            r62 = r2
            r63 = r4
            r57 = r9
            r66 = r11
            r61 = r13
            r2 = r29
            r7 = r31
            r4 = r34
            r9 = r60
            r13 = r79
            r11 = r81
        L_0x0d4e:
            r79 = r10
            r10 = r30
            goto L_0x0adf
        L_0x0d54:
            if (r17 != 0) goto L_0x0var_
            r12 = r6
            r5 = 2500(0x9c4, double:1.235E-320)
            int r1 = r3.dequeueOutputBuffer(r14, r5)     // Catch:{ Exception -> 0x0var_, all -> 0x119e }
            if (r1 != r8) goto L_0x0d79
            r62 = r2
            r63 = r4
            r41 = r5
            r57 = r9
            r66 = r11
            r38 = r12
            r7 = r31
            r4 = r34
            r2 = r59
            r1 = 0
            r6 = 0
            r9 = r78
            r11 = r81
            goto L_0x0var_
        L_0x0d79:
            r5 = -3
            if (r1 != r5) goto L_0x0d88
        L_0x0d7c:
            r62 = r2
            r63 = r4
            r57 = r9
            r66 = r11
            r38 = r12
            goto L_0x0var_
        L_0x0d88:
            r5 = -2
            if (r1 != r5) goto L_0x0db4
            android.media.MediaFormat r1 = r3.getOutputFormat()     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            if (r5 == 0) goto L_0x0d7c
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            r5.<init>()     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            java.lang.String r6 = "newFormat = "
            r5.append(r6)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            r5.append(r1)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            goto L_0x0d7c
        L_0x0da8:
            r0 = move-exception
        L_0x0da9:
            r9 = r78
            r11 = r81
        L_0x0dad:
            r43 = r85
            r14 = r86
            r1 = r0
            goto L_0x0800
        L_0x0db4:
            if (r1 < 0) goto L_0x0var_
            int r5 = r14.size     // Catch:{ Exception -> 0x0var_, all -> 0x119e }
            if (r5 == 0) goto L_0x0dbc
            r5 = 1
            goto L_0x0dbd
        L_0x0dbc:
            r5 = 0
        L_0x0dbd:
            long r7 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0var_, all -> 0x119e }
            r44 = 0
            int r6 = (r34 > r44 ? 1 : (r34 == r44 ? 0 : -1))
            if (r6 <= 0) goto L_0x0dd4
            int r6 = (r7 > r34 ? 1 : (r7 == r34 ? 0 : -1))
            if (r6 < 0) goto L_0x0dd4
            int r5 = r14.flags     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            r5 = r5 | 4
            r14.flags = r5     // Catch:{ Exception -> 0x0da8, all -> 0x119e }
            r5 = 0
            r17 = 1
            r24 = 1
        L_0x0dd4:
            r44 = 0
            int r6 = (r48 > r44 ? 1 : (r48 == r44 ? 0 : -1))
            if (r6 < 0) goto L_0x0e57
            int r6 = r14.flags     // Catch:{ Exception -> 0x0e50, all -> 0x119e }
            r6 = r6 & 4
            if (r6 == 0) goto L_0x0e57
            r66 = r11
            r6 = r12
            r11 = r81
            long r50 = r48 - r11
            long r50 = java.lang.Math.abs(r50)     // Catch:{ Exception -> 0x0e4b, all -> 0x119e }
            r54 = 1000000(0xvar_, float:1.401298E-39)
            r62 = r2
            r57 = r9
            r41 = 2500(0x9c4, double:1.235E-320)
            r9 = r78
            int r2 = r54 / r9
            r63 = r4
            r54 = r5
            long r4 = (long) r2
            int r2 = (r50 > r4 ? 1 : (r50 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e43
            r4 = 0
            int r2 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e13
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0e11, all -> 0x119e }
            r4 = 0
            r2.seekTo(r11, r4)     // Catch:{ Exception -> 0x0e11, all -> 0x119e }
            r38 = r6
            r6 = 0
            goto L_0x0e1d
        L_0x0e11:
            r0 = move-exception
            goto L_0x0dad
        L_0x0e13:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0e48, all -> 0x119e }
            r38 = r6
            r4 = 0
            r6 = 0
            r2.seekTo(r4, r6)     // Catch:{ Exception -> 0x0e40, all -> 0x119e }
        L_0x0e1d:
            long r46 = r31 + r36
            int r2 = r14.flags     // Catch:{ Exception -> 0x0e34, all -> 0x119e }
            r4 = -5
            r2 = r2 & r4
            r14.flags = r2     // Catch:{ Exception -> 0x0e34, all -> 0x119e }
            r3.flush()     // Catch:{ Exception -> 0x0e34, all -> 0x119e }
            r33 = r20
            r2 = 1
            r17 = 0
            r24 = 0
            r44 = 0
            r54 = 0
            goto L_0x0e74
        L_0x0e34:
            r0 = move-exception
            r43 = r85
            r14 = r86
            r1 = r0
            r34 = r48
            r48 = r20
            goto L_0x10de
        L_0x0e40:
            r0 = move-exception
            goto L_0x0ff0
        L_0x0e43:
            r38 = r6
            r4 = -5
            r6 = 0
            goto L_0x0e6b
        L_0x0e48:
            r0 = move-exception
            goto L_0x0fb8
        L_0x0e4b:
            r0 = move-exception
            r9 = r78
            goto L_0x0fb8
        L_0x0e50:
            r0 = move-exception
            r9 = r78
            r11 = r81
            goto L_0x0fb8
        L_0x0e57:
            r62 = r2
            r63 = r4
            r54 = r5
            r57 = r9
            r66 = r11
            r38 = r12
            r4 = -5
            r6 = 0
            r41 = 2500(0x9c4, double:1.235E-320)
            r9 = r78
            r11 = r81
        L_0x0e6b:
            r2 = 0
            r44 = 0
            r68 = r34
            r33 = r48
            r48 = r68
        L_0x0e74:
            int r5 = (r33 > r44 ? 1 : (r33 == r44 ? 0 : -1))
            if (r5 < 0) goto L_0x0e7b
            r4 = r33
            goto L_0x0e7c
        L_0x0e7b:
            r4 = r11
        L_0x0e7c:
            int r35 = (r4 > r44 ? 1 : (r4 == r44 ? 0 : -1))
            if (r35 <= 0) goto L_0x0ebd
            int r35 = (r52 > r20 ? 1 : (r52 == r20 ? 0 : -1))
            if (r35 != 0) goto L_0x0ebd
            int r35 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r35 >= 0) goto L_0x0eac
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0eba, all -> 0x119e }
            if (r7 == 0) goto L_0x0eaa
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0eba, all -> 0x119e }
            r7.<init>()     // Catch:{ Exception -> 0x0eba, all -> 0x119e }
            java.lang.String r8 = "drop frame startTime = "
            r7.append(r8)     // Catch:{ Exception -> 0x0eba, all -> 0x119e }
            r7.append(r4)     // Catch:{ Exception -> 0x0eba, all -> 0x119e }
            java.lang.String r4 = " present time = "
            r7.append(r4)     // Catch:{ Exception -> 0x0eba, all -> 0x119e }
            long r4 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0eba, all -> 0x119e }
            r7.append(r4)     // Catch:{ Exception -> 0x0eba, all -> 0x119e }
            java.lang.String r4 = r7.toString()     // Catch:{ Exception -> 0x0eba, all -> 0x119e }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x0eba, all -> 0x119e }
        L_0x0eaa:
            r4 = 0
            goto L_0x0ebf
        L_0x0eac:
            long r4 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0eba, all -> 0x119e }
            r7 = -2147483648(0xfffffffvar_, double:NaN)
            int r35 = (r31 > r7 ? 1 : (r31 == r7 ? 0 : -1))
            if (r35 == 0) goto L_0x0eb7
            long r46 = r46 - r4
        L_0x0eb7:
            r52 = r4
            goto L_0x0ebd
        L_0x0eba:
            r0 = move-exception
            goto L_0x0var_
        L_0x0ebd:
            r4 = r54
        L_0x0ebf:
            if (r2 == 0) goto L_0x0ec4
            r52 = r20
            goto L_0x0ed7
        L_0x0ec4:
            int r2 = (r33 > r20 ? 1 : (r33 == r20 ? 0 : -1))
            if (r2 != 0) goto L_0x0ed4
            r7 = 0
            int r2 = (r46 > r7 ? 1 : (r46 == r7 ? 0 : -1))
            if (r2 == 0) goto L_0x0ed4
            long r7 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0eba, all -> 0x119e }
            long r7 = r7 + r46
            r14.presentationTimeUs = r7     // Catch:{ Exception -> 0x0eba, all -> 0x119e }
        L_0x0ed4:
            r3.releaseOutputBuffer(r1, r4)     // Catch:{ Exception -> 0x0var_, all -> 0x119e }
        L_0x0ed7:
            if (r4 == 0) goto L_0x0var_
            r1 = 0
            int r4 = (r33 > r1 ? 1 : (r33 == r1 ? 0 : -1))
            if (r4 < 0) goto L_0x0ee8
            long r4 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0eba, all -> 0x119e }
            r7 = r31
            long r7 = java.lang.Math.max(r7, r4)     // Catch:{ Exception -> 0x0eba, all -> 0x119e }
            goto L_0x0eea
        L_0x0ee8:
            r7 = r31
        L_0x0eea:
            r22.awaitNewImage()     // Catch:{ Exception -> 0x0eef, all -> 0x119e }
            r4 = 0
            goto L_0x0ef5
        L_0x0eef:
            r0 = move-exception
            r4 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ Exception -> 0x0var_, all -> 0x119e }
            r4 = 1
        L_0x0ef5:
            if (r4 != 0) goto L_0x0f0b
            r22.drawImage()     // Catch:{ Exception -> 0x0var_, all -> 0x119e }
            long r4 = r14.presentationTimeUs     // Catch:{ Exception -> 0x0var_, all -> 0x119e }
            r31 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 * r31
            r2 = r59
            r2.setPresentationTime(r4)     // Catch:{ Exception -> 0x0f2a, all -> 0x119e }
            r2.swapBuffers()     // Catch:{ Exception -> 0x0f2a, all -> 0x119e }
            goto L_0x0f0d
        L_0x0var_:
            r7 = r31
        L_0x0f0b:
            r2 = r59
        L_0x0f0d:
            int r1 = r14.flags     // Catch:{ Exception -> 0x0f2a, all -> 0x119e }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x0var_
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0f2a, all -> 0x119e }
            if (r1 == 0) goto L_0x0f1c
            java.lang.String r1 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0f2a, all -> 0x119e }
        L_0x0f1c:
            r10.signalEndOfInputStream()     // Catch:{ Exception -> 0x0f2a, all -> 0x119e }
            r4 = r48
            r1 = 0
            goto L_0x0var_
        L_0x0var_:
            r1 = r84
            r4 = r48
        L_0x0var_:
            r48 = r33
            goto L_0x0var_
        L_0x0f2a:
            r0 = move-exception
            r43 = r85
            r14 = r86
            r1 = r0
            r59 = r2
            goto L_0x0f3b
        L_0x0var_:
            r0 = move-exception
            r2 = r59
        L_0x0var_:
            r43 = r85
            r14 = r86
            r1 = r0
        L_0x0f3b:
            r68 = r33
            r34 = r48
            r48 = r68
            goto L_0x10de
        L_0x0var_:
            r9 = r78
            r11 = r81
            r2 = r59
            r6 = 0
            java.lang.RuntimeException r4 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            r5.<init>()     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            java.lang.String r7 = "unexpected result from decoder.dequeueOutputBuffer: "
            r5.append(r7)     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            r5.append(r1)     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            r4.<init>(r1)     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            throw r4     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
        L_0x0var_:
            r0 = move-exception
            r9 = r78
            r11 = r81
            goto L_0x0fb6
        L_0x0var_:
            r62 = r2
            r63 = r4
            r38 = r6
            r57 = r9
            r66 = r11
        L_0x0var_:
            r7 = r31
            r2 = r59
            r6 = 0
            r41 = 2500(0x9c4, double:1.235E-320)
            r9 = r78
            r11 = r81
            r1 = r84
            r4 = r34
        L_0x0var_:
            r59 = r2
            r61 = r13
            r2 = r29
            r6 = r38
            r9 = r60
            r13 = r79
            goto L_0x0d4e
        L_0x0f8e:
            r9 = r78
            r34 = r4
            r2 = r59
            r6 = 0
            java.lang.RuntimeException r4 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            r5.<init>()     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            java.lang.String r7 = "encoderOutputBuffer "
            r5.append(r7)     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            r5.append(r1)     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            java.lang.String r1 = " was null"
            r5.append(r1)     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            r4.<init>(r1)     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            throw r4     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
        L_0x0fb1:
            r0 = move-exception
            r9 = r78
            r34 = r4
        L_0x0fb6:
            r2 = r59
        L_0x0fb8:
            r6 = 0
            goto L_0x0ff0
        L_0x0fba:
            r9 = r78
            r34 = r4
            r2 = r59
            r6 = 0
            java.lang.RuntimeException r4 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            r5.<init>()     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            java.lang.String r7 = "unexpected result from encoder.dequeueOutputBuffer: "
            r5.append(r7)     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            r5.append(r1)     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            r4.<init>(r1)     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
            throw r4     // Catch:{ Exception -> 0x0fd8, all -> 0x119e }
        L_0x0fd8:
            r0 = move-exception
            r43 = r85
            r14 = r86
            r1 = r0
            r59 = r2
            goto L_0x10de
        L_0x0fe2:
            r0 = move-exception
            goto L_0x0fe7
        L_0x0fe4:
            r0 = move-exception
            r10 = r79
        L_0x0fe7:
            r34 = r4
            r60 = r9
            r2 = r59
            r6 = 0
            r9 = r78
        L_0x0ff0:
            r43 = r85
            r14 = r86
            r1 = r0
            goto L_0x10de
        L_0x0ff7:
            r0 = move-exception
            r10 = r79
            r64 = r4
            r85 = r9
            r9 = r13
            r86 = r14
            r2 = r59
            r6 = 0
            r43 = r85
            r1 = r0
            r34 = r64
            goto L_0x10de
        L_0x100b:
            r0 = move-exception
            r10 = r79
            r85 = r9
            r9 = r13
            r86 = r14
            r2 = r59
            r6 = 0
            r43 = r85
            r1 = r0
            r34 = r4
            r48 = r27
            goto L_0x10de
        L_0x101f:
            r0 = move-exception
            r10 = r79
            r11 = r81
            r4 = r83
            r9 = r13
            r86 = r14
            goto L_0x1036
        L_0x102a:
            r0 = move-exception
            r10 = r79
            r11 = r81
            r4 = r83
            r9 = r13
            r86 = r14
            r3 = r52
        L_0x1036:
            r2 = r59
            r6 = 0
            r1 = r0
            r34 = r4
            r48 = r27
            goto L_0x1081
        L_0x103f:
            r0 = move-exception
            r10 = r79
            r11 = r81
            r4 = r83
            r9 = r13
            r86 = r14
            r3 = r52
            r2 = r59
            r6 = 0
            r1 = r0
            goto L_0x1063
        L_0x1050:
            r0 = move-exception
            r11 = r81
            r27 = r85
            r3 = r1
            r2 = r4
            r10 = r5
            r9 = r13
            r86 = r14
            r60 = r24
            r6 = 0
            r4 = r83
            r1 = r0
            r59 = r2
        L_0x1063:
            r34 = r4
            r48 = r27
            goto L_0x107f
        L_0x1068:
            r0 = move-exception
            r11 = r81
            r27 = r85
            r2 = r4
            r10 = r5
            r9 = r13
            r86 = r14
            r60 = r24
            r6 = 0
            r4 = r83
            r1 = r0
            r59 = r2
            r34 = r4
            r48 = r27
            r3 = 0
        L_0x107f:
            r22 = 0
        L_0x1081:
            r43 = 0
            goto L_0x10de
        L_0x1085:
            r0 = move-exception
            r11 = r81
            r27 = r85
            r10 = r5
            r9 = r13
            r86 = r14
            r60 = r24
            r6 = 0
            r4 = r83
            r1 = r0
            r34 = r4
            r48 = r27
            r3 = 0
            goto L_0x10d8
        L_0x109a:
            r0 = move-exception
            r11 = r81
            r4 = r83
            r27 = r85
            r9 = r13
            r86 = r14
            goto L_0x10ad
        L_0x10a5:
            r0 = move-exception
            r11 = r81
            r4 = r83
            r27 = r85
            r9 = r13
        L_0x10ad:
            r60 = r24
            r6 = 0
            goto L_0x10c0
        L_0x10b1:
            r0 = move-exception
            r27 = r85
            r11 = r4
            goto L_0x10ba
        L_0x10b6:
            r0 = move-exception
            r11 = r4
            r27 = r7
        L_0x10ba:
            r9 = r13
            r60 = r24
            r6 = 0
            r4 = r83
        L_0x10c0:
            r1 = r0
            r34 = r4
            r48 = r27
            goto L_0x10d6
        L_0x10c6:
            r0 = move-exception
            r11 = r81
            r60 = r5
            r9 = r13
            r6 = 0
            r4 = r83
            r14 = r79
            r48 = r85
            r1 = r0
            r34 = r4
        L_0x10d6:
            r3 = 0
        L_0x10d7:
            r10 = 0
        L_0x10d8:
            r22 = 0
            r43 = 0
            r59 = 0
        L_0x10de:
            boolean r2 = r1 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x1145, all -> 0x119e }
            if (r2 == 0) goto L_0x10e6
            if (r90 != 0) goto L_0x10e6
            r2 = 1
            goto L_0x10e7
        L_0x10e6:
            r2 = 0
        L_0x10e7:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x113d, all -> 0x119e }
            r4.<init>()     // Catch:{ Exception -> 0x113d, all -> 0x119e }
            java.lang.String r5 = "bitrate: "
            r4.append(r5)     // Catch:{ Exception -> 0x113d, all -> 0x119e }
            r4.append(r14)     // Catch:{ Exception -> 0x113d, all -> 0x119e }
            java.lang.String r5 = " framerate: "
            r4.append(r5)     // Catch:{ Exception -> 0x113d, all -> 0x119e }
            r4.append(r9)     // Catch:{ Exception -> 0x113d, all -> 0x119e }
            java.lang.String r5 = " size: "
            r4.append(r5)     // Catch:{ Exception -> 0x113d, all -> 0x119e }
            r7 = r77
            r4.append(r7)     // Catch:{ Exception -> 0x1139, all -> 0x119e }
            java.lang.String r5 = "x"
            r4.append(r5)     // Catch:{ Exception -> 0x1139, all -> 0x119e }
            r8 = r76
            r4.append(r8)     // Catch:{ Exception -> 0x1137, all -> 0x119e }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x1137, all -> 0x119e }
            org.telegram.messenger.FileLog.e((java.lang.String) r4)     // Catch:{ Exception -> 0x1137, all -> 0x119e }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x1137, all -> 0x119e }
            r38 = r2
            r1 = r3
            r6 = r10
            r2 = 1
        L_0x111f:
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x116b, all -> 0x119e }
            r4 = r60
            r3.unselectTrack(r4)     // Catch:{ Exception -> 0x116b, all -> 0x119e }
            if (r1 == 0) goto L_0x112e
            r1.stop()     // Catch:{ Exception -> 0x116b, all -> 0x119e }
            r1.release()     // Catch:{ Exception -> 0x116b, all -> 0x119e }
        L_0x112e:
            r1 = r2
            r2 = r6
            r6 = r22
            r3 = r43
            r43 = r59
            goto L_0x1165
        L_0x1137:
            r0 = move-exception
            goto L_0x1142
        L_0x1139:
            r0 = move-exception
            r8 = r76
            goto L_0x1142
        L_0x113d:
            r0 = move-exception
            r8 = r76
            r7 = r77
        L_0x1142:
            r1 = r2
            goto L_0x056d
        L_0x1145:
            r0 = move-exception
            r8 = r76
            r7 = r77
            r2 = r0
            goto L_0x11b3
        L_0x114d:
            r8 = r76
            r7 = r77
            r11 = r81
            r4 = r83
            r9 = r13
            r6 = 0
            r14 = r79
            r48 = r85
            r34 = r4
            r1 = 0
            r2 = 0
            r3 = 0
            r6 = 0
            r38 = 0
            r43 = 0
        L_0x1165:
            if (r6 == 0) goto L_0x1170
            r6.release()     // Catch:{ Exception -> 0x116b, all -> 0x119e }
            goto L_0x1170
        L_0x116b:
            r0 = move-exception
            r2 = r0
            r1 = r38
            goto L_0x11b4
        L_0x1170:
            if (r43 == 0) goto L_0x1175
            r43.release()     // Catch:{ Exception -> 0x116b, all -> 0x119e }
        L_0x1175:
            if (r2 == 0) goto L_0x117d
            r2.stop()     // Catch:{ Exception -> 0x116b, all -> 0x119e }
            r2.release()     // Catch:{ Exception -> 0x116b, all -> 0x119e }
        L_0x117d:
            if (r3 == 0) goto L_0x1182
            r3.release()     // Catch:{ Exception -> 0x116b, all -> 0x119e }
        L_0x1182:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x116b, all -> 0x119e }
            r2 = r1
            r1 = r38
        L_0x1188:
            android.media.MediaExtractor r3 = r15.extractor
            if (r3 == 0) goto L_0x118f
            r3.release()
        L_0x118f:
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer
            if (r3 == 0) goto L_0x119c
            r3.finishMovie()     // Catch:{ Exception -> 0x1197 }
            goto L_0x119c
        L_0x1197:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x119c:
            r6 = r8
            goto L_0x11f9
        L_0x119e:
            r0 = move-exception
            r2 = r0
            r1 = r15
            goto L_0x122a
        L_0x11a3:
            r0 = move-exception
            r4 = r83
            r9 = r10
            r7 = r11
            r8 = r12
            r6 = 0
            r11 = r81
            r14 = r79
            r48 = r85
            r2 = r0
            r34 = r4
        L_0x11b3:
            r1 = 0
        L_0x11b4:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x1226 }
            r3.<init>()     // Catch:{ all -> 0x1226 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x1226 }
            r3.append(r14)     // Catch:{ all -> 0x1226 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x1226 }
            r3.append(r9)     // Catch:{ all -> 0x1226 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x1226 }
            r3.append(r7)     // Catch:{ all -> 0x1226 }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x1226 }
            r3.append(r8)     // Catch:{ all -> 0x1226 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x1226 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x1226 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x1226 }
            android.media.MediaExtractor r2 = r15.extractor
            if (r2 == 0) goto L_0x11ea
            r2.release()
        L_0x11ea:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer
            if (r2 == 0) goto L_0x11f7
            r2.finishMovie()     // Catch:{ Exception -> 0x11f2 }
            goto L_0x11f7
        L_0x11f2:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x11f7:
            r6 = r8
            r2 = 1
        L_0x11f9:
            if (r1 == 0) goto L_0x1225
            r20 = 1
            r1 = r71
            r2 = r72
            r3 = r73
            r4 = r74
            r5 = r75
            r8 = r78
            r9 = r14
            r10 = r80
            r11 = r81
            r13 = r34
            r15 = r48
            r17 = r87
            r19 = r89
            r21 = r91
            r22 = r92
            r23 = r93
            r24 = r94
            r25 = r95
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r13, r15, r17, r19, r20, r21, r22, r23, r24, r25)
            return r1
        L_0x1225:
            return r2
        L_0x1226:
            r0 = move-exception
            r1 = r71
            r2 = r0
        L_0x122a:
            android.media.MediaExtractor r3 = r1.extractor
            if (r3 == 0) goto L_0x1231
            r3.release()
        L_0x1231:
            org.telegram.messenger.video.MP4Builder r3 = r1.mediaMuxer
            if (r3 == 0) goto L_0x123e
            r3.finishMovie()     // Catch:{ Exception -> 0x1239 }
            goto L_0x123e
        L_0x1239:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x123e:
            goto L_0x1240
        L_0x123f:
            throw r2
        L_0x1240:
            goto L_0x123f
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
