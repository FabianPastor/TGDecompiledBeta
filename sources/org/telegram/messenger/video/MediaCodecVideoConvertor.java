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

    public boolean convertVideo(String str, File file, int i, boolean z, int i2, int i3, int i4, int i5, int i6, long j, long j2, long j3, boolean z2, long j4, MediaController.SavedFilterState savedFilterState, String str2, ArrayList<VideoEditedInfo.MediaEntity> arrayList, boolean z3, MediaController.CropState cropState, MediaController.VideoConvertorListener videoConvertorListener) {
        String str3 = str;
        long j5 = j4;
        this.callback = videoConvertorListener;
        return convertVideoInternal(str, file, i, z, i2, i3, i4, i5, i6, j, j2, j3, j5, z2, false, savedFilterState, str2, arrayList, z3, cropState);
    }

    public long getLastFrameTimestamp() {
        return this.endPresentationTime;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v63, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v63, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v64, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v68, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v72, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v73, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v76, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v97, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v138, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r25v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v92, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v95, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v96, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v97, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v98, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v99, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v100, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v101, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v102, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v103, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v104, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v105, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v106, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v107, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v108, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v109, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v110, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v111, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v112, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v113, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v114, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v115, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v80, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v116, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v117, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v118, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v119, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v120, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v121, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v122, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v228, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v229, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v230, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v231, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v232, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v234, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v235, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v236, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v237, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v239, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v178, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v252, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v254, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v256, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v258, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v259, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v260, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v179, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v79, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r48v80, resolved type: int} */
    /* JADX WARNING: type inference failed for: r4v71 */
    /* JADX WARNING: type inference failed for: r4v80 */
    /* JADX WARNING: type inference failed for: r4v90 */
    /* JADX WARNING: Code restructure failed: missing block: B:1023:0x118d, code lost:
        r0 = th;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1028:0x119a, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1029:0x119b, code lost:
        r11 = r79;
        r50 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1052:0x1203, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1053:0x1204, code lost:
        r11 = r79;
        r8 = r80;
        r4 = r84;
        r2 = r58;
        r3 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1056:0x1214, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1057:0x1215, code lost:
        r11 = r79;
        r4 = r84;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1058:0x121b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1059:0x121c, code lost:
        r11 = r79;
        r8 = r80;
        r4 = r84;
        r2 = r58;
        r3 = r60;
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1060:0x1228, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1061:0x1229, code lost:
        r3 = r2;
        r31 = r7;
        r11 = r14;
        r61 = r24;
        r2 = r1;
        r8 = r4;
        r1 = false;
        r4 = r84;
        r58 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1062:0x1236, code lost:
        r50 = r4;
        r6 = r8;
        r2 = -5;
        r27 = null;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1068:0x125e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1069:0x125f, code lost:
        r31 = r7;
        r11 = r14;
        r61 = r24;
        r1 = false;
        r50 = r84;
        r6 = r4;
        r3 = r20;
        r2 = -5;
        r4 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1070:0x1271, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1071:0x1272, code lost:
        r4 = r84;
        r31 = r7;
        r11 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1075:0x1283, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1076:0x1284, code lost:
        r31 = r7;
        r11 = r14;
        r61 = r24;
        r1 = false;
        r50 = r84;
        r3 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1158:0x1403, code lost:
        r1.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1162:?, code lost:
        r1.finishMovie();
        r15.endPresentationTime = r15.mediaMuxer.getLastFrameTimestamp(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1163:0x1416, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1164:0x1417, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x03a6, code lost:
        r2 = java.nio.ByteBuffer.allocate(r3);
        r7 = java.nio.ByteBuffer.allocate(r14.size - r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x03b1, code lost:
        r20 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:?, code lost:
        r2.put(r9, 0, r3).position(0);
        r7.put(r9, r3, r14.size - r3).position(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x04f4, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x04f5, code lost:
        r20 = r3;
        r10 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0536, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:277:0x0538, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0539, code lost:
        r6 = r1;
        r36 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x053d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x053f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x0540, code lost:
        r20 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x054c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x054d, code lost:
        r1 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x054f, code lost:
        r20 = r3;
        r3 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x0583, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x0584, code lost:
        r3 = r36;
        r6 = r77;
        r11 = r49;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x058d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x058e, code lost:
        r11 = r79;
        r50 = r84;
        r31 = r86;
        r1 = r0;
        r9 = r48;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x05ae, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x05af, code lost:
        r1 = r6;
        r48 = r9;
        r49 = r11;
        r2 = -5;
        r16 = null;
        r36 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x05bb, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x05bc, code lost:
        r48 = r9;
        r49 = r11;
        r11 = r79;
        r50 = r84;
        r31 = r86;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x05c7, code lost:
        r8 = r49;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:311:0x05ca, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x05cb, code lost:
        r48 = r9;
        r49 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:426:0x07c2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x07c3, code lost:
        r12 = r77;
        r50 = r84;
        r1 = r0;
        r9 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x07f5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x07f6, code lost:
        r50 = r84;
        r5 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x0926, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x098c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r5 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x09ed, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x0a1f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x0a21, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0a9c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0a9d, code lost:
        r12 = r77;
        r8 = r78;
        r11 = r79;
        r1 = r0;
        r50 = r4;
        r2 = r18;
        r9 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x0aac, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0aad, code lost:
        r11 = r79;
        r6 = r80;
        r50 = r4;
        r40 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x01ae, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x01af, code lost:
        r6 = r77;
        r1 = r0;
        r11 = r49;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x0b14, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x0b15, code lost:
        r11 = r79;
        r40 = r86;
        r50 = r4;
        r61 = r6;
        r2 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x0b30, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x0b31, code lost:
        r11 = r79;
        r40 = r86;
        r50 = r4;
        r61 = r6;
        r2 = r18;
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x0b3c, code lost:
        r6 = r80;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0b6a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:685:0x0bbf, code lost:
        if (r13.presentationTimeUs < r4) goto L_0x0bc4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0c0a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0c0b, code lost:
        r48 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0c1d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0c1e, code lost:
        r48 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0c5b, code lost:
        r0 = th;
        r48 = r48;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:727:0x0c5d, code lost:
        r0 = e;
        r48 = r48;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0CLASSNAME, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0CLASSNAME, code lost:
        r48 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x0CLASSNAME, code lost:
        r12 = r77;
        r8 = r78;
        r11 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0c6c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0c6d, code lost:
        r48 = r4;
        r86 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x01d6, code lost:
        r1 = r2;
        r2 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r6;
        r6 = r7;
        r7 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:946:0x102a, code lost:
        r0 = th;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x102d, code lost:
        r0 = e;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:989:0x10b9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x10bb, code lost:
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:992:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r4 = true;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:423:0x07b3, B:434:0x07e8] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1002:0x10dd A[Catch:{ Exception -> 0x118f, all -> 0x118d }] */
    /* JADX WARNING: Removed duplicated region for block: B:1006:0x10ed A[Catch:{ Exception -> 0x118f, all -> 0x118d }] */
    /* JADX WARNING: Removed duplicated region for block: B:1023:0x118d A[ExcHandler: all (th java.lang.Throwable), PHI: r11 r12 r31 r50 
      PHI: (r11v64 int) = (r11v228 int), (r11v230 int), (r11v231 int), (r11v232 int), (r11v234 int), (r11v235 int), (r11v237 int), (r11v239 int), (r11v252 int), (r11v254 int), (r11v256 int), (r11v258 int), (r11v260 java.lang.String) binds: [B:986:0x10b4, B:996:0x10cc, B:991:0x10bb, B:987:?, B:983:0x10a9, B:978:0x109e, B:979:?, B:961:0x1056, B:935:0x0fec, B:936:?, B:929:0x0fdf, B:930:?, B:872:0x0efe] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r12v26 int) = (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v34 int) binds: [B:986:0x10b4, B:996:0x10cc, B:991:0x10bb, B:987:?, B:983:0x10a9, B:978:0x109e, B:979:?, B:961:0x1056, B:935:0x0fec, B:936:?, B:929:0x0fdf, B:930:?, B:872:0x0efe] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r31v42 long) = (r31v45 long), (r31v45 long), (r31v45 long), (r31v45 long), (r31v45 long), (r31v45 long), (r31v45 long), (r31v45 long), (r31v35 long), (r31v35 long), (r31v35 long), (r31v35 long), (r31v35 long) binds: [B:986:0x10b4, B:996:0x10cc, B:991:0x10bb, B:987:?, B:983:0x10a9, B:978:0x109e, B:979:?, B:961:0x1056, B:935:0x0fec, B:936:?, B:929:0x0fdf, B:930:?, B:872:0x0efe] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r50v38 long) = (r50v46 long), (r50v46 long), (r50v46 long), (r50v46 long), (r50v46 long), (r50v46 long), (r50v46 long), (r50v46 long), (r50v44 long), (r50v44 long), (r50v44 long), (r50v44 long), (r50v53 long) binds: [B:986:0x10b4, B:996:0x10cc, B:991:0x10bb, B:987:?, B:983:0x10a9, B:978:0x109e, B:979:?, B:961:0x1056, B:935:0x0fec, B:936:?, B:929:0x0fdf, B:930:?, B:872:0x0efe] A[DONT_GENERATE, DONT_INLINE], Splitter:B:986:0x10b4] */
    /* JADX WARNING: Removed duplicated region for block: B:1028:0x119a A[ExcHandler: all (th java.lang.Throwable), Splitter:B:746:0x0ce1] */
    /* JADX WARNING: Removed duplicated region for block: B:1056:0x1214 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:493:0x08fd] */
    /* JADX WARNING: Removed duplicated region for block: B:1070:0x1271 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:483:0x089b] */
    /* JADX WARNING: Removed duplicated region for block: B:1112:0x132c A[Catch:{ all -> 0x133d }] */
    /* JADX WARNING: Removed duplicated region for block: B:1124:0x135b  */
    /* JADX WARNING: Removed duplicated region for block: B:1126:0x1373 A[SYNTHETIC, Splitter:B:1126:0x1373] */
    /* JADX WARNING: Removed duplicated region for block: B:1132:0x137f A[Catch:{ all -> 0x1377 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1134:0x1384 A[Catch:{ all -> 0x1377 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1136:0x138c A[Catch:{ all -> 0x1377 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1141:0x1398  */
    /* JADX WARNING: Removed duplicated region for block: B:1144:0x139f A[SYNTHETIC, Splitter:B:1144:0x139f] */
    /* JADX WARNING: Removed duplicated region for block: B:1158:0x1403  */
    /* JADX WARNING: Removed duplicated region for block: B:1161:0x140a A[SYNTHETIC, Splitter:B:1161:0x140a] */
    /* JADX WARNING: Removed duplicated region for block: B:1167:0x1423  */
    /* JADX WARNING: Removed duplicated region for block: B:1219:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x043a A[Catch:{ Exception -> 0x04ed, all -> 0x04e8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x043c A[Catch:{ Exception -> 0x04ed, all -> 0x04e8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x0536 A[ExcHandler: all (th java.lang.Throwable), PHI: r20 
      PHI: (r20v11 int) = (r20v10 int), (r20v29 int), (r20v29 int), (r20v36 int), (r20v36 int) binds: [B:271:0x04fe, B:202:0x03b4, B:203:?, B:172:0x034b, B:173:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:172:0x034b] */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x053f A[ExcHandler: all (th java.lang.Throwable), Splitter:B:74:0x01de] */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x058d A[ExcHandler: all (r0v142 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:54:0x01a2] */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x05bb A[ExcHandler: all (r0v136 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:40:0x010f] */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x05f0 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x0631 A[SYNTHETIC, Splitter:B:337:0x0631] */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x063f A[Catch:{ all -> 0x0635 }] */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x0644 A[Catch:{ all -> 0x0635 }] */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x072f  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x07c2 A[ExcHandler: all (r0v119 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:423:0x07b3] */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x080f  */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0826  */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x0830 A[SYNTHETIC, Splitter:B:460:0x0830] */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x086e A[SYNTHETIC, Splitter:B:466:0x086e] */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x0897  */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x0916 A[SYNTHETIC, Splitter:B:502:0x0916] */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x0926 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:502:0x0916] */
    /* JADX WARNING: Removed duplicated region for block: B:509:0x0940  */
    /* JADX WARNING: Removed duplicated region for block: B:511:0x0946 A[SYNTHETIC, Splitter:B:511:0x0946] */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0974  */
    /* JADX WARNING: Removed duplicated region for block: B:524:0x0977  */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x0a1f A[ExcHandler: all (th java.lang.Throwable), Splitter:B:549:0x09d9] */
    /* JADX WARNING: Removed duplicated region for block: B:574:0x0a23  */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x0a45  */
    /* JADX WARNING: Removed duplicated region for block: B:586:0x0a51  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a54  */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x0a74 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:602:0x0a9c A[ExcHandler: all (r0v93 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:599:0x0a93] */
    /* JADX WARNING: Removed duplicated region for block: B:609:0x0abc A[SYNTHETIC, Splitter:B:609:0x0abc] */
    /* JADX WARNING: Removed duplicated region for block: B:719:0x0c3f A[Catch:{ Exception -> 0x0c5d, all -> 0x0c5b }] */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0c4c A[Catch:{ Exception -> 0x0c5d, all -> 0x0c5b }] */
    /* JADX WARNING: Removed duplicated region for block: B:726:0x0c5b A[ExcHandler: all (th java.lang.Throwable), PHI: r48 
      PHI: (r48v28 long) = (r48v55 long), (r48v66 long), (r48v68 long), (r48v71 long), (r48v76 long) binds: [B:643:0x0b4d, B:660:0x0b7e, B:673:0x0bac, B:691:0x0bd3, B:695:0x0bdd] A[DONT_GENERATE, DONT_INLINE], Splitter:B:691:0x0bd3] */
    /* JADX WARNING: Removed duplicated region for block: B:728:0x0c5f  */
    /* JADX WARNING: Removed duplicated region for block: B:729:0x0CLASSNAME A[ExcHandler: all (th java.lang.Throwable), Splitter:B:609:0x0abc] */
    /* JADX WARNING: Removed duplicated region for block: B:736:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:739:0x0ca9 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:744:0x0ccc  */
    /* JADX WARNING: Removed duplicated region for block: B:745:0x0cd9  */
    /* JADX WARNING: Removed duplicated region for block: B:750:0x0ce8  */
    /* JADX WARNING: Removed duplicated region for block: B:752:0x0d00  */
    /* JADX WARNING: Removed duplicated region for block: B:875:0x0var_ A[Catch:{ Exception -> 0x1119, all -> 0x1114 }] */
    /* JADX WARNING: Removed duplicated region for block: B:876:0x0var_ A[Catch:{ Exception -> 0x1119, all -> 0x1114 }] */
    /* JADX WARNING: Removed duplicated region for block: B:879:0x0var_ A[Catch:{ Exception -> 0x1119, all -> 0x1114 }] */
    /* JADX WARNING: Removed duplicated region for block: B:881:0x0var_ A[Catch:{ Exception -> 0x1119, all -> 0x1114 }] */
    /* JADX WARNING: Removed duplicated region for block: B:947:0x102d A[ExcHandler: Exception (e java.lang.Exception), PHI: r11 r31 r50 
      PHI: (r11v77 int) = (r11v233 int), (r11v238 int), (r11v242 int), (r11v244 int), (r11v249 int), (r11v250 int), (r11v251 int), (r11v253 int), (r11v255 int), (r11v257 int) binds: [B:983:0x10a9, B:961:0x1056, B:923:0x0fd1, B:924:?, B:932:0x0fe7, B:933:?, B:935:0x0fec, B:936:?, B:929:0x0fdf, B:930:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r31v46 long) = (r31v45 long), (r31v45 long), (r31v35 long), (r31v35 long), (r31v35 long), (r31v35 long), (r31v35 long), (r31v35 long), (r31v35 long), (r31v35 long) binds: [B:983:0x10a9, B:961:0x1056, B:923:0x0fd1, B:924:?, B:932:0x0fe7, B:933:?, B:935:0x0fec, B:936:?, B:929:0x0fdf, B:930:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r50v47 long) = (r50v46 long), (r50v46 long), (r50v44 long), (r50v44 long), (r50v44 long), (r50v44 long), (r50v44 long), (r50v44 long), (r50v44 long), (r50v44 long) binds: [B:983:0x10a9, B:961:0x1056, B:923:0x0fd1, B:924:?, B:932:0x0fe7, B:933:?, B:935:0x0fec, B:936:?, B:929:0x0fdf, B:930:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:923:0x0fd1] */
    /* JADX WARNING: Removed duplicated region for block: B:953:0x1045  */
    /* JADX WARNING: Removed duplicated region for block: B:954:0x1048  */
    /* JADX WARNING: Removed duplicated region for block: B:961:0x1056 A[SYNTHETIC, Splitter:B:961:0x1056] */
    /* JADX WARNING: Removed duplicated region for block: B:966:0x107a A[Catch:{ Exception -> 0x102d, all -> 0x118d }] */
    /* JADX WARNING: Removed duplicated region for block: B:972:0x108b A[Catch:{ Exception -> 0x102d, all -> 0x118d }] */
    /* JADX WARNING: Removed duplicated region for block: B:973:0x108e A[Catch:{ Exception -> 0x102d, all -> 0x118d }] */
    /* JADX WARNING: Removed duplicated region for block: B:981:0x10a3  */
    /* JADX WARNING: Removed duplicated region for block: B:998:0x10d3 A[Catch:{ Exception -> 0x118f, all -> 0x118d }] */
    /* JADX WARNING: Unknown variable types count: 1 */
    @android.annotation.TargetApi(18)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideoInternal(java.lang.String r73, java.io.File r74, int r75, boolean r76, int r77, int r78, int r79, int r80, int r81, long r82, long r84, long r86, long r88, boolean r90, boolean r91, org.telegram.messenger.MediaController.SavedFilterState r92, java.lang.String r93, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r94, boolean r95, org.telegram.messenger.MediaController.CropState r96) {
        /*
            r72 = this;
            r15 = r72
            r13 = r73
            r14 = r75
            r12 = r77
            r11 = r78
            r10 = r79
            r9 = r80
            r8 = r81
            r6 = r82
            r4 = r88
            r3 = r96
            r16 = -1
            r6 = 0
            android.media.MediaCodec$BufferInfo r7 = new android.media.MediaCodec$BufferInfo     // Catch:{ all -> 0x13bf }
            r7.<init>()     // Catch:{ all -> 0x13bf }
            org.telegram.messenger.video.Mp4Movie r2 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ all -> 0x13bf }
            r2.<init>()     // Catch:{ all -> 0x13bf }
            r1 = r74
            r2.setCacheFile(r1)     // Catch:{ all -> 0x13bf }
            r2.setRotation(r6)     // Catch:{ all -> 0x13bf }
            r2.setSize(r12, r11)     // Catch:{ all -> 0x13bf }
            org.telegram.messenger.video.MP4Builder r6 = new org.telegram.messenger.video.MP4Builder     // Catch:{ all -> 0x13bf }
            r6.<init>()     // Catch:{ all -> 0x13bf }
            r14 = r76
            org.telegram.messenger.video.MP4Builder r2 = r6.createMovie(r2, r14)     // Catch:{ all -> 0x13bf }
            r15.mediaMuxer = r2     // Catch:{ all -> 0x13bf }
            float r2 = (float) r4     // Catch:{ all -> 0x13bf }
            r21 = 1148846080(0x447a0000, float:1000.0)
            float r22 = r2 / r21
            r23 = 1000(0x3e8, double:4.94E-321)
            long r1 = r4 * r23
            r15.endPresentationTime = r1     // Catch:{ all -> 0x13bf }
            r72.checkConversionCanceled()     // Catch:{ all -> 0x13bf }
            java.lang.String r6 = "csd-1"
            java.lang.String r1 = "csd-0"
            r23 = r6
            java.lang.String r6 = "prepend-sps-pps-to-idr-frames"
            r2 = 921600(0xe1000, float:1.291437E-39)
            r26 = r6
            r25 = r7
            java.lang.String r7 = "video/avc"
            r31 = r7
            r6 = 0
            if (r95 == 0) goto L_0x0681
            int r16 = (r86 > r6 ? 1 : (r86 == r6 ? 0 : -1))
            if (r16 < 0) goto L_0x0087
            r2 = 1157234688(0x44fa0000, float:2000.0)
            int r2 = (r22 > r2 ? 1 : (r22 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0072
            r2 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            r9 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x008c
        L_0x0072:
            r2 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r2 = (r22 > r2 ? 1 : (r22 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0080
            r2 = 2200000(0x2191c0, float:3.082857E-39)
            r9 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x008c
        L_0x0080:
            r2 = 1560000(0x17cdc0, float:2.186026E-39)
            r9 = 1560000(0x17cdc0, float:2.186026E-39)
            goto L_0x008c
        L_0x0087:
            if (r9 > 0) goto L_0x008c
            r9 = 921600(0xe1000, float:1.291437E-39)
        L_0x008c:
            int r2 = r12 % 16
            r16 = 1098907648(0x41800000, float:16.0)
            if (r2 == 0) goto L_0x00d8
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            if (r2 == 0) goto L_0x00bb
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            r2.<init>()     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            java.lang.String r6 = "changing width from "
            r2.append(r6)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            r2.append(r12)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            float r6 = (float) r12     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            float r6 = r6 / r16
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
        L_0x00bb:
            float r2 = (float) r12     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            float r2 = r2 / r16
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            int r2 = r2 * 16
            r12 = r2
            goto L_0x00d8
        L_0x00c6:
            r0 = move-exception
            r50 = r84
            r31 = r86
            r1 = r0
            r8 = r11
            r2 = -5
            r6 = 0
        L_0x00cf:
            r11 = r10
            goto L_0x13cf
        L_0x00d2:
            r0 = move-exception
            r1 = r0
            r48 = r9
            goto L_0x05e6
        L_0x00d8:
            int r2 = r11 % 16
            if (r2 == 0) goto L_0x010f
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            if (r2 == 0) goto L_0x0105
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            r2.<init>()     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            java.lang.String r6 = "changing height from "
            r2.append(r6)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            r2.append(r11)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            float r6 = (float) r11     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            float r6 = r6 / r16
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
        L_0x0105:
            float r2 = (float) r11     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            float r2 = r2 / r16
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            int r2 = r2 * 16
            r11 = r2
        L_0x010f:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05ca, all -> 0x05bb }
            if (r2 == 0) goto L_0x0137
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            r2.<init>()     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            java.lang.String r6 = "create photo encoder "
            r2.append(r6)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            r2.append(r12)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            java.lang.String r6 = " "
            r2.append(r6)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            r2.append(r11)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            java.lang.String r6 = " duration = "
            r2.append(r6)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            r2.append(r4)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d2, all -> 0x00c6 }
        L_0x0137:
            r7 = r31
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r7, r12, r11)     // Catch:{ Exception -> 0x05ca, all -> 0x05bb }
            java.lang.String r6 = "color-format"
            r31 = r1
            r1 = 2130708361(0x7var_, float:1.701803E38)
            r2.setInteger(r6, r1)     // Catch:{ Exception -> 0x05ca, all -> 0x05bb }
            java.lang.String r1 = "bitrate"
            r2.setInteger(r1, r9)     // Catch:{ Exception -> 0x05ca, all -> 0x05bb }
            java.lang.String r1 = "frame-rate"
            r2.setInteger(r1, r10)     // Catch:{ Exception -> 0x05ca, all -> 0x05bb }
            java.lang.String r1 = "i-frame-interval"
            r6 = 2
            r2.setInteger(r1, r6)     // Catch:{ Exception -> 0x05ca, all -> 0x05bb }
            android.media.MediaCodec r6 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x05ca, all -> 0x05bb }
            r1 = 0
            r3 = 1
            r6.configure(r2, r1, r1, r3)     // Catch:{ Exception -> 0x05ae, all -> 0x05bb }
            org.telegram.messenger.video.InputSurface r2 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x05ae, all -> 0x05bb }
            android.view.Surface r1 = r6.createInputSurface()     // Catch:{ Exception -> 0x05ae, all -> 0x05bb }
            r2.<init>(r1)     // Catch:{ Exception -> 0x05ae, all -> 0x05bb }
            r2.makeCurrent()     // Catch:{ Exception -> 0x05a1, all -> 0x05bb }
            r6.start()     // Catch:{ Exception -> 0x05a1, all -> 0x05bb }
            org.telegram.messenger.video.OutputSurface r16 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x05a1, all -> 0x05bb }
            r17 = 0
            float r1 = (float) r10
            r19 = 1
            r30 = r1
            r35 = r31
            r31 = 0
            r1 = r16
            r36 = r2
            r2 = r92
            r3 = r73
            r4 = r93
            r5 = r94
            r77 = r6
            r44 = r23
            r45 = r26
            r14 = 21
            r6 = r17
            r47 = r7
            r46 = r25
            r7 = r12
            r8 = r11
            r48 = r9
            r9 = r75
            r10 = r30
            r49 = r11
            r11 = r19
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0598, all -> 0x058d }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0583, all -> 0x058d }
            if (r1 >= r14) goto L_0x01b7
            java.nio.ByteBuffer[] r6 = r77.getOutputBuffers()     // Catch:{ Exception -> 0x01ae, all -> 0x058d }
            goto L_0x01b8
        L_0x01ae:
            r0 = move-exception
            r6 = r77
            r1 = r0
            r11 = r49
            r2 = -5
            goto L_0x05ec
        L_0x01b7:
            r6 = 0
        L_0x01b8:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x0583, all -> 0x058d }
            r4 = r6
            r1 = 1
            r2 = -5
            r3 = 0
            r5 = 0
            r6 = 0
            r7 = 0
        L_0x01c2:
            if (r6 != 0) goto L_0x056e
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x0565, all -> 0x0557 }
            r8 = r3 ^ 1
            r9 = r7
            r7 = r6
            r6 = r5
            r5 = r4
            r4 = r3
            r3 = r2
            r2 = r1
            r1 = 1
        L_0x01d1:
            if (r8 != 0) goto L_0x01de
            if (r1 == 0) goto L_0x01d6
            goto L_0x01de
        L_0x01d6:
            r1 = r2
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r9
            goto L_0x01c2
        L_0x01de:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x054c, all -> 0x053f }
            if (r91 == 0) goto L_0x01e8
            r10 = 22000(0x55f0, double:1.08694E-319)
            r14 = r46
            goto L_0x01ec
        L_0x01e8:
            r14 = r46
            r10 = 2500(0x9c4, double:1.235E-320)
        L_0x01ec:
            r69 = r1
            r1 = r77
            r77 = r69
            int r10 = r1.dequeueOutputBuffer(r14, r10)     // Catch:{ Exception -> 0x053d, all -> 0x053f }
            r11 = -1
            if (r10 != r11) goto L_0x0210
            r18 = r6
            r80 = r8
            r17 = r9
            r8 = r35
            r11 = r44
            r13 = r47
            r77 = 0
            r6 = r2
            r9 = r7
            r2 = -1
            r7 = r5
            r5 = r3
            r3 = r49
            goto L_0x0447
        L_0x0210:
            r11 = -3
            if (r10 != r11) goto L_0x0245
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            r78 = r7
            r7 = 21
            if (r11 >= r7) goto L_0x021f
            java.nio.ByteBuffer[] r5 = r1.getOutputBuffers()     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
        L_0x021f:
            r7 = r5
            r18 = r6
            r80 = r8
            r17 = r9
            r8 = r35
            r11 = r44
        L_0x022a:
            r13 = r47
            r9 = r78
            r6 = r2
            r5 = r3
            r3 = r49
        L_0x0232:
            r2 = -1
            goto L_0x0447
        L_0x0235:
            r0 = move-exception
            r11 = r79
            r50 = r84
            r31 = r86
            r1 = r0
            r2 = r3
            goto L_0x055f
        L_0x0240:
            r0 = move-exception
            r6 = r1
            r2 = r3
            goto L_0x056b
        L_0x0245:
            r78 = r7
            r7 = -2
            if (r10 != r7) goto L_0x02aa
            android.media.MediaFormat r7 = r1.getOutputFormat()     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            if (r11 == 0) goto L_0x0269
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            r11.<init>()     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            r80 = r8
            java.lang.String r8 = "photo encoder new format "
            r11.append(r8)     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            r11.append(r7)     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            java.lang.String r8 = r11.toString()     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            org.telegram.messenger.FileLog.d(r8)     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            goto L_0x026b
        L_0x0269:
            r80 = r8
        L_0x026b:
            r8 = -5
            if (r3 != r8) goto L_0x02a0
            if (r7 == 0) goto L_0x02a0
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            r8 = 0
            int r3 = r11.addTrack(r7, r8)     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            r11 = r45
            boolean r17 = r7.containsKey(r11)     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            if (r17 == 0) goto L_0x029e
            int r8 = r7.getInteger(r11)     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            r45 = r11
            r11 = 1
            if (r8 != r11) goto L_0x02a0
            r8 = r35
            java.nio.ByteBuffer r6 = r7.getByteBuffer(r8)     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            r11 = r44
            java.nio.ByteBuffer r7 = r7.getByteBuffer(r11)     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            int r6 = r6.limit()     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            int r7 = r7.limit()     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            int r6 = r6 + r7
            goto L_0x02a4
        L_0x029e:
            r45 = r11
        L_0x02a0:
            r8 = r35
            r11 = r44
        L_0x02a4:
            r7 = r5
            r18 = r6
            r17 = r9
            goto L_0x022a
        L_0x02aa:
            r80 = r8
            r8 = r35
            r11 = r44
            if (r10 < 0) goto L_0x051a
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x053d, all -> 0x053f }
            r13 = 21
            if (r7 >= r13) goto L_0x02bb
            r7 = r5[r10]     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            goto L_0x02bf
        L_0x02bb:
            java.nio.ByteBuffer r7 = r1.getOutputBuffer(r10)     // Catch:{ Exception -> 0x053d, all -> 0x053f }
        L_0x02bf:
            if (r7 == 0) goto L_0x04fa
            int r13 = r14.size     // Catch:{ Exception -> 0x04f4, all -> 0x053f }
            r78 = r5
            r5 = 1
            if (r13 <= r5) goto L_0x0427
            int r5 = r14.flags     // Catch:{ Exception -> 0x041c, all -> 0x0409 }
            r17 = r5 & 2
            if (r17 != 0) goto L_0x036c
            if (r6 == 0) goto L_0x02df
            r17 = r5 & 1
            if (r17 == 0) goto L_0x02df
            r17 = r9
            int r9 = r14.offset     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            int r9 = r9 + r6
            r14.offset = r9     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            int r13 = r13 - r6
            r14.size = r13     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            goto L_0x02e1
        L_0x02df:
            r17 = r9
        L_0x02e1:
            if (r2 == 0) goto L_0x032f
            r5 = r5 & 1
            if (r5 == 0) goto L_0x032f
            int r2 = r14.size     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            r5 = 100
            if (r2 <= r5) goto L_0x032e
            int r2 = r14.offset     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            r7.position(r2)     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            byte[] r2 = new byte[r5]     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            r7.get(r2)     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            r9 = 0
            r13 = 0
        L_0x02f9:
            r5 = 96
            if (r9 >= r5) goto L_0x032e
            byte r5 = r2[r9]     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            if (r5 != 0) goto L_0x0325
            int r5 = r9 + 1
            byte r5 = r2[r5]     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            if (r5 != 0) goto L_0x0325
            int r5 = r9 + 2
            byte r5 = r2[r5]     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            if (r5 != 0) goto L_0x0325
            int r5 = r9 + 3
            byte r5 = r2[r5]     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            r18 = r2
            r2 = 1
            if (r5 != r2) goto L_0x0327
            int r13 = r13 + 1
            if (r13 <= r2) goto L_0x0327
            int r2 = r14.offset     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            int r2 = r2 + r9
            r14.offset = r2     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            int r2 = r14.size     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            int r2 = r2 - r9
            r14.size = r2     // Catch:{ Exception -> 0x0240, all -> 0x0235 }
            goto L_0x032e
        L_0x0325:
            r18 = r2
        L_0x0327:
            int r9 = r9 + 1
            r2 = r18
            r5 = 100
            goto L_0x02f9
        L_0x032e:
            r2 = 0
        L_0x032f:
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x0367, all -> 0x053f }
            r18 = r6
            r9 = 1
            long r5 = r5.writeSampleData(r3, r7, r14, r9)     // Catch:{ Exception -> 0x0367, all -> 0x053f }
            r13 = r2
            r9 = r3
            r2 = 0
            int r7 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r7 == 0) goto L_0x035c
            org.telegram.messenger.MediaController$VideoConvertorListener r7 = r15.callback     // Catch:{ Exception -> 0x0357, all -> 0x0352 }
            if (r7 == 0) goto L_0x035c
            r20 = r9
            float r9 = (float) r2
            float r9 = r9 / r21
            float r9 = r9 / r22
            r7.didWriteData(r5, r9)     // Catch:{ Exception -> 0x034f, all -> 0x0536 }
            goto L_0x035e
        L_0x034f:
            r0 = move-exception
            goto L_0x0553
        L_0x0352:
            r0 = move-exception
            r20 = r9
            goto L_0x0542
        L_0x0357:
            r0 = move-exception
            r20 = r9
            goto L_0x0553
        L_0x035c:
            r20 = r9
        L_0x035e:
            r6 = r13
            r2 = r20
            r13 = r47
            r3 = r49
            goto L_0x0434
        L_0x0367:
            r0 = move-exception
            r20 = r3
            goto L_0x0553
        L_0x036c:
            r5 = r3
            r18 = r6
            r17 = r9
            r9 = -5
            r6 = r2
            r2 = 0
            if (r5 != r9) goto L_0x0402
            byte[] r9 = new byte[r13]     // Catch:{ Exception -> 0x03fe, all -> 0x03fa }
            int r2 = r14.offset     // Catch:{ Exception -> 0x03fe, all -> 0x03fa }
            int r2 = r2 + r13
            r7.limit(r2)     // Catch:{ Exception -> 0x03fe, all -> 0x03fa }
            int r2 = r14.offset     // Catch:{ Exception -> 0x03fe, all -> 0x03fa }
            r7.position(r2)     // Catch:{ Exception -> 0x03fe, all -> 0x03fa }
            r7.get(r9)     // Catch:{ Exception -> 0x03fe, all -> 0x03fa }
            int r2 = r14.size     // Catch:{ Exception -> 0x03fe, all -> 0x03fa }
            r3 = 1
            int r2 = r2 - r3
        L_0x038b:
            if (r2 < 0) goto L_0x03d8
            r13 = 3
            if (r2 <= r13) goto L_0x03d8
            byte r7 = r9[r2]     // Catch:{ Exception -> 0x03d3, all -> 0x03ce }
            if (r7 != r3) goto L_0x03c6
            int r3 = r2 + -1
            byte r3 = r9[r3]     // Catch:{ Exception -> 0x03d3, all -> 0x03ce }
            if (r3 != 0) goto L_0x03c6
            int r3 = r2 + -2
            byte r3 = r9[r3]     // Catch:{ Exception -> 0x03d3, all -> 0x03ce }
            if (r3 != 0) goto L_0x03c6
            int r3 = r2 + -3
            byte r7 = r9[r3]     // Catch:{ Exception -> 0x03d3, all -> 0x03ce }
            if (r7 != 0) goto L_0x03c6
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r3)     // Catch:{ Exception -> 0x03d3, all -> 0x03ce }
            int r7 = r14.size     // Catch:{ Exception -> 0x03d3, all -> 0x03ce }
            int r7 = r7 - r3
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.allocate(r7)     // Catch:{ Exception -> 0x03d3, all -> 0x03ce }
            r20 = r5
            r13 = 0
            java.nio.ByteBuffer r5 = r2.put(r9, r13, r3)     // Catch:{ Exception -> 0x034f, all -> 0x0536 }
            r5.position(r13)     // Catch:{ Exception -> 0x034f, all -> 0x0536 }
            int r5 = r14.size     // Catch:{ Exception -> 0x034f, all -> 0x0536 }
            int r5 = r5 - r3
            java.nio.ByteBuffer r3 = r7.put(r9, r3, r5)     // Catch:{ Exception -> 0x034f, all -> 0x0536 }
            r3.position(r13)     // Catch:{ Exception -> 0x034f, all -> 0x0536 }
            goto L_0x03dc
        L_0x03c6:
            r20 = r5
            int r2 = r2 + -1
            r5 = r20
            r3 = 1
            goto L_0x038b
        L_0x03ce:
            r0 = move-exception
            r20 = r5
            goto L_0x0542
        L_0x03d3:
            r0 = move-exception
            r20 = r5
            goto L_0x0553
        L_0x03d8:
            r20 = r5
            r2 = 0
            r7 = 0
        L_0x03dc:
            r13 = r47
            r3 = r49
            android.media.MediaFormat r5 = android.media.MediaFormat.createVideoFormat(r13, r12, r3)     // Catch:{ Exception -> 0x03f8, all -> 0x03f6 }
            if (r2 == 0) goto L_0x03ee
            if (r7 == 0) goto L_0x03ee
            r5.setByteBuffer(r8, r2)     // Catch:{ Exception -> 0x03f8, all -> 0x03f6 }
            r5.setByteBuffer(r11, r7)     // Catch:{ Exception -> 0x03f8, all -> 0x03f6 }
        L_0x03ee:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x03f8, all -> 0x03f6 }
            r7 = 0
            int r2 = r2.addTrack(r5, r7)     // Catch:{ Exception -> 0x03f8, all -> 0x03f6 }
            goto L_0x0434
        L_0x03f6:
            r0 = move-exception
            goto L_0x040e
        L_0x03f8:
            r0 = move-exception
            goto L_0x0421
        L_0x03fa:
            r0 = move-exception
            r20 = r5
            goto L_0x040c
        L_0x03fe:
            r0 = move-exception
            r20 = r5
            goto L_0x041f
        L_0x0402:
            r20 = r5
            r13 = r47
            r3 = r49
            goto L_0x0432
        L_0x0409:
            r0 = move-exception
            r20 = r3
        L_0x040c:
            r3 = r49
        L_0x040e:
            r11 = r79
            r50 = r84
            r31 = r86
            r1 = r0
            r8 = r3
            r2 = r20
            r9 = r48
            goto L_0x05dd
        L_0x041c:
            r0 = move-exception
            r20 = r3
        L_0x041f:
            r3 = r49
        L_0x0421:
            r6 = r1
            r11 = r3
            r2 = r20
            goto L_0x05b9
        L_0x0427:
            r20 = r3
            r18 = r6
            r17 = r9
            r13 = r47
            r3 = r49
            r6 = r2
        L_0x0432:
            r2 = r20
        L_0x0434:
            int r5 = r14.flags     // Catch:{ Exception -> 0x04ed, all -> 0x04e8 }
            r5 = r5 & 4
            if (r5 == 0) goto L_0x043c
            r5 = 1
            goto L_0x043d
        L_0x043c:
            r5 = 0
        L_0x043d:
            r7 = 0
            r1.releaseOutputBuffer(r10, r7)     // Catch:{ Exception -> 0x04ed, all -> 0x04e8 }
            r7 = r78
            r9 = r5
            r5 = r2
            goto L_0x0232
        L_0x0447:
            if (r10 == r2) goto L_0x0469
            r49 = r3
            r3 = r5
            r2 = r6
            r5 = r7
            r35 = r8
            r7 = r9
            r44 = r11
            r47 = r13
            r46 = r14
            r9 = r17
            r6 = r18
            r14 = 21
            r13 = r73
            r8 = r80
            r69 = r1
            r1 = r77
            r77 = r69
            goto L_0x01d1
        L_0x0469:
            if (r4 != 0) goto L_0x04bc
            r16.drawImage()     // Catch:{ Exception -> 0x04b3, all -> 0x04a6 }
            r2 = r17
            float r10 = (float) r2
            r17 = 1106247680(0x41var_, float:30.0)
            float r10 = r10 / r17
            float r10 = r10 * r21
            float r10 = r10 * r21
            float r10 = r10 * r21
            r49 = r3
            r78 = r4
            long r3 = (long) r10
            r10 = r36
            r10.setPresentationTime(r3)     // Catch:{ Exception -> 0x049f, all -> 0x049d }
            r10.swapBuffers()     // Catch:{ Exception -> 0x049f, all -> 0x049d }
            int r2 = r2 + 1
            float r3 = (float) r2     // Catch:{ Exception -> 0x049f, all -> 0x049d }
            r4 = 1106247680(0x41var_, float:30.0)
            float r4 = r4 * r22
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 < 0) goto L_0x049a
            r1.signalEndOfInputStream()     // Catch:{ Exception -> 0x049f, all -> 0x049d }
            r3 = r2
            r2 = 0
            r4 = 1
            goto L_0x04c7
        L_0x049a:
            r4 = r78
            goto L_0x04c4
        L_0x049d:
            r0 = move-exception
            goto L_0x04a9
        L_0x049f:
            r0 = move-exception
            r6 = r1
            r2 = r5
            r36 = r10
            goto L_0x056b
        L_0x04a6:
            r0 = move-exception
            r49 = r3
        L_0x04a9:
            r11 = r79
            r50 = r84
            r31 = r86
            r1 = r0
            r2 = r5
            goto L_0x055f
        L_0x04b3:
            r0 = move-exception
            r49 = r3
            r10 = r36
            r6 = r1
            r2 = r5
            goto L_0x056b
        L_0x04bc:
            r49 = r3
            r78 = r4
            r2 = r17
            r10 = r36
        L_0x04c4:
            r3 = r2
            r2 = r80
        L_0x04c7:
            r35 = r8
            r36 = r10
            r44 = r11
            r47 = r13
            r46 = r14
            r14 = 21
            r13 = r73
            r8 = r2
            r2 = r6
            r6 = r18
            r69 = r1
            r1 = r77
            r77 = r69
            r70 = r9
            r9 = r3
            r3 = r5
            r5 = r7
            r7 = r70
            goto L_0x01d1
        L_0x04e8:
            r0 = move-exception
            r49 = r3
            goto L_0x0558
        L_0x04ed:
            r0 = move-exception
            r49 = r3
            r10 = r36
            goto L_0x056a
        L_0x04f4:
            r0 = move-exception
            r20 = r3
            r10 = r36
            goto L_0x0553
        L_0x04fa:
            r20 = r3
            r3 = r36
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0538, all -> 0x0536 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0538, all -> 0x0536 }
            r4.<init>()     // Catch:{ Exception -> 0x0538, all -> 0x0536 }
            java.lang.String r5 = "encoderOutputBuffer "
            r4.append(r5)     // Catch:{ Exception -> 0x0538, all -> 0x0536 }
            r4.append(r10)     // Catch:{ Exception -> 0x0538, all -> 0x0536 }
            java.lang.String r5 = " was null"
            r4.append(r5)     // Catch:{ Exception -> 0x0538, all -> 0x0536 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0538, all -> 0x0536 }
            r2.<init>(r4)     // Catch:{ Exception -> 0x0538, all -> 0x0536 }
            throw r2     // Catch:{ Exception -> 0x0538, all -> 0x0536 }
        L_0x051a:
            r20 = r3
            r3 = r36
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0538, all -> 0x0536 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0538, all -> 0x0536 }
            r4.<init>()     // Catch:{ Exception -> 0x0538, all -> 0x0536 }
            java.lang.String r5 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x0538, all -> 0x0536 }
            r4.append(r10)     // Catch:{ Exception -> 0x0538, all -> 0x0536 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0538, all -> 0x0536 }
            r2.<init>(r4)     // Catch:{ Exception -> 0x0538, all -> 0x0536 }
            throw r2     // Catch:{ Exception -> 0x0538, all -> 0x0536 }
        L_0x0536:
            r0 = move-exception
            goto L_0x0542
        L_0x0538:
            r0 = move-exception
            r6 = r1
            r36 = r3
            goto L_0x0554
        L_0x053d:
            r0 = move-exception
            goto L_0x054f
        L_0x053f:
            r0 = move-exception
            r20 = r3
        L_0x0542:
            r11 = r79
            r50 = r84
            r31 = r86
            r1 = r0
            r2 = r20
            goto L_0x055f
        L_0x054c:
            r0 = move-exception
            r1 = r77
        L_0x054f:
            r20 = r3
            r3 = r36
        L_0x0553:
            r6 = r1
        L_0x0554:
            r2 = r20
            goto L_0x056b
        L_0x0557:
            r0 = move-exception
        L_0x0558:
            r11 = r79
            r50 = r84
            r31 = r86
            r1 = r0
        L_0x055f:
            r9 = r48
            r8 = r49
            goto L_0x05dd
        L_0x0565:
            r0 = move-exception
            r1 = r77
            r3 = r36
        L_0x056a:
            r6 = r1
        L_0x056b:
            r11 = r49
            goto L_0x05b9
        L_0x056e:
            r1 = r77
            r3 = r36
            r10 = r79
            r6 = r1
            r9 = r48
            r11 = r49
            r1 = 0
            r43 = 0
            r69 = r3
            r3 = r2
            r2 = r69
            goto L_0x062f
        L_0x0583:
            r0 = move-exception
            r1 = r77
            r3 = r36
            r6 = r1
            r11 = r49
            r2 = -5
            goto L_0x05b9
        L_0x058d:
            r0 = move-exception
            r11 = r79
            r50 = r84
            r31 = r86
            r1 = r0
            r9 = r48
            goto L_0x05c7
        L_0x0598:
            r0 = move-exception
            r1 = r77
            r3 = r36
            r6 = r1
            r11 = r49
            goto L_0x05aa
        L_0x05a1:
            r0 = move-exception
            r3 = r2
            r1 = r6
            r48 = r9
            r49 = r11
            r36 = r3
        L_0x05aa:
            r2 = -5
            r16 = 0
            goto L_0x05b9
        L_0x05ae:
            r0 = move-exception
            r1 = r6
            r48 = r9
            r49 = r11
            r2 = -5
            r16 = 0
            r36 = 0
        L_0x05b9:
            r1 = r0
            goto L_0x05ec
        L_0x05bb:
            r0 = move-exception
            r48 = r9
            r49 = r11
            r11 = r79
            r50 = r84
            r31 = r86
            r1 = r0
        L_0x05c7:
            r8 = r49
            goto L_0x05dc
        L_0x05ca:
            r0 = move-exception
            r48 = r9
            r49 = r11
            goto L_0x05e5
        L_0x05d0:
            r0 = move-exception
            r48 = r9
            r8 = r78
            r11 = r79
            r50 = r84
            r31 = r86
            r1 = r0
        L_0x05dc:
            r2 = -5
        L_0x05dd:
            r6 = 0
            goto L_0x13cf
        L_0x05e0:
            r0 = move-exception
            r48 = r9
            r11 = r78
        L_0x05e5:
            r1 = r0
        L_0x05e6:
            r2 = -5
            r6 = 0
            r16 = 0
            r36 = 0
        L_0x05ec:
            boolean r3 = r1 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x0673 }
            if (r3 == 0) goto L_0x05f5
            if (r91 != 0) goto L_0x05f5
            r43 = 1
            goto L_0x05f7
        L_0x05f5:
            r43 = 0
        L_0x05f7:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0667 }
            r3.<init>()     // Catch:{ all -> 0x0667 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x0667 }
            r9 = r48
            r3.append(r9)     // Catch:{ all -> 0x0665 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x0665 }
            r10 = r79
            r3.append(r10)     // Catch:{ all -> 0x065a }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x065a }
            r3.append(r11)     // Catch:{ all -> 0x065a }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x065a }
            r3.append(r12)     // Catch:{ all -> 0x065a }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x065a }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x065a }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x065a }
            r3 = r2
            r2 = r36
            r1 = 1
        L_0x062f:
            if (r16 == 0) goto L_0x063d
            r16.release()     // Catch:{ all -> 0x0635 }
            goto L_0x063d
        L_0x0635:
            r0 = move-exception
            r50 = r84
            r31 = r86
            r1 = r0
            r2 = r3
            goto L_0x0660
        L_0x063d:
            if (r2 == 0) goto L_0x0642
            r2.release()     // Catch:{ all -> 0x0635 }
        L_0x0642:
            if (r6 == 0) goto L_0x064a
            r6.stop()     // Catch:{ all -> 0x0635 }
            r6.release()     // Catch:{ all -> 0x0635 }
        L_0x064a:
            r72.checkConversionCanceled()     // Catch:{ all -> 0x0635 }
            r50 = r84
            r31 = r86
            r2 = r3
            r3 = r9
            r8 = r11
            r9 = r12
            r6 = r43
            r11 = r10
            goto L_0x1394
        L_0x065a:
            r0 = move-exception
            r50 = r84
            r31 = r86
            r1 = r0
        L_0x0660:
            r8 = r11
            r6 = r43
            goto L_0x00cf
        L_0x0665:
            r0 = move-exception
            goto L_0x066a
        L_0x0667:
            r0 = move-exception
            r9 = r48
        L_0x066a:
            r50 = r84
            r31 = r86
            r1 = r0
            r8 = r11
            r6 = r43
            goto L_0x067d
        L_0x0673:
            r0 = move-exception
            r9 = r48
            r50 = r84
            r31 = r86
            r1 = r0
            r8 = r11
            r6 = 0
        L_0x067d:
            r11 = r79
            goto L_0x13cf
        L_0x0681:
            r8 = r1
            r11 = r23
            r14 = r25
            r7 = r26
            r13 = r31
            r1 = 921600(0xe1000, float:1.291437E-39)
            r18 = -5
            android.media.MediaExtractor r2 = new android.media.MediaExtractor     // Catch:{ all -> 0x13b6 }
            r2.<init>()     // Catch:{ all -> 0x13b6 }
            r15.extractor = r2     // Catch:{ all -> 0x13b6 }
            r5 = r73
            r2.setDataSource(r5)     // Catch:{ all -> 0x13b6 }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x13b6 }
            r6 = 0
            int r4 = org.telegram.messenger.MediaController.findTrack(r2, r6)     // Catch:{ all -> 0x13b6 }
            r2 = -1
            if (r9 == r2) goto L_0x06bb
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x06af }
            r3 = 1
            int r2 = org.telegram.messenger.MediaController.findTrack(r2, r3)     // Catch:{ all -> 0x06af }
            r44 = r11
            goto L_0x06bf
        L_0x06af:
            r0 = move-exception
            r8 = r78
            r50 = r84
            r31 = r86
            r1 = r0
            r11 = r10
            r2 = -5
            goto L_0x13cf
        L_0x06bb:
            r3 = 1
            r44 = r11
            r2 = -1
        L_0x06bf:
            java.lang.String r11 = "mime"
            if (r4 < 0) goto L_0x06d5
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ all -> 0x06af }
            android.media.MediaFormat r3 = r3.getTrackFormat(r4)     // Catch:{ all -> 0x06af }
            java.lang.String r3 = r3.getString(r11)     // Catch:{ all -> 0x06af }
            boolean r3 = r3.equals(r13)     // Catch:{ all -> 0x06af }
            if (r3 != 0) goto L_0x06d5
            r3 = 1
            goto L_0x06d6
        L_0x06d5:
            r3 = 0
        L_0x06d6:
            if (r90 != 0) goto L_0x0727
            if (r3 == 0) goto L_0x06db
            goto L_0x0727
        L_0x06db:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x0718 }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ all -> 0x0718 }
            r1 = -1
            if (r9 == r1) goto L_0x06e4
            r13 = 1
            goto L_0x06e5
        L_0x06e4:
            r13 = 0
        L_0x06e5:
            r1 = r72
            r11 = 1
            r4 = r14
            r14 = r5
            r7 = 0
            r5 = r82
            r14 = 0
            r7 = r84
            r14 = r10
            r9 = r88
            r11 = r74
            r12 = r13
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ all -> 0x0709 }
            r9 = r77
            r8 = r78
            r3 = r80
            r50 = r84
            r31 = r86
            r11 = r14
            r1 = 0
            r2 = -5
            r6 = 0
            goto L_0x1394
        L_0x0709:
            r0 = move-exception
            r12 = r77
            r8 = r78
            r9 = r80
            r50 = r84
            r31 = r86
            r1 = r0
            r11 = r14
            goto L_0x05dc
        L_0x0718:
            r0 = move-exception
            r12 = r77
            r8 = r78
            r9 = r80
            r50 = r84
            r31 = r86
            r1 = r0
            r11 = r10
            goto L_0x05dc
        L_0x0727:
            r12 = r5
            r69 = r14
            r14 = r10
            r10 = r69
            if (r4 < 0) goto L_0x135b
            r18 = -2147483648(0xfffffffvar_, double:NaN)
            r3 = 1000(0x3e8, float:1.401E-42)
            int r3 = r3 / r14
            int r3 = r3 * 1000
            long r5 = (long) r3     // Catch:{ Exception -> 0x12cb, all -> 0x12b9 }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x12cb, all -> 0x12b9 }
            r3.selectTrack(r4)     // Catch:{ Exception -> 0x12cb, all -> 0x12b9 }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x12cb, all -> 0x12b9 }
            android.media.MediaFormat r9 = r3.getTrackFormat(r4)     // Catch:{ Exception -> 0x12cb, all -> 0x12b9 }
            r23 = 0
            int r3 = (r86 > r23 ? 1 : (r86 == r23 ? 0 : -1))
            if (r3 < 0) goto L_0x0766
            r3 = 1157234688(0x44fa0000, float:2000.0)
            int r3 = (r22 > r3 ? 1 : (r22 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x0753
            r3 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x0761
        L_0x0753:
            r3 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r3 = (r22 > r3 ? 1 : (r22 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x075e
            r3 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x0761
        L_0x075e:
            r3 = 1560000(0x17cdc0, float:2.186026E-39)
        L_0x0761:
            r1 = r81
            r23 = 0
            goto L_0x0776
        L_0x0766:
            if (r80 > 0) goto L_0x0770
            r1 = r81
            r23 = r86
            r3 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x0776
        L_0x0770:
            r3 = r80
            r1 = r81
            r23 = r86
        L_0x0776:
            if (r1 <= 0) goto L_0x07a0
            int r3 = java.lang.Math.min(r1, r3)     // Catch:{ Exception -> 0x078b, all -> 0x077d }
            goto L_0x07a0
        L_0x077d:
            r0 = move-exception
            r12 = r77
            r8 = r78
            r50 = r84
            r1 = r0
            r9 = r3
            r11 = r14
            r31 = r23
            goto L_0x05dc
        L_0x078b:
            r0 = move-exception
            r50 = r84
            r5 = r0
            r61 = r4
            r11 = r14
            r31 = r23
        L_0x0794:
            r1 = 0
            r2 = -5
            r4 = 0
            r6 = 0
            r27 = 0
            r40 = 0
            r58 = 0
            goto L_0x12e2
        L_0x07a0:
            r25 = 0
            int r27 = (r23 > r25 ? 1 : (r23 == r25 ? 0 : -1))
            r45 = r7
            r35 = r8
            if (r27 < 0) goto L_0x07ad
            r7 = r16
            goto L_0x07af
        L_0x07ad:
            r7 = r23
        L_0x07af:
            int r23 = (r7 > r25 ? 1 : (r7 == r25 ? 0 : -1))
            if (r23 < 0) goto L_0x07dc
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x07d2, all -> 0x07c2 }
            r23 = r2
            r2 = 0
            r1.seekTo(r7, r2)     // Catch:{ Exception -> 0x07d2, all -> 0x07c2 }
            r24 = r4
            r25 = r5
            r5 = 0
            goto L_0x080b
        L_0x07c2:
            r0 = move-exception
            r12 = r77
            r50 = r84
            r1 = r0
            r9 = r3
        L_0x07c9:
            r31 = r7
            r11 = r14
            r2 = -5
            r6 = 0
            r8 = r78
            goto L_0x13cf
        L_0x07d2:
            r0 = move-exception
            r50 = r84
            r5 = r0
            r61 = r4
            r31 = r7
            r11 = r14
            goto L_0x0794
        L_0x07dc:
            r23 = r2
            r24 = 0
            r1 = r82
            int r26 = (r1 > r24 ? 1 : (r1 == r24 ? 0 : -1))
            if (r26 <= 0) goto L_0x07ff
            r24 = r4
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x07f5, all -> 0x07c2 }
            r25 = r5
            r5 = 0
            r4.seekTo(r1, r5)     // Catch:{ Exception -> 0x07f5, all -> 0x07c2 }
            r4 = r96
            r5 = 0
            goto L_0x080d
        L_0x07f5:
            r0 = move-exception
            r50 = r84
            r5 = r0
        L_0x07f9:
            r31 = r7
            r11 = r14
            r61 = r24
            goto L_0x0794
        L_0x07ff:
            r24 = r4
            r25 = r5
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x12af, all -> 0x12a1 }
            r1 = 0
            r5 = 0
            r4.seekTo(r5, r1)     // Catch:{ Exception -> 0x1298, all -> 0x1291 }
        L_0x080b:
            r4 = r96
        L_0x080d:
            if (r4 == 0) goto L_0x0826
            r1 = 90
            r2 = r75
            if (r2 == r1) goto L_0x081f
            r1 = 270(0x10e, float:3.78E-43)
            if (r2 != r1) goto L_0x081a
            goto L_0x081f
        L_0x081a:
            int r1 = r4.transformWidth     // Catch:{ Exception -> 0x07f5, all -> 0x07c2 }
            int r5 = r4.transformHeight     // Catch:{ Exception -> 0x07f5, all -> 0x07c2 }
            goto L_0x0823
        L_0x081f:
            int r1 = r4.transformHeight     // Catch:{ Exception -> 0x07f5, all -> 0x07c2 }
            int r5 = r4.transformWidth     // Catch:{ Exception -> 0x07f5, all -> 0x07c2 }
        L_0x0823:
            r6 = r5
            r5 = r1
            goto L_0x082c
        L_0x0826:
            r2 = r75
            r5 = r77
            r6 = r78
        L_0x082c:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x12af, all -> 0x12a1 }
            if (r1 == 0) goto L_0x084c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x07f5, all -> 0x07c2 }
            r1.<init>()     // Catch:{ Exception -> 0x07f5, all -> 0x07c2 }
            java.lang.String r2 = "create encoder with w = "
            r1.append(r2)     // Catch:{ Exception -> 0x07f5, all -> 0x07c2 }
            r1.append(r5)     // Catch:{ Exception -> 0x07f5, all -> 0x07c2 }
            java.lang.String r2 = " h = "
            r1.append(r2)     // Catch:{ Exception -> 0x07f5, all -> 0x07c2 }
            r1.append(r6)     // Catch:{ Exception -> 0x07f5, all -> 0x07c2 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x07f5, all -> 0x07c2 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x07f5, all -> 0x07c2 }
        L_0x084c:
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r13, r5, r6)     // Catch:{ Exception -> 0x12af, all -> 0x12a1 }
            java.lang.String r2 = "color-format"
            r4 = 2130708361(0x7var_, float:1.701803E38)
            r1.setInteger(r2, r4)     // Catch:{ Exception -> 0x12af, all -> 0x12a1 }
            java.lang.String r2 = "bitrate"
            r1.setInteger(r2, r3)     // Catch:{ Exception -> 0x12af, all -> 0x12a1 }
            java.lang.String r2 = "frame-rate"
            r1.setInteger(r2, r14)     // Catch:{ Exception -> 0x12af, all -> 0x12a1 }
            java.lang.String r2 = "i-frame-interval"
            r4 = 2
            r1.setInteger(r2, r4)     // Catch:{ Exception -> 0x12af, all -> 0x12a1 }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x12af, all -> 0x12a1 }
            r2 = 23
            if (r4 >= r2) goto L_0x0897
            int r2 = java.lang.Math.min(r6, r5)     // Catch:{ Exception -> 0x07f5, all -> 0x07c2 }
            r80 = r4
            r4 = 480(0x1e0, float:6.73E-43)
            if (r2 > r4) goto L_0x0899
            r2 = 921600(0xe1000, float:1.291437E-39)
            if (r3 <= r2) goto L_0x087e
            goto L_0x087f
        L_0x087e:
            r2 = r3
        L_0x087f:
            java.lang.String r3 = "bitrate"
            r1.setInteger(r3, r2)     // Catch:{ Exception -> 0x0890, all -> 0x0887 }
            r20 = r2
            goto L_0x089b
        L_0x0887:
            r0 = move-exception
            r12 = r77
            r50 = r84
            r1 = r0
            r9 = r2
            goto L_0x07c9
        L_0x0890:
            r0 = move-exception
            r50 = r84
            r5 = r0
            r3 = r2
            goto L_0x07f9
        L_0x0897:
            r80 = r4
        L_0x0899:
            r20 = r3
        L_0x089b:
            android.media.MediaCodec r4 = android.media.MediaCodec.createEncoderByType(r13)     // Catch:{ Exception -> 0x1283, all -> 0x1271 }
            r2 = 1
            r3 = 0
            r4.configure(r1, r3, r3, r2)     // Catch:{ Exception -> 0x125e, all -> 0x1271 }
            org.telegram.messenger.video.InputSurface r1 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x125e, all -> 0x1271 }
            android.view.Surface r2 = r4.createInputSurface()     // Catch:{ Exception -> 0x125e, all -> 0x1271 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x125e, all -> 0x1271 }
            r1.makeCurrent()     // Catch:{ Exception -> 0x1244, all -> 0x1271 }
            r4.start()     // Catch:{ Exception -> 0x1244, all -> 0x1271 }
            java.lang.String r2 = r9.getString(r11)     // Catch:{ Exception -> 0x1244, all -> 0x1271 }
            android.media.MediaCodec r2 = android.media.MediaCodec.createDecoderByType(r2)     // Catch:{ Exception -> 0x1244, all -> 0x1271 }
            org.telegram.messenger.video.OutputSurface r27 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x1228, all -> 0x1271 }
            r28 = 0
            float r3 = (float) r14
            r29 = 0
            r58 = r1
            r1 = r27
            r60 = r2
            r59 = r23
            r2 = r92
            r23 = r3
            r3 = r28
            r62 = r80
            r80 = r4
            r61 = r24
            r4 = r93
            r63 = r5
            r24 = r25
            r5 = r94
            r64 = r6
            r6 = r96
            r31 = r7
            r8 = r45
            r7 = r77
            r66 = r8
            r65 = r35
            r8 = r78
            r67 = r9
            r9 = r75
            r68 = r10
            r10 = r23
            r47 = r13
            r14 = r44
            r13 = r11
            r11 = r29
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x121b, all -> 0x1214 }
            android.view.Surface r1 = r27.getSurface()     // Catch:{ Exception -> 0x1203, all -> 0x1214 }
            r3 = r60
            r2 = r67
            r4 = 0
            r5 = 0
            r3.configure(r2, r1, r4, r5)     // Catch:{ Exception -> 0x11f9, all -> 0x1214 }
            r3.start()     // Catch:{ Exception -> 0x11f9, all -> 0x1214 }
            r1 = r62
            r2 = 21
            if (r1 >= r2) goto L_0x0940
            java.nio.ByteBuffer[] r6 = r3.getInputBuffers()     // Catch:{ Exception -> 0x0932, all -> 0x0926 }
            java.nio.ByteBuffer[] r1 = r80.getOutputBuffers()     // Catch:{ Exception -> 0x0932, all -> 0x0926 }
            r2 = r59
            r69 = r6
            r6 = r1
            r1 = r69
            goto L_0x0944
        L_0x0926:
            r0 = move-exception
        L_0x0927:
            r12 = r77
            r8 = r78
            r11 = r79
            r50 = r84
            r1 = r0
            goto L_0x127f
        L_0x0932:
            r0 = move-exception
            r11 = r79
            r6 = r80
            r50 = r84
            r5 = r0
            r40 = r4
            r1 = 0
            r2 = -5
            goto L_0x123f
        L_0x0940:
            r1 = r4
            r6 = r1
            r2 = r59
        L_0x0944:
            if (r2 < 0) goto L_0x0a45
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x0a37, all -> 0x0a2a }
            android.media.MediaFormat r5 = r5.getTrackFormat(r2)     // Catch:{ Exception -> 0x0a37, all -> 0x0a2a }
            java.lang.String r7 = r5.getString(r13)     // Catch:{ Exception -> 0x0a37, all -> 0x0a2a }
            java.lang.String r8 = "audio/mp4a-latm"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x0a37, all -> 0x0a2a }
            if (r7 != 0) goto L_0x0967
            java.lang.String r7 = r5.getString(r13)     // Catch:{ Exception -> 0x0932, all -> 0x0926 }
            java.lang.String r8 = "audio/mpeg"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x0932, all -> 0x0926 }
            if (r7 == 0) goto L_0x0965
            goto L_0x0967
        L_0x0965:
            r7 = 0
            goto L_0x0968
        L_0x0967:
            r7 = 1
        L_0x0968:
            java.lang.String r8 = r5.getString(r13)     // Catch:{ Exception -> 0x0a37, all -> 0x0a2a }
            java.lang.String r9 = "audio/unknown"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x0a37, all -> 0x0a2a }
            if (r8 == 0) goto L_0x0975
            r2 = -1
        L_0x0975:
            if (r2 < 0) goto L_0x0a23
            if (r7 == 0) goto L_0x09d4
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x09c1, all -> 0x09bc }
            r9 = 1
            int r8 = r8.addTrack(r5, r9)     // Catch:{ Exception -> 0x09c1, all -> 0x09bc }
            android.media.MediaExtractor r10 = r15.extractor     // Catch:{ Exception -> 0x09c1, all -> 0x09bc }
            r10.selectTrack(r2)     // Catch:{ Exception -> 0x09c1, all -> 0x09bc }
            java.lang.String r10 = "max-input-size"
            int r5 = r5.getInteger(r10)     // Catch:{ Exception -> 0x098c, all -> 0x0926 }
            goto L_0x0992
        L_0x098c:
            r0 = move-exception
            r5 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ Exception -> 0x09c1, all -> 0x09bc }
            r5 = 0
        L_0x0992:
            if (r5 > 0) goto L_0x0996
            r5 = 65536(0x10000, float:9.18355E-41)
        L_0x0996:
            java.nio.ByteBuffer r10 = java.nio.ByteBuffer.allocateDirect(r5)     // Catch:{ Exception -> 0x09c1, all -> 0x09bc }
            r87 = r5
            r86 = r10
            r4 = 0
            r9 = r82
            int r11 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r11 <= 0) goto L_0x09ad
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x09ed, all -> 0x0926 }
            r13 = 0
            r11.seekTo(r9, r13)     // Catch:{ Exception -> 0x09ed, all -> 0x0926 }
            goto L_0x09b3
        L_0x09ad:
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x09ed, all -> 0x0926 }
            r13 = 0
            r11.seekTo(r4, r13)     // Catch:{ Exception -> 0x09ed, all -> 0x0926 }
        L_0x09b3:
            r4 = r84
            r23 = r86
            r11 = r87
            r13 = 0
            goto L_0x0a4f
        L_0x09bc:
            r0 = move-exception
            r9 = r82
            goto L_0x0927
        L_0x09c1:
            r0 = move-exception
            r9 = r82
        L_0x09c4:
            r11 = r79
            r6 = r80
            r50 = r84
            r5 = r0
            r4 = r3
            r3 = r20
            r1 = 0
            r2 = -5
            r40 = 0
            goto L_0x12e2
        L_0x09d4:
            r9 = r82
            r8 = r5
            r4 = 0
            android.media.MediaExtractor r11 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0a21, all -> 0x0a1f }
            r11.<init>()     // Catch:{ Exception -> 0x0a21, all -> 0x0a1f }
            r11.setDataSource(r12)     // Catch:{ Exception -> 0x0a21, all -> 0x0a1f }
            r11.selectTrack(r2)     // Catch:{ Exception -> 0x0a21, all -> 0x0a1f }
            int r13 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r13 <= 0) goto L_0x09ef
            r13 = 0
            r11.seekTo(r9, r13)     // Catch:{ Exception -> 0x09ed, all -> 0x0926 }
            goto L_0x09f3
        L_0x09ed:
            r0 = move-exception
            goto L_0x09c4
        L_0x09ef:
            r13 = 0
            r11.seekTo(r4, r13)     // Catch:{ Exception -> 0x0a21, all -> 0x0a1f }
        L_0x09f3:
            org.telegram.messenger.video.AudioRecoder r13 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x0a21, all -> 0x0a1f }
            r13.<init>(r8, r11, r2)     // Catch:{ Exception -> 0x0a21, all -> 0x0a1f }
            r13.startTime = r9     // Catch:{ Exception -> 0x0a11, all -> 0x0a1f }
            r4 = r84
            r13.endTime = r4     // Catch:{ Exception -> 0x0a0f, all -> 0x0a0d }
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x0a0f, all -> 0x0a0d }
            android.media.MediaFormat r11 = r13.format     // Catch:{ Exception -> 0x0a0f, all -> 0x0a0d }
            r86 = r2
            r2 = 1
            int r8 = r8.addTrack(r11, r2)     // Catch:{ Exception -> 0x0a0f, all -> 0x0a0d }
            r2 = r86
            r11 = 0
            goto L_0x0a4d
        L_0x0a0d:
            r0 = move-exception
            goto L_0x0a2f
        L_0x0a0f:
            r0 = move-exception
            goto L_0x0a14
        L_0x0a11:
            r0 = move-exception
            r4 = r84
        L_0x0a14:
            r11 = r79
            r6 = r80
            r50 = r4
            r40 = r13
            r1 = 0
            goto L_0x11f7
        L_0x0a1f:
            r0 = move-exception
            goto L_0x0a2d
        L_0x0a21:
            r0 = move-exception
            goto L_0x0a3a
        L_0x0a23:
            r9 = r82
            r4 = r84
            r86 = r2
            goto L_0x0a4a
        L_0x0a2a:
            r0 = move-exception
            r9 = r82
        L_0x0a2d:
            r4 = r84
        L_0x0a2f:
            r12 = r77
            r8 = r78
            r11 = r79
            goto L_0x127c
        L_0x0a37:
            r0 = move-exception
            r9 = r82
        L_0x0a3a:
            r4 = r84
            r11 = r79
            r6 = r80
            r50 = r4
            r1 = 0
            goto L_0x1212
        L_0x0a45:
            r9 = r82
            r4 = r84
            r7 = 1
        L_0x0a4a:
            r8 = -5
            r11 = 0
            r13 = 0
        L_0x0a4d:
            r23 = 0
        L_0x0a4f:
            if (r2 >= 0) goto L_0x0a54
            r26 = 1
            goto L_0x0a56
        L_0x0a54:
            r26 = 0
        L_0x0a56:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x11e8, all -> 0x11e3 }
            r44 = r16
            r37 = r18
            r29 = r23
            r30 = r26
            r84 = 1
            r18 = -5
            r19 = 0
            r23 = 0
            r28 = 0
            r33 = 0
            r35 = 0
            r26 = r6
            r6 = 0
        L_0x0a72:
            if (r19 == 0) goto L_0x0a8c
            if (r7 != 0) goto L_0x0a79
            if (r30 != 0) goto L_0x0a79
            goto L_0x0a8c
        L_0x0a79:
            r9 = r77
            r8 = r78
            r11 = r79
            r2 = r3
            r50 = r4
            r40 = r13
            r3 = r20
            r1 = 0
            r6 = 0
            r4 = r80
            goto L_0x1323
        L_0x0a8c:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x11cd, all -> 0x11ba }
            if (r7 != 0) goto L_0x0aba
            if (r13 == 0) goto L_0x0aba
            org.telegram.messenger.video.MP4Builder r12 = r15.mediaMuxer     // Catch:{ Exception -> 0x0aac, all -> 0x0a9c }
            boolean r12 = r13.step(r12, r8)     // Catch:{ Exception -> 0x0aac, all -> 0x0a9c }
            r30 = r12
            goto L_0x0aba
        L_0x0a9c:
            r0 = move-exception
            r12 = r77
            r8 = r78
            r11 = r79
            r1 = r0
            r50 = r4
            r2 = r18
            r9 = r20
            goto L_0x05dd
        L_0x0aac:
            r0 = move-exception
            r11 = r79
            r6 = r80
            r50 = r4
            r40 = r13
        L_0x0ab5:
            r2 = r18
        L_0x0ab7:
            r1 = 0
            goto L_0x123e
        L_0x0aba:
            if (r6 != 0) goto L_0x0CLASSNAME
            android.media.MediaExtractor r12 = r15.extractor     // Catch:{ Exception -> 0x0c6c, all -> 0x0CLASSNAME }
            int r12 = r12.getSampleTrackIndex()     // Catch:{ Exception -> 0x0c6c, all -> 0x0CLASSNAME }
            r85 = r6
            r6 = r61
            if (r12 != r6) goto L_0x0b40
            r86 = r13
            r46 = r14
            r13 = 2500(0x9c4, double:1.235E-320)
            int r12 = r3.dequeueInputBuffer(r13)     // Catch:{ Exception -> 0x0b30, all -> 0x0a9c }
            if (r12 < 0) goto L_0x0b20
            int r13 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b30, all -> 0x0a9c }
            r14 = 21
            if (r13 >= r14) goto L_0x0add
            r13 = r1[r12]     // Catch:{ Exception -> 0x0b30, all -> 0x0a9c }
            goto L_0x0ae1
        L_0x0add:
            java.nio.ByteBuffer r13 = r3.getInputBuffer(r12)     // Catch:{ Exception -> 0x0b30, all -> 0x0a9c }
        L_0x0ae1:
            android.media.MediaExtractor r14 = r15.extractor     // Catch:{ Exception -> 0x0b30, all -> 0x0a9c }
            r87 = r1
            r1 = 0
            int r54 = r14.readSampleData(r13, r1)     // Catch:{ Exception -> 0x0b14, all -> 0x0a9c }
            if (r54 >= 0) goto L_0x0afd
            r53 = 0
            r54 = 0
            r55 = 0
            r57 = 4
            r51 = r3
            r52 = r12
            r51.queueInputBuffer(r52, r53, r54, r55, r57)     // Catch:{ Exception -> 0x0b30, all -> 0x0a9c }
            r1 = 1
            goto L_0x0b24
        L_0x0afd:
            r53 = 0
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0b30, all -> 0x0a9c }
            long r55 = r1.getSampleTime()     // Catch:{ Exception -> 0x0b30, all -> 0x0a9c }
            r57 = 0
            r51 = r3
            r52 = r12
            r51.queueInputBuffer(r52, r53, r54, r55, r57)     // Catch:{ Exception -> 0x0b30, all -> 0x0a9c }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0b30, all -> 0x0a9c }
            r1.advance()     // Catch:{ Exception -> 0x0b30, all -> 0x0a9c }
            goto L_0x0b22
        L_0x0b14:
            r0 = move-exception
            r11 = r79
            r40 = r86
            r50 = r4
            r61 = r6
            r2 = r18
            goto L_0x0b3c
        L_0x0b20:
            r87 = r1
        L_0x0b22:
            r1 = r85
        L_0x0b24:
            r59 = r2
            r48 = r4
            r61 = r6
            r14 = r7
            r13 = r68
            r2 = r1
            goto L_0x0CLASSNAME
        L_0x0b30:
            r0 = move-exception
            r11 = r79
            r40 = r86
            r50 = r4
            r61 = r6
            r2 = r18
            r1 = 0
        L_0x0b3c:
            r6 = r80
            goto L_0x123e
        L_0x0b40:
            r87 = r1
            r86 = r13
            r46 = r14
            if (r7 == 0) goto L_0x0CLASSNAME
            r1 = -1
            if (r2 == r1) goto L_0x0CLASSNAME
            if (r12 != r2) goto L_0x0CLASSNAME
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
            r12 = 28
            if (r1 < r12) goto L_0x0b79
            android.media.MediaExtractor r12 = r15.extractor     // Catch:{ Exception -> 0x0b6c, all -> 0x0a9c }
            long r12 = r12.getSampleSize()     // Catch:{ Exception -> 0x0b6c, all -> 0x0a9c }
            r61 = r6
            r14 = r7
            long r6 = (long) r11
            int r48 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r48 <= 0) goto L_0x0b7c
            r6 = 1024(0x400, double:5.06E-321)
            long r12 = r12 + r6
            int r11 = (int) r12
            java.nio.ByteBuffer r29 = java.nio.ByteBuffer.allocateDirect(r11)     // Catch:{ Exception -> 0x0b6a, all -> 0x0a9c }
            goto L_0x0b7c
        L_0x0b6a:
            r0 = move-exception
            goto L_0x0b6f
        L_0x0b6c:
            r0 = move-exception
            r61 = r6
        L_0x0b6f:
            r11 = r79
            r6 = r80
            r40 = r86
            r50 = r4
            goto L_0x0ab5
        L_0x0b79:
            r61 = r6
            r14 = r7
        L_0x0b7c:
            r6 = r29
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x0c1d, all -> 0x0CLASSNAME }
            r12 = 0
            int r7 = r7.readSampleData(r6, r12)     // Catch:{ Exception -> 0x0c1d, all -> 0x0CLASSNAME }
            r13 = r68
            r13.size = r7     // Catch:{ Exception -> 0x0c1d, all -> 0x0CLASSNAME }
            r7 = 21
            if (r1 >= r7) goto L_0x0b95
            r6.position(r12)     // Catch:{ Exception -> 0x0b6a, all -> 0x0a9c }
            int r1 = r13.size     // Catch:{ Exception -> 0x0b6a, all -> 0x0a9c }
            r6.limit(r1)     // Catch:{ Exception -> 0x0b6a, all -> 0x0a9c }
        L_0x0b95:
            int r1 = r13.size     // Catch:{ Exception -> 0x0c1d, all -> 0x0CLASSNAME }
            if (r1 < 0) goto L_0x0baa
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0b6a, all -> 0x0a9c }
            r7 = r2
            long r1 = r1.getSampleTime()     // Catch:{ Exception -> 0x0b6a, all -> 0x0a9c }
            r13.presentationTimeUs = r1     // Catch:{ Exception -> 0x0b6a, all -> 0x0a9c }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0b6a, all -> 0x0a9c }
            r1.advance()     // Catch:{ Exception -> 0x0b6a, all -> 0x0a9c }
            r1 = r85
            goto L_0x0baf
        L_0x0baa:
            r7 = r2
            r1 = 0
            r13.size = r1     // Catch:{ Exception -> 0x0c0a, all -> 0x0CLASSNAME }
            r1 = 1
        L_0x0baf:
            int r2 = r13.size     // Catch:{ Exception -> 0x0c1d, all -> 0x0CLASSNAME }
            if (r2 <= 0) goto L_0x0bfd
            r41 = 0
            int r2 = (r4 > r41 ? 1 : (r4 == r41 ? 0 : -1))
            if (r2 < 0) goto L_0x0bc2
            r85 = r1
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0b6a, all -> 0x0a9c }
            int r12 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r12 >= 0) goto L_0x0bff
            goto L_0x0bc4
        L_0x0bc2:
            r85 = r1
        L_0x0bc4:
            r1 = 0
            r13.offset = r1     // Catch:{ Exception -> 0x0c0a, all -> 0x0CLASSNAME }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0c0a, all -> 0x0CLASSNAME }
            int r2 = r2.getSampleFlags()     // Catch:{ Exception -> 0x0c0a, all -> 0x0CLASSNAME }
            r13.flags = r2     // Catch:{ Exception -> 0x0c0a, all -> 0x0CLASSNAME }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0c0a, all -> 0x0CLASSNAME }
            r48 = r4
            long r4 = r2.writeSampleData(r8, r6, r13, r1)     // Catch:{ Exception -> 0x0bfb, all -> 0x0c5b }
            r1 = 0
            int r12 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r12 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r15.callback     // Catch:{ Exception -> 0x0c5d, all -> 0x0c5b }
            if (r1 == 0) goto L_0x0CLASSNAME
            r2 = r6
            r59 = r7
            long r6 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0c5d, all -> 0x0c5b }
            long r51 = r6 - r9
            int r12 = (r51 > r33 ? 1 : (r51 == r33 ? 0 : -1))
            if (r12 <= 0) goto L_0x0bee
            long r33 = r6 - r9
        L_0x0bee:
            r6 = r33
            float r12 = (float) r6     // Catch:{ Exception -> 0x0c5d, all -> 0x0c5b }
            float r12 = r12 / r21
            float r12 = r12 / r22
            r1.didWriteData(r4, r12)     // Catch:{ Exception -> 0x0c5d, all -> 0x0c5b }
            r33 = r6
            goto L_0x0CLASSNAME
        L_0x0bfb:
            r0 = move-exception
            goto L_0x0c0d
        L_0x0bfd:
            r85 = r1
        L_0x0bff:
            r48 = r4
        L_0x0CLASSNAME:
            r2 = r6
            r59 = r7
        L_0x0CLASSNAME:
            r29 = r2
            r1 = 0
            r2 = r85
            goto L_0x0CLASSNAME
        L_0x0c0a:
            r0 = move-exception
            r48 = r4
        L_0x0c0d:
            r11 = r79
            r6 = r80
            r40 = r86
            r5 = r0
            r4 = r3
            r2 = r18
            r3 = r20
            r50 = r48
            goto L_0x12e2
        L_0x0c1d:
            r0 = move-exception
            r48 = r4
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = move-exception
            r48 = r4
            r61 = r6
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r59 = r2
            r48 = r4
            r61 = r6
            r14 = r7
            r13 = r68
            goto L_0x0c3b
        L_0x0CLASSNAME:
            r59 = r2
            r48 = r4
            r61 = r6
            r14 = r7
            r13 = r68
            r1 = -1
        L_0x0c3b:
            r2 = r85
            if (r12 != r1) goto L_0x0CLASSNAME
            r1 = 1
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r1 = 0
        L_0x0CLASSNAME:
            if (r1 == 0) goto L_0x0c5f
            r4 = 2500(0x9c4, double:1.235E-320)
            int r52 = r3.dequeueInputBuffer(r4)     // Catch:{ Exception -> 0x0c5d, all -> 0x0c5b }
            if (r52 < 0) goto L_0x0c5f
            r53 = 0
            r54 = 0
            r55 = 0
            r57 = 4
            r51 = r3
            r51.queueInputBuffer(r52, r53, r54, r55, r57)     // Catch:{ Exception -> 0x0c5d, all -> 0x0c5b }
            r1 = 1
            goto L_0x0CLASSNAME
        L_0x0c5b:
            r0 = move-exception
            goto L_0x0CLASSNAME
        L_0x0c5d:
            r0 = move-exception
            goto L_0x0CLASSNAME
        L_0x0c5f:
            r1 = r2
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = move-exception
            r48 = r4
        L_0x0CLASSNAME:
            r12 = r77
            r8 = r78
            r11 = r79
            goto L_0x11c4
        L_0x0c6c:
            r0 = move-exception
            r48 = r4
            r86 = r13
        L_0x0CLASSNAME:
            r11 = r79
            r6 = r80
            r40 = r86
            r5 = r0
            r4 = r3
            r2 = r18
            r3 = r20
            r50 = r48
        L_0x0c7f:
            r1 = 0
            goto L_0x12e2
        L_0x0CLASSNAME:
            r87 = r1
            r59 = r2
            r48 = r4
            r85 = r6
            r86 = r13
            r46 = r14
            r13 = r68
            r14 = r7
            r1 = r85
        L_0x0CLASSNAME:
            r2 = r23 ^ 1
            r6 = r2
            r12 = r18
            r4 = r48
            r2 = 1
            r69 = r1
            r1 = r84
            r84 = r69
            r70 = r37
            r37 = r8
            r7 = r70
        L_0x0ca7:
            if (r6 != 0) goto L_0x0cc7
            if (r2 == 0) goto L_0x0cac
            goto L_0x0cc7
        L_0x0cac:
            r6 = r84
            r84 = r1
            r18 = r12
            r68 = r13
            r2 = r59
            r12 = r73
            r13 = r86
            r1 = r87
            r69 = r7
            r7 = r14
            r8 = r37
            r14 = r46
            r37 = r69
            goto L_0x0a72
        L_0x0cc7:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x11ab, all -> 0x119a }
            if (r91 == 0) goto L_0x0cd9
            r48 = 22000(0x55f0, double:1.08694E-319)
            r85 = r6
            r69 = r7
            r8 = r80
            r6 = r48
            r48 = r69
            goto L_0x0ce1
        L_0x0cd9:
            r85 = r6
            r48 = r7
            r6 = 2500(0x9c4, double:1.235E-320)
            r8 = r80
        L_0x0ce1:
            int r6 = r8.dequeueOutputBuffer(r13, r6)     // Catch:{ Exception -> 0x1196, all -> 0x119a }
            r7 = -1
            if (r6 != r7) goto L_0x0d00
            r52 = r1
            r50 = r4
            r38 = r11
            r11 = r46
            r4 = r47
            r2 = r64
            r7 = r65
            r1 = -1
            r5 = 0
        L_0x0cf8:
            r69 = r19
            r19 = r14
            r14 = r69
            goto L_0x0var_
        L_0x0d00:
            r7 = -3
            if (r6 != r7) goto L_0x0d36
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0d2b, all -> 0x0d21 }
            r80 = r2
            r2 = 21
            if (r7 >= r2) goto L_0x0d0f
            java.nio.ByteBuffer[] r26 = r8.getOutputBuffers()     // Catch:{ Exception -> 0x0d2b, all -> 0x0d21 }
        L_0x0d0f:
            r52 = r1
            r50 = r4
            r38 = r11
            r11 = r46
            r4 = r47
            r2 = r64
            r7 = r65
        L_0x0d1d:
            r1 = -1
            r5 = r80
            goto L_0x0cf8
        L_0x0d21:
            r0 = move-exception
            r8 = r78
            r11 = r79
            r1 = r0
            r50 = r4
            goto L_0x11a3
        L_0x0d2b:
            r0 = move-exception
            r11 = r79
            r40 = r86
            r50 = r4
            r6 = r8
            r2 = r12
            goto L_0x0ab7
        L_0x0d36:
            r80 = r2
            r2 = -2
            if (r6 != r2) goto L_0x0db9
            android.media.MediaFormat r2 = r8.getOutputFormat()     // Catch:{ Exception -> 0x0d2b, all -> 0x0d21 }
            r7 = -5
            if (r12 != r7) goto L_0x0da9
            if (r2 == 0) goto L_0x0da9
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ Exception -> 0x0d2b, all -> 0x0d21 }
            r38 = r11
            r11 = 0
            int r7 = r7.addTrack(r2, r11)     // Catch:{ Exception -> 0x0d2b, all -> 0x0d21 }
            r11 = r66
            boolean r12 = r2.containsKey(r11)     // Catch:{ Exception -> 0x0d9a, all -> 0x0d87 }
            if (r12 == 0) goto L_0x0d7c
            int r12 = r2.getInteger(r11)     // Catch:{ Exception -> 0x0d9a, all -> 0x0d87 }
            r50 = r7
            r7 = 1
            if (r12 != r7) goto L_0x0d7e
            r7 = r65
            java.nio.ByteBuffer r12 = r2.getByteBuffer(r7)     // Catch:{ Exception -> 0x0d7a, all -> 0x0d78 }
            r66 = r11
            r11 = r46
            java.nio.ByteBuffer r2 = r2.getByteBuffer(r11)     // Catch:{ Exception -> 0x0d7a, all -> 0x0d78 }
            int r12 = r12.limit()     // Catch:{ Exception -> 0x0d7a, all -> 0x0d78 }
            int r2 = r2.limit()     // Catch:{ Exception -> 0x0d7a, all -> 0x0d78 }
            int r12 = r12 + r2
            r28 = r12
            goto L_0x0d84
        L_0x0d78:
            r0 = move-exception
            goto L_0x0d8a
        L_0x0d7a:
            r0 = move-exception
            goto L_0x0d9d
        L_0x0d7c:
            r50 = r7
        L_0x0d7e:
            r66 = r11
            r11 = r46
            r7 = r65
        L_0x0d84:
            r12 = r50
            goto L_0x0daf
        L_0x0d87:
            r0 = move-exception
            r50 = r7
        L_0x0d8a:
            r12 = r77
            r8 = r78
            r11 = r79
            r1 = r0
            r9 = r20
            r2 = r50
            r6 = 0
            r50 = r4
            goto L_0x13cf
        L_0x0d9a:
            r0 = move-exception
            r50 = r7
        L_0x0d9d:
            r11 = r79
            r40 = r86
            r6 = r8
            r2 = r50
            r1 = 0
            r50 = r4
            goto L_0x123e
        L_0x0da9:
            r38 = r11
            r11 = r46
            r7 = r65
        L_0x0daf:
            r52 = r1
            r50 = r4
            r4 = r47
            r2 = r64
            goto L_0x0d1d
        L_0x0db9:
            r38 = r11
            r11 = r46
            r7 = r65
            if (r6 < 0) goto L_0x116e
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1196, all -> 0x119a }
            r19 = r14
            r14 = 21
            if (r2 >= r14) goto L_0x0dcc
            r2 = r26[r6]     // Catch:{ Exception -> 0x0d2b, all -> 0x0d21 }
            goto L_0x0dd0
        L_0x0dcc:
            java.nio.ByteBuffer r2 = r8.getOutputBuffer(r6)     // Catch:{ Exception -> 0x1196, all -> 0x119a }
        L_0x0dd0:
            if (r2 == 0) goto L_0x114b
            int r14 = r13.size     // Catch:{ Exception -> 0x1196, all -> 0x119a }
            r50 = r4
            r4 = 1
            if (r14 <= r4) goto L_0x0ef8
            int r4 = r13.flags     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r5 = r4 & 2
            if (r5 != 0) goto L_0x0e76
            if (r28 == 0) goto L_0x0def
            r5 = r4 & 1
            if (r5 == 0) goto L_0x0def
            int r5 = r13.offset     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            int r5 = r5 + r28
            r13.offset = r5     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            int r14 = r14 - r28
            r13.size = r14     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
        L_0x0def:
            if (r1 == 0) goto L_0x0e3d
            r4 = r4 & 1
            if (r4 == 0) goto L_0x0e3d
            int r1 = r13.size     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r4 = 100
            if (r1 <= r4) goto L_0x0e3c
            int r1 = r13.offset     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r2.position(r1)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            byte[] r1 = new byte[r4]     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r2.get(r1)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r5 = 0
            r14 = 0
        L_0x0e07:
            r4 = 96
            if (r5 >= r4) goto L_0x0e3c
            byte r4 = r1[r5]     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            if (r4 != 0) goto L_0x0e33
            int r4 = r5 + 1
            byte r4 = r1[r4]     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            if (r4 != 0) goto L_0x0e33
            int r4 = r5 + 2
            byte r4 = r1[r4]     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            if (r4 != 0) goto L_0x0e33
            int r4 = r5 + 3
            byte r4 = r1[r4]     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r46 = r1
            r1 = 1
            if (r4 != r1) goto L_0x0e35
            int r14 = r14 + 1
            if (r14 <= r1) goto L_0x0e35
            int r1 = r13.offset     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            int r1 = r1 + r5
            r13.offset = r1     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            int r1 = r13.size     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            int r1 = r1 - r5
            r13.size = r1     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            goto L_0x0e3c
        L_0x0e33:
            r46 = r1
        L_0x0e35:
            int r5 = r5 + 1
            r1 = r46
            r4 = 100
            goto L_0x0e07
        L_0x0e3c:
            r1 = 0
        L_0x0e3d:
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r14 = r1
            r5 = 1
            long r1 = r4.writeSampleData(r12, r2, r13, r5)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r4 = 0
            int r46 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r46 == 0) goto L_0x0e6a
            org.telegram.messenger.MediaController$VideoConvertorListener r4 = r15.callback     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            if (r4 == 0) goto L_0x0e6a
            r46 = r6
            long r5 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            long r52 = r5 - r9
            int r54 = (r52 > r33 ? 1 : (r52 == r33 ? 0 : -1))
            if (r54 <= 0) goto L_0x0e5b
            long r33 = r5 - r9
        L_0x0e5b:
            r5 = r33
            r52 = r14
            float r14 = (float) r5     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            float r14 = r14 / r21
            float r14 = r14 / r22
            r4.didWriteData(r1, r14)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r33 = r5
            goto L_0x0e6e
        L_0x0e6a:
            r46 = r6
            r52 = r14
        L_0x0e6e:
            r4 = r47
            r1 = r63
            r2 = r64
            goto L_0x0efe
        L_0x0e76:
            r46 = r6
            r4 = -5
            if (r12 != r4) goto L_0x0ef4
            byte[] r4 = new byte[r14]     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            int r5 = r13.offset     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            int r5 = r5 + r14
            r2.limit(r5)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            int r5 = r13.offset     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r2.position(r5)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r2.get(r4)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            int r2 = r13.size     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r6 = 1
            int r2 = r2 - r6
        L_0x0e8f:
            if (r2 < 0) goto L_0x0ed3
            r5 = 3
            if (r2 <= r5) goto L_0x0ed3
            byte r14 = r4[r2]     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            if (r14 != r6) goto L_0x0ecb
            int r14 = r2 + -1
            byte r14 = r4[r14]     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            if (r14 != 0) goto L_0x0ecb
            int r14 = r2 + -2
            byte r14 = r4[r14]     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            if (r14 != 0) goto L_0x0ecb
            int r14 = r2 + -3
            byte r39 = r4[r14]     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            if (r39 != 0) goto L_0x0ecb
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r14)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            int r5 = r13.size     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            int r5 = r5 - r14
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocate(r5)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r52 = r1
            r6 = 0
            java.nio.ByteBuffer r1 = r2.put(r4, r6, r14)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r1.position(r6)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            int r1 = r13.size     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            int r1 = r1 - r14
            java.nio.ByteBuffer r1 = r5.put(r4, r14, r1)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r1.position(r6)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r6 = r2
            goto L_0x0ed7
        L_0x0ecb:
            r52 = r1
            int r2 = r2 + -1
            r1 = r52
            r6 = 1
            goto L_0x0e8f
        L_0x0ed3:
            r52 = r1
            r5 = 0
            r6 = 0
        L_0x0ed7:
            r4 = r47
            r1 = r63
            r2 = r64
            android.media.MediaFormat r14 = android.media.MediaFormat.createVideoFormat(r4, r1, r2)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            if (r6 == 0) goto L_0x0eeb
            if (r5 == 0) goto L_0x0eeb
            r14.setByteBuffer(r7, r6)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r14.setByteBuffer(r11, r5)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
        L_0x0eeb:
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r6 = 0
            int r5 = r5.addTrack(r14, r6)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r12 = r5
            goto L_0x0efe
        L_0x0ef4:
            r52 = r1
            goto L_0x0e6e
        L_0x0ef8:
            r52 = r1
            r46 = r6
            goto L_0x0e6e
        L_0x0efe:
            int r5 = r13.flags     // Catch:{ Exception -> 0x1119, all -> 0x1114 }
            r5 = r5 & 4
            r6 = r46
            if (r5 == 0) goto L_0x0var_
            r5 = 1
            goto L_0x0var_
        L_0x0var_:
            r5 = 0
        L_0x0var_:
            r14 = 0
            r8.releaseOutputBuffer(r6, r14)     // Catch:{ Exception -> 0x1119, all -> 0x1114 }
            r63 = r1
            r14 = r5
            r1 = -1
            r5 = r80
        L_0x0var_:
            if (r6 == r1) goto L_0x0var_
            r6 = r85
            r64 = r2
            r47 = r4
            r2 = r5
            r65 = r7
            r80 = r8
            r46 = r11
            r11 = r38
            r7 = r48
            r4 = r50
            r1 = r52
        L_0x0f2a:
            r69 = r19
            r19 = r14
            r14 = r69
            goto L_0x0ca7
        L_0x0var_:
            if (r23 != 0) goto L_0x111e
            r64 = r2
            r1 = 2500(0x9c4, double:1.235E-320)
            int r6 = r3.dequeueOutputBuffer(r13, r1)     // Catch:{ Exception -> 0x1119, all -> 0x1114 }
            r1 = -1
            if (r6 != r1) goto L_0x0f4f
            r1 = r84
            r47 = r4
            r56 = r5
            r55 = r11
            r4 = r58
            r5 = 0
            r6 = 0
            r11 = r79
            goto L_0x1133
        L_0x0f4f:
            r2 = -3
            if (r6 != r2) goto L_0x0var_
            goto L_0x1120
        L_0x0var_:
            r2 = -2
            if (r6 != r2) goto L_0x0var_
            android.media.MediaFormat r2 = r3.getOutputFormat()     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            if (r6 == 0) goto L_0x1120
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r6.<init>()     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            java.lang.String r1 = "newFormat = "
            r6.append(r1)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r6.append(r2)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            java.lang.String r1 = r6.toString()     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            goto L_0x1120
        L_0x0var_:
            r0 = move-exception
            r8 = r78
            r11 = r79
            goto L_0x11a2
        L_0x0f7c:
            r0 = move-exception
            r11 = r79
        L_0x0f7f:
            r40 = r86
            r5 = r0
            r4 = r3
            r6 = r8
            r2 = r12
            r3 = r20
            goto L_0x0c7f
        L_0x0var_:
            if (r6 < 0) goto L_0x10f7
            int r1 = r13.size     // Catch:{ Exception -> 0x1119, all -> 0x1114 }
            if (r1 == 0) goto L_0x0var_
            r80 = 1
            goto L_0x0var_
        L_0x0var_:
            r80 = 0
        L_0x0var_:
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1119, all -> 0x1114 }
            r41 = 0
            int r46 = (r50 > r41 ? 1 : (r50 == r41 ? 0 : -1))
            if (r46 <= 0) goto L_0x0fb0
            int r46 = (r1 > r50 ? 1 : (r1 == r50 ? 0 : -1))
            if (r46 < 0) goto L_0x0fb0
            r47 = r4
            int r4 = r13.flags     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r4 = r4 | 4
            r13.flags = r4     // Catch:{ Exception -> 0x0f7c, all -> 0x0var_ }
            r4 = 1
            r23 = 1
            r41 = 0
            r46 = 0
            goto L_0x0fb8
        L_0x0fb0:
            r47 = r4
            r46 = r80
            r4 = r84
            r41 = 0
        L_0x0fb8:
            int r53 = (r31 > r41 ? 1 : (r31 == r41 ? 0 : -1))
            if (r53 < 0) goto L_0x1030
            r80 = r4
            int r4 = r13.flags     // Catch:{ Exception -> 0x0f7c, all -> 0x1114 }
            r4 = r4 & 4
            if (r4 == 0) goto L_0x1032
            long r53 = r31 - r9
            long r53 = java.lang.Math.abs(r53)     // Catch:{ Exception -> 0x0f7c, all -> 0x1114 }
            r4 = 1000000(0xvar_, float:1.401298E-39)
            r55 = r11
            r11 = r79
            int r4 = r4 / r11
            r56 = r5
            long r4 = (long) r4
            int r57 = (r53 > r4 ? 1 : (r53 == r4 ? 0 : -1))
            if (r57 <= 0) goto L_0x1027
            r4 = 0
            int r23 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r23 <= 0) goto L_0x0fe7
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x102d, all -> 0x118d }
            r5 = 0
            r4.seekTo(r9, r5)     // Catch:{ Exception -> 0x102d, all -> 0x118d }
            r5 = 0
            goto L_0x0fef
        L_0x0fe7:
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x102d, all -> 0x102a }
            r5 = 0
            r9 = 0
            r4.seekTo(r9, r5)     // Catch:{ Exception -> 0x102d, all -> 0x118d }
        L_0x0fef:
            long r35 = r48 + r24
            int r4 = r13.flags     // Catch:{ Exception -> 0x1017, all -> 0x1007 }
            r9 = -5
            r4 = r4 & r9
            r13.flags = r4     // Catch:{ Exception -> 0x1017, all -> 0x1007 }
            r3.flush()     // Catch:{ Exception -> 0x1017, all -> 0x1007 }
            r50 = r31
            r4 = 1
            r10 = 0
            r23 = 0
            r41 = 0
            r46 = 0
            r31 = r16
            goto L_0x103f
        L_0x1007:
            r0 = move-exception
            r8 = r78
            r1 = r0
            r2 = r12
            r9 = r20
            r50 = r31
            r6 = 0
            r12 = r77
            r31 = r16
            goto L_0x13cf
        L_0x1017:
            r0 = move-exception
            r40 = r86
            r5 = r0
            r4 = r3
            r6 = r8
            r2 = r12
            r3 = r20
            r50 = r31
            r1 = 0
            r31 = r16
            goto L_0x12e2
        L_0x1027:
            r5 = 0
            r9 = -5
            goto L_0x103a
        L_0x102a:
            r0 = move-exception
            goto L_0x119f
        L_0x102d:
            r0 = move-exception
            goto L_0x0f7f
        L_0x1030:
            r80 = r4
        L_0x1032:
            r56 = r5
            r55 = r11
            r5 = 0
            r9 = -5
            r11 = r79
        L_0x103a:
            r10 = r80
            r4 = 0
            r41 = 0
        L_0x103f:
            int r18 = (r31 > r41 ? 1 : (r31 == r41 ? 0 : -1))
            r80 = r10
            if (r18 < 0) goto L_0x1048
            r9 = r31
            goto L_0x104a
        L_0x1048:
            r9 = r82
        L_0x104a:
            int r43 = (r9 > r41 ? 1 : (r9 == r41 ? 0 : -1))
            if (r43 <= 0) goto L_0x1087
            int r43 = (r44 > r16 ? 1 : (r44 == r16 ? 0 : -1))
            if (r43 != 0) goto L_0x1087
            int r43 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r43 >= 0) goto L_0x107a
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x102d, all -> 0x118d }
            if (r1 == 0) goto L_0x1078
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x102d, all -> 0x118d }
            r1.<init>()     // Catch:{ Exception -> 0x102d, all -> 0x118d }
            java.lang.String r2 = "drop frame startTime = "
            r1.append(r2)     // Catch:{ Exception -> 0x102d, all -> 0x118d }
            r1.append(r9)     // Catch:{ Exception -> 0x102d, all -> 0x118d }
            java.lang.String r2 = " present time = "
            r1.append(r2)     // Catch:{ Exception -> 0x102d, all -> 0x118d }
            long r9 = r13.presentationTimeUs     // Catch:{ Exception -> 0x102d, all -> 0x118d }
            r1.append(r9)     // Catch:{ Exception -> 0x102d, all -> 0x118d }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x102d, all -> 0x118d }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x102d, all -> 0x118d }
        L_0x1078:
            r1 = 0
            goto L_0x1089
        L_0x107a:
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x102d, all -> 0x118d }
            r9 = -2147483648(0xfffffffvar_, double:NaN)
            int r43 = (r48 > r9 ? 1 : (r48 == r9 ? 0 : -1))
            if (r43 == 0) goto L_0x1085
            long r35 = r35 - r1
        L_0x1085:
            r44 = r1
        L_0x1087:
            r1 = r46
        L_0x1089:
            if (r4 == 0) goto L_0x108e
            r44 = r16
            goto L_0x10a1
        L_0x108e:
            int r2 = (r31 > r16 ? 1 : (r31 == r16 ? 0 : -1))
            if (r2 != 0) goto L_0x109e
            r9 = 0
            int r2 = (r35 > r9 ? 1 : (r35 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x109e
            long r9 = r13.presentationTimeUs     // Catch:{ Exception -> 0x102d, all -> 0x118d }
            long r9 = r9 + r35
            r13.presentationTimeUs = r9     // Catch:{ Exception -> 0x102d, all -> 0x118d }
        L_0x109e:
            r3.releaseOutputBuffer(r6, r1)     // Catch:{ Exception -> 0x10f4, all -> 0x118d }
        L_0x10a1:
            if (r1 == 0) goto L_0x10d3
            r1 = 0
            int r4 = (r31 > r1 ? 1 : (r31 == r1 ? 0 : -1))
            if (r4 < 0) goto L_0x10b2
            long r9 = r13.presentationTimeUs     // Catch:{ Exception -> 0x102d, all -> 0x118d }
            r1 = r48
            long r1 = java.lang.Math.max(r1, r9)     // Catch:{ Exception -> 0x102d, all -> 0x118d }
            goto L_0x10b4
        L_0x10b2:
            r1 = r48
        L_0x10b4:
            r27.awaitNewImage()     // Catch:{ Exception -> 0x10b9, all -> 0x118d }
            r4 = 0
            goto L_0x10bf
        L_0x10b9:
            r0 = move-exception
            r4 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ Exception -> 0x10f4, all -> 0x118d }
            r4 = 1
        L_0x10bf:
            if (r4 != 0) goto L_0x10d5
            r27.drawImage()     // Catch:{ Exception -> 0x10f4, all -> 0x118d }
            long r9 = r13.presentationTimeUs     // Catch:{ Exception -> 0x10f4, all -> 0x118d }
            r48 = 1000(0x3e8, double:4.94E-321)
            long r9 = r9 * r48
            r4 = r58
            r4.setPresentationTime(r9)     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            r4.swapBuffers()     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            goto L_0x10d7
        L_0x10d3:
            r1 = r48
        L_0x10d5:
            r4 = r58
        L_0x10d7:
            int r6 = r13.flags     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            r6 = r6 & 4
            if (r6 == 0) goto L_0x10ed
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            if (r6 == 0) goto L_0x10e6
            java.lang.String r6 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x118f, all -> 0x118d }
        L_0x10e6:
            r8.signalEndOfInputStream()     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            r48 = r1
            r6 = 0
            goto L_0x10f1
        L_0x10ed:
            r6 = r85
            r48 = r1
        L_0x10f1:
            r1 = r80
            goto L_0x1133
        L_0x10f4:
            r0 = move-exception
            goto L_0x11b2
        L_0x10f7:
            r11 = r79
            r4 = r58
            r5 = 0
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            r2.<init>()     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            java.lang.String r7 = "unexpected result from decoder.dequeueOutputBuffer: "
            r2.append(r7)     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            r2.append(r6)     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            r1.<init>(r2)     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            throw r1     // Catch:{ Exception -> 0x118f, all -> 0x118d }
        L_0x1114:
            r0 = move-exception
            r11 = r79
            goto L_0x119f
        L_0x1119:
            r0 = move-exception
            r11 = r79
            goto L_0x11b2
        L_0x111e:
            r64 = r2
        L_0x1120:
            r47 = r4
            r56 = r5
            r55 = r11
            r1 = r48
            r4 = r58
            r5 = 0
            r11 = r79
            r6 = r85
            r48 = r1
            r1 = r84
        L_0x1133:
            r9 = r82
            r84 = r1
            r58 = r4
            r65 = r7
            r80 = r8
            r11 = r38
            r7 = r48
            r4 = r50
            r1 = r52
            r46 = r55
            r2 = r56
            goto L_0x0f2a
        L_0x114b:
            r11 = r79
            r50 = r4
            r4 = r58
            r5 = 0
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            r2.<init>()     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            java.lang.String r7 = "encoderOutputBuffer "
            r2.append(r7)     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            r2.append(r6)     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            java.lang.String r6 = " was null"
            r2.append(r6)     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            r1.<init>(r2)     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            throw r1     // Catch:{ Exception -> 0x118f, all -> 0x118d }
        L_0x116e:
            r11 = r79
            r50 = r4
            r4 = r58
            r5 = 0
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            r2.<init>()     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            java.lang.String r7 = "unexpected result from encoder.dequeueOutputBuffer: "
            r2.append(r7)     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            r2.append(r6)     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            r1.<init>(r2)     // Catch:{ Exception -> 0x118f, all -> 0x118d }
            throw r1     // Catch:{ Exception -> 0x118f, all -> 0x118d }
        L_0x118d:
            r0 = move-exception
            goto L_0x11a0
        L_0x118f:
            r0 = move-exception
            r40 = r86
            r5 = r0
            r58 = r4
            goto L_0x11b7
        L_0x1196:
            r0 = move-exception
            r11 = r79
            goto L_0x11b0
        L_0x119a:
            r0 = move-exception
            r11 = r79
            r50 = r4
        L_0x119f:
            r5 = 0
        L_0x11a0:
            r8 = r78
        L_0x11a2:
            r1 = r0
        L_0x11a3:
            r2 = r12
            r9 = r20
            r6 = 0
            r12 = r77
            goto L_0x13cf
        L_0x11ab:
            r0 = move-exception
            r11 = r79
            r8 = r80
        L_0x11b0:
            r50 = r4
        L_0x11b2:
            r4 = r58
            r40 = r86
            r5 = r0
        L_0x11b7:
            r6 = r8
            r2 = r12
            goto L_0x11e0
        L_0x11ba:
            r0 = move-exception
            r11 = r79
            r48 = r4
            r5 = 0
            r12 = r77
            r8 = r78
        L_0x11c4:
            r1 = r0
            r2 = r18
            r9 = r20
            r50 = r48
            goto L_0x05dd
        L_0x11cd:
            r0 = move-exception
            r11 = r79
            r8 = r80
            r48 = r4
            r86 = r13
            r4 = r58
            r40 = r86
            r5 = r0
            r6 = r8
            r2 = r18
            r50 = r48
        L_0x11e0:
            r1 = 0
            goto L_0x123f
        L_0x11e3:
            r0 = move-exception
            r11 = r79
            goto L_0x1277
        L_0x11e8:
            r0 = move-exception
            r11 = r79
            r8 = r80
            r86 = r13
            r2 = r58
            r1 = 0
            r40 = r86
            r50 = r4
            r6 = r8
        L_0x11f7:
            r2 = -5
            goto L_0x123e
        L_0x11f9:
            r0 = move-exception
            r11 = r79
            r8 = r80
            r4 = r84
            r2 = r58
            goto L_0x120e
        L_0x1203:
            r0 = move-exception
            r11 = r79
            r8 = r80
            r4 = r84
            r2 = r58
            r3 = r60
        L_0x120e:
            r1 = 0
            r50 = r4
            r6 = r8
        L_0x1212:
            r2 = -5
            goto L_0x123c
        L_0x1214:
            r0 = move-exception
            r11 = r79
            r4 = r84
            goto L_0x1277
        L_0x121b:
            r0 = move-exception
            r11 = r79
            r8 = r80
            r4 = r84
            r2 = r58
            r3 = r60
            r1 = 0
            goto L_0x1236
        L_0x1228:
            r0 = move-exception
            r3 = r2
            r31 = r7
            r11 = r14
            r61 = r24
            r2 = r1
            r8 = r4
            r1 = 0
            r4 = r84
            r58 = r2
        L_0x1236:
            r50 = r4
            r6 = r8
            r2 = -5
            r27 = 0
        L_0x123c:
            r40 = 0
        L_0x123e:
            r5 = r0
        L_0x123f:
            r4 = r3
            r3 = r20
            goto L_0x12e2
        L_0x1244:
            r0 = move-exception
            r2 = r1
            r31 = r7
            r11 = r14
            r61 = r24
            r1 = 0
            r8 = r4
            r4 = r84
            r58 = r2
            r50 = r4
            r6 = r8
            r3 = r20
            r2 = -5
            r4 = 0
            r27 = 0
            r40 = 0
            goto L_0x12e1
        L_0x125e:
            r0 = move-exception
            r31 = r7
            r11 = r14
            r61 = r24
            r1 = 0
            r8 = r4
            r4 = r84
            r50 = r4
            r6 = r8
            r3 = r20
            r2 = -5
            r4 = 0
            goto L_0x12db
        L_0x1271:
            r0 = move-exception
            r4 = r84
            r31 = r7
            r11 = r14
        L_0x1277:
            r1 = 0
            r12 = r77
            r8 = r78
        L_0x127c:
            r1 = r0
            r50 = r4
        L_0x127f:
            r9 = r20
            goto L_0x05dc
        L_0x1283:
            r0 = move-exception
            r4 = r84
            r31 = r7
            r11 = r14
            r61 = r24
            r1 = 0
            r50 = r4
            r3 = r20
            goto L_0x12d8
        L_0x1291:
            r0 = move-exception
            r4 = r84
            r31 = r7
            r11 = r14
            goto L_0x12a8
        L_0x1298:
            r0 = move-exception
            r4 = r84
            r31 = r7
            r11 = r14
            r61 = r24
            goto L_0x12d6
        L_0x12a1:
            r0 = move-exception
            r4 = r84
            r31 = r7
            r11 = r14
            r1 = 0
        L_0x12a8:
            r12 = r77
            r8 = r78
            r1 = r0
            r9 = r3
            goto L_0x12c7
        L_0x12af:
            r0 = move-exception
            r4 = r84
            r31 = r7
            r11 = r14
            r61 = r24
            r1 = 0
            goto L_0x12d6
        L_0x12b9:
            r0 = move-exception
            r4 = r84
            r11 = r14
            r1 = 0
            r12 = r77
            r8 = r78
            r9 = r80
            r31 = r86
            r1 = r0
        L_0x12c7:
            r50 = r4
            goto L_0x05dc
        L_0x12cb:
            r0 = move-exception
            r61 = r4
            r11 = r14
            r1 = 0
            r4 = r84
            r3 = r80
            r31 = r86
        L_0x12d6:
            r50 = r4
        L_0x12d8:
            r2 = -5
            r4 = 0
            r6 = 0
        L_0x12db:
            r27 = 0
            r40 = 0
            r58 = 0
        L_0x12e1:
            r5 = r0
        L_0x12e2:
            boolean r7 = r5 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x1352 }
            if (r7 == 0) goto L_0x12e9
            if (r91 != 0) goto L_0x12e9
            r1 = 1
        L_0x12e9:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x1349 }
            r7.<init>()     // Catch:{ all -> 0x1349 }
            java.lang.String r8 = "bitrate: "
            r7.append(r8)     // Catch:{ all -> 0x1349 }
            r7.append(r3)     // Catch:{ all -> 0x1349 }
            java.lang.String r8 = " framerate: "
            r7.append(r8)     // Catch:{ all -> 0x1349 }
            r7.append(r11)     // Catch:{ all -> 0x1349 }
            java.lang.String r8 = " size: "
            r7.append(r8)     // Catch:{ all -> 0x1349 }
            r8 = r78
            r7.append(r8)     // Catch:{ all -> 0x1345 }
            java.lang.String r9 = "x"
            r7.append(r9)     // Catch:{ all -> 0x1345 }
            r9 = r77
            r7.append(r9)     // Catch:{ all -> 0x1343 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x1343 }
            org.telegram.messenger.FileLog.e((java.lang.String) r7)     // Catch:{ all -> 0x1343 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ all -> 0x1343 }
            r18 = r2
            r2 = r4
            r4 = r6
            r6 = r1
            r1 = 1
        L_0x1323:
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ all -> 0x133d }
            r7 = r61
            r5.unselectTrack(r7)     // Catch:{ all -> 0x133d }
            if (r2 == 0) goto L_0x1332
            r2.stop()     // Catch:{ all -> 0x133d }
            r2.release()     // Catch:{ all -> 0x133d }
        L_0x1332:
            r5 = r1
            r7 = r6
            r2 = r18
            r6 = r27
            r1 = r40
            r40 = r58
            goto L_0x1371
        L_0x133d:
            r0 = move-exception
            r1 = r0
            r12 = r9
            r2 = r18
            goto L_0x137b
        L_0x1343:
            r0 = move-exception
            goto L_0x134e
        L_0x1345:
            r0 = move-exception
            r9 = r77
            goto L_0x134e
        L_0x1349:
            r0 = move-exception
            r9 = r77
            r8 = r78
        L_0x134e:
            r6 = r1
            r12 = r9
            r1 = r0
            goto L_0x137b
        L_0x1352:
            r0 = move-exception
            r9 = r77
            r8 = r78
            r1 = r0
            r12 = r9
            r6 = 0
            goto L_0x137b
        L_0x135b:
            r9 = r77
            r8 = r78
            r4 = r84
            r11 = r14
            r1 = 0
            r3 = r80
            r31 = r86
            r50 = r4
            r1 = 0
            r2 = -5
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r40 = 0
        L_0x1371:
            if (r6 == 0) goto L_0x137d
            r6.release()     // Catch:{ all -> 0x1377 }
            goto L_0x137d
        L_0x1377:
            r0 = move-exception
            r1 = r0
            r6 = r7
            r12 = r9
        L_0x137b:
            r9 = r3
            goto L_0x13cf
        L_0x137d:
            if (r40 == 0) goto L_0x1382
            r40.release()     // Catch:{ all -> 0x1377 }
        L_0x1382:
            if (r4 == 0) goto L_0x138a
            r4.stop()     // Catch:{ all -> 0x1377 }
            r4.release()     // Catch:{ all -> 0x1377 }
        L_0x138a:
            if (r1 == 0) goto L_0x138f
            r1.release()     // Catch:{ all -> 0x1377 }
        L_0x138f:
            r72.checkConversionCanceled()     // Catch:{ all -> 0x1377 }
            r1 = r5
            r6 = r7
        L_0x1394:
            android.media.MediaExtractor r4 = r15.extractor
            if (r4 == 0) goto L_0x139b
            r4.release()
        L_0x139b:
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer
            if (r4 == 0) goto L_0x13b0
            r4.finishMovie()     // Catch:{ all -> 0x13ab }
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ all -> 0x13ab }
            long r4 = r4.getLastFrameTimestamp(r2)     // Catch:{ all -> 0x13ab }
            r15.endPresentationTime = r4     // Catch:{ all -> 0x13ab }
            goto L_0x13b0
        L_0x13ab:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x13b0:
            r10 = r3
            r7 = r8
            r13 = r50
            goto L_0x1421
        L_0x13b6:
            r0 = move-exception
            r8 = r78
            r4 = r84
            r11 = r10
            r9 = r12
            r1 = 0
            goto L_0x13c6
        L_0x13bf:
            r0 = move-exception
            r4 = r84
            r8 = r11
            r9 = r12
            r1 = 0
            r11 = r10
        L_0x13c6:
            r31 = r86
            r1 = r0
            r50 = r4
            r2 = -5
            r6 = 0
            r9 = r80
        L_0x13cf:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x144c }
            r3.<init>()     // Catch:{ all -> 0x144c }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x144c }
            r3.append(r9)     // Catch:{ all -> 0x144c }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x144c }
            r3.append(r11)     // Catch:{ all -> 0x144c }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x144c }
            r3.append(r8)     // Catch:{ all -> 0x144c }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x144c }
            r3.append(r12)     // Catch:{ all -> 0x144c }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x144c }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x144c }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x144c }
            android.media.MediaExtractor r1 = r15.extractor
            if (r1 == 0) goto L_0x1406
            r1.release()
        L_0x1406:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer
            if (r1 == 0) goto L_0x141b
            r1.finishMovie()     // Catch:{ all -> 0x1416 }
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ all -> 0x1416 }
            long r1 = r1.getLastFrameTimestamp(r2)     // Catch:{ all -> 0x1416 }
            r15.endPresentationTime = r1     // Catch:{ all -> 0x1416 }
            goto L_0x141b
        L_0x1416:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x141b:
            r7 = r8
            r10 = r9
            r9 = r12
            r13 = r50
            r1 = 1
        L_0x1421:
            if (r6 == 0) goto L_0x144b
            r20 = 1
            r1 = r72
            r2 = r73
            r3 = r74
            r4 = r75
            r5 = r76
            r6 = r9
            r8 = r79
            r9 = r10
            r10 = r81
            r11 = r82
            r15 = r31
            r17 = r88
            r19 = r90
            r21 = r92
            r22 = r93
            r23 = r94
            r24 = r95
            r25 = r96
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r13, r15, r17, r19, r20, r21, r22, r23, r24, r25)
        L_0x144b:
            return r1
        L_0x144c:
            r0 = move-exception
            r1 = r72
            r3 = r0
            android.media.MediaExtractor r4 = r1.extractor
            if (r4 == 0) goto L_0x1457
            r4.release()
        L_0x1457:
            org.telegram.messenger.video.MP4Builder r4 = r1.mediaMuxer
            if (r4 == 0) goto L_0x146c
            r4.finishMovie()     // Catch:{ all -> 0x1467 }
            org.telegram.messenger.video.MP4Builder r4 = r1.mediaMuxer     // Catch:{ all -> 0x1467 }
            long r4 = r4.getLastFrameTimestamp(r2)     // Catch:{ all -> 0x1467 }
            r1.endPresentationTime = r4     // Catch:{ all -> 0x1467 }
            goto L_0x146c
        L_0x1467:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x146c:
            goto L_0x146e
        L_0x146d:
            throw r3
        L_0x146e:
            goto L_0x146d
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, int, long, long, long, long, boolean, boolean, org.telegram.messenger.MediaController$SavedFilterState, java.lang.String, java.util.ArrayList, boolean, org.telegram.messenger.MediaController$CropState):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0123, code lost:
        if (r9[r6 + 3] != 1) goto L_0x012b;
     */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x01d0  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x01d5  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x01ec  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x01ee  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00df  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long readAndWriteTracks(android.media.MediaExtractor r29, org.telegram.messenger.video.MP4Builder r30, android.media.MediaCodec.BufferInfo r31, long r32, long r34, long r36, java.io.File r38, boolean r39) throws java.lang.Exception {
        /*
            r28 = this;
            r1 = r29
            r2 = r30
            r3 = r31
            r4 = r32
            r6 = 0
            int r7 = org.telegram.messenger.MediaController.findTrack(r1, r6)
            r9 = 1
            if (r39 == 0) goto L_0x0018
            int r0 = org.telegram.messenger.MediaController.findTrack(r1, r9)
            r11 = r36
            r10 = r0
            goto L_0x001b
        L_0x0018:
            r11 = r36
            r10 = -1
        L_0x001b:
            float r0 = (float) r11
            r11 = 1148846080(0x447a0000, float:1000.0)
            float r12 = r0 / r11
            java.lang.String r13 = "max-input-size"
            r14 = 0
            if (r7 < 0) goto L_0x004a
            r1.selectTrack(r7)
            android.media.MediaFormat r0 = r1.getTrackFormat(r7)
            int r16 = r2.addTrack(r0, r6)
            int r0 = r0.getInteger(r13)     // Catch:{ Exception -> 0x0036 }
            goto L_0x003d
        L_0x0036:
            r0 = move-exception
            r17 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r17)
            r0 = 0
        L_0x003d:
            int r17 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r17 <= 0) goto L_0x0045
            r1.seekTo(r4, r6)
            goto L_0x0048
        L_0x0045:
            r1.seekTo(r14, r6)
        L_0x0048:
            r11 = r0
            goto L_0x004d
        L_0x004a:
            r11 = 0
            r16 = -1
        L_0x004d:
            if (r10 < 0) goto L_0x0086
            r1.selectTrack(r10)
            android.media.MediaFormat r0 = r1.getTrackFormat(r10)
            java.lang.String r8 = "mime"
            java.lang.String r8 = r0.getString(r8)
            java.lang.String r6 = "audio/unknown"
            boolean r6 = r8.equals(r6)
            if (r6 == 0) goto L_0x0067
            r6 = -1
            r10 = -1
            goto L_0x0087
        L_0x0067:
            int r6 = r2.addTrack(r0, r9)
            int r0 = r0.getInteger(r13)     // Catch:{ Exception -> 0x0074 }
            int r11 = java.lang.Math.max(r0, r11)     // Catch:{ Exception -> 0x0074 }
            goto L_0x0078
        L_0x0074:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0078:
            int r0 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x0081
            r8 = 0
            r1.seekTo(r4, r8)
            goto L_0x0087
        L_0x0081:
            r8 = 0
            r1.seekTo(r14, r8)
            goto L_0x0087
        L_0x0086:
            r6 = -1
        L_0x0087:
            if (r11 > 0) goto L_0x008b
            r11 = 65536(0x10000, float:9.18355E-41)
        L_0x008b:
            java.nio.ByteBuffer r0 = java.nio.ByteBuffer.allocateDirect(r11)
            r18 = -1
            if (r10 >= 0) goto L_0x0097
            if (r7 < 0) goto L_0x0096
            goto L_0x0097
        L_0x0096:
            return r18
        L_0x0097:
            r28.checkConversionCanceled()
            r22 = r14
            r20 = r18
            r8 = 0
        L_0x009f:
            if (r8 != 0) goto L_0x01fb
            r28.checkConversionCanceled()
            int r13 = android.os.Build.VERSION.SDK_INT
            r14 = 28
            if (r13 < r14) goto L_0x00c0
            long r14 = r29.getSampleSize()
            r37 = r10
            long r9 = (long) r11
            int r24 = (r14 > r9 ? 1 : (r14 == r9 ? 0 : -1))
            if (r24 <= 0) goto L_0x00c2
            r9 = 1024(0x400, double:5.06E-321)
            long r14 = r14 + r9
            int r0 = (int) r14
            java.nio.ByteBuffer r9 = java.nio.ByteBuffer.allocateDirect(r0)
            r11 = r0
            r0 = r9
            goto L_0x00c2
        L_0x00c0:
            r37 = r10
        L_0x00c2:
            r9 = 0
            int r10 = r1.readSampleData(r0, r9)
            r3.size = r10
            int r10 = r29.getSampleTrackIndex()
            if (r10 != r7) goto L_0x00d5
            r14 = r37
            r15 = r16
        L_0x00d3:
            r9 = -1
            goto L_0x00dd
        L_0x00d5:
            r14 = r37
            if (r10 != r14) goto L_0x00db
            r15 = r6
            goto L_0x00d3
        L_0x00db:
            r9 = -1
            r15 = -1
        L_0x00dd:
            if (r15 == r9) goto L_0x01d5
            r9 = 21
            if (r13 >= r9) goto L_0x00ec
            r9 = 0
            r0.position(r9)
            int r9 = r3.size
            r0.limit(r9)
        L_0x00ec:
            if (r10 == r14) goto L_0x015b
            byte[] r9 = r0.array()
            if (r9 == 0) goto L_0x015b
            int r13 = r0.arrayOffset()
            int r24 = r0.limit()
            int r24 = r13 + r24
            r37 = r6
            r6 = r13
            r13 = -1
        L_0x0102:
            r25 = 4
            r39 = r8
            int r8 = r24 + -4
            if (r6 > r8) goto L_0x015f
            byte r26 = r9[r6]
            if (r26 != 0) goto L_0x0126
            int r26 = r6 + 1
            byte r26 = r9[r26]
            if (r26 != 0) goto L_0x0126
            int r26 = r6 + 2
            byte r26 = r9[r26]
            if (r26 != 0) goto L_0x0126
            int r26 = r6 + 3
            r27 = r11
            byte r11 = r9[r26]
            r26 = r14
            r14 = 1
            if (r11 == r14) goto L_0x012d
            goto L_0x012b
        L_0x0126:
            r27 = r11
            r26 = r14
            r14 = 1
        L_0x012b:
            if (r6 != r8) goto L_0x0152
        L_0x012d:
            r11 = -1
            if (r13 == r11) goto L_0x0151
            int r11 = r6 - r13
            if (r6 == r8) goto L_0x0135
            goto L_0x0137
        L_0x0135:
            r25 = 0
        L_0x0137:
            int r11 = r11 - r25
            int r8 = r11 >> 24
            byte r8 = (byte) r8
            r9[r13] = r8
            int r8 = r13 + 1
            int r14 = r11 >> 16
            byte r14 = (byte) r14
            r9[r8] = r14
            int r8 = r13 + 2
            int r14 = r11 >> 8
            byte r14 = (byte) r14
            r9[r8] = r14
            int r13 = r13 + 3
            byte r8 = (byte) r11
            r9[r13] = r8
        L_0x0151:
            r13 = r6
        L_0x0152:
            int r6 = r6 + 1
            r8 = r39
            r14 = r26
            r11 = r27
            goto L_0x0102
        L_0x015b:
            r37 = r6
            r39 = r8
        L_0x015f:
            r27 = r11
            r26 = r14
            int r6 = r3.size
            if (r6 < 0) goto L_0x016f
            long r8 = r29.getSampleTime()
            r3.presentationTimeUs = r8
            r8 = 0
            goto L_0x0173
        L_0x016f:
            r6 = 0
            r3.size = r6
            r8 = 1
        L_0x0173:
            int r6 = r3.size
            if (r6 <= 0) goto L_0x0198
            if (r8 != 0) goto L_0x0198
            if (r10 != r7) goto L_0x018a
            r9 = 0
            int r6 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r6 <= 0) goto L_0x018c
            int r6 = (r20 > r18 ? 1 : (r20 == r18 ? 0 : -1))
            if (r6 != 0) goto L_0x018c
            long r13 = r3.presentationTimeUs
            r20 = r13
            goto L_0x018c
        L_0x018a:
            r9 = 0
        L_0x018c:
            int r6 = (r34 > r9 ? 1 : (r34 == r9 ? 0 : -1))
            if (r6 < 0) goto L_0x019d
            long r9 = r3.presentationTimeUs
            int r6 = (r9 > r34 ? 1 : (r9 == r34 ? 0 : -1))
            if (r6 >= 0) goto L_0x0197
            goto L_0x019d
        L_0x0197:
            r8 = 1
        L_0x0198:
            r11 = r28
        L_0x019a:
            r24 = 1148846080(0x447a0000, float:1000.0)
            goto L_0x01ce
        L_0x019d:
            r6 = 0
            r3.offset = r6
            int r9 = r29.getSampleFlags()
            r3.flags = r9
            long r9 = r2.writeSampleData(r15, r0, r3, r6)
            r13 = 0
            int r11 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r11 == 0) goto L_0x0198
            r11 = r28
            org.telegram.messenger.MediaController$VideoConvertorListener r15 = r11.callback
            if (r15 == 0) goto L_0x019a
            long r13 = r3.presentationTimeUs
            long r24 = r13 - r20
            int r17 = (r24 > r22 ? 1 : (r24 == r22 ? 0 : -1))
            if (r17 <= 0) goto L_0x01c1
            long r13 = r13 - r20
            goto L_0x01c3
        L_0x01c1:
            r13 = r22
        L_0x01c3:
            float r6 = (float) r13
            r24 = 1148846080(0x447a0000, float:1000.0)
            float r6 = r6 / r24
            float r6 = r6 / r12
            r15.didWriteData(r9, r6)
            r22 = r13
        L_0x01ce:
            if (r8 != 0) goto L_0x01d3
            r29.advance()
        L_0x01d3:
            r6 = -1
            goto L_0x01ea
        L_0x01d5:
            r37 = r6
            r39 = r8
            r27 = r11
            r26 = r14
            r6 = -1
            r24 = 1148846080(0x447a0000, float:1000.0)
            r11 = r28
            if (r10 != r6) goto L_0x01e6
            r8 = 1
            goto L_0x01ea
        L_0x01e6:
            r29.advance()
            r8 = 0
        L_0x01ea:
            if (r8 == 0) goto L_0x01ee
            r8 = 1
            goto L_0x01f0
        L_0x01ee:
            r8 = r39
        L_0x01f0:
            r6 = r37
            r10 = r26
            r11 = r27
            r9 = 1
            r14 = 0
            goto L_0x009f
        L_0x01fb:
            r11 = r28
            r26 = r10
            if (r7 < 0) goto L_0x0204
            r1.unselectTrack(r7)
        L_0x0204:
            if (r26 < 0) goto L_0x020b
            r10 = r26
            r1.unselectTrack(r10)
        L_0x020b:
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
