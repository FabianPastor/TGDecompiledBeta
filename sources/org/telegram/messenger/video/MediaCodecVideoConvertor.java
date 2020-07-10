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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v0, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v1, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v2, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r30v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v3, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v4, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v5, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v40, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v6, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v7, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v8, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v45, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v9, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v49, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v10, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v29, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v11, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v30, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v53, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v12, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v13, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v14, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v56, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v15, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v16, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v60, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v61, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v62, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v65, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v17, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v67, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v68, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v69, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v70, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v79, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v80, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v41, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v18, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v84, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v84, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v91, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v71, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v72, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v73, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v19, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v140, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v92, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v146, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v95, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v96, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v97, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v98, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v99, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v100, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v101, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v157, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v102, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v103, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v159, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v104, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v161, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v105, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v164, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v106, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v165, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v167, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v168, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v107, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v108, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v173, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v109, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v110, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v111, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v112, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r35v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v175, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v180, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v181, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v183, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v185, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v186, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v187, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v189, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v190, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v247, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v115, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v116, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v117, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v118, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v126, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v129, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v130, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v131, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v193, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v135, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v143, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v144, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v145, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v146, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v147, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v194, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v148, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v197, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v152, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v154, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v199, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v159, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v161, resolved type: long} */
    /* JADX WARNING: type inference failed for: r4v68 */
    /* JADX WARNING: type inference failed for: r4v82 */
    /* JADX WARNING: type inference failed for: r3v83 */
    /* JADX WARNING: type inference failed for: r14v120 */
    /* JADX WARNING: type inference failed for: r4v172 */
    /* JADX WARNING: type inference failed for: r4v174 */
    /* JADX WARNING: Code restructure failed: missing block: B:1000:0x1169, code lost:
        r4 = r78;
        r7 = r79;
        r9 = r81;
        r3 = r59;
        r14 = r61;
        r54 = r83;
        r5 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1001:0x1177, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1002:0x1178, code lost:
        r9 = r81;
        r30 = r7;
        r62 = r24;
        r7 = r4;
        r4 = r14;
        r14 = r2;
        r54 = r83;
        r5 = r0;
        r59 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1003:0x1187, code lost:
        r6 = r7;
        r3 = r18;
        r35 = r30;
        r1 = 0;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1004:0x118f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1005:0x1190, code lost:
        r9 = r81;
        r30 = r7;
        r62 = r24;
        r7 = r4;
        r4 = r14;
        r54 = r83;
        r5 = r0;
        r59 = r1;
        r6 = r7;
        r3 = r18;
        r35 = r30;
        r1 = 0;
        r2 = -5;
        r14 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1006:0x11a6, code lost:
        r27 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1008:0x11ac, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1009:0x11ad, code lost:
        r9 = r81;
        r30 = r7;
        r62 = r24;
        r7 = r4;
        r4 = r14;
        r54 = r83;
        r5 = r0;
        r6 = r7;
        r3 = r18;
        r35 = r30;
        r1 = 0;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1010:0x11c0, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1011:0x11c1, code lost:
        r9 = r81;
        r4 = r14;
        r62 = r24;
        r54 = r83;
        r5 = r0;
        r3 = r18;
        r35 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1012:0x11d1, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1013:0x11d2, code lost:
        r9 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1014:0x11d5, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1015:0x11d6, code lost:
        r9 = r81;
        r30 = r7;
        r4 = r14;
        r62 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1016:0x11de, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1017:0x11df, code lost:
        r9 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1018:0x11e0, code lost:
        r30 = r7;
        r4 = r14;
        r62 = r24;
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1019:0x11e6, code lost:
        r54 = r83;
        r5 = r0;
        r35 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1020:0x11ec, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1021:0x11ed, code lost:
        r9 = r81;
        r62 = r4;
        r4 = r14;
        r1 = 0;
        r3 = r79;
        r54 = r83;
        r35 = r85;
        r5 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1043:0x125f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1044:0x1260, code lost:
        r3 = r0;
        r1 = r15;
        r2 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1047:0x126b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1048:0x126d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1049:0x126e, code lost:
        r11 = r76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1050:0x1271, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1051:0x1272, code lost:
        r11 = r76;
        r8 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1052:0x1276, code lost:
        r6 = r1;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1053:0x127a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1054:0x127b, code lost:
        r11 = r76;
        r8 = r77;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1059:0x129e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1060:0x129f, code lost:
        r1 = r0;
        r6 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1080:0x12e0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1081:0x12e1, code lost:
        r8 = r77;
        r4 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1082:0x12e5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1083:0x12e6, code lost:
        r3 = r0;
        r1 = r15;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1084:0x12eb, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1085:0x12ec, code lost:
        r4 = r10;
        r8 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1086:0x12ee, code lost:
        r11 = r12;
        r9 = r81;
        r3 = r79;
        r54 = r83;
        r35 = r85;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1087:0x12f9, code lost:
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1088:0x12fa, code lost:
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1093:0x132e, code lost:
        r1.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1097:?, code lost:
        r1.finishMovie();
        r15.endPresentationTime = r15.mediaMuxer.getLastFrameTimestamp(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1098:0x1341, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1099:0x1342, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1108:0x1380, code lost:
        r4.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1112:?, code lost:
        r4.finishMovie();
        r1.endPresentationTime = r1.mediaMuxer.getLastFrameTimestamp(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1113:0x1393, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1114:0x1394, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:163:0x031b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x0332, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x0333, code lost:
        r18 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x0373, code lost:
        r2 = java.nio.ByteBuffer.allocate(r3);
        r7 = java.nio.ByteBuffer.allocate(r14.size - r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x037e, code lost:
        r18 = r5;
        r17 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:?, code lost:
        r2.put(r13, 0, r3).position(0);
        r7.put(r13, r3, r14.size - r3).position(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x0394, code lost:
        r6 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x03a2, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x03a3, code lost:
        r18 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x03c7, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x03c9, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x03ca, code lost:
        r18 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x00a8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x03d7, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x03d8, code lost:
        r18 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x00a9, code lost:
        r1 = r0;
        r50 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x044b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x044c, code lost:
        r6 = r1;
        r2 = r5;
        r36 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x0452, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x0453, code lost:
        r3 = r0;
        r2 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x0483, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x0484, code lost:
        r18 = r3;
        r10 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x04c4, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x04cb, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x04cd, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x04ce, code lost:
        r18 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x04e1, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x04e2, code lost:
        r1 = r76;
        r3 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x04fc, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:273:0x04fd, code lost:
        r3 = r36;
        r6 = r76;
        r11 = r51;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x0506, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:275:0x0507, code lost:
        r3 = r36;
        r6 = r76;
        r11 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x050f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:277:0x0510, code lost:
        r50 = r9;
        r51 = r11;
        r6 = r1;
        r36 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0518, code lost:
        r2 = -5;
        r30 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x051c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x051d, code lost:
        r50 = r9;
        r51 = r11;
        r6 = r1;
        r2 = -5;
        r30 = null;
        r36 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0529, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x052a, code lost:
        r50 = r9;
        r51 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x052f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x0530, code lost:
        r50 = r9;
        r11 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x0534, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:287:0x0535, code lost:
        r2 = -5;
        r6 = null;
        r30 = null;
        r36 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x0541, code lost:
        r43 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x0589, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x058a, code lost:
        r2 = r3;
        r1 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x05bb, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x05bc, code lost:
        r54 = r83;
        r35 = r85;
        r1 = r0;
        r3 = r9;
        r4 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x05c4, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x05c6, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x05c7, code lost:
        r9 = r50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x05c9, code lost:
        r4 = r78;
        r54 = r83;
        r35 = r85;
        r1 = r0;
        r3 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x05d1, code lost:
        r8 = r11;
        r11 = r12;
        r6 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x05d6, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x05db, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x05dc, code lost:
        r4 = r78;
        r54 = r83;
        r35 = r85;
        r1 = r0;
        r3 = r50;
        r8 = r11;
        r11 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x05e8, code lost:
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x0619, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:339:0x061a, code lost:
        r8 = r77;
        r54 = r83;
        r35 = r85;
        r1 = r0;
        r3 = r9;
        r4 = r10;
        r11 = r12;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x0677, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x0678, code lost:
        r11 = r76;
        r8 = r77;
        r3 = r79;
        r9 = r81;
        r54 = r83;
        r35 = r85;
        r1 = r0;
        r4 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x0688, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x0689, code lost:
        r11 = r76;
        r8 = r77;
        r3 = r79;
        r54 = r83;
        r35 = r85;
        r1 = r0;
        r4 = r10;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x06db, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x06dc, code lost:
        r9 = r81;
        r54 = r83;
        r5 = r0;
        r62 = r4;
        r4 = r14;
        r35 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x070b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x070c, code lost:
        r9 = r81;
        r54 = r83;
        r5 = r0;
        r62 = r4;
        r35 = r7;
        r4 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x0730, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x0731, code lost:
        r54 = r83;
        r5 = r0;
        r9 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x07c8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x07c9, code lost:
        r9 = r81;
        r54 = r83;
        r5 = r0;
        r3 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x0860, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x0861, code lost:
        r6 = r79;
        r9 = r81;
        r54 = r83;
        r5 = r0;
        r14 = r3;
        r40 = null;
        r3 = r18;
        r35 = r30;
        r1 = 0;
        r2 = -5;
        r4 = r78;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x08e3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x08e4, code lost:
        r4 = r78;
        r6 = r79;
        r9 = r81;
        r54 = r83;
        r5 = r0;
        r14 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x0909, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x090a, code lost:
        r4 = r78;
        r6 = r79;
        r54 = r83;
        r5 = r0;
        r14 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x0931, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x0933, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x0934, code lost:
        r8 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x0936, code lost:
        r4 = r78;
        r6 = r79;
        r5 = r0;
        r14 = r3;
        r54 = r8;
        r9 = r10;
        r40 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x0943, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x0944, code lost:
        r4 = r78;
        r6 = r79;
        r5 = r0;
        r14 = r3;
        r54 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x095a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x095b, code lost:
        r4 = r78;
        r6 = r79;
        r5 = r0;
        r14 = r3;
        r54 = r83;
        r3 = r18;
        r35 = r30;
        r1 = 0;
        r2 = -5;
        r40 = null;
        r9 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x0182, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0183, code lost:
        r6 = r76;
        r1 = r0;
        r11 = r51;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x09c5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x09c6, code lost:
        r4 = r78;
        r6 = r79;
        r5 = r0;
        r14 = r3;
        r54 = r8;
        r9 = r10;
        r40 = r13;
        r2 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x0a31, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x0a32, code lost:
        r4 = r78;
        r40 = r85;
        r5 = r0;
        r14 = r3;
        r62 = r6;
        r54 = r8;
        r9 = r10;
        r2 = r17;
        r3 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x0a50, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x0a51, code lost:
        r4 = r78;
        r40 = r85;
        r5 = r0;
        r14 = r3;
        r62 = r6;
        r54 = r8;
        r9 = r10;
        r2 = r17;
        r3 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x0a60, code lost:
        r1 = 0;
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x0a61, code lost:
        r6 = r79;
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x0ab5, code lost:
        if (r13.presentationTimeUs < r8) goto L_0x0aba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x0af3, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x0af4, code lost:
        r60 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:627:0x0afc, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x0afd, code lost:
        r60 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x0aff, code lost:
        r4 = r78;
        r40 = r85;
        r5 = r0;
        r14 = r3;
        r62 = r6;
        r9 = r10;
        r2 = r17;
        r3 = r18;
        r54 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x0b10, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x0b11, code lost:
        r60 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0b46, code lost:
        r0 = e;
        r60 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0b47, code lost:
        r4 = r78;
        r40 = r85;
        r5 = r0;
        r14 = r3;
        r62 = r6;
        r9 = r10;
        r2 = r17;
        r3 = r18;
        r54 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x0b5a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x0b5b, code lost:
        r60 = r8;
        r4 = r78;
        r6 = r79;
        r40 = r13;
        r5 = r0;
        r14 = r3;
        r9 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x01aa, code lost:
        r1 = r2;
        r2 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r6;
        r6 = r7;
        r7 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x0d3f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x0d5d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x0d5e, code lost:
        r61 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x0d60, code lost:
        r4 = r78;
        r40 = r85;
        r5 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x0d66, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x0d67, code lost:
        r61 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x0e9a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x0e9b, code lost:
        r4 = r78;
        r9 = r81;
        r54 = r54;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x0200, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x0201, code lost:
        r6 = r1;
        r2 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x1047, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x1049, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:0x104a, code lost:
        r2 = r83;
        r40 = r85;
        r5 = r0;
        r59 = r3;
        r54 = r54;
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:0x109f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:964:0x10a0, code lost:
        r4 = r78;
        r9 = r10;
        r3 = r59;
        r14 = r61;
        r54 = r54;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:0x10f0, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:974:0x10f1, code lost:
        r40 = r85;
        r5 = r0;
        r59 = r3;
        r54 = r54;
        r14 = r14;
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:977:0x10fb, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:978:0x10fc, code lost:
        r3 = r0;
        r2 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:986:0x1114, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:987:0x1115, code lost:
        r4 = r78;
        r14 = r3;
        r60 = r8;
        r9 = r10;
        r3 = r59;
        r40 = r13;
        r5 = r0;
        r6 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:988:0x1125, code lost:
        r2 = r17;
        r3 = r18;
        r54 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:990:0x112e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x112f, code lost:
        r4 = r78;
        r14 = r3;
        r9 = r10;
        r3 = r59;
        r54 = r83;
        r40 = r13;
        r5 = r0;
        r6 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:992:0x113f, code lost:
        r3 = r18;
        r35 = r30;
        r1 = 0;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:993:0x1147, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:994:0x1148, code lost:
        r4 = r78;
        r7 = r79;
        r9 = r81;
        r14 = r3;
        r3 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:995:0x1152, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:996:0x1153, code lost:
        r4 = r78;
        r7 = r79;
        r9 = r81;
        r3 = r59;
        r14 = r61;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:997:0x115d, code lost:
        r54 = r83;
        r5 = r0;
        r6 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:999:0x1168, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:59:0x0198, B:1024:0x1203] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:59:0x0198, B:1029:0x120a] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:59:0x0198, B:1032:0x1226] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:59:0x0198, B:1035:0x1230] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:59:0x0198, B:1057:0x129a] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:59:0x0198, B:811:0x0e05] */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1041:0x124e A[Catch:{ Exception -> 0x1266, all -> 0x125f }] */
    /* JADX WARNING: Removed duplicated region for block: B:1043:0x125f A[ExcHandler: all (r0v10 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r17 
      PHI: (r17v1 int) = (r17v2 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int) binds: [B:1038:0x1245, B:535:0x09b5, B:536:?, B:546:0x09d9, B:581:0x0a72, B:582:?, B:587:0x0a8b, B:588:?, B:597:0x0aa5, B:598:?, B:609:0x0abb, B:610:?, B:612:0x0acb, B:616:0x0ad5, B:603:0x0ab1, B:604:?, B:594:0x0aa2, B:595:?, B:590:0x0a8f, B:591:?, B:585:0x0a83, B:586:?, B:551:0x09eb, B:560:0x0a03, B:564:0x0a15, B:539:0x09bc] A[DONT_GENERATE, DONT_INLINE], Splitter:B:535:0x09b5] */
    /* JADX WARNING: Removed duplicated region for block: B:1055:0x1282  */
    /* JADX WARNING: Removed duplicated region for block: B:1057:0x129a A[SYNTHETIC, Splitter:B:1057:0x129a] */
    /* JADX WARNING: Removed duplicated region for block: B:1063:0x12a7 A[Catch:{ Exception -> 0x129e, all -> 0x05d6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1065:0x12ac A[Catch:{ Exception -> 0x129e, all -> 0x05d6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1067:0x12b4 A[Catch:{ Exception -> 0x129e, all -> 0x05d6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1072:0x12c2  */
    /* JADX WARNING: Removed duplicated region for block: B:1075:0x12c9 A[SYNTHETIC, Splitter:B:1075:0x12c9] */
    /* JADX WARNING: Removed duplicated region for block: B:1082:0x12e5 A[ExcHandler: all (r0v6 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:1:0x0017] */
    /* JADX WARNING: Removed duplicated region for block: B:1093:0x132e  */
    /* JADX WARNING: Removed duplicated region for block: B:1096:0x1335 A[SYNTHETIC, Splitter:B:1096:0x1335] */
    /* JADX WARNING: Removed duplicated region for block: B:1102:0x134e  */
    /* JADX WARNING: Removed duplicated region for block: B:1108:0x1380  */
    /* JADX WARNING: Removed duplicated region for block: B:1111:0x1387 A[SYNTHETIC, Splitter:B:1111:0x1387] */
    /* JADX WARNING: Removed duplicated region for block: B:1158:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x03c9 A[ExcHandler: all (th java.lang.Throwable), PHI: r5 
      PHI: (r5v171 int) = (r5v170 int), (r5v170 int), (r5v172 int) binds: [B:174:0x0340, B:175:?, B:180:0x035d] A[DONT_GENERATE, DONT_INLINE], Splitter:B:174:0x0340] */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x03f4 A[Catch:{ Exception -> 0x047c, all -> 0x05d6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x03f6 A[Catch:{ Exception -> 0x047c, all -> 0x05d6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0402  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0420  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x0452 A[ExcHandler: all (r0v127 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:228:0x0422] */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x04c4 A[ExcHandler: all (th java.lang.Throwable), PHI: r18 
      PHI: (r18v20 int) = (r18v19 int), (r18v30 int), (r18v34 int), (r18v34 int), (r18v41 int), (r18v41 int) binds: [B:250:0x048d, B:199:0x03b1, B:191:0x0383, B:192:?, B:161:0x0317, B:162:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:161:0x0317] */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x04cd A[ExcHandler: all (th java.lang.Throwable), Splitter:B:65:0x01b2] */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x053f A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x0585 A[SYNTHETIC, Splitter:B:304:0x0585] */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x0589 A[Catch:{ Exception -> 0x058e, all -> 0x0589 }, ExcHandler: all (th java.lang.Throwable), PHI: r3 
      PHI: (r3v135 int) = (r3v136 int), (r3v158 int), (r3v158 int), (r3v158 int), (r3v158 int) binds: [B:304:0x0585, B:125:0x0291, B:111:0x0279, B:112:?, B:79:0x01e5] A[DONT_GENERATE, DONT_INLINE], Splitter:B:79:0x01e5] */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x059e A[Catch:{ Exception -> 0x058e, all -> 0x0589 }] */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x05a3 A[Catch:{ Exception -> 0x058e, all -> 0x0589 }] */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x05d6 A[ExcHandler: all (th java.lang.Throwable), PHI: r2 
      PHI: (r2v168 int) = (r2v20 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v94 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v187 int), (r2v187 int), (r2v215 int) binds: [B:1057:0x129a, B:1024:0x1203, B:1025:?, B:1029:0x120a, B:1030:?, B:1032:0x1226, B:1033:?, B:1035:0x1230, B:1036:?, B:811:0x0e05, B:288:0x053b, B:289:?, B:294:0x0546, B:295:?, B:297:0x0552, B:298:?, B:300:0x055c, B:301:?, B:59:0x0198, B:60:?, B:217:0x03ee] A[DONT_GENERATE, DONT_INLINE], Splitter:B:59:0x0198] */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x06a0  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x074b  */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x0769  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0773 A[SYNTHETIC, Splitter:B:423:0x0773] */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x07b1 A[SYNTHETIC, Splitter:B:429:0x07b1] */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x0850 A[SYNTHETIC, Splitter:B:462:0x0850] */
    /* JADX WARNING: Removed duplicated region for block: B:467:0x0875  */
    /* JADX WARNING: Removed duplicated region for block: B:469:0x087b A[SYNTHETIC, Splitter:B:469:0x087b] */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x08a9  */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x08ac  */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x0951  */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x0971  */
    /* JADX WARNING: Removed duplicated region for block: B:526:0x097b  */
    /* JADX WARNING: Removed duplicated region for block: B:527:0x097e  */
    /* JADX WARNING: Removed duplicated region for block: B:532:0x099e A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:546:0x09d9 A[SYNTHETIC, Splitter:B:546:0x09d9] */
    /* JADX WARNING: Removed duplicated region for block: B:635:0x0b26 A[Catch:{ Exception -> 0x0b46, all -> 0x125f }] */
    /* JADX WARNING: Removed duplicated region for block: B:641:0x0b37 A[Catch:{ Exception -> 0x0b46, all -> 0x125f }] */
    /* JADX WARNING: Removed duplicated region for block: B:645:0x0b58  */
    /* JADX WARNING: Removed duplicated region for block: B:648:0x0b6a  */
    /* JADX WARNING: Removed duplicated region for block: B:651:0x0b8f A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0bb3  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bbc  */
    /* JADX WARNING: Removed duplicated region for block: B:663:0x0bcf  */
    /* JADX WARNING: Removed duplicated region for block: B:665:0x0be7  */
    /* JADX WARNING: Removed duplicated region for block: B:763:0x0d33 A[Catch:{ Exception -> 0x0d5d, all -> 0x10fb }] */
    /* JADX WARNING: Removed duplicated region for block: B:776:0x0d52 A[Catch:{ Exception -> 0x0de5, all -> 0x10fb }] */
    /* JADX WARNING: Removed duplicated region for block: B:814:0x0e0b A[Catch:{ Exception -> 0x109f, all -> 0x05d6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:815:0x0e0d A[Catch:{ Exception -> 0x109f, all -> 0x05d6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:819:0x0e1a  */
    /* JADX WARNING: Removed duplicated region for block: B:820:0x0e39  */
    /* JADX WARNING: Removed duplicated region for block: B:887:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:888:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:895:0x0var_ A[SYNTHETIC, Splitter:B:895:0x0var_] */
    /* JADX WARNING: Removed duplicated region for block: B:900:0x0fa6 A[Catch:{ Exception -> 0x0var_, all -> 0x1047 }] */
    /* JADX WARNING: Removed duplicated region for block: B:906:0x0fb7 A[Catch:{ Exception -> 0x0var_, all -> 0x1047 }] */
    /* JADX WARNING: Removed duplicated region for block: B:907:0x0fba A[Catch:{ Exception -> 0x0var_, all -> 0x1047 }] */
    /* JADX WARNING: Removed duplicated region for block: B:915:0x0fcf  */
    /* JADX WARNING: Removed duplicated region for block: B:934:0x1003 A[Catch:{ Exception -> 0x1049, all -> 0x1047 }] */
    /* JADX WARNING: Removed duplicated region for block: B:937:0x100f A[Catch:{ Exception -> 0x1049, all -> 0x1047 }] */
    /* JADX WARNING: Removed duplicated region for block: B:947:0x1047 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:921:0x0fe1] */
    /* JADX WARNING: Removed duplicated region for block: B:977:0x10fb A[ExcHandler: all (r0v35 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r12 
      PHI: (r12v7 int) = (r12v3 int), (r12v3 int), (r12v3 int), (r12v8 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int) binds: [B:653:0x0bae, B:654:?, B:659:0x0bc8, B:832:0x0e7d, B:701:0x0c8f, B:702:?, B:966:0x10b0, B:714:0x0ca5, B:715:?, B:755:0x0d21, B:756:?, B:758:0x0d25, B:771:0x0d43, B:772:?, B:774:0x0d4c, B:766:0x0d3a, B:767:?, B:724:0x0cc0, B:733:0x0cdf, B:719:0x0cad, B:708:0x0c9a, B:705:0x0CLASSNAME, B:706:?, B:678:0x0c1f, B:684:0x0c2d, B:667:0x0bea] A[DONT_GENERATE, DONT_INLINE], Splitter:B:755:0x0d21] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:1024:0x1203=Splitter:B:1024:0x1203, B:811:0x0e05=Splitter:B:811:0x0e05, B:294:0x0546=Splitter:B:294:0x0546, B:1029:0x120a=Splitter:B:1029:0x120a, B:288:0x053b=Splitter:B:288:0x053b, B:217:0x03ee=Splitter:B:217:0x03ee} */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:528:0x0980=Splitter:B:528:0x0980, B:443:0x07d3=Splitter:B:443:0x07d3, B:31:0x00e5=Splitter:B:31:0x00e5, B:55:0x018c=Splitter:B:55:0x018c, B:477:0x089d=Splitter:B:477:0x089d, B:10:0x006e=Splitter:B:10:0x006e, B:420:0x076f=Splitter:B:420:0x076f} */
    @android.annotation.TargetApi(18)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean convertVideoInternal(java.lang.String r72, java.io.File r73, int r74, boolean r75, int r76, int r77, int r78, int r79, int r80, long r81, long r83, long r85, long r87, boolean r89, boolean r90, org.telegram.messenger.MediaController.SavedFilterState r91, java.lang.String r92, java.util.ArrayList<org.telegram.messenger.VideoEditedInfo.MediaEntity> r93, boolean r94, org.telegram.messenger.MediaController.CropState r95) {
        /*
            r71 = this;
            r15 = r71
            r13 = r72
            r14 = r74
            r12 = r76
            r11 = r77
            r10 = r78
            r9 = r79
            r8 = r80
            r6 = r81
            r4 = r87
            r3 = r95
            r6 = 0
            android.media.MediaCodec$BufferInfo r7 = new android.media.MediaCodec$BufferInfo     // Catch:{ Exception -> 0x12eb, all -> 0x12e5 }
            r7.<init>()     // Catch:{ Exception -> 0x12eb, all -> 0x12e5 }
            org.telegram.messenger.video.Mp4Movie r2 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ Exception -> 0x12eb, all -> 0x12e5 }
            r2.<init>()     // Catch:{ Exception -> 0x12eb, all -> 0x12e5 }
            r1 = r73
            r2.setCacheFile(r1)     // Catch:{ Exception -> 0x12eb, all -> 0x12e5 }
            r2.setRotation(r6)     // Catch:{ Exception -> 0x12eb, all -> 0x12e5 }
            r2.setSize(r12, r11)     // Catch:{ Exception -> 0x12eb, all -> 0x12e5 }
            org.telegram.messenger.video.MP4Builder r6 = new org.telegram.messenger.video.MP4Builder     // Catch:{ Exception -> 0x12eb, all -> 0x12e5 }
            r6.<init>()     // Catch:{ Exception -> 0x12eb, all -> 0x12e5 }
            r14 = r75
            org.telegram.messenger.video.MP4Builder r2 = r6.createMovie(r2, r14)     // Catch:{ Exception -> 0x12eb, all -> 0x12e5 }
            r15.mediaMuxer = r2     // Catch:{ Exception -> 0x12eb, all -> 0x12e5 }
            float r2 = (float) r4     // Catch:{ Exception -> 0x12eb, all -> 0x12e5 }
            r19 = 1148846080(0x447a0000, float:1000.0)
            float r20 = r2 / r19
            r21 = 1000(0x3e8, double:4.94E-321)
            long r1 = r4 * r21
            r15.endPresentationTime = r1     // Catch:{ Exception -> 0x12eb, all -> 0x12e5 }
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x12eb, all -> 0x12e5 }
            java.lang.String r6 = "csd-1"
            java.lang.String r1 = "csd-0"
            r21 = r6
            java.lang.String r6 = "prepend-sps-pps-to-idr-frames"
            r2 = 921600(0xe1000, float:1.291437E-39)
            r24 = r6
            r23 = r7
            java.lang.String r7 = "video/avc"
            r29 = r7
            r6 = 0
            if (r94 == 0) goto L_0x05ed
            int r31 = (r85 > r6 ? 1 : (r85 == r6 ? 0 : -1))
            if (r31 < 0) goto L_0x0069
            r2 = 1560000(0x17cdc0, float:2.186026E-39)
            r9 = 1560000(0x17cdc0, float:2.186026E-39)
            goto L_0x006e
        L_0x0069:
            if (r9 > 0) goto L_0x006e
            r9 = 921600(0xe1000, float:1.291437E-39)
        L_0x006e:
            int r2 = r12 % 16
            r31 = 1098907648(0x41800000, float:16.0)
            if (r2 == 0) goto L_0x00ae
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            if (r2 == 0) goto L_0x009d
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            r2.<init>()     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            java.lang.String r6 = "changing width from "
            r2.append(r6)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            r2.append(r12)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            float r6 = (float) r12     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            float r6 = r6 / r31
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
        L_0x009d:
            float r2 = (float) r12     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            float r2 = r2 / r31
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            int r2 = r2 * 16
            r12 = r2
            goto L_0x00ae
        L_0x00a8:
            r0 = move-exception
            r1 = r0
            r50 = r9
            goto L_0x0535
        L_0x00ae:
            int r2 = r11 % 16
            if (r2 == 0) goto L_0x00e5
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            if (r2 == 0) goto L_0x00db
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            r2.<init>()     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            java.lang.String r6 = "changing height from "
            r2.append(r6)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            r2.append(r11)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            float r6 = (float) r11     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            float r6 = r6 / r31
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
        L_0x00db:
            float r2 = (float) r11     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            float r2 = r2 / r31
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            int r2 = r2 * 16
            r11 = r2
        L_0x00e5:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0529, all -> 0x12e5 }
            if (r2 == 0) goto L_0x010d
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            r2.<init>()     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            java.lang.String r6 = "create photo encoder "
            r2.append(r6)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            r2.append(r12)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            java.lang.String r6 = " "
            r2.append(r6)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            r2.append(r11)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            java.lang.String r6 = " duration = "
            r2.append(r6)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            r2.append(r4)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00a8, all -> 0x12e5 }
        L_0x010d:
            r7 = r29
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r7, r12, r11)     // Catch:{ Exception -> 0x0529, all -> 0x12e5 }
            java.lang.String r6 = "color-format"
            r29 = r1
            r1 = 2130708361(0x7var_, float:1.701803E38)
            r2.setInteger(r6, r1)     // Catch:{ Exception -> 0x0529, all -> 0x12e5 }
            java.lang.String r1 = "bitrate"
            r2.setInteger(r1, r9)     // Catch:{ Exception -> 0x0529, all -> 0x12e5 }
            java.lang.String r1 = "frame-rate"
            r2.setInteger(r1, r10)     // Catch:{ Exception -> 0x0529, all -> 0x12e5 }
            java.lang.String r1 = "i-frame-interval"
            r6 = 2
            r2.setInteger(r1, r6)     // Catch:{ Exception -> 0x0529, all -> 0x12e5 }
            android.media.MediaCodec r1 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x0529, all -> 0x12e5 }
            r3 = 1
            r6 = 0
            r1.configure(r2, r6, r6, r3)     // Catch:{ Exception -> 0x051c, all -> 0x12e5 }
            org.telegram.messenger.video.InputSurface r2 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x051c, all -> 0x12e5 }
            android.view.Surface r3 = r1.createInputSurface()     // Catch:{ Exception -> 0x051c, all -> 0x12e5 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x051c, all -> 0x12e5 }
            r2.makeCurrent()     // Catch:{ Exception -> 0x050f, all -> 0x12e5 }
            r1.start()     // Catch:{ Exception -> 0x050f, all -> 0x12e5 }
            org.telegram.messenger.video.OutputSurface r30 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x050f, all -> 0x12e5 }
            r31 = 0
            float r3 = (float) r10
            r34 = 1
            r76 = r1
            r35 = r29
            r1 = r30
            r36 = r2
            r2 = r91
            r16 = r3
            r3 = r72
            r4 = r92
            r5 = r93
            r44 = r21
            r45 = r24
            r14 = 21
            r6 = r31
            r49 = r7
            r48 = r23
            r7 = r12
            r8 = r11
            r50 = r9
            r9 = r74
            r10 = r16
            r51 = r11
            r11 = r34
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0506, all -> 0x12e5 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x04fc, all -> 0x12e5 }
            if (r1 >= r14) goto L_0x018b
            java.nio.ByteBuffer[] r6 = r76.getOutputBuffers()     // Catch:{ Exception -> 0x0182, all -> 0x12e5 }
            goto L_0x018c
        L_0x0182:
            r0 = move-exception
            r6 = r76
            r1 = r0
            r11 = r51
            r2 = -5
            goto L_0x053b
        L_0x018b:
            r6 = 0
        L_0x018c:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x04fc, all -> 0x12e5 }
            r4 = r6
            r1 = 1
            r2 = -5
            r3 = 0
            r5 = 0
            r6 = 0
            r7 = 0
        L_0x0196:
            if (r6 != 0) goto L_0x04ea
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x04e1, all -> 0x05d6 }
            r8 = r3 ^ 1
            r9 = r7
            r7 = r6
            r6 = r5
            r5 = r4
            r4 = r3
            r3 = r2
            r2 = r1
            r1 = 1
        L_0x01a5:
            if (r8 != 0) goto L_0x01b2
            if (r1 == 0) goto L_0x01aa
            goto L_0x01b2
        L_0x01aa:
            r1 = r2
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r9
            goto L_0x0196
        L_0x01b2:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x04d6, all -> 0x04cd }
            if (r90 == 0) goto L_0x01bc
            r10 = 22000(0x55f0, double:1.08694E-319)
            r14 = r48
            goto L_0x01c0
        L_0x01bc:
            r14 = r48
            r10 = 2500(0x9c4, double:1.235E-320)
        L_0x01c0:
            r69 = r1
            r1 = r76
            r76 = r69
            int r10 = r1.dequeueOutputBuffer(r14, r10)     // Catch:{ Exception -> 0x04cb, all -> 0x04cd }
            r11 = -1
            if (r10 != r11) goto L_0x01e2
            r17 = r2
            r16 = r6
            r79 = r8
            r8 = r35
            r11 = r44
            r13 = r49
            r76 = 0
        L_0x01db:
            r2 = -1
            r6 = r5
            r5 = r3
            r3 = r51
            goto L_0x0400
        L_0x01e2:
            r11 = -3
            if (r10 != r11) goto L_0x0205
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            r77 = r7
            r7 = 21
            if (r11 >= r7) goto L_0x01f1
            java.nio.ByteBuffer[] r5 = r1.getOutputBuffers()     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
        L_0x01f1:
            r7 = r77
            r17 = r2
            r16 = r6
            r79 = r8
            r8 = r35
            r11 = r44
        L_0x01fd:
            r13 = r49
            goto L_0x01db
        L_0x0200:
            r0 = move-exception
            r6 = r1
            r2 = r3
            goto L_0x04e7
        L_0x0205:
            r77 = r7
            r7 = -2
            if (r10 != r7) goto L_0x026b
            android.media.MediaFormat r7 = r1.getOutputFormat()     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            if (r11 == 0) goto L_0x0229
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            r11.<init>()     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            r79 = r8
            java.lang.String r8 = "photo encoder new format "
            r11.append(r8)     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            r11.append(r7)     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            java.lang.String r8 = r11.toString()     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            org.telegram.messenger.FileLog.d(r8)     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            goto L_0x022b
        L_0x0229:
            r79 = r8
        L_0x022b:
            r8 = -5
            if (r3 != r8) goto L_0x0260
            if (r7 == 0) goto L_0x0260
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            r8 = 0
            int r3 = r11.addTrack(r7, r8)     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            r11 = r45
            boolean r16 = r7.containsKey(r11)     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            if (r16 == 0) goto L_0x025e
            int r8 = r7.getInteger(r11)     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            r45 = r11
            r11 = 1
            if (r8 != r11) goto L_0x0260
            r8 = r35
            java.nio.ByteBuffer r6 = r7.getByteBuffer(r8)     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            r11 = r44
            java.nio.ByteBuffer r7 = r7.getByteBuffer(r11)     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            int r6 = r6.limit()     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            int r7 = r7.limit()     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            int r6 = r6 + r7
            goto L_0x0264
        L_0x025e:
            r45 = r11
        L_0x0260:
            r8 = r35
            r11 = r44
        L_0x0264:
            r7 = r77
            r17 = r2
            r16 = r6
            goto L_0x01fd
        L_0x026b:
            r79 = r8
            r8 = r35
            r11 = r44
            if (r10 < 0) goto L_0x04a9
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x04cb, all -> 0x04cd }
            r13 = 21
            if (r7 >= r13) goto L_0x027c
            r7 = r5[r10]     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            goto L_0x0280
        L_0x027c:
            java.nio.ByteBuffer r7 = r1.getOutputBuffer(r10)     // Catch:{ Exception -> 0x04cb, all -> 0x04cd }
        L_0x0280:
            if (r7 == 0) goto L_0x0489
            int r13 = r14.size     // Catch:{ Exception -> 0x0483, all -> 0x04cd }
            r77 = r5
            r5 = 1
            if (r13 <= r5) goto L_0x03e2
            int r13 = r14.flags     // Catch:{ Exception -> 0x03d7, all -> 0x04cd }
            r5 = 2
            r13 = r13 & r5
            if (r13 != 0) goto L_0x0337
            if (r6 == 0) goto L_0x02a3
            int r13 = r14.flags     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            r16 = 1
            r13 = r13 & 1
            if (r13 == 0) goto L_0x02a3
            int r13 = r14.offset     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            int r13 = r13 + r6
            r14.offset = r13     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            int r13 = r14.size     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            int r13 = r13 - r6
            r14.size = r13     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
        L_0x02a3:
            if (r2 == 0) goto L_0x02f8
            int r13 = r14.flags     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            r16 = 1
            r13 = r13 & 1
            if (r13 == 0) goto L_0x02f8
            int r2 = r14.size     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            r13 = 100
            if (r2 <= r13) goto L_0x02f7
            int r2 = r14.offset     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            r7.position(r2)     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            byte[] r2 = new byte[r13]     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            r7.get(r2)     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            r13 = 0
            r16 = 0
        L_0x02c0:
            r5 = 96
            if (r13 >= r5) goto L_0x02f7
            byte r5 = r2[r13]     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            if (r5 != 0) goto L_0x02ef
            int r5 = r13 + 1
            byte r5 = r2[r5]     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            if (r5 != 0) goto L_0x02ef
            int r5 = r13 + 2
            byte r5 = r2[r5]     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            if (r5 != 0) goto L_0x02ef
            int r5 = r13 + 3
            byte r5 = r2[r5]     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            r17 = r2
            r2 = 1
            if (r5 != r2) goto L_0x02f1
            int r5 = r16 + 1
            if (r5 <= r2) goto L_0x02ec
            int r2 = r14.offset     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            int r2 = r2 + r13
            r14.offset = r2     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            int r2 = r14.size     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            int r2 = r2 - r13
            r14.size = r2     // Catch:{ Exception -> 0x0200, all -> 0x0589 }
            goto L_0x02f7
        L_0x02ec:
            r16 = r5
            goto L_0x02f1
        L_0x02ef:
            r17 = r2
        L_0x02f1:
            int r13 = r13 + 1
            r2 = r17
            r5 = 2
            goto L_0x02c0
        L_0x02f7:
            r2 = 0
        L_0x02f8:
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x0332, all -> 0x04cd }
            r16 = r6
            r13 = 1
            long r5 = r5.writeSampleData(r3, r7, r14, r13)     // Catch:{ Exception -> 0x0332, all -> 0x04cd }
            r17 = r2
            r13 = r3
            r2 = 0
            int r7 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r7 == 0) goto L_0x0328
            org.telegram.messenger.MediaController$VideoConvertorListener r7 = r15.callback     // Catch:{ Exception -> 0x0323, all -> 0x031e }
            if (r7 == 0) goto L_0x0328
            org.telegram.messenger.MediaController$VideoConvertorListener r7 = r15.callback     // Catch:{ Exception -> 0x0323, all -> 0x031e }
            r18 = r13
            float r13 = (float) r2
            float r13 = r13 / r19
            float r13 = r13 / r20
            r7.didWriteData(r5, r13)     // Catch:{ Exception -> 0x031b, all -> 0x04c4 }
            goto L_0x032a
        L_0x031b:
            r0 = move-exception
            goto L_0x04dd
        L_0x031e:
            r0 = move-exception
            r18 = r13
            goto L_0x04d0
        L_0x0323:
            r0 = move-exception
            r18 = r13
            goto L_0x04dd
        L_0x0328:
            r18 = r13
        L_0x032a:
            r2 = r18
            r13 = r49
            r3 = r51
            goto L_0x03ee
        L_0x0332:
            r0 = move-exception
            r18 = r3
            goto L_0x04dd
        L_0x0337:
            r5 = r3
            r16 = r6
            r13 = -5
            r6 = r2
            r2 = 0
            if (r5 != r13) goto L_0x03d2
            int r13 = r14.size     // Catch:{ Exception -> 0x03ce, all -> 0x03c9 }
            byte[] r13 = new byte[r13]     // Catch:{ Exception -> 0x03ce, all -> 0x03c9 }
            int r2 = r14.offset     // Catch:{ Exception -> 0x03ce, all -> 0x03c9 }
            int r3 = r14.size     // Catch:{ Exception -> 0x03ce, all -> 0x03c9 }
            int r2 = r2 + r3
            r7.limit(r2)     // Catch:{ Exception -> 0x03ce, all -> 0x03c9 }
            int r2 = r14.offset     // Catch:{ Exception -> 0x03ce, all -> 0x03c9 }
            r7.position(r2)     // Catch:{ Exception -> 0x03ce, all -> 0x03c9 }
            r7.get(r13)     // Catch:{ Exception -> 0x03ce, all -> 0x03c9 }
            int r2 = r14.size     // Catch:{ Exception -> 0x03ce, all -> 0x03c9 }
            r3 = 1
            int r2 = r2 - r3
        L_0x0358:
            if (r2 < 0) goto L_0x03a7
            r7 = 3
            if (r2 <= r7) goto L_0x03a7
            byte r7 = r13[r2]     // Catch:{ Exception -> 0x03a2, all -> 0x03c9 }
            if (r7 != r3) goto L_0x0396
            int r3 = r2 + -1
            byte r3 = r13[r3]     // Catch:{ Exception -> 0x03a2, all -> 0x03c9 }
            if (r3 != 0) goto L_0x0396
            int r3 = r2 + -2
            byte r3 = r13[r3]     // Catch:{ Exception -> 0x03a2, all -> 0x03c9 }
            if (r3 != 0) goto L_0x0396
            int r3 = r2 + -3
            byte r7 = r13[r3]     // Catch:{ Exception -> 0x03a2, all -> 0x03c9 }
            if (r7 != 0) goto L_0x0396
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r3)     // Catch:{ Exception -> 0x03a2, all -> 0x03c9 }
            int r7 = r14.size     // Catch:{ Exception -> 0x03a2, all -> 0x03c9 }
            int r7 = r7 - r3
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.allocate(r7)     // Catch:{ Exception -> 0x03a2, all -> 0x03c9 }
            r18 = r5
            r17 = r6
            r5 = 0
            java.nio.ByteBuffer r6 = r2.put(r13, r5, r3)     // Catch:{ Exception -> 0x031b, all -> 0x04c4 }
            r6.position(r5)     // Catch:{ Exception -> 0x031b, all -> 0x04c4 }
            int r6 = r14.size     // Catch:{ Exception -> 0x031b, all -> 0x04c4 }
            int r6 = r6 - r3
            java.nio.ByteBuffer r3 = r7.put(r13, r3, r6)     // Catch:{ Exception -> 0x031b, all -> 0x04c4 }
            r3.position(r5)     // Catch:{ Exception -> 0x031b, all -> 0x04c4 }
            r6 = r2
            goto L_0x03ad
        L_0x0396:
            r18 = r5
            r17 = r6
            int r2 = r2 + -1
            r6 = r17
            r5 = r18
            r3 = 1
            goto L_0x0358
        L_0x03a2:
            r0 = move-exception
            r18 = r5
            goto L_0x04dd
        L_0x03a7:
            r18 = r5
            r17 = r6
            r6 = 0
            r7 = 0
        L_0x03ad:
            r13 = r49
            r3 = r51
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r13, r12, r3)     // Catch:{ Exception -> 0x03c7, all -> 0x04c4 }
            if (r6 == 0) goto L_0x03bf
            if (r7 == 0) goto L_0x03bf
            r2.setByteBuffer(r8, r6)     // Catch:{ Exception -> 0x03c7, all -> 0x04c4 }
            r2.setByteBuffer(r11, r7)     // Catch:{ Exception -> 0x03c7, all -> 0x04c4 }
        L_0x03bf:
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x03c7, all -> 0x04c4 }
            r6 = 0
            int r2 = r5.addTrack(r2, r6)     // Catch:{ Exception -> 0x03c7, all -> 0x04c4 }
            goto L_0x03ee
        L_0x03c7:
            r0 = move-exception
            goto L_0x03dc
        L_0x03c9:
            r0 = move-exception
            r18 = r5
            goto L_0x04d0
        L_0x03ce:
            r0 = move-exception
            r18 = r5
            goto L_0x03da
        L_0x03d2:
            r18 = r5
            r17 = r6
            goto L_0x03e8
        L_0x03d7:
            r0 = move-exception
            r18 = r3
        L_0x03da:
            r3 = r51
        L_0x03dc:
            r6 = r1
            r11 = r3
            r2 = r18
            goto L_0x0527
        L_0x03e2:
            r17 = r2
            r18 = r3
            r16 = r6
        L_0x03e8:
            r13 = r49
            r3 = r51
            r2 = r18
        L_0x03ee:
            int r5 = r14.flags     // Catch:{ Exception -> 0x047c, all -> 0x05d6 }
            r5 = r5 & 4
            if (r5 == 0) goto L_0x03f6
            r5 = 1
            goto L_0x03f7
        L_0x03f6:
            r5 = 0
        L_0x03f7:
            r6 = 0
            r1.releaseOutputBuffer(r10, r6)     // Catch:{ Exception -> 0x047c, all -> 0x05d6 }
            r6 = r77
            r7 = r5
            r5 = r2
            r2 = -1
        L_0x0400:
            if (r10 == r2) goto L_0x0420
            r51 = r3
            r3 = r5
            r5 = r6
            r35 = r8
            r44 = r11
            r49 = r13
            r48 = r14
            r6 = r16
            r2 = r17
            r14 = 21
            r13 = r72
            r8 = r79
        L_0x0418:
            r69 = r1
            r1 = r76
            r76 = r69
            goto L_0x01a5
        L_0x0420:
            if (r4 != 0) goto L_0x0460
            r30.drawImage()     // Catch:{ Exception -> 0x0457, all -> 0x0452 }
            float r2 = (float) r9
            r10 = 1106247680(0x41var_, float:30.0)
            float r2 = r2 / r10
            float r2 = r2 * r19
            float r2 = r2 * r19
            float r2 = r2 * r19
            r51 = r3
            long r2 = (long) r2
            r10 = r36
            r10.setPresentationTime(r2)     // Catch:{ Exception -> 0x044b, all -> 0x0452 }
            r10.swapBuffers()     // Catch:{ Exception -> 0x044b, all -> 0x0452 }
            int r9 = r9 + 1
            float r2 = (float) r9     // Catch:{ Exception -> 0x044b, all -> 0x0452 }
            r3 = 1106247680(0x41var_, float:30.0)
            float r3 = r3 * r20
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 < 0) goto L_0x0464
            r1.signalEndOfInputStream()     // Catch:{ Exception -> 0x044b, all -> 0x0452 }
            r2 = 0
            r4 = 1
            goto L_0x0466
        L_0x044b:
            r0 = move-exception
            r6 = r1
            r2 = r5
            r36 = r10
            goto L_0x04e7
        L_0x0452:
            r0 = move-exception
            r3 = r0
            r2 = r5
            goto L_0x05d8
        L_0x0457:
            r0 = move-exception
            r51 = r3
            r10 = r36
            r6 = r1
            r2 = r5
            goto L_0x04e7
        L_0x0460:
            r51 = r3
            r10 = r36
        L_0x0464:
            r2 = r79
        L_0x0466:
            r3 = r5
            r5 = r6
            r35 = r8
            r36 = r10
            r44 = r11
            r49 = r13
            r48 = r14
            r6 = r16
            r14 = 21
            r13 = r72
            r8 = r2
            r2 = r17
            goto L_0x0418
        L_0x047c:
            r0 = move-exception
            r51 = r3
            r10 = r36
            goto L_0x04e6
        L_0x0483:
            r0 = move-exception
            r18 = r3
            r10 = r36
            goto L_0x04dd
        L_0x0489:
            r18 = r3
            r3 = r36
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x04c6, all -> 0x04c4 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04c6, all -> 0x04c4 }
            r4.<init>()     // Catch:{ Exception -> 0x04c6, all -> 0x04c4 }
            java.lang.String r5 = "encoderOutputBuffer "
            r4.append(r5)     // Catch:{ Exception -> 0x04c6, all -> 0x04c4 }
            r4.append(r10)     // Catch:{ Exception -> 0x04c6, all -> 0x04c4 }
            java.lang.String r5 = " was null"
            r4.append(r5)     // Catch:{ Exception -> 0x04c6, all -> 0x04c4 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x04c6, all -> 0x04c4 }
            r2.<init>(r4)     // Catch:{ Exception -> 0x04c6, all -> 0x04c4 }
            throw r2     // Catch:{ Exception -> 0x04c6, all -> 0x04c4 }
        L_0x04a9:
            r18 = r3
            r3 = r36
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x04c6, all -> 0x04c4 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04c6, all -> 0x04c4 }
            r4.<init>()     // Catch:{ Exception -> 0x04c6, all -> 0x04c4 }
            java.lang.String r5 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x04c6, all -> 0x04c4 }
            r4.append(r10)     // Catch:{ Exception -> 0x04c6, all -> 0x04c4 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x04c6, all -> 0x04c4 }
            r2.<init>(r4)     // Catch:{ Exception -> 0x04c6, all -> 0x04c4 }
            throw r2     // Catch:{ Exception -> 0x04c6, all -> 0x04c4 }
        L_0x04c4:
            r0 = move-exception
            goto L_0x04d0
        L_0x04c6:
            r0 = move-exception
            r6 = r1
            r36 = r3
            goto L_0x04de
        L_0x04cb:
            r0 = move-exception
            goto L_0x04d9
        L_0x04cd:
            r0 = move-exception
            r18 = r3
        L_0x04d0:
            r3 = r0
            r1 = r15
            r2 = r18
            goto L_0x137c
        L_0x04d6:
            r0 = move-exception
            r1 = r76
        L_0x04d9:
            r18 = r3
            r3 = r36
        L_0x04dd:
            r6 = r1
        L_0x04de:
            r2 = r18
            goto L_0x04e7
        L_0x04e1:
            r0 = move-exception
            r1 = r76
            r3 = r36
        L_0x04e6:
            r6 = r1
        L_0x04e7:
            r11 = r51
            goto L_0x0527
        L_0x04ea:
            r1 = r76
            r3 = r36
            r10 = r78
            r9 = r50
            r6 = 0
            r43 = 0
            r69 = r3
            r3 = r2
            r2 = r69
            goto L_0x0583
        L_0x04fc:
            r0 = move-exception
            r1 = r76
            r3 = r36
            r6 = r1
            r11 = r51
            r2 = -5
            goto L_0x0527
        L_0x0506:
            r0 = move-exception
            r1 = r76
            r3 = r36
            r6 = r1
            r11 = r51
            goto L_0x0518
        L_0x050f:
            r0 = move-exception
            r3 = r2
            r50 = r9
            r51 = r11
            r6 = r1
            r36 = r3
        L_0x0518:
            r2 = -5
            r30 = 0
            goto L_0x0527
        L_0x051c:
            r0 = move-exception
            r50 = r9
            r51 = r11
            r6 = r1
            r2 = -5
            r30 = 0
            r36 = 0
        L_0x0527:
            r1 = r0
            goto L_0x053b
        L_0x0529:
            r0 = move-exception
            r50 = r9
            r51 = r11
            goto L_0x0534
        L_0x052f:
            r0 = move-exception
            r50 = r9
            r11 = r77
        L_0x0534:
            r1 = r0
        L_0x0535:
            r2 = -5
            r6 = 0
            r30 = 0
            r36 = 0
        L_0x053b:
            boolean r3 = r1 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x05db, all -> 0x05d6 }
            if (r3 == 0) goto L_0x0544
            if (r90 != 0) goto L_0x0544
            r43 = 1
            goto L_0x0546
        L_0x0544:
            r43 = 0
        L_0x0546:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05c6, all -> 0x05d6 }
            r3.<init>()     // Catch:{ Exception -> 0x05c6, all -> 0x05d6 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ Exception -> 0x05c6, all -> 0x05d6 }
            r9 = r50
            r3.append(r9)     // Catch:{ Exception -> 0x05c4, all -> 0x05d6 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ Exception -> 0x05c4, all -> 0x05d6 }
            r10 = r78
            r3.append(r10)     // Catch:{ Exception -> 0x05bb, all -> 0x05d6 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ Exception -> 0x05bb, all -> 0x05d6 }
            r3.append(r11)     // Catch:{ Exception -> 0x05bb, all -> 0x05d6 }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ Exception -> 0x05bb, all -> 0x05d6 }
            r3.append(r12)     // Catch:{ Exception -> 0x05bb, all -> 0x05d6 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x05bb, all -> 0x05d6 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ Exception -> 0x05bb, all -> 0x05d6 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x05bb, all -> 0x05d6 }
            r3 = r2
            r1 = r6
            r51 = r11
            r2 = r36
            r6 = r43
            r43 = 1
        L_0x0583:
            if (r30 == 0) goto L_0x059c
            r30.release()     // Catch:{ Exception -> 0x058e, all -> 0x0589 }
            goto L_0x059c
        L_0x0589:
            r0 = move-exception
            r2 = r3
            r1 = r15
            goto L_0x137b
        L_0x058e:
            r0 = move-exception
            r54 = r83
            r35 = r85
            r1 = r0
            r2 = r3
            r3 = r9
            r4 = r10
            r11 = r12
            r8 = r51
            goto L_0x05e9
        L_0x059c:
            if (r2 == 0) goto L_0x05a1
            r2.release()     // Catch:{ Exception -> 0x058e, all -> 0x0589 }
        L_0x05a1:
            if (r1 == 0) goto L_0x05a9
            r1.stop()     // Catch:{ Exception -> 0x058e, all -> 0x0589 }
            r1.release()     // Catch:{ Exception -> 0x058e, all -> 0x0589 }
        L_0x05a9:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x058e, all -> 0x0589 }
            r54 = r83
            r35 = r85
            r2 = r3
            r18 = r9
            r4 = r10
            r11 = r12
            r1 = r43
            r9 = r81
            goto L_0x12be
        L_0x05bb:
            r0 = move-exception
            r54 = r83
            r35 = r85
            r1 = r0
            r3 = r9
            r4 = r10
            goto L_0x05d1
        L_0x05c4:
            r0 = move-exception
            goto L_0x05c9
        L_0x05c6:
            r0 = move-exception
            r9 = r50
        L_0x05c9:
            r4 = r78
            r54 = r83
            r35 = r85
            r1 = r0
            r3 = r9
        L_0x05d1:
            r8 = r11
            r11 = r12
            r6 = r43
            goto L_0x05e9
        L_0x05d6:
            r0 = move-exception
        L_0x05d7:
            r3 = r0
        L_0x05d8:
            r1 = r15
            goto L_0x137c
        L_0x05db:
            r0 = move-exception
            r9 = r50
            r4 = r78
            r54 = r83
            r35 = r85
            r1 = r0
            r3 = r9
            r8 = r11
            r11 = r12
        L_0x05e8:
            r6 = 0
        L_0x05e9:
            r9 = r81
            goto L_0x12fb
        L_0x05ed:
            r8 = r1
            r11 = r21
            r14 = r23
            r7 = r24
            r13 = r29
            r1 = 921600(0xe1000, float:1.291437E-39)
            android.media.MediaExtractor r2 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x12e0, all -> 0x12e5 }
            r2.<init>()     // Catch:{ Exception -> 0x12e0, all -> 0x12e5 }
            r15.extractor = r2     // Catch:{ Exception -> 0x12e0, all -> 0x12e5 }
            r5 = r72
            r2.setDataSource(r5)     // Catch:{ Exception -> 0x12e0, all -> 0x12e5 }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x12e0, all -> 0x12e5 }
            r6 = 0
            int r4 = org.telegram.messenger.MediaController.findTrack(r2, r6)     // Catch:{ Exception -> 0x12e0, all -> 0x12e5 }
            r2 = -1
            if (r9 == r2) goto L_0x0626
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0619, all -> 0x12e5 }
            r3 = 1
            int r2 = org.telegram.messenger.MediaController.findTrack(r2, r3)     // Catch:{ Exception -> 0x0619, all -> 0x12e5 }
            r44 = r11
            goto L_0x062a
        L_0x0619:
            r0 = move-exception
            r8 = r77
            r54 = r83
            r35 = r85
            r1 = r0
            r3 = r9
            r4 = r10
            r11 = r12
            r2 = -5
            goto L_0x05e9
        L_0x0626:
            r3 = 1
            r44 = r11
            r2 = -1
        L_0x062a:
            java.lang.String r11 = "mime"
            if (r4 < 0) goto L_0x0640
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x0619, all -> 0x12e5 }
            android.media.MediaFormat r3 = r3.getTrackFormat(r4)     // Catch:{ Exception -> 0x0619, all -> 0x12e5 }
            java.lang.String r3 = r3.getString(r11)     // Catch:{ Exception -> 0x0619, all -> 0x12e5 }
            boolean r3 = r3.equals(r13)     // Catch:{ Exception -> 0x0619, all -> 0x12e5 }
            if (r3 != 0) goto L_0x0640
            r3 = 1
            goto L_0x0641
        L_0x0640:
            r3 = 0
        L_0x0641:
            if (r89 != 0) goto L_0x0698
            if (r3 == 0) goto L_0x0647
            goto L_0x0698
        L_0x0647:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0688, all -> 0x12e5 }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ Exception -> 0x0688, all -> 0x12e5 }
            r1 = -1
            if (r9 == r1) goto L_0x0650
            r13 = 1
            goto L_0x0651
        L_0x0650:
            r13 = 0
        L_0x0651:
            r1 = r71
            r11 = 1
            r4 = r14
            r14 = r5
            r7 = 0
            r5 = r81
            r14 = 0
            r7 = r83
            r14 = r10
            r9 = r87
            r11 = r73
            r12 = r13
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ Exception -> 0x0677, all -> 0x12e5 }
            r11 = r76
            r51 = r77
            r18 = r79
            r9 = r81
            r54 = r83
            r35 = r85
            r4 = r14
            r1 = 0
            r2 = -5
            r6 = 0
            goto L_0x12be
        L_0x0677:
            r0 = move-exception
            r11 = r76
            r8 = r77
            r3 = r79
            r9 = r81
            r54 = r83
            r35 = r85
            r1 = r0
            r4 = r14
            goto L_0x12f9
        L_0x0688:
            r0 = move-exception
            r11 = r76
            r8 = r77
            r3 = r79
            r54 = r83
            r35 = r85
            r1 = r0
            r4 = r10
            r2 = -5
            goto L_0x05e8
        L_0x0698:
            r12 = r5
            r69 = r14
            r14 = r10
            r10 = r69
            if (r4 < 0) goto L_0x1282
            r16 = -2147483648(0xfffffffvar_, double:NaN)
            r3 = 1000(0x3e8, float:1.401E-42)
            r21 = -1
            int r3 = r3 / r14
            int r3 = r3 * 1000
            long r5 = (long) r3     // Catch:{ Exception -> 0x11ec, all -> 0x12e5 }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x11ec, all -> 0x12e5 }
            r3.selectTrack(r4)     // Catch:{ Exception -> 0x11ec, all -> 0x12e5 }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x11ec, all -> 0x12e5 }
            android.media.MediaFormat r9 = r3.getTrackFormat(r4)     // Catch:{ Exception -> 0x11ec, all -> 0x12e5 }
            r23 = 0
            int r3 = (r85 > r23 ? 1 : (r85 == r23 ? 0 : -1))
            if (r3 < 0) goto L_0x06c4
            r3 = 1560000(0x17cdc0, float:2.186026E-39)
            r1 = r80
            r23 = 0
            goto L_0x06d4
        L_0x06c4:
            if (r79 > 0) goto L_0x06ce
            r1 = r80
            r23 = r85
            r3 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x06d4
        L_0x06ce:
            r3 = r79
            r1 = r80
            r23 = r85
        L_0x06d4:
            if (r1 <= 0) goto L_0x06e9
            int r3 = java.lang.Math.min(r1, r3)     // Catch:{ Exception -> 0x06db, all -> 0x12e5 }
            goto L_0x06e9
        L_0x06db:
            r0 = move-exception
            r9 = r81
            r54 = r83
            r5 = r0
            r62 = r4
            r4 = r14
            r35 = r23
        L_0x06e6:
            r1 = 0
            goto L_0x11fa
        L_0x06e9:
            r25 = 0
            int r27 = (r23 > r25 ? 1 : (r23 == r25 ? 0 : -1))
            r45 = r7
            r35 = r8
            if (r27 < 0) goto L_0x06f6
            r7 = r21
            goto L_0x06f8
        L_0x06f6:
            r7 = r23
        L_0x06f8:
            int r23 = (r7 > r25 ? 1 : (r7 == r25 ? 0 : -1))
            if (r23 < 0) goto L_0x0717
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x070b, all -> 0x12e5 }
            r23 = r2
            r2 = 0
            r1.seekTo(r7, r2)     // Catch:{ Exception -> 0x070b, all -> 0x12e5 }
            r24 = r4
            r25 = r5
            r5 = 0
            goto L_0x0747
        L_0x070b:
            r0 = move-exception
            r9 = r81
            r54 = r83
            r5 = r0
            r62 = r4
            r35 = r7
            r4 = r14
            goto L_0x06e6
        L_0x0717:
            r23 = r2
            r24 = 0
            r1 = r81
            int r26 = (r1 > r24 ? 1 : (r1 == r24 ? 0 : -1))
            if (r26 <= 0) goto L_0x073b
            r24 = r4
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x0730, all -> 0x12e5 }
            r25 = r5
            r5 = 0
            r4.seekTo(r1, r5)     // Catch:{ Exception -> 0x0730, all -> 0x12e5 }
            r4 = r95
            r5 = 0
            goto L_0x0749
        L_0x0730:
            r0 = move-exception
            r54 = r83
            r5 = r0
            r9 = r1
        L_0x0735:
            r35 = r7
            r4 = r14
            r62 = r24
            goto L_0x06e6
        L_0x073b:
            r24 = r4
            r25 = r5
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x11de, all -> 0x12e5 }
            r1 = 0
            r5 = 0
            r4.seekTo(r5, r1)     // Catch:{ Exception -> 0x11d5, all -> 0x12e5 }
        L_0x0747:
            r4 = r95
        L_0x0749:
            if (r4 == 0) goto L_0x0769
            r1 = 90
            r2 = r74
            if (r2 == r1) goto L_0x075b
            r1 = 270(0x10e, float:3.78E-43)
            if (r2 != r1) goto L_0x0756
            goto L_0x075b
        L_0x0756:
            int r1 = r4.transformWidth     // Catch:{ Exception -> 0x0762, all -> 0x12e5 }
            int r5 = r4.transformHeight     // Catch:{ Exception -> 0x0762, all -> 0x12e5 }
            goto L_0x075f
        L_0x075b:
            int r1 = r4.transformHeight     // Catch:{ Exception -> 0x0762, all -> 0x12e5 }
            int r5 = r4.transformWidth     // Catch:{ Exception -> 0x0762, all -> 0x12e5 }
        L_0x075f:
            r6 = r5
            r5 = r1
            goto L_0x076f
        L_0x0762:
            r0 = move-exception
            r9 = r81
            r54 = r83
            r5 = r0
            goto L_0x0735
        L_0x0769:
            r2 = r74
            r5 = r76
            r6 = r77
        L_0x076f:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x11d1, all -> 0x12e5 }
            if (r1 == 0) goto L_0x078f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0762, all -> 0x12e5 }
            r1.<init>()     // Catch:{ Exception -> 0x0762, all -> 0x12e5 }
            java.lang.String r2 = "create encoder with w = "
            r1.append(r2)     // Catch:{ Exception -> 0x0762, all -> 0x12e5 }
            r1.append(r5)     // Catch:{ Exception -> 0x0762, all -> 0x12e5 }
            java.lang.String r2 = " h = "
            r1.append(r2)     // Catch:{ Exception -> 0x0762, all -> 0x12e5 }
            r1.append(r6)     // Catch:{ Exception -> 0x0762, all -> 0x12e5 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0762, all -> 0x12e5 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0762, all -> 0x12e5 }
        L_0x078f:
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r13, r5, r6)     // Catch:{ Exception -> 0x11d1, all -> 0x12e5 }
            java.lang.String r2 = "color-format"
            r4 = 2130708361(0x7var_, float:1.701803E38)
            r1.setInteger(r2, r4)     // Catch:{ Exception -> 0x11d1, all -> 0x12e5 }
            java.lang.String r2 = "bitrate"
            r1.setInteger(r2, r3)     // Catch:{ Exception -> 0x11d1, all -> 0x12e5 }
            java.lang.String r2 = "frame-rate"
            r1.setInteger(r2, r14)     // Catch:{ Exception -> 0x11d1, all -> 0x12e5 }
            java.lang.String r2 = "i-frame-interval"
            r4 = 2
            r1.setInteger(r2, r4)     // Catch:{ Exception -> 0x11d1, all -> 0x12e5 }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x11d1, all -> 0x12e5 }
            r4 = 23
            if (r2 >= r4) goto L_0x07d1
            int r2 = java.lang.Math.min(r6, r5)     // Catch:{ Exception -> 0x0762, all -> 0x12e5 }
            r4 = 480(0x1e0, float:6.73E-43)
            if (r2 > r4) goto L_0x07d1
            r2 = 921600(0xe1000, float:1.291437E-39)
            if (r3 <= r2) goto L_0x07bf
            goto L_0x07c0
        L_0x07bf:
            r2 = r3
        L_0x07c0:
            java.lang.String r3 = "bitrate"
            r1.setInteger(r3, r2)     // Catch:{ Exception -> 0x07c8, all -> 0x12e5 }
            r18 = r2
            goto L_0x07d3
        L_0x07c8:
            r0 = move-exception
            r9 = r81
            r54 = r83
            r5 = r0
            r3 = r2
            goto L_0x0735
        L_0x07d1:
            r18 = r3
        L_0x07d3:
            android.media.MediaCodec r4 = android.media.MediaCodec.createEncoderByType(r13)     // Catch:{ Exception -> 0x11c0, all -> 0x12e5 }
            r2 = 1
            r3 = 0
            r4.configure(r1, r3, r3, r2)     // Catch:{ Exception -> 0x11ac, all -> 0x12e5 }
            org.telegram.messenger.video.InputSurface r1 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x11ac, all -> 0x12e5 }
            android.view.Surface r2 = r4.createInputSurface()     // Catch:{ Exception -> 0x11ac, all -> 0x12e5 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x11ac, all -> 0x12e5 }
            r1.makeCurrent()     // Catch:{ Exception -> 0x118f, all -> 0x12e5 }
            r4.start()     // Catch:{ Exception -> 0x118f, all -> 0x12e5 }
            java.lang.String r2 = r9.getString(r11)     // Catch:{ Exception -> 0x118f, all -> 0x12e5 }
            android.media.MediaCodec r2 = android.media.MediaCodec.createDecoderByType(r2)     // Catch:{ Exception -> 0x118f, all -> 0x12e5 }
            org.telegram.messenger.video.OutputSurface r27 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x1177, all -> 0x12e5 }
            r28 = 0
            float r3 = (float) r14
            r29 = 0
            r59 = r1
            r1 = r27
            r61 = r2
            r60 = r23
            r2 = r91
            r23 = r3
            r3 = r28
            r79 = r4
            r62 = r24
            r24 = 2
            r4 = r92
            r63 = r5
            r24 = r25
            r26 = 2
            r5 = r93
            r64 = r6
            r6 = r95
            r30 = r7
            r8 = r45
            r7 = r76
            r66 = r8
            r65 = r35
            r8 = r77
            r67 = r9
            r9 = r74
            r68 = r10
            r10 = r23
            r49 = r13
            r14 = r44
            r13 = r11
            r11 = r29
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x1168, all -> 0x12e5 }
            android.view.Surface r1 = r27.getSurface()     // Catch:{ Exception -> 0x1152, all -> 0x12e5 }
            r3 = r61
            r2 = r67
            r4 = 0
            r5 = 0
            r3.configure(r2, r1, r4, r5)     // Catch:{ Exception -> 0x1147, all -> 0x12e5 }
            r3.start()     // Catch:{ Exception -> 0x1147, all -> 0x12e5 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1147, all -> 0x12e5 }
            r2 = 21
            if (r1 >= r2) goto L_0x0875
            java.nio.ByteBuffer[] r6 = r3.getInputBuffers()     // Catch:{ Exception -> 0x0860, all -> 0x12e5 }
            java.nio.ByteBuffer[] r1 = r79.getOutputBuffers()     // Catch:{ Exception -> 0x0860, all -> 0x12e5 }
            r2 = r60
            r69 = r6
            r6 = r1
            r1 = r69
            goto L_0x0879
        L_0x0860:
            r0 = move-exception
            r6 = r79
            r9 = r81
            r54 = r83
            r5 = r0
            r14 = r3
            r40 = r4
            r3 = r18
            r35 = r30
            r1 = 0
            r2 = -5
            r4 = r78
            goto L_0x1203
        L_0x0875:
            r1 = r4
            r6 = r1
            r2 = r60
        L_0x0879:
            if (r2 < 0) goto L_0x0971
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x095a, all -> 0x12e5 }
            android.media.MediaFormat r5 = r5.getTrackFormat(r2)     // Catch:{ Exception -> 0x095a, all -> 0x12e5 }
            java.lang.String r7 = r5.getString(r13)     // Catch:{ Exception -> 0x095a, all -> 0x12e5 }
            java.lang.String r8 = "audio/mp4a-latm"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x095a, all -> 0x12e5 }
            if (r7 != 0) goto L_0x089c
            java.lang.String r7 = r5.getString(r13)     // Catch:{ Exception -> 0x0860, all -> 0x12e5 }
            java.lang.String r8 = "audio/mpeg"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x0860, all -> 0x12e5 }
            if (r7 == 0) goto L_0x089a
            goto L_0x089c
        L_0x089a:
            r7 = 0
            goto L_0x089d
        L_0x089c:
            r7 = 1
        L_0x089d:
            java.lang.String r8 = r5.getString(r13)     // Catch:{ Exception -> 0x095a, all -> 0x12e5 }
            java.lang.String r9 = "audio/unknown"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x095a, all -> 0x12e5 }
            if (r8 == 0) goto L_0x08aa
            r2 = -1
        L_0x08aa:
            if (r2 < 0) goto L_0x0951
            if (r7 == 0) goto L_0x08f0
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x08e3, all -> 0x12e5 }
            r9 = 1
            int r8 = r8.addTrack(r5, r9)     // Catch:{ Exception -> 0x08e3, all -> 0x12e5 }
            android.media.MediaExtractor r10 = r15.extractor     // Catch:{ Exception -> 0x08e3, all -> 0x12e5 }
            r10.selectTrack(r2)     // Catch:{ Exception -> 0x08e3, all -> 0x12e5 }
            java.lang.String r10 = "max-input-size"
            int r5 = r5.getInteger(r10)     // Catch:{ Exception -> 0x08e3, all -> 0x12e5 }
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocateDirect(r5)     // Catch:{ Exception -> 0x08e3, all -> 0x12e5 }
            r10 = r81
            r85 = r5
            r4 = 0
            int r13 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r13 <= 0) goto L_0x08d5
            android.media.MediaExtractor r13 = r15.extractor     // Catch:{ Exception -> 0x0909, all -> 0x12e5 }
            r9 = 0
            r13.seekTo(r10, r9)     // Catch:{ Exception -> 0x0909, all -> 0x12e5 }
            goto L_0x08db
        L_0x08d5:
            android.media.MediaExtractor r9 = r15.extractor     // Catch:{ Exception -> 0x0909, all -> 0x12e5 }
            r13 = 0
            r9.seekTo(r4, r13)     // Catch:{ Exception -> 0x0909, all -> 0x12e5 }
        L_0x08db:
            r5 = r85
            r4 = r8
            r13 = 0
            r8 = r83
            goto L_0x0979
        L_0x08e3:
            r0 = move-exception
            r4 = r78
            r6 = r79
            r9 = r81
            r54 = r83
            r5 = r0
            r14 = r3
            goto L_0x1161
        L_0x08f0:
            r10 = r81
            r8 = r5
            r4 = 0
            android.media.MediaExtractor r9 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0943, all -> 0x12e5 }
            r9.<init>()     // Catch:{ Exception -> 0x0943, all -> 0x12e5 }
            r9.setDataSource(r12)     // Catch:{ Exception -> 0x0943, all -> 0x12e5 }
            r9.selectTrack(r2)     // Catch:{ Exception -> 0x0943, all -> 0x12e5 }
            int r13 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r13 <= 0) goto L_0x0913
            r13 = 0
            r9.seekTo(r10, r13)     // Catch:{ Exception -> 0x0909, all -> 0x12e5 }
            goto L_0x0917
        L_0x0909:
            r0 = move-exception
            r4 = r78
            r6 = r79
            r54 = r83
            r5 = r0
            r14 = r3
            goto L_0x094e
        L_0x0913:
            r13 = 0
            r9.seekTo(r4, r13)     // Catch:{ Exception -> 0x0943, all -> 0x12e5 }
        L_0x0917:
            org.telegram.messenger.video.AudioRecoder r13 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x0943, all -> 0x12e5 }
            r13.<init>(r8, r9, r2)     // Catch:{ Exception -> 0x0943, all -> 0x12e5 }
            r13.startTime = r10     // Catch:{ Exception -> 0x0933, all -> 0x12e5 }
            r8 = r83
            r13.endTime = r8     // Catch:{ Exception -> 0x0931, all -> 0x12e5 }
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x0931, all -> 0x12e5 }
            android.media.MediaFormat r5 = r13.format     // Catch:{ Exception -> 0x0931, all -> 0x12e5 }
            r85 = r2
            r2 = 1
            int r4 = r4.addTrack(r5, r2)     // Catch:{ Exception -> 0x0931, all -> 0x12e5 }
            r2 = r85
            r5 = 0
            goto L_0x0979
        L_0x0931:
            r0 = move-exception
            goto L_0x0936
        L_0x0933:
            r0 = move-exception
            r8 = r83
        L_0x0936:
            r4 = r78
            r6 = r79
            r5 = r0
            r14 = r3
            r54 = r8
            r9 = r10
            r40 = r13
            goto L_0x113f
        L_0x0943:
            r0 = move-exception
            r8 = r83
            r4 = r78
            r6 = r79
            r5 = r0
            r14 = r3
            r54 = r8
        L_0x094e:
            r9 = r10
            goto L_0x1161
        L_0x0951:
            r10 = r81
            r8 = r83
            r85 = r2
            r4 = -5
            r5 = 0
            goto L_0x0978
        L_0x095a:
            r0 = move-exception
            r8 = r83
            r4 = r78
            r6 = r79
            r5 = r0
            r14 = r3
            r54 = r8
            r3 = r18
            r35 = r30
            r1 = 0
            r2 = -5
            r40 = 0
            r9 = r81
            goto L_0x1203
        L_0x0971:
            r10 = r81
            r8 = r83
            r4 = -5
            r5 = 0
            r7 = 1
        L_0x0978:
            r13 = 0
        L_0x0979:
            if (r2 >= 0) goto L_0x097e
            r23 = 1
            goto L_0x0980
        L_0x097e:
            r23 = 0
        L_0x0980:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x112e, all -> 0x12e5 }
            r29 = r6
            r44 = r16
            r46 = r21
            r37 = r23
            r35 = r30
            r6 = 0
            r16 = 1
            r17 = -5
            r23 = 0
            r28 = 0
            r30 = 0
            r31 = 0
            r33 = 0
        L_0x099c:
            if (r23 == 0) goto L_0x09b5
            if (r7 != 0) goto L_0x09a3
            if (r37 != 0) goto L_0x09a3
            goto L_0x09b5
        L_0x09a3:
            r4 = r78
            r2 = r3
            r54 = r8
            r9 = r10
            r40 = r13
            r1 = 0
            r6 = 0
            r11 = r76
            r8 = r77
            r3 = r79
            goto L_0x1245
        L_0x09b5:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x1114, all -> 0x125f }
            if (r7 != 0) goto L_0x09d7
            if (r13 == 0) goto L_0x09d7
            org.telegram.messenger.video.MP4Builder r12 = r15.mediaMuxer     // Catch:{ Exception -> 0x09c5, all -> 0x125f }
            boolean r12 = r13.step(r12, r4)     // Catch:{ Exception -> 0x09c5, all -> 0x125f }
            r37 = r12
            goto L_0x09d7
        L_0x09c5:
            r0 = move-exception
            r4 = r78
            r6 = r79
            r5 = r0
            r14 = r3
            r54 = r8
            r9 = r10
            r40 = r13
            r2 = r17
        L_0x09d3:
            r3 = r18
            goto L_0x112b
        L_0x09d7:
            if (r6 != 0) goto L_0x0b6a
            android.media.MediaExtractor r12 = r15.extractor     // Catch:{ Exception -> 0x0b5a, all -> 0x125f }
            int r12 = r12.getSampleTrackIndex()     // Catch:{ Exception -> 0x0b5a, all -> 0x125f }
            r83 = r6
            r6 = r62
            if (r12 != r6) goto L_0x0a65
            r85 = r13
            r48 = r14
            r13 = 2500(0x9c4, double:1.235E-320)
            int r12 = r3.dequeueInputBuffer(r13)     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            if (r12 < 0) goto L_0x0a42
            int r13 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            r14 = 21
            if (r13 >= r14) goto L_0x09fa
            r13 = r1[r12]     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            goto L_0x09fe
        L_0x09fa:
            java.nio.ByteBuffer r13 = r3.getInputBuffer(r12)     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
        L_0x09fe:
            android.media.MediaExtractor r14 = r15.extractor     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            r86 = r1
            r1 = 0
            int r55 = r14.readSampleData(r13, r1)     // Catch:{ Exception -> 0x0a31, all -> 0x125f }
            if (r55 >= 0) goto L_0x0a1a
            r54 = 0
            r55 = 0
            r56 = 0
            r58 = 4
            r52 = r3
            r53 = r12
            r52.queueInputBuffer(r53, r54, r55, r56, r58)     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            r1 = 1
            goto L_0x0a46
        L_0x0a1a:
            r54 = 0
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            long r56 = r1.getSampleTime()     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            r58 = 0
            r52 = r3
            r53 = r12
            r52.queueInputBuffer(r53, r54, r55, r56, r58)     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            r1.advance()     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            goto L_0x0a44
        L_0x0a31:
            r0 = move-exception
            r4 = r78
            r40 = r85
            r5 = r0
            r14 = r3
            r62 = r6
            r54 = r8
            r9 = r10
            r2 = r17
            r3 = r18
            goto L_0x0a61
        L_0x0a42:
            r86 = r1
        L_0x0a44:
            r1 = r83
        L_0x0a46:
            r14 = r2
            r50 = r7
            r60 = r8
            r13 = r68
            r2 = r1
            goto L_0x0b2c
        L_0x0a50:
            r0 = move-exception
            r4 = r78
            r40 = r85
            r5 = r0
            r14 = r3
            r62 = r6
            r54 = r8
            r9 = r10
            r2 = r17
            r3 = r18
        L_0x0a60:
            r1 = 0
        L_0x0a61:
            r6 = r79
            goto L_0x1203
        L_0x0a65:
            r86 = r1
            r85 = r13
            r48 = r14
            if (r7 == 0) goto L_0x0b1c
            r1 = -1
            if (r2 == r1) goto L_0x0b14
            if (r12 != r2) goto L_0x0b1c
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0b10, all -> 0x125f }
            r12 = 0
            int r1 = r1.readSampleData(r5, r12)     // Catch:{ Exception -> 0x0b10, all -> 0x125f }
            r13 = r68
            r13.size = r1     // Catch:{ Exception -> 0x0b10, all -> 0x125f }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b10, all -> 0x125f }
            r14 = 21
            if (r1 >= r14) goto L_0x0a8b
            r5.position(r12)     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            int r1 = r13.size     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            r5.limit(r1)     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
        L_0x0a8b:
            int r1 = r13.size     // Catch:{ Exception -> 0x0b10, all -> 0x125f }
            if (r1 < 0) goto L_0x0aa0
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            r14 = r2
            long r1 = r1.getSampleTime()     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            r13.presentationTimeUs = r1     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            r1.advance()     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            r1 = r83
            goto L_0x0aa5
        L_0x0aa0:
            r14 = r2
            r1 = 0
            r13.size = r1     // Catch:{ Exception -> 0x0afc, all -> 0x125f }
            r1 = 1
        L_0x0aa5:
            int r2 = r13.size     // Catch:{ Exception -> 0x0b10, all -> 0x125f }
            if (r2 <= 0) goto L_0x0af5
            r41 = 0
            int r2 = (r8 > r41 ? 1 : (r8 == r41 ? 0 : -1))
            if (r2 < 0) goto L_0x0ab8
            r83 = r1
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0a50, all -> 0x125f }
            int r12 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r12 >= 0) goto L_0x0af7
            goto L_0x0aba
        L_0x0ab8:
            r83 = r1
        L_0x0aba:
            r1 = 0
            r13.offset = r1     // Catch:{ Exception -> 0x0afc, all -> 0x125f }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0afc, all -> 0x125f }
            int r2 = r2.getSampleFlags()     // Catch:{ Exception -> 0x0afc, all -> 0x125f }
            r13.flags = r2     // Catch:{ Exception -> 0x0afc, all -> 0x125f }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0afc, all -> 0x125f }
            r50 = r7
            r60 = r8
            long r7 = r2.writeSampleData(r4, r5, r13, r1)     // Catch:{ Exception -> 0x0af3, all -> 0x125f }
            r1 = 0
            int r9 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
            if (r9 == 0) goto L_0x0b2a
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r15.callback     // Catch:{ Exception -> 0x0b46, all -> 0x125f }
            if (r1 == 0) goto L_0x0b2a
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0b46, all -> 0x125f }
            long r1 = r1 - r10
            int r9 = (r1 > r31 ? 1 : (r1 == r31 ? 0 : -1))
            if (r9 <= 0) goto L_0x0ae4
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0b46, all -> 0x125f }
            long r31 = r1 - r10
        L_0x0ae4:
            r1 = r31
            org.telegram.messenger.MediaController$VideoConvertorListener r9 = r15.callback     // Catch:{ Exception -> 0x0b46, all -> 0x125f }
            float r12 = (float) r1     // Catch:{ Exception -> 0x0b46, all -> 0x125f }
            float r12 = r12 / r19
            float r12 = r12 / r20
            r9.didWriteData(r7, r12)     // Catch:{ Exception -> 0x0b46, all -> 0x125f }
            r31 = r1
            goto L_0x0b2a
        L_0x0af3:
            r0 = move-exception
            goto L_0x0aff
        L_0x0af5:
            r83 = r1
        L_0x0af7:
            r50 = r7
            r60 = r8
            goto L_0x0b2a
        L_0x0afc:
            r0 = move-exception
            r60 = r8
        L_0x0aff:
            r4 = r78
            r40 = r85
            r5 = r0
            r14 = r3
            r62 = r6
            r9 = r10
            r2 = r17
            r3 = r18
            r54 = r60
            goto L_0x0a61
        L_0x0b10:
            r0 = move-exception
            r60 = r8
            goto L_0x0b47
        L_0x0b14:
            r14 = r2
            r50 = r7
            r60 = r8
            r13 = r68
            goto L_0x0b24
        L_0x0b1c:
            r14 = r2
            r50 = r7
            r60 = r8
            r13 = r68
            r1 = -1
        L_0x0b24:
            if (r12 != r1) goto L_0x0b2a
            r2 = r83
            r1 = 1
            goto L_0x0b2d
        L_0x0b2a:
            r2 = r83
        L_0x0b2c:
            r1 = 0
        L_0x0b2d:
            if (r1 == 0) goto L_0x0b58
            r7 = 2500(0x9c4, double:1.235E-320)
            int r53 = r3.dequeueInputBuffer(r7)     // Catch:{ Exception -> 0x0b46, all -> 0x125f }
            if (r53 < 0) goto L_0x0b58
            r54 = 0
            r55 = 0
            r56 = 0
            r58 = 4
            r52 = r3
            r52.queueInputBuffer(r53, r54, r55, r56, r58)     // Catch:{ Exception -> 0x0b46, all -> 0x125f }
            r1 = 1
            goto L_0x0b7d
        L_0x0b46:
            r0 = move-exception
        L_0x0b47:
            r4 = r78
            r40 = r85
            r5 = r0
            r14 = r3
            r62 = r6
            r9 = r10
            r2 = r17
            r3 = r18
            r54 = r60
            goto L_0x0a60
        L_0x0b58:
            r1 = r2
            goto L_0x0b7d
        L_0x0b5a:
            r0 = move-exception
            r60 = r8
            r85 = r13
            r4 = r78
            r6 = r79
            r40 = r85
            r5 = r0
            r14 = r3
            r9 = r10
            goto L_0x1125
        L_0x0b6a:
            r86 = r1
            r83 = r6
            r50 = r7
            r60 = r8
            r85 = r13
            r48 = r14
            r6 = r62
            r13 = r68
            r14 = r2
            r1 = r83
        L_0x0b7d:
            r2 = r28 ^ 1
            r7 = r1
            r12 = r17
            r8 = r60
            r1 = 1
            r69 = r44
            r44 = r4
            r45 = r5
            r4 = r69
        L_0x0b8d:
            if (r2 != 0) goto L_0x0bae
            if (r1 == 0) goto L_0x0b92
            goto L_0x0bae
        L_0x0b92:
            r1 = r86
            r62 = r6
            r6 = r7
            r17 = r12
            r68 = r13
            r2 = r14
            r14 = r48
            r7 = r50
            r12 = r72
            r13 = r85
            r69 = r4
            r4 = r44
            r5 = r45
            r44 = r69
            goto L_0x099c
        L_0x0bae:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x1100, all -> 0x10fb }
            if (r90 == 0) goto L_0x0bbc
            r52 = 22000(0x55f0, double:1.08694E-319)
            r83 = r1
            r84 = r2
            r1 = r52
            goto L_0x0bc2
        L_0x0bbc:
            r83 = r1
            r84 = r2
            r1 = 2500(0x9c4, double:1.235E-320)
        L_0x0bc2:
            r69 = r7
            r7 = r79
            r79 = r69
            int r1 = r7.dequeueOutputBuffer(r13, r1)     // Catch:{ Exception -> 0x10f7, all -> 0x10fb }
            r2 = -1
            if (r1 != r2) goto L_0x0be7
            r61 = r3
            r52 = r4
            r62 = r6
            r54 = r8
            r51 = r14
            r4 = r48
            r8 = r49
            r3 = r63
            r5 = r64
            r6 = r65
            r2 = 0
        L_0x0be4:
            r9 = -1
            goto L_0x0e18
        L_0x0be7:
            r2 = -3
            if (r1 != r2) goto L_0x0c1a
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c0b, all -> 0x10fb }
            r51 = r14
            r14 = 21
            if (r2 >= r14) goto L_0x0bf6
            java.nio.ByteBuffer[] r29 = r7.getOutputBuffers()     // Catch:{ Exception -> 0x0c0b, all -> 0x10fb }
        L_0x0bf6:
            r2 = r83
            r61 = r3
            r52 = r4
            r62 = r6
            r54 = r8
            r4 = r48
            r8 = r49
            r3 = r63
            r5 = r64
            r6 = r65
            goto L_0x0be4
        L_0x0c0b:
            r0 = move-exception
            r4 = r78
            r40 = r85
            r5 = r0
            r14 = r3
            r62 = r6
        L_0x0CLASSNAME:
            r6 = r7
            r54 = r8
        L_0x0CLASSNAME:
            r9 = r10
            goto L_0x1111
        L_0x0c1a:
            r51 = r14
            r2 = -2
            if (r1 != r2) goto L_0x0CLASSNAME
            android.media.MediaFormat r2 = r7.getOutputFormat()     // Catch:{ Exception -> 0x0c7b, all -> 0x10fb }
            r14 = -5
            if (r12 != r14) goto L_0x0CLASSNAME
            if (r2 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.video.MP4Builder r14 = r15.mediaMuxer     // Catch:{ Exception -> 0x0c7b, all -> 0x10fb }
            r62 = r6
            r6 = 0
            int r12 = r14.addTrack(r2, r6)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x10fb }
            r6 = r66
            boolean r14 = r2.containsKey(r6)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x10fb }
            if (r14 == 0) goto L_0x0c5f
            int r14 = r2.getInteger(r6)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x10fb }
            r66 = r6
            r6 = 1
            if (r14 != r6) goto L_0x0c5c
            r6 = r65
            java.nio.ByteBuffer r14 = r2.getByteBuffer(r6)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x10fb }
            r52 = r4
            r4 = r48
            java.nio.ByteBuffer r2 = r2.getByteBuffer(r4)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x10fb }
            int r5 = r14.limit()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x10fb }
            int r2 = r2.limit()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x10fb }
            int r5 = r5 + r2
            r30 = r5
            goto L_0x0c6d
        L_0x0c5c:
            r52 = r4
            goto L_0x0CLASSNAME
        L_0x0c5f:
            r52 = r4
            r66 = r6
        L_0x0CLASSNAME:
            r4 = r48
            r6 = r65
            goto L_0x0c6d
        L_0x0CLASSNAME:
            r52 = r4
            r62 = r6
            goto L_0x0CLASSNAME
        L_0x0c6d:
            r2 = r83
            r61 = r3
            r54 = r8
            r8 = r49
            r3 = r63
            r5 = r64
            goto L_0x0be4
        L_0x0c7b:
            r0 = move-exception
            r62 = r6
        L_0x0c7e:
            r4 = r78
            r40 = r85
            r5 = r0
            r14 = r3
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r52 = r4
            r62 = r6
            r4 = r48
            r6 = r65
            if (r1 < 0) goto L_0x10d1
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x10cc, all -> 0x10fb }
            r5 = 21
            if (r2 >= r5) goto L_0x0c9a
            r2 = r29[r1]     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x10fb }
            goto L_0x0c9e
        L_0x0CLASSNAME:
            r0 = move-exception
            goto L_0x0c7e
        L_0x0c9a:
            java.nio.ByteBuffer r2 = r7.getOutputBuffer(r1)     // Catch:{ Exception -> 0x10cc, all -> 0x10fb }
        L_0x0c9e:
            if (r2 == 0) goto L_0x10a8
            int r14 = r13.size     // Catch:{ Exception -> 0x10cc, all -> 0x10fb }
            r5 = 1
            if (r14 <= r5) goto L_0x0dfa
            int r14 = r13.flags     // Catch:{ Exception -> 0x0de7, all -> 0x10fb }
            r14 = r14 & 2
            if (r14 != 0) goto L_0x0d6b
            if (r30 == 0) goto L_0x0cbe
            int r14 = r13.flags     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x10fb }
            r14 = r14 & r5
            if (r14 == 0) goto L_0x0cbe
            int r5 = r13.offset     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x10fb }
            int r5 = r5 + r30
            r13.offset = r5     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x10fb }
            int r5 = r13.size     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x10fb }
            int r5 = r5 - r30
            r13.size = r5     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x10fb }
        L_0x0cbe:
            if (r16 == 0) goto L_0x0d1f
            int r5 = r13.flags     // Catch:{ Exception -> 0x0d13, all -> 0x10fb }
            r14 = 1
            r5 = r5 & r14
            if (r5 == 0) goto L_0x0d1f
            int r5 = r13.size     // Catch:{ Exception -> 0x0d13, all -> 0x10fb }
            r14 = 100
            if (r5 <= r14) goto L_0x0d0e
            int r5 = r13.offset     // Catch:{ Exception -> 0x0d13, all -> 0x10fb }
            r2.position(r5)     // Catch:{ Exception -> 0x0d13, all -> 0x10fb }
            byte[] r5 = new byte[r14]     // Catch:{ Exception -> 0x0d13, all -> 0x10fb }
            r2.get(r5)     // Catch:{ Exception -> 0x0d13, all -> 0x10fb }
            r14 = 0
            r16 = 0
        L_0x0cd9:
            r54 = r8
            r8 = 96
            if (r14 >= r8) goto L_0x0d10
            byte r8 = r5[r14]     // Catch:{ Exception -> 0x0d0c, all -> 0x10fb }
            if (r8 != 0) goto L_0x0d07
            int r8 = r14 + 1
            byte r8 = r5[r8]     // Catch:{ Exception -> 0x0d0c, all -> 0x10fb }
            if (r8 != 0) goto L_0x0d07
            int r8 = r14 + 2
            byte r8 = r5[r8]     // Catch:{ Exception -> 0x0d0c, all -> 0x10fb }
            if (r8 != 0) goto L_0x0d07
            int r8 = r14 + 3
            byte r8 = r5[r8]     // Catch:{ Exception -> 0x0d0c, all -> 0x10fb }
            r9 = 1
            if (r8 != r9) goto L_0x0d07
            int r8 = r16 + 1
            if (r8 <= r9) goto L_0x0d05
            int r5 = r13.offset     // Catch:{ Exception -> 0x0d0c, all -> 0x10fb }
            int r5 = r5 + r14
            r13.offset = r5     // Catch:{ Exception -> 0x0d0c, all -> 0x10fb }
            int r5 = r13.size     // Catch:{ Exception -> 0x0d0c, all -> 0x10fb }
            int r5 = r5 - r14
            r13.size = r5     // Catch:{ Exception -> 0x0d0c, all -> 0x10fb }
            goto L_0x0d10
        L_0x0d05:
            r16 = r8
        L_0x0d07:
            int r14 = r14 + 1
            r8 = r54
            goto L_0x0cd9
        L_0x0d0c:
            r0 = move-exception
            goto L_0x0d16
        L_0x0d0e:
            r54 = r8
        L_0x0d10:
            r16 = 0
            goto L_0x0d21
        L_0x0d13:
            r0 = move-exception
            r54 = r8
        L_0x0d16:
            r4 = r78
            r40 = r85
            r5 = r0
            r14 = r3
        L_0x0d1c:
            r6 = r7
            goto L_0x0CLASSNAME
        L_0x0d1f:
            r54 = r8
        L_0x0d21:
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x0d66, all -> 0x10fb }
            r14 = r3
            r8 = 1
            long r2 = r5.writeSampleData(r12, r2, r13, r8)     // Catch:{ Exception -> 0x0d5d, all -> 0x10fb }
            r8 = 0
            int r5 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r5 == 0) goto L_0x0d52
            org.telegram.messenger.MediaController$VideoConvertorListener r5 = r15.callback     // Catch:{ Exception -> 0x0d5d, all -> 0x10fb }
            if (r5 == 0) goto L_0x0d52
            long r8 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0d5d, all -> 0x10fb }
            long r8 = r8 - r10
            int r5 = (r8 > r31 ? 1 : (r8 == r31 ? 0 : -1))
            if (r5 <= 0) goto L_0x0d41
            long r8 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0d3f, all -> 0x10fb }
            long r31 = r8 - r10
            goto L_0x0d41
        L_0x0d3f:
            r0 = move-exception
            goto L_0x0d60
        L_0x0d41:
            r8 = r31
            org.telegram.messenger.MediaController$VideoConvertorListener r5 = r15.callback     // Catch:{ Exception -> 0x0d5d, all -> 0x10fb }
            r61 = r14
            float r14 = (float) r8
            float r14 = r14 / r19
            float r14 = r14 / r20
            r5.didWriteData(r2, r14)     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            r31 = r8
            goto L_0x0d54
        L_0x0d52:
            r61 = r14
        L_0x0d54:
            r2 = r12
            r8 = r49
            r3 = r63
            r5 = r64
            goto L_0x0e05
        L_0x0d5d:
            r0 = move-exception
            r61 = r14
        L_0x0d60:
            r4 = r78
            r40 = r85
            r5 = r0
            goto L_0x0d1c
        L_0x0d66:
            r0 = move-exception
            r61 = r3
            goto L_0x0dec
        L_0x0d6b:
            r61 = r3
            r54 = r8
            r3 = -5
            if (r12 != r3) goto L_0x0dfe
            int r3 = r13.size     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            byte[] r3 = new byte[r3]     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            int r5 = r13.offset     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            int r8 = r13.size     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            int r5 = r5 + r8
            r2.limit(r5)     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            int r5 = r13.offset     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            r2.position(r5)     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            r2.get(r3)     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            int r2 = r13.size     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            r5 = 1
            int r2 = r2 - r5
        L_0x0d8a:
            if (r2 < 0) goto L_0x0dc7
            r8 = 3
            if (r2 <= r8) goto L_0x0dc7
            byte r9 = r3[r2]     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            if (r9 != r5) goto L_0x0dc3
            int r9 = r2 + -1
            byte r9 = r3[r9]     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            if (r9 != 0) goto L_0x0dc3
            int r9 = r2 + -2
            byte r9 = r3[r9]     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            if (r9 != 0) goto L_0x0dc3
            int r9 = r2 + -3
            byte r14 = r3[r9]     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            if (r14 != 0) goto L_0x0dc3
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r9)     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            int r14 = r13.size     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            int r14 = r14 - r9
            java.nio.ByteBuffer r14 = java.nio.ByteBuffer.allocate(r14)     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            r5 = 0
            java.nio.ByteBuffer r8 = r2.put(r3, r5, r9)     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            r8.position(r5)     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            int r8 = r13.size     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            int r8 = r8 - r9
            java.nio.ByteBuffer r3 = r14.put(r3, r9, r8)     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            r3.position(r5)     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            goto L_0x0dc9
        L_0x0dc3:
            int r2 = r2 + -1
            r5 = 1
            goto L_0x0d8a
        L_0x0dc7:
            r2 = 0
            r14 = 0
        L_0x0dc9:
            r8 = r49
            r3 = r63
            r5 = r64
            android.media.MediaFormat r9 = android.media.MediaFormat.createVideoFormat(r8, r3, r5)     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            if (r2 == 0) goto L_0x0ddd
            if (r14 == 0) goto L_0x0ddd
            r9.setByteBuffer(r6, r2)     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            r9.setByteBuffer(r4, r14)     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
        L_0x0ddd:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            r14 = 0
            int r2 = r2.addTrack(r9, r14)     // Catch:{ Exception -> 0x0de5, all -> 0x10fb }
            goto L_0x0e05
        L_0x0de5:
            r0 = move-exception
            goto L_0x0dec
        L_0x0de7:
            r0 = move-exception
            r61 = r3
            r54 = r8
        L_0x0dec:
            r4 = r78
            r40 = r85
            r5 = r0
            r6 = r7
            r9 = r10
            r2 = r12
            r3 = r18
            r14 = r61
            goto L_0x112b
        L_0x0dfa:
            r61 = r3
            r54 = r8
        L_0x0dfe:
            r8 = r49
            r3 = r63
            r5 = r64
            r2 = r12
        L_0x0e05:
            int r9 = r13.flags     // Catch:{ Exception -> 0x109f, all -> 0x05d6 }
            r9 = r9 & 4
            if (r9 == 0) goto L_0x0e0d
            r9 = 1
            goto L_0x0e0e
        L_0x0e0d:
            r9 = 0
        L_0x0e0e:
            r12 = 0
            r7.releaseOutputBuffer(r1, r12)     // Catch:{ Exception -> 0x109f, all -> 0x05d6 }
            r12 = r2
            r23 = r9
            r9 = -1
            r2 = r83
        L_0x0e18:
            if (r1 == r9) goto L_0x0e39
            r1 = r2
            r63 = r3
            r48 = r4
            r64 = r5
            r65 = r6
            r49 = r8
            r14 = r51
            r4 = r52
            r8 = r54
            r3 = r61
            r6 = r62
            r2 = r84
            r69 = r7
            r7 = r79
            r79 = r69
            goto L_0x0b8d
        L_0x0e39:
            if (r28 != 0) goto L_0x106a
            r14 = r61
            r9 = 2500(0x9c4, double:1.235E-320)
            int r1 = r14.dequeueOutputBuffer(r13, r9)     // Catch:{ Exception -> 0x1059, all -> 0x1052 }
            r11 = -1
            if (r1 != r11) goto L_0x0e63
            r1 = r79
            r17 = r2
            r63 = r3
            r57 = r4
            r64 = r5
            r65 = r6
            r38 = r9
            r83 = r12
            r11 = r52
            r3 = r59
            r2 = 0
            r5 = 0
            r4 = r78
            r9 = r81
            goto L_0x1087
        L_0x0e63:
            r9 = -3
            if (r1 != r9) goto L_0x0e7a
        L_0x0e66:
            r9 = r81
            r17 = r2
            r63 = r3
            r57 = r4
            r64 = r5
            r65 = r6
            r83 = r12
            r11 = r52
            r3 = r59
            goto L_0x107d
        L_0x0e7a:
            r9 = -2
            if (r1 != r9) goto L_0x0ea1
            android.media.MediaFormat r1 = r14.getOutputFormat()     // Catch:{ Exception -> 0x0e9a, all -> 0x10fb }
            boolean r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0e9a, all -> 0x10fb }
            if (r9 == 0) goto L_0x0e66
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0e9a, all -> 0x10fb }
            r9.<init>()     // Catch:{ Exception -> 0x0e9a, all -> 0x10fb }
            java.lang.String r10 = "newFormat = "
            r9.append(r10)     // Catch:{ Exception -> 0x0e9a, all -> 0x10fb }
            r9.append(r1)     // Catch:{ Exception -> 0x0e9a, all -> 0x10fb }
            java.lang.String r1 = r9.toString()     // Catch:{ Exception -> 0x0e9a, all -> 0x10fb }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0e9a, all -> 0x10fb }
            goto L_0x0e66
        L_0x0e9a:
            r0 = move-exception
            r4 = r78
            r9 = r81
            goto L_0x110d
        L_0x0ea1:
            if (r1 < 0) goto L_0x1028
            int r9 = r13.size     // Catch:{ Exception -> 0x1059, all -> 0x1052 }
            r83 = r12
            if (r9 == 0) goto L_0x0eab
            r9 = 1
            goto L_0x0eac
        L_0x0eab:
            r9 = 0
        L_0x0eac:
            long r11 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1022, all -> 0x1047 }
            r41 = 0
            int r10 = (r54 > r41 ? 1 : (r54 == r41 ? 0 : -1))
            if (r10 <= 0) goto L_0x0ec5
            int r10 = (r11 > r54 ? 1 : (r11 == r54 ? 0 : -1))
            if (r10 < 0) goto L_0x0ec5
            int r9 = r13.flags     // Catch:{ Exception -> 0x0f4a, all -> 0x1047 }
            r9 = r9 | 4
            r13.flags = r9     // Catch:{ Exception -> 0x0f4a, all -> 0x1047 }
            r9 = 1
            r10 = 0
            r28 = 1
            r41 = 0
            goto L_0x0eca
        L_0x0ec5:
            r10 = r9
            r41 = 0
            r9 = r79
        L_0x0eca:
            int r17 = (r35 > r41 ? 1 : (r35 == r41 ? 0 : -1))
            if (r17 < 0) goto L_0x0var_
            r17 = r2
            int r2 = r13.flags     // Catch:{ Exception -> 0x0f4a, all -> 0x1047 }
            r2 = r2 & 4
            if (r2 == 0) goto L_0x0var_
            r79 = r9
            r2 = r10
            r38 = 2500(0x9c4, double:1.235E-320)
            r9 = r81
            long r48 = r35 - r9
            long r48 = java.lang.Math.abs(r48)     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            r56 = 1000000(0xvar_, float:1.401298E-39)
            r58 = r2
            r57 = r4
            r4 = r78
            int r2 = r56 / r4
            r63 = r3
            long r2 = (long) r2     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            int r56 = (r48 > r2 ? 1 : (r48 == r2 ? 0 : -1))
            if (r56 <= 0) goto L_0x0f3b
            r2 = 0
            int r28 = (r9 > r2 ? 1 : (r9 == r2 ? 0 : -1))
            if (r28 <= 0) goto L_0x0var_
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            r3 = 0
            r2.seekTo(r9, r3)     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            r64 = r5
            r65 = r6
            r3 = 0
            goto L_0x0var_
        L_0x0var_:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            r64 = r5
            r65 = r6
            r3 = 0
            r5 = 0
            r2.seekTo(r5, r3)     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
        L_0x0var_:
            long r33 = r52 + r24
            int r2 = r13.flags     // Catch:{ Exception -> 0x0f2b, all -> 0x1047 }
            r5 = -5
            r2 = r2 & r5
            r13.flags = r2     // Catch:{ Exception -> 0x0f2b, all -> 0x1047 }
            r14.flush()     // Catch:{ Exception -> 0x0f2b, all -> 0x1047 }
            r54 = r35
            r2 = 1
            r6 = 0
            r28 = 0
            r41 = 0
            r58 = 0
            r35 = r21
            goto L_0x0f6c
        L_0x0f2b:
            r0 = move-exception
            r2 = r83
            r40 = r85
            r5 = r0
            r6 = r7
            r3 = r18
            r54 = r35
            r1 = 0
            r35 = r21
            goto L_0x1203
        L_0x0f3b:
            r64 = r5
            r65 = r6
            r3 = 0
            r5 = -5
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            goto L_0x1062
        L_0x0var_:
            r0 = move-exception
            r4 = r78
            goto L_0x1062
        L_0x0f4a:
            r0 = move-exception
            r4 = r78
            r9 = r81
            goto L_0x1062
        L_0x0var_:
            r17 = r2
        L_0x0var_:
            r63 = r3
            r57 = r4
            r64 = r5
            r65 = r6
            r79 = r9
            r58 = r10
            r3 = 0
            r5 = -5
            r38 = 2500(0x9c4, double:1.235E-320)
            r4 = r78
            r9 = r81
        L_0x0var_:
            r6 = r79
            r2 = 0
            r41 = 0
        L_0x0f6c:
            int r43 = (r35 > r41 ? 1 : (r35 == r41 ? 0 : -1))
            r79 = r6
            if (r43 < 0) goto L_0x0var_
            r5 = r35
            goto L_0x0var_
        L_0x0var_:
            r5 = r9
        L_0x0var_:
            int r43 = (r5 > r41 ? 1 : (r5 == r41 ? 0 : -1))
            if (r43 <= 0) goto L_0x0fb3
            int r43 = (r46 > r21 ? 1 : (r46 == r21 ? 0 : -1))
            if (r43 != 0) goto L_0x0fb3
            int r43 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1))
            if (r43 >= 0) goto L_0x0fa6
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            if (r11 == 0) goto L_0x0fa4
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            r11.<init>()     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            java.lang.String r12 = "drop frame startTime = "
            r11.append(r12)     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            r11.append(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            java.lang.String r5 = " present time = "
            r11.append(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            long r5 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            r11.append(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            java.lang.String r5 = r11.toString()     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            org.telegram.messenger.FileLog.d(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
        L_0x0fa4:
            r6 = 0
            goto L_0x0fb5
        L_0x0fa6:
            long r5 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            r11 = -2147483648(0xfffffffvar_, double:NaN)
            int r43 = (r52 > r11 ? 1 : (r52 == r11 ? 0 : -1))
            if (r43 == 0) goto L_0x0fb1
            long r33 = r33 - r5
        L_0x0fb1:
            r46 = r5
        L_0x0fb3:
            r6 = r58
        L_0x0fb5:
            if (r2 == 0) goto L_0x0fba
            r46 = r21
            goto L_0x0fcd
        L_0x0fba:
            int r2 = (r35 > r21 ? 1 : (r35 == r21 ? 0 : -1))
            if (r2 != 0) goto L_0x0fca
            r11 = 0
            int r2 = (r33 > r11 ? 1 : (r33 == r11 ? 0 : -1))
            if (r2 == 0) goto L_0x0fca
            long r11 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            long r11 = r11 + r33
            r13.presentationTimeUs = r11     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
        L_0x0fca:
            r14.releaseOutputBuffer(r1, r6)     // Catch:{ Exception -> 0x1020, all -> 0x1047 }
        L_0x0fcd:
            if (r6 == 0) goto L_0x1003
            r5 = 0
            int r1 = (r35 > r5 ? 1 : (r35 == r5 ? 0 : -1))
            if (r1 < 0) goto L_0x0fdf
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            r11 = r52
            long r1 = java.lang.Math.max(r11, r1)     // Catch:{ Exception -> 0x0var_, all -> 0x1047 }
            r11 = r1
            goto L_0x0fe1
        L_0x0fdf:
            r11 = r52
        L_0x0fe1:
            r27.awaitNewImage()     // Catch:{ Exception -> 0x0fe6, all -> 0x1047 }
            r1 = 0
            goto L_0x0fec
        L_0x0fe6:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x1020, all -> 0x1047 }
            r1 = 1
        L_0x0fec:
            if (r1 != 0) goto L_0x1000
            r27.drawImage()     // Catch:{ Exception -> 0x1020, all -> 0x1047 }
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1020, all -> 0x1047 }
            r41 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 * r41
            r3 = r59
            r3.setPresentationTime(r1)     // Catch:{ Exception -> 0x1049, all -> 0x1047 }
            r3.swapBuffers()     // Catch:{ Exception -> 0x1049, all -> 0x1047 }
            goto L_0x1009
        L_0x1000:
            r3 = r59
            goto L_0x1009
        L_0x1003:
            r11 = r52
            r3 = r59
            r5 = 0
        L_0x1009:
            int r1 = r13.flags     // Catch:{ Exception -> 0x1049, all -> 0x1047 }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x1083
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1049, all -> 0x1047 }
            if (r1 == 0) goto L_0x1018
            java.lang.String r1 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x1049, all -> 0x1047 }
        L_0x1018:
            r7.signalEndOfInputStream()     // Catch:{ Exception -> 0x1049, all -> 0x1047 }
            r1 = r79
            r2 = 0
            goto L_0x1087
        L_0x1020:
            r0 = move-exception
            goto L_0x1060
        L_0x1022:
            r0 = move-exception
            r4 = r78
            r9 = r81
            goto L_0x1060
        L_0x1028:
            r4 = r78
            r9 = r81
            r83 = r12
            r3 = r59
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1049, all -> 0x1047 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1049, all -> 0x1047 }
            r5.<init>()     // Catch:{ Exception -> 0x1049, all -> 0x1047 }
            java.lang.String r6 = "unexpected result from decoder.dequeueOutputBuffer: "
            r5.append(r6)     // Catch:{ Exception -> 0x1049, all -> 0x1047 }
            r5.append(r1)     // Catch:{ Exception -> 0x1049, all -> 0x1047 }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x1049, all -> 0x1047 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x1049, all -> 0x1047 }
            throw r2     // Catch:{ Exception -> 0x1049, all -> 0x1047 }
        L_0x1047:
            r0 = move-exception
            goto L_0x1055
        L_0x1049:
            r0 = move-exception
            r2 = r83
            r40 = r85
            r5 = r0
            r59 = r3
            goto L_0x1067
        L_0x1052:
            r0 = move-exception
            r83 = r12
        L_0x1055:
            r2 = r83
            goto L_0x05d7
        L_0x1059:
            r0 = move-exception
            r4 = r78
            r9 = r81
            r83 = r12
        L_0x1060:
            r3 = r59
        L_0x1062:
            r2 = r83
        L_0x1064:
            r40 = r85
            r5 = r0
        L_0x1067:
            r6 = r7
            goto L_0x09d3
        L_0x106a:
            r17 = r2
            r63 = r3
            r57 = r4
            r64 = r5
            r65 = r6
            r9 = r10
            r83 = r12
            r11 = r52
            r3 = r59
            r14 = r61
        L_0x107d:
            r5 = 0
            r38 = 2500(0x9c4, double:1.235E-320)
            r4 = r78
        L_0x1083:
            r1 = r79
            r2 = r84
        L_0x1087:
            r59 = r3
            r79 = r7
            r49 = r8
            r4 = r11
            r3 = r14
            r14 = r51
            r48 = r57
            r6 = r62
            r12 = r83
            r7 = r1
            r10 = r9
            r1 = r17
            r8 = r54
            goto L_0x0b8d
        L_0x109f:
            r0 = move-exception
            r4 = r78
            r9 = r10
            r3 = r59
            r14 = r61
            goto L_0x1064
        L_0x10a8:
            r4 = r78
            r14 = r3
            r54 = r8
            r9 = r10
            r3 = r59
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x10f0, all -> 0x10fb }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x10f0, all -> 0x10fb }
            r5.<init>()     // Catch:{ Exception -> 0x10f0, all -> 0x10fb }
            java.lang.String r6 = "encoderOutputBuffer "
            r5.append(r6)     // Catch:{ Exception -> 0x10f0, all -> 0x10fb }
            r5.append(r1)     // Catch:{ Exception -> 0x10f0, all -> 0x10fb }
            java.lang.String r1 = " was null"
            r5.append(r1)     // Catch:{ Exception -> 0x10f0, all -> 0x10fb }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x10f0, all -> 0x10fb }
            r2.<init>(r1)     // Catch:{ Exception -> 0x10f0, all -> 0x10fb }
            throw r2     // Catch:{ Exception -> 0x10f0, all -> 0x10fb }
        L_0x10cc:
            r0 = move-exception
            r4 = r78
            r14 = r3
            goto L_0x1108
        L_0x10d1:
            r4 = r78
            r14 = r3
            r54 = r8
            r9 = r10
            r3 = r59
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x10f0, all -> 0x10fb }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x10f0, all -> 0x10fb }
            r5.<init>()     // Catch:{ Exception -> 0x10f0, all -> 0x10fb }
            java.lang.String r6 = "unexpected result from encoder.dequeueOutputBuffer: "
            r5.append(r6)     // Catch:{ Exception -> 0x10f0, all -> 0x10fb }
            r5.append(r1)     // Catch:{ Exception -> 0x10f0, all -> 0x10fb }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x10f0, all -> 0x10fb }
            r2.<init>(r1)     // Catch:{ Exception -> 0x10f0, all -> 0x10fb }
            throw r2     // Catch:{ Exception -> 0x10f0, all -> 0x10fb }
        L_0x10f0:
            r0 = move-exception
            r40 = r85
            r5 = r0
            r59 = r3
            goto L_0x1110
        L_0x10f7:
            r0 = move-exception
            r4 = r78
            goto L_0x1105
        L_0x10fb:
            r0 = move-exception
            r3 = r0
            r2 = r12
            goto L_0x05d8
        L_0x1100:
            r0 = move-exception
            r4 = r78
            r7 = r79
        L_0x1105:
            r14 = r3
            r62 = r6
        L_0x1108:
            r54 = r8
            r9 = r10
            r3 = r59
        L_0x110d:
            r40 = r85
            r5 = r0
        L_0x1110:
            r6 = r7
        L_0x1111:
            r2 = r12
            goto L_0x09d3
        L_0x1114:
            r0 = move-exception
            r4 = r78
            r7 = r79
            r14 = r3
            r60 = r8
            r9 = r10
            r85 = r13
            r3 = r59
            r40 = r85
            r5 = r0
            r6 = r7
        L_0x1125:
            r2 = r17
            r3 = r18
            r54 = r60
        L_0x112b:
            r1 = 0
            goto L_0x1203
        L_0x112e:
            r0 = move-exception
            r4 = r78
            r7 = r79
            r14 = r3
            r9 = r10
            r85 = r13
            r3 = r59
            r54 = r83
            r40 = r85
            r5 = r0
            r6 = r7
        L_0x113f:
            r3 = r18
            r35 = r30
            r1 = 0
            r2 = -5
            goto L_0x1203
        L_0x1147:
            r0 = move-exception
            r4 = r78
            r7 = r79
            r9 = r81
            r14 = r3
            r3 = r59
            goto L_0x115d
        L_0x1152:
            r0 = move-exception
            r4 = r78
            r7 = r79
            r9 = r81
            r3 = r59
            r14 = r61
        L_0x115d:
            r54 = r83
            r5 = r0
            r6 = r7
        L_0x1161:
            r3 = r18
            r35 = r30
            r1 = 0
            r2 = -5
            goto L_0x11a8
        L_0x1168:
            r0 = move-exception
            r4 = r78
            r7 = r79
            r9 = r81
            r3 = r59
            r14 = r61
            r54 = r83
            r5 = r0
            goto L_0x1187
        L_0x1177:
            r0 = move-exception
            r9 = r81
            r3 = r1
            r30 = r7
            r62 = r24
            r7 = r4
            r4 = r14
            r14 = r2
            r54 = r83
            r5 = r0
            r59 = r3
        L_0x1187:
            r6 = r7
            r3 = r18
            r35 = r30
            r1 = 0
            r2 = -5
            goto L_0x11a6
        L_0x118f:
            r0 = move-exception
            r9 = r81
            r3 = r1
            r30 = r7
            r62 = r24
            r7 = r4
            r4 = r14
            r54 = r83
            r5 = r0
            r59 = r3
            r6 = r7
            r3 = r18
            r35 = r30
            r1 = 0
            r2 = -5
            r14 = 0
        L_0x11a6:
            r27 = 0
        L_0x11a8:
            r40 = 0
            goto L_0x1203
        L_0x11ac:
            r0 = move-exception
            r9 = r81
            r30 = r7
            r62 = r24
            r7 = r4
            r4 = r14
            r54 = r83
            r5 = r0
            r6 = r7
            r3 = r18
            r35 = r30
            r1 = 0
            r2 = -5
            goto L_0x11fc
        L_0x11c0:
            r0 = move-exception
            r9 = r81
            r30 = r7
            r4 = r14
            r62 = r24
            r54 = r83
            r5 = r0
            r3 = r18
            r35 = r30
            goto L_0x06e6
        L_0x11d1:
            r0 = move-exception
            r9 = r81
            goto L_0x11e0
        L_0x11d5:
            r0 = move-exception
            r9 = r81
            r30 = r7
            r4 = r14
            r62 = r24
            goto L_0x11e6
        L_0x11de:
            r0 = move-exception
            r9 = r1
        L_0x11e0:
            r30 = r7
            r4 = r14
            r62 = r24
            r1 = 0
        L_0x11e6:
            r54 = r83
            r5 = r0
            r35 = r30
            goto L_0x11fa
        L_0x11ec:
            r0 = move-exception
            r9 = r81
            r62 = r4
            r4 = r14
            r1 = 0
            r3 = r79
            r54 = r83
            r35 = r85
            r5 = r0
        L_0x11fa:
            r2 = -5
            r6 = 0
        L_0x11fc:
            r14 = 0
            r27 = 0
            r40 = 0
            r59 = 0
        L_0x1203:
            boolean r7 = r5 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x127a, all -> 0x05d6 }
            if (r7 == 0) goto L_0x120a
            if (r90 != 0) goto L_0x120a
            r1 = 1
        L_0x120a:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1271, all -> 0x05d6 }
            r7.<init>()     // Catch:{ Exception -> 0x1271, all -> 0x05d6 }
            java.lang.String r8 = "bitrate: "
            r7.append(r8)     // Catch:{ Exception -> 0x1271, all -> 0x05d6 }
            r7.append(r3)     // Catch:{ Exception -> 0x1271, all -> 0x05d6 }
            java.lang.String r8 = " framerate: "
            r7.append(r8)     // Catch:{ Exception -> 0x1271, all -> 0x05d6 }
            r7.append(r4)     // Catch:{ Exception -> 0x1271, all -> 0x05d6 }
            java.lang.String r8 = " size: "
            r7.append(r8)     // Catch:{ Exception -> 0x1271, all -> 0x05d6 }
            r8 = r77
            r7.append(r8)     // Catch:{ Exception -> 0x126d, all -> 0x05d6 }
            java.lang.String r11 = "x"
            r7.append(r11)     // Catch:{ Exception -> 0x126d, all -> 0x05d6 }
            r11 = r76
            r7.append(r11)     // Catch:{ Exception -> 0x126b, all -> 0x05d6 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x126b, all -> 0x05d6 }
            org.telegram.messenger.FileLog.e((java.lang.String) r7)     // Catch:{ Exception -> 0x126b, all -> 0x05d6 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ Exception -> 0x126b, all -> 0x05d6 }
            r17 = r2
            r18 = r3
            r3 = r6
            r2 = r14
            r6 = r1
            r1 = 1
        L_0x1245:
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x1266, all -> 0x125f }
            r7 = r62
            r5.unselectTrack(r7)     // Catch:{ Exception -> 0x1266, all -> 0x125f }
            if (r2 == 0) goto L_0x1254
            r2.stop()     // Catch:{ Exception -> 0x1266, all -> 0x125f }
            r2.release()     // Catch:{ Exception -> 0x1266, all -> 0x125f }
        L_0x1254:
            r5 = r1
            r7 = r6
            r2 = r17
            r6 = r27
            r1 = r40
            r40 = r59
            goto L_0x1298
        L_0x125f:
            r0 = move-exception
            r3 = r0
            r1 = r15
            r2 = r17
            goto L_0x137c
        L_0x1266:
            r0 = move-exception
            r1 = r0
            r2 = r17
            goto L_0x12a1
        L_0x126b:
            r0 = move-exception
            goto L_0x1276
        L_0x126d:
            r0 = move-exception
            r11 = r76
            goto L_0x1276
        L_0x1271:
            r0 = move-exception
            r11 = r76
            r8 = r77
        L_0x1276:
            r6 = r1
            r1 = r0
            goto L_0x12fb
        L_0x127a:
            r0 = move-exception
            r11 = r76
            r8 = r77
            r1 = r0
            goto L_0x12fa
        L_0x1282:
            r11 = r76
            r8 = r77
            r9 = r81
            r4 = r14
            r1 = 0
            r18 = r79
            r54 = r83
            r35 = r85
            r1 = 0
            r2 = -5
            r3 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r40 = 0
        L_0x1298:
            if (r6 == 0) goto L_0x12a5
            r6.release()     // Catch:{ Exception -> 0x129e, all -> 0x05d6 }
            goto L_0x12a5
        L_0x129e:
            r0 = move-exception
            r1 = r0
            r6 = r7
        L_0x12a1:
            r3 = r18
            goto L_0x12fb
        L_0x12a5:
            if (r40 == 0) goto L_0x12aa
            r40.release()     // Catch:{ Exception -> 0x129e, all -> 0x05d6 }
        L_0x12aa:
            if (r3 == 0) goto L_0x12b2
            r3.stop()     // Catch:{ Exception -> 0x129e, all -> 0x05d6 }
            r3.release()     // Catch:{ Exception -> 0x129e, all -> 0x05d6 }
        L_0x12b2:
            if (r1 == 0) goto L_0x12b7
            r1.release()     // Catch:{ Exception -> 0x129e, all -> 0x05d6 }
        L_0x12b7:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x129e, all -> 0x05d6 }
            r1 = r5
            r6 = r7
            r51 = r8
        L_0x12be:
            android.media.MediaExtractor r3 = r15.extractor
            if (r3 == 0) goto L_0x12c5
            r3.release()
        L_0x12c5:
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer
            if (r3 == 0) goto L_0x12da
            r3.finishMovie()     // Catch:{ Exception -> 0x12d5 }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ Exception -> 0x12d5 }
            long r2 = r3.getLastFrameTimestamp(r2)     // Catch:{ Exception -> 0x12d5 }
            r15.endPresentationTime = r2     // Catch:{ Exception -> 0x12d5 }
            goto L_0x12da
        L_0x12d5:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x12da:
            r7 = r51
            r13 = r54
            goto L_0x134c
        L_0x12e0:
            r0 = move-exception
            r8 = r77
            r4 = r10
            goto L_0x12ee
        L_0x12e5:
            r0 = move-exception
            r3 = r0
            r1 = r15
            r2 = -5
            goto L_0x137c
        L_0x12eb:
            r0 = move-exception
            r4 = r10
            r8 = r11
        L_0x12ee:
            r11 = r12
            r1 = 0
            r9 = r81
            r3 = r79
            r54 = r83
            r35 = r85
            r1 = r0
        L_0x12f9:
            r2 = -5
        L_0x12fa:
            r6 = 0
        L_0x12fb:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x1378 }
            r5.<init>()     // Catch:{ all -> 0x1378 }
            java.lang.String r7 = "bitrate: "
            r5.append(r7)     // Catch:{ all -> 0x1378 }
            r5.append(r3)     // Catch:{ all -> 0x1378 }
            java.lang.String r7 = " framerate: "
            r5.append(r7)     // Catch:{ all -> 0x1378 }
            r5.append(r4)     // Catch:{ all -> 0x1378 }
            java.lang.String r7 = " size: "
            r5.append(r7)     // Catch:{ all -> 0x1378 }
            r5.append(r8)     // Catch:{ all -> 0x1378 }
            java.lang.String r7 = "x"
            r5.append(r7)     // Catch:{ all -> 0x1378 }
            r5.append(r11)     // Catch:{ all -> 0x1378 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x1378 }
            org.telegram.messenger.FileLog.e((java.lang.String) r5)     // Catch:{ all -> 0x1378 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x1378 }
            android.media.MediaExtractor r1 = r15.extractor
            if (r1 == 0) goto L_0x1331
            r1.release()
        L_0x1331:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer
            if (r1 == 0) goto L_0x1346
            r1.finishMovie()     // Catch:{ Exception -> 0x1341 }
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ Exception -> 0x1341 }
            long r1 = r1.getLastFrameTimestamp(r2)     // Catch:{ Exception -> 0x1341 }
            r15.endPresentationTime = r1     // Catch:{ Exception -> 0x1341 }
            goto L_0x1346
        L_0x1341:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1346:
            r18 = r3
            r7 = r8
            r13 = r54
            r1 = 1
        L_0x134c:
            if (r6 == 0) goto L_0x1377
            r20 = 1
            r1 = r71
            r2 = r72
            r3 = r73
            r4 = r74
            r5 = r75
            r6 = r11
            r8 = r78
            r9 = r18
            r10 = r80
            r11 = r81
            r15 = r35
            r17 = r87
            r19 = r89
            r21 = r91
            r22 = r92
            r23 = r93
            r24 = r94
            r25 = r95
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r13, r15, r17, r19, r20, r21, r22, r23, r24, r25)
        L_0x1377:
            return r1
        L_0x1378:
            r0 = move-exception
            r1 = r71
        L_0x137b:
            r3 = r0
        L_0x137c:
            android.media.MediaExtractor r4 = r1.extractor
            if (r4 == 0) goto L_0x1383
            r4.release()
        L_0x1383:
            org.telegram.messenger.video.MP4Builder r4 = r1.mediaMuxer
            if (r4 == 0) goto L_0x1398
            r4.finishMovie()     // Catch:{ Exception -> 0x1393 }
            org.telegram.messenger.video.MP4Builder r4 = r1.mediaMuxer     // Catch:{ Exception -> 0x1393 }
            long r4 = r4.getLastFrameTimestamp(r2)     // Catch:{ Exception -> 0x1393 }
            r1.endPresentationTime = r4     // Catch:{ Exception -> 0x1393 }
            goto L_0x1398
        L_0x1393:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x1398:
            goto L_0x139a
        L_0x1399:
            throw r3
        L_0x139a:
            goto L_0x1399
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.MediaCodecVideoConvertor.convertVideoInternal(java.lang.String, java.io.File, int, boolean, int, int, int, int, int, long, long, long, long, boolean, boolean, org.telegram.messenger.MediaController$SavedFilterState, java.lang.String, java.util.ArrayList, boolean, org.telegram.messenger.MediaController$CropState):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00f0, code lost:
        if (r9[r15 + 3] != 1) goto L_0x00f6;
     */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0199  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long readAndWriteTracks(android.media.MediaExtractor r27, org.telegram.messenger.video.MP4Builder r28, android.media.MediaCodec.BufferInfo r29, long r30, long r32, long r34, java.io.File r36, boolean r37) throws java.lang.Exception {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            r2 = r28
            r3 = r29
            r4 = r30
            r6 = 0
            int r7 = org.telegram.messenger.MediaController.findTrack(r1, r6)
            r9 = 1
            if (r37 == 0) goto L_0x0019
            int r10 = org.telegram.messenger.MediaController.findTrack(r1, r9)
            r11 = r34
            goto L_0x001c
        L_0x0019:
            r11 = r34
            r10 = -1
        L_0x001c:
            float r11 = (float) r11
            r12 = 1148846080(0x447a0000, float:1000.0)
            float r11 = r11 / r12
            java.lang.String r13 = "max-input-size"
            r14 = 0
            if (r7 < 0) goto L_0x0041
            r1.selectTrack(r7)
            android.media.MediaFormat r12 = r1.getTrackFormat(r7)
            int r16 = r2.addTrack(r12, r6)
            int r12 = r12.getInteger(r13)
            int r17 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r17 <= 0) goto L_0x003d
            r1.seekTo(r4, r6)
            goto L_0x0044
        L_0x003d:
            r1.seekTo(r14, r6)
            goto L_0x0044
        L_0x0041:
            r12 = 0
            r16 = -1
        L_0x0044:
            if (r10 < 0) goto L_0x007a
            r1.selectTrack(r10)
            android.media.MediaFormat r8 = r1.getTrackFormat(r10)
            java.lang.String r6 = "mime"
            java.lang.String r6 = r8.getString(r6)
            java.lang.String r14 = "audio/unknown"
            boolean r6 = r6.equals(r14)
            if (r6 == 0) goto L_0x005e
            r6 = -1
            r10 = -1
            goto L_0x007b
        L_0x005e:
            int r6 = r2.addTrack(r8, r9)
            int r8 = r8.getInteger(r13)
            int r12 = java.lang.Math.max(r8, r12)
            r13 = 0
            int r8 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r8 <= 0) goto L_0x0075
            r8 = 0
            r1.seekTo(r4, r8)
            goto L_0x007b
        L_0x0075:
            r8 = 0
            r1.seekTo(r13, r8)
            goto L_0x007b
        L_0x007a:
            r6 = -1
        L_0x007b:
            java.nio.ByteBuffer r8 = java.nio.ByteBuffer.allocateDirect(r12)
            r12 = -1
            if (r10 >= 0) goto L_0x0087
            if (r7 < 0) goto L_0x0086
            goto L_0x0087
        L_0x0086:
            return r12
        L_0x0087:
            r26.checkConversionCanceled()
            r20 = r12
            r14 = 0
            r18 = 0
        L_0x008f:
            if (r14 != 0) goto L_0x01c2
            r26.checkConversionCanceled()
            r15 = 0
            int r12 = r1.readSampleData(r8, r15)
            r3.size = r12
            int r12 = r27.getSampleTrackIndex()
            if (r12 != r7) goto L_0x00a5
            r13 = r16
        L_0x00a3:
            r15 = -1
            goto L_0x00ab
        L_0x00a5:
            if (r12 != r10) goto L_0x00a9
            r13 = r6
            goto L_0x00a3
        L_0x00a9:
            r13 = -1
            goto L_0x00a3
        L_0x00ab:
            if (r13 == r15) goto L_0x019f
            int r15 = android.os.Build.VERSION.SDK_INT
            r9 = 21
            if (r15 >= r9) goto L_0x00bc
            r9 = 0
            r8.position(r9)
            int r9 = r3.size
            r8.limit(r9)
        L_0x00bc:
            if (r12 == r10) goto L_0x0129
            byte[] r9 = r8.array()
            if (r9 == 0) goto L_0x0129
            int r15 = r8.arrayOffset()
            int r22 = r8.limit()
            int r22 = r15 + r22
            r35 = r6
            r6 = -1
        L_0x00d1:
            r23 = 4
            r37 = r14
            int r14 = r22 + -4
            if (r15 > r14) goto L_0x0126
            byte r24 = r9[r15]
            if (r24 != 0) goto L_0x00f3
            int r24 = r15 + 1
            byte r24 = r9[r24]
            if (r24 != 0) goto L_0x00f3
            int r24 = r15 + 2
            byte r24 = r9[r24]
            if (r24 != 0) goto L_0x00f3
            int r24 = r15 + 3
            r25 = r10
            byte r10 = r9[r24]
            r1 = 1
            if (r10 == r1) goto L_0x00f8
            goto L_0x00f6
        L_0x00f3:
            r25 = r10
            r1 = 1
        L_0x00f6:
            if (r15 != r14) goto L_0x011d
        L_0x00f8:
            r10 = -1
            if (r6 == r10) goto L_0x011c
            int r10 = r15 - r6
            if (r15 == r14) goto L_0x0100
            goto L_0x0102
        L_0x0100:
            r23 = 0
        L_0x0102:
            int r10 = r10 - r23
            int r14 = r10 >> 24
            byte r14 = (byte) r14
            r9[r6] = r14
            int r14 = r6 + 1
            int r1 = r10 >> 16
            byte r1 = (byte) r1
            r9[r14] = r1
            int r1 = r6 + 2
            int r14 = r10 >> 8
            byte r14 = (byte) r14
            r9[r1] = r14
            int r6 = r6 + 3
            byte r1 = (byte) r10
            r9[r6] = r1
        L_0x011c:
            r6 = r15
        L_0x011d:
            int r15 = r15 + 1
            r1 = r27
            r14 = r37
            r10 = r25
            goto L_0x00d1
        L_0x0126:
            r25 = r10
            goto L_0x012f
        L_0x0129:
            r35 = r6
            r25 = r10
            r37 = r14
        L_0x012f:
            int r1 = r3.size
            if (r1 < 0) goto L_0x013b
            long r9 = r27.getSampleTime()
            r3.presentationTimeUs = r9
            r1 = 0
            goto L_0x013f
        L_0x013b:
            r1 = 0
            r3.size = r1
            r1 = 1
        L_0x013f:
            int r6 = r3.size
            if (r6 <= 0) goto L_0x0163
            if (r1 != 0) goto L_0x0163
            r9 = 0
            if (r12 != r7) goto L_0x0157
            int r6 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r6 <= 0) goto L_0x0157
            r14 = -1
            int r6 = (r20 > r14 ? 1 : (r20 == r14 ? 0 : -1))
            if (r6 != 0) goto L_0x0157
            long r14 = r3.presentationTimeUs
            r20 = r14
        L_0x0157:
            int r6 = (r32 > r9 ? 1 : (r32 == r9 ? 0 : -1))
            if (r6 < 0) goto L_0x0167
            long r9 = r3.presentationTimeUs
            int r6 = (r9 > r32 ? 1 : (r9 == r32 ? 0 : -1))
            if (r6 >= 0) goto L_0x0162
            goto L_0x0167
        L_0x0162:
            r1 = 1
        L_0x0163:
            r6 = 0
        L_0x0164:
            r17 = 1148846080(0x447a0000, float:1000.0)
            goto L_0x0197
        L_0x0167:
            r6 = 0
            r3.offset = r6
            int r9 = r27.getSampleFlags()
            r3.flags = r9
            long r9 = r2.writeSampleData(r13, r8, r3, r6)
            r13 = 0
            int r12 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r12 == 0) goto L_0x0164
            org.telegram.messenger.MediaController$VideoConvertorListener r12 = r0.callback
            if (r12 == 0) goto L_0x0164
            long r13 = r3.presentationTimeUs
            long r22 = r13 - r20
            int r12 = (r22 > r18 ? 1 : (r22 == r18 ? 0 : -1))
            if (r12 <= 0) goto L_0x0188
            long r18 = r13 - r20
        L_0x0188:
            r12 = r18
            org.telegram.messenger.MediaController$VideoConvertorListener r14 = r0.callback
            float r15 = (float) r12
            r17 = 1148846080(0x447a0000, float:1000.0)
            float r15 = r15 / r17
            float r15 = r15 / r11
            r14.didWriteData(r9, r15)
            r18 = r12
        L_0x0197:
            if (r1 != 0) goto L_0x019c
            r27.advance()
        L_0x019c:
            r9 = r1
            r1 = -1
            goto L_0x01b1
        L_0x019f:
            r35 = r6
            r25 = r10
            r37 = r14
            r1 = -1
            r6 = 0
            r17 = 1148846080(0x447a0000, float:1000.0)
            if (r12 != r1) goto L_0x01ad
            r9 = 1
            goto L_0x01b1
        L_0x01ad:
            r27.advance()
            r9 = 0
        L_0x01b1:
            if (r9 == 0) goto L_0x01b5
            r14 = 1
            goto L_0x01b7
        L_0x01b5:
            r14 = r37
        L_0x01b7:
            r1 = r27
            r6 = r35
            r10 = r25
            r9 = 1
            r12 = -1
            goto L_0x008f
        L_0x01c2:
            r25 = r10
            r1 = r27
            if (r7 < 0) goto L_0x01cb
            r1.unselectTrack(r7)
        L_0x01cb:
            if (r25 < 0) goto L_0x01d2
            r10 = r25
            r1.unselectTrack(r10)
        L_0x01d2:
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
