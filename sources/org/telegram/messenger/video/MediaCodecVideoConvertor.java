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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r86v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r86v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r86v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v4, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v70, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v81, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v82, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r66v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v109, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v96, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v110, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v111, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v98, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v99, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v100, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v102, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r86v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r86v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v104, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r86v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v105, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v106, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v108, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v112, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v114, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v115, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v116, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v117, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v118, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v119, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v120, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v121, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v122, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v123, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v124, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v125, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v126, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v80, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v127, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v128, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v129, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v130, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v131, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v132, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v133, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v141, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v142, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v149, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v24, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v25, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v193, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v194, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v197, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v198, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v165, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v167, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v168, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v169, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v173, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v186, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v187, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v188, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v189, resolved type: long} */
    /* JADX WARNING: type inference failed for: r9v132 */
    /* JADX WARNING: Code restructure failed: missing block: B:1001:0x10d3, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1021:0x113a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1022:0x113b, code lost:
        r8 = r79;
        r34 = r4;
        r9 = r53;
        r5 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1035:0x1197, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1036:0x1198, code lost:
        r8 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1037:0x119b, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1038:0x119c, code lost:
        r8 = r79;
        r34 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1041:0x11a7, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1042:0x11a8, code lost:
        r8 = r79;
        r12 = r80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1043:0x11ac, code lost:
        r2 = r3;
        r34 = r4;
        r28 = r14;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1055:0x11f6, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1056:0x11f7, code lost:
        r8 = r79;
        r12 = r80;
        r4 = r84;
        r2 = r3;
        r9 = r53;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1057:0x1201, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1058:0x1202, code lost:
        r8 = r79;
        r12 = r80;
        r4 = r84;
        r9 = r53;
        r3 = r54;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1059:0x120d, code lost:
        r34 = r4;
        r6 = r12;
        r5 = r29;
        r1 = -5;
        r28 = null;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1060:0x1217, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1061:0x1218, code lost:
        r8 = r79;
        r4 = r84;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1064:0x122d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1065:0x122e, code lost:
        r26 = r86;
        r12 = r8;
        r8 = r9;
        r58 = r23;
        r3 = r2;
        r34 = r84;
        r53 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1069:0x1260, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1070:0x1261, code lost:
        r26 = r86;
        r12 = r8;
        r8 = r9;
        r58 = r23;
        r2 = r0;
        r34 = r84;
        r6 = r12;
        r5 = r29;
        r1 = -5;
        r3 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1071:0x1273, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1072:0x1274, code lost:
        r4 = r84;
        r26 = r86;
        r8 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1076:0x1284, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1077:0x1285, code lost:
        r26 = r86;
        r8 = r9;
        r58 = r23;
        r2 = r0;
        r34 = r84;
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
        r8 = r79;
        r34 = r84;
        r26 = r86;
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
        r6 = r77;
        r11 = r49;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x0545, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0546, code lost:
        r8 = r79;
        r34 = r84;
        r26 = r86;
        r2 = r0;
        r9 = r48;
        r11 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x0552, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0553, code lost:
        r2 = r40;
        r6 = r77;
        r11 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x055b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x055c, code lost:
        r13 = r6;
        r48 = r9;
        r49 = r11;
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
        r48 = r9;
        r49 = r11;
        r2 = r0;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x0571, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x0572, code lost:
        r48 = r9;
        r49 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x059f, code lost:
        r45 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x08d4, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x08e0, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x08e1, code lost:
        r8 = r79;
        r6 = r80;
        r34 = r84;
        r2 = r0;
        r28 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x099a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x09be, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x09bf, code lost:
        r4 = r84;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x09cc, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0b4d, code lost:
        if (r13.presentationTimeUs < r4) goto L_0x0b4f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0CLASSNAME, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x01d3, code lost:
        r6 = r7;
        r7 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x0d22, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x0d23, code lost:
        r8 = r79;
        r2 = r0;
        r34 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x0d50, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x0d51, code lost:
        r34 = r34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x0e53, code lost:
        r0 = th;
        r34 = r34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x0e55, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x0e56, code lost:
        r8 = r79;
        r2 = r0;
        r6 = r12;
        r5 = r29;
        r3 = r54;
        r34 = r34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x0f0e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x0f0f, code lost:
        r8 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x0fb3, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x0fb5, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x0fb8, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x0fb9, code lost:
        r8 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x0221, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:0x104f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:989:0x108b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:990:0x108c, code lost:
        r1 = r84;
        r2 = r0;
        r3 = r5;
        r53 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x1093, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:992:0x1094, code lost:
        r12 = r77;
        r11 = r78;
        r1 = r84;
        r2 = r0;
        r9 = r29;
        r7 = false;
        r70 = r26;
        r26 = r33;
        r34 = r70;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1001:0x10d3 A[ExcHandler: all (th java.lang.Throwable), PHI: r8 
      PHI: (r8v67 int) = (r8v66 int), (r8v73 int), (r8v73 int), (r8v73 int), (r8v73 int) binds: [B:998:0x10bc, B:911:0x0var_, B:912:?, B:905:0x0var_, B:906:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:905:0x0var_] */
    /* JADX WARNING: Removed duplicated region for block: B:1037:0x119b A[ExcHandler: all (th java.lang.Throwable), Splitter:B:693:0x0CLASSNAME] */
    /* JADX WARNING: Removed duplicated region for block: B:1060:0x1217 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:478:0x08b2] */
    /* JADX WARNING: Removed duplicated region for block: B:1071:0x1273 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:468:0x0850] */
    /* JADX WARNING: Removed duplicated region for block: B:1091:0x12e3 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:1106:0x132b A[Catch:{ all -> 0x133a }] */
    /* JADX WARNING: Removed duplicated region for block: B:1120:0x135f  */
    /* JADX WARNING: Removed duplicated region for block: B:1122:0x137a A[SYNTHETIC, Splitter:B:1122:0x137a] */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x1389 A[Catch:{ all -> 0x137e }] */
    /* JADX WARNING: Removed duplicated region for block: B:1130:0x138e A[Catch:{ all -> 0x137e }] */
    /* JADX WARNING: Removed duplicated region for block: B:1132:0x1396 A[Catch:{ all -> 0x137e }] */
    /* JADX WARNING: Removed duplicated region for block: B:1137:0x13a3  */
    /* JADX WARNING: Removed duplicated region for block: B:1140:0x13aa A[SYNTHETIC, Splitter:B:1140:0x13aa] */
    /* JADX WARNING: Removed duplicated region for block: B:1154:0x1412  */
    /* JADX WARNING: Removed duplicated region for block: B:1157:0x1419 A[SYNTHETIC, Splitter:B:1157:0x1419] */
    /* JADX WARNING: Removed duplicated region for block: B:1163:0x1431  */
    /* JADX WARNING: Removed duplicated region for block: B:1165:0x145a  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x0417 A[Catch:{ Exception -> 0x04b0, all -> 0x04ab }] */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0419 A[Catch:{ Exception -> 0x04b0, all -> 0x04ab }] */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0428  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x043f  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x04f6 A[ExcHandler: all (r0v170 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r21 
      PHI: (r21v22 int) = (r21v21 int), (r21v33 int), (r21v33 int) binds: [B:254:0x04bf, B:189:0x038a, B:190:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:189:0x038a] */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x050a A[ExcHandler: all (th java.lang.Throwable), PHI: r1 
      PHI: (r1v182 int) = (r1v181 int), (r1v181 int), (r1v181 int), (r1v181 int), (r1v181 int), (r1v181 int), (r1v181 int), (r1v209 int), (r1v181 int) binds: [B:73:0x01d6, B:74:?, B:78:0x01e4, B:116:0x0298, B:117:?, B:125:0x02a7, B:126:?, B:178:0x0366, B:122:0x02a1] A[DONT_GENERATE, DONT_INLINE], Splitter:B:78:0x01e4] */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0514 A[ExcHandler: all (th java.lang.Throwable), PHI: r1 r49 
      PHI: (r1v180 int) = (r1v175 int), (r1v175 int), (r1v181 int), (r1v181 int), (r1v181 int), (r1v181 int) binds: [B:67:0x01c6, B:68:?, B:137:0x02bc, B:120:0x029e, B:121:?, B:86:0x0203] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r49v10 int) = (r49v5 int), (r49v5 int), (r49v11 int), (r49v11 int), (r49v11 int), (r49v11 int) binds: [B:67:0x01c6, B:68:?, B:137:0x02bc, B:120:0x029e, B:121:?, B:86:0x0203] A[DONT_GENERATE, DONT_INLINE], Splitter:B:67:0x01c6] */
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
    /* JADX WARNING: Removed duplicated region for block: B:451:0x081c A[SYNTHETIC, Splitter:B:451:0x081c] */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x084c  */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x08cb A[SYNTHETIC, Splitter:B:487:0x08cb] */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x08d4 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:487:0x08cb] */
    /* JADX WARNING: Removed duplicated region for block: B:494:0x08ee  */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x08f4 A[SYNTHETIC, Splitter:B:497:0x08f4] */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x0922  */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x0925  */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x09cc A[ExcHandler: all (th java.lang.Throwable), Splitter:B:535:0x0986] */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x09d0  */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x09f9  */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x0a07  */
    /* JADX WARNING: Removed duplicated region for block: B:573:0x0a0a  */
    /* JADX WARNING: Removed duplicated region for block: B:578:0x0a2c A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a4a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:598:0x0a7b  */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x0bda  */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x0bfa A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:696:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:697:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:704:0x0c4f  */
    /* JADX WARNING: Removed duplicated region for block: B:712:0x0CLASSNAME A[Catch:{ Exception -> 0x0c7e, all -> 0x0CLASSNAME }, ExcHandler: all (th java.lang.Throwable), Splitter:B:706:0x0CLASSNAME] */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x0e53 A[ExcHandler: all (th java.lang.Throwable), PHI: r34 
      PHI: (r34v62 long) = (r34v165 long), (r34v168 long), (r34v169 long), (r34v173 long) binds: [B:798:0x0da4, B:799:?, B:801:0x0da9, B:771:0x0d45] A[DONT_GENERATE, DONT_INLINE], Splitter:B:771:0x0d45] */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x0e81 A[Catch:{ Exception -> 0x1131, all -> 0x112b }] */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x0e84 A[Catch:{ Exception -> 0x1131, all -> 0x112b }] */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x0e94  */
    /* JADX WARNING: Removed duplicated region for block: B:856:0x0eb4  */
    /* JADX WARNING: Removed duplicated region for block: B:874:0x0f0e A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:868:0x0ee8] */
    /* JADX WARNING: Removed duplicated region for block: B:924:0x0fb5 A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:899:0x0var_] */
    /* JADX WARNING: Removed duplicated region for block: B:932:0x0fd9  */
    /* JADX WARNING: Removed duplicated region for block: B:933:0x0fdc  */
    /* JADX WARNING: Removed duplicated region for block: B:940:0x0fea A[SYNTHETIC, Splitter:B:940:0x0fea] */
    /* JADX WARNING: Removed duplicated region for block: B:945:0x100e A[Catch:{ Exception -> 0x101c, all -> 0x1093 }] */
    /* JADX WARNING: Removed duplicated region for block: B:953:0x1023 A[Catch:{ Exception -> 0x101c, all -> 0x1093 }] */
    /* JADX WARNING: Removed duplicated region for block: B:954:0x1026 A[Catch:{ Exception -> 0x101c, all -> 0x1093 }] */
    /* JADX WARNING: Removed duplicated region for block: B:962:0x103b  */
    /* JADX WARNING: Removed duplicated region for block: B:980:0x106c A[Catch:{ Exception -> 0x108b, all -> 0x1093 }] */
    /* JADX WARNING: Removed duplicated region for block: B:983:0x1078 A[Catch:{ Exception -> 0x108b, all -> 0x1093 }] */
    /* JADX WARNING: Removed duplicated region for block: B:988:0x1087  */
    /* JADX WARNING: Removed duplicated region for block: B:991:0x1093 A[ExcHandler: all (r0v53 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:959:0x1036] */
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
            r3 = r90
            r2 = r96
            long r16 = java.lang.System.currentTimeMillis()
            r18 = -1
            r7 = 0
            android.media.MediaCodec$BufferInfo r6 = new android.media.MediaCodec$BufferInfo     // Catch:{ all -> 0x13d0 }
            r6.<init>()     // Catch:{ all -> 0x13d0 }
            org.telegram.messenger.video.Mp4Movie r1 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ all -> 0x13d0 }
            r1.<init>()     // Catch:{ all -> 0x13d0 }
            r22 = r6
            r6 = r74
            r1.setCacheFile(r6)     // Catch:{ all -> 0x13d0 }
            r1.setRotation(r7)     // Catch:{ all -> 0x13d0 }
            r1.setSize(r12, r11)     // Catch:{ all -> 0x13d0 }
            org.telegram.messenger.video.MP4Builder r7 = new org.telegram.messenger.video.MP4Builder     // Catch:{ all -> 0x13d0 }
            r7.<init>()     // Catch:{ all -> 0x13d0 }
            r6 = r76
            org.telegram.messenger.video.MP4Builder r1 = r7.createMovie(r1, r6)     // Catch:{ all -> 0x13d0 }
            r15.mediaMuxer = r1     // Catch:{ all -> 0x13d0 }
            float r1 = (float) r4     // Catch:{ all -> 0x13d0 }
            r24 = 1148846080(0x447a0000, float:1000.0)
            float r25 = r1 / r24
            r26 = 1000(0x3e8, double:4.94E-321)
            long r1 = r4 * r26
            r15.endPresentationTime = r1     // Catch:{ all -> 0x13d0 }
            r72.checkConversionCanceled()     // Catch:{ all -> 0x13d0 }
            java.lang.String r7 = "csd-1"
            java.lang.String r1 = "csd-0"
            java.lang.String r6 = "prepend-sps-pps-to-idr-frames"
            r2 = 921600(0xe1000, float:1.291437E-39)
            r28 = r6
            r27 = r7
            java.lang.String r7 = "video/avc"
            r33 = r7
            r6 = 0
            if (r95 == 0) goto L_0x062e
            int r18 = (r86 > r6 ? 1 : (r86 == r6 ? 0 : -1))
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
            r34 = r84
            r26 = r86
            r2 = r0
            r8 = r10
            goto L_0x0589
        L_0x00d4:
            r0 = move-exception
            r2 = r0
            r48 = r9
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
            r7 = r33
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r7, r12, r11)     // Catch:{ Exception -> 0x0577, all -> 0x0571 }
            java.lang.String r6 = "color-format"
            r33 = r1
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
            r37 = 1
            r21 = r1
            r39 = r33
            r33 = 0
            r1 = r19
            r40 = r2
            r2 = r92
            r3 = r73
            r4 = r93
            r5 = r94
            r7 = r6
            r14 = r22
            r13 = 21
            r6 = r20
            r77 = r7
            r47 = r18
            r46 = r27
            r7 = r12
            r8 = r11
            r48 = r9
            r9 = r75
            r10 = r21
            r49 = r11
            r11 = r37
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0552, all -> 0x0545 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x053b, all -> 0x0545 }
            if (r1 >= r13) goto L_0x01ba
            java.nio.ByteBuffer[] r6 = r77.getOutputBuffers()     // Catch:{ Exception -> 0x01b1, all -> 0x0545 }
            goto L_0x01bb
        L_0x01b1:
            r0 = move-exception
            r6 = r77
            r2 = r0
            r11 = r49
            r1 = -5
            goto L_0x0599
        L_0x01ba:
            r6 = 0
        L_0x01bb:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x053b, all -> 0x0545 }
            r1 = -5
            r2 = 1
            r3 = 0
            r4 = 0
            r5 = 0
            r7 = 0
        L_0x01c4:
            if (r7 != 0) goto L_0x052b
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x0522, all -> 0x0514 }
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
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x050e, all -> 0x050a }
            if (r91 == 0) goto L_0x01e0
            r10 = 22000(0x55f0, double:1.08694E-319)
            r13 = r77
            goto L_0x01e4
        L_0x01e0:
            r13 = r77
            r10 = 2500(0x9c4, double:1.235E-320)
        L_0x01e4:
            int r10 = r13.dequeueOutputBuffer(r14, r10)     // Catch:{ Exception -> 0x0508, all -> 0x050a }
            r11 = -1
            if (r10 != r11) goto L_0x0200
            r77 = r1
            r20 = r4
            r18 = r5
            r78 = r8
            r8 = r39
            r11 = r46
            r4 = r47
            r1 = -1
            r6 = 0
        L_0x01fb:
            r5 = r3
            r3 = r49
            goto L_0x0426
        L_0x0200:
            r11 = -3
            if (r10 != r11) goto L_0x0228
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r77 = r6
            r6 = 21
            if (r11 >= r6) goto L_0x020f
            java.nio.ByteBuffer[] r7 = r13.getOutputBuffers()     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
        L_0x020f:
            r6 = r77
            r77 = r1
            r20 = r4
            r18 = r5
            r78 = r8
            r8 = r39
            r11 = r46
        L_0x021d:
            r4 = r47
            r1 = -1
            goto L_0x01fb
        L_0x0221:
            r0 = move-exception
        L_0x0222:
            r2 = r0
        L_0x0223:
            r6 = r13
        L_0x0224:
            r11 = r49
            goto L_0x0599
        L_0x0228:
            r77 = r6
            r6 = -2
            if (r10 != r6) goto L_0x0290
            android.media.MediaFormat r6 = r13.getOutputFormat()     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            if (r11 == 0) goto L_0x024c
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r11.<init>()     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r78 = r8
            java.lang.String r8 = "photo encoder new format "
            r11.append(r8)     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            r11.append(r6)     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            java.lang.String r8 = r11.toString()     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            org.telegram.messenger.FileLog.d(r8)     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            goto L_0x024e
        L_0x024c:
            r78 = r8
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
            r11 = r46
            java.nio.ByteBuffer r6 = r6.getByteBuffer(r11)     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            int r4 = r4.limit()     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            int r6 = r6.limit()     // Catch:{ Exception -> 0x0221, all -> 0x0514 }
            int r4 = r4 + r6
            goto L_0x0287
        L_0x0281:
            r28 = r11
        L_0x0283:
            r8 = r39
            r11 = r46
        L_0x0287:
            r6 = r77
            r77 = r1
            r20 = r4
            r18 = r5
            goto L_0x021d
        L_0x0290:
            r78 = r8
            r8 = r39
            r11 = r46
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
            r80 = r7
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
            r4 = r47
            r3 = r49
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
            r4 = r47
            r3 = r49
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
            r8 = r79
            r34 = r84
            r26 = r86
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
            r4 = r47
            goto L_0x040d
        L_0x03e9:
            r0 = move-exception
            r21 = r1
            r3 = r49
            r8 = r79
            r34 = r84
            r26 = r86
            r2 = r0
            r11 = r3
        L_0x03f6:
            r9 = r48
            goto L_0x058a
        L_0x03fa:
            r0 = move-exception
            r21 = r1
            r3 = r49
            r2 = r0
            r11 = r3
            r6 = r13
            goto L_0x0599
        L_0x0404:
            r21 = r1
            r20 = r4
            r18 = r5
            r4 = r47
            r5 = r3
        L_0x040d:
            r3 = r49
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
            r7 = r80
            r9 = r6
            r6 = r77
            r77 = r1
            r1 = -1
        L_0x0426:
            if (r10 == r1) goto L_0x043f
            r1 = r77
            r49 = r3
            r47 = r4
            r3 = r5
            r39 = r8
            r46 = r11
            r77 = r13
            r5 = r18
            r4 = r20
            r13 = 21
            r8 = r78
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
            r80 = r2
            r49 = r3
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
            r1 = r77
            r2 = r0
            r40 = r10
            goto L_0x0223
        L_0x047a:
            r0 = move-exception
            r49 = r3
        L_0x047d:
            r1 = r77
            goto L_0x0515
        L_0x0481:
            r0 = move-exception
            r49 = r3
            r10 = r40
            r1 = r77
            goto L_0x0222
        L_0x048a:
            r80 = r2
            r49 = r3
            r1 = r18
            r10 = r40
        L_0x0492:
            r3 = r5
            r5 = r1
            r1 = r78
        L_0x0496:
            r2 = r80
            r47 = r4
            r39 = r8
            r40 = r10
            r46 = r11
            r4 = r20
            r8 = r1
            r1 = r77
            r77 = r13
            r13 = 21
            goto L_0x01ce
        L_0x04ab:
            r0 = move-exception
            r49 = r3
            goto L_0x0515
        L_0x04b0:
            r0 = move-exception
            r49 = r3
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
            r8 = r79
            r34 = r84
            r26 = r86
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
            r13 = r77
        L_0x0511:
            r21 = r1
            goto L_0x0525
        L_0x0514:
            r0 = move-exception
        L_0x0515:
            r8 = r79
            r34 = r84
            r26 = r86
            r2 = r0
        L_0x051c:
            r9 = r48
            r11 = r49
            goto L_0x058a
        L_0x0522:
            r0 = move-exception
            r13 = r77
        L_0x0525:
            r2 = r40
            r6 = r13
        L_0x0528:
            r11 = r49
            goto L_0x0566
        L_0x052b:
            r13 = r77
            r2 = r40
            r6 = r13
            r9 = r48
            r11 = r49
            r7 = 0
            r45 = 0
            r13 = r79
            goto L_0x05dd
        L_0x053b:
            r0 = move-exception
            r13 = r77
            r2 = r40
            r6 = r13
            r11 = r49
            r1 = -5
            goto L_0x0566
        L_0x0545:
            r0 = move-exception
            r8 = r79
            r34 = r84
            r26 = r86
            r2 = r0
            r9 = r48
            r11 = r49
            goto L_0x0589
        L_0x0552:
            r0 = move-exception
            r13 = r77
            r2 = r40
            r6 = r13
            r11 = r49
            goto L_0x0563
        L_0x055b:
            r0 = move-exception
            r13 = r6
            r48 = r9
            r49 = r11
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
            r48 = r9
            r49 = r11
            r2 = r0
            r1 = -5
            goto L_0x0595
        L_0x0571:
            r0 = move-exception
            r48 = r9
            r49 = r11
            goto L_0x0582
        L_0x0577:
            r0 = move-exception
            r48 = r9
            r49 = r11
            goto L_0x0592
        L_0x057d:
            r0 = move-exception
            r48 = r9
            r11 = r78
        L_0x0582:
            r8 = r79
        L_0x0584:
            r34 = r84
            r26 = r86
            r2 = r0
        L_0x0589:
            r1 = -5
        L_0x058a:
            r7 = 0
            goto L_0x13df
        L_0x058d:
            r0 = move-exception
            r48 = r9
            r11 = r78
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
            if (r91 != 0) goto L_0x05a2
            r45 = 1
            goto L_0x05a4
        L_0x05a2:
            r45 = 0
        L_0x05a4:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0614 }
            r3.<init>()     // Catch:{ all -> 0x0614 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x0614 }
            r9 = r48
            r3.append(r9)     // Catch:{ all -> 0x0612 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x0612 }
            r13 = r79
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
            r7 = r45
            r45 = 1
        L_0x05dd:
            if (r19 == 0) goto L_0x05ec
            r19.release()     // Catch:{ all -> 0x05e3 }
            goto L_0x05ec
        L_0x05e3:
            r0 = move-exception
            r34 = r84
            r26 = r86
            r2 = r0
            r8 = r13
            goto L_0x13df
        L_0x05ec:
            if (r2 == 0) goto L_0x05f1
            r2.release()     // Catch:{ all -> 0x05e3 }
        L_0x05f1:
            if (r6 == 0) goto L_0x05f9
            r6.stop()     // Catch:{ all -> 0x05e3 }
            r6.release()     // Catch:{ all -> 0x05e3 }
        L_0x05f9:
            r72.checkConversionCanceled()     // Catch:{ all -> 0x05e3 }
            r34 = r84
            r26 = r86
            r29 = r9
            r9 = r12
            r8 = r13
            r6 = r45
            r13 = r7
            r7 = r11
            goto L_0x139f
        L_0x060a:
            r0 = move-exception
            r34 = r84
            r26 = r86
            r2 = r0
            r8 = r13
            goto L_0x061e
        L_0x0612:
            r0 = move-exception
            goto L_0x0617
        L_0x0614:
            r0 = move-exception
            r9 = r48
        L_0x0617:
            r8 = r79
            r34 = r84
            r26 = r86
            r2 = r0
        L_0x061e:
            r7 = r45
            goto L_0x13df
        L_0x0622:
            r0 = move-exception
            r9 = r48
            r8 = r79
            r34 = r84
            r26 = r86
            r2 = r0
            goto L_0x058a
        L_0x062e:
            r8 = r1
            r13 = r10
            r14 = r22
            r11 = r27
            r1 = r28
            r4 = r33
            r10 = 3
            android.media.MediaExtractor r3 = new android.media.MediaExtractor     // Catch:{ all -> 0x13c2 }
            r3.<init>()     // Catch:{ all -> 0x13c2 }
            r15.extractor = r3     // Catch:{ all -> 0x13c2 }
            r7 = r73
            r6 = r1
            r3.setDataSource(r7)     // Catch:{ all -> 0x13c2 }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ all -> 0x13c2 }
            r5 = 0
            int r3 = org.telegram.messenger.MediaController.findTrack(r1, r5)     // Catch:{ all -> 0x13c2 }
            r1 = -1
            if (r9 == r1) goto L_0x0665
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ all -> 0x065a }
            r46 = r11
            r11 = 1
            int r1 = org.telegram.messenger.MediaController.findTrack(r1, r11)     // Catch:{ all -> 0x065a }
            goto L_0x0668
        L_0x065a:
            r0 = move-exception
            r11 = r78
            r34 = r84
            r26 = r86
            r2 = r0
            r8 = r13
            goto L_0x0589
        L_0x0665:
            r46 = r11
            r1 = -1
        L_0x0668:
            java.lang.String r11 = "mime"
            if (r3 < 0) goto L_0x0680
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ all -> 0x065a }
            android.media.MediaFormat r5 = r5.getTrackFormat(r3)     // Catch:{ all -> 0x065a }
            java.lang.String r5 = r5.getString(r11)     // Catch:{ all -> 0x065a }
            boolean r5 = r5.equals(r4)     // Catch:{ all -> 0x065a }
            if (r5 != 0) goto L_0x0680
            r13 = r90
            r5 = 1
            goto L_0x0683
        L_0x0680:
            r13 = r90
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
            r1 = r72
            r4 = r14
            r8 = 0
            r5 = r82
            r14 = r7
            r10 = 0
            r11 = -5
            r7 = r84
            r13 = 0
            r9 = r88
            r11 = r74
            r12 = r18
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ all -> 0x06b9 }
            r9 = r77
            r7 = r78
            r8 = r79
            r29 = r80
            r34 = r84
            r26 = r86
            r1 = -5
            r6 = 0
            goto L_0x139f
        L_0x06b9:
            r0 = move-exception
            goto L_0x06be
        L_0x06bb:
            r0 = move-exception
            r14 = r7
            r13 = 0
        L_0x06be:
            r12 = r77
            r11 = r78
            r8 = r79
            r9 = r80
            goto L_0x0584
        L_0x06c8:
            r12 = r7
            r5 = -1
            r13 = 0
            if (r3 < 0) goto L_0x135f
            r20 = -2147483648(0xfffffffvar_, double:NaN)
            r7 = 1000(0x3e8, float:1.401E-42)
            r9 = r79
            int r7 = r7 / r9
            int r7 = r7 * 1000
            r22 = r14
            long r13 = (long) r7     // Catch:{ Exception -> 0x12c9, all -> 0x12b8 }
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x12c9, all -> 0x12b8 }
            r7.selectTrack(r3)     // Catch:{ Exception -> 0x12c9, all -> 0x12b8 }
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x12c9, all -> 0x12b8 }
            android.media.MediaFormat r7 = r7.getTrackFormat(r3)     // Catch:{ Exception -> 0x12c9, all -> 0x12b8 }
            r26 = 0
            int r23 = (r86 > r26 ? 1 : (r86 == r26 ? 0 : -1))
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
            if (r80 > 0) goto L_0x0712
            r26 = r86
            r23 = r3
            r5 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x0718
        L_0x0712:
            r5 = r80
            r26 = r86
        L_0x0716:
            r23 = r3
        L_0x0718:
            r3 = r81
            if (r3 <= 0) goto L_0x073f
            int r5 = java.lang.Math.min(r3, r5)     // Catch:{ Exception -> 0x072d, all -> 0x0721 }
            goto L_0x073f
        L_0x0721:
            r0 = move-exception
            r12 = r77
            r11 = r78
            r34 = r84
        L_0x0728:
            r2 = r0
            r8 = r9
            r1 = -5
            goto L_0x135b
        L_0x072d:
            r0 = move-exception
            r34 = r84
        L_0x0730:
            r2 = r0
            r8 = r9
            r58 = r23
        L_0x0734:
            r1 = -5
            r3 = 0
            r6 = 0
        L_0x0737:
            r28 = 0
            r30 = 0
            r53 = 0
            goto L_0x12df
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
            r10 = r96
            r86 = r2
            r47 = r4
        L_0x075c:
            r2 = 0
            goto L_0x07b3
        L_0x075f:
            r0 = move-exception
            r12 = r77
            r11 = r78
            r34 = r84
            r26 = r2
            r8 = r9
            r1 = -5
            r7 = 0
            r2 = r0
            goto L_0x135c
        L_0x076e:
            r0 = move-exception
            r34 = r84
            r26 = r2
        L_0x0773:
            r8 = r9
            r58 = r23
            r1 = -5
            r3 = 0
            r6 = 0
            r28 = 0
            r30 = 0
            r53 = 0
        L_0x077f:
            r2 = r0
            goto L_0x12df
        L_0x0782:
            r26 = r1
            r86 = r2
            r47 = r4
            r1 = 0
            r3 = r82
            int r10 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r10 <= 0) goto L_0x07a9
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
            r2 = 0
            r1.seekTo(r3, r2)     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
            r10 = r96
            goto L_0x075c
        L_0x0799:
            r0 = move-exception
            r12 = r77
            r11 = r78
            r34 = r84
            r26 = r86
            goto L_0x0728
        L_0x07a3:
            r0 = move-exception
            r34 = r84
            r26 = r86
            goto L_0x0730
        L_0x07a9:
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x12a1, all -> 0x1293 }
            r2 = 0
            r4 = 0
            r1.seekTo(r2, r4)     // Catch:{ Exception -> 0x12a1, all -> 0x1293 }
            r10 = r96
        L_0x07b3:
            if (r10 == 0) goto L_0x07cc
            r1 = 90
            r4 = r75
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
            r4 = r75
            r2 = r77
            r3 = r78
        L_0x07d2:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x12a1, all -> 0x1293 }
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
            r4 = r47
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r4, r2, r3)     // Catch:{ Exception -> 0x12a1, all -> 0x1293 }
            r28 = r6
            java.lang.String r6 = "color-format"
            r39 = r8
            r8 = 2130708361(0x7var_, float:1.701803E38)
            r1.setInteger(r6, r8)     // Catch:{ Exception -> 0x12a1, all -> 0x1293 }
            java.lang.String r6 = "bitrate"
            r1.setInteger(r6, r5)     // Catch:{ Exception -> 0x12a1, all -> 0x1293 }
            java.lang.String r6 = "frame-rate"
            r8 = 30
            r1.setInteger(r6, r8)     // Catch:{ Exception -> 0x12a1, all -> 0x1293 }
            java.lang.String r6 = "i-frame-interval"
            r8 = 1
            r1.setInteger(r6, r8)     // Catch:{ Exception -> 0x12a1, all -> 0x1293 }
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x12a1, all -> 0x1293 }
            r8 = 23
            if (r6 >= r8) goto L_0x084c
            int r8 = java.lang.Math.min(r3, r2)     // Catch:{ Exception -> 0x07a3, all -> 0x0799 }
            r27 = r2
            r2 = 480(0x1e0, float:6.73E-43)
            if (r8 > r2) goto L_0x084e
            r2 = 921600(0xe1000, float:1.291437E-39)
            if (r5 <= r2) goto L_0x082c
            goto L_0x082d
        L_0x082c:
            r2 = r5
        L_0x082d:
            java.lang.String r5 = "bitrate"
            r1.setInteger(r5, r2)     // Catch:{ Exception -> 0x0844, all -> 0x0835 }
            r29 = r2
            goto L_0x0850
        L_0x0835:
            r0 = move-exception
            r12 = r77
            r11 = r78
            r34 = r84
            r26 = r86
            r8 = r9
            r1 = -5
            r7 = 0
            r9 = r2
            goto L_0x1384
        L_0x0844:
            r0 = move-exception
            r34 = r84
            r26 = r86
            r5 = r2
            goto L_0x0773
        L_0x084c:
            r27 = r2
        L_0x084e:
            r29 = r5
        L_0x0850:
            android.media.MediaCodec r8 = android.media.MediaCodec.createEncoderByType(r4)     // Catch:{ Exception -> 0x1284, all -> 0x1273 }
            r2 = 1
            r5 = 0
            r8.configure(r1, r5, r5, r2)     // Catch:{ Exception -> 0x1260, all -> 0x1273 }
            org.telegram.messenger.video.InputSurface r1 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x1260, all -> 0x1273 }
            android.view.Surface r2 = r8.createInputSurface()     // Catch:{ Exception -> 0x1260, all -> 0x1273 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x1260, all -> 0x1273 }
            r1.makeCurrent()     // Catch:{ Exception -> 0x1246, all -> 0x1273 }
            r8.start()     // Catch:{ Exception -> 0x1246, all -> 0x1273 }
            java.lang.String r2 = r7.getString(r11)     // Catch:{ Exception -> 0x1246, all -> 0x1273 }
            android.media.MediaCodec r2 = android.media.MediaCodec.createDecoderByType(r2)     // Catch:{ Exception -> 0x1246, all -> 0x1273 }
            org.telegram.messenger.video.OutputSurface r30 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x122d, all -> 0x1273 }
            r31 = 0
            r32 = r3
            float r3 = (float) r9
            r33 = 0
            r53 = r1
            r52 = r26
            r1 = r30
            r54 = r2
            r55 = r27
            r34 = 1
            r26 = r86
            r2 = r92
            r58 = r23
            r59 = r32
            r23 = r3
            r3 = r31
            r60 = r4
            r4 = r93
            r5 = r94
            r62 = r6
            r63 = r28
            r6 = r96
            r64 = r7
            r7 = r77
            r80 = r8
            r65 = r39
            r8 = r78
            r9 = r75
            r10 = r23
            r39 = r13
            r13 = r46
            r14 = r11
            r11 = r33
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x121e, all -> 0x1217 }
            android.view.Surface r1 = r30.getSurface()     // Catch:{ Exception -> 0x1201, all -> 0x1217 }
            r3 = r54
            r2 = r64
            r4 = 0
            r5 = 0
            r3.configure(r2, r1, r4, r5)     // Catch:{ Exception -> 0x11f6, all -> 0x1217 }
            r3.start()     // Catch:{ Exception -> 0x11f6, all -> 0x1217 }
            r1 = r62
            r2 = 21
            if (r1 >= r2) goto L_0x08ee
            java.nio.ByteBuffer[] r6 = r3.getInputBuffers()     // Catch:{ Exception -> 0x08e0, all -> 0x08d4 }
            java.nio.ByteBuffer[] r1 = r80.getOutputBuffers()     // Catch:{ Exception -> 0x08e0, all -> 0x08d4 }
            goto L_0x08f0
        L_0x08d4:
            r0 = move-exception
        L_0x08d5:
            r12 = r77
            r11 = r78
            r8 = r79
            r34 = r84
            r2 = r0
            goto L_0x1280
        L_0x08e0:
            r0 = move-exception
            r8 = r79
            r6 = r80
            r34 = r84
            r2 = r0
            r28 = r4
        L_0x08ea:
            r5 = r29
            goto L_0x12b6
        L_0x08ee:
            r1 = r4
            r6 = r1
        L_0x08f0:
            r2 = r52
            if (r2 < 0) goto L_0x09f9
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x09e6, all -> 0x09d9 }
            android.media.MediaFormat r5 = r5.getTrackFormat(r2)     // Catch:{ Exception -> 0x09e6, all -> 0x09d9 }
            java.lang.String r7 = r5.getString(r14)     // Catch:{ Exception -> 0x09e6, all -> 0x09d9 }
            java.lang.String r8 = "audio/mp4a-latm"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x09e6, all -> 0x09d9 }
            if (r7 != 0) goto L_0x0915
            java.lang.String r7 = r5.getString(r14)     // Catch:{ Exception -> 0x08e0, all -> 0x08d4 }
            java.lang.String r8 = "audio/mpeg"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x08e0, all -> 0x08d4 }
            if (r7 == 0) goto L_0x0913
            goto L_0x0915
        L_0x0913:
            r7 = 0
            goto L_0x0916
        L_0x0915:
            r7 = 1
        L_0x0916:
            java.lang.String r8 = r5.getString(r14)     // Catch:{ Exception -> 0x09e6, all -> 0x09d9 }
            java.lang.String r9 = "audio/unknown"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x09e6, all -> 0x09d9 }
            if (r8 == 0) goto L_0x0923
            r2 = -1
        L_0x0923:
            if (r2 < 0) goto L_0x09d0
            if (r7 == 0) goto L_0x0981
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x0975, all -> 0x0970 }
            r9 = 1
            int r8 = r8.addTrack(r5, r9)     // Catch:{ Exception -> 0x0975, all -> 0x0970 }
            android.media.MediaExtractor r10 = r15.extractor     // Catch:{ Exception -> 0x0975, all -> 0x0970 }
            r10.selectTrack(r2)     // Catch:{ Exception -> 0x0975, all -> 0x0970 }
            java.lang.String r10 = "max-input-size"
            int r5 = r5.getInteger(r10)     // Catch:{ Exception -> 0x093a, all -> 0x08d4 }
            goto L_0x0940
        L_0x093a:
            r0 = move-exception
            r5 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ Exception -> 0x0975, all -> 0x0970 }
            r5 = 0
        L_0x0940:
            if (r5 > 0) goto L_0x0944
            r5 = 65536(0x10000, float:9.18355E-41)
        L_0x0944:
            java.nio.ByteBuffer r10 = java.nio.ByteBuffer.allocateDirect(r5)     // Catch:{ Exception -> 0x0975, all -> 0x0970 }
            r87 = r5
            r86 = r10
            r4 = 0
            r9 = r82
            int r11 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r11 <= 0) goto L_0x095b
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x099a, all -> 0x08d4 }
            r14 = 0
            r11.seekTo(r9, r14)     // Catch:{ Exception -> 0x099a, all -> 0x08d4 }
            goto L_0x0961
        L_0x095b:
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x099a, all -> 0x08d4 }
            r14 = 0
            r11.seekTo(r4, r14)     // Catch:{ Exception -> 0x099a, all -> 0x08d4 }
        L_0x0961:
            r4 = r84
            r11 = r87
            r14 = 0
            r70 = r2
            r2 = r86
            r86 = r1
            r1 = r70
            goto L_0x0a05
        L_0x0970:
            r0 = move-exception
            r9 = r82
            goto L_0x08d5
        L_0x0975:
            r0 = move-exception
            r9 = r82
        L_0x0978:
            r8 = r79
            r6 = r80
            r34 = r84
            r2 = r0
            goto L_0x09f2
        L_0x0981:
            r9 = r82
            r8 = r5
            r4 = 0
            android.media.MediaExtractor r11 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x09ce, all -> 0x09cc }
            r11.<init>()     // Catch:{ Exception -> 0x09ce, all -> 0x09cc }
            r11.setDataSource(r12)     // Catch:{ Exception -> 0x09ce, all -> 0x09cc }
            r11.selectTrack(r2)     // Catch:{ Exception -> 0x09ce, all -> 0x09cc }
            int r14 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r14 <= 0) goto L_0x099c
            r14 = 0
            r11.seekTo(r9, r14)     // Catch:{ Exception -> 0x099a, all -> 0x08d4 }
            goto L_0x09a0
        L_0x099a:
            r0 = move-exception
            goto L_0x0978
        L_0x099c:
            r14 = 0
            r11.seekTo(r4, r14)     // Catch:{ Exception -> 0x09ce, all -> 0x09cc }
        L_0x09a0:
            org.telegram.messenger.video.AudioRecoder r14 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x09ce, all -> 0x09cc }
            r14.<init>(r8, r11, r2)     // Catch:{ Exception -> 0x09ce, all -> 0x09cc }
            r14.startTime = r9     // Catch:{ Exception -> 0x09be, all -> 0x09cc }
            r4 = r84
            r14.endTime = r4     // Catch:{ Exception -> 0x09bc, all -> 0x09ba }
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x09bc, all -> 0x09ba }
            android.media.MediaFormat r11 = r14.format     // Catch:{ Exception -> 0x09bc, all -> 0x09ba }
            r86 = r1
            r1 = 1
            int r8 = r8.addTrack(r11, r1)     // Catch:{ Exception -> 0x09bc, all -> 0x09ba }
            r1 = r2
            r2 = 0
            r11 = 0
            goto L_0x0a05
        L_0x09ba:
            r0 = move-exception
            goto L_0x09de
        L_0x09bc:
            r0 = move-exception
            goto L_0x09c1
        L_0x09be:
            r0 = move-exception
            r4 = r84
        L_0x09c1:
            r8 = r79
            r6 = r80
            r2 = r0
            r34 = r4
            r28 = r14
            goto L_0x08ea
        L_0x09cc:
            r0 = move-exception
            goto L_0x09dc
        L_0x09ce:
            r0 = move-exception
            goto L_0x09e9
        L_0x09d0:
            r9 = r82
            r4 = r84
            r86 = r1
            r1 = r2
            r2 = 0
            goto L_0x0a02
        L_0x09d9:
            r0 = move-exception
            r9 = r82
        L_0x09dc:
            r4 = r84
        L_0x09de:
            r12 = r77
            r11 = r78
            r8 = r79
            goto L_0x127d
        L_0x09e6:
            r0 = move-exception
            r9 = r82
        L_0x09e9:
            r4 = r84
            r8 = r79
            r6 = r80
            r2 = r0
            r34 = r4
        L_0x09f2:
            r5 = r29
            r1 = -5
            r28 = 0
            goto L_0x12df
        L_0x09f9:
            r9 = r82
            r4 = r84
            r86 = r1
            r1 = r2
            r2 = 0
            r7 = 1
        L_0x0a02:
            r8 = -5
            r11 = 0
            r14 = 0
        L_0x0a05:
            if (r1 >= 0) goto L_0x0a0a
            r23 = 1
            goto L_0x0a0c
        L_0x0a0a:
            r23 = 0
        L_0x0a0c:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x11e4, all -> 0x11df }
            r61 = r18
            r56 = r20
            r32 = r23
            r84 = -5
            r21 = 0
            r23 = 1
            r28 = 0
            r31 = 0
            r41 = 0
            r46 = 0
            r48 = 0
            r20 = r2
            r2 = r11
            r11 = r86
        L_0x0a2a:
            if (r28 == 0) goto L_0x0a45
            if (r7 != 0) goto L_0x0a31
            if (r32 != 0) goto L_0x0a31
            goto L_0x0a45
        L_0x0a31:
            r9 = r77
            r7 = r78
            r8 = r79
            r1 = r84
            r2 = r3
            r34 = r4
            r28 = r14
            r6 = 0
            r45 = 0
            r3 = r80
            goto L_0x1322
        L_0x0a45:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x11ca, all -> 0x11b8 }
            if (r7 != 0) goto L_0x0a75
            if (r14 == 0) goto L_0x0a75
            r85 = r11
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            boolean r11 = r14.step(r11, r8)     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            goto L_0x0a79
        L_0x0a55:
            r0 = move-exception
            r12 = r77
            r11 = r78
            r8 = r79
            r1 = r84
        L_0x0a5e:
            r2 = r0
            r34 = r4
        L_0x0a61:
            r9 = r29
            goto L_0x058a
        L_0x0a65:
            r0 = move-exception
            r8 = r79
            r6 = r80
            r1 = r84
            r2 = r0
            r34 = r4
        L_0x0a6f:
            r28 = r14
        L_0x0a71:
            r5 = r29
            goto L_0x12df
        L_0x0a75:
            r85 = r11
            r11 = r32
        L_0x0a79:
            if (r21 != 0) goto L_0x0bda
            r86 = r11
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x0bc8, all -> 0x0bbd }
            int r11 = r11.getSampleTrackIndex()     // Catch:{ Exception -> 0x0bc8, all -> 0x0bbd }
            r12 = r58
            if (r11 != r12) goto L_0x0ae4
            r58 = r12
            r44 = r13
            r12 = 2500(0x9c4, double:1.235E-320)
            int r11 = r3.dequeueInputBuffer(r12)     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            if (r11 < 0) goto L_0x0ad3
            int r12 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            r13 = 21
            if (r12 >= r13) goto L_0x0a9c
            r12 = r6[r11]     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            goto L_0x0aa0
        L_0x0a9c:
            java.nio.ByteBuffer r12 = r3.getInputBuffer(r11)     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
        L_0x0aa0:
            android.media.MediaExtractor r13 = r15.extractor     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            r87 = r6
            r6 = 0
            int r35 = r13.readSampleData(r12, r6)     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            if (r35 >= 0) goto L_0x0abc
            r34 = 0
            r35 = 0
            r36 = 0
            r38 = 4
            r32 = r3
            r33 = r11
            r32.queueInputBuffer(r33, r34, r35, r36, r38)     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            r6 = 1
            goto L_0x0ad7
        L_0x0abc:
            r34 = 0
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            long r36 = r6.getSampleTime()     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            r38 = 0
            r32 = r3
            r33 = r11
            r32.queueInputBuffer(r33, r34, r35, r36, r38)     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            r6.advance()     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            goto L_0x0ad5
        L_0x0ad3:
            r87 = r6
        L_0x0ad5:
            r6 = r21
        L_0x0ad7:
            r66 = r4
            r21 = r6
            r51 = r7
            r13 = r22
            r6 = 0
            r22 = r1
            goto L_0x0ba0
        L_0x0ae4:
            r87 = r6
            r58 = r12
            r44 = r13
            if (r7 == 0) goto L_0x0b92
            r6 = -1
            if (r1 == r6) goto L_0x0b92
            if (r11 != r1) goto L_0x0b92
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0bc8, all -> 0x0bbd }
            r12 = 28
            if (r11 < r12) goto L_0x0b0d
            android.media.MediaExtractor r12 = r15.extractor     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            long r12 = r12.getSampleSize()     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            r51 = r7
            long r6 = (long) r2     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            int r32 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r32 <= 0) goto L_0x0b0f
            r6 = 1024(0x400, double:5.06E-321)
            long r12 = r12 + r6
            int r2 = (int) r12     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            java.nio.ByteBuffer r20 = java.nio.ByteBuffer.allocateDirect(r2)     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            goto L_0x0b0f
        L_0x0b0d:
            r51 = r7
        L_0x0b0f:
            r6 = r20
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x0bc8, all -> 0x0bbd }
            r12 = 0
            int r7 = r7.readSampleData(r6, r12)     // Catch:{ Exception -> 0x0bc8, all -> 0x0bbd }
            r13 = r22
            r13.size = r7     // Catch:{ Exception -> 0x0bc8, all -> 0x0bbd }
            r7 = 21
            if (r11 >= r7) goto L_0x0b28
            r6.position(r12)     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            int r7 = r13.size     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            r6.limit(r7)     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
        L_0x0b28:
            int r7 = r13.size     // Catch:{ Exception -> 0x0bc8, all -> 0x0bbd }
            if (r7 < 0) goto L_0x0b3a
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            long r11 = r7.getSampleTime()     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            r13.presentationTimeUs = r11     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            r7.advance()     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            goto L_0x0b3f
        L_0x0b3a:
            r7 = 0
            r13.size = r7     // Catch:{ Exception -> 0x0bc8, all -> 0x0bbd }
            r21 = 1
        L_0x0b3f:
            int r7 = r13.size     // Catch:{ Exception -> 0x0bc8, all -> 0x0bbd }
            if (r7 <= 0) goto L_0x0b87
            r11 = 0
            int r7 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1))
            if (r7 < 0) goto L_0x0b4f
            long r11 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0a65, all -> 0x0a55 }
            int r7 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r7 >= 0) goto L_0x0b87
        L_0x0b4f:
            r7 = 0
            r13.offset = r7     // Catch:{ Exception -> 0x0bc8, all -> 0x0bbd }
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x0bc8, all -> 0x0bbd }
            int r11 = r11.getSampleFlags()     // Catch:{ Exception -> 0x0bc8, all -> 0x0bbd }
            r13.flags = r11     // Catch:{ Exception -> 0x0bc8, all -> 0x0bbd }
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x0bc8, all -> 0x0bbd }
            long r11 = r11.writeSampleData(r8, r6, r13, r7)     // Catch:{ Exception -> 0x0bc8, all -> 0x0bbd }
            r32 = 0
            int r7 = (r11 > r32 ? 1 : (r11 == r32 ? 0 : -1))
            if (r7 == 0) goto L_0x0b87
            org.telegram.messenger.MediaController$VideoConvertorListener r7 = r15.callback     // Catch:{ Exception -> 0x0bc8, all -> 0x0bbd }
            if (r7 == 0) goto L_0x0b87
            r22 = r1
            r20 = r2
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0bc8, all -> 0x0bbd }
            long r32 = r1 - r9
            int r34 = (r32 > r46 ? 1 : (r32 == r46 ? 0 : -1))
            if (r34 <= 0) goto L_0x0b78
            long r46 = r1 - r9
        L_0x0b78:
            r1 = r46
            r66 = r4
            float r4 = (float) r1
            float r4 = r4 / r24
            float r4 = r4 / r25
            r7.didWriteData(r11, r4)     // Catch:{ Exception -> 0x0bbb, all -> 0x0bb9 }
            r46 = r1
            goto L_0x0b8d
        L_0x0b87:
            r22 = r1
            r20 = r2
            r66 = r4
        L_0x0b8d:
            r2 = r20
            r20 = r6
            goto L_0x0b9f
        L_0x0b92:
            r66 = r4
            r51 = r7
            r13 = r22
            r22 = r1
            r1 = -1
            if (r11 != r1) goto L_0x0b9f
            r6 = 1
            goto L_0x0ba0
        L_0x0b9f:
            r6 = 0
        L_0x0ba0:
            if (r6 == 0) goto L_0x0be8
            r4 = 2500(0x9c4, double:1.235E-320)
            int r33 = r3.dequeueInputBuffer(r4)     // Catch:{ Exception -> 0x0bbb, all -> 0x0bb9 }
            if (r33 < 0) goto L_0x0be8
            r34 = 0
            r35 = 0
            r36 = 0
            r38 = 4
            r32 = r3
            r32.queueInputBuffer(r33, r34, r35, r36, r38)     // Catch:{ Exception -> 0x0bbb, all -> 0x0bb9 }
            r6 = 1
            goto L_0x0bea
        L_0x0bb9:
            r0 = move-exception
            goto L_0x0bc0
        L_0x0bbb:
            r0 = move-exception
            goto L_0x0bcb
        L_0x0bbd:
            r0 = move-exception
            r66 = r4
        L_0x0bc0:
            r12 = r77
            r11 = r78
            r8 = r79
            goto L_0x11c1
        L_0x0bc8:
            r0 = move-exception
            r66 = r4
        L_0x0bcb:
            r8 = r79
            r6 = r80
            r1 = r84
            r2 = r0
            r28 = r14
            r5 = r29
            r34 = r66
            goto L_0x12df
        L_0x0bda:
            r66 = r4
            r87 = r6
            r51 = r7
            r86 = r11
            r44 = r13
            r13 = r22
            r22 = r1
        L_0x0be8:
            r6 = r21
        L_0x0bea:
            r1 = r31 ^ 1
            r11 = r85
            r7 = r1
            r21 = r6
            r68 = r56
            r4 = r66
            r6 = 1
            r1 = r84
        L_0x0bf8:
            if (r7 != 0) goto L_0x0CLASSNAME
            if (r6 == 0) goto L_0x0bfd
            goto L_0x0CLASSNAME
        L_0x0bfd:
            r12 = r73
            r32 = r86
            r6 = r87
            r84 = r1
            r1 = r22
            r7 = r51
            r56 = r68
            r22 = r13
            r13 = r44
            goto L_0x0a2a
        L_0x0CLASSNAME:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x11a7, all -> 0x119b }
            if (r91 == 0) goto L_0x0CLASSNAME
            r32 = 22000(0x55f0, double:1.08694E-319)
            r12 = r80
            r84 = r6
            r85 = r7
            r6 = r32
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r12 = r80
            r84 = r6
            r85 = r7
            r6 = 2500(0x9c4, double:1.235E-320)
        L_0x0CLASSNAME:
            int r6 = r12.dequeueOutputBuffer(r13, r6)     // Catch:{ Exception -> 0x1197, all -> 0x119b }
            r7 = -1
            if (r6 != r7) goto L_0x0c4f
            r84 = r1
            r80 = r2
            r54 = r3
            r34 = r4
            r5 = r6
            r32 = r8
            r7 = r44
            r2 = r55
            r3 = r59
            r4 = r60
            r8 = r65
            r1 = -1
            r6 = 0
        L_0x0CLASSNAME:
            r70 = r28
            r28 = r14
            r14 = r70
            goto L_0x0e92
        L_0x0c4f:
            r7 = -3
            if (r6 != r7) goto L_0x0CLASSNAME
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c7e, all -> 0x0CLASSNAME }
            r80 = r2
            r2 = 21
            if (r7 >= r2) goto L_0x0c5e
            java.nio.ByteBuffer[] r11 = r12.getOutputBuffers()     // Catch:{ Exception -> 0x0c7e, all -> 0x0CLASSNAME }
        L_0x0c5e:
            r54 = r3
            r34 = r4
            r5 = r6
            r32 = r8
            r7 = r44
            r2 = r55
            r3 = r59
            r4 = r60
            r8 = r65
        L_0x0c6f:
            r6 = r84
            r84 = r1
            r1 = -1
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = move-exception
        L_0x0CLASSNAME:
            r12 = r77
            r11 = r78
            r8 = r79
            goto L_0x0a5e
        L_0x0c7e:
            r0 = move-exception
        L_0x0c7f:
            r8 = r79
            r2 = r0
            r34 = r4
            r6 = r12
            goto L_0x0a6f
        L_0x0CLASSNAME:
            r80 = r2
            r2 = -2
            if (r6 != r2) goto L_0x0d0f
            android.media.MediaFormat r2 = r12.getOutputFormat()     // Catch:{ Exception -> 0x0c7e, all -> 0x0CLASSNAME }
            r7 = -5
            if (r1 != r7) goto L_0x0cfc
            if (r2 == 0) goto L_0x0cfc
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ Exception -> 0x0c7e, all -> 0x0CLASSNAME }
            r32 = r8
            r8 = 0
            int r1 = r7.addTrack(r2, r8)     // Catch:{ Exception -> 0x0c7e, all -> 0x0CLASSNAME }
            r7 = r63
            boolean r8 = r2.containsKey(r7)     // Catch:{ Exception -> 0x0cf8, all -> 0x0cf3 }
            if (r8 == 0) goto L_0x0ce8
            int r8 = r2.getInteger(r7)     // Catch:{ Exception -> 0x0cf8, all -> 0x0cf3 }
            r33 = r1
            r1 = 1
            if (r8 != r1) goto L_0x0cea
            r8 = r65
            java.nio.ByteBuffer r1 = r2.getByteBuffer(r8)     // Catch:{ Exception -> 0x0cd9, all -> 0x0cc9 }
            r63 = r7
            r7 = r44
            java.nio.ByteBuffer r2 = r2.getByteBuffer(r7)     // Catch:{ Exception -> 0x0cd9, all -> 0x0cc9 }
            int r1 = r1.limit()     // Catch:{ Exception -> 0x0cd9, all -> 0x0cc9 }
            int r2 = r2.limit()     // Catch:{ Exception -> 0x0cd9, all -> 0x0cc9 }
            int r1 = r1 + r2
            r41 = r1
            goto L_0x0cf0
        L_0x0cc9:
            r0 = move-exception
            r12 = r77
            r11 = r78
            r8 = r79
            r2 = r0
            r34 = r4
            r9 = r29
            r1 = r33
            goto L_0x058a
        L_0x0cd9:
            r0 = move-exception
            r8 = r79
            r2 = r0
            r34 = r4
            r6 = r12
            r28 = r14
            r5 = r29
            r1 = r33
            goto L_0x12df
        L_0x0ce8:
            r33 = r1
        L_0x0cea:
            r63 = r7
            r7 = r44
            r8 = r65
        L_0x0cf0:
            r1 = r33
            goto L_0x0d02
        L_0x0cf3:
            r0 = move-exception
            r33 = r1
            goto L_0x0CLASSNAME
        L_0x0cf8:
            r0 = move-exception
            r33 = r1
            goto L_0x0c7f
        L_0x0cfc:
            r32 = r8
            r7 = r44
            r8 = r65
        L_0x0d02:
            r54 = r3
            r34 = r4
            r5 = r6
            r2 = r55
            r3 = r59
            r4 = r60
            goto L_0x0c6f
        L_0x0d0f:
            r32 = r8
            r7 = r44
            r8 = r65
            if (r6 < 0) goto L_0x116f
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1197, all -> 0x119b }
            r28 = r14
            r14 = 21
            if (r2 >= r14) goto L_0x0d2a
            r2 = r11[r6]     // Catch:{ Exception -> 0x0d22, all -> 0x0CLASSNAME }
            goto L_0x0d2e
        L_0x0d22:
            r0 = move-exception
            r8 = r79
            r2 = r0
            r34 = r4
            goto L_0x10f1
        L_0x0d2a:
            java.nio.ByteBuffer r2 = r12.getOutputBuffer(r6)     // Catch:{ Exception -> 0x1168, all -> 0x119b }
        L_0x0d2e:
            if (r2 == 0) goto L_0x1144
            int r14 = r13.size     // Catch:{ Exception -> 0x113a, all -> 0x119b }
            r33 = r11
            r11 = 1
            if (r14 <= r11) goto L_0x0e74
            int r11 = r13.flags     // Catch:{ Exception -> 0x0e6b, all -> 0x0e60 }
            r34 = r11 & 2
            if (r34 != 0) goto L_0x0ddb
            if (r41 == 0) goto L_0x0d53
            r34 = r11 & 1
            if (r34 == 0) goto L_0x0d53
            r34 = r4
            int r4 = r13.offset     // Catch:{ Exception -> 0x0d50, all -> 0x0e53 }
            int r4 = r4 + r41
            r13.offset = r4     // Catch:{ Exception -> 0x0d50, all -> 0x0e53 }
            int r14 = r14 - r41
            r13.size = r14     // Catch:{ Exception -> 0x0d50, all -> 0x0e53 }
            goto L_0x0d55
        L_0x0d50:
            r0 = move-exception
            goto L_0x0e70
        L_0x0d53:
            r34 = r4
        L_0x0d55:
            if (r23 == 0) goto L_0x0da4
            r4 = r11 & 1
            if (r4 == 0) goto L_0x0da4
            int r4 = r13.size     // Catch:{ Exception -> 0x0d50, all -> 0x0e53 }
            r5 = 100
            if (r4 <= r5) goto L_0x0da2
            int r4 = r13.offset     // Catch:{ Exception -> 0x0d50, all -> 0x0e53 }
            r2.position(r4)     // Catch:{ Exception -> 0x0d50, all -> 0x0e53 }
            byte[] r4 = new byte[r5]     // Catch:{ Exception -> 0x0d50, all -> 0x0e53 }
            r2.get(r4)     // Catch:{ Exception -> 0x0d50, all -> 0x0e53 }
            r11 = 0
            r14 = 0
        L_0x0d6d:
            r5 = 96
            if (r11 >= r5) goto L_0x0da2
            byte r5 = r4[r11]     // Catch:{ Exception -> 0x0d50, all -> 0x0e53 }
            if (r5 != 0) goto L_0x0d99
            int r5 = r11 + 1
            byte r5 = r4[r5]     // Catch:{ Exception -> 0x0d50, all -> 0x0e53 }
            if (r5 != 0) goto L_0x0d99
            int r5 = r11 + 2
            byte r5 = r4[r5]     // Catch:{ Exception -> 0x0d50, all -> 0x0e53 }
            if (r5 != 0) goto L_0x0d99
            int r5 = r11 + 3
            byte r5 = r4[r5]     // Catch:{ Exception -> 0x0d50, all -> 0x0e53 }
            r23 = r4
            r4 = 1
            if (r5 != r4) goto L_0x0d9b
            int r14 = r14 + 1
            if (r14 <= r4) goto L_0x0d9b
            int r4 = r13.offset     // Catch:{ Exception -> 0x0d50, all -> 0x0e53 }
            int r4 = r4 + r11
            r13.offset = r4     // Catch:{ Exception -> 0x0d50, all -> 0x0e53 }
            int r4 = r13.size     // Catch:{ Exception -> 0x0d50, all -> 0x0e53 }
            int r4 = r4 - r11
            r13.size = r4     // Catch:{ Exception -> 0x0d50, all -> 0x0e53 }
            goto L_0x0da2
        L_0x0d99:
            r23 = r4
        L_0x0d9b:
            int r11 = r11 + 1
            r4 = r23
            r5 = 100
            goto L_0x0d6d
        L_0x0da2:
            r23 = 0
        L_0x0da4:
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x0dd6, all -> 0x0e53 }
            r54 = r3
            r5 = 1
            long r2 = r4.writeSampleData(r1, r2, r13, r5)     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            r4 = 0
            int r11 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r11 == 0) goto L_0x0e78
            org.telegram.messenger.MediaController$VideoConvertorListener r4 = r15.callback     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            if (r4 == 0) goto L_0x0e78
            r11 = r6
            long r5 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            long r36 = r5 - r9
            int r14 = (r36 > r46 ? 1 : (r36 == r46 ? 0 : -1))
            if (r14 <= 0) goto L_0x0dc2
            long r46 = r5 - r9
        L_0x0dc2:
            r5 = r46
            float r14 = (float) r5     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            float r14 = r14 / r24
            float r14 = r14 / r25
            r4.didWriteData(r2, r14)     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            r46 = r5
        L_0x0dce:
            r2 = r55
            r3 = r59
            r4 = r60
            goto L_0x0e7b
        L_0x0dd6:
            r0 = move-exception
            r54 = r3
            goto L_0x0e70
        L_0x0ddb:
            r54 = r3
            r34 = r4
            r11 = r6
            r3 = -5
            if (r1 != r3) goto L_0x0dce
            byte[] r3 = new byte[r14]     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            int r4 = r13.offset     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            int r4 = r4 + r14
            r2.limit(r4)     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            int r4 = r13.offset     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            r2.position(r4)     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            r2.get(r3)     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            int r2 = r13.size     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            r6 = 1
            int r2 = r2 - r6
        L_0x0df7:
            if (r2 < 0) goto L_0x0e35
            r4 = 3
            if (r2 <= r4) goto L_0x0e35
            byte r5 = r3[r2]     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            if (r5 != r6) goto L_0x0e31
            int r5 = r2 + -1
            byte r5 = r3[r5]     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            if (r5 != 0) goto L_0x0e31
            int r5 = r2 + -2
            byte r5 = r3[r5]     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            if (r5 != 0) goto L_0x0e31
            int r5 = r2 + -3
            byte r14 = r3[r5]     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            if (r14 != 0) goto L_0x0e31
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r5)     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            int r14 = r13.size     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            int r14 = r14 - r5
            java.nio.ByteBuffer r14 = java.nio.ByteBuffer.allocate(r14)     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            r4 = 0
            java.nio.ByteBuffer r6 = r2.put(r3, r4, r5)     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            r6.position(r4)     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            int r6 = r13.size     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            int r6 = r6 - r5
            java.nio.ByteBuffer r3 = r14.put(r3, r5, r6)     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            r3.position(r4)     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            r6 = r2
            goto L_0x0e37
        L_0x0e31:
            int r2 = r2 + -1
            r6 = 1
            goto L_0x0df7
        L_0x0e35:
            r6 = 0
            r14 = 0
        L_0x0e37:
            r2 = r55
            r3 = r59
            r4 = r60
            android.media.MediaFormat r5 = android.media.MediaFormat.createVideoFormat(r4, r2, r3)     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            if (r6 == 0) goto L_0x0e4b
            if (r14 == 0) goto L_0x0e4b
            r5.setByteBuffer(r8, r6)     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            r5.setByteBuffer(r7, r14)     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
        L_0x0e4b:
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            r14 = 0
            int r1 = r6.addTrack(r5, r14)     // Catch:{ Exception -> 0x0e55, all -> 0x0e53 }
            goto L_0x0e7b
        L_0x0e53:
            r0 = move-exception
            goto L_0x0e63
        L_0x0e55:
            r0 = move-exception
            r8 = r79
            r2 = r0
            r6 = r12
            r5 = r29
            r3 = r54
            goto L_0x12df
        L_0x0e60:
            r0 = move-exception
            r34 = r4
        L_0x0e63:
            r12 = r77
            r11 = r78
            r8 = r79
            goto L_0x11a4
        L_0x0e6b:
            r0 = move-exception
            r54 = r3
            r34 = r4
        L_0x0e70:
            r8 = r79
            goto L_0x1142
        L_0x0e74:
            r54 = r3
            r34 = r4
        L_0x0e78:
            r11 = r6
            goto L_0x0dce
        L_0x0e7b:
            int r5 = r13.flags     // Catch:{ Exception -> 0x1131, all -> 0x112b }
            r5 = r5 & 4
            if (r5 == 0) goto L_0x0e84
            r5 = r11
            r6 = 1
            goto L_0x0e86
        L_0x0e84:
            r5 = r11
            r6 = 0
        L_0x0e86:
            r11 = 0
            r12.releaseOutputBuffer(r5, r11)     // Catch:{ Exception -> 0x1131, all -> 0x112b }
            r14 = r6
            r11 = r33
            r6 = r84
            r84 = r1
            r1 = -1
        L_0x0e92:
            if (r5 == r1) goto L_0x0eb4
            r1 = r84
            r55 = r2
            r59 = r3
            r60 = r4
            r44 = r7
            r65 = r8
            r8 = r32
            r4 = r34
            r3 = r54
            r2 = r80
            r7 = r85
            r80 = r12
        L_0x0eac:
            r70 = r28
            r28 = r14
            r14 = r70
            goto L_0x0bf8
        L_0x0eb4:
            if (r31 != 0) goto L_0x10f4
            r55 = r2
            r59 = r3
            r5 = r54
            r1 = 2500(0x9c4, double:1.235E-320)
            int r3 = r5.dequeueOutputBuffer(r13, r1)     // Catch:{ Exception -> 0x10e8, all -> 0x10dd }
            r1 = -1
            if (r3 != r1) goto L_0x0ed6
            r60 = r4
            r52 = r6
            r50 = r7
            r65 = r8
            r9 = r53
            r1 = 0
            r7 = 0
            r8 = r79
            goto L_0x110e
        L_0x0ed6:
            r2 = -3
            if (r3 != r2) goto L_0x0ee5
        L_0x0ed9:
            r60 = r4
            r52 = r6
            r50 = r7
            r65 = r8
            r9 = r53
            goto L_0x1104
        L_0x0ee5:
            r2 = -2
            if (r3 != r2) goto L_0x0var_
            android.media.MediaFormat r2 = r5.getOutputFormat()     // Catch:{ Exception -> 0x0f0e, all -> 0x0var_ }
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0f0e, all -> 0x0var_ }
            if (r3 == 0) goto L_0x0ed9
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0f0e, all -> 0x0var_ }
            r3.<init>()     // Catch:{ Exception -> 0x0f0e, all -> 0x0var_ }
            java.lang.String r1 = "newFormat = "
            r3.append(r1)     // Catch:{ Exception -> 0x0f0e, all -> 0x0var_ }
            r3.append(r2)     // Catch:{ Exception -> 0x0f0e, all -> 0x0var_ }
            java.lang.String r1 = r3.toString()     // Catch:{ Exception -> 0x0f0e, all -> 0x0var_ }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0f0e, all -> 0x0var_ }
            goto L_0x0ed9
        L_0x0var_:
            r0 = move-exception
            r12 = r77
            r11 = r78
            r8 = r79
            goto L_0x10e4
        L_0x0f0e:
            r0 = move-exception
            r8 = r79
            goto L_0x10ed
        L_0x0var_:
            if (r3 < 0) goto L_0x10b8
            int r1 = r13.size     // Catch:{ Exception -> 0x10e8, all -> 0x10dd }
            if (r1 == 0) goto L_0x0f1c
            r33 = 1
            goto L_0x0f1e
        L_0x0f1c:
            r33 = 0
        L_0x0f1e:
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x10e8, all -> 0x10dd }
            r36 = 0
            int r50 = (r34 > r36 ? 1 : (r34 == r36 ? 0 : -1))
            if (r50 <= 0) goto L_0x0var_
            int r36 = (r1 > r34 ? 1 : (r1 == r34 ? 0 : -1))
            if (r36 < 0) goto L_0x0var_
            r60 = r4
            int r4 = r13.flags     // Catch:{ Exception -> 0x0f0e, all -> 0x0var_ }
            r4 = r4 | 4
            r13.flags = r4     // Catch:{ Exception -> 0x0f0e, all -> 0x0var_ }
            r21 = 1
            r31 = 1
            r33 = 0
            goto L_0x0f3b
        L_0x0var_:
            r60 = r4
        L_0x0f3b:
            r36 = 0
            int r4 = (r26 > r36 ? 1 : (r26 == r36 ? 0 : -1))
            if (r4 < 0) goto L_0x0fbe
            int r4 = r13.flags     // Catch:{ Exception -> 0x0f0e, all -> 0x0fb8 }
            r4 = r4 & 4
            if (r4 == 0) goto L_0x0fbe
            long r36 = r26 - r9
            long r36 = java.lang.Math.abs(r36)     // Catch:{ Exception -> 0x0f0e, all -> 0x0fb8 }
            r4 = 1000000(0xvar_, float:1.401298E-39)
            r65 = r8
            r8 = r79
            int r4 = r4 / r8
            r52 = r6
            r50 = r7
            long r6 = (long) r4
            int r4 = (r36 > r6 ? 1 : (r36 == r6 ? 0 : -1))
            if (r4 <= 0) goto L_0x0fae
            r6 = 0
            int r4 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r4 <= 0) goto L_0x0f6e
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x0fb5, all -> 0x10d3 }
            r6 = 0
            r4.seekTo(r9, r6)     // Catch:{ Exception -> 0x0fb5, all -> 0x10d3 }
            r6 = r68
            r9 = 0
            goto L_0x0var_
        L_0x0f6e:
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x0fb5, all -> 0x0fb3 }
            r6 = 0
            r9 = 0
            r4.seekTo(r6, r9)     // Catch:{ Exception -> 0x0fb5, all -> 0x10d3 }
            r6 = r68
        L_0x0var_:
            long r48 = r6 + r39
            int r4 = r13.flags     // Catch:{ Exception -> 0x0fa0, all -> 0x0f8f }
            r10 = -5
            r4 = r4 & r10
            r13.flags = r4     // Catch:{ Exception -> 0x0fa0, all -> 0x0f8f }
            r5.flush()     // Catch:{ Exception -> 0x0fa0, all -> 0x0f8f }
            r33 = r18
            r4 = 1
            r21 = 0
            r31 = 0
            r35 = 0
            r36 = 0
            goto L_0x0fd5
        L_0x0f8f:
            r0 = move-exception
            r12 = r77
            r11 = r78
            r1 = r84
            r2 = r0
            r34 = r26
            r9 = r29
            r7 = 0
            r26 = r18
            goto L_0x13df
        L_0x0fa0:
            r0 = move-exception
            r1 = r84
            r2 = r0
            r3 = r5
            r6 = r12
            r34 = r26
            r5 = r29
            r26 = r18
            goto L_0x12df
        L_0x0fae:
            r6 = r68
            r9 = 0
            r10 = -5
            goto L_0x0fca
        L_0x0fb3:
            r0 = move-exception
            goto L_0x0fbb
        L_0x0fb5:
            r0 = move-exception
            goto L_0x10ed
        L_0x0fb8:
            r0 = move-exception
            r8 = r79
        L_0x0fbb:
            r9 = 0
            goto L_0x10e0
        L_0x0fbe:
            r52 = r6
            r50 = r7
            r65 = r8
            r6 = r68
            r9 = 0
            r10 = -5
            r8 = r79
        L_0x0fca:
            r4 = 0
            r36 = 0
            r70 = r34
            r35 = r33
            r33 = r26
            r26 = r70
        L_0x0fd5:
            int r38 = (r33 > r36 ? 1 : (r33 == r36 ? 0 : -1))
            if (r38 < 0) goto L_0x0fdc
            r9 = r33
            goto L_0x0fde
        L_0x0fdc:
            r9 = r82
        L_0x0fde:
            int r54 = (r9 > r36 ? 1 : (r9 == r36 ? 0 : -1))
            if (r54 <= 0) goto L_0x101f
            int r36 = (r61 > r18 ? 1 : (r61 == r18 ? 0 : -1))
            if (r36 != 0) goto L_0x101f
            int r36 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r36 >= 0) goto L_0x100e
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x101c, all -> 0x1093 }
            if (r1 == 0) goto L_0x100c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x101c, all -> 0x1093 }
            r1.<init>()     // Catch:{ Exception -> 0x101c, all -> 0x1093 }
            java.lang.String r2 = "drop frame startTime = "
            r1.append(r2)     // Catch:{ Exception -> 0x101c, all -> 0x1093 }
            r1.append(r9)     // Catch:{ Exception -> 0x101c, all -> 0x1093 }
            java.lang.String r2 = " present time = "
            r1.append(r2)     // Catch:{ Exception -> 0x101c, all -> 0x1093 }
            long r9 = r13.presentationTimeUs     // Catch:{ Exception -> 0x101c, all -> 0x1093 }
            r1.append(r9)     // Catch:{ Exception -> 0x101c, all -> 0x1093 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x101c, all -> 0x1093 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x101c, all -> 0x1093 }
        L_0x100c:
            r1 = 0
            goto L_0x1021
        L_0x100e:
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x101c, all -> 0x1093 }
            r9 = -2147483648(0xfffffffvar_, double:NaN)
            int r36 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r36 == 0) goto L_0x1019
            long r48 = r48 - r1
        L_0x1019:
            r61 = r1
            goto L_0x101f
        L_0x101c:
            r0 = move-exception
            goto L_0x10a9
        L_0x101f:
            r1 = r35
        L_0x1021:
            if (r4 == 0) goto L_0x1026
            r61 = r18
            goto L_0x1039
        L_0x1026:
            int r2 = (r33 > r18 ? 1 : (r33 == r18 ? 0 : -1))
            if (r2 != 0) goto L_0x1036
            r9 = 0
            int r2 = (r48 > r9 ? 1 : (r48 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x1036
            long r9 = r13.presentationTimeUs     // Catch:{ Exception -> 0x101c, all -> 0x1093 }
            long r9 = r9 + r48
            r13.presentationTimeUs = r9     // Catch:{ Exception -> 0x101c, all -> 0x1093 }
        L_0x1036:
            r5.releaseOutputBuffer(r3, r1)     // Catch:{ Exception -> 0x10a6, all -> 0x1093 }
        L_0x1039:
            if (r1 == 0) goto L_0x106c
            r1 = 0
            int r3 = (r33 > r1 ? 1 : (r33 == r1 ? 0 : -1))
            if (r3 < 0) goto L_0x1048
            long r3 = r13.presentationTimeUs     // Catch:{ Exception -> 0x101c, all -> 0x1093 }
            long r68 = java.lang.Math.max(r6, r3)     // Catch:{ Exception -> 0x101c, all -> 0x1093 }
            goto L_0x104a
        L_0x1048:
            r68 = r6
        L_0x104a:
            r30.awaitNewImage()     // Catch:{ Exception -> 0x104f, all -> 0x1093 }
            r6 = 0
            goto L_0x1055
        L_0x104f:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)     // Catch:{ Exception -> 0x10a6, all -> 0x1093 }
            r6 = 1
        L_0x1055:
            if (r6 != 0) goto L_0x1069
            r30.drawImage()     // Catch:{ Exception -> 0x10a6, all -> 0x1093 }
            long r3 = r13.presentationTimeUs     // Catch:{ Exception -> 0x10a6, all -> 0x1093 }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r3 = r3 * r6
            r9 = r53
            r9.setPresentationTime(r3)     // Catch:{ Exception -> 0x108b, all -> 0x1093 }
            r9.swapBuffers()     // Catch:{ Exception -> 0x108b, all -> 0x1093 }
            goto L_0x1072
        L_0x1069:
            r9 = r53
            goto L_0x1072
        L_0x106c:
            r9 = r53
            r1 = 0
            r68 = r6
        L_0x1072:
            int r3 = r13.flags     // Catch:{ Exception -> 0x108b, all -> 0x1093 }
            r3 = r3 & 4
            if (r3 == 0) goto L_0x1087
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x108b, all -> 0x1093 }
            if (r3 == 0) goto L_0x1081
            java.lang.String r3 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x108b, all -> 0x1093 }
        L_0x1081:
            r12.signalEndOfInputStream()     // Catch:{ Exception -> 0x108b, all -> 0x1093 }
            r7 = 0
            goto L_0x1114
        L_0x1087:
            r7 = r85
            goto L_0x1114
        L_0x108b:
            r0 = move-exception
            r1 = r84
            r2 = r0
            r3 = r5
            r53 = r9
            goto L_0x10ad
        L_0x1093:
            r0 = move-exception
            r12 = r77
            r11 = r78
            r1 = r84
            r2 = r0
            r9 = r29
            r7 = 0
            r70 = r26
            r26 = r33
            r34 = r70
            goto L_0x13df
        L_0x10a6:
            r0 = move-exception
            r9 = r53
        L_0x10a9:
            r1 = r84
            r2 = r0
            r3 = r5
        L_0x10ad:
            r6 = r12
            r5 = r29
            r70 = r26
            r26 = r33
            r34 = r70
            goto L_0x12df
        L_0x10b8:
            r8 = r79
            r9 = r53
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x10d5, all -> 0x10d3 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x10d5, all -> 0x10d3 }
            r2.<init>()     // Catch:{ Exception -> 0x10d5, all -> 0x10d3 }
            java.lang.String r4 = "unexpected result from decoder.dequeueOutputBuffer: "
            r2.append(r4)     // Catch:{ Exception -> 0x10d5, all -> 0x10d3 }
            r2.append(r3)     // Catch:{ Exception -> 0x10d5, all -> 0x10d3 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x10d5, all -> 0x10d3 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x10d5, all -> 0x10d3 }
            throw r1     // Catch:{ Exception -> 0x10d5, all -> 0x10d3 }
        L_0x10d3:
            r0 = move-exception
            goto L_0x10e0
        L_0x10d5:
            r0 = move-exception
            r1 = r84
            r2 = r0
            r3 = r5
            r53 = r9
            goto L_0x10f1
        L_0x10dd:
            r0 = move-exception
            r8 = r79
        L_0x10e0:
            r12 = r77
            r11 = r78
        L_0x10e4:
            r1 = r84
            goto L_0x11a4
        L_0x10e8:
            r0 = move-exception
            r8 = r79
            r9 = r53
        L_0x10ed:
            r1 = r84
        L_0x10ef:
            r2 = r0
            r3 = r5
        L_0x10f1:
            r6 = r12
            goto L_0x0a71
        L_0x10f4:
            r55 = r2
            r59 = r3
            r60 = r4
            r52 = r6
            r50 = r7
            r65 = r8
            r9 = r53
            r5 = r54
        L_0x1104:
            r6 = r68
            r1 = 0
            r8 = r79
            r68 = r6
            r7 = r85
        L_0x110e:
            r70 = r26
            r26 = r34
            r33 = r70
        L_0x1114:
            r2 = r80
            r1 = r84
            r3 = r5
            r53 = r9
            r80 = r12
            r4 = r26
            r8 = r32
            r26 = r33
            r44 = r50
            r6 = r52
            r9 = r82
            goto L_0x0eac
        L_0x112b:
            r0 = move-exception
            r8 = r79
            r3 = r1
            goto L_0x11a0
        L_0x1131:
            r0 = move-exception
            r8 = r79
            r3 = r1
            r9 = r53
            r5 = r54
            goto L_0x10ef
        L_0x113a:
            r0 = move-exception
            r8 = r79
            r34 = r4
            r9 = r53
            r5 = r3
        L_0x1142:
            r2 = r0
            goto L_0x10f1
        L_0x1144:
            r8 = r79
            r2 = r3
            r34 = r4
            r5 = r6
            r9 = r53
            java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1192, all -> 0x1190 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1192, all -> 0x1190 }
            r4.<init>()     // Catch:{ Exception -> 0x1192, all -> 0x1190 }
            java.lang.String r6 = "encoderOutputBuffer "
            r4.append(r6)     // Catch:{ Exception -> 0x1192, all -> 0x1190 }
            r4.append(r5)     // Catch:{ Exception -> 0x1192, all -> 0x1190 }
            java.lang.String r5 = " was null"
            r4.append(r5)     // Catch:{ Exception -> 0x1192, all -> 0x1190 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x1192, all -> 0x1190 }
            r3.<init>(r4)     // Catch:{ Exception -> 0x1192, all -> 0x1190 }
            throw r3     // Catch:{ Exception -> 0x1192, all -> 0x1190 }
        L_0x1168:
            r0 = move-exception
            r8 = r79
            r2 = r3
            r34 = r4
            goto L_0x11b1
        L_0x116f:
            r8 = r79
            r2 = r3
            r34 = r4
            r5 = r6
            r28 = r14
            r9 = r53
            java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1192, all -> 0x1190 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1192, all -> 0x1190 }
            r4.<init>()     // Catch:{ Exception -> 0x1192, all -> 0x1190 }
            java.lang.String r6 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r6)     // Catch:{ Exception -> 0x1192, all -> 0x1190 }
            r4.append(r5)     // Catch:{ Exception -> 0x1192, all -> 0x1190 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x1192, all -> 0x1190 }
            r3.<init>(r4)     // Catch:{ Exception -> 0x1192, all -> 0x1190 }
            throw r3     // Catch:{ Exception -> 0x1192, all -> 0x1190 }
        L_0x1190:
            r0 = move-exception
            goto L_0x11a0
        L_0x1192:
            r0 = move-exception
            r3 = r2
            r53 = r9
            goto L_0x11b3
        L_0x1197:
            r0 = move-exception
            r8 = r79
            goto L_0x11ac
        L_0x119b:
            r0 = move-exception
            r8 = r79
            r34 = r4
        L_0x11a0:
            r12 = r77
            r11 = r78
        L_0x11a4:
            r2 = r0
            goto L_0x0a61
        L_0x11a7:
            r0 = move-exception
            r8 = r79
            r12 = r80
        L_0x11ac:
            r2 = r3
            r34 = r4
            r28 = r14
        L_0x11b1:
            r9 = r53
        L_0x11b3:
            r6 = r12
            r5 = r29
            goto L_0x077f
        L_0x11b8:
            r0 = move-exception
            r8 = r79
            r66 = r4
            r12 = r77
            r11 = r78
        L_0x11c1:
            r1 = r84
            r2 = r0
            r9 = r29
            r34 = r66
            goto L_0x058a
        L_0x11ca:
            r0 = move-exception
            r8 = r79
            r12 = r80
            r2 = r3
            r66 = r4
            r28 = r14
            r9 = r53
            r1 = r84
            r6 = r12
            r5 = r29
            r34 = r66
            goto L_0x077f
        L_0x11df:
            r0 = move-exception
            r8 = r79
            goto L_0x1279
        L_0x11e4:
            r0 = move-exception
            r8 = r79
            r12 = r80
            r2 = r3
            r28 = r14
            r9 = r53
            r34 = r4
            r6 = r12
            r5 = r29
            r1 = -5
            goto L_0x077f
        L_0x11f6:
            r0 = move-exception
            r8 = r79
            r12 = r80
            r4 = r84
            r2 = r3
            r9 = r53
            goto L_0x120d
        L_0x1201:
            r0 = move-exception
            r8 = r79
            r12 = r80
            r4 = r84
            r9 = r53
            r2 = r54
            r3 = r2
        L_0x120d:
            r34 = r4
            r6 = r12
            r5 = r29
            r1 = -5
            r28 = 0
            goto L_0x077f
        L_0x1217:
            r0 = move-exception
            r8 = r79
            r4 = r84
            goto L_0x1279
        L_0x121e:
            r0 = move-exception
            r8 = r79
            r12 = r80
            r4 = r84
            r9 = r53
            r2 = r54
            r3 = r2
            r34 = r4
            goto L_0x123c
        L_0x122d:
            r0 = move-exception
            r4 = r84
            r26 = r86
            r12 = r8
            r8 = r9
            r58 = r23
            r9 = r1
            r3 = r2
            r34 = r4
            r53 = r9
        L_0x123c:
            r6 = r12
            r5 = r29
            r1 = -5
            r28 = 0
            r30 = 0
            goto L_0x077f
        L_0x1246:
            r0 = move-exception
            r4 = r84
            r26 = r86
            r12 = r8
            r8 = r9
            r58 = r23
            r9 = r1
            r2 = r0
            r34 = r4
            r53 = r9
            r6 = r12
            r5 = r29
            r1 = -5
            r3 = 0
            r28 = 0
            r30 = 0
            goto L_0x12df
        L_0x1260:
            r0 = move-exception
            r4 = r84
            r26 = r86
            r12 = r8
            r8 = r9
            r58 = r23
            r2 = r0
            r34 = r4
            r6 = r12
            r5 = r29
            r1 = -5
            r3 = 0
            goto L_0x0737
        L_0x1273:
            r0 = move-exception
            r4 = r84
            r26 = r86
            r8 = r9
        L_0x1279:
            r12 = r77
            r11 = r78
        L_0x127d:
            r2 = r0
            r34 = r4
        L_0x1280:
            r9 = r29
            goto L_0x0589
        L_0x1284:
            r0 = move-exception
            r4 = r84
            r26 = r86
            r8 = r9
            r58 = r23
            r2 = r0
            r34 = r4
            r5 = r29
            goto L_0x0734
        L_0x1293:
            r0 = move-exception
            r26 = r86
            r1 = r5
            r8 = r9
            r4 = r84
            r12 = r77
            r11 = r78
            r2 = r0
            r9 = r1
            goto L_0x12c5
        L_0x12a1:
            r0 = move-exception
            r26 = r86
            r1 = r5
            r8 = r9
            r58 = r23
            r4 = r84
            r2 = r0
            r34 = r4
            r3 = 0
            r6 = 0
            r28 = 0
            r30 = 0
            r53 = 0
            r5 = r1
        L_0x12b6:
            r1 = -5
            goto L_0x12df
        L_0x12b8:
            r0 = move-exception
            r4 = r84
            r8 = r9
            r12 = r77
            r11 = r78
            r9 = r80
            r26 = r86
            r2 = r0
        L_0x12c5:
            r34 = r4
            goto L_0x0589
        L_0x12c9:
            r0 = move-exception
            r4 = r84
            r58 = r3
            r8 = r9
            r26 = r86
            r2 = r0
            r34 = r4
            r1 = -5
            r3 = 0
            r6 = 0
            r28 = 0
            r30 = 0
            r53 = 0
            r5 = r80
        L_0x12df:
            boolean r4 = r2 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x1353 }
            if (r4 == 0) goto L_0x12e8
            if (r91 != 0) goto L_0x12e8
            r45 = 1
            goto L_0x12ea
        L_0x12e8:
            r45 = 0
        L_0x12ea:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x1348 }
            r4.<init>()     // Catch:{ all -> 0x1348 }
            java.lang.String r7 = "bitrate: "
            r4.append(r7)     // Catch:{ all -> 0x1348 }
            r4.append(r5)     // Catch:{ all -> 0x1348 }
            java.lang.String r7 = " framerate: "
            r4.append(r7)     // Catch:{ all -> 0x1348 }
            r4.append(r8)     // Catch:{ all -> 0x1348 }
            java.lang.String r7 = " size: "
            r4.append(r7)     // Catch:{ all -> 0x1348 }
            r7 = r78
            r4.append(r7)     // Catch:{ all -> 0x1344 }
            java.lang.String r9 = "x"
            r4.append(r9)     // Catch:{ all -> 0x1344 }
            r9 = r77
            r4.append(r9)     // Catch:{ all -> 0x1342 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x1342 }
            org.telegram.messenger.FileLog.e((java.lang.String) r4)     // Catch:{ all -> 0x1342 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x1342 }
            r2 = r3
            r29 = r5
            r3 = r6
            r6 = 1
        L_0x1322:
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ all -> 0x133a }
            r5 = r58
            r4.unselectTrack(r5)     // Catch:{ all -> 0x133a }
            if (r2 == 0) goto L_0x1331
            r2.stop()     // Catch:{ all -> 0x133a }
            r2.release()     // Catch:{ all -> 0x133a }
        L_0x1331:
            r42 = r3
            r2 = r45
            r45 = r6
            r6 = r30
            goto L_0x1378
        L_0x133a:
            r0 = move-exception
            r2 = r0
            r11 = r7
            r12 = r9
            r9 = r29
            goto L_0x061e
        L_0x1342:
            r0 = move-exception
            goto L_0x134d
        L_0x1344:
            r0 = move-exception
            r9 = r77
            goto L_0x134d
        L_0x1348:
            r0 = move-exception
            r9 = r77
            r7 = r78
        L_0x134d:
            r2 = r0
            r11 = r7
            r12 = r9
            r7 = r45
            goto L_0x135c
        L_0x1353:
            r0 = move-exception
            r9 = r77
            r7 = r78
            r2 = r0
            r11 = r7
            r12 = r9
        L_0x135b:
            r7 = 0
        L_0x135c:
            r9 = r5
            goto L_0x13df
        L_0x135f:
            r9 = r77
            r7 = r78
            r8 = r79
            r4 = r84
            r29 = r80
            r26 = r86
            r34 = r4
            r1 = -5
            r2 = 0
            r6 = 0
            r28 = 0
            r42 = 0
            r45 = 0
            r53 = 0
        L_0x1378:
            if (r6 == 0) goto L_0x1387
            r6.release()     // Catch:{ all -> 0x137e }
            goto L_0x1387
        L_0x137e:
            r0 = move-exception
            r11 = r7
            r12 = r9
            r9 = r29
            r7 = r2
        L_0x1384:
            r2 = r0
            goto L_0x13df
        L_0x1387:
            if (r53 == 0) goto L_0x138c
            r53.release()     // Catch:{ all -> 0x137e }
        L_0x138c:
            if (r42 == 0) goto L_0x1394
            r42.stop()     // Catch:{ all -> 0x137e }
            r42.release()     // Catch:{ all -> 0x137e }
        L_0x1394:
            if (r28 == 0) goto L_0x1399
            r28.release()     // Catch:{ all -> 0x137e }
        L_0x1399:
            r72.checkConversionCanceled()     // Catch:{ all -> 0x137e }
            r13 = r2
            r6 = r45
        L_0x139f:
            android.media.MediaExtractor r2 = r15.extractor
            if (r2 == 0) goto L_0x13a6
            r2.release()
        L_0x13a6:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer
            if (r2 == 0) goto L_0x13bb
            r2.finishMovie()     // Catch:{ all -> 0x13b6 }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ all -> 0x13b6 }
            long r1 = r2.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x13b6 }
            r15.endPresentationTime = r1     // Catch:{ all -> 0x13b6 }
            goto L_0x13bb
        L_0x13b6:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x13bb:
            r43 = r6
            r6 = r9
            r9 = r29
            goto L_0x142f
        L_0x13c2:
            r0 = move-exception
            r7 = r78
            r4 = r84
            r9 = r12
            r8 = r13
            r26 = r86
            r2 = r0
            r34 = r4
            r11 = r7
            goto L_0x13db
        L_0x13d0:
            r0 = move-exception
            r4 = r84
            r8 = r10
            r7 = r11
            r9 = r12
            r26 = r86
            r2 = r0
            r34 = r4
        L_0x13db:
            r1 = -5
            r7 = 0
            r9 = r80
        L_0x13df:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x149b }
            r3.<init>()     // Catch:{ all -> 0x149b }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x149b }
            r3.append(r9)     // Catch:{ all -> 0x149b }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x149b }
            r3.append(r8)     // Catch:{ all -> 0x149b }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x149b }
            r3.append(r11)     // Catch:{ all -> 0x149b }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x149b }
            r3.append(r12)     // Catch:{ all -> 0x149b }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x149b }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x149b }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x149b }
            android.media.MediaExtractor r2 = r15.extractor
            if (r2 == 0) goto L_0x1415
            r2.release()
        L_0x1415:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer
            if (r2 == 0) goto L_0x142a
            r2.finishMovie()     // Catch:{ all -> 0x1425 }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ all -> 0x1425 }
            long r1 = r2.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x1425 }
            r15.endPresentationTime = r1     // Catch:{ all -> 0x1425 }
            goto L_0x142a
        L_0x1425:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x142a:
            r13 = r7
            r7 = r11
            r6 = r12
            r43 = 1
        L_0x142f:
            if (r13 == 0) goto L_0x145a
            r20 = 1
            r1 = r72
            r2 = r73
            r3 = r74
            r4 = r75
            r5 = r76
            r8 = r79
            r10 = r81
            r11 = r82
            r13 = r34
            r15 = r26
            r17 = r88
            r19 = r90
            r21 = r92
            r22 = r93
            r23 = r94
            r24 = r95
            r25 = r96
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r13, r15, r17, r19, r20, r21, r22, r23, r24, r25)
            return r1
        L_0x145a:
            long r1 = java.lang.System.currentTimeMillis()
            long r1 = r1 - r16
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x149a
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "compression completed time="
            r3.append(r4)
            r3.append(r1)
            java.lang.String r1 = " needCompress="
            r3.append(r1)
            r1 = r90
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
        L_0x149a:
            return r43
        L_0x149b:
            r0 = move-exception
            r2 = r72
            r3 = r0
            android.media.MediaExtractor r4 = r2.extractor
            if (r4 == 0) goto L_0x14a6
            r4.release()
        L_0x14a6:
            org.telegram.messenger.video.MP4Builder r4 = r2.mediaMuxer
            if (r4 == 0) goto L_0x14bb
            r4.finishMovie()     // Catch:{ all -> 0x14b6 }
            org.telegram.messenger.video.MP4Builder r4 = r2.mediaMuxer     // Catch:{ all -> 0x14b6 }
            long r4 = r4.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x14b6 }
            r2.endPresentationTime = r4     // Catch:{ all -> 0x14b6 }
            goto L_0x14bb
        L_0x14b6:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x14bb:
            goto L_0x14bd
        L_0x14bc:
            throw r3
        L_0x14bd:
            goto L_0x14bc
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
