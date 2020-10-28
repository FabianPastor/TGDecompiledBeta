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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v21, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v27, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v92, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v80, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v92, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v95, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v96, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v97, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r38v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v20, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v108, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v110, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v111, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v112, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v113, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v115, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v31, resolved type: long} */
    /* JADX WARNING: type inference failed for: r46v11 */
    /* JADX WARNING: type inference failed for: r46v12 */
    /* JADX WARNING: type inference failed for: r46v13 */
    /* JADX WARNING: type inference failed for: r59v1 */
    /* JADX WARNING: type inference failed for: r46v23 */
    /* JADX WARNING: type inference failed for: r46v25 */
    /* JADX WARNING: type inference failed for: r46v26 */
    /* JADX WARNING: Code restructure failed: missing block: B:1008:0x1174, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1009:0x1175, code lost:
        r3 = r0;
        r2 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1010:0x1179, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1011:0x117a, code lost:
        r2 = r0;
        r46 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1041:0x1207, code lost:
        r2.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1045:?, code lost:
        r2.finishMovie();
        r11.endPresentationTime = r11.mediaMuxer.getLastFrameTimestamp(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1046:0x121a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1047:0x121b, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1058:0x1257, code lost:
        r4.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1062:?, code lost:
        r4.finishMovie();
        r2.endPresentationTime = r2.mediaMuxer.getLastFrameTimestamp(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1063:0x126a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1064:0x126b, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x0433, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x046c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x048f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x0490, code lost:
        r10 = r37;
        r2 = r0;
        r6 = r75;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x0496, code lost:
        r11 = r49;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x049a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x049b, code lost:
        r10 = r37;
        r2 = r0;
        r6 = r75;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x04a2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x04a3, code lost:
        r10 = r2;
        r48 = r9;
        r49 = r11;
        r11 = r6;
        r2 = r0;
        r37 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x04ac, code lost:
        r11 = r49;
        r1 = -5;
        r28 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x04b2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x04b3, code lost:
        r48 = r9;
        r49 = r11;
        r11 = r6;
        r2 = r0;
        r11 = r49;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x04bd, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x04be, code lost:
        r48 = r9;
        r49 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x04c3, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00c5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00c6, code lost:
        r2 = r0;
        r48 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x0543, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x0544, code lost:
        r47 = r82;
        r38 = r84;
        r2 = r0;
        r46 = r46;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x0550, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x0552, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x0553, code lost:
        r9 = r48;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x0555, code lost:
        r47 = r82;
        r38 = r84;
        r2 = r0;
        r4 = r9;
        r7 = r11;
        r8 = r12;
        r11 = r15;
        r46 = r46;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x055f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x0564, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x0565, code lost:
        r47 = r82;
        r38 = r84;
        r2 = r0;
        r4 = r48;
        r7 = r11;
        r8 = r12;
        r11 = r15;
        r46 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x0572, code lost:
        r9 = r77;
        r46 = r46;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x05a1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x05a2, code lost:
        r47 = r82;
        r38 = r84;
        r2 = r0;
        r4 = r9;
        r9 = r10;
        r7 = r11;
        r8 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x05ab, code lost:
        r11 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x05fb, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x05fc, code lost:
        r8 = r75;
        r7 = r76;
        r4 = r78;
        r47 = r82;
        r38 = r84;
        r2 = r0;
        r9 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x0609, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:0x060a, code lost:
        r8 = r75;
        r7 = r76;
        r4 = r78;
        r47 = r82;
        r38 = r84;
        r2 = r0;
        r9 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x0671, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x0672, code lost:
        r47 = r82;
        r2 = r0;
        r61 = r5;
        r9 = r14;
        r38 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x06a2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x06a3, code lost:
        r47 = r82;
        r2 = r0;
        r61 = r5;
        r38 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x06aa, code lost:
        r9 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x06c6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x06c7, code lost:
        r47 = r82;
        r38 = r84;
        r2 = r0;
        r61 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x06f7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x06f8, code lost:
        r47 = r82;
        r38 = r84;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x06fd, code lost:
        r9 = r14;
        r61 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x0764, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x0765, code lost:
        r47 = r82;
        r38 = r84;
        r2 = r0;
        r4 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x07f7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x07f8, code lost:
        r9 = r77;
        r6 = r78;
        r47 = r82;
        r2 = r0;
        r42 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x0801, code lost:
        r4 = r22;
        r38 = r23;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x0877, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x0879, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x087a, code lost:
        r13 = r80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x087c, code lost:
        r9 = r77;
        r6 = r78;
        r47 = r82;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x08a3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x08a4, code lost:
        r6 = r78;
        r47 = r82;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x08c8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x08ca, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x08cb, code lost:
        r9 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x08cd, code lost:
        r2 = r0;
        r42 = r6;
        r47 = r9;
        r9 = r11;
        r4 = r22;
        r38 = r23;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x08da, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x08db, code lost:
        r6 = r78;
        r2 = r0;
        r47 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x08e2, code lost:
        r9 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x08e5, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x08f3, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x08f4, code lost:
        r13 = r80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x08f6, code lost:
        r6 = r78;
        r2 = r0;
        r47 = r82;
        r4 = r22;
        r38 = r23;
        r1 = -5;
        r42 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x0962, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0963, code lost:
        r1 = r82;
        r2 = r0;
        r42 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x09db, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x09dc, code lost:
        r1 = r82;
        r42 = r84;
        r2 = r0;
        r61 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x0a2b, code lost:
        if (r13.presentationTimeUs < r9) goto L_0x0a2d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x01a2, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x01a3, code lost:
        r6 = r75;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0a74, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x0a75, code lost:
        r61 = r6;
        r59 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:600:0x0a82, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0a83, code lost:
        r61 = r6;
        r59 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0a87, code lost:
        r6 = r80;
        r59 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x0ac7, code lost:
        r0 = e;
        r59 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x0ac9, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x0aca, code lost:
        r1 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:673:0x0bd8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0bd9, code lost:
        r3 = r0;
        r1 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0bdd, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0bde, code lost:
        r42 = r84;
        r2 = r0;
        r1 = r5;
        r6 = r8;
        r47 = r9;
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x01c1, code lost:
        r6 = r7;
        r7 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x0d1b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x0d1c, code lost:
        r47 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x0e35, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x0e36, code lost:
        r1 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x0eb0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x0ee1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:?, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
        r6 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:899:0x0f1c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x0f3f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x0f4a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:909:0x0f4b, code lost:
        r9 = r77;
        r82 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:0x0var_, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:0x0f8a, code lost:
        r9 = r77;
        r10 = r54;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x0fce, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x0fcf, code lost:
        r42 = r84;
        r2 = r0;
        r1 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x0fd9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x0fda, code lost:
        r2 = r70;
        r3 = r0;
        r1 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:928:0x0fe0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:0x0fe1, code lost:
        r8 = r78;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:935:0x0ff2, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:0x0ff3, code lost:
        r82 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:0x0ff5, code lost:
        r2 = r70;
        r1 = r82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:0x1013, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:0x1014, code lost:
        r9 = r11;
        r10 = r54;
        r47 = r82;
        r42 = r6;
        r2 = r0;
        r6 = r78;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:943:0x1025, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:944:0x1026, code lost:
        r9 = r77;
        r8 = r78;
        r10 = r54;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:0x102d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:946:0x102e, code lost:
        r9 = r77;
        r8 = r78;
        r10 = r54;
        r3 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x1036, code lost:
        r47 = r82;
        r2 = r0;
        r6 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x103c, code lost:
        r4 = r22;
        r38 = r23;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x1052, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x1053, code lost:
        r23 = r84;
        r9 = r14;
        r61 = r25;
        r47 = r82;
        r2 = r0;
        r6 = r5;
        r54 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:0x1068, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x1069, code lost:
        r9 = r14;
        r61 = r25;
        r47 = r82;
        r2 = r0;
        r6 = r5;
        r54 = r1;
        r4 = r22;
        r38 = r84;
        r1 = -5;
        r3 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:0x1084, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x1085, code lost:
        r9 = r14;
        r61 = r25;
        r47 = r82;
        r2 = r0;
        r6 = r5;
        r4 = r22;
        r38 = r84;
        r1 = -5;
        r3 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x1098, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:0x1099, code lost:
        r23 = r84;
        r9 = r14;
        r61 = r25;
        r47 = r82;
        r2 = r0;
        r4 = r22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:962:0x10a6, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:0x10a7, code lost:
        r23 = r84;
        r9 = r14;
        r61 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:968:0x10bb, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:969:0x10bc, code lost:
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:978:0x10dd, code lost:
        r46 = 1;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:16:0x008b, B:301:0x0598] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:16:0x008b, B:317:0x05cc] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:16:0x008b, B:324:0x05e6] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:16:0x008b, B:351:0x066c] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:16:0x008b, B:361:0x068f] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:16:0x008b, B:369:0x06b7] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:16:0x008b, B:385:0x06eb] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:16:0x008b, B:410:0x075e] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:16:0x008b, B:436:0x07e7] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:16:0x008b, B:444:0x080e] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:16:0x008b, B:458:0x0843] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:16:0x008b, B:462:0x0861] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:16:0x008b, B:471:0x0889] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:16:0x008b, B:474:0x0892] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:16:0x008b, B:479:0x089d] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:16:0x008b, B:488:0x08b6] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:16:0x008b, B:491:0x08ba] */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1004:0x1156  */
    /* JADX WARNING: Removed duplicated region for block: B:1006:0x1170 A[SYNTHETIC, Splitter:B:1006:0x1170] */
    /* JADX WARNING: Removed duplicated region for block: B:1008:0x1174 A[Catch:{ Exception -> 0x1179, all -> 0x1174 }, ExcHandler: all (th java.lang.Throwable), PHI: r1 r11 
      PHI: (r1v23 int) = (r1v24 int), (r1v27 int) binds: [B:1006:0x1170, B:989:0x111a] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r11v10 org.telegram.messenger.video.MediaCodecVideoConvertor) = (r11v11 org.telegram.messenger.video.MediaCodecVideoConvertor), (r11v14 org.telegram.messenger.video.MediaCodecVideoConvertor) binds: [B:1006:0x1170, B:989:0x111a] A[DONT_GENERATE, DONT_INLINE], Splitter:B:989:0x111a] */
    /* JADX WARNING: Removed duplicated region for block: B:1013:0x1180 A[Catch:{ Exception -> 0x1179, all -> 0x1174 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1015:0x1185 A[Catch:{ Exception -> 0x1179, all -> 0x1174 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1017:0x118d A[Catch:{ Exception -> 0x1179, all -> 0x1174 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1022:0x119b  */
    /* JADX WARNING: Removed duplicated region for block: B:1025:0x11a2 A[SYNTHETIC, Splitter:B:1025:0x11a2] */
    /* JADX WARNING: Removed duplicated region for block: B:1041:0x1207  */
    /* JADX WARNING: Removed duplicated region for block: B:1044:0x120e A[SYNTHETIC, Splitter:B:1044:0x120e] */
    /* JADX WARNING: Removed duplicated region for block: B:1050:0x1227  */
    /* JADX WARNING: Removed duplicated region for block: B:1052:0x124e A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:1058:0x1257  */
    /* JADX WARNING: Removed duplicated region for block: B:1061:0x125e A[SYNTHETIC, Splitter:B:1061:0x125e] */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x03bb A[Catch:{ Exception -> 0x0433, all -> 0x055f }] */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x03be A[Catch:{ Exception -> 0x0433, all -> 0x055f }] */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x03cb  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03e1  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x04c3 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:16:0x008b] */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x04d4 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0517 A[SYNTHETIC, Splitter:B:273:0x0517] */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0526 A[Catch:{ Exception -> 0x051b, all -> 0x055f }] */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x052b A[Catch:{ Exception -> 0x051b, all -> 0x055f }] */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x055f A[ExcHandler: all (th java.lang.Throwable), PHI: r1 
      PHI: (r1v139 int) = (r1v141 int), (r1v142 int), (r1v142 int), (r1v142 int), (r1v142 int), (r1v142 int), (r1v142 int), (r1v142 int), (r1v142 int), (r1v157 int), (r1v161 int), (r1v161 int), (r1v172 int), (r1v161 int), (r1v161 int), (r1v161 int), (r1v161 int), (r1v161 int), (r1v161 int), (r1v161 int), (r1v161 int), (r1v161 int) binds: [B:273:0x0517, B:257:0x04d0, B:258:?, B:263:0x04db, B:264:?, B:266:0x04e7, B:267:?, B:269:0x04f1, B:270:?, B:64:0x01b4, B:74:0x01dc, B:225:0x0437, B:192:0x03b5, B:117:0x0292, B:118:?, B:162:0x032f, B:163:?, B:181:0x0388, B:168:0x0348, B:125:0x02a0, B:111:0x0285, B:83:0x01fc] A[DONT_GENERATE, DONT_INLINE], Splitter:B:192:0x03b5] */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x061d  */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x07e7 A[SYNTHETIC, Splitter:B:436:0x07e7] */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x0808  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x080e A[SYNTHETIC, Splitter:B:444:0x080e] */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x083e  */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x0841  */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x08e7  */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x0908  */
    /* JADX WARNING: Removed duplicated region for block: B:509:0x0916  */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x0918  */
    /* JADX WARNING: Removed duplicated region for block: B:532:0x0975 A[SYNTHETIC, Splitter:B:532:0x0975] */
    /* JADX WARNING: Removed duplicated region for block: B:606:0x0aaa A[Catch:{ Exception -> 0x0ac7, all -> 0x0ac9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:614:0x0ac9 A[ExcHandler: all (th java.lang.Throwable), PHI: r82 
      PHI: (r82v4 int) = (r82v5 int), (r82v5 int), (r82v5 int), (r82v5 int), (r82v5 int), (r82v5 int), (r82v5 int), (r82v5 int), (r82v5 int), (r82v5 int), (r82v5 int), (r82v5 int), (r82v5 int), (r82v5 int), (r82v5 int), (r82v5 int), (r82v5 int), (r82v18 int) binds: [B:532:0x0975, B:557:0x09ed, B:558:?, B:563:0x0a04, B:564:?, B:580:0x0a2e, B:581:?, B:583:0x0a3e, B:593:0x0a66, B:575:0x0a27, B:576:?, B:569:0x0a19, B:566:0x0a08, B:567:?, B:561:0x09fc, B:562:?, B:537:0x0983, B:523:0x0959] A[DONT_GENERATE, DONT_INLINE], Splitter:B:523:0x0959] */
    /* JADX WARNING: Removed duplicated region for block: B:619:0x0adf  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b15  */
    /* JADX WARNING: Removed duplicated region for block: B:628:0x0b20  */
    /* JADX WARNING: Removed duplicated region for block: B:633:0x0b2f  */
    /* JADX WARNING: Removed duplicated region for block: B:636:0x0b47  */
    /* JADX WARNING: Removed duplicated region for block: B:673:0x0bd8 A[ExcHandler: all (r0v58 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:640:0x0b4e] */
    /* JADX WARNING: Removed duplicated region for block: B:765:0x0d2c A[Catch:{ Exception -> 0x0var_, all -> 0x0fd9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x0d2e A[Catch:{ Exception -> 0x0var_, all -> 0x0fd9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:770:0x0d39  */
    /* JADX WARNING: Removed duplicated region for block: B:772:0x0d54  */
    /* JADX WARNING: Removed duplicated region for block: B:830:0x0e35 A[ExcHandler: all (th java.lang.Throwable), PHI: r11 
      PHI: (r11v29 org.telegram.messenger.video.MediaCodecVideoConvertor) = (r11v30 org.telegram.messenger.video.MediaCodecVideoConvertor), (r11v30 org.telegram.messenger.video.MediaCodecVideoConvertor), (r11v32 org.telegram.messenger.video.MediaCodecVideoConvertor) binds: [B:825:0x0e0e, B:826:?, B:820:0x0df6] A[DONT_GENERATE, DONT_INLINE], Splitter:B:820:0x0df6] */
    /* JADX WARNING: Removed duplicated region for block: B:844:0x0e6d  */
    /* JADX WARNING: Removed duplicated region for block: B:845:0x0e70  */
    /* JADX WARNING: Removed duplicated region for block: B:852:0x0e7e A[SYNTHETIC, Splitter:B:852:0x0e7e] */
    /* JADX WARNING: Removed duplicated region for block: B:857:0x0ea2 A[Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }] */
    /* JADX WARNING: Removed duplicated region for block: B:864:0x0eb5 A[Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }] */
    /* JADX WARNING: Removed duplicated region for block: B:865:0x0eb8 A[Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }] */
    /* JADX WARNING: Removed duplicated region for block: B:873:0x0ecd  */
    /* JADX WARNING: Removed duplicated region for block: B:891:0x0efe A[Catch:{ Exception -> 0x0var_, all -> 0x0f3f }] */
    /* JADX WARNING: Removed duplicated region for block: B:894:0x0f0a A[Catch:{ Exception -> 0x0var_, all -> 0x0f3f }] */
    /* JADX WARNING: Removed duplicated region for block: B:898:0x0var_ A[Catch:{ Exception -> 0x0var_, all -> 0x0f3f }] */
    /* JADX WARNING: Removed duplicated region for block: B:905:0x0f3f A[ExcHandler: all (th java.lang.Throwable), Splitter:B:870:0x0ec8] */
    /* JADX WARNING: Removed duplicated region for block: B:926:0x0fd9 A[ExcHandler: all (r0v39 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r5 
      PHI: (r5v22 int) = (r5v19 int), (r5v19 int), (r5v19 int), (r5v24 int), (r5v19 int), (r5v30 int), (r5v19 int) binds: [B:624:0x0b10, B:625:?, B:629:0x0b28, B:783:0x0d7f, B:917:0x0var_, B:762:0x0d26, B:683:0x0bf6] A[DONT_GENERATE, DONT_INLINE], Splitter:B:624:0x0b10] */
    /* JADX WARNING: Removed duplicated region for block: B:935:0x0ff2 A[ExcHandler: all (th java.lang.Throwable), PHI: r5 
      PHI: (r5v17 int) = (r5v16 int), (r5v16 int), (r5v24 int), (r5v24 int), (r5v24 int) binds: [B:518:0x0950, B:519:?, B:774:0x0d58, B:789:0x0da2, B:790:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:518:0x0950] */
    /* JADX WARNING: Removed duplicated region for block: B:968:0x10bb A[ExcHandler: all (th java.lang.Throwable), Splitter:B:393:0x0709] */
    /* JADX WARNING: Removed duplicated region for block: B:977:0x10db A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:992:0x1123 A[Catch:{ Exception -> 0x1136, all -> 0x1174 }] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:37:0x0102=Splitter:B:37:0x0102, B:452:0x0832=Splitter:B:452:0x0832, B:60:0x01a9=Splitter:B:60:0x01a9, B:16:0x008b=Splitter:B:16:0x008b, B:28:0x00cb=Splitter:B:28:0x00cb} */
    /* JADX WARNING: Unknown variable types count: 1 */
    @android.annotation.TargetApi(18)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideoInternal(java.lang.String r71, java.io.File r72, int r73, boolean r74, int r75, int r76, int r77, int r78, int r79, long r80, long r82, long r84, long r86, boolean r88, boolean r89, org.telegram.messenger.MediaController.SavedFilterState r90, java.lang.String r91, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r92, boolean r93, org.telegram.messenger.MediaController.CropState r94) {
        /*
            r70 = this;
            r15 = r70
            r13 = r71
            r14 = r73
            r12 = r75
            r11 = r76
            r10 = r77
            r9 = r78
            r8 = r79
            r6 = r80
            r4 = r86
            r3 = r94
            int r2 = android.os.Build.VERSION.SDK_INT
            r7 = 0
            android.media.MediaCodec$BufferInfo r6 = new android.media.MediaCodec$BufferInfo     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            r6.<init>()     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            org.telegram.messenger.video.Mp4Movie r1 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            r1.<init>()     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            r18 = r6
            r6 = r72
            r1.setCacheFile(r6)     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            r1.setRotation(r7)     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            r1.setSize(r12, r11)     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            org.telegram.messenger.video.MP4Builder r7 = new org.telegram.messenger.video.MP4Builder     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            r7.<init>()     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            r6 = r74
            org.telegram.messenger.video.MP4Builder r1 = r7.createMovie(r1, r6)     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            r15.mediaMuxer = r1     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            float r1 = (float) r4     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            r20 = 1148846080(0x447a0000, float:1000.0)
            float r21 = r1 / r20
            r22 = 1000(0x3e8, double:4.94E-321)
            r7 = r2
            long r1 = r4 * r22
            r15.endPresentationTime = r1     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            r70.checkConversionCanceled()     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            java.lang.String r1 = "csd-0"
            java.lang.String r6 = "prepend-sps-pps-to-idr-frames"
            r2 = 921600(0xe1000, float:1.291437E-39)
            r24 = r6
            r23 = r7
            java.lang.String r7 = "video/avc"
            r29 = r7
            r6 = 0
            if (r93 == 0) goto L_0x0576
            int r31 = (r84 > r6 ? 1 : (r84 == r6 ? 0 : -1))
            if (r31 < 0) goto L_0x0086
            r2 = 1157234688(0x44fa0000, float:2000.0)
            int r2 = (r21 > r2 ? 1 : (r21 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0071
            r2 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            r9 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x008b
        L_0x0071:
            r2 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r2 = (r21 > r2 ? 1 : (r21 == r2 ? 0 : -1))
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
            r31 = 1098907648(0x41800000, float:16.0)
            if (r2 == 0) goto L_0x00cb
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            if (r2 == 0) goto L_0x00ba
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            r2.<init>()     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            java.lang.String r6 = "changing width from "
            r2.append(r6)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            r2.append(r12)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            float r6 = (float) r12     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            float r6 = r6 / r31
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
        L_0x00ba:
            float r2 = (float) r12     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            float r2 = r2 / r31
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            int r2 = r2 * 16
            r12 = r2
            goto L_0x00cb
        L_0x00c5:
            r0 = move-exception
            r2 = r0
            r48 = r9
            goto L_0x04ca
        L_0x00cb:
            int r2 = r11 % 16
            if (r2 == 0) goto L_0x0102
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            if (r2 == 0) goto L_0x00f8
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            r2.<init>()     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            java.lang.String r6 = "changing height from "
            r2.append(r6)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            r2.append(r11)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            float r6 = (float) r11     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            float r6 = r6 / r31
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
        L_0x00f8:
            float r2 = (float) r11     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            float r2 = r2 / r31
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            int r2 = r2 * 16
            r11 = r2
        L_0x0102:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x04bd, all -> 0x04c3 }
            if (r2 == 0) goto L_0x012a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            r2.<init>()     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            java.lang.String r6 = "create photo encoder "
            r2.append(r6)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            r2.append(r12)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            java.lang.String r6 = " "
            r2.append(r6)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            r2.append(r11)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            java.lang.String r6 = " duration = "
            r2.append(r6)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            r2.append(r4)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00c5, all -> 0x04c3 }
        L_0x012a:
            r7 = r29
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r7, r12, r11)     // Catch:{ Exception -> 0x04bd, all -> 0x04c3 }
            java.lang.String r6 = "color-format"
            r29 = r1
            r1 = 2130708361(0x7var_, float:1.701803E38)
            r2.setInteger(r6, r1)     // Catch:{ Exception -> 0x04bd, all -> 0x04c3 }
            java.lang.String r1 = "bitrate"
            r2.setInteger(r1, r9)     // Catch:{ Exception -> 0x04bd, all -> 0x04c3 }
            java.lang.String r1 = "frame-rate"
            r2.setInteger(r1, r10)     // Catch:{ Exception -> 0x04bd, all -> 0x04c3 }
            java.lang.String r1 = "i-frame-interval"
            r6 = 2
            r2.setInteger(r1, r6)     // Catch:{ Exception -> 0x04bd, all -> 0x04c3 }
            android.media.MediaCodec r6 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x04bd, all -> 0x04c3 }
            r16 = r7
            r1 = 0
            r7 = 1
            r6.configure(r2, r1, r1, r7)     // Catch:{ Exception -> 0x04b2, all -> 0x04c3 }
            org.telegram.messenger.video.InputSurface r2 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x04b2, all -> 0x04c3 }
            android.view.Surface r1 = r6.createInputSurface()     // Catch:{ Exception -> 0x04b2, all -> 0x04c3 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x04b2, all -> 0x04c3 }
            r2.makeCurrent()     // Catch:{ Exception -> 0x04a2, all -> 0x04c3 }
            r6.start()     // Catch:{ Exception -> 0x04a2, all -> 0x04c3 }
            org.telegram.messenger.video.OutputSurface r28 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x04a2, all -> 0x04c3 }
            r31 = 0
            float r1 = (float) r10
            r34 = 1
            r17 = r1
            r36 = r29
            r29 = 0
            r1 = r28
            r37 = r2
            r38 = r23
            r2 = r90
            r3 = r71
            r4 = r91
            r5 = r92
            r7 = r6
            r14 = r18
            r13 = 21
            r6 = r31
            r75 = r7
            r47 = r16
            r7 = r12
            r8 = r11
            r48 = r9
            r9 = r73
            r10 = r17
            r49 = r11
            r11 = r34
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x049a, all -> 0x04c3 }
            r10 = r38
            if (r10 >= r13) goto L_0x01a8
            java.nio.ByteBuffer[] r6 = r75.getOutputBuffers()     // Catch:{ Exception -> 0x01a2, all -> 0x04c3 }
            goto L_0x01a9
        L_0x01a2:
            r0 = move-exception
            r6 = r75
            r2 = r0
            goto L_0x0496
        L_0x01a8:
            r6 = 0
        L_0x01a9:
            r70.checkConversionCanceled()     // Catch:{ Exception -> 0x048f, all -> 0x04c3 }
            r1 = -5
            r2 = 1
            r3 = 0
            r4 = 0
            r5 = 0
            r7 = 0
        L_0x01b2:
            if (r7 != 0) goto L_0x047e
            r70.checkConversionCanceled()     // Catch:{ Exception -> 0x0473, all -> 0x055f }
            r8 = r3 ^ 1
            r9 = r7
            r7 = r6
            r6 = 1
        L_0x01bc:
            if (r8 != 0) goto L_0x01c4
            if (r6 == 0) goto L_0x01c1
            goto L_0x01c4
        L_0x01c1:
            r6 = r7
            r7 = r9
            goto L_0x01b2
        L_0x01c4:
            r70.checkConversionCanceled()     // Catch:{ Exception -> 0x0473, all -> 0x055f }
            if (r89 == 0) goto L_0x01d4
            r16 = 22000(0x55f0, double:1.08694E-319)
            r11 = r75
            r76 = r8
            r78 = r9
            r8 = r16
            goto L_0x01dc
        L_0x01d4:
            r11 = r75
            r76 = r8
            r78 = r9
            r8 = 2500(0x9c4, double:1.235E-320)
        L_0x01dc:
            int r8 = r11.dequeueOutputBuffer(r14, r8)     // Catch:{ Exception -> 0x0471, all -> 0x055f }
            r9 = -1
            if (r8 != r9) goto L_0x01f7
            r9 = r78
            r16 = r3
            r17 = r4
            r3 = r7
            r38 = r10
            r13 = r36
            r7 = r47
            r6 = 0
        L_0x01f1:
            r10 = -1
            r4 = r2
            r2 = r49
            goto L_0x03c9
        L_0x01f7:
            r9 = -3
            if (r8 != r9) goto L_0x020e
            if (r10 >= r13) goto L_0x0200
            java.nio.ByteBuffer[] r7 = r11.getOutputBuffers()     // Catch:{ Exception -> 0x0275, all -> 0x055f }
        L_0x0200:
            r9 = r78
            r16 = r3
            r17 = r4
            r3 = r7
            r38 = r10
            r13 = r36
        L_0x020b:
            r7 = r47
            goto L_0x01f1
        L_0x020e:
            r9 = -2
            if (r8 != r9) goto L_0x0278
            android.media.MediaFormat r9 = r11.getOutputFormat()     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            boolean r16 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            if (r16 == 0) goto L_0x0230
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            r13.<init>()     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            r75 = r6
            java.lang.String r6 = "photo encoder new format "
            r13.append(r6)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            r13.append(r9)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            java.lang.String r6 = r13.toString()     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            goto L_0x0232
        L_0x0230:
            r75 = r6
        L_0x0232:
            r13 = -5
            if (r1 != r13) goto L_0x0267
            if (r9 == 0) goto L_0x0267
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            r13 = 0
            int r1 = r6.addTrack(r9, r13)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            r6 = r24
            boolean r16 = r9.containsKey(r6)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            if (r16 == 0) goto L_0x0265
            int r13 = r9.getInteger(r6)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            r24 = r6
            r6 = 1
            if (r13 != r6) goto L_0x0267
            r13 = r36
            java.nio.ByteBuffer r4 = r9.getByteBuffer(r13)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            java.lang.String r6 = "csd-1"
            java.nio.ByteBuffer r6 = r9.getByteBuffer(r6)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            int r4 = r4.limit()     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            int r6 = r6.limit()     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            int r4 = r4 + r6
            goto L_0x0269
        L_0x0265:
            r24 = r6
        L_0x0267:
            r13 = r36
        L_0x0269:
            r6 = r75
            r9 = r78
            r16 = r3
            r17 = r4
            r3 = r7
            r38 = r10
            goto L_0x020b
        L_0x0275:
            r0 = move-exception
            goto L_0x0478
        L_0x0278:
            r75 = r6
            r13 = r36
            if (r8 < 0) goto L_0x0453
            r6 = 21
            if (r10 >= r6) goto L_0x0285
            r6 = r7[r8]     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            goto L_0x0289
        L_0x0285:
            java.nio.ByteBuffer r6 = r11.getOutputBuffer(r8)     // Catch:{ Exception -> 0x0471, all -> 0x055f }
        L_0x0289:
            if (r6 == 0) goto L_0x0435
            int r9 = r14.size     // Catch:{ Exception -> 0x0471, all -> 0x055f }
            r78 = r7
            r7 = 1
            if (r9 <= r7) goto L_0x03aa
            int r7 = r14.flags     // Catch:{ Exception -> 0x03a2, all -> 0x055f }
            r16 = r7 & 2
            if (r16 != 0) goto L_0x0323
            if (r4 == 0) goto L_0x02a9
            r16 = r7 & 1
            if (r16 == 0) goto L_0x02a9
            r38 = r10
            int r10 = r14.offset     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            int r10 = r10 + r4
            r14.offset = r10     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            int r9 = r9 - r4
            r14.size = r9     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            goto L_0x02ab
        L_0x02a9:
            r38 = r10
        L_0x02ab:
            if (r2 == 0) goto L_0x02fa
            r7 = r7 & 1
            if (r7 == 0) goto L_0x02fa
            int r2 = r14.size     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            r10 = 100
            if (r2 <= r10) goto L_0x02f8
            int r2 = r14.offset     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            r6.position(r2)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            byte[] r2 = new byte[r10]     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            r6.get(r2)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            r7 = 0
            r9 = 0
        L_0x02c3:
            r10 = 96
            if (r7 >= r10) goto L_0x02f8
            byte r10 = r2[r7]     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            if (r10 != 0) goto L_0x02ef
            int r10 = r7 + 1
            byte r10 = r2[r10]     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            if (r10 != 0) goto L_0x02ef
            int r10 = r7 + 2
            byte r10 = r2[r10]     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            if (r10 != 0) goto L_0x02ef
            int r10 = r7 + 3
            byte r10 = r2[r10]     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            r16 = r2
            r2 = 1
            if (r10 != r2) goto L_0x02f1
            int r9 = r9 + 1
            if (r9 <= r2) goto L_0x02f1
            int r2 = r14.offset     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            int r2 = r2 + r7
            r14.offset = r2     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            int r2 = r14.size     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            int r2 = r2 - r7
            r14.size = r2     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            goto L_0x02f8
        L_0x02ef:
            r16 = r2
        L_0x02f1:
            int r7 = r7 + 1
            r2 = r16
            r10 = 100
            goto L_0x02c3
        L_0x02f8:
            r7 = 0
            goto L_0x02fb
        L_0x02fa:
            r7 = r2
        L_0x02fb:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            r10 = r7
            r9 = 1
            long r6 = r2.writeSampleData(r1, r6, r14, r9)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            r16 = r3
            r2 = 0
            int r9 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r9 == 0) goto L_0x031a
            org.telegram.messenger.MediaController$VideoConvertorListener r9 = r15.callback     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            if (r9 == 0) goto L_0x031a
            r17 = r4
            float r4 = (float) r2     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            float r4 = r4 / r20
            float r4 = r4 / r21
            r9.didWriteData(r6, r4)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            goto L_0x031c
        L_0x031a:
            r17 = r4
        L_0x031c:
            r4 = r10
        L_0x031d:
            r7 = r47
        L_0x031f:
            r2 = r49
            goto L_0x03b5
        L_0x0323:
            r16 = r3
            r17 = r4
            r38 = r10
            r7 = -5
            r4 = r2
            r2 = 0
            if (r1 != r7) goto L_0x031d
            byte[] r7 = new byte[r9]     // Catch:{ Exception -> 0x03a2, all -> 0x055f }
            int r10 = r14.offset     // Catch:{ Exception -> 0x03a2, all -> 0x055f }
            int r10 = r10 + r9
            r6.limit(r10)     // Catch:{ Exception -> 0x03a2, all -> 0x055f }
            int r9 = r14.offset     // Catch:{ Exception -> 0x03a2, all -> 0x055f }
            r6.position(r9)     // Catch:{ Exception -> 0x03a2, all -> 0x055f }
            r6.get(r7)     // Catch:{ Exception -> 0x03a2, all -> 0x055f }
            int r6 = r14.size     // Catch:{ Exception -> 0x03a2, all -> 0x055f }
            r9 = 1
            int r6 = r6 - r9
        L_0x0343:
            if (r6 < 0) goto L_0x0382
            r10 = 3
            if (r6 <= r10) goto L_0x0382
            byte r10 = r7[r6]     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            if (r10 != r9) goto L_0x037c
            int r9 = r6 + -1
            byte r9 = r7[r9]     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            if (r9 != 0) goto L_0x037c
            int r9 = r6 + -2
            byte r9 = r7[r9]     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            if (r9 != 0) goto L_0x037c
            int r9 = r6 + -3
            byte r10 = r7[r9]     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            if (r10 != 0) goto L_0x037c
            java.nio.ByteBuffer r6 = java.nio.ByteBuffer.allocate(r9)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            int r10 = r14.size     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            int r10 = r10 - r9
            java.nio.ByteBuffer r10 = java.nio.ByteBuffer.allocate(r10)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            r2 = 0
            java.nio.ByteBuffer r3 = r6.put(r7, r2, r9)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            r3.position(r2)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            int r3 = r14.size     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            int r3 = r3 - r9
            java.nio.ByteBuffer r3 = r10.put(r7, r9, r3)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            r3.position(r2)     // Catch:{ Exception -> 0x0275, all -> 0x055f }
            goto L_0x0384
        L_0x037c:
            int r6 = r6 + -1
            r2 = 0
            r9 = 1
            goto L_0x0343
        L_0x0382:
            r6 = 0
            r10 = 0
        L_0x0384:
            r7 = r47
            r2 = r49
            android.media.MediaFormat r3 = android.media.MediaFormat.createVideoFormat(r7, r12, r2)     // Catch:{ Exception -> 0x03a0, all -> 0x055f }
            if (r6 == 0) goto L_0x0398
            if (r10 == 0) goto L_0x0398
            r3.setByteBuffer(r13, r6)     // Catch:{ Exception -> 0x03a0, all -> 0x055f }
            java.lang.String r6 = "csd-1"
            r3.setByteBuffer(r6, r10)     // Catch:{ Exception -> 0x03a0, all -> 0x055f }
        L_0x0398:
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x03a0, all -> 0x055f }
            r9 = 0
            int r1 = r6.addTrack(r3, r9)     // Catch:{ Exception -> 0x03a0, all -> 0x055f }
            goto L_0x03b5
        L_0x03a0:
            r0 = move-exception
            goto L_0x03a5
        L_0x03a2:
            r0 = move-exception
            r2 = r49
        L_0x03a5:
            r6 = r11
            r11 = r2
            r2 = r0
            goto L_0x04d0
        L_0x03aa:
            r16 = r3
            r17 = r4
            r38 = r10
            r7 = r47
            r4 = r2
            goto L_0x031f
        L_0x03b5:
            int r3 = r14.flags     // Catch:{ Exception -> 0x0433, all -> 0x055f }
            r3 = r3 & 4
            if (r3 == 0) goto L_0x03be
            r3 = 0
            r6 = 1
            goto L_0x03c0
        L_0x03be:
            r3 = 0
            r6 = 0
        L_0x03c0:
            r11.releaseOutputBuffer(r8, r3)     // Catch:{ Exception -> 0x0433, all -> 0x055f }
            r3 = r78
            r9 = r6
            r10 = -1
            r6 = r75
        L_0x03c9:
            if (r8 == r10) goto L_0x03e1
            r8 = r76
            r49 = r2
            r2 = r4
            r47 = r7
        L_0x03d2:
            r75 = r11
            r36 = r13
            r4 = r17
            r10 = r38
            r13 = 21
            r7 = r3
            r3 = r16
            goto L_0x01bc
        L_0x03e1:
            if (r16 != 0) goto L_0x0423
            r28.drawImage()     // Catch:{ Exception -> 0x041d, all -> 0x0418 }
            float r8 = (float) r5
            r10 = 1106247680(0x41var_, float:30.0)
            float r8 = r8 / r10
            float r8 = r8 * r20
            float r8 = r8 * r20
            float r8 = r8 * r20
            r75 = r1
            r49 = r2
            long r1 = (long) r8
            r10 = r37
            r10.setPresentationTime(r1)     // Catch:{ Exception -> 0x0414, all -> 0x040f }
            r10.swapBuffers()     // Catch:{ Exception -> 0x0414, all -> 0x040f }
            int r5 = r5 + 1
            float r1 = (float) r5     // Catch:{ Exception -> 0x0414, all -> 0x040f }
            r2 = 1106247680(0x41var_, float:30.0)
            float r2 = r2 * r21
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 < 0) goto L_0x0429
            r11.signalEndOfInputStream()     // Catch:{ Exception -> 0x0414, all -> 0x040f }
            r8 = 0
            r16 = 1
            goto L_0x042b
        L_0x040f:
            r0 = move-exception
            r1 = r75
            goto L_0x0560
        L_0x0414:
            r0 = move-exception
            r1 = r75
            goto L_0x046d
        L_0x0418:
            r0 = move-exception
            r75 = r1
            goto L_0x0560
        L_0x041d:
            r0 = move-exception
            r75 = r1
        L_0x0420:
            r49 = r2
            goto L_0x0476
        L_0x0423:
            r75 = r1
            r49 = r2
            r10 = r37
        L_0x0429:
            r8 = r76
        L_0x042b:
            r1 = r75
            r2 = r4
            r47 = r7
            r37 = r10
            goto L_0x03d2
        L_0x0433:
            r0 = move-exception
            goto L_0x0420
        L_0x0435:
            r10 = r37
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x046c, all -> 0x055f }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x046c, all -> 0x055f }
            r3.<init>()     // Catch:{ Exception -> 0x046c, all -> 0x055f }
            java.lang.String r4 = "encoderOutputBuffer "
            r3.append(r4)     // Catch:{ Exception -> 0x046c, all -> 0x055f }
            r3.append(r8)     // Catch:{ Exception -> 0x046c, all -> 0x055f }
            java.lang.String r4 = " was null"
            r3.append(r4)     // Catch:{ Exception -> 0x046c, all -> 0x055f }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x046c, all -> 0x055f }
            r2.<init>(r3)     // Catch:{ Exception -> 0x046c, all -> 0x055f }
            throw r2     // Catch:{ Exception -> 0x046c, all -> 0x055f }
        L_0x0453:
            r10 = r37
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x046c, all -> 0x055f }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x046c, all -> 0x055f }
            r3.<init>()     // Catch:{ Exception -> 0x046c, all -> 0x055f }
            java.lang.String r4 = "unexpected result from encoder.dequeueOutputBuffer: "
            r3.append(r4)     // Catch:{ Exception -> 0x046c, all -> 0x055f }
            r3.append(r8)     // Catch:{ Exception -> 0x046c, all -> 0x055f }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x046c, all -> 0x055f }
            r2.<init>(r3)     // Catch:{ Exception -> 0x046c, all -> 0x055f }
            throw r2     // Catch:{ Exception -> 0x046c, all -> 0x055f }
        L_0x046c:
            r0 = move-exception
        L_0x046d:
            r2 = r0
            r37 = r10
            goto L_0x0479
        L_0x0471:
            r0 = move-exception
            goto L_0x0476
        L_0x0473:
            r0 = move-exception
            r11 = r75
        L_0x0476:
            r10 = r37
        L_0x0478:
            r2 = r0
        L_0x0479:
            r6 = r11
            r11 = r49
            goto L_0x04d0
        L_0x047e:
            r11 = r75
            r10 = r37
            r2 = r10
            r6 = r11
            r9 = r48
            r11 = r49
            r7 = 0
            r46 = 0
            r10 = r77
            goto L_0x0515
        L_0x048f:
            r0 = move-exception
            r11 = r75
            r10 = r37
            r2 = r0
            r6 = r11
        L_0x0496:
            r11 = r49
            r1 = -5
            goto L_0x04d0
        L_0x049a:
            r0 = move-exception
            r11 = r75
            r10 = r37
            r2 = r0
            r6 = r11
            goto L_0x04ac
        L_0x04a2:
            r0 = move-exception
            r10 = r2
            r48 = r9
            r49 = r11
            r11 = r6
            r2 = r0
            r37 = r10
        L_0x04ac:
            r11 = r49
            r1 = -5
            r28 = 0
            goto L_0x04d0
        L_0x04b2:
            r0 = move-exception
            r48 = r9
            r49 = r11
            r11 = r6
            r2 = r0
            r11 = r49
            r1 = -5
            goto L_0x04cc
        L_0x04bd:
            r0 = move-exception
            r48 = r9
            r49 = r11
            goto L_0x04c9
        L_0x04c3:
            r0 = move-exception
            goto L_0x11bd
        L_0x04c6:
            r0 = move-exception
            r48 = r9
        L_0x04c9:
            r2 = r0
        L_0x04ca:
            r1 = -5
            r6 = 0
        L_0x04cc:
            r28 = 0
            r37 = 0
        L_0x04d0:
            boolean r3 = r2 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x0564, all -> 0x055f }
            if (r3 == 0) goto L_0x04d9
            if (r89 != 0) goto L_0x04d9
            r46 = 1
            goto L_0x04db
        L_0x04d9:
            r46 = 0
        L_0x04db:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0552, all -> 0x055f }
            r3.<init>()     // Catch:{ Exception -> 0x0552, all -> 0x055f }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ Exception -> 0x0552, all -> 0x055f }
            r9 = r48
            r3.append(r9)     // Catch:{ Exception -> 0x0550, all -> 0x055f }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ Exception -> 0x0550, all -> 0x055f }
            r10 = r77
            r3.append(r10)     // Catch:{ Exception -> 0x0543, all -> 0x055f }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ Exception -> 0x0543, all -> 0x055f }
            r3.append(r11)     // Catch:{ Exception -> 0x0543, all -> 0x055f }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ Exception -> 0x0543, all -> 0x055f }
            r3.append(r12)     // Catch:{ Exception -> 0x0543, all -> 0x055f }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0543, all -> 0x055f }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ Exception -> 0x0543, all -> 0x055f }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ Exception -> 0x0543, all -> 0x055f }
            r2 = r37
            r7 = r46
            r46 = 1
        L_0x0515:
            if (r28 == 0) goto L_0x0524
            r28.release()     // Catch:{ Exception -> 0x051b, all -> 0x055f }
            goto L_0x0524
        L_0x051b:
            r0 = move-exception
            r47 = r82
            r38 = r84
            r2 = r0
            r46 = r7
            goto L_0x0549
        L_0x0524:
            if (r2 == 0) goto L_0x0529
            r2.release()     // Catch:{ Exception -> 0x051b, all -> 0x055f }
        L_0x0529:
            if (r6 == 0) goto L_0x0531
            r6.stop()     // Catch:{ Exception -> 0x051b, all -> 0x055f }
            r6.release()     // Catch:{ Exception -> 0x051b, all -> 0x055f }
        L_0x0531:
            r70.checkConversionCanceled()     // Catch:{ Exception -> 0x051b, all -> 0x055f }
            r47 = r82
            r38 = r84
            r4 = r9
            r9 = r10
            r8 = r12
            r6 = r46
            r46 = r7
            r7 = r11
            r11 = r15
            goto L_0x1197
        L_0x0543:
            r0 = move-exception
            r47 = r82
            r38 = r84
            r2 = r0
        L_0x0549:
            r4 = r9
            r9 = r10
            r7 = r11
            r8 = r12
            r11 = r15
            goto L_0x11d3
        L_0x0550:
            r0 = move-exception
            goto L_0x0555
        L_0x0552:
            r0 = move-exception
            r9 = r48
        L_0x0555:
            r47 = r82
            r38 = r84
            r2 = r0
            r4 = r9
            r7 = r11
            r8 = r12
            r11 = r15
            goto L_0x0572
        L_0x055f:
            r0 = move-exception
        L_0x0560:
            r3 = r0
        L_0x0561:
            r2 = r15
            goto L_0x1253
        L_0x0564:
            r0 = move-exception
            r9 = r48
            r47 = r82
            r38 = r84
            r2 = r0
            r4 = r9
            r7 = r11
            r8 = r12
            r11 = r15
            r46 = 0
        L_0x0572:
            r9 = r77
            goto L_0x11d3
        L_0x0576:
            r13 = r1
            r14 = r18
            r38 = r23
            r7 = r29
            r1 = 921600(0xe1000, float:1.291437E-39)
            android.media.MediaExtractor r2 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            r2.<init>()     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            r15.extractor = r2     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            r8 = r71
            r6 = r24
            r2.setDataSource(r8)     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            r3 = 0
            int r5 = org.telegram.messenger.MediaController.findTrack(r2, r3)     // Catch:{ Exception -> 0x11c2, all -> 0x11ba }
            r2 = -1
            if (r9 == r2) goto L_0x05ae
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x05a1, all -> 0x04c3 }
            r4 = 1
            int r2 = org.telegram.messenger.MediaController.findTrack(r2, r4)     // Catch:{ Exception -> 0x05a1, all -> 0x04c3 }
            r3 = r2
            goto L_0x05b0
        L_0x05a1:
            r0 = move-exception
            r47 = r82
            r38 = r84
            r2 = r0
            r4 = r9
            r9 = r10
            r7 = r11
            r8 = r12
        L_0x05ab:
            r11 = r15
            goto L_0x11d0
        L_0x05ae:
            r4 = 1
            r3 = -1
        L_0x05b0:
            java.lang.String r2 = "mime"
            if (r5 < 0) goto L_0x05c6
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x05a1, all -> 0x04c3 }
            android.media.MediaFormat r4 = r4.getTrackFormat(r5)     // Catch:{ Exception -> 0x05a1, all -> 0x04c3 }
            java.lang.String r4 = r4.getString(r2)     // Catch:{ Exception -> 0x05a1, all -> 0x04c3 }
            boolean r4 = r4.equals(r7)     // Catch:{ Exception -> 0x05a1, all -> 0x04c3 }
            if (r4 != 0) goto L_0x05c6
            r4 = 1
            goto L_0x05c7
        L_0x05c6:
            r4 = 0
        L_0x05c7:
            if (r88 != 0) goto L_0x0617
            if (r4 == 0) goto L_0x05cc
            goto L_0x0617
        L_0x05cc:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0609, all -> 0x04c3 }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ Exception -> 0x0609, all -> 0x04c3 }
            r4 = -1
            if (r9 == r4) goto L_0x05d5
            r13 = 1
            goto L_0x05d6
        L_0x05d5:
            r13 = 0
        L_0x05d6:
            r1 = r70
            r5 = 1
            r4 = r14
            r14 = 1
            r5 = r80
            r14 = r8
            r7 = r82
            r14 = r10
            r9 = r86
            r11 = r72
            r12 = r13
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ Exception -> 0x05fb, all -> 0x04c3 }
            r8 = r75
            r7 = r76
            r4 = r78
            r47 = r82
            r38 = r84
            r9 = r14
            r11 = r15
            r1 = -5
            r6 = 0
            r46 = 0
            goto L_0x1197
        L_0x05fb:
            r0 = move-exception
            r8 = r75
            r7 = r76
            r4 = r78
            r47 = r82
            r38 = r84
            r2 = r0
            r9 = r14
            goto L_0x05ab
        L_0x0609:
            r0 = move-exception
            r8 = r75
            r7 = r76
            r4 = r78
            r47 = r82
            r38 = r84
            r2 = r0
            r9 = r10
            goto L_0x05ab
        L_0x0617:
            r12 = r14
            r4 = -1
            r11 = 1
            r14 = r10
            if (r5 < 0) goto L_0x1156
            r16 = -2147483648(0xfffffffvar_, double:NaN)
            r8 = 1000(0x3e8, float:1.401E-42)
            r18 = -1
            int r8 = r8 / r14
            int r8 = r8 * 1000
            long r9 = (long) r8     // Catch:{ Exception -> 0x10c1, all -> 0x10bb }
            android.media.MediaExtractor r8 = r15.extractor     // Catch:{ Exception -> 0x10c1, all -> 0x10bb }
            r8.selectTrack(r5)     // Catch:{ Exception -> 0x10c1, all -> 0x10bb }
            android.media.MediaExtractor r8 = r15.extractor     // Catch:{ Exception -> 0x10c1, all -> 0x10bb }
            android.media.MediaFormat r8 = r8.getTrackFormat(r5)     // Catch:{ Exception -> 0x10c1, all -> 0x10bb }
            r22 = 0
            int r24 = (r84 > r22 ? 1 : (r84 == r22 ? 0 : -1))
            if (r24 < 0) goto L_0x0658
            r22 = 1157234688(0x44fa0000, float:2000.0)
            int r22 = (r21 > r22 ? 1 : (r21 == r22 ? 0 : -1))
            if (r22 > 0) goto L_0x0643
            r22 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x0651
        L_0x0643:
            r22 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r22 = (r21 > r22 ? 1 : (r21 == r22 ? 0 : -1))
            if (r22 > 0) goto L_0x064e
            r22 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x0651
        L_0x064e:
            r22 = 1560000(0x17cdc0, float:2.186026E-39)
        L_0x0651:
            r24 = r3
            r4 = r22
            r22 = 0
            goto L_0x0668
        L_0x0658:
            if (r78 > 0) goto L_0x0662
            r22 = r84
            r24 = r3
            r4 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x0668
        L_0x0662:
            r4 = r78
            r22 = r84
            r24 = r3
        L_0x0668:
            r3 = r79
            if (r3 <= 0) goto L_0x067c
            int r4 = java.lang.Math.min(r3, r4)     // Catch:{ Exception -> 0x0671, all -> 0x04c3 }
            goto L_0x067c
        L_0x0671:
            r0 = move-exception
            r47 = r82
            r2 = r0
            r61 = r5
            r9 = r14
            r38 = r22
            goto L_0x10ce
        L_0x067c:
            r25 = 0
            int r27 = (r22 > r25 ? 1 : (r22 == r25 ? 0 : -1))
            if (r27 < 0) goto L_0x0687
            r27 = r12
            r11 = r18
            goto L_0x068b
        L_0x0687:
            r27 = r12
            r11 = r22
        L_0x068b:
            int r22 = (r11 > r25 ? 1 : (r11 == r25 ? 0 : -1))
            if (r22 < 0) goto L_0x06ad
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x06a2, all -> 0x04c3 }
            r3 = 0
            r1.seekTo(r11, r3)     // Catch:{ Exception -> 0x06a2, all -> 0x04c3 }
            r3 = r94
            r25 = r5
            r23 = r6
            r84 = r11
            r5 = 0
            r11 = r80
            goto L_0x06dd
        L_0x06a2:
            r0 = move-exception
            r47 = r82
            r2 = r0
            r61 = r5
            r38 = r11
        L_0x06aa:
            r9 = r14
            goto L_0x10ce
        L_0x06ad:
            r84 = r11
            r25 = 0
            r11 = r80
            int r1 = (r11 > r25 ? 1 : (r11 == r25 ? 0 : -1))
            if (r1 <= 0) goto L_0x06cf
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x06c6, all -> 0x04c3 }
            r3 = 0
            r1.seekTo(r11, r3)     // Catch:{ Exception -> 0x06c6, all -> 0x04c3 }
            r3 = r94
            r25 = r5
            r23 = r6
            r5 = 0
            goto L_0x06dd
        L_0x06c6:
            r0 = move-exception
            r47 = r82
            r38 = r84
            r2 = r0
            r61 = r5
            goto L_0x06aa
        L_0x06cf:
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x10ad, all -> 0x10bb }
            r25 = r5
            r23 = r6
            r3 = 0
            r5 = 0
            r1.seekTo(r5, r3)     // Catch:{ Exception -> 0x10a6, all -> 0x10bb }
            r3 = r94
        L_0x06dd:
            if (r3 == 0) goto L_0x0702
            r1 = 90
            r11 = r73
            r12 = -1
            if (r11 == r1) goto L_0x06f0
            r1 = 270(0x10e, float:3.78E-43)
            if (r11 != r1) goto L_0x06eb
            goto L_0x06f0
        L_0x06eb:
            int r1 = r3.transformWidth     // Catch:{ Exception -> 0x06f7, all -> 0x04c3 }
            int r5 = r3.transformHeight     // Catch:{ Exception -> 0x06f7, all -> 0x04c3 }
            goto L_0x06f4
        L_0x06f0:
            int r1 = r3.transformHeight     // Catch:{ Exception -> 0x06f7, all -> 0x04c3 }
            int r5 = r3.transformWidth     // Catch:{ Exception -> 0x06f7, all -> 0x04c3 }
        L_0x06f4:
            r6 = r5
            r5 = r1
            goto L_0x0709
        L_0x06f7:
            r0 = move-exception
            r47 = r82
            r38 = r84
            r2 = r0
        L_0x06fd:
            r9 = r14
            r61 = r25
            goto L_0x10ce
        L_0x0702:
            r11 = r73
            r12 = -1
            r5 = r75
            r6 = r76
        L_0x0709:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x10a6, all -> 0x10bb }
            if (r1 == 0) goto L_0x0729
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06f7, all -> 0x04c3 }
            r1.<init>()     // Catch:{ Exception -> 0x06f7, all -> 0x04c3 }
            java.lang.String r12 = "create encoder with w = "
            r1.append(r12)     // Catch:{ Exception -> 0x06f7, all -> 0x04c3 }
            r1.append(r5)     // Catch:{ Exception -> 0x06f7, all -> 0x04c3 }
            java.lang.String r12 = " h = "
            r1.append(r12)     // Catch:{ Exception -> 0x06f7, all -> 0x04c3 }
            r1.append(r6)     // Catch:{ Exception -> 0x06f7, all -> 0x04c3 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x06f7, all -> 0x04c3 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x06f7, all -> 0x04c3 }
        L_0x0729:
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r7, r5, r6)     // Catch:{ Exception -> 0x10a6, all -> 0x10bb }
            java.lang.String r12 = "color-format"
            r3 = 2130708361(0x7var_, float:1.701803E38)
            r1.setInteger(r12, r3)     // Catch:{ Exception -> 0x10a6, all -> 0x10bb }
            java.lang.String r3 = "bitrate"
            r1.setInteger(r3, r4)     // Catch:{ Exception -> 0x10a6, all -> 0x10bb }
            java.lang.String r3 = "frame-rate"
            r1.setInteger(r3, r14)     // Catch:{ Exception -> 0x10a6, all -> 0x10bb }
            java.lang.String r3 = "i-frame-interval"
            r12 = 2
            r1.setInteger(r3, r12)     // Catch:{ Exception -> 0x10a6, all -> 0x10bb }
            r3 = 23
            r12 = r38
            if (r12 >= r3) goto L_0x076c
            int r3 = java.lang.Math.min(r6, r5)     // Catch:{ Exception -> 0x06f7, all -> 0x04c3 }
            r26 = r5
            r5 = 480(0x1e0, float:6.73E-43)
            if (r3 > r5) goto L_0x076e
            r3 = 921600(0xe1000, float:1.291437E-39)
            if (r4 <= r3) goto L_0x075b
            goto L_0x075c
        L_0x075b:
            r3 = r4
        L_0x075c:
            java.lang.String r4 = "bitrate"
            r1.setInteger(r4, r3)     // Catch:{ Exception -> 0x0764, all -> 0x04c3 }
            r22 = r3
            goto L_0x0770
        L_0x0764:
            r0 = move-exception
            r47 = r82
            r38 = r84
            r2 = r0
            r4 = r3
            goto L_0x06fd
        L_0x076c:
            r26 = r5
        L_0x076e:
            r22 = r4
        L_0x0770:
            android.media.MediaCodec r5 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x1098, all -> 0x10bb }
            r3 = 1
            r4 = 0
            r5.configure(r1, r4, r4, r3)     // Catch:{ Exception -> 0x1084, all -> 0x10bb }
            org.telegram.messenger.video.InputSurface r1 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x1084, all -> 0x10bb }
            android.view.Surface r3 = r5.createInputSurface()     // Catch:{ Exception -> 0x1084, all -> 0x10bb }
            r1.<init>(r3)     // Catch:{ Exception -> 0x1084, all -> 0x10bb }
            r1.makeCurrent()     // Catch:{ Exception -> 0x1068, all -> 0x10bb }
            r5.start()     // Catch:{ Exception -> 0x1068, all -> 0x10bb }
            java.lang.String r3 = r8.getString(r2)     // Catch:{ Exception -> 0x1068, all -> 0x10bb }
            android.media.MediaCodec r3 = android.media.MediaCodec.createDecoderByType(r3)     // Catch:{ Exception -> 0x1068, all -> 0x10bb }
            org.telegram.messenger.video.OutputSurface r28 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x1052, all -> 0x10bb }
            r29 = 0
            r78 = r3
            float r3 = (float) r14
            r30 = 0
            r54 = r1
            r1 = r28
            r55 = r2
            r2 = r90
            r59 = r78
            r58 = r24
            r31 = 1
            r24 = r3
            r3 = r29
            r4 = r91
            r78 = r5
            r61 = r25
            r62 = r26
            r5 = r92
            r63 = r6
            r64 = r23
            r6 = r94
            r65 = r7
            r7 = r75
            r66 = r8
            r8 = r76
            r25 = r9
            r9 = r73
            r10 = r24
            r23 = r84
            r36 = r13
            r13 = 1
            r11 = r30
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x1042, all -> 0x10bb }
            android.view.Surface r1 = r28.getSurface()     // Catch:{ Exception -> 0x102d, all -> 0x10bb }
            r3 = r59
            r2 = r66
            r4 = 0
            r5 = 0
            r3.configure(r2, r1, r5, r4)     // Catch:{ Exception -> 0x1025, all -> 0x10bb }
            r3.start()     // Catch:{ Exception -> 0x1025, all -> 0x10bb }
            r1 = 21
            if (r12 >= r1) goto L_0x0808
            java.nio.ByteBuffer[] r6 = r3.getInputBuffers()     // Catch:{ Exception -> 0x07f7, all -> 0x04c3 }
            java.nio.ByteBuffer[] r1 = r78.getOutputBuffers()     // Catch:{ Exception -> 0x07f7, all -> 0x04c3 }
            r2 = r58
            r69 = r6
            r6 = r1
            r1 = r69
            goto L_0x080c
        L_0x07f7:
            r0 = move-exception
            r9 = r77
            r6 = r78
            r47 = r82
            r2 = r0
            r42 = r5
        L_0x0801:
            r4 = r22
            r38 = r23
            r1 = -5
            goto L_0x10d7
        L_0x0808:
            r1 = r5
            r6 = r1
            r2 = r58
        L_0x080c:
            if (r2 < 0) goto L_0x0908
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x08f3, all -> 0x04c3 }
            android.media.MediaFormat r4 = r4.getTrackFormat(r2)     // Catch:{ Exception -> 0x08f3, all -> 0x04c3 }
            r7 = r55
            java.lang.String r8 = r4.getString(r7)     // Catch:{ Exception -> 0x08f3, all -> 0x04c3 }
            java.lang.String r9 = "audio/mp4a-latm"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x08f3, all -> 0x04c3 }
            if (r8 != 0) goto L_0x0831
            java.lang.String r8 = r4.getString(r7)     // Catch:{ Exception -> 0x07f7, all -> 0x04c3 }
            java.lang.String r9 = "audio/mpeg"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x07f7, all -> 0x04c3 }
            if (r8 == 0) goto L_0x082f
            goto L_0x0831
        L_0x082f:
            r8 = 0
            goto L_0x0832
        L_0x0831:
            r8 = 1
        L_0x0832:
            java.lang.String r7 = r4.getString(r7)     // Catch:{ Exception -> 0x08f3, all -> 0x04c3 }
            java.lang.String r9 = "audio/unknown"
            boolean r7 = r7.equals(r9)     // Catch:{ Exception -> 0x08f3, all -> 0x04c3 }
            if (r7 == 0) goto L_0x083f
            r2 = -1
        L_0x083f:
            if (r2 < 0) goto L_0x08e7
            if (r8 == 0) goto L_0x0885
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ Exception -> 0x0879, all -> 0x04c3 }
            int r7 = r7.addTrack(r4, r13)     // Catch:{ Exception -> 0x0879, all -> 0x04c3 }
            android.media.MediaExtractor r9 = r15.extractor     // Catch:{ Exception -> 0x0879, all -> 0x04c3 }
            r9.selectTrack(r2)     // Catch:{ Exception -> 0x0879, all -> 0x04c3 }
            java.lang.String r9 = "max-input-size"
            int r4 = r4.getInteger(r9)     // Catch:{ Exception -> 0x0879, all -> 0x04c3 }
            java.nio.ByteBuffer r4 = java.nio.ByteBuffer.allocateDirect(r4)     // Catch:{ Exception -> 0x0879, all -> 0x04c3 }
            r13 = r80
            r9 = 0
            r11 = 1
            int r29 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1))
            if (r29 <= 0) goto L_0x0868
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x0877, all -> 0x04c3 }
            r11 = 0
            r5.seekTo(r13, r11)     // Catch:{ Exception -> 0x0877, all -> 0x04c3 }
            goto L_0x086e
        L_0x0868:
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x0877, all -> 0x04c3 }
            r11 = 0
            r5.seekTo(r9, r11)     // Catch:{ Exception -> 0x0877, all -> 0x04c3 }
        L_0x086e:
            r11 = r77
            r9 = r82
            r84 = r6
            r6 = 0
            goto L_0x0914
        L_0x0877:
            r0 = move-exception
            goto L_0x087c
        L_0x0879:
            r0 = move-exception
            r13 = r80
        L_0x087c:
            r9 = r77
            r6 = r78
            r47 = r82
            r2 = r0
            goto L_0x103c
        L_0x0885:
            r13 = r80
            r9 = 0
            android.media.MediaExtractor r5 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x08e5, all -> 0x04c3 }
            r5.<init>()     // Catch:{ Exception -> 0x08e5, all -> 0x04c3 }
            r7 = r71
            r11 = r77
            r5.setDataSource(r7)     // Catch:{ Exception -> 0x08da, all -> 0x04c3 }
            r5.selectTrack(r2)     // Catch:{ Exception -> 0x08da, all -> 0x04c3 }
            int r29 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1))
            if (r29 <= 0) goto L_0x08aa
            r9 = 0
            r5.seekTo(r13, r9)     // Catch:{ Exception -> 0x08a3, all -> 0x04c3 }
            r84 = r6
            goto L_0x08b1
        L_0x08a3:
            r0 = move-exception
            r6 = r78
            r47 = r82
            r2 = r0
            goto L_0x08e2
        L_0x08aa:
            r84 = r6
            r6 = r9
            r9 = 0
            r5.seekTo(r6, r9)     // Catch:{ Exception -> 0x08da, all -> 0x04c3 }
        L_0x08b1:
            org.telegram.messenger.video.AudioRecoder r6 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x08da, all -> 0x04c3 }
            r6.<init>(r4, r5, r2)     // Catch:{ Exception -> 0x08da, all -> 0x04c3 }
            r6.startTime = r13     // Catch:{ Exception -> 0x08ca, all -> 0x04c3 }
            r9 = r82
            r6.endTime = r9     // Catch:{ Exception -> 0x08c8, all -> 0x04c3 }
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x08c8, all -> 0x04c3 }
            android.media.MediaFormat r5 = r6.format     // Catch:{ Exception -> 0x08c8, all -> 0x04c3 }
            r7 = 1
            int r4 = r4.addTrack(r5, r7)     // Catch:{ Exception -> 0x08c8, all -> 0x04c3 }
            r7 = r4
            r4 = 0
            goto L_0x0914
        L_0x08c8:
            r0 = move-exception
            goto L_0x08cd
        L_0x08ca:
            r0 = move-exception
            r9 = r82
        L_0x08cd:
            r2 = r0
            r42 = r6
            r47 = r9
            r9 = r11
            r4 = r22
            r38 = r23
            r1 = -5
            goto L_0x096d
        L_0x08da:
            r0 = move-exception
            r9 = r82
            r6 = r78
            r2 = r0
            r47 = r9
        L_0x08e2:
            r9 = r11
            goto L_0x103c
        L_0x08e5:
            r0 = move-exception
            goto L_0x08f6
        L_0x08e7:
            r11 = r77
            r13 = r80
            r9 = r82
            r84 = r6
            r4 = 0
            r6 = 0
            r7 = -5
            goto L_0x0914
        L_0x08f3:
            r0 = move-exception
            r13 = r80
        L_0x08f6:
            r9 = r82
            r6 = r78
            r2 = r0
            r47 = r9
            r4 = r22
            r38 = r23
            r1 = -5
            r42 = 0
        L_0x0904:
            r9 = r77
            goto L_0x10d7
        L_0x0908:
            r11 = r77
            r13 = r80
            r9 = r82
            r84 = r6
            r4 = 0
            r6 = 0
            r7 = -5
            r8 = 1
        L_0x0914:
            if (r2 >= 0) goto L_0x0918
            r5 = 1
            goto L_0x0919
        L_0x0918:
            r5 = 0
        L_0x0919:
            r70.checkConversionCanceled()     // Catch:{ Exception -> 0x1013, all -> 0x10bb }
            r55 = r16
            r57 = r18
            r38 = r23
            r17 = 0
            r24 = 1
            r29 = 0
            r31 = 0
            r33 = 0
            r34 = 0
            r37 = 0
            r23 = r84
            r16 = r5
            r5 = -5
        L_0x0935:
            if (r33 == 0) goto L_0x0950
            if (r8 != 0) goto L_0x093c
            if (r16 != 0) goto L_0x093c
            goto L_0x0950
        L_0x093c:
            r8 = r75
            r7 = r76
            r2 = r78
            r1 = r5
            r42 = r6
            r47 = r9
            r9 = r11
            r11 = r15
            r4 = r22
            r6 = 0
            r46 = 0
            goto L_0x111a
        L_0x0950:
            r70.checkConversionCanceled()     // Catch:{ Exception -> 0x0ffb, all -> 0x0ff2 }
            if (r8 != 0) goto L_0x0971
            if (r6 == 0) goto L_0x0971
            r82 = r5
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x0962, all -> 0x0ac9 }
            boolean r5 = r6.step(r5, r7)     // Catch:{ Exception -> 0x0962, all -> 0x0ac9 }
            r16 = r5
            goto L_0x0973
        L_0x0962:
            r0 = move-exception
            r1 = r82
            r2 = r0
            r42 = r6
        L_0x0968:
            r47 = r9
            r9 = r11
            r4 = r22
        L_0x096d:
            r6 = r78
            goto L_0x10d7
        L_0x0971:
            r82 = r5
        L_0x0973:
            if (r17 != 0) goto L_0x0adf
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x0ace, all -> 0x0ac9 }
            int r5 = r5.getSampleTrackIndex()     // Catch:{ Exception -> 0x0ace, all -> 0x0ac9 }
            r84 = r6
            r6 = r61
            if (r5 != r6) goto L_0x09e4
            r13 = 2500(0x9c4, double:1.235E-320)
            int r5 = r3.dequeueInputBuffer(r13)     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            if (r5 < 0) goto L_0x09c8
            r13 = 21
            if (r12 >= r13) goto L_0x0990
            r13 = r1[r5]     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            goto L_0x0994
        L_0x0990:
            java.nio.ByteBuffer r13 = r3.getInputBuffer(r5)     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
        L_0x0994:
            android.media.MediaExtractor r14 = r15.extractor     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            r85 = r1
            r1 = 0
            int r50 = r14.readSampleData(r13, r1)     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            if (r50 >= 0) goto L_0x09b1
            r49 = 0
            r50 = 0
            r51 = 0
            r53 = 4
            r47 = r3
            r48 = r5
            r47.queueInputBuffer(r48, r49, r50, r51, r53)     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            r17 = 1
            goto L_0x09ca
        L_0x09b1:
            r49 = 0
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            long r51 = r1.getSampleTime()     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            r53 = 0
            r47 = r3
            r48 = r5
            r47.queueInputBuffer(r48, r49, r50, r51, r53)     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            r1.advance()     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            goto L_0x09ca
        L_0x09c8:
            r85 = r1
        L_0x09ca:
            r14 = r2
            r2 = r4
            r61 = r6
            r45 = r7
            r59 = r9
            r13 = r27
            r1 = 0
            r6 = r80
            r27 = r8
            goto L_0x0aad
        L_0x09db:
            r0 = move-exception
            r1 = r82
            r42 = r84
            r2 = r0
            r61 = r6
            goto L_0x0968
        L_0x09e4:
            r85 = r1
            if (r8 == 0) goto L_0x0a99
            r1 = -1
            if (r2 == r1) goto L_0x0a8a
            if (r5 != r2) goto L_0x0a99
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a82, all -> 0x0ac9 }
            r5 = 0
            int r1 = r1.readSampleData(r4, r5)     // Catch:{ Exception -> 0x0a82, all -> 0x0ac9 }
            r13 = r27
            r13.size = r1     // Catch:{ Exception -> 0x0a82, all -> 0x0ac9 }
            r1 = 21
            if (r12 >= r1) goto L_0x0a04
            r4.position(r5)     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            int r1 = r13.size     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            r4.limit(r1)     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
        L_0x0a04:
            int r1 = r13.size     // Catch:{ Exception -> 0x0a82, all -> 0x0ac9 }
            if (r1 < 0) goto L_0x0a17
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            r14 = r2
            long r1 = r1.getSampleTime()     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            r13.presentationTimeUs = r1     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            r1.advance()     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            goto L_0x0a1d
        L_0x0a17:
            r14 = r2
            r1 = 0
            r13.size = r1     // Catch:{ Exception -> 0x0a82, all -> 0x0ac9 }
            r17 = 1
        L_0x0a1d:
            int r1 = r13.size     // Catch:{ Exception -> 0x0a82, all -> 0x0ac9 }
            if (r1 <= 0) goto L_0x0a78
            r1 = 0
            int r5 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r5 < 0) goto L_0x0a2d
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x09db, all -> 0x0ac9 }
            int r5 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r5 >= 0) goto L_0x0a78
        L_0x0a2d:
            r1 = 0
            r13.offset = r1     // Catch:{ Exception -> 0x0a82, all -> 0x0ac9 }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0a82, all -> 0x0ac9 }
            int r2 = r2.getSampleFlags()     // Catch:{ Exception -> 0x0a82, all -> 0x0ac9 }
            r13.flags = r2     // Catch:{ Exception -> 0x0a82, all -> 0x0ac9 }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0a82, all -> 0x0ac9 }
            r27 = r8
            r59 = r9
            long r8 = r2.writeSampleData(r7, r4, r13, r1)     // Catch:{ Exception -> 0x0a74, all -> 0x0ac9 }
            r1 = 0
            int r5 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r5 == 0) goto L_0x0a6c
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r15.callback     // Catch:{ Exception -> 0x0a74, all -> 0x0ac9 }
            if (r1 == 0) goto L_0x0a6c
            r2 = r4
            long r4 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0a74, all -> 0x0ac9 }
            r61 = r6
            r45 = r7
            r10 = 2500(0x9c4, double:1.235E-320)
            r6 = r80
            long r40 = r4 - r6
            int r47 = (r40 > r29 ? 1 : (r40 == r29 ? 0 : -1))
            if (r47 <= 0) goto L_0x0a5f
            long r29 = r4 - r6
        L_0x0a5f:
            r4 = r29
            float r10 = (float) r4
            float r10 = r10 / r20
            float r10 = r10 / r21
            r1.didWriteData(r8, r10)     // Catch:{ Exception -> 0x0ac7, all -> 0x0ac9 }
            r29 = r4
            goto L_0x0aac
        L_0x0a6c:
            r2 = r4
            r61 = r6
            r45 = r7
        L_0x0a71:
            r6 = r80
            goto L_0x0aac
        L_0x0a74:
            r0 = move-exception
            r61 = r6
            goto L_0x0a87
        L_0x0a78:
            r2 = r4
            r61 = r6
            r45 = r7
            r27 = r8
            r59 = r9
            goto L_0x0a71
        L_0x0a82:
            r0 = move-exception
            r61 = r6
            r59 = r9
        L_0x0a87:
            r6 = r80
            goto L_0x0ad4
        L_0x0a8a:
            r14 = r2
            r2 = r4
            r61 = r6
            r45 = r7
            r59 = r9
            r13 = r27
            r6 = r80
            r27 = r8
            goto L_0x0aa8
        L_0x0a99:
            r14 = r2
            r2 = r4
            r61 = r6
            r45 = r7
            r59 = r9
            r13 = r27
            r6 = r80
            r27 = r8
            r1 = -1
        L_0x0aa8:
            if (r5 != r1) goto L_0x0aac
            r1 = 1
            goto L_0x0aad
        L_0x0aac:
            r1 = 0
        L_0x0aad:
            if (r1 == 0) goto L_0x0aee
            r4 = 2500(0x9c4, double:1.235E-320)
            int r48 = r3.dequeueInputBuffer(r4)     // Catch:{ Exception -> 0x0ac7, all -> 0x0ac9 }
            if (r48 < 0) goto L_0x0aee
            r49 = 0
            r50 = 0
            r51 = 0
            r53 = 4
            r47 = r3
            r47.queueInputBuffer(r48, r49, r50, r51, r53)     // Catch:{ Exception -> 0x0ac7, all -> 0x0ac9 }
            r17 = 1
            goto L_0x0aee
        L_0x0ac7:
            r0 = move-exception
            goto L_0x0ad4
        L_0x0ac9:
            r0 = move-exception
            r1 = r82
            goto L_0x0560
        L_0x0ace:
            r0 = move-exception
            r84 = r6
            r59 = r9
            r6 = r13
        L_0x0ad4:
            r9 = r77
            r6 = r78
            r1 = r82
            r42 = r84
            r2 = r0
            goto L_0x100d
        L_0x0adf:
            r85 = r1
            r84 = r6
            r45 = r7
            r59 = r9
            r6 = r13
            r13 = r27
            r14 = r2
            r2 = r4
            r27 = r8
        L_0x0aee:
            r1 = r34 ^ 1
            r5 = r82
            r4 = r1
            r67 = r55
            r9 = r59
            r1 = 1
        L_0x0af8:
            if (r4 != 0) goto L_0x0b10
            if (r1 == 0) goto L_0x0afd
            goto L_0x0b10
        L_0x0afd:
            r11 = r77
            r1 = r85
            r4 = r2
            r2 = r14
            r8 = r27
            r55 = r67
            r27 = r13
            r13 = r6
            r7 = r45
            r6 = r84
            goto L_0x0935
        L_0x0b10:
            r70.checkConversionCanceled()     // Catch:{ Exception -> 0x0fe0, all -> 0x0fd9 }
            if (r89 == 0) goto L_0x0b20
            r47 = 22000(0x55f0, double:1.08694E-319)
            r8 = r78
            r82 = r1
            r83 = r2
            r1 = r47
            goto L_0x0b28
        L_0x0b20:
            r8 = r78
            r82 = r1
            r83 = r2
            r1 = 2500(0x9c4, double:1.235E-320)
        L_0x0b28:
            int r1 = r8.dequeueOutputBuffer(r13, r1)     // Catch:{ Exception -> 0x0fd7, all -> 0x0fd9 }
            r2 = -1
            if (r1 != r2) goto L_0x0b47
            r78 = r4
            r47 = r9
            r4 = r36
            r9 = r62
            r10 = r63
            r11 = r65
            r2 = 0
        L_0x0b3c:
            r36 = r14
        L_0x0b3e:
            r14 = -1
            r69 = r33
            r33 = r12
            r12 = r69
            goto L_0x0d37
        L_0x0b47:
            r2 = -3
            if (r1 != r2) goto L_0x0b61
            r2 = 21
            if (r12 >= r2) goto L_0x0b52
            java.nio.ByteBuffer[] r23 = r8.getOutputBuffers()     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
        L_0x0b52:
            r2 = r82
            r78 = r4
            r47 = r9
            r4 = r36
            r9 = r62
            r10 = r63
            r11 = r65
            goto L_0x0b3c
        L_0x0b61:
            r2 = -2
            if (r1 != r2) goto L_0x0be9
            android.media.MediaFormat r2 = r8.getOutputFormat()     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            r11 = -5
            if (r5 != r11) goto L_0x0bc6
            if (r2 == 0) goto L_0x0bc6
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            r78 = r4
            r4 = 0
            int r5 = r11.addTrack(r2, r4)     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            r4 = r64
            boolean r11 = r2.containsKey(r4)     // Catch:{ Exception -> 0x0bb7, all -> 0x0bae }
            if (r11 == 0) goto L_0x0ba5
            int r11 = r2.getInteger(r4)     // Catch:{ Exception -> 0x0bb7, all -> 0x0bae }
            r64 = r4
            r4 = 1
            if (r11 != r4) goto L_0x0ba7
            r4 = r36
            java.nio.ByteBuffer r11 = r2.getByteBuffer(r4)     // Catch:{ Exception -> 0x0bb7, all -> 0x0bae }
            r36 = r5
            java.lang.String r5 = "csd-1"
            java.nio.ByteBuffer r2 = r2.getByteBuffer(r5)     // Catch:{ Exception -> 0x0ba3, all -> 0x0ba1 }
            int r5 = r11.limit()     // Catch:{ Exception -> 0x0ba3, all -> 0x0ba1 }
            int r2 = r2.limit()     // Catch:{ Exception -> 0x0ba3, all -> 0x0ba1 }
            int r5 = r5 + r2
            r37 = r5
            goto L_0x0bab
        L_0x0ba1:
            r0 = move-exception
            goto L_0x0bb1
        L_0x0ba3:
            r0 = move-exception
            goto L_0x0bba
        L_0x0ba5:
            r64 = r4
        L_0x0ba7:
            r4 = r36
            r36 = r5
        L_0x0bab:
            r5 = r36
            goto L_0x0bca
        L_0x0bae:
            r0 = move-exception
            r36 = r5
        L_0x0bb1:
            r3 = r0
            r2 = r15
            r1 = r36
            goto L_0x1253
        L_0x0bb7:
            r0 = move-exception
            r36 = r5
        L_0x0bba:
            r42 = r84
            r2 = r0
            r6 = r8
            r47 = r9
            r4 = r22
            r1 = r36
            goto L_0x0904
        L_0x0bc6:
            r78 = r4
            r4 = r36
        L_0x0bca:
            r2 = r82
            r47 = r9
            r36 = r14
            r9 = r62
            r10 = r63
            r11 = r65
            goto L_0x0b3e
        L_0x0bd8:
            r0 = move-exception
            r3 = r0
            r1 = r5
            goto L_0x0561
        L_0x0bdd:
            r0 = move-exception
            r42 = r84
            r2 = r0
            r1 = r5
            r6 = r8
            r47 = r9
            r4 = r22
            goto L_0x0904
        L_0x0be9:
            r78 = r4
            r4 = r36
            if (r1 < 0) goto L_0x0fb1
            r2 = 21
            if (r12 >= r2) goto L_0x0bf6
            r11 = r23[r1]     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            goto L_0x0bfa
        L_0x0bf6:
            java.nio.ByteBuffer r11 = r8.getOutputBuffer(r1)     // Catch:{ Exception -> 0x0fd7, all -> 0x0fd9 }
        L_0x0bfa:
            if (r11 == 0) goto L_0x0f8f
            int r2 = r13.size     // Catch:{ Exception -> 0x0fd7, all -> 0x0fd9 }
            r33 = r12
            r12 = 1
            if (r2 <= r12) goto L_0x0d20
            int r12 = r13.flags     // Catch:{ Exception -> 0x0d1b, all -> 0x0bd8 }
            r36 = r12 & 2
            if (r36 != 0) goto L_0x0ca2
            if (r37 == 0) goto L_0x0c1c
            r36 = r12 & 1
            if (r36 == 0) goto L_0x0c1c
            r36 = r14
            int r14 = r13.offset     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            int r14 = r14 + r37
            r13.offset = r14     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            int r2 = r2 - r37
            r13.size = r2     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            goto L_0x0c1e
        L_0x0c1c:
            r36 = r14
        L_0x0c1e:
            if (r24 == 0) goto L_0x0CLASSNAME
            r2 = r12 & 1
            if (r2 == 0) goto L_0x0CLASSNAME
            int r2 = r13.size     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            r12 = 100
            if (r2 <= r12) goto L_0x0c6f
            int r2 = r13.offset     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            r11.position(r2)     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            byte[] r2 = new byte[r12]     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            r11.get(r2)     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            r14 = 0
            r24 = 0
        L_0x0CLASSNAME:
            r12 = 96
            if (r14 >= r12) goto L_0x0c6f
            byte r12 = r2[r14]     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            if (r12 != 0) goto L_0x0CLASSNAME
            int r12 = r14 + 1
            byte r12 = r2[r12]     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            if (r12 != 0) goto L_0x0CLASSNAME
            int r12 = r14 + 2
            byte r12 = r2[r12]     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            if (r12 != 0) goto L_0x0CLASSNAME
            int r12 = r14 + 3
            byte r12 = r2[r12]     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            r47 = r2
            r2 = 1
            if (r12 != r2) goto L_0x0CLASSNAME
            int r12 = r24 + 1
            if (r12 <= r2) goto L_0x0CLASSNAME
            int r2 = r13.offset     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            int r2 = r2 + r14
            r13.offset = r2     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            int r2 = r13.size     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            int r2 = r2 - r14
            r13.size = r2     // Catch:{ Exception -> 0x0bdd, all -> 0x0bd8 }
            goto L_0x0c6f
        L_0x0CLASSNAME:
            r24 = r12
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r47 = r2
        L_0x0CLASSNAME:
            int r14 = r14 + 1
            r2 = r47
            r12 = 100
            goto L_0x0CLASSNAME
        L_0x0c6f:
            r24 = 0
        L_0x0CLASSNAME:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0d1b, all -> 0x0bd8 }
            r47 = r9
            r12 = 1
            long r9 = r2.writeSampleData(r5, r11, r13, r12)     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            r11 = 0
            int r2 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r2 == 0) goto L_0x0c9a
            org.telegram.messenger.MediaController$VideoConvertorListener r2 = r15.callback     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            if (r2 == 0) goto L_0x0c9a
            long r11 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            long r49 = r11 - r6
            int r14 = (r49 > r29 ? 1 : (r49 == r29 ? 0 : -1))
            if (r14 <= 0) goto L_0x0c8e
            long r29 = r11 - r6
        L_0x0c8e:
            r11 = r29
            float r14 = (float) r11     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            float r14 = r14 / r20
            float r14 = r14 / r21
            r2.didWriteData(r9, r14)     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            r29 = r11
        L_0x0c9a:
            r9 = r62
            r10 = r63
            r11 = r65
            goto L_0x0d26
        L_0x0ca2:
            r47 = r9
            r36 = r14
            r9 = -5
            if (r5 != r9) goto L_0x0c9a
            byte[] r9 = new byte[r2]     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            int r10 = r13.offset     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            int r10 = r10 + r2
            r11.limit(r10)     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            int r2 = r13.offset     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            r11.position(r2)     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            r11.get(r9)     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            int r2 = r13.size     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            r11 = 1
            int r2 = r2 - r11
        L_0x0cbd:
            if (r2 < 0) goto L_0x0cfa
            r10 = 3
            if (r2 <= r10) goto L_0x0cfa
            byte r12 = r9[r2]     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            if (r12 != r11) goto L_0x0cf6
            int r12 = r2 + -1
            byte r12 = r9[r12]     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            if (r12 != 0) goto L_0x0cf6
            int r12 = r2 + -2
            byte r12 = r9[r12]     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            if (r12 != 0) goto L_0x0cf6
            int r12 = r2 + -3
            byte r14 = r9[r12]     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            if (r14 != 0) goto L_0x0cf6
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r12)     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            int r14 = r13.size     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            int r14 = r14 - r12
            java.nio.ByteBuffer r14 = java.nio.ByteBuffer.allocate(r14)     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            r10 = 0
            java.nio.ByteBuffer r11 = r2.put(r9, r10, r12)     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            r11.position(r10)     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            int r11 = r13.size     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            int r11 = r11 - r12
            java.nio.ByteBuffer r9 = r14.put(r9, r12, r11)     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            r9.position(r10)     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            goto L_0x0cfc
        L_0x0cf6:
            int r2 = r2 + -1
            r11 = 1
            goto L_0x0cbd
        L_0x0cfa:
            r2 = 0
            r14 = 0
        L_0x0cfc:
            r9 = r62
            r10 = r63
            r11 = r65
            android.media.MediaFormat r12 = android.media.MediaFormat.createVideoFormat(r11, r9, r10)     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            if (r2 == 0) goto L_0x0d12
            if (r14 == 0) goto L_0x0d12
            r12.setByteBuffer(r4, r2)     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            java.lang.String r2 = "csd-1"
            r12.setByteBuffer(r2, r14)     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
        L_0x0d12:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            r14 = 0
            int r2 = r2.addTrack(r12, r14)     // Catch:{ Exception -> 0x0d9d, all -> 0x0bd8 }
            r5 = r2
            goto L_0x0d26
        L_0x0d1b:
            r0 = move-exception
            r47 = r9
            goto L_0x0fe7
        L_0x0d20:
            r47 = r9
            r36 = r14
            goto L_0x0c9a
        L_0x0d26:
            int r2 = r13.flags     // Catch:{ Exception -> 0x0var_, all -> 0x0fd9 }
            r2 = r2 & 4
            if (r2 == 0) goto L_0x0d2e
            r2 = 1
            goto L_0x0d2f
        L_0x0d2e:
            r2 = 0
        L_0x0d2f:
            r12 = 0
            r8.releaseOutputBuffer(r1, r12)     // Catch:{ Exception -> 0x0var_, all -> 0x0fd9 }
            r12 = r2
            r14 = -1
            r2 = r82
        L_0x0d37:
            if (r1 == r14) goto L_0x0d54
            r1 = r2
            r62 = r9
            r63 = r10
            r65 = r11
            r14 = r36
            r9 = r47
            r2 = r83
            r36 = r4
            r4 = r78
            r78 = r8
        L_0x0d4c:
            r69 = r33
            r33 = r12
            r12 = r69
            goto L_0x0af8
        L_0x0d54:
            if (r34 != 0) goto L_0x0var_
            r14 = 2500(0x9c4, double:1.235E-320)
            int r1 = r3.dequeueOutputBuffer(r13, r14)     // Catch:{ Exception -> 0x0f4a, all -> 0x0ff2 }
            r14 = -1
            if (r1 != r14) goto L_0x0d77
            r49 = r2
            r15 = r4
            r82 = r5
            r62 = r9
            r63 = r10
            r65 = r11
            r10 = r54
            r1 = 0
            r4 = 0
            r35 = -5
            r40 = 2500(0x9c4, double:1.235E-320)
            r9 = r77
            goto L_0x0var_
        L_0x0d77:
            r15 = -3
            if (r1 != r15) goto L_0x0d7c
            goto L_0x0var_
        L_0x0d7c:
            r15 = -2
            if (r1 != r15) goto L_0x0da0
            android.media.MediaFormat r1 = r3.getOutputFormat()     // Catch:{ Exception -> 0x0d9d, all -> 0x0fd9 }
            boolean r15 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0d9d, all -> 0x0fd9 }
            if (r15 == 0) goto L_0x0var_
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d9d, all -> 0x0fd9 }
            r15.<init>()     // Catch:{ Exception -> 0x0d9d, all -> 0x0fd9 }
            java.lang.String r14 = "newFormat = "
            r15.append(r14)     // Catch:{ Exception -> 0x0d9d, all -> 0x0fd9 }
            r15.append(r1)     // Catch:{ Exception -> 0x0d9d, all -> 0x0fd9 }
            java.lang.String r1 = r15.toString()     // Catch:{ Exception -> 0x0d9d, all -> 0x0fd9 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0d9d, all -> 0x0fd9 }
            goto L_0x0var_
        L_0x0d9d:
            r0 = move-exception
            goto L_0x0fe7
        L_0x0da0:
            if (r1 < 0) goto L_0x0var_
            int r14 = r13.size     // Catch:{ Exception -> 0x0f4a, all -> 0x0ff2 }
            r15 = r4
            r82 = r5
            if (r14 == 0) goto L_0x0dab
            r14 = 1
            goto L_0x0dac
        L_0x0dab:
            r14 = 0
        L_0x0dac:
            long r4 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0f1e, all -> 0x0f3f }
            r43 = 0
            int r49 = (r47 > r43 ? 1 : (r47 == r43 ? 0 : -1))
            if (r49 <= 0) goto L_0x0dc7
            int r49 = (r4 > r47 ? 1 : (r4 == r47 ? 0 : -1))
            if (r49 < 0) goto L_0x0dc7
            int r14 = r13.flags     // Catch:{ Exception -> 0x0dc4, all -> 0x0f3f }
            r14 = r14 | 4
            r13.flags = r14     // Catch:{ Exception -> 0x0dc4, all -> 0x0f3f }
            r14 = 0
            r17 = 1
            r34 = 1
            goto L_0x0dc7
        L_0x0dc4:
            r0 = move-exception
            goto L_0x0e4f
        L_0x0dc7:
            r43 = 0
            int r49 = (r38 > r43 ? 1 : (r38 == r43 ? 0 : -1))
            if (r49 < 0) goto L_0x0e53
            r49 = r2
            int r2 = r13.flags     // Catch:{ Exception -> 0x0e4c, all -> 0x0f3f }
            r2 = r2 & 4
            if (r2 == 0) goto L_0x0e47
            long r50 = r38 - r6
            long r50 = java.lang.Math.abs(r50)     // Catch:{ Exception -> 0x0e4c, all -> 0x0f3f }
            r2 = 1000000(0xvar_, float:1.401298E-39)
            r62 = r9
            r40 = 2500(0x9c4, double:1.235E-320)
            r9 = r77
            int r2 = r2 / r9
            r63 = r10
            r65 = r11
            long r10 = (long) r2
            int r2 = (r50 > r10 ? 1 : (r50 == r10 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e3a
            r10 = 0
            int r2 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e00
            r11 = r70
            android.media.MediaExtractor r2 = r11.extractor     // Catch:{ Exception -> 0x0eb0, all -> 0x0e35 }
            r10 = 0
            r2.seekTo(r6, r10)     // Catch:{ Exception -> 0x0eb0, all -> 0x0e35 }
            r6 = r67
            r10 = 0
            goto L_0x0e0c
        L_0x0e00:
            r11 = r70
            android.media.MediaExtractor r2 = r11.extractor     // Catch:{ Exception -> 0x0eb0, all -> 0x0e35 }
            r6 = 0
            r10 = 0
            r2.seekTo(r6, r10)     // Catch:{ Exception -> 0x0eb0, all -> 0x0e35 }
            r6 = r67
        L_0x0e0c:
            long r31 = r6 + r25
            int r2 = r13.flags     // Catch:{ Exception -> 0x0e26, all -> 0x0e35 }
            r35 = -5
            r2 = r2 & -5
            r13.flags = r2     // Catch:{ Exception -> 0x0e26, all -> 0x0e35 }
            r3.flush()     // Catch:{ Exception -> 0x0e26, all -> 0x0e35 }
            r47 = r38
            r2 = 1
            r14 = 0
            r17 = 0
            r34 = 0
            r43 = 0
            r38 = r18
            goto L_0x0e69
        L_0x0e26:
            r0 = move-exception
            r1 = r82
            r42 = r84
            r2 = r0
            r6 = r8
            r4 = r22
            r47 = r38
            r38 = r18
            goto L_0x10d7
        L_0x0e35:
            r0 = move-exception
            r1 = r82
            goto L_0x1175
        L_0x0e3a:
            r6 = r67
            r10 = 0
            r35 = -5
            r11 = r70
            goto L_0x0e66
        L_0x0e42:
            r0 = move-exception
            r11 = r70
            goto L_0x0var_
        L_0x0e47:
            r35 = -5
            r40 = 2500(0x9c4, double:1.235E-320)
            goto L_0x0e59
        L_0x0e4c:
            r0 = move-exception
            r11 = r70
        L_0x0e4f:
            r9 = r77
            goto L_0x0var_
        L_0x0e53:
            r35 = -5
            r40 = 2500(0x9c4, double:1.235E-320)
            r49 = r2
        L_0x0e59:
            r62 = r9
            r63 = r10
            r65 = r11
            r6 = r67
            r10 = 0
            r11 = r70
            r9 = r77
        L_0x0e66:
            r2 = 0
            r43 = 0
        L_0x0e69:
            int r46 = (r38 > r43 ? 1 : (r38 == r43 ? 0 : -1))
            if (r46 < 0) goto L_0x0e70
            r10 = r38
            goto L_0x0e72
        L_0x0e70:
            r10 = r80
        L_0x0e72:
            int r50 = (r10 > r43 ? 1 : (r10 == r43 ? 0 : -1))
            if (r50 <= 0) goto L_0x0eb3
            int r50 = (r57 > r18 ? 1 : (r57 == r18 ? 0 : -1))
            if (r50 != 0) goto L_0x0eb3
            int r50 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r50 >= 0) goto L_0x0ea2
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }
            if (r4 == 0) goto L_0x0ea0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }
            r4.<init>()     // Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }
            java.lang.String r5 = "drop frame startTime = "
            r4.append(r5)     // Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }
            r4.append(r10)     // Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }
            java.lang.String r5 = " present time = "
            r4.append(r5)     // Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }
            long r10 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }
            r4.append(r10)     // Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }
        L_0x0ea0:
            r14 = 0
            goto L_0x0eb3
        L_0x0ea2:
            long r4 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }
            r10 = -2147483648(0xfffffffvar_, double:NaN)
            int r50 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r50 == 0) goto L_0x0ead
            long r31 = r31 - r4
        L_0x0ead:
            r57 = r4
            goto L_0x0eb3
        L_0x0eb0:
            r0 = move-exception
            goto L_0x0var_
        L_0x0eb3:
            if (r2 == 0) goto L_0x0eb8
            r57 = r18
            goto L_0x0ecb
        L_0x0eb8:
            int r2 = (r38 > r18 ? 1 : (r38 == r18 ? 0 : -1))
            if (r2 != 0) goto L_0x0ec8
            r4 = 0
            int r2 = (r31 > r4 ? 1 : (r31 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x0ec8
            long r4 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }
            long r4 = r4 + r31
            r13.presentationTimeUs = r4     // Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }
        L_0x0ec8:
            r3.releaseOutputBuffer(r1, r14)     // Catch:{ Exception -> 0x0f1c, all -> 0x0f3f }
        L_0x0ecb:
            if (r14 == 0) goto L_0x0efe
            r1 = 0
            int r4 = (r38 > r1 ? 1 : (r38 == r1 ? 0 : -1))
            if (r4 < 0) goto L_0x0eda
            long r4 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }
            long r67 = java.lang.Math.max(r6, r4)     // Catch:{ Exception -> 0x0eb0, all -> 0x0f3f }
            goto L_0x0edc
        L_0x0eda:
            r67 = r6
        L_0x0edc:
            r28.awaitNewImage()     // Catch:{ Exception -> 0x0ee1, all -> 0x0f3f }
            r6 = 0
            goto L_0x0ee7
        L_0x0ee1:
            r0 = move-exception
            r4 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ Exception -> 0x0f1c, all -> 0x0f3f }
            r6 = 1
        L_0x0ee7:
            if (r6 != 0) goto L_0x0efb
            r28.drawImage()     // Catch:{ Exception -> 0x0f1c, all -> 0x0f3f }
            long r4 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0f1c, all -> 0x0f3f }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 * r6
            r10 = r54
            r10.setPresentationTime(r4)     // Catch:{ Exception -> 0x0var_, all -> 0x0f3f }
            r10.swapBuffers()     // Catch:{ Exception -> 0x0var_, all -> 0x0f3f }
            goto L_0x0var_
        L_0x0efb:
            r10 = r54
            goto L_0x0var_
        L_0x0efe:
            r10 = r54
            r1 = 0
            r67 = r6
        L_0x0var_:
            int r4 = r13.flags     // Catch:{ Exception -> 0x0var_, all -> 0x0f3f }
            r4 = r4 & 4
            if (r4 == 0) goto L_0x0var_
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0var_, all -> 0x0f3f }
            if (r4 == 0) goto L_0x0var_
            java.lang.String r4 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x0var_, all -> 0x0f3f }
        L_0x0var_:
            r8.signalEndOfInputStream()     // Catch:{ Exception -> 0x0var_, all -> 0x0f3f }
            r4 = 0
            goto L_0x0var_
        L_0x0var_:
            r4 = r78
            goto L_0x0var_
        L_0x0f1c:
            r0 = move-exception
            goto L_0x0f4f
        L_0x0f1e:
            r0 = move-exception
            r9 = r77
            goto L_0x0f4f
        L_0x0var_:
            r9 = r77
            r82 = r5
            r10 = r54
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0var_, all -> 0x0f3f }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0var_, all -> 0x0f3f }
            r4.<init>()     // Catch:{ Exception -> 0x0var_, all -> 0x0f3f }
            java.lang.String r5 = "unexpected result from decoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x0f3f }
            r4.append(r1)     // Catch:{ Exception -> 0x0var_, all -> 0x0f3f }
            java.lang.String r1 = r4.toString()     // Catch:{ Exception -> 0x0var_, all -> 0x0f3f }
            r2.<init>(r1)     // Catch:{ Exception -> 0x0var_, all -> 0x0f3f }
            throw r2     // Catch:{ Exception -> 0x0var_, all -> 0x0f3f }
        L_0x0f3f:
            r0 = move-exception
            goto L_0x0ff5
        L_0x0var_:
            r0 = move-exception
            r1 = r82
            r42 = r84
            r2 = r0
            goto L_0x0fd3
        L_0x0f4a:
            r0 = move-exception
            r9 = r77
            r82 = r5
        L_0x0f4f:
            r10 = r54
        L_0x0var_:
            r1 = r82
            r42 = r84
            r2 = r0
            goto L_0x0fed
        L_0x0var_:
            r49 = r2
            r15 = r4
            r82 = r5
            r62 = r9
            r63 = r10
            r65 = r11
            r10 = r54
            r6 = r67
            r1 = 0
            r35 = -5
            r40 = 2500(0x9c4, double:1.235E-320)
            r9 = r77
            r4 = r78
            r67 = r6
        L_0x0var_:
            r6 = r80
            r5 = r82
            r2 = r83
            r78 = r8
            r54 = r10
            r14 = r36
            r9 = r47
            r1 = r49
            r36 = r15
            r15 = r70
            goto L_0x0d4c
        L_0x0var_:
            r0 = move-exception
            r9 = r77
            r10 = r54
            goto L_0x0fe9
        L_0x0f8f:
            r47 = r9
            r10 = r54
            r9 = r77
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0fce, all -> 0x0fd9 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0fce, all -> 0x0fd9 }
            r4.<init>()     // Catch:{ Exception -> 0x0fce, all -> 0x0fd9 }
            java.lang.String r6 = "encoderOutputBuffer "
            r4.append(r6)     // Catch:{ Exception -> 0x0fce, all -> 0x0fd9 }
            r4.append(r1)     // Catch:{ Exception -> 0x0fce, all -> 0x0fd9 }
            java.lang.String r1 = " was null"
            r4.append(r1)     // Catch:{ Exception -> 0x0fce, all -> 0x0fd9 }
            java.lang.String r1 = r4.toString()     // Catch:{ Exception -> 0x0fce, all -> 0x0fd9 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x0fce, all -> 0x0fd9 }
            throw r2     // Catch:{ Exception -> 0x0fce, all -> 0x0fd9 }
        L_0x0fb1:
            r47 = r9
            r10 = r54
            r9 = r77
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0fce, all -> 0x0fd9 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0fce, all -> 0x0fd9 }
            r4.<init>()     // Catch:{ Exception -> 0x0fce, all -> 0x0fd9 }
            java.lang.String r6 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r6)     // Catch:{ Exception -> 0x0fce, all -> 0x0fd9 }
            r4.append(r1)     // Catch:{ Exception -> 0x0fce, all -> 0x0fd9 }
            java.lang.String r1 = r4.toString()     // Catch:{ Exception -> 0x0fce, all -> 0x0fd9 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x0fce, all -> 0x0fd9 }
            throw r2     // Catch:{ Exception -> 0x0fce, all -> 0x0fd9 }
        L_0x0fce:
            r0 = move-exception
            r42 = r84
            r2 = r0
            r1 = r5
        L_0x0fd3:
            r6 = r8
            r54 = r10
            goto L_0x0fee
        L_0x0fd7:
            r0 = move-exception
            goto L_0x0fe3
        L_0x0fd9:
            r0 = move-exception
            r2 = r70
            r3 = r0
            r1 = r5
            goto L_0x1253
        L_0x0fe0:
            r0 = move-exception
            r8 = r78
        L_0x0fe3:
            r47 = r9
            r10 = r54
        L_0x0fe7:
            r9 = r77
        L_0x0fe9:
            r42 = r84
            r2 = r0
            r1 = r5
        L_0x0fed:
            r6 = r8
        L_0x0fee:
            r4 = r22
            goto L_0x10d7
        L_0x0ff2:
            r0 = move-exception
            r82 = r5
        L_0x0ff5:
            r2 = r70
            r1 = r82
            goto L_0x1252
        L_0x0ffb:
            r0 = move-exception
            r8 = r78
            r82 = r5
            r84 = r6
            r59 = r9
            r9 = r11
            r10 = r54
            r1 = r82
            r42 = r84
            r2 = r0
            r6 = r8
        L_0x100d:
            r4 = r22
            r47 = r59
            goto L_0x10d7
        L_0x1013:
            r0 = move-exception
            r8 = r78
            r84 = r6
            r9 = r11
            r10 = r54
            r35 = -5
            r47 = r82
            r42 = r84
            r2 = r0
            r6 = r8
            goto L_0x0801
        L_0x1025:
            r0 = move-exception
            r9 = r77
            r8 = r78
            r10 = r54
            goto L_0x1036
        L_0x102d:
            r0 = move-exception
            r9 = r77
            r8 = r78
            r10 = r54
            r3 = r59
        L_0x1036:
            r35 = -5
            r47 = r82
            r2 = r0
            r6 = r8
        L_0x103c:
            r4 = r22
            r38 = r23
            r1 = -5
            goto L_0x1080
        L_0x1042:
            r0 = move-exception
            r9 = r77
            r8 = r78
            r10 = r54
            r3 = r59
            r35 = -5
            r47 = r82
            r2 = r0
            r6 = r8
            goto L_0x1062
        L_0x1052:
            r0 = move-exception
            r23 = r84
            r10 = r1
            r8 = r5
            r9 = r14
            r61 = r25
            r35 = -5
            r47 = r82
            r2 = r0
            r6 = r8
            r54 = r10
        L_0x1062:
            r4 = r22
            r38 = r23
            r1 = -5
            goto L_0x107e
        L_0x1068:
            r0 = move-exception
            r23 = r84
            r10 = r1
            r8 = r5
            r9 = r14
            r61 = r25
            r35 = -5
            r47 = r82
            r2 = r0
            r6 = r8
            r54 = r10
            r4 = r22
            r38 = r23
            r1 = -5
            r3 = 0
        L_0x107e:
            r28 = 0
        L_0x1080:
            r42 = 0
            goto L_0x10d7
        L_0x1084:
            r0 = move-exception
            r23 = r84
            r8 = r5
            r9 = r14
            r61 = r25
            r35 = -5
            r47 = r82
            r2 = r0
            r6 = r8
            r4 = r22
            r38 = r23
            r1 = -5
            r3 = 0
            goto L_0x10d1
        L_0x1098:
            r0 = move-exception
            r23 = r84
            r9 = r14
            r61 = r25
            r35 = -5
            r47 = r82
            r2 = r0
            r4 = r22
            goto L_0x10b8
        L_0x10a6:
            r0 = move-exception
            r23 = r84
            r9 = r14
            r61 = r25
            goto L_0x10b3
        L_0x10ad:
            r0 = move-exception
            r23 = r84
            r61 = r5
            r9 = r14
        L_0x10b3:
            r35 = -5
            r47 = r82
            r2 = r0
        L_0x10b8:
            r38 = r23
            goto L_0x10ce
        L_0x10bb:
            r0 = move-exception
            r35 = -5
            r1 = -5
            goto L_0x1250
        L_0x10c1:
            r0 = move-exception
            r61 = r5
            r9 = r14
            r35 = -5
            r4 = r78
            r47 = r82
            r38 = r84
            r2 = r0
        L_0x10ce:
            r1 = -5
            r3 = 0
            r6 = 0
        L_0x10d1:
            r28 = 0
            r42 = 0
            r54 = 0
        L_0x10d7:
            boolean r5 = r2 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x114c }
            if (r5 == 0) goto L_0x10e0
            if (r89 != 0) goto L_0x10e0
            r46 = 1
            goto L_0x10e2
        L_0x10e0:
            r46 = 0
        L_0x10e2:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1142 }
            r5.<init>()     // Catch:{ Exception -> 0x1142 }
            java.lang.String r7 = "bitrate: "
            r5.append(r7)     // Catch:{ Exception -> 0x1142 }
            r5.append(r4)     // Catch:{ Exception -> 0x1142 }
            java.lang.String r7 = " framerate: "
            r5.append(r7)     // Catch:{ Exception -> 0x1142 }
            r5.append(r9)     // Catch:{ Exception -> 0x1142 }
            java.lang.String r7 = " size: "
            r5.append(r7)     // Catch:{ Exception -> 0x1142 }
            r7 = r76
            r5.append(r7)     // Catch:{ Exception -> 0x113c }
            java.lang.String r8 = "x"
            r5.append(r8)     // Catch:{ Exception -> 0x113c }
            r8 = r75
            r5.append(r8)     // Catch:{ Exception -> 0x1138 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x1138 }
            org.telegram.messenger.FileLog.e((java.lang.String) r5)     // Catch:{ Exception -> 0x1138 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ Exception -> 0x1138 }
            r11 = r70
            r2 = r6
            r6 = 1
        L_0x111a:
            android.media.MediaExtractor r5 = r11.extractor     // Catch:{ Exception -> 0x1136, all -> 0x1174 }
            r10 = r61
            r5.unselectTrack(r10)     // Catch:{ Exception -> 0x1136, all -> 0x1174 }
            if (r3 == 0) goto L_0x1129
            r3.stop()     // Catch:{ Exception -> 0x1136, all -> 0x1174 }
            r3.release()     // Catch:{ Exception -> 0x1136, all -> 0x1174 }
        L_0x1129:
            r3 = r46
            r46 = r6
            r6 = r28
            r69 = r42
            r42 = r2
            r2 = r69
            goto L_0x116e
        L_0x1136:
            r0 = move-exception
            goto L_0x1149
        L_0x1138:
            r0 = move-exception
            r11 = r70
            goto L_0x1149
        L_0x113c:
            r0 = move-exception
            r11 = r70
            r8 = r75
            goto L_0x1149
        L_0x1142:
            r0 = move-exception
            r11 = r70
            r8 = r75
            r7 = r76
        L_0x1149:
            r2 = r0
            goto L_0x11d3
        L_0x114c:
            r0 = move-exception
            r11 = r70
            r8 = r75
            r7 = r76
            r2 = r0
            goto L_0x11d1
        L_0x1156:
            r8 = r75
            r7 = r76
            r9 = r14
            r11 = r15
            r35 = -5
            r4 = r78
            r47 = r82
            r38 = r84
            r1 = -5
            r2 = 0
            r3 = 0
            r6 = 0
            r42 = 0
            r46 = 0
            r54 = 0
        L_0x116e:
            if (r6 == 0) goto L_0x117e
            r6.release()     // Catch:{ Exception -> 0x1179, all -> 0x1174 }
            goto L_0x117e
        L_0x1174:
            r0 = move-exception
        L_0x1175:
            r3 = r0
            r2 = r11
            goto L_0x1253
        L_0x1179:
            r0 = move-exception
            r2 = r0
            r46 = r3
            goto L_0x11d3
        L_0x117e:
            if (r54 == 0) goto L_0x1183
            r54.release()     // Catch:{ Exception -> 0x1179, all -> 0x1174 }
        L_0x1183:
            if (r42 == 0) goto L_0x118b
            r42.stop()     // Catch:{ Exception -> 0x1179, all -> 0x1174 }
            r42.release()     // Catch:{ Exception -> 0x1179, all -> 0x1174 }
        L_0x118b:
            if (r2 == 0) goto L_0x1190
            r2.release()     // Catch:{ Exception -> 0x1179, all -> 0x1174 }
        L_0x1190:
            r70.checkConversionCanceled()     // Catch:{ Exception -> 0x1179, all -> 0x1174 }
            r6 = r46
            r46 = r3
        L_0x1197:
            android.media.MediaExtractor r2 = r11.extractor
            if (r2 == 0) goto L_0x119e
            r2.release()
        L_0x119e:
            org.telegram.messenger.video.MP4Builder r2 = r11.mediaMuxer
            if (r2 == 0) goto L_0x11b3
            r2.finishMovie()     // Catch:{ Exception -> 0x11ae }
            org.telegram.messenger.video.MP4Builder r2 = r11.mediaMuxer     // Catch:{ Exception -> 0x11ae }
            long r1 = r2.getLastFrameTimestamp(r1)     // Catch:{ Exception -> 0x11ae }
            r11.endPresentationTime = r1     // Catch:{ Exception -> 0x11ae }
            goto L_0x11b3
        L_0x11ae:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x11b3:
            r10 = r4
            r15 = r38
            r13 = r47
            goto L_0x1225
        L_0x11ba:
            r0 = move-exception
            r35 = -5
        L_0x11bd:
            r3 = r0
            r2 = r15
            r1 = -5
            goto L_0x1253
        L_0x11c2:
            r0 = move-exception
            r9 = r10
            r7 = r11
            r8 = r12
            r11 = r15
            r35 = -5
            r4 = r78
            r47 = r82
            r38 = r84
            r2 = r0
        L_0x11d0:
            r1 = -5
        L_0x11d1:
            r46 = 0
        L_0x11d3:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x124f }
            r3.<init>()     // Catch:{ all -> 0x124f }
            java.lang.String r5 = "bitrate: "
            r3.append(r5)     // Catch:{ all -> 0x124f }
            r3.append(r4)     // Catch:{ all -> 0x124f }
            java.lang.String r5 = " framerate: "
            r3.append(r5)     // Catch:{ all -> 0x124f }
            r3.append(r9)     // Catch:{ all -> 0x124f }
            java.lang.String r5 = " size: "
            r3.append(r5)     // Catch:{ all -> 0x124f }
            r3.append(r7)     // Catch:{ all -> 0x124f }
            java.lang.String r5 = "x"
            r3.append(r5)     // Catch:{ all -> 0x124f }
            r3.append(r8)     // Catch:{ all -> 0x124f }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x124f }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x124f }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x124f }
            android.media.MediaExtractor r2 = r11.extractor
            if (r2 == 0) goto L_0x120a
            r2.release()
        L_0x120a:
            org.telegram.messenger.video.MP4Builder r2 = r11.mediaMuxer
            if (r2 == 0) goto L_0x121f
            r2.finishMovie()     // Catch:{ Exception -> 0x121a }
            org.telegram.messenger.video.MP4Builder r2 = r11.mediaMuxer     // Catch:{ Exception -> 0x121a }
            long r1 = r2.getLastFrameTimestamp(r1)     // Catch:{ Exception -> 0x121a }
            r11.endPresentationTime = r1     // Catch:{ Exception -> 0x121a }
            goto L_0x121f
        L_0x121a:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x121f:
            r10 = r4
            r15 = r38
            r13 = r47
            r6 = 1
        L_0x1225:
            if (r46 == 0) goto L_0x124e
            r20 = 1
            r1 = r70
            r2 = r71
            r3 = r72
            r4 = r73
            r5 = r74
            r6 = r8
            r8 = r77
            r9 = r10
            r10 = r79
            r11 = r80
            r17 = r86
            r19 = r88
            r21 = r90
            r22 = r91
            r23 = r92
            r24 = r93
            r25 = r94
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r13, r15, r17, r19, r20, r21, r22, r23, r24, r25)
            return r1
        L_0x124e:
            return r6
        L_0x124f:
            r0 = move-exception
        L_0x1250:
            r2 = r70
        L_0x1252:
            r3 = r0
        L_0x1253:
            android.media.MediaExtractor r4 = r2.extractor
            if (r4 == 0) goto L_0x125a
            r4.release()
        L_0x125a:
            org.telegram.messenger.video.MP4Builder r4 = r2.mediaMuxer
            if (r4 == 0) goto L_0x126f
            r4.finishMovie()     // Catch:{ Exception -> 0x126a }
            org.telegram.messenger.video.MP4Builder r4 = r2.mediaMuxer     // Catch:{ Exception -> 0x126a }
            long r4 = r4.getLastFrameTimestamp(r1)     // Catch:{ Exception -> 0x126a }
            r2.endPresentationTime = r4     // Catch:{ Exception -> 0x126a }
            goto L_0x126f
        L_0x126a:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x126f:
            goto L_0x1271
        L_0x1270:
            throw r3
        L_0x1271:
            goto L_0x1270
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, int, long, long, long, long, boolean, boolean, org.telegram.messenger.MediaController$SavedFilterState, java.lang.String, java.util.ArrayList, boolean, org.telegram.messenger.MediaController$CropState):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00ed, code lost:
        if (r13[r8 + 3] != 1) goto L_0x00f5;
     */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0194  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long readAndWriteTracks(android.media.MediaExtractor r28, org.telegram.messenger.video.MP4Builder r29, android.media.MediaCodec.BufferInfo r30, long r31, long r33, long r35, java.io.File r37, boolean r38) throws java.lang.Exception {
        /*
            r27 = this;
            r0 = r28
            r1 = r29
            r2 = r30
            r3 = r31
            r5 = 0
            int r6 = org.telegram.messenger.MediaController.findTrack(r0, r5)
            r8 = 1
            if (r38 == 0) goto L_0x0017
            int r9 = org.telegram.messenger.MediaController.findTrack(r0, r8)
            r10 = r35
            goto L_0x001a
        L_0x0017:
            r10 = r35
            r9 = -1
        L_0x001a:
            float r10 = (float) r10
            r11 = 1148846080(0x447a0000, float:1000.0)
            float r10 = r10 / r11
            java.lang.String r12 = "max-input-size"
            r13 = 0
            if (r6 < 0) goto L_0x003f
            r0.selectTrack(r6)
            android.media.MediaFormat r15 = r0.getTrackFormat(r6)
            int r16 = r1.addTrack(r15, r5)
            int r15 = r15.getInteger(r12)
            int r17 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r17 <= 0) goto L_0x003b
            r0.seekTo(r3, r5)
            goto L_0x0042
        L_0x003b:
            r0.seekTo(r13, r5)
            goto L_0x0042
        L_0x003f:
            r15 = 0
            r16 = -1
        L_0x0042:
            if (r9 < 0) goto L_0x0076
            r0.selectTrack(r9)
            android.media.MediaFormat r11 = r0.getTrackFormat(r9)
            java.lang.String r7 = "mime"
            java.lang.String r7 = r11.getString(r7)
            java.lang.String r5 = "audio/unknown"
            boolean r5 = r7.equals(r5)
            if (r5 == 0) goto L_0x005c
            r5 = -1
            r9 = -1
            goto L_0x0077
        L_0x005c:
            int r5 = r1.addTrack(r11, r8)
            int r7 = r11.getInteger(r12)
            int r15 = java.lang.Math.max(r7, r15)
            int r7 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r7 <= 0) goto L_0x0071
            r7 = 0
            r0.seekTo(r3, r7)
            goto L_0x0077
        L_0x0071:
            r7 = 0
            r0.seekTo(r13, r7)
            goto L_0x0077
        L_0x0076:
            r5 = -1
        L_0x0077:
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.allocateDirect(r15)
            r11 = -1
            if (r9 >= 0) goto L_0x0083
            if (r6 < 0) goto L_0x0082
            goto L_0x0083
        L_0x0082:
            return r11
        L_0x0083:
            r27.checkConversionCanceled()
            r18 = r11
            r20 = r13
            r15 = 0
        L_0x008b:
            if (r15 != 0) goto L_0x01bc
            r27.checkConversionCanceled()
            r11 = 0
            int r12 = r0.readSampleData(r7, r11)
            r2.size = r12
            int r11 = r28.getSampleTrackIndex()
            if (r11 != r6) goto L_0x00a1
            r12 = r16
        L_0x009f:
            r13 = -1
            goto L_0x00a7
        L_0x00a1:
            if (r11 != r9) goto L_0x00a5
            r12 = r5
            goto L_0x009f
        L_0x00a5:
            r12 = -1
            goto L_0x009f
        L_0x00a7:
            if (r12 == r13) goto L_0x019a
            int r13 = android.os.Build.VERSION.SDK_INT
            r14 = 21
            if (r13 >= r14) goto L_0x00b8
            r13 = 0
            r7.position(r13)
            int r13 = r2.size
            r7.limit(r13)
        L_0x00b8:
            if (r11 == r9) goto L_0x0125
            byte[] r13 = r7.array()
            if (r13 == 0) goto L_0x0125
            int r14 = r7.arrayOffset()
            int r24 = r7.limit()
            int r24 = r14 + r24
            r8 = r14
            r14 = -1
        L_0x00cc:
            r25 = 4
            r36 = r5
            int r5 = r24 + -4
            if (r8 > r5) goto L_0x0127
            byte r26 = r13[r8]
            if (r26 != 0) goto L_0x00f0
            int r26 = r8 + 1
            byte r26 = r13[r26]
            if (r26 != 0) goto L_0x00f0
            int r26 = r8 + 2
            byte r26 = r13[r26]
            if (r26 != 0) goto L_0x00f0
            int r26 = r8 + 3
            r38 = r15
            byte r15 = r13[r26]
            r26 = r9
            r9 = 1
            if (r15 == r9) goto L_0x00f7
            goto L_0x00f5
        L_0x00f0:
            r26 = r9
            r38 = r15
            r9 = 1
        L_0x00f5:
            if (r8 != r5) goto L_0x011c
        L_0x00f7:
            r15 = -1
            if (r14 == r15) goto L_0x011b
            int r15 = r8 - r14
            if (r8 == r5) goto L_0x00ff
            goto L_0x0101
        L_0x00ff:
            r25 = 0
        L_0x0101:
            int r15 = r15 - r25
            int r5 = r15 >> 24
            byte r5 = (byte) r5
            r13[r14] = r5
            int r5 = r14 + 1
            int r9 = r15 >> 16
            byte r9 = (byte) r9
            r13[r5] = r9
            int r5 = r14 + 2
            int r9 = r15 >> 8
            byte r9 = (byte) r9
            r13[r5] = r9
            int r14 = r14 + 3
            byte r5 = (byte) r15
            r13[r14] = r5
        L_0x011b:
            r14 = r8
        L_0x011c:
            int r8 = r8 + 1
            r5 = r36
            r15 = r38
            r9 = r26
            goto L_0x00cc
        L_0x0125:
            r36 = r5
        L_0x0127:
            r26 = r9
            r38 = r15
            int r5 = r2.size
            if (r5 < 0) goto L_0x0137
            long r8 = r28.getSampleTime()
            r2.presentationTimeUs = r8
            r5 = 0
            goto L_0x013b
        L_0x0137:
            r5 = 0
            r2.size = r5
            r5 = 1
        L_0x013b:
            int r8 = r2.size
            if (r8 <= 0) goto L_0x015f
            if (r5 != 0) goto L_0x015f
            r8 = 0
            if (r11 != r6) goto L_0x0153
            int r11 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r11 <= 0) goto L_0x0153
            r13 = -1
            int r11 = (r18 > r13 ? 1 : (r18 == r13 ? 0 : -1))
            if (r11 != 0) goto L_0x0153
            long r13 = r2.presentationTimeUs
            r18 = r13
        L_0x0153:
            int r11 = (r33 > r8 ? 1 : (r33 == r8 ? 0 : -1))
            if (r11 < 0) goto L_0x0162
            long r8 = r2.presentationTimeUs
            int r11 = (r8 > r33 ? 1 : (r8 == r33 ? 0 : -1))
            if (r11 >= 0) goto L_0x015e
            goto L_0x0162
        L_0x015e:
            r5 = 1
        L_0x015f:
            r14 = 1148846080(0x447a0000, float:1000.0)
            goto L_0x0192
        L_0x0162:
            r8 = 0
            r2.offset = r8
            int r9 = r28.getSampleFlags()
            r2.flags = r9
            long r11 = r1.writeSampleData(r12, r7, r2, r8)
            r13 = 0
            int r9 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r9 == 0) goto L_0x015f
            r9 = r27
            org.telegram.messenger.MediaController$VideoConvertorListener r15 = r9.callback
            if (r15 == 0) goto L_0x015f
            long r8 = r2.presentationTimeUs
            long r22 = r8 - r18
            int r24 = (r22 > r20 ? 1 : (r22 == r20 ? 0 : -1))
            if (r24 <= 0) goto L_0x0186
            long r8 = r8 - r18
            goto L_0x0188
        L_0x0186:
            r8 = r20
        L_0x0188:
            float r13 = (float) r8
            r14 = 1148846080(0x447a0000, float:1000.0)
            float r13 = r13 / r14
            float r13 = r13 / r10
            r15.didWriteData(r11, r13)
            r20 = r8
        L_0x0192:
            if (r5 != 0) goto L_0x0197
            r28.advance()
        L_0x0197:
            r8 = r5
            r5 = -1
            goto L_0x01ab
        L_0x019a:
            r36 = r5
            r26 = r9
            r38 = r15
            r5 = -1
            r14 = 1148846080(0x447a0000, float:1000.0)
            if (r11 != r5) goto L_0x01a7
            r8 = 1
            goto L_0x01ab
        L_0x01a7:
            r28.advance()
            r8 = 0
        L_0x01ab:
            if (r8 == 0) goto L_0x01af
            r15 = 1
            goto L_0x01b1
        L_0x01af:
            r15 = r38
        L_0x01b1:
            r5 = r36
            r9 = r26
            r8 = 1
            r11 = -1
            r13 = 0
            goto L_0x008b
        L_0x01bc:
            r26 = r9
            if (r6 < 0) goto L_0x01c3
            r0.unselectTrack(r6)
        L_0x01c3:
            if (r26 < 0) goto L_0x01ca
            r9 = r26
            r0.unselectTrack(r9)
        L_0x01ca:
            return r18
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
