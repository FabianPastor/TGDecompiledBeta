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
    /* JADX WARNING: Code restructure failed: missing block: B:1000:0x1145, code lost:
        r4 = r78;
        r14 = r3;
        r60 = r8;
        r9 = r10;
        r3 = r59;
        r40 = r13;
        r5 = r0;
        r6 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1001:0x1155, code lost:
        r2 = r17;
        r3 = r18;
        r54 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1003:0x115e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1004:0x115f, code lost:
        r4 = r78;
        r14 = r3;
        r9 = r10;
        r3 = r59;
        r54 = r83;
        r40 = r13;
        r5 = r0;
        r6 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1005:0x116f, code lost:
        r3 = r18;
        r35 = r30;
        r1 = 0;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1006:0x1177, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1007:0x1178, code lost:
        r4 = r78;
        r7 = r79;
        r9 = r81;
        r14 = r3;
        r3 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1008:0x1182, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1009:0x1183, code lost:
        r4 = r78;
        r7 = r79;
        r9 = r81;
        r3 = r59;
        r14 = r61;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1010:0x118d, code lost:
        r54 = r83;
        r5 = r0;
        r6 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1012:0x1198, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1013:0x1199, code lost:
        r4 = r78;
        r7 = r79;
        r9 = r81;
        r3 = r59;
        r14 = r61;
        r54 = r83;
        r5 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1014:0x11a7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1015:0x11a8, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:1016:0x11b7, code lost:
        r6 = r7;
        r3 = r18;
        r35 = r30;
        r1 = 0;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1017:0x11bf, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1018:0x11c0, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:1019:0x11d6, code lost:
        r27 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1021:0x11dc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1022:0x11dd, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:1023:0x11f0, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1024:0x11f1, code lost:
        r9 = r81;
        r4 = r14;
        r62 = r24;
        r54 = r83;
        r5 = r0;
        r3 = r18;
        r35 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1025:0x1201, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1026:0x1202, code lost:
        r9 = r81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1027:0x1205, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1028:0x1206, code lost:
        r9 = r81;
        r30 = r7;
        r4 = r14;
        r62 = r24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1029:0x120e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1030:0x120f, code lost:
        r9 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1031:0x1210, code lost:
        r30 = r7;
        r4 = r14;
        r62 = r24;
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1032:0x1216, code lost:
        r54 = r83;
        r5 = r0;
        r35 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1033:0x121c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1034:0x121d, code lost:
        r9 = r81;
        r62 = r4;
        r4 = r14;
        r1 = 0;
        r3 = r79;
        r54 = r83;
        r35 = r85;
        r5 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1056:0x128f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1057:0x1290, code lost:
        r3 = r0;
        r1 = r15;
        r2 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1060:0x129b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1061:0x129d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1062:0x129e, code lost:
        r11 = r76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1063:0x12a1, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1064:0x12a2, code lost:
        r11 = r76;
        r8 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1065:0x12a6, code lost:
        r6 = r1;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1066:0x12aa, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1067:0x12ab, code lost:
        r11 = r76;
        r8 = r77;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1072:0x12ce, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1073:0x12cf, code lost:
        r1 = r0;
        r6 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1093:0x1310, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1094:0x1311, code lost:
        r8 = r77;
        r4 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1095:0x1315, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1096:0x1316, code lost:
        r3 = r0;
        r1 = r15;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1097:0x131b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1098:0x131c, code lost:
        r4 = r10;
        r8 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1099:0x131e, code lost:
        r11 = r12;
        r9 = r81;
        r3 = r79;
        r54 = r83;
        r35 = r85;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1100:0x1329, code lost:
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1101:0x132a, code lost:
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1106:0x135e, code lost:
        r1.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1110:?, code lost:
        r1.finishMovie();
        r15.endPresentationTime = r15.mediaMuxer.getLastFrameTimestamp(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1111:0x1371, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1112:0x1372, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1121:0x13b0, code lost:
        r4.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1125:?, code lost:
        r4.finishMovie();
        r1.endPresentationTime = r1.mediaMuxer.getLastFrameTimestamp(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1126:0x13c3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1127:0x13c4, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x0537, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x0538, code lost:
        r50 = r9;
        r51 = r11;
        r6 = r1;
        r2 = -5;
        r30 = null;
        r36 = null;
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
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x05a4, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x05a5, code lost:
        r2 = r3;
        r1 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x05a9, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x05aa, code lost:
        r54 = r83;
        r35 = r85;
        r1 = r0;
        r2 = r3;
        r3 = r9;
        r4 = r10;
        r11 = r12;
        r8 = r51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x05d6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x05d7, code lost:
        r54 = r83;
        r35 = r85;
        r1 = r0;
        r3 = r9;
        r4 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x05df, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x05e1, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x05e2, code lost:
        r9 = r50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x05e4, code lost:
        r4 = r78;
        r54 = r83;
        r35 = r85;
        r1 = r0;
        r3 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x05ec, code lost:
        r8 = r11;
        r11 = r12;
        r6 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x05f1, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x0634, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x0635, code lost:
        r8 = r77;
        r54 = r83;
        r35 = r85;
        r1 = r0;
        r3 = r9;
        r4 = r10;
        r11 = r12;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x0692, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x0693, code lost:
        r11 = r76;
        r8 = r77;
        r3 = r79;
        r9 = r81;
        r54 = r83;
        r35 = r85;
        r1 = r0;
        r4 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x06a3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x06a4, code lost:
        r11 = r76;
        r8 = r77;
        r3 = r79;
        r54 = r83;
        r35 = r85;
        r1 = r0;
        r4 = r10;
        r2 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x070b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x070c, code lost:
        r9 = r81;
        r54 = r83;
        r5 = r0;
        r62 = r4;
        r4 = r14;
        r35 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x073b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x073c, code lost:
        r9 = r81;
        r54 = r83;
        r5 = r0;
        r62 = r4;
        r35 = r7;
        r4 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x0760, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0761, code lost:
        r54 = r83;
        r5 = r0;
        r9 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x07f8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x07f9, code lost:
        r9 = r81;
        r54 = r83;
        r5 = r0;
        r3 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x0890, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0891, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x0913, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x0914, code lost:
        r4 = r78;
        r6 = r79;
        r9 = r81;
        r54 = r83;
        r5 = r0;
        r14 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x0939, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x093a, code lost:
        r4 = r78;
        r6 = r79;
        r54 = r83;
        r5 = r0;
        r14 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x0961, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0963, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x0964, code lost:
        r8 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0966, code lost:
        r4 = r78;
        r6 = r79;
        r5 = r0;
        r14 = r3;
        r54 = r8;
        r9 = r10;
        r40 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x0973, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0974, code lost:
        r4 = r78;
        r6 = r79;
        r5 = r0;
        r14 = r3;
        r54 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x098a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x098b, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x09f5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x09f6, code lost:
        r4 = r78;
        r6 = r79;
        r5 = r0;
        r14 = r3;
        r54 = r8;
        r9 = r10;
        r40 = r13;
        r2 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x0a61, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x0a62, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x0a80, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x0a81, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x0a90, code lost:
        r1 = 0;
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:588:0x0a91, code lost:
        r6 = r79;
        r4 = r4;
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
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x0ae5, code lost:
        if (r13.presentationTimeUs < r8) goto L_0x0aea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x0b23, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x0b24, code lost:
        r60 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x0b2c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x0b2d, code lost:
        r60 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0b2f, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0b40, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0b41, code lost:
        r60 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0b76, code lost:
        r0 = e;
        r60 = r60;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0b77, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0b8a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0b8b, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x0d6f, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x0d8d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x0d8e, code lost:
        r61 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x0d90, code lost:
        r4 = r78;
        r40 = r85;
        r5 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x0d96, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x0d97, code lost:
        r61 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x0eca, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x0ecb, code lost:
        r4 = r78;
        r9 = r81;
        r54 = r54;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x1077, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:0x1079, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:962:0x107a, code lost:
        r2 = r83;
        r40 = r85;
        r5 = r0;
        r59 = r3;
        r54 = r54;
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:976:0x10cf, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:977:0x10d0, code lost:
        r4 = r78;
        r9 = r10;
        r3 = r59;
        r14 = r61;
        r54 = r54;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:986:0x1120, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:987:0x1121, code lost:
        r40 = r85;
        r5 = r0;
        r59 = r3;
        r54 = r54;
        r14 = r14;
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:990:0x112b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:991:0x112c, code lost:
        r3 = r0;
        r2 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:999:0x1144, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:65:0x01b3, B:1037:0x1233] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:65:0x01b3, B:1042:0x123a] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:65:0x01b3, B:1045:0x1256] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:65:0x01b3, B:1048:0x1260] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:65:0x01b3, B:1070:0x12ca] */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:65:0x01b3, B:824:0x0e35] */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1054:0x127e A[Catch:{ Exception -> 0x1296, all -> 0x128f }] */
    /* JADX WARNING: Removed duplicated region for block: B:1056:0x128f A[ExcHandler: all (r0v10 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r17 
      PHI: (r17v1 int) = (r17v2 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int), (r17v5 int) binds: [B:1051:0x1275, B:548:0x09e5, B:549:?, B:559:0x0a09, B:594:0x0aa2, B:595:?, B:600:0x0abb, B:601:?, B:610:0x0ad5, B:611:?, B:622:0x0aeb, B:623:?, B:625:0x0afb, B:629:0x0b05, B:616:0x0ae1, B:617:?, B:607:0x0ad2, B:608:?, B:603:0x0abf, B:604:?, B:598:0x0ab3, B:599:?, B:564:0x0a1b, B:573:0x0a33, B:577:0x0a45, B:552:0x09ec] A[DONT_GENERATE, DONT_INLINE], Splitter:B:548:0x09e5] */
    /* JADX WARNING: Removed duplicated region for block: B:1068:0x12b2  */
    /* JADX WARNING: Removed duplicated region for block: B:1070:0x12ca A[SYNTHETIC, Splitter:B:1070:0x12ca] */
    /* JADX WARNING: Removed duplicated region for block: B:1076:0x12d7 A[Catch:{ Exception -> 0x12ce, all -> 0x05f1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1078:0x12dc A[Catch:{ Exception -> 0x12ce, all -> 0x05f1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1080:0x12e4 A[Catch:{ Exception -> 0x12ce, all -> 0x05f1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1085:0x12f2  */
    /* JADX WARNING: Removed duplicated region for block: B:1088:0x12f9 A[SYNTHETIC, Splitter:B:1088:0x12f9] */
    /* JADX WARNING: Removed duplicated region for block: B:1095:0x1315 A[ExcHandler: all (r0v6 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:1:0x0017] */
    /* JADX WARNING: Removed duplicated region for block: B:1106:0x135e  */
    /* JADX WARNING: Removed duplicated region for block: B:1109:0x1365 A[SYNTHETIC, Splitter:B:1109:0x1365] */
    /* JADX WARNING: Removed duplicated region for block: B:1115:0x137e  */
    /* JADX WARNING: Removed duplicated region for block: B:1121:0x13b0  */
    /* JADX WARNING: Removed duplicated region for block: B:1124:0x13b7 A[SYNTHETIC, Splitter:B:1124:0x13b7] */
    /* JADX WARNING: Removed duplicated region for block: B:1171:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x03e4 A[ExcHandler: all (th java.lang.Throwable), PHI: r5 
      PHI: (r5v171 int) = (r5v170 int), (r5v170 int), (r5v172 int) binds: [B:180:0x035b, B:181:?, B:186:0x0378] A[DONT_GENERATE, DONT_INLINE], Splitter:B:180:0x035b] */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x040f A[Catch:{ Exception -> 0x0497, all -> 0x05f1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0411 A[Catch:{ Exception -> 0x0497, all -> 0x05f1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x043b  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x046d A[ExcHandler: all (r0v127 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:234:0x043d] */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x04df A[ExcHandler: all (th java.lang.Throwable), PHI: r18 
      PHI: (r18v20 int) = (r18v19 int), (r18v30 int), (r18v34 int), (r18v34 int), (r18v41 int), (r18v41 int) binds: [B:256:0x04a8, B:205:0x03cc, B:197:0x039e, B:198:?, B:167:0x0332, B:168:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:167:0x0332] */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x04e8 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:158:0x0313] */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x055a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x05a0 A[SYNTHETIC, Splitter:B:310:0x05a0] */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x05a4 A[Catch:{ Exception -> 0x05a9, all -> 0x05a4 }, ExcHandler: all (th java.lang.Throwable), PHI: r3 
      PHI: (r3v142 int) = (r3v143 int), (r3v165 int), (r3v165 int), (r3v165 int), (r3v165 int) binds: [B:310:0x05a0, B:131:0x02ac, B:117:0x0294, B:118:?, B:85:0x0200] A[DONT_GENERATE, DONT_INLINE], Splitter:B:85:0x0200] */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x05b9 A[Catch:{ Exception -> 0x05a9, all -> 0x05a4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:319:0x05be A[Catch:{ Exception -> 0x05a9, all -> 0x05a4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:329:0x05f1 A[ExcHandler: all (th java.lang.Throwable), PHI: r2 
      PHI: (r2v168 int) = (r2v20 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v26 int), (r2v94 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v173 int), (r2v187 int), (r2v187 int), (r2v215 int) binds: [B:1070:0x12ca, B:1037:0x1233, B:1038:?, B:1042:0x123a, B:1043:?, B:1045:0x1256, B:1046:?, B:1048:0x1260, B:1049:?, B:824:0x0e35, B:294:0x0556, B:295:?, B:300:0x0561, B:301:?, B:303:0x056d, B:304:?, B:306:0x0577, B:307:?, B:65:0x01b3, B:66:?, B:223:0x0409] A[DONT_GENERATE, DONT_INLINE], Splitter:B:65:0x01b3] */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x06bb  */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x077b  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0799  */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x07a3 A[SYNTHETIC, Splitter:B:436:0x07a3] */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x07e1 A[SYNTHETIC, Splitter:B:442:0x07e1] */
    /* JADX WARNING: Removed duplicated region for block: B:475:0x0880 A[SYNTHETIC, Splitter:B:475:0x0880] */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x08a5  */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x08ab A[SYNTHETIC, Splitter:B:482:0x08ab] */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x08d9  */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x08dc  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0981  */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x09a1  */
    /* JADX WARNING: Removed duplicated region for block: B:539:0x09ab  */
    /* JADX WARNING: Removed duplicated region for block: B:540:0x09ae  */
    /* JADX WARNING: Removed duplicated region for block: B:545:0x09ce A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:559:0x0a09 A[SYNTHETIC, Splitter:B:559:0x0a09] */
    /* JADX WARNING: Removed duplicated region for block: B:648:0x0b56 A[Catch:{ Exception -> 0x0b76, all -> 0x128f }] */
    /* JADX WARNING: Removed duplicated region for block: B:654:0x0b67 A[Catch:{ Exception -> 0x0b76, all -> 0x128f }] */
    /* JADX WARNING: Removed duplicated region for block: B:658:0x0b88  */
    /* JADX WARNING: Removed duplicated region for block: B:661:0x0b9a  */
    /* JADX WARNING: Removed duplicated region for block: B:664:0x0bbf A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:669:0x0be3  */
    /* JADX WARNING: Removed duplicated region for block: B:670:0x0bec  */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0bff  */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:776:0x0d63 A[Catch:{ Exception -> 0x0d8d, all -> 0x112b }] */
    /* JADX WARNING: Removed duplicated region for block: B:789:0x0d82 A[Catch:{ Exception -> 0x0e15, all -> 0x112b }] */
    /* JADX WARNING: Removed duplicated region for block: B:827:0x0e3b A[Catch:{ Exception -> 0x10cf, all -> 0x05f1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:828:0x0e3d A[Catch:{ Exception -> 0x10cf, all -> 0x05f1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x0e4a  */
    /* JADX WARNING: Removed duplicated region for block: B:833:0x0e69  */
    /* JADX WARNING: Removed duplicated region for block: B:900:0x0fa2  */
    /* JADX WARNING: Removed duplicated region for block: B:901:0x0fa5  */
    /* JADX WARNING: Removed duplicated region for block: B:908:0x0fb2 A[SYNTHETIC, Splitter:B:908:0x0fb2] */
    /* JADX WARNING: Removed duplicated region for block: B:913:0x0fd6 A[Catch:{ Exception -> 0x0var_, all -> 0x1077 }] */
    /* JADX WARNING: Removed duplicated region for block: B:919:0x0fe7 A[Catch:{ Exception -> 0x0var_, all -> 0x1077 }] */
    /* JADX WARNING: Removed duplicated region for block: B:920:0x0fea A[Catch:{ Exception -> 0x0var_, all -> 0x1077 }] */
    /* JADX WARNING: Removed duplicated region for block: B:928:0x0fff  */
    /* JADX WARNING: Removed duplicated region for block: B:947:0x1033 A[Catch:{ Exception -> 0x1079, all -> 0x1077 }] */
    /* JADX WARNING: Removed duplicated region for block: B:950:0x103f A[Catch:{ Exception -> 0x1079, all -> 0x1077 }] */
    /* JADX WARNING: Removed duplicated region for block: B:960:0x1077 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:934:0x1011] */
    /* JADX WARNING: Removed duplicated region for block: B:990:0x112b A[ExcHandler: all (r0v35 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r12 
      PHI: (r12v7 int) = (r12v3 int), (r12v3 int), (r12v3 int), (r12v8 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int), (r12v3 int) binds: [B:666:0x0bde, B:667:?, B:672:0x0bf8, B:845:0x0ead, B:714:0x0cbf, B:715:?, B:979:0x10e0, B:727:0x0cd5, B:728:?, B:768:0x0d51, B:769:?, B:771:0x0d55, B:784:0x0d73, B:785:?, B:787:0x0d7c, B:779:0x0d6a, B:780:?, B:737:0x0cf0, B:746:0x0d0f, B:732:0x0cdd, B:721:0x0cca, B:718:0x0cc5, B:719:?, B:691:0x0c4f, B:697:0x0c5d, B:680:0x0c1a] A[DONT_GENERATE, DONT_INLINE], Splitter:B:768:0x0d51] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:300:0x0561=Splitter:B:300:0x0561, B:1037:0x1233=Splitter:B:1037:0x1233, B:824:0x0e35=Splitter:B:824:0x0e35, B:294:0x0556=Splitter:B:294:0x0556, B:223:0x0409=Splitter:B:223:0x0409, B:1042:0x123a=Splitter:B:1042:0x123a} */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:37:0x0100=Splitter:B:37:0x0100, B:541:0x09b0=Splitter:B:541:0x09b0, B:456:0x0803=Splitter:B:456:0x0803, B:61:0x01a7=Splitter:B:61:0x01a7, B:16:0x0089=Splitter:B:16:0x0089, B:490:0x08cd=Splitter:B:490:0x08cd, B:433:0x079f=Splitter:B:433:0x079f} */
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
            android.media.MediaCodec$BufferInfo r7 = new android.media.MediaCodec$BufferInfo     // Catch:{ Exception -> 0x131b, all -> 0x1315 }
            r7.<init>()     // Catch:{ Exception -> 0x131b, all -> 0x1315 }
            org.telegram.messenger.video.Mp4Movie r2 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ Exception -> 0x131b, all -> 0x1315 }
            r2.<init>()     // Catch:{ Exception -> 0x131b, all -> 0x1315 }
            r1 = r73
            r2.setCacheFile(r1)     // Catch:{ Exception -> 0x131b, all -> 0x1315 }
            r2.setRotation(r6)     // Catch:{ Exception -> 0x131b, all -> 0x1315 }
            r2.setSize(r12, r11)     // Catch:{ Exception -> 0x131b, all -> 0x1315 }
            org.telegram.messenger.video.MP4Builder r6 = new org.telegram.messenger.video.MP4Builder     // Catch:{ Exception -> 0x131b, all -> 0x1315 }
            r6.<init>()     // Catch:{ Exception -> 0x131b, all -> 0x1315 }
            r14 = r75
            org.telegram.messenger.video.MP4Builder r2 = r6.createMovie(r2, r14)     // Catch:{ Exception -> 0x131b, all -> 0x1315 }
            r15.mediaMuxer = r2     // Catch:{ Exception -> 0x131b, all -> 0x1315 }
            float r2 = (float) r4     // Catch:{ Exception -> 0x131b, all -> 0x1315 }
            r19 = 1148846080(0x447a0000, float:1000.0)
            float r20 = r2 / r19
            r21 = 1000(0x3e8, double:4.94E-321)
            long r1 = r4 * r21
            r15.endPresentationTime = r1     // Catch:{ Exception -> 0x131b, all -> 0x1315 }
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x131b, all -> 0x1315 }
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
            if (r94 == 0) goto L_0x0608
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
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            if (r2 == 0) goto L_0x00b8
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            r2.<init>()     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            java.lang.String r6 = "changing width from "
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            r2.append(r12)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            float r6 = (float) r12     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            float r6 = r6 / r31
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
        L_0x00b8:
            float r2 = (float) r12     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            float r2 = r2 / r31
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
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
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            if (r2 == 0) goto L_0x00f6
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            r2.<init>()     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            java.lang.String r6 = "changing height from "
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            r2.append(r11)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            float r6 = (float) r11     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            float r6 = r6 / r31
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
        L_0x00f6:
            float r2 = (float) r11     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            float r2 = r2 / r31
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            int r2 = r2 * 16
            r11 = r2
        L_0x0100:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0544, all -> 0x1315 }
            if (r2 == 0) goto L_0x0128
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            r2.<init>()     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            java.lang.String r6 = "create photo encoder "
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            r2.append(r12)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            java.lang.String r6 = " "
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            r2.append(r11)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            java.lang.String r6 = " duration = "
            r2.append(r6)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            r2.append(r4)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00c3, all -> 0x1315 }
        L_0x0128:
            r7 = r29
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r7, r12, r11)     // Catch:{ Exception -> 0x0544, all -> 0x1315 }
            java.lang.String r6 = "color-format"
            r29 = r1
            r1 = 2130708361(0x7var_, float:1.701803E38)
            r2.setInteger(r6, r1)     // Catch:{ Exception -> 0x0544, all -> 0x1315 }
            java.lang.String r1 = "bitrate"
            r2.setInteger(r1, r9)     // Catch:{ Exception -> 0x0544, all -> 0x1315 }
            java.lang.String r1 = "frame-rate"
            r2.setInteger(r1, r10)     // Catch:{ Exception -> 0x0544, all -> 0x1315 }
            java.lang.String r1 = "i-frame-interval"
            r6 = 2
            r2.setInteger(r1, r6)     // Catch:{ Exception -> 0x0544, all -> 0x1315 }
            android.media.MediaCodec r1 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x0544, all -> 0x1315 }
            r3 = 1
            r6 = 0
            r1.configure(r2, r6, r6, r3)     // Catch:{ Exception -> 0x0537, all -> 0x1315 }
            org.telegram.messenger.video.InputSurface r2 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x0537, all -> 0x1315 }
            android.view.Surface r3 = r1.createInputSurface()     // Catch:{ Exception -> 0x0537, all -> 0x1315 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0537, all -> 0x1315 }
            r2.makeCurrent()     // Catch:{ Exception -> 0x052a, all -> 0x1315 }
            r1.start()     // Catch:{ Exception -> 0x052a, all -> 0x1315 }
            org.telegram.messenger.video.OutputSurface r30 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x052a, all -> 0x1315 }
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
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0521, all -> 0x1315 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0517, all -> 0x1315 }
            if (r1 >= r14) goto L_0x01a6
            java.nio.ByteBuffer[] r6 = r76.getOutputBuffers()     // Catch:{ Exception -> 0x019d, all -> 0x1315 }
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
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x0517, all -> 0x1315 }
            r4 = r6
            r1 = 1
            r2 = -5
            r3 = 0
            r5 = 0
            r6 = 0
            r7 = 0
        L_0x01b1:
            if (r6 != 0) goto L_0x0505
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x04fc, all -> 0x05f1 }
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
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            r77 = r7
            r7 = 21
            if (r11 >= r7) goto L_0x020c
            java.nio.ByteBuffer[] r5 = r1.getOutputBuffers()     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
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
            android.media.MediaFormat r7 = r1.getOutputFormat()     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            if (r11 == 0) goto L_0x0244
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            r11.<init>()     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            r79 = r8
            java.lang.String r8 = "photo encoder new format "
            r11.append(r8)     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            r11.append(r7)     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            java.lang.String r8 = r11.toString()     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            org.telegram.messenger.FileLog.d(r8)     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            goto L_0x0246
        L_0x0244:
            r79 = r8
        L_0x0246:
            r8 = -5
            if (r3 != r8) goto L_0x027b
            if (r7 == 0) goto L_0x027b
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            r8 = 0
            int r3 = r11.addTrack(r7, r8)     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            r11 = r45
            boolean r16 = r7.containsKey(r11)     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            if (r16 == 0) goto L_0x0279
            int r8 = r7.getInteger(r11)     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            r45 = r11
            r11 = 1
            if (r8 != r11) goto L_0x027b
            r8 = r35
            java.nio.ByteBuffer r6 = r7.getByteBuffer(r8)     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            r11 = r44
            java.nio.ByteBuffer r7 = r7.getByteBuffer(r11)     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            int r6 = r6.limit()     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            int r7 = r7.limit()     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
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
            r7 = r5[r10]     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
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
            int r13 = r14.flags     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            r16 = 1
            r13 = r13 & 1
            if (r13 == 0) goto L_0x02be
            int r13 = r14.offset     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            int r13 = r13 + r6
            r14.offset = r13     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            int r13 = r14.size     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            int r13 = r13 - r6
            r14.size = r13     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
        L_0x02be:
            if (r2 == 0) goto L_0x0313
            int r13 = r14.flags     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            r16 = 1
            r13 = r13 & 1
            if (r13 == 0) goto L_0x0313
            int r2 = r14.size     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            r13 = 100
            if (r2 <= r13) goto L_0x0312
            int r2 = r14.offset     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            r7.position(r2)     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            byte[] r2 = new byte[r13]     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            r7.get(r2)     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            r13 = 0
            r16 = 0
        L_0x02db:
            r5 = 96
            if (r13 >= r5) goto L_0x0312
            byte r5 = r2[r13]     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            if (r5 != 0) goto L_0x030a
            int r5 = r13 + 1
            byte r5 = r2[r5]     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            if (r5 != 0) goto L_0x030a
            int r5 = r13 + 2
            byte r5 = r2[r5]     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            if (r5 != 0) goto L_0x030a
            int r5 = r13 + 3
            byte r5 = r2[r5]     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            r17 = r2
            r2 = 1
            if (r5 != r2) goto L_0x030c
            int r5 = r16 + 1
            if (r5 <= r2) goto L_0x0307
            int r2 = r14.offset     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            int r2 = r2 + r13
            r14.offset = r2     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            int r2 = r14.size     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
            int r2 = r2 - r13
            r14.size = r2     // Catch:{ Exception -> 0x021b, all -> 0x05a4 }
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
            int r5 = r14.flags     // Catch:{ Exception -> 0x0497, all -> 0x05f1 }
            r5 = r5 & 4
            if (r5 == 0) goto L_0x0411
            r5 = 1
            goto L_0x0412
        L_0x0411:
            r5 = 0
        L_0x0412:
            r6 = 0
            r1.releaseOutputBuffer(r10, r6)     // Catch:{ Exception -> 0x0497, all -> 0x05f1 }
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
            goto L_0x05f3
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
            goto L_0x13ac
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
            goto L_0x059e
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
            boolean r3 = r1 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x05f6, all -> 0x05f1 }
            if (r3 == 0) goto L_0x055f
            if (r90 != 0) goto L_0x055f
            r43 = 1
            goto L_0x0561
        L_0x055f:
            r43 = 0
        L_0x0561:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05e1, all -> 0x05f1 }
            r3.<init>()     // Catch:{ Exception -> 0x05e1, all -> 0x05f1 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ Exception -> 0x05e1, all -> 0x05f1 }
            r9 = r50
            r3.append(r9)     // Catch:{ Exception -> 0x05df, all -> 0x05f1 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ Exception -> 0x05df, all -> 0x05f1 }
            r10 = r78
            r3.append(r10)     // Catch:{ Exception -> 0x05d6, all -> 0x05f1 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ Exception -> 0x05d6, all -> 0x05f1 }
            r3.append(r11)     // Catch:{ Exception -> 0x05d6, all -> 0x05f1 }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ Exception -> 0x05d6, all -> 0x05f1 }
            r3.append(r12)     // Catch:{ Exception -> 0x05d6, all -> 0x05f1 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x05d6, all -> 0x05f1 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ Exception -> 0x05d6, all -> 0x05f1 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x05d6, all -> 0x05f1 }
            r3 = r2
            r1 = r6
            r51 = r11
            r2 = r36
            r6 = r43
            r43 = 1
        L_0x059e:
            if (r30 == 0) goto L_0x05b7
            r30.release()     // Catch:{ Exception -> 0x05a9, all -> 0x05a4 }
            goto L_0x05b7
        L_0x05a4:
            r0 = move-exception
            r2 = r3
            r1 = r15
            goto L_0x13ab
        L_0x05a9:
            r0 = move-exception
            r54 = r83
            r35 = r85
            r1 = r0
            r2 = r3
            r3 = r9
            r4 = r10
            r11 = r12
            r8 = r51
            goto L_0x0604
        L_0x05b7:
            if (r2 == 0) goto L_0x05bc
            r2.release()     // Catch:{ Exception -> 0x05a9, all -> 0x05a4 }
        L_0x05bc:
            if (r1 == 0) goto L_0x05c4
            r1.stop()     // Catch:{ Exception -> 0x05a9, all -> 0x05a4 }
            r1.release()     // Catch:{ Exception -> 0x05a9, all -> 0x05a4 }
        L_0x05c4:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x05a9, all -> 0x05a4 }
            r54 = r83
            r35 = r85
            r2 = r3
            r18 = r9
            r4 = r10
            r11 = r12
            r1 = r43
            r9 = r81
            goto L_0x12ee
        L_0x05d6:
            r0 = move-exception
            r54 = r83
            r35 = r85
            r1 = r0
            r3 = r9
            r4 = r10
            goto L_0x05ec
        L_0x05df:
            r0 = move-exception
            goto L_0x05e4
        L_0x05e1:
            r0 = move-exception
            r9 = r50
        L_0x05e4:
            r4 = r78
            r54 = r83
            r35 = r85
            r1 = r0
            r3 = r9
        L_0x05ec:
            r8 = r11
            r11 = r12
            r6 = r43
            goto L_0x0604
        L_0x05f1:
            r0 = move-exception
        L_0x05f2:
            r3 = r0
        L_0x05f3:
            r1 = r15
            goto L_0x13ac
        L_0x05f6:
            r0 = move-exception
            r9 = r50
            r4 = r78
            r54 = r83
            r35 = r85
            r1 = r0
            r3 = r9
            r8 = r11
            r11 = r12
        L_0x0603:
            r6 = 0
        L_0x0604:
            r9 = r81
            goto L_0x132b
        L_0x0608:
            r8 = r1
            r11 = r21
            r14 = r23
            r7 = r24
            r13 = r29
            r1 = 921600(0xe1000, float:1.291437E-39)
            android.media.MediaExtractor r2 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x1310, all -> 0x1315 }
            r2.<init>()     // Catch:{ Exception -> 0x1310, all -> 0x1315 }
            r15.extractor = r2     // Catch:{ Exception -> 0x1310, all -> 0x1315 }
            r5 = r72
            r2.setDataSource(r5)     // Catch:{ Exception -> 0x1310, all -> 0x1315 }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x1310, all -> 0x1315 }
            r6 = 0
            int r4 = org.telegram.messenger.MediaController.findTrack(r2, r6)     // Catch:{ Exception -> 0x1310, all -> 0x1315 }
            r2 = -1
            if (r9 == r2) goto L_0x0641
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0634, all -> 0x1315 }
            r3 = 1
            int r2 = org.telegram.messenger.MediaController.findTrack(r2, r3)     // Catch:{ Exception -> 0x0634, all -> 0x1315 }
            r44 = r11
            goto L_0x0645
        L_0x0634:
            r0 = move-exception
            r8 = r77
            r54 = r83
            r35 = r85
            r1 = r0
            r3 = r9
            r4 = r10
            r11 = r12
            r2 = -5
            goto L_0x0604
        L_0x0641:
            r3 = 1
            r44 = r11
            r2 = -1
        L_0x0645:
            java.lang.String r11 = "mime"
            if (r4 < 0) goto L_0x065b
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x0634, all -> 0x1315 }
            android.media.MediaFormat r3 = r3.getTrackFormat(r4)     // Catch:{ Exception -> 0x0634, all -> 0x1315 }
            java.lang.String r3 = r3.getString(r11)     // Catch:{ Exception -> 0x0634, all -> 0x1315 }
            boolean r3 = r3.equals(r13)     // Catch:{ Exception -> 0x0634, all -> 0x1315 }
            if (r3 != 0) goto L_0x065b
            r3 = 1
            goto L_0x065c
        L_0x065b:
            r3 = 0
        L_0x065c:
            if (r89 != 0) goto L_0x06b3
            if (r3 == 0) goto L_0x0662
            goto L_0x06b3
        L_0x0662:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x06a3, all -> 0x1315 }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ Exception -> 0x06a3, all -> 0x1315 }
            r1 = -1
            if (r9 == r1) goto L_0x066b
            r13 = 1
            goto L_0x066c
        L_0x066b:
            r13 = 0
        L_0x066c:
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
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ Exception -> 0x0692, all -> 0x1315 }
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
            goto L_0x12ee
        L_0x0692:
            r0 = move-exception
            r11 = r76
            r8 = r77
            r3 = r79
            r9 = r81
            r54 = r83
            r35 = r85
            r1 = r0
            r4 = r14
            goto L_0x1329
        L_0x06a3:
            r0 = move-exception
            r11 = r76
            r8 = r77
            r3 = r79
            r54 = r83
            r35 = r85
            r1 = r0
            r4 = r10
            r2 = -5
            goto L_0x0603
        L_0x06b3:
            r12 = r5
            r69 = r14
            r14 = r10
            r10 = r69
            if (r4 < 0) goto L_0x12b2
            r16 = -2147483648(0xfffffffvar_, double:NaN)
            r3 = 1000(0x3e8, float:1.401E-42)
            r21 = -1
            int r3 = r3 / r14
            int r3 = r3 * 1000
            long r5 = (long) r3     // Catch:{ Exception -> 0x121c, all -> 0x1315 }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x121c, all -> 0x1315 }
            r3.selectTrack(r4)     // Catch:{ Exception -> 0x121c, all -> 0x1315 }
            android.media.MediaExtractor r3 = r15.extractor     // Catch:{ Exception -> 0x121c, all -> 0x1315 }
            android.media.MediaFormat r9 = r3.getTrackFormat(r4)     // Catch:{ Exception -> 0x121c, all -> 0x1315 }
            r23 = 0
            int r3 = (r85 > r23 ? 1 : (r85 == r23 ? 0 : -1))
            if (r3 < 0) goto L_0x06f4
            r3 = 1157234688(0x44fa0000, float:2000.0)
            int r3 = (r20 > r3 ? 1 : (r20 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x06e1
            r3 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x06ef
        L_0x06e1:
            r3 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r3 = (r20 > r3 ? 1 : (r20 == r3 ? 0 : -1))
            if (r3 > 0) goto L_0x06ec
            r3 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x06ef
        L_0x06ec:
            r3 = 1560000(0x17cdc0, float:2.186026E-39)
        L_0x06ef:
            r1 = r80
            r23 = 0
            goto L_0x0704
        L_0x06f4:
            if (r79 > 0) goto L_0x06fe
            r1 = r80
            r23 = r85
            r3 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x0704
        L_0x06fe:
            r3 = r79
            r1 = r80
            r23 = r85
        L_0x0704:
            if (r1 <= 0) goto L_0x0719
            int r3 = java.lang.Math.min(r1, r3)     // Catch:{ Exception -> 0x070b, all -> 0x1315 }
            goto L_0x0719
        L_0x070b:
            r0 = move-exception
            r9 = r81
            r54 = r83
            r5 = r0
            r62 = r4
            r4 = r14
            r35 = r23
        L_0x0716:
            r1 = 0
            goto L_0x122a
        L_0x0719:
            r25 = 0
            int r27 = (r23 > r25 ? 1 : (r23 == r25 ? 0 : -1))
            r45 = r7
            r35 = r8
            if (r27 < 0) goto L_0x0726
            r7 = r21
            goto L_0x0728
        L_0x0726:
            r7 = r23
        L_0x0728:
            int r23 = (r7 > r25 ? 1 : (r7 == r25 ? 0 : -1))
            if (r23 < 0) goto L_0x0747
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x073b, all -> 0x1315 }
            r23 = r2
            r2 = 0
            r1.seekTo(r7, r2)     // Catch:{ Exception -> 0x073b, all -> 0x1315 }
            r24 = r4
            r25 = r5
            r5 = 0
            goto L_0x0777
        L_0x073b:
            r0 = move-exception
            r9 = r81
            r54 = r83
            r5 = r0
            r62 = r4
            r35 = r7
            r4 = r14
            goto L_0x0716
        L_0x0747:
            r23 = r2
            r24 = 0
            r1 = r81
            int r26 = (r1 > r24 ? 1 : (r1 == r24 ? 0 : -1))
            if (r26 <= 0) goto L_0x076b
            r24 = r4
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x0760, all -> 0x1315 }
            r25 = r5
            r5 = 0
            r4.seekTo(r1, r5)     // Catch:{ Exception -> 0x0760, all -> 0x1315 }
            r4 = r95
            r5 = 0
            goto L_0x0779
        L_0x0760:
            r0 = move-exception
            r54 = r83
            r5 = r0
            r9 = r1
        L_0x0765:
            r35 = r7
            r4 = r14
            r62 = r24
            goto L_0x0716
        L_0x076b:
            r24 = r4
            r25 = r5
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x120e, all -> 0x1315 }
            r1 = 0
            r5 = 0
            r4.seekTo(r5, r1)     // Catch:{ Exception -> 0x1205, all -> 0x1315 }
        L_0x0777:
            r4 = r95
        L_0x0779:
            if (r4 == 0) goto L_0x0799
            r1 = 90
            r2 = r74
            if (r2 == r1) goto L_0x078b
            r1 = 270(0x10e, float:3.78E-43)
            if (r2 != r1) goto L_0x0786
            goto L_0x078b
        L_0x0786:
            int r1 = r4.transformWidth     // Catch:{ Exception -> 0x0792, all -> 0x1315 }
            int r5 = r4.transformHeight     // Catch:{ Exception -> 0x0792, all -> 0x1315 }
            goto L_0x078f
        L_0x078b:
            int r1 = r4.transformHeight     // Catch:{ Exception -> 0x0792, all -> 0x1315 }
            int r5 = r4.transformWidth     // Catch:{ Exception -> 0x0792, all -> 0x1315 }
        L_0x078f:
            r6 = r5
            r5 = r1
            goto L_0x079f
        L_0x0792:
            r0 = move-exception
            r9 = r81
            r54 = r83
            r5 = r0
            goto L_0x0765
        L_0x0799:
            r2 = r74
            r5 = r76
            r6 = r77
        L_0x079f:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1201, all -> 0x1315 }
            if (r1 == 0) goto L_0x07bf
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0792, all -> 0x1315 }
            r1.<init>()     // Catch:{ Exception -> 0x0792, all -> 0x1315 }
            java.lang.String r2 = "create encoder with w = "
            r1.append(r2)     // Catch:{ Exception -> 0x0792, all -> 0x1315 }
            r1.append(r5)     // Catch:{ Exception -> 0x0792, all -> 0x1315 }
            java.lang.String r2 = " h = "
            r1.append(r2)     // Catch:{ Exception -> 0x0792, all -> 0x1315 }
            r1.append(r6)     // Catch:{ Exception -> 0x0792, all -> 0x1315 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0792, all -> 0x1315 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0792, all -> 0x1315 }
        L_0x07bf:
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r13, r5, r6)     // Catch:{ Exception -> 0x1201, all -> 0x1315 }
            java.lang.String r2 = "color-format"
            r4 = 2130708361(0x7var_, float:1.701803E38)
            r1.setInteger(r2, r4)     // Catch:{ Exception -> 0x1201, all -> 0x1315 }
            java.lang.String r2 = "bitrate"
            r1.setInteger(r2, r3)     // Catch:{ Exception -> 0x1201, all -> 0x1315 }
            java.lang.String r2 = "frame-rate"
            r1.setInteger(r2, r14)     // Catch:{ Exception -> 0x1201, all -> 0x1315 }
            java.lang.String r2 = "i-frame-interval"
            r4 = 2
            r1.setInteger(r2, r4)     // Catch:{ Exception -> 0x1201, all -> 0x1315 }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1201, all -> 0x1315 }
            r4 = 23
            if (r2 >= r4) goto L_0x0801
            int r2 = java.lang.Math.min(r6, r5)     // Catch:{ Exception -> 0x0792, all -> 0x1315 }
            r4 = 480(0x1e0, float:6.73E-43)
            if (r2 > r4) goto L_0x0801
            r2 = 921600(0xe1000, float:1.291437E-39)
            if (r3 <= r2) goto L_0x07ef
            goto L_0x07f0
        L_0x07ef:
            r2 = r3
        L_0x07f0:
            java.lang.String r3 = "bitrate"
            r1.setInteger(r3, r2)     // Catch:{ Exception -> 0x07f8, all -> 0x1315 }
            r18 = r2
            goto L_0x0803
        L_0x07f8:
            r0 = move-exception
            r9 = r81
            r54 = r83
            r5 = r0
            r3 = r2
            goto L_0x0765
        L_0x0801:
            r18 = r3
        L_0x0803:
            android.media.MediaCodec r4 = android.media.MediaCodec.createEncoderByType(r13)     // Catch:{ Exception -> 0x11f0, all -> 0x1315 }
            r2 = 1
            r3 = 0
            r4.configure(r1, r3, r3, r2)     // Catch:{ Exception -> 0x11dc, all -> 0x1315 }
            org.telegram.messenger.video.InputSurface r1 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x11dc, all -> 0x1315 }
            android.view.Surface r2 = r4.createInputSurface()     // Catch:{ Exception -> 0x11dc, all -> 0x1315 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x11dc, all -> 0x1315 }
            r1.makeCurrent()     // Catch:{ Exception -> 0x11bf, all -> 0x1315 }
            r4.start()     // Catch:{ Exception -> 0x11bf, all -> 0x1315 }
            java.lang.String r2 = r9.getString(r11)     // Catch:{ Exception -> 0x11bf, all -> 0x1315 }
            android.media.MediaCodec r2 = android.media.MediaCodec.createDecoderByType(r2)     // Catch:{ Exception -> 0x11bf, all -> 0x1315 }
            org.telegram.messenger.video.OutputSurface r27 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x11a7, all -> 0x1315 }
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
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x1198, all -> 0x1315 }
            android.view.Surface r1 = r27.getSurface()     // Catch:{ Exception -> 0x1182, all -> 0x1315 }
            r3 = r61
            r2 = r67
            r4 = 0
            r5 = 0
            r3.configure(r2, r1, r4, r5)     // Catch:{ Exception -> 0x1177, all -> 0x1315 }
            r3.start()     // Catch:{ Exception -> 0x1177, all -> 0x1315 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x1177, all -> 0x1315 }
            r2 = 21
            if (r1 >= r2) goto L_0x08a5
            java.nio.ByteBuffer[] r6 = r3.getInputBuffers()     // Catch:{ Exception -> 0x0890, all -> 0x1315 }
            java.nio.ByteBuffer[] r1 = r79.getOutputBuffers()     // Catch:{ Exception -> 0x0890, all -> 0x1315 }
            r2 = r60
            r69 = r6
            r6 = r1
            r1 = r69
            goto L_0x08a9
        L_0x0890:
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
            goto L_0x1233
        L_0x08a5:
            r1 = r4
            r6 = r1
            r2 = r60
        L_0x08a9:
            if (r2 < 0) goto L_0x09a1
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x098a, all -> 0x1315 }
            android.media.MediaFormat r5 = r5.getTrackFormat(r2)     // Catch:{ Exception -> 0x098a, all -> 0x1315 }
            java.lang.String r7 = r5.getString(r13)     // Catch:{ Exception -> 0x098a, all -> 0x1315 }
            java.lang.String r8 = "audio/mp4a-latm"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x098a, all -> 0x1315 }
            if (r7 != 0) goto L_0x08cc
            java.lang.String r7 = r5.getString(r13)     // Catch:{ Exception -> 0x0890, all -> 0x1315 }
            java.lang.String r8 = "audio/mpeg"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x0890, all -> 0x1315 }
            if (r7 == 0) goto L_0x08ca
            goto L_0x08cc
        L_0x08ca:
            r7 = 0
            goto L_0x08cd
        L_0x08cc:
            r7 = 1
        L_0x08cd:
            java.lang.String r8 = r5.getString(r13)     // Catch:{ Exception -> 0x098a, all -> 0x1315 }
            java.lang.String r9 = "audio/unknown"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x098a, all -> 0x1315 }
            if (r8 == 0) goto L_0x08da
            r2 = -1
        L_0x08da:
            if (r2 < 0) goto L_0x0981
            if (r7 == 0) goto L_0x0920
            org.telegram.messenger.video.MP4Builder r8 = r15.mediaMuxer     // Catch:{ Exception -> 0x0913, all -> 0x1315 }
            r9 = 1
            int r8 = r8.addTrack(r5, r9)     // Catch:{ Exception -> 0x0913, all -> 0x1315 }
            android.media.MediaExtractor r10 = r15.extractor     // Catch:{ Exception -> 0x0913, all -> 0x1315 }
            r10.selectTrack(r2)     // Catch:{ Exception -> 0x0913, all -> 0x1315 }
            java.lang.String r10 = "max-input-size"
            int r5 = r5.getInteger(r10)     // Catch:{ Exception -> 0x0913, all -> 0x1315 }
            java.nio.ByteBuffer r5 = java.nio.ByteBuffer.allocateDirect(r5)     // Catch:{ Exception -> 0x0913, all -> 0x1315 }
            r10 = r81
            r85 = r5
            r4 = 0
            int r13 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r13 <= 0) goto L_0x0905
            android.media.MediaExtractor r13 = r15.extractor     // Catch:{ Exception -> 0x0939, all -> 0x1315 }
            r9 = 0
            r13.seekTo(r10, r9)     // Catch:{ Exception -> 0x0939, all -> 0x1315 }
            goto L_0x090b
        L_0x0905:
            android.media.MediaExtractor r9 = r15.extractor     // Catch:{ Exception -> 0x0939, all -> 0x1315 }
            r13 = 0
            r9.seekTo(r4, r13)     // Catch:{ Exception -> 0x0939, all -> 0x1315 }
        L_0x090b:
            r5 = r85
            r4 = r8
            r13 = 0
            r8 = r83
            goto L_0x09a9
        L_0x0913:
            r0 = move-exception
            r4 = r78
            r6 = r79
            r9 = r81
            r54 = r83
            r5 = r0
            r14 = r3
            goto L_0x1191
        L_0x0920:
            r10 = r81
            r8 = r5
            r4 = 0
            android.media.MediaExtractor r9 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0973, all -> 0x1315 }
            r9.<init>()     // Catch:{ Exception -> 0x0973, all -> 0x1315 }
            r9.setDataSource(r12)     // Catch:{ Exception -> 0x0973, all -> 0x1315 }
            r9.selectTrack(r2)     // Catch:{ Exception -> 0x0973, all -> 0x1315 }
            int r13 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r13 <= 0) goto L_0x0943
            r13 = 0
            r9.seekTo(r10, r13)     // Catch:{ Exception -> 0x0939, all -> 0x1315 }
            goto L_0x0947
        L_0x0939:
            r0 = move-exception
            r4 = r78
            r6 = r79
            r54 = r83
            r5 = r0
            r14 = r3
            goto L_0x097e
        L_0x0943:
            r13 = 0
            r9.seekTo(r4, r13)     // Catch:{ Exception -> 0x0973, all -> 0x1315 }
        L_0x0947:
            org.telegram.messenger.video.AudioRecoder r13 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x0973, all -> 0x1315 }
            r13.<init>(r8, r9, r2)     // Catch:{ Exception -> 0x0973, all -> 0x1315 }
            r13.startTime = r10     // Catch:{ Exception -> 0x0963, all -> 0x1315 }
            r8 = r83
            r13.endTime = r8     // Catch:{ Exception -> 0x0961, all -> 0x1315 }
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x0961, all -> 0x1315 }
            android.media.MediaFormat r5 = r13.format     // Catch:{ Exception -> 0x0961, all -> 0x1315 }
            r85 = r2
            r2 = 1
            int r4 = r4.addTrack(r5, r2)     // Catch:{ Exception -> 0x0961, all -> 0x1315 }
            r2 = r85
            r5 = 0
            goto L_0x09a9
        L_0x0961:
            r0 = move-exception
            goto L_0x0966
        L_0x0963:
            r0 = move-exception
            r8 = r83
        L_0x0966:
            r4 = r78
            r6 = r79
            r5 = r0
            r14 = r3
            r54 = r8
            r9 = r10
            r40 = r13
            goto L_0x116f
        L_0x0973:
            r0 = move-exception
            r8 = r83
            r4 = r78
            r6 = r79
            r5 = r0
            r14 = r3
            r54 = r8
        L_0x097e:
            r9 = r10
            goto L_0x1191
        L_0x0981:
            r10 = r81
            r8 = r83
            r85 = r2
            r4 = -5
            r5 = 0
            goto L_0x09a8
        L_0x098a:
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
            goto L_0x1233
        L_0x09a1:
            r10 = r81
            r8 = r83
            r4 = -5
            r5 = 0
            r7 = 1
        L_0x09a8:
            r13 = 0
        L_0x09a9:
            if (r2 >= 0) goto L_0x09ae
            r23 = 1
            goto L_0x09b0
        L_0x09ae:
            r23 = 0
        L_0x09b0:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x115e, all -> 0x1315 }
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
        L_0x09cc:
            if (r23 == 0) goto L_0x09e5
            if (r7 != 0) goto L_0x09d3
            if (r37 != 0) goto L_0x09d3
            goto L_0x09e5
        L_0x09d3:
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
            goto L_0x1275
        L_0x09e5:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x1144, all -> 0x128f }
            if (r7 != 0) goto L_0x0a07
            if (r13 == 0) goto L_0x0a07
            org.telegram.messenger.video.MP4Builder r12 = r15.mediaMuxer     // Catch:{ Exception -> 0x09f5, all -> 0x128f }
            boolean r12 = r13.step(r12, r4)     // Catch:{ Exception -> 0x09f5, all -> 0x128f }
            r37 = r12
            goto L_0x0a07
        L_0x09f5:
            r0 = move-exception
            r4 = r78
            r6 = r79
            r5 = r0
            r14 = r3
            r54 = r8
            r9 = r10
            r40 = r13
            r2 = r17
        L_0x0a03:
            r3 = r18
            goto L_0x115b
        L_0x0a07:
            if (r6 != 0) goto L_0x0b9a
            android.media.MediaExtractor r12 = r15.extractor     // Catch:{ Exception -> 0x0b8a, all -> 0x128f }
            int r12 = r12.getSampleTrackIndex()     // Catch:{ Exception -> 0x0b8a, all -> 0x128f }
            r83 = r6
            r6 = r62
            if (r12 != r6) goto L_0x0a95
            r85 = r13
            r48 = r14
            r13 = 2500(0x9c4, double:1.235E-320)
            int r12 = r3.dequeueInputBuffer(r13)     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            if (r12 < 0) goto L_0x0a72
            int r13 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            r14 = 21
            if (r13 >= r14) goto L_0x0a2a
            r13 = r1[r12]     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            goto L_0x0a2e
        L_0x0a2a:
            java.nio.ByteBuffer r13 = r3.getInputBuffer(r12)     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
        L_0x0a2e:
            android.media.MediaExtractor r14 = r15.extractor     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            r86 = r1
            r1 = 0
            int r55 = r14.readSampleData(r13, r1)     // Catch:{ Exception -> 0x0a61, all -> 0x128f }
            if (r55 >= 0) goto L_0x0a4a
            r54 = 0
            r55 = 0
            r56 = 0
            r58 = 4
            r52 = r3
            r53 = r12
            r52.queueInputBuffer(r53, r54, r55, r56, r58)     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            r1 = 1
            goto L_0x0a76
        L_0x0a4a:
            r54 = 0
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            long r56 = r1.getSampleTime()     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            r58 = 0
            r52 = r3
            r53 = r12
            r52.queueInputBuffer(r53, r54, r55, r56, r58)     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            r1.advance()     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            goto L_0x0a74
        L_0x0a61:
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
            goto L_0x0a91
        L_0x0a72:
            r86 = r1
        L_0x0a74:
            r1 = r83
        L_0x0a76:
            r14 = r2
            r50 = r7
            r60 = r8
            r13 = r68
            r2 = r1
            goto L_0x0b5c
        L_0x0a80:
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
        L_0x0a90:
            r1 = 0
        L_0x0a91:
            r6 = r79
            goto L_0x1233
        L_0x0a95:
            r86 = r1
            r85 = r13
            r48 = r14
            if (r7 == 0) goto L_0x0b4c
            r1 = -1
            if (r2 == r1) goto L_0x0b44
            if (r12 != r2) goto L_0x0b4c
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0b40, all -> 0x128f }
            r12 = 0
            int r1 = r1.readSampleData(r5, r12)     // Catch:{ Exception -> 0x0b40, all -> 0x128f }
            r13 = r68
            r13.size = r1     // Catch:{ Exception -> 0x0b40, all -> 0x128f }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b40, all -> 0x128f }
            r14 = 21
            if (r1 >= r14) goto L_0x0abb
            r5.position(r12)     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            int r1 = r13.size     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            r5.limit(r1)     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
        L_0x0abb:
            int r1 = r13.size     // Catch:{ Exception -> 0x0b40, all -> 0x128f }
            if (r1 < 0) goto L_0x0ad0
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            r14 = r2
            long r1 = r1.getSampleTime()     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            r13.presentationTimeUs = r1     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            r1.advance()     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            r1 = r83
            goto L_0x0ad5
        L_0x0ad0:
            r14 = r2
            r1 = 0
            r13.size = r1     // Catch:{ Exception -> 0x0b2c, all -> 0x128f }
            r1 = 1
        L_0x0ad5:
            int r2 = r13.size     // Catch:{ Exception -> 0x0b40, all -> 0x128f }
            if (r2 <= 0) goto L_0x0b25
            r41 = 0
            int r2 = (r8 > r41 ? 1 : (r8 == r41 ? 0 : -1))
            if (r2 < 0) goto L_0x0ae8
            r83 = r1
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0a80, all -> 0x128f }
            int r12 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r12 >= 0) goto L_0x0b27
            goto L_0x0aea
        L_0x0ae8:
            r83 = r1
        L_0x0aea:
            r1 = 0
            r13.offset = r1     // Catch:{ Exception -> 0x0b2c, all -> 0x128f }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0b2c, all -> 0x128f }
            int r2 = r2.getSampleFlags()     // Catch:{ Exception -> 0x0b2c, all -> 0x128f }
            r13.flags = r2     // Catch:{ Exception -> 0x0b2c, all -> 0x128f }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0b2c, all -> 0x128f }
            r50 = r7
            r60 = r8
            long r7 = r2.writeSampleData(r4, r5, r13, r1)     // Catch:{ Exception -> 0x0b23, all -> 0x128f }
            r1 = 0
            int r9 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
            if (r9 == 0) goto L_0x0b5a
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r15.callback     // Catch:{ Exception -> 0x0b76, all -> 0x128f }
            if (r1 == 0) goto L_0x0b5a
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0b76, all -> 0x128f }
            long r1 = r1 - r10
            int r9 = (r1 > r31 ? 1 : (r1 == r31 ? 0 : -1))
            if (r9 <= 0) goto L_0x0b14
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0b76, all -> 0x128f }
            long r31 = r1 - r10
        L_0x0b14:
            r1 = r31
            org.telegram.messenger.MediaController$VideoConvertorListener r9 = r15.callback     // Catch:{ Exception -> 0x0b76, all -> 0x128f }
            float r12 = (float) r1     // Catch:{ Exception -> 0x0b76, all -> 0x128f }
            float r12 = r12 / r19
            float r12 = r12 / r20
            r9.didWriteData(r7, r12)     // Catch:{ Exception -> 0x0b76, all -> 0x128f }
            r31 = r1
            goto L_0x0b5a
        L_0x0b23:
            r0 = move-exception
            goto L_0x0b2f
        L_0x0b25:
            r83 = r1
        L_0x0b27:
            r50 = r7
            r60 = r8
            goto L_0x0b5a
        L_0x0b2c:
            r0 = move-exception
            r60 = r8
        L_0x0b2f:
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
        L_0x0b40:
            r0 = move-exception
            r60 = r8
            goto L_0x0b77
        L_0x0b44:
            r14 = r2
            r50 = r7
            r60 = r8
            r13 = r68
            goto L_0x0b54
        L_0x0b4c:
            r14 = r2
            r50 = r7
            r60 = r8
            r13 = r68
            r1 = -1
        L_0x0b54:
            if (r12 != r1) goto L_0x0b5a
            r2 = r83
            r1 = 1
            goto L_0x0b5d
        L_0x0b5a:
            r2 = r83
        L_0x0b5c:
            r1 = 0
        L_0x0b5d:
            if (r1 == 0) goto L_0x0b88
            r7 = 2500(0x9c4, double:1.235E-320)
            int r53 = r3.dequeueInputBuffer(r7)     // Catch:{ Exception -> 0x0b76, all -> 0x128f }
            if (r53 < 0) goto L_0x0b88
            r54 = 0
            r55 = 0
            r56 = 0
            r58 = 4
            r52 = r3
            r52.queueInputBuffer(r53, r54, r55, r56, r58)     // Catch:{ Exception -> 0x0b76, all -> 0x128f }
            r1 = 1
            goto L_0x0bad
        L_0x0b76:
            r0 = move-exception
        L_0x0b77:
            r4 = r78
            r40 = r85
            r5 = r0
            r14 = r3
            r62 = r6
            r9 = r10
            r2 = r17
            r3 = r18
            r54 = r60
            goto L_0x0a90
        L_0x0b88:
            r1 = r2
            goto L_0x0bad
        L_0x0b8a:
            r0 = move-exception
            r60 = r8
            r85 = r13
            r4 = r78
            r6 = r79
            r40 = r85
            r5 = r0
            r14 = r3
            r9 = r10
            goto L_0x1155
        L_0x0b9a:
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
        L_0x0bad:
            r2 = r28 ^ 1
            r7 = r1
            r12 = r17
            r8 = r60
            r1 = 1
            r69 = r44
            r44 = r4
            r45 = r5
            r4 = r69
        L_0x0bbd:
            if (r2 != 0) goto L_0x0bde
            if (r1 == 0) goto L_0x0bc2
            goto L_0x0bde
        L_0x0bc2:
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
            goto L_0x09cc
        L_0x0bde:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x1130, all -> 0x112b }
            if (r90 == 0) goto L_0x0bec
            r52 = 22000(0x55f0, double:1.08694E-319)
            r83 = r1
            r84 = r2
            r1 = r52
            goto L_0x0bf2
        L_0x0bec:
            r83 = r1
            r84 = r2
            r1 = 2500(0x9c4, double:1.235E-320)
        L_0x0bf2:
            r69 = r7
            r7 = r79
            r79 = r69
            int r1 = r7.dequeueOutputBuffer(r13, r1)     // Catch:{ Exception -> 0x1127, all -> 0x112b }
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
            goto L_0x0e48
        L_0x0CLASSNAME:
            r2 = -3
            if (r1 != r2) goto L_0x0c4a
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c3b, all -> 0x112b }
            r51 = r14
            r14 = 21
            if (r2 >= r14) goto L_0x0CLASSNAME
            java.nio.ByteBuffer[] r29 = r7.getOutputBuffers()     // Catch:{ Exception -> 0x0c3b, all -> 0x112b }
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
        L_0x0c3b:
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
            goto L_0x1141
        L_0x0c4a:
            r51 = r14
            r2 = -2
            if (r1 != r2) goto L_0x0cb5
            android.media.MediaFormat r2 = r7.getOutputFormat()     // Catch:{ Exception -> 0x0cab, all -> 0x112b }
            r14 = -5
            if (r12 != r14) goto L_0x0CLASSNAME
            if (r2 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.video.MP4Builder r14 = r15.mediaMuxer     // Catch:{ Exception -> 0x0cab, all -> 0x112b }
            r62 = r6
            r6 = 0
            int r12 = r14.addTrack(r2, r6)     // Catch:{ Exception -> 0x0cc8, all -> 0x112b }
            r6 = r66
            boolean r14 = r2.containsKey(r6)     // Catch:{ Exception -> 0x0cc8, all -> 0x112b }
            if (r14 == 0) goto L_0x0c8f
            int r14 = r2.getInteger(r6)     // Catch:{ Exception -> 0x0cc8, all -> 0x112b }
            r66 = r6
            r6 = 1
            if (r14 != r6) goto L_0x0c8c
            r6 = r65
            java.nio.ByteBuffer r14 = r2.getByteBuffer(r6)     // Catch:{ Exception -> 0x0cc8, all -> 0x112b }
            r52 = r4
            r4 = r48
            java.nio.ByteBuffer r2 = r2.getByteBuffer(r4)     // Catch:{ Exception -> 0x0cc8, all -> 0x112b }
            int r5 = r14.limit()     // Catch:{ Exception -> 0x0cc8, all -> 0x112b }
            int r2 = r2.limit()     // Catch:{ Exception -> 0x0cc8, all -> 0x112b }
            int r5 = r5 + r2
            r30 = r5
            goto L_0x0c9d
        L_0x0c8c:
            r52 = r4
            goto L_0x0CLASSNAME
        L_0x0c8f:
            r52 = r4
            r66 = r6
        L_0x0CLASSNAME:
            r4 = r48
            r6 = r65
            goto L_0x0c9d
        L_0x0CLASSNAME:
            r52 = r4
            r62 = r6
            goto L_0x0CLASSNAME
        L_0x0c9d:
            r2 = r83
            r61 = r3
            r54 = r8
            r8 = r49
            r3 = r63
            r5 = r64
            goto L_0x0CLASSNAME
        L_0x0cab:
            r0 = move-exception
            r62 = r6
        L_0x0cae:
            r4 = r78
            r40 = r85
            r5 = r0
            r14 = r3
            goto L_0x0CLASSNAME
        L_0x0cb5:
            r52 = r4
            r62 = r6
            r4 = r48
            r6 = r65
            if (r1 < 0) goto L_0x1101
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x10fc, all -> 0x112b }
            r5 = 21
            if (r2 >= r5) goto L_0x0cca
            r2 = r29[r1]     // Catch:{ Exception -> 0x0cc8, all -> 0x112b }
            goto L_0x0cce
        L_0x0cc8:
            r0 = move-exception
            goto L_0x0cae
        L_0x0cca:
            java.nio.ByteBuffer r2 = r7.getOutputBuffer(r1)     // Catch:{ Exception -> 0x10fc, all -> 0x112b }
        L_0x0cce:
            if (r2 == 0) goto L_0x10d8
            int r14 = r13.size     // Catch:{ Exception -> 0x10fc, all -> 0x112b }
            r5 = 1
            if (r14 <= r5) goto L_0x0e2a
            int r14 = r13.flags     // Catch:{ Exception -> 0x0e17, all -> 0x112b }
            r14 = r14 & 2
            if (r14 != 0) goto L_0x0d9b
            if (r30 == 0) goto L_0x0cee
            int r14 = r13.flags     // Catch:{ Exception -> 0x0cc8, all -> 0x112b }
            r14 = r14 & r5
            if (r14 == 0) goto L_0x0cee
            int r5 = r13.offset     // Catch:{ Exception -> 0x0cc8, all -> 0x112b }
            int r5 = r5 + r30
            r13.offset = r5     // Catch:{ Exception -> 0x0cc8, all -> 0x112b }
            int r5 = r13.size     // Catch:{ Exception -> 0x0cc8, all -> 0x112b }
            int r5 = r5 - r30
            r13.size = r5     // Catch:{ Exception -> 0x0cc8, all -> 0x112b }
        L_0x0cee:
            if (r16 == 0) goto L_0x0d4f
            int r5 = r13.flags     // Catch:{ Exception -> 0x0d43, all -> 0x112b }
            r14 = 1
            r5 = r5 & r14
            if (r5 == 0) goto L_0x0d4f
            int r5 = r13.size     // Catch:{ Exception -> 0x0d43, all -> 0x112b }
            r14 = 100
            if (r5 <= r14) goto L_0x0d3e
            int r5 = r13.offset     // Catch:{ Exception -> 0x0d43, all -> 0x112b }
            r2.position(r5)     // Catch:{ Exception -> 0x0d43, all -> 0x112b }
            byte[] r5 = new byte[r14]     // Catch:{ Exception -> 0x0d43, all -> 0x112b }
            r2.get(r5)     // Catch:{ Exception -> 0x0d43, all -> 0x112b }
            r14 = 0
            r16 = 0
        L_0x0d09:
            r54 = r8
            r8 = 96
            if (r14 >= r8) goto L_0x0d40
            byte r8 = r5[r14]     // Catch:{ Exception -> 0x0d3c, all -> 0x112b }
            if (r8 != 0) goto L_0x0d37
            int r8 = r14 + 1
            byte r8 = r5[r8]     // Catch:{ Exception -> 0x0d3c, all -> 0x112b }
            if (r8 != 0) goto L_0x0d37
            int r8 = r14 + 2
            byte r8 = r5[r8]     // Catch:{ Exception -> 0x0d3c, all -> 0x112b }
            if (r8 != 0) goto L_0x0d37
            int r8 = r14 + 3
            byte r8 = r5[r8]     // Catch:{ Exception -> 0x0d3c, all -> 0x112b }
            r9 = 1
            if (r8 != r9) goto L_0x0d37
            int r8 = r16 + 1
            if (r8 <= r9) goto L_0x0d35
            int r5 = r13.offset     // Catch:{ Exception -> 0x0d3c, all -> 0x112b }
            int r5 = r5 + r14
            r13.offset = r5     // Catch:{ Exception -> 0x0d3c, all -> 0x112b }
            int r5 = r13.size     // Catch:{ Exception -> 0x0d3c, all -> 0x112b }
            int r5 = r5 - r14
            r13.size = r5     // Catch:{ Exception -> 0x0d3c, all -> 0x112b }
            goto L_0x0d40
        L_0x0d35:
            r16 = r8
        L_0x0d37:
            int r14 = r14 + 1
            r8 = r54
            goto L_0x0d09
        L_0x0d3c:
            r0 = move-exception
            goto L_0x0d46
        L_0x0d3e:
            r54 = r8
        L_0x0d40:
            r16 = 0
            goto L_0x0d51
        L_0x0d43:
            r0 = move-exception
            r54 = r8
        L_0x0d46:
            r4 = r78
            r40 = r85
            r5 = r0
            r14 = r3
        L_0x0d4c:
            r6 = r7
            goto L_0x0CLASSNAME
        L_0x0d4f:
            r54 = r8
        L_0x0d51:
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x0d96, all -> 0x112b }
            r14 = r3
            r8 = 1
            long r2 = r5.writeSampleData(r12, r2, r13, r8)     // Catch:{ Exception -> 0x0d8d, all -> 0x112b }
            r8 = 0
            int r5 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r5 == 0) goto L_0x0d82
            org.telegram.messenger.MediaController$VideoConvertorListener r5 = r15.callback     // Catch:{ Exception -> 0x0d8d, all -> 0x112b }
            if (r5 == 0) goto L_0x0d82
            long r8 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0d8d, all -> 0x112b }
            long r8 = r8 - r10
            int r5 = (r8 > r31 ? 1 : (r8 == r31 ? 0 : -1))
            if (r5 <= 0) goto L_0x0d71
            long r8 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0d6f, all -> 0x112b }
            long r31 = r8 - r10
            goto L_0x0d71
        L_0x0d6f:
            r0 = move-exception
            goto L_0x0d90
        L_0x0d71:
            r8 = r31
            org.telegram.messenger.MediaController$VideoConvertorListener r5 = r15.callback     // Catch:{ Exception -> 0x0d8d, all -> 0x112b }
            r61 = r14
            float r14 = (float) r8
            float r14 = r14 / r19
            float r14 = r14 / r20
            r5.didWriteData(r2, r14)     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            r31 = r8
            goto L_0x0d84
        L_0x0d82:
            r61 = r14
        L_0x0d84:
            r2 = r12
            r8 = r49
            r3 = r63
            r5 = r64
            goto L_0x0e35
        L_0x0d8d:
            r0 = move-exception
            r61 = r14
        L_0x0d90:
            r4 = r78
            r40 = r85
            r5 = r0
            goto L_0x0d4c
        L_0x0d96:
            r0 = move-exception
            r61 = r3
            goto L_0x0e1c
        L_0x0d9b:
            r61 = r3
            r54 = r8
            r3 = -5
            if (r12 != r3) goto L_0x0e2e
            int r3 = r13.size     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            byte[] r3 = new byte[r3]     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            int r5 = r13.offset     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            int r8 = r13.size     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            int r5 = r5 + r8
            r2.limit(r5)     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            int r5 = r13.offset     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            r2.position(r5)     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            r2.get(r3)     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            int r2 = r13.size     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            r5 = 1
            int r2 = r2 - r5
        L_0x0dba:
            if (r2 < 0) goto L_0x0df7
            r8 = 3
            if (r2 <= r8) goto L_0x0df7
            byte r9 = r3[r2]     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            if (r9 != r5) goto L_0x0df3
            int r9 = r2 + -1
            byte r9 = r3[r9]     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            if (r9 != 0) goto L_0x0df3
            int r9 = r2 + -2
            byte r9 = r3[r9]     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            if (r9 != 0) goto L_0x0df3
            int r9 = r2 + -3
            byte r14 = r3[r9]     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            if (r14 != 0) goto L_0x0df3
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r9)     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            int r14 = r13.size     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            int r14 = r14 - r9
            java.nio.ByteBuffer r14 = java.nio.ByteBuffer.allocate(r14)     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            r5 = 0
            java.nio.ByteBuffer r8 = r2.put(r3, r5, r9)     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            r8.position(r5)     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            int r8 = r13.size     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            int r8 = r8 - r9
            java.nio.ByteBuffer r3 = r14.put(r3, r9, r8)     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            r3.position(r5)     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            goto L_0x0df9
        L_0x0df3:
            int r2 = r2 + -1
            r5 = 1
            goto L_0x0dba
        L_0x0df7:
            r2 = 0
            r14 = 0
        L_0x0df9:
            r8 = r49
            r3 = r63
            r5 = r64
            android.media.MediaFormat r9 = android.media.MediaFormat.createVideoFormat(r8, r3, r5)     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            if (r2 == 0) goto L_0x0e0d
            if (r14 == 0) goto L_0x0e0d
            r9.setByteBuffer(r6, r2)     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            r9.setByteBuffer(r4, r14)     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
        L_0x0e0d:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            r14 = 0
            int r2 = r2.addTrack(r9, r14)     // Catch:{ Exception -> 0x0e15, all -> 0x112b }
            goto L_0x0e35
        L_0x0e15:
            r0 = move-exception
            goto L_0x0e1c
        L_0x0e17:
            r0 = move-exception
            r61 = r3
            r54 = r8
        L_0x0e1c:
            r4 = r78
            r40 = r85
            r5 = r0
            r6 = r7
            r9 = r10
            r2 = r12
            r3 = r18
            r14 = r61
            goto L_0x115b
        L_0x0e2a:
            r61 = r3
            r54 = r8
        L_0x0e2e:
            r8 = r49
            r3 = r63
            r5 = r64
            r2 = r12
        L_0x0e35:
            int r9 = r13.flags     // Catch:{ Exception -> 0x10cf, all -> 0x05f1 }
            r9 = r9 & 4
            if (r9 == 0) goto L_0x0e3d
            r9 = 1
            goto L_0x0e3e
        L_0x0e3d:
            r9 = 0
        L_0x0e3e:
            r12 = 0
            r7.releaseOutputBuffer(r1, r12)     // Catch:{ Exception -> 0x10cf, all -> 0x05f1 }
            r12 = r2
            r23 = r9
            r9 = -1
            r2 = r83
        L_0x0e48:
            if (r1 == r9) goto L_0x0e69
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
            goto L_0x0bbd
        L_0x0e69:
            if (r28 != 0) goto L_0x109a
            r14 = r61
            r9 = 2500(0x9c4, double:1.235E-320)
            int r1 = r14.dequeueOutputBuffer(r13, r9)     // Catch:{ Exception -> 0x1089, all -> 0x1082 }
            r11 = -1
            if (r1 != r11) goto L_0x0e93
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
            goto L_0x10b7
        L_0x0e93:
            r9 = -3
            if (r1 != r9) goto L_0x0eaa
        L_0x0e96:
            r9 = r81
            r17 = r2
            r63 = r3
            r57 = r4
            r64 = r5
            r65 = r6
            r83 = r12
            r11 = r52
            r3 = r59
            goto L_0x10ad
        L_0x0eaa:
            r9 = -2
            if (r1 != r9) goto L_0x0ed1
            android.media.MediaFormat r1 = r14.getOutputFormat()     // Catch:{ Exception -> 0x0eca, all -> 0x112b }
            boolean r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0eca, all -> 0x112b }
            if (r9 == 0) goto L_0x0e96
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0eca, all -> 0x112b }
            r9.<init>()     // Catch:{ Exception -> 0x0eca, all -> 0x112b }
            java.lang.String r10 = "newFormat = "
            r9.append(r10)     // Catch:{ Exception -> 0x0eca, all -> 0x112b }
            r9.append(r1)     // Catch:{ Exception -> 0x0eca, all -> 0x112b }
            java.lang.String r1 = r9.toString()     // Catch:{ Exception -> 0x0eca, all -> 0x112b }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0eca, all -> 0x112b }
            goto L_0x0e96
        L_0x0eca:
            r0 = move-exception
            r4 = r78
            r9 = r81
            goto L_0x113d
        L_0x0ed1:
            if (r1 < 0) goto L_0x1058
            int r9 = r13.size     // Catch:{ Exception -> 0x1089, all -> 0x1082 }
            r83 = r12
            if (r9 == 0) goto L_0x0edb
            r9 = 1
            goto L_0x0edc
        L_0x0edb:
            r9 = 0
        L_0x0edc:
            long r11 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1052, all -> 0x1077 }
            r41 = 0
            int r10 = (r54 > r41 ? 1 : (r54 == r41 ? 0 : -1))
            if (r10 <= 0) goto L_0x0ef5
            int r10 = (r11 > r54 ? 1 : (r11 == r54 ? 0 : -1))
            if (r10 < 0) goto L_0x0ef5
            int r9 = r13.flags     // Catch:{ Exception -> 0x0f7a, all -> 0x1077 }
            r9 = r9 | 4
            r13.flags = r9     // Catch:{ Exception -> 0x0f7a, all -> 0x1077 }
            r9 = 1
            r10 = 0
            r28 = 1
            r41 = 0
            goto L_0x0efa
        L_0x0ef5:
            r10 = r9
            r41 = 0
            r9 = r79
        L_0x0efa:
            int r17 = (r35 > r41 ? 1 : (r35 == r41 ? 0 : -1))
            if (r17 < 0) goto L_0x0var_
            r17 = r2
            int r2 = r13.flags     // Catch:{ Exception -> 0x0f7a, all -> 0x1077 }
            r2 = r2 & 4
            if (r2 == 0) goto L_0x0var_
            r79 = r9
            r2 = r10
            r38 = 2500(0x9c4, double:1.235E-320)
            r9 = r81
            long r48 = r35 - r9
            long r48 = java.lang.Math.abs(r48)     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            r56 = 1000000(0xvar_, float:1.401298E-39)
            r58 = r2
            r57 = r4
            r4 = r78
            int r2 = r56 / r4
            r63 = r3
            long r2 = (long) r2     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            int r56 = (r48 > r2 ? 1 : (r48 == r2 ? 0 : -1))
            if (r56 <= 0) goto L_0x0f6b
            r2 = 0
            int r28 = (r9 > r2 ? 1 : (r9 == r2 ? 0 : -1))
            if (r28 <= 0) goto L_0x0var_
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            r3 = 0
            r2.seekTo(r9, r3)     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            r64 = r5
            r65 = r6
            r3 = 0
            goto L_0x0var_
        L_0x0var_:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            r64 = r5
            r65 = r6
            r3 = 0
            r5 = 0
            r2.seekTo(r5, r3)     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
        L_0x0var_:
            long r33 = r52 + r24
            int r2 = r13.flags     // Catch:{ Exception -> 0x0f5b, all -> 0x1077 }
            r5 = -5
            r2 = r2 & r5
            r13.flags = r2     // Catch:{ Exception -> 0x0f5b, all -> 0x1077 }
            r14.flush()     // Catch:{ Exception -> 0x0f5b, all -> 0x1077 }
            r54 = r35
            r2 = 1
            r6 = 0
            r28 = 0
            r41 = 0
            r58 = 0
            r35 = r21
            goto L_0x0f9c
        L_0x0f5b:
            r0 = move-exception
            r2 = r83
            r40 = r85
            r5 = r0
            r6 = r7
            r3 = r18
            r54 = r35
            r1 = 0
            r35 = r21
            goto L_0x1233
        L_0x0f6b:
            r64 = r5
            r65 = r6
            r3 = 0
            r5 = -5
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            goto L_0x1092
        L_0x0var_:
            r0 = move-exception
            r4 = r78
            goto L_0x1092
        L_0x0f7a:
            r0 = move-exception
            r4 = r78
            r9 = r81
            goto L_0x1092
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
        L_0x0f9c:
            int r43 = (r35 > r41 ? 1 : (r35 == r41 ? 0 : -1))
            r79 = r6
            if (r43 < 0) goto L_0x0fa5
            r5 = r35
            goto L_0x0fa6
        L_0x0fa5:
            r5 = r9
        L_0x0fa6:
            int r43 = (r5 > r41 ? 1 : (r5 == r41 ? 0 : -1))
            if (r43 <= 0) goto L_0x0fe3
            int r43 = (r46 > r21 ? 1 : (r46 == r21 ? 0 : -1))
            if (r43 != 0) goto L_0x0fe3
            int r43 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1))
            if (r43 >= 0) goto L_0x0fd6
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            if (r11 == 0) goto L_0x0fd4
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            r11.<init>()     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            java.lang.String r12 = "drop frame startTime = "
            r11.append(r12)     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            r11.append(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            java.lang.String r5 = " present time = "
            r11.append(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            long r5 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            r11.append(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            java.lang.String r5 = r11.toString()     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            org.telegram.messenger.FileLog.d(r5)     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
        L_0x0fd4:
            r6 = 0
            goto L_0x0fe5
        L_0x0fd6:
            long r5 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            r11 = -2147483648(0xfffffffvar_, double:NaN)
            int r43 = (r52 > r11 ? 1 : (r52 == r11 ? 0 : -1))
            if (r43 == 0) goto L_0x0fe1
            long r33 = r33 - r5
        L_0x0fe1:
            r46 = r5
        L_0x0fe3:
            r6 = r58
        L_0x0fe5:
            if (r2 == 0) goto L_0x0fea
            r46 = r21
            goto L_0x0ffd
        L_0x0fea:
            int r2 = (r35 > r21 ? 1 : (r35 == r21 ? 0 : -1))
            if (r2 != 0) goto L_0x0ffa
            r11 = 0
            int r2 = (r33 > r11 ? 1 : (r33 == r11 ? 0 : -1))
            if (r2 == 0) goto L_0x0ffa
            long r11 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            long r11 = r11 + r33
            r13.presentationTimeUs = r11     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
        L_0x0ffa:
            r14.releaseOutputBuffer(r1, r6)     // Catch:{ Exception -> 0x1050, all -> 0x1077 }
        L_0x0ffd:
            if (r6 == 0) goto L_0x1033
            r5 = 0
            int r1 = (r35 > r5 ? 1 : (r35 == r5 ? 0 : -1))
            if (r1 < 0) goto L_0x100f
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            r11 = r52
            long r1 = java.lang.Math.max(r11, r1)     // Catch:{ Exception -> 0x0var_, all -> 0x1077 }
            r11 = r1
            goto L_0x1011
        L_0x100f:
            r11 = r52
        L_0x1011:
            r27.awaitNewImage()     // Catch:{ Exception -> 0x1016, all -> 0x1077 }
            r1 = 0
            goto L_0x101c
        L_0x1016:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x1050, all -> 0x1077 }
            r1 = 1
        L_0x101c:
            if (r1 != 0) goto L_0x1030
            r27.drawImage()     // Catch:{ Exception -> 0x1050, all -> 0x1077 }
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1050, all -> 0x1077 }
            r41 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 * r41
            r3 = r59
            r3.setPresentationTime(r1)     // Catch:{ Exception -> 0x1079, all -> 0x1077 }
            r3.swapBuffers()     // Catch:{ Exception -> 0x1079, all -> 0x1077 }
            goto L_0x1039
        L_0x1030:
            r3 = r59
            goto L_0x1039
        L_0x1033:
            r11 = r52
            r3 = r59
            r5 = 0
        L_0x1039:
            int r1 = r13.flags     // Catch:{ Exception -> 0x1079, all -> 0x1077 }
            r1 = r1 & 4
            if (r1 == 0) goto L_0x10b3
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1079, all -> 0x1077 }
            if (r1 == 0) goto L_0x1048
            java.lang.String r1 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x1079, all -> 0x1077 }
        L_0x1048:
            r7.signalEndOfInputStream()     // Catch:{ Exception -> 0x1079, all -> 0x1077 }
            r1 = r79
            r2 = 0
            goto L_0x10b7
        L_0x1050:
            r0 = move-exception
            goto L_0x1090
        L_0x1052:
            r0 = move-exception
            r4 = r78
            r9 = r81
            goto L_0x1090
        L_0x1058:
            r4 = r78
            r9 = r81
            r83 = r12
            r3 = r59
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1079, all -> 0x1077 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1079, all -> 0x1077 }
            r5.<init>()     // Catch:{ Exception -> 0x1079, all -> 0x1077 }
            java.lang.String r6 = "unexpected result from decoder.dequeueOutputBuffer: "
            r5.append(r6)     // Catch:{ Exception -> 0x1079, all -> 0x1077 }
            r5.append(r1)     // Catch:{ Exception -> 0x1079, all -> 0x1077 }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x1079, all -> 0x1077 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x1079, all -> 0x1077 }
            throw r2     // Catch:{ Exception -> 0x1079, all -> 0x1077 }
        L_0x1077:
            r0 = move-exception
            goto L_0x1085
        L_0x1079:
            r0 = move-exception
            r2 = r83
            r40 = r85
            r5 = r0
            r59 = r3
            goto L_0x1097
        L_0x1082:
            r0 = move-exception
            r83 = r12
        L_0x1085:
            r2 = r83
            goto L_0x05f2
        L_0x1089:
            r0 = move-exception
            r4 = r78
            r9 = r81
            r83 = r12
        L_0x1090:
            r3 = r59
        L_0x1092:
            r2 = r83
        L_0x1094:
            r40 = r85
            r5 = r0
        L_0x1097:
            r6 = r7
            goto L_0x0a03
        L_0x109a:
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
        L_0x10ad:
            r5 = 0
            r38 = 2500(0x9c4, double:1.235E-320)
            r4 = r78
        L_0x10b3:
            r1 = r79
            r2 = r84
        L_0x10b7:
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
            goto L_0x0bbd
        L_0x10cf:
            r0 = move-exception
            r4 = r78
            r9 = r10
            r3 = r59
            r14 = r61
            goto L_0x1094
        L_0x10d8:
            r4 = r78
            r14 = r3
            r54 = r8
            r9 = r10
            r3 = r59
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1120, all -> 0x112b }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1120, all -> 0x112b }
            r5.<init>()     // Catch:{ Exception -> 0x1120, all -> 0x112b }
            java.lang.String r6 = "encoderOutputBuffer "
            r5.append(r6)     // Catch:{ Exception -> 0x1120, all -> 0x112b }
            r5.append(r1)     // Catch:{ Exception -> 0x1120, all -> 0x112b }
            java.lang.String r1 = " was null"
            r5.append(r1)     // Catch:{ Exception -> 0x1120, all -> 0x112b }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x1120, all -> 0x112b }
            r2.<init>(r1)     // Catch:{ Exception -> 0x1120, all -> 0x112b }
            throw r2     // Catch:{ Exception -> 0x1120, all -> 0x112b }
        L_0x10fc:
            r0 = move-exception
            r4 = r78
            r14 = r3
            goto L_0x1138
        L_0x1101:
            r4 = r78
            r14 = r3
            r54 = r8
            r9 = r10
            r3 = r59
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1120, all -> 0x112b }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1120, all -> 0x112b }
            r5.<init>()     // Catch:{ Exception -> 0x1120, all -> 0x112b }
            java.lang.String r6 = "unexpected result from encoder.dequeueOutputBuffer: "
            r5.append(r6)     // Catch:{ Exception -> 0x1120, all -> 0x112b }
            r5.append(r1)     // Catch:{ Exception -> 0x1120, all -> 0x112b }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x1120, all -> 0x112b }
            r2.<init>(r1)     // Catch:{ Exception -> 0x1120, all -> 0x112b }
            throw r2     // Catch:{ Exception -> 0x1120, all -> 0x112b }
        L_0x1120:
            r0 = move-exception
            r40 = r85
            r5 = r0
            r59 = r3
            goto L_0x1140
        L_0x1127:
            r0 = move-exception
            r4 = r78
            goto L_0x1135
        L_0x112b:
            r0 = move-exception
            r3 = r0
            r2 = r12
            goto L_0x05f3
        L_0x1130:
            r0 = move-exception
            r4 = r78
            r7 = r79
        L_0x1135:
            r14 = r3
            r62 = r6
        L_0x1138:
            r54 = r8
            r9 = r10
            r3 = r59
        L_0x113d:
            r40 = r85
            r5 = r0
        L_0x1140:
            r6 = r7
        L_0x1141:
            r2 = r12
            goto L_0x0a03
        L_0x1144:
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
        L_0x1155:
            r2 = r17
            r3 = r18
            r54 = r60
        L_0x115b:
            r1 = 0
            goto L_0x1233
        L_0x115e:
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
        L_0x116f:
            r3 = r18
            r35 = r30
            r1 = 0
            r2 = -5
            goto L_0x1233
        L_0x1177:
            r0 = move-exception
            r4 = r78
            r7 = r79
            r9 = r81
            r14 = r3
            r3 = r59
            goto L_0x118d
        L_0x1182:
            r0 = move-exception
            r4 = r78
            r7 = r79
            r9 = r81
            r3 = r59
            r14 = r61
        L_0x118d:
            r54 = r83
            r5 = r0
            r6 = r7
        L_0x1191:
            r3 = r18
            r35 = r30
            r1 = 0
            r2 = -5
            goto L_0x11d8
        L_0x1198:
            r0 = move-exception
            r4 = r78
            r7 = r79
            r9 = r81
            r3 = r59
            r14 = r61
            r54 = r83
            r5 = r0
            goto L_0x11b7
        L_0x11a7:
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
        L_0x11b7:
            r6 = r7
            r3 = r18
            r35 = r30
            r1 = 0
            r2 = -5
            goto L_0x11d6
        L_0x11bf:
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
        L_0x11d6:
            r27 = 0
        L_0x11d8:
            r40 = 0
            goto L_0x1233
        L_0x11dc:
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
            goto L_0x122c
        L_0x11f0:
            r0 = move-exception
            r9 = r81
            r30 = r7
            r4 = r14
            r62 = r24
            r54 = r83
            r5 = r0
            r3 = r18
            r35 = r30
            goto L_0x0716
        L_0x1201:
            r0 = move-exception
            r9 = r81
            goto L_0x1210
        L_0x1205:
            r0 = move-exception
            r9 = r81
            r30 = r7
            r4 = r14
            r62 = r24
            goto L_0x1216
        L_0x120e:
            r0 = move-exception
            r9 = r1
        L_0x1210:
            r30 = r7
            r4 = r14
            r62 = r24
            r1 = 0
        L_0x1216:
            r54 = r83
            r5 = r0
            r35 = r30
            goto L_0x122a
        L_0x121c:
            r0 = move-exception
            r9 = r81
            r62 = r4
            r4 = r14
            r1 = 0
            r3 = r79
            r54 = r83
            r35 = r85
            r5 = r0
        L_0x122a:
            r2 = -5
            r6 = 0
        L_0x122c:
            r14 = 0
            r27 = 0
            r40 = 0
            r59 = 0
        L_0x1233:
            boolean r7 = r5 instanceof java.lang.IllegalStateException     // Catch:{ Exception -> 0x12aa, all -> 0x05f1 }
            if (r7 == 0) goto L_0x123a
            if (r90 != 0) goto L_0x123a
            r1 = 1
        L_0x123a:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x12a1, all -> 0x05f1 }
            r7.<init>()     // Catch:{ Exception -> 0x12a1, all -> 0x05f1 }
            java.lang.String r8 = "bitrate: "
            r7.append(r8)     // Catch:{ Exception -> 0x12a1, all -> 0x05f1 }
            r7.append(r3)     // Catch:{ Exception -> 0x12a1, all -> 0x05f1 }
            java.lang.String r8 = " framerate: "
            r7.append(r8)     // Catch:{ Exception -> 0x12a1, all -> 0x05f1 }
            r7.append(r4)     // Catch:{ Exception -> 0x12a1, all -> 0x05f1 }
            java.lang.String r8 = " size: "
            r7.append(r8)     // Catch:{ Exception -> 0x12a1, all -> 0x05f1 }
            r8 = r77
            r7.append(r8)     // Catch:{ Exception -> 0x129d, all -> 0x05f1 }
            java.lang.String r11 = "x"
            r7.append(r11)     // Catch:{ Exception -> 0x129d, all -> 0x05f1 }
            r11 = r76
            r7.append(r11)     // Catch:{ Exception -> 0x129b, all -> 0x05f1 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x129b, all -> 0x05f1 }
            org.telegram.messenger.FileLog.e((java.lang.String) r7)     // Catch:{ Exception -> 0x129b, all -> 0x05f1 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ Exception -> 0x129b, all -> 0x05f1 }
            r17 = r2
            r18 = r3
            r3 = r6
            r2 = r14
            r6 = r1
            r1 = 1
        L_0x1275:
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x1296, all -> 0x128f }
            r7 = r62
            r5.unselectTrack(r7)     // Catch:{ Exception -> 0x1296, all -> 0x128f }
            if (r2 == 0) goto L_0x1284
            r2.stop()     // Catch:{ Exception -> 0x1296, all -> 0x128f }
            r2.release()     // Catch:{ Exception -> 0x1296, all -> 0x128f }
        L_0x1284:
            r5 = r1
            r7 = r6
            r2 = r17
            r6 = r27
            r1 = r40
            r40 = r59
            goto L_0x12c8
        L_0x128f:
            r0 = move-exception
            r3 = r0
            r1 = r15
            r2 = r17
            goto L_0x13ac
        L_0x1296:
            r0 = move-exception
            r1 = r0
            r2 = r17
            goto L_0x12d1
        L_0x129b:
            r0 = move-exception
            goto L_0x12a6
        L_0x129d:
            r0 = move-exception
            r11 = r76
            goto L_0x12a6
        L_0x12a1:
            r0 = move-exception
            r11 = r76
            r8 = r77
        L_0x12a6:
            r6 = r1
            r1 = r0
            goto L_0x132b
        L_0x12aa:
            r0 = move-exception
            r11 = r76
            r8 = r77
            r1 = r0
            goto L_0x132a
        L_0x12b2:
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
        L_0x12c8:
            if (r6 == 0) goto L_0x12d5
            r6.release()     // Catch:{ Exception -> 0x12ce, all -> 0x05f1 }
            goto L_0x12d5
        L_0x12ce:
            r0 = move-exception
            r1 = r0
            r6 = r7
        L_0x12d1:
            r3 = r18
            goto L_0x132b
        L_0x12d5:
            if (r40 == 0) goto L_0x12da
            r40.release()     // Catch:{ Exception -> 0x12ce, all -> 0x05f1 }
        L_0x12da:
            if (r3 == 0) goto L_0x12e2
            r3.stop()     // Catch:{ Exception -> 0x12ce, all -> 0x05f1 }
            r3.release()     // Catch:{ Exception -> 0x12ce, all -> 0x05f1 }
        L_0x12e2:
            if (r1 == 0) goto L_0x12e7
            r1.release()     // Catch:{ Exception -> 0x12ce, all -> 0x05f1 }
        L_0x12e7:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x12ce, all -> 0x05f1 }
            r1 = r5
            r6 = r7
            r51 = r8
        L_0x12ee:
            android.media.MediaExtractor r3 = r15.extractor
            if (r3 == 0) goto L_0x12f5
            r3.release()
        L_0x12f5:
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer
            if (r3 == 0) goto L_0x130a
            r3.finishMovie()     // Catch:{ Exception -> 0x1305 }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ Exception -> 0x1305 }
            long r2 = r3.getLastFrameTimestamp(r2)     // Catch:{ Exception -> 0x1305 }
            r15.endPresentationTime = r2     // Catch:{ Exception -> 0x1305 }
            goto L_0x130a
        L_0x1305:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x130a:
            r7 = r51
            r13 = r54
            goto L_0x137c
        L_0x1310:
            r0 = move-exception
            r8 = r77
            r4 = r10
            goto L_0x131e
        L_0x1315:
            r0 = move-exception
            r3 = r0
            r1 = r15
            r2 = -5
            goto L_0x13ac
        L_0x131b:
            r0 = move-exception
            r4 = r10
            r8 = r11
        L_0x131e:
            r11 = r12
            r1 = 0
            r9 = r81
            r3 = r79
            r54 = r83
            r35 = r85
            r1 = r0
        L_0x1329:
            r2 = -5
        L_0x132a:
            r6 = 0
        L_0x132b:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x13a8 }
            r5.<init>()     // Catch:{ all -> 0x13a8 }
            java.lang.String r7 = "bitrate: "
            r5.append(r7)     // Catch:{ all -> 0x13a8 }
            r5.append(r3)     // Catch:{ all -> 0x13a8 }
            java.lang.String r7 = " framerate: "
            r5.append(r7)     // Catch:{ all -> 0x13a8 }
            r5.append(r4)     // Catch:{ all -> 0x13a8 }
            java.lang.String r7 = " size: "
            r5.append(r7)     // Catch:{ all -> 0x13a8 }
            r5.append(r8)     // Catch:{ all -> 0x13a8 }
            java.lang.String r7 = "x"
            r5.append(r7)     // Catch:{ all -> 0x13a8 }
            r5.append(r11)     // Catch:{ all -> 0x13a8 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x13a8 }
            org.telegram.messenger.FileLog.e((java.lang.String) r5)     // Catch:{ all -> 0x13a8 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x13a8 }
            android.media.MediaExtractor r1 = r15.extractor
            if (r1 == 0) goto L_0x1361
            r1.release()
        L_0x1361:
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer
            if (r1 == 0) goto L_0x1376
            r1.finishMovie()     // Catch:{ Exception -> 0x1371 }
            org.telegram.messenger.video.MP4Builder r1 = r15.mediaMuxer     // Catch:{ Exception -> 0x1371 }
            long r1 = r1.getLastFrameTimestamp(r2)     // Catch:{ Exception -> 0x1371 }
            r15.endPresentationTime = r1     // Catch:{ Exception -> 0x1371 }
            goto L_0x1376
        L_0x1371:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1376:
            r18 = r3
            r7 = r8
            r13 = r54
            r1 = 1
        L_0x137c:
            if (r6 == 0) goto L_0x13a7
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
        L_0x13a7:
            return r1
        L_0x13a8:
            r0 = move-exception
            r1 = r71
        L_0x13ab:
            r3 = r0
        L_0x13ac:
            android.media.MediaExtractor r4 = r1.extractor
            if (r4 == 0) goto L_0x13b3
            r4.release()
        L_0x13b3:
            org.telegram.messenger.video.MP4Builder r4 = r1.mediaMuxer
            if (r4 == 0) goto L_0x13c8
            r4.finishMovie()     // Catch:{ Exception -> 0x13c3 }
            org.telegram.messenger.video.MP4Builder r4 = r1.mediaMuxer     // Catch:{ Exception -> 0x13c3 }
            long r4 = r4.getLastFrameTimestamp(r2)     // Catch:{ Exception -> 0x13c3 }
            r1.endPresentationTime = r4     // Catch:{ Exception -> 0x13c3 }
            goto L_0x13c8
        L_0x13c3:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x13c8:
            goto L_0x13ca
        L_0x13c9:
            throw r3
        L_0x13ca:
            goto L_0x13c9
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
