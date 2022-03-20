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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r88v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r88v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r88v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r42v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v27, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r88v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r88v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r88v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r88v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r88v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r88v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v63, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v80, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v92, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v80, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v206, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v207, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v212, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v91, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v135, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v102, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v103, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v273, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v275, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v277, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v92, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v279, resolved type: java.nio.ByteBuffer[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v147, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v148, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r21v149, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v95, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v116, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v117, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v118, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v119, resolved type: int} */
    /* JADX WARNING: type inference failed for: r1v228 */
    /* JADX WARNING: type inference failed for: r1v229 */
    /* JADX WARNING: Code restructure failed: missing block: B:1007:0x11c8, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1008:0x11ca, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1009:0x11cb, code lost:
        r29 = r88;
        r2 = r0;
        r10 = r11;
        r52 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1023:0x122d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1024:0x122e, code lost:
        r14 = r81;
        r47 = r8;
        r12 = r52;
        r29 = r88;
        r2 = r0;
        r13 = r5;
        r10 = r45;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1036:0x1293, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1037:0x1294, code lost:
        r14 = r81;
        r47 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1038:0x1299, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1039:0x129a, code lost:
        r14 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1054:0x12de, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1055:0x12df, code lost:
        r14 = r81;
        r47 = r82;
        r53 = r8;
        r1 = r10;
        r12 = r52;
        r21 = r86;
        r29 = r4;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1056:0x12f3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1057:0x12f4, code lost:
        r14 = r81;
        r47 = r82;
        r53 = r8;
        r1 = r10;
        r12 = r52;
        r21 = r86;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1060:0x130b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1061:0x130c, code lost:
        r14 = r81;
        r47 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1064:0x1323, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1065:0x1324, code lost:
        r14 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1066:0x1328, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1067:0x1329, code lost:
        r14 = r81;
        r47 = r82;
        r12 = r52;
        r21 = r86;
        r2 = r0;
        r10 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1068:0x1338, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1069:0x1339, code lost:
        r42 = r88;
        r47 = r5;
        r14 = r13;
        r54 = r30;
        r21 = r86;
        r2 = r0;
        r10 = r1;
        r52 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1070:0x134a, code lost:
        r1 = r23;
        r27 = r42;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1071:0x134f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1072:0x1350, code lost:
        r47 = r5;
        r14 = r13;
        r54 = r30;
        r21 = r86;
        r2 = r0;
        r52 = r3;
        r1 = r23;
        r27 = r88;
        r10 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1073:0x1364, code lost:
        r13 = -5;
        r29 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1076:0x1380, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1077:0x1381, code lost:
        r42 = r88;
        r14 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1078:0x1384, code lost:
        r15 = r72;
        r5 = r79;
        r10 = r80;
        r21 = r86;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1080:0x1392, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1081:0x1393, code lost:
        r42 = r88;
        r14 = r13;
        r54 = r30;
        r21 = r86;
        r2 = r0;
        r1 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1099:0x13ea, code lost:
        r48 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1164:0x151b, code lost:
        r2.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1168:?, code lost:
        r2.finishMovie();
        r15.endPresentationTime = r15.mediaMuxer.getLastFrameTimestamp(r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1169:0x152e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1170:0x152f, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x052f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x0530, code lost:
        r21 = r19;
        r10 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x0557, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x0558, code lost:
        r21 = r19;
        r10 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0583, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x0585, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0586, code lost:
        r35 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x058d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x058e, code lost:
        r21 = r19;
        r10 = r22;
        r15 = r72;
        r14 = r81;
        r27 = r88;
        r2 = r0;
        r13 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x059b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x059c, code lost:
        r12 = r72;
        r11 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x05e1, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x05e2, code lost:
        r12 = r72;
        r11 = r79;
        r10 = r22;
        r1 = r35;
        r14 = r19;
        r13 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x05f0, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x05f1, code lost:
        r21 = r19;
        r10 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x0604, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x0605, code lost:
        r12 = r72;
        r38 = r7;
        r11 = r9;
        r21 = r14;
        r10 = r15;
        r35 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x0621, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x0622, code lost:
        r38 = r7;
        r21 = r14;
        r10 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:311:0x0628, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x0629, code lost:
        r12 = r72;
        r38 = r7;
        r21 = r14;
        r10 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x0674, code lost:
        r36 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x0882, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x0883, code lost:
        r21 = r86;
        r27 = r88;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x08ad, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x08ae, code lost:
        r15 = r72;
        r5 = r79;
        r10 = r80;
        r21 = r86;
        r27 = r88;
        r2 = r0;
        r14 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0a11, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x0a12, code lost:
        r14 = r81;
        r21 = r86;
        r5 = r2;
        r10 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0a1a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x0a4b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x0aaa, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x0acf, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:588:0x0ad0, code lost:
        r2 = r86;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x0ae2, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0b6d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x0b6e, code lost:
        r14 = r81;
        r47 = r82;
        r2 = r0;
        r29 = r4;
        r53 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0be2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0be3, code lost:
        r14 = r81;
        r47 = r82;
        r29 = r88;
        r2 = r0;
        r54 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0c9a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x0c9b, code lost:
        r54 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0ccf, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0cd1, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0cdc, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0cdd, code lost:
        r88 = r4;
        r53 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0ce1, code lost:
        r14 = r81;
        r47 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0d82, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0d83, code lost:
        r10 = r80;
        r14 = r81;
        r2 = r0;
        r15 = r13;
        r1 = r23;
        r6 = false;
        r13 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0d8f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x0var_, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x0var_, code lost:
        r14 = r81;
        r29 = r88;
        r2 = r0;
        r13 = r5;
        r47 = r8;
        r1 = r23;
        r10 = r45;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x021e, code lost:
        r6 = r7;
        r13 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x0ff1, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x0ff2, code lost:
        r5 = r79;
        r10 = r80;
        r14 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x0ffc, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x0ffd, code lost:
        r14 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x0fff, code lost:
        r29 = r88;
        r2 = r0;
        r47 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x10ab, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x10ad, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:0x10e6, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:0x10e8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1002:0x11a3 A[Catch:{ Exception -> 0x11ca, all -> 0x11c8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1007:0x11c8 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:981:0x1162] */
    /* JADX WARNING: Removed duplicated region for block: B:1038:0x1299 A[ExcHandler: all (th java.lang.Throwable), PHI: r5 
      PHI: (r5v47 int) = (r5v44 int), (r5v44 int), (r5v44 int), (r5v44 int), (r5v44 int), (r5v44 int), (r5v44 int), (r5v60 int), (r5v44 int) binds: [B:704:0x0d1f, B:705:?, B:709:0x0d37, B:757:0x0e08, B:758:?, B:766:0x0e19, B:767:?, B:844:0x0f5f, B:763:0x0e13] A[DONT_GENERATE, DONT_INLINE], Splitter:B:709:0x0d37] */
    /* JADX WARNING: Removed duplicated region for block: B:1064:0x1323 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:499:0x098b] */
    /* JADX WARNING: Removed duplicated region for block: B:1076:0x1380 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:488:0x0922] */
    /* JADX WARNING: Removed duplicated region for block: B:1098:0x13e8 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:1113:0x1437 A[Catch:{ all -> 0x1443 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1127:0x146c  */
    /* JADX WARNING: Removed duplicated region for block: B:1129:0x1487 A[SYNTHETIC, Splitter:B:1129:0x1487] */
    /* JADX WARNING: Removed duplicated region for block: B:1135:0x1496 A[Catch:{ all -> 0x148b }] */
    /* JADX WARNING: Removed duplicated region for block: B:1137:0x149b A[Catch:{ all -> 0x148b }] */
    /* JADX WARNING: Removed duplicated region for block: B:1139:0x14a3 A[Catch:{ all -> 0x148b }] */
    /* JADX WARNING: Removed duplicated region for block: B:1144:0x14b3  */
    /* JADX WARNING: Removed duplicated region for block: B:1147:0x14ba A[SYNTHETIC, Splitter:B:1147:0x14ba] */
    /* JADX WARNING: Removed duplicated region for block: B:1164:0x151b  */
    /* JADX WARNING: Removed duplicated region for block: B:1167:0x1522 A[SYNTHETIC, Splitter:B:1167:0x1522] */
    /* JADX WARNING: Removed duplicated region for block: B:1173:0x153d  */
    /* JADX WARNING: Removed duplicated region for block: B:1175:0x156a  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0557 A[Catch:{ Exception -> 0x0585, all -> 0x0583 }, ExcHandler: all (th java.lang.Throwable), Splitter:B:141:0x032a] */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0583 A[ExcHandler: all (th java.lang.Throwable), PHI: r8 r10 r12 r21 
      PHI: (r8v40 int) = (r8v42 int), (r8v39 int), (r8v39 int) binds: [B:249:0x04df, B:141:0x032a, B:268:0x053b] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r10v133 int) = (r10v136 int), (r10v138 int), (r10v143 int) binds: [B:249:0x04df, B:141:0x032a, B:268:0x053b] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r12v91 org.telegram.messenger.video.MediaCodecVideoConvertor) = (r12v94 org.telegram.messenger.video.MediaCodecVideoConvertor), (r12v98 org.telegram.messenger.video.MediaCodecVideoConvertor), (r12v98 org.telegram.messenger.video.MediaCodecVideoConvertor) binds: [B:249:0x04df, B:141:0x032a, B:268:0x053b] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r21v115 int) = (r21v124 int), (r21v111 int), (r21v128 int) binds: [B:249:0x04df, B:141:0x032a, B:268:0x053b] A[DONT_GENERATE, DONT_INLINE], Splitter:B:249:0x04df] */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x058d A[ExcHandler: all (r0v148 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:86:0x0221] */
    /* JADX WARNING: Removed duplicated region for block: B:299:0x05f0 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:63:0x01d8] */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x0621 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:46:0x012d] */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x0672 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x06b3 A[SYNTHETIC, Splitter:B:344:0x06b3] */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x06c7 A[Catch:{ all -> 0x06b7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x06cc A[Catch:{ all -> 0x06b7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x07cf  */
    /* JADX WARNING: Removed duplicated region for block: B:452:0x0882 A[ExcHandler: Exception (r0v109 'e' java.lang.Exception A[CUSTOM_DECLARE]), PHI: r88 
      PHI: (r88v16 long) = (r88v3 long), (r88v3 long), (r88v3 long), (r88v3 long), (r88v18 long), (r88v18 long) binds: [B:479:0x0906, B:473:0x08c6, B:474:?, B:463:0x08a2, B:447:0x086f, B:448:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:447:0x086f] */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0897  */
    /* JADX WARNING: Removed duplicated region for block: B:469:0x08bc  */
    /* JADX WARNING: Removed duplicated region for block: B:473:0x08c6 A[SYNTHETIC, Splitter:B:473:0x08c6] */
    /* JADX WARNING: Removed duplicated region for block: B:479:0x0906 A[SYNTHETIC, Splitter:B:479:0x0906] */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x091e  */
    /* JADX WARNING: Removed duplicated region for block: B:512:0x09b9 A[SYNTHETIC, Splitter:B:512:0x09b9] */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x09e6  */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x09ec  */
    /* JADX WARNING: Removed duplicated region for block: B:531:0x0a11 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:527:0x0a02] */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0a1a A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:527:0x0a02] */
    /* JADX WARNING: Removed duplicated region for block: B:539:0x0a33  */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0a36  */
    /* JADX WARNING: Removed duplicated region for block: B:590:0x0ae2 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:567:0x0a92] */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x0ae6  */
    /* JADX WARNING: Removed duplicated region for block: B:602:0x0b18  */
    /* JADX WARNING: Removed duplicated region for block: B:605:0x0b25  */
    /* JADX WARNING: Removed duplicated region for block: B:606:0x0b27  */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0b4a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:623:0x0b7b  */
    /* JADX WARNING: Removed duplicated region for block: B:686:0x0cb2 A[Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x0cd1 A[ExcHandler: all (th java.lang.Throwable), PHI: r13 r21 r27 r35 
      PHI: (r13v36 org.telegram.messenger.video.MediaCodecVideoConvertor) = (r13v37 org.telegram.messenger.video.MediaCodecVideoConvertor), (r13v32 org.telegram.messenger.video.MediaCodecVideoConvertor), (r13v32 org.telegram.messenger.video.MediaCodecVideoConvertor), (r13v32 org.telegram.messenger.video.MediaCodecVideoConvertor), (r13v32 org.telegram.messenger.video.MediaCodecVideoConvertor), (r13v32 org.telegram.messenger.video.MediaCodecVideoConvertor) binds: [B:741:0x0dc4, B:624:0x0b7d, B:649:0x0bfb, B:656:0x0CLASSNAME, B:629:0x0b8f, B:618:0x0b66] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r21v39 long) = (r21v40 long), (r21v35 long), (r21v35 long), (r21v35 long), (r21v35 long), (r21v35 long) binds: [B:741:0x0dc4, B:624:0x0b7d, B:649:0x0bfb, B:656:0x0CLASSNAME, B:629:0x0b8f, B:618:0x0b66] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r27v31 long) = (r27v32 long), (r27v27 long), (r27v27 long), (r27v27 long), (r27v27 long), (r27v27 long) binds: [B:741:0x0dc4, B:624:0x0b7d, B:649:0x0bfb, B:656:0x0CLASSNAME, B:629:0x0b8f, B:618:0x0b66] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r35v7 int) = (r35v36 int), (r35v3 int), (r35v3 int), (r35v3 int), (r35v3 int), (r35v3 int) binds: [B:741:0x0dc4, B:624:0x0b7d, B:649:0x0bfb, B:656:0x0CLASSNAME, B:629:0x0b8f, B:618:0x0b66] A[DONT_GENERATE, DONT_INLINE], Splitter:B:618:0x0b66] */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0ce7  */
    /* JADX WARNING: Removed duplicated region for block: B:702:0x0d05 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:707:0x0d24  */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0d2f  */
    /* JADX WARNING: Removed duplicated region for block: B:713:0x0d3e  */
    /* JADX WARNING: Removed duplicated region for block: B:714:0x0d58  */
    /* JADX WARNING: Removed duplicated region for block: B:723:0x0d82 A[Catch:{ Exception -> 0x0d8f, all -> 0x0d82 }, ExcHandler: all (r0v77 'th' java.lang.Throwable A[CUSTOM_DECLARE, Catch:{ Exception -> 0x0d8f, all -> 0x0d82 }]), Splitter:B:716:0x0d5b] */
    /* JADX WARNING: Removed duplicated region for block: B:847:0x0var_ A[Catch:{ Exception -> 0x122d, all -> 0x1299 }] */
    /* JADX WARNING: Removed duplicated region for block: B:848:0x0var_ A[Catch:{ Exception -> 0x122d, all -> 0x1299 }] */
    /* JADX WARNING: Removed duplicated region for block: B:852:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:853:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:872:0x0ffc A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:865:0x0fd4] */
    /* JADX WARNING: Removed duplicated region for block: B:923:0x10ad A[ExcHandler: Exception (e java.lang.Exception), Splitter:B:898:0x1046] */
    /* JADX WARNING: Removed duplicated region for block: B:933:0x10d1  */
    /* JADX WARNING: Removed duplicated region for block: B:941:0x10e6 A[ExcHandler: all (th java.lang.Throwable), PHI: r14 r21 r27 
      PHI: (r14v72 int) = (r14v73 int), (r14v79 int), (r14v79 int), (r14v79 int), (r14v79 int) binds: [B:934:0x10d3, B:910:0x1063, B:911:?, B:904:0x1054, B:905:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r21v49 long) = (r21v50 long), (r21v40 long), (r21v40 long), (r21v40 long), (r21v40 long) binds: [B:934:0x10d3, B:910:0x1063, B:911:?, B:904:0x1054, B:905:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r27v41 long) = (r27v42 long), (r27v32 long), (r27v32 long), (r27v32 long), (r27v32 long) binds: [B:934:0x10d3, B:910:0x1063, B:911:?, B:904:0x1054, B:905:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:904:0x1054] */
    /* JADX WARNING: Removed duplicated region for block: B:942:0x10e8 A[ExcHandler: Exception (e java.lang.Exception), PHI: r47 
      PHI: (r47v39 android.media.MediaCodec) = (r47v36 android.media.MediaCodec), (r47v36 android.media.MediaCodec), (r47v40 android.media.MediaCodec) binds: [B:978:0x115b, B:955:0x1106, B:934:0x10d3] A[DONT_GENERATE, DONT_INLINE], Splitter:B:934:0x10d3] */
    /* JADX WARNING: Removed duplicated region for block: B:943:0x10eb  */
    /* JADX WARNING: Removed duplicated region for block: B:947:0x10f5  */
    /* JADX WARNING: Removed duplicated region for block: B:948:0x10f8  */
    /* JADX WARNING: Removed duplicated region for block: B:951:0x10fe  */
    /* JADX WARNING: Removed duplicated region for block: B:966:0x113b A[Catch:{ Exception -> 0x10e8, all -> 0x11c8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:967:0x113e A[Catch:{ Exception -> 0x10e8, all -> 0x11c8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:975:0x1153 A[Catch:{ Exception -> 0x11a9, all -> 0x11c8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:995:0x1186 A[Catch:{ Exception -> 0x11ca, all -> 0x11c8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:998:0x1192 A[Catch:{ Exception -> 0x11ca, all -> 0x11c8 }] */
    @android.annotation.TargetApi(18)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideoInternal(java.lang.String r73, java.io.File r74, int r75, boolean r76, int r77, int r78, int r79, int r80, int r81, int r82, int r83, long r84, long r86, long r88, long r90, boolean r92, boolean r93, org.telegram.messenger.MediaController.SavedFilterState r94, java.lang.String r95, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r96, boolean r97, org.telegram.messenger.MediaController.CropState r98) {
        /*
            r72 = this;
            r15 = r72
            r14 = r73
            r13 = r75
            r12 = r77
            r11 = r78
            r9 = r79
            r10 = r80
            r8 = r81
            r7 = r82
            r6 = r83
            r4 = r84
            r2 = r90
            r1 = r92
            long r16 = java.lang.System.currentTimeMillis()
            r18 = -1
            android.media.MediaCodec$BufferInfo r13 = new android.media.MediaCodec$BufferInfo     // Catch:{ all -> 0x14d9 }
            r13.<init>()     // Catch:{ all -> 0x14d9 }
            org.telegram.messenger.video.Mp4Movie r6 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ all -> 0x14d9 }
            r6.<init>()     // Catch:{ all -> 0x14d9 }
            r23 = r13
            r13 = r74
            r6.setCacheFile(r13)     // Catch:{ all -> 0x14d9 }
            r11 = 0
            r6.setRotation(r11)     // Catch:{ all -> 0x14d9 }
            r6.setSize(r9, r10)     // Catch:{ all -> 0x14d9 }
            org.telegram.messenger.video.MP4Builder r11 = new org.telegram.messenger.video.MP4Builder     // Catch:{ all -> 0x14d9 }
            r11.<init>()     // Catch:{ all -> 0x14d9 }
            r13 = r76
            org.telegram.messenger.video.MP4Builder r6 = r11.createMovie(r6, r13)     // Catch:{ all -> 0x14d9 }
            r15.mediaMuxer = r6     // Catch:{ all -> 0x14d9 }
            float r6 = (float) r2     // Catch:{ all -> 0x14d9 }
            r24 = 1148846080(0x447a0000, float:1000.0)
            float r25 = r6 / r24
            r26 = 1000(0x3e8, double:4.94E-321)
            long r4 = r2 * r26
            r15.endPresentationTime = r4     // Catch:{ all -> 0x14d9 }
            r72.checkConversionCanceled()     // Catch:{ all -> 0x14d9 }
            java.lang.String r11 = "csd-1"
            java.lang.String r5 = "csd-0"
            java.lang.String r4 = "prepend-sps-pps-to-idr-frames"
            r6 = 921600(0xe1000, float:1.291437E-39)
            java.lang.String r13 = "video/avc"
            r14 = 0
            if (r97 == 0) goto L_0x0711
            int r18 = (r88 > r14 ? 1 : (r88 == r14 ? 0 : -1))
            if (r18 < 0) goto L_0x0088
            r6 = 1157234688(0x44fa0000, float:2000.0)
            int r6 = (r25 > r6 ? 1 : (r25 == r6 ? 0 : -1))
            if (r6 > 0) goto L_0x0073
            r6 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            r7 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x008d
        L_0x0073:
            r6 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r6 = (r25 > r6 ? 1 : (r25 == r6 ? 0 : -1))
            if (r6 > 0) goto L_0x0081
            r6 = 2200000(0x2191c0, float:3.082857E-39)
            r7 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x008d
        L_0x0081:
            r6 = 1560000(0x17cdc0, float:2.186026E-39)
            r7 = 1560000(0x17cdc0, float:2.186026E-39)
            goto L_0x008d
        L_0x0088:
            if (r7 > 0) goto L_0x008d
            r7 = 921600(0xe1000, float:1.291437E-39)
        L_0x008d:
            int r6 = r9 % 16
            r18 = 1098907648(0x41800000, float:16.0)
            if (r6 == 0) goto L_0x00dc
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00d4, all -> 0x00c7 }
            if (r6 == 0) goto L_0x00bc
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d4, all -> 0x00c7 }
            r6.<init>()     // Catch:{ Exception -> 0x00d4, all -> 0x00c7 }
            java.lang.String r14 = "changing width from "
            r6.append(r14)     // Catch:{ Exception -> 0x00d4, all -> 0x00c7 }
            r6.append(r9)     // Catch:{ Exception -> 0x00d4, all -> 0x00c7 }
            java.lang.String r14 = " to "
            r6.append(r14)     // Catch:{ Exception -> 0x00d4, all -> 0x00c7 }
            float r14 = (float) r9     // Catch:{ Exception -> 0x00d4, all -> 0x00c7 }
            float r14 = r14 / r18
            int r14 = java.lang.Math.round(r14)     // Catch:{ Exception -> 0x00d4, all -> 0x00c7 }
            int r14 = r14 * 16
            r6.append(r14)     // Catch:{ Exception -> 0x00d4, all -> 0x00c7 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x00d4, all -> 0x00c7 }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00c7 }
        L_0x00bc:
            float r6 = (float) r9     // Catch:{ Exception -> 0x00d4, all -> 0x00c7 }
            float r6 = r6 / r18
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00d4, all -> 0x00c7 }
            int r6 = r6 * 16
            r14 = r6
            goto L_0x00dd
        L_0x00c7:
            r0 = move-exception
            r15 = r72
            r21 = r86
            r27 = r88
            r2 = r0
            r1 = r7
            r14 = r8
            r5 = r9
            goto L_0x14e6
        L_0x00d4:
            r0 = move-exception
            r12 = r72
            r1 = r0
            r38 = r7
            goto L_0x0667
        L_0x00dc:
            r14 = r9
        L_0x00dd:
            int r6 = r10 % 16
            if (r6 == 0) goto L_0x012c
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0124, all -> 0x0115 }
            if (r6 == 0) goto L_0x010a
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0124, all -> 0x0115 }
            r6.<init>()     // Catch:{ Exception -> 0x0124, all -> 0x0115 }
            java.lang.String r9 = "changing height from "
            r6.append(r9)     // Catch:{ Exception -> 0x0124, all -> 0x0115 }
            r6.append(r10)     // Catch:{ Exception -> 0x0124, all -> 0x0115 }
            java.lang.String r9 = " to "
            r6.append(r9)     // Catch:{ Exception -> 0x0124, all -> 0x0115 }
            float r9 = (float) r10     // Catch:{ Exception -> 0x0124, all -> 0x0115 }
            float r9 = r9 / r18
            int r9 = java.lang.Math.round(r9)     // Catch:{ Exception -> 0x0124, all -> 0x0115 }
            int r9 = r9 * 16
            r6.append(r9)     // Catch:{ Exception -> 0x0124, all -> 0x0115 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0124, all -> 0x0115 }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x0124, all -> 0x0115 }
        L_0x010a:
            float r6 = (float) r10     // Catch:{ Exception -> 0x0124, all -> 0x0115 }
            float r6 = r6 / r18
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x0124, all -> 0x0115 }
            int r6 = r6 * 16
            r15 = r6
            goto L_0x012d
        L_0x0115:
            r0 = move-exception
            r15 = r72
            r21 = r86
            r27 = r88
            r2 = r0
            r1 = r7
            r5 = r14
            r6 = 0
            r13 = -5
        L_0x0121:
            r14 = r8
            goto L_0x14e8
        L_0x0124:
            r0 = move-exception
            r12 = r72
            r1 = r0
            r38 = r7
            goto L_0x0668
        L_0x012c:
            r15 = r10
        L_0x012d:
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0628, all -> 0x0621 }
            if (r6 == 0) goto L_0x016d
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0164, all -> 0x0156 }
            r6.<init>()     // Catch:{ Exception -> 0x0164, all -> 0x0156 }
            java.lang.String r9 = "create photo encoder "
            r6.append(r9)     // Catch:{ Exception -> 0x0164, all -> 0x0156 }
            r6.append(r14)     // Catch:{ Exception -> 0x0164, all -> 0x0156 }
            java.lang.String r9 = " "
            r6.append(r9)     // Catch:{ Exception -> 0x0164, all -> 0x0156 }
            r6.append(r15)     // Catch:{ Exception -> 0x0164, all -> 0x0156 }
            java.lang.String r9 = " duration = "
            r6.append(r9)     // Catch:{ Exception -> 0x0164, all -> 0x0156 }
            r6.append(r2)     // Catch:{ Exception -> 0x0164, all -> 0x0156 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0164, all -> 0x0156 }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x0164, all -> 0x0156 }
            goto L_0x016d
        L_0x0156:
            r0 = move-exception
            r21 = r86
            r27 = r88
            r2 = r0
            r1 = r7
            r5 = r14
            r10 = r15
            r6 = 0
            r13 = -5
            r15 = r72
            goto L_0x0121
        L_0x0164:
            r0 = move-exception
            r12 = r72
            r1 = r0
            r38 = r7
            r10 = r15
            goto L_0x0668
        L_0x016d:
            android.media.MediaFormat r6 = android.media.MediaFormat.createVideoFormat(r13, r14, r15)     // Catch:{ Exception -> 0x0628, all -> 0x0621 }
            java.lang.String r9 = "color-format"
            r10 = 2130708361(0x7var_, float:1.701803E38)
            r6.setInteger(r9, r10)     // Catch:{ Exception -> 0x0628, all -> 0x0621 }
            java.lang.String r9 = "bitrate"
            r6.setInteger(r9, r7)     // Catch:{ Exception -> 0x0628, all -> 0x0621 }
            java.lang.String r9 = "frame-rate"
            r10 = 30
            r6.setInteger(r9, r10)     // Catch:{ Exception -> 0x0628, all -> 0x0621 }
            java.lang.String r9 = "i-frame-interval"
            r10 = 1
            r6.setInteger(r9, r10)     // Catch:{ Exception -> 0x0628, all -> 0x0621 }
            android.media.MediaCodec r9 = android.media.MediaCodec.createEncoderByType(r13)     // Catch:{ Exception -> 0x0628, all -> 0x0621 }
            r1 = 0
            r9.configure(r6, r1, r1, r10)     // Catch:{ Exception -> 0x0616, all -> 0x0621 }
            org.telegram.messenger.video.InputSurface r6 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x0616, all -> 0x0621 }
            android.view.Surface r1 = r9.createInputSurface()     // Catch:{ Exception -> 0x0616, all -> 0x0621 }
            r6.<init>(r1)     // Catch:{ Exception -> 0x0616, all -> 0x0621 }
            r6.makeCurrent()     // Catch:{ Exception -> 0x0604, all -> 0x0621 }
            r9.start()     // Catch:{ Exception -> 0x0604, all -> 0x0621 }
            org.telegram.messenger.video.OutputSurface r18 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x0604, all -> 0x0621 }
            r19 = 0
            float r1 = (float) r8
            r21 = 1
            r32 = r1
            r1 = r18
            r2 = r94
            r3 = r73
            r33 = r4
            r4 = r95
            r34 = r5
            r5 = r96
            r35 = r6
            r6 = r19
            r38 = r7
            r7 = r14
            r8 = r15
            r79 = r9
            r9 = r77
            r19 = 1
            r10 = r78
            r39 = r11
            r11 = r75
            r12 = r32
            r19 = r14
            r22 = r15
            r14 = r23
            r15 = r13
            r13 = r21
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x05f6, all -> 0x05f0 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x05e1, all -> 0x05f0 }
            r2 = 21
            if (r1 >= r2) goto L_0x0201
            java.nio.ByteBuffer[] r1 = r79.getOutputBuffers()     // Catch:{ Exception -> 0x01f4, all -> 0x01e6 }
            goto L_0x0202
        L_0x01e6:
            r0 = move-exception
            r15 = r72
            r14 = r81
            r27 = r88
            r2 = r0
            r5 = r19
            r10 = r22
            goto L_0x063f
        L_0x01f4:
            r0 = move-exception
            r12 = r72
            r11 = r79
            r1 = r0
            r14 = r19
            r10 = r22
            r13 = -5
            goto L_0x066e
        L_0x0201:
            r1 = 0
        L_0x0202:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x05e1, all -> 0x05f0 }
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 1
            r6 = 0
            r13 = -5
        L_0x020b:
            if (r6 != 0) goto L_0x05cb
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x05bc, all -> 0x05a8 }
            r7 = r2 ^ 1
            r8 = r13
            r13 = 1
            r70 = r7
            r7 = r6
            r6 = r70
        L_0x0219:
            if (r6 != 0) goto L_0x0221
            if (r13 == 0) goto L_0x021e
            goto L_0x0221
        L_0x021e:
            r6 = r7
            r13 = r8
            goto L_0x020b
        L_0x0221:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x059b, all -> 0x058d }
            if (r93 == 0) goto L_0x022b
            r9 = 22000(0x55f0, double:1.08694E-319)
            r11 = r79
            goto L_0x022f
        L_0x022b:
            r11 = r79
            r9 = 2500(0x9c4, double:1.235E-320)
        L_0x022f:
            int r9 = r11.dequeueOutputBuffer(r14, r9)     // Catch:{ Exception -> 0x0589, all -> 0x058d }
            r10 = -1
            if (r9 != r10) goto L_0x024d
            r12 = r72
            r80 = r1
            r21 = r4
            r79 = r6
            r6 = r7
            r10 = r22
            r7 = r34
            r13 = r39
            r1 = -1
            r4 = r2
            r2 = r19
            r19 = 0
            goto L_0x04ae
        L_0x024d:
            r10 = -3
            if (r9 != r10) goto L_0x028b
            int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0280, all -> 0x0271 }
            r12 = 21
            if (r10 >= r12) goto L_0x025a
            java.nio.ByteBuffer[] r1 = r11.getOutputBuffers()     // Catch:{ Exception -> 0x0280, all -> 0x0271 }
        L_0x025a:
            r12 = r72
            r80 = r1
            r21 = r4
            r79 = r6
            r6 = r7
            r10 = r22
            r7 = r34
            r1 = -1
            r4 = r2
            r2 = r19
            r19 = r13
            r13 = r39
            goto L_0x04ae
        L_0x0271:
            r0 = move-exception
            r15 = r72
            r14 = r81
            r27 = r88
            r2 = r0
            r13 = r8
        L_0x027a:
            r5 = r19
            r10 = r22
            goto L_0x05b7
        L_0x0280:
            r0 = move-exception
            r12 = r72
        L_0x0283:
            r1 = r0
            r13 = r8
        L_0x0285:
            r14 = r19
            r10 = r22
            goto L_0x066e
        L_0x028b:
            r10 = -2
            if (r9 != r10) goto L_0x031c
            android.media.MediaFormat r10 = r11.getOutputFormat()     // Catch:{ Exception -> 0x0280, all -> 0x0271 }
            boolean r12 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0280, all -> 0x0271 }
            if (r12 == 0) goto L_0x02ad
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0280, all -> 0x0271 }
            r12.<init>()     // Catch:{ Exception -> 0x0280, all -> 0x0271 }
            r79 = r6
            java.lang.String r6 = "photo encoder new format "
            r12.append(r6)     // Catch:{ Exception -> 0x0280, all -> 0x0271 }
            r12.append(r10)     // Catch:{ Exception -> 0x0280, all -> 0x0271 }
            java.lang.String r6 = r12.toString()     // Catch:{ Exception -> 0x0280, all -> 0x0271 }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x0280, all -> 0x0271 }
            goto L_0x02af
        L_0x02ad:
            r79 = r6
        L_0x02af:
            r6 = -5
            if (r8 != r6) goto L_0x02fe
            if (r10 == 0) goto L_0x02fe
            r12 = r72
            r80 = r7
            org.telegram.messenger.video.MP4Builder r6 = r12.mediaMuxer     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            r7 = 0
            int r6 = r6.addTrack(r10, r7)     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            r8 = r33
            boolean r21 = r10.containsKey(r8)     // Catch:{ Exception -> 0x02fa, all -> 0x02f2 }
            if (r21 == 0) goto L_0x02e6
            int r7 = r10.getInteger(r8)     // Catch:{ Exception -> 0x02fa, all -> 0x02f2 }
            r82 = r13
            r13 = 1
            if (r7 != r13) goto L_0x02e8
            r7 = r34
            java.nio.ByteBuffer r3 = r10.getByteBuffer(r7)     // Catch:{ Exception -> 0x02fa, all -> 0x02f2 }
            r13 = r39
            java.nio.ByteBuffer r10 = r10.getByteBuffer(r13)     // Catch:{ Exception -> 0x02fa, all -> 0x02f2 }
            int r3 = r3.limit()     // Catch:{ Exception -> 0x02fa, all -> 0x02f2 }
            int r10 = r10.limit()     // Catch:{ Exception -> 0x02fa, all -> 0x02f2 }
            int r3 = r3 + r10
            goto L_0x02ec
        L_0x02e6:
            r82 = r13
        L_0x02e8:
            r7 = r34
            r13 = r39
        L_0x02ec:
            r70 = r8
            r8 = r6
            r6 = r70
            goto L_0x030a
        L_0x02f2:
            r0 = move-exception
            r14 = r81
            r27 = r88
            r2 = r0
            r13 = r6
            goto L_0x033c
        L_0x02fa:
            r0 = move-exception
            r1 = r0
            r13 = r6
            goto L_0x0285
        L_0x02fe:
            r12 = r72
            r80 = r7
            r82 = r13
            r6 = r33
            r7 = r34
            r13 = r39
        L_0x030a:
            r21 = r4
            r33 = r6
            r10 = r22
            r6 = r80
            r80 = r1
            r4 = r2
            r2 = r19
            r1 = -1
            r19 = r82
            goto L_0x04ae
        L_0x031c:
            r12 = r72
            r79 = r6
            r82 = r13
            r6 = r33
            r7 = r34
            r13 = r39
            if (r9 < 0) goto L_0x0566
            int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0564, all -> 0x0557 }
            r33 = r6
            r6 = 21
            if (r10 >= r6) goto L_0x0342
            r6 = r1[r9]     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            goto L_0x0346
        L_0x0335:
            r0 = move-exception
            r14 = r81
            r27 = r88
            r2 = r0
            r13 = r8
        L_0x033c:
            r15 = r12
            goto L_0x027a
        L_0x033f:
            r0 = move-exception
            goto L_0x0283
        L_0x0342:
            java.nio.ByteBuffer r6 = r11.getOutputBuffer(r9)     // Catch:{ Exception -> 0x0564, all -> 0x0557 }
        L_0x0346:
            if (r6 == 0) goto L_0x0535
            int r10 = r14.size     // Catch:{ Exception -> 0x052f, all -> 0x0557 }
            r80 = r1
            r1 = 1
            if (r10 <= r1) goto L_0x048e
            int r1 = r14.flags     // Catch:{ Exception -> 0x0484, all -> 0x0470 }
            r21 = r1 & 2
            if (r21 != 0) goto L_0x03e8
            if (r3 == 0) goto L_0x0366
            r21 = r1 & 1
            if (r21 == 0) goto L_0x0366
            r21 = r4
            int r4 = r14.offset     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            int r4 = r4 + r3
            r14.offset = r4     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            int r10 = r10 - r3
            r14.size = r10     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            goto L_0x0368
        L_0x0366:
            r21 = r4
        L_0x0368:
            if (r5 == 0) goto L_0x03b6
            r1 = r1 & 1
            if (r1 == 0) goto L_0x03b6
            int r1 = r14.size     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            r4 = 100
            if (r1 <= r4) goto L_0x03b5
            int r1 = r14.offset     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            r6.position(r1)     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            byte[] r1 = new byte[r4]     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            r6.get(r1)     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            r5 = 0
            r10 = 0
        L_0x0380:
            r4 = 96
            if (r5 >= r4) goto L_0x03b5
            byte r4 = r1[r5]     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            if (r4 != 0) goto L_0x03ac
            int r4 = r5 + 1
            byte r4 = r1[r4]     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            if (r4 != 0) goto L_0x03ac
            int r4 = r5 + 2
            byte r4 = r1[r4]     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            if (r4 != 0) goto L_0x03ac
            int r4 = r5 + 3
            byte r4 = r1[r4]     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            r23 = r1
            r1 = 1
            if (r4 != r1) goto L_0x03ae
            int r10 = r10 + 1
            if (r10 <= r1) goto L_0x03ae
            int r1 = r14.offset     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            int r1 = r1 + r5
            r14.offset = r1     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            int r1 = r14.size     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            int r1 = r1 - r5
            r14.size = r1     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            goto L_0x03b5
        L_0x03ac:
            r23 = r1
        L_0x03ae:
            int r5 = r5 + 1
            r1 = r23
            r4 = 100
            goto L_0x0380
        L_0x03b5:
            r5 = 0
        L_0x03b6:
            org.telegram.messenger.video.MP4Builder r1 = r12.mediaMuxer     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            r10 = r5
            r4 = 1
            long r5 = r1.writeSampleData(r8, r6, r14, r4)     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            r1 = r3
            r3 = 0
            int r23 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r23 == 0) goto L_0x03d9
            org.telegram.messenger.MediaController$VideoConvertorListener r3 = r12.callback     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            r23 = r1
            r4 = r2
            r26 = r10
            r1 = 0
            if (r3 == 0) goto L_0x03e2
            float r10 = (float) r1     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            float r10 = r10 / r24
            float r10 = r10 / r25
            r3.didWriteData(r5, r10)     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            goto L_0x03e2
        L_0x03d9:
            r23 = r1
            r26 = r10
            r70 = r3
            r4 = r2
            r1 = r70
        L_0x03e2:
            r2 = r19
            r10 = r22
            goto L_0x0499
        L_0x03e8:
            r23 = r3
            r21 = r4
            r3 = -5
            r4 = r2
            r1 = 0
            if (r8 != r3) goto L_0x046c
            byte[] r3 = new byte[r10]     // Catch:{ Exception -> 0x0484, all -> 0x0470 }
            int r1 = r14.offset     // Catch:{ Exception -> 0x0484, all -> 0x0470 }
            int r1 = r1 + r10
            r6.limit(r1)     // Catch:{ Exception -> 0x0484, all -> 0x0470 }
            int r1 = r14.offset     // Catch:{ Exception -> 0x0484, all -> 0x0470 }
            r6.position(r1)     // Catch:{ Exception -> 0x0484, all -> 0x0470 }
            r6.get(r3)     // Catch:{ Exception -> 0x0484, all -> 0x0470 }
            int r1 = r14.size     // Catch:{ Exception -> 0x0484, all -> 0x0470 }
            r2 = 1
            int r1 = r1 - r2
        L_0x0406:
            if (r1 < 0) goto L_0x0449
            r6 = 3
            if (r1 <= r6) goto L_0x0449
            byte r6 = r3[r1]     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            if (r6 != r2) goto L_0x0441
            int r2 = r1 + -1
            byte r2 = r3[r2]     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            if (r2 != 0) goto L_0x0441
            int r2 = r1 + -2
            byte r2 = r3[r2]     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            if (r2 != 0) goto L_0x0441
            int r2 = r1 + -3
            byte r6 = r3[r2]     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            if (r6 != 0) goto L_0x0441
            java.nio.ByteBuffer r1 = java.nio.ByteBuffer.allocate(r2)     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            int r6 = r14.size     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            int r6 = r6 - r2
            java.nio.ByteBuffer r6 = java.nio.ByteBuffer.allocate(r6)     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            r26 = r5
            r10 = 0
            java.nio.ByteBuffer r5 = r1.put(r3, r10, r2)     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            r5.position(r10)     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            int r5 = r14.size     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            int r5 = r5 - r2
            java.nio.ByteBuffer r2 = r6.put(r3, r2, r5)     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            r2.position(r10)     // Catch:{ Exception -> 0x033f, all -> 0x0335 }
            goto L_0x044d
        L_0x0441:
            r26 = r5
            int r1 = r1 + -1
            r5 = r26
            r2 = 1
            goto L_0x0406
        L_0x0449:
            r26 = r5
            r1 = 0
            r6 = 0
        L_0x044d:
            r2 = r19
            r10 = r22
            android.media.MediaFormat r3 = android.media.MediaFormat.createVideoFormat(r15, r2, r10)     // Catch:{ Exception -> 0x046a, all -> 0x0468 }
            if (r1 == 0) goto L_0x045f
            if (r6 == 0) goto L_0x045f
            r3.setByteBuffer(r7, r1)     // Catch:{ Exception -> 0x046a, all -> 0x0468 }
            r3.setByteBuffer(r13, r6)     // Catch:{ Exception -> 0x046a, all -> 0x0468 }
        L_0x045f:
            org.telegram.messenger.video.MP4Builder r1 = r12.mediaMuxer     // Catch:{ Exception -> 0x046a, all -> 0x0468 }
            r5 = 0
            int r1 = r1.addTrack(r3, r5)     // Catch:{ Exception -> 0x046a, all -> 0x0468 }
            r8 = r1
            goto L_0x0499
        L_0x0468:
            r0 = move-exception
            goto L_0x0475
        L_0x046a:
            r0 = move-exception
            goto L_0x0489
        L_0x046c:
            r26 = r5
            goto L_0x03e2
        L_0x0470:
            r0 = move-exception
            r2 = r19
            r10 = r22
        L_0x0475:
            r14 = r81
            r21 = r86
            r27 = r88
            r5 = r2
            r13 = r8
            r15 = r12
            r1 = r38
            r6 = 0
        L_0x0481:
            r2 = r0
            goto L_0x14e8
        L_0x0484:
            r0 = move-exception
            r2 = r19
            r10 = r22
        L_0x0489:
            r1 = r0
            r14 = r2
            r13 = r8
            goto L_0x066e
        L_0x048e:
            r23 = r3
            r21 = r4
            r26 = r5
            r10 = r22
            r4 = r2
            r2 = r19
        L_0x0499:
            r5 = r26
            int r1 = r14.flags     // Catch:{ Exception -> 0x0502, all -> 0x04fd }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x04a3
            r1 = 1
            goto L_0x04a4
        L_0x04a3:
            r1 = 0
        L_0x04a4:
            r3 = 0
            r11.releaseOutputBuffer(r9, r3)     // Catch:{ Exception -> 0x0502, all -> 0x04fd }
            r19 = r82
            r6 = r1
            r3 = r23
            r1 = -1
        L_0x04ae:
            if (r9 == r1) goto L_0x04c6
            r1 = r80
            r34 = r7
            r22 = r10
            r39 = r13
            r13 = r19
            r19 = r2
            r2 = r4
            r7 = r6
            r4 = r21
            r6 = r79
            r79 = r11
            goto L_0x0219
        L_0x04c6:
            if (r4 != 0) goto L_0x050d
            r18.drawImage()     // Catch:{ Exception -> 0x0502, all -> 0x04fd }
            r1 = r21
            float r9 = (float) r1
            r21 = 1106247680(0x41var_, float:30.0)
            float r9 = r9 / r21
            float r9 = r9 * r24
            float r9 = r9 * r24
            float r9 = r9 * r24
            r21 = r2
            r82 = r3
            long r2 = (long) r9
            r9 = r35
            r9.setPresentationTime(r2)     // Catch:{ Exception -> 0x04f7, all -> 0x0583 }
            r9.swapBuffers()     // Catch:{ Exception -> 0x04f7, all -> 0x0583 }
            int r1 = r1 + 1
            float r2 = (float) r1     // Catch:{ Exception -> 0x04f7, all -> 0x0583 }
            r3 = 1106247680(0x41var_, float:30.0)
            float r3 = r3 * r25
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 < 0) goto L_0x0515
            r11.signalEndOfInputStream()     // Catch:{ Exception -> 0x04f7, all -> 0x0583 }
            r4 = r1
            r1 = 0
            r2 = 1
            goto L_0x0519
        L_0x04f7:
            r0 = move-exception
            r1 = r0
            r13 = r8
            r35 = r9
            goto L_0x0509
        L_0x04fd:
            r0 = move-exception
            r21 = r2
            goto L_0x055c
        L_0x0502:
            r0 = move-exception
            r21 = r2
        L_0x0505:
            r9 = r35
            r1 = r0
            r13 = r8
        L_0x0509:
            r14 = r21
            goto L_0x066e
        L_0x050d:
            r82 = r3
            r1 = r21
            r9 = r35
            r21 = r2
        L_0x0515:
            r2 = r4
            r4 = r1
            r1 = r79
        L_0x0519:
            r3 = r82
            r34 = r7
            r35 = r9
            r22 = r10
            r79 = r11
            r39 = r13
            r13 = r19
            r19 = r21
            r7 = r6
            r6 = r1
            r1 = r80
            goto L_0x0219
        L_0x052f:
            r0 = move-exception
            r21 = r19
            r10 = r22
            goto L_0x0505
        L_0x0535:
            r21 = r19
            r10 = r22
            r1 = r35
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0585, all -> 0x0583 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0585, all -> 0x0583 }
            r3.<init>()     // Catch:{ Exception -> 0x0585, all -> 0x0583 }
            java.lang.String r4 = "encoderOutputBuffer "
            r3.append(r4)     // Catch:{ Exception -> 0x0585, all -> 0x0583 }
            r3.append(r9)     // Catch:{ Exception -> 0x0585, all -> 0x0583 }
            java.lang.String r4 = " was null"
            r3.append(r4)     // Catch:{ Exception -> 0x0585, all -> 0x0583 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0585, all -> 0x0583 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0585, all -> 0x0583 }
            throw r2     // Catch:{ Exception -> 0x0585, all -> 0x0583 }
        L_0x0557:
            r0 = move-exception
            r21 = r19
            r10 = r22
        L_0x055c:
            r14 = r81
            r27 = r88
            r2 = r0
            r13 = r8
            r15 = r12
            goto L_0x05b5
        L_0x0564:
            r0 = move-exception
            goto L_0x05a0
        L_0x0566:
            r21 = r19
            r10 = r22
            r1 = r35
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0585, all -> 0x0583 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0585, all -> 0x0583 }
            r3.<init>()     // Catch:{ Exception -> 0x0585, all -> 0x0583 }
            java.lang.String r4 = "unexpected result from encoder.dequeueOutputBuffer: "
            r3.append(r4)     // Catch:{ Exception -> 0x0585, all -> 0x0583 }
            r3.append(r9)     // Catch:{ Exception -> 0x0585, all -> 0x0583 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0585, all -> 0x0583 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0585, all -> 0x0583 }
            throw r2     // Catch:{ Exception -> 0x0585, all -> 0x0583 }
        L_0x0583:
            r0 = move-exception
            goto L_0x055c
        L_0x0585:
            r0 = move-exception
            r35 = r1
            goto L_0x05a6
        L_0x0589:
            r0 = move-exception
            r12 = r72
            goto L_0x05a0
        L_0x058d:
            r0 = move-exception
            r21 = r19
            r10 = r22
            r15 = r72
            r14 = r81
            r27 = r88
            r2 = r0
            r13 = r8
            goto L_0x05b5
        L_0x059b:
            r0 = move-exception
            r12 = r72
            r11 = r79
        L_0x05a0:
            r21 = r19
            r10 = r22
            r1 = r35
        L_0x05a6:
            r13 = r8
            goto L_0x05c8
        L_0x05a8:
            r0 = move-exception
            r8 = r13
            r21 = r19
            r10 = r22
            r15 = r72
            r14 = r81
            r27 = r88
            r2 = r0
        L_0x05b5:
            r5 = r21
        L_0x05b7:
            r1 = r38
            r6 = 0
            goto L_0x0643
        L_0x05bc:
            r0 = move-exception
            r12 = r72
            r11 = r79
            r8 = r13
            r21 = r19
            r10 = r22
            r1 = r35
        L_0x05c8:
            r14 = r21
            goto L_0x0613
        L_0x05cb:
            r12 = r72
            r11 = r79
            r8 = r13
            r21 = r19
            r10 = r22
            r1 = r35
            r1 = r8
            r9 = r11
            r6 = r38
            r13 = 0
            r36 = 0
            r8 = r81
            goto L_0x06b1
        L_0x05e1:
            r0 = move-exception
            r12 = r72
            r11 = r79
            r21 = r19
            r10 = r22
            r1 = r35
            r14 = r21
            r13 = -5
            goto L_0x0613
        L_0x05f0:
            r0 = move-exception
            r21 = r19
            r10 = r22
            goto L_0x0636
        L_0x05f6:
            r0 = move-exception
            r12 = r72
            r11 = r79
            r21 = r19
            r10 = r22
            r1 = r35
            r14 = r21
            goto L_0x0610
        L_0x0604:
            r0 = move-exception
            r12 = r72
            r1 = r6
            r38 = r7
            r11 = r9
            r21 = r14
            r10 = r15
            r35 = r1
        L_0x0610:
            r13 = -5
            r18 = 0
        L_0x0613:
            r1 = r0
            goto L_0x066e
        L_0x0616:
            r0 = move-exception
            r12 = r72
            r38 = r7
            r11 = r9
            r21 = r14
            r10 = r15
            r1 = r0
            goto L_0x0669
        L_0x0621:
            r0 = move-exception
            r38 = r7
            r21 = r14
            r10 = r15
            goto L_0x0636
        L_0x0628:
            r0 = move-exception
            r12 = r72
            r38 = r7
            r21 = r14
            r10 = r15
            goto L_0x064e
        L_0x0631:
            r0 = move-exception
            r38 = r7
            r21 = r14
        L_0x0636:
            r15 = r72
            r14 = r81
            r27 = r88
            r2 = r0
            r5 = r21
        L_0x063f:
            r1 = r38
            r6 = 0
            r13 = -5
        L_0x0643:
            r21 = r86
            goto L_0x14e8
        L_0x0647:
            r0 = move-exception
            r12 = r72
            r38 = r7
            r21 = r14
        L_0x064e:
            r1 = r0
            goto L_0x0668
        L_0x0650:
            r0 = move-exception
            r38 = r7
            r15 = r72
            r14 = r81
            r21 = r86
            r27 = r88
            r2 = r0
            r5 = r9
            r1 = r38
            goto L_0x14e6
        L_0x0661:
            r0 = move-exception
            r12 = r72
            r38 = r7
            r1 = r0
        L_0x0667:
            r14 = r9
        L_0x0668:
            r11 = 0
        L_0x0669:
            r13 = -5
            r18 = 0
            r35 = 0
        L_0x066e:
            boolean r2 = r1 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x0701 }
            if (r2 == 0) goto L_0x0677
            if (r93 != 0) goto L_0x0677
            r36 = 1
            goto L_0x0679
        L_0x0677:
            r36 = 0
        L_0x0679:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x06f3 }
            r2.<init>()     // Catch:{ all -> 0x06f3 }
            java.lang.String r3 = "bitrate: "
            r2.append(r3)     // Catch:{ all -> 0x06f3 }
            r6 = r38
            r2.append(r6)     // Catch:{ all -> 0x06f1 }
            java.lang.String r3 = " framerate: "
            r2.append(r3)     // Catch:{ all -> 0x06f1 }
            r8 = r81
            r2.append(r8)     // Catch:{ all -> 0x06e4 }
            java.lang.String r3 = " size: "
            r2.append(r3)     // Catch:{ all -> 0x06e4 }
            r2.append(r10)     // Catch:{ all -> 0x06e4 }
            java.lang.String r3 = "x"
            r2.append(r3)     // Catch:{ all -> 0x06e4 }
            r2.append(r14)     // Catch:{ all -> 0x06e4 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x06e4 }
            org.telegram.messenger.FileLog.e((java.lang.String) r2)     // Catch:{ all -> 0x06e4 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x06e4 }
            r9 = r11
            r1 = r13
            r21 = r14
            r13 = 1
        L_0x06b1:
            if (r18 == 0) goto L_0x06c5
            r18.release()     // Catch:{ all -> 0x06b7 }
            goto L_0x06c5
        L_0x06b7:
            r0 = move-exception
            r27 = r88
            r2 = r0
            r13 = r1
            r1 = r6
            r14 = r8
            r15 = r12
            r5 = r21
            r6 = r36
            goto L_0x0643
        L_0x06c5:
            if (r35 == 0) goto L_0x06ca
            r35.release()     // Catch:{ all -> 0x06b7 }
        L_0x06ca:
            if (r9 == 0) goto L_0x06d2
            r9.stop()     // Catch:{ all -> 0x06b7 }
            r9.release()     // Catch:{ all -> 0x06b7 }
        L_0x06d2:
            r72.checkConversionCanceled()     // Catch:{ all -> 0x06b7 }
            r27 = r88
            r14 = r8
            r15 = r12
            r5 = r21
            r21 = r86
            r70 = r13
            r13 = r1
            r1 = r70
            goto L_0x14af
        L_0x06e4:
            r0 = move-exception
            r21 = r86
            r27 = r88
            r2 = r0
            r1 = r6
            r15 = r12
            r5 = r14
            r6 = r36
            goto L_0x0121
        L_0x06f1:
            r0 = move-exception
            goto L_0x06f6
        L_0x06f3:
            r0 = move-exception
            r6 = r38
        L_0x06f6:
            r21 = r86
            r27 = r88
            r2 = r0
            r1 = r6
            r15 = r12
            r5 = r14
            r6 = r36
            goto L_0x070d
        L_0x0701:
            r0 = move-exception
            r6 = r38
            r21 = r86
            r27 = r88
            r2 = r0
            r1 = r6
            r15 = r12
            r5 = r14
            r6 = 0
        L_0x070d:
            r14 = r81
            goto L_0x14e8
        L_0x0711:
            r12 = r72
            r33 = r4
            r15 = r13
            r14 = r23
            r1 = 921600(0xe1000, float:1.291437E-39)
            r13 = r11
            r11 = r7
            r7 = r5
            android.media.MediaExtractor r2 = new android.media.MediaExtractor     // Catch:{ all -> 0x14d3 }
            r2.<init>()     // Catch:{ all -> 0x14d3 }
            r12.extractor = r2     // Catch:{ all -> 0x14d3 }
            r5 = r73
            r2.setDataSource(r5)     // Catch:{ all -> 0x14d3 }
            android.media.MediaExtractor r2 = r12.extractor     // Catch:{ all -> 0x14d3 }
            r6 = 0
            int r4 = org.telegram.messenger.MediaController.findTrack(r2, r6)     // Catch:{ all -> 0x14d3 }
            r2 = -1
            if (r11 == r2) goto L_0x0749
            android.media.MediaExtractor r2 = r12.extractor     // Catch:{ all -> 0x073d }
            r3 = 1
            int r2 = org.telegram.messenger.MediaController.findTrack(r2, r3)     // Catch:{ all -> 0x073d }
            r3 = r2
            goto L_0x074a
        L_0x073d:
            r0 = move-exception
            r21 = r86
            r27 = r88
            r2 = r0
            r14 = r8
            r5 = r9
            r1 = r11
            r15 = r12
            goto L_0x14e7
        L_0x0749:
            r3 = -1
        L_0x074a:
            java.lang.String r2 = "mime"
            if (r4 < 0) goto L_0x076e
            android.media.MediaExtractor r6 = r12.extractor     // Catch:{ all -> 0x0762 }
            android.media.MediaFormat r6 = r6.getTrackFormat(r4)     // Catch:{ all -> 0x0762 }
            java.lang.String r6 = r6.getString(r2)     // Catch:{ all -> 0x0762 }
            boolean r6 = r6.equals(r15)     // Catch:{ all -> 0x0762 }
            if (r6 != 0) goto L_0x076e
            r39 = r13
            r6 = 1
            goto L_0x0771
        L_0x0762:
            r0 = move-exception
            r21 = r86
            r27 = r88
            r2 = r0
            r14 = r8
            r5 = r9
            r1 = r11
        L_0x076b:
            r15 = r12
            goto L_0x14e6
        L_0x076e:
            r39 = r13
            r6 = 0
        L_0x0771:
            r13 = r92
            if (r13 != 0) goto L_0x07c9
            if (r6 == 0) goto L_0x0779
            goto L_0x07c9
        L_0x0779:
            android.media.MediaExtractor r2 = r12.extractor     // Catch:{ all -> 0x07bb }
            org.telegram.messenger.video.MP4Builder r3 = r12.mediaMuxer     // Catch:{ all -> 0x07bb }
            r1 = -1
            if (r11 == r1) goto L_0x0782
            r15 = 1
            goto L_0x0783
        L_0x0782:
            r15 = 0
        L_0x0783:
            r1 = r72
            r4 = r14
            r14 = r5
            r7 = 0
            r5 = r84
            r11 = r8
            r13 = 0
            r7 = r86
            r9 = r90
            r13 = r11
            r11 = r74
            r14 = r12
            r12 = r15
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ all -> 0x07aa }
            r5 = r79
            r10 = r80
            r6 = r82
            r21 = r86
            r27 = r88
            r15 = r14
            r1 = 0
            r36 = 0
            r14 = r13
            r13 = -5
            goto L_0x14af
        L_0x07aa:
            r0 = move-exception
            r5 = r79
            r10 = r80
            r1 = r82
        L_0x07b1:
            r21 = r86
            r27 = r88
            r2 = r0
        L_0x07b6:
            r15 = r14
        L_0x07b7:
            r6 = 0
            r14 = r13
            goto L_0x14e7
        L_0x07bb:
            r0 = move-exception
            r5 = r79
            r10 = r80
            r1 = r82
            r21 = r86
            r27 = r88
            r2 = r0
            r14 = r8
            goto L_0x076b
        L_0x07c9:
            r13 = r8
            r11 = r14
            r14 = r12
            r12 = r5
            if (r4 < 0) goto L_0x146c
            r21 = -2147483648(0xfffffffvar_, double:NaN)
            r5 = 1000(0x3e8, float:1.401E-42)
            int r5 = r5 / r13
            int r5 = r5 * 1000
            long r9 = (long) r5     // Catch:{ Exception -> 0x13cd, all -> 0x13c1 }
            android.media.MediaExtractor r5 = r14.extractor     // Catch:{ Exception -> 0x13cd, all -> 0x13c1 }
            r5.selectTrack(r4)     // Catch:{ Exception -> 0x13cd, all -> 0x13c1 }
            android.media.MediaExtractor r5 = r14.extractor     // Catch:{ Exception -> 0x13cd, all -> 0x13c1 }
            android.media.MediaFormat r8 = r5.getTrackFormat(r4)     // Catch:{ Exception -> 0x13cd, all -> 0x13c1 }
            r5 = 0
            int r23 = (r88 > r5 ? 1 : (r88 == r5 ? 0 : -1))
            if (r23 < 0) goto L_0x0808
            r23 = 1157234688(0x44fa0000, float:2000.0)
            int r23 = (r25 > r23 ? 1 : (r25 == r23 ? 0 : -1))
            if (r23 > 0) goto L_0x07f3
            r23 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x0801
        L_0x07f3:
            r23 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r23 = (r25 > r23 ? 1 : (r25 == r23 ? 0 : -1))
            if (r23 > 0) goto L_0x07fe
            r23 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x0801
        L_0x07fe:
            r23 = 1560000(0x17cdc0, float:2.186026E-39)
        L_0x0801:
            r28 = r3
            r26 = r5
            r1 = r23
            goto L_0x0811
        L_0x0808:
            if (r82 > 0) goto L_0x080b
            goto L_0x080d
        L_0x080b:
            r1 = r82
        L_0x080d:
            r26 = r88
            r28 = r3
        L_0x0811:
            r3 = r83
            if (r3 <= 0) goto L_0x0831
            int r1 = java.lang.Math.min(r3, r1)     // Catch:{ Exception -> 0x0826, all -> 0x081a }
            goto L_0x0831
        L_0x081a:
            r0 = move-exception
            r5 = r79
            r10 = r80
            r21 = r86
            r2 = r0
            r15 = r14
            r27 = r26
            goto L_0x07b7
        L_0x0826:
            r0 = move-exception
            r21 = r86
            r2 = r0
            r54 = r4
            r14 = r13
            r27 = r26
            goto L_0x13da
        L_0x0831:
            int r30 = (r26 > r5 ? 1 : (r26 == r5 ? 0 : -1))
            if (r30 < 0) goto L_0x083a
            r30 = r4
            r3 = r18
            goto L_0x083e
        L_0x083a:
            r30 = r4
            r3 = r26
        L_0x083e:
            int r26 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r26 < 0) goto L_0x0865
            android.media.MediaExtractor r5 = r14.extractor     // Catch:{ Exception -> 0x085a, all -> 0x084e }
            r6 = 0
            r5.seekTo(r3, r6)     // Catch:{ Exception -> 0x085a, all -> 0x084e }
            r88 = r3
            r4 = 0
            r5 = 0
            goto L_0x0893
        L_0x084e:
            r0 = move-exception
            r5 = r79
            r10 = r80
            r21 = r86
            r2 = r0
            r27 = r3
            goto L_0x07b6
        L_0x085a:
            r0 = move-exception
            r21 = r86
            r2 = r0
            r27 = r3
        L_0x0860:
            r14 = r13
            r54 = r30
            goto L_0x13da
        L_0x0865:
            r26 = r5
            r5 = r84
            int r31 = (r5 > r26 ? 1 : (r5 == r26 ? 0 : -1))
            if (r31 <= 0) goto L_0x0889
            r88 = r3
            android.media.MediaExtractor r3 = r14.extractor     // Catch:{ Exception -> 0x0882, all -> 0x087b }
            r4 = 0
            r3.seekTo(r5, r4)     // Catch:{ Exception -> 0x0882, all -> 0x087b }
            r3 = r98
            r4 = 0
            r5 = 0
            goto L_0x0895
        L_0x087b:
            r0 = move-exception
            r5 = r79
            r10 = r80
            goto L_0x07b1
        L_0x0882:
            r0 = move-exception
            r21 = r86
            r27 = r88
            r2 = r0
            goto L_0x0860
        L_0x0889:
            r88 = r3
            android.media.MediaExtractor r3 = r14.extractor     // Catch:{ Exception -> 0x13b3, all -> 0x13a0 }
            r4 = 0
            r5 = 0
            r3.seekTo(r5, r4)     // Catch:{ Exception -> 0x13b3, all -> 0x13a0 }
        L_0x0893:
            r3 = r98
        L_0x0895:
            if (r3 == 0) goto L_0x08bc
            r4 = 90
            r14 = r75
            if (r14 == r4) goto L_0x08a7
            r4 = 270(0x10e, float:3.78E-43)
            if (r14 != r4) goto L_0x08a2
            goto L_0x08a7
        L_0x08a2:
            int r4 = r3.transformWidth     // Catch:{ Exception -> 0x0882, all -> 0x08ad }
            int r5 = r3.transformHeight     // Catch:{ Exception -> 0x0882, all -> 0x08ad }
            goto L_0x08ab
        L_0x08a7:
            int r4 = r3.transformHeight     // Catch:{ Exception -> 0x0882, all -> 0x08ad }
            int r5 = r3.transformWidth     // Catch:{ Exception -> 0x0882, all -> 0x08ad }
        L_0x08ab:
            r6 = r4
            goto L_0x08c2
        L_0x08ad:
            r0 = move-exception
            r15 = r72
            r5 = r79
            r10 = r80
            r21 = r86
            r27 = r88
            r2 = r0
            r14 = r13
            goto L_0x14e6
        L_0x08bc:
            r14 = r75
            r6 = r79
            r5 = r80
        L_0x08c2:
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x13b3, all -> 0x13a0 }
            if (r4 == 0) goto L_0x08e2
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0882, all -> 0x08ad }
            r4.<init>()     // Catch:{ Exception -> 0x0882, all -> 0x08ad }
            java.lang.String r3 = "create encoder with w = "
            r4.append(r3)     // Catch:{ Exception -> 0x0882, all -> 0x08ad }
            r4.append(r6)     // Catch:{ Exception -> 0x0882, all -> 0x08ad }
            java.lang.String r3 = " h = "
            r4.append(r3)     // Catch:{ Exception -> 0x0882, all -> 0x08ad }
            r4.append(r5)     // Catch:{ Exception -> 0x0882, all -> 0x08ad }
            java.lang.String r3 = r4.toString()     // Catch:{ Exception -> 0x0882, all -> 0x08ad }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x0882, all -> 0x08ad }
        L_0x08e2:
            android.media.MediaFormat r3 = android.media.MediaFormat.createVideoFormat(r15, r6, r5)     // Catch:{ Exception -> 0x13b3, all -> 0x13a0 }
            java.lang.String r4 = "color-format"
            r34 = r7
            r7 = 2130708361(0x7var_, float:1.701803E38)
            r3.setInteger(r4, r7)     // Catch:{ Exception -> 0x13b3, all -> 0x13a0 }
            java.lang.String r4 = "bitrate"
            r3.setInteger(r4, r1)     // Catch:{ Exception -> 0x13b3, all -> 0x13a0 }
            java.lang.String r4 = "frame-rate"
            r3.setInteger(r4, r13)     // Catch:{ Exception -> 0x13b3, all -> 0x13a0 }
            java.lang.String r4 = "i-frame-interval"
            r7 = 2
            r3.setInteger(r4, r7)     // Catch:{ Exception -> 0x13b3, all -> 0x13a0 }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x13b3, all -> 0x13a0 }
            r4 = 23
            if (r7 >= r4) goto L_0x091e
            int r4 = java.lang.Math.min(r5, r6)     // Catch:{ Exception -> 0x0882, all -> 0x08ad }
            r26 = r5
            r5 = 480(0x1e0, float:6.73E-43)
            if (r4 > r5) goto L_0x0920
            r4 = 921600(0xe1000, float:1.291437E-39)
            if (r1 <= r4) goto L_0x0918
            r1 = 921600(0xe1000, float:1.291437E-39)
        L_0x0918:
            java.lang.String r4 = "bitrate"
            r3.setInteger(r4, r1)     // Catch:{ Exception -> 0x0882, all -> 0x08ad }
            goto L_0x0920
        L_0x091e:
            r26 = r5
        L_0x0920:
            r23 = r1
            android.media.MediaCodec r5 = android.media.MediaCodec.createEncoderByType(r15)     // Catch:{ Exception -> 0x1392, all -> 0x1380 }
            r1 = 0
            r4 = 1
            r5.configure(r3, r1, r1, r4)     // Catch:{ Exception -> 0x1369, all -> 0x1380 }
            org.telegram.messenger.video.InputSurface r3 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x1369, all -> 0x1380 }
            android.view.Surface r1 = r5.createInputSurface()     // Catch:{ Exception -> 0x1369, all -> 0x1380 }
            r3.<init>(r1)     // Catch:{ Exception -> 0x1369, all -> 0x1380 }
            r3.makeCurrent()     // Catch:{ Exception -> 0x134f, all -> 0x1380 }
            r5.start()     // Catch:{ Exception -> 0x134f, all -> 0x1380 }
            java.lang.String r1 = r8.getString(r2)     // Catch:{ Exception -> 0x134f, all -> 0x1380 }
            android.media.MediaCodec r1 = android.media.MediaCodec.createDecoderByType(r1)     // Catch:{ Exception -> 0x134f, all -> 0x1380 }
            r82 = r3
            org.telegram.messenger.video.OutputSurface r3 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x1338, all -> 0x1380 }
            r27 = 0
            float r12 = (float) r13
            r31 = 0
            r49 = r1
            r1 = r3
            r50 = r2
            r2 = r94
            r52 = r82
            r42 = r88
            r53 = r3
            r51 = r28
            r3 = r27
            r54 = r30
            r27 = 1
            r28 = 0
            r4 = r95
            r82 = r5
            r55 = r26
            r5 = r96
            r58 = r6
            r59 = r33
            r6 = r98
            r60 = r7
            r61 = r34
            r7 = r79
            r62 = r8
            r8 = r80
            r32 = r9
            r9 = r77
            r10 = r78
            r63 = r11
            r11 = r75
            r26 = r15
            r14 = r39
            r15 = 1
            r13 = r31
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x1328, all -> 0x1323 }
            r6 = r77
            r7 = r78
            r2 = r79
            r3 = r80
            java.lang.String r1 = createFragmentShader(r6, r7, r2, r3, r15)     // Catch:{ Exception -> 0x130b, all -> 0x1323 }
            r4 = 0
            java.lang.String r5 = createFragmentShader(r6, r7, r2, r3, r4)     // Catch:{ Exception -> 0x130b, all -> 0x1323 }
            r8 = r53
            r8.changeFragmentShader(r1, r5)     // Catch:{ Exception -> 0x1303, all -> 0x1323 }
            android.view.Surface r1 = r8.getSurface()     // Catch:{ Exception -> 0x1303, all -> 0x1323 }
            r10 = r49
            r9 = r62
            r5 = 0
            r10.configure(r9, r1, r5, r4)     // Catch:{ Exception -> 0x12f3, all -> 0x1323 }
            r10.start()     // Catch:{ Exception -> 0x12f3, all -> 0x1323 }
            r1 = r60
            r9 = 21
            if (r1 >= r9) goto L_0x09e6
            java.nio.ByteBuffer[] r1 = r10.getInputBuffers()     // Catch:{ Exception -> 0x09d3, all -> 0x09c2 }
            java.nio.ByteBuffer[] r9 = r82.getOutputBuffers()     // Catch:{ Exception -> 0x09d3, all -> 0x09c2 }
            goto L_0x09e8
        L_0x09c2:
            r0 = move-exception
            r15 = r72
            r14 = r81
            r21 = r86
            r5 = r2
            r10 = r3
        L_0x09cb:
            r1 = r23
            r27 = r42
            r6 = 0
            r13 = -5
            goto L_0x0481
        L_0x09d3:
            r0 = move-exception
            r14 = r81
            r47 = r82
            r21 = r86
            r2 = r0
            r29 = r5
            r53 = r8
        L_0x09df:
            r1 = r23
            r27 = r42
            r13 = -5
            goto L_0x13e4
        L_0x09e6:
            r1 = r5
            r9 = r1
        L_0x09e8:
            r11 = r51
            if (r11 < 0) goto L_0x0b18
            r13 = r72
            android.media.MediaExtractor r12 = r13.extractor     // Catch:{ Exception -> 0x0b01, all -> 0x0af1 }
            android.media.MediaFormat r12 = r12.getTrackFormat(r11)     // Catch:{ Exception -> 0x0b01, all -> 0x0af1 }
            r5 = r50
            java.lang.String r4 = r12.getString(r5)     // Catch:{ Exception -> 0x0b01, all -> 0x0af1 }
            java.lang.String r15 = "audio/mp4a-latm"
            boolean r4 = r4.equals(r15)     // Catch:{ Exception -> 0x0b01, all -> 0x0af1 }
            if (r4 != 0) goto L_0x0a26
            java.lang.String r4 = r12.getString(r5)     // Catch:{ Exception -> 0x0a1a, all -> 0x0a11 }
            java.lang.String r15 = "audio/mpeg"
            boolean r4 = r4.equals(r15)     // Catch:{ Exception -> 0x0a1a, all -> 0x0a11 }
            if (r4 == 0) goto L_0x0a0f
            goto L_0x0a26
        L_0x0a0f:
            r4 = 0
            goto L_0x0a27
        L_0x0a11:
            r0 = move-exception
            r14 = r81
            r21 = r86
            r5 = r2
            r10 = r3
            goto L_0x0afe
        L_0x0a1a:
            r0 = move-exception
        L_0x0a1b:
            r14 = r81
            r47 = r82
            r21 = r86
            r2 = r0
            r53 = r8
            goto L_0x131a
        L_0x0a26:
            r4 = 1
        L_0x0a27:
            java.lang.String r5 = r12.getString(r5)     // Catch:{ Exception -> 0x0b01, all -> 0x0af1 }
            java.lang.String r15 = "audio/unknown"
            boolean r5 = r5.equals(r15)     // Catch:{ Exception -> 0x0b01, all -> 0x0af1 }
            if (r5 == 0) goto L_0x0a34
            r11 = -1
        L_0x0a34:
            if (r11 < 0) goto L_0x0ae6
            if (r4 == 0) goto L_0x0a8e
            org.telegram.messenger.video.MP4Builder r5 = r13.mediaMuxer     // Catch:{ Exception -> 0x0a8a, all -> 0x0a7b }
            r15 = 1
            int r5 = r5.addTrack(r12, r15)     // Catch:{ Exception -> 0x0a8a, all -> 0x0a7b }
            android.media.MediaExtractor r15 = r13.extractor     // Catch:{ Exception -> 0x0a8a, all -> 0x0a7b }
            r15.selectTrack(r11)     // Catch:{ Exception -> 0x0a8a, all -> 0x0a7b }
            java.lang.String r15 = "max-input-size"
            int r12 = r12.getInteger(r15)     // Catch:{ Exception -> 0x0a4b, all -> 0x0a11 }
            goto L_0x0a51
        L_0x0a4b:
            r0 = move-exception
            r12 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)     // Catch:{ Exception -> 0x0a8a, all -> 0x0a7b }
            r12 = 0
        L_0x0a51:
            if (r12 > 0) goto L_0x0a55
            r12 = 65536(0x10000, float:9.18355E-41)
        L_0x0a55:
            java.nio.ByteBuffer r15 = java.nio.ByteBuffer.allocateDirect(r12)     // Catch:{ Exception -> 0x0a8a, all -> 0x0a7b }
            r6 = r84
            r88 = r4
            r89 = r5
            r4 = 0
            int r27 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r27 <= 0) goto L_0x0a6c
            android.media.MediaExtractor r4 = r13.extractor     // Catch:{ Exception -> 0x0a1a, all -> 0x0a11 }
            r5 = 0
            r4.seekTo(r6, r5)     // Catch:{ Exception -> 0x0a1a, all -> 0x0a11 }
            goto L_0x0a74
        L_0x0a6c:
            android.media.MediaExtractor r4 = r13.extractor     // Catch:{ Exception -> 0x0a1a, all -> 0x0aaa }
            r2 = 0
            r5 = 0
            r4.seekTo(r2, r5)     // Catch:{ Exception -> 0x0a1a, all -> 0x0aaa }
        L_0x0a74:
            r5 = r88
            r2 = r89
            r4 = 0
            goto L_0x0b23
        L_0x0a7b:
            r0 = move-exception
            r6 = r84
        L_0x0a7e:
            r5 = r79
            r10 = r80
            r14 = r81
            r21 = r86
            r2 = r0
            r15 = r13
            goto L_0x138f
        L_0x0a8a:
            r0 = move-exception
            r6 = r84
            goto L_0x0a1b
        L_0x0a8e:
            r6 = r84
            r88 = r4
            android.media.MediaExtractor r2 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0ae4, all -> 0x0ae2 }
            r2.<init>()     // Catch:{ Exception -> 0x0ae4, all -> 0x0ae2 }
            r3 = r73
            r2.setDataSource(r3)     // Catch:{ Exception -> 0x0ae4, all -> 0x0ae2 }
            r2.selectTrack(r11)     // Catch:{ Exception -> 0x0ae4, all -> 0x0ae2 }
            r4 = 0
            int r15 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r15 <= 0) goto L_0x0aac
            r15 = 0
            r2.seekTo(r6, r15)     // Catch:{ Exception -> 0x0a1a, all -> 0x0aaa }
            goto L_0x0ab0
        L_0x0aaa:
            r0 = move-exception
            goto L_0x0a7e
        L_0x0aac:
            r15 = 0
            r2.seekTo(r4, r15)     // Catch:{ Exception -> 0x0ae4, all -> 0x0ae2 }
        L_0x0ab0:
            org.telegram.messenger.video.AudioRecoder r4 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x0ae4, all -> 0x0ae2 }
            r4.<init>(r12, r2, r11)     // Catch:{ Exception -> 0x0ae4, all -> 0x0ae2 }
            r4.startTime = r6     // Catch:{ Exception -> 0x0acf, all -> 0x0ae2 }
            r2 = r86
            r4.endTime = r2     // Catch:{ Exception -> 0x0acd, all -> 0x0acb }
            org.telegram.messenger.video.MP4Builder r5 = r13.mediaMuxer     // Catch:{ Exception -> 0x0acd, all -> 0x0acb }
            android.media.MediaFormat r12 = r4.format     // Catch:{ Exception -> 0x0acd, all -> 0x0acb }
            r15 = 1
            int r5 = r5.addTrack(r12, r15)     // Catch:{ Exception -> 0x0acd, all -> 0x0acb }
            r2 = r5
            r12 = 0
            r15 = 0
            r5 = r88
            goto L_0x0b23
        L_0x0acb:
            r0 = move-exception
            goto L_0x0af6
        L_0x0acd:
            r0 = move-exception
            goto L_0x0ad2
        L_0x0acf:
            r0 = move-exception
            r2 = r86
        L_0x0ad2:
            r14 = r81
            r47 = r82
            r21 = r2
            r29 = r4
            r53 = r8
            r1 = r23
            r27 = r42
            r13 = -5
            goto L_0x0b15
        L_0x0ae2:
            r0 = move-exception
            goto L_0x0af4
        L_0x0ae4:
            r0 = move-exception
            goto L_0x0b04
        L_0x0ae6:
            r6 = r84
            r2 = r86
            r88 = r4
            r5 = r88
            r2 = -5
            r4 = 0
            goto L_0x0b21
        L_0x0af1:
            r0 = move-exception
            r6 = r84
        L_0x0af4:
            r2 = r86
        L_0x0af6:
            r5 = r79
            r10 = r80
            r14 = r81
            r21 = r2
        L_0x0afe:
            r15 = r13
            goto L_0x09cb
        L_0x0b01:
            r0 = move-exception
            r6 = r84
        L_0x0b04:
            r2 = r86
            r14 = r81
            r47 = r82
            r21 = r2
            r53 = r8
            r1 = r23
            r27 = r42
            r13 = -5
            r29 = 0
        L_0x0b15:
            r2 = r0
            goto L_0x13e4
        L_0x0b18:
            r13 = r72
            r6 = r84
            r2 = r86
            r2 = -5
            r4 = 0
            r5 = 1
        L_0x0b21:
            r12 = 0
            r15 = 0
        L_0x0b23:
            if (r11 >= 0) goto L_0x0b27
            r3 = 1
            goto L_0x0b28
        L_0x0b27:
            r3 = 0
        L_0x0b28:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x12de, all -> 0x1323 }
            r34 = r9
            r39 = r15
            r64 = r18
            r66 = r64
            r56 = r21
            r27 = r42
            r9 = 0
            r35 = -5
            r37 = 0
            r38 = 1
            r40 = 0
            r49 = 0
            r21 = r86
            r15 = r12
            r86 = 0
            r12 = 0
        L_0x0b48:
            if (r9 == 0) goto L_0x0b5f
            if (r5 != 0) goto L_0x0b4f
            if (r3 != 0) goto L_0x0b4f
            goto L_0x0b5f
        L_0x0b4f:
            r5 = r79
            r14 = r81
            r47 = r82
            r29 = r4
            r1 = r10
            r15 = r13
            r6 = 0
            r13 = 0
            r4 = r80
            goto L_0x142e
        L_0x0b5f:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x12c9, all -> 0x12b9 }
            if (r5 != 0) goto L_0x0b79
            if (r4 == 0) goto L_0x0b79
            org.telegram.messenger.video.MP4Builder r3 = r13.mediaMuxer     // Catch:{ Exception -> 0x0b6d, all -> 0x0cd1 }
            boolean r3 = r4.step(r3, r2)     // Catch:{ Exception -> 0x0b6d, all -> 0x0cd1 }
            goto L_0x0b79
        L_0x0b6d:
            r0 = move-exception
            r14 = r81
            r47 = r82
            r2 = r0
            r29 = r4
            r53 = r8
            goto L_0x12d8
        L_0x0b79:
            if (r37 != 0) goto L_0x0ce7
            r87 = r3
            android.media.MediaExtractor r3 = r13.extractor     // Catch:{ Exception -> 0x0cdc, all -> 0x0cd1 }
            int r3 = r3.getSampleTrackIndex()     // Catch:{ Exception -> 0x0cdc, all -> 0x0cd1 }
            r88 = r4
            r4 = r54
            if (r3 != r4) goto L_0x0bee
            r53 = r8
            r89 = r9
            r8 = 2500(0x9c4, double:1.235E-320)
            int r3 = r10.dequeueInputBuffer(r8)     // Catch:{ Exception -> 0x0be2, all -> 0x0cd1 }
            if (r3 < 0) goto L_0x0bd6
            int r8 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0be2, all -> 0x0cd1 }
            r9 = 21
            if (r8 >= r9) goto L_0x0b9e
            r8 = r1[r3]     // Catch:{ Exception -> 0x0be2, all -> 0x0cd1 }
            goto L_0x0ba2
        L_0x0b9e:
            java.nio.ByteBuffer r8 = r10.getInputBuffer(r3)     // Catch:{ Exception -> 0x0be2, all -> 0x0cd1 }
        L_0x0ba2:
            android.media.MediaExtractor r9 = r13.extractor     // Catch:{ Exception -> 0x0be2, all -> 0x0cd1 }
            r51 = r1
            r1 = 0
            int r45 = r9.readSampleData(r8, r1)     // Catch:{ Exception -> 0x0be2, all -> 0x0cd1 }
            if (r45 >= 0) goto L_0x0bbf
            r44 = 0
            r45 = 0
            r46 = 0
            r48 = 4
            r42 = r10
            r43 = r3
            r42.queueInputBuffer(r43, r44, r45, r46, r48)     // Catch:{ Exception -> 0x0be2, all -> 0x0cd1 }
            r37 = 1
            goto L_0x0bd8
        L_0x0bbf:
            r44 = 0
            android.media.MediaExtractor r1 = r13.extractor     // Catch:{ Exception -> 0x0be2, all -> 0x0cd1 }
            long r46 = r1.getSampleTime()     // Catch:{ Exception -> 0x0be2, all -> 0x0cd1 }
            r48 = 0
            r42 = r10
            r43 = r3
            r42.queueInputBuffer(r43, r44, r45, r46, r48)     // Catch:{ Exception -> 0x0be2, all -> 0x0cd1 }
            android.media.MediaExtractor r1 = r13.extractor     // Catch:{ Exception -> 0x0be2, all -> 0x0cd1 }
            r1.advance()     // Catch:{ Exception -> 0x0be2, all -> 0x0cd1 }
            goto L_0x0bd8
        L_0x0bd6:
            r51 = r1
        L_0x0bd8:
            r60 = r2
            r54 = r4
            r62 = r5
            r9 = r63
            goto L_0x0cb4
        L_0x0be2:
            r0 = move-exception
            r14 = r81
            r47 = r82
            r29 = r88
            r2 = r0
            r54 = r4
            goto L_0x12d8
        L_0x0bee:
            r51 = r1
            r53 = r8
            r89 = r9
            if (r5 == 0) goto L_0x0ca7
            r1 = -1
            if (r11 == r1) goto L_0x0c9e
            if (r3 != r11) goto L_0x0ca7
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c9a, all -> 0x0cd1 }
            r3 = 28
            if (r1 < r3) goto L_0x0CLASSNAME
            android.media.MediaExtractor r3 = r13.extractor     // Catch:{ Exception -> 0x0c9a, all -> 0x0cd1 }
            long r8 = r3.getSampleSize()     // Catch:{ Exception -> 0x0c9a, all -> 0x0cd1 }
            r54 = r4
            long r3 = (long) r15
            int r42 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r42 <= 0) goto L_0x0CLASSNAME
            r3 = 1024(0x400, double:5.06E-321)
            long r8 = r8 + r3
            int r15 = (int) r8
            java.nio.ByteBuffer r39 = java.nio.ByteBuffer.allocateDirect(r15)     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r54 = r4
        L_0x0CLASSNAME:
            r3 = r39
            android.media.MediaExtractor r4 = r13.extractor     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            r8 = 0
            int r4 = r4.readSampleData(r3, r8)     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            r9 = r63
            r9.size = r4     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            r4 = 21
            if (r1 >= r4) goto L_0x0CLASSNAME
            r3.position(r8)     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            int r1 = r9.size     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            r3.limit(r1)     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
        L_0x0CLASSNAME:
            int r1 = r9.size     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            if (r1 < 0) goto L_0x0CLASSNAME
            android.media.MediaExtractor r1 = r13.extractor     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            r8 = r5
            long r4 = r1.getSampleTime()     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            r9.presentationTimeUs = r4     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            android.media.MediaExtractor r1 = r13.extractor     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            r1.advance()     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            goto L_0x0c4b
        L_0x0CLASSNAME:
            r8 = r5
            r1 = 0
            r9.size = r1     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            r37 = 1
        L_0x0c4b:
            int r1 = r9.size     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            if (r1 <= 0) goto L_0x0CLASSNAME
            r4 = 0
            int r1 = (r21 > r4 ? 1 : (r21 == r4 ? 0 : -1))
            if (r1 < 0) goto L_0x0c5b
            long r4 = r9.presentationTimeUs     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            int r1 = (r4 > r21 ? 1 : (r4 == r21 ? 0 : -1))
            if (r1 >= 0) goto L_0x0CLASSNAME
        L_0x0c5b:
            r1 = 0
            r9.offset = r1     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            android.media.MediaExtractor r4 = r13.extractor     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            int r4 = r4.getSampleFlags()     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            r9.flags = r4     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            org.telegram.messenger.video.MP4Builder r4 = r13.mediaMuxer     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            long r4 = r4.writeSampleData(r2, r3, r9, r1)     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            r30 = 0
            int r1 = (r4 > r30 ? 1 : (r4 == r30 ? 0 : -1))
            if (r1 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r13.callback     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            if (r1 == 0) goto L_0x0CLASSNAME
            r60 = r2
            r39 = r3
            long r2 = r9.presentationTimeUs     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            long r42 = r2 - r6
            int r44 = (r42 > r40 ? 1 : (r42 == r40 ? 0 : -1))
            if (r44 <= 0) goto L_0x0CLASSNAME
            long r40 = r2 - r6
        L_0x0CLASSNAME:
            r2 = r40
            r62 = r8
            float r8 = (float) r2     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            float r8 = r8 / r24
            float r8 = r8 / r25
            r1.didWriteData(r4, r8)     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            r40 = r2
            goto L_0x0cb4
        L_0x0CLASSNAME:
            r60 = r2
            r39 = r3
            r62 = r8
            goto L_0x0cb4
        L_0x0c9a:
            r0 = move-exception
            r54 = r4
            goto L_0x0ce1
        L_0x0c9e:
            r60 = r2
            r54 = r4
            r62 = r5
            r9 = r63
            goto L_0x0cb0
        L_0x0ca7:
            r60 = r2
            r54 = r4
            r62 = r5
            r9 = r63
            r1 = -1
        L_0x0cb0:
            if (r3 != r1) goto L_0x0cb4
            r1 = 1
            goto L_0x0cb5
        L_0x0cb4:
            r1 = 0
        L_0x0cb5:
            if (r1 == 0) goto L_0x0cf7
            r1 = 2500(0x9c4, double:1.235E-320)
            int r43 = r10.dequeueInputBuffer(r1)     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            if (r43 < 0) goto L_0x0cf7
            r44 = 0
            r45 = 0
            r46 = 0
            r48 = 4
            r42 = r10
            r42.queueInputBuffer(r43, r44, r45, r46, r48)     // Catch:{ Exception -> 0x0ccf, all -> 0x0cd1 }
            r37 = 1
            goto L_0x0cf7
        L_0x0ccf:
            r0 = move-exception
            goto L_0x0ce1
        L_0x0cd1:
            r0 = move-exception
        L_0x0cd2:
            r5 = r79
            r10 = r80
            r14 = r81
            r2 = r0
            r15 = r13
            goto L_0x12c3
        L_0x0cdc:
            r0 = move-exception
            r88 = r4
            r53 = r8
        L_0x0ce1:
            r14 = r81
            r47 = r82
            goto L_0x12d5
        L_0x0ce7:
            r51 = r1
            r60 = r2
            r87 = r3
            r88 = r4
            r62 = r5
            r53 = r8
            r89 = r9
            r9 = r63
        L_0x0cf7:
            r1 = r12 ^ 1
            r3 = r89
            r2 = r1
            r5 = r35
            r68 = r56
            r4 = 1
            r1 = r86
        L_0x0d03:
            if (r2 != 0) goto L_0x0d1f
            if (r4 == 0) goto L_0x0d08
            goto L_0x0d1f
        L_0x0d08:
            r4 = r88
            r86 = r1
            r35 = r5
            r63 = r9
            r1 = r51
            r8 = r53
            r2 = r60
            r5 = r62
            r56 = r68
            r9 = r3
            r3 = r87
            goto L_0x0b48
        L_0x0d1f:
            r72.checkConversionCanceled()     // Catch:{ Exception -> 0x12a9, all -> 0x1299 }
            if (r93 == 0) goto L_0x0d2f
            r42 = 22000(0x55f0, double:1.08694E-319)
            r8 = r82
            r86 = r2
            r89 = r3
            r2 = r42
            goto L_0x0d37
        L_0x0d2f:
            r8 = r82
            r86 = r2
            r89 = r3
            r2 = 2500(0x9c4, double:1.235E-320)
        L_0x0d37:
            int r2 = r8.dequeueOutputBuffer(r9, r2)     // Catch:{ Exception -> 0x1293, all -> 0x1299 }
            r3 = -1
            if (r2 != r3) goto L_0x0d58
            r82 = r1
            r45 = r10
            r44 = r11
            r10 = r26
            r3 = r55
            r4 = r61
            r1 = -1
            r11 = r2
            r26 = r5
            r2 = r58
            r5 = r89
            r89 = r15
            r15 = r12
            r12 = 0
            goto L_0x0var_
        L_0x0d58:
            r3 = -3
            if (r2 != r3) goto L_0x0d9a
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0d8f, all -> 0x0d82 }
            r82 = r4
            r4 = 21
            if (r3 >= r4) goto L_0x0d67
            java.nio.ByteBuffer[] r34 = r8.getOutputBuffers()     // Catch:{ Exception -> 0x0d8f, all -> 0x0d82 }
        L_0x0d67:
            r45 = r10
            r44 = r11
            r10 = r26
            r3 = r55
            r4 = r61
        L_0x0d71:
            r11 = r2
            r26 = r5
            r2 = r58
            r5 = r89
            r89 = r15
            r15 = r12
            r12 = r82
            r82 = r1
        L_0x0d7f:
            r1 = -1
            goto L_0x0var_
        L_0x0d82:
            r0 = move-exception
            r10 = r80
            r14 = r81
            r2 = r0
            r15 = r13
            r1 = r23
            r6 = 0
            r13 = r5
            goto L_0x12a5
        L_0x0d8f:
            r0 = move-exception
        L_0x0d90:
            r14 = r81
            r29 = r88
            r2 = r0
            r13 = r5
            r47 = r8
            goto L_0x12b5
        L_0x0d9a:
            r82 = r4
            r3 = -2
            if (r2 != r3) goto L_0x0e00
            android.media.MediaFormat r3 = r8.getOutputFormat()     // Catch:{ Exception -> 0x0d8f, all -> 0x0d82 }
            r4 = -5
            if (r5 != r4) goto L_0x0df0
            if (r3 == 0) goto L_0x0df0
            org.telegram.messenger.video.MP4Builder r4 = r13.mediaMuxer     // Catch:{ Exception -> 0x0d8f, all -> 0x0d82 }
            r44 = r11
            r11 = 0
            int r4 = r4.addTrack(r3, r11)     // Catch:{ Exception -> 0x0d8f, all -> 0x0d82 }
            r11 = r59
            boolean r5 = r3.containsKey(r11)     // Catch:{ Exception -> 0x0de4, all -> 0x0ddf }
            if (r5 == 0) goto L_0x0dd8
            int r5 = r3.getInteger(r11)     // Catch:{ Exception -> 0x0de4, all -> 0x0ddf }
            r35 = r4
            r4 = 1
            if (r5 != r4) goto L_0x0dda
            r4 = r61
            java.nio.ByteBuffer r1 = r3.getByteBuffer(r4)     // Catch:{ Exception -> 0x0dd6, all -> 0x0cd1 }
            java.nio.ByteBuffer r3 = r3.getByteBuffer(r14)     // Catch:{ Exception -> 0x0dd6, all -> 0x0cd1 }
            int r1 = r1.limit()     // Catch:{ Exception -> 0x0dd6, all -> 0x0cd1 }
            int r3 = r3.limit()     // Catch:{ Exception -> 0x0dd6, all -> 0x0cd1 }
            int r1 = r1 + r3
            goto L_0x0ddc
        L_0x0dd6:
            r0 = move-exception
            goto L_0x0de7
        L_0x0dd8:
            r35 = r4
        L_0x0dda:
            r4 = r61
        L_0x0ddc:
            r5 = r35
            goto L_0x0df6
        L_0x0ddf:
            r0 = move-exception
            r35 = r4
            goto L_0x0cd2
        L_0x0de4:
            r0 = move-exception
            r35 = r4
        L_0x0de7:
            r14 = r81
            r29 = r88
            r2 = r0
            r47 = r8
            goto L_0x12d8
        L_0x0df0:
            r44 = r11
            r11 = r59
            r4 = r61
        L_0x0df6:
            r45 = r10
            r59 = r11
            r10 = r26
            r3 = r55
            goto L_0x0d71
        L_0x0e00:
            r44 = r11
            r11 = r59
            r4 = r61
            if (r2 < 0) goto L_0x1269
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1293, all -> 0x1299 }
            r59 = r11
            r11 = 21
            if (r3 >= r11) goto L_0x0e13
            r3 = r34[r2]     // Catch:{ Exception -> 0x0d8f, all -> 0x0d82 }
            goto L_0x0e17
        L_0x0e13:
            java.nio.ByteBuffer r3 = r8.getOutputBuffer(r2)     // Catch:{ Exception -> 0x1293, all -> 0x1299 }
        L_0x0e17:
            if (r3 == 0) goto L_0x1245
            int r11 = r9.size     // Catch:{ Exception -> 0x123d, all -> 0x1299 }
            r89 = r15
            r15 = 1
            if (r11 <= r15) goto L_0x0var_
            int r15 = r9.flags     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r35 = r15 & 2
            if (r35 != 0) goto L_0x0ec7
            if (r1 == 0) goto L_0x0e37
            r35 = r15 & 1
            if (r35 == 0) goto L_0x0e37
            r45 = r10
            int r10 = r9.offset     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            int r10 = r10 + r1
            r9.offset = r10     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            int r11 = r11 - r1
            r9.size = r11     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            goto L_0x0e39
        L_0x0e37:
            r45 = r10
        L_0x0e39:
            if (r38 == 0) goto L_0x0e8f
            r10 = r15 & 1
            if (r10 == 0) goto L_0x0e8f
            int r10 = r9.size     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r15 = 100
            if (r10 <= r15) goto L_0x0e8a
            int r10 = r9.offset     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r3.position(r10)     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            byte[] r10 = new byte[r15]     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r3.get(r10)     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r11 = 0
            r35 = 0
        L_0x0e52:
            r15 = 96
            if (r11 >= r15) goto L_0x0e8a
            byte r15 = r10[r11]     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            if (r15 != 0) goto L_0x0e81
            int r15 = r11 + 1
            byte r15 = r10[r15]     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            if (r15 != 0) goto L_0x0e81
            int r15 = r11 + 2
            byte r15 = r10[r15]     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            if (r15 != 0) goto L_0x0e81
            int r15 = r11 + 3
            byte r15 = r10[r15]     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r42 = r1
            r1 = 1
            if (r15 != r1) goto L_0x0e83
            int r15 = r35 + 1
            if (r15 <= r1) goto L_0x0e7e
            int r1 = r9.offset     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            int r1 = r1 + r11
            r9.offset = r1     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            int r1 = r9.size     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            int r1 = r1 - r11
            r9.size = r1     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            goto L_0x0e8c
        L_0x0e7e:
            r35 = r15
            goto L_0x0e83
        L_0x0e81:
            r42 = r1
        L_0x0e83:
            int r11 = r11 + 1
            r1 = r42
            r15 = 100
            goto L_0x0e52
        L_0x0e8a:
            r42 = r1
        L_0x0e8c:
            r38 = 0
            goto L_0x0e91
        L_0x0e8f:
            r42 = r1
        L_0x0e91:
            org.telegram.messenger.video.MP4Builder r1 = r13.mediaMuxer     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r15 = r12
            r10 = 1
            long r11 = r1.writeSampleData(r5, r3, r9, r10)     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r30 = 0
            int r1 = (r11 > r30 ? 1 : (r11 == r30 ? 0 : -1))
            if (r1 == 0) goto L_0x0ebd
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r13.callback     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            if (r1 == 0) goto L_0x0ebd
            r10 = r2
            long r2 = r9.presentationTimeUs     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            long r46 = r2 - r6
            int r35 = (r46 > r40 ? 1 : (r46 == r40 ? 0 : -1))
            if (r35 <= 0) goto L_0x0eae
            long r40 = r2 - r6
        L_0x0eae:
            r2 = r40
            r35 = r10
            float r10 = (float) r2     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            float r10 = r10 / r24
            float r10 = r10 / r25
            r1.didWriteData(r11, r10)     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r40 = r2
            goto L_0x0ebf
        L_0x0ebd:
            r35 = r2
        L_0x0ebf:
            r10 = r26
            r3 = r55
            r2 = r58
            goto L_0x0f5f
        L_0x0ec7:
            r42 = r1
            r35 = r2
            r45 = r10
            r15 = r12
            r1 = -5
            if (r5 != r1) goto L_0x0ebf
            byte[] r1 = new byte[r11]     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            int r2 = r9.offset     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            int r2 = r2 + r11
            r3.limit(r2)     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            int r2 = r9.offset     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r3.position(r2)     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r3.get(r1)     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            int r2 = r9.size     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r3 = 1
            int r2 = r2 - r3
        L_0x0ee5:
            if (r2 < 0) goto L_0x0var_
            r10 = 3
            if (r2 <= r10) goto L_0x0var_
            byte r11 = r1[r2]     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            if (r11 != r3) goto L_0x0f1f
            int r11 = r2 + -1
            byte r11 = r1[r11]     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            if (r11 != 0) goto L_0x0f1f
            int r11 = r2 + -2
            byte r11 = r1[r11]     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            if (r11 != 0) goto L_0x0f1f
            int r11 = r2 + -3
            byte r12 = r1[r11]     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            if (r12 != 0) goto L_0x0f1f
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r11)     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            int r12 = r9.size     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            int r12 = r12 - r11
            java.nio.ByteBuffer r12 = java.nio.ByteBuffer.allocate(r12)     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r3 = 0
            java.nio.ByteBuffer r10 = r2.put(r1, r3, r11)     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r10.position(r3)     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            int r10 = r9.size     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            int r10 = r10 - r11
            java.nio.ByteBuffer r1 = r12.put(r1, r11, r10)     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r1.position(r3)     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r1 = r2
            goto L_0x0var_
        L_0x0f1f:
            int r2 = r2 + -1
            r3 = 1
            goto L_0x0ee5
        L_0x0var_:
            r1 = 0
            r12 = 0
        L_0x0var_:
            r10 = r26
            r3 = r55
            r2 = r58
            android.media.MediaFormat r11 = android.media.MediaFormat.createVideoFormat(r10, r2, r3)     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            if (r1 == 0) goto L_0x0var_
            if (r12 == 0) goto L_0x0var_
            r11.setByteBuffer(r4, r1)     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r11.setByteBuffer(r14, r12)     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
        L_0x0var_:
            org.telegram.messenger.video.MP4Builder r1 = r13.mediaMuxer     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r12 = 0
            int r1 = r1.addTrack(r11, r12)     // Catch:{ Exception -> 0x0var_, all -> 0x0d82 }
            r5 = r1
            goto L_0x0f5f
        L_0x0var_:
            r0 = move-exception
            r14 = r81
            r29 = r88
            r2 = r0
            r13 = r5
            r47 = r8
            r1 = r23
            r10 = r45
            goto L_0x13e4
        L_0x0var_:
            r0 = move-exception
            r45 = r10
            goto L_0x0d90
        L_0x0var_:
            r42 = r1
            r35 = r2
            r45 = r10
            r15 = r12
            goto L_0x0ebf
        L_0x0f5f:
            int r1 = r9.flags     // Catch:{ Exception -> 0x122d, all -> 0x1299 }
            r1 = r1 & 4
            r11 = r35
            if (r1 == 0) goto L_0x0var_
            r1 = 1
            goto L_0x0f6a
        L_0x0var_:
            r1 = 0
        L_0x0f6a:
            r12 = 0
            r8.releaseOutputBuffer(r11, r12)     // Catch:{ Exception -> 0x122d, all -> 0x1299 }
            r12 = r82
            r26 = r5
            r82 = r42
            r5 = r1
            goto L_0x0d7f
        L_0x0var_:
            if (r11 == r1) goto L_0x0var_
            r1 = r82
            r58 = r2
            r55 = r3
            r61 = r4
            r3 = r5
            r82 = r8
            r4 = r12
            r12 = r15
            r5 = r26
            r11 = r44
            r2 = r86
            r15 = r89
            r26 = r10
            r10 = r45
            goto L_0x0d03
        L_0x0var_:
            if (r15 != 0) goto L_0x11f3
            r58 = r2
            r55 = r3
            r11 = r45
            r1 = 2500(0x9c4, double:1.235E-320)
            int r3 = r11.dequeueOutputBuffer(r9, r1)     // Catch:{ Exception -> 0x11e2, all -> 0x11d2 }
            r1 = -1
            if (r3 != r1) goto L_0x0fba
            r61 = r4
            r46 = r5
            r47 = r8
            r42 = r12
            r45 = r14
            r12 = r52
            r2 = 0
            r7 = 0
            r20 = -5
            r14 = r81
            goto L_0x1211
        L_0x0fba:
            r2 = -3
            if (r3 != r2) goto L_0x0fd1
        L_0x0fbd:
            r61 = r4
            r46 = r5
            r47 = r8
            r42 = r12
            r45 = r14
            r12 = r52
            r4 = r68
            r7 = 0
            r20 = -5
            goto L_0x120b
        L_0x0fd1:
            r2 = -2
            if (r3 != r2) goto L_0x1006
            android.media.MediaFormat r2 = r11.getOutputFormat()     // Catch:{ Exception -> 0x0ffc, all -> 0x0ff1 }
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0ffc, all -> 0x0ff1 }
            if (r3 == 0) goto L_0x0fbd
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ffc, all -> 0x0ff1 }
            r3.<init>()     // Catch:{ Exception -> 0x0ffc, all -> 0x0ff1 }
            java.lang.String r1 = "newFormat = "
            r3.append(r1)     // Catch:{ Exception -> 0x0ffc, all -> 0x0ff1 }
            r3.append(r2)     // Catch:{ Exception -> 0x0ffc, all -> 0x0ff1 }
            java.lang.String r1 = r3.toString()     // Catch:{ Exception -> 0x0ffc, all -> 0x0ff1 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0ffc, all -> 0x0ff1 }
            goto L_0x0fbd
        L_0x0ff1:
            r0 = move-exception
            r5 = r79
            r10 = r80
            r14 = r81
        L_0x0ff8:
            r2 = r0
            r15 = r13
            goto L_0x11dc
        L_0x0ffc:
            r0 = move-exception
            r14 = r81
        L_0x0fff:
            r29 = r88
            r2 = r0
            r47 = r8
            goto L_0x11ec
        L_0x1006:
            if (r3 < 0) goto L_0x11ab
            int r1 = r9.size     // Catch:{ Exception -> 0x11e2, all -> 0x11d2 }
            if (r1 == 0) goto L_0x100f
            r35 = 1
            goto L_0x1011
        L_0x100f:
            r35 = 0
        L_0x1011:
            long r1 = r9.presentationTimeUs     // Catch:{ Exception -> 0x11e2, all -> 0x11d2 }
            r30 = 0
            int r42 = (r21 > r30 ? 1 : (r21 == r30 ? 0 : -1))
            if (r42 <= 0) goto L_0x102b
            int r42 = (r1 > r21 ? 1 : (r1 == r21 ? 0 : -1))
            if (r42 < 0) goto L_0x102b
            int r15 = r9.flags     // Catch:{ Exception -> 0x0ffc, all -> 0x0ff1 }
            r15 = r15 | 4
            r9.flags = r15     // Catch:{ Exception -> 0x0ffc, all -> 0x0ff1 }
            r15 = 1
            r30 = 0
            r35 = 0
            r37 = 1
            goto L_0x102d
        L_0x102b:
            r30 = 0
        L_0x102d:
            int r42 = (r27 > r30 ? 1 : (r27 == r30 ? 0 : -1))
            if (r42 < 0) goto L_0x10ba
            r61 = r4
            int r4 = r9.flags     // Catch:{ Exception -> 0x0ffc, all -> 0x10b0 }
            r4 = r4 & 4
            if (r4 == 0) goto L_0x10bc
            long r42 = r27 - r6
            long r42 = java.lang.Math.abs(r42)     // Catch:{ Exception -> 0x0ffc, all -> 0x10b0 }
            r4 = 1000000(0xvar_, float:1.401298E-39)
            r45 = r14
            r14 = r81
            int r4 = r4 / r14
            r46 = r5
            long r4 = (long) r4
            int r47 = (r42 > r4 ? 1 : (r42 == r4 ? 0 : -1))
            if (r47 <= 0) goto L_0x10a5
            r4 = 0
            int r15 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r15 <= 0) goto L_0x105e
            android.media.MediaExtractor r4 = r13.extractor     // Catch:{ Exception -> 0x10ad, all -> 0x10e6 }
            r5 = 0
            r4.seekTo(r6, r5)     // Catch:{ Exception -> 0x10ad, all -> 0x10e6 }
            r4 = r68
            r7 = 0
            goto L_0x1068
        L_0x105e:
            android.media.MediaExtractor r4 = r13.extractor     // Catch:{ Exception -> 0x10ad, all -> 0x10ab }
            r5 = 0
            r7 = 0
            r4.seekTo(r5, r7)     // Catch:{ Exception -> 0x10ad, all -> 0x10e6 }
            r4 = r68
        L_0x1068:
            long r49 = r4 + r32
            int r6 = r9.flags     // Catch:{ Exception -> 0x1094, all -> 0x1082 }
            r20 = -5
            r6 = r6 & -5
            r9.flags = r6     // Catch:{ Exception -> 0x1094, all -> 0x1082 }
            r11.flush()     // Catch:{ Exception -> 0x1094, all -> 0x1082 }
            r21 = r27
            r6 = 0
            r15 = 0
            r30 = 0
            r35 = 0
            r36 = 1
            r27 = r18
            goto L_0x10cd
        L_0x1082:
            r0 = move-exception
            r5 = r79
            r10 = r80
            r2 = r0
            r15 = r13
            r1 = r23
            r13 = r26
            r21 = r27
            r6 = 0
            r27 = r18
            goto L_0x14e8
        L_0x1094:
            r0 = move-exception
            r29 = r88
            r2 = r0
            r47 = r8
            r10 = r11
            r1 = r23
            r13 = r26
            r21 = r27
            r27 = r18
            goto L_0x13e4
        L_0x10a5:
            r4 = r68
            r7 = 0
            r20 = -5
            goto L_0x10c7
        L_0x10ab:
            r0 = move-exception
            goto L_0x10b3
        L_0x10ad:
            r0 = move-exception
            goto L_0x0fff
        L_0x10b0:
            r0 = move-exception
            r14 = r81
        L_0x10b3:
            r7 = 0
        L_0x10b4:
            r5 = r79
            r10 = r80
            goto L_0x0ff8
        L_0x10ba:
            r61 = r4
        L_0x10bc:
            r46 = r5
            r45 = r14
            r4 = r68
            r7 = 0
            r20 = -5
            r14 = r81
        L_0x10c7:
            r6 = r37
            r30 = 0
            r36 = 0
        L_0x10cd:
            int r37 = (r66 > r30 ? 1 : (r66 == r30 ? 0 : -1))
            if (r37 <= 0) goto L_0x10eb
            r47 = r8
            long r7 = r9.presentationTimeUs     // Catch:{ Exception -> 0x10e8, all -> 0x10e6 }
            long r7 = r7 - r66
            int r37 = (r7 > r32 ? 1 : (r7 == r32 ? 0 : -1))
            if (r37 >= 0) goto L_0x10ed
            int r7 = r9.flags     // Catch:{ Exception -> 0x10e8, all -> 0x10e6 }
            r7 = r7 & 4
            if (r7 != 0) goto L_0x10ed
            r7 = 0
            r35 = 0
            goto L_0x10ef
        L_0x10e6:
            r0 = move-exception
            goto L_0x10b4
        L_0x10e8:
            r0 = move-exception
            goto L_0x11e9
        L_0x10eb:
            r47 = r8
        L_0x10ed:
            r7 = 0
        L_0x10ef:
            int r30 = (r27 > r7 ? 1 : (r27 == r7 ? 0 : -1))
            r42 = r12
            if (r30 < 0) goto L_0x10f8
            r12 = r27
            goto L_0x10fa
        L_0x10f8:
            r12 = r84
        L_0x10fa:
            int r37 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r37 <= 0) goto L_0x1137
            int r7 = (r64 > r18 ? 1 : (r64 == r18 ? 0 : -1))
            if (r7 != 0) goto L_0x1137
            int r7 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r7 >= 0) goto L_0x112a
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x10e8, all -> 0x11c8 }
            if (r1 == 0) goto L_0x1128
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x10e8, all -> 0x11c8 }
            r1.<init>()     // Catch:{ Exception -> 0x10e8, all -> 0x11c8 }
            java.lang.String r2 = "drop frame startTime = "
            r1.append(r2)     // Catch:{ Exception -> 0x10e8, all -> 0x11c8 }
            r1.append(r12)     // Catch:{ Exception -> 0x10e8, all -> 0x11c8 }
            java.lang.String r2 = " present time = "
            r1.append(r2)     // Catch:{ Exception -> 0x10e8, all -> 0x11c8 }
            long r7 = r9.presentationTimeUs     // Catch:{ Exception -> 0x10e8, all -> 0x11c8 }
            r1.append(r7)     // Catch:{ Exception -> 0x10e8, all -> 0x11c8 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x10e8, all -> 0x11c8 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x10e8, all -> 0x11c8 }
        L_0x1128:
            r1 = 0
            goto L_0x1139
        L_0x112a:
            long r1 = r9.presentationTimeUs     // Catch:{ Exception -> 0x10e8, all -> 0x11c8 }
            r7 = -2147483648(0xfffffffvar_, double:NaN)
            int r12 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r12 == 0) goto L_0x1135
            long r49 = r49 - r1
        L_0x1135:
            r64 = r1
        L_0x1137:
            r1 = r35
        L_0x1139:
            if (r36 == 0) goto L_0x113e
            r64 = r18
            goto L_0x1151
        L_0x113e:
            int r2 = (r27 > r18 ? 1 : (r27 == r18 ? 0 : -1))
            if (r2 != 0) goto L_0x114e
            r7 = 0
            int r2 = (r49 > r7 ? 1 : (r49 == r7 ? 0 : -1))
            if (r2 == 0) goto L_0x114e
            long r7 = r9.presentationTimeUs     // Catch:{ Exception -> 0x10e8, all -> 0x11c8 }
            long r7 = r7 + r49
            r9.presentationTimeUs = r7     // Catch:{ Exception -> 0x10e8, all -> 0x11c8 }
        L_0x114e:
            r11.releaseOutputBuffer(r3, r1)     // Catch:{ Exception -> 0x11a9, all -> 0x11c8 }
        L_0x1151:
            if (r1 == 0) goto L_0x1186
            long r1 = r9.presentationTimeUs     // Catch:{ Exception -> 0x11a9, all -> 0x11c8 }
            r7 = 0
            int r3 = (r27 > r7 ? 1 : (r27 == r7 ? 0 : -1))
            if (r3 < 0) goto L_0x1160
            long r68 = java.lang.Math.max(r4, r1)     // Catch:{ Exception -> 0x10e8, all -> 0x11c8 }
            goto L_0x1162
        L_0x1160:
            r68 = r4
        L_0x1162:
            r53.awaitNewImage()     // Catch:{ Exception -> 0x1167, all -> 0x11c8 }
            r13 = 0
            goto L_0x116d
        L_0x1167:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)     // Catch:{ Exception -> 0x11a9, all -> 0x11c8 }
            r13 = 1
        L_0x116d:
            if (r13 != 0) goto L_0x1181
            r53.drawImage()     // Catch:{ Exception -> 0x11a9, all -> 0x11c8 }
            long r3 = r9.presentationTimeUs     // Catch:{ Exception -> 0x11a9, all -> 0x11c8 }
            r12 = 1000(0x3e8, double:4.94E-321)
            long r3 = r3 * r12
            r12 = r52
            r12.setPresentationTime(r3)     // Catch:{ Exception -> 0x11ca, all -> 0x11c8 }
            r12.swapBuffers()     // Catch:{ Exception -> 0x11ca, all -> 0x11c8 }
            goto L_0x1183
        L_0x1181:
            r12 = r52
        L_0x1183:
            r66 = r1
            goto L_0x118c
        L_0x1186:
            r12 = r52
            r7 = 0
            r68 = r4
        L_0x118c:
            int r1 = r9.flags     // Catch:{ Exception -> 0x11ca, all -> 0x11c8 }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x11a3
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x11ca, all -> 0x11c8 }
            if (r1 == 0) goto L_0x119b
            java.lang.String r1 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x11ca, all -> 0x11c8 }
        L_0x119b:
            r47.signalEndOfInputStream()     // Catch:{ Exception -> 0x11ca, all -> 0x11c8 }
            r37 = r6
            r2 = 0
            goto L_0x1211
        L_0x11a3:
            r2 = r86
            r37 = r6
            goto L_0x1211
        L_0x11a9:
            r0 = move-exception
            goto L_0x11e7
        L_0x11ab:
            r14 = r81
            r47 = r8
            r12 = r52
            java.lang.RuntimeException r1 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x11ca, all -> 0x11c8 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x11ca, all -> 0x11c8 }
            r2.<init>()     // Catch:{ Exception -> 0x11ca, all -> 0x11c8 }
            java.lang.String r4 = "unexpected result from decoder.dequeueOutputBuffer: "
            r2.append(r4)     // Catch:{ Exception -> 0x11ca, all -> 0x11c8 }
            r2.append(r3)     // Catch:{ Exception -> 0x11ca, all -> 0x11c8 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x11ca, all -> 0x11c8 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x11ca, all -> 0x11c8 }
            throw r1     // Catch:{ Exception -> 0x11ca, all -> 0x11c8 }
        L_0x11c8:
            r0 = move-exception
            goto L_0x11d5
        L_0x11ca:
            r0 = move-exception
            r29 = r88
            r2 = r0
            r10 = r11
            r52 = r12
            goto L_0x11ed
        L_0x11d2:
            r0 = move-exception
            r14 = r81
        L_0x11d5:
            r15 = r72
            r5 = r79
            r10 = r80
            r2 = r0
        L_0x11dc:
            r1 = r23
            r13 = r26
            goto L_0x1469
        L_0x11e2:
            r0 = move-exception
            r14 = r81
            r47 = r8
        L_0x11e7:
            r12 = r52
        L_0x11e9:
            r29 = r88
            r2 = r0
        L_0x11ec:
            r10 = r11
        L_0x11ed:
            r1 = r23
            r13 = r26
            goto L_0x13e4
        L_0x11f3:
            r58 = r2
            r55 = r3
            r61 = r4
            r46 = r5
            r47 = r8
            r42 = r12
            r11 = r45
            r12 = r52
            r4 = r68
            r7 = 0
            r20 = -5
            r45 = r14
        L_0x120b:
            r14 = r81
            r2 = r86
            r68 = r4
        L_0x1211:
            r13 = r72
            r1 = r82
            r6 = r84
            r52 = r12
            r12 = r15
            r5 = r26
            r4 = r42
            r14 = r45
            r3 = r46
            r82 = r47
            r15 = r89
            r26 = r10
            r10 = r11
            r11 = r44
            goto L_0x0d03
        L_0x122d:
            r0 = move-exception
            r14 = r81
            r47 = r8
            r11 = r45
            r12 = r52
            r29 = r88
            r2 = r0
            r13 = r5
            r10 = r11
            goto L_0x12b5
        L_0x123d:
            r0 = move-exception
            r14 = r81
            r47 = r8
            r11 = r10
            goto L_0x12af
        L_0x1245:
            r14 = r81
            r11 = r2
            r47 = r8
            r1 = r10
            r12 = r52
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x128a, all -> 0x1288 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x128a, all -> 0x1288 }
            r3.<init>()     // Catch:{ Exception -> 0x128a, all -> 0x1288 }
            java.lang.String r4 = "encoderOutputBuffer "
            r3.append(r4)     // Catch:{ Exception -> 0x128a, all -> 0x1288 }
            r3.append(r11)     // Catch:{ Exception -> 0x128a, all -> 0x1288 }
            java.lang.String r4 = " was null"
            r3.append(r4)     // Catch:{ Exception -> 0x128a, all -> 0x1288 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x128a, all -> 0x1288 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x128a, all -> 0x1288 }
            throw r2     // Catch:{ Exception -> 0x128a, all -> 0x1288 }
        L_0x1269:
            r14 = r81
            r11 = r2
            r47 = r8
            r1 = r10
            r12 = r52
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x128a, all -> 0x1288 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x128a, all -> 0x1288 }
            r3.<init>()     // Catch:{ Exception -> 0x128a, all -> 0x1288 }
            java.lang.String r4 = "unexpected result from encoder.dequeueOutputBuffer: "
            r3.append(r4)     // Catch:{ Exception -> 0x128a, all -> 0x1288 }
            r3.append(r11)     // Catch:{ Exception -> 0x128a, all -> 0x1288 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x128a, all -> 0x1288 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x128a, all -> 0x1288 }
            throw r2     // Catch:{ Exception -> 0x128a, all -> 0x1288 }
        L_0x1288:
            r0 = move-exception
            goto L_0x129c
        L_0x128a:
            r0 = move-exception
            r29 = r88
            r2 = r0
            r10 = r1
            r13 = r5
            r52 = r12
            goto L_0x12b5
        L_0x1293:
            r0 = move-exception
            r14 = r81
            r47 = r8
            goto L_0x12ae
        L_0x1299:
            r0 = move-exception
            r14 = r81
        L_0x129c:
            r15 = r72
            r10 = r80
            r2 = r0
            r13 = r5
            r1 = r23
            r6 = 0
        L_0x12a5:
            r5 = r79
            goto L_0x14e8
        L_0x12a9:
            r0 = move-exception
            r14 = r81
            r47 = r82
        L_0x12ae:
            r1 = r10
        L_0x12af:
            r12 = r52
            r29 = r88
            r2 = r0
            r13 = r5
        L_0x12b5:
            r1 = r23
            goto L_0x13e4
        L_0x12b9:
            r0 = move-exception
            r14 = r81
            r15 = r72
            r5 = r79
            r10 = r80
            r2 = r0
        L_0x12c3:
            r1 = r23
            r13 = r35
            goto L_0x1469
        L_0x12c9:
            r0 = move-exception
            r14 = r81
            r47 = r82
            r88 = r4
            r53 = r8
            r1 = r10
            r12 = r52
        L_0x12d5:
            r29 = r88
            r2 = r0
        L_0x12d8:
            r1 = r23
            r13 = r35
            goto L_0x13e4
        L_0x12de:
            r0 = move-exception
            r14 = r81
            r47 = r82
            r88 = r4
            r53 = r8
            r1 = r10
            r12 = r52
            r20 = -5
            r21 = r86
            r29 = r88
            r2 = r0
            goto L_0x09df
        L_0x12f3:
            r0 = move-exception
            r14 = r81
            r47 = r82
            r53 = r8
            r1 = r10
            r12 = r52
            r20 = -5
            r21 = r86
            r2 = r0
            goto L_0x131a
        L_0x1303:
            r0 = move-exception
            r14 = r81
            r47 = r82
            r53 = r8
            goto L_0x1310
        L_0x130b:
            r0 = move-exception
            r14 = r81
            r47 = r82
        L_0x1310:
            r1 = r49
            r12 = r52
            r20 = -5
            r21 = r86
            r2 = r0
            r10 = r1
        L_0x131a:
            r1 = r23
            r27 = r42
            r13 = -5
            r29 = 0
            goto L_0x13e4
        L_0x1323:
            r0 = move-exception
            r14 = r81
            goto L_0x1384
        L_0x1328:
            r0 = move-exception
            r14 = r81
            r47 = r82
            r1 = r49
            r12 = r52
            r20 = -5
            r21 = r86
            r2 = r0
            r10 = r1
            goto L_0x134a
        L_0x1338:
            r0 = move-exception
            r12 = r82
            r42 = r88
            r47 = r5
            r14 = r13
            r54 = r30
            r20 = -5
            r21 = r86
            r2 = r0
            r10 = r1
            r52 = r12
        L_0x134a:
            r1 = r23
            r27 = r42
            goto L_0x1364
        L_0x134f:
            r0 = move-exception
            r42 = r88
            r12 = r3
            r47 = r5
            r14 = r13
            r54 = r30
            r20 = -5
            r21 = r86
            r2 = r0
            r52 = r12
            r1 = r23
            r27 = r42
            r10 = 0
        L_0x1364:
            r13 = -5
            r29 = 0
            goto L_0x13e2
        L_0x1369:
            r0 = move-exception
            r42 = r88
            r47 = r5
            r14 = r13
            r54 = r30
            r20 = -5
            r21 = r86
            r2 = r0
            r1 = r23
            r27 = r42
            r10 = 0
            r13 = -5
            r29 = 0
            goto L_0x13e0
        L_0x1380:
            r0 = move-exception
            r42 = r88
            r14 = r13
        L_0x1384:
            r20 = -5
            r15 = r72
            r5 = r79
            r10 = r80
            r21 = r86
            r2 = r0
        L_0x138f:
            r1 = r23
            goto L_0x13af
        L_0x1392:
            r0 = move-exception
            r42 = r88
            r14 = r13
            r54 = r30
            r20 = -5
            r21 = r86
            r2 = r0
            r1 = r23
            goto L_0x13be
        L_0x13a0:
            r0 = move-exception
            r42 = r88
            r14 = r13
            r20 = -5
            r15 = r72
            r5 = r79
            r10 = r80
            r21 = r86
            r2 = r0
        L_0x13af:
            r27 = r42
            goto L_0x14e6
        L_0x13b3:
            r0 = move-exception
            r42 = r88
            r14 = r13
            r54 = r30
            r20 = -5
            r21 = r86
            r2 = r0
        L_0x13be:
            r27 = r42
            goto L_0x13da
        L_0x13c1:
            r0 = move-exception
            r14 = r13
            r20 = -5
            r15 = r72
            r5 = r79
            r10 = r80
            goto L_0x14df
        L_0x13cd:
            r0 = move-exception
            r54 = r4
            r14 = r13
            r20 = -5
            r1 = r82
            r21 = r86
            r27 = r88
            r2 = r0
        L_0x13da:
            r10 = 0
            r13 = -5
            r29 = 0
            r47 = 0
        L_0x13e0:
            r52 = 0
        L_0x13e2:
            r53 = 0
        L_0x13e4:
            boolean r3 = r2 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x1460 }
            if (r3 == 0) goto L_0x13ed
            if (r93 != 0) goto L_0x13ed
            r48 = 1
            goto L_0x13ef
        L_0x13ed:
            r48 = 0
        L_0x13ef:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x1456 }
            r3.<init>()     // Catch:{ all -> 0x1456 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x1456 }
            r3.append(r1)     // Catch:{ all -> 0x1456 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x1456 }
            r3.append(r14)     // Catch:{ all -> 0x1456 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x1456 }
            r4 = r80
            r3.append(r4)     // Catch:{ all -> 0x1450 }
            java.lang.String r5 = "x"
            r3.append(r5)     // Catch:{ all -> 0x1450 }
            r5 = r79
            r3.append(r5)     // Catch:{ all -> 0x144c }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x144c }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x144c }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x144c }
            r15 = r72
            r23 = r1
            r1 = r10
            r35 = r13
            r6 = r48
            r8 = r53
            r13 = 1
        L_0x142e:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x1443 }
            r3 = r54
            r2.unselectTrack(r3)     // Catch:{ all -> 0x1443 }
            if (r1 == 0) goto L_0x143d
            r1.stop()     // Catch:{ all -> 0x1443 }
            r1.release()     // Catch:{ all -> 0x1443 }
        L_0x143d:
            r48 = r6
            r6 = r13
            r13 = r35
            goto L_0x1485
        L_0x1443:
            r0 = move-exception
            r2 = r0
            r10 = r4
            r1 = r23
            r13 = r35
            goto L_0x14e8
        L_0x144c:
            r0 = move-exception
            r15 = r72
            goto L_0x145d
        L_0x1450:
            r0 = move-exception
            r15 = r72
            r5 = r79
            goto L_0x145d
        L_0x1456:
            r0 = move-exception
            r15 = r72
            r5 = r79
            r4 = r80
        L_0x145d:
            r2 = r0
            r10 = r4
            goto L_0x1490
        L_0x1460:
            r0 = move-exception
            r15 = r72
            r5 = r79
            r4 = r80
            r2 = r0
            r10 = r4
        L_0x1469:
            r6 = 0
            goto L_0x14e8
        L_0x146c:
            r5 = r79
            r4 = r80
            r15 = r14
            r20 = -5
            r14 = r13
            r23 = r82
            r21 = r86
            r27 = r88
            r6 = 0
            r8 = 0
            r13 = -5
            r29 = 0
            r47 = 0
            r48 = 0
            r52 = 0
        L_0x1485:
            if (r8 == 0) goto L_0x1494
            r8.release()     // Catch:{ all -> 0x148b }
            goto L_0x1494
        L_0x148b:
            r0 = move-exception
            r2 = r0
            r10 = r4
            r1 = r23
        L_0x1490:
            r6 = r48
            goto L_0x14e8
        L_0x1494:
            if (r52 == 0) goto L_0x1499
            r52.release()     // Catch:{ all -> 0x148b }
        L_0x1499:
            if (r47 == 0) goto L_0x14a1
            r47.stop()     // Catch:{ all -> 0x148b }
            r47.release()     // Catch:{ all -> 0x148b }
        L_0x14a1:
            if (r29 == 0) goto L_0x14a6
            r29.release()     // Catch:{ all -> 0x148b }
        L_0x14a6:
            r72.checkConversionCanceled()     // Catch:{ all -> 0x148b }
            r10 = r4
            r1 = r6
            r6 = r23
            r36 = r48
        L_0x14af:
            android.media.MediaExtractor r2 = r15.extractor
            if (r2 == 0) goto L_0x14b6
            r2.release()
        L_0x14b6:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer
            if (r2 == 0) goto L_0x14cb
            r2.finishMovie()     // Catch:{ all -> 0x14c6 }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ all -> 0x14c6 }
            long r2 = r2.getLastFrameTimestamp(r13)     // Catch:{ all -> 0x14c6 }
            r15.endPresentationTime = r2     // Catch:{ all -> 0x14c6 }
            goto L_0x14cb
        L_0x14c6:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x14cb:
            r13 = r1
            r8 = r5
            r11 = r6
            r9 = r10
            r18 = r21
            goto L_0x153b
        L_0x14d3:
            r0 = move-exception
            r14 = r8
            r5 = r9
            r4 = r10
            r15 = r12
            goto L_0x14dd
        L_0x14d9:
            r0 = move-exception
            r14 = r8
            r5 = r9
            r4 = r10
        L_0x14dd:
            r20 = -5
        L_0x14df:
            r1 = r82
            r21 = r86
            r27 = r88
            r2 = r0
        L_0x14e6:
            r6 = 0
        L_0x14e7:
            r13 = -5
        L_0x14e8:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x15ab }
            r3.<init>()     // Catch:{ all -> 0x15ab }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x15ab }
            r3.append(r1)     // Catch:{ all -> 0x15ab }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x15ab }
            r3.append(r14)     // Catch:{ all -> 0x15ab }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x15ab }
            r3.append(r10)     // Catch:{ all -> 0x15ab }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x15ab }
            r3.append(r5)     // Catch:{ all -> 0x15ab }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x15ab }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x15ab }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x15ab }
            android.media.MediaExtractor r2 = r15.extractor
            if (r2 == 0) goto L_0x151e
            r2.release()
        L_0x151e:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer
            if (r2 == 0) goto L_0x1533
            r2.finishMovie()     // Catch:{ all -> 0x152e }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ all -> 0x152e }
            long r2 = r2.getLastFrameTimestamp(r13)     // Catch:{ all -> 0x152e }
            r15.endPresentationTime = r2     // Catch:{ all -> 0x152e }
            goto L_0x1533
        L_0x152e:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x1533:
            r11 = r1
            r8 = r5
            r36 = r6
            r9 = r10
            r18 = r21
            r13 = 1
        L_0x153b:
            if (r36 == 0) goto L_0x156a
            r22 = 1
            r1 = r72
            r2 = r73
            r3 = r74
            r4 = r75
            r5 = r76
            r6 = r77
            r7 = r78
            r10 = r81
            r12 = r83
            r13 = r84
            r15 = r18
            r17 = r27
            r19 = r90
            r21 = r92
            r23 = r94
            r24 = r95
            r25 = r96
            r26 = r97
            r27 = r98
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r15, r17, r19, r21, r22, r23, r24, r25, r26, r27)
            return r1
        L_0x156a:
            long r1 = java.lang.System.currentTimeMillis()
            long r1 = r1 - r16
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x15aa
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "compression completed time="
            r3.append(r4)
            r3.append(r1)
            java.lang.String r1 = " needCompress="
            r3.append(r1)
            r1 = r92
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
        L_0x15aa:
            return r13
        L_0x15ab:
            r0 = move-exception
            r1 = r72
            r2 = r0
            android.media.MediaExtractor r3 = r1.extractor
            if (r3 == 0) goto L_0x15b6
            r3.release()
        L_0x15b6:
            org.telegram.messenger.video.MP4Builder r3 = r1.mediaMuxer
            if (r3 == 0) goto L_0x15cb
            r3.finishMovie()     // Catch:{ all -> 0x15c6 }
            org.telegram.messenger.video.MP4Builder r3 = r1.mediaMuxer     // Catch:{ all -> 0x15c6 }
            long r3 = r3.getLastFrameTimestamp(r13)     // Catch:{ all -> 0x15c6 }
            r1.endPresentationTime = r3     // Catch:{ all -> 0x15c6 }
            goto L_0x15cb
        L_0x15c6:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x15cb:
            goto L_0x15cd
        L_0x15cc:
            throw r2
        L_0x15cd:
            goto L_0x15cc
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
