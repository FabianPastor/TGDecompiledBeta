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

    public boolean convertVideo(String str, File file, int i, boolean z, int i2, int i3, int i4, int i5, int i6, int i7, int i8, long j, long j2, long j3, boolean z2, long j4, MediaController.SavedFilterState savedFilterState, String str2, ArrayList<VideoEditedInfo.MediaEntity> arrayList, boolean z3, MediaController.CropState cropState, MediaController.VideoConvertorListener videoConvertorListener) {
        String str3 = str;
        long j5 = j4;
        this.callback = videoConvertorListener;
        return convertVideoInternal(str, file, i, z, i2, i3, i4, i5, i6, i7, i8, j, j2, j3, j5, z2, false, savedFilterState, str2, arrayList, z3, cropState);
    }

    public long getLastFrameTimestamp() {
        return this.endPresentationTime;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v26, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v0, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v66, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v68, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v43, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v57, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v97, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v71, resolved type: org.telegram.messenger.video.MP4Builder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v99, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v111, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v84, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v88, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v97, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v98, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v99, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v127, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v128, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v129, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v132, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v133, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v134, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v135, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v95, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v95, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v96, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v96, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v137, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v97, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v98, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v99, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v100, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v104, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v101, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v102, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v103, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v104, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v105, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v106, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v107, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v108, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v109, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v110, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v111, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v112, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v113, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v114, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v115, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v80, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v116, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v117, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v118, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v119, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v120, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v121, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v122, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v123, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v124, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v125, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v126, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v92, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v127, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v128, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v138, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v242, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v246, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v248, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v36, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v139, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v140, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v255, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v256, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v257, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v258, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v259, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v261, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v262, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v263, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v264, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v266, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v146, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v148, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v149, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v270, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v278, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v280, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v283, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v284, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v150, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v151, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v153, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v154, resolved type: int} */
    /* JADX WARNING: type inference failed for: r4v48 */
    /* JADX WARNING: type inference failed for: r4v49 */
    /* JADX WARNING: type inference failed for: r4v124 */
    /* JADX WARNING: Code restructure failed: missing block: B:1001:0x11f3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1002:0x11f4, code lost:
        r32 = r84;
        r4 = r5;
        r11 = r14;
        r55 = r22;
        r5 = r80;
        r2 = r0;
        r7 = r4;
        r44 = r82;
        r1 = r26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1003:0x1206, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1004:0x1207, code lost:
        r5 = r80;
        r32 = r84;
        r11 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1005:0x120c, code lost:
        r13 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1009:0x1219, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1010:0x121a, code lost:
        r5 = r80;
        r32 = r84;
        r11 = r14;
        r55 = r22;
        r2 = r0;
        r44 = r82;
        r1 = r26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1032:0x128b, code lost:
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1098:0x13c5, code lost:
        r2.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x022d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1102:?, code lost:
        r2.finishMovie();
        r15.endPresentationTime = r15.mediaMuxer.getLastFrameTimestamp(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1103:0x13d8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1104:0x13d9, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x03af, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x03b0, code lost:
        r11 = r77;
        r5 = r80;
        r44 = r82;
        r32 = r84;
        r2 = r0;
        r1 = r12;
        r10 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x0408, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x0409, code lost:
        r36 = r10;
        r2 = r0;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x040e, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x040f, code lost:
        r36 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x0411, code lost:
        r11 = r77;
        r5 = r80;
        r44 = r82;
        r32 = r84;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x041c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x041d, code lost:
        r36 = r10;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x0456, code lost:
        r22 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x0630, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x063e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x063f, code lost:
        r44 = r82;
        r2 = r0;
        r32 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x07b6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x07b7, code lost:
        r8 = r75;
        r9 = r76;
        r11 = r77;
        r5 = r80;
        r44 = r82;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x07c4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x07c5, code lost:
        r11 = r77;
        r7 = r78;
        r5 = r80;
        r44 = r82;
        r2 = r0;
        r10 = r3;
        r59 = null;
        r25 = r14;
        r1 = r26;
        r40 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x0854, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x0855, code lost:
        r52 = r3;
        r25 = r14;
        r11 = r77;
        r7 = r78;
        r5 = r80;
        r44 = r82;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x08c9, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x08cb, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x012a, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0aa3, code lost:
        if (r12.presentationTimeUs < r13) goto L_0x0aa5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0173, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0174, code lost:
        r2 = r0;
        r34 = r3;
        r36 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x019d, code lost:
        r11 = r12;
        r12 = r18;
        r18 = r19;
        r19 = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x0fdf, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x0fe0, code lost:
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x1013, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x1015, code lost:
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r13 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x1049, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:0x104a, code lost:
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x1109, code lost:
        r0 = th;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:0x1113, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:962:0x1114, code lost:
        r59 = r85;
        r2 = r0;
        r49 = r7;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:965:0x111e, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:966:0x111f, code lost:
        r11 = r77;
        r40 = r1;
        r5 = r6;
        r44 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:969:0x112d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:0x112e, code lost:
        r11 = r77;
        r4 = r78;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:986:0x1196, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:987:0x1197, code lost:
        r11 = r77;
        r4 = r78;
        r5 = r80;
        r25 = r14;
        r7 = r49;
        r10 = r52;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:989:0x11ab, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:990:0x11ac, code lost:
        r11 = r77;
        r5 = r80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x11b2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:992:0x11b3, code lost:
        r11 = r77;
        r4 = r78;
        r5 = r80;
        r13 = r82;
        r7 = r49;
        r10 = r52;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:993:0x11c1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:994:0x11c2, code lost:
        r32 = r84;
        r7 = r2;
        r10 = r3;
        r4 = r5;
        r11 = r14;
        r55 = r22;
        r5 = r80;
        r13 = r82;
        r2 = r0;
        r49 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:995:0x11d1, code lost:
        r44 = r13;
        r1 = r26;
        r11 = r11;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1003:0x1206 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:400:0x0722] */
    /* JADX WARNING: Removed duplicated region for block: B:1031:0x1289 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:1046:0x12d4 A[Catch:{ all -> 0x12e4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1060:0x130c  */
    /* JADX WARNING: Removed duplicated region for block: B:1062:0x1325 A[SYNTHETIC, Splitter:B:1062:0x1325] */
    /* JADX WARNING: Removed duplicated region for block: B:1067:0x1336 A[Catch:{ all -> 0x1329 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1069:0x133b A[Catch:{ all -> 0x1329 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1071:0x1343 A[Catch:{ all -> 0x1329 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1076:0x1354  */
    /* JADX WARNING: Removed duplicated region for block: B:1079:0x135b A[SYNTHETIC, Splitter:B:1079:0x135b] */
    /* JADX WARNING: Removed duplicated region for block: B:1098:0x13c5  */
    /* JADX WARNING: Removed duplicated region for block: B:1101:0x13cc A[SYNTHETIC, Splitter:B:1101:0x13cc] */
    /* JADX WARNING: Removed duplicated region for block: B:1107:0x13e3  */
    /* JADX WARNING: Removed duplicated region for block: B:1109:0x1411  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x03af A[ExcHandler: all (r0v161 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:83:0x01bb] */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x040e A[ExcHandler: all (th java.lang.Throwable), Splitter:B:46:0x0101] */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0454 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x0494 A[SYNTHETIC, Splitter:B:249:0x0494] */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x04a4 A[Catch:{ all -> 0x0498 }] */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x0596  */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x063e A[ExcHandler: Exception (r0v127 'e' java.lang.Exception A[CUSTOM_DECLARE]), Splitter:B:337:0x0620] */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x0690  */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x06b7  */
    /* JADX WARNING: Removed duplicated region for block: B:383:0x06c1  */
    /* JADX WARNING: Removed duplicated region for block: B:386:0x06e0  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0706 A[SYNTHETIC, Splitter:B:391:0x0706] */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x071e  */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x07ad A[SYNTHETIC, Splitter:B:420:0x07ad] */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x07b6 A[ExcHandler: all (r0v120 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:420:0x07ad] */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x07d9  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x07df A[SYNTHETIC, Splitter:B:429:0x07df] */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x080d  */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x0810  */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x08c9 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:467:0x0873] */
    /* JADX WARNING: Removed duplicated region for block: B:504:0x08f6  */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x092c  */
    /* JADX WARNING: Removed duplicated region for block: B:514:0x093d  */
    /* JADX WARNING: Removed duplicated region for block: B:515:0x0940  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x012a A[ExcHandler: all (th java.lang.Throwable), Splitter:B:49:0x0105] */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x095f A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:526:0x097d A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x09b0  */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0b93  */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0bbb A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0bdf  */
    /* JADX WARNING: Removed duplicated region for block: B:650:0x0be8  */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0bfb  */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:791:0x0e2a A[Catch:{ Exception -> 0x10b6, all -> 0x10af }] */
    /* JADX WARNING: Removed duplicated region for block: B:792:0x0e2c A[Catch:{ Exception -> 0x10b6, all -> 0x10af }] */
    /* JADX WARNING: Removed duplicated region for block: B:796:0x0e38  */
    /* JADX WARNING: Removed duplicated region for block: B:797:0x0e55  */
    /* JADX WARNING: Removed duplicated region for block: B:884:0x0f9d  */
    /* JADX WARNING: Removed duplicated region for block: B:885:0x0fa0  */
    /* JADX WARNING: Removed duplicated region for block: B:892:0x0fad A[SYNTHETIC, Splitter:B:892:0x0fad] */
    /* JADX WARNING: Removed duplicated region for block: B:897:0x0fd1 A[Catch:{ Exception -> 0x0fdf, all -> 0x1109 }] */
    /* JADX WARNING: Removed duplicated region for block: B:905:0x0fe5 A[Catch:{ Exception -> 0x0fdf, all -> 0x1109 }] */
    /* JADX WARNING: Removed duplicated region for block: B:906:0x0fe8 A[Catch:{ Exception -> 0x0fdf, all -> 0x1109 }] */
    /* JADX WARNING: Removed duplicated region for block: B:914:0x0ffd  */
    /* JADX WARNING: Removed duplicated region for block: B:931:0x102d A[Catch:{ Exception -> 0x1113, all -> 0x1109 }] */
    /* JADX WARNING: Removed duplicated region for block: B:935:0x1037 A[Catch:{ Exception -> 0x1113, all -> 0x1109 }] */
    /* JADX WARNING: Removed duplicated region for block: B:959:0x1109 A[ExcHandler: all (th java.lang.Throwable), PHI: r11 r32 r40 r44 
      PHI: (r11v68 java.lang.String) = (r11v252 java.lang.String), (r11v255 int), (r11v257 int), (r11v258 int), (r11v259 int), (r11v261 int), (r11v262 int), (r11v264 int), (r11v266 int), (r11v278 int), (r11v280 int), (r11v284 java.lang.String) binds: [B:799:0x0e5b, B:919:0x100e, B:929:0x1026, B:924:0x1015, B:920:?, B:916:0x1003, B:911:0x0ff8, B:912:?, B:892:0x0fad, B:856:0x0var_, B:857:?, B:788:0x0e24] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r32v46 long) = (r32v41 long), (r32v52 long), (r32v52 long), (r32v52 long), (r32v52 long), (r32v52 long), (r32v52 long), (r32v52 long), (r32v52 long), (r32v41 long), (r32v41 long), (r32v41 long) binds: [B:799:0x0e5b, B:919:0x100e, B:929:0x1026, B:924:0x1015, B:920:?, B:916:0x1003, B:911:0x0ff8, B:912:?, B:892:0x0fad, B:856:0x0var_, B:857:?, B:788:0x0e24] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r40v29 int) = (r40v22 int), (r40v41 int), (r40v41 int), (r40v41 int), (r40v41 int), (r40v41 int), (r40v41 int), (r40v41 int), (r40v41 int), (r40v50 int), (r40v50 int), (r40v22 int) binds: [B:799:0x0e5b, B:919:0x100e, B:929:0x1026, B:924:0x1015, B:920:?, B:916:0x1003, B:911:0x0ff8, B:912:?, B:892:0x0fad, B:856:0x0var_, B:857:?, B:788:0x0e24] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r44v39 long) = (r44v46 long), (r44v49 long), (r44v49 long), (r44v49 long), (r44v49 long), (r44v49 long), (r44v49 long), (r44v49 long), (r44v49 long), (r44v46 long), (r44v46 long), (r44v56 long) binds: [B:799:0x0e5b, B:919:0x100e, B:929:0x1026, B:924:0x1015, B:920:?, B:916:0x1003, B:911:0x0ff8, B:912:?, B:892:0x0fad, B:856:0x0var_, B:857:?, B:788:0x0e24] A[DONT_GENERATE, DONT_INLINE], Splitter:B:788:0x0e24] */
    /* JADX WARNING: Removed duplicated region for block: B:965:0x111e A[ExcHandler: all (th java.lang.Throwable), Splitter:B:646:0x0bda] */
    /* JADX WARNING: Removed duplicated region for block: B:989:0x11ab A[ExcHandler: all (th java.lang.Throwable), Splitter:B:411:0x078d] */
    /* JADX WARNING: Unknown variable types count: 1 */
    @android.annotation.TargetApi(18)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideoInternal(java.lang.String r69, java.io.File r70, int r71, boolean r72, int r73, int r74, int r75, int r76, int r77, int r78, int r79, long r80, long r82, long r84, long r86, boolean r88, boolean r89, org.telegram.messenger.MediaController.SavedFilterState r90, java.lang.String r91, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r92, boolean r93, org.telegram.messenger.MediaController.CropState r94) {
        /*
            r68 = this;
            r15 = r68
            r13 = r69
            r14 = r71
            r12 = r75
            r11 = r76
            r9 = r77
            r10 = r78
            r7 = r79
            r5 = r80
            r3 = r86
            r8 = r88
            r2 = r94
            long r16 = java.lang.System.currentTimeMillis()
            r18 = -1
            android.media.MediaCodec$BufferInfo r1 = new android.media.MediaCodec$BufferInfo     // Catch:{ all -> 0x1381 }
            r1.<init>()     // Catch:{ all -> 0x1381 }
            org.telegram.messenger.video.Mp4Movie r2 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ all -> 0x1381 }
            r2.<init>()     // Catch:{ all -> 0x1381 }
            r14 = r70
            r2.setCacheFile(r14)     // Catch:{ all -> 0x1381 }
            r5 = 0
            r2.setRotation(r5)     // Catch:{ all -> 0x137d }
            r2.setSize(r12, r11)     // Catch:{ all -> 0x137d }
            org.telegram.messenger.video.MP4Builder r5 = new org.telegram.messenger.video.MP4Builder     // Catch:{ all -> 0x137d }
            r5.<init>()     // Catch:{ all -> 0x137d }
            r6 = r72
            org.telegram.messenger.video.MP4Builder r2 = r5.createMovie(r2, r6)     // Catch:{ all -> 0x137d }
            r15.mediaMuxer = r2     // Catch:{ all -> 0x137d }
            float r2 = (float) r3     // Catch:{ all -> 0x137d }
            r23 = 1148846080(0x447a0000, float:1000.0)
            float r24 = r2 / r23
            r25 = 1000(0x3e8, double:4.94E-321)
            long r5 = r3 * r25
            r15.endPresentationTime = r5     // Catch:{ all -> 0x137d }
            r68.checkConversionCanceled()     // Catch:{ all -> 0x137d }
            java.lang.String r6 = "csd-1"
            java.lang.String r2 = "csd-0"
            java.lang.String r5 = "prepend-sps-pps-to-idr-frames"
            java.lang.String r14 = "video/avc"
            r8 = 0
            if (r93 == 0) goto L_0x04e3
            int r18 = (r84 > r8 ? 1 : (r84 == r8 ? 0 : -1))
            if (r18 < 0) goto L_0x0078
            r10 = 1157234688(0x44fa0000, float:2000.0)
            int r10 = (r24 > r10 ? 1 : (r24 == r10 ? 0 : -1))
            if (r10 > 0) goto L_0x0069
            r10 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x007d
        L_0x0069:
            r10 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r10 = (r24 > r10 ? 1 : (r24 == r10 ? 0 : -1))
            if (r10 > 0) goto L_0x0074
            r10 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x007d
        L_0x0074:
            r10 = 1560000(0x17cdc0, float:2.186026E-39)
            goto L_0x007d
        L_0x0078:
            if (r10 > 0) goto L_0x007d
            r10 = 921600(0xe1000, float:1.291437E-39)
        L_0x007d:
            int r18 = r12 % 16
            r19 = 1098907648(0x41800000, float:16.0)
            if (r18 == 0) goto L_0x00bf
            boolean r18 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00b9, all -> 0x00b6 }
            if (r18 == 0) goto L_0x00ac
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00b9, all -> 0x00b6 }
            r8.<init>()     // Catch:{ Exception -> 0x00b9, all -> 0x00b6 }
            java.lang.String r9 = "changing width from "
            r8.append(r9)     // Catch:{ Exception -> 0x00b9, all -> 0x00b6 }
            r8.append(r12)     // Catch:{ Exception -> 0x00b9, all -> 0x00b6 }
            java.lang.String r9 = " to "
            r8.append(r9)     // Catch:{ Exception -> 0x00b9, all -> 0x00b6 }
            float r9 = (float) r12     // Catch:{ Exception -> 0x00b9, all -> 0x00b6 }
            float r9 = r9 / r19
            int r9 = java.lang.Math.round(r9)     // Catch:{ Exception -> 0x00b9, all -> 0x00b6 }
            int r9 = r9 * 16
            r8.append(r9)     // Catch:{ Exception -> 0x00b9, all -> 0x00b6 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x00b9, all -> 0x00b6 }
            org.telegram.messenger.FileLog.d(r8)     // Catch:{ Exception -> 0x00b9, all -> 0x00b6 }
        L_0x00ac:
            float r8 = (float) r12     // Catch:{ Exception -> 0x00b9, all -> 0x00b6 }
            float r8 = r8 / r19
            int r8 = java.lang.Math.round(r8)     // Catch:{ Exception -> 0x00b9, all -> 0x00b6 }
            int r8 = r8 * 16
            goto L_0x00c0
        L_0x00b6:
            r0 = move-exception
            goto L_0x0436
        L_0x00b9:
            r0 = move-exception
            r2 = r0
            r36 = r10
            goto L_0x044a
        L_0x00bf:
            r8 = r12
        L_0x00c0:
            int r9 = r11 % 16
            if (r9 == 0) goto L_0x0100
            boolean r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00fa, all -> 0x00f7 }
            if (r9 == 0) goto L_0x00ed
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00fa, all -> 0x00f7 }
            r9.<init>()     // Catch:{ Exception -> 0x00fa, all -> 0x00f7 }
            java.lang.String r12 = "changing height from "
            r9.append(r12)     // Catch:{ Exception -> 0x00fa, all -> 0x00f7 }
            r9.append(r11)     // Catch:{ Exception -> 0x00fa, all -> 0x00f7 }
            java.lang.String r12 = " to "
            r9.append(r12)     // Catch:{ Exception -> 0x00fa, all -> 0x00f7 }
            float r12 = (float) r11     // Catch:{ Exception -> 0x00fa, all -> 0x00f7 }
            float r12 = r12 / r19
            int r12 = java.lang.Math.round(r12)     // Catch:{ Exception -> 0x00fa, all -> 0x00f7 }
            int r12 = r12 * 16
            r9.append(r12)     // Catch:{ Exception -> 0x00fa, all -> 0x00f7 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x00fa, all -> 0x00f7 }
            org.telegram.messenger.FileLog.d(r9)     // Catch:{ Exception -> 0x00fa, all -> 0x00f7 }
        L_0x00ed:
            float r9 = (float) r11     // Catch:{ Exception -> 0x00fa, all -> 0x00f7 }
            float r9 = r9 / r19
            int r9 = java.lang.Math.round(r9)     // Catch:{ Exception -> 0x00fa, all -> 0x00f7 }
            int r9 = r9 * 16
            goto L_0x0101
        L_0x00f7:
            r0 = move-exception
            goto L_0x0424
        L_0x00fa:
            r0 = move-exception
            r2 = r0
            r36 = r10
            goto L_0x0431
        L_0x0100:
            r9 = r11
        L_0x0101:
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x041c, all -> 0x040e }
            if (r11 == 0) goto L_0x0133
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x012d, all -> 0x012a }
            r11.<init>()     // Catch:{ Exception -> 0x012d, all -> 0x012a }
            java.lang.String r12 = "create photo encoder "
            r11.append(r12)     // Catch:{ Exception -> 0x012d, all -> 0x012a }
            r11.append(r8)     // Catch:{ Exception -> 0x012d, all -> 0x012a }
            java.lang.String r12 = " "
            r11.append(r12)     // Catch:{ Exception -> 0x012d, all -> 0x012a }
            r11.append(r9)     // Catch:{ Exception -> 0x012d, all -> 0x012a }
            java.lang.String r12 = " duration = "
            r11.append(r12)     // Catch:{ Exception -> 0x012d, all -> 0x012a }
            r11.append(r3)     // Catch:{ Exception -> 0x012d, all -> 0x012a }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x012d, all -> 0x012a }
            org.telegram.messenger.FileLog.d(r11)     // Catch:{ Exception -> 0x012d, all -> 0x012a }
            goto L_0x0133
        L_0x012a:
            r0 = move-exception
            goto L_0x0411
        L_0x012d:
            r0 = move-exception
            r2 = r0
            r36 = r10
            goto L_0x044c
        L_0x0133:
            android.media.MediaFormat r11 = android.media.MediaFormat.createVideoFormat(r14, r8, r9)     // Catch:{ Exception -> 0x041c, all -> 0x040e }
            java.lang.String r12 = "color-format"
            r7 = 2130708361(0x7var_, float:1.701803E38)
            r11.setInteger(r12, r7)     // Catch:{ Exception -> 0x041c, all -> 0x040e }
            java.lang.String r7 = "bitrate"
            r11.setInteger(r7, r10)     // Catch:{ Exception -> 0x041c, all -> 0x040e }
            java.lang.String r7 = "frame-rate"
            r12 = 30
            r11.setInteger(r7, r12)     // Catch:{ Exception -> 0x041c, all -> 0x040e }
            java.lang.String r7 = "i-frame-interval"
            r12 = 1
            r11.setInteger(r7, r12)     // Catch:{ Exception -> 0x041c, all -> 0x040e }
            android.media.MediaCodec r7 = android.media.MediaCodec.createEncoderByType(r14)     // Catch:{ Exception -> 0x041c, all -> 0x040e }
            r3 = 0
            r7.configure(r11, r3, r3, r12)     // Catch:{ Exception -> 0x0408, all -> 0x040e }
            org.telegram.messenger.video.InputSurface r3 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x0408, all -> 0x040e }
            android.view.Surface r4 = r7.createInputSurface()     // Catch:{ Exception -> 0x0408, all -> 0x040e }
            r3.<init>(r4)     // Catch:{ Exception -> 0x0408, all -> 0x040e }
            r3.makeCurrent()     // Catch:{ Exception -> 0x03fd, all -> 0x040e }
            r7.start()     // Catch:{ Exception -> 0x03fd, all -> 0x040e }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x03fd, all -> 0x040e }
            r11 = 21
            if (r4 >= r11) goto L_0x017b
            java.nio.ByteBuffer[] r4 = r7.getOutputBuffers()     // Catch:{ Exception -> 0x0173, all -> 0x012a }
            goto L_0x017c
        L_0x0173:
            r0 = move-exception
            r2 = r0
            r34 = r3
            r36 = r10
            goto L_0x0405
        L_0x017b:
            r4 = 0
        L_0x017c:
            r68.checkConversionCanceled()     // Catch:{ Exception -> 0x03fd, all -> 0x040e }
            r11 = -5
            r12 = 1
            r18 = 0
            r19 = 0
        L_0x0185:
            if (r18 != 0) goto L_0x03f4
            r68.checkConversionCanceled()     // Catch:{ Exception -> 0x03e9, all -> 0x03dc }
            r26 = 0
            r21 = 1
            r26 = r26 ^ 1
            r35 = r19
            r19 = r18
            r18 = r12
            r12 = r11
            r11 = 1
        L_0x0198:
            if (r26 != 0) goto L_0x01a5
            if (r11 == 0) goto L_0x019d
            goto L_0x01a5
        L_0x019d:
            r11 = r12
            r12 = r18
            r18 = r19
            r19 = r35
            goto L_0x0185
        L_0x01a5:
            r68.checkConversionCanceled()     // Catch:{ Exception -> 0x03d1, all -> 0x03c2 }
            if (r89 == 0) goto L_0x01b5
            r36 = 22000(0x55f0, double:1.08694E-319)
            r75 = r11
            r66 = r36
            r36 = r10
            r10 = r66
            goto L_0x01bb
        L_0x01b5:
            r36 = r10
            r75 = r11
            r10 = 2500(0x9c4, double:1.235E-320)
        L_0x01bb:
            int r10 = r7.dequeueOutputBuffer(r1, r10)     // Catch:{ Exception -> 0x03be, all -> 0x03af }
            r11 = -1
            if (r10 != r11) goto L_0x01ca
            r76 = r3
            r37 = r5
            r3 = -1
            r11 = 0
            goto L_0x036e
        L_0x01ca:
            r11 = -3
            if (r10 != r11) goto L_0x01e0
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x03be, all -> 0x03af }
            r76 = r3
            r3 = 21
            if (r11 >= r3) goto L_0x01d9
            java.nio.ByteBuffer[] r4 = r7.getOutputBuffers()     // Catch:{ Exception -> 0x022d, all -> 0x03af }
        L_0x01d9:
            r11 = r75
            r37 = r5
        L_0x01dd:
            r3 = -1
            goto L_0x036e
        L_0x01e0:
            r76 = r3
            r3 = -2
            if (r10 != r3) goto L_0x0230
            android.media.MediaFormat r3 = r7.getOutputFormat()     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            if (r11 == 0) goto L_0x0201
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r11.<init>()     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            java.lang.String r13 = "photo encoder new format "
            r11.append(r13)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r11.append(r3)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            org.telegram.messenger.FileLog.d(r11)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
        L_0x0201:
            r11 = -5
            if (r12 != r11) goto L_0x01d9
            if (r3 == 0) goto L_0x01d9
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r13 = 0
            int r12 = r11.addTrack(r3, r13)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            boolean r11 = r3.containsKey(r5)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            if (r11 == 0) goto L_0x01d9
            int r11 = r3.getInteger(r5)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r13 = 1
            if (r11 != r13) goto L_0x01d9
            java.nio.ByteBuffer r11 = r3.getByteBuffer(r2)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            java.nio.ByteBuffer r3 = r3.getByteBuffer(r6)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            int r11 = r11.limit()     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            int r3 = r3.limit()     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            int r35 = r11 + r3
            goto L_0x01d9
        L_0x022d:
            r0 = move-exception
            goto L_0x03d6
        L_0x0230:
            if (r10 < 0) goto L_0x0398
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r13 = 21
            if (r3 >= r13) goto L_0x023b
            r3 = r4[r10]     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            goto L_0x023f
        L_0x023b:
            java.nio.ByteBuffer r3 = r7.getOutputBuffer(r10)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
        L_0x023f:
            if (r3 == 0) goto L_0x037c
            int r11 = r1.size     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r13 = 1
            if (r11 <= r13) goto L_0x0354
            int r13 = r1.flags     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r19 = r13 & 2
            if (r19 != 0) goto L_0x02d9
            if (r35 == 0) goto L_0x025f
            r19 = r13 & 1
            if (r19 == 0) goto L_0x025f
            r78 = r4
            int r4 = r1.offset     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            int r4 = r4 + r35
            r1.offset = r4     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            int r11 = r11 - r35
            r1.size = r11     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            goto L_0x0261
        L_0x025f:
            r78 = r4
        L_0x0261:
            if (r18 == 0) goto L_0x02b4
            r4 = r13 & 1
            if (r4 == 0) goto L_0x02b4
            int r4 = r1.size     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r13 = 100
            if (r4 <= r13) goto L_0x02b2
            int r4 = r1.offset     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r3.position(r4)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            byte[] r4 = new byte[r13]     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r3.get(r4)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r11 = 0
            r18 = 0
        L_0x027a:
            r13 = 96
            if (r11 >= r13) goto L_0x02b2
            byte r13 = r4[r11]     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            if (r13 != 0) goto L_0x02a9
            int r13 = r11 + 1
            byte r13 = r4[r13]     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            if (r13 != 0) goto L_0x02a9
            int r13 = r11 + 2
            byte r13 = r4[r13]     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            if (r13 != 0) goto L_0x02a9
            int r13 = r11 + 3
            byte r13 = r4[r13]     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r19 = r4
            r4 = 1
            if (r13 != r4) goto L_0x02ab
            int r13 = r18 + 1
            if (r13 <= r4) goto L_0x02a6
            int r4 = r1.offset     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            int r4 = r4 + r11
            r1.offset = r4     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            int r4 = r1.size     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            int r4 = r4 - r11
            r1.size = r4     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            goto L_0x02b2
        L_0x02a6:
            r18 = r13
            goto L_0x02ab
        L_0x02a9:
            r19 = r4
        L_0x02ab:
            int r11 = r11 + 1
            r4 = r19
            r13 = 100
            goto L_0x027a
        L_0x02b2:
            r18 = 0
        L_0x02b4:
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r11 = 1
            long r3 = r4.writeSampleData(r12, r3, r1, r11)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r37 = r5
            r13 = r6
            r5 = 0
            int r11 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r11 == 0) goto L_0x02d3
            org.telegram.messenger.MediaController$VideoConvertorListener r11 = r15.callback     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            if (r11 == 0) goto L_0x02d3
            r19 = r13
            float r13 = (float) r5     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            float r13 = r13 / r23
            float r13 = r13 / r24
            r11.didWriteData(r3, r13)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            goto L_0x02d5
        L_0x02d3:
            r19 = r13
        L_0x02d5:
            r6 = r19
            goto L_0x0358
        L_0x02d9:
            r78 = r4
            r37 = r5
            r19 = r6
            r4 = -5
            r5 = 0
            if (r12 != r4) goto L_0x02d5
            byte[] r13 = new byte[r11]     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            int r4 = r1.offset     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            int r4 = r4 + r11
            r3.limit(r4)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            int r4 = r1.offset     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r3.position(r4)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r3.get(r13)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            int r3 = r1.size     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r4 = 1
            int r3 = r3 - r4
        L_0x02f8:
            if (r3 < 0) goto L_0x0337
            r11 = 3
            if (r3 <= r11) goto L_0x0337
            byte r11 = r13[r3]     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            if (r11 != r4) goto L_0x0331
            int r4 = r3 + -1
            byte r4 = r13[r4]     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            if (r4 != 0) goto L_0x0331
            int r4 = r3 + -2
            byte r4 = r13[r4]     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            if (r4 != 0) goto L_0x0331
            int r4 = r3 + -3
            byte r11 = r13[r4]     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            if (r11 != 0) goto L_0x0331
            java.nio.ByteBuffer r3 = java.nio.ByteBuffer.allocate(r4)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            int r11 = r1.size     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            int r11 = r11 - r4
            java.nio.ByteBuffer r11 = java.nio.ByteBuffer.allocate(r11)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r5 = 0
            java.nio.ByteBuffer r6 = r3.put(r13, r5, r4)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r6.position(r5)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            int r6 = r1.size     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            int r6 = r6 - r4
            java.nio.ByteBuffer r4 = r11.put(r13, r4, r6)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r4.position(r5)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            goto L_0x0339
        L_0x0331:
            int r3 = r3 + -1
            r4 = 1
            r5 = 0
            goto L_0x02f8
        L_0x0337:
            r3 = 0
            r11 = 0
        L_0x0339:
            android.media.MediaFormat r4 = android.media.MediaFormat.createVideoFormat(r14, r8, r9)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            if (r3 == 0) goto L_0x034a
            if (r11 == 0) goto L_0x034a
            r4.setByteBuffer(r2, r3)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r6 = r19
            r4.setByteBuffer(r6, r11)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            goto L_0x034c
        L_0x034a:
            r6 = r19
        L_0x034c:
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r5 = 0
            int r12 = r3.addTrack(r4, r5)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            goto L_0x0358
        L_0x0354:
            r78 = r4
            r37 = r5
        L_0x0358:
            int r3 = r1.flags     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r3 = r3 & 4
            if (r3 == 0) goto L_0x0361
            r3 = 0
            r5 = 1
            goto L_0x0363
        L_0x0361:
            r3 = 0
            r5 = 0
        L_0x0363:
            r7.releaseOutputBuffer(r10, r3)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r11 = r75
            r4 = r78
            r19 = r5
            goto L_0x01dd
        L_0x036e:
            if (r10 == r3) goto L_0x037a
            r13 = r69
            r3 = r76
            r10 = r36
            r5 = r37
            goto L_0x0198
        L_0x037a:
            r1 = 0
            throw r1     // Catch:{ Exception -> 0x022d, all -> 0x03af }
        L_0x037c:
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r2.<init>()     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            java.lang.String r3 = "encoderOutputBuffer "
            r2.append(r3)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r2.append(r10)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            java.lang.String r3 = " was null"
            r2.append(r3)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r1.<init>(r2)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            throw r1     // Catch:{ Exception -> 0x022d, all -> 0x03af }
        L_0x0398:
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r2.<init>()     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            java.lang.String r3 = "unexpected result from encoder.dequeueOutputBuffer: "
            r2.append(r3)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r2.append(r10)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            r1.<init>(r2)     // Catch:{ Exception -> 0x022d, all -> 0x03af }
            throw r1     // Catch:{ Exception -> 0x022d, all -> 0x03af }
        L_0x03af:
            r0 = move-exception
            r11 = r77
            r5 = r80
            r44 = r82
            r32 = r84
            r2 = r0
            r1 = r12
            r10 = r36
            goto L_0x1390
        L_0x03be:
            r0 = move-exception
            r76 = r3
            goto L_0x03d6
        L_0x03c2:
            r0 = move-exception
            r36 = r10
            r11 = r77
            r5 = r80
            r44 = r82
            r32 = r84
            r2 = r0
            r1 = r12
            goto L_0x1390
        L_0x03d1:
            r0 = move-exception
            r76 = r3
            r36 = r10
        L_0x03d6:
            r34 = r76
            r2 = r0
            r1 = r12
            goto L_0x0450
        L_0x03dc:
            r0 = move-exception
            r36 = r10
            r5 = r80
            r44 = r82
            r32 = r84
            r2 = r0
            r1 = r11
            goto L_0x0440
        L_0x03e9:
            r0 = move-exception
            r76 = r3
            r36 = r10
            r34 = r76
            r2 = r0
            r1 = r11
            goto L_0x0450
        L_0x03f4:
            r76 = r3
            r13 = r77
            r1 = 0
            r22 = 0
            goto L_0x0492
        L_0x03fd:
            r0 = move-exception
            r76 = r3
            r36 = r10
            r34 = r76
            r2 = r0
        L_0x0405:
            r1 = -5
            goto L_0x0450
        L_0x0408:
            r0 = move-exception
            r36 = r10
            r2 = r0
            r1 = -5
            goto L_0x044e
        L_0x040e:
            r0 = move-exception
            r36 = r10
        L_0x0411:
            r11 = r77
            r5 = r80
            r44 = r82
            r32 = r84
            r2 = r0
            goto L_0x138f
        L_0x041c:
            r0 = move-exception
            r36 = r10
            r2 = r0
            goto L_0x044c
        L_0x0421:
            r0 = move-exception
            r36 = r10
        L_0x0424:
            r5 = r80
            r44 = r82
            r32 = r84
            r2 = r0
            r9 = r11
            goto L_0x043f
        L_0x042d:
            r0 = move-exception
            r36 = r10
            r2 = r0
        L_0x0431:
            r9 = r11
            goto L_0x044c
        L_0x0433:
            r0 = move-exception
            r36 = r10
        L_0x0436:
            r5 = r80
            r44 = r82
            r32 = r84
            r2 = r0
            r9 = r11
            r8 = r12
        L_0x043f:
            r1 = -5
        L_0x0440:
            r40 = 0
            r11 = r77
            goto L_0x1392
        L_0x0446:
            r0 = move-exception
            r36 = r10
            r2 = r0
        L_0x044a:
            r9 = r11
            r8 = r12
        L_0x044c:
            r1 = -5
            r7 = 0
        L_0x044e:
            r34 = 0
        L_0x0450:
            boolean r3 = r2 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x04d5 }
            if (r3 == 0) goto L_0x0459
            if (r89 != 0) goto L_0x0459
            r22 = 1
            goto L_0x045b
        L_0x0459:
            r22 = 0
        L_0x045b:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x04c5 }
            r3.<init>()     // Catch:{ all -> 0x04c5 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x04c5 }
            r10 = r36
            r3.append(r10)     // Catch:{ all -> 0x04c3 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x04c3 }
            r13 = r77
            r3.append(r13)     // Catch:{ all -> 0x04b9 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x04b9 }
            r3.append(r9)     // Catch:{ all -> 0x04b9 }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x04b9 }
            r3.append(r8)     // Catch:{ all -> 0x04b9 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x04b9 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x04b9 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x04b9 }
            r11 = r1
            r3 = r34
            r1 = 1
        L_0x0492:
            if (r3 == 0) goto L_0x04a2
            r3.release()     // Catch:{ all -> 0x0498 }
            goto L_0x04a2
        L_0x0498:
            r0 = move-exception
            r5 = r80
            r44 = r82
            r32 = r84
            r2 = r0
            r1 = r11
            goto L_0x04c1
        L_0x04a2:
            if (r7 == 0) goto L_0x04aa
            r7.stop()     // Catch:{ all -> 0x0498 }
            r7.release()     // Catch:{ all -> 0x0498 }
        L_0x04aa:
            r68.checkConversionCanceled()     // Catch:{ all -> 0x0498 }
            r5 = r80
            r32 = r84
            r40 = r1
            r26 = r10
            r1 = r11
            r11 = r13
            goto L_0x0573
        L_0x04b9:
            r0 = move-exception
            r5 = r80
            r44 = r82
            r32 = r84
            r2 = r0
        L_0x04c1:
            r11 = r13
            goto L_0x04d1
        L_0x04c3:
            r0 = move-exception
            goto L_0x04c8
        L_0x04c5:
            r0 = move-exception
            r10 = r36
        L_0x04c8:
            r11 = r77
            r5 = r80
            r44 = r82
            r32 = r84
            r2 = r0
        L_0x04d1:
            r40 = r22
            goto L_0x1392
        L_0x04d5:
            r0 = move-exception
            r10 = r36
            r11 = r77
            r5 = r80
            r44 = r82
            r32 = r84
            r2 = r0
            goto L_0x1390
        L_0x04e3:
            r13 = r77
            r37 = r5
            android.media.MediaExtractor r3 = new android.media.MediaExtractor     // Catch:{ all -> 0x1374 }
            r3.<init>()     // Catch:{ all -> 0x1374 }
            r15.extractor = r3     // Catch:{ all -> 0x1374 }
            r7 = r69
            r3.setDataSource(r7)     // Catch:{ all -> 0x1374 }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ all -> 0x1374 }
            r5 = 0
            int r4 = org.telegram.messenger.MediaController.findTrack(r3, r5)     // Catch:{ all -> 0x1374 }
            r3 = -1
            if (r10 == r3) goto L_0x0512
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ all -> 0x0505 }
            r5 = 1
            int r3 = org.telegram.messenger.MediaController.findTrack(r3, r5)     // Catch:{ all -> 0x0505 }
            goto L_0x0514
        L_0x0505:
            r0 = move-exception
            r5 = r80
            r44 = r82
            r32 = r84
            r2 = r0
            r9 = r11
        L_0x050e:
            r8 = r12
            r11 = r13
            goto L_0x138f
        L_0x0512:
            r5 = 1
            r3 = -1
        L_0x0514:
            java.lang.String r11 = "mime"
            if (r4 < 0) goto L_0x0535
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ all -> 0x052a }
            android.media.MediaFormat r5 = r5.getTrackFormat(r4)     // Catch:{ all -> 0x052a }
            java.lang.String r5 = r5.getString(r11)     // Catch:{ all -> 0x052a }
            boolean r5 = r5.equals(r14)     // Catch:{ all -> 0x052a }
            if (r5 != 0) goto L_0x0535
            r5 = 1
            goto L_0x0536
        L_0x052a:
            r0 = move-exception
            r9 = r76
            r5 = r80
            r44 = r82
            r32 = r84
            r2 = r0
            goto L_0x050e
        L_0x0535:
            r5 = 0
        L_0x0536:
            if (r88 != 0) goto L_0x058d
            if (r5 == 0) goto L_0x053b
            goto L_0x058d
        L_0x053b:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x0579 }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ all -> 0x0579 }
            r5 = -1
            r4 = r1
            r9 = 0
            r11 = 1
            r14 = -5
            if (r10 == r5) goto L_0x0549
            r21 = 1
            goto L_0x054b
        L_0x0549:
            r21 = 0
        L_0x054b:
            r1 = r68
            r8 = r94
            r13 = r80
            r5 = r80
            r13 = r79
            r14 = r8
            r7 = r82
            r14 = r77
            r9 = r86
            r11 = r70
            r12 = r21
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ all -> 0x0577 }
            r8 = r75
            r9 = r76
            r26 = r78
            r5 = r80
            r32 = r84
            r11 = r14
            r1 = -5
            r22 = 0
            r40 = 0
        L_0x0573:
            r13 = r82
            goto L_0x1350
        L_0x0577:
            r0 = move-exception
            goto L_0x057d
        L_0x0579:
            r0 = move-exception
            r14 = r13
            r13 = r79
        L_0x057d:
            r8 = r75
            r9 = r76
            r10 = r78
            r5 = r80
            r44 = r82
            r32 = r84
            r2 = r0
            r11 = r14
            goto L_0x138f
        L_0x058d:
            r12 = r1
            r10 = r14
            r5 = -1
            r7 = -5
            r14 = r13
            r13 = r79
            if (r4 < 0) goto L_0x130c
            r20 = -2147483648(0xfffffffvar_, double:NaN)
            r1 = 1000(0x3e8, float:1.401E-42)
            int r1 = r1 / r14
            int r1 = r1 * 1000
            long r8 = (long) r1     // Catch:{ Exception -> 0x126c, all -> 0x125b }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x126c, all -> 0x125b }
            r1.selectTrack(r4)     // Catch:{ Exception -> 0x126c, all -> 0x125b }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x126c, all -> 0x125b }
            android.media.MediaFormat r1 = r1.getTrackFormat(r4)     // Catch:{ Exception -> 0x126c, all -> 0x125b }
            r28 = 0
            int r22 = (r84 > r28 ? 1 : (r84 == r28 ? 0 : -1))
            if (r22 < 0) goto L_0x05cd
            r22 = 1157234688(0x44fa0000, float:2000.0)
            int r22 = (r24 > r22 ? 1 : (r24 == r22 ? 0 : -1))
            if (r22 > 0) goto L_0x05ba
            r22 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x05c8
        L_0x05ba:
            r22 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r22 = (r24 > r22 ? 1 : (r24 == r22 ? 0 : -1))
            if (r22 > 0) goto L_0x05c5
            r22 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x05c8
        L_0x05c5:
            r22 = 1560000(0x17cdc0, float:2.186026E-39)
        L_0x05c8:
            r5 = r22
            r28 = 0
            goto L_0x05d9
        L_0x05cd:
            if (r78 > 0) goto L_0x05d5
            r28 = r84
            r5 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x05d9
        L_0x05d5:
            r5 = r78
            r28 = r84
        L_0x05d9:
            if (r13 <= 0) goto L_0x060b
            int r5 = java.lang.Math.min(r13, r5)     // Catch:{ Exception -> 0x05f3, all -> 0x05e0 }
            goto L_0x060b
        L_0x05e0:
            r0 = move-exception
            r8 = r75
            r9 = r76
            r44 = r82
            r2 = r0
            r10 = r5
            r11 = r14
            r32 = r28
        L_0x05ec:
            r1 = -5
            r40 = 0
            r5 = r80
            goto L_0x1392
        L_0x05f3:
            r0 = move-exception
            r44 = r82
            r2 = r0
            r55 = r4
            r1 = r5
            r11 = r14
            r32 = r28
        L_0x05fd:
            r7 = 0
            r10 = 0
            r25 = 0
            r40 = -5
            r49 = 0
            r59 = 0
            r5 = r80
            goto L_0x1285
        L_0x060b:
            r32 = 0
            int r22 = (r28 > r32 ? 1 : (r28 == r32 ? 0 : -1))
            r35 = r3
            if (r22 < 0) goto L_0x0618
            r22 = r4
            r3 = r18
            goto L_0x061c
        L_0x0618:
            r22 = r4
            r3 = r28
        L_0x061c:
            int r28 = (r3 > r32 ? 1 : (r3 == r32 ? 0 : -1))
            if (r28 < 0) goto L_0x0649
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x063e, all -> 0x0630 }
            r13 = 0
            r7.seekTo(r3, r13)     // Catch:{ Exception -> 0x063e, all -> 0x062e }
            r7 = r94
            r84 = r3
        L_0x062a:
            r3 = 0
            goto L_0x068e
        L_0x062e:
            r0 = move-exception
            goto L_0x0632
        L_0x0630:
            r0 = move-exception
            r13 = 0
        L_0x0632:
            r8 = r75
            r9 = r76
            r44 = r82
            r2 = r0
            r32 = r3
        L_0x063b:
            r10 = r5
            r11 = r14
            goto L_0x05ec
        L_0x063e:
            r0 = move-exception
            r44 = r82
            r2 = r0
            r32 = r3
        L_0x0644:
            r1 = r5
            r11 = r14
            r55 = r22
            goto L_0x05fd
        L_0x0649:
            r84 = r3
            r13 = 0
            r32 = 0
            r3 = r80
            int r7 = (r3 > r32 ? 1 : (r3 == r32 ? 0 : -1))
            if (r7 <= 0) goto L_0x0685
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x066e, all -> 0x065c }
            r7.seekTo(r3, r13)     // Catch:{ Exception -> 0x066e, all -> 0x065c }
            r7 = r94
            goto L_0x062a
        L_0x065c:
            r0 = move-exception
            r8 = r75
            r9 = r76
            r44 = r82
            r32 = r84
            r2 = r0
            r10 = r5
            r11 = r14
            r1 = -5
            r40 = 0
            r5 = r3
            goto L_0x1392
        L_0x066e:
            r0 = move-exception
            r44 = r82
            r32 = r84
            r2 = r0
            r1 = r5
            r11 = r14
            r55 = r22
            r7 = 0
            r10 = 0
            r25 = 0
            r40 = -5
            r49 = 0
            r59 = 0
            r5 = r3
            goto L_0x1285
        L_0x0685:
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x1250, all -> 0x1240 }
            r3 = 0
            r7.seekTo(r3, r13)     // Catch:{ Exception -> 0x1234, all -> 0x122a }
            r7 = r94
        L_0x068e:
            if (r7 == 0) goto L_0x06b7
            r3 = 90
            r4 = r71
            if (r4 == r3) goto L_0x06a0
            r3 = 270(0x10e, float:3.78E-43)
            if (r4 != r3) goto L_0x069b
            goto L_0x06a0
        L_0x069b:
            int r3 = r7.transformWidth     // Catch:{ Exception -> 0x06b0, all -> 0x06a5 }
            int r13 = r7.transformHeight     // Catch:{ Exception -> 0x06b0, all -> 0x06a5 }
            goto L_0x06bd
        L_0x06a0:
            int r3 = r7.transformHeight     // Catch:{ Exception -> 0x06b0, all -> 0x06a5 }
            int r13 = r7.transformWidth     // Catch:{ Exception -> 0x06b0, all -> 0x06a5 }
            goto L_0x06bd
        L_0x06a5:
            r0 = move-exception
            r8 = r75
            r9 = r76
            r44 = r82
            r32 = r84
            r2 = r0
            goto L_0x063b
        L_0x06b0:
            r0 = move-exception
            r44 = r82
            r32 = r84
            r2 = r0
            goto L_0x0644
        L_0x06b7:
            r4 = r71
            r3 = r75
            r13 = r76
        L_0x06bd:
            boolean r29 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1234, all -> 0x122a }
            if (r29 == 0) goto L_0x06e0
            r29 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06b0, all -> 0x06a5 }
            r2.<init>()     // Catch:{ Exception -> 0x06b0, all -> 0x06a5 }
            java.lang.String r4 = "create encoder with w = "
            r2.append(r4)     // Catch:{ Exception -> 0x06b0, all -> 0x06a5 }
            r2.append(r3)     // Catch:{ Exception -> 0x06b0, all -> 0x06a5 }
            java.lang.String r4 = " h = "
            r2.append(r4)     // Catch:{ Exception -> 0x06b0, all -> 0x06a5 }
            r2.append(r13)     // Catch:{ Exception -> 0x06b0, all -> 0x06a5 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x06b0, all -> 0x06a5 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x06b0, all -> 0x06a5 }
            goto L_0x06e2
        L_0x06e0:
            r29 = r2
        L_0x06e2:
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r10, r3, r13)     // Catch:{ Exception -> 0x1234, all -> 0x122a }
            java.lang.String r4 = "color-format"
            r36 = r6
            r6 = 2130708361(0x7var_, float:1.701803E38)
            r2.setInteger(r4, r6)     // Catch:{ Exception -> 0x1234, all -> 0x122a }
            java.lang.String r4 = "bitrate"
            r2.setInteger(r4, r5)     // Catch:{ Exception -> 0x1234, all -> 0x122a }
            java.lang.String r4 = "frame-rate"
            r2.setInteger(r4, r14)     // Catch:{ Exception -> 0x1234, all -> 0x122a }
            java.lang.String r4 = "i-frame-interval"
            r6 = 2
            r2.setInteger(r4, r6)     // Catch:{ Exception -> 0x1234, all -> 0x122a }
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1234, all -> 0x122a }
            r4 = 23
            if (r6 >= r4) goto L_0x071e
            int r4 = java.lang.Math.min(r13, r3)     // Catch:{ Exception -> 0x06b0, all -> 0x06a5 }
            r78 = r3
            r3 = 480(0x1e0, float:6.73E-43)
            if (r4 > r3) goto L_0x0720
            r3 = 921600(0xe1000, float:1.291437E-39)
            if (r5 <= r3) goto L_0x0718
            r5 = 921600(0xe1000, float:1.291437E-39)
        L_0x0718:
            java.lang.String r3 = "bitrate"
            r2.setInteger(r3, r5)     // Catch:{ Exception -> 0x06b0, all -> 0x06a5 }
            goto L_0x0720
        L_0x071e:
            r78 = r3
        L_0x0720:
            r26 = r5
            android.media.MediaCodec r5 = android.media.MediaCodec.createEncoderByType(r10)     // Catch:{ Exception -> 0x1219, all -> 0x1206 }
            r3 = 1
            r4 = 0
            r5.configure(r2, r4, r4, r3)     // Catch:{ Exception -> 0x11f3, all -> 0x1206 }
            org.telegram.messenger.video.InputSurface r2 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x11f3, all -> 0x1206 }
            android.view.Surface r3 = r5.createInputSurface()     // Catch:{ Exception -> 0x11f3, all -> 0x1206 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x11f3, all -> 0x1206 }
            r2.makeCurrent()     // Catch:{ Exception -> 0x11d6, all -> 0x1206 }
            r5.start()     // Catch:{ Exception -> 0x11d6, all -> 0x1206 }
            java.lang.String r3 = r1.getString(r11)     // Catch:{ Exception -> 0x11d6, all -> 0x1206 }
            android.media.MediaCodec r3 = android.media.MediaCodec.createDecoderByType(r3)     // Catch:{ Exception -> 0x11d6, all -> 0x1206 }
            r34 = r11
            org.telegram.messenger.video.OutputSurface r11 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x11c1, all -> 0x1206 }
            r41 = 0
            r42 = r3
            float r3 = (float) r14
            r43 = 0
            r48 = r1
            r1 = r11
            r49 = r2
            r50 = r29
            r2 = r90
            r53 = r78
            r32 = r84
            r29 = r3
            r51 = r35
            r52 = r42
            r44 = 0
            r3 = r41
            r55 = r22
            r22 = r4
            r4 = r91
            r78 = r5
            r56 = r37
            r25 = -1
            r5 = r92
            r57 = r6
            r58 = r36
            r6 = r94
            r7 = r75
            r30 = r8
            r9 = 21
            r8 = r76
            r9 = r71
            r61 = r10
            r10 = r29
            r14 = r11
            r22 = r13
            r13 = r34
            r11 = r43
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x11b2, all -> 0x11ab }
            java.lang.String r1 = createFragmentShader(r73, r74, r75, r76)     // Catch:{ Exception -> 0x1196, all -> 0x11ab }
            r14.changeFragmentShader(r1)     // Catch:{ Exception -> 0x1196, all -> 0x11ab }
            android.view.Surface r1 = r14.getSurface()     // Catch:{ Exception -> 0x1196, all -> 0x11ab }
            r2 = r48
            r3 = r52
            r4 = 0
            r5 = 0
            r3.configure(r2, r1, r4, r5)     // Catch:{ Exception -> 0x1189, all -> 0x11ab }
            r3.start()     // Catch:{ Exception -> 0x1189, all -> 0x11ab }
            r1 = r57
            r2 = 21
            if (r1 >= r2) goto L_0x07d9
            java.nio.ByteBuffer[] r7 = r3.getInputBuffers()     // Catch:{ Exception -> 0x07c4, all -> 0x07b6 }
            java.nio.ByteBuffer[] r1 = r78.getOutputBuffers()     // Catch:{ Exception -> 0x07c4, all -> 0x07b6 }
            goto L_0x07db
        L_0x07b6:
            r0 = move-exception
            r8 = r75
            r9 = r76
            r11 = r77
            r5 = r80
            r44 = r82
            r2 = r0
            goto L_0x1215
        L_0x07c4:
            r0 = move-exception
            r11 = r77
            r7 = r78
            r5 = r80
            r44 = r82
            r2 = r0
            r10 = r3
            r59 = r4
            r25 = r14
            r1 = r26
            r40 = -5
            goto L_0x1285
        L_0x07d9:
            r1 = r4
            r7 = r1
        L_0x07db:
            r5 = r51
            if (r5 < 0) goto L_0x092c
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0912, all -> 0x0903 }
            android.media.MediaFormat r6 = r6.getTrackFormat(r5)     // Catch:{ Exception -> 0x0912, all -> 0x0903 }
            java.lang.String r8 = r6.getString(r13)     // Catch:{ Exception -> 0x0912, all -> 0x0903 }
            java.lang.String r9 = "audio/mp4a-latm"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x0912, all -> 0x0903 }
            if (r8 != 0) goto L_0x0800
            java.lang.String r8 = r6.getString(r13)     // Catch:{ Exception -> 0x07c4, all -> 0x07b6 }
            java.lang.String r9 = "audio/mpeg"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x07c4, all -> 0x07b6 }
            if (r8 == 0) goto L_0x07fe
            goto L_0x0800
        L_0x07fe:
            r8 = 0
            goto L_0x0801
        L_0x0800:
            r8 = 1
        L_0x0801:
            java.lang.String r9 = r6.getString(r13)     // Catch:{ Exception -> 0x0912, all -> 0x0903 }
            java.lang.String r10 = "audio/unknown"
            boolean r9 = r9.equals(r10)     // Catch:{ Exception -> 0x0912, all -> 0x0903 }
            if (r9 == 0) goto L_0x080e
            r5 = -1
        L_0x080e:
            if (r5 < 0) goto L_0x08f6
            if (r8 == 0) goto L_0x0864
            org.telegram.messenger.video.MP4Builder r9 = r15.mediaMuxer     // Catch:{ Exception -> 0x0854, all -> 0x07b6 }
            r10 = 1
            int r9 = r9.addTrack(r6, r10)     // Catch:{ Exception -> 0x0854, all -> 0x07b6 }
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x0854, all -> 0x07b6 }
            r11.selectTrack(r5)     // Catch:{ Exception -> 0x0854, all -> 0x07b6 }
            java.lang.String r11 = "max-input-size"
            int r6 = r6.getInteger(r11)     // Catch:{ Exception -> 0x0825, all -> 0x07b6 }
            goto L_0x082b
        L_0x0825:
            r0 = move-exception
            r6 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)     // Catch:{ Exception -> 0x0854, all -> 0x07b6 }
            r6 = 0
        L_0x082b:
            if (r6 > 0) goto L_0x082f
            r6 = 65536(0x10000, float:9.18355E-41)
        L_0x082f:
            java.nio.ByteBuffer r11 = java.nio.ByteBuffer.allocateDirect(r6)     // Catch:{ Exception -> 0x0854, all -> 0x07b6 }
            r52 = r3
            r25 = r14
            r13 = 0
            r2 = r80
            int r28 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r28 <= 0) goto L_0x0846
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x088d, all -> 0x0882 }
            r10 = 0
            r4.seekTo(r2, r10)     // Catch:{ Exception -> 0x088d, all -> 0x0882 }
            goto L_0x084c
        L_0x0846:
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x088d, all -> 0x0882 }
            r10 = 0
            r4.seekTo(r13, r10)     // Catch:{ Exception -> 0x088d, all -> 0x0882 }
        L_0x084c:
            r13 = r82
            r4 = r9
            r10 = 0
            r9 = r69
            goto L_0x093b
        L_0x0854:
            r0 = move-exception
            r52 = r3
            r25 = r14
            r11 = r77
            r7 = r78
            r5 = r80
            r44 = r82
            r2 = r0
            goto L_0x0924
        L_0x0864:
            r52 = r3
            r25 = r14
            r13 = 0
            r2 = r80
            android.media.MediaExtractor r4 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x08e0, all -> 0x08cd }
            r4.<init>()     // Catch:{ Exception -> 0x08e0, all -> 0x08cd }
            r9 = r69
            r4.setDataSource(r9)     // Catch:{ Exception -> 0x08cb, all -> 0x08c9 }
            r4.selectTrack(r5)     // Catch:{ Exception -> 0x08cb, all -> 0x08c9 }
            int r10 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r10 <= 0) goto L_0x0896
            r10 = 0
            r4.seekTo(r2, r10)     // Catch:{ Exception -> 0x088d, all -> 0x0882 }
            goto L_0x089a
        L_0x0882:
            r0 = move-exception
            r8 = r75
            r9 = r76
            r11 = r77
            r44 = r82
            r5 = r2
            goto L_0x08db
        L_0x088d:
            r0 = move-exception
            r11 = r77
            r7 = r78
            r44 = r82
            r5 = r2
            goto L_0x08ec
        L_0x0896:
            r10 = 0
            r4.seekTo(r13, r10)     // Catch:{ Exception -> 0x08cb, all -> 0x08c9 }
        L_0x089a:
            org.telegram.messenger.video.AudioRecoder r10 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x08cb, all -> 0x08c9 }
            r10.<init>(r6, r4, r5)     // Catch:{ Exception -> 0x08cb, all -> 0x08c9 }
            r10.startTime = r2     // Catch:{ Exception -> 0x08b5, all -> 0x08c9 }
            r13 = r82
            r10.endTime = r13     // Catch:{ Exception -> 0x08b3, all -> 0x08b1 }
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x08b3, all -> 0x08b1 }
            android.media.MediaFormat r6 = r10.format     // Catch:{ Exception -> 0x08b3, all -> 0x08b1 }
            r11 = 1
            int r4 = r4.addTrack(r6, r11)     // Catch:{ Exception -> 0x08b3, all -> 0x08b1 }
            r6 = 0
            goto L_0x093a
        L_0x08b1:
            r0 = move-exception
            goto L_0x08d2
        L_0x08b3:
            r0 = move-exception
            goto L_0x08b8
        L_0x08b5:
            r0 = move-exception
            r13 = r82
        L_0x08b8:
            r11 = r77
            r7 = r78
            r5 = r2
            r59 = r10
            r44 = r13
            r1 = r26
            r10 = r52
            r40 = -5
            goto L_0x0b90
        L_0x08c9:
            r0 = move-exception
            goto L_0x08d0
        L_0x08cb:
            r0 = move-exception
            goto L_0x08e3
        L_0x08cd:
            r0 = move-exception
            r9 = r69
        L_0x08d0:
            r13 = r82
        L_0x08d2:
            r8 = r75
            r9 = r76
            r11 = r77
            r5 = r2
            r44 = r13
        L_0x08db:
            r10 = r26
            r1 = -5
            goto L_0x0b78
        L_0x08e0:
            r0 = move-exception
            r9 = r69
        L_0x08e3:
            r13 = r82
            r11 = r77
            r7 = r78
            r5 = r2
            r44 = r13
        L_0x08ec:
            r1 = r26
            r10 = r52
            r40 = -5
            r59 = 0
            goto L_0x0b90
        L_0x08f6:
            r9 = r69
            r52 = r3
            r25 = r14
            r2 = r80
            r13 = r82
            r4 = -5
            r6 = 0
            goto L_0x0939
        L_0x0903:
            r0 = move-exception
            r9 = r69
            r13 = r82
            r8 = r75
            r9 = r76
            r11 = r77
            r5 = r80
            goto L_0x1212
        L_0x0912:
            r0 = move-exception
            r9 = r69
            r52 = r3
            r25 = r14
            r13 = r82
            r11 = r77
            r7 = r78
            r5 = r80
            r2 = r0
            r44 = r13
        L_0x0924:
            r1 = r26
            r10 = r52
            r40 = -5
            goto L_0x1283
        L_0x092c:
            r9 = r69
            r52 = r3
            r25 = r14
            r2 = r80
            r13 = r82
            r4 = -5
            r6 = 0
            r8 = 1
        L_0x0939:
            r10 = 0
        L_0x093a:
            r11 = 0
        L_0x093b:
            if (r5 >= 0) goto L_0x0940
            r28 = 1
            goto L_0x0942
        L_0x0940:
            r28 = 0
        L_0x0942:
            r68.checkConversionCanceled()     // Catch:{ Exception -> 0x1172, all -> 0x116c }
            r48 = r11
            r64 = r18
            r62 = r20
            r41 = r28
            r82 = -5
            r11 = 0
            r20 = 0
            r28 = 0
            r29 = 1
            r34 = 0
            r36 = 0
            r21 = r1
            r1 = 0
        L_0x095d:
            if (r1 == 0) goto L_0x0978
            if (r8 != 0) goto L_0x0964
            if (r41 != 0) goto L_0x0964
            goto L_0x0978
        L_0x0964:
            r8 = r75
            r4 = r76
            r11 = r77
            r7 = r78
            r5 = r2
            r44 = r13
            r3 = r52
            r1 = 0
            r40 = 0
            r2 = r82
            goto L_0x12cb
        L_0x0978:
            r68.checkConversionCanceled()     // Catch:{ Exception -> 0x1153, all -> 0x1140 }
            if (r8 != 0) goto L_0x09aa
            if (r10 == 0) goto L_0x09aa
            r83 = r1
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ Exception -> 0x0998, all -> 0x0988 }
            boolean r1 = r10.step(r1, r4)     // Catch:{ Exception -> 0x0998, all -> 0x0988 }
            goto L_0x09ae
        L_0x0988:
            r0 = move-exception
            r8 = r75
            r9 = r76
            r11 = r77
            r1 = r82
            r5 = r2
            r44 = r13
            r10 = r26
            goto L_0x0b78
        L_0x0998:
            r0 = move-exception
            r11 = r77
            r7 = r78
            r40 = r82
            r5 = r2
            r59 = r10
            r44 = r13
            r1 = r26
            r10 = r52
            goto L_0x0b90
        L_0x09aa:
            r83 = r1
            r1 = r41
        L_0x09ae:
            if (r11 != 0) goto L_0x0b93
            r84 = r1
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0b7c, all -> 0x0b68 }
            int r1 = r1.getSampleTrackIndex()     // Catch:{ Exception -> 0x0b7c, all -> 0x0b68 }
            r9 = r55
            if (r1 != r9) goto L_0x0a3e
            r85 = r10
            r10 = r52
            r2 = 2500(0x9c4, double:1.235E-320)
            int r1 = r10.dequeueInputBuffer(r2)     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            if (r1 < 0) goto L_0x0a08
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            r3 = 21
            if (r2 >= r3) goto L_0x09d1
            r3 = r7[r1]     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            goto L_0x09d5
        L_0x09d1:
            java.nio.ByteBuffer r3 = r10.getInputBuffer(r1)     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
        L_0x09d5:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            r51 = r7
            r7 = 0
            int r44 = r2.readSampleData(r3, r7)     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            if (r44 >= 0) goto L_0x09f1
            r43 = 0
            r44 = 0
            r45 = 0
            r47 = 4
            r41 = r10
            r42 = r1
            r41.queueInputBuffer(r42, r43, r44, r45, r47)     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            r1 = 1
            goto L_0x0a0b
        L_0x09f1:
            r43 = 0
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            long r45 = r2.getSampleTime()     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            r47 = 0
            r41 = r10
            r42 = r1
            r41.queueInputBuffer(r42, r43, r44, r45, r47)     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            r1.advance()     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            goto L_0x0a0a
        L_0x0a08:
            r51 = r7
        L_0x0a0a:
            r1 = r11
        L_0x0a0b:
            r11 = r1
            r54 = r4
            r52 = r5
            r2 = r6
            r38 = r13
            r1 = 0
            r6 = r80
            goto L_0x0b2e
        L_0x0a18:
            r0 = move-exception
            r8 = r75
            r9 = r76
            r11 = r77
            r5 = r80
            r1 = r82
            r2 = r0
        L_0x0a24:
            r44 = r13
        L_0x0a26:
            r10 = r26
            goto L_0x1390
        L_0x0a2a:
            r0 = move-exception
            r11 = r77
            r7 = r78
            r5 = r80
            r40 = r82
            r59 = r85
            r2 = r0
            r55 = r9
            r44 = r13
        L_0x0a3a:
            r1 = r26
            goto L_0x1285
        L_0x0a3e:
            r51 = r7
            r85 = r10
            r10 = r52
            if (r8 == 0) goto L_0x0b1f
            r2 = -1
            if (r5 == r2) goto L_0x0b1f
            if (r1 != r5) goto L_0x0b1f
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b09, all -> 0x0afc }
            r3 = 28
            if (r1 < r3) goto L_0x0a65
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            long r41 = r3.getSampleSize()     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            long r2 = (long) r6     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            int r7 = (r41 > r2 ? 1 : (r41 == r2 ? 0 : -1))
            if (r7 <= 0) goto L_0x0a65
            r2 = 1024(0x400, double:5.06E-321)
            long r2 = r41 + r2
            int r6 = (int) r2     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            java.nio.ByteBuffer r48 = java.nio.ByteBuffer.allocateDirect(r6)     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
        L_0x0a65:
            r2 = r48
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x0b09, all -> 0x0afc }
            r7 = 0
            int r3 = r3.readSampleData(r2, r7)     // Catch:{ Exception -> 0x0b09, all -> 0x0afc }
            r12.size = r3     // Catch:{ Exception -> 0x0b09, all -> 0x0afc }
            r3 = 21
            if (r1 >= r3) goto L_0x0a7c
            r2.position(r7)     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            int r1 = r12.size     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            r2.limit(r1)     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
        L_0x0a7c:
            int r1 = r12.size     // Catch:{ Exception -> 0x0b09, all -> 0x0afc }
            if (r1 < 0) goto L_0x0a90
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            r7 = r4
            long r3 = r1.getSampleTime()     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            r12.presentationTimeUs = r3     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            r1.advance()     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            r1 = r11
            goto L_0x0a95
        L_0x0a90:
            r7 = r4
            r1 = 0
            r12.size = r1     // Catch:{ Exception -> 0x0b09, all -> 0x0afc }
            r1 = 1
        L_0x0a95:
            int r3 = r12.size     // Catch:{ Exception -> 0x0b09, all -> 0x0afc }
            if (r3 <= 0) goto L_0x0aea
            r3 = 0
            int r11 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1))
            if (r11 < 0) goto L_0x0aa5
            long r3 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0a2a, all -> 0x0a18 }
            int r11 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r11 >= 0) goto L_0x0aea
        L_0x0aa5:
            r3 = 0
            r12.offset = r3     // Catch:{ Exception -> 0x0b09, all -> 0x0afc }
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x0b09, all -> 0x0afc }
            int r4 = r4.getSampleFlags()     // Catch:{ Exception -> 0x0b09, all -> 0x0afc }
            r12.flags = r4     // Catch:{ Exception -> 0x0b09, all -> 0x0afc }
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x0b09, all -> 0x0afc }
            r52 = r5
            long r4 = r4.writeSampleData(r7, r2, r12, r3)     // Catch:{ Exception -> 0x0b09, all -> 0x0afc }
            r41 = 0
            int r3 = (r4 > r41 ? 1 : (r4 == r41 ? 0 : -1))
            if (r3 == 0) goto L_0x0ae6
            org.telegram.messenger.MediaController$VideoConvertorListener r3 = r15.callback     // Catch:{ Exception -> 0x0b09, all -> 0x0afc }
            if (r3 == 0) goto L_0x0ae6
            r11 = r1
            r41 = r2
            long r1 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0b09, all -> 0x0afc }
            r42 = r6
            r54 = r7
            r38 = r13
            r13 = 2500(0x9c4, double:1.235E-320)
            r6 = r80
            long r43 = r1 - r6
            int r45 = (r43 > r34 ? 1 : (r43 == r34 ? 0 : -1))
            if (r45 <= 0) goto L_0x0ad9
            long r34 = r1 - r6
        L_0x0ad9:
            r1 = r34
            float r13 = (float) r1
            float r13 = r13 / r23
            float r13 = r13 / r24
            r3.didWriteData(r4, r13)     // Catch:{ Exception -> 0x0b55, all -> 0x0b48 }
            r34 = r1
            goto L_0x0af7
        L_0x0ae6:
            r11 = r1
            r41 = r2
            goto L_0x0aef
        L_0x0aea:
            r11 = r1
            r41 = r2
            r52 = r5
        L_0x0aef:
            r42 = r6
            r54 = r7
            r38 = r13
            r6 = r80
        L_0x0af7:
            r48 = r41
            r2 = r42
            goto L_0x0b2d
        L_0x0afc:
            r0 = move-exception
            r38 = r13
            r8 = r75
            r9 = r76
            r11 = r77
            r5 = r80
            goto L_0x114a
        L_0x0b09:
            r0 = move-exception
            r38 = r13
            r11 = r77
            r7 = r78
            r5 = r80
            r40 = r82
            r59 = r85
            r2 = r0
            r55 = r9
            r1 = r26
            r44 = r38
            goto L_0x1285
        L_0x0b1f:
            r54 = r4
            r52 = r5
            r2 = r6
            r38 = r13
            r6 = r80
            r3 = -1
            if (r1 != r3) goto L_0x0b2d
            r1 = 1
            goto L_0x0b2e
        L_0x0b2d:
            r1 = 0
        L_0x0b2e:
            if (r1 == 0) goto L_0x0ba8
            r3 = 2500(0x9c4, double:1.235E-320)
            int r42 = r10.dequeueInputBuffer(r3)     // Catch:{ Exception -> 0x0b55, all -> 0x0b48 }
            if (r42 < 0) goto L_0x0ba8
            r43 = 0
            r44 = 0
            r45 = 0
            r47 = 4
            r41 = r10
            r41.queueInputBuffer(r42, r43, r44, r45, r47)     // Catch:{ Exception -> 0x0b55, all -> 0x0b48 }
            r1 = 1
            goto L_0x0ba9
        L_0x0b48:
            r0 = move-exception
            r8 = r75
            r9 = r76
            r11 = r77
            r1 = r82
            r2 = r0
            r5 = r6
            goto L_0x114d
        L_0x0b55:
            r0 = move-exception
            r11 = r77
            r40 = r82
            r59 = r85
            r2 = r0
            r5 = r6
            r55 = r9
            r1 = r26
            r44 = r38
            r7 = r78
            goto L_0x1285
        L_0x0b68:
            r0 = move-exception
            r38 = r13
            r8 = r75
            r9 = r76
            r11 = r77
            r1 = r82
            r5 = r2
            r10 = r26
            r44 = r38
        L_0x0b78:
            r40 = 0
            goto L_0x12e9
        L_0x0b7c:
            r0 = move-exception
            r85 = r10
            r38 = r13
            r10 = r52
            r11 = r77
            r7 = r78
            r40 = r82
            r59 = r85
            r5 = r2
            r1 = r26
            r44 = r38
        L_0x0b90:
            r2 = r0
            goto L_0x1285
        L_0x0b93:
            r84 = r1
            r54 = r4
            r51 = r7
            r85 = r10
            r38 = r13
            r10 = r52
            r9 = r55
            r52 = r5
            r66 = r2
            r2 = r6
            r6 = r66
        L_0x0ba8:
            r1 = r11
        L_0x0ba9:
            r3 = r20 ^ 1
            r5 = r83
            r11 = r1
            r41 = r8
            r55 = r9
            r13 = r38
            r8 = r62
            r4 = 1
            r1 = r82
        L_0x0bb9:
            if (r3 != 0) goto L_0x0bda
            if (r4 == 0) goto L_0x0bbe
            goto L_0x0bda
        L_0x0bbe:
            r82 = r1
            r1 = r5
            r62 = r8
            r8 = r41
            r5 = r52
            r4 = r54
            r9 = r69
            r41 = r84
            r52 = r10
            r10 = r85
            r66 = r6
            r6 = r2
            r2 = r66
            r7 = r51
            goto L_0x095d
        L_0x0bda:
            r68.checkConversionCanceled()     // Catch:{ Exception -> 0x112d, all -> 0x111e }
            if (r89 == 0) goto L_0x0be8
            r38 = 22000(0x55f0, double:1.08694E-319)
            r83 = r2
            r82 = r3
            r2 = r38
            goto L_0x0bee
        L_0x0be8:
            r83 = r2
            r82 = r3
            r2 = 2500(0x9c4, double:1.235E-320)
        L_0x0bee:
            r66 = r4
            r4 = r78
            r78 = r66
            int r2 = r4.dequeueOutputBuffer(r12, r2)     // Catch:{ Exception -> 0x111a, all -> 0x111e }
            r3 = -1
            if (r2 != r3) goto L_0x0CLASSNAME
            r38 = r5
            r42 = r8
            r39 = r11
            r44 = r13
            r6 = r22
            r11 = r50
            r5 = r53
            r3 = r58
            r9 = r61
            r7 = 0
        L_0x0c0e:
            r8 = 3
            r13 = -1
            goto L_0x0e36
        L_0x0CLASSNAME:
            r3 = -3
            if (r2 != r3) goto L_0x0c4c
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c3f, all -> 0x0CLASSNAME }
            r38 = r5
            r5 = 21
            if (r3 >= r5) goto L_0x0CLASSNAME
            java.nio.ByteBuffer[] r21 = r4.getOutputBuffers()     // Catch:{ Exception -> 0x0c3f, all -> 0x0CLASSNAME }
        L_0x0CLASSNAME:
            r7 = r78
            r42 = r8
            r39 = r11
            r44 = r13
            r6 = r22
            r11 = r50
            r5 = r53
            r3 = r58
        L_0x0CLASSNAME:
            r9 = r61
            goto L_0x0c0e
        L_0x0CLASSNAME:
            r0 = move-exception
        L_0x0CLASSNAME:
            r8 = r75
            r9 = r76
            r11 = r77
            r2 = r0
            r5 = r6
            goto L_0x0a24
        L_0x0c3f:
            r0 = move-exception
            r11 = r77
            r59 = r85
            r2 = r0
            r40 = r1
            r5 = r6
            r44 = r13
            goto L_0x113c
        L_0x0c4c:
            r38 = r5
            r5 = -2
            if (r2 != r5) goto L_0x0cd3
            android.media.MediaFormat r5 = r4.getOutputFormat()     // Catch:{ Exception -> 0x0c3f, all -> 0x0CLASSNAME }
            r3 = -5
            if (r1 != r3) goto L_0x0cc1
            if (r5 == 0) goto L_0x0cc1
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ Exception -> 0x0c3f, all -> 0x0CLASSNAME }
            r39 = r11
            r11 = 0
            int r1 = r3.addTrack(r5, r11)     // Catch:{ Exception -> 0x0c3f, all -> 0x0CLASSNAME }
            r3 = r56
            boolean r11 = r5.containsKey(r3)     // Catch:{ Exception -> 0x0cb0, all -> 0x0cac }
            if (r11 == 0) goto L_0x0ca1
            int r11 = r5.getInteger(r3)     // Catch:{ Exception -> 0x0cb0, all -> 0x0cac }
            r42 = r1
            r1 = 1
            if (r11 != r1) goto L_0x0ca3
            r11 = r50
            java.nio.ByteBuffer r1 = r5.getByteBuffer(r11)     // Catch:{ Exception -> 0x0c9f, all -> 0x0c8e }
            r56 = r3
            r3 = r58
            java.nio.ByteBuffer r5 = r5.getByteBuffer(r3)     // Catch:{ Exception -> 0x0c9f, all -> 0x0c8e }
            int r1 = r1.limit()     // Catch:{ Exception -> 0x0c9f, all -> 0x0c8e }
            int r5 = r5.limit()     // Catch:{ Exception -> 0x0c9f, all -> 0x0c8e }
            int r1 = r1 + r5
            r28 = r1
            goto L_0x0ca9
        L_0x0c8e:
            r0 = move-exception
            r8 = r75
            r9 = r76
            r11 = r77
            r2 = r0
            r5 = r6
            r44 = r13
            r10 = r26
            r1 = r42
            goto L_0x1390
        L_0x0c9f:
            r0 = move-exception
            goto L_0x0cb3
        L_0x0ca1:
            r42 = r1
        L_0x0ca3:
            r56 = r3
            r11 = r50
            r3 = r58
        L_0x0ca9:
            r1 = r42
            goto L_0x0cc7
        L_0x0cac:
            r0 = move-exception
            r42 = r1
            goto L_0x0CLASSNAME
        L_0x0cb0:
            r0 = move-exception
            r42 = r1
        L_0x0cb3:
            r11 = r77
            r59 = r85
            r2 = r0
            r5 = r6
            r44 = r13
            r1 = r26
            r40 = r42
            goto L_0x11f0
        L_0x0cc1:
            r39 = r11
            r11 = r50
            r3 = r58
        L_0x0cc7:
            r7 = r78
            r42 = r8
            r44 = r13
            r6 = r22
            r5 = r53
            goto L_0x0CLASSNAME
        L_0x0cd3:
            r39 = r11
            r11 = r50
            r3 = r58
            if (r2 < 0) goto L_0x10e9
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x111a, all -> 0x111e }
            r42 = r8
            r8 = 21
            if (r5 >= r8) goto L_0x0ce6
            r5 = r21[r2]     // Catch:{ Exception -> 0x0c3f, all -> 0x0CLASSNAME }
            goto L_0x0cea
        L_0x0ce6:
            java.nio.ByteBuffer r5 = r4.getOutputBuffer(r2)     // Catch:{ Exception -> 0x111a, all -> 0x111e }
        L_0x0cea:
            if (r5 == 0) goto L_0x10c4
            int r9 = r12.size     // Catch:{ Exception -> 0x111a, all -> 0x111e }
            r8 = 1
            if (r9 <= r8) goto L_0x0e20
            int r8 = r12.flags     // Catch:{ Exception -> 0x0e1b, all -> 0x0e16 }
            r38 = r8 & 2
            if (r38 != 0) goto L_0x0da1
            if (r28 == 0) goto L_0x0d20
            r38 = r8 & 1
            if (r38 == 0) goto L_0x0d20
            r44 = r13
            int r13 = r12.offset     // Catch:{ Exception -> 0x0d15, all -> 0x0d0a }
            int r13 = r13 + r28
            r12.offset = r13     // Catch:{ Exception -> 0x0d15, all -> 0x0d0a }
            int r9 = r9 - r28
            r12.size = r9     // Catch:{ Exception -> 0x0d15, all -> 0x0d0a }
            goto L_0x0d22
        L_0x0d0a:
            r0 = move-exception
            r8 = r75
            r9 = r76
            r11 = r77
            r2 = r0
            r5 = r6
            goto L_0x0a26
        L_0x0d15:
            r0 = move-exception
            r11 = r77
            r59 = r85
            r2 = r0
            r40 = r1
            r5 = r6
            goto L_0x113c
        L_0x0d20:
            r44 = r13
        L_0x0d22:
            if (r29 == 0) goto L_0x0d71
            r8 = r8 & 1
            if (r8 == 0) goto L_0x0d71
            int r8 = r12.size     // Catch:{ Exception -> 0x0d15, all -> 0x0d0a }
            r13 = 100
            if (r8 <= r13) goto L_0x0d6f
            int r8 = r12.offset     // Catch:{ Exception -> 0x0d15, all -> 0x0d0a }
            r5.position(r8)     // Catch:{ Exception -> 0x0d15, all -> 0x0d0a }
            byte[] r8 = new byte[r13]     // Catch:{ Exception -> 0x0d15, all -> 0x0d0a }
            r5.get(r8)     // Catch:{ Exception -> 0x0d15, all -> 0x0d0a }
            r9 = 0
            r14 = 0
        L_0x0d3a:
            r13 = 96
            if (r9 >= r13) goto L_0x0d6f
            byte r13 = r8[r9]     // Catch:{ Exception -> 0x0d15, all -> 0x0d0a }
            if (r13 != 0) goto L_0x0d66
            int r13 = r9 + 1
            byte r13 = r8[r13]     // Catch:{ Exception -> 0x0d15, all -> 0x0d0a }
            if (r13 != 0) goto L_0x0d66
            int r13 = r9 + 2
            byte r13 = r8[r13]     // Catch:{ Exception -> 0x0d15, all -> 0x0d0a }
            if (r13 != 0) goto L_0x0d66
            int r13 = r9 + 3
            byte r13 = r8[r13]     // Catch:{ Exception -> 0x0d15, all -> 0x0d0a }
            r29 = r8
            r8 = 1
            if (r13 != r8) goto L_0x0d68
            int r14 = r14 + 1
            if (r14 <= r8) goto L_0x0d68
            int r8 = r12.offset     // Catch:{ Exception -> 0x0d15, all -> 0x0d0a }
            int r8 = r8 + r9
            r12.offset = r8     // Catch:{ Exception -> 0x0d15, all -> 0x0d0a }
            int r8 = r12.size     // Catch:{ Exception -> 0x0d15, all -> 0x0d0a }
            int r8 = r8 - r9
            r12.size = r8     // Catch:{ Exception -> 0x0d15, all -> 0x0d0a }
            goto L_0x0d6f
        L_0x0d66:
            r29 = r8
        L_0x0d68:
            int r9 = r9 + 1
            r8 = r29
            r13 = 100
            goto L_0x0d3a
        L_0x0d6f:
            r29 = 0
        L_0x0d71:
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            r9 = 1
            long r13 = r8.writeSampleData(r1, r5, r12, r9)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            r8 = 0
            int r5 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1))
            if (r5 == 0) goto L_0x0d98
            org.telegram.messenger.MediaController$VideoConvertorListener r5 = r15.callback     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            if (r5 == 0) goto L_0x0d98
            long r8 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            long r46 = r8 - r6
            int r38 = (r46 > r34 ? 1 : (r46 == r34 ? 0 : -1))
            if (r38 <= 0) goto L_0x0d8c
            long r34 = r8 - r6
        L_0x0d8c:
            r8 = r34
            float r6 = (float) r8     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            float r6 = r6 / r23
            float r6 = r6 / r24
            r5.didWriteData(r13, r6)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            r34 = r8
        L_0x0d98:
            r6 = r22
            r5 = r53
            r9 = r61
            r8 = 3
            goto L_0x0e24
        L_0x0da1:
            r44 = r13
            r6 = -5
            if (r1 != r6) goto L_0x0d98
            byte[] r6 = new byte[r9]     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            int r7 = r12.offset     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            int r7 = r7 + r9
            r5.limit(r7)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            int r7 = r12.offset     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            r5.position(r7)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            r5.get(r6)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            int r5 = r12.size     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            r7 = 1
            int r5 = r5 - r7
        L_0x0dba:
            r8 = 3
            if (r5 < 0) goto L_0x0df8
            if (r5 <= r8) goto L_0x0df8
            byte r9 = r6[r5]     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            if (r9 != r7) goto L_0x0df4
            int r9 = r5 + -1
            byte r9 = r6[r9]     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            if (r9 != 0) goto L_0x0df4
            int r9 = r5 + -2
            byte r9 = r6[r9]     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            if (r9 != 0) goto L_0x0df4
            int r9 = r5 + -3
            byte r13 = r6[r9]     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            if (r13 != 0) goto L_0x0df4
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocate(r9)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            int r13 = r12.size     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            int r13 = r13 - r9
            java.nio.ByteBuffer r13 = java.nio.ByteBuffer.allocate(r13)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            r14 = 0
            java.nio.ByteBuffer r7 = r5.put(r6, r14, r9)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            r7.position(r14)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            int r7 = r12.size     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            int r7 = r7 - r9
            java.nio.ByteBuffer r6 = r13.put(r6, r9, r7)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            r6.position(r14)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            r7 = r5
            goto L_0x0dfa
        L_0x0df4:
            int r5 = r5 + -1
            r7 = 1
            goto L_0x0dba
        L_0x0df8:
            r7 = 0
            r13 = 0
        L_0x0dfa:
            r6 = r22
            r5 = r53
            r9 = r61
            android.media.MediaFormat r14 = android.media.MediaFormat.createVideoFormat(r9, r5, r6)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            if (r7 == 0) goto L_0x0e0e
            if (r13 == 0) goto L_0x0e0e
            r14.setByteBuffer(r11, r7)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            r14.setByteBuffer(r3, r13)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
        L_0x0e0e:
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            r13 = 0
            int r1 = r7.addTrack(r14, r13)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            goto L_0x0e24
        L_0x0e16:
            r0 = move-exception
            r44 = r13
            goto L_0x0ea4
        L_0x0e1b:
            r0 = move-exception
            r44 = r13
            goto L_0x0eaf
        L_0x0e20:
            r44 = r13
            goto L_0x0d98
        L_0x0e24:
            int r7 = r12.flags     // Catch:{ Exception -> 0x10b6, all -> 0x10af }
            r7 = r7 & 4
            if (r7 == 0) goto L_0x0e2c
            r7 = 1
            goto L_0x0e2d
        L_0x0e2c:
            r7 = 0
        L_0x0e2d:
            r13 = 0
            r4.releaseOutputBuffer(r2, r13)     // Catch:{ Exception -> 0x10b6, all -> 0x10af }
            r38 = r7
            r13 = -1
            r7 = r78
        L_0x0e36:
            if (r2 == r13) goto L_0x0e55
            r2 = r83
            r58 = r3
            r78 = r4
            r53 = r5
            r22 = r6
            r4 = r7
            r61 = r9
            r50 = r11
            r5 = r38
            r11 = r39
            r8 = r42
            r13 = r44
            r6 = r80
            r3 = r82
            goto L_0x0bb9
        L_0x0e55:
            if (r20 != 0) goto L_0x107d
            r61 = r9
            r8 = 2500(0x9c4, double:1.235E-320)
            int r2 = r10.dequeueOutputBuffer(r12, r8)     // Catch:{ Exception -> 0x1074, all -> 0x106b }
            if (r2 != r13) goto L_0x0e76
            r40 = r1
            r53 = r5
            r22 = r6
            r78 = r7
            r50 = r11
            r13 = r44
            r7 = r49
            r1 = 0
            r11 = r77
            r5 = r80
            goto L_0x1097
        L_0x0e76:
            r14 = -3
            if (r2 != r14) goto L_0x0e83
        L_0x0e79:
            r40 = r1
            r53 = r5
            r22 = r6
            r78 = r7
            goto L_0x1087
        L_0x0e83:
            r14 = -2
            if (r2 != r14) goto L_0x0ebb
            android.media.MediaFormat r2 = r10.getOutputFormat()     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            boolean r14 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            if (r14 == 0) goto L_0x0e79
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            r14.<init>()     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            java.lang.String r8 = "newFormat = "
            r14.append(r8)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            r14.append(r2)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            java.lang.String r2 = r14.toString()     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            goto L_0x0e79
        L_0x0ea3:
            r0 = move-exception
        L_0x0ea4:
            r8 = r75
            r9 = r76
            r11 = r77
            r5 = r80
            goto L_0x112a
        L_0x0eae:
            r0 = move-exception
        L_0x0eaf:
            r11 = r77
            r5 = r80
        L_0x0eb3:
            r59 = r85
            r2 = r0
            r40 = r1
        L_0x0eb8:
            r7 = r4
            goto L_0x0a3a
        L_0x0ebb:
            if (r2 < 0) goto L_0x104c
            int r8 = r12.size     // Catch:{ Exception -> 0x1074, all -> 0x106b }
            if (r8 == 0) goto L_0x0ec3
            r8 = 1
            goto L_0x0ec4
        L_0x0ec3:
            r8 = 0
        L_0x0ec4:
            long r13 = r12.presentationTimeUs     // Catch:{ Exception -> 0x1074, all -> 0x106b }
            r46 = 0
            int r9 = (r44 > r46 ? 1 : (r44 == r46 ? 0 : -1))
            if (r9 <= 0) goto L_0x0edb
            int r9 = (r13 > r44 ? 1 : (r13 == r44 ? 0 : -1))
            if (r9 < 0) goto L_0x0edb
            int r8 = r12.flags     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            r8 = r8 | 4
            r12.flags = r8     // Catch:{ Exception -> 0x0eae, all -> 0x0ea3 }
            r8 = 0
            r20 = 1
            r39 = 1
        L_0x0edb:
            r46 = 0
            int r9 = (r32 > r46 ? 1 : (r32 == r46 ? 0 : -1))
            if (r9 < 0) goto L_0x0var_
            int r9 = r12.flags     // Catch:{ Exception -> 0x0var_, all -> 0x0f6d }
            r9 = r9 & 4
            if (r9 == 0) goto L_0x0var_
            r53 = r5
            r22 = r6
            r5 = r80
            long r46 = r32 - r5
            long r46 = java.lang.Math.abs(r46)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r9 = 1000000(0xvar_, float:1.401298E-39)
            r50 = r11
            r11 = r77
            int r9 = r9 / r11
            r78 = r7
            r57 = r8
            long r7 = (long) r9
            int r9 = (r46 > r7 ? 1 : (r46 == r7 ? 0 : -1))
            if (r9 <= 0) goto L_0x0f5c
            r7 = 0
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 <= 0) goto L_0x0var_
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r8 = 0
            r7.seekTo(r5, r8)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r40 = r1
            r1 = 0
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            goto L_0x1126
        L_0x0var_:
            r0 = move-exception
            goto L_0x0eb3
        L_0x0var_:
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r40 = r1
            r1 = 0
            r8 = 0
            r7.seekTo(r8, r1)     // Catch:{ Exception -> 0x0fdf, all -> 0x1109 }
        L_0x0var_:
            long r36 = r42 + r30
            int r7 = r12.flags     // Catch:{ Exception -> 0x0f4f, all -> 0x0f3d }
            r8 = -5
            r7 = r7 & r8
            r12.flags = r7     // Catch:{ Exception -> 0x0f4f, all -> 0x0f3d }
            r10.flush()     // Catch:{ Exception -> 0x0f4f, all -> 0x0f3d }
            r44 = r32
            r7 = 1
            r20 = 0
            r39 = 0
            r46 = 0
            r57 = 0
            r32 = r18
            goto L_0x0var_
        L_0x0f3d:
            r0 = move-exception
            r8 = r75
            r9 = r76
            r2 = r0
            r10 = r26
            r44 = r32
            r1 = r40
            r40 = 0
            r32 = r18
            goto L_0x1392
        L_0x0f4f:
            r0 = move-exception
            r59 = r85
            r2 = r0
            r7 = r4
            r1 = r26
            r44 = r32
            r32 = r18
            goto L_0x1285
        L_0x0f5c:
            r40 = r1
            r1 = 0
            r8 = -5
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            goto L_0x0f7c
        L_0x0var_:
            r0 = move-exception
            r11 = r77
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            r11 = r77
            goto L_0x0f7c
        L_0x0f6d:
            r0 = move-exception
            r11 = r77
            r5 = r80
        L_0x0var_:
            r40 = r1
            r1 = 0
            goto L_0x110a
        L_0x0var_:
            r0 = move-exception
            r11 = r77
            r5 = r80
        L_0x0f7c:
            r40 = r1
            r1 = 0
        L_0x0f7f:
            r59 = r85
            r2 = r0
            goto L_0x0eb8
        L_0x0var_:
            r40 = r1
            r53 = r5
            r22 = r6
            r78 = r7
            r57 = r8
            r50 = r11
            r1 = 0
            r8 = -5
            r11 = r77
            r5 = r80
        L_0x0var_:
            r7 = 0
            r46 = 0
        L_0x0var_:
            int r9 = (r32 > r46 ? 1 : (r32 == r46 ? 0 : -1))
            if (r9 < 0) goto L_0x0fa0
            r8 = r32
            goto L_0x0fa1
        L_0x0fa0:
            r8 = r5
        L_0x0fa1:
            int r58 = (r8 > r46 ? 1 : (r8 == r46 ? 0 : -1))
            if (r58 <= 0) goto L_0x0fe1
            int r46 = (r64 > r18 ? 1 : (r64 == r18 ? 0 : -1))
            if (r46 != 0) goto L_0x0fe1
            int r46 = (r13 > r8 ? 1 : (r13 == r8 ? 0 : -1))
            if (r46 >= 0) goto L_0x0fd1
            boolean r13 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0fdf, all -> 0x1109 }
            if (r13 == 0) goto L_0x0fcf
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0fdf, all -> 0x1109 }
            r13.<init>()     // Catch:{ Exception -> 0x0fdf, all -> 0x1109 }
            java.lang.String r14 = "drop frame startTime = "
            r13.append(r14)     // Catch:{ Exception -> 0x0fdf, all -> 0x1109 }
            r13.append(r8)     // Catch:{ Exception -> 0x0fdf, all -> 0x1109 }
            java.lang.String r8 = " present time = "
            r13.append(r8)     // Catch:{ Exception -> 0x0fdf, all -> 0x1109 }
            long r8 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0fdf, all -> 0x1109 }
            r13.append(r8)     // Catch:{ Exception -> 0x0fdf, all -> 0x1109 }
            java.lang.String r8 = r13.toString()     // Catch:{ Exception -> 0x0fdf, all -> 0x1109 }
            org.telegram.messenger.FileLog.d(r8)     // Catch:{ Exception -> 0x0fdf, all -> 0x1109 }
        L_0x0fcf:
            r8 = 0
            goto L_0x0fe3
        L_0x0fd1:
            long r8 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0fdf, all -> 0x1109 }
            r13 = -2147483648(0xfffffffvar_, double:NaN)
            int r46 = (r42 > r13 ? 1 : (r42 == r13 ? 0 : -1))
            if (r46 == 0) goto L_0x0fdc
            long r36 = r36 - r8
        L_0x0fdc:
            r64 = r8
            goto L_0x0fe1
        L_0x0fdf:
            r0 = move-exception
            goto L_0x0f7f
        L_0x0fe1:
            r8 = r57
        L_0x0fe3:
            if (r7 == 0) goto L_0x0fe8
            r64 = r18
            goto L_0x0ffb
        L_0x0fe8:
            int r7 = (r32 > r18 ? 1 : (r32 == r18 ? 0 : -1))
            if (r7 != 0) goto L_0x0ff8
            r13 = 0
            int r7 = (r36 > r13 ? 1 : (r36 == r13 ? 0 : -1))
            if (r7 == 0) goto L_0x0ff8
            long r13 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0fdf, all -> 0x1109 }
            long r13 = r13 + r36
            r12.presentationTimeUs = r13     // Catch:{ Exception -> 0x0fdf, all -> 0x1109 }
        L_0x0ff8:
            r10.releaseOutputBuffer(r2, r8)     // Catch:{ Exception -> 0x1049, all -> 0x1109 }
        L_0x0ffb:
            if (r8 == 0) goto L_0x102d
            r7 = 0
            int r2 = (r32 > r7 ? 1 : (r32 == r7 ? 0 : -1))
            if (r2 < 0) goto L_0x100c
            long r13 = r12.presentationTimeUs     // Catch:{ Exception -> 0x0fdf, all -> 0x1109 }
            r1 = r42
            long r1 = java.lang.Math.max(r1, r13)     // Catch:{ Exception -> 0x0fdf, all -> 0x1109 }
            goto L_0x100e
        L_0x100c:
            r1 = r42
        L_0x100e:
            r25.awaitNewImage()     // Catch:{ Exception -> 0x1013, all -> 0x1109 }
            r13 = 0
            goto L_0x1019
        L_0x1013:
            r0 = move-exception
            r13 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)     // Catch:{ Exception -> 0x1049, all -> 0x1109 }
            r13 = 1
        L_0x1019:
            if (r13 != 0) goto L_0x102f
            r25.drawImage()     // Catch:{ Exception -> 0x1049, all -> 0x1109 }
            long r13 = r12.presentationTimeUs     // Catch:{ Exception -> 0x1049, all -> 0x1109 }
            r42 = 1000(0x3e8, double:4.94E-321)
            long r13 = r13 * r42
            r7 = r49
            r7.setPresentationTime(r13)     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            r7.swapBuffers()     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            goto L_0x1031
        L_0x102d:
            r1 = r42
        L_0x102f:
            r7 = r49
        L_0x1031:
            int r8 = r12.flags     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            r8 = r8 & 4
            if (r8 == 0) goto L_0x1091
            boolean r8 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            if (r8 == 0) goto L_0x1040
            java.lang.String r8 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r8)     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
        L_0x1040:
            r4.signalEndOfInputStream()     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            r42 = r1
            r13 = r44
            r1 = 0
            goto L_0x1097
        L_0x1049:
            r0 = move-exception
            goto L_0x1137
        L_0x104c:
            r11 = r77
            r5 = r80
            r40 = r1
            r7 = r49
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            r3.<init>()     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            java.lang.String r8 = "unexpected result from decoder.dequeueOutputBuffer: "
            r3.append(r8)     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            r3.append(r2)     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            java.lang.String r2 = r3.toString()     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            throw r1     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
        L_0x106b:
            r0 = move-exception
            r11 = r77
            r5 = r80
            r40 = r1
            goto L_0x1126
        L_0x1074:
            r0 = move-exception
            r11 = r77
            r5 = r80
            r40 = r1
            goto L_0x1137
        L_0x107d:
            r40 = r1
            r53 = r5
            r22 = r6
            r78 = r7
            r61 = r9
        L_0x1087:
            r50 = r11
            r1 = r42
            r7 = r49
            r11 = r77
            r5 = r80
        L_0x1091:
            r42 = r1
            r13 = r44
            r1 = r82
        L_0x1097:
            r2 = r83
            r58 = r3
            r49 = r7
            r11 = r39
            r8 = r42
            r3 = r1
            r6 = r5
            r5 = r38
            r1 = r40
            r66 = r4
            r4 = r78
            r78 = r66
            goto L_0x0bb9
        L_0x10af:
            r0 = move-exception
            r11 = r77
            r5 = r80
            goto L_0x1126
        L_0x10b6:
            r0 = move-exception
            r11 = r77
            r5 = r80
            r7 = r49
            r59 = r85
            r2 = r0
            r40 = r1
            goto L_0x113c
        L_0x10c4:
            r11 = r77
            r40 = r1
            r5 = r6
            r44 = r13
            r7 = r49
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            r3.<init>()     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            java.lang.String r8 = "encoderOutputBuffer "
            r3.append(r8)     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            r3.append(r2)     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            java.lang.String r2 = " was null"
            r3.append(r2)     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            java.lang.String r2 = r3.toString()     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            throw r1     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
        L_0x10e9:
            r11 = r77
            r40 = r1
            r5 = r6
            r44 = r13
            r7 = r49
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            r3.<init>()     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            java.lang.String r8 = "unexpected result from encoder.dequeueOutputBuffer: "
            r3.append(r8)     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            r3.append(r2)     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            java.lang.String r2 = r3.toString()     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
            throw r1     // Catch:{ Exception -> 0x1113, all -> 0x1109 }
        L_0x1109:
            r0 = move-exception
        L_0x110a:
            r8 = r75
            r9 = r76
            r2 = r0
            r10 = r26
            goto L_0x1308
        L_0x1113:
            r0 = move-exception
            r59 = r85
            r2 = r0
            r49 = r7
            goto L_0x113c
        L_0x111a:
            r0 = move-exception
            r11 = r77
            goto L_0x1132
        L_0x111e:
            r0 = move-exception
            r11 = r77
            r40 = r1
            r5 = r6
            r44 = r13
        L_0x1126:
            r8 = r75
            r9 = r76
        L_0x112a:
            r2 = r0
            goto L_0x0a26
        L_0x112d:
            r0 = move-exception
            r11 = r77
            r4 = r78
        L_0x1132:
            r40 = r1
            r5 = r6
            r44 = r13
        L_0x1137:
            r7 = r49
            r59 = r85
            r2 = r0
        L_0x113c:
            r1 = r26
            goto L_0x11f0
        L_0x1140:
            r0 = move-exception
            r11 = r77
            r5 = r2
            r38 = r13
            r8 = r75
            r9 = r76
        L_0x114a:
            r1 = r82
            r2 = r0
        L_0x114d:
            r10 = r26
            r44 = r38
            goto L_0x1390
        L_0x1153:
            r0 = move-exception
            r11 = r77
            r4 = r78
            r5 = r2
            r85 = r10
            r38 = r13
            r7 = r49
            r10 = r52
            r40 = r82
            r59 = r85
            r2 = r0
            r1 = r26
            r44 = r38
            goto L_0x11f0
        L_0x116c:
            r0 = move-exception
            r11 = r77
            r5 = r2
            goto L_0x120e
        L_0x1172:
            r0 = move-exception
            r11 = r77
            r4 = r78
            r5 = r2
            r85 = r10
            r7 = r49
            r10 = r52
            r59 = r85
            r2 = r0
            r44 = r13
            r1 = r26
            r40 = -5
            goto L_0x11f0
        L_0x1189:
            r0 = move-exception
            r11 = r77
            r4 = r78
            r5 = r80
            r10 = r3
            r25 = r14
            r7 = r49
            goto L_0x11a3
        L_0x1196:
            r0 = move-exception
            r11 = r77
            r4 = r78
            r5 = r80
            r25 = r14
            r7 = r49
            r10 = r52
        L_0x11a3:
            r13 = r82
            r2 = r0
            r44 = r13
            r1 = r26
            goto L_0x11ec
        L_0x11ab:
            r0 = move-exception
            r11 = r77
            r5 = r80
            goto L_0x120c
        L_0x11b2:
            r0 = move-exception
            r11 = r77
            r4 = r78
            r5 = r80
            r13 = r82
            r7 = r49
            r10 = r52
            r2 = r0
            goto L_0x11d1
        L_0x11c1:
            r0 = move-exception
            r32 = r84
            r7 = r2
            r10 = r3
            r4 = r5
            r11 = r14
            r55 = r22
            r5 = r80
            r13 = r82
            r2 = r0
            r49 = r7
        L_0x11d1:
            r44 = r13
            r1 = r26
            goto L_0x11ea
        L_0x11d6:
            r0 = move-exception
            r32 = r84
            r7 = r2
            r4 = r5
            r11 = r14
            r55 = r22
            r5 = r80
            r13 = r82
            r2 = r0
            r49 = r7
            r44 = r13
            r1 = r26
            r10 = 0
        L_0x11ea:
            r25 = 0
        L_0x11ec:
            r40 = -5
            r59 = 0
        L_0x11f0:
            r7 = r4
            goto L_0x1285
        L_0x11f3:
            r0 = move-exception
            r32 = r84
            r4 = r5
            r11 = r14
            r55 = r22
            r5 = r80
            r13 = r82
            r2 = r0
            r7 = r4
            r44 = r13
            r1 = r26
            goto L_0x127c
        L_0x1206:
            r0 = move-exception
            r5 = r80
            r32 = r84
            r11 = r14
        L_0x120c:
            r13 = r82
        L_0x120e:
            r8 = r75
            r9 = r76
        L_0x1212:
            r2 = r0
            r44 = r13
        L_0x1215:
            r10 = r26
            goto L_0x138f
        L_0x1219:
            r0 = move-exception
            r5 = r80
            r32 = r84
            r11 = r14
            r55 = r22
            r13 = r82
            r2 = r0
            r44 = r13
            r1 = r26
            goto L_0x127b
        L_0x122a:
            r0 = move-exception
            r32 = r84
            r1 = r5
            r11 = r14
            r5 = r80
            r13 = r82
            goto L_0x1248
        L_0x1234:
            r0 = move-exception
            r32 = r84
            r1 = r5
            r11 = r14
            r55 = r22
            r5 = r80
            r13 = r82
            goto L_0x1278
        L_0x1240:
            r0 = move-exception
            r32 = r84
            r1 = r5
            r11 = r14
            r13 = r82
            r5 = r3
        L_0x1248:
            r8 = r75
            r9 = r76
            r2 = r0
            r10 = r1
            goto L_0x138d
        L_0x1250:
            r0 = move-exception
            r32 = r84
            r1 = r5
            r11 = r14
            r55 = r22
            r13 = r82
            r5 = r3
            goto L_0x1278
        L_0x125b:
            r0 = move-exception
            r5 = r80
            r11 = r14
            r13 = r82
            r8 = r75
            r9 = r76
            r10 = r78
            r32 = r84
            r2 = r0
            goto L_0x138d
        L_0x126c:
            r0 = move-exception
            r5 = r80
            r55 = r4
            r11 = r14
            r13 = r82
            r1 = r78
            r32 = r84
        L_0x1278:
            r2 = r0
            r44 = r13
        L_0x127b:
            r7 = 0
        L_0x127c:
            r10 = 0
            r25 = 0
            r40 = -5
            r49 = 0
        L_0x1283:
            r59 = 0
        L_0x1285:
            boolean r3 = r2 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x1300 }
            if (r3 == 0) goto L_0x128d
            if (r89 != 0) goto L_0x128d
            r9 = 1
            goto L_0x128e
        L_0x128d:
            r9 = 0
        L_0x128e:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x12f2 }
            r3.<init>()     // Catch:{ all -> 0x12f2 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x12f2 }
            r3.append(r1)     // Catch:{ all -> 0x12f2 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x12f2 }
            r3.append(r11)     // Catch:{ all -> 0x12f2 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x12f2 }
            r4 = r76
            r3.append(r4)     // Catch:{ all -> 0x12ee }
            java.lang.String r8 = "x"
            r3.append(r8)     // Catch:{ all -> 0x12ee }
            r8 = r75
            r3.append(r8)     // Catch:{ all -> 0x12ec }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x12ec }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x12ec }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x12ec }
            r26 = r1
            r3 = r10
            r2 = r40
            r10 = r59
            r1 = 1
            r40 = r9
        L_0x12cb:
            android.media.MediaExtractor r9 = r15.extractor     // Catch:{ all -> 0x12e4 }
            r12 = r55
            r9.unselectTrack(r12)     // Catch:{ all -> 0x12e4 }
            if (r3 == 0) goto L_0x12da
            r3.stop()     // Catch:{ all -> 0x12e4 }
            r3.release()     // Catch:{ all -> 0x12e4 }
        L_0x12da:
            r60 = r2
            r2 = r7
            r7 = r25
            r13 = r44
            r59 = r49
            goto L_0x1323
        L_0x12e4:
            r0 = move-exception
            r1 = r2
            r9 = r4
            r10 = r26
        L_0x12e9:
            r2 = r0
            goto L_0x1392
        L_0x12ec:
            r0 = move-exception
            goto L_0x12f7
        L_0x12ee:
            r0 = move-exception
            r8 = r75
            goto L_0x12f7
        L_0x12f2:
            r0 = move-exception
            r8 = r75
            r4 = r76
        L_0x12f7:
            r2 = r0
            r10 = r1
            r1 = r40
            r40 = r9
            r9 = r4
            goto L_0x1392
        L_0x1300:
            r0 = move-exception
            r8 = r75
            r4 = r76
            r2 = r0
            r10 = r1
            r9 = r4
        L_0x1308:
            r1 = r40
            goto L_0x1390
        L_0x130c:
            r8 = r75
            r4 = r76
            r5 = r80
            r11 = r14
            r13 = r82
            r26 = r78
            r32 = r84
            r1 = 0
            r2 = 0
            r7 = 0
            r10 = 0
            r40 = 0
            r59 = 0
            r60 = -5
        L_0x1323:
            if (r7 == 0) goto L_0x1334
            r7.release()     // Catch:{ all -> 0x1329 }
            goto L_0x1334
        L_0x1329:
            r0 = move-exception
            r2 = r0
            r9 = r4
            r44 = r13
            r10 = r26
            r1 = r60
            goto L_0x1392
        L_0x1334:
            if (r59 == 0) goto L_0x1339
            r59.release()     // Catch:{ all -> 0x1329 }
        L_0x1339:
            if (r2 == 0) goto L_0x1341
            r2.stop()     // Catch:{ all -> 0x1329 }
            r2.release()     // Catch:{ all -> 0x1329 }
        L_0x1341:
            if (r10 == 0) goto L_0x1346
            r10.release()     // Catch:{ all -> 0x1329 }
        L_0x1346:
            r68.checkConversionCanceled()     // Catch:{ all -> 0x1329 }
            r9 = r4
            r22 = r40
            r40 = r1
            r1 = r60
        L_0x1350:
            android.media.MediaExtractor r2 = r15.extractor
            if (r2 == 0) goto L_0x1357
            r2.release()
        L_0x1357:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer
            if (r2 == 0) goto L_0x136c
            r2.finishMovie()     // Catch:{ all -> 0x1367 }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ all -> 0x1367 }
            long r1 = r2.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x1367 }
            r15.endPresentationTime = r1     // Catch:{ all -> 0x1367 }
            goto L_0x136c
        L_0x1367:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x136c:
            r44 = r13
            r12 = r26
            r1 = r40
            goto L_0x13e1
        L_0x1374:
            r0 = move-exception
            r5 = r80
            r4 = r11
            r8 = r12
            r11 = r13
            r13 = r82
            goto L_0x1387
        L_0x137d:
            r0 = move-exception
            r5 = r80
            goto L_0x1382
        L_0x1381:
            r0 = move-exception
        L_0x1382:
            r13 = r82
            r4 = r11
            r8 = r12
            r11 = r9
        L_0x1387:
            r10 = r78
            r32 = r84
            r2 = r0
            r9 = r4
        L_0x138d:
            r44 = r13
        L_0x138f:
            r1 = -5
        L_0x1390:
            r40 = 0
        L_0x1392:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x1452 }
            r3.<init>()     // Catch:{ all -> 0x1452 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x1452 }
            r3.append(r10)     // Catch:{ all -> 0x1452 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x1452 }
            r3.append(r11)     // Catch:{ all -> 0x1452 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x1452 }
            r3.append(r9)     // Catch:{ all -> 0x1452 }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x1452 }
            r3.append(r8)     // Catch:{ all -> 0x1452 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x1452 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x1452 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x1452 }
            android.media.MediaExtractor r2 = r15.extractor
            if (r2 == 0) goto L_0x13c8
            r2.release()
        L_0x13c8:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer
            if (r2 == 0) goto L_0x13dd
            r2.finishMovie()     // Catch:{ all -> 0x13d8 }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ all -> 0x13d8 }
            long r1 = r2.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x13d8 }
            r15.endPresentationTime = r1     // Catch:{ all -> 0x13d8 }
            goto L_0x13dd
        L_0x13d8:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x13dd:
            r12 = r10
            r22 = r40
            r1 = 1
        L_0x13e1:
            if (r22 == 0) goto L_0x1411
            r22 = 1
            r1 = r68
            r2 = r69
            r3 = r70
            r4 = r71
            r5 = r72
            r6 = r73
            r7 = r74
            r10 = r77
            r11 = r12
            r12 = r79
            r13 = r80
            r15 = r44
            r17 = r32
            r19 = r86
            r21 = r88
            r23 = r90
            r24 = r91
            r25 = r92
            r26 = r93
            r27 = r94
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r15, r17, r19, r21, r22, r23, r24, r25, r26, r27)
            return r1
        L_0x1411:
            long r2 = java.lang.System.currentTimeMillis()
            long r2 = r2 - r16
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r4 == 0) goto L_0x1451
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "compression completed time="
            r4.append(r5)
            r4.append(r2)
            java.lang.String r2 = " needCompress="
            r4.append(r2)
            r2 = r88
            r4.append(r2)
            java.lang.String r2 = " w="
            r4.append(r2)
            r4.append(r8)
            java.lang.String r2 = " h="
            r4.append(r2)
            r4.append(r9)
            java.lang.String r2 = " bitrate="
            r4.append(r2)
            r4.append(r12)
            java.lang.String r2 = r4.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x1451:
            return r1
        L_0x1452:
            r0 = move-exception
            r2 = r68
            r3 = r0
            android.media.MediaExtractor r4 = r2.extractor
            if (r4 == 0) goto L_0x145d
            r4.release()
        L_0x145d:
            org.telegram.messenger.video.MP4Builder r4 = r2.mediaMuxer
            if (r4 == 0) goto L_0x1472
            r4.finishMovie()     // Catch:{ all -> 0x146d }
            org.telegram.messenger.video.MP4Builder r4 = r2.mediaMuxer     // Catch:{ all -> 0x146d }
            long r4 = r4.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x146d }
            r2.endPresentationTime = r4     // Catch:{ all -> 0x146d }
            goto L_0x1472
        L_0x146d:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1472:
            goto L_0x1474
        L_0x1473:
            throw r3
        L_0x1474:
            goto L_0x1473
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, int, int, int, long, long, long, long, boolean, boolean, org.telegram.messenger.MediaController$SavedFilterState, java.lang.String, java.util.ArrayList, boolean, org.telegram.messenger.MediaController$CropState):boolean");
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
            throw new ConversionCanceledException();
        }
    }

    private static String createFragmentShader(int i, int i2, int i3, int i4) {
        float f = (float) i;
        float max = Math.max(2.0f, f / ((float) i3));
        float f2 = (float) i2;
        float max2 = Math.max(2.0f, f2 / ((float) i4));
        int ceil = ((int) Math.ceil((double) (max - 0.1f))) / 2;
        int ceil2 = ((int) Math.ceil((double) (max2 - 0.1f))) / 2;
        int i5 = (ceil * 2) + 1;
        float f3 = (max / ((float) i5)) * (1.0f / f);
        int i6 = (ceil2 * 2) + 1;
        float f4 = (max2 / ((float) i6)) * (1.0f / f2);
        float f5 = (float) (i5 * i6);
        StringBuilder sb = new StringBuilder();
        for (int i7 = -ceil; i7 <= ceil; i7++) {
            for (int i8 = -ceil2; i8 <= ceil2; i8++) {
                if (i7 != 0 || i8 != 0) {
                    sb.append("      + texture2D(sTexture, vTextureCoord.xy + vec2(");
                    sb.append(((float) i7) * f3);
                    sb.append(", ");
                    sb.append(((float) i8) * f4);
                    sb.append("))\n");
                }
            }
        }
        return "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n    gl_FragColor = (texture2D(sTexture, vTextureCoord)\n" + sb.toString() + "    ) / " + f5 + ";\n}\n";
    }

    public class ConversionCanceledException extends RuntimeException {
        public ConversionCanceledException() {
            super("canceled conversion");
        }
    }
}
