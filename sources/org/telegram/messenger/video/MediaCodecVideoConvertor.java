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
    /* JADX WARNING: Code restructure failed: missing block: B:1001:0x10cd, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1021:0x1134, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1022:0x1135, code lost:
        r8 = r83;
        r34 = r4;
        r9 = r56;
        r5 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1028:0x1162, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1029:0x1163, code lost:
        r8 = r83;
        r2 = r3;
        r34 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1037:0x1195, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1038:0x1196, code lost:
        r8 = r83;
        r34 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1055:0x11f0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1056:0x11f1, code lost:
        r8 = r83;
        r12 = r84;
        r4 = r88;
        r2 = r3;
        r9 = r56;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1057:0x11fb, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1058:0x11fc, code lost:
        r8 = r83;
        r12 = r84;
        r4 = r88;
        r9 = r56;
        r3 = r57;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1059:0x1207, code lost:
        r34 = r4;
        r6 = r12;
        r5 = r29;
        r1 = -5;
        r28 = null;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1060:0x1211, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1061:0x1212, code lost:
        r8 = r83;
        r4 = r88;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1067:0x1240, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1068:0x1241, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:1069:0x1259, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1070:0x125a, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:1071:0x126b, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1072:0x126c, code lost:
        r4 = r88;
        r26 = r90;
        r8 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1076:0x127c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1077:0x127d, code lost:
        r26 = r90;
        r8 = r9;
        r61 = r23;
        r2 = r0;
        r34 = r88;
        r5 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x037c, code lost:
        r4 = java.nio.ByteBuffer.allocate(r6);
        r7 = java.nio.ByteBuffer.allocate(r14.size - r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x0387, code lost:
        r21 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:?, code lost:
        r4.put(r3, 0, r6).position(0);
        r7.put(r3, r6, r14.size - r6).position(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x039b, code lost:
        r6 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x039d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x039e, code lost:
        r2 = r0;
        r6 = r13;
        r1 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x03ad, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x03ae, code lost:
        r21 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x04f6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x04f7, code lost:
        r8 = r83;
        r34 = r88;
        r26 = r90;
        r2 = r0;
        r1 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x0501, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x0502, code lost:
        r40 = r40;
        r6 = r13;
        r1 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x0508, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x050a, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x050b, code lost:
        r21 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x0514, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:277:0x053b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x053c, code lost:
        r2 = r40;
        r6 = r81;
        r11 = r51;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x0545, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0546, code lost:
        r8 = r83;
        r34 = r88;
        r26 = r90;
        r2 = r0;
        r9 = r50;
        r11 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x0552, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0553, code lost:
        r2 = r40;
        r6 = r81;
        r11 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x055b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x055c, code lost:
        r13 = r6;
        r50 = r9;
        r51 = r11;
        r40 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x0563, code lost:
        r1 = -5;
        r19 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x0568, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x0569, code lost:
        r13 = r6;
        r50 = r9;
        r51 = r11;
        r2 = r0;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x0571, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x0572, code lost:
        r50 = r9;
        r51 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x059f, code lost:
        r47 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x08d0, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x08dc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x08dd, code lost:
        r8 = r83;
        r6 = r84;
        r34 = r88;
        r2 = r0;
        r28 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x0996, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x09ba, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x09bb, code lost:
        r4 = r88;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x09c8, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0b49, code lost:
        if (r13.presentationTimeUs < r4) goto L_0x0b4b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0CLASSNAME, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x01d3, code lost:
        r6 = r7;
        r7 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x0d1e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x0d1f, code lost:
        r8 = r83;
        r2 = r0;
        r34 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x0d4c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x0e4f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x0e51, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x0e52, code lost:
        r8 = r83;
        r2 = r0;
        r6 = r12;
        r5 = r29;
        r3 = r57;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x0f0a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x0f0b, code lost:
        r8 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x0faf, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x0fb1, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x0fb4, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x0fb5, code lost:
        r8 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x0221, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:0x1049, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x108d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:992:0x108e, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:993:0x10a0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:994:0x10a1, code lost:
        r9 = r56;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1001:0x10cd A[ExcHandler: all (th java.lang.Throwable), PHI: r8 
      PHI: (r8v65 int) = (r8v64 int), (r8v71 int), (r8v71 int), (r8v71 int), (r8v71 int) binds: [B:998:0x10b6, B:911:0x0f6f, B:912:?, B:905:0x0var_, B:906:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:905:0x0var_] */
    /* JADX WARNING: Removed duplicated region for block: B:1037:0x1195 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:698:0x0CLASSNAME] */
    /* JADX WARNING: Removed duplicated region for block: B:1060:0x1211 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:478:0x08ae] */
    /* JADX WARNING: Removed duplicated region for block: B:1071:0x126b A[ExcHandler: all (th java.lang.Throwable), Splitter:B:468:0x084e] */
    /* JADX WARNING: Removed duplicated region for block: B:1091:0x12db A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:1106:0x1323 A[Catch:{ all -> 0x1332 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1120:0x1357  */
    /* JADX WARNING: Removed duplicated region for block: B:1122:0x1372 A[SYNTHETIC, Splitter:B:1122:0x1372] */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x1381 A[Catch:{ all -> 0x1376 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1130:0x1386 A[Catch:{ all -> 0x1376 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1132:0x138e A[Catch:{ all -> 0x1376 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1137:0x139b  */
    /* JADX WARNING: Removed duplicated region for block: B:1140:0x13a2 A[SYNTHETIC, Splitter:B:1140:0x13a2] */
    /* JADX WARNING: Removed duplicated region for block: B:1154:0x140a  */
    /* JADX WARNING: Removed duplicated region for block: B:1157:0x1411 A[SYNTHETIC, Splitter:B:1157:0x1411] */
    /* JADX WARNING: Removed duplicated region for block: B:1163:0x1429  */
    /* JADX WARNING: Removed duplicated region for block: B:1165:0x1452  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x0417 A[Catch:{ Exception -> 0x04b0, all -> 0x04ab }] */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0419 A[Catch:{ Exception -> 0x04b0, all -> 0x04ab }] */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0428  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x043f  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x04f6 A[ExcHandler: all (r0v170 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r21 
      PHI: (r21v22 int) = (r21v21 int), (r21v33 int), (r21v33 int) binds: [B:254:0x04bf, B:189:0x038a, B:190:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:189:0x038a] */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x050a A[ExcHandler: all (th java.lang.Throwable), PHI: r1 
      PHI: (r1v182 int) = (r1v181 int), (r1v181 int), (r1v181 int), (r1v181 int), (r1v181 int), (r1v181 int), (r1v181 int), (r1v209 int), (r1v181 int) binds: [B:73:0x01d6, B:74:?, B:78:0x01e4, B:116:0x0298, B:117:?, B:125:0x02a7, B:126:?, B:178:0x0366, B:122:0x02a1] A[DONT_GENERATE, DONT_INLINE], Splitter:B:78:0x01e4] */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0514 A[ExcHandler: all (th java.lang.Throwable), PHI: r1 r51 
      PHI: (r1v180 int) = (r1v175 int), (r1v175 int), (r1v181 int), (r1v181 int), (r1v181 int), (r1v181 int) binds: [B:67:0x01c6, B:68:?, B:137:0x02bc, B:120:0x029e, B:121:?, B:86:0x0203] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r51v10 int) = (r51v5 int), (r51v5 int), (r51v11 int), (r51v11 int), (r51v11 int), (r51v11 int) binds: [B:67:0x01c6, B:68:?, B:137:0x02bc, B:120:0x029e, B:121:?, B:86:0x0203] A[DONT_GENERATE, DONT_INLINE], Splitter:B:67:0x01c6] */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x0545 A[ExcHandler: all (r0v152 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:53:0x01a5] */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x0571 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:39:0x0111] */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x059d A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x05df A[SYNTHETIC, Splitter:B:320:0x05df] */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x05ee A[Catch:{ all -> 0x05e3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x05f3 A[Catch:{ all -> 0x05e3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x06cd  */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x071c A[SYNTHETIC, Splitter:B:393:0x071c] */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0745  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0748  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x074e A[SYNTHETIC, Splitter:B:409:0x074e] */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x0782  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x07b5  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x07cc  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x07d6 A[SYNTHETIC, Splitter:B:444:0x07d6] */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x081a A[SYNTHETIC, Splitter:B:451:0x081a] */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x084a  */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x08c7 A[SYNTHETIC, Splitter:B:487:0x08c7] */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x08d0 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:487:0x08c7] */
    /* JADX WARNING: Removed duplicated region for block: B:494:0x08ea  */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x08f0 A[SYNTHETIC, Splitter:B:497:0x08f0] */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x091e  */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x0921  */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x09c8 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:535:0x0982] */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x09cc  */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09f5  */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x0a03  */
    /* JADX WARNING: Removed duplicated region for block: B:573:0x0a06  */
    /* JADX WARNING: Removed duplicated region for block: B:578:0x0a28 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a46 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:598:0x0a77  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0bd6  */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0bf6 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:696:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:697:0x0c1d  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0c2c  */
    /* JADX WARNING: Removed duplicated region for block: B:704:0x0c4b  */
    /* JADX WARNING: Removed duplicated region for block: B:712:0x0CLASSNAME A[Catch:{ Exception -> 0x0c7a, all -> 0x0CLASSNAME }, ExcHandler: all (th java.lang.Throwable), Splitter:B:706:0x0c4e] */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x0e4f A[ExcHandler: all (th java.lang.Throwable), PHI: r34 
      PHI: (r34v68 long) = (r34v69 long), (r34v69 long), (r34v69 long), (r34v72 long) binds: [B:798:0x0da0, B:799:?, B:801:0x0da5, B:771:0x0d41] A[DONT_GENERATE, DONT_INLINE], Splitter:B:771:0x0d41] */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x0e7d A[Catch:{ Exception -> 0x112b, all -> 0x1125 }] */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x0e80 A[Catch:{ Exception -> 0x112b, all -> 0x1125 }] */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x0e90  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x0eb0  */
    /* JADX WARNING: Removed duplicated region for block: B:874:0x0f0a A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:868:0x0ee4] */
    /* JADX WARNING: Removed duplicated region for block: B:924:0x0fb1 A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:899:0x0var_] */
    /* JADX WARNING: Removed duplicated region for block: B:932:0x0fd3  */
    /* JADX WARNING: Removed duplicated region for block: B:933:0x0fd6  */
    /* JADX WARNING: Removed duplicated region for block: B:940:0x0fe4 A[SYNTHETIC, Splitter:B:940:0x0fe4] */
    /* JADX WARNING: Removed duplicated region for block: B:945:0x1008 A[Catch:{ Exception -> 0x1016, all -> 0x108d }] */
    /* JADX WARNING: Removed duplicated region for block: B:953:0x101d A[Catch:{ Exception -> 0x1016, all -> 0x108d }] */
    /* JADX WARNING: Removed duplicated region for block: B:954:0x1020 A[Catch:{ Exception -> 0x1016, all -> 0x108d }] */
    /* JADX WARNING: Removed duplicated region for block: B:962:0x1035  */
    /* JADX WARNING: Removed duplicated region for block: B:980:0x1066 A[Catch:{ Exception -> 0x1085, all -> 0x108d }] */
    /* JADX WARNING: Removed duplicated region for block: B:983:0x1072 A[Catch:{ Exception -> 0x1085, all -> 0x108d }] */
    /* JADX WARNING: Removed duplicated region for block: B:988:0x1081  */
    /* JADX WARNING: Removed duplicated region for block: B:991:0x108d A[ExcHandler: all (r0v53 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:959:0x1030] */
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
            android.media.MediaCodec$BufferInfo r6 = new android.media.MediaCodec$BufferInfo     // Catch:{ all -> 0x13c8 }
            r6.<init>()     // Catch:{ all -> 0x13c8 }
            org.telegram.messenger.video.Mp4Movie r1 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ all -> 0x13c8 }
            r1.<init>()     // Catch:{ all -> 0x13c8 }
            r22 = r6
            r6 = r78
            r1.setCacheFile(r6)     // Catch:{ all -> 0x13c8 }
            r1.setRotation(r7)     // Catch:{ all -> 0x13c8 }
            r1.setSize(r12, r11)     // Catch:{ all -> 0x13c8 }
            org.telegram.messenger.video.MP4Builder r7 = new org.telegram.messenger.video.MP4Builder     // Catch:{ all -> 0x13c8 }
            r7.<init>()     // Catch:{ all -> 0x13c8 }
            r6 = r80
            org.telegram.messenger.video.MP4Builder r1 = r7.createMovie(r1, r6)     // Catch:{ all -> 0x13c8 }
            r15.mediaMuxer = r1     // Catch:{ all -> 0x13c8 }
            float r1 = (float) r4     // Catch:{ all -> 0x13c8 }
            r24 = 1148846080(0x447a0000, float:1000.0)
            float r25 = r1 / r24
            r26 = 1000(0x3e8, double:4.94E-321)
            long r1 = r4 * r26
            r15.endPresentationTime = r1     // Catch:{ all -> 0x13c8 }
            r76.checkConversionCanceled()     // Catch:{ all -> 0x13c8 }
            java.lang.String r7 = "csd-1"
            java.lang.String r1 = "csd-0"
            java.lang.String r6 = "prepend-sps-pps-to-idr-frames"
            r2 = 921600(0xe1000, float:1.291437E-39)
            r28 = r6
            r27 = r7
            java.lang.String r7 = "video/avc"
            r34 = r7
            r6 = 0
            if (r99 == 0) goto L_0x062e
            int r18 = (r90 > r6 ? 1 : (r90 == r6 ? 0 : -1))
            if (r18 < 0) goto L_0x008c
            r2 = 1157234688(0x44fa0000, float:2000.0)
            int r2 = (r25 > r2 ? 1 : (r25 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0077
            r2 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            r9 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x0091
        L_0x0077:
            r2 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r2 = (r25 > r2 ? 1 : (r25 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0085
            r2 = 2200000(0x2191c0, float:3.082857E-39)
            r9 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x0091
        L_0x0085:
            r2 = 1560000(0x17cdc0, float:2.186026E-39)
            r9 = 1560000(0x17cdc0, float:2.186026E-39)
            goto L_0x0091
        L_0x008c:
            if (r9 > 0) goto L_0x0091
            r9 = 921600(0xe1000, float:1.291437E-39)
        L_0x0091:
            int r2 = r12 % 16
            r18 = 1098907648(0x41800000, float:16.0)
            if (r2 == 0) goto L_0x00da
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            if (r2 == 0) goto L_0x00c0
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            r2.<init>()     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            java.lang.String r6 = "changing width from "
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            r2.append(r12)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            float r6 = (float) r12     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            float r6 = r6 / r18
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
        L_0x00c0:
            float r2 = (float) r12     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            float r2 = r2 / r18
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            int r2 = r2 * 16
            r12 = r2
            goto L_0x00da
        L_0x00cb:
            r0 = move-exception
            r34 = r88
            r26 = r90
            r2 = r0
            r8 = r10
            goto L_0x0589
        L_0x00d4:
            r0 = move-exception
            r2 = r0
            r50 = r9
            goto L_0x0593
        L_0x00da:
            int r2 = r11 % 16
            if (r2 == 0) goto L_0x0111
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            if (r2 == 0) goto L_0x0107
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            r2.<init>()     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            java.lang.String r6 = "changing height from "
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            r2.append(r11)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            float r6 = (float) r11     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            float r6 = r6 / r18
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
        L_0x0107:
            float r2 = (float) r11     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            float r2 = r2 / r18
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            int r2 = r2 * 16
            r11 = r2
        L_0x0111:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0577, all -> 0x0571 }
            if (r2 == 0) goto L_0x0139
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            r2.<init>()     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            java.lang.String r6 = "create photo encoder "
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            r2.append(r12)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            java.lang.String r6 = " "
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            r2.append(r11)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            java.lang.String r6 = " duration = "
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            r2.append(r4)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d4, all -> 0x00cb }
        L_0x0139:
            r7 = r34
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r7, r12, r11)     // Catch:{ Exception -> 0x0577, all -> 0x0571 }
            java.lang.String r6 = "color-format"
            r34 = r1
            r1 = 2130708361(0x7var_, float:1.701803E38)
            r2.setInteger(r6, r1)     // Catch:{ Exception -> 0x0577, all -> 0x0571 }
            java.lang.String r1 = "bitrate"
            r2.setInteger(r1, r9)     // Catch:{ Exception -> 0x0577, all -> 0x0571 }
            java.lang.String r1 = "frame-rate"
            r2.setInteger(r1, r10)     // Catch:{ Exception -> 0x0577, all -> 0x0571 }
            java.lang.String r1 = "i-frame-interval"
            r6 = 2
            r2.setInteger(r1, r6)     // Catch:{ Exception -> 0x0577, all -> 0x0571 }
            android.media.MediaCodec r6 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x0577, all -> 0x0571 }
            r18 = r7
            r1 = 0
            r7 = 1
            r6.configure(r2, r1, r1, r7)     // Catch:{ Exception -> 0x0568, all -> 0x0571 }
            org.telegram.messenger.video.InputSurface r2 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x0568, all -> 0x0571 }
            android.view.Surface r1 = r6.createInputSurface()     // Catch:{ Exception -> 0x0568, all -> 0x0571 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x0568, all -> 0x0571 }
            r2.makeCurrent()     // Catch:{ Exception -> 0x055b, all -> 0x0571 }
            r6.start()     // Catch:{ Exception -> 0x055b, all -> 0x0571 }
            org.telegram.messenger.video.OutputSurface r19 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x055b, all -> 0x0571 }
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
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0552, all -> 0x0545 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x053b, all -> 0x0545 }
            if (r1 >= r13) goto L_0x01ba
            java.nio.ByteBuffer[] r6 = r81.getOutputBuffers()     // Catch:{ Exception -> 0x01b1, all -> 0x0545 }
            goto L_0x01bb
        L_0x01b1:
            r0 = move-exception
            r6 = r81
            r2 = r0
            r11 = r51
            r1 = -5
            goto L_0x0599
        L_0x01ba:
            r6 = 0
        L_0x01bb:
            r76.checkConversionCanceled()     // Catch:{ Exception -> 0x053b, all -> 0x0545 }
            r1 = -5
            r2 = 1
            r3 = 0
            r4 = 0
            r5 = 0
            r7 = 0
        L_0x01c4:
            if (r7 != 0) goto L_0x052b
            r76.checkConversionCanceled()     // Catch:{ Exception -> 0x0522, all -> 0x0514 }
            r8 = r3 ^ 1
            r9 = r7
            r7 = r6
            r6 = 1
        L_0x01ce:
            if (r8 != 0) goto L_0x01d6
            if (r6 == 0) goto L_0x01d3
            goto L_0x01d6
        L_0x01d3:
            r6 = r7
            r7 = r9
            goto L_0x01c4
        L_0x01d6:
            r76.checkConversionCanceled()     // Catch:{ Exception -> 0x050e, all -> 0x050a }
            if (r95 == 0) goto L_0x01e0
            r10 = 22000(0x55f0, double:1.08694E-319)
            r13 = r81
            goto L_0x01e4
        L_0x01e0:
            r13 = r81
            r10 = 2500(0x9c4, double:1.235E-320)
        L_0x01e4:
            int r10 = r13.dequeueOutputBuffer(r14, r10)     // Catch:{ Exception -> 0x0508, all -> 0x050a }
            r11 = -1
            if (r10 != r11) goto L_0x0200
            r81 = r1
            r20 = r4
            r18 = r5
            r82 = r8
            r8 = r39
            r11 = r48
            r4 = r49
            r1 = -1
            r6 = 0
        L_0x01fb:
            r5 = r3
            r3 = r51
            goto L_0x0426
        L_0x0200:
            r11 = -3
            if (r10 != r11) goto L_0x0228
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r81 = r6
            r6 = 21
            if (r11 >= r6) goto L_0x020f
            java.nio.ByteBuffer[] r7 = r13.getOutputBuffers()     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
        L_0x020f:
            r6 = r81
            r81 = r1
            r20 = r4
            r18 = r5
            r82 = r8
            r8 = r39
            r11 = r48
        L_0x021d:
            r4 = r49
            r1 = -1
            goto L_0x01fb
        L_0x0221:
            r0 = move-exception
        L_0x0222:
            r2 = r0
        L_0x0223:
            r6 = r13
        L_0x0224:
            r11 = r51
            goto L_0x0599
        L_0x0228:
            r81 = r6
            r6 = -2
            if (r10 != r6) goto L_0x0290
            android.media.MediaFormat r6 = r13.getOutputFormat()     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            if (r11 == 0) goto L_0x024c
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r11.<init>()     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r82 = r8
            java.lang.String r8 = "photo encoder new format "
            r11.append(r8)     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r11.append(r6)     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            java.lang.String r8 = r11.toString()     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            org.telegram.messenger.FileLog.d(r8)     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            goto L_0x024e
        L_0x024c:
            r82 = r8
        L_0x024e:
            r8 = -5
            if (r1 != r8) goto L_0x0283
            if (r6 == 0) goto L_0x0283
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r8 = 0
            int r1 = r11.addTrack(r6, r8)     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r11 = r28
            boolean r18 = r6.containsKey(r11)     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            if (r18 == 0) goto L_0x0281
            int r8 = r6.getInteger(r11)     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r28 = r11
            r11 = 1
            if (r8 != r11) goto L_0x0283
            r8 = r39
            java.nio.ByteBuffer r4 = r6.getByteBuffer(r8)     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r11 = r48
            java.nio.ByteBuffer r6 = r6.getByteBuffer(r11)     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            int r4 = r4.limit()     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            int r6 = r6.limit()     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            int r4 = r4 + r6
            goto L_0x0287
        L_0x0281:
            r28 = r11
        L_0x0283:
            r8 = r39
            r11 = r48
        L_0x0287:
            r6 = r81
            r81 = r1
            r20 = r4
            r18 = r5
            goto L_0x021d
        L_0x0290:
            r82 = r8
            r8 = r39
            r11 = r48
            if (r10 < 0) goto L_0x04db
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0508, all -> 0x050a }
            r9 = 21
            if (r6 >= r9) goto L_0x02a1
            r6 = r7[r10]     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            goto L_0x02a5
        L_0x02a1:
            java.nio.ByteBuffer r6 = r13.getOutputBuffer(r10)     // Catch:{ Exception -> 0x0508, all -> 0x050a }
        L_0x02a5:
            if (r6 == 0) goto L_0x04bb
            int r9 = r14.size     // Catch:{ Exception -> 0x04b4, all -> 0x050a }
            r84 = r7
            r7 = 1
            if (r9 <= r7) goto L_0x0404
            int r7 = r14.flags     // Catch:{ Exception -> 0x03fa, all -> 0x03e9 }
            r18 = r7 & 2
            if (r18 != 0) goto L_0x0345
            if (r4 == 0) goto L_0x02c5
            r18 = r7 & 1
            if (r18 == 0) goto L_0x02c5
            r18 = r5
            int r5 = r14.offset     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            int r5 = r5 + r4
            r14.offset = r5     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            int r9 = r9 - r4
            r14.size = r9     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            goto L_0x02c7
        L_0x02c5:
            r18 = r5
        L_0x02c7:
            if (r2 == 0) goto L_0x0316
            r5 = r7 & 1
            if (r5 == 0) goto L_0x0316
            int r2 = r14.size     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r7 = 100
            if (r2 <= r7) goto L_0x0314
            int r2 = r14.offset     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r6.position(r2)     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            byte[] r2 = new byte[r7]     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r6.get(r2)     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r5 = 0
            r9 = 0
        L_0x02df:
            r7 = 96
            if (r5 >= r7) goto L_0x0314
            byte r7 = r2[r5]     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            if (r7 != 0) goto L_0x030b
            int r7 = r5 + 1
            byte r7 = r2[r7]     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            if (r7 != 0) goto L_0x030b
            int r7 = r5 + 2
            byte r7 = r2[r7]     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            if (r7 != 0) goto L_0x030b
            int r7 = r5 + 3
            byte r7 = r2[r7]     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r20 = r2
            r2 = 1
            if (r7 != r2) goto L_0x030d
            int r9 = r9 + 1
            if (r9 <= r2) goto L_0x030d
            int r2 = r14.offset     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            int r2 = r2 + r5
            r14.offset = r2     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            int r2 = r14.size     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            int r2 = r2 - r5
            r14.size = r2     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            goto L_0x0314
        L_0x030b:
            r20 = r2
        L_0x030d:
            int r5 = r5 + 1
            r2 = r20
            r7 = 100
            goto L_0x02df
        L_0x0314:
            r7 = 0
            goto L_0x0317
        L_0x0316:
            r7 = r2
        L_0x0317:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r9 = r7
            r5 = 1
            long r6 = r2.writeSampleData(r1, r6, r14, r5)     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r5 = r3
            r2 = 0
            int r20 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r20 == 0) goto L_0x0339
            org.telegram.messenger.MediaController$VideoConvertorListener r2 = r15.callback     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            if (r2 == 0) goto L_0x0339
            r20 = r4
            r21 = r9
            r3 = 0
            float r9 = (float) r3     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            float r9 = r9 / r24
            float r9 = r9 / r25
            r2.didWriteData(r6, r9)     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            goto L_0x033d
        L_0x0339:
            r20 = r4
            r21 = r9
        L_0x033d:
            r2 = r21
            r4 = r49
            r3 = r51
            goto L_0x0411
        L_0x0345:
            r20 = r4
            r18 = r5
            r7 = -5
            r5 = r3
            if (r1 != r7) goto L_0x03e4
            byte[] r3 = new byte[r9]     // Catch:{ Exception -> 0x03fa, all -> 0x03e9 }
            int r4 = r14.offset     // Catch:{ Exception -> 0x03fa, all -> 0x03e9 }
            int r4 = r4 + r9
            r6.limit(r4)     // Catch:{ Exception -> 0x03fa, all -> 0x03e9 }
            int r4 = r14.offset     // Catch:{ Exception -> 0x03fa, all -> 0x03e9 }
            r6.position(r4)     // Catch:{ Exception -> 0x03fa, all -> 0x03e9 }
            r6.get(r3)     // Catch:{ Exception -> 0x03fa, all -> 0x03e9 }
            int r4 = r14.size     // Catch:{ Exception -> 0x03fa, all -> 0x03e9 }
            r6 = 1
            int r4 = r4 - r6
        L_0x0361:
            if (r4 < 0) goto L_0x03b2
            r9 = 3
            if (r4 <= r9) goto L_0x03b2
            byte r7 = r3[r4]     // Catch:{ Exception -> 0x03ad, all -> 0x050a }
            if (r7 != r6) goto L_0x03a4
            int r6 = r4 + -1
            byte r6 = r3[r6]     // Catch:{ Exception -> 0x03ad, all -> 0x050a }
            if (r6 != 0) goto L_0x03a4
            int r6 = r4 + -2
            byte r6 = r3[r6]     // Catch:{ Exception -> 0x03ad, all -> 0x050a }
            if (r6 != 0) goto L_0x03a4
            int r6 = r4 + -3
            byte r7 = r3[r6]     // Catch:{ Exception -> 0x03ad, all -> 0x050a }
            if (r7 != 0) goto L_0x03a4
            java.nio.ByteBuffer r4 = java.nio.ByteBuffer.allocate(r6)     // Catch:{ Exception -> 0x03ad, all -> 0x050a }
            int r7 = r14.size     // Catch:{ Exception -> 0x03ad, all -> 0x050a }
            int r7 = r7 - r6
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.allocate(r7)     // Catch:{ Exception -> 0x03ad, all -> 0x050a }
            r21 = r1
            r9 = 0
            java.nio.ByteBuffer r1 = r4.put(r3, r9, r6)     // Catch:{ Exception -> 0x039d, all -> 0x04f6 }
            r1.position(r9)     // Catch:{ Exception -> 0x039d, all -> 0x04f6 }
            int r1 = r14.size     // Catch:{ Exception -> 0x039d, all -> 0x04f6 }
            int r1 = r1 - r6
            java.nio.ByteBuffer r1 = r7.put(r3, r6, r1)     // Catch:{ Exception -> 0x039d, all -> 0x04f6 }
            r1.position(r9)     // Catch:{ Exception -> 0x039d, all -> 0x04f6 }
            r6 = r4
            goto L_0x03b6
        L_0x039d:
            r0 = move-exception
            r2 = r0
            r6 = r13
            r1 = r21
            goto L_0x0224
        L_0x03a4:
            r21 = r1
            int r4 = r4 + -1
            r1 = r21
            r6 = 1
            r7 = -5
            goto L_0x0361
        L_0x03ad:
            r0 = move-exception
            r21 = r1
            goto L_0x0222
        L_0x03b2:
            r21 = r1
            r6 = 0
            r7 = 0
        L_0x03b6:
            r4 = r49
            r3 = r51
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r4, r12, r3)     // Catch:{ Exception -> 0x03dc, all -> 0x03d0 }
            if (r6 == 0) goto L_0x03c8
            if (r7 == 0) goto L_0x03c8
            r1.setByteBuffer(r8, r6)     // Catch:{ Exception -> 0x03dc, all -> 0x03d0 }
            r1.setByteBuffer(r11, r7)     // Catch:{ Exception -> 0x03dc, all -> 0x03d0 }
        L_0x03c8:
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x03dc, all -> 0x03d0 }
            r7 = 0
            int r1 = r6.addTrack(r1, r7)     // Catch:{ Exception -> 0x03dc, all -> 0x03d0 }
            goto L_0x0411
        L_0x03d0:
            r0 = move-exception
            r8 = r83
            r34 = r88
            r26 = r90
            r2 = r0
            r11 = r3
            r1 = r21
            goto L_0x03f6
        L_0x03dc:
            r0 = move-exception
            r2 = r0
            r11 = r3
            r6 = r13
            r1 = r21
            goto L_0x0599
        L_0x03e4:
            r21 = r1
            r4 = r49
            goto L_0x040d
        L_0x03e9:
            r0 = move-exception
            r21 = r1
            r3 = r51
            r8 = r83
            r34 = r88
            r26 = r90
            r2 = r0
            r11 = r3
        L_0x03f6:
            r9 = r50
            goto L_0x058a
        L_0x03fa:
            r0 = move-exception
            r21 = r1
            r3 = r51
            r2 = r0
            r11 = r3
            r6 = r13
            goto L_0x0599
        L_0x0404:
            r21 = r1
            r20 = r4
            r18 = r5
            r4 = r49
            r5 = r3
        L_0x040d:
            r3 = r51
            r1 = r21
        L_0x0411:
            int r6 = r14.flags     // Catch:{ Exception -> 0x04b0, all -> 0x04ab }
            r6 = r6 & 4
            if (r6 == 0) goto L_0x0419
            r6 = 1
            goto L_0x041a
        L_0x0419:
            r6 = 0
        L_0x041a:
            r7 = 0
            r13.releaseOutputBuffer(r10, r7)     // Catch:{ Exception -> 0x04b0, all -> 0x04ab }
            r7 = r84
            r9 = r6
            r6 = r81
            r81 = r1
            r1 = -1
        L_0x0426:
            if (r10 == r1) goto L_0x043f
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
            goto L_0x01ce
        L_0x043f:
            if (r5 != 0) goto L_0x048a
            r19.drawImage()     // Catch:{ Exception -> 0x0481, all -> 0x047a }
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
            r10.setPresentationTime(r2)     // Catch:{ Exception -> 0x0472, all -> 0x0470 }
            r10.swapBuffers()     // Catch:{ Exception -> 0x0472, all -> 0x0470 }
            int r1 = r1 + 1
            float r2 = (float) r1     // Catch:{ Exception -> 0x0472, all -> 0x0470 }
            r3 = 1106247680(0x41var_, float:30.0)
            float r3 = r3 * r25
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 < 0) goto L_0x0492
            r13.signalEndOfInputStream()     // Catch:{ Exception -> 0x0472, all -> 0x0470 }
            r5 = r1
            r1 = 0
            r3 = 1
            goto L_0x0496
        L_0x0470:
            r0 = move-exception
            goto L_0x047d
        L_0x0472:
            r0 = move-exception
            r1 = r81
            r2 = r0
            r40 = r10
            goto L_0x0223
        L_0x047a:
            r0 = move-exception
            r51 = r3
        L_0x047d:
            r1 = r81
            goto L_0x0515
        L_0x0481:
            r0 = move-exception
            r51 = r3
            r10 = r40
            r1 = r81
            goto L_0x0222
        L_0x048a:
            r84 = r2
            r51 = r3
            r1 = r18
            r10 = r40
        L_0x0492:
            r3 = r5
            r5 = r1
            r1 = r82
        L_0x0496:
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
            goto L_0x01ce
        L_0x04ab:
            r0 = move-exception
            r51 = r3
            goto L_0x0515
        L_0x04b0:
            r0 = move-exception
            r51 = r3
            goto L_0x04b7
        L_0x04b4:
            r0 = move-exception
            r21 = r1
        L_0x04b7:
            r10 = r40
            goto L_0x0222
        L_0x04bb:
            r21 = r1
            r2 = r40
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0501, all -> 0x04f6 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0501, all -> 0x04f6 }
            r3.<init>()     // Catch:{ Exception -> 0x0501, all -> 0x04f6 }
            java.lang.String r4 = "encoderOutputBuffer "
            r3.append(r4)     // Catch:{ Exception -> 0x0501, all -> 0x04f6 }
            r3.append(r10)     // Catch:{ Exception -> 0x0501, all -> 0x04f6 }
            java.lang.String r4 = " was null"
            r3.append(r4)     // Catch:{ Exception -> 0x0501, all -> 0x04f6 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0501, all -> 0x04f6 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x0501, all -> 0x04f6 }
            throw r1     // Catch:{ Exception -> 0x0501, all -> 0x04f6 }
        L_0x04db:
            r21 = r1
            r2 = r40
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0501, all -> 0x04f6 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0501, all -> 0x04f6 }
            r3.<init>()     // Catch:{ Exception -> 0x0501, all -> 0x04f6 }
            java.lang.String r4 = "unexpected result from encoder.dequeueOutputBuffer: "
            r3.append(r4)     // Catch:{ Exception -> 0x0501, all -> 0x04f6 }
            r3.append(r10)     // Catch:{ Exception -> 0x0501, all -> 0x04f6 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0501, all -> 0x04f6 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x0501, all -> 0x04f6 }
            throw r1     // Catch:{ Exception -> 0x0501, all -> 0x04f6 }
        L_0x04f6:
            r0 = move-exception
            r8 = r83
            r34 = r88
            r26 = r90
            r2 = r0
            r1 = r21
            goto L_0x051c
        L_0x0501:
            r0 = move-exception
            r40 = r2
            r6 = r13
            r1 = r21
            goto L_0x0528
        L_0x0508:
            r0 = move-exception
            goto L_0x0511
        L_0x050a:
            r0 = move-exception
            r21 = r1
            goto L_0x0515
        L_0x050e:
            r0 = move-exception
            r13 = r81
        L_0x0511:
            r21 = r1
            goto L_0x0525
        L_0x0514:
            r0 = move-exception
        L_0x0515:
            r8 = r83
            r34 = r88
            r26 = r90
            r2 = r0
        L_0x051c:
            r9 = r50
            r11 = r51
            goto L_0x058a
        L_0x0522:
            r0 = move-exception
            r13 = r81
        L_0x0525:
            r2 = r40
            r6 = r13
        L_0x0528:
            r11 = r51
            goto L_0x0566
        L_0x052b:
            r13 = r81
            r2 = r40
            r6 = r13
            r9 = r50
            r11 = r51
            r7 = 0
            r47 = 0
            r13 = r83
            goto L_0x05dd
        L_0x053b:
            r0 = move-exception
            r13 = r81
            r2 = r40
            r6 = r13
            r11 = r51
            r1 = -5
            goto L_0x0566
        L_0x0545:
            r0 = move-exception
            r8 = r83
            r34 = r88
            r26 = r90
            r2 = r0
            r9 = r50
            r11 = r51
            goto L_0x0589
        L_0x0552:
            r0 = move-exception
            r13 = r81
            r2 = r40
            r6 = r13
            r11 = r51
            goto L_0x0563
        L_0x055b:
            r0 = move-exception
            r13 = r6
            r50 = r9
            r51 = r11
            r40 = r2
        L_0x0563:
            r1 = -5
            r19 = 0
        L_0x0566:
            r2 = r0
            goto L_0x0599
        L_0x0568:
            r0 = move-exception
            r13 = r6
            r50 = r9
            r51 = r11
            r2 = r0
            r1 = -5
            goto L_0x0595
        L_0x0571:
            r0 = move-exception
            r50 = r9
            r51 = r11
            goto L_0x0582
        L_0x0577:
            r0 = move-exception
            r50 = r9
            r51 = r11
            goto L_0x0592
        L_0x057d:
            r0 = move-exception
            r50 = r9
            r11 = r82
        L_0x0582:
            r8 = r83
        L_0x0584:
            r34 = r88
            r26 = r90
            r2 = r0
        L_0x0589:
            r1 = -5
        L_0x058a:
            r7 = 0
            goto L_0x13d7
        L_0x058d:
            r0 = move-exception
            r50 = r9
            r11 = r82
        L_0x0592:
            r2 = r0
        L_0x0593:
            r1 = -5
            r6 = 0
        L_0x0595:
            r19 = 0
            r40 = 0
        L_0x0599:
            boolean r3 = r2 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x0622 }
            if (r3 == 0) goto L_0x05a2
            if (r95 != 0) goto L_0x05a2
            r47 = 1
            goto L_0x05a4
        L_0x05a2:
            r47 = 0
        L_0x05a4:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0614 }
            r3.<init>()     // Catch:{ all -> 0x0614 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x0614 }
            r9 = r50
            r3.append(r9)     // Catch:{ all -> 0x0612 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x0612 }
            r13 = r83
            r3.append(r13)     // Catch:{ all -> 0x060a }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x060a }
            r3.append(r11)     // Catch:{ all -> 0x060a }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x060a }
            r3.append(r12)     // Catch:{ all -> 0x060a }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x060a }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x060a }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x060a }
            r2 = r40
            r7 = r47
            r47 = 1
        L_0x05dd:
            if (r19 == 0) goto L_0x05ec
            r19.release()     // Catch:{ all -> 0x05e3 }
            goto L_0x05ec
        L_0x05e3:
            r0 = move-exception
            r34 = r88
            r26 = r90
            r2 = r0
            r8 = r13
            goto L_0x13d7
        L_0x05ec:
            if (r2 == 0) goto L_0x05f1
            r2.release()     // Catch:{ all -> 0x05e3 }
        L_0x05f1:
            if (r6 == 0) goto L_0x05f9
            r6.stop()     // Catch:{ all -> 0x05e3 }
            r6.release()     // Catch:{ all -> 0x05e3 }
        L_0x05f9:
            r76.checkConversionCanceled()     // Catch:{ all -> 0x05e3 }
            r34 = r88
            r26 = r90
            r29 = r9
            r9 = r12
            r8 = r13
            r6 = r47
            r13 = r7
            r7 = r11
            goto L_0x1397
        L_0x060a:
            r0 = move-exception
            r34 = r88
            r26 = r90
            r2 = r0
            r8 = r13
            goto L_0x061e
        L_0x0612:
            r0 = move-exception
            goto L_0x0617
        L_0x0614:
            r0 = move-exception
            r9 = r50
        L_0x0617:
            r8 = r83
            r34 = r88
            r26 = r90
            r2 = r0
        L_0x061e:
            r7 = r47
            goto L_0x13d7
        L_0x0622:
            r0 = move-exception
            r9 = r50
            r8 = r83
            r34 = r88
            r26 = r90
            r2 = r0
            goto L_0x058a
        L_0x062e:
            r8 = r1
            r13 = r10
            r14 = r22
            r11 = r27
            r1 = r28
            r4 = r34
            r10 = 3
            android.media.MediaExtractor r3 = new android.media.MediaExtractor     // Catch:{ all -> 0x13ba }
            r3.<init>()     // Catch:{ all -> 0x13ba }
            r15.extractor = r3     // Catch:{ all -> 0x13ba }
            r7 = r77
            r6 = r1
            r3.setDataSource(r7)     // Catch:{ all -> 0x13ba }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ all -> 0x13ba }
            r5 = 0
            int r3 = org.telegram.messenger.MediaController.findTrack(r1, r5)     // Catch:{ all -> 0x13ba }
            r1 = -1
            if (r9 == r1) goto L_0x0665
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ all -> 0x065a }
            r48 = r11
            r11 = 1
            int r1 = org.telegram.messenger.MediaController.findTrack(r1, r11)     // Catch:{ all -> 0x065a }
            goto L_0x0668
        L_0x065a:
            r0 = move-exception
            r11 = r82
            r34 = r88
            r26 = r90
            r2 = r0
            r8 = r13
            goto L_0x0589
        L_0x0665:
            r48 = r11
            r1 = -1
        L_0x0668:
            java.lang.String r11 = "mime"
            if (r3 < 0) goto L_0x0680
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ all -> 0x065a }
            android.media.MediaFormat r5 = r5.getTrackFormat(r3)     // Catch:{ all -> 0x065a }
            java.lang.String r5 = r5.getString(r11)     // Catch:{ all -> 0x065a }
            boolean r5 = r5.equals(r4)     // Catch:{ all -> 0x065a }
            if (r5 != 0) goto L_0x0680
            r13 = r94
            r5 = 1
            goto L_0x0683
        L_0x0680:
            r13 = r94
            r5 = 0
        L_0x0683:
            if (r13 != 0) goto L_0x06c8
            if (r5 == 0) goto L_0x0688
            goto L_0x06c8
        L_0x0688:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x06bb }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ all -> 0x06bb }
            r5 = -1
            if (r9 == r5) goto L_0x0692
            r18 = 1
            goto L_0x0694
        L_0x0692:
            r18 = 0
        L_0x0694:
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
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ all -> 0x06b9 }
            r9 = r81
            r7 = r82
            r8 = r83
            r29 = r84
            r34 = r88
            r26 = r90
            r1 = -5
            r6 = 0
            goto L_0x1397
        L_0x06b9:
            r0 = move-exception
            goto L_0x06be
        L_0x06bb:
            r0 = move-exception
            r14 = r7
            r13 = 0
        L_0x06be:
            r12 = r81
            r11 = r82
            r8 = r83
            r9 = r84
            goto L_0x0584
        L_0x06c8:
            r12 = r7
            r5 = -1
            r13 = 0
            if (r3 < 0) goto L_0x1357
            r20 = -2147483648(0xfffffffvar_, double:NaN)
            r7 = 1000(0x3e8, float:1.401E-42)
            r9 = r83
            int r7 = r7 / r9
            int r7 = r7 * 1000
            r22 = r14
            long r13 = (long) r7     // Catch:{ Exception -> 0x12c1, all -> 0x12b0 }
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x12c1, all -> 0x12b0 }
            r7.selectTrack(r3)     // Catch:{ Exception -> 0x12c1, all -> 0x12b0 }
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x12c1, all -> 0x12b0 }
            android.media.MediaFormat r7 = r7.getTrackFormat(r3)     // Catch:{ Exception -> 0x12c1, all -> 0x12b0 }
            r26 = 0
            int r23 = (r90 > r26 ? 1 : (r90 == r26 ? 0 : -1))
            if (r23 < 0) goto L_0x0708
            r23 = 1157234688(0x44fa0000, float:2000.0)
            int r23 = (r25 > r23 ? 1 : (r25 == r23 ? 0 : -1))
            if (r23 > 0) goto L_0x06f5
            r23 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x0703
        L_0x06f5:
            r23 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r23 = (r25 > r23 ? 1 : (r25 == r23 ? 0 : -1))
            if (r23 > 0) goto L_0x0700
            r23 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x0703
        L_0x0700:
            r23 = 1560000(0x17cdc0, float:2.186026E-39)
        L_0x0703:
            r5 = r23
            r26 = 0
            goto L_0x0716
        L_0x0708:
            if (r84 > 0) goto L_0x0712
            r26 = r90
            r23 = r3
            r5 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x0718
        L_0x0712:
            r5 = r84
            r26 = r90
        L_0x0716:
            r23 = r3
        L_0x0718:
            r3 = r85
            if (r3 <= 0) goto L_0x073f
            int r5 = java.lang.Math.min(r3, r5)     // Catch:{ Exception -> 0x072d, all -> 0x0721 }
            goto L_0x073f
        L_0x0721:
            r0 = move-exception
            r12 = r81
            r11 = r82
            r34 = r88
        L_0x0728:
            r2 = r0
            r8 = r9
            r1 = -5
            goto L_0x1353
        L_0x072d:
            r0 = move-exception
            r34 = r88
        L_0x0730:
            r2 = r0
            r8 = r9
            r61 = r23
        L_0x0734:
            r1 = -5
            r3 = 0
            r6 = 0
        L_0x0737:
            r28 = 0
            r30 = 0
            r56 = 0
            goto L_0x12d7
        L_0x073f:
            r28 = 0
            int r30 = (r26 > r28 ? 1 : (r26 == r28 ? 0 : -1))
            if (r30 < 0) goto L_0x0748
            r2 = r18
            goto L_0x074a
        L_0x0748:
            r2 = r26
        L_0x074a:
            int r26 = (r2 > r28 ? 1 : (r2 == r28 ? 0 : -1))
            if (r26 < 0) goto L_0x0782
            android.media.MediaExtractor r10 = r15.extractor     // Catch:{ Exception -> 0x076e, all -> 0x075f }
            r26 = r1
            r1 = 0
            r10.seekTo(r2, r1)     // Catch:{ Exception -> 0x076e, all -> 0x075f }
            r10 = r100
            r90 = r2
            r49 = r4
        L_0x075c:
            r2 = 0
            goto L_0x07b3
        L_0x075f:
            r0 = move-exception
            r12 = r81
            r11 = r82
            r34 = r88
            r26 = r2
            r8 = r9
            r1 = -5
            r7 = 0
            r2 = r0
            goto L_0x1354
        L_0x076e:
            r0 = move-exception
            r34 = r88
            r26 = r2
        L_0x0773:
            r8 = r9
            r61 = r23
            r1 = -5
            r3 = 0
            r6 = 0
            r28 = 0
            r30 = 0
            r56 = 0
        L_0x077f:
            r2 = r0
            goto L_0x12d7
        L_0x0782:
            r26 = r1
            r90 = r2
            r49 = r4
            r1 = 0
            r3 = r86
            int r10 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r10 <= 0) goto L_0x07a9
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
            r2 = 0
            r1.seekTo(r3, r2)     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
            r10 = r100
            goto L_0x075c
        L_0x0799:
            r0 = move-exception
            r12 = r81
            r11 = r82
            r34 = r88
            r26 = r90
            goto L_0x0728
        L_0x07a3:
            r0 = move-exception
            r34 = r88
            r26 = r90
            goto L_0x0730
        L_0x07a9:
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x1299, all -> 0x128b }
            r2 = 0
            r4 = 0
            r1.seekTo(r2, r4)     // Catch:{ Exception -> 0x1299, all -> 0x128b }
            r10 = r100
        L_0x07b3:
            if (r10 == 0) goto L_0x07cc
            r1 = 90
            r4 = r79
            if (r4 == r1) goto L_0x07c5
            r1 = 270(0x10e, float:3.78E-43)
            if (r4 != r1) goto L_0x07c0
            goto L_0x07c5
        L_0x07c0:
            int r1 = r10.transformWidth     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
            int r2 = r10.transformHeight     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
            goto L_0x07c9
        L_0x07c5:
            int r1 = r10.transformHeight     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
            int r2 = r10.transformWidth     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
        L_0x07c9:
            r3 = r2
            r2 = r1
            goto L_0x07d2
        L_0x07cc:
            r4 = r79
            r2 = r81
            r3 = r82
        L_0x07d2:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1299, all -> 0x128b }
            if (r1 == 0) goto L_0x07f2
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
            r1.<init>()     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
            java.lang.String r4 = "create encoder with w = "
            r1.append(r4)     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
            r1.append(r2)     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
            java.lang.String r4 = " h = "
            r1.append(r4)     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
            r1.append(r3)     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
        L_0x07f2:
            r4 = r49
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r4, r2, r3)     // Catch:{ Exception -> 0x1299, all -> 0x128b }
            r28 = r6
            java.lang.String r6 = "color-format"
            r39 = r8
            r8 = 2130708361(0x7var_, float:1.701803E38)
            r1.setInteger(r6, r8)     // Catch:{ Exception -> 0x1299, all -> 0x128b }
            java.lang.String r6 = "bitrate"
            r1.setInteger(r6, r5)     // Catch:{ Exception -> 0x1299, all -> 0x128b }
            java.lang.String r6 = "frame-rate"
            r1.setInteger(r6, r9)     // Catch:{ Exception -> 0x1299, all -> 0x128b }
            java.lang.String r6 = "i-frame-interval"
            r8 = 2
            r1.setInteger(r6, r8)     // Catch:{ Exception -> 0x1299, all -> 0x128b }
            int r8 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1299, all -> 0x128b }
            r6 = 23
            if (r8 >= r6) goto L_0x084a
            int r6 = java.lang.Math.min(r3, r2)     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
            r27 = r2
            r2 = 480(0x1e0, float:6.73E-43)
            if (r6 > r2) goto L_0x084c
            r2 = 921600(0xe1000, float:1.291437E-39)
            if (r5 <= r2) goto L_0x082a
            goto L_0x082b
        L_0x082a:
            r2 = r5
        L_0x082b:
            java.lang.String r5 = "bitrate"
            r1.setInteger(r5, r2)     // Catch:{ Exception -> 0x0842, all -> 0x0833 }
            r29 = r2
            goto L_0x084e
        L_0x0833:
            r0 = move-exception
            r12 = r81
            r11 = r82
            r34 = r88
            r26 = r90
            r8 = r9
            r1 = -5
            r7 = 0
            r9 = r2
            goto L_0x137c
        L_0x0842:
            r0 = move-exception
            r34 = r88
            r26 = r90
            r5 = r2
            goto L_0x0773
        L_0x084a:
            r27 = r2
        L_0x084c:
            r29 = r5
        L_0x084e:
            android.media.MediaCodec r6 = android.media.MediaCodec.createEncoderByType(r4)     // Catch:{ Exception -> 0x127c, all -> 0x126b }
            r2 = 1
            r5 = 0
            r6.configure(r1, r5, r5, r2)     // Catch:{ Exception -> 0x1259, all -> 0x126b }
            org.telegram.messenger.video.InputSurface r1 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x1259, all -> 0x126b }
            android.view.Surface r2 = r6.createInputSurface()     // Catch:{ Exception -> 0x1259, all -> 0x126b }
            r1.<init>(r2)     // Catch:{ Exception -> 0x1259, all -> 0x126b }
            r1.makeCurrent()     // Catch:{ Exception -> 0x1240, all -> 0x126b }
            r6.start()     // Catch:{ Exception -> 0x1240, all -> 0x126b }
            java.lang.String r2 = r7.getString(r11)     // Catch:{ Exception -> 0x1240, all -> 0x126b }
            android.media.MediaCodec r2 = android.media.MediaCodec.createDecoderByType(r2)     // Catch:{ Exception -> 0x1240, all -> 0x126b }
            org.telegram.messenger.video.OutputSurface r30 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x1228, all -> 0x126b }
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
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x1218, all -> 0x1211 }
            android.view.Surface r1 = r30.getSurface()     // Catch:{ Exception -> 0x11fb, all -> 0x1211 }
            r3 = r57
            r2 = r65
            r4 = 0
            r5 = 0
            r3.configure(r2, r1, r4, r5)     // Catch:{ Exception -> 0x11f0, all -> 0x1211 }
            r3.start()     // Catch:{ Exception -> 0x11f0, all -> 0x1211 }
            r1 = r66
            r2 = 21
            if (r1 >= r2) goto L_0x08ea
            java.nio.ByteBuffer[] r6 = r3.getInputBuffers()     // Catch:{ Exception -> 0x08dc, all -> 0x08d0 }
            java.nio.ByteBuffer[] r1 = r84.getOutputBuffers()     // Catch:{ Exception -> 0x08dc, all -> 0x08d0 }
            goto L_0x08ec
        L_0x08d0:
            r0 = move-exception
        L_0x08d1:
            r12 = r81
            r11 = r82
            r8 = r83
            r34 = r88
            r2 = r0
            goto L_0x1278
        L_0x08dc:
            r0 = move-exception
            r8 = r83
            r6 = r84
            r34 = r88
            r2 = r0
            r28 = r4
        L_0x08e6:
            r5 = r29
            goto L_0x12ae
        L_0x08ea:
            r1 = r4
            r6 = r1
        L_0x08ec:
            r2 = r55
            if (r2 < 0) goto L_0x09f5
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x09e2, all -> 0x09d5 }
            android.media.MediaFormat r5 = r5.getTrackFormat(r2)     // Catch:{ Exception -> 0x09e2, all -> 0x09d5 }
            java.lang.String r7 = r5.getString(r14)     // Catch:{ Exception -> 0x09e2, all -> 0x09d5 }
            java.lang.String r8 = "audio/mp4a-latm"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x09e2, all -> 0x09d5 }
            if (r7 != 0) goto L_0x0911
            java.lang.String r7 = r5.getString(r14)     // Catch:{ Exception -> 0x08dc, all -> 0x08d0 }
            java.lang.String r8 = "audio/mpeg"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x08dc, all -> 0x08d0 }
            if (r7 == 0) goto L_0x090f
            goto L_0x0911
        L_0x090f:
            r7 = 0
            goto L_0x0912
        L_0x0911:
            r7 = 1
        L_0x0912:
            java.lang.String r8 = r5.getString(r14)     // Catch:{ Exception -> 0x09e2, all -> 0x09d5 }
            java.lang.String r9 = "audio/unknown"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x09e2, all -> 0x09d5 }
            if (r8 == 0) goto L_0x091f
            r2 = -1
        L_0x091f:
            if (r2 < 0) goto L_0x09cc
            if (r7 == 0) goto L_0x097d
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x0971, all -> 0x096c }
            r9 = 1
            int r8 = r8.addTrack(r5, r9)     // Catch:{ Exception -> 0x0971, all -> 0x096c }
            android.media.MediaExtractor r10 = r15.extractor     // Catch:{ Exception -> 0x0971, all -> 0x096c }
            r10.selectTrack(r2)     // Catch:{ Exception -> 0x0971, all -> 0x096c }
            java.lang.String r10 = "max-input-size"
            int r5 = r5.getInteger(r10)     // Catch:{ Exception -> 0x0936, all -> 0x08d0 }
            goto L_0x093c
        L_0x0936:
            r0 = move-exception
            r5 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ Exception -> 0x0971, all -> 0x096c }
            r5 = 0
        L_0x093c:
            if (r5 > 0) goto L_0x0940
            r5 = 65536(0x10000, float:9.18355E-41)
        L_0x0940:
            java.nio.ByteBuffer r10 = java.nio.ByteBuffer.allocateDirect(r5)     // Catch:{ Exception -> 0x0971, all -> 0x096c }
            r91 = r5
            r90 = r10
            r4 = 0
            r9 = r86
            int r11 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r11 <= 0) goto L_0x0957
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x0996, all -> 0x08d0 }
            r14 = 0
            r11.seekTo(r9, r14)     // Catch:{ Exception -> 0x0996, all -> 0x08d0 }
            goto L_0x095d
        L_0x0957:
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x0996, all -> 0x08d0 }
            r14 = 0
            r11.seekTo(r4, r14)     // Catch:{ Exception -> 0x0996, all -> 0x08d0 }
        L_0x095d:
            r4 = r88
            r11 = r91
            r14 = 0
            r74 = r2
            r2 = r90
            r90 = r1
            r1 = r74
            goto L_0x0a01
        L_0x096c:
            r0 = move-exception
            r9 = r86
            goto L_0x08d1
        L_0x0971:
            r0 = move-exception
            r9 = r86
        L_0x0974:
            r8 = r83
            r6 = r84
            r34 = r88
            r2 = r0
            goto L_0x09ee
        L_0x097d:
            r9 = r86
            r8 = r5
            r4 = 0
            android.media.MediaExtractor r11 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x09ca, all -> 0x09c8 }
            r11.<init>()     // Catch:{ Exception -> 0x09ca, all -> 0x09c8 }
            r11.setDataSource(r12)     // Catch:{ Exception -> 0x09ca, all -> 0x09c8 }
            r11.selectTrack(r2)     // Catch:{ Exception -> 0x09ca, all -> 0x09c8 }
            int r14 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r14 <= 0) goto L_0x0998
            r14 = 0
            r11.seekTo(r9, r14)     // Catch:{ Exception -> 0x0996, all -> 0x08d0 }
            goto L_0x099c
        L_0x0996:
            r0 = move-exception
            goto L_0x0974
        L_0x0998:
            r14 = 0
            r11.seekTo(r4, r14)     // Catch:{ Exception -> 0x09ca, all -> 0x09c8 }
        L_0x099c:
            org.telegram.messenger.video.AudioRecoder r14 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x09ca, all -> 0x09c8 }
            r14.<init>(r8, r11, r2)     // Catch:{ Exception -> 0x09ca, all -> 0x09c8 }
            r14.startTime = r9     // Catch:{ Exception -> 0x09ba, all -> 0x09c8 }
            r4 = r88
            r14.endTime = r4     // Catch:{ Exception -> 0x09b8, all -> 0x09b6 }
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x09b8, all -> 0x09b6 }
            android.media.MediaFormat r11 = r14.format     // Catch:{ Exception -> 0x09b8, all -> 0x09b6 }
            r90 = r1
            r1 = 1
            int r8 = r8.addTrack(r11, r1)     // Catch:{ Exception -> 0x09b8, all -> 0x09b6 }
            r1 = r2
            r2 = 0
            r11 = 0
            goto L_0x0a01
        L_0x09b6:
            r0 = move-exception
            goto L_0x09da
        L_0x09b8:
            r0 = move-exception
            goto L_0x09bd
        L_0x09ba:
            r0 = move-exception
            r4 = r88
        L_0x09bd:
            r8 = r83
            r6 = r84
            r2 = r0
            r34 = r4
            r28 = r14
            goto L_0x08e6
        L_0x09c8:
            r0 = move-exception
            goto L_0x09d8
        L_0x09ca:
            r0 = move-exception
            goto L_0x09e5
        L_0x09cc:
            r9 = r86
            r4 = r88
            r90 = r1
            r1 = r2
            r2 = 0
            goto L_0x09fe
        L_0x09d5:
            r0 = move-exception
            r9 = r86
        L_0x09d8:
            r4 = r88
        L_0x09da:
            r12 = r81
            r11 = r82
            r8 = r83
            goto L_0x1275
        L_0x09e2:
            r0 = move-exception
            r9 = r86
        L_0x09e5:
            r4 = r88
            r8 = r83
            r6 = r84
            r2 = r0
            r34 = r4
        L_0x09ee:
            r5 = r29
            r1 = -5
            r28 = 0
            goto L_0x12d7
        L_0x09f5:
            r9 = r86
            r4 = r88
            r90 = r1
            r1 = r2
            r2 = 0
            r7 = 1
        L_0x09fe:
            r8 = -5
            r11 = 0
            r14 = 0
        L_0x0a01:
            if (r1 >= 0) goto L_0x0a06
            r23 = 1
            goto L_0x0a08
        L_0x0a06:
            r23 = 0
        L_0x0a08:
            r76.checkConversionCanceled()     // Catch:{ Exception -> 0x11de, all -> 0x11d9 }
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
        L_0x0a26:
            if (r28 == 0) goto L_0x0a41
            if (r7 != 0) goto L_0x0a2d
            if (r32 != 0) goto L_0x0a2d
            goto L_0x0a41
        L_0x0a2d:
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
            goto L_0x131a
        L_0x0a41:
            r76.checkConversionCanceled()     // Catch:{ Exception -> 0x11c4, all -> 0x11b2 }
            if (r7 != 0) goto L_0x0a71
            if (r14 == 0) goto L_0x0a71
            r89 = r11
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            boolean r11 = r14.step(r11, r8)     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            goto L_0x0a75
        L_0x0a51:
            r0 = move-exception
            r12 = r81
            r11 = r82
            r8 = r83
            r1 = r88
        L_0x0a5a:
            r2 = r0
            r34 = r4
        L_0x0a5d:
            r9 = r29
            goto L_0x058a
        L_0x0a61:
            r0 = move-exception
            r8 = r83
            r6 = r84
            r1 = r88
            r2 = r0
            r34 = r4
        L_0x0a6b:
            r28 = r14
        L_0x0a6d:
            r5 = r29
            goto L_0x12d7
        L_0x0a71:
            r89 = r11
            r11 = r32
        L_0x0a75:
            if (r21 != 0) goto L_0x0bd6
            r90 = r11
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x0bc4, all -> 0x0bb9 }
            int r11 = r11.getSampleTrackIndex()     // Catch:{ Exception -> 0x0bc4, all -> 0x0bb9 }
            r12 = r61
            if (r11 != r12) goto L_0x0ae0
            r61 = r12
            r46 = r13
            r12 = 2500(0x9c4, double:1.235E-320)
            int r11 = r3.dequeueInputBuffer(r12)     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            if (r11 < 0) goto L_0x0acf
            int r12 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            r13 = 21
            if (r12 >= r13) goto L_0x0a98
            r12 = r6[r11]     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            goto L_0x0a9c
        L_0x0a98:
            java.nio.ByteBuffer r12 = r3.getInputBuffer(r11)     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
        L_0x0a9c:
            android.media.MediaExtractor r13 = r15.extractor     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            r91 = r6
            r6 = 0
            int r35 = r13.readSampleData(r12, r6)     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            if (r35 >= 0) goto L_0x0ab8
            r34 = 0
            r35 = 0
            r36 = 0
            r38 = 4
            r32 = r3
            r33 = r11
            r32.queueInputBuffer(r33, r34, r35, r36, r38)     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            r6 = 1
            goto L_0x0ad3
        L_0x0ab8:
            r34 = 0
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            long r36 = r6.getSampleTime()     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            r38 = 0
            r32 = r3
            r33 = r11
            r32.queueInputBuffer(r33, r34, r35, r36, r38)     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            r6.advance()     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            goto L_0x0ad1
        L_0x0acf:
            r91 = r6
        L_0x0ad1:
            r6 = r21
        L_0x0ad3:
            r70 = r4
            r21 = r6
            r53 = r7
            r13 = r22
            r6 = 0
            r22 = r1
            goto L_0x0b9c
        L_0x0ae0:
            r91 = r6
            r61 = r12
            r46 = r13
            if (r7 == 0) goto L_0x0b8e
            r6 = -1
            if (r1 == r6) goto L_0x0b8e
            if (r11 != r1) goto L_0x0b8e
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0bc4, all -> 0x0bb9 }
            r12 = 28
            if (r11 < r12) goto L_0x0b09
            android.media.MediaExtractor r12 = r15.extractor     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            long r12 = r12.getSampleSize()     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            r53 = r7
            long r6 = (long) r2     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            int r32 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r32 <= 0) goto L_0x0b0b
            r6 = 1024(0x400, double:5.06E-321)
            long r12 = r12 + r6
            int r2 = (int) r12     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            java.nio.ByteBuffer r20 = java.nio.ByteBuffer.allocateDirect(r2)     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            goto L_0x0b0b
        L_0x0b09:
            r53 = r7
        L_0x0b0b:
            r6 = r20
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x0bc4, all -> 0x0bb9 }
            r12 = 0
            int r7 = r7.readSampleData(r6, r12)     // Catch:{ Exception -> 0x0bc4, all -> 0x0bb9 }
            r13 = r22
            r13.size = r7     // Catch:{ Exception -> 0x0bc4, all -> 0x0bb9 }
            r7 = 21
            if (r11 >= r7) goto L_0x0b24
            r6.position(r12)     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            int r7 = r13.size     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            r6.limit(r7)     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
        L_0x0b24:
            int r7 = r13.size     // Catch:{ Exception -> 0x0bc4, all -> 0x0bb9 }
            if (r7 < 0) goto L_0x0b36
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            long r11 = r7.getSampleTime()     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            r13.presentationTimeUs = r11     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            r7.advance()     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            goto L_0x0b3b
        L_0x0b36:
            r7 = 0
            r13.size = r7     // Catch:{ Exception -> 0x0bc4, all -> 0x0bb9 }
            r21 = 1
        L_0x0b3b:
            int r7 = r13.size     // Catch:{ Exception -> 0x0bc4, all -> 0x0bb9 }
            if (r7 <= 0) goto L_0x0b83
            r11 = 0
            int r7 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1))
            if (r7 < 0) goto L_0x0b4b
            long r11 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0a61, all -> 0x0a51 }
            int r7 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r7 >= 0) goto L_0x0b83
        L_0x0b4b:
            r7 = 0
            r13.offset = r7     // Catch:{ Exception -> 0x0bc4, all -> 0x0bb9 }
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x0bc4, all -> 0x0bb9 }
            int r11 = r11.getSampleFlags()     // Catch:{ Exception -> 0x0bc4, all -> 0x0bb9 }
            r13.flags = r11     // Catch:{ Exception -> 0x0bc4, all -> 0x0bb9 }
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x0bc4, all -> 0x0bb9 }
            long r11 = r11.writeSampleData(r8, r6, r13, r7)     // Catch:{ Exception -> 0x0bc4, all -> 0x0bb9 }
            r32 = 0
            int r7 = (r11 > r32 ? 1 : (r11 == r32 ? 0 : -1))
            if (r7 == 0) goto L_0x0b83
            org.telegram.messenger.MediaController$VideoConvertorListener r7 = r15.callback     // Catch:{ Exception -> 0x0bc4, all -> 0x0bb9 }
            if (r7 == 0) goto L_0x0b83
            r22 = r1
            r20 = r2
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0bc4, all -> 0x0bb9 }
            long r32 = r1 - r9
            int r34 = (r32 > r48 ? 1 : (r32 == r48 ? 0 : -1))
            if (r34 <= 0) goto L_0x0b74
            long r48 = r1 - r9
        L_0x0b74:
            r1 = r48
            r70 = r4
            float r4 = (float) r1
            float r4 = r4 / r24
            float r4 = r4 / r25
            r7.didWriteData(r11, r4)     // Catch:{ Exception -> 0x0bb7, all -> 0x0bb5 }
            r48 = r1
            goto L_0x0b89
        L_0x0b83:
            r22 = r1
            r20 = r2
            r70 = r4
        L_0x0b89:
            r2 = r20
            r20 = r6
            goto L_0x0b9b
        L_0x0b8e:
            r70 = r4
            r53 = r7
            r13 = r22
            r22 = r1
            r1 = -1
            if (r11 != r1) goto L_0x0b9b
            r6 = 1
            goto L_0x0b9c
        L_0x0b9b:
            r6 = 0
        L_0x0b9c:
            if (r6 == 0) goto L_0x0be4
            r4 = 2500(0x9c4, double:1.235E-320)
            int r33 = r3.dequeueInputBuffer(r4)     // Catch:{ Exception -> 0x0bb7, all -> 0x0bb5 }
            if (r33 < 0) goto L_0x0be4
            r34 = 0
            r35 = 0
            r36 = 0
            r38 = 4
            r32 = r3
            r32.queueInputBuffer(r33, r34, r35, r36, r38)     // Catch:{ Exception -> 0x0bb7, all -> 0x0bb5 }
            r6 = 1
            goto L_0x0be6
        L_0x0bb5:
            r0 = move-exception
            goto L_0x0bbc
        L_0x0bb7:
            r0 = move-exception
            goto L_0x0bc7
        L_0x0bb9:
            r0 = move-exception
            r70 = r4
        L_0x0bbc:
            r12 = r81
            r11 = r82
            r8 = r83
            goto L_0x11bb
        L_0x0bc4:
            r0 = move-exception
            r70 = r4
        L_0x0bc7:
            r8 = r83
            r6 = r84
            r1 = r88
            r2 = r0
            r28 = r14
            r5 = r29
            r34 = r70
            goto L_0x12d7
        L_0x0bd6:
            r70 = r4
            r91 = r6
            r53 = r7
            r90 = r11
            r46 = r13
            r13 = r22
            r22 = r1
        L_0x0be4:
            r6 = r21
        L_0x0be6:
            r1 = r31 ^ 1
            r11 = r89
            r7 = r1
            r21 = r6
            r72 = r59
            r4 = r70
            r6 = 1
            r1 = r88
        L_0x0bf4:
            if (r7 != 0) goto L_0x0c0d
            if (r6 == 0) goto L_0x0bf9
            goto L_0x0c0d
        L_0x0bf9:
            r12 = r77
            r32 = r90
            r6 = r91
            r88 = r1
            r1 = r22
            r7 = r53
            r59 = r72
            r22 = r13
            r13 = r46
            goto L_0x0a26
        L_0x0c0d:
            r76.checkConversionCanceled()     // Catch:{ Exception -> 0x11a1, all -> 0x1195 }
            if (r95 == 0) goto L_0x0c1d
            r32 = 22000(0x55f0, double:1.08694E-319)
            r12 = r84
            r88 = r6
            r89 = r7
            r6 = r32
            goto L_0x0CLASSNAME
        L_0x0c1d:
            r12 = r84
            r88 = r6
            r89 = r7
            r6 = 2500(0x9c4, double:1.235E-320)
        L_0x0CLASSNAME:
            int r6 = r12.dequeueOutputBuffer(r13, r6)     // Catch:{ Exception -> 0x1191, all -> 0x1195 }
            r7 = -1
            if (r6 != r7) goto L_0x0c4b
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
            goto L_0x0e8e
        L_0x0c4b:
            r7 = -3
            if (r6 != r7) goto L_0x0CLASSNAME
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c7a, all -> 0x0CLASSNAME }
            r84 = r2
            r2 = 21
            if (r7 >= r2) goto L_0x0c5a
            java.nio.ByteBuffer[] r11 = r12.getOutputBuffers()     // Catch:{ Exception -> 0x0c7a, all -> 0x0CLASSNAME }
        L_0x0c5a:
            r57 = r3
            r34 = r4
            r5 = r6
            r32 = r8
            r7 = r46
            r2 = r58
            r3 = r62
            r4 = r63
            r8 = r67
        L_0x0c6b:
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
            goto L_0x0a5a
        L_0x0c7a:
            r0 = move-exception
        L_0x0c7b:
            r8 = r83
            r2 = r0
            r34 = r4
            r6 = r12
            goto L_0x0a6b
        L_0x0CLASSNAME:
            r84 = r2
            r2 = -2
            if (r6 != r2) goto L_0x0d0b
            android.media.MediaFormat r2 = r12.getOutputFormat()     // Catch:{ Exception -> 0x0c7a, all -> 0x0CLASSNAME }
            r7 = -5
            if (r1 != r7) goto L_0x0cf8
            if (r2 == 0) goto L_0x0cf8
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ Exception -> 0x0c7a, all -> 0x0CLASSNAME }
            r32 = r8
            r8 = 0
            int r1 = r7.addTrack(r2, r8)     // Catch:{ Exception -> 0x0c7a, all -> 0x0CLASSNAME }
            r7 = r64
            boolean r8 = r2.containsKey(r7)     // Catch:{ Exception -> 0x0cf4, all -> 0x0cef }
            if (r8 == 0) goto L_0x0ce4
            int r8 = r2.getInteger(r7)     // Catch:{ Exception -> 0x0cf4, all -> 0x0cef }
            r33 = r1
            r1 = 1
            if (r8 != r1) goto L_0x0ce6
            r8 = r67
            java.nio.ByteBuffer r1 = r2.getByteBuffer(r8)     // Catch:{ Exception -> 0x0cd5, all -> 0x0cc5 }
            r64 = r7
            r7 = r46
            java.nio.ByteBuffer r2 = r2.getByteBuffer(r7)     // Catch:{ Exception -> 0x0cd5, all -> 0x0cc5 }
            int r1 = r1.limit()     // Catch:{ Exception -> 0x0cd5, all -> 0x0cc5 }
            int r2 = r2.limit()     // Catch:{ Exception -> 0x0cd5, all -> 0x0cc5 }
            int r1 = r1 + r2
            r41 = r1
            goto L_0x0cec
        L_0x0cc5:
            r0 = move-exception
            r12 = r81
            r11 = r82
            r8 = r83
            r2 = r0
            r34 = r4
            r9 = r29
            r1 = r33
            goto L_0x058a
        L_0x0cd5:
            r0 = move-exception
            r8 = r83
            r2 = r0
            r34 = r4
            r6 = r12
            r28 = r14
            r5 = r29
            r1 = r33
            goto L_0x12d7
        L_0x0ce4:
            r33 = r1
        L_0x0ce6:
            r64 = r7
            r7 = r46
            r8 = r67
        L_0x0cec:
            r1 = r33
            goto L_0x0cfe
        L_0x0cef:
            r0 = move-exception
            r33 = r1
            goto L_0x0CLASSNAME
        L_0x0cf4:
            r0 = move-exception
            r33 = r1
            goto L_0x0c7b
        L_0x0cf8:
            r32 = r8
            r7 = r46
            r8 = r67
        L_0x0cfe:
            r57 = r3
            r34 = r4
            r5 = r6
            r2 = r58
            r3 = r62
            r4 = r63
            goto L_0x0c6b
        L_0x0d0b:
            r32 = r8
            r7 = r46
            r8 = r67
            if (r6 < 0) goto L_0x1169
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1191, all -> 0x1195 }
            r28 = r14
            r14 = 21
            if (r2 >= r14) goto L_0x0d26
            r2 = r11[r6]     // Catch:{ Exception -> 0x0d1e, all -> 0x0CLASSNAME }
            goto L_0x0d2a
        L_0x0d1e:
            r0 = move-exception
            r8 = r83
            r2 = r0
            r34 = r4
            goto L_0x10eb
        L_0x0d26:
            java.nio.ByteBuffer r2 = r12.getOutputBuffer(r6)     // Catch:{ Exception -> 0x1162, all -> 0x1195 }
        L_0x0d2a:
            if (r2 == 0) goto L_0x113e
            int r14 = r13.size     // Catch:{ Exception -> 0x1134, all -> 0x1195 }
            r33 = r11
            r11 = 1
            if (r14 <= r11) goto L_0x0e70
            int r11 = r13.flags     // Catch:{ Exception -> 0x0e67, all -> 0x0e5c }
            r34 = r11 & 2
            if (r34 != 0) goto L_0x0dd7
            if (r41 == 0) goto L_0x0d4f
            r34 = r11 & 1
            if (r34 == 0) goto L_0x0d4f
            r34 = r4
            int r4 = r13.offset     // Catch:{ Exception -> 0x0d4c, all -> 0x0e4f }
            int r4 = r4 + r41
            r13.offset = r4     // Catch:{ Exception -> 0x0d4c, all -> 0x0e4f }
            int r14 = r14 - r41
            r13.size = r14     // Catch:{ Exception -> 0x0d4c, all -> 0x0e4f }
            goto L_0x0d51
        L_0x0d4c:
            r0 = move-exception
            goto L_0x0e6c
        L_0x0d4f:
            r34 = r4
        L_0x0d51:
            if (r23 == 0) goto L_0x0da0
            r4 = r11 & 1
            if (r4 == 0) goto L_0x0da0
            int r4 = r13.size     // Catch:{ Exception -> 0x0d4c, all -> 0x0e4f }
            r5 = 100
            if (r4 <= r5) goto L_0x0d9e
            int r4 = r13.offset     // Catch:{ Exception -> 0x0d4c, all -> 0x0e4f }
            r2.position(r4)     // Catch:{ Exception -> 0x0d4c, all -> 0x0e4f }
            byte[] r4 = new byte[r5]     // Catch:{ Exception -> 0x0d4c, all -> 0x0e4f }
            r2.get(r4)     // Catch:{ Exception -> 0x0d4c, all -> 0x0e4f }
            r11 = 0
            r14 = 0
        L_0x0d69:
            r5 = 96
            if (r11 >= r5) goto L_0x0d9e
            byte r5 = r4[r11]     // Catch:{ Exception -> 0x0d4c, all -> 0x0e4f }
            if (r5 != 0) goto L_0x0d95
            int r5 = r11 + 1
            byte r5 = r4[r5]     // Catch:{ Exception -> 0x0d4c, all -> 0x0e4f }
            if (r5 != 0) goto L_0x0d95
            int r5 = r11 + 2
            byte r5 = r4[r5]     // Catch:{ Exception -> 0x0d4c, all -> 0x0e4f }
            if (r5 != 0) goto L_0x0d95
            int r5 = r11 + 3
            byte r5 = r4[r5]     // Catch:{ Exception -> 0x0d4c, all -> 0x0e4f }
            r23 = r4
            r4 = 1
            if (r5 != r4) goto L_0x0d97
            int r14 = r14 + 1
            if (r14 <= r4) goto L_0x0d97
            int r4 = r13.offset     // Catch:{ Exception -> 0x0d4c, all -> 0x0e4f }
            int r4 = r4 + r11
            r13.offset = r4     // Catch:{ Exception -> 0x0d4c, all -> 0x0e4f }
            int r4 = r13.size     // Catch:{ Exception -> 0x0d4c, all -> 0x0e4f }
            int r4 = r4 - r11
            r13.size = r4     // Catch:{ Exception -> 0x0d4c, all -> 0x0e4f }
            goto L_0x0d9e
        L_0x0d95:
            r23 = r4
        L_0x0d97:
            int r11 = r11 + 1
            r4 = r23
            r5 = 100
            goto L_0x0d69
        L_0x0d9e:
            r23 = 0
        L_0x0da0:
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x0dd2, all -> 0x0e4f }
            r57 = r3
            r5 = 1
            long r2 = r4.writeSampleData(r1, r2, r13, r5)     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            r4 = 0
            int r11 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r11 == 0) goto L_0x0e74
            org.telegram.messenger.MediaController$VideoConvertorListener r4 = r15.callback     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            if (r4 == 0) goto L_0x0e74
            r11 = r6
            long r5 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            long r36 = r5 - r9
            int r14 = (r36 > r48 ? 1 : (r36 == r48 ? 0 : -1))
            if (r14 <= 0) goto L_0x0dbe
            long r48 = r5 - r9
        L_0x0dbe:
            r5 = r48
            float r14 = (float) r5     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            float r14 = r14 / r24
            float r14 = r14 / r25
            r4.didWriteData(r2, r14)     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            r48 = r5
        L_0x0dca:
            r2 = r58
            r3 = r62
            r4 = r63
            goto L_0x0e77
        L_0x0dd2:
            r0 = move-exception
            r57 = r3
            goto L_0x0e6c
        L_0x0dd7:
            r57 = r3
            r34 = r4
            r11 = r6
            r3 = -5
            if (r1 != r3) goto L_0x0dca
            byte[] r3 = new byte[r14]     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            int r4 = r13.offset     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            int r4 = r4 + r14
            r2.limit(r4)     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            int r4 = r13.offset     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            r2.position(r4)     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            r2.get(r3)     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            int r2 = r13.size     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            r6 = 1
            int r2 = r2 - r6
        L_0x0df3:
            if (r2 < 0) goto L_0x0e31
            r4 = 3
            if (r2 <= r4) goto L_0x0e31
            byte r5 = r3[r2]     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            if (r5 != r6) goto L_0x0e2d
            int r5 = r2 + -1
            byte r5 = r3[r5]     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            if (r5 != 0) goto L_0x0e2d
            int r5 = r2 + -2
            byte r5 = r3[r5]     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            if (r5 != 0) goto L_0x0e2d
            int r5 = r2 + -3
            byte r14 = r3[r5]     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            if (r14 != 0) goto L_0x0e2d
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r5)     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            int r14 = r13.size     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            int r14 = r14 - r5
            java.nio.ByteBuffer r14 = java.nio.ByteBuffer.allocate(r14)     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            r4 = 0
            java.nio.ByteBuffer r6 = r2.put(r3, r4, r5)     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            r6.position(r4)     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            int r6 = r13.size     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            int r6 = r6 - r5
            java.nio.ByteBuffer r3 = r14.put(r3, r5, r6)     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            r3.position(r4)     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            r6 = r2
            goto L_0x0e33
        L_0x0e2d:
            int r2 = r2 + -1
            r6 = 1
            goto L_0x0df3
        L_0x0e31:
            r6 = 0
            r14 = 0
        L_0x0e33:
            r2 = r58
            r3 = r62
            r4 = r63
            android.media.MediaFormat r5 = android.media.MediaFormat.createVideoFormat(r4, r2, r3)     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            if (r6 == 0) goto L_0x0e47
            if (r14 == 0) goto L_0x0e47
            r5.setByteBuffer(r8, r6)     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            r5.setByteBuffer(r7, r14)     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
        L_0x0e47:
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            r14 = 0
            int r1 = r6.addTrack(r5, r14)     // Catch:{ Exception -> 0x0e51, all -> 0x0e4f }
            goto L_0x0e77
        L_0x0e4f:
            r0 = move-exception
            goto L_0x0e5f
        L_0x0e51:
            r0 = move-exception
            r8 = r83
            r2 = r0
            r6 = r12
            r5 = r29
            r3 = r57
            goto L_0x12d7
        L_0x0e5c:
            r0 = move-exception
            r34 = r4
        L_0x0e5f:
            r12 = r81
            r11 = r82
            r8 = r83
            goto L_0x119e
        L_0x0e67:
            r0 = move-exception
            r57 = r3
            r34 = r4
        L_0x0e6c:
            r8 = r83
            goto L_0x113c
        L_0x0e70:
            r57 = r3
            r34 = r4
        L_0x0e74:
            r11 = r6
            goto L_0x0dca
        L_0x0e77:
            int r5 = r13.flags     // Catch:{ Exception -> 0x112b, all -> 0x1125 }
            r5 = r5 & 4
            if (r5 == 0) goto L_0x0e80
            r5 = r11
            r6 = 1
            goto L_0x0e82
        L_0x0e80:
            r5 = r11
            r6 = 0
        L_0x0e82:
            r11 = 0
            r12.releaseOutputBuffer(r5, r11)     // Catch:{ Exception -> 0x112b, all -> 0x1125 }
            r14 = r6
            r11 = r33
            r6 = r88
            r88 = r1
            r1 = -1
        L_0x0e8e:
            if (r5 == r1) goto L_0x0eb0
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
        L_0x0ea8:
            r74 = r28
            r28 = r14
            r14 = r74
            goto L_0x0bf4
        L_0x0eb0:
            if (r31 != 0) goto L_0x10ee
            r58 = r2
            r62 = r3
            r5 = r57
            r1 = 2500(0x9c4, double:1.235E-320)
            int r3 = r5.dequeueOutputBuffer(r13, r1)     // Catch:{ Exception -> 0x10e2, all -> 0x10d7 }
            r1 = -1
            if (r3 != r1) goto L_0x0ed2
            r63 = r4
            r52 = r6
            r37 = r7
            r67 = r8
            r9 = r56
            r1 = 0
            r7 = 0
            r8 = r83
            goto L_0x1108
        L_0x0ed2:
            r2 = -3
            if (r3 != r2) goto L_0x0ee1
        L_0x0ed5:
            r63 = r4
            r52 = r6
            r37 = r7
            r67 = r8
            r9 = r56
            goto L_0x10fe
        L_0x0ee1:
            r2 = -2
            if (r3 != r2) goto L_0x0f0f
            android.media.MediaFormat r2 = r5.getOutputFormat()     // Catch:{ Exception -> 0x0f0a, all -> 0x0var_ }
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0f0a, all -> 0x0var_ }
            if (r3 == 0) goto L_0x0ed5
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0f0a, all -> 0x0var_ }
            r3.<init>()     // Catch:{ Exception -> 0x0f0a, all -> 0x0var_ }
            java.lang.String r1 = "newFormat = "
            r3.append(r1)     // Catch:{ Exception -> 0x0f0a, all -> 0x0var_ }
            r3.append(r2)     // Catch:{ Exception -> 0x0f0a, all -> 0x0var_ }
            java.lang.String r1 = r3.toString()     // Catch:{ Exception -> 0x0f0a, all -> 0x0var_ }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0f0a, all -> 0x0var_ }
            goto L_0x0ed5
        L_0x0var_:
            r0 = move-exception
            r12 = r81
            r11 = r82
            r8 = r83
            goto L_0x10de
        L_0x0f0a:
            r0 = move-exception
            r8 = r83
            goto L_0x10e7
        L_0x0f0f:
            if (r3 < 0) goto L_0x10b2
            int r1 = r13.size     // Catch:{ Exception -> 0x10e2, all -> 0x10d7 }
            if (r1 == 0) goto L_0x0var_
            r36 = 1
            goto L_0x0f1a
        L_0x0var_:
            r36 = 0
        L_0x0f1a:
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x10e2, all -> 0x10d7 }
            r43 = 0
            int r37 = (r34 > r43 ? 1 : (r34 == r43 ? 0 : -1))
            if (r37 <= 0) goto L_0x0var_
            int r37 = (r1 > r34 ? 1 : (r1 == r34 ? 0 : -1))
            if (r37 < 0) goto L_0x0var_
            r63 = r4
            int r4 = r13.flags     // Catch:{ Exception -> 0x0f0a, all -> 0x0var_ }
            r4 = r4 | 4
            r13.flags = r4     // Catch:{ Exception -> 0x0f0a, all -> 0x0var_ }
            r21 = 1
            r31 = 1
            r36 = 0
            goto L_0x0var_
        L_0x0var_:
            r63 = r4
        L_0x0var_:
            r43 = 0
            int r4 = (r26 > r43 ? 1 : (r26 == r43 ? 0 : -1))
            if (r4 < 0) goto L_0x0fba
            int r4 = r13.flags     // Catch:{ Exception -> 0x0f0a, all -> 0x0fb4 }
            r4 = r4 & 4
            if (r4 == 0) goto L_0x0fba
            long r54 = r26 - r9
            long r54 = java.lang.Math.abs(r54)     // Catch:{ Exception -> 0x0f0a, all -> 0x0fb4 }
            r4 = 1000000(0xvar_, float:1.401298E-39)
            r67 = r8
            r8 = r83
            int r4 = r4 / r8
            r52 = r6
            r37 = r7
            long r6 = (long) r4
            int r4 = (r54 > r6 ? 1 : (r54 == r6 ? 0 : -1))
            if (r4 <= 0) goto L_0x0faa
            r6 = 0
            int r4 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r4 <= 0) goto L_0x0f6a
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x0fb1, all -> 0x10cd }
            r6 = 0
            r4.seekTo(r9, r6)     // Catch:{ Exception -> 0x0fb1, all -> 0x10cd }
            r6 = r72
            r9 = 0
            goto L_0x0var_
        L_0x0f6a:
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x0fb1, all -> 0x0faf }
            r6 = 0
            r9 = 0
            r4.seekTo(r6, r9)     // Catch:{ Exception -> 0x0fb1, all -> 0x10cd }
            r6 = r72
        L_0x0var_:
            long r50 = r6 + r39
            int r4 = r13.flags     // Catch:{ Exception -> 0x0f9c, all -> 0x0f8b }
            r10 = -5
            r4 = r4 & r10
            r13.flags = r4     // Catch:{ Exception -> 0x0f9c, all -> 0x0f8b }
            r5.flush()     // Catch:{ Exception -> 0x0f9c, all -> 0x0f8b }
            r34 = r18
            r4 = 1
            r21 = 0
            r31 = 0
            r36 = 0
            r43 = 0
            goto L_0x0fcf
        L_0x0f8b:
            r0 = move-exception
            r12 = r81
            r11 = r82
            r1 = r88
            r2 = r0
            r34 = r26
            r9 = r29
            r7 = 0
            r26 = r18
            goto L_0x13d7
        L_0x0f9c:
            r0 = move-exception
            r1 = r88
            r2 = r0
            r3 = r5
            r6 = r12
            r34 = r26
            r5 = r29
            r26 = r18
            goto L_0x12d7
        L_0x0faa:
            r6 = r72
            r9 = 0
            r10 = -5
            goto L_0x0fc6
        L_0x0faf:
            r0 = move-exception
            goto L_0x0fb7
        L_0x0fb1:
            r0 = move-exception
            goto L_0x10e7
        L_0x0fb4:
            r0 = move-exception
            r8 = r83
        L_0x0fb7:
            r9 = 0
            goto L_0x10da
        L_0x0fba:
            r52 = r6
            r37 = r7
            r67 = r8
            r6 = r72
            r9 = 0
            r10 = -5
            r8 = r83
        L_0x0fc6:
            r4 = 0
            r43 = 0
            r74 = r26
            r26 = r34
            r34 = r74
        L_0x0fcf:
            int r38 = (r34 > r43 ? 1 : (r34 == r43 ? 0 : -1))
            if (r38 < 0) goto L_0x0fd6
            r9 = r34
            goto L_0x0fd8
        L_0x0fd6:
            r9 = r86
        L_0x0fd8:
            int r54 = (r9 > r43 ? 1 : (r9 == r43 ? 0 : -1))
            if (r54 <= 0) goto L_0x1019
            int r54 = (r68 > r18 ? 1 : (r68 == r18 ? 0 : -1))
            if (r54 != 0) goto L_0x1019
            int r54 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r54 >= 0) goto L_0x1008
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1016, all -> 0x108d }
            if (r1 == 0) goto L_0x1006
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1016, all -> 0x108d }
            r1.<init>()     // Catch:{ Exception -> 0x1016, all -> 0x108d }
            java.lang.String r2 = "drop frame startTime = "
            r1.append(r2)     // Catch:{ Exception -> 0x1016, all -> 0x108d }
            r1.append(r9)     // Catch:{ Exception -> 0x1016, all -> 0x108d }
            java.lang.String r2 = " present time = "
            r1.append(r2)     // Catch:{ Exception -> 0x1016, all -> 0x108d }
            long r9 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1016, all -> 0x108d }
            r1.append(r9)     // Catch:{ Exception -> 0x1016, all -> 0x108d }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x1016, all -> 0x108d }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x1016, all -> 0x108d }
        L_0x1006:
            r1 = 0
            goto L_0x101b
        L_0x1008:
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1016, all -> 0x108d }
            r9 = -2147483648(0xfffffffvar_, double:NaN)
            int r54 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r54 == 0) goto L_0x1013
            long r50 = r50 - r1
        L_0x1013:
            r68 = r1
            goto L_0x1019
        L_0x1016:
            r0 = move-exception
            goto L_0x10a3
        L_0x1019:
            r1 = r36
        L_0x101b:
            if (r4 == 0) goto L_0x1020
            r68 = r18
            goto L_0x1033
        L_0x1020:
            int r2 = (r34 > r18 ? 1 : (r34 == r18 ? 0 : -1))
            if (r2 != 0) goto L_0x1030
            r9 = 0
            int r2 = (r50 > r9 ? 1 : (r50 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x1030
            long r9 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1016, all -> 0x108d }
            long r9 = r9 + r50
            r13.presentationTimeUs = r9     // Catch:{ Exception -> 0x1016, all -> 0x108d }
        L_0x1030:
            r5.releaseOutputBuffer(r3, r1)     // Catch:{ Exception -> 0x10a0, all -> 0x108d }
        L_0x1033:
            if (r1 == 0) goto L_0x1066
            r1 = 0
            int r3 = (r34 > r1 ? 1 : (r34 == r1 ? 0 : -1))
            if (r3 < 0) goto L_0x1042
            long r3 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1016, all -> 0x108d }
            long r72 = java.lang.Math.max(r6, r3)     // Catch:{ Exception -> 0x1016, all -> 0x108d }
            goto L_0x1044
        L_0x1042:
            r72 = r6
        L_0x1044:
            r30.awaitNewImage()     // Catch:{ Exception -> 0x1049, all -> 0x108d }
            r6 = 0
            goto L_0x104f
        L_0x1049:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)     // Catch:{ Exception -> 0x10a0, all -> 0x108d }
            r6 = 1
        L_0x104f:
            if (r6 != 0) goto L_0x1063
            r30.drawImage()     // Catch:{ Exception -> 0x10a0, all -> 0x108d }
            long r3 = r13.presentationTimeUs     // Catch:{ Exception -> 0x10a0, all -> 0x108d }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r3 = r3 * r6
            r9 = r56
            r9.setPresentationTime(r3)     // Catch:{ Exception -> 0x1085, all -> 0x108d }
            r9.swapBuffers()     // Catch:{ Exception -> 0x1085, all -> 0x108d }
            goto L_0x106c
        L_0x1063:
            r9 = r56
            goto L_0x106c
        L_0x1066:
            r9 = r56
            r1 = 0
            r72 = r6
        L_0x106c:
            int r3 = r13.flags     // Catch:{ Exception -> 0x1085, all -> 0x108d }
            r3 = r3 & 4
            if (r3 == 0) goto L_0x1081
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1085, all -> 0x108d }
            if (r3 == 0) goto L_0x107b
            java.lang.String r3 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x1085, all -> 0x108d }
        L_0x107b:
            r12.signalEndOfInputStream()     // Catch:{ Exception -> 0x1085, all -> 0x108d }
            r7 = 0
            goto L_0x110e
        L_0x1081:
            r7 = r89
            goto L_0x110e
        L_0x1085:
            r0 = move-exception
            r1 = r88
            r2 = r0
            r3 = r5
            r56 = r9
            goto L_0x10a7
        L_0x108d:
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
            goto L_0x13d7
        L_0x10a0:
            r0 = move-exception
            r9 = r56
        L_0x10a3:
            r1 = r88
            r2 = r0
            r3 = r5
        L_0x10a7:
            r6 = r12
            r5 = r29
            r74 = r26
            r26 = r34
            r34 = r74
            goto L_0x12d7
        L_0x10b2:
            r8 = r83
            r9 = r56
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x10cf, all -> 0x10cd }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x10cf, all -> 0x10cd }
            r2.<init>()     // Catch:{ Exception -> 0x10cf, all -> 0x10cd }
            java.lang.String r4 = "unexpected result from decoder.dequeueOutputBuffer: "
            r2.append(r4)     // Catch:{ Exception -> 0x10cf, all -> 0x10cd }
            r2.append(r3)     // Catch:{ Exception -> 0x10cf, all -> 0x10cd }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x10cf, all -> 0x10cd }
            r1.<init>(r2)     // Catch:{ Exception -> 0x10cf, all -> 0x10cd }
            throw r1     // Catch:{ Exception -> 0x10cf, all -> 0x10cd }
        L_0x10cd:
            r0 = move-exception
            goto L_0x10da
        L_0x10cf:
            r0 = move-exception
            r1 = r88
            r2 = r0
            r3 = r5
            r56 = r9
            goto L_0x10eb
        L_0x10d7:
            r0 = move-exception
            r8 = r83
        L_0x10da:
            r12 = r81
            r11 = r82
        L_0x10de:
            r1 = r88
            goto L_0x119e
        L_0x10e2:
            r0 = move-exception
            r8 = r83
            r9 = r56
        L_0x10e7:
            r1 = r88
        L_0x10e9:
            r2 = r0
            r3 = r5
        L_0x10eb:
            r6 = r12
            goto L_0x0a6d
        L_0x10ee:
            r58 = r2
            r62 = r3
            r63 = r4
            r52 = r6
            r37 = r7
            r67 = r8
            r9 = r56
            r5 = r57
        L_0x10fe:
            r6 = r72
            r1 = 0
            r8 = r83
            r72 = r6
            r7 = r89
        L_0x1108:
            r74 = r26
            r26 = r34
            r34 = r74
        L_0x110e:
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
            goto L_0x0ea8
        L_0x1125:
            r0 = move-exception
            r8 = r83
            r3 = r1
            goto L_0x119a
        L_0x112b:
            r0 = move-exception
            r8 = r83
            r3 = r1
            r9 = r56
            r5 = r57
            goto L_0x10e9
        L_0x1134:
            r0 = move-exception
            r8 = r83
            r34 = r4
            r9 = r56
            r5 = r3
        L_0x113c:
            r2 = r0
            goto L_0x10eb
        L_0x113e:
            r8 = r83
            r2 = r3
            r34 = r4
            r5 = r6
            r9 = r56
            java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x118c, all -> 0x118a }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x118c, all -> 0x118a }
            r4.<init>()     // Catch:{ Exception -> 0x118c, all -> 0x118a }
            java.lang.String r6 = "encoderOutputBuffer "
            r4.append(r6)     // Catch:{ Exception -> 0x118c, all -> 0x118a }
            r4.append(r5)     // Catch:{ Exception -> 0x118c, all -> 0x118a }
            java.lang.String r5 = " was null"
            r4.append(r5)     // Catch:{ Exception -> 0x118c, all -> 0x118a }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x118c, all -> 0x118a }
            r3.<init>(r4)     // Catch:{ Exception -> 0x118c, all -> 0x118a }
            throw r3     // Catch:{ Exception -> 0x118c, all -> 0x118a }
        L_0x1162:
            r0 = move-exception
            r8 = r83
            r2 = r3
            r34 = r4
            goto L_0x11ab
        L_0x1169:
            r8 = r83
            r2 = r3
            r34 = r4
            r5 = r6
            r28 = r14
            r9 = r56
            java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x118c, all -> 0x118a }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x118c, all -> 0x118a }
            r4.<init>()     // Catch:{ Exception -> 0x118c, all -> 0x118a }
            java.lang.String r6 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r6)     // Catch:{ Exception -> 0x118c, all -> 0x118a }
            r4.append(r5)     // Catch:{ Exception -> 0x118c, all -> 0x118a }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x118c, all -> 0x118a }
            r3.<init>(r4)     // Catch:{ Exception -> 0x118c, all -> 0x118a }
            throw r3     // Catch:{ Exception -> 0x118c, all -> 0x118a }
        L_0x118a:
            r0 = move-exception
            goto L_0x119a
        L_0x118c:
            r0 = move-exception
            r3 = r2
            r56 = r9
            goto L_0x11ad
        L_0x1191:
            r0 = move-exception
            r8 = r83
            goto L_0x11a6
        L_0x1195:
            r0 = move-exception
            r8 = r83
            r34 = r4
        L_0x119a:
            r12 = r81
            r11 = r82
        L_0x119e:
            r2 = r0
            goto L_0x0a5d
        L_0x11a1:
            r0 = move-exception
            r8 = r83
            r12 = r84
        L_0x11a6:
            r2 = r3
            r34 = r4
            r28 = r14
        L_0x11ab:
            r9 = r56
        L_0x11ad:
            r6 = r12
            r5 = r29
            goto L_0x077f
        L_0x11b2:
            r0 = move-exception
            r8 = r83
            r70 = r4
            r12 = r81
            r11 = r82
        L_0x11bb:
            r1 = r88
            r2 = r0
            r9 = r29
            r34 = r70
            goto L_0x058a
        L_0x11c4:
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
            goto L_0x077f
        L_0x11d9:
            r0 = move-exception
            r8 = r83
            goto L_0x1271
        L_0x11de:
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
            goto L_0x077f
        L_0x11f0:
            r0 = move-exception
            r8 = r83
            r12 = r84
            r4 = r88
            r2 = r3
            r9 = r56
            goto L_0x1207
        L_0x11fb:
            r0 = move-exception
            r8 = r83
            r12 = r84
            r4 = r88
            r9 = r56
            r2 = r57
            r3 = r2
        L_0x1207:
            r34 = r4
            r6 = r12
            r5 = r29
            r1 = -5
            r28 = 0
            goto L_0x077f
        L_0x1211:
            r0 = move-exception
            r8 = r83
            r4 = r88
            goto L_0x1271
        L_0x1218:
            r0 = move-exception
            r8 = r83
            r12 = r84
            r4 = r88
            r9 = r56
            r2 = r57
            r3 = r2
            r34 = r4
            r6 = r12
            goto L_0x1237
        L_0x1228:
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
        L_0x1237:
            r5 = r29
            r1 = -5
            r28 = 0
            r30 = 0
            goto L_0x077f
        L_0x1240:
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
            goto L_0x12d7
        L_0x1259:
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
            goto L_0x0737
        L_0x126b:
            r0 = move-exception
            r4 = r88
            r26 = r90
            r8 = r9
        L_0x1271:
            r12 = r81
            r11 = r82
        L_0x1275:
            r2 = r0
            r34 = r4
        L_0x1278:
            r9 = r29
            goto L_0x0589
        L_0x127c:
            r0 = move-exception
            r4 = r88
            r26 = r90
            r8 = r9
            r61 = r23
            r2 = r0
            r34 = r4
            r5 = r29
            goto L_0x0734
        L_0x128b:
            r0 = move-exception
            r26 = r90
            r1 = r5
            r8 = r9
            r4 = r88
            r12 = r81
            r11 = r82
            r2 = r0
            r9 = r1
            goto L_0x12bd
        L_0x1299:
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
        L_0x12ae:
            r1 = -5
            goto L_0x12d7
        L_0x12b0:
            r0 = move-exception
            r4 = r88
            r8 = r9
            r12 = r81
            r11 = r82
            r9 = r84
            r26 = r90
            r2 = r0
        L_0x12bd:
            r34 = r4
            goto L_0x0589
        L_0x12c1:
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
        L_0x12d7:
            boolean r4 = r2 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x134b }
            if (r4 == 0) goto L_0x12e0
            if (r95 != 0) goto L_0x12e0
            r47 = 1
            goto L_0x12e2
        L_0x12e0:
            r47 = 0
        L_0x12e2:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x1340 }
            r4.<init>()     // Catch:{ all -> 0x1340 }
            java.lang.String r7 = "bitrate: "
            r4.append(r7)     // Catch:{ all -> 0x1340 }
            r4.append(r5)     // Catch:{ all -> 0x1340 }
            java.lang.String r7 = " framerate: "
            r4.append(r7)     // Catch:{ all -> 0x1340 }
            r4.append(r8)     // Catch:{ all -> 0x1340 }
            java.lang.String r7 = " size: "
            r4.append(r7)     // Catch:{ all -> 0x1340 }
            r7 = r82
            r4.append(r7)     // Catch:{ all -> 0x133c }
            java.lang.String r9 = "x"
            r4.append(r9)     // Catch:{ all -> 0x133c }
            r9 = r81
            r4.append(r9)     // Catch:{ all -> 0x133a }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x133a }
            org.telegram.messenger.FileLog.e((java.lang.String) r4)     // Catch:{ all -> 0x133a }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x133a }
            r2 = r3
            r29 = r5
            r3 = r6
            r6 = 1
        L_0x131a:
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ all -> 0x1332 }
            r5 = r61
            r4.unselectTrack(r5)     // Catch:{ all -> 0x1332 }
            if (r2 == 0) goto L_0x1329
            r2.stop()     // Catch:{ all -> 0x1332 }
            r2.release()     // Catch:{ all -> 0x1332 }
        L_0x1329:
            r42 = r3
            r2 = r47
            r47 = r6
            r6 = r30
            goto L_0x1370
        L_0x1332:
            r0 = move-exception
            r2 = r0
            r11 = r7
            r12 = r9
            r9 = r29
            goto L_0x061e
        L_0x133a:
            r0 = move-exception
            goto L_0x1345
        L_0x133c:
            r0 = move-exception
            r9 = r81
            goto L_0x1345
        L_0x1340:
            r0 = move-exception
            r9 = r81
            r7 = r82
        L_0x1345:
            r2 = r0
            r11 = r7
            r12 = r9
            r7 = r47
            goto L_0x1354
        L_0x134b:
            r0 = move-exception
            r9 = r81
            r7 = r82
            r2 = r0
            r11 = r7
            r12 = r9
        L_0x1353:
            r7 = 0
        L_0x1354:
            r9 = r5
            goto L_0x13d7
        L_0x1357:
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
        L_0x1370:
            if (r6 == 0) goto L_0x137f
            r6.release()     // Catch:{ all -> 0x1376 }
            goto L_0x137f
        L_0x1376:
            r0 = move-exception
            r11 = r7
            r12 = r9
            r9 = r29
            r7 = r2
        L_0x137c:
            r2 = r0
            goto L_0x13d7
        L_0x137f:
            if (r56 == 0) goto L_0x1384
            r56.release()     // Catch:{ all -> 0x1376 }
        L_0x1384:
            if (r42 == 0) goto L_0x138c
            r42.stop()     // Catch:{ all -> 0x1376 }
            r42.release()     // Catch:{ all -> 0x1376 }
        L_0x138c:
            if (r28 == 0) goto L_0x1391
            r28.release()     // Catch:{ all -> 0x1376 }
        L_0x1391:
            r76.checkConversionCanceled()     // Catch:{ all -> 0x1376 }
            r13 = r2
            r6 = r47
        L_0x1397:
            android.media.MediaExtractor r2 = r15.extractor
            if (r2 == 0) goto L_0x139e
            r2.release()
        L_0x139e:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer
            if (r2 == 0) goto L_0x13b3
            r2.finishMovie()     // Catch:{ all -> 0x13ae }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ all -> 0x13ae }
            long r1 = r2.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x13ae }
            r15.endPresentationTime = r1     // Catch:{ all -> 0x13ae }
            goto L_0x13b3
        L_0x13ae:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x13b3:
            r45 = r6
            r6 = r9
            r9 = r29
            goto L_0x1427
        L_0x13ba:
            r0 = move-exception
            r7 = r82
            r4 = r88
            r9 = r12
            r8 = r13
            r26 = r90
            r2 = r0
            r34 = r4
            r11 = r7
            goto L_0x13d3
        L_0x13c8:
            r0 = move-exception
            r4 = r88
            r8 = r10
            r7 = r11
            r9 = r12
            r26 = r90
            r2 = r0
            r34 = r4
        L_0x13d3:
            r1 = -5
            r7 = 0
            r9 = r84
        L_0x13d7:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x1493 }
            r3.<init>()     // Catch:{ all -> 0x1493 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x1493 }
            r3.append(r9)     // Catch:{ all -> 0x1493 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x1493 }
            r3.append(r8)     // Catch:{ all -> 0x1493 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x1493 }
            r3.append(r11)     // Catch:{ all -> 0x1493 }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x1493 }
            r3.append(r12)     // Catch:{ all -> 0x1493 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x1493 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x1493 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x1493 }
            android.media.MediaExtractor r2 = r15.extractor
            if (r2 == 0) goto L_0x140d
            r2.release()
        L_0x140d:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer
            if (r2 == 0) goto L_0x1422
            r2.finishMovie()     // Catch:{ all -> 0x141d }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ all -> 0x141d }
            long r1 = r2.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x141d }
            r15.endPresentationTime = r1     // Catch:{ all -> 0x141d }
            goto L_0x1422
        L_0x141d:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1422:
            r13 = r7
            r7 = r11
            r6 = r12
            r45 = 1
        L_0x1427:
            if (r13 == 0) goto L_0x1452
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
        L_0x1452:
            long r1 = java.lang.System.currentTimeMillis()
            long r1 = r1 - r16
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x1492
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
        L_0x1492:
            return r45
        L_0x1493:
            r0 = move-exception
            r2 = r76
            r3 = r0
            android.media.MediaExtractor r4 = r2.extractor
            if (r4 == 0) goto L_0x149e
            r4.release()
        L_0x149e:
            org.telegram.messenger.video.MP4Builder r4 = r2.mediaMuxer
            if (r4 == 0) goto L_0x14b3
            r4.finishMovie()     // Catch:{ all -> 0x14ae }
            org.telegram.messenger.video.MP4Builder r4 = r2.mediaMuxer     // Catch:{ all -> 0x14ae }
            long r4 = r4.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x14ae }
            r2.endPresentationTime = r4     // Catch:{ all -> 0x14ae }
            goto L_0x14b3
        L_0x14ae:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x14b3:
            goto L_0x14b5
        L_0x14b4:
            throw r3
        L_0x14b5:
            goto L_0x14b4
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
