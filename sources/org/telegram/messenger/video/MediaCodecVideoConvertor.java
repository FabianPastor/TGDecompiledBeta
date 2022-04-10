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

    public boolean convertVideo(String str, File file, int i, boolean z, int i2, int i3, int i4, int i5, int i6, int i7, int i8, long j, long j2, long j3, boolean z2, long j4, MediaController.SavedFilterState savedFilterState, String str2, ArrayList<VideoEditedInfo.MediaEntity> arrayList, boolean z3, MediaController.CropState cropState, MediaController.VideoConvertorListener videoConvertorListener) {
        String str3 = str;
        long j5 = j4;
        this.callback = videoConvertorListener;
        return convertVideoInternal(str, file, i, z, i2, i3, i4, i5, i6, i7, i8, j, j2, j3, j5, z2, false, savedFilterState, str2, arrayList, z3, cropState);
    }

    public long getLastFrameTimestamp() {
        return this.endPresentationTime;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v92, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v95, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v96, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v97, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v98, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v99, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v100, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v101, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v102, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v103, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v104, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v105, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v106, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v107, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v108, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v109, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v110, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v111, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v112, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v113, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v114, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v115, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v116, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v117, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v118, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v80, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v119, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v120, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v121, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v122, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v123, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v124, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v125, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v126, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v127, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v128, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v201, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v202, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v129, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v130, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v92, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v131, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v206, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v132, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v133, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v95, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v134, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v96, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v135, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v97, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v121, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v136, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v98, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v136, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v137, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v137, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v99, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v262, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v263, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v264, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v266, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v138, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v100, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v268, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v139, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v101, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r43v140, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v102, resolved type: long} */
    /* JADX WARNING: type inference failed for: r1v229 */
    /* JADX WARNING: type inference failed for: r1v230 */
    /* JADX WARNING: Code restructure failed: missing block: B:1006:0x1167, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1007:0x1168, code lost:
        r1 = r0;
        r14 = r3;
        r51 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1008:0x116d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1009:0x116e, code lost:
        r1 = r81;
        r3 = r82;
        r2 = r0;
        r13 = r9;
        r7 = r21;
        r6 = false;
        r71 = r26;
        r26 = r43;
        r43 = r71;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1020:0x11b0, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1028:0x11ee, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1029:0x11ef, code lost:
        r12 = r83;
        r1 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1036:0x1233, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1037:0x1235, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1038:0x1236, code lost:
        r51 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1039:0x1239, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1040:0x123b, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1041:0x123c, code lost:
        r43 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1042:0x123e, code lost:
        r12 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1053:0x127f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1054:0x1280, code lost:
        r12 = r83;
        r59 = r9;
        r69 = r14;
        r1 = r51;
        r43 = r88;
        r26 = r90;
        r14 = r84;
        r3 = r21;
        r13 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1055:0x1294, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1056:0x1295, code lost:
        r12 = r83;
        r3 = r84;
        r69 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1057:0x129c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1058:0x129d, code lost:
        r12 = r83;
        r3 = r84;
        r69 = r14;
        r10 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1059:0x12a5, code lost:
        r1 = r51;
        r43 = r88;
        r26 = r90;
        r14 = r3;
        r3 = r21;
        r13 = -5;
        r59 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1060:0x12b2, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1061:0x12b3, code lost:
        r12 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1064:0x12c5, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1065:0x12c6, code lost:
        r10 = r2;
        r3 = r5;
        r12 = r9;
        r52 = r29;
        r15 = r74;
        r43 = r88;
        r26 = r14;
        r51 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1067:0x12da, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1068:0x12db, code lost:
        r12 = r9;
        r52 = r29;
        r15 = r74;
        r43 = r88;
        r26 = r14;
        r51 = r4;
        r14 = r5;
        r3 = r21;
        r10 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1071:0x12f6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1072:0x12f7, code lost:
        r12 = r9;
        r52 = r29;
        r15 = r74;
        r43 = r88;
        r26 = r14;
        r1 = r0;
        r14 = r5;
        r3 = r21;
        r10 = null;
        r13 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1073:0x130b, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1074:0x130c, code lost:
        r12 = r9;
        r90 = r14;
        r15 = r74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1075:0x1311, code lost:
        r1 = r81;
        r3 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1100:0x1380, code lost:
        r45 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1163:0x14ab, code lost:
        r2.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1167:?, code lost:
        r2.finishMovie();
        r15.endPresentationTime = r15.mediaMuxer.getLastFrameTimestamp(r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1168:0x14be, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1169:0x14bf, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x047c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x047d, code lost:
        r1 = r0;
        r36 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x0481, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x04da, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x04db, code lost:
        r3 = r37;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x04dd, code lost:
        r12 = r83;
        r43 = r88;
        r26 = r90;
        r2 = r0;
        r15 = r13;
        r7 = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x04eb, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x0508, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x055b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x055c, code lost:
        r13 = r74;
        r1 = r36;
        r3 = r37;
        r14 = r15;
        r11 = r38;
        r8 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x0567, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x0568, code lost:
        r3 = r37;
        r15 = r74;
        r12 = r83;
        r43 = r88;
        r26 = r90;
        r2 = r0;
        r7 = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x0594, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x0595, code lost:
        r13 = r74;
        r35 = r7;
        r3 = r10;
        r38 = r11;
        r1 = r0;
        r14 = r15;
        r8 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x05a1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x05a2, code lost:
        r35 = r7;
        r3 = r10;
        r38 = r11;
        r15 = r74;
        r12 = r83;
        r43 = r88;
        r26 = r90;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x05b1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x05b2, code lost:
        r13 = r74;
        r35 = r7;
        r3 = r10;
        r38 = r11;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x0803, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x0804, code lost:
        r1 = r81;
        r43 = r88;
        r2 = r0;
        r7 = r3;
        r12 = r9;
        r26 = r14;
        r6 = false;
        r3 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x0842, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x0843, code lost:
        r43 = r88;
        r1 = r0;
        r12 = r9;
        r26 = r14;
        r52 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x09b5, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x09b6, code lost:
        r12 = r83;
        r43 = r88;
        r26 = r90;
        r3 = r2;
        r7 = r21;
        r6 = false;
        r13 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x09c3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x09c4, code lost:
        r12 = r83;
        r43 = r88;
        r26 = r90;
        r1 = r0;
        r59 = null;
        r69 = r14;
        r3 = r21;
        r13 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x09ff, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x0a96, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x0a97, code lost:
        r12 = r83;
        r43 = r88;
        r26 = r90;
        r2 = r0;
        r3 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0ad7, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x0ad9, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x0b6a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x0b6b, code lost:
        r1 = r0;
        r59 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0CLASSNAME, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0cbb, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0cbd, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0cbe, code lost:
        r1 = r81;
        r3 = r82;
        r2 = r0;
        r43 = r11;
        r7 = r21;
        r13 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0ccc, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0ccd, code lost:
        r59 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0ccf, code lost:
        r69 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0cd1, code lost:
        r14 = r84;
        r1 = r0;
        r43 = r11;
        r3 = r21;
        r13 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x01f9, code lost:
        r6 = r7;
        r13 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x0fab, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x104d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x1050, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x1051, code lost:
        r12 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:985:0x1124, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:988:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r8 = true;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:435:0x07f1, B:446:0x0838] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1004:0x115f  */
    /* JADX WARNING: Removed duplicated region for block: B:1008:0x116d A[ExcHandler: all (r0v57 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:972:0x1109] */
    /* JADX WARNING: Removed duplicated region for block: B:1020:0x11b0 A[ExcHandler: all (th java.lang.Throwable), PHI: r9 r43 
      PHI: (r9v22 int) = (r9v27 int), (r9v27 int), (r9v28 int) binds: [B:858:0x0var_, B:876:0x0fb2, B:846:0x0f2f] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r43v42 long) = (r43v47 long), (r43v47 long), (r43v61 long) binds: [B:858:0x0var_, B:876:0x0fb2, B:846:0x0f2f] A[DONT_GENERATE, DONT_INLINE], Splitter:B:846:0x0f2f] */
    /* JADX WARNING: Removed duplicated region for block: B:1036:0x1233 A[ExcHandler: all (th java.lang.Throwable), PHI: r9 r12 r43 
      PHI: (r9v19 int) = (r9v27 int), (r9v27 int), (r9v27 int), (r9v27 int), (r9v27 int), (r9v14 int) binds: [B:1015:0x1193, B:909:0x1012, B:910:?, B:903:0x1005, B:904:?, B:1031:0x11fa] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r12v49 int) = (r12v57 int), (r12v63 int), (r12v63 int), (r12v63 int), (r12v63 int), (r12v66 int) binds: [B:1015:0x1193, B:909:0x1012, B:910:?, B:903:0x1005, B:904:?, B:1031:0x11fa] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r43v39 long) = (r43v47 long), (r43v47 long), (r43v47 long), (r43v47 long), (r43v47 long), (r43v60 long) binds: [B:1015:0x1193, B:909:0x1012, B:910:?, B:903:0x1005, B:904:?, B:1031:0x11fa] A[DONT_GENERATE, DONT_INLINE], Splitter:B:903:0x1005] */
    /* JADX WARNING: Removed duplicated region for block: B:1040:0x123b A[ExcHandler: all (th java.lang.Throwable), Splitter:B:717:0x0d13] */
    /* JADX WARNING: Removed duplicated region for block: B:1060:0x12b2 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:510:0x097f] */
    /* JADX WARNING: Removed duplicated region for block: B:1073:0x130b A[ExcHandler: all (th java.lang.Throwable), Splitter:B:500:0x091a] */
    /* JADX WARNING: Removed duplicated region for block: B:1099:0x137e A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:1114:0x13cb A[Catch:{ all -> 0x13db }] */
    /* JADX WARNING: Removed duplicated region for block: B:1127:0x1401  */
    /* JADX WARNING: Removed duplicated region for block: B:1129:0x1419 A[SYNTHETIC, Splitter:B:1129:0x1419] */
    /* JADX WARNING: Removed duplicated region for block: B:1135:0x1429 A[Catch:{ all -> 0x141d }] */
    /* JADX WARNING: Removed duplicated region for block: B:1137:0x142e A[Catch:{ all -> 0x141d }] */
    /* JADX WARNING: Removed duplicated region for block: B:1139:0x1436 A[Catch:{ all -> 0x141d }] */
    /* JADX WARNING: Removed duplicated region for block: B:1144:0x1444  */
    /* JADX WARNING: Removed duplicated region for block: B:1147:0x144b A[SYNTHETIC, Splitter:B:1147:0x144b] */
    /* JADX WARNING: Removed duplicated region for block: B:1163:0x14ab  */
    /* JADX WARNING: Removed duplicated region for block: B:1166:0x14b2 A[SYNTHETIC, Splitter:B:1166:0x14b2] */
    /* JADX WARNING: Removed duplicated region for block: B:1172:0x14c9  */
    /* JADX WARNING: Removed duplicated region for block: B:1174:0x14f6  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x042d A[Catch:{ Exception -> 0x04b0, all -> 0x04a1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x042f A[Catch:{ Exception -> 0x04b0, all -> 0x04a1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x043f  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x044e  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x04da A[Catch:{ Exception -> 0x050a, all -> 0x0508 }, ExcHandler: all (th java.lang.Throwable), Splitter:B:126:0x02c7] */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0508 A[ExcHandler: all (th java.lang.Throwable), PHI: r3 r8 r13 r38 
      PHI: (r3v191 int) = (r3v197 int), (r3v197 int), (r3v197 int), (r3v187 int), (r3v206 int) binds: [B:230:0x0450, B:231:?, B:233:0x0462, B:126:0x02c7, B:256:0x04be] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r8v69 int) = (r8v75 int), (r8v75 int), (r8v75 int), (r8v68 int), (r8v68 int) binds: [B:230:0x0450, B:231:?, B:233:0x0462, B:126:0x02c7, B:256:0x04be] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r13v125 org.telegram.messenger.video.MediaCodecVideoConvertor) = (r13v130 org.telegram.messenger.video.MediaCodecVideoConvertor), (r13v130 org.telegram.messenger.video.MediaCodecVideoConvertor), (r13v130 org.telegram.messenger.video.MediaCodecVideoConvertor), (r13v137 org.telegram.messenger.video.MediaCodecVideoConvertor), (r13v137 org.telegram.messenger.video.MediaCodecVideoConvertor) binds: [B:230:0x0450, B:231:?, B:233:0x0462, B:126:0x02c7, B:256:0x04be] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r38v21 int) = (r38v27 int), (r38v27 int), (r38v27 int), (r38v20 int), (r38v20 int) binds: [B:230:0x0450, B:231:?, B:233:0x0462, B:126:0x02c7, B:256:0x04be] A[DONT_GENERATE, DONT_INLINE], Splitter:B:230:0x0450] */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x0567 A[ExcHandler: all (r0v141 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:57:0x01b2] */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x05a1 A[ExcHandler: all (r0v135 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:44:0x0123] */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x05f9 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x063f A[SYNTHETIC, Splitter:B:330:0x063f] */
    /* JADX WARNING: Removed duplicated region for block: B:335:0x0654 A[Catch:{ all -> 0x0643 }] */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x0659 A[Catch:{ all -> 0x0643 }] */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x073f  */
    /* JADX WARNING: Removed duplicated region for block: B:437:0x0803 A[Catch:{ Exception -> 0x0812, all -> 0x0803 }, ExcHandler: all (r0v114 'th' java.lang.Throwable A[CUSTOM_DECLARE, Catch:{ Exception -> 0x0812, all -> 0x0803 }]), Splitter:B:435:0x07f1] */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x085c  */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x089c  */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x08a6 A[SYNTHETIC, Splitter:B:476:0x08a6] */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x08e4 A[SYNTHETIC, Splitter:B:482:0x08e4] */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x0916  */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x09ac A[SYNTHETIC, Splitter:B:520:0x09ac] */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x09b5 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:520:0x09ac] */
    /* JADX WARNING: Removed duplicated region for block: B:527:0x09d6  */
    /* JADX WARNING: Removed duplicated region for block: B:530:0x09dc A[SYNTHETIC, Splitter:B:530:0x09dc] */
    /* JADX WARNING: Removed duplicated region for block: B:537:0x09ff A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:533:0x09f0] */
    /* JADX WARNING: Removed duplicated region for block: B:543:0x0a1c  */
    /* JADX WARNING: Removed duplicated region for block: B:545:0x0a1f  */
    /* JADX WARNING: Removed duplicated region for block: B:597:0x0ad7 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:573:0x0a7b] */
    /* JADX WARNING: Removed duplicated region for block: B:599:0x0adb  */
    /* JADX WARNING: Removed duplicated region for block: B:609:0x0b10  */
    /* JADX WARNING: Removed duplicated region for block: B:612:0x0b1f  */
    /* JADX WARNING: Removed duplicated region for block: B:613:0x0b21  */
    /* JADX WARNING: Removed duplicated region for block: B:618:0x0b42 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:624:0x0b5d A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:634:0x0b7b A[SYNTHETIC, Splitter:B:634:0x0b7b] */
    /* JADX WARNING: Removed duplicated region for block: B:704:0x0cbd A[ExcHandler: all (r0v38 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:626:0x0b61] */
    /* JADX WARNING: Removed duplicated region for block: B:712:0x0cde  */
    /* JADX WARNING: Removed duplicated region for block: B:715:0x0cfb A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:720:0x0d18  */
    /* JADX WARNING: Removed duplicated region for block: B:721:0x0d29  */
    /* JADX WARNING: Removed duplicated region for block: B:726:0x0d3c  */
    /* JADX WARNING: Removed duplicated region for block: B:728:0x0d51  */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x0var_ A[Catch:{ Exception -> 0x11ee, all -> 0x11b0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x0var_ A[Catch:{ Exception -> 0x11ee, all -> 0x11b0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x0f5d  */
    /* JADX WARNING: Removed duplicated region for block: B:873:0x0fab A[ExcHandler: Exception (e java.lang.Exception), PHI: r9 r43 
      PHI: (r9v24 int) = (r9v27 int), (r9v27 int), (r9v27 int), (r9v27 int), (r9v14 int) binds: [B:892:0x0fdd, B:886:0x0fc9, B:887:?, B:867:0x0var_, B:784:0x0e1e] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r43v44 long) = (r43v47 long), (r43v47 long), (r43v47 long), (r43v47 long), (r43v69 long) binds: [B:892:0x0fdd, B:886:0x0fc9, B:887:?, B:867:0x0var_, B:784:0x0e1e] A[DONT_GENERATE, DONT_INLINE], Splitter:B:784:0x0e1e] */
    /* JADX WARNING: Removed duplicated region for block: B:921:0x104d A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:897:0x0ff6] */
    /* JADX WARNING: Removed duplicated region for block: B:929:0x1071  */
    /* JADX WARNING: Removed duplicated region for block: B:941:0x10a2  */
    /* JADX WARNING: Removed duplicated region for block: B:945:0x10ac  */
    /* JADX WARNING: Removed duplicated region for block: B:946:0x10af  */
    /* JADX WARNING: Removed duplicated region for block: B:953:0x10bd A[SYNTHETIC, Splitter:B:953:0x10bd] */
    /* JADX WARNING: Removed duplicated region for block: B:958:0x10e1 A[Catch:{ Exception -> 0x10ef, all -> 0x116d }] */
    /* JADX WARNING: Removed duplicated region for block: B:966:0x10f6 A[Catch:{ Exception -> 0x10ef, all -> 0x116d }] */
    /* JADX WARNING: Removed duplicated region for block: B:967:0x10f9 A[Catch:{ Exception -> 0x10ef, all -> 0x116d }] */
    /* JADX WARNING: Removed duplicated region for block: B:975:0x110e A[Catch:{ Exception -> 0x117f, all -> 0x116d }] */
    /* JADX WARNING: Removed duplicated region for block: B:996:0x1143 A[Catch:{ Exception -> 0x1167, all -> 0x116d }] */
    /* JADX WARNING: Removed duplicated region for block: B:999:0x114e A[Catch:{ Exception -> 0x1167, all -> 0x116d }] */
    @android.annotation.TargetApi(18)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideoInternal(java.lang.String r75, java.io.File r76, int r77, boolean r78, int r79, int r80, int r81, int r82, int r83, int r84, int r85, long r86, long r88, long r90, long r92, boolean r94, boolean r95, org.telegram.messenger.MediaController.SavedFilterState r96, java.lang.String r97, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r98, boolean r99, org.telegram.messenger.MediaController.CropState r100) {
        /*
            r74 = this;
            r15 = r74
            r14 = r75
            r13 = r77
            r12 = r79
            r11 = r80
            r9 = r81
            r10 = r82
            r8 = r83
            r7 = r84
            r6 = r85
            r4 = r86
            r2 = r92
            r1 = r94
            long r16 = java.lang.System.currentTimeMillis()
            r18 = -1
            android.media.MediaCodec$BufferInfo r13 = new android.media.MediaCodec$BufferInfo     // Catch:{ all -> 0x1469 }
            r13.<init>()     // Catch:{ all -> 0x1469 }
            org.telegram.messenger.video.Mp4Movie r6 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ all -> 0x1469 }
            r6.<init>()     // Catch:{ all -> 0x1469 }
            r23 = r13
            r13 = r76
            r6.setCacheFile(r13)     // Catch:{ all -> 0x1469 }
            r11 = 0
            r6.setRotation(r11)     // Catch:{ all -> 0x1469 }
            r6.setSize(r9, r10)     // Catch:{ all -> 0x1469 }
            org.telegram.messenger.video.MP4Builder r11 = new org.telegram.messenger.video.MP4Builder     // Catch:{ all -> 0x1469 }
            r11.<init>()     // Catch:{ all -> 0x1469 }
            r13 = r78
            org.telegram.messenger.video.MP4Builder r6 = r11.createMovie(r6, r13)     // Catch:{ all -> 0x1469 }
            r15.mediaMuxer = r6     // Catch:{ all -> 0x1469 }
            float r6 = (float) r2     // Catch:{ all -> 0x1469 }
            r24 = 1148846080(0x447a0000, float:1000.0)
            float r25 = r6 / r24
            r26 = 1000(0x3e8, double:4.94E-321)
            long r4 = r2 * r26
            r15.endPresentationTime = r4     // Catch:{ all -> 0x1469 }
            r74.checkConversionCanceled()     // Catch:{ all -> 0x1469 }
            java.lang.String r6 = "csd-0"
            java.lang.String r5 = "prepend-sps-pps-to-idr-frames"
            java.lang.String r13 = "video/avc"
            r14 = 0
            if (r99 == 0) goto L_0x06a2
            int r18 = (r90 > r14 ? 1 : (r90 == r14 ? 0 : -1))
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
            if (r4 == 0) goto L_0x00d5
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00cd, all -> 0x00c2 }
            if (r4 == 0) goto L_0x00b7
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00cd, all -> 0x00c2 }
            r4.<init>()     // Catch:{ Exception -> 0x00cd, all -> 0x00c2 }
            java.lang.String r11 = "changing width from "
            r4.append(r11)     // Catch:{ Exception -> 0x00cd, all -> 0x00c2 }
            r4.append(r9)     // Catch:{ Exception -> 0x00cd, all -> 0x00c2 }
            java.lang.String r11 = " to "
            r4.append(r11)     // Catch:{ Exception -> 0x00cd, all -> 0x00c2 }
            float r11 = (float) r9     // Catch:{ Exception -> 0x00cd, all -> 0x00c2 }
            float r11 = r11 / r18
            int r11 = java.lang.Math.round(r11)     // Catch:{ Exception -> 0x00cd, all -> 0x00c2 }
            int r11 = r11 * 16
            r4.append(r11)     // Catch:{ Exception -> 0x00cd, all -> 0x00c2 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x00cd, all -> 0x00c2 }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x00cd, all -> 0x00c2 }
        L_0x00b7:
            float r4 = (float) r9     // Catch:{ Exception -> 0x00cd, all -> 0x00c2 }
            float r4 = r4 / r18
            int r4 = java.lang.Math.round(r4)     // Catch:{ Exception -> 0x00cd, all -> 0x00c2 }
            int r4 = r4 * 16
            r11 = r4
            goto L_0x00d6
        L_0x00c2:
            r0 = move-exception
            r15 = r74
            r43 = r88
            r26 = r90
            r2 = r0
            r12 = r8
            goto L_0x05e3
        L_0x00cd:
            r0 = move-exception
            r13 = r74
            r1 = r0
            r35 = r7
            goto L_0x05ed
        L_0x00d5:
            r11 = r9
        L_0x00d6:
            int r4 = r10 % 16
            if (r4 == 0) goto L_0x0123
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            if (r4 == 0) goto L_0x0103
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            r4.<init>()     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            java.lang.String r9 = "changing height from "
            r4.append(r9)     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            r4.append(r10)     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            java.lang.String r9 = " to "
            r4.append(r9)     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            float r9 = (float) r10     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            float r9 = r9 / r18
            int r9 = java.lang.Math.round(r9)     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            int r9 = r9 * 16
            r4.append(r9)     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x011b, all -> 0x010e }
        L_0x0103:
            float r4 = (float) r10     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            float r4 = r4 / r18
            int r4 = java.lang.Math.round(r4)     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            int r4 = r4 * 16
            r10 = r4
            goto L_0x0123
        L_0x010e:
            r0 = move-exception
            r15 = r74
            r43 = r88
            r26 = r90
            r2 = r0
            r12 = r8
            r3 = r10
            r1 = r11
            goto L_0x1476
        L_0x011b:
            r0 = move-exception
            r13 = r74
            r1 = r0
            r35 = r7
            goto L_0x05ee
        L_0x0123:
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05b1, all -> 0x05a1 }
            if (r4 == 0) goto L_0x014b
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            r4.<init>()     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            java.lang.String r9 = "create photo encoder "
            r4.append(r9)     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            r4.append(r11)     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            java.lang.String r9 = " "
            r4.append(r9)     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            r4.append(r10)     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            java.lang.String r9 = " duration = "
            r4.append(r9)     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            r4.append(r2)     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x011b, all -> 0x010e }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x011b, all -> 0x010e }
        L_0x014b:
            android.media.MediaFormat r4 = android.media.MediaFormat.createVideoFormat(r13, r11, r10)     // Catch:{ Exception -> 0x05b1, all -> 0x05a1 }
            java.lang.String r9 = "color-format"
            r14 = 2130708361(0x7var_, float:1.701803E38)
            r4.setInteger(r9, r14)     // Catch:{ Exception -> 0x05b1, all -> 0x05a1 }
            java.lang.String r9 = "bitrate"
            r4.setInteger(r9, r7)     // Catch:{ Exception -> 0x05b1, all -> 0x05a1 }
            java.lang.String r9 = "frame-rate"
            r14 = 30
            r4.setInteger(r9, r14)     // Catch:{ Exception -> 0x05b1, all -> 0x05a1 }
            java.lang.String r9 = "i-frame-interval"
            r14 = 1
            r4.setInteger(r9, r14)     // Catch:{ Exception -> 0x05b1, all -> 0x05a1 }
            android.media.MediaCodec r15 = android.media.MediaCodec.createEncoderByType(r13)     // Catch:{ Exception -> 0x05b1, all -> 0x05a1 }
            r9 = 0
            r15.configure(r4, r9, r9, r14)     // Catch:{ Exception -> 0x0594, all -> 0x05a1 }
            org.telegram.messenger.video.InputSurface r9 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x0594, all -> 0x05a1 }
            android.view.Surface r4 = r15.createInputSurface()     // Catch:{ Exception -> 0x0594, all -> 0x05a1 }
            r9.<init>(r4)     // Catch:{ Exception -> 0x0594, all -> 0x05a1 }
            r9.makeCurrent()     // Catch:{ Exception -> 0x0582, all -> 0x05a1 }
            r15.start()     // Catch:{ Exception -> 0x0582, all -> 0x05a1 }
            org.telegram.messenger.video.OutputSurface r18 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x0582, all -> 0x05a1 }
            r19 = 0
            float r4 = (float) r8
            r21 = 1
            r1 = r18
            r2 = r96
            r3 = r75
            r32 = r4
            r4 = r97
            r14 = r5
            r5 = r98
            r34 = r6
            r6 = r19
            r35 = r7
            r7 = r11
            r8 = r10
            r36 = r9
            r9 = r79
            r37 = r10
            r10 = r80
            r38 = r11
            r11 = r77
            r12 = r32
            r41 = r13
            r20 = r14
            r14 = r23
            r13 = r21
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x0577, all -> 0x0567 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x055b, all -> 0x0567 }
            r2 = 21
            if (r1 >= r2) goto L_0x01dc
            java.nio.ByteBuffer[] r1 = r15.getOutputBuffers()     // Catch:{ Exception -> 0x01d0, all -> 0x01c0 }
            goto L_0x01dd
        L_0x01c0:
            r0 = move-exception
            r15 = r74
            r12 = r83
            r43 = r88
            r26 = r90
            r2 = r0
            r7 = r35
            r3 = r37
            goto L_0x05ca
        L_0x01d0:
            r0 = move-exception
            r13 = r74
            r1 = r0
            r14 = r15
            r3 = r37
            r11 = r38
            r8 = -5
            goto L_0x05f5
        L_0x01dc:
            r1 = 0
        L_0x01dd:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x055b, all -> 0x0567 }
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 1
            r6 = 0
            r13 = -5
        L_0x01e6:
            if (r6 != 0) goto L_0x0548
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x0539, all -> 0x0525 }
            r7 = r2 ^ 1
            r8 = r13
            r13 = 1
            r71 = r7
            r7 = r6
            r6 = r71
        L_0x01f4:
            if (r6 != 0) goto L_0x01fc
            if (r13 == 0) goto L_0x01f9
            goto L_0x01fc
        L_0x01f9:
            r6 = r7
            r13 = r8
            goto L_0x01e6
        L_0x01fc:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x051c, all -> 0x050e }
            if (r95 == 0) goto L_0x0204
            r9 = 22000(0x55f0, double:1.08694E-319)
            goto L_0x0206
        L_0x0204:
            r9 = 2500(0x9c4, double:1.235E-320)
        L_0x0206:
            int r9 = r15.dequeueOutputBuffer(r14, r9)     // Catch:{ Exception -> 0x051c, all -> 0x050e }
            r10 = -1
            if (r9 != r10) goto L_0x021f
            r10 = 0
            r13 = r74
            r19 = r3
            r84 = r6
            r12 = r34
        L_0x0216:
            r3 = r37
            r11 = r41
        L_0x021a:
            r6 = r5
            r5 = r1
            r1 = -1
            goto L_0x043d
        L_0x021f:
            r10 = -3
            if (r9 != r10) goto L_0x0256
            int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x024b, all -> 0x023a }
            r11 = 21
            if (r10 >= r11) goto L_0x022c
            java.nio.ByteBuffer[] r1 = r15.getOutputBuffers()     // Catch:{ Exception -> 0x024b, all -> 0x023a }
        L_0x022c:
            r19 = r3
            r84 = r6
            r10 = r13
            r12 = r34
            r3 = r37
            r11 = r41
            r13 = r74
            goto L_0x021a
        L_0x023a:
            r0 = move-exception
            r15 = r74
            r12 = r83
            r43 = r88
            r26 = r90
            r2 = r0
            r13 = r8
            r7 = r35
            r3 = r37
            goto L_0x0535
        L_0x024b:
            r0 = move-exception
            r13 = r74
        L_0x024e:
            r1 = r0
            r14 = r15
            r3 = r37
        L_0x0252:
            r11 = r38
            goto L_0x05f5
        L_0x0256:
            r10 = -2
            if (r9 != r10) goto L_0x02bf
            android.media.MediaFormat r10 = r15.getOutputFormat()     // Catch:{ Exception -> 0x024b, all -> 0x023a }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x024b, all -> 0x023a }
            if (r11 == 0) goto L_0x0275
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x024b, all -> 0x023a }
            r11.<init>()     // Catch:{ Exception -> 0x024b, all -> 0x023a }
            java.lang.String r12 = "photo encoder new format "
            r11.append(r12)     // Catch:{ Exception -> 0x024b, all -> 0x023a }
            r11.append(r10)     // Catch:{ Exception -> 0x024b, all -> 0x023a }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x024b, all -> 0x023a }
            org.telegram.messenger.FileLog.d(r11)     // Catch:{ Exception -> 0x024b, all -> 0x023a }
        L_0x0275:
            r11 = -5
            if (r8 != r11) goto L_0x02b1
            if (r10 == 0) goto L_0x02b1
            r81 = r13
            r13 = r74
            org.telegram.messenger.video.MP4Builder r11 = r13.mediaMuxer     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            r12 = 0
            int r8 = r11.addTrack(r10, r12)     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            r11 = r20
            boolean r19 = r10.containsKey(r11)     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            if (r19 == 0) goto L_0x02ac
            int r12 = r10.getInteger(r11)     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            r20 = r11
            r11 = 1
            if (r12 != r11) goto L_0x02ae
            r12 = r34
            java.nio.ByteBuffer r3 = r10.getByteBuffer(r12)     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            java.lang.String r11 = "csd-1"
            java.nio.ByteBuffer r10 = r10.getByteBuffer(r11)     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            int r3 = r3.limit()     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            int r10 = r10.limit()     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            int r3 = r3 + r10
            goto L_0x02b7
        L_0x02ac:
            r20 = r11
        L_0x02ae:
            r12 = r34
            goto L_0x02b7
        L_0x02b1:
            r81 = r13
            r12 = r34
            r13 = r74
        L_0x02b7:
            r10 = r81
            r19 = r3
            r84 = r6
            goto L_0x0216
        L_0x02bf:
            r81 = r13
            r12 = r34
            r13 = r74
            if (r9 < 0) goto L_0x04ed
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x04eb, all -> 0x04da }
            r10 = 21
            if (r7 >= r10) goto L_0x02e2
            r7 = r1[r9]     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            goto L_0x02e6
        L_0x02d0:
            r0 = move-exception
            r12 = r83
            r43 = r88
            r26 = r90
            r2 = r0
            r15 = r13
            r7 = r35
            r3 = r37
            goto L_0x04e7
        L_0x02df:
            r0 = move-exception
            goto L_0x024e
        L_0x02e2:
            java.nio.ByteBuffer r7 = r15.getOutputBuffer(r9)     // Catch:{ Exception -> 0x04eb, all -> 0x04da }
        L_0x02e6:
            if (r7 == 0) goto L_0x04ba
            int r10 = r14.size     // Catch:{ Exception -> 0x04b4, all -> 0x04da }
            r11 = 1
            if (r10 <= r11) goto L_0x041b
            int r11 = r14.flags     // Catch:{ Exception -> 0x0412, all -> 0x040b }
            r19 = r11 & 2
            if (r19 != 0) goto L_0x0382
            if (r3 == 0) goto L_0x0304
            r19 = r11 & 1
            if (r19 == 0) goto L_0x0304
            r82 = r1
            int r1 = r14.offset     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            int r1 = r1 + r3
            r14.offset = r1     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            int r10 = r10 - r3
            r14.size = r10     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            goto L_0x0306
        L_0x0304:
            r82 = r1
        L_0x0306:
            if (r5 == 0) goto L_0x0354
            r1 = r11 & 1
            if (r1 == 0) goto L_0x0354
            int r1 = r14.size     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            r11 = 100
            if (r1 <= r11) goto L_0x0353
            int r1 = r14.offset     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            r7.position(r1)     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            byte[] r1 = new byte[r11]     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            r7.get(r1)     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            r5 = 0
            r10 = 0
        L_0x031e:
            r11 = 96
            if (r5 >= r11) goto L_0x0353
            byte r11 = r1[r5]     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            if (r11 != 0) goto L_0x034a
            int r11 = r5 + 1
            byte r11 = r1[r11]     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            if (r11 != 0) goto L_0x034a
            int r11 = r5 + 2
            byte r11 = r1[r11]     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            if (r11 != 0) goto L_0x034a
            int r11 = r5 + 3
            byte r11 = r1[r11]     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            r19 = r1
            r1 = 1
            if (r11 != r1) goto L_0x034c
            int r10 = r10 + 1
            if (r10 <= r1) goto L_0x034c
            int r1 = r14.offset     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            int r1 = r1 + r5
            r14.offset = r1     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            int r1 = r14.size     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            int r1 = r1 - r5
            r14.size = r1     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            goto L_0x0353
        L_0x034a:
            r19 = r1
        L_0x034c:
            int r5 = r5 + 1
            r1 = r19
            r11 = 100
            goto L_0x031e
        L_0x0353:
            r5 = 0
        L_0x0354:
            org.telegram.messenger.video.MP4Builder r1 = r13.mediaMuxer     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            r11 = r5
            r84 = r6
            r10 = 1
            long r5 = r1.writeSampleData(r8, r7, r14, r10)     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            r1 = r11
            r10 = 0
            int r7 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r7 == 0) goto L_0x0374
            org.telegram.messenger.MediaController$VideoConvertorListener r7 = r13.callback     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            if (r7 == 0) goto L_0x0374
            r19 = r1
            float r1 = (float) r10     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            float r1 = r1 / r24
            float r1 = r1 / r25
            r7.didWriteData(r5, r1)     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            goto L_0x0376
        L_0x0374:
            r19 = r1
        L_0x0376:
            r5 = r19
            r1 = r38
            r11 = r41
            r19 = r3
            r3 = r37
            goto L_0x0427
        L_0x0382:
            r82 = r1
            r84 = r6
            r1 = -5
            if (r8 != r1) goto L_0x0408
            byte[] r1 = new byte[r10]     // Catch:{ Exception -> 0x0412, all -> 0x040b }
            int r6 = r14.offset     // Catch:{ Exception -> 0x0412, all -> 0x040b }
            int r6 = r6 + r10
            r7.limit(r6)     // Catch:{ Exception -> 0x0412, all -> 0x040b }
            int r6 = r14.offset     // Catch:{ Exception -> 0x0412, all -> 0x040b }
            r7.position(r6)     // Catch:{ Exception -> 0x0412, all -> 0x040b }
            r7.get(r1)     // Catch:{ Exception -> 0x0412, all -> 0x040b }
            int r6 = r14.size     // Catch:{ Exception -> 0x0412, all -> 0x040b }
            r7 = 1
            int r6 = r6 - r7
        L_0x039d:
            if (r6 < 0) goto L_0x03e0
            r10 = 3
            if (r6 <= r10) goto L_0x03e0
            byte r10 = r1[r6]     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            if (r10 != r7) goto L_0x03d8
            int r7 = r6 + -1
            byte r7 = r1[r7]     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            if (r7 != 0) goto L_0x03d8
            int r7 = r6 + -2
            byte r7 = r1[r7]     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            if (r7 != 0) goto L_0x03d8
            int r7 = r6 + -3
            byte r10 = r1[r7]     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            if (r10 != 0) goto L_0x03d8
            java.nio.ByteBuffer r6 = java.nio.ByteBuffer.allocate(r7)     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            int r10 = r14.size     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            int r10 = r10 - r7
            java.nio.ByteBuffer r10 = java.nio.ByteBuffer.allocate(r10)     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            r19 = r3
            r11 = 0
            java.nio.ByteBuffer r3 = r6.put(r1, r11, r7)     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            r3.position(r11)     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            int r3 = r14.size     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            int r3 = r3 - r7
            java.nio.ByteBuffer r1 = r10.put(r1, r7, r3)     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            r1.position(r11)     // Catch:{ Exception -> 0x02df, all -> 0x02d0 }
            goto L_0x03e4
        L_0x03d8:
            r19 = r3
            int r6 = r6 + -1
            r3 = r19
            r7 = 1
            goto L_0x039d
        L_0x03e0:
            r19 = r3
            r6 = 0
            r10 = 0
        L_0x03e4:
            r3 = r37
            r1 = r38
            r11 = r41
            android.media.MediaFormat r7 = android.media.MediaFormat.createVideoFormat(r11, r1, r3)     // Catch:{ Exception -> 0x0406, all -> 0x0403 }
            if (r6 == 0) goto L_0x03fa
            if (r10 == 0) goto L_0x03fa
            r7.setByteBuffer(r12, r6)     // Catch:{ Exception -> 0x0406, all -> 0x0403 }
            java.lang.String r6 = "csd-1"
            r7.setByteBuffer(r6, r10)     // Catch:{ Exception -> 0x0406, all -> 0x0403 }
        L_0x03fa:
            org.telegram.messenger.video.MP4Builder r6 = r13.mediaMuxer     // Catch:{ Exception -> 0x0406, all -> 0x0403 }
            r10 = 0
            int r6 = r6.addTrack(r7, r10)     // Catch:{ Exception -> 0x0406, all -> 0x0403 }
            r8 = r6
            goto L_0x0427
        L_0x0403:
            r0 = move-exception
            goto L_0x04a4
        L_0x0406:
            r0 = move-exception
            goto L_0x0417
        L_0x0408:
            r19 = r3
            goto L_0x0421
        L_0x040b:
            r0 = move-exception
            r3 = r37
            r1 = r38
            goto L_0x04a4
        L_0x0412:
            r0 = move-exception
            r3 = r37
            r1 = r38
        L_0x0417:
            r11 = r1
            r14 = r15
            goto L_0x0591
        L_0x041b:
            r82 = r1
            r19 = r3
            r84 = r6
        L_0x0421:
            r3 = r37
            r1 = r38
            r11 = r41
        L_0x0427:
            int r6 = r14.flags     // Catch:{ Exception -> 0x04b0, all -> 0x04a1 }
            r6 = r6 & 4
            if (r6 == 0) goto L_0x042f
            r6 = 1
            goto L_0x0430
        L_0x042f:
            r6 = 0
        L_0x0430:
            r7 = 0
            r15.releaseOutputBuffer(r9, r7)     // Catch:{ Exception -> 0x04b0, all -> 0x04a1 }
            r10 = r81
            r38 = r1
            r7 = r6
            r1 = -1
            r6 = r5
            r5 = r82
        L_0x043d:
            if (r9 == r1) goto L_0x044e
            r37 = r3
            r1 = r5
            r5 = r6
            r13 = r10
            r41 = r11
            r34 = r12
            r3 = r19
            r6 = r84
            goto L_0x01f4
        L_0x044e:
            if (r2 != 0) goto L_0x0488
            r18.drawImage()     // Catch:{ Exception -> 0x0481, all -> 0x0508 }
            float r1 = (float) r4
            r9 = 1106247680(0x41var_, float:30.0)
            float r1 = r1 / r9
            float r1 = r1 * r24
            float r1 = r1 * r24
            float r1 = r1 * r24
            r81 = r2
            long r1 = (long) r1
            r9 = r36
            r9.setPresentationTime(r1)     // Catch:{ Exception -> 0x047c, all -> 0x0508 }
            r9.swapBuffers()     // Catch:{ Exception -> 0x047c, all -> 0x0508 }
            int r4 = r4 + 1
            float r1 = (float) r4     // Catch:{ Exception -> 0x047c, all -> 0x0508 }
            r2 = 1106247680(0x41var_, float:30.0)
            float r2 = r2 * r25
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 < 0) goto L_0x0479
            r15.signalEndOfInputStream()     // Catch:{ Exception -> 0x047c, all -> 0x0508 }
            r1 = 0
            r2 = 1
            goto L_0x048e
        L_0x0479:
            r2 = r81
            goto L_0x048c
        L_0x047c:
            r0 = move-exception
            r1 = r0
            r36 = r9
            goto L_0x0485
        L_0x0481:
            r0 = move-exception
        L_0x0482:
            r9 = r36
        L_0x0484:
            r1 = r0
        L_0x0485:
            r14 = r15
            goto L_0x0252
        L_0x0488:
            r81 = r2
            r9 = r36
        L_0x048c:
            r1 = r84
        L_0x048e:
            r37 = r3
            r36 = r9
            r13 = r10
            r41 = r11
            r34 = r12
            r3 = r19
            r71 = r6
            r6 = r1
            r1 = r5
            r5 = r71
            goto L_0x01f4
        L_0x04a1:
            r0 = move-exception
            r38 = r1
        L_0x04a4:
            r12 = r83
            r43 = r88
            r26 = r90
            r2 = r0
            r15 = r13
            r7 = r35
            goto L_0x069e
        L_0x04b0:
            r0 = move-exception
            r38 = r1
            goto L_0x0482
        L_0x04b4:
            r0 = move-exception
            r9 = r36
            r3 = r37
            goto L_0x0484
        L_0x04ba:
            r1 = r36
            r3 = r37
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x050a, all -> 0x0508 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x050a, all -> 0x0508 }
            r4.<init>()     // Catch:{ Exception -> 0x050a, all -> 0x0508 }
            java.lang.String r5 = "encoderOutputBuffer "
            r4.append(r5)     // Catch:{ Exception -> 0x050a, all -> 0x0508 }
            r4.append(r9)     // Catch:{ Exception -> 0x050a, all -> 0x0508 }
            java.lang.String r5 = " was null"
            r4.append(r5)     // Catch:{ Exception -> 0x050a, all -> 0x0508 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x050a, all -> 0x0508 }
            r2.<init>(r4)     // Catch:{ Exception -> 0x050a, all -> 0x0508 }
            throw r2     // Catch:{ Exception -> 0x050a, all -> 0x0508 }
        L_0x04da:
            r0 = move-exception
            r3 = r37
        L_0x04dd:
            r12 = r83
            r43 = r88
            r26 = r90
            r2 = r0
            r15 = r13
            r7 = r35
        L_0x04e7:
            r1 = r38
            goto L_0x069e
        L_0x04eb:
            r0 = move-exception
            goto L_0x051f
        L_0x04ed:
            r1 = r36
            r3 = r37
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x050a, all -> 0x0508 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x050a, all -> 0x0508 }
            r4.<init>()     // Catch:{ Exception -> 0x050a, all -> 0x0508 }
            java.lang.String r5 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x050a, all -> 0x0508 }
            r4.append(r9)     // Catch:{ Exception -> 0x050a, all -> 0x0508 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x050a, all -> 0x0508 }
            r2.<init>(r4)     // Catch:{ Exception -> 0x050a, all -> 0x0508 }
            throw r2     // Catch:{ Exception -> 0x050a, all -> 0x0508 }
        L_0x0508:
            r0 = move-exception
            goto L_0x04dd
        L_0x050a:
            r0 = move-exception
            r36 = r1
            goto L_0x0523
        L_0x050e:
            r0 = move-exception
            r3 = r37
            r15 = r74
            r12 = r83
            r43 = r88
            r26 = r90
            r2 = r0
            r13 = r8
            goto L_0x0533
        L_0x051c:
            r0 = move-exception
            r13 = r74
        L_0x051f:
            r1 = r36
            r3 = r37
        L_0x0523:
            r14 = r15
            goto L_0x0545
        L_0x0525:
            r0 = move-exception
            r22 = r13
            r3 = r37
            r15 = r74
            r12 = r83
            r43 = r88
            r26 = r90
            r2 = r0
        L_0x0533:
            r7 = r35
        L_0x0535:
            r1 = r38
            goto L_0x13fe
        L_0x0539:
            r0 = move-exception
            r22 = r13
            r1 = r36
            r3 = r37
            r13 = r74
            r14 = r15
            r8 = r22
        L_0x0545:
            r11 = r38
            goto L_0x0591
        L_0x0548:
            r22 = r13
            r1 = r36
            r3 = r37
            r13 = r74
            r9 = r1
            r14 = r15
            r4 = r35
            r6 = 0
            r33 = 0
            r15 = r83
            goto L_0x063d
        L_0x055b:
            r0 = move-exception
            r13 = r74
            r1 = r36
            r3 = r37
            r14 = r15
            r11 = r38
            r8 = -5
            goto L_0x0591
        L_0x0567:
            r0 = move-exception
            r3 = r37
            r15 = r74
            r12 = r83
            r43 = r88
            r26 = r90
            r2 = r0
            r7 = r35
            goto L_0x05ca
        L_0x0577:
            r0 = move-exception
            r13 = r74
            r1 = r36
            r3 = r37
            r14 = r15
            r11 = r38
            goto L_0x058e
        L_0x0582:
            r0 = move-exception
            r13 = r74
            r35 = r7
            r1 = r9
            r3 = r10
            r38 = r11
            r36 = r1
            r14 = r15
        L_0x058e:
            r8 = -5
            r18 = 0
        L_0x0591:
            r1 = r0
            goto L_0x05f5
        L_0x0594:
            r0 = move-exception
            r13 = r74
            r35 = r7
            r3 = r10
            r38 = r11
            r1 = r0
            r14 = r15
            r8 = -5
            goto L_0x05f1
        L_0x05a1:
            r0 = move-exception
            r35 = r7
            r3 = r10
            r38 = r11
            r15 = r74
            r12 = r83
            r43 = r88
            r26 = r90
            r2 = r0
            goto L_0x05ca
        L_0x05b1:
            r0 = move-exception
            r13 = r74
            r35 = r7
            r3 = r10
            r38 = r11
            r1 = r0
            goto L_0x05ef
        L_0x05bb:
            r0 = move-exception
            r35 = r7
            r38 = r11
            r15 = r74
            r12 = r83
            r43 = r88
            r26 = r90
            r2 = r0
            r3 = r10
        L_0x05ca:
            r1 = r38
            goto L_0x1476
        L_0x05ce:
            r0 = move-exception
            r13 = r74
            r35 = r7
            r38 = r11
            r1 = r0
            goto L_0x05ee
        L_0x05d7:
            r0 = move-exception
            r35 = r7
            r15 = r74
            r12 = r83
            r43 = r88
            r26 = r90
            r2 = r0
        L_0x05e3:
            r1 = r9
            r3 = r10
            goto L_0x1476
        L_0x05e7:
            r0 = move-exception
            r13 = r74
            r35 = r7
            r1 = r0
        L_0x05ed:
            r11 = r9
        L_0x05ee:
            r3 = r10
        L_0x05ef:
            r8 = -5
            r14 = 0
        L_0x05f1:
            r18 = 0
            r36 = 0
        L_0x05f5:
            boolean r2 = r1 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x0691 }
            if (r2 == 0) goto L_0x05fe
            if (r95 != 0) goto L_0x05fe
            r33 = 1
            goto L_0x0600
        L_0x05fe:
            r33 = 0
        L_0x0600:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0681 }
            r2.<init>()     // Catch:{ all -> 0x0681 }
            java.lang.String r4 = "bitrate: "
            r2.append(r4)     // Catch:{ all -> 0x0681 }
            r4 = r35
            r2.append(r4)     // Catch:{ all -> 0x067f }
            java.lang.String r5 = " framerate: "
            r2.append(r5)     // Catch:{ all -> 0x067f }
            r15 = r83
            r2.append(r15)     // Catch:{ all -> 0x0672 }
            java.lang.String r5 = " size: "
            r2.append(r5)     // Catch:{ all -> 0x0672 }
            r2.append(r3)     // Catch:{ all -> 0x0672 }
            java.lang.String r5 = "x"
            r2.append(r5)     // Catch:{ all -> 0x0672 }
            r2.append(r11)     // Catch:{ all -> 0x0672 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0672 }
            org.telegram.messenger.FileLog.e((java.lang.String) r2)     // Catch:{ all -> 0x0672 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x0672 }
            r22 = r8
            r38 = r11
            r6 = r33
            r9 = r36
            r33 = 1
        L_0x063d:
            if (r18 == 0) goto L_0x0652
            r18.release()     // Catch:{ all -> 0x0643 }
            goto L_0x0652
        L_0x0643:
            r0 = move-exception
            r43 = r88
            r26 = r90
            r2 = r0
            r7 = r4
            r12 = r15
            r1 = r38
            r15 = r13
            r13 = r22
            goto L_0x1478
        L_0x0652:
            if (r9 == 0) goto L_0x0657
            r9.release()     // Catch:{ all -> 0x0643 }
        L_0x0657:
            if (r14 == 0) goto L_0x065f
            r14.stop()     // Catch:{ all -> 0x0643 }
            r14.release()     // Catch:{ all -> 0x0643 }
        L_0x065f:
            r74.checkConversionCanceled()     // Catch:{ all -> 0x0643 }
            r43 = r88
            r26 = r90
            r21 = r4
            r12 = r15
            r14 = r33
            r5 = r38
            r15 = r13
            r13 = r22
            goto L_0x1440
        L_0x0672:
            r0 = move-exception
            r43 = r88
            r26 = r90
            r2 = r0
            r7 = r4
            r1 = r11
            r12 = r15
            r6 = r33
            r15 = r13
            goto L_0x069f
        L_0x067f:
            r0 = move-exception
            goto L_0x0684
        L_0x0681:
            r0 = move-exception
            r4 = r35
        L_0x0684:
            r12 = r83
            r43 = r88
            r26 = r90
            r2 = r0
            r7 = r4
            r1 = r11
            r15 = r13
            r6 = r33
            goto L_0x069f
        L_0x0691:
            r0 = move-exception
            r4 = r35
            r12 = r83
            r43 = r88
            r26 = r90
            r2 = r0
            r7 = r4
            r1 = r11
            r15 = r13
        L_0x069e:
            r6 = 0
        L_0x069f:
            r13 = r8
            goto L_0x1478
        L_0x06a2:
            r20 = r5
            r12 = r6
            r15 = r8
            r11 = r13
            r14 = r23
            r13 = r74
            android.media.MediaExtractor r1 = new android.media.MediaExtractor     // Catch:{ all -> 0x1463 }
            r1.<init>()     // Catch:{ all -> 0x1463 }
            r13.extractor = r1     // Catch:{ all -> 0x1463 }
            r8 = r75
            r1.setDataSource(r8)     // Catch:{ all -> 0x1463 }
            android.media.MediaExtractor r1 = r13.extractor     // Catch:{ all -> 0x1463 }
            r5 = 0
            int r6 = org.telegram.messenger.MediaController.findTrack(r1, r5)     // Catch:{ all -> 0x1463 }
            r1 = -1
            if (r7 == r1) goto L_0x06d7
            android.media.MediaExtractor r1 = r13.extractor     // Catch:{ all -> 0x06ca }
            r3 = 1
            int r1 = org.telegram.messenger.MediaController.findTrack(r1, r3)     // Catch:{ all -> 0x06ca }
            r2 = r1
            goto L_0x06d9
        L_0x06ca:
            r0 = move-exception
            r43 = r88
            r26 = r90
            r2 = r0
            r1 = r9
            r3 = r10
            r12 = r15
        L_0x06d3:
            r6 = 0
        L_0x06d4:
            r15 = r13
            goto L_0x1477
        L_0x06d7:
            r3 = 1
            r2 = -1
        L_0x06d9:
            java.lang.String r1 = "mime"
            if (r6 < 0) goto L_0x06f1
            android.media.MediaExtractor r3 = r13.extractor     // Catch:{ all -> 0x06ca }
            android.media.MediaFormat r3 = r3.getTrackFormat(r6)     // Catch:{ all -> 0x06ca }
            java.lang.String r3 = r3.getString(r1)     // Catch:{ all -> 0x06ca }
            boolean r3 = r3.equals(r11)     // Catch:{ all -> 0x06ca }
            if (r3 != 0) goto L_0x06f1
            r15 = r94
            r3 = 1
            goto L_0x06f4
        L_0x06f1:
            r15 = r94
            r3 = 0
        L_0x06f4:
            if (r15 != 0) goto L_0x073a
            if (r3 == 0) goto L_0x06f9
            goto L_0x073a
        L_0x06f9:
            android.media.MediaExtractor r2 = r13.extractor     // Catch:{ all -> 0x072a }
            org.telegram.messenger.video.MP4Builder r3 = r13.mediaMuxer     // Catch:{ all -> 0x072a }
            r1 = -1
            if (r7 == r1) goto L_0x0702
            r12 = 1
            goto L_0x0703
        L_0x0702:
            r12 = 0
        L_0x0703:
            r1 = r74
            r11 = 1
            r4 = r14
            r14 = 0
            r5 = r86
            r15 = r8
            r7 = r88
            r14 = r9
            r15 = r10
            r9 = r92
            r14 = 1
            r11 = r76
            r14 = 0
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ all -> 0x0728 }
            r5 = r81
            r12 = r83
            r21 = r84
            r43 = r88
            r26 = r90
            r3 = r15
            r6 = 0
            r15 = r13
            r13 = -5
            goto L_0x1440
        L_0x0728:
            r0 = move-exception
            goto L_0x072d
        L_0x072a:
            r0 = move-exception
            r15 = r10
            r14 = 0
        L_0x072d:
            r1 = r81
            r12 = r83
            r7 = r84
            r43 = r88
            r26 = r90
            r2 = r0
            r3 = r15
            goto L_0x06d3
        L_0x073a:
            r15 = r10
            r10 = r14
            r14 = 0
            if (r6 < 0) goto L_0x1401
            r26 = -2147483648(0xfffffffvar_, double:NaN)
            r3 = 1000(0x3e8, float:1.401E-42)
            r9 = r83
            r8 = r94
            int r5 = r3 / r9
            int r5 = r5 * 1000
            long r4 = (long) r5
            r7 = 30
            if (r9 >= r7) goto L_0x0777
            int r7 = r9 + 5
            int r7 = r3 / r7
        L_0x0755:
            int r7 = r7 * 1000
            long r14 = (long) r7
            goto L_0x077c
        L_0x0759:
            r0 = move-exception
            r1 = r81
            r3 = r82
            r7 = r84
            r43 = r88
            r26 = r90
            r2 = r0
            r12 = r9
            r15 = r13
            goto L_0x1476
        L_0x0769:
            r0 = move-exception
            r3 = r84
            r43 = r88
            r26 = r90
            r1 = r0
            r52 = r6
            r12 = r9
            r15 = r13
            goto L_0x1371
        L_0x0777:
            int r7 = r9 + 1
            int r7 = r3 / r7
            goto L_0x0755
        L_0x077c:
            android.media.MediaExtractor r3 = r13.extractor     // Catch:{ Exception -> 0x1365, all -> 0x1355 }
            r3.selectTrack(r6)     // Catch:{ Exception -> 0x1365, all -> 0x1355 }
            android.media.MediaExtractor r3 = r13.extractor     // Catch:{ Exception -> 0x1365, all -> 0x1355 }
            android.media.MediaFormat r7 = r3.getTrackFormat(r6)     // Catch:{ Exception -> 0x1365, all -> 0x1355 }
            r29 = 0
            int r3 = (r90 > r29 ? 1 : (r90 == r29 ? 0 : -1))
            if (r3 < 0) goto L_0x07aa
            r3 = 1157234688(0x44fa0000, float:2000.0)
            int r3 = (r25 > r3 ? 1 : (r25 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x0797
            r3 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x07a5
        L_0x0797:
            r3 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r3 = (r25 > r3 ? 1 : (r25 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x07a2
            r3 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x07a5
        L_0x07a2:
            r3 = 1560000(0x17cdc0, float:2.186026E-39)
        L_0x07a5:
            r36 = r14
            r34 = 0
            goto L_0x07ba
        L_0x07aa:
            if (r84 > 0) goto L_0x07b4
            r34 = r90
            r36 = r14
            r3 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x07ba
        L_0x07b4:
            r3 = r84
            r34 = r90
            r36 = r14
        L_0x07ba:
            r14 = r85
            if (r14 <= 0) goto L_0x07e0
            int r3 = java.lang.Math.min(r14, r3)     // Catch:{ Exception -> 0x07d4, all -> 0x07c3 }
            goto L_0x07e0
        L_0x07c3:
            r0 = move-exception
            r1 = r81
            r43 = r88
            r2 = r0
            r7 = r3
            r12 = r9
            r15 = r13
            r26 = r34
        L_0x07ce:
            r6 = 0
            r13 = -5
        L_0x07d0:
            r3 = r82
            goto L_0x1478
        L_0x07d4:
            r0 = move-exception
            r43 = r88
            r1 = r0
            r52 = r6
            r12 = r9
            r15 = r13
            r26 = r34
            goto L_0x1371
        L_0x07e0:
            r29 = 0
            int r15 = (r34 > r29 ? 1 : (r34 == r29 ? 0 : -1))
            if (r15 < 0) goto L_0x07e9
            r14 = r18
            goto L_0x07eb
        L_0x07e9:
            r14 = r34
        L_0x07eb:
            int r23 = (r14 > r29 ? 1 : (r14 == r29 ? 0 : -1))
            if (r23 < 0) goto L_0x0827
            r23 = r2
            android.media.MediaExtractor r2 = r13.extractor     // Catch:{ Exception -> 0x0812, all -> 0x0803 }
            r34 = r4
            r4 = 0
            r2.seekTo(r14, r4)     // Catch:{ Exception -> 0x0812, all -> 0x0803 }
            r4 = r86
            r29 = r6
            r32 = r12
            r12 = 0
            goto L_0x0858
        L_0x0803:
            r0 = move-exception
            r1 = r81
            r43 = r88
            r2 = r0
            r7 = r3
            r12 = r9
            r26 = r14
            r6 = 0
            r3 = r82
            goto L_0x06d4
        L_0x0812:
            r0 = move-exception
            r43 = r88
            r1 = r0
            r52 = r6
            r12 = r9
            r26 = r14
        L_0x081b:
            r10 = 0
            r14 = 0
            r51 = 0
            r59 = 0
            r69 = 0
            r15 = r13
            r13 = -5
            goto L_0x137a
        L_0x0827:
            r23 = r2
            r34 = r4
            r29 = 0
            r4 = r86
            int r2 = (r4 > r29 ? 1 : (r4 == r29 ? 0 : -1))
            if (r2 <= 0) goto L_0x084c
            android.media.MediaExtractor r2 = r13.extractor     // Catch:{ Exception -> 0x0812, all -> 0x0803 }
            r29 = r6
            r6 = 0
            r2.seekTo(r4, r6)     // Catch:{ Exception -> 0x0842, all -> 0x0803 }
            r6 = r100
            r32 = r12
            r12 = 0
            goto L_0x085a
        L_0x0842:
            r0 = move-exception
            r43 = r88
            r1 = r0
            r12 = r9
            r26 = r14
            r52 = r29
            goto L_0x081b
        L_0x084c:
            r29 = r6
            android.media.MediaExtractor r2 = r13.extractor     // Catch:{ Exception -> 0x134d, all -> 0x133e }
            r32 = r12
            r6 = 0
            r12 = 0
            r2.seekTo(r12, r6)     // Catch:{ Exception -> 0x1335, all -> 0x132e }
        L_0x0858:
            r6 = r100
        L_0x085a:
            if (r6 == 0) goto L_0x089c
            r2 = 90
            r13 = r77
            if (r13 == r2) goto L_0x086c
            r2 = 270(0x10e, float:3.78E-43)
            if (r13 != r2) goto L_0x0867
            goto L_0x086c
        L_0x0867:
            int r2 = r6.transformWidth     // Catch:{ Exception -> 0x0886, all -> 0x0876 }
            int r12 = r6.transformHeight     // Catch:{ Exception -> 0x0886, all -> 0x0876 }
            goto L_0x0870
        L_0x086c:
            int r2 = r6.transformHeight     // Catch:{ Exception -> 0x0886, all -> 0x0876 }
            int r12 = r6.transformWidth     // Catch:{ Exception -> 0x0886, all -> 0x0876 }
        L_0x0870:
            r71 = r12
            r12 = r2
            r2 = r71
            goto L_0x08a2
        L_0x0876:
            r0 = move-exception
            r1 = r81
            r43 = r88
            r2 = r0
            r7 = r3
            r12 = r9
            r26 = r14
            r6 = 0
            r13 = -5
            r15 = r74
            goto L_0x07d0
        L_0x0886:
            r0 = move-exception
            r43 = r88
            r1 = r0
        L_0x088a:
            r12 = r9
            r26 = r14
            r52 = r29
            r10 = 0
            r13 = -5
            r14 = 0
            r51 = 0
            r59 = 0
            r69 = 0
            r15 = r74
            goto L_0x137a
        L_0x089c:
            r13 = r77
            r12 = r81
            r2 = r82
        L_0x08a2:
            boolean r38 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1335, all -> 0x132e }
            if (r38 == 0) goto L_0x08c2
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0886, all -> 0x0876 }
            r4.<init>()     // Catch:{ Exception -> 0x0886, all -> 0x0876 }
            java.lang.String r5 = "create encoder with w = "
            r4.append(r5)     // Catch:{ Exception -> 0x0886, all -> 0x0876 }
            r4.append(r12)     // Catch:{ Exception -> 0x0886, all -> 0x0876 }
            java.lang.String r5 = " h = "
            r4.append(r5)     // Catch:{ Exception -> 0x0886, all -> 0x0876 }
            r4.append(r2)     // Catch:{ Exception -> 0x0886, all -> 0x0876 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0886, all -> 0x0876 }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x0886, all -> 0x0876 }
        L_0x08c2:
            android.media.MediaFormat r4 = android.media.MediaFormat.createVideoFormat(r11, r12, r2)     // Catch:{ Exception -> 0x1335, all -> 0x132e }
            java.lang.String r5 = "color-format"
            r6 = 2130708361(0x7var_, float:1.701803E38)
            r4.setInteger(r5, r6)     // Catch:{ Exception -> 0x1335, all -> 0x132e }
            java.lang.String r5 = "bitrate"
            r4.setInteger(r5, r3)     // Catch:{ Exception -> 0x1335, all -> 0x132e }
            java.lang.String r5 = "frame-rate"
            r4.setInteger(r5, r9)     // Catch:{ Exception -> 0x1335, all -> 0x132e }
            java.lang.String r5 = "i-frame-interval"
            r6 = 2
            r4.setInteger(r5, r6)     // Catch:{ Exception -> 0x1335, all -> 0x132e }
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1335, all -> 0x132e }
            r5 = 23
            if (r6 >= r5) goto L_0x0916
            int r5 = java.lang.Math.min(r2, r12)     // Catch:{ Exception -> 0x0886, all -> 0x0876 }
            r38 = r2
            r2 = 480(0x1e0, float:6.73E-43)
            if (r5 > r2) goto L_0x0918
            r2 = 921600(0xe1000, float:1.291437E-39)
            if (r3 <= r2) goto L_0x08f4
            goto L_0x08f5
        L_0x08f4:
            r2 = r3
        L_0x08f5:
            java.lang.String r3 = "bitrate"
            r4.setInteger(r3, r2)     // Catch:{ Exception -> 0x090f, all -> 0x08fd }
            r21 = r2
            goto L_0x091a
        L_0x08fd:
            r0 = move-exception
            r1 = r81
            r3 = r82
            r43 = r88
            r7 = r2
            r12 = r9
            r26 = r14
            r6 = 0
            r13 = -5
            r15 = r74
        L_0x090c:
            r2 = r0
            goto L_0x1478
        L_0x090f:
            r0 = move-exception
            r43 = r88
            r1 = r0
            r3 = r2
            goto L_0x088a
        L_0x0916:
            r38 = r2
        L_0x0918:
            r21 = r3
        L_0x091a:
            android.media.MediaCodec r5 = android.media.MediaCodec.createEncoderByType(r11)     // Catch:{ Exception -> 0x131e, all -> 0x130b }
            r2 = 0
            r3 = 1
            r5.configure(r4, r2, r2, r3)     // Catch:{ Exception -> 0x12f6, all -> 0x130b }
            org.telegram.messenger.video.InputSurface r4 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x12f6, all -> 0x130b }
            android.view.Surface r2 = r5.createInputSurface()     // Catch:{ Exception -> 0x12f6, all -> 0x130b }
            r4.<init>(r2)     // Catch:{ Exception -> 0x12f6, all -> 0x130b }
            r4.makeCurrent()     // Catch:{ Exception -> 0x12da, all -> 0x130b }
            r5.start()     // Catch:{ Exception -> 0x12da, all -> 0x130b }
            java.lang.String r2 = r7.getString(r1)     // Catch:{ Exception -> 0x12da, all -> 0x130b }
            android.media.MediaCodec r2 = android.media.MediaCodec.createDecoderByType(r2)     // Catch:{ Exception -> 0x12da, all -> 0x130b }
            org.telegram.messenger.video.OutputSurface r13 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x12c5, all -> 0x130b }
            r40 = 0
            float r3 = (float) r9
            r42 = 0
            r47 = r1
            r1 = r13
            r49 = r2
            r48 = r23
            r50 = r38
            r2 = r96
            r23 = r3
            r38 = 1
            r3 = r40
            r51 = r4
            r4 = r97
            r84 = r5
            r5 = r98
            r53 = r6
            r52 = r29
            r6 = r100
            r54 = r7
            r7 = r81
            r8 = r82
            r9 = r79
            r55 = r10
            r10 = r80
            r57 = r11
            r56 = r20
            r11 = r77
            r60 = r12
            r61 = r32
            r12 = r23
            r90 = r14
            r15 = r74
            r14 = r13
            r13 = r42
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x12b7, all -> 0x12b2 }
            r6 = r79
            r7 = r80
            r1 = r81
            r2 = r82
            r4 = 1
            java.lang.String r3 = createFragmentShader(r6, r7, r1, r2, r4)     // Catch:{ Exception -> 0x129c, all -> 0x12b2 }
            r5 = 0
            java.lang.String r8 = createFragmentShader(r6, r7, r1, r2, r5)     // Catch:{ Exception -> 0x129c, all -> 0x12b2 }
            r14.changeFragmentShader(r3, r8)     // Catch:{ Exception -> 0x129c, all -> 0x12b2 }
            android.view.Surface r3 = r14.getSurface()     // Catch:{ Exception -> 0x129c, all -> 0x12b2 }
            r10 = r49
            r8 = r54
            r9 = 0
            r10.configure(r8, r3, r9, r5)     // Catch:{ Exception -> 0x1294, all -> 0x12b2 }
            r10.start()     // Catch:{ Exception -> 0x1294, all -> 0x12b2 }
            r3 = r53
            r5 = 21
            if (r3 >= r5) goto L_0x09d6
            java.nio.ByteBuffer[] r3 = r10.getInputBuffers()     // Catch:{ Exception -> 0x09c3, all -> 0x09b5 }
            java.nio.ByteBuffer[] r5 = r84.getOutputBuffers()     // Catch:{ Exception -> 0x09c3, all -> 0x09b5 }
            goto L_0x09d8
        L_0x09b5:
            r0 = move-exception
            r12 = r83
            r43 = r88
            r26 = r90
            r3 = r2
            r7 = r21
            r6 = 0
            r13 = -5
            goto L_0x090c
        L_0x09c3:
            r0 = move-exception
            r12 = r83
            r43 = r88
            r26 = r90
            r1 = r0
            r59 = r9
            r69 = r14
            r3 = r21
            r13 = -5
        L_0x09d2:
            r14 = r84
            goto L_0x137a
        L_0x09d6:
            r3 = r9
            r5 = r3
        L_0x09d8:
            r8 = r48
            if (r8 < 0) goto L_0x0b10
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x0afb, all -> 0x0ae7 }
            android.media.MediaFormat r11 = r11.getTrackFormat(r8)     // Catch:{ Exception -> 0x0afb, all -> 0x0ae7 }
            r12 = r47
            java.lang.String r13 = r11.getString(r12)     // Catch:{ Exception -> 0x0afb, all -> 0x0ae7 }
            java.lang.String r9 = "audio/mp4a-latm"
            boolean r9 = r13.equals(r9)     // Catch:{ Exception -> 0x0afb, all -> 0x0ae7 }
            if (r9 != 0) goto L_0x0a0f
            java.lang.String r9 = r11.getString(r12)     // Catch:{ Exception -> 0x09ff, all -> 0x09b5 }
            java.lang.String r13 = "audio/mpeg"
            boolean r9 = r9.equals(r13)     // Catch:{ Exception -> 0x09ff, all -> 0x09b5 }
            if (r9 == 0) goto L_0x09fd
            goto L_0x0a0f
        L_0x09fd:
            r13 = 0
            goto L_0x0a10
        L_0x09ff:
            r0 = move-exception
        L_0x0a00:
            r12 = r83
            r43 = r88
            r26 = r90
            r1 = r0
            r69 = r14
            r3 = r21
            r13 = -5
            r59 = 0
            goto L_0x09d2
        L_0x0a0f:
            r13 = 1
        L_0x0a10:
            java.lang.String r9 = r11.getString(r12)     // Catch:{ Exception -> 0x0afb, all -> 0x0ae7 }
            java.lang.String r12 = "audio/unknown"
            boolean r9 = r9.equals(r12)     // Catch:{ Exception -> 0x0afb, all -> 0x0ae7 }
            if (r9 == 0) goto L_0x0a1d
            r8 = -1
        L_0x0a1d:
            if (r8 < 0) goto L_0x0adb
            if (r13 == 0) goto L_0x0a77
            org.telegram.messenger.video.MP4Builder r9 = r15.mediaMuxer     // Catch:{ Exception -> 0x0a73, all -> 0x0a6a }
            int r9 = r9.addTrack(r11, r4)     // Catch:{ Exception -> 0x0a73, all -> 0x0a6a }
            android.media.MediaExtractor r12 = r15.extractor     // Catch:{ Exception -> 0x0a73, all -> 0x0a6a }
            r12.selectTrack(r8)     // Catch:{ Exception -> 0x0a73, all -> 0x0a6a }
            java.lang.String r12 = "max-input-size"
            int r11 = r11.getInteger(r12)     // Catch:{ Exception -> 0x0a33, all -> 0x09b5 }
            goto L_0x0a39
        L_0x0a33:
            r0 = move-exception
            r11 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)     // Catch:{ Exception -> 0x0a73, all -> 0x0a6a }
            r11 = 0
        L_0x0a39:
            if (r11 > 0) goto L_0x0a3d
            r11 = 65536(0x10000, float:9.18355E-41)
        L_0x0a3d:
            java.nio.ByteBuffer r12 = java.nio.ByteBuffer.allocateDirect(r11)     // Catch:{ Exception -> 0x0a73, all -> 0x0a6a }
            r20 = r5
            r6 = 0
            r4 = r86
            int r23 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r23 <= 0) goto L_0x0a55
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x09ff, all -> 0x09b5 }
            r7 = 0
            r6.seekTo(r4, r7)     // Catch:{ Exception -> 0x09ff, all -> 0x09b5 }
            r7 = r11
            r23 = r12
            goto L_0x0a60
        L_0x0a55:
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x09ff, all -> 0x0a68 }
            r7 = r11
            r23 = r12
            r2 = 0
            r11 = 0
            r6.seekTo(r11, r2)     // Catch:{ Exception -> 0x09ff, all -> 0x0a68 }
        L_0x0a60:
            r11 = r88
            r6 = r7
            r2 = r8
            r7 = r9
            r9 = 0
            goto L_0x0b1d
        L_0x0a68:
            r0 = move-exception
            goto L_0x0a6d
        L_0x0a6a:
            r0 = move-exception
            r4 = r86
        L_0x0a6d:
            r3 = r82
            r12 = r83
            goto L_0x1315
        L_0x0a73:
            r0 = move-exception
            r4 = r86
            goto L_0x0a00
        L_0x0a77:
            r20 = r5
            r4 = r86
            android.media.MediaExtractor r2 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0ad9, all -> 0x0ad7 }
            r2.<init>()     // Catch:{ Exception -> 0x0ad9, all -> 0x0ad7 }
            r6 = r75
            r7 = r82
            r2.setDataSource(r6)     // Catch:{ Exception -> 0x0ad9, all -> 0x0ad7 }
            r2.selectTrack(r8)     // Catch:{ Exception -> 0x0ad9, all -> 0x0ad7 }
            r9 = r13
            r12 = 0
            int r23 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r23 <= 0) goto L_0x0aa1
            r12 = 0
            r2.seekTo(r4, r12)     // Catch:{ Exception -> 0x09ff, all -> 0x0a96 }
            goto L_0x0aa6
        L_0x0a96:
            r0 = move-exception
            r12 = r83
            r43 = r88
            r26 = r90
            r2 = r0
            r3 = r7
            goto L_0x131a
        L_0x0aa1:
            r6 = r12
            r12 = 0
            r2.seekTo(r6, r12)     // Catch:{ Exception -> 0x0ad9, all -> 0x0ad7 }
        L_0x0aa6:
            org.telegram.messenger.video.AudioRecoder r6 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x0ad9, all -> 0x0ad7 }
            r6.<init>(r11, r2, r8)     // Catch:{ Exception -> 0x0ad9, all -> 0x0ad7 }
            r6.startTime = r4     // Catch:{ Exception -> 0x0ac7, all -> 0x0ad7 }
            r11 = r88
            r6.endTime = r11     // Catch:{ Exception -> 0x0ac5, all -> 0x0ac3 }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0ac5, all -> 0x0ac3 }
            android.media.MediaFormat r7 = r6.format     // Catch:{ Exception -> 0x0ac5, all -> 0x0ac3 }
            r13 = 1
            int r2 = r2.addTrack(r7, r13)     // Catch:{ Exception -> 0x0ac5, all -> 0x0ac3 }
            r7 = r2
            r2 = r8
            r13 = r9
            r23 = 0
            r9 = r6
            r6 = 0
            goto L_0x0b1d
        L_0x0ac3:
            r0 = move-exception
            goto L_0x0aec
        L_0x0ac5:
            r0 = move-exception
            goto L_0x0aca
        L_0x0ac7:
            r0 = move-exception
            r11 = r88
        L_0x0aca:
            r26 = r90
            r1 = r0
            r59 = r6
            r43 = r11
            r69 = r14
            r3 = r21
            r13 = -5
            goto L_0x0b0c
        L_0x0ad7:
            r0 = move-exception
            goto L_0x0aea
        L_0x0ad9:
            r0 = move-exception
            goto L_0x0afe
        L_0x0adb:
            r11 = r88
            r20 = r5
            r9 = r13
            r4 = r86
            r2 = r8
            r6 = 0
            r7 = -5
            r9 = 0
            goto L_0x0b1b
        L_0x0ae7:
            r0 = move-exception
            r4 = r86
        L_0x0aea:
            r11 = r88
        L_0x0aec:
            r3 = r82
            r26 = r90
            r2 = r0
            r43 = r11
            r7 = r21
            r6 = 0
            r13 = -5
        L_0x0af7:
            r12 = r83
            goto L_0x1478
        L_0x0afb:
            r0 = move-exception
            r4 = r86
        L_0x0afe:
            r11 = r88
            r26 = r90
            r1 = r0
            r43 = r11
            r69 = r14
            r3 = r21
            r13 = -5
            r59 = 0
        L_0x0b0c:
            r12 = r83
            goto L_0x09d2
        L_0x0b10:
            r11 = r88
            r20 = r5
            r4 = r86
            r2 = r8
            r6 = 0
            r7 = -5
            r9 = 0
            r13 = 1
        L_0x0b1b:
            r23 = 0
        L_0x0b1d:
            if (r2 >= 0) goto L_0x0b21
            r8 = 1
            goto L_0x0b22
        L_0x0b21:
            r8 = 0
        L_0x0b22:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x127f, all -> 0x12b2 }
            r1 = r6
            r29 = r8
            r64 = r18
            r66 = r64
            r58 = r23
            r62 = r26
            r6 = 0
            r8 = 0
            r23 = 0
            r32 = -5
            r39 = 0
            r47 = 1
            r48 = 0
            r53 = 0
            r26 = r90
        L_0x0b40:
            if (r6 == 0) goto L_0x0b58
            if (r13 != 0) goto L_0x0b47
            if (r29 != 0) goto L_0x0b47
            goto L_0x0b58
        L_0x0b47:
            r5 = r81
            r4 = r82
            r2 = r10
            r43 = r11
            r69 = r14
            r6 = 0
            r13 = 0
            r12 = r83
            r14 = r84
            goto L_0x13c2
        L_0x0b58:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x126a, all -> 0x1259 }
            if (r13 != 0) goto L_0x0b77
            if (r9 == 0) goto L_0x0b77
            r88 = r6
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x0b6a, all -> 0x0cbd }
            boolean r6 = r9.step(r6, r7)     // Catch:{ Exception -> 0x0b6a, all -> 0x0cbd }
            r29 = r6
            goto L_0x0b79
        L_0x0b6a:
            r0 = move-exception
            r1 = r0
            r59 = r9
        L_0x0b6e:
            r43 = r11
            r69 = r14
            r3 = r21
            r13 = r32
            goto L_0x0b0c
        L_0x0b77:
            r88 = r6
        L_0x0b79:
            if (r39 != 0) goto L_0x0cde
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0ccc, all -> 0x0cbd }
            int r6 = r6.getSampleTrackIndex()     // Catch:{ Exception -> 0x0ccc, all -> 0x0cbd }
            r59 = r9
            r9 = r52
            if (r6 != r9) goto L_0x0be2
            r89 = r8
            r52 = r9
            r8 = 2500(0x9c4, double:1.235E-320)
            int r6 = r10.dequeueInputBuffer(r8)     // Catch:{ Exception -> 0x0bdf, all -> 0x0cbd }
            if (r6 < 0) goto L_0x0bd4
            int r8 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0bdf, all -> 0x0cbd }
            r9 = 21
            if (r8 >= r9) goto L_0x0b9c
            r8 = r3[r6]     // Catch:{ Exception -> 0x0bdf, all -> 0x0cbd }
            goto L_0x0ba0
        L_0x0b9c:
            java.nio.ByteBuffer r8 = r10.getInputBuffer(r6)     // Catch:{ Exception -> 0x0bdf, all -> 0x0cbd }
        L_0x0ba0:
            android.media.MediaExtractor r9 = r15.extractor     // Catch:{ Exception -> 0x0bdf, all -> 0x0cbd }
            r68 = r3
            r3 = 0
            int r43 = r9.readSampleData(r8, r3)     // Catch:{ Exception -> 0x0bdf, all -> 0x0cbd }
            if (r43 >= 0) goto L_0x0bbd
            r42 = 0
            r43 = 0
            r44 = 0
            r46 = 4
            r40 = r10
            r41 = r6
            r40.queueInputBuffer(r41, r42, r43, r44, r46)     // Catch:{ Exception -> 0x0bdf, all -> 0x0cbd }
            r39 = 1
            goto L_0x0bd6
        L_0x0bbd:
            r42 = 0
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x0bdf, all -> 0x0cbd }
            long r44 = r3.getSampleTime()     // Catch:{ Exception -> 0x0bdf, all -> 0x0cbd }
            r46 = 0
            r40 = r10
            r41 = r6
            r40.queueInputBuffer(r41, r42, r43, r44, r46)     // Catch:{ Exception -> 0x0bdf, all -> 0x0cbd }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x0bdf, all -> 0x0cbd }
            r3.advance()     // Catch:{ Exception -> 0x0bdf, all -> 0x0cbd }
            goto L_0x0bd6
        L_0x0bd4:
            r68 = r3
        L_0x0bd6:
            r70 = r13
            r69 = r14
            r13 = r55
            r14 = r2
            goto L_0x0ca0
        L_0x0bdf:
            r0 = move-exception
            r1 = r0
            goto L_0x0b6e
        L_0x0be2:
            r68 = r3
            r89 = r8
            r52 = r9
            if (r13 == 0) goto L_0x0CLASSNAME
            r3 = -1
            if (r2 == r3) goto L_0x0CLASSNAME
            if (r6 != r2) goto L_0x0CLASSNAME
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cbd }
            r6 = 28
            if (r3 < r6) goto L_0x0c0d
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cbd }
            long r8 = r6.getSampleSize()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0cbd }
            r70 = r13
            r69 = r14
            long r13 = (long) r1
            int r6 = (r8 > r13 ? 1 : (r8 == r13 ? 0 : -1))
            if (r6 <= 0) goto L_0x0CLASSNAME
            r13 = 1024(0x400, double:5.06E-321)
            long r8 = r8 + r13
            int r1 = (int) r8
            java.nio.ByteBuffer r58 = java.nio.ByteBuffer.allocateDirect(r1)     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            goto L_0x0CLASSNAME
        L_0x0c0d:
            r70 = r13
            r69 = r14
        L_0x0CLASSNAME:
            r6 = r58
            android.media.MediaExtractor r8 = r15.extractor     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            r9 = 0
            int r8 = r8.readSampleData(r6, r9)     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            r13 = r55
            r13.size = r8     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            r8 = 21
            if (r3 >= r8) goto L_0x0c2a
            r6.position(r9)     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            int r3 = r13.size     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            r6.limit(r3)     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
        L_0x0c2a:
            int r3 = r13.size     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            if (r3 < 0) goto L_0x0c3c
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            long r8 = r3.getSampleTime()     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            r13.presentationTimeUs = r8     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            r3.advance()     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            goto L_0x0CLASSNAME
        L_0x0c3c:
            r3 = 0
            r13.size = r3     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            r39 = 1
        L_0x0CLASSNAME:
            int r3 = r13.size     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            if (r3 <= 0) goto L_0x0CLASSNAME
            r8 = 0
            int r3 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
            if (r3 < 0) goto L_0x0CLASSNAME
            long r8 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            int r3 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r3 >= 0) goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r3 = 0
            r13.offset = r3     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            android.media.MediaExtractor r8 = r15.extractor     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            int r8 = r8.getSampleFlags()     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            r13.flags = r8     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            long r8 = r8.writeSampleData(r7, r6, r13, r3)     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            r30 = 0
            int r3 = (r8 > r30 ? 1 : (r8 == r30 ? 0 : -1))
            if (r3 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MediaController$VideoConvertorListener r3 = r15.callback     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            if (r3 == 0) goto L_0x0CLASSNAME
            r90 = r1
            r14 = r2
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            long r40 = r1 - r4
            int r42 = (r40 > r48 ? 1 : (r40 == r48 ? 0 : -1))
            if (r42 <= 0) goto L_0x0CLASSNAME
            long r48 = r1 - r4
        L_0x0CLASSNAME:
            r1 = r48
            r40 = r6
            float r6 = (float) r1     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            float r6 = r6 / r24
            float r6 = r6 / r25
            r3.didWriteData(r8, r6)     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            r48 = r1
            goto L_0x0c8d
        L_0x0CLASSNAME:
            r90 = r1
            r14 = r2
            r40 = r6
        L_0x0c8d:
            r1 = r90
            r58 = r40
            goto L_0x0ca0
        L_0x0CLASSNAME:
            r0 = move-exception
            goto L_0x0ccf
        L_0x0CLASSNAME:
            r70 = r13
            r69 = r14
            r13 = r55
            r14 = r2
            r2 = -1
            if (r6 != r2) goto L_0x0ca0
            r2 = 1
            goto L_0x0ca1
        L_0x0ca0:
            r2 = 0
        L_0x0ca1:
            if (r2 == 0) goto L_0x0ceb
            r2 = 2500(0x9c4, double:1.235E-320)
            int r41 = r10.dequeueInputBuffer(r2)     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            if (r41 < 0) goto L_0x0ceb
            r42 = 0
            r43 = 0
            r44 = 0
            r46 = 4
            r40 = r10
            r40.queueInputBuffer(r41, r42, r43, r44, r46)     // Catch:{ Exception -> 0x0cbb, all -> 0x0cbd }
            r39 = 1
            goto L_0x0ceb
        L_0x0cbb:
            r0 = move-exception
            goto L_0x0cd1
        L_0x0cbd:
            r0 = move-exception
            r1 = r81
            r3 = r82
            r2 = r0
            r43 = r11
            r7 = r21
            r13 = r32
        L_0x0cc9:
            r6 = 0
            goto L_0x0af7
        L_0x0ccc:
            r0 = move-exception
            r59 = r9
        L_0x0ccf:
            r69 = r14
        L_0x0cd1:
            r14 = r84
            r1 = r0
            r43 = r11
            r3 = r21
            r13 = r32
        L_0x0cda:
            r12 = r83
            goto L_0x137a
        L_0x0cde:
            r68 = r3
            r89 = r8
            r59 = r9
            r70 = r13
            r69 = r14
            r13 = r55
            r14 = r2
        L_0x0ceb:
            r2 = r89 ^ 1
            r8 = r89
            r6 = r2
            r9 = r32
            r3 = 1
            r89 = r88
            r88 = r1
            r1 = r62
        L_0x0cf9:
            if (r6 != 0) goto L_0x0d13
            if (r3 == 0) goto L_0x0cfe
            goto L_0x0d13
        L_0x0cfe:
            r6 = r89
            r62 = r1
            r32 = r9
            r55 = r13
            r2 = r14
            r9 = r59
            r3 = r68
            r14 = r69
            r13 = r70
            r1 = r88
            goto L_0x0b40
        L_0x0d13:
            r74.checkConversionCanceled()     // Catch:{ Exception -> 0x124a, all -> 0x123b }
            if (r95 == 0) goto L_0x0d29
            r40 = 22000(0x55f0, double:1.08694E-319)
            r90 = r6
            r71 = r3
            r3 = r84
            r84 = r71
            r72 = r40
            r40 = r7
            r6 = r72
            goto L_0x0d35
        L_0x0d29:
            r90 = r6
            r40 = r7
            r6 = 2500(0x9c4, double:1.235E-320)
            r71 = r3
            r3 = r84
            r84 = r71
        L_0x0d35:
            int r6 = r3.dequeueOutputBuffer(r13, r6)     // Catch:{ Exception -> 0x1239, all -> 0x123b }
            r7 = -1
            if (r6 != r7) goto L_0x0d51
            r41 = r1
            r43 = r11
            r91 = r14
            r12 = r50
            r4 = r57
            r2 = r60
            r14 = r61
            r5 = 3
            r7 = 0
        L_0x0d4c:
            r11 = -1
            r1 = r89
            goto L_0x0var_
        L_0x0d51:
            r7 = -3
            if (r6 != r7) goto L_0x0d87
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0d7d, all -> 0x0d70 }
            r91 = r14
            r14 = 21
            if (r7 >= r14) goto L_0x0d60
            java.nio.ByteBuffer[] r20 = r3.getOutputBuffers()     // Catch:{ Exception -> 0x0d7d, all -> 0x0d70 }
        L_0x0d60:
            r7 = r84
            r41 = r1
            r43 = r11
            r12 = r50
            r4 = r57
            r2 = r60
            r14 = r61
            r5 = 3
            goto L_0x0d4c
        L_0x0d70:
            r0 = move-exception
            r1 = r81
            r3 = r82
            r2 = r0
            r13 = r9
            r43 = r11
            r7 = r21
            goto L_0x0cc9
        L_0x0d7d:
            r0 = move-exception
            r1 = r0
            r14 = r3
            r13 = r9
        L_0x0d81:
            r43 = r11
            r3 = r21
            goto L_0x0cda
        L_0x0d87:
            r91 = r14
            r7 = -2
            if (r6 != r7) goto L_0x0df6
            android.media.MediaFormat r7 = r3.getOutputFormat()     // Catch:{ Exception -> 0x0d7d, all -> 0x0d70 }
            r14 = -5
            if (r9 != r14) goto L_0x0de2
            if (r7 == 0) goto L_0x0de2
            org.telegram.messenger.video.MP4Builder r14 = r15.mediaMuxer     // Catch:{ Exception -> 0x0d7d, all -> 0x0d70 }
            r41 = r1
            r1 = 0
            int r2 = r14.addTrack(r7, r1)     // Catch:{ Exception -> 0x0d7d, all -> 0x0d70 }
            r1 = r56
            boolean r9 = r7.containsKey(r1)     // Catch:{ Exception -> 0x0ddd, all -> 0x0dce }
            if (r9 == 0) goto L_0x0dc8
            int r9 = r7.getInteger(r1)     // Catch:{ Exception -> 0x0ddd, all -> 0x0dce }
            r14 = 1
            if (r9 != r14) goto L_0x0dc8
            r14 = r61
            java.nio.ByteBuffer r9 = r7.getByteBuffer(r14)     // Catch:{ Exception -> 0x0ddd, all -> 0x0dce }
            r56 = r1
            java.lang.String r1 = "csd-1"
            java.nio.ByteBuffer r1 = r7.getByteBuffer(r1)     // Catch:{ Exception -> 0x0ddd, all -> 0x0dce }
            int r7 = r9.limit()     // Catch:{ Exception -> 0x0ddd, all -> 0x0dce }
            int r1 = r1.limit()     // Catch:{ Exception -> 0x0ddd, all -> 0x0dce }
            int r7 = r7 + r1
            r9 = r2
            r23 = r7
            goto L_0x0de6
        L_0x0dc8:
            r56 = r1
            r14 = r61
            r9 = r2
            goto L_0x0de6
        L_0x0dce:
            r0 = move-exception
            r1 = r81
            r3 = r82
            r13 = r2
            r43 = r11
            r7 = r21
            r6 = 0
            r12 = r83
            goto L_0x090c
        L_0x0ddd:
            r0 = move-exception
            r1 = r0
            r13 = r2
            r14 = r3
            goto L_0x0d81
        L_0x0de2:
            r41 = r1
            r14 = r61
        L_0x0de6:
            r7 = r84
            r1 = r89
            r43 = r11
            r12 = r50
            r4 = r57
            r2 = r60
            r5 = 3
        L_0x0df3:
            r11 = -1
            goto L_0x0var_
        L_0x0df6:
            r41 = r1
            r14 = r61
            if (r6 < 0) goto L_0x1216
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1239, all -> 0x123b }
            r2 = 21
            if (r1 >= r2) goto L_0x0e05
            r1 = r20[r6]     // Catch:{ Exception -> 0x0d7d, all -> 0x0d70 }
            goto L_0x0e09
        L_0x0e05:
            java.nio.ByteBuffer r1 = r3.getOutputBuffer(r6)     // Catch:{ Exception -> 0x1239, all -> 0x123b }
        L_0x0e09:
            if (r1 == 0) goto L_0x11f4
            int r7 = r13.size     // Catch:{ Exception -> 0x1239, all -> 0x123b }
            r2 = 1
            if (r7 <= r2) goto L_0x0f2b
            int r2 = r13.flags     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r32 = r2 & 2
            if (r32 != 0) goto L_0x0eaa
            if (r23 == 0) goto L_0x0e29
            r32 = r2 & 1
            if (r32 == 0) goto L_0x0e29
            r43 = r11
            int r11 = r13.offset     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            int r11 = r11 + r23
            r13.offset = r11     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            int r7 = r7 - r23
            r13.size = r7     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            goto L_0x0e2b
        L_0x0e29:
            r43 = r11
        L_0x0e2b:
            if (r47 == 0) goto L_0x0e7a
            r2 = r2 & 1
            if (r2 == 0) goto L_0x0e7a
            int r2 = r13.size     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r11 = 100
            if (r2 <= r11) goto L_0x0e78
            int r2 = r13.offset     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r1.position(r2)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            byte[] r2 = new byte[r11]     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r1.get(r2)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r7 = 0
            r12 = 0
        L_0x0e43:
            r11 = 96
            if (r7 >= r11) goto L_0x0e78
            byte r11 = r2[r7]     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            if (r11 != 0) goto L_0x0e6f
            int r11 = r7 + 1
            byte r11 = r2[r11]     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            if (r11 != 0) goto L_0x0e6f
            int r11 = r7 + 2
            byte r11 = r2[r11]     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            if (r11 != 0) goto L_0x0e6f
            int r11 = r7 + 3
            byte r11 = r2[r11]     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r32 = r2
            r2 = 1
            if (r11 != r2) goto L_0x0e71
            int r12 = r12 + 1
            if (r12 <= r2) goto L_0x0e71
            int r2 = r13.offset     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            int r2 = r2 + r7
            r13.offset = r2     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            int r2 = r13.size     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            int r2 = r2 - r7
            r13.size = r2     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            goto L_0x0e78
        L_0x0e6f:
            r32 = r2
        L_0x0e71:
            int r7 = r7 + 1
            r2 = r32
            r11 = 100
            goto L_0x0e43
        L_0x0e78:
            r47 = 0
        L_0x0e7a:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r7 = 1
            long r1 = r2.writeSampleData(r9, r1, r13, r7)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r11 = 0
            int r7 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r7 == 0) goto L_0x0ea1
            org.telegram.messenger.MediaController$VideoConvertorListener r7 = r15.callback     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            if (r7 == 0) goto L_0x0ea1
            long r11 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            long r45 = r11 - r4
            int r32 = (r45 > r48 ? 1 : (r45 == r48 ? 0 : -1))
            if (r32 <= 0) goto L_0x0e95
            long r48 = r11 - r4
        L_0x0e95:
            r11 = r48
            float r4 = (float) r11     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            float r4 = r4 / r24
            float r4 = r4 / r25
            r7.didWriteData(r1, r4)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r48 = r11
        L_0x0ea1:
            r12 = r50
            r4 = r57
            r2 = r60
            r5 = 3
            goto L_0x0f2f
        L_0x0eaa:
            r43 = r11
            r2 = -5
            if (r9 != r2) goto L_0x0ea1
            byte[] r2 = new byte[r7]     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            int r4 = r13.offset     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            int r4 = r4 + r7
            r1.limit(r4)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            int r4 = r13.offset     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r1.position(r4)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r1.get(r2)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            int r1 = r13.size     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r4 = 1
            int r1 = r1 - r4
        L_0x0ec3:
            r5 = 3
            if (r1 < 0) goto L_0x0var_
            if (r1 <= r5) goto L_0x0var_
            byte r7 = r2[r1]     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            if (r7 != r4) goto L_0x0efc
            int r7 = r1 + -1
            byte r7 = r2[r7]     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            if (r7 != 0) goto L_0x0efc
            int r7 = r1 + -2
            byte r7 = r2[r7]     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            if (r7 != 0) goto L_0x0efc
            int r7 = r1 + -3
            byte r11 = r2[r7]     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            if (r11 != 0) goto L_0x0efc
            java.nio.ByteBuffer r1 = java.nio.ByteBuffer.allocate(r7)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            int r11 = r13.size     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            int r11 = r11 - r7
            java.nio.ByteBuffer r11 = java.nio.ByteBuffer.allocate(r11)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r12 = 0
            java.nio.ByteBuffer r4 = r1.put(r2, r12, r7)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r4.position(r12)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            int r4 = r13.size     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            int r4 = r4 - r7
            java.nio.ByteBuffer r2 = r11.put(r2, r7, r4)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r2.position(r12)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            goto L_0x0var_
        L_0x0efc:
            int r1 = r1 + -1
            r4 = 1
            goto L_0x0ec3
        L_0x0var_:
            r1 = 0
            r11 = 0
        L_0x0var_:
            r12 = r50
            r4 = r57
            r2 = r60
            android.media.MediaFormat r7 = android.media.MediaFormat.createVideoFormat(r4, r2, r12)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            if (r1 == 0) goto L_0x0var_
            if (r11 == 0) goto L_0x0var_
            r7.setByteBuffer(r14, r1)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            java.lang.String r1 = "csd-1"
            r7.setByteBuffer(r1, r11)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
        L_0x0var_:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r11 = 0
            int r1 = r1.addTrack(r7, r11)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r9 = r1
            goto L_0x0f2f
        L_0x0var_:
            r0 = move-exception
            r43 = r11
            goto L_0x0fa3
        L_0x0var_:
            r0 = move-exception
            r43 = r11
            goto L_0x0fac
        L_0x0f2b:
            r43 = r11
            goto L_0x0ea1
        L_0x0f2f:
            int r1 = r13.flags     // Catch:{ Exception -> 0x11ee, all -> 0x11b0 }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x0var_
            r1 = 1
            goto L_0x0var_
        L_0x0var_:
            r1 = 0
        L_0x0var_:
            r7 = 0
            r3.releaseOutputBuffer(r6, r7)     // Catch:{ Exception -> 0x11ee, all -> 0x11b0 }
            r7 = r84
            goto L_0x0df3
        L_0x0var_:
            if (r6 == r11) goto L_0x0f5d
            r6 = r90
            r89 = r1
            r60 = r2
            r84 = r3
            r57 = r4
            r3 = r7
            r50 = r12
            r61 = r14
            r7 = r40
            r1 = r41
            r11 = r43
            r4 = r86
        L_0x0var_:
            r14 = r91
            goto L_0x0cf9
        L_0x0f5d:
            if (r8 != 0) goto L_0x11bf
            r5 = 2500(0x9c4, double:1.235E-320)
            int r11 = r10.dequeueOutputBuffer(r13, r5)     // Catch:{ Exception -> 0x11b3, all -> 0x11b0 }
            r5 = -1
            if (r11 != r5) goto L_0x0f7c
            r84 = r1
            r60 = r2
            r57 = r4
            r32 = r7
            r50 = r12
            r6 = r41
            r1 = r51
            r33 = 0
            r12 = r83
            goto L_0x11d1
        L_0x0f7c:
            r6 = -3
            if (r11 != r6) goto L_0x0var_
            goto L_0x11bf
        L_0x0var_:
            r6 = -2
            if (r11 != r6) goto L_0x0fb0
            android.media.MediaFormat r6 = r10.getOutputFormat()     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            if (r11 == 0) goto L_0x11bf
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r11.<init>()     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            java.lang.String r5 = "newFormat = "
            r11.append(r5)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r11.append(r6)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            java.lang.String r5 = r11.toString()     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            org.telegram.messenger.FileLog.d(r5)     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            goto L_0x11bf
        L_0x0fa2:
            r0 = move-exception
        L_0x0fa3:
            r1 = r81
            r3 = r82
            r12 = r83
            goto L_0x1244
        L_0x0fab:
            r0 = move-exception
        L_0x0fac:
            r12 = r83
            goto L_0x11b8
        L_0x0fb0:
            if (r11 < 0) goto L_0x118f
            int r5 = r13.size     // Catch:{ Exception -> 0x11b3, all -> 0x11b0 }
            r84 = r1
            r60 = r2
            if (r5 == 0) goto L_0x0fbc
            r5 = 1
            goto L_0x0fbd
        L_0x0fbc:
            r5 = 0
        L_0x0fbd:
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x11b3, all -> 0x11b0 }
            r30 = 0
            int r6 = (r43 > r30 ? 1 : (r43 == r30 ? 0 : -1))
            if (r6 <= 0) goto L_0x0fd6
            int r6 = (r1 > r43 ? 1 : (r1 == r43 ? 0 : -1))
            if (r6 < 0) goto L_0x0fd6
            int r5 = r13.flags     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r5 = r5 | 4
            r13.flags = r5     // Catch:{ Exception -> 0x0fab, all -> 0x0fa2 }
            r6 = 0
            r8 = 1
            r30 = 0
            r39 = 1
            goto L_0x0fd9
        L_0x0fd6:
            r6 = r5
            r30 = 0
        L_0x0fd9:
            int r5 = (r26 > r30 ? 1 : (r26 == r30 ? 0 : -1))
            if (r5 < 0) goto L_0x1056
            int r5 = r13.flags     // Catch:{ Exception -> 0x0fab, all -> 0x1050 }
            r5 = r5 & 4
            if (r5 == 0) goto L_0x1056
            r57 = r4
            r4 = r86
            long r45 = r26 - r4
            long r45 = java.lang.Math.abs(r45)     // Catch:{ Exception -> 0x0fab, all -> 0x1050 }
            r32 = 1000000(0xvar_, float:1.401298E-39)
            r89 = r6
            r50 = r12
            r12 = r83
            int r6 = r32 / r12
            r32 = r7
            long r6 = (long) r6
            int r55 = (r45 > r6 ? 1 : (r45 == r6 ? 0 : -1))
            if (r55 <= 0) goto L_0x1048
            r6 = 0
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 <= 0) goto L_0x100d
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x104d, all -> 0x1233 }
            r7 = 0
            r6.seekTo(r4, r7)     // Catch:{ Exception -> 0x104d, all -> 0x1233 }
            r4 = 0
            goto L_0x1015
        L_0x100d:
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x104d, all -> 0x104b }
            r4 = 0
            r7 = 0
            r6.seekTo(r7, r4)     // Catch:{ Exception -> 0x104d, all -> 0x1233 }
        L_0x1015:
            long r53 = r41 + r34
            int r5 = r13.flags     // Catch:{ Exception -> 0x103c, all -> 0x102c }
            r6 = -5
            r5 = r5 & r6
            r13.flags = r5     // Catch:{ Exception -> 0x103c, all -> 0x102c }
            r10.flush()     // Catch:{ Exception -> 0x103c, all -> 0x102c }
            r7 = r18
            r5 = 0
            r22 = 0
            r30 = 0
            r33 = 1
            r39 = 0
            goto L_0x106d
        L_0x102c:
            r0 = move-exception
            r1 = r81
            r3 = r82
            r2 = r0
            r13 = r9
            r7 = r21
            r43 = r26
            r6 = 0
            r26 = r18
            goto L_0x1478
        L_0x103c:
            r0 = move-exception
            r1 = r0
            r14 = r3
            r13 = r9
            r3 = r21
            r43 = r26
            r26 = r18
            goto L_0x137a
        L_0x1048:
            r4 = 0
            r6 = -5
            goto L_0x1062
        L_0x104b:
            r0 = move-exception
            goto L_0x1053
        L_0x104d:
            r0 = move-exception
            goto L_0x11b8
        L_0x1050:
            r0 = move-exception
            r12 = r83
        L_0x1053:
            r4 = 0
            goto L_0x1240
        L_0x1056:
            r57 = r4
            r89 = r6
            r32 = r7
            r50 = r12
            r4 = 0
            r6 = -5
            r12 = r83
        L_0x1062:
            r22 = r89
            r5 = r8
            r7 = r26
            r26 = r43
            r30 = 0
            r33 = 0
        L_0x106d:
            int r43 = (r66 > r30 ? 1 : (r66 == r30 ? 0 : -1))
            if (r43 <= 0) goto L_0x10a2
            r89 = r5
            long r4 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1096, all -> 0x1086 }
            long r4 = r4 - r66
            int r43 = (r4 > r36 ? 1 : (r4 == r36 ? 0 : -1))
            if (r43 >= 0) goto L_0x10a4
            int r4 = r13.flags     // Catch:{ Exception -> 0x1096, all -> 0x1086 }
            r4 = r4 & 4
            if (r4 != 0) goto L_0x10a4
            r4 = 0
            r22 = 0
            goto L_0x10a6
        L_0x1086:
            r0 = move-exception
            r1 = r81
            r3 = r82
            r2 = r0
            r13 = r9
            r43 = r26
            r6 = 0
            r26 = r7
            r7 = r21
            goto L_0x1478
        L_0x1096:
            r0 = move-exception
            r1 = r0
            r14 = r3
            r13 = r9
            r3 = r21
            r43 = r26
            r26 = r7
            goto L_0x137a
        L_0x10a2:
            r89 = r5
        L_0x10a4:
            r4 = 0
        L_0x10a6:
            int r30 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            r43 = r7
            if (r30 < 0) goto L_0x10af
            r6 = r43
            goto L_0x10b1
        L_0x10af:
            r6 = r86
        L_0x10b1:
            int r8 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r8 <= 0) goto L_0x10f2
            int r4 = (r64 > r18 ? 1 : (r64 == r18 ? 0 : -1))
            if (r4 != 0) goto L_0x10f2
            int r4 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r4 >= 0) goto L_0x10e1
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x10ef, all -> 0x116d }
            if (r1 == 0) goto L_0x10df
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x10ef, all -> 0x116d }
            r1.<init>()     // Catch:{ Exception -> 0x10ef, all -> 0x116d }
            java.lang.String r2 = "drop frame startTime = "
            r1.append(r2)     // Catch:{ Exception -> 0x10ef, all -> 0x116d }
            r1.append(r6)     // Catch:{ Exception -> 0x10ef, all -> 0x116d }
            java.lang.String r2 = " present time = "
            r1.append(r2)     // Catch:{ Exception -> 0x10ef, all -> 0x116d }
            long r4 = r13.presentationTimeUs     // Catch:{ Exception -> 0x10ef, all -> 0x116d }
            r1.append(r4)     // Catch:{ Exception -> 0x10ef, all -> 0x116d }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x10ef, all -> 0x116d }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x10ef, all -> 0x116d }
        L_0x10df:
            r6 = 0
            goto L_0x10f4
        L_0x10e1:
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x10ef, all -> 0x116d }
            r4 = -2147483648(0xfffffffvar_, double:NaN)
            int r6 = (r41 > r4 ? 1 : (r41 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x10ec
            long r53 = r53 - r1
        L_0x10ec:
            r64 = r1
            goto L_0x10f2
        L_0x10ef:
            r0 = move-exception
            goto L_0x1182
        L_0x10f2:
            r6 = r22
        L_0x10f4:
            if (r33 == 0) goto L_0x10f9
            r64 = r18
            goto L_0x110c
        L_0x10f9:
            int r1 = (r43 > r18 ? 1 : (r43 == r18 ? 0 : -1))
            if (r1 != 0) goto L_0x1109
            r1 = 0
            int r4 = (r53 > r1 ? 1 : (r53 == r1 ? 0 : -1))
            if (r4 == 0) goto L_0x1109
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x10ef, all -> 0x116d }
            long r1 = r1 + r53
            r13.presentationTimeUs = r1     // Catch:{ Exception -> 0x10ef, all -> 0x116d }
        L_0x1109:
            r10.releaseOutputBuffer(r11, r6)     // Catch:{ Exception -> 0x117f, all -> 0x116d }
        L_0x110c:
            if (r6 == 0) goto L_0x1143
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x117f, all -> 0x116d }
            r4 = 0
            int r6 = (r43 > r4 ? 1 : (r43 == r4 ? 0 : -1))
            if (r6 < 0) goto L_0x111d
            r6 = r41
            long r6 = java.lang.Math.max(r6, r1)     // Catch:{ Exception -> 0x10ef, all -> 0x116d }
            goto L_0x111f
        L_0x111d:
            r6 = r41
        L_0x111f:
            r69.awaitNewImage()     // Catch:{ Exception -> 0x1124, all -> 0x116d }
            r8 = 0
            goto L_0x112a
        L_0x1124:
            r0 = move-exception
            r8 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)     // Catch:{ Exception -> 0x117f, all -> 0x116d }
            r8 = 1
        L_0x112a:
            if (r8 != 0) goto L_0x113e
            r69.drawImage()     // Catch:{ Exception -> 0x117f, all -> 0x116d }
            long r4 = r13.presentationTimeUs     // Catch:{ Exception -> 0x117f, all -> 0x116d }
            r41 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 * r41
            r8 = r51
            r8.setPresentationTime(r4)     // Catch:{ Exception -> 0x1167, all -> 0x116d }
            r8.swapBuffers()     // Catch:{ Exception -> 0x1167, all -> 0x116d }
            goto L_0x1140
        L_0x113e:
            r8 = r51
        L_0x1140:
            r66 = r1
            goto L_0x1147
        L_0x1143:
            r6 = r41
            r8 = r51
        L_0x1147:
            r1 = r6
            int r4 = r13.flags     // Catch:{ Exception -> 0x1167, all -> 0x116d }
            r4 = r4 & 4
            if (r4 == 0) goto L_0x115f
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1167, all -> 0x116d }
            if (r4 == 0) goto L_0x1157
            java.lang.String r4 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x1167, all -> 0x116d }
        L_0x1157:
            r3.signalEndOfInputStream()     // Catch:{ Exception -> 0x1167, all -> 0x116d }
            r6 = r1
            r1 = r8
            r33 = 0
            goto L_0x1163
        L_0x115f:
            r33 = r90
            r6 = r1
            r1 = r8
        L_0x1163:
            r8 = r89
            goto L_0x11d7
        L_0x1167:
            r0 = move-exception
            r1 = r0
            r14 = r3
            r51 = r8
            goto L_0x1184
        L_0x116d:
            r0 = move-exception
            r1 = r81
            r3 = r82
            r2 = r0
            r13 = r9
            r7 = r21
            r6 = 0
            r71 = r26
            r26 = r43
            r43 = r71
            goto L_0x1478
        L_0x117f:
            r0 = move-exception
            r8 = r51
        L_0x1182:
            r1 = r0
            r14 = r3
        L_0x1184:
            r13 = r9
            r3 = r21
            r71 = r26
            r26 = r43
            r43 = r71
            goto L_0x137a
        L_0x118f:
            r12 = r83
            r8 = r51
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x11aa, all -> 0x1233 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x11aa, all -> 0x1233 }
            r2.<init>()     // Catch:{ Exception -> 0x11aa, all -> 0x1233 }
            java.lang.String r4 = "unexpected result from decoder.dequeueOutputBuffer: "
            r2.append(r4)     // Catch:{ Exception -> 0x11aa, all -> 0x1233 }
            r2.append(r11)     // Catch:{ Exception -> 0x11aa, all -> 0x1233 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x11aa, all -> 0x1233 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x11aa, all -> 0x1233 }
            throw r1     // Catch:{ Exception -> 0x11aa, all -> 0x1233 }
        L_0x11aa:
            r0 = move-exception
            r1 = r0
            r14 = r3
            r51 = r8
            goto L_0x11ba
        L_0x11b0:
            r0 = move-exception
            goto L_0x123e
        L_0x11b3:
            r0 = move-exception
            r12 = r83
            r8 = r51
        L_0x11b8:
            r1 = r0
            r14 = r3
        L_0x11ba:
            r13 = r9
            r3 = r21
            goto L_0x137a
        L_0x11bf:
            r84 = r1
            r60 = r2
            r57 = r4
            r32 = r7
            r50 = r12
            r6 = r41
            r1 = r51
            r12 = r83
            r33 = r90
        L_0x11d1:
            r71 = r26
            r26 = r43
            r43 = r71
        L_0x11d7:
            r89 = r84
            r4 = r86
            r51 = r1
            r84 = r3
            r1 = r6
            r61 = r14
            r11 = r26
            r3 = r32
            r6 = r33
            r7 = r40
            r26 = r43
            goto L_0x0var_
        L_0x11ee:
            r0 = move-exception
            r12 = r83
            r1 = r51
            goto L_0x1253
        L_0x11f4:
            r43 = r11
            r1 = r51
            r12 = r83
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1235, all -> 0x1233 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1235, all -> 0x1233 }
            r4.<init>()     // Catch:{ Exception -> 0x1235, all -> 0x1233 }
            java.lang.String r5 = "encoderOutputBuffer "
            r4.append(r5)     // Catch:{ Exception -> 0x1235, all -> 0x1233 }
            r4.append(r6)     // Catch:{ Exception -> 0x1235, all -> 0x1233 }
            java.lang.String r5 = " was null"
            r4.append(r5)     // Catch:{ Exception -> 0x1235, all -> 0x1233 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x1235, all -> 0x1233 }
            r2.<init>(r4)     // Catch:{ Exception -> 0x1235, all -> 0x1233 }
            throw r2     // Catch:{ Exception -> 0x1235, all -> 0x1233 }
        L_0x1216:
            r43 = r11
            r1 = r51
            r12 = r83
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1235, all -> 0x1233 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1235, all -> 0x1233 }
            r4.<init>()     // Catch:{ Exception -> 0x1235, all -> 0x1233 }
            java.lang.String r5 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x1235, all -> 0x1233 }
            r4.append(r6)     // Catch:{ Exception -> 0x1235, all -> 0x1233 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x1235, all -> 0x1233 }
            r2.<init>(r4)     // Catch:{ Exception -> 0x1235, all -> 0x1233 }
            throw r2     // Catch:{ Exception -> 0x1235, all -> 0x1233 }
        L_0x1233:
            r0 = move-exception
            goto L_0x1240
        L_0x1235:
            r0 = move-exception
            r51 = r1
            goto L_0x1253
        L_0x1239:
            r0 = move-exception
            goto L_0x124d
        L_0x123b:
            r0 = move-exception
            r43 = r11
        L_0x123e:
            r12 = r83
        L_0x1240:
            r1 = r81
            r3 = r82
        L_0x1244:
            r2 = r0
            r13 = r9
            r7 = r21
            goto L_0x13fe
        L_0x124a:
            r0 = move-exception
            r3 = r84
        L_0x124d:
            r43 = r11
            r1 = r51
            r12 = r83
        L_0x1253:
            r14 = r3
            r13 = r9
            r3 = r21
            goto L_0x12f3
        L_0x1259:
            r0 = move-exception
            r4 = r11
            r12 = r83
            r1 = r81
            r3 = r82
            r2 = r0
            r43 = r4
            r7 = r21
            r13 = r32
            goto L_0x13fe
        L_0x126a:
            r0 = move-exception
            r3 = r84
            r59 = r9
            r4 = r11
            r69 = r14
            r1 = r51
            r12 = r83
            r14 = r3
            r43 = r4
            r3 = r21
            r13 = r32
            goto L_0x12f3
        L_0x127f:
            r0 = move-exception
            r12 = r83
            r3 = r84
            r59 = r9
            r69 = r14
            r1 = r51
            r43 = r88
            r26 = r90
            r14 = r3
            r3 = r21
            r13 = -5
            goto L_0x12f3
        L_0x1294:
            r0 = move-exception
            r12 = r83
            r3 = r84
            r69 = r14
            goto L_0x12a5
        L_0x129c:
            r0 = move-exception
            r12 = r83
            r3 = r84
            r69 = r14
            r10 = r49
        L_0x12a5:
            r1 = r51
            r43 = r88
            r26 = r90
            r14 = r3
            r3 = r21
            r13 = -5
            r59 = 0
            goto L_0x12f3
        L_0x12b2:
            r0 = move-exception
            r12 = r83
            goto L_0x1311
        L_0x12b7:
            r0 = move-exception
            r12 = r83
            r3 = r84
            r10 = r49
            r1 = r51
            r43 = r88
            r26 = r90
            goto L_0x12d6
        L_0x12c5:
            r0 = move-exception
            r10 = r2
            r1 = r4
            r3 = r5
            r12 = r9
            r90 = r14
            r52 = r29
            r15 = r74
            r43 = r88
            r26 = r90
            r51 = r1
        L_0x12d6:
            r14 = r3
            r3 = r21
            goto L_0x12ee
        L_0x12da:
            r0 = move-exception
            r1 = r4
            r3 = r5
            r12 = r9
            r90 = r14
            r52 = r29
            r15 = r74
            r43 = r88
            r26 = r90
            r51 = r1
            r14 = r3
            r3 = r21
            r10 = 0
        L_0x12ee:
            r13 = -5
            r59 = 0
            r69 = 0
        L_0x12f3:
            r1 = r0
            goto L_0x137a
        L_0x12f6:
            r0 = move-exception
            r3 = r5
            r12 = r9
            r90 = r14
            r52 = r29
            r15 = r74
            r43 = r88
            r26 = r90
            r1 = r0
            r14 = r3
            r3 = r21
            r10 = 0
            r13 = -5
            goto L_0x1374
        L_0x130b:
            r0 = move-exception
            r12 = r9
            r90 = r14
            r15 = r74
        L_0x1311:
            r1 = r81
            r3 = r82
        L_0x1315:
            r43 = r88
            r26 = r90
            r2 = r0
        L_0x131a:
            r7 = r21
            goto L_0x1476
        L_0x131e:
            r0 = move-exception
            r12 = r9
            r90 = r14
            r52 = r29
            r15 = r74
            r43 = r88
            r26 = r90
            r1 = r0
            r3 = r21
            goto L_0x1371
        L_0x132e:
            r0 = move-exception
            r12 = r9
            r90 = r14
            r15 = r74
            goto L_0x1343
        L_0x1335:
            r0 = move-exception
            r12 = r9
            r90 = r14
            r52 = r29
            r15 = r74
            goto L_0x136c
        L_0x133e:
            r0 = move-exception
            r12 = r9
            r90 = r14
            r15 = r13
        L_0x1343:
            r1 = r81
            r43 = r88
            r26 = r90
            r2 = r0
            r7 = r3
            goto L_0x07ce
        L_0x134d:
            r0 = move-exception
            r12 = r9
            r90 = r14
            r52 = r29
            r15 = r13
            goto L_0x136c
        L_0x1355:
            r0 = move-exception
            r12 = r9
            r15 = r13
            r1 = r81
            r3 = r82
            r7 = r84
            r43 = r88
            r26 = r90
            r2 = r0
            goto L_0x1476
        L_0x1365:
            r0 = move-exception
            r52 = r6
            r12 = r9
            r15 = r13
            r3 = r84
        L_0x136c:
            r43 = r88
            r26 = r90
            r1 = r0
        L_0x1371:
            r10 = 0
            r13 = -5
            r14 = 0
        L_0x1374:
            r51 = 0
            r59 = 0
            r69 = 0
        L_0x137a:
            boolean r2 = r1 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x13f5 }
            if (r2 == 0) goto L_0x1383
            if (r95 != 0) goto L_0x1383
            r45 = 1
            goto L_0x1385
        L_0x1383:
            r45 = 0
        L_0x1385:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x13eb }
            r2.<init>()     // Catch:{ all -> 0x13eb }
            java.lang.String r4 = "bitrate: "
            r2.append(r4)     // Catch:{ all -> 0x13eb }
            r2.append(r3)     // Catch:{ all -> 0x13eb }
            java.lang.String r4 = " framerate: "
            r2.append(r4)     // Catch:{ all -> 0x13eb }
            r2.append(r12)     // Catch:{ all -> 0x13eb }
            java.lang.String r4 = " size: "
            r2.append(r4)     // Catch:{ all -> 0x13eb }
            r4 = r82
            r2.append(r4)     // Catch:{ all -> 0x13e7 }
            java.lang.String r5 = "x"
            r2.append(r5)     // Catch:{ all -> 0x13e7 }
            r5 = r81
            r2.append(r5)     // Catch:{ all -> 0x13e5 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x13e5 }
            org.telegram.messenger.FileLog.e((java.lang.String) r2)     // Catch:{ all -> 0x13e5 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x13e5 }
            r21 = r3
            r2 = r10
            r32 = r13
            r6 = r45
            r9 = r59
            r13 = 1
        L_0x13c2:
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ all -> 0x13db }
            r3 = r52
            r1.unselectTrack(r3)     // Catch:{ all -> 0x13db }
            if (r2 == 0) goto L_0x13d1
            r2.stop()     // Catch:{ all -> 0x13db }
            r2.release()     // Catch:{ all -> 0x13db }
        L_0x13d1:
            r45 = r6
            r6 = r13
            r28 = r14
            r13 = r32
            r14 = r69
            goto L_0x1417
        L_0x13db:
            r0 = move-exception
            r2 = r0
            r3 = r4
            r1 = r5
            r7 = r21
            r13 = r32
            goto L_0x1478
        L_0x13e5:
            r0 = move-exception
            goto L_0x13f0
        L_0x13e7:
            r0 = move-exception
            r5 = r81
            goto L_0x13f0
        L_0x13eb:
            r0 = move-exception
            r5 = r81
            r4 = r82
        L_0x13f0:
            r2 = r0
            r7 = r3
            r3 = r4
            r1 = r5
            goto L_0x1423
        L_0x13f5:
            r0 = move-exception
            r5 = r81
            r4 = r82
            r2 = r0
            r7 = r3
            r3 = r4
            r1 = r5
        L_0x13fe:
            r6 = 0
            goto L_0x1478
        L_0x1401:
            r5 = r81
            r12 = r83
            r4 = r15
            r15 = r13
            r21 = r84
            r43 = r88
            r26 = r90
            r6 = 0
            r9 = 0
            r13 = -5
            r14 = 0
            r28 = 0
            r45 = 0
            r51 = 0
        L_0x1417:
            if (r14 == 0) goto L_0x1427
            r14.release()     // Catch:{ all -> 0x141d }
            goto L_0x1427
        L_0x141d:
            r0 = move-exception
            r2 = r0
            r3 = r4
            r1 = r5
            r7 = r21
        L_0x1423:
            r6 = r45
            goto L_0x1478
        L_0x1427:
            if (r51 == 0) goto L_0x142c
            r51.release()     // Catch:{ all -> 0x141d }
        L_0x142c:
            if (r28 == 0) goto L_0x1434
            r28.stop()     // Catch:{ all -> 0x141d }
            r28.release()     // Catch:{ all -> 0x141d }
        L_0x1434:
            if (r9 == 0) goto L_0x1439
            r9.release()     // Catch:{ all -> 0x141d }
        L_0x1439:
            r74.checkConversionCanceled()     // Catch:{ all -> 0x141d }
            r3 = r4
            r14 = r6
            r6 = r45
        L_0x1440:
            android.media.MediaExtractor r1 = r15.extractor
            if (r1 == 0) goto L_0x1447
            r1.release()
        L_0x1447:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer
            if (r1 == 0) goto L_0x145c
            r1.finishMovie()     // Catch:{ all -> 0x1457 }
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ all -> 0x1457 }
            long r1 = r1.getLastFrameTimestamp(r13)     // Catch:{ all -> 0x1457 }
            r15.endPresentationTime = r1     // Catch:{ all -> 0x1457 }
            goto L_0x145c
        L_0x1457:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x145c:
            r9 = r3
            r8 = r5
            r13 = r14
            r11 = r21
            goto L_0x14c7
        L_0x1463:
            r0 = move-exception
            r5 = r9
            r4 = r10
            r12 = r15
            r15 = r13
            goto L_0x146d
        L_0x1469:
            r0 = move-exception
            r12 = r8
            r5 = r9
            r4 = r10
        L_0x146d:
            r7 = r84
            r43 = r88
            r26 = r90
            r2 = r0
            r3 = r4
            r1 = r5
        L_0x1476:
            r6 = 0
        L_0x1477:
            r13 = -5
        L_0x1478:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x1537 }
            r4.<init>()     // Catch:{ all -> 0x1537 }
            java.lang.String r5 = "bitrate: "
            r4.append(r5)     // Catch:{ all -> 0x1537 }
            r4.append(r7)     // Catch:{ all -> 0x1537 }
            java.lang.String r5 = " framerate: "
            r4.append(r5)     // Catch:{ all -> 0x1537 }
            r4.append(r12)     // Catch:{ all -> 0x1537 }
            java.lang.String r5 = " size: "
            r4.append(r5)     // Catch:{ all -> 0x1537 }
            r4.append(r3)     // Catch:{ all -> 0x1537 }
            java.lang.String r5 = "x"
            r4.append(r5)     // Catch:{ all -> 0x1537 }
            r4.append(r1)     // Catch:{ all -> 0x1537 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x1537 }
            org.telegram.messenger.FileLog.e((java.lang.String) r4)     // Catch:{ all -> 0x1537 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x1537 }
            android.media.MediaExtractor r2 = r15.extractor
            if (r2 == 0) goto L_0x14ae
            r2.release()
        L_0x14ae:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer
            if (r2 == 0) goto L_0x14c3
            r2.finishMovie()     // Catch:{ all -> 0x14be }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ all -> 0x14be }
            long r4 = r2.getLastFrameTimestamp(r13)     // Catch:{ all -> 0x14be }
            r15.endPresentationTime = r4     // Catch:{ all -> 0x14be }
            goto L_0x14c3
        L_0x14be:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x14c3:
            r8 = r1
            r9 = r3
            r11 = r7
            r13 = 1
        L_0x14c7:
            if (r6 == 0) goto L_0x14f6
            r22 = 1
            r1 = r74
            r2 = r75
            r3 = r76
            r4 = r77
            r5 = r78
            r6 = r79
            r7 = r80
            r10 = r83
            r12 = r85
            r13 = r86
            r15 = r43
            r17 = r26
            r19 = r92
            r21 = r94
            r23 = r96
            r24 = r97
            r25 = r98
            r26 = r99
            r27 = r100
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r15, r17, r19, r21, r22, r23, r24, r25, r26, r27)
            return r1
        L_0x14f6:
            long r1 = java.lang.System.currentTimeMillis()
            long r1 = r1 - r16
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x1536
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "compression completed time="
            r3.append(r4)
            r3.append(r1)
            java.lang.String r1 = " needCompress="
            r3.append(r1)
            r1 = r94
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
        L_0x1536:
            return r13
        L_0x1537:
            r0 = move-exception
            r1 = r74
            r2 = r0
            android.media.MediaExtractor r3 = r1.extractor
            if (r3 == 0) goto L_0x1542
            r3.release()
        L_0x1542:
            org.telegram.messenger.video.MP4Builder r3 = r1.mediaMuxer
            if (r3 == 0) goto L_0x1557
            r3.finishMovie()     // Catch:{ all -> 0x1552 }
            org.telegram.messenger.video.MP4Builder r3 = r1.mediaMuxer     // Catch:{ all -> 0x1552 }
            long r3 = r3.getLastFrameTimestamp(r13)     // Catch:{ all -> 0x1552 }
            r1.endPresentationTime = r3     // Catch:{ all -> 0x1552 }
            goto L_0x1557
        L_0x1552:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x1557:
            goto L_0x1559
        L_0x1558:
            throw r2
        L_0x1559:
            goto L_0x1558
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
