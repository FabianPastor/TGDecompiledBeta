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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r90v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r90v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r90v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r70v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r70v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v70, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v79, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v80, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r70v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r70v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r70v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r70v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v89, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r70v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v95, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v96, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v98, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v102, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v100, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v101, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v106, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v109, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v102, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v110, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v111, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v103, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v113, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v104, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v105, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v106, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v107, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v108, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r90v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v109, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r90v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v110, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r90v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v111, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v112, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r90v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v113, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v114, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v115, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v116, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v117, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v118, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v119, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v120, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v121, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v122, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v123, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v124, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v125, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v126, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v127, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v128, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v129, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v130, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v131, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v134, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v80, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v135, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v136, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v137, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v138, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v139, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v140, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v141, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v151, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v190, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v191, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v194, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v195, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v154, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v158, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r9v132 */
    /* JADX WARNING: Code restructure failed: missing block: B:1001:0x10d1, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1021:0x1138, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1022:0x1139, code lost:
        r8 = r83;
        r34 = r4;
        r9 = r56;
        r5 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1028:0x1166, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1029:0x1167, code lost:
        r8 = r83;
        r2 = r3;
        r34 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1035:0x1196, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1036:0x1197, code lost:
        r8 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1037:0x119a, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1038:0x119b, code lost:
        r8 = r83;
        r34 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1057:0x1200, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1058:0x1201, code lost:
        r8 = r83;
        r12 = r84;
        r4 = r88;
        r9 = r56;
        r3 = r57;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1060:0x1216, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1061:0x1217, code lost:
        r8 = r83;
        r4 = r88;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1062:0x121d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1063:0x121e, code lost:
        r8 = r83;
        r9 = r56;
        r3 = r57;
        r34 = r88;
        r6 = r84;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1067:0x1245, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1068:0x1246, code lost:
        r26 = r90;
        r12 = r6;
        r8 = r9;
        r61 = r23;
        r2 = r0;
        r34 = r88;
        r56 = r1;
        r5 = r29;
        r1 = -5;
        r3 = null;
        r28 = null;
        r30 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1069:0x125e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1070:0x125f, code lost:
        r26 = r90;
        r12 = r6;
        r8 = r9;
        r61 = r23;
        r2 = r0;
        r34 = r88;
        r5 = r29;
        r1 = -5;
        r3 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1071:0x1270, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1072:0x1271, code lost:
        r4 = r88;
        r26 = r90;
        r8 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1076:0x1281, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1077:0x1282, code lost:
        r26 = r90;
        r8 = r9;
        r61 = r23;
        r2 = r0;
        r34 = r88;
        r5 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1092:0x12e2, code lost:
        r47 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x037d, code lost:
        r4 = java.nio.ByteBuffer.allocate(r6);
        r7 = java.nio.ByteBuffer.allocate(r14.size - r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x0388, code lost:
        r21 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:?, code lost:
        r4.put(r3, 0, r6).position(0);
        r7.put(r3, r6, r14.size - r6).position(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x039c, code lost:
        r6 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x039e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x039f, code lost:
        r2 = r0;
        r6 = r13;
        r1 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x03ae, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x03af, code lost:
        r21 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x04b5, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x04b6, code lost:
        r21 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x04f8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x04f9, code lost:
        r8 = r83;
        r34 = r88;
        r26 = r90;
        r2 = r0;
        r1 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x0503, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x0504, code lost:
        r40 = r40;
        r6 = r13;
        r1 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x050c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x050d, code lost:
        r21 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x0516, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x0524, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:273:0x0525, code lost:
        r13 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x0547, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0548, code lost:
        r8 = r83;
        r34 = r88;
        r26 = r90;
        r2 = r0;
        r9 = r50;
        r11 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x0554, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0555, code lost:
        r2 = r40;
        r6 = r81;
        r11 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x056a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x056b, code lost:
        r13 = r6;
        r50 = r9;
        r51 = r11;
        r2 = r0;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x0573, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x0574, code lost:
        r50 = r9;
        r51 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x0579, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x057a, code lost:
        r50 = r9;
        r51 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x08d3, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x08df, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x08e0, code lost:
        r8 = r83;
        r6 = r84;
        r34 = r88;
        r2 = r0;
        r28 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x0999, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x09cb, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x09cd, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x01b2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x01b3, code lost:
        r6 = r81;
        r2 = r0;
        r11 = r51;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0b4c, code lost:
        if (r13.presentationTimeUs < r4) goto L_0x0b4e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0CLASSNAME, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0c7d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x01d4, code lost:
        r6 = r7;
        r7 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x0dd5, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x0dd6, code lost:
        r57 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x0e52, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x0e54, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x0e55, code lost:
        r8 = r83;
        r2 = r0;
        r6 = r12;
        r5 = r29;
        r3 = r57;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x0var_, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x0var_, code lost:
        r12 = r81;
        r11 = r82;
        r8 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x0f0d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x0f0e, code lost:
        r8 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x0fb4, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:0x104c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:989:0x1088, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:990:0x1089, code lost:
        r1 = r88;
        r2 = r0;
        r3 = r5;
        r56 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x1090, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:992:0x1091, code lost:
        r12 = r81;
        r11 = r82;
        r1 = r88;
        r2 = r0;
        r9 = r29;
        r7 = false;
        r74 = r26;
        r26 = r34;
        r34 = r74;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1001:0x10d1 A[ExcHandler: all (th java.lang.Throwable), PHI: r8 
      PHI: (r8v65 int) = (r8v64 int), (r8v71 int), (r8v71 int), (r8v71 int), (r8v71 int) binds: [B:998:0x10b9, B:911:0x0var_, B:912:?, B:905:0x0var_, B:906:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:905:0x0var_] */
    /* JADX WARNING: Removed duplicated region for block: B:1037:0x119a A[ExcHandler: all (th java.lang.Throwable), Splitter:B:693:0x0CLASSNAME] */
    /* JADX WARNING: Removed duplicated region for block: B:1060:0x1216 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:478:0x08b1] */
    /* JADX WARNING: Removed duplicated region for block: B:1071:0x1270 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:468:0x0851] */
    /* JADX WARNING: Removed duplicated region for block: B:1091:0x12e0 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:1106:0x1329 A[Catch:{ all -> 0x1338 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1120:0x135d  */
    /* JADX WARNING: Removed duplicated region for block: B:1122:0x1378 A[SYNTHETIC, Splitter:B:1122:0x1378] */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x1387 A[Catch:{ all -> 0x137c }] */
    /* JADX WARNING: Removed duplicated region for block: B:1130:0x138c A[Catch:{ all -> 0x137c }] */
    /* JADX WARNING: Removed duplicated region for block: B:1132:0x1394 A[Catch:{ all -> 0x137c }] */
    /* JADX WARNING: Removed duplicated region for block: B:1137:0x13a1  */
    /* JADX WARNING: Removed duplicated region for block: B:1140:0x13a8 A[SYNTHETIC, Splitter:B:1140:0x13a8] */
    /* JADX WARNING: Removed duplicated region for block: B:1154:0x1411  */
    /* JADX WARNING: Removed duplicated region for block: B:1157:0x1418 A[SYNTHETIC, Splitter:B:1157:0x1418] */
    /* JADX WARNING: Removed duplicated region for block: B:1163:0x1430  */
    /* JADX WARNING: Removed duplicated region for block: B:1165:0x1459  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x0418 A[Catch:{ Exception -> 0x04b1, all -> 0x04ac }] */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x041a A[Catch:{ Exception -> 0x04b1, all -> 0x04ac }] */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0429  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x0440  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x04f8 A[ExcHandler: all (r0v170 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r21 
      PHI: (r21v22 int) = (r21v21 int), (r21v33 int), (r21v33 int) binds: [B:254:0x04c0, B:189:0x038b, B:190:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:189:0x038b] */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x050c A[ExcHandler: all (th java.lang.Throwable), PHI: r1 
      PHI: (r1v182 int) = (r1v181 int), (r1v181 int), (r1v181 int), (r1v181 int), (r1v181 int), (r1v181 int), (r1v181 int), (r1v209 int), (r1v181 int) binds: [B:73:0x01d7, B:74:?, B:78:0x01e5, B:116:0x0299, B:117:?, B:125:0x02a8, B:126:?, B:178:0x0367, B:122:0x02a2] A[DONT_GENERATE, DONT_INLINE], Splitter:B:78:0x01e5] */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0516 A[ExcHandler: all (th java.lang.Throwable), PHI: r1 r51 
      PHI: (r1v180 int) = (r1v175 int), (r1v175 int), (r1v181 int), (r1v181 int), (r1v181 int), (r1v181 int) binds: [B:67:0x01c7, B:68:?, B:137:0x02bd, B:120:0x029f, B:121:?, B:86:0x0204] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r51v10 int) = (r51v5 int), (r51v5 int), (r51v11 int), (r51v11 int), (r51v11 int), (r51v11 int) binds: [B:67:0x01c7, B:68:?, B:137:0x02bd, B:120:0x029f, B:121:?, B:86:0x0204] A[DONT_GENERATE, DONT_INLINE], Splitter:B:67:0x01c7] */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x0547 A[ExcHandler: all (r0v152 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:53:0x01a6] */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x0573 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:39:0x0112] */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x059f A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x05e2 A[SYNTHETIC, Splitter:B:320:0x05e2] */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x05f1 A[Catch:{ all -> 0x05e6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x05f6 A[Catch:{ all -> 0x05e6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x06d0  */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x071f A[SYNTHETIC, Splitter:B:393:0x071f] */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0748  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x074b  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0751 A[SYNTHETIC, Splitter:B:409:0x0751] */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x0785  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x07b8  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x07cf  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x07d9 A[SYNTHETIC, Splitter:B:444:0x07d9] */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x081d A[SYNTHETIC, Splitter:B:451:0x081d] */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x084d  */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x08ca A[SYNTHETIC, Splitter:B:487:0x08ca] */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x08d3 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:487:0x08ca] */
    /* JADX WARNING: Removed duplicated region for block: B:494:0x08ed  */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x08f3 A[SYNTHETIC, Splitter:B:497:0x08f3] */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x0921  */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x0924  */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x09cb A[ExcHandler: all (th java.lang.Throwable), Splitter:B:535:0x0985] */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x09cf  */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09f8  */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x0a06  */
    /* JADX WARNING: Removed duplicated region for block: B:573:0x0a09  */
    /* JADX WARNING: Removed duplicated region for block: B:578:0x0a2b A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a49 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:598:0x0a7a  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0bd9  */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0bf9 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:696:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:697:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0c2f  */
    /* JADX WARNING: Removed duplicated region for block: B:704:0x0c4e  */
    /* JADX WARNING: Removed duplicated region for block: B:712:0x0CLASSNAME A[Catch:{ Exception -> 0x0c7d, all -> 0x0CLASSNAME }, ExcHandler: all (th java.lang.Throwable), Splitter:B:706:0x0CLASSNAME] */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x0e52 A[ExcHandler: all (th java.lang.Throwable), PHI: r34 
      PHI: (r34v68 long) = (r34v69 long), (r34v69 long), (r34v69 long), (r34v72 long) binds: [B:798:0x0da3, B:799:?, B:801:0x0da8, B:771:0x0d44] A[DONT_GENERATE, DONT_INLINE], Splitter:B:771:0x0d44] */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x0e80 A[Catch:{ Exception -> 0x112f, all -> 0x1129 }] */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x0e83 A[Catch:{ Exception -> 0x112f, all -> 0x1129 }] */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x0e93  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x0eb3  */
    /* JADX WARNING: Removed duplicated region for block: B:874:0x0f0d A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:868:0x0ee7] */
    /* JADX WARNING: Removed duplicated region for block: B:924:0x0fb4 A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:899:0x0var_] */
    /* JADX WARNING: Removed duplicated region for block: B:932:0x0fd6  */
    /* JADX WARNING: Removed duplicated region for block: B:933:0x0fd9  */
    /* JADX WARNING: Removed duplicated region for block: B:940:0x0fe7 A[SYNTHETIC, Splitter:B:940:0x0fe7] */
    /* JADX WARNING: Removed duplicated region for block: B:945:0x100b A[Catch:{ Exception -> 0x1019, all -> 0x1090 }] */
    /* JADX WARNING: Removed duplicated region for block: B:953:0x1020 A[Catch:{ Exception -> 0x1019, all -> 0x1090 }] */
    /* JADX WARNING: Removed duplicated region for block: B:954:0x1023 A[Catch:{ Exception -> 0x1019, all -> 0x1090 }] */
    /* JADX WARNING: Removed duplicated region for block: B:962:0x1038  */
    /* JADX WARNING: Removed duplicated region for block: B:980:0x1069 A[Catch:{ Exception -> 0x1088, all -> 0x1090 }] */
    /* JADX WARNING: Removed duplicated region for block: B:983:0x1075 A[Catch:{ Exception -> 0x1088, all -> 0x1090 }] */
    /* JADX WARNING: Removed duplicated region for block: B:988:0x1084  */
    /* JADX WARNING: Removed duplicated region for block: B:991:0x1090 A[ExcHandler: all (r0v53 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:959:0x1033] */
    @android.annotation.TargetApi(18)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideoInternal(java.lang.String r77, java.io.File r78, int r79, boolean r80, int r81, int r82, int r83, int r84, int r85, long r86, long r88, long r90, long r92, boolean r94, boolean r95, org.telegram.messenger.MediaController.SavedFilterState r96, java.lang.String r97, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r98, boolean r99, org.telegram.messenger.MediaController.CropState r100) {
        /*
            r76 = this;
            r15 = r76
            r13 = r77
            r14 = r79
            r12 = r81
            r11 = r82
            r10 = r83
            r9 = r84
            r8 = r85
            r6 = r86
            r4 = r92
            r3 = r94
            r2 = r100
            long r16 = java.lang.System.currentTimeMillis()
            r18 = -1
            r7 = 0
            android.media.MediaCodec$BufferInfo r6 = new android.media.MediaCodec$BufferInfo     // Catch:{ all -> 0x13ce }
            r6.<init>()     // Catch:{ all -> 0x13ce }
            org.telegram.messenger.video.Mp4Movie r1 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ all -> 0x13ce }
            r1.<init>()     // Catch:{ all -> 0x13ce }
            r22 = r6
            r6 = r78
            r1.setCacheFile(r6)     // Catch:{ all -> 0x13ce }
            r1.setRotation(r7)     // Catch:{ all -> 0x13ce }
            r1.setSize(r12, r11)     // Catch:{ all -> 0x13ce }
            org.telegram.messenger.video.MP4Builder r7 = new org.telegram.messenger.video.MP4Builder     // Catch:{ all -> 0x13ce }
            r7.<init>()     // Catch:{ all -> 0x13ce }
            r6 = r80
            org.telegram.messenger.video.MP4Builder r1 = r7.createMovie(r1, r6)     // Catch:{ all -> 0x13ce }
            r15.mediaMuxer = r1     // Catch:{ all -> 0x13ce }
            float r1 = (float) r4     // Catch:{ all -> 0x13ce }
            r24 = 1148846080(0x447a0000, float:1000.0)
            float r25 = r1 / r24
            r26 = 1000(0x3e8, double:4.94E-321)
            long r1 = r4 * r26
            r15.endPresentationTime = r1     // Catch:{ all -> 0x13ce }
            r76.checkConversionCanceled()     // Catch:{ all -> 0x13ce }
            java.lang.String r7 = "csd-1"
            java.lang.String r1 = "csd-0"
            java.lang.String r6 = "prepend-sps-pps-to-idr-frames"
            r2 = 921600(0xe1000, float:1.291437E-39)
            r28 = r6
            r27 = r7
            java.lang.String r7 = "video/avc"
            r34 = r7
            r6 = 0
            if (r99 == 0) goto L_0x0631
            int r18 = (r90 > r6 ? 1 : (r90 == r6 ? 0 : -1))
            if (r18 < 0) goto L_0x008d
            r2 = 1157234688(0x44fa0000, float:2000.0)
            int r2 = (r25 > r2 ? 1 : (r25 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0078
            r2 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            r9 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x0092
        L_0x0078:
            r2 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r2 = (r25 > r2 ? 1 : (r25 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0086
            r2 = 2200000(0x2191c0, float:3.082857E-39)
            r9 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x0092
        L_0x0086:
            r2 = 1560000(0x17cdc0, float:2.186026E-39)
            r9 = 1560000(0x17cdc0, float:2.186026E-39)
            goto L_0x0092
        L_0x008d:
            if (r9 > 0) goto L_0x0092
            r9 = 921600(0xe1000, float:1.291437E-39)
        L_0x0092:
            int r2 = r12 % 16
            r18 = 1098907648(0x41800000, float:16.0)
            if (r2 == 0) goto L_0x00db
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            if (r2 == 0) goto L_0x00c1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            r2.<init>()     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            java.lang.String r6 = "changing width from "
            r2.append(r6)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            r2.append(r12)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            float r6 = (float) r12     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            float r6 = r6 / r18
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
        L_0x00c1:
            float r2 = (float) r12     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            float r2 = r2 / r18
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            int r2 = r2 * 16
            r12 = r2
            goto L_0x00db
        L_0x00cc:
            r0 = move-exception
            r34 = r88
            r26 = r90
            r2 = r0
            r8 = r10
            goto L_0x058b
        L_0x00d5:
            r0 = move-exception
            r2 = r0
            r50 = r9
            goto L_0x0595
        L_0x00db:
            int r2 = r11 % 16
            if (r2 == 0) goto L_0x0112
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            if (r2 == 0) goto L_0x0108
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            r2.<init>()     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            java.lang.String r6 = "changing height from "
            r2.append(r6)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            r2.append(r11)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            float r6 = (float) r11     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            float r6 = r6 / r18
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
        L_0x0108:
            float r2 = (float) r11     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            float r2 = r2 / r18
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            int r2 = r2 * 16
            r11 = r2
        L_0x0112:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0579, all -> 0x0573 }
            if (r2 == 0) goto L_0x013a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            r2.<init>()     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            java.lang.String r6 = "create photo encoder "
            r2.append(r6)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            r2.append(r12)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            java.lang.String r6 = " "
            r2.append(r6)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            r2.append(r11)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            java.lang.String r6 = " duration = "
            r2.append(r6)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            r2.append(r4)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d5, all -> 0x00cc }
        L_0x013a:
            r7 = r34
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r7, r12, r11)     // Catch:{ Exception -> 0x0579, all -> 0x0573 }
            java.lang.String r6 = "color-format"
            r34 = r1
            r1 = 2130708361(0x7var_, float:1.701803E38)
            r2.setInteger(r6, r1)     // Catch:{ Exception -> 0x0579, all -> 0x0573 }
            java.lang.String r1 = "bitrate"
            r2.setInteger(r1, r9)     // Catch:{ Exception -> 0x0579, all -> 0x0573 }
            java.lang.String r1 = "frame-rate"
            r2.setInteger(r1, r10)     // Catch:{ Exception -> 0x0579, all -> 0x0573 }
            java.lang.String r1 = "i-frame-interval"
            r6 = 2
            r2.setInteger(r1, r6)     // Catch:{ Exception -> 0x0579, all -> 0x0573 }
            android.media.MediaCodec r6 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x0579, all -> 0x0573 }
            r18 = r7
            r1 = 0
            r7 = 1
            r6.configure(r2, r1, r1, r7)     // Catch:{ Exception -> 0x056a, all -> 0x0573 }
            org.telegram.messenger.video.InputSurface r2 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x056a, all -> 0x0573 }
            android.view.Surface r1 = r6.createInputSurface()     // Catch:{ Exception -> 0x056a, all -> 0x0573 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x056a, all -> 0x0573 }
            r2.makeCurrent()     // Catch:{ Exception -> 0x055d, all -> 0x0573 }
            r6.start()     // Catch:{ Exception -> 0x055d, all -> 0x0573 }
            org.telegram.messenger.video.OutputSurface r19 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x055d, all -> 0x0573 }
            r20 = 0
            float r1 = (float) r10
            r32 = 1
            r21 = r1
            r39 = r34
            r34 = 0
            r1 = r19
            r40 = r2
            r2 = r96
            r3 = r77
            r4 = r97
            r5 = r98
            r7 = r6
            r14 = r22
            r13 = 21
            r6 = r20
            r81 = r7
            r49 = r18
            r48 = r27
            r7 = r12
            r8 = r11
            r50 = r9
            r9 = r79
            r10 = r21
            r51 = r11
            r11 = r32
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0554, all -> 0x0547 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x053d, all -> 0x0547 }
            if (r1 >= r13) goto L_0x01bb
            java.nio.ByteBuffer[] r6 = r81.getOutputBuffers()     // Catch:{ Exception -> 0x01b2, all -> 0x0547 }
            goto L_0x01bc
        L_0x01b2:
            r0 = move-exception
            r6 = r81
            r2 = r0
            r11 = r51
            r1 = -5
            goto L_0x059b
        L_0x01bb:
            r6 = 0
        L_0x01bc:
            r76.checkConversionCanceled()     // Catch:{ Exception -> 0x053d, all -> 0x0547 }
            r1 = -5
            r2 = 1
            r3 = 0
            r4 = 0
            r5 = 0
            r7 = 0
        L_0x01c5:
            if (r7 != 0) goto L_0x052d
            r76.checkConversionCanceled()     // Catch:{ Exception -> 0x0524, all -> 0x0516 }
            r8 = r3 ^ 1
            r9 = r7
            r7 = r6
            r6 = 1
        L_0x01cf:
            if (r8 != 0) goto L_0x01d7
            if (r6 == 0) goto L_0x01d4
            goto L_0x01d7
        L_0x01d4:
            r6 = r7
            r7 = r9
            goto L_0x01c5
        L_0x01d7:
            r76.checkConversionCanceled()     // Catch:{ Exception -> 0x0510, all -> 0x050c }
            if (r95 == 0) goto L_0x01e1
            r10 = 22000(0x55f0, double:1.08694E-319)
            r13 = r81
            goto L_0x01e5
        L_0x01e1:
            r13 = r81
            r10 = 2500(0x9c4, double:1.235E-320)
        L_0x01e5:
            int r10 = r13.dequeueOutputBuffer(r14, r10)     // Catch:{ Exception -> 0x050a, all -> 0x050c }
            r11 = -1
            if (r10 != r11) goto L_0x0201
            r81 = r1
            r20 = r4
            r18 = r5
            r82 = r8
            r8 = r39
            r11 = r48
            r4 = r49
            r1 = -1
            r6 = 0
        L_0x01fc:
            r5 = r3
            r3 = r51
            goto L_0x0427
        L_0x0201:
            r11 = -3
            if (r10 != r11) goto L_0x0229
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            r81 = r6
            r6 = 21
            if (r11 >= r6) goto L_0x0210
            java.nio.ByteBuffer[] r7 = r13.getOutputBuffers()     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
        L_0x0210:
            r6 = r81
            r81 = r1
            r20 = r4
            r18 = r5
            r82 = r8
            r8 = r39
            r11 = r48
        L_0x021e:
            r4 = r49
            r1 = -1
            goto L_0x01fc
        L_0x0222:
            r0 = move-exception
        L_0x0223:
            r2 = r0
        L_0x0224:
            r6 = r13
        L_0x0225:
            r11 = r51
            goto L_0x059b
        L_0x0229:
            r81 = r6
            r6 = -2
            if (r10 != r6) goto L_0x0291
            android.media.MediaFormat r6 = r13.getOutputFormat()     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            if (r11 == 0) goto L_0x024d
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            r11.<init>()     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            r82 = r8
            java.lang.String r8 = "photo encoder new format "
            r11.append(r8)     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            r11.append(r6)     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            java.lang.String r8 = r11.toString()     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            org.telegram.messenger.FileLog.d(r8)     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            goto L_0x024f
        L_0x024d:
            r82 = r8
        L_0x024f:
            r8 = -5
            if (r1 != r8) goto L_0x0284
            if (r6 == 0) goto L_0x0284
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            r8 = 0
            int r1 = r11.addTrack(r6, r8)     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            r11 = r28
            boolean r18 = r6.containsKey(r11)     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            if (r18 == 0) goto L_0x0282
            int r8 = r6.getInteger(r11)     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            r28 = r11
            r11 = 1
            if (r8 != r11) goto L_0x0284
            r8 = r39
            java.nio.ByteBuffer r4 = r6.getByteBuffer(r8)     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            r11 = r48
            java.nio.ByteBuffer r6 = r6.getByteBuffer(r11)     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            int r4 = r4.limit()     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            int r6 = r6.limit()     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            int r4 = r4 + r6
            goto L_0x0288
        L_0x0282:
            r28 = r11
        L_0x0284:
            r8 = r39
            r11 = r48
        L_0x0288:
            r6 = r81
            r81 = r1
            r20 = r4
            r18 = r5
            goto L_0x021e
        L_0x0291:
            r82 = r8
            r8 = r39
            r11 = r48
            if (r10 < 0) goto L_0x04dc
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x050a, all -> 0x050c }
            r9 = 21
            if (r6 >= r9) goto L_0x02a2
            r6 = r7[r10]     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            goto L_0x02a6
        L_0x02a2:
            java.nio.ByteBuffer r6 = r13.getOutputBuffer(r10)     // Catch:{ Exception -> 0x050a, all -> 0x050c }
        L_0x02a6:
            if (r6 == 0) goto L_0x04bc
            int r9 = r14.size     // Catch:{ Exception -> 0x04b5, all -> 0x050c }
            r84 = r7
            r7 = 1
            if (r9 <= r7) goto L_0x0405
            int r7 = r14.flags     // Catch:{ Exception -> 0x03fb, all -> 0x03ea }
            r18 = r7 & 2
            if (r18 != 0) goto L_0x0346
            if (r4 == 0) goto L_0x02c6
            r18 = r7 & 1
            if (r18 == 0) goto L_0x02c6
            r18 = r5
            int r5 = r14.offset     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            int r5 = r5 + r4
            r14.offset = r5     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            int r9 = r9 - r4
            r14.size = r9     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            goto L_0x02c8
        L_0x02c6:
            r18 = r5
        L_0x02c8:
            if (r2 == 0) goto L_0x0317
            r5 = r7 & 1
            if (r5 == 0) goto L_0x0317
            int r2 = r14.size     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            r7 = 100
            if (r2 <= r7) goto L_0x0315
            int r2 = r14.offset     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            r6.position(r2)     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            byte[] r2 = new byte[r7]     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            r6.get(r2)     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            r5 = 0
            r9 = 0
        L_0x02e0:
            r7 = 96
            if (r5 >= r7) goto L_0x0315
            byte r7 = r2[r5]     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            if (r7 != 0) goto L_0x030c
            int r7 = r5 + 1
            byte r7 = r2[r7]     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            if (r7 != 0) goto L_0x030c
            int r7 = r5 + 2
            byte r7 = r2[r7]     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            if (r7 != 0) goto L_0x030c
            int r7 = r5 + 3
            byte r7 = r2[r7]     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            r20 = r2
            r2 = 1
            if (r7 != r2) goto L_0x030e
            int r9 = r9 + 1
            if (r9 <= r2) goto L_0x030e
            int r2 = r14.offset     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            int r2 = r2 + r5
            r14.offset = r2     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            int r2 = r14.size     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            int r2 = r2 - r5
            r14.size = r2     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            goto L_0x0315
        L_0x030c:
            r20 = r2
        L_0x030e:
            int r5 = r5 + 1
            r2 = r20
            r7 = 100
            goto L_0x02e0
        L_0x0315:
            r7 = 0
            goto L_0x0318
        L_0x0317:
            r7 = r2
        L_0x0318:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            r9 = r7
            r5 = 1
            long r6 = r2.writeSampleData(r1, r6, r14, r5)     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            r5 = r3
            r2 = 0
            int r20 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r20 == 0) goto L_0x033a
            org.telegram.messenger.MediaController$VideoConvertorListener r2 = r15.callback     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            if (r2 == 0) goto L_0x033a
            r20 = r4
            r21 = r9
            r3 = 0
            float r9 = (float) r3     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            float r9 = r9 / r24
            float r9 = r9 / r25
            r2.didWriteData(r6, r9)     // Catch:{ Exception -> 0x0222, all -> 0x0516 }
            goto L_0x033e
        L_0x033a:
            r20 = r4
            r21 = r9
        L_0x033e:
            r2 = r21
            r4 = r49
            r3 = r51
            goto L_0x0412
        L_0x0346:
            r20 = r4
            r18 = r5
            r7 = -5
            r5 = r3
            if (r1 != r7) goto L_0x03e5
            byte[] r3 = new byte[r9]     // Catch:{ Exception -> 0x03fb, all -> 0x03ea }
            int r4 = r14.offset     // Catch:{ Exception -> 0x03fb, all -> 0x03ea }
            int r4 = r4 + r9
            r6.limit(r4)     // Catch:{ Exception -> 0x03fb, all -> 0x03ea }
            int r4 = r14.offset     // Catch:{ Exception -> 0x03fb, all -> 0x03ea }
            r6.position(r4)     // Catch:{ Exception -> 0x03fb, all -> 0x03ea }
            r6.get(r3)     // Catch:{ Exception -> 0x03fb, all -> 0x03ea }
            int r4 = r14.size     // Catch:{ Exception -> 0x03fb, all -> 0x03ea }
            r6 = 1
            int r4 = r4 - r6
        L_0x0362:
            if (r4 < 0) goto L_0x03b3
            r9 = 3
            if (r4 <= r9) goto L_0x03b3
            byte r7 = r3[r4]     // Catch:{ Exception -> 0x03ae, all -> 0x050c }
            if (r7 != r6) goto L_0x03a5
            int r6 = r4 + -1
            byte r6 = r3[r6]     // Catch:{ Exception -> 0x03ae, all -> 0x050c }
            if (r6 != 0) goto L_0x03a5
            int r6 = r4 + -2
            byte r6 = r3[r6]     // Catch:{ Exception -> 0x03ae, all -> 0x050c }
            if (r6 != 0) goto L_0x03a5
            int r6 = r4 + -3
            byte r7 = r3[r6]     // Catch:{ Exception -> 0x03ae, all -> 0x050c }
            if (r7 != 0) goto L_0x03a5
            java.nio.ByteBuffer r4 = java.nio.ByteBuffer.allocate(r6)     // Catch:{ Exception -> 0x03ae, all -> 0x050c }
            int r7 = r14.size     // Catch:{ Exception -> 0x03ae, all -> 0x050c }
            int r7 = r7 - r6
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.allocate(r7)     // Catch:{ Exception -> 0x03ae, all -> 0x050c }
            r21 = r1
            r9 = 0
            java.nio.ByteBuffer r1 = r4.put(r3, r9, r6)     // Catch:{ Exception -> 0x039e, all -> 0x04f8 }
            r1.position(r9)     // Catch:{ Exception -> 0x039e, all -> 0x04f8 }
            int r1 = r14.size     // Catch:{ Exception -> 0x039e, all -> 0x04f8 }
            int r1 = r1 - r6
            java.nio.ByteBuffer r1 = r7.put(r3, r6, r1)     // Catch:{ Exception -> 0x039e, all -> 0x04f8 }
            r1.position(r9)     // Catch:{ Exception -> 0x039e, all -> 0x04f8 }
            r6 = r4
            goto L_0x03b7
        L_0x039e:
            r0 = move-exception
            r2 = r0
            r6 = r13
            r1 = r21
            goto L_0x0225
        L_0x03a5:
            r21 = r1
            int r4 = r4 + -1
            r1 = r21
            r6 = 1
            r7 = -5
            goto L_0x0362
        L_0x03ae:
            r0 = move-exception
            r21 = r1
            goto L_0x0223
        L_0x03b3:
            r21 = r1
            r6 = 0
            r7 = 0
        L_0x03b7:
            r4 = r49
            r3 = r51
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r4, r12, r3)     // Catch:{ Exception -> 0x03dd, all -> 0x03d1 }
            if (r6 == 0) goto L_0x03c9
            if (r7 == 0) goto L_0x03c9
            r1.setByteBuffer(r8, r6)     // Catch:{ Exception -> 0x03dd, all -> 0x03d1 }
            r1.setByteBuffer(r11, r7)     // Catch:{ Exception -> 0x03dd, all -> 0x03d1 }
        L_0x03c9:
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x03dd, all -> 0x03d1 }
            r7 = 0
            int r1 = r6.addTrack(r1, r7)     // Catch:{ Exception -> 0x03dd, all -> 0x03d1 }
            goto L_0x0412
        L_0x03d1:
            r0 = move-exception
            r8 = r83
            r34 = r88
            r26 = r90
            r2 = r0
            r11 = r3
            r1 = r21
            goto L_0x03f7
        L_0x03dd:
            r0 = move-exception
            r2 = r0
            r11 = r3
            r6 = r13
            r1 = r21
            goto L_0x059b
        L_0x03e5:
            r21 = r1
            r4 = r49
            goto L_0x040e
        L_0x03ea:
            r0 = move-exception
            r21 = r1
            r3 = r51
            r8 = r83
            r34 = r88
            r26 = r90
            r2 = r0
            r11 = r3
        L_0x03f7:
            r9 = r50
            goto L_0x058c
        L_0x03fb:
            r0 = move-exception
            r21 = r1
            r3 = r51
            r2 = r0
            r11 = r3
            r6 = r13
            goto L_0x059b
        L_0x0405:
            r21 = r1
            r20 = r4
            r18 = r5
            r4 = r49
            r5 = r3
        L_0x040e:
            r3 = r51
            r1 = r21
        L_0x0412:
            int r6 = r14.flags     // Catch:{ Exception -> 0x04b1, all -> 0x04ac }
            r6 = r6 & 4
            if (r6 == 0) goto L_0x041a
            r6 = 1
            goto L_0x041b
        L_0x041a:
            r6 = 0
        L_0x041b:
            r7 = 0
            r13.releaseOutputBuffer(r10, r7)     // Catch:{ Exception -> 0x04b1, all -> 0x04ac }
            r7 = r84
            r9 = r6
            r6 = r81
            r81 = r1
            r1 = -1
        L_0x0427:
            if (r10 == r1) goto L_0x0440
            r1 = r81
            r51 = r3
            r49 = r4
            r3 = r5
            r39 = r8
            r48 = r11
            r81 = r13
            r5 = r18
            r4 = r20
            r13 = 21
            r8 = r82
            goto L_0x01cf
        L_0x0440:
            if (r5 != 0) goto L_0x048b
            r19.drawImage()     // Catch:{ Exception -> 0x0482, all -> 0x047b }
            r1 = r18
            float r10 = (float) r1
            r18 = 1106247680(0x41var_, float:30.0)
            float r10 = r10 / r18
            float r10 = r10 * r24
            float r10 = r10 * r24
            float r10 = r10 * r24
            r84 = r2
            r51 = r3
            long r2 = (long) r10
            r10 = r40
            r10.setPresentationTime(r2)     // Catch:{ Exception -> 0x0473, all -> 0x0471 }
            r10.swapBuffers()     // Catch:{ Exception -> 0x0473, all -> 0x0471 }
            int r1 = r1 + 1
            float r2 = (float) r1     // Catch:{ Exception -> 0x0473, all -> 0x0471 }
            r3 = 1106247680(0x41var_, float:30.0)
            float r3 = r3 * r25
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 < 0) goto L_0x0493
            r13.signalEndOfInputStream()     // Catch:{ Exception -> 0x0473, all -> 0x0471 }
            r5 = r1
            r1 = 0
            r3 = 1
            goto L_0x0497
        L_0x0471:
            r0 = move-exception
            goto L_0x047e
        L_0x0473:
            r0 = move-exception
            r1 = r81
            r2 = r0
            r40 = r10
            goto L_0x0224
        L_0x047b:
            r0 = move-exception
            r51 = r3
        L_0x047e:
            r1 = r81
            goto L_0x0517
        L_0x0482:
            r0 = move-exception
            r51 = r3
            r10 = r40
            r1 = r81
            goto L_0x0223
        L_0x048b:
            r84 = r2
            r51 = r3
            r1 = r18
            r10 = r40
        L_0x0493:
            r3 = r5
            r5 = r1
            r1 = r82
        L_0x0497:
            r2 = r84
            r49 = r4
            r39 = r8
            r40 = r10
            r48 = r11
            r4 = r20
            r8 = r1
            r1 = r81
            r81 = r13
            r13 = 21
            goto L_0x01cf
        L_0x04ac:
            r0 = move-exception
            r51 = r3
            goto L_0x0517
        L_0x04b1:
            r0 = move-exception
            r51 = r3
            goto L_0x04b8
        L_0x04b5:
            r0 = move-exception
            r21 = r1
        L_0x04b8:
            r10 = r40
            goto L_0x0223
        L_0x04bc:
            r21 = r1
            r2 = r40
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0503, all -> 0x04f8 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0503, all -> 0x04f8 }
            r3.<init>()     // Catch:{ Exception -> 0x0503, all -> 0x04f8 }
            java.lang.String r4 = "encoderOutputBuffer "
            r3.append(r4)     // Catch:{ Exception -> 0x0503, all -> 0x04f8 }
            r3.append(r10)     // Catch:{ Exception -> 0x0503, all -> 0x04f8 }
            java.lang.String r4 = " was null"
            r3.append(r4)     // Catch:{ Exception -> 0x0503, all -> 0x04f8 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0503, all -> 0x04f8 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x0503, all -> 0x04f8 }
            throw r1     // Catch:{ Exception -> 0x0503, all -> 0x04f8 }
        L_0x04dc:
            r21 = r1
            r2 = r40
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0503, all -> 0x04f8 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0503, all -> 0x04f8 }
            r3.<init>()     // Catch:{ Exception -> 0x0503, all -> 0x04f8 }
            java.lang.String r4 = "unexpected result from encoder.dequeueOutputBuffer: "
            r3.append(r4)     // Catch:{ Exception -> 0x0503, all -> 0x04f8 }
            r3.append(r10)     // Catch:{ Exception -> 0x0503, all -> 0x04f8 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0503, all -> 0x04f8 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x0503, all -> 0x04f8 }
            throw r1     // Catch:{ Exception -> 0x0503, all -> 0x04f8 }
        L_0x04f8:
            r0 = move-exception
            r8 = r83
            r34 = r88
            r26 = r90
            r2 = r0
            r1 = r21
            goto L_0x051e
        L_0x0503:
            r0 = move-exception
            r40 = r2
            r6 = r13
            r1 = r21
            goto L_0x052a
        L_0x050a:
            r0 = move-exception
            goto L_0x0513
        L_0x050c:
            r0 = move-exception
            r21 = r1
            goto L_0x0517
        L_0x0510:
            r0 = move-exception
            r13 = r81
        L_0x0513:
            r21 = r1
            goto L_0x0527
        L_0x0516:
            r0 = move-exception
        L_0x0517:
            r8 = r83
            r34 = r88
            r26 = r90
            r2 = r0
        L_0x051e:
            r9 = r50
            r11 = r51
            goto L_0x058c
        L_0x0524:
            r0 = move-exception
            r13 = r81
        L_0x0527:
            r2 = r40
            r6 = r13
        L_0x052a:
            r11 = r51
            goto L_0x0568
        L_0x052d:
            r13 = r81
            r2 = r40
            r6 = r13
            r9 = r50
            r11 = r51
            r7 = 0
            r47 = 0
            r13 = r83
            goto L_0x05e0
        L_0x053d:
            r0 = move-exception
            r13 = r81
            r2 = r40
            r6 = r13
            r11 = r51
            r1 = -5
            goto L_0x0568
        L_0x0547:
            r0 = move-exception
            r8 = r83
            r34 = r88
            r26 = r90
            r2 = r0
            r9 = r50
            r11 = r51
            goto L_0x058b
        L_0x0554:
            r0 = move-exception
            r13 = r81
            r2 = r40
            r6 = r13
            r11 = r51
            goto L_0x0565
        L_0x055d:
            r0 = move-exception
            r13 = r6
            r50 = r9
            r51 = r11
            r40 = r2
        L_0x0565:
            r1 = -5
            r19 = 0
        L_0x0568:
            r2 = r0
            goto L_0x059b
        L_0x056a:
            r0 = move-exception
            r13 = r6
            r50 = r9
            r51 = r11
            r2 = r0
            r1 = -5
            goto L_0x0597
        L_0x0573:
            r0 = move-exception
            r50 = r9
            r51 = r11
            goto L_0x0584
        L_0x0579:
            r0 = move-exception
            r50 = r9
            r51 = r11
            goto L_0x0594
        L_0x057f:
            r0 = move-exception
            r50 = r9
            r11 = r82
        L_0x0584:
            r8 = r83
        L_0x0586:
            r34 = r88
            r26 = r90
            r2 = r0
        L_0x058b:
            r1 = -5
        L_0x058c:
            r7 = 0
            goto L_0x13dd
        L_0x058f:
            r0 = move-exception
            r50 = r9
            r11 = r82
        L_0x0594:
            r2 = r0
        L_0x0595:
            r1 = -5
            r6 = 0
        L_0x0597:
            r19 = 0
            r40 = 0
        L_0x059b:
            boolean r3 = r2 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x0625 }
            if (r3 == 0) goto L_0x05a4
            if (r95 != 0) goto L_0x05a4
            r47 = 1
            goto L_0x05a6
        L_0x05a4:
            r47 = 0
        L_0x05a6:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0617 }
            r3.<init>()     // Catch:{ all -> 0x0617 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x0617 }
            r9 = r50
            r3.append(r9)     // Catch:{ all -> 0x0615 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x0615 }
            r13 = r83
            r3.append(r13)     // Catch:{ all -> 0x060d }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x060d }
            r3.append(r11)     // Catch:{ all -> 0x060d }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x060d }
            r3.append(r12)     // Catch:{ all -> 0x060d }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x060d }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x060d }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x060d }
            r2 = r40
            r7 = r47
            r47 = 1
        L_0x05e0:
            if (r19 == 0) goto L_0x05ef
            r19.release()     // Catch:{ all -> 0x05e6 }
            goto L_0x05ef
        L_0x05e6:
            r0 = move-exception
            r34 = r88
            r26 = r90
            r2 = r0
            r8 = r13
            goto L_0x13dd
        L_0x05ef:
            if (r2 == 0) goto L_0x05f4
            r2.release()     // Catch:{ all -> 0x05e6 }
        L_0x05f4:
            if (r6 == 0) goto L_0x05fc
            r6.stop()     // Catch:{ all -> 0x05e6 }
            r6.release()     // Catch:{ all -> 0x05e6 }
        L_0x05fc:
            r76.checkConversionCanceled()     // Catch:{ all -> 0x05e6 }
            r34 = r88
            r26 = r90
            r29 = r9
            r9 = r12
            r8 = r13
            r6 = r47
            r13 = r7
            r7 = r11
            goto L_0x139d
        L_0x060d:
            r0 = move-exception
            r34 = r88
            r26 = r90
            r2 = r0
            r8 = r13
            goto L_0x0621
        L_0x0615:
            r0 = move-exception
            goto L_0x061a
        L_0x0617:
            r0 = move-exception
            r9 = r50
        L_0x061a:
            r8 = r83
            r34 = r88
            r26 = r90
            r2 = r0
        L_0x0621:
            r7 = r47
            goto L_0x13dd
        L_0x0625:
            r0 = move-exception
            r9 = r50
            r8 = r83
            r34 = r88
            r26 = r90
            r2 = r0
            goto L_0x058c
        L_0x0631:
            r8 = r1
            r13 = r10
            r14 = r22
            r11 = r27
            r1 = r28
            r4 = r34
            r10 = 3
            android.media.MediaExtractor r3 = new android.media.MediaExtractor     // Catch:{ all -> 0x13c0 }
            r3.<init>()     // Catch:{ all -> 0x13c0 }
            r15.extractor = r3     // Catch:{ all -> 0x13c0 }
            r7 = r77
            r6 = r1
            r3.setDataSource(r7)     // Catch:{ all -> 0x13c0 }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ all -> 0x13c0 }
            r5 = 0
            int r3 = org.telegram.messenger.MediaController.findTrack(r1, r5)     // Catch:{ all -> 0x13c0 }
            r1 = -1
            if (r9 == r1) goto L_0x0668
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ all -> 0x065d }
            r48 = r11
            r11 = 1
            int r1 = org.telegram.messenger.MediaController.findTrack(r1, r11)     // Catch:{ all -> 0x065d }
            goto L_0x066b
        L_0x065d:
            r0 = move-exception
            r11 = r82
            r34 = r88
            r26 = r90
            r2 = r0
            r8 = r13
            goto L_0x058b
        L_0x0668:
            r48 = r11
            r1 = -1
        L_0x066b:
            java.lang.String r11 = "mime"
            if (r3 < 0) goto L_0x0683
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ all -> 0x065d }
            android.media.MediaFormat r5 = r5.getTrackFormat(r3)     // Catch:{ all -> 0x065d }
            java.lang.String r5 = r5.getString(r11)     // Catch:{ all -> 0x065d }
            boolean r5 = r5.equals(r4)     // Catch:{ all -> 0x065d }
            if (r5 != 0) goto L_0x0683
            r13 = r94
            r5 = 1
            goto L_0x0686
        L_0x0683:
            r13 = r94
            r5 = 0
        L_0x0686:
            if (r13 != 0) goto L_0x06cb
            if (r5 == 0) goto L_0x068b
            goto L_0x06cb
        L_0x068b:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x06be }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ all -> 0x06be }
            r5 = -1
            if (r9 == r5) goto L_0x0695
            r18 = 1
            goto L_0x0697
        L_0x0695:
            r18 = 0
        L_0x0697:
            r1 = r76
            r4 = r14
            r8 = 0
            r5 = r86
            r14 = r7
            r10 = 0
            r11 = -5
            r7 = r88
            r13 = 0
            r9 = r92
            r11 = r78
            r12 = r18
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ all -> 0x06bc }
            r9 = r81
            r7 = r82
            r8 = r83
            r29 = r84
            r34 = r88
            r26 = r90
            r1 = -5
            r6 = 0
            goto L_0x139d
        L_0x06bc:
            r0 = move-exception
            goto L_0x06c1
        L_0x06be:
            r0 = move-exception
            r14 = r7
            r13 = 0
        L_0x06c1:
            r12 = r81
            r11 = r82
            r8 = r83
            r9 = r84
            goto L_0x0586
        L_0x06cb:
            r12 = r7
            r5 = -1
            r13 = 0
            if (r3 < 0) goto L_0x135d
            r20 = -2147483648(0xfffffffvar_, double:NaN)
            r7 = 1000(0x3e8, float:1.401E-42)
            r9 = r83
            int r7 = r7 / r9
            int r7 = r7 * 1000
            r22 = r14
            long r13 = (long) r7     // Catch:{ Exception -> 0x12c6, all -> 0x12b5 }
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x12c6, all -> 0x12b5 }
            r7.selectTrack(r3)     // Catch:{ Exception -> 0x12c6, all -> 0x12b5 }
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x12c6, all -> 0x12b5 }
            android.media.MediaFormat r7 = r7.getTrackFormat(r3)     // Catch:{ Exception -> 0x12c6, all -> 0x12b5 }
            r26 = 0
            int r23 = (r90 > r26 ? 1 : (r90 == r26 ? 0 : -1))
            if (r23 < 0) goto L_0x070b
            r23 = 1157234688(0x44fa0000, float:2000.0)
            int r23 = (r25 > r23 ? 1 : (r25 == r23 ? 0 : -1))
            if (r23 > 0) goto L_0x06f8
            r23 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x0706
        L_0x06f8:
            r23 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r23 = (r25 > r23 ? 1 : (r25 == r23 ? 0 : -1))
            if (r23 > 0) goto L_0x0703
            r23 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x0706
        L_0x0703:
            r23 = 1560000(0x17cdc0, float:2.186026E-39)
        L_0x0706:
            r5 = r23
            r26 = 0
            goto L_0x0719
        L_0x070b:
            if (r84 > 0) goto L_0x0715
            r26 = r90
            r23 = r3
            r5 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x071b
        L_0x0715:
            r5 = r84
            r26 = r90
        L_0x0719:
            r23 = r3
        L_0x071b:
            r3 = r85
            if (r3 <= 0) goto L_0x0742
            int r5 = java.lang.Math.min(r3, r5)     // Catch:{ Exception -> 0x0730, all -> 0x0724 }
            goto L_0x0742
        L_0x0724:
            r0 = move-exception
            r12 = r81
            r11 = r82
            r34 = r88
        L_0x072b:
            r2 = r0
            r8 = r9
            r1 = -5
            goto L_0x1359
        L_0x0730:
            r0 = move-exception
            r34 = r88
        L_0x0733:
            r2 = r0
            r8 = r9
            r61 = r23
        L_0x0737:
            r1 = -5
            r3 = 0
            r6 = 0
        L_0x073a:
            r28 = 0
            r30 = 0
            r56 = 0
            goto L_0x12dc
        L_0x0742:
            r28 = 0
            int r30 = (r26 > r28 ? 1 : (r26 == r28 ? 0 : -1))
            if (r30 < 0) goto L_0x074b
            r2 = r18
            goto L_0x074d
        L_0x074b:
            r2 = r26
        L_0x074d:
            int r26 = (r2 > r28 ? 1 : (r2 == r28 ? 0 : -1))
            if (r26 < 0) goto L_0x0785
            android.media.MediaExtractor r10 = r15.extractor     // Catch:{ Exception -> 0x0771, all -> 0x0762 }
            r26 = r1
            r1 = 0
            r10.seekTo(r2, r1)     // Catch:{ Exception -> 0x0771, all -> 0x0762 }
            r10 = r100
            r90 = r2
            r49 = r4
        L_0x075f:
            r2 = 0
            goto L_0x07b6
        L_0x0762:
            r0 = move-exception
            r12 = r81
            r11 = r82
            r34 = r88
            r26 = r2
            r8 = r9
            r1 = -5
            r7 = 0
            r2 = r0
            goto L_0x135a
        L_0x0771:
            r0 = move-exception
            r34 = r88
            r26 = r2
        L_0x0776:
            r8 = r9
            r61 = r23
            r1 = -5
            r3 = 0
            r6 = 0
            r28 = 0
            r30 = 0
            r56 = 0
        L_0x0782:
            r2 = r0
            goto L_0x12dc
        L_0x0785:
            r26 = r1
            r90 = r2
            r49 = r4
            r1 = 0
            r3 = r86
            int r10 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r10 <= 0) goto L_0x07ac
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x07a6, all -> 0x079c }
            r2 = 0
            r1.seekTo(r3, r2)     // Catch:{ Exception -> 0x07a6, all -> 0x079c }
            r10 = r100
            goto L_0x075f
        L_0x079c:
            r0 = move-exception
            r12 = r81
            r11 = r82
            r34 = r88
            r26 = r90
            goto L_0x072b
        L_0x07a6:
            r0 = move-exception
            r34 = r88
            r26 = r90
            goto L_0x0733
        L_0x07ac:
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x129e, all -> 0x1290 }
            r2 = 0
            r4 = 0
            r1.seekTo(r2, r4)     // Catch:{ Exception -> 0x129e, all -> 0x1290 }
            r10 = r100
        L_0x07b6:
            if (r10 == 0) goto L_0x07cf
            r1 = 90
            r4 = r79
            if (r4 == r1) goto L_0x07c8
            r1 = 270(0x10e, float:3.78E-43)
            if (r4 != r1) goto L_0x07c3
            goto L_0x07c8
        L_0x07c3:
            int r1 = r10.transformWidth     // Catch:{ Exception -> 0x07a6, all -> 0x079c }
            int r2 = r10.transformHeight     // Catch:{ Exception -> 0x07a6, all -> 0x079c }
            goto L_0x07cc
        L_0x07c8:
            int r1 = r10.transformHeight     // Catch:{ Exception -> 0x07a6, all -> 0x079c }
            int r2 = r10.transformWidth     // Catch:{ Exception -> 0x07a6, all -> 0x079c }
        L_0x07cc:
            r3 = r2
            r2 = r1
            goto L_0x07d5
        L_0x07cf:
            r4 = r79
            r2 = r81
            r3 = r82
        L_0x07d5:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x129e, all -> 0x1290 }
            if (r1 == 0) goto L_0x07f5
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x07a6, all -> 0x079c }
            r1.<init>()     // Catch:{ Exception -> 0x07a6, all -> 0x079c }
            java.lang.String r4 = "create encoder with w = "
            r1.append(r4)     // Catch:{ Exception -> 0x07a6, all -> 0x079c }
            r1.append(r2)     // Catch:{ Exception -> 0x07a6, all -> 0x079c }
            java.lang.String r4 = " h = "
            r1.append(r4)     // Catch:{ Exception -> 0x07a6, all -> 0x079c }
            r1.append(r3)     // Catch:{ Exception -> 0x07a6, all -> 0x079c }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x07a6, all -> 0x079c }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x07a6, all -> 0x079c }
        L_0x07f5:
            r4 = r49
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r4, r2, r3)     // Catch:{ Exception -> 0x129e, all -> 0x1290 }
            r28 = r6
            java.lang.String r6 = "color-format"
            r39 = r8
            r8 = 2130708361(0x7var_, float:1.701803E38)
            r1.setInteger(r6, r8)     // Catch:{ Exception -> 0x129e, all -> 0x1290 }
            java.lang.String r6 = "bitrate"
            r1.setInteger(r6, r5)     // Catch:{ Exception -> 0x129e, all -> 0x1290 }
            java.lang.String r6 = "frame-rate"
            r1.setInteger(r6, r9)     // Catch:{ Exception -> 0x129e, all -> 0x1290 }
            java.lang.String r6 = "i-frame-interval"
            r8 = 2
            r1.setInteger(r6, r8)     // Catch:{ Exception -> 0x129e, all -> 0x1290 }
            int r8 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x129e, all -> 0x1290 }
            r6 = 23
            if (r8 >= r6) goto L_0x084d
            int r6 = java.lang.Math.min(r3, r2)     // Catch:{ Exception -> 0x07a6, all -> 0x079c }
            r27 = r2
            r2 = 480(0x1e0, float:6.73E-43)
            if (r6 > r2) goto L_0x084f
            r2 = 921600(0xe1000, float:1.291437E-39)
            if (r5 <= r2) goto L_0x082d
            goto L_0x082e
        L_0x082d:
            r2 = r5
        L_0x082e:
            java.lang.String r5 = "bitrate"
            r1.setInteger(r5, r2)     // Catch:{ Exception -> 0x0845, all -> 0x0836 }
            r29 = r2
            goto L_0x0851
        L_0x0836:
            r0 = move-exception
            r12 = r81
            r11 = r82
            r34 = r88
            r26 = r90
            r8 = r9
            r1 = -5
            r7 = 0
            r9 = r2
            goto L_0x1382
        L_0x0845:
            r0 = move-exception
            r34 = r88
            r26 = r90
            r5 = r2
            goto L_0x0776
        L_0x084d:
            r27 = r2
        L_0x084f:
            r29 = r5
        L_0x0851:
            android.media.MediaCodec r6 = android.media.MediaCodec.createEncoderByType(r4)     // Catch:{ Exception -> 0x1281, all -> 0x1270 }
            r2 = 1
            r5 = 0
            r6.configure(r1, r5, r5, r2)     // Catch:{ Exception -> 0x125e, all -> 0x1270 }
            org.telegram.messenger.video.InputSurface r1 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x125e, all -> 0x1270 }
            android.view.Surface r2 = r6.createInputSurface()     // Catch:{ Exception -> 0x125e, all -> 0x1270 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x125e, all -> 0x1270 }
            r1.makeCurrent()     // Catch:{ Exception -> 0x1245, all -> 0x1270 }
            r6.start()     // Catch:{ Exception -> 0x1245, all -> 0x1270 }
            java.lang.String r2 = r7.getString(r11)     // Catch:{ Exception -> 0x1245, all -> 0x1270 }
            android.media.MediaCodec r2 = android.media.MediaCodec.createDecoderByType(r2)     // Catch:{ Exception -> 0x1245, all -> 0x1270 }
            org.telegram.messenger.video.OutputSurface r30 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x122d, all -> 0x1270 }
            r31 = 0
            r32 = r3
            float r3 = (float) r9
            r33 = 0
            r56 = r1
            r55 = r26
            r1 = r30
            r57 = r2
            r58 = r27
            r26 = r90
            r2 = r96
            r61 = r23
            r62 = r32
            r23 = r3
            r3 = r31
            r63 = r4
            r4 = r97
            r5 = r98
            r84 = r6
            r64 = r28
            r6 = r100
            r65 = r7
            r7 = r81
            r66 = r8
            r67 = r39
            r8 = r82
            r9 = r79
            r10 = r23
            r39 = r13
            r13 = r48
            r14 = r11
            r11 = r33
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x121d, all -> 0x1216 }
            android.view.Surface r1 = r30.getSurface()     // Catch:{ Exception -> 0x1200, all -> 0x1216 }
            r3 = r57
            r2 = r65
            r4 = 0
            r5 = 0
            r3.configure(r2, r1, r4, r5)     // Catch:{ Exception -> 0x11f5, all -> 0x1216 }
            r3.start()     // Catch:{ Exception -> 0x11f5, all -> 0x1216 }
            r1 = r66
            r2 = 21
            if (r1 >= r2) goto L_0x08ed
            java.nio.ByteBuffer[] r6 = r3.getInputBuffers()     // Catch:{ Exception -> 0x08df, all -> 0x08d3 }
            java.nio.ByteBuffer[] r1 = r84.getOutputBuffers()     // Catch:{ Exception -> 0x08df, all -> 0x08d3 }
            goto L_0x08ef
        L_0x08d3:
            r0 = move-exception
        L_0x08d4:
            r12 = r81
            r11 = r82
            r8 = r83
            r34 = r88
            r2 = r0
            goto L_0x127d
        L_0x08df:
            r0 = move-exception
            r8 = r83
            r6 = r84
            r34 = r88
            r2 = r0
            r28 = r4
        L_0x08e9:
            r5 = r29
            goto L_0x12b3
        L_0x08ed:
            r1 = r4
            r6 = r1
        L_0x08ef:
            r2 = r55
            if (r2 < 0) goto L_0x09f8
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x09e5, all -> 0x09d8 }
            android.media.MediaFormat r5 = r5.getTrackFormat(r2)     // Catch:{ Exception -> 0x09e5, all -> 0x09d8 }
            java.lang.String r7 = r5.getString(r14)     // Catch:{ Exception -> 0x09e5, all -> 0x09d8 }
            java.lang.String r8 = "audio/mp4a-latm"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x09e5, all -> 0x09d8 }
            if (r7 != 0) goto L_0x0914
            java.lang.String r7 = r5.getString(r14)     // Catch:{ Exception -> 0x08df, all -> 0x08d3 }
            java.lang.String r8 = "audio/mpeg"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x08df, all -> 0x08d3 }
            if (r7 == 0) goto L_0x0912
            goto L_0x0914
        L_0x0912:
            r7 = 0
            goto L_0x0915
        L_0x0914:
            r7 = 1
        L_0x0915:
            java.lang.String r8 = r5.getString(r14)     // Catch:{ Exception -> 0x09e5, all -> 0x09d8 }
            java.lang.String r9 = "audio/unknown"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x09e5, all -> 0x09d8 }
            if (r8 == 0) goto L_0x0922
            r2 = -1
        L_0x0922:
            if (r2 < 0) goto L_0x09cf
            if (r7 == 0) goto L_0x0980
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x0974, all -> 0x096f }
            r9 = 1
            int r8 = r8.addTrack(r5, r9)     // Catch:{ Exception -> 0x0974, all -> 0x096f }
            android.media.MediaExtractor r10 = r15.extractor     // Catch:{ Exception -> 0x0974, all -> 0x096f }
            r10.selectTrack(r2)     // Catch:{ Exception -> 0x0974, all -> 0x096f }
            java.lang.String r10 = "max-input-size"
            int r5 = r5.getInteger(r10)     // Catch:{ Exception -> 0x0939, all -> 0x08d3 }
            goto L_0x093f
        L_0x0939:
            r0 = move-exception
            r5 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ Exception -> 0x0974, all -> 0x096f }
            r5 = 0
        L_0x093f:
            if (r5 > 0) goto L_0x0943
            r5 = 65536(0x10000, float:9.18355E-41)
        L_0x0943:
            java.nio.ByteBuffer r10 = java.nio.ByteBuffer.allocateDirect(r5)     // Catch:{ Exception -> 0x0974, all -> 0x096f }
            r91 = r5
            r90 = r10
            r4 = 0
            r9 = r86
            int r11 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r11 <= 0) goto L_0x095a
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x0999, all -> 0x08d3 }
            r14 = 0
            r11.seekTo(r9, r14)     // Catch:{ Exception -> 0x0999, all -> 0x08d3 }
            goto L_0x0960
        L_0x095a:
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x0999, all -> 0x08d3 }
            r14 = 0
            r11.seekTo(r4, r14)     // Catch:{ Exception -> 0x0999, all -> 0x08d3 }
        L_0x0960:
            r4 = r88
            r11 = r91
            r14 = 0
            r74 = r2
            r2 = r90
            r90 = r1
            r1 = r74
            goto L_0x0a04
        L_0x096f:
            r0 = move-exception
            r9 = r86
            goto L_0x08d4
        L_0x0974:
            r0 = move-exception
            r9 = r86
        L_0x0977:
            r8 = r83
            r6 = r84
            r34 = r88
            r2 = r0
            goto L_0x09f1
        L_0x0980:
            r9 = r86
            r8 = r5
            r4 = 0
            android.media.MediaExtractor r11 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x09cd, all -> 0x09cb }
            r11.<init>()     // Catch:{ Exception -> 0x09cd, all -> 0x09cb }
            r11.setDataSource(r12)     // Catch:{ Exception -> 0x09cd, all -> 0x09cb }
            r11.selectTrack(r2)     // Catch:{ Exception -> 0x09cd, all -> 0x09cb }
            int r14 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r14 <= 0) goto L_0x099b
            r14 = 0
            r11.seekTo(r9, r14)     // Catch:{ Exception -> 0x0999, all -> 0x08d3 }
            goto L_0x099f
        L_0x0999:
            r0 = move-exception
            goto L_0x0977
        L_0x099b:
            r14 = 0
            r11.seekTo(r4, r14)     // Catch:{ Exception -> 0x09cd, all -> 0x09cb }
        L_0x099f:
            org.telegram.messenger.video.AudioRecoder r14 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x09cd, all -> 0x09cb }
            r14.<init>(r8, r11, r2)     // Catch:{ Exception -> 0x09cd, all -> 0x09cb }
            r14.startTime = r9     // Catch:{ Exception -> 0x09bd, all -> 0x09cb }
            r4 = r88
            r14.endTime = r4     // Catch:{ Exception -> 0x09bb, all -> 0x09b9 }
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x09bb, all -> 0x09b9 }
            android.media.MediaFormat r11 = r14.format     // Catch:{ Exception -> 0x09bb, all -> 0x09b9 }
            r90 = r1
            r1 = 1
            int r8 = r8.addTrack(r11, r1)     // Catch:{ Exception -> 0x09bb, all -> 0x09b9 }
            r1 = r2
            r2 = 0
            r11 = 0
            goto L_0x0a04
        L_0x09b9:
            r0 = move-exception
            goto L_0x09dd
        L_0x09bb:
            r0 = move-exception
            goto L_0x09c0
        L_0x09bd:
            r0 = move-exception
            r4 = r88
        L_0x09c0:
            r8 = r83
            r6 = r84
            r2 = r0
            r34 = r4
            r28 = r14
            goto L_0x08e9
        L_0x09cb:
            r0 = move-exception
            goto L_0x09db
        L_0x09cd:
            r0 = move-exception
            goto L_0x09e8
        L_0x09cf:
            r9 = r86
            r4 = r88
            r90 = r1
            r1 = r2
            r2 = 0
            goto L_0x0a01
        L_0x09d8:
            r0 = move-exception
            r9 = r86
        L_0x09db:
            r4 = r88
        L_0x09dd:
            r12 = r81
            r11 = r82
            r8 = r83
            goto L_0x127a
        L_0x09e5:
            r0 = move-exception
            r9 = r86
        L_0x09e8:
            r4 = r88
            r8 = r83
            r6 = r84
            r2 = r0
            r34 = r4
        L_0x09f1:
            r5 = r29
            r1 = -5
            r28 = 0
            goto L_0x12dc
        L_0x09f8:
            r9 = r86
            r4 = r88
            r90 = r1
            r1 = r2
            r2 = 0
            r7 = 1
        L_0x0a01:
            r8 = -5
            r11 = 0
            r14 = 0
        L_0x0a04:
            if (r1 >= 0) goto L_0x0a09
            r23 = 1
            goto L_0x0a0b
        L_0x0a09:
            r23 = 0
        L_0x0a0b:
            r76.checkConversionCanceled()     // Catch:{ Exception -> 0x11e3, all -> 0x11de }
            r68 = r18
            r59 = r20
            r32 = r23
            r88 = -5
            r21 = 0
            r23 = 1
            r28 = 0
            r31 = 0
            r41 = 0
            r48 = 0
            r50 = 0
            r20 = r2
            r2 = r11
            r11 = r90
        L_0x0a29:
            if (r28 == 0) goto L_0x0a44
            if (r7 != 0) goto L_0x0a30
            if (r32 != 0) goto L_0x0a30
            goto L_0x0a44
        L_0x0a30:
            r9 = r81
            r7 = r82
            r8 = r83
            r1 = r88
            r2 = r3
            r34 = r4
            r28 = r14
            r6 = 0
            r47 = 0
            r3 = r84
            goto L_0x1320
        L_0x0a44:
            r76.checkConversionCanceled()     // Catch:{ Exception -> 0x11c9, all -> 0x11b7 }
            if (r7 != 0) goto L_0x0a74
            if (r14 == 0) goto L_0x0a74
            r89 = r11
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            boolean r11 = r14.step(r11, r8)     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            goto L_0x0a78
        L_0x0a54:
            r0 = move-exception
            r12 = r81
            r11 = r82
            r8 = r83
            r1 = r88
        L_0x0a5d:
            r2 = r0
            r34 = r4
        L_0x0a60:
            r9 = r29
            goto L_0x058c
        L_0x0a64:
            r0 = move-exception
            r8 = r83
            r6 = r84
            r1 = r88
            r2 = r0
            r34 = r4
        L_0x0a6e:
            r28 = r14
        L_0x0a70:
            r5 = r29
            goto L_0x12dc
        L_0x0a74:
            r89 = r11
            r11 = r32
        L_0x0a78:
            if (r21 != 0) goto L_0x0bd9
            r90 = r11
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x0bc7, all -> 0x0bbc }
            int r11 = r11.getSampleTrackIndex()     // Catch:{ Exception -> 0x0bc7, all -> 0x0bbc }
            r12 = r61
            if (r11 != r12) goto L_0x0ae3
            r61 = r12
            r46 = r13
            r12 = 2500(0x9c4, double:1.235E-320)
            int r11 = r3.dequeueInputBuffer(r12)     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            if (r11 < 0) goto L_0x0ad2
            int r12 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            r13 = 21
            if (r12 >= r13) goto L_0x0a9b
            r12 = r6[r11]     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            goto L_0x0a9f
        L_0x0a9b:
            java.nio.ByteBuffer r12 = r3.getInputBuffer(r11)     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
        L_0x0a9f:
            android.media.MediaExtractor r13 = r15.extractor     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            r91 = r6
            r6 = 0
            int r35 = r13.readSampleData(r12, r6)     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            if (r35 >= 0) goto L_0x0abb
            r34 = 0
            r35 = 0
            r36 = 0
            r38 = 4
            r32 = r3
            r33 = r11
            r32.queueInputBuffer(r33, r34, r35, r36, r38)     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            r6 = 1
            goto L_0x0ad6
        L_0x0abb:
            r34 = 0
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            long r36 = r6.getSampleTime()     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            r38 = 0
            r32 = r3
            r33 = r11
            r32.queueInputBuffer(r33, r34, r35, r36, r38)     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            r6.advance()     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            goto L_0x0ad4
        L_0x0ad2:
            r91 = r6
        L_0x0ad4:
            r6 = r21
        L_0x0ad6:
            r70 = r4
            r21 = r6
            r53 = r7
            r13 = r22
            r6 = 0
            r22 = r1
            goto L_0x0b9f
        L_0x0ae3:
            r91 = r6
            r61 = r12
            r46 = r13
            if (r7 == 0) goto L_0x0b91
            r6 = -1
            if (r1 == r6) goto L_0x0b91
            if (r11 != r1) goto L_0x0b91
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0bc7, all -> 0x0bbc }
            r12 = 28
            if (r11 < r12) goto L_0x0b0c
            android.media.MediaExtractor r12 = r15.extractor     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            long r12 = r12.getSampleSize()     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            r53 = r7
            long r6 = (long) r2     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            int r32 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r32 <= 0) goto L_0x0b0e
            r6 = 1024(0x400, double:5.06E-321)
            long r12 = r12 + r6
            int r2 = (int) r12     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            java.nio.ByteBuffer r20 = java.nio.ByteBuffer.allocateDirect(r2)     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            goto L_0x0b0e
        L_0x0b0c:
            r53 = r7
        L_0x0b0e:
            r6 = r20
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x0bc7, all -> 0x0bbc }
            r12 = 0
            int r7 = r7.readSampleData(r6, r12)     // Catch:{ Exception -> 0x0bc7, all -> 0x0bbc }
            r13 = r22
            r13.size = r7     // Catch:{ Exception -> 0x0bc7, all -> 0x0bbc }
            r7 = 21
            if (r11 >= r7) goto L_0x0b27
            r6.position(r12)     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            int r7 = r13.size     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            r6.limit(r7)     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
        L_0x0b27:
            int r7 = r13.size     // Catch:{ Exception -> 0x0bc7, all -> 0x0bbc }
            if (r7 < 0) goto L_0x0b39
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            long r11 = r7.getSampleTime()     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            r13.presentationTimeUs = r11     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            r7.advance()     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            goto L_0x0b3e
        L_0x0b39:
            r7 = 0
            r13.size = r7     // Catch:{ Exception -> 0x0bc7, all -> 0x0bbc }
            r21 = 1
        L_0x0b3e:
            int r7 = r13.size     // Catch:{ Exception -> 0x0bc7, all -> 0x0bbc }
            if (r7 <= 0) goto L_0x0b86
            r11 = 0
            int r7 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1))
            if (r7 < 0) goto L_0x0b4e
            long r11 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0a64, all -> 0x0a54 }
            int r7 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r7 >= 0) goto L_0x0b86
        L_0x0b4e:
            r7 = 0
            r13.offset = r7     // Catch:{ Exception -> 0x0bc7, all -> 0x0bbc }
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x0bc7, all -> 0x0bbc }
            int r11 = r11.getSampleFlags()     // Catch:{ Exception -> 0x0bc7, all -> 0x0bbc }
            r13.flags = r11     // Catch:{ Exception -> 0x0bc7, all -> 0x0bbc }
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x0bc7, all -> 0x0bbc }
            long r11 = r11.writeSampleData(r8, r6, r13, r7)     // Catch:{ Exception -> 0x0bc7, all -> 0x0bbc }
            r32 = 0
            int r7 = (r11 > r32 ? 1 : (r11 == r32 ? 0 : -1))
            if (r7 == 0) goto L_0x0b86
            org.telegram.messenger.MediaController$VideoConvertorListener r7 = r15.callback     // Catch:{ Exception -> 0x0bc7, all -> 0x0bbc }
            if (r7 == 0) goto L_0x0b86
            r22 = r1
            r20 = r2
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0bc7, all -> 0x0bbc }
            long r32 = r1 - r9
            int r34 = (r32 > r48 ? 1 : (r32 == r48 ? 0 : -1))
            if (r34 <= 0) goto L_0x0b77
            long r48 = r1 - r9
        L_0x0b77:
            r1 = r48
            r70 = r4
            float r4 = (float) r1
            float r4 = r4 / r24
            float r4 = r4 / r25
            r7.didWriteData(r11, r4)     // Catch:{ Exception -> 0x0bba, all -> 0x0bb8 }
            r48 = r1
            goto L_0x0b8c
        L_0x0b86:
            r22 = r1
            r20 = r2
            r70 = r4
        L_0x0b8c:
            r2 = r20
            r20 = r6
            goto L_0x0b9e
        L_0x0b91:
            r70 = r4
            r53 = r7
            r13 = r22
            r22 = r1
            r1 = -1
            if (r11 != r1) goto L_0x0b9e
            r6 = 1
            goto L_0x0b9f
        L_0x0b9e:
            r6 = 0
        L_0x0b9f:
            if (r6 == 0) goto L_0x0be7
            r4 = 2500(0x9c4, double:1.235E-320)
            int r33 = r3.dequeueInputBuffer(r4)     // Catch:{ Exception -> 0x0bba, all -> 0x0bb8 }
            if (r33 < 0) goto L_0x0be7
            r34 = 0
            r35 = 0
            r36 = 0
            r38 = 4
            r32 = r3
            r32.queueInputBuffer(r33, r34, r35, r36, r38)     // Catch:{ Exception -> 0x0bba, all -> 0x0bb8 }
            r6 = 1
            goto L_0x0be9
        L_0x0bb8:
            r0 = move-exception
            goto L_0x0bbf
        L_0x0bba:
            r0 = move-exception
            goto L_0x0bca
        L_0x0bbc:
            r0 = move-exception
            r70 = r4
        L_0x0bbf:
            r12 = r81
            r11 = r82
            r8 = r83
            goto L_0x11c0
        L_0x0bc7:
            r0 = move-exception
            r70 = r4
        L_0x0bca:
            r8 = r83
            r6 = r84
            r1 = r88
            r2 = r0
            r28 = r14
            r5 = r29
            r34 = r70
            goto L_0x12dc
        L_0x0bd9:
            r70 = r4
            r91 = r6
            r53 = r7
            r90 = r11
            r46 = r13
            r13 = r22
            r22 = r1
        L_0x0be7:
            r6 = r21
        L_0x0be9:
            r1 = r31 ^ 1
            r11 = r89
            r7 = r1
            r21 = r6
            r72 = r59
            r4 = r70
            r6 = 1
            r1 = r88
        L_0x0bf7:
            if (r7 != 0) goto L_0x0CLASSNAME
            if (r6 == 0) goto L_0x0bfc
            goto L_0x0CLASSNAME
        L_0x0bfc:
            r12 = r77
            r32 = r90
            r6 = r91
            r88 = r1
            r1 = r22
            r7 = r53
            r59 = r72
            r22 = r13
            r13 = r46
            goto L_0x0a29
        L_0x0CLASSNAME:
            r76.checkConversionCanceled()     // Catch:{ Exception -> 0x11a6, all -> 0x119a }
            if (r95 == 0) goto L_0x0CLASSNAME
            r32 = 22000(0x55f0, double:1.08694E-319)
            r12 = r84
            r88 = r6
            r89 = r7
            r6 = r32
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r12 = r84
            r88 = r6
            r89 = r7
            r6 = 2500(0x9c4, double:1.235E-320)
        L_0x0CLASSNAME:
            int r6 = r12.dequeueOutputBuffer(r13, r6)     // Catch:{ Exception -> 0x1196, all -> 0x119a }
            r7 = -1
            if (r6 != r7) goto L_0x0c4e
            r88 = r1
            r84 = r2
            r57 = r3
            r34 = r4
            r5 = r6
            r32 = r8
            r7 = r46
            r2 = r58
            r3 = r62
            r4 = r63
            r8 = r67
            r1 = -1
            r6 = 0
        L_0x0CLASSNAME:
            r74 = r28
            r28 = r14
            r14 = r74
            goto L_0x0e91
        L_0x0c4e:
            r7 = -3
            if (r6 != r7) goto L_0x0CLASSNAME
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c7d, all -> 0x0CLASSNAME }
            r84 = r2
            r2 = 21
            if (r7 >= r2) goto L_0x0c5d
            java.nio.ByteBuffer[] r11 = r12.getOutputBuffers()     // Catch:{ Exception -> 0x0c7d, all -> 0x0CLASSNAME }
        L_0x0c5d:
            r57 = r3
            r34 = r4
            r5 = r6
            r32 = r8
            r7 = r46
            r2 = r58
            r3 = r62
            r4 = r63
            r8 = r67
        L_0x0c6e:
            r6 = r88
            r88 = r1
            r1 = -1
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = move-exception
        L_0x0CLASSNAME:
            r12 = r81
            r11 = r82
            r8 = r83
            goto L_0x0a5d
        L_0x0c7d:
            r0 = move-exception
        L_0x0c7e:
            r8 = r83
            r2 = r0
            r34 = r4
            r6 = r12
            goto L_0x0a6e
        L_0x0CLASSNAME:
            r84 = r2
            r2 = -2
            if (r6 != r2) goto L_0x0d0e
            android.media.MediaFormat r2 = r12.getOutputFormat()     // Catch:{ Exception -> 0x0c7d, all -> 0x0CLASSNAME }
            r7 = -5
            if (r1 != r7) goto L_0x0cfb
            if (r2 == 0) goto L_0x0cfb
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ Exception -> 0x0c7d, all -> 0x0CLASSNAME }
            r32 = r8
            r8 = 0
            int r1 = r7.addTrack(r2, r8)     // Catch:{ Exception -> 0x0c7d, all -> 0x0CLASSNAME }
            r7 = r64
            boolean r8 = r2.containsKey(r7)     // Catch:{ Exception -> 0x0cf7, all -> 0x0cf2 }
            if (r8 == 0) goto L_0x0ce7
            int r8 = r2.getInteger(r7)     // Catch:{ Exception -> 0x0cf7, all -> 0x0cf2 }
            r33 = r1
            r1 = 1
            if (r8 != r1) goto L_0x0ce9
            r8 = r67
            java.nio.ByteBuffer r1 = r2.getByteBuffer(r8)     // Catch:{ Exception -> 0x0cd8, all -> 0x0cc8 }
            r64 = r7
            r7 = r46
            java.nio.ByteBuffer r2 = r2.getByteBuffer(r7)     // Catch:{ Exception -> 0x0cd8, all -> 0x0cc8 }
            int r1 = r1.limit()     // Catch:{ Exception -> 0x0cd8, all -> 0x0cc8 }
            int r2 = r2.limit()     // Catch:{ Exception -> 0x0cd8, all -> 0x0cc8 }
            int r1 = r1 + r2
            r41 = r1
            goto L_0x0cef
        L_0x0cc8:
            r0 = move-exception
            r12 = r81
            r11 = r82
            r8 = r83
            r2 = r0
            r34 = r4
            r9 = r29
            r1 = r33
            goto L_0x058c
        L_0x0cd8:
            r0 = move-exception
            r8 = r83
            r2 = r0
            r34 = r4
            r6 = r12
            r28 = r14
            r5 = r29
            r1 = r33
            goto L_0x12dc
        L_0x0ce7:
            r33 = r1
        L_0x0ce9:
            r64 = r7
            r7 = r46
            r8 = r67
        L_0x0cef:
            r1 = r33
            goto L_0x0d01
        L_0x0cf2:
            r0 = move-exception
            r33 = r1
            goto L_0x0CLASSNAME
        L_0x0cf7:
            r0 = move-exception
            r33 = r1
            goto L_0x0c7e
        L_0x0cfb:
            r32 = r8
            r7 = r46
            r8 = r67
        L_0x0d01:
            r57 = r3
            r34 = r4
            r5 = r6
            r2 = r58
            r3 = r62
            r4 = r63
            goto L_0x0c6e
        L_0x0d0e:
            r32 = r8
            r7 = r46
            r8 = r67
            if (r6 < 0) goto L_0x116d
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1196, all -> 0x119a }
            r28 = r14
            r14 = 21
            if (r2 >= r14) goto L_0x0d29
            r2 = r11[r6]     // Catch:{ Exception -> 0x0d21, all -> 0x0CLASSNAME }
            goto L_0x0d2d
        L_0x0d21:
            r0 = move-exception
            r8 = r83
            r2 = r0
            r34 = r4
            goto L_0x10ef
        L_0x0d29:
            java.nio.ByteBuffer r2 = r12.getOutputBuffer(r6)     // Catch:{ Exception -> 0x1166, all -> 0x119a }
        L_0x0d2d:
            if (r2 == 0) goto L_0x1142
            int r14 = r13.size     // Catch:{ Exception -> 0x1138, all -> 0x119a }
            r33 = r11
            r11 = 1
            if (r14 <= r11) goto L_0x0e73
            int r11 = r13.flags     // Catch:{ Exception -> 0x0e6a, all -> 0x0e5f }
            r34 = r11 & 2
            if (r34 != 0) goto L_0x0dda
            if (r41 == 0) goto L_0x0d52
            r34 = r11 & 1
            if (r34 == 0) goto L_0x0d52
            r34 = r4
            int r4 = r13.offset     // Catch:{ Exception -> 0x0d4f, all -> 0x0e52 }
            int r4 = r4 + r41
            r13.offset = r4     // Catch:{ Exception -> 0x0d4f, all -> 0x0e52 }
            int r14 = r14 - r41
            r13.size = r14     // Catch:{ Exception -> 0x0d4f, all -> 0x0e52 }
            goto L_0x0d54
        L_0x0d4f:
            r0 = move-exception
            goto L_0x0e6f
        L_0x0d52:
            r34 = r4
        L_0x0d54:
            if (r23 == 0) goto L_0x0da3
            r4 = r11 & 1
            if (r4 == 0) goto L_0x0da3
            int r4 = r13.size     // Catch:{ Exception -> 0x0d4f, all -> 0x0e52 }
            r5 = 100
            if (r4 <= r5) goto L_0x0da1
            int r4 = r13.offset     // Catch:{ Exception -> 0x0d4f, all -> 0x0e52 }
            r2.position(r4)     // Catch:{ Exception -> 0x0d4f, all -> 0x0e52 }
            byte[] r4 = new byte[r5]     // Catch:{ Exception -> 0x0d4f, all -> 0x0e52 }
            r2.get(r4)     // Catch:{ Exception -> 0x0d4f, all -> 0x0e52 }
            r11 = 0
            r14 = 0
        L_0x0d6c:
            r5 = 96
            if (r11 >= r5) goto L_0x0da1
            byte r5 = r4[r11]     // Catch:{ Exception -> 0x0d4f, all -> 0x0e52 }
            if (r5 != 0) goto L_0x0d98
            int r5 = r11 + 1
            byte r5 = r4[r5]     // Catch:{ Exception -> 0x0d4f, all -> 0x0e52 }
            if (r5 != 0) goto L_0x0d98
            int r5 = r11 + 2
            byte r5 = r4[r5]     // Catch:{ Exception -> 0x0d4f, all -> 0x0e52 }
            if (r5 != 0) goto L_0x0d98
            int r5 = r11 + 3
            byte r5 = r4[r5]     // Catch:{ Exception -> 0x0d4f, all -> 0x0e52 }
            r23 = r4
            r4 = 1
            if (r5 != r4) goto L_0x0d9a
            int r14 = r14 + 1
            if (r14 <= r4) goto L_0x0d9a
            int r4 = r13.offset     // Catch:{ Exception -> 0x0d4f, all -> 0x0e52 }
            int r4 = r4 + r11
            r13.offset = r4     // Catch:{ Exception -> 0x0d4f, all -> 0x0e52 }
            int r4 = r13.size     // Catch:{ Exception -> 0x0d4f, all -> 0x0e52 }
            int r4 = r4 - r11
            r13.size = r4     // Catch:{ Exception -> 0x0d4f, all -> 0x0e52 }
            goto L_0x0da1
        L_0x0d98:
            r23 = r4
        L_0x0d9a:
            int r11 = r11 + 1
            r4 = r23
            r5 = 100
            goto L_0x0d6c
        L_0x0da1:
            r23 = 0
        L_0x0da3:
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x0dd5, all -> 0x0e52 }
            r57 = r3
            r5 = 1
            long r2 = r4.writeSampleData(r1, r2, r13, r5)     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            r4 = 0
            int r11 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r11 == 0) goto L_0x0e77
            org.telegram.messenger.MediaController$VideoConvertorListener r4 = r15.callback     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            if (r4 == 0) goto L_0x0e77
            r11 = r6
            long r5 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            long r36 = r5 - r9
            int r14 = (r36 > r48 ? 1 : (r36 == r48 ? 0 : -1))
            if (r14 <= 0) goto L_0x0dc1
            long r48 = r5 - r9
        L_0x0dc1:
            r5 = r48
            float r14 = (float) r5     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            float r14 = r14 / r24
            float r14 = r14 / r25
            r4.didWriteData(r2, r14)     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            r48 = r5
        L_0x0dcd:
            r2 = r58
            r3 = r62
            r4 = r63
            goto L_0x0e7a
        L_0x0dd5:
            r0 = move-exception
            r57 = r3
            goto L_0x0e6f
        L_0x0dda:
            r57 = r3
            r34 = r4
            r11 = r6
            r3 = -5
            if (r1 != r3) goto L_0x0dcd
            byte[] r3 = new byte[r14]     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            int r4 = r13.offset     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            int r4 = r4 + r14
            r2.limit(r4)     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            int r4 = r13.offset     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            r2.position(r4)     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            r2.get(r3)     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            int r2 = r13.size     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            r6 = 1
            int r2 = r2 - r6
        L_0x0df6:
            if (r2 < 0) goto L_0x0e34
            r4 = 3
            if (r2 <= r4) goto L_0x0e34
            byte r5 = r3[r2]     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            if (r5 != r6) goto L_0x0e30
            int r5 = r2 + -1
            byte r5 = r3[r5]     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            if (r5 != 0) goto L_0x0e30
            int r5 = r2 + -2
            byte r5 = r3[r5]     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            if (r5 != 0) goto L_0x0e30
            int r5 = r2 + -3
            byte r14 = r3[r5]     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            if (r14 != 0) goto L_0x0e30
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r5)     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            int r14 = r13.size     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            int r14 = r14 - r5
            java.nio.ByteBuffer r14 = java.nio.ByteBuffer.allocate(r14)     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            r4 = 0
            java.nio.ByteBuffer r6 = r2.put(r3, r4, r5)     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            r6.position(r4)     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            int r6 = r13.size     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            int r6 = r6 - r5
            java.nio.ByteBuffer r3 = r14.put(r3, r5, r6)     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            r3.position(r4)     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            r6 = r2
            goto L_0x0e36
        L_0x0e30:
            int r2 = r2 + -1
            r6 = 1
            goto L_0x0df6
        L_0x0e34:
            r6 = 0
            r14 = 0
        L_0x0e36:
            r2 = r58
            r3 = r62
            r4 = r63
            android.media.MediaFormat r5 = android.media.MediaFormat.createVideoFormat(r4, r2, r3)     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            if (r6 == 0) goto L_0x0e4a
            if (r14 == 0) goto L_0x0e4a
            r5.setByteBuffer(r8, r6)     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            r5.setByteBuffer(r7, r14)     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
        L_0x0e4a:
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            r14 = 0
            int r1 = r6.addTrack(r5, r14)     // Catch:{ Exception -> 0x0e54, all -> 0x0e52 }
            goto L_0x0e7a
        L_0x0e52:
            r0 = move-exception
            goto L_0x0e62
        L_0x0e54:
            r0 = move-exception
            r8 = r83
            r2 = r0
            r6 = r12
            r5 = r29
            r3 = r57
            goto L_0x12dc
        L_0x0e5f:
            r0 = move-exception
            r34 = r4
        L_0x0e62:
            r12 = r81
            r11 = r82
            r8 = r83
            goto L_0x11a3
        L_0x0e6a:
            r0 = move-exception
            r57 = r3
            r34 = r4
        L_0x0e6f:
            r8 = r83
            goto L_0x1140
        L_0x0e73:
            r57 = r3
            r34 = r4
        L_0x0e77:
            r11 = r6
            goto L_0x0dcd
        L_0x0e7a:
            int r5 = r13.flags     // Catch:{ Exception -> 0x112f, all -> 0x1129 }
            r5 = r5 & 4
            if (r5 == 0) goto L_0x0e83
            r5 = r11
            r6 = 1
            goto L_0x0e85
        L_0x0e83:
            r5 = r11
            r6 = 0
        L_0x0e85:
            r11 = 0
            r12.releaseOutputBuffer(r5, r11)     // Catch:{ Exception -> 0x112f, all -> 0x1129 }
            r14 = r6
            r11 = r33
            r6 = r88
            r88 = r1
            r1 = -1
        L_0x0e91:
            if (r5 == r1) goto L_0x0eb3
            r1 = r88
            r58 = r2
            r62 = r3
            r63 = r4
            r46 = r7
            r67 = r8
            r8 = r32
            r4 = r34
            r3 = r57
            r2 = r84
            r7 = r89
            r84 = r12
        L_0x0eab:
            r74 = r28
            r28 = r14
            r14 = r74
            goto L_0x0bf7
        L_0x0eb3:
            if (r31 != 0) goto L_0x10f2
            r58 = r2
            r62 = r3
            r5 = r57
            r1 = 2500(0x9c4, double:1.235E-320)
            int r3 = r5.dequeueOutputBuffer(r13, r1)     // Catch:{ Exception -> 0x10e6, all -> 0x10db }
            r1 = -1
            if (r3 != r1) goto L_0x0ed5
            r63 = r4
            r52 = r6
            r37 = r7
            r67 = r8
            r9 = r56
            r1 = 0
            r7 = 0
            r8 = r83
            goto L_0x110c
        L_0x0ed5:
            r2 = -3
            if (r3 != r2) goto L_0x0ee4
        L_0x0ed8:
            r63 = r4
            r52 = r6
            r37 = r7
            r67 = r8
            r9 = r56
            goto L_0x1102
        L_0x0ee4:
            r2 = -2
            if (r3 != r2) goto L_0x0var_
            android.media.MediaFormat r2 = r5.getOutputFormat()     // Catch:{ Exception -> 0x0f0d, all -> 0x0var_ }
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0f0d, all -> 0x0var_ }
            if (r3 == 0) goto L_0x0ed8
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0f0d, all -> 0x0var_ }
            r3.<init>()     // Catch:{ Exception -> 0x0f0d, all -> 0x0var_ }
            java.lang.String r1 = "newFormat = "
            r3.append(r1)     // Catch:{ Exception -> 0x0f0d, all -> 0x0var_ }
            r3.append(r2)     // Catch:{ Exception -> 0x0f0d, all -> 0x0var_ }
            java.lang.String r1 = r3.toString()     // Catch:{ Exception -> 0x0f0d, all -> 0x0var_ }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0f0d, all -> 0x0var_ }
            goto L_0x0ed8
        L_0x0var_:
            r0 = move-exception
            r12 = r81
            r11 = r82
            r8 = r83
            goto L_0x10e2
        L_0x0f0d:
            r0 = move-exception
            r8 = r83
            goto L_0x10eb
        L_0x0var_:
            if (r3 < 0) goto L_0x10b5
            int r1 = r13.size     // Catch:{ Exception -> 0x10e6, all -> 0x10db }
            if (r1 == 0) goto L_0x0f1b
            r36 = 1
            goto L_0x0f1d
        L_0x0f1b:
            r36 = 0
        L_0x0f1d:
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x10e6, all -> 0x10db }
            r43 = 0
            int r37 = (r34 > r43 ? 1 : (r34 == r43 ? 0 : -1))
            if (r37 <= 0) goto L_0x0var_
            int r37 = (r1 > r34 ? 1 : (r1 == r34 ? 0 : -1))
            if (r37 < 0) goto L_0x0var_
            r63 = r4
            int r4 = r13.flags     // Catch:{ Exception -> 0x0f0d, all -> 0x0var_ }
            r4 = r4 | 4
            r13.flags = r4     // Catch:{ Exception -> 0x0f0d, all -> 0x0var_ }
            r21 = 1
            r31 = 1
            r36 = 0
            goto L_0x0f3a
        L_0x0var_:
            r63 = r4
        L_0x0f3a:
            r43 = 0
            int r4 = (r26 > r43 ? 1 : (r26 == r43 ? 0 : -1))
            if (r4 < 0) goto L_0x0fbd
            int r4 = r13.flags     // Catch:{ Exception -> 0x0f0d, all -> 0x0fb7 }
            r4 = r4 & 4
            if (r4 == 0) goto L_0x0fbd
            long r54 = r26 - r9
            long r54 = java.lang.Math.abs(r54)     // Catch:{ Exception -> 0x0f0d, all -> 0x0fb7 }
            r4 = 1000000(0xvar_, float:1.401298E-39)
            r67 = r8
            r8 = r83
            int r4 = r4 / r8
            r52 = r6
            r37 = r7
            long r6 = (long) r4
            int r4 = (r54 > r6 ? 1 : (r54 == r6 ? 0 : -1))
            if (r4 <= 0) goto L_0x0fad
            r6 = 0
            int r4 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r4 <= 0) goto L_0x0f6d
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x0fb4, all -> 0x10d1 }
            r6 = 0
            r4.seekTo(r9, r6)     // Catch:{ Exception -> 0x0fb4, all -> 0x10d1 }
            r6 = r72
            r9 = 0
            goto L_0x0var_
        L_0x0f6d:
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x0fb4, all -> 0x0fb2 }
            r6 = 0
            r9 = 0
            r4.seekTo(r6, r9)     // Catch:{ Exception -> 0x0fb4, all -> 0x10d1 }
            r6 = r72
        L_0x0var_:
            long r50 = r6 + r39
            int r4 = r13.flags     // Catch:{ Exception -> 0x0f9f, all -> 0x0f8e }
            r10 = -5
            r4 = r4 & r10
            r13.flags = r4     // Catch:{ Exception -> 0x0f9f, all -> 0x0f8e }
            r5.flush()     // Catch:{ Exception -> 0x0f9f, all -> 0x0f8e }
            r34 = r18
            r4 = 1
            r21 = 0
            r31 = 0
            r36 = 0
            r43 = 0
            goto L_0x0fd2
        L_0x0f8e:
            r0 = move-exception
            r12 = r81
            r11 = r82
            r1 = r88
            r2 = r0
            r34 = r26
            r9 = r29
            r7 = 0
            r26 = r18
            goto L_0x13dd
        L_0x0f9f:
            r0 = move-exception
            r1 = r88
            r2 = r0
            r3 = r5
            r6 = r12
            r34 = r26
            r5 = r29
            r26 = r18
            goto L_0x12dc
        L_0x0fad:
            r6 = r72
            r9 = 0
            r10 = -5
            goto L_0x0fc9
        L_0x0fb2:
            r0 = move-exception
            goto L_0x0fba
        L_0x0fb4:
            r0 = move-exception
            goto L_0x10eb
        L_0x0fb7:
            r0 = move-exception
            r8 = r83
        L_0x0fba:
            r9 = 0
            goto L_0x10de
        L_0x0fbd:
            r52 = r6
            r37 = r7
            r67 = r8
            r6 = r72
            r9 = 0
            r10 = -5
            r8 = r83
        L_0x0fc9:
            r4 = 0
            r43 = 0
            r74 = r26
            r26 = r34
            r34 = r74
        L_0x0fd2:
            int r38 = (r34 > r43 ? 1 : (r34 == r43 ? 0 : -1))
            if (r38 < 0) goto L_0x0fd9
            r9 = r34
            goto L_0x0fdb
        L_0x0fd9:
            r9 = r86
        L_0x0fdb:
            int r54 = (r9 > r43 ? 1 : (r9 == r43 ? 0 : -1))
            if (r54 <= 0) goto L_0x101c
            int r54 = (r68 > r18 ? 1 : (r68 == r18 ? 0 : -1))
            if (r54 != 0) goto L_0x101c
            int r54 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r54 >= 0) goto L_0x100b
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1019, all -> 0x1090 }
            if (r1 == 0) goto L_0x1009
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1019, all -> 0x1090 }
            r1.<init>()     // Catch:{ Exception -> 0x1019, all -> 0x1090 }
            java.lang.String r2 = "drop frame startTime = "
            r1.append(r2)     // Catch:{ Exception -> 0x1019, all -> 0x1090 }
            r1.append(r9)     // Catch:{ Exception -> 0x1019, all -> 0x1090 }
            java.lang.String r2 = " present time = "
            r1.append(r2)     // Catch:{ Exception -> 0x1019, all -> 0x1090 }
            long r9 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1019, all -> 0x1090 }
            r1.append(r9)     // Catch:{ Exception -> 0x1019, all -> 0x1090 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x1019, all -> 0x1090 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x1019, all -> 0x1090 }
        L_0x1009:
            r1 = 0
            goto L_0x101e
        L_0x100b:
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1019, all -> 0x1090 }
            r9 = -2147483648(0xfffffffvar_, double:NaN)
            int r54 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r54 == 0) goto L_0x1016
            long r50 = r50 - r1
        L_0x1016:
            r68 = r1
            goto L_0x101c
        L_0x1019:
            r0 = move-exception
            goto L_0x10a6
        L_0x101c:
            r1 = r36
        L_0x101e:
            if (r4 == 0) goto L_0x1023
            r68 = r18
            goto L_0x1036
        L_0x1023:
            int r2 = (r34 > r18 ? 1 : (r34 == r18 ? 0 : -1))
            if (r2 != 0) goto L_0x1033
            r9 = 0
            int r2 = (r50 > r9 ? 1 : (r50 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x1033
            long r9 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1019, all -> 0x1090 }
            long r9 = r9 + r50
            r13.presentationTimeUs = r9     // Catch:{ Exception -> 0x1019, all -> 0x1090 }
        L_0x1033:
            r5.releaseOutputBuffer(r3, r1)     // Catch:{ Exception -> 0x10a3, all -> 0x1090 }
        L_0x1036:
            if (r1 == 0) goto L_0x1069
            r1 = 0
            int r3 = (r34 > r1 ? 1 : (r34 == r1 ? 0 : -1))
            if (r3 < 0) goto L_0x1045
            long r3 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1019, all -> 0x1090 }
            long r72 = java.lang.Math.max(r6, r3)     // Catch:{ Exception -> 0x1019, all -> 0x1090 }
            goto L_0x1047
        L_0x1045:
            r72 = r6
        L_0x1047:
            r30.awaitNewImage()     // Catch:{ Exception -> 0x104c, all -> 0x1090 }
            r6 = 0
            goto L_0x1052
        L_0x104c:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)     // Catch:{ Exception -> 0x10a3, all -> 0x1090 }
            r6 = 1
        L_0x1052:
            if (r6 != 0) goto L_0x1066
            r30.drawImage()     // Catch:{ Exception -> 0x10a3, all -> 0x1090 }
            long r3 = r13.presentationTimeUs     // Catch:{ Exception -> 0x10a3, all -> 0x1090 }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r3 = r3 * r6
            r9 = r56
            r9.setPresentationTime(r3)     // Catch:{ Exception -> 0x1088, all -> 0x1090 }
            r9.swapBuffers()     // Catch:{ Exception -> 0x1088, all -> 0x1090 }
            goto L_0x106f
        L_0x1066:
            r9 = r56
            goto L_0x106f
        L_0x1069:
            r9 = r56
            r1 = 0
            r72 = r6
        L_0x106f:
            int r3 = r13.flags     // Catch:{ Exception -> 0x1088, all -> 0x1090 }
            r3 = r3 & 4
            if (r3 == 0) goto L_0x1084
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1088, all -> 0x1090 }
            if (r3 == 0) goto L_0x107e
            java.lang.String r3 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x1088, all -> 0x1090 }
        L_0x107e:
            r12.signalEndOfInputStream()     // Catch:{ Exception -> 0x1088, all -> 0x1090 }
            r7 = 0
            goto L_0x1112
        L_0x1084:
            r7 = r89
            goto L_0x1112
        L_0x1088:
            r0 = move-exception
            r1 = r88
            r2 = r0
            r3 = r5
            r56 = r9
            goto L_0x10aa
        L_0x1090:
            r0 = move-exception
            r12 = r81
            r11 = r82
            r1 = r88
            r2 = r0
            r9 = r29
            r7 = 0
            r74 = r26
            r26 = r34
            r34 = r74
            goto L_0x13dd
        L_0x10a3:
            r0 = move-exception
            r9 = r56
        L_0x10a6:
            r1 = r88
            r2 = r0
            r3 = r5
        L_0x10aa:
            r6 = r12
            r5 = r29
            r74 = r26
            r26 = r34
            r34 = r74
            goto L_0x12dc
        L_0x10b5:
            r8 = r83
            r9 = r56
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x10d3, all -> 0x10d1 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x10d3, all -> 0x10d1 }
            r2.<init>()     // Catch:{ Exception -> 0x10d3, all -> 0x10d1 }
            java.lang.String r4 = "unexpected result from decoder.dequeueOutputBuffer: "
            r2.append(r4)     // Catch:{ Exception -> 0x10d3, all -> 0x10d1 }
            r2.append(r3)     // Catch:{ Exception -> 0x10d3, all -> 0x10d1 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x10d3, all -> 0x10d1 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x10d3, all -> 0x10d1 }
            throw r1     // Catch:{ Exception -> 0x10d3, all -> 0x10d1 }
        L_0x10d1:
            r0 = move-exception
            goto L_0x10de
        L_0x10d3:
            r0 = move-exception
            r1 = r88
            r2 = r0
            r3 = r5
            r56 = r9
            goto L_0x10ef
        L_0x10db:
            r0 = move-exception
            r8 = r83
        L_0x10de:
            r12 = r81
            r11 = r82
        L_0x10e2:
            r1 = r88
            goto L_0x11a3
        L_0x10e6:
            r0 = move-exception
            r8 = r83
            r9 = r56
        L_0x10eb:
            r1 = r88
        L_0x10ed:
            r2 = r0
            r3 = r5
        L_0x10ef:
            r6 = r12
            goto L_0x0a70
        L_0x10f2:
            r58 = r2
            r62 = r3
            r63 = r4
            r52 = r6
            r37 = r7
            r67 = r8
            r9 = r56
            r5 = r57
        L_0x1102:
            r6 = r72
            r1 = 0
            r8 = r83
            r72 = r6
            r7 = r89
        L_0x110c:
            r74 = r26
            r26 = r34
            r34 = r74
        L_0x1112:
            r2 = r84
            r1 = r88
            r3 = r5
            r56 = r9
            r84 = r12
            r4 = r26
            r8 = r32
            r26 = r34
            r46 = r37
            r6 = r52
            r9 = r86
            goto L_0x0eab
        L_0x1129:
            r0 = move-exception
            r8 = r83
            r3 = r1
            goto L_0x119f
        L_0x112f:
            r0 = move-exception
            r8 = r83
            r3 = r1
            r9 = r56
            r5 = r57
            goto L_0x10ed
        L_0x1138:
            r0 = move-exception
            r8 = r83
            r34 = r4
            r9 = r56
            r5 = r3
        L_0x1140:
            r2 = r0
            goto L_0x10ef
        L_0x1142:
            r8 = r83
            r2 = r3
            r34 = r4
            r5 = r6
            r9 = r56
            java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1191, all -> 0x118f }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1191, all -> 0x118f }
            r4.<init>()     // Catch:{ Exception -> 0x1191, all -> 0x118f }
            java.lang.String r6 = "encoderOutputBuffer "
            r4.append(r6)     // Catch:{ Exception -> 0x1191, all -> 0x118f }
            r4.append(r5)     // Catch:{ Exception -> 0x1191, all -> 0x118f }
            java.lang.String r5 = " was null"
            r4.append(r5)     // Catch:{ Exception -> 0x1191, all -> 0x118f }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x1191, all -> 0x118f }
            r3.<init>(r4)     // Catch:{ Exception -> 0x1191, all -> 0x118f }
            throw r3     // Catch:{ Exception -> 0x1191, all -> 0x118f }
        L_0x1166:
            r0 = move-exception
            r8 = r83
            r2 = r3
            r34 = r4
            goto L_0x11b0
        L_0x116d:
            r8 = r83
            r2 = r3
            r34 = r4
            r5 = r6
            r28 = r14
            r9 = r56
            java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1191, all -> 0x118f }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1191, all -> 0x118f }
            r4.<init>()     // Catch:{ Exception -> 0x1191, all -> 0x118f }
            java.lang.String r6 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r6)     // Catch:{ Exception -> 0x1191, all -> 0x118f }
            r4.append(r5)     // Catch:{ Exception -> 0x1191, all -> 0x118f }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x1191, all -> 0x118f }
            r3.<init>(r4)     // Catch:{ Exception -> 0x1191, all -> 0x118f }
            throw r3     // Catch:{ Exception -> 0x1191, all -> 0x118f }
        L_0x118f:
            r0 = move-exception
            goto L_0x119f
        L_0x1191:
            r0 = move-exception
            r3 = r2
            r56 = r9
            goto L_0x11b2
        L_0x1196:
            r0 = move-exception
            r8 = r83
            goto L_0x11ab
        L_0x119a:
            r0 = move-exception
            r8 = r83
            r34 = r4
        L_0x119f:
            r12 = r81
            r11 = r82
        L_0x11a3:
            r2 = r0
            goto L_0x0a60
        L_0x11a6:
            r0 = move-exception
            r8 = r83
            r12 = r84
        L_0x11ab:
            r2 = r3
            r34 = r4
            r28 = r14
        L_0x11b0:
            r9 = r56
        L_0x11b2:
            r6 = r12
            r5 = r29
            goto L_0x0782
        L_0x11b7:
            r0 = move-exception
            r8 = r83
            r70 = r4
            r12 = r81
            r11 = r82
        L_0x11c0:
            r1 = r88
            r2 = r0
            r9 = r29
            r34 = r70
            goto L_0x058c
        L_0x11c9:
            r0 = move-exception
            r8 = r83
            r12 = r84
            r2 = r3
            r70 = r4
            r28 = r14
            r9 = r56
            r1 = r88
            r6 = r12
            r5 = r29
            r34 = r70
            goto L_0x0782
        L_0x11de:
            r0 = move-exception
            r8 = r83
            goto L_0x1276
        L_0x11e3:
            r0 = move-exception
            r8 = r83
            r12 = r84
            r2 = r3
            r28 = r14
            r9 = r56
            r34 = r4
            r6 = r12
            r5 = r29
            r1 = -5
            goto L_0x0782
        L_0x11f5:
            r0 = move-exception
            r8 = r83
            r12 = r84
            r4 = r88
            r2 = r3
            r9 = r56
            goto L_0x120c
        L_0x1200:
            r0 = move-exception
            r8 = r83
            r12 = r84
            r4 = r88
            r9 = r56
            r2 = r57
            r3 = r2
        L_0x120c:
            r34 = r4
            r6 = r12
            r5 = r29
            r1 = -5
            r28 = 0
            goto L_0x0782
        L_0x1216:
            r0 = move-exception
            r8 = r83
            r4 = r88
            goto L_0x1276
        L_0x121d:
            r0 = move-exception
            r8 = r83
            r12 = r84
            r4 = r88
            r9 = r56
            r2 = r57
            r3 = r2
            r34 = r4
            r6 = r12
            goto L_0x123c
        L_0x122d:
            r0 = move-exception
            r4 = r88
            r26 = r90
            r12 = r6
            r8 = r9
            r61 = r23
            r9 = r1
            r3 = r2
            r34 = r4
            r56 = r9
        L_0x123c:
            r5 = r29
            r1 = -5
            r28 = 0
            r30 = 0
            goto L_0x0782
        L_0x1245:
            r0 = move-exception
            r4 = r88
            r26 = r90
            r12 = r6
            r8 = r9
            r61 = r23
            r9 = r1
            r2 = r0
            r34 = r4
            r56 = r9
            r5 = r29
            r1 = -5
            r3 = 0
            r28 = 0
            r30 = 0
            goto L_0x12dc
        L_0x125e:
            r0 = move-exception
            r4 = r88
            r26 = r90
            r12 = r6
            r8 = r9
            r61 = r23
            r2 = r0
            r34 = r4
            r5 = r29
            r1 = -5
            r3 = 0
            goto L_0x073a
        L_0x1270:
            r0 = move-exception
            r4 = r88
            r26 = r90
            r8 = r9
        L_0x1276:
            r12 = r81
            r11 = r82
        L_0x127a:
            r2 = r0
            r34 = r4
        L_0x127d:
            r9 = r29
            goto L_0x058b
        L_0x1281:
            r0 = move-exception
            r4 = r88
            r26 = r90
            r8 = r9
            r61 = r23
            r2 = r0
            r34 = r4
            r5 = r29
            goto L_0x0737
        L_0x1290:
            r0 = move-exception
            r26 = r90
            r1 = r5
            r8 = r9
            r4 = r88
            r12 = r81
            r11 = r82
            r2 = r0
            r9 = r1
            goto L_0x12c2
        L_0x129e:
            r0 = move-exception
            r26 = r90
            r1 = r5
            r8 = r9
            r61 = r23
            r4 = r88
            r2 = r0
            r34 = r4
            r3 = 0
            r6 = 0
            r28 = 0
            r30 = 0
            r56 = 0
            r5 = r1
        L_0x12b3:
            r1 = -5
            goto L_0x12dc
        L_0x12b5:
            r0 = move-exception
            r4 = r88
            r8 = r9
            r12 = r81
            r11 = r82
            r9 = r84
            r26 = r90
            r2 = r0
        L_0x12c2:
            r34 = r4
            goto L_0x058b
        L_0x12c6:
            r0 = move-exception
            r4 = r88
            r61 = r3
            r8 = r9
            r26 = r90
            r2 = r0
            r34 = r4
            r1 = -5
            r3 = 0
            r6 = 0
            r28 = 0
            r30 = 0
            r56 = 0
            r5 = r84
        L_0x12dc:
            boolean r4 = r2 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x1351 }
            if (r4 == 0) goto L_0x12e5
            if (r95 != 0) goto L_0x12e5
            r47 = 1
            goto L_0x12e7
        L_0x12e5:
            r47 = 0
        L_0x12e7:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x1346 }
            r4.<init>()     // Catch:{ all -> 0x1346 }
            java.lang.String r7 = "bitrate: "
            r4.append(r7)     // Catch:{ all -> 0x1346 }
            r4.append(r5)     // Catch:{ all -> 0x1346 }
            java.lang.String r7 = " framerate: "
            r4.append(r7)     // Catch:{ all -> 0x1346 }
            r4.append(r8)     // Catch:{ all -> 0x1346 }
            java.lang.String r7 = " size: "
            r4.append(r7)     // Catch:{ all -> 0x1346 }
            r7 = r82
            r4.append(r7)     // Catch:{ all -> 0x1342 }
            java.lang.String r9 = "x"
            r4.append(r9)     // Catch:{ all -> 0x1342 }
            r9 = r81
            r4.append(r9)     // Catch:{ all -> 0x1340 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x1340 }
            org.telegram.messenger.FileLog.e((java.lang.String) r4)     // Catch:{ all -> 0x1340 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x1340 }
            r2 = r3
            r29 = r5
            r3 = r6
            r6 = 1
        L_0x1320:
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ all -> 0x1338 }
            r5 = r61
            r4.unselectTrack(r5)     // Catch:{ all -> 0x1338 }
            if (r2 == 0) goto L_0x132f
            r2.stop()     // Catch:{ all -> 0x1338 }
            r2.release()     // Catch:{ all -> 0x1338 }
        L_0x132f:
            r42 = r3
            r2 = r47
            r47 = r6
            r6 = r30
            goto L_0x1376
        L_0x1338:
            r0 = move-exception
            r2 = r0
            r11 = r7
            r12 = r9
            r9 = r29
            goto L_0x0621
        L_0x1340:
            r0 = move-exception
            goto L_0x134b
        L_0x1342:
            r0 = move-exception
            r9 = r81
            goto L_0x134b
        L_0x1346:
            r0 = move-exception
            r9 = r81
            r7 = r82
        L_0x134b:
            r2 = r0
            r11 = r7
            r12 = r9
            r7 = r47
            goto L_0x135a
        L_0x1351:
            r0 = move-exception
            r9 = r81
            r7 = r82
            r2 = r0
            r11 = r7
            r12 = r9
        L_0x1359:
            r7 = 0
        L_0x135a:
            r9 = r5
            goto L_0x13dd
        L_0x135d:
            r9 = r81
            r7 = r82
            r8 = r83
            r4 = r88
            r29 = r84
            r26 = r90
            r34 = r4
            r1 = -5
            r2 = 0
            r6 = 0
            r28 = 0
            r42 = 0
            r47 = 0
            r56 = 0
        L_0x1376:
            if (r6 == 0) goto L_0x1385
            r6.release()     // Catch:{ all -> 0x137c }
            goto L_0x1385
        L_0x137c:
            r0 = move-exception
            r11 = r7
            r12 = r9
            r9 = r29
            r7 = r2
        L_0x1382:
            r2 = r0
            goto L_0x13dd
        L_0x1385:
            if (r56 == 0) goto L_0x138a
            r56.release()     // Catch:{ all -> 0x137c }
        L_0x138a:
            if (r42 == 0) goto L_0x1392
            r42.stop()     // Catch:{ all -> 0x137c }
            r42.release()     // Catch:{ all -> 0x137c }
        L_0x1392:
            if (r28 == 0) goto L_0x1397
            r28.release()     // Catch:{ all -> 0x137c }
        L_0x1397:
            r76.checkConversionCanceled()     // Catch:{ all -> 0x137c }
            r13 = r2
            r6 = r47
        L_0x139d:
            android.media.MediaExtractor r2 = r15.extractor
            if (r2 == 0) goto L_0x13a4
            r2.release()
        L_0x13a4:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer
            if (r2 == 0) goto L_0x13b9
            r2.finishMovie()     // Catch:{ all -> 0x13b4 }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ all -> 0x13b4 }
            long r1 = r2.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x13b4 }
            r15.endPresentationTime = r1     // Catch:{ all -> 0x13b4 }
            goto L_0x13b9
        L_0x13b4:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x13b9:
            r45 = r6
            r6 = r9
            r9 = r29
            goto L_0x142e
        L_0x13c0:
            r0 = move-exception
            r7 = r82
            r4 = r88
            r9 = r12
            r8 = r13
            r26 = r90
            r2 = r0
            r34 = r4
            r11 = r7
            goto L_0x13d9
        L_0x13ce:
            r0 = move-exception
            r4 = r88
            r8 = r10
            r7 = r11
            r9 = r12
            r26 = r90
            r2 = r0
            r34 = r4
        L_0x13d9:
            r1 = -5
            r7 = 0
            r9 = r84
        L_0x13dd:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x149a }
            r3.<init>()     // Catch:{ all -> 0x149a }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x149a }
            r3.append(r9)     // Catch:{ all -> 0x149a }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x149a }
            r3.append(r8)     // Catch:{ all -> 0x149a }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x149a }
            r3.append(r11)     // Catch:{ all -> 0x149a }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x149a }
            r3.append(r12)     // Catch:{ all -> 0x149a }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x149a }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x149a }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x149a }
            android.media.MediaExtractor r2 = r15.extractor
            if (r2 == 0) goto L_0x1414
            r2.release()
        L_0x1414:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer
            if (r2 == 0) goto L_0x1429
            r2.finishMovie()     // Catch:{ all -> 0x1424 }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ all -> 0x1424 }
            long r1 = r2.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x1424 }
            r15.endPresentationTime = r1     // Catch:{ all -> 0x1424 }
            goto L_0x1429
        L_0x1424:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1429:
            r13 = r7
            r7 = r11
            r6 = r12
            r45 = 1
        L_0x142e:
            if (r13 == 0) goto L_0x1459
            r20 = 1
            r1 = r76
            r2 = r77
            r3 = r78
            r4 = r79
            r5 = r80
            r8 = r83
            r10 = r85
            r11 = r86
            r13 = r34
            r15 = r26
            r17 = r92
            r19 = r94
            r21 = r96
            r22 = r97
            r23 = r98
            r24 = r99
            r25 = r100
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r13, r15, r17, r19, r20, r21, r22, r23, r24, r25)
            return r1
        L_0x1459:
            long r1 = java.lang.System.currentTimeMillis()
            long r1 = r1 - r16
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x1499
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
            r3.append(r6)
            java.lang.String r1 = " h="
            r3.append(r1)
            r3.append(r7)
            java.lang.String r1 = " bitrate="
            r3.append(r1)
            r3.append(r9)
            java.lang.String r1 = r3.toString()
            org.telegram.messenger.FileLog.d(r1)
        L_0x1499:
            return r45
        L_0x149a:
            r0 = move-exception
            r2 = r76
            r3 = r0
            android.media.MediaExtractor r4 = r2.extractor
            if (r4 == 0) goto L_0x14a5
            r4.release()
        L_0x14a5:
            org.telegram.messenger.video.MP4Builder r4 = r2.mediaMuxer
            if (r4 == 0) goto L_0x14ba
            r4.finishMovie()     // Catch:{ all -> 0x14b5 }
            org.telegram.messenger.video.MP4Builder r4 = r2.mediaMuxer     // Catch:{ all -> 0x14b5 }
            long r4 = r4.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x14b5 }
            r2.endPresentationTime = r4     // Catch:{ all -> 0x14b5 }
            goto L_0x14ba
        L_0x14b5:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x14ba:
            goto L_0x14bc
        L_0x14bb:
            throw r3
        L_0x14bc:
            goto L_0x14bb
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
