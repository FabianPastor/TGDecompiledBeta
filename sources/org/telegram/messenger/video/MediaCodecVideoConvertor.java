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

    public boolean convertVideo(String str, File file, int i, boolean z, int i2, int i3, int i4, int i5, int i6, int i7, int i8, long j, long j2, long j3, boolean z2, long j4, MediaController.SavedFilterState savedFilterState, String str2, ArrayList<VideoEditedInfo.MediaEntity> arrayList, boolean z3, MediaController.CropState cropState, boolean z4, MediaController.VideoConvertorListener videoConvertorListener) {
        String str3 = str;
        long j5 = j4;
        this.callback = videoConvertorListener;
        return convertVideoInternal(str, file, i, z, i2, i3, i4, i5, i6, i7, i8, j, j2, j3, j5, z2, false, savedFilterState, str2, arrayList, z3, cropState, z4);
    }

    public long getLastFrameTimestamp() {
        return this.endPresentationTime;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v29, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v30, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v31, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v32, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v33, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v42, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v43, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v51, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v39, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v41, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v42, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v43, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v46, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v48, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v80, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r50v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v112, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v113, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v116, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v117, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v80, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v96, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v120, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v98, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v100, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v101, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v104, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v105, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v92, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v106, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v107, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v108, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v95, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v109, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v96, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v110, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v97, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v111, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v98, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v112, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v99, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v113, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v100, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v114, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v101, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v115, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v102, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v116, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v103, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v117, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v104, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v118, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v105, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v119, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v106, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v120, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v107, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v121, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v108, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v122, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v109, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v123, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v110, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v124, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v111, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v125, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v112, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v126, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v113, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v127, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v114, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v128, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v115, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v129, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v116, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v130, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v117, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v131, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v118, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v132, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v119, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v133, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v120, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v135, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v202, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v203, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v121, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v137, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v122, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v138, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v123, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v139, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v209, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v124, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v141, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v125, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v142, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v126, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v143, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v127, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v147, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v110, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v128, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v148, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v125, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v126, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v129, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v149, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v270, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v271, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v272, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v274, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v130, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v150, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v277, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v131, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v151, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v132, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v152, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r72v133, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v153, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v179, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v180, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v181, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v182, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v91, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v92, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v93, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v98, resolved type: int} */
    /* JADX WARNING: type inference failed for: r1v235 */
    /* JADX WARNING: type inference failed for: r1v236 */
    /* JADX WARNING: Code restructure failed: missing block: B:1004:0x1132, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1081:0x1229, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1085:0x1248, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1086:0x124a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1087:0x124b, code lost:
        r13 = r93;
        r1 = r0;
        r54 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1108:0x12f5, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1115:0x1302, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1116:0x1303, code lost:
        r10 = r87;
        r44 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1120:0x1311, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1121:0x1312, code lost:
        r10 = r87;
        r5 = r88;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1131:0x134e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1132:0x134f, code lost:
        r10 = r87;
        r5 = r88;
        r69 = r9;
        r23 = r14;
        r4 = r54;
        r71 = r55;
        r14 = r8;
        r72 = r92;
        r44 = r94;
        r1 = r0;
        r3 = r21;
        r13 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1133:0x1368, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1134:0x1369, code lost:
        r10 = r87;
        r5 = r88;
        r23 = r14;
        r4 = r54;
        r71 = r55;
        r14 = r8;
        r72 = r92;
        r44 = r94;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1138:0x1395, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1139:0x1396, code lost:
        r10 = r87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1142:0x13af, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1143:0x13b0, code lost:
        r10 = r9;
        r94 = r14;
        r71 = r30;
        r15 = r78;
        r14 = r2;
        r72 = r92;
        r44 = r94;
        r1 = r0;
        r54 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1149:0x13e6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1150:0x13e7, code lost:
        r10 = r9;
        r71 = r30;
        r15 = r78;
        r72 = r92;
        r44 = r14;
        r1 = r0;
        r14 = r5;
        r3 = r21;
        r8 = null;
        r13 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1151:0x13fc, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1152:0x13fd, code lost:
        r10 = r9;
        r94 = r14;
        r15 = r78;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1153:0x1404, code lost:
        r2 = r85;
        r3 = r86;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1156:0x1411, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1157:0x1412, code lost:
        r10 = r9;
        r71 = r30;
        r15 = r78;
        r72 = r92;
        r44 = r14;
        r1 = r0;
        r3 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1179:0x1482, code lost:
        r47 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1242:0x15b5, code lost:
        r1.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1246:?, code lost:
        r1.finishMovie();
        r15.endPresentationTime = r15.mediaMuxer.getLastFrameTimestamp(r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1247:0x15c8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1248:0x15c9, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1300:0x0var_, code lost:
        r14 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x0489, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x048a, code lost:
        r1 = r0;
        r37 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x048e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x04b6, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x04b7, code lost:
        r9 = r37;
        r3 = r38;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x04dc, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x04dd, code lost:
        r3 = r38;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x050a, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x0569, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x056a, code lost:
        r3 = r38;
        r15 = r78;
        r10 = r87;
        r72 = r92;
        r44 = r94;
        r1 = r0;
        r7 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x0578, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x0579, code lost:
        r13 = r78;
        r1 = r37;
        r3 = r38;
        r14 = r15;
        r11 = r39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x0583, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x0584, code lost:
        r13 = r78;
        r36 = r7;
        r3 = r10;
        r39 = r11;
        r37 = r9;
        r14 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x058f, code lost:
        r8 = -5;
        r18 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x05a2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x05a3, code lost:
        r36 = r7;
        r3 = r10;
        r39 = r11;
        r15 = r78;
        r10 = r87;
        r72 = r92;
        r44 = r94;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x05b5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x05b6, code lost:
        r13 = r78;
        r36 = r7;
        r3 = r10;
        r39 = r11;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x0808, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x0809, code lost:
        r2 = r85;
        r72 = r92;
        r1 = r0;
        r7 = r3;
        r10 = r9;
        r44 = r14;
        r6 = false;
        r3 = r86;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x0847, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x0848, code lost:
        r72 = r92;
        r1 = r0;
        r10 = r9;
        r44 = r14;
        r71 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x09b1, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x09b3, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x0a16, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x0a17, code lost:
        r10 = r87;
        r72 = r92;
        r44 = r94;
        r1 = r0;
        r69 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x0a51, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x0a7f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r11 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x0ab3, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0b0f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x0b10, code lost:
        r11 = r92;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x0b1d, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0bac, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0bad, code lost:
        r3 = r86;
        r10 = r87;
        r44 = r92;
        r1 = r0;
        r72 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0bb8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0bb9, code lost:
        r10 = r87;
        r44 = r92;
        r1 = r0;
        r69 = r9;
        r72 = r11;
        r23 = r14;
        r3 = r21;
        r13 = r30;
        r71 = r55;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0c6d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0c6f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0CLASSNAME, code lost:
        r71 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0cbe, code lost:
        if (r9.presentationTimeUs < r11) goto L_0x0cc0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x0d11, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0d12, code lost:
        r72 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x0d17, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x0d18, code lost:
        r71 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x01fb, code lost:
        r6 = r7;
        r13 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x0ed6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x0ed7, code lost:
        r10 = r87;
        r1 = r0;
        r14 = r5;
        r13 = r11;
        r3 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x0var_, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x1073, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x107c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x107d, code lost:
        r10 = r87;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:436:0x07f6, B:447:0x083d] */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1004:0x1132 A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:979:0x10c8] */
    /* JADX WARNING: Removed duplicated region for block: B:1013:0x1156  */
    /* JADX WARNING: Removed duplicated region for block: B:1022:0x116b A[Catch:{ Exception -> 0x1168, all -> 0x1248 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1025:0x1173 A[Catch:{ Exception -> 0x1168, all -> 0x1248 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1026:0x1177 A[Catch:{ Exception -> 0x1168, all -> 0x1248 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1031:0x1182 A[Catch:{ Exception -> 0x1168, all -> 0x1248 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1042:0x11bc A[Catch:{ Exception -> 0x1168, all -> 0x1248 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1044:0x11bf A[Catch:{ Exception -> 0x1168, all -> 0x1248 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1045:0x11c2 A[Catch:{ Exception -> 0x1168, all -> 0x1248 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1053:0x11d7 A[Catch:{ Exception -> 0x1229, all -> 0x1248 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1073:0x120a A[Catch:{ Exception -> 0x124a, all -> 0x1248 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1076:0x1216 A[Catch:{ Exception -> 0x124a, all -> 0x1248 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1080:0x1225 A[Catch:{ Exception -> 0x124a, all -> 0x1248 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1085:0x1248 A[ExcHandler: all (th java.lang.Throwable), PHI: r93 
      PHI: (r93v9 int) = (r93v10 int), (r93v10 int), (r93v10 int), (r93v10 int), (r93v10 int), (r93v10 int), (r93v13 int) binds: [B:1059:0x11e6, B:1069:0x11fe, B:1064:0x11ed, B:1060:?, B:1056:0x11df, B:1050:0x11d2, B:1014:0x1158] A[DONT_GENERATE, DONT_INLINE], Splitter:B:1050:0x11d2] */
    /* JADX WARNING: Removed duplicated region for block: B:1108:0x12f5 A[ExcHandler: all (th java.lang.Throwable), PHI: r10 r11 r44 
      PHI: (r10v63 int) = (r10v82 int), (r10v82 int), (r10v82 int), (r10v82 int), (r10v87 int) binds: [B:991:0x10eb, B:992:?, B:985:0x10d8, B:986:?, B:1103:0x12b9] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r11v15 int) = (r11v20 int), (r11v20 int), (r11v20 int), (r11v20 int), (r11v11 int) binds: [B:991:0x10eb, B:992:?, B:985:0x10d8, B:986:?, B:1103:0x12b9] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r44v44 long) = (r44v49 long), (r44v49 long), (r44v49 long), (r44v49 long), (r44v59 long) binds: [B:991:0x10eb, B:992:?, B:985:0x10d8, B:986:?, B:1103:0x12b9] A[DONT_GENERATE, DONT_INLINE], Splitter:B:985:0x10d8] */
    /* JADX WARNING: Removed duplicated region for block: B:1115:0x1302 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:779:0x0dc3] */
    /* JADX WARNING: Removed duplicated region for block: B:1138:0x1395 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:541:0x09f7] */
    /* JADX WARNING: Removed duplicated region for block: B:1151:0x13fc A[ExcHandler: all (th java.lang.Throwable), Splitter:B:500:0x091f] */
    /* JADX WARNING: Removed duplicated region for block: B:1178:0x1480 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:1193:0x14cc A[Catch:{ all -> 0x14dc }] */
    /* JADX WARNING: Removed duplicated region for block: B:1206:0x1502  */
    /* JADX WARNING: Removed duplicated region for block: B:1208:0x151d A[SYNTHETIC, Splitter:B:1208:0x151d] */
    /* JADX WARNING: Removed duplicated region for block: B:1214:0x152d A[Catch:{ all -> 0x1521 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1216:0x1532 A[Catch:{ all -> 0x1521 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1218:0x153a A[Catch:{ all -> 0x1521 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1223:0x1548  */
    /* JADX WARNING: Removed duplicated region for block: B:1226:0x154f A[SYNTHETIC, Splitter:B:1226:0x154f] */
    /* JADX WARNING: Removed duplicated region for block: B:1242:0x15b5  */
    /* JADX WARNING: Removed duplicated region for block: B:1245:0x15bc A[SYNTHETIC, Splitter:B:1245:0x15bc] */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x15d3  */
    /* JADX WARNING: Removed duplicated region for block: B:1253:0x1602  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x043a A[Catch:{ Exception -> 0x04b2, all -> 0x04ae }] */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x043c A[Catch:{ Exception -> 0x04b2, all -> 0x04ae }] */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x044c  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x045b  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x04dc A[Catch:{ Exception -> 0x050c, all -> 0x050a }, ExcHandler: all (th java.lang.Throwable), Splitter:B:127:0x02c9] */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x050a A[ExcHandler: all (th java.lang.Throwable), PHI: r3 r8 r13 r39 
      PHI: (r3v220 int) = (r3v226 int), (r3v226 int), (r3v226 int), (r3v216 int), (r3v235 int) binds: [B:232:0x045d, B:233:?, B:235:0x046f, B:127:0x02c9, B:257:0x04c0] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r8v75 int) = (r8v81 int), (r8v81 int), (r8v81 int), (r8v74 int), (r8v74 int) binds: [B:232:0x045d, B:233:?, B:235:0x046f, B:127:0x02c9, B:257:0x04c0] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r13v123 org.telegram.messenger.video.MediaCodecVideoConvertor) = (r13v128 org.telegram.messenger.video.MediaCodecVideoConvertor), (r13v128 org.telegram.messenger.video.MediaCodecVideoConvertor), (r13v128 org.telegram.messenger.video.MediaCodecVideoConvertor), (r13v135 org.telegram.messenger.video.MediaCodecVideoConvertor), (r13v135 org.telegram.messenger.video.MediaCodecVideoConvertor) binds: [B:232:0x045d, B:233:?, B:235:0x046f, B:127:0x02c9, B:257:0x04c0] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r39v21 int) = (r39v27 int), (r39v27 int), (r39v27 int), (r39v20 int), (r39v20 int) binds: [B:232:0x045d, B:233:?, B:235:0x046f, B:127:0x02c9, B:257:0x04c0] A[DONT_GENERATE, DONT_INLINE], Splitter:B:232:0x045d] */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x0569 A[ExcHandler: all (r0v163 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:58:0x01b4] */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x05a2 A[ExcHandler: all (r0v157 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:45:0x0125] */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x05fc A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x0643 A[SYNTHETIC, Splitter:B:331:0x0643] */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x0658 A[Catch:{ all -> 0x0647 }] */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x065d A[Catch:{ all -> 0x0647 }] */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x0744  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0808 A[Catch:{ Exception -> 0x0817, all -> 0x0808 }, ExcHandler: all (r0v136 'th' java.lang.Throwable A[CUSTOM_DECLARE, Catch:{ Exception -> 0x0817, all -> 0x0808 }]), Splitter:B:436:0x07f6] */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x0861  */
    /* JADX WARNING: Removed duplicated region for block: B:473:0x08a1  */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x08ab A[SYNTHETIC, Splitter:B:477:0x08ab] */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x08e9 A[SYNTHETIC, Splitter:B:483:0x08e9] */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x091b  */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x0989  */
    /* JADX WARNING: Removed duplicated region for block: B:524:0x09b1 A[ExcHandler: all (th java.lang.Throwable), PHI: r1 r2 
      PHI: (r1v149 int) = (r1v54 int), (r1v54 int), (r1v54 int), (r1v54 int), (r1v54 int), (r1v150 int), (r1v150 int) binds: [B:575:0x0a7a, B:586:0x0a97, B:587:?, B:559:0x0a42, B:548:0x0a0d, B:522:0x09a4, B:523:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r2v96 int) = (r2v30 int), (r2v30 int), (r2v30 int), (r2v30 int), (r2v30 int), (r2v104 int), (r2v104 int) binds: [B:575:0x0a7a, B:586:0x0a97, B:587:?, B:559:0x0a42, B:548:0x0a0d, B:522:0x09a4, B:523:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:522:0x09a4] */
    /* JADX WARNING: Removed duplicated region for block: B:539:0x09ee  */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0a0d A[SYNTHETIC, Splitter:B:548:0x0a0d] */
    /* JADX WARNING: Removed duplicated region for block: B:553:0x0a28  */
    /* JADX WARNING: Removed duplicated region for block: B:556:0x0a2e A[SYNTHETIC, Splitter:B:556:0x0a2e] */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x0a51 A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:559:0x0a42] */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x0a68  */
    /* JADX WARNING: Removed duplicated region for block: B:571:0x0a6b  */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b1d A[ExcHandler: all (th java.lang.Throwable), Splitter:B:599:0x0ac6] */
    /* JADX WARNING: Removed duplicated region for block: B:625:0x0b21  */
    /* JADX WARNING: Removed duplicated region for block: B:634:0x0b4e  */
    /* JADX WARNING: Removed duplicated region for block: B:637:0x0b5c  */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0b5e  */
    /* JADX WARNING: Removed duplicated region for block: B:643:0x0b7f A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:649:0x0b9f A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0bac A[ExcHandler: all (r0v97 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:651:0x0ba3] */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0bd0 A[SYNTHETIC, Splitter:B:660:0x0bd0] */
    /* JADX WARNING: Removed duplicated region for block: B:748:0x0d11 A[Catch:{ Exception -> 0x0d61, all -> 0x0d5f }, ExcHandler: all (th java.lang.Throwable), Splitter:B:686:0x0c4f] */
    /* JADX WARNING: Removed duplicated region for block: B:758:0x0d44 A[Catch:{ Exception -> 0x0d61, all -> 0x0d5f }] */
    /* JADX WARNING: Removed duplicated region for block: B:774:0x0d84  */
    /* JADX WARNING: Removed duplicated region for block: B:777:0x0da5 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:782:0x0dc8  */
    /* JADX WARNING: Removed duplicated region for block: B:788:0x0dd9  */
    /* JADX WARNING: Removed duplicated region for block: B:790:0x0df1  */
    /* JADX WARNING: Removed duplicated region for block: B:929:0x0ffa A[Catch:{ Exception -> 0x12a9, all -> 0x12a4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:930:0x0ffc A[Catch:{ Exception -> 0x12a9, all -> 0x12a4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:934:0x1009  */
    /* JADX WARNING: Removed duplicated region for block: B:935:0x1028  */
    /* JADX WARNING: Removed duplicated region for block: B:950:0x1073 A[ExcHandler: all (th java.lang.Throwable), PHI: r11 r44 
      PHI: (r11v19 int) = (r11v20 int), (r11v20 int), (r11v20 int), (r11v11 int), (r11v11 int), (r11v11 int) binds: [B:967:0x109b, B:968:?, B:946:0x1055, B:882:0x0var_, B:888:0x0var_, B:855:0x0ecd] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r44v48 long) = (r44v49 long), (r44v49 long), (r44v49 long), (r44v68 long), (r44v68 long), (r44v70 long) binds: [B:967:0x109b, B:968:?, B:946:0x1055, B:882:0x0var_, B:888:0x0var_, B:855:0x0ecd] A[DONT_GENERATE, DONT_INLINE], Splitter:B:855:0x0ecd] */
    /* JADX WARNING: Removed duplicated region for block: B:952:0x107c A[ExcHandler: Exception (e java.lang.Exception), PHI: r11 r14 r23 r44 
      PHI: (r11v17 int) = (r11v20 int), (r11v20 int), (r11v20 int), (r11v20 int), (r11v11 int), (r11v11 int) binds: [B:974:0x10b5, B:967:0x109b, B:968:?, B:946:0x1055, B:882:0x0var_, B:888:0x0var_] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r14v32 android.media.MediaCodec) = (r14v33 android.media.MediaCodec), (r14v33 android.media.MediaCodec), (r14v33 android.media.MediaCodec), (r14v33 android.media.MediaCodec), (r14v48 android.media.MediaCodec), (r14v91 android.media.MediaCodec) binds: [B:974:0x10b5, B:967:0x109b, B:968:?, B:946:0x1055, B:888:0x0var_, B:882:0x0var_] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r23v28 org.telegram.messenger.video.OutputSurface) = (r23v29 org.telegram.messenger.video.OutputSurface), (r23v29 org.telegram.messenger.video.OutputSurface), (r23v29 org.telegram.messenger.video.OutputSurface), (r23v29 org.telegram.messenger.video.OutputSurface), (r23v35 org.telegram.messenger.video.OutputSurface), (r23v35 org.telegram.messenger.video.OutputSurface) binds: [B:974:0x10b5, B:967:0x109b, B:968:?, B:946:0x1055, B:882:0x0var_, B:888:0x0var_] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r44v46 long) = (r44v49 long), (r44v49 long), (r44v49 long), (r44v49 long), (r44v68 long), (r44v68 long) binds: [B:974:0x10b5, B:967:0x109b, B:968:?, B:946:0x1055, B:882:0x0var_, B:888:0x0var_] A[DONT_GENERATE, DONT_INLINE], Splitter:B:888:0x0var_] */
    @android.annotation.TargetApi(18)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideoInternal(java.lang.String r79, java.io.File r80, int r81, boolean r82, int r83, int r84, int r85, int r86, int r87, int r88, int r89, long r90, long r92, long r94, long r96, boolean r98, boolean r99, org.telegram.messenger.MediaController.SavedFilterState r100, java.lang.String r101, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r102, boolean r103, org.telegram.messenger.MediaController.CropState r104, boolean r105) {
        /*
            r78 = this;
            r15 = r78
            r14 = r79
            r13 = r81
            r12 = r83
            r11 = r84
            r9 = r85
            r10 = r86
            r8 = r87
            r7 = r88
            r6 = r89
            r4 = r90
            r2 = r96
            r1 = r98
            long r16 = java.lang.System.currentTimeMillis()
            r18 = -1
            android.media.MediaCodec$BufferInfo r13 = new android.media.MediaCodec$BufferInfo     // Catch:{ all -> 0x1570 }
            r13.<init>()     // Catch:{ all -> 0x1570 }
            org.telegram.messenger.video.Mp4Movie r6 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ all -> 0x1570 }
            r6.<init>()     // Catch:{ all -> 0x1570 }
            r23 = r13
            r13 = r80
            r6.setCacheFile(r13)     // Catch:{ all -> 0x1570 }
            r11 = 0
            r6.setRotation(r11)     // Catch:{ all -> 0x1570 }
            r6.setSize(r9, r10)     // Catch:{ all -> 0x1570 }
            org.telegram.messenger.video.MP4Builder r11 = new org.telegram.messenger.video.MP4Builder     // Catch:{ all -> 0x1570 }
            r11.<init>()     // Catch:{ all -> 0x1570 }
            r13 = r82
            org.telegram.messenger.video.MP4Builder r6 = r11.createMovie(r6, r13)     // Catch:{ all -> 0x1570 }
            r15.mediaMuxer = r6     // Catch:{ all -> 0x1570 }
            float r6 = (float) r2     // Catch:{ all -> 0x1570 }
            r24 = 1148846080(0x447a0000, float:1000.0)
            float r25 = r6 / r24
            r26 = 1000(0x3e8, double:4.94E-321)
            long r4 = r2 * r26
            r15.endPresentationTime = r4     // Catch:{ all -> 0x1570 }
            r78.checkConversionCanceled()     // Catch:{ all -> 0x1570 }
            java.lang.String r6 = "csd-0"
            java.lang.String r5 = "prepend-sps-pps-to-idr-frames"
            java.lang.String r13 = "video/avc"
            r14 = 0
            if (r103 == 0) goto L_0x06a5
            int r18 = (r94 > r14 ? 1 : (r94 == r14 ? 0 : -1))
            if (r18 < 0) goto L_0x0083
            r4 = 1157234688(0x44fa0000, float:2000.0)
            int r4 = (r25 > r4 ? 1 : (r25 == r4 ? 0 : -1))
            if (r4 > 0) goto L_0x006e
            r4 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            r7 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x0088
        L_0x006e:
            r4 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r4 = (r25 > r4 ? 1 : (r25 == r4 ? 0 : -1))
            if (r4 > 0) goto L_0x007c
            r4 = 2200000(0x2191c0, float:3.082857E-39)
            r7 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x0088
        L_0x007c:
            r4 = 1560000(0x17cdc0, float:2.186026E-39)
            r7 = 1560000(0x17cdc0, float:2.186026E-39)
            goto L_0x0088
        L_0x0083:
            if (r7 > 0) goto L_0x0088
            r7 = 921600(0xe1000, float:1.291437E-39)
        L_0x0088:
            int r4 = r9 % 16
            r18 = 1098907648(0x41800000, float:16.0)
            if (r4 == 0) goto L_0x00d9
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00d1, all -> 0x00c2 }
            if (r4 == 0) goto L_0x00b7
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d1, all -> 0x00c2 }
            r4.<init>()     // Catch:{ Exception -> 0x00d1, all -> 0x00c2 }
            java.lang.String r11 = "changing width from "
            r4.append(r11)     // Catch:{ Exception -> 0x00d1, all -> 0x00c2 }
            r4.append(r9)     // Catch:{ Exception -> 0x00d1, all -> 0x00c2 }
            java.lang.String r11 = " to "
            r4.append(r11)     // Catch:{ Exception -> 0x00d1, all -> 0x00c2 }
            float r11 = (float) r9     // Catch:{ Exception -> 0x00d1, all -> 0x00c2 }
            float r11 = r11 / r18
            int r11 = java.lang.Math.round(r11)     // Catch:{ Exception -> 0x00d1, all -> 0x00c2 }
            int r11 = r11 * 16
            r4.append(r11)     // Catch:{ Exception -> 0x00d1, all -> 0x00c2 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x00d1, all -> 0x00c2 }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x00d1, all -> 0x00c2 }
        L_0x00b7:
            float r4 = (float) r9     // Catch:{ Exception -> 0x00d1, all -> 0x00c2 }
            float r4 = r4 / r18
            int r4 = java.lang.Math.round(r4)     // Catch:{ Exception -> 0x00d1, all -> 0x00c2 }
            int r4 = r4 * 16
            r11 = r4
            goto L_0x00da
        L_0x00c2:
            r0 = move-exception
            r15 = r78
            r72 = r92
            r44 = r94
            r1 = r0
            r2 = r9
            r3 = r10
        L_0x00cc:
            r6 = 0
            r13 = -5
            r10 = r8
            goto L_0x1581
        L_0x00d1:
            r0 = move-exception
            r13 = r78
            r1 = r0
            r36 = r7
            goto L_0x05f0
        L_0x00d9:
            r11 = r9
        L_0x00da:
            int r4 = r10 % 16
            if (r4 == 0) goto L_0x0125
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            if (r4 == 0) goto L_0x0107
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            r4.<init>()     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            java.lang.String r9 = "changing height from "
            r4.append(r9)     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            r4.append(r10)     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            java.lang.String r9 = " to "
            r4.append(r9)     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            float r9 = (float) r10     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            float r9 = r9 / r18
            int r9 = java.lang.Math.round(r9)     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            int r9 = r9 * 16
            r4.append(r9)     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
        L_0x0107:
            float r4 = (float) r10     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            float r4 = r4 / r18
            int r4 = java.lang.Math.round(r4)     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            int r4 = r4 * 16
            r10 = r4
            goto L_0x0125
        L_0x0112:
            r0 = move-exception
            r15 = r78
            r72 = r92
            r44 = r94
            r1 = r0
            r3 = r10
            r2 = r11
            goto L_0x00cc
        L_0x011d:
            r0 = move-exception
            r13 = r78
            r1 = r0
            r36 = r7
            goto L_0x05f1
        L_0x0125:
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05b5, all -> 0x05a2 }
            if (r4 == 0) goto L_0x014d
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            r4.<init>()     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            java.lang.String r9 = "create photo encoder "
            r4.append(r9)     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            r4.append(r11)     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            java.lang.String r9 = " "
            r4.append(r9)     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            r4.append(r10)     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            java.lang.String r9 = " duration = "
            r4.append(r9)     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            r4.append(r2)     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x011d, all -> 0x0112 }
        L_0x014d:
            android.media.MediaFormat r4 = android.media.MediaFormat.createVideoFormat(r13, r11, r10)     // Catch:{ Exception -> 0x05b5, all -> 0x05a2 }
            java.lang.String r9 = "color-format"
            r14 = 2130708361(0x7var_, float:1.701803E38)
            r4.setInteger(r9, r14)     // Catch:{ Exception -> 0x05b5, all -> 0x05a2 }
            java.lang.String r9 = "bitrate"
            r4.setInteger(r9, r7)     // Catch:{ Exception -> 0x05b5, all -> 0x05a2 }
            java.lang.String r9 = "frame-rate"
            r14 = 30
            r4.setInteger(r9, r14)     // Catch:{ Exception -> 0x05b5, all -> 0x05a2 }
            java.lang.String r9 = "i-frame-interval"
            r14 = 1
            r4.setInteger(r9, r14)     // Catch:{ Exception -> 0x05b5, all -> 0x05a2 }
            android.media.MediaCodec r15 = android.media.MediaCodec.createEncoderByType(r13)     // Catch:{ Exception -> 0x05b5, all -> 0x05a2 }
            r9 = 0
            r15.configure(r4, r9, r9, r14)     // Catch:{ Exception -> 0x0595, all -> 0x05a2 }
            org.telegram.messenger.video.InputSurface r9 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x0595, all -> 0x05a2 }
            android.view.Surface r4 = r15.createInputSurface()     // Catch:{ Exception -> 0x0595, all -> 0x05a2 }
            r9.<init>(r4)     // Catch:{ Exception -> 0x0595, all -> 0x05a2 }
            r9.makeCurrent()     // Catch:{ Exception -> 0x0583, all -> 0x05a2 }
            r15.start()     // Catch:{ Exception -> 0x0583, all -> 0x05a2 }
            org.telegram.messenger.video.OutputSurface r18 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x0583, all -> 0x05a2 }
            r19 = 0
            float r4 = (float) r8
            r21 = 1
            r1 = r18
            r2 = r100
            r3 = r79
            r33 = r4
            r4 = r101
            r14 = r5
            r5 = r102
            r35 = r6
            r6 = r19
            r36 = r7
            r7 = r11
            r8 = r10
            r37 = r9
            r9 = r83
            r38 = r10
            r10 = r84
            r39 = r11
            r11 = r81
            r12 = r33
            r44 = r13
            r20 = r14
            r14 = r23
            r13 = r21
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x0578, all -> 0x0569 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x055d, all -> 0x0569 }
            r2 = 21
            if (r1 >= r2) goto L_0x01de
            java.nio.ByteBuffer[] r1 = r15.getOutputBuffers()     // Catch:{ Exception -> 0x01d2, all -> 0x01c2 }
            goto L_0x01df
        L_0x01c2:
            r0 = move-exception
            r15 = r78
            r10 = r87
            r72 = r92
            r44 = r94
            r1 = r0
            r7 = r36
            r3 = r38
            goto L_0x05b1
        L_0x01d2:
            r0 = move-exception
            r13 = r78
            r1 = r0
            r14 = r15
            r3 = r38
            r11 = r39
            r8 = -5
            goto L_0x05f8
        L_0x01de:
            r1 = 0
        L_0x01df:
            r78.checkConversionCanceled()     // Catch:{ Exception -> 0x055d, all -> 0x0569 }
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 1
            r6 = 0
            r13 = -5
        L_0x01e8:
            if (r6 != 0) goto L_0x054a
            r78.checkConversionCanceled()     // Catch:{ Exception -> 0x053b, all -> 0x0527 }
            r7 = r2 ^ 1
            r8 = r13
            r13 = 1
            r76 = r7
            r7 = r6
            r6 = r76
        L_0x01f6:
            if (r6 != 0) goto L_0x01fe
            if (r13 == 0) goto L_0x01fb
            goto L_0x01fe
        L_0x01fb:
            r6 = r7
            r13 = r8
            goto L_0x01e8
        L_0x01fe:
            r78.checkConversionCanceled()     // Catch:{ Exception -> 0x051e, all -> 0x0510 }
            if (r99 == 0) goto L_0x0206
            r9 = 22000(0x55f0, double:1.08694E-319)
            goto L_0x0208
        L_0x0206:
            r9 = 2500(0x9c4, double:1.235E-320)
        L_0x0208:
            int r9 = r15.dequeueOutputBuffer(r14, r9)     // Catch:{ Exception -> 0x051e, all -> 0x0510 }
            r10 = -1
            if (r9 != r10) goto L_0x0221
            r10 = 0
            r13 = r78
            r19 = r3
            r88 = r6
            r12 = r35
        L_0x0218:
            r3 = r38
            r11 = r44
        L_0x021c:
            r6 = r5
            r5 = r1
            r1 = -1
            goto L_0x044a
        L_0x0221:
            r10 = -3
            if (r9 != r10) goto L_0x0258
            int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x024d, all -> 0x023c }
            r11 = 21
            if (r10 >= r11) goto L_0x022e
            java.nio.ByteBuffer[] r1 = r15.getOutputBuffers()     // Catch:{ Exception -> 0x024d, all -> 0x023c }
        L_0x022e:
            r19 = r3
            r88 = r6
            r10 = r13
            r12 = r35
            r3 = r38
            r11 = r44
            r13 = r78
            goto L_0x021c
        L_0x023c:
            r0 = move-exception
            r15 = r78
            r10 = r87
            r72 = r92
            r44 = r94
            r1 = r0
            r13 = r8
            r7 = r36
            r3 = r38
            goto L_0x0537
        L_0x024d:
            r0 = move-exception
            r13 = r78
        L_0x0250:
            r1 = r0
            r14 = r15
            r3 = r38
        L_0x0254:
            r11 = r39
            goto L_0x05f8
        L_0x0258:
            r10 = -2
            if (r9 != r10) goto L_0x02c1
            android.media.MediaFormat r10 = r15.getOutputFormat()     // Catch:{ Exception -> 0x024d, all -> 0x023c }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x024d, all -> 0x023c }
            if (r11 == 0) goto L_0x0277
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x024d, all -> 0x023c }
            r11.<init>()     // Catch:{ Exception -> 0x024d, all -> 0x023c }
            java.lang.String r12 = "photo encoder new format "
            r11.append(r12)     // Catch:{ Exception -> 0x024d, all -> 0x023c }
            r11.append(r10)     // Catch:{ Exception -> 0x024d, all -> 0x023c }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x024d, all -> 0x023c }
            org.telegram.messenger.FileLog.d(r11)     // Catch:{ Exception -> 0x024d, all -> 0x023c }
        L_0x0277:
            r11 = -5
            if (r8 != r11) goto L_0x02b3
            if (r10 == 0) goto L_0x02b3
            r85 = r13
            r13 = r78
            org.telegram.messenger.video.MP4Builder r11 = r13.mediaMuxer     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            r12 = 0
            int r8 = r11.addTrack(r10, r12)     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            r11 = r20
            boolean r19 = r10.containsKey(r11)     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            if (r19 == 0) goto L_0x02ae
            int r12 = r10.getInteger(r11)     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            r20 = r11
            r11 = 1
            if (r12 != r11) goto L_0x02b0
            r12 = r35
            java.nio.ByteBuffer r3 = r10.getByteBuffer(r12)     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            java.lang.String r11 = "csd-1"
            java.nio.ByteBuffer r10 = r10.getByteBuffer(r11)     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            int r3 = r3.limit()     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            int r10 = r10.limit()     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            int r3 = r3 + r10
            goto L_0x02b9
        L_0x02ae:
            r20 = r11
        L_0x02b0:
            r12 = r35
            goto L_0x02b9
        L_0x02b3:
            r85 = r13
            r12 = r35
            r13 = r78
        L_0x02b9:
            r10 = r85
            r19 = r3
            r88 = r6
            goto L_0x0218
        L_0x02c1:
            r85 = r13
            r12 = r35
            r13 = r78
            if (r9 < 0) goto L_0x04ef
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x04ed, all -> 0x04dc }
            r10 = 21
            if (r7 >= r10) goto L_0x02e4
            r7 = r1[r9]     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            goto L_0x02e8
        L_0x02d2:
            r0 = move-exception
            r10 = r87
            r72 = r92
            r44 = r94
            r1 = r0
            r15 = r13
            r7 = r36
            r3 = r38
            goto L_0x04e9
        L_0x02e1:
            r0 = move-exception
            goto L_0x0250
        L_0x02e4:
            java.nio.ByteBuffer r7 = r15.getOutputBuffer(r9)     // Catch:{ Exception -> 0x04ed, all -> 0x04dc }
        L_0x02e8:
            if (r7 == 0) goto L_0x04bc
            int r10 = r14.size     // Catch:{ Exception -> 0x04b6, all -> 0x04dc }
            r11 = 1
            if (r10 <= r11) goto L_0x0428
            int r11 = r14.flags     // Catch:{ Exception -> 0x041f, all -> 0x040c }
            r19 = r11 & 2
            if (r19 != 0) goto L_0x0384
            if (r3 == 0) goto L_0x0306
            r19 = r11 & 1
            if (r19 == 0) goto L_0x0306
            r86 = r1
            int r1 = r14.offset     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            int r1 = r1 + r3
            r14.offset = r1     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            int r10 = r10 - r3
            r14.size = r10     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            goto L_0x0308
        L_0x0306:
            r86 = r1
        L_0x0308:
            if (r5 == 0) goto L_0x0356
            r1 = r11 & 1
            if (r1 == 0) goto L_0x0356
            int r1 = r14.size     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            r11 = 100
            if (r1 <= r11) goto L_0x0355
            int r1 = r14.offset     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            r7.position(r1)     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            byte[] r1 = new byte[r11]     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            r7.get(r1)     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            r5 = 0
            r10 = 0
        L_0x0320:
            r11 = 96
            if (r5 >= r11) goto L_0x0355
            byte r11 = r1[r5]     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            if (r11 != 0) goto L_0x034c
            int r11 = r5 + 1
            byte r11 = r1[r11]     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            if (r11 != 0) goto L_0x034c
            int r11 = r5 + 2
            byte r11 = r1[r11]     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            if (r11 != 0) goto L_0x034c
            int r11 = r5 + 3
            byte r11 = r1[r11]     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            r19 = r1
            r1 = 1
            if (r11 != r1) goto L_0x034e
            int r10 = r10 + 1
            if (r10 <= r1) goto L_0x034e
            int r1 = r14.offset     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            int r1 = r1 + r5
            r14.offset = r1     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            int r1 = r14.size     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            int r1 = r1 - r5
            r14.size = r1     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            goto L_0x0355
        L_0x034c:
            r19 = r1
        L_0x034e:
            int r5 = r5 + 1
            r1 = r19
            r11 = 100
            goto L_0x0320
        L_0x0355:
            r5 = 0
        L_0x0356:
            org.telegram.messenger.video.MP4Builder r1 = r13.mediaMuxer     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            r11 = r5
            r88 = r6
            r10 = 1
            long r5 = r1.writeSampleData(r8, r7, r14, r10)     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            r1 = r11
            r10 = 0
            int r7 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r7 == 0) goto L_0x0376
            org.telegram.messenger.MediaController$VideoConvertorListener r7 = r13.callback     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            if (r7 == 0) goto L_0x0376
            r19 = r1
            float r1 = (float) r10     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            float r1 = r1 / r24
            float r1 = r1 / r25
            r7.didWriteData(r5, r1)     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            goto L_0x0378
        L_0x0376:
            r19 = r1
        L_0x0378:
            r5 = r19
            r1 = r39
            r11 = r44
            r19 = r3
            r3 = r38
            goto L_0x0434
        L_0x0384:
            r86 = r1
            r88 = r6
            r1 = -5
            if (r8 != r1) goto L_0x0409
            byte[] r1 = new byte[r10]     // Catch:{ Exception -> 0x041f, all -> 0x040c }
            int r6 = r14.offset     // Catch:{ Exception -> 0x041f, all -> 0x040c }
            int r6 = r6 + r10
            r7.limit(r6)     // Catch:{ Exception -> 0x041f, all -> 0x040c }
            int r6 = r14.offset     // Catch:{ Exception -> 0x041f, all -> 0x040c }
            r7.position(r6)     // Catch:{ Exception -> 0x041f, all -> 0x040c }
            r7.get(r1)     // Catch:{ Exception -> 0x041f, all -> 0x040c }
            int r6 = r14.size     // Catch:{ Exception -> 0x041f, all -> 0x040c }
            r7 = 1
            int r6 = r6 - r7
        L_0x039f:
            if (r6 < 0) goto L_0x03e2
            r10 = 3
            if (r6 <= r10) goto L_0x03e2
            byte r10 = r1[r6]     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            if (r10 != r7) goto L_0x03da
            int r7 = r6 + -1
            byte r7 = r1[r7]     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            if (r7 != 0) goto L_0x03da
            int r7 = r6 + -2
            byte r7 = r1[r7]     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            if (r7 != 0) goto L_0x03da
            int r7 = r6 + -3
            byte r10 = r1[r7]     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            if (r10 != 0) goto L_0x03da
            java.nio.ByteBuffer r6 = java.nio.ByteBuffer.allocate(r7)     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            int r10 = r14.size     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            int r10 = r10 - r7
            java.nio.ByteBuffer r10 = java.nio.ByteBuffer.allocate(r10)     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            r19 = r3
            r11 = 0
            java.nio.ByteBuffer r3 = r6.put(r1, r11, r7)     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            r3.position(r11)     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            int r3 = r14.size     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            int r3 = r3 - r7
            java.nio.ByteBuffer r1 = r10.put(r1, r7, r3)     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            r1.position(r11)     // Catch:{ Exception -> 0x02e1, all -> 0x02d2 }
            goto L_0x03e6
        L_0x03da:
            r19 = r3
            int r6 = r6 + -1
            r3 = r19
            r7 = 1
            goto L_0x039f
        L_0x03e2:
            r19 = r3
            r6 = 0
            r10 = 0
        L_0x03e6:
            r3 = r38
            r1 = r39
            r11 = r44
            android.media.MediaFormat r7 = android.media.MediaFormat.createVideoFormat(r11, r1, r3)     // Catch:{ Exception -> 0x0407, all -> 0x0405 }
            if (r6 == 0) goto L_0x03fc
            if (r10 == 0) goto L_0x03fc
            r7.setByteBuffer(r12, r6)     // Catch:{ Exception -> 0x0407, all -> 0x0405 }
            java.lang.String r6 = "csd-1"
            r7.setByteBuffer(r6, r10)     // Catch:{ Exception -> 0x0407, all -> 0x0405 }
        L_0x03fc:
            org.telegram.messenger.video.MP4Builder r6 = r13.mediaMuxer     // Catch:{ Exception -> 0x0407, all -> 0x0405 }
            r10 = 0
            int r6 = r6.addTrack(r7, r10)     // Catch:{ Exception -> 0x0407, all -> 0x0405 }
            r8 = r6
            goto L_0x0434
        L_0x0405:
            r0 = move-exception
            goto L_0x0411
        L_0x0407:
            r0 = move-exception
            goto L_0x0424
        L_0x0409:
            r19 = r3
            goto L_0x042e
        L_0x040c:
            r0 = move-exception
            r3 = r38
            r1 = r39
        L_0x0411:
            r10 = r87
            r72 = r92
            r44 = r94
            r2 = r1
            r15 = r13
            r7 = r36
            r6 = 0
            r1 = r0
            goto L_0x06a2
        L_0x041f:
            r0 = move-exception
            r3 = r38
            r1 = r39
        L_0x0424:
            r11 = r1
            r14 = r15
            goto L_0x0592
        L_0x0428:
            r86 = r1
            r19 = r3
            r88 = r6
        L_0x042e:
            r3 = r38
            r1 = r39
            r11 = r44
        L_0x0434:
            int r6 = r14.flags     // Catch:{ Exception -> 0x04b2, all -> 0x04ae }
            r6 = r6 & 4
            if (r6 == 0) goto L_0x043c
            r6 = 1
            goto L_0x043d
        L_0x043c:
            r6 = 0
        L_0x043d:
            r7 = 0
            r15.releaseOutputBuffer(r9, r7)     // Catch:{ Exception -> 0x04b2, all -> 0x04ae }
            r10 = r85
            r39 = r1
            r7 = r6
            r1 = -1
            r6 = r5
            r5 = r86
        L_0x044a:
            if (r9 == r1) goto L_0x045b
            r38 = r3
            r1 = r5
            r5 = r6
            r13 = r10
            r44 = r11
            r35 = r12
            r3 = r19
            r6 = r88
            goto L_0x01f6
        L_0x045b:
            if (r2 != 0) goto L_0x0495
            r18.drawImage()     // Catch:{ Exception -> 0x048e, all -> 0x050a }
            float r1 = (float) r4
            r9 = 1106247680(0x41var_, float:30.0)
            float r1 = r1 / r9
            float r1 = r1 * r24
            float r1 = r1 * r24
            float r1 = r1 * r24
            r85 = r2
            long r1 = (long) r1
            r9 = r37
            r9.setPresentationTime(r1)     // Catch:{ Exception -> 0x0489, all -> 0x050a }
            r9.swapBuffers()     // Catch:{ Exception -> 0x0489, all -> 0x050a }
            int r4 = r4 + 1
            float r1 = (float) r4     // Catch:{ Exception -> 0x0489, all -> 0x050a }
            r2 = 1106247680(0x41var_, float:30.0)
            float r2 = r2 * r25
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 < 0) goto L_0x0486
            r15.signalEndOfInputStream()     // Catch:{ Exception -> 0x0489, all -> 0x050a }
            r1 = 0
            r2 = 1
            goto L_0x049b
        L_0x0486:
            r2 = r85
            goto L_0x0499
        L_0x0489:
            r0 = move-exception
            r1 = r0
            r37 = r9
            goto L_0x0492
        L_0x048e:
            r0 = move-exception
        L_0x048f:
            r9 = r37
        L_0x0491:
            r1 = r0
        L_0x0492:
            r14 = r15
            goto L_0x0254
        L_0x0495:
            r85 = r2
            r9 = r37
        L_0x0499:
            r1 = r88
        L_0x049b:
            r38 = r3
            r37 = r9
            r13 = r10
            r44 = r11
            r35 = r12
            r3 = r19
            r76 = r6
            r6 = r1
            r1 = r5
            r5 = r76
            goto L_0x01f6
        L_0x04ae:
            r0 = move-exception
            r39 = r1
            goto L_0x04df
        L_0x04b2:
            r0 = move-exception
            r39 = r1
            goto L_0x048f
        L_0x04b6:
            r0 = move-exception
            r9 = r37
            r3 = r38
            goto L_0x0491
        L_0x04bc:
            r1 = r37
            r3 = r38
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x050c, all -> 0x050a }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x050c, all -> 0x050a }
            r4.<init>()     // Catch:{ Exception -> 0x050c, all -> 0x050a }
            java.lang.String r5 = "encoderOutputBuffer "
            r4.append(r5)     // Catch:{ Exception -> 0x050c, all -> 0x050a }
            r4.append(r9)     // Catch:{ Exception -> 0x050c, all -> 0x050a }
            java.lang.String r5 = " was null"
            r4.append(r5)     // Catch:{ Exception -> 0x050c, all -> 0x050a }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x050c, all -> 0x050a }
            r2.<init>(r4)     // Catch:{ Exception -> 0x050c, all -> 0x050a }
            throw r2     // Catch:{ Exception -> 0x050c, all -> 0x050a }
        L_0x04dc:
            r0 = move-exception
            r3 = r38
        L_0x04df:
            r10 = r87
            r72 = r92
            r44 = r94
            r1 = r0
            r15 = r13
            r7 = r36
        L_0x04e9:
            r2 = r39
            goto L_0x06a1
        L_0x04ed:
            r0 = move-exception
            goto L_0x0521
        L_0x04ef:
            r1 = r37
            r3 = r38
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x050c, all -> 0x050a }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x050c, all -> 0x050a }
            r4.<init>()     // Catch:{ Exception -> 0x050c, all -> 0x050a }
            java.lang.String r5 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x050c, all -> 0x050a }
            r4.append(r9)     // Catch:{ Exception -> 0x050c, all -> 0x050a }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x050c, all -> 0x050a }
            r2.<init>(r4)     // Catch:{ Exception -> 0x050c, all -> 0x050a }
            throw r2     // Catch:{ Exception -> 0x050c, all -> 0x050a }
        L_0x050a:
            r0 = move-exception
            goto L_0x04df
        L_0x050c:
            r0 = move-exception
            r37 = r1
            goto L_0x0525
        L_0x0510:
            r0 = move-exception
            r3 = r38
            r15 = r78
            r10 = r87
            r72 = r92
            r44 = r94
            r1 = r0
            r13 = r8
            goto L_0x0535
        L_0x051e:
            r0 = move-exception
            r13 = r78
        L_0x0521:
            r1 = r37
            r3 = r38
        L_0x0525:
            r14 = r15
            goto L_0x0547
        L_0x0527:
            r0 = move-exception
            r22 = r13
            r3 = r38
            r15 = r78
            r10 = r87
            r72 = r92
            r44 = r94
            r1 = r0
        L_0x0535:
            r7 = r36
        L_0x0537:
            r2 = r39
            goto L_0x14ff
        L_0x053b:
            r0 = move-exception
            r22 = r13
            r1 = r37
            r3 = r38
            r13 = r78
            r14 = r15
            r8 = r22
        L_0x0547:
            r11 = r39
            goto L_0x0592
        L_0x054a:
            r22 = r13
            r1 = r37
            r3 = r38
            r13 = r78
            r9 = r1
            r14 = r15
            r4 = r36
            r6 = 0
            r34 = 0
            r15 = r87
            goto L_0x0641
        L_0x055d:
            r0 = move-exception
            r13 = r78
            r1 = r37
            r3 = r38
            r14 = r15
            r11 = r39
            r8 = -5
            goto L_0x0592
        L_0x0569:
            r0 = move-exception
            r3 = r38
            r15 = r78
            r10 = r87
            r72 = r92
            r44 = r94
            r1 = r0
            r7 = r36
            goto L_0x05b1
        L_0x0578:
            r0 = move-exception
            r13 = r78
            r1 = r37
            r3 = r38
            r14 = r15
            r11 = r39
            goto L_0x058f
        L_0x0583:
            r0 = move-exception
            r13 = r78
            r36 = r7
            r1 = r9
            r3 = r10
            r39 = r11
            r37 = r1
            r14 = r15
        L_0x058f:
            r8 = -5
            r18 = 0
        L_0x0592:
            r1 = r0
            goto L_0x05f8
        L_0x0595:
            r0 = move-exception
            r13 = r78
            r36 = r7
            r3 = r10
            r39 = r11
            r1 = r0
            r14 = r15
            r8 = -5
            goto L_0x05f4
        L_0x05a2:
            r0 = move-exception
            r36 = r7
            r3 = r10
            r39 = r11
            r15 = r78
            r10 = r87
            r72 = r92
            r44 = r94
            r1 = r0
        L_0x05b1:
            r2 = r39
            goto L_0x157f
        L_0x05b5:
            r0 = move-exception
            r13 = r78
            r36 = r7
            r3 = r10
            r39 = r11
            r1 = r0
            goto L_0x05f2
        L_0x05bf:
            r0 = move-exception
            r36 = r7
            r39 = r11
            r15 = r78
            r72 = r92
            r44 = r94
            r1 = r0
            r3 = r10
            r2 = r39
            goto L_0x05e4
        L_0x05cf:
            r0 = move-exception
            r13 = r78
            r36 = r7
            r39 = r11
            r1 = r0
            goto L_0x05f1
        L_0x05d8:
            r0 = move-exception
            r36 = r7
            r15 = r78
            r72 = r92
            r44 = r94
            r1 = r0
            r2 = r9
            r3 = r10
        L_0x05e4:
            r6 = 0
            r13 = -5
            r10 = r87
            goto L_0x1581
        L_0x05ea:
            r0 = move-exception
            r13 = r78
            r36 = r7
            r1 = r0
        L_0x05f0:
            r11 = r9
        L_0x05f1:
            r3 = r10
        L_0x05f2:
            r8 = -5
            r14 = 0
        L_0x05f4:
            r18 = 0
            r37 = 0
        L_0x05f8:
            boolean r2 = r1 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x0694 }
            if (r2 == 0) goto L_0x0601
            if (r99 != 0) goto L_0x0601
            r34 = 1
            goto L_0x0603
        L_0x0601:
            r34 = 0
        L_0x0603:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0684 }
            r2.<init>()     // Catch:{ all -> 0x0684 }
            java.lang.String r4 = "bitrate: "
            r2.append(r4)     // Catch:{ all -> 0x0684 }
            r4 = r36
            r2.append(r4)     // Catch:{ all -> 0x0682 }
            java.lang.String r5 = " framerate: "
            r2.append(r5)     // Catch:{ all -> 0x0682 }
            r15 = r87
            r2.append(r15)     // Catch:{ all -> 0x0675 }
            java.lang.String r5 = " size: "
            r2.append(r5)     // Catch:{ all -> 0x0675 }
            r2.append(r3)     // Catch:{ all -> 0x0675 }
            java.lang.String r5 = "x"
            r2.append(r5)     // Catch:{ all -> 0x0675 }
            r2.append(r11)     // Catch:{ all -> 0x0675 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0675 }
            org.telegram.messenger.FileLog.e((java.lang.String) r2)     // Catch:{ all -> 0x0675 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x0675 }
            r22 = r8
            r39 = r11
            r6 = r34
            r9 = r37
            r34 = 1
        L_0x0641:
            if (r18 == 0) goto L_0x0656
            r18.release()     // Catch:{ all -> 0x0647 }
            goto L_0x0656
        L_0x0647:
            r0 = move-exception
            r72 = r92
            r44 = r94
            r1 = r0
            r7 = r4
            r10 = r15
            r2 = r39
            r15 = r13
            r13 = r22
            goto L_0x1581
        L_0x0656:
            if (r9 == 0) goto L_0x065b
            r9.release()     // Catch:{ all -> 0x0647 }
        L_0x065b:
            if (r14 == 0) goto L_0x0663
            r14.stop()     // Catch:{ all -> 0x0647 }
            r14.release()     // Catch:{ all -> 0x0647 }
        L_0x0663:
            r78.checkConversionCanceled()     // Catch:{ all -> 0x0647 }
            r72 = r92
            r44 = r94
            r21 = r4
            r10 = r15
            r5 = r39
            r4 = r3
            r15 = r13
            r13 = r22
            goto L_0x1544
        L_0x0675:
            r0 = move-exception
            r72 = r92
            r44 = r94
            r1 = r0
            r7 = r4
            r2 = r11
            r10 = r15
            r6 = r34
            r15 = r13
            goto L_0x06a2
        L_0x0682:
            r0 = move-exception
            goto L_0x0687
        L_0x0684:
            r0 = move-exception
            r4 = r36
        L_0x0687:
            r10 = r87
            r72 = r92
            r44 = r94
            r1 = r0
            r7 = r4
            r2 = r11
            r15 = r13
            r6 = r34
            goto L_0x06a2
        L_0x0694:
            r0 = move-exception
            r4 = r36
            r10 = r87
            r72 = r92
            r44 = r94
            r1 = r0
            r7 = r4
            r2 = r11
            r15 = r13
        L_0x06a1:
            r6 = 0
        L_0x06a2:
            r13 = r8
            goto L_0x1581
        L_0x06a5:
            r20 = r5
            r12 = r6
            r15 = r8
            r11 = r13
            r14 = r23
            r13 = r78
            android.media.MediaExtractor r1 = new android.media.MediaExtractor     // Catch:{ all -> 0x1568 }
            r1.<init>()     // Catch:{ all -> 0x1568 }
            r13.extractor = r1     // Catch:{ all -> 0x1568 }
            r8 = r79
            r1.setDataSource(r8)     // Catch:{ all -> 0x1568 }
            android.media.MediaExtractor r1 = r13.extractor     // Catch:{ all -> 0x1568 }
            r5 = 0
            int r6 = org.telegram.messenger.MediaController.findTrack(r1, r5)     // Catch:{ all -> 0x1568 }
            r1 = -1
            if (r7 == r1) goto L_0x06da
            android.media.MediaExtractor r1 = r13.extractor     // Catch:{ all -> 0x06cd }
            r3 = 1
            int r1 = org.telegram.messenger.MediaController.findTrack(r1, r3)     // Catch:{ all -> 0x06cd }
            r2 = r1
            goto L_0x06dc
        L_0x06cd:
            r0 = move-exception
            r72 = r92
            r44 = r94
            r1 = r0
            r2 = r9
            r3 = r10
            r10 = r15
        L_0x06d6:
            r6 = 0
        L_0x06d7:
            r15 = r13
            goto L_0x1580
        L_0x06da:
            r3 = 1
            r2 = -1
        L_0x06dc:
            java.lang.String r1 = "mime"
            if (r6 < 0) goto L_0x06f4
            android.media.MediaExtractor r3 = r13.extractor     // Catch:{ all -> 0x06cd }
            android.media.MediaFormat r3 = r3.getTrackFormat(r6)     // Catch:{ all -> 0x06cd }
            java.lang.String r3 = r3.getString(r1)     // Catch:{ all -> 0x06cd }
            boolean r3 = r3.equals(r11)     // Catch:{ all -> 0x06cd }
            if (r3 != 0) goto L_0x06f4
            r15 = r98
            r3 = 1
            goto L_0x06f7
        L_0x06f4:
            r15 = r98
            r3 = 0
        L_0x06f7:
            if (r15 != 0) goto L_0x073f
            if (r3 == 0) goto L_0x06fc
            goto L_0x073f
        L_0x06fc:
            android.media.MediaExtractor r2 = r13.extractor     // Catch:{ all -> 0x072f }
            org.telegram.messenger.video.MP4Builder r3 = r13.mediaMuxer     // Catch:{ all -> 0x072f }
            r1 = -1
            if (r7 == r1) goto L_0x0705
            r12 = 1
            goto L_0x0706
        L_0x0705:
            r12 = 0
        L_0x0706:
            r1 = r78
            r11 = 1
            r4 = r14
            r14 = 0
            r5 = r90
            r15 = r8
            r7 = r92
            r14 = r9
            r15 = r10
            r9 = r96
            r14 = 1
            r11 = r80
            r14 = 0
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ all -> 0x072d }
            r5 = r85
            r10 = r87
            r21 = r88
            r72 = r92
            r44 = r94
            r4 = r15
            r6 = 0
            r34 = 0
            r15 = r13
            r13 = -5
            goto L_0x1544
        L_0x072d:
            r0 = move-exception
            goto L_0x0732
        L_0x072f:
            r0 = move-exception
            r15 = r10
            r14 = 0
        L_0x0732:
            r2 = r85
            r10 = r87
            r7 = r88
            r72 = r92
            r44 = r94
            r1 = r0
            r3 = r15
            goto L_0x06d6
        L_0x073f:
            r15 = r10
            r10 = r14
            r14 = 0
            if (r6 < 0) goto L_0x1502
            r26 = -2147483648(0xfffffffvar_, double:NaN)
            r3 = 1000(0x3e8, float:1.401E-42)
            r9 = r87
            r8 = r98
            int r5 = r3 / r9
            int r5 = r5 * 1000
            long r4 = (long) r5
            r7 = 30
            if (r9 >= r7) goto L_0x077c
            int r7 = r9 + 5
            int r7 = r3 / r7
        L_0x075a:
            int r7 = r7 * 1000
            long r14 = (long) r7
            goto L_0x0781
        L_0x075e:
            r0 = move-exception
            r2 = r85
            r3 = r86
            r7 = r88
            r72 = r92
            r44 = r94
            r1 = r0
            r10 = r9
            r15 = r13
            goto L_0x157f
        L_0x076e:
            r0 = move-exception
            r3 = r88
            r72 = r92
            r44 = r94
            r1 = r0
            r71 = r6
            r10 = r9
            r15 = r13
            goto L_0x1473
        L_0x077c:
            int r7 = r9 + 1
            int r7 = r3 / r7
            goto L_0x075a
        L_0x0781:
            android.media.MediaExtractor r3 = r13.extractor     // Catch:{ Exception -> 0x1465, all -> 0x1453 }
            r3.selectTrack(r6)     // Catch:{ Exception -> 0x1465, all -> 0x1453 }
            android.media.MediaExtractor r3 = r13.extractor     // Catch:{ Exception -> 0x1465, all -> 0x1453 }
            android.media.MediaFormat r7 = r3.getTrackFormat(r6)     // Catch:{ Exception -> 0x1465, all -> 0x1453 }
            r30 = 0
            int r3 = (r94 > r30 ? 1 : (r94 == r30 ? 0 : -1))
            if (r3 < 0) goto L_0x07af
            r3 = 1157234688(0x44fa0000, float:2000.0)
            int r3 = (r25 > r3 ? 1 : (r25 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x079c
            r3 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x07aa
        L_0x079c:
            r3 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r3 = (r25 > r3 ? 1 : (r25 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x07a7
            r3 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x07aa
        L_0x07a7:
            r3 = 1560000(0x17cdc0, float:2.186026E-39)
        L_0x07aa:
            r37 = r14
            r35 = 0
            goto L_0x07bf
        L_0x07af:
            if (r88 > 0) goto L_0x07b9
            r35 = r94
            r37 = r14
            r3 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x07bf
        L_0x07b9:
            r3 = r88
            r35 = r94
            r37 = r14
        L_0x07bf:
            r14 = r89
            if (r14 <= 0) goto L_0x07e5
            int r3 = java.lang.Math.min(r14, r3)     // Catch:{ Exception -> 0x07d9, all -> 0x07c8 }
            goto L_0x07e5
        L_0x07c8:
            r0 = move-exception
            r2 = r85
            r72 = r92
            r1 = r0
            r7 = r3
            r10 = r9
            r15 = r13
            r44 = r35
        L_0x07d3:
            r6 = 0
            r13 = -5
        L_0x07d5:
            r3 = r86
            goto L_0x1581
        L_0x07d9:
            r0 = move-exception
            r72 = r92
            r1 = r0
            r71 = r6
            r10 = r9
            r15 = r13
            r44 = r35
            goto L_0x1473
        L_0x07e5:
            r30 = 0
            int r15 = (r35 > r30 ? 1 : (r35 == r30 ? 0 : -1))
            if (r15 < 0) goto L_0x07ee
            r14 = r18
            goto L_0x07f0
        L_0x07ee:
            r14 = r35
        L_0x07f0:
            int r23 = (r14 > r30 ? 1 : (r14 == r30 ? 0 : -1))
            if (r23 < 0) goto L_0x082c
            r23 = r2
            android.media.MediaExtractor r2 = r13.extractor     // Catch:{ Exception -> 0x0817, all -> 0x0808 }
            r35 = r4
            r4 = 0
            r2.seekTo(r14, r4)     // Catch:{ Exception -> 0x0817, all -> 0x0808 }
            r4 = r90
            r30 = r6
            r33 = r12
            r12 = 0
            goto L_0x085d
        L_0x0808:
            r0 = move-exception
            r2 = r85
            r72 = r92
            r1 = r0
            r7 = r3
            r10 = r9
            r44 = r14
            r6 = 0
            r3 = r86
            goto L_0x06d7
        L_0x0817:
            r0 = move-exception
            r72 = r92
            r1 = r0
            r71 = r6
            r10 = r9
            r44 = r14
        L_0x0820:
            r8 = 0
            r14 = 0
            r23 = 0
            r54 = 0
            r69 = 0
            r15 = r13
            r13 = -5
            goto L_0x147c
        L_0x082c:
            r23 = r2
            r35 = r4
            r30 = 0
            r4 = r90
            int r2 = (r4 > r30 ? 1 : (r4 == r30 ? 0 : -1))
            if (r2 <= 0) goto L_0x0851
            android.media.MediaExtractor r2 = r13.extractor     // Catch:{ Exception -> 0x0817, all -> 0x0808 }
            r30 = r6
            r6 = 0
            r2.seekTo(r4, r6)     // Catch:{ Exception -> 0x0847, all -> 0x0808 }
            r6 = r104
            r33 = r12
            r12 = 0
            goto L_0x085f
        L_0x0847:
            r0 = move-exception
            r72 = r92
            r1 = r0
            r10 = r9
            r44 = r14
            r71 = r30
            goto L_0x0820
        L_0x0851:
            r30 = r6
            android.media.MediaExtractor r2 = r13.extractor     // Catch:{ Exception -> 0x1449, all -> 0x1438 }
            r33 = r12
            r6 = 0
            r12 = 0
            r2.seekTo(r12, r6)     // Catch:{ Exception -> 0x142d, all -> 0x1424 }
        L_0x085d:
            r6 = r104
        L_0x085f:
            if (r6 == 0) goto L_0x08a1
            r2 = 90
            r13 = r81
            if (r13 == r2) goto L_0x0871
            r2 = 270(0x10e, float:3.78E-43)
            if (r13 != r2) goto L_0x086c
            goto L_0x0871
        L_0x086c:
            int r2 = r6.transformWidth     // Catch:{ Exception -> 0x088b, all -> 0x087b }
            int r12 = r6.transformHeight     // Catch:{ Exception -> 0x088b, all -> 0x087b }
            goto L_0x0875
        L_0x0871:
            int r2 = r6.transformHeight     // Catch:{ Exception -> 0x088b, all -> 0x087b }
            int r12 = r6.transformWidth     // Catch:{ Exception -> 0x088b, all -> 0x087b }
        L_0x0875:
            r76 = r12
            r12 = r2
            r2 = r76
            goto L_0x08a7
        L_0x087b:
            r0 = move-exception
            r2 = r85
            r72 = r92
            r1 = r0
            r7 = r3
            r10 = r9
            r44 = r14
            r6 = 0
            r13 = -5
            r15 = r78
            goto L_0x07d5
        L_0x088b:
            r0 = move-exception
            r72 = r92
            r1 = r0
        L_0x088f:
            r10 = r9
            r44 = r14
            r71 = r30
            r8 = 0
            r13 = -5
            r14 = 0
            r23 = 0
            r54 = 0
            r69 = 0
            r15 = r78
            goto L_0x147c
        L_0x08a1:
            r13 = r81
            r12 = r85
            r2 = r86
        L_0x08a7:
            boolean r39 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x142d, all -> 0x1424 }
            if (r39 == 0) goto L_0x08c7
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x088b, all -> 0x087b }
            r4.<init>()     // Catch:{ Exception -> 0x088b, all -> 0x087b }
            java.lang.String r5 = "create encoder with w = "
            r4.append(r5)     // Catch:{ Exception -> 0x088b, all -> 0x087b }
            r4.append(r12)     // Catch:{ Exception -> 0x088b, all -> 0x087b }
            java.lang.String r5 = " h = "
            r4.append(r5)     // Catch:{ Exception -> 0x088b, all -> 0x087b }
            r4.append(r2)     // Catch:{ Exception -> 0x088b, all -> 0x087b }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x088b, all -> 0x087b }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x088b, all -> 0x087b }
        L_0x08c7:
            android.media.MediaFormat r4 = android.media.MediaFormat.createVideoFormat(r11, r12, r2)     // Catch:{ Exception -> 0x142d, all -> 0x1424 }
            java.lang.String r5 = "color-format"
            r6 = 2130708361(0x7var_, float:1.701803E38)
            r4.setInteger(r5, r6)     // Catch:{ Exception -> 0x142d, all -> 0x1424 }
            java.lang.String r5 = "bitrate"
            r4.setInteger(r5, r3)     // Catch:{ Exception -> 0x142d, all -> 0x1424 }
            java.lang.String r5 = "frame-rate"
            r4.setInteger(r5, r9)     // Catch:{ Exception -> 0x142d, all -> 0x1424 }
            java.lang.String r5 = "i-frame-interval"
            r6 = 2
            r4.setInteger(r5, r6)     // Catch:{ Exception -> 0x142d, all -> 0x1424 }
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x142d, all -> 0x1424 }
            r5 = 23
            if (r6 >= r5) goto L_0x091b
            int r5 = java.lang.Math.min(r2, r12)     // Catch:{ Exception -> 0x088b, all -> 0x087b }
            r39 = r2
            r2 = 480(0x1e0, float:6.73E-43)
            if (r5 > r2) goto L_0x091d
            r2 = 921600(0xe1000, float:1.291437E-39)
            if (r3 <= r2) goto L_0x08f9
            goto L_0x08fa
        L_0x08f9:
            r2 = r3
        L_0x08fa:
            java.lang.String r3 = "bitrate"
            r4.setInteger(r3, r2)     // Catch:{ Exception -> 0x0914, all -> 0x0902 }
            r21 = r2
            goto L_0x091f
        L_0x0902:
            r0 = move-exception
            r3 = r86
            r72 = r92
            r1 = r0
            r7 = r2
            r10 = r9
            r44 = r14
            r6 = 0
            r13 = -5
            r15 = r78
            r2 = r85
            goto L_0x1581
        L_0x0914:
            r0 = move-exception
            r72 = r92
            r1 = r0
            r3 = r2
            goto L_0x088f
        L_0x091b:
            r39 = r2
        L_0x091d:
            r21 = r3
        L_0x091f:
            android.media.MediaCodec r5 = android.media.MediaCodec.createEncoderByType(r11)     // Catch:{ Exception -> 0x1411, all -> 0x13fc }
            r2 = 0
            r3 = 1
            r5.configure(r4, r2, r2, r3)     // Catch:{ Exception -> 0x13e6, all -> 0x13fc }
            org.telegram.messenger.video.InputSurface r4 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x13e6, all -> 0x13fc }
            android.view.Surface r2 = r5.createInputSurface()     // Catch:{ Exception -> 0x13e6, all -> 0x13fc }
            r4.<init>(r2)     // Catch:{ Exception -> 0x13e6, all -> 0x13fc }
            r4.makeCurrent()     // Catch:{ Exception -> 0x13cc, all -> 0x13fc }
            r5.start()     // Catch:{ Exception -> 0x13cc, all -> 0x13fc }
            java.lang.String r2 = r7.getString(r1)     // Catch:{ Exception -> 0x13cc, all -> 0x13fc }
            android.media.MediaCodec r2 = android.media.MediaCodec.createDecoderByType(r2)     // Catch:{ Exception -> 0x13cc, all -> 0x13fc }
            org.telegram.messenger.video.OutputSurface r13 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x13af, all -> 0x13fc }
            r43 = 0
            float r3 = (float) r9
            r45 = 0
            r50 = r1
            r1 = r13
            r52 = r2
            r51 = r23
            r53 = r39
            r2 = r100
            r23 = r3
            r39 = 1
            r3 = r43
            r54 = r4
            r4 = r101
            r88 = r5
            r5 = r102
            r56 = r6
            r55 = r30
            r6 = r104
            r57 = r7
            r7 = r85
            r8 = r86
            r9 = r83
            r58 = r10
            r10 = r84
            r60 = r11
            r59 = r20
            r11 = r81
            r63 = r12
            r64 = r33
            r12 = r23
            r94 = r14
            r15 = r78
            r14 = r13
            r13 = r45
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x139c, all -> 0x1395 }
            if (r105 != 0) goto L_0x09ee
            r1 = r86
            int r2 = java.lang.Math.max(r1, r1)     // Catch:{ Exception -> 0x09d1, all -> 0x09bc }
            float r2 = (float) r2
            r6 = r83
            r7 = r84
            int r3 = java.lang.Math.max(r7, r6)     // Catch:{ Exception -> 0x09ba, all -> 0x09b8 }
            float r3 = (float) r3
            float r2 = r2 / r3
            r3 = 1063675494(0x3var_, float:0.9)
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 >= 0) goto L_0x09b5
            r2 = r85
            r4 = 1
            java.lang.String r3 = createFragmentShader(r6, r7, r2, r1, r4)     // Catch:{ Exception -> 0x09b3, all -> 0x09b1 }
            r5 = 0
            java.lang.String r8 = createFragmentShader(r6, r7, r2, r1, r5)     // Catch:{ Exception -> 0x09b3, all -> 0x09b1 }
            r14.changeFragmentShader(r3, r8)     // Catch:{ Exception -> 0x09b3, all -> 0x09b1 }
            goto L_0x09f7
        L_0x09b1:
            r0 = move-exception
            goto L_0x09c3
        L_0x09b3:
            r0 = move-exception
            goto L_0x09d8
        L_0x09b5:
            r2 = r85
            goto L_0x09f6
        L_0x09b8:
            r0 = move-exception
            goto L_0x09c1
        L_0x09ba:
            r0 = move-exception
            goto L_0x09d6
        L_0x09bc:
            r0 = move-exception
            r6 = r83
            r7 = r84
        L_0x09c1:
            r2 = r85
        L_0x09c3:
            r10 = r87
            r72 = r92
            r44 = r94
            r3 = r1
            r7 = r21
            r6 = 0
            r13 = -5
            r1 = r0
            goto L_0x1581
        L_0x09d1:
            r0 = move-exception
            r6 = r83
            r7 = r84
        L_0x09d6:
            r2 = r85
        L_0x09d8:
            r10 = r87
            r72 = r92
            r44 = r94
            r1 = r0
            r23 = r14
            r3 = r21
            r8 = r52
        L_0x09e5:
            r71 = r55
            r13 = -5
            r69 = 0
        L_0x09ea:
            r14 = r88
            goto L_0x147c
        L_0x09ee:
            r6 = r83
            r7 = r84
            r2 = r85
            r1 = r86
        L_0x09f6:
            r4 = 1
        L_0x09f7:
            android.view.Surface r3 = r14.getSurface()     // Catch:{ Exception -> 0x137c, all -> 0x1395 }
            r8 = r52
            r5 = r57
            r9 = 0
            r10 = 0
            r8.configure(r5, r3, r9, r10)     // Catch:{ Exception -> 0x1368, all -> 0x1395 }
            r8.start()     // Catch:{ Exception -> 0x1368, all -> 0x1395 }
            r3 = r56
            r5 = 21
            if (r3 >= r5) goto L_0x0a28
            java.nio.ByteBuffer[] r3 = r8.getInputBuffers()     // Catch:{ Exception -> 0x0a16, all -> 0x09b1 }
            java.nio.ByteBuffer[] r5 = r88.getOutputBuffers()     // Catch:{ Exception -> 0x0a16, all -> 0x09b1 }
            goto L_0x0a2a
        L_0x0a16:
            r0 = move-exception
            r10 = r87
            r72 = r92
            r44 = r94
            r1 = r0
            r69 = r9
        L_0x0a20:
            r23 = r14
            r3 = r21
            r71 = r55
            r13 = -5
            goto L_0x09ea
        L_0x0a28:
            r3 = r9
            r5 = r3
        L_0x0a2a:
            r10 = r51
            if (r10 < 0) goto L_0x0b4e
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x0b3c, all -> 0x0b2c }
            android.media.MediaFormat r11 = r11.getTrackFormat(r10)     // Catch:{ Exception -> 0x0b3c, all -> 0x0b2c }
            r12 = r50
            java.lang.String r13 = r11.getString(r12)     // Catch:{ Exception -> 0x0b3c, all -> 0x0b2c }
            java.lang.String r9 = "audio/mp4a-latm"
            boolean r9 = r13.equals(r9)     // Catch:{ Exception -> 0x0b3c, all -> 0x0b2c }
            if (r9 != 0) goto L_0x0a5b
            java.lang.String r9 = r11.getString(r12)     // Catch:{ Exception -> 0x0a51, all -> 0x09b1 }
            java.lang.String r13 = "audio/mpeg"
            boolean r9 = r9.equals(r13)     // Catch:{ Exception -> 0x0a51, all -> 0x09b1 }
            if (r9 == 0) goto L_0x0a4f
            goto L_0x0a5b
        L_0x0a4f:
            r13 = 0
            goto L_0x0a5c
        L_0x0a51:
            r0 = move-exception
        L_0x0a52:
            r10 = r87
            r72 = r92
            r44 = r94
            r1 = r0
            goto L_0x0b48
        L_0x0a5b:
            r13 = 1
        L_0x0a5c:
            java.lang.String r9 = r11.getString(r12)     // Catch:{ Exception -> 0x0b3c, all -> 0x0b2c }
            java.lang.String r12 = "audio/unknown"
            boolean r9 = r9.equals(r12)     // Catch:{ Exception -> 0x0b3c, all -> 0x0b2c }
            if (r9 == 0) goto L_0x0a69
            r10 = -1
        L_0x0a69:
            if (r10 < 0) goto L_0x0b21
            if (r13 == 0) goto L_0x0ac2
            org.telegram.messenger.video.MP4Builder r9 = r15.mediaMuxer     // Catch:{ Exception -> 0x0abe, all -> 0x0ab5 }
            int r9 = r9.addTrack(r11, r4)     // Catch:{ Exception -> 0x0abe, all -> 0x0ab5 }
            android.media.MediaExtractor r12 = r15.extractor     // Catch:{ Exception -> 0x0abe, all -> 0x0ab5 }
            r12.selectTrack(r10)     // Catch:{ Exception -> 0x0abe, all -> 0x0ab5 }
            java.lang.String r12 = "max-input-size"
            int r11 = r11.getInteger(r12)     // Catch:{ Exception -> 0x0a7f, all -> 0x09b1 }
            goto L_0x0a85
        L_0x0a7f:
            r0 = move-exception
            r11 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)     // Catch:{ Exception -> 0x0abe, all -> 0x0ab5 }
            r11 = 0
        L_0x0a85:
            if (r11 > 0) goto L_0x0a89
            r11 = 65536(0x10000, float:9.18355E-41)
        L_0x0a89:
            java.nio.ByteBuffer r12 = java.nio.ByteBuffer.allocateDirect(r11)     // Catch:{ Exception -> 0x0abe, all -> 0x0ab5 }
            r20 = r5
            r6 = 0
            r4 = r90
            int r23 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r23 <= 0) goto L_0x0aa1
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0a51, all -> 0x09b1 }
            r7 = 0
            r6.seekTo(r4, r7)     // Catch:{ Exception -> 0x0a51, all -> 0x09b1 }
            r7 = r11
            r23 = r12
            goto L_0x0aac
        L_0x0aa1:
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0a51, all -> 0x0ab3 }
            r7 = r11
            r23 = r12
            r1 = 0
            r11 = 0
            r6.seekTo(r11, r1)     // Catch:{ Exception -> 0x0a51, all -> 0x0ab3 }
        L_0x0aac:
            r11 = r92
            r6 = r7
            r1 = r9
            r9 = 0
            goto L_0x0b5a
        L_0x0ab3:
            r0 = move-exception
            goto L_0x0ab8
        L_0x0ab5:
            r0 = move-exception
            r4 = r90
        L_0x0ab8:
            r3 = r86
            r10 = r87
            goto L_0x1408
        L_0x0abe:
            r0 = move-exception
            r4 = r90
            goto L_0x0a52
        L_0x0ac2:
            r20 = r5
            r4 = r90
            android.media.MediaExtractor r1 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0b1f, all -> 0x0b1d }
            r1.<init>()     // Catch:{ Exception -> 0x0b1f, all -> 0x0b1d }
            r6 = r79
            r7 = r86
            r1.setDataSource(r6)     // Catch:{ Exception -> 0x0b1f, all -> 0x0b1d }
            r1.selectTrack(r10)     // Catch:{ Exception -> 0x0b1f, all -> 0x0b1d }
            r9 = r13
            r12 = 0
            int r23 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r23 <= 0) goto L_0x0aec
            r12 = 0
            r1.seekTo(r4, r12)     // Catch:{ Exception -> 0x0a51, all -> 0x0ae1 }
            goto L_0x0af1
        L_0x0ae1:
            r0 = move-exception
            r10 = r87
            r72 = r92
            r44 = r94
            r1 = r0
            r3 = r7
            goto L_0x140d
        L_0x0aec:
            r6 = r12
            r12 = 0
            r1.seekTo(r6, r12)     // Catch:{ Exception -> 0x0b1f, all -> 0x0b1d }
        L_0x0af1:
            org.telegram.messenger.video.AudioRecoder r6 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x0b1f, all -> 0x0b1d }
            r6.<init>(r11, r1, r10)     // Catch:{ Exception -> 0x0b1f, all -> 0x0b1d }
            r6.startTime = r4     // Catch:{ Exception -> 0x0b0f, all -> 0x0b1d }
            r11 = r92
            r6.endTime = r11     // Catch:{ Exception -> 0x0b0d, all -> 0x0b0b }
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ Exception -> 0x0b0d, all -> 0x0b0b }
            android.media.MediaFormat r7 = r6.format     // Catch:{ Exception -> 0x0b0d, all -> 0x0b0b }
            r13 = 1
            int r1 = r1.addTrack(r7, r13)     // Catch:{ Exception -> 0x0b0d, all -> 0x0b0b }
            r13 = r9
            r23 = 0
            r9 = r6
            r6 = 0
            goto L_0x0b5a
        L_0x0b0b:
            r0 = move-exception
            goto L_0x0b31
        L_0x0b0d:
            r0 = move-exception
            goto L_0x0b12
        L_0x0b0f:
            r0 = move-exception
            r11 = r92
        L_0x0b12:
            r10 = r87
            r44 = r94
            r1 = r0
            r69 = r6
            r72 = r11
            goto L_0x0a20
        L_0x0b1d:
            r0 = move-exception
            goto L_0x0b2f
        L_0x0b1f:
            r0 = move-exception
            goto L_0x0b3f
        L_0x0b21:
            r11 = r92
            r20 = r5
            r9 = r13
            r4 = r90
            r1 = -5
            r6 = 0
            r9 = 0
            goto L_0x0b58
        L_0x0b2c:
            r0 = move-exception
            r4 = r90
        L_0x0b2f:
            r11 = r92
        L_0x0b31:
            r3 = r86
            r10 = r87
            r44 = r94
            r1 = r0
            r72 = r11
            goto L_0x140d
        L_0x0b3c:
            r0 = move-exception
            r4 = r90
        L_0x0b3f:
            r11 = r92
            r10 = r87
            r44 = r94
            r1 = r0
            r72 = r11
        L_0x0b48:
            r23 = r14
            r3 = r21
            goto L_0x09e5
        L_0x0b4e:
            r11 = r92
            r20 = r5
            r4 = r90
            r1 = -5
            r6 = 0
            r9 = 0
            r13 = 1
        L_0x0b58:
            r23 = 0
        L_0x0b5a:
            if (r10 >= 0) goto L_0x0b5e
            r7 = 1
            goto L_0x0b5f
        L_0x0b5e:
            r7 = 0
        L_0x0b5f:
            r78.checkConversionCanceled()     // Catch:{ Exception -> 0x134e, all -> 0x1395 }
            r92 = r94
            r65 = r18
            r67 = r65
            r52 = r23
            r61 = r26
            r23 = 0
            r26 = 0
            r30 = -5
            r33 = 0
            r40 = 1
            r50 = 0
            r56 = 0
            r27 = r7
            r7 = 0
        L_0x0b7d:
            if (r23 == 0) goto L_0x0b9a
            if (r13 != 0) goto L_0x0b84
            if (r27 != 0) goto L_0x0b84
            goto L_0x0b9a
        L_0x0b84:
            r4 = r86
            r10 = r87
            r44 = r92
            r5 = r2
            r2 = r8
            r69 = r9
            r72 = r11
            r23 = r14
            r71 = r55
            r6 = 0
            r13 = 0
            r14 = r88
            goto L_0x14c3
        L_0x0b9a:
            r78.checkConversionCanceled()     // Catch:{ Exception -> 0x1335, all -> 0x1323 }
            if (r13 != 0) goto L_0x0bcc
            if (r9 == 0) goto L_0x0bcc
            r94 = r7
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ Exception -> 0x0bb8, all -> 0x0bac }
            boolean r7 = r9.step(r7, r1)     // Catch:{ Exception -> 0x0bb8, all -> 0x0bac }
            r27 = r7
            goto L_0x0bce
        L_0x0bac:
            r0 = move-exception
            r3 = r86
            r10 = r87
            r44 = r92
            r1 = r0
            r72 = r11
            goto L_0x132f
        L_0x0bb8:
            r0 = move-exception
            r10 = r87
            r44 = r92
            r1 = r0
            r69 = r9
            r72 = r11
            r23 = r14
            r3 = r21
            r13 = r30
            r71 = r55
            goto L_0x09ea
        L_0x0bcc:
            r94 = r7
        L_0x0bce:
            if (r33 != 0) goto L_0x0d84
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x0d6f, all -> 0x0d63 }
            int r7 = r7.getSampleTrackIndex()     // Catch:{ Exception -> 0x0d6f, all -> 0x0d63 }
            r69 = r9
            r9 = r55
            if (r7 != r9) goto L_0x0CLASSNAME
            r4 = 2500(0x9c4, double:1.235E-320)
            int r7 = r8.dequeueInputBuffer(r4)     // Catch:{ Exception -> 0x0c3a, all -> 0x0bac }
            if (r7 < 0) goto L_0x0CLASSNAME
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c3a, all -> 0x0bac }
            r5 = 21
            if (r4 >= r5) goto L_0x0bed
            r4 = r3[r7]     // Catch:{ Exception -> 0x0c3a, all -> 0x0bac }
            goto L_0x0bf1
        L_0x0bed:
            java.nio.ByteBuffer r4 = r8.getInputBuffer(r7)     // Catch:{ Exception -> 0x0c3a, all -> 0x0bac }
        L_0x0bf1:
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x0c3a, all -> 0x0bac }
            r55 = r3
            r3 = 0
            int r46 = r5.readSampleData(r4, r3)     // Catch:{ Exception -> 0x0c3a, all -> 0x0bac }
            if (r46 >= 0) goto L_0x0c0e
            r45 = 0
            r46 = 0
            r47 = 0
            r49 = 4
            r43 = r8
            r44 = r7
            r43.queueInputBuffer(r44, r45, r46, r47, r49)     // Catch:{ Exception -> 0x0c3a, all -> 0x0bac }
            r33 = 1
            goto L_0x0CLASSNAME
        L_0x0c0e:
            r45 = 0
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x0c3a, all -> 0x0bac }
            long r47 = r3.getSampleTime()     // Catch:{ Exception -> 0x0c3a, all -> 0x0bac }
            r49 = 0
            r43 = r8
            r44 = r7
            r43.queueInputBuffer(r44, r45, r46, r47, r49)     // Catch:{ Exception -> 0x0c3a, all -> 0x0bac }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x0c3a, all -> 0x0bac }
            r3.advance()     // Catch:{ Exception -> 0x0c3a, all -> 0x0bac }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r55 = r3
        L_0x0CLASSNAME:
            r41 = r1
            r71 = r9
            r70 = r10
            r72 = r11
            r9 = r58
            r1 = 2500(0x9c4, double:1.235E-320)
            r3 = 0
            r58 = r13
            r12 = r90
            goto L_0x0d47
        L_0x0c3a:
            r0 = move-exception
            r10 = r87
            r44 = r92
            r1 = r0
            r71 = r9
        L_0x0CLASSNAME:
            r72 = r11
            goto L_0x0d7c
        L_0x0CLASSNAME:
            r55 = r3
            if (r13 == 0) goto L_0x0d31
            r3 = -1
            if (r10 == r3) goto L_0x0d20
            if (r7 != r10) goto L_0x0d31
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0d17, all -> 0x0d11 }
            r4 = 28
            if (r3 < r4) goto L_0x0CLASSNAME
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x0c6f, all -> 0x0bac }
            long r4 = r4.getSampleSize()     // Catch:{ Exception -> 0x0c6f, all -> 0x0bac }
            r71 = r9
            r70 = r10
            long r9 = (long) r6
            int r7 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r7 <= 0) goto L_0x0c7c
            r6 = 1024(0x400, double:5.06E-321)
            long r4 = r4 + r6
            int r6 = (int) r4
            java.nio.ByteBuffer r52 = java.nio.ByteBuffer.allocateDirect(r6)     // Catch:{ Exception -> 0x0c6d, all -> 0x0bac }
            goto L_0x0c7c
        L_0x0c6d:
            r0 = move-exception
            goto L_0x0CLASSNAME
        L_0x0c6f:
            r0 = move-exception
            r71 = r9
        L_0x0CLASSNAME:
            r10 = r87
            r44 = r92
            r1 = r0
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r71 = r9
            r70 = r10
        L_0x0c7c:
            r4 = r52
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x0d0f, all -> 0x0d11 }
            r7 = 0
            int r5 = r5.readSampleData(r4, r7)     // Catch:{ Exception -> 0x0d0f, all -> 0x0d11 }
            r9 = r58
            r9.size = r5     // Catch:{ Exception -> 0x0d0f, all -> 0x0d11 }
            r5 = 21
            if (r3 >= r5) goto L_0x0CLASSNAME
            r4.position(r7)     // Catch:{ Exception -> 0x0c6d, all -> 0x0bac }
            int r3 = r9.size     // Catch:{ Exception -> 0x0c6d, all -> 0x0bac }
            r4.limit(r3)     // Catch:{ Exception -> 0x0c6d, all -> 0x0bac }
        L_0x0CLASSNAME:
            int r3 = r9.size     // Catch:{ Exception -> 0x0d0f, all -> 0x0d11 }
            if (r3 < 0) goto L_0x0ca9
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x0c6d, all -> 0x0bac }
            r95 = r6
            long r5 = r3.getSampleTime()     // Catch:{ Exception -> 0x0c6d, all -> 0x0bac }
            r9.presentationTimeUs = r5     // Catch:{ Exception -> 0x0c6d, all -> 0x0bac }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x0c6d, all -> 0x0bac }
            r3.advance()     // Catch:{ Exception -> 0x0c6d, all -> 0x0bac }
            goto L_0x0cb0
        L_0x0ca9:
            r95 = r6
            r3 = 0
            r9.size = r3     // Catch:{ Exception -> 0x0d0f, all -> 0x0d11 }
            r33 = 1
        L_0x0cb0:
            int r3 = r9.size     // Catch:{ Exception -> 0x0d0f, all -> 0x0d11 }
            if (r3 <= 0) goto L_0x0d00
            r5 = 0
            int r3 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1))
            if (r3 < 0) goto L_0x0cc0
            long r5 = r9.presentationTimeUs     // Catch:{ Exception -> 0x0c6d, all -> 0x0bac }
            int r3 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r3 >= 0) goto L_0x0d00
        L_0x0cc0:
            r3 = 0
            r9.offset = r3     // Catch:{ Exception -> 0x0d0f, all -> 0x0d11 }
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x0d0f, all -> 0x0d11 }
            int r5 = r5.getSampleFlags()     // Catch:{ Exception -> 0x0d0f, all -> 0x0d11 }
            r9.flags = r5     // Catch:{ Exception -> 0x0d0f, all -> 0x0d11 }
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x0d0f, all -> 0x0d11 }
            long r5 = r5.writeSampleData(r1, r4, r9, r3)     // Catch:{ Exception -> 0x0d0f, all -> 0x0d11 }
            r31 = 0
            int r3 = (r5 > r31 ? 1 : (r5 == r31 ? 0 : -1))
            if (r3 == 0) goto L_0x0d00
            org.telegram.messenger.MediaController$VideoConvertorListener r3 = r15.callback     // Catch:{ Exception -> 0x0d0f, all -> 0x0d11 }
            if (r3 == 0) goto L_0x0d00
            r72 = r11
            long r10 = r9.presentationTimeUs     // Catch:{ Exception -> 0x0cfe, all -> 0x0cfc }
            r41 = r1
            r58 = r13
            r1 = 2500(0x9c4, double:1.235E-320)
            r12 = r90
            long r42 = r10 - r12
            int r7 = (r42 > r50 ? 1 : (r42 == r50 ? 0 : -1))
            if (r7 <= 0) goto L_0x0cef
            long r50 = r10 - r12
        L_0x0cef:
            r10 = r50
            float r7 = (float) r10
            float r7 = r7 / r24
            float r7 = r7 / r25
            r3.didWriteData(r5, r7)     // Catch:{ Exception -> 0x0d61, all -> 0x0d5f }
            r50 = r10
            goto L_0x0d0a
        L_0x0cfc:
            r0 = move-exception
            goto L_0x0d14
        L_0x0cfe:
            r0 = move-exception
            goto L_0x0d1c
        L_0x0d00:
            r41 = r1
            r72 = r11
            r58 = r13
            r1 = 2500(0x9c4, double:1.235E-320)
            r12 = r90
        L_0x0d0a:
            r6 = r95
            r52 = r4
            goto L_0x0d46
        L_0x0d0f:
            r0 = move-exception
            goto L_0x0d1a
        L_0x0d11:
            r0 = move-exception
            r72 = r11
        L_0x0d14:
            r12 = r90
            goto L_0x0d67
        L_0x0d17:
            r0 = move-exception
            r71 = r9
        L_0x0d1a:
            r72 = r11
        L_0x0d1c:
            r12 = r90
            goto L_0x0d77
        L_0x0d20:
            r41 = r1
            r71 = r9
            r70 = r10
            r72 = r11
            r9 = r58
            r1 = 2500(0x9c4, double:1.235E-320)
            r58 = r13
            r12 = r90
            goto L_0x0d42
        L_0x0d31:
            r41 = r1
            r71 = r9
            r70 = r10
            r72 = r11
            r9 = r58
            r1 = 2500(0x9c4, double:1.235E-320)
            r58 = r13
            r12 = r90
            r3 = -1
        L_0x0d42:
            if (r7 != r3) goto L_0x0d46
            r3 = 1
            goto L_0x0d47
        L_0x0d46:
            r3 = 0
        L_0x0d47:
            if (r3 == 0) goto L_0x0d97
            int r44 = r8.dequeueInputBuffer(r1)     // Catch:{ Exception -> 0x0d61, all -> 0x0d5f }
            if (r44 < 0) goto L_0x0d97
            r45 = 0
            r46 = 0
            r47 = 0
            r49 = 4
            r43 = r8
            r43.queueInputBuffer(r44, r45, r46, r47, r49)     // Catch:{ Exception -> 0x0d61, all -> 0x0d5f }
            r33 = 1
            goto L_0x0d97
        L_0x0d5f:
            r0 = move-exception
            goto L_0x0d67
        L_0x0d61:
            r0 = move-exception
            goto L_0x0d77
        L_0x0d63:
            r0 = move-exception
            r72 = r11
            r12 = r4
        L_0x0d67:
            r2 = r85
            r3 = r86
            r10 = r87
            goto L_0x132c
        L_0x0d6f:
            r0 = move-exception
            r69 = r9
            r72 = r11
            r71 = r55
            r12 = r4
        L_0x0d77:
            r10 = r87
            r44 = r92
            r1 = r0
        L_0x0d7c:
            r23 = r14
            r3 = r21
            r13 = r30
            goto L_0x09ea
        L_0x0d84:
            r41 = r1
            r69 = r9
            r70 = r10
            r72 = r11
            r71 = r55
            r9 = r58
            r1 = 2500(0x9c4, double:1.235E-320)
            r55 = r3
            r58 = r13
            r12 = r4
        L_0x0d97:
            r3 = r26 ^ 1
            r7 = r94
            r5 = r3
            r11 = r30
            r74 = r61
            r10 = 1
            r3 = r92
        L_0x0da3:
            if (r5 != 0) goto L_0x0dc3
            if (r10 == 0) goto L_0x0da8
            goto L_0x0dc3
        L_0x0da8:
            r2 = r85
            r92 = r3
            r30 = r11
            r4 = r12
            r1 = r41
            r3 = r55
            r13 = r58
            r10 = r70
            r55 = r71
            r11 = r72
            r61 = r74
            r58 = r9
            r9 = r69
            goto L_0x0b7d
        L_0x0dc3:
            r78.checkConversionCanceled()     // Catch:{ Exception -> 0x1311, all -> 0x1302 }
            if (r99 == 0) goto L_0x0dcc
            r42 = 22000(0x55f0, double:1.08694E-319)
            r1 = r42
        L_0x0dcc:
            r76 = r5
            r5 = r88
            r88 = r76
            int r1 = r5.dequeueOutputBuffer(r9, r1)     // Catch:{ Exception -> 0x12fe, all -> 0x1302 }
            r2 = -1
            if (r1 != r2) goto L_0x0df1
            r44 = r3
            r94 = r6
            r92 = r7
            r4 = r53
            r7 = r60
            r3 = r63
            r6 = r64
            r10 = 0
        L_0x0de8:
            r76 = r14
            r14 = r8
            r8 = r23
            r23 = r76
            goto L_0x1007
        L_0x0df1:
            r2 = -3
            if (r1 != r2) goto L_0x0e27
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0e1c, all -> 0x0e0e }
            r94 = r6
            r6 = 21
            if (r2 >= r6) goto L_0x0e00
            java.nio.ByteBuffer[] r20 = r5.getOutputBuffers()     // Catch:{ Exception -> 0x0e1c, all -> 0x0e0e }
        L_0x0e00:
            r44 = r3
            r92 = r7
            r4 = r53
            r7 = r60
            r3 = r63
            r6 = r64
            r2 = -1
            goto L_0x0de8
        L_0x0e0e:
            r0 = move-exception
            r2 = r85
            r10 = r87
            r1 = r0
            r44 = r3
            r13 = r11
        L_0x0e17:
            r7 = r21
            r6 = 0
            goto L_0x07d5
        L_0x0e1c:
            r0 = move-exception
            r10 = r87
            r1 = r0
            r44 = r3
            r13 = r11
        L_0x0e23:
            r23 = r14
            goto L_0x131f
        L_0x0e27:
            r94 = r6
            r2 = -2
            if (r1 != r2) goto L_0x0e9f
            android.media.MediaFormat r2 = r5.getOutputFormat()     // Catch:{ Exception -> 0x0e1c, all -> 0x0e0e }
            r6 = -5
            if (r11 != r6) goto L_0x0e88
            if (r2 == 0) goto L_0x0e88
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x0e1c, all -> 0x0e0e }
            r92 = r10
            r10 = 0
            int r6 = r6.addTrack(r2, r10)     // Catch:{ Exception -> 0x0e1c, all -> 0x0e0e }
            r10 = r59
            boolean r11 = r2.containsKey(r10)     // Catch:{ Exception -> 0x0e7d, all -> 0x0e70 }
            if (r11 == 0) goto L_0x0e69
            int r11 = r2.getInteger(r10)     // Catch:{ Exception -> 0x0e7d, all -> 0x0e70 }
            r93 = r6
            r6 = 1
            if (r11 != r6) goto L_0x0e6b
            r6 = r64
            java.nio.ByteBuffer r7 = r2.getByteBuffer(r6)     // Catch:{ Exception -> 0x0e67, all -> 0x0e65 }
            java.lang.String r11 = "csd-1"
            java.nio.ByteBuffer r2 = r2.getByteBuffer(r11)     // Catch:{ Exception -> 0x0e67, all -> 0x0e65 }
            int r7 = r7.limit()     // Catch:{ Exception -> 0x0e67, all -> 0x0e65 }
            int r2 = r2.limit()     // Catch:{ Exception -> 0x0e67, all -> 0x0e65 }
            int r7 = r7 + r2
            goto L_0x0e6d
        L_0x0e65:
            r0 = move-exception
            goto L_0x0e73
        L_0x0e67:
            r0 = move-exception
            goto L_0x0e80
        L_0x0e69:
            r93 = r6
        L_0x0e6b:
            r6 = r64
        L_0x0e6d:
            r11 = r93
            goto L_0x0e8e
        L_0x0e70:
            r0 = move-exception
            r93 = r6
        L_0x0e73:
            r2 = r85
            r10 = r87
            r13 = r93
            r1 = r0
            r44 = r3
            goto L_0x0e17
        L_0x0e7d:
            r0 = move-exception
            r93 = r6
        L_0x0e80:
            r10 = r87
            r13 = r93
            r1 = r0
            r44 = r3
            goto L_0x0e23
        L_0x0e88:
            r92 = r10
            r10 = r59
            r6 = r64
        L_0x0e8e:
            r44 = r3
            r59 = r10
            r4 = r53
            r3 = r63
            r2 = -1
            r10 = r92
            r92 = r7
            r7 = r60
            goto L_0x0de8
        L_0x0e9f:
            r92 = r10
            r10 = r59
            r6 = r64
            if (r1 < 0) goto L_0x12d5
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x12fe, all -> 0x1302 }
            r59 = r10
            r10 = 21
            if (r2 >= r10) goto L_0x0eb2
            r2 = r20[r1]     // Catch:{ Exception -> 0x0e1c, all -> 0x0e0e }
            goto L_0x0eb6
        L_0x0eb2:
            java.nio.ByteBuffer r2 = r5.getOutputBuffer(r1)     // Catch:{ Exception -> 0x12fe, all -> 0x1302 }
        L_0x0eb6:
            if (r2 == 0) goto L_0x12b0
            int r10 = r9.size     // Catch:{ Exception -> 0x12fe, all -> 0x1302 }
            r23 = r14
            r14 = 1
            if (r10 <= r14) goto L_0x0fed
            int r14 = r9.flags     // Catch:{ Exception -> 0x0fe5, all -> 0x0fe0 }
            r30 = r14 & 2
            if (r30 != 0) goto L_0x0var_
            if (r7 == 0) goto L_0x0ee0
            r30 = r14 & 1
            if (r30 == 0) goto L_0x0ee0
            r44 = r3
            int r3 = r9.offset     // Catch:{ Exception -> 0x0ed6, all -> 0x1073 }
            int r3 = r3 + r7
            r9.offset = r3     // Catch:{ Exception -> 0x0ed6, all -> 0x1073 }
            int r10 = r10 - r7
            r9.size = r10     // Catch:{ Exception -> 0x0ed6, all -> 0x1073 }
            goto L_0x0ee2
        L_0x0ed6:
            r0 = move-exception
            r10 = r87
            r1 = r0
            r14 = r5
            r13 = r11
            r3 = r21
            goto L_0x147c
        L_0x0ee0:
            r44 = r3
        L_0x0ee2:
            if (r40 == 0) goto L_0x0var_
            r3 = r14 & 1
            if (r3 == 0) goto L_0x0var_
            int r3 = r9.size     // Catch:{ Exception -> 0x0ed6, all -> 0x1073 }
            r4 = 100
            if (r3 <= r4) goto L_0x0f2f
            int r3 = r9.offset     // Catch:{ Exception -> 0x0ed6, all -> 0x1073 }
            r2.position(r3)     // Catch:{ Exception -> 0x0ed6, all -> 0x1073 }
            byte[] r3 = new byte[r4]     // Catch:{ Exception -> 0x0ed6, all -> 0x1073 }
            r2.get(r3)     // Catch:{ Exception -> 0x0ed6, all -> 0x1073 }
            r10 = 0
            r14 = 0
        L_0x0efa:
            r4 = 96
            if (r10 >= r4) goto L_0x0f2f
            byte r4 = r3[r10]     // Catch:{ Exception -> 0x0ed6, all -> 0x1073 }
            if (r4 != 0) goto L_0x0var_
            int r4 = r10 + 1
            byte r4 = r3[r4]     // Catch:{ Exception -> 0x0ed6, all -> 0x1073 }
            if (r4 != 0) goto L_0x0var_
            int r4 = r10 + 2
            byte r4 = r3[r4]     // Catch:{ Exception -> 0x0ed6, all -> 0x1073 }
            if (r4 != 0) goto L_0x0var_
            int r4 = r10 + 3
            byte r4 = r3[r4]     // Catch:{ Exception -> 0x0ed6, all -> 0x1073 }
            r40 = r3
            r3 = 1
            if (r4 != r3) goto L_0x0var_
            int r14 = r14 + 1
            if (r14 <= r3) goto L_0x0var_
            int r3 = r9.offset     // Catch:{ Exception -> 0x0ed6, all -> 0x1073 }
            int r3 = r3 + r10
            r9.offset = r3     // Catch:{ Exception -> 0x0ed6, all -> 0x1073 }
            int r3 = r9.size     // Catch:{ Exception -> 0x0ed6, all -> 0x1073 }
            int r3 = r3 - r10
            r9.size = r3     // Catch:{ Exception -> 0x0ed6, all -> 0x1073 }
            goto L_0x0f2f
        L_0x0var_:
            r40 = r3
        L_0x0var_:
            int r10 = r10 + 1
            r3 = r40
            r4 = 100
            goto L_0x0efa
        L_0x0f2f:
            r40 = 0
        L_0x0var_:
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ Exception -> 0x0var_, all -> 0x1073 }
            r4 = 1
            long r2 = r3.writeSampleData(r11, r2, r9, r4)     // Catch:{ Exception -> 0x0var_, all -> 0x1073 }
            r31 = 0
            int r4 = (r2 > r31 ? 1 : (r2 == r31 ? 0 : -1))
            if (r4 == 0) goto L_0x0fef
            org.telegram.messenger.MediaController$VideoConvertorListener r4 = r15.callback     // Catch:{ Exception -> 0x0var_, all -> 0x1073 }
            if (r4 == 0) goto L_0x0fef
            r93 = r7
            r14 = r8
            long r7 = r9.presentationTimeUs     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            long r46 = r7 - r12
            int r10 = (r46 > r50 ? 1 : (r46 == r50 ? 0 : -1))
            if (r10 <= 0) goto L_0x0f4f
            long r50 = r7 - r12
        L_0x0f4f:
            r7 = r50
            float r10 = (float) r7     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            float r10 = r10 / r24
            float r10 = r10 / r25
            r4.didWriteData(r2, r10)     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            r50 = r7
        L_0x0f5b:
            r4 = r53
            r7 = r60
            r3 = r63
            goto L_0x0ff4
        L_0x0var_:
            r0 = move-exception
            goto L_0x0fe8
        L_0x0var_:
            r44 = r3
            r93 = r7
            r14 = r8
            r3 = -5
            if (r11 != r3) goto L_0x0f5b
            byte[] r3 = new byte[r10]     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            int r4 = r9.offset     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            int r4 = r4 + r10
            r2.limit(r4)     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            int r4 = r9.offset     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            r2.position(r4)     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            r2.get(r3)     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            int r2 = r9.size     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            r4 = 1
            int r2 = r2 - r4
        L_0x0var_:
            if (r2 < 0) goto L_0x0fbf
            r7 = 3
            if (r2 <= r7) goto L_0x0fbf
            byte r8 = r3[r2]     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            if (r8 != r4) goto L_0x0fbb
            int r8 = r2 + -1
            byte r8 = r3[r8]     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            if (r8 != 0) goto L_0x0fbb
            int r8 = r2 + -2
            byte r8 = r3[r8]     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            if (r8 != 0) goto L_0x0fbb
            int r8 = r2 + -3
            byte r10 = r3[r8]     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            if (r10 != 0) goto L_0x0fbb
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r8)     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            int r10 = r9.size     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            int r10 = r10 - r8
            java.nio.ByteBuffer r10 = java.nio.ByteBuffer.allocate(r10)     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            r4 = 0
            java.nio.ByteBuffer r7 = r2.put(r3, r4, r8)     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            r7.position(r4)     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            int r7 = r9.size     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            int r7 = r7 - r8
            java.nio.ByteBuffer r3 = r10.put(r3, r8, r7)     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            r3.position(r4)     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            goto L_0x0fc1
        L_0x0fbb:
            int r2 = r2 + -1
            r4 = 1
            goto L_0x0var_
        L_0x0fbf:
            r2 = 0
            r10 = 0
        L_0x0fc1:
            r4 = r53
            r7 = r60
            r3 = r63
            android.media.MediaFormat r8 = android.media.MediaFormat.createVideoFormat(r7, r3, r4)     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            if (r2 == 0) goto L_0x0fd7
            if (r10 == 0) goto L_0x0fd7
            r8.setByteBuffer(r6, r2)     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            java.lang.String r2 = "csd-1"
            r8.setByteBuffer(r2, r10)     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
        L_0x0fd7:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            r10 = 0
            int r2 = r2.addTrack(r8, r10)     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            r11 = r2
            goto L_0x0ff4
        L_0x0fe0:
            r0 = move-exception
            r44 = r3
            goto L_0x1074
        L_0x0fe5:
            r0 = move-exception
            r44 = r3
        L_0x0fe8:
            r14 = r8
            r10 = r87
            goto L_0x131d
        L_0x0fed:
            r44 = r3
        L_0x0fef:
            r93 = r7
            r14 = r8
            goto L_0x0f5b
        L_0x0ff4:
            int r2 = r9.flags     // Catch:{ Exception -> 0x12a9, all -> 0x12a4 }
            r2 = r2 & 4
            if (r2 == 0) goto L_0x0ffc
            r2 = 1
            goto L_0x0ffd
        L_0x0ffc:
            r2 = 0
        L_0x0ffd:
            r8 = 0
            r5.releaseOutputBuffer(r1, r8)     // Catch:{ Exception -> 0x12a9, all -> 0x12a4 }
            r10 = r92
            r92 = r93
            r8 = r2
            r2 = -1
        L_0x1007:
            if (r1 == r2) goto L_0x1028
            r63 = r3
            r53 = r4
            r64 = r6
            r60 = r7
            r3 = r44
            r1 = 2500(0x9c4, double:1.235E-320)
            r7 = r92
            r6 = r94
            r76 = r5
            r5 = r88
            r88 = r76
            r77 = r23
            r23 = r8
            r8 = r14
            r14 = r77
            goto L_0x0da3
        L_0x1028:
            if (r26 != 0) goto L_0x126c
            r63 = r3
            r2 = 2500(0x9c4, double:1.235E-320)
            int r1 = r14.dequeueOutputBuffer(r9, r2)     // Catch:{ Exception -> 0x1260, all -> 0x1252 }
            r2 = -1
            if (r1 != r2) goto L_0x104d
            r53 = r4
            r64 = r6
            r60 = r7
            r34 = r8
            r3 = r9
            r95 = r10
            r93 = r11
            r4 = r54
            r1 = 0
            r8 = 0
            r22 = -5
            r10 = r87
            goto L_0x1289
        L_0x104d:
            r3 = -3
            if (r1 != r3) goto L_0x1052
            goto L_0x126e
        L_0x1052:
            r3 = -2
            if (r1 != r3) goto L_0x1082
            android.media.MediaFormat r1 = r14.getOutputFormat()     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            if (r3 == 0) goto L_0x126e
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            r3.<init>()     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            java.lang.String r2 = "newFormat = "
            r3.append(r2)     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            r3.append(r1)     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            java.lang.String r1 = r3.toString()     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            goto L_0x126e
        L_0x1073:
            r0 = move-exception
        L_0x1074:
            r2 = r85
            r3 = r86
            r10 = r87
            goto L_0x130b
        L_0x107c:
            r0 = move-exception
            r10 = r87
        L_0x107f:
            r1 = r0
            goto L_0x12fb
        L_0x1082:
            if (r1 < 0) goto L_0x122b
            int r2 = r9.size     // Catch:{ Exception -> 0x1260, all -> 0x1252 }
            if (r2 == 0) goto L_0x108b
            r93 = 1
            goto L_0x108d
        L_0x108b:
            r93 = 0
        L_0x108d:
            long r2 = r9.presentationTimeUs     // Catch:{ Exception -> 0x1260, all -> 0x1252 }
            r31 = 0
            int r46 = (r72 > r31 ? 1 : (r72 == r31 ? 0 : -1))
            if (r46 <= 0) goto L_0x10a9
            int r46 = (r2 > r72 ? 1 : (r2 == r72 ? 0 : -1))
            if (r46 < 0) goto L_0x10a9
            r53 = r4
            int r4 = r9.flags     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            r4 = r4 | 4
            r9.flags = r4     // Catch:{ Exception -> 0x107c, all -> 0x1073 }
            r4 = 0
            r26 = 1
            r31 = 0
            r33 = 1
            goto L_0x10af
        L_0x10a9:
            r53 = r4
            r4 = r93
            r31 = 0
        L_0x10af:
            int r46 = (r44 > r31 ? 1 : (r44 == r31 ? 0 : -1))
            if (r46 < 0) goto L_0x113b
            r93 = r4
            int r4 = r9.flags     // Catch:{ Exception -> 0x107c, all -> 0x1135 }
            r4 = r4 & 4
            if (r4 == 0) goto L_0x113d
            long r46 = r44 - r12
            long r46 = java.lang.Math.abs(r46)     // Catch:{ Exception -> 0x107c, all -> 0x1135 }
            r4 = 1000000(0xvar_, float:1.401298E-39)
            r95 = r10
            r10 = r87
            int r4 = r4 / r10
            r64 = r6
            r60 = r7
            long r6 = (long) r4
            int r4 = (r46 > r6 ? 1 : (r46 == r6 ? 0 : -1))
            if (r4 <= 0) goto L_0x1128
            r6 = 0
            int r4 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r4 <= 0) goto L_0x10e4
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x1132, all -> 0x12f5 }
            r6 = 0
            r4.seekTo(r12, r6)     // Catch:{ Exception -> 0x1132, all -> 0x12f5 }
            r34 = r8
            r6 = r74
            r8 = 0
            goto L_0x10f0
        L_0x10e4:
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x1132, all -> 0x1130 }
            r34 = r8
            r6 = 0
            r8 = 0
            r4.seekTo(r6, r8)     // Catch:{ Exception -> 0x1132, all -> 0x12f5 }
            r6 = r74
        L_0x10f0:
            long r56 = r6 + r35
            int r4 = r9.flags     // Catch:{ Exception -> 0x111b, all -> 0x110b }
            r22 = -5
            r4 = r4 & -5
            r9.flags = r4     // Catch:{ Exception -> 0x111b, all -> 0x110b }
            r14.flush()     // Catch:{ Exception -> 0x111b, all -> 0x110b }
            r72 = r44
            r4 = 0
            r26 = 0
            r31 = 0
            r33 = 0
            r46 = 1
            r44 = r18
            goto L_0x1152
        L_0x110b:
            r0 = move-exception
            r2 = r85
            r3 = r86
            r1 = r0
            r13 = r11
            r7 = r21
            r72 = r44
            r6 = 0
            r44 = r18
            goto L_0x1581
        L_0x111b:
            r0 = move-exception
            r1 = r0
            r13 = r11
            r8 = r14
            r3 = r21
            r72 = r44
            r14 = r5
            r44 = r18
            goto L_0x147c
        L_0x1128:
            r34 = r8
            r6 = r74
            r8 = 0
            r22 = -5
            goto L_0x114c
        L_0x1130:
            r0 = move-exception
            goto L_0x1138
        L_0x1132:
            r0 = move-exception
            goto L_0x107f
        L_0x1135:
            r0 = move-exception
            r10 = r87
        L_0x1138:
            r8 = 0
            goto L_0x1307
        L_0x113b:
            r93 = r4
        L_0x113d:
            r64 = r6
            r60 = r7
            r34 = r8
            r95 = r10
            r6 = r74
            r8 = 0
            r22 = -5
            r10 = r87
        L_0x114c:
            r4 = r93
            r31 = 0
            r46 = 0
        L_0x1152:
            int r47 = (r65 > r31 ? 1 : (r65 == r31 ? 0 : -1))
            if (r47 <= 0) goto L_0x116b
            r93 = r11
            long r11 = r9.presentationTimeUs     // Catch:{ Exception -> 0x1168, all -> 0x1248 }
            long r11 = r11 - r65
            int r13 = (r11 > r37 ? 1 : (r11 == r37 ? 0 : -1))
            if (r13 >= 0) goto L_0x116d
            int r11 = r9.flags     // Catch:{ Exception -> 0x1168, all -> 0x1248 }
            r11 = r11 & 4
            if (r11 != 0) goto L_0x116d
            r4 = 0
            goto L_0x116d
        L_0x1168:
            r0 = move-exception
            goto L_0x1267
        L_0x116b:
            r93 = r11
        L_0x116d:
            r11 = 0
            int r13 = (r44 > r11 ? 1 : (r44 == r11 ? 0 : -1))
            if (r13 < 0) goto L_0x1177
            r13 = r9
            r8 = r44
            goto L_0x117a
        L_0x1177:
            r13 = r9
            r8 = r90
        L_0x117a:
            int r48 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r48 <= 0) goto L_0x11bc
            int r11 = (r67 > r18 ? 1 : (r67 == r18 ? 0 : -1))
            if (r11 != 0) goto L_0x11bc
            int r11 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r11 >= 0) goto L_0x11ad
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1168, all -> 0x1248 }
            if (r2 == 0) goto L_0x11aa
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1168, all -> 0x1248 }
            r2.<init>()     // Catch:{ Exception -> 0x1168, all -> 0x1248 }
            java.lang.String r3 = "drop frame startTime = "
            r2.append(r3)     // Catch:{ Exception -> 0x1168, all -> 0x1248 }
            r2.append(r8)     // Catch:{ Exception -> 0x1168, all -> 0x1248 }
            java.lang.String r3 = " present time = "
            r2.append(r3)     // Catch:{ Exception -> 0x1168, all -> 0x1248 }
            r3 = r13
            long r8 = r3.presentationTimeUs     // Catch:{ Exception -> 0x1168, all -> 0x1248 }
            r2.append(r8)     // Catch:{ Exception -> 0x1168, all -> 0x1248 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x1168, all -> 0x1248 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x1168, all -> 0x1248 }
            goto L_0x11ab
        L_0x11aa:
            r3 = r13
        L_0x11ab:
            r4 = 0
            goto L_0x11bd
        L_0x11ad:
            r3 = r13
            long r8 = r3.presentationTimeUs     // Catch:{ Exception -> 0x1168, all -> 0x1248 }
            r11 = -2147483648(0xfffffffvar_, double:NaN)
            int r2 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r2 == 0) goto L_0x11b9
            long r56 = r56 - r8
        L_0x11b9:
            r67 = r8
            goto L_0x11bd
        L_0x11bc:
            r3 = r13
        L_0x11bd:
            if (r46 == 0) goto L_0x11c2
            r67 = r18
            goto L_0x11d5
        L_0x11c2:
            int r2 = (r44 > r18 ? 1 : (r44 == r18 ? 0 : -1))
            if (r2 != 0) goto L_0x11d2
            r8 = 0
            int r2 = (r56 > r8 ? 1 : (r56 == r8 ? 0 : -1))
            if (r2 == 0) goto L_0x11d2
            long r8 = r3.presentationTimeUs     // Catch:{ Exception -> 0x1168, all -> 0x1248 }
            long r8 = r8 + r56
            r3.presentationTimeUs = r8     // Catch:{ Exception -> 0x1168, all -> 0x1248 }
        L_0x11d2:
            r14.releaseOutputBuffer(r1, r4)     // Catch:{ Exception -> 0x1229, all -> 0x1248 }
        L_0x11d5:
            if (r4 == 0) goto L_0x120a
            long r1 = r3.presentationTimeUs     // Catch:{ Exception -> 0x1229, all -> 0x1248 }
            r8 = 0
            int r4 = (r44 > r8 ? 1 : (r44 == r8 ? 0 : -1))
            if (r4 < 0) goto L_0x11e4
            long r74 = java.lang.Math.max(r6, r1)     // Catch:{ Exception -> 0x1168, all -> 0x1248 }
            goto L_0x11e6
        L_0x11e4:
            r74 = r6
        L_0x11e6:
            r23.awaitNewImage()     // Catch:{ Exception -> 0x11eb, all -> 0x1248 }
            r13 = 0
            goto L_0x11f1
        L_0x11eb:
            r0 = move-exception
            r4 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ Exception -> 0x1229, all -> 0x1248 }
            r13 = 1
        L_0x11f1:
            if (r13 != 0) goto L_0x1205
            r23.drawImage()     // Catch:{ Exception -> 0x1229, all -> 0x1248 }
            long r6 = r3.presentationTimeUs     // Catch:{ Exception -> 0x1229, all -> 0x1248 }
            r11 = 1000(0x3e8, double:4.94E-321)
            long r6 = r6 * r11
            r4 = r54
            r4.setPresentationTime(r6)     // Catch:{ Exception -> 0x124a, all -> 0x1248 }
            r4.swapBuffers()     // Catch:{ Exception -> 0x124a, all -> 0x1248 }
            goto L_0x1207
        L_0x1205:
            r4 = r54
        L_0x1207:
            r65 = r1
            goto L_0x1210
        L_0x120a:
            r4 = r54
            r8 = 0
            r74 = r6
        L_0x1210:
            int r1 = r3.flags     // Catch:{ Exception -> 0x124a, all -> 0x1248 }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x1225
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x124a, all -> 0x1248 }
            if (r1 == 0) goto L_0x121f
            java.lang.String r1 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x124a, all -> 0x1248 }
        L_0x121f:
            r5.signalEndOfInputStream()     // Catch:{ Exception -> 0x124a, all -> 0x1248 }
            r1 = 0
            goto L_0x1289
        L_0x1225:
            r1 = r88
            goto L_0x1289
        L_0x1229:
            r0 = move-exception
            goto L_0x1265
        L_0x122b:
            r10 = r87
            r93 = r11
            r4 = r54
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x124a, all -> 0x1248 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x124a, all -> 0x1248 }
            r3.<init>()     // Catch:{ Exception -> 0x124a, all -> 0x1248 }
            java.lang.String r6 = "unexpected result from decoder.dequeueOutputBuffer: "
            r3.append(r6)     // Catch:{ Exception -> 0x124a, all -> 0x1248 }
            r3.append(r1)     // Catch:{ Exception -> 0x124a, all -> 0x1248 }
            java.lang.String r1 = r3.toString()     // Catch:{ Exception -> 0x124a, all -> 0x1248 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x124a, all -> 0x1248 }
            throw r2     // Catch:{ Exception -> 0x124a, all -> 0x1248 }
        L_0x1248:
            r0 = move-exception
            goto L_0x1257
        L_0x124a:
            r0 = move-exception
            r13 = r93
            r1 = r0
            r54 = r4
            goto L_0x12fc
        L_0x1252:
            r0 = move-exception
            r10 = r87
            r93 = r11
        L_0x1257:
            r2 = r85
            r3 = r86
            r13 = r93
            r1 = r0
            goto L_0x130d
        L_0x1260:
            r0 = move-exception
            r10 = r87
            r93 = r11
        L_0x1265:
            r4 = r54
        L_0x1267:
            r13 = r93
            r1 = r0
            goto L_0x12fc
        L_0x126c:
            r63 = r3
        L_0x126e:
            r53 = r4
            r64 = r6
            r60 = r7
            r34 = r8
            r3 = r9
            r95 = r10
            r93 = r11
            r4 = r54
            r6 = r74
            r8 = 0
            r22 = -5
            r10 = r87
            r1 = r88
            r74 = r6
        L_0x1289:
            r12 = r90
            r7 = r92
            r11 = r93
            r6 = r94
            r10 = r95
            r9 = r3
            r54 = r4
            r88 = r5
            r8 = r14
            r14 = r23
            r23 = r34
            r3 = r44
            r5 = r1
            r1 = 2500(0x9c4, double:1.235E-320)
            goto L_0x0da3
        L_0x12a4:
            r0 = move-exception
            r10 = r87
            goto L_0x1307
        L_0x12a9:
            r0 = move-exception
            r10 = r87
            r4 = r54
            goto L_0x107f
        L_0x12b0:
            r10 = r87
            r44 = r3
            r23 = r14
            r4 = r54
            r14 = r8
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x12f7, all -> 0x12f5 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x12f7, all -> 0x12f5 }
            r3.<init>()     // Catch:{ Exception -> 0x12f7, all -> 0x12f5 }
            java.lang.String r6 = "encoderOutputBuffer "
            r3.append(r6)     // Catch:{ Exception -> 0x12f7, all -> 0x12f5 }
            r3.append(r1)     // Catch:{ Exception -> 0x12f7, all -> 0x12f5 }
            java.lang.String r1 = " was null"
            r3.append(r1)     // Catch:{ Exception -> 0x12f7, all -> 0x12f5 }
            java.lang.String r1 = r3.toString()     // Catch:{ Exception -> 0x12f7, all -> 0x12f5 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x12f7, all -> 0x12f5 }
            throw r2     // Catch:{ Exception -> 0x12f7, all -> 0x12f5 }
        L_0x12d5:
            r10 = r87
            r44 = r3
            r23 = r14
            r4 = r54
            r14 = r8
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x12f7, all -> 0x12f5 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x12f7, all -> 0x12f5 }
            r3.<init>()     // Catch:{ Exception -> 0x12f7, all -> 0x12f5 }
            java.lang.String r6 = "unexpected result from encoder.dequeueOutputBuffer: "
            r3.append(r6)     // Catch:{ Exception -> 0x12f7, all -> 0x12f5 }
            r3.append(r1)     // Catch:{ Exception -> 0x12f7, all -> 0x12f5 }
            java.lang.String r1 = r3.toString()     // Catch:{ Exception -> 0x12f7, all -> 0x12f5 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x12f7, all -> 0x12f5 }
            throw r2     // Catch:{ Exception -> 0x12f7, all -> 0x12f5 }
        L_0x12f5:
            r0 = move-exception
            goto L_0x1307
        L_0x12f7:
            r0 = move-exception
            r1 = r0
            r54 = r4
        L_0x12fb:
            r13 = r11
        L_0x12fc:
            r8 = r14
            goto L_0x131f
        L_0x12fe:
            r0 = move-exception
            r10 = r87
            goto L_0x1316
        L_0x1302:
            r0 = move-exception
            r10 = r87
            r44 = r3
        L_0x1307:
            r2 = r85
            r3 = r86
        L_0x130b:
            r1 = r0
            r13 = r11
        L_0x130d:
            r7 = r21
            goto L_0x14ff
        L_0x1311:
            r0 = move-exception
            r10 = r87
            r5 = r88
        L_0x1316:
            r44 = r3
            r23 = r14
            r4 = r54
            r14 = r8
        L_0x131d:
            r1 = r0
            r13 = r11
        L_0x131f:
            r3 = r21
            goto L_0x13c9
        L_0x1323:
            r0 = move-exception
            r10 = r87
            r72 = r11
            r2 = r85
            r3 = r86
        L_0x132c:
            r44 = r92
            r1 = r0
        L_0x132f:
            r7 = r21
            r13 = r30
            goto L_0x14ff
        L_0x1335:
            r0 = move-exception
            r10 = r87
            r5 = r88
            r69 = r9
            r72 = r11
            r23 = r14
            r4 = r54
            r71 = r55
            r14 = r8
            r44 = r92
            r1 = r0
            r3 = r21
            r13 = r30
            goto L_0x13c9
        L_0x134e:
            r0 = move-exception
            r10 = r87
            r5 = r88
            r69 = r9
            r23 = r14
            r4 = r54
            r71 = r55
            r22 = -5
            r14 = r8
            r72 = r92
            r44 = r94
            r1 = r0
            r3 = r21
            r13 = -5
            goto L_0x13c9
        L_0x1368:
            r0 = move-exception
            r10 = r87
            r5 = r88
            r23 = r14
            r4 = r54
            r71 = r55
            r22 = -5
            r14 = r8
            r72 = r92
            r44 = r94
            r1 = r0
            goto L_0x1391
        L_0x137c:
            r0 = move-exception
            r10 = r87
            r5 = r88
            r23 = r14
            r14 = r52
            r4 = r54
            r71 = r55
            r22 = -5
            r72 = r92
            r44 = r94
            r1 = r0
            r8 = r14
        L_0x1391:
            r3 = r21
            r13 = -5
            goto L_0x13c7
        L_0x1395:
            r0 = move-exception
            r10 = r87
            r22 = -5
            goto L_0x1404
        L_0x139c:
            r0 = move-exception
            r10 = r87
            r5 = r88
            r14 = r52
            r4 = r54
            r71 = r55
            r22 = -5
            r72 = r92
            r44 = r94
            r1 = r0
            goto L_0x13c1
        L_0x13af:
            r0 = move-exception
            r10 = r9
            r94 = r14
            r71 = r30
            r22 = -5
            r15 = r78
            r14 = r2
            r72 = r92
            r44 = r94
            r1 = r0
            r54 = r4
        L_0x13c1:
            r8 = r14
            r3 = r21
            r13 = -5
            r23 = 0
        L_0x13c7:
            r69 = 0
        L_0x13c9:
            r14 = r5
            goto L_0x147c
        L_0x13cc:
            r0 = move-exception
            r10 = r9
            r94 = r14
            r71 = r30
            r22 = -5
            r15 = r78
            r72 = r92
            r44 = r94
            r1 = r0
            r54 = r4
            r14 = r5
            r3 = r21
            r8 = 0
            r13 = -5
            r23 = 0
            goto L_0x147a
        L_0x13e6:
            r0 = move-exception
            r10 = r9
            r94 = r14
            r71 = r30
            r22 = -5
            r15 = r78
            r72 = r92
            r44 = r94
            r1 = r0
            r14 = r5
            r3 = r21
            r8 = 0
            r13 = -5
            goto L_0x1476
        L_0x13fc:
            r0 = move-exception
            r22 = -5
            r10 = r9
            r94 = r14
            r15 = r78
        L_0x1404:
            r2 = r85
            r3 = r86
        L_0x1408:
            r72 = r92
            r44 = r94
            r1 = r0
        L_0x140d:
            r7 = r21
            goto L_0x157f
        L_0x1411:
            r0 = move-exception
            r10 = r9
            r94 = r14
            r71 = r30
            r22 = -5
            r15 = r78
            r72 = r92
            r44 = r94
            r1 = r0
            r3 = r21
            goto L_0x1473
        L_0x1424:
            r0 = move-exception
            r22 = -5
            r10 = r9
            r94 = r14
            r15 = r78
            goto L_0x143f
        L_0x142d:
            r0 = move-exception
            r10 = r9
            r94 = r14
            r71 = r30
            r22 = -5
            r15 = r78
            goto L_0x146e
        L_0x1438:
            r0 = move-exception
            r10 = r9
            r94 = r14
            r22 = -5
            r15 = r13
        L_0x143f:
            r2 = r85
            r72 = r92
            r44 = r94
            r1 = r0
            r7 = r3
            goto L_0x07d3
        L_0x1449:
            r0 = move-exception
            r10 = r9
            r94 = r14
            r71 = r30
            r22 = -5
            r15 = r13
            goto L_0x146e
        L_0x1453:
            r0 = move-exception
            r10 = r9
            r15 = r13
            r22 = -5
            r2 = r85
            r3 = r86
            r7 = r88
            r72 = r92
            r44 = r94
            r1 = r0
            goto L_0x157f
        L_0x1465:
            r0 = move-exception
            r71 = r6
            r10 = r9
            r15 = r13
            r22 = -5
            r3 = r88
        L_0x146e:
            r72 = r92
            r44 = r94
            r1 = r0
        L_0x1473:
            r8 = 0
            r13 = -5
            r14 = 0
        L_0x1476:
            r23 = 0
            r54 = 0
        L_0x147a:
            r69 = 0
        L_0x147c:
            boolean r2 = r1 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x14f6 }
            if (r2 == 0) goto L_0x1485
            if (r99 != 0) goto L_0x1485
            r47 = 1
            goto L_0x1487
        L_0x1485:
            r47 = 0
        L_0x1487:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x14ec }
            r2.<init>()     // Catch:{ all -> 0x14ec }
            java.lang.String r4 = "bitrate: "
            r2.append(r4)     // Catch:{ all -> 0x14ec }
            r2.append(r3)     // Catch:{ all -> 0x14ec }
            java.lang.String r4 = " framerate: "
            r2.append(r4)     // Catch:{ all -> 0x14ec }
            r2.append(r10)     // Catch:{ all -> 0x14ec }
            java.lang.String r4 = " size: "
            r2.append(r4)     // Catch:{ all -> 0x14ec }
            r4 = r86
            r2.append(r4)     // Catch:{ all -> 0x14e8 }
            java.lang.String r5 = "x"
            r2.append(r5)     // Catch:{ all -> 0x14e8 }
            r5 = r85
            r2.append(r5)     // Catch:{ all -> 0x14e6 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x14e6 }
            org.telegram.messenger.FileLog.e((java.lang.String) r2)     // Catch:{ all -> 0x14e6 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x14e6 }
            r21 = r3
            r2 = r8
            r30 = r13
            r6 = r47
            r13 = 1
        L_0x14c3:
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ all -> 0x14dc }
            r3 = r71
            r1.unselectTrack(r3)     // Catch:{ all -> 0x14dc }
            if (r2 == 0) goto L_0x14d2
            r2.stop()     // Catch:{ all -> 0x14dc }
            r2.release()     // Catch:{ all -> 0x14dc }
        L_0x14d2:
            r47 = r6
            r6 = r13
            r29 = r14
            r14 = r23
            r13 = r30
            goto L_0x151b
        L_0x14dc:
            r0 = move-exception
            r1 = r0
            r3 = r4
            r2 = r5
            r7 = r21
            r13 = r30
            goto L_0x1581
        L_0x14e6:
            r0 = move-exception
            goto L_0x14f1
        L_0x14e8:
            r0 = move-exception
            r5 = r85
            goto L_0x14f1
        L_0x14ec:
            r0 = move-exception
            r5 = r85
            r4 = r86
        L_0x14f1:
            r1 = r0
            r7 = r3
            r3 = r4
            r2 = r5
            goto L_0x1527
        L_0x14f6:
            r0 = move-exception
            r5 = r85
            r4 = r86
            r1 = r0
            r7 = r3
            r3 = r4
            r2 = r5
        L_0x14ff:
            r6 = 0
            goto L_0x1581
        L_0x1502:
            r5 = r85
            r10 = r87
            r4 = r15
            r22 = -5
            r15 = r13
            r21 = r88
            r72 = r92
            r44 = r94
            r6 = 0
            r13 = -5
            r14 = 0
            r29 = 0
            r47 = 0
            r54 = 0
            r69 = 0
        L_0x151b:
            if (r14 == 0) goto L_0x152b
            r14.release()     // Catch:{ all -> 0x1521 }
            goto L_0x152b
        L_0x1521:
            r0 = move-exception
            r1 = r0
            r3 = r4
            r2 = r5
            r7 = r21
        L_0x1527:
            r6 = r47
            goto L_0x1581
        L_0x152b:
            if (r54 == 0) goto L_0x1530
            r54.release()     // Catch:{ all -> 0x1521 }
        L_0x1530:
            if (r29 == 0) goto L_0x1538
            r29.stop()     // Catch:{ all -> 0x1521 }
            r29.release()     // Catch:{ all -> 0x1521 }
        L_0x1538:
            if (r69 == 0) goto L_0x153d
            r69.release()     // Catch:{ all -> 0x1521 }
        L_0x153d:
            r78.checkConversionCanceled()     // Catch:{ all -> 0x1521 }
            r34 = r6
            r6 = r47
        L_0x1544:
            android.media.MediaExtractor r1 = r15.extractor
            if (r1 == 0) goto L_0x154b
            r1.release()
        L_0x154b:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer
            if (r1 == 0) goto L_0x1560
            r1.finishMovie()     // Catch:{ all -> 0x155b }
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ all -> 0x155b }
            long r1 = r1.getLastFrameTimestamp(r13)     // Catch:{ all -> 0x155b }
            r15.endPresentationTime = r1     // Catch:{ all -> 0x155b }
            goto L_0x1560
        L_0x155b:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1560:
            r9 = r4
            r8 = r5
            r11 = r21
            r13 = r34
            goto L_0x15d1
        L_0x1568:
            r0 = move-exception
            r5 = r9
            r4 = r10
            r10 = r15
            r22 = -5
            r15 = r13
            goto L_0x1576
        L_0x1570:
            r0 = move-exception
            r5 = r9
            r4 = r10
            r22 = -5
            r10 = r8
        L_0x1576:
            r7 = r88
            r72 = r92
            r44 = r94
            r1 = r0
            r3 = r4
            r2 = r5
        L_0x157f:
            r6 = 0
        L_0x1580:
            r13 = -5
        L_0x1581:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x1643 }
            r4.<init>()     // Catch:{ all -> 0x1643 }
            java.lang.String r5 = "bitrate: "
            r4.append(r5)     // Catch:{ all -> 0x1643 }
            r4.append(r7)     // Catch:{ all -> 0x1643 }
            java.lang.String r5 = " framerate: "
            r4.append(r5)     // Catch:{ all -> 0x1643 }
            r4.append(r10)     // Catch:{ all -> 0x1643 }
            java.lang.String r5 = " size: "
            r4.append(r5)     // Catch:{ all -> 0x1643 }
            r4.append(r3)     // Catch:{ all -> 0x1643 }
            java.lang.String r5 = "x"
            r4.append(r5)     // Catch:{ all -> 0x1643 }
            r4.append(r2)     // Catch:{ all -> 0x1643 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x1643 }
            org.telegram.messenger.FileLog.e((java.lang.String) r4)     // Catch:{ all -> 0x1643 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x1643 }
            android.media.MediaExtractor r1 = r15.extractor
            if (r1 == 0) goto L_0x15b8
            r1.release()
        L_0x15b8:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer
            if (r1 == 0) goto L_0x15cd
            r1.finishMovie()     // Catch:{ all -> 0x15c8 }
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ all -> 0x15c8 }
            long r4 = r1.getLastFrameTimestamp(r13)     // Catch:{ all -> 0x15c8 }
            r15.endPresentationTime = r4     // Catch:{ all -> 0x15c8 }
            goto L_0x15cd
        L_0x15c8:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x15cd:
            r8 = r2
            r9 = r3
            r11 = r7
            r13 = 1
        L_0x15d1:
            if (r6 == 0) goto L_0x1602
            r22 = 1
            r1 = r78
            r2 = r79
            r3 = r80
            r4 = r81
            r5 = r82
            r6 = r83
            r7 = r84
            r10 = r87
            r12 = r89
            r13 = r90
            r15 = r72
            r17 = r44
            r19 = r96
            r21 = r98
            r23 = r100
            r24 = r101
            r25 = r102
            r26 = r103
            r27 = r104
            r28 = r105
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r15, r17, r19, r21, r22, r23, r24, r25, r26, r27, r28)
            return r1
        L_0x1602:
            long r1 = java.lang.System.currentTimeMillis()
            long r1 = r1 - r16
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x1642
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "compression completed time="
            r3.append(r4)
            r3.append(r1)
            java.lang.String r1 = " needCompress="
            r3.append(r1)
            r1 = r98
            r3.append(r1)
            java.lang.String r1 = " w="
            r3.append(r1)
            r3.append(r8)
            java.lang.String r1 = " h="
            r3.append(r1)
            r3.append(r9)
            java.lang.String r1 = " bitrate="
            r3.append(r1)
            r3.append(r11)
            java.lang.String r1 = r3.toString()
            org.telegram.messenger.FileLog.d(r1)
        L_0x1642:
            return r13
        L_0x1643:
            r0 = move-exception
            r1 = r78
            r2 = r0
            android.media.MediaExtractor r3 = r1.extractor
            if (r3 == 0) goto L_0x164e
            r3.release()
        L_0x164e:
            org.telegram.messenger.video.MP4Builder r3 = r1.mediaMuxer
            if (r3 == 0) goto L_0x1663
            r3.finishMovie()     // Catch:{ all -> 0x165e }
            org.telegram.messenger.video.MP4Builder r3 = r1.mediaMuxer     // Catch:{ all -> 0x165e }
            long r3 = r3.getLastFrameTimestamp(r13)     // Catch:{ all -> 0x165e }
            r1.endPresentationTime = r3     // Catch:{ all -> 0x165e }
            goto L_0x1663
        L_0x165e:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x1663:
            goto L_0x1665
        L_0x1664:
            throw r2
        L_0x1665:
            goto L_0x1664
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, int, int, int, long, long, long, long, boolean, boolean, org.telegram.messenger.MediaController$SavedFilterState, java.lang.String, java.util.ArrayList, boolean, org.telegram.messenger.MediaController$CropState, boolean):boolean");
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

    private static String createFragmentShader(int i, int i2, int i3, int i4, boolean z) {
        int clamp = (int) Utilities.clamp((((float) Math.max(i, i2)) / ((float) Math.max(i4, i3))) * 0.8f, 2.0f, 1.0f);
        FileLog.d("source size " + i + "x" + i2 + "    dest size " + i3 + i4 + "   kernelRadius " + clamp);
        if (z) {
            return "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nconst float kernel = " + clamp + ".0;\nconst float pixelSizeX = 1.0 / " + i + ".0;\nconst float pixelSizeY = 1.0 / " + i2 + ".0;\nuniform samplerExternalOES sTexture;\nvoid main() {\nvec3 accumulation = vec3(0);\nvec3 weightsum = vec3(0);\nfor (float x = -kernel; x <= kernel; x++){\n   for (float y = -kernel; y <= kernel; y++){\n       accumulation += texture2D(sTexture, vTextureCoord + vec2(x * pixelSizeX, y * pixelSizeY)).xyz;\n       weightsum += 1.0;\n   }\n}\ngl_FragColor = vec4(accumulation / weightsum, 1.0);\n}\n";
        }
        return "precision mediump float;\nvarying vec2 vTextureCoord;\nconst float kernel = " + clamp + ".0;\nconst float pixelSizeX = 1.0 / " + i2 + ".0;\nconst float pixelSizeY = 1.0 / " + i + ".0;\nuniform sampler2D sTexture;\nvoid main() {\nvec3 accumulation = vec3(0);\nvec3 weightsum = vec3(0);\nfor (float x = -kernel; x <= kernel; x++){\n   for (float y = -kernel; y <= kernel; y++){\n       accumulation += texture2D(sTexture, vTextureCoord + vec2(x * pixelSizeX, y * pixelSizeY)).xyz;\n       weightsum += 1.0;\n   }\n}\ngl_FragColor = vec4(accumulation / weightsum, 1.0);\n}\n";
    }

    public class ConversionCanceledException extends RuntimeException {
        public ConversionCanceledException() {
            super("canceled conversion");
        }
    }
}
