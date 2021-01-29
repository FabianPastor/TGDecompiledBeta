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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v0, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v1, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v2, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v8, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v14, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v18, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v21, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v13, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v21, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v14, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v25, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v26, resolved type: android.media.MediaFormat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v15, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v16, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v17, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v27, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v19, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v20, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v32, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v33, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v24, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v34, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v35, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v36, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v28, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v29, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v37, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v41, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v4, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v13, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v42, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v33, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v45, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v46, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v38, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v39, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v40, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v40, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v49, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v50, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v51, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v3, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v52, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v35, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v36, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v5, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v5, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v53, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v41, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v57, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v6, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v37, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v22, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v17, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v18, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v31, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v7, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v8, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v62, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v63, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v9, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v47, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v41, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v48, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v9, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v10, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v26, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v43, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v72, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r65v12, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v73, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v49, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v47, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v50, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v52, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v53, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v76, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v54, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v58, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v59, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v60, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v92, resolved type: org.telegram.messenger.video.MP4Builder} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v93, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v97, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v55, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v10, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v51, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v11, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v56, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v18, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v21, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v12, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v25, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v76, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v77, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v44, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v45, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v53, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v55, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v80, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v23, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v81, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v46, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v108, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v60, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v82, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v59, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v60, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v83, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v26, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v27, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v84, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v85, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v86, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v66, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v67, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v87, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v88, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v89, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v70, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v90, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v91, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v30, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v92, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v117, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v93, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v94, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v76, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v77, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v95, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v96, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v78, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v97, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v98, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v99, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v79, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v51, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v100, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v101, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v124, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v102, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v80, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v103, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v55, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v104, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v56, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v81, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v105, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v57, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v106, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v82, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v83, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v107, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v84, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v108, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v86, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v87, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v109, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v61, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v110, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v62, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v88, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v111, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v89, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v112, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v64, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v90, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v113, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v65, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v91, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v114, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v66, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v92, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v115, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v67, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v94, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v116, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v68, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v117, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v69, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v95, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v118, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v70, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v96, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v119, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v71, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v72, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v98, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v120, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v73, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v99, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v74, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v100, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v75, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v101, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v78, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v109, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v79, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v118, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r47v127, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v177, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v94, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v95, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v179, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v96, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v97, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v98, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v99, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v100, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v101, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v182, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v183, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v184, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v185, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v186, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v187, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v188, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v102, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v194, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v103, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v142, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v26, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v146, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v147, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v148, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v197, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v198, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v199, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v201, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v202, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v109, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v149, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v203, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v111, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v204, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v112, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v206, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v207, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v113, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v114, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v115, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v116, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v117, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v153, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v154, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v155, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v156, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v159, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v118, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v48, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v49, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v51, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v52, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v54, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v209, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v210, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v211, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v212, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v120, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v215, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v129, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v130, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v131, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v134, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v217, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v218, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v219, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v220, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v221, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v222, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v55, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v224, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v226, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v227, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v228, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v229, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v56, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v57, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v58, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v61, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v63, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v231, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v232, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v66, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v249, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v250, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v251, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v252, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v253, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v254, resolved type: java.nio.ByteBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v255, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v260, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v261, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v262, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v135, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v136, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v32, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v34, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r59v42, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v137, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v265, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v138, resolved type: android.media.MediaCodec} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v160, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v139, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v163, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v164, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v141, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v145, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v148, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v167, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v168, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v266, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v267, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v269, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v169, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v270, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v150, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v172, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v173, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v271, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v273, resolved type: org.telegram.messenger.video.MediaCodecVideoConvertor} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v275, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v276, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v277, resolved type: long} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v278, resolved type: long} */
    /* JADX WARNING: type inference failed for: r8v70 */
    /* JADX WARNING: type inference failed for: r7v140 */
    /* JADX WARNING: type inference failed for: r7v141 */
    /* JADX WARNING: type inference failed for: r8v151 */
    /* JADX WARNING: type inference failed for: r11v279 */
    /* JADX WARNING: Code restructure failed: missing block: B:1016:0x115c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1017:0x115d, code lost:
        r47 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1021:0x116b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1022:0x116c, code lost:
        r8 = r79;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1038:0x11c0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1039:0x11c1, code lost:
        r9 = r78;
        r8 = r79;
        r10 = r54;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1040:0x11c8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1041:0x11c9, code lost:
        r9 = r78;
        r8 = r79;
        r10 = r54;
        r3 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1042:0x11d1, code lost:
        r47 = r83;
        r2 = r0;
        r6 = r8;
        r7 = r7;
        r11 = r11;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1044:0x11db, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1045:0x11dc, code lost:
        r9 = r78;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1051:0x1204, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1052:0x1205, code lost:
        r31 = r85;
        r8 = r5;
        r9 = r14;
        r61 = r21;
        r47 = r83;
        r2 = r0;
        r6 = r8;
        r54 = r1;
        r4 = r20;
        r1 = -5;
        r3 = null;
        r7 = r7;
        r11 = r11;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1055:0x121e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1056:0x121f, code lost:
        r31 = r85;
        r8 = r5;
        r9 = r14;
        r61 = r21;
        r47 = r83;
        r2 = r0;
        r6 = r8;
        r4 = r20;
        r1 = -5;
        r3 = null;
        r11 = r11;
        r8 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1057:0x1231, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1058:0x1232, code lost:
        r31 = r85;
        r9 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1061:0x1244, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1062:0x1245, code lost:
        r31 = r85;
        r9 = r14;
        r61 = r21;
        r47 = r83;
        r2 = r0;
        r4 = r20;
        r8 = r8;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1065:0x1259, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1066:0x125a, code lost:
        r31 = r85;
        r9 = r14;
        r11 = r71;
        r12 = r76;
        r7 = r77;
        r47 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1067:0x1269, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1068:0x126a, code lost:
        r31 = r85;
        r61 = r5;
        r9 = r14;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:107:0x0283, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1081:0x129c, code lost:
        r50 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1097:0x12f5, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1098:0x12f6, code lost:
        r11 = r71;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1109:0x1336, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1110:0x1337, code lost:
        r2 = r0;
        r12 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1139:0x13b8, code lost:
        r2.release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1143:?, code lost:
        r2.finishMovie();
        r11.endPresentationTime = r11.mediaMuxer.getLastFrameTimestamp(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1144:0x13cb, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1145:0x13cc, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x0492, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x0497, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x0499, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x04ce, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x04d0, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x04d1, code lost:
        r10 = r37;
        r2 = r0;
        r6 = r76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x04d8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x04d9, code lost:
        r10 = r2;
        r48 = r9;
        r49 = r11;
        r11 = r6;
        r2 = r0;
        r37 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x04e2, code lost:
        r11 = r49;
        r1 = -5;
        r17 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x04e8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x04e9, code lost:
        r48 = r9;
        r49 = r11;
        r11 = r6;
        r2 = r0;
        r11 = r49;
        r1 = -5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x04f3, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x04f4, code lost:
        r48 = r9;
        r49 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x04f8, code lost:
        r9 = r78;
        r31 = r85;
        r2 = r0;
        r11 = r15;
        r4 = r48;
        r7 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x052c, code lost:
        r46 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x0726, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x0727, code lost:
        r12 = r76;
        r7 = r77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x075e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x075f, code lost:
        r47 = r83;
        r31 = r85;
        r2 = r0;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x086b, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x0878, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x0879, code lost:
        r9 = r78;
        r6 = r79;
        r47 = r83;
        r2 = r0;
        r42 = null;
        r11 = r11;
        r8 = r8;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0968, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x0969, code lost:
        r9 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0975, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x0976, code lost:
        r6 = r79;
        r2 = r0;
        r47 = r83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x0a0d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x0a0e, code lost:
        r12 = r76;
        r7 = r77;
        r1 = r83;
        r2 = r0;
        r47 = r9;
        r9 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x0a1d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x0a1e, code lost:
        r1 = r83;
        r2 = r0;
        r42 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x01b0, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x01b1, code lost:
        r6 = r76;
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x0ae6, code lost:
        if (r13.presentationTimeUs < r9) goto L_0x0ae8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x01cf, code lost:
        r6 = r7;
        r7 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x0e01, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x0e9d, code lost:
        r0 = e;
        r8 = r8;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x0f0b, code lost:
        r0 = th;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x0f5a, code lost:
        r0 = e;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:971:0x1050, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:972:0x1051, code lost:
        r1 = r83;
        r42 = r85;
        r2 = r0;
        r6 = r8;
        r54 = r10;
        r46 = r46;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:0x105a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:974:0x105b, code lost:
        r11 = r71;
        r12 = r76;
        r7 = r77;
        r1 = r83;
        r2 = r0;
        r4 = r20;
        r47 = r31;
        r31 = r37;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:975:0x106c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:976:0x106d, code lost:
        r10 = r54;
        r46 = r46;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1016:0x115c A[ExcHandler: all (th java.lang.Throwable), Splitter:B:673:0x0be5] */
    /* JADX WARNING: Removed duplicated region for block: B:1044:0x11db A[ExcHandler: all (th java.lang.Throwable), Splitter:B:450:0x0844] */
    /* JADX WARNING: Removed duplicated region for block: B:1057:0x1231 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:440:0x07e4] */
    /* JADX WARNING: Removed duplicated region for block: B:1065:0x1259 A[ExcHandler: all (th java.lang.Throwable), PHI: r85 
      PHI: (r85v1 long) = (r85v3 long), (r85v3 long), (r85v3 long), (r85v3 long), (r85v19 long), (r85v19 long), (r85v19 long), (r85v19 long) binds: [B:414:0x0770, B:415:?, B:419:0x0790, B:420:?, B:395:0x0736, B:396:?, B:398:0x073f, B:399:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:395:0x0736] */
    /* JADX WARNING: Removed duplicated region for block: B:1080:0x129a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:1095:0x12e4 A[Catch:{ all -> 0x12f5, all -> 0x1336 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1106:0x1317 A[Catch:{ all -> 0x12f5, all -> 0x1336 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1108:0x1332 A[Catch:{ all -> 0x12f5, all -> 0x1336 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1112:0x133c A[Catch:{ all -> 0x12f5, all -> 0x1336 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1114:0x1341 A[Catch:{ all -> 0x12f5, all -> 0x1336 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1116:0x1349 A[Catch:{ all -> 0x12f5, all -> 0x1336 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1121:0x1355  */
    /* JADX WARNING: Removed duplicated region for block: B:1124:0x135c A[SYNTHETIC, Splitter:B:1124:0x135c] */
    /* JADX WARNING: Removed duplicated region for block: B:1139:0x13b8  */
    /* JADX WARNING: Removed duplicated region for block: B:1142:0x13bf A[SYNTHETIC, Splitter:B:1142:0x13bf] */
    /* JADX WARNING: Removed duplicated region for block: B:1148:0x13d9  */
    /* JADX WARNING: Removed duplicated region for block: B:1150:0x1400 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03dd A[Catch:{ Exception -> 0x0459, all -> 0x0457 }] */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x03e0 A[Catch:{ Exception -> 0x0459, all -> 0x0457 }] */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x03ed  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x0499 A[ExcHandler: all (th java.lang.Throwable), PHI: r1 r49 
      PHI: (r1v167 int) = (r1v162 int), (r1v168 int), (r1v168 int), (r1v168 int), (r1v168 int), (r1v168 int), (r1v168 int) binds: [B:67:0x01c2, B:77:0x01ea, B:234:0x045d, B:171:0x0356, B:128:0x02ae, B:114:0x0293, B:86:0x020a] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r49v28 int) = (r49v23 int), (r49v29 int), (r49v29 int), (r49v29 int), (r49v29 int), (r49v29 int), (r49v29 int) binds: [B:67:0x01c2, B:77:0x01ea, B:234:0x045d, B:171:0x0356, B:128:0x02ae, B:114:0x0293, B:86:0x020a] A[DONT_GENERATE, DONT_INLINE], Splitter:B:67:0x01c2] */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x04ce A[ExcHandler: all (th java.lang.Throwable), Splitter:B:54:0x01a4] */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x04f3 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:40:0x0110] */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x052a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x056d A[SYNTHETIC, Splitter:B:291:0x056d] */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x057c A[Catch:{ all -> 0x0571 }] */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x0581 A[Catch:{ all -> 0x0571 }] */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x0660  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0726 A[ExcHandler: all (th java.lang.Throwable), PHI: r85 
      PHI: (r85v18 long) = (r85v3 long), (r85v3 long), (r85v3 long), (r85v3 long), (r85v19 long), (r85v19 long) binds: [B:423:0x07b2, B:417:0x0774, B:418:?, B:406:0x0752, B:388:0x0717, B:389:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:388:0x0717] */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x085b A[SYNTHETIC, Splitter:B:459:0x085b] */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x086b A[ExcHandler: all (th java.lang.Throwable), Splitter:B:459:0x085b] */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0887  */
    /* JADX WARNING: Removed duplicated region for block: B:470:0x088d A[SYNTHETIC, Splitter:B:470:0x088d] */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x08bd  */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x08c0  */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0968 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:502:0x0916] */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x0984  */
    /* JADX WARNING: Removed duplicated region for block: B:546:0x09b5  */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x09c3  */
    /* JADX WARNING: Removed duplicated region for block: B:549:0x09c5  */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x0a0d A[ExcHandler: all (r0v103 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r83 
      PHI: (r83v21 int) = (r83v3 int), (r83v3 int), (r83v3 int), (r83v3 int), (r83v3 int), (r83v3 int), (r83v3 int), (r83v22 int) binds: [B:618:0x0ae2, B:619:?, B:609:0x0ac3, B:610:?, B:604:0x0ab7, B:605:?, B:580:0x0a3e, B:562:0x0a04] A[DONT_GENERATE, DONT_INLINE], Splitter:B:562:0x0a04] */
    /* JADX WARNING: Removed duplicated region for block: B:575:0x0a30 A[SYNTHETIC, Splitter:B:575:0x0a30] */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0b72 A[Catch:{ Exception -> 0x0b91, all -> 0x0b8f }] */
    /* JADX WARNING: Removed duplicated region for block: B:668:0x0bb4  */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0bea  */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0bf5  */
    /* JADX WARNING: Removed duplicated region for block: B:682:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:685:0x0c1c  */
    /* JADX WARNING: Removed duplicated region for block: B:818:0x0e21 A[Catch:{ Exception -> 0x1109, all -> 0x1107 }] */
    /* JADX WARNING: Removed duplicated region for block: B:819:0x0e23 A[Catch:{ Exception -> 0x1109, all -> 0x1107 }] */
    /* JADX WARNING: Removed duplicated region for block: B:823:0x0e2e  */
    /* JADX WARNING: Removed duplicated region for block: B:825:0x0e49  */
    /* JADX WARNING: Removed duplicated region for block: B:842:0x0e9d A[ExcHandler: Exception (e java.lang.Exception), PHI: r5 r11 r47 
      PHI: (r5v24 int) = (r5v25 int), (r5v18 int) binds: [B:836:0x0e74, B:775:0x0d5c] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r11v50 org.telegram.messenger.video.MediaCodecVideoConvertor) = (r11v222 java.lang.String), (r11v253 java.nio.ByteBuffer) binds: [B:836:0x0e74, B:775:0x0d5c] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r47v43 long) = (r47v44 long), (r47v60 long) binds: [B:836:0x0e74, B:775:0x0d5c] A[DONT_GENERATE, DONT_INLINE], Splitter:B:775:0x0d5c] */
    /* JADX WARNING: Removed duplicated region for block: B:898:0x0f5a A[ExcHandler: Exception (e java.lang.Exception), PHI: r11 
      PHI: (r11v65 org.telegram.messenger.video.MediaCodecVideoConvertor) = (r11v235 org.telegram.messenger.video.MediaCodecVideoConvertor), (r11v237 org.telegram.messenger.video.MediaCodecVideoConvertor), (r11v238 org.telegram.messenger.video.MediaCodecVideoConvertor), (r11v240 org.telegram.messenger.video.MediaCodecVideoConvertor), (r11v244 org.telegram.messenger.video.MediaCodecVideoConvertor), (r11v246 org.telegram.messenger.video.MediaCodecVideoConvertor) binds: [B:882:0x0var_, B:883:?, B:885:0x0var_, B:886:?, B:877:0x0var_, B:878:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:877:0x0var_] */
    /* JADX WARNING: Removed duplicated region for block: B:915:0x0fa0  */
    /* JADX WARNING: Removed duplicated region for block: B:916:0x0fa3  */
    /* JADX WARNING: Removed duplicated region for block: B:923:0x0fb1 A[SYNTHETIC, Splitter:B:923:0x0fb1] */
    /* JADX WARNING: Removed duplicated region for block: B:928:0x0fd5 A[Catch:{ Exception -> 0x0fe3, all -> 0x105a }] */
    /* JADX WARNING: Removed duplicated region for block: B:935:0x0fe8 A[Catch:{ Exception -> 0x0fe3, all -> 0x105a }] */
    /* JADX WARNING: Removed duplicated region for block: B:936:0x0feb A[Catch:{ Exception -> 0x0fe3, all -> 0x105a }] */
    /* JADX WARNING: Removed duplicated region for block: B:944:0x1000  */
    /* JADX WARNING: Removed duplicated region for block: B:962:0x1031 A[Catch:{ Exception -> 0x1050, all -> 0x105a }] */
    /* JADX WARNING: Removed duplicated region for block: B:965:0x103d A[Catch:{ Exception -> 0x1050, all -> 0x105a }] */
    /* JADX WARNING: Removed duplicated region for block: B:970:0x104c  */
    /* JADX WARNING: Removed duplicated region for block: B:973:0x105a A[ExcHandler: all (r0v57 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:941:0x0ffb] */
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
            int r2 = android.os.Build.VERSION.SDK_INT
            r16 = -1
            r7 = 0
            android.media.MediaCodec$BufferInfo r6 = new android.media.MediaCodec$BufferInfo     // Catch:{ all -> 0x1373 }
            r6.<init>()     // Catch:{ all -> 0x1373 }
            org.telegram.messenger.video.Mp4Movie r1 = new org.telegram.messenger.video.Mp4Movie     // Catch:{ all -> 0x1373 }
            r1.<init>()     // Catch:{ all -> 0x1373 }
            r20 = r6
            r6 = r73
            r1.setCacheFile(r6)     // Catch:{ all -> 0x1373 }
            r1.setRotation(r7)     // Catch:{ all -> 0x1373 }
            r1.setSize(r12, r11)     // Catch:{ all -> 0x1373 }
            org.telegram.messenger.video.MP4Builder r7 = new org.telegram.messenger.video.MP4Builder     // Catch:{ all -> 0x1373 }
            r7.<init>()     // Catch:{ all -> 0x1373 }
            r6 = r75
            org.telegram.messenger.video.MP4Builder r1 = r7.createMovie(r1, r6)     // Catch:{ all -> 0x1373 }
            r15.mediaMuxer = r1     // Catch:{ all -> 0x1373 }
            float r1 = (float) r4     // Catch:{ all -> 0x1373 }
            r22 = 1148846080(0x447a0000, float:1000.0)
            float r23 = r1 / r22
            r24 = 1000(0x3e8, double:4.94E-321)
            r7 = r2
            long r1 = r4 * r24
            r15.endPresentationTime = r1     // Catch:{ all -> 0x1373 }
            r71.checkConversionCanceled()     // Catch:{ all -> 0x1373 }
            java.lang.String r1 = "csd-0"
            java.lang.String r6 = "prepend-sps-pps-to-idr-frames"
            r2 = 921600(0xe1000, float:1.291437E-39)
            r26 = r6
            r25 = r7
            java.lang.String r7 = "video/avc"
            r31 = r7
            r6 = 0
            if (r94 == 0) goto L_0x05c4
            int r16 = (r85 > r6 ? 1 : (r85 == r6 ? 0 : -1))
            if (r16 < 0) goto L_0x0088
            r2 = 1157234688(0x44fa0000, float:2000.0)
            int r2 = (r23 > r2 ? 1 : (r23 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0073
            r2 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            r9 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x008d
        L_0x0073:
            r2 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r2 = (r23 > r2 ? 1 : (r23 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x0081
            r2 = 2200000(0x2191c0, float:3.082857E-39)
            r9 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x008d
        L_0x0081:
            r2 = 1560000(0x17cdc0, float:2.186026E-39)
            r9 = 1560000(0x17cdc0, float:2.186026E-39)
            goto L_0x008d
        L_0x0088:
            if (r9 > 0) goto L_0x008d
            r9 = 921600(0xe1000, float:1.291437E-39)
        L_0x008d:
            int r2 = r12 % 16
            r16 = 1098907648(0x41800000, float:16.0)
            if (r2 == 0) goto L_0x00d9
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00d3 }
            if (r2 == 0) goto L_0x00bc
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d3 }
            r2.<init>()     // Catch:{ Exception -> 0x00d3 }
            java.lang.String r6 = "changing width from "
            r2.append(r6)     // Catch:{ Exception -> 0x00d3 }
            r2.append(r12)     // Catch:{ Exception -> 0x00d3 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00d3 }
            float r6 = (float) r12     // Catch:{ Exception -> 0x00d3 }
            float r6 = r6 / r16
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00d3 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00d3 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d3 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d3 }
        L_0x00bc:
            float r2 = (float) r12     // Catch:{ Exception -> 0x00d3 }
            float r2 = r2 / r16
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00d3 }
            int r2 = r2 * 16
            r12 = r2
            goto L_0x00d9
        L_0x00c7:
            r0 = move-exception
            r47 = r83
            r31 = r85
            r2 = r0
            r4 = r9
            r9 = r10
            r7 = r11
        L_0x00d0:
            r11 = r15
            goto L_0x1381
        L_0x00d3:
            r0 = move-exception
            r2 = r0
            r48 = r9
            goto L_0x0520
        L_0x00d9:
            int r2 = r11 % 16
            if (r2 == 0) goto L_0x0110
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x00d3 }
            if (r2 == 0) goto L_0x0106
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d3 }
            r2.<init>()     // Catch:{ Exception -> 0x00d3 }
            java.lang.String r6 = "changing height from "
            r2.append(r6)     // Catch:{ Exception -> 0x00d3 }
            r2.append(r11)     // Catch:{ Exception -> 0x00d3 }
            java.lang.String r6 = " to "
            r2.append(r6)     // Catch:{ Exception -> 0x00d3 }
            float r6 = (float) r11     // Catch:{ Exception -> 0x00d3 }
            float r6 = r6 / r16
            int r6 = java.lang.Math.round(r6)     // Catch:{ Exception -> 0x00d3 }
            int r6 = r6 * 16
            r2.append(r6)     // Catch:{ Exception -> 0x00d3 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d3 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d3 }
        L_0x0106:
            float r2 = (float) r11     // Catch:{ Exception -> 0x00d3 }
            float r2 = r2 / r16
            int r2 = java.lang.Math.round(r2)     // Catch:{ Exception -> 0x00d3 }
            int r2 = r2 * 16
            r11 = r2
        L_0x0110:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0503, all -> 0x04f3 }
            if (r2 == 0) goto L_0x0138
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d3 }
            r2.<init>()     // Catch:{ Exception -> 0x00d3 }
            java.lang.String r6 = "create photo encoder "
            r2.append(r6)     // Catch:{ Exception -> 0x00d3 }
            r2.append(r12)     // Catch:{ Exception -> 0x00d3 }
            java.lang.String r6 = " "
            r2.append(r6)     // Catch:{ Exception -> 0x00d3 }
            r2.append(r11)     // Catch:{ Exception -> 0x00d3 }
            java.lang.String r6 = " duration = "
            r2.append(r6)     // Catch:{ Exception -> 0x00d3 }
            r2.append(r4)     // Catch:{ Exception -> 0x00d3 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00d3 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x00d3 }
        L_0x0138:
            r7 = r31
            android.media.MediaFormat r2 = android.media.MediaFormat.createVideoFormat(r7, r12, r11)     // Catch:{ Exception -> 0x0503, all -> 0x04f3 }
            java.lang.String r6 = "color-format"
            r31 = r1
            r1 = 2130708361(0x7var_, float:1.701803E38)
            r2.setInteger(r6, r1)     // Catch:{ Exception -> 0x0503, all -> 0x04f3 }
            java.lang.String r1 = "bitrate"
            r2.setInteger(r1, r9)     // Catch:{ Exception -> 0x0503, all -> 0x04f3 }
            java.lang.String r1 = "frame-rate"
            r2.setInteger(r1, r10)     // Catch:{ Exception -> 0x0503, all -> 0x04f3 }
            java.lang.String r1 = "i-frame-interval"
            r6 = 2
            r2.setInteger(r1, r6)     // Catch:{ Exception -> 0x0503, all -> 0x04f3 }
            android.media.MediaCodec r6 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x0503, all -> 0x04f3 }
            r16 = r7
            r1 = 0
            r7 = 1
            r6.configure(r2, r1, r1, r7)     // Catch:{ Exception -> 0x04e8, all -> 0x04f3 }
            org.telegram.messenger.video.InputSurface r2 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x04e8, all -> 0x04f3 }
            android.view.Surface r1 = r6.createInputSurface()     // Catch:{ Exception -> 0x04e8, all -> 0x04f3 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x04e8, all -> 0x04f3 }
            r2.makeCurrent()     // Catch:{ Exception -> 0x04d8, all -> 0x04f3 }
            r6.start()     // Catch:{ Exception -> 0x04d8, all -> 0x04f3 }
            org.telegram.messenger.video.OutputSurface r17 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x04d8, all -> 0x04f3 }
            r18 = 0
            float r1 = (float) r10
            r30 = 1
            r19 = r1
            r36 = r31
            r31 = 0
            r1 = r17
            r37 = r2
            r38 = r25
            r2 = r91
            r3 = r72
            r4 = r92
            r5 = r93
            r7 = r6
            r14 = r20
            r13 = 21
            r6 = r18
            r76 = r7
            r47 = r16
            r7 = r12
            r8 = r11
            r48 = r9
            r9 = r74
            r10 = r19
            r49 = r11
            r11 = r30
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x04d0, all -> 0x04ce }
            r10 = r38
            if (r10 >= r13) goto L_0x01b6
            java.nio.ByteBuffer[] r6 = r76.getOutputBuffers()     // Catch:{ Exception -> 0x01b0, all -> 0x04ce }
            goto L_0x01b7
        L_0x01b0:
            r0 = move-exception
            r6 = r76
            r2 = r0
            goto L_0x04c9
        L_0x01b6:
            r6 = 0
        L_0x01b7:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x04c2, all -> 0x04ce }
            r1 = -5
            r2 = 1
            r3 = 0
            r4 = 0
            r5 = 0
            r7 = 0
        L_0x01c0:
            if (r7 != 0) goto L_0x04b1
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x04a6, all -> 0x0499 }
            r8 = r3 ^ 1
            r9 = r7
            r7 = r6
            r6 = 1
        L_0x01ca:
            if (r8 != 0) goto L_0x01d2
            if (r6 == 0) goto L_0x01cf
            goto L_0x01d2
        L_0x01cf:
            r6 = r7
            r7 = r9
            goto L_0x01c0
        L_0x01d2:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x04a6, all -> 0x0499 }
            if (r90 == 0) goto L_0x01e2
            r18 = 22000(0x55f0, double:1.08694E-319)
            r11 = r76
            r77 = r8
            r79 = r9
            r8 = r18
            goto L_0x01ea
        L_0x01e2:
            r11 = r76
            r77 = r8
            r79 = r9
            r8 = 2500(0x9c4, double:1.235E-320)
        L_0x01ea:
            int r8 = r11.dequeueOutputBuffer(r14, r8)     // Catch:{ Exception -> 0x0497, all -> 0x0499 }
            r9 = -1
            if (r8 != r9) goto L_0x0205
            r9 = r79
            r16 = r3
            r18 = r4
            r3 = r7
            r38 = r10
            r13 = r36
            r7 = r47
            r6 = 0
        L_0x01ff:
            r10 = -1
            r4 = r2
            r2 = r49
            goto L_0x03eb
        L_0x0205:
            r9 = -3
            if (r8 != r9) goto L_0x021c
            if (r10 >= r13) goto L_0x020e
            java.nio.ByteBuffer[] r7 = r11.getOutputBuffers()     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
        L_0x020e:
            r9 = r79
            r16 = r3
            r18 = r4
            r3 = r7
            r38 = r10
            r13 = r36
        L_0x0219:
            r7 = r47
            goto L_0x01ff
        L_0x021c:
            r9 = -2
            if (r8 != r9) goto L_0x0286
            android.media.MediaFormat r9 = r11.getOutputFormat()     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            boolean r16 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            if (r16 == 0) goto L_0x023e
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            r13.<init>()     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            r76 = r6
            java.lang.String r6 = "photo encoder new format "
            r13.append(r6)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            r13.append(r9)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            java.lang.String r6 = r13.toString()     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            org.telegram.messenger.FileLog.d(r6)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            goto L_0x0240
        L_0x023e:
            r76 = r6
        L_0x0240:
            r13 = -5
            if (r1 != r13) goto L_0x0275
            if (r9 == 0) goto L_0x0275
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            r13 = 0
            int r1 = r6.addTrack(r9, r13)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            r6 = r26
            boolean r16 = r9.containsKey(r6)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            if (r16 == 0) goto L_0x0273
            int r13 = r9.getInteger(r6)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            r26 = r6
            r6 = 1
            if (r13 != r6) goto L_0x0275
            r13 = r36
            java.nio.ByteBuffer r4 = r9.getByteBuffer(r13)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            java.lang.String r6 = "csd-1"
            java.nio.ByteBuffer r6 = r9.getByteBuffer(r6)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            int r4 = r4.limit()     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            int r6 = r6.limit()     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            int r4 = r4 + r6
            goto L_0x0277
        L_0x0273:
            r26 = r6
        L_0x0275:
            r13 = r36
        L_0x0277:
            r6 = r76
            r9 = r79
            r16 = r3
            r18 = r4
            r3 = r7
            r38 = r10
            goto L_0x0219
        L_0x0283:
            r0 = move-exception
            goto L_0x04ab
        L_0x0286:
            r76 = r6
            r13 = r36
            if (r8 < 0) goto L_0x0479
            r6 = 21
            if (r10 >= r6) goto L_0x0293
            r6 = r7[r8]     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            goto L_0x0297
        L_0x0293:
            java.nio.ByteBuffer r6 = r11.getOutputBuffer(r8)     // Catch:{ Exception -> 0x0497, all -> 0x0499 }
        L_0x0297:
            if (r6 == 0) goto L_0x045b
            int r9 = r14.size     // Catch:{ Exception -> 0x0497, all -> 0x0499 }
            r79 = r7
            r7 = 1
            if (r9 <= r7) goto L_0x03cc
            int r7 = r14.flags     // Catch:{ Exception -> 0x03c4, all -> 0x03b2 }
            r16 = r7 & 2
            if (r16 != 0) goto L_0x0331
            if (r4 == 0) goto L_0x02b7
            r16 = r7 & 1
            if (r16 == 0) goto L_0x02b7
            r38 = r10
            int r10 = r14.offset     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            int r10 = r10 + r4
            r14.offset = r10     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            int r9 = r9 - r4
            r14.size = r9     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            goto L_0x02b9
        L_0x02b7:
            r38 = r10
        L_0x02b9:
            if (r2 == 0) goto L_0x0308
            r7 = r7 & 1
            if (r7 == 0) goto L_0x0308
            int r2 = r14.size     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            r10 = 100
            if (r2 <= r10) goto L_0x0306
            int r2 = r14.offset     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            r6.position(r2)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            byte[] r2 = new byte[r10]     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            r6.get(r2)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            r7 = 0
            r9 = 0
        L_0x02d1:
            r10 = 96
            if (r7 >= r10) goto L_0x0306
            byte r10 = r2[r7]     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            if (r10 != 0) goto L_0x02fd
            int r10 = r7 + 1
            byte r10 = r2[r10]     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            if (r10 != 0) goto L_0x02fd
            int r10 = r7 + 2
            byte r10 = r2[r10]     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            if (r10 != 0) goto L_0x02fd
            int r10 = r7 + 3
            byte r10 = r2[r10]     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            r16 = r2
            r2 = 1
            if (r10 != r2) goto L_0x02ff
            int r9 = r9 + 1
            if (r9 <= r2) goto L_0x02ff
            int r2 = r14.offset     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            int r2 = r2 + r7
            r14.offset = r2     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            int r2 = r14.size     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            int r2 = r2 - r7
            r14.size = r2     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            goto L_0x0306
        L_0x02fd:
            r16 = r2
        L_0x02ff:
            int r7 = r7 + 1
            r2 = r16
            r10 = 100
            goto L_0x02d1
        L_0x0306:
            r7 = 0
            goto L_0x0309
        L_0x0308:
            r7 = r2
        L_0x0309:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            r10 = r7
            r9 = 1
            long r6 = r2.writeSampleData(r1, r6, r14, r9)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            r16 = r3
            r2 = 0
            int r9 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r9 == 0) goto L_0x0328
            org.telegram.messenger.MediaController$VideoConvertorListener r9 = r15.callback     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            if (r9 == 0) goto L_0x0328
            r18 = r4
            float r4 = (float) r2     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            float r4 = r4 / r22
            float r4 = r4 / r23
            r9.didWriteData(r6, r4)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            goto L_0x032a
        L_0x0328:
            r18 = r4
        L_0x032a:
            r4 = r10
        L_0x032b:
            r7 = r47
        L_0x032d:
            r2 = r49
            goto L_0x03d7
        L_0x0331:
            r16 = r3
            r18 = r4
            r38 = r10
            r7 = -5
            r4 = r2
            r2 = 0
            if (r1 != r7) goto L_0x032b
            byte[] r7 = new byte[r9]     // Catch:{ Exception -> 0x03c4, all -> 0x03b2 }
            int r10 = r14.offset     // Catch:{ Exception -> 0x03c4, all -> 0x03b2 }
            int r10 = r10 + r9
            r6.limit(r10)     // Catch:{ Exception -> 0x03c4, all -> 0x03b2 }
            int r9 = r14.offset     // Catch:{ Exception -> 0x03c4, all -> 0x03b2 }
            r6.position(r9)     // Catch:{ Exception -> 0x03c4, all -> 0x03b2 }
            r6.get(r7)     // Catch:{ Exception -> 0x03c4, all -> 0x03b2 }
            int r6 = r14.size     // Catch:{ Exception -> 0x03c4, all -> 0x03b2 }
            r9 = 1
            int r6 = r6 - r9
        L_0x0351:
            if (r6 < 0) goto L_0x0390
            r10 = 3
            if (r6 <= r10) goto L_0x0390
            byte r10 = r7[r6]     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            if (r10 != r9) goto L_0x038a
            int r9 = r6 + -1
            byte r9 = r7[r9]     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            if (r9 != 0) goto L_0x038a
            int r9 = r6 + -2
            byte r9 = r7[r9]     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            if (r9 != 0) goto L_0x038a
            int r9 = r6 + -3
            byte r10 = r7[r9]     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            if (r10 != 0) goto L_0x038a
            java.nio.ByteBuffer r6 = java.nio.ByteBuffer.allocate(r9)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            int r10 = r14.size     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            int r10 = r10 - r9
            java.nio.ByteBuffer r10 = java.nio.ByteBuffer.allocate(r10)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            r2 = 0
            java.nio.ByteBuffer r3 = r6.put(r7, r2, r9)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            r3.position(r2)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            int r3 = r14.size     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            int r3 = r3 - r9
            java.nio.ByteBuffer r3 = r10.put(r7, r9, r3)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            r3.position(r2)     // Catch:{ Exception -> 0x0283, all -> 0x0499 }
            goto L_0x0392
        L_0x038a:
            int r6 = r6 + -1
            r2 = 0
            r9 = 1
            goto L_0x0351
        L_0x0390:
            r6 = 0
            r10 = 0
        L_0x0392:
            r7 = r47
            r2 = r49
            android.media.MediaFormat r3 = android.media.MediaFormat.createVideoFormat(r7, r12, r2)     // Catch:{ Exception -> 0x03b0, all -> 0x03ae }
            if (r6 == 0) goto L_0x03a6
            if (r10 == 0) goto L_0x03a6
            r3.setByteBuffer(r13, r6)     // Catch:{ Exception -> 0x03b0, all -> 0x03ae }
            java.lang.String r6 = "csd-1"
            r3.setByteBuffer(r6, r10)     // Catch:{ Exception -> 0x03b0, all -> 0x03ae }
        L_0x03a6:
            org.telegram.messenger.video.MP4Builder r6 = r15.mediaMuxer     // Catch:{ Exception -> 0x03b0, all -> 0x03ae }
            r9 = 0
            int r1 = r6.addTrack(r3, r9)     // Catch:{ Exception -> 0x03b0, all -> 0x03ae }
            goto L_0x03d7
        L_0x03ae:
            r0 = move-exception
            goto L_0x03b5
        L_0x03b0:
            r0 = move-exception
            goto L_0x03c7
        L_0x03b2:
            r0 = move-exception
            r2 = r49
        L_0x03b5:
            r9 = r78
            r31 = r85
            r7 = r2
            r11 = r15
            r4 = r48
            r46 = 0
            r47 = r83
            r2 = r0
            goto L_0x1384
        L_0x03c4:
            r0 = move-exception
            r2 = r49
        L_0x03c7:
            r6 = r11
            r11 = r2
            r2 = r0
            goto L_0x0526
        L_0x03cc:
            r16 = r3
            r18 = r4
            r38 = r10
            r7 = r47
            r4 = r2
            goto L_0x032d
        L_0x03d7:
            int r3 = r14.flags     // Catch:{ Exception -> 0x0459, all -> 0x0457 }
            r3 = r3 & 4
            if (r3 == 0) goto L_0x03e0
            r3 = 0
            r6 = 1
            goto L_0x03e2
        L_0x03e0:
            r3 = 0
            r6 = 0
        L_0x03e2:
            r11.releaseOutputBuffer(r8, r3)     // Catch:{ Exception -> 0x0459, all -> 0x0457 }
            r3 = r79
            r9 = r6
            r10 = -1
            r6 = r76
        L_0x03eb:
            if (r8 == r10) goto L_0x0403
            r8 = r77
            r49 = r2
            r2 = r4
            r47 = r7
        L_0x03f4:
            r76 = r11
            r36 = r13
            r4 = r18
            r10 = r38
            r13 = 21
            r7 = r3
            r3 = r16
            goto L_0x01ca
        L_0x0403:
            if (r16 != 0) goto L_0x0447
            r17.drawImage()     // Catch:{ Exception -> 0x0440, all -> 0x043a }
            float r8 = (float) r5
            r10 = 1106247680(0x41var_, float:30.0)
            float r8 = r8 / r10
            float r8 = r8 * r22
            float r8 = r8 * r22
            float r8 = r8 * r22
            r76 = r1
            r49 = r2
            long r1 = (long) r8
            r10 = r37
            r10.setPresentationTime(r1)     // Catch:{ Exception -> 0x0436, all -> 0x0431 }
            r10.swapBuffers()     // Catch:{ Exception -> 0x0436, all -> 0x0431 }
            int r5 = r5 + 1
            float r1 = (float) r5     // Catch:{ Exception -> 0x0436, all -> 0x0431 }
            r2 = 1106247680(0x41var_, float:30.0)
            float r2 = r2 * r23
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 < 0) goto L_0x044d
            r11.signalEndOfInputStream()     // Catch:{ Exception -> 0x0436, all -> 0x0431 }
            r8 = 0
            r16 = 1
            goto L_0x044f
        L_0x0431:
            r0 = move-exception
            r1 = r76
            goto L_0x049a
        L_0x0436:
            r0 = move-exception
            r1 = r76
            goto L_0x0493
        L_0x043a:
            r0 = move-exception
            r76 = r1
        L_0x043d:
            r49 = r2
            goto L_0x049a
        L_0x0440:
            r0 = move-exception
            r76 = r1
        L_0x0443:
            r49 = r2
            goto L_0x04a9
        L_0x0447:
            r76 = r1
            r49 = r2
            r10 = r37
        L_0x044d:
            r8 = r77
        L_0x044f:
            r1 = r76
            r2 = r4
            r47 = r7
            r37 = r10
            goto L_0x03f4
        L_0x0457:
            r0 = move-exception
            goto L_0x043d
        L_0x0459:
            r0 = move-exception
            goto L_0x0443
        L_0x045b:
            r10 = r37
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0492, all -> 0x0499 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0492, all -> 0x0499 }
            r3.<init>()     // Catch:{ Exception -> 0x0492, all -> 0x0499 }
            java.lang.String r4 = "encoderOutputBuffer "
            r3.append(r4)     // Catch:{ Exception -> 0x0492, all -> 0x0499 }
            r3.append(r8)     // Catch:{ Exception -> 0x0492, all -> 0x0499 }
            java.lang.String r4 = " was null"
            r3.append(r4)     // Catch:{ Exception -> 0x0492, all -> 0x0499 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0492, all -> 0x0499 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0492, all -> 0x0499 }
            throw r2     // Catch:{ Exception -> 0x0492, all -> 0x0499 }
        L_0x0479:
            r10 = r37
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0492, all -> 0x0499 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0492, all -> 0x0499 }
            r3.<init>()     // Catch:{ Exception -> 0x0492, all -> 0x0499 }
            java.lang.String r4 = "unexpected result from encoder.dequeueOutputBuffer: "
            r3.append(r4)     // Catch:{ Exception -> 0x0492, all -> 0x0499 }
            r3.append(r8)     // Catch:{ Exception -> 0x0492, all -> 0x0499 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0492, all -> 0x0499 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0492, all -> 0x0499 }
            throw r2     // Catch:{ Exception -> 0x0492, all -> 0x0499 }
        L_0x0492:
            r0 = move-exception
        L_0x0493:
            r2 = r0
            r37 = r10
            goto L_0x04ac
        L_0x0497:
            r0 = move-exception
            goto L_0x04a9
        L_0x0499:
            r0 = move-exception
        L_0x049a:
            r9 = r78
            r31 = r85
            r2 = r0
            r11 = r15
            r4 = r48
            r7 = r49
            goto L_0x0516
        L_0x04a6:
            r0 = move-exception
            r11 = r76
        L_0x04a9:
            r10 = r37
        L_0x04ab:
            r2 = r0
        L_0x04ac:
            r6 = r11
            r11 = r49
            goto L_0x0526
        L_0x04b1:
            r11 = r76
            r10 = r37
            r2 = r10
            r6 = r11
            r9 = r48
            r11 = r49
            r7 = 0
            r46 = 0
            r10 = r78
            goto L_0x056b
        L_0x04c2:
            r0 = move-exception
            r11 = r76
            r10 = r37
            r2 = r0
            r6 = r11
        L_0x04c9:
            r11 = r49
            r1 = -5
            goto L_0x0526
        L_0x04ce:
            r0 = move-exception
            goto L_0x04f8
        L_0x04d0:
            r0 = move-exception
            r11 = r76
            r10 = r37
            r2 = r0
            r6 = r11
            goto L_0x04e2
        L_0x04d8:
            r0 = move-exception
            r10 = r2
            r48 = r9
            r49 = r11
            r11 = r6
            r2 = r0
            r37 = r10
        L_0x04e2:
            r11 = r49
            r1 = -5
            r17 = 0
            goto L_0x0526
        L_0x04e8:
            r0 = move-exception
            r48 = r9
            r49 = r11
            r11 = r6
            r2 = r0
            r11 = r49
            r1 = -5
            goto L_0x0522
        L_0x04f3:
            r0 = move-exception
            r48 = r9
            r49 = r11
        L_0x04f8:
            r9 = r78
            r31 = r85
            r2 = r0
            r11 = r15
            r4 = r48
            r7 = r49
            goto L_0x0515
        L_0x0503:
            r0 = move-exception
            r48 = r9
            r49 = r11
            goto L_0x051f
        L_0x0509:
            r0 = move-exception
            r48 = r9
            r9 = r78
            r31 = r85
            r2 = r0
            r7 = r11
            r11 = r15
            r4 = r48
        L_0x0515:
            r1 = -5
        L_0x0516:
            r46 = 0
            r47 = r83
            goto L_0x1384
        L_0x051c:
            r0 = move-exception
            r48 = r9
        L_0x051f:
            r2 = r0
        L_0x0520:
            r1 = -5
            r6 = 0
        L_0x0522:
            r17 = 0
            r37 = 0
        L_0x0526:
            boolean r3 = r2 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x05b3 }
            if (r3 == 0) goto L_0x052f
            if (r90 != 0) goto L_0x052f
            r46 = 1
            goto L_0x0531
        L_0x052f:
            r46 = 0
        L_0x0531:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x05a7 }
            r3.<init>()     // Catch:{ all -> 0x05a7 }
            java.lang.String r4 = "bitrate: "
            r3.append(r4)     // Catch:{ all -> 0x05a7 }
            r9 = r48
            r3.append(r9)     // Catch:{ all -> 0x05a5 }
            java.lang.String r4 = " framerate: "
            r3.append(r4)     // Catch:{ all -> 0x05a5 }
            r10 = r78
            r3.append(r10)     // Catch:{ all -> 0x0599 }
            java.lang.String r4 = " size: "
            r3.append(r4)     // Catch:{ all -> 0x0599 }
            r3.append(r11)     // Catch:{ all -> 0x0599 }
            java.lang.String r4 = "x"
            r3.append(r4)     // Catch:{ all -> 0x0599 }
            r3.append(r12)     // Catch:{ all -> 0x0599 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0599 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x0599 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x0599 }
            r2 = r37
            r7 = r46
            r46 = 1
        L_0x056b:
            if (r17 == 0) goto L_0x057a
            r17.release()     // Catch:{ all -> 0x0571 }
            goto L_0x057a
        L_0x0571:
            r0 = move-exception
            r47 = r83
            r31 = r85
            r2 = r0
            r46 = r7
            goto L_0x059f
        L_0x057a:
            if (r2 == 0) goto L_0x057f
            r2.release()     // Catch:{ all -> 0x0571 }
        L_0x057f:
            if (r6 == 0) goto L_0x0587
            r6.stop()     // Catch:{ all -> 0x0571 }
            r6.release()     // Catch:{ all -> 0x0571 }
        L_0x0587:
            r71.checkConversionCanceled()     // Catch:{ all -> 0x0571 }
            r47 = r83
            r31 = r85
            r4 = r9
            r9 = r10
            r8 = r12
            r6 = r46
            r46 = r7
            r7 = r11
            r11 = r15
            goto L_0x1351
        L_0x0599:
            r0 = move-exception
            r47 = r83
            r31 = r85
            r2 = r0
        L_0x059f:
            r4 = r9
            r9 = r10
            r7 = r11
            r11 = r15
            goto L_0x1384
        L_0x05a5:
            r0 = move-exception
            goto L_0x05aa
        L_0x05a7:
            r0 = move-exception
            r9 = r48
        L_0x05aa:
            r47 = r83
            r31 = r85
            r2 = r0
            r4 = r9
            r7 = r11
            r11 = r15
            goto L_0x05c0
        L_0x05b3:
            r0 = move-exception
            r9 = r48
            r47 = r83
            r31 = r85
            r2 = r0
            r4 = r9
            r7 = r11
            r11 = r15
        L_0x05be:
            r46 = 0
        L_0x05c0:
            r9 = r78
            goto L_0x1384
        L_0x05c4:
            r13 = r1
            r14 = r20
            r38 = r25
            r7 = r31
            r1 = 921600(0xe1000, float:1.291437E-39)
            android.media.MediaExtractor r2 = new android.media.MediaExtractor     // Catch:{ all -> 0x1373 }
            r2.<init>()     // Catch:{ all -> 0x1373 }
            r15.extractor = r2     // Catch:{ all -> 0x1373 }
            r8 = r72
            r6 = r26
            r2.setDataSource(r8)     // Catch:{ all -> 0x1373 }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x1373 }
            r3 = 0
            int r5 = org.telegram.messenger.MediaController.findTrack(r2, r3)     // Catch:{ all -> 0x1373 }
            r2 = -1
            if (r9 == r2) goto L_0x05ef
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x00c7 }
            r4 = 1
            int r2 = org.telegram.messenger.MediaController.findTrack(r2, r4)     // Catch:{ all -> 0x00c7 }
            r3 = r2
            goto L_0x05f1
        L_0x05ef:
            r4 = 1
            r3 = -1
        L_0x05f1:
            java.lang.String r2 = "mime"
            if (r5 < 0) goto L_0x0607
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ all -> 0x00c7 }
            android.media.MediaFormat r4 = r4.getTrackFormat(r5)     // Catch:{ all -> 0x00c7 }
            java.lang.String r4 = r4.getString(r2)     // Catch:{ all -> 0x00c7 }
            boolean r4 = r4.equals(r7)     // Catch:{ all -> 0x00c7 }
            if (r4 != 0) goto L_0x0607
            r4 = 1
            goto L_0x0608
        L_0x0607:
            r4 = 0
        L_0x0608:
            if (r89 != 0) goto L_0x065a
            if (r4 == 0) goto L_0x060d
            goto L_0x065a
        L_0x060d:
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ all -> 0x064b }
            org.telegram.messenger.video.MP4Builder r3 = r15.mediaMuxer     // Catch:{ all -> 0x064b }
            r4 = -1
            if (r9 == r4) goto L_0x0616
            r13 = 1
            goto L_0x0617
        L_0x0616:
            r13 = 0
        L_0x0617:
            r1 = r71
            r5 = 1
            r4 = r14
            r14 = 1
            r5 = r81
            r14 = r8
            r7 = r83
            r14 = r10
            r9 = r87
            r11 = r73
            r12 = r13
            r1.readAndWriteTracks(r2, r3, r4, r5, r7, r9, r11, r12)     // Catch:{ all -> 0x063c }
            r8 = r76
            r7 = r77
            r4 = r79
            r47 = r83
            r31 = r85
            r9 = r14
            r11 = r15
            r1 = -5
            r6 = 0
            r46 = 0
            goto L_0x1351
        L_0x063c:
            r0 = move-exception
            r12 = r76
            r7 = r77
            r4 = r79
        L_0x0643:
            r47 = r83
            r31 = r85
            r2 = r0
        L_0x0648:
            r9 = r14
            goto L_0x00d0
        L_0x064b:
            r0 = move-exception
            r12 = r76
            r7 = r77
            r4 = r79
            r47 = r83
            r31 = r85
            r2 = r0
            r9 = r10
            goto L_0x00d0
        L_0x065a:
            r12 = r14
            r4 = -1
            r11 = 1
            r14 = r10
            if (r5 < 0) goto L_0x1317
            r18 = -2147483648(0xfffffffvar_, double:NaN)
            r8 = 1000(0x3e8, float:1.401E-42)
            int r8 = r8 / r14
            int r8 = r8 * 1000
            long r9 = (long) r8     // Catch:{ Exception -> 0x1280, all -> 0x1274 }
            android.media.MediaExtractor r8 = r15.extractor     // Catch:{ Exception -> 0x1280, all -> 0x1274 }
            r8.selectTrack(r5)     // Catch:{ Exception -> 0x1280, all -> 0x1274 }
            android.media.MediaExtractor r8 = r15.extractor     // Catch:{ Exception -> 0x1280, all -> 0x1274 }
            android.media.MediaFormat r8 = r8.getTrackFormat(r5)     // Catch:{ Exception -> 0x1280, all -> 0x1274 }
            r20 = 0
            int r24 = (r85 > r20 ? 1 : (r85 == r20 ? 0 : -1))
            if (r24 < 0) goto L_0x0699
            r20 = 1157234688(0x44fa0000, float:2000.0)
            int r20 = (r23 > r20 ? 1 : (r23 == r20 ? 0 : -1))
            if (r20 > 0) goto L_0x0684
            r20 = 2600000(0x27aCLASSNAME, float:3.643376E-39)
            goto L_0x0692
        L_0x0684:
            r20 = 1167867904(0x459CLASSNAME, float:5000.0)
            int r20 = (r23 > r20 ? 1 : (r23 == r20 ? 0 : -1))
            if (r20 > 0) goto L_0x068f
            r20 = 2200000(0x2191c0, float:3.082857E-39)
            goto L_0x0692
        L_0x068f:
            r20 = 1560000(0x17cdc0, float:2.186026E-39)
        L_0x0692:
            r24 = r3
            r4 = r20
            r20 = 0
            goto L_0x06a9
        L_0x0699:
            if (r79 > 0) goto L_0x06a3
            r20 = r85
            r24 = r3
            r4 = 921600(0xe1000, float:1.291437E-39)
            goto L_0x06a9
        L_0x06a3:
            r4 = r79
            r20 = r85
            r24 = r3
        L_0x06a9:
            r3 = r80
            if (r3 <= 0) goto L_0x06cb
            int r4 = java.lang.Math.min(r3, r4)     // Catch:{ Exception -> 0x06c0, all -> 0x06b2 }
            goto L_0x06cb
        L_0x06b2:
            r0 = move-exception
            r12 = r76
            r7 = r77
            r47 = r83
            r2 = r0
            r9 = r14
            r11 = r15
            r31 = r20
            goto L_0x1381
        L_0x06c0:
            r0 = move-exception
            r47 = r83
            r2 = r0
            r61 = r5
            r9 = r14
            r31 = r20
            goto L_0x128d
        L_0x06cb:
            r25 = 0
            int r27 = (r20 > r25 ? 1 : (r20 == r25 ? 0 : -1))
            if (r27 < 0) goto L_0x06d6
            r27 = r12
            r11 = r16
            goto L_0x06da
        L_0x06d6:
            r27 = r12
            r11 = r20
        L_0x06da:
            int r20 = (r11 > r25 ? 1 : (r11 == r25 ? 0 : -1))
            if (r20 < 0) goto L_0x070d
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0702, all -> 0x06f1 }
            r3 = 0
            r1.seekTo(r11, r3)     // Catch:{ Exception -> 0x0702, all -> 0x06f1 }
            r3 = r95
            r21 = r5
            r26 = r6
            r85 = r11
            r5 = 0
            r11 = r81
            goto L_0x0744
        L_0x06f1:
            r0 = move-exception
            r7 = r77
            r47 = r83
            r2 = r0
            r31 = r11
            r9 = r14
            r11 = r15
            r1 = -5
            r46 = 0
            r12 = r76
            goto L_0x1384
        L_0x0702:
            r0 = move-exception
            r47 = r83
            r2 = r0
            r61 = r5
            r31 = r11
        L_0x070a:
            r9 = r14
            goto L_0x128d
        L_0x070d:
            r85 = r11
            r25 = 0
            r11 = r81
            int r1 = (r11 > r25 ? 1 : (r11 == r25 ? 0 : -1))
            if (r1 <= 0) goto L_0x0736
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x072d, all -> 0x0726 }
            r3 = 0
            r1.seekTo(r11, r3)     // Catch:{ Exception -> 0x072d, all -> 0x0726 }
            r3 = r95
            r21 = r5
            r26 = r6
            r5 = 0
            goto L_0x0744
        L_0x0726:
            r0 = move-exception
            r12 = r76
            r7 = r77
            goto L_0x0643
        L_0x072d:
            r0 = move-exception
            r47 = r83
            r31 = r85
            r2 = r0
            r61 = r5
            goto L_0x070a
        L_0x0736:
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x1269, all -> 0x1259 }
            r21 = r5
            r26 = r6
            r3 = 0
            r5 = 0
            r1.seekTo(r5, r3)     // Catch:{ Exception -> 0x1252, all -> 0x1259 }
            r3 = r95
        L_0x0744:
            if (r3 == 0) goto L_0x0769
            r1 = 90
            r11 = r74
            r12 = -1
            if (r11 == r1) goto L_0x0757
            r1 = 270(0x10e, float:3.78E-43)
            if (r11 != r1) goto L_0x0752
            goto L_0x0757
        L_0x0752:
            int r1 = r3.transformWidth     // Catch:{ Exception -> 0x075e, all -> 0x0726 }
            int r5 = r3.transformHeight     // Catch:{ Exception -> 0x075e, all -> 0x0726 }
            goto L_0x075b
        L_0x0757:
            int r1 = r3.transformHeight     // Catch:{ Exception -> 0x075e, all -> 0x0726 }
            int r5 = r3.transformWidth     // Catch:{ Exception -> 0x075e, all -> 0x0726 }
        L_0x075b:
            r6 = r5
            r5 = r1
            goto L_0x0770
        L_0x075e:
            r0 = move-exception
            r47 = r83
            r31 = r85
            r2 = r0
        L_0x0764:
            r9 = r14
            r61 = r21
            goto L_0x128d
        L_0x0769:
            r11 = r74
            r12 = -1
            r5 = r76
            r6 = r77
        L_0x0770:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1252, all -> 0x1259 }
            if (r1 == 0) goto L_0x0790
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x075e, all -> 0x0726 }
            r1.<init>()     // Catch:{ Exception -> 0x075e, all -> 0x0726 }
            java.lang.String r12 = "create encoder with w = "
            r1.append(r12)     // Catch:{ Exception -> 0x075e, all -> 0x0726 }
            r1.append(r5)     // Catch:{ Exception -> 0x075e, all -> 0x0726 }
            java.lang.String r12 = " h = "
            r1.append(r12)     // Catch:{ Exception -> 0x075e, all -> 0x0726 }
            r1.append(r6)     // Catch:{ Exception -> 0x075e, all -> 0x0726 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x075e, all -> 0x0726 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x075e, all -> 0x0726 }
        L_0x0790:
            android.media.MediaFormat r1 = android.media.MediaFormat.createVideoFormat(r7, r5, r6)     // Catch:{ Exception -> 0x1252, all -> 0x1259 }
            java.lang.String r12 = "color-format"
            r3 = 2130708361(0x7var_, float:1.701803E38)
            r1.setInteger(r12, r3)     // Catch:{ Exception -> 0x1252, all -> 0x1259 }
            java.lang.String r3 = "bitrate"
            r1.setInteger(r3, r4)     // Catch:{ Exception -> 0x1252, all -> 0x1259 }
            java.lang.String r3 = "frame-rate"
            r1.setInteger(r3, r14)     // Catch:{ Exception -> 0x1252, all -> 0x1259 }
            java.lang.String r3 = "i-frame-interval"
            r12 = 2
            r1.setInteger(r3, r12)     // Catch:{ Exception -> 0x1252, all -> 0x1259 }
            r3 = 23
            r12 = r38
            if (r12 >= r3) goto L_0x07e0
            int r3 = java.lang.Math.min(r6, r5)     // Catch:{ Exception -> 0x075e, all -> 0x0726 }
            r25 = r5
            r5 = 480(0x1e0, float:6.73E-43)
            if (r3 > r5) goto L_0x07e2
            r3 = 921600(0xe1000, float:1.291437E-39)
            if (r4 <= r3) goto L_0x07c2
            goto L_0x07c3
        L_0x07c2:
            r3 = r4
        L_0x07c3:
            java.lang.String r4 = "bitrate"
            r1.setInteger(r4, r3)     // Catch:{ Exception -> 0x07d8, all -> 0x07cb }
            r20 = r3
            goto L_0x07e4
        L_0x07cb:
            r0 = move-exception
            r12 = r76
            r7 = r77
            r47 = r83
            r31 = r85
            r2 = r0
            r4 = r3
            goto L_0x0648
        L_0x07d8:
            r0 = move-exception
            r47 = r83
            r31 = r85
            r2 = r0
            r4 = r3
            goto L_0x0764
        L_0x07e0:
            r25 = r5
        L_0x07e2:
            r20 = r4
        L_0x07e4:
            android.media.MediaCodec r5 = android.media.MediaCodec.createEncoderByType(r7)     // Catch:{ Exception -> 0x1244, all -> 0x1231 }
            r3 = 1
            r4 = 0
            r5.configure(r1, r4, r4, r3)     // Catch:{ Exception -> 0x121e, all -> 0x1231 }
            org.telegram.messenger.video.InputSurface r1 = new org.telegram.messenger.video.InputSurface     // Catch:{ Exception -> 0x121e, all -> 0x1231 }
            android.view.Surface r3 = r5.createInputSurface()     // Catch:{ Exception -> 0x121e, all -> 0x1231 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x121e, all -> 0x1231 }
            r1.makeCurrent()     // Catch:{ Exception -> 0x1204, all -> 0x1231 }
            r5.start()     // Catch:{ Exception -> 0x1204, all -> 0x1231 }
            java.lang.String r3 = r8.getString(r2)     // Catch:{ Exception -> 0x1204, all -> 0x1231 }
            android.media.MediaCodec r3 = android.media.MediaCodec.createDecoderByType(r3)     // Catch:{ Exception -> 0x1204, all -> 0x1231 }
            org.telegram.messenger.video.OutputSurface r28 = new org.telegram.messenger.video.OutputSurface     // Catch:{ Exception -> 0x11f0, all -> 0x1231 }
            r29 = 0
            r79 = r3
            float r3 = (float) r14
            r30 = 0
            r54 = r1
            r1 = r28
            r55 = r2
            r2 = r91
            r59 = r79
            r58 = r24
            r31 = 1
            r24 = r3
            r3 = r29
            r4 = r92
            r79 = r5
            r61 = r21
            r62 = r25
            r5 = r93
            r63 = r6
            r64 = r26
            r6 = r95
            r65 = r7
            r7 = r76
            r66 = r8
            r8 = r77
            r25 = r9
            r9 = r74
            r10 = r24
            r31 = r85
            r36 = r13
            r13 = 1
            r11 = r30
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x11e0, all -> 0x11db }
            android.view.Surface r1 = r28.getSurface()     // Catch:{ Exception -> 0x11c8, all -> 0x11db }
            r3 = r59
            r2 = r66
            r4 = 0
            r5 = 0
            r3.configure(r2, r1, r5, r4)     // Catch:{ Exception -> 0x11c0, all -> 0x11db }
            r3.start()     // Catch:{ Exception -> 0x11c0, all -> 0x11db }
            r1 = 21
            if (r12 >= r1) goto L_0x0887
            java.nio.ByteBuffer[] r6 = r3.getInputBuffers()     // Catch:{ Exception -> 0x0878, all -> 0x086b }
            java.nio.ByteBuffer[] r1 = r79.getOutputBuffers()     // Catch:{ Exception -> 0x0878, all -> 0x086b }
            r2 = r58
            r69 = r6
            r6 = r1
            r1 = r69
            goto L_0x088b
        L_0x086b:
            r0 = move-exception
        L_0x086c:
            r12 = r76
            r7 = r77
            r9 = r78
            r47 = r83
            r2 = r0
        L_0x0875:
            r11 = r15
            goto L_0x1240
        L_0x0878:
            r0 = move-exception
            r9 = r78
            r6 = r79
            r47 = r83
            r2 = r0
            r42 = r5
        L_0x0882:
            r4 = r20
            r1 = -5
            goto L_0x1296
        L_0x0887:
            r1 = r5
            r6 = r1
            r2 = r58
        L_0x088b:
            if (r2 < 0) goto L_0x09b5
            android.media.MediaExtractor r4 = r15.extractor     // Catch:{ Exception -> 0x09a2, all -> 0x0990 }
            android.media.MediaFormat r4 = r4.getTrackFormat(r2)     // Catch:{ Exception -> 0x09a2, all -> 0x0990 }
            r7 = r55
            java.lang.String r8 = r4.getString(r7)     // Catch:{ Exception -> 0x09a2, all -> 0x0990 }
            java.lang.String r9 = "audio/mp4a-latm"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x09a2, all -> 0x0990 }
            if (r8 != 0) goto L_0x08b0
            java.lang.String r8 = r4.getString(r7)     // Catch:{ Exception -> 0x0878, all -> 0x086b }
            java.lang.String r9 = "audio/mpeg"
            boolean r8 = r8.equals(r9)     // Catch:{ Exception -> 0x0878, all -> 0x086b }
            if (r8 == 0) goto L_0x08ae
            goto L_0x08b0
        L_0x08ae:
            r8 = 0
            goto L_0x08b1
        L_0x08b0:
            r8 = 1
        L_0x08b1:
            java.lang.String r7 = r4.getString(r7)     // Catch:{ Exception -> 0x09a2, all -> 0x0990 }
            java.lang.String r9 = "audio/unknown"
            boolean r7 = r7.equals(r9)     // Catch:{ Exception -> 0x09a2, all -> 0x0990 }
            if (r7 == 0) goto L_0x08be
            r2 = -1
        L_0x08be:
            if (r2 < 0) goto L_0x0984
            if (r8 == 0) goto L_0x0909
            org.telegram.messenger.video.MP4Builder r7 = r15.mediaMuxer     // Catch:{ Exception -> 0x08fd, all -> 0x08f8 }
            int r7 = r7.addTrack(r4, r13)     // Catch:{ Exception -> 0x08fd, all -> 0x08f8 }
            android.media.MediaExtractor r9 = r15.extractor     // Catch:{ Exception -> 0x08fd, all -> 0x08f8 }
            r9.selectTrack(r2)     // Catch:{ Exception -> 0x08fd, all -> 0x08f8 }
            java.lang.String r9 = "max-input-size"
            int r4 = r4.getInteger(r9)     // Catch:{ Exception -> 0x08fd, all -> 0x08f8 }
            java.nio.ByteBuffer r4 = java.nio.ByteBuffer.allocateDirect(r4)     // Catch:{ Exception -> 0x08fd, all -> 0x08f8 }
            r13 = r81
            r9 = 0
            r11 = 1
            int r21 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1))
            if (r21 <= 0) goto L_0x08e7
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x08f6, all -> 0x086b }
            r11 = 0
            r5.seekTo(r13, r11)     // Catch:{ Exception -> 0x08f6, all -> 0x086b }
            goto L_0x08ed
        L_0x08e7:
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x08f6, all -> 0x086b }
            r11 = 0
            r5.seekTo(r9, r11)     // Catch:{ Exception -> 0x08f6, all -> 0x086b }
        L_0x08ed:
            r11 = r78
            r9 = r83
            r85 = r6
            r6 = 0
            goto L_0x09c1
        L_0x08f6:
            r0 = move-exception
            goto L_0x0900
        L_0x08f8:
            r0 = move-exception
            r13 = r81
            goto L_0x086c
        L_0x08fd:
            r0 = move-exception
            r13 = r81
        L_0x0900:
            r9 = r78
            r6 = r79
            r47 = r83
            r2 = r0
            goto L_0x11d7
        L_0x0909:
            r13 = r81
            r9 = 0
            android.media.MediaExtractor r5 = new android.media.MediaExtractor     // Catch:{ Exception -> 0x0982, all -> 0x0980 }
            r5.<init>()     // Catch:{ Exception -> 0x0982, all -> 0x0980 }
            r7 = r72
            r11 = r78
            r5.setDataSource(r7)     // Catch:{ Exception -> 0x0975, all -> 0x0968 }
            r5.selectTrack(r2)     // Catch:{ Exception -> 0x0975, all -> 0x0968 }
            int r21 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1))
            if (r21 <= 0) goto L_0x0937
            r9 = 0
            r5.seekTo(r13, r9)     // Catch:{ Exception -> 0x0930, all -> 0x0927 }
            r85 = r6
            goto L_0x093e
        L_0x0927:
            r0 = move-exception
            r12 = r76
            r7 = r77
            r47 = r83
            r2 = r0
            goto L_0x0972
        L_0x0930:
            r0 = move-exception
            r6 = r79
            r47 = r83
            r2 = r0
            goto L_0x097d
        L_0x0937:
            r85 = r6
            r6 = r9
            r9 = 0
            r5.seekTo(r6, r9)     // Catch:{ Exception -> 0x0975, all -> 0x0968 }
        L_0x093e:
            org.telegram.messenger.video.AudioRecoder r6 = new org.telegram.messenger.video.AudioRecoder     // Catch:{ Exception -> 0x0975, all -> 0x0968 }
            r6.<init>(r4, r5, r2)     // Catch:{ Exception -> 0x0975, all -> 0x0968 }
            r6.startTime = r13     // Catch:{ Exception -> 0x095a, all -> 0x0968 }
            r9 = r83
            r6.endTime = r9     // Catch:{ Exception -> 0x0958, all -> 0x0956 }
            org.telegram.messenger.video.MP4Builder r4 = r15.mediaMuxer     // Catch:{ Exception -> 0x0958, all -> 0x0956 }
            android.media.MediaFormat r5 = r6.format     // Catch:{ Exception -> 0x0958, all -> 0x0956 }
            r7 = 1
            int r4 = r4.addTrack(r5, r7)     // Catch:{ Exception -> 0x0958, all -> 0x0956 }
            r7 = r4
            r4 = 0
            goto L_0x09c1
        L_0x0956:
            r0 = move-exception
            goto L_0x096b
        L_0x0958:
            r0 = move-exception
            goto L_0x095d
        L_0x095a:
            r0 = move-exception
            r9 = r83
        L_0x095d:
            r2 = r0
            r42 = r6
            r47 = r9
            r9 = r11
            r4 = r20
            r1 = -5
            goto L_0x0a28
        L_0x0968:
            r0 = move-exception
            r9 = r83
        L_0x096b:
            r12 = r76
            r7 = r77
            r2 = r0
            r47 = r9
        L_0x0972:
            r9 = r11
            goto L_0x0875
        L_0x0975:
            r0 = move-exception
            r9 = r83
            r6 = r79
            r2 = r0
            r47 = r9
        L_0x097d:
            r9 = r11
            goto L_0x11d7
        L_0x0980:
            r0 = move-exception
            goto L_0x0993
        L_0x0982:
            r0 = move-exception
            goto L_0x09a5
        L_0x0984:
            r11 = r78
            r13 = r81
            r9 = r83
            r85 = r6
            r4 = 0
            r6 = 0
            r7 = -5
            goto L_0x09c1
        L_0x0990:
            r0 = move-exception
            r13 = r81
        L_0x0993:
            r9 = r83
            r12 = r76
            r7 = r77
            r2 = r0
            r47 = r9
            r11 = r15
            r4 = r20
            r1 = -5
            goto L_0x05be
        L_0x09a2:
            r0 = move-exception
            r13 = r81
        L_0x09a5:
            r9 = r83
            r6 = r79
            r2 = r0
            r47 = r9
            r4 = r20
            r1 = -5
            r42 = 0
        L_0x09b1:
            r9 = r78
            goto L_0x1296
        L_0x09b5:
            r11 = r78
            r13 = r81
            r9 = r83
            r85 = r6
            r4 = 0
            r6 = 0
            r7 = -5
            r8 = 1
        L_0x09c1:
            if (r2 >= 0) goto L_0x09c5
            r5 = 1
            goto L_0x09c6
        L_0x09c5:
            r5 = 0
        L_0x09c6:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x11ae, all -> 0x11aa }
            r21 = r85
            r57 = r16
            r55 = r18
            r19 = 0
            r24 = 1
            r29 = 0
            r30 = 0
            r33 = 0
            r37 = 0
            r39 = 0
            r18 = r5
            r5 = -5
        L_0x09e0:
            if (r29 == 0) goto L_0x09fb
            if (r8 != 0) goto L_0x09e7
            if (r18 != 0) goto L_0x09e7
            goto L_0x09fb
        L_0x09e7:
            r8 = r76
            r7 = r77
            r2 = r79
            r1 = r5
            r42 = r6
            r47 = r9
            r9 = r11
            r11 = r15
            r4 = r20
            r6 = 0
            r46 = 0
            goto L_0x12db
        L_0x09fb:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x1192, all -> 0x117d }
            if (r8 != 0) goto L_0x0a2c
            if (r6 == 0) goto L_0x0a2c
            r83 = r5
            org.telegram.messenger.video.MP4Builder r5 = r15.mediaMuxer     // Catch:{ Exception -> 0x0a1d, all -> 0x0a0d }
            boolean r5 = r6.step(r5, r7)     // Catch:{ Exception -> 0x0a1d, all -> 0x0a0d }
            r18 = r5
            goto L_0x0a2e
        L_0x0a0d:
            r0 = move-exception
            r12 = r76
            r7 = r77
            r1 = r83
            r2 = r0
            r47 = r9
            r9 = r11
        L_0x0a18:
            r11 = r15
        L_0x0a19:
            r4 = r20
            goto L_0x1382
        L_0x0a1d:
            r0 = move-exception
            r1 = r83
            r2 = r0
            r42 = r6
        L_0x0a23:
            r47 = r9
            r9 = r11
            r4 = r20
        L_0x0a28:
            r6 = r79
            goto L_0x1296
        L_0x0a2c:
            r83 = r5
        L_0x0a2e:
            if (r19 != 0) goto L_0x0bb4
            android.media.MediaExtractor r5 = r15.extractor     // Catch:{ Exception -> 0x0ba3, all -> 0x0b93 }
            int r5 = r5.getSampleTrackIndex()     // Catch:{ Exception -> 0x0ba3, all -> 0x0b93 }
            r85 = r6
            r6 = r61
            if (r5 != r6) goto L_0x0a9f
            r13 = 2500(0x9c4, double:1.235E-320)
            int r5 = r3.dequeueInputBuffer(r13)     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            if (r5 < 0) goto L_0x0a83
            r13 = 21
            if (r12 >= r13) goto L_0x0a4b
            r13 = r1[r5]     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            goto L_0x0a4f
        L_0x0a4b:
            java.nio.ByteBuffer r13 = r3.getInputBuffer(r5)     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
        L_0x0a4f:
            android.media.MediaExtractor r14 = r15.extractor     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            r86 = r1
            r1 = 0
            int r50 = r14.readSampleData(r13, r1)     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            if (r50 >= 0) goto L_0x0a6c
            r49 = 0
            r50 = 0
            r51 = 0
            r53 = 4
            r47 = r3
            r48 = r5
            r47.queueInputBuffer(r48, r49, r50, r51, r53)     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            r19 = 1
            goto L_0x0a85
        L_0x0a6c:
            r49 = 0
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            long r51 = r1.getSampleTime()     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            r53 = 0
            r47 = r3
            r48 = r5
            r47.queueInputBuffer(r48, r49, r50, r51, r53)     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            r1.advance()     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            goto L_0x0a85
        L_0x0a83:
            r86 = r1
        L_0x0a85:
            r14 = r2
            r2 = r4
            r61 = r6
            r45 = r7
            r59 = r9
            r13 = r27
            r1 = 0
            r6 = r81
            r27 = r8
            goto L_0x0b75
        L_0x0a96:
            r0 = move-exception
            r1 = r83
            r42 = r85
            r2 = r0
            r61 = r6
            goto L_0x0a23
        L_0x0a9f:
            r86 = r1
            if (r8 == 0) goto L_0x0b61
            r1 = -1
            if (r2 == r1) goto L_0x0b52
            if (r5 != r2) goto L_0x0b61
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0b49, all -> 0x0b43 }
            r5 = 0
            int r1 = r1.readSampleData(r4, r5)     // Catch:{ Exception -> 0x0b49, all -> 0x0b43 }
            r13 = r27
            r13.size = r1     // Catch:{ Exception -> 0x0b49, all -> 0x0b43 }
            r1 = 21
            if (r12 >= r1) goto L_0x0abf
            r4.position(r5)     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            int r1 = r13.size     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            r4.limit(r1)     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
        L_0x0abf:
            int r1 = r13.size     // Catch:{ Exception -> 0x0b49, all -> 0x0b43 }
            if (r1 < 0) goto L_0x0ad2
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            r14 = r2
            long r1 = r1.getSampleTime()     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            r13.presentationTimeUs = r1     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            android.media.MediaExtractor r1 = r15.extractor     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            r1.advance()     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            goto L_0x0ad8
        L_0x0ad2:
            r14 = r2
            r1 = 0
            r13.size = r1     // Catch:{ Exception -> 0x0b49, all -> 0x0b43 }
            r19 = 1
        L_0x0ad8:
            int r1 = r13.size     // Catch:{ Exception -> 0x0b49, all -> 0x0b43 }
            if (r1 <= 0) goto L_0x0b39
            r1 = 0
            int r5 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r5 < 0) goto L_0x0ae8
            long r1 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0a96, all -> 0x0a0d }
            int r5 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r5 >= 0) goto L_0x0b39
        L_0x0ae8:
            r1 = 0
            r13.offset = r1     // Catch:{ Exception -> 0x0b49, all -> 0x0b43 }
            android.media.MediaExtractor r2 = r15.extractor     // Catch:{ Exception -> 0x0b49, all -> 0x0b43 }
            int r2 = r2.getSampleFlags()     // Catch:{ Exception -> 0x0b49, all -> 0x0b43 }
            r13.flags = r2     // Catch:{ Exception -> 0x0b49, all -> 0x0b43 }
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0b49, all -> 0x0b43 }
            r27 = r8
            r59 = r9
            long r8 = r2.writeSampleData(r7, r4, r13, r1)     // Catch:{ Exception -> 0x0b35, all -> 0x0b30 }
            r1 = 0
            int r5 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r5 == 0) goto L_0x0b28
            org.telegram.messenger.MediaController$VideoConvertorListener r1 = r15.callback     // Catch:{ Exception -> 0x0b35, all -> 0x0b30 }
            if (r1 == 0) goto L_0x0b28
            r2 = r4
            long r4 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0b35, all -> 0x0b30 }
            r61 = r6
            r45 = r7
            r10 = 2500(0x9c4, double:1.235E-320)
            r6 = r81
            long r40 = r4 - r6
            int r47 = (r40 > r33 ? 1 : (r40 == r33 ? 0 : -1))
            if (r47 <= 0) goto L_0x0b1a
            long r33 = r4 - r6
        L_0x0b1a:
            r4 = r33
            float r10 = (float) r4
            float r10 = r10 / r22
            float r10 = r10 / r23
            r1.didWriteData(r8, r10)     // Catch:{ Exception -> 0x0b91, all -> 0x0b8f }
            r33 = r4
            goto L_0x0b74
        L_0x0b28:
            r2 = r4
            r61 = r6
            r45 = r7
        L_0x0b2d:
            r6 = r81
            goto L_0x0b74
        L_0x0b30:
            r0 = move-exception
            r6 = r81
            goto L_0x0b97
        L_0x0b35:
            r0 = move-exception
            r61 = r6
            goto L_0x0b4e
        L_0x0b39:
            r2 = r4
            r61 = r6
            r45 = r7
            r27 = r8
            r59 = r9
            goto L_0x0b2d
        L_0x0b43:
            r0 = move-exception
            r6 = r81
            r59 = r9
            goto L_0x0b97
        L_0x0b49:
            r0 = move-exception
            r61 = r6
            r59 = r9
        L_0x0b4e:
            r6 = r81
            goto L_0x0ba9
        L_0x0b52:
            r14 = r2
            r2 = r4
            r61 = r6
            r45 = r7
            r59 = r9
            r13 = r27
            r6 = r81
            r27 = r8
            goto L_0x0b70
        L_0x0b61:
            r14 = r2
            r2 = r4
            r61 = r6
            r45 = r7
            r59 = r9
            r13 = r27
            r6 = r81
            r27 = r8
            r1 = -1
        L_0x0b70:
            if (r5 != r1) goto L_0x0b74
            r1 = 1
            goto L_0x0b75
        L_0x0b74:
            r1 = 0
        L_0x0b75:
            if (r1 == 0) goto L_0x0bc3
            r4 = 2500(0x9c4, double:1.235E-320)
            int r48 = r3.dequeueInputBuffer(r4)     // Catch:{ Exception -> 0x0b91, all -> 0x0b8f }
            if (r48 < 0) goto L_0x0bc3
            r49 = 0
            r50 = 0
            r51 = 0
            r53 = 4
            r47 = r3
            r47.queueInputBuffer(r48, r49, r50, r51, r53)     // Catch:{ Exception -> 0x0b91, all -> 0x0b8f }
            r19 = 1
            goto L_0x0bc3
        L_0x0b8f:
            r0 = move-exception
            goto L_0x0b97
        L_0x0b91:
            r0 = move-exception
            goto L_0x0ba9
        L_0x0b93:
            r0 = move-exception
            r59 = r9
            r6 = r13
        L_0x0b97:
            r12 = r76
            r7 = r77
            r9 = r78
            r1 = r83
            r2 = r0
            r11 = r15
            goto L_0x118c
        L_0x0ba3:
            r0 = move-exception
            r85 = r6
            r59 = r9
            r6 = r13
        L_0x0ba9:
            r9 = r78
            r6 = r79
            r1 = r83
            r42 = r85
            r2 = r0
            goto L_0x11a4
        L_0x0bb4:
            r86 = r1
            r85 = r6
            r45 = r7
            r59 = r9
            r6 = r13
            r13 = r27
            r14 = r2
            r2 = r4
            r27 = r8
        L_0x0bc3:
            r1 = r30 ^ 1
            r5 = r83
            r4 = r1
            r67 = r55
            r9 = r59
            r1 = 1
        L_0x0bcd:
            if (r4 != 0) goto L_0x0be5
            if (r1 == 0) goto L_0x0bd2
            goto L_0x0be5
        L_0x0bd2:
            r11 = r78
            r1 = r86
            r4 = r2
            r2 = r14
            r8 = r27
            r55 = r67
            r27 = r13
            r13 = r6
            r7 = r45
            r6 = r85
            goto L_0x09e0
        L_0x0be5:
            r71.checkConversionCanceled()     // Catch:{ Exception -> 0x116b, all -> 0x115c }
            if (r90 == 0) goto L_0x0bf5
            r47 = 22000(0x55f0, double:1.08694E-319)
            r8 = r79
            r83 = r1
            r84 = r2
            r1 = r47
            goto L_0x0bfd
        L_0x0bf5:
            r8 = r79
            r83 = r1
            r84 = r2
            r1 = 2500(0x9c4, double:1.235E-320)
        L_0x0bfd:
            int r1 = r8.dequeueOutputBuffer(r13, r1)     // Catch:{ Exception -> 0x115a, all -> 0x115c }
            r2 = -1
            if (r1 != r2) goto L_0x0c1c
            r79 = r4
            r47 = r9
            r4 = r36
            r9 = r62
            r10 = r63
            r11 = r65
            r2 = 0
        L_0x0CLASSNAME:
            r36 = r14
        L_0x0CLASSNAME:
            r14 = -1
            r69 = r29
            r29 = r12
            r12 = r69
            goto L_0x0e2c
        L_0x0c1c:
            r2 = -3
            if (r1 != r2) goto L_0x0CLASSNAME
            r2 = 21
            if (r12 >= r2) goto L_0x0CLASSNAME
            java.nio.ByteBuffer[] r21 = r8.getOutputBuffers()     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
        L_0x0CLASSNAME:
            r2 = r83
            r79 = r4
            r47 = r9
            r4 = r36
            r9 = r62
            r10 = r63
            r11 = r65
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r2 = -2
            if (r1 != r2) goto L_0x0ccf
            android.media.MediaFormat r2 = r8.getOutputFormat()     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            r11 = -5
            if (r5 != r11) goto L_0x0ca3
            if (r2 == 0) goto L_0x0ca3
            org.telegram.messenger.video.MP4Builder r11 = r15.mediaMuxer     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            r79 = r4
            r4 = 0
            int r5 = r11.addTrack(r2, r4)     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            r4 = r64
            boolean r11 = r2.containsKey(r4)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
            if (r11 == 0) goto L_0x0c7a
            int r11 = r2.getInteger(r4)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
            r64 = r4
            r4 = 1
            if (r11 != r4) goto L_0x0c7c
            r4 = r36
            java.nio.ByteBuffer r11 = r2.getByteBuffer(r4)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
            r36 = r5
            java.lang.String r5 = "csd-1"
            java.nio.ByteBuffer r2 = r2.getByteBuffer(r5)     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
            int r5 = r11.limit()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
            int r2 = r2.limit()     // Catch:{ Exception -> 0x0CLASSNAME, all -> 0x0CLASSNAME }
            int r5 = r5 + r2
            r39 = r5
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = move-exception
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = move-exception
            goto L_0x0CLASSNAME
        L_0x0c7a:
            r64 = r4
        L_0x0c7c:
            r4 = r36
            r36 = r5
        L_0x0CLASSNAME:
            r5 = r36
            goto L_0x0ca7
        L_0x0CLASSNAME:
            r0 = move-exception
            r36 = r5
        L_0x0CLASSNAME:
            r12 = r76
            r7 = r77
            r2 = r0
            r47 = r9
            r11 = r15
            r4 = r20
            r1 = r36
            goto L_0x05be
        L_0x0CLASSNAME:
            r0 = move-exception
            r36 = r5
        L_0x0CLASSNAME:
            r42 = r85
            r2 = r0
            r6 = r8
            r47 = r9
            r4 = r20
            r1 = r36
            goto L_0x09b1
        L_0x0ca3:
            r79 = r4
            r4 = r36
        L_0x0ca7:
            r2 = r83
            r47 = r9
            r36 = r14
            r9 = r62
            r10 = r63
            r11 = r65
            goto L_0x0CLASSNAME
        L_0x0cb5:
            r0 = move-exception
            r12 = r76
            r7 = r77
            r2 = r0
            r1 = r5
            r47 = r9
            r11 = r15
            r4 = r20
            goto L_0x05be
        L_0x0cc3:
            r0 = move-exception
            r42 = r85
            r2 = r0
            r1 = r5
            r6 = r8
            r47 = r9
            r4 = r20
            goto L_0x09b1
        L_0x0ccf:
            r79 = r4
            r4 = r36
            if (r1 < 0) goto L_0x1132
            r2 = 21
            if (r12 >= r2) goto L_0x0cdc
            r11 = r21[r1]     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            goto L_0x0ce0
        L_0x0cdc:
            java.nio.ByteBuffer r11 = r8.getOutputBuffer(r1)     // Catch:{ Exception -> 0x115a, all -> 0x115c }
        L_0x0ce0:
            if (r11 == 0) goto L_0x1110
            int r2 = r13.size     // Catch:{ Exception -> 0x115a, all -> 0x115c }
            r29 = r12
            r12 = 1
            if (r2 <= r12) goto L_0x0e15
            int r12 = r13.flags     // Catch:{ Exception -> 0x0e10, all -> 0x0e03 }
            r36 = r12 & 2
            if (r36 != 0) goto L_0x0d88
            if (r39 == 0) goto L_0x0d02
            r36 = r12 & 1
            if (r36 == 0) goto L_0x0d02
            r36 = r14
            int r14 = r13.offset     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            int r14 = r14 + r39
            r13.offset = r14     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            int r2 = r2 - r39
            r13.size = r2     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            goto L_0x0d04
        L_0x0d02:
            r36 = r14
        L_0x0d04:
            if (r24 == 0) goto L_0x0d57
            r2 = r12 & 1
            if (r2 == 0) goto L_0x0d57
            int r2 = r13.size     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            r12 = 100
            if (r2 <= r12) goto L_0x0d55
            int r2 = r13.offset     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            r11.position(r2)     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            byte[] r2 = new byte[r12]     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            r11.get(r2)     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            r14 = 0
            r24 = 0
        L_0x0d1d:
            r12 = 96
            if (r14 >= r12) goto L_0x0d55
            byte r12 = r2[r14]     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            if (r12 != 0) goto L_0x0d4c
            int r12 = r14 + 1
            byte r12 = r2[r12]     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            if (r12 != 0) goto L_0x0d4c
            int r12 = r14 + 2
            byte r12 = r2[r12]     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            if (r12 != 0) goto L_0x0d4c
            int r12 = r14 + 3
            byte r12 = r2[r12]     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            r47 = r2
            r2 = 1
            if (r12 != r2) goto L_0x0d4e
            int r12 = r24 + 1
            if (r12 <= r2) goto L_0x0d49
            int r2 = r13.offset     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            int r2 = r2 + r14
            r13.offset = r2     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            int r2 = r13.size     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            int r2 = r2 - r14
            r13.size = r2     // Catch:{ Exception -> 0x0cc3, all -> 0x0cb5 }
            goto L_0x0d55
        L_0x0d49:
            r24 = r12
            goto L_0x0d4e
        L_0x0d4c:
            r47 = r2
        L_0x0d4e:
            int r14 = r14 + 1
            r2 = r47
            r12 = 100
            goto L_0x0d1d
        L_0x0d55:
            r24 = 0
        L_0x0d57:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0e10, all -> 0x0e03 }
            r47 = r9
            r12 = 1
            long r9 = r2.writeSampleData(r5, r11, r13, r12)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            r11 = 0
            int r2 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r2 == 0) goto L_0x0d80
            org.telegram.messenger.MediaController$VideoConvertorListener r2 = r15.callback     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            if (r2 == 0) goto L_0x0d80
            long r11 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            long r49 = r11 - r6
            int r14 = (r49 > r33 ? 1 : (r49 == r33 ? 0 : -1))
            if (r14 <= 0) goto L_0x0d74
            long r33 = r11 - r6
        L_0x0d74:
            r11 = r33
            float r14 = (float) r11     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            float r14 = r14 / r22
            float r14 = r14 / r23
            r2.didWriteData(r9, r14)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            r33 = r11
        L_0x0d80:
            r9 = r62
            r10 = r63
            r11 = r65
            goto L_0x0e1b
        L_0x0d88:
            r47 = r9
            r36 = r14
            r9 = -5
            if (r5 != r9) goto L_0x0d80
            byte[] r9 = new byte[r2]     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            int r10 = r13.offset     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            int r10 = r10 + r2
            r11.limit(r10)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            int r2 = r13.offset     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            r11.position(r2)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            r11.get(r9)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            int r2 = r13.size     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            r11 = 1
            int r2 = r2 - r11
        L_0x0da3:
            if (r2 < 0) goto L_0x0de0
            r10 = 3
            if (r2 <= r10) goto L_0x0de0
            byte r12 = r9[r2]     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            if (r12 != r11) goto L_0x0ddc
            int r12 = r2 + -1
            byte r12 = r9[r12]     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            if (r12 != 0) goto L_0x0ddc
            int r12 = r2 + -2
            byte r12 = r9[r12]     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            if (r12 != 0) goto L_0x0ddc
            int r12 = r2 + -3
            byte r14 = r9[r12]     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            if (r14 != 0) goto L_0x0ddc
            java.nio.ByteBuffer r2 = java.nio.ByteBuffer.allocate(r12)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            int r14 = r13.size     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            int r14 = r14 - r12
            java.nio.ByteBuffer r14 = java.nio.ByteBuffer.allocate(r14)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            r10 = 0
            java.nio.ByteBuffer r11 = r2.put(r9, r10, r12)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            r11.position(r10)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            int r11 = r13.size     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            int r11 = r11 - r12
            java.nio.ByteBuffer r9 = r14.put(r9, r12, r11)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            r9.position(r10)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            goto L_0x0de2
        L_0x0ddc:
            int r2 = r2 + -1
            r11 = 1
            goto L_0x0da3
        L_0x0de0:
            r2 = 0
            r14 = 0
        L_0x0de2:
            r9 = r62
            r10 = r63
            r11 = r65
            android.media.MediaFormat r12 = android.media.MediaFormat.createVideoFormat(r11, r9, r10)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            if (r2 == 0) goto L_0x0df8
            if (r14 == 0) goto L_0x0df8
            r12.setByteBuffer(r4, r2)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            java.lang.String r2 = "csd-1"
            r12.setByteBuffer(r2, r14)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
        L_0x0df8:
            org.telegram.messenger.video.MP4Builder r2 = r15.mediaMuxer     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            r14 = 0
            int r2 = r2.addTrack(r12, r14)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e01 }
            r5 = r2
            goto L_0x0e1b
        L_0x0e01:
            r0 = move-exception
            goto L_0x0e06
        L_0x0e03:
            r0 = move-exception
            r47 = r9
        L_0x0e06:
            r12 = r76
            r7 = r77
            r9 = r78
            r2 = r0
            r1 = r5
            goto L_0x0a18
        L_0x0e10:
            r0 = move-exception
            r47 = r9
            goto L_0x1172
        L_0x0e15:
            r47 = r9
            r36 = r14
            goto L_0x0d80
        L_0x0e1b:
            int r2 = r13.flags     // Catch:{ Exception -> 0x1109, all -> 0x1107 }
            r2 = r2 & 4
            if (r2 == 0) goto L_0x0e23
            r2 = 1
            goto L_0x0e24
        L_0x0e23:
            r2 = 0
        L_0x0e24:
            r12 = 0
            r8.releaseOutputBuffer(r1, r12)     // Catch:{ Exception -> 0x1109, all -> 0x1107 }
            r12 = r2
            r14 = -1
            r2 = r83
        L_0x0e2c:
            if (r1 == r14) goto L_0x0e49
            r1 = r2
            r62 = r9
            r63 = r10
            r65 = r11
            r14 = r36
            r9 = r47
            r2 = r84
            r36 = r4
            r4 = r79
            r79 = r8
        L_0x0e41:
            r69 = r29
            r29 = r12
            r12 = r69
            goto L_0x0bcd
        L_0x0e49:
            if (r30 != 0) goto L_0x10ca
            r14 = 2500(0x9c4, double:1.235E-320)
            int r1 = r3.dequeueOutputBuffer(r13, r14)     // Catch:{ Exception -> 0x10bc, all -> 0x10ac }
            r14 = -1
            if (r1 != r14) goto L_0x0e6c
            r49 = r2
            r15 = r4
            r83 = r5
            r62 = r9
            r63 = r10
            r65 = r11
            r10 = r54
            r1 = 0
            r4 = 0
            r35 = -5
            r40 = 2500(0x9c4, double:1.235E-320)
            r9 = r78
            goto L_0x10e5
        L_0x0e6c:
            r15 = -3
            if (r1 != r15) goto L_0x0e71
            goto L_0x10ca
        L_0x0e71:
            r15 = -2
            if (r1 != r15) goto L_0x0ea0
            android.media.MediaFormat r1 = r3.getOutputFormat()     // Catch:{ Exception -> 0x0e9d, all -> 0x0e92 }
            boolean r15 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0e9d, all -> 0x0e92 }
            if (r15 == 0) goto L_0x10ca
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0e9d, all -> 0x0e92 }
            r15.<init>()     // Catch:{ Exception -> 0x0e9d, all -> 0x0e92 }
            java.lang.String r14 = "newFormat = "
            r15.append(r14)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e92 }
            r15.append(r1)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e92 }
            java.lang.String r1 = r15.toString()     // Catch:{ Exception -> 0x0e9d, all -> 0x0e92 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0e9d, all -> 0x0e92 }
            goto L_0x10ca
        L_0x0e92:
            r0 = move-exception
            r11 = r71
            r12 = r76
            r7 = r77
            r9 = r78
            goto L_0x1167
        L_0x0e9d:
            r0 = move-exception
            goto L_0x1172
        L_0x0ea0:
            if (r1 < 0) goto L_0x1085
            int r14 = r13.size     // Catch:{ Exception -> 0x10bc, all -> 0x10ac }
            r15 = r4
            r83 = r5
            if (r14 == 0) goto L_0x0eab
            r14 = 1
            goto L_0x0eac
        L_0x0eab:
            r14 = 0
        L_0x0eac:
            long r4 = r13.presentationTimeUs     // Catch:{ Exception -> 0x1081, all -> 0x107d }
            r43 = 0
            int r49 = (r47 > r43 ? 1 : (r47 == r43 ? 0 : -1))
            if (r49 <= 0) goto L_0x0ed2
            int r49 = (r4 > r47 ? 1 : (r4 == r47 ? 0 : -1))
            if (r49 < 0) goto L_0x0ed2
            int r14 = r13.flags     // Catch:{ Exception -> 0x0ecf, all -> 0x0ec4 }
            r14 = r14 | 4
            r13.flags = r14     // Catch:{ Exception -> 0x0ecf, all -> 0x0ec4 }
            r14 = 0
            r19 = 1
            r30 = 1
            goto L_0x0ed2
        L_0x0ec4:
            r0 = move-exception
            r11 = r71
            r12 = r76
            r7 = r77
            r9 = r78
            goto L_0x10b7
        L_0x0ecf:
            r0 = move-exception
            goto L_0x0f7a
        L_0x0ed2:
            r43 = 0
            int r49 = (r31 > r43 ? 1 : (r31 == r43 ? 0 : -1))
            if (r49 < 0) goto L_0x0f7e
            r49 = r2
            int r2 = r13.flags     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r2 = r2 & 4
            if (r2 == 0) goto L_0x0f6c
            long r50 = r31 - r6
            long r50 = java.lang.Math.abs(r50)     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r2 = 1000000(0xvar_, float:1.401298E-39)
            r62 = r9
            r40 = 2500(0x9c4, double:1.235E-320)
            r9 = r78
            int r2 = r2 / r9
            r63 = r10
            r65 = r11
            long r10 = (long) r2
            int r2 = (r50 > r10 ? 1 : (r50 == r10 ? 0 : -1))
            if (r2 <= 0) goto L_0x0f5d
            r10 = 0
            int r2 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r2 <= 0) goto L_0x0f0e
            r11 = r71
            android.media.MediaExtractor r2 = r11.extractor     // Catch:{ Exception -> 0x0f5a, all -> 0x0f0b }
            r10 = 0
            r2.seekTo(r6, r10)     // Catch:{ Exception -> 0x0f5a, all -> 0x0f0b }
            r6 = r67
            r10 = 0
            goto L_0x0f1a
        L_0x0f0b:
            r0 = move-exception
            goto L_0x10b3
        L_0x0f0e:
            r11 = r71
            android.media.MediaExtractor r2 = r11.extractor     // Catch:{ Exception -> 0x0f5a, all -> 0x0var_ }
            r6 = 0
            r10 = 0
            r2.seekTo(r6, r10)     // Catch:{ Exception -> 0x0f5a, all -> 0x0f0b }
            r6 = r67
        L_0x0f1a:
            long r37 = r6 + r25
            int r2 = r13.flags     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r35 = -5
            r2 = r2 & -5
            r13.flags = r2     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r3.flush()     // Catch:{ Exception -> 0x0var_, all -> 0x0var_ }
            r46 = r37
            r2 = 1
            r14 = 0
            r19 = 0
            r30 = 0
            r43 = 0
            r37 = r16
            goto L_0x0f9c
        L_0x0var_:
            r0 = move-exception
            r12 = r76
            r7 = r77
            r1 = r83
            r2 = r0
            r4 = r20
            r47 = r31
            r46 = 0
            r31 = r16
            goto L_0x1384
        L_0x0var_:
            r0 = move-exception
            r1 = r83
            r42 = r85
            r2 = r0
            r6 = r8
            r4 = r20
            r47 = r31
            r31 = r16
            goto L_0x1296
        L_0x0var_:
            r0 = move-exception
            r10 = 0
            goto L_0x10b3
        L_0x0f5a:
            r0 = move-exception
            goto L_0x10c3
        L_0x0f5d:
            r6 = r67
            r10 = 0
            r35 = -5
            r11 = r71
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            r11 = r71
            goto L_0x10c3
        L_0x0f6c:
            r35 = -5
            r40 = 2500(0x9c4, double:1.235E-320)
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            r9 = r78
        L_0x0var_:
            r10 = 0
            goto L_0x10b1
        L_0x0var_:
            r0 = move-exception
            r11 = r71
        L_0x0f7a:
            r9 = r78
            goto L_0x10c3
        L_0x0f7e:
            r35 = -5
            r40 = 2500(0x9c4, double:1.235E-320)
            r49 = r2
        L_0x0var_:
            r62 = r9
            r63 = r10
            r65 = r11
            r6 = r67
            r10 = 0
            r11 = r71
            r9 = r78
        L_0x0var_:
            r2 = 0
            r43 = 0
            r69 = r31
            r31 = r47
            r46 = r37
            r37 = r69
        L_0x0f9c:
            int r48 = (r37 > r43 ? 1 : (r37 == r43 ? 0 : -1))
            if (r48 < 0) goto L_0x0fa3
            r10 = r37
            goto L_0x0fa5
        L_0x0fa3:
            r10 = r81
        L_0x0fa5:
            int r48 = (r10 > r43 ? 1 : (r10 == r43 ? 0 : -1))
            if (r48 <= 0) goto L_0x0fe6
            int r48 = (r57 > r16 ? 1 : (r57 == r16 ? 0 : -1))
            if (r48 != 0) goto L_0x0fe6
            int r48 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r48 >= 0) goto L_0x0fd5
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0fe3, all -> 0x105a }
            if (r4 == 0) goto L_0x0fd3
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0fe3, all -> 0x105a }
            r4.<init>()     // Catch:{ Exception -> 0x0fe3, all -> 0x105a }
            java.lang.String r5 = "drop frame startTime = "
            r4.append(r5)     // Catch:{ Exception -> 0x0fe3, all -> 0x105a }
            r4.append(r10)     // Catch:{ Exception -> 0x0fe3, all -> 0x105a }
            java.lang.String r5 = " present time = "
            r4.append(r5)     // Catch:{ Exception -> 0x0fe3, all -> 0x105a }
            long r10 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0fe3, all -> 0x105a }
            r4.append(r10)     // Catch:{ Exception -> 0x0fe3, all -> 0x105a }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0fe3, all -> 0x105a }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x0fe3, all -> 0x105a }
        L_0x0fd3:
            r14 = 0
            goto L_0x0fe6
        L_0x0fd5:
            long r4 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0fe3, all -> 0x105a }
            r10 = -2147483648(0xfffffffvar_, double:NaN)
            int r48 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r48 == 0) goto L_0x0fe0
            long r46 = r46 - r4
        L_0x0fe0:
            r57 = r4
            goto L_0x0fe6
        L_0x0fe3:
            r0 = move-exception
            goto L_0x106f
        L_0x0fe6:
            if (r2 == 0) goto L_0x0feb
            r57 = r16
            goto L_0x0ffe
        L_0x0feb:
            int r2 = (r37 > r16 ? 1 : (r37 == r16 ? 0 : -1))
            if (r2 != 0) goto L_0x0ffb
            r4 = 0
            int r2 = (r46 > r4 ? 1 : (r46 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x0ffb
            long r4 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0fe3, all -> 0x105a }
            long r4 = r4 + r46
            r13.presentationTimeUs = r4     // Catch:{ Exception -> 0x0fe3, all -> 0x105a }
        L_0x0ffb:
            r3.releaseOutputBuffer(r1, r14)     // Catch:{ Exception -> 0x106c, all -> 0x105a }
        L_0x0ffe:
            if (r14 == 0) goto L_0x1031
            r1 = 0
            int r4 = (r37 > r1 ? 1 : (r37 == r1 ? 0 : -1))
            if (r4 < 0) goto L_0x100d
            long r4 = r13.presentationTimeUs     // Catch:{ Exception -> 0x0fe3, all -> 0x105a }
            long r67 = java.lang.Math.max(r6, r4)     // Catch:{ Exception -> 0x0fe3, all -> 0x105a }
            goto L_0x100f
        L_0x100d:
            r67 = r6
        L_0x100f:
            r28.awaitNewImage()     // Catch:{ Exception -> 0x1014, all -> 0x105a }
            r6 = 0
            goto L_0x101a
        L_0x1014:
            r0 = move-exception
            r4 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ Exception -> 0x106c, all -> 0x105a }
            r6 = 1
        L_0x101a:
            if (r6 != 0) goto L_0x102e
            r28.drawImage()     // Catch:{ Exception -> 0x106c, all -> 0x105a }
            long r4 = r13.presentationTimeUs     // Catch:{ Exception -> 0x106c, all -> 0x105a }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 * r6
            r10 = r54
            r10.setPresentationTime(r4)     // Catch:{ Exception -> 0x1050, all -> 0x105a }
            r10.swapBuffers()     // Catch:{ Exception -> 0x1050, all -> 0x105a }
            goto L_0x1037
        L_0x102e:
            r10 = r54
            goto L_0x1037
        L_0x1031:
            r10 = r54
            r1 = 0
            r67 = r6
        L_0x1037:
            int r4 = r13.flags     // Catch:{ Exception -> 0x1050, all -> 0x105a }
            r4 = r4 & 4
            if (r4 == 0) goto L_0x104c
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1050, all -> 0x105a }
            if (r4 == 0) goto L_0x1046
            java.lang.String r4 = "decoder stream end"
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x1050, all -> 0x105a }
        L_0x1046:
            r8.signalEndOfInputStream()     // Catch:{ Exception -> 0x1050, all -> 0x105a }
            r4 = 0
            goto L_0x10ed
        L_0x104c:
            r4 = r79
            goto L_0x10ed
        L_0x1050:
            r0 = move-exception
            r1 = r83
            r42 = r85
            r2 = r0
            r6 = r8
            r54 = r10
            goto L_0x1075
        L_0x105a:
            r0 = move-exception
            r11 = r71
            r12 = r76
            r7 = r77
            r1 = r83
            r2 = r0
            r4 = r20
            r47 = r31
            r31 = r37
            goto L_0x1382
        L_0x106c:
            r0 = move-exception
            r10 = r54
        L_0x106f:
            r1 = r83
            r42 = r85
            r2 = r0
            r6 = r8
        L_0x1075:
            r4 = r20
            r47 = r31
            r31 = r37
            goto L_0x1296
        L_0x107d:
            r0 = move-exception
            r9 = r78
            goto L_0x10b1
        L_0x1081:
            r0 = move-exception
            r9 = r78
            goto L_0x10c1
        L_0x1085:
            r9 = r78
            r83 = r5
            r10 = r54
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x10a4, all -> 0x10a2 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x10a4, all -> 0x10a2 }
            r4.<init>()     // Catch:{ Exception -> 0x10a4, all -> 0x10a2 }
            java.lang.String r5 = "unexpected result from decoder.dequeueOutputBuffer: "
            r4.append(r5)     // Catch:{ Exception -> 0x10a4, all -> 0x10a2 }
            r4.append(r1)     // Catch:{ Exception -> 0x10a4, all -> 0x10a2 }
            java.lang.String r1 = r4.toString()     // Catch:{ Exception -> 0x10a4, all -> 0x10a2 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x10a4, all -> 0x10a2 }
            throw r2     // Catch:{ Exception -> 0x10a4, all -> 0x10a2 }
        L_0x10a2:
            r0 = move-exception
            goto L_0x10b1
        L_0x10a4:
            r0 = move-exception
            r1 = r83
            r42 = r85
            r2 = r0
            goto L_0x1156
        L_0x10ac:
            r0 = move-exception
            r9 = r78
            r83 = r5
        L_0x10b1:
            r11 = r71
        L_0x10b3:
            r12 = r76
            r7 = r77
        L_0x10b7:
            r1 = r83
            r2 = r0
            goto L_0x0a19
        L_0x10bc:
            r0 = move-exception
            r9 = r78
            r83 = r5
        L_0x10c1:
            r10 = r54
        L_0x10c3:
            r1 = r83
            r42 = r85
            r2 = r0
            goto L_0x1178
        L_0x10ca:
            r49 = r2
            r15 = r4
            r83 = r5
            r62 = r9
            r63 = r10
            r65 = r11
            r10 = r54
            r6 = r67
            r1 = 0
            r35 = -5
            r40 = 2500(0x9c4, double:1.235E-320)
            r9 = r78
            r4 = r79
            r67 = r6
        L_0x10e5:
            r69 = r31
            r31 = r47
            r46 = r37
            r37 = r69
        L_0x10ed:
            r6 = r81
            r5 = r83
            r2 = r84
            r79 = r8
            r54 = r10
            r9 = r31
            r14 = r36
            r31 = r37
            r37 = r46
            r1 = r49
            r36 = r15
            r15 = r71
            goto L_0x0e41
        L_0x1107:
            r0 = move-exception
            goto L_0x115f
        L_0x1109:
            r0 = move-exception
            r9 = r78
            r10 = r54
            goto L_0x1174
        L_0x1110:
            r47 = r9
            r10 = r54
            r9 = r78
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1151, all -> 0x114f }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1151, all -> 0x114f }
            r4.<init>()     // Catch:{ Exception -> 0x1151, all -> 0x114f }
            java.lang.String r6 = "encoderOutputBuffer "
            r4.append(r6)     // Catch:{ Exception -> 0x1151, all -> 0x114f }
            r4.append(r1)     // Catch:{ Exception -> 0x1151, all -> 0x114f }
            java.lang.String r1 = " was null"
            r4.append(r1)     // Catch:{ Exception -> 0x1151, all -> 0x114f }
            java.lang.String r1 = r4.toString()     // Catch:{ Exception -> 0x1151, all -> 0x114f }
            r2.<init>(r1)     // Catch:{ Exception -> 0x1151, all -> 0x114f }
            throw r2     // Catch:{ Exception -> 0x1151, all -> 0x114f }
        L_0x1132:
            r47 = r9
            r10 = r54
            r9 = r78
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x1151, all -> 0x114f }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1151, all -> 0x114f }
            r4.<init>()     // Catch:{ Exception -> 0x1151, all -> 0x114f }
            java.lang.String r6 = "unexpected result from encoder.dequeueOutputBuffer: "
            r4.append(r6)     // Catch:{ Exception -> 0x1151, all -> 0x114f }
            r4.append(r1)     // Catch:{ Exception -> 0x1151, all -> 0x114f }
            java.lang.String r1 = r4.toString()     // Catch:{ Exception -> 0x1151, all -> 0x114f }
            r2.<init>(r1)     // Catch:{ Exception -> 0x1151, all -> 0x114f }
            throw r2     // Catch:{ Exception -> 0x1151, all -> 0x114f }
        L_0x114f:
            r0 = move-exception
            goto L_0x1161
        L_0x1151:
            r0 = move-exception
            r42 = r85
            r2 = r0
            r1 = r5
        L_0x1156:
            r6 = r8
            r54 = r10
            goto L_0x1179
        L_0x115a:
            r0 = move-exception
            goto L_0x116e
        L_0x115c:
            r0 = move-exception
            r47 = r9
        L_0x115f:
            r9 = r78
        L_0x1161:
            r11 = r71
            r12 = r76
            r7 = r77
        L_0x1167:
            r2 = r0
            r1 = r5
            goto L_0x0a19
        L_0x116b:
            r0 = move-exception
            r8 = r79
        L_0x116e:
            r47 = r9
            r10 = r54
        L_0x1172:
            r9 = r78
        L_0x1174:
            r42 = r85
            r2 = r0
            r1 = r5
        L_0x1178:
            r6 = r8
        L_0x1179:
            r4 = r20
            goto L_0x1296
        L_0x117d:
            r0 = move-exception
            r83 = r5
            r59 = r9
            r9 = r11
            r11 = r71
            r12 = r76
            r7 = r77
            r1 = r83
            r2 = r0
        L_0x118c:
            r4 = r20
            r47 = r59
            goto L_0x1382
        L_0x1192:
            r0 = move-exception
            r8 = r79
            r83 = r5
            r85 = r6
            r59 = r9
            r9 = r11
            r10 = r54
            r1 = r83
            r42 = r85
            r2 = r0
            r6 = r8
        L_0x11a4:
            r4 = r20
            r47 = r59
            goto L_0x1296
        L_0x11aa:
            r0 = move-exception
            r9 = r11
            goto L_0x1235
        L_0x11ae:
            r0 = move-exception
            r8 = r79
            r85 = r6
            r9 = r11
            r10 = r54
            r35 = -5
            r47 = r83
            r42 = r85
            r2 = r0
            r6 = r8
            goto L_0x0882
        L_0x11c0:
            r0 = move-exception
            r9 = r78
            r8 = r79
            r10 = r54
            goto L_0x11d1
        L_0x11c8:
            r0 = move-exception
            r9 = r78
            r8 = r79
            r10 = r54
            r3 = r59
        L_0x11d1:
            r35 = -5
            r47 = r83
            r2 = r0
            r6 = r8
        L_0x11d7:
            r4 = r20
            r1 = -5
            goto L_0x121a
        L_0x11db:
            r0 = move-exception
            r9 = r78
            goto L_0x1235
        L_0x11e0:
            r0 = move-exception
            r9 = r78
            r8 = r79
            r10 = r54
            r3 = r59
            r35 = -5
            r47 = r83
            r2 = r0
            r6 = r8
            goto L_0x1200
        L_0x11f0:
            r0 = move-exception
            r31 = r85
            r10 = r1
            r8 = r5
            r9 = r14
            r61 = r21
            r35 = -5
            r47 = r83
            r2 = r0
            r6 = r8
            r54 = r10
        L_0x1200:
            r4 = r20
            r1 = -5
            goto L_0x1218
        L_0x1204:
            r0 = move-exception
            r31 = r85
            r10 = r1
            r8 = r5
            r9 = r14
            r61 = r21
            r35 = -5
            r47 = r83
            r2 = r0
            r6 = r8
            r54 = r10
            r4 = r20
            r1 = -5
            r3 = 0
        L_0x1218:
            r28 = 0
        L_0x121a:
            r42 = 0
            goto L_0x1296
        L_0x121e:
            r0 = move-exception
            r31 = r85
            r8 = r5
            r9 = r14
            r61 = r21
            r35 = -5
            r47 = r83
            r2 = r0
            r6 = r8
            r4 = r20
            r1 = -5
            r3 = 0
            goto L_0x1290
        L_0x1231:
            r0 = move-exception
            r31 = r85
            r9 = r14
        L_0x1235:
            r35 = -5
            r11 = r71
            r12 = r76
            r7 = r77
            r47 = r83
            r2 = r0
        L_0x1240:
            r4 = r20
            goto L_0x1381
        L_0x1244:
            r0 = move-exception
            r31 = r85
            r9 = r14
            r61 = r21
            r35 = -5
            r47 = r83
            r2 = r0
            r4 = r20
            goto L_0x128d
        L_0x1252:
            r0 = move-exception
            r31 = r85
            r9 = r14
            r61 = r21
            goto L_0x126f
        L_0x1259:
            r0 = move-exception
            r31 = r85
            r9 = r14
            r35 = -5
            r11 = r71
            r12 = r76
            r7 = r77
            r47 = r83
            goto L_0x1380
        L_0x1269:
            r0 = move-exception
            r31 = r85
            r61 = r5
            r9 = r14
        L_0x126f:
            r35 = -5
            r47 = r83
            goto L_0x128c
        L_0x1274:
            r0 = move-exception
            r9 = r14
            r35 = -5
            r11 = r71
            r12 = r76
            r7 = r77
            goto L_0x137a
        L_0x1280:
            r0 = move-exception
            r61 = r5
            r9 = r14
            r35 = -5
            r4 = r79
            r47 = r83
            r31 = r85
        L_0x128c:
            r2 = r0
        L_0x128d:
            r1 = -5
            r3 = 0
            r6 = 0
        L_0x1290:
            r28 = 0
            r42 = 0
            r54 = 0
        L_0x1296:
            boolean r5 = r2 instanceof java.lang.IllegalStateException     // Catch:{ all -> 0x130c }
            if (r5 == 0) goto L_0x129f
            if (r90 != 0) goto L_0x129f
            r50 = 1
            goto L_0x12a1
        L_0x129f:
            r50 = 0
        L_0x12a1:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x12ff }
            r5.<init>()     // Catch:{ all -> 0x12ff }
            java.lang.String r7 = "bitrate: "
            r5.append(r7)     // Catch:{ all -> 0x12ff }
            r5.append(r4)     // Catch:{ all -> 0x12ff }
            java.lang.String r7 = " framerate: "
            r5.append(r7)     // Catch:{ all -> 0x12ff }
            r5.append(r9)     // Catch:{ all -> 0x12ff }
            java.lang.String r7 = " size: "
            r5.append(r7)     // Catch:{ all -> 0x12ff }
            r7 = r77
            r5.append(r7)     // Catch:{ all -> 0x12f9 }
            java.lang.String r8 = "x"
            r5.append(r8)     // Catch:{ all -> 0x12f9 }
            r8 = r76
            r5.append(r8)     // Catch:{ all -> 0x12f5 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x12f5 }
            org.telegram.messenger.FileLog.e((java.lang.String) r5)     // Catch:{ all -> 0x12f5 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x12f5 }
            r11 = r71
            r2 = r6
            r46 = r50
            r6 = 1
        L_0x12db:
            android.media.MediaExtractor r5 = r11.extractor     // Catch:{ all -> 0x1336 }
            r10 = r61
            r5.unselectTrack(r10)     // Catch:{ all -> 0x1336 }
            if (r3 == 0) goto L_0x12ea
            r3.stop()     // Catch:{ all -> 0x1336 }
            r3.release()     // Catch:{ all -> 0x1336 }
        L_0x12ea:
            r50 = r6
            r6 = r28
            r69 = r42
            r42 = r2
            r2 = r69
            goto L_0x1330
        L_0x12f5:
            r0 = move-exception
            r11 = r71
            goto L_0x1306
        L_0x12f9:
            r0 = move-exception
            r11 = r71
            r8 = r76
            goto L_0x1306
        L_0x12ff:
            r0 = move-exception
            r11 = r71
            r8 = r76
            r7 = r77
        L_0x1306:
            r2 = r0
            r12 = r8
            r46 = r50
            goto L_0x1384
        L_0x130c:
            r0 = move-exception
            r11 = r71
            r8 = r76
            r7 = r77
            r2 = r0
            r12 = r8
            goto L_0x1382
        L_0x1317:
            r8 = r76
            r7 = r77
            r9 = r14
            r11 = r15
            r35 = -5
            r4 = r79
            r47 = r83
            r31 = r85
            r1 = -5
            r2 = 0
            r6 = 0
            r42 = 0
            r46 = 0
            r50 = 0
            r54 = 0
        L_0x1330:
            if (r6 == 0) goto L_0x133a
            r6.release()     // Catch:{ all -> 0x1336 }
            goto L_0x133a
        L_0x1336:
            r0 = move-exception
            r2 = r0
            r12 = r8
            goto L_0x1384
        L_0x133a:
            if (r54 == 0) goto L_0x133f
            r54.release()     // Catch:{ all -> 0x1336 }
        L_0x133f:
            if (r42 == 0) goto L_0x1347
            r42.stop()     // Catch:{ all -> 0x1336 }
            r42.release()     // Catch:{ all -> 0x1336 }
        L_0x1347:
            if (r2 == 0) goto L_0x134c
            r2.release()     // Catch:{ all -> 0x1336 }
        L_0x134c:
            r71.checkConversionCanceled()     // Catch:{ all -> 0x1336 }
            r6 = r50
        L_0x1351:
            android.media.MediaExtractor r2 = r11.extractor
            if (r2 == 0) goto L_0x1358
            r2.release()
        L_0x1358:
            org.telegram.messenger.video.MP4Builder r2 = r11.mediaMuxer
            if (r2 == 0) goto L_0x136d
            r2.finishMovie()     // Catch:{ all -> 0x1368 }
            org.telegram.messenger.video.MP4Builder r2 = r11.mediaMuxer     // Catch:{ all -> 0x1368 }
            long r1 = r2.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x1368 }
            r11.endPresentationTime = r1     // Catch:{ all -> 0x1368 }
            goto L_0x136d
        L_0x1368:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x136d:
            r10 = r4
            r15 = r31
            r13 = r47
            goto L_0x13d7
        L_0x1373:
            r0 = move-exception
            r9 = r10
            r7 = r11
            r8 = r12
            r11 = r15
            r35 = -5
        L_0x137a:
            r4 = r79
            r47 = r83
            r31 = r85
        L_0x1380:
            r2 = r0
        L_0x1381:
            r1 = -5
        L_0x1382:
            r46 = 0
        L_0x1384:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x1401 }
            r3.<init>()     // Catch:{ all -> 0x1401 }
            java.lang.String r5 = "bitrate: "
            r3.append(r5)     // Catch:{ all -> 0x1401 }
            r3.append(r4)     // Catch:{ all -> 0x1401 }
            java.lang.String r5 = " framerate: "
            r3.append(r5)     // Catch:{ all -> 0x1401 }
            r3.append(r9)     // Catch:{ all -> 0x1401 }
            java.lang.String r5 = " size: "
            r3.append(r5)     // Catch:{ all -> 0x1401 }
            r3.append(r7)     // Catch:{ all -> 0x1401 }
            java.lang.String r5 = "x"
            r3.append(r5)     // Catch:{ all -> 0x1401 }
            r3.append(r12)     // Catch:{ all -> 0x1401 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x1401 }
            org.telegram.messenger.FileLog.e((java.lang.String) r3)     // Catch:{ all -> 0x1401 }
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x1401 }
            android.media.MediaExtractor r2 = r11.extractor
            if (r2 == 0) goto L_0x13bb
            r2.release()
        L_0x13bb:
            org.telegram.messenger.video.MP4Builder r2 = r11.mediaMuxer
            if (r2 == 0) goto L_0x13d0
            r2.finishMovie()     // Catch:{ all -> 0x13cb }
            org.telegram.messenger.video.MP4Builder r2 = r11.mediaMuxer     // Catch:{ all -> 0x13cb }
            long r1 = r2.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x13cb }
            r11.endPresentationTime = r1     // Catch:{ all -> 0x13cb }
            goto L_0x13d0
        L_0x13cb:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x13d0:
            r10 = r4
            r8 = r12
            r15 = r31
            r13 = r47
            r6 = 1
        L_0x13d7:
            if (r46 == 0) goto L_0x1400
            r20 = 1
            r1 = r71
            r2 = r72
            r3 = r73
            r4 = r74
            r5 = r75
            r6 = r8
            r8 = r78
            r9 = r10
            r10 = r80
            r11 = r81
            r17 = r87
            r19 = r89
            r21 = r91
            r22 = r92
            r23 = r93
            r24 = r94
            r25 = r95
            boolean r1 = r1.convertVideoInternal(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r13, r15, r17, r19, r20, r21, r22, r23, r24, r25)
            return r1
        L_0x1400:
            return r6
        L_0x1401:
            r0 = move-exception
            r2 = r71
            r3 = r0
            android.media.MediaExtractor r4 = r2.extractor
            if (r4 == 0) goto L_0x140c
            r4.release()
        L_0x140c:
            org.telegram.messenger.video.MP4Builder r4 = r2.mediaMuxer
            if (r4 == 0) goto L_0x1421
            r4.finishMovie()     // Catch:{ all -> 0x141c }
            org.telegram.messenger.video.MP4Builder r4 = r2.mediaMuxer     // Catch:{ all -> 0x141c }
            long r4 = r4.getLastFrameTimestamp(r1)     // Catch:{ all -> 0x141c }
            r2.endPresentationTime = r4     // Catch:{ all -> 0x141c }
            goto L_0x1421
        L_0x141c:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1421:
            goto L_0x1423
        L_0x1422:
            throw r3
        L_0x1423:
            goto L_0x1422
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
