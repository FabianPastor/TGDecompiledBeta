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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v1, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v2, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v4, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v5, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v6, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v8, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v46, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v9, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v11, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v59, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v93, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v17, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v25, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v26, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v38, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v39, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v41, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v42, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v43, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v80, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v92, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v95, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v96, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r83v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v97, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v98, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v99, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v100, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v101, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v102, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v103, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v104, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v105, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v106, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v107, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v108, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v109, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v110, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v111, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v112, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v113, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v114, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v115, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v116, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r32v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v117, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v118, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v119, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v120, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v45, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v131, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v133, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v134, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v135, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v136, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v137, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v138, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v143, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v144, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v145, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v146, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v147, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v148, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v150, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v151, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v152, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v153, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v154, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v155, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v156, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v157, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v160, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v162, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v166, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v46, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v47, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v49, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v50, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v51, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v52, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v53, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v54, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v176, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v177, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v178, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v179, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v181, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v182, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v183, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v184, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v185, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v140, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v142, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v55, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v56, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v57, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v58, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v219, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v63, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v64, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v65, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v67, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v187, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v189, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v190, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v191, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v147, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v192, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v74, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v148, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v194, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v195, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v196, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v197, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v199, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r41v76, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v200, resolved type: long} */
    /* JADX WARNING: type inference failed for: r7v138 */
    /* JADX WARNING: Code restructure failed: missing block: B:1001:0x10d7, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1002:0x10d8, code lost:
        r4 = r41;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1003:0x10db, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1004:0x10dc, code lost:
        r4 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1009:0x1100, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1010:0x1101, code lost:
        r1 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1011:0x1102, code lost:
        r61 = r6;
        r7 = r54;
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1014:0x1126, code lost:
        r0 = th;
        r59 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1015:0x1128, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1016:0x1129, code lost:
        r42 = r83;
        r2 = r0;
        r3 = r1;
        r59 = r59;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1018:0x1130, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1019:0x1131, code lost:
        r8 = false;
        r14 = r69;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1020:0x1135, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1022:0x1137, code lost:
        r14 = r69;
        r59 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1023:0x1139, code lost:
        r12 = r74;
        r11 = r75;
        r2 = r0;
        r1 = r10;
        r59 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1039:0x1190, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1040:0x1191, code lost:
        r9 = r76;
        r11 = r77;
        r1 = r3;
        r14 = r15;
        r7 = r54;
        r8 = false;
        r59 = r81;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1041:0x119e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1042:0x119f, code lost:
        r9 = r76;
        r11 = r77;
        r14 = r15;
        r7 = r54;
        r1 = r59;
        r8 = false;
        r59 = r81;
        r2 = r0;
        r3 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1043:0x11ad, code lost:
        r6 = r11;
        r4 = r20;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1044:0x11b2, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1045:0x11b3, code lost:
        r9 = r76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1048:0x11c7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1049:0x11c8, code lost:
        r32 = r83;
        r7 = r1;
        r1 = r3;
        r11 = r5;
        r9 = r14;
        r14 = r15;
        r61 = r21;
        r8 = false;
        r59 = r81;
        r2 = r0;
        r54 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1055:0x11f6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1056:0x11f7, code lost:
        r32 = r83;
        r9 = r14;
        r14 = r15;
        r61 = r21;
        r8 = false;
        r59 = r81;
        r2 = r0;
        r6 = r5;
        r4 = r20;
        r1 = -5;
        r3 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1057:0x1209, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1058:0x120a, code lost:
        r32 = r83;
        r9 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1061:0x121a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1062:0x121b, code lost:
        r32 = r83;
        r9 = r14;
        r14 = r15;
        r61 = r21;
        r8 = false;
        r59 = r81;
        r2 = r0;
        r4 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1063:0x1228, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1064:0x1229, code lost:
        r32 = r83;
        r9 = r14;
        r14 = r15;
        r61 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1065:0x1230, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1066:0x1231, code lost:
        r32 = r83;
        r9 = r14;
        r14 = r15;
        r12 = r74;
        r11 = r75;
        r59 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:108:0x0282, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1136:0x1378, code lost:
        r2.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1140:?, code lost:
        r2.finishMovie();
        r14.endPresentationTime = r14.mediaMuxer.getLastFrameTimestamp(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1141:0x138b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1142:0x138c, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x0491, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x0498, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x04a7, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x04a8, code lost:
        r11 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x04c3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x04c4, code lost:
        r10 = r37;
        r2 = r0;
        r6 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x04ca, code lost:
        r11 = r49;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x04cf, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x04d0, code lost:
        r9 = r76;
        r59 = r81;
        r32 = r83;
        r2 = r0;
        r14 = r15;
        r4 = r48;
        r11 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x04f6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x04f7, code lost:
        r48 = r9;
        r49 = r11;
        r11 = r6;
        r2 = r0;
        r11 = r49;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x0501, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x0502, code lost:
        r48 = r9;
        r49 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x0507, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x0508, code lost:
        r48 = r9;
        r49 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x052c, code lost:
        r46 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x072c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x072d, code lost:
        r12 = r74;
        r11 = r75;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x0764, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x0765, code lost:
        r59 = r81;
        r32 = r83;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x0872, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x087f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x0880, code lost:
        r9 = r76;
        r6 = r77;
        r59 = r81;
        r2 = r0;
        r42 = null;
        r14 = r15;
        r4 = r20;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x08dd, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x097f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:540:0x0981, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x0a1e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x0a1f, code lost:
        r12 = r74;
        r1 = r81;
        r2 = r0;
        r59 = r10;
        r14 = r15;
        r4 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0aa5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0aa6, code lost:
        r1 = r81;
        r42 = r83;
        r2 = r0;
        r61 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x01af, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x01b0, code lost:
        r6 = r74;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x0b0c, code lost:
        if (r13.presentationTimeUs < r10) goto L_0x0b0e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0CLASSNAME, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0CLASSNAME, code lost:
        r8 = false;
        r14 = r69;
        r42 = r83;
        r2 = r0;
        r61 = r6;
        r59 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x01ce, code lost:
        r6 = r7;
        r7 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0d1f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:968:0x1041, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:0x1043, code lost:
        r59 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:971:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:985:0x1073, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:986:0x1074, code lost:
        r7 = r54;
        r59 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x1099, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:992:0x109a, code lost:
        r59 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:993:0x109d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:994:0x109e, code lost:
        r7 = r54;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1014:0x1126 A[ExcHandler: all (th java.lang.Throwable), PHI: r10 r14 r32 r59 
      PHI: (r10v24 int) = (r10v29 int), (r10v29 int), (r10v29 int), (r10v29 int), (r10v29 int), (r10v29 int), (r10v29 int), (r10v29 int), (r10v29 int), (r10v29 int), (r10v29 int), (r10v29 int), (r10v29 int), (r10v29 int), (r10v29 int), (r10v20 int), (r10v20 int), (r10v20 int), (r10v20 int), (r10v20 int), (r10v20 int), (r10v20 int), (r10v20 int), (r10v20 int), (r10v20 int), (r10v20 int), (r10v20 int), (r10v20 int), (r10v20 int) binds: [B:965:0x103c, B:975:0x1054, B:970:0x1043, B:966:?, B:962:0x1031, B:957:0x1026, B:958:?, B:938:0x0fdb, B:914:0x0f7b, B:915:?, B:908:0x0f6e, B:909:?, B:893:0x0f3b, B:894:?, B:876:0x0var_, B:1006:0x10e4, B:769:0x0d50, B:770:?, B:803:0x0dbe, B:804:?, B:806:0x0dc3, B:812:0x0dd3, B:813:?, B:818:0x0de6, B:777:0x0d5e, B:762:0x0d43, B:759:0x0d3e, B:760:?, B:723:0x0ca2] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r14v53 org.telegram.messenger.video.MediaCodecVideoConvertor) = (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v65 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v65 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v65 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v65 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v65 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v65 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v65 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v65 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v65 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v65 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v65 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v65 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v65 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v71 org.telegram.messenger.video.MediaCodecVideoConvertor) binds: [B:965:0x103c, B:975:0x1054, B:970:0x1043, B:966:?, B:962:0x1031, B:957:0x1026, B:958:?, B:938:0x0fdb, B:914:0x0f7b, B:915:?, B:908:0x0f6e, B:909:?, B:893:0x0f3b, B:894:?, B:876:0x0var_, B:1006:0x10e4, B:769:0x0d50, B:770:?, B:803:0x0dbe, B:804:?, B:806:0x0dc3, B:812:0x0dd3, B:813:?, B:818:0x0de6, B:777:0x0d5e, B:762:0x0d43, B:759:0x0d3e, B:760:?, B:723:0x0ca2] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r32v39 long) = (r32v45 long), (r32v45 long), (r32v45 long), (r32v45 long), (r32v45 long), (r32v45 long), (r32v45 long), (r32v45 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long), (r32v35 long) binds: [B:965:0x103c, B:975:0x1054, B:970:0x1043, B:966:?, B:962:0x1031, B:957:0x1026, B:958:?, B:938:0x0fdb, B:914:0x0f7b, B:915:?, B:908:0x0f6e, B:909:?, B:893:0x0f3b, B:894:?, B:876:0x0var_, B:1006:0x10e4, B:769:0x0d50, B:770:?, B:803:0x0dbe, B:804:?, B:806:0x0dc3, B:812:0x0dd3, B:813:?, B:818:0x0de6, B:777:0x0d5e, B:762:0x0d43, B:759:0x0d3e, B:760:?, B:723:0x0ca2] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r59v45 long) = (r59v131 long), (r59v133 long), (r59v134 long), (r59v135 long), (r59v136 long), (r59v137 long), (r59v138 long), (r59v143 long), (r59v144 long), (r59v145 long), (r59v146 long), (r59v147 long), (r59v148 long), (r59v150 long), (r59v151 long), (r59v152 long), (r59v153 long), (r59v154 long), (r59v155 long), (r59v156 long), (r59v157 long), (r59v176 long), (r59v177 long), (r59v178 long), (r59v179 long), (r59v181 long), (r59v182 long), (r59v183 long), (r59v185 long) binds: [B:914:0x0f7b, B:915:?, B:908:0x0f6e, B:909:?, B:893:0x0f3b, B:894:?, B:876:0x0var_, B:1006:0x10e4, B:769:0x0d50, B:770:?, B:803:0x0dbe, B:804:?, B:806:0x0dc3, B:812:0x0dd3, B:813:?, B:818:0x0de6, B:777:0x0d5e, B:762:0x0d43, B:759:0x0d3e, B:760:?, B:723:0x0ca2, B:965:0x103c, B:975:0x1054, B:970:0x1043, B:966:?, B:962:0x1031, B:957:0x1026, B:958:?, B:938:0x0fdb] A[DONT_GENERATE, DONT_INLINE], Splitter:B:957:0x1026] */
    /* JADX WARNING: Removed duplicated region for block: B:1020:0x1135 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:693:0x0CLASSNAME] */
    /* JADX WARNING: Removed duplicated region for block: B:1044:0x11b2 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:450:0x084b] */
    /* JADX WARNING: Removed duplicated region for block: B:1057:0x1209 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:440:0x07eb] */
    /* JADX WARNING: Removed duplicated region for block: B:1065:0x1230 A[ExcHandler: all (th java.lang.Throwable), PHI: r83 
      PHI: (r83v1 long) = (r83v3 long), (r83v3 long), (r83v3 long), (r83v3 long), (r83v21 long), (r83v21 long), (r83v21 long), (r83v21 long) binds: [B:414:0x0777, B:415:?, B:419:0x0797, B:420:?, B:395:0x073c, B:396:?, B:398:0x0745, B:399:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:395:0x073c] */
    /* JADX WARNING: Removed duplicated region for block: B:1092:0x12af A[Catch:{ all -> 0x12c0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1102:0x12db  */
    /* JADX WARNING: Removed duplicated region for block: B:1104:0x12f2 A[SYNTHETIC, Splitter:B:1104:0x12f2] */
    /* JADX WARNING: Removed duplicated region for block: B:1109:0x12fe A[Catch:{ all -> 0x12f6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1111:0x1303 A[Catch:{ all -> 0x12f6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1113:0x130b A[Catch:{ all -> 0x12f6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1118:0x1318  */
    /* JADX WARNING: Removed duplicated region for block: B:1121:0x131f A[SYNTHETIC, Splitter:B:1121:0x131f] */
    /* JADX WARNING: Removed duplicated region for block: B:1136:0x1378  */
    /* JADX WARNING: Removed duplicated region for block: B:1139:0x137f A[SYNTHETIC, Splitter:B:1139:0x137f] */
    /* JADX WARNING: Removed duplicated region for block: B:1145:0x139a  */
    /* JADX WARNING: Removed duplicated region for block: B:1147:0x13c3 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x03db A[Catch:{ Exception -> 0x0457, all -> 0x0455 }] */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x03de A[Catch:{ Exception -> 0x0457, all -> 0x0455 }] */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x03eb  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x0401  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x0498 A[ExcHandler: all (th java.lang.Throwable), PHI: r1 r49 
      PHI: (r1v198 int) = (r1v193 int), (r1v199 int), (r1v199 int), (r1v199 int), (r1v199 int), (r1v199 int), (r1v199 int) binds: [B:68:0x01c1, B:78:0x01e9, B:235:0x045b, B:172:0x0355, B:129:0x02ad, B:115:0x0292, B:87:0x0209] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r49v18 int) = (r49v13 int), (r49v19 int), (r49v19 int), (r49v19 int), (r49v19 int), (r49v19 int), (r49v19 int) binds: [B:68:0x01c1, B:78:0x01e9, B:235:0x045b, B:172:0x0355, B:129:0x02ad, B:115:0x0292, B:87:0x0209] A[DONT_GENERATE, DONT_INLINE], Splitter:B:68:0x01c1] */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x04cf A[ExcHandler: all (r0v143 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:55:0x01a3] */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0501 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:41:0x010f] */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x052a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x056d A[SYNTHETIC, Splitter:B:291:0x056d] */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x057e A[Catch:{ all -> 0x0571 }] */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x0583 A[Catch:{ all -> 0x0571 }] */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x0661  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x072c A[ExcHandler: all (th java.lang.Throwable), PHI: r83 
      PHI: (r83v20 long) = (r83v3 long), (r83v3 long), (r83v3 long), (r83v3 long), (r83v21 long), (r83v21 long) binds: [B:423:0x07b9, B:417:0x077b, B:418:?, B:406:0x0758, B:388:0x071d, B:389:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:388:0x071d] */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0862 A[SYNTHETIC, Splitter:B:459:0x0862] */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x0872 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:459:0x0862] */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0890  */
    /* JADX WARNING: Removed duplicated region for block: B:470:0x0896 A[SYNTHETIC, Splitter:B:470:0x0896] */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x08c6  */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x08c9  */
    /* JADX WARNING: Removed duplicated region for block: B:539:0x097f A[ExcHandler: all (th java.lang.Throwable), Splitter:B:513:0x0931] */
    /* JADX WARNING: Removed duplicated region for block: B:545:0x098b  */
    /* JADX WARNING: Removed duplicated region for block: B:555:0x09be  */
    /* JADX WARNING: Removed duplicated region for block: B:557:0x09cd  */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x09d0  */
    /* JADX WARNING: Removed duplicated region for block: B:574:0x0a1e A[ExcHandler: all (r0v87 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:571:0x0a15] */
    /* JADX WARNING: Removed duplicated region for block: B:583:0x0a3f A[SYNTHETIC, Splitter:B:583:0x0a3f] */
    /* JADX WARNING: Removed duplicated region for block: B:670:0x0b97 A[Catch:{ Exception -> 0x0bba, all -> 0x0bb8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:671:0x0b9b A[Catch:{ Exception -> 0x0bba, all -> 0x0bb8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:688:0x0be8  */
    /* JADX WARNING: Removed duplicated region for block: B:696:0x0c2c  */
    /* JADX WARNING: Removed duplicated region for block: B:697:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:703:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:706:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:749:0x0d1f A[ExcHandler: all (th java.lang.Throwable), Splitter:B:710:0x0c6b] */
    /* JADX WARNING: Removed duplicated region for block: B:863:0x0eb6  */
    /* JADX WARNING: Removed duplicated region for block: B:864:0x0ed5  */
    /* JADX WARNING: Removed duplicated region for block: B:899:0x0f4f  */
    /* JADX WARNING: Removed duplicated region for block: B:925:0x0fb7  */
    /* JADX WARNING: Removed duplicated region for block: B:930:0x0fc6  */
    /* JADX WARNING: Removed duplicated region for block: B:931:0x0fcb  */
    /* JADX WARNING: Removed duplicated region for block: B:938:0x0fdb A[SYNTHETIC, Splitter:B:938:0x0fdb] */
    /* JADX WARNING: Removed duplicated region for block: B:943:0x0fff A[Catch:{ Exception -> 0x100d, all -> 0x1126 }] */
    /* JADX WARNING: Removed duplicated region for block: B:951:0x1013 A[Catch:{ Exception -> 0x100d, all -> 0x1126 }] */
    /* JADX WARNING: Removed duplicated region for block: B:952:0x1016 A[Catch:{ Exception -> 0x100d, all -> 0x1126 }] */
    /* JADX WARNING: Removed duplicated region for block: B:960:0x102b  */
    /* JADX WARNING: Removed duplicated region for block: B:977:0x105b A[Catch:{ Exception -> 0x1092, all -> 0x1126 }] */
    /* JADX WARNING: Removed duplicated region for block: B:981:0x1065 A[Catch:{ Exception -> 0x1092, all -> 0x1126 }] */
    /* JADX WARNING: Removed duplicated region for block: B:991:0x1099 A[ExcHandler: all (th java.lang.Throwable), PHI: r10 r14 
      PHI: (r10v28 int) = (r10v29 int), (r10v29 int), (r10v29 int), (r10v29 int), (r10v29 int), (r10v20 int), (r10v20 int), (r10v31 int), (r10v20 int) binds: [B:866:0x0ee1, B:883:0x0var_, B:900:0x0var_, B:911:0x0var_, B:912:?, B:765:0x0d49, B:766:?, B:855:0x0ea1, B:762:0x0d43] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r14v57 org.telegram.messenger.video.MediaCodecVideoConvertor) = (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v58 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v65 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v65 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v65 org.telegram.messenger.video.MediaCodecVideoConvertor), (r14v65 org.telegram.messenger.video.MediaCodecVideoConvertor) binds: [B:866:0x0ee1, B:883:0x0var_, B:900:0x0var_, B:911:0x0var_, B:912:?, B:765:0x0d49, B:766:?, B:855:0x0ea1, B:762:0x0d43] A[DONT_GENERATE, DONT_INLINE], Splitter:B:762:0x0d43] */
    @android.annotation.TargetApi(18)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideoInternal(java.lang.String r70, java.io.File r71, int r72, boolean r73, int r74, int r75, int r76, int r77, int r78, long r79, long r81, long r83, long r85, boolean r87, boolean r88, org.telegram.messenger.MediaController.SavedFilterState r89, java.lang.String r90, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r91, boolean r92, org.telegram.messenger.MediaController.CropState r93) {
        /*
            r69 = this;
            r15 = r69
            r13 = r70
            r14 = r72
            r12 = r74
            r11 = r75
            r10 = r76
            r9 = r77
            r8 = r78
            r6 = r79
            r4 = r85
            r3 = r93
            int r2 = android.os.Build.VERSION.SDK_INT
            r16 = -1
            r7 = 0
            android.media.MediaCodec$BufferInfo r6 = new android.media.MediaCodec$BufferInfo     // Catch:{ all -> 0x1335 }
            r6.<init>()     // Catch:{ all -> 0x1335 }
            org.telegram.messenger.video.Mp4Movie r1 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ all -> 0x1335 }
            r1.<init>()     // Catch:{ all -> 0x1335 }
            r20 = r6
            r6 = r71
            r1.setCacheFile(r6)     // Catch:{ all -> 0x1335 }
            r1.setRotation(r7)     // Catch:{ all -> 0x1335 }
            r1.setSize(r12, r11)     // Catch:{ all -> 0x1335 }
            org.telegram.messenger.video.MP4Builder r7 = new org.telegram.messenger.video.MP4Builder     // Catch:{ all -> 0x1335 }
            r7.<init>()     // Catch:{ all -> 0x1335 }
            r6 = r73
            org.telegram.messenger.video.MP4Builder r1 = r7.createMovie(r1, r6)     // Catch:{ all -> 0x1335 }
            r15.mediaMuxer = r1     // Catch:{ all -> 0x1335 }
            float r1 = (float) r4     // Catch:{ all -> 0x1335 }
            r22 = 1148846080(0x447a0000, float:1000.0)
            float r23 = r1 / r22
            r24 = 1000(0x3e8, double:4.94E-321)
            r7 = r2
            long r1 = r4 * r24
            r15.endPresentationTime = r1     // Catch:{ all -> 0x1335 }
            r69.checkConversionCanceled()     // Catch:{ all -> 0x1335 }
            java.lang.String r1 = "csd-0"
            java.lang.String r6 = "prepend-sps-pps-to-idr-frames"
            r2 = 921600(0xe1000, float:1.291437E-39)
            r26 = r6
            r25 = r7
            java.lang.String r7 = "video/avc"
            r32 = r7
            r6 = 0
            if (r92 == 0) goto L_0x05c6
            int r16 = (r83 > r6 ? 1 : (r83 == r6 ? 0 : -1))
            if (r16 < 0) goto L_0x0088
            r2 = 1157234688(0x44fa0000, float:2000.0)
            int r2 = (r23 > r2 ? 1 : (r23 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0073
            r2 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            r9 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x008d
        L_0x0073:
            r2 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r2 = (r23 > r2 ? 1 : (r23 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0081
            r2 = 2200000(0x2191c0, float:3.082857E-39)
            r9 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x008d
        L_0x0081:
            r2 = 1560000(0x17cdc0, float:2.186026E-39)
            r9 = 1560000(0x17cdc0, float:2.186026E-39)
            goto L_0x008d
        L_0x0088:
            if (r9 > 0) goto L_0x008d
            r9 = 921600(0xe1000, float:1.291437E-39)
        L_0x008d:
            int r2 = r12 % 16
            r16 = 1098907648(0x41800000, float:16.0)
            if (r2 == 0) goto L_0x00d8
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00d2 }
            if (r2 == 0) goto L_0x00bc
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d2 }
            r2.<init>()     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r6 = "changing width from "
            r2.append(r6)     // Catch:{ Exception -> 0x00d2 }
            r2.append(r12)     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00d2 }
            float r6 = (float) r12     // Catch:{ Exception -> 0x00d2 }
            float r6 = r6 / r16
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00d2 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d2 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d2 }
        L_0x00bc:
            float r2 = (float) r12     // Catch:{ Exception -> 0x00d2 }
            float r2 = r2 / r16
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00d2 }
            int r2 = r2 * 16
            r12 = r2
            goto L_0x00d8
        L_0x00c7:
            r0 = move-exception
            r59 = r81
            r32 = r83
            r2 = r0
            r4 = r9
        L_0x00ce:
            r9 = r10
        L_0x00cf:
            r14 = r15
            goto L_0x1342
        L_0x00d2:
            r0 = move-exception
            r2 = r0
            r48 = r9
            goto L_0x0520
        L_0x00d8:
            int r2 = r11 % 16
            if (r2 == 0) goto L_0x010f
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00d2 }
            if (r2 == 0) goto L_0x0105
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d2 }
            r2.<init>()     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r6 = "changing height from "
            r2.append(r6)     // Catch:{ Exception -> 0x00d2 }
            r2.append(r11)     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00d2 }
            float r6 = (float) r11     // Catch:{ Exception -> 0x00d2 }
            float r6 = r6 / r16
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00d2 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d2 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d2 }
        L_0x0105:
            float r2 = (float) r11     // Catch:{ Exception -> 0x00d2 }
            float r2 = r2 / r16
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00d2 }
            int r2 = r2 * 16
            r11 = r2
        L_0x010f:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0507, all -> 0x0501 }
            if (r2 == 0) goto L_0x0137
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d2 }
            r2.<init>()     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r6 = "create photo encoder "
            r2.append(r6)     // Catch:{ Exception -> 0x00d2 }
            r2.append(r12)     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r6 = " "
            r2.append(r6)     // Catch:{ Exception -> 0x00d2 }
            r2.append(r11)     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r6 = " duration = "
            r2.append(r6)     // Catch:{ Exception -> 0x00d2 }
            r2.append(r4)     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d2 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d2 }
        L_0x0137:
            r7 = r32
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r7, r12, r11)     // Catch:{ Exception -> 0x0507, all -> 0x0501 }
            java.lang.String r6 = "color-format"
            r32 = r1
            r1 = 2130708361(0x7var_, float:1.701803E38)
            r2.setInteger(r6, r1)     // Catch:{ Exception -> 0x0507, all -> 0x0501 }
            java.lang.String r1 = "bitrate"
            r2.setInteger(r1, r9)     // Catch:{ Exception -> 0x0507, all -> 0x0501 }
            java.lang.String r1 = "frame-rate"
            r2.setInteger(r1, r10)     // Catch:{ Exception -> 0x0507, all -> 0x0501 }
            java.lang.String r1 = "i-frame-interval"
            r6 = 2
            r2.setInteger(r1, r6)     // Catch:{ Exception -> 0x0507, all -> 0x0501 }
            android.media.MediaCodec r6 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x0507, all -> 0x0501 }
            r16 = r7
            r1 = 0
            r7 = 1
            r6.configure(r2, r1, r1, r7)     // Catch:{ Exception -> 0x04f6, all -> 0x0501 }
            org.telegram.messenger.video.InputSurface r2 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x04f6, all -> 0x0501 }
            android.view.Surface r1 = r6.createInputSurface()     // Catch:{ Exception -> 0x04f6, all -> 0x0501 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x04f6, all -> 0x0501 }
            r2.makeCurrent()     // Catch:{ Exception -> 0x04e6, all -> 0x0501 }
            r6.start()     // Catch:{ Exception -> 0x04e6, all -> 0x0501 }
            org.telegram.messenger.video.OutputSurface r17 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x04e6, all -> 0x0501 }
            r18 = 0
            float r1 = (float) r10
            r30 = 1
            r19 = r1
            r36 = r32
            r32 = 0
            r1 = r17
            r37 = r2
            r38 = r25
            r2 = r89
            r3 = r70
            r4 = r90
            r5 = r91
            r7 = r6
            r14 = r20
            r13 = 21
            r6 = r18
            r74 = r7
            r47 = r16
            r7 = r12
            r8 = r11
            r48 = r9
            r9 = r72
            r10 = r19
            r49 = r11
            r11 = r30
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x04de, all -> 0x04cf }
            r10 = r38
            if (r10 >= r13) goto L_0x01b5
            java.nio.ByteBuffer[] r6 = r74.getOutputBuffers()     // Catch:{ Exception -> 0x01af, all -> 0x04cf }
            goto L_0x01b6
        L_0x01af:
            r0 = move-exception
            r6 = r74
            r2 = r0
            goto L_0x04ca
        L_0x01b5:
            r6 = 0
        L_0x01b6:
            r69.checkConversionCanceled()     // Catch:{ Exception -> 0x04c3, all -> 0x04cf }
            r1 = -5
            r2 = 1
            r3 = 0
            r4 = 0
            r5 = 0
            r7 = 0
        L_0x01bf:
            if (r7 != 0) goto L_0x04b2
            r69.checkConversionCanceled()     // Catch:{ Exception -> 0x04a7, all -> 0x0498 }
            r8 = r3 ^ 1
            r9 = r7
            r7 = r6
            r6 = 1
        L_0x01c9:
            if (r8 != 0) goto L_0x01d1
            if (r6 == 0) goto L_0x01ce
            goto L_0x01d1
        L_0x01ce:
            r6 = r7
            r7 = r9
            goto L_0x01bf
        L_0x01d1:
            r69.checkConversionCanceled()     // Catch:{ Exception -> 0x04a7, all -> 0x0498 }
            if (r88 == 0) goto L_0x01e1
            r18 = 22000(0x55f0, double:1.08694E-319)
            r11 = r74
            r75 = r8
            r77 = r9
            r8 = r18
            goto L_0x01e9
        L_0x01e1:
            r11 = r74
            r75 = r8
            r77 = r9
            r8 = 2500(0x9c4, double:1.235E-320)
        L_0x01e9:
            int r8 = r11.dequeueOutputBuffer(r14, r8)     // Catch:{ Exception -> 0x0496, all -> 0x0498 }
            r9 = -1
            if (r8 != r9) goto L_0x0204
            r9 = r77
            r16 = r3
            r18 = r4
            r3 = r7
            r38 = r10
            r13 = r36
            r7 = r47
            r6 = 0
        L_0x01fe:
            r10 = -1
            r4 = r2
            r2 = r49
            goto L_0x03e9
        L_0x0204:
            r9 = -3
            if (r8 != r9) goto L_0x021b
            if (r10 >= r13) goto L_0x020d
            java.nio.ByteBuffer[] r7 = r11.getOutputBuffers()     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
        L_0x020d:
            r9 = r77
            r16 = r3
            r18 = r4
            r3 = r7
            r38 = r10
            r13 = r36
        L_0x0218:
            r7 = r47
            goto L_0x01fe
        L_0x021b:
            r9 = -2
            if (r8 != r9) goto L_0x0285
            android.media.MediaFormat r9 = r11.getOutputFormat()     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            boolean r16 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            if (r16 == 0) goto L_0x023d
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            r13.<init>()     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            r74 = r6
            java.lang.String r6 = "photo encoder new format "
            r13.append(r6)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            r13.append(r9)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            java.lang.String r6 = r13.toString()     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            goto L_0x023f
        L_0x023d:
            r74 = r6
        L_0x023f:
            r13 = -5
            if (r1 != r13) goto L_0x0274
            if (r9 == 0) goto L_0x0274
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            r13 = 0
            int r1 = r6.addTrack(r9, r13)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            r6 = r26
            boolean r16 = r9.containsKey(r6)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            if (r16 == 0) goto L_0x0272
            int r13 = r9.getInteger(r6)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            r26 = r6
            r6 = 1
            if (r13 != r6) goto L_0x0274
            r13 = r36
            java.nio.ByteBuffer r4 = r9.getByteBuffer(r13)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            java.lang.String r6 = "csd-1"
            java.nio.ByteBuffer r6 = r9.getByteBuffer(r6)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            int r4 = r4.limit()     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            int r6 = r6.limit()     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            int r4 = r4 + r6
            goto L_0x0276
        L_0x0272:
            r26 = r6
        L_0x0274:
            r13 = r36
        L_0x0276:
            r6 = r74
            r9 = r77
            r16 = r3
            r18 = r4
            r3 = r7
            r38 = r10
            goto L_0x0218
        L_0x0282:
            r0 = move-exception
            goto L_0x04ac
        L_0x0285:
            r74 = r6
            r13 = r36
            if (r8 < 0) goto L_0x0477
            r6 = 21
            if (r10 >= r6) goto L_0x0292
            r6 = r7[r8]     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            goto L_0x0296
        L_0x0292:
            java.nio.ByteBuffer r6 = r11.getOutputBuffer(r8)     // Catch:{ Exception -> 0x0496, all -> 0x0498 }
        L_0x0296:
            if (r6 == 0) goto L_0x0459
            int r9 = r14.size     // Catch:{ Exception -> 0x0496, all -> 0x0498 }
            r77 = r7
            r7 = 1
            if (r9 <= r7) goto L_0x03ca
            int r7 = r14.flags     // Catch:{ Exception -> 0x03c2, all -> 0x03b1 }
            r16 = r7 & 2
            if (r16 != 0) goto L_0x0330
            if (r4 == 0) goto L_0x02b6
            r16 = r7 & 1
            if (r16 == 0) goto L_0x02b6
            r38 = r10
            int r10 = r14.offset     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            int r10 = r10 + r4
            r14.offset = r10     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            int r9 = r9 - r4
            r14.size = r9     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            goto L_0x02b8
        L_0x02b6:
            r38 = r10
        L_0x02b8:
            if (r2 == 0) goto L_0x0307
            r7 = r7 & 1
            if (r7 == 0) goto L_0x0307
            int r2 = r14.size     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            r10 = 100
            if (r2 <= r10) goto L_0x0305
            int r2 = r14.offset     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            r6.position(r2)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            byte[] r2 = new byte[r10]     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            r6.get(r2)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            r7 = 0
            r9 = 0
        L_0x02d0:
            r10 = 96
            if (r7 >= r10) goto L_0x0305
            byte r10 = r2[r7]     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            if (r10 != 0) goto L_0x02fc
            int r10 = r7 + 1
            byte r10 = r2[r10]     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            if (r10 != 0) goto L_0x02fc
            int r10 = r7 + 2
            byte r10 = r2[r10]     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            if (r10 != 0) goto L_0x02fc
            int r10 = r7 + 3
            byte r10 = r2[r10]     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            r16 = r2
            r2 = 1
            if (r10 != r2) goto L_0x02fe
            int r9 = r9 + 1
            if (r9 <= r2) goto L_0x02fe
            int r2 = r14.offset     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            int r2 = r2 + r7
            r14.offset = r2     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            int r2 = r14.size     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            int r2 = r2 - r7
            r14.size = r2     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            goto L_0x0305
        L_0x02fc:
            r16 = r2
        L_0x02fe:
            int r7 = r7 + 1
            r2 = r16
            r10 = 100
            goto L_0x02d0
        L_0x0305:
            r7 = 0
            goto L_0x0308
        L_0x0307:
            r7 = r2
        L_0x0308:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            r10 = r7
            r9 = 1
            long r6 = r2.writeSampleData(r1, r6, r14, r9)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            r16 = r3
            r2 = 0
            int r9 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r9 == 0) goto L_0x0327
            org.telegram.messenger.MediaController$VideoConvertorListener r9 = r15.callback     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            if (r9 == 0) goto L_0x0327
            r18 = r4
            float r4 = (float) r2     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            float r4 = r4 / r22
            float r4 = r4 / r23
            r9.didWriteData(r6, r4)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            goto L_0x0329
        L_0x0327:
            r18 = r4
        L_0x0329:
            r4 = r10
        L_0x032a:
            r7 = r47
        L_0x032c:
            r2 = r49
            goto L_0x03d5
        L_0x0330:
            r16 = r3
            r18 = r4
            r38 = r10
            r7 = -5
            r4 = r2
            r2 = 0
            if (r1 != r7) goto L_0x032a
            byte[] r7 = new byte[r9]     // Catch:{ Exception -> 0x03c2, all -> 0x03b1 }
            int r10 = r14.offset     // Catch:{ Exception -> 0x03c2, all -> 0x03b1 }
            int r10 = r10 + r9
            r6.limit(r10)     // Catch:{ Exception -> 0x03c2, all -> 0x03b1 }
            int r9 = r14.offset     // Catch:{ Exception -> 0x03c2, all -> 0x03b1 }
            r6.position(r9)     // Catch:{ Exception -> 0x03c2, all -> 0x03b1 }
            r6.get(r7)     // Catch:{ Exception -> 0x03c2, all -> 0x03b1 }
            int r6 = r14.size     // Catch:{ Exception -> 0x03c2, all -> 0x03b1 }
            r9 = 1
            int r6 = r6 - r9
        L_0x0350:
            if (r6 < 0) goto L_0x038f
            r10 = 3
            if (r6 <= r10) goto L_0x038f
            byte r10 = r7[r6]     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            if (r10 != r9) goto L_0x0389
            int r9 = r6 + -1
            byte r9 = r7[r9]     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            if (r9 != 0) goto L_0x0389
            int r9 = r6 + -2
            byte r9 = r7[r9]     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            if (r9 != 0) goto L_0x0389
            int r9 = r6 + -3
            byte r10 = r7[r9]     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            if (r10 != 0) goto L_0x0389
            java.nio.ByteBuffer r6 = java.nio.ByteBuffer.allocate(r9)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            int r10 = r14.size     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            int r10 = r10 - r9
            java.nio.ByteBuffer r10 = java.nio.ByteBuffer.allocate(r10)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            r2 = 0
            java.nio.ByteBuffer r3 = r6.put(r7, r2, r9)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            r3.position(r2)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            int r3 = r14.size     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            int r3 = r3 - r9
            java.nio.ByteBuffer r3 = r10.put(r7, r9, r3)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            r3.position(r2)     // Catch:{ Exception -> 0x0282, all -> 0x0498 }
            goto L_0x0391
        L_0x0389:
            int r6 = r6 + -1
            r2 = 0
            r9 = 1
            goto L_0x0350
        L_0x038f:
            r6 = 0
            r10 = 0
        L_0x0391:
            r7 = r47
            r2 = r49
            android.media.MediaFormat r3 = android.media.MediaFormat.createVideoFormat(r7, r12, r2)     // Catch:{ Exception -> 0x03af, all -> 0x03ad }
            if (r6 == 0) goto L_0x03a5
            if (r10 == 0) goto L_0x03a5
            r3.setByteBuffer(r13, r6)     // Catch:{ Exception -> 0x03af, all -> 0x03ad }
            java.lang.String r6 = "csd-1"
            r3.setByteBuffer(r6, r10)     // Catch:{ Exception -> 0x03af, all -> 0x03ad }
        L_0x03a5:
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x03af, all -> 0x03ad }
            r9 = 0
            int r1 = r6.addTrack(r3, r9)     // Catch:{ Exception -> 0x03af, all -> 0x03ad }
            goto L_0x03d5
        L_0x03ad:
            r0 = move-exception
            goto L_0x03b4
        L_0x03af:
            r0 = move-exception
            goto L_0x03c5
        L_0x03b1:
            r0 = move-exception
            r2 = r49
        L_0x03b4:
            r9 = r76
            r59 = r81
            r32 = r83
            r11 = r2
            r14 = r15
            r4 = r48
            r7 = 0
            r2 = r0
            goto L_0x1344
        L_0x03c2:
            r0 = move-exception
            r2 = r49
        L_0x03c5:
            r6 = r11
            r11 = r2
            r2 = r0
            goto L_0x0526
        L_0x03ca:
            r16 = r3
            r18 = r4
            r38 = r10
            r7 = r47
            r4 = r2
            goto L_0x032c
        L_0x03d5:
            int r3 = r14.flags     // Catch:{ Exception -> 0x0457, all -> 0x0455 }
            r3 = r3 & 4
            if (r3 == 0) goto L_0x03de
            r3 = 0
            r6 = 1
            goto L_0x03e0
        L_0x03de:
            r3 = 0
            r6 = 0
        L_0x03e0:
            r11.releaseOutputBuffer(r8, r3)     // Catch:{ Exception -> 0x0457, all -> 0x0455 }
            r3 = r77
            r9 = r6
            r10 = -1
            r6 = r74
        L_0x03e9:
            if (r8 == r10) goto L_0x0401
            r8 = r75
            r49 = r2
            r2 = r4
            r47 = r7
        L_0x03f2:
            r74 = r11
            r36 = r13
            r4 = r18
            r10 = r38
            r13 = 21
            r7 = r3
            r3 = r16
            goto L_0x01c9
        L_0x0401:
            if (r16 != 0) goto L_0x0445
            r17.drawImage()     // Catch:{ Exception -> 0x043e, all -> 0x0438 }
            float r8 = (float) r5
            r10 = 1106247680(0x41var_, float:30.0)
            float r8 = r8 / r10
            float r8 = r8 * r22
            float r8 = r8 * r22
            float r8 = r8 * r22
            r74 = r1
            r49 = r2
            long r1 = (long) r8
            r10 = r37
            r10.setPresentationTime(r1)     // Catch:{ Exception -> 0x0434, all -> 0x042f }
            r10.swapBuffers()     // Catch:{ Exception -> 0x0434, all -> 0x042f }
            int r5 = r5 + 1
            float r1 = (float) r5     // Catch:{ Exception -> 0x0434, all -> 0x042f }
            r2 = 1106247680(0x41var_, float:30.0)
            float r2 = r2 * r23
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 < 0) goto L_0x044b
            r11.signalEndOfInputStream()     // Catch:{ Exception -> 0x0434, all -> 0x042f }
            r8 = 0
            r16 = 1
            goto L_0x044d
        L_0x042f:
            r0 = move-exception
            r1 = r74
            goto L_0x0499
        L_0x0434:
            r0 = move-exception
            r1 = r74
            goto L_0x0492
        L_0x0438:
            r0 = move-exception
            r74 = r1
        L_0x043b:
            r49 = r2
            goto L_0x0499
        L_0x043e:
            r0 = move-exception
            r74 = r1
        L_0x0441:
            r49 = r2
            goto L_0x04aa
        L_0x0445:
            r74 = r1
            r49 = r2
            r10 = r37
        L_0x044b:
            r8 = r75
        L_0x044d:
            r1 = r74
            r2 = r4
            r47 = r7
            r37 = r10
            goto L_0x03f2
        L_0x0455:
            r0 = move-exception
            goto L_0x043b
        L_0x0457:
            r0 = move-exception
            goto L_0x0441
        L_0x0459:
            r10 = r37
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0491, all -> 0x0498 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0491, all -> 0x0498 }
            r3.<init>()     // Catch:{ Exception -> 0x0491, all -> 0x0498 }
            java.lang.String r4 = "encoderOutputBuffer "
            r3.append(r4)     // Catch:{ Exception -> 0x0491, all -> 0x0498 }
            r3.append(r8)     // Catch:{ Exception -> 0x0491, all -> 0x0498 }
            java.lang.String r4 = " was null"
            r3.append(r4)     // Catch:{ Exception -> 0x0491, all -> 0x0498 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0491, all -> 0x0498 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0491, all -> 0x0498 }
            throw r2     // Catch:{ Exception -> 0x0491, all -> 0x0498 }
        L_0x0477:
            r10 = r37
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0491, all -> 0x0498 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0491, all -> 0x0498 }
            r3.<init>()     // Catch:{ Exception -> 0x0491, all -> 0x0498 }
            java.lang.String r4 = "unexpected result from encoder.dequeueOutputBuffer: "
            r3.append(r4)     // Catch:{ Exception -> 0x0491, all -> 0x0498 }
            r3.append(r8)     // Catch:{ Exception -> 0x0491, all -> 0x0498 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0491, all -> 0x0498 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0491, all -> 0x0498 }
            throw r2     // Catch:{ Exception -> 0x0491, all -> 0x0498 }
        L_0x0491:
            r0 = move-exception
        L_0x0492:
            r2 = r0
            r37 = r10
            goto L_0x04ad
        L_0x0496:
            r0 = move-exception
            goto L_0x04aa
        L_0x0498:
            r0 = move-exception
        L_0x0499:
            r9 = r76
            r59 = r81
            r32 = r83
            r2 = r0
            r14 = r15
            r4 = r48
            r11 = r49
            goto L_0x1343
        L_0x04a7:
            r0 = move-exception
            r11 = r74
        L_0x04aa:
            r10 = r37
        L_0x04ac:
            r2 = r0
        L_0x04ad:
            r6 = r11
            r11 = r49
            goto L_0x0526
        L_0x04b2:
            r11 = r74
            r10 = r37
            r2 = r10
            r6 = r11
            r9 = r48
            r11 = r49
            r7 = 0
            r46 = 0
            r10 = r76
            goto L_0x056b
        L_0x04c3:
            r0 = move-exception
            r11 = r74
            r10 = r37
            r2 = r0
            r6 = r11
        L_0x04ca:
            r11 = r49
            r1 = -5
            goto L_0x0526
        L_0x04cf:
            r0 = move-exception
            r9 = r76
            r59 = r81
            r32 = r83
            r2 = r0
            r14 = r15
            r4 = r48
            r11 = r49
            goto L_0x1342
        L_0x04de:
            r0 = move-exception
            r11 = r74
            r10 = r37
            r2 = r0
            r6 = r11
            goto L_0x04f0
        L_0x04e6:
            r0 = move-exception
            r10 = r2
            r48 = r9
            r49 = r11
            r11 = r6
            r2 = r0
            r37 = r10
        L_0x04f0:
            r11 = r49
            r1 = -5
            r17 = 0
            goto L_0x0526
        L_0x04f6:
            r0 = move-exception
            r48 = r9
            r49 = r11
            r11 = r6
            r2 = r0
            r11 = r49
            r1 = -5
            goto L_0x0522
        L_0x0501:
            r0 = move-exception
            r48 = r9
            r49 = r11
            goto L_0x0510
        L_0x0507:
            r0 = move-exception
            r48 = r9
            r49 = r11
            goto L_0x051f
        L_0x050d:
            r0 = move-exception
            r48 = r9
        L_0x0510:
            r9 = r76
            r59 = r81
            r32 = r83
            r2 = r0
            r14 = r15
            r4 = r48
            goto L_0x1342
        L_0x051c:
            r0 = move-exception
            r48 = r9
        L_0x051f:
            r2 = r0
        L_0x0520:
            r1 = -5
            r6 = 0
        L_0x0522:
            r17 = 0
            r37 = 0
        L_0x0526:
            boolean r3 = r2 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x05b7 }
            if (r3 == 0) goto L_0x052f
            if (r88 != 0) goto L_0x052f
            r46 = 1
            goto L_0x0531
        L_0x052f:
            r46 = 0
        L_0x0531:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x05aa }
            r3.<init>()     // Catch:{ all -> 0x05aa }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x05aa }
            r9 = r48
            r3.append(r9)     // Catch:{ all -> 0x05a8 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x05a8 }
            r10 = r76
            r3.append(r10)     // Catch:{ all -> 0x059b }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x059b }
            r3.append(r11)     // Catch:{ all -> 0x059b }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x059b }
            r3.append(r12)     // Catch:{ all -> 0x059b }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x059b }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x059b }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x059b }
            r2 = r37
            r7 = r46
            r46 = 1
        L_0x056b:
            if (r17 == 0) goto L_0x057c
            r17.release()     // Catch:{ all -> 0x0571 }
            goto L_0x057c
        L_0x0571:
            r0 = move-exception
            r59 = r81
            r32 = r83
            r2 = r0
            r4 = r9
            r9 = r10
            r14 = r15
            goto L_0x1344
        L_0x057c:
            if (r2 == 0) goto L_0x0581
            r2.release()     // Catch:{ all -> 0x0571 }
        L_0x0581:
            if (r6 == 0) goto L_0x0589
            r6.stop()     // Catch:{ all -> 0x0571 }
            r6.release()     // Catch:{ all -> 0x0571 }
        L_0x0589:
            r69.checkConversionCanceled()     // Catch:{ all -> 0x0571 }
            r59 = r81
            r32 = r83
            r4 = r9
            r9 = r10
            r10 = r12
            r14 = r15
            r6 = r46
            r46 = r7
            r7 = r11
            goto L_0x1314
        L_0x059b:
            r0 = move-exception
            r59 = r81
            r32 = r83
            r2 = r0
            r4 = r9
            r9 = r10
            r14 = r15
            r7 = r46
            goto L_0x1344
        L_0x05a8:
            r0 = move-exception
            goto L_0x05ad
        L_0x05aa:
            r0 = move-exception
            r9 = r48
        L_0x05ad:
            r59 = r81
            r32 = r83
            r2 = r0
            r4 = r9
            r14 = r15
            r7 = r46
            goto L_0x05c2
        L_0x05b7:
            r0 = move-exception
            r9 = r48
            r59 = r81
            r32 = r83
            r2 = r0
            r4 = r9
            r14 = r15
            r7 = 0
        L_0x05c2:
            r9 = r76
            goto L_0x1344
        L_0x05c6:
            r13 = r1
            r14 = r20
            r38 = r25
            r7 = r32
            r1 = 921600(0xe1000, float:1.291437E-39)
            android.media.MediaExtractor r2 = new android.media.MediaExtractor     // Catch:{ all -> 0x1335 }
            r2.<init>()     // Catch:{ all -> 0x1335 }
            r15.extractor = r2     // Catch:{ all -> 0x1335 }
            r8 = r70
            r6 = r26
            r2.setDataSource(r8)     // Catch:{ all -> 0x1335 }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x1335 }
            r3 = 0
            int r5 = org.telegram.messenger.MediaController.findTrack(r2, r3)     // Catch:{ all -> 0x1335 }
            r2 = -1
            if (r9 == r2) goto L_0x05f1
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x00c7 }
            r4 = 1
            int r2 = org.telegram.messenger.MediaController.findTrack(r2, r4)     // Catch:{ all -> 0x00c7 }
            r3 = r2
            goto L_0x05f3
        L_0x05f1:
            r4 = 1
            r3 = -1
        L_0x05f3:
            java.lang.String r2 = "mime"
            if (r5 < 0) goto L_0x0609
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ all -> 0x00c7 }
            android.media.MediaFormat r4 = r4.getTrackFormat(r5)     // Catch:{ all -> 0x00c7 }
            java.lang.String r4 = r4.getString(r2)     // Catch:{ all -> 0x00c7 }
            boolean r4 = r4.equals(r7)     // Catch:{ all -> 0x00c7 }
            if (r4 != 0) goto L_0x0609
            r4 = 1
            goto L_0x060a
        L_0x0609:
            r4 = 0
        L_0x060a:
            if (r87 != 0) goto L_0x065b
            if (r4 == 0) goto L_0x060f
            goto L_0x065b
        L_0x060f:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x064d }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ all -> 0x064d }
            r4 = -1
            if (r9 == r4) goto L_0x0618
            r13 = 1
            goto L_0x0619
        L_0x0618:
            r13 = 0
        L_0x0619:
            r1 = r69
            r5 = 1
            r4 = r14
            r14 = 1
            r5 = r79
            r14 = r8
            r7 = r81
            r14 = r10
            r9 = r85
            r11 = r71
            r12 = r13
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ all -> 0x063e }
            r10 = r74
            r7 = r75
            r4 = r77
            r59 = r81
            r32 = r83
            r9 = r14
            r14 = r15
            r1 = -5
            r6 = 0
            r46 = 0
            goto L_0x1314
        L_0x063e:
            r0 = move-exception
            r12 = r74
            r11 = r75
            r4 = r77
        L_0x0645:
            r59 = r81
            r32 = r83
            r2 = r0
        L_0x064a:
            r9 = r14
            goto L_0x00cf
        L_0x064d:
            r0 = move-exception
            r12 = r74
            r11 = r75
            r4 = r77
            r59 = r81
            r32 = r83
            r2 = r0
            goto L_0x00ce
        L_0x065b:
            r12 = r14
            r4 = -1
            r11 = 1
            r14 = r10
            if (r5 < 0) goto L_0x12db
            r18 = -2147483648(0xfffffffvar_, double:NaN)
            r8 = 1000(0x3e8, float:1.401E-42)
            int r8 = r8 / r14
            int r8 = r8 * 1000
            long r9 = (long) r8     // Catch:{ Exception -> 0x1253, all -> 0x1249 }
            android.media.MediaExtractor r8 = r15.extractor     // Catch:{ Exception -> 0x1253, all -> 0x1249 }
            r8.selectTrack(r5)     // Catch:{ Exception -> 0x1253, all -> 0x1249 }
            android.media.MediaExtractor r8 = r15.extractor     // Catch:{ Exception -> 0x1253, all -> 0x1249 }
            android.media.MediaFormat r8 = r8.getTrackFormat(r5)     // Catch:{ Exception -> 0x1253, all -> 0x1249 }
            r20 = 0
            int r24 = (r83 > r20 ? 1 : (r83 == r20 ? 0 : -1))
            if (r24 < 0) goto L_0x069a
            r20 = 1157234688(0x44fa0000, float:2000.0)
            int r20 = (r23 > r20 ? 1 : (r23 == r20 ? 0 : -1))
            if (r20 > 0) goto L_0x0685
            r20 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x0693
        L_0x0685:
            r20 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r20 = (r23 > r20 ? 1 : (r23 == r20 ? 0 : -1))
            if (r20 > 0) goto L_0x0690
            r20 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x0693
        L_0x0690:
            r20 = 1560000(0x17cdc0, float:2.186026E-39)
        L_0x0693:
            r24 = r3
            r4 = r20
            r20 = 0
            goto L_0x06aa
        L_0x069a:
            if (r77 > 0) goto L_0x06a4
            r20 = r83
            r24 = r3
            r4 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x06aa
        L_0x06a4:
            r4 = r77
            r20 = r83
            r24 = r3
        L_0x06aa:
            r3 = r78
            if (r3 <= 0) goto L_0x06d1
            int r4 = java.lang.Math.min(r3, r4)     // Catch:{ Exception -> 0x06c1, all -> 0x06b3 }
            goto L_0x06d1
        L_0x06b3:
            r0 = move-exception
            r12 = r74
            r11 = r75
            r59 = r81
            r2 = r0
            r9 = r14
            r14 = r15
            r32 = r20
            goto L_0x1342
        L_0x06c1:
            r0 = move-exception
            r59 = r81
            r2 = r0
            r61 = r5
            r9 = r14
            r14 = r15
            r32 = r20
        L_0x06cb:
            r1 = -5
            r3 = 0
            r6 = 0
            r8 = 0
            goto L_0x1263
        L_0x06d1:
            r25 = 0
            int r27 = (r20 > r25 ? 1 : (r20 == r25 ? 0 : -1))
            if (r27 < 0) goto L_0x06dc
            r27 = r12
            r11 = r16
            goto L_0x06e0
        L_0x06dc:
            r27 = r12
            r11 = r20
        L_0x06e0:
            int r20 = (r11 > r25 ? 1 : (r11 == r25 ? 0 : -1))
            if (r20 < 0) goto L_0x0713
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0708, all -> 0x06f8 }
            r3 = 0
            r1.seekTo(r11, r3)     // Catch:{ Exception -> 0x0708, all -> 0x06f8 }
            r3 = r93
            r21 = r5
            r26 = r6
            r83 = r11
            r5 = 0
            r11 = r79
            goto L_0x074a
        L_0x06f8:
            r0 = move-exception
            r59 = r81
            r2 = r0
            r32 = r11
            r9 = r14
            r14 = r15
            r1 = -5
            r7 = 0
            r12 = r74
        L_0x0704:
            r11 = r75
            goto L_0x1344
        L_0x0708:
            r0 = move-exception
            r59 = r81
            r2 = r0
            r61 = r5
            r32 = r11
        L_0x0710:
            r9 = r14
            r14 = r15
            goto L_0x06cb
        L_0x0713:
            r83 = r11
            r25 = 0
            r11 = r79
            int r1 = (r11 > r25 ? 1 : (r11 == r25 ? 0 : -1))
            if (r1 <= 0) goto L_0x073c
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0733, all -> 0x072c }
            r3 = 0
            r1.seekTo(r11, r3)     // Catch:{ Exception -> 0x0733, all -> 0x072c }
            r3 = r93
            r21 = r5
            r26 = r6
            r5 = 0
            goto L_0x074a
        L_0x072c:
            r0 = move-exception
            r12 = r74
            r11 = r75
            goto L_0x0645
        L_0x0733:
            r0 = move-exception
            r59 = r81
            r32 = r83
            r2 = r0
            r61 = r5
            goto L_0x0710
        L_0x073c:
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x123e, all -> 0x1230 }
            r21 = r5
            r26 = r6
            r3 = 0
            r5 = 0
            r1.seekTo(r5, r3)     // Catch:{ Exception -> 0x1228, all -> 0x1230 }
            r3 = r93
        L_0x074a:
            if (r3 == 0) goto L_0x0770
            r1 = 90
            r11 = r72
            r12 = -1
            if (r11 == r1) goto L_0x075d
            r1 = 270(0x10e, float:3.78E-43)
            if (r11 != r1) goto L_0x0758
            goto L_0x075d
        L_0x0758:
            int r1 = r3.transformWidth     // Catch:{ Exception -> 0x0764, all -> 0x072c }
            int r5 = r3.transformHeight     // Catch:{ Exception -> 0x0764, all -> 0x072c }
            goto L_0x0761
        L_0x075d:
            int r1 = r3.transformHeight     // Catch:{ Exception -> 0x0764, all -> 0x072c }
            int r5 = r3.transformWidth     // Catch:{ Exception -> 0x0764, all -> 0x072c }
        L_0x0761:
            r6 = r5
            r5 = r1
            goto L_0x0777
        L_0x0764:
            r0 = move-exception
            r59 = r81
            r32 = r83
            r2 = r0
        L_0x076a:
            r9 = r14
            r14 = r15
            r61 = r21
            goto L_0x06cb
        L_0x0770:
            r11 = r72
            r12 = -1
            r5 = r74
            r6 = r75
        L_0x0777:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1228, all -> 0x1230 }
            if (r1 == 0) goto L_0x0797
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0764, all -> 0x072c }
            r1.<init>()     // Catch:{ Exception -> 0x0764, all -> 0x072c }
            java.lang.String r12 = "create encoder with w = "
            r1.append(r12)     // Catch:{ Exception -> 0x0764, all -> 0x072c }
            r1.append(r5)     // Catch:{ Exception -> 0x0764, all -> 0x072c }
            java.lang.String r12 = " h = "
            r1.append(r12)     // Catch:{ Exception -> 0x0764, all -> 0x072c }
            r1.append(r6)     // Catch:{ Exception -> 0x0764, all -> 0x072c }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0764, all -> 0x072c }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0764, all -> 0x072c }
        L_0x0797:
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r7, r5, r6)     // Catch:{ Exception -> 0x1228, all -> 0x1230 }
            java.lang.String r12 = "color-format"
            r3 = 2130708361(0x7var_, float:1.701803E38)
            r1.setInteger(r12, r3)     // Catch:{ Exception -> 0x1228, all -> 0x1230 }
            java.lang.String r3 = "bitrate"
            r1.setInteger(r3, r4)     // Catch:{ Exception -> 0x1228, all -> 0x1230 }
            java.lang.String r3 = "frame-rate"
            r1.setInteger(r3, r14)     // Catch:{ Exception -> 0x1228, all -> 0x1230 }
            java.lang.String r3 = "i-frame-interval"
            r12 = 2
            r1.setInteger(r3, r12)     // Catch:{ Exception -> 0x1228, all -> 0x1230 }
            r3 = 23
            r12 = r38
            if (r12 >= r3) goto L_0x07e7
            int r3 = java.lang.Math.min(r6, r5)     // Catch:{ Exception -> 0x0764, all -> 0x072c }
            r25 = r5
            r5 = 480(0x1e0, float:6.73E-43)
            if (r3 > r5) goto L_0x07e9
            r3 = 921600(0xe1000, float:1.291437E-39)
            if (r4 <= r3) goto L_0x07c9
            goto L_0x07ca
        L_0x07c9:
            r3 = r4
        L_0x07ca:
            java.lang.String r4 = "bitrate"
            r1.setInteger(r4, r3)     // Catch:{ Exception -> 0x07df, all -> 0x07d2 }
            r20 = r3
            goto L_0x07eb
        L_0x07d2:
            r0 = move-exception
            r12 = r74
            r11 = r75
            r59 = r81
            r32 = r83
            r2 = r0
            r4 = r3
            goto L_0x064a
        L_0x07df:
            r0 = move-exception
            r59 = r81
            r32 = r83
            r2 = r0
            r4 = r3
            goto L_0x076a
        L_0x07e7:
            r25 = r5
        L_0x07e9:
            r20 = r4
        L_0x07eb:
            android.media.MediaCodec r5 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x121a, all -> 0x1209 }
            r3 = 1
            r4 = 0
            r5.configure(r1, r4, r4, r3)     // Catch:{ Exception -> 0x11f6, all -> 0x1209 }
            org.telegram.messenger.video.InputSurface r1 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x11f6, all -> 0x1209 }
            android.view.Surface r3 = r5.createInputSurface()     // Catch:{ Exception -> 0x11f6, all -> 0x1209 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x11f6, all -> 0x1209 }
            r1.makeCurrent()     // Catch:{ Exception -> 0x11dc, all -> 0x1209 }
            r5.start()     // Catch:{ Exception -> 0x11dc, all -> 0x1209 }
            java.lang.String r3 = r8.getString(r2)     // Catch:{ Exception -> 0x11dc, all -> 0x1209 }
            android.media.MediaCodec r3 = android.media.MediaCodec.createDecoderByType(r3)     // Catch:{ Exception -> 0x11dc, all -> 0x1209 }
            org.telegram.messenger.video.OutputSurface r28 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x11c7, all -> 0x1209 }
            r29 = 0
            r77 = r3
            float r3 = (float) r14
            r30 = 0
            r54 = r1
            r1 = r28
            r55 = r2
            r2 = r89
            r59 = r77
            r58 = r24
            r32 = 1
            r24 = r3
            r3 = r29
            r4 = r90
            r77 = r5
            r61 = r21
            r62 = r25
            r5 = r91
            r63 = r6
            r64 = r26
            r6 = r93
            r65 = r7
            r7 = r74
            r66 = r8
            r8 = r75
            r25 = r9
            r9 = r72
            r10 = r24
            r32 = r83
            r36 = r13
            r13 = 1
            r11 = r30
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x11b7, all -> 0x11b2 }
            android.view.Surface r1 = r28.getSurface()     // Catch:{ Exception -> 0x119e, all -> 0x11b2 }
            r3 = r59
            r2 = r66
            r4 = 0
            r5 = 0
            r3.configure(r2, r1, r5, r4)     // Catch:{ Exception -> 0x1190, all -> 0x11b2 }
            r3.start()     // Catch:{ Exception -> 0x1190, all -> 0x11b2 }
            r1 = 21
            if (r12 >= r1) goto L_0x0890
            java.nio.ByteBuffer[] r6 = r3.getInputBuffers()     // Catch:{ Exception -> 0x087f, all -> 0x0872 }
            java.nio.ByteBuffer[] r1 = r77.getOutputBuffers()     // Catch:{ Exception -> 0x087f, all -> 0x0872 }
            r2 = r58
            r67 = r6
            r6 = r1
            r1 = r67
            goto L_0x0894
        L_0x0872:
            r0 = move-exception
        L_0x0873:
            r12 = r74
            r11 = r75
            r9 = r76
        L_0x0879:
            r59 = r81
            r2 = r0
            r14 = r15
            goto L_0x1216
        L_0x087f:
            r0 = move-exception
            r9 = r76
            r6 = r77
            r59 = r81
            r2 = r0
            r42 = r5
            r14 = r15
            r4 = r20
            r1 = -5
        L_0x088d:
            r8 = 0
            goto L_0x1269
        L_0x0890:
            r1 = r5
            r6 = r1
            r2 = r58
        L_0x0894:
            if (r2 < 0) goto L_0x09be
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x09ab, all -> 0x0998 }
            android.media.MediaFormat r4 = r4.getTrackFormat(r2)     // Catch:{ Exception -> 0x09ab, all -> 0x0998 }
            r7 = r55
            java.lang.String r8 = r4.getString(r7)     // Catch:{ Exception -> 0x09ab, all -> 0x0998 }
            java.lang.String r9 = "audio/mp4a-latm"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x09ab, all -> 0x0998 }
            if (r8 != 0) goto L_0x08b9
            java.lang.String r8 = r4.getString(r7)     // Catch:{ Exception -> 0x087f, all -> 0x0872 }
            java.lang.String r9 = "audio/mpeg"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x087f, all -> 0x0872 }
            if (r8 == 0) goto L_0x08b7
            goto L_0x08b9
        L_0x08b7:
            r8 = 0
            goto L_0x08ba
        L_0x08b9:
            r8 = 1
        L_0x08ba:
            java.lang.String r7 = r4.getString(r7)     // Catch:{ Exception -> 0x09ab, all -> 0x0998 }
            java.lang.String r9 = "audio/unknown"
            boolean r7 = r7.equals(r9)     // Catch:{ Exception -> 0x09ab, all -> 0x0998 }
            if (r7 == 0) goto L_0x08c7
            r2 = -1
        L_0x08c7:
            if (r2 < 0) goto L_0x098b
            if (r8 == 0) goto L_0x0926
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ Exception -> 0x091a, all -> 0x0915 }
            int r7 = r7.addTrack(r4, r13)     // Catch:{ Exception -> 0x091a, all -> 0x0915 }
            android.media.MediaExtractor r9 = r15.extractor     // Catch:{ Exception -> 0x091a, all -> 0x0915 }
            r9.selectTrack(r2)     // Catch:{ Exception -> 0x091a, all -> 0x0915 }
            java.lang.String r9 = "max-input-size"
            int r4 = r4.getInteger(r9)     // Catch:{ Exception -> 0x08dd, all -> 0x0872 }
            goto L_0x08e3
        L_0x08dd:
            r0 = move-exception
            r4 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ Exception -> 0x091a, all -> 0x0915 }
            r4 = 0
        L_0x08e3:
            if (r4 > 0) goto L_0x08e7
            r4 = 16384(0x4000, float:2.2959E-41)
        L_0x08e7:
            java.nio.ByteBuffer r9 = java.nio.ByteBuffer.allocateDirect(r4)     // Catch:{ Exception -> 0x091a, all -> 0x0915 }
            r13 = r79
            r5 = 1
            r10 = 0
            int r21 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
            if (r21 <= 0) goto L_0x08fd
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x0913, all -> 0x0872 }
            r10 = 0
            r5.seekTo(r13, r10)     // Catch:{ Exception -> 0x0913, all -> 0x0872 }
            r83 = r4
            goto L_0x0907
        L_0x08fd:
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x0913, all -> 0x0872 }
            r83 = r4
            r4 = 0
            r10 = 0
            r5.seekTo(r10, r4)     // Catch:{ Exception -> 0x0913, all -> 0x0872 }
        L_0x0907:
            r10 = r81
            r5 = r83
            r83 = r6
            r4 = r9
            r6 = 0
            r9 = r76
            goto L_0x09cb
        L_0x0913:
            r0 = move-exception
            goto L_0x091d
        L_0x0915:
            r0 = move-exception
            r13 = r79
            goto L_0x0873
        L_0x091a:
            r0 = move-exception
            r13 = r79
        L_0x091d:
            r9 = r76
        L_0x091f:
            r6 = r77
            r59 = r81
            r2 = r0
            goto L_0x09b7
        L_0x0926:
            r13 = r79
            android.media.MediaExtractor r5 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0987, all -> 0x0983 }
            r5.<init>()     // Catch:{ Exception -> 0x0987, all -> 0x0983 }
            r7 = r70
            r9 = r76
            r5.setDataSource(r7)     // Catch:{ Exception -> 0x0981, all -> 0x097f }
            r5.selectTrack(r2)     // Catch:{ Exception -> 0x0981, all -> 0x097f }
            r10 = 0
            int r21 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
            if (r21 <= 0) goto L_0x094d
            r10 = 0
            r5.seekTo(r13, r10)     // Catch:{ Exception -> 0x094b, all -> 0x0944 }
            r83 = r6
            goto L_0x0954
        L_0x0944:
            r0 = move-exception
            r12 = r74
            r11 = r75
            goto L_0x0879
        L_0x094b:
            r0 = move-exception
            goto L_0x091f
        L_0x094d:
            r83 = r6
            r6 = r10
            r10 = 0
            r5.seekTo(r6, r10)     // Catch:{ Exception -> 0x0981, all -> 0x097f }
        L_0x0954:
            org.telegram.messenger.video.AudioRecoder r6 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x0981, all -> 0x097f }
            r6.<init>(r4, r5, r2)     // Catch:{ Exception -> 0x0981, all -> 0x097f }
            r6.startTime = r13     // Catch:{ Exception -> 0x0971, all -> 0x097f }
            r10 = r81
            r6.endTime = r10     // Catch:{ Exception -> 0x096f, all -> 0x096d }
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x096f, all -> 0x096d }
            android.media.MediaFormat r5 = r6.format     // Catch:{ Exception -> 0x096f, all -> 0x096d }
            r7 = 1
            int r4 = r4.addTrack(r5, r7)     // Catch:{ Exception -> 0x096f, all -> 0x096d }
            r7 = r4
            r4 = 0
            r5 = 0
            goto L_0x09cb
        L_0x096d:
            r0 = move-exception
            goto L_0x099f
        L_0x096f:
            r0 = move-exception
            goto L_0x0974
        L_0x0971:
            r0 = move-exception
            r10 = r81
        L_0x0974:
            r2 = r0
            r42 = r6
            r59 = r10
            r14 = r15
            r4 = r20
            r1 = -5
            goto L_0x0a36
        L_0x097f:
            r0 = move-exception
            goto L_0x099d
        L_0x0981:
            r0 = move-exception
            goto L_0x09b0
        L_0x0983:
            r0 = move-exception
            r9 = r76
            goto L_0x099d
        L_0x0987:
            r0 = move-exception
            r9 = r76
            goto L_0x09b0
        L_0x098b:
            r9 = r76
            r13 = r79
            r10 = r81
            r83 = r6
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = -5
            goto L_0x09cb
        L_0x0998:
            r0 = move-exception
            r9 = r76
            r13 = r79
        L_0x099d:
            r10 = r81
        L_0x099f:
            r12 = r74
            r2 = r0
            r59 = r10
            r14 = r15
            r4 = r20
            r1 = -5
        L_0x09a8:
            r7 = 0
            goto L_0x0704
        L_0x09ab:
            r0 = move-exception
            r9 = r76
            r13 = r79
        L_0x09b0:
            r10 = r81
            r6 = r77
            r2 = r0
            r59 = r10
        L_0x09b7:
            r14 = r15
            r4 = r20
            r1 = -5
            r8 = 0
            goto L_0x11f2
        L_0x09be:
            r9 = r76
            r13 = r79
            r10 = r81
            r83 = r6
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = -5
            r8 = 1
        L_0x09cb:
            if (r2 >= 0) goto L_0x09d0
            r21 = 1
            goto L_0x09d2
        L_0x09d0:
            r21 = 0
        L_0x09d2:
            r69.checkConversionCanceled()     // Catch:{ Exception -> 0x117b, all -> 0x1178 }
            r57 = r16
            r55 = r18
            r39 = r21
            r81 = -5
            r18 = 0
            r21 = 1
            r24 = 0
            r29 = 0
            r30 = 0
            r34 = 0
            r37 = 0
            r19 = r83
            r67 = r5
            r5 = r4
            r4 = r67
        L_0x09f2:
            if (r24 == 0) goto L_0x0a0c
            if (r8 != 0) goto L_0x09f9
            if (r39 != 0) goto L_0x09f9
            goto L_0x0a0c
        L_0x09f9:
            r7 = r75
            r2 = r77
            r1 = r81
            r42 = r6
            r59 = r10
            r14 = r15
            r4 = r20
            r6 = 0
            r8 = 0
            r10 = r74
            goto L_0x12a6
        L_0x0a0c:
            r69.checkConversionCanceled()     // Catch:{ Exception -> 0x1162, all -> 0x1152 }
            if (r8 != 0) goto L_0x0a3b
            if (r6 == 0) goto L_0x0a3b
            r82 = r5
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x0a2b, all -> 0x0a1e }
            boolean r5 = r6.step(r5, r7)     // Catch:{ Exception -> 0x0a2b, all -> 0x0a1e }
            r39 = r5
            goto L_0x0a3d
        L_0x0a1e:
            r0 = move-exception
            r12 = r74
            r1 = r81
            r2 = r0
            r59 = r10
            r14 = r15
            r4 = r20
            goto L_0x09a8
        L_0x0a2b:
            r0 = move-exception
            r1 = r81
            r2 = r0
            r42 = r6
        L_0x0a31:
            r59 = r10
            r14 = r15
            r4 = r20
        L_0x0a36:
            r8 = 0
        L_0x0a37:
            r6 = r77
            goto L_0x1269
        L_0x0a3b:
            r82 = r5
        L_0x0a3d:
            if (r18 != 0) goto L_0x0be8
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x0bd4, all -> 0x0bcc }
            int r5 = r5.getSampleTrackIndex()     // Catch:{ Exception -> 0x0bd4, all -> 0x0bcc }
            r83 = r6
            r6 = r61
            if (r5 != r6) goto L_0x0aae
            r13 = 2500(0x9c4, double:1.235E-320)
            int r5 = r3.dequeueInputBuffer(r13)     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            if (r5 < 0) goto L_0x0a92
            r13 = 21
            if (r12 >= r13) goto L_0x0a5a
            r13 = r1[r5]     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            goto L_0x0a5e
        L_0x0a5a:
            java.nio.ByteBuffer r13 = r3.getInputBuffer(r5)     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
        L_0x0a5e:
            android.media.MediaExtractor r14 = r15.extractor     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            r84 = r1
            r1 = 0
            int r50 = r14.readSampleData(r13, r1)     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            if (r50 >= 0) goto L_0x0a7b
            r49 = 0
            r50 = 0
            r51 = 0
            r53 = 4
            r47 = r3
            r48 = r5
            r47.queueInputBuffer(r48, r49, r50, r51, r53)     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            r18 = 1
            goto L_0x0a94
        L_0x0a7b:
            r49 = 0
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            long r51 = r1.getSampleTime()     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            r53 = 0
            r47 = r3
            r48 = r5
            r47.queueInputBuffer(r48, r49, r50, r51, r53)     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            r1.advance()     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            goto L_0x0a94
        L_0x0a92:
            r84 = r1
        L_0x0a94:
            r5 = r82
            r45 = r2
            r40 = r7
            r59 = r10
            r13 = r27
            r1 = 0
            r27 = r8
            r7 = r79
            goto L_0x0b9e
        L_0x0aa5:
            r0 = move-exception
            r1 = r81
            r42 = r83
            r2 = r0
            r61 = r6
            goto L_0x0a31
        L_0x0aae:
            r84 = r1
            if (r8 == 0) goto L_0x0b88
            r1 = -1
            if (r2 == r1) goto L_0x0b7b
            if (r5 != r2) goto L_0x0b88
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0b75, all -> 0x0b6e }
            long r13 = r1.getSampleSize()     // Catch:{ Exception -> 0x0b75, all -> 0x0b6e }
            r45 = r2
            long r1 = (long) r4
            int r5 = (r13 > r1 ? 1 : (r13 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0ace
            r1 = 1024(0x400, double:5.06E-321)
            long r13 = r13 + r1
            int r1 = (int) r13
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocateDirect(r1)     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            r4 = r1
            goto L_0x0ad0
        L_0x0ace:
            r5 = r82
        L_0x0ad0:
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0b75, all -> 0x0b6e }
            r2 = 0
            int r1 = r1.readSampleData(r5, r2)     // Catch:{ Exception -> 0x0b75, all -> 0x0b6e }
            r13 = r27
            r13.size = r1     // Catch:{ Exception -> 0x0b75, all -> 0x0b6e }
            r1 = 21
            if (r12 >= r1) goto L_0x0ae7
            r5.position(r2)     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            int r1 = r13.size     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            r5.limit(r1)     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
        L_0x0ae7:
            int r1 = r13.size     // Catch:{ Exception -> 0x0b75, all -> 0x0b6e }
            if (r1 < 0) goto L_0x0af9
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            long r1 = r1.getSampleTime()     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            r13.presentationTimeUs = r1     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            r1.advance()     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            goto L_0x0afe
        L_0x0af9:
            r1 = 0
            r13.size = r1     // Catch:{ Exception -> 0x0b75, all -> 0x0b6e }
            r18 = 1
        L_0x0afe:
            int r1 = r13.size     // Catch:{ Exception -> 0x0b75, all -> 0x0b6e }
            if (r1 <= 0) goto L_0x0b5f
            r1 = 0
            int r14 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r14 < 0) goto L_0x0b0e
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0aa5, all -> 0x0a1e }
            int r14 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r14 >= 0) goto L_0x0b5f
        L_0x0b0e:
            r1 = 0
            r13.offset = r1     // Catch:{ Exception -> 0x0b75, all -> 0x0b6e }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0b75, all -> 0x0b6e }
            int r2 = r2.getSampleFlags()     // Catch:{ Exception -> 0x0b75, all -> 0x0b6e }
            r13.flags = r2     // Catch:{ Exception -> 0x0b75, all -> 0x0b6e }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0b75, all -> 0x0b6e }
            r59 = r10
            long r10 = r2.writeSampleData(r7, r5, r13, r1)     // Catch:{ Exception -> 0x0b5a, all -> 0x0b55 }
            r1 = 0
            int r14 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r14 == 0) goto L_0x0b4d
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r15.callback     // Catch:{ Exception -> 0x0b5a, all -> 0x0b55 }
            if (r1 == 0) goto L_0x0b4d
            r82 = r4
            r2 = r5
            long r4 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0b5a, all -> 0x0b55 }
            r40 = r7
            r27 = r8
            r14 = 2500(0x9c4, double:1.235E-320)
            r7 = r79
            long r47 = r4 - r7
            int r41 = (r47 > r34 ? 1 : (r47 == r34 ? 0 : -1))
            if (r41 <= 0) goto L_0x0b40
            long r34 = r4 - r7
        L_0x0b40:
            r4 = r34
            float r14 = (float) r4
            float r14 = r14 / r22
            float r14 = r14 / r23
            r1.didWriteData(r10, r14)     // Catch:{ Exception -> 0x0bba, all -> 0x0bb8 }
            r34 = r4
            goto L_0x0b6a
        L_0x0b4d:
            r82 = r4
            r2 = r5
            r40 = r7
            r27 = r8
            goto L_0x0b68
        L_0x0b55:
            r0 = move-exception
            r7 = r79
            goto L_0x0bd0
        L_0x0b5a:
            r0 = move-exception
            r7 = r79
            goto L_0x0bbb
        L_0x0b5f:
            r82 = r4
            r2 = r5
            r40 = r7
            r27 = r8
            r59 = r10
        L_0x0b68:
            r7 = r79
        L_0x0b6a:
            r4 = r82
            r5 = r2
            goto L_0x0b9d
        L_0x0b6e:
            r0 = move-exception
            r7 = r79
            r59 = r10
            goto L_0x0bd0
        L_0x0b75:
            r0 = move-exception
            r7 = r79
            r59 = r10
            goto L_0x0bbb
        L_0x0b7b:
            r45 = r2
            r40 = r7
            r59 = r10
            r13 = r27
            r27 = r8
            r7 = r79
            goto L_0x0b95
        L_0x0b88:
            r45 = r2
            r40 = r7
            r59 = r10
            r13 = r27
            r27 = r8
            r7 = r79
            r1 = -1
        L_0x0b95:
            if (r5 != r1) goto L_0x0b9b
            r5 = r82
            r1 = 1
            goto L_0x0b9e
        L_0x0b9b:
            r5 = r82
        L_0x0b9d:
            r1 = 0
        L_0x0b9e:
            if (r1 == 0) goto L_0x0bc9
            r1 = 2500(0x9c4, double:1.235E-320)
            int r48 = r3.dequeueInputBuffer(r1)     // Catch:{ Exception -> 0x0bba, all -> 0x0bb8 }
            if (r48 < 0) goto L_0x0bff
            r49 = 0
            r50 = 0
            r51 = 0
            r53 = 4
            r47 = r3
            r47.queueInputBuffer(r48, r49, r50, r51, r53)     // Catch:{ Exception -> 0x0bba, all -> 0x0bb8 }
            r18 = 1
            goto L_0x0bff
        L_0x0bb8:
            r0 = move-exception
            goto L_0x0bd0
        L_0x0bba:
            r0 = move-exception
        L_0x0bbb:
            r8 = 0
            r14 = r69
            r1 = r81
            r42 = r83
            r2 = r0
            r61 = r6
            r4 = r20
            goto L_0x0a37
        L_0x0bc9:
            r1 = 2500(0x9c4, double:1.235E-320)
            goto L_0x0bff
        L_0x0bcc:
            r0 = move-exception
            r59 = r10
            r7 = r13
        L_0x0bd0:
            r14 = r69
            goto L_0x1157
        L_0x0bd4:
            r0 = move-exception
            r83 = r6
            r59 = r10
            r7 = r13
            r8 = 0
            r14 = r69
            r6 = r77
            r1 = r81
            r42 = r83
            r2 = r0
        L_0x0be4:
            r4 = r20
            goto L_0x1269
        L_0x0be8:
            r84 = r1
            r45 = r2
            r83 = r6
            r40 = r7
            r59 = r10
            r6 = r61
            r1 = 2500(0x9c4, double:1.235E-320)
            r67 = r27
            r27 = r8
            r7 = r13
            r13 = r67
            r5 = r82
        L_0x0bff:
            r10 = r29 ^ 1
            r14 = r10
            r1 = r55
            r11 = 1
            r10 = r81
        L_0x0CLASSNAME:
            if (r14 != 0) goto L_0x0CLASSNAME
            if (r11 == 0) goto L_0x0c0c
            goto L_0x0CLASSNAME
        L_0x0c0c:
            r15 = r69
            r55 = r1
            r61 = r6
            r81 = r10
            r2 = r45
            r10 = r59
            r6 = r83
            r1 = r84
            r67 = r27
            r27 = r13
            r13 = r7
            r8 = r67
            r7 = r40
            goto L_0x09f2
        L_0x0CLASSNAME:
            r69.checkConversionCanceled()     // Catch:{ Exception -> 0x1140, all -> 0x1135 }
            if (r88 == 0) goto L_0x0CLASSNAME
            r47 = 22000(0x55f0, double:1.08694E-319)
            r82 = r4
            r15 = r5
            r4 = r47
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r82 = r4
            r15 = r5
            r4 = 2500(0x9c4, double:1.235E-320)
        L_0x0CLASSNAME:
            r67 = r11
            r11 = r77
            r77 = r67
            int r4 = r11.dequeueOutputBuffer(r13, r4)     // Catch:{ Exception -> 0x1130, all -> 0x1135 }
            r5 = -1
            if (r4 != r5) goto L_0x0CLASSNAME
            r47 = r1
            r41 = r3
            r61 = r6
            r81 = r14
            r1 = r36
            r2 = r62
            r5 = r63
            r3 = r65
            r6 = 0
            r14 = r69
        L_0x0CLASSNAME:
            r36 = r15
            r15 = -1
        L_0x0c5c:
            r67 = r24
            r24 = r12
            r12 = r67
            goto L_0x0eb4
        L_0x0CLASSNAME:
            r5 = -3
            if (r4 != r5) goto L_0x0CLASSNAME
            r5 = 21
            if (r12 >= r5) goto L_0x0c7b
            java.nio.ByteBuffer[] r19 = r11.getOutputBuffers()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0d1f }
            goto L_0x0c7b
        L_0x0CLASSNAME:
            r0 = move-exception
            r8 = 0
            r14 = r69
            r42 = r83
            r2 = r0
            r61 = r6
            goto L_0x114e
        L_0x0c7b:
            r47 = r1
            r41 = r3
            r61 = r6
            r81 = r14
            r1 = r36
            r2 = r62
            r5 = r63
            r3 = r65
            r14 = r69
            r6 = r77
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r5 = -2
            if (r4 != r5) goto L_0x0d30
            android.media.MediaFormat r5 = r11.getOutputFormat()     // Catch:{ Exception -> 0x0d22, all -> 0x0d1f }
            r81 = r14
            r14 = -5
            if (r10 != r14) goto L_0x0d08
            if (r5 == 0) goto L_0x0d08
            r14 = r69
            r47 = r1
            org.telegram.messenger.video.MP4Builder r1 = r14.mediaMuxer     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            r2 = 0
            int r1 = r1.addTrack(r5, r2)     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            r2 = r64
            boolean r10 = r5.containsKey(r2)     // Catch:{ Exception -> 0x0cff, all -> 0x0cf6 }
            if (r10 == 0) goto L_0x0ced
            int r10 = r5.getInteger(r2)     // Catch:{ Exception -> 0x0cff, all -> 0x0cf6 }
            r41 = r1
            r1 = 1
            if (r10 != r1) goto L_0x0cef
            r1 = r36
            java.nio.ByteBuffer r10 = r5.getByteBuffer(r1)     // Catch:{ Exception -> 0x0ce0, all -> 0x0cd4 }
            r64 = r2
            java.lang.String r2 = "csd-1"
            java.nio.ByteBuffer r2 = r5.getByteBuffer(r2)     // Catch:{ Exception -> 0x0ce0, all -> 0x0cd4 }
            int r5 = r10.limit()     // Catch:{ Exception -> 0x0ce0, all -> 0x0cd4 }
            int r2 = r2.limit()     // Catch:{ Exception -> 0x0ce0, all -> 0x0cd4 }
            int r5 = r5 + r2
            r30 = r5
            goto L_0x0cf3
        L_0x0cd4:
            r0 = move-exception
            r12 = r74
            r11 = r75
            r2 = r0
            r4 = r20
            r1 = r41
            goto L_0x1343
        L_0x0ce0:
            r0 = move-exception
            r42 = r83
            r2 = r0
            r61 = r6
            r6 = r11
            r4 = r20
            r1 = r41
            goto L_0x088d
        L_0x0ced:
            r41 = r1
        L_0x0cef:
            r64 = r2
            r1 = r36
        L_0x0cf3:
            r10 = r41
            goto L_0x0d0e
        L_0x0cf6:
            r0 = move-exception
            r41 = r1
            r12 = r74
            r11 = r75
            goto L_0x115d
        L_0x0cff:
            r0 = move-exception
            r41 = r1
            r42 = r83
            r2 = r0
            r61 = r6
            goto L_0x0d2b
        L_0x0d08:
            r14 = r69
            r47 = r1
            r1 = r36
        L_0x0d0e:
            r41 = r3
            r61 = r6
            r36 = r15
            r2 = r62
            r5 = r63
            r3 = r65
            r15 = -1
            r6 = r77
            goto L_0x0c5c
        L_0x0d1f:
            r0 = move-exception
            goto L_0x1137
        L_0x0d22:
            r0 = move-exception
            r14 = r69
        L_0x0d25:
            r42 = r83
            r2 = r0
            r61 = r6
        L_0x0d2a:
            r1 = r10
        L_0x0d2b:
            r6 = r11
            r4 = r20
            goto L_0x088d
        L_0x0d30:
            r47 = r1
            r81 = r14
            r1 = r36
            r14 = r69
            if (r4 < 0) goto L_0x1108
            r2 = 21
            if (r12 >= r2) goto L_0x0d43
            r5 = r19[r4]     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            goto L_0x0d47
        L_0x0d41:
            r0 = move-exception
            goto L_0x0d25
        L_0x0d43:
            java.nio.ByteBuffer r5 = r11.getOutputBuffer(r4)     // Catch:{ Exception -> 0x1100, all -> 0x1099 }
        L_0x0d47:
            if (r5 == 0) goto L_0x10de
            int r2 = r13.size     // Catch:{ Exception -> 0x10db, all -> 0x1099 }
            r24 = r12
            r12 = 1
            if (r2 <= r12) goto L_0x0e94
            int r12 = r13.flags     // Catch:{ Exception -> 0x0e8a, all -> 0x1126 }
            r36 = r12 & 2
            if (r36 != 0) goto L_0x0e01
            if (r30 == 0) goto L_0x0d69
            r36 = r12 & 1
            if (r36 == 0) goto L_0x0d69
            r36 = r15
            int r15 = r13.offset     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            int r15 = r15 + r30
            r13.offset = r15     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            int r2 = r2 - r30
            r13.size = r2     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            goto L_0x0d6b
        L_0x0d69:
            r36 = r15
        L_0x0d6b:
            if (r21 == 0) goto L_0x0dbe
            r2 = r12 & 1
            if (r2 == 0) goto L_0x0dbe
            int r2 = r13.size     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            r12 = 100
            if (r2 <= r12) goto L_0x0dbc
            int r2 = r13.offset     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            r5.position(r2)     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            byte[] r2 = new byte[r12]     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            r5.get(r2)     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            r15 = 0
            r21 = 0
        L_0x0d84:
            r12 = 96
            if (r15 >= r12) goto L_0x0dbc
            byte r12 = r2[r15]     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            if (r12 != 0) goto L_0x0db3
            int r12 = r15 + 1
            byte r12 = r2[r12]     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            if (r12 != 0) goto L_0x0db3
            int r12 = r15 + 2
            byte r12 = r2[r12]     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            if (r12 != 0) goto L_0x0db3
            int r12 = r15 + 3
            byte r12 = r2[r12]     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            r41 = r2
            r2 = 1
            if (r12 != r2) goto L_0x0db5
            int r12 = r21 + 1
            if (r12 <= r2) goto L_0x0db0
            int r2 = r13.offset     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            int r2 = r2 + r15
            r13.offset = r2     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            int r2 = r13.size     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            int r2 = r2 - r15
            r13.size = r2     // Catch:{ Exception -> 0x0d41, all -> 0x1126 }
            goto L_0x0dbc
        L_0x0db0:
            r21 = r12
            goto L_0x0db5
        L_0x0db3:
            r41 = r2
        L_0x0db5:
            int r15 = r15 + 1
            r2 = r41
            r12 = 100
            goto L_0x0d84
        L_0x0dbc:
            r21 = 0
        L_0x0dbe:
            org.telegram.messenger.video.MP4Builder r2 = r14.mediaMuxer     // Catch:{ Exception -> 0x0e8a, all -> 0x1126 }
            r61 = r6
            r12 = 1
            long r5 = r2.writeSampleData(r10, r5, r13, r12)     // Catch:{ Exception -> 0x0dfc, all -> 0x1126 }
            r43 = 0
            int r2 = (r5 > r43 ? 1 : (r5 == r43 ? 0 : -1))
            if (r2 == 0) goto L_0x0df1
            org.telegram.messenger.MediaController$VideoConvertorListener r2 = r14.callback     // Catch:{ Exception -> 0x0dfc, all -> 0x1126 }
            if (r2 == 0) goto L_0x0df1
            r12 = r3
            r15 = r4
            long r3 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0dec, all -> 0x1126 }
            long r49 = r3 - r7
            int r41 = (r49 > r34 ? 1 : (r49 == r34 ? 0 : -1))
            if (r41 <= 0) goto L_0x0ddd
            long r34 = r3 - r7
        L_0x0ddd:
            r3 = r34
            r41 = r12
            float r12 = (float) r3
            float r12 = r12 / r22
            float r12 = r12 / r23
            r2.didWriteData(r5, r12)     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            r34 = r3
            goto L_0x0df4
        L_0x0dec:
            r0 = move-exception
            r41 = r12
            goto L_0x0e7f
        L_0x0df1:
            r41 = r3
            r15 = r4
        L_0x0df4:
            r2 = r62
            r5 = r63
            r3 = r65
            goto L_0x0ea1
        L_0x0dfc:
            r0 = move-exception
            r41 = r3
            goto L_0x0e8f
        L_0x0e01:
            r41 = r3
            r61 = r6
            r36 = r15
            r3 = -5
            r15 = r4
            if (r10 != r3) goto L_0x0df4
            byte[] r3 = new byte[r2]     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            int r4 = r13.offset     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            int r4 = r4 + r2
            r5.limit(r4)     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            int r2 = r13.offset     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            r5.position(r2)     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            r5.get(r3)     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            int r2 = r13.size     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            r5 = 1
            int r2 = r2 - r5
        L_0x0e1f:
            if (r2 < 0) goto L_0x0e5d
            r4 = 3
            if (r2 <= r4) goto L_0x0e5d
            byte r6 = r3[r2]     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            if (r6 != r5) goto L_0x0e59
            int r6 = r2 + -1
            byte r6 = r3[r6]     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            if (r6 != 0) goto L_0x0e59
            int r6 = r2 + -2
            byte r6 = r3[r6]     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            if (r6 != 0) goto L_0x0e59
            int r6 = r2 + -3
            byte r12 = r3[r6]     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            if (r12 != 0) goto L_0x0e59
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r6)     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            int r12 = r13.size     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            int r12 = r12 - r6
            java.nio.ByteBuffer r12 = java.nio.ByteBuffer.allocate(r12)     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            r4 = 0
            java.nio.ByteBuffer r5 = r2.put(r3, r4, r6)     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            r5.position(r4)     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            int r5 = r13.size     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            int r5 = r5 - r6
            java.nio.ByteBuffer r3 = r12.put(r3, r6, r5)     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            r3.position(r4)     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            r6 = r2
            goto L_0x0e5f
        L_0x0e59:
            int r2 = r2 + -1
            r5 = 1
            goto L_0x0e1f
        L_0x0e5d:
            r6 = 0
            r12 = 0
        L_0x0e5f:
            r2 = r62
            r5 = r63
            r3 = r65
            android.media.MediaFormat r4 = android.media.MediaFormat.createVideoFormat(r3, r2, r5)     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            if (r6 == 0) goto L_0x0e75
            if (r12 == 0) goto L_0x0e75
            r4.setByteBuffer(r1, r6)     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            java.lang.String r6 = "csd-1"
            r4.setByteBuffer(r6, r12)     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
        L_0x0e75:
            org.telegram.messenger.video.MP4Builder r6 = r14.mediaMuxer     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            r12 = 0
            int r4 = r6.addTrack(r4, r12)     // Catch:{ Exception -> 0x0e7e, all -> 0x1126 }
            r10 = r4
            goto L_0x0ea1
        L_0x0e7e:
            r0 = move-exception
        L_0x0e7f:
            r42 = r83
            r2 = r0
            r1 = r10
            r6 = r11
            r4 = r20
            r3 = r41
            goto L_0x088d
        L_0x0e8a:
            r0 = move-exception
            r41 = r3
            r61 = r6
        L_0x0e8f:
            r42 = r83
            r2 = r0
            goto L_0x0d2a
        L_0x0e94:
            r41 = r3
            r61 = r6
            r36 = r15
            r2 = r62
            r5 = r63
            r3 = r65
            r15 = r4
        L_0x0ea1:
            int r4 = r13.flags     // Catch:{ Exception -> 0x10d7, all -> 0x1099 }
            r4 = r4 & 4
            if (r4 == 0) goto L_0x0eaa
            r4 = r15
            r6 = 1
            goto L_0x0eac
        L_0x0eaa:
            r4 = r15
            r6 = 0
        L_0x0eac:
            r12 = 0
            r11.releaseOutputBuffer(r4, r12)     // Catch:{ Exception -> 0x10d7, all -> 0x1099 }
            r12 = r6
            r15 = -1
            r6 = r77
        L_0x0eb4:
            if (r4 == r15) goto L_0x0ed5
            r14 = r81
            r4 = r82
            r62 = r2
            r65 = r3
            r63 = r5
            r77 = r11
            r5 = r36
            r3 = r41
            r36 = r1
            r11 = r6
            r1 = r47
            r6 = r61
            r67 = r24
            r24 = r12
            r12 = r67
            goto L_0x0CLASSNAME
        L_0x0ed5:
            if (r29 != 0) goto L_0x10a7
            r31 = r1
            r62 = r2
            r65 = r3
            r4 = r41
            r1 = 2500(0x9c4, double:1.235E-320)
            int r3 = r4.dequeueOutputBuffer(r13, r1)     // Catch:{ Exception -> 0x109d, all -> 0x1099 }
            if (r3 != r15) goto L_0x0ef4
            r63 = r5
            r77 = r6
            r1 = r47
            r7 = r54
            r8 = 0
        L_0x0ef0:
            r46 = 0
            goto L_0x10ba
        L_0x0ef4:
            r1 = -3
            if (r3 != r1) goto L_0x0efd
        L_0x0ef7:
            r63 = r5
            r77 = r6
            goto L_0x10b3
        L_0x0efd:
            r1 = -2
            if (r3 != r1) goto L_0x0var_
            android.media.MediaFormat r1 = r4.getOutputFormat()     // Catch:{ Exception -> 0x0f1d, all -> 0x1126 }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0f1d, all -> 0x1126 }
            if (r2 == 0) goto L_0x0ef7
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0f1d, all -> 0x1126 }
            r2.<init>()     // Catch:{ Exception -> 0x0f1d, all -> 0x1126 }
            java.lang.String r3 = "newFormat = "
            r2.append(r3)     // Catch:{ Exception -> 0x0f1d, all -> 0x1126 }
            r2.append(r1)     // Catch:{ Exception -> 0x0f1d, all -> 0x1126 }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x0f1d, all -> 0x1126 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0f1d, all -> 0x1126 }
            goto L_0x0ef7
        L_0x0f1d:
            r0 = move-exception
            r42 = r83
            r2 = r0
            r3 = r4
            goto L_0x0d2a
        L_0x0var_:
            if (r3 < 0) goto L_0x1077
            int r1 = r13.size     // Catch:{ Exception -> 0x109d, all -> 0x1099 }
            if (r1 == 0) goto L_0x0f2d
            r77 = 1
            goto L_0x0f2f
        L_0x0f2d:
            r77 = 0
        L_0x0f2f:
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x109d, all -> 0x1099 }
            r43 = 0
            int r41 = (r59 > r43 ? 1 : (r59 == r43 ? 0 : -1))
            if (r41 <= 0) goto L_0x0var_
            int r41 = (r1 > r59 ? 1 : (r1 == r59 ? 0 : -1))
            if (r41 < 0) goto L_0x0var_
            int r15 = r13.flags     // Catch:{ Exception -> 0x0f1d, all -> 0x1126 }
            r15 = r15 | 4
            r13.flags = r15     // Catch:{ Exception -> 0x0f1d, all -> 0x1126 }
            r15 = 0
            r18 = 1
            r29 = 1
            goto L_0x0var_
        L_0x0var_:
            r15 = r77
        L_0x0var_:
            r43 = 0
            int r49 = (r32 > r43 ? 1 : (r32 == r43 ? 0 : -1))
            if (r49 < 0) goto L_0x0fb7
            r63 = r5
            int r5 = r13.flags     // Catch:{ Exception -> 0x0fb4, all -> 0x1099 }
            r5 = r5 & 4
            if (r5 == 0) goto L_0x0fb9
            long r49 = r32 - r7
            long r49 = java.lang.Math.abs(r49)     // Catch:{ Exception -> 0x0fb4, all -> 0x1099 }
            r5 = 1000000(0xvar_, float:1.401298E-39)
            int r5 = r5 / r9
            r77 = r6
            long r5 = (long) r5
            int r51 = (r49 > r5 ? 1 : (r49 == r5 ? 0 : -1))
            if (r51 <= 0) goto L_0x0fbb
            r5 = 0
            int r15 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r15 <= 0) goto L_0x0var_
            android.media.MediaExtractor r5 = r14.extractor     // Catch:{ Exception -> 0x0f1d, all -> 0x1126 }
            r6 = 0
            r5.seekTo(r7, r6)     // Catch:{ Exception -> 0x0f1d, all -> 0x1126 }
            r8 = 0
            goto L_0x0f7e
        L_0x0var_:
            android.media.MediaExtractor r5 = r14.extractor     // Catch:{ Exception -> 0x0fb4, all -> 0x1099 }
            r6 = 0
            r8 = 0
            r5.seekTo(r6, r8)     // Catch:{ Exception -> 0x100d, all -> 0x1126 }
        L_0x0f7e:
            long r37 = r47 + r25
            int r5 = r13.flags     // Catch:{ Exception -> 0x0fa5, all -> 0x0var_ }
            r6 = -5
            r5 = r5 & r6
            r13.flags = r5     // Catch:{ Exception -> 0x0fa5, all -> 0x0var_ }
            r4.flush()     // Catch:{ Exception -> 0x0fa5, all -> 0x0var_ }
            r59 = r32
            r5 = 1
            r7 = 0
            r15 = 0
            r29 = 0
            r43 = 0
            r32 = r16
            goto L_0x0fc2
        L_0x0var_:
            r0 = move-exception
            r12 = r74
            r11 = r75
            r2 = r0
            r1 = r10
            r4 = r20
            r59 = r32
            r7 = 0
            r32 = r16
            goto L_0x1344
        L_0x0fa5:
            r0 = move-exception
            r42 = r83
            r2 = r0
            r3 = r4
            r1 = r10
            r6 = r11
            r4 = r20
            r59 = r32
            r32 = r16
            goto L_0x1269
        L_0x0fb4:
            r0 = move-exception
            goto L_0x10a0
        L_0x0fb7:
            r63 = r5
        L_0x0fb9:
            r77 = r6
        L_0x0fbb:
            r6 = -5
            r8 = 0
            r7 = r18
            r5 = 0
            r43 = 0
        L_0x0fc2:
            int r18 = (r32 > r43 ? 1 : (r32 == r43 ? 0 : -1))
            if (r18 < 0) goto L_0x0fcb
            r18 = r7
            r6 = r32
            goto L_0x0fcf
        L_0x0fcb:
            r18 = r7
            r6 = r79
        L_0x0fcf:
            int r46 = (r6 > r43 ? 1 : (r6 == r43 ? 0 : -1))
            if (r46 <= 0) goto L_0x1010
            int r46 = (r57 > r16 ? 1 : (r57 == r16 ? 0 : -1))
            if (r46 != 0) goto L_0x1010
            int r46 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r46 >= 0) goto L_0x0fff
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x100d, all -> 0x1126 }
            if (r1 == 0) goto L_0x0ffd
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x100d, all -> 0x1126 }
            r1.<init>()     // Catch:{ Exception -> 0x100d, all -> 0x1126 }
            java.lang.String r2 = "drop frame startTime = "
            r1.append(r2)     // Catch:{ Exception -> 0x100d, all -> 0x1126 }
            r1.append(r6)     // Catch:{ Exception -> 0x100d, all -> 0x1126 }
            java.lang.String r2 = " present time = "
            r1.append(r2)     // Catch:{ Exception -> 0x100d, all -> 0x1126 }
            long r6 = r13.presentationTimeUs     // Catch:{ Exception -> 0x100d, all -> 0x1126 }
            r1.append(r6)     // Catch:{ Exception -> 0x100d, all -> 0x1126 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x100d, all -> 0x1126 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x100d, all -> 0x1126 }
        L_0x0ffd:
            r7 = 0
            goto L_0x1011
        L_0x0fff:
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x100d, all -> 0x1126 }
            r6 = -2147483648(0xfffffffvar_, double:NaN)
            int r46 = (r47 > r6 ? 1 : (r47 == r6 ? 0 : -1))
            if (r46 == 0) goto L_0x100a
            long r37 = r37 - r1
        L_0x100a:
            r57 = r1
            goto L_0x1010
        L_0x100d:
            r0 = move-exception
            goto L_0x10a1
        L_0x1010:
            r7 = r15
        L_0x1011:
            if (r5 == 0) goto L_0x1016
            r57 = r16
            goto L_0x1029
        L_0x1016:
            int r1 = (r32 > r16 ? 1 : (r32 == r16 ? 0 : -1))
            if (r1 != 0) goto L_0x1026
            r1 = 0
            int r5 = (r37 > r1 ? 1 : (r37 == r1 ? 0 : -1))
            if (r5 == 0) goto L_0x1026
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x100d, all -> 0x1126 }
            long r1 = r1 + r37
            r13.presentationTimeUs = r1     // Catch:{ Exception -> 0x100d, all -> 0x1126 }
        L_0x1026:
            r4.releaseOutputBuffer(r3, r7)     // Catch:{ Exception -> 0x1073, all -> 0x1126 }
        L_0x1029:
            if (r7 == 0) goto L_0x105b
            r1 = 0
            int r3 = (r32 > r1 ? 1 : (r32 == r1 ? 0 : -1))
            if (r3 < 0) goto L_0x103a
            long r5 = r13.presentationTimeUs     // Catch:{ Exception -> 0x100d, all -> 0x1126 }
            r1 = r47
            long r1 = java.lang.Math.max(r1, r5)     // Catch:{ Exception -> 0x100d, all -> 0x1126 }
            goto L_0x103c
        L_0x103a:
            r1 = r47
        L_0x103c:
            r28.awaitNewImage()     // Catch:{ Exception -> 0x1041, all -> 0x1126 }
            r6 = 0
            goto L_0x1047
        L_0x1041:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)     // Catch:{ Exception -> 0x1073, all -> 0x1126 }
            r6 = 1
        L_0x1047:
            if (r6 != 0) goto L_0x105d
            r28.drawImage()     // Catch:{ Exception -> 0x1073, all -> 0x1126 }
            long r5 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1073, all -> 0x1126 }
            r46 = 1000(0x3e8, double:4.94E-321)
            long r5 = r5 * r46
            r7 = r54
            r7.setPresentationTime(r5)     // Catch:{ Exception -> 0x1092, all -> 0x1126 }
            r7.swapBuffers()     // Catch:{ Exception -> 0x1092, all -> 0x1126 }
            goto L_0x105f
        L_0x105b:
            r1 = r47
        L_0x105d:
            r7 = r54
        L_0x105f:
            int r3 = r13.flags     // Catch:{ Exception -> 0x1092, all -> 0x1126 }
            r3 = r3 & 4
            if (r3 == 0) goto L_0x10b8
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1092, all -> 0x1126 }
            if (r3 == 0) goto L_0x106e
            java.lang.String r3 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x1092, all -> 0x1126 }
        L_0x106e:
            r11.signalEndOfInputStream()     // Catch:{ Exception -> 0x1092, all -> 0x1126 }
            goto L_0x0ef0
        L_0x1073:
            r0 = move-exception
            r7 = r54
            goto L_0x10a1
        L_0x1077:
            r7 = r54
            r8 = 0
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1092, all -> 0x1126 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1092, all -> 0x1126 }
            r2.<init>()     // Catch:{ Exception -> 0x1092, all -> 0x1126 }
            java.lang.String r5 = "unexpected result from decoder.dequeueOutputBuffer: "
            r2.append(r5)     // Catch:{ Exception -> 0x1092, all -> 0x1126 }
            r2.append(r3)     // Catch:{ Exception -> 0x1092, all -> 0x1126 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x1092, all -> 0x1126 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x1092, all -> 0x1126 }
            throw r1     // Catch:{ Exception -> 0x1092, all -> 0x1126 }
        L_0x1092:
            r0 = move-exception
            r42 = r83
            r2 = r0
            r3 = r4
            goto L_0x112d
        L_0x1099:
            r0 = move-exception
            r8 = 0
            goto L_0x1139
        L_0x109d:
            r0 = move-exception
        L_0x109e:
            r7 = r54
        L_0x10a0:
            r8 = 0
        L_0x10a1:
            r42 = r83
            r2 = r0
            r3 = r4
            goto L_0x114e
        L_0x10a7:
            r31 = r1
            r62 = r2
            r65 = r3
            r63 = r5
            r77 = r6
            r4 = r41
        L_0x10b3:
            r1 = r47
            r7 = r54
            r8 = 0
        L_0x10b8:
            r46 = r81
        L_0x10ba:
            r3 = r4
            r54 = r7
            r5 = r36
            r14 = r46
            r6 = r61
            r7 = r79
            r4 = r82
            r36 = r31
            r67 = r11
            r11 = r77
            r77 = r67
            r68 = r24
            r24 = r12
            r12 = r68
            goto L_0x0CLASSNAME
        L_0x10d7:
            r0 = move-exception
            r4 = r41
            goto L_0x109e
        L_0x10db:
            r0 = move-exception
            r4 = r3
            goto L_0x1102
        L_0x10de:
            r1 = r3
            r61 = r6
            r7 = r54
            r8 = 0
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1128, all -> 0x1126 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1128, all -> 0x1126 }
            r3.<init>()     // Catch:{ Exception -> 0x1128, all -> 0x1126 }
            java.lang.String r5 = "encoderOutputBuffer "
            r3.append(r5)     // Catch:{ Exception -> 0x1128, all -> 0x1126 }
            r3.append(r4)     // Catch:{ Exception -> 0x1128, all -> 0x1126 }
            java.lang.String r4 = " was null"
            r3.append(r4)     // Catch:{ Exception -> 0x1128, all -> 0x1126 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x1128, all -> 0x1126 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x1128, all -> 0x1126 }
            throw r2     // Catch:{ Exception -> 0x1128, all -> 0x1126 }
        L_0x1100:
            r0 = move-exception
            r1 = r3
        L_0x1102:
            r61 = r6
            r7 = r54
            r8 = 0
            goto L_0x114b
        L_0x1108:
            r1 = r3
            r61 = r6
            r7 = r54
            r8 = 0
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1128, all -> 0x1126 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1128, all -> 0x1126 }
            r3.<init>()     // Catch:{ Exception -> 0x1128, all -> 0x1126 }
            java.lang.String r5 = "unexpected result from encoder.dequeueOutputBuffer: "
            r3.append(r5)     // Catch:{ Exception -> 0x1128, all -> 0x1126 }
            r3.append(r4)     // Catch:{ Exception -> 0x1128, all -> 0x1126 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x1128, all -> 0x1126 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x1128, all -> 0x1126 }
            throw r2     // Catch:{ Exception -> 0x1128, all -> 0x1126 }
        L_0x1126:
            r0 = move-exception
            goto L_0x1139
        L_0x1128:
            r0 = move-exception
            r42 = r83
            r2 = r0
            r3 = r1
        L_0x112d:
            r54 = r7
            goto L_0x114e
        L_0x1130:
            r0 = move-exception
            r8 = 0
            r14 = r69
            goto L_0x1146
        L_0x1135:
            r0 = move-exception
            r8 = 0
        L_0x1137:
            r14 = r69
        L_0x1139:
            r12 = r74
            r11 = r75
            r2 = r0
            r1 = r10
            goto L_0x115e
        L_0x1140:
            r0 = move-exception
            r8 = 0
            r14 = r69
            r11 = r77
        L_0x1146:
            r1 = r3
            r61 = r6
            r7 = r54
        L_0x114b:
            r42 = r83
            r2 = r0
        L_0x114e:
            r1 = r10
            r6 = r11
            goto L_0x0be4
        L_0x1152:
            r0 = move-exception
            r59 = r10
            r14 = r15
            r8 = 0
        L_0x1157:
            r12 = r74
            r11 = r75
            r1 = r81
        L_0x115d:
            r2 = r0
        L_0x115e:
            r4 = r20
            goto L_0x1343
        L_0x1162:
            r0 = move-exception
            r1 = r3
            r83 = r6
            r59 = r10
            r14 = r15
            r7 = r54
            r8 = 0
            r11 = r77
            r42 = r83
            r2 = r0
            r6 = r11
            r4 = r20
            r1 = r81
            goto L_0x1269
        L_0x1178:
            r0 = move-exception
            goto L_0x120d
        L_0x117b:
            r0 = move-exception
            r11 = r77
            r1 = r3
            r83 = r6
            r14 = r15
            r7 = r54
            r8 = 0
            r59 = r81
            r42 = r83
            r2 = r0
            r6 = r11
            r4 = r20
            r1 = -5
            goto L_0x1269
        L_0x1190:
            r0 = move-exception
            r9 = r76
            r11 = r77
            r1 = r3
            r14 = r15
            r7 = r54
            r8 = 0
            r59 = r81
            r2 = r0
            goto L_0x11ad
        L_0x119e:
            r0 = move-exception
            r9 = r76
            r11 = r77
            r14 = r15
            r7 = r54
            r1 = r59
            r8 = 0
            r59 = r81
            r2 = r0
            r3 = r1
        L_0x11ad:
            r6 = r11
            r4 = r20
            r1 = -5
            goto L_0x11f2
        L_0x11b2:
            r0 = move-exception
            r9 = r76
            goto L_0x120d
        L_0x11b7:
            r0 = move-exception
            r9 = r76
            r11 = r77
            r14 = r15
            r7 = r54
            r1 = r59
            r8 = 0
            r59 = r81
            r2 = r0
            r3 = r1
            goto L_0x11d7
        L_0x11c7:
            r0 = move-exception
            r32 = r83
            r7 = r1
            r1 = r3
            r11 = r5
            r9 = r14
            r14 = r15
            r61 = r21
            r8 = 0
            r59 = r81
            r2 = r0
            r54 = r7
        L_0x11d7:
            r6 = r11
            r4 = r20
            r1 = -5
            goto L_0x11f0
        L_0x11dc:
            r0 = move-exception
            r32 = r83
            r7 = r1
            r11 = r5
            r9 = r14
            r14 = r15
            r61 = r21
            r8 = 0
            r59 = r81
            r2 = r0
            r54 = r7
            r6 = r11
            r4 = r20
            r1 = -5
            r3 = 0
        L_0x11f0:
            r28 = 0
        L_0x11f2:
            r42 = 0
            goto L_0x1269
        L_0x11f6:
            r0 = move-exception
            r32 = r83
            r11 = r5
            r9 = r14
            r14 = r15
            r61 = r21
            r8 = 0
            r59 = r81
            r2 = r0
            r6 = r11
            r4 = r20
            r1 = -5
            r3 = 0
            goto L_0x1263
        L_0x1209:
            r0 = move-exception
            r32 = r83
            r9 = r14
        L_0x120d:
            r14 = r15
            r8 = 0
            r12 = r74
            r11 = r75
            r59 = r81
            r2 = r0
        L_0x1216:
            r4 = r20
            goto L_0x1342
        L_0x121a:
            r0 = move-exception
            r32 = r83
            r9 = r14
            r14 = r15
            r61 = r21
            r8 = 0
            r59 = r81
            r2 = r0
            r4 = r20
            goto L_0x1260
        L_0x1228:
            r0 = move-exception
            r32 = r83
            r9 = r14
            r14 = r15
            r61 = r21
            goto L_0x1245
        L_0x1230:
            r0 = move-exception
            r32 = r83
            r9 = r14
            r14 = r15
            r8 = 0
            r12 = r74
            r11 = r75
            r59 = r81
            goto L_0x1341
        L_0x123e:
            r0 = move-exception
            r32 = r83
            r61 = r5
            r9 = r14
            r14 = r15
        L_0x1245:
            r8 = 0
            r59 = r81
            goto L_0x125f
        L_0x1249:
            r0 = move-exception
            r9 = r14
            r14 = r15
            r8 = 0
            r12 = r74
            r11 = r75
            goto L_0x133b
        L_0x1253:
            r0 = move-exception
            r61 = r5
            r9 = r14
            r14 = r15
            r8 = 0
            r4 = r77
            r59 = r81
            r32 = r83
        L_0x125f:
            r2 = r0
        L_0x1260:
            r1 = -5
            r3 = 0
            r6 = 0
        L_0x1263:
            r28 = 0
            r42 = 0
            r54 = 0
        L_0x1269:
            boolean r5 = r2 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x12d1 }
            if (r5 == 0) goto L_0x1270
            if (r88 != 0) goto L_0x1270
            r8 = 1
        L_0x1270:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x12c6 }
            r5.<init>()     // Catch:{ all -> 0x12c6 }
            java.lang.String r7 = "bitrate: "
            r5.append(r7)     // Catch:{ all -> 0x12c6 }
            r5.append(r4)     // Catch:{ all -> 0x12c6 }
            java.lang.String r7 = " framerate: "
            r5.append(r7)     // Catch:{ all -> 0x12c6 }
            r5.append(r9)     // Catch:{ all -> 0x12c6 }
            java.lang.String r7 = " size: "
            r5.append(r7)     // Catch:{ all -> 0x12c6 }
            r7 = r75
            r5.append(r7)     // Catch:{ all -> 0x12c2 }
            java.lang.String r10 = "x"
            r5.append(r10)     // Catch:{ all -> 0x12c2 }
            r10 = r74
            r5.append(r10)     // Catch:{ all -> 0x12c0 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x12c0 }
            org.telegram.messenger.FileLog.e((java.lang.String) r5)     // Catch:{ all -> 0x12c0 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x12c0 }
            r2 = r6
            r6 = 1
        L_0x12a6:
            android.media.MediaExtractor r5 = r14.extractor     // Catch:{ all -> 0x12c0 }
            r11 = r61
            r5.unselectTrack(r11)     // Catch:{ all -> 0x12c0 }
            if (r3 == 0) goto L_0x12b5
            r3.stop()     // Catch:{ all -> 0x12c0 }
            r3.release()     // Catch:{ all -> 0x12c0 }
        L_0x12b5:
            r3 = r8
            r8 = r6
            r6 = r28
            r67 = r42
            r42 = r2
            r2 = r67
            goto L_0x12f0
        L_0x12c0:
            r0 = move-exception
            goto L_0x12cb
        L_0x12c2:
            r0 = move-exception
            r10 = r74
            goto L_0x12cb
        L_0x12c6:
            r0 = move-exception
            r10 = r74
            r7 = r75
        L_0x12cb:
            r2 = r0
            r11 = r7
            r7 = r8
            r12 = r10
            goto L_0x1344
        L_0x12d1:
            r0 = move-exception
            r10 = r74
            r7 = r75
            r2 = r0
            r11 = r7
            r12 = r10
            goto L_0x1343
        L_0x12db:
            r10 = r74
            r7 = r75
            r9 = r14
            r14 = r15
            r8 = 0
            r4 = r77
            r59 = r81
            r32 = r83
            r1 = -5
            r2 = 0
            r3 = 0
            r6 = 0
            r42 = 0
            r54 = 0
        L_0x12f0:
            if (r6 == 0) goto L_0x12fc
            r6.release()     // Catch:{ all -> 0x12f6 }
            goto L_0x12fc
        L_0x12f6:
            r0 = move-exception
            r2 = r0
            r11 = r7
            r12 = r10
            r7 = r3
            goto L_0x1344
        L_0x12fc:
            if (r54 == 0) goto L_0x1301
            r54.release()     // Catch:{ all -> 0x12f6 }
        L_0x1301:
            if (r42 == 0) goto L_0x1309
            r42.stop()     // Catch:{ all -> 0x12f6 }
            r42.release()     // Catch:{ all -> 0x12f6 }
        L_0x1309:
            if (r2 == 0) goto L_0x130e
            r2.release()     // Catch:{ all -> 0x12f6 }
        L_0x130e:
            r69.checkConversionCanceled()     // Catch:{ all -> 0x12f6 }
            r46 = r3
            r6 = r8
        L_0x1314:
            android.media.MediaExtractor r2 = r14.extractor
            if (r2 == 0) goto L_0x131b
            r2.release()
        L_0x131b:
            org.telegram.messenger.video.MP4Builder r2 = r14.mediaMuxer
            if (r2 == 0) goto L_0x1330
            r2.finishMovie()     // Catch:{ all -> 0x132b }
            org.telegram.messenger.video.MP4Builder r2 = r14.mediaMuxer     // Catch:{ all -> 0x132b }
            long r1 = r2.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x132b }
            r14.endPresentationTime = r1     // Catch:{ all -> 0x132b }
            goto L_0x1330
        L_0x132b:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1330:
            r11 = r4
            r15 = r32
            goto L_0x1398
        L_0x1335:
            r0 = move-exception
            r9 = r10
            r7 = r11
            r10 = r12
            r14 = r15
            r8 = 0
        L_0x133b:
            r4 = r77
            r59 = r81
            r32 = r83
        L_0x1341:
            r2 = r0
        L_0x1342:
            r1 = -5
        L_0x1343:
            r7 = 0
        L_0x1344:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x13c4 }
            r3.<init>()     // Catch:{ all -> 0x13c4 }
            java.lang.String r5 = "bitrate: "
            r3.append(r5)     // Catch:{ all -> 0x13c4 }
            r3.append(r4)     // Catch:{ all -> 0x13c4 }
            java.lang.String r5 = " framerate: "
            r3.append(r5)     // Catch:{ all -> 0x13c4 }
            r3.append(r9)     // Catch:{ all -> 0x13c4 }
            java.lang.String r5 = " size: "
            r3.append(r5)     // Catch:{ all -> 0x13c4 }
            r3.append(r11)     // Catch:{ all -> 0x13c4 }
            java.lang.String r5 = "x"
            r3.append(r5)     // Catch:{ all -> 0x13c4 }
            r3.append(r12)     // Catch:{ all -> 0x13c4 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x13c4 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x13c4 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x13c4 }
            android.media.MediaExtractor r2 = r14.extractor
            if (r2 == 0) goto L_0x137b
            r2.release()
        L_0x137b:
            org.telegram.messenger.video.MP4Builder r2 = r14.mediaMuxer
            if (r2 == 0) goto L_0x1390
            r2.finishMovie()     // Catch:{ all -> 0x138b }
            org.telegram.messenger.video.MP4Builder r2 = r14.mediaMuxer     // Catch:{ all -> 0x138b }
            long r1 = r2.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x138b }
            r14.endPresentationTime = r1     // Catch:{ all -> 0x138b }
            goto L_0x1390
        L_0x138b:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1390:
            r46 = r7
            r7 = r11
            r10 = r12
            r15 = r32
            r6 = 1
            r11 = r4
        L_0x1398:
            if (r46 == 0) goto L_0x13c3
            r20 = 1
            r1 = r69
            r2 = r70
            r3 = r71
            r4 = r72
            r5 = r73
            r6 = r10
            r8 = r76
            r9 = r11
            r10 = r78
            r11 = r79
            r13 = r59
            r17 = r85
            r19 = r87
            r21 = r89
            r22 = r90
            r23 = r91
            r24 = r92
            r25 = r93
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r13, r15, r17, r19, r20, r21, r22, r23, r24, r25)
            return r1
        L_0x13c3:
            return r6
        L_0x13c4:
            r0 = move-exception
            r2 = r69
            r3 = r0
            android.media.MediaExtractor r4 = r2.extractor
            if (r4 == 0) goto L_0x13cf
            r4.release()
        L_0x13cf:
            org.telegram.messenger.video.MP4Builder r4 = r2.mediaMuxer
            if (r4 == 0) goto L_0x13e4
            r4.finishMovie()     // Catch:{ all -> 0x13df }
            org.telegram.messenger.video.MP4Builder r4 = r2.mediaMuxer     // Catch:{ all -> 0x13df }
            long r4 = r4.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x13df }
            r2.endPresentationTime = r4     // Catch:{ all -> 0x13df }
            goto L_0x13e4
        L_0x13df:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x13e4:
            goto L_0x13e6
        L_0x13e5:
            throw r3
        L_0x13e6:
            goto L_0x13e5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, int, long, long, long, long, boolean, boolean, org.telegram.messenger.MediaController$SavedFilterState, java.lang.String, java.util.ArrayList, boolean, org.telegram.messenger.MediaController$CropState):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:67:0x011b, code lost:
        if (r9[r15 + 3] != 1) goto L_0x0123;
     */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x01c7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long readAndWriteTracks(android.media.MediaExtractor r33, org.telegram.messenger.video.MP4Builder r34, android.media.MediaCodec.BufferInfo r35, long r36, long r38, long r40, java.io.File r42, boolean r43) throws java.lang.Exception {
        /*
            r32 = this;
            r1 = r33
            r2 = r34
            r3 = r35
            r4 = r36
            r6 = 0
            int r7 = org.telegram.messenger.MediaController.findTrack(r1, r6)
            r9 = 1
            if (r43 == 0) goto L_0x0018
            int r0 = org.telegram.messenger.MediaController.findTrack(r1, r9)
            r11 = r40
            r10 = r0
            goto L_0x001b
        L_0x0018:
            r11 = r40
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
            r11 = 16384(0x4000, float:2.2959E-41)
        L_0x008b:
            java.nio.ByteBuffer r0 = java.nio.ByteBuffer.allocateDirect(r11)
            r18 = -1
            if (r10 >= 0) goto L_0x0097
            if (r7 < 0) goto L_0x0096
            goto L_0x0097
        L_0x0096:
            return r18
        L_0x0097:
            r32.checkConversionCanceled()
            r22 = r14
            r20 = r18
            r8 = 0
        L_0x009f:
            if (r8 != 0) goto L_0x01f2
            r32.checkConversionCanceled()
            long r24 = r33.getSampleSize()
            long r14 = (long) r11
            int r13 = (r24 > r14 ? 1 : (r24 == r14 ? 0 : -1))
            if (r13 <= 0) goto L_0x00bd
            r13 = 1024(0x400, double:5.06E-321)
            long r13 = r24 + r13
            int r0 = (int) r13
            java.nio.ByteBuffer r11 = java.nio.ByteBuffer.allocateDirect(r0)
            r13 = 0
            r31 = r11
            r11 = r0
            r0 = r31
            goto L_0x00be
        L_0x00bd:
            r13 = 0
        L_0x00be:
            int r14 = r1.readSampleData(r0, r13)
            r3.size = r14
            int r13 = r33.getSampleTrackIndex()
            if (r13 != r7) goto L_0x00ce
            r14 = r16
        L_0x00cc:
            r15 = -1
            goto L_0x00d4
        L_0x00ce:
            if (r13 != r10) goto L_0x00d2
            r14 = r6
            goto L_0x00cc
        L_0x00d2:
            r14 = -1
            goto L_0x00cc
        L_0x00d4:
            if (r14 == r15) goto L_0x01cc
            int r15 = android.os.Build.VERSION.SDK_INT
            r9 = 21
            if (r15 >= r9) goto L_0x00e5
            r9 = 0
            r0.position(r9)
            int r9 = r3.size
            r0.limit(r9)
        L_0x00e5:
            if (r13 == r10) goto L_0x0153
            byte[] r9 = r0.array()
            if (r9 == 0) goto L_0x0153
            int r15 = r0.arrayOffset()
            int r25 = r0.limit()
            int r25 = r15 + r25
            r41 = r6
            r6 = -1
        L_0x00fa:
            r26 = 4
            r43 = r8
            int r8 = r25 + -4
            if (r15 > r8) goto L_0x0157
            byte r27 = r9[r15]
            if (r27 != 0) goto L_0x011e
            int r27 = r15 + 1
            byte r27 = r9[r27]
            if (r27 != 0) goto L_0x011e
            int r27 = r15 + 2
            byte r27 = r9[r27]
            if (r27 != 0) goto L_0x011e
            int r27 = r15 + 3
            r28 = r11
            byte r11 = r9[r27]
            r27 = r10
            r10 = 1
            if (r11 == r10) goto L_0x0125
            goto L_0x0123
        L_0x011e:
            r27 = r10
            r28 = r11
            r10 = 1
        L_0x0123:
            if (r15 != r8) goto L_0x014a
        L_0x0125:
            r11 = -1
            if (r6 == r11) goto L_0x0149
            int r11 = r15 - r6
            if (r15 == r8) goto L_0x012d
            goto L_0x012f
        L_0x012d:
            r26 = 0
        L_0x012f:
            int r11 = r11 - r26
            int r8 = r11 >> 24
            byte r8 = (byte) r8
            r9[r6] = r8
            int r8 = r6 + 1
            int r10 = r11 >> 16
            byte r10 = (byte) r10
            r9[r8] = r10
            int r8 = r6 + 2
            int r10 = r11 >> 8
            byte r10 = (byte) r10
            r9[r8] = r10
            int r6 = r6 + 3
            byte r8 = (byte) r11
            r9[r6] = r8
        L_0x0149:
            r6 = r15
        L_0x014a:
            int r15 = r15 + 1
            r8 = r43
            r10 = r27
            r11 = r28
            goto L_0x00fa
        L_0x0153:
            r41 = r6
            r43 = r8
        L_0x0157:
            r27 = r10
            r28 = r11
            int r6 = r3.size
            if (r6 < 0) goto L_0x0167
            long r8 = r33.getSampleTime()
            r3.presentationTimeUs = r8
            r8 = 0
            goto L_0x016b
        L_0x0167:
            r6 = 0
            r3.size = r6
            r8 = 1
        L_0x016b:
            int r6 = r3.size
            if (r6 <= 0) goto L_0x018f
            if (r8 != 0) goto L_0x018f
            if (r13 != r7) goto L_0x0181
            r9 = 0
            int r6 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r6 <= 0) goto L_0x0181
            int r6 = (r20 > r18 ? 1 : (r20 == r18 ? 0 : -1))
            if (r6 != 0) goto L_0x0181
            long r9 = r3.presentationTimeUs
            r20 = r9
        L_0x0181:
            r9 = 0
            int r6 = (r38 > r9 ? 1 : (r38 == r9 ? 0 : -1))
            if (r6 < 0) goto L_0x0194
            long r9 = r3.presentationTimeUs
            int r6 = (r9 > r38 ? 1 : (r9 == r38 ? 0 : -1))
            if (r6 >= 0) goto L_0x018e
            goto L_0x0194
        L_0x018e:
            r8 = 1
        L_0x018f:
            r11 = r32
        L_0x0191:
            r25 = 1148846080(0x447a0000, float:1000.0)
            goto L_0x01c5
        L_0x0194:
            r6 = 0
            r3.offset = r6
            int r9 = r33.getSampleFlags()
            r3.flags = r9
            long r9 = r2.writeSampleData(r14, r0, r3, r6)
            r14 = 0
            int r11 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1))
            if (r11 == 0) goto L_0x018f
            r11 = r32
            org.telegram.messenger.MediaController$VideoConvertorListener r13 = r11.callback
            if (r13 == 0) goto L_0x0191
            long r14 = r3.presentationTimeUs
            long r29 = r14 - r20
            int r17 = (r29 > r22 ? 1 : (r29 == r22 ? 0 : -1))
            if (r17 <= 0) goto L_0x01b8
            long r14 = r14 - r20
            goto L_0x01ba
        L_0x01b8:
            r14 = r22
        L_0x01ba:
            float r6 = (float) r14
            r25 = 1148846080(0x447a0000, float:1000.0)
            float r6 = r6 / r25
            float r6 = r6 / r12
            r13.didWriteData(r9, r6)
            r22 = r14
        L_0x01c5:
            if (r8 != 0) goto L_0x01ca
            r33.advance()
        L_0x01ca:
            r6 = -1
            goto L_0x01e1
        L_0x01cc:
            r41 = r6
            r43 = r8
            r27 = r10
            r28 = r11
            r6 = -1
            r25 = 1148846080(0x447a0000, float:1000.0)
            r11 = r32
            if (r13 != r6) goto L_0x01dd
            r8 = 1
            goto L_0x01e1
        L_0x01dd:
            r33.advance()
            r8 = 0
        L_0x01e1:
            if (r8 == 0) goto L_0x01e5
            r8 = 1
            goto L_0x01e7
        L_0x01e5:
            r8 = r43
        L_0x01e7:
            r6 = r41
            r10 = r27
            r11 = r28
            r9 = 1
            r14 = 0
            goto L_0x009f
        L_0x01f2:
            r11 = r32
            r27 = r10
            if (r7 < 0) goto L_0x01fb
            r1.unselectTrack(r7)
        L_0x01fb:
            if (r27 < 0) goto L_0x0202
            r10 = r27
            r1.unselectTrack(r10)
        L_0x0202:
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
