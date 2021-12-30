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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v0, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v1, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v2, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v3, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v4, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v5, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v6, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v7, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v8, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v9, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v10, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v11, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v12, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v17, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v13, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v18, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v14, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v19, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v15, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v26, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v16, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v27, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v17, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v81, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v82, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v18, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v92, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v97, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v28, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v77, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v30, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v19, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v83, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v34, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v120, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v124, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v132, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v20, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r84v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r84v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v21, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v92, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v95, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v96, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v97, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v98, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v99, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v100, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v101, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v102, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v103, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v104, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v105, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r34v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v226, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v227, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v139, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v140, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r12v79 */
    /* JADX WARNING: type inference failed for: r1v123 */
    /* JADX WARNING: type inference failed for: r12v80 */
    /* JADX WARNING: Code restructure failed: missing block: B:1003:0x10f6, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1015:0x1127, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1016:0x1128, code lost:
        r15 = r68;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1017:0x112a, code lost:
        r12 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1038:0x1197, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1039:0x1198, code lost:
        r13 = r78;
        r3 = r82;
        r39 = r12;
        r41 = r14;
        r2 = r55;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1041:0x11b0, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1042:0x11b1, code lost:
        r12 = r77;
        r3 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1043:0x11b7, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1044:0x11b8, code lost:
        r13 = r78;
        r3 = r82;
        r39 = r12;
        r2 = r55;
        r1 = r58;
        r12 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1045:0x11c6, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1046:0x11c7, code lost:
        r34 = r84;
        r39 = r12;
        r12 = r13;
        r3 = r82;
        r13 = r5;
        r58 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1047:0x11d3, code lost:
        r43 = r2;
        r59 = r3;
        r6 = r13;
        r2 = r20;
        r1 = -5;
        r41 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1048:0x11de, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1049:0x11df, code lost:
        r34 = r84;
        r39 = r12;
        r12 = r13;
        r58 = r4;
        r59 = r82;
        r6 = r5;
        r2 = r20;
        r1 = -5;
        r41 = null;
        r43 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1053:0x120b, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1054:0x120c, code lost:
        r3 = r82;
        r34 = r84;
        r12 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1058:0x121d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1059:0x121e, code lost:
        r34 = r84;
        r39 = r12;
        r12 = r13;
        r59 = r82;
        r2 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1076:0x126e, code lost:
        r45 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1137:0x1397, code lost:
        r2.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1141:?, code lost:
        r2.finishMovie();
        r15.endPresentationTime = r15.mediaMuxer.getLastFrameTimestamp(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1142:0x13aa, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1143:0x13ab, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x04dc, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x04e1, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x04e3, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x04f0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x04f1, code lost:
        r13 = r76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x050b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x050c, code lost:
        r3 = r39;
        r2 = r0;
        r6 = r76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x0512, code lost:
        r11 = r50;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x0517, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x0518, code lost:
        r59 = r82;
        r34 = r84;
        r2 = r0;
        r8 = r12;
        r9 = r49;
        r11 = r50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x053c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x053d, code lost:
        r49 = r9;
        r50 = r11;
        r2 = r0;
        r6 = r1;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0546, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x0547, code lost:
        r49 = r9;
        r50 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x054c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x054d, code lost:
        r49 = r9;
        r50 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x0571, code lost:
        r45 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x0876, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0882, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x0883, code lost:
        r6 = r78;
        r59 = r82;
        r43 = r2;
        r57 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x08e4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r5 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0983, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x0985, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x0a1b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x0a1c, code lost:
        r8 = r75;
        r11 = r76;
        r12 = r77;
        r1 = r82;
        r2 = r0;
        r59 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x0a29, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x0a2a, code lost:
        r6 = r78;
        r1 = r82;
        r43 = r2;
        r59 = r3;
        r57 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x01ad, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x01ae, code lost:
        r6 = r76;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:625:0x0afc, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x0b20, code lost:
        if (r11.presentationTimeUs < r3) goto L_0x0b22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x0b78, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0b79, code lost:
        r59 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0b83, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x0b84, code lost:
        r9 = r80;
        r59 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0c7f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0CLASSNAME, code lost:
        r15 = r68;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0CLASSNAME, code lost:
        r8 = r75;
        r11 = r76;
        r12 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x01cc, code lost:
        r6 = r7;
        r7 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x0d00, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0d01, code lost:
        r15 = r68;
        r39 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x0d05, code lost:
        r12 = r77;
        r3 = r0;
        r43 = r2;
        r1 = r6;
        r2 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x0d25, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:0x0d28, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x0e49, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x0edb, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x0var_, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:0x0fe7, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x101d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:962:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:976:0x1056, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:980:0x1075, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:995:0x10cb, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:996:0x10cc, code lost:
        r12 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:997:0x10cf, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:998:0x10d0, code lost:
        r12 = r77;
        r13 = r42;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1003:0x10f6 A[Catch:{ Exception -> 0x111d, all -> 0x111b }, ExcHandler: all (th java.lang.Throwable), PHI: r6 
      PHI: (r6v67 int) = (r6v37 int), (r6v37 int), (r6v68 int), (r6v68 int), (r6v68 int), (r6v68 int), (r6v37 int) binds: [B:746:0x0d1c, B:747:?, B:834:0x0e60, B:835:?, B:841:0x0e6c, B:842:?, B:754:0x0d2a] A[DONT_GENERATE, DONT_INLINE], Splitter:B:746:0x0d1c] */
    /* JADX WARNING: Removed duplicated region for block: B:1015:0x1127 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:701:0x0CLASSNAME] */
    /* JADX WARNING: Removed duplicated region for block: B:1041:0x11b0 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:460:0x084f] */
    /* JADX WARNING: Removed duplicated region for block: B:1053:0x120b A[ExcHandler: all (th java.lang.Throwable), Splitter:B:449:0x07ec] */
    /* JADX WARNING: Removed duplicated region for block: B:1075:0x126c A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:1088:0x12b1 A[Catch:{ all -> 0x12c0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1100:0x12db  */
    /* JADX WARNING: Removed duplicated region for block: B:1102:0x12f6 A[SYNTHETIC, Splitter:B:1102:0x12f6] */
    /* JADX WARNING: Removed duplicated region for block: B:1107:0x1303 A[Catch:{ all -> 0x12fa }] */
    /* JADX WARNING: Removed duplicated region for block: B:1109:0x1308 A[Catch:{ all -> 0x12fa }] */
    /* JADX WARNING: Removed duplicated region for block: B:1111:0x1310 A[Catch:{ all -> 0x12fa }] */
    /* JADX WARNING: Removed duplicated region for block: B:1116:0x131e  */
    /* JADX WARNING: Removed duplicated region for block: B:1119:0x1325 A[SYNTHETIC, Splitter:B:1119:0x1325] */
    /* JADX WARNING: Removed duplicated region for block: B:1137:0x1397  */
    /* JADX WARNING: Removed duplicated region for block: B:1140:0x139e A[SYNTHETIC, Splitter:B:1140:0x139e] */
    /* JADX WARNING: Removed duplicated region for block: B:1146:0x13b8  */
    /* JADX WARNING: Removed duplicated region for block: B:1148:0x13e5  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0401 A[Catch:{ Exception -> 0x04a1, all -> 0x049d }] */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x0403 A[Catch:{ Exception -> 0x04a1, all -> 0x049d }] */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x0415  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x042e  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x04e3 A[ExcHandler: all (th java.lang.Throwable), PHI: r1 r50 
      PHI: (r1v191 int) = (r1v185 int), (r1v192 int), (r1v192 int), (r1v192 int), (r1v192 int), (r1v192 int), (r1v192 int), (r1v192 int), (r1v192 int), (r1v192 int), (r1v192 int) binds: [B:67:0x01bf, B:77:0x01dd, B:129:0x02bb, B:130:?, B:252:0x04a7, B:190:0x0384, B:149:0x02e1, B:135:0x02c6, B:133:0x02c3, B:134:?, B:85:0x01fd] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r50v13 int) = (r50v8 int), (r50v14 int), (r50v14 int), (r50v14 int), (r50v14 int), (r50v14 int), (r50v14 int), (r50v14 int), (r50v14 int), (r50v14 int), (r50v14 int) binds: [B:67:0x01bf, B:77:0x01dd, B:129:0x02bb, B:130:?, B:252:0x04a7, B:190:0x0384, B:149:0x02e1, B:135:0x02c6, B:133:0x02c3, B:134:?, B:85:0x01fd] A[DONT_GENERATE, DONT_INLINE], Splitter:B:67:0x01bf] */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0517 A[ExcHandler: all (r0v158 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:53:0x01a1] */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x0546 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:39:0x0111] */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x056f A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x05b0 A[SYNTHETIC, Splitter:B:311:0x05b0] */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x05c0 A[Catch:{ all -> 0x05b4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x05c5 A[Catch:{ all -> 0x05b4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x0695  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0771  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0786  */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x0790 A[SYNTHETIC, Splitter:B:434:0x0790] */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x07d0 A[SYNTHETIC, Splitter:B:440:0x07d0] */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x07e8  */
    /* JADX WARNING: Removed duplicated region for block: B:469:0x086d A[SYNTHETIC, Splitter:B:469:0x086d] */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x0876 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:469:0x086d] */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x0896  */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x089c A[SYNTHETIC, Splitter:B:480:0x089c] */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x08cc  */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x08cf  */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0983 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:518:0x0939] */
    /* JADX WARNING: Removed duplicated region for block: B:543:0x0987  */
    /* JADX WARNING: Removed duplicated region for block: B:551:0x09b6  */
    /* JADX WARNING: Removed duplicated region for block: B:554:0x09c4  */
    /* JADX WARNING: Removed duplicated region for block: B:555:0x09c7  */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x09e9 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:566:0x0a0e A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:571:0x0a1b A[ExcHandler: all (r0v108 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:568:0x0a12] */
    /* JADX WARNING: Removed duplicated region for block: B:578:0x0a40 A[SYNTHETIC, Splitter:B:578:0x0a40] */
    /* JADX WARNING: Removed duplicated region for block: B:666:0x0b83 A[Catch:{ Exception -> 0x0bc1, all -> 0x0bbf }, ExcHandler: all (th java.lang.Throwable), Splitter:B:601:0x0ab1] */
    /* JADX WARNING: Removed duplicated region for block: B:689:0x0bea  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0c0c A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0c2d  */
    /* JADX WARNING: Removed duplicated region for block: B:700:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:705:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:707:0x0c5e  */
    /* JADX WARNING: Removed duplicated region for block: B:714:0x0c7f A[ExcHandler: all (th java.lang.Throwable), Splitter:B:709:0x0CLASSNAME] */
    /* JADX WARNING: Removed duplicated region for block: B:752:0x0d25 A[ExcHandler: all (th java.lang.Throwable), PHI: r6 r15 
      PHI: (r6v64 int) = (r6v45 int), (r6v45 int), (r6v45 int), (r6v37 int), (r6v37 int), (r6v37 int), (r6v37 int), (r6v37 int), (r6v37 int), (r6v37 int), (r6v37 int) binds: [B:872:0x0ef3, B:873:?, B:856:0x0ebd, B:760:0x0d37, B:761:?, B:793:0x0da1, B:799:0x0db4, B:768:0x0d45, B:750:0x0d22, B:751:?, B:728:0x0cac] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r15v26 org.telegram.messenger.video.MediaCodecVideoConvertor) = (r15v25 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v25 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v25 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v30 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v30 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v30 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v30 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v30 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v30 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v30 org.telegram.messenger.video.MediaCodecVideoConvertor), (r15v38 org.telegram.messenger.video.MediaCodecVideoConvertor) binds: [B:872:0x0ef3, B:873:?, B:856:0x0ebd, B:760:0x0d37, B:761:?, B:793:0x0da1, B:799:0x0db4, B:768:0x0d45, B:750:0x0d22, B:751:?, B:728:0x0cac] A[DONT_GENERATE, DONT_INLINE], Splitter:B:728:0x0cac] */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x0e68  */
    /* JADX WARNING: Removed duplicated region for block: B:839:0x0e6a  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x0e7a  */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x0e93  */
    /* JADX WARNING: Removed duplicated region for block: B:921:0x0fa5  */
    /* JADX WARNING: Removed duplicated region for block: B:922:0x0fa8  */
    /* JADX WARNING: Removed duplicated region for block: B:929:0x0fb5 A[SYNTHETIC, Splitter:B:929:0x0fb5] */
    /* JADX WARNING: Removed duplicated region for block: B:934:0x0fd9 A[Catch:{ Exception -> 0x0fe7, all -> 0x1075 }] */
    /* JADX WARNING: Removed duplicated region for block: B:938:0x0fe7 A[Catch:{ Exception -> 0x0fe7, all -> 0x1075 }, ExcHandler: Exception (e java.lang.Exception), PHI: r12 r34 r47 r59 
      PHI: (r12v64 int) = (r12v65 int), (r12v65 int), (r12v73 int), (r12v73 int), (r12v73 int), (r12v73 int), (r12v73 int), (r12v73 int) binds: [B:953:0x100c, B:929:0x0fb5, B:892:0x0var_, B:893:?, B:895:0x0f3a, B:896:?, B:889:0x0var_, B:890:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r34v43 long) = (r34v44 long), (r34v44 long), (r34v34 long), (r34v34 long), (r34v34 long), (r34v34 long), (r34v34 long), (r34v34 long) binds: [B:953:0x100c, B:929:0x0fb5, B:892:0x0var_, B:893:?, B:895:0x0f3a, B:896:?, B:889:0x0var_, B:890:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r47v11 int) = (r47v12 int), (r47v12 int), (r47v18 int), (r47v18 int), (r47v18 int), (r47v18 int), (r47v18 int), (r47v18 int) binds: [B:953:0x100c, B:929:0x0fb5, B:892:0x0var_, B:893:?, B:895:0x0f3a, B:896:?, B:889:0x0var_, B:890:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r59v43 long) = (r59v44 long), (r59v44 long), (r59v34 long), (r59v34 long), (r59v34 long), (r59v34 long), (r59v34 long), (r59v34 long) binds: [B:953:0x100c, B:929:0x0fb5, B:892:0x0var_, B:893:?, B:895:0x0f3a, B:896:?, B:889:0x0var_, B:890:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:889:0x0var_] */
    /* JADX WARNING: Removed duplicated region for block: B:942:0x0fee A[Catch:{ Exception -> 0x0fe7, all -> 0x1075 }] */
    /* JADX WARNING: Removed duplicated region for block: B:943:0x0ff1 A[Catch:{ Exception -> 0x0fe7, all -> 0x1075 }] */
    /* JADX WARNING: Removed duplicated region for block: B:951:0x1006  */
    /* JADX WARNING: Removed duplicated region for block: B:969:0x103a A[Catch:{ Exception -> 0x1077, all -> 0x1075 }] */
    /* JADX WARNING: Removed duplicated region for block: B:972:0x1047 A[Catch:{ Exception -> 0x1077, all -> 0x1075 }] */
    /* JADX WARNING: Removed duplicated region for block: B:980:0x1075 A[ExcHandler: all (th java.lang.Throwable), PHI: r12 r34 r47 r59 
      PHI: (r12v63 int) = (r12v65 int), (r12v65 int), (r12v65 int), (r12v65 int), (r12v65 int), (r12v65 int), (r12v65 int), (r12v65 int), (r12v73 int), (r12v73 int), (r12v73 int), (r12v73 int) binds: [B:956:0x1018, B:966:0x1030, B:961:0x101f, B:957:?, B:953:0x100c, B:948:0x1001, B:949:?, B:929:0x0fb5, B:895:0x0f3a, B:896:?, B:889:0x0var_, B:890:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r34v42 long) = (r34v44 long), (r34v44 long), (r34v44 long), (r34v44 long), (r34v44 long), (r34v44 long), (r34v44 long), (r34v44 long), (r34v34 long), (r34v34 long), (r34v34 long), (r34v34 long) binds: [B:956:0x1018, B:966:0x1030, B:961:0x101f, B:957:?, B:953:0x100c, B:948:0x1001, B:949:?, B:929:0x0fb5, B:895:0x0f3a, B:896:?, B:889:0x0var_, B:890:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r47v10 int) = (r47v12 int), (r47v12 int), (r47v12 int), (r47v12 int), (r47v12 int), (r47v12 int), (r47v12 int), (r47v12 int), (r47v18 int), (r47v18 int), (r47v18 int), (r47v18 int) binds: [B:956:0x1018, B:966:0x1030, B:961:0x101f, B:957:?, B:953:0x100c, B:948:0x1001, B:949:?, B:929:0x0fb5, B:895:0x0f3a, B:896:?, B:889:0x0var_, B:890:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r59v42 long) = (r59v44 long), (r59v44 long), (r59v44 long), (r59v44 long), (r59v44 long), (r59v44 long), (r59v44 long), (r59v44 long), (r59v34 long), (r59v34 long), (r59v34 long), (r59v34 long) binds: [B:956:0x1018, B:966:0x1030, B:961:0x101f, B:957:?, B:953:0x100c, B:948:0x1001, B:949:?, B:929:0x0fb5, B:895:0x0f3a, B:896:?, B:889:0x0var_, B:890:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:948:0x1001] */
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
            r10 = r77
            r9 = r78
            r8 = r79
            r6 = r80
            r4 = r86
            r3 = r88
            r2 = r94
            long r16 = java.lang.System.currentTimeMillis()
            r18 = -1
            r7 = 0
            android.media.MediaCodec$BufferInfo r6 = new android.media.MediaCodec$BufferInfo     // Catch:{ all -> 0x1354 }
            r6.<init>()     // Catch:{ all -> 0x1354 }
            org.telegram.messenger.video.Mp4Movie r1 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ all -> 0x1354 }
            r1.<init>()     // Catch:{ all -> 0x1354 }
            r22 = r6
            r6 = r70
            r1.setCacheFile(r6)     // Catch:{ all -> 0x1354 }
            r1.setRotation(r7)     // Catch:{ all -> 0x1354 }
            r1.setSize(r12, r11)     // Catch:{ all -> 0x1354 }
            org.telegram.messenger.video.MP4Builder r7 = new org.telegram.messenger.video.MP4Builder     // Catch:{ all -> 0x1354 }
            r7.<init>()     // Catch:{ all -> 0x1354 }
            r6 = r72
            org.telegram.messenger.video.MP4Builder r1 = r7.createMovie(r1, r6)     // Catch:{ all -> 0x1354 }
            r15.mediaMuxer = r1     // Catch:{ all -> 0x1354 }
            float r1 = (float) r4     // Catch:{ all -> 0x1354 }
            r24 = 1148846080(0x447a0000, float:1000.0)
            float r25 = r1 / r24
            r26 = 1000(0x3e8, double:4.94E-321)
            long r1 = r4 * r26
            r15.endPresentationTime = r1     // Catch:{ all -> 0x1354 }
            r68.checkConversionCanceled()     // Catch:{ all -> 0x1354 }
            java.lang.String r7 = "csd-1"
            java.lang.String r1 = "csd-0"
            java.lang.String r6 = "prepend-sps-pps-to-idr-frames"
            r28 = r6
            r27 = r7
            java.lang.String r7 = "video/avc"
            r33 = r7
            r6 = 0
            if (r93 == 0) goto L_0x05ff
            int r18 = (r84 > r6 ? 1 : (r84 == r6 ? 0 : -1))
            if (r18 < 0) goto L_0x0089
            r2 = 1157234688(0x44fa0000, float:2000.0)
            int r2 = (r25 > r2 ? 1 : (r25 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0074
            r2 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            r9 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x008e
        L_0x0074:
            r2 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r2 = (r25 > r2 ? 1 : (r25 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0082
            r2 = 2200000(0x2191c0, float:3.082857E-39)
            r9 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x008e
        L_0x0082:
            r2 = 1560000(0x17cdc0, float:2.186026E-39)
            r9 = 1560000(0x17cdc0, float:2.186026E-39)
            goto L_0x008e
        L_0x0089:
            if (r9 > 0) goto L_0x008e
            r9 = 921600(0xe1000, float:1.291437E-39)
        L_0x008e:
            int r2 = r12 % 16
            r18 = 1098907648(0x41800000, float:16.0)
            if (r2 == 0) goto L_0x00da
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            if (r2 == 0) goto L_0x00bd
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            r2.<init>()     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            java.lang.String r6 = "changing width from "
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            r2.append(r12)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            float r6 = (float) r12     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            float r6 = r6 / r18
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
        L_0x00bd:
            float r2 = (float) r12     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            float r2 = r2 / r18
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            int r2 = r2 * 16
            r12 = r2
            goto L_0x00da
        L_0x00c8:
            r0 = move-exception
            r59 = r82
            r34 = r84
            r2 = r0
            r8 = r12
            r1 = -5
            r7 = 0
            r12 = r10
            goto L_0x1364
        L_0x00d4:
            r0 = move-exception
            r2 = r0
            r49 = r9
            goto L_0x0565
        L_0x00da:
            int r2 = r11 % 16
            if (r2 == 0) goto L_0x0111
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            if (r2 == 0) goto L_0x0107
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            r2.<init>()     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            java.lang.String r6 = "changing height from "
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            r2.append(r11)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            float r6 = (float) r11     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            float r6 = r6 / r18
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
        L_0x0107:
            float r2 = (float) r11     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            float r2 = r2 / r18
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            int r2 = r2 * 16
            r11 = r2
        L_0x0111:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x054c, all -> 0x0546 }
            if (r2 == 0) goto L_0x0139
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            r2.<init>()     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            java.lang.String r6 = "create photo encoder "
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            r2.append(r12)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            java.lang.String r6 = " "
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            r2.append(r11)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            java.lang.String r6 = " duration = "
            r2.append(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            r2.append(r4)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d4, all -> 0x00c8 }
        L_0x0139:
            r7 = r33
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r7, r12, r11)     // Catch:{ Exception -> 0x054c, all -> 0x0546 }
            java.lang.String r6 = "color-format"
            r33 = r1
            r1 = 2130708361(0x7var_, float:1.701803E38)
            r2.setInteger(r6, r1)     // Catch:{ Exception -> 0x054c, all -> 0x0546 }
            java.lang.String r1 = "bitrate"
            r2.setInteger(r1, r9)     // Catch:{ Exception -> 0x054c, all -> 0x0546 }
            java.lang.String r1 = "frame-rate"
            r6 = 30
            r2.setInteger(r1, r6)     // Catch:{ Exception -> 0x054c, all -> 0x0546 }
            java.lang.String r1 = "i-frame-interval"
            r6 = 1
            r2.setInteger(r1, r6)     // Catch:{ Exception -> 0x054c, all -> 0x0546 }
            android.media.MediaCodec r1 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x054c, all -> 0x0546 }
            r18 = r7
            r7 = 0
            r1.configure(r2, r7, r7, r6)     // Catch:{ Exception -> 0x053c, all -> 0x0546 }
            org.telegram.messenger.video.InputSurface r2 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x053c, all -> 0x0546 }
            android.view.Surface r6 = r1.createInputSurface()     // Catch:{ Exception -> 0x053c, all -> 0x0546 }
            r2.<init>(r6)     // Catch:{ Exception -> 0x053c, all -> 0x0546 }
            r2.makeCurrent()     // Catch:{ Exception -> 0x052d, all -> 0x0546 }
            r1.start()     // Catch:{ Exception -> 0x052d, all -> 0x0546 }
            org.telegram.messenger.video.OutputSurface r19 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x052d, all -> 0x0546 }
            float r6 = (float) r10
            r34 = 1
            r76 = r1
            r38 = r33
            r1 = r19
            r39 = r2
            r2 = r90
            r3 = r69
            r4 = r91
            r5 = r92
            r20 = r6
            r14 = r22
            r6 = 0
            r7 = -1
            r13 = 21
            r47 = r18
            r46 = r27
            r7 = r12
            r8 = r11
            r49 = r9
            r9 = r71
            r10 = r20
            r50 = r11
            r11 = r34
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0523, all -> 0x0517 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x050b, all -> 0x0517 }
            if (r1 >= r13) goto L_0x01b3
            java.nio.ByteBuffer[] r6 = r76.getOutputBuffers()     // Catch:{ Exception -> 0x01ad, all -> 0x0517 }
            goto L_0x01b4
        L_0x01ad:
            r0 = move-exception
            r6 = r76
            r2 = r0
            goto L_0x0512
        L_0x01b3:
            r6 = 0
        L_0x01b4:
            r68.checkConversionCanceled()     // Catch:{ Exception -> 0x050b, all -> 0x0517 }
            r1 = -5
            r2 = 1
            r3 = 0
            r4 = 0
            r5 = 0
            r7 = 0
        L_0x01bd:
            if (r7 != 0) goto L_0x04fb
            r68.checkConversionCanceled()     // Catch:{ Exception -> 0x04f0, all -> 0x04e3 }
            r8 = r3 ^ 1
            r9 = r7
            r7 = r6
            r6 = 1
        L_0x01c7:
            if (r8 != 0) goto L_0x01cf
            if (r6 == 0) goto L_0x01cc
            goto L_0x01cf
        L_0x01cc:
            r6 = r7
            r7 = r9
            goto L_0x01bd
        L_0x01cf:
            r68.checkConversionCanceled()     // Catch:{ Exception -> 0x04f0, all -> 0x04e3 }
            if (r89 == 0) goto L_0x01d9
            r10 = 22000(0x55f0, double:1.08694E-319)
            r13 = r76
            goto L_0x01dd
        L_0x01d9:
            r13 = r76
            r10 = 2500(0x9c4, double:1.235E-320)
        L_0x01dd:
            int r10 = r13.dequeueOutputBuffer(r14, r10)     // Catch:{ Exception -> 0x04e1, all -> 0x04e3 }
            r11 = -1
            if (r10 != r11) goto L_0x01fa
            r75 = r1
            r20 = r3
            r18 = r5
            r76 = r8
            r11 = r9
            r8 = r38
            r6 = r46
            r3 = r50
            r1 = -1
            r5 = 0
        L_0x01f5:
            r9 = r7
            r7 = r47
            goto L_0x0413
        L_0x01fa:
            r11 = -3
            if (r10 != r11) goto L_0x0223
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            r75 = r6
            r6 = 21
            if (r11 >= r6) goto L_0x0209
            java.nio.ByteBuffer[] r7 = r13.getOutputBuffers()     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
        L_0x0209:
            r20 = r3
            r18 = r5
            r76 = r8
            r11 = r9
            r8 = r38
            r6 = r46
            r3 = r50
            r5 = r75
            r75 = r1
            r9 = r7
            r7 = r47
        L_0x021d:
            r1 = -1
            goto L_0x0413
        L_0x0220:
            r0 = move-exception
            goto L_0x04f5
        L_0x0223:
            r75 = r6
            r6 = -2
            if (r10 != r6) goto L_0x02b1
            android.media.MediaFormat r6 = r13.getOutputFormat()     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            if (r11 == 0) goto L_0x0247
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            r11.<init>()     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            r76 = r8
            java.lang.String r8 = "photo encoder new format "
            r11.append(r8)     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            r11.append(r6)     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            java.lang.String r8 = r11.toString()     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            org.telegram.messenger.FileLog.d(r8)     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            goto L_0x0249
        L_0x0247:
            r76 = r8
        L_0x0249:
            r8 = -5
            if (r1 != r8) goto L_0x029b
            if (r6 == 0) goto L_0x029b
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            r8 = 0
            int r1 = r11.addTrack(r6, r8)     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            r11 = r28
            boolean r18 = r6.containsKey(r11)     // Catch:{ Exception -> 0x0296, all -> 0x0291 }
            if (r18 == 0) goto L_0x0288
            int r8 = r6.getInteger(r11)     // Catch:{ Exception -> 0x0296, all -> 0x0291 }
            r78 = r1
            r1 = 1
            if (r8 != r1) goto L_0x028a
            r8 = r38
            java.nio.ByteBuffer r1 = r6.getByteBuffer(r8)     // Catch:{ Exception -> 0x0283, all -> 0x027e }
            r4 = r46
            java.nio.ByteBuffer r6 = r6.getByteBuffer(r4)     // Catch:{ Exception -> 0x0283, all -> 0x027e }
            int r1 = r1.limit()     // Catch:{ Exception -> 0x0283, all -> 0x027e }
            int r6 = r6.limit()     // Catch:{ Exception -> 0x0283, all -> 0x027e }
            int r1 = r1 + r6
            r6 = r4
            r4 = r1
            goto L_0x028e
        L_0x027e:
            r0 = move-exception
            r1 = r78
            goto L_0x04e4
        L_0x0283:
            r0 = move-exception
            r1 = r78
            goto L_0x04f5
        L_0x0288:
            r78 = r1
        L_0x028a:
            r8 = r38
            r6 = r46
        L_0x028e:
            r1 = r78
            goto L_0x02a1
        L_0x0291:
            r0 = move-exception
            r78 = r1
            goto L_0x04e4
        L_0x0296:
            r0 = move-exception
            r78 = r1
            goto L_0x04f5
        L_0x029b:
            r11 = r28
            r8 = r38
            r6 = r46
        L_0x02a1:
            r20 = r3
            r18 = r5
            r28 = r11
            r3 = r50
            r5 = r75
            r75 = r1
            r11 = r9
            r1 = -1
            goto L_0x01f5
        L_0x02b1:
            r76 = r8
            r11 = r28
            r8 = r38
            r6 = r46
            if (r10 < 0) goto L_0x04c3
            int r9 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x04e1, all -> 0x04e3 }
            r28 = r11
            r11 = 21
            if (r9 >= r11) goto L_0x02c6
            r9 = r7[r10]     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            goto L_0x02ca
        L_0x02c6:
            java.nio.ByteBuffer r9 = r13.getOutputBuffer(r10)     // Catch:{ Exception -> 0x04e1, all -> 0x04e3 }
        L_0x02ca:
            if (r9 == 0) goto L_0x04a5
            int r11 = r14.size     // Catch:{ Exception -> 0x04e1, all -> 0x04e3 }
            r78 = r7
            r7 = 1
            if (r11 <= r7) goto L_0x03f3
            int r7 = r14.flags     // Catch:{ Exception -> 0x03eb, all -> 0x03dd }
            r18 = r7 & 2
            if (r18 != 0) goto L_0x0360
            if (r4 == 0) goto L_0x02ea
            r18 = r7 & 1
            if (r18 == 0) goto L_0x02ea
            r18 = r5
            int r5 = r14.offset     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            int r5 = r5 + r4
            r14.offset = r5     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            int r11 = r11 - r4
            r14.size = r11     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            goto L_0x02ec
        L_0x02ea:
            r18 = r5
        L_0x02ec:
            if (r2 == 0) goto L_0x033b
            r5 = r7 & 1
            if (r5 == 0) goto L_0x033b
            int r2 = r14.size     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            r7 = 100
            if (r2 <= r7) goto L_0x0339
            int r2 = r14.offset     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            r9.position(r2)     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            byte[] r2 = new byte[r7]     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            r9.get(r2)     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            r5 = 0
            r11 = 0
        L_0x0304:
            r7 = 96
            if (r5 >= r7) goto L_0x0339
            byte r7 = r2[r5]     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            if (r7 != 0) goto L_0x0330
            int r7 = r5 + 1
            byte r7 = r2[r7]     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            if (r7 != 0) goto L_0x0330
            int r7 = r5 + 2
            byte r7 = r2[r7]     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            if (r7 != 0) goto L_0x0330
            int r7 = r5 + 3
            byte r7 = r2[r7]     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            r20 = r2
            r2 = 1
            if (r7 != r2) goto L_0x0332
            int r11 = r11 + 1
            if (r11 <= r2) goto L_0x0332
            int r2 = r14.offset     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            int r2 = r2 + r5
            r14.offset = r2     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            int r2 = r14.size     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            int r2 = r2 - r5
            r14.size = r2     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            goto L_0x0339
        L_0x0330:
            r20 = r2
        L_0x0332:
            int r5 = r5 + 1
            r2 = r20
            r7 = 100
            goto L_0x0304
        L_0x0339:
            r7 = 0
            goto L_0x033c
        L_0x033b:
            r7 = r2
        L_0x033c:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            r20 = r3
            r5 = 1
            long r2 = r2.writeSampleData(r1, r9, r14, r5)     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            r22 = r4
            r4 = 0
            int r9 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r9 == 0) goto L_0x0359
            org.telegram.messenger.MediaController$VideoConvertorListener r9 = r15.callback     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            if (r9 == 0) goto L_0x0359
            float r11 = (float) r4     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            float r11 = r11 / r24
            float r11 = r11 / r25
            r9.didWriteData(r2, r11)     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
        L_0x0359:
            r2 = r7
        L_0x035a:
            r7 = r47
            r3 = r50
            goto L_0x03fb
        L_0x0360:
            r20 = r3
            r22 = r4
            r18 = r5
            r4 = 0
            r7 = -5
            if (r1 != r7) goto L_0x035a
            byte[] r3 = new byte[r11]     // Catch:{ Exception -> 0x03eb, all -> 0x03dd }
            int r4 = r14.offset     // Catch:{ Exception -> 0x03eb, all -> 0x03dd }
            int r4 = r4 + r11
            r9.limit(r4)     // Catch:{ Exception -> 0x03eb, all -> 0x03dd }
            int r4 = r14.offset     // Catch:{ Exception -> 0x03eb, all -> 0x03dd }
            r9.position(r4)     // Catch:{ Exception -> 0x03eb, all -> 0x03dd }
            r9.get(r3)     // Catch:{ Exception -> 0x03eb, all -> 0x03dd }
            int r4 = r14.size     // Catch:{ Exception -> 0x03eb, all -> 0x03dd }
            r5 = 1
            int r4 = r4 - r5
        L_0x037f:
            if (r4 < 0) goto L_0x03bd
            r9 = 3
            if (r4 <= r9) goto L_0x03bd
            byte r11 = r3[r4]     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            if (r11 != r5) goto L_0x03b8
            int r5 = r4 + -1
            byte r5 = r3[r5]     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            if (r5 != 0) goto L_0x03b8
            int r5 = r4 + -2
            byte r5 = r3[r5]     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            if (r5 != 0) goto L_0x03b8
            int r5 = r4 + -3
            byte r11 = r3[r5]     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            if (r11 != 0) goto L_0x03b8
            java.nio.ByteBuffer r4 = java.nio.ByteBuffer.allocate(r5)     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            int r11 = r14.size     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            int r11 = r11 - r5
            java.nio.ByteBuffer r11 = java.nio.ByteBuffer.allocate(r11)     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            r7 = 0
            java.nio.ByteBuffer r9 = r4.put(r3, r7, r5)     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            r9.position(r7)     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            int r9 = r14.size     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            int r9 = r9 - r5
            java.nio.ByteBuffer r3 = r11.put(r3, r5, r9)     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            r3.position(r7)     // Catch:{ Exception -> 0x0220, all -> 0x04e3 }
            goto L_0x03bf
        L_0x03b8:
            int r4 = r4 + -1
            r5 = 1
            r7 = -5
            goto L_0x037f
        L_0x03bd:
            r4 = 0
            r11 = 0
        L_0x03bf:
            r7 = r47
            r3 = r50
            android.media.MediaFormat r5 = android.media.MediaFormat.createVideoFormat(r7, r12, r3)     // Catch:{ Exception -> 0x03db, all -> 0x03d9 }
            if (r4 == 0) goto L_0x03d1
            if (r11 == 0) goto L_0x03d1
            r5.setByteBuffer(r8, r4)     // Catch:{ Exception -> 0x03db, all -> 0x03d9 }
            r5.setByteBuffer(r6, r11)     // Catch:{ Exception -> 0x03db, all -> 0x03d9 }
        L_0x03d1:
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x03db, all -> 0x03d9 }
            r9 = 0
            int r1 = r4.addTrack(r5, r9)     // Catch:{ Exception -> 0x03db, all -> 0x03d9 }
            goto L_0x03fb
        L_0x03d9:
            r0 = move-exception
            goto L_0x03e0
        L_0x03db:
            r0 = move-exception
            goto L_0x03ee
        L_0x03dd:
            r0 = move-exception
            r3 = r50
        L_0x03e0:
            r59 = r82
            r34 = r84
            r2 = r0
            r11 = r3
            r8 = r12
            r9 = r49
            goto L_0x055c
        L_0x03eb:
            r0 = move-exception
            r3 = r50
        L_0x03ee:
            r2 = r0
            r11 = r3
            r6 = r13
            goto L_0x056b
        L_0x03f3:
            r20 = r3
            r22 = r4
            r18 = r5
            goto L_0x035a
        L_0x03fb:
            int r4 = r14.flags     // Catch:{ Exception -> 0x04a1, all -> 0x049d }
            r4 = r4 & 4
            if (r4 == 0) goto L_0x0403
            r4 = 1
            goto L_0x0404
        L_0x0403:
            r4 = 0
        L_0x0404:
            r5 = 0
            r13.releaseOutputBuffer(r10, r5)     // Catch:{ Exception -> 0x04a1, all -> 0x049d }
            r5 = r75
            r9 = r78
            r75 = r1
            r11 = r4
            r4 = r22
            goto L_0x021d
        L_0x0413:
            if (r10 == r1) goto L_0x042e
            r1 = r75
            r50 = r3
            r46 = r6
            r47 = r7
            r38 = r8
            r7 = r9
            r9 = r11
            r3 = r20
            r8 = r76
            r6 = r5
            r76 = r13
            r5 = r18
            r13 = 21
            goto L_0x01c7
        L_0x042e:
            if (r20 != 0) goto L_0x0479
            r19.drawImage()     // Catch:{ Exception -> 0x0470, all -> 0x0469 }
            r10 = r18
            float r1 = (float) r10
            r18 = 1106247680(0x41var_, float:30.0)
            float r1 = r1 / r18
            float r1 = r1 * r24
            float r1 = r1 * r24
            float r1 = r1 * r24
            r78 = r2
            long r1 = (long) r1
            r50 = r3
            r3 = r39
            r3.setPresentationTime(r1)     // Catch:{ Exception -> 0x0464, all -> 0x0462 }
            r3.swapBuffers()     // Catch:{ Exception -> 0x0464, all -> 0x0462 }
            int r1 = r10 + 1
            float r2 = (float) r1     // Catch:{ Exception -> 0x0464, all -> 0x0462 }
            r10 = 1106247680(0x41var_, float:30.0)
            float r10 = r10 * r25
            int r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r2 < 0) goto L_0x045f
            r13.signalEndOfInputStream()     // Catch:{ Exception -> 0x0464, all -> 0x0462 }
            r2 = 0
            r20 = 1
            goto L_0x0484
        L_0x045f:
            r2 = r76
            goto L_0x0484
        L_0x0462:
            r0 = move-exception
            goto L_0x046c
        L_0x0464:
            r0 = move-exception
            r1 = r75
            goto L_0x04dd
        L_0x0469:
            r0 = move-exception
            r50 = r3
        L_0x046c:
            r1 = r75
            goto L_0x04e4
        L_0x0470:
            r0 = move-exception
            r50 = r3
            r3 = r39
            r1 = r75
            goto L_0x04f5
        L_0x0479:
            r78 = r2
            r50 = r3
            r10 = r18
            r3 = r39
            r2 = r76
            r1 = r10
        L_0x0484:
            r39 = r3
            r46 = r6
            r47 = r7
            r38 = r8
            r7 = r9
            r9 = r11
            r76 = r13
            r3 = r20
            r13 = 21
            r8 = r2
            r6 = r5
            r2 = r78
            r5 = r1
            r1 = r75
            goto L_0x01c7
        L_0x049d:
            r0 = move-exception
            r50 = r3
            goto L_0x04e4
        L_0x04a1:
            r0 = move-exception
            r50 = r3
            goto L_0x04f3
        L_0x04a5:
            r3 = r39
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x04dc, all -> 0x04e3 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04dc, all -> 0x04e3 }
            r4.<init>()     // Catch:{ Exception -> 0x04dc, all -> 0x04e3 }
            java.lang.String r5 = "encoderOutputBuffer "
            r4.append(r5)     // Catch:{ Exception -> 0x04dc, all -> 0x04e3 }
            r4.append(r10)     // Catch:{ Exception -> 0x04dc, all -> 0x04e3 }
            java.lang.String r5 = " was null"
            r4.append(r5)     // Catch:{ Exception -> 0x04dc, all -> 0x04e3 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x04dc, all -> 0x04e3 }
            r2.<init>(r4)     // Catch:{ Exception -> 0x04dc, all -> 0x04e3 }
            throw r2     // Catch:{ Exception -> 0x04dc, all -> 0x04e3 }
        L_0x04c3:
            r3 = r39
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x04dc, all -> 0x04e3 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04dc, all -> 0x04e3 }
            r4.<init>()     // Catch:{ Exception -> 0x04dc, all -> 0x04e3 }
            java.lang.String r5 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x04dc, all -> 0x04e3 }
            r4.append(r10)     // Catch:{ Exception -> 0x04dc, all -> 0x04e3 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x04dc, all -> 0x04e3 }
            r2.<init>(r4)     // Catch:{ Exception -> 0x04dc, all -> 0x04e3 }
            throw r2     // Catch:{ Exception -> 0x04dc, all -> 0x04e3 }
        L_0x04dc:
            r0 = move-exception
        L_0x04dd:
            r2 = r0
            r39 = r3
            goto L_0x04f6
        L_0x04e1:
            r0 = move-exception
            goto L_0x04f3
        L_0x04e3:
            r0 = move-exception
        L_0x04e4:
            r59 = r82
            r34 = r84
            r2 = r0
            r8 = r12
            r9 = r49
            r11 = r50
            goto L_0x055c
        L_0x04f0:
            r0 = move-exception
            r13 = r76
        L_0x04f3:
            r3 = r39
        L_0x04f5:
            r2 = r0
        L_0x04f6:
            r6 = r13
            r11 = r50
            goto L_0x056b
        L_0x04fb:
            r13 = r76
            r3 = r39
            r2 = r1
            r1 = r13
            r9 = r49
            r11 = r50
            r6 = 0
            r7 = 0
            r13 = r77
            goto L_0x05ae
        L_0x050b:
            r0 = move-exception
            r13 = r76
            r3 = r39
            r2 = r0
            r6 = r13
        L_0x0512:
            r11 = r50
            r1 = -5
            goto L_0x056b
        L_0x0517:
            r0 = move-exception
            r59 = r82
            r34 = r84
            r2 = r0
            r8 = r12
            r9 = r49
            r11 = r50
            goto L_0x055b
        L_0x0523:
            r0 = move-exception
            r13 = r76
            r3 = r39
            r2 = r0
            r6 = r13
            r11 = r50
            goto L_0x0538
        L_0x052d:
            r0 = move-exception
            r13 = r1
            r3 = r2
            r49 = r9
            r50 = r11
            r2 = r0
            r39 = r3
            r6 = r13
        L_0x0538:
            r1 = -5
            r19 = 0
            goto L_0x056b
        L_0x053c:
            r0 = move-exception
            r13 = r1
            r49 = r9
            r50 = r11
            r2 = r0
            r6 = r13
            r1 = -5
            goto L_0x0567
        L_0x0546:
            r0 = move-exception
            r49 = r9
            r50 = r11
            goto L_0x0555
        L_0x054c:
            r0 = move-exception
            r49 = r9
            r50 = r11
            goto L_0x0564
        L_0x0552:
            r0 = move-exception
            r49 = r9
        L_0x0555:
            r59 = r82
            r34 = r84
            r2 = r0
            r8 = r12
        L_0x055b:
            r1 = -5
        L_0x055c:
            r7 = 0
        L_0x055d:
            r12 = r77
            goto L_0x1364
        L_0x0561:
            r0 = move-exception
            r49 = r9
        L_0x0564:
            r2 = r0
        L_0x0565:
            r1 = -5
            r6 = 0
        L_0x0567:
            r19 = 0
            r39 = 0
        L_0x056b:
            boolean r3 = r2 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x05f4 }
            if (r3 == 0) goto L_0x0574
            if (r89 != 0) goto L_0x0574
            r45 = 1
            goto L_0x0576
        L_0x0574:
            r45 = 0
        L_0x0576:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x05e7 }
            r3.<init>()     // Catch:{ all -> 0x05e7 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x05e7 }
            r9 = r49
            r3.append(r9)     // Catch:{ all -> 0x05e5 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x05e5 }
            r13 = r77
            r3.append(r13)     // Catch:{ all -> 0x05d9 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x05d9 }
            r3.append(r11)     // Catch:{ all -> 0x05d9 }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x05d9 }
            r3.append(r12)     // Catch:{ all -> 0x05d9 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x05d9 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x05d9 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x05d9 }
            r2 = r1
            r1 = r6
            r7 = r45
            r6 = 1
        L_0x05ae:
            if (r19 == 0) goto L_0x05be
            r19.release()     // Catch:{ all -> 0x05b4 }
            goto L_0x05be
        L_0x05b4:
            r0 = move-exception
            r59 = r82
            r34 = r84
            r1 = r2
            r8 = r12
            r12 = r13
            goto L_0x12d8
        L_0x05be:
            if (r39 == 0) goto L_0x05c3
            r39.release()     // Catch:{ all -> 0x05b4 }
        L_0x05c3:
            if (r1 == 0) goto L_0x05cb
            r1.stop()     // Catch:{ all -> 0x05b4 }
            r1.release()     // Catch:{ all -> 0x05b4 }
        L_0x05cb:
            r68.checkConversionCanceled()     // Catch:{ all -> 0x05b4 }
            r59 = r82
            r34 = r84
            r1 = r2
            r14 = r7
            r7 = r11
            r8 = r12
            r12 = r13
            goto L_0x131a
        L_0x05d9:
            r0 = move-exception
            r59 = r82
            r34 = r84
            r2 = r0
            r8 = r12
            r12 = r13
            r7 = r45
            goto L_0x1364
        L_0x05e5:
            r0 = move-exception
            goto L_0x05ea
        L_0x05e7:
            r0 = move-exception
            r9 = r49
        L_0x05ea:
            r59 = r82
            r34 = r84
            r2 = r0
            r8 = r12
            r7 = r45
            goto L_0x055d
        L_0x05f4:
            r0 = move-exception
            r9 = r49
            r59 = r82
            r34 = r84
            r2 = r0
            r8 = r12
            goto L_0x055c
        L_0x05ff:
            r8 = r1
            r13 = r10
            r14 = r22
            r6 = r27
            r10 = r28
            r7 = r33
            r4 = -1
            r5 = 3
            android.media.MediaExtractor r1 = new android.media.MediaExtractor     // Catch:{ all -> 0x134c }
            r1.<init>()     // Catch:{ all -> 0x134c }
            r15.extractor = r1     // Catch:{ all -> 0x134c }
            r3 = r69
            r11 = 0
            r1.setDataSource(r3)     // Catch:{ all -> 0x133a }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ all -> 0x133a }
            r11 = 0
            int r12 = org.telegram.messenger.MediaController.findTrack(r1, r11)     // Catch:{ all -> 0x133a }
            if (r9 == r4) goto L_0x0638
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ all -> 0x062b }
            r5 = 1
            int r1 = org.telegram.messenger.MediaController.findTrack(r1, r5)     // Catch:{ all -> 0x062b }
            r5 = r1
            goto L_0x0639
        L_0x062b:
            r0 = move-exception
            r8 = r75
            r11 = r76
        L_0x0630:
            r59 = r82
            r34 = r84
            r2 = r0
            r12 = r13
            goto L_0x1362
        L_0x0638:
            r5 = -1
        L_0x0639:
            java.lang.String r1 = "mime"
            if (r12 < 0) goto L_0x0651
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ all -> 0x062b }
            android.media.MediaFormat r11 = r11.getTrackFormat(r12)     // Catch:{ all -> 0x062b }
            java.lang.String r11 = r11.getString(r1)     // Catch:{ all -> 0x062b }
            boolean r11 = r11.equals(r7)     // Catch:{ all -> 0x062b }
            if (r11 != 0) goto L_0x0651
            r4 = r88
            r11 = 1
            goto L_0x0654
        L_0x0651:
            r4 = r88
            r11 = 0
        L_0x0654:
            if (r4 != 0) goto L_0x068f
            if (r11 == 0) goto L_0x0659
            goto L_0x068f
        L_0x0659:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x0686 }
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ all -> 0x0686 }
            r11 = -1
            if (r9 == r11) goto L_0x0662
            r12 = 1
            goto L_0x0663
        L_0x0662:
            r12 = 0
        L_0x0663:
            r1 = r68
            r11 = r3
            r3 = r5
            r10 = r4
            r4 = r14
            r5 = r80
            r14 = 0
            r7 = r82
            r9 = r86
            r11 = r70
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ all -> 0x0684 }
            r8 = r75
            r7 = r76
            r9 = r78
            r59 = r82
            r34 = r84
            r12 = r13
            r1 = -5
            r6 = 0
            goto L_0x131a
        L_0x0684:
            r0 = move-exception
            goto L_0x0688
        L_0x0686:
            r0 = move-exception
            r14 = 0
        L_0x0688:
            r8 = r75
            r11 = r76
            r9 = r78
            goto L_0x0630
        L_0x068f:
            r9 = r14
            r4 = -5
            r11 = -1
            r14 = 0
            if (r12 < 0) goto L_0x12db
            r22 = -2147483648(0xfffffffvar_, double:NaN)
            r3 = 1000(0x3e8, float:1.401E-42)
            int r3 = r3 / r13
            int r3 = r3 * 1000
            long r3 = (long) r3     // Catch:{ Exception -> 0x1250, all -> 0x1245 }
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x1250, all -> 0x1245 }
            r11.selectTrack(r12)     // Catch:{ Exception -> 0x1250, all -> 0x1245 }
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x1250, all -> 0x1245 }
            android.media.MediaFormat r11 = r11.getTrackFormat(r12)     // Catch:{ Exception -> 0x1250, all -> 0x1245 }
            r26 = 0
            int r20 = (r84 > r26 ? 1 : (r84 == r26 ? 0 : -1))
            if (r20 < 0) goto L_0x06ce
            r20 = 1157234688(0x44fa0000, float:2000.0)
            int r20 = (r25 > r20 ? 1 : (r25 == r20 ? 0 : -1))
            if (r20 > 0) goto L_0x06b9
            r20 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x06c7
        L_0x06b9:
            r20 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r20 = (r25 > r20 ? 1 : (r25 == r20 ? 0 : -1))
            if (r20 > 0) goto L_0x06c4
            r20 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x06c7
        L_0x06c4:
            r20 = 1560000(0x17cdc0, float:2.186026E-39)
        L_0x06c7:
            r28 = r3
            r2 = r20
            r26 = 0
            goto L_0x06de
        L_0x06ce:
            if (r78 > 0) goto L_0x06d8
            r26 = r84
            r28 = r3
            r2 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x06de
        L_0x06d8:
            r2 = r78
            r26 = r84
            r28 = r3
        L_0x06de:
            r3 = r79
            if (r3 <= 0) goto L_0x070a
            int r2 = java.lang.Math.min(r3, r2)     // Catch:{ Exception -> 0x06f5, all -> 0x06e7 }
            goto L_0x070a
        L_0x06e7:
            r0 = move-exception
            r8 = r75
            r11 = r76
            r59 = r82
            r9 = r2
            r12 = r13
            r34 = r26
        L_0x06f2:
            r1 = -5
            goto L_0x12d7
        L_0x06f5:
            r0 = move-exception
            r59 = r82
            r3 = r0
            r39 = r12
            r12 = r13
            r34 = r26
        L_0x06fe:
            r1 = -5
            r6 = 0
            r41 = 0
            r43 = 0
            r57 = 0
            r58 = 0
            goto L_0x1268
        L_0x070a:
            r30 = 0
            int r4 = (r26 > r30 ? 1 : (r26 == r30 ? 0 : -1))
            if (r4 < 0) goto L_0x0713
            r3 = r18
            goto L_0x0715
        L_0x0713:
            r3 = r26
        L_0x0715:
            int r26 = (r3 > r30 ? 1 : (r3 == r30 ? 0 : -1))
            if (r26 < 0) goto L_0x073d
            r26 = r5
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x0733, all -> 0x0727 }
            r5.seekTo(r3, r14)     // Catch:{ Exception -> 0x0733, all -> 0x0727 }
            r5 = r94
            r84 = r3
        L_0x0724:
            r3 = 0
            goto L_0x076f
        L_0x0727:
            r0 = move-exception
            r8 = r75
            r11 = r76
            r59 = r82
            r9 = r2
            r34 = r3
        L_0x0731:
            r12 = r13
            goto L_0x06f2
        L_0x0733:
            r0 = move-exception
            r59 = r82
            r34 = r3
            r39 = r12
            r12 = r13
            goto L_0x125d
        L_0x073d:
            r84 = r3
            r26 = r5
            r30 = 0
            r3 = r80
            int r5 = (r3 > r30 ? 1 : (r3 == r30 ? 0 : -1))
            if (r5 <= 0) goto L_0x0766
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x075c, all -> 0x0751 }
            r5.seekTo(r3, r14)     // Catch:{ Exception -> 0x075c, all -> 0x0751 }
            r5 = r94
            goto L_0x0724
        L_0x0751:
            r0 = move-exception
            r8 = r75
            r11 = r76
            r59 = r82
            r34 = r84
            r9 = r2
            goto L_0x0731
        L_0x075c:
            r0 = move-exception
            r59 = r82
            r34 = r84
            r3 = r0
            r39 = r12
            r12 = r13
            goto L_0x06fe
        L_0x0766:
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x123b, all -> 0x122b }
            r3 = 0
            r5.seekTo(r3, r14)     // Catch:{ Exception -> 0x123b, all -> 0x122b }
            r5 = r94
        L_0x076f:
            if (r5 == 0) goto L_0x0786
            r3 = 90
            r4 = r71
            if (r4 == r3) goto L_0x0781
            r3 = 270(0x10e, float:3.78E-43)
            if (r4 != r3) goto L_0x077c
            goto L_0x0781
        L_0x077c:
            int r3 = r5.transformWidth     // Catch:{ Exception -> 0x075c, all -> 0x0751 }
            int r14 = r5.transformHeight     // Catch:{ Exception -> 0x075c, all -> 0x0751 }
            goto L_0x078c
        L_0x0781:
            int r3 = r5.transformHeight     // Catch:{ Exception -> 0x075c, all -> 0x0751 }
            int r14 = r5.transformWidth     // Catch:{ Exception -> 0x075c, all -> 0x0751 }
            goto L_0x078c
        L_0x0786:
            r4 = r71
            r3 = r75
            r14 = r76
        L_0x078c:
            boolean r27 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x123b, all -> 0x122b }
            if (r27 == 0) goto L_0x07ac
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x075c, all -> 0x0751 }
            r4.<init>()     // Catch:{ Exception -> 0x075c, all -> 0x0751 }
            java.lang.String r5 = "create encoder with w = "
            r4.append(r5)     // Catch:{ Exception -> 0x075c, all -> 0x0751 }
            r4.append(r3)     // Catch:{ Exception -> 0x075c, all -> 0x0751 }
            java.lang.String r5 = " h = "
            r4.append(r5)     // Catch:{ Exception -> 0x075c, all -> 0x0751 }
            r4.append(r14)     // Catch:{ Exception -> 0x075c, all -> 0x0751 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x075c, all -> 0x0751 }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x075c, all -> 0x0751 }
        L_0x07ac:
            android.media.MediaFormat r4 = android.media.MediaFormat.createVideoFormat(r7, r3, r14)     // Catch:{ Exception -> 0x123b, all -> 0x122b }
            java.lang.String r5 = "color-format"
            r46 = r6
            r6 = 2130708361(0x7var_, float:1.701803E38)
            r4.setInteger(r5, r6)     // Catch:{ Exception -> 0x123b, all -> 0x122b }
            java.lang.String r5 = "bitrate"
            r4.setInteger(r5, r2)     // Catch:{ Exception -> 0x123b, all -> 0x122b }
            java.lang.String r5 = "frame-rate"
            r4.setInteger(r5, r13)     // Catch:{ Exception -> 0x123b, all -> 0x122b }
            java.lang.String r5 = "i-frame-interval"
            r6 = 2
            r4.setInteger(r5, r6)     // Catch:{ Exception -> 0x123b, all -> 0x122b }
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x123b, all -> 0x122b }
            r5 = 23
            if (r6 >= r5) goto L_0x07e8
            int r5 = java.lang.Math.min(r14, r3)     // Catch:{ Exception -> 0x075c, all -> 0x0751 }
            r27 = r3
            r3 = 480(0x1e0, float:6.73E-43)
            if (r5 > r3) goto L_0x07ea
            r3 = 921600(0xe1000, float:1.291437E-39)
            if (r2 <= r3) goto L_0x07e2
            r2 = 921600(0xe1000, float:1.291437E-39)
        L_0x07e2:
            java.lang.String r3 = "bitrate"
            r4.setInteger(r3, r2)     // Catch:{ Exception -> 0x075c, all -> 0x0751 }
            goto L_0x07ea
        L_0x07e8:
            r27 = r3
        L_0x07ea:
            r20 = r2
            android.media.MediaCodec r5 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x121d, all -> 0x120b }
            r2 = 1
            r3 = 0
            r5.configure(r4, r3, r3, r2)     // Catch:{ Exception -> 0x11f9, all -> 0x120b }
            org.telegram.messenger.video.InputSurface r4 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x11f9, all -> 0x120b }
            android.view.Surface r2 = r5.createInputSurface()     // Catch:{ Exception -> 0x11f9, all -> 0x120b }
            r4.<init>(r2)     // Catch:{ Exception -> 0x11f9, all -> 0x120b }
            r4.makeCurrent()     // Catch:{ Exception -> 0x11de, all -> 0x120b }
            r5.start()     // Catch:{ Exception -> 0x11de, all -> 0x120b }
            java.lang.String r2 = r11.getString(r1)     // Catch:{ Exception -> 0x11de, all -> 0x120b }
            android.media.MediaCodec r2 = android.media.MediaCodec.createDecoderByType(r2)     // Catch:{ Exception -> 0x11de, all -> 0x120b }
            r30 = r11
            org.telegram.messenger.video.OutputSurface r11 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x11c6, all -> 0x120b }
            r31 = 0
            float r3 = (float) r13
            r33 = 0
            r54 = r1
            r1 = r11
            r55 = r2
            r2 = r90
            r34 = r84
            r56 = r27
            r27 = r28
            r38 = 0
            r29 = r3
            r3 = r31
            r58 = r4
            r4 = r91
            r78 = r5
            r60 = r26
            r5 = r92
            r61 = r6
            r62 = r46
            r6 = r94
            r63 = r7
            r7 = r75
            r64 = r8
            r8 = r76
            r65 = r9
            r9 = r71
            r66 = r10
            r10 = r29
            r26 = r14
            r13 = r30
            r14 = r11
            r11 = r33
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x11b7, all -> 0x11b0 }
            java.lang.String r1 = createFragmentShader(r73, r74, r75, r76)     // Catch:{ Exception -> 0x1197, all -> 0x11b0 }
            r14.changeFragmentShader(r1)     // Catch:{ Exception -> 0x1197, all -> 0x11b0 }
            android.view.Surface r1 = r14.getSurface()     // Catch:{ Exception -> 0x1197, all -> 0x11b0 }
            r2 = r55
            r3 = 0
            r4 = 0
            r2.configure(r13, r1, r3, r4)     // Catch:{ Exception -> 0x118d, all -> 0x11b0 }
            r2.start()     // Catch:{ Exception -> 0x118d, all -> 0x11b0 }
            r1 = r61
            r4 = 21
            if (r1 >= r4) goto L_0x0896
            java.nio.ByteBuffer[] r6 = r2.getInputBuffers()     // Catch:{ Exception -> 0x0882, all -> 0x0876 }
            java.nio.ByteBuffer[] r1 = r78.getOutputBuffers()     // Catch:{ Exception -> 0x0882, all -> 0x0876 }
            goto L_0x0898
        L_0x0876:
            r0 = move-exception
        L_0x0877:
            r8 = r75
            r11 = r76
            r12 = r77
            r59 = r82
            r2 = r0
            goto L_0x1219
        L_0x0882:
            r0 = move-exception
            r6 = r78
            r59 = r82
            r43 = r2
            r57 = r3
        L_0x088b:
            r39 = r12
            r41 = r14
            r2 = r20
            r1 = -5
        L_0x0892:
            r12 = r77
            goto L_0x1267
        L_0x0896:
            r1 = r3
            r6 = r1
        L_0x0898:
            r4 = r60
            if (r4 < 0) goto L_0x09b6
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x09a0, all -> 0x0993 }
            android.media.MediaFormat r5 = r5.getTrackFormat(r4)     // Catch:{ Exception -> 0x09a0, all -> 0x0993 }
            r7 = r54
            java.lang.String r8 = r5.getString(r7)     // Catch:{ Exception -> 0x09a0, all -> 0x0993 }
            java.lang.String r9 = "audio/mp4a-latm"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x09a0, all -> 0x0993 }
            if (r8 != 0) goto L_0x08bf
            java.lang.String r8 = r5.getString(r7)     // Catch:{ Exception -> 0x0882, all -> 0x0876 }
            java.lang.String r9 = "audio/mpeg"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x0882, all -> 0x0876 }
            if (r8 == 0) goto L_0x08bd
            goto L_0x08bf
        L_0x08bd:
            r8 = 0
            goto L_0x08c0
        L_0x08bf:
            r8 = 1
        L_0x08c0:
            java.lang.String r7 = r5.getString(r7)     // Catch:{ Exception -> 0x09a0, all -> 0x0993 }
            java.lang.String r9 = "audio/unknown"
            boolean r7 = r7.equals(r9)     // Catch:{ Exception -> 0x09a0, all -> 0x0993 }
            if (r7 == 0) goto L_0x08cd
            r4 = -1
        L_0x08cd:
            if (r4 < 0) goto L_0x0987
            if (r8 == 0) goto L_0x0935
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ Exception -> 0x0920, all -> 0x091b }
            r9 = 1
            int r7 = r7.addTrack(r5, r9)     // Catch:{ Exception -> 0x0920, all -> 0x091b }
            android.media.MediaExtractor r9 = r15.extractor     // Catch:{ Exception -> 0x0920, all -> 0x091b }
            r9.selectTrack(r4)     // Catch:{ Exception -> 0x0920, all -> 0x091b }
            java.lang.String r9 = "max-input-size"
            int r5 = r5.getInteger(r9)     // Catch:{ Exception -> 0x08e4, all -> 0x0876 }
            goto L_0x08ea
        L_0x08e4:
            r0 = move-exception
            r5 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ Exception -> 0x0920, all -> 0x091b }
            r5 = 0
        L_0x08ea:
            if (r5 > 0) goto L_0x08ee
            r5 = 65536(0x10000, float:9.18355E-41)
        L_0x08ee:
            java.nio.ByteBuffer r9 = java.nio.ByteBuffer.allocateDirect(r5)     // Catch:{ Exception -> 0x0920, all -> 0x091b }
            r10 = r80
            r84 = r4
            r3 = 0
            int r13 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r13 <= 0) goto L_0x0905
            android.media.MediaExtractor r13 = r15.extractor     // Catch:{ Exception -> 0x0955, all -> 0x0876 }
            r3 = 0
            r13.seekTo(r10, r3)     // Catch:{ Exception -> 0x0955, all -> 0x0876 }
            r85 = r5
            goto L_0x090f
        L_0x0905:
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x0955, all -> 0x0876 }
            r85 = r5
            r4 = 0
            r13 = 0
            r3.seekTo(r4, r13)     // Catch:{ Exception -> 0x0955, all -> 0x0876 }
        L_0x090f:
            r3 = r82
            r29 = r85
            r5 = r7
            r13 = r9
            r7 = r84
            r9 = r8
            r8 = 0
            goto L_0x09c2
        L_0x091b:
            r0 = move-exception
            r10 = r80
            goto L_0x0877
        L_0x0920:
            r0 = move-exception
            r10 = r80
        L_0x0923:
            r6 = r78
            r59 = r82
            r3 = r0
            r43 = r2
            r39 = r12
            r41 = r14
            r2 = r20
            r1 = -5
            r57 = 0
            goto L_0x0be6
        L_0x0935:
            r10 = r80
            r84 = r4
            android.media.MediaExtractor r3 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0985, all -> 0x0983 }
            r3.<init>()     // Catch:{ Exception -> 0x0985, all -> 0x0983 }
            r4 = r69
            r3.setDataSource(r4)     // Catch:{ Exception -> 0x0985, all -> 0x0983 }
            r7 = r84
            r3.selectTrack(r7)     // Catch:{ Exception -> 0x0985, all -> 0x0983 }
            r84 = r8
            r8 = 0
            int r13 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r13 <= 0) goto L_0x0957
            r13 = 0
            r3.seekTo(r10, r13)     // Catch:{ Exception -> 0x0955, all -> 0x0876 }
            goto L_0x095b
        L_0x0955:
            r0 = move-exception
            goto L_0x0923
        L_0x0957:
            r13 = 0
            r3.seekTo(r8, r13)     // Catch:{ Exception -> 0x0985, all -> 0x0983 }
        L_0x095b:
            org.telegram.messenger.video.AudioRecoder r8 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x0985, all -> 0x0983 }
            r8.<init>(r5, r3, r7)     // Catch:{ Exception -> 0x0985, all -> 0x0983 }
            r8.startTime = r10     // Catch:{ Exception -> 0x0976, all -> 0x0983 }
            r3 = r82
            r8.endTime = r3     // Catch:{ Exception -> 0x0974, all -> 0x0972 }
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x0974, all -> 0x0972 }
            android.media.MediaFormat r9 = r8.format     // Catch:{ Exception -> 0x0974, all -> 0x0972 }
            r13 = 1
            int r5 = r5.addTrack(r9, r13)     // Catch:{ Exception -> 0x0974, all -> 0x0972 }
            r9 = r84
            goto L_0x09bf
        L_0x0972:
            r0 = move-exception
            goto L_0x0998
        L_0x0974:
            r0 = move-exception
            goto L_0x0979
        L_0x0976:
            r0 = move-exception
            r3 = r82
        L_0x0979:
            r6 = r78
            r43 = r2
            r59 = r3
            r57 = r8
            goto L_0x088b
        L_0x0983:
            r0 = move-exception
            goto L_0x0996
        L_0x0985:
            r0 = move-exception
            goto L_0x09a3
        L_0x0987:
            r10 = r80
            r7 = r4
            r84 = r8
            r3 = r82
            r9 = r84
            r5 = -5
            r8 = 0
            goto L_0x09bf
        L_0x0993:
            r0 = move-exception
            r10 = r80
        L_0x0996:
            r3 = r82
        L_0x0998:
            r8 = r75
            r11 = r76
            r12 = r77
            goto L_0x1216
        L_0x09a0:
            r0 = move-exception
            r10 = r80
        L_0x09a3:
            r3 = r82
            r6 = r78
            r43 = r2
            r59 = r3
            r39 = r12
            r41 = r14
            r2 = r20
            r1 = -5
            r57 = 0
            goto L_0x0892
        L_0x09b6:
            r10 = r80
            r5 = r4
            r3 = r82
            r7 = r5
            r5 = -5
            r8 = 0
            r9 = 1
        L_0x09bf:
            r13 = 0
            r29 = 0
        L_0x09c2:
            if (r7 >= 0) goto L_0x09c7
            r30 = 1
            goto L_0x09c9
        L_0x09c7:
            r30 = 0
        L_0x09c9:
            r68.checkConversionCanceled()     // Catch:{ Exception -> 0x1175, all -> 0x1170 }
            r38 = r13
            r54 = r18
            r39 = r22
            r44 = r30
            r82 = -5
            r22 = 0
            r23 = 1
            r31 = 0
            r32 = 0
            r36 = 0
            r37 = 0
            r13 = r1
            r1 = r29
            r29 = 0
        L_0x09e7:
            if (r31 == 0) goto L_0x0a09
            if (r9 != 0) goto L_0x09ee
            if (r44 != 0) goto L_0x09ee
            goto L_0x0a09
        L_0x09ee:
            r7 = r76
            r1 = r82
            r43 = r2
            r59 = r3
            r57 = r8
            r39 = r12
            r41 = r14
            r2 = r20
            r6 = 0
            r45 = 0
            r8 = r75
            r12 = r77
            r3 = r78
            goto L_0x12a8
        L_0x0a09:
            r68.checkConversionCanceled()     // Catch:{ Exception -> 0x1157, all -> 0x1147 }
            if (r9 != 0) goto L_0x0a3c
            if (r8 == 0) goto L_0x0a3c
            r83 = r13
            org.telegram.messenger.video.MP4Builder r13 = r15.mediaMuxer     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            boolean r13 = r8.step(r13, r5)     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            r44 = r13
            goto L_0x0a3e
        L_0x0a1b:
            r0 = move-exception
            r8 = r75
            r11 = r76
            r12 = r77
            r1 = r82
            r2 = r0
            r59 = r3
            goto L_0x1153
        L_0x0a29:
            r0 = move-exception
            r6 = r78
            r1 = r82
            r43 = r2
            r59 = r3
            r57 = r8
        L_0x0a34:
            r39 = r12
            r41 = r14
            r2 = r20
            goto L_0x0892
        L_0x0a3c:
            r83 = r13
        L_0x0a3e:
            if (r22 != 0) goto L_0x0bea
            android.media.MediaExtractor r13 = r15.extractor     // Catch:{ Exception -> 0x0bd1, all -> 0x0bc3 }
            int r13 = r13.getSampleTrackIndex()     // Catch:{ Exception -> 0x0bd1, all -> 0x0bc3 }
            if (r13 != r12) goto L_0x0aa8
            r10 = 2500(0x9c4, double:1.235E-320)
            int r13 = r2.dequeueInputBuffer(r10)     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            if (r13 < 0) goto L_0x0a90
            int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            r11 = 21
            if (r10 >= r11) goto L_0x0a59
            r10 = r6[r13]     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            goto L_0x0a5d
        L_0x0a59:
            java.nio.ByteBuffer r10 = r2.getInputBuffer(r13)     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
        L_0x0a5d:
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            r84 = r6
            r6 = 0
            int r50 = r11.readSampleData(r10, r6)     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            if (r50 >= 0) goto L_0x0a79
            r49 = 0
            r50 = 0
            r51 = 0
            r53 = 4
            r47 = r2
            r48 = r13
            r47.queueInputBuffer(r48, r49, r50, r51, r53)     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            r6 = 1
            goto L_0x0a94
        L_0x0a79:
            r49 = 0
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            long r51 = r6.getSampleTime()     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            r53 = 0
            r47 = r2
            r48 = r13
            r47.queueInputBuffer(r48, r49, r50, r51, r53)     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            android.media.MediaExtractor r6 = r15.extractor     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            r6.advance()     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            goto L_0x0a92
        L_0x0a90:
            r84 = r6
        L_0x0a92:
            r6 = r22
        L_0x0a94:
            r59 = r3
            r22 = r6
            r85 = r7
            r57 = r8
            r46 = r9
            r41 = r14
            r11 = r65
            r6 = 0
            r9 = r80
            r8 = r5
            goto L_0x0ba6
        L_0x0aa8:
            r84 = r6
            if (r9 == 0) goto L_0x0b91
            r6 = -1
            if (r7 == r6) goto L_0x0b91
            if (r13 != r7) goto L_0x0b91
            int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b89, all -> 0x0b83 }
            r11 = 28
            if (r10 < r11) goto L_0x0ace
            android.media.MediaExtractor r11 = r15.extractor     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            long r46 = r11.getSampleSize()     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            r85 = r7
            long r6 = (long) r1     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            int r13 = (r46 > r6 ? 1 : (r46 == r6 ? 0 : -1))
            if (r13 <= 0) goto L_0x0ad0
            r6 = 1024(0x400, double:5.06E-321)
            long r6 = r46 + r6
            int r1 = (int) r6     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            java.nio.ByteBuffer r38 = java.nio.ByteBuffer.allocateDirect(r1)     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            goto L_0x0ad0
        L_0x0ace:
            r85 = r7
        L_0x0ad0:
            r6 = r38
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x0b89, all -> 0x0b83 }
            r13 = 0
            int r7 = r7.readSampleData(r6, r13)     // Catch:{ Exception -> 0x0b89, all -> 0x0b83 }
            r11 = r65
            r11.size = r7     // Catch:{ Exception -> 0x0b89, all -> 0x0b83 }
            r7 = 21
            if (r10 >= r7) goto L_0x0ae9
            r6.position(r13)     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            int r7 = r11.size     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
            r6.limit(r7)     // Catch:{ Exception -> 0x0a29, all -> 0x0a1b }
        L_0x0ae9:
            int r7 = r11.size     // Catch:{ Exception -> 0x0b89, all -> 0x0b83 }
            if (r7 < 0) goto L_0x0b0c
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x0afe, all -> 0x0a1b }
            r10 = r8
            long r7 = r7.getSampleTime()     // Catch:{ Exception -> 0x0afc, all -> 0x0a1b }
            r11.presentationTimeUs = r7     // Catch:{ Exception -> 0x0afc, all -> 0x0a1b }
            android.media.MediaExtractor r7 = r15.extractor     // Catch:{ Exception -> 0x0afc, all -> 0x0a1b }
            r7.advance()     // Catch:{ Exception -> 0x0afc, all -> 0x0a1b }
            goto L_0x0b12
        L_0x0afc:
            r0 = move-exception
            goto L_0x0b00
        L_0x0afe:
            r0 = move-exception
            r10 = r8
        L_0x0b00:
            r6 = r78
            r1 = r82
            r43 = r2
            r59 = r3
            r57 = r10
            goto L_0x0a34
        L_0x0b0c:
            r10 = r8
            r7 = 0
            r11.size = r7     // Catch:{ Exception -> 0x0b78, all -> 0x0b83 }
            r22 = 1
        L_0x0b12:
            int r7 = r11.size     // Catch:{ Exception -> 0x0b78, all -> 0x0b83 }
            if (r7 <= 0) goto L_0x0b69
            r7 = 0
            int r13 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r13 < 0) goto L_0x0b22
            long r7 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0afc, all -> 0x0a1b }
            int r13 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r13 >= 0) goto L_0x0b69
        L_0x0b22:
            r7 = 0
            r11.offset = r7     // Catch:{ Exception -> 0x0b78, all -> 0x0b83 }
            android.media.MediaExtractor r8 = r15.extractor     // Catch:{ Exception -> 0x0b78, all -> 0x0b83 }
            int r8 = r8.getSampleFlags()     // Catch:{ Exception -> 0x0b78, all -> 0x0b83 }
            r11.flags = r8     // Catch:{ Exception -> 0x0b78, all -> 0x0b83 }
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x0b78, all -> 0x0b83 }
            r59 = r3
            long r3 = r8.writeSampleData(r5, r6, r11, r7)     // Catch:{ Exception -> 0x0b67, all -> 0x0b62 }
            r7 = 0
            int r13 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r13 == 0) goto L_0x0b6b
            org.telegram.messenger.MediaController$VideoConvertorListener r7 = r15.callback     // Catch:{ Exception -> 0x0b67, all -> 0x0b62 }
            if (r7 == 0) goto L_0x0b6b
            r8 = r5
            r13 = r6
            long r5 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0b67, all -> 0x0b62 }
            r46 = r9
            r57 = r10
            r41 = r14
            r14 = 2500(0x9c4, double:1.235E-320)
            r9 = r80
            long r49 = r5 - r9
            int r38 = (r49 > r29 ? 1 : (r49 == r29 ? 0 : -1))
            if (r38 <= 0) goto L_0x0b55
            long r29 = r5 - r9
        L_0x0b55:
            r5 = r29
            float r14 = (float) r5
            float r14 = r14 / r24
            float r14 = r14 / r25
            r7.didWriteData(r3, r14)     // Catch:{ Exception -> 0x0bc1, all -> 0x0bbf }
            r29 = r5
            goto L_0x0b75
        L_0x0b62:
            r0 = move-exception
            r9 = r80
            goto L_0x0bc7
        L_0x0b67:
            r0 = move-exception
            goto L_0x0b7b
        L_0x0b69:
            r59 = r3
        L_0x0b6b:
            r8 = r5
            r13 = r6
            r46 = r9
            r57 = r10
            r41 = r14
            r9 = r80
        L_0x0b75:
            r38 = r13
            goto L_0x0ba5
        L_0x0b78:
            r0 = move-exception
            r59 = r3
        L_0x0b7b:
            r57 = r10
            r41 = r14
            r9 = r80
            goto L_0x0bd9
        L_0x0b83:
            r0 = move-exception
            r9 = r80
            r59 = r3
            goto L_0x0bc7
        L_0x0b89:
            r0 = move-exception
            r9 = r80
            r59 = r3
            r57 = r8
            goto L_0x0bd7
        L_0x0b91:
            r59 = r3
            r85 = r7
            r57 = r8
            r46 = r9
            r41 = r14
            r11 = r65
            r9 = r80
            r8 = r5
            r3 = -1
            if (r13 != r3) goto L_0x0ba5
            r6 = 1
            goto L_0x0ba6
        L_0x0ba5:
            r6 = 0
        L_0x0ba6:
            if (r6 == 0) goto L_0x0bfa
            r4 = 2500(0x9c4, double:1.235E-320)
            int r48 = r2.dequeueInputBuffer(r4)     // Catch:{ Exception -> 0x0bc1, all -> 0x0bbf }
            if (r48 < 0) goto L_0x0bfc
            r49 = 0
            r50 = 0
            r51 = 0
            r53 = 4
            r47 = r2
            r47.queueInputBuffer(r48, r49, r50, r51, r53)     // Catch:{ Exception -> 0x0bc1, all -> 0x0bbf }
            r6 = 1
            goto L_0x0bfe
        L_0x0bbf:
            r0 = move-exception
            goto L_0x0bc7
        L_0x0bc1:
            r0 = move-exception
            goto L_0x0bd9
        L_0x0bc3:
            r0 = move-exception
            r59 = r3
            r9 = r10
        L_0x0bc7:
            r15 = r68
            r8 = r75
            r11 = r76
            r12 = r77
            goto L_0x1150
        L_0x0bd1:
            r0 = move-exception
            r59 = r3
            r57 = r8
            r9 = r10
        L_0x0bd7:
            r41 = r14
        L_0x0bd9:
            r15 = r68
            r6 = r78
            r1 = r82
            r3 = r0
            r43 = r2
            r39 = r12
            r2 = r20
        L_0x0be6:
            r12 = r77
            goto L_0x1268
        L_0x0bea:
            r59 = r3
            r84 = r6
            r85 = r7
            r57 = r8
            r46 = r9
            r9 = r10
            r41 = r14
            r11 = r65
            r8 = r5
        L_0x0bfa:
            r4 = 2500(0x9c4, double:1.235E-320)
        L_0x0bfc:
            r6 = r22
        L_0x0bfe:
            r7 = r36 ^ 1
            r14 = r83
            r22 = r6
            r13 = r7
            r3 = r39
            r7 = 1
            r6 = r82
        L_0x0c0a:
            if (r13 != 0) goto L_0x0CLASSNAME
            if (r7 == 0) goto L_0x0c0f
            goto L_0x0CLASSNAME
        L_0x0c0f:
            r15 = r68
            r7 = r85
            r39 = r3
            r82 = r6
            r5 = r8
            r65 = r11
            r13 = r14
            r14 = r41
            r8 = r57
            r3 = r59
            r6 = r84
            r10 = r9
            r9 = r46
            goto L_0x09e7
        L_0x0CLASSNAME:
            r68.checkConversionCanceled()     // Catch:{ Exception -> 0x1133, all -> 0x1127 }
            if (r89 == 0) goto L_0x0CLASSNAME
            r39 = 22000(0x55f0, double:1.08694E-319)
            r5 = r78
            r82 = r7
            r83 = r8
            r7 = r39
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r5 = r78
            r82 = r7
            r83 = r8
            r7 = 2500(0x9c4, double:1.235E-320)
        L_0x0CLASSNAME:
            int r7 = r5.dequeueOutputBuffer(r11, r7)     // Catch:{ Exception -> 0x1122, all -> 0x1127 }
            r8 = -1
            if (r7 != r8) goto L_0x0c5e
            r15 = r68
            r40 = r1
            r49 = r3
            r39 = r12
            r78 = r13
            r3 = r56
            r12 = r62
            r8 = r63
            r1 = -1
            r4 = 0
        L_0x0CLASSNAME:
            r13 = r5
            r5 = r26
            goto L_0x0e78
        L_0x0c5e:
            r15 = -3
            if (r7 != r15) goto L_0x0c9a
            int r15 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c8a, all -> 0x0c7f }
            r8 = 21
            if (r15 >= r8) goto L_0x0c6b
            java.nio.ByteBuffer[] r14 = r5.getOutputBuffers()     // Catch:{ Exception -> 0x0c8a, all -> 0x0c7f }
        L_0x0c6b:
            r15 = r68
            r40 = r1
            r49 = r3
            r39 = r12
            r78 = r13
            r3 = r56
            r12 = r62
            r8 = r63
            r1 = -1
            r4 = r82
            goto L_0x0CLASSNAME
        L_0x0c7f:
            r0 = move-exception
            r15 = r68
        L_0x0CLASSNAME:
            r8 = r75
            r11 = r76
            r12 = r77
            goto L_0x1130
        L_0x0c8a:
            r0 = move-exception
            r15 = r68
            r3 = r0
            r43 = r2
            r1 = r6
            r39 = r12
            r2 = r20
            r12 = r77
        L_0x0CLASSNAME:
            r6 = r5
            goto L_0x1268
        L_0x0c9a:
            r8 = -2
            if (r7 != r8) goto L_0x0d0e
            android.media.MediaFormat r8 = r5.getOutputFormat()     // Catch:{ Exception -> 0x0d00, all -> 0x0c7f }
            r15 = -5
            if (r6 != r15) goto L_0x0ce7
            if (r8 == 0) goto L_0x0ce7
            r15 = r68
            r39 = r12
            r78 = r13
            org.telegram.messenger.video.MP4Builder r12 = r15.mediaMuxer     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            r13 = 0
            int r6 = r12.addTrack(r8, r13)     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            r12 = r66
            boolean r13 = r8.containsKey(r12)     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            if (r13 == 0) goto L_0x0cde
            int r13 = r8.getInteger(r12)     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            r40 = r1
            r1 = 1
            if (r13 != r1) goto L_0x0ce0
            r1 = r64
            java.nio.ByteBuffer r13 = r8.getByteBuffer(r1)     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            r66 = r12
            r12 = r62
            java.nio.ByteBuffer r8 = r8.getByteBuffer(r12)     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            int r13 = r13.limit()     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            int r8 = r8.limit()     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            int r13 = r13 + r8
            r37 = r13
            goto L_0x0cf0
        L_0x0cde:
            r40 = r1
        L_0x0ce0:
            r66 = r12
        L_0x0ce2:
            r12 = r62
            r1 = r64
            goto L_0x0cf0
        L_0x0ce7:
            r15 = r68
            r40 = r1
            r39 = r12
            r78 = r13
            goto L_0x0ce2
        L_0x0cf0:
            r64 = r1
            r49 = r3
            r13 = r5
            r5 = r26
            r3 = r56
            r8 = r63
            r1 = -1
        L_0x0cfc:
            r4 = r82
            goto L_0x0e78
        L_0x0d00:
            r0 = move-exception
            r15 = r68
            r39 = r12
        L_0x0d05:
            r12 = r77
            r3 = r0
            r43 = r2
            r1 = r6
            r2 = r20
            goto L_0x0CLASSNAME
        L_0x0d0e:
            r15 = r68
            r40 = r1
            r39 = r12
            r78 = r13
            r12 = r62
            r1 = r64
            if (r7 < 0) goto L_0x10ff
            int r8 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x10f8, all -> 0x10f6 }
            r13 = 21
            if (r8 >= r13) goto L_0x0d2a
            r8 = r14[r7]     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            goto L_0x0d2e
        L_0x0d25:
            r0 = move-exception
            goto L_0x0CLASSNAME
        L_0x0d28:
            r0 = move-exception
            goto L_0x0d05
        L_0x0d2a:
            java.nio.ByteBuffer r8 = r5.getOutputBuffer(r7)     // Catch:{ Exception -> 0x10f8, all -> 0x10f6 }
        L_0x0d2e:
            if (r8 == 0) goto L_0x10d5
            int r13 = r11.size     // Catch:{ Exception -> 0x10f8, all -> 0x10f6 }
            r31 = r14
            r14 = 1
            if (r13 <= r14) goto L_0x0e5a
            int r14 = r11.flags     // Catch:{ Exception -> 0x0e4b, all -> 0x0d25 }
            r42 = r14 & 2
            if (r42 != 0) goto L_0x0dd2
            if (r37 == 0) goto L_0x0d50
            r42 = r14 & 1
            if (r42 == 0) goto L_0x0d50
            r49 = r3
            int r3 = r11.offset     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            int r3 = r3 + r37
            r11.offset = r3     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            int r13 = r13 - r37
            r11.size = r13     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            goto L_0x0d52
        L_0x0d50:
            r49 = r3
        L_0x0d52:
            if (r23 == 0) goto L_0x0da1
            r3 = r14 & 1
            if (r3 == 0) goto L_0x0da1
            int r3 = r11.size     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            r4 = 100
            if (r3 <= r4) goto L_0x0d9f
            int r3 = r11.offset     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            r8.position(r3)     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            byte[] r3 = new byte[r4]     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            r8.get(r3)     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            r13 = 0
            r14 = 0
        L_0x0d6a:
            r4 = 96
            if (r13 >= r4) goto L_0x0d9f
            byte r4 = r3[r13]     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            if (r4 != 0) goto L_0x0d96
            int r4 = r13 + 1
            byte r4 = r3[r4]     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            if (r4 != 0) goto L_0x0d96
            int r4 = r13 + 2
            byte r4 = r3[r4]     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            if (r4 != 0) goto L_0x0d96
            int r4 = r13 + 3
            byte r4 = r3[r4]     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            r23 = r3
            r3 = 1
            if (r4 != r3) goto L_0x0d98
            int r14 = r14 + 1
            if (r14 <= r3) goto L_0x0d98
            int r3 = r11.offset     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            int r3 = r3 + r13
            r11.offset = r3     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            int r3 = r11.size     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            int r3 = r3 - r13
            r11.size = r3     // Catch:{ Exception -> 0x0d28, all -> 0x0d25 }
            goto L_0x0d9f
        L_0x0d96:
            r23 = r3
        L_0x0d98:
            int r13 = r13 + 1
            r3 = r23
            r4 = 100
            goto L_0x0d6a
        L_0x0d9f:
            r23 = 0
        L_0x0da1:
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ Exception -> 0x0e4b, all -> 0x0d25 }
            r4 = 1
            long r13 = r3.writeSampleData(r6, r8, r11, r4)     // Catch:{ Exception -> 0x0e4b, all -> 0x0d25 }
            r3 = 0
            int r8 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1))
            if (r8 == 0) goto L_0x0e5c
            org.telegram.messenger.MediaController$VideoConvertorListener r3 = r15.callback     // Catch:{ Exception -> 0x0e4b, all -> 0x0d25 }
            if (r3 == 0) goto L_0x0e5c
            r42 = r5
            long r4 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            long r51 = r4 - r9
            int r8 = (r51 > r29 ? 1 : (r51 == r29 ? 0 : -1))
            if (r8 <= 0) goto L_0x0dbe
            long r29 = r4 - r9
        L_0x0dbe:
            r4 = r29
            float r8 = (float) r4     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            float r8 = r8 / r24
            float r8 = r8 / r25
            r3.didWriteData(r13, r8)     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            r29 = r4
        L_0x0dca:
            r5 = r26
            r3 = r56
            r8 = r63
            goto L_0x0e60
        L_0x0dd2:
            r49 = r3
            r42 = r5
            r3 = -5
            if (r6 != r3) goto L_0x0dca
            byte[] r3 = new byte[r13]     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            int r4 = r11.offset     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            int r4 = r4 + r13
            r8.limit(r4)     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            int r4 = r11.offset     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            r8.position(r4)     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            r8.get(r3)     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            int r4 = r11.size     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            r5 = 1
            int r4 = r4 - r5
        L_0x0ded:
            if (r4 < 0) goto L_0x0e2a
            r8 = 3
            if (r4 <= r8) goto L_0x0e2a
            byte r13 = r3[r4]     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            if (r13 != r5) goto L_0x0e26
            int r13 = r4 + -1
            byte r13 = r3[r13]     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            if (r13 != 0) goto L_0x0e26
            int r13 = r4 + -2
            byte r13 = r3[r13]     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            if (r13 != 0) goto L_0x0e26
            int r13 = r4 + -3
            byte r14 = r3[r13]     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            if (r14 != 0) goto L_0x0e26
            java.nio.ByteBuffer r4 = java.nio.ByteBuffer.allocate(r13)     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            int r14 = r11.size     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            int r14 = r14 - r13
            java.nio.ByteBuffer r14 = java.nio.ByteBuffer.allocate(r14)     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            r5 = 0
            java.nio.ByteBuffer r8 = r4.put(r3, r5, r13)     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            r8.position(r5)     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            int r8 = r11.size     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            int r8 = r8 - r13
            java.nio.ByteBuffer r3 = r14.put(r3, r13, r8)     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            r3.position(r5)     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            goto L_0x0e2c
        L_0x0e26:
            int r4 = r4 + -1
            r5 = 1
            goto L_0x0ded
        L_0x0e2a:
            r4 = 0
            r14 = 0
        L_0x0e2c:
            r5 = r26
            r3 = r56
            r8 = r63
            android.media.MediaFormat r13 = android.media.MediaFormat.createVideoFormat(r8, r3, r5)     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            if (r4 == 0) goto L_0x0e40
            if (r14 == 0) goto L_0x0e40
            r13.setByteBuffer(r1, r4)     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            r13.setByteBuffer(r12, r14)     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
        L_0x0e40:
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            r14 = 0
            int r4 = r4.addTrack(r13, r14)     // Catch:{ Exception -> 0x0e49, all -> 0x0d25 }
            r6 = r4
            goto L_0x0e60
        L_0x0e49:
            r0 = move-exception
            goto L_0x0e4e
        L_0x0e4b:
            r0 = move-exception
            r42 = r5
        L_0x0e4e:
            r12 = r77
            r3 = r0
            r43 = r2
            r1 = r6
            r2 = r20
            r6 = r42
            goto L_0x1268
        L_0x0e5a:
            r49 = r3
        L_0x0e5c:
            r42 = r5
            goto L_0x0dca
        L_0x0e60:
            int r4 = r11.flags     // Catch:{ Exception -> 0x10cf, all -> 0x10f6 }
            r4 = r4 & 4
            r13 = r42
            if (r4 == 0) goto L_0x0e6a
            r4 = 1
            goto L_0x0e6b
        L_0x0e6a:
            r4 = 0
        L_0x0e6b:
            r14 = 0
            r13.releaseOutputBuffer(r7, r14)     // Catch:{ Exception -> 0x10cb, all -> 0x10f6 }
            r64 = r1
            r14 = r31
            r1 = -1
            r31 = r4
            goto L_0x0cfc
        L_0x0e78:
            if (r7 == r1) goto L_0x0e93
            r56 = r3
            r7 = r4
            r26 = r5
            r63 = r8
            r62 = r12
            r12 = r39
            r1 = r40
            r3 = r49
            r8 = r83
            r67 = r13
            r13 = r78
            r78 = r67
            goto L_0x0c0a
        L_0x0e93:
            if (r36 != 0) goto L_0x109d
            r56 = r3
            r82 = r4
            r3 = 2500(0x9c4, double:1.235E-320)
            int r7 = r2.dequeueOutputBuffer(r11, r3)     // Catch:{ Exception -> 0x108c, all -> 0x107c }
            if (r7 != r1) goto L_0x0eb5
            r42 = r5
            r47 = r6
            r63 = r8
            r62 = r12
            r6 = r49
            r1 = r58
            r3 = 0
            r5 = -5
            r8 = 0
            r12 = r77
            goto L_0x10b5
        L_0x0eb5:
            r1 = -3
            if (r7 != r1) goto L_0x0eba
            goto L_0x10a1
        L_0x0eba:
            r1 = -2
            if (r7 != r1) goto L_0x0ede
            android.media.MediaFormat r1 = r2.getOutputFormat()     // Catch:{ Exception -> 0x0edb, all -> 0x0d25 }
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0edb, all -> 0x0d25 }
            if (r7 == 0) goto L_0x10a1
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0edb, all -> 0x0d25 }
            r7.<init>()     // Catch:{ Exception -> 0x0edb, all -> 0x0d25 }
            java.lang.String r3 = "newFormat = "
            r7.append(r3)     // Catch:{ Exception -> 0x0edb, all -> 0x0d25 }
            r7.append(r1)     // Catch:{ Exception -> 0x0edb, all -> 0x0d25 }
            java.lang.String r1 = r7.toString()     // Catch:{ Exception -> 0x0edb, all -> 0x0d25 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0edb, all -> 0x0d25 }
            goto L_0x10a1
        L_0x0edb:
            r0 = move-exception
            goto L_0x113c
        L_0x0ede:
            if (r7 < 0) goto L_0x1058
            int r1 = r11.size     // Catch:{ Exception -> 0x108c, all -> 0x107c }
            if (r1 == 0) goto L_0x0ee6
            r1 = 1
            goto L_0x0ee7
        L_0x0ee6:
            r1 = 0
        L_0x0ee7:
            long r3 = r11.presentationTimeUs     // Catch:{ Exception -> 0x108c, all -> 0x107c }
            r51 = 0
            int r26 = (r59 > r51 ? 1 : (r59 == r51 ? 0 : -1))
            if (r26 <= 0) goto L_0x0efe
            int r26 = (r3 > r59 ? 1 : (r3 == r59 ? 0 : -1))
            if (r26 < 0) goto L_0x0efe
            int r1 = r11.flags     // Catch:{ Exception -> 0x0edb, all -> 0x0d25 }
            r1 = r1 | 4
            r11.flags = r1     // Catch:{ Exception -> 0x0edb, all -> 0x0d25 }
            r1 = 0
            r22 = 1
            r36 = 1
        L_0x0efe:
            r51 = 0
            int r26 = (r34 > r51 ? 1 : (r34 == r51 ? 0 : -1))
            if (r26 < 0) goto L_0x0var_
            r26 = r1
            int r1 = r11.flags     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x0var_
            long r51 = r34 - r9
            long r51 = java.lang.Math.abs(r51)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r1 = 1000000(0xvar_, float:1.401298E-39)
            r62 = r12
            r12 = r77
            int r1 = r1 / r12
            r42 = r5
            r47 = r6
            long r5 = (long) r1
            int r1 = (r51 > r5 ? 1 : (r51 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0var_
            r5 = 0
            int r1 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0var_
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
            r5 = 0
            r1.seekTo(r9, r5)     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
            r63 = r8
            r8 = 0
            goto L_0x0f3d
        L_0x0var_:
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0fe7, all -> 0x0var_ }
            r63 = r8
            r5 = 0
            r8 = 0
            r1.seekTo(r5, r8)     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
        L_0x0f3d:
            long r32 = r49 + r27
            int r1 = r11.flags     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r5 = -5
            r1 = r1 & r5
            r11.flags = r1     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r2.flush()     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r59 = r34
            r6 = 1
            r22 = 0
            r26 = 0
            r36 = 0
            r51 = 0
            r34 = r18
            goto L_0x0fa1
        L_0x0var_:
            r0 = move-exception
            r8 = r75
            r11 = r76
            r2 = r0
            r9 = r20
            r59 = r34
            r1 = r47
            r7 = 0
            r34 = r18
            goto L_0x1364
        L_0x0var_:
            r0 = move-exception
            r3 = r0
            r43 = r2
            r6 = r13
            r2 = r20
            r59 = r34
            r1 = r47
            r34 = r18
            goto L_0x1268
        L_0x0var_:
            r0 = move-exception
            goto L_0x0var_
        L_0x0var_:
            r63 = r8
            r5 = -5
            r8 = 0
            goto L_0x0f9e
        L_0x0f7d:
            r0 = move-exception
            goto L_0x0var_
        L_0x0f7f:
            r0 = move-exception
            goto L_0x0f8c
        L_0x0var_:
            r0 = move-exception
            r12 = r77
        L_0x0var_:
            r47 = r6
        L_0x0var_:
            r8 = 0
            goto L_0x1081
        L_0x0var_:
            r0 = move-exception
            r12 = r77
        L_0x0f8c:
            r47 = r6
            goto L_0x1093
        L_0x0var_:
            r26 = r1
        L_0x0var_:
            r42 = r5
            r47 = r6
            r63 = r8
            r62 = r12
            r5 = -5
            r8 = 0
            r12 = r77
        L_0x0f9e:
            r6 = 0
            r51 = 0
        L_0x0fa1:
            int r1 = (r34 > r51 ? 1 : (r34 == r51 ? 0 : -1))
            if (r1 < 0) goto L_0x0fa8
            r8 = r34
            goto L_0x0fa9
        L_0x0fa8:
            r8 = r9
        L_0x0fa9:
            int r1 = (r8 > r51 ? 1 : (r8 == r51 ? 0 : -1))
            if (r1 <= 0) goto L_0x0fea
            int r1 = (r54 > r18 ? 1 : (r54 == r18 ? 0 : -1))
            if (r1 != 0) goto L_0x0fea
            int r1 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r1 >= 0) goto L_0x0fd9
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
            if (r1 == 0) goto L_0x0fd7
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
            r1.<init>()     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
            java.lang.String r3 = "drop frame startTime = "
            r1.append(r3)     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
            r1.append(r8)     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
            java.lang.String r3 = " present time = "
            r1.append(r3)     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
            long r3 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
            r1.append(r3)     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
        L_0x0fd7:
            r1 = 0
            goto L_0x0fec
        L_0x0fd9:
            long r3 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
            r8 = -2147483648(0xfffffffvar_, double:NaN)
            int r1 = (r49 > r8 ? 1 : (r49 == r8 ? 0 : -1))
            if (r1 == 0) goto L_0x0fe4
            long r32 = r32 - r3
        L_0x0fe4:
            r54 = r3
            goto L_0x0fea
        L_0x0fe7:
            r0 = move-exception
            goto L_0x1093
        L_0x0fea:
            r1 = r26
        L_0x0fec:
            if (r6 == 0) goto L_0x0ff1
            r54 = r18
            goto L_0x1004
        L_0x0ff1:
            int r3 = (r34 > r18 ? 1 : (r34 == r18 ? 0 : -1))
            if (r3 != 0) goto L_0x1001
            r3 = 0
            int r6 = (r32 > r3 ? 1 : (r32 == r3 ? 0 : -1))
            if (r6 == 0) goto L_0x1001
            long r3 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
            long r3 = r3 + r32
            r11.presentationTimeUs = r3     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
        L_0x1001:
            r2.releaseOutputBuffer(r7, r1)     // Catch:{ Exception -> 0x1056, all -> 0x1075 }
        L_0x1004:
            if (r1 == 0) goto L_0x103a
            r3 = 0
            int r1 = (r34 > r3 ? 1 : (r34 == r3 ? 0 : -1))
            if (r1 < 0) goto L_0x1015
            long r6 = r11.presentationTimeUs     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
            r8 = r49
            long r6 = java.lang.Math.max(r8, r6)     // Catch:{ Exception -> 0x0fe7, all -> 0x1075 }
            goto L_0x1018
        L_0x1015:
            r8 = r49
            r6 = r8
        L_0x1018:
            r41.awaitNewImage()     // Catch:{ Exception -> 0x101d, all -> 0x1075 }
            r1 = 0
            goto L_0x1023
        L_0x101d:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x1056, all -> 0x1075 }
            r1 = 1
        L_0x1023:
            if (r1 != 0) goto L_0x1037
            r41.drawImage()     // Catch:{ Exception -> 0x1056, all -> 0x1075 }
            long r8 = r11.presentationTimeUs     // Catch:{ Exception -> 0x1056, all -> 0x1075 }
            r49 = 1000(0x3e8, double:4.94E-321)
            long r8 = r8 * r49
            r1 = r58
            r1.setPresentationTime(r8)     // Catch:{ Exception -> 0x1077, all -> 0x1075 }
            r1.swapBuffers()     // Catch:{ Exception -> 0x1077, all -> 0x1075 }
            goto L_0x1041
        L_0x1037:
            r1 = r58
            goto L_0x1041
        L_0x103a:
            r8 = r49
            r1 = r58
            r3 = 0
            r6 = r8
        L_0x1041:
            int r8 = r11.flags     // Catch:{ Exception -> 0x1077, all -> 0x1075 }
            r8 = r8 & 4
            if (r8 == 0) goto L_0x10b3
            boolean r8 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1077, all -> 0x1075 }
            if (r8 == 0) goto L_0x1050
            java.lang.String r8 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r8)     // Catch:{ Exception -> 0x1077, all -> 0x1075 }
        L_0x1050:
            r13.signalEndOfInputStream()     // Catch:{ Exception -> 0x1077, all -> 0x1075 }
            r8 = 0
            goto L_0x10b5
        L_0x1056:
            r0 = move-exception
            goto L_0x1091
        L_0x1058:
            r12 = r77
            r47 = r6
            r1 = r58
            java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1077, all -> 0x1075 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1077, all -> 0x1075 }
            r4.<init>()     // Catch:{ Exception -> 0x1077, all -> 0x1075 }
            java.lang.String r5 = "unexpected result from decoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x1077, all -> 0x1075 }
            r4.append(r7)     // Catch:{ Exception -> 0x1077, all -> 0x1075 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x1077, all -> 0x1075 }
            r3.<init>(r4)     // Catch:{ Exception -> 0x1077, all -> 0x1075 }
            throw r3     // Catch:{ Exception -> 0x1077, all -> 0x1075 }
        L_0x1075:
            r0 = move-exception
            goto L_0x1081
        L_0x1077:
            r0 = move-exception
            r3 = r0
            r58 = r1
            goto L_0x1094
        L_0x107c:
            r0 = move-exception
            r12 = r77
            r47 = r6
        L_0x1081:
            r8 = r75
            r11 = r76
            r2 = r0
            r9 = r20
            r1 = r47
            goto L_0x1363
        L_0x108c:
            r0 = move-exception
            r12 = r77
            r47 = r6
        L_0x1091:
            r1 = r58
        L_0x1093:
            r3 = r0
        L_0x1094:
            r43 = r2
            r6 = r13
            r2 = r20
            r1 = r47
            goto L_0x1268
        L_0x109d:
            r56 = r3
            r82 = r4
        L_0x10a1:
            r42 = r5
            r47 = r6
            r63 = r8
            r62 = r12
            r8 = r49
            r1 = r58
            r3 = 0
            r5 = -5
            r12 = r77
            r6 = r8
        L_0x10b3:
            r8 = r78
        L_0x10b5:
            r9 = r80
            r58 = r1
            r3 = r6
            r78 = r13
            r12 = r39
            r1 = r40
            r26 = r42
            r6 = r47
            r7 = r82
            r13 = r8
            r8 = r83
            goto L_0x0c0a
        L_0x10cb:
            r0 = move-exception
            r12 = r77
            goto L_0x10fc
        L_0x10cf:
            r0 = move-exception
            r12 = r77
            r13 = r42
            goto L_0x10fc
        L_0x10d5:
            r12 = r77
            r13 = r5
            r1 = r58
            java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x111d, all -> 0x111b }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x111d, all -> 0x111b }
            r4.<init>()     // Catch:{ Exception -> 0x111d, all -> 0x111b }
            java.lang.String r5 = "encoderOutputBuffer "
            r4.append(r5)     // Catch:{ Exception -> 0x111d, all -> 0x111b }
            r4.append(r7)     // Catch:{ Exception -> 0x111d, all -> 0x111b }
            java.lang.String r5 = " was null"
            r4.append(r5)     // Catch:{ Exception -> 0x111d, all -> 0x111b }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x111d, all -> 0x111b }
            r3.<init>(r4)     // Catch:{ Exception -> 0x111d, all -> 0x111b }
            throw r3     // Catch:{ Exception -> 0x111d, all -> 0x111b }
        L_0x10f6:
            r0 = move-exception
            goto L_0x112a
        L_0x10f8:
            r0 = move-exception
            r12 = r77
            r13 = r5
        L_0x10fc:
            r1 = r58
            goto L_0x113e
        L_0x10ff:
            r12 = r77
            r13 = r5
            r1 = r58
            java.lang.RuntimeException r3 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x111d, all -> 0x111b }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x111d, all -> 0x111b }
            r4.<init>()     // Catch:{ Exception -> 0x111d, all -> 0x111b }
            java.lang.String r5 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x111d, all -> 0x111b }
            r4.append(r7)     // Catch:{ Exception -> 0x111d, all -> 0x111b }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x111d, all -> 0x111b }
            r3.<init>(r4)     // Catch:{ Exception -> 0x111d, all -> 0x111b }
            throw r3     // Catch:{ Exception -> 0x111d, all -> 0x111b }
        L_0x111b:
            r0 = move-exception
            goto L_0x112c
        L_0x111d:
            r0 = move-exception
            r3 = r0
            r58 = r1
            goto L_0x113f
        L_0x1122:
            r0 = move-exception
            r15 = r68
            r13 = r5
            goto L_0x1138
        L_0x1127:
            r0 = move-exception
            r15 = r68
        L_0x112a:
            r12 = r77
        L_0x112c:
            r8 = r75
            r11 = r76
        L_0x1130:
            r2 = r0
            r1 = r6
            goto L_0x1153
        L_0x1133:
            r0 = move-exception
            r15 = r68
            r13 = r78
        L_0x1138:
            r39 = r12
            r1 = r58
        L_0x113c:
            r12 = r77
        L_0x113e:
            r3 = r0
        L_0x113f:
            r43 = r2
            r1 = r6
            r6 = r13
            r2 = r20
            goto L_0x1268
        L_0x1147:
            r0 = move-exception
            r12 = r77
            r59 = r3
            r8 = r75
            r11 = r76
        L_0x1150:
            r1 = r82
            r2 = r0
        L_0x1153:
            r9 = r20
            goto L_0x1363
        L_0x1157:
            r0 = move-exception
            r13 = r78
            r59 = r3
            r57 = r8
            r39 = r12
            r41 = r14
            r1 = r58
            r12 = r77
            r3 = r0
            r43 = r2
            r6 = r13
            r2 = r20
            r1 = r82
            goto L_0x1268
        L_0x1170:
            r0 = move-exception
            r12 = r77
            goto L_0x1211
        L_0x1175:
            r0 = move-exception
            r13 = r78
            r57 = r8
            r39 = r12
            r41 = r14
            r1 = r58
            r5 = -5
            r12 = r77
            r43 = r2
            r59 = r3
            r6 = r13
            r2 = r20
            r1 = -5
            goto L_0x1267
        L_0x118d:
            r0 = move-exception
            r13 = r78
            r3 = r82
            r39 = r12
            r41 = r14
            goto L_0x11a2
        L_0x1197:
            r0 = move-exception
            r13 = r78
            r3 = r82
            r39 = r12
            r41 = r14
            r2 = r55
        L_0x11a2:
            r1 = r58
            r5 = -5
            r12 = r77
            r43 = r2
            r59 = r3
            r6 = r13
            r2 = r20
            r1 = -5
            goto L_0x11f5
        L_0x11b0:
            r0 = move-exception
            r12 = r77
            r3 = r82
            goto L_0x1211
        L_0x11b7:
            r0 = move-exception
            r13 = r78
            r3 = r82
            r39 = r12
            r2 = r55
            r1 = r58
            r5 = -5
            r12 = r77
            goto L_0x11d3
        L_0x11c6:
            r0 = move-exception
            r34 = r84
            r1 = r4
            r39 = r12
            r12 = r13
            r3 = r82
            r13 = r5
            r5 = -5
            r58 = r1
        L_0x11d3:
            r43 = r2
            r59 = r3
            r6 = r13
            r2 = r20
            r1 = -5
            r41 = 0
            goto L_0x11f5
        L_0x11de:
            r0 = move-exception
            r34 = r84
            r1 = r4
            r39 = r12
            r12 = r13
            r3 = r82
            r13 = r5
            r5 = -5
            r58 = r1
            r59 = r3
            r6 = r13
            r2 = r20
            r1 = -5
            r41 = 0
            r43 = 0
        L_0x11f5:
            r57 = 0
            goto L_0x1267
        L_0x11f9:
            r0 = move-exception
            r3 = r82
            r34 = r84
            r39 = r12
            r12 = r13
            r13 = r5
            r5 = -5
            r59 = r3
            r6 = r13
            r2 = r20
            r1 = -5
            goto L_0x125f
        L_0x120b:
            r0 = move-exception
            r3 = r82
            r34 = r84
            r12 = r13
        L_0x1211:
            r5 = -5
            r8 = r75
            r11 = r76
        L_0x1216:
            r2 = r0
            r59 = r3
        L_0x1219:
            r9 = r20
            goto L_0x1362
        L_0x121d:
            r0 = move-exception
            r3 = r82
            r34 = r84
            r39 = r12
            r12 = r13
            r5 = -5
            r59 = r3
            r2 = r20
            goto L_0x125d
        L_0x122b:
            r0 = move-exception
            r3 = r82
            r34 = r84
            r12 = r13
            r5 = -5
            r8 = r75
            r11 = r76
            r9 = r2
            r59 = r3
            goto L_0x06f2
        L_0x123b:
            r0 = move-exception
            r3 = r82
            r34 = r84
            r39 = r12
            r12 = r13
            r5 = -5
            goto L_0x125b
        L_0x1245:
            r0 = move-exception
            r3 = r82
            r12 = r13
            r5 = -5
            r8 = r75
            r11 = r76
            goto L_0x135b
        L_0x1250:
            r0 = move-exception
            r3 = r82
            r39 = r12
            r12 = r13
            r5 = -5
            r2 = r78
            r34 = r84
        L_0x125b:
            r59 = r3
        L_0x125d:
            r1 = -5
            r6 = 0
        L_0x125f:
            r41 = 0
            r43 = 0
            r57 = 0
            r58 = 0
        L_0x1267:
            r3 = r0
        L_0x1268:
            boolean r4 = r3 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x12d0 }
            if (r4 == 0) goto L_0x1271
            if (r89 != 0) goto L_0x1271
            r45 = 1
            goto L_0x1273
        L_0x1271:
            r45 = 0
        L_0x1273:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x12c6 }
            r4.<init>()     // Catch:{ all -> 0x12c6 }
            java.lang.String r5 = "bitrate: "
            r4.append(r5)     // Catch:{ all -> 0x12c6 }
            r4.append(r2)     // Catch:{ all -> 0x12c6 }
            java.lang.String r5 = " framerate: "
            r4.append(r5)     // Catch:{ all -> 0x12c6 }
            r4.append(r12)     // Catch:{ all -> 0x12c6 }
            java.lang.String r5 = " size: "
            r4.append(r5)     // Catch:{ all -> 0x12c6 }
            r7 = r76
            r4.append(r7)     // Catch:{ all -> 0x12c2 }
            java.lang.String r5 = "x"
            r4.append(r5)     // Catch:{ all -> 0x12c2 }
            r8 = r75
            r4.append(r8)     // Catch:{ all -> 0x12c0 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x12c0 }
            org.telegram.messenger.FileLog.e((java.lang.String) r4)     // Catch:{ all -> 0x12c0 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)     // Catch:{ all -> 0x12c0 }
            r3 = r6
            r6 = 1
        L_0x12a8:
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ all -> 0x12c0 }
            r5 = r39
            r4.unselectTrack(r5)     // Catch:{ all -> 0x12c0 }
            if (r43 == 0) goto L_0x12b7
            r43.stop()     // Catch:{ all -> 0x12c0 }
            r43.release()     // Catch:{ all -> 0x12c0 }
        L_0x12b7:
            r43 = r3
            r3 = r45
            r45 = r6
            r6 = r41
            goto L_0x12f4
        L_0x12c0:
            r0 = move-exception
            goto L_0x12cb
        L_0x12c2:
            r0 = move-exception
            r8 = r75
            goto L_0x12cb
        L_0x12c6:
            r0 = move-exception
            r8 = r75
            r7 = r76
        L_0x12cb:
            r9 = r2
            r11 = r7
            r7 = r45
            goto L_0x12d8
        L_0x12d0:
            r0 = move-exception
            r8 = r75
            r7 = r76
            r9 = r2
            r11 = r7
        L_0x12d7:
            r7 = 0
        L_0x12d8:
            r2 = r0
            goto L_0x1364
        L_0x12db:
            r8 = r75
            r7 = r76
            r3 = r82
            r12 = r13
            r5 = -5
            r2 = r78
            r34 = r84
            r59 = r3
            r1 = -5
            r3 = 0
            r6 = 0
            r43 = 0
            r45 = 0
            r57 = 0
            r58 = 0
        L_0x12f4:
            if (r6 == 0) goto L_0x1301
            r6.release()     // Catch:{ all -> 0x12fa }
            goto L_0x1301
        L_0x12fa:
            r0 = move-exception
            r9 = r2
            r11 = r7
            r2 = r0
            r7 = r3
            goto L_0x1364
        L_0x1301:
            if (r58 == 0) goto L_0x1306
            r58.release()     // Catch:{ all -> 0x12fa }
        L_0x1306:
            if (r43 == 0) goto L_0x130e
            r43.stop()     // Catch:{ all -> 0x12fa }
            r43.release()     // Catch:{ all -> 0x12fa }
        L_0x130e:
            if (r57 == 0) goto L_0x1313
            r57.release()     // Catch:{ all -> 0x12fa }
        L_0x1313:
            r68.checkConversionCanceled()     // Catch:{ all -> 0x12fa }
            r9 = r2
            r14 = r3
            r6 = r45
        L_0x131a:
            android.media.MediaExtractor r2 = r15.extractor
            if (r2 == 0) goto L_0x1321
            r2.release()
        L_0x1321:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer
            if (r2 == 0) goto L_0x1336
            r2.finishMovie()     // Catch:{ all -> 0x1331 }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ all -> 0x1331 }
            long r1 = r2.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x1331 }
            r15.endPresentationTime = r1     // Catch:{ all -> 0x1331 }
            goto L_0x1336
        L_0x1331:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1336:
            r11 = r9
            r9 = r7
            goto L_0x13b6
        L_0x133a:
            r0 = move-exception
            r8 = r75
            r7 = r76
            r3 = r82
            r12 = r13
            r5 = -5
            r9 = r78
            r34 = r84
            r2 = r0
            r59 = r3
            r11 = r7
            goto L_0x1362
        L_0x134c:
            r0 = move-exception
            r3 = r82
            r7 = r11
            r8 = r12
            r12 = r13
            r5 = -5
            goto L_0x135b
        L_0x1354:
            r0 = move-exception
            r3 = r82
            r7 = r11
            r8 = r12
            r5 = -5
            r12 = r10
        L_0x135b:
            r9 = r78
            r34 = r84
            r2 = r0
            r59 = r3
        L_0x1362:
            r1 = -5
        L_0x1363:
            r7 = 0
        L_0x1364:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x1426 }
            r3.<init>()     // Catch:{ all -> 0x1426 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x1426 }
            r3.append(r9)     // Catch:{ all -> 0x1426 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x1426 }
            r3.append(r12)     // Catch:{ all -> 0x1426 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x1426 }
            r3.append(r11)     // Catch:{ all -> 0x1426 }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x1426 }
            r3.append(r8)     // Catch:{ all -> 0x1426 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x1426 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x1426 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x1426 }
            android.media.MediaExtractor r2 = r15.extractor
            if (r2 == 0) goto L_0x139a
            r2.release()
        L_0x139a:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer
            if (r2 == 0) goto L_0x13af
            r2.finishMovie()     // Catch:{ all -> 0x13aa }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ all -> 0x13aa }
            long r1 = r2.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x13aa }
            r15.endPresentationTime = r1     // Catch:{ all -> 0x13aa }
            goto L_0x13af
        L_0x13aa:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x13af:
            r14 = r7
            r6 = 1
            r67 = r11
            r11 = r9
            r9 = r67
        L_0x13b6:
            if (r14 == 0) goto L_0x13e5
            r22 = 1
            r1 = r68
            r2 = r69
            r3 = r70
            r4 = r71
            r5 = r72
            r6 = r73
            r7 = r74
            r10 = r77
            r12 = r79
            r13 = r80
            r15 = r59
            r17 = r34
            r19 = r86
            r21 = r88
            r23 = r90
            r24 = r91
            r25 = r92
            r26 = r93
            r27 = r94
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r15, r17, r19, r21, r22, r23, r24, r25, r26, r27)
            return r1
        L_0x13e5:
            long r1 = java.lang.System.currentTimeMillis()
            long r1 = r1 - r16
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x1425
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "compression completed time="
            r3.append(r4)
            r3.append(r1)
            java.lang.String r1 = " needCompress="
            r3.append(r1)
            r1 = r88
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
        L_0x1425:
            return r6
        L_0x1426:
            r0 = move-exception
            r2 = r68
            r3 = r0
            android.media.MediaExtractor r4 = r2.extractor
            if (r4 == 0) goto L_0x1431
            r4.release()
        L_0x1431:
            org.telegram.messenger.video.MP4Builder r4 = r2.mediaMuxer
            if (r4 == 0) goto L_0x1446
            r4.finishMovie()     // Catch:{ all -> 0x1441 }
            org.telegram.messenger.video.MP4Builder r4 = r2.mediaMuxer     // Catch:{ all -> 0x1441 }
            long r4 = r4.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x1441 }
            r2.endPresentationTime = r4     // Catch:{ all -> 0x1441 }
            goto L_0x1446
        L_0x1441:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1446:
            goto L_0x1448
        L_0x1447:
            throw r3
        L_0x1448:
            goto L_0x1447
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
