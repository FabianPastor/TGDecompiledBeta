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
    /* JADX WARNING: Code restructure failed: missing block: B:1023:0x1188, code lost:
        r0 = th;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1026:0x1191, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1027:0x1192, code lost:
        r11 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1028:0x1195, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1029:0x1196, code lost:
        r11 = r79;
        r50 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1050:0x11f4, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1051:0x11f5, code lost:
        r11 = r79;
        r8 = r80;
        r4 = r84;
        r2 = r58;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1052:0x11fe, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1053:0x11ff, code lost:
        r11 = r79;
        r8 = r80;
        r4 = r84;
        r2 = r58;
        r3 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1054:0x1209, code lost:
        r1 = false;
        r50 = r4;
        r6 = r8;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1056:0x120f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1057:0x1210, code lost:
        r11 = r79;
        r4 = r84;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1060:0x1223, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1061:0x1224, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:1066:0x123f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1067:0x1240, code lost:
        r2 = r1;
        r31 = r7;
        r11 = r14;
        r61 = r24;
        r1 = false;
        r58 = r2;
        r50 = r84;
        r6 = r4;
        r3 = r20;
        r2 = -5;
        r4 = null;
        r27 = null;
        r40 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1070:0x126c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1071:0x126d, code lost:
        r4 = r84;
        r31 = r7;
        r11 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1075:0x127e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1076:0x127f, code lost:
        r31 = r7;
        r11 = r14;
        r61 = r24;
        r1 = false;
        r50 = r84;
        r3 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1158:0x13fc, code lost:
        r1.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1162:?, code lost:
        r1.finishMovie();
        r15.endPresentationTime = r15.mediaMuxer.getLastFrameTimestamp(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1163:0x140f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1164:0x1410, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x034e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:181:0x0366, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:0x0367, code lost:
        r20 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x03a5, code lost:
        r2 = java.nio.ByteBuffer.allocate(r3);
        r7 = java.nio.ByteBuffer.allocate(r14.size - r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x03b0, code lost:
        r20 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:?, code lost:
        r2.put(r9, 0, r3).position(0);
        r7.put(r9, r3, r14.size - r3).position(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x04f3, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x04f4, code lost:
        r20 = r3;
        r10 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0534, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x053b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x053d, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x053e, code lost:
        r20 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x0581, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x0582, code lost:
        r3 = r36;
        r6 = r77;
        r11 = r49;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x058b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x058c, code lost:
        r11 = r79;
        r50 = r84;
        r31 = r86;
        r1 = r0;
        r9 = r48;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x05ac, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x05ad, code lost:
        r1 = r6;
        r48 = r9;
        r49 = r11;
        r2 = -5;
        r16 = null;
        r36 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x05b9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x05ba, code lost:
        r48 = r9;
        r49 = r11;
        r11 = r79;
        r50 = r84;
        r31 = r86;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x05c5, code lost:
        r8 = r49;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:311:0x05c8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x05c9, code lost:
        r48 = r9;
        r49 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x05f0, code lost:
        r43 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:426:0x07bf, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x07c0, code lost:
        r12 = r77;
        r50 = r84;
        r1 = r0;
        r9 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x07f2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x07f3, code lost:
        r50 = r84;
        r5 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x0923, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x092f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x0930, code lost:
        r11 = r79;
        r6 = r80;
        r50 = r84;
        r5 = r0;
        r40 = null;
        r1 = false;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x09ea, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x0a0e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x0a0f, code lost:
        r4 = r84;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x0a1c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0a99, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0a9a, code lost:
        r12 = r77;
        r8 = r78;
        r11 = r79;
        r1 = r0;
        r50 = r4;
        r2 = r18;
        r9 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x0aa9, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0aaa, code lost:
        r11 = r79;
        r6 = r80;
        r50 = r4;
        r40 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x0ab2, code lost:
        r2 = r18;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x0ab4, code lost:
        r1 = false;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x01ad, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x01ae, code lost:
        r6 = r77;
        r1 = r0;
        r11 = r49;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x0b2d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x0b2e, code lost:
        r11 = r79;
        r40 = r86;
        r50 = r4;
        r61 = r6;
        r2 = r18;
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0b67, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0b69, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0b6a, code lost:
        r61 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0b6c, code lost:
        r11 = r79;
        r6 = r80;
        r40 = r86;
        r50 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:685:0x0bbc, code lost:
        if (r13.presentationTimeUs < r4) goto L_0x0bc1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0bf8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0bf9, code lost:
        r48 = r48;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0c1a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0c1b, code lost:
        r48 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0c1e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0c1f, code lost:
        r48 = r4;
        r61 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0CLASSNAME, code lost:
        r0 = th;
        r48 = r48;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0c5e, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0c5f, code lost:
        r48 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x0CLASSNAME, code lost:
        r12 = r77;
        r8 = r78;
        r11 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0CLASSNAME, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0c6a, code lost:
        r48 = r4;
        r86 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x01d5, code lost:
        r1 = r2;
        r2 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r6;
        r6 = r7;
        r7 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x0var_, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x0var_, code lost:
        r8 = r78;
        r11 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x102a, code lost:
        r0 = e;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:989:0x10b6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x10b8, code lost:
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:992:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r4 = true;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:423:0x07b0, B:434:0x07e5] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1002:0x10da A[Catch:{ Exception -> 0x118a, all -> 0x1188 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1006:0x10ea A[Catch:{ Exception -> 0x118a, all -> 0x1188 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1023:0x1188 A[ExcHandler: all (th java.lang.Throwable), PHI: r11 r12 r31 r50 
      PHI: (r11v64 int) = (r11v228 int), (r11v230 int), (r11v231 int), (r11v232 int), (r11v234 int), (r11v235 int), (r11v237 int), (r11v239 int), (r11v252 int), (r11v254 int), (r11v256 int), (r11v258 int), (r11v260 java.lang.String) binds: [B:986:0x10b1, B:996:0x10c9, B:991:0x10b8, B:987:?, B:983:0x10a6, B:978:0x109b, B:979:?, B:961:0x1053, B:935:0x0fe9, B:936:?, B:929:0x0fdc, B:930:?, B:872:0x0efb] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r12v26 int) = (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v32 int), (r12v34 int) binds: [B:986:0x10b1, B:996:0x10c9, B:991:0x10b8, B:987:?, B:983:0x10a6, B:978:0x109b, B:979:?, B:961:0x1053, B:935:0x0fe9, B:936:?, B:929:0x0fdc, B:930:?, B:872:0x0efb] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r31v42 long) = (r31v45 long), (r31v45 long), (r31v45 long), (r31v45 long), (r31v45 long), (r31v45 long), (r31v45 long), (r31v45 long), (r31v35 long), (r31v35 long), (r31v35 long), (r31v35 long), (r31v35 long) binds: [B:986:0x10b1, B:996:0x10c9, B:991:0x10b8, B:987:?, B:983:0x10a6, B:978:0x109b, B:979:?, B:961:0x1053, B:935:0x0fe9, B:936:?, B:929:0x0fdc, B:930:?, B:872:0x0efb] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r50v38 long) = (r50v46 long), (r50v46 long), (r50v46 long), (r50v46 long), (r50v46 long), (r50v46 long), (r50v46 long), (r50v46 long), (r50v44 long), (r50v44 long), (r50v44 long), (r50v44 long), (r50v53 long) binds: [B:986:0x10b1, B:996:0x10c9, B:991:0x10b8, B:987:?, B:983:0x10a6, B:978:0x109b, B:979:?, B:961:0x1053, B:935:0x0fe9, B:936:?, B:929:0x0fdc, B:930:?, B:872:0x0efb] A[DONT_GENERATE, DONT_INLINE], Splitter:B:986:0x10b1] */
    /* JADX WARNING: Removed duplicated region for block: B:1028:0x1195 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:741:0x0cc4] */
    /* JADX WARNING: Removed duplicated region for block: B:1056:0x120f A[ExcHandler: all (th java.lang.Throwable), Splitter:B:493:0x08fa] */
    /* JADX WARNING: Removed duplicated region for block: B:1070:0x126c A[ExcHandler: all (th java.lang.Throwable), Splitter:B:483:0x0898] */
    /* JADX WARNING: Removed duplicated region for block: B:1112:0x1326 A[Catch:{ all -> 0x1337 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1124:0x1355  */
    /* JADX WARNING: Removed duplicated region for block: B:1126:0x136d A[SYNTHETIC, Splitter:B:1126:0x136d] */
    /* JADX WARNING: Removed duplicated region for block: B:1132:0x1379 A[Catch:{ all -> 0x1371 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1134:0x137e A[Catch:{ all -> 0x1371 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1136:0x1386 A[Catch:{ all -> 0x1371 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1141:0x1392  */
    /* JADX WARNING: Removed duplicated region for block: B:1144:0x1399 A[SYNTHETIC, Splitter:B:1144:0x1399] */
    /* JADX WARNING: Removed duplicated region for block: B:1158:0x13fc  */
    /* JADX WARNING: Removed duplicated region for block: B:1161:0x1403 A[SYNTHETIC, Splitter:B:1161:0x1403] */
    /* JADX WARNING: Removed duplicated region for block: B:1167:0x141c  */
    /* JADX WARNING: Removed duplicated region for block: B:1219:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x0439 A[Catch:{ Exception -> 0x04ec, all -> 0x04e7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x043b A[Catch:{ Exception -> 0x04ec, all -> 0x04e7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x0534 A[ExcHandler: all (th java.lang.Throwable), PHI: r20 
      PHI: (r20v11 int) = (r20v10 int), (r20v29 int), (r20v29 int), (r20v36 int), (r20v36 int) binds: [B:271:0x04fd, B:202:0x03b3, B:203:?, B:172:0x034a, B:173:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:172:0x034a] */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x053d A[ExcHandler: all (th java.lang.Throwable), Splitter:B:74:0x01dd] */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x058b A[ExcHandler: all (r0v142 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:54:0x01a1] */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x05b9 A[ExcHandler: all (r0v136 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:40:0x010e] */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x05ee A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x062e A[SYNTHETIC, Splitter:B:337:0x062e] */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x063c A[Catch:{ all -> 0x0632 }] */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x0641 A[Catch:{ all -> 0x0632 }] */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x072c  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x07bf A[ExcHandler: all (r0v119 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:423:0x07b0] */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x080c  */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0823  */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x082d A[SYNTHETIC, Splitter:B:460:0x082d] */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x086b A[SYNTHETIC, Splitter:B:466:0x086b] */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x0894  */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x0913 A[SYNTHETIC, Splitter:B:502:0x0913] */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x0923 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:502:0x0913] */
    /* JADX WARNING: Removed duplicated region for block: B:509:0x093d  */
    /* JADX WARNING: Removed duplicated region for block: B:511:0x0943 A[SYNTHETIC, Splitter:B:511:0x0943] */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0971  */
    /* JADX WARNING: Removed duplicated region for block: B:524:0x0974  */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x0a1c A[ExcHandler: all (th java.lang.Throwable), Splitter:B:549:0x09d6] */
    /* JADX WARNING: Removed duplicated region for block: B:574:0x0a20  */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x0a42  */
    /* JADX WARNING: Removed duplicated region for block: B:586:0x0a4e  */
    /* JADX WARNING: Removed duplicated region for block: B:587:0x0a51  */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x0a71 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:602:0x0a99 A[ExcHandler: all (r0v93 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:599:0x0a90] */
    /* JADX WARNING: Removed duplicated region for block: B:609:0x0ab9 A[SYNTHETIC, Splitter:B:609:0x0ab9] */
    /* JADX WARNING: Removed duplicated region for block: B:719:0x0c3c A[Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }] */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0CLASSNAME A[Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }] */
    /* JADX WARNING: Removed duplicated region for block: B:726:0x0CLASSNAME A[ExcHandler: all (th java.lang.Throwable), PHI: r48 
      PHI: (r48v28 long) = (r48v55 long), (r48v66 long), (r48v68 long), (r48v71 long), (r48v76 long) binds: [B:643:0x0b4a, B:660:0x0b7b, B:673:0x0ba9, B:691:0x0bd0, B:695:0x0bda] A[DONT_GENERATE, DONT_INLINE], Splitter:B:691:0x0bd0] */
    /* JADX WARNING: Removed duplicated region for block: B:728:0x0c5c  */
    /* JADX WARNING: Removed duplicated region for block: B:729:0x0c5e A[ExcHandler: all (th java.lang.Throwable), Splitter:B:609:0x0ab9] */
    /* JADX WARNING: Removed duplicated region for block: B:736:0x0c7f  */
    /* JADX WARNING: Removed duplicated region for block: B:739:0x0ca6 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:744:0x0cc9  */
    /* JADX WARNING: Removed duplicated region for block: B:745:0x0cd6  */
    /* JADX WARNING: Removed duplicated region for block: B:750:0x0ce5  */
    /* JADX WARNING: Removed duplicated region for block: B:752:0x0cfd  */
    /* JADX WARNING: Removed duplicated region for block: B:875:0x0var_ A[Catch:{ Exception -> 0x1115, all -> 0x1110 }] */
    /* JADX WARNING: Removed duplicated region for block: B:876:0x0var_ A[Catch:{ Exception -> 0x1115, all -> 0x1110 }] */
    /* JADX WARNING: Removed duplicated region for block: B:879:0x0var_ A[Catch:{ Exception -> 0x1115, all -> 0x1110 }] */
    /* JADX WARNING: Removed duplicated region for block: B:881:0x0f2f A[Catch:{ Exception -> 0x1115, all -> 0x1110 }] */
    /* JADX WARNING: Removed duplicated region for block: B:947:0x102a A[ExcHandler: Exception (e java.lang.Exception), PHI: r11 r31 r50 
      PHI: (r11v77 int) = (r11v233 int), (r11v238 int), (r11v242 int), (r11v244 int), (r11v249 int), (r11v250 int), (r11v251 int), (r11v253 int), (r11v255 int), (r11v257 int) binds: [B:983:0x10a6, B:961:0x1053, B:923:0x0fce, B:924:?, B:932:0x0fe4, B:933:?, B:935:0x0fe9, B:936:?, B:929:0x0fdc, B:930:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r31v46 long) = (r31v45 long), (r31v45 long), (r31v35 long), (r31v35 long), (r31v35 long), (r31v35 long), (r31v35 long), (r31v35 long), (r31v35 long), (r31v35 long) binds: [B:983:0x10a6, B:961:0x1053, B:923:0x0fce, B:924:?, B:932:0x0fe4, B:933:?, B:935:0x0fe9, B:936:?, B:929:0x0fdc, B:930:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r50v47 long) = (r50v46 long), (r50v46 long), (r50v44 long), (r50v44 long), (r50v44 long), (r50v44 long), (r50v44 long), (r50v44 long), (r50v44 long), (r50v44 long) binds: [B:983:0x10a6, B:961:0x1053, B:923:0x0fce, B:924:?, B:932:0x0fe4, B:933:?, B:935:0x0fe9, B:936:?, B:929:0x0fdc, B:930:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:923:0x0fce] */
    /* JADX WARNING: Removed duplicated region for block: B:953:0x1042  */
    /* JADX WARNING: Removed duplicated region for block: B:954:0x1045  */
    /* JADX WARNING: Removed duplicated region for block: B:961:0x1053 A[SYNTHETIC, Splitter:B:961:0x1053] */
    /* JADX WARNING: Removed duplicated region for block: B:966:0x1077 A[Catch:{ Exception -> 0x102a, all -> 0x1188 }] */
    /* JADX WARNING: Removed duplicated region for block: B:972:0x1088 A[Catch:{ Exception -> 0x102a, all -> 0x1188 }] */
    /* JADX WARNING: Removed duplicated region for block: B:973:0x108b A[Catch:{ Exception -> 0x102a, all -> 0x1188 }] */
    /* JADX WARNING: Removed duplicated region for block: B:981:0x10a0  */
    /* JADX WARNING: Removed duplicated region for block: B:998:0x10d0 A[Catch:{ Exception -> 0x118a, all -> 0x1188 }] */
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
            android.media.MediaCodec$BufferInfo r7 = new android.media.MediaCodec$BufferInfo     // Catch:{ all -> 0x13b9 }
            r7.<init>()     // Catch:{ all -> 0x13b9 }
            org.telegram.messenger.video.Mp4Movie r2 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ all -> 0x13b9 }
            r2.<init>()     // Catch:{ all -> 0x13b9 }
            r1 = r74
            r2.setCacheFile(r1)     // Catch:{ all -> 0x13b9 }
            r2.setRotation(r6)     // Catch:{ all -> 0x13b9 }
            r2.setSize(r12, r11)     // Catch:{ all -> 0x13b9 }
            org.telegram.messenger.video.MP4Builder r6 = new org.telegram.messenger.video.MP4Builder     // Catch:{ all -> 0x13b9 }
            r6.<init>()     // Catch:{ all -> 0x13b9 }
            r14 = r76
            org.telegram.messenger.video.MP4Builder r2 = r6.createMovie(r2, r14)     // Catch:{ all -> 0x13b9 }
            r15.mediaMuxer = r2     // Catch:{ all -> 0x13b9 }
            float r2 = (float) r4     // Catch:{ all -> 0x13b9 }
            r21 = 1148846080(0x447a0000, float:1000.0)
            float r22 = r2 / r21
            r23 = 1000(0x3e8, double:4.94E-321)
            long r1 = r4 * r23
            r15.endPresentationTime = r1     // Catch:{ all -> 0x13b9 }
            r72.checkConversionCanceled()     // Catch:{ all -> 0x13b9 }
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
            if (r95 == 0) goto L_0x067e
            int r16 = (r86 > r6 ? 1 : (r86 == r6 ? 0 : -1))
            if (r16 < 0) goto L_0x0086
            r2 = 1157234688(0x44fa0000, float:2000.0)
            int r2 = (r22 > r2 ? 1 : (r22 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0071
            r2 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            r9 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x008b
        L_0x0071:
            r2 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r2 = (r22 > r2 ? 1 : (r22 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x007f
            r2 = 2200000(0x2191c0, float:3.082857E-39)
            r9 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x008b
        L_0x007f:
            r2 = 1560000(0x17cdc0, float:2.186026E-39)
            r9 = 1560000(0x17cdc0, float:2.186026E-39)
            goto L_0x008b
        L_0x0086:
            if (r9 > 0) goto L_0x008b
            r9 = 921600(0xe1000, float:1.291437E-39)
        L_0x008b:
            int r2 = r12 % 16
            r16 = 1098907648(0x41800000, float:16.0)
            if (r2 == 0) goto L_0x00d7
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            if (r2 == 0) goto L_0x00ba
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            r2.<init>()     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            java.lang.String r6 = "changing width from "
            r2.append(r6)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            r2.append(r12)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            float r6 = (float) r12     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            float r6 = r6 / r16
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
        L_0x00ba:
            float r2 = (float) r12     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            float r2 = r2 / r16
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            int r2 = r2 * 16
            r12 = r2
            goto L_0x00d7
        L_0x00c5:
            r0 = move-exception
            r50 = r84
            r31 = r86
            r1 = r0
            r8 = r11
            r2 = -5
            r6 = 0
        L_0x00ce:
            r11 = r10
            goto L_0x13c9
        L_0x00d1:
            r0 = move-exception
            r1 = r0
            r48 = r9
            goto L_0x05e4
        L_0x00d7:
            int r2 = r11 % 16
            if (r2 == 0) goto L_0x010e
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            if (r2 == 0) goto L_0x0104
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            r2.<init>()     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            java.lang.String r6 = "changing height from "
            r2.append(r6)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            r2.append(r11)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            float r6 = (float) r11     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            float r6 = r6 / r16
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
        L_0x0104:
            float r2 = (float) r11     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            float r2 = r2 / r16
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            int r2 = r2 * 16
            r11 = r2
        L_0x010e:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05c8, all -> 0x05b9 }
            if (r2 == 0) goto L_0x0136
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            r2.<init>()     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            java.lang.String r6 = "create photo encoder "
            r2.append(r6)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            r2.append(r12)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            java.lang.String r6 = " "
            r2.append(r6)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            r2.append(r11)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            java.lang.String r6 = " duration = "
            r2.append(r6)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            r2.append(r4)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d1, all -> 0x00c5 }
        L_0x0136:
            r7 = r31
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r7, r12, r11)     // Catch:{ Exception -> 0x05c8, all -> 0x05b9 }
            java.lang.String r6 = "color-format"
            r31 = r1
            r1 = 2130708361(0x7var_, float:1.701803E38)
            r2.setInteger(r6, r1)     // Catch:{ Exception -> 0x05c8, all -> 0x05b9 }
            java.lang.String r1 = "bitrate"
            r2.setInteger(r1, r9)     // Catch:{ Exception -> 0x05c8, all -> 0x05b9 }
            java.lang.String r1 = "frame-rate"
            r2.setInteger(r1, r10)     // Catch:{ Exception -> 0x05c8, all -> 0x05b9 }
            java.lang.String r1 = "i-frame-interval"
            r6 = 2
            r2.setInteger(r1, r6)     // Catch:{ Exception -> 0x05c8, all -> 0x05b9 }
            android.media.MediaCodec r6 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x05c8, all -> 0x05b9 }
            r1 = 0
            r3 = 1
            r6.configure(r2, r1, r1, r3)     // Catch:{ Exception -> 0x05ac, all -> 0x05b9 }
            org.telegram.messenger.video.InputSurface r2 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x05ac, all -> 0x05b9 }
            android.view.Surface r1 = r6.createInputSurface()     // Catch:{ Exception -> 0x05ac, all -> 0x05b9 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x05ac, all -> 0x05b9 }
            r2.makeCurrent()     // Catch:{ Exception -> 0x059f, all -> 0x05b9 }
            r6.start()     // Catch:{ Exception -> 0x059f, all -> 0x05b9 }
            org.telegram.messenger.video.OutputSurface r16 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x059f, all -> 0x05b9 }
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
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0596, all -> 0x058b }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0581, all -> 0x058b }
            if (r1 >= r14) goto L_0x01b6
            java.nio.ByteBuffer[] r6 = r77.getOutputBuffers()     // Catch:{ Exception -> 0x01ad, all -> 0x058b }
            goto L_0x01b7
        L_0x01ad:
            r0 = move-exception
            r6 = r77
            r1 = r0
            r11 = r49
            r2 = -5
            goto L_0x05ea
        L_0x01b6:
            r6 = 0
        L_0x01b7:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x0581, all -> 0x058b }
            r4 = r6
            r1 = 1
            r2 = -5
            r3 = 0
            r5 = 0
            r6 = 0
            r7 = 0
        L_0x01c1:
            if (r6 != 0) goto L_0x056c
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x0563, all -> 0x0555 }
            r8 = r3 ^ 1
            r9 = r7
            r7 = r6
            r6 = r5
            r5 = r4
            r4 = r3
            r3 = r2
            r2 = r1
            r1 = 1
        L_0x01d0:
            if (r8 != 0) goto L_0x01dd
            if (r1 == 0) goto L_0x01d5
            goto L_0x01dd
        L_0x01d5:
            r1 = r2
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r9
            goto L_0x01c1
        L_0x01dd:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x054a, all -> 0x053d }
            if (r91 == 0) goto L_0x01e7
            r10 = 22000(0x55f0, double:1.08694E-319)
            r14 = r46
            goto L_0x01eb
        L_0x01e7:
            r14 = r46
            r10 = 2500(0x9c4, double:1.235E-320)
        L_0x01eb:
            r69 = r1
            r1 = r77
            r77 = r69
            int r10 = r1.dequeueOutputBuffer(r14, r10)     // Catch:{ Exception -> 0x053b, all -> 0x053d }
            r11 = -1
            if (r10 != r11) goto L_0x020f
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
            goto L_0x0446
        L_0x020f:
            r11 = -3
            if (r10 != r11) goto L_0x0244
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            r78 = r7
            r7 = 21
            if (r11 >= r7) goto L_0x021e
            java.nio.ByteBuffer[] r5 = r1.getOutputBuffers()     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
        L_0x021e:
            r7 = r5
            r18 = r6
            r80 = r8
            r17 = r9
            r8 = r35
            r11 = r44
        L_0x0229:
            r13 = r47
            r9 = r78
            r6 = r2
            r5 = r3
            r3 = r49
        L_0x0231:
            r2 = -1
            goto L_0x0446
        L_0x0234:
            r0 = move-exception
            r11 = r79
            r50 = r84
            r31 = r86
            r1 = r0
            r2 = r3
            goto L_0x055d
        L_0x023f:
            r0 = move-exception
            r6 = r1
            r2 = r3
            goto L_0x0569
        L_0x0244:
            r78 = r7
            r7 = -2
            if (r10 != r7) goto L_0x02a9
            android.media.MediaFormat r7 = r1.getOutputFormat()     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            if (r11 == 0) goto L_0x0268
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            r11.<init>()     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            r80 = r8
            java.lang.String r8 = "photo encoder new format "
            r11.append(r8)     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            r11.append(r7)     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            java.lang.String r8 = r11.toString()     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            org.telegram.messenger.FileLog.d(r8)     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            goto L_0x026a
        L_0x0268:
            r80 = r8
        L_0x026a:
            r8 = -5
            if (r3 != r8) goto L_0x029f
            if (r7 == 0) goto L_0x029f
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            r8 = 0
            int r3 = r11.addTrack(r7, r8)     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            r11 = r45
            boolean r17 = r7.containsKey(r11)     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            if (r17 == 0) goto L_0x029d
            int r8 = r7.getInteger(r11)     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            r45 = r11
            r11 = 1
            if (r8 != r11) goto L_0x029f
            r8 = r35
            java.nio.ByteBuffer r6 = r7.getByteBuffer(r8)     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            r11 = r44
            java.nio.ByteBuffer r7 = r7.getByteBuffer(r11)     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            int r6 = r6.limit()     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            int r7 = r7.limit()     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            int r6 = r6 + r7
            goto L_0x02a3
        L_0x029d:
            r45 = r11
        L_0x029f:
            r8 = r35
            r11 = r44
        L_0x02a3:
            r7 = r5
            r18 = r6
            r17 = r9
            goto L_0x0229
        L_0x02a9:
            r80 = r8
            r8 = r35
            r11 = r44
            if (r10 < 0) goto L_0x0519
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x053b, all -> 0x053d }
            r13 = 21
            if (r7 >= r13) goto L_0x02ba
            r7 = r5[r10]     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            goto L_0x02be
        L_0x02ba:
            java.nio.ByteBuffer r7 = r1.getOutputBuffer(r10)     // Catch:{ Exception -> 0x053b, all -> 0x053d }
        L_0x02be:
            if (r7 == 0) goto L_0x04f9
            int r13 = r14.size     // Catch:{ Exception -> 0x04f3, all -> 0x053d }
            r78 = r5
            r5 = 1
            if (r13 <= r5) goto L_0x0426
            int r5 = r14.flags     // Catch:{ Exception -> 0x041b, all -> 0x0408 }
            r17 = r5 & 2
            if (r17 != 0) goto L_0x036b
            if (r6 == 0) goto L_0x02de
            r17 = r5 & 1
            if (r17 == 0) goto L_0x02de
            r17 = r9
            int r9 = r14.offset     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            int r9 = r9 + r6
            r14.offset = r9     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            int r13 = r13 - r6
            r14.size = r13     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            goto L_0x02e0
        L_0x02de:
            r17 = r9
        L_0x02e0:
            if (r2 == 0) goto L_0x032e
            r5 = r5 & 1
            if (r5 == 0) goto L_0x032e
            int r2 = r14.size     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            r5 = 100
            if (r2 <= r5) goto L_0x032d
            int r2 = r14.offset     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            r7.position(r2)     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            byte[] r2 = new byte[r5]     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            r7.get(r2)     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            r9 = 0
            r13 = 0
        L_0x02f8:
            r5 = 96
            if (r9 >= r5) goto L_0x032d
            byte r5 = r2[r9]     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            if (r5 != 0) goto L_0x0324
            int r5 = r9 + 1
            byte r5 = r2[r5]     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            if (r5 != 0) goto L_0x0324
            int r5 = r9 + 2
            byte r5 = r2[r5]     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            if (r5 != 0) goto L_0x0324
            int r5 = r9 + 3
            byte r5 = r2[r5]     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            r18 = r2
            r2 = 1
            if (r5 != r2) goto L_0x0326
            int r13 = r13 + 1
            if (r13 <= r2) goto L_0x0326
            int r2 = r14.offset     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            int r2 = r2 + r9
            r14.offset = r2     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            int r2 = r14.size     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            int r2 = r2 - r9
            r14.size = r2     // Catch:{ Exception -> 0x023f, all -> 0x0234 }
            goto L_0x032d
        L_0x0324:
            r18 = r2
        L_0x0326:
            int r9 = r9 + 1
            r2 = r18
            r5 = 100
            goto L_0x02f8
        L_0x032d:
            r2 = 0
        L_0x032e:
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x0366, all -> 0x053d }
            r18 = r6
            r9 = 1
            long r5 = r5.writeSampleData(r3, r7, r14, r9)     // Catch:{ Exception -> 0x0366, all -> 0x053d }
            r13 = r2
            r9 = r3
            r2 = 0
            int r7 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r7 == 0) goto L_0x035b
            org.telegram.messenger.MediaController$VideoConvertorListener r7 = r15.callback     // Catch:{ Exception -> 0x0356, all -> 0x0351 }
            if (r7 == 0) goto L_0x035b
            r20 = r9
            float r9 = (float) r2
            float r9 = r9 / r21
            float r9 = r9 / r22
            r7.didWriteData(r5, r9)     // Catch:{ Exception -> 0x034e, all -> 0x0534 }
            goto L_0x035d
        L_0x034e:
            r0 = move-exception
            goto L_0x0551
        L_0x0351:
            r0 = move-exception
            r20 = r9
            goto L_0x0540
        L_0x0356:
            r0 = move-exception
            r20 = r9
            goto L_0x0551
        L_0x035b:
            r20 = r9
        L_0x035d:
            r6 = r13
            r2 = r20
            r13 = r47
            r3 = r49
            goto L_0x0433
        L_0x0366:
            r0 = move-exception
            r20 = r3
            goto L_0x0551
        L_0x036b:
            r5 = r3
            r18 = r6
            r17 = r9
            r9 = -5
            r6 = r2
            r2 = 0
            if (r5 != r9) goto L_0x0401
            byte[] r9 = new byte[r13]     // Catch:{ Exception -> 0x03fd, all -> 0x03f9 }
            int r2 = r14.offset     // Catch:{ Exception -> 0x03fd, all -> 0x03f9 }
            int r2 = r2 + r13
            r7.limit(r2)     // Catch:{ Exception -> 0x03fd, all -> 0x03f9 }
            int r2 = r14.offset     // Catch:{ Exception -> 0x03fd, all -> 0x03f9 }
            r7.position(r2)     // Catch:{ Exception -> 0x03fd, all -> 0x03f9 }
            r7.get(r9)     // Catch:{ Exception -> 0x03fd, all -> 0x03f9 }
            int r2 = r14.size     // Catch:{ Exception -> 0x03fd, all -> 0x03f9 }
            r3 = 1
            int r2 = r2 - r3
        L_0x038a:
            if (r2 < 0) goto L_0x03d7
            r13 = 3
            if (r2 <= r13) goto L_0x03d7
            byte r7 = r9[r2]     // Catch:{ Exception -> 0x03d2, all -> 0x03cd }
            if (r7 != r3) goto L_0x03c5
            int r3 = r2 + -1
            byte r3 = r9[r3]     // Catch:{ Exception -> 0x03d2, all -> 0x03cd }
            if (r3 != 0) goto L_0x03c5
            int r3 = r2 + -2
            byte r3 = r9[r3]     // Catch:{ Exception -> 0x03d2, all -> 0x03cd }
            if (r3 != 0) goto L_0x03c5
            int r3 = r2 + -3
            byte r7 = r9[r3]     // Catch:{ Exception -> 0x03d2, all -> 0x03cd }
            if (r7 != 0) goto L_0x03c5
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r3)     // Catch:{ Exception -> 0x03d2, all -> 0x03cd }
            int r7 = r14.size     // Catch:{ Exception -> 0x03d2, all -> 0x03cd }
            int r7 = r7 - r3
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.allocate(r7)     // Catch:{ Exception -> 0x03d2, all -> 0x03cd }
            r20 = r5
            r13 = 0
            java.nio.ByteBuffer r5 = r2.put(r9, r13, r3)     // Catch:{ Exception -> 0x034e, all -> 0x0534 }
            r5.position(r13)     // Catch:{ Exception -> 0x034e, all -> 0x0534 }
            int r5 = r14.size     // Catch:{ Exception -> 0x034e, all -> 0x0534 }
            int r5 = r5 - r3
            java.nio.ByteBuffer r3 = r7.put(r9, r3, r5)     // Catch:{ Exception -> 0x034e, all -> 0x0534 }
            r3.position(r13)     // Catch:{ Exception -> 0x034e, all -> 0x0534 }
            goto L_0x03db
        L_0x03c5:
            r20 = r5
            int r2 = r2 + -1
            r5 = r20
            r3 = 1
            goto L_0x038a
        L_0x03cd:
            r0 = move-exception
            r20 = r5
            goto L_0x0540
        L_0x03d2:
            r0 = move-exception
            r20 = r5
            goto L_0x0551
        L_0x03d7:
            r20 = r5
            r2 = 0
            r7 = 0
        L_0x03db:
            r13 = r47
            r3 = r49
            android.media.MediaFormat r5 = android.media.MediaFormat.createVideoFormat(r13, r12, r3)     // Catch:{ Exception -> 0x03f7, all -> 0x03f5 }
            if (r2 == 0) goto L_0x03ed
            if (r7 == 0) goto L_0x03ed
            r5.setByteBuffer(r8, r2)     // Catch:{ Exception -> 0x03f7, all -> 0x03f5 }
            r5.setByteBuffer(r11, r7)     // Catch:{ Exception -> 0x03f7, all -> 0x03f5 }
        L_0x03ed:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x03f7, all -> 0x03f5 }
            r7 = 0
            int r2 = r2.addTrack(r5, r7)     // Catch:{ Exception -> 0x03f7, all -> 0x03f5 }
            goto L_0x0433
        L_0x03f5:
            r0 = move-exception
            goto L_0x040d
        L_0x03f7:
            r0 = move-exception
            goto L_0x0420
        L_0x03f9:
            r0 = move-exception
            r20 = r5
            goto L_0x040b
        L_0x03fd:
            r0 = move-exception
            r20 = r5
            goto L_0x041e
        L_0x0401:
            r20 = r5
            r13 = r47
            r3 = r49
            goto L_0x0431
        L_0x0408:
            r0 = move-exception
            r20 = r3
        L_0x040b:
            r3 = r49
        L_0x040d:
            r11 = r79
            r50 = r84
            r31 = r86
            r1 = r0
            r8 = r3
            r2 = r20
            r9 = r48
            goto L_0x05db
        L_0x041b:
            r0 = move-exception
            r20 = r3
        L_0x041e:
            r3 = r49
        L_0x0420:
            r6 = r1
            r11 = r3
            r2 = r20
            goto L_0x05b7
        L_0x0426:
            r20 = r3
            r18 = r6
            r17 = r9
            r13 = r47
            r3 = r49
            r6 = r2
        L_0x0431:
            r2 = r20
        L_0x0433:
            int r5 = r14.flags     // Catch:{ Exception -> 0x04ec, all -> 0x04e7 }
            r5 = r5 & 4
            if (r5 == 0) goto L_0x043b
            r5 = 1
            goto L_0x043c
        L_0x043b:
            r5 = 0
        L_0x043c:
            r7 = 0
            r1.releaseOutputBuffer(r10, r7)     // Catch:{ Exception -> 0x04ec, all -> 0x04e7 }
            r7 = r78
            r9 = r5
            r5 = r2
            goto L_0x0231
        L_0x0446:
            if (r10 == r2) goto L_0x0468
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
            goto L_0x01d0
        L_0x0468:
            if (r4 != 0) goto L_0x04bb
            r16.drawImage()     // Catch:{ Exception -> 0x04b2, all -> 0x04a5 }
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
            r10.setPresentationTime(r3)     // Catch:{ Exception -> 0x049e, all -> 0x049c }
            r10.swapBuffers()     // Catch:{ Exception -> 0x049e, all -> 0x049c }
            int r2 = r2 + 1
            float r3 = (float) r2     // Catch:{ Exception -> 0x049e, all -> 0x049c }
            r4 = 1106247680(0x41var_, float:30.0)
            float r4 = r4 * r22
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 < 0) goto L_0x0499
            r1.signalEndOfInputStream()     // Catch:{ Exception -> 0x049e, all -> 0x049c }
            r3 = r2
            r2 = 0
            r4 = 1
            goto L_0x04c6
        L_0x0499:
            r4 = r78
            goto L_0x04c3
        L_0x049c:
            r0 = move-exception
            goto L_0x04a8
        L_0x049e:
            r0 = move-exception
            r6 = r1
            r2 = r5
            r36 = r10
            goto L_0x0569
        L_0x04a5:
            r0 = move-exception
            r49 = r3
        L_0x04a8:
            r11 = r79
            r50 = r84
            r31 = r86
            r1 = r0
            r2 = r5
            goto L_0x055d
        L_0x04b2:
            r0 = move-exception
            r49 = r3
            r10 = r36
            r6 = r1
            r2 = r5
            goto L_0x0569
        L_0x04bb:
            r49 = r3
            r78 = r4
            r2 = r17
            r10 = r36
        L_0x04c3:
            r3 = r2
            r2 = r80
        L_0x04c6:
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
            goto L_0x01d0
        L_0x04e7:
            r0 = move-exception
            r49 = r3
            goto L_0x0556
        L_0x04ec:
            r0 = move-exception
            r49 = r3
            r10 = r36
            goto L_0x0568
        L_0x04f3:
            r0 = move-exception
            r20 = r3
            r10 = r36
            goto L_0x0551
        L_0x04f9:
            r20 = r3
            r3 = r36
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0536, all -> 0x0534 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0536, all -> 0x0534 }
            r4.<init>()     // Catch:{ Exception -> 0x0536, all -> 0x0534 }
            java.lang.String r5 = "encoderOutputBuffer "
            r4.append(r5)     // Catch:{ Exception -> 0x0536, all -> 0x0534 }
            r4.append(r10)     // Catch:{ Exception -> 0x0536, all -> 0x0534 }
            java.lang.String r5 = " was null"
            r4.append(r5)     // Catch:{ Exception -> 0x0536, all -> 0x0534 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0536, all -> 0x0534 }
            r2.<init>(r4)     // Catch:{ Exception -> 0x0536, all -> 0x0534 }
            throw r2     // Catch:{ Exception -> 0x0536, all -> 0x0534 }
        L_0x0519:
            r20 = r3
            r3 = r36
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0536, all -> 0x0534 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0536, all -> 0x0534 }
            r4.<init>()     // Catch:{ Exception -> 0x0536, all -> 0x0534 }
            java.lang.String r5 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x0536, all -> 0x0534 }
            r4.append(r10)     // Catch:{ Exception -> 0x0536, all -> 0x0534 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0536, all -> 0x0534 }
            r2.<init>(r4)     // Catch:{ Exception -> 0x0536, all -> 0x0534 }
            throw r2     // Catch:{ Exception -> 0x0536, all -> 0x0534 }
        L_0x0534:
            r0 = move-exception
            goto L_0x0540
        L_0x0536:
            r0 = move-exception
            r6 = r1
            r36 = r3
            goto L_0x0552
        L_0x053b:
            r0 = move-exception
            goto L_0x054d
        L_0x053d:
            r0 = move-exception
            r20 = r3
        L_0x0540:
            r11 = r79
            r50 = r84
            r31 = r86
            r1 = r0
            r2 = r20
            goto L_0x055d
        L_0x054a:
            r0 = move-exception
            r1 = r77
        L_0x054d:
            r20 = r3
            r3 = r36
        L_0x0551:
            r6 = r1
        L_0x0552:
            r2 = r20
            goto L_0x0569
        L_0x0555:
            r0 = move-exception
        L_0x0556:
            r11 = r79
            r50 = r84
            r31 = r86
            r1 = r0
        L_0x055d:
            r9 = r48
            r8 = r49
            goto L_0x05db
        L_0x0563:
            r0 = move-exception
            r1 = r77
            r3 = r36
        L_0x0568:
            r6 = r1
        L_0x0569:
            r11 = r49
            goto L_0x05b7
        L_0x056c:
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
            goto L_0x062c
        L_0x0581:
            r0 = move-exception
            r1 = r77
            r3 = r36
            r6 = r1
            r11 = r49
            r2 = -5
            goto L_0x05b7
        L_0x058b:
            r0 = move-exception
            r11 = r79
            r50 = r84
            r31 = r86
            r1 = r0
            r9 = r48
            goto L_0x05c5
        L_0x0596:
            r0 = move-exception
            r1 = r77
            r3 = r36
            r6 = r1
            r11 = r49
            goto L_0x05a8
        L_0x059f:
            r0 = move-exception
            r3 = r2
            r1 = r6
            r48 = r9
            r49 = r11
            r36 = r3
        L_0x05a8:
            r2 = -5
            r16 = 0
            goto L_0x05b7
        L_0x05ac:
            r0 = move-exception
            r1 = r6
            r48 = r9
            r49 = r11
            r2 = -5
            r16 = 0
            r36 = 0
        L_0x05b7:
            r1 = r0
            goto L_0x05ea
        L_0x05b9:
            r0 = move-exception
            r48 = r9
            r49 = r11
            r11 = r79
            r50 = r84
            r31 = r86
            r1 = r0
        L_0x05c5:
            r8 = r49
            goto L_0x05da
        L_0x05c8:
            r0 = move-exception
            r48 = r9
            r49 = r11
            goto L_0x05e3
        L_0x05ce:
            r0 = move-exception
            r48 = r9
            r8 = r78
            r11 = r79
            r50 = r84
            r31 = r86
            r1 = r0
        L_0x05da:
            r2 = -5
        L_0x05db:
            r6 = 0
            goto L_0x13c9
        L_0x05de:
            r0 = move-exception
            r48 = r9
            r11 = r78
        L_0x05e3:
            r1 = r0
        L_0x05e4:
            r2 = -5
            r6 = 0
            r16 = 0
            r36 = 0
        L_0x05ea:
            boolean r3 = r1 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x0670 }
            if (r3 == 0) goto L_0x05f3
            if (r91 != 0) goto L_0x05f3
            r43 = 1
            goto L_0x05f5
        L_0x05f3:
            r43 = 0
        L_0x05f5:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0664 }
            r3.<init>()     // Catch:{ all -> 0x0664 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x0664 }
            r9 = r48
            r3.append(r9)     // Catch:{ all -> 0x0662 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x0662 }
            r10 = r79
            r3.append(r10)     // Catch:{ all -> 0x0657 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x0657 }
            r3.append(r11)     // Catch:{ all -> 0x0657 }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x0657 }
            r3.append(r12)     // Catch:{ all -> 0x0657 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0657 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x0657 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x0657 }
            r3 = r2
            r2 = r36
            r1 = 1
        L_0x062c:
            if (r16 == 0) goto L_0x063a
            r16.release()     // Catch:{ all -> 0x0632 }
            goto L_0x063a
        L_0x0632:
            r0 = move-exception
            r50 = r84
            r31 = r86
            r1 = r0
            r2 = r3
            goto L_0x065d
        L_0x063a:
            if (r2 == 0) goto L_0x063f
            r2.release()     // Catch:{ all -> 0x0632 }
        L_0x063f:
            if (r6 == 0) goto L_0x0647
            r6.stop()     // Catch:{ all -> 0x0632 }
            r6.release()     // Catch:{ all -> 0x0632 }
        L_0x0647:
            r72.checkConversionCanceled()     // Catch:{ all -> 0x0632 }
            r50 = r84
            r31 = r86
            r2 = r3
            r3 = r9
            r8 = r11
            r9 = r12
            r6 = r43
            r11 = r10
            goto L_0x138e
        L_0x0657:
            r0 = move-exception
            r50 = r84
            r31 = r86
            r1 = r0
        L_0x065d:
            r8 = r11
            r6 = r43
            goto L_0x00ce
        L_0x0662:
            r0 = move-exception
            goto L_0x0667
        L_0x0664:
            r0 = move-exception
            r9 = r48
        L_0x0667:
            r50 = r84
            r31 = r86
            r1 = r0
            r8 = r11
            r6 = r43
            goto L_0x067a
        L_0x0670:
            r0 = move-exception
            r9 = r48
            r50 = r84
            r31 = r86
            r1 = r0
            r8 = r11
            r6 = 0
        L_0x067a:
            r11 = r79
            goto L_0x13c9
        L_0x067e:
            r8 = r1
            r11 = r23
            r14 = r25
            r7 = r26
            r13 = r31
            r1 = 921600(0xe1000, float:1.291437E-39)
            r18 = -5
            android.media.MediaExtractor r2 = new android.media.MediaExtractor     // Catch:{ all -> 0x13b0 }
            r2.<init>()     // Catch:{ all -> 0x13b0 }
            r15.extractor = r2     // Catch:{ all -> 0x13b0 }
            r5 = r73
            r2.setDataSource(r5)     // Catch:{ all -> 0x13b0 }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x13b0 }
            r6 = 0
            int r4 = org.telegram.messenger.MediaController.findTrack(r2, r6)     // Catch:{ all -> 0x13b0 }
            r2 = -1
            if (r9 == r2) goto L_0x06b8
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x06ac }
            r3 = 1
            int r2 = org.telegram.messenger.MediaController.findTrack(r2, r3)     // Catch:{ all -> 0x06ac }
            r44 = r11
            goto L_0x06bc
        L_0x06ac:
            r0 = move-exception
            r8 = r78
            r50 = r84
            r31 = r86
            r1 = r0
            r11 = r10
            r2 = -5
            goto L_0x13c9
        L_0x06b8:
            r3 = 1
            r44 = r11
            r2 = -1
        L_0x06bc:
            java.lang.String r11 = "mime"
            if (r4 < 0) goto L_0x06d2
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ all -> 0x06ac }
            android.media.MediaFormat r3 = r3.getTrackFormat(r4)     // Catch:{ all -> 0x06ac }
            java.lang.String r3 = r3.getString(r11)     // Catch:{ all -> 0x06ac }
            boolean r3 = r3.equals(r13)     // Catch:{ all -> 0x06ac }
            if (r3 != 0) goto L_0x06d2
            r3 = 1
            goto L_0x06d3
        L_0x06d2:
            r3 = 0
        L_0x06d3:
            if (r90 != 0) goto L_0x0724
            if (r3 == 0) goto L_0x06d8
            goto L_0x0724
        L_0x06d8:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x0715 }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ all -> 0x0715 }
            r1 = -1
            if (r9 == r1) goto L_0x06e1
            r13 = 1
            goto L_0x06e2
        L_0x06e1:
            r13 = 0
        L_0x06e2:
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
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ all -> 0x0706 }
            r9 = r77
            r8 = r78
            r3 = r80
            r50 = r84
            r31 = r86
            r11 = r14
            r1 = 0
            r2 = -5
            r6 = 0
            goto L_0x138e
        L_0x0706:
            r0 = move-exception
            r12 = r77
            r8 = r78
            r9 = r80
            r50 = r84
            r31 = r86
            r1 = r0
            r11 = r14
            goto L_0x05da
        L_0x0715:
            r0 = move-exception
            r12 = r77
            r8 = r78
            r9 = r80
            r50 = r84
            r31 = r86
            r1 = r0
            r11 = r10
            goto L_0x05da
        L_0x0724:
            r12 = r5
            r69 = r14
            r14 = r10
            r10 = r69
            if (r4 < 0) goto L_0x1355
            r18 = -2147483648(0xfffffffvar_, double:NaN)
            r3 = 1000(0x3e8, float:1.401E-42)
            int r3 = r3 / r14
            int r3 = r3 * 1000
            long r5 = (long) r3     // Catch:{ Exception -> 0x12c6, all -> 0x12b4 }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x12c6, all -> 0x12b4 }
            r3.selectTrack(r4)     // Catch:{ Exception -> 0x12c6, all -> 0x12b4 }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x12c6, all -> 0x12b4 }
            android.media.MediaFormat r9 = r3.getTrackFormat(r4)     // Catch:{ Exception -> 0x12c6, all -> 0x12b4 }
            r23 = 0
            int r3 = (r86 > r23 ? 1 : (r86 == r23 ? 0 : -1))
            if (r3 < 0) goto L_0x0763
            r3 = 1157234688(0x44fa0000, float:2000.0)
            int r3 = (r22 > r3 ? 1 : (r22 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x0750
            r3 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x075e
        L_0x0750:
            r3 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r3 = (r22 > r3 ? 1 : (r22 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x075b
            r3 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x075e
        L_0x075b:
            r3 = 1560000(0x17cdc0, float:2.186026E-39)
        L_0x075e:
            r1 = r81
            r23 = 0
            goto L_0x0773
        L_0x0763:
            if (r80 > 0) goto L_0x076d
            r1 = r81
            r23 = r86
            r3 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x0773
        L_0x076d:
            r3 = r80
            r1 = r81
            r23 = r86
        L_0x0773:
            if (r1 <= 0) goto L_0x079d
            int r3 = java.lang.Math.min(r1, r3)     // Catch:{ Exception -> 0x0788, all -> 0x077a }
            goto L_0x079d
        L_0x077a:
            r0 = move-exception
            r12 = r77
            r8 = r78
            r50 = r84
            r1 = r0
            r9 = r3
            r11 = r14
            r31 = r23
            goto L_0x05da
        L_0x0788:
            r0 = move-exception
            r50 = r84
            r5 = r0
            r61 = r4
            r11 = r14
            r31 = r23
        L_0x0791:
            r1 = 0
            r2 = -5
            r4 = 0
            r6 = 0
            r27 = 0
            r40 = 0
            r58 = 0
            goto L_0x12dd
        L_0x079d:
            r25 = 0
            int r27 = (r23 > r25 ? 1 : (r23 == r25 ? 0 : -1))
            r45 = r7
            r35 = r8
            if (r27 < 0) goto L_0x07aa
            r7 = r16
            goto L_0x07ac
        L_0x07aa:
            r7 = r23
        L_0x07ac:
            int r23 = (r7 > r25 ? 1 : (r7 == r25 ? 0 : -1))
            if (r23 < 0) goto L_0x07d9
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x07cf, all -> 0x07bf }
            r23 = r2
            r2 = 0
            r1.seekTo(r7, r2)     // Catch:{ Exception -> 0x07cf, all -> 0x07bf }
            r24 = r4
            r25 = r5
            r5 = 0
            goto L_0x0808
        L_0x07bf:
            r0 = move-exception
            r12 = r77
            r50 = r84
            r1 = r0
            r9 = r3
        L_0x07c6:
            r31 = r7
            r11 = r14
            r2 = -5
            r6 = 0
            r8 = r78
            goto L_0x13c9
        L_0x07cf:
            r0 = move-exception
            r50 = r84
            r5 = r0
            r61 = r4
            r31 = r7
            r11 = r14
            goto L_0x0791
        L_0x07d9:
            r23 = r2
            r24 = 0
            r1 = r82
            int r26 = (r1 > r24 ? 1 : (r1 == r24 ? 0 : -1))
            if (r26 <= 0) goto L_0x07fc
            r24 = r4
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x07f2, all -> 0x07bf }
            r25 = r5
            r5 = 0
            r4.seekTo(r1, r5)     // Catch:{ Exception -> 0x07f2, all -> 0x07bf }
            r4 = r96
            r5 = 0
            goto L_0x080a
        L_0x07f2:
            r0 = move-exception
            r50 = r84
            r5 = r0
        L_0x07f6:
            r31 = r7
            r11 = r14
            r61 = r24
            goto L_0x0791
        L_0x07fc:
            r24 = r4
            r25 = r5
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x12aa, all -> 0x129c }
            r1 = 0
            r5 = 0
            r4.seekTo(r5, r1)     // Catch:{ Exception -> 0x1293, all -> 0x128c }
        L_0x0808:
            r4 = r96
        L_0x080a:
            if (r4 == 0) goto L_0x0823
            r1 = 90
            r2 = r75
            if (r2 == r1) goto L_0x081c
            r1 = 270(0x10e, float:3.78E-43)
            if (r2 != r1) goto L_0x0817
            goto L_0x081c
        L_0x0817:
            int r1 = r4.transformWidth     // Catch:{ Exception -> 0x07f2, all -> 0x07bf }
            int r5 = r4.transformHeight     // Catch:{ Exception -> 0x07f2, all -> 0x07bf }
            goto L_0x0820
        L_0x081c:
            int r1 = r4.transformHeight     // Catch:{ Exception -> 0x07f2, all -> 0x07bf }
            int r5 = r4.transformWidth     // Catch:{ Exception -> 0x07f2, all -> 0x07bf }
        L_0x0820:
            r6 = r5
            r5 = r1
            goto L_0x0829
        L_0x0823:
            r2 = r75
            r5 = r77
            r6 = r78
        L_0x0829:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x12aa, all -> 0x129c }
            if (r1 == 0) goto L_0x0849
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x07f2, all -> 0x07bf }
            r1.<init>()     // Catch:{ Exception -> 0x07f2, all -> 0x07bf }
            java.lang.String r2 = "create encoder with w = "
            r1.append(r2)     // Catch:{ Exception -> 0x07f2, all -> 0x07bf }
            r1.append(r5)     // Catch:{ Exception -> 0x07f2, all -> 0x07bf }
            java.lang.String r2 = " h = "
            r1.append(r2)     // Catch:{ Exception -> 0x07f2, all -> 0x07bf }
            r1.append(r6)     // Catch:{ Exception -> 0x07f2, all -> 0x07bf }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x07f2, all -> 0x07bf }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x07f2, all -> 0x07bf }
        L_0x0849:
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r13, r5, r6)     // Catch:{ Exception -> 0x12aa, all -> 0x129c }
            java.lang.String r2 = "color-format"
            r4 = 2130708361(0x7var_, float:1.701803E38)
            r1.setInteger(r2, r4)     // Catch:{ Exception -> 0x12aa, all -> 0x129c }
            java.lang.String r2 = "bitrate"
            r1.setInteger(r2, r3)     // Catch:{ Exception -> 0x12aa, all -> 0x129c }
            java.lang.String r2 = "frame-rate"
            r1.setInteger(r2, r14)     // Catch:{ Exception -> 0x12aa, all -> 0x129c }
            java.lang.String r2 = "i-frame-interval"
            r4 = 2
            r1.setInteger(r2, r4)     // Catch:{ Exception -> 0x12aa, all -> 0x129c }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x12aa, all -> 0x129c }
            r2 = 23
            if (r4 >= r2) goto L_0x0894
            int r2 = java.lang.Math.min(r6, r5)     // Catch:{ Exception -> 0x07f2, all -> 0x07bf }
            r80 = r4
            r4 = 480(0x1e0, float:6.73E-43)
            if (r2 > r4) goto L_0x0896
            r2 = 921600(0xe1000, float:1.291437E-39)
            if (r3 <= r2) goto L_0x087b
            goto L_0x087c
        L_0x087b:
            r2 = r3
        L_0x087c:
            java.lang.String r3 = "bitrate"
            r1.setInteger(r3, r2)     // Catch:{ Exception -> 0x088d, all -> 0x0884 }
            r20 = r2
            goto L_0x0898
        L_0x0884:
            r0 = move-exception
            r12 = r77
            r50 = r84
            r1 = r0
            r9 = r2
            goto L_0x07c6
        L_0x088d:
            r0 = move-exception
            r50 = r84
            r5 = r0
            r3 = r2
            goto L_0x07f6
        L_0x0894:
            r80 = r4
        L_0x0896:
            r20 = r3
        L_0x0898:
            android.media.MediaCodec r4 = android.media.MediaCodec.createEncoderByType(r13)     // Catch:{ Exception -> 0x127e, all -> 0x126c }
            r2 = 1
            r3 = 0
            r4.configure(r1, r3, r3, r2)     // Catch:{ Exception -> 0x1259, all -> 0x126c }
            org.telegram.messenger.video.InputSurface r1 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x1259, all -> 0x126c }
            android.view.Surface r2 = r4.createInputSurface()     // Catch:{ Exception -> 0x1259, all -> 0x126c }
            r1.<init>(r2)     // Catch:{ Exception -> 0x1259, all -> 0x126c }
            r1.makeCurrent()     // Catch:{ Exception -> 0x123f, all -> 0x126c }
            r4.start()     // Catch:{ Exception -> 0x123f, all -> 0x126c }
            java.lang.String r2 = r9.getString(r11)     // Catch:{ Exception -> 0x123f, all -> 0x126c }
            android.media.MediaCodec r2 = android.media.MediaCodec.createDecoderByType(r2)     // Catch:{ Exception -> 0x123f, all -> 0x126c }
            org.telegram.messenger.video.OutputSurface r27 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x1223, all -> 0x126c }
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
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x1216, all -> 0x120f }
            android.view.Surface r1 = r27.getSurface()     // Catch:{ Exception -> 0x11fe, all -> 0x120f }
            r3 = r60
            r2 = r67
            r4 = 0
            r5 = 0
            r3.configure(r2, r1, r4, r5)     // Catch:{ Exception -> 0x11f4, all -> 0x120f }
            r3.start()     // Catch:{ Exception -> 0x11f4, all -> 0x120f }
            r1 = r62
            r2 = 21
            if (r1 >= r2) goto L_0x093d
            java.nio.ByteBuffer[] r6 = r3.getInputBuffers()     // Catch:{ Exception -> 0x092f, all -> 0x0923 }
            java.nio.ByteBuffer[] r1 = r80.getOutputBuffers()     // Catch:{ Exception -> 0x092f, all -> 0x0923 }
            r2 = r59
            r69 = r6
            r6 = r1
            r1 = r69
            goto L_0x0941
        L_0x0923:
            r0 = move-exception
        L_0x0924:
            r12 = r77
            r8 = r78
            r11 = r79
            r50 = r84
            r1 = r0
            goto L_0x127a
        L_0x092f:
            r0 = move-exception
            r11 = r79
            r6 = r80
            r50 = r84
            r5 = r0
            r40 = r4
            r1 = 0
            r2 = -5
            goto L_0x123a
        L_0x093d:
            r1 = r4
            r6 = r1
            r2 = r59
        L_0x0941:
            if (r2 < 0) goto L_0x0a42
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x0a34, all -> 0x0a27 }
            android.media.MediaFormat r5 = r5.getTrackFormat(r2)     // Catch:{ Exception -> 0x0a34, all -> 0x0a27 }
            java.lang.String r7 = r5.getString(r13)     // Catch:{ Exception -> 0x0a34, all -> 0x0a27 }
            java.lang.String r8 = "audio/mp4a-latm"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x0a34, all -> 0x0a27 }
            if (r7 != 0) goto L_0x0964
            java.lang.String r7 = r5.getString(r13)     // Catch:{ Exception -> 0x092f, all -> 0x0923 }
            java.lang.String r8 = "audio/mpeg"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x092f, all -> 0x0923 }
            if (r7 == 0) goto L_0x0962
            goto L_0x0964
        L_0x0962:
            r7 = 0
            goto L_0x0965
        L_0x0964:
            r7 = 1
        L_0x0965:
            java.lang.String r8 = r5.getString(r13)     // Catch:{ Exception -> 0x0a34, all -> 0x0a27 }
            java.lang.String r9 = "audio/unknown"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x0a34, all -> 0x0a27 }
            if (r8 == 0) goto L_0x0972
            r2 = -1
        L_0x0972:
            if (r2 < 0) goto L_0x0a20
            if (r7 == 0) goto L_0x09d1
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x09be, all -> 0x09b9 }
            r9 = 1
            int r8 = r8.addTrack(r5, r9)     // Catch:{ Exception -> 0x09be, all -> 0x09b9 }
            android.media.MediaExtractor r10 = r15.extractor     // Catch:{ Exception -> 0x09be, all -> 0x09b9 }
            r10.selectTrack(r2)     // Catch:{ Exception -> 0x09be, all -> 0x09b9 }
            java.lang.String r10 = "max-input-size"
            int r5 = r5.getInteger(r10)     // Catch:{ Exception -> 0x0989, all -> 0x0923 }
            goto L_0x098f
        L_0x0989:
            r0 = move-exception
            r5 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ Exception -> 0x09be, all -> 0x09b9 }
            r5 = 0
        L_0x098f:
            if (r5 > 0) goto L_0x0993
            r5 = 65536(0x10000, float:9.18355E-41)
        L_0x0993:
            java.nio.ByteBuffer r10 = java.nio.ByteBuffer.allocateDirect(r5)     // Catch:{ Exception -> 0x09be, all -> 0x09b9 }
            r87 = r5
            r86 = r10
            r4 = 0
            r9 = r82
            int r11 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r11 <= 0) goto L_0x09aa
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x09ea, all -> 0x0923 }
            r13 = 0
            r11.seekTo(r9, r13)     // Catch:{ Exception -> 0x09ea, all -> 0x0923 }
            goto L_0x09b0
        L_0x09aa:
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x09ea, all -> 0x0923 }
            r13 = 0
            r11.seekTo(r4, r13)     // Catch:{ Exception -> 0x09ea, all -> 0x0923 }
        L_0x09b0:
            r4 = r84
            r23 = r86
            r11 = r87
            r13 = 0
            goto L_0x0a4c
        L_0x09b9:
            r0 = move-exception
            r9 = r82
            goto L_0x0924
        L_0x09be:
            r0 = move-exception
            r9 = r82
        L_0x09c1:
            r11 = r79
            r6 = r80
            r50 = r84
            r5 = r0
            r4 = r3
            r3 = r20
            r1 = 0
            r2 = -5
            r40 = 0
            goto L_0x12dd
        L_0x09d1:
            r9 = r82
            r8 = r5
            r4 = 0
            android.media.MediaExtractor r11 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0a1e, all -> 0x0a1c }
            r11.<init>()     // Catch:{ Exception -> 0x0a1e, all -> 0x0a1c }
            r11.setDataSource(r12)     // Catch:{ Exception -> 0x0a1e, all -> 0x0a1c }
            r11.selectTrack(r2)     // Catch:{ Exception -> 0x0a1e, all -> 0x0a1c }
            int r13 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r13 <= 0) goto L_0x09ec
            r13 = 0
            r11.seekTo(r9, r13)     // Catch:{ Exception -> 0x09ea, all -> 0x0923 }
            goto L_0x09f0
        L_0x09ea:
            r0 = move-exception
            goto L_0x09c1
        L_0x09ec:
            r13 = 0
            r11.seekTo(r4, r13)     // Catch:{ Exception -> 0x0a1e, all -> 0x0a1c }
        L_0x09f0:
            org.telegram.messenger.video.AudioRecoder r13 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x0a1e, all -> 0x0a1c }
            r13.<init>(r8, r11, r2)     // Catch:{ Exception -> 0x0a1e, all -> 0x0a1c }
            r13.startTime = r9     // Catch:{ Exception -> 0x0a0e, all -> 0x0a1c }
            r4 = r84
            r13.endTime = r4     // Catch:{ Exception -> 0x0a0c, all -> 0x0a0a }
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x0a0c, all -> 0x0a0a }
            android.media.MediaFormat r11 = r13.format     // Catch:{ Exception -> 0x0a0c, all -> 0x0a0a }
            r86 = r2
            r2 = 1
            int r8 = r8.addTrack(r11, r2)     // Catch:{ Exception -> 0x0a0c, all -> 0x0a0a }
            r2 = r86
            r11 = 0
            goto L_0x0a4a
        L_0x0a0a:
            r0 = move-exception
            goto L_0x0a2c
        L_0x0a0c:
            r0 = move-exception
            goto L_0x0a11
        L_0x0a0e:
            r0 = move-exception
            r4 = r84
        L_0x0a11:
            r11 = r79
            r6 = r80
            r50 = r4
            r40 = r13
            r1 = 0
            goto L_0x11f2
        L_0x0a1c:
            r0 = move-exception
            goto L_0x0a2a
        L_0x0a1e:
            r0 = move-exception
            goto L_0x0a37
        L_0x0a20:
            r9 = r82
            r4 = r84
            r86 = r2
            goto L_0x0a47
        L_0x0a27:
            r0 = move-exception
            r9 = r82
        L_0x0a2a:
            r4 = r84
        L_0x0a2c:
            r12 = r77
            r8 = r78
            r11 = r79
            goto L_0x1277
        L_0x0a34:
            r0 = move-exception
            r9 = r82
        L_0x0a37:
            r4 = r84
            r11 = r79
            r6 = r80
            r50 = r4
            r1 = 0
            goto L_0x120d
        L_0x0a42:
            r9 = r82
            r4 = r84
            r7 = 1
        L_0x0a47:
            r8 = -5
            r11 = 0
            r13 = 0
        L_0x0a4a:
            r23 = 0
        L_0x0a4c:
            if (r2 >= 0) goto L_0x0a51
            r26 = 1
            goto L_0x0a53
        L_0x0a51:
            r26 = 0
        L_0x0a53:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x11e3, all -> 0x11de }
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
        L_0x0a6f:
            if (r19 == 0) goto L_0x0a89
            if (r7 != 0) goto L_0x0a76
            if (r30 != 0) goto L_0x0a76
            goto L_0x0a89
        L_0x0a76:
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
            goto L_0x131d
        L_0x0a89:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x11c8, all -> 0x11b5 }
            if (r7 != 0) goto L_0x0ab7
            if (r13 == 0) goto L_0x0ab7
            org.telegram.messenger.video.MP4Builder r12 = r15.mediaMuxer     // Catch:{ Exception -> 0x0aa9, all -> 0x0a99 }
            boolean r12 = r13.step(r12, r8)     // Catch:{ Exception -> 0x0aa9, all -> 0x0a99 }
            r30 = r12
            goto L_0x0ab7
        L_0x0a99:
            r0 = move-exception
            r12 = r77
            r8 = r78
            r11 = r79
            r1 = r0
            r50 = r4
            r2 = r18
            r9 = r20
            goto L_0x05db
        L_0x0aa9:
            r0 = move-exception
            r11 = r79
            r6 = r80
            r50 = r4
            r40 = r13
        L_0x0ab2:
            r2 = r18
        L_0x0ab4:
            r1 = 0
            goto L_0x1239
        L_0x0ab7:
            if (r6 != 0) goto L_0x0c7f
            android.media.MediaExtractor r12 = r15.extractor     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c5e }
            int r12 = r12.getSampleTrackIndex()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c5e }
            r85 = r6
            r6 = r61
            if (r12 != r6) goto L_0x0b3d
            r86 = r13
            r46 = r14
            r13 = 2500(0x9c4, double:1.235E-320)
            int r12 = r3.dequeueInputBuffer(r13)     // Catch:{ Exception -> 0x0b2d, all -> 0x0a99 }
            if (r12 < 0) goto L_0x0b1d
            int r13 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b2d, all -> 0x0a99 }
            r14 = 21
            if (r13 >= r14) goto L_0x0ada
            r13 = r1[r12]     // Catch:{ Exception -> 0x0b2d, all -> 0x0a99 }
            goto L_0x0ade
        L_0x0ada:
            java.nio.ByteBuffer r13 = r3.getInputBuffer(r12)     // Catch:{ Exception -> 0x0b2d, all -> 0x0a99 }
        L_0x0ade:
            android.media.MediaExtractor r14 = r15.extractor     // Catch:{ Exception -> 0x0b2d, all -> 0x0a99 }
            r87 = r1
            r1 = 0
            int r54 = r14.readSampleData(r13, r1)     // Catch:{ Exception -> 0x0b11, all -> 0x0a99 }
            if (r54 >= 0) goto L_0x0afa
            r53 = 0
            r54 = 0
            r55 = 0
            r57 = 4
            r51 = r3
            r52 = r12
            r51.queueInputBuffer(r52, r53, r54, r55, r57)     // Catch:{ Exception -> 0x0b2d, all -> 0x0a99 }
            r1 = 1
            goto L_0x0b21
        L_0x0afa:
            r53 = 0
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0b2d, all -> 0x0a99 }
            long r55 = r1.getSampleTime()     // Catch:{ Exception -> 0x0b2d, all -> 0x0a99 }
            r57 = 0
            r51 = r3
            r52 = r12
            r51.queueInputBuffer(r52, r53, r54, r55, r57)     // Catch:{ Exception -> 0x0b2d, all -> 0x0a99 }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0b2d, all -> 0x0a99 }
            r1.advance()     // Catch:{ Exception -> 0x0b2d, all -> 0x0a99 }
            goto L_0x0b1f
        L_0x0b11:
            r0 = move-exception
            r11 = r79
            r40 = r86
            r50 = r4
            r61 = r6
            r2 = r18
            goto L_0x0b39
        L_0x0b1d:
            r87 = r1
        L_0x0b1f:
            r1 = r85
        L_0x0b21:
            r59 = r2
            r48 = r4
            r61 = r6
            r14 = r7
            r13 = r68
            r2 = r1
            goto L_0x0c3e
        L_0x0b2d:
            r0 = move-exception
            r11 = r79
            r40 = r86
            r50 = r4
            r61 = r6
            r2 = r18
            r1 = 0
        L_0x0b39:
            r6 = r80
            goto L_0x1239
        L_0x0b3d:
            r87 = r1
            r86 = r13
            r46 = r14
            if (r7 == 0) goto L_0x0c2e
            r1 = -1
            if (r2 == r1) goto L_0x0CLASSNAME
            if (r12 != r2) goto L_0x0c2e
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c1e, all -> 0x0c5e }
            r12 = 28
            if (r1 < r12) goto L_0x0b76
            android.media.MediaExtractor r12 = r15.extractor     // Catch:{ Exception -> 0x0b69, all -> 0x0a99 }
            long r12 = r12.getSampleSize()     // Catch:{ Exception -> 0x0b69, all -> 0x0a99 }
            r61 = r6
            r14 = r7
            long r6 = (long) r11
            int r48 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r48 <= 0) goto L_0x0b79
            r6 = 1024(0x400, double:5.06E-321)
            long r12 = r12 + r6
            int r11 = (int) r12
            java.nio.ByteBuffer r29 = java.nio.ByteBuffer.allocateDirect(r11)     // Catch:{ Exception -> 0x0b67, all -> 0x0a99 }
            goto L_0x0b79
        L_0x0b67:
            r0 = move-exception
            goto L_0x0b6c
        L_0x0b69:
            r0 = move-exception
            r61 = r6
        L_0x0b6c:
            r11 = r79
            r6 = r80
            r40 = r86
            r50 = r4
            goto L_0x0ab2
        L_0x0b76:
            r61 = r6
            r14 = r7
        L_0x0b79:
            r6 = r29
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x0c1a, all -> 0x0c5e }
            r12 = 0
            int r7 = r7.readSampleData(r6, r12)     // Catch:{ Exception -> 0x0c1a, all -> 0x0c5e }
            r13 = r68
            r13.size = r7     // Catch:{ Exception -> 0x0c1a, all -> 0x0c5e }
            r7 = 21
            if (r1 >= r7) goto L_0x0b92
            r6.position(r12)     // Catch:{ Exception -> 0x0b67, all -> 0x0a99 }
            int r1 = r13.size     // Catch:{ Exception -> 0x0b67, all -> 0x0a99 }
            r6.limit(r1)     // Catch:{ Exception -> 0x0b67, all -> 0x0a99 }
        L_0x0b92:
            int r1 = r13.size     // Catch:{ Exception -> 0x0c1a, all -> 0x0c5e }
            if (r1 < 0) goto L_0x0ba7
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0b67, all -> 0x0a99 }
            r7 = r2
            long r1 = r1.getSampleTime()     // Catch:{ Exception -> 0x0b67, all -> 0x0a99 }
            r13.presentationTimeUs = r1     // Catch:{ Exception -> 0x0b67, all -> 0x0a99 }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0b67, all -> 0x0a99 }
            r1.advance()     // Catch:{ Exception -> 0x0b67, all -> 0x0a99 }
            r1 = r85
            goto L_0x0bac
        L_0x0ba7:
            r7 = r2
            r1 = 0
            r13.size = r1     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c5e }
            r1 = 1
        L_0x0bac:
            int r2 = r13.size     // Catch:{ Exception -> 0x0c1a, all -> 0x0c5e }
            if (r2 <= 0) goto L_0x0bfa
            r41 = 0
            int r2 = (r4 > r41 ? 1 : (r4 == r41 ? 0 : -1))
            if (r2 < 0) goto L_0x0bbf
            r85 = r1
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0b67, all -> 0x0a99 }
            int r12 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r12 >= 0) goto L_0x0bfc
            goto L_0x0bc1
        L_0x0bbf:
            r85 = r1
        L_0x0bc1:
            r1 = 0
            r13.offset = r1     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c5e }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c5e }
            int r2 = r2.getSampleFlags()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c5e }
            r13.flags = r2     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c5e }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0c5e }
            r48 = r4
            long r4 = r2.writeSampleData(r8, r6, r13, r1)     // Catch:{ Exception -> 0x0bf8, all -> 0x0CLASSNAME }
            r1 = 0
            int r12 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r12 == 0) goto L_0x0bfe
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r15.callback     // Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
            if (r1 == 0) goto L_0x0bfe
            r2 = r6
            r59 = r7
            long r6 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
            long r51 = r6 - r9
            int r12 = (r51 > r33 ? 1 : (r51 == r33 ? 0 : -1))
            if (r12 <= 0) goto L_0x0beb
            long r33 = r6 - r9
        L_0x0beb:
            r6 = r33
            float r12 = (float) r6     // Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
            float r12 = r12 / r21
            float r12 = r12 / r22
            r1.didWriteData(r4, r12)     // Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
            r33 = r6
            goto L_0x0CLASSNAME
        L_0x0bf8:
            r0 = move-exception
            goto L_0x0c0a
        L_0x0bfa:
            r85 = r1
        L_0x0bfc:
            r48 = r4
        L_0x0bfe:
            r2 = r6
            r59 = r7
        L_0x0CLASSNAME:
            r29 = r2
            r1 = 0
            r2 = r85
            goto L_0x0c3f
        L_0x0CLASSNAME:
            r0 = move-exception
            r48 = r4
        L_0x0c0a:
            r11 = r79
            r6 = r80
            r40 = r86
            r5 = r0
            r4 = r3
            r2 = r18
            r3 = r20
            r50 = r48
            goto L_0x12dd
        L_0x0c1a:
            r0 = move-exception
            r48 = r4
            goto L_0x0c6e
        L_0x0c1e:
            r0 = move-exception
            r48 = r4
            r61 = r6
            goto L_0x0c6e
        L_0x0CLASSNAME:
            r59 = r2
            r48 = r4
            r61 = r6
            r14 = r7
            r13 = r68
            goto L_0x0CLASSNAME
        L_0x0c2e:
            r59 = r2
            r48 = r4
            r61 = r6
            r14 = r7
            r13 = r68
            r1 = -1
        L_0x0CLASSNAME:
            r2 = r85
            if (r12 != r1) goto L_0x0c3e
            r1 = 1
            goto L_0x0c3f
        L_0x0c3e:
            r1 = 0
        L_0x0c3f:
            if (r1 == 0) goto L_0x0c5c
            r4 = 2500(0x9c4, double:1.235E-320)
            int r52 = r3.dequeueInputBuffer(r4)     // Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
            if (r52 < 0) goto L_0x0c5c
            r53 = 0
            r54 = 0
            r55 = 0
            r57 = 4
            r51 = r3
            r51.queueInputBuffer(r52, r53, r54, r55, r57)     // Catch:{ Exception -> 0x0c5a, all -> 0x0CLASSNAME }
            r1 = 1
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = move-exception
            goto L_0x0CLASSNAME
        L_0x0c5a:
            r0 = move-exception
            goto L_0x0c6e
        L_0x0c5c:
            r1 = r2
            goto L_0x0CLASSNAME
        L_0x0c5e:
            r0 = move-exception
            r48 = r4
        L_0x0CLASSNAME:
            r12 = r77
            r8 = r78
            r11 = r79
            goto L_0x11bf
        L_0x0CLASSNAME:
            r0 = move-exception
            r48 = r4
            r86 = r13
        L_0x0c6e:
            r11 = r79
            r6 = r80
            r40 = r86
            r5 = r0
            r4 = r3
            r2 = r18
            r3 = r20
            r50 = r48
        L_0x0c7c:
            r1 = 0
            goto L_0x12dd
        L_0x0c7f:
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
        L_0x0ca4:
            if (r6 != 0) goto L_0x0cc4
            if (r2 == 0) goto L_0x0ca9
            goto L_0x0cc4
        L_0x0ca9:
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
            goto L_0x0a6f
        L_0x0cc4:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x11a6, all -> 0x1195 }
            if (r91 == 0) goto L_0x0cd6
            r48 = 22000(0x55f0, double:1.08694E-319)
            r85 = r6
            r69 = r7
            r8 = r80
            r6 = r48
            r48 = r69
            goto L_0x0cde
        L_0x0cd6:
            r85 = r6
            r48 = r7
            r6 = 2500(0x9c4, double:1.235E-320)
            r8 = r80
        L_0x0cde:
            int r6 = r8.dequeueOutputBuffer(r13, r6)     // Catch:{ Exception -> 0x1191, all -> 0x1195 }
            r7 = -1
            if (r6 != r7) goto L_0x0cfd
            r52 = r1
            r50 = r4
            r38 = r11
            r11 = r46
            r4 = r47
            r2 = r64
            r7 = r65
            r1 = -1
            r5 = 0
        L_0x0cf5:
            r69 = r19
            r19 = r14
            r14 = r69
            goto L_0x0var_
        L_0x0cfd:
            r7 = -3
            if (r6 != r7) goto L_0x0d33
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0d28, all -> 0x0d1e }
            r80 = r2
            r2 = 21
            if (r7 >= r2) goto L_0x0d0c
            java.nio.ByteBuffer[] r26 = r8.getOutputBuffers()     // Catch:{ Exception -> 0x0d28, all -> 0x0d1e }
        L_0x0d0c:
            r52 = r1
            r50 = r4
            r38 = r11
            r11 = r46
            r4 = r47
            r2 = r64
            r7 = r65
        L_0x0d1a:
            r1 = -1
            r5 = r80
            goto L_0x0cf5
        L_0x0d1e:
            r0 = move-exception
            r8 = r78
            r11 = r79
            r1 = r0
            r50 = r4
            goto L_0x119e
        L_0x0d28:
            r0 = move-exception
            r11 = r79
            r40 = r86
            r50 = r4
            r6 = r8
            r2 = r12
            goto L_0x0ab4
        L_0x0d33:
            r80 = r2
            r2 = -2
            if (r6 != r2) goto L_0x0db6
            android.media.MediaFormat r2 = r8.getOutputFormat()     // Catch:{ Exception -> 0x0d28, all -> 0x0d1e }
            r7 = -5
            if (r12 != r7) goto L_0x0da6
            if (r2 == 0) goto L_0x0da6
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ Exception -> 0x0d28, all -> 0x0d1e }
            r38 = r11
            r11 = 0
            int r7 = r7.addTrack(r2, r11)     // Catch:{ Exception -> 0x0d28, all -> 0x0d1e }
            r11 = r66
            boolean r12 = r2.containsKey(r11)     // Catch:{ Exception -> 0x0d97, all -> 0x0d84 }
            if (r12 == 0) goto L_0x0d79
            int r12 = r2.getInteger(r11)     // Catch:{ Exception -> 0x0d97, all -> 0x0d84 }
            r50 = r7
            r7 = 1
            if (r12 != r7) goto L_0x0d7b
            r7 = r65
            java.nio.ByteBuffer r12 = r2.getByteBuffer(r7)     // Catch:{ Exception -> 0x0d77, all -> 0x0d75 }
            r66 = r11
            r11 = r46
            java.nio.ByteBuffer r2 = r2.getByteBuffer(r11)     // Catch:{ Exception -> 0x0d77, all -> 0x0d75 }
            int r12 = r12.limit()     // Catch:{ Exception -> 0x0d77, all -> 0x0d75 }
            int r2 = r2.limit()     // Catch:{ Exception -> 0x0d77, all -> 0x0d75 }
            int r12 = r12 + r2
            r28 = r12
            goto L_0x0d81
        L_0x0d75:
            r0 = move-exception
            goto L_0x0d87
        L_0x0d77:
            r0 = move-exception
            goto L_0x0d9a
        L_0x0d79:
            r50 = r7
        L_0x0d7b:
            r66 = r11
            r11 = r46
            r7 = r65
        L_0x0d81:
            r12 = r50
            goto L_0x0dac
        L_0x0d84:
            r0 = move-exception
            r50 = r7
        L_0x0d87:
            r12 = r77
            r8 = r78
            r11 = r79
            r1 = r0
            r9 = r20
            r2 = r50
            r6 = 0
            r50 = r4
            goto L_0x13c9
        L_0x0d97:
            r0 = move-exception
            r50 = r7
        L_0x0d9a:
            r11 = r79
            r40 = r86
            r6 = r8
            r2 = r50
            r1 = 0
            r50 = r4
            goto L_0x1239
        L_0x0da6:
            r38 = r11
            r11 = r46
            r7 = r65
        L_0x0dac:
            r52 = r1
            r50 = r4
            r4 = r47
            r2 = r64
            goto L_0x0d1a
        L_0x0db6:
            r38 = r11
            r11 = r46
            r7 = r65
            if (r6 < 0) goto L_0x116a
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1191, all -> 0x1195 }
            r19 = r14
            r14 = 21
            if (r2 >= r14) goto L_0x0dc9
            r2 = r26[r6]     // Catch:{ Exception -> 0x0d28, all -> 0x0d1e }
            goto L_0x0dcd
        L_0x0dc9:
            java.nio.ByteBuffer r2 = r8.getOutputBuffer(r6)     // Catch:{ Exception -> 0x1191, all -> 0x1195 }
        L_0x0dcd:
            if (r2 == 0) goto L_0x1147
            int r14 = r13.size     // Catch:{ Exception -> 0x1191, all -> 0x1195 }
            r50 = r4
            r4 = 1
            if (r14 <= r4) goto L_0x0ef5
            int r4 = r13.flags     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r5 = r4 & 2
            if (r5 != 0) goto L_0x0e73
            if (r28 == 0) goto L_0x0dec
            r5 = r4 & 1
            if (r5 == 0) goto L_0x0dec
            int r5 = r13.offset     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            int r5 = r5 + r28
            r13.offset = r5     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            int r14 = r14 - r28
            r13.size = r14     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        L_0x0dec:
            if (r1 == 0) goto L_0x0e3a
            r4 = r4 & 1
            if (r4 == 0) goto L_0x0e3a
            int r1 = r13.size     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r4 = 100
            if (r1 <= r4) goto L_0x0e39
            int r1 = r13.offset     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r2.position(r1)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            byte[] r1 = new byte[r4]     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r2.get(r1)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r5 = 0
            r14 = 0
        L_0x0e04:
            r4 = 96
            if (r5 >= r4) goto L_0x0e39
            byte r4 = r1[r5]     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            if (r4 != 0) goto L_0x0e30
            int r4 = r5 + 1
            byte r4 = r1[r4]     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            if (r4 != 0) goto L_0x0e30
            int r4 = r5 + 2
            byte r4 = r1[r4]     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            if (r4 != 0) goto L_0x0e30
            int r4 = r5 + 3
            byte r4 = r1[r4]     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r46 = r1
            r1 = 1
            if (r4 != r1) goto L_0x0e32
            int r14 = r14 + 1
            if (r14 <= r1) goto L_0x0e32
            int r1 = r13.offset     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            int r1 = r1 + r5
            r13.offset = r1     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            int r1 = r13.size     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            int r1 = r1 - r5
            r13.size = r1     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            goto L_0x0e39
        L_0x0e30:
            r46 = r1
        L_0x0e32:
            int r5 = r5 + 1
            r1 = r46
            r4 = 100
            goto L_0x0e04
        L_0x0e39:
            r1 = 0
        L_0x0e3a:
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r14 = r1
            r5 = 1
            long r1 = r4.writeSampleData(r12, r2, r13, r5)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r4 = 0
            int r46 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r46 == 0) goto L_0x0e67
            org.telegram.messenger.MediaController$VideoConvertorListener r4 = r15.callback     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            if (r4 == 0) goto L_0x0e67
            r46 = r6
            long r5 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            long r52 = r5 - r9
            int r54 = (r52 > r33 ? 1 : (r52 == r33 ? 0 : -1))
            if (r54 <= 0) goto L_0x0e58
            long r33 = r5 - r9
        L_0x0e58:
            r5 = r33
            r52 = r14
            float r14 = (float) r5     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            float r14 = r14 / r21
            float r14 = r14 / r22
            r4.didWriteData(r1, r14)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r33 = r5
            goto L_0x0e6b
        L_0x0e67:
            r46 = r6
            r52 = r14
        L_0x0e6b:
            r4 = r47
            r1 = r63
            r2 = r64
            goto L_0x0efb
        L_0x0e73:
            r46 = r6
            r4 = -5
            if (r12 != r4) goto L_0x0ef1
            byte[] r4 = new byte[r14]     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            int r5 = r13.offset     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            int r5 = r5 + r14
            r2.limit(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            int r5 = r13.offset     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r2.position(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r2.get(r4)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            int r2 = r13.size     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r6 = 1
            int r2 = r2 - r6
        L_0x0e8c:
            if (r2 < 0) goto L_0x0ed0
            r5 = 3
            if (r2 <= r5) goto L_0x0ed0
            byte r14 = r4[r2]     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            if (r14 != r6) goto L_0x0ec8
            int r14 = r2 + -1
            byte r14 = r4[r14]     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            if (r14 != 0) goto L_0x0ec8
            int r14 = r2 + -2
            byte r14 = r4[r14]     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            if (r14 != 0) goto L_0x0ec8
            int r14 = r2 + -3
            byte r39 = r4[r14]     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            if (r39 != 0) goto L_0x0ec8
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r14)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            int r5 = r13.size     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            int r5 = r5 - r14
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocate(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r52 = r1
            r6 = 0
            java.nio.ByteBuffer r1 = r2.put(r4, r6, r14)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r1.position(r6)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            int r1 = r13.size     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            int r1 = r1 - r14
            java.nio.ByteBuffer r1 = r5.put(r4, r14, r1)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r1.position(r6)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r6 = r2
            goto L_0x0ed4
        L_0x0ec8:
            r52 = r1
            int r2 = r2 + -1
            r1 = r52
            r6 = 1
            goto L_0x0e8c
        L_0x0ed0:
            r52 = r1
            r5 = 0
            r6 = 0
        L_0x0ed4:
            r4 = r47
            r1 = r63
            r2 = r64
            android.media.MediaFormat r14 = android.media.MediaFormat.createVideoFormat(r4, r1, r2)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            if (r6 == 0) goto L_0x0ee8
            if (r5 == 0) goto L_0x0ee8
            r14.setByteBuffer(r7, r6)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r14.setByteBuffer(r11, r5)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
        L_0x0ee8:
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r6 = 0
            int r5 = r5.addTrack(r14, r6)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r12 = r5
            goto L_0x0efb
        L_0x0ef1:
            r52 = r1
            goto L_0x0e6b
        L_0x0ef5:
            r52 = r1
            r46 = r6
            goto L_0x0e6b
        L_0x0efb:
            int r5 = r13.flags     // Catch:{ Exception -> 0x1115, all -> 0x1110 }
            r5 = r5 & 4
            r6 = r46
            if (r5 == 0) goto L_0x0var_
            r5 = 1
            goto L_0x0var_
        L_0x0var_:
            r5 = 0
        L_0x0var_:
            r14 = 0
            r8.releaseOutputBuffer(r6, r14)     // Catch:{ Exception -> 0x1115, all -> 0x1110 }
            r63 = r1
            r14 = r5
            r1 = -1
            r5 = r80
        L_0x0var_:
            if (r6 == r1) goto L_0x0f2f
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
        L_0x0var_:
            r69 = r19
            r19 = r14
            r14 = r69
            goto L_0x0ca4
        L_0x0f2f:
            if (r23 != 0) goto L_0x111a
            r64 = r2
            r1 = 2500(0x9c4, double:1.235E-320)
            int r6 = r3.dequeueOutputBuffer(r13, r1)     // Catch:{ Exception -> 0x1115, all -> 0x1110 }
            r1 = -1
            if (r6 != r1) goto L_0x0f4c
            r1 = r84
            r47 = r4
            r56 = r5
            r55 = r11
            r4 = r58
            r5 = 0
            r6 = 0
            r11 = r79
            goto L_0x112f
        L_0x0f4c:
            r2 = -3
            if (r6 != r2) goto L_0x0var_
            goto L_0x111c
        L_0x0var_:
            r2 = -2
            if (r6 != r2) goto L_0x0var_
            android.media.MediaFormat r2 = r3.getOutputFormat()     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            if (r6 == 0) goto L_0x111c
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r6.<init>()     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            java.lang.String r1 = "newFormat = "
            r6.append(r1)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r6.append(r2)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            java.lang.String r1 = r6.toString()     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            goto L_0x111c
        L_0x0var_:
            r0 = move-exception
            r8 = r78
            r11 = r79
            goto L_0x119d
        L_0x0var_:
            r0 = move-exception
            r11 = r79
        L_0x0f7c:
            r40 = r86
            r5 = r0
            r4 = r3
            r6 = r8
            r2 = r12
            r3 = r20
            goto L_0x0c7c
        L_0x0var_:
            if (r6 < 0) goto L_0x10f4
            int r1 = r13.size     // Catch:{ Exception -> 0x1115, all -> 0x1110 }
            if (r1 == 0) goto L_0x0f8f
            r80 = 1
            goto L_0x0var_
        L_0x0f8f:
            r80 = 0
        L_0x0var_:
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1115, all -> 0x1110 }
            r41 = 0
            int r46 = (r50 > r41 ? 1 : (r50 == r41 ? 0 : -1))
            if (r46 <= 0) goto L_0x0fad
            int r46 = (r1 > r50 ? 1 : (r1 == r50 ? 0 : -1))
            if (r46 < 0) goto L_0x0fad
            r47 = r4
            int r4 = r13.flags     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r4 = r4 | 4
            r13.flags = r4     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r4 = 1
            r23 = 1
            r41 = 0
            r46 = 0
            goto L_0x0fb5
        L_0x0fad:
            r47 = r4
            r46 = r80
            r4 = r84
            r41 = 0
        L_0x0fb5:
            int r53 = (r31 > r41 ? 1 : (r31 == r41 ? 0 : -1))
            if (r53 < 0) goto L_0x102d
            r80 = r4
            int r4 = r13.flags     // Catch:{ Exception -> 0x0var_, all -> 0x1110 }
            r4 = r4 & 4
            if (r4 == 0) goto L_0x102f
            long r53 = r31 - r9
            long r53 = java.lang.Math.abs(r53)     // Catch:{ Exception -> 0x0var_, all -> 0x1110 }
            r4 = 1000000(0xvar_, float:1.401298E-39)
            r55 = r11
            r11 = r79
            int r4 = r4 / r11
            r56 = r5
            long r4 = (long) r4
            int r57 = (r53 > r4 ? 1 : (r53 == r4 ? 0 : -1))
            if (r57 <= 0) goto L_0x1024
            r4 = 0
            int r23 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r23 <= 0) goto L_0x0fe4
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
            r5 = 0
            r4.seekTo(r9, r5)     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
            r5 = 0
            goto L_0x0fec
        L_0x0fe4:
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x102a, all -> 0x1027 }
            r5 = 0
            r9 = 0
            r4.seekTo(r9, r5)     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
        L_0x0fec:
            long r35 = r48 + r24
            int r4 = r13.flags     // Catch:{ Exception -> 0x1014, all -> 0x1004 }
            r9 = -5
            r4 = r4 & r9
            r13.flags = r4     // Catch:{ Exception -> 0x1014, all -> 0x1004 }
            r3.flush()     // Catch:{ Exception -> 0x1014, all -> 0x1004 }
            r50 = r31
            r4 = 1
            r10 = 0
            r23 = 0
            r41 = 0
            r46 = 0
            r31 = r16
            goto L_0x103c
        L_0x1004:
            r0 = move-exception
            r8 = r78
            r1 = r0
            r2 = r12
            r9 = r20
            r50 = r31
            r6 = 0
            r12 = r77
            r31 = r16
            goto L_0x13c9
        L_0x1014:
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
            goto L_0x12dd
        L_0x1024:
            r5 = 0
            r9 = -5
            goto L_0x1037
        L_0x1027:
            r0 = move-exception
            goto L_0x119a
        L_0x102a:
            r0 = move-exception
            goto L_0x0f7c
        L_0x102d:
            r80 = r4
        L_0x102f:
            r56 = r5
            r55 = r11
            r5 = 0
            r9 = -5
            r11 = r79
        L_0x1037:
            r10 = r80
            r4 = 0
            r41 = 0
        L_0x103c:
            int r18 = (r31 > r41 ? 1 : (r31 == r41 ? 0 : -1))
            r80 = r10
            if (r18 < 0) goto L_0x1045
            r9 = r31
            goto L_0x1047
        L_0x1045:
            r9 = r82
        L_0x1047:
            int r43 = (r9 > r41 ? 1 : (r9 == r41 ? 0 : -1))
            if (r43 <= 0) goto L_0x1084
            int r43 = (r44 > r16 ? 1 : (r44 == r16 ? 0 : -1))
            if (r43 != 0) goto L_0x1084
            int r43 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r43 >= 0) goto L_0x1077
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
            if (r1 == 0) goto L_0x1075
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
            r1.<init>()     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
            java.lang.String r2 = "drop frame startTime = "
            r1.append(r2)     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
            r1.append(r9)     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
            java.lang.String r2 = " present time = "
            r1.append(r2)     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
            long r9 = r13.presentationTimeUs     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
            r1.append(r9)     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
        L_0x1075:
            r1 = 0
            goto L_0x1086
        L_0x1077:
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
            r9 = -2147483648(0xfffffffvar_, double:NaN)
            int r43 = (r48 > r9 ? 1 : (r48 == r9 ? 0 : -1))
            if (r43 == 0) goto L_0x1082
            long r35 = r35 - r1
        L_0x1082:
            r44 = r1
        L_0x1084:
            r1 = r46
        L_0x1086:
            if (r4 == 0) goto L_0x108b
            r44 = r16
            goto L_0x109e
        L_0x108b:
            int r2 = (r31 > r16 ? 1 : (r31 == r16 ? 0 : -1))
            if (r2 != 0) goto L_0x109b
            r9 = 0
            int r2 = (r35 > r9 ? 1 : (r35 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x109b
            long r9 = r13.presentationTimeUs     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
            long r9 = r9 + r35
            r13.presentationTimeUs = r9     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
        L_0x109b:
            r3.releaseOutputBuffer(r6, r1)     // Catch:{ Exception -> 0x10f1, all -> 0x1188 }
        L_0x109e:
            if (r1 == 0) goto L_0x10d0
            r1 = 0
            int r4 = (r31 > r1 ? 1 : (r31 == r1 ? 0 : -1))
            if (r4 < 0) goto L_0x10af
            long r9 = r13.presentationTimeUs     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
            r1 = r48
            long r1 = java.lang.Math.max(r1, r9)     // Catch:{ Exception -> 0x102a, all -> 0x1188 }
            goto L_0x10b1
        L_0x10af:
            r1 = r48
        L_0x10b1:
            r27.awaitNewImage()     // Catch:{ Exception -> 0x10b6, all -> 0x1188 }
            r4 = 0
            goto L_0x10bc
        L_0x10b6:
            r0 = move-exception
            r4 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ Exception -> 0x10f1, all -> 0x1188 }
            r4 = 1
        L_0x10bc:
            if (r4 != 0) goto L_0x10d2
            r27.drawImage()     // Catch:{ Exception -> 0x10f1, all -> 0x1188 }
            long r9 = r13.presentationTimeUs     // Catch:{ Exception -> 0x10f1, all -> 0x1188 }
            r48 = 1000(0x3e8, double:4.94E-321)
            long r9 = r9 * r48
            r4 = r58
            r4.setPresentationTime(r9)     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            r4.swapBuffers()     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            goto L_0x10d4
        L_0x10d0:
            r1 = r48
        L_0x10d2:
            r4 = r58
        L_0x10d4:
            int r6 = r13.flags     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            r6 = r6 & 4
            if (r6 == 0) goto L_0x10ea
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            if (r6 == 0) goto L_0x10e3
            java.lang.String r6 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
        L_0x10e3:
            r8.signalEndOfInputStream()     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            r48 = r1
            r6 = 0
            goto L_0x10ee
        L_0x10ea:
            r6 = r85
            r48 = r1
        L_0x10ee:
            r1 = r80
            goto L_0x112f
        L_0x10f1:
            r0 = move-exception
            goto L_0x11ad
        L_0x10f4:
            r11 = r79
            r4 = r58
            r5 = 0
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            r2.<init>()     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            java.lang.String r7 = "unexpected result from decoder.dequeueOutputBuffer: "
            r2.append(r7)     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            r2.append(r6)     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            throw r1     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
        L_0x1110:
            r0 = move-exception
            r11 = r79
            goto L_0x119a
        L_0x1115:
            r0 = move-exception
            r11 = r79
            goto L_0x11ad
        L_0x111a:
            r64 = r2
        L_0x111c:
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
        L_0x112f:
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
            goto L_0x0var_
        L_0x1147:
            r11 = r79
            r50 = r4
            r4 = r58
            r5 = 0
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            r2.<init>()     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            java.lang.String r7 = "encoderOutputBuffer "
            r2.append(r7)     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            r2.append(r6)     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            java.lang.String r6 = " was null"
            r2.append(r6)     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            throw r1     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
        L_0x116a:
            r11 = r79
            r50 = r4
            r4 = r58
            r5 = 0
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            r2.<init>()     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            java.lang.String r7 = "unexpected result from encoder.dequeueOutputBuffer: "
            r2.append(r7)     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            r2.append(r6)     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
            throw r1     // Catch:{ Exception -> 0x118a, all -> 0x1188 }
        L_0x1188:
            r0 = move-exception
            goto L_0x119b
        L_0x118a:
            r0 = move-exception
            r40 = r86
            r5 = r0
            r58 = r4
            goto L_0x11b2
        L_0x1191:
            r0 = move-exception
            r11 = r79
            goto L_0x11ab
        L_0x1195:
            r0 = move-exception
            r11 = r79
            r50 = r4
        L_0x119a:
            r5 = 0
        L_0x119b:
            r8 = r78
        L_0x119d:
            r1 = r0
        L_0x119e:
            r2 = r12
            r9 = r20
            r6 = 0
            r12 = r77
            goto L_0x13c9
        L_0x11a6:
            r0 = move-exception
            r11 = r79
            r8 = r80
        L_0x11ab:
            r50 = r4
        L_0x11ad:
            r4 = r58
            r40 = r86
            r5 = r0
        L_0x11b2:
            r6 = r8
            r2 = r12
            goto L_0x11db
        L_0x11b5:
            r0 = move-exception
            r11 = r79
            r48 = r4
            r5 = 0
            r12 = r77
            r8 = r78
        L_0x11bf:
            r1 = r0
            r2 = r18
            r9 = r20
            r50 = r48
            goto L_0x05db
        L_0x11c8:
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
        L_0x11db:
            r1 = 0
            goto L_0x123a
        L_0x11de:
            r0 = move-exception
            r11 = r79
            goto L_0x1272
        L_0x11e3:
            r0 = move-exception
            r11 = r79
            r8 = r80
            r86 = r13
            r2 = r58
            r1 = 0
            r40 = r86
            r50 = r4
            r6 = r8
        L_0x11f2:
            r2 = -5
            goto L_0x1239
        L_0x11f4:
            r0 = move-exception
            r11 = r79
            r8 = r80
            r4 = r84
            r2 = r58
            goto L_0x1209
        L_0x11fe:
            r0 = move-exception
            r11 = r79
            r8 = r80
            r4 = r84
            r2 = r58
            r3 = r60
        L_0x1209:
            r1 = 0
            r50 = r4
            r6 = r8
        L_0x120d:
            r2 = -5
            goto L_0x1237
        L_0x120f:
            r0 = move-exception
            r11 = r79
            r4 = r84
            goto L_0x1272
        L_0x1216:
            r0 = move-exception
            r11 = r79
            r8 = r80
            r4 = r84
            r2 = r58
            r3 = r60
            r1 = 0
            goto L_0x1231
        L_0x1223:
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
        L_0x1231:
            r50 = r4
            r6 = r8
            r2 = -5
            r27 = 0
        L_0x1237:
            r40 = 0
        L_0x1239:
            r5 = r0
        L_0x123a:
            r4 = r3
            r3 = r20
            goto L_0x12dd
        L_0x123f:
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
            goto L_0x12dc
        L_0x1259:
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
            goto L_0x12d6
        L_0x126c:
            r0 = move-exception
            r4 = r84
            r31 = r7
            r11 = r14
        L_0x1272:
            r1 = 0
            r12 = r77
            r8 = r78
        L_0x1277:
            r1 = r0
            r50 = r4
        L_0x127a:
            r9 = r20
            goto L_0x05da
        L_0x127e:
            r0 = move-exception
            r4 = r84
            r31 = r7
            r11 = r14
            r61 = r24
            r1 = 0
            r50 = r4
            r3 = r20
            goto L_0x12d3
        L_0x128c:
            r0 = move-exception
            r4 = r84
            r31 = r7
            r11 = r14
            goto L_0x12a3
        L_0x1293:
            r0 = move-exception
            r4 = r84
            r31 = r7
            r11 = r14
            r61 = r24
            goto L_0x12d1
        L_0x129c:
            r0 = move-exception
            r4 = r84
            r31 = r7
            r11 = r14
            r1 = 0
        L_0x12a3:
            r12 = r77
            r8 = r78
            r1 = r0
            r9 = r3
            goto L_0x12c2
        L_0x12aa:
            r0 = move-exception
            r4 = r84
            r31 = r7
            r11 = r14
            r61 = r24
            r1 = 0
            goto L_0x12d1
        L_0x12b4:
            r0 = move-exception
            r4 = r84
            r11 = r14
            r1 = 0
            r12 = r77
            r8 = r78
            r9 = r80
            r31 = r86
            r1 = r0
        L_0x12c2:
            r50 = r4
            goto L_0x05da
        L_0x12c6:
            r0 = move-exception
            r61 = r4
            r11 = r14
            r1 = 0
            r4 = r84
            r3 = r80
            r31 = r86
        L_0x12d1:
            r50 = r4
        L_0x12d3:
            r2 = -5
            r4 = 0
            r6 = 0
        L_0x12d6:
            r27 = 0
            r40 = 0
            r58 = 0
        L_0x12dc:
            r5 = r0
        L_0x12dd:
            boolean r7 = r5 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x134c }
            if (r7 == 0) goto L_0x12e4
            if (r91 != 0) goto L_0x12e4
            r1 = 1
        L_0x12e4:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x1343 }
            r7.<init>()     // Catch:{ all -> 0x1343 }
            java.lang.String r8 = "bitrate: "
            r7.append(r8)     // Catch:{ all -> 0x1343 }
            r7.append(r3)     // Catch:{ all -> 0x1343 }
            java.lang.String r8 = " framerate: "
            r7.append(r8)     // Catch:{ all -> 0x1343 }
            r7.append(r11)     // Catch:{ all -> 0x1343 }
            java.lang.String r8 = " size: "
            r7.append(r8)     // Catch:{ all -> 0x1343 }
            r8 = r78
            r7.append(r8)     // Catch:{ all -> 0x133f }
            java.lang.String r9 = "x"
            r7.append(r9)     // Catch:{ all -> 0x133f }
            r9 = r77
            r7.append(r9)     // Catch:{ all -> 0x133d }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x133d }
            org.telegram.messenger.FileLog.e((java.lang.String) r7)     // Catch:{ all -> 0x133d }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ all -> 0x133d }
            r18 = r2
            r2 = r4
            r4 = r6
            r6 = r1
            r1 = 1
        L_0x131d:
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ all -> 0x1337 }
            r7 = r61
            r5.unselectTrack(r7)     // Catch:{ all -> 0x1337 }
            if (r2 == 0) goto L_0x132c
            r2.stop()     // Catch:{ all -> 0x1337 }
            r2.release()     // Catch:{ all -> 0x1337 }
        L_0x132c:
            r5 = r1
            r7 = r6
            r2 = r18
            r6 = r27
            r1 = r40
            r40 = r58
            goto L_0x136b
        L_0x1337:
            r0 = move-exception
            r1 = r0
            r12 = r9
            r2 = r18
            goto L_0x1375
        L_0x133d:
            r0 = move-exception
            goto L_0x1348
        L_0x133f:
            r0 = move-exception
            r9 = r77
            goto L_0x1348
        L_0x1343:
            r0 = move-exception
            r9 = r77
            r8 = r78
        L_0x1348:
            r6 = r1
            r12 = r9
            r1 = r0
            goto L_0x1375
        L_0x134c:
            r0 = move-exception
            r9 = r77
            r8 = r78
            r1 = r0
            r12 = r9
            r6 = 0
            goto L_0x1375
        L_0x1355:
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
        L_0x136b:
            if (r6 == 0) goto L_0x1377
            r6.release()     // Catch:{ all -> 0x1371 }
            goto L_0x1377
        L_0x1371:
            r0 = move-exception
            r1 = r0
            r6 = r7
            r12 = r9
        L_0x1375:
            r9 = r3
            goto L_0x13c9
        L_0x1377:
            if (r40 == 0) goto L_0x137c
            r40.release()     // Catch:{ all -> 0x1371 }
        L_0x137c:
            if (r4 == 0) goto L_0x1384
            r4.stop()     // Catch:{ all -> 0x1371 }
            r4.release()     // Catch:{ all -> 0x1371 }
        L_0x1384:
            if (r1 == 0) goto L_0x1389
            r1.release()     // Catch:{ all -> 0x1371 }
        L_0x1389:
            r72.checkConversionCanceled()     // Catch:{ all -> 0x1371 }
            r1 = r5
            r6 = r7
        L_0x138e:
            android.media.MediaExtractor r4 = r15.extractor
            if (r4 == 0) goto L_0x1395
            r4.release()
        L_0x1395:
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer
            if (r4 == 0) goto L_0x13aa
            r4.finishMovie()     // Catch:{ all -> 0x13a5 }
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ all -> 0x13a5 }
            long r4 = r4.getLastFrameTimestamp(r2)     // Catch:{ all -> 0x13a5 }
            r15.endPresentationTime = r4     // Catch:{ all -> 0x13a5 }
            goto L_0x13aa
        L_0x13a5:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x13aa:
            r10 = r3
            r7 = r8
            r13 = r50
            goto L_0x141a
        L_0x13b0:
            r0 = move-exception
            r8 = r78
            r4 = r84
            r11 = r10
            r9 = r12
            r1 = 0
            goto L_0x13c0
        L_0x13b9:
            r0 = move-exception
            r4 = r84
            r8 = r11
            r9 = r12
            r1 = 0
            r11 = r10
        L_0x13c0:
            r31 = r86
            r1 = r0
            r50 = r4
            r2 = -5
            r6 = 0
            r9 = r80
        L_0x13c9:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x1445 }
            r3.<init>()     // Catch:{ all -> 0x1445 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x1445 }
            r3.append(r9)     // Catch:{ all -> 0x1445 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x1445 }
            r3.append(r11)     // Catch:{ all -> 0x1445 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x1445 }
            r3.append(r8)     // Catch:{ all -> 0x1445 }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x1445 }
            r3.append(r12)     // Catch:{ all -> 0x1445 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x1445 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x1445 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x1445 }
            android.media.MediaExtractor r1 = r15.extractor
            if (r1 == 0) goto L_0x13ff
            r1.release()
        L_0x13ff:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer
            if (r1 == 0) goto L_0x1414
            r1.finishMovie()     // Catch:{ all -> 0x140f }
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ all -> 0x140f }
            long r1 = r1.getLastFrameTimestamp(r2)     // Catch:{ all -> 0x140f }
            r15.endPresentationTime = r1     // Catch:{ all -> 0x140f }
            goto L_0x1414
        L_0x140f:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1414:
            r7 = r8
            r10 = r9
            r9 = r12
            r13 = r50
            r1 = 1
        L_0x141a:
            if (r6 == 0) goto L_0x1444
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
        L_0x1444:
            return r1
        L_0x1445:
            r0 = move-exception
            r1 = r72
            r3 = r0
            android.media.MediaExtractor r4 = r1.extractor
            if (r4 == 0) goto L_0x1450
            r4.release()
        L_0x1450:
            org.telegram.messenger.video.MP4Builder r4 = r1.mediaMuxer
            if (r4 == 0) goto L_0x1465
            r4.finishMovie()     // Catch:{ all -> 0x1460 }
            org.telegram.messenger.video.MP4Builder r4 = r1.mediaMuxer     // Catch:{ all -> 0x1460 }
            long r4 = r4.getLastFrameTimestamp(r2)     // Catch:{ all -> 0x1460 }
            r1.endPresentationTime = r4     // Catch:{ all -> 0x1460 }
            goto L_0x1465
        L_0x1460:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x1465:
            goto L_0x1467
        L_0x1466:
            throw r3
        L_0x1467:
            goto L_0x1466
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
