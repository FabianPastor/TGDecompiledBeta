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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v200, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v135, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v143, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v144, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v145, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v146, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v147, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v201, resolved type: org.telegram.messenger.video.InputSurface} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v148, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v204, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v152, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v154, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v206, resolved type: int} */
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
    /* JADX WARNING: Code restructure failed: missing block: B:1000:0x1146, code lost:
        r4 = r78;
        r14 = r3;
        r60 = r8;
        r9 = r10;
        r3 = r59;
        r40 = r13;
        r5 = r0;
        r6 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1001:0x1156, code lost:
        r2 = r17;
        r3 = r18;
        r54 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1003:0x115f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1004:0x1160, code lost:
        r4 = r78;
        r14 = r3;
        r9 = r10;
        r3 = r59;
        r54 = r83;
        r40 = r13;
        r5 = r0;
        r6 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1005:0x1170, code lost:
        r3 = r18;
        r35 = r30;
        r1 = 0;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1006:0x1178, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1007:0x1179, code lost:
        r4 = r78;
        r7 = r79;
        r9 = r81;
        r14 = r3;
        r3 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1008:0x1183, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1009:0x1184, code lost:
        r4 = r78;
        r7 = r79;
        r9 = r81;
        r3 = r59;
        r14 = r61;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1010:0x118e, code lost:
        r54 = r83;
        r5 = r0;
        r6 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1012:0x1199, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1013:0x119a, code lost:
        r4 = r78;
        r7 = r79;
        r9 = r81;
        r3 = r59;
        r14 = r61;
        r54 = r83;
        r5 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1014:0x11a8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1015:0x11a9, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:1016:0x11b8, code lost:
        r6 = r7;
        r3 = r18;
        r35 = r30;
        r1 = 0;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1017:0x11c0, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1018:0x11c1, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:1019:0x11d7, code lost:
        r27 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1021:0x11dd, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1022:0x11de, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:1023:0x11f1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1024:0x11f2, code lost:
        r9 = r81;
        r4 = r14;
        r62 = r24;
        r54 = r83;
        r5 = r0;
        r3 = r18;
        r35 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1025:0x1202, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1026:0x1203, code lost:
        r9 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1027:0x1206, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1028:0x1207, code lost:
        r9 = r81;
        r30 = r7;
        r4 = r14;
        r62 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1029:0x120f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1030:0x1210, code lost:
        r9 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1031:0x1211, code lost:
        r30 = r7;
        r4 = r14;
        r62 = r24;
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1032:0x1217, code lost:
        r54 = r83;
        r5 = r0;
        r35 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1033:0x121d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1034:0x121e, code lost:
        r9 = r81;
        r62 = r4;
        r4 = r14;
        r1 = 0;
        r3 = r79;
        r54 = r83;
        r35 = r85;
        r5 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1035:0x122b, code lost:
        r2 = -5;
        r6 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1036:0x122d, code lost:
        r14 = null;
        r27 = null;
        r40 = null;
        r59 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1056:0x1291, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1057:0x1292, code lost:
        r3 = r0;
        r1 = r15;
        r2 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1058:0x1298, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1059:0x1299, code lost:
        r1 = r0;
        r2 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1060:0x129d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1061:0x129f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1062:0x12a0, code lost:
        r11 = r76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1063:0x12a3, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1064:0x12a4, code lost:
        r11 = r76;
        r8 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1065:0x12a8, code lost:
        r6 = r1;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1066:0x12ac, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1067:0x12ad, code lost:
        r11 = r76;
        r8 = r77;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1072:0x12d0, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1073:0x12d1, code lost:
        r1 = r0;
        r6 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1074:0x12d3, code lost:
        r3 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1093:0x1312, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1094:0x1313, code lost:
        r8 = r77;
        r4 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1095:0x1317, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1096:0x1318, code lost:
        r3 = r0;
        r1 = r15;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1097:0x131d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1098:0x131e, code lost:
        r4 = r10;
        r8 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1099:0x1320, code lost:
        r11 = r12;
        r9 = r81;
        r3 = r79;
        r54 = r83;
        r35 = r85;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1100:0x132b, code lost:
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1101:0x132c, code lost:
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1106:0x1361, code lost:
        r1.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1110:?, code lost:
        r1.finishMovie();
        r15.endPresentationTime = r15.mediaMuxer.getLastFrameTimestamp(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1111:0x1374, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1112:0x1375, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1121:0x13b3, code lost:
        r4.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1125:?, code lost:
        r4.finishMovie();
        r1.endPresentationTime = r1.mediaMuxer.getLastFrameTimestamp(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1126:0x13c6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1127:0x13c7, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x038e, code lost:
        r2 = java.nio.ByteBuffer.allocate(r3);
        r7 = java.nio.ByteBuffer.allocate(r14.size - r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x0399, code lost:
        r18 = r5;
        r17 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:?, code lost:
        r2.put(r13, 0, r3).position(0);
        r7.put(r13, r3, r14.size - r3).position(0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x03af, code lost:
        r6 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x03e2, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x03e4, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x03e5, code lost:
        r18 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x03e9, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x03ea, code lost:
        r18 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x046d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x046e, code lost:
        r3 = r0;
        r2 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x0472, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x0473, code lost:
        r51 = r3;
        r10 = r36;
        r6 = r1;
        r2 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x0497, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x0498, code lost:
        r51 = r3;
        r10 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x04df, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x04e1, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x04e2, code lost:
        r6 = r1;
        r36 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x04e8, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x04e9, code lost:
        r18 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00c3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:273:0x04fc, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x04fd, code lost:
        r1 = r76;
        r3 = r36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:275:0x0501, code lost:
        r6 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0517, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x0518, code lost:
        r3 = r36;
        r6 = r76;
        r11 = r51;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00c4, code lost:
        r1 = r0;
        r50 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0521, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x0522, code lost:
        r3 = r36;
        r6 = r76;
        r11 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x052a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x052b, code lost:
        r50 = r9;
        r51 = r11;
        r6 = r1;
        r36 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x0533, code lost:
        r2 = -5;
        r30 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x0544, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x0545, code lost:
        r50 = r9;
        r51 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x054a, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x054b, code lost:
        r50 = r9;
        r11 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x054f, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x0550, code lost:
        r2 = -5;
        r6 = null;
        r30 = null;
        r36 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x05a5, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x05a6, code lost:
        r2 = r3;
        r1 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x05d7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x05d8, code lost:
        r54 = r83;
        r35 = r85;
        r1 = r0;
        r3 = r9;
        r4 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x05e0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x05e2, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x05e3, code lost:
        r9 = r50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x05e5, code lost:
        r4 = r78;
        r54 = r83;
        r35 = r85;
        r1 = r0;
        r3 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x05ed, code lost:
        r8 = r11;
        r11 = r12;
        r6 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x05f2, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x0635, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x0636, code lost:
        r8 = r77;
        r54 = r83;
        r35 = r85;
        r1 = r0;
        r3 = r9;
        r4 = r10;
        r11 = r12;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x0693, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x0694, code lost:
        r11 = r76;
        r8 = r77;
        r3 = r79;
        r9 = r81;
        r54 = r83;
        r35 = r85;
        r1 = r0;
        r4 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x06a4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x06a5, code lost:
        r11 = r76;
        r8 = r77;
        r3 = r79;
        r54 = r83;
        r35 = r85;
        r1 = r0;
        r4 = r10;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x070c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x070d, code lost:
        r9 = r81;
        r54 = r83;
        r5 = r0;
        r62 = r4;
        r4 = r14;
        r35 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x0717, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x073c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x073d, code lost:
        r9 = r81;
        r54 = r83;
        r5 = r0;
        r62 = r4;
        r35 = r7;
        r4 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x0761, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0762, code lost:
        r54 = r83;
        r5 = r0;
        r9 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x0766, code lost:
        r35 = r7;
        r4 = r14;
        r62 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x0793, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x0794, code lost:
        r9 = r81;
        r54 = r83;
        r5 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x07f9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x07fa, code lost:
        r9 = r81;
        r54 = r83;
        r5 = r0;
        r3 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x0891, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0892, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x0914, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x0915, code lost:
        r4 = r78;
        r6 = r79;
        r9 = r81;
        r54 = r83;
        r5 = r0;
        r14 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x093a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x093b, code lost:
        r4 = r78;
        r6 = r79;
        r54 = r83;
        r5 = r0;
        r14 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x0962, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0964, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x0965, code lost:
        r8 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0967, code lost:
        r4 = r78;
        r6 = r79;
        r5 = r0;
        r14 = r3;
        r54 = r8;
        r9 = r10;
        r40 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x0974, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0975, code lost:
        r4 = r78;
        r6 = r79;
        r5 = r0;
        r14 = r3;
        r54 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x098b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x098c, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x09f6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x09f7, code lost:
        r4 = r78;
        r6 = r79;
        r5 = r0;
        r14 = r3;
        r54 = r8;
        r9 = r10;
        r40 = r13;
        r2 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x0a62, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x0a63, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x0a81, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x0a82, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x019d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x019e, code lost:
        r6 = r76;
        r1 = r0;
        r11 = r51;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x0ae6, code lost:
        if (r13.presentationTimeUs < r8) goto L_0x0aeb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x0b24, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x0b25, code lost:
        r60 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x0b2d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x0b2e, code lost:
        r60 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0b30, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0b77, code lost:
        r0 = e;
        r60 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0b8b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0b8c, code lost:
        r60 = r8;
        r4 = r78;
        r6 = r79;
        r40 = r13;
        r5 = r0;
        r14 = r3;
        r9 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x01c5, code lost:
        r1 = r2;
        r2 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r6;
        r6 = r7;
        r7 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x0d70, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x0d8e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x0d8f, code lost:
        r61 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x0d91, code lost:
        r4 = r78;
        r40 = r85;
        r5 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x0d97, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x0d98, code lost:
        r61 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x0e16, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x021b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x021c, code lost:
        r6 = r1;
        r2 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x1078, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:0x107a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:962:0x107b, code lost:
        r2 = r83;
        r40 = r85;
        r5 = r0;
        r59 = r3;
        r54 = r54;
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:976:0x10d0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:977:0x10d1, code lost:
        r4 = r78;
        r9 = r10;
        r3 = r59;
        r14 = r61;
        r54 = r54;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:986:0x1121, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:987:0x1122, code lost:
        r40 = r85;
        r5 = r0;
        r59 = r3;
        r54 = r54;
        r14 = r14;
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:990:0x112c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x112d, code lost:
        r3 = r0;
        r2 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:999:0x1145, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:65:0x01b3, B:1037:0x1234] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:65:0x01b3, B:1042:0x123b] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:65:0x01b3, B:1045:0x1257] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:65:0x01b3, B:1048:0x1262] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:65:0x01b3, B:1070:0x12cc] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:65:0x01b3, B:824:0x0e36] */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1054:0x1280 A[Catch:{ Exception -> 0x1298, all -> 0x1291 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1056:0x1291 A[ExcHandler: all (r0v10 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r17 
      PHI: (r17v1 int) = (r17v2 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int) binds: [B:1051:0x1277, B:548:0x09e6, B:549:?, B:559:0x0a0a, B:594:0x0aa3, B:595:?, B:600:0x0abc, B:601:?, B:610:0x0ad6, B:611:?, B:622:0x0aec, B:623:?, B:625:0x0afc, B:629:0x0b06, B:616:0x0ae2, B:617:?, B:607:0x0ad3, B:608:?, B:603:0x0ac0, B:604:?, B:598:0x0ab4, B:599:?, B:564:0x0a1c, B:573:0x0a34, B:577:0x0a46, B:552:0x09ed] A[DONT_GENERATE, DONT_INLINE], Splitter:B:548:0x09e6] */
    /* JADX WARNING: Removed duplicated region for block: B:1068:0x12b4  */
    /* JADX WARNING: Removed duplicated region for block: B:1070:0x12cc A[SYNTHETIC, Splitter:B:1070:0x12cc] */
    /* JADX WARNING: Removed duplicated region for block: B:1076:0x12d9 A[Catch:{ Exception -> 0x12d0, all -> 0x05f2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1078:0x12de A[Catch:{ Exception -> 0x12d0, all -> 0x05f2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1080:0x12e6 A[Catch:{ Exception -> 0x12d0, all -> 0x05f2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1085:0x12f4  */
    /* JADX WARNING: Removed duplicated region for block: B:1088:0x12fb A[SYNTHETIC, Splitter:B:1088:0x12fb] */
    /* JADX WARNING: Removed duplicated region for block: B:1095:0x1317 A[ExcHandler: all (r0v6 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:1:0x0017] */
    /* JADX WARNING: Removed duplicated region for block: B:1106:0x1361  */
    /* JADX WARNING: Removed duplicated region for block: B:1109:0x1368 A[SYNTHETIC, Splitter:B:1109:0x1368] */
    /* JADX WARNING: Removed duplicated region for block: B:1115:0x1381  */
    /* JADX WARNING: Removed duplicated region for block: B:1121:0x13b3  */
    /* JADX WARNING: Removed duplicated region for block: B:1124:0x13ba A[SYNTHETIC, Splitter:B:1124:0x13ba] */
    /* JADX WARNING: Removed duplicated region for block: B:1171:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x03e4 A[ExcHandler: all (th java.lang.Throwable), PHI: r5 
      PHI: (r5v171 int) = (r5v170 int), (r5v170 int), (r5v172 int) binds: [B:180:0x035b, B:181:?, B:186:0x0378] A[DONT_GENERATE, DONT_INLINE], Splitter:B:180:0x035b] */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x040f A[Catch:{ Exception -> 0x0497, all -> 0x05f2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0411 A[Catch:{ Exception -> 0x0497, all -> 0x05f2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x043b  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x046d A[ExcHandler: all (r0v127 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:234:0x043d] */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x04df A[ExcHandler: all (th java.lang.Throwable), PHI: r18 
      PHI: (r18v20 int) = (r18v19 int), (r18v30 int), (r18v34 int), (r18v34 int), (r18v41 int), (r18v41 int) binds: [B:256:0x04a8, B:205:0x03cc, B:197:0x039e, B:198:?, B:167:0x0332, B:168:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:167:0x0332] */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x04e8 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:158:0x0313] */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x055a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x05a1 A[SYNTHETIC, Splitter:B:310:0x05a1] */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x05a5 A[Catch:{ Exception -> 0x05aa, all -> 0x05a5 }, ExcHandler: all (th java.lang.Throwable), PHI: r3 
      PHI: (r3v142 int) = (r3v143 int), (r3v165 int), (r3v165 int), (r3v165 int), (r3v165 int) binds: [B:310:0x05a1, B:131:0x02ac, B:117:0x0294, B:118:?, B:85:0x0200] A[DONT_GENERATE, DONT_INLINE], Splitter:B:85:0x0200] */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x05ba A[Catch:{ Exception -> 0x05aa, all -> 0x05a5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x05bf A[Catch:{ Exception -> 0x05aa, all -> 0x05a5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:329:0x05f2 A[ExcHandler: all (th java.lang.Throwable), PHI: r2 
      PHI: (r2v168 int) = (r2v20 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v94 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v187 int), (r2v187 int), (r2v215 int) binds: [B:1070:0x12cc, B:1037:0x1234, B:1038:?, B:1042:0x123b, B:1043:?, B:1045:0x1257, B:1046:?, B:1048:0x1262, B:1049:?, B:824:0x0e36, B:294:0x0556, B:295:?, B:300:0x0561, B:301:?, B:303:0x056d, B:304:?, B:306:0x0577, B:307:?, B:65:0x01b3, B:66:?, B:223:0x0409] A[DONT_GENERATE, DONT_INLINE], Splitter:B:65:0x01b3] */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x06bc  */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x077c  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x079a  */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x07a4 A[SYNTHETIC, Splitter:B:436:0x07a4] */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x07e2 A[SYNTHETIC, Splitter:B:442:0x07e2] */
    /* JADX WARNING: Removed duplicated region for block: B:475:0x0881 A[SYNTHETIC, Splitter:B:475:0x0881] */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x08a6  */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x08ac A[SYNTHETIC, Splitter:B:482:0x08ac] */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x08da  */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x08dd  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0982  */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x09a2  */
    /* JADX WARNING: Removed duplicated region for block: B:539:0x09ac  */
    /* JADX WARNING: Removed duplicated region for block: B:540:0x09af  */
    /* JADX WARNING: Removed duplicated region for block: B:545:0x09cf A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:559:0x0a0a A[SYNTHETIC, Splitter:B:559:0x0a0a] */
    /* JADX WARNING: Removed duplicated region for block: B:648:0x0b57 A[Catch:{ Exception -> 0x0b77, all -> 0x1291 }] */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0b68 A[Catch:{ Exception -> 0x0b77, all -> 0x1291 }] */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0b89  */
    /* JADX WARNING: Removed duplicated region for block: B:661:0x0b9b  */
    /* JADX WARNING: Removed duplicated region for block: B:664:0x0bc0 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:669:0x0be4  */
    /* JADX WARNING: Removed duplicated region for block: B:670:0x0bed  */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:776:0x0d64 A[Catch:{ Exception -> 0x0d8e, all -> 0x112c }] */
    /* JADX WARNING: Removed duplicated region for block: B:789:0x0d83 A[Catch:{ Exception -> 0x0e16, all -> 0x112c }] */
    /* JADX WARNING: Removed duplicated region for block: B:827:0x0e3c A[Catch:{ Exception -> 0x10d0, all -> 0x05f2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:828:0x0e3e A[Catch:{ Exception -> 0x10d0, all -> 0x05f2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x0e4b  */
    /* JADX WARNING: Removed duplicated region for block: B:833:0x0e6a  */
    /* JADX WARNING: Removed duplicated region for block: B:900:0x0fa3  */
    /* JADX WARNING: Removed duplicated region for block: B:901:0x0fa6  */
    /* JADX WARNING: Removed duplicated region for block: B:908:0x0fb3 A[SYNTHETIC, Splitter:B:908:0x0fb3] */
    /* JADX WARNING: Removed duplicated region for block: B:913:0x0fd7 A[Catch:{ Exception -> 0x0var_, all -> 0x1078 }] */
    /* JADX WARNING: Removed duplicated region for block: B:919:0x0fe8 A[Catch:{ Exception -> 0x0var_, all -> 0x1078 }] */
    /* JADX WARNING: Removed duplicated region for block: B:920:0x0feb A[Catch:{ Exception -> 0x0var_, all -> 0x1078 }] */
    /* JADX WARNING: Removed duplicated region for block: B:928:0x1000  */
    /* JADX WARNING: Removed duplicated region for block: B:947:0x1034 A[Catch:{ Exception -> 0x107a, all -> 0x1078 }] */
    /* JADX WARNING: Removed duplicated region for block: B:950:0x1040 A[Catch:{ Exception -> 0x107a, all -> 0x1078 }] */
    /* JADX WARNING: Removed duplicated region for block: B:960:0x1078 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:934:0x1012] */
    /* JADX WARNING: Removed duplicated region for block: B:990:0x112c A[ExcHandler: all (r0v35 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r12 
      PHI: (r12v7 int) = (r12v3 int), (r12v3 int), (r12v3 int), (r12v8 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int) binds: [B:666:0x0bdf, B:667:?, B:672:0x0bf9, B:845:0x0eae, B:714:0x0cc0, B:715:?, B:979:0x10e1, B:727:0x0cd6, B:728:?, B:768:0x0d52, B:769:?, B:771:0x0d56, B:784:0x0d74, B:785:?, B:787:0x0d7d, B:779:0x0d6b, B:780:?, B:737:0x0cf1, B:746:0x0d10, B:732:0x0cde, B:721:0x0ccb, B:718:0x0cc6, B:719:?, B:691:0x0CLASSNAME, B:697:0x0c5e, B:680:0x0c1b] A[DONT_GENERATE, DONT_INLINE], Splitter:B:768:0x0d52] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:300:0x0561=Splitter:B:300:0x0561, B:1037:0x1234=Splitter:B:1037:0x1234, B:294:0x0556=Splitter:B:294:0x0556, B:824:0x0e36=Splitter:B:824:0x0e36, B:223:0x0409=Splitter:B:223:0x0409, B:1042:0x123b=Splitter:B:1042:0x123b} */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:37:0x0100=Splitter:B:37:0x0100, B:433:0x07a0=Splitter:B:433:0x07a0, B:541:0x09b1=Splitter:B:541:0x09b1, B:456:0x0804=Splitter:B:456:0x0804, B:61:0x01a7=Splitter:B:61:0x01a7, B:16:0x0089=Splitter:B:16:0x0089, B:490:0x08ce=Splitter:B:490:0x08ce} */
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
            android.media.MediaCodec$BufferInfo r7 = new android.media.MediaCodec$BufferInfo     // Catch:{ Exception -> 0x131d, all -> 0x1317 }
            r7.<init>()     // Catch:{ Exception -> 0x131d, all -> 0x1317 }
            org.telegram.messenger.video.Mp4Movie r2 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ Exception -> 0x131d, all -> 0x1317 }
            r2.<init>()     // Catch:{ Exception -> 0x131d, all -> 0x1317 }
            r1 = r73
            r2.setCacheFile(r1)     // Catch:{ Exception -> 0x131d, all -> 0x1317 }
            r2.setRotation(r6)     // Catch:{ Exception -> 0x131d, all -> 0x1317 }
            r2.setSize(r12, r11)     // Catch:{ Exception -> 0x131d, all -> 0x1317 }
            org.telegram.messenger.video.MP4Builder r6 = new org.telegram.messenger.video.MP4Builder     // Catch:{ Exception -> 0x131d, all -> 0x1317 }
            r6.<init>()     // Catch:{ Exception -> 0x131d, all -> 0x1317 }
            r14 = r75
            org.telegram.messenger.video.MP4Builder r2 = r6.createMovie(r2, r14)     // Catch:{ Exception -> 0x131d, all -> 0x1317 }
            r15.mediaMuxer = r2     // Catch:{ Exception -> 0x131d, all -> 0x1317 }
            float r2 = (float) r4     // Catch:{ Exception -> 0x131d, all -> 0x1317 }
            r19 = 1148846080(0x447a0000, float:1000.0)
            float r20 = r2 / r19
            r21 = 1000(0x3e8, double:4.94E-321)
            long r1 = r4 * r21
            r15.endPresentationTime = r1     // Catch:{ Exception -> 0x131d, all -> 0x1317 }
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x131d, all -> 0x1317 }
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
            if (r94 == 0) goto L_0x0609
            int r31 = (r85 > r6 ? 1 : (r85 == r6 ? 0 : -1))
            if (r31 < 0) goto L_0x0084
            r2 = 1157234688(0x44fa0000, float:2000.0)
            int r2 = (r20 > r2 ? 1 : (r20 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x006f
            r2 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            r9 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x0089
        L_0x006f:
            r2 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r2 = (r20 > r2 ? 1 : (r20 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x007d
            r2 = 2200000(0x2191c0, float:3.082857E-39)
            r9 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x0089
        L_0x007d:
            r2 = 1560000(0x17cdc0, float:2.186026E-39)
            r9 = 1560000(0x17cdc0, float:2.186026E-39)
            goto L_0x0089
        L_0x0084:
            if (r9 > 0) goto L_0x0089
            r9 = 921600(0xe1000, float:1.291437E-39)
        L_0x0089:
            int r2 = r12 % 16
            r31 = 1098907648(0x41800000, float:16.0)
            if (r2 == 0) goto L_0x00c9
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            if (r2 == 0) goto L_0x00b8
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            r2.<init>()     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            java.lang.String r6 = "changing width from "
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            r2.append(r12)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            float r6 = (float) r12     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            float r6 = r6 / r31
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
        L_0x00b8:
            float r2 = (float) r12     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            float r2 = r2 / r31
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            int r2 = r2 * 16
            r12 = r2
            goto L_0x00c9
        L_0x00c3:
            r0 = move-exception
            r1 = r0
            r50 = r9
            goto L_0x0550
        L_0x00c9:
            int r2 = r11 % 16
            if (r2 == 0) goto L_0x0100
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            if (r2 == 0) goto L_0x00f6
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            r2.<init>()     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            java.lang.String r6 = "changing height from "
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            r2.append(r11)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            float r6 = (float) r11     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            float r6 = r6 / r31
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
        L_0x00f6:
            float r2 = (float) r11     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            float r2 = r2 / r31
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            int r2 = r2 * 16
            r11 = r2
        L_0x0100:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0544, all -> 0x1317 }
            if (r2 == 0) goto L_0x0128
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            r2.<init>()     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            java.lang.String r6 = "create photo encoder "
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            r2.append(r12)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            java.lang.String r6 = " "
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            r2.append(r11)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            java.lang.String r6 = " duration = "
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            r2.append(r4)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00c3, all -> 0x1317 }
        L_0x0128:
            r7 = r29
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r7, r12, r11)     // Catch:{ Exception -> 0x0544, all -> 0x1317 }
            java.lang.String r6 = "color-format"
            r29 = r1
            r1 = 2130708361(0x7var_, float:1.701803E38)
            r2.setInteger(r6, r1)     // Catch:{ Exception -> 0x0544, all -> 0x1317 }
            java.lang.String r1 = "bitrate"
            r2.setInteger(r1, r9)     // Catch:{ Exception -> 0x0544, all -> 0x1317 }
            java.lang.String r1 = "frame-rate"
            r2.setInteger(r1, r10)     // Catch:{ Exception -> 0x0544, all -> 0x1317 }
            java.lang.String r1 = "i-frame-interval"
            r6 = 2
            r2.setInteger(r1, r6)     // Catch:{ Exception -> 0x0544, all -> 0x1317 }
            android.media.MediaCodec r1 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x0544, all -> 0x1317 }
            r3 = 1
            r6 = 0
            r1.configure(r2, r6, r6, r3)     // Catch:{ Exception -> 0x0537, all -> 0x1317 }
            org.telegram.messenger.video.InputSurface r2 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x0537, all -> 0x1317 }
            android.view.Surface r3 = r1.createInputSurface()     // Catch:{ Exception -> 0x0537, all -> 0x1317 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0537, all -> 0x1317 }
            r2.makeCurrent()     // Catch:{ Exception -> 0x052a, all -> 0x1317 }
            r1.start()     // Catch:{ Exception -> 0x052a, all -> 0x1317 }
            org.telegram.messenger.video.OutputSurface r30 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x052a, all -> 0x1317 }
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
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0521, all -> 0x1317 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0517, all -> 0x1317 }
            if (r1 >= r14) goto L_0x01a6
            java.nio.ByteBuffer[] r6 = r76.getOutputBuffers()     // Catch:{ Exception -> 0x019d, all -> 0x1317 }
            goto L_0x01a7
        L_0x019d:
            r0 = move-exception
            r6 = r76
            r1 = r0
            r11 = r51
            r2 = -5
            goto L_0x0556
        L_0x01a6:
            r6 = 0
        L_0x01a7:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x0517, all -> 0x1317 }
            r4 = r6
            r1 = 1
            r2 = -5
            r3 = 0
            r5 = 0
            r6 = 0
            r7 = 0
        L_0x01b1:
            if (r6 != 0) goto L_0x0505
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x04fc, all -> 0x05f2 }
            r8 = r3 ^ 1
            r9 = r7
            r7 = r6
            r6 = r5
            r5 = r4
            r4 = r3
            r3 = r2
            r2 = r1
            r1 = 1
        L_0x01c0:
            if (r8 != 0) goto L_0x01cd
            if (r1 == 0) goto L_0x01c5
            goto L_0x01cd
        L_0x01c5:
            r1 = r2
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r9
            goto L_0x01b1
        L_0x01cd:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x04f1, all -> 0x04e8 }
            if (r90 == 0) goto L_0x01d7
            r10 = 22000(0x55f0, double:1.08694E-319)
            r14 = r48
            goto L_0x01db
        L_0x01d7:
            r14 = r48
            r10 = 2500(0x9c4, double:1.235E-320)
        L_0x01db:
            r69 = r1
            r1 = r76
            r76 = r69
            int r10 = r1.dequeueOutputBuffer(r14, r10)     // Catch:{ Exception -> 0x04e6, all -> 0x04e8 }
            r11 = -1
            if (r10 != r11) goto L_0x01fd
            r17 = r2
            r16 = r6
            r79 = r8
            r8 = r35
            r11 = r44
            r13 = r49
            r76 = 0
        L_0x01f6:
            r2 = -1
            r6 = r5
            r5 = r3
            r3 = r51
            goto L_0x041b
        L_0x01fd:
            r11 = -3
            if (r10 != r11) goto L_0x0220
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            r77 = r7
            r7 = 21
            if (r11 >= r7) goto L_0x020c
            java.nio.ByteBuffer[] r5 = r1.getOutputBuffers()     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
        L_0x020c:
            r7 = r77
            r17 = r2
            r16 = r6
            r79 = r8
            r8 = r35
            r11 = r44
        L_0x0218:
            r13 = r49
            goto L_0x01f6
        L_0x021b:
            r0 = move-exception
            r6 = r1
            r2 = r3
            goto L_0x0502
        L_0x0220:
            r77 = r7
            r7 = -2
            if (r10 != r7) goto L_0x0286
            android.media.MediaFormat r7 = r1.getOutputFormat()     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            if (r11 == 0) goto L_0x0244
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            r11.<init>()     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            r79 = r8
            java.lang.String r8 = "photo encoder new format "
            r11.append(r8)     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            r11.append(r7)     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            java.lang.String r8 = r11.toString()     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            org.telegram.messenger.FileLog.d(r8)     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            goto L_0x0246
        L_0x0244:
            r79 = r8
        L_0x0246:
            r8 = -5
            if (r3 != r8) goto L_0x027b
            if (r7 == 0) goto L_0x027b
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            r8 = 0
            int r3 = r11.addTrack(r7, r8)     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            r11 = r45
            boolean r16 = r7.containsKey(r11)     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            if (r16 == 0) goto L_0x0279
            int r8 = r7.getInteger(r11)     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            r45 = r11
            r11 = 1
            if (r8 != r11) goto L_0x027b
            r8 = r35
            java.nio.ByteBuffer r6 = r7.getByteBuffer(r8)     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            r11 = r44
            java.nio.ByteBuffer r7 = r7.getByteBuffer(r11)     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            int r6 = r6.limit()     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            int r7 = r7.limit()     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            int r6 = r6 + r7
            goto L_0x027f
        L_0x0279:
            r45 = r11
        L_0x027b:
            r8 = r35
            r11 = r44
        L_0x027f:
            r7 = r77
            r17 = r2
            r16 = r6
            goto L_0x0218
        L_0x0286:
            r79 = r8
            r8 = r35
            r11 = r44
            if (r10 < 0) goto L_0x04c4
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x04e6, all -> 0x04e8 }
            r13 = 21
            if (r7 >= r13) goto L_0x0297
            r7 = r5[r10]     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            goto L_0x029b
        L_0x0297:
            java.nio.ByteBuffer r7 = r1.getOutputBuffer(r10)     // Catch:{ Exception -> 0x04e6, all -> 0x04e8 }
        L_0x029b:
            if (r7 == 0) goto L_0x04a4
            int r13 = r14.size     // Catch:{ Exception -> 0x049e, all -> 0x04e8 }
            r77 = r5
            r5 = 1
            if (r13 <= r5) goto L_0x03fd
            int r13 = r14.flags     // Catch:{ Exception -> 0x03f2, all -> 0x04e8 }
            r5 = 2
            r13 = r13 & r5
            if (r13 != 0) goto L_0x0352
            if (r6 == 0) goto L_0x02be
            int r13 = r14.flags     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            r16 = 1
            r13 = r13 & 1
            if (r13 == 0) goto L_0x02be
            int r13 = r14.offset     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            int r13 = r13 + r6
            r14.offset = r13     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            int r13 = r14.size     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            int r13 = r13 - r6
            r14.size = r13     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
        L_0x02be:
            if (r2 == 0) goto L_0x0313
            int r13 = r14.flags     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            r16 = 1
            r13 = r13 & 1
            if (r13 == 0) goto L_0x0313
            int r2 = r14.size     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            r13 = 100
            if (r2 <= r13) goto L_0x0312
            int r2 = r14.offset     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            r7.position(r2)     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            byte[] r2 = new byte[r13]     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            r7.get(r2)     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            r13 = 0
            r16 = 0
        L_0x02db:
            r5 = 96
            if (r13 >= r5) goto L_0x0312
            byte r5 = r2[r13]     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            if (r5 != 0) goto L_0x030a
            int r5 = r13 + 1
            byte r5 = r2[r5]     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            if (r5 != 0) goto L_0x030a
            int r5 = r13 + 2
            byte r5 = r2[r5]     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            if (r5 != 0) goto L_0x030a
            int r5 = r13 + 3
            byte r5 = r2[r5]     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            r17 = r2
            r2 = 1
            if (r5 != r2) goto L_0x030c
            int r5 = r16 + 1
            if (r5 <= r2) goto L_0x0307
            int r2 = r14.offset     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            int r2 = r2 + r13
            r14.offset = r2     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            int r2 = r14.size     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            int r2 = r2 - r13
            r14.size = r2     // Catch:{ Exception -> 0x021b, all -> 0x05a5 }
            goto L_0x0312
        L_0x0307:
            r16 = r5
            goto L_0x030c
        L_0x030a:
            r17 = r2
        L_0x030c:
            int r13 = r13 + 1
            r2 = r17
            r5 = 2
            goto L_0x02db
        L_0x0312:
            r2 = 0
        L_0x0313:
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x034d, all -> 0x04e8 }
            r16 = r6
            r13 = 1
            long r5 = r5.writeSampleData(r3, r7, r14, r13)     // Catch:{ Exception -> 0x034d, all -> 0x04e8 }
            r17 = r2
            r13 = r3
            r2 = 0
            int r7 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r7 == 0) goto L_0x0343
            org.telegram.messenger.MediaController$VideoConvertorListener r7 = r15.callback     // Catch:{ Exception -> 0x033e, all -> 0x0339 }
            if (r7 == 0) goto L_0x0343
            org.telegram.messenger.MediaController$VideoConvertorListener r7 = r15.callback     // Catch:{ Exception -> 0x033e, all -> 0x0339 }
            r18 = r13
            float r13 = (float) r2
            float r13 = r13 / r19
            float r13 = r13 / r20
            r7.didWriteData(r5, r13)     // Catch:{ Exception -> 0x0336, all -> 0x04df }
            goto L_0x0345
        L_0x0336:
            r0 = move-exception
            goto L_0x04f8
        L_0x0339:
            r0 = move-exception
            r18 = r13
            goto L_0x04eb
        L_0x033e:
            r0 = move-exception
            r18 = r13
            goto L_0x04f8
        L_0x0343:
            r18 = r13
        L_0x0345:
            r2 = r18
            r13 = r49
            r3 = r51
            goto L_0x0409
        L_0x034d:
            r0 = move-exception
            r18 = r3
            goto L_0x04f8
        L_0x0352:
            r5 = r3
            r16 = r6
            r13 = -5
            r6 = r2
            r2 = 0
            if (r5 != r13) goto L_0x03ed
            int r13 = r14.size     // Catch:{ Exception -> 0x03e9, all -> 0x03e4 }
            byte[] r13 = new byte[r13]     // Catch:{ Exception -> 0x03e9, all -> 0x03e4 }
            int r2 = r14.offset     // Catch:{ Exception -> 0x03e9, all -> 0x03e4 }
            int r3 = r14.size     // Catch:{ Exception -> 0x03e9, all -> 0x03e4 }
            int r2 = r2 + r3
            r7.limit(r2)     // Catch:{ Exception -> 0x03e9, all -> 0x03e4 }
            int r2 = r14.offset     // Catch:{ Exception -> 0x03e9, all -> 0x03e4 }
            r7.position(r2)     // Catch:{ Exception -> 0x03e9, all -> 0x03e4 }
            r7.get(r13)     // Catch:{ Exception -> 0x03e9, all -> 0x03e4 }
            int r2 = r14.size     // Catch:{ Exception -> 0x03e9, all -> 0x03e4 }
            r3 = 1
            int r2 = r2 - r3
        L_0x0373:
            if (r2 < 0) goto L_0x03c2
            r7 = 3
            if (r2 <= r7) goto L_0x03c2
            byte r7 = r13[r2]     // Catch:{ Exception -> 0x03bd, all -> 0x03e4 }
            if (r7 != r3) goto L_0x03b1
            int r3 = r2 + -1
            byte r3 = r13[r3]     // Catch:{ Exception -> 0x03bd, all -> 0x03e4 }
            if (r3 != 0) goto L_0x03b1
            int r3 = r2 + -2
            byte r3 = r13[r3]     // Catch:{ Exception -> 0x03bd, all -> 0x03e4 }
            if (r3 != 0) goto L_0x03b1
            int r3 = r2 + -3
            byte r7 = r13[r3]     // Catch:{ Exception -> 0x03bd, all -> 0x03e4 }
            if (r7 != 0) goto L_0x03b1
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r3)     // Catch:{ Exception -> 0x03bd, all -> 0x03e4 }
            int r7 = r14.size     // Catch:{ Exception -> 0x03bd, all -> 0x03e4 }
            int r7 = r7 - r3
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.allocate(r7)     // Catch:{ Exception -> 0x03bd, all -> 0x03e4 }
            r18 = r5
            r17 = r6
            r5 = 0
            java.nio.ByteBuffer r6 = r2.put(r13, r5, r3)     // Catch:{ Exception -> 0x0336, all -> 0x04df }
            r6.position(r5)     // Catch:{ Exception -> 0x0336, all -> 0x04df }
            int r6 = r14.size     // Catch:{ Exception -> 0x0336, all -> 0x04df }
            int r6 = r6 - r3
            java.nio.ByteBuffer r3 = r7.put(r13, r3, r6)     // Catch:{ Exception -> 0x0336, all -> 0x04df }
            r3.position(r5)     // Catch:{ Exception -> 0x0336, all -> 0x04df }
            r6 = r2
            goto L_0x03c8
        L_0x03b1:
            r18 = r5
            r17 = r6
            int r2 = r2 + -1
            r6 = r17
            r5 = r18
            r3 = 1
            goto L_0x0373
        L_0x03bd:
            r0 = move-exception
            r18 = r5
            goto L_0x04f8
        L_0x03c2:
            r18 = r5
            r17 = r6
            r6 = 0
            r7 = 0
        L_0x03c8:
            r13 = r49
            r3 = r51
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r13, r12, r3)     // Catch:{ Exception -> 0x03e2, all -> 0x04df }
            if (r6 == 0) goto L_0x03da
            if (r7 == 0) goto L_0x03da
            r2.setByteBuffer(r8, r6)     // Catch:{ Exception -> 0x03e2, all -> 0x04df }
            r2.setByteBuffer(r11, r7)     // Catch:{ Exception -> 0x03e2, all -> 0x04df }
        L_0x03da:
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x03e2, all -> 0x04df }
            r6 = 0
            int r2 = r5.addTrack(r2, r6)     // Catch:{ Exception -> 0x03e2, all -> 0x04df }
            goto L_0x0409
        L_0x03e2:
            r0 = move-exception
            goto L_0x03f7
        L_0x03e4:
            r0 = move-exception
            r18 = r5
            goto L_0x04eb
        L_0x03e9:
            r0 = move-exception
            r18 = r5
            goto L_0x03f5
        L_0x03ed:
            r18 = r5
            r17 = r6
            goto L_0x0403
        L_0x03f2:
            r0 = move-exception
            r18 = r3
        L_0x03f5:
            r3 = r51
        L_0x03f7:
            r6 = r1
            r11 = r3
            r2 = r18
            goto L_0x0542
        L_0x03fd:
            r17 = r2
            r18 = r3
            r16 = r6
        L_0x0403:
            r13 = r49
            r3 = r51
            r2 = r18
        L_0x0409:
            int r5 = r14.flags     // Catch:{ Exception -> 0x0497, all -> 0x05f2 }
            r5 = r5 & 4
            if (r5 == 0) goto L_0x0411
            r5 = 1
            goto L_0x0412
        L_0x0411:
            r5 = 0
        L_0x0412:
            r6 = 0
            r1.releaseOutputBuffer(r10, r6)     // Catch:{ Exception -> 0x0497, all -> 0x05f2 }
            r6 = r77
            r7 = r5
            r5 = r2
            r2 = -1
        L_0x041b:
            if (r10 == r2) goto L_0x043b
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
        L_0x0433:
            r69 = r1
            r1 = r76
            r76 = r69
            goto L_0x01c0
        L_0x043b:
            if (r4 != 0) goto L_0x047b
            r30.drawImage()     // Catch:{ Exception -> 0x0472, all -> 0x046d }
            float r2 = (float) r9
            r10 = 1106247680(0x41var_, float:30.0)
            float r2 = r2 / r10
            float r2 = r2 * r19
            float r2 = r2 * r19
            float r2 = r2 * r19
            r51 = r3
            long r2 = (long) r2
            r10 = r36
            r10.setPresentationTime(r2)     // Catch:{ Exception -> 0x0466, all -> 0x046d }
            r10.swapBuffers()     // Catch:{ Exception -> 0x0466, all -> 0x046d }
            int r9 = r9 + 1
            float r2 = (float) r9     // Catch:{ Exception -> 0x0466, all -> 0x046d }
            r3 = 1106247680(0x41var_, float:30.0)
            float r3 = r3 * r20
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 < 0) goto L_0x047f
            r1.signalEndOfInputStream()     // Catch:{ Exception -> 0x0466, all -> 0x046d }
            r2 = 0
            r4 = 1
            goto L_0x0481
        L_0x0466:
            r0 = move-exception
            r6 = r1
            r2 = r5
            r36 = r10
            goto L_0x0502
        L_0x046d:
            r0 = move-exception
            r3 = r0
            r2 = r5
            goto L_0x05f4
        L_0x0472:
            r0 = move-exception
            r51 = r3
            r10 = r36
            r6 = r1
            r2 = r5
            goto L_0x0502
        L_0x047b:
            r51 = r3
            r10 = r36
        L_0x047f:
            r2 = r79
        L_0x0481:
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
            goto L_0x0433
        L_0x0497:
            r0 = move-exception
            r51 = r3
            r10 = r36
            goto L_0x0501
        L_0x049e:
            r0 = move-exception
            r18 = r3
            r10 = r36
            goto L_0x04f8
        L_0x04a4:
            r18 = r3
            r3 = r36
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x04e1, all -> 0x04df }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04e1, all -> 0x04df }
            r4.<init>()     // Catch:{ Exception -> 0x04e1, all -> 0x04df }
            java.lang.String r5 = "encoderOutputBuffer "
            r4.append(r5)     // Catch:{ Exception -> 0x04e1, all -> 0x04df }
            r4.append(r10)     // Catch:{ Exception -> 0x04e1, all -> 0x04df }
            java.lang.String r5 = " was null"
            r4.append(r5)     // Catch:{ Exception -> 0x04e1, all -> 0x04df }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x04e1, all -> 0x04df }
            r2.<init>(r4)     // Catch:{ Exception -> 0x04e1, all -> 0x04df }
            throw r2     // Catch:{ Exception -> 0x04e1, all -> 0x04df }
        L_0x04c4:
            r18 = r3
            r3 = r36
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x04e1, all -> 0x04df }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04e1, all -> 0x04df }
            r4.<init>()     // Catch:{ Exception -> 0x04e1, all -> 0x04df }
            java.lang.String r5 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x04e1, all -> 0x04df }
            r4.append(r10)     // Catch:{ Exception -> 0x04e1, all -> 0x04df }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x04e1, all -> 0x04df }
            r2.<init>(r4)     // Catch:{ Exception -> 0x04e1, all -> 0x04df }
            throw r2     // Catch:{ Exception -> 0x04e1, all -> 0x04df }
        L_0x04df:
            r0 = move-exception
            goto L_0x04eb
        L_0x04e1:
            r0 = move-exception
            r6 = r1
            r36 = r3
            goto L_0x04f9
        L_0x04e6:
            r0 = move-exception
            goto L_0x04f4
        L_0x04e8:
            r0 = move-exception
            r18 = r3
        L_0x04eb:
            r3 = r0
            r1 = r15
            r2 = r18
            goto L_0x13af
        L_0x04f1:
            r0 = move-exception
            r1 = r76
        L_0x04f4:
            r18 = r3
            r3 = r36
        L_0x04f8:
            r6 = r1
        L_0x04f9:
            r2 = r18
            goto L_0x0502
        L_0x04fc:
            r0 = move-exception
            r1 = r76
            r3 = r36
        L_0x0501:
            r6 = r1
        L_0x0502:
            r11 = r51
            goto L_0x0542
        L_0x0505:
            r1 = r76
            r3 = r36
            r10 = r78
            r9 = r50
            r6 = 0
            r43 = 0
            r69 = r3
            r3 = r2
            r2 = r69
            goto L_0x059f
        L_0x0517:
            r0 = move-exception
            r1 = r76
            r3 = r36
            r6 = r1
            r11 = r51
            r2 = -5
            goto L_0x0542
        L_0x0521:
            r0 = move-exception
            r1 = r76
            r3 = r36
            r6 = r1
            r11 = r51
            goto L_0x0533
        L_0x052a:
            r0 = move-exception
            r3 = r2
            r50 = r9
            r51 = r11
            r6 = r1
            r36 = r3
        L_0x0533:
            r2 = -5
            r30 = 0
            goto L_0x0542
        L_0x0537:
            r0 = move-exception
            r50 = r9
            r51 = r11
            r6 = r1
            r2 = -5
            r30 = 0
            r36 = 0
        L_0x0542:
            r1 = r0
            goto L_0x0556
        L_0x0544:
            r0 = move-exception
            r50 = r9
            r51 = r11
            goto L_0x054f
        L_0x054a:
            r0 = move-exception
            r50 = r9
            r11 = r77
        L_0x054f:
            r1 = r0
        L_0x0550:
            r2 = -5
            r6 = 0
            r30 = 0
            r36 = 0
        L_0x0556:
            boolean r3 = r1 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x05f7, all -> 0x05f2 }
            if (r3 == 0) goto L_0x055f
            if (r90 != 0) goto L_0x055f
            r43 = 1
            goto L_0x0561
        L_0x055f:
            r43 = 0
        L_0x0561:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05e2, all -> 0x05f2 }
            r3.<init>()     // Catch:{ Exception -> 0x05e2, all -> 0x05f2 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ Exception -> 0x05e2, all -> 0x05f2 }
            r9 = r50
            r3.append(r9)     // Catch:{ Exception -> 0x05e0, all -> 0x05f2 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ Exception -> 0x05e0, all -> 0x05f2 }
            r10 = r78
            r3.append(r10)     // Catch:{ Exception -> 0x05d7, all -> 0x05f2 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ Exception -> 0x05d7, all -> 0x05f2 }
            r3.append(r11)     // Catch:{ Exception -> 0x05d7, all -> 0x05f2 }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ Exception -> 0x05d7, all -> 0x05f2 }
            r3.append(r12)     // Catch:{ Exception -> 0x05d7, all -> 0x05f2 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x05d7, all -> 0x05f2 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ Exception -> 0x05d7, all -> 0x05f2 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x05d7, all -> 0x05f2 }
            r3 = r2
            r1 = r6
            r51 = r11
            r2 = r36
            r6 = r43
            r43 = 1
        L_0x059f:
            if (r30 == 0) goto L_0x05b8
            r30.release()     // Catch:{ Exception -> 0x05aa, all -> 0x05a5 }
            goto L_0x05b8
        L_0x05a5:
            r0 = move-exception
            r2 = r3
            r1 = r15
            goto L_0x13ae
        L_0x05aa:
            r0 = move-exception
            r54 = r83
            r35 = r85
            r1 = r0
            r2 = r3
            r3 = r9
            r4 = r10
            r11 = r12
            r8 = r51
            goto L_0x0605
        L_0x05b8:
            if (r2 == 0) goto L_0x05bd
            r2.release()     // Catch:{ Exception -> 0x05aa, all -> 0x05a5 }
        L_0x05bd:
            if (r1 == 0) goto L_0x05c5
            r1.stop()     // Catch:{ Exception -> 0x05aa, all -> 0x05a5 }
            r1.release()     // Catch:{ Exception -> 0x05aa, all -> 0x05a5 }
        L_0x05c5:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x05aa, all -> 0x05a5 }
            r54 = r83
            r35 = r85
            r2 = r3
            r18 = r9
            r4 = r10
            r11 = r12
            r1 = r43
            r9 = r81
            goto L_0x12f0
        L_0x05d7:
            r0 = move-exception
            r54 = r83
            r35 = r85
            r1 = r0
            r3 = r9
            r4 = r10
            goto L_0x05ed
        L_0x05e0:
            r0 = move-exception
            goto L_0x05e5
        L_0x05e2:
            r0 = move-exception
            r9 = r50
        L_0x05e5:
            r4 = r78
            r54 = r83
            r35 = r85
            r1 = r0
            r3 = r9
        L_0x05ed:
            r8 = r11
            r11 = r12
            r6 = r43
            goto L_0x0605
        L_0x05f2:
            r0 = move-exception
        L_0x05f3:
            r3 = r0
        L_0x05f4:
            r1 = r15
            goto L_0x13af
        L_0x05f7:
            r0 = move-exception
            r9 = r50
            r4 = r78
            r54 = r83
            r35 = r85
            r1 = r0
            r3 = r9
            r8 = r11
            r11 = r12
        L_0x0604:
            r6 = 0
        L_0x0605:
            r9 = r81
            goto L_0x132d
        L_0x0609:
            r8 = r1
            r11 = r21
            r14 = r23
            r7 = r24
            r13 = r29
            r1 = 921600(0xe1000, float:1.291437E-39)
            android.media.MediaExtractor r2 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x1312, all -> 0x1317 }
            r2.<init>()     // Catch:{ Exception -> 0x1312, all -> 0x1317 }
            r15.extractor = r2     // Catch:{ Exception -> 0x1312, all -> 0x1317 }
            r5 = r72
            r2.setDataSource(r5)     // Catch:{ Exception -> 0x1312, all -> 0x1317 }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x1312, all -> 0x1317 }
            r6 = 0
            int r4 = org.telegram.messenger.MediaController.findTrack(r2, r6)     // Catch:{ Exception -> 0x1312, all -> 0x1317 }
            r2 = -1
            if (r9 == r2) goto L_0x0642
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0635, all -> 0x1317 }
            r3 = 1
            int r2 = org.telegram.messenger.MediaController.findTrack(r2, r3)     // Catch:{ Exception -> 0x0635, all -> 0x1317 }
            r44 = r11
            goto L_0x0646
        L_0x0635:
            r0 = move-exception
            r8 = r77
            r54 = r83
            r35 = r85
            r1 = r0
            r3 = r9
            r4 = r10
            r11 = r12
            r2 = -5
            goto L_0x0605
        L_0x0642:
            r3 = 1
            r44 = r11
            r2 = -1
        L_0x0646:
            java.lang.String r11 = "mime"
            if (r4 < 0) goto L_0x065c
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x0635, all -> 0x1317 }
            android.media.MediaFormat r3 = r3.getTrackFormat(r4)     // Catch:{ Exception -> 0x0635, all -> 0x1317 }
            java.lang.String r3 = r3.getString(r11)     // Catch:{ Exception -> 0x0635, all -> 0x1317 }
            boolean r3 = r3.equals(r13)     // Catch:{ Exception -> 0x0635, all -> 0x1317 }
            if (r3 != 0) goto L_0x065c
            r3 = 1
            goto L_0x065d
        L_0x065c:
            r3 = 0
        L_0x065d:
            if (r89 != 0) goto L_0x06b4
            if (r3 == 0) goto L_0x0663
            goto L_0x06b4
        L_0x0663:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x06a4, all -> 0x1317 }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ Exception -> 0x06a4, all -> 0x1317 }
            r1 = -1
            if (r9 == r1) goto L_0x066c
            r13 = 1
            goto L_0x066d
        L_0x066c:
            r13 = 0
        L_0x066d:
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
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ Exception -> 0x0693, all -> 0x1317 }
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
            goto L_0x12f0
        L_0x0693:
            r0 = move-exception
            r11 = r76
            r8 = r77
            r3 = r79
            r9 = r81
            r54 = r83
            r35 = r85
            r1 = r0
            r4 = r14
            goto L_0x132b
        L_0x06a4:
            r0 = move-exception
            r11 = r76
            r8 = r77
            r3 = r79
            r54 = r83
            r35 = r85
            r1 = r0
            r4 = r10
            r2 = -5
            goto L_0x0604
        L_0x06b4:
            r12 = r5
            r69 = r14
            r14 = r10
            r10 = r69
            if (r4 < 0) goto L_0x12b4
            r16 = -2147483648(0xfffffffvar_, double:NaN)
            r3 = 1000(0x3e8, float:1.401E-42)
            r21 = -1
            int r3 = r3 / r14
            int r3 = r3 * 1000
            long r5 = (long) r3     // Catch:{ Exception -> 0x121d, all -> 0x1317 }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x121d, all -> 0x1317 }
            r3.selectTrack(r4)     // Catch:{ Exception -> 0x121d, all -> 0x1317 }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x121d, all -> 0x1317 }
            android.media.MediaFormat r9 = r3.getTrackFormat(r4)     // Catch:{ Exception -> 0x121d, all -> 0x1317 }
            r23 = 0
            int r3 = (r85 > r23 ? 1 : (r85 == r23 ? 0 : -1))
            if (r3 < 0) goto L_0x06f5
            r3 = 1157234688(0x44fa0000, float:2000.0)
            int r3 = (r20 > r3 ? 1 : (r20 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x06e2
            r3 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x06f0
        L_0x06e2:
            r3 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r3 = (r20 > r3 ? 1 : (r20 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x06ed
            r3 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x06f0
        L_0x06ed:
            r3 = 1560000(0x17cdc0, float:2.186026E-39)
        L_0x06f0:
            r1 = r80
            r23 = 0
            goto L_0x0705
        L_0x06f5:
            if (r79 > 0) goto L_0x06ff
            r1 = r80
            r23 = r85
            r3 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x0705
        L_0x06ff:
            r3 = r79
            r1 = r80
            r23 = r85
        L_0x0705:
            if (r1 <= 0) goto L_0x071a
            int r3 = java.lang.Math.min(r1, r3)     // Catch:{ Exception -> 0x070c, all -> 0x1317 }
            goto L_0x071a
        L_0x070c:
            r0 = move-exception
            r9 = r81
            r54 = r83
            r5 = r0
            r62 = r4
            r4 = r14
            r35 = r23
        L_0x0717:
            r1 = 0
            goto L_0x122b
        L_0x071a:
            r25 = 0
            int r27 = (r23 > r25 ? 1 : (r23 == r25 ? 0 : -1))
            r45 = r7
            r35 = r8
            if (r27 < 0) goto L_0x0727
            r7 = r21
            goto L_0x0729
        L_0x0727:
            r7 = r23
        L_0x0729:
            int r23 = (r7 > r25 ? 1 : (r7 == r25 ? 0 : -1))
            if (r23 < 0) goto L_0x0748
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x073c, all -> 0x1317 }
            r23 = r2
            r2 = 0
            r1.seekTo(r7, r2)     // Catch:{ Exception -> 0x073c, all -> 0x1317 }
            r24 = r4
            r25 = r5
            r5 = 0
            goto L_0x0778
        L_0x073c:
            r0 = move-exception
            r9 = r81
            r54 = r83
            r5 = r0
            r62 = r4
            r35 = r7
            r4 = r14
            goto L_0x0717
        L_0x0748:
            r23 = r2
            r24 = 0
            r1 = r81
            int r26 = (r1 > r24 ? 1 : (r1 == r24 ? 0 : -1))
            if (r26 <= 0) goto L_0x076c
            r24 = r4
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x0761, all -> 0x1317 }
            r25 = r5
            r5 = 0
            r4.seekTo(r1, r5)     // Catch:{ Exception -> 0x0761, all -> 0x1317 }
            r4 = r95
            r5 = 0
            goto L_0x077a
        L_0x0761:
            r0 = move-exception
            r54 = r83
            r5 = r0
            r9 = r1
        L_0x0766:
            r35 = r7
            r4 = r14
            r62 = r24
            goto L_0x0717
        L_0x076c:
            r24 = r4
            r25 = r5
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x120f, all -> 0x1317 }
            r1 = 0
            r5 = 0
            r4.seekTo(r5, r1)     // Catch:{ Exception -> 0x1206, all -> 0x1317 }
        L_0x0778:
            r4 = r95
        L_0x077a:
            if (r4 == 0) goto L_0x079a
            r1 = 90
            r2 = r74
            if (r2 == r1) goto L_0x078c
            r1 = 270(0x10e, float:3.78E-43)
            if (r2 != r1) goto L_0x0787
            goto L_0x078c
        L_0x0787:
            int r1 = r4.transformWidth     // Catch:{ Exception -> 0x0793, all -> 0x1317 }
            int r5 = r4.transformHeight     // Catch:{ Exception -> 0x0793, all -> 0x1317 }
            goto L_0x0790
        L_0x078c:
            int r1 = r4.transformHeight     // Catch:{ Exception -> 0x0793, all -> 0x1317 }
            int r5 = r4.transformWidth     // Catch:{ Exception -> 0x0793, all -> 0x1317 }
        L_0x0790:
            r6 = r5
            r5 = r1
            goto L_0x07a0
        L_0x0793:
            r0 = move-exception
            r9 = r81
            r54 = r83
            r5 = r0
            goto L_0x0766
        L_0x079a:
            r2 = r74
            r5 = r76
            r6 = r77
        L_0x07a0:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1202, all -> 0x1317 }
            if (r1 == 0) goto L_0x07c0
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0793, all -> 0x1317 }
            r1.<init>()     // Catch:{ Exception -> 0x0793, all -> 0x1317 }
            java.lang.String r2 = "create encoder with w = "
            r1.append(r2)     // Catch:{ Exception -> 0x0793, all -> 0x1317 }
            r1.append(r5)     // Catch:{ Exception -> 0x0793, all -> 0x1317 }
            java.lang.String r2 = " h = "
            r1.append(r2)     // Catch:{ Exception -> 0x0793, all -> 0x1317 }
            r1.append(r6)     // Catch:{ Exception -> 0x0793, all -> 0x1317 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0793, all -> 0x1317 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0793, all -> 0x1317 }
        L_0x07c0:
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r13, r5, r6)     // Catch:{ Exception -> 0x1202, all -> 0x1317 }
            java.lang.String r2 = "color-format"
            r4 = 2130708361(0x7var_, float:1.701803E38)
            r1.setInteger(r2, r4)     // Catch:{ Exception -> 0x1202, all -> 0x1317 }
            java.lang.String r2 = "bitrate"
            r1.setInteger(r2, r3)     // Catch:{ Exception -> 0x1202, all -> 0x1317 }
            java.lang.String r2 = "frame-rate"
            r1.setInteger(r2, r14)     // Catch:{ Exception -> 0x1202, all -> 0x1317 }
            java.lang.String r2 = "i-frame-interval"
            r4 = 2
            r1.setInteger(r2, r4)     // Catch:{ Exception -> 0x1202, all -> 0x1317 }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1202, all -> 0x1317 }
            r4 = 23
            if (r2 >= r4) goto L_0x0802
            int r2 = java.lang.Math.min(r6, r5)     // Catch:{ Exception -> 0x0793, all -> 0x1317 }
            r4 = 480(0x1e0, float:6.73E-43)
            if (r2 > r4) goto L_0x0802
            r2 = 921600(0xe1000, float:1.291437E-39)
            if (r3 <= r2) goto L_0x07f0
            goto L_0x07f1
        L_0x07f0:
            r2 = r3
        L_0x07f1:
            java.lang.String r3 = "bitrate"
            r1.setInteger(r3, r2)     // Catch:{ Exception -> 0x07f9, all -> 0x1317 }
            r18 = r2
            goto L_0x0804
        L_0x07f9:
            r0 = move-exception
            r9 = r81
            r54 = r83
            r5 = r0
            r3 = r2
            goto L_0x0766
        L_0x0802:
            r18 = r3
        L_0x0804:
            android.media.MediaCodec r4 = android.media.MediaCodec.createEncoderByType(r13)     // Catch:{ Exception -> 0x11f1, all -> 0x1317 }
            r2 = 1
            r3 = 0
            r4.configure(r1, r3, r3, r2)     // Catch:{ Exception -> 0x11dd, all -> 0x1317 }
            org.telegram.messenger.video.InputSurface r1 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x11dd, all -> 0x1317 }
            android.view.Surface r2 = r4.createInputSurface()     // Catch:{ Exception -> 0x11dd, all -> 0x1317 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x11dd, all -> 0x1317 }
            r1.makeCurrent()     // Catch:{ Exception -> 0x11c0, all -> 0x1317 }
            r4.start()     // Catch:{ Exception -> 0x11c0, all -> 0x1317 }
            java.lang.String r2 = r9.getString(r11)     // Catch:{ Exception -> 0x11c0, all -> 0x1317 }
            android.media.MediaCodec r2 = android.media.MediaCodec.createDecoderByType(r2)     // Catch:{ Exception -> 0x11c0, all -> 0x1317 }
            org.telegram.messenger.video.OutputSurface r27 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x11a8, all -> 0x1317 }
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
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x1199, all -> 0x1317 }
            android.view.Surface r1 = r27.getSurface()     // Catch:{ Exception -> 0x1183, all -> 0x1317 }
            r3 = r61
            r2 = r67
            r4 = 0
            r5 = 0
            r3.configure(r2, r1, r4, r5)     // Catch:{ Exception -> 0x1178, all -> 0x1317 }
            r3.start()     // Catch:{ Exception -> 0x1178, all -> 0x1317 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1178, all -> 0x1317 }
            r2 = 21
            if (r1 >= r2) goto L_0x08a6
            java.nio.ByteBuffer[] r6 = r3.getInputBuffers()     // Catch:{ Exception -> 0x0891, all -> 0x1317 }
            java.nio.ByteBuffer[] r1 = r79.getOutputBuffers()     // Catch:{ Exception -> 0x0891, all -> 0x1317 }
            r2 = r60
            r69 = r6
            r6 = r1
            r1 = r69
            goto L_0x08aa
        L_0x0891:
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
            goto L_0x1234
        L_0x08a6:
            r1 = r4
            r6 = r1
            r2 = r60
        L_0x08aa:
            if (r2 < 0) goto L_0x09a2
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x098b, all -> 0x1317 }
            android.media.MediaFormat r5 = r5.getTrackFormat(r2)     // Catch:{ Exception -> 0x098b, all -> 0x1317 }
            java.lang.String r7 = r5.getString(r13)     // Catch:{ Exception -> 0x098b, all -> 0x1317 }
            java.lang.String r8 = "audio/mp4a-latm"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x098b, all -> 0x1317 }
            if (r7 != 0) goto L_0x08cd
            java.lang.String r7 = r5.getString(r13)     // Catch:{ Exception -> 0x0891, all -> 0x1317 }
            java.lang.String r8 = "audio/mpeg"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x0891, all -> 0x1317 }
            if (r7 == 0) goto L_0x08cb
            goto L_0x08cd
        L_0x08cb:
            r7 = 0
            goto L_0x08ce
        L_0x08cd:
            r7 = 1
        L_0x08ce:
            java.lang.String r8 = r5.getString(r13)     // Catch:{ Exception -> 0x098b, all -> 0x1317 }
            java.lang.String r9 = "audio/unknown"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x098b, all -> 0x1317 }
            if (r8 == 0) goto L_0x08db
            r2 = -1
        L_0x08db:
            if (r2 < 0) goto L_0x0982
            if (r7 == 0) goto L_0x0921
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x0914, all -> 0x1317 }
            r9 = 1
            int r8 = r8.addTrack(r5, r9)     // Catch:{ Exception -> 0x0914, all -> 0x1317 }
            android.media.MediaExtractor r10 = r15.extractor     // Catch:{ Exception -> 0x0914, all -> 0x1317 }
            r10.selectTrack(r2)     // Catch:{ Exception -> 0x0914, all -> 0x1317 }
            java.lang.String r10 = "max-input-size"
            int r5 = r5.getInteger(r10)     // Catch:{ Exception -> 0x0914, all -> 0x1317 }
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocateDirect(r5)     // Catch:{ Exception -> 0x0914, all -> 0x1317 }
            r10 = r81
            r85 = r5
            r4 = 0
            int r13 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r13 <= 0) goto L_0x0906
            android.media.MediaExtractor r13 = r15.extractor     // Catch:{ Exception -> 0x093a, all -> 0x1317 }
            r9 = 0
            r13.seekTo(r10, r9)     // Catch:{ Exception -> 0x093a, all -> 0x1317 }
            goto L_0x090c
        L_0x0906:
            android.media.MediaExtractor r9 = r15.extractor     // Catch:{ Exception -> 0x093a, all -> 0x1317 }
            r13 = 0
            r9.seekTo(r4, r13)     // Catch:{ Exception -> 0x093a, all -> 0x1317 }
        L_0x090c:
            r5 = r85
            r4 = r8
            r13 = 0
            r8 = r83
            goto L_0x09aa
        L_0x0914:
            r0 = move-exception
            r4 = r78
            r6 = r79
            r9 = r81
            r54 = r83
            r5 = r0
            r14 = r3
            goto L_0x1192
        L_0x0921:
            r10 = r81
            r8 = r5
            r4 = 0
            android.media.MediaExtractor r9 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0974, all -> 0x1317 }
            r9.<init>()     // Catch:{ Exception -> 0x0974, all -> 0x1317 }
            r9.setDataSource(r12)     // Catch:{ Exception -> 0x0974, all -> 0x1317 }
            r9.selectTrack(r2)     // Catch:{ Exception -> 0x0974, all -> 0x1317 }
            int r13 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r13 <= 0) goto L_0x0944
            r13 = 0
            r9.seekTo(r10, r13)     // Catch:{ Exception -> 0x093a, all -> 0x1317 }
            goto L_0x0948
        L_0x093a:
            r0 = move-exception
            r4 = r78
            r6 = r79
            r54 = r83
            r5 = r0
            r14 = r3
            goto L_0x097f
        L_0x0944:
            r13 = 0
            r9.seekTo(r4, r13)     // Catch:{ Exception -> 0x0974, all -> 0x1317 }
        L_0x0948:
            org.telegram.messenger.video.AudioRecoder r13 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x0974, all -> 0x1317 }
            r13.<init>(r8, r9, r2)     // Catch:{ Exception -> 0x0974, all -> 0x1317 }
            r13.startTime = r10     // Catch:{ Exception -> 0x0964, all -> 0x1317 }
            r8 = r83
            r13.endTime = r8     // Catch:{ Exception -> 0x0962, all -> 0x1317 }
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x0962, all -> 0x1317 }
            android.media.MediaFormat r5 = r13.format     // Catch:{ Exception -> 0x0962, all -> 0x1317 }
            r85 = r2
            r2 = 1
            int r4 = r4.addTrack(r5, r2)     // Catch:{ Exception -> 0x0962, all -> 0x1317 }
            r2 = r85
            r5 = 0
            goto L_0x09aa
        L_0x0962:
            r0 = move-exception
            goto L_0x0967
        L_0x0964:
            r0 = move-exception
            r8 = r83
        L_0x0967:
            r4 = r78
            r6 = r79
            r5 = r0
            r14 = r3
            r54 = r8
            r9 = r10
            r40 = r13
            goto L_0x1170
        L_0x0974:
            r0 = move-exception
            r8 = r83
            r4 = r78
            r6 = r79
            r5 = r0
            r14 = r3
            r54 = r8
        L_0x097f:
            r9 = r10
            goto L_0x1192
        L_0x0982:
            r10 = r81
            r8 = r83
            r85 = r2
            r4 = -5
            r5 = 0
            goto L_0x09a9
        L_0x098b:
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
            goto L_0x1234
        L_0x09a2:
            r10 = r81
            r8 = r83
            r4 = -5
            r5 = 0
            r7 = 1
        L_0x09a9:
            r13 = 0
        L_0x09aa:
            if (r2 >= 0) goto L_0x09af
            r23 = 1
            goto L_0x09b1
        L_0x09af:
            r23 = 0
        L_0x09b1:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x115f, all -> 0x1317 }
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
        L_0x09cd:
            if (r23 == 0) goto L_0x09e6
            if (r7 != 0) goto L_0x09d4
            if (r37 != 0) goto L_0x09d4
            goto L_0x09e6
        L_0x09d4:
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
            goto L_0x1277
        L_0x09e6:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x1145, all -> 0x1291 }
            if (r7 != 0) goto L_0x0a08
            if (r13 == 0) goto L_0x0a08
            org.telegram.messenger.video.MP4Builder r12 = r15.mediaMuxer     // Catch:{ Exception -> 0x09f6, all -> 0x1291 }
            boolean r12 = r13.step(r12, r4)     // Catch:{ Exception -> 0x09f6, all -> 0x1291 }
            r37 = r12
            goto L_0x0a08
        L_0x09f6:
            r0 = move-exception
            r4 = r78
            r6 = r79
            r5 = r0
            r14 = r3
            r54 = r8
            r9 = r10
            r40 = r13
            r2 = r17
        L_0x0a04:
            r3 = r18
            goto L_0x115c
        L_0x0a08:
            if (r6 != 0) goto L_0x0b9b
            android.media.MediaExtractor r12 = r15.extractor     // Catch:{ Exception -> 0x0b8b, all -> 0x1291 }
            int r12 = r12.getSampleTrackIndex()     // Catch:{ Exception -> 0x0b8b, all -> 0x1291 }
            r83 = r6
            r6 = r62
            if (r12 != r6) goto L_0x0a96
            r85 = r13
            r48 = r14
            r13 = 2500(0x9c4, double:1.235E-320)
            int r12 = r3.dequeueInputBuffer(r13)     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            if (r12 < 0) goto L_0x0a73
            int r13 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            r14 = 21
            if (r13 >= r14) goto L_0x0a2b
            r13 = r1[r12]     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            goto L_0x0a2f
        L_0x0a2b:
            java.nio.ByteBuffer r13 = r3.getInputBuffer(r12)     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
        L_0x0a2f:
            android.media.MediaExtractor r14 = r15.extractor     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            r86 = r1
            r1 = 0
            int r55 = r14.readSampleData(r13, r1)     // Catch:{ Exception -> 0x0a62, all -> 0x1291 }
            if (r55 >= 0) goto L_0x0a4b
            r54 = 0
            r55 = 0
            r56 = 0
            r58 = 4
            r52 = r3
            r53 = r12
            r52.queueInputBuffer(r53, r54, r55, r56, r58)     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            r1 = 1
            goto L_0x0a77
        L_0x0a4b:
            r54 = 0
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            long r56 = r1.getSampleTime()     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            r58 = 0
            r52 = r3
            r53 = r12
            r52.queueInputBuffer(r53, r54, r55, r56, r58)     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            r1.advance()     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            goto L_0x0a75
        L_0x0a62:
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
            goto L_0x0a92
        L_0x0a73:
            r86 = r1
        L_0x0a75:
            r1 = r83
        L_0x0a77:
            r14 = r2
            r50 = r7
            r60 = r8
            r13 = r68
            r2 = r1
            goto L_0x0b5d
        L_0x0a81:
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
        L_0x0a91:
            r1 = 0
        L_0x0a92:
            r6 = r79
            goto L_0x1234
        L_0x0a96:
            r86 = r1
            r85 = r13
            r48 = r14
            if (r7 == 0) goto L_0x0b4d
            r1 = -1
            if (r2 == r1) goto L_0x0b45
            if (r12 != r2) goto L_0x0b4d
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0b41, all -> 0x1291 }
            r12 = 0
            int r1 = r1.readSampleData(r5, r12)     // Catch:{ Exception -> 0x0b41, all -> 0x1291 }
            r13 = r68
            r13.size = r1     // Catch:{ Exception -> 0x0b41, all -> 0x1291 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b41, all -> 0x1291 }
            r14 = 21
            if (r1 >= r14) goto L_0x0abc
            r5.position(r12)     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            int r1 = r13.size     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            r5.limit(r1)     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
        L_0x0abc:
            int r1 = r13.size     // Catch:{ Exception -> 0x0b41, all -> 0x1291 }
            if (r1 < 0) goto L_0x0ad1
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            r14 = r2
            long r1 = r1.getSampleTime()     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            r13.presentationTimeUs = r1     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            r1.advance()     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            r1 = r83
            goto L_0x0ad6
        L_0x0ad1:
            r14 = r2
            r1 = 0
            r13.size = r1     // Catch:{ Exception -> 0x0b2d, all -> 0x1291 }
            r1 = 1
        L_0x0ad6:
            int r2 = r13.size     // Catch:{ Exception -> 0x0b41, all -> 0x1291 }
            if (r2 <= 0) goto L_0x0b26
            r41 = 0
            int r2 = (r8 > r41 ? 1 : (r8 == r41 ? 0 : -1))
            if (r2 < 0) goto L_0x0ae9
            r83 = r1
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0a81, all -> 0x1291 }
            int r12 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r12 >= 0) goto L_0x0b28
            goto L_0x0aeb
        L_0x0ae9:
            r83 = r1
        L_0x0aeb:
            r1 = 0
            r13.offset = r1     // Catch:{ Exception -> 0x0b2d, all -> 0x1291 }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0b2d, all -> 0x1291 }
            int r2 = r2.getSampleFlags()     // Catch:{ Exception -> 0x0b2d, all -> 0x1291 }
            r13.flags = r2     // Catch:{ Exception -> 0x0b2d, all -> 0x1291 }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0b2d, all -> 0x1291 }
            r50 = r7
            r60 = r8
            long r7 = r2.writeSampleData(r4, r5, r13, r1)     // Catch:{ Exception -> 0x0b24, all -> 0x1291 }
            r1 = 0
            int r9 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
            if (r9 == 0) goto L_0x0b5b
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r15.callback     // Catch:{ Exception -> 0x0b77, all -> 0x1291 }
            if (r1 == 0) goto L_0x0b5b
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0b77, all -> 0x1291 }
            long r1 = r1 - r10
            int r9 = (r1 > r31 ? 1 : (r1 == r31 ? 0 : -1))
            if (r9 <= 0) goto L_0x0b15
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0b77, all -> 0x1291 }
            long r31 = r1 - r10
        L_0x0b15:
            r1 = r31
            org.telegram.messenger.MediaController$VideoConvertorListener r9 = r15.callback     // Catch:{ Exception -> 0x0b77, all -> 0x1291 }
            float r12 = (float) r1     // Catch:{ Exception -> 0x0b77, all -> 0x1291 }
            float r12 = r12 / r19
            float r12 = r12 / r20
            r9.didWriteData(r7, r12)     // Catch:{ Exception -> 0x0b77, all -> 0x1291 }
            r31 = r1
            goto L_0x0b5b
        L_0x0b24:
            r0 = move-exception
            goto L_0x0b30
        L_0x0b26:
            r83 = r1
        L_0x0b28:
            r50 = r7
            r60 = r8
            goto L_0x0b5b
        L_0x0b2d:
            r0 = move-exception
            r60 = r8
        L_0x0b30:
            r4 = r78
            r40 = r85
            r5 = r0
            r14 = r3
            r62 = r6
            r9 = r10
            r2 = r17
            r3 = r18
            r54 = r60
            goto L_0x0a92
        L_0x0b41:
            r0 = move-exception
            r60 = r8
            goto L_0x0b78
        L_0x0b45:
            r14 = r2
            r50 = r7
            r60 = r8
            r13 = r68
            goto L_0x0b55
        L_0x0b4d:
            r14 = r2
            r50 = r7
            r60 = r8
            r13 = r68
            r1 = -1
        L_0x0b55:
            if (r12 != r1) goto L_0x0b5b
            r2 = r83
            r1 = 1
            goto L_0x0b5e
        L_0x0b5b:
            r2 = r83
        L_0x0b5d:
            r1 = 0
        L_0x0b5e:
            if (r1 == 0) goto L_0x0b89
            r7 = 2500(0x9c4, double:1.235E-320)
            int r53 = r3.dequeueInputBuffer(r7)     // Catch:{ Exception -> 0x0b77, all -> 0x1291 }
            if (r53 < 0) goto L_0x0b89
            r54 = 0
            r55 = 0
            r56 = 0
            r58 = 4
            r52 = r3
            r52.queueInputBuffer(r53, r54, r55, r56, r58)     // Catch:{ Exception -> 0x0b77, all -> 0x1291 }
            r1 = 1
            goto L_0x0bae
        L_0x0b77:
            r0 = move-exception
        L_0x0b78:
            r4 = r78
            r40 = r85
            r5 = r0
            r14 = r3
            r62 = r6
            r9 = r10
            r2 = r17
            r3 = r18
            r54 = r60
            goto L_0x0a91
        L_0x0b89:
            r1 = r2
            goto L_0x0bae
        L_0x0b8b:
            r0 = move-exception
            r60 = r8
            r85 = r13
            r4 = r78
            r6 = r79
            r40 = r85
            r5 = r0
            r14 = r3
            r9 = r10
            goto L_0x1156
        L_0x0b9b:
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
        L_0x0bae:
            r2 = r28 ^ 1
            r7 = r1
            r12 = r17
            r8 = r60
            r1 = 1
            r69 = r44
            r44 = r4
            r45 = r5
            r4 = r69
        L_0x0bbe:
            if (r2 != 0) goto L_0x0bdf
            if (r1 == 0) goto L_0x0bc3
            goto L_0x0bdf
        L_0x0bc3:
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
            goto L_0x09cd
        L_0x0bdf:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x1131, all -> 0x112c }
            if (r90 == 0) goto L_0x0bed
            r52 = 22000(0x55f0, double:1.08694E-319)
            r83 = r1
            r84 = r2
            r1 = r52
            goto L_0x0bf3
        L_0x0bed:
            r83 = r1
            r84 = r2
            r1 = 2500(0x9c4, double:1.235E-320)
        L_0x0bf3:
            r69 = r7
            r7 = r79
            r79 = r69
            int r1 = r7.dequeueOutputBuffer(r13, r1)     // Catch:{ Exception -> 0x1128, all -> 0x112c }
            r2 = -1
            if (r1 != r2) goto L_0x0CLASSNAME
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
        L_0x0CLASSNAME:
            r9 = -1
            goto L_0x0e49
        L_0x0CLASSNAME:
            r2 = -3
            if (r1 != r2) goto L_0x0c4b
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c3c, all -> 0x112c }
            r51 = r14
            r14 = 21
            if (r2 >= r14) goto L_0x0CLASSNAME
            java.nio.ByteBuffer[] r29 = r7.getOutputBuffers()     // Catch:{ Exception -> 0x0c3c, all -> 0x112c }
        L_0x0CLASSNAME:
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
            goto L_0x0CLASSNAME
        L_0x0c3c:
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
            goto L_0x1142
        L_0x0c4b:
            r51 = r14
            r2 = -2
            if (r1 != r2) goto L_0x0cb6
            android.media.MediaFormat r2 = r7.getOutputFormat()     // Catch:{ Exception -> 0x0cac, all -> 0x112c }
            r14 = -5
            if (r12 != r14) goto L_0x0CLASSNAME
            if (r2 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.video.MP4Builder r14 = r15.mediaMuxer     // Catch:{ Exception -> 0x0cac, all -> 0x112c }
            r62 = r6
            r6 = 0
            int r12 = r14.addTrack(r2, r6)     // Catch:{ Exception -> 0x0cc9, all -> 0x112c }
            r6 = r66
            boolean r14 = r2.containsKey(r6)     // Catch:{ Exception -> 0x0cc9, all -> 0x112c }
            if (r14 == 0) goto L_0x0CLASSNAME
            int r14 = r2.getInteger(r6)     // Catch:{ Exception -> 0x0cc9, all -> 0x112c }
            r66 = r6
            r6 = 1
            if (r14 != r6) goto L_0x0c8d
            r6 = r65
            java.nio.ByteBuffer r14 = r2.getByteBuffer(r6)     // Catch:{ Exception -> 0x0cc9, all -> 0x112c }
            r52 = r4
            r4 = r48
            java.nio.ByteBuffer r2 = r2.getByteBuffer(r4)     // Catch:{ Exception -> 0x0cc9, all -> 0x112c }
            int r5 = r14.limit()     // Catch:{ Exception -> 0x0cc9, all -> 0x112c }
            int r2 = r2.limit()     // Catch:{ Exception -> 0x0cc9, all -> 0x112c }
            int r5 = r5 + r2
            r30 = r5
            goto L_0x0c9e
        L_0x0c8d:
            r52 = r4
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r52 = r4
            r66 = r6
        L_0x0CLASSNAME:
            r4 = r48
            r6 = r65
            goto L_0x0c9e
        L_0x0CLASSNAME:
            r52 = r4
            r62 = r6
            goto L_0x0CLASSNAME
        L_0x0c9e:
            r2 = r83
            r61 = r3
            r54 = r8
            r8 = r49
            r3 = r63
            r5 = r64
            goto L_0x0CLASSNAME
        L_0x0cac:
            r0 = move-exception
            r62 = r6
        L_0x0caf:
            r4 = r78
            r40 = r85
            r5 = r0
            r14 = r3
            goto L_0x0CLASSNAME
        L_0x0cb6:
            r52 = r4
            r62 = r6
            r4 = r48
            r6 = r65
            if (r1 < 0) goto L_0x1102
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x10fd, all -> 0x112c }
            r5 = 21
            if (r2 >= r5) goto L_0x0ccb
            r2 = r29[r1]     // Catch:{ Exception -> 0x0cc9, all -> 0x112c }
            goto L_0x0ccf
        L_0x0cc9:
            r0 = move-exception
            goto L_0x0caf
        L_0x0ccb:
            java.nio.ByteBuffer r2 = r7.getOutputBuffer(r1)     // Catch:{ Exception -> 0x10fd, all -> 0x112c }
        L_0x0ccf:
            if (r2 == 0) goto L_0x10d9
            int r14 = r13.size     // Catch:{ Exception -> 0x10fd, all -> 0x112c }
            r5 = 1
            if (r14 <= r5) goto L_0x0e2b
            int r14 = r13.flags     // Catch:{ Exception -> 0x0e18, all -> 0x112c }
            r14 = r14 & 2
            if (r14 != 0) goto L_0x0d9c
            if (r30 == 0) goto L_0x0cef
            int r14 = r13.flags     // Catch:{ Exception -> 0x0cc9, all -> 0x112c }
            r14 = r14 & r5
            if (r14 == 0) goto L_0x0cef
            int r5 = r13.offset     // Catch:{ Exception -> 0x0cc9, all -> 0x112c }
            int r5 = r5 + r30
            r13.offset = r5     // Catch:{ Exception -> 0x0cc9, all -> 0x112c }
            int r5 = r13.size     // Catch:{ Exception -> 0x0cc9, all -> 0x112c }
            int r5 = r5 - r30
            r13.size = r5     // Catch:{ Exception -> 0x0cc9, all -> 0x112c }
        L_0x0cef:
            if (r16 == 0) goto L_0x0d50
            int r5 = r13.flags     // Catch:{ Exception -> 0x0d44, all -> 0x112c }
            r14 = 1
            r5 = r5 & r14
            if (r5 == 0) goto L_0x0d50
            int r5 = r13.size     // Catch:{ Exception -> 0x0d44, all -> 0x112c }
            r14 = 100
            if (r5 <= r14) goto L_0x0d3f
            int r5 = r13.offset     // Catch:{ Exception -> 0x0d44, all -> 0x112c }
            r2.position(r5)     // Catch:{ Exception -> 0x0d44, all -> 0x112c }
            byte[] r5 = new byte[r14]     // Catch:{ Exception -> 0x0d44, all -> 0x112c }
            r2.get(r5)     // Catch:{ Exception -> 0x0d44, all -> 0x112c }
            r14 = 0
            r16 = 0
        L_0x0d0a:
            r54 = r8
            r8 = 96
            if (r14 >= r8) goto L_0x0d41
            byte r8 = r5[r14]     // Catch:{ Exception -> 0x0d3d, all -> 0x112c }
            if (r8 != 0) goto L_0x0d38
            int r8 = r14 + 1
            byte r8 = r5[r8]     // Catch:{ Exception -> 0x0d3d, all -> 0x112c }
            if (r8 != 0) goto L_0x0d38
            int r8 = r14 + 2
            byte r8 = r5[r8]     // Catch:{ Exception -> 0x0d3d, all -> 0x112c }
            if (r8 != 0) goto L_0x0d38
            int r8 = r14 + 3
            byte r8 = r5[r8]     // Catch:{ Exception -> 0x0d3d, all -> 0x112c }
            r9 = 1
            if (r8 != r9) goto L_0x0d38
            int r8 = r16 + 1
            if (r8 <= r9) goto L_0x0d36
            int r5 = r13.offset     // Catch:{ Exception -> 0x0d3d, all -> 0x112c }
            int r5 = r5 + r14
            r13.offset = r5     // Catch:{ Exception -> 0x0d3d, all -> 0x112c }
            int r5 = r13.size     // Catch:{ Exception -> 0x0d3d, all -> 0x112c }
            int r5 = r5 - r14
            r13.size = r5     // Catch:{ Exception -> 0x0d3d, all -> 0x112c }
            goto L_0x0d41
        L_0x0d36:
            r16 = r8
        L_0x0d38:
            int r14 = r14 + 1
            r8 = r54
            goto L_0x0d0a
        L_0x0d3d:
            r0 = move-exception
            goto L_0x0d47
        L_0x0d3f:
            r54 = r8
        L_0x0d41:
            r16 = 0
            goto L_0x0d52
        L_0x0d44:
            r0 = move-exception
            r54 = r8
        L_0x0d47:
            r4 = r78
            r40 = r85
            r5 = r0
            r14 = r3
        L_0x0d4d:
            r6 = r7
            goto L_0x0CLASSNAME
        L_0x0d50:
            r54 = r8
        L_0x0d52:
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x0d97, all -> 0x112c }
            r14 = r3
            r8 = 1
            long r2 = r5.writeSampleData(r12, r2, r13, r8)     // Catch:{ Exception -> 0x0d8e, all -> 0x112c }
            r8 = 0
            int r5 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r5 == 0) goto L_0x0d83
            org.telegram.messenger.MediaController$VideoConvertorListener r5 = r15.callback     // Catch:{ Exception -> 0x0d8e, all -> 0x112c }
            if (r5 == 0) goto L_0x0d83
            long r8 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0d8e, all -> 0x112c }
            long r8 = r8 - r10
            int r5 = (r8 > r31 ? 1 : (r8 == r31 ? 0 : -1))
            if (r5 <= 0) goto L_0x0d72
            long r8 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0d70, all -> 0x112c }
            long r31 = r8 - r10
            goto L_0x0d72
        L_0x0d70:
            r0 = move-exception
            goto L_0x0d91
        L_0x0d72:
            r8 = r31
            org.telegram.messenger.MediaController$VideoConvertorListener r5 = r15.callback     // Catch:{ Exception -> 0x0d8e, all -> 0x112c }
            r61 = r14
            float r14 = (float) r8
            float r14 = r14 / r19
            float r14 = r14 / r20
            r5.didWriteData(r2, r14)     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            r31 = r8
            goto L_0x0d85
        L_0x0d83:
            r61 = r14
        L_0x0d85:
            r2 = r12
            r8 = r49
            r3 = r63
            r5 = r64
            goto L_0x0e36
        L_0x0d8e:
            r0 = move-exception
            r61 = r14
        L_0x0d91:
            r4 = r78
            r40 = r85
            r5 = r0
            goto L_0x0d4d
        L_0x0d97:
            r0 = move-exception
            r61 = r3
            goto L_0x0e1d
        L_0x0d9c:
            r61 = r3
            r54 = r8
            r3 = -5
            if (r12 != r3) goto L_0x0e2f
            int r3 = r13.size     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            byte[] r3 = new byte[r3]     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            int r5 = r13.offset     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            int r8 = r13.size     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            int r5 = r5 + r8
            r2.limit(r5)     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            int r5 = r13.offset     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            r2.position(r5)     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            r2.get(r3)     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            int r2 = r13.size     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            r5 = 1
            int r2 = r2 - r5
        L_0x0dbb:
            if (r2 < 0) goto L_0x0df8
            r8 = 3
            if (r2 <= r8) goto L_0x0df8
            byte r9 = r3[r2]     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            if (r9 != r5) goto L_0x0df4
            int r9 = r2 + -1
            byte r9 = r3[r9]     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            if (r9 != 0) goto L_0x0df4
            int r9 = r2 + -2
            byte r9 = r3[r9]     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            if (r9 != 0) goto L_0x0df4
            int r9 = r2 + -3
            byte r14 = r3[r9]     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            if (r14 != 0) goto L_0x0df4
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r9)     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            int r14 = r13.size     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            int r14 = r14 - r9
            java.nio.ByteBuffer r14 = java.nio.ByteBuffer.allocate(r14)     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            r5 = 0
            java.nio.ByteBuffer r8 = r2.put(r3, r5, r9)     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            r8.position(r5)     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            int r8 = r13.size     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            int r8 = r8 - r9
            java.nio.ByteBuffer r3 = r14.put(r3, r9, r8)     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            r3.position(r5)     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            goto L_0x0dfa
        L_0x0df4:
            int r2 = r2 + -1
            r5 = 1
            goto L_0x0dbb
        L_0x0df8:
            r2 = 0
            r14 = 0
        L_0x0dfa:
            r8 = r49
            r3 = r63
            r5 = r64
            android.media.MediaFormat r9 = android.media.MediaFormat.createVideoFormat(r8, r3, r5)     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            if (r2 == 0) goto L_0x0e0e
            if (r14 == 0) goto L_0x0e0e
            r9.setByteBuffer(r6, r2)     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            r9.setByteBuffer(r4, r14)     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
        L_0x0e0e:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            r14 = 0
            int r2 = r2.addTrack(r9, r14)     // Catch:{ Exception -> 0x0e16, all -> 0x112c }
            goto L_0x0e36
        L_0x0e16:
            r0 = move-exception
            goto L_0x0e1d
        L_0x0e18:
            r0 = move-exception
            r61 = r3
            r54 = r8
        L_0x0e1d:
            r4 = r78
            r40 = r85
            r5 = r0
            r6 = r7
            r9 = r10
            r2 = r12
            r3 = r18
            r14 = r61
            goto L_0x115c
        L_0x0e2b:
            r61 = r3
            r54 = r8
        L_0x0e2f:
            r8 = r49
            r3 = r63
            r5 = r64
            r2 = r12
        L_0x0e36:
            int r9 = r13.flags     // Catch:{ Exception -> 0x10d0, all -> 0x05f2 }
            r9 = r9 & 4
            if (r9 == 0) goto L_0x0e3e
            r9 = 1
            goto L_0x0e3f
        L_0x0e3e:
            r9 = 0
        L_0x0e3f:
            r12 = 0
            r7.releaseOutputBuffer(r1, r12)     // Catch:{ Exception -> 0x10d0, all -> 0x05f2 }
            r12 = r2
            r23 = r9
            r9 = -1
            r2 = r83
        L_0x0e49:
            if (r1 == r9) goto L_0x0e6a
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
            goto L_0x0bbe
        L_0x0e6a:
            if (r28 != 0) goto L_0x109b
            r14 = r61
            r9 = 2500(0x9c4, double:1.235E-320)
            int r1 = r14.dequeueOutputBuffer(r13, r9)     // Catch:{ Exception -> 0x108a, all -> 0x1083 }
            r11 = -1
            if (r1 != r11) goto L_0x0e94
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
            goto L_0x10b8
        L_0x0e94:
            r9 = -3
            if (r1 != r9) goto L_0x0eab
        L_0x0e97:
            r9 = r81
            r17 = r2
            r63 = r3
            r57 = r4
            r64 = r5
            r65 = r6
            r83 = r12
            r11 = r52
            r3 = r59
            goto L_0x10ae
        L_0x0eab:
            r9 = -2
            if (r1 != r9) goto L_0x0ed2
            android.media.MediaFormat r1 = r14.getOutputFormat()     // Catch:{ Exception -> 0x0ecb, all -> 0x112c }
            boolean r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0ecb, all -> 0x112c }
            if (r9 == 0) goto L_0x0e97
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ecb, all -> 0x112c }
            r9.<init>()     // Catch:{ Exception -> 0x0ecb, all -> 0x112c }
            java.lang.String r10 = "newFormat = "
            r9.append(r10)     // Catch:{ Exception -> 0x0ecb, all -> 0x112c }
            r9.append(r1)     // Catch:{ Exception -> 0x0ecb, all -> 0x112c }
            java.lang.String r1 = r9.toString()     // Catch:{ Exception -> 0x0ecb, all -> 0x112c }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0ecb, all -> 0x112c }
            goto L_0x0e97
        L_0x0ecb:
            r0 = move-exception
            r4 = r78
            r9 = r81
            goto L_0x113e
        L_0x0ed2:
            if (r1 < 0) goto L_0x1059
            int r9 = r13.size     // Catch:{ Exception -> 0x108a, all -> 0x1083 }
            r83 = r12
            if (r9 == 0) goto L_0x0edc
            r9 = 1
            goto L_0x0edd
        L_0x0edc:
            r9 = 0
        L_0x0edd:
            long r11 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1053, all -> 0x1078 }
            r41 = 0
            int r10 = (r54 > r41 ? 1 : (r54 == r41 ? 0 : -1))
            if (r10 <= 0) goto L_0x0ef6
            int r10 = (r11 > r54 ? 1 : (r11 == r54 ? 0 : -1))
            if (r10 < 0) goto L_0x0ef6
            int r9 = r13.flags     // Catch:{ Exception -> 0x0f7b, all -> 0x1078 }
            r9 = r9 | 4
            r13.flags = r9     // Catch:{ Exception -> 0x0f7b, all -> 0x1078 }
            r9 = 1
            r10 = 0
            r28 = 1
            r41 = 0
            goto L_0x0efb
        L_0x0ef6:
            r10 = r9
            r41 = 0
            r9 = r79
        L_0x0efb:
            int r17 = (r35 > r41 ? 1 : (r35 == r41 ? 0 : -1))
            if (r17 < 0) goto L_0x0var_
            r17 = r2
            int r2 = r13.flags     // Catch:{ Exception -> 0x0f7b, all -> 0x1078 }
            r2 = r2 & 4
            if (r2 == 0) goto L_0x0var_
            r79 = r9
            r2 = r10
            r38 = 2500(0x9c4, double:1.235E-320)
            r9 = r81
            long r48 = r35 - r9
            long r48 = java.lang.Math.abs(r48)     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            r56 = 1000000(0xvar_, float:1.401298E-39)
            r58 = r2
            r57 = r4
            r4 = r78
            int r2 = r56 / r4
            r63 = r3
            long r2 = (long) r2     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            int r56 = (r48 > r2 ? 1 : (r48 == r2 ? 0 : -1))
            if (r56 <= 0) goto L_0x0f6c
            r2 = 0
            int r28 = (r9 > r2 ? 1 : (r9 == r2 ? 0 : -1))
            if (r28 <= 0) goto L_0x0var_
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            r3 = 0
            r2.seekTo(r9, r3)     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            r64 = r5
            r65 = r6
            r3 = 0
            goto L_0x0var_
        L_0x0var_:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            r64 = r5
            r65 = r6
            r3 = 0
            r5 = 0
            r2.seekTo(r5, r3)     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
        L_0x0var_:
            long r33 = r52 + r24
            int r2 = r13.flags     // Catch:{ Exception -> 0x0f5c, all -> 0x1078 }
            r5 = -5
            r2 = r2 & r5
            r13.flags = r2     // Catch:{ Exception -> 0x0f5c, all -> 0x1078 }
            r14.flush()     // Catch:{ Exception -> 0x0f5c, all -> 0x1078 }
            r54 = r35
            r2 = 1
            r6 = 0
            r28 = 0
            r41 = 0
            r58 = 0
            r35 = r21
            goto L_0x0f9d
        L_0x0f5c:
            r0 = move-exception
            r2 = r83
            r40 = r85
            r5 = r0
            r6 = r7
            r3 = r18
            r54 = r35
            r1 = 0
            r35 = r21
            goto L_0x1234
        L_0x0f6c:
            r64 = r5
            r65 = r6
            r3 = 0
            r5 = -5
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            goto L_0x1093
        L_0x0var_:
            r0 = move-exception
            r4 = r78
            goto L_0x1093
        L_0x0f7b:
            r0 = move-exception
            r4 = r78
            r9 = r81
            goto L_0x1093
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
        L_0x0f9d:
            int r43 = (r35 > r41 ? 1 : (r35 == r41 ? 0 : -1))
            r79 = r6
            if (r43 < 0) goto L_0x0fa6
            r5 = r35
            goto L_0x0fa7
        L_0x0fa6:
            r5 = r9
        L_0x0fa7:
            int r43 = (r5 > r41 ? 1 : (r5 == r41 ? 0 : -1))
            if (r43 <= 0) goto L_0x0fe4
            int r43 = (r46 > r21 ? 1 : (r46 == r21 ? 0 : -1))
            if (r43 != 0) goto L_0x0fe4
            int r43 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1))
            if (r43 >= 0) goto L_0x0fd7
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            if (r11 == 0) goto L_0x0fd5
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            r11.<init>()     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            java.lang.String r12 = "drop frame startTime = "
            r11.append(r12)     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            r11.append(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            java.lang.String r5 = " present time = "
            r11.append(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            long r5 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            r11.append(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            java.lang.String r5 = r11.toString()     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            org.telegram.messenger.FileLog.d(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
        L_0x0fd5:
            r6 = 0
            goto L_0x0fe6
        L_0x0fd7:
            long r5 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            r11 = -2147483648(0xfffffffvar_, double:NaN)
            int r43 = (r52 > r11 ? 1 : (r52 == r11 ? 0 : -1))
            if (r43 == 0) goto L_0x0fe2
            long r33 = r33 - r5
        L_0x0fe2:
            r46 = r5
        L_0x0fe4:
            r6 = r58
        L_0x0fe6:
            if (r2 == 0) goto L_0x0feb
            r46 = r21
            goto L_0x0ffe
        L_0x0feb:
            int r2 = (r35 > r21 ? 1 : (r35 == r21 ? 0 : -1))
            if (r2 != 0) goto L_0x0ffb
            r11 = 0
            int r2 = (r33 > r11 ? 1 : (r33 == r11 ? 0 : -1))
            if (r2 == 0) goto L_0x0ffb
            long r11 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            long r11 = r11 + r33
            r13.presentationTimeUs = r11     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
        L_0x0ffb:
            r14.releaseOutputBuffer(r1, r6)     // Catch:{ Exception -> 0x1051, all -> 0x1078 }
        L_0x0ffe:
            if (r6 == 0) goto L_0x1034
            r5 = 0
            int r1 = (r35 > r5 ? 1 : (r35 == r5 ? 0 : -1))
            if (r1 < 0) goto L_0x1010
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            r11 = r52
            long r1 = java.lang.Math.max(r11, r1)     // Catch:{ Exception -> 0x0var_, all -> 0x1078 }
            r11 = r1
            goto L_0x1012
        L_0x1010:
            r11 = r52
        L_0x1012:
            r27.awaitNewImage()     // Catch:{ Exception -> 0x1017, all -> 0x1078 }
            r1 = 0
            goto L_0x101d
        L_0x1017:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x1051, all -> 0x1078 }
            r1 = 1
        L_0x101d:
            if (r1 != 0) goto L_0x1031
            r27.drawImage()     // Catch:{ Exception -> 0x1051, all -> 0x1078 }
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1051, all -> 0x1078 }
            r41 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 * r41
            r3 = r59
            r3.setPresentationTime(r1)     // Catch:{ Exception -> 0x107a, all -> 0x1078 }
            r3.swapBuffers()     // Catch:{ Exception -> 0x107a, all -> 0x1078 }
            goto L_0x103a
        L_0x1031:
            r3 = r59
            goto L_0x103a
        L_0x1034:
            r11 = r52
            r3 = r59
            r5 = 0
        L_0x103a:
            int r1 = r13.flags     // Catch:{ Exception -> 0x107a, all -> 0x1078 }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x10b4
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x107a, all -> 0x1078 }
            if (r1 == 0) goto L_0x1049
            java.lang.String r1 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x107a, all -> 0x1078 }
        L_0x1049:
            r7.signalEndOfInputStream()     // Catch:{ Exception -> 0x107a, all -> 0x1078 }
            r1 = r79
            r2 = 0
            goto L_0x10b8
        L_0x1051:
            r0 = move-exception
            goto L_0x1091
        L_0x1053:
            r0 = move-exception
            r4 = r78
            r9 = r81
            goto L_0x1091
        L_0x1059:
            r4 = r78
            r9 = r81
            r83 = r12
            r3 = r59
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x107a, all -> 0x1078 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x107a, all -> 0x1078 }
            r5.<init>()     // Catch:{ Exception -> 0x107a, all -> 0x1078 }
            java.lang.String r6 = "unexpected result from decoder.dequeueOutputBuffer: "
            r5.append(r6)     // Catch:{ Exception -> 0x107a, all -> 0x1078 }
            r5.append(r1)     // Catch:{ Exception -> 0x107a, all -> 0x1078 }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x107a, all -> 0x1078 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x107a, all -> 0x1078 }
            throw r2     // Catch:{ Exception -> 0x107a, all -> 0x1078 }
        L_0x1078:
            r0 = move-exception
            goto L_0x1086
        L_0x107a:
            r0 = move-exception
            r2 = r83
            r40 = r85
            r5 = r0
            r59 = r3
            goto L_0x1098
        L_0x1083:
            r0 = move-exception
            r83 = r12
        L_0x1086:
            r2 = r83
            goto L_0x05f3
        L_0x108a:
            r0 = move-exception
            r4 = r78
            r9 = r81
            r83 = r12
        L_0x1091:
            r3 = r59
        L_0x1093:
            r2 = r83
        L_0x1095:
            r40 = r85
            r5 = r0
        L_0x1098:
            r6 = r7
            goto L_0x0a04
        L_0x109b:
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
        L_0x10ae:
            r5 = 0
            r38 = 2500(0x9c4, double:1.235E-320)
            r4 = r78
        L_0x10b4:
            r1 = r79
            r2 = r84
        L_0x10b8:
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
            goto L_0x0bbe
        L_0x10d0:
            r0 = move-exception
            r4 = r78
            r9 = r10
            r3 = r59
            r14 = r61
            goto L_0x1095
        L_0x10d9:
            r4 = r78
            r14 = r3
            r54 = r8
            r9 = r10
            r3 = r59
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1121, all -> 0x112c }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1121, all -> 0x112c }
            r5.<init>()     // Catch:{ Exception -> 0x1121, all -> 0x112c }
            java.lang.String r6 = "encoderOutputBuffer "
            r5.append(r6)     // Catch:{ Exception -> 0x1121, all -> 0x112c }
            r5.append(r1)     // Catch:{ Exception -> 0x1121, all -> 0x112c }
            java.lang.String r1 = " was null"
            r5.append(r1)     // Catch:{ Exception -> 0x1121, all -> 0x112c }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x1121, all -> 0x112c }
            r2.<init>(r1)     // Catch:{ Exception -> 0x1121, all -> 0x112c }
            throw r2     // Catch:{ Exception -> 0x1121, all -> 0x112c }
        L_0x10fd:
            r0 = move-exception
            r4 = r78
            r14 = r3
            goto L_0x1139
        L_0x1102:
            r4 = r78
            r14 = r3
            r54 = r8
            r9 = r10
            r3 = r59
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1121, all -> 0x112c }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1121, all -> 0x112c }
            r5.<init>()     // Catch:{ Exception -> 0x1121, all -> 0x112c }
            java.lang.String r6 = "unexpected result from encoder.dequeueOutputBuffer: "
            r5.append(r6)     // Catch:{ Exception -> 0x1121, all -> 0x112c }
            r5.append(r1)     // Catch:{ Exception -> 0x1121, all -> 0x112c }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x1121, all -> 0x112c }
            r2.<init>(r1)     // Catch:{ Exception -> 0x1121, all -> 0x112c }
            throw r2     // Catch:{ Exception -> 0x1121, all -> 0x112c }
        L_0x1121:
            r0 = move-exception
            r40 = r85
            r5 = r0
            r59 = r3
            goto L_0x1141
        L_0x1128:
            r0 = move-exception
            r4 = r78
            goto L_0x1136
        L_0x112c:
            r0 = move-exception
            r3 = r0
            r2 = r12
            goto L_0x05f4
        L_0x1131:
            r0 = move-exception
            r4 = r78
            r7 = r79
        L_0x1136:
            r14 = r3
            r62 = r6
        L_0x1139:
            r54 = r8
            r9 = r10
            r3 = r59
        L_0x113e:
            r40 = r85
            r5 = r0
        L_0x1141:
            r6 = r7
        L_0x1142:
            r2 = r12
            goto L_0x0a04
        L_0x1145:
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
        L_0x1156:
            r2 = r17
            r3 = r18
            r54 = r60
        L_0x115c:
            r1 = 0
            goto L_0x1234
        L_0x115f:
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
        L_0x1170:
            r3 = r18
            r35 = r30
            r1 = 0
            r2 = -5
            goto L_0x1234
        L_0x1178:
            r0 = move-exception
            r4 = r78
            r7 = r79
            r9 = r81
            r14 = r3
            r3 = r59
            goto L_0x118e
        L_0x1183:
            r0 = move-exception
            r4 = r78
            r7 = r79
            r9 = r81
            r3 = r59
            r14 = r61
        L_0x118e:
            r54 = r83
            r5 = r0
            r6 = r7
        L_0x1192:
            r3 = r18
            r35 = r30
            r1 = 0
            r2 = -5
            goto L_0x11d9
        L_0x1199:
            r0 = move-exception
            r4 = r78
            r7 = r79
            r9 = r81
            r3 = r59
            r14 = r61
            r54 = r83
            r5 = r0
            goto L_0x11b8
        L_0x11a8:
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
        L_0x11b8:
            r6 = r7
            r3 = r18
            r35 = r30
            r1 = 0
            r2 = -5
            goto L_0x11d7
        L_0x11c0:
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
        L_0x11d7:
            r27 = 0
        L_0x11d9:
            r40 = 0
            goto L_0x1234
        L_0x11dd:
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
            goto L_0x122d
        L_0x11f1:
            r0 = move-exception
            r9 = r81
            r30 = r7
            r4 = r14
            r62 = r24
            r54 = r83
            r5 = r0
            r3 = r18
            r35 = r30
            goto L_0x0717
        L_0x1202:
            r0 = move-exception
            r9 = r81
            goto L_0x1211
        L_0x1206:
            r0 = move-exception
            r9 = r81
            r30 = r7
            r4 = r14
            r62 = r24
            goto L_0x1217
        L_0x120f:
            r0 = move-exception
            r9 = r1
        L_0x1211:
            r30 = r7
            r4 = r14
            r62 = r24
            r1 = 0
        L_0x1217:
            r54 = r83
            r5 = r0
            r35 = r30
            goto L_0x122b
        L_0x121d:
            r0 = move-exception
            r9 = r81
            r62 = r4
            r4 = r14
            r1 = 0
            r3 = r79
            r54 = r83
            r35 = r85
            r5 = r0
        L_0x122b:
            r2 = -5
            r6 = 0
        L_0x122d:
            r14 = 0
            r27 = 0
            r40 = 0
            r59 = 0
        L_0x1234:
            boolean r7 = r5 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x12ac, all -> 0x05f2 }
            if (r7 == 0) goto L_0x123b
            if (r90 != 0) goto L_0x123b
            r1 = 1
        L_0x123b:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x12a3, all -> 0x05f2 }
            r7.<init>()     // Catch:{ Exception -> 0x12a3, all -> 0x05f2 }
            java.lang.String r8 = "bitrate: "
            r7.append(r8)     // Catch:{ Exception -> 0x12a3, all -> 0x05f2 }
            r7.append(r3)     // Catch:{ Exception -> 0x12a3, all -> 0x05f2 }
            java.lang.String r8 = " framerate: "
            r7.append(r8)     // Catch:{ Exception -> 0x12a3, all -> 0x05f2 }
            r7.append(r4)     // Catch:{ Exception -> 0x12a3, all -> 0x05f2 }
            java.lang.String r8 = " size: "
            r7.append(r8)     // Catch:{ Exception -> 0x12a3, all -> 0x05f2 }
            r8 = r77
            r7.append(r8)     // Catch:{ Exception -> 0x129f, all -> 0x05f2 }
            java.lang.String r11 = "x"
            r7.append(r11)     // Catch:{ Exception -> 0x129f, all -> 0x05f2 }
            r11 = r76
            r7.append(r11)     // Catch:{ Exception -> 0x129d, all -> 0x05f2 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x129d, all -> 0x05f2 }
            org.telegram.messenger.FileLog.e((java.lang.String) r7)     // Catch:{ Exception -> 0x129d, all -> 0x05f2 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ Exception -> 0x129d, all -> 0x05f2 }
            r17 = r2
            r18 = r3
            r3 = r6
            r2 = r14
            r6 = r1
            r1 = 1
        L_0x1277:
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x1298, all -> 0x1291 }
            r7 = r62
            r5.unselectTrack(r7)     // Catch:{ Exception -> 0x1298, all -> 0x1291 }
            if (r2 == 0) goto L_0x1286
            r2.stop()     // Catch:{ Exception -> 0x1298, all -> 0x1291 }
            r2.release()     // Catch:{ Exception -> 0x1298, all -> 0x1291 }
        L_0x1286:
            r5 = r1
            r7 = r6
            r2 = r17
            r6 = r27
            r1 = r40
            r40 = r59
            goto L_0x12ca
        L_0x1291:
            r0 = move-exception
            r3 = r0
            r1 = r15
            r2 = r17
            goto L_0x13af
        L_0x1298:
            r0 = move-exception
            r1 = r0
            r2 = r17
            goto L_0x12d3
        L_0x129d:
            r0 = move-exception
            goto L_0x12a8
        L_0x129f:
            r0 = move-exception
            r11 = r76
            goto L_0x12a8
        L_0x12a3:
            r0 = move-exception
            r11 = r76
            r8 = r77
        L_0x12a8:
            r6 = r1
            r1 = r0
            goto L_0x132d
        L_0x12ac:
            r0 = move-exception
            r11 = r76
            r8 = r77
            r1 = r0
            goto L_0x132c
        L_0x12b4:
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
        L_0x12ca:
            if (r6 == 0) goto L_0x12d7
            r6.release()     // Catch:{ Exception -> 0x12d0, all -> 0x05f2 }
            goto L_0x12d7
        L_0x12d0:
            r0 = move-exception
            r1 = r0
            r6 = r7
        L_0x12d3:
            r3 = r18
            goto L_0x132d
        L_0x12d7:
            if (r40 == 0) goto L_0x12dc
            r40.release()     // Catch:{ Exception -> 0x12d0, all -> 0x05f2 }
        L_0x12dc:
            if (r3 == 0) goto L_0x12e4
            r3.stop()     // Catch:{ Exception -> 0x12d0, all -> 0x05f2 }
            r3.release()     // Catch:{ Exception -> 0x12d0, all -> 0x05f2 }
        L_0x12e4:
            if (r1 == 0) goto L_0x12e9
            r1.release()     // Catch:{ Exception -> 0x12d0, all -> 0x05f2 }
        L_0x12e9:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x12d0, all -> 0x05f2 }
            r1 = r5
            r6 = r7
            r51 = r8
        L_0x12f0:
            android.media.MediaExtractor r3 = r15.extractor
            if (r3 == 0) goto L_0x12f7
            r3.release()
        L_0x12f7:
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer
            if (r3 == 0) goto L_0x130c
            r3.finishMovie()     // Catch:{ Exception -> 0x1307 }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ Exception -> 0x1307 }
            long r2 = r3.getLastFrameTimestamp(r2)     // Catch:{ Exception -> 0x1307 }
            r15.endPresentationTime = r2     // Catch:{ Exception -> 0x1307 }
            goto L_0x130c
        L_0x1307:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x130c:
            r7 = r51
            r13 = r54
            goto L_0x137f
        L_0x1312:
            r0 = move-exception
            r8 = r77
            r4 = r10
            goto L_0x1320
        L_0x1317:
            r0 = move-exception
            r3 = r0
            r1 = r15
            r2 = -5
            goto L_0x13af
        L_0x131d:
            r0 = move-exception
            r4 = r10
            r8 = r11
        L_0x1320:
            r11 = r12
            r1 = 0
            r9 = r81
            r3 = r79
            r54 = r83
            r35 = r85
            r1 = r0
        L_0x132b:
            r2 = -5
        L_0x132c:
            r6 = 0
        L_0x132d:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x13ab }
            r5.<init>()     // Catch:{ all -> 0x13ab }
            java.lang.String r7 = "bitrate: "
            r5.append(r7)     // Catch:{ all -> 0x13ab }
            r5.append(r3)     // Catch:{ all -> 0x13ab }
            java.lang.String r7 = " framerate: "
            r5.append(r7)     // Catch:{ all -> 0x13ab }
            r5.append(r4)     // Catch:{ all -> 0x13ab }
            java.lang.String r7 = " size: "
            r5.append(r7)     // Catch:{ all -> 0x13ab }
            r5.append(r8)     // Catch:{ all -> 0x13ab }
            java.lang.String r7 = "x"
            r5.append(r7)     // Catch:{ all -> 0x13ab }
            r5.append(r11)     // Catch:{ all -> 0x13ab }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x13ab }
            org.telegram.messenger.FileLog.e((java.lang.String) r5)     // Catch:{ all -> 0x13ab }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x13ab }
            android.media.MediaExtractor r1 = r15.extractor
            if (r1 == 0) goto L_0x1364
            r1.release()
        L_0x1364:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer
            if (r1 == 0) goto L_0x1379
            r1.finishMovie()     // Catch:{ Exception -> 0x1374 }
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ Exception -> 0x1374 }
            long r1 = r1.getLastFrameTimestamp(r2)     // Catch:{ Exception -> 0x1374 }
            r15.endPresentationTime = r1     // Catch:{ Exception -> 0x1374 }
            goto L_0x1379
        L_0x1374:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1379:
            r18 = r3
            r7 = r8
            r13 = r54
            r1 = 1
        L_0x137f:
            if (r6 == 0) goto L_0x13aa
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
        L_0x13aa:
            return r1
        L_0x13ab:
            r0 = move-exception
            r1 = r71
        L_0x13ae:
            r3 = r0
        L_0x13af:
            android.media.MediaExtractor r4 = r1.extractor
            if (r4 == 0) goto L_0x13b6
            r4.release()
        L_0x13b6:
            org.telegram.messenger.video.MP4Builder r4 = r1.mediaMuxer
            if (r4 == 0) goto L_0x13cb
            r4.finishMovie()     // Catch:{ Exception -> 0x13c6 }
            org.telegram.messenger.video.MP4Builder r4 = r1.mediaMuxer     // Catch:{ Exception -> 0x13c6 }
            long r4 = r4.getLastFrameTimestamp(r2)     // Catch:{ Exception -> 0x13c6 }
            r1.endPresentationTime = r4     // Catch:{ Exception -> 0x13c6 }
            goto L_0x13cb
        L_0x13c6:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x13cb:
            goto L_0x13cd
        L_0x13cc:
            throw r3
        L_0x13cd:
            goto L_0x13cc
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
